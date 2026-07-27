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
      default: '365px'
    },
    chartData: {
      type: Object,
      default: () => {
        return {
          name: '',
          legend: [],
          series: []
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
          formatter: '{a} <br/>{b} : {c} ({d}%)'
        },
        title: {
          bottom: 0,
          left: 'center',
          textStyle: {
            color: '#242526',
            fontSize: '14px'
          },
          text: this.chartData.name
        },
        // legend: {
        //   left: "center",
        //   bottom: "10",
        //   data: this.chartData.legend
        // },
        color: [
          '#187CFA',
          '#358AF4',
          '#66AEF4',
          '#97D964',
          '#F1D16E',
          '#F39E67'
        ],
        series: [
          {
            name: this.chartData.name,
            type: 'pie',
            label: {
              normal: {
                formatter: '{b} : {c} %',
                color: '#242526',
                fontSize: '12px'
              }
            },
            // roseType: "radius",
            // radius: [15, 95],
            // center: ["50%", "38%"],
            data: this.chartData.series,
            animationEasing: 'cubicInOut',
            animationDuration: 2600
          }
        ]
      })
    }
  }
}
</script>
