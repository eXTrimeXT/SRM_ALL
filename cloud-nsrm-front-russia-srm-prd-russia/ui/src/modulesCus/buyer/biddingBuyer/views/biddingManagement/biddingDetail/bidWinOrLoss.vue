<template>
  <div>
    <div style="margin: 16px 0">
      <span style="font-size:14px; font-weight:bold">查看中/落标通知</span>
    </div>
    <el-table
      border
      max-height="200"
      style="width: 100%"
      :data="noticeDetailList"
      highlight-current-row
    >
      <el-table-column
        align="center"
        type="index"
        fixed="left"
        :label="$t('common.sort')"
        width="55"
      />
      <el-table-column
        align="center"
        prop="vendorCode"
        :label="$t('bidMod.vendorCode')"
        min-width="150"
        show-overflow-tooltip
      />
      <el-table-column
        align="center"
        prop="vendorName"
        :label="$t('bidMod.vendorName')"
        min-width="150"
        show-overflow-tooltip
      />
      <el-table-column
        align="center"
        prop="isWin"
        label="是否中标"
        min-width="100"
        :formatter="(row, column, cellValue) => $getDictLabel('YES_OR_NO', cellValue)"
        show-overflow-tooltip
      />
      <el-table-column
        align="center"
        prop="winAmount"
        label="中标金额（万元）"
        min-width="150"
        show-overflow-tooltip
      />
      <el-table-column
        align="center"
        prop="contractSignUnit"
        label="合同签署单位"
        min-width="150"
        show-overflow-tooltip
      />
      <el-table-column
        align="center"
        prop="contractPeriod"
        label="合同周期"
        min-width="150"
        show-overflow-tooltip
      />
      <el-table-column
        align="center"
        prop="noticeAttachmentId"
        label="通知书附件"
        min-width="180"
        show-overflow-tooltip
      >
        <template slot-scope="scope">
          <SrmCommonFile
            :default-file="{
              fileId: scope.row.noticeAttachmentId,
              fileName: scope.row.noticeAttachmentName
            }"
            readonly
          />
        </template>
      </el-table-column>
      <el-table-column
        align="center"
        prop="isSend"
        label="通知书是否发送"
        min-width="150"
        :formatter="(row, column, cellValue) => $getDictLabel('YES_OR_NO', cellValue)"
        show-overflow-tooltip
      />
    </el-table>

    <div style="margin: 30px 0 16px 0;font-size:14px; font-weight:bold">
      查看内部通知
    </div>

    <el-table
      border
      max-height="200"
      style="width: 100%"
      :data="noticeInternalList"
      highlight-current-row
    >
      <el-table-column
        align="center"
        type="index"
        fixed="left"
        :label="$t('common.sort')"
        width="55"
      />
      <el-table-column
        align="center"
        prop="extOrgBuName"
        label="板块"
        min-width="150"
        show-overflow-tooltip
      />
      <el-table-column
        align="center"
        prop="extOrgOuName"
        label="公司"
        min-width="150"
        show-overflow-tooltip
      />
      <el-table-column
        align="center"
        prop="demandDepartmentName"
        label="需求部门"
        min-width="150"
        show-overflow-tooltip
      />
      <el-table-column
        align="center"
        prop="attachmentId"
        label="通知书附件"
        min-width="180"
        show-overflow-tooltip
      >
        <template slot-scope="scope">
          <SrmCommonFile
            :default-file="{
              fileId: scope.row.attachmentId,
              fileName: scope.row.attachmentName
            }"
            readonly
          />
        </template>
      </el-table-column>
      <el-table-column
        align="center"
        prop="isSend"
        label="通知书是否发送"
        min-width="150"
        show-overflow-tooltip
      />
    </el-table>
  </div>
</template>

<script>
import { bidBuyerHttp } from 'modcb@/biddingBuyer/api'
import { judgeManagement } from '@/library/composition/biddingLts/utils'

export default {
  name: 'BidWinOrLoss',

  props: {
    projectStatus: {
      // 招标状态
      type: String,
      default: ''
    },
    createApprovalStatus: {
      // 审批状态
      type: String,
      default: ''
    },
    biddingBase: {
      type: Object,
      default: () => ({})
    },
    isActiveMenu: {
      type: Boolean,
      required: true
    }
  },

  data () {
    return {
      noticeDetailList: [],
      noticeInternalList: []
    }
  },

  computed: {
    readonly () {
      // 项目状态=='拟定' && 审批状态=='草稿、审批中'
      return judgeManagement(this.projectStatus, this.createApprovalStatus)
    }
  },

  watch: {
    isActiveMenu: {
      handler (val) {
        if (val) {
          this.getWinLossNotice()
        }
      },
      immediate: true
    }
  },

  methods: {
    /* 查询数据 */
    getWinLossNotice () {
      bidBuyerHttp.notice.getNotice(this.biddingBase.projectId).then(res => {
        if (res && res.data) {
          this.noticeDetailList = res.data.noticeDetailList
          this.noticeInternalList = res.data.noticeInternalList
        }
      })
    }
  }
}
</script>
<style lang="scss" scoped>
</style>
