import {
  expression,
  generateXindexInOrder,
  i18nExpression
} from '@meicloud/render-engine'

import {
  requiredValidatorSegment,
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'

export default {
  type: 'void',
  title: i18nExpression('vendorMod.batchMaintain'),
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
          $batchSubmit($form,$queryEngine,done)
        } else {
          done()
          }
        }
      `)
  },
  properties: {
    batchUpdateForm: {
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
        // currencyCode: {
        //   type: 'string',
        //   title: "{{$t('cusEntry.sup.currency')}}", 
        //   'x-decorator': 'FormItem',
        //   'x-component': 'DictSelect',
        //   'x-component-props': {
        //     code: 'currency',
        //   }
        // },
        requirementDate: {
          'x-decorator': 'FormItem',
          title: "{{$t('purchaseDemand.requirementDate')}}", 
          ...yearMonthDaySelectorSegment
        }
      }
    }
  }
}
