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
      this.chart = echarts.init(this.$el)
      let seriesData = JSON.parse(JSON.stringify(data))
      let arr1 = []; let arr2 = []; let arr3 = []
      for (let item of seriesData) {
        arr1.push(item.year)
        arr2.push(item.amount)
        arr3.push(item.frequency)
      }
      let option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross',
            crossStyle: {
              color: '#999'
            }
          }
        },
        color: ['#4C88FF', '#58B8F8', '#F39E67', '#F1D16E'],
        xAxis: [
          {
            type: 'category',
            data: arr1,
            axisPointer: {
              type: 'shadow'
            },
            axisTick: {
              show: false
            },
            axisLine: {
              show: false
            }
          }
        ],
        yAxis: [
          {
            type: 'value',
            name: this.$t('supRisk.winBidsNum'),
            min: 0,
            max: 50,
            interval: 10,
            axisLabel: {
              formatter: '{value} '
            },
            axisLine: {
              show: false
            },
            axisTick: {
              show: false
            }
          },
          {
            type: 'value',
            name: this.$t('dataConfMod.purchaseAmount'),
            min: 0,
            max: 500,
            interval: 100,
            axisLabel: {
              formatter: '{value}万',
              margin: 2
            },
            axisLine: {
              show: false
            },
            axisTick: {
              show: false
            }
          }
        ],
        series: [
          {
            name: this.$t('supRisk.winBidsNum'),
            type: 'bar',
            barWidth: 40,
            data: arr3
          },
          {
            name: this.$t('dataConfMod.purchaseAmount'),
            type: 'line',
            yAxisIndex: 1,
            data: arr2
          }
        ]
      }
      this.chart.setOption(option)
    }
  }
}
</script>
