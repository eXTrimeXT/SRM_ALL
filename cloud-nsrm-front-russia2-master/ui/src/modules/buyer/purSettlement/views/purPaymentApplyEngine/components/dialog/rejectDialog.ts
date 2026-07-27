import {
  expression,
  generateXindexInOrder,
  i18nExpression
} from '@meicloud/render-engine'

import {
  requiredValidatorSegment
} from 'lib@/components/render-engine/schema-segments'

export default {
  type: 'void',
  title: i18nExpression('purSettlementMod.reasonForRejection'),
  'x-component': 'RDialog',
  'x-read-pretty': false,
  'x-component-props': {
    size: 'middle',
    'close-on-click-modal': false,
    beforeClose: expression(`(done, type) => {
      if ( type === 'ok') {
        $self.query('*.rejectDialog.form').take().submit(values => {
          // TODO       
        })
      }
      done()
    }`)
  },
  properties: {
    form: {
      type: 'object',
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
