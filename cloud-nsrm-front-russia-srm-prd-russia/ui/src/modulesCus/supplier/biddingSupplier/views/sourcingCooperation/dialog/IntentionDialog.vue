<template>
  <SrmDialog
    title="意向金退款申请"
    size="small"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
  >
    <el-form ref="form" :model="form" disabled>
      <SrmRow>
        <SrmCol :init-col="2">
          <el-form-item prop="refundBankAccount" label="退款账户">
            <el-input v-model="form.refundBankAccount" />
          </el-form-item>
        </SrmCol>
        <SrmCol :init-col="2">
          <el-form-item prop="refundBankAccountName" label="退款户名">
            <el-input v-model="form.refundBankAccountName" />
          </el-form-item>
        </SrmCol>
        <SrmCol :init-col="2">
          <el-form-item prop="refundBankName" label="退款银行">
            <el-input v-model="form.refundBankName" />
          </el-form-item>
        </SrmCol>
        <SrmCol :init-col="2">
          <el-form-item prop="refundAmount" label="退款金额">
            <el-input v-model="form.refundAmount" />
          </el-form-item>
        </SrmCol>
        <SrmCol :init-col="2">
          <el-form-item prop="depositRefundStatus" label="退款状态">
            <DictSelect v-model="form.applyId.depositRefundStatus" code="SOU_INT_DEPOSIT_REFUND_STATUS" />
          </el-form-item>
        </SrmCol>
        <SrmCol :init-col="2">
          <el-form-item prop="refundReason" label="退款原因">
            <DictSelect v-model="form.refundReason" code="INT_DEPOSIT_REFUND_REASON" />
          </el-form-item>
        </SrmCol>
        <SrmCol :init-col="2">
          <!--上传附件-->
          <el-form-item prop="refundFileId" label="附件">
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
          <el-form-item prop="lastUpdateDate" label="退款时间">
            <el-date-picker
              v-model="form.lastUpdateDate"
              type="datetime"
              value-format="yyyy-MM-dd HH:mm:ss"
            />
          </el-form-item>
        </SrmCol>
        <SrmCol :init-col="1">
          <el-form-item prop="refundRemark" label="备注">
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
        this.$message.warning('尚无退款记录')
        this.dialogVisible = false
      }
    }
  }
}
</script>
