import {
  expression,
  i18nExpression,
} from "@meicloud/render-engine"
import {
  requiredValidatorSegment
} from 'lib@/components/render-engine/schema-segments'
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
    'x-visible': expression(`$form.query('state').get('data').overseasRelation != 'PERSONAL'`),
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
              disabled: expression('$form.query(\'state\').get(\'data\').$disabled'),
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
              disabled: expression('$form.query(\'state\').get(\'data\').$disabled')
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
