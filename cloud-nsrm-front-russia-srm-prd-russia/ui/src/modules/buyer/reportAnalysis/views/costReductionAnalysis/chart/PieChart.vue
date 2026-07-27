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
      default: '300px'
    },
    chartData: {
      type: Object,
      default: () => {
        return {
          legend: [],
          seriesData: [],
          seriesOpts: {
            radius: [0, 70],
            center: ['50%', '30%'],
            roseType: ''
          }
        }
      }
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
          trigger: 'item',
          formatter: '{d}%'
        },
        color: this.chartData.color,
        // grid: {
        //   top: 0,
        //   left: 0,
        //   right: "10%",
        //   bottom: 0,
        //   containLabel: true
        // },
        legend: {
          orient: 'vertical',
          icon: 'rect',
          left: 'center',
          bottom: 0,
          data: this.chartData.legend,
          // 使用回调函数
          formatter: name => {
            var data = this.chartData.seriesData
            var total = 0
            var tarValue
            for (var i = 0, l = data.length; i < l; i++) {
              total += data[i].value
              if (data[i].name == name) {
                tarValue = data[i].value
              }
            }
            var p = ((tarValue / total) * 100).toFixed(2)
            return (
              name + '：' + this.$t('reportMod.categoryProportion') + p + '%'
            ) // 品类占比
          }
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
