<template>
  <el-container
    class="purchase-analysis-container"
    direction="vertical"
  >
    <el-main>
      <el-row class="mb-16">
        <el-col :span="6">
          <!-- 采购降本 -->
          <span class="translation-title">{{
            $t("reportMod.costReductionAnalysis")
          }}</span>
        </el-col>
        <el-col :span="18">
          <el-row :gutter="32">
            <!-- 事业部 -->
            <el-col :span="4">
              <DictSelect
                v-model="queryParams.organizationTypeCode"
                code="ORG_TYPE"
                custom-select-type="ORG_TYPE"
                @change="clearFullPathId"
              />
            </el-col>
            <!-- 事业部 -->
            <el-col :span="4">
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
            <!-- 品类 -->
            <el-col :span="4">
              <DictSelect
                v-model="queryParams.level"
                code="CATEGORY"
                custom-select-type="CATEGORY"
              />
            </el-col>
            <!-- 年份 -->
            <el-col :span="4">
              <el-date-picker
                v-model="queryParams.year"
                class="year"
                type="year"
                value-format="yyyy"
              />
            </el-col>
            <!-- 开始月份 -->
            <el-col :span="4">
              <DictSelect
                v-model="queryParams.startMonth"
                code="MONTH"
                :dict-class="dictClass"
              />
            </el-col>
            <!-- 结束月份 -->
            <el-col :span="4">
              <DictSelect
                v-model="queryParams.endMonth"
                code="MONTH"
                :dict-class="dictClass"
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
              <!-- 目前采购金额（元） -->
              <el-row>
                <el-col :span="blockCountData.yoyRate ? 14 : 24">
                  <el-tooltip
                    class="item"
                    effect="dark"
                    :content="$t('reportMod.costReductionTitle[0]')"
                    placement="top-end"
                  >
                    <div class="block-count__title-sub">
                      {{ $t("reportMod.costReductionTitle[0]") }}
                    </div>
                  </el-tooltip>
                </el-col>
                <el-col
                  v-if="blockCountData.yoyRate"
                  :span="10"
                >
                  <!-- 同比下降 -->
                  {{
                    $t("reportMod.costReductionTitle[1]") +
                      blockCountData.yoyRate
                  }}%
                </el-col>
              </el-row>
            </div>
            <div class="block-count__value">
              {{ blockCountData.curAmount | toThousand | toFixed2 }}
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="block-count">
            <!-- 采购降本金额（元） -->
            <div class="block-count__title">
              <el-tooltip
                class="item"
                effect="dark"
                :content="$t('reportMod.costReductionTitle[2]')"
                placement="top-end"
              >
                <div class="block-count__title-sub">
                  {{ $t("reportMod.costReductionTitle[2]") }}
                </div>
              </el-tooltip>
            </div>
            <div class="block-count__value">
              {{ blockCountData.crAmount | toThousand | toFixed2 }}
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="block-count block-count-active">
            <div class="block-count__title">
              <el-row>
                <el-col :span="blockCountData.crTragetRate ? 14 : 24">
                  <!-- 采购降本率（%） -->
                  <el-tooltip
                    class="item"
                    effect="dark"
                    :content="$t('reportMod.costReductionTitle[3]')"
                    placement="top-end"
                  >
                    <div class="block-count__title-sub">
                      {{ $t("reportMod.costReductionTitle[3]") }}
                    </div>
                  </el-tooltip>
                </el-col>
                <el-col :span="10">
                  <div class="block-count__title_right">
                    {{
                      $t("reportMod.costReductionTitle[6]") + blockCountData.crTragetRate
                    }}%
                  </div>
                </el-col>
              </el-row>
            </div>
            <div class="block-count__value">
              {{ blockCountData.crRate }}
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="block-count">
            <div class="block-count__title">
              <!-- 上涨物料金额（元） -->
              <el-row>
                <el-col :span="blockCountData.yoyUpMaterialRate ? 14 : 24">
                  <el-tooltip
                    class="item"
                    effect="dark"
                    :content="$t('reportMod.costReductionTitle[4]')"
                    placement="top-end"
                  >
                    <div class="block-count__title-sub">
                      {{ $t("reportMod.costReductionTitle[4]") }}
                    </div>
                  </el-tooltip>
                </el-col>
                <el-col
                  v-if="blockCountData.yoyUpMaterialRate"
                  :span="10"
                >
                  {{
                    $t("reportMod.costReductionTitle[5]") +
                      blockCountData.yoyUpMaterialRate
                  }}%
                </el-col>
              </el-row>
            </div>
            <div class="block-count__value">
              {{ blockCountData.upMaterialAmount | toThousand | toFixed2 }}
            </div>
          </div>
        </el-col>
      </el-row>
      <el-row :gutter="32">
        <el-col :span="12">
          <el-row class="chart-box mb-16">
            <div class="sub-title__wapper">
              <div class="sub-title">
                <!-- 月降本趋势 -->
                <div>{{ $t("reportMod.costReductionTitle[7]") }}</div>
                <el-tooltip
                  class="item"
                  effect="dark"
                  placement="top"
                >
                  <div
                    slot="content"
                    class="tooltip-content"
                    v-html="$t('reportMod.costReductionTitle[8]')"
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
              <div
                class="more"
                @click="showMoreInfo(1)"
              >
                {{ $t("common.more") }}&gt;
              </div>
            </div>
            <monthsTrendChart :chart-data="monthsTrend" :comActive="changeTab" />
          </el-row>
        </el-col>
        <el-col :span="12">
          <el-row class="chart-box mb-16">
            <div class="sub-title__wapper">
              <div class="sub-title">
                <!-- 年度累计降本率 -->
                <div>{{ $t("reportMod.costReductionTitle[9]") }}</div>
                <el-tooltip
                  class="item"
                  effect="dark"
                  placement="top"
                >
                  <div
                    slot="content"
                    class="tooltip-content"
                    v-html="$t('reportMod.costReductionTitle[10]')"
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
                @click="showMoreInfo(2)"
              >
                {{ $t("common.more") }}&gt;
              </div>
            </div>
            <yearCumulativeRateChart :chart-data="yearCumulativeRate" :comActive="changeTab" />
          </el-row>
        </el-col>
      </el-row>
      <el-row :gutter="32">
        <el-col :span="8">
          <el-row class="chart-box mb-16">
            <div class="sub-title__wapper">
              <div class="sub-title">
                <!-- 品类降本达成率排名 -->
                <div>{{ $t("reportMod.costReductionTitle[11]") }}</div>
                <el-tooltip
                  class="item"
                  effect="dark"
                  placement="top"
                >
                  <div
                    slot="content"
                    class="tooltip-content"
                    v-html="$t('reportMod.costReductionTitle[12]')"
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
            <div
              v-for="(item, index) in categoryReachRate"
              :key="index + 'ww'"
            >
              <el-row :gutter="32">
                <el-col :span="4">
                  <div class="progress-box">
                    {{ item.categoryName }}
                  </div>
                </el-col>
                <el-col :span="20">
                  <div class="progress-box">
                    <el-progress
                      v-if="item.rate || item.rate == 0"
                      :percentage="item.rate > 100 ? 100 : item.rate"
                      :stroke-width="10"
                      color="#409EFF"
                      :show-text="false"
                    />
                    <el-row class="progress-text">
                      <el-col :span="12">
                        <div>
                          {{
                            $t("reportMod.costReductionTitle[13]") +
                              item.amount
                          }}%
                        </div>
                      </el-col>
                      <el-col :span="12">
                        <div class="progress-rate">
                          {{ item.rate }}%
                        </div>
                      </el-col>
                    </el-row>
                  </div>
                </el-col>
              </el-row>
            </div>
          </el-row>
        </el-col>
        <el-col :span="8">
          <el-row class="chart-box mb-16">
            <div class="sub-title__wapper">
              <div class="sub-title">
                <!-- 品类降本金额区间占比（%） -->
                <div>{{ $t("reportMod.costReductionTitle[14]") }}</div>
                <el-tooltip
                  class="item"
                  effect="dark"
                  placement="top"
                >
                  <div
                    slot="content"
                    class="tooltip-content"
                    v-html="$t('reportMod.costReductionTitle[15]')"
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
            <PieChart :chart-data="categoryAmountRate" :comActive="changeTab" />
          </el-row>
        </el-col>
        <el-col :span="8">
          <el-row class="chart-box mb-16">
            <div class="sub-title__wapper">
              <div class="sub-title">
                <!-- 品类上涨金额排名 -->
                <div>{{ $t("reportMod.costReductionTitle[16]") }}</div>
                <el-tooltip
                  class="item"
                  effect="dark"
                  placement="top"
                >
                  <div
                    slot="content"
                    class="tooltip-content"
                    v-html="$t('reportMod.costReductionTitle[17]')"
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
            <div
              v-for="(item, index) in categoryUpAmount"
              :key="index + 'ew'"
            >
              <el-row :gutter="32">
                <el-col :span="4">
                  <div class="progress-box">
                    {{ item.categoryName }}
                  </div>
                </el-col>
                <el-col :span="20">
                  <div class="progress-box">
                    <el-progress
                      v-if="item.amount || item.amount == 0"
                      :percentage="
                        item.amount | getAmount(categoryUpAmount[0].amount)
                      "
                      :stroke-width="10"
                      color="#409EFF"
                      :show-text="false"
                    />
                    <el-row class="progress-text">
                      <el-col :span="12">
                        <div>
                          {{
                            $t("reportMod.costReductionTitle[18]") + item.amount
                          }}
                        </div>
                      </el-col>
                    </el-row>
                  </div>
                </el-col>
              </el-row>
            </div>
          </el-row>
        </el-col>
      </el-row>
    </el-main>
  </el-container>
</template>
<script>
import { isNull, toThousand, toNumber } from '@/utils'
import PieChart from './chart/PieChart'
import monthsTrendChart from './chart/monthsTrendChart'
import yearCumulativeRateChart from './chart/yearCumulativeRateChart'
import moreInfo from './moreInfo.vue'
import OrganizationSelector from 'lib@/components/organization-selector'
import { createDictClass } from '@/library/utils/dict/dict-utils'
import { geti18n } from '@/main'
import { costReductionApi } from 'modb@/reportAnalysis/api'
const i18n = geti18n()

export default {
  name: 'SupplierAnalysis',
  components: {
    PieChart,
    monthsTrendChart,
    yearCumulativeRateChart,
    OrganizationSelector
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
      dictClass: createDictClass({
        'MONTH': [
          { id: 1, value: 1, label: i18n.t('reportMod.January') },
          { id: 2, value: 2, label: i18n.t('reportMod.February') },
          { id: 3, value: 3, label: i18n.t('reportMod.March') },
          { id: 4, value: 4, label: i18n.t('reportMod.April') },
          { id: 5, value: 5, label: i18n.t('reportMod.May') },
          { id: 6, value: 6, label: i18n.t('reportMod.June') },
          { id: 7, value: 7, label: i18n.t('reportMod.July') },
          { id: 8, value: 8, label: i18n.t('reportMod.Augest') },
          { id: 9, value: 9, label: i18n.t('reportMod.September') },
          { id: 10, value: 10, label: i18n.t('reportMod.October') },
          { id: 11, value: 11, label: i18n.t('reportMod.November') },
          { id: 12, value: 12, label: i18n.t('reportMod.December') }
        ] }, false),
      queryParams: {
        organizationTypeCode: null,
        level: null,
        year: null,
        organizationId: null,
        fullPathId: null,
        startMonth: null,
        endMonth: null
      },
      originData: null,
      blockCountData: {
        curAmount: 0,
        yoyRate: 0,
        crAmount: 0,
        crTragetAmount: 0,
        crRate: 0,
        crTragetRate: 0,
        upMaterialAmount: 0,
        yoyUpMaterialRate: 0
      },
      chinaMap: {
        seriesData: []
      },
      monthsTrend: {
        xAsixData: [],
        yAsixData: [[], [], []]
      },
      yearCumulativeRate: {
        xAsixData: [],
        yAsixData: [[], []],
        crTragetRate: []
      },
      categoryAmountRate: {
        seriesData: [],
        legend: [],
        color: []
      },
      categoryReachRate: [],
      categoryUpAmount: [],
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
        this.queryParamsChangeHandler()
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
  },
  mounted () {
    this.queryParams.year = `${this.$dayjs().year() - 1}`
    this.queryParams.startMonth = 1
    this.queryParams.endMonth = 12
    this.queryParams.organizationTypeCode = 'GROUP'
    this.$nextTick(() => {
      this.queryParams.organizationId = 291915626131456
    })
    this.queryParams.level = '1'
    this.queryParams.year = '2022'
    this.getSetting()
  },
  methods: {
    getSetting () {
      this.$http({
        url: '/api-report/config/queryConfig',
        method: 'post',
        params: {},
        loading: true
      })
      .then(res => {
        let data = res.data
        this.chinaMap.areaOne = data.supplier.areaOne
        this.chinaMap.areaTwoStart = data.supplier.areaTwoStart
        this.chinaMap.areaTwoEnd = data.supplier.areaTwoEnd
        this.chinaMap.areaThreeStart = data.supplier.areaThreeStart
        this.chinaMap.areaThreeEnd = data.supplier.areaThreeEnd
        this.chinaMap.areaFour = data.supplier.areaFour
      })
      .catch(err => {
        console.log(err)
      })
    },
    clearFullPathId () {
      this.queryParams.organizationId = null
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
          tab.title = this.$t('reportMod.costReductionTitle[7]') // 月降本趋势
          break
        case 2:
          tab.title = this.$t('reportMod.costReductionTitle[9]')
          break
        case 3:
          tab.title = this.$t('reportMod.costReductionTitle[11]') // 品类降本达成率排名
          break
        case 4:
          tab.title = this.$t('reportMod.costReductionTitle[19]') // 品类降本金额区间占比
          break
        case 5:
          tab.title = this.$t('reportMod.costReductionTitle[16]') // 品类上涨金额排名
          break
      }
      tab.params.name = tab.title
      this.$emit('tab-add', tab)
    },
    async queryParamsChangeHandler () {
      const params = this.queryParams
      const checkList = ['organizationTypeCode', 'year']
      if (checkList.some(key => !(params[key] || params[key] == 0))) {
        return
      }
      let getData = {}
       if (window.location.host === 'sccdemosyy.meicloud.com' || window.location.host === 'localhost:8082') {
            getData = {
              'categoryAmountRate': {
                  // 'legend': ['降本金额(X<=50万元)', '降本金额(50万元<X<=100万元)', '降本金额(100万元<X<=200万元)', '降本金额(X>200万元)'],
                  'legend': [this.$t('cusEntry.supplement20250211.costReductionAmountX50k'), 
                              this.$t('cusEntry.supplement20250211.reduceCostAmount'), 
                              this.$t('cusEntry.supplement20250211.reducedCostAmount'), 
                              this.$t('cusEntry.supplement20250211.lowerCostAmountOverTwoMillion')
                            ],
                  'lineSeriesData': [],
                  'seriesData': [{
                      'name': this.$t('cusEntry.supplement20250211.costReductionAmountX50k'),  // '降本金额(X<=50万元)'
                      'value': 23
                  }, {
                      'name': this.$t('cusEntry.supplement20250211.reduceCostAmount'),  // '降本金额(50万元<X<=100万元)'
                      'value': 1
                  }, {
                      'name': this.$t('cusEntry.supplement20250211.reducedCostAmount'),  // '降本金额(100万元<X<=200万元)'
                      'value': 2
                  }, {
                      'name': this.$t('cusEntry.supplement20250211.lowerCostAmountOverTwoMillion'),  // '降本金额(X>200万元)'
                      'value': 2
                  }],
                  'xAxisData': []
              },
              'categoryReachRate': [{
                  'amount': 88.7400,
                  'categoryName': this.$t('cusEntry.supplement20250211.wireConnectionAccessories'),  // '接线连接配件'
                  'rate': 4437.0000
              }, {
                  'amount': 66.6700,
                  'categoryName': this.$t('cusEntry.supplement20250211.filmAdhesive'),  // '胶膜'
                  'rate': 3333.5000
              }, {
                  'amount': 8.0000,
                  'categoryName': this.$t('cusEntry.supplement20250211.toolAndToolConsumables'),  // '工具及工具耗材'
                  'rate': 400.0000
              }, {
                  'amount': 7.1000,
                  'categoryName': this.$t('reportMod.carbonCarbonComposite '),  // '碳纤维及碳碳复合材料'
                  'rate': 355.0000
              }, {
                  'amount': 6.8600,
                  'categoryName': this.$t('reportMod.felts'),  // '毡类'
                  'rate': 343.0000
              }],
              'categoryUpAmount': [{
                  'amount': 1259600.0000000000000000,
                  'categoryName': this.$t('reportMod.graphiteProducts'),  // '石墨制品'
                  'rate': null
              }, {
                  'amount': 400000.0000000000000000,
                  'categoryName': this.$t('reportMod.formulatedChemical'),  // '配方化学品'
                  'rate': null
              }, {
                  'amount': 258000.0000000000000000,
                  'categoryName': this.$t('reportMod.fabricatedMetalProduct'),  // '金属加工品'
                  'rate': null
              }, {
                  'amount': 10000.0000000000000000,
                  'categoryName': this.$t('reportMod.carbonCarbonComposite '),  // '碳纤维及碳碳复合材料'
                  'rate': null
              }, {
                  'amount': 7000.0000000000000000,
                  'categoryName': this.$t('reportMod.felts'),  // '毡类'
                  'rate': null
              }],
              'crAmount': 11143600.0000000000000000,
              'crRate': 4.9700,
              'crTragetAmount': 4486872.0000,
              'crTragetRate': 2.000000,
              'curAmount': 11143600.0000000000000000,
              'monthsTrend': {
                  // 'legend': ['目标降本金额', '实际采购降本金额', '月降本达成率'],
                  'legend': [this.$t('reportMod.targetCaptialReduction'), 
                            this.$t('reportMod.actualPurCostReduction'),
                            this.$t('reportMod.reachRateMonth')],
                  'lineSeriesData': [{
                      'data': [72560.0000, 686720.0000, 1144280.0000, 1454080.0000, 0, 0, 549000.0000, 202588.0000, 183088.0000, 175756.0000, 0, 18800.0000],
                      // '目标降本金额'
                      'name': this.$t('reportMod.targetCaptialReduction'),
                      'stack': '',
                      'type': 'bar',
                      'yAxisIndex': null
                  }, {
                      'data': [1708000.0000000000000000, 1246000.0000000000000000, 604000.0000000000000000, 2044000.0000000000000000, 0, 0, 950000.0000000000000000, -373600.0000000000000000, 1575400.0000000000000000, 3519800.0000000000000000, 0, -130000.0000000000000000],
                      // '实际采购降本金额'
                      'name': this.$t('reportMod.actualPurCostReduction'),
                      'stack': '',
                      'type': 'bar',
                      'yAxisIndex': null
                  }, {
                      'data': [2353.9100, 181.4400, 52.7800, 140.5700, 0, 0, 173.0400, -184.4100, 860.4600, 2002.6600, 0, -691.4900],
                      // '月降本达成率'
                      'name': this.$t('reportMod.reachRateMonth'),
                      'stack': '',
                      'type': 'line',
                      'yAxisIndex': null
                  }],
                  'seriesData': [],
                  // 'xAxisData': ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
                  'xAxisData': [this.$t('dataConfMod.timeIntervalList.7'), 
                                this.$t('reportMod.February'), 
                                this.$t('reportMod.March'), 
                                this.$t('reportMod.April'), 
                                this.$t('reportMod.May'), 
                                this.$t('reportMod.June'), 
                                this.$t('reportMod.July'), 
                                this.$t('reportMod.August'), 
                                this.$t('reportMod.September'), 
                                this.$t('reportMod.October'), 
                                this.$t('reportMod.November'), 
                                this.$t('reportMod.December')]
              },
              'upMaterialAmount': 1934600.0000000000000000,
              'yearCumulativeRate': {
                  // 'legend': ['年度累计降本率'],
                  'legend': [this.$t('reportMod.costReductionTitle.9')],
                  'lineSeriesData': [{
                      'data': [47.0800, 7.7800, 3.7400, 3.3400, 3.3400, 3.3400, 3.3500, 3.0100, 3.6100, 5.0500, 5.0500, 4.9700],
                      // 'name': '年度累计降本率',
                      'name': this.$t('reportMod.costReductionTitle.9'),
                      'stack': '',
                      'type': 'bar',
                      'yAxisIndex': null
                  }],
                  'seriesData': [],
                  // 'xAxisData': ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
                     'xAxisData': [this.$t('dataConfMod.timeIntervalList.7'), 
                                this.$t('reportMod.February'), 
                                this.$t('reportMod.March'), 
                                this.$t('reportMod.April'), 
                                this.$t('reportMod.May'), 
                                this.$t('reportMod.June'), 
                                this.$t('reportMod.July'), 
                                this.$t('reportMod.August'), 
                                this.$t('reportMod.September'), 
                                this.$t('reportMod.October'), 
                                this.$t('reportMod.November'), 
                                this.$t('reportMod.December')]
              },
              'yoyRate': 0,
              'yoyUpMaterialRate': 0
          }
      } else {
        const { data } = await costReductionApi.costReductionAnalysis(params)
        getData = data
      }

      const {
        monthsTrend,
        yearCumulativeRate,
        categoryAmountRate,
        categoryReachRate,
        categoryUpAmount,
        curAmount,
        yoyRate,
        crAmount,
        crTragetAmount,
        crRate,
        crTragetRate,
        upMaterialAmount,
        yoyUpMaterialRate
      } = getData

      this.blockCountData = {
        curAmount,
        yoyRate,
        crAmount,
        crTragetAmount,
        crRate,
        crTragetRate,
        upMaterialAmount,
        yoyUpMaterialRate
      }

      this.categoryReachRate = categoryReachRate
      this.categoryUpAmount = categoryUpAmount

      this.monthsTrend.xAsixData = monthsTrend.xAxisData
      let monthsTrendY = []
      monthsTrend.lineSeriesData.forEach(i => {
        monthsTrendY.push(i.data)
      })
      this.monthsTrend.yAsixData = monthsTrendY

      this.yearCumulativeRate.xAsixData = yearCumulativeRate.xAxisData
      let yearCumulativeRateY = []
      yearCumulativeRate.lineSeriesData.forEach(i => {
        let arr = []
        i.data.forEach(item => {
          arr.push(item)
        })
        yearCumulativeRateY.push(arr)
      })
      this.yearCumulativeRate.yAsixData = yearCumulativeRateY
      this.yearCumulativeRate.crTragetRate = []
      for (var i = 0; i < 12; i++) {
        this.yearCumulativeRate.crTragetRate.push(crTragetRate)
      }

      this.categoryAmountRate = {
        ...categoryAmountRate,
        color: ['#4C88FF', '#66AEF4', '#7E6EE8', '#F39E67'],
        seriesOpts: {
          radius: [0, 70],
          center: ['50%', '35%'],
          roseType: 'pie'
        }
      }
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
    .block-count__title-sub{
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
