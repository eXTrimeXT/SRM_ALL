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
            'x-decorator': 'FormItem'
          },
          companyName: {
            type: 'string',
            title: i18nExpression('vendorMod.companyName'), // 企业名称
            'x-component-props': {
              disabled: true
            },
            'x-decorator': 'FormItem'
          },
          companyShortName: {
            type: 'string',
            title: i18nExpression('vendorMod.companyShortName'), // 企业简称
            'x-disabled': true,
            'x-decorator': 'FormItem'
          },
          companyEnName: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.companyEnName'), // 企业英文名称
            'x-disabled': true,
            'x-decorator': 'FormItem'
          },
          lcCode: {
            type: 'string',
            title: i18nExpression('vendorMod.lcCode'), // 纳税人识别号
            'x-disabled': true,
            'x-decorator': 'FormItem'
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
          // 账户组
          accountGroup: {
            type: 'string',
            default: 'Z001',
            'x-decorator': 'FormItem',
            title: expression(`$t('cusEntry.vendorMod.accountGroup')`),
            'x-component-props': {
              disabled: true
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
            }
          },
          ifLongPeriod: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.ifLongTermSupplier'), // 是否长期供应商
            'x-disabled': true,
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'YES_OR_NO'
            },
            'x-decorator': 'FormItem'
          },
          businessStartDate: {
            ...yearMonthDaySelectorSegment,
            title: i18nExpression('vendorMod.businessStartFrom'), // 营业日期从
            'x-disabled': true,
            'x-decorator': 'FormItem'
          },
          businessEndDate: {
            type: 'date',
            'x-hidden': true
          },
          companyCreationDate: {
            ...yearMonthDaySelectorSegment,
            title: i18nExpression('vendorMod.creationDate'), // 成立日期
            'x-disabled': true,
            'x-decorator': 'FormItem'
          },
          businessScope: {
            type: 'string',
            title: i18nExpression('vendorMod.businessScope'),
            'x-component-props': {
              type: 'textarea',
              maxlength: 2000
            },
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
                if (file && file.file) {
                  const { fileId = null, fileName = null } = file.file || {}
                  $form.query('.enterpriseThreeCertificatesAfter.businessLicenseFileId').take().value = fileId.toString()
                  $form.query('.enterpriseThreeCertificatesAfter.businessLicense').take().value = fileName
                  // 读取图片信息
                  // app.$http({
                  //   url: '/api-pj/ocr/recognizeLcImage',
                  //   method: 'GET',
                  //   params: { fileuploadId: fileId },
                  //   loading: true
                  // }).then(res => {
                  //   const {
                  //     regNum,
                  //     person,
                  //     name,
                  //     address,
                  //     business,
                  //     businessEndDate,
                  //     businessStartDate,
                  //     capital,
                  //     period,
                  //     setDate,
                  //     type
                  //   } = res.data
                  //   let form = $form.query('enterpriseThreeCertificatesAfter').get('value')
                  //   form.companyName = name
                  //   form.companyType = type
                  //   form.legalPerson = person
                  //   // form.lcCode = regNum
                  //   form.businessStartDate = businessStartDate
                  //   form.businessEndDate = businessEndDate
                  //   form.companyAddress = address
                  //   form.businessScope = business
                  //   const [year, month, day] = setDate.replace(\/[^\\d]\/g, '-').split('-')
                  //   const createDate = year + '-' + month + '-' + day
                  //   form.companyCreationDate = app.$dayjs(createDate).format('YYYY-MM-DD')
                  // })
                  // .catch(err => {
                  //   console.log(err)
                  // })
                } else {
                  $form.query('.enterpriseThreeCertificatesAfter.businessLicenseFileId').take().value = null
                  $form.query('.enterpriseThreeCertificatesAfter.businessLicense').take().value = null
                }
              }`)
            },
            'x-decorator': 'FormItem',
            'x-reactions': expression(`() => {
              const newData = $form.query('.enterpriseThreeCertificatesAfter.businessLicenseFileId').take().value
              const oldData = $form.query('.enterpriseThreeCertificatesBefore.businessLicenseFileId').take().value
              let className = redFunction(oldData, newData)
              $self.setComponentProps({ class: className })
            }`),
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
            ...requiredValidatorSegment
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
            'x-decorator': 'FormItem',
            ...requiredValidatorSegment
          },
          companyEnName: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.companyEnName'), // 企业英文名称
            'x-decorator': 'FormItem',
            'x-reactions': expression(`() => {
              const newData = $form.query('.enterpriseThreeCertificatesAfter.companyEnName').take().value
              const oldData = $form.query('.enterpriseThreeCertificatesBefore.companyEnName').take().value
              let className = redFunction(oldData, newData)
              $self.setComponentProps({ class: className })
            }`),
          },
          lcCode: {
            type: 'string',
            title: i18nExpression('vendorMod.lcCode'), // 纳税人识别号
            'x-reactions': expression(`() => {
                const newData = $form.query('.enterpriseThreeCertificatesAfter.lcCode').take().value
                const oldData = $form.query('.enterpriseThreeCertificatesBefore.lcCode').take().value
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),
            'x-disabled': true,
            'x-decorator': 'FormItem',
            ...requiredValidatorSegment
          },
          enterpriseNo: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.enterpriseNo'),
            'x-reactions': expression(`() => {
              const newData = $form.query('.enterpriseThreeCertificatesAfter.enterpriseNo').take().value
              const oldData = $form.query('.enterpriseThreeCertificatesBefore.enterpriseNo').take().value
              let className = redFunction(oldData, newData)
              $self.setComponentProps({ class: className })
            }`),
            'x-disabled': expression(`$form.readPretty`),
            'x-decorator': 'FormItem',
            ...requiredValidatorSegment
          },
          // KPP编码
          extKpp: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.extKpp'),
            'x-decorator': 'FormItem',
            'x-reactions': expression(`() => {
              const newData = $form.query('.enterpriseThreeCertificatesAfter.extKpp').take().value
              const oldData = $form.query('.enterpriseThreeCertificatesBefore.extKpp').take().value
              let className = redFunction(oldData, newData)
              $self.setComponentProps({ class: className })
            }`),
            'x-component-props': {
              disabled:  expression(`$form.readPretty`),
            }
          },
          // 账户组
          accountGroup: {
            type: 'string',
            default: 'Z001',
            'x-decorator': 'FormItem',
            title: expression(`$t('cusEntry.vendorMod.accountGroup')`),
            'x-component-props': {
              disabled: true
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
            'x-decorator': 'FormItem',
            ...requiredValidatorSegment
          },
          // 币种
          registCurrency: {
            type: 'string',
            'x-hidden': true
          },
          // 注册资本(万)
          registeredCapital: {
            type: 'number',
            'x-decorator': 'FormItem',
            'x-component': 'Input',
            title: expression(`$t('vendorMod.registeredCapital')`),
            'x-component-props': {
              'disabled': expression(`$form.readPretty`),
              class: "input-with-select",
              '@change': expression(`(value) => {
                $self.value = value.replace(\/[^\\d.]\/g, '')
              }`)
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
                  const beforeValue = $form.query('.enterpriseThreeCertificatesBefore.registCurrency').take().value
                  let className = redFunction(beforeValue, targetField.value)
                  return h(
                    $self.readPretty ? $$components.DictSelectPreview : $$components.DictSelect,
                    {
                      [$self.readPretty ? 'props' : 'attrs']: {
                        code: 'currency',
                        value: targetField.value,
                        class: className
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
          ifLongPeriod: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.ifLongTermSupplier'), // 是否长期供应商
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'YES_OR_NO',
              '@change': expression(`value => {
                if (value === 'Y') {
                  $form.query('enterpriseThreeCertificatesAfter.businessStartDate').take().value = null
                  $form.query('enterpriseThreeCertificatesAfter.businessEndDate').take().value = null
                }
              }`)
            },
            'x-reactions': expression(`() => {
              const newData = $form.query('.enterpriseThreeCertificatesAfter.ifLongPeriod').take().value
              const oldData = $form.query('.enterpriseThreeCertificatesBefore.ifLongPeriod').take().value
              let className = redFunction(oldData, newData)
              $self.setComponentProps({ class: className })
            }`),
            'x-decorator': 'FormItem',
            ...requiredValidatorSegment
          },
          businessStartDate: {
            ...yearMonthDaySelectorSegment,
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
            type: 'date',
            'x-hidden': true
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
            'x-decorator': 'FormItem',
            ...requiredValidatorSegment
          },
          businessScope: {
            type: 'string',
            title: i18nExpression('vendorMod.businessScope'),  // 经营范围
            'x-reactions': expression(`() => {
              const newData = $form.query('.enterpriseThreeCertificatesAfter.businessScope').take().value
              const oldData = $form.query('.enterpriseThreeCertificatesBefore.businessScope').take().value
              let className = redFunction(oldData, newData)
              $self.setComponentProps({ class: className })
            }`),
            'x-disabled': expression(`$form.readPretty`),
            'x-decorator': 'FormItem',
            'x-component-props': {
              type: 'textarea',
              maxlength: 2000
            },
            //去除必填验证器
            // ...requiredValidatorSegment
          }
        }
      }
    }
  }
}
