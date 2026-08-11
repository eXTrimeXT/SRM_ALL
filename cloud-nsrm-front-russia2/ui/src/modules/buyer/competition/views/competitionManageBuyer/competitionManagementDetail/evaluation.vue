<!--suppress AllyHtmlVueInspection, AllyHtmlVueInspection -->
<template>
  <el-container
    class="flex-container"
    direction="vertical"
    style="padding-top: 10px;"
  >
    <SearchForm
      :pre-form-obj="{ round: baseInfo.currentRound || 1 }"
      :require-info-data="requireInfoData"
      :vendor-info-data="vendorInfoData"
      @search="getQueryData"
    />

    <!--按钮操作区-->
    <div class="btn-wrap" style="margin-bottom: 10px">
      <!--决标操作-->
      <el-dropdown style="margin: 0 8px;" @command="changeSelectStatus">
        <el-button type="primary">
          {{ $t("bidMod.bidAwardOperation") }}
          <em class="el-icon-arrow-down el-icon--right" />
        </el-button>
        <el-dropdown-menu slot="dropdown">
          <!--中标-->
          <el-dropdown-item command="win">
            {{ $t("bid_mod.winTheBidding") }}
          </el-dropdown-item>
          <!--落标-->
          <el-dropdown-item command="loss">
            {{ $t("bid_mod.lossTheBidding") }}
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>

      <!-- 发起新一轮 评选中 -->
      <el-button type="primary" @click="startNewRoundVisible = true">
        {{ $t('bidMod.biddingControl.startNewRound') }}
      </el-button>

      <!--生成价格审批单-->
      <el-button type="primary" @click="signCreate">
        <!-- 生成价格审批单 -->
        {{ $t("bidMod.toPriceApproval") }}
      </el-button>

      <!--结果公式-->
      <el-button type="primary" @click="noticeCreate">
        <!-- 结果公示 -->
        {{ $t("competition.noticeCreate") }}
      </el-button>
    </div>

    <!--评选表格-->
    <TableView
      ref="tenderSelectionTable"
      :table-data="tableData"
      :table-header="tableHeader"
      row-key="orderItemId"
      :pre-query-data="queryParam"
      checkbox
      reserve-selection
      :sortable="false"
      table-height="330px"
      :check-change="handleSelectionChange"
      :open-custom-table="false"
      :com-active="$attrs['changeTab']"
      :adeptMeiQl="true"
      :url="tableViewUrl"
      class="evaluation-table"
      :source="() => {}"
      :transformData="transformTableData"
    >
      <!--中标数量-->
      <template #winAmount="{ scope }">
        <el-input
          v-model="scope.row.winAmount"
          :disabled="baseInfo.annualFrameworkType === 'Y'"
          @click.native.stop
        />
      </template>
    </TableView>

    <!--发起新一轮-->
    <StartNewRound
      v-if="startNewRoundVisible"
      ref="startNewRound"
      :visible.sync="startNewRoundVisible"
      :base-info="baseInfo"
      :project-id="baseInfo.projectId"
      :require-info-data="requireInfoData"
      :vendor-info-data="vendorInfoData"
      :business-type="BUSINESS_TYPE_ENUM.COMPETITION"
      @submit="(data) => submitStartNewRound(data,'N')"
      @submitNext="openSetNewRoundStartingPrice"
    />

    <!--设置新一轮起拍价-->
    <SetNewRoundStartingPrice
      v-if="setNewRoundStartingPriceVisible"
      :visible.sync="setNewRoundStartingPriceVisible"
      :base-info="baseInfo"
      :require-info-data="requireInfoData"
      @submit="(data) => submitStartNewRound(data,'Y')"
    />

    <!--费用核对-->
    <ExpenseVerification
      v-if="expenseVerificationVisible"
      :visible.sync="expenseVerificationVisible"
      :project-id="baseInfo.projectId"
    />
  </el-container>
</template>

<script>
import { carBuyerHttp } from 'modb@/competition/api'
import { BUSINESS_TYPE_ENUM } from 'lib@/composition/origin/enum'
import TableView from 'lib@/components/Table/TableView'
import SearchForm from './evaluation/searchForm'
import StartNewRound from 'lib@/composition/competition/startNewRound'
import ExpenseVerification from './evaluation/expenseVerification'
import SetNewRoundStartingPrice from './evaluation/setNewRoundStartingPrice.vue'
import { transformMQL } from 'lib@/utils/util'

export default {
  name: 'Evaluation',

  components: {
    SearchForm,
    TableView,
    StartNewRound,
    ExpenseVerification,
    SetNewRoundStartingPrice
  },

  props: {
    baseInfo: {
      type: Object,
      default: () => ({})
    },
    isActiveMenu: {
      type: Boolean,
      required: true
    },
    // 当前启用的节点
    enabledNodeMenu: {
      type: Array,
      default: () => []
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
      tableViewUrl: carBuyerHttp.select.listPageUrl,
      tableData: [],
      tableHeader: [
        // 评选结果
        {
          prop: 'selectStatus',
          label: this.$t('bidMod.selectionStatus'),  // '评选结果'
          minWidth: 120,
          formattor: value => this.$getDictLabel('SOU_SELECT_STATUS', value)
        },
        // 供应商编码
        {
          prop: 'vendorCode',
          label: this.$t('bidMod.vendorCode'),
          minWidth: 110
        },
        // 供应商名称
        {
          prop: 'vendorName',
          label: this.$t('bidMod.vendorName'),
          minWidth: 110
        },
        // 无料号寻源
        {
          prop: 'noCodeItem',
          label: this.$t('sourcingBuyer.isMaterialSourcing'),  // '无料号寻源'
          minWidth: 120,
          formattor: value => this.$getDictLabel('YES_OR_NO', value)
        },
        // 物料编码
        {
          prop: 'itemCode',
          label: this.$t('bidMod.targetNum'),
          minWidth: 120
        },
        // 物料名称
        {
          prop: 'itemDesc',
          label: this.$t('bidMod.targetDesc'),
          minWidth: 120
        },
        // 需求数量
        {
          prop: 'requireQuantity',
          label: this.$t('bidMod.demandQuantity2'),
          minWidth: 100
        },
        // 中标数量
        {
          prop: 'winAmount',
          label: this.$t('bidMod.quotaQuantity'),
          minWidth: 100,
          showType: 'slot',
          slot: 'winAmount'
        },
        // 报价币种
        {
          prop: 'orderCurrency',
          label: this.$t('bidMod.bidingCurrency2'),
          minWidth: 120,
          dataType: 'dict',
          code: 'currency'
        },
        // 税率
        {
          prop: 'taxKey',
          label: this.$t('bidMod.taxRate'),
          minWidth: 120,
          dataType: 'dict',
          code: 'tax'
        },
        // 本币未税单价
        {
          prop: 'orderNotaxPrice',
          label: this.$t('bidMod.standardNotaxPriceOr'),  // '本币未税单价'
          minWidth: 120
        },
        // 本币含税单价
        {
          prop: 'orderTaxPrice',
          label: this.$t('competition.orderTaxPrice'),
          minWidth: 120
        },
        // 原币未税单价
        {
          prop: 'orderNotaxPrice',
          label: this.$t('bidMod.orderNotaxPrice'),
          minWidth: 120
        },
        // 原币含税单价
        {
          prop: 'orderTaxPrice',
          label: this.$t('bidMod.orderTaxPrice'),
          minWidth: 120
        },
        // 汇率
        {
          prop: 'priceTax',
          label: this.$t('bid_mod.priceTax'),
          minWidth: 120
        },
        // 轮次
        {
          prop: 'round',
          label: this.$t('bidMod.bidingRound'),
          minWidth: 120
        },
        // 业务实体
        {
          prop: 'orgOuName',
          label: this.$t('bid_mod.businessEntity'),
          minWidth: 120
        },
        // 库存组织
        {
          prop: 'orgInvName',
          label: this.$t('bid_mod.inv'),
          minWidth: 120
        },
        // 价格类型
        {
          prop: 'priceType',
          label: this.$t('bid_mod.priceType'),
          minWidth: 120,
          formattor: value => this.$getDictLabel('PRICE_TYPE', value)
        },
        // 价格有效期从
        {
          prop: 'priceStartTime',
          label: this.$t('contractMod.price_startDate'),
          minWidth: 120,
          dataType: 'dateTime'
        },
        // 价格有效期至
        {
          prop: 'priceEndTime',
          label: this.$t('dataConfMod.priceExpirationDate'),
          minWidth: 120,
          dataType: 'dateTime'
        },
        // 贸易条款（月）
        {
          prop: 'tradeTerm',
          label: this.$t('cusEntry.supplement20250211.tradeTermsMonth'),
          minWidth: 120,
          formattor: value => this.$getDictLabel('trade_clause', value)
        },
        // 质保期
        {
          prop: 'warrantyPeriod',
          label: this.$t('bidMod.guaranteePeriod'),
          minWidth: 120
        },
        // 采购申请单号
        {
          prop: 'sourceFromNo',
          label: this.$t('purchaseDemand.purRequisitionNum'),
          minWidth: 120
        },
        // 采购申请行号
        {
          prop: 'sourceFromLineNo',
          label: this.$t('bid_mod.purchaseRequestRowNum'),
          minWidth: 120
        },

        // 排名
        {
          prop: 'ranking',
          label: this.$t('perfMod.rank'),
          minWidth: 120
        }
      ],
      multipleSelection: [],
      queryParam: {},
      startNewRoundVisible: false,
      expenseVerificationVisible: false,
      setNewRoundStartingPriceVisible: false,
      fromStartNewRound: {},
      BUSINESS_TYPE_ENUM,
      prevRoundData: {}// 发起新一轮数据
    }
  },

  watch: {
    isActiveMenu: {
      handler (val) {
        if (val) {
          this.getQueryData()
        }
      },
      immediate: true
    }
  },

  methods: {
    /* 搜索 */
    getQueryData (params = {}) {
      this.queryParam = transformMQL.listPageData({
        type: 'AuctSouProjectForBuyer',
        action: 'listEvaluations',
        params: {
          ...params,
          projectId: this.baseInfo.projectId
        }
      })

      this.$nextTick(() => {
        this.$refs.tenderSelectionTable.query()
      })
    },

    transformTableData (data) {
      const records = data.data.records
      data.data.records = records.map(item => ({
        ...item,
        ...item.auctSouOrderItem,
        ...item.souItem,
        ...item.souItem.auctSouItem,
        ...item.souVendor
      }))
      console.log('reocrds::', data.data.records)
      return data
    },

    /* 记录表格选中行 */
    handleSelectionChange (val) {
      this.multipleSelection = val
    },

    /* 中标 */
    async changeSelectStatus (type) {
      if (this.multipleSelection.length === 0) {
        this.$message.warning(this.$t('bidMod.msgSelData'))
        return false
      }

      const selects = this.multipleSelection.map(item => {
        return {
          orderItemId: item.orderItemId,
          winAmount: item.winAmount
        }
      })

      if (!selects || selects.length === 0) {
        return
      }

      const toWin = type === 'win'
      // if (toWin) {
      //   // 允许为0
      //   if (selects.find(item => !item.winAmount && item.winAmount !== 0)) {
      //     // 请先填写中标数量
      //     return this.$message.warning(this.$t('vendorMod.msgSelBidder'))
      //   }
      // }

      let transformParams = transformMQL.save('AuctSouProjectForBuyer', [{
        projectId: this.baseInfo.projectId,
        selects,
        toWin: type === 'win'
      }], 'changeSelectStatus')
      // 发起中标
      const response = await carBuyerHttp.select.changeSelectStatus(transformParams)

      if (response) {
        this.$message.success(this.$t('common.successSubmit'))

        this.getQueryData()
      }
    },

    /* 生成价格审批单 */
    async signCreate () {
      let transformParams = transformMQL.save('AuctSouProjectForBuyer', [{
        projectId: this.baseInfo.projectId
      }], 'createPricingApproval')
      const response = await carBuyerHttp.control.createPricingApproval(transformParams)

      if (response) {
        this.getQueryData()

        if (response.data) {
          // 定点会签单创建成功，是否跳转到单据页面？
          const confirmResult = await this.$confirm('价格审批单创建成功，是否跳转到单据页面？', {
            confirmButtonText: this.$t('common.confirm'),
            cancelButtonText: this.$t('common.cancel'),
            type: 'success'
          })

          if (confirmResult !== 'confirm') {
            return
          }

          const { approvalHeaderId, approvalNo } = response.data.records[0] || {}
          // 传递参数到定点会签单页
          this.$router.push({
            name: 'priceApproval',
            params: {
              from: 'fromFun',
              funName: 'priceApproval',
              formId: approvalHeaderId,
              formNo: approvalNo
            }
          })
        }
      }
    },

    /* 生成定点通知单 */
    async noticeCreate () {
      let transformParams = transformMQL.save('AuctSouProjectForBuyer', [{
        projectId: this.baseInfo.projectId
      }], 'openResult')
      const response = await carBuyerHttp.select.openResult(transformParams)

      if (response) {
        this.getQueryData()
        this.$message.success(this.$t('common.successSubmit'))
      }
    },

    /* 打开设置新一轮起拍价 */
    async openSetNewRoundStartingPrice (data) {
      console.log('data:::', data)
      this.prevRoundData = data
      this.setNewRoundStartingPriceVisible = true
    },

    /* 提交发起新一轮 */
    async submitStartNewRound (data, flag) {
      let params = {}
      if (flag === 'Y') {
        const { formData, vendorData } = this.prevRoundData
        params = {
          projectId: this.baseInfo.projectId,
          orderStartTime: formData.orderStartTime,
          orderEndTime: formData.orderEndTime,
          startNow: true,
          newVendors: vendorData,
          auctSouItemList: data.souItemList
        }
      } else {
        const { formData, vendorData } = data
        params = {
          projectId: this.baseInfo.projectId,
          orderStartTime: formData.orderStartTime,
          orderEndTime: formData.orderEndTime,
          startNow: true,
          newVendors: vendorData,
          auctSouItemList: []
        }
      }
      console.log('params:::', params)
      let transformParams = transformMQL.save('AuctSouProjectForBuyer', [params], 'startNewRound')
      const response = await carBuyerHttp.control.startNewRound(transformParams)

      if (response) {
        this.$message.success(this.$t('common.successSubmit'))
        this.startNewRoundVisible = false
        this.setNewRoundStartingPriceVisible = false
        this.$emit('refresh')
      }
    }
  }
}
</script>
