import {
  expression, i18nExpression
} from '@meicloud/render-engine'
import { dataTimeSelectorSegment, yearMonthDaySelectorSegment } from 'lib@/components/render-engine'

export const companyInfo = {
  companyInfo: {
    type: 'void',
    'x-component': 'CollapseItem',
    'x-component-props': {
      title: i18nExpression('vendorMod.enterpriseThreeCertificates'),
      class: 'companyInfo'
    },
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
              // 统一社会信用代码
              lcCode: {
                type: 'string',
                'x-visible': expression('$form.query(\'.overseasRelation\').take().value == \'INSIDE\''),
                'x-decorator': 'FormItem',
                'x-component': 'Input',
                title: expression('$t(\'vendorMod.lcCode\')'),
                'x-component-props': {
                  'disabled': expression('$disabled')
                },
                'x-validator': {
                  required: expression('$form.query(\'.overseasRelation\').take().value == \'INSIDE\''),
                  message: i18nExpression('vendorMod.msgLcCode')
                }
              },
              // 营业执照号
              businessLicenseNo: {
                type: 'string',
                'x-visible': expression('$form.query(\'.overseasRelation\').take().value == \'OUT\''),
                'x-decorator': 'FormItem',
                'x-component': 'Input',
                title: expression('$t(\'vendorMod.lcCode2\')'),
                'x-component-props': {
                  'disabled': expression('$form.query(\'state\').get(\'data\').$disabled')
                }
              },
              // 币种
              registCurrency: {
                type: 'string',
                'x-hidden': true
              },
              // 注册资本(万)
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
                  return h(DictSelect, {
                    props: {
                      value: targetField.value,
                      code: 'currency'
                    },
                    attrs: {
                      disabled: 'true'
                    },
                    on: {
                      'change-value': (value) => {
                        // targetField.value = value
                      }
                    }
                  })
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
              // 成立日期
              companyCreationDate: {
                type: 'date',
                default: null,
                'x-decorator': 'FormItem',
                'x-component-props': {
                  placeholder: i18nExpression('common.pleaseSelectDate'),
                  format: 'yyyy-MM-dd',
                  'value-format': 'yyyy-MM-dd',
                  'disabled': expression('$disabled')
                },
                title: expression('$t(\'vendorMod.creationDate\')'),
                'x-validator': {
                  required: expression('![\'FEIYINGLI\'].includes($form.query(\'.companyType\').take().value)'),
                  message: i18nExpression('vendorMod.msgCreationDate')
                }
              },
              businessStartDate: {
                type: 'date',
                'x-hidden': true
              },
              businessEndDate: {
                type: 'date',
                'x-hidden': true
              },
              // 营业期限
              businessDate: {
                type: 'string',
                'x-component': 'DatePicker',
                'x-decorator': 'FormItem',
                'x-component-props': {
                  ...yearMonthDaySelectorSegment['x-component-props'],
                  type: 'daterange',
                  'disabled': expression('$disabled')
                },
                title: expression('$t(\'vendorMod.dateBusiness\')'),
                'x-validator': {
                  required: expression('![\'FEIYINGLI\'].includes($form.query(\'.companyType\').take().value)'),
                  message: i18nExpression('vendorMod.msgCreationDate')
                }
              },
              // 营业地址
              // address: {
              //   type: 'string',
              //   'x-decorator': 'FormItem',
              //   'x-component': 'newAddress',
              //   title: expression(`$t('vendorMod.businessAddress')`),
              //   'x-read-pretty': true,
              //   'x-component-props': {
              //     ref: 'address',
              //     style: 'width: 100%',
              //     '@change-value': expression(`(value) => {
              //   $form.query('.address').take().value = value
              // }`),
              //     'disabled': expression(`$disabled`)
              //   }
              // },
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
              // 登记机关
              registrationAuthority: {
                type: 'string',
                'x-decorator': 'FormItem',
                'x-component': 'Input',
                title: expression('$t(\'vendorMod.registrationAuthority\')'),
                'x-component-props': {
                  'disabled': expression('$disabled')
                }
              },
              // 营业范围
              businessScope: {
                type: 'string',
                'x-decorator': 'FormItem',
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
