import {expression, i18nExpression, methodExpression} from '@meicloud/render-engine'

import { requiredValidatorSegment, yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'

export const enterpriseThreeCertificates = {
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
      enterpriseThreeCertificatesBefore: {
        type: 'object',
        'x-query-engine-skip': true,
        'x-component': 'FormGrid',
        'x-component-props': {
          class: 'forms'
        },
        properties: {
          businessLicenseFileId: {
            type: 'string',
            'x-hidden': true
          },
          businessLicense: {
            type: 'string',
            'x-hidden': true
          },
          businessLicenseFile: {
            type: 'string',
            title: i18nExpression('vendorMod.businessLicense'), // 营业执照
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
                fileId: expression(`$form.query('.enterpriseThreeCertificatesBefore.businessLicenseFileId').take()?.value`),
                fileName: expression(`$form.query('.enterpriseThreeCertificatesBefore.businessLicense').take()?.value`)
              }
            },
            'x-decorator': 'FormItem',
            ...requiredValidatorSegment
          },
          companyName: {
            type: 'string',
            title: i18nExpression('vendorMod.companyName'), // 企业名称
            'x-component-props': {
              disabled: true
            },
            'x-decorator': 'FormItem',
            'x-validator': {
              required: expression(`$form.query('.companyTypeBefore.overseasRelation').take()?.value == 'INSIDE'`),
              message: i18nExpression('common.requiredField')
            }
          },
          // 法人代表
          legalPerson: {
            type: 'string',
            title: i18nExpression('vendorMod.legalPerson'),
            'x-component-props': {
              disabled: true
            },
            'x-decorator': 'FormItem'
          },
          // 营业执照号
          businessLicenseNo: {
            type: 'string',
            'x-visible': expression(`$form.query('.companyTypeBefore.overseasRelation').take()?.value == 'OUT'`),
            'x-decorator': 'FormItem',
            'x-component': 'Input',
            title: expression(`$t('vendorMod.lcCode2')`),
            'x-component-props': {
              'disabled': true
            }
          },
          // 币种
          registCurrency: {
            type: 'string',
            'x-hidden': true,
            'x-decorator': 'FormItem'
          },
          // 注册资本(万)
          registeredCapital: {
            type: 'number',
            'x-visible': expression(`$form.query('.companyTypeAfter.companyType').take()?.value != 'GETI'`),
            'x-decorator': 'FormItem',
            'x-component': 'Input',
            title: expression(`$t('vendorMod.registeredCapital')`),
            'x-component-props': {
              'disabled': true,
              class:"input-with-select"
            },
            'x-content': {
              append: expression(`observer({
                render: (h) => {
                  const targetField = $form.query('.enterpriseThreeCertificatesBefore.registCurrency').take()
                  return h(
                    $self.readPretty ? $$components.DictSelectPreview : $$components.DictSelect,
                    {
                      [$self.readPretty ? 'props' : 'attrs']: {
                        code: 'currency',
                        value: targetField?.value,
                        disabled: true
                      },
                      style: { 'padding-left': $self.readPretty ? '5px' : 0 },
                      on: {
                        'change': (v) => {
                          targetField.value = v
                        }
                      }
                    }
                  )
                }
              })`)
            },
            'x-validator': {
              required: expression(`!['GETI','FEIYINGLI'].includes($form.query('.companyTypeAfter.companyType').take()?.value)`),
              message: i18nExpression('vendorMod.msgRegisteredCapital')
            }
          },
          companyCreationDate: {
            ...yearMonthDaySelectorSegment,
            title: i18nExpression('vendorMod.creationDate'), // 成立日期
            'x-disabled': true,
            'x-decorator': 'FormItem'
          },
          companyShortName: {
            type: 'string',
            title: i18nExpression('vendorMod.companyShortName'), // 企业简称
            'x-disabled': true,
            'x-decorator': 'FormItem'
          },
          lcCode: {
            type: 'string',
            'x-visible': expression(`$form.query('.companyTypeBefore.overseasRelation').take()?.value == 'INSIDE' ? true : false`),
            title: i18nExpression('vendorMod.lcCode'), // 统一社会信用代码
            'x-disabled': true,
            'x-decorator': 'FormItem'
          },
          registrationAuthority: {
            type: 'string',
            title: i18nExpression('vendorMod.registrationAuthority'), // 登记机关
            'x-disabled': true,
            'x-decorator': 'FormItem'
          },
          businessStartDate: {
            ...yearMonthDaySelectorSegment,
            'x-hidden': expression(`$form.query('.companyTypeAfter.companyType').take()?.value == 'GETI'`),
            title: i18nExpression('vendorMod.businessStartFrom'), // 营业日期从
            'x-disabled': true,
            'x-decorator': 'FormItem'
          },
          businessEndDate: {
            ...yearMonthDaySelectorSegment,
            'x-hidden': expression(`$form.query('.companyTypeAfter.companyType').take()?.value == 'GETI'`),
            title: i18nExpression('common.pleaseSelectDate'), // 营业日期至
            'x-disabled': true,
            'x-decorator': 'FormItem'
          },
          businessScope: {
            type: 'string',
            title: i18nExpression('vendorMod.businessScope'),
            'x-disabled': true,
            'x-decorator': 'FormItem'
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
      enterpriseThreeCertificatesAfter: {
        type: 'object',
        'x-query-engine-skip': true,
        'x-component': 'FormGrid',
        'x-component-props': {
          class: 'forms'
        },
        properties: {
          businessLicenseFileId: {
            type: 'string',
            'x-hidden': true
          },
          businessLicense: {
            type: 'string',
            'x-hidden': true
          },
          businessLicenseFile: {
            type: 'string',
            title: i18nExpression('vendorMod.businessLicense'), // 营业执照
            'x-component': 'SrmCommonFile',
            'x-component-props': {
              disabled: expression(`$form.readPretty`),
              'extra-data': {
                uploadType: 'DEF',
                sourceType: 'WEB_APP',
                fileModular: 'sup',
                fileFunction: 'companyInfoMaintain',
                fileType: 'images'
              },
              'default-file': {
                fileId: expression(`$form.query('.enterpriseThreeCertificatesAfter.businessLicenseFileId').take()?.value`),
                fileName: expression(`$form.query('.enterpriseThreeCertificatesAfter.businessLicense').take()?.value`)
              },
              '@on-change': expression(`(file) => {
                console.log(file)
                if (file) {
                  const { fileId, fileName } = file.file || {}
                  $form.query('.enterpriseThreeCertificatesAfter.businessLicenseFileId').take().value = fileId.toString()
                  $form.query('.enterpriseThreeCertificatesAfter.businessLicense').take().value = fileName
                } else {
                  $form.query('.enterpriseThreeCertificatesAfter.businessLicenseFileId').take().value = null
                  $form.query('.enterpriseThreeCertificatesAfter.businessLicense').take().value = null
                }
              }`)
            },
            'x-decorator': 'FormItem',
            ...requiredValidatorSegment
          },
          companyName: {
            type: 'string',
            title: i18nExpression('vendorMod.companyName'), // 企业名称
            'x-component-props': {
              disabled: expression(`$form.readPretty`)
            },
            'x-reactions': expression(`() => {
                const newData = $form.query('.enterpriseThreeCertificatesAfter.companyName').take()?.value
                const oldData = $form.query('.enterpriseThreeCertificatesBefore.companyName').take()?.value
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),
            'x-decorator': 'FormItem',
            'x-validator': {
              required: expression(`$form.query('.companyTypeAfter.overseasRelation').take()?.value == 'INSIDE'`),
              message: i18nExpression('common.requiredField')
            }
          },
          // 法人代表
          legalPerson: {
            type: 'string',
            title: i18nExpression('vendorMod.legalPerson'),
            'x-component-props': {
              disabled: expression(`$form.readPretty`)
            },
            'x-reactions': expression(`() => {
                const newData = $form.query('.enterpriseThreeCertificatesAfter.legalPerson').take()?.value
                const oldData = $form.query('.enterpriseThreeCertificatesBefore.legalPerson').take()?.value
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),
            'x-decorator': 'FormItem'
          },
          // 营业执照号
          businessLicenseNo: {
            type: 'string',
            'x-visible': expression(`$form.query('.companyTypeAfter.overseasRelation').take()?.value == 'OUT'`),
            'x-decorator': 'FormItem',
            'x-component': 'Input',
            title: expression(`$t('vendorMod.lcCode2')`),
            'x-component-props': {
              'disabled': expression(`$form.readPretty`)
            },
            'x-reactions': expression(`() => {
                const newData = $form.query('.enterpriseThreeCertificatesAfter.businessLicenseNo').take().value
                const oldData = $form.query('.enterpriseThreeCertificatesBefore.businessLicenseNo').take().value
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`)
          },
          // 币种
          registCurrency: {
            type: 'string',
            'x-hidden': true
          },
          // 注册资本(万)
          registeredCapital: {
            type: 'number',
            // 'x-visible': expression(`$form.query('.companyTypeAfter.companyType').take().value != 'GETI'`),
            'x-decorator': 'FormItem',
            'x-component': 'Input',
            title: expression(`$t('vendorMod.registeredCapital')`),
            'x-component-props': {
              'disabled': expression(`$form.readPretty`),
              class: "input-with-select"
            },
            'x-reactions': expression(`() => {
                const newData = $form.query('.enterpriseThreeCertificatesAfter.registeredCapital').take()?.value
                const oldData = $form.query('.enterpriseThreeCertificatesBefore.registeredCapital').take()?.value
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),
            'x-content': {
              append: expression(`observer({
                render: (h) => {
                  const targetField = $form.query('.enterpriseThreeCertificatesAfter.registCurrency').take()
                  return h(
                    $self.readPretty ? $$components.DictSelectPreview : $$components.DictSelect,
                    {
                      [$self.readPretty ? 'props' : 'attrs']: {
                        code: 'currency',
                        value: targetField.value,
                      },
                      style: { 'padding-left': $self.readPretty ? '5px' : 0 },
                      on: {
                        'change': (v) => {
                          targetField.value = v
                        }
                      }
                    }
                  )
                }
              })`)
            },
            'x-validator': {
              required: expression(`!['GETI','FEIYINGLI'].includes($form.query('.companyTypeAfter.companyType').take().value)`),
              message: i18nExpression('vendorMod.msgRegisteredCapital')
            }
          },
          companyCreationDate: {
            ...yearMonthDaySelectorSegment,
            title: i18nExpression('vendorMod.creationDate'), // 成立日期
            'x-disabled': expression(`$form.readPretty`),
            'x-reactions': expression(`() => {
                const newData = $form.query('.enterpriseThreeCertificatesAfter.companyCreationDate').take().value
                const oldData = $form.query('.enterpriseThreeCertificatesBefore.companyCreationDate').take().value
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),
            'x-decorator': 'FormItem'
          },
          companyShortName: {
            type: 'string',
            title: i18nExpression('vendorMod.companyShortName'), // 企业简称
            'x-disabled': expression(`$form.readPretty`),
            'x-reactions': expression(`() => {
                const newData = $form.query('.enterpriseThreeCertificatesAfter.companyShortName').take().value
                const oldData = $form.query('.enterpriseThreeCertificatesBefore.companyShortName').take().value
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),
            'x-decorator': 'FormItem'
          },
          lcCode: {
            type: 'string',
            'x-visible': expression(`$form.query('.companyTypeAfter.overseasRelation').take().value == 'INSIDE' ? true : false`),
            title: i18nExpression('vendorMod.lcCode'), // 统一社会信用代码
            'x-reactions': expression(`() => {
                const newData = $form.query('.enterpriseThreeCertificatesAfter.lcCode').take().value
                const oldData = $form.query('.enterpriseThreeCertificatesBefore.lcCode').take().value
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),
            'x-disabled': expression(`$form.readPretty`),
            'x-decorator': 'FormItem'
          },
          registrationAuthority: {
            type: 'string',
            title: i18nExpression('vendorMod.registrationAuthority'), // 登记机关
            'x-reactions': expression(`() => {
                const newData = $form.query('.enterpriseThreeCertificatesAfter.registrationAuthority').take().value
                const oldData = $form.query('.enterpriseThreeCertificatesBefore.registrationAuthority').take().value
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),
            'x-disabled': expression(`$form.readPretty`),
            'x-decorator': 'FormItem'
          },
          businessStartDate: {
            ...yearMonthDaySelectorSegment,
            'x-hidden': expression(`$form.query('.companyTypeAfter.companyType').take().value == 'GETI'`),
            title: i18nExpression('vendorMod.businessStartFrom'), // 营业日期从
            'x-reactions': expression(`() => {
                const newData = $form.query('.enterpriseThreeCertificatesAfter.businessStartDate').take().value
                const oldData = $form.query('.enterpriseThreeCertificatesBefore.businessStartDate').take().value
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),
            'x-disabled': expression(`$form.readPretty`),
            'x-decorator': 'FormItem'
          },
          businessEndDate: {
            ...yearMonthDaySelectorSegment,
            'x-hidden': expression(`$form.query('.companyTypeAfter.companyType').take().value == 'GETI'`),
            title: i18nExpression('common.pleaseSelectDate'), // 营业日期至
            'x-reactions': expression(`() => {
                const newData = $form.query('.enterpriseThreeCertificatesAfter.businessEndDate').take().value
                const oldData = $form.query('.enterpriseThreeCertificatesBefore.businessEndDate').take().value
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),
            'x-disabled': expression(`$form.readPretty`),
            'x-decorator': 'FormItem'
          },
          businessScope: {
            type: 'string',
            title: i18nExpression('vendorMod.businessScope'),
            'x-reactions': expression(`() => {
                const newData = $form.query('.enterpriseThreeCertificatesAfter.businessScope').take().value
                const oldData = $form.query('.enterpriseThreeCertificatesBefore.businessScope').take().value
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),
            'x-disabled': expression(`$form.readPretty`),
            'x-decorator': 'FormItem'
          }
        }
      }
    }
  }
}
