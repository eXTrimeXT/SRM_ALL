import {
  expression,
  i18nExpression,
  generateCharExpressionByFunction
} from '@meicloud/render-engine'
import { yearMonthDaySelectorSegment } from 'lib@/components/render-engine'

export const companyInfo = {
  companyInfo: {
    type: 'void',
    'x-component': 'CollapseItem',
    'x-component-props': {
      title: `{{observer(
        {
          render(h) {
            return h($$components.Note, {
              props: {
                title: t('vendorMod.enterpriseThreeCertificates'),
                value: $form.values.extRejectAttribute2,
                readonly: !($form.values.status === 'SUBMITTED' && $form.values.dataSources !== 'MANUALLY_CREATE' && $attrs.params.flag === 'edit')
              },
              on: {
                change: value => {
                  $form.values.extRejectAttribute2 = value
                }
              }
            })
          }
        }
      )}}`
    },
    'x-visible': generateCharExpressionByFunction(({ $form }) => {
      return $form.query('state').get('data').userType !== 'PERSONAL'
    }),
    'x-query-engine-skip': true,
    properties: {
      div: {
        type: 'void',
        'x-component': 'div',
        'x-component-props': {
          class: 'companyInfo'
        },
        properties: {
          // 营业执照上传
          businessLicense: {
            type: 'string',
            'x-hidden': true
          },
          'businessLicenseFileId': {
            type: 'string',
            'x-component': 'SrmCommonFile',
            'x-component-props': {
              'readonly': expression('$disabled'),
              'list-type': 'picture-card',
              style: {
                'width': '33%',
                'padding-right': '25px'
              },
              'defaultFile': {
                fileId: expression('$self.value'),
                fileName: expression('$form.query(\'businessLicense\').get(\'value\')')
              },
              'dragger-options': {
                width: '100%',
                height: '345px'
              },
              'limit': 1,
              'drag': 'drag',
              '@on-change': expression(`({ file }) => {

          }`)
            },
            title: ''
          },
          layout: {
            type: 'void',
            'x-decorator': 'FormLayout',
            'x-decorator-props': {
              layout: 'vertical',
              style: {
                'width': '67%',
                'padding-left': '20px'
              }
            },
            'x-component': 'FormGrid',
            'x-component-props': {
              maxColumns: 2,
              columnGap: 32,
              rowGap: 0
            },
            properties: {
              // 企业名称
              'companyName': {
                type: 'string',
                'x-decorator': 'FormItem',
                'x-component': 'Input',
                title: expression('$t(\'vendorMod.companyName\')'),
                'x-component-props': {
                  'disabled': expression('$disabled')
                },
                'x-validator': {
                  required: true,
                  message: i18nExpression('vendorMod.msgCompanyName')
                }
              },
              // 企业简称
              companyShortName: {
                type: 'string',
                'x-decorator': 'FormItem',
                'x-component': 'Input',
                title: expression('$t(\'vendorMod.companyShortName\')'),
                'x-component-props': {
                  'disabled': expression('$disabled')
                }
              },
              companyEnName: {
                type: 'string',
                'x-decorator': 'FormItem',
                'x-component': 'Input',
                title: expression(`$t('cusEntry.vendorMod.companyEnName')`),
                'x-component-props': {
                  disabled: expression('$disabled')
                }
              },
              // 纳税人识别号
              lcCode: {
                type: 'string',
                'x-decorator': 'FormItem',
                'x-component': 'Input',
                title: expression('$t(\'vendorMod.lcCode\')'),
                'x-component-props': {
                  'disabled': expression('$disabled')
                },
                'x-validator': {
                  required: true,
                  message: i18nExpression('vendorMod.msgLcCode')
                }
              },
              enterpriseNo: {
                type: 'string',
                title: i18nExpression('cusEntry.vendorMod.enterpriseNo'),
                'x-decorator': 'FormItem',
                'x-component-props': {
                  disabled: expression('$disabled')
                },
                'x-validator': {
                  required: true,
                  message: i18nExpression('vendorMod.msgLegalPerson')
                }
              },
              // KPP编码
              extKpp: {
                type: 'string',
                title: i18nExpression('cusEntry.vendorMod.extKpp'),
                'x-decorator': 'FormItem',
                'x-component-props': {
                  disabled: expression('$disabled')
                }
              },
              // 法人代表
              legalPerson: {
                type: 'string',
                'x-decorator': 'FormItem',
                'x-component': 'Input',
                title: expression('$t(\'vendorMod.legalPerson\')'),
                'x-component-props': {
                  'disabled': expression('$disabled')
                },
                'x-validator': {
                  required: true,
                  message: i18nExpression('vendorMod.msgLegalPerson')
                }
              },
              // 注册资本(卢布)
              registeredCapital: {
                type: 'number',
                'x-visible': expression('$form.query(\'.companyType\').take().value != \'GETI\''),
                'x-decorator': 'FormItem',
                'x-component': 'Input',
                title: expression('$t(\'vendorMod.registeredCapital\')'),
                'x-component-props': {
                  'disabled': expression('$disabled'),
                  class: 'input-with-select',
                  style: 'pointer-events:none'
                },
                'x-content': {
                  append: expression(`observer(
                    {
                      render(h) {
                        const targetField = $self.query('.registCurrency').take()
                        return h("div", {class: "bzBox"}, [
                          h("label", {class: "bzTitle"}, $t('vendorMod.currencyCode')),
                            h(DictSelect, {
                              props: {
                                value: targetField.value,
                                code: 'currency',
                              },
                              attrs: {
                                disabled: true,
                              },
                              on: {
                                'change-value': (value) => {
                                  // targetField.value = value
                                }
                              }
                            }),
                          ])
                        }
                      }
                    )
                  `)
                },
                'x-validator': {
                  required: expression('![\'GETI\',\'FEIYINGLI\'].includes($form.query(\'.companyType\').take().value)'),
                  message: i18nExpression('vendorMod.msgRegisteredCapital')
                }
              },
              // 币种
              registCurrency: {
                type: 'string',
                'x-hidden': true
              },
              ifLongPeriod: {
                type: 'string',
                title: i18nExpression('cusEntry.vendorMod.ifLongTermSupplier'), // 是否长期供应商
                'x-disabled': true,
                'x-component': 'DictSelect',
                'x-component-props': {
                  code: 'YES_OR_NO'
                },
                'x-decorator': 'FormItem',
                'x-validator': {
                  required: true,
                  message: i18nExpression('cusEntry.tipMessage.ifLongPeriodMsg')
                }
              },
              businessEndDate: {
                type: 'date',
                'x-hidden': true
              },
              // 营业期限
              businessStartDate: {
                ...yearMonthDaySelectorSegment,
                'x-decorator': 'FormItem',
                'x-component-props': {
                  ...yearMonthDaySelectorSegment['x-component-props'],
                  disabled: expression('$disabled')
                },
                title: expression('$t(\'vendorMod.dateBusiness\')'),
                'x-validator': {
                  required: expression(`$form.query('ifLongPeriod').take().value === 'N'`),
                  message: i18nExpression('vendorMod.msgCreationDate')
                }
              },
              // 成立日期
              companyCreationDate: {
                ...yearMonthDaySelectorSegment,
                'x-decorator': 'FormItem',
                'x-component-props': {
                  ...yearMonthDaySelectorSegment['x-component-props'],
                  'disabled': expression('$disabled')
                },
                title: expression('$t(\'vendorMod.creationDate\')'),
                'x-validator': {
                  required: expression('![\'FEIYINGLI\'].includes($form.query(\'.companyType\').take().value)'),
                  message: i18nExpression('vendorMod.msgCreationDate')
                }
              },
              // 营业范围
              businessScope: {
                type: 'string',
                'x-decorator': 'FormItem',
                'x-decorator-props': {
                  gridSpan: 2
                },
                'x-component': 'Input',
                title: expression('$t(\'vendorMod.businessScope\')'),
                'x-component-props': {
                  'disabled': expression('$disabled'),
                  type: 'textarea'
                }
              }
            }
          }
        }
      }

    }
  }
}
