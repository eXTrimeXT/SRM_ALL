import { 
  expression, 
  i18nExpression,
  generateCharExpressionByFunction
} from '@meicloud/render-engine'
import { requiredValidatorSegment } from 'lib@/components/render-engine/schema-segments'
export const personBaseInfo = {
  person: {
    type: 'void',
    'x-component': 'CollapseItem',
    'x-component-props': {
      title: i18nExpression('cusEntry.vendorMod.baseInfo'),
    },
    'x-visible': generateCharExpressionByFunction(({ $form }) => {
      return $form.query('state').get('data').overseasRelation == 'PERSONAL' && !$form.query('state').get('data').isSimple
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
          personBaseInfo: {
            type: 'object',
            'x-query-engine-skip': true,
            properties: {
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
                    fileId: expression(`$form.query('personBaseInfo').get('value')?.businessLicenseFileId`),
                    fileName: expression(`$form.query('personBaseInfo').get('value')?.businessLicense`)
                  },
                  '@on-change': expression(`({file}) => {
                     const { fileId = null, fileName = null } = file || {}
                     $form.query('personBaseInfo').get('value').businessLicenseFileId = fileId
                     $form.query('personBaseInfo').get('value').businessLicense = fileName
                  }`),
                  readonly: expression(`$form.query('state').get('data').$disabled`)
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
                    fileId: expression(`$form.query('personBaseInfo').get('value')?.extIdCardOppositeFileId`),
                    fileName: expression(`$form.query('personBaseInfo').get('value')?.extIdCardOppositeFileName`)
                  },
                  '@on-change': expression(`({file}) => {
                    const { fileId = null, fileName = null } = file || {}
                    $form.query('personBaseInfo').get('value').extIdCardOppositeFileId = fileId
                    $form.query('personBaseInfo').get('value').extIdCardOppositeFileName = fileName
                  }`),
                  readonly: expression(`$form.query('state').get('data').$disabled`)
                },
                ...requiredValidatorSegment
              },
              companyName: {
                type: 'string',
                'x-decorator': 'FormItem',
                title: i18nExpression('cusEntry.vendorMod.companyNameOrPersonName'),
                ...requiredValidatorSegment,
                'x-component-props': {
                  disabled: expression(`$form.query('state').get('data').$disabled`)
                }
              },
              companyShortName: {
                type: 'string',
                'x-decorator': 'FormItem',
                title: i18nExpression('cusEntry.vendorMod.personalAbbreviation'),
                ...requiredValidatorSegment,
                'x-component-props': {
                  disabled: expression(`$form.query('state').get('data').$disabled`),
                  maxlength: 100,
                 'show-word-limit': true
                }
              },
              businessLicense: {
                type: 'string',
                'x-hidden': true
              },
              idNumber: {
                type: 'string',
                'x-decorator': 'FormItem',
                title: i18nExpression('cusEntry.vendorMod.idNo'),
                ...requiredValidatorSegment,
                'x-component-props': {
                  disabled: expression(`$form.query('state').get('data').$disabled`)
                }
              },
              validityPeriodOfCard: {
                type: 'date',
                'x-decorator': 'FormItem',
                title: i18nExpression('cusEntry.vendorMod.validityPeriodOfCard'),
                'x-component-props': {
                  type: 'daterange',
                  disabled: expression(`$form.query('state').get('data').$disabled`)
                },
                ...requiredValidatorSegment
              },
              businessScope: {
                type: 'string',
                title: i18nExpression('cusEntry.vendorMod.mainBusinessScope'),
                'x-decorator': 'FormItem',
                  'x-component-props': {
                  disabled: expression(`$form.query('state').get('data').$disabled`)
                }
              },
              enterpriseNo: {
                type: 'string',
                title: i18nExpression('cusEntry.vendorMod.enterpriseNo'),
                'x-decorator': 'FormItem',
                'x-component-props': {
                  disabled: expression(`$form.query('state').get('data').$disabled`)
                },
                ...requiredValidatorSegment
              },
              // 国家
              companyCountry: {
                type: 'string',
                title: i18nExpression('components.address.country'),
                'x-decorator': 'FormItem',
                'x-component': 'DictSelect',
                'x-component-props': {
                  code: 'country',
                  disabled: expression(`$form.query('state').get('data').$disabled`),
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
              // 州
              companyProvince: {
                type: 'string',
                title: i18nExpression('components.address.area'),
                'x-decorator': 'FormItem',
                'x-component': 'DictSelect',
                'x-component-props': {
                  disabled: expression(`$form.query('state').get('data').$disabled`),
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
                  emptyOptionCanCreat: true,
                  placeholder: expression('$t(\'common.pleaseSelect\')'),
                  disabled: expression(`$form.query('state').get('data').$disabled || !$form.query('personBaseInfo.companyProvince').take()?.value`)
                },
                'x-visible': `{{ ['CN', 'RU'].includes($form.query('personBaseInfo.companyCountry').take().value) }}`,
                ...requiredValidatorSegment
              },
              companyAddress: {
                type: 'string',
                title: i18nExpression('cusEntry.vendorMod.detailAddress'),
                'x-decorator': 'FormItem',
                ...requiredValidatorSegment,
                'x-component-props': {
                  disabled: expression(`$form.query('state').get('data').$disabled`)
                }
              },
              // 岗位
              'position': {
                type: 'string',
                'x-decorator': 'FormItem',
                'x-component': 'Input',
                title: expression(`$t('components.orgPositionSel.position')`),
                'x-component-props': {
                  disabled: expression(`$form.query('state').get('data').$disabled`)
                }
              }
            }
          }
        }
      }
    }
  }
}
