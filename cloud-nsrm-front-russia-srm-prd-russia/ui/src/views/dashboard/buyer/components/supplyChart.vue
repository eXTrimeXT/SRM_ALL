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
        { value: 1048, name: '供方数(X<=2)' },
        { value: 735, name: '数量(2<x<=5)' },
        { value: 580, name: '数量(5<x<=10)' },
        { value: 484, name: '数量(x>10)' }
      ]
      let textData = [
        { label: '供方数(X<=2)', name: 'A' },
        { label: '数量(2<x<=5)', name: 'B' },
        { label: '数量(5<x<=10)', name: 'C' },
        { label: '数量(x>10)', name: 'D' }
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
              return `${name}:品类占比${precent}`
            },
            textStyle: {
              color: '#393E45'
            }
          },
          color: ['#4C88FF', '#58B8F8', '#F39E67', '#F1D16E'],
          series: [
            {
              name: '品类供方数占比',
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
