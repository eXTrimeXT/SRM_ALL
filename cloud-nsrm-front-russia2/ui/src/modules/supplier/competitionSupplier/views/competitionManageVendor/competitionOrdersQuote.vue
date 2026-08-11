<template>
  <el-container class="flex-container" direction="vertical">
    <el-main class="competition-project-quote-wrap">
      <div class="competition-project-quote">
        <div class="progress-wrap">
          <el-steps :active="4">
            <el-step :title="$t('bidMod.published')" />
            <el-step :title="$t('bidMod.apply')" />
            <el-step :title="$t('bidMod.applied')" />
            <el-step :title="$t('bidMod.underQualification')" />
            <el-step :title="$t('bidMod.quote')" />
            <el-step :title="$t('bidMod.completeQuotation')" />
          </el-steps>
        </div>

        <!--竞价大厅顶部内容-->
        <HallContainer :show-countdown-delayed="allowExtendTime === 'Y'">
          <template #countdownRound>
            <CountdownRound :deadline-time="orderEndTime" right @deadline="countdownRoundDeadline">
              <template #title>
                <p class="countdown-round-title">
                  {{ $t('competition.distance') }}<span class="red">{{ $t('components.ocr.diTip') }}{{ baseInfo.currentRound }}{{ $t('common.wheel') }}</span>{{ $t('cusEntry.supplement20250205.biddingEndRemainingTime') }}
                </p>
              </template>
            </CountdownRound>
          </template>

          <template v-if="allowExtendTime === 'Y'" #countdownDelayed>
            <CountdownDelayed
              ref="countdownDelayed"
              :deadline-time="delayedOrderEndTime"
              :showCountdownDelayed="allowExtendTime === 'Y'"
              @deadline="countdownDelayedDeadline"
            />
          </template>

          <template #hallDetailInfo>
            <HallDetailInfo :info-list="infoList" />
          </template>
        </HallContainer>

        <!--操作工具栏-->
        <TimerToolbar
          :show-tips="false"
          role="VENDOR"
          auto
          @refresh="refresh"
        />

        <div class="rank-container">
          <div class="container-left">
            <!-- 物料组件 -->
            <ItemList
              ref="itemList"
              :orderItemList.sync="selfItemList"
              :orderInfo="orderItemInfo"
              :souItemId="curSouItemId"
              @item-click="itemClick"
            />
          </div>

          <div class="container-right">
            <RankingList :info="itemInfo" :orderItemInfo="orderItemInfo" :base-info="baseInfo" />
            <QuotationInfo
              :info.sync="itemInfo"
              :orderInfo="orderItemInfo"
              :base-info="baseInfo"
              :latestOrderNotaxPrice="latestOrderNotaxPrice"
              :readonly="quoteReadonly"
              @refresh="refresh"
              @quotation="quoteOrderPrice"
            />
            <el-tabs v-model="activeName" class="mg-10" @tab-click="tabClick">
              <el-tab-pane :label="$t('competition.priceRanking')" name="first">
                <PriceRanking :base-info="baseInfo" :table-data="itemPriceList.slice(0,5)" />
              </el-tab-pane>
              <el-tab-pane :label="$t('competition.quoteRecord')" name="second">
                <QuoteRecord
                  :table-data="quoteRecorList"
                  :base-info="baseInfo"
                />
              </el-tab-pane>
            </el-tabs>
            <!--附件信息-->
            <FileContainer :order-file-list.sync="orderFileList" :readonly="quoteReadonly" />
          </div>
        </div>
      </div>
    </el-main>
  </el-container>
</template>

<script>
import { carVendorHttp } from 'mods@/competitionSupplier/api'
import { getEvaluateMethodFlag } from 'lib@/composition/competition/utils'
import HallContainer from 'lib@/composition/competition/competitionHallHeader/hallContainer.vue'
import CountdownRound from 'lib@/composition/competition/competitionHallHeader/countdownRound.vue'
import CountdownDelayed from 'lib@/composition/competition/competitionHallHeader/countdownDelayed.vue'
import HallDetailInfo from 'lib@/composition/competition/competitionHallHeader/hallDetailInfo.vue'
import RankingContainer from './competitionOrdersQuote/rankingContainer.vue'
import QuoteContainer from './competitionOrdersQuote/quoteContainer.vue'
import FileContainer from './competitionOrdersQuote/fileContainer.vue'
import TimerToolbar from 'lib@/composition/competition/timerToolbar.vue'
import ItemList from './competitionOrdersQuote/itemList.vue'
import RankingList from './competitionOrdersQuote/rankingList.vue'
import QuotationInfo from './competitionOrdersQuote/quotationInfo'
import PriceRanking from './competitionOrdersQuote/rankingContainer/priceRanking.vue'
import QuoteRecord from './competitionOrdersQuote/quoteContainer/quoteRecord.vue'
import { transformMQL } from 'lib@/utils/util'
import { mapState } from 'vuex'
import { dateFormat } from 'lib@/utils/date-format'

export default {
  name: 'CompetitionOrdersQuote',

  components: {
    HallContainer,
    CountdownRound,
    CountdownDelayed,
    HallDetailInfo,
    RankingContainer,
    QuoteContainer,
    FileContainer,
    TimerToolbar,
    ItemList,
    RankingList,
    QuotationInfo,
    PriceRanking,
    QuoteRecord
  },

  data () {
    return {
      projectId: null,
      selfItemList: [],
      orderLineList: [],
      baseInfo: {

      },
      rankingContainerData: {
        price: [],
        totalPrice: []
      },
      viewRow: null,
      orderFileList: [],
      timer: null,
      rankingContainerLoading: false,
      delayedOrderEndTime: '',
      orderEndTime: '',
      allowExtendTime: 'N',
      priceRankingItem: '',
      isCountdownRoundDeadline: false,
      isDeadline: false,
      activeName: 'first',
      originPriceList: [],
      auctSouItemHisList: [],
      quoteRecorList: [], // 报价历史
      curSouItemId: null // 物料列表当前的souItemId
    }
  },

  computed: {
    ...mapState({
      userInfo: state => state.user.userInfo
    }),
    infoList () {
      const projectInfo = this.baseInfo || {}
      return [
        {
          label: this.$t('bidMod.bidClosingTime'),
          value: projectInfo.orderEndTime || '-',
          enable: true
        },
        {
          label: this.$t('bidMod.competitionLts.openRules'),
          value: this.$getDictLabel('SOU_AUCT_SCOPE_RULE', projectInfo.scopeRule) || '-',
          enable: true
        },
        {
          label: this.$t('bidMod.extendBidTrigger'),
          value: projectInfo.extendTrigger ? `${projectInfo.extendTrigger}${this.$t('bidMod.minute')}` : '-',
          enable: true
        },
        {
          label: this.$t('bidMod.competitionLts.competitionRules'),
          value: this.$getDictLabel('SOU_AUCT_RULE', projectInfo.auctRule) || '-',
          enable: true
        },
        {
          label: this.$t('bidMod.competitionLts.floating'),
          value: projectInfo.minPercent || '-',
          enable: !!projectInfo.minPercent
        },
        {
          label: this.$t('bidMod.competitionLts.floatingAmount'),
          value: projectInfo.minAmount || '-',
          enable: !!projectInfo.minAmount
        },
        {
          label: this.$t('competition.extendMaxOrderCount'),
          value: projectInfo.extendMaxOrderCount || '-',
          enable: true
        },
        {
          label: this.$t('bidMod.bidingCurrency2'),
          value: this.$getDictLabel('currency', projectInfo.standardCurrency) || '-',
          enable: true
        },
        {
          label: this.$t('bidMod.competitionLts.delayMinutes'),
          value: projectInfo.extendMinute ? `${projectInfo.extendMinute}${this.$t('bidMod.minute')}` : '-',
          enable: true
        },
        {
          label: this.$t('bidMod.evaluateMethod'),
          value: this.$getDictLabel('SOU_AUCT_SCORE_RULE_TYPE', projectInfo.scoreRuleType) || '-',
          enable: true
        }
      ]
    },

    quoteReadonly () {
      return this.isDeadline
    },
    itemPriceList () {
      return this.originPriceList.filter(item => item.souItemId === this.curSouItemId)
    },
    latestOrderNotaxPrice () {
      return this.auctSouItemHisList.find(item => item.souItemId === this.curSouItemId)?.latestOrderNotaxPrice
    },
    orderItemInfo () { // 报价相关信息
      return this.itemPriceList.find(item => item.vendorId === this.userInfo.companyId) || {}
    },
    itemInfo () { // 询价物料信息
      return this.selfItemList.find(item => item.souItemId === this.curSouItemId) || {}
    }
  },

  async created () {
    this.projectId = this.baseInfo.projectId = this.$attrs.params.row.projectId || null
    this.getOrderDetails()
  },

  methods: {
    /* 排名tab切换 */
    tabClick (val) {
      if (this.activeName === 'second') {
        this.getHisRanking()
      }
    },

    /* 获取报价详情 */
    async getOrderDetails () {
      let transformParams = transformMQL.save('AuctSouOrderForVendor', [{ projectId: this.projectId }], 'getSouOrderInfo')
      const response = await carVendorHttp.orderQuotation.getInitDetail(transformParams)

      if (response && response.data) {
        const {
          auctSouItemHisList = [], // 获取当前最新价
          orderItemList = [], // 实时排名列表
          orderFileList = [], // 附件列表
          souInitInfo = {}, // 立项信息
          ...rest
        } = response.data.records[0]

        /** 立项信息处理 */
        this.baseInfo = JSON.parse(JSON.stringify(Object.assign({}, souInitInfo, souInitInfo.auctSouProject)))
        let { itemList, originOrderEndTime, orderEndTime, allowExtendTime, extendTrigger } = this.baseInfo
        this.allowExtendTime = allowExtendTime

        this.selfItemList = (itemList || []).map(item => ({ // 左边物料列表
          ...item,
          ...item.auctSouItem
        }))

        if (!this.curSouItemId && this.selfItemList.length) { // 界面初始化，物料默认高亮显示第一个
          this.curSouItemId = this.selfItemList[0].souItemId
        }

        for (let item of this.selfItemList) {
          let curItemObj = orderItemList.find(innerItem => innerItem.souItemId === item.souItemId && innerItem.vendorId === this.userInfo.companyId)
          if (curItemObj) {
            item.auctRanking = curItemObj.auctRanking // 排名
            item.taxKey = curItemObj.taxKey // 税率
          }
        }

        /** 实时报价列表 */
        this.originPriceList = JSON.parse(JSON.stringify(orderItemList.map(item => ({
          ...item,
          ...item.auctSouItem,
          ...item.auctSouOrderItem
        }))))

        this.auctSouItemHisList = auctSouItemHisList

        this.orderFileList = orderFileList

        if (allowExtendTime === 'Y') { // 允许延时竞价
          // 系统时间
          let sysTimeNum = new Date().getTime()
          // 延长时间节点（原始报价截止时间 - 延时竞价触发点）
          let extendTimeNum = new Date(originOrderEndTime).getTime() - extendTrigger * 60 * 1000
          // 原始报价截止时间
          let originOrderEndTimeNum = new Date(originOrderEndTime).getTime()
          // 后台返回动态延时时间
          let orderEndTimeNum = new Date(orderEndTime).getTime()

          if (orderEndTimeNum > originOrderEndTimeNum) { // 延时中
            this.orderEndTime = dateFormat(new Date())
            this.delayedOrderEndTime = orderEndTime
            this.$nextTick(() => {
              this.$refs.countdownDelayed.delayed()
            })
          } else { // 没有延时
            this.orderEndTime = orderEndTime
            this.$nextTick(() => {
              this.$refs.countdownDelayed.clearTimer()
            })
          }

          // 系统时间大于等于后台返回的动态延时时间证明已截止了
          if (sysTimeNum >= orderEndTimeNum) {
            this.orderEndTime = dateFormat(new Date())
            this.isDeadline = true
            this.$nextTick(() => {
              this.$refs.countdownDelayed.clearTimer()
            })
          }
        } else { // 不允许延时竞价
          this.orderEndTime = originOrderEndTime
        }
      }
    },
    /* 查询历史排名 */
    async getHisRanking () {
      let params = {
        filter: {
          projectId: { eq: this.projectId },
          vendorId: { eq: this.userInfo.companyId },
          round: { eq: this.baseInfo.currentRound }
        },
        page: { sort: 'creationDate desc' }
      }
      let transformParams = transformMQL.save('AuctSouOrderItemHis', params, 'query')
      let response = await carVendorHttp.orderQuotation.getHisRanking(transformParams)
      if (response?.data?.records.length) {
        this.quoteRecorList = response.data.records.slice(0, 5).map(item => ({
          ...item,
          ...item.orderItemInfo,
          ...item.orderItemInfo?.auctSouOrderItem
        }))
      }
    },

    /* 物料切换 */
    itemClick (row) {
      this.curSouItemId = row.souItemId
    },

    /* 报价 */
    async quoteOrderPrice (price) {
      const { souItemId, orderId, taxKey, priceStartTime, priceEndTime } = this.itemInfo
      const { orderItemId } = this.orderItemInfo
      if (!taxKey) {
        this.$message.warning(this.$t('bidMod.selectTaxRate'))
        return
      }
      if (!price) {
        this.$message.warning(this.$t('competition.priceTip'))
        return
      }
      let params = {
        projectId: this.projectId,
        orderItemList: [{
          souItemId,
          orderId,
          orderItemId,
          taxKey,
          priceStartTime,
          priceEndTime,
          orderNotaxPrice: price
        }],
        orderFileList: this.orderFileList,
        'tempSave': false
      }
      console.log('params', params)
      let transformParams = transformMQL.save('AuctSouOrderForVendor', [params], 'editOrder')
      let response = await carVendorHttp.orderQuotation.editOrder(transformParams)
      if (response?.data) {
        this.$message.success(this.$t('competition.orderQuotationSuccess'))
        this.getOrderDetails()
      }
    },

    /* 竞价截止时间 */
    countdownRoundDeadline () {
      if (this.allowExtendTime !== 'Y') {
        // 已截止
        this.isDeadline = true
      }
      this.isCountdownRoundDeadline = true
      this.refresh()
    },

    /* 延时竞价已截止 */
    countdownDelayedDeadline () {
      if (this.isCountdownRoundDeadline) {
        // 已截止
        this.isDeadline = true
      }
      this.refresh()
    },

    /* 刷新 */
    refresh () {
      this.getOrderDetails()
    },

    /* 提交成功 */
    submitSuccess () {
      // 更新时间，更新排名数据，更新当前数据
      this.getOrderDetails()
    }
  }
}
</script>

<style scoped lang="scss">
.competition-project-quote {
  padding-bottom: 20px;
}
.competition-project-quote ::v-deep {
  .el-tabs__item.is-active {
    background: none !important;
  }

  .progress-wrap {
    padding: 11px;
    margin: 10px 0;
    height: auto;
    .el-steps {
      padding-bottom: 0;
    }
  }

  .countdown-round-title {
    margin-top: 5px;
    margin-bottom: 3px;
    font-size: 14px;
    .red {
      color: red;
    }
  }

  .evaluate-method-wrap {
    padding: 11px;
    .el-row {
      margin-bottom: 11px;
      font-size: 16px;
      span {
        padding-right: 11px;
        display: inline-block;
        color: #999;
      }
    }
  }

  .details-info-wrap {
    padding: 5px;
    .label {
      padding: 0 11px;
      line-height: 30px
    }
    .refresh-tips {
      text-align: center;
      color: red;
      font-size: 14px;
    }
  }

  .rank-container {
    display: flex;

    .container-left {
      width: 25%;
      min-width: 250px;
      border-right: 1px solid #E4E4E4;
      padding-right: 8px;
    }
    .container-right {
      width: 80%;
      padding-left: 8px;
    }
  }
}
.mg-10 {
  margin: 10px 0;
}
</style>
