import {
  expression, generateCharExpressionByFunction, i18nExpression
} from '@meicloud/render-engine'

export const companyNatureEngine = {
  steps: {
    type: 'void',
    'x-decorator': 'div',
    'x-decorator-props': {
      class: 'comInfosteps'
    },
    'x-component': 'Steps',
    'x-component-props': {
      alignCenter: true,
      finishStatus: 'success'
    },
    'x-reactions': {
      dependencies: ['status'],
      fulfill: {
        state: {
          'component[1].active': 2
        }
      }
    },
    'x-visible': false,
    properties: {
      step1: { // 注册须知
        type: 'void',
        'x-component': 'el-step',
        'x-component-props': {
          title: expression(`$t('vendorMod.registrationPolicy')`)
        }
      },
      step2: { // 注册账号
        type: 'void',
        'x-component': 'el-step',
        'x-component-props': {
          title: expression(`$t('vendorMod.registerAccount')`)
        }
      },
      step3: { // 选择注册类型
        type: 'void',
        'x-component': 'el-step',
        'x-component-props': {
          title: expression(`$t('vendorMod.registrationType')`)
        }
      },
      step4: { // 填写认证信息
        type: 'void',
        'x-component': 'el-step',
        'x-component-props': {
          title: expression(`$t('vendorMod.authenticationInformation')`)
        }
      },
      step5: { // 等待审批
        type: 'void',
        'x-component': 'el-step',
        'x-component-props': {
          title: expression(`$t('vendorMod.pendingApproval')`)
        }
      },
      step6: { // 完成企业信息认证
        type: 'void',
        'x-component': 'el-step',
        'x-component-props': {
          title: expression(`$t('vendorMod.informationAuthentication')`)
        }
      }
    }
  },
  formCompanyNature: {
    type: 'object',
    'x-decorator': 'FormLayout',
    'x-decorator-props': {
      layout: 'vertical',
      class: 'boxs-row'
    },
    'x-component': 'FormGrid',
    'x-component-props': {
      maxColumns: 1,
      columnGap: 32,
      rowGap: 0
    },
    'x-query-engine-skip': true,
    properties: {
      // 企业分类
      'overseasRelation': {
        type: 'string',
        default: 'INSIDE',
        'x-decorator': 'FormItem',
        'x-component': 'natureChose',
        'x-component-props': {
          style: 'margin:18px 0 15px 0;',
          '@change': expression(`(who) => {
            $self.value = who
          }`)
        },
        title: expression(`$t('vendorMod.overseasRelation')`),
        'x-validator': {
          required: true,
          message: i18nExpression('common.requiredField')
        }
      },
      // 企业性质
      'companyType': {
        type: 'string',
        'x-decorator': 'FormItem',
        'x-component': 'DictSelect',
        'x-component-props': {
          code: 'COMPANY_NATURE',
          class: 'companyType'
        },
        'x-reactions': expression(`field => {
            const overseasRelation = $self.query('.overseasRelation').take().value
            if (overseasRelation == 'INSIDE') {
              field.visible = true
            } else {
              field.visible = false
            }
        }`),
        title: expression(`$t('vendorMod.companyType')`),
        'x-validator': {
          required: true,
          message: i18nExpression('common.requiredField')
        }
      }
    }
  }
}
