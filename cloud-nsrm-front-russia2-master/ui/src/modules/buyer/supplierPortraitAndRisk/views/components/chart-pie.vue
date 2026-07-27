<template>
  <div
    :class="className"
    :style="{height:height,width:width}"
  />
</template>

<script>
import echarts from 'echarts' // echarts theme
import resize from '@/views/dashboard/buyer/components/mixins/resize'
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
      type: Array,
      default: []
    },
    isFormatter: {
      type: Boolean,
      default: true
    }
  },
  data () {
    return {
      chart: null
    }
  },
  watch: {
    chartData: {
      handler (nVal) {
        console.log('nVal:::', nVal)
        if (nVal && nVal.length) {
          this.$nextTick(() => {
            this.initChart(nVal)
          })
        }
      },
      immediate: true,
      deep: true
    }
  },
  beforeDestroy () {
    if (!this.chart) {
      return
    }
    this.chart.dispose()
    this.chart = null
  },
  methods: {
    initChart (data) {
      this.chart = echarts.init(this.$el, 'macarons')
      let seriesData = JSON.parse(JSON.stringify(data))
      let total = seriesData.reduce((pre, cur) => {
        return pre += cur.value
      }, 0)
      this.chart.setOption({
          tooltip: {
            trigger: 'item',
            formatter: '{b}：{d}%'
          },
          legend: {
            bottom: 10,
            left: 'center',
            orient: 'horizontal',
            icon: 'rect',
            itemWidth: 10,
            itemHeight: 10,
            formatter: name => {
              let curObj = seriesData.find(item => item.name === name)
              if (this.isFormatter) {
                let precent = Math.round((curObj.value / total) * 100) + '%'
                return `${name}:${precent}`
              }
              return `${name}:${curObj.value}${this.$t('common.yuan')}`
            },
            textStyle: {
              color: '#393E45'
            }
          },
          color: ['#4C88FF', '#58B8F8', '#F39E67', '#F1D16E'],
          series: [
            {
              type: 'pie',
              radius: ['30%', '60%'],
              center: [ '50%', '50%' ],
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
