<template>
  <div
    :class="className"
    :style="{ height: height, width: width }"
  />
</template>

<script>
import echarts from 'echarts' // echarts theme
import 'echarts/map/js/china.js' // 引入中国地图数据
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
          seriesData: []
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
          trigger: 'item',
          formatter: '{b}: {c}',
          itemSize: '14px'
        },
        dataRange: {
          x: 'left',
          y: 'bottom',
          splitList: [
            {
              start: this.chartData.areaFour,
              label:
                this.$t('quota.supplierNumber') +
                '(X>=' +
                this.chartData.areaFour +
                ')'
            },
            {
              start: this.chartData.areaThreeStart,
              end: this.chartData.areaThreeEnd,
              label:
                this.$t('quota.supplierNumber') +
                '(' +
                this.chartData.areaThreeStart +
                '<=X<' +
                this.chartData.areaThreeEnd +
                ')'
            },
            {
              start: this.chartData.areaTwoStart,
              end: this.chartData.areaTwoEnd,
              label:
                this.$t('quota.supplierNumber') +
                '(' +
                this.chartData.areaTwoStart +
                '<=X<' +
                this.chartData.areaTwoEnd +
                ')'
            },
            {
              end: this.chartData.areaOne,
              label:
                this.$t('quota.supplierNumber') +
                '(X<' +
                this.chartData.areaOne +
                ')'
            }
          ],
          color: ['#187CFA', '#4C88FF', '#67AFF4', '#C5E4FF']
        },
        series: [
          {
            type: 'map',
            mapType: 'china',
            zoom: 1.5,
            roam: true,
            label: {
              normal: {
                show: true
              },
              emphasis: {
                show: true
              }
            },
            data: this.chartData.seriesData
          }
        ]
      })
    }
  }
}
</script>
