import {
  expression, i18nExpression
} from "@meicloud/render-engine"
import {editTableFormItemValid, radioGroupByYOrNSegment} from "lib@/components/render-engine";

export const companyBaseInfo = {
  companyBaseInfo: {
    type: 'void',
    'x-component': 'CollapseItem',
    'x-component-props': {
      title: i18nExpression('vendorMod.companyBaseInfo2')
    },
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
          // 代理品牌
          'ceeaAgentBrand': {
            type: 'string',
            'x-decorator': 'FormItem',
            'x-component': 'Input',
            title: expression(`$t('vendorMod.agencyBrand')`),
            'x-component-props': {
              'disabled': expression(`$disabled`)
            },
            'x-validator': {
              required: false,
              message: i18nExpression('vendorMod.msgAgencyBrand')
            }
          },
          categoryName: {
            type: 'string',
            'x-hidden': true
          },
          cateJournalList: {
            type: 'Array',
            'x-hidden': true
          },
          // 主营品类
          'cateJournalListAll': {
            type: 'Array',
            'x-decorator': 'FormItem',
            'x-component': 'CCategorySelect',
            title: expression(`$t('vendorMod.mainCategory')`),
            'x-query-engine-skip': true,
            'x-component-props': {
              'disabled': expression(`$disabled`),
              'select-type': "input",
              'class': "categoryName",
              'multiple': true,
              'selected-lines': expression(`$form.query('.cateJournalList').take().value`),
              placeholder: expression(`$form.query('.categoryName').take().value`),
              '@select': expression(`(val) => {

              }`)
            },
            'x-validator': {
              required: false,
              message: i18nExpression('vendorMod.msgAgencyBrand')
            }
          },
          // 是否上市
          ceeaIfListed: {
            title: i18nExpression('vendorMod.ifListed'),
            ...radioGroupByYOrNSegment,
            'x-component-props': {
              'disabled': expression(`$disabled`)
            }
          },
          // 上市时间
          ceeaListedTime: {
            type: 'date',
            default: null,
            'x-visible': `{{$form.query('.ceeaIfListed').take().value == 'Y'}}`,
            'x-decorator': 'FormItem',
            'x-component-props': {
              placeholder: i18nExpression('common.pleaseSelectDate'),
              format: 'yyyy-MM-dd',
              'value-format': 'yyyy-MM-dd',
              'disabled': expression(`$disabled`)
            },
            title: expression(`$t('vendorMod.listedDate')`),
            'x-validator': {
              required: true,
              message: i18nExpression('请选择上市时间')
            }
          },
          // 上市交易所
          listedExchange: {
            type: 'string',
            'x-visible': `{{$form.query('.ceeaIfListed').take().value == 'Y'}}`,
            'x-decorator': 'FormItem',
            'x-component-props': {
              'disabled': expression(`$disabled`)
            },
            title: expression(`$t('vendorMod.listedExchange')`),
            'x-validator': {
              required: true,
              message: i18nExpression('请选择上市交易所')
            }
          },
          // 商业模式
          'ceeaBusinessModel': {
            type: 'string',
            'x-decorator': 'FormItem',
            'x-component': 'DictSelect',
            'x-component-props': {
              'disabled': expression(`$disabled`),
              code: 'BIZ_MODEL'
            },
            title: expression(`$t('vendorMod.bizModel')`)
          },
          // 国家
          companyCountry: {
            type: 'string',
            title: i18nExpression('components.address.country'),
            'x-decorator': 'FormItem',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'country',
              placeholder:  expression(`$t('common.pleaseSelect')`),
              'disabled': expression(`$disabled`),
              '@change': expression(`(val) => {
                let row = $form.values
                // 选择国外就清理省市区，并且禁用
                if (row.companyCountry !== 'CN') {
                  row.companyProvince = null
                  row.companyCity = null
                }
              }`)
            },
          },
          // 省
          companyProvince: {
            type: 'string',
            title: i18nExpression('components.address.area'),
            'x-decorator': 'FormItem',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'PROVINCE',
              'custom-select-type':"PROVINCE",
              placeholder:  expression(`$t('common.pleaseSelect')`),
              disabled: expression(`$disabled`)
            },
          },
          // 城市
          companyCity: {
            type: 'string',
            title: i18nExpression('components.address.city'),
            'x-decorator': 'FormItem',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: expression(`$form.values.companyProvince`),
              'custom-select-type': "CITY",
              placeholder:  expression(`$t('common.pleaseSelect')`),
              disabled: expression(`$disabled`)
            },
          },
          // 详细地址
          companyAddress: {
            type: 'string',
            'x-decorator': 'FormItem',
            'x-component': 'Input',
            title: expression(`$t('components.address.detailAddress2')`),
            'x-component-props': {
              'disabled': expression(`$disabled`)
            },
            'x-validator': {
              required: true,
              message: i18nExpression('vendorMod.msgDetailAddr')
            }
          },
          // 是否有母公司
          ceeaHasParentCompany: {
            title: i18nExpression('vendorMod.ifParentCompany'),
            ...radioGroupByYOrNSegment,
            'x-component-props': {
              'disabled': expression(`$disabled`)
            }
          },
          // 母公司统一信用代码
          'ceeaParentCompanyLcCode': {
            type: 'string',
            'x-decorator': 'FormItem',
            'x-component': 'Input',
            'x-visible': `{{$form.query('.ceeaHasParentCompany').take().value == 'Y'}}`,
            title: expression(`$t('vendorMod.parentCompanyLcCode')`),
            'x-component-props': {
              'disabled': expression(`$disabled`)
            },
            'x-validator': {
              required: true,
              message: i18nExpression('请输入母公司统一信用代码')
            }
          },
          // 母公司名称
          'ceeaParentCompanyName': {
            type: 'string',
            'x-decorator': 'FormItem',
            'x-component': 'Input',
            'x-visible': `{{$form.query('.ceeaHasParentCompany').take().value == 'Y'}}`,
            title: expression(`$t('vendorMod.parentCompanyName')`),
            'x-component-props': {
              'disabled': expression(`$disabled`)
            },
            'x-validator': {
              required: true,
              message: i18nExpression('请输入母公司名称')
            }
          },
          // 企业简介
          'ceeaCompanyIntro': {
            type: 'string',
            'x-decorator': 'FormItem',
            'x-component': 'Input',
            title: expression(`$t('vendorMod.companyProfile')`),
            'x-component-props': {
              'disabled': expression(`$disabled`)
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
}
