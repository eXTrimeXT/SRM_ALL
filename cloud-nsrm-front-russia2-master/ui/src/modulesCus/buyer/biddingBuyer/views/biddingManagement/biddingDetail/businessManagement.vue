<template>
  <div style="padding-top: 10px">
    <p style="margin: 10px 0">
      <!-- 组织商务标 -->
      <el-button
        type="primary"
        :disabled="readonly"
        @click="businessBid"
      >
        {{ $t('cusEntry.bidMod.businessBid') }}
      </el-button>
      <!-- 商务开标 -->
      <!-- 招标专家、区域负责人才能点击商务开标 -->
      <!-- 1.11 新加校验：简易招标流程，未确认评标应不可开商务标 -->
      <el-button
        type="primary"
        :disabled="readonly || userDisabled || (biddingBase.extSouProcess==='SIMPLE' && biddingBase.extConfirmFlag !== 'Y')"
        @click="businessOpen"
      >
        {{ $t("bidMod.openBusinessBiding") }}
      </el-button>
      <!-- 查看比价 -->
      <el-button
        type="primary"
        :disabled="readonly"
        @click="openPriceComparison"
      >
        {{ $t('cusEntry.bidMod.priceComparison') }}
      </el-button>
      <!-- 下载商务附件 -->
      <el-button
        type="primary"
        :disabled="readonly || !quoteFlag"
        @click="downLoadBusFile"
      >
        {{ $t('cusEntry.supplement20250121.downloadBusinessFile') }}
      </el-button>
      <!--刷新-->
      <!-- <el-button @click="getBusinessOrders">
        {{ $t('common.refresh') }}
      </el-button> -->
    </p>

    <el-table
      border
      style="width: 100%"
      max-height="380"
      :data="businessItemList"
      highlight-current-row
    >
      <!--轮次-->
      <el-table-column
        align="center"
        prop="round"
        :label="$t('bidMod.bidingRound')"
        fixed="left"
        width="60"
        show-overflow-tooltip
      />

      <!--供应商编码-->
      <el-table-column
        align="center"
        prop="vendorCode"
        :label="$t('bidMod.vendorCode')"
        min-width="120"
        show-overflow-tooltip
      />

      <!--供应商名称-->
      <el-table-column
        align="center"
        prop="vendorName"
        :label="$t('bidMod.vendorName')"
        min-width="120"
        show-overflow-tooltip
      />

      <!-- 投标次数 -->
      <el-table-column
        align="center"
        prop="tenderTimes"
        :label="$t('cusEntry.supplement20250121.tenderTimes')"
        min-width="100"
        show-overflow-tooltip
      />

      <!-- 含税总价(卢布) -->
      <!-- <el-table-column
        align="center"
        prop="extTaxAmount"
        :label="$t('cusEntry.supplement20250121.extTaxAmount')"
        min-width="120"
        show-overflow-tooltip
      >
        <template v-slot="{ row }">
          <span v-if="quoteFlag">{{ row.extTaxAmount }} </span>
        </template>
      </el-table-column> -->

      <!-- 未税总价（卢布） -->
      <el-table-column
        align="center"
        prop="extNoTaxAmount"
        :label="$t('cusEntry.supplement20250205.totalPriceExcludingTaxRUB')"
        min-width="120"
        show-overflow-tooltip
      >
        <template v-slot="{ row }">
          <span v-if="quoteFlag">{{ row.extNoTaxAmount }} </span>
        </template>
      </el-table-column>

      <!--投标详情-->
      <el-table-column
        align="center"
        prop="orderId"
        :label="$t('bidMod.bidDetail')"
        min-width="100"
        show-overflow-tooltip
      >
        <template v-slot="{ row }">
          <el-button
            type="text"
            @click="openBiddingDetailDialog(row)"
          >
            {{ $t('common.view') }}
          </el-button>
        </template>
      </el-table-column>

      <!--商务附件-->
      <el-table-column
        align="center"
        prop="orderId"
        :label="$t('bidMod.businessAttch')"
        min-width="100"
        show-overflow-tooltip
      >
        <template v-slot="{ row }">
          <!-- 查看 -->
          <el-button type="text" @click="openBusinessFileDialog(row)">
            {{ $t('common.view') }}
          </el-button>
        </template>
      </el-table-column>

      <!--投标状态-->
      <el-table-column
        align="center"
        prop="orderStatus"
        :label="$t('bidMod.orderStatus')"
        :formatter="(row, column, cellValue) => $getDictLabel('SOU_ORDER_STATUS', cellValue)"
        min-width="100"
        show-overflow-tooltip
      />
      <!-- 投标时间 -->
      <el-table-column
        align="center"
        prop="submitTime"
        :label="$t('cusEntry.biddingSettings.bidTime')"
        min-width="120"
        :formatter="(row, column, cellValue) => $parseTime(cellValue)"
        show-overflow-tooltip
      />

      <!--组织报价原因-->
      <el-table-column
        align="center"
        prop="extOrderReason"
        :label="$t('cusEntry.bidMod.extOrderReason')"
        min-width="150"
        show-overflow-tooltip
      />
    </el-table>

    <div style="margin: 16px 0">
      <!-- 谈判资料 -->
      <span style="font-size:14px; font-weight:bold">{{ $t('cusEntry.bidMod.negotiationInfo') }}</span>
    </div>
    <el-button
      type="primary"
      style="margin-bottom:10px"
      @click="addRow"
    >
      {{ $t('common.add') }}
    </el-button>
    <el-button
      type="primary"
      @click="saveList"
    >
      {{ $t('common.save') }}
    </el-button>
    <el-table
      border
      max-height="180"
      style="width: 100%"
      :data="negotiationList"
      highlight-current-row
    >
      <el-table-column
        align="center"
        type="index"
        fixed="left"
        :label="$t('common.sort')"
        width="60"
      />
      <el-table-column
        align="center"
        prop="souFileName"
        :label="$t('bidMod.fileName')"
        min-width="150"
      >
        <template slot-scope="scope">
          <SrmCommonFile
            :extra-data="fileInfo"
            :default-file="{
              fileId: scope.row.souDocId,
              fileName: scope.row.souFileName
            }"
            :readonly="readonly"
            @on-change="({file}) => handleUploadSuccess(file,scope.row)"
          />
        </template>
      </el-table-column>
      <el-table-column
        align="center"
        prop="souRemark"
        :label="$t('common.remark')"
        min-width="150"
      >
        <template slot-scope="scope">
          <el-input v-model="scope.row.souRemark" />
        </template>
      </el-table-column>
      <el-table-column
        align="center"
        :label="$t('common.operation')"
        width="100"
      >
        <template slot-scope="scope">
          <el-button type="text" @click="deleteRow(scope.$index)">
            {{ $t('common.delete') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!--商务附件-->
    <BusinessFileDialog
      v-if="businessFileDialogVisible"
      :visible.sync="businessFileDialogVisible"
      :edit-row="editRow"
      :merge-flag="biddingBase.mergeFlag"
    />

    <!--报价详情-->
    <BiddingDetailDialog
      v-if="biddingDetailDialogVisible"
      :visible.sync="biddingDetailDialogVisible"
      :edit-row="editRow"
      :merge-flag="biddingBase.mergeFlag"
    />

    <!-- 组织商务标 -->
    <BusinessBidDialog
      v-if="businessBidVisible"
      :visible.sync="businessBidVisible"
      :projectId="biddingBase.projectId"
      @success="businessBidSuccess"
    />
  </div>
</template>

<script>
/**
 * 商务标管理
 */
import { bidBuyerHttp } from 'modcb@/biddingBuyer/api'
import { judgeListRepeatValueWarnTag } from 'lib@/composition/origin/composition'
import { SOU_PROJECT_STATUS_ENUM, SOU_ORDER_STATUS_ENUM } from 'lib@/composition/origin/enum'
import { judgeManagement } from '@/library/composition/biddingLts/utils'
import BusinessFileDialog from './businessManagement/businessFileDialog'
import BiddingDetailDialog from './businessManagement/biddingDetailDialog'
import BusinessBidDialog from './businessManagement/businessBidDialog'
import PriceComparison from './businessManagement/priceComparison'
import { downloadFileLink } from 'lib@/utils/file'

export default {
  name: 'BusinessManagement',

  components: {
    BusinessFileDialog,
    BiddingDetailDialog,
    BusinessBidDialog
  },

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
    // 投标基础信息
    biddingBase: {
      type: Object,
      default: () => ({})
    },
    // 投标基础信息
    projectInfoData: {
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
      fileInfo: {
        uploadType: 'DEF', // 固定
        sourceType: 'WEB_APP', // 固定
        fileModular: 'base', // 模块
        fileFunction: 'BID_NEGOTIATE', // 功能
        fileType: 'images' // 类型
      },
      busOpenUserList: [], // 可以点击商务开标的角色（招标专家+区域负责人）
      businessItemList: [],
      negotiationList: [],
      businessBidVisible: false,
      editRow: null,
      businessFileDialogVisible: false,
      biddingDetailDialogVisible: false,
      SOU_ORDER_STATUS_ENUM,
      SOU_PROJECT_STATUS_ENUM
    }
  },

  computed: {
    // 商务标未开标，不可查看报价信息(查看比价、投标详情、商务附件), 不可下载商务附件
    quoteFlag () {
      return ['BUS_BID_OPEN', 'CONFIRM_BID', 'WIN_LOSS_NOTICE', 'NOTICE_ING', 'ARCHIVE_TODO', 'ARCHIVE_DONE'].includes(this.biddingBase.projectStatus)
    },
    userId () {
      return this.$store.getters.userInfo.userId || ''
    },
    readonly () {
      // 项目状态=='拟定' && 审批状态=='草稿、审批中'
      return judgeManagement(this.projectStatus, this.createApprovalStatus)
    },
    // 是否可点击商务开标
    userDisabled () {
      let busOpenUserList = this.busOpenUserList.map(item => item.userId)
      return (
        !busOpenUserList.includes(this.userId) ||
        this.busOpenUserList.some(item => item.userId === this.userId && item.openStatus === 'COMPLETED')
      )
    }
  },

  watch: {
    isActiveMenu: {
      handler (val) {
        if (val) {
          this.getBusinessOrders()
        }
      },
      immediate: true
    }
  },

  methods: {
    // 点击组织商务标
    businessBid () {
      // 收标方式为：先收技术后收商务 && 确认评标后（技术标所有评标完成后）才可以点击
      if (this.biddingBase.extSouMode === 'TECH_THEN_BUS' && this.biddingBase.extConfirmFlag !== 'Y') {
        // 确认评标之后才可组织商务标
        this.$message.error(this.$t('cusEntry.supplement20250121.bidTips10'))
        return
      }
      // 收标方式为：同时收标 && 单据状态：商务已开标 才可以点击
      if (this.biddingBase.extSouMode === 'SAME_TIME' && this.biddingBase.projectStatus !== 'BUS_BID_OPEN') {
        // 非商务已开标，不可组织商务标
        this.$message.error(this.$t('cusEntry.supplement20250121.bidTips11'))
        return
      }
      this.businessBidVisible = true
    },

    businessBidSuccess () {
      // 更新节点信息
      this.$emit('refresh-process')
      // 更新招标基础数据
      this.$emit('refresh')
      // 更新当前页面数据
      this.getBusinessOrders()
    },

    addRow () {
      this.negotiationList.push({
        souDocId: null,
        souFileName: '',
        souRemark: ''
      })
    },
    deleteRow (index) {
      this.negotiationList.splice(index, 1)
    },
    handleUploadSuccess (file, row) {
      const { fileId = '', fileName = '' } = file || {}
      row.souDocId = fileId
      row.souFileName = fileName
    },
    saveList () {
      let flag = this.negotiationList.some(item => !item.souDocId)
      if (flag) {
        this.$message.error(this.$t('bidMod.pleaseUploadFile'))
        return
      }
      const params = {
        projectId: this.biddingBase.projectId,
        talkFileList: this.negotiationList
      }
      bidBuyerHttp.control.saveTalkFile(params).then(res => {
        this.$message.success(res.message)
        this.getBusinessOrders()
      })
    },

    /* 查询数据 */
    getBusinessOrders () {
      bidBuyerHttp.control.businessOrders(this.biddingBase.projectId).then(res => {
        if (res && res.data) {
          this.businessItemList = res.data.orderList
          this.negotiationList = res.data.talkFileList
          this.busOpenUserList = res.data.openUserList
        }
      })
    },

    /* 商务开标 */
    async businessOpen () {
      const confirmResult = await this.$confirm(this.$t('cusEntry.bidMod.confirmOpenBusBid'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).catch(() => { /* nothing */ })

      if (confirmResult !== 'confirm') {
        return
      }

      const response = await bidBuyerHttp.control.businessOpen(this.biddingBase.projectId)
      if (response) {
        this.$message.success(response.message)

        // 更新节点信息
        this.$emit('refresh-process')
        // 更新招标基础数据
        this.$emit('refresh')
        // 更新当前页面数据
        this.getBusinessOrders()
      }
    },

    // 查看比价
    openPriceComparison () {
      // 商务标未开标，不可查看报价信息
      if (!this.quoteFlag) {
        this.$message.error(this.$t('cusEntry.supplement20250121.bidTips12'))
        return
      }
      let tab = {
        component: PriceComparison,
        params: {
          projectId: this.biddingBase.projectId,
          tabName: 'PriceComparison' + this.biddingBase.projectId
        },
        title: this.biddingBase.extProjectNo + '-' + this.$t('bidMod.priceCompareList'),
        name: 'PriceComparison' + this.biddingBase.projectId
      }
      this.$emit('tab-add', tab)
    },

    /* 打开投标详情弹窗 */
    openBiddingDetailDialog (row) {
      // 商务标未开标，不可查看报价信息
      if (!this.quoteFlag) {
        this.$message.error(this.$t('cusEntry.supplement20250121.bidTips12'))
        return
      }
      this.editRow = row
      this.biddingDetailDialogVisible = true
    },

    /* 打开商务附件弹窗 */
    openBusinessFileDialog (row) {
      // 商务标未开标，不可查看报价信息
      if (!this.quoteFlag) {
        this.$message.error(this.$t('cusEntry.supplement20250121.bidTips12'))
        return
      }
      this.editRow = row
      this.businessFileDialogVisible = true
    },

    // 下载商务附件
    downLoadBusFile () {
      // 文件已开始下载，请耐心等待，或进入浏览器的下载界面查看下载进度。
      this.$confirm(this.$t('cusEntry.supplement20250121.promptTips6'), this.$t('common.tips'), {
        showCancelButton: false
      }).then(() => {})
      downloadFileLink(
        `/api-sou/ext/buyer/bid/init/downloadBusinessFile?projectId=${this.biddingBase.projectId}`,
        // `招标项目[${this.biddingBase.extProjectNo}]商务附件.zip`
        this.$t('cusEntry.supplement20250121.bidTips13', { extProjectNo: this.biddingBase.extProjectNo })
      ).catch(res => {
        this.$message.error(res.message)
      })
    }
  }
}
</script>
