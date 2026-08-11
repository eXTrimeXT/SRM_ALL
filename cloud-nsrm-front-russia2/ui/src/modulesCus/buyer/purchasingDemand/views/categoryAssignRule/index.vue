<template>
  <el-container
    class="flex-container-notab the_contractPaymentTypeList_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="preArr"
        @getFormData="getQuerydata"
        @synchronous-value="syncFilterParams"
      />
      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <AuthorityButton
            code="po:divisionCategory:add"
            type="primary"
            style="float:left;"
            @click="editTab('add')"
          >
            {{ $t("common.add") }}
          </AuthorityButton>
          <AuthorityButton
            type="primary"
            @click="openAssignOne"
          >
            <!-- 修改负责人 -->
            {{ $t('cusEntry.supplement20250121.chargePerson') }}
          </AuthorityButton>
          <MImport
            ref="import"
            code="po:divisionCategory:import"
            type="default"
            :title="iModal.title"
            :extra-data="iModal.extraData"
            :up-load-url="iModal.upLoadUrl"
            :show-success-deal="true"
            @downloadTemplate="downloadTemplate"
            @handleSuccess="uploadSuccess"
          />
          <ExportExcel
            page-url="/api-sup-ce/division/divisionCategory/listPageByParam"
            :table-header="tableHeader"
            :dict-codes="dictCodes"
            export-mode="front"
            type="default"
            :filter-params="queryParam"
          />
          <AuthorityButton
            type="default"
            :disabled="!selectedRows.length"
            code="po:divisionCategory:delete"
            @click="deleteHandle(selectedRows)"
          >
            {{ $t('cusEntry.common.batchDelete') }}
          </AuthorityButton>
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :current-change="handleCurrentChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :checkbox="true"
        :check-change="handleCurrentChange"
        url="/api-sup-ce/division/divisionCategory/listPageByParam"
      />
      <srm-dialog
        :title="$t('purchaseDemand.categoryDivisionRules')"
        :visible.sync="dialogFormVisible"
        :close-on-click-modal="false"
        :before-close="handleClose"
        size="middle"
      >
        <el-form
          ref="form"
          :model="form"
          class="form-incontainer"
          :rules="rules"
          label-width="80px"
          label-position="top"
        >
          <srm-row>
            <srm-col :initCol="2">
              <!-- 板块 -->
              <el-form-item
                :label="$t('cusEntry.bidSuperviseReport.extOrgBuName')"
                :label-width="formLabelWidth"
                prop="organizationName"
              >
                <el-input v-model="form.organizationName" disabled />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="2">
              <!-- 业务实体 -->
              <el-form-item
                :label="$t('purchaseDemand.businessEntity')"
                :label-width="formLabelWidth"
                prop="orgId"
              >
                <OrganizationSelector
                  ref="organizationSelector"
                  v-model="form.orgId"
                  :parent-id="-1"
                  :placeholder="$t('common.pleaseSelect')"
                  node-type="OU"
                  @select="selectHandler"
                />
              </el-form-item>
            </srm-col>
            <!-- 库存组织 -->
            <!-- <srm-col :initCol="2">
              <el-form-item
                :label="$t('purchaseDemand.invOrg')"
                :label-width="formLabelWidth"
                prop="organizationId"
              >
                <OrganizationSelector
                  ref="organizationSelector2"
                  v-model="form.organizationId"
                  :parent-id="form.orgId"
                  node-type="INV"
                  :placeholder="$t('common.pleaseSelect')"
                  auto-select-when-one-item
                  @select="selectHandler2"
                />
              </el-form-item>
            </srm-col> -->
            <srm-col :initCol="2">
              <!-- 物料小类 -->
              <el-form-item
                :label="$t('purchaseDemand.materialCateSub')"
                :label-width="formLabelWidth"
                prop="categoryName"
              >
                <QuickSearch
                  :show-input="form.categoryName"
                  show-key="categoryName"
                  :scope-data="form"
                  name="scc_base_purchase_category2"
                  @close-quicksearch="getCategoryObj"
                />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="2">
              <!-- 是否主要负责人 -->
              <el-form-item
                :label="$t('purchaseDemand.ifMainPerson')"
                :label-width="formLabelWidth"
                prop="ifMainPerson"
              >
                <DictSelect
                  v-model="form.ifMainPerson"
                  code="YES_OR_NO"
                />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="2">
              <!-- 职责 -->
              <el-form-item
                :label="$t('purchaseDemand.duty')"
                :label-width="formLabelWidth"
                prop="duty"
              >
                <DictSelect
                  v-model="form.duty"
                  code="DUTY"
                />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="2">
              <!-- 负责人 -->
              <el-form-item
                :label="$t('purchaseDemand.personInCharge')"
                :label-width="formLabelWidth"
                prop="personInChargeNickname"
              >
                <QuickSearch
                  :show-input="form.personInChargeNickname"
                  show-key="nickname"
                  :scope-data="form"
                  name="scc_rbac_user_display"
                  @close-quicksearch="getUserObj3"
                />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="2">
              <el-form-item
                :label="$t('contractMod.startDate')"
                :label-width="formLabelWidth"
                prop="startDate"
              >
                <el-date-picker
                  v-model="form.startDate"
                  type="date"
                  :format="$formatDatePicker"
                  :picker-options="pickerOptionsStart"
                />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="2">
              <el-form-item
                :label="$t('contractMod.endDate')"
                :label-width="formLabelWidth"
                prop="endDate"
              >
                <el-date-picker
                  v-model="form.endDate"
                  type="date"
                  :format="$formatDatePicker"
                  :picker-options="pickerOptionsEnd"
                />
              </el-form-item>
            </srm-col>
          </srm-row>
        </el-form>
        <div
          slot="footer"
          class="dialog-footer"
        >
          <el-button @click="handleClose">
            {{ $t("common.cancel") }}
          </el-button>
          <el-button
            type="primary"
            @click="addOneItem"
          >
            {{ $t("common.confirm") }}
          </el-button>
        </div>
      </srm-dialog>
      <!-- 修改负责人 -->
      <srm-dialog
        :title="$t('cusEntry.supplement20250121.chargePerson')"
        size="small"
        :visible.sync="dialogFormShow"
        :close-on-click-modal="false"
      >
        <el-form
          ref="form"
          :model="form"
          class="form-incontainer"
          :rules="rules"
          label-width="80px"
          label-position="top"
        >
          <el-row type="flex">
            <el-col>
              <el-form-item
                :label="$t('purchaseDemand.personInChargeNicknameTips')"
                :label-width="formLabelWidth"
              >
                <QuickSearch
                  :show-input="formData.personInChargeNickname"
                  show-key="nickname"
                  :scope-data="form"
                  name="scc_rbac_user_display"
                  @close-quicksearch="getUserObj11"
                />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>

        <div
          slot="footer"
          class="dialog-footer"
        >
          <el-button @click="dialogFormShow = false">
            {{ $t('common.cancel') }}
          </el-button>
          <el-button
            type="primary"
            :disabled="!formData.personInChargeUserId"
            @click="assignOne"
          >
            {{ $t('common.confirm') }}
          </el-button>
        </div>
      </srm-dialog>
    </el-main>
  </el-container>
</template>
<script>
import MImport from 'lib@/components/import'
import ExportExcel from 'lib@/components/export-excel'
import { downloadFileLink } from 'lib@/utils/file'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { parseTime } from '@/utils'
import QuickSearch from 'lib@/components/QuickSearch'
import OrganizationSelector from 'lib@/components/organization-selector'

export default {
  name: 'CategoryAssignRule',
  components: {
    TableView,
    MainHeader,
    ExportExcel,
    FormWrapper,
    QuickSearch,
    MImport,
    OrganizationSelector
  },
  // mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      dictCodes: {
        duty: 'DUTY',
        ifMainPerson: 'YES_OR_NO',
        enable: 'YES_OR_NO'
      },
      name: 'categoryAssignRuleTable',
      tableName: 'categoryAssignRuleTable',
      iModal: {
        title: this.$t('common.import'),
        extraData: {},
        upLoadUrl: '/api-sup-ce/pj/division/divisionCategory/importExcelNew'
      },
      reviewFormNumber: '',
      gridData: [],
      pageSize: 15,
      gridId: 'list',
      selectList: [],
      currentRow: null,
      showFilterBar: 1,
      tableHeader: [],
      tableData: [],
      initActive: true,
      // collapseTagsBool: true,
      dialogFormVisible: false,
      formLabelWidth: '100px',
      preArr: [
        {
          prop: 'organizationIds',
          label: this.$t('cusEntry.bidSuperviseReport.extOrgBuName'), // 板块
          showKey: 'organizationName',
          propKey: 'organizationId',
          name: 'scc_base_organization',
          type: 'quicksearch',
          preQueryData: {
            't.ORGANIZATION_TYPE_CODE': 'BU',
            't.organizationTypeCode': 'BU'
          }
        },
        {
          prop: 'orgIds',
          label: () => this.$t('purchaseDemand.businessEntity'), // 业务实体
          type: 'OUorganizationSelector',
          multiple: true
        },
        // {
        //   prop: 'organizationIds',
        //   parentId: 'orgIds',
        //   label: () => this.$t('purchaseDemand.invOrg'), // 库存组织
        //   type: 'INVorganizationSelector',
        //   multiple: true
        // },
        {
          prop: 'categoryId',
          label: () => this.$t('purchaseDemand.materialCateSub'), // 物料小类
          type: 'quicksearch',
          showKey: 'categoryName',
          propKey: 'categoryId',
          name: 'scc_base_purchase_category2'
        },
        {
          prop: 'duty',
          label: () => this.$t('purchaseDemand.duty'), // 职责
          type: 'dict',
          code: 'DUTY'
        },
        {
          prop: 'personInChargeNickname',
          label: () => this.$t('purchaseDemand.personInChargeNickname') // 负责人名称
        },
        {
          prop: 'ifMainPerson',
          label: () => this.$t('purchaseDemand.ifMainPerson'), // 是否主要负责人
          type: 'dict',
          code: 'YES_OR_NO'
        },
        {
          prop: 'enable',
          label: () => this.$t('purchaseDemand.enable'), // 是否生效
          type: 'dict',
          code: 'YES_OR_NO'
        }
      ],
      form: {
        divisionCategoryId: null,
        orgId: '',
        orgCode: '',
        orgName: '',
        categoryId: '',
        categoryCode: '',
        categoryName: '',
        organizationId: '',
        organizationCode: '',
        organizationName: '',
        strategyUserId: '',
        strategyUserNickname: '',
        performUserId: '',
        performUserNickname: '',
        startDate: null,
        endDate: null,
        duty: '',
        ifMainPerson: 'N',
        personInChargeUserId: null,
        personInChargeUsername: null,
        personInChargeNickname: null
      },
      rules: {
        duty: [{ required: true, message: this.$t('purchaseDemand.dutyTips') }], // 请选择职责
        startDate: [{ required: true, message: this.$t('purchaseDemand.startDateTips') }], // 请选择生效日期
        orgId: [{ required: true, message: this.$t('purchaseDemand.orgIdTips') }], // 请选择业务实体
        categoryName: [{ required: true, message: this.$t('purchaseDemand.categoryNameTips') }], // 请选择物料小类
        strategyUserNickname: [{ required: true, message: this.$t('purchaseDemand.strategyUserNicknameTips') }], // 请选择策略负责
        performUserNickname: [{ required: true, message: this.$t('purchaseDemand.performUserNicknameTips') }], // 请选择采购履行
        personInChargeNickname: [{ required: true, message: this.$t('purchaseDemand.personInChargeNicknameTips') }], // 请选择负责人
        organizationName: [{ required: true, message: this.$t('common.requiredField') }]
      },
      queryParam: {},
      projectTypeList: [],
      pickerOptionsStart: {
        disabledDate: time => {
          let endDateVal = this.form.endDate
          if (endDateVal) {
            return time.getTime() > new Date(endDateVal).getTime()
          }
        }
      },
      pickerOptionsEnd: {
        disabledDate: time => {
          let beginDateVal = this.form.startDate
          if (beginDateVal) {
            return time.getTime() < new Date(beginDateVal).getTime()
          }
        }
      },
      dialogFormShow: false,
      formData: {
        personInChargeNickname: null,
        personInChargeUsername: null,
        personInChargeUserId: null,
        divisionCategoryIds: []
      },
      selectedRows: []
    }
  },
  created () {
    let _this = this
    this.tableHeader = [
      { prop: 'organizationName', label: this.$t('cusEntry.bidSuperviseReport.extOrgBuName'), minWidth: 130 }, // 板块
      { prop: 'orgName', label: _this.$t('purchaseDemand.businessEntity'), width: 150 }, // 业务实体
      // { prop: 'organizationName', label: _this.$t('purchaseDemand.invOrg'), width: 150 }, // 库存组织
      { prop: 'categoryName', label: _this.$t('purchaseDemand.materialCateSub'), minWidth: 150 }, // 物料小类
      {
        prop: 'ifMainPerson',
        label: _this.$t('purchaseDemand.ifMainPerson'), // 是否主要负责人
        width: 140,
        formattor (val) {
          return val === 'Y' ? _this.$t('common.yes') : _this.$t('common.no')
        }
      },
      {
        prop: 'duty',
        label: _this.$t('purchaseDemand.duty'), // 职责
        width: 120,
        dataType: 'dict',
        code: 'DUTY'

      },
      { prop: 'personInChargeNickname', label: _this.$t('purchaseDemand.personInChargeNickname'), width: 120 }, // 负责人名称
      {
        prop: 'startDate',
        label: _this.$t('purchaseDemand.startDate'), // 生效日期
        width: 100,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'endDate',
        label: _this.$t('purchaseDemand.endDate'), // 失效日期
        width: 100,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'lastUpdatedUserName', // lastUpdatedBy
        label: _this.$t('purchaseDemand.lastUpdatedBy'), // 更新人
        width: 100
      },
      {
        prop: 'lastUpdateDate',
        label: () => _this.$t('purchaseDemand.lastUpdateDate'), // 更新时间
        width: 100,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      // { prop: "payExplain", label: _this.$t('contractMod.payExplain'), minWidth: 150 },
      {
        prop: 'operation',
        label: _this.$t('common.operation'),
        width: 200,
        showType: 'buttons',
        fixed: 'right',
        btnStyle: 'text',
        buttons: [
          {
            callback: function (row) {
              this.editTab('edit', row)
            }.bind(this),
            code: 'po:divisionCategory:edit',
            formattor (val) {
              return _this.$t('common.edit')
            }
          },
          {
            callback: row => this.deleteHandle([row]),
            code: 'po:divisionCategory:delete',
            formattor (val) {
              return _this.$t('common.delete')
            }
          }
        ]
      }
    ]
    this.defaultTableHeader = this.tableHeader

    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    handleCurrentChange (val) {
      this.selectedRows = val
    },
    openAssignOne () {
      if (this.selectedRows.length === 0) {
        this.$message.warning(this.$t('purchaseDemand.selectData'))
        return
      }
      for (const i in this.formData) {
        this.formData[i] = null
      }
      this.dialogFormShow = true
    },
    getUserObj11 (val) {
      this.formData.personInChargeUserId = val?.userId
      this.formData.personInChargeUsername = val?.username
      this.formData.personInChargeNickname = val?.nickname
    },
    assignOne () {
      this.formData.divisionCategoryIds = this.selectedRows.map(v => v.divisionCategoryId)
      this.$http({
        url: '/api-sup-ce/pj/division/divisionCategory/batchUpdatePersonInChargeUser',
        method: 'POST',
        data: this.formData,
        loading: true
      })
        .then(data => {
          this.dialogFormShow = false
          this.$message.success(this.$t('common.success'))
          this.getQuerydata()
        })
        .catch(err => {
          console.log(err)
        })
    },
    getQuerydata (v) {
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
        this.selectedRows = []
      })
    },
    editTab (type, row) {
      if (type === 'add') {
        // 新增
        for (let i in this.form) {
          this.form[i] = null
        }
        // this.form.startDate = new Date();
        this.form.ifMainPerson = 'N'
      } else {
        // 修改
        for (let i in this.form) {
          this.form[i] = row[i]
        }
      }
      this.dialogFormVisible = true
    },
    downloadTemplate () {
      // 下载模板
      downloadFileLink(
        '/api-sup-ce/pj/division/divisionCategory/importModelDownload',
        `${this.$t('dataConfMod.importTemplate')}${new Date().getTime()}.xls`
      )
    },
    uploadSuccess (val) {
      if (val && val.code === '0') this.getQuerydata()
    },
    syncFilterParams (values) {
      this.queryParam = values
    },
    selectHandler (node, value, scope) {
      this.form.orgId = node ? node.organizationId : null
      this.form.orgCode = node ? node.organizationCode : null
      this.form.orgName = node ? node.organizationName : null
      this.$http({
        url: '/api-pj/pj-anon/user/getBuOrganizationByOuOrgCode',
        method: 'GET',
        params: {
          organizationCode: this.form.orgCode
        }
      }).then(response => {
        console.log('response', response)
        if (response.data) {
          let result = response.data || {}
          this.form.organizationId = result.organizationId
          this.form.organizationCode = result.organizationCode
          this.form.organizationName = result.organizationName
        }
      })
    },
    selectHandler2 (node, value, scope) {
      this.form.organizationId = node ? node.organizationId : null
      this.form.organizationCode = node ? node.organizationCode : null
      this.form.organizationName = node ? node.organizationName : null
    },
    addOneItem () {
      this.$refs.form.validate(valid => {
        if (valid) {
          this.form.startDate = this.form.startDate
            ? parseTime(this.form.startDate, '{y}-{m}-{d}', true)
            : null
          this.form.endDate = this.form.endDate
            ? parseTime(this.form.endDate, '{y}-{m}-{d}', true)
            : null
          let urlById =
            '/api-sup-ce/pj/division/divisionCategory/saveDivisionCategory'
          if (this.form.divisionCategoryId) {
            urlById =
              '/api-sup-ce/pj/division/divisionCategory/updateDivisionCategory'
          }
          this.$http({
            url: urlById,
            method: 'POST',
            data: [this.form],
            loading: true
          })
            .then(data => {
              this.$refs.form.resetFields()
              this.dialogFormVisible = false
              this.$message.success(this.$t('common.successSave'))
              this.getQuerydata()
            })
            .catch(err => {
              console.log(err)
            })
        } else {
          return false
        }
      })
    },
    handleClose () {
      this.$refs.form.resetFields()
      this.dialogFormVisible = false
    },
    getCategoryObj (val, scope) {
      scope.categoryId = val ? val.categoryId : ''
      scope.categoryCode = val ? val.categoryCode : ''
      scope.categoryName = val ? val.categoryName : ''
    },
    getUserObj2 (val, scope) {
      scope.strategyUserId = val ? val.userId : ''
      scope.strategyUserName = val ? val.username : ''
      scope.strategyUserNickname = val ? val.nickname : ''
    },
    getUserObj3 (val, scope) {
      scope.personInChargeUserId = val ? val.userId : ''
      scope.personInChargeUsername = val ? val.username : ''
      scope.personInChargeNickname = val ? val.nickname : ''
    },
    deleteHandle (rows) {
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          this.$http({
            url: '/api-sup-ce/pj/division/divisionCategory/deleteBatch',
            method: 'POST',
            data: rows.map(item => item.divisionCategoryId),
            loading: true
          })
            .then(data => {
              this.$message.success(this.$t('common.successDelete'))
              this.getQuerydata()
            })
            .catch(err => {
              console.log(err)
            })
        })
        .catch(() => {})
    }
  }
}
</script>
<style scoped lang="scss"></style>
