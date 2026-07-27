import { expression, i18nExpression, methodExpression } from '@meicloud/render-engine'

import {
  checkboxByYOrNSegment, editTableFormItemValid,
  formGridSegment, radioGroupByYOrNSegment, yearMonthDaySelectorSegment
} from 'lib@/components/render-engine/schema-segments'

export const companyBaseInfo = {
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
      companyBaseInfoBefore: {
        type: 'object',
        'x-query-engine-skip': true,
        'x-component': 'FormGrid',
        'x-component-props': {
          class: 'forms'
        },
        properties: {
          ceeaAgentBrand: {
            type: 'string',
            title: i18nExpression('vendorMod.agencyBrand'),
            'x-validator': {
              required: false,
              message: i18nExpression('common.requiredField')
            },
            'x-disabled': true,
            'x-decorator': 'FormItem'
          },
          // 是否上市
          ceeaIfListed: {
            title: i18nExpression('vendorMod.ifListed'),
            ...radioGroupByYOrNSegment,
            'x-component-props': {
              'disabled': true
              // '@change': expression(`() => updateButtonConfig($form)`)
            }
          },
          // 上市时间
          ceeaListedTime: {
            ...yearMonthDaySelectorSegment,
            'x-visible': '{{$form.query(\'.companyBaseInfoBefore.ceeaIfListed\').take()?.value == \'Y\'}}',
            'x-decorator': 'FormItem',
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              'disabled': true
            },
            title: expression('$t(\'vendorMod.creationDate\')'),
            'x-validator': {
              required: true,
              message: i18nExpression('请选择上市时间')
            }
          },
          // 上市交易所
          listedExchange: {
            type: 'string',
            'x-visible': '{{$form.query(\'.companyBaseInfoBefore.ceeaIfListed\').take()?.value == \'Y\'}}',
            'x-decorator': 'FormItem',
            'x-component-props': {
              'disabled': true
            },
            title: expression('$t(\'vendorMod.listedExchange\')'),
            'x-validator': {
              required: true,
              message: i18nExpression('请选择上市交易所')
            }
          },
          ceeaBusinessModel: {
            type: 'string',
            title: i18nExpression('vendorMod.bizModel'), // 商业模式
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'BIZ_MODEL',
              disabled: true
            },
            'x-decorator': 'FormItem'
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
            }
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
            }
          },
          // 城市
          companyCity: {
            type: 'string',
            title: i18nExpression('components.address.city'),
            'x-decorator': 'FormItem',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: expression('$form.query(\'.companyBaseInfoBefore.companyProvince\').take()?.value'),
              'custom-select-type': 'CITY',
              placeholder: expression('$t(\'common.pleaseSelect\')'),
              disabled: true
            }
          },
          companyAddress: {
            type: 'string',
            title: i18nExpression('components.address.detailAddress'),
            'x-disabled': true,
            'x-decorator': 'FormItem'
          },
          // 是否有母公司
          ceeaHasParentCompany: {
            title: i18nExpression('vendorMod.ifParentCompany'),
            ...radioGroupByYOrNSegment,
            'x-component-props': {
              'disabled': true
              // '@change': expression(`() => updateButtonConfig($form)`)
            }
          },
          // 母公司名称
          'ceeaParentCompanyName': {
            type: 'string',
            'x-decorator': 'FormItem',
            'x-component': 'Input',
            'x-visible': '{{$form.query(\'.companyBaseInfoBefore.ceeaHasParentCompany\').take()?.value == \'Y\'}}',
            title: expression('$t(\'vendorMod.parentCompanyName\')'),
            'x-component-props': {
              'disabled': true
            },
            'x-validator': {
              required: true,
              // '请输入母公司名称'
              message: i18nExpression('vendorMod.msgPCompany')
            }
          },
          // DUNS
          dunsCode: {
            type: 'string',
            'x-visible': expression('$form.query(\'.companyTypeAfter.overseasRelation\').take()?.value != \'INSIDE\''),
            title: 'D-U-N-S',
            'x-decorator': 'FormItem'
          },
          // 母公司统一信用代码
          'ceeaParentCompanyLcCode': {
            type: 'string',
            'x-decorator': 'FormItem',
            'x-component': 'Input',
            'x-visible': '{{$form.query(\'.companyBaseInfoBefore.ceeaHasParentCompany\').take()?.value == \'Y\'}}',
            title: expression('$t(\'vendorMod.parentCompanyLcCode\')'),
            'x-component-props': {
              'disabled': true
            },
            'x-validator': {
              required: true,
              // '请输入母公司统一信用代码'
              message: i18nExpression('cusEntry.supplement20250211.parentCompanyCreditCode')
            }
          },
          // 企业简介
          'ceeaCompanyIntro': {
            type: 'string',
            'x-decorator': 'FormItem',
            'x-component': 'Input',
            title: expression('$t(\'vendorMod.companyProfile\')'),
            'x-component-props': {
              'disabled': true
            },
            'x-decorator-props': { gridSpan: 3 },
            'x-validator': {
              required: true,
              message: i18nExpression('vendorMod.msgCompanyProfile')
            }
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
      companyBaseInfoAfter: {
        type: 'object',
        'x-query-engine-skip': true,
        'x-component': 'FormGrid',
        'x-component-props': {
          class: 'forms'
        },
        properties: {
          ceeaAgentBrand: {
            type: 'string',
            title: i18nExpression('vendorMod.agencyBrand'),
            'x-validator': {
              required: false,
              message: i18nExpression('common.requiredField')
            },
            'x-reactions': expression(`() => {
                const newData = $form.query('.companyBaseInfoAfter.ceeaAgentBrand').take()?.value
                const oldData = $form.query('.companyBaseInfoBefore.ceeaAgentBrand').take()?.value
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),
            'x-disabled': expression('$form.readPretty'),
            'x-decorator': 'FormItem'
          },
          // 是否上市
          ceeaIfListed: {
            title: i18nExpression('vendorMod.ifListed'),
            ...radioGroupByYOrNSegment,
            'x-reactions': expression(`() => {
                const newData = $form.query('.companyBaseInfoAfter.ceeaIfListed').take()?.value
                const oldData = $form.query('.companyBaseInfoBefore.ceeaIfListed').take()?.value
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),
            'x-component-props': {
              // '@change': expression(`() => updateButtonConfig($form)`)
            }
          },
          // 上市时间
          ceeaListedTime: {
            ...yearMonthDaySelectorSegment,
            'x-visible': '{{$form.query(\'.companyBaseInfoAfter.ceeaIfListed\').take()?.value == \'Y\'}}',
            'x-decorator': 'FormItem',
            'x-component-props': {
              ...yearMonthDaySelectorSegment['x-component-props'],
              'disabled': expression('$form.readPretty')
            },
            'x-reactions': expression(`() => {
                const newData = $form.query('.companyBaseInfoAfter.ceeaListedTime').take()?.value
                const oldData = $form.query('.companyBaseInfoBefore.ceeaListedTime').take()?.value
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),
            title: expression('$t(\'vendorMod.creationDate\')'),
            'x-validator': {
              required: true,
              // '请选择上市时间'
              message: i18nExpression('common.marketTime')
            }
          },
          // 上市交易所
          listedExchange: {
            type: 'string',
            'x-visible': '{{$form.query(\'.companyBaseInfoAfter.ceeaIfListed\').take().value == \'Y\'}}',
            'x-decorator': 'FormItem',
            'x-component-props': {
              'disabled': expression('$form.readPretty')
            },
            title: expression('$t(\'vendorMod.listedExchange\')'),
            'x-validator': {
              required: true,
              // '请选择上市交易所'
              message: i18nExpression('common.listingExchange')
            }
          },
          ceeaBusinessModel: {
            type: 'string',
            title: i18nExpression('vendorMod.bizModel'), // 商业模式
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'BIZ_MODEL'
            },
            'x-reactions': expression(`() => {
                const newData = $form.query('.companyTypeAfter.ceeaBusinessModel').take()?.value || null
                const oldData = $form.query('.companyTypeBefore.ceeaBusinessModel').take()?.value || null
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),
            'x-decorator': 'FormItem'
          },
          companyCountry: {
            type: 'string',
            title: i18nExpression('vendorMod.businessAddr'),
            'x-component': 'DictSelect',
            'x-reactions': expression(`() => {
                const newData = $form.query('.companyBaseInfoAfter.companyCountry').take().value
                const oldData = $form.query('.companyBaseInfoBefore.companyCountry').take().value
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),
            'x-component-props': {
              code: 'country',
              placeholder: expression('$t(\'common.pleaseSelect\')'),
              'disabled': expression('$form.readPretty'),
              '@change': expression(`(val) => {
                // 选择国外就清理省市区，并且禁用
                if ($form.query('.companyBaseInfoAfter.companyCountry').take().value !== 'CN') {
                  $form.query('.companyBaseInfoAfter.companyProvince').take().value = ''
                  $form.query('.companyBaseInfoAfter.companyCity').take().value = ''
                }
              }`)
            },
            'x-decorator': 'FormItem'
          },
          companyProvince: {
            type: 'string',
            title: i18nExpression('vendorMod.province'),
            'x-component': 'DictSelect',
            'x-reactions': expression(`() => {
                const newData = $form.query('.companyBaseInfoAfter.companyProvince').take().value
                const oldData = $form.query('.companyBaseInfoBefore.companyProvince').take().value
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),
            'x-component-props': {
              code: 'PROVINCE',
              'custom-select-type': 'PROVINCE',
              placeholder: expression('$t(\'common.pleaseSelect\')'),
              disabled: expression('$form.readPretty || $form.query(\'.companyBaseInfoAfter.companyCountry\').take().value !=\'CN\'')
            },
            'x-decorator': 'FormItem'
          },
          companyCity: {
            type: 'string',
            title: i18nExpression('vendorMod.city'),
            'x-component': 'DictSelect',
            'x-reactions': expression(`() => {
                const newData = $form.query('.companyBaseInfoAfter.companyCity').take().value
                const oldData = $form.query('.companyBaseInfoBefore.companyCity').take().value
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),
            'x-component-props': {
              code: expression('$form.query(\'.companyBaseInfoAfter.companyProvince\').take().value || \'\''),
              'custom-select-type': 'CITY',
              placeholder: expression('$t(\'common.pleaseSelect\')'),
              disabled: expression('$form.readPretty || $form.query(\'.companyBaseInfoAfter.companyCountry\').take().value !=\'CN\'')
            },
            'x-decorator': 'FormItem'
          },
          companyAddress: {
            type: 'string',
            title: i18nExpression('components.address.detailAddress2'),
            'x-reactions': expression(`() => {
                const newData = $form.query('.enterpriseThreeCertificatesAfter.companyAddress').take()?.value
                const oldData = $form.query('.enterpriseThreeCertificatesBefore.companyAddress').take()?.value
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),
            'x-disabled': expression('$form.readPretty'),
            'x-decorator': 'FormItem'
          },
          // 是否有母公司
          ceeaHasParentCompany: {
            title: i18nExpression('vendorMod.ifParentCompany'),
            ...radioGroupByYOrNSegment,
            'x-reactions': expression(`() => {
                const newData = $form.query('.companyBaseInfoAfter.ceeaHasParentCompany').take().value
                const oldData = $form.query('.companyBaseInfoBefore.ceeaHasParentCompany').take().value
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),
            'x-component-props': {
              // '@change': expression(`() => updateButtonConfig($form)`)
            }
          },
          // 母公司名称
          'ceeaParentCompanyName': {
            type: 'string',
            'x-decorator': 'FormItem',
            'x-component': 'Input',
            'x-visible': '{{$form.query(\'.companyBaseInfoBefore.ceeaHasParentCompany\').take().value == \'Y\'}}',
            'x-reactions': expression(`() => {
                const newData = $form.query('.companyBaseInfoAfter.ceeaParentCompanyName').take().value
                const oldData = $form.query('.companyBaseInfoBefore.ceeaParentCompanyName').take().value
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),
            title: expression('$t(\'vendorMod.parentCompanyName\')'),
            'x-component-props': {
              'disabled': expression('$form.readPretty')
            },
            'x-validator': {
              required: true,
              // '请输入母公司名称'
              message: i18nExpression('vendorMod.msgPCompany')
            }
          },
          // 母公司统一信用代码
          'ceeaParentCompanyLcCode': {
            type: 'string',
            'x-decorator': 'FormItem',
            'x-component': 'Input',
            'x-visible': '{{$form.query(\'.companyBaseInfoBefore.ceeaHasParentCompany\').take().value == \'Y\'}}',
            'x-reactions': expression(`() => {
                const newData = $form.query('.companyBaseInfoAfter.ceeaParentCompanyLcCode').take().value
                const oldData = $form.query('.companyBaseInfoBefore.ceeaParentCompanyLcCode').take().value
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),
            title: expression('$t(\'vendorMod.parentCompanyLcCode\')'),
            'x-component-props': {
              'disabled': expression('$form.readPretty')
            },
            'x-validator': {
              required: true,
              // '请输入母公司统一信用代码'
              message: i18nExpression('cusEntry.supplement20250211.parentCompanyCreditCode')
            }
          },
          // DUNS
          dunsCode: {
            type: 'string',
            'x-visible': expression('$form.query(\'.companyTypeAfter.overseasRelation\').take().value != \'INSIDE\''),
            title: 'D-U-N-S',
            'x-reactions': expression(`() => {
                const newData = $form.query('.companyBaseInfoAfter.dunsCode').take().value
                const oldData = $form.query('.companyBaseInfoBefore.dunsCode').take().value
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),
            'x-decorator': 'FormItem'
          },
          // 企业简介
          'ceeaCompanyIntro': {
            type: 'string',
            'x-decorator': 'FormItem',
            'x-component': 'Input',
            title: expression('$t(\'vendorMod.companyProfile\')'),
            'x-component-props': {
              'disabled': expression('$form.readPretty')
            },
            'x-reactions': expression(`() => {
                const newData = $form.query('.companyBaseInfoAfter.ceeaCompanyIntro').take().value
                const oldData = $form.query('.companyBaseInfoBefore.ceeaCompanyIntro').take().value
                let className = redFunction(oldData, newData)
                $self.setComponentProps({ class: className })
            }`),
            'x-decorator-props': { gridSpan: 3 },
            'x-validator': {
              required: true,
              message: i18nExpression('vendorMod.msgCompanyProfile')
            }
          }
        }
      }
    }
  }
}
