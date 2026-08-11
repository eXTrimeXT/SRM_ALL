<template>
  <el-container
    class="flex-container budgetmanagement_list_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        ref="formRef"
        :form-array="filterConfig"
        @getFormData="getQuerydata"
        @synchronous-value="syncFilterParams"
      >
        <!-- 公司 -->
        <template #companyId="{scope}">
          <el-select
            v-model="scope.companyId"
            clearable
            filterable
            @change="companyHandle"
          >
            <el-option
              v-for="item in companyInfo"
              :key="item.params.companyId"
              :value="item.params.companyId"
              :label="item.params.companyName"
            />
          </el-select>
        </template>
        <!-- 预算部门 -->
        <template #deptId="{scope}">
          <el-select
            v-model="scope.deptId"
            clearable
            filterable
          >
            <el-option
              v-for="item in budDepatInfo"
              :key="item.params.deptId"
              :value="item.params.deptId"
              :label="item.params.deptName"
              @change="setBudpat"
            />
          </el-select>
        </template>
        <!-- 年份 -->
        <template #year="{ scope }">
          <el-date-picker
            v-model="scope.year"
            class="year"
            type="year"
            value-format="yyyy"
          />
        </template>
      </FormWrapper>

      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <AuthorityButton
            type="primary"
            @click="addHandle"
          >
            {{ $t('common.add') }}
          </AuthorityButton>
          <MImport
            type="default"
            :title="$t('common.import')"
            up-load-url="/api-sup-ce/budget/budgetManagement/importBudgetManagement"
            :extra-data="extraData"
            @downloadTemplate="downloadTemplate"
            @handleSuccess="handleSuccess"
          />
          <ExportExcel
            type="default"
            page-url="/api-sup-ce/budget/budgetManagement/listPage"
            :filter-params="queryParam"
            :table-header="tableHeader"
            :dict-codes="dictCodes"
            timeout="1000000"
            export-mode="front"
          />
          <el-button
            type="text"
            class="tool-tip"
          >
            {{ $t('purchaseDemand.amountsIncludingTax') }}
          </el-button>
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :checkbox="true"
        :table-header="tableHeader"
        :check-change="handleCurrentChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        url="/api-sup-ce/budget/budgetManagement/listPage"
      >
        <!-- 公司 -->
        <template #companyName="{scope}">
          <el-select
            v-if="scope.row.editable === true && scope.row.status !== 'ADJUSTIVE'"
            v-model="scope.row.companyName"
            clearable
            filterable
            @change="companySelect(scope.row)"
          >
            <el-option
              v-for="item in companyInfo"
              :key="item.params.companyId"
              :value="item.params.companyId"
              :label="item.params.companyName"
            />
          </el-select>
          <span v-else>{{ scope.row.companyName }}</span>
        </template>
        <!-- 预算部门 -->
        <template #deptName="{scope}">
          <el-select
            v-if="scope.row.editable === true && scope.row.status !== 'ADJUSTIVE'"
            v-model="scope.row.deptName"
            clearable
            filterable
            @change="setCompanyDept(scope.row)"
          >
            <el-option
              v-for="item in companyDeptInfo"
              :key="item.params.deptId"
              :value="item.params.deptId"
              :label="item.params.deptName"
            />
          </el-select>
          <span v-else>{{ scope.row.deptName }}</span>
        </template>
        <!-- 年份 -->
        <template #year="{ scope }">
          <el-date-picker
            v-if="scope.row.editable === true && scope.row.status !== 'ADJUSTIVE'"
            v-model="scope.row.year"
            class="year"
            type="year"
            value-format="yyyy"
          />
          <span v-else>{{ scope.row.year }}</span>
        </template>
        <!-- 费用类型 -->
        <template #expenseType="{ scope }">
          <DictSelect
            v-if="scope.row.editable === true && scope.row.status !== 'ADJUSTIVE'"
            v-model="scope.row.expenseType"
            code="EXPENSE_TYPE"
          />
          <span v-else>{{ $getDictLabel('EXPENSE_TYPE', scope.row.expenseType) }}</span>
        </template>
        <!-- 预算科目 -->
        <template #budgetItem="{ scope }">
          <DictSelect
            v-if="scope.row.editable === true && scope.row.status !== 'ADJUSTIVE'"
            v-model="scope.row.budgetItem"
            code="BUDGET_ITEM"
          />
          <span v-else>{{ $getDictLabel('BUDGET_ITEM', scope.row.budgetItem) }}</span>
        </template>
      </TableView>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
// import budgetmanagementEdit from './edit.vue'
import MImport from 'lib@/components/import'
import { downloadFileLink } from 'lib@/utils/file'
import ExportExcel from 'lib@/components/export-excel'
import OrganizationSelector from 'lib@/components/organization-selector'

export default {
  name: 'BudgetmanagementList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    MImport,
    ExportExcel,
    OrganizationSelector
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      budDepatInfo: [],
      name: 'budgetmanagementList',
      tableName: 'budgetmanagementTable',
      pageSize: 15,
      gridId: 'list',
      currentRows: [],
      extraData: {
        fileModular: 'base',
        fileFunction: 'budgetmanagement',
        fileType: 'excel'
      },
      dictCodes: {
        status: 'BUDGET_MANAGEMENT_STATUS',
        budgetItem: 'BUDGET_ITEM',
        expenseType: 'EXPENSE_TYPE'
      },
      filterParams: {},
      companyInfo: [],
      companyDeptInfo: [],
      tableHeader: [
        {
          prop: 'companyName', // 因为导出用到显示字段，注意change事件判断companyName=companyId
          label: this.$t('vendorMod.corporateName'), // 公司
          width: 110,
          showType: 'slot',
          slot: 'companyName',
          addStarToColumn: true
        },
        {
          prop: 'deptName', // 因为导出用到显示字段，注意change事件判断deptName=deptId
          label: this.$t('purchaseDemand.budgetDepartment'), // 预算部门
          width: 130,
          showType: 'slot',
          slot: 'deptName',
          addStarToColumn: true
        },
        {
          prop: 'year',
          label: this.$t('purchaseDemand.budgetYear'), // 预算年份
          width: 110,
          showType: 'slot',
          slot: 'year',
          addStarToColumn: true
        },
        {
          prop: 'budgetManagementNumber',
          label: this.$t('purchaseDemand.budgetNumber'), // 预算编号
          width: 100
        },
        {
          prop: 'status',
          label: this.$t('purchaseDemand.applyStatus'), // 单据状态
          width: 100,
          dataType: 'dict',
          code: 'BUDGET_MANAGEMENT_STATUS'
        },
        {
          prop: 'expenseType',
          label: this.$t('purchaseDemand.typeOfFee'), // 费用类型
          width: 130,
          showType: 'slot',
          slot: 'expenseType',
          addStarToColumn: true
        },
        {
          prop: 'budgetItem',
          label: this.$t('purchaseDemand.budgetAccount'), // 预算科目
          width: 130,
          showType: 'slot',
          slot: 'budgetItem',
          addStarToColumn: true
        },
        {
          prop: 'befAdjustBudgetAmount',
          label: this.$t('purchaseDemand.budgetBeforeAdjustment'), // 调整前预算金额
          width: 150,
          showType: 'input',
          addStarToColumn: true,
          editable: row => row.editable === true && row.status !== 'ADJUSTIVE',
          callback: row => {
            this.checkRowDataAmount(row, 'befAdjustBudgetAmount')
          }
        },
        {
          prop: 'aftAdjustBudgetAmount',
          label: this.$t('purchaseDemand.budgetafterAdjustment'), // 调整后预算金额
          width: 150,
          showType: 'input',
          editable: row => row.editable === true && row.status === 'ADJUSTIVE',
          addStarToColumn: true,
          callback: row => {
            this.checkRowDataAmount(row, 'aftAdjustBudgetAmount')
          }
        },
        {
          prop: 'useableAmount',
          label: this.$t('purSettlementMod.usableAmount'), // 可用金额
          width: 100
        },
        {
          prop: 'freezeAmount',
          label: this.$t('purchaseDemand.frozenAmount'), // 冻结金额
          width: 100
        },
        {
          prop: 'usedAmount',
          label: this.$t('contractMod.usedAmount'), // 已用金额
          width: 100
        },
        {
          prop: 'createdFullName',
          label: this.$t('common.creator'), // 创建人
          width: 100
        },
        {
          prop: 'creationDate',
          label: this.$t('quota.createdDate'), // 创建时间
          width: 100,
          formatter: val => (val ? this.$parseTime(val, '{y}-{m}-{d}') : '')
        },
        {
          prop: 'lastUpdatedFullName',
          label: this.$t('perfMod.lastUpdatedFullName'), // 更新人
          width: 100
        },
        {
          prop: 'lastUpdateDate',
          label: this.$t('perfMod.lastUpdateDate'), // 更新时间
          width: 100,
          formatter: val => (val ? this.$parseTime(val, '{y}-{m}-{d}') : '')
        },
        {
          prop: 'operation',
          label: this.$t('common.operation'),
          showType: 'buttons',
          btnStyle: 'text',
          fixed: 'right',
          width: 160,
          buttons: [
            {
              callback: row => this.editHandle(row),
              show: row => ['DRAFT', 'ADJUSTIVE'].includes(row.status) && !row.editable,
              formattor: () => {
                return this.$t('common.edit') // 编辑
              }
            },
            {
              callback: (row, scope) => this.deleteHandle(row, scope),
              show: row => row.status === 'DRAFT' || (row.editable && row.status !== 'ADJUSTIVE'),
              formattor: () => {
                return this.$t('common.delete') // 删除
              }
            },
            {
              callback: (row, scope) => this.saveHandle(row, scope),
              show: row => row.editable && row.status !== 'ADJUSTIVE',
              disabled: false,
              formattor: () => {
                return this.$t('common.save') // 保存
              }
            },
            {
              callback: row => this.takeEffect(row),
              show: row => ['ADJUSTIVE', 'DRAFT'].includes(row.status),
              formattor: () => {
                return this.$t('common.active') // 生效
              }
            },
            {
              callback: row => this.budgetAdjustment(row),
              show: row => row.status === 'EFFECTIVE',
              formattor: () => {
                return this.$t('common.budgetAdjustment') // 预算调整
              }
            }
          ]
        }
      ],

      filterConfig: [
        {
          prop: 'budgetManagementNumber',
          label: this.$t('purchaseDemand.budgetNumber') // 预算编号
        },
        {
          prop: 'companyId',
          label: this.$t('vendorMod.corporateName'), // 公司
          type: 'slot',
          slot: 'companyId'
        },
        {
          prop: 'deptId',
          label: this.$t('purchaseDemand.budgetDepartment'), // 预算部门
          type: 'slot',
          slot: 'deptId'
        },
        {
          prop: 'status',
          label: this.$t('purchaseDemand.applyStatus'), // 单据状态
          type: 'dict',
          code: 'BUDGET_MANAGEMENT_STATUS'
        },
        // 创建人
        {
          prop: 'createdFullName',
          label: this.$t('common.creator'),
          width: 100
        },
        {
          prop: 'year',
          label: this.$t('dataConfMod.year'), // 年份
          slot: 'year',
          type: 'slot'
        },
        {
          prop: 'budgetItem',
          label: this.$t('purchaseDemand.budgetAccount'), // 预算科目
          type: 'dict',
          code: 'BUDGET_ITEM'
        },
        // 费用类型
        {
          prop: 'expenseType',
          label: this.$t('purchaseDemand.typeOfFee'),
          type: 'dict',
          code: 'EXPENSE_TYPE'
        },
        // 创建时间
        {
          prop: 'dateList',
          width: 180,
          label: this.$t('common.creationTime'),
          type: 'daterange'
        }
      ],
      queryParam: {}
    }
  },
  created () {
    this.$nextTick(() => {
      this.getOrgInfoList()
      this.getQuerydata()
    })
  },
  methods: {
    setBudpat (val) {
      this.queryParam.deptId = val
    },
    // 表单 公司选择
    companyHandle (val) {
      this.queryParam.companyId = val
      this.$http({
        url: '/api-base/base/org_company_dept/listPage',
        method: 'POST',
        data: {
          organizationId: val
        },
        loading: true
      }).then(res => {
        const depts = []
        res.data.list.forEach(v => {
          depts.push({
            value: v.companyDeptId,
            label: v.organizationName,
            params: {
              deptId: v.companyDeptId,
              deptCode: v.deptCode,
              deptName: v.deptName
            }
          })
        })
        this.budDepatInfo = depts
      })
    },
    // 选择预算部门
    setCompanyDept (row) {
      this.companyDeptInfo.forEach(item => {
        if (item.value === row.deptName) {
          this.$set(row, 'deptId', item.params.deptId)
          this.$set(row, 'deptCode', item.params.deptCode)
          this.$set(row, 'deptName', item.params.deptName)
        }
      })
    },
    // 获取预算部门
    getCompanyDept (id) {
      this.$http({
        url: '/api-base/base/org_company_dept/listPage',
        method: 'POST',
        data: {
          organizationId: id
        },
        loading: true
      }).then(res => {
        const depts = []
        res.data.list.forEach(v => {
          depts.push({
            value: v.companyDeptId,
            label: v.deptName,
            params: {
              deptId: v.companyDeptId,
              deptCode: v.deptCode,
              deptName: v.deptName
            }
          })
        })
        this.companyDeptInfo = depts
      })
    },
    // 公司选择
    companySelect (row) {
      row.deptId = ''
      row.deptCode = ''
      row.deptName = ''

      this.companyInfo.forEach(item => {
        if (item.value === row.companyName) {
          row.companyId = item.params.companyId
          row.companyCode = item.params.companyCode
          row.companyName = item.params.companyName
        }
      })
      this.getCompanyDept(row.companyId)
    },
    // 获取公司
    getOrgInfoList () {
      this.$http({
        url: '/api-base/organization/organization/listAllOrganization',
        method: 'POST',
        data: {
          organizationTypeCode: 'COMPANY'
        },
        loading: false
      })
        .then(res => {
          const companys = []
          res.data.list.forEach(v => {
            companys.push({
              value: v.organizationId,
              label: v.organizationName,
              params: {
                companyId: v.organizationId,
                companyCode: v.organizationCode,
                companyName: v.organizationName
              }
            })
          })
          this.companyInfo = companys
        })
        .catch(err => {
          console.log(err)
        })
    },
    // 校验行数据
    checkRowDataAmount (row, key) {
      let n = Number(row[key])
      if (isNaN(n)) {
        // this.$message.warning(this.$t('common.errorNumber'))
        row[key] = ''
      }
      if (n < 0) {
        this.$message.warning(this.$t('purchaseDemand.lessThan0Tips'))
        row[key] = 0
      }
    },
    handleSuccess () {
      this.getQuerydata()
    },
    downloadTemplate () {
      downloadFileLink(
        '/api-sup-ce/budget/budgetManagement/importTemplateDownload',
        '导入模板.xlsx'
      ).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail'))
      })
    },

    syncFilterParams (values) {
      this.filterParams = values
    },
    getQuerydata (obj) {
      const { dateList, ...rest } = obj || this.queryParam
      const params = {}
      if (dateList) {
        params.startCreationDate = dateList[0]
        params.endCreationDate = dateList[1]
      }
      this.queryParam = { ...rest, ...params }
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    // 预算调整
    budgetAdjustment (row) {
      this.$http({
        url: '/api-sup-ce/budget/budgetManagement/adjustBudget',
        method: 'GET',
        laoding: true,
        params: { budgetManagementId: row.budgetManagementId }
      }).then(res => {
        this.$message.success(res.message)
        this.getQuerydata()
      })
    },
    // 生效
    takeEffect (row) {
      const sign = row.aftAdjustBudgetAmount > row.freezeAmount + row.usedAmount
      if (!sign) {
        return this.$message.warning(this.$t('purchaseDemand.adjustedBudget'))
      }
      this.$confirm(this.$t('dataConfMod.enabled'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        this.$http({
          url: '/api-sup-ce/budget/budgetManagement/effect',
          method: 'POST',
          data: row,
          laoding: true
        }).then(res => {
          this.$message.success(res.message)
          this.getQuerydata()
        })
      })
    },
    // 删除
    deleteHandle (row, scope) {
      if (row.status) {
        this.$confirm(this.$t('common.confirmDelete'), {
          confirmButtonText: this.$t('common.confirm'),
          cancelButtonText: this.$t('common.cancel'),
          type: 'warning'
        })
          .then(() => {
            this.$http({
              url: '/api-sup-ce/budget/budgetManagement/deleteBudget',
              method: 'GET',
              params: { budgetManagementId: row.budgetManagementId },
              laoding: true
            }).then(res => {
              this.$message.success(res.message)
              this.getQuerydata()
            })
          })
          .catch(() => {})
      } else {
        this.$refs[this.gridId].deleteRow(scope.$rowIndex)
      }
    },
    // 保存
    async saveHandle (row, scope) {
      const check = [
        'companyId',
        'befAdjustBudgetAmount',
        'deptId',
        'budgetItem',
        'expenseType',
        'year'
      ]
      row.status === 'ADJUSTIVE' && check.push('aftAdjustBudgetAmount')
      for (let key of check) {
        if (!row[key]) return this.$message.warning(this.$t('planMod.validate'))
      }
      this.$set(this.tableHeader[this.tableHeader.length - 1].buttons[2], 'disabled', true)
      const res = await this.$http({
        url: '/api-sup-ce/budget/budgetManagement/saveOrUpdateBudget',
        method: 'POST',
        data: row,
        laoding: true
      })
      if (res.code === '0') {
        this.$message.success(res.message)
        await this.getQuerydata()
        this.$refs[this.gridId].tableData[scope.$rowIndex].editable = false
        this.$set(this.tableHeader[this.tableHeader.length - 1].buttons[2], 'disabled', false)
      }
    },
    // 添加
    addHandle () {
      const props = { editable: true, isNew: true }
      this.tableHeader.forEach(r => {
        props[r.prop] = ''
      })
      this.$refs[this.gridId].addOneEditableColumn(props)
    },
    editHandle (row) {
      Object.assign(row, {
        year: String(row.year),
        editable: true
      })
      this.$set(this.tableHeader[this.tableHeader.length - 1].buttons[2], 'disabled', false)
    },
    handleCurrentChange (val) {
      this.currentRows = val
    }
  }
}
</script>

<style lang="scss" scoped>
:deep(.tool-tip) {
  font-size: 14px;
  padding: 0;
  color: red;
}
</style>
