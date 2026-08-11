<template>
  <!-- 意向金退款申请 -->
  <SrmDialog
    :title="$t('cusEntry.supplement20250205.refund')"
    size="small"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
  >
    <el-form ref="form" :model="form" disabled>
      <SrmRow>
        <SrmCol :init-col="2">
          <!-- 退款账户 -->
          <el-form-item prop="refundBankAccount" :label="$t('cusEntry.bidMod.refundAccount')">
            <el-input v-model="form.refundBankAccount" />
          </el-form-item>
        </SrmCol>
        <SrmCol :init-col="2">
          <!-- 退款户名 -->
          <el-form-item prop="refundBankAccountName" :label="$t('cusEntry.bidMod.refundAccountName')">
            <el-input v-model="form.refundBankAccountName" />
          </el-form-item>
        </SrmCol>
        <SrmCol :init-col="2">
          <!-- 退款银行 -->
          <el-form-item prop="refundBankName" :label="$t('cusEntry.bidMod.refundBank')">
            <el-input v-model="form.refundBankName" />
          </el-form-item>
        </SrmCol>
        <SrmCol :init-col="2">
          <!-- 退款金额 -->
          <el-form-item prop="refundAmount" :label="$t('cusEntry.reportManagement.depositRefundAmount')">
            <el-input v-model="form.refundAmount" />
          </el-form-item>
        </SrmCol>
        <SrmCol :init-col="2">
          <!-- 退款状态 -->
          <el-form-item prop="depositRefundStatus" :label="$t('cusEntry.bidMod.refundStatus')">
            <DictSelect v-model="form.applyId.depositRefundStatus" code="SOU_INT_DEPOSIT_REFUND_STATUS" />
          </el-form-item>
        </SrmCol>
        <SrmCol :init-col="2">
          <!-- 退款原因 -->
          <el-form-item prop="refundReason" :label="$t('cusEntry.reportManagement.refundReason')">
            <DictSelect v-model="form.refundReason" code="INT_DEPOSIT_REFUND_REASON" />
          </el-form-item>
        </SrmCol>
        <SrmCol :init-col="2">
          <!--上传附件-->
          <el-form-item prop="refundFileId" :label="$t('bidMod.accessory')">
            <SrmCommonFile
              :default-file="{
                fileId: form.refundFileId,
                fileName: form.refundFileName
              }"
              :readonly="true"
            />
          </el-form-item>
        </SrmCol>
        <SrmCol :init-col="2">
          <!-- 退款时间 -->
          <el-form-item prop="lastUpdateDate" :label="$t('cusEntry.reportManagement.refundTime')">
            <el-date-picker
              v-model="form.lastUpdateDate"
              type="datetime"
              :format="$formatDatePickerTime"
              value-format="yyyy-MM-dd HH:mm:ss"
            />
          </el-form-item>
        </SrmCol>
        <SrmCol :init-col="1">
          <el-form-item prop="refundRemark" :label="$t('common.remark')">
            <el-input v-model="form.refundRemark" />
          </el-form-item>
        </SrmCol>
      </SrmRow>
    </el-form>

    <div slot="footer" class="dialog-footer">
      <el-button @click="dialogVisible = false">
        {{ $t("common.close") }}
      </el-button>
    </div>
  </SrmDialog>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { transformMQL } from 'lib@/utils/util'
import soucHttp from '../../../api/soucHttp.js'
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
      form: {
        applyId: {
          depositRefundStatus: null
        },
        refundBankAccount: null,
        refundBankAccountName: null,
        refundBankName: null,
        refundAmount: null,
        refundReason: null,
        refundFileId: null,
        refundFileName: null,
        lastUpdateDate: null
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
    viewDepositNo (row) {},
    clearForm () {
      this.form = {
        applyId: {
          depositRefundStatus: null
        },
        refundBankAccount: null,
        refundBankAccountName: null,
        refundBankName: null,
        refundAmount: null,
        refundReason: null,
        refundFileId: null,
        refundFileName: null,
        lastUpdateDate: null
      }
    },
    async getFormDetail () {
      this.clearForm()
      let reqHeadId = this.editRows.reqHeadId
      console.log('company',)
      let transfromParams = transformMQL.save(
        'SouIntDepositRefund',
        {
          filter: {
            reqHeadId: {
              eq: reqHeadId
            },
            vendorId: {
              eq: this.$store.getters.companyId
            }
          }
        },
        'query',
        {
          '*': {},
          applyId: { '*': {} }
        },
      )
      const response = await soucHttp.SouIntDeposit(transfromParams)
      if (response?.data?.records?.length) {
        this.form = response.data.records[0]
      } else {
        // 尚无退款记录
        this.$message.warning(this.$t('cusEntry.supplement20250205.refundTip'))
        this.dialogVisible = false
      }
    }
  }
}
</script>
