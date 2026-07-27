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
          data: 80
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
        title: {
          show: true,
          text: '完成率',
          left: 'center',
          top: '30%',
          textStyle: {
            color: '#666666',
            fontSize: 10
          }
        },
        series: [
          {
            type: 'gauge',
            radius: '110%',
            center: ['50%', '55%'],
            axisLine: {
              lineStyle: {
                width: 10,
                color: [
                  [0.4, '#E9982F'],
                  [0.8, '#5CB5F4'],
                  [1, '#4E8AE9']
                ]
              }
            },
            pointer: {
              itemStyle: {
                color: 'auto'
              }
            },
            axisTick: {
              show: false,
              distance: -30,
              length: 8,
              lineStyle: {
                color: '#fff',
                width: 2
              }
            },
            splitLine: {
              // distance: -30,
              length: 30,
              lineStyle: {
                color: '#fff',
                width: 4
              }
            },
            axisLabel: {
              color: '#666666',
              distance: -15,
              fontSize: 9
            },
            anchor: {
              show: true,
              showAbove: true,
              size: 25,
              icon: 'circle',
              itemStyle: {
                borderWidth: 10
              }
            },
            detail: {
              valueAnimation: true,
              formatter: '{value}',
              fontSize: 21,
              padding: [40, 0, 0, 0],
              color: '#666666'
            },
            data: [
              {
                value: this.chartData.data
              }
            ]
          }
        ]
      })
    }
  }
}
</script>
