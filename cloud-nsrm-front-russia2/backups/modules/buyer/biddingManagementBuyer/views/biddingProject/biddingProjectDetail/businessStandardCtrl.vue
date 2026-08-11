<template>
  <div>
    <p style="margin: 10px 0;">
      <!-- 商务开标 项目状态为投标截止 or 技术评标 -->
      <el-button
        v-if="isBusinessBiding"
        type="primary"

        class="detail-pbtn"
        :disabled="isDisabledTable"
        @click="openBusinessBiding"
      >
        {{ $t("bidMod.openBusinessBiding") }}
      </el-button>

      <!--报价解密 密封报价才需要解密-->
      <el-button
        v-if="isNeedEncryptPrice"
        type="primary"

        class="detail-pbtn"
        :disabled="isDisabledTable"
        @click="decryptBid"
      >
        报价解密
      </el-button>
    </p>

    <el-table
      :data="businessItemList"
      style="width: 100%"
      border
      height="400px"
      highlight-current-row
    >
      <el-table-column
        align="center"
        type="index"
        width="40"
      />
      <!--轮次-->
      <el-table-column
        align="center"
        prop="round"
        :label="$t('bidMod.bidingRound')"
        width="60"
        show-overflow-tooltip
      />

      <!--供应商编码-->
      <el-table-column
        align="center"
        prop="vendorCode"
        :label="$t('bidMod.vendorCode')"
        width="120"
        show-overflow-tooltip
      />

      <!--供应商名称-->
      <el-table-column
        align="center"
        prop="vendorName"
        :label="$t('bidMod.vendorName')"
        min-width="150"
        show-overflow-tooltip
      />

      <!--供应商IP-->
      <el-table-column
        align="center"
        prop="quotationIp"
        label="供应商IP"
        width="100"
        show-overflow-tooltip
      >
        <template v-slot="{ row }">
          <span :style="{ color: row.ipWarn ? 'red' : '' }">{{ row.quotationIp }}</span>
        </template>
      </el-table-column>

      <!--投标详情-->
      <el-table-column
        align="center"
        prop="bidDetail"
        :label="$t('bidMod.bidDetail')"
        width="150"
        show-overflow-tooltip
      >
        <template slot-scope="scope">
          <!--(密封报价 && 已解密) || 非密封报价-->
          <el-button
            v-if="(isNeedEncryptPrice && isDecryptFlag) || !isNeedEncryptPrice"
            type="text"
            @click="openBiddingDetailDialog(scope.row)"
          >
            {{ scope.row.bidOrderNum }}
          </el-button>
        </template>
      </el-table-column>

      <!--提交日期-->
      <el-table-column
        align="center"
        prop="submitTime"
        :label="$t('bidMod.submitDate')"
        width="150"
        show-overflow-tooltip
      />

      <!--作废原因-->
      <el-table-column
        align="center"
        prop="rejectReason"
        :label="$t('bidMod.rejectReason')"
        width="120"
        show-overflow-tooltip
      />

      <!--商务附件-->
      <el-table-column
        align="center"
        :label="$t('bidMod.businessAttch')"
        width="100"
        show-overflow-tooltip
      >
        <template slot-scope="scope">
          <!-- 查看 -->
          <el-button
            type="text"
            @click="openBusinessFileDialog(scope.row)"
          >
            {{ $t("common.view") }}
          </el-button>
        </template>
      </el-table-column>

      <el-table-column
        align="center"
        :label="$t('bidMod.operation')"
        width="130"
        fixed="right"
      >
        <template slot-scope="scope">
          <!-- 作废投标 非查看 && 未作废 && [接受投标中、投标截止、技术评标、商务评标] -->
          <el-button
            v-if="
              !isViewOrApproval &&
                scope.row.orderStatus !== 'INVALID' &&
                ['ACCEPT_BID', 'TENDER_ENDING', 'TECHNICAL_EVALUATION', 'BUSINESS_EVALUATION'].includes(bidingStatus)
            "
            type="text"
            @click="cancelBiding(scope.row)"
          >
            {{ $t("bidMod.cancelBiding") }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!--商务附件-->
    <business-file-dialog
      :visible.sync="businessFileDialogVisible"
      :edit-row="editRow"
    />

    <!--投标详情-->
    <bidding-detail-dialog
      :visible.sync="biddingDetailDialogVisible"
      :edit-row="editRow"
    />
  </div>
</template>

<script>
/**
 * 商务标管理
 */
import { judgeListRepeatValueWarnTag } from 'lib@/composition/origin/composition'
import businessFileDialog from './businessStandardCtrl/businessFileDialog'
import biddingDetailDialog from './businessStandardCtrl/biddingDetailDialog'

export default {
  name: 'TechnicalStandardCtrl',
  components: {
    businessFileDialog,
    biddingDetailDialog
  },
  props: {
    scopeBidingId: {
      // 招标ID
      type: [Number, String],
      default () {
        return ''
      }
    },
    bidingStatus: {
      // 招标状态
      type: String,
      default () {
        return ''
      }
    },
    auditStatus: {
      // 审批状态
      type: String,
      default () {
        return ''
      }
    },
    isViewOrApproval: {
      type: Boolean,
      required: true
    },
    // 投标基础信息
    bidingBase: {
      type: Object,
      default () {
        return {}
      }
    },
    // 投标基础信息
    projectInformationData: {
      type: Object,
      default () {
        return {}
      }
    },
    isActiveMenu: {
      type: Boolean
    }
  },
  data () {
    return {
      businessItemList: [],
      editRow: null,
      businessFileDialogVisible: false,
      biddingDetailDialogVisible: false
    }
  },
  computed: {
    userId () {
      return this.$store.getters.userInfo.userId || ''
    },
    isDisabledTable () {
      // 项目状态=='拟定' && 审批状态=='草稿、审批中'
      return this.bidingStatus === 'DRAW_UP' &&
        ['DRAFT', 'SUBMITTED'].includes(this.auditStatus)
    },
    // 是否显示商务开标
    isBusinessBiding () {
      return !this.isDisabledTable && ['TECHNICAL_EVALUATION', 'TENDER_ENDING'].includes(this.bidingBase.bidingStatus)
    },
    // 是否显示密封报价
    isNeedEncryptPrice () {
      // 需要解密、未解密，在工作小组中且有解密权限
      return this.bidingBase.needEncryptPrice === 'Y' &&
        !this.isDecryptFlag &&
        (this.projectInformationData.groupList || []).find(item => item.userId === this.userId && item.canDecrypt === 'Y')
    },
    // 是否已解密报价
    isDecryptFlag () {
      return this.bidingBase.decryptFlag === 'Y'
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
    /* 查询数据 */
    getBusinessOrders () {
      this.$http({
        url: `/api-bid/businessProposal/queryBusinessOrders/${this.scopeBidingId}`,
        method: 'GET',
        loading: true
      }).then(data => {
        if (data && data.data) {
          this.businessItemList = judgeListRepeatValueWarnTag((data.data || []), 'quotationIp', 'ipWarn')
        }
      })
    },

    /* 商务开标 */
    openBusinessBiding () {
      this.$confirm('确定发起商务开标吗？', {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        this.$http({
          url: `/api-bid/businessProposal/openBid/${this.scopeBidingId}`,
          method: 'POST',
          loading: true
        }).then(() => {
          this.$message.success(this.$t('common.successSubmit'))

          // 更新节点信息
          this.$emit('fetchParentNodeData')
          // 更新招标基础数据
          this.$emit('fetchBaseInfo')
        })
      })
    },

    /* 解密报价 */
    decryptBid () {
      this.$http({
        url: `/api-bid/businessProposal/decryptBid/${this.scopeBidingId}`,
        method: 'POST',
        loading: true
      }).then(() => {
        this.$t('common.successSubmit')

        // 更新招标基础数据
        this.$emit('fetchBaseInfo')
      })
    },

    /* 打开投标详情弹窗 */
    openBiddingDetailDialog (row) {
      this.editRow = row
      this.biddingDetailDialogVisible = true
    },

    /* 打开商务附件弹窗 */
    openBusinessFileDialog (row) {
      this.editRow = row
      this.businessFileDialogVisible = true
    },

    /* 作废投标 */
    cancelBiding (row) {
      this.$prompt(this.$t('bidMod.abandonBidReason'), this.$t('bidMod.cancelBiding'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        inputValidator: value => !(!value || value.length === 0),
        inputErrorMessage: this.$t('logisticsMod.msgInvalidReason')
      }).then(({ value }) => {
        this.$http({
          url: '/api-bid/businessProposal/withdrawOrder',
          method: 'POST',
          data: {
            orderHeadId: row.orderHeadId,
            rejectReason: value
          },
          loading: true
        }).then(() => {
          this.$message.success(this.$t('bidMod.abandonBidSuccess')) // 废弃投标成功
          this.getBusinessOrders()
        })
      })
    },

    /* 驳回投标 */
    delQuote (row) {
      let query = {}
      query.bidVendorId = row.bidVendorId
      query.round = row.round
      this.$http({
        url: '/api-bid/businessProposal/removeOrderInfo',
        method: 'GET',
        params: query,
        loading: true
      }).then(() => {
        this.$message.success(this.$t('bidMod.successDelQuote'))
        this.getBusinessOrders(this.scopeBidingId)
      })
    }
  }
}
</script>
