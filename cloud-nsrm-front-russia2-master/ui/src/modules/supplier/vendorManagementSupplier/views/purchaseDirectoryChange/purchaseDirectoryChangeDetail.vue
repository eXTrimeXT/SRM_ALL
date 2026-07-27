<template>
  <el-container class="flex-container" direction="vertical">
    <el-main>
      <CWorkflowMulti
        ref="workflowMulti"
        v-model="activeTabName"
        :fun-params="workflowParamsInfo"
        :button-config-info="buttonConfigInfo"
        :button-custom="buttonCustom"
        @tab-click="workflowView"
        @workflow-handler="workflowHandler"
        @click-handler="type => saveBill(type)"
        @submit-direct="type => saveBill(type)"
        @confirm="(type, comment) => saveBill(type, comment)"
        @close-tab="back"
      >
        <el-form ref="form" :model="formData" :rules="rules" :disabled="disabledFlag">
          <el-collapse v-model="colValue">
            <el-collapse-item :title="$t('purchase.BasicMaterialInformation')" name="1">
              <srm-row>
                <srm-col>
                  <el-form-item :label="$t('orderMod.buyerOrderSynergy.vendorName')" prop="vendorName">
                    <QuickSearch
                      :show-input="formData.vendorName"
                      show-key="companyName"
                      :scope-data="formData"
                      name="scc_sup_company_info_display"
                      disabled
                      @close-quicksearch="getCompanyObj"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item :label="$t('common.vendorCode')">
                    <el-input v-model="formData.vendorCode" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item :label="$t('bidMod.businessEntity')" prop="purchaseOrgId">
                    <OrganizationSelector
                      ref="orgSelector1"
                      v-model="formData.purchaseOrgId"
                      :placeholder="$t('common.pleaseSelect')"
                      disabled
                      :parent-id="-1"
                      node-type="OU"
                      @select="ouSelectHandler"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item :label="$t('purchaseDemand.invOrg')" prop="invId">
                    <OrganizationSelector
                      ref="orgSelector2"
                      v-model="formData.invId"
                      :placeholder="$t('common.pleaseSelect')"
                      disabled
                      :parent-id="formData.purchaseOrgId"
                      node-type="INV"
                      @select="invSelectHandler"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item :label="$t('purSettlementMod.materialId')" prop="materialName">
                    <QuickSearch
                      disabled
                      :show-input="formData.materialName"
                      show-key="materialName"
                      :scope-data="formData"
                      name="scc_base_material_item"
                      @close-quicksearch="getMaterialObj"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item :label="$t('mould.itemNumber')">
                    <el-input v-model="formData.materialCode" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item :label="$t('orderMod.categoryName')">
                    <el-input v-model="formData.categoryName" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item :label="$t('priceFormula.formulaStatus')">
                    <dict-select
                      v-model="formData.changeStatus"
                      code="MATERIAL_LIST_CHANGE"
                      disabled
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item :label="$t('dataConfMod.createdBy')">
                    <el-input v-model="formData.createdUserName" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item :label="$t('common.creationTime')">
                    <el-date-picker
                      v-model="formData.creationDate"
                      :format="$formatDatePickerTime"
                      value-format="yyyy-MM-dd HH:mm:ss"
                      disabled
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item :label="$t('purchase.SourceChangeOrderNo')">
                    <el-input v-model="formData.changeNo" disabled />
                  </el-form-item>
                </srm-col>

                <srm-col>
                  <el-form-item :label="$t('common.effectTime')">
                    <el-date-picker
                      v-model="formData.startDate"
                      value-format="yyyy-MM-dd"
                      :format="$formatDatePicker"
                      :placeholder="$t('common.selectDate')"
                      :picker-options="cannotLessCurrentTimeOptions"
                      disabled
                    />
                    <!-- :disabled="urlParams.flag !== 'edit'" -->
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item :label="$t('dataConfMod.endDateTime')">
                    <el-date-picker
                      v-model="formData.endDate"
                      value-format="yyyy-MM-dd"
                      :format="$formatDatePicker"
                      :placeholder="$t('common.selectDate')"
                      :picker-options="cannotLessCurrentTimeOptions"
                      disabled
                    />
                    <!-- :disabled="urlParams.flag !== 'edit'" -->
                  </el-form-item>
                </srm-col>
              </srm-row>
            </el-collapse-item>
            <el-collapse-item :title="$t('purchase.MaterialAttributeInformation')" name="2">
              <srm-row>
                <srm-col>
                  <el-form-item :label="$t('dataConfMod.orderQuantityMinimum')">
                    <el-input
                      v-model="formData.minOrderNum"
                      v-input-format="{ type: 'integer' }"
                      min="1"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item :label="$t('dataConfMod.minimumSafetyInventory')">
                    <el-input
                      v-model="formData.minInventory"
                      v-input-format="{ type: 'integer' }"
                      min="1"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item :label="$t('dataConfMod.band')">
                    <el-input v-model="formData.brand" />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item :label="$t('dataConfMod.minimumPackingQuantity')">
                    <el-input
                      v-model="formData.innerBoxMinPackNum"
                      v-input-format="{ type: 'integer' }"
                      min="1"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item :label="$t('dataConfMod.maxPackingCarton')">
                    <el-input
                      v-model="formData.outerBoxPageNum"
                      v-input-format="{ type: 'integer' }"
                      min="1"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item :label="$t('purchase.PlaceOfOrigin')">
                    <el-input v-model="formData.placeOrigin" />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item :label="$t('purchase.DeliveryTime')">
                    <el-input v-model="formData.deliveryTime" v-input-format="inputFormat" />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item :label="$t('purchase.NumberOfPackages')">
                    <el-input v-model="formData.packNum" v-input-format="inputFormat" />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item :label="$t('purchase.fullContainer')">
                    <el-input v-model="formData.grossWeight" v-input-format="inputFormat" />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item :label="$t('purchase.OuterBoxLength')">
                    <el-input v-model="formData.outerBoxLong" v-input-format="inputFormat" />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item :label="$t('purchase.OuterBoxWidth')">
                    <el-input v-model="formData.outerBoxWide" v-input-format="inputFormat" />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item :label="$t('purchase.OuterBoxHeight')">
                    <el-input v-model="formData.outerBoxHide" v-input-format="inputFormat" />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item :label="$t('purchase.InnerBoxLength')">
                    <el-input v-model="formData.innerBoxLong" v-input-format="inputFormat" />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item :label="$t('purchase.InnerBoxWidth')">
                    <el-input v-model="formData.innerBoxWide" v-input-format="inputFormat" />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item :label="$t('purchase.InnerBoxHeight')">
                    <el-input v-model="formData.innerBoxHide" v-input-format="inputFormat" />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item :label="$t('purchase.InnerBoxWeight')">
                    <el-input v-model="formData.innerBoxWeight" v-input-format="inputFormat" />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item :label="$t('purchase.NumberOfInnerCases')">
                    <el-input v-model="formData.innerBoxPackNum" v-input-format="inputFormat" />
                  </el-form-item>
                </srm-col>
              </srm-row>
            </el-collapse-item>
            <el-collapse-item name="3">
              <template slot="title">
                <i class="toRequired">*</i>
                {{ $t('purchase.UpdateReasonDescription') }}
              </template>
              <el-form-item label="" prop="updatedReason">
                <el-input
                  v-model="formData.updatedReason"
                  type="textarea"
                  :rows="4"
                  :maxlength="200"
                  :placeholder="$t('purchase.PleaseEnterTheReason')"
                  show-word-limit
                />
              </el-form-item>
            </el-collapse-item>
            <el-collapse-item :title="$t('accountMod.relevantAttachment')" name="4">
              <el-button type="primary" @click="fileAdd">
                {{ $t('bidMod.affairsIncreased') }}
              </el-button>
              <el-table class="mt-10" :data="fileData" border stripe>
                <el-table-column type="index" width="60" :label="$t('contractMod.order')" />
                <el-table-column :label="$t('contractMod.addUploadFile')">
                  <template slot-scope="{row,index}">
                    <SrmCommonFile
                      :extra-data="fileInfo"
                      :default-file="{
                        fileId: row.fileId,
                        fileName: row.fileName
                      }"
                      :readonly="disabledFlag"
                      @on-change="({file}) => fileSuccess(file,row,index)"
                    />
                  </template>
                </el-table-column>
                <el-table-column prop="createdUserName" :label="$t('quota.uploadBy')" />
                <el-table-column prop="creationDate" :label="$t('components.fileupload.uploadDate')" :formatter="(row, column, cellValue) => $parseTime(cellValue)" />
                <el-table-column :label="$t('formula.handle')" width="100">
                  <template slot-scope="scope">
                    <el-button type="text" @click="deleteFile(scope)">
                      {{ $t('components.common.delete') }}
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-collapse-item>
          </el-collapse>
        </el-form>
      </CWorkflowMulti>
    </el-main>
    <CToolbar
      v-if="curRole === 'VENDOR' && ['FIRST_REJECT','DRAFT'].includes(formData.changeStatus) && !disabledFlag"
    >
      <template slot="right">
        <el-button type="ghost" @click="back">
          {{ $t('bidMod.cancel') }}
        </el-button>
        <el-button type="primary" @click="saveBill('SAVE')">
          {{ $t('bidMod.temporaryStorage') }}
        </el-button>
        <el-button type="primary" @click="vendorSubmit">
          {{ $t('problemManagement.submit') }}
        </el-button>
      </template>
    </CToolbar>
    <CToolbar
      v-if="curRole === 'BUYER' && formData.changeStatus === 'CONFIRMING' && urlParams.flag === 'manage'"
    >
      <template slot="right">
        <el-button type="ghost" @click="back">
          {{ $t('bidMod.cancel') }}
        </el-button>
        <el-button type="primary" @click="handlePass">
          {{ $t('common.toApprove') }}
        </el-button>
        <el-button type="primary" @click="handleReject">
          {{ $t('orderMod.buyerOrderSynergy.sureRefuse') }}
        </el-button>
      </template>
    </CToolbar>
  </el-container>
</template>
<script>
import { tabTodoMixin } from '@/utils/mixins'
import CToolbar from 'lib@/components/c-toolbar'
import QuickSearch from 'lib@/components/QuickSearch'
import OrganizationSelector from 'lib@/components/organization-selector'
import validate from 'lib@/mixins/validate'
import FileDynamic from 'lib@/components/c-file-management/file-dynamic'
import WorkflowCommon from '@/library/mixins/workflow-common'
import { cannotLessCurrentTime } from '@/library/mixins/datePickerOptions'

export default {
  name: 'PurchaseDirectoryChangeDetail',
  components: {
    QuickSearch,
    OrganizationSelector,
    CToolbar,
    FileDynamic
  },
  mixins: [tabTodoMixin, validate, WorkflowCommon, cannotLessCurrentTime],
  data () {
    return {
      colValue: ['1', '2', '3', '4'],
      changeId: null,
      formData: {
        changeId: null,
        changeNo: null,
        startDate: '',
        endDate: '',
        catalogId: null, // 主键id
        vendorId: null,
        vendorCode: '',
        vendorName: '',
        materialCode: '',
        materialName: '',
        materialId: null,
        categoryId: null,
        categoryName: '',
        categoryCode: '',
        categoryFullName: '',
        changeStatus: 'DRAFT', // 默认为拟定
        createdUserName: '',
        creationDate: '',
        dataSource: 'MANUAL_CREATE', // 默认为手工新增
        purchaseOrgId: null,
        purchaseOrgCode: null,
        purchaseOrgName: null,
        invId: null,
        invCode: null,
        invName: null,
        minOrderNum: '',
        minInventory: '',
        brand: '',
        outerBoxPageNum: '',
        placeOrigin: '',
        deliveryTime: '',
        grossWeight: '',
        outerBoxLong: '',
        outerBoxWide: '',
        outerBoxHide: '',
        innerBoxLong: '',
        innerBoxWide: '',
        innerBoxHide: '',
        innerBoxPackNum: '',
        innerBoxMinPackNum: '',
        innerBoxWeight: '',
        packNum: '',
        updatedReason: ''
      },
      rules: {
        vendorName: [{ required: true, message: this.$t('vendorMod.msgVendorId') }], // '请输入供应商名称
        purchaseOrgName: [{ required: true, message: this.$t('dataConfMod.msgInputItemCode') }], // '请输入物料编号
        materialName: [{ required: true, message: this.$t('dataConfMod.msgInputItemCode') }], // '请输入物料编号
        purchaseOrgId: [{ required: true, message: this.$t('vendorMod.purchaseOrgId') }],
        invId: [{ required: true, message: this.$t('vendorMod.invId') }],
        updatedReason: [{ required: true, message:this.$t('dataConfMod.enterUpdateReason') }]
      },
      fileData: [],
      fileInfo: {
        fileModular: 'sup',
        fileFunction: 'purchaseDirectory',
        fileType: 'images'
      },
      curRole: this.$store.getters.userType,
      curAction: '', // 判断审批流页签是否可选 approval no-approval
      inputFormat: { type: 'float', digits: 2, negative: false, zero: false }
    }
  },
  computed: {
    urlParams () {
      return this.$attrs.params || {}
    },
    disabledFlag () {
      return !!['view', 'manage', 'approval'].includes(this.urlParams.flag)
    },
    viewUpdateButton () { // 用来控制保存、提交按钮是否可见。如果自定义按钮则无需添加
      // return (this.curRole === 'BUYER' && !this.disabledFlag && this.formData.changeStatus !== 'APPROVED')
      return (this.curRole === 'BUYER' && (!this.disabledFlag || this.urlParams.flag === 'submit') && this.formData.changeStatus !== 'APPROVED')
    },
    disabledUpdateButton () {
      return ['APPROVING', 'FIRST_APPROVED'].includes(this.formData.changeStatus)
    },
    workflowBusinessId () { // 用来指定工作流的业务ID 单据ID 注意每一个单的ID不一样
      return this.changeId || null
    },
    workflowTabDisabled () {
      if (this.curRole === 'BUYER') { // 采购商
        if (this.curAction) return this.curAction !== 'approval'
        return !!['DRAFT', 'CONFIRMING', 'FIRST_APPROVED', 'FIRST_REJECT'].includes(this.formData.changeStatus)
      }
      return true
    }
  },
  watch: {
    viewUpdateButton () {
      this.buttonConfigInfo.save.view = this.viewUpdateButton
      this.buttonConfigInfo.submit.view = this.viewUpdateButton
    },
    disabledUpdateButton () {
      this.buttonConfigInfo.save.disabled = this.disabledUpdateButton
      // this.buttonConfigInfo.submit.disabled = this.disabledUpdateButton
      this.buttonConfigInfo.submit.disabled = this.disabledUpdateButton && this.urlParams.flag !== 'submit'
    }
  },
  created () {
    let { flag, row } = this.urlParams
    if (flag === 'add' && row.catalogId) {
      this.getFormDetail(row.catalogId)
    }
    if (row.changeId) {
      this.changeId = row.changeId
      this.getChangeFormDetail(row.changeId)
    }
    this.getButtonConfig()
  },
  methods: {
    getButtonConfig () {
      this.buttonConfigInfo.save.view = this.viewUpdateButton
      this.buttonConfigInfo.submit.view = this.viewUpdateButton
      this.buttonConfigInfo.save.disabled = this.disabledUpdateButton
      // this.buttonConfigInfo.submit.disabled = this.disabledUpdateButton
      this.buttonConfigInfo.submit.disabled = this.disabledUpdateButton && this.urlParams.flag !== 'submit'
      this.buttonConfigInfo.cancel.view = this.curRole === 'VENDOR' ? false : !this.disabledFlag
      this.buttonConfigInfo.close.view = this.curRole === 'VEDNOR' ? false : this.disabledFlag
    },
    getCompanyObj (val) {
      this.formData.vendorId = val ? val.companyId : null
      this.formData.vendorCode = val ? val.companyCode : null
      this.formData.vendorName = val ? val.companyName : null
    },
    // 确认选择物料
    getMaterialObj (val) {
      this.formData.materialCode = val ? val.materialCode : ''
      this.formData.materialName = val ? val.materialName : ''
      this.formData.materialId = val ? val.materialId : null
      this.formData.categoryId = val ? val.categoryId : null
      this.formData.categoryName = val ? val.categoryName : ''
      this.formData.categoryCode = val ? val.categoryCode : ''
      this.formData.categoryFullName = val ? val.categoryFullName : ''
    },
    ouSelectHandler (node, value, scope) {
      this.formData.purchaseOrgId = node ? node.organizationId : null
      this.formData.purchaseOrgCode = node ? node.organizationCode : null
      this.formData.purchaseOrgName = node ? node.organizationName : null
    },
    invSelectHandler (node, value, scope) {
      this.formData.invId = node ? node.organizationId : null
      this.formData.invCode = node ? node.organizationCode : null
      this.formData.invName = node ? node.organizationName : null
    },
    fileAdd () {
      this.fileData.push({
        fileId: '',
        fileName: ''
      })
    },
    deleteFile (scope) {
      let { row, $index } = scope
      this.fileData.splice($index, 1)
    },
    fileSuccess (file, row, index) {
      const { fileId = '', fileName = '' } = file || {}
      row.fileId = fileId.toString()
      row.fileName = fileName
    },
    back () {
      let { tabName } = this.$attrs.params
      console.log('tabName:::', tabName)
      this.$emit('tab-remove', tabName)
      this.__setTabTodo('PurchaseDirectoryChangeList.getQuerydata')
    },
    initParams () { // 参数
      let params = {}
      for (let key in this.formData) {
        params[key] = this.formData[key]
      }
      let fileAff = this.fileData.filter(item => !!item.fileId)
      params.purCatalogAttChangeList = fileAff
      console.log('params:::', params)
      return params
    },
    async saveBill (type) {
      console.log('type:::', type)
      let params = this.initParams()
      let saveMethods = this.formData.changeId ? this.handleSubmit : this.handleSave
      if (type === 'SAVE') {
        saveMethods(params).then((res) => {
          if (res.data) {
            this.$message.success(this.$t('common.success'))
            this.getChangeFormDetail(res.data)
          }
        })
      } else if (type === 'SUBMIT') {
        let validForm
        this.$refs.form.validate(valid => {
          validForm = valid
        })
        if (!validForm) {
          this.__focus_error__()
          return
        }
        saveMethods(params).then(async (res) => {
          this.curAction = 'approval'
          this.$message.success(this.$t('common.success'))
          await this.getChangeFormDetail(res.data)
          await this.handlerAfter(type)
        })
      }
    },
    vendorSubmit () { // 供应商提交
      let validForm
      this.$refs.form.validate(valid => { validForm = valid })
      if (!validForm) {
        this.__focus_error__()
        return
      }
      let params = this.initParams()
      this.$http({
        url: '/api-sup/purCatalogChange/submitFirst',
        method: 'POST',
        data: params,
        loading: true
      }).then(() => {
        this.back()
      })
    },
    handlePass () { // 采购商通过
      let params = this.initParams()
      this.$http({
        url: '/api-sup/purCatalogChange/firstPass',
        method: 'POST',
        data: params,
        loading: true
      }).then(() => {
        this.back()
      })
    },
    handleReject () { // 采购商驳回
      let params = this.initParams()
      this.$http({
        url: '/api-sup/purCatalogChange/firstReject',
        method: 'POST',
        data: params,
        loading: true
      }).then(() => {
        this.back()
      })
    },
    handleSave (params) { // 暂存接口
      return this.$http({
        url: '/api-sup/purCatalogChange/addPurCatalogChange',
        method: 'POST',
        data: params,
        loading: true
      })
    },
    handleSubmit (params) { // 修改接口
      return this.$http({
        url: '/api-sup/purCatalogChange/modify',
        method: 'POST',
        data: params,
        loading: true
      })
    },
    getFormDetail (catalogId) {
      this.$http({
        url: '/api-sup/purchaseCataLog/get',
        method: 'GET',
        params: { catalogId },
        loading: true
      }).then(res => {
        let data = res.data || {}
        let { purCatalogAttList = [] } = data
        this.formData = data
        this.fileData = purCatalogAttList
        delete this.formData.purCatalogAttList
        this.formData.changeStatus = 'DRAFT'
        let attrs = ['createdBy', 'createdByIp', 'createdFullName', 'createdId', 'createdUserName', 'creationDate', 'updatedReason']
        for (let key of attrs) {
          this.formData[key] = null
        }
      })
    },
    async getChangeFormDetail (changeId) {
      let res = await this.$http({
        url: '/api-sup/purCatalogChange/get',
        method: 'GET',
        params: { changeId },
        loading: true
      })
      let data = res.data || {}
      let { purCatalogAttChangeList = [], ...rest } = data
      Object.assign(this.formData, rest)
      this.changeId = this.formData.changeId
      this.fileData = purCatalogAttChangeList
    },
    // 指定工作流的业务类型，在定义工作流时指定
    async getWorkflowBusinessType () {
      return 'PURLOGCHANGE'
    },
    // 获取ref值
    getCWorkflowRefName () {
      return 'workflowMulti' // 对应CWorkflowMulti标签中的ref
    },
    // 定义流程额外变量，如果没有就不用添加这个函数
    async getWorkflowBusinessVariables () {
      return {
        // Amount: this.requirementHead.ceeaTotalBudget
      }
    }
  }
}
</script>
<style lang="scss" scoped>
.mt-10 {
  margin-top: 10px;
  margin-bottom: 10px;
}
</style>
