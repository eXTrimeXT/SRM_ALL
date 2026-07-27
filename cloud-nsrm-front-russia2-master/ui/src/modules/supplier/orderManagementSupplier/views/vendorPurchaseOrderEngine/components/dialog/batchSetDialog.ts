import {
  generateXindexInOrder,
  i18nExpression
} from '@meicloud/render-engine'
import {
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'

const batchSet = ($form: any, values: any, done: any) => {
  const { vendorRefuse, vendorModifyCount, vendorSplitReply } = $form.query('OrderVendor').get('data').configValue
  $form.values.detailList.forEach((item:any) => {
    if ($form.query('OrderVendor').get('data').batchSetIds.includes(item.lineNum)) {
      if (vendorRefuse || vendorModifyCount || vendorSplitReply) {
        item.confirmNum = values.confirmNum ? (values.numType === 1 ? values.confirmNum : (item.orderNum * values.confirmNum / 100)) : item.confirmNum
      }
      item.ceeaPromiseReceiveDate = values.ceeaPromiseReceiveDate ?? item.ceeaPromiseReceiveDate
      item.refusedReason = values.refusedReason ?? item.refusedReason
    }
  })
  done()
}

export default {
  type: 'void',
  title: i18nExpression('purchaseOrder.batchSet'), // 批量维护
  'x-component': 'RDialog',
  'x-component-props': {
    size: 'middle',
    'close-on-click-modal': false,
    beforeClose: `{{(done, type,closeLoading) => { 
      if ( type === 'ok') { 
        $self.query('*.batchSetDialog.form').take().submit(values => {
          $batchSet($form,values,done)         
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
        name: {
          type: 'void',
          'x-hidden': '{{(!$form.query(\'OrderVendor\').get(\'data\').configValue.vendorRefuse && !$form.query(\'OrderVendor\').get(\'data\').configValue.vendorModifyCount && !$form.query(\'OrderVendor\').get(\'data\').configValue.vendorSplitReply) || ($form.values.demandType === \'NONPRODUCTIVE_DEMAND\' && !$form.query(\'OrderVendor\').get(\'data\').configValue.vendorSplitReply)}}',
          title: i18nExpression('purchaseOrder.confirmNum'), // 供方确认订单数量
          'x-decorator': 'FormItem',
          'x-decorator-props': {
            feedbackLayout: 'none'
          },
          'x-component': 'Space',
          properties: {
            numType: {
              type: 'string',
              'x-decorator': 'FormItem',
              'x-component': 'Select',
              'x-component-props': {
                disabled: '{{!$form.query(\'OrderVendor\').get(\'data\').configValue.vendorSplitReply && ($form.query(\'OrderVendor\').get(\'data\').configValue.vendorRefuse && !$form.query(\'OrderVendor\').get(\'data\').configValue.vendorModifyCount)}}'
              },
              default: 1,
              enum: [
                {
                  label: i18nExpression('purchaseOrder.numType1'), // '数值'
                  value: 1
                },
                {
                  label: i18nExpression('purchaseOrder.numType2'), // '百分比'
                  value: 2
                }
              ]
            },
            confirmNum: {
              type: 'number',
              'x-decorator': 'FormItem'
            }
          }
        },
        ceeaPromiseReceiveDate: {
          'x-decorator': 'FormItem',
          title: i18nExpression('purchaseDemand.promiseReceiveDate'), // 供方承诺到货日期
          ...yearMonthDaySelectorSegment,
          'x-component-props': {
            ...yearMonthDaySelectorSegment['x-component-props'],
            'picker-options': {
              disabledDate: (time:any) => {
                const start = new Date()
                return time.getTime() < start.getTime() - 24 * 60 * 60 * 1000
              }
            }
          }

        },
        refusedReason: {
          type: 'string',
          title: i18nExpression('purchaseOrder.refusedReason'), // 确认意见,
          'x-decorator': 'FormItem',
          'x-component-props': {
            maxlength: 50
          }

        }
      })
    }
  }
}
export {
  batchSet
}
