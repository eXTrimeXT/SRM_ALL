import {
  generateXindexInOrder,
  i18nExpression
} from '@meicloud/render-engine'
import {
  requiredValidatorSegment
} from 'lib@/components/render-engine/schema-segments'

const rejectFetch = ($form: any, $queryEngine: any, values: any, done: any, $message: any, $t: any, closeLoading: any) => {
  const data = $form.query('PrRequirementPoolForBuyer').get('data')
  $queryEngine.request.baseRequest({
    'type': 'PrRequirementPoolForBuyer',
    'lang': 'zh-cn',
    'query': {
      '*': {}
    },
    'payload': [{
      'requirementLineIds': data.requirementLineIds,
      'rejectReason': values.rejectReason
    }],
    'action': 'batchReturn'
  }).then((res: any) => {
    $message.success($t('common.success'))
    data.requirementLineIds = []
    done()
    $queryEngine.state.paginationManagement.refresh()
  }).catch(() => {
    closeLoading()
  })
}

export default {
  type: 'void',
  title: i18nExpression('purchaseDemand.rejectReason'), // 退回原因
  'x-component': 'RDialog',
  'x-component-props': {
    size: 'middle',
    'close-on-click-modal': false,
    beforeClose: `{{(done, type,closeLoading) => { 
      if ( type === 'ok') { 
        $self.query('*.rejectDialog.form').take().submit(values => {
          $rejectFetch($form,$queryEngine,values,done,$message,$t,closeLoading)       
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
        rejectReason: {
          type: 'string',
          title: i18nExpression('purchaseDemand.rejectReason'), // 退回原因
          'x-decorator': 'FormItem',
          'x-component-props': {
            type: 'textarea',
            autosize: { minRows: 2, maxRows: 4 }
          },
          ...requiredValidatorSegment
        }
      })
    }
  }
}
export {
  rejectFetch
}
