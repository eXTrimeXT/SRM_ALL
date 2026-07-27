<template>
  <SrmDialog
    :title="$t('sourcingBuyer.signUpDetail')"
    size="large"
    destroy-on-close
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
  >
    <div style="color:red;margin-bottom:10px">
      <!-- 报名须知：报名前须自行详细查看并判断资质及业绩符合性后，再进行报名，如果不能准确判定可询问报名负责人。 -->
      {{ $t('cusEntry.supplement20250121.beforeRegistrationYouMustCarefullyReviewItYourself') }}
    </div>
    <el-form
      ref="form"
      label-position="top"
      :model="form"
      :rules="rules"
      :disabled="editRows.flag === 'view'"
    >
      <el-collapse v-model="activeDims" class="tab-form-style">
        <!-- 基本信息 -->
        <el-collapse-item :title="$t('meeting.baseInfo')" name="1">
          <srm-row>
            <!-- 寻源单号 -->
            <srm-col :init-col="4">
              <el-form-item :label="$t('bidMod.businessNo')">
                <el-input v-model="form.souReqHead.reqHeadNo" disabled />
              </el-form-item>
            </srm-col>
            <!-- 单据状态 -->
            <srm-col :init-col="4">
              <el-form-item :label="$t('orderMod.deliveryNoteStatus')">
                <dictSelect v-model="form.souReqHead.status" code="SOU_REQ_HEAD_STATUS" disabled />
              </el-form-item>
            </srm-col>
            <!-- 供应商名称 -->
            <srm-col :init-col="4">
              <el-form-item :label="$t('common.vendorName')">
                <el-input v-model="form.souReqApply.vendorName" disabled />
              </el-form-item>
            </srm-col>
            <!-- 报名状态 -->
            <srm-col :init-col="4">
              <el-form-item :label="$t('sourcingBuyer.applyStatus')">
                <dictSelect v-model="form.souReqHead.applyStatus" code="SOU_REQ_APPLY_STATUS" disabled />
              </el-form-item>
            </srm-col>
            <!-- 项目名称 -->
            <srm-col :init-col="4">
              <el-form-item :label="$t('bidMod.bidingName')">
                <el-input v-model="form.souReqHead.projectName" disabled />
              </el-form-item>
            </srm-col>
            <!-- 项目所在地 -->
            <srm-col :init-col="4">
              <el-form-item :label="$t('cusEntry.supplement20250121.projectAddress')">
                <el-input v-model="form.souReqHead.projectAddress" disabled />
              </el-form-item>
            </srm-col>
            <!-- 是否为代理商 -->
            <srm-col :init-col="4">
              <el-form-item :label="$t('cusEntry.supplement20250205.isAgent')" prop="souReqApply.isAgent">
                <dictSelect v-model="form.souReqApply.isAgent" code="YES_OR_NO" />
              </el-form-item>
            </srm-col>
            <!-- 代理品牌 -->
            <srm-col :init-col="4">
              <el-form-item :label="$t('vendorMod.agencyBrand')" prop="souReqApply.agentBrand" :rules="[{ required: form.souReqApply.isAgent === 'Y', message: $t('vendorMod.required') }]">
                <el-input v-model="form.souReqApply.agentBrand" />
              </el-form-item>
            </srm-col>
          </srm-row>
        </el-collapse-item>

        <!-- 供应商报名联系方式 -->
        <el-collapse-item :title="$t('cusEntry.supplement20250121.supplierRegistrationContactInformation')" name="3">
          <srm-row>
            <!-- 供应商报名联系人 -->
            <srm-col :init-col="4">
              <el-form-item :label="$t('cusEntry.supplement20250121.supplierRegistrationContactPerson')" prop="souReqApply.applyContactName" :rules="rules.field1">
                <el-input v-model="form.souReqApply.applyContactName" />
              </el-form-item>
            </srm-col>
            <!--报名人联系方式 -->
            <srm-col :init-col="4">
              <el-form-item
                :label="$t('cusEntry.supplement20250121.contactNumberOfTheApplicant')"
                prop="souReqApply.applyPhone"
                :rules="[
                  { required: true, message: $t('cusEntry.tipMessage.phoneMsg') },
                  { validator: validatePhoneFn, trigger: ['blur', 'change'] },
                ]"
              >
                <el-input v-model="form.souReqApply.applyPhone" />
              </el-form-item>
            </srm-col>
            <!-- 报名人邮箱 -->
            <srm-col :init-col="4">
              <el-form-item
                :label="$t('cusEntry.supplement20250121.applicantsEmail')"
                prop="souReqApply.applyEmail"
                :rules="[
                  { required: true, message: $t('dataConfMod.msgMail') },
                  { validator: validateEmailFn, trigger: ['blur', 'change'] }
                ]"
              >
                <el-input
                  v-model="form.souReqApply.applyEmail"
                />
              </el-form-item>
            </srm-col>
            <!-- 报名失败原因 -->
            <srm-col :init-col="1">
              <el-form-item :label="$t('cusEntry.supplement20250121.reasonForRegistrationFailure')">
                <el-input v-model="form.souReqApply.applyFailReason" disabled />
              </el-form-item>
            </srm-col>
          </srm-row>
        </el-collapse-item>

        <!-- 附件信息 -->
        <el-collapse-item :title="$t('bidMod.fileInfo')" name="4">
          <el-table :data="form.souReqHead.fileUploads" style="width: 100%" border max-height="390px">
            <el-table-column width="70px" type="index" :label="$t('common.sort')" />
            <el-table-column prop="fileuploadId" :label="$t('bidMod.fileName')">
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
            <el-table-column prop="remark" :label="$t('common.remark')" />
          </el-table>
        </el-collapse-item>

        <!-- 资质业绩证明文件 -->
        <!-- <el-collapse-item title="资质业绩证明文件" name="5">
          <el-table :data="form.souReqApply.fileUploads" style="width: 100%" border max-height="390px">
            <el-table-column width="70px" type="index" label="序号" />
            <el-table-column prop="attachmentType" label="附件类型" :formatter="(row, column, cellValue) => $getDictLabel('SCENE_SOU_REQ_APPLY_ATTACHMENT', cellValue)" />
            <el-table-column prop="fileName" label="附件名称" :render-header="_addStarToColumn">
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
            <el-table-column prop="field1" label="备注">
              <template slot-scope="scope">
                <el-input v-model="scope.row.remark" />
              </template>
            </el-table-column>
          </el-table>
        </el-collapse-item> -->
      </el-collapse>
    </el-form>
    <srm-dialog
      :title="$t('bidMod.withdraw')"
      size="small"
      append-to-body
      :visible.sync="withdrawDailog"
    >
      <el-form ref="withdrawform" :model="withdrawform" :rules="rules.field1">
        <el-form-item :label="$t('bidMod.withdrawReason')">
          <el-input
            v-model="withdrawform.withdrawReason"
            :autosize="{ minRows: 2, maxRows: 4}"
            type="textarea"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="withdrawDailog = false">
          {{ $t('common.cancel') }}
        </el-button>
        <el-button type="primary" @click="confirmWithdraw">
          {{ $t('common.confirm') }}
        </el-button>
      </div>
    </srm-dialog>
    <div v-if="editRows.flag === 'edit' || editRows.isWithdraw " slot="footer" class="dialog-footer">
      <el-button v-if="!editRows.isWithdraw" @click="dialogVisible = false">
        {{ $t("common.close") }}
      </el-button>
      <el-button v-if="!editRows.isWithdraw" type="primary" @click="save">
        {{ $t('bidMod.temporaryStorage') }}
      </el-button>
      <el-button v-if="!editRows.isWithdraw" type="primary" @click="submit">
        {{ $t('sourcingBuyer.submitSignUp') }}
      </el-button>
      <el-button v-if="editRows.isWithdraw" type="primary" @click="withdrawDailog = true">
        {{ $t('bidMod.withdraw') }}
      </el-button>
    </div>
  </SrmDialog>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { transformMQL } from 'lib@/utils/util'
import soucHttp from '../../../api/soucHttp.js'
import { validEmail, validatePhone } from '@/utils/validate'
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
        fileFunction: 'SOURCE_PUBLISH',
        fileType: 'images'
      },
      rules: {
        field1: [{ required: true, message: this.$t('vendorMod.required') }]
      },
      withdrawformRules: {
        field1: [{ required: true, message: this.$t('vendorMod.required') }]
      },
      withdrawDailog: false,
      withdrawform: {
        withdrawReason: null
      },
      form: {
        souReqHead: {
          field1: ''
        },
        souReqApply: {
          applyId: '',
          fileUploads: [{ attachmentType: 'QUALIFICATION_CERTIFICATE' }, { attachmentType: 'PERFORMANCE_CERTIFICATE' }, { attachmentType: 'OTHER' }]
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
    // 是否代缴
    extIsBehalfPayChange (value) {
      this.$set(this.form.souReqApply, 'vendorBankAccount', value === 'N' ? this.$store.getters.userInfo.companyName : '')
    },
    async confirmWithdraw () {
      if (!this.withdrawform.withdrawReason) {
        this.$message.error(this.$t('logisticsMod.msgWithdrawReason'))
        return
      }
      this.form.souReqApply.withdrawReason = this.withdrawform.withdrawReason
      let transfromParams = transformMQL.save(
        'SouReqApply',
        [this.form.souReqApply],
        'withdraw',
      )
      const response = await soucHttp.withdraw(transfromParams)
      this.$message.success(this.$t('common.successWithdraw'))
      this.withdrawDailog = false
      this.dialogVisible = false
      this.$emit('fresh')
    },
    // 验证邮箱
    validateEmailFn (rule, value, callback) {
      if (!value) {
        return callback(new Error(this.$t('dataConfMod.msgMail')))
      } else {
        if (!validEmail(value)) {
          return callback(new Error(this.$t('dataConfMod.fillEmail')))
        } else {
          return callback()
        }
      }
    },
    // 验证手机
    validatePhoneFn (rule, value, callback) {
      if (!value) {
        return callback(new Error(this.$t('vendorMod.enterCode')))
      } else {
        if (!validatePhone(value)) {
          return callback(new Error(this.$t('dataConfMod.fillPhone')))
        } else {
          return callback()
        }
      }
    },
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
          // let fileUploadsFlag = false
          // this.form.souReqApply.fileUploads.forEach((item, index) => {
          //   if (index < 2 && !item.fileuploadId) {
          //     fileUploadsFlag = true
          //   }
          // })
          // if (fileUploadsFlag) {
          //   return this.$message.error('请上传业绩证明文件和资质证明文件')
          // }
          let transfromParams = transformMQL.save(
            'SouReqApply',
            [this.form.souReqApply],
            'apply',
          )
          const response = await soucHttp.applySubmit(transfromParams)
          this.$message.success(this.$t('cusEntry.common.signUpSuccess')) // 报名成功
          this.dialogVisible = false
          this.$emit('fresh')
        } else {
          this.$message.error(this.$t('cusEntry.supplement20250121.pleaseFillInTheRequiredFields')) // 请填写必填项
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
      this.$message.success(this.$t('sourcingBuyer.stagingSuccess'))
      this.dialogVisible = false
    },
    clearFrom () {
      this.form = {
        souReqHead: {
        },
        souReqApply: {
          applyId: null,
          fileUploads: [{ attachmentType: 'QUALIFICATION_CERTIFICATE' }, { attachmentType: 'PERFORMANCE_CERTIFICATE' }, { attachmentType: 'OTHER' }]
        }
      }
    },
    async getFormDetail () {
      this.clearFrom()
      let reqHeadId = this.editRows.reqHeadId
      console.log('editRows:', this.editRows)
      let transfromParams = transformMQL.save(
        'SouReqApply',
        [{
          reqHeadId
        }],
        'getApplyInfo',
      )
      const response = await soucHttp.getApplyInfo(transfromParams)
      console.log('response:', response)
      if (response?.data?.records.length) {
        const { souReqHead, souReqApply } = response.data.records[0]
        console.log('souReqHead', souReqHead)
        if (souReqHead) {
          this.form.souReqHead = souReqHead
          this.form.souReqHead.applyStatus = this.editRows.souReqApplyList[0]?.applyStatus
        }
        if (souReqApply) {
          this.form.souReqApply = souReqApply
        }
        const { companyId, companyCode, companyName } = this.$store.getters.userInfo
        this.form.souReqApply.reqHeadId = reqHeadId
        this.form.souReqApply.vendorId = companyId
        this.form.souReqApply.vendorCode = companyCode
        this.form.souReqApply.vendorName = companyName
        // 默认是否代缴为是
        this.$set(this.form.souReqApply, 'extIsBehalfPay', this.form.souReqApply?.extIsBehalfPay || 'N')
        // 默认付款方名称
        this.$set(this.form.souReqApply, 'vendorBankAccount', this.form.souReqApply.vendorBankAccount || this.$store.getters.userInfo.companyName)
        console.log('this.form:', this.form, this.$store.getters)
      }
    }
  }
}
</script>
