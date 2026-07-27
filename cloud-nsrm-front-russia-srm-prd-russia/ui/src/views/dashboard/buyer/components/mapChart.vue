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
              { gt: 30, label: '供应商数量(X>30)', color: '#035CCD' },
              { gte: 15, lt: 30, label: '供应商数量(30>X>=15)', color: '#4C88FF' },
              { gte: 5, lt: 15, label: '供应商数量(15>X>=5)', color: '#67AFF4' },
              { gt: 0, lt: 5, label: '供应商数量(5>X>0)', color: '#C5E4FF' },
              { value: 0, label: '供应商数量(X=0)', color: '#EDEDED' }
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
