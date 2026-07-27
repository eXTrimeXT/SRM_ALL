<template>
  <div
    :class="className"
    :style="{ height: height, width: width, 'margin-left': '8%' }"
  />
</template>

<script>
import echarts from 'echarts' // echarts theme
import 'echarts/theme/macarons'

export default {
  name: 'LineChart',

  props: {
    className: {
      type: String,
      default: 'chart'
    },
    width: {
      type: String,
      default: '80%'
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

    setOptions ({ xAxisData = [], seriesData = [], vendorInfos = [], legend = '' } = {}) {
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
            type: 'shadow'
          },
          formatter: params => this.$t('bidMod.competitionLts.tooltip', { h: params[0].value, m: vendorInfos[params[0].dataIndex] }),
          padding: [5, 10]
        },
        yAxis: {
          axisTick: {
            show: false
          }
        },
        series: [
          {
            name: legend,
            itemStyle: {
              normal: {
                color: '#FF005A',
                lineStyle: {
                  color: '#FF005A',
                  width: 1
                }
              }
            },
            smooth: true,
            type: 'line',
            data: seriesData,
            animationDuration: 2800,
            animationEasing: 'cubicInOut'
          }
        ]
      })
    }
  }
}
</script>
