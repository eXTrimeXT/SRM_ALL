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
              placeholder: i18nExpression('userInfo.onlyNumOrEn'),
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
            title: expression(`$t('dataConfMod.userName')`),
            'x-component-props': {
              'disabled': expression(`$disabled`),
            },
            'x-validator': {
              required: true,
              message: i18nExpression('vendorMod.msgInputNickname')
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
              // '@change': expression(`() => {
              //                   let pattern = /^[A-Za-z\\d]+([-_.][A-Za-z\\d]+)*@([A-Za-z\\d]+[-.]){1,2}[A-Za-z\\d]{2,5}$/g
              //                   if (!pattern.test($self.value)) {
              //                     app.$message.warning($t('vendorMod.msgEmailErroe'))
              //                   }
              //                 }`)
            },
            'x-validator': {
              required: true,
              message: i18nExpression('请输入邮箱'),
              validator: expression(`(value, rule) => {
                if(value && !validEmail(value)){
                  return '请输入格式正确的邮箱地址'
                }
              }`)
            }
          },
          // 联系人电话
          'phone': {
            type: 'string',
            'x-decorator': 'FormItem',
            'x-component': 'Input',
            title: expression(`$t('vendorMod.contactPhone')`),
            'x-component-props': {
              'disabled': expression(`$disabled`)
            },
            'x-validator': {
              required: true,
              message: i18nExpression('请输入联系人电话'),
              validator: expression(`(value, rule) => {
                if(value && !validatePhone(value)){
                  return '请输入格式正确的电话号码'
                }
              }`)
            }
          },
          // 岗位
          'position': {
            type: 'string',
            'x-decorator': 'FormItem',
            'x-component': 'Input',
            title: expression(`$t('components.orgPositionSel.position')`),
            'x-component-props': {
              'disabled': expression(`$disabled`)
            }
          },
          // 绿色通道引入供应商的原因详述
          greenChannelReason: {
            type: 'string',
            'x-decorator': 'FormItem',
            title: expression(`$t('vendorMod.greenChannelReason')`),
            'x-component-props': {
              'disabled': expression(`$disabled`),
              type: 'textarea'
            },
            'x-decorator-props': {
              gridSpan: 3
            },
            'x-validator': {
              required: true
            }
          }
        }
      }
    }
  },
}
