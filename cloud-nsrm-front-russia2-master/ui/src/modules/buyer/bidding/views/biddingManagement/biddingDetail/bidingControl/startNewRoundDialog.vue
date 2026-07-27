<template>
  <SrmDialog
    size="middle"
    :title="$t('bidMod.biddingControl.startNewRound')"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
  >
    <p style="text-align: center">
      <span style="padding-right: 11px">{{ $t('bidMod.biddingControl.selectNewBidingStartDatetime') }}</span>
      <el-date-picker
        v-model="orderStartTime"
        type="datetime"
        :format="$formatDatePickerTime"
        value-format="timestamp"
        :placeholder="$t('bidMod.datePicker')"
      />
    </p>

    <p style="text-align: center">
      <span style="padding-right: 11px">{{ $t('bidMod.biddingControl.selectNewBidingEndDatetime') }}</span>
      <el-date-picker
        v-model="orderEndTime"
        type="datetime"
        :format="$formatDatePickerTime"
        value-format="timestamp"
        :placeholder="$t('bidMod.datePicker')"
      />
    </p>

    <template #footer>
      <el-button @click="dialogVisible = false">
        {{ $t('common.cancel') }}
      </el-button>

      <el-button
        type="primary"
        :disabled="!orderStartTime || !orderEndTime"
        @click="confirm"
      >
        {{ $t('common.confirm') }}
      </el-button>
    </template>
  </SrmDialog>
</template>

<script>
/**
 * 发起新一轮
 */
import { bidBuyerHttp } from 'modb@/bidding/api'

export default {
  name: 'StartNewRoundDialog',

  props: {
    visible: {
      type: Boolean,
      required: true
    },
    projectId: {
      type: [String, Number],
      required: true
    }
  },

  data () {
    return {
      orderStartTime: new Date(),
      orderEndTime: ''
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

  methods: {
    /* 发起新一轮 */
    async confirm () {
      const response = await bidBuyerHttp.control.startNewRound({
        projectId: this.projectId,
        orderStartTime: this.orderStartTime,
        orderEndTime: this.orderEndTime
      })
      if (response) {
        this.$message.success(this.$t('common.successSubmit'))
        this.$emit('success')
        this.dialogVisible = false
      }
    }
  }
}
</script>
