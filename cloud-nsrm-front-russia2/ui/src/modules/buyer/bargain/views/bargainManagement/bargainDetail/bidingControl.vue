<template>
  <div class="bidding-control">
    <el-form label-position="left" disabled>
      <SrmRow>
        <!--本轮需报价的供应商数量-->
        <SrmCol :init-col="3">
          <el-form-item :label="$t('bidMod.currentRoundSupplierCount1')">
            <el-input v-model="bargainConForm.inviteCount" />
          </el-form-item>
        </SrmCol>

        <!--已提交报价供应商-->
        <SrmCol :init-col="3">
          <el-form-item :label="$t('bidMod.submitSupplierCount1')">
            <el-input v-model="bargainConForm.orderCount" />
          </el-form-item>
        </SrmCol>

        <!--本轮报价截止时间-->
        <SrmCol :init-col="3">
          <el-form-item :label="$t('bidMod.endTime1')">
            <el-date-picker
              v-model="bargainConForm.orderEndTime"
              :format="$formatDatePickerTime"
              value-format="yyyy-MM-dd HH:mm:ss"
            />
          </el-form-item>
        </SrmCol>
      </SrmRow>
    </el-form>

    <div style="padding: 5px 5px 8px 0">
      <!-- 立即开始报价 投标开始时间 > 当前时间 -->
      <el-button
        v-if="isShowStartBargainButton"
        type="primary"
        :disabled="readonly"
        @click="startBargain"
      >
        <!-- 立即开始报价 -->
        {{ $t("key") }}
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

        <!-- 立即结束报价 -->
        <el-button
          type="primary"
          :disabled="readonly"
          @click="endImmediatelyDoBargain"
        >
          {{ $t("bidMod.immediateClose") }}
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
        prop="isProxy"
        :label="$t('bid_mod.isProxyBidding')"
        width="150"
        :formatter="(row, column, cellValue) => $getDictLabel('YES_OR_NO', cellValue)"
      />

      <!--授权证明-->
      <SrmCommonFile
        type="table-column"
        :table-column-options="{
          label: $t('bidMod.proxyFileName'),
          prop: 'proxyDocId',
          nameProp: 'proxyFileName'
        }"
        readonly
      />

      <!--投标状态-->
      <el-table-column
        align="center"
        prop="orderStatus"
        :label="$t('bidMod.orderStatus1')"
        width="100"
        show-overflow-tooltip
        :formatter="(row, column, cellValue) => $getDictLabel('SOU_ORDER_STATUS', cellValue)"
      />

      <!--提交时间-->
      <el-table-column
        align="center"
        prop="submitTime"
        :label="$t('bidMod.lastUpdateDate2')"
        width="150"
        :formatter="(row, column, cellValue) => $parseTime(cellValue)"
        show-overflow-tooltip
      />

      <el-table-column
        align="center"
        fixed="right"
        :label="$t('bidMod.operation')"
        width="120"
      >
        <template v-slot="{ row }">
          <!--代理报价-->
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
      :project-id="bargainBase.projectId"
      @success="startNewRoundSuccess"
    />

    <!-- 调整截止时间 -->
    <AdjustDeadlineDialog
      v-if="adjustDeadlineDialogVisible"
      :visible.sync="adjustDeadlineDialogVisible"
      :project-id="bargainBase.projectId"
      @success="adjustDeadlineSuccess"
    />

    <!--代理报价-->
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
 * 报价控制
 */
import { brgBuyerHttp } from 'modb@/bargain/api'
import { SOU_PROJECT_STATUS_ENUM } from 'lib@/composition/origin/enum'
import { judgeManagement, judgeProxyQuote } from 'lib@/composition/bargainLts/utils'
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
    bargainBase: {
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
      bargainConForm: {
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
    isShowStartBargainButton () {
      // 报名截止 投标未开始
      return [SOU_PROJECT_STATUS_ENUM.SIGN_UP_END, SOU_PROJECT_STATUS_ENUM.ORDER_NOT_START].includes(this.projectStatus)
    },
    isShowHandleEndTimeButton () {
      //  接受投标中
      return this.projectStatus === SOU_PROJECT_STATUS_ENUM.ACCEPT_ORDER
    },
    isShowStartNewRound () {
      return this.bargainBase.projectStatus === SOU_PROJECT_STATUS_ENUM.EVALUATING
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
      const response = await brgBuyerHttp.control.orders(this.bargainBase.projectId)
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
        this.bargainConForm = {
          inviteCount,
          orderCount,
          orderEndTime: orderEndTime
        }
        this.orderInfos = orderInfos.concat()
      }
    },

    /* 立即开始投标 */
    async startBargain () {
      const confirmResult = await this.$confirm(this.$t('bidMod.biddingManagementBuyer.confirmStartBiddingTitle'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).catch(() => { /* nothing */ })

      if (confirmResult !== 'confirm') {
        return
      }

      const response = await brgBuyerHttp.control.changeOrderStartTime({
        projectId: this.bargainBase.projectId,
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
    async endImmediatelyDoBargain () {
      const confirmResult = await this.$confirm(this.$t('bidMod.biddingManagementBuyer.confirmEndBiddingTitle'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).catch(() => { /* nothing */ })

      if (confirmResult !== 'confirm') {
        return
      }

      const response = await brgBuyerHttp.control.changeOrderEndTime({
        projectId: this.bargainBase.projectId,
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
        projectId: this.bargainBase.projectId,
        souName: this.bargainBase.souName,
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
