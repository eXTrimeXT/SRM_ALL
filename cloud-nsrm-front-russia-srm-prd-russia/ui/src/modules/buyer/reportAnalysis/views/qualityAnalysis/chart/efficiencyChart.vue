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
          data1: [],
          data2: [],
          data3: []
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
      default: '208px'
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
  activated () {
    if (this.chart) {
      this.chart.resize()
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
        color: ['#4C88FF', '#3BC2EF', '#F39E67'],
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            // 坐标轴指示器，坐标轴触发有效
            type: 'shadow' // 默认为直线，可选为：'line' | 'shadow'
          }
        },
        grid: {
          top: '20%',
          left: '16px',
          right: '16px',
          bottom: '5%',
          containLabel: true
        },
        legend: {
            data: ['异常问题', '已处理异常问题'],
            icon: 'circle',
            top: '10px',
            textStyle: {
              color: '#242526'
            }
        },
        xAxis: [
          {
            type: 'category',
            data: this.chartData.xAsixData,
            axisLabel: {
              color: '#242526'
            },
            axisTick: {
              show: false
            },
            axisLine: {
              lineStyle: {
                color: '#E3E9F1'
              }
            }
          }
        ],
        yAxis: [
          {
            type: 'value',
            splitLine: {
              show: false
            },
            axisLabel: {
              color: '#242526'
            },
            axisTick: {
              show: false
            },
            axisLine: {
              lineStyle: {
                color: '#E3E9F1'
              }
            }
          },
          {
            type: 'value',
            show: false
          }
        ],
        series: [
          {
            name: '异常问题',
            type: 'bar',
            barGap: '20%',
            barMaxWidth: 20,
            data: this.chartData.data1
          },
          {
            name: '已处理异常问题',
            type: 'bar',
            barGap: '20%',
            barMaxWidth: 20,
            data: this.chartData.data2
          },
          {
              name: '异常处理效率',
              data: this.chartData.data3,
              type: 'line',
              yAxisIndex: 1,
              symbol: 'none',
              lineStyle: {
                color: '#F39E67'
              }
          }
        ]
      })
    }
  }
}
</script>
