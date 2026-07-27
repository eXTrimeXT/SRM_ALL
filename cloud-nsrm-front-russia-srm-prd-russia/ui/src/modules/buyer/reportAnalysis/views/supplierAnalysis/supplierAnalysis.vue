<template>
  <el-container
    class="purchase-analysis-container"
    direction="vertical"
  >
    <el-main>
      <el-row class="mb-16">
        <el-col :span="12">
          <!-- 供应商分析 -->
          <span class="translation-title">{{
            $t("reportMod.supplierAnalysis")
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
                :node-type="queryParams.organizationTypeCode"
                :scope="queryParams"
                :placeholder="$t('common.pleaseSelect')"
                @select="selectHandle"
              />
            </el-col>
            <!-- 年份 -->
            <el-col :span="6">
              <el-date-picker
                v-model="queryParams.year"
                class="year"
                type="year"
                value-format="yyyy"
              />
            </el-col>
            <!-- 季度 -->
            <el-col :span="6">
              <DictSelect
                v-model="queryParams.season"
                code="SEASON"
                :dict-class="dictClass"
              />
            </el-col>
          </el-row>
        </el-col>
      </el-row>
      <el-row :gutter="32">
        <el-col :span="6">
          <el-row class="chart-box mb-16">
            <div class="sub-title__wapper">
              <div class="sub-title">
                <!-- 供应商合作年限分布 -->
                <div>{{ $t("reportMod.supplierAnalysisTitle[0]") }}</div>
                <el-tooltip
                  class="item"
                  effect="dark"
                  placement="top"
                >
                  <div
                    slot="content"
                    class="tooltip-content"
                    v-html="$t('reportMod.supplierAnalysisTitle[1]')"
                  >
                    <!-- 根据筛选条件，取维度组织，<br />
                    取数供应商档案供应商供方准入时间（年为单位，若1年2个月为1年；1年7个月为2年）第一次准入日期为准 -->
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
            <PieChart :chart-data="cooperation" :comActive="changeTab" />
          </el-row>
          <el-row class="chart-box mb-16">
            <div class="sub-title__wapper">
              <div class="sub-title">
                <!-- 采购金额供方数量占比 -->
                <div>{{ $t("reportMod.supplierAnalysisTitle[2]") }}</div>
                <el-tooltip
                  class="item"
                  effect="dark"
                  placement="top"
                >
                  <div
                    slot="content"
                    class="tooltip-content"
                    v-html="$t('reportMod.supplierAnalysisTitle[3]')"
                  >
                    <!-- 根据筛选条件，取维度组织里的供应商一共供货总金额所在的区域里占比。<br />
                    金额可配置。采购入库的金额总和，以确认时间为准。 -->
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
            <PieChart :chart-data="purchase" :comActive="changeTab" />
          </el-row>
        </el-col>
        <el-col :span="12">
          <el-row
            :gutter="32"
            class="mb-16"
          >
            <el-col :span="6">
              <div class="block-count">
                <!-- 合格供应商总数 -->
                <el-tooltip
                  class="item"
                  effect="dark"
                  :content="$t('reportMod.qualifiedSupplierTotal')"
                  placement="top-end"
                >
                  <div class="block-count__title">
                    {{ $t("reportMod.qualifiedSupplierTotal") }}
                  </div>
                </el-tooltip>
                <div class="block-count__value">
                  {{ blockCountData.sum }}
                </div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="block-count block-count-active">
                <!-- 活跃供应商总数 -->
                <el-tooltip
                  class="item"
                  effect="dark"
                  :content="$t('reportMod.activeSupplierTotal')"
                  placement="top-end"
                >
                  <div class="block-count__title">
                    {{ $t("reportMod.activeSupplierTotal") }}
                  </div>
                </el-tooltip>
                <div class="block-count__value">
                  {{ blockCountData.activeNum }}
                </div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="block-count">
                <!-- 新增供应商数 -->
                <el-tooltip
                  class="item"
                  effect="dark"
                  :content="$t('reportMod.newSupplierNumber')"
                  placement="top-end"
                >
                  <div class="block-count__title">
                    {{ $t("reportMod.newSupplierNumber") }}
                  </div>
                </el-tooltip>
                <div class="block-count__value">
                  {{ blockCountData.addNum }}
                </div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="block-count">
                <!-- 退出供应商数 -->
                <el-tooltip
                  class="item"
                  effect="dark"
                  :content="$t('reportMod.outVendorNum')"
                  placement="top-end"
                >
                  <div class="block-count__title">
                    {{ $t("reportMod.outVendorNum") }}
                  </div>
                </el-tooltip>
                <div class="block-count__value">
                  {{ blockCountData.outNum }}
                </div>
              </div>
            </el-col>
          </el-row>
          <el-row class="chart-box mb-16 no-border">
            <div class="sub-title__wapper">
              <div class="sub-title">
                <!-- 供应商地图分布 -->
                <div>{{ $t("reportMod.supplierAnalysisTitle[4]") }}</div>
                <el-tooltip
                  class="item"
                  effect="dark"
                  placement="top"
                >
                  <div
                    slot="content"
                    class="tooltip-content"
                    v-html="$t('reportMod.supplierAnalysisTitle[5]')"
                  >
                    <!-- 根据筛选条件，取维度组织，供应商数量分布区域分析，<br />
                    鼠标移上去，弹出具体城市的具体数量。小于5；大于30。可配置。 -->
                  </div>
                  <i class="el-icon-warning info-icon" />
                </el-tooltip>
              </div>
            </div>
            <ChinaMapChart
              :chart-data="chinaMap"
              :comActive="changeTab"
              height="317px"
            />
          </el-row>
          <el-row class="chart-box mb-16">
            <div class="sub-title__wapper">
              <div class="sub-title">
                <!-- 采购金额供方排名 -->
                <div>{{ $t("reportMod.supplierAnalysisTitle[6]") }}</div>
                <el-tooltip
                  class="item"
                  effect="dark"
                  placement="top"
                >
                  <div
                    slot="content"
                    class="tooltip-content"
                    v-html="$t('reportMod.supplierAnalysisTitle[7]')"
                  >
                    <!-- 根据筛选条件，取维度组织，该品类供货的金额排名，展示10个排名。<br />
                    采购入库的金额总和-已入库（确认时间，需要考虑退货） -->
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
            <VerticalBarChart
              :chart-data="purchaseRank"
              :comActive="changeTab"
              height="242px"
            />
          </el-row>
        </el-col>
        <el-col :span="6">
          <el-row class="chart-box mb-16">
            <div class="sub-title__wapper">
              <div class="sub-title">
                <!-- 供应商等级占比 -->
                <div>{{ $t("reportMod.supplierAnalysisTitle[8]") }}</div>
                <el-tooltip
                  class="item"
                  effect="dark"
                  placement="top"
                >
                  <div
                    slot="content"
                    class="tooltip-content"
                    v-html="$t('reportMod.supplierAnalysisTitle[9]')"
                  >
                    <!-- 根据筛选条件，取维度组织的供应商等级占比；在绩效中取，分成优、良、合格、不合格四个等级，若是分数亦折算成优>=90、良>=80、合格>=60、不合格<60；<br />
                    最后一次的绩效成绩。 -->
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
            <PieChart :chart-data="level" :comActive="changeTab" />
          </el-row>
          <el-row class="chart-box mb-16">
            <div class="sub-title__wapper">
              <div class="sub-title">
                <!-- 品类供方数占比 -->
                <div>{{ $t("reportMod.supplierAnalysisTitle[10]") }}</div>
                <el-tooltip
                  class="item"
                  effect="dark"
                  placement="top"
                >
                  <div
                    slot="content"
                    class="tooltip-content"
                    v-html="$t('reportMod.supplierAnalysisTitle[11]')"
                  >
                    <!-- 根据筛选条件，取维度组织，品类供方数区间占比。准入合格的供应商。 -->
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
            <PieChart :chart-data="category" :comActive="changeTab" />
          </el-row>
        </el-col>
      </el-row>
    </el-main>
  </el-container>
</template>
<script>
import { isNull, toThousand, toNumber } from '@/utils'
import PieChart from './chart/PieChart'
import ChinaMapChart from './chart/ChinaMapChart'
import VerticalBarChart from './chart/VerticalBarChart'
import moreInfo from './moreInfo.vue'
import OrganizationSelector from 'lib@/components/organization-selector'
import { createDictClass } from '@/library/utils/dict/dict-utils'
import { costReductionApi } from 'modb@/reportAnalysis/api'
import { geti18n } from '@/main'
const i18n = geti18n()
const dictClass = createDictClass({
  'SEASON': [{ id: 1, value: 1, label: i18n.t('time.quarterList[0]') },
    { id: 2, value: 2, label: i18n.t('time.quarterList[1]') },
    { id: 3, value: 3, label: i18n.t('time.quarterList[2]') },
    { id: 4, value: 4, label: i18n.t('time.quarterList[3]') },
    { id: 5, value: 0, label: i18n.t('reportMod.allYear') }]
  }, false)

export default {
  name: 'SupplierAnalysis',
  components: {
    VerticalBarChart,
    PieChart,
    ChinaMapChart,
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
    }
  },
  data () {
    return {
      dictClass: dictClass,
      queryParams: {
        organizationTypeCode: null,
        level: null,
        year: null,
        organizationId: null,
        fullPathId: null,
        season: null
      },
      originData: null,
      blockCountData: {
        activeNum: 0,
        addNum: 0,
        outNum: 0,
        sum: 0
      },
      cooperation: {
        seriesData: [],
        legend: [],
        color: []
      },
      purchase: {
        seriesData: [],
        legend: [],
        color: []
      },
      chinaMap: {
        seriesData: []
      },
      purchaseRank: {
        xAsixData: [],
        yAsixData: []
      },
      level: {
        seriesData: [],
        legend: [],
        color: []
      },
      category: {
        seriesData: [],
        legend: [],
        color: []
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
    this.queryParams.season = 0
    this.queryParams.organizationTypeCode = 'GROUP'
    this.$nextTick(() => {
      this.queryParams.organizationId = 291915626131456
    })
    this.queryParams.year = '2022'

    // this.queryParams.season = this.getCurrentQuarter();
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
          tab.title = this.$t('reportMod.supplierAnalysisTitle[12]') // 供应商合作年限
          break
        case 2:
          tab.title = this.$t('reportMod.supplierAnalysisTitle[13]') // 采购金额供方占比
          break
        case 3:
          tab.title = this.$t('reportMod.supplierAnalysisTitle[6]') // 采购金额供方排名
          break
        case 4:
          tab.title = this.$t('reportMod.supplierAnalysisTitle[8]') // 供应商等级占比
          break
        case 5:
          tab.title = this.$t('reportMod.supplierAnalysisTitle[10]') // 品类供方数占比
          break
      }
      tab.params.name = tab.title
      this.$emit('tab-add', tab)
    },
    getCurrentQuarter () {
      const month = this.$dayjs().month()
      const quarterMap = new Map()
      quarterMap.set([1, 2, 3], 1)
      quarterMap.set([4, 5, 6], 2)
      quarterMap.set([7, 8, 9], 3)
      quarterMap.set([10, 11, 12], 4)
      for (let [key, value] of quarterMap.entries()) {
        if (key.includes(month)) {
          return value
        }
      }
    },
    barClick (params, type) {
      const { dataIndex } = params
      const data = this.originData[type]
      const reverseData = []
      data.forEach(item => reverseData.unshift(item))
      const { fullPathId, organizationName } = reverseData[dataIndex]
      this.getPurchaseAnalysisCategory(fullPathId, organizationName)
    },
    async queryParamsChangeHandler () {
      const params = this.queryParams
      const checkList = ['organizationTypeCode', 'season', 'year']
      if (checkList.some(key => !(params[key] || params[key] == 0))) {
        return
      }
      const { data } = await costReductionApi.supplierAnalysis(params)
      let setData = {}
      if (window.location.host === 'sccdemosyy.meicloud.com' || window.location.host === 'localhost:8082') {
            setData = {
              'activeNum': 62,
              'addNum': 22,
              'category': {
                  'legend': ['数量(X<=2)', '数量(2<X<=5)', '数量(5<X<=10)', '数量(X>10)'],
                  'lineSeriesData': [],
                  'seriesData': [{
                      'name': '数量(X<=2)',
                      'value': 9
                  }, {
                      'name': '数量(2<X<=5)',
                      'value': 5
                  }, {
                      'name': '数量(5<X<=10)',
                      'value': 7
                  }, {
                      'name': '数量(X>10)',
                      'value': 4
                  }],
                  'xAxisData': []
              },
              'chinaMap': {
                  'legend': ['北京', '天津', '河北', '山西', '内蒙古', '辽宁', '吉林', '黑龙江', '上海', '江苏', '浙江', '安徽', '福建', '江西', '山东', '河南', '湖北', '湖南', '广东', '广西', '海南', '重庆', '四川', '贵州', '云南', '西藏', '陕西', '甘肃', '青海', '宁夏', '新疆'],
                  'lineSeriesData': [],
                  'seriesData': [{
                      'name': '北京',
                      'value': 29
                  }, {
                      'name': '天津',
                      'value': 3
                  }, {
                      'name': '河北',
                      'value': 3
                  }, {
                      'name': '山西',
                      'value': 3
                  }, {
                      'name': '内蒙古',
                      'value': 3
                  }, {
                      'name': '辽宁',
                      'value': 2
                  }, {
                      'name': '吉林',
                      'value': 2
                  }, {
                      'name': '黑龙江',
                      'value': 2
                  }, {
                      'name': '上海',
                      'value': 2
                  }, {
                      'name': '江苏',
                      'value': 2
                  }, {
                      'name': '浙江',
                      'value': 2
                  }, {
                      'name': '安徽',
                      'value': 2
                  }, {
                      'name': '福建',
                      'value': 2
                  }, {
                      'name': '江西',
                      'value': 2
                  }, {
                      'name': '山东',
                      'value': 2
                  }, {
                      'name': '河南',
                      'value': 2
                  }, {
                      'name': '湖北',
                      'value': 2
                  }, {
                      'name': '湖南',
                      'value': 2
                  }, {
                      'name': '广东',
                      'value': 8
                  }, {
                      'name': '广西',
                      'value': 2
                  }, {
                      'name': '海南',
                      'value': 3
                  }, {
                      'name': '重庆',
                      'value': 3
                  }, {
                      'name': '四川',
                      'value': 9
                  }, {
                      'name': '贵州',
                      'value': 3
                  }, {
                      'name': '云南',
                      'value': 15
                  }, {
                      'name': '西藏',
                      'value': 2
                  }, {
                      'name': '陕西',
                      'value': 2
                  }, {
                      'name': '甘肃',
                      'value': 2
                  }, {
                      'name': '青海',
                      'value': 2
                  }, {
                      'name': '宁夏',
                      'value': 2
                  }, {
                      'name': '新疆',
                      'value': 2
                  }],
                  'xAxisData': []
              },
              'cooperation': {
                  'legend': ['1年以内', '1-3年', '3-5年', '5年以上'],
                  'lineSeriesData': [],
                  'seriesData': [{
                      'name': '1年以内',
                      'value': 22
                  }, {
                      'name': '1-3年',
                      'value': 25
                  }, {
                      'name': '3-5年',
                      'value': 20
                  }, {
                      'name': '5年以上',
                      'value': 71
                  }],
                  'xAxisData': []
              },
              'level': {
                  'legend': ['优', '良', '合格', '不合格'],
                  'lineSeriesData': [],
                  'seriesData': [{
                      'name': '优',
                      'value': 18
                  }, {
                      'name': '良',
                      'value': 21
                  }, {
                      'name': '合格',
                      'value': 27
                  }, {
                      'name': '不合格',
                      'value': 8
                  }],
                  'xAxisData': []
              },
              'outNum': 0,
              'purchase': {
                  'legend': ['金额（X<=10万）', '金额（10万<X<=50万）', '金额（50万<X<=100万）', '金额（X>=100万）'],
                  'lineSeriesData': [],
                  'seriesData': [{
                      'name': '金额（X<=10万）',
                      'value': 10
                  }, {
                      'name': '金额（10万<X<=50万）',
                      'value': 31
                  }, {
                      'name': '金额（50万<X<=100万）',
                      'value': 28
                  }, {
                      'name': '金额（X>=100万）',
                      'value': 60
                  }],
                  'xAxisData': []
              },
              'purchaseRank': {
                  'legend': ['鸿辉原料加工有限公司', '广东小官网有限公司', '佛山市顺德区北滘金骏尚塑料五金厂', '广安冰鑫电器技术咨询有限公司', '广东威特真空电子制造有限公司', '浙江豪大情', '广东省佰钧成科技有限公司', '广东腰子有限公司', '青岛晨展五金索具有限公司', '广东星辉精密'],
                  'lineSeriesData': [],
                  'seriesData': [{
                      'name': '鸿辉原料加工有限公司',
                      'value': 35100000.0000000000000000
                  }, {
                      'name': '广东小官网有限公司',
                      'value': 35100000.0000000000000000
                  }, {
                      'name': '佛山市顺德区北滘金骏尚塑料五金厂',
                      'value': 30400000.0000000000000000
                  }, {
                      'name': '广安冰鑫电器技术咨询有限公司',
                      'value': 19500000.0000000000000000
                  }, {
                      'name': '广东威特真空电子制造有限公司',
                      'value': 19000000.0000000000000000
                  }, {
                      'name': '浙江豪大情',
                      'value': 18400000.0000000000000000
                  }, {
                      'name': '广东省佰钧成科技有限公司',
                      'value': 18400000.0000000000000000
                  }, {
                      'name': '广东腰子有限公司',
                      'value': 18400000.0000000000000000
                  }, {
                      'name': '青岛晨展五金索具有限公司',
                      'value': 12000000.0000000000000000
                  }, {
                      'name': '广东星辉精密',
                      'value': 12000000.0000000000000000
                  }],
                  'xAxisData': []
              },
              'sum': 138
          }
      } else {
        setData = data
      }

      this.originData = setData
      const {
        activeNum,
        addNum,
        outNum,
        sum,
        cooperation,
        purchase,
        chinaMap,
        purchaseRank,
        level,
        category
      } = setData
      this.blockCountData = {
        activeNum,
        addNum,
        outNum,
        sum
      }
      this.cooperation = {
        ...cooperation,
        color: ['#0B3ECD', '#187CFA', '#358AF4', '#66AEF4'],
        seriesOpts: {
          radius: [0, 70],
          center: ['50%', '40%'],
          roseType: ''
        }
      }
      this.purchase = {
        ...purchase,
        type: 'purchase',
        color: ['#0B3ECD', '#187CFA', '#F39E67', '#F1D16E'],
        seriesOpts: {
          radius: [0, 70],
          center: ['50%', '40%'],
          roseType: ''
        }
      }
      this.chinaMap.seriesData = chinaMap.seriesData
      this.purchaseRank.xAsixData = []
      this.purchaseRank.yAsixData = []
      purchaseRank.seriesData.forEach(i => {
        this.purchaseRank.xAsixData.push(i.value / 10000)
        this.purchaseRank.yAsixData.push(i.name)
      })
      this.level = {
        ...level,
        color: ['#0B3ECD', '#187CFA', '#358AF4', '#66AEF4'],
        seriesOpts: {
          radius: [40, 70],
          center: ['50%', '40%'],
          roseType: ''
        }
      }
      this.category = {
        ...category,
        type: 'category',
        color: ['#0B3ECD', '#187CFA', '#F39E67', '#F1D16E'],
        seriesOpts: {
          radius: [40, 70],
          center: ['50%', '40%'],
          roseType: ''
        }
      }
    },
    async getPurchaseAnalysisCategory (fullPathId, organizationName) {
      const params = { ...this.queryParams, fullPathId }
      if (Object.values(this.queryParams).some(i => !i && i !== 0)) {
        return
      }
      const {
        data
      } = await costReductionApi.getPurchaseAnalysisCategory(
        params
      )
      this.purchaseAnalysisCategory = data.reduce(
        (last, item) => {
          const { categoryName, rate } = item
          last.legend.push(categoryName)
          last.series.push({ value: rate, name: categoryName })
          return last
        },
        {
          legend: [],
          name: organizationName,
          series: []
        }
      )
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
    height: 59px;
    padding: 7px 10px;
    background: url("../../../../../assets/images/bg@x3.png") no-repeat center;
    background-size: cover;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    align-items: center;
  }
  .block-count-active {
    background: url("../../../../../assets/images/bg_active@x3.png") no-repeat
      center;
    background-size: cover;
  }
  .block-count__title {
    height: 16px;
    font-size: 12px;
    color: #ffffff;
    line-height: 16px;
    word-break: keep-all;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    width: 100%;
  }
  .block-count__value {
    height: 24px;
    font-size: 18px;
    font-weight: bold;
    color: #ffffff;
    line-height: 24px;
    display: flex;
    align-items: center;
    justify-content: center;
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
}
</style>
