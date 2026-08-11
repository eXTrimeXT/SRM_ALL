<template>
  <div class="competition-hall">
    <HallContainer :show-countdown-delayed="allowExtendTime === 'Y'">
      <template #countdownRound>
        <CountdownRound
          v-bind="countdownRoundAttrsProps"
          @deadline="countdownRoundDeadline"
        >
          <template #right>
            <div class="countdown-round-right">
              <!--调整时间-->
              <el-button
                v-if="!showStartQuoteButton"
                type="primary"
                @click="adjustmentTimeDialogVisible = true"
                :disabled="!activeAdjustmentTimeButton"
              >
                调整时间
              </el-button>

              <!--开始报价-->
              <el-button
                v-if="showStartQuoteButton"
                type="primary"
                @click="startQuote"
              >
                开始报价
              </el-button>

              <!--结束报价-->
              <el-button
                type="primary"
                :disabled="!activeEndQuoteButton"
                @click="endQuote"
              >
                结束报价
              </el-button>
            </div>
          </template>
        </CountdownRound>
      </template>

      <!-- 延时竞价 -->
      <template v-if="allowExtendTime === 'Y'" #countdownDelayed>
        <CountdownDelayed
          ref="countdownDelayed"
          :deadline-time="delayedOrderEndTime"
          @deadline="countdownDelayedDeadline"
        />
      </template>

      <!-- 竞价规则信息 -->
      <template #hallDetailInfo>
        <HallDetailInfo :info-list="infoList" />
      </template>
    </HallContainer>

    <!--操作工具栏-->
    <TimerToolbar auto :round="baseInfo.currentRound" @refresh="autoRefreshHall" @change-round="changeRound" />

    <!--物料维度的报价汇总-->
    <HallRequireQuote
      v-loading="hallRequireQuoteLoading"
      :base-info="baseInfo"
      :page-info="pageInfo"
      :item-list="itemList.slice((pageInfo.pageNum-1)*pageInfo.pageSize,pageInfo.pageNum*pageInfo.pageSize)"
      @page-change="hallRequireQuotePageChange"
      @size-change="hallRequireQuoteSizeChange"
    />

    <!--报表-->
    <HallReportTabs
      ref="hallReportTabs"
      :base-info="baseInfo"
      :require-info-data="requireInfoData"
      :vendor-info-data="vendorInfoData"
      :order-item-list="orderItemList"
      :order-infos="orderInfos"
      :round="selectRound"
      style="margin-top: 20px;"
    />

    <!--调整时间-->
    <AdjustmentTimeDialog
      v-if="adjustmentTimeDialogVisible"
      :visible.sync="adjustmentTimeDialogVisible"
      :base-info="baseInfo"
      @success="adjustmentTimeSuccess"
    />
  </div>
</template>

<script>
/**
 * 竞价大厅
 */
import { carBuyerHttp } from 'modb@/competition/api'
import { SOU_AUCT_PROJECT_STATUS_ENUM, getEvaluateMethodFlag } from 'lib@/composition/competition/utils'
import HallContainer from 'lib@/composition/competition/competitionHallHeader/hallContainer.vue'
import CountdownRound from 'lib@/composition/competition/competitionHallHeader/countdownRound.vue'
import CountdownDelayed from 'lib@/composition/competition/competitionHallHeader/countdownDelayed.vue'
import HallDetailInfo from 'lib@/composition/competition/competitionHallHeader/hallDetailInfo.vue'
import TimerToolbar from 'lib@/composition/competition/timerToolbar.vue'
import HallRequireQuote from './competitionHall/hallRequireQuote.vue'
import HallReportTabs from './competitionHall/hallReportTabs.vue'
import AdjustmentTimeDialog from './competitionHall/adjustmentTimeDialog.vue'
import { transformMQL } from 'lib@/utils/util'
import { dateFormat } from 'lib@/utils/date-format'

export default {
  name: 'CompetitionHall',

  components: {
    HallContainer,
    CountdownRound,
    CountdownDelayed,
    HallDetailInfo,
    TimerToolbar,
    HallRequireQuote,
    HallReportTabs,
    AdjustmentTimeDialog
  },

  props: {
    baseInfo: {
      type: Object,
      required: true
    },
    // 是否当前tab页
    isActiveTab: {
      type: Boolean,
      default: false
    },
    // 物料需求数据
    requireInfoData: {
      type: Array,
      default: () => []
    },
    // 邀请供应商数据
    vendorInfoData: {
      type: Array,
      default: () => []
    }
  },

  data () {
    return {
      adjustmentTimeDialogVisible: false,
      compDelayed: 'N',
      delayedOrderEndTime: '',
      orderEndTime: '',
      itemList: [],
      orderItemList: [],
      orderInfos: [],
      pageInfo: {
        total: 0,
        pageNum: 1,
        pageSize: 15
      },
      hallRequireQuoteLoading: false,
      isDeadline: false,
      selectRound: this.baseInfo.currentRound,
      allowExtendTime: 'N'
    }
  },

  computed: {
    infoList () {
      const projectInfo = this.baseInfo || {}
      return [
        {
          label: '竞价截止时间',
          value: projectInfo.orderEndTime || '-',
          enable: true
        },
        {
          label: '公开规则',
          value: this.$getDictLabel('SOU_AUCT_SCOPE_RULE', projectInfo.auctSouProject.scopeRule) || '-',
          enable: true
        },
        {
          label: '延长竞价触发点',
          value: projectInfo.auctSouProject.extendTrigger ? `${projectInfo.auctSouProject.extendTrigger}分钟` : '-',
          enable: true
        },
        {
          label: '竞价规则',
          value: this.$getDictLabel('SOU_AUCT_RULE', projectInfo.auctSouProject.auctRule) || '-',
          enable: true
        },
        {
          label: this.$t('bidMod.competitionLts.floating'),
          value: projectInfo.auctSouProject.minPercent || '-',
          enable: !!projectInfo.auctSouProject.minPercent
        },
        {
          label: this.$t('bidMod.competitionLts.floatingAmount'),
          value: projectInfo.auctSouProject.minAmount || '-',
          enable: !!projectInfo.auctSouProject.minAmount
        },
        {
          label: '每次延时最多可报次数',
          value: projectInfo.auctSouProject.extendMaxOrderCount || '-',
          enable: true
        },
        {
          label: '报价币种',
          value: this.$getDictLabel('currency', projectInfo.standardCurrency) || '-',
          enable: true
        },
        {
          label: '延长竞价时长',
          value: projectInfo.auctSouProject.extendMinute ? `${projectInfo.auctSouProject.extendMinute}分钟` : '-',
          enable: true
        },
        {
          label: '评分规则',
          value: this.$getDictLabel('SOU_AUCT_SCORE_RULE_TYPE', projectInfo.scoreRuleType) || '-',
          enable: true
        }
      ]
    },

    // 调整时间按钮是否可用
    activeAdjustmentTimeButton () {
      return !this.showStartQuoteButton &&
        // 接受竞价中
        this.baseInfo.projectStatus === SOU_AUCT_PROJECT_STATUS_ENUM.ACCEPT_ORDER
    },

    // 显示开始报价按钮
    showStartQuoteButton () {
      // 竞价未开始
      return [
        // 竞价未开始
        SOU_AUCT_PROJECT_STATUS_ENUM.ORDER_NOT_START,
        // 接受报名中
        // SOU_AUCT_PROJECT_STATUS_ENUM.ACCEPT_SIGN_UP,
        // 报名截止
        SOU_AUCT_PROJECT_STATUS_ENUM.SIGN_UP_END
      ].includes(this.baseInfo.projectStatus)
    },

    // 结束报价按钮是否可用
    activeEndQuoteButton () {
      // 接受竞价中
      return this.baseInfo.projectStatus === SOU_AUCT_PROJECT_STATUS_ENUM.ACCEPT_ORDER
    },

    // 截止时间props
    countdownRoundAttrsProps () {
      // 显示开始报价按钮或者接受报名中
      if (this.showStartQuoteButton || this.baseInfo.projectStatus === SOU_AUCT_PROJECT_STATUS_ENUM.ACCEPT_SIGN_UP) {
        return {
          title: this.$t('bidMod.competitionLts.countdownRoundAttrsProps1'), // '距本轮竞价开始仅剩：',
          descLabel: this.$t('bidMod.competitionLts.countdownRoundAttrsProps2'), // '竞价开始时间：',
          deadlineTime: this.baseInfo.orderStartTime
        }
      }
      return {
        title: this.isDeadline ? this.$t('bidMod.competitionLts.countdownRoundAttrsProps3') : this.$t('bidMod.competitionLts.countdownRoundAttrsProps4'), // '竞价已截止' : '距本轮竞价结束仅剩：',
        deadlineTime: this.orderEndTime
      }
    }
  },

  watch: {
    isActiveTab: {
      handler (newValue, oldValue) {
        // 切换到当前标签页
        if (newValue && !oldValue) {
          this.getHallData()
        }
      },
      immediate: true
    }
  },

  methods: {
    /* 获取竞价大厅数据 */
    async getHallData () {
      if (!this.baseInfo.projectId) {
        return
      }

      this.hallRequireQuoteLoading = true

      let transformParams = transformMQL.save('AuctSouProjectForBuyer', [{
        projectId: this.baseInfo.projectId,
        round: this.selectRound,
        showAllVendors: false
      }], 'getControlInfo')
      const response = await carBuyerHttp.control.getControlInfo(transformParams).catch(() => { this.hallRequireQuoteLoading = false })

      this.hallRequireQuoteLoading = false
      if (response && response.data) {
        const {
          currentRound,
          currentRoundSouItemHisMap,
          orderInfos,
          orderItemList,
          project
        } = response.data.records[0] || {}
        const {
          orderEndTime = '',
          originOrderEndTime = '',
          allowExtendTime = 'N',
          itemList = [],
          vendorList = [],
          extendTrigger,
          extendMinute
        } = { ...project, ...project.auctSouProject }

        for (let item of itemList) {
          let obj = currentRoundSouItemHisMap[item.souItemId] || {}
          for (let key in obj) {
            item[key] = obj[key]
          }
        }

        this.pageInfo.total = itemList.length
        this.itemList = itemList
        // 每个物料报价的供应商
        this.itemList.forEach(item => {
          let vendorIdList = this.orderItemList.filter(innerItem => innerItem.souItemId === item.souItemId).map(innerItem => innerItem.vendorId)
          if (vendorIdList.length) {
            item.orderSouVendorList = vendorList.filter(innerItem => vendorIdList.includes(innerItem.vendorId))
          }
        })
        this.orderItemList = orderItemList.map(item => {
          let vendorObj = vendorList.find(innerItem => innerItem.vendorId === item.vendorId) ?? {}
          return {
            ...item,
            ...item.auctSouOrderItem,
            ...vendorObj
          }
        })

        this.orderInfos = orderInfos.map(item => ({
          ...item,
          ...item.auctSouOrder
        }))

        this.allowExtendTime = allowExtendTime // 是否有延时竞价

        if (allowExtendTime === 'Y') { // 允许延时竞价
          // 系统时间
          let sysTimeNum = new Date().getTime()
          // 延长时间节点（原始报价截止时间 - 延时竞价触发点）
          let extendTimeNum = new Date(originOrderEndTime).getTime() - extendTrigger * 60 * 1000
          // 原始报价截止时间
          let originOrderEndTimeNum = new Date(originOrderEndTime).getTime()
          // 后台返回动态延时时间
          let orderEndTimeNum = new Date(orderEndTime).getTime()
          
          if(orderEndTimeNum > originOrderEndTimeNum){ // 延时中
            this.orderEndTime = dateFormat(new Date()) 
            this.delayedOrderEndTime = orderEndTime
            this.$nextTick(() => {
              this.$refs.countdownDelayed.delayed()
            })
          }else{ // 没有延时
            this.orderEndTime = orderEndTime
            this.$nextTick(() => {
              this.$refs.countdownDelayed.clearTimer()
            })
          }

          //系统时间大于等于后台返回的动态延时时间证明已截止了
          if(sysTimeNum >= orderEndTimeNum){
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

    changeRound (round) {
      this.selectRound = round
      this.autoRefreshHall()
    },

    /* 自动刷新竞价大厅数据 */
    autoRefreshHall () {
      this.getHallData()

      if (this.$refs.hallReportTabs) {
        this.$refs.hallReportTabs.refreshItemDetailRanking()
      }
    },

    /* 页码改变 */
    hallRequireQuotePageChange (page) {
      this.pageInfo.pageNum = page
      // this.getHallData()
    },

    /* 页码大小改变 */
    hallRequireQuoteSizeChange (size) {
      this.pageInfo.pageNum = 1
      this.pageInfo.pageSize = size
      // this.getHallData()
    },

    /* 竞价截止时间 */
    countdownRoundDeadline () {
      this.isDeadline = true
      this.$emit('refresh')
      this.$emit('refresh-process')
      this.autoRefreshHall()
    },

    /* 延时竞价已截止 */
    countdownDelayedDeadline () {
      this.$emit('refresh')
      this.$emit('refresh-process')
      this.autoRefreshHall()
    },

    /* 调整截止时间成功 */
    adjustmentTimeSuccess () {
      this.$emit('refresh')
      this.autoRefreshHall()
      this.adjustmentTimeDialogVisible = false
    },

    /* 开始报价 */
    async startQuote () {
      let transformParams = transformMQL.save('AuctSouProjectForBuyer', [{
        projectId: this.baseInfo.projectId,
        startNow: true
      }], 'changeOrderStartTime')
      const response = await carBuyerHttp.order.changeOrderStartTime(transformParams)

      if (response) {
        this.$message.success(this.$t('common.successUpdate'))
        this.$emit('refresh')
        this.$emit('refresh-process')
      }
    },

    /* 结束报价 */
    async endQuote () {
      const confirmResult = await this.$confirm('请确认是否结束本轮竞价，结束后供应商将不可再报价。', {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).catch(() => { /* nothing */ })

      if (confirmResult !== 'confirm') {
        return
      }

      let transformParams = transformMQL.save('AuctSouProjectForBuyer', [{
        projectId: this.baseInfo.projectId,
        endNow: true
      }], 'changeOrderEndTime')
      const response = await carBuyerHttp.order.changeOrderEndTime(transformParams)

      if (response) {
        this.$message.success(this.$t('common.successUpdate'))
        this.$emit('refresh')
        this.$emit('refresh-process')
        this.autoRefreshHall()
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.countdown-round-right {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  width: 100%;
  height: 100%;
  padding: 25px 0 0 15px;
  .el-button {
    margin-left: 0;
    &:not(:last-child) {
      margin-bottom: 10px;
    }
  }
}
</style>
