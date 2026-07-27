<template>
  <srm-dialog
    :visible.sync="dialogVisible"
    :title="readonly ? $t('cusEntry.biddingSettings.viewDeposit') : $t('cusEntry.biddingSettings.payDeposit')"
    size="large"
    append-to-body
    :close-on-click-modal="false"
  >
    <div class="dialog-wrap">
      <!--报价截止倒计时-->
      <!-- <div>
        <DynamicCutoffTime
          label="保证金缴纳剩余时间："
          :deadline-time="bondPayInfoData.bondEndDatetime"
          @isDeadline="value => isDeadline = value"
        />
      </div> -->
      <el-form
        ref="bondPayForm"
        :model="bondPayInfoData"
        :rules="bondPayFormRules"
        label-position="top"
        :disabled="readonly"
      >
        <srm-row>
          <srm-col :init-col="3">
            <el-form-item
              prop="extEarnestAmount"
              :label="$t('cusEntry.biddingSettings.bondAmount')"
            >
              <el-input v-model="bondPayInfoData.extEarnestAmount" disabled />
            </el-form-item>
          </srm-col>
          <srm-col :init-col="3">
            <el-form-item
              prop="extBankName"
              :label="$t('cusEntry.biddingSettings.bank')"
            >
              <el-input v-model="bondPayInfoData.extBankName" disabled />
            </el-form-item>
          </srm-col>
          <srm-col :init-col="3">
            <el-form-item
              prop="extBankNumber"
              :label="$t('cusEntry.biddingSettings.bankNum')"
            >
              <el-input v-model="bondPayInfoData.extBankNumber" disabled />
            </el-form-item>
          </srm-col>
          <srm-col :init-col="3">
            <el-form-item
              prop="extBankAccount"
              :label="$t('cusEntry.biddingSettings.accountNum')"
            >
              <el-input v-model="bondPayInfoData.extBankAccount" disabled />
            </el-form-item>
          </srm-col>
          <srm-col :init-col="3">
            <el-form-item
              prop="extBankAccountName"
              :label="$t('cusEntry.biddingSettings.accountName')"
            >
              <el-input v-model="bondPayInfoData.extBankAccountName" disabled />
            </el-form-item>
          </srm-col>
          <srm-col :init-col="3">
            <el-form-item
              prop="marginStatus"
              :label="$t('cusEntry.biddingSettings.paymentStatus')"
            >
              <DictSelect
                v-model="bondPayInfoData.marginStatus"
                code="SOU_BID_MARGIN_STATUS"
                disabled
              />
            </el-form-item>
          </srm-col>
        </srm-row>
        <srm-row>
          <srm-col :init-col="3" prop="payVoucherFileId">
            <SrmCommonFile
              type="form-item"
              :default-file="{
                fileId: bondPayInfoData.payVoucherFileId,
                fileName: bondPayInfoData.payVoucher
              }"
              :form-item-options="{
                label: $t('cusEntry.biddingSettings.payFileName'),
                prop: 'payVoucherFileId',
                nameProp: 'payVoucher'
              }"
              :readonly="readonly || isDeadline"
              @on-change="bondPayFilesChange"
            />
          </srm-col>
          <srm-col :init-col="3">
            <el-form-item
              prop="extIsBehalfPay"
              :label="$t('cusEntry.biddingSettings.extIsBehalfPay')"
            >
              <DictSelect
                v-model="bondPayInfoData.extIsBehalfPay"
                code="YES_OR_NO"
                :clearable="false"
                @change="extIsBehalfPayChange"
              />
            </el-form-item>
          </srm-col>
          <srm-col :init-col="3">
            <el-form-item
              prop="payName"
              :label="$t('cusEntry.biddingSettings.payName')"
            >
              <template #label>
                {{ $t('cusEntry.biddingSettings.payName') }}
                <el-tooltip :content="$t('cusEntry.biddingSettings.payNameTip')" placement="top">
                  <i class="el-icon-question" />
                </el-tooltip>
              </template>
              <el-input v-model="bondPayInfoData.payName" :disabled="readonly || isDeadline || bondPayInfoData.extIsBehalfPay === 'N'" />
            </el-form-item>
          </srm-col>
          <!-- 缴纳失败才展示原因 -->
          <srm-col v-if="bondPayInfoData.marginStatus=='FAIL_PAY'" :init-col="1">
            <el-form-item
              prop="causeDesc"
              :label="$t('cusEntry.biddingSettings.payFailReason')"
            >
              <el-input v-model="bondPayInfoData.causeDesc" disabled />
            </el-form-item>
          </srm-col>
        </srm-row>
      </el-form>
    </div>
    <div slot="footer" class="dialog-footer">
      <el-button @click="dialogVisible = false">
        {{ $t('common.close') }}
      </el-button>
      <el-button
        v-if="!readonly && !isDeadline"
        type="primary"
        @click="submitBond"
      >
        {{ $t('common.submit') }}
      </el-button>
    </div>
  </srm-dialog>
</template>
<script>
/**
 * 供应商保证金缴纳
 */
import { validatorBusinessType } from 'lib@/composition/origin/composition'
import { cannotLargerCurrentTime } from 'lib@/mixins/datePickerOptions'
import DynamicCutoffTime from 'lib@/components/dynamic-cutoff-time'

export default {
  name: 'BondPayDialog',

  components: { DynamicCutoffTime },

  mixins: [cannotLargerCurrentTime],

  props: {
    // 业务类型
    businessType: {
      type: String,
      required: true,
      validator: value => validatorBusinessType(value)
    },
    visible: {
      type: Boolean,
      default: false
    },
    // 单据基础信息 { id, idKey }
    baseInfo: {
      type: Object,
      required: true,
      default: () => {
        return {
          id: '',
          idKey: ''
        }
      }
    },
    // 只读
    readonly: {
      type: Boolean,
      default: false
    }
  },

  data () {
    return {
      bondPayInfoData: {
        extEarnestAmount: null,
        extBankName: null,
        extBankNumber: null,
        extBankAccount: null,
        extBankAccountName: null,
        marginStatus: null,
        payVoucherFileId: null,
        payVoucher: null,
        marginFailCause: null
      },
      bondPayFormRules: {
        payVoucherFileId: [{ required: true, message: this.$t('cusEntry.biddingSettings.pleaseUploadFile') }],
        payName: [{ required: true, message: this.$t('cusEntry.biddingSettings.payNameMsg') }]
      },
      isDeadline: false
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

  created () {
    this.getBondData()
  },

  methods: {
    // 是否代缴
    extIsBehalfPayChange (value) {
      if (value === 'N') {
        this.bondPayInfoData.payName = this.$store.getters.userInfo.companyName
      } else {
        this.bondPayInfoData.payName = ''
      }
    },
    /* 获取保证金缴纳数据 */
    getBondData () {
      this.$http({
        url: `/api-sou/ext/vendor/bid/getMargin?projectId=${this.baseInfo.id}&vendorId=${this.baseInfo.vendorId}`,
        method: 'GET',
        loading: true
      }).then(res => {
        if (res && res.data) {
          this.bondPayInfoData = res.data
          // 默认是否代缴为是
          this.bondPayInfoData.extIsBehalfPay = this.bondPayInfoData.extIsBehalfPay || 'N'
          // 默认付款方名称
          this.bondPayInfoData.payName = this.bondPayInfoData.payName || this.$store.getters.userInfo.companyName
        }
      })
    },

    /* 文件上传改变 */
    bondPayFilesChange ({ file }) {
      if (file) {
        this.bondPayInfoData.payVoucherFileId = file.fileId.toString()
        this.bondPayInfoData.payVoucher = file.fileName
      } else {
        this.bondPayInfoData.payVoucherFileId = ''
        this.bondPayInfoData.payVoucher = ''
      }
    },

    /* 提交缴纳保证金 */
    async submitBond () {
      this.$refs.bondPayForm.validate(valid => {
        if (valid) {
          this.$http({
            url: '/api-sou/ext/vendor/bid/editOrderMargin',
            method: 'POST',
            data: this.bondPayInfoData,
            loading: true
          }).then(res => {
            this.$message.success(res.message)
            this.$emit('success')
            this.dialogVisible = false
          })
        }
      })
    }
  }
}
</script>
<style lang="scss" scoped>
</style>
