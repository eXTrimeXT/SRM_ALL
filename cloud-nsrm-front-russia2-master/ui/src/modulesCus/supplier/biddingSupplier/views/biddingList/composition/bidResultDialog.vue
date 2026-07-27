<template>
  <srm-dialog
    :visible.sync="dialogVisible"
    :title="$t('cusEntry.biddingSettings.viewBidResult')"
    size="small"
    append-to-body
    :close-on-click-modal="false"
  >
    <div style="font-size: 12px;">
      <span v-if="isWin=='Y'">{{ $t('cusEntry.biddingSettings.bidResultTip1') }}</span>
      <span v-else>{{ $t('cusEntry.biddingSettings.bidResultTip2') }}</span>
      <div style="margin-top: 15px;">
        <SrmCommonFile
          :default-file="{
            fileId: winOrLoss.noticeAttachmentId,
            fileName: winOrLoss.noticeAttachmentName
          }"
          readonly
        />
      </div>
    </div>
    <div slot="footer" class="dialog-footer">
      <el-button @click="dialogVisible = false">
        {{ $t('common.close') }}
      </el-button>
    </div>
  </srm-dialog>
</template>
<script>
/**
 * 查看投标结果
 */
export default {
  name: 'BidResultDialog',
  props: {
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
    visible: {
      type: Boolean,
      default: false
    },
    // 只读
    readonly: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      isWin: null, // 是否中标
      winOrLoss: {
        noticeAttachmentId: null,
        noticeAttachmentName: null
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
  created () {
    this.getBidResult()
  },
  methods: {
    // 查看招标结果
    getBidResult () {
      this.$http({
        url: `/api-sou/ext/vendor/bid/getBidNoticeDetail?projectId=${this.baseInfo.id}`,
        method: 'GET',
        loading: true
      }).then(res => {
        if (res && res.data) {
          this.isWin = res.data.isWin
          this.winOrLoss = res.data.winOrLossNotice
        }
      })
    }
  }
}
</script>
<style lang="scss" scoped>
</style>
