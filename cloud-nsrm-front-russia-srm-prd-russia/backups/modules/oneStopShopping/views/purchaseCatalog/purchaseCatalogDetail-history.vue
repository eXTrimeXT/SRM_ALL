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
          class="form-incontainer"
          :rules="rules"
        >
          <el-collapse
            v-model="activeDims"
            class="tab-form-style"
          >
            <el-collapse-item
              :title="$t('dataConfMod.materialPicture')"
              name="1"
            >
              <div class="itemPic">
                <img
                  v-if="materialForm.materialPictureFileId"
                  :src="itemImgUrl"
                >
                <img
                  v-else
                  :src="imgPlaceHolder"
                >
              </div>
            </el-collapse-item>
            <el-collapse-item
              :title="$t('dataConfMod.materialParams')"
              name="2"
            >
              <el-row>
                <el-col :span="6">
                  <el-form-item
                    :label="$t('common.materialCode')"
                    prop="materialCode"
                  >
                    <el-input
                      v-model="materialForm.materialCode"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    :label="$t('materialMainData.materialDesc')"
                    prop="materialName"
                  >
                    <el-input
                      v-model="materialForm.materialName"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    :label="$t('common.creator')"
                    prop="createdUserName"
                  >
                    <el-input
                      v-model="materialForm.createdUserName"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    :label="$t('common.creationTime')"
                    prop="creationDate"
                  >
                    <el-input
                      v-model="materialForm.creationDate"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    :label="$t('common.category')"
                    prop="categoryName"
                  >
                    <el-input
                      v-model="materialForm.categoryName"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    :label="$t('dataConfMod.unit')"
                    prop="unit"
                  >
                    <DictSelect
                      v-model="materialForm.unit"
                      code="unit"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    :label="$t('common.weight')"
                    prop="ceeaWeight"
                  >
                    <el-input v-model="materialForm.ceeaWeight" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    :label="$t('common.size')"
                    prop="ceeaSize"
                  >
                    <el-input v-model="materialForm.ceeaSize" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    :label="$t('common.specification')"
                    prop="specification"
                  >
                    <el-input v-model="materialForm.specification" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    :label="$t('dataConfMod.orderQuantityMinimum')"
                    prop="ceeaOrderQuantityMinimum"
                  >
                    <el-input v-model="materialForm.ceeaOrderQuantityMinimum" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    :label="$t('dataConfMod.deliveryCycle')"
                    prop="ceeaDeliveryCycle"
                  >
                    <el-input v-model="materialForm.ceeaDeliveryCycle" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    :label="$t('dataConfMod.band')"
                    prop="ceeaBrand"
                  >
                    <el-input v-model="materialForm.ceeaBrand" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    :label="$t('dataConfMod.ceeaColor')"
                    prop="ceeaColor"
                  >
                    <el-input v-model="materialForm.ceeaColor" />
                  </el-form-item>
                </el-col>
              </el-row>
            </el-collapse-item>
            <el-collapse-item
              :title="$t('dataConfMod.supplierContactInfo')"
              name="3"
            >
              <el-row>
                <el-col :span="6">
                  <el-form-item
                    :label="$t('dataConfMod.userName')"
                    prop="ceeaNames"
                  >
                    <el-input v-model="materialForm.ceeaNames" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    :label="$t('common.phone')"
                    prop="ceeaTelephones"
                  >
                    <el-input v-model="materialForm.ceeaTelephones" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    :label="$t('common.email')"
                    prop="ceeaEmails"
                  >
                    <el-input v-model="materialForm.ceeaEmails" />
                  </el-form-item>
                </el-col>
              </el-row>
            </el-collapse-item>
            <el-collapse-item
              :title="$t('bidMod.attachment')"
              name="3"
            >
              <div class="btn_line">
                <el-button
                  type="primary"
                  class="detail-pbtn"
                  @click="addFile"
                >
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
                      @on-change="({file}) => HandleUploadSuccess(file, scope)"
                    />
                  </template>
                </el-table-column>
                <el-table-column
                  align="center"
                  prop="remark"
                  :label="$t('supRisk.explanation')"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  :label="$t('common.operation')"
                  width="100"
                >
                  <template slot-scope="scope">
                    <el-button
                      type="text"
                      @click="deleteOneContent3(scope.$index, scope.row)"
                    >
                      {{ $t("common.delete") }}
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-collapse-item>
            <el-collapse-item
              :title="$t('dataConfMod.sparePartDetail')"
              name="4"
            >
              <el-row>
                <el-col :span="6">
                  <el-form-item
                    :label="$t('dataConfMod.texture')"
                    prop="ceeaTexture"
                  >
                    <el-input v-model="materialForm.ceeaTexture" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    :label="$t('dataConfMod.usage')"
                    prop="ceeaUsage"
                  >
                    <el-input v-model="materialForm.ceeaUsage" />
                  </el-form-item>
                </el-col>
              </el-row>
              <div>
                <el-upload
                  action="#"
                  list-type="picture-card"
                  :auto-upload="false"
                  :file-list="fileList"
                  :on-change="loadJsonFromFile"
                >
                  <i
                    slot="default"
                    class="el-icon-plus"
                  />
                  <div
                    slot="file"
                    slot-scope="{ file }"
                  >
                    <img
                      class="el-upload-list__item-thumbnail"
                      :src="file.url"
                      alt=""
                    >
                    <span class="el-upload-list__item-actions">
                      <span
                        class="el-upload-list__item-preview"
                        @click="handlePictureCardPreview(file)"
                      >
                        <i class="el-icon-zoom-in" />
                      </span>
                      <span
                        class="el-upload-list__item-delete"
                        @click="handleDownload(file)"
                      >
                        <i class="el-icon-download" />
                      </span>
                      <span
                        v-if="!isReadOnly"
                        class="el-upload-list__item-delete"
                        @click="handleRemoveData(file)"
                      >
                        <i class="el-icon-delete" />
                      </span>
                    </span>
                  </div>
                </el-upload>
                <srm-dialog :visible.sync="dialogVisible">
                  <img
                    width="100%"
                    :src="dialogImageUrl"
                    alt=""
                  >
                </srm-dialog>
              </div>
              <div class="itemDetailEdit">
                <vue-ueditor-wrap
                  v-model="materialForm.ceeaRichText"
                  mode="observer"
                  :observer-debounce-time="100"
                  :observer-options="{
                    attributes: true,
                    characterData: true,
                    childList: true,
                    subtree: true
                  }"
                  :destroy="true"
                  :config="customConfig"
                  @ready="ready"
                />
              </div>
            </el-collapse-item>
          </el-collapse>
        </el-form>
      </div>
      <c-toolbar>
        <template slot="right">
          <el-button @click="goBack">
            {{ $t("common.close") }}
          </el-button>
          <el-button
            v-if="curOpt==='edit' && userType==='VENDOR' && materialForm.ceeaMaterialStatus==='NOTIFIED'"
            type="primary"
            @click="itemDataSubmit"
          >
            {{ $t("common.submit") }}
          </el-button>
          <el-button
            v-if="curOpt==='edit'"
            type="primary"
            @click="itemDataSave"
          >
            {{ $t("common.staging") }}
          </el-button>
          <el-button
            v-if="curOpt==='view' && userType==='BUYER'"
            type="primary"
            @click="addShoppingCart"
          >
            {{ $t("common.addShoppingCart") }}
          </el-button>
        </template>
      </c-toolbar>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoMixin, tabTodoWatch } from '@/utils/mixins'
import CToolbar from 'lib@/components/c-toolbar'
import VueUeditorWrap from 'vue-ueditor-wrap'

import QuickSearch from 'lib@/components/QuickSearch'
import MainHeader from 'lib@/components/Table/MainHeader'
import CPagination from 'lib@/components/c-pagination'
import OrganizationSelector from 'lib@/components/organization-selector'
import CCategorySelect from 'lib@/components/c-category-select'
import { downloadFileLink } from 'lib@/utils/file'

const imgPlaceHolder = require('@/assets/images/placeholder.png')

export default {
  name: 'PurchaseCatalogDetail',
  components: {
    MainHeader,
    CToolbar,
    QuickSearch,
    CPagination,
    OrganizationSelector,
    VueUeditorWrap,
    CCategorySelect
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      dialogImageUrl: '',
      dialogVisible: false,
      fileList: [],
      fileInfo: {
        fileModular: 'sup',
        fileFunction: 'inquiry',
        fileType: 'images'
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
        ceeaMaterialStatus: ''
      },
      yesNoOptions: [{ value: 'Y', label: this.$t('common.yes') }, { value: 'N', label: this.$t('common.no') }],
      dialogFormVisible: false,
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
        vendorCode: [{ required: true, message: this.$t('quota.vendorTips') }], // 请选择供应商
        orgId: [{ required: true, message: this.$t('quota.orgIdTips') }], // 请选择业务实体
        businessType: [{ required: true, message: this.$t('quota.businessTypeTips') }], // 请选择业务类型
        contractNum: [{ required: true, message: this.$t('quota.contractNumTips') }], // 请选择合同编号
        costType: [{ required: true, message: this.$t('quota.costTypeTips') }]// 请选择成本类型
      },
      isReadOnly: false,
      formLabelWidth: '120px',
      isModify: false,
      numList: [{ label: 1, value: 1 }],
      sourceTypeList2obj: {},
      payTermsList2obj: {},
      customConfig: {
        // 编辑器不自动被内容撑高
        autoHeightEnabled: false,
        // 初始容器高度
        initialFrameHeight: 240,
        // 初始容器宽度
        initialFrameWidth: '100%',
        // 上传文件接口（这个地址是我为了方便各位体验文件上传功能搭建的临时接口，请勿在生产环境使用！！！）
        // serverUrl: "http://35.201.165.105:8000/controller.php",
        // UEditor 资源文件的存放路径，如果你使用的是 vue-cli 生成的项目，通常不需要设置该选项，vue-ueditor-wrap 会自动处理常见的情况，如果需要特殊配置，参考下方的常见问题2
        UEDITOR_HOME_URL: '/UEditor/'
      }
    }
  },

 created () {
    this.curOpt = this.$attrs.params.flag
    this.isReadOnly = this.$attrs.params.flag === 'view'
    if (this.$attrs.params.flag == 'add') {
      // eslint-disable-next-line no-empty
      if (this.$store.getters.userInfo) {}
    } else if (this.$attrs.params.flag == 'edit' || this.$attrs.params.flag == 'view') {
      this.materialId = this.$attrs.params.materialId
      this.queryParam = this.$attrs.params.queryParam // 加入购物车接口调用所需参数
      this.getFormDetail()
    }
  },
  methods: {
      loadJsonFromFile (file, fileList) {
      console.log('fileLisAt', fileList)
      // 判定进来的必须是图片类型
      if (!/image\/\w+/.test(file.raw.type)) {
        this.$message.error(this.$t('dataConfMod.msgMakeSurePicType'))
        fileList.pop()
        return false
      }

      if (typeof FileReader === 'undefined') {
        this.$message.error(this.$t('dataConfMod.msgNotSupport'))
      } else {
        try {
          var reader = new FileReader()
          reader.onload = () => {
            let obj = {
              materialSecondaryId: this.materialForm.materialSecondaryId,
              imageContent: reader.result,
              url: reader.result
            }
            this.fileList.push(obj)
          }
          reader.readAsDataURL(file.raw)
          // });
          console.log('fileListB', this.fileList)
        } catch (e) {
          this.$message.error(this.$t('dataConfMod.msgBase64Error') + e.toString())
        }
      }
    },
    handleRemoveData (file) {
      console.log('handleRemoveData', file)
      let fileList = this.fileList.filter(item => {
        return item.uid != file.uid
      })
      console.log('fileList', fileList)
      this.fileList = fileList
    },
    handlePictureCardPreview (file) {
      this.dialogImageUrl = file.url
      console.log('handlePictureCardPreview', this.dialogImageUrl)
      this.dialogVisible = true
    },
    handleDownload (file) {
      // console.log("handleDownload", file);
      // downloadFileByBase64
        downloadFileLink(`${file.url}`).catch(() => {
          this.$message.error(this.$t('purchaseDemand.downloadFail'))
        })
    },
    // 富文本编辑框
    ready (editorInstance) {
      console.log(editorInstance)
      const { flag, row } = this.$attrs.params
      if (flag === 'view') {
        editorInstance.setDisabled()
      }
      this.editorInstance = editorInstance
      console.log('key: ' + editorInstance.key)

      // if (flag !== "add") {
      //   this.$nextTick(() => {
      //     this.form = cloneDeep(row);
      //     this.initData(row.modelType, true);
      //   });
      // }
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
        categoryName: this.queryParam.categoryName, // 加入购物车接口调用所需参数
        categoryId: this.queryParam.categoryId // 加入购物车接口调用所需参数
      }
      this.$api.shop.purCat.ceeaAddToShoppingCart(param).then(res => {
        if (res.data) {
          this.$message({ type: 'success', message: res.data ? res.data : res.message })
        }
      })
    },
    getFormDetail () {
      /* let id = this.materialId
      this.$api.shop.purCat.materialItemGet({id}).then(res=>{
        if (res.data) {
          this.materialForm = res.data.materialItem;
          this.materialFormPreinstallList = res.data.matFiles;
          this.itemImgUrl = '/api-file/files-anon/download-image?fileuploadId='+this.materialForm.materialPictureFileId;
        }
      }) */
      this.materialForm = this.$attrs.params.row.materialItem
      this.materialFormPreinstallList = this.$attrs.params.row.matFiles
      this.itemImgUrl = '/api-file/files-anon/download-image?fileuploadId=' + this.materialForm.materialPictureFileId
      // console.log("this.$attrs.params",this.$attrs.params)
      let itemSceImages = this.$attrs.params.row.itemSceImages || []
            let itemSceImagesData = []
            itemSceImages.forEach(item => {
                itemSceImagesData.push({
                  materialId: item.materialId,
                  url: item.imageContent,
                  imageContent: item.imageContent
                  })
            })
            this.fileList = itemSceImagesData
        /* this.$http({
          url: "/api-base/base/material-item-sec/getByParam",
          method: "POST",
          data: {
            "materialCode": this.$attrs.params.row.materialCode,
            "materialName": this.$attrs.params.row.materialName,
            "ceeaSupplierId": this.$attrs.params.row.ceeaSupplierId,
          },
          loading: true
        })
        .then(res => {
          if (res.data) {
            this.materialForm = res.data;
            this.itemImgUrl = '/api-file/files-anon/download-image?fileuploadId='+this.materialForm.materialPictureFileId;
          }
        })
        .catch(err => {
          console.log(err);
        }); */
    },
    deleteOneContent3 (index, row) {
      this.materialFormPreinstallList.splice(index, 1)
    },
    // 保存
    itemDataSave () {
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
    // 提交
    itemDataSubmit () {
      // let submitData = this.materialForm
      this.materialForm.ceeaMaterialStatus = 'MAINTAINED'
      let submitData = {
        itemSceImages: this.fileList,
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
      // this.__setTabTodo("SampleConfirmedList.getQuerydata");
    },
    HandleUploadSuccess (file, scope) {
      const { fileId = '', fileName = '' } = file || {}
      scope.row.fileuploadId = fileId.toString()
      scope.row.fileSourceName = fileName
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
.el-main{
    overflow-y: auto !important;
  }
.the-purchaseCatalogDetail-detail {

  .form-container2{padding: 5px;}
  .sub_header {
    padding: 4px 11px;
    background: #eee;
  }
  .el-table .el-date-editor {
    width: 135px;
  }
  .btn_line{margin:0 0 8px 0;}
  .itemBtnDiv{
    padding: 10px;
  }
  .itemPic{
    height: 240px;
    overflow: hidden;
    img{
      height: auto;
      max-height:100%;
    }
  }
}
::v-deep .el-upload--picture-card {
  width: 100px;
  height: 100px;
}
::v-deep .el-upload {
  width: 100px;
  height: 100px;
  line-height: 100px;
}
::v-deep .el-upload-list--picture-card .el-upload-list__item {
  width: 100px;
  height: 100px;
  line-height: 100px;
}
::v-deep .el-upload-list--picture-card .el-upload-list__item-thumbnail {
  width: 100px;
  height: 100px;
  line-height: 100px;
}
::v-deep .avatar {
  width: 100px;
  height: 100px;
}
</style>
