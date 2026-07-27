<template>
  <div class="bidding-control">
    <el-form
      label-position="left"
      disabled
    >
      <srm-row>
        <!--本轮需报价的供应商数量-->
        <srm-col :init-col="3">
          <el-form-item :label="$t('bidMod.currentRoundSupplierCount1')">
            <el-input v-model="bargainConForm.currentRoundVendorCount" />
          </el-form-item>
        </srm-col>

        <!--已提交报价供应商-->
        <srm-col :init-col="3">
          <el-form-item :label="$t('bidMod.submitSupplierCount1')">
            <el-input v-model="bargainConForm.currentRoundSubmitVendorCount" />
          </el-form-item>
        </srm-col>

        <!--本轮报价截止时间-->
        <srm-col :init-col="3">
          <el-form-item :label="$t('bidMod.endTime1')">
            <el-input v-model="bargainConForm.bargainEndTime" />
          </el-form-item>
        </srm-col>
      </srm-row>
    </el-form>

    <div style="padding: 5px 5px 8px 0">
      <!-- 立即开始报价 投标开始时间 > 当前时间 -->
      <el-button
        v-if="isShowStartBargainButton"
        type="primary"
        :disabled="isDisabledTable"
        class="detail-pbtn"
        @click="startBargain"
      >
        立即开始报价
      </el-button>

      <!-- 发起新一轮 评选中 -->
      <el-button
        v-if="bargainBase.bargainStatus === 'EVALUATE_ING'"
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

        <!-- 立即结束报价 -->
        <el-button
          type="primary"
          class="detail-pbtn"
          :disabled="isDisabledTable"
          @click="endImmediatelyDoBargain"
        >
          {{ $t("bidMod.immediateClose") }}
        </el-button>
      </template>
    </div>

    <el-table
      :data="brgControlItemList"
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
        prop="isProxyBargain"
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
        :label="$t('bidMod.orderStatus1')"
        width="100"
        show-overflow-tooltip
      >
        <template v-slot="scope">
          <span>{{ $getDictLabel('BRG_ORDER_STATUS', scope.row.orderStatus) }}</span>
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
      @startBargain="startBargainSubmit"
    />

    <!-- 调整截止时间 -->
    <adjust-deadline-dialog
      :visible.sync="adjustDeadlineDialogVisible"
      :bargain-id="scopeBargainId"
      @adjustDeadlineSuccess="getBargainControlDetailData"
    />

    <!--代理报价-->
    <proxy-quote-dialog
      v-if="proxyQuoteDialogVisible"
      :visible.sync="proxyQuoteDialogVisible"
      :proxy-quote-params="proxyQuoteParams"
      @proxyQuoteSuccess="getBargainControlDetailData"
    />
  </div>
</template>

<script>
/**
 * 报价控制
 */
import startNewRoundDialog from './bargainControl/startNewRoundDialog'
import adjustDeadlineDialog from './bargainControl/adjustDeadlineDialog'
import proxyQuoteDialog from './bargainControl/proxyQuoteDialog'

export default {
  name: 'BargainControl',
  components: {
    startNewRoundDialog,
    adjustDeadlineDialog,
    proxyQuoteDialog
  },
  props: {
    scopeBargainId: {
      // 招标ID
      type: [Number, String],
      default () {
        return ''
      }
    },
    bargainBase: {
      type: Object,
      default () {
        return {}
      }
    },
    bargainStatus: {
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
      bargainConForm: {
        currentRoundVendorCount: '',
        currentRoundSubmitVendorCount: '',
        bargainEndTime: ''
      },
      brgControlItemList: [],
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
      return this.bargainStatus === 'DRAW_UP' && ['DRAFT', 'SUBMITTED'].includes(this.auditStatus)
    },
    isShowStartBargainButton () {
      // 报名截止 投标未开始
      return ['SIGNUP_DONE', 'BRG_NOT_START'].includes(this.bargainStatus)
    },
    isShowHandleEndTimeButton () {
      //  接受投标中
      return this.bargainStatus === 'ACCEPT_BRG'
    }
  },
  methods: {
    /* 查询数据 */
    getBargainControlDetailData () {
      this.$api.brg.inquiryByProject.getBrgControlInfo(this.scopeBargainId).then(data => {
        if (data && data.data) {
          this.bargainConForm = {
            currentRoundVendorCount: data.data.currentRoundVendorCount,
            currentRoundSubmitVendorCount: data.data.currentRoundSubmitVendorCount,
            bargainEndTime: data.data.bargainEndTime
          }
          this.brgControlItemList = data.data.vendorBargainInfos || []
        }
      })
    },

    /* 立即开始投标 */
    startBargain () {
      this.$confirm(this.$t('bidMod.biddingManagementBuyer.confirmStartBiddingTitle'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        // 确认
        this.startBargainSubmit()
      })
    },

    /* 立即结束投标 */
    endImmediatelyDoBargain () {
      this.$confirm(this.$t('bidMod.biddingManagementBuyer.confirmEndBiddingTitle'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        // 确认
        this.$api.brg.inquiryByProject.changeBargainEndTime({
          bargainId: this.scopeBargainId,
          bargainEndTime: new Date(Date.now()),
          endNow: 'Y'
        }).then(() => {
          this.$message.success(this.$t('bidMod.endBiddingImmediate'))
          this.$emit('updateProcessNode', 'bargainControl')
          this.$emit('fetchBaseInfo')
          this.getBargainControlDetailData(this.scopeBargainId)
        })
      })
    },

    /* 提交立即发起投标 / 发起新一轮 */
    startBargainSubmit (type, form) {
      let params = {
        bargainId: this.scopeBargainId
      }
      if (type === 'newRound') {
        // 新一轮
        params = {
          ...params,
          bargainStartTime: form.bargainStartTime,
          bargainEndTime: form.bargainEndTime
        }
      }
      this.$api.brg.inquiryByProject.startBargain(params).then(() => {
        this.$message.success(this.$t('common.successSubmit'))
        // 更新本页签数据
        this.getBargainControlDetailData()
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
        bargainId: this.scopeBargainId,
        bargainName: this.bargainBase.bargainName,
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

.the_biding_control_row {
  padding: 3px;
  :deep(.el-col) {
    & > span {
      line-height: 30px;
      padding-right: 10px;
      white-space: nowrap;
      text-overflow: ellipsis;
      overflow: hidden;
    }
    & > .el-input {
      width: 125px;
    }
  }
}
</style>
