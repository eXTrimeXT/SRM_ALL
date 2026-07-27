<template>
  <div class="edit-quote-time">
    <!--修改报价时间-->
    <el-button type="primary" @click="openDialog">
      {{ $t('bidMod.editQuoteTime') }}
    </el-button>

    <!--修改报价时间 弹窗-->
    <srm-dialog
      size="small"
      title="选择新的报价结束时间"
      :visible.sync="dialogVisible"
      append-to-body
      :close-on-click-modal="false"
    >
      <p>
        <span style="padding-right: 11px;">报价结束时间</span>
        <el-date-picker v-model="beforeDeadline" type="datetime" :picker-options="pickerOptions" />
      </p>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">
          {{ $t("common.backTo") }}
        </el-button>
        <el-button type="primary" :disabled="!beforeDeadline" @click="formerFinish">
          {{ $t('common.confirm') }}
        </el-button>
      </div>
    </srm-dialog>
  </div>
</template>

<script>
/**
 * 修改报价时间
 */
export default {
  name: 'EditQuoteTime',

  props: {
    inquiryId: {
      type: [String, Number],
      default: ''
    },
    beginQuote: {
      type: String,
      required: true
    }
  },

  data () {
    return {
      dialogVisible: false,
      beforeDeadline: '',
      pickerOptions: {
        disabledDate: time => {
          if (this.beginQuote) {
            return time.getTime() < new Date(this.beginQuote).getTime()
          }
          const today = new Date()
          today.setHours(0)
          today.setMinutes(0)
          today.setSeconds(0)
          today.setMilliseconds(0)
          return time.getTime() < today.getTime()
        }
      }
    }
  },

  methods: {
    openDialog () {
      this.beforeDeadline = ''
      this.dialogVisible = true
    },

    formerFinish () {
      if (new Date(this.beforeDeadline).getTime() <= new Date(this.beginQuote).getTime()) {
        this.$message.warning('报价截止时间必须大于报价开始时间！')
        return
      }

      this.$api.inq.inquiryBySimple.changeDeadline({
          // 询价单ID
          inquiryId: this.inquiryId,
          // 是否立即开始(Y/N)
          shouldDeadlineNow: 'N',
          // 时间
          deadline: this.beforeDeadline }).then(() => {
        this.$message.success('修改报价截止时间成功!')
        this.$emit('changeDeadlineSuccess')
        this.dialogVisible = false
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.edit-quote-time {
  display: inline-block;
  margin: 0 5px;
}
</style>
