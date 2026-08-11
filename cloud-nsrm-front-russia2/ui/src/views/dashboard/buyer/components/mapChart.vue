<template>
  <div
    :class="className"
    :style="{height:height,width:width}"
  />
</template>

<script>
import echarts from 'echarts' // echarts theme
import 'echarts/map/js/china.js' // 引入中国地图数据
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
    }
  },
  data () {
    return {
      chart: null,
      chartData: {
        seriesData: []
      }
    }
  },
  // watch: {
  //   chartData: {
  //     handler () {
  //       this.initChart()
  //       this.chart.resize()
  //     },
  //     deep: true
  //   }
  // },
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
        visualMap: [
          {
            type: 'piecewise',
            pieces: [
              { gt: 30, label: this.$t('cusEntry.dashboard.suppliersNum1'), color: '#035CCD' }, // 供应商数量(X>30)
              { gte: 15, lt: 30, label: this.$t('cusEntry.dashboard.suppliersNum2'), color: '#4C88FF' }, // 供应商数量(30>X>=15)
              { gte: 5, lt: 15, label: this.$t('cusEntry.dashboard.suppliersNum3'), color: '#67AFF4' }, // 供应商数量(15>X>=5)
              { gt: 0, lt: 5, label: this.$t('cusEntry.dashboard.suppliersNum4'), color: '#C5E4FF' }, // 供应商数量(5>X>0)
              { value: 0, label: this.$t('cusEntry.dashboard.suppliersNum5'), color: '#EDEDED' } // 供应商数量(X=0)
            ],
            left: '70%',
            top: 'center',
            itemWidth: 10,
            itemHeight: 10,
            align: 'left',
            textStyle: {
              color: '#393E45'
            }
          }
        ],
        series: [
          {
            type: 'map',
            mapType: 'china',
            // zoom: 1.5,
            roam: true,
            layoutCenter: ['40%', '50%'],
            layoutSize: '120%',
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
