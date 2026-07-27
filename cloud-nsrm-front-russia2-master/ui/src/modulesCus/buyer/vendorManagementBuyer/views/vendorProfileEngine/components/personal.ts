import { 
  expression, 
  i18nExpression,
  generateCharExpressionByFunction
} from '@meicloud/render-engine'

import {
  requiredValidatorSegment,
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'

export const personBaseInfo = {
  person: {
    type: 'void',
    'x-component': 'CollapseItem',
    'x-component-props': {
      title: `{{observer(
        {
          render(h) {
            return h($$components.Note, {
              props: {
                title: t('cusEntry.vendorMod.baseInfo'),
                value: $form.values.extRejectAttribute3,
                readonly: !($form.values.status === 'SUBMITTED' && $form.values.dataSources !== 'MANUALLY_CREATE' && $attrs.params.flag === 'edit')
              },
              on: {
                change: value => {
                  $form.values.extRejectAttribute3 = value
                }
              }
            })
          }
        }
      )}}`
    },
    'x-visible': generateCharExpressionByFunction(({ $form }) => {
      return $form.query('state').get('data').userType === 'PERSONAL'
    }),
    'x-query-engine-skip': true,
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
          personBaseInfo: {
            type: 'object',
            'x-query-engine-skip': true,
            properties: {
              businessLicense: {
                type: 'string',
                'x-hidden': true
              },
              businessLicenseFileId: {
                type: 'string',
                title: i18nExpression('cusEntry.vendorMod.frontOfIdCard'),
                'x-decorator': 'FormItem',
                'x-component': 'SrmCommonFile',
                'x-component-props': {
                  'extra-data': {
                    uploadType: 'DEF',
                    sourceType: 'WEB_APP',
                    fileModular: 'sup',
                    fileFunction: 'companyInfoMaintain',
                    fileType: 'images'
                  },
                  'default-file': {
                    fileId: expression(`$form.query('personBaseInfo').get('value').businessLicenseFileId`),
                    fileName: expression(`$form.query('personBaseInfo').get('value').businessLicense`)
                  },
                  readonly: true
                },
                ...requiredValidatorSegment
              },
              extIdCardOppositeFileName: {
                type: 'string',
                'x-hidden': true
              },
              extIdCardOppositeFileId: {
                type: 'string',
                title: i18nExpression('cusEntry.vendorMod.backOfIdCard'),
                'x-decorator': 'FormItem',
                'x-component': 'SrmCommonFile',
                'x-component-props': {
                  'extra-data': {
                    uploadType: 'DEF',
                    sourceType: 'WEB_APP',
                    fileModular: 'sup',
                    fileFunction: 'companyInfoMaintain',
                    fileType: 'images'
                  },
                  'default-file': {
                    fileId: expression(`$form.query('personBaseInfo').get('value').extIdCardOppositeFileId`),
                    fileName: expression(`$form.query('personBaseInfo').get('value').extIdCardOppositeFileName`)
                  },
                  readonly: true
                },
                ...requiredValidatorSegment
              },
              companyName: {
                type: 'string',
                'x-decorator': 'FormItem',
                'x-component-props': {
                  disabled: true
                },
                title: i18nExpression('cusEntry.vendorMod.companyNameOrPersonName'),
                ...requiredValidatorSegment
              },
              companyShortName: {
                type: 'string',
                'x-decorator': 'FormItem',
                'x-component-props': {
                  disabled: true
                },
                title: i18nExpression('cusEntry.vendorMod.personalAbbreviation'),
                ...requiredValidatorSegment
              },
              idNumber: {
                type: 'string',
                'x-decorator': 'FormItem',
                'x-component-props': {
                  disabled: true
                },
                title: i18nExpression('cusEntry.vendorMod.idNo'),
                ...requiredValidatorSegment
              },
              validityPeriodOfCard: {
                ...yearMonthDaySelectorSegment,
                'x-decorator': 'FormItem',
                title: i18nExpression('cusEntry.vendorMod.validityPeriodOfCard'),
                'x-component-props': {
                  ...yearMonthDaySelectorSegment['x-component-props'],
                  type: 'daterange',
                  disabled: true
                },
                ...requiredValidatorSegment
              },
              businessScope: {
                type: 'string',
                title: i18nExpression('cusEntry.vendorMod.mainBusinessScope'),
                'x-decorator': 'FormItem',
                'x-component-props': {
                  disabled: true
                }
              },
              lcCode: {
                type: 'string',
                title: i18nExpression('vendorMod.lcCode'),
                'x-decorator': 'FormItem',
                'x-component-props': {
                  disabled: true
                },
                ...requiredValidatorSegment
              },
              enterpriseNo: {
                type: 'string',
                title: i18nExpression('cusEntry.vendorMod.enterpriseNo'),
                'x-decorator': 'FormItem',
                'x-component-props': {
                  disabled: true
                },
                ...requiredValidatorSegment
              },
              // KPP编码
              extKpp: {
                type: 'string',
                title: i18nExpression('cusEntry.vendorMod.extKpp'),
                'x-decorator': 'FormItem',
                'x-component-props': {
                  disabled: true
                }
              },
              // 国家
              companyCountry: {
                type: 'string',
                title: i18nExpression('components.address.country'),
                'x-decorator': 'FormItem',
                'x-component': 'DictSelect',
                'x-component-props': {
                  code: 'country',
                  disabled: true,
                  placeholder: expression('$t(\'common.pleaseSelect\')'),
                  '@change': expression(`(val) => {
                    $form.query('personBaseInfo.companyProvince').take().value = ''
                    $form.query('personBaseInfo.companyCity').take().value = ''
                  }`)
                },
                'x-reactions': expression(`() => {
                  const data = $taxDictClass.getDictDetail('country', $self.value)
                  $form.query('personBaseInfo.companyProvince').take()?.setComponentProps({ code: data ? data.description : undefined })
                }`),
                ...requiredValidatorSegment
              },
              // 省
              companyProvince: {
                type: 'string',
                title: i18nExpression('components.address.area'),
                'x-decorator': 'FormItem',
                'x-component': 'DictSelect',
                'x-component-props': {
                  disabled: true,
                  'custom-select-type': 'PROVINCE',
                  placeholder: expression('$t(\'common.pleaseSelect\')'),
                  '@change': expression(`(val) => {
                    $form.query('personBaseInfo.companyCity').take().value = ''
                  }`)
                },
                'x-visible': `{{ ['CN', 'RU'].includes($form.query('personBaseInfo.companyCountry').take().value) }}`,
                ...requiredValidatorSegment
              },
              // 城市
              companyCity: {
                type: 'string',
                title: i18nExpression('components.address.city'),
                'x-decorator': 'FormItem',
                'x-component': 'DictSelect',
                'x-component-props': {
                  code: expression(`$form.query('personBaseInfo.companyProvince').take()?.value || ''`),
                  'custom-select-type': 'CITY',
                  placeholder: expression('$t(\'common.pleaseSelect\')'),
                  disabled: true
                },
                'x-visible': `{{ ['CN', 'RU'].includes($form.query('personBaseInfo.companyCountry').take().value) }}`,
                ...requiredValidatorSegment
              },
              companyAddress: {
                type: 'string',
                title: i18nExpression('cusEntry.vendorMod.detailAddress'),
                'x-decorator': 'FormItem',
                'x-component-props': {
                  disabled: true
                },
                ...requiredValidatorSegment
              }
            }
          }
        }
      }
    }
  }
}
