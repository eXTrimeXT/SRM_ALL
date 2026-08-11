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
            title: expression(`$t('common.vendorName') + '（' + $t('vendorMod.userName') + '）'`),
            'x-component-props': {
              'disabled': expression(`$disabled`),
              // placeholder: i18nExpression('userInfo.onlyNumOrEn'),
              // '@change': expression(`() => {
              //                   let values = $self.value
              //                   console.log($self)
              //                   $self.setValue(values.replace(/[\\W]/g, ''))
              //                 }`)
            },
            'x-validator': {
              required: true,
              message: i18nExpression('vendorMod.enterUserName')
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
              message: i18nExpression('dataConfMod.msgContactPhone'),
              validator: expression(`(value, rule) => {
                if(value && !validatePhone(value)){
                  return $t('vendorMod.correctPhoneNumber')
                }
              }`)
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
              message: i18nExpression('vendorMod.pleaseInputEmail'),
              validator: expression(`(value, rule) => {
                if(value && !validEmail(value)){
                  return $t('vendorMod.correctEmail')
                }
              }`)
            }
          },
          // 纳税人识别号
          lcCode: {
            type: 'string',
            'x-decorator': 'FormItem',
            'x-component': 'Input',
            title: expression(`$t('vendorMod.lcCode')`),
            'x-component-props': {
              'disabled': expression(`$disabled`),
              '@blur': expression(`(value) => {
                if ($self.value) {
                  app.$http({
                    url: '/api-rbac/extUser/lcCodeVerify',
                    method: 'GET',
                    params: { lcCode: $self.value, isPersonalAccount: 'N' }
                  }).then(res => {
                    if (res.code + '' === '0') {
                      const data = res.data
                      if (data.isRemind === 'Y') {
                        let maskedName = ''
                        if(data.nickname.length==2){
                          maskedName=data.nickname.substring(0,1)+'*'
                        }else if(data.nickname.length==3){
                          maskedName=data.nickname.substring(0,1)+"*"+data.nickname.substring(2,3)
                        }else if(data.nickname.length>3){
                          maskedName=data.nickname.substring(0,1)+"*"+'*'+data.nickname.substring(3,data.nickname.length)
                        }
                        const maskedPhone = data.phone.replace(data.phone.substring(3,7),'****')
                        // 该供应商已被注册，请联系' + maskedName + '，账号：' + maskedPhone + '，进行子账号维护操作。
                        app.$alert($t('cusEntry.vendorMod.registerTips', { maskedName, maskedPhone }), '', {
                          confirmButtonText: $t('common.confirm'),
                          callback: action => {}
                        });
                      }
                    }
                  })
                }
              }`)
            },
            'x-validator': {
              required: true,
              message: i18nExpression('vendorMod.msgLcCode')
            }
          },
          // 账户组
          'accountGroup': {
            type: 'string',
            default: 'Z001',
            'x-decorator': 'FormItem',
            'x-component': 'Input',
            title: expression(`$t('cusEntry.vendorMod.accountGroup')`),
            'x-component-props': {
              'disabled': true
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
            }
          }
        }
      }
    }
  },
}
