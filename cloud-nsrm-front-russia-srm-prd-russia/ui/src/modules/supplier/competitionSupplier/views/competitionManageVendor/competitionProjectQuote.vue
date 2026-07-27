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

        <!--报价截止倒计时-->
        <div class="sign-up-deadline">
          <DynamicCutoffTime label="距离报价截止还剩余：" :deadline-time="orderInfo.orderEndTime" />
        </div>

        <div class="evaluate-method-wrap">
          <srmRow>
            <srmCol :init-col="3">
              <span>{{ $t('bidMod.scoreRule') }}</span>{{ $getDictLabel('SOU_AUCT_SCORE_RULE_TYPE', orderInfo.scoreRuleType) }}
            </srmCol>
          </srmRow>
        </div>

        <div
          v-if="orderInfo.projectStatus === SOU_AUCT_PROJECT_STATUS_ENUM.ACCEPT_ORDER"
          class="details-info-wrap"
        >
          <span class="label">{{ $t('bidMod.commercialMessage') }}</span>

          <!--刷新-->
          <el-button
            type="primary"
            style="margin: 0 0 0 11px"
            @click="getOrderDetails"
          >
            {{ $t('bidMod.refresh') }}
          </el-button>

          <!--（请点击“刷新”，刷新最新价格）-->
          <span class="refresh-tips">
            {{ $t('bidMod.refreshTxt') }} {{ quotePriceTips }}
          </span>
        </div>

        <!--物料列表-->
        <el-table
          ref="orderLineListTable"
          :data="orderLineListPaginationData"
          style="width: 100%"
          border
          height="200px"
        >
          <el-table-column
            type="index"
            fixed="left"
            :label="$t('common.sort')"
            width="50"
          />

          <!--物料编码-->
          <el-table-column
            prop="itemCode"
            :label="$t('bidMod.itemCode')"
            width="120"
            fixed="left"
            show-overflow-tooltip
          />

          <!--物料名称-->
          <el-table-column
            prop="itemDesc"
            :label="$t('bidMod.targetDesc')"
            min-width="150"
            fixed="left"
            show-overflow-tooltip
          />

          <!--起拍价-->
          <el-table-column
            align="right"
            prop="startOrderNotaxPrice"
            :label="$t('bidMod.startingPrice')"
            width="100"
            show-overflow-tooltip
          />

          <!--本次报价-->
          <el-table-column
            align="right"
            prop="recommendOrderNotaxPrice"
            :label="$t('bidMod.thisQuotation')"
            width="100"
            show-overflow-tooltip
            :render-header="_addStarToColumn"
          >
            <template v-slot="scope">
              <el-input v-model="scope.row.recommendOrderNotaxPrice" v-input-format="{ type: 'float' }" />
            </template>
          </el-table-column>

          <!--含税报价(单价)-->
          <el-table-column
            align="right"
            prop="orderTaxPrice"
            :label="$t('bidMod.taxQuotation')"
            width="120"
          />

          <!--我司上次报价-->
          <el-table-column
            align="right"
            prop="orderNotaxPrice"
            label="我司上次报价"
            width="100"
            show-overflow-tooltip
          />

          <!--报价币种-->
          <el-table-column
            prop="orderCurrency"
            :label="$t('bidMod.currencyType')"
            width="150"
            show-overflow-tooltip
            :formatter="(row, colum, value) => $getDictLabel('currency', value)"
          />

          <!--税率-->
          <el-table-column
            prop="taxKey"
            :label="$t('bidMod.tech_taxRate')"
            width="100"
            show-overflow-tooltip
            :formatter="(row, colum, value) => $getDictLabel('tax', value)"
          />

          <!--付款条款-->
          <el-table-column
            prop="paymentType"
            :label="$t('bidMod.affairsPayClause')"
            width="100"
          >
            <template v-slot="{ row }">
              <el-button type="text" @click="openPaymentTypeDialog(row)">
                {{ $t('common.view') }}
              </el-button>
            </template>
          </el-table-column>

          <!--定价开始日期-->
          <el-table-column
            prop="priceStartTime"
            :label="$t('bidMod.priceBeginDate')"
            width="100"
            show-overflow-tooltip
            :formatter="(row, colum, value) => $dayjsParse(value)"
          />

          <!--定价结束日期-->
          <el-table-column
            prop="priceEndTime"
            :label="$t('bidMod.priceEndDate')"
            width="100"
            show-overflow-tooltip
            :formatter="(row, colum, value) => $dayjsParse(value)"
          />

          <!--业务实体-->
          <el-table-column
            prop="orgOuName"
            :label="$t('bidMod.businessEntity')"
            width="150"
            show-overflow-tooltip
          />

          <!--库存组织-->
          <el-table-column
            prop="orgInvName"
            :label="$t('bidMod.inventoryOrg')"
            width="150"
            show-overflow-tooltip
          />

          <!--交货地点-->
          <el-table-column
            prop="deliveryPlace"
            :label="$t('bidMod.deliveryPoints')"
            width="150"
            show-overflow-tooltip
          >
            <template v-slot="scope">
              <RenderAsyncText :cell-value="scope.row.deliveryPlace" />
            </template>
          </el-table-column>

          <!--贸易条款-->
          <el-table-column
            prop="tradeTerm"
            :label="$t('bidMod.technical_tradeClause')"
            width="150"
            show-overflow-tooltip
            :formatter="(row, colum, value) => $getDictLabel('trade_clause', value)"
          />

          <!--质保期(月)-->
          <el-table-column
            prop="warrantyPeriod"
            :label="$t('bidMod.appraisGuarantee')"
            width="150"
            show-overflow-tooltip
          />

          <!--采购数量-->
          <el-table-column
            prop="requireQuantity"
            :label="$t('bidMod.purchaseAmount')"
            width="100"
            show-overflow-tooltip
          />

          <!--单位-->
          <el-table-column
            prop="unit"
            :label="$t('bidMod.appraisUnit')"
            width="100"
            show-overflow-tooltip
            :formatter="(row, column, cellValue) => $getDictLabel('unit', cellValue)"
          />

          <!--备注-->
          <el-table-column
            prop="remark"
            :label="$t('common.remark')"
            min-width="150"
            show-overflow-tooltip
          />

          <el-table-column
            fixed="right"
            :label="$t('bidMod.operation')"
            width="130"
          >
            <template v-slot="{ row }">
              <!--提交-->
              <el-button type="text" @click="rowQuote(row)">
                {{ $t('common.submit') }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <div style="width: 100%">
          <CPagination
            ref="queryPagination"
            style="margin: 0"
            class="c-query-table-pagination"
            :total="orderLineList.length"
            :page-num="currentPage"
            layout="total, sizes, prev, pager, next, jumper"
            :page-size="pageSize"
            :page-sizes="[5, 10]"
            @current-change="handleCurrentChange"
            @size-change="handleSizeChange"
          />
        </div>

        <!--附件信息-->
        <TechInfo :order-file-list.sync="orderFileList" />

        <!--查看付款条款-->
        <PaymentTypeDialog
          v-if="paymentTypeDialogVisible"
          :visible.sync="paymentTypeDialogVisible"
          :business-type="BUSINESS_TYPE_ENUM.COMPETITION"
          :edit-row="viewRow"
          readonly
        />
      </div>
    </el-main>
  </el-container>
</template>

<script>
import { BUSINESS_TYPE_ENUM } from 'lib@/composition/origin/enum'
import { SOU_AUCT_PROJECT_STATUS_ENUM } from 'lib@/composition/competition/utils'
import { compVendorHttp } from 'mods@/competitionSupplier/api'
import CPagination from 'lib@/components/c-pagination'
import RenderAsyncText from 'lib@/components/provice-city/renderAsyncText'
import DynamicCutoffTime from 'lib@/components/dynamic-cutoff-time'
import TechInfo from './competitionProjectQuote/techInfo'
import PaymentTypeDialog from 'lib@/composition/origin/paymentTypeDialog'

export default {
  name: 'CompetitionProjectQuote',

  components: {
    CPagination,
    RenderAsyncText,
    DynamicCutoffTime,
    TechInfo,
    PaymentTypeDialog
  },

  data () {
    return {
      projectId: this.$attrs.params.row.projectId,
      currentPage: 1,
      total: 20,
      pageSize: 10,
      orderLineList: [],
      orderInfo: {
        projectStatus: '',
        minPercent: '',
        minAmount: '',
        scoreRuleType: '',
        orderEndTime: ''
      },
      paymentTypeDialogVisible: false,
      viewRow: null,
      orderFileList: [],
      BUSINESS_TYPE_ENUM,
      SOU_AUCT_PROJECT_STATUS_ENUM
    }
  },

  computed: {
    quotePriceTips () {
      const { minPercent, minAmount, scoreRuleType } = this.orderInfo
      if (minPercent || minPercent === 0) {
        // 百分比
        if (scoreRuleType === 'MAX_PRICE') {
          // 正向 最小涨幅百分比
          return `（${this.$t('bidMod.minimumPercentage')}：${minPercent}%）`
        } else if (scoreRuleType === 'MIN_PRICE') {
          // 反向 最小降幅百分比
          return `（${this.$t('bidMod.minimalPercentage')}：${minPercent}%）`
        }
      }

      if (minAmount || minAmount === 0) {
        // 金额
        if (scoreRuleType === 'MAX_PRICE') {
          // 正向 最小涨幅百分比
          return `（${this.$t('bidMod.minimumPriceIncrease')}：${minAmount}）`
        } else if (scoreRuleType === 'MIN_PRICE') {
          // 反向 最小降价金额
          return `（${this.$t('bidMod.minimumPriceReduction')}：${minAmount}）`
        }
      }

      return ''
    },

    orderLineListPaginationData () {
      return this.orderLineList.slice((this.currentPage - 1) * this.pageSize, this.currentPage * this.pageSize)
    }
  },

  created () {
    this.getOrderDetails()
  },

  methods: {
    /* 获取详情 */
    async getOrderDetails () {
      const response = await compVendorHttp.order.getOrderInfo(this.projectId)

      if (response && response.data) {
        const {
          initInfo = {},
          itemList = [],
          orderFileList = []
        } = response.data

        const { projectInfo = {} } = initInfo

        this.orderLineList = itemList
        this.$nextTick(() => {
          this.$refs.orderLineListTable.doLayout()
        })
        this.orderFileList = orderFileList

        const obj = {}
        for (let key in this.orderInfo) {
          if (projectInfo[key] || projectInfo[key] === 0) {
            obj[key] = projectInfo[key]
          }
        }
        this.orderInfo = obj
      }
    },

    /* 分页大小改变 */
    handleSizeChange (val) {
      this.currentPage = 1
      this.pageSize = val
    },

    /* 当前页改变 */
    handleCurrentChange (val) {
      this.currentPage = val
    },

    /* 打开付款条款弹窗 */
    openPaymentTypeDialog (row) {
      this.viewRow = {
        paymentList: row.paymentList
      }
      this.paymentTypeDialogVisible = true
    },

    /* 提交一行报价 */
    async rowQuote (row) {
      if (!row.recommendOrderNotaxPrice && row.recommendOrderNotaxPrice !== 0) {
        this.$message.warning('请输入本次报价！')
        return
      }

      const response = await compVendorHttp.order.editOrder({
        projectId: this.projectId,
        orderFileList: this.orderFileList,
        orderItemList: [
          {
            orderItemId: row.orderItemId,
            souItemId: row.souItemId,
            orderNotaxPrice: row.recommendOrderNotaxPrice
          }
        ],
        tempSave: false
      })

      if (response) {
        this.$message.success(this.$t('common.successSubmit'))
        await this.getOrderDetails()
      }
    }
  }
}
</script>

<style scoped lang="scss">
.competition-project-quote {
  padding-bottom: 20px;
}
:deep(.competition-project-quote){
  .progress-wrap {
    padding: 11px;
    margin: 10px;
    background: #eee;
    height: auto;
    .el-steps {
      padding-bottom: 0;
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
}
</style>
