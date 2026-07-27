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
  title: i18nExpression('oneStopShopping.inputRejectReason'),
  'x-component': 'RDialog',
  'x-component-props': {
    'close-on-click-modal': false,
    destroyOnClose: true,
    size: 'small',
    footerButtonList: expression(`(_, { cancelButton,okButton }) => {
      return [
        cancelButton,
        okButton,
      ]
        
      }`),
    beforeClose: expression(`(done, type) => {
        if ( type === 'ok') {
          $rejectSubmit($form,$queryEngine,done)
        } else {
          done()
          }
        }
      `)
  },
  properties: {
    rejectForm: {
      type: 'object',
      'x-decorator': 'FormLayout',
      'x-decorator-props': {
        layout: 'vertical'
      },
      'x-component': 'FormGrid',
      'x-component-props': {
        maxColumns: 1,
        columnGap: 32,
        rowGap: 0
      },
      properties: {
        returnReason: {
          type: 'string',
          'x-decorator': 'FormItem',
          title: "{{$t('purchaseDemand.rejectReason')}}", 
          'x-component-props': {
            disabled: expression('$form.readPretty')
          },
          ...requiredValidatorSegment,
        },

      }
    }
  }
}
