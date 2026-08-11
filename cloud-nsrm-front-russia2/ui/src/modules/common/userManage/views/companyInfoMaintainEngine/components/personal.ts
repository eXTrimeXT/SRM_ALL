import {
  expression, i18nExpression
} from '@meicloud/render-engine'

export const personal = {
  personal: {
    type: 'void',
    'x-component': 'CollapseItem',
    'x-visible': false,
    'x-component-props': {
      title: i18nExpression('vendorMod.companyBaseInfo3')
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
          flowRemark: {
            type: 'string',
            'x-decorator': 'FormItem',
            'x-hidden': true
          },
          status: {
            type: 'string',
            default: 'DRAFT',
            'x-hidden': true
          },
          // 境内外关系
          'overseasRelation2': {
            type: 'string',
            'x-decorator': 'FormItem',
            default: 'PERSONAL',
            'x-component': 'DictSelect',
            'x-component-props': {
              'disabled': true,
              code: 'RELATION_NEW'
            },
            title: expression(`$t('vendorMod.overseasRelation')`),
            'x-validator': {
              required: true,
              message: i18nExpression('common.requiredField')
            }
          },
          companyName2: {
            type: 'string',
            title: expression(`$t('vendorMod.vendorName')`), // 供应商名称
            'x-decorator': 'FormItem',
            'x-component-props': {
              'disabled': expression(`$form.query('state').get('data').$disabled`)
            },
            'x-validator': {
              required: true,
              message: i18nExpression('common.requiredField')
            }
          },
          companyShortName2: {
            type: 'string',
            title: expression(`$t('vendorMod.companyShortNameV')`), // 供应商简称
            'x-component-props': {
              'disabled': expression(`$form.query('state').get('data').$disabled`)
            },
            'x-decorator': 'FormItem'
          },
          companyEnName: {
            type: 'string',
            title: expression(`$t('vendorMod.companyEnName')`), // 公司名称(英文)
            'x-component-props': {
              'disabled': expression(`$form.query('state').get('data').$disabled`)
            },
            'x-decorator': 'FormItem'
          },
          // 是否具有出口资质(Y/N)
          exportQualification: {
            type: 'string',
            'x-decorator': 'FormItem',
            'x-component': 'DictSelect',
            'x-component-props': {
              'disabled': expression(`$form.query('state').get('data').$disabled`),
              code: 'YES_OR_NO'
            },
            title: expression(`$t('vendorMod.exportQualification')`)
          },
          idNumber: {
            type: 'string',
            title: expression(`$t('vendorMod.idNumber')`), // 身份证号码
            'x-decorator': 'FormItem',
            'x-component-props': {
              'disabled': expression(`$form.query('state').get('data').$disabled`)
            },
            'x-validator': {
              required: true,
              message: i18nExpression('common.requiredField')
            }
          },
          idCardFileId: {
            type: 'string',
            'x-hidden': true,
            'x-decorator': 'FormItem'
          },
          idCardFileName: {
            type: 'string',
            title: i18nExpression('vendorMod.cardFile'), // 上传身份证
            'x-component': 'SrmCommonFile',
            'x-component-props': {
              disabled: expression(`$form.query('state').get('data').$disabled`),
              'extra-data': {
                uploadType: 'DEF',
                sourceType: 'WEB_APP',
                fileModular: 'sup',
                fileFunction: 'companyInfoMaintain',
                fileType: 'images'
              },
              'default-file': {
                fileId: expression(`$form.query('.idCardFileId').take()?.value`),
                fileName: expression(`$form.query('.idCardFileName').take()?.value`)
              },
              '@on-change': expression(`(file) => {
                if (file) {
                  const { fileId, fileName } = file.file || {}
                  $form.query('.idCardFileId').take().value = fileId.toString()
                  $form.query('.idCardFileName').take().value = fileName
                } else {
                  $form.query('.idCardFileId').take().value = null
                  $form.query('.idCardFileName').take().value = null
                }
              }`)
            },
            'x-decorator': 'FormItem',
            'x-validator': {
              required: true,
              message: i18nExpression('common.requiredField')
            }
          }
        }
      }
    }
  },
}
