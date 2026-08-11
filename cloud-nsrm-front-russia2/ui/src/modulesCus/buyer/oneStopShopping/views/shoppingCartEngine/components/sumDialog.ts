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
  title: i18nExpression('oneStopShopping.setSummaryAndNoticeUser'),
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
          $confirmSum($form,$queryEngine,done)
        } else {
          done()
          }
        }
      `)
  },
  properties: {
    sumForm: {
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
        summaryNickname: {
          type: 'string',
          title: "{{$t('oneStopShopping.summaryUser')}}",
          'x-decorator': 'FormItem',
          'x-component': 'QuickSearchWrapper',
          'x-component-props': {
            showKey: 'username',
            propKey: 'username',
            'name': 'scc_rbac_user_display',
            '@close-quicksearch': expression(`(val, scope) => {
              $values.sumForm.summaryUserId = val ? val.userId : ''
              $values.sumForm.summaryEmpNo = val ? val.username : ''
              $values.sumForm.summaryNickname = val ? val.nickname : ''
            }`)
          }
        },
        deptLeaderUserNickname: {
          type: 'string',
          title: "{{$t('cusEntry.inq.departmentLeader')}}",
          'x-decorator': 'FormItem',
          'x-component': 'QuickSearchWrapper',
          'x-component-props': {
            showKey: 'nickname',
            name: 'scc_rbac_user_display',
            '@close-quicksearch': expression(`val => {
              const {
                userId,
                nickname
              } = val || {}
              $values.sumForm.deptLeaderUserId = userId || ''
              $values.sumForm.deptLeaderUserNickname = nickname || ''
            }`)
          },
        }
      }
    }
  }
}
