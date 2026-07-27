import {
  generateXindexInOrder,
  i18nExpression
} from '@meicloud/render-engine'

const setRefusedReason = ($form: any, values: any, done: any, $rejectOrAcceptHandle:any) => {
  $rejectOrAcceptHandle($form.query('DeliveryNoticeVendor').get('data').currentRows, $form, 'REFUSE', values.refusedReason)
  done()
}

export default {
  type: 'void',
  title: i18nExpression('buyerDeliveryNotice.prompt11'), // 提示
  'x-component': 'RDialog',
  'x-component-props': {
    size: 'middle',
    class: 'the-splitDialog',
    'close-on-click-modal': false,
    beforeClose: `{{(done, type,closeLoading) => { 
          if ( type === 'ok') { 
            $self.query('*.refusedReasonDialog.form').take().submit(values => {
              $setRefusedReason($form,values,done,$rejectOrAcceptHandle)         
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
        refusedReason: {
          type: 'string',
          title: i18nExpression('buyerDeliveryNotice.prompt12'), // 请填写拒绝原因
          'x-decorator': 'FormItem'
        }
      })
    }
  }
}
export {
  setRefusedReason
}
