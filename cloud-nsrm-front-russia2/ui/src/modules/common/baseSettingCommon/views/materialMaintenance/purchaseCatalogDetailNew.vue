<template>
  <el-container
    class="flex-container the-purchaseCatalogDetail-detail"
    direction="vertical"
  >
    <el-main>
      <div class="form-container2">
        <el-form
          ref="materialForm"
          :disabled="isReadOnly"
          :model="materialForm"
          label-width="80px"
          label-position="top"
          class="form-fill-style"
          :rules="rules"
        >
          <el-collapse
            v-model="activeDims"
            class="tab-form-style"
          >
            <!-- 物料参数 -->
            <el-collapse-item
              :title="$t('dataConfMod.materialParams')"
              name="2"
            >
              <srm-row>
                <srm-col>
                  <!-- 物料编码 -->
                  <el-form-item
                    :label="$t('materialMainData.materialCode')"
                    prop="materialCode"
                  >
                    <el-input v-model="materialForm.materialCode" />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 物料名称 -->
                  <el-form-item
                    :label="$t('common.materialName')"
                    prop="materialName"
                  >
                    <el-input v-model="materialForm.materialName" />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 创建人 -->
                  <el-form-item
                    :label="$t('common.creator')"
                    prop="createdBy"
                  >
                    <el-input
                      v-model="materialForm.createdBy"
                      disabled
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 创建时间 -->
                  <el-form-item
                    :label="$t('common.creationTime')"
                    prop="creationDate"
                  >
                    <el-date-picker
                      v-model="materialForm.creationDate"
                      :format="$formatDatePickerTime"
                      value-format="yyyy-MM-dd HH:mm:ss"
                      disabled
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 品类 -->
                  <el-form-item
                    :label="$t('dataConfMod.category')"
                    prop="categoryName"
                  >
                    <CCategorySelect
                      v-model="materialForm.categoryName"
                      :scope="materialForm"
                      :placeholder="$t('common.pleaseSelect')"
                      show-key="categoryName"
                      @select="comfirmSelect"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 单位 -->
                  <el-form-item
                    :label="$t('dataConfMod.unit')"
                    prop="unit"
                  >
                    <dict-select
                      v-model="materialForm.unit"
                      code="unit"
                      @change-value="taxKeyChange"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item
                    :label="$t('dataConfMod.quotaManagementType')"
                    prop="quotaManagementType"
                  >
                    <el-select
                      v-model="materialForm.quotaManagementType"
                      filterable
                      :placeholder="$t('dataConfMod.quotaManagementType')"
                      @change="val => changeQuotaType(val, materialForm)"
                    >
                      <el-option
                        v-for="item in quotaManagementTypeList"
                        :key="item.id"
                        :label="item.label"
                        :value="item.value"
                      />
                    </el-select>
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 最小起订量 -->
                  <el-form-item
                    :label="$t('dataConfMod.orderQuantityMinimum')"
                    prop="orderQuantityMinimum"
                  >
                    <el-input
                      v-model="materialForm.orderQuantityMinimum"
                      :placeholder="$t('common.pleaseInput')"
                      type="number"
                    />
                  </el-form-item>
                </srm-col>

                <srm-col>
                  <!-- 内箱最大包装数量 -->
                  <el-form-item
                    :label="$t('dataConfMod.MtInnerboxMinPackagingQuantity')"
                    prop="minimumPackagingQuantity"
                  >
                    <el-input
                      v-model="materialForm.minimumPackagingQuantity"
                      :placeholder="$t('common.pleaseInput')"
                      type="number"
                    />
                  </el-form-item>
                </srm-col>

                <srm-col>
                  <!-- 最小安全库存 -->
                  <el-form-item
                    :label="$t('dataConfMod.minimumSafetyInventory')"
                    prop="minimumSafetyInventory"
                  >
                    <el-input
                      v-model="materialForm.minimumSafetyInventory"
                      :placeholder="$t('common.pleaseInput')"
                      type="number"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 外箱最大包装数量 -->
                  <el-form-item
                    :label="$t('dataConfMod.MtOutboxMinPackagingQuantity')"
                    prop="outboxMinPackagingQuantity"
                  >
                    <el-input
                      v-model="materialForm.outboxMinPackagingQuantity "
                      :placeholder="$t('common.pleaseInput')"
                      type="number"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 品牌 -->
                  <el-form-item
                    :label="$t('dataConfMod.band')"
                    prop="brand"
                  >
                    <el-input v-model="materialForm.brand" />
                  </el-form-item>
                </srm-col>
                <!-- 委外属性 -->
                <srm-col>
                  <el-form-item
                    :label="$t('dataConfMod.purchasingAttributes')"
                    prop="materialAttr"
                  >
                    <el-select
                      v-model="materialForm.materialAttr"
                      filterable
                      @change="val => changeQuotaType(val, materialForm)"
                    >
                      <el-option
                        v-for="item in materialAttrList"
                        :key="item.id"
                        :label="item.label"
                        :value="item.value"
                      />
                    </el-select>
                  </el-form-item>
                </srm-col>

                <!-- 采购周期（天） -->
                <srm-col>
                  <el-form-item
                    :label="$t('dataConfMod.purchasingCycle')"
                    prop="purchaseCycle"
                  >
                    <el-input
                      v-model="materialForm.purchaseCycle"
                      :placeholder="$t('common.pleaseInput')"
                      type="number"
                    />
                  </el-form-item>
                </srm-col>
              </srm-row>
            </el-collapse-item>
          </el-collapse>
        </el-form>
      </div>
      <CToolbar>
        <template slot="right">
          <el-button @click="goBack">
            {{ $t("common.cancel") }}
          </el-button>
          <el-button
            v-if="curOpt === 'edit' || curOpt === 'add'"
            type="primary"
            :loading="saveLoading"
            @click="itemDataSave"
          >
            {{ $t("common.save") }}
          </el-button>
          <el-button
            v-if="
              curOpt === 'edit' &&
                userType === 'VENDOR' &&
                materialForm.ceeaMaterialStatus === 'NOTIFIED'
            "
            type="primary"
            :loading="submitLoading"
            @click="itemDataSubmit"
          >
            {{ $t("common.submit") }}
          </el-button>
        </template>
      </CToolbar>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import CToolbar from 'lib@/components/c-toolbar'
import { getDictItem } from '@/api/common'
import { adaptDictData } from '@/utils'
import CCategorySelect from 'lib@/components/c-category-select'
import { commonApi } from 'mod@/common/baseSettingCommon/api'
export default {
  name: 'PurchaseCatalogDetailNew',
  components: {
    CToolbar,
    CCategorySelect
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      saveLoading: false,
      submitLoading: false,
      itemImagesData: [],
      currentBase64: [],
      fileList: [],
      dialogImageUrl: '',
      dialogVisible: false,
      disabled: false,
      materialId: null,
      queryParam: {},
      itemImgUrl: '',
      curOpt: 'edit',
      userType: this.$store.getters.userType, // 用户类型 VENDOR | BUYER
      editorInstance: null,
      queryTotal: -1,
      viewSize: 10,
      viewIndex: 1,
      materialForm: {
        materialCode: '',
        materialName: '',
        createdBy: '',
        creationDate: '',
        categoryId: '',
        categoryCode: '',
        categoryName: '',
        unit: '',
        quotaManagementType: null,
        materialAttr: null,
        miniSplit: null,
        maxAllocation: null,
        unitName: '',
        ceeaWeight: '',
        ceeaSize: '',
        specification: '',
        orderQuantityMinimum: '',
        ceeaDeliveryCycle: '',
        ceeaBrand: '',
        ceeaColor: '',
        ceeaNames: '',
        ceeaTelephones: '',
        ceeaEmails: '',
        ceeaSecondNames: '',
        ceeaSecondTelephones: '',
        ceeaSecondEmails: '',
        ceeaTexture: '',
        ceeaUsage: '',
        struct: '',
        ceeaRichText: '',
        ceeaMaterialStatus: '',
        minimumPackagingQuantity: null
      },
      yesNoOptions: [
        { value: 'Y', label: this.$t('common.yes') },
        { value: 'N', label: this.$t('common.no') }
      ],
      dialogFormVisible: false,
      statusList: [],
      mulSelection: [],
      fileuploadList: [],
      materialFormBuDTOList: [],
      materialFormPreinstallList: [],
      materialFormRestrictionsList: [],
      agreementRatioList: [],
      priceStandardList: [],
      materialFormRebateList: [],
      displayPaymentPlanLines: [],
      activeDims: ['1', '2', '3', '4', '5', '6', '7', '8'],
      rules: {
        materialCode: [
          {
            required: true,
            message: this.$t('dataConfMod.msgInputItemCode') // 请输入物料编码
          }
        ],
        materialName: [
          {
            required: true,
            message: this.$t('dataConfMod.msgInputItemName') // 请输入物料名称
          }
        ],
        materialAttr: [
          {
            required: false,
            message: this.$t('dataConfMod.materialAttr') // 请选择配额管理类型
          }
        ],
        categoryName: [
          {
            required: true,
            message: this.$t('dataConfMod.msgInputCate') // 请输入品类
          }
        ],
        unit: [
          {
            required: true,
            message: this.$t('dataConfMod.msgInputUnit') //  请选择单位
          }
        ],
        costType: [
          {
            required: true,
            message: this.$t('dataConfMod.msgInputCostType') // 请选择成本类型
          }
        ]
      },
      isReadOnly: false,
      formLabelWidth: '120px',
      isModify: false,
      numList: [{ label: 1, value: 1 }],
      symbolTypeList: [],
      proportionList: [],
      quotaManagementTypeList: [],
      materialAttrList: [],
      sourceTypeList2obj: {},
      payTermsList: [],
      payTermsList2obj: {}
    }
  },

  created () {
    getDictItem('SYMBOL_TYPE').then(res => {
      this.symbolTypeList = adaptDictData(res.data, 'dict')
    })
    getDictItem('PAYMENT_TERMS').then(res => {
      this.payTermsList = adaptDictData(res.data, 'dict')
      for (const v of this.payTermsList) {
        this.payTermsList2obj[v.value] = v.label
      }
    })
    // 状态
    getDictItem('materialForm_STATUS').then(res => {
      this.statusList = adaptDictData(res.data, 'dict')
    })
    getDictItem('PAYMENT_METHOD').then(res => {
      this.proportionList = adaptDictData(res.data, 'dict')
    })
    getDictItem('QUOTA_MANAGEMENT_TYPE').then(res => {
      this.quotaManagementTypeList = adaptDictData(res.data, 'dict')
    })
    getDictItem('MATERIAL_ATTR').then(res => {
      this.materialAttrList = adaptDictData(res.data, 'dict')
    })
    this.curOpt = this.$attrs.params.flag
    this.isReadOnly = this.$attrs.params.flag === 'view'
    if (this.$attrs.params.flag == 'edit' || this.$attrs.params.flag == 'view') {
      this.materialId = this.$attrs.params.materialId
      this.queryParam = this.$attrs.params.queryParam // 加入购物车接口调用所需参数
      this.getFormDetail()
    }
  },
  methods: {
    changeQuotaType (val, row) {
      if (val == 'QUOTA_ACHIEVEMENT_RATE') {
        row.miniSplit = null
      } else if (val == 'FIXED_RATIO' || val == 'COMPREHENSIVE_RATIO') {
        row.maxAllocation = null
      }
    },
    loadJsonFromFile (file, fileList) {
      // 判定进来的必须是图片类型
      if (!/image\/\w+/.test(file.raw.type)) {
        // 抱歉，你的浏览器不支持 FileReader，不能将图片转换为Base64，请使用现代浏览器操作！
        this.$message.error(this.$t('dataConfMod.msgMakeSurePicType'))
        fileList.pop()
        return false
      }

      if (typeof FileReader === 'undefined') {
        this.$message.error(this.$t('dataConfMod.msgNotSupport'))
      } else {
        try {
          let reader = new FileReader()
          reader.onload = () => {
            const obj = {
              materialId: this.materialId,
              imageContent: reader.result,
              url: reader.result
            }
            this.fileList.push(obj)
          }
          reader.readAsDataURL(file.raw)
        } catch (e) {
          this.$message.error(
            // "图片转Base64出错啦！"
            this.$t('dataConfMod.msgBase64Error') + e.toString()
          )
        }
      }
    },
    // 确认选中的品类
    comfirmSelect (node, scope) {
      scope.categoryId = node ? node.categoryId : null
      scope.categoryCode = node ? node.categoryCode : ''
      scope.categoryName = node ? node.categoryName : ''
      scope.struct = node ? node.struct : ''
      scope.categoryFullName = node ? node.categoryFullName : ''
    },
    getFormDetail () {
      const id = this.materialId
      commonApi.materialItemGet({ id }).then(res => {
        if (res.data) {
          res.data.itemImages = res.data.itemImages || []
          res.data.itemImages.forEach(item => {
            this.itemImagesData.push({
              materialId: item.materialId,
              url: item.imageContent,
              imageContent: item.imageContent
            })
          })
          this.fileList = this.itemImagesData
          this.materialForm = res.data.materialItem
          this.materialFormPreinstallList = res.data.matFiles
          this.itemImgUrl =
            '/api-file/files-anon/download-image?fileuploadId=' +
            this.materialForm.materialPictureFileId
        }
      })
    },
    deleteOneContent3 (index) {
      this.materialFormPreinstallList.splice(index, 1)
    },
    // 保存
    itemDataSave () {
      const submitData = {
        itemImages: this.fileList,
        materialItem: this.materialForm,
        matFiles: this.materialFormPreinstallList
      }
      this.$refs.materialForm.validate(async valid => {
        if (!valid) {
          this.$message.error(this.$t('common.pleasefinishRequired')) // '请输入必填项'
          return false
        }
        this.saveLoading = true
        try {
          let res = await commonApi.materialItemModify(submitData)
          if (res) {
            this.saveLoading = false
            this.$message({ type: 'success', message: res.message })
            this.goBack()
            this.__setTabTodo('MaterialMaintenanceBuyer.getQuerydata')
          }
        } catch (error) {
          console.log(error)
        }
        this.saveLoading = false
      })
    },
    taxKeyChange (value, dictItem, scope) {
      this.materialForm.unitName = dictItem.label
    },
    // 提交
    itemDataSubmit () {
      this.submitLoading = true
      this.materialForm.ceeaMaterialStatus = 'MAINTAINED'
      const submitData = {
        materialItem: this.materialForm,
        matFiles: this.materialFormPreinstallList
      }
      commonApi.materialItemModify(submitData).then(res => {
        if (res) {
          this.submitLoading = false
          this.$message({ type: 'success', message: res.message })
          this.getFormDetail()
        }
      })
    },
    goBack () {
      this.$emit('tab-remove', this.$attrs.params.tabName)
    }
  }
}
</script>
<style scoped lang="scss">
.el-main {
  overflow-y: auto !important;
}
.the-purchaseCatalogDetail-detail {
  .form-container2 {
    padding: 5px;
  }
  .sub_header {
    padding: 4px 11px;
    background: #eee;
  }
  .el-table .el-date-editor {
    width: 135px;
  }
  .btn_line {
    margin: 0 0 8px 0;
  }
  .itemBtnDiv {
    padding: 10px;
  }
  .itemPic {
    height: 240px;
    overflow: hidden;
    img {
      height: auto;
      max-height: 100%;
    }
  }
  .el-upload--picture-card {
    width: 100px;
    height: 100px;
  }
  .el-upload {
    width: 100px;
    height: 100px;
    line-height: 100px;
  }
  .el-upload-list--picture-card .el-upload-list__item {
    width: 100px;
    height: 100px;
    line-height: 100px;
  }
  .el-upload-list--picture-card .el-upload-list__item-thumbnail {
    width: 100px;
    height: 100px;
    line-height: 100px;
  }
  .avatar {
    width: 100px;
    height: 100px;
  }
}

</style>
