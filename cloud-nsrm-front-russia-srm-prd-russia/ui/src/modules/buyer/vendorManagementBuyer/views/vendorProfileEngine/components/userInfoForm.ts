import {
  expression, i18nExpression
} from "@meicloud/render-engine"

export const userInfoForm = {
  userInfoForm: {
    type: 'void',
    'x-component': 'CollapseItem',
    'x-component-props': {
      title: i18nExpression('vendorMod.vendorUserInfo')
    },
    'x-query-engine-skip': true,
    properties: {
      userInfo: {
        type: 'object',
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
        'x-query-engine-skip': true,
        properties: {
          // 用户名
          'username': {
            type: 'string',
            'x-decorator': 'FormItem',
            'x-component': 'Input',
            title: expression(`$t('vendorMod.userName')`),
            'x-component-props': {
              'disabled': expression(`$disabled`),
              '@change': expression(`() => {
                                let values = $self.value
                                console.log($self)
                                $self.setValue(values.replace(/[\\W]/g, ''))
                              }`)
            },
            'x-validator': {
              required: true,
              message: i18nExpression('请输入用户名')
            }
          },
          // 名称
          'nickname': {
            type: 'string',
            'x-decorator': 'FormItem',
            'x-component': 'Input',
            title: expression(`$t('bid_mod.linkManName')`),
            'x-component-props': {
              'disabled': expression(`$disabled`)
            },
            'x-validator': {
              required: true,
              message: i18nExpression('vendorMod.msgInputNickname')
            }
          },
          // 联系人电话
          'phone': {
            type: 'string',
            'x-decorator': 'FormItem',
            'x-component': 'Input',
            title: expression(`$t('vendorMod.contactMethod')`),
            'x-component-props': {
              'disabled': expression(`$disabled`)
            }
          },
          // 邮箱
          'email': {
            type: 'string',
            'x-decorator': 'FormItem',
            'x-component': 'Input',
            title: expression(`$t('common.email')`),
            'x-component-props': {
              'disabled': expression(`$disabled`)
            },
            'x-validator': {
              required: true,
              message: i18nExpression('请输入用户名')
            }
          },
          // 岗位
          'ceeaJobcodeDescr': {
            type: 'string',
            'x-decorator': 'FormItem',
            'x-component': 'Input',
            title: expression(`$t('dataConfMod.position')`),
            'x-component-props': {
              'disabled': expression(`$disabled`)
            }
          },
          // 主账号
          'usernameMain': {
            type: 'string',
            'x-decorator': 'FormItem',
            'x-component': 'Input',
            title: expression(`$t('vendorMod.mainAccount')`),
            'x-component-props': {
              'disabled': expression(`$disabled`)
            },
            'x-reactions': expression(`field => {
                setTimeout(() => {
                  const username = $form.query('.username').take().value
                  field.value = username
                }, 50)

            }`)
          }
        }
      }
    }
  },
}
