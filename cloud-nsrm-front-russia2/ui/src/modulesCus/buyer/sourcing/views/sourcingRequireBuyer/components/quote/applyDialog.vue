<template>
  <!-- <SrmDialog
    title="报名详情"
    size="large"
    destroy-on-close
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
  > -->
  <SrmDialog
    :title="$t('sourcingBuyer.signUpDetail')"
    size="large"
    destroy-on-close
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
  >
    <div style="color:red;margin-bottom:10px">
      <!-- 报名须知：报名前须自行详细查看并判断资质及业绩符合性后，再进行报名，如果不能准确判定可询问报名负责人。 -->
      {{ $t("cusEntry.supplement20250121.beforeRegistrationYouMustCarefullyReviewItYourself") }}
    </div>
    <el-form ref="form" :model="form" :rules="rules" :disabled="editRows.flag === 'view'">
      <el-collapse v-model="activeDims" class="tab-form-style">
        <!-- 基本信息 -->
        <!-- <el-collapse-item title="基础信息" name="1"> -->
        <el-collapse-item :title="$t('common.baseInfo')" name="1">
          <srm-row>
            <!-- 寻源单号 -->
            <srm-col :init-col="4">
              <!-- <el-form-item label="寻源单号"> -->
              <el-form-item :label="$t('bidMod.businessNo')">
                <el-input v-model="form.souReqHead.reqHeadNo" disabled />
              </el-form-item>
            </srm-col>
            <!-- 单据状态 -->
            <srm-col :init-col="4">
              <!-- <el-form-item label="单据状态"> -->
              <el-form-item :label="$t('vendorMod.relegation.documentStatus')">
                <dictSelect v-model="form.souReqHead.status" code="SOU_REQ_HEAD_STATUS" disabled />
              </el-form-item>
            </srm-col>
            <!-- 供应商名称 -->
            <srm-col :init-col="4">
              <!-- <el-form-item label="供应商名称"> -->
              <el-form-item :label="$t('common.companyName')">
                <el-input v-model="editRows.vendorName" disabled />
              </el-form-item>
            </srm-col>
            <!-- 报名状态 -->
            <srm-col :init-col="4">
              <!-- <el-form-item label="报名状态"> -->
              <el-form-item :label="$t('bidMod.signUpStatus')">
                <dictSelect v-model="form.souReqHead.applyStatus" code="SOU_REQ_APPLY_STATUS" disabled />
              </el-form-item>
            </srm-col>
            <!-- 项目名称 -->
            <srm-col :init-col="4">
              <!-- <el-form-item label="项目名称"> -->
              <el-form-item  :label="$t('bidMod.bidingName')">
                <el-input v-model="form.souReqHead.projectName" disabled />
              </el-form-item>
            </srm-col>
            <!-- 项目所在地 -->
            <srm-col :init-col="4">
              <!-- <el-form-item label="项目所在地"> -->
              <el-form-item  :label="$t('cusEntry.supplement20250121.locationOfTheProject')">
                <el-input v-model="form.souReqHead.projectAddress" disabled />
              </el-form-item>
            </srm-col>
            <!-- 是否代理 -->
            <srm-col :init-col="4">
              <!-- <el-form-item label="是否代理" prop="souReqApply.isAgent"> -->
              <el-form-item :label="$t('cusEntry.supplement20250121.whetherToActAsAnAgent')" prop="souReqApply.isAgent">
                <dictSelect v-model="form.souReqApply.isAgent" code="YES_OR_NO" />
              </el-form-item>
            </srm-col>
            <!-- 代理品牌 -->
            <srm-col :init-col="4">
              <!-- <el-form-item label="代理品牌" prop="souReqApply.agentBrand" 
              :rules="[{ required: form.souReqApply.isAgent === 'Y', message: '必填' }]"> -->
              <el-form-item :label="$t('vendorMod.agencyBrand')" prop="souReqApply.agentBrand" 
              :rules="[{ required: form.souReqApply.isAgent === 'Y', message: $t('vendorMod.required') }]">
                <el-input v-model="form.souReqApply.agentBrand" />
              </el-form-item>
            </srm-col>
          </srm-row>
        </el-collapse-item>

        <!-- 供应商报名联系方式 -->
        <!-- <el-collapse-item title="供应商报名联系方式" name="3"> -->
        <el-collapse-item :title="$t('cusEntry.supplement20250121.supplierRegistrationContactInformation')" name="3">
          <srm-row>
            <!-- 供应商报名联系人 -->
            <srm-col :init-col="4">
              <!-- <el-form-item label="供应商报名联系人" prop="souReqApply.applyContactName" :rules="rules.field1"> -->
              <el-form-item :label="$t('cusEntry.supplement20250121.supplierRegistrationContactPerson')" prop="souReqApply.applyContactName" :rules="rules.field1">
                <el-input v-model="form.souReqApply.applyContactName" />
              </el-form-item>
            </srm-col>
            <!--报名人联系方式 -->
            <srm-col :init-col="4">
              <!-- <el-form-item label="报名人联系电话" prop="souReqApply.applyPhone" :rules="rules.field1"> -->
              <el-form-item :label="$t('cusEntry.supplement20250121.contactNumberOfTheApplicant')" prop="souReqApply.applyPhone" :rules="rules.field1">
                <el-input v-model="form.souReqApply.applyPhone" />
              </el-form-item>
            </srm-col>
            <!-- 报名人邮箱 -->
            <srm-col :init-col="4">
              <!-- <el-form-item label="报名人邮箱" prop="souReqApply.applyEmail" :rules="rules.field1"> -->
              <el-form-item :label="$t('cusEntry.supplement20250121.applicantsEmail')" prop="souReqApply.applyEmail" :rules="rules.field1">
                <el-input v-model="form.souReqApply.applyEmail" />
              </el-form-item>
            </srm-col>
            <!-- 报名失败原因 -->
            <srm-col :init-col="1">
              <!-- <el-form-item label="报名失败原因"> -->
              <el-form-item :label="$t('cusEntry.supplement20250121.reasonForRegistrationFailure')">
                <el-input v-model="form.souReqApply.applyFailReason" disabled />
              </el-form-item>
            </srm-col>
          </srm-row>
        </el-collapse-item>

        <!-- 附件信息 -->
        <!-- <el-collapse-item title="附件信息" name="4"> -->
        <el-collapse-item :title="$t('bidMod.fileInfo')" name="4">
          <el-table :data="form.souReqHead.fileUploads" style="width: 100%" border max-height="390px">
            <!-- <el-table-column width="70px" type="index" label="序号" /> -->
            <el-table-column width="70px" type="index" :label="$t('components.common.sort')" />
            <!-- <el-table-column prop="fileuploadId" label="附件名称"> -->
            <el-table-column prop="fileuploadId" :label="$t('common.fileUploadName')">
              <template slot-scope="scope">
                <SrmCommonFile
                  :extra-data="fileInfo"
                  :default-file="{
                    fileId: scope.row.fileuploadId,
                    fileName: scope.row.fileName
                  }"
                  :readonly="true"
                />
              </template>
            </el-table-column>
            <!-- <el-table-column prop="remark" label="备注" /> -->
            <el-table-column prop="remark" :label="$t('components.eio.headers.remark')" />
          </el-table>
        </el-collapse-item>

        <!-- 供方上传附件信息 -->
        <!-- <el-collapse-item title="供方上传附件信息" name="5"> -->
        <el-collapse-item :title="$t('cusEntry.supplement20250121.supplierUploadsAttachmentInformation')" name="5">
          <p>
            <el-button type="primary" @click="form.souReqApply.fileUploads.push({})">
              <!-- 新增 -->
              {{ $t("common.add") }}
            </el-button>
          </p>
          <el-table :data="form.souReqApply.fileUploads" style="width: 100%" border max-height="390px">
            <!-- <el-table-column width="70px" type="index" label="序号" /> -->
            <el-table-column width="70px" type="index" :label="$t('components.common.sort')" />
            <!-- <el-table-column prop="attachmentType" label="附件类型"> -->
            <el-table-column prop="attachmentType" :label="$t('dataConfMod.attachmentType')">
              <template slot-scope="scope">
                <dictSelect v-model="scope.row.attachmentType" code="SCENE_SOU_REQ_APPLY_ATTACHMENT" />
              </template>
            </el-table-column>
            <!-- <el-table-column prop="fileName" label="附件名称"> -->
            <el-table-column prop="fileName" :label="$t('common.fileUploadName')">
              <template slot-scope="scope">
                <SrmCommonFile
                  :extra-data="fileInfo"
                  :default-file="{
                    fileId: scope.row.fileuploadId,
                    fileName: scope.row.fileName
                  }"
                  @on-change="({file}) => handleUploadSuccessTable(file,scope.row)"
                />
              </template>
            </el-table-column>
            <!-- <el-table-column prop="field1" label="备注"> -->
            <el-table-column prop="field1" :label="$t('components.eio.headers.remark')">
              <template slot-scope="scope">
                <el-input v-model="scope.row.remark" />
              </template>
            </el-table-column>
            <!-- <el-table-column label="操作"> -->
            <el-table-column :label="$t('components.eio.headers.operation')">
              <template v-slot="scope">
                <!--删除-->
                <el-button
                  type="text"
                  @click="delRow(scope.$index)"
                >
                  {{ $t("common.delete") }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-collapse-item>
      </el-collapse>
    </el-form>

    <div slot="footer" class="dialog-footer" />
  </SrmDialog>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { transformMQL } from 'lib@/utils/util'
import souHttp from '../../../../api'

export default {
  name: 'InvoiceDialog',
  components: {
    TableView,
    FormWrapper
  },
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    readonly: {
      type: Boolean,
      default: false
    },
    editRows: {
      type: Object,
      default () {
        return {}
      }
    }
  },
  data () {
    return {
      activeDims: ['1', '2', '3', '4', '5'],
      fileInfo: {
        fileModular: 'sourcingSupplier',
        fileFunction: 'sourcingApplicationDetail',
        fileType: 'images'
      },
      rules: {
        // field1: [{ required: true, message: '必填' }]
        field1: [{ required: true, message:  this.$t("vendorMod.required") }]
      },
      form: {
        souReqHead: {
          field1: ''
        },
        souReqApply: {
          applyId: '',
          fileUploads: []
        }
      }
    }
  },
  computed: {
    dialogVisible: {
      get: function () {
        return this.visible
      },
      set: function (val) {
        this.$emit('update:visible', val)
      }
    }
  },
  watch: {
    visible: {
      handler (nVal) {
        if (nVal) {
          this.getFormDetail()
        }
      },
      immediate: true,
      deep: true
    }
  },
  methods: {
    handleUploadSuccess (file) {
      const { fileId = '', fileName = '', fileType = '' } = file || {}
      this.form.souReqApply.depositFileId = fileId.toString()
      this.form.souReqApply.depositFileName = fileName
    },
    handleUploadSuccessTable (file, row) {
      const { fileId = '', fileName = '', fileType = '' } = file || {}
      row.fileuploadId = fileId.toString()
      row.fileName = fileName
      console.log('row:', row)
    },
    delRow (index) {
      this.form.souReqApply.fileUploads.splice(index, 1)
    },
    submit () {
      this.$refs.form.validate(async valid => {
        if (valid) {
          let transfromParams = transformMQL.save(
            'SouReqApply',
            [this.form.souReqApply],
            'apply',
          )
          const response = await soucHttp.applySubmit(transfromParams)
          // this.$message.success('报名成功')
          this.$message.success(this.$t("cusEntry.common.signUpSuccess"))
          this.dialogVisible = false
          this.$emit('fresh')
        } else {
          // this.$message.error('请填写必填项')
          this.$message.error(this.$t("cusEntry.supplement20250121.pleaseFillInTheRequiredFields"))
        }
      })
    },
    async save () {
      console.log('this.form', this.form)
      let transfromParams = transformMQL.save(
        'SouReqApply',
        [this.form.souReqApply],
        'save',
      )
      const response = await soucHttp.save(transfromParams)
      // this.$message.success('暂存成功')
      this.$message.success(this.$t("vendorMod.temporarySuccess"))
      this.dialogVisible = false
    },
    clearFrom () {
      this.form = {
        souReqHead: {
        },
        souReqApply: {
          fileUploads: []
        }
      }
    },
    async getFormDetail () {
      this.clearFrom()
      let reqHeadId = this.editRows.reqHeadId
      let applyId = this.editRows.applyId
      console.log('editRows:', this.editRows)
      let transfromParams = transformMQL.save(
        'SouReqApplyBuyer',
        [{
          reqHeadId,
          applyId
        }],
        'getApplyInfo',
      )
      const response = await souHttp.getApplyInfo(transfromParams)
      console.log('response:', response)
      if (response?.data?.records.length) {
        const { souReqHead, souReqApply } = response.data.records[0]
        console.log('souReqHead', souReqHead)
        if (souReqHead) {
          this.form.souReqHead = souReqHead
          this.form.souReqHead.applyStatus = souReqApply.applyStatus
        }

        if (souReqApply) {
          this.form.souReqApply = souReqApply
        }
        const { companyId, companyCode, companyName } = this.$store.getters.userInfo
        this.form.souReqApply.reqHeadId = reqHeadId
        this.form.souReqApply.vendorId = companyId
        this.form.souReqApply.vendorCode = companyCode
        this.form.souReqApply.vendorName = companyName
        console.log('this.form:', this.form, this.$store.getters)
      }
    }
  }
}
</script>
