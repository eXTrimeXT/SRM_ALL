<template>
  <srm-dialog
    :title="$t('vendorMod.loggerComment')"
    :visible.sync="dialogVisible"
    size="middle"
    style="text-align: center"
  >
    <el-input
      v-model="inputComment"
      type="textarea"
      :rows="4"
      maxlength="1000"
      show-word-limit
      :placeholder="$t('vendorMod.pleaseApproval')"
    />
    <div class="topComment">
      <el-button @click="dialogVisible = false">
        {{ $t("common.cancel") }}
      </el-button>
      <el-button
        type="primary"
        @click="commentForm"
      >
        {{ $t("common.confirm") }}
      </el-button>
    </div>
  </srm-dialog>
</template>

<script>
/**
 * 起草人意见弹窗
 */
import { getSaveAndSubmitApiUrl } from '../composition'

export default {
  name: 'LoggerCommentDialog',
  props: {
    visible: Boolean,
    pageType: Object,
    approvalHeader: Object,
    approvalFileList: Array,
    approvalBiddingItemList: Array,
    callBackConfirm: Function
  },
  data () {
    return {
      inputComment: ''
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
    commentForm () {
      const allParams = {
        approvalHeader: {
          ...this.approvalHeader,
          drafterOpinion: this.inputComment || this.$t('vendorMod.pleaseApproval')
        },
        approvalFileList: this.approvalFileList,
        approvalBiddingItemList: this.approvalBiddingItemList
      }

      this.$http({
        url: getSaveAndSubmitApiUrl(this.pageType).submitApi,
        method: 'POST',
        data: allParams,
        loading: true
      }).then((res) => {
        this.dialogVisible = false
        this.$message.success(this.$t('common.successSave'))
        this.callBackConfirm({ message: this.inputComment, approvalHeaderId: res.data.approvalHeaderId })
        this.$emit('saveLoggerCommentSuccess')
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.topComment {
  margin-top: 15px;
  float: right;
}
</style>
