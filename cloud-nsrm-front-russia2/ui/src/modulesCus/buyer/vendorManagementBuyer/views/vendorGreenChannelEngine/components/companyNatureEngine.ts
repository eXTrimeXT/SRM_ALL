import {
  expression,
  i18nExpression,
  generateCharExpressionByFunction
} from "@meicloud/render-engine"
export const companyNatureEngine = {
  companyNature: {
    type: 'void',
    'x-decorator': 'FormContainer',
    'x-decorator-props': {
      class: 'companyNature'
    },
    'x-visible': generateCharExpressionByFunction(({ $form }) => {
      return $form.query('state').get('data').activeStep === 'companyNature'
    }),
    items: {
      type: 'object',
      properties: {
        submit: {
          type: 'void',
          'x-content': i18nExpression('common.nextOne'),
          'x-component': 'Button',
          'x-component-props': {
            '@click': expression(`async (values) => {
              $form.validate('CompanyInfo.companyNature.formCompanyNature.overseasRelation').then(e => {
                $form.query('state').get('data').overseasRelation = $form.values.overseasRelation
                $form.query('state').get('data').activeStep = 'main'
                initButtonConfig($form)
                $initQualificationInfo($form)
                setTimeout(() => {
                  $addScrollEvent($form)
                }, 1000)
              })
            }`)
          }
        }
      }
    },
    properties: {
      formCompanyNature: {
        type: 'void',
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
          overseasRelation: {
            'x-hidden': generateCharExpressionByFunction(({ $form }) => {
              return $form.query('state').get('data').activeStep !== 'companyNature'
            }),
            type: 'string',
            default: 'INSIDE',
            'x-decorator': 'FormItem',
            'x-component': 'natureChose',
            'x-component-props': {
              style: 'margin:18px 0 15px 0;',
              '@change': expression(`(who) => {
                setTimeout(() => {
                  if (!$attrs.params.companyId) {
                    $form.values.contactInfos = who !== 'PERSONAL' ? [
                      {
                        position: 'SALES_MANAGER'
                      },
                      {
                        position: 'SENIOR_LEADER'
                      }
                    ] : [{ position: 'SALES_MANAGER' }]
                  }
                })
                $self.value = who
              }`)
            },
            title: i18nExpression('vendorMod.overseasRelation'),
            'x-validator': {
              required: true,
              message: i18nExpression('common.requiredField')
            }
          }
        }
      }
    }
  }
}

