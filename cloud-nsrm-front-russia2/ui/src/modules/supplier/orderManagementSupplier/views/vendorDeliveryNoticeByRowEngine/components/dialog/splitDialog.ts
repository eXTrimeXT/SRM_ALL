import {
  generateXindexInOrder,
  expression,
  i18nExpression
} from '@meicloud/render-engine'
import {
  requiredValidatorSegment
} from 'lib@/components/render-engine/schema-segments'
// @ts-ignore
import { deepClone } from '@/utils'

const split = ($form: any, values: any, done: any) => {
  const row = deepClone($form.query('DeliveryNoticeVendor').get('data').currentOrderRow)
  const arr = []
  row.confirmNum = null
  row.promiseReceiveDate = null
  row.refuseReason = null
  row.isParentLine = false
  for (let i = 0; i < values.splitNum; i++) {
    arr.push({ ...row })
  }
  $form.values.detailList.splice(+row.lineNum, 0, ...arr)
  $form.values.detailList.forEach((item:any, index:any) => {
    item.lineNum = index + 1 + ''
  })
  done()
}

export default {
  type: 'void',
  title: i18nExpression('purchaseOrder.split'), // 拆行
  'x-component': 'RDialog',
  'x-component-props': {
    size: 'middle',
    class: 'the-splitDialog',
    'close-on-click-modal': false,
    beforeClose: `{{(done, type,closeLoading) => { 
        if ( type === 'ok') { 
          $self.query('*.splitDialog.form').take().submit(values => {
            $split($form,values,done)         
          }).catch(() => {
            closeLoading()
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
        splitNum: {
          type: 'number',
          default: 1,
          title: i18nExpression('purchaseOrder.splitNum'), // 新增行数,
          'x-decorator': 'FormItem',
          ...requiredValidatorSegment,
          'x-validator': {
            required: true,
            message: i18nExpression('common.requiredField'),
            validator: expression(`(value, rule) => {
              if(value < 1){
                return $t('purchaseOrder.prompt16') // 拆分行数最小为1
              } else if (String(value).indexOf('.') > -1) {
                return $t('purchaseOrder.prompt17') // 拆分行数请输入整数
              }
            }`)
          },
          'x-component-props': {
            max: 50
          }
        }
      })
    }
  }
}
export {
  split
}
