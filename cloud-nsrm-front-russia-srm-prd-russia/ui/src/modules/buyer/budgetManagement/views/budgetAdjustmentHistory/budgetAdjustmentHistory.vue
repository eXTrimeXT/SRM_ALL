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
          <ExportExcel
            page-url="/api-sup-ce/budget/budgetManagementHistory/listPage"
            :filter-params="queryParam"
            :table-header="tableHeader"
            :dict-codes="dictCodes"
            timeout="1000000"
            export-mode="front"
          />
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
        url="/api-sup-ce/budget/budgetManagementHistory/listPage"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MImport from 'lib@/components/import'
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
      companyInfo: [],
      name: 'budgetmanagementList',
      tableName: 'budgetmanagementTable',
      pageSize: 15,
      gridId: 'list',
      currentRows: [],
      dictCodes: {
        status: 'BUDGET_MANAGEMENT_STATUS',
        budgetItem: 'BUDGET_ITEM',
        expenseType: 'EXPENSE_TYPE'
      },
      filterParams: {},
      tableHeader: [
        {
          prop: 'companyName',
          label: this.$t('vendorMod.corporateName'), // 预算部门
          width: 130
        },
        {
          prop: 'deptName',
          label: this.$t('purchaseDemand.budgetDepartment'), // 预算部门
          width: 130
        },
        {
          prop: 'year',
          label: this.$t('purchaseDemand.budgetYear'), // 预算年份
          width: 110
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
          formattor: (val) => this.$getDictLabel('EXPENSE_TYPE', val)
        },
        {
          prop: 'budgetItem',
          label: this.$t('purchaseDemand.budgetAccount'), // 预算科目
          width: 130,
          formattor: (val) => this.$getDictLabel('BUDGET_ITEM', val)
        },
        {
          prop: 'befAdjustBudgetAmount',
          label: this.$t('purchaseDemand.budgetBeforeAdjustment'), // 调整前预算金额
          width: 150
        },
        {
          prop: 'aftAdjustBudgetAmount',
          label: this.$t('purchaseDemand.budgetafterAdjustment'), // 调整后预算金额
          width: 150
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
        }
        // 创建日期
        // { prop: 'creationDate', label: () => this.$t('quota.createdDate'), type: 'date' }
      ],
      queryParam: {}
    }
  },
  created () {
    this.defaultTableHeader = this.tableHeader
    this.$nextTick(() => {
      this.getQuerydata()
      this.getOrgInfoList()
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
          this.tableHeader[0].options = companys
          this.companyInfo = companys
        })
        .catch(err => {
          console.log(err)
        })
    },
    syncFilterParams (values) {
      this.filterParams = values
    },
    getQuerydata (params = {}) {
      this.queryParam = params
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    handleCurrentChange (val) {
      this.currentRows = val
    }
  }
}
</script>
