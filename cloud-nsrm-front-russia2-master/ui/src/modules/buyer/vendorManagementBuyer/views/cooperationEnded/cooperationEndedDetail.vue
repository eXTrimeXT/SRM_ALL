<template>
  <el-container
    class="the-vendorEffectDetail-detail"
    direction="vertical"
  >
    <el-main>
      <CWorkflowMulti
        ref="workflowMulti"
        v-model="activeTabName"
        :fun-params="workflowParamsInfo"
        :button-config-info="buttonConfigInfo"
        @tab-click="workflowView"
        @workflow-handler="workflowHandler"
        @click-handler="type => dataHandle(type)"
        @submit-direct="type => dataHandle(type)"
        @confirm="(type, comment) => dataHandle(type)"
        @close-tab="back"
      >
        <el-collapse
          v-model="activeDims"
          class="tab-form-style"
        >
          <!-- 合作终止单据 -->
          <el-collapse-item
            :title="$t('vendorMod.cooperationEndedForm')"
            name="1"
          >
            <el-form
              ref="coopForm"
              :model="coopForm"
              :rules="coopRules"
              class="form-fill-style"
              :disabled="curOpt!='add' && curOpt!='edit'"
            >
              <srm-row :gutter="32">
                <srm-col :span="6">
                  <!-- 供应商名称 -->
                  <el-form-item
                    :label="$t('common.vendorName')"
                    prop="vendorName"
                  >
                    <QuickSearch
                      ref="quickCompany"
                      :show-input="coopForm.vendorName"
                      show-key="vendorName"
                      :scope-data="coopForm"
                      name="scc_sup_company_info2"
                      @close-quicksearch="getCompanyObj"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col :span="6">
                  <!-- 控制类型 -->
                  <el-form-item
                    :label="$t('vendorMod.controlType')"
                    prop="supplierControlType"
                  >
                    <DictSelect
                      v-model="coopForm.supplierControlType"
                      code="SUPPLIER_CONTROL_TYPE2"
                      @change="controlTypeChange"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col :span="6">
                  <!-- 生效日期 -->
                  <el-form-item :label="$t('vendorMod.startDate')">
                    <el-date-picker
                      v-model="coopForm.startDate"
                      type="date"
                      :format="$formatDatePicker"
                      value-format="yyyy-MM-dd"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col :span="6">
                  <!-- 控制单号 -->
                  <el-form-item :label="$t('vendorMod.controlNumber')">
                    <el-input
                      v-model="coopForm.orgCatFormNumber"
                      disabled
                    />
                  </el-form-item>
                </srm-col>
                <srm-col :span="6">
                  <!-- 状态 -->
                  <el-form-item :label="$t('vendorMod.operationStatus')">
                    <DictSelect
                      v-model="coopForm.approveStatus"
                      code="APPROVE_STATUS_TYPE"
                      disabled
                    />
                  </el-form-item>
                </srm-col>
                <srm-col :span="6">
                  <!-- 创建人 -->
                  <el-form-item :label="$t('common.creator')">
                    <el-input
                      v-model="coopForm.createdBy"
                      disabled
                    />
                  </el-form-item>
                </srm-col>
                <srm-col :span="6">
                  <!-- 创建时间 -->
                  <el-form-item :label="$t('common.creationTime')">
                    <el-date-picker
                      v-model="coopForm.creationDate"
                      :format="$formatDatePickerTime"
                      value-format="yyyy-MM-dd HH:mm:ss"
                      disabled
                    />
                  </el-form-item>
                </srm-col>
                <srm-col
                  v-if="false"
                  :span="6"
                >
                  <el-form-item :label="$t('vendorMod.transitDay')">
                    <el-input v-model="coopForm.transitDay" />
                  </el-form-item>
                </srm-col>
              </srm-row>
            </el-form>
          </el-collapse-item>
          <!-- 业务控制 0805注释 -->
          <!-- <el-collapse-item
            :title="$t('vendorMod.businessControl')"
            name="2"
          >
            <business :datas="businessData" />
          </el-collapse-item> -->

          <Range
            ref="range"
            :datas="rangeData"
            :range-type="coopForm.supplierControlType"
            :list-data-all="listDataAll"
            :cur-opt="curOpt"
          />

          <!-- 商务事项 -->
          <el-collapse-item
            :title="$t('vendorMod.businessMatter')"
            name="5"
          >
            <el-checkbox-group
              v-model="businessMatterType"
              :disabled="curOpt!='add' && curOpt!='edit'"
            >
              <srm-row>
                <srm-col>
                  <el-checkbox label="20">
                    {{ $t('vendorMod.cooperationEndedDetail[0]') }}
                  </el-checkbox>
                  <el-checkbox label="50">
                    {{ $t('vendorMod.cooperationEndedDetail[1]') }}
                  </el-checkbox>
                  <el-checkbox label="30">
                    {{ $t('vendorMod.cooperationEndedDetail[2]') }}
                  </el-checkbox>
                  <el-checkbox label="40">
                    {{ $t('vendorMod.cooperationEndedDetail[3]') }}
                  </el-checkbox>
                  <el-checkbox label="10">
                    {{ $t('vendorMod.cooperationEndedDetail[4]') }}
                  </el-checkbox>
                </srm-col>
              </srm-row>
            </el-checkbox-group>
            <div style="margin-top:30px">
              {{ $t('vendorMod.cooperationEndedDetail[5]') }}:
              <el-input
                v-model="coopForm.otherExplain"
                :disabled="curOpt!='add' && curOpt!='edit'"
                type="textarea"
                :placeholder="$t('common.pleaseTypeContents')"
              />
            </div>
          </el-collapse-item>
        </el-collapse>
      </CWorkflowMulti>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoMixin } from '@/utils/mixins'
import CToolbar from 'lib@/components/c-toolbar'
import QuickSearch from 'lib@/components/QuickSearch'
import Business from './business'
import Range from './range'
import WorkflowCommon from '@/library/mixins/workflow-common'
import { blackComApi } from 'modb@/vendorManagementBuyer/api/black'
import { saveOrUpdateOrderByUrl } from 'modb@/vendorManagementBuyer/api/vendorManagement'
import { orgCatForm } from 'modb@/vendorManagementBuyer/api/supApi'
/**
 * CATEGORY_EXIT 品类退出 | ORGANIZATION_EXIT 组织退出 | OVERALL_EXIT 整体退出
 * CATEGORY_FORZEN 品类冻结 | ORGANIZATION_FORZEN 组织冻结 | OVERALL_FORZEN 整体冻结
 * CATEGORY_THAW 品类解冻 |  ORGANIZATION_THAW 组织解冻 | OVERALL_THAW 整体解冻
*/

export default {
  name: 'CooperationEndedDetail',
  components: {
    QuickSearch,
    CToolbar,
    Business,
    Range
  },
  mixins: [tabTodoMixin, WorkflowCommon],
  data () {
    return {
      isReadOnly: false,
      listDataAll: [], // 所有详情的信息
      rangeData: [], // 控制范围的数据
      businessData: [], // 业务控制的数据
      orderStatus: '',
      curRole: this.$store.getters.userType, // VENDOR BUYER
      supplierControlType: [], // 控制类型
      businessType: [], // 商务类型
      curCtrlType: '', // 当前控制状态
      coopForm: {
        orgCatFormNumber: '', // 单据号
        supplierControlType: '', // 资质审查类型
        vendorId: null,
        vendorCode: '', // 供应商code
        vendorName: '', // 供应商名称
        approveStatus: 'DRAFT',
        controlExplain: '', // 单据说明
        businessMatterType: '', // 商务信息选项
        otherExplain: '', // 其他说明
        createdBy: '', // 创建人
        creationDate: '', // 创建时间
        startDate: '', // 生效时间
        transitDay: '' // 过渡期业务时间
      },
      coopRules: {
        supplierControlType: [{ required: true, message: this.$t('vendorMod.msgControlType') }], // '请选择控制类型'
        vendorName: [{ required: true, message: this.$t('vendorMod.msgVendorName') }] // '请输入供应商名称'
      },
      businessMatterType: [], // 商务信息选项
      curOpt: 'add',
      activeDims: ['1', '2', '3', '4', '5', '6'],
      queryForm: {
        // 查询条件
        supplierControlType: '',
        companyId: null,
        pageNum: 1,
        pageSize: 10
      },
      pageInfo: {
        total: 0,
        pageNum: 1,
        pageSize: 20
      },
      curOrderId: null // 单据Id
    }
  },
  computed: {
    viewUpdateButton () { // 用来控制保存、提交按钮是否可见。如果自定义按钮则无需添加
      return ['DRAFT', 'REJECTED', 'WITHDRAW'].includes(this.coopForm.approveStatus) && (this.curOpt == 'add' || this.curOpt == 'edit')
    },
    workflowBusinessId () { // 用来指定工作流的业务ID
      return this.curOrderId ? this.curOrderId : null
    },
    // 展示工作流tab页
    workflowTabDisabled () {
      return ['DRAFT'].includes(this.coopForm.approveStatus) // this.$attrs.params.flag != 'doApproval'
    }
  },
  watch: {
    // 监听保存提交 按钮变更状态，如果自定义按钮则无需添加
    viewUpdateButton () {
      this.buttonConfigInfo.save.view = this.viewUpdateButton
      this.buttonConfigInfo.submit.view = this.viewUpdateButton
    }
  },
  created () {
    this.curOpt = this.$attrs.params.flag
    if (['edit', 'view', 'doApproval'].includes(this.curOpt)) {
      this.curOrderId = this.$attrs.params.orderId // 单据Id
      this.getOrderFormDetail()
    }
    // 工作流按钮
    this.buttonConfigInfo.save.view = this.viewUpdateButton
    this.buttonConfigInfo.submit.view = this.viewUpdateButton
    this.buttonConfigInfo.cancel.view = false
    this.buttonConfigInfo.close.view = false
  },
  methods: {
    async getWorkflowBusinessVariables () { // 定义流程变量，如果没有可以不添加
      return {
        formNo: this.coopForm.orgCatFormNumber
      }
    },
    // 指定工作流的业务类型，在定义工作流时指定
    async getWorkflowBusinessType () {
      return 'COOPERATIONEND'
    },
    getCWorkflowRefName () {
      return 'workflowMulti' // 对应CWorkflowMulti标签中的ref
    },
    // 选择供应商回调
    async getCompanyObj (val, data) {
      if (val) {
        let res = await blackComApi.findByCompanyIdAndStatus(val.companyId)
        if (res.data.length == 0) {
          if (val.companyId) {
            this.coopForm.vendorId = val.companyId
          }
          if (val.companyCode) {
            this.coopForm.vendorCode = val.companyCode
          }
          if (val.companyName) {
            this.$set(this.coopForm, 'vendorName', val.companyName)
          } else {
            this.$set(this.coopForm, 'vendorName', '')
          }
          this.queryForm.companyId = val.companyId
        } else {
          this.$refs.quickCompany.getSelectData(null)
          return this.$message({
            type: 'warning',
            message: this.$t('black.msgBlackCompanyAdd')
          })
        }
        if (this.coopForm.supplierControlType !== '') {
          this.controlTypeChange(this.coopForm.supplierControlType)
        }
      } else {
          this.coopForm.vendorId = ''
          this.coopForm.vendorCode = ''
          this.coopForm.vendorName = ''
          this.queryForm.companyId = ''
        if (this.coopForm.supplierControlType !== '') {
          this.controlTypeChange(this.coopForm.supplierControlType)
        }
      }
    },
    // 控制类型切换
    async controlTypeChange (type) {
      this.curCtrlType = type
      this.queryForm.supplierControlType = type
      if (!this.coopForm.vendorId) {
        this.$message({
          message: this.$t('vendorMod.msgVendor'), // '请选择供应商',
          type: 'warning'
        })
        return
      }
      const obj = { vendorId: this.coopForm.vendorId }
      let result = null
      // 解冻类 品类解冻 组织解冻 整体解冻
      if (['CATEGORY_THAW', 'ORGANIZATION_THAW', 'OVERALL_THAW'].includes(type)) {
        result = await orgCatForm.listForzenOrgCategory(obj)
      } else {
        result = await orgCatForm.listOrgCategory(obj)
      }

      const { data } = result
      if (['ORGANIZATION_EXIT', 'ORGANIZATION_FORZEN', 'ORGANIZATION_THAW'].includes(type)) { // 组织退出 组织冻结
        this.rangeData = data.orgRangeList // 组织数据
      } else if (['CATEGORY_EXIT', 'CATEGORY_FORZEN', 'CATEGORY_THAW'].includes(type)) { // 品类退出 品类冻结 品类解冻
        this.rangeData = data.categoryRangeList // 品类数据
      }
      this.listDataAll = data.detailList
    },
    // 查询单据详情
    getOrderFormDetail () {
      let parame = {
        orgCatFormId: this.curOrderId
      }
      orgCatForm.getDetail(this.curOrderId).then(res => {
        this.businessMatterType = res.data.businessMatterType.split(',')
        this.curOrderId = res.data.orgCatFormId
        this.orderStatus = res.data.approveStatus
        this.coopForm = res.data
        this.rangeData = res.data.rangeList
        this.listDataAll = res.data.detailList
      })
    },
    dataHandle (type) {
      this.$refs.coopForm.validate((valid) => {
        if (valid) {
          this.dataHandleFunction(type)
        } else {
          this.__jump_error__(
            'coopForm',
            null,
            this.$t('vendorMod.enterRequired')
          )
          return false
        }
      })
    },
    // end
    // 数据处理
    async dataHandleFunction (type) {
      let url = ''
      let submitData = {} // 单据
      submitData = this.coopForm
      let rangeData = JSON.parse(JSON.stringify(this.rangeData))
      let listDataAll = JSON.parse(JSON.stringify(this.listDataAll))
      if (['CATEGORY_EXIT', 'CATEGORY_FORZEN', 'CATEGORY_THAW'].includes(this.coopForm.supplierControlType)) { // 如果是品类
        let bol2 = 0
        let optSelectRange = this.$refs.range.rangeList.length > 0 ? this.$refs.range.rangeList : this.$refs.range.getRangeList() // 控制范围
        rangeData.forEach(datas => {
          let categoryId = datas.categoryId
          let selectedIndex = optSelectRange.findIndex(i => (i.categoryId == categoryId))
          if (selectedIndex > -1) { // 选择的设置为Y
            datas.selected = 'Y'
            bol2 += 1
          } else {
            datas.selected = 'N'
          }
        })
        if (bol2 == 0) {
          this.__jump_error__(
            'range',
            null,
            this.$t('dataConfMod.msgInputCate')
          )// 请输入品类
          return false
        }
        let bol1 = 0
        let selectedCategoryList = this.$refs.range.categoryList.length > 0 ? this.$refs.range.categoryList : this.$refs.range.getCategoryList() // 控制明细
        listDataAll.forEach(datas => {
          let rowKey = datas.orgId + '_' + datas.categoryId
          let detailSelectedIndex = selectedCategoryList.findIndex(i => (i.orgId + '_' + i.categoryId == rowKey))
          if (detailSelectedIndex > -1) { // 选择的设置为Y
            datas.selected = 'Y'
            bol1 += 1
          } else {
            datas.selected = 'N'
          }
        })
        if (bol1 == 0) {
          this.__jump_error__(
            'range',
            null,
            this.$t('dataConfMod.msgInputUnit2')
          )// 请输入组织
          return false
        }
      } else if (['ORGANIZATION_EXIT', 'ORGANIZATION_FORZEN', 'ORGANIZATION_THAW'].includes(this.coopForm.supplierControlType)) { // 组织退出
        let bol1 = 0
        let optSelectRange = this.$refs.range.rangeList.length > 0 ? this.$refs.range.rangeList : this.$refs.range.getRangeList() // 控制范围
        rangeData.forEach(datas => {
          let orgId = datas.orgId
          let selectedIndex = optSelectRange.findIndex(i => (i.orgId == orgId))
          if (selectedIndex > -1) { // 选择的设置为Y
            datas.selected = 'Y'
            bol1 += 1
          } else {
            datas.selected = 'N'
          }
        })
        if (bol1 == 0) {
          this.__jump_error__(
            'range',
            null,
            this.$t('dataConfMod.msgInputUnit2')
          )// 请输入组织
          return false
        }

        let rangeObj = {}
        rangeData.forEach(rangeItem => {
          if (rangeItem.selected == 'Y') {
            rangeObj[rangeItem.orgId] = rangeItem
          }
        })
        listDataAll.forEach(datas => {
          if (rangeObj.hasOwnProperty(datas.orgId)) {
            datas.selected = 'Y'
          } else {
            datas.selected = 'N'
          }
        })
      } else {
        rangeData = []
        listDataAll.forEach(datas => {
          datas.selected = 'Y'
        })
      }

      submitData.rangeList = rangeData
      submitData.detailList = listDataAll
      submitData.businessMatterType = this.businessMatterType.join(',')
      if (type === 'SUBMIT') {
        // 提交
        submitData.submitFlag = 'SUBMIT'
      } else {
        // 暂存
        submitData.submitFlag = 'SAVE'
      }
      if (this.curOpt == 'add') {
        url = '/api-sup/orgcategory/orgCatForm/add'
      } else {
        url = '/api-sup/orgcategory/orgCatForm/modify'
      }
      saveOrUpdateOrderByUrl(url, submitData).then(async res => {
          this.$message({
            message: res.message,
            type: 'success'
          })
          this.curOrderId = res.data
          if (type === 'SAVE') {
            this.curOpt = 'edit'
            await this.getOrderFormDetail()
            // this.$emit('tab-remove', this.$attrs.params.tabName)
            // this.__setTabTodo('CooperationEndedList.getQuerydata')
          } else {
            await this.getOrderFormDetail()
            await this.handlerAfter(type)
          }
        })
        .catch(err => {
          console.log(err)
        })
    },
    back () {
      this.$emit('tab-remove', this.$attrs.params.tabName)
      this.__setTabTodo('CooperationEndedList.getQuerydata')
    }
  }
}
</script>
<style scoped lang="scss">
.the-vendorEffectDetail-detail {
  .el-table .el-date-editor {
    width: 135px;
  }
  .el-collapse-item__content > .el-button {
    margin-bottom: 5px;
  }
}
</style>
