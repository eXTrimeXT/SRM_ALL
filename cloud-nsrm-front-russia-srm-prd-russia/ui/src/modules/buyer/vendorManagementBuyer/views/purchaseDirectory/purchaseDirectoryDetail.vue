<template>
  <el-container class="flex-container" direction="vertical">
    <el-main>
      <el-form
        ref="form"
        :model="formData"
        :rules="rules"
        :disabled="disabledFlag"
      >
        <el-collapse v-model="colValue">
          <el-collapse-item ref="materialBaseInfo" :title="$t('purchase.BasicMaterialInformation')" name="1">
            <srm-row>
              <srm-col>
                <el-form-item :label="$t('orderMod.buyerOrderSynergy.vendorName')" prop="vendorName">
                  <QuickSearch
                    :show-input="formData.vendorName"
                    show-key="companyName"
                    :scope-data="formData"
                    :disabled="disabledFlag"
                    name="scc_sup_company_info2"
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
                    :disabled="false"
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
                    :disabled="false"
                    :parent-id="formData.purchaseOrgId"
                    node-type="INV"
                    @select="invSelectHandler"
                  />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item :label="$t('purSettlementMod.materialId')" prop="materialName">
                  <QuickSearch
                    :show-input="formData.materialName"
                    show-key="materialName"
                    :scope-data="formData"
                    :disabled="disabledFlag"
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
                  <dict-select v-model="formData.catalogStatus" code="CATALOG_STATUS" disabled />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item :label="$t('dataConfMod.createdBy')">
                  <el-input v-model="formData.createdUserName" disabled />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item :label="$t('common.creationTime')">
                  <el-input v-model="formData.creationDate" disabled />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item :label="$t('basicPrice.dataSource')">
                  <dict-select v-model="formData.dataSource" code="PURCHASE_DATA_SOURCE" disabled />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item :label="$t('common.effectTime')">
                  <el-date-picker
                    v-model="formData.startDate"
                    value-format="yyyy-MM-dd"
                    :placeholder="$t('common.selectDate')"
                    :picker-options="cannotLessCurrentTimeOptions"
                  />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item :label="$t('dataConfMod.endDateTime')">
                  <el-date-picker
                    v-model="formData.endDate"
                    value-format="yyyy-MM-dd"
                    :placeholder="$t('common.selectDate')"
                    :picker-options="cannotLessCurrentTimeOptions"
                  />
                </el-form-item>
              </srm-col>
            </srm-row>
          </el-collapse-item>
          <el-collapse-item :title="$t('purchase.MaterialAttributeInformation')" name="2">
            <srm-row>
              <srm-col>
                <el-form-item :label="$t('dataConfMod.orderQuantityMinimum')">
                  <el-input v-model="formData.minOrderNum" v-input-format="{ type: 'integer' }" min="1" />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item :label="$t('dataConfMod.minimumSafetyInventory')">
                  <el-input v-model="formData.minInventory" v-input-format="{ type: 'integer' }" min="1" />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item :label="$t('dataConfMod.band')">
                  <el-input v-model="formData.brand" />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item :label="$t('dataConfMod.minimumPackingQuantity')">
                  <el-input v-model="formData.innerBoxMinPackNum" v-input-format="{ type: 'integer' }" min="1" />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item :label="$t('dataConfMod.maxPackingCarton')">
                  <el-input v-model="formData.outerBoxPageNum" v-input-format="{ type: 'integer' }" min="1" />
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
          <el-collapse-item :title="$t('accountMod.relevantAttachment')" name="3">
            <el-button type="primary" @click="fileAdd">
              {{ $t('bidMod.affairsIncreased') }}
            </el-button>
            <el-table
              class="mt-10"
              :data="fileData"
              border
              stripe
            >
              <el-table-column
                type="index"
                width="60"
                :label="$t('contractMod.order')"
              />
              <SrmCommonFile
                type="table-column"
                :extra-data="fileInfo"
                :table-column-options="{
                  label: $t('vendorMod.attachmentUpload'),
                  prop: 'fileId',
                  nameProp: 'fileName',
                  minWidth: '150px'
                }"
                :readonly="disabledFlag"
                @on-change="filesUploadSuccess"
              />
              <el-table-column
                prop="createdUserName"
                :label="$t('quota.uploadBy')"
              />
              <el-table-column
                prop="creationDate"
                :label="$t('components.fileupload.uploadDate')"
              />
              <el-table-column
                :label="$t('formula.handle')"
                width="100"
              >
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
    </el-main>
    <CToolbar>
      <template slot="right">
        <el-button type="ghost" @click="back">
          {{ $t('bidMod.cancel') }}
        </el-button>
        <template v-if="!disabledFlag">
          <el-button type="primary" @click="saveBill('SAVE')">
            {{ $t('bidMod.temporaryStorage') }}
          </el-button>
          <el-button type="primary" @click="saveBill('SUBMIT')">
            {{ $t('problemManagement.submit') }}
          </el-button>
        </template>
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
import { cannotLessCurrentTime } from '@/library/mixins/datePickerOptions'

export default {
  name: 'PurchaseDirectoryDetail',
  components: {
    QuickSearch,
    OrganizationSelector,
    CToolbar,
    FileDynamic
  },
  mixins: [tabTodoMixin, validate, cannotLessCurrentTime],
  data () {
    return {
      colValue: ['1', '2', '3'],
      formData: {
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
        catalogStatus: 'DRAFT', // 默认为拟定
        createdUserName: '',
        creationDate: '',
        startDate: '',
        endDate: '',
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
        packNum: ''
      },
      rules: {
        vendorName: [{ required: true, message: this.$t('vendorMod.msgVendorId') }], // '请输入供应商名称
        purchaseOrgName: [{ required: true, message: this.$t('dataConfMod.msgInputItemCode') }], // '请输入物料编号
        materialName: [{ required: true, message: this.$t('dataConfMod.msgInputItemCode') }], // '请输入物料编号
        purchaseOrgId: [{ required: true, message: '请输入业务实体' }],
        invId: [{ required: true, message: '请输入库存组织' }]
      },
      fileData: [],
      fileInfo: {
        fileModular: 'sup',
        fileFunction: 'purchaseDirectory',
        fileType: 'images'
      },
      inputFormat: { type: 'float', digits: 2, negative: false, zero: false }
    }
  },
  computed: {
    urlParams () {
      return this.$attrs.params || {}
    },
    disabledFlag () {
      return this.urlParams.flag === 'view'
    }
  },
  created () {
    let { flag, row } = this.urlParams
    if (row.catalogId) {
      this.getFormDetail(row.catalogId)
    }
  },
  methods: {
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
      this.formData.invId = null
      this.formData.invCode = null
      this.formData.invName = null
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
    filesUploadSuccess ({ file, $index }) {
      const { fileId = '', fileName = '' } = file || {}
      this.fileData[$index].fileId = fileId
      this.fileData[$index].fileName = fileName
    },
    back () {
      let { tabName } = this.$attrs.params
      this.$emit('tab-remove', tabName)
      this.__setTabTodo('PurchaseDirectoryList.getQuerydata')
    },
    initParams () { // 参数
      let params = {}
      for (let key in this.formData) {
        params[key] = this.formData[key]
      }
      params.purCatalogAttList = this.fileData
      return params
    },
    saveBill (type) {
      let params = this.initParams()
      if (type === 'SAVE') {
        this.handleSave(params).then(res => {
          this.$message.success(this.$t('common.success'))
          if (res.data) this.getFormDetail(res.data)
        })
      } else if (type === 'SUBMIT') {
        let validForm
        this.$refs.form.validate(valid => { validForm = valid })
        if (!validForm) {
          this.__focus_error__()
          return
        }
        if (this.formData.startDate >= this.formData.endDate) {
          return this.__jump_error__('materialBaseInfo', null, '失效时间必须大于生效时间')
        }

        this.handleSubmit(params).then(() => {
          this.$message.success(this.$t('common.success'))
          this.back()
        })
      }
    },
    handleSave (params) { // 保存接口
      return this.$http({
        url: '/api-sup/purchaseCataLog/saveOrUpdateCatalog',
        method: 'POST',
        data: params,
        loading: true
      })
    },
    handleSubmit (params) { // 提交接口
      return this.$http({
        url: '/api-sup/purchaseCataLog/submitCatalog',
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
      })
    }
  }
}
</script>
<style lang="scss" scoped>
.mt-10 {
  margin-top: 10px;
  margin-bottom: 20px;
}
</style>
