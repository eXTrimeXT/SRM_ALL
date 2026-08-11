<template>
  <!-- 投标控制 -->
  <div class="bidding-control">
    <el-form
      label-position="left"
      disabled
    >
      <srm-row>
        <!--本轮需投标的供应商数量-->
        <srm-col :init-col="3">
          <el-form-item :label="$t('bidMod.currentRoundSupplierCount')">
            <el-input v-model="bidingConForm.currentRoundVendorCount" />
          </el-form-item>
        </srm-col>

        <!--已提交投标供应商-->
        <srm-col :init-col="3">
          <el-form-item :label="$t('bidMod.submitSupplierCount')">
            <el-input v-model="bidingConForm.currentRoundSubmitVendorCount" />
          </el-form-item>
        </srm-col>

        <!--本轮投标截止时间-->
        <srm-col :init-col="3">
          <el-form-item :label="$t('bidMod.endTime1')">
            <el-input v-model="bidingConForm.bidingEndTime" />
          </el-form-item>
        </srm-col>
      </srm-row>
    </el-form>

    <div style="padding: 5px 5px 8px 0">
      <!-- 立即开始投标 投标开始时间 > 当前时间 -->
      <el-button
        v-if="isShowStartBiddingButton"
        type="primary"

        :disabled="isDisabledTable"
        class="detail-pbtn"
        @click="startBidding"
      >
        立即开始投标
      </el-button>

      <!-- 发起新一轮 评选中 -->
      <el-button
        v-if="bidingBase.bidingStatus === 'EVALUATE_ING'"
        type="primary"

        :disabled="isDisabledTable"
        class="detail-pbtn"
        @click="startNewRoundDialogVisible = true"
      >
        发起新一轮
      </el-button>

      <template v-if="isShowHandleEndTimeButton">
        <!-- 调整截至时间 -->
        <el-button
          type="primary"

          class="detail-pbtn"
          :disabled="isDisabledTable"
          @click="adjustDeadlineDialogVisible = true"
        >
          {{ $t("bidMod.adjustDeadline") }}
        </el-button>

        <!-- 立即结束投标功能 -->
        <el-button
          type="primary"

          class="detail-pbtn"
          :disabled="isDisabledTable"
          @click="endImmediatelyDoBidding"
        >
          {{ $t("bidMod.endBidding") }}
        </el-button>
      </template>
    </div>

    <el-table
      :data="bidControlItemList"
      style="width: 100%"
      border
      height="400px"
      highlight-current-row
    >
      <el-table-column
        align="center"
        type="index"
        width="50"
      />

      <!--轮次-->
      <el-table-column
        align="center"
        prop="round"
        :label="$t('bidMod.bidingRound')"
        width="80"
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

      <!--联系人-->
      <el-table-column
        align="center"
        prop="linkManName"
        :label="$t('bidMod.linkMan')"
        width="100"
        show-overflow-tooltip
      />

      <!--电话-->
      <el-table-column
        align="center"
        prop="phone"
        :label="$t('bidMod.phone')"
        width="100"
        show-overflow-tooltip
      />

      <!--电子邮箱-->
      <el-table-column
        align="center"
        prop="email"
        :label="$t('bidMod.email')"
        width="180"
        show-overflow-tooltip
      />

      <!--是否代理报价-->
      <el-table-column
        align="center"
        prop="isProxyBidding"
        :label="$t('bid_mod.isProxyBidding')"
        width="150"
        :formatter="(...rest) => $getDictLabel('YES_OR_NO', rest[2])"
        show-overflow-tooltip
      />

      <!--授权证明-->
      <SrmCommonFile
        type="table-column"
        :table-column-options="{
          label: '授权证明',
          prop: 'proxyDocId',
          nameProp: 'proxyFileName'
        }"
        readonly
      />

      <!--投标状态-->
      <el-table-column
        align="center"
        prop="orderStatus"
        :label="$t('bidMod.orderStatus')"
        width="100"
        show-overflow-tooltip
      >
        <template v-slot="scope">
          <span>{{ $getDictLabel('BIDDING_ORDER_STATES', scope.row.orderStatus) }}</span>
        </template>
      </el-table-column>

      <!--提交时间-->
      <el-table-column
        align="center"
        prop="submitTime"
        :label="$t('bidMod.lastUpdateDate2')"
        width="150"
        show-overflow-tooltip
      />

      <el-table-column
        align="center"
        fixed="right"
        :label="$t('bidMod.operation')"
        width="120"
      >
        <template v-slot="{ row }">
          <!--代理报价 未投标、撤回-->
          <el-button
            v-if="['DRAFT', 'WITHDRAW'].includes(row.orderStatus)"
            type="text"
            :disabled="isDisabledTable"
            @click="openProxyQuoteDialog(row)"
          >
            {{ $t("bid_mod.proxyQuoteHandle") }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 发起新一轮 -->
    <start-new-round-dialog
      :visible.sync="startNewRoundDialogVisible"
      @startBiding="startBiding"
    />

    <!-- 调整截止时间 -->
    <adjust-deadline-dialog
      :visible.sync="adjustDeadlineDialogVisible"
      :biding-id="scopeBidingId"
      @adjustDeadlineSuccess="getBiddingControlDetailData"
    />

    <!--代理报价-->
    <proxy-quote-dialog
      v-if="proxyQuoteDialogVisible"
      :visible.sync="proxyQuoteDialogVisible"
      :proxy-quote-params="proxyQuoteParams"
      @proxyQuoteSuccess="getBiddingControlDetailData"
    />
  </div>
</template>

<script>
/**
 * 投标控制
 */
import startNewRoundDialog from './biddingControl/startNewRoundDialog'
import adjustDeadlineDialog from './biddingControl/adjustDeadlineDialog'
import proxyQuoteDialog from './biddingControl/proxyQuoteDialog'

export default {
  name: 'BiddingControl',
  components: {
    startNewRoundDialog,
    adjustDeadlineDialog,
    proxyQuoteDialog
  },
  props: {
    scopeBidingId: {
      // 招标ID
      type: [Number, String],
      default () {
        return ''
      }
    },
    bidingBase: {
      type: Object,
      default () {
        return {}
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
    }
  },
  data () {
    return {
      bidingConForm: {
        currentRoundVendorCount: '',
        currentRoundSubmitVendorCount: '',
        bidingEndTime: ''
      },
      bidControlItemList: [],
      startNewRoundDialogVisible: false,
      adjustDeadlineDialogVisible: false,
      proxyQuoteDialogVisible: false,
      proxyQuoteParams: {
        visible: false
      }
    }
  },
  computed: {
    isDisabledTable () {
      // 项目状态=='拟定' && 审批状态=='草稿、审批中'
      return this.bidingStatus === 'DRAW_UP' && ['DRAFT', 'SUBMITTED'].includes(this.auditStatus)
    },
    isShowStartBiddingButton () {
      // 报名截止 投标未开始
      return ['SIGNUP_DONE', 'BID_NOT_START'].includes(this.bidingStatus)
    },
    isShowHandleEndTimeButton () {
      //  接受投标中
      return this.bidingStatus === 'ACCEPT_BID'
    }
  },
  methods: {
    /* 查询数据 */
    getBiddingControlDetailData () {
      this.$http({
        url: `/api-bid/bidControl/getBidControlInfo/${this.scopeBidingId}`,
        method: 'GET',
        loading: true
      }).then(data => {
        if (data && data.data) {
          this.bidingConForm = {
            currentRoundVendorCount: data.data.currentRoundVendorCount,
            currentRoundSubmitVendorCount: data.data.currentRoundSubmitVendorCount,
            bidingEndTime: data.data.bidingEndTime
          }
          this.bidControlItemList = data.data.vendorBidingInfos || []
        }
      })
    },

    /* 立即开始投标 */
    startBidding () {
      this.$confirm(this.$t('bidMod.biddingManagementBuyer.confirmStartBiddingTitle'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        // 确认
        this.startBiding()
      })
    },

    /* 立即结束投标 */
    endImmediatelyDoBidding () {
      this.$confirm(this.$t('bidMod.biddingManagementBuyer.confirmEndBiddingTitle'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        // 确认
        this.$http({
          url: '/api-bid/bidControl/changeBidingEndTime',
          method: 'POST',
          data: {
            bidingId: this.scopeBidingId,
            bidingEndTime: new Date(Date.now()),
            endNow: 'Y'
          },
          loading: true
        }).then(() => {
          this.$message.success(this.$t('bidMod.endBiddingImmediate'))
          this.$emit('updateProcessNode', 'bidingControl')
          this.$emit('fetchBaseInfo')
          this.getBiddingControlDetailData(this.scopeBidingId)
        })
      })
    },

    /* 提交立即发起投标 / 发起新一轮 */
    startBiding (type, form) {
      let params = {
        bidingId: this.scopeBidingId
      }
      if (type === 'newRound') {
        // 新一轮
        params = {
          ...params,
          bidingStartTime: form.bidingStartTime,
          bidingEndTime: form.bidingEndTime
        }
      }
      this.$http({
        url: '/api-bid/bidControl/startBiding',
        method: 'POST',
        data: params,
        loading: true
      }).then(() => {
        this.$message.success(this.$t('common.successSubmit'))
        // 更新本页签数据
        this.getBiddingControlDetailData()
        // 更新基础数据
        this.$emit('fetchBaseInfo')
        if (type === 'newRound') {
          this.startNewRoundDialogVisible = false
        }
      })
    },

    /* 打开代理报价弹窗 */
    openProxyQuoteDialog (row) {
      this.proxyQuoteParams = {
        visible: true,
        bidingId: this.scopeBidingId,
        bidingName: this.bidingBase.bidingName,
        vendorId: row.vendorId
      }
      this.proxyQuoteDialogVisible = true
    }
  }
}
</script>

<style lang="scss" scoped>
.bidding-control {
  padding-top: 8px;
}
</style>
