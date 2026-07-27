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

export default {
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
      default: '350px'
    },
    autoResize: {
      type: Boolean,
      default: true
    },
    chartData: {
      type: Object,
      required: true
    }
  },
  data () {
    return {
      chart: null
    }
  },
  watch: {
    chartData: {
      deep: true,
      handler (val) {
        this.setOptions(val)
      }
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
      this.setOptions(this.chartData)
    },
    setOptions ({
      xAxisData = [],
      seriesData = [],
      legend = '',
      vendorInfos = []
    } = {}) {
      this.chart.setOption({
        xAxis: {
          data: xAxisData,
          boundaryGap: false,
          axisTick: {
            show: false
          }
        },
        title: {
          show: true,
          text: legend,
          padding: [5, 10],
          left: 'center'
        },
        grid: {
          left: 20,
          right: 35,
          bottom: 20,
          top: 30,
          containLabel: true
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross'
          },
          formatter: params => {
            const label = `含税单价: ${params[0].value}<br />${
              vendorInfos[params[0].dataIndex]
            }`
            return label
          },
          padding: [5, 10]
        },
        yAxis: {
          axisTick: {
            show: false
          }
        },
        // legend: {
        //   data: [legend]
        // },
        series: [
          {
            name: legend,
            itemStyle: {
              normal: {
                color: '#FF005A',
                lineStyle: {
                  color: '#FF005A',
                  width: 2
                }
              }
            },
            smooth: true,
            type: 'line',
            data: seriesData,
            animationDuration: 2800,
            animationEasing: 'cubicInOut'
          }
          // {
          //   name: 'actual',
          //   smooth: true,
          //   type: 'line',
          //   itemStyle: {
          //     normal: {
          //       color: '#3888fa',
          //       lineStyle: {
          //         color: '#3888fa',
          //         width: 2
          //       },
          //       areaStyle: {
          //         color: '#f3f8ff'
          //       }
          //     }
          //   },
          //   data: actualData,
          //   animationDuration: 2800,
          //   animationEasing: 'quadraticOut'
          // }
        ]
      })
    }
  }
}
</script>
