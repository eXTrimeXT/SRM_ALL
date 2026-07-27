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
          data: []
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
      default: '141px'
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
        color: ['#4C88FF', '#3BC2EF', '#F39E67'],
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            // 坐标轴指示器，坐标轴触发有效
            type: 'shadow' // 默认为直线，可选为：'line' | 'shadow'
          }
        },
        grid: {
          top: '0%',
          left: '0px',
          right: '8px',
          bottom: '0%',
          containLabel: true
        },
        xAxis: [
          {
            type: 'value',
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
            type: 'category',
            data: this.chartData.xAsixData,
            splitLine: {
              show: false
            },
            axisLabel: {
              color: '#242526',
              formatter: function (params) {
                let val = ''
                if (params.length > 4) {
                  val = params.substr(0, 3) + '...'
                  return val
                } else {
                  return params
                }
              }
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
        series: [
          {
            name: '',
            type: 'bar',
            // barGap:'20%',
            barMaxWidth: '35%',
            itemStyle: {
            normal: {
                barBorderRadius: [0, 12, 12, 0]
            }
          },
            data: this.chartData.data
          }
        ]
      })
    }
  }
}
</script>
