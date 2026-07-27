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
        grid: {
          left: 60,
          top: 10,
          bottom: 30,
          right: '30%'
        },
        color: ['#4C88FF', '#3BC2EF'],
        legend: {
          data: [this.$t('dataConfMod.order'), this.$t('dashboard.orderGoods')],
          orient: 'vertical',
          icon: 'circle',
          itemWidth: 10,
          itemHeight: 10,
          top: 'middle',
          right: '16%',
          textStyle: {
            color: '#393E45'
          }
        },
        xAxis: [
          {
            type: 'category',
            data: [this.$t('reportMod.January'), this.$t('reportMod.February'), this.$t('reportMod.March'), this.$t('reportMod.April')],
            axisPointer: {
              type: 'shadow'
            },
            axisTick: {
              show: false
            },
            axisLabel: {
              textStyle: {
                color: '#51555B'
              }
            }
          }
        ],
        yAxis: [
          {
            type: 'value',
            name: 'Precipitation',
            min: 0,
            axisTick: {
              show: false
            },
            splitLine: {
              show: false
            },
            splitArea: {
              show: false
            },
            axisLabel: {
              textStyle: {
                color: '#51555B'
              }
            }
          }
        ],
        series: [
          {
            name: this.$t('dataConfMod.order'),
            type: 'bar',
            data: [
              30, 20, 40, 50
            ]
          },
          {
            name: this.$t('dashboard.orderGoods'),
            type: 'bar',
            data: [
              10, 15, 25, 10
            ]
          }
        ]
      }
      this.chart.setOption(option)
    }
  }
}
</script>
