import {
  expression,
  generateXindexInOrder,
  i18nExpression
} from '@meicloud/render-engine'

import {
  requiredValidatorSegment
} from 'lib@/components/render-engine/schema-segments'

// 初审驳回
const reject = (values: any, $form: any, $queryEngine: any, $message: any, $bus: any, done: any, emitTabRemove:any, $attrs:any, $t:any) => {
  $queryEngine.request.baseRequest({
    'type': 'OnlineInvoice',
    'lang': 'zh-cn',
    'payload': [{ onlineInvoiceId: $form.values.onlineInvoiceId, rejectReason: values.rejectReason }],
    'action': 'firstReject'
  }).then((res: any) => {
    $message.success($t('agentOnlineInvoice.prompt5')) // 驳回成功
    done()
    emitTabRemove($attrs.tabName)
    $bus.$emit('OnlineInvoiceHead')
  })
}

export default {
  type: 'void',
  title: i18nExpression('purSettlementMod.reasonForRejection'), // 驳回原因
  'x-component': 'RDialog',
  'x-read-pretty': false,
  'x-component-props': {
    size: 'middle',
    'close-on-click-modal': false,
    beforeClose: expression(`(done, type) => {
        if ( type === 'ok') {
          $self.query('*.rejectDialog.form').take().submit(values => {
            $reject(values, $form, $queryEngine, $message, $bus,done,emitTabRemove,$attrs,$t) 
          })
        }else{
          done()
        }
      }`)
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
        rejectReason: {
          'x-decorator': 'FormItem',
          type: 'string',
          title: i18nExpression('purSettlementMod.pleaseFillReasonForRejection'),
          'x-component-props': {
            type: 'textarea',
            rows: 4
          },
          ...requiredValidatorSegment
        }

      })
    }
  }
}

export { reject }
