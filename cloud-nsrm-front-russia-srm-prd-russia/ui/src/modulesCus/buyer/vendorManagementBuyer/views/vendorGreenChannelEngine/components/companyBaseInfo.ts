import {
  expression, 
  i18nExpression,
  generateCharExpressionByFunction
} from "@meicloud/render-engine"
import {radioGroupByYOrNSegment} from "lib@/components/render-engine";

export const companyBaseInfo = {
  companyBaseInfo: {
    type: 'void',
    'x-component': 'CollapseItem',
    'x-component-props': {
      title: i18nExpression('vendorMod.companyBaseInfo')
    },
    'x-visible': generateCharExpressionByFunction(({ $form }) => {
      return $form.query('state').get('data').overseasRelation !== 'PERSONAL'
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
          // 商业模式
          ceeaBusinessModel: {
            type: 'string',
            'x-decorator': 'FormItem',
            'x-component': 'DictSelect',
            'x-component-props': {
              'disabled': expression('$disabled'),
              code: 'BIZ_MODEL',
              multiple: true
            },
            title: expression(`$t('vendorMod.bizModel')`)
          },
          // 是否上市
          ceeaIfListed: {
            title: i18nExpression('vendorMod.ifListed'),
            ...radioGroupByYOrNSegment,
            'x-component-props': {
              'disabled': expression('$disabled')
              // '@change': expression(`() => updateButtonConfig($form)`)
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
              'disabled': expression('$disabled')
            },
            title: expression(`$t('vendorMod.listedDate')`),
            'x-validator': {
              required: true,
              message: i18nExpression('请选择上市时间')
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
          // 国家
          companyCountry: {
            type: 'string',
            title: i18nExpression('components.address.country'),
            'x-decorator': 'FormItem',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'country',
              placeholder:  expression(`$t('common.pleaseSelect')`),
              'disabled': expression('$disabled'),
              '@change': expression(`(val) => {
                let row = $form.values
                // 选择国外就清理省市区，并且禁用
                if (row.companyCountry !== 'CN') {
                  row.companyProvince = null
                  row.companyCity = null
                }
              }`)
            },
            'x-validator': {
              required: true,
              message: i18nExpression('vendorMod.msgSelCountry')
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
              'custom-select-type':"PROVINCE",
              placeholder:  expression(`$t('common.pleaseSelect')`),
              disabled: expression(`$disabled || $form.values.companyCountry!='CN'`)
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
              disabled: expression(`$disabled || $form.values.companyCountry!='CN'`)
            },
          },
          // 详细地址
          companyAddress: {
            type: 'string',
            'x-decorator': 'FormItem',
            'x-component': 'Input',
            title: i18nExpression('cusEntry.vendorMod.detailAddress'),
            'x-component-props': {
              'disabled': expression('$disabled')
            },
            'x-validator': {
              required: true,
              message: i18nExpression('vendorMod.msgDetailAddr')
            }
          },
          // 是否有母公司
          ceeaHasParentCompany: {
            title: i18nExpression('cusEntry.vendorMod.ifParentCompany'),
            ...radioGroupByYOrNSegment,
            'x-component-props': {
              'disabled': expression('$disabled')
              // '@change': expression(`() => updateButtonConfig($form)`)
            }
          },
          // 母公司名称
          ceeaParentCompanyName: {
            type: 'string',
            'x-decorator': 'FormItem',
            'x-component': 'Input',
            'x-visible': `{{$form.query('.ceeaHasParentCompany').take().value == 'Y'}}`,
            title: i18nExpression('cusEntry.vendorMod.parentCompanyName'),
            'x-component-props': {
              'disabled': expression('$disabled')
            },
            'x-validator': {
              required: true,
              message: i18nExpression('请输入集团名称')
            }
          },
          groupCountry: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.parentCompanyCountry'),
            'x-visible': `{{$form.query('.ceeaHasParentCompany').take().value == 'Y'}}`,
            'x-decorator': 'FormItem',
            'x-component': 'DictSelect',
            'x-component-props': {
              code: 'country',
              'disabled': expression('$disabled'),
              placeholder: expression('$t(\'common.pleaseSelect\')')
            },
            'x-validator': {
              required: true,
              message: i18nExpression('cusEntry.tipMessage.parentCompanyCountryMsg')
            }
          },
          // 企业简介
          ceeaCompanyIntro: {
            type: 'string',
            'x-decorator': 'FormItem',
            'x-component': 'Input.TextArea',
            title: expression(`$t('vendorMod.companyProfile')`),
            'x-component-props': {
              'disabled': expression('$disabled'),
              maxlength: 2000
            },
            'x-decorator-props': { gridSpan: 3 }
          }
        }
      }
    }
  },
}
