<template>
  <srm-dialog
    size="xLarge"
    title="发起新一轮报价"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
    append-to-body
  >
    <el-form
      ref="continuePriTalkForm"
      :model="continuePriTalkFormData"
      :rules="continuePriTalkFormRules"
      inline
      label-position="right"
      label-width="130px"
    >
      <el-form-item label="新一轮报价开始时间" prop="beginQuote" style="margin-bottom: 20px">
        <el-date-picker
          v-model="continuePriTalkFormData.beginQuote"
          type="datetime"
          value-format="yyyy-MM-dd HH:mm:ss"
          :picker-options="pickerOptions"
        />
      </el-form-item>

      <el-form-item label="新一轮报价结束时间" prop="deadline" style="margin-bottom: 20px">
        <el-date-picker
          v-model="continuePriTalkFormData.deadline"
          type="datetime"
          value-format="yyyy-MM-dd HH:mm:ss"
          :picker-options="pickerOptions"
        />
      </el-form-item>
    </el-form>

    <!--邀请询价-->
    <template v-if="isInviteTender">
      <p>邀请新供应商</p>
      <InviteSuppliers
        ref="inviteSuppliers"
        business-type="INQUIRY"
        :material-data="materialData"
        :base-info="header"
      />
    </template>

    <div slot="footer" class="dialog-footer">
      <el-button @click="dialogVisible = false">
        {{ $t('common.cancel') }}
      </el-button>
      <el-button type="primary" @click="saveContinuePriTalkFormData">
        {{ $t('common.confirm') }}
      </el-button>
    </div>
  </srm-dialog>
</template>

<script>
/**
 * 发起新一轮议价
 */
import InviteSuppliers from 'lib@/composition/origin/inviteSuppliers'

export default {
  name: 'ContinuePriTalkDialog',

  components: {
    InviteSuppliers
  },

  props: {
    visible: {
      type: Boolean,
      required: true
    },
    header: {
      type: Object,
      required: true
    }
  },

  data () {
    return {
      continuePriTalkFormData: {
        beginQuote: '',
        deadline: ''
      },
      continuePriTalkFormRules: {
        beginQuote: [
          { required: true, message: '请选择', trigger: 'change' },
          { validator: this.validateDateTime, trigger: 'change' }
        ],
        deadline: [
          { required: true, message: '请选择', trigger: 'change' },
          { validator: this.validateDateTime, trigger: 'change' }
        ]
      },
      pickerOptions: {
        disabledDate (time) {
          const today = new Date()
          today.setHours(0)
          today.setMinutes(0)
          today.setSeconds(0)
          today.setMilliseconds(0)
          return time.getTime() < today.getTime()
        }
      },
      materialData: []
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
    },

    // 是否邀请询价
    isInviteTender () {
      return this.header.publishScope === 'INVITE_TENDER'
    }
  },

  created () {
    if (this.isInviteTender) {
      this.getInqInfo()
    }
  },

  methods: {
    /* 查询询价单信息 */
    async getInqInfo () {
      const responseData = await this.$api.inq.getInqInfoById(this.header.inquiryId)
      // 获取物料列表
      if (responseData && responseData.data) {
        this.materialData = responseData.data.items || []
      }
    },

    /* 校验时间 */
    validateDateTime (rule, value, callback) {
      if (!value) {
        callback(new Error(this.$t('common.pleaseSelect')))
      }
      if (this.continuePriTalkFormData.beginQuote && this.continuePriTalkFormData.deadline) {
        const beginQuoteTime = new Date(this.continuePriTalkFormData.beginQuote).getTime()
        const deadlineTime = new Date(this.continuePriTalkFormData.deadline).getTime()
        if (beginQuoteTime >= deadlineTime) {
          callback(new Error('开始时间不能大于等于结束时间'))
        }
      }
      callback()
    },

    /* 确定提交 */
    saveContinuePriTalkFormData () {
      this.$refs.continuePriTalkForm.validate(valid => {
        if (valid) {
          // 校验添加供应商
          const paramsData = {
            inquiryId: this.header.inquiryId,
            vendorList: [],
            ...this.continuePriTalkFormData
          }

          if (this.isInviteTender) {
            paramsData.vendorList = this.$refs.inviteSuppliers.getSuppliersPermissionData()
          }

          this.$api.inq.inquiryBySimple.startNewRound(paramsData).then(() => {
            this.$message.success('发起新一轮报价成功')
            this.dialogVisible = false
            this.$emit('saveContinueTalkPriceSuccess')
          })
        }
      })
    }
  }
}
</script>
