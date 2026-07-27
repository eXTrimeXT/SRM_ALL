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
          xAxisData: [],
          yAsixData: []
        }
      }
    },
    type: {
      type: String,
      default: ''
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
      default: '146px'
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
        this.setOptions()
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
      console.log(this.chart)
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
      console.log(this.$el.children)
      this.chart = echarts.init(this.$el, 'macarons')
      this.chart.on('click', params => {
        this.$emit('barClick', params, this.type)
        console.log('[vertical bar chart click]', params)
      })
      this.setOptions()
    },
    setOptions () {
      const { yAsixData = [], xAsixData = [] } = this.chartData
      const xData = []
      xAsixData.forEach(i => xData.unshift(i))
      const yData = []
      yAsixData.forEach(i => yData.unshift(i))
      this.chart.setOption({
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            // 坐标轴指示器，坐标轴触发有效
            type: 'shadow' // 默认为直线，可选为：'line' | 'shadow'
          }
        },
        color: ['#4C88FF'],
        grid: {
          top: 0,
          left: 0,
          right: '10%',
          bottom: 0,
          containLabel: true
        },
        yAxis: [
          {
            type: 'category',
            data: yData,
            axisLabel: {
              color: '#242526',
              fontSize: 14,
              fontFamily: 'MicrosoftYaHei',
              // lineHeight: 24,
              formatter: function (value) {
                if (value.length > 3) {
                  return value.substring(0, 3) + '...'
                } else {
                  return value
                }
              }
            },
            axisTick: {
              alignWithLabel: true
            }
          }
        ],
        xAxis: [
          {
            name: this.$t('dataConfMod.tenThousand'), // 万元
            nameTextStyle: {
              // 关键代码
              padding: [38, 0, 0, 8]
            },
            axisLabel: {
              color: '#242526',
              fontSize: 14,
              fontFamily: 'MicrosoftYaHei',
              lineHeight: 22
            },
            type: 'value',
            // min: 0,
            // max: 100,
            axisTick: {
              show: false
            }
          }
        ],
        series: [
          {
            type: 'bar',
            barWidth: 8,
            itemStyle: {
              normal: {
                color: params => {
                  if (params.value < 60) {
                    return '#F33A00'
                  }
                  return '#4C88FF'
                },
                barBorderRadius: [0, 6, 6, 0]
              },
              emphasis: {
                barBorderRadius: [0, 6, 6, 0]
              }
            },
            data: xData,
            animationDuration
          }
        ]
      })
    }
  }
}
</script>
