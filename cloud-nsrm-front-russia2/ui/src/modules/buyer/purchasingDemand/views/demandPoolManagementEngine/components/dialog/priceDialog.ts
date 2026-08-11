import {
  generateXindexInOrder,
  i18nExpression
} from '@meicloud/render-engine'
import {
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'

const savePriceFetch = ($form: any, values: any, done: any) => {
  done()
}

export default {
  type: 'void',
  title: i18nExpression('purchaseDemand.priceFromToTips'), // 请选择价格起止日期
  'x-component': 'RDialog',
  'x-component-props': {
    size: 'middle',
    'close-on-click-modal': false,
    beforeClose: `{{(done, type) => { 
        if ( type === 'ok') { 
          $self.query('*.priceDialog.form').take().submit(values => {
            $savePriceFetch($form, values, done)         
          })
        } else {
          done()
        }
      }}}`
  },
  properties: {
    form: {
      type: 'object',
      'x-query-engine-skip': true,
      'x-decorator': 'FormLayout',
      'x-decorator-props': {
        layout: 'vertical'
      },
      'x-component': 'FormGrid',
      'x-component-props': {
        maxColumns: 2,
        columnGap: 32,
        rowGap: 0
      },
      properties: generateXindexInOrder({
        from: {
          title: i18nExpression('purchaseDemand.effectiveDate'), // 价格生效日期
          'x-decorator': 'FormItem',
          ...yearMonthDaySelectorSegment
        },
        to: {
          title: i18nExpression('purchaseDemand.expirationDate'), // 价格失效日期
          'x-decorator': 'FormItem',
          ...yearMonthDaySelectorSegment
        }
      })
    }
  }
}
export {
  savePriceFetch
}
