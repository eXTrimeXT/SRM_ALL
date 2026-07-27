<template>
  <div class="bidding-control">
    <el-form label-position="left" disabled>
      <SrmRow>
        <!--本轮需投标的供应商数量-->
        <SrmCol :init-col="3">
          <el-form-item :label="$t('bidMod.currentRoundSupplierCount')">
            <el-input v-model="biddingConForm.inviteCount" />
          </el-form-item>
        </SrmCol>

        <!--已提交投标供应商-->
        <SrmCol :init-col="3">
          <el-form-item :label="$t('bidMod.submitSupplierCount')">
            <el-input v-model="biddingConForm.orderCount" />
          </el-form-item>
        </SrmCol>

        <!--本轮投标截止时间-->
        <SrmCol :init-col="3">
          <el-form-item :label="$t('bidMod.endTime')">
            <el-date-picker
              v-model="biddingConForm.orderEndTime"
              :format="$formatDatePickerTime"
              value-format="yyyy-MM-dd HH:mm:ss"
            />
          </el-form-item>
        </SrmCol>
      </SrmRow>
    </el-form>

    <div style="padding: 5px 5px 8px 0">
      <!-- 立即开始投标 投标开始时间 > 当前时间 -->
      <el-button
        v-if="isShowStartBiddingButton"
        type="primary"
        :disabled="readonly"
        @click="startBidding"
      >
        <!-- 立即开始投标 -->
        {{ $t("bidMod.startBidding") }}
      </el-button>

      <!-- 发起新一轮 评选中 -->
      <el-button
        v-if="isShowStartNewRound"
        type="primary"
        :disabled="readonly"
        @click="startNewRoundDialogVisible = true"
      >
        <!-- 发起新一轮 -->
        {{ $t("bidMod.biddingControl.startNewRound") }}
      </el-button>

      <template v-if="isShowHandleEndTimeButton">
        <!-- 调整截至时间 -->
        <el-button
          type="primary"
          :disabled="readonly"
          @click="adjustDeadlineDialogVisible = true"
        >
          {{ $t("bidMod.adjustDeadline") }}
        </el-button>

        <!-- 立即结束投标 -->
        <el-button
          type="primary"
          :disabled="readonly"
          @click="endImmediatelyDoBidding"
        >
          <!-- 立即结束投标 -->
          {{ $t("bidMod.endBidding") }}
        </el-button>

        <!--刷新-->
        <el-button :disabled="readonly" @click="getOrders">
          {{ $t("common.refresh") }}
        </el-button>
      </template>
    </div>

    <el-table
      :data="orderInfos"
      style="width: 100%"
      border
      height="400px"
      highlight-current-row
    >
      <el-table-column
        type="index"
        :label="$t('common.sort')"
        width="50"
      />

      <!--轮次-->
      <el-table-column
        prop="round"
        :label="$t('bidMod.bidingRound')"
        width="80"
        show-overflow-tooltip
      />

      <!--供应商编码-->
      <el-table-column
        prop="vendorCode"
        :label="$t('bidMod.vendorCode')"
        width="120"
        show-overflow-tooltip
      />

      <!--供应商名称-->
      <el-table-column
        prop="vendorName"
        :label="$t('bidMod.vendorName')"
        min-width="150"
        show-overflow-tooltip
      />

      <!--联系人-->
      <el-table-column
        prop="linkManName"
        :label="$t('bidMod.linkMan')"
        width="100"
        show-overflow-tooltip
      />

      <!--电话-->
      <el-table-column
        prop="phone"
        :label="$t('bidMod.phone')"
        width="100"
        show-overflow-tooltip
      />

      <!--电子邮箱-->
      <el-table-column
        prop="email"
        :label="$t('bidMod.email')"
        width="180"
        show-overflow-tooltip
      />

      <!--是否代理投标-->
      <el-table-column
        prop="isProxy"
        :label="$t('bidMod.isProxy')"
        width="150"
        :formatter="(row, column, cellValue) => $getDictLabel('YES_OR_NO', cellValue)"
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
        prop="orderStatus"
        :label="$t('bidMod.orderStatus')"
        width="100"
        show-overflow-tooltip
        :formatter="(row, column, cellValue) => $getDictLabel('SOU_ORDER_STATUS', cellValue)"
      />

      <!--提交时间-->
      <el-table-column
        prop="submitTime"
        :label="$t('bidMod.lastUpdateDate2')"
        width="150"
        show-overflow-tooltip
        :formatter="(row, column, cellValue) => $parseTime(cellValue)"
      />

      <el-table-column
        fixed="right"
        :label="$t('bidMod.operation')"
        width="120"
      >
        <template v-slot="{ row }">
          <!--代理投标-->
          <el-button
            v-if="judgeProxyQuote(row.orderStatus, projectStatus)"
            type="text"
            :disabled="readonly"
            @click="openProxyQuoteDialog(row)"
          >
            {{ $t("bid_mod.proxyQuoteHandle") }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 发起新一轮 -->
    <StartNewRoundDialog
      v-if="startNewRoundDialogVisible"
      :visible.sync="startNewRoundDialogVisible"
      :project-id="biddingBase.projectId"
      @success="startNewRoundSuccess"
    />

    <!-- 调整截止时间 -->
    <AdjustDeadlineDialog
      v-if="adjustDeadlineDialogVisible"
      :visible.sync="adjustDeadlineDialogVisible"
      :project-id="biddingBase.projectId"
      @success="adjustDeadlineSuccess"
    />

    <!--代理投标-->
    <ProxyQuoteDialog
      v-if="proxyQuoteDialogVisible"
      :visible.sync="proxyQuoteDialogVisible"
      :proxy-quote-params="proxyQuoteParams"
      @success="getOrders"
    />
  </div>
</template>

<script>
/**
 * 投标控制
 */
import { bidBuyerHttp } from 'modb@/bidding/api'
import { SOU_PROJECT_STATUS_ENUM } from 'lib@/composition/origin/enum'
import { judgeManagement, judgeProxyQuote } from 'lib@/composition/biddingLts/utils'
import StartNewRoundDialog from './bidingControl/startNewRoundDialog'
import AdjustDeadlineDialog from './bidingControl/adjustDeadlineDialog'
import ProxyQuoteDialog from './bidingControl/proxyQuoteDialog'

export default {
  name: 'BidingControl',

  components: {
    StartNewRoundDialog,
    AdjustDeadlineDialog,
    ProxyQuoteDialog
  },

  props: {
    biddingBase: {
      type: Object,
      default: () => ({})
    },
    projectStatus: {
      // 招标状态
      type: String,
      default: ''
    },
    // 审批状态
    createApprovalStatus: {
      type: String,
      default: ''
    },
    isActiveMenu: {
      type: Boolean,
      required: true
    }
  },

  data () {
    return {
      biddingConForm: {
        inviteCount: '',
        orderCount: '',
        orderEndTime: ''
      },
      orderInfos: [],
      startNewRoundDialogVisible: false,
      adjustDeadlineDialogVisible: false,
      proxyQuoteDialogVisible: false,
      proxyQuoteParams: {
        visible: false
      },
      judgeProxyQuote
    }
  },

  computed: {
    readonly () {
      // 项目状态=='拟定' && 审批状态=='草稿、审批中'
      return judgeManagement(this.projectStatus, this.createApprovalStatus)
    },
    isShowStartBiddingButton () {
      // 报名截止 投标未开始
      return [SOU_PROJECT_STATUS_ENUM.SIGN_UP_END, SOU_PROJECT_STATUS_ENUM.ORDER_NOT_START].includes(this.projectStatus)
    },
    isShowHandleEndTimeButton () {
      //  接受投标中
      return this.projectStatus === SOU_PROJECT_STATUS_ENUM.ACCEPT_ORDER
    },
    isShowStartNewRound () {
      return this.biddingBase.projectStatus === SOU_PROJECT_STATUS_ENUM.EVALUATING
    }
  },

  watch: {
    isActiveMenu: {
      handler (val) {
        if (val) {
          this.getOrders()
        }
      },
      immediate: true
    }
  },

  methods: {
    /* 查询数据 */
    async getOrders () {
      const response = await bidBuyerHttp.control.orders(this.biddingBase.projectId)
      if (response && response.data) {
        const {
          currentRound = {},
          orderInfos = []
        } = response.data
        const {
          inviteCount = '',
          orderCount = '',
          orderEndTime = ''
        } = currentRound
        this.biddingConForm = {
          inviteCount,
          orderCount,
          orderEndTime: orderEndTime
        }
        this.orderInfos = orderInfos.concat()
      }
    },

    /* 立即开始投标 */
    async startBidding () {
      const confirmResult = await this.$confirm(this.$t('bidMod.biddingManagementBuyer.confirmStartBiddingTitle'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).catch(() => { /* nothing */ })

      if (confirmResult !== 'confirm') {
        return
      }

      const response = await bidBuyerHttp.control.changeOrderStartTime({
        projectId: this.biddingBase.projectId,
        startNow: true
      })
      if (response) {
        this.$message.success(this.$t('common.successSubmit'))
        // 更新本页签数据
        await this.getOrders()
        // 更新基础数据
        this.$emit('refresh')
      }
    },

    /* 发起新一轮成功 */
    async startNewRoundSuccess () {
      // 更新基础数据
      this.$emit('refresh')
      // 更新本页签数据
      await this.getOrders()
    },

    /* 立即结束投标 */
    async endImmediatelyDoBidding () {
      const confirmResult = await this.$confirm(this.$t('bidMod.biddingManagementBuyer.confirmEndBiddingTitle'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).catch(() => { /* nothing */ })

      if (confirmResult !== 'confirm') {
        return
      }

      const response = await bidBuyerHttp.control.changeOrderEndTime({
        projectId: this.biddingBase.projectId,
        endNow: true
      })
      if (response) {
        // 立即结束投标成功！
        this.$message.success(this.$t('bidMod.endBiddingImmediate'))
        await this.getOrders()
        this.$emit('refresh-process')
        this.$emit('refresh')
      }
    },

    /* 调整时间成功 */
    adjustDeadlineSuccess () {
      this.$emit('refresh')
      this.getOrders()
    },

    /* 打开代理报价弹窗 */
    openProxyQuoteDialog (row) {
      this.proxyQuoteParams = {
        visible: true,
        projectId: this.biddingBase.projectId,
        souName: this.biddingBase.souName,
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
