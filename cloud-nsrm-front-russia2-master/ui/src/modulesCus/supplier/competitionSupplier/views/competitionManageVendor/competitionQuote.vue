<template>
  <el-container class="flex-container" direction="vertical">
    <el-main class="competition-project-quote-wrap">
      <div class="competition-project-quote">
        <div class="progress-wrap">
          <el-steps :active="activeStep">
            <el-step :title="$t('bidMod.published')" />
            <el-step :title="$t('bidMod.apply')" />
            <el-step :title="$t('bidMod.applied')" />
            <el-step :title="$t('bidMod.quote')" />
            <el-step :title="$t('bidMod.completeQuotation')" />
          </el-steps>
        </div>

        <!--报价截止倒计时-->
        <div class="sign-up-deadline">
          <!-- 距离报价截止还剩余： -->
          <DynamicCutoffTime :label="$t('competition.signUpDeadline')" :deadline-time="orderInfo.orderEndTime" />
        </div>
      </div>
      <el-collapse v-model="activeNames" class="tab-form-style">
        <el-collapse-item name="1" :title="$t('cusEntry.competition.quoteInfo')">
          <el-form :model="orderInfo">
            <srm-row>
              <srm-col :init-col="4">
                <el-form-item :label="$t('cusEntry.competition.competitionNotice')">
                  <el-input
                    v-model="competitionNotice"
                    type="textarea"
                    rows="4"
                    disabled
                  />
                </el-form-item>
              </srm-col>
              <srm-col :init-col="4">
                <el-form-item :label="$t('cusEntry.competition.quoteCap')">
                  <template slot="label">
                    {{ $t('cusEntry.competition.quoteCap') }}
                    <el-tooltip
                      class="item"
                      effect="dark"
                      :content="$t('cusEntry.competition.noticeQuoteCap')"
                      placement="top"
                    >
                      <em class="el-icon-question" />
                    </el-tooltip>
                  </template>
                  <el-input
                    v-model="orderInfo.quoteCap"
                    disabled
                  />
                </el-form-item>
              </srm-col>
              <srm-col :init-col="4">
                <el-form-item :label="$t('cusEntry.competition.planQuoteRound')">
                  <el-input
                    v-model="orderInfo.orderNum"
                    disabled
                  />
                </el-form-item>
              </srm-col>
              <!-- <srm-col :init-col="4">
                <el-form-item :label="$t('cusEntry.competition.orderRound')">
                  <el-input
                    v-model="orderInfo.orderRound"
                    disabled
                  />
                </el-form-item>
              </srm-col> -->
            </srm-row>
          </el-form>
          <el-table
            ref="requirementTable"
            :data="orderLineList"
            border
          >
            <el-table-column
              align="center"
              type="index"
              width="50"
              fixed="left"
            />
            <!-- 物资名称 -->
            <el-table-column
              align="center"
              :label="$t('cusEntry.competition.materialName')"
              prop="itemDesc"
              min-width="120"
              show-overflow-tooltip
              fixed="left"
            />
            <!-- 组合 -->
            <el-table-column
              align="center"
              :label="$t('cusEntry.competition.combination')"
              prop="itemGroup"
              min-width="120"
              show-overflow-tooltip
              fixed="left"
            />
            <!-- 所属单位 -->
            <el-table-column
              align="center"
              :label="$t('cusEntry.competition.belongCompany')"
              prop="affiliatedUnit"
              min-width="120"
              show-overflow-tooltip
            />
            <!-- 单位 -->
            <el-table-column
              align="center"
              :label="$t('cusEntry.competition.unit')"
              prop="unit"
              min-width="120"
              show-overflow-tooltip
            />
            <!-- 月约产量 -->
            <el-table-column
              align="center"
              :label="$t('cusEntry.competition.monthProduct')"
              prop="monthlyProduction"
              min-width="120"
              show-overflow-tooltip
            />
            <!-- 计量单位 -->
            <el-table-column
              align="center"
              :label="$t('cusEntry.competition.measurementUnit')"
              prop="meteringUnit"
              min-width="120"
              show-overflow-tooltip
            />
            <!-- 起拍价格 -->
            <el-table-column
              align="center"
              :label="$t('cusEntry.competition.startPrice')"
              prop="startPrice"
              min-width="120"
              show-overflow-tooltip
            />
            <!-- 梯次价格 -->
            <el-table-column
              align="center"
              :label="$t('cusEntry.competition.cascadePrice')"
              prop="echelonPrice"
              min-width="120"
              show-overflow-tooltip
            />
            <el-table-column
              align="center"
              :label="$t('cusEntry.competition.quoteRound')"
              prop="orderRound"
              min-width="120"
              show-overflow-tooltip
            />
            <!-- 本次报价 -->
            <el-table-column
              align="center"
              :label="$t('cusEntry.competition.thisQuote')"
              prop="orderNowPrice"
              min-width="130"
            >
              <template slot-scope="scope">
                <el-input
                  v-model="scope.row.orderNowPrice"
                  v-input-format="{ type: 'float', negative: false}"
                  @change="value => taxPriceChange(value, scope.row)"
                />
              </template>
            </el-table-column>
            <!-- 上次报价 -->
            <el-table-column
              align="center"
              :label="$t('cusEntry.competition.lastPrice')"
              prop="orderTaxPrice"
              min-width="130"
            />
            <el-table-column
              fixed="right"
              :label="$t('bidMod.operation')"
              align="center"
              width="60"
            >
              <template slot-scope="scope">
                <el-button
                  v-if="Number(orderInfo.orderNum) > (Number(scope.row.orderRound) || 0)"
                  type="text"
                  @click="submitQuote(scope.row)"
                >
                  {{ $t('common.submit') }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-collapse-item>
      </el-collapse>
      <!-- <CToolbar>
        <template slot="right">
          <el-button type="primary" @click="submitQuote">
            {{ $t('common.submit') }}
          </el-button>
        </template>
      </CToolbar> -->
    </el-main>
  </el-container>
</template>

<script>
import { BUSINESS_TYPE_ENUM, SOU_PROJECT_STATUS_ENUM } from 'lib@/composition/origin/enum'
import { compVendorHttp } from 'modcs@/competitionSupplier/api'
import RenderAsyncText from 'lib@/components/provice-city/renderAsyncText'
import DynamicCutoffTime from 'lib@/components/dynamic-cutoff-time'
import TechInfo from './competitionProjectQuote/techInfo'
import PaymentTypeDialog from 'lib@/composition/origin/paymentTypeDialog'
import CToolbar from 'lib@/components/c-toolbar'
import { tabTodoMixin } from '@/utils/mixins'
export default {
  name: 'CompetitionProjectQuote',
  components: {
    RenderAsyncText,
    DynamicCutoffTime,
    TechInfo,
    PaymentTypeDialog,
    CToolbar
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      activeStep: 3,
      projectId: this.$attrs.params.row.projectId,
      orderLineList: [],
      orderInfo: {
        projectStatus: '',
        minPercent: '',
        minAmount: '',
        scoreRuleType: '',
        orderEndTime: '',
        quoteCap: '',
        orderRound: '',
        orderNum: '',
        souRules: ''
      },
      // competitionNotice: '1、每次报价≥起拍价格 \n2、每次报价≥起拍价格+梯次价格 \n3、有相同组合标识的物资为打包处置物资（打包定）\n4、组合打包报价物资即打包处置物资，供应商必须每个物料必须报价',
      competitionNotice: this.$t('cusEntry.supplement20250205.competitionNotice'),
      paymentTypeDialogVisible: false,
      viewRow: null,
      orderFileList: [],
      BUSINESS_TYPE_ENUM,
      SOU_PROJECT_STATUS_ENUM,
      activeNames: ['1']
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
    /* 报价变更 */
    taxPriceChange (value, row) {
      /* 如果清空不处理 */
      if (!value) {
        return false
      }
      const {
        orderTaxPrice,
        echelonPrice,
        startPrice
      } = row
      if (this.orderInfo.souRules === 'FORWARD_RULE') {
        /* 最低价 */
        const minPrice = Number(orderTaxPrice) + Number(echelonPrice)
        /* 最高价 */
        const overPercentage = Number(orderTaxPrice) * (1 + Number(this.orderInfo.quoteCap) / 100)
        if (orderTaxPrice) {
          if (Number(value) > overPercentage) {
            row.orderNowPrice = overPercentage
            return false
          } else if (Number(value) < minPrice) {
            row.orderNowPrice = minPrice
          }
        } else {
          if (Number(value) < Number(startPrice)) {
            row.orderNowPrice = startPrice
            return false
          }
        }
      } else {
        if (orderTaxPrice) {
          if (Number(value > Number(orderTaxPrice))) {
            row.orderNowPrice = orderTaxPrice
            return false
          } else {
            row.orderNowPrice = value
            return false
          }
        }
      }
    },
    /* 提交报价 */
    async submitQuote (row) {
      let quoteList = [row]
      if (row.itemGroup) {
        /* 同一组合必须一起报价 */
        quoteList = this.orderLineList.filter(item => item.itemGroup == row.itemGroup)
        if (quoteList.length > 1) {
          let validFlag = true
          quoteList.some(item => {
            if (!item.orderNowPrice && item.orderNowPrice != 0) {
              validFlag = false
              return true
            }
          })
          if (!validFlag) {
            this.$message.warning(this.$t('cusEntry.tipMessage.saveGroupTogetherQuote'))
            return
          }
        }
      }
      const response = await compVendorHttp.order.editOrder({
        projectId: this.projectId,
        orderFileList: this.orderFileList,
        orderItemList: quoteList,
        isTempSave: false
      })
      if (response) {
        this.$message.success(this.$t('common.successSubmit'))
        this.getOrderDetails()
      }
    },
    /* 获取详情 */
    async getOrderDetails () {
      const response = await compVendorHttp.order.getOrderInfo(this.projectId)
      if (response && response.data) {
        const {
          initInfo = {},
          itemList = [],
          orderFileList = [],
          order
        } = response.data

        const { projectInfo = {}, requireInfo = [] } = initInfo

        this.orderLineList = itemList
        // this.$nextTick(() => {
        //   this.$refs.orderLineListTable.doLayout()
        // })
        this.orderFileList = orderFileList

        const obj = {}
        for (let key in this.orderInfo) {
          if (projectInfo[key] || projectInfo[key] === 0) {
            obj[key] = projectInfo[key]
          }
        }
        obj.orderRound = order.orderRound
        if (order.orderStatus === 'SUBMISSION' && obj.orderRound == obj.orderNum) {
          this.activeStep = 4
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
        this.$message.warning(this.$t('competition.orderNotaxPriceTip'))  // '请输入本次报价！'
        return
      }

      const response = await compVendorHttp.order.editOrder({
        projectId: this.projectId,
        orderFileList: this.orderFileList,
        orderItemList: this.orderLineList,
        isTempSave: false
      })

      if (response) {
        this.$message.success(this.$t('common.successSubmit'))
        await this.getOrderDetails()
      }
    },
    /* 返回标签页 */
    backTab (type) {
      this.$emit('tab-remove', this.$attrs.params.tabName)
      if (type === 'refresh') {
        // 刷新列表
        this.__setTabTodo('CompetitionProjectList.getQueryData')
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
    height: 75px;
    background: #eee;
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
  .sign-up-deadline {
    margin-top: 40px;
    margin-left: 10px;
  }
}
</style>
