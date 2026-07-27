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
      default: '350px'
    },
    chartData: {
      type: Object,
      default: () => {
        return {
          seriesData: [
            { value: 65, name: '外观' },
            { value: 25, name: '材料' },
            { value: 8, name: '尺寸' },
            { value: 9, name: '环保' },
            { value: 19, name: '包装' },
            { value: 2, name: '其他' }
          ]
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
      chart: null,
      proportion: [] // 各个选项计算后的占比
    }
  },
  watch: {
    chartData: {
      handler: function () {
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
    // screenWidth(val) {
    //   this.chart.resize();
    // }
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

      let sum = 0
      let u = 0
      let legends = []
      var objString = JSON.stringify(this.chartData.seriesData)
      var seriesDatas = JSON.parse(objString)
      for (u in seriesDatas) {
        sum += parseFloat(seriesDatas[u].value)
        legends.push(seriesDatas[u].name)
      }
      if (sum == 0) {
        sum = 1
      }

      let i = -1

      let _this = this

      let colorList = [
        '#0A3ECD',
        '#177CFA',
        '#4A8AFF',
        '#3AC3EF',
        '#5DC9B8',
        '#F33A00'
      ]
      setTimeout(function () {
        _this.chart.setOption({
          title: {
            show: false
          },
          tooltip: {
            trigger: 'item'
          },
          legend: {
            data: legends,
            icon: 'rect',
            width: '15px',
            itemWidth: 14,
            bottom: '10px',
            textStyle: {
              color: '#242526'
            },
            formatter: function (name) {
              i++
              // console.log(i + name);
              // return name + ':  占比' + parseInt(seriesDatas[seriesDatas.length-i].value/sum*100) + '%';
              // console.log(i);
              if (seriesDatas.length <= i) {
                i = 0
              }
              return (
                name +
                ':  占比' +
                parseInt((seriesDatas[i].value / sum) * 100) +
                '%'
              )
            }
          },
          series: [
            {
              name: '',
              type: 'pie',
              radius: '55%',
              center: ['50%', '30%'],
              data: seriesDatas,
              // roseType: "radius",
              label: {
                color: 'rgba(255, 255, 255, 0.3)'
              },
              labelLine: {
                lineStyle: {
                  color: 'rgba(255, 255, 255, 0.3)'
                },
                smooth: 0.2,
                length: 10,
                length2: 20
              },
              itemStyle: {
                color: function (params) {
                  return colorList[params.dataIndex]
                }
              }
            }
          ]
        })
      }, 200)
    }
  }
}
</script>
