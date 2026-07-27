/**
 * @description 供应商报价记录图表
 */

import { parseTime } from '@/utils'
import i18n from '@/lang'

interface PaymentTypeProps {}

// 调色盘 27个 超过轮回
const colorList = [
  '#FF005A',
  '#32C5E9',
  '#2f4554',
  '#e7bcf3',
  '#759aa0',
  '#e69d87',
  '#8dc1a9',
  '#ea7e53',
  '#eedd78',
  '#9d96f5',
  '#67E0E3',
  '#7289ab',
  '#91ca8c',
  '#f49f42',
  '#37A2DA',
  '#dd6b66',
  '#ff9f7f',
  '#73a373',
  '#fb7293',
  '#FFDB5C',
  '#E062AE',
  '#73b9bc',
  '#8378EA',
  '#96BFFF',
  '#c23531',
  '#9FE6B8',
  '#E690D1'
]

/**
 * 所有日期排序，升序
 * @param a
 * @param b
 */
const sortDownXAxisDate = (a: any, b: any) => {
  return Date.parse(a) - Date.parse(b)
}

/**
 * 供应商日期排序，升序
 * @param a
 * @param b
 */
const sortDownVendorDate = (a: Record<any, any>, b: Record<any, any>) => {
  return Date.parse(a[0]) - Date.parse(b[0])
}

/**
 * 编排数据并设置配置图表
 * @param $form
 * @param priceNodes
 * @param title
 * @param prevPathAddress 需要拼接的form path address对象
 */
const $arrangeOnSetChartData = ($form: any, priceNodes: Record<any, any> | any, title: string, prevPathAddress?: any) => {
  if (!priceNodes || typeof priceNodes !== 'object') {
    return
  }

  // 所有日期 X轴 升序
  let xAxis: any[] = []
  // 折线配置 一个供应商一个折线
  const series: any[] = []

  const vendorKeys = Object.keys(priceNodes)
  if (priceNodes && vendorKeys.length > 0) {
    vendorKeys.forEach(item => {
      // 添加一条折线
      series.push({
        name: item,
        data: priceNodes[item]
          .map((itemVendor: any) => {
            // 横轴
            xAxis.push(parseTime(itemVendor.date))
            return [parseTime(itemVendor.date), itemVendor.notaxPrice]
          })
          // 时间排序
          .sort(sortDownVendorDate)
      })
    })
  }
  // 去重，排序
  xAxis = [...new Set(xAxis)].sort(sortDownXAxisDate)

  // 颜色数量
  const colorListLength = colorList.length
  const seriesList = series.map((item, index) => {
    // 取余轮回
    const colorIndex = index % colorListLength
    return {
      name: item.name,
      itemStyle: {
        normal: {
          color: colorList[colorIndex],
          lineStyle: {
            color: colorList[colorIndex],
            width: 2
          }
        }
      },
      smooth: true,
      type: 'line',
      data: item.data,
      animationDuration: 2800,
      animationEasing: 'cubicInOut'
    }
  })

  // 设置图表数据
  const chartOption = {
    xAxis: {
      data: xAxis,
      boundaryGap: false,
      axisTick: {
        show: false
      }
    },
    yAxis: {
      name: i18n.t('bidMod.quotenotaxPrice2'),
      axisTick: {
        show: false
      }
    },
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: vendorKeys,
      top: 'bottom'
    },
    title: {
      // 标题 物料名称
      text: title,
      padding: [5, 10],
      left: 'center',
      top: 'top'
    },
    grid: {
      left: 40,
      right: 60,
      bottom: 40,
      top: 30,
      containLabel: true
    },
    series: seriesList
  }

  // 有值才显示
  $form.query(prevPathAddress ? prevPathAddress.concat('quoteRecordChartWrap') : 'quoteRecordChartWrap').take((field: any) => {
    console.log(field)
    // field.setProperties({
    //   'x-hidden': false
    // })
  })

  // 可以拼接上级path
  $form.query(prevPathAddress ? prevPathAddress.concat('quoteRecordChart') : 'quoteRecordChart').take((field: any) => {
    field.setComponentProps({
      option: chartOption
    })
  })
}

export default function (props?: PaymentTypeProps): Record<any, any> {
  return {
    quoteRecordChartWrap: {
      type: 'void',
      'x-component': 'div',
      // TODO 默认需要隐藏，有值才放开
      'x-hidden': false,
      properties: {
        chartTips: {
          type: 'void',
          'x-component': 'p',
          'x-component-props': {
            style: 'font-size: 12px; color: red;'
          },
          'x-content': "{{$t('competition.chartTips')}}"
        },
        quoteRecordChart: {
          type: 'void',
          'x-component': 'Echarts'
        }
      }
    }
  }
}

export {
  $arrangeOnSetChartData
}
