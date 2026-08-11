<template>
  <div class="edit-quote-time">
    <!--修改报价时间-->
    <el-button type="primary" @click="openDialog">
      {{ $t('bidMod.editQuoteTime') }}
    </el-button>

    <!--修改报价时间 弹窗-->
    <SrmDialog
      size="small"
      :title="$t('bidMod.chooseNewQuoteTime')"
      :visible.sync="dialogVisible"
      append-to-body
      :close-on-click-modal="false"
    >
      <p>
        <span style="padding-right: 11px;">{{ $t('bidMod.quoteEndTime') }}</span>
        <el-date-picker
          v-model="orderEndTime"
          type="datetime"
          :format="$formatDatePickerTime"
          :picker-options="pickerOptions"
        />
      </p>

      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">
          {{ $t('common.backTo') }}
        </el-button>
        <el-button
          type="primary"
          :disabled="!orderEndTime"
          @click="formerFinish"
        >
          {{ $t('common.confirm') }}
        </el-button>
      </div>
    </SrmDialog>
  </div>
</template>

<script>
/**
 * 修改报价时间
 */
import purInqApi from 'modcb@/centralizedPurchase/api'
export default {
  name: 'EditQuoteTime',

  props: {
    projectId: {
      type: [String, Number],
      default: ''
    },
    orderStartTime: {
      type: String,
      required: true
    }
  },

  data () {
    return {
      dialogVisible: false,
      orderEndTime: '',
      pickerOptions: {
        disabledDate: time => {
          if (this.orderStartTime) {
            return time.getTime() < new Date(this.orderStartTime).getTime()
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
    /* 打开弹窗 */
    openDialog () {
      this.orderEndTime = ''
      this.dialogVisible = true
    },

    /* 提交修改 */
    async formerFinish () {
      if (new Date(this.orderEndTime).getTime() <= new Date(this.orderStartTime).getTime()) {
        this.$message.warning(this.$t('bidMod.quoteEndTimeMsg'))
        return
      }

      const response = await purInqApi.select.changeDeadline({
        // 询价单ID
        projectId: this.projectId,
        // 是否立即结束(Y/N)
        endNow: 'N',
        // 时间
        orderEndTime: this.orderEndTime
      })
      if (response) {
        this.$message.success(this.$t('bidMod.quoteEndTimeSuccess'))
        this.$emit('success')
        this.dialogVisible = false
      }
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
