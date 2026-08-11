<template>
  <srm-dialog
    :visible.sync="dialogVisible"
    :title="baseInfo.type === 'confirm' ? $t('cusEntry.biddingSettings.confirmBid') : $t('cusEntry.biddingSettings.extNotAttend')"
    size="middle"
    append-to-body
    :close-on-click-modal="false"
  >
    <div class="dialog-wrap">
      <el-form
        ref="confirmBidForm"
        :model="confirmBidForm"
        :rules="confirmBidFormRules"
        :disabled="readonly || isDeadline"
      >
        <srm-row>
          <srm-col :init-col="2">
            <!-- 投标人 -->
            <el-form-item
              prop="extTenderName"
              :label="$t('cusEntry.biddingSettings.bidPerson')"
            >
              <el-input
                v-model="confirmBidForm.extTenderName"
                :placeholder="$t('cusEntry.common.pleaseFill')"
              />
            </el-form-item>
          </srm-col>
          <srm-col :init-col="2">
            <el-form-item
              prop="extTenderPhone"
              :label="$t('cusEntry.biddingSettings.bidPhone')"
            >
              <el-input
                v-model="confirmBidForm.extTenderPhone"
                :placeholder="$t('cusEntry.common.pleaseFill')"
              />
            </el-form-item>
          </srm-col>
          <srm-col :init-col="2">
            <el-form-item
              prop="extTenderEmail"
              :label="$t('cusEntry.biddingSettings.bidEmail')"
            >
              <el-input
                v-model="confirmBidForm.extTenderEmail"
                :placeholder="$t('cusEntry.common.pleaseFill')"
              />
            </el-form-item>
          </srm-col>
          <srm-col v-if="baseInfo.type === 'extNotAttend'" :init-col="2">
            <el-form-item
              prop="extNotAttendReason"
              :label="$t('cusEntry.bidMod.withdrawReason')"
            >
              <el-input
                v-model="confirmBidForm.extNotAttendReason"
                :placeholder="$t('cusEntry.common.pleaseFill')"
              />
            </el-form-item>
          </srm-col>
        </srm-row>
      </el-form>
    </div>
    <div slot="footer" class="dialog-footer">
      <el-button @click="dialogVisible = false">
        {{ $t('common.close') }}
      </el-button>
      <!--提交-->
      <el-button
        v-if="!readonly && !isDeadline"
        type="primary"
        @click="confirmBid"
      >
        {{ $t('common.submit') }}
      </el-button>
    </div>
  </srm-dialog>
</template>
<script>
/**
 * 供应商确认投标 or 不参与
 */
import { validatorBusinessType } from 'lib@/composition/origin/composition'
import { isMobile, isEmail } from 'lib@/utils/validate'
export default {
  name: 'ConfirmBidDialog',
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
          type: 'confirm',
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
      confirmBidForm: {
        extTenderName: '',
        extTenderPhone: '',
        extTenderEmail: '',
        extNotAttendReason: ''
      },
      confirmBidFormRules: {
        extTenderName: [{ required: true, message: this.$t('cusEntry.common.pleaseFill') }],
        extTenderPhone: [
          { required: true, message: this.$t('cusEntry.common.pleaseFill') },
          {
            validator: (_rule, value, callback) => {
              if (!value) {
                callback()
              } else if (!isMobile(value)) {
                // 手机格式不合法
                callback(new Error(this.$t('bidMod.bidMsgList[26]')))
              }
              callback()
            },
            trigger: ['blur', 'change']
          }
        ],
        extTenderEmail: [
          { required: true, message: this.$t('cusEntry.common.pleaseFill') },
          {
            validator: (_rule, value, callback) => {
              if (!value) {
                callback()
              } else if (!isEmail(value)) {
                // 手机格式不合法
                callback(new Error(this.$t('dataConfMod.fillEmail')))
              }
              callback()
            },
            trigger: ['blur', 'change']
          }
        ],
        extNotAttendReason: [{ required: true, message: this.$t('cusEntry.common.pleaseFill') }]
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
  created () {},
  methods: {
    // 确认投标
    confirmBid () {
      this.$refs.confirmBidForm.validate(valid => {
        if (valid) {
          this.$http({
            url: '/api-sou/ext/vendor/bid/confirmTender',
            method: 'POST',
            data: {
              ...this.confirmBidForm,
              orderId: this.baseInfo.id
            },
            loading: true
          }).then(res => {
            this.$message.success(res.message)
            this.$emit('success')
            this.dialogVisible = false
          })
        } else {
          return false
        }
      })
    }
  }
}
</script>
<style lang="scss" scoped>
</style>
