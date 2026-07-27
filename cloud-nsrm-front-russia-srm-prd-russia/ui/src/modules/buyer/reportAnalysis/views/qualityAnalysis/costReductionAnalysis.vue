<template>
  <el-container
    class="purchase-analysis-container"
    direction="vertical"
  >
    <el-main>
      <el-row class="mb-16">
        <el-col :span="12">
          <!-- 采购降本 -->
          <span class="translation-title">{{
            $t("reportMod.qualityAnalysis")
          }}</span>
        </el-col>
        <el-col :span="12">
          <el-row :gutter="32">
            <!-- 事业部 -->
            <el-col :span="6">
              <DictSelect
                v-model="queryParams.organizationTypeCode"
                code="ORG_TYPE"
                custom-select-type="ORG_TYPE"
                @change="clearFullPathId"
              />
            </el-col>
            <!-- 事业部 -->
            <el-col :span="6">
              <!-- 组织 -->
              <OrganizationSelector
                :key="queryParams.organizationTypeCode"
                ref="selector"
                v-model="queryParams.organizationId"
                :parent-id="-1"
                :placeholder="$t('common.pleaseSelect')"
                :node-type="queryParams.organizationTypeCode"
                :scope="queryParams"
                @select="selectHandle"
              />
            </el-col>
            <el-col :span="6">
              <!-- 开始时间 $t('componentDoc.stratTime') -->
              <el-date-picker
                v-model="queryParams.startTime"
                type="date"
                :placeholder="$t('componentDoc.stratTime')"
                value-format="yyyy-MM-dd"
                style="width:132px"
              />
            </el-col>
            <el-col :span="6">
              <!-- 结束时间 -->
              <el-date-picker
                v-model="queryParams.endTime"
                type="date"
                :placeholder="$t('componentDoc.endTime')"
                value-format="yyyy-MM-dd"
                style="width:132px"
              />
            </el-col>
          </el-row>
        </el-col>
      </el-row>
      <el-row
        :gutter="32"
        class="mb-16"
      >
        <el-col :span="6">
          <div class="block-count">
            <div class="block-count__title">
              <!-- 来料异常问题（单） -->
              <el-row>
                <el-col :span="blockCountData.yoyRate ? 14 : 24">
                  <el-tooltip
                    class="item"
                    effect="dark"
                    :content="$t('reportMod.qualityTitle[0]')"
                    placement="top-end"
                  >
                    <div class="block-count__title-sub">
                      {{ $t("reportMod.qualityTitle[0]") }}
                    </div>
                  </el-tooltip>
                </el-col>
                <el-col
                  v-if="blockCountData.yoyRate"
                  :span="10"
                >
                  <!-- 同比下降 -->
                  {{
                    $t("reportMod.qualityTitle[1]") + blockCountData.yoyRate
                  }}%
                </el-col>
              </el-row>
            </div>
            <div class="block-count__value">
              {{ blockCountData.curAmount }}
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="block-count">
            <!-- 制程异常问题（单） -->
            <div class="block-count__title">
              <el-tooltip
                class="item"
                effect="dark"
                :content="$t('reportMod.qualityTitle[2]')"
                placement="top-end"
              >
                <div class="block-count__title-sub">
                  {{ $t("reportMod.qualityTitle[2]") }}
                </div>
              </el-tooltip>
            </div>
            <div class="block-count__value">
              {{ blockCountData.crAmount }}
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="block-count">
            <div class="block-count__title">
              <el-row>
                <el-col :span="blockCountData.crTragetRate ? 14 : 24">
                  <!-- 8D报告单据（单） -->
                  <el-tooltip
                    class="item"
                    effect="dark"
                    :content="$t('reportMod.qualityTitle[3]')"
                    placement="top-end"
                  >
                    <div class="block-count__title-sub">
                      {{ $t("reportMod.qualityTitle[3]") }}
                    </div>
                  </el-tooltip>
                </el-col>
              </el-row>
            </div>
            <div class="block-count__value">
              {{ blockCountData.crRate }}
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="block-count block-count-active">
            <div class="block-count__title">
              <!-- 退货单据（单） -->
              <el-row>
                <el-col :span="blockCountData.yoyUpMaterialRate ? 14 : 24">
                  <el-tooltip
                    class="item"
                    effect="dark"
                    :content="$t('reportMod.qualityTitle[4]')"
                    placement="top-end"
                  >
                    <div class="block-count__title-sub">
                      {{ $t("reportMod.qualityTitle[4]") }}
                    </div>
                  </el-tooltip>
                </el-col>
                <el-col
                  v-if="blockCountData.yoyUpMaterialRate"
                  :span="10"
                >
                  {{
                    $t("reportMod.qualityTitle[5]") +
                      blockCountData.yoyUpMaterialRate
                  }}%
                </el-col>
              </el-row>
            </div>
            <div class="block-count__value">
              {{ blockCountData.upMaterialAmount }}
            </div>
          </div>
        </el-col>
      </el-row>
      <el-row :gutter="32">
        <el-col :span="6">
          <el-row class="chart-box mb-16">
            <div class="sub-title__wapper">
              <div class="sub-title">
                <!-- 来料批次合格率（%） -->
                <div>{{ $t("reportMod.qualityTitle[7]") }}</div>
                <el-tooltip
                  class="item"
                  effect="dark"
                  placement="top"
                >
                  <div
                    slot="content"
                    class="tooltip-content"
                    v-html="$t('reportMod.qualityTitle[8]')"
                  >
                    <!-- 数据来源：入库订单、退货订单、去年冻结单价、年度目标降本率<br />
                    过滤条件：<br />
                    1）已入库的搜索时间段按月份的采购订单情况<br />
                    2）时间范围：入库确认时间<br />
                    3）月度目标降本率=年度目标降本率。年度目标降本率需要在报表配置里进行设置<br />
                    4）月度目标降本金额=去年冻结单价*年度目标降本率*当前阶段月度采购数量<br />
                    5）实际采购降本金额=∑（去年冻结单价*当前阶段月度采购数量-∑（各订单月度单价*各订单月度入库数量））<br />
                    6）降本达成率=实际采购降本金额/月度目标降本金额*100%<br />
                    7）采购订单的实际金额，需要减去退货的订单金额<br /> -->
                  </div>
                  <i class="el-icon-warning info-icon" />
                </el-tooltip>
              </div>
              <!-- <div class="more" @click="showMoreInfo(1)">
                {{ $t("common.more") }}&gt;
              </div> -->
            </div>
            <gaugeChart :chart-data="gaugeChart" :comActive="changeTab" />
          </el-row>
        </el-col>
        <el-col :span="18">
          <el-row
            class="chart-box mb-16"
            style="height: 208px"
          >
            <div class="sub-title__wapper">
              <div class="sub-title">
                <!-- 异常处理效率 -->
                <div>{{ $t("reportMod.qualityTitle[9]") }}</div>
                <el-tooltip
                  class="item"
                  effect="dark"
                  placement="top"
                  style="position:absolute;top:12px;left:100px;z-index:9"
                >
                  <div
                    slot="content"
                    class="tooltip-content"
                    v-html="$t('reportMod.qualityTitle[10]')"
                  >
                    <!-- 数据来源:入库订单、退货订单、去年冻结单价、年度目标降本率<br />
                    过滤条件：<br />
                    1）已入库的搜索时间段累计采购订单情况<br />
                    2）时间范围：入库确认时间<br />
                    3）一月份累计降本率=（一月份∑(去年冻结单价*当前阶段采购数量
                    )-（当前阶段采购单价*当前阶段采购数量））/(去年冻结单价*当前阶段采购数量
                    )；<br />
                    二月份累计降本率=（一月份+二月份∑(去年冻结单价*当前阶段采购数量
                    )-（当前阶段采购单价*当前阶段采购数量））/(去年冻结单价*当前阶段采购数量
                    )<br />
                    ...以此类推，当前月份累计降本率，时间上需要累计月份。<br />
                    4）年度目标降本率，目标配置上去。<br /> -->
                  </div>
                  <i class="el-icon-warning info-icon" />
                </el-tooltip>
              </div>
              <div
                class="more"
                style="position:absolute;top:13px;right:18px;z-index:9"
                @click="showMoreInfo(2)"
              >
                {{ $t("common.more") }}&gt;
              </div>
            </div>
            <efficiencyChart
              style="position: absolute; top: 0; left: 0;z-index:0"
              :chart-data="efficiencyChartData"
              :comActive="changeTab"
            />
          </el-row>
        </el-col>
      </el-row>
      <el-row :gutter="32">
        <el-col :span="8">
          <el-row class="chart-box mb-16">
            <div class="sub-title__wapper">
              <div class="sub-title">
                <!-- 来料异常问题原因分布 -->
                <div>{{ $t("reportMod.qualityTitle[11]") }}</div>
                <el-tooltip
                  class="item"
                  effect="dark"
                  placement="top"
                >
                  <div
                    slot="content"
                    class="tooltip-content"
                    v-html="$t('reportMod.qualityTitle[12]')"
                  >
                    <!-- 数据来源：品类树、入库订单、退货订单、去年冻结单价、品类目标降本率<br />
                    过滤条件：<br />
                    1）已入库的搜索时间段累计采购订单情况。时间范围：入库确认时间<br />
                    2）品类降本率=∑（当前阶段品类采购单价*当前阶段品类采购数量）/∑（去年冻结单价*当前阶段品类采购数量）*100%<br />
                    3）品类降本达成率=品类降本率/品类目标降本率*100%；数值越高，达成率越高；达成率可大于100<br />
                    4）维护了父品类目标达成率，子级品类继承父级的目标达成率。但维护了子级的达成率，不会影响父级的目标达成率。<br /> -->
                  </div>
                  <i class="el-icon-warning info-icon" />
                </el-tooltip>
              </div>
              <div
                class="more"
                @click="showMoreInfo(3)"
              >
                {{ $t("common.more") }}&gt;
              </div>
            </div>
            <PieChart :chart-data="incomingData" :comActive="changeTab" />
          </el-row>
        </el-col>
        <el-col :span="8">
          <el-row class="chart-box mb-16">
            <div class="sub-title__wapper">
              <div class="sub-title">
                <!-- 制程异常问题原因分布 -->
                <div>{{ $t("reportMod.qualityTitle[14]") }}</div>
                <el-tooltip
                  class="item"
                  effect="dark"
                  placement="top"
                >
                  <div
                    slot="content"
                    class="tooltip-content"
                    v-html="$t('reportMod.qualityTitle[15]')"
                  >
                    <!-- 数据来源：入库订单、退货订单、去年冻结单价<br />
                    过滤条件：<br />
                    1）已入库的搜索时间段累计采购订单情况<br />
                    2）时间范围：入库确认时间<br />
                    3）品类降本金额=去年冻结单价*当前阶段月度采购数量-当前阶段月度单价*当前阶段月度采购数量<br />
                    4）金额区间可在报表配置中进行设置<br /> -->
                  </div>
                  <i class="el-icon-warning info-icon" />
                </el-tooltip>
              </div>
              <div
                class="more"
                @click="showMoreInfo(4)"
              >
                {{ $t("common.more") }}&gt;
              </div>
            </div>
            <PieChart :chart-data="processData" :comActive="changeTab" />
          </el-row>
        </el-col>
        <el-col :span="8">
          <el-row class="chart-box mb-16">
            <div class="sub-title__wapper">
              <div class="sub-title">
                <!-- 品类异常问题数量排名 -->
                <div>{{ $t("reportMod.qualityTitle[16]") }}</div>
                <el-tooltip
                  class="item"
                  effect="dark"
                  placement="top"
                >
                  <div
                    slot="content"
                    class="tooltip-content"
                    v-html="$t('reportMod.qualityTitle[17]')"
                  >
                    <!-- 数据来源:入库订单、退货订单、去年冻结单价<br />
                    过滤条件：<br />
                    1）已入库的搜索时间段累计采购订单情况<br />
                    2）时间范围：入库确认时间<br />
                    3）品类上涨金额=当前阶段单价*当前阶段采购数量-去年冻结单价*当前阶段采购数量<br />
                    4）单个品类上涨金额>0，即记录该品类为上涨品类，总和是各品类的上涨金额相加<br />
                    5）举例：若当前阶段，只有1月的采购单价是高于去年冻结单价，即只计算1月的上涨金额<br />
                    6）已入库的搜索时间段采购订单单价>去年冻结单价的采购金额数据才统计<br /> -->
                  </div>
                  <i class="el-icon-warning info-icon" />
                </el-tooltip>
              </div>
              <div
                class="more"
                @click="showMoreInfo(5)"
              >
                {{ $t("common.more") }}&gt;
              </div>
            </div>
            <barChart :chart-data="categoryData" :comActive="changeTab" />
          </el-row>
          <el-row class="chart-box mb-16">
            <div class="sub-title__wapper">
              <div class="sub-title">
                <!-- 供应商异常问题数量排名 -->
                <div>{{ $t("reportMod.qualityTitle[20]") }}</div>
                <el-tooltip
                  class="item"
                  effect="dark"
                  placement="top"
                >
                  <div
                    slot="content"
                    class="tooltip-content"
                    v-html="$t('reportMod.qualityTitle[17]')"
                  >
                    <!-- 数据来源:入库订单、退货订单、去年冻结单价<br />
                    过滤条件：<br />
                    1）已入库的搜索时间段累计采购订单情况<br />
                    2）时间范围：入库确认时间<br />
                    3）品类上涨金额=当前阶段单价*当前阶段采购数量-去年冻结单价*当前阶段采购数量<br />
                    4）单个品类上涨金额>0，即记录该品类为上涨品类，总和是各品类的上涨金额相加<br />
                    5）举例：若当前阶段，只有1月的采购单价是高于去年冻结单价，即只计算1月的上涨金额<br />
                    6）已入库的搜索时间段采购订单单价>去年冻结单价的采购金额数据才统计<br /> -->
                  </div>
                  <i class="el-icon-warning info-icon" />
                </el-tooltip>
              </div>
              <div
                class="more"
                @click="showMoreInfo(5)"
              >
                {{ $t("common.more") }}&gt;
              </div>
            </div>
            <barChart :chart-data="supplierData" :comActive="changeTab" />
          </el-row>
        </el-col>
      </el-row>
    </el-main>
  </el-container>
</template>
<script>
import { isNull, toThousand, toNumber } from '@/utils'
import PieChart from './chart/PieChart'
import barChart from './chart/barChart'
import gaugeChart from './chart/gaugeChart'
import efficiencyChart from './chart/efficiencyChart'
import moreInfo from './moreInfo.vue'
import OrganizationSelector from 'lib@/components/organization-selector'
import { geti18n } from '@/main'
const i18n = geti18n()

export default {
  name: 'SupplierAnalysis',
  components: {
    PieChart,
    gaugeChart,
    efficiencyChart,
    OrganizationSelector,
    barChart
  },
  filters: {
    toThousand (value) {
      return toThousand(value)
    },
    toFixed2 (value) {
      const numberValue = Number(value)
      if (!isNaN(numberValue)) {
        return numberValue.toFixed(2)
      }
      return value
    },
    getAmount (value, data) {
      if (data == 0) {
        return 0
      } else {
        return (value / data) * 100
      }
    }
  },
  data () {
    return {
      queryParams: {
        // 选择查询条件
        organizationTypeCode: null,
        level: null,
        organizationId: '',
        fullPathId: null,
        startTime: new Date().getFullYear() + '-' + '01' + '-' + '01',
        endTime: ''
      },
      blockCountData: {
        // title的四项显示
        curAmount: 0,
        yoyRate: 0,
        crAmount: 0,
        crTragetAmount: 0,
        crRate: 0,
        crTragetRate: 0,
        upMaterialAmount: 0,
        yoyUpMaterialRate: 0
      },
      gaugeChart: {
        // 来料批次合格率（%）
        data: 0
      },
      efficiencyChartData: {
        // 异常处理效率
        xAsixData: [],
        data1: [],
        data2: [],
        data3: []
      },
      categoryData: {
        // 品类异常问题数量排名
        xAsixData: [],
        data: []
      },
      supplierData: {
        // 供应商异常问题数量排名
        xAsixData: [],
        data: []
      },
      incomingData: {
        // 来料异常问题原因分布
        seriesData: []
      },
      processData: {
        // 制程异常问题原因分布
        seriesData: []
      },
      routerChange: 0,
      changeTab: 0
    }
  },
  computed: {
    tabChange () {
      return this.$attrs['changeTab']
    }
  },
  watch: {
    queryParams: {
      handler () {
        this.getData()
      },
      deep: true
    },
    tabChange: {
      handler (val) {
        if (val > 1) {
          this.changeTab += 1
        }
      },
      deep: true
    },
    routerChange: {
      handler () {
        this.changeTab += 1
      },
      deep: true
    }
  },
  activated () {
    this.routerChange += 1
  },
  created () {
    var date = new Date()
            var year = date.getFullYear()
            var month = date.getMonth() + 1
            var day = date.getDate()
            if (month < 10) {
                month = '0' + month
            }
            if (day < 10) {
                day = '0' + day
            }
      this.queryParams.endTime = year + '-' + month + '-' + day
  },
  mounted () {

  },
  methods: {
    getData () {
      this.$http({
        url: '/api-pef/report/quantity',
        method: 'POST',
        loading: true,
        data: {
            'orgType': this.queryParams.organizationTypeCode, // 组织类型，如GROUP、BU、OU
            'orgId': this.queryParams.organizationId,
            'startTime': this.queryParams.startTime, // 提供到天，保证月份准确就好，具体哪天可以随便填
            'endTime': this.queryParams.endTime
        }
      }).then(data => {
          let result = data.data
          this.blockCountData.curAmount = result.itemExceptionCount// 来料异常问题单数
          this.blockCountData.crAmount = result.processExceptionCount// 制程异常问题单数
          this.blockCountData.crRate = result.d8ReportCount// 8D报告单数
          this.blockCountData.upMaterialAmount = result.returnOrderCount// 退货单数量
          this.gaugeChart.data = result.itemPassPercent// 来料批次合格率(%)

          // 异常处理效率
          this.efficiencyChartData.xAsixData = []
          this.efficiencyChartData.data1 = []
          this.efficiencyChartData.data2 = []
          this.efficiencyChartData.data3 = []
          for (var i in result.exceptionHandlerMap) {
            this.efficiencyChartData.xAsixData.push(i)
            this.efficiencyChartData.data1.push(result.exceptionHandlerMap[i][0])
            this.efficiencyChartData.data2.push(result.exceptionHandlerMap[i][1])
            this.efficiencyChartData.data3.push(parseInt(result.exceptionHandlerMap[i][1] / result.exceptionHandlerMap[i][0] * 100))
          }

          // 来料异常原因分布
          this.incomingData.seriesData = []
          let incomingDataSeries = []
          for (var u in result.itemExceptionPercentMap) {
            let obj = {
              value: result.itemExceptionPercentMap[u],
              name: u
            }
            incomingDataSeries.push(obj)
          }
          this.incomingData.seriesData = incomingDataSeries

          // 制程异常原因分布
          this.processData.seriesData = []
          let processDataSeries = []
          for (var u in result.processExceptionPercentMap) {
            let obj = {
              value: result.processExceptionPercentMap[u],
              name: u
            }
            processDataSeries.push(obj)
          }
          this.processData.seriesData = processDataSeries

          // 品类异常问题数量排行
          this.categoryData.xAsixData = []
          this.categoryData.data = []
          for (var i in result.categoryExceptionMap) {
            this.categoryData.xAsixData.push(i)
            this.categoryData.data.push(result.categoryExceptionMap[i])
          }

          // 供应商异常问题处理排行
          this.supplierData.xAsixData = []
          this.supplierData.data = []
          for (var i in result.vendorExceptionMap) {
            this.supplierData.xAsixData.push(i)
            this.supplierData.data.push(result.vendorExceptionMap[i])
          }
      })
        .catch(err => {
          console.log(err)
        })

      // let result = {
      //   itemExceptionCount: 1,     // 来料异常问题单数
      //   processExceptionCount: 2,  // 制程异常问题单数
      //   d8ReportCount: 3,          // 8D报告单数
      //   returnOrderCount: 4,       // 退货单数量
      //   itemPassPercent: 51.99,    // 来料批次合格率(%)
      //   itemExceptionPercentMap: { // 来料异常原因分布
      //     '退货类型1': 50,
      //     '退货类型2': 20,
      //     '退货类型3': 10,
      //     '退货类型4': 20,
      //   },
      //   processExceptionPercentMap: { // 制程异常原因分布
      //     '异常问题类型1': 50,
      //     '异常问题类型2': 30,
      //     '异常问题类型3': 10,
      //     '异常问题类型4': 10,
      //   },
      //   categoryExceptionMap: { // 品类异常问题数量排行
      //     '品类1': 100,
      //     '品类2': 10,
      //     '品类3': 5,
      //     '品类4': 1,
      //   },
      //   vendorExceptionMap: {      // 供应商异常问题处理排行
      //     '供应商1': 10,
      //     '供应商2': 5,
      //     '供应商3': 2,
      //   },
      //   exceptionHandlerMap: {     // 异常处理效率
      //     '1月份': [5, 3],       // [xx] 数组中有2个值，第一个代表异常问题数量，第二个代表已处理问题数量
      //     '2月份': [8, 8],
      //     '3月份': [7, 2],
      //   }
      // }
    },
    clearFullPathId () {
      this.queryParams.organizationId = ''
      this.queryParams.fullPathId = null
    },
    selectHandle (node, value, scope) {
      const { fullPathId } = node
      scope.fullPathId = fullPathId
    },
    showMoreInfo (type) {
      const tab = {
        component: moreInfo,
        name: `moreInfo_${type}`,
        params: {
          ...this.queryParams,
          type
        }
      }
      switch (type) {
        case 1:
          tab.title = this.$t('reportMod.qualityTitle[7]') // 月降本趋势
          break
        case 2:
          tab.title = this.$t('reportMod.qualityTitle[9]')
          break
        case 3:
          tab.title = this.$t('reportMod.qualityTitle[11]') // 品类降本达成率排名
          break
        case 4:
          tab.title = this.$t('reportMod.qualityTitle[19]') // 品类降本金额区间占比
          break
        case 5:
          tab.title = this.$t('reportMod.qualityTitle[16]') // 品类上涨金额排名
          break
      }
      tab.params.name = tab.title
      this.$emit('tab-add', tab)
    }
  }
}
</script>
<style scoped lang="scss">
.purchase-analysis-container {
  padding: 16px;
  .tooltip-content {
    font-size: 12px;
    color: #ffffff;
    line-height: 18px;
  }
  .translation-title {
    font-size: 20px;
    font-weight: bold;
    color: #242526;
    line-height: 26px;
  }
  .sub-title__wapper {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 10px;
  }
  .mb-0 {
    margin-bottom: 0;
  }
  .sub-title {
    font-size: 14px;
    font-weight: bold;
    color: #242526;
    display: flex;
    align-items: center;
  }
  .year {
    width: 100%;
  }
  .mb-16 {
    margin-bottom: 16px;
  }
  .block-count {
    width: 100%;
    height: 80px;
    padding: 10px 16px;
    box-sizing: border-box;
    background: url("../../../../../assets/images/bg@x2.png") no-repeat center;
    background-size: cover;
    // display: flex;
    // flex-direction: column;
    // justify-content: space-between;
    // align-items: center;
  }
  .block-count-active {
    background: url("../../../../../assets/images/bg@x2_active.png") no-repeat
      center;
    background-size: cover;
  }
  .block-count__title {
    font-size: 14px;
    color: #ffffff;
    line-height: 18px;
    position: relative;
    word-break: keep-all;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    width: 100%;
    .block-count__title-sub {
      word-break: keep-all;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      width: 100%;
    }
  }
  .block-count__title_right {
    text-align: right;
    // position: absolute;
    // right: 0;
    // top: 0;
    // width: 35%;
  }
  .block-count__value {
    font-size: 24px;
    font-weight: bold;
    color: #ffffff;
    line-height: 50px;
    text-align: center;
    // display: flex;
    // align-items: center;
    // justify-content: center;
  }
  .more {
    font-size: 12px;
    color: #409eff;
    line-height: 16px;
    cursor: pointer;
  }
  .info-icon {
    color: #409eff;
    font-size: 16px;
    margin-left: 3px;
  }
  .chart-box {
    border: 1px solid #e8e9ef;
    padding: 12px 16px;
  }
  .no-border {
    border: none;
  }
  .progress-box {
    height: 60px;
    padding: 10px 0;
    box-sizing: border-box;
    color: #242526;
  }
  .progress-text {
    margin-top: 8px;
    font-size: 12px;
  }
  .progress-rate {
    text-align: right;
    color: #707070;
  }
}
</style>
