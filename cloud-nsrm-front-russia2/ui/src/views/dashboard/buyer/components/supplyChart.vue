<template>
  <div
    :class="className"
    :style="{height:height,width:width}"
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
    }
  },
  data () {
    return {
      chart: null
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
      let seriesData = [
        { value: 1048, name: this.$t('cusEntry.dashboard.quantity1') }, // 供方数(X<=2)
        { value: 735, name: this.$t('cusEntry.dashboard.quantity2') }, // 数量(2<x<=5)
        { value: 580, name: this.$t('cusEntry.dashboard.quantity3') }, // 数量(5<x<=10)
        { value: 484, name: this.$t('cusEntry.dashboard.quantity4') } // 数量(x>10)
      ]
      let textData = [
        { label: this.$t('cusEntry.dashboard.quantity1'), name: 'A' }, // 供方数(X<=2)
        { label: this.$t('cusEntry.dashboard.quantity2'), name: 'B' }, // 数量(2<x<=5)
        { label: this.$t('cusEntry.dashboard.quantity3'), name: 'C' }, // 数量(5<x<=10)
        { label: this.$t('cusEntry.dashboard.quantity4'), name: 'D' } // 数量(x>10)
      ]
      let total = seriesData.reduce((pre, cur) => {
        return pre += cur.value
      }, 0)
      this.chart.setOption({
          tooltip: {
            trigger: 'item',
            formatter: '{b}：{d}%'
          },
          legend: {
            top: 'middle',
            left: 200,
            orient: 'vertical',
            icon: 'rect',
            itemWidth: 10,
            itemHeight: 10,
            formatter: name => {
              let curObj = seriesData.find(item => item.name === name)
              let precent = Math.round((curObj.value / total) * 100) + '%'
              return `${name}: ${this.$t('reportMod.categoryProportion')}${precent}`
            },
            textStyle: {
              color: '#393E45'
            }
          },
          color: ['#4C88FF', '#58B8F8', '#F39E67', '#F1D16E'],
          series: [
            {
              name: this.$t('reportMod.supplierAnalysisTitle[10]'),
              type: 'pie',
              radius: ['40%', '76%'],
              center: [ 100, '50%' ],
              avoidLabelOverlap: false,
              label: {
                show: false,
                position: 'center'
              },
              labelLine: {
                show: false
              },
              data: seriesData
            }
          ]
      })
    }
  }
}
</script>
