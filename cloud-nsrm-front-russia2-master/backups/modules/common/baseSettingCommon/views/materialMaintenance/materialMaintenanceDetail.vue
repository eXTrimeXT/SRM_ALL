<template>
  <el-container class="flex-container the-materialMaintenanceDetail-detail" direction="vertical">
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
          <el-collapse v-model="activeDims" class="tab-form-style">
            <!-- 物料图片 -->
            <el-collapse-item :title="$t('dataConfMod.materialPicture')" name="1">
              <div style="padding:5px">
                <SrmCommonFile
                  :extra-data="fileInfo"
                  :default-file="{
                    fileId: materialForm.materialPictureFileId,
                    fileName: materialForm.materialPictureName
                  }"
                  :readonly="false"
                  @on-change="({file}) => HandleFormUploadSuccess(file)"
                />
              </div>
              <div class="itemPic">
                <img v-if="materialForm.materialPictureFileId" :src="itemImgUrl" alt="img">
                <img v-else :src="imgPlaceHolder" alt="">
              </div>
            </el-collapse-item>
            <!-- 物料参数 -->
            <el-collapse-item :title="$t('dataConfMod.materialParams')" name="2">
              <srm-row>
                <srm-col>
                  <!-- 物料编码 -->
                  <el-form-item :label="$t('materialMainData.materialCode')" prop="materialCode">
                    <el-input v-model="materialForm.materialCode" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 物料描述 -->
                  <el-form-item :label="$t('materialMainData.materialDesc')" prop="materialName">
                    <el-input v-model="materialForm.materialName" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 创建人 -->
                  <el-form-item :label="$t('common.creator')" prop="createdBy">
                    <el-input v-model="materialForm.createdBy" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 创建时间 -->
                  <el-form-item :label="$t('common.creationTime')" prop="creationDate">
                    <el-input v-model="materialForm.creationDate" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 品类 -->
                  <el-form-item :label="$t('dataConfMod.category')" prop="categoryName">
                    <el-input v-model="materialForm.categoryName" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 单位 -->
                  <el-form-item :label="$t('dataConfMod.unit')" prop="unit">
                    <dict-select v-model="materialForm.unit" disabled code="unit" />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 重量 -->
                  <el-form-item :label="$t('common.weight')" prop="ceeaWeight">
                    <el-input v-model="materialForm.ceeaWeight" />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 尺寸 -->
                  <el-form-item :label="$t('common.size')" prop="ceeaSize">
                    <el-input v-model="materialForm.ceeaSize" />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 规格/型号 -->
                  <el-form-item :label="$t('common.specification')" prop="specification">
                    <el-input v-model="materialForm.specification" />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 最小起订量 -->
                  <el-form-item :label="最小起订量1232" prop="ceeaOrderQuantityMinimum">
                    <el-input v-model="materialForm.ceeaOrderQuantityMinimum" />
                  </el-form-item>
                </srm-col>

                <srm-col>
                  <!-- 最小包装量 -->
                  <el-form-item :label="内箱最小包装量" prop="minimumPackagingQuantity">
                    <el-input v-model="materialForm.minimumPackagingQuantity" />
                  </el-form-item>
                </srm-col>

                <srm-col>
                  <!-- 最小包装量 -->
                  <el-form-item :label="外箱最小包装量1" prop="outboxMinPackagingQuantity">
                    <el-input v-model="materialForm.outboxMinPackagingQuantity" />
                  </el-form-item>
                </srm-col>

                <srm-col>
                  <!-- 送货周期 -->
                  <el-form-item :label="$t('dataConfMod.deliveryCycle')" prop="ceeaDeliveryCycle">
                    <el-input v-model="materialForm.ceeaDeliveryCycle" />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 品牌 -->
                  <el-form-item :label="$t('dataConfMod.band')" prop="ceeaBrand">
                    <el-input v-model="materialForm.ceeaBrand" />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 颜色 -->
                  <el-form-item :label="$t('dataConfMod.ceeaColor')" prop="ceeaColor">
                    <el-input v-model="materialForm.ceeaColor" />
                  </el-form-item>
                </srm-col>
              </srm-row>
            </el-collapse-item>
            <!-- 供方联系人信息 -->
            <el-collapse-item :title="$t('dataConfMod.supplierContactInfo')" name="3">
              <srm-row>
                <srm-col>
                  <!-- 姓名 -->
                  <el-form-item :label="$t('dataConfMod.userName')" prop="ceeaNames">
                    <el-input v-model="materialForm.ceeaNames" />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 电话 -->
                  <el-form-item :label="$t('common.phone')" prop="ceeaTelephones">
                    <el-input v-model="materialForm.ceeaTelephones" />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 邮箱 -->
                  <el-form-item :label="$t('common.email')" prop="ceeaEmails">
                    <el-input v-model="materialForm.ceeaEmails" />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 姓名 -->
                  <el-form-item :label="$t('dataConfMod.userName')" prop="ceeaSecondNames">
                    <el-input v-model="materialForm.ceeaSecondNames" />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 电话 -->
                  <el-form-item :label="$t('common.phone')" prop="ceeaSecondTelephones">
                    <el-input v-model="materialForm.ceeaSecondTelephones" />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 邮箱 -->
                  <el-form-item :label="$t('common.email')" prop="ceeaSecondEmails">
                    <el-input v-model="materialForm.ceeaSecondEmails" />
                  </el-form-item>
                </srm-col>
              </srm-row>
            </el-collapse-item>
            <!-- 附件 -->
            <el-collapse-item :title="$t('dataConfMod.attachment')" name="3">
              <div class="btn_line">
                <el-button type="primary" class="detail-pbtn" @click="addFile">
                  {{ $t("common.add") }}
                </el-button>
              </div>
              <el-table
                :data="materialFormPreinstallList"
                style="width: 100%"
                border
                max-height="200"
              >
                <el-table-column
                  align="center"
                  type="index"
                  :label="$t('purSettlementMod.tabindex')"
                  width="50"
                />
                <el-table-column
                  align="center"
                  prop="fileSourceName"
                  :label="$t('bidMod.fileName')"
                >
                  <template slot-scope="scope">
                    <SrmCommonFile
                      :extra-data="fileInfo"
                      :default-file="{
                        fileId: scope.row.fileuploadId,
                        fileName: scope.row.fileSourceName
                      }"
                      :readonly="false"
                      @on-change="({file}) => HandleUploadSuccess(file,scope)"
                    />
                  </template>
                </el-table-column>
                <!-- 说明 -->
                <el-table-column
                  align="center"
                  prop="remark"
                  :label="$t('vendorMod.operationMemo')"
                  :show-overflow-tooltip="true"
                />
                <el-table-column :label="$t('common.operation')" width="100">
                  <template slot-scope="scope">
                    <el-button type="text" @click="deleteOneContent3(scope.$index, scope.row)">
                      {{ $t("common.delete") }}
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-collapse-item>
            <!-- 备件详情 -->
            <el-collapse-item :title="$t('dataConfMod.sparePartDetail')" name="4">
              <srm-row>
                <srm-col>
                  <!-- 材质 -->
                  <el-form-item :label="$t('dataConfMod.texture')" prop="ceeaTexture">
                    <el-input v-model="materialForm.ceeaTexture" />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 使用用途 -->
                  <el-form-item :label="$t('dataConfMod.usage')" prop="ceeaUsage">
                    <el-input v-model="materialForm.ceeaUsage" />
                  </el-form-item>
                </srm-col>
              </srm-row>
              <div>
                <el-upload
                  action="#"
                  list-type="picture-card"
                  :auto-upload="false"
                  :file-list="fileList"
                  :on-change="loadJsonFromFile"
                >
                  <em slot="default" class="el-icon-plus" />
                  <div slot="file" slot-scope="{ file }">
                    <img class="el-upload-list__item-thumbnail" :src="file.url" alt="">
                    <span class="el-upload-list__item-actions">
                      <span
                        class="el-upload-list__item-preview"
                        @click="handlePictureCardPreview(file)"
                      >
                        <em class="el-icon-zoom-in" />
                      </span>
                      <span class="el-upload-list__item-delete" @click="handleDownload(file)">
                        <em class="el-icon-download" />
                      </span>
                      <span class="el-upload-list__item-delete" @click="handleRemoveData(file)">
                        <em class="el-icon-delete" />
                      </span>
                    </span>
                  </div>
                </el-upload>
                <srm-dialog :visible.sync="dialogVisible">
                  <img width="100%" :src="dialogImageUrl" alt="">
                </srm-dialog>
              </div>
              <div class="itemDetailEdit">
                <Tinymce
                  id="materialMaintenanceTinymce"
                  v-model="materialForm.ceeaRichText"
                  :height="460"
                  @setup="ready"
                />
              </div>
            </el-collapse-item>
          </el-collapse>
        </el-form>
      </div>
      <CToolbar>
        <template slot="right">
          <el-button @click="goBack">
            {{ $t("common.cancel") }}
          </el-button>
          <!-- 加入购物车 -->
          <el-button
            v-if="curOpt === 'view' && userType === 'BUYER'"
            type="primary"
            @click="addShoppingCart"
          >
            {{ $t("common.addShoppingCart") }}
          </el-button>
          <el-button v-if="curOpt === 'edit'" type="primary" @click="itemDataSave">
            {{ $t("common.staging") }}
          </el-button>
          <el-button
            v-if="
              curOpt === 'edit' &&
                userType === 'VENDOR' &&
                materialForm.ceeaMaterialStatus === 'NOTIFIED'
            "
            type="primary"
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
import Tinymce from '@/components/Tinymce'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import CToolbar from 'lib@/components/c-toolbar'
import { downloadFileLink } from 'lib@/utils/file'
import { getDictItem } from '@/api/common'
import { adaptDictData } from '@/utils'
const imgPlaceHolder = '/assets/images/placeholder.png'

export default {
  name: 'MaterialMaintenanceDetail',
  components: {
    CToolbar,
    Tinymce
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      dialogVisible: false,
      fileList: [],
      dialogImageUrl: '',
      // 文件上传配置信息
      fileInfo: {
        fileModular: 'sup', // 文件所属模块 -》基础模块
        fileFunction: 'inquiry', // 文件所属功能
        fileType: 'images' // 文件所属类型
      },
      materialId: null,
      queryParam: {},
      imgPlaceHolder: imgPlaceHolder,
      itemImgUrl: '',
      curOpt: 'edit',
      userType: this.$store.getters.userType, // 用户类型 VENDOR | BUYER
      editorInstance: null,
      queryTotal: -1,
      viewSize: 10,
      viewIndex: 1,
      materialForm: {
        priceLibraryId: null,
        materialSecondaryId: null,
        materialId: '',
        materialCode: '',
        materialName: '',
        createdBy: '',
        creationDate: '',
        categoryId: '',
        categoryCode: '',
        categoryName: '',
        unit: '',
        unitName: '',
        ceeaWeight: '',
        ceeaSize: '',
        specification: '',
        ceeaOrderQuantityMinimum: '',
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
        ceeaRichText: '',
        ceeaMaterialStatus: '',
        materialPictureFileId: null,
        materialPictureName: null,
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
        vendorCode: [
          {
            required: true,
            message: this.$t('vendorMod.msgVendor') // 请选择供应商
          }
        ],
        orgId: [
          { required: true, message: this.$t('dataConfMod.msgPleaseSelectOrg') }
        ], // 请选择业务实体
        businessType: [
          { required: true, message: this.$t('dataConfMod.msgBusinessType') }
        ], // 请选择业务类型
        contractNum: [
          { required: true, message: this.$t('dataConfMod.msgSelContract') }
        ], // 请选择合同编号
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
      for (let v of this.payTermsList) {
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
    this.curOpt = this.$attrs.params.flag
    this.isReadOnly = this.$attrs.params.flag === 'view'
    if (this.$attrs.params.flag == 'edit' || this.$attrs.params.flag == 'view') {
      this.materialId = this.$attrs.params.materialId
      this.queryParam = this.$attrs.params.queryParam // 加入购物车接口调用所需参数
      this.getFormDetail()
    }
  },
  methods: {
    loadJsonFromFile (file, fileList) {
      // 判定进来的必须是图片类型
      if (!/image\/\w+/.test(file.raw.type)) {
        // 请确保文件为图像类型
        this.$message.error(this.$t('dataConfMod.msgMakeSurePicType'))
        fileList.pop()
        return false
      }

      if (typeof FileReader === 'undefined') {
        // 抱歉，你的浏览器不支持 FileReader，不能将图片转换为Base64，请使用现代浏览器操作！
        this.$message.error(this.$t('dataConfMod.msgNotSupport'))
      } else {
        try {
          let reader = new FileReader()
          reader.onload = () => {
            let obj = {
              materialSecondaryId: this.materialForm.materialSecondaryId,
              imageContent: reader.result,
              url: reader.result
            }
            this.fileList.push(obj)
          }
          reader.readAsDataURL(file.raw)
        } catch (e) {
          this.$message.error(
            this.$t('dataConfMod.msgBase64Error') + e.toString()
          )
        }
      }
    },
    handleRemoveData (file) {
      let fileList = this.fileList.filter(item => {
        return item.uid != file.uid
      })
      this.fileList = fileList
    },
    handlePictureCardPreview (file) {
      this.dialogImageUrl = file.url
      this.dialogVisible = true
    },
    handleDownload (file) {
      downloadFileLink(`${file.url}`).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail'))
      })
    },
    // 富文本编辑框
    ready (editorInstance) {
      const { flag } = this.$attrs.params
      if (flag === 'view') {
       editorInstance.setMode('readonly')
      }
      this.editorInstance = editorInstance
    },
    // 确认选中的品类
    comfirmSelect (node, scope) {
      scope.categoryId = node ? node.categoryId : null
      scope.categoryCode = node ? node.categoryCode : ''
      scope.categoryName = node ? node.categoryName : ''
      scope.categoryFullName = node ? node.categoryFullName : ''
    },
    // 加入购物车
    addShoppingCart () {
      let param = {
        materialId: this.materialId,
        ceeaOrgId: this.queryParam.ceeaOrgId, // 加入购物车接口调用所需参数
        ceeaOrganizationId: this.queryParam.ceeaOrganizationId, // 加入购物车接口调用所需参数
        ifCatalogMaterial: this.queryParam.ifCatalogMaterial, // 加入购物车接口调用所需参数
        categoryName: this.queryParam.categoryName // 加入购物车接口调用所需参数
      }
      this.$api.shop.purCat.ceeaAddToShoppingCart(param).then(res => {
        if (res.data) {
          this.$message({
            type: 'success',
            message: res.data ? res.data : res.message
          })
        }
      })
    },
    getFormDetail () {
      this.$http({
        url: '/api-base/base/material-item-sec/getByParam',
        method: 'POST',
        data: {
          materialCode: this.$attrs.params.row.itemCode,
          materialName: this.$attrs.params.row.itemDesc,
          ceeaSupplierId: this.$attrs.params.row.vendorId
        },
        loading: true
      })
        .then(res => {
          this.materialForm = res.data.materialItem || {}
          this.materialFormPreinstallList = res.data.matFiles
          this.itemImgUrl =
            '/api-file/files-anon/download-image?fileuploadId=' +
            this.materialForm.materialPictureFileId
          let itemSceImages = res.data.itemSceImages || []
          let itemSceImagesData = []
          itemSceImages.forEach(item => {
            itemSceImagesData.push({
              materialId: item.materialId,
              url: item.imageContent,
              imageContent: item.imageContent
            })
          })
          this.fileList = itemSceImagesData
          if (!res.data.materialItem) {
            this.materialForm.priceLibraryId = this.$attrs.params.row.priceLibraryId
            this.materialForm.materialId = this.$attrs.params.row.itemId
            this.materialForm.materialCode = this.$attrs.params.row.itemCode
            this.materialForm.materialName = this.$attrs.params.row.itemDesc
            this.materialForm.categoryId = this.$attrs.params.row.categoryId
            this.materialForm.categoryName = this.$attrs.params.row.categoryName
            this.materialForm.unitName = this.$attrs.params.row.unit
            this.materialForm.unit = this.$attrs.params.row.unit
          }
          // 给供应商的信息带过来
          this.materialForm.ceeaSupplierId = this.$attrs.params.row.vendorId
          this.materialForm.ceeaSupplierCode = this.$attrs.params.row.vendorCode
          this.materialForm.ceeaSupplierName = this.$attrs.params.row.vendorName
          if (!this.materialForm.materialPictureFileId) {
            this.$set(this.materialForm, 'materialPictureFileId', null)
            this.$set(this.materialForm, 'materialPictureName', null)
          }
          this.itemImgUrl =
            '/api-file/files-anon/download-image?fileuploadId=' +
            this.materialForm.materialPictureFileId
        })
    },
    deleteOneContent3 (index, row) {
      if (row.fileuploadId) {
        this.$http({
          url: '/api-file/file/fileupload/delete',
          method: 'POST',
          params: { id: row.fileuploadId },
          loading: true
        }).then(() => {
          this.materialFormPreinstallList.splice(index, 1)
        })
      } else {
        this.materialFormPreinstallList.splice(index, 1)
      }
    },
    // 保存
    itemDataSave () {
      let url = '/api-base/base/material-item-sec/modify'
      if (!this.materialForm.materialSecondaryId) {
        url = '/api-base/base/material-item-sec/add'
      }
      this.$http({
        url: url,
        method: 'POST',
        data: {
          itemSceImages: this.fileList,
          materialItemSec: this.materialForm,
          fileuploadList: this.materialFormPreinstallList
        },
        loading: true
      })
        .then(res => {
          this.$message({ type: 'success', message: res.message })
          this.goBack()
        })
    },
    // 提交
    itemDataSubmit () {
      this.materialForm.ceeaMaterialStatus = 'MAINTAINED'
      let submitData = {
        materialItem: this.materialForm,
        matFiles: this.materialFormPreinstallList
      }
      this.$api.shop.purCat.materialItemModify(submitData).then(res => {
        if (res) {
          this.$message({ type: 'success', message: res.message })
          this.getFormDetail()
        }
      })
    },
    goBack () {
      this.$emit('tab-remove', this.$attrs.params.tabName)
    },
    HandleUploadSuccess (file, scope) {
      const { fileId = '', fileName = '' } = file || {}
      scope.row.fileuploadId = fileId.toString()
      scope.row.fileSourceName = fileName
    },
    HandleFormUploadSuccess (file) {
      const { fileId = '', fileName = '' } = file || {}
      this.materialForm.materialPictureFileId = fileId.toString()
      this.materialForm.materialPictureName = fileName
      this.itemImgUrl =
        '/api-file/files-anon/download-image?fileuploadId=' +
        this.materialForm.materialPictureFileId
    },
    addFile () {
      this.materialFormPreinstallList.push({
        fileuploadId: null,
        fileSourceName: '',
        fileFunction: 'materialMaintenance' // 文件所属功能
      })
    }
  }
}
</script>

<style scoped lang="scss">
.el-main {
  overflow-y: auto !important;
}
:deep(.the-materialMaintenanceDetail-detail) {
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
