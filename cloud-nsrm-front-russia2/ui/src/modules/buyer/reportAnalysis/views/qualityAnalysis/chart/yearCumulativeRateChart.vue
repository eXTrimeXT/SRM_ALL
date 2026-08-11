<template>
  <div
    :class="className"
    :style="{ height: height, width: width }"
  />
</template>

<script>
import echarts from 'echarts' // echarts theme
import resize from './mixins/resize'
import 'echarts/theme/macarons'

const animationDuration = 6000

export default {
  mixins: [resize],
  props: {
    chartData: {
      type: Object,
      default: () => {
        return {
          xAsixData: [],
          yAsixData: [[], [], []]
        }
      }
    },
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
      default: '156px'
    },
    // 组件激活
    comActive: {
      type: [String, Number],
      default: 0
    }
  },
  data () {
    return {
      chart: null
    }
  },
  watch: {
    chartData: {
      handler () {
        this.initChart()
        this.$nextTick(() => this.chart.resize())
      },
      deep: true
    },
    comActive: {
      handler: function (n, o) {
        if (n) {
          this.$nextTick(() => this.chart.resize())
        }
      },
      deep: true
    }
  },
  mounted () {
    this.$nextTick(() => {
      this.initChart()
    })
  },
  beforeDestroy () {
    if (!this.chart) {
      return
    }
    this.chart.dispose()
    this.chart = null
  },
  methods: {
    initChart () {
      this.chart = echarts.init(this.$el, 'macarons')

      this.chart.setOption({
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            // 坐标轴指示器，坐标轴触发有效
            type: 'shadow' // 默认为直线，可选为：'line' | 'shadow'
          }
        },
        color: ['#4C88FF', '#F39E67', '#F39E67'],
        grid: {
          top: '15%',
          left: 0,
          right: 0,
          bottom: 0,
          containLabel: true
        },
        xAxis: [
          {
            type: 'category',
            splitLine: {
              show: false
            },
            data: this.chartData.xAsixData,
            axisLabel: {
              color: '#242526',
              fontSize: 13,
              fontFamily: 'MicrosoftYaHei',
              lineHeight: 22
            },
            splitArea: {
              show: false
            },
            axisTick: {
              alignWithLabel: true
            }
          }
        ],
        yAxis: [
          {
            type: 'value',
            splitLine: {
              show: false
            },
            splitArea: {
              show: false
            },
            // name: "数量",
            axisLabel: {
              color: '#242526',
              fontSize: 13,
              fontFamily: 'MicrosoftYaHei',
              lineHeight: 22
            },
            axisTick: {
              show: false
            }
          }
        ],
        legend: {
          // left: "center",
          // top: "-20",
          itemHeight: 10,
          // itemWidth: 10,
          data: [
            {
              name: this.$t('reportMod.actualSumCostReduceRate'), // 实际累计降本率
              icon: 'roundRect'
            },
            {
              name: this.$t('reportMod.annualTargetReductionRate') // 年度目标降本率
            }
          ]
        },
        series: [
          {
            name: this.$t('reportMod.actualSumCostReduceRate'), // 实际累计降本率
            type: 'bar',
            barWidth: 8,
            data: this.chartData.yAsixData[0],
            animationDuration
          },
          {
            // 这两组数据用来模拟markLine线段开关,data可以为空
            name: this.$t('reportMod.annualTargetReductionRate'), // 年度目标降本率
            type: 'line',
            data: this.chartData.crTragetRate,
            animationDuration
            // markLine:{
            //   symbol:'none',//去掉箭头
            //   data: [
            //     {
            //       name:'年度目标降本率',
            //       yAxis: this.chartData.crTragetRate,
            //       lineStyle: {
            //         type: 'solid',
            //         color: '#F39E67'
            //       },
            //     },
            //   ]
            // }
          }
        ]
      })
    }
  }
}
</script>
