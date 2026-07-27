import { h } from '@meicloud/render-engine'
import {
  defineComponent,
  onMounted,
  onUnmounted,
  ref,
  getCurrentInstance,
  nextTick,
  watch
} from 'vue-demi'
import * as echarts from 'echarts'
import 'echarts/theme/macarons'

export const Echarts = defineComponent({
  name: 'RenderEcharts',
  props: {
    // 宽
    width: {
      type: String,
      default: '100%'
    },
    // 高
    height: {
      type: String,
      default: '370px'
    },
    // 配置
    option: {
      type: Object,
      default: () => {}
    }
  },
  setup (props, { attrs }) {
    // 获取当前实例
    const instance = getCurrentInstance()

    const chart: any = ref(null)
    const element = ref<HTMLDivElement>()

    // 监听页面宽度改变，调resize()方法重新适应大小
    const resizeHandler = () => chart.value.resize()

    // 设置图表配置
    const setChartOption = () => {
      if (chart.value) {
        chart.value.setOption(props.option)
      }
    }

    watch(
      () => props.option,
      val => {
        if (val) {
          setChartOption()
        }
      },
      { deep: true }
    )

    onMounted(() => {
      // 以当前实例创建echarts
      nextTick(() => {
        element.value = instance?.proxy.$refs?.chart as HTMLDivElement
        chart.value = echarts.init(element.value, 'macarons')
        if (props.option) {
          setChartOption()
        }
        window.addEventListener('resize', resizeHandler)
      })
    })

    onUnmounted(() => {
      window.removeEventListener('resize', resizeHandler)
      // 销毁实例
      if (chart.value) {
        chart.value.dispose()
        chart.value = null
      }
    })

    return () => {
      return h(
        'div',
        {
          class: 'chart-wrap'
        },
        {
          default: () => [
            h(
              'div',
              {
                ref: 'chart',
                style: `width: ${props.width}; height: ${props.height}; ${attrs.style || ''}`
              },
              {}
            )
          ]
        }
      )
    }
  }
})
