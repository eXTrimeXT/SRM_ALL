<template>
  <el-container
    class="purchase-analysis-container"
    direction="vertical"
  >
    <el-main>
      <el-row class="mb-16">
        <el-col :span="12">
          <span class="translation-title">{{
            $t("dataConfMod.purchaseAnalysis")
          }}</span>
        </el-col>
        <el-col :span="12">
          <el-row :gutter="16">
            <!-- 事业部 -->
            <el-col :span="6">
              <DictSelect
                v-model="queryParams.organizationTypeCode"
                code="ORG_TYPE"
                custom-select-type="ORG_TYPE"
              />
            </el-col>
            <!-- 品类 -->
            <el-col :span="6">
              <DictSelect
                v-model="queryParams.level"
                code="CATEGORY"
                custom-select-type="CATEGORY"
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
      <el-row
        :gutter="32"
        class="mb-16"
      >
        <el-col :span="8">
          <div class="block-count">
            <div class="block-count__title">
              {{ $t("dataConfMod.orderQuantity") }}
            </div>
            <div class="block-count__value">
              {{ blockCountData.orderDetailLineNum }}
            </div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="block-count">
            <div class="block-count__title">
              {{ $t("dataConfMod.orderAmount1") }}
            </div>
            <div class="block-count__value">
              {{ blockCountData.purchaseAmount | toThousand | toFixed2 }}
            </div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="block-count">
            <div class="block-count__title">
              {{ $t("dataConfMod.deliveryAmount") }}
            </div>
            <div class="block-count__value">
              {{ blockCountData.warehousingAmount | toThousand | toFixed2 }}
            </div>
          </div>
        </el-col>
      </el-row>
      <el-row
        :gutter="32"
        class="mb-16"
      >
        <el-col :span="8">
          <el-row class="chart-box mb-16">
            <div class="sub-title__wapper">
              <div class="sub-title">
                <div>{{ $t("dataConfMod.purchaseAnalysisTitle[0]") }}</div>
                <div>
                  <el-tooltip
                    class="item"
                    effect="dark"
                    placement="top"
                  >
                    <div
                      slot="content"
                      class="tooltip-content"
                      v-html="$t('dataConfMod.purchaseAnalysisTitle[1]')"
                    />
                    <i class="el-icon-warning info-icon" />
                  </el-tooltip>
                </div>
              </div>
              <div
                class="more"
                @click="showMoreInfo(1)"
              >
                {{ $t("common.more") }}&gt;
              </div>
            </div>
            <VerticalBarChart
              type="executionList"
              :chart-data="executionList"
              :comActive="changeTab"
              @barClick="barClick"
            />
          </el-row>
          <el-row class="chart-box">
            <div class="sub-title__wapper">
              <div class="sub-title">
                <div>{{ $t("dataConfMod.purchaseAnalysisTitle[2]") }}</div>
                <div>
                  <el-tooltip
                    class="item"
                    effect="dark"
                    placement="top"
                  >
                    <div
                      slot="content"
                      class="tooltip-content"
                    >
                      {{ $t("dataConfMod.purchaseAnalysisTitle[3]") }}
                    </div>
                    <i class="el-icon-warning info-icon" />
                  </el-tooltip>
                </div>
              </div>
              <div
                class="more"
                @click="showMoreInfo(3)"
              >
                {{ $t("common.more") }}&gt;
              </div>
            </div>
            <VerticalBarChart
              type="receiveList"
              :chart-data="receiveList"
              :comActive="changeTab"
              @barClick="barClick"
            />
          </el-row>
        </el-col>
        <el-col :span="16">
          <el-row class="chart-box">
            <div class="sub-title__wapper">
              <div class="sub-title">
                {{ $t("dataConfMod.purchaseAnalysisTitle[4]") }}
              </div>
              <div
                class="more"
                @click="showMoreInfo(5)"
              >
                {{ $t("common.more") }}&gt;
              </div>
            </div>
            <PieChart :chart-data="purchaseAnalysisCategory" />
          </el-row>
        </el-col>
      </el-row>
      <el-row :gutter="32">
        <el-col :span="8">
          <el-row class="chart-box mb-16">
            <div class="sub-title__wapper">
              <div class="sub-title">
                <div>{{ $t("dataConfMod.purchaseAnalysisTitle[5]") }}</div>
                <div>
                  <el-tooltip
                    class="item"
                    effect="dark"
                    placement="top"
                  >
                    <div
                      slot="content"
                      class="tooltip-content"
                      v-html="$t('dataConfMod.purchaseAnalysisTitle[6]')"
                    />
                    <i class="el-icon-warning info-icon" />
                  </el-tooltip>
                </div>
              </div>
              <div
                class="more"
                @click="showMoreInfo(2)"
              >
                {{ $t("common.more") }}&gt;
              </div>
            </div>
            <VerticalBarChart
              type="punctualityList"
              :chart-data="punctualityList"
              :comActive="changeTab"
              @barClick="barClick"
            />
          </el-row>
        </el-col>
        <el-col :span="16">
          <el-row class="chart-box mb-16">
            <div class="sub-title__wapper mb-0">
              <div class="sub-title">
                <div>{{ $t("dataConfMod.purchaseAnalysisTitle[7]") }}</div>
                <div>
                  <el-tooltip
                    class="item"
                    effect="dark"
                    placement="top"
                  >
                    <div
                      slot="content"
                      class="tooltip-content"
                      v-html="$t('dataConfMod.purchaseAnalysisTitle[8]')"
                    />
                    <i class="el-icon-warning info-icon" />
                  </el-tooltip>
                </div>
              </div>
              <div
                class="more"
                @click="showMoreInfo(4)"
              >
                {{ $t("common.more") }}&gt;
              </div>
            </div>
            <HorizontalBarChart :chart-data="months" :comActive="changeTab" />
          </el-row>
        </el-col>
      </el-row>
    </el-main>
  </el-container>
</template>
<script>
import { isNull, toThousand, toNumber } from '@/utils'
import PieChart from './chart/PieChart'
import HorizontalBarChart from './chart/HorizontalBarChart'
import VerticalBarChart from './chart/VerticalBarChart'
import moreInfo from './moreInfo.vue'
import i18n from '@/lang'
import { createDictClass } from '@/library/utils/dict/dict-utils'
import { purchaseAnalysisApi } from 'modb@/orderManagementBuyer/api'
const dictClass = createDictClass({
  'SEASON': [{ id: 1, value: 1, label: i18n.t('time.quarterList[0]') },
    { id: 2, value: 2, label: i18n.t('time.quarterList[1]') },
    { id: 3, value: 3, label: i18n.t('time.quarterList[2]') },
    { id: 4, value: 4, label: i18n.t('time.quarterList[3]') },
    { id: 5, value: 0, label: i18n.t('reportMod.allYear') }]
}, false)
export default {
  name: 'PurchaseAnalysis',
  components: { VerticalBarChart, PieChart, HorizontalBarChart },
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
        season: null
      },
      originData: null,
      blockCountData: {
        orderDetailLineNum: 0,
        purchaseAmount: 0,
        warehousingAmount: 0
      },
      // 采购执行情况
      executionList: {
        xAsixData: [],
        yAsixData: []
      },
      // 采购接收情况
      receiveList: {
        xAsixData: [],
        yAsixData: []
      },
      // 采购准时率
      punctualityList: {
        xAsixData: [],
        yAsixData: []
      },
      // 收货达成率
      months: {
        xAsixData: [],
        yAsixData: [[], [], []]
      },
      // 采购品类交付占比分析
      purchaseAnalysisCategory: {
        legend: [],
        name: '',
        series: []
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
        this.getPurchaseAnalysisCategory('', this.$t('reportMod.analysisPurchasedItems'))
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
    this.queryParams.level = '1'
    this.queryParams.year = '2022'
    // this.queryParams.season = this.getCurrentQuarter();
  },
  methods: {
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
        tab.title = this.$t('dataConfMod.receiptAchievementRate')
        break
      case 2:
        tab.title = this.$t('dataConfMod.purchaseOnTimeRate')
        break
      case 3:
        tab.title = this.$t('dataConfMod.orderConfirm')
        break
      case 4:
        tab.title = this.$t('dataConfMod.purchaseOnTimeRate')
        break
      case 5:
        tab.title = this.$t('dataConfMod.catePurAmountProportion')
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
      if (Object.values(params).some(i => !i && i !== 0)) {
        return
      }
      const { data } = await purchaseAnalysisApi.getPurchaseAnalysis(
        params
      )
      // origin-change-start -liwenhong
      // this.originData = data;
      // const {
      //   orderDetailLineNum,
      //   purchaseAmount,
      //   warehousingAmount,
      //   executionList,
      //   receiveList,
      //   punctualityList,
      //   months
      // } = data;
      // origin-change-end -liwenhong

      // simulation-start -liwenhong
      let setOriginData = {}
      if (window.location.host === 'sccdemosyy.meicloud.com' || window.location.host === 'localhost:8082') {
        setOriginData = {
          'executionList': [{
            'fullPathId': '06c4a1a8453d2605f425918f56418e48',
            'organizationName': this.$t('reportMod.meiyunPurchasing'),
            'rate': 100.0000
          }, {
            'fullPathId': '9ddd92ee46833f2f50799a326e310fb9',
            'organizationName': this.$t('reportMod.businessDivision'),
            'rate': 100.0000
          }, {
            'fullPathId': '8bc88e09bab107eeac5f136182a710f9',
            'organizationName': this.$t('reportMod.homeAppliances'),
            'rate': 100.0000
          }, {
            'fullPathId': 'd1f47d9f124ec15b07770a76eee1112e',
            'organizationName': this.$t('reportMod.HVAC'),
            'rate': 100.0000
          }, {
            'fullPathId': '752d787083630ec4fac99a963bb3b4e6',
            'organizationName': this.$t('reportMod.newEnergy'),
            'rate': 100.0000
          }],
          'months': [{
            'deliveryAmount': 0,
            'month': this.$t('dataConfMod.timeIntervalList.7'),  // '1月'
            'orderAmount': 0,
            'rate': 0
          }, {
            'deliveryAmount': 35010000.0000000000000000,
            'month': this.$t('reportMod.February'),  // '2月'
            'orderAmount': 35010000.0000000000000000,
            'rate': 100.0000
          }, {
            'deliveryAmount': 8600000.0000000000000000,
            'month': this.$t('reportMod.March'),  // '3月'
            'orderAmount': 10600000.0000000000000000,
            'rate': 81.1300
          }, {
            'deliveryAmount': 171490000.0000000000000000,
            'month': this.$t('reportMod.April'),  // '4月'
            'orderAmount': 173840000.0000000000000000,
            'rate': 98.6500
          }, {
            'deliveryAmount': 5170000.0000000000000000,
            'month': this.$t('reportMod.May'),  // '5月'
            'orderAmount': 5460000.0000000000000000,
            'rate': 94.6900
          }, {
            'deliveryAmount': 104700000.0000000000000000,
            'month': this.$t('reportMod.June'),  // '6月'
            'orderAmount': 104700000.0000000000000000,
            'rate': 100.0000
          }, {
            'deliveryAmount': 0,
            'month': this.$t('reportMod.July'),  // '7月'
            'orderAmount': 0,
            'rate': 0
          }, {
            'deliveryAmount': 37003000.0000000000000000,
            'month': this.$t('reportMod.August'),  // '8月'
            'orderAmount': 37003000.0000000000000000,
            'rate': 100.0000
          }, {
            'deliveryAmount': 8176000.0000000000000000,
            'month': this.$t('reportMod.September'),  // '9月'
            'orderAmount': 8176000.0000000000000000,
            'rate': 100.0000
          }, {
            'deliveryAmount': 5853000.0000000000000000,
            'month': this.$t('reportMod.October'),  // '10月'
            'orderAmount': 5853000.0000000000000000,
            'rate': 100.0000
          }, {
            'deliveryAmount': 10930000.0000000000000000,
            'month': this.$t('reportMod.November'),  // '11月'
            'orderAmount': 12700000.0000000000000000,
            'rate': 86.0600
          }, {
            'deliveryAmount': 11702000.0000000000000000,
            'month': this.$t('reportMod.December'),  // '12月'
            'orderAmount': 11702000.0000000000000000,
            'rate': 100.0000
          }],
          'orderDetailLineNum': 121,
          'punctualityList': [{
            'fullPathId': '06c4a1a8453d2605f425918f56418e48',
            'organizationName': this.$t('reportMod.meiyunPurchasing'),
            'rate': 100.0000
          }, {
            'fullPathId': '9ddd92ee46833f2f50799a326e310fb9',
            'organizationName': this.$t('reportMod.businessDivision'),
            'rate': 100.0000
          }, {
            'fullPathId': '8bc88e09bab107eeac5f136182a710f9',
            'organizationName': this.$t('reportMod.homeAppliances'),
            'rate': 100.0000
          }, {
            'fullPathId': 'd1f47d9f124ec15b07770a76eee1112e',
            'organizationName': this.$t('reportMod.HVAC'),
            'rate': 100.0000
          }, {
            'fullPathId': '752d787083630ec4fac99a963bb3b4e6',
            'organizationName': this.$t('reportMod.newEnergy'),
            'rate': 100.0000
          }],
          'purchaseAmount': 405044000.0000000000000000,
          'receiveList': [{
            'fullPathId': '06c4a1a8453d2605f425918f56418e48',
            'organizationName': this.$t('reportMod.meiyunPurchasing'),
            'rate': 100.0000
          }, {
            'fullPathId': '9ddd92ee46833f2f50799a326e310fb9',
            'organizationName': this.$t('reportMod.businessDivision'),
            'rate': 100.0000
          }, {
            'fullPathId': '8bc88e09bab107eeac5f136182a710f9',
            'organizationName': this.$t('reportMod.homeAppliances'),
            'rate': 100.0000
          }, {
            'fullPathId': 'd1f47d9f124ec15b07770a76eee1112e',
            'organizationName': this.$t('reportMod.HVAC'),
            'rate': 100.0000
          }, {
            'fullPathId': '6f2d5b1cc19888f3e5bd32856c5acfe7',
            'organizationName': this.$t('reportMod.newEnergy'),
            'rate': 100.0000
          }],
          'warehousingAmount': 398634000.0000000000000000
        }
      } else {
        setOriginData = data
      }

      this.originData = setOriginData
      const {
        orderDetailLineNum,
        purchaseAmount,
        warehousingAmount,
        executionList,
        receiveList,
        punctualityList,
        months
      } = setOriginData
      // simulation-end -liwenhong

      this.blockCountData = {
        orderDetailLineNum,
        purchaseAmount,
        warehousingAmount
      }
      this.executionList = executionList.reduce(
        (last, item) => {
          const { organizationName, rate } = item
          last.xAsixData.push(rate)
          last.yAsixData.push(organizationName)
          return last
        },
        { xAsixData: [], yAsixData: [] }
      )
      this.punctualityList = punctualityList.reduce(
        (last, item) => {
          const { organizationName, rate } = item
          last.xAsixData.push(rate)
          last.yAsixData.push(organizationName)
          return last
        },
        { xAsixData: [], yAsixData: [] }
      )
      this.receiveList = receiveList.reduce(
        (last, item) => {
          const { organizationName, rate } = item
          last.xAsixData.push(rate)
          last.yAsixData.push(organizationName)
          return last
        },
        { xAsixData: [], yAsixData: [] }
      )
      this.months = months.reduce(
        (last, item) => {
          const { deliveryAmount, month, orderAmount, rate } = item
          last.xAsixData.push(month)
          last.yAsixData[0].push(orderAmount)
          last.yAsixData[1].push(deliveryAmount)
          last.yAsixData[2].push(rate)
          return last
        },
        { xAsixData: [], yAsixData: [[], [], []] }
      )
      console.log('this.executionList', this.executionList)
      console.log('this.receiveList', this.receiveList)
      console.log('this.punctualityList', this.punctualityList)
      console.log('this.months', this.months)
    },
    async getPurchaseAnalysisCategory (fullPathId, organizationName) {
      const params = { ...this.queryParams, fullPathId }
      if (Object.values(this.queryParams).some(i => !i && i !== 0)) {
        return
      }
      const {
        data
      } = await purchaseAnalysisApi.getPurchaseAnalysisCategory(
        params
      )
      // simulation-start -liwenhong
      let setData = []
      if (window.location.host === 'sccdemosyy.meicloud.com' || window.location.host === 'localhost:8082') {
        setData = [{
          'categoryId': 8123879483637952,
          'categoryName': this.$t('reportMod.graphiteProducts'),
          'num': null,
          'purchaseAmount': 398634000.0000000000000000,
          'rate': 37.4700,
          'warehousingAmount': 149373000.0000000000000000
        }, {
          'categoryId': 8123879500611776,
          'categoryName': this.$t('reportMod.fabricatedMetalProduct'),
          'num': null,
          'purchaseAmount': 398634000.0000000000000000,
          'rate': 20.7300,
          'warehousingAmount': 82650000.0000000000000000
        }, {
          'categoryId': 8123879486849216,
          'categoryName': this.$t('reportMod.carbonCarbonComposite '),
          'num': null,
          'purchaseAmount': 398634000.0000000000000000,
          'rate': 18.7300,
          'warehousingAmount': 74674000.0000000000000000
        }, {
          'categoryId': 8123879473217728,
          'categoryName': this.$t('reportMod.felts'),
          'num': null,
          'purchaseAmount': 398634000.0000000000000000,
          'rate': 11.1900,
          'warehousingAmount': 44627000.0000000000000000
        }, {
          'categoryId': 8123879499301056,
          'categoryName': this.$t('reportMod.formulatedChemical'),
          'num': null,
          'purchaseAmount': 398634000.0000000000000000,
          'rate': 6.2100,
          'warehousingAmount': 24770000.0000000000000000
        }, {
          'categoryId': null,
          'categoryName': this.$t('dataConfMod.settingGuide.step6[0]'),
          'num': null,
          'purchaseAmount': null,
          'rate': 5.6700,
          'warehousingAmount': null
        }]
      } else {
        setData = data
      }

      // simulation-end -liwenhong
      this.purchaseAnalysisCategory = setData.reduce(
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
  // padding: 16px;
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
    height: 81px;
    padding: 10px 15px;
    background: url("../../../../../assets/images/bg@x31.png") no-repeat center;
    background-size: cover;
  }
  .block-count__title {
    font-size: 14px;
    color: #ffffff;
    line-height: 18px;
    word-break: keep-all;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
  .block-count__value {
    font-size: 24px;
    font-weight: bold;
    color: #ffffff;
    line-height: 31px;
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
}
</style>
