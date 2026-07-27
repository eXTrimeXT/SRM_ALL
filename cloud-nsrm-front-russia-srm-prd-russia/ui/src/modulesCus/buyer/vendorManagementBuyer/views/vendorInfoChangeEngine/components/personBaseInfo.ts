import { expression, i18nExpression } from '@meicloud/render-engine'

import {
  requiredValidatorSegment
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
                fileId: expression(`$form.query('.personBaseInfoBefore').get('value')?.extIdCardOppositeFileId`),
                fileName: expression(`$form.query('.personBaseInfoBefore').get('value').extIdCardOppositeFileName`)
              },
              'disabled': true
            },
            ...requiredValidatorSegment
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
            },
            ...requiredValidatorSegment
          },
          validityPeriodOfCard: {
            type: 'date',
            'x-decorator': 'FormItem',
            title: i18nExpression('cusEntry.vendorMod.validityPeriodOfCard'),
            'x-component-props': {
              type: 'daterange',
              disabled: true
            },
            ...requiredValidatorSegment
          },
          extSex: {
            type: 'string',
            'x-decorator': 'FormItem',
            title: i18nExpression('cusEntry.vendorMod.sex'),
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'GENDER',
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
              placeholder: expression('$t(\'common.pleaseSelect\')'),
              'disabled': true
            },
            ...requiredValidatorSegment
          },
          // 省
          companyProvince: {
            type: 'string',
            title: i18nExpression('components.address.area'),
            'x-decorator': 'FormItem',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'PROVINCE',
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
            }`),
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
                fileId: expression(`$form.query('.personBaseInfoAfter').get('value')?.extIdCardOppositeFileId`),
                fileName: expression(`$form.query('.personBaseInfoAfter').get('value').extIdCardOppositeFileName`)
              },
              '@on-change': expression(`({file}) => {
                const { fileId, fileName } = file || {}
                $form.query('.personBaseInfoAfter').get('value').extIdCardOppositeFileId = fileId
                $form.query('.personBaseInfoAfter').get('value').extIdCardOppositeFileName = fileName
                if (file) {
                  // 读取图片信息
                  app.$http({
                    url: '/api-pj/ocr/recognizeIDCardBack',
                    method: 'GET',
                    params: { fileuploadId: fileId },
                    loading: true
                  }).then(res => {
                    const {
                      businessEndDate,
                      businessStartDate
                    } = res.data
                    $form.query('personBaseInfoAfter').get('value').validityPeriodOfCard = [businessStartDate, businessEndDate]
                  })
                }
             }`)
            },
            'x-reactions': expression(`() => {
              const newData = $form.query('.personBaseInfoAfter.extIdCardOppositeFileId').take()?.value
              const oldData = $form.query('.personBaseInfoBefore.extIdCardOppositeFileId').take()?.value
              let className = redFunction(oldData, newData)
              $self.setComponentProps({ class: className })
            }`),
            ...requiredValidatorSegment
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
            type: 'date',
            'x-decorator': 'FormItem',
            title: i18nExpression('cusEntry.vendorMod.validityPeriodOfCard'),
            'x-component-props': {
              type: 'daterange'
            },
            'x-reactions': expression(`() => {
              const newData = $form.query('.personBaseInfoAfter.validityPeriodOfCard').take()?.value
              const oldData = $form.query('.personBaseInfoBefore.validityPeriodOfCard').take()?.value
              let className = redFunction(oldData, newData)
              $self.setComponentProps({ class: className })
            }`),
            ...requiredValidatorSegment
          },
          extSex: {
            type: 'string',
            'x-decorator': 'FormItem',
            title: i18nExpression('cusEntry.vendorMod.sex'),
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'GENDER'
            },
            'x-reactions': expression(`() => {
              const newData = $form.query('.personBaseInfoAfter.extSex').take()?.value
              const oldData = $form.query('.personBaseInfoBefore.extSex').take()?.value
              let className = redFunction(oldData, newData)
              $self.setComponentProps({ class: className })
            }`),
            ...requiredValidatorSegment
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
            }`),
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
              placeholder: expression('$t(\'common.pleaseSelect\')'),
              '@change': expression(`(val) => {
                // 选择国外就清理省市区，并且禁用
                if ($form.query('.personBaseInfoAfter.companyCountry').take().value !== 'CN') {
                  $form.query('.personBaseInfoAfter.companyProvince').take().value = ''
                  $form.query('.personBaseInfoAfter.companyCity').take().value = ''
                }
              }`)
            },
            'x-reactions': expression(`() => {
              const newData = $form.query('.personBaseInfoAfter.companyCountry').take()?.value
              const oldData = $form.query('.personBaseInfoBefore.companyCountry').take()?.value
              let className = redFunction(oldData, newData)
              $self.setComponentProps({ class: className })
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
              code: 'PROVINCE',
              'custom-select-type': 'PROVINCE',
              placeholder: expression('$t(\'common.pleaseSelect\')')
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
              code: expression('$form.query(\'.personBaseInfoAfter.companyProvince\').take()?.value'),
              'custom-select-type': 'CITY',
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
