import { expression, i18nExpression } from '@meicloud/render-engine'

import {
  requiredValidatorSegment,
  yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'

export const personBaseInfo = {
  beforeChange: {
    type: 'void',
    'x-component': 'div',
    'x-component-props': {
      class: 'formClassAllChange'
    },
    properties: {
      // 变更前
      beforeChangeTitle: {
        type: 'void',
        'x-component': 'changeTitle',
        'x-component-props': {
          language: 'supplierChange.beforeChange'
        }
      },
      personBaseInfoBefore: {
        type: 'object',
        'x-query-engine-skip': true,
        'x-component': 'FormGrid',
        'x-component-props': {
          class: 'forms'
        },
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
                fileId: expression(`$form.query('.personBaseInfoBefore').get('value').businessLicenseFileId`),
                fileName: expression(`$form.query('.personBaseInfoBefore').get('value').businessLicense`)
              },
              readonly: true
            }
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
                fileId: expression(`$form.query('.personBaseInfoBefore').get('value')?.extIdCardOppositeFileId`),
                fileName: expression(`$form.query('.personBaseInfoBefore').get('value').extIdCardOppositeFileName`)
              },
              'disabled': true
            }
          },
          companyName: {
            type: 'string',
            'x-decorator': 'FormItem',
            title: i18nExpression('cusEntry.vendorMod.companyNameOrPersonName'),
            'x-component-props': {
              'disabled': true
            },
            ...requiredValidatorSegment
          },
          companyShortName: {
            type: 'string',
            'x-decorator': 'FormItem',
            title: i18nExpression('cusEntry.vendorMod.personalAbbreviation'),
            'x-component-props': {
              'disabled': true
            },
            ...requiredValidatorSegment
          },
          businessLicense: {
            type: 'string',
            'x-hidden': 'true'
          },

          idNumber: {
            type: 'string',
            'x-decorator': 'FormItem',
            title: i18nExpression('cusEntry.vendorMod.idNo'),
            'x-component-props': {
              disabled: true
            }
          },
          validityPeriodOfCard: {
            ...yearMonthDaySelectorSegment,
            'x-decorator': 'FormItem',
            title: i18nExpression('cusEntry.vendorMod.validityPeriodOfCard'),
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              type: 'daterange',
              disabled: true
            }
          },
          lcCode: {
            type: 'string',
            title: i18nExpression('vendorMod.lcCode'),
            'x-decorator': 'FormItem',
            'x-component-props': {
              disabled: true
            }
          },
          enterpriseNo: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.enterpriseNo'),
            'x-decorator': 'FormItem',
            'x-component-props': {
              disabled: true
            }
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
          businessScope: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.mainBusinessScope'),
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
              placeholder: expression('$t(\'common.pleaseSelect\')'),
              'disabled': true
            },
            'x-reactions': expression(`() => {
              const data = $taxDictClass.getDictDetail('country', $self.value)
              $form.query('.personBaseInfoBefore.companyProvince').take()?.setComponentProps({ code: data ? data.description : undefined })
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
              'custom-select-type': 'PROVINCE',
              placeholder: expression('$t(\'common.pleaseSelect\')'),
              disabled: true
            },
            ...requiredValidatorSegment
          },
          // 城市
          companyCity: {
            type: 'string',
            title: i18nExpression('components.address.city'),
            'x-decorator': 'FormItem',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: expression('$form.query(\'.personBaseInfoBefore.companyProvince\').take()?.value'),
              'custom-select-type': 'CITY',
              placeholder: expression('$t(\'common.pleaseSelect\')'),
              disabled: true
            },
            ...requiredValidatorSegment
          },
          companyAddress: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.detailAddress'),
            'x-disabled': true,
            'x-decorator': 'FormItem',
            ...requiredValidatorSegment
          }
        }
      }
    }
  },
  afterChange: {
    type: 'void',
    'x-component': 'div',
    'x-component-props': {
      class: 'formClassAllChange'
    },
    properties: {
      // 变更后
      afterChangeTitle: {
        type: 'void',
        'x-component': 'changeTitle',
        'x-component-props': {
          language: 'supplierChange.afterChange'
        }
      },
      personBaseInfoAfter: {
        type: 'object',
        'x-query-engine-skip': true,
        'x-component': 'FormGrid',
        'x-component-props': {
          class: 'forms'
        },
        properties: {
          businessLicenseFileId: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.frontOfIdCard'),
            'x-decorator': 'FormItem',
            'x-component': 'SrmCommonFile',
            'x-component-props': {
              disabled: true,
              'extra-data': {
                uploadType: 'DEF',
                sourceType: 'WEB_APP',
                fileModular: 'sup',
                fileFunction: 'companyInfoMaintain',
                fileType: 'images'
              },
              'default-file': {
                fileId: expression(`$form.query('.personBaseInfoAfter').get('value').businessLicenseFileId`),
                fileName: expression(`$form.query('.personBaseInfoAfter').get('value').businessLicense`)
              },
              '@on-change': expression(`({file}) => {
                 const { fileId, fileName } = file || {}
                 $form.query($self.parent.address).get('value').businessLicenseFileId = fileId
                 $form.query($self.parent.address).get('value').businessLicense = fileName
              }`)
            },
            'x-reactions': expression(`() => {
              const newData = $form.query('.personBaseInfoAfter.businessLicenseFileId').take()?.value
              const oldData = $form.query('.personBaseInfoBefore.businessLicenseFileId').take()?.value
              let className = redFunction(oldData, newData)
              $self.setComponentProps({ class: className })
            }`)
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
              disabled: true,
              'extra-data': {
                uploadType: 'DEF',
                sourceType: 'WEB_APP',
                fileModular: 'sup',
                fileFunction: 'companyInfoMaintain',
                fileType: 'images'
              },
              'default-file': {
                fileId: expression(`$form.query('.personBaseInfoAfter').get('value')?.extIdCardOppositeFileId`),
                fileName: expression(`$form.query('.personBaseInfoAfter').get('value').extIdCardOppositeFileName`)
              },
              '@on-change': expression(`({file}) => {
                const { fileId, fileName } = file || {}
                $form.query('.personBaseInfoAfter').get('value').extIdCardOppositeFileId = fileId
                $form.query('.personBaseInfoAfter').get('value').extIdCardOppositeFileName = fileName
             }`)
            },
            'x-reactions': expression(`() => {
              const newData = $form.query('.personBaseInfoAfter.extIdCardOppositeFileId').take()?.value
              const oldData = $form.query('.personBaseInfoBefore.extIdCardOppositeFileId').take()?.value
              let className = redFunction(oldData, newData)
              $self.setComponentProps({ class: className })
            }`)
          },
          companyName: {
            type: 'string',
            'x-decorator': 'FormItem',
            title: i18nExpression('cusEntry.vendorMod.companyNameOrPersonName'),
            'x-reactions': expression(`() => {
              const newData = $form.query('.personBaseInfoAfter.companyName').take()?.value
              const oldData = $form.query('.personBaseInfoBefore.companyName').take()?.value
              let className = redFunction(oldData, newData)
              $self.setComponentProps({ class: className })
            }`),
            ...requiredValidatorSegment
          },
          companyShortName: {
            type: 'string',
            'x-decorator': 'FormItem',
            title: i18nExpression('cusEntry.vendorMod.personalAbbreviation'),
            'x-component-props': {
              'show-word-limit': true,
              'maxlength': 100
            },
            ...requiredValidatorSegment,
            'x-reactions': expression(`() => {
              const newData = $form.query('.personBaseInfoAfter.companyShortName').take()?.value
              const oldData = $form.query('.personBaseInfoBefore.companyShortName').take()?.value
              let className = redFunction(oldData, newData)
              $self.setComponentProps({ class: className })
            }`)
          },
          businessLicense: {
            type: 'string',
            'x-hidden': true
          },
          idNumber: {
            type: 'string',
            'x-decorator': 'FormItem',
            title: i18nExpression('cusEntry.vendorMod.idNo'),
            'x-component-props': {
              'disabled': true
            },
            'x-reactions': expression(`() => {
              const newData = $form.query('.personBaseInfoAfter.lcCode').take()?.value
              const oldData = $form.query('.personBaseInfoBefore.lcCode').take()?.value
              let className = redFunction(oldData, newData)
              $self.setComponentProps({ class: className })
            }`),
            ...requiredValidatorSegment
          },
          validityPeriodOfCard: {
            ...yearMonthDaySelectorSegment,
            'x-decorator': 'FormItem',
            title: i18nExpression('cusEntry.vendorMod.validityPeriodOfCard'),
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              type: 'daterange'
            },
            'x-reactions': expression(`() => {
              const newData = $form.query('.personBaseInfoAfter.validityPeriodOfCard').take()?.value
              const oldData = $form.query('.personBaseInfoBefore.validityPeriodOfCard').take()?.value
              let className = redFunction(oldData, newData)
              $self.setComponentProps({ class: className })
            }`)
          },
          lcCode: {
            type: 'string',
            title: i18nExpression('vendorMod.lcCode'),
            'x-decorator': 'FormItem',
            'x-component-props': {
              disabled: true,
              'x-reactions': expression(`() => {
                const newData = $form.query('.personBaseInfoAfter.lcCode').take()?.value
                const oldData = $form.query('.personBaseInfoBefore.lcCode').take()?.value
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
              }`)
            }
          },
          enterpriseNo: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.enterpriseNo'),
            'x-decorator': 'FormItem',
            'x-component-props': {
              disabled: true
            },
            'x-reactions': expression(`() => {
              const newData = $form.query('.personBaseInfoAfter.enterpriseNo').take()?.value
              const oldData = $form.query('.personBaseInfoBefore.enterpriseNo').take()?.value
              let className = redFunction(oldData, newData)
              $self.setComponentProps({ class: className })
            }`)
          },
          // KPP编码
          extKpp: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.extKpp'),
            'x-decorator': 'FormItem',
            'x-reactions': expression(`() => {
              const newData = $form.query('.personBaseInfoAfter.extKpp').take()?.value
              const oldData = $form.query('.personBaseInfoBefore.extKpp').take()?.value
              let className = redFunction(oldData, newData)
              $self.setComponentProps({ class: className })
            }`)
          },
          businessScope: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.mainBusinessScope'),
            'x-decorator': 'FormItem',
            'x-reactions': expression(`() => {
              const newData = $form.query('.personBaseInfoAfter.businessScope').take()?.value
              const oldData = $form.query('.personBaseInfoBefore.businessScope').take()?.value
              let className = redFunction(oldData, newData)
              $self.setComponentProps({ class: className })
            }`)
          },
          // 国家
          companyCountry: {
            type: 'string',
            title: i18nExpression('components.address.country'),
            'x-decorator': 'FormItem',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'country',
              placeholder: expression('$t(\'common.pleaseSelect\')'),
              '@change': expression(`(val) => {
                $form.query('.personBaseInfoAfter.companyProvince').take().value = ''
                $form.query('.personBaseInfoAfter.companyCity').take().value = ''
              }`)
            },
            'x-reactions': expression(`() => {
              const newData = $form.query('.personBaseInfoAfter.companyCountry').take()?.value
              const oldData = $form.query('.personBaseInfoBefore.companyCountry').take()?.value
              let className = redFunction(oldData, newData)
              $self.setComponentProps({ class: className })
              const data = $taxDictClass.getDictDetail('country', newData)
              $form.query('.personBaseInfoAfter.companyProvince').take()?.setComponentProps({ code: data ? data.description : undefined })
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
              'custom-select-type': 'PROVINCE',
              disabled: expression(`$form.readPretty || !['CN', 'RU'].includes($form.query('.personBaseInfoAfter.companyCountry').take().value)`),
              placeholder: expression('$t(\'common.pleaseSelect\')'),
              '@change': expression(`(val) => {
                $form.query('.personBaseInfoAfter.companyCity').take().value = ''
              }`)
            },
            'x-reactions': expression(`() => {
              const newData = $form.query('.personBaseInfoAfter.companyProvince').take()?.value
              const oldData = $form.query('.personBaseInfoBefore.companyProvince').take()?.value
              let className = redFunction(oldData, newData)
              $self.setComponentProps({ class: className })
            }`),
            ...requiredValidatorSegment
          },
          // 城市
          companyCity: {
            type: 'string',
            title: i18nExpression('components.address.city'),
            'x-decorator': 'FormItem',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: expression(`$form.query('.personBaseInfoAfter.companyProvince').take()?.value || ''`),
              'custom-select-type': 'CITY',
              emptyOptionCanCreat: true,
              disabled: expression(`$form.readPretty || !$form.query('.personBaseInfoAfter.companyProvince').take().value`),
              placeholder: expression('$t(\'common.pleaseSelect\')')
            },
            'x-reactions': expression(`() => {
              const newData = $form.query('.personBaseInfoAfter.companyCity').take()?.value
              const oldData = $form.query('.personBaseInfoBefore.companyCity').take()?.value
              let className = redFunction(oldData, newData)
              $self.setComponentProps({ class: className })
            }`),
            ...requiredValidatorSegment
          },
          companyAddress: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.detailAddress'),
            'x-decorator': 'FormItem',
            'x-reactions': expression(`() => {
              const newData = $form.query('.personBaseInfoAfter.companyAddress').take()?.value
              const oldData = $form.query('.personBaseInfoBefore.companyAddress').take()?.value
              let className = redFunction(oldData, newData)
              $self.setComponentProps({ class: className })
            }`),
            ...requiredValidatorSegment
          }
        }
      }
    }
  }
}
