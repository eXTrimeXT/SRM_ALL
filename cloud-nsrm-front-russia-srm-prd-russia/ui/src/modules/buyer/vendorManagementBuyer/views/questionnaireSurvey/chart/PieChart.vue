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
      default: '250px'
    },
    chartData: {
      type: Object,
      default: () => {
        return {
          legend: [],
          seriesData: [],
          seriesOpts: {
            radius: [0, 70],
            center: ['50%', '40%'],
            roseType: ''
          }
        }
      }
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
        this.chart.resize()
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
          trigger: 'item',
          formatter: '{d}%'
        },
        color: this.chartData.color,
        legend: {
          // orient: "vertical",
          icon: 'circle',
          left: 'center',
          bottom: 0,
          data: this.chartData.legend
        },
        series: [
          {
            type: 'pie',
            label: {
              normal: {
                formatter: '{d}%',
                color: '#242526',
                fontSize: '12px'
              }
            },
            ...this.chartData.seriesOpts,
            data: this.chartData.seriesData,
            animationEasing: 'cubicInOut',
            animationDuration: 2600
          }
        ]
      })
    }
  }
}
</script>
