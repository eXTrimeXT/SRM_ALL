<template>
  <srm-dialog
    size="middle"
    :title="$t('bidMod.biddingControl.startNewRound')"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
  >
    <p style="text-align: center">
      <span style="padding-right: 11px">{{ $t('bidMod.biddingControl.selectNewBidingStartDatetime') }}</span>
      <el-date-picker
        v-model="bidingStartTime"
        type="datetime"
        value-format="timestamp"
        :placeholder="$t('bidMod.datePicker')"
      />
    </p>
    <p style="text-align: center">
      <span style="padding-right: 11px">{{ $t('bidMod.biddingControl.selectNewBidingEndDatetime') }}</span>
      <el-date-picker
        v-model="bidingEndTime"
        type="datetime"
        value-format="timestamp"
        :placeholder="$t('bidMod.datePicker')"
      />
    </p>
    <template #footer>
      <el-button

        @click="dialogVisible = false"
      >
        {{ $t("common.cancel") }}
      </el-button>

      <el-button
        type="primary"

        :disabled="!bidingStartTime || !bidingEndTime"
        @click="startNewRound"
      >
        {{ $t("common.confirm") }}
      </el-button>
    </template>
  </srm-dialog>
</template>

<script>
/**
 * 发起新一轮
 */
export default {
  name: 'StartNewRoundDialog',
  props: {
    visible: {
      type: Boolean
    }
  },
  data () {
    return {
      bidingStartTime: new Date(),
      bidingEndTime: ''
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
    startNewRound () {
      this.$emit('startBiding', 'newRound', {
        bidingStartTime: this.bidingStartTime,
        bidingEndTime: this.bidingEndTime
      })
    }
  }
}
</script>
