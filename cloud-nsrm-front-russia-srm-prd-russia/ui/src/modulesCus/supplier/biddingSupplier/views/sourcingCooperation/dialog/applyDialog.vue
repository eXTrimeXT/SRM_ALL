<template>
  <SrmDialog
    title="报名详情"
    size="large"
    destroy-on-close
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
  >
    <div style="color:red;margin-bottom:10px">
      报名须知：报名前须自行详细查看并判断资质及业绩符合性后，再进行报名，投标意向金缴纳后不予退还，若项目在招标过程中任意环节因招标方原因取消的，可办理投标意向金退款
      （若供应商已办理开票将不予退款），如果不能准确判定可询问报名负责人；
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
        <el-collapse-item title="基础信息" name="1">
          <srm-row>
            <!-- 寻源单号 -->
            <srm-col :init-col="4">
              <el-form-item label="寻源单号">
                <el-input v-model="form.souReqHead.reqHeadNo" disabled />
              </el-form-item>
            </srm-col>
            <!-- 单据状态 -->
            <srm-col :init-col="4">
              <el-form-item label="单据状态">
                <dictSelect v-model="form.souReqHead.status" code="SOU_REQ_HEAD_STATUS" disabled />
              </el-form-item>
            </srm-col>
            <!-- 供应商名称 -->
            <srm-col :init-col="4">
              <el-form-item label="供应商名称">
                <el-input v-model="form.souReqApply.vendorName" disabled />
              </el-form-item>
            </srm-col>
            <!-- 报名状态 -->
            <srm-col :init-col="4">
              <el-form-item label="报名状态">
                <dictSelect v-model="form.souReqHead.applyStatus" code="SOU_REQ_APPLY_STATUS" disabled />
              </el-form-item>
            </srm-col>
            <!-- 项目名称 -->
            <srm-col :init-col="4">
              <el-form-item label="项目名称">
                <el-input v-model="form.souReqHead.projectName" disabled />
              </el-form-item>
            </srm-col>
            <!-- 项目所在地 -->
            <srm-col :init-col="4">
              <el-form-item label="项目所在地">
                <el-input v-model="form.souReqHead.projectAddress" disabled />
              </el-form-item>
            </srm-col>
            <!-- 是否代理 -->
            <srm-col :init-col="4">
              <el-form-item label="是否为代理商" prop="souReqApply.isAgent" :rules="rules.field1">
                <dictSelect v-model="form.souReqApply.isAgent" code="YES_OR_NO" />
              </el-form-item>
            </srm-col>
            <!-- 代理品牌 -->
            <srm-col :init-col="4">
              <el-form-item label="代理的品牌名称" prop="souReqApply.agentBrand" :rules="[{ required: form.souReqApply.isAgent === 'Y', message: '必填' }]">
                <el-input v-model="form.souReqApply.agentBrand" />
              </el-form-item>
            </srm-col>
          </srm-row>
        </el-collapse-item>

        <!-- 意向金缴纳消息 -->
        <el-collapse-item v-if="form.souReqHead.isNeedDeposit === 'Y'" title="意向金缴纳消息" name="2">
          <srm-row>
            <!-- 投标意向金 -->
            <srm-col :init-col="4">
              <el-form-item label="投标意向金">
                <el-input v-model="form.souReqHead.depositAmount" disabled />
              </el-form-item>
            </srm-col>
            <!-- 收款账户 -->
            <srm-col :init-col="4">
              <el-form-item label="收款账户">
                <el-input v-model="form.souReqHead.bankAccount" disabled />
              </el-form-item>
            </srm-col>
            <!-- 开户银行 -->
            <srm-col :init-col="4">
              <el-form-item label="开户银行">
                <el-input v-model="form.souReqHead.bankName" disabled />
              </el-form-item>
            </srm-col>
            <!-- 开户户名 -->
            <srm-col :init-col="4">
              <el-form-item label="开户户名">
                <el-input v-model="form.souReqHead.bankAccountName" disabled />
              </el-form-item>
            </srm-col>
            <!-- 开户银行行号  -->
            <srm-col :init-col="4">
              <el-form-item label="开户银行行号">
                <el-input v-model="form.souReqHead.bankNumber" disabled />
              </el-form-item>
            </srm-col>
            <!-- 投标意向金缴纳凭证  -->
            <srm-col :init-col="4">
              <el-form-item
                label="投标意向金缴纳凭证"
                prop="souReqApply.depositFileId"
                :rules="rules.field1"
              >
                <SrmCommonFile
                  :extra-data="fileInfo"
                  :default-file="{
                    fileId: form.souReqApply.depositFileId,
                    fileName: form.souReqApply.depositFileName
                  }"
                  @on-change="({file}) => handleUploadSuccess(file)"
                />
              </el-form-item>
            </srm-col>
          </srm-row>
          <srm-row>
            <srm-col :init-col="4">
              <el-form-item
                prop="extIsBehalfPay"
                :label="$t('cusEntry.biddingSettings.extIsBehalfPay')"
              >
                <DictSelect
                  v-model="form.souReqApply.extIsBehalfPay"
                  code="YES_OR_NO"
                  :clearable="false"
                  @change="extIsBehalfPayChange"
                />
              </el-form-item>
            </srm-col>
            <!-- 付款方名称  -->
            <srm-col :init-col="4">
              <el-form-item prop="souReqApply.vendorBankAccount" :rules="rules.field1">
                <template #label>
                  {{ $t('cusEntry.biddingSettings.payName') }}
                  <span style="color:red;font-size:10px">(需要填写付款人/付款公司名称，即交款底单上的付款人/付款公司名称，否则报名无效；)</span>
                  <!-- <el-tooltip :content="$t('cusEntry.biddingSettings.payNameTip')" placement="top">
                    <i class="el-icon-question" />
                  </el-tooltip> -->
                </template>
                <el-input v-model="form.souReqApply.vendorBankAccount" :disabled="form.souReqApply.extIsBehalfPay === 'N'" />
              </el-form-item>
            </srm-col>
            <div style="font-size:10px">
              注意：①原则上以公司交款为准，即公对公账户；注册成功后汇款至以上账户；②交款行为仅在此账户有效力，我司不存在其他任何代收账户；③请不要相信任何个人或公司的交款要求，以防上当受骗！
            </div>
            <div style="color:red;font-size:10px">
              特别提醒：公司缴费时请务必在交款凭证上备注“项目简称”，如为个人代缴，请务必在交款凭证上备注“项目简称+参标公司简称”
              (注：项目简称填写标的物名称即可)，未备注造成意向金无法追溯，将取消您此项目的报名资格。
            </div>
          </srm-row>
        </el-collapse-item>

        <!-- 供应商报名联系方式 -->
        <el-collapse-item title="供应商报名联系方式" name="3">
          <srm-row>
            <!-- 供应商报名联系人 -->
            <srm-col :init-col="4">
              <el-form-item label="供应商报名联系人" prop="souReqApply.applyContactName" :rules="rules.field1">
                <el-input v-model="form.souReqApply.applyContactName" />
              </el-form-item>
            </srm-col>
            <!--报名人联系方式 -->
            <srm-col :init-col="4">
              <el-form-item
                label="报名人联系电话"
                prop="souReqApply.applyPhone"
                :rules="[
                  { required: true, message: '请输入手机号' },
                  { validator: validatePhoneFn, trigger: ['blur', 'change'] },
                ]"
              >
                <el-input v-model="form.souReqApply.applyPhone" />
              </el-form-item>
            </srm-col>
            <!-- 报名人邮箱 -->
            <srm-col :init-col="4">
              <el-form-item
                label="报名人邮箱"
                prop="souReqApply.applyEmail"
                :rules="[
                  { required: true, message: '请输入邮箱'},
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
              <el-form-item label="报名失败原因">
                <el-input v-model="form.souReqApply.applyFailReason" disabled />
              </el-form-item>
            </srm-col>
          </srm-row>
        </el-collapse-item>

        <!-- 附件信息 -->
        <el-collapse-item title="附件信息" name="4">
          <el-table :data="form.souReqHead.fileUploads" style="width: 100%" border max-height="390px">
            <el-table-column width="70px" type="index" label="序号" />
            <el-table-column prop="fileuploadId" label="附件名称">
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
            <el-table-column prop="remark" label="备注" />
          </el-table>
        </el-collapse-item>

        <!-- 资质业绩证明文件 -->
        <el-collapse-item title="资质业绩证明文件" name="5">
          <!-- <p>
            <el-button type="primary" @click="form.souReqApply.fileUploads.push({})">
              新增
            </el-button>
          </p> -->
          <el-table :data="form.souReqApply.fileUploads" style="width: 100%" border max-height="390px">
            <el-table-column width="70px" type="index" label="序号" />
            <el-table-column prop="attachmentType" label="附件类型" :formatter="(row, column, cellValue) => $getDictLabel('SCENE_SOU_REQ_APPLY_ATTACHMENT', cellValue)">
              <!-- <template slot-scope="scope">
                <dictSelect v-model="scope.row.attachmentType" code="SCENE_SOU_REQ_APPLY_ATTACHMENT" />
              </template> -->
            </el-table-column>
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
            <!-- <el-table-column label="操作">
              <template v-slot="scope"> -->
            <!--删除-->
            <!-- <el-button
                  type="text"
                  :disabled="scope.$index<3"
                  @click="delRow(scope.$index)"
                >
                  {{ $t("common.delete") }}
                </el-button>
              </template>
            </el-table-column> -->
          </el-table>
        </el-collapse-item>
      </el-collapse>
    </el-form>
    <srm-dialog
      title="撤回"
      size="small"
      append-to-body
      :visible.sync="withdrawDailog"
    >
      <el-form ref="withdrawform" :model="withdrawform" :rules="rules.field1">
        <el-form-item label="撤回原因">
          <el-input
            v-model="withdrawform.withdrawReason"
            :autosize="{ minRows: 2, maxRows: 4}"
            type="textarea"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="withdrawDailog = false">
          取消
        </el-button>
        <el-button type="primary" @click="confirmWithdraw">
          确定
        </el-button>
      </div>
    </srm-dialog>
    <div v-if="editRows.flag === 'edit' || editRows.isWithdraw " slot="footer" class="dialog-footer">
      <el-button v-if="!editRows.isWithdraw" @click="dialogVisible = false">
        {{ $t("common.close") }}
      </el-button>
      <el-button v-if="!editRows.isWithdraw" type="primary" @click="save">
        暂存
      </el-button>
      <el-button v-if="!editRows.isWithdraw" type="primary" @click="submit">
        提交报名
      </el-button>
      <el-button v-if="editRows.isWithdraw" type="primary" @click="withdrawDailog = true">
        撤回
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
        field1: [{ required: true, message: '必填' }]
      },
      withdrawformRules: {
        field1: [{ required: true, message: '必填' }]
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
        this.$message.error('请填写撤回原因')
        return
      }
      this.form.souReqApply.withdrawReason = this.withdrawform.withdrawReason
      let transfromParams = transformMQL.save(
        'SouReqApply',
        [this.form.souReqApply],
        'withdraw',
      )
      const response = await soucHttp.withdraw(transfromParams)
      this.$message.success('撤回成功')
      this.withdrawDailog = false
      this.dialogVisible = false
      this.$emit('fresh')
    },
    // 验证邮箱
    validateEmailFn (rule, value, callback) {
      if (!value) {
        return callback(new Error('请输入邮箱'))
      } else {
        if (!validEmail(value)) {
          return callback(new Error('请输入正确的邮箱地址'))
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
          return callback(new Error('请输入正确的手机号码'))
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
          let fileUploadsFlag = false
          this.form.souReqApply.fileUploads.forEach((item, index) => {
            if (index < 2 && !item.fileuploadId) {
              fileUploadsFlag = true
            }
          })
          if (fileUploadsFlag) {
            return this.$message.error('请上传业绩证明文件和资质证明文件')
          }
          let transfromParams = transformMQL.save(
            'SouReqApply',
            [this.form.souReqApply],
            'apply',
          )
          const response = await soucHttp.applySubmit(transfromParams)
          this.$message.success('报名成功')
          this.dialogVisible = false
          this.$emit('fresh')
        } else {
          this.$message.error('请填写必填项')
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
      this.$message.success('暂存成功')
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
