<template>
  <div class="price-line-chart-wrap">
    <!--<FormWrapper :form-array="searchFormConfig" @getFormData="getQueryData" />-->

    <!--提供tips插槽-->
    <slot name="tips">
      <p class="chart-tips">
        {{ chartTips }}
      </p>
    </slot>

    <div
      ref="priceLineChart"
      :class="className"
      :style="{ height: height, width: width }"
    />
  </div>
</template>

<script>
import { colorList } from './utils'
import echarts from 'echarts'
import resize from './resize'
// import FormWrapper from 'lib@/components/Table/FormWrapper'
import 'echarts/theme/macarons'

export default {
  name: 'VendorQoutePriceLineChart',

  // components: { FormWrapper },

  mixins: [resize],

  props: {
    className: {
      type: String,
      default: 'chart'
    },
    width: {
      type: String,
      default: '100%'
    },
    height: {
      type: String,
      default: '370px'
    },
    // 供应商历史价格信息
    vendorPriceNodes: {
      type: Object,
      required: true
    },
    // 物料名称
    itemDesc: {
      type: String,
      required: true
    },
    // 查询表单配置
    formConfig: {
      type: [Object, Array],
      default: () => null
    },
    // 是否显示查询条件
    showSearchForm: {
      type: Boolean,
      default: true
    },
    chartTips: {
      type: String,
      default: '* 通过点击不同物料行，切换比价图表'
    }
  },

  data () {
    return {
      chart: null
    }
  },

  computed: {
    searchFormConfig () {
      if (this.formConfig && Array.isArray(this.formConfig)) {
        return this.formConfig
      }
      // 默认查询条件配置
      return [
        {
          prop: 'dateList',
          label: '历史价格范围',
          type: 'daterange'
        }
      ]
    }
  },

  watch: {
    vendorPriceNodes: {
      handler (val) {
        if (val) {
          this.setChartData()
        }
      },
      deep: true
    }
  },

  mounted () {
    this.$nextTick(() => {
      // 以当前标签创建echarts实例
      this.chart = echarts.init(this.$refs.priceLineChart, 'macarons')
      if (this.vendorPriceNodes) {
        this.setChartData()
      }
    })
  },

  beforeDestroy () {
    if (!this.chart) {
      return
    }
    // 销毁实例
    this.chart.dispose()
    this.chart = null
  },

  methods: {
    /* 触发查询 */
    getQueryData (value) {
      this.$emit('query', value)
    },

    /* 编排折线图数据 */
    setChartData () {
      // 所有日期 X轴 升序
      let xAxis = []
      // 折线配置 一个供应商一个折线
      const series = []

      const vendorKeys = Object.keys(this.vendorPriceNodes)
      if (this.vendorPriceNodes && vendorKeys.length > 0) {
        vendorKeys.forEach(item => {
          // 添加一条折线
          series.push({
            name: item,
            data: this.vendorPriceNodes[item]
              .map(itemVendor => {
                xAxis.push(itemVendor.date)
                return [itemVendor.date, itemVendor.notaxPrice]
              })
              // 时间排序
              .sort(this.sortDownVendorDate)
          })
        })
      }
      // 去重，排序
      xAxis = [...new Set(xAxis)].sort(this.sortDownXAxisDate)

      this.setChartOptions({
        xAxisData: xAxis,
        seriesData: series,
        vendorList: vendorKeys
      })
    },

    /* 所有日期排序，升序 */
    sortDownXAxisDate (a, b) {
      return Date.parse(a) - Date.parse(b)
    },

    /* 供应商日期排序，升序 */
    sortDownVendorDate (a, b) {
      return Date.parse(a[0]) - Date.parse(b[0])
    },

    /* 设置折线图参数 */
    setChartOptions (val) {
      const {
        xAxisData = [],
        seriesData = [],
        vendorList = []
      } = val

      // 颜色数量
      const colorListLength = colorList.length
      const seriesList = seriesData.map((item, index) => {
        // 取余轮回
        const colorIndex = index % colorListLength
        return {
          name: item.name,
          itemStyle: {
            normal: {
              color: colorList[colorIndex],
              lineStyle: {
                color: colorList[colorIndex],
                width: 2
              }
            }
          },
          smooth: true,
          type: 'line',
          data: item.data,
          animationDuration: 2800,
          animationEasing: 'cubicInOut'
        }
      })

      this.chart.setOption({
        xAxis: {
          data: xAxisData,
          boundaryGap: false,
          axisTick: {
            show: false
          }
        },
        yAxis: {
          name: '未税单价',
          axisTick: {
            show: false
          }
        },
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: vendorList,
          top: 'bottom'
          // bottom: 0
        },
        title: {
          // 标题 物料名称
          text: this.itemDesc,
          padding: [5, 10],
          left: 'center',
          top: 'top'
        },
        grid: {
          left: 40,
          right: 60,
          bottom: 40,
          top: 30,
          containLabel: true
        },
        series: seriesList
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.chart-tips {
  font-size: 12px;
  color: red;
}
</style>
