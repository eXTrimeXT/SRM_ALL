import {
  expression,
  i18nExpression,
  generateCharExpressionByFunction
} from "@meicloud/render-engine"
export const authInfo = {
  authInfo: {
    type: 'void',
    'x-component': 'CollapseItem',
    'x-component-props': {
      title: `{{observer(
        {
          render(h) {
            return h($$components.Note, {
              props: {
                title: t('cusEntry.vendorMod.authInfo'),
                value: $form.values.extRejectAttribute10,
                readonly: true
              }
            })
          }
        }
      )}}`
    },
    'x-visible': generateCharExpressionByFunction(({ $form }) => {
      return $form.query('state').get('data').userType !== 'PERSONAL'
    }),
    properties: {
      layout: {
        type: 'void',
        'x-decorator': 'FormLayout',
        'x-decorator-props': {
          layout: 'vertical'
        },
        'x-component': 'FormGrid',
        'x-component-props': {
          maxColumns: 3,
          columnGap: 32,
          rowGap: 0
        },
        properties: {
          // 认证联系人
          certifiedContact: {
            type: 'string',
            'x-decorator': 'FormItem',
            'x-component': 'Input',
            title: expression(`$t('cusEntry.vendorMod.certifiedContact')`),
            'x-component-props': {
              disabled: true,
              maxlength: 100
            }
          },
          // 认证联系人电话
          certifiedContactPhone: { 
            type: 'string',
            'x-decorator': 'FormItem',
            'x-component': 'Input',
            title: expression(`$t('cusEntry.vendorMod.certifiedContactPhone')`),
            'x-component-props': {
              disabled: true
            },
            'x-validator': {
              message: i18nExpression('dataConfMod.msgContactPhone'),
              validator: expression(`(value, rule) => {
                if(value && !validatePhone(value)){
                  return $t('vendorMod.correctPhoneNumber')
                }
              }`)
            }
          }
        }
      }
    }
  }
}
