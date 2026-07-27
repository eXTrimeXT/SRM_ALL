import {
  expression,
  i18nExpression,
  generateCharExpressionByFunction
} from '@meicloud/render-engine'
import {
  requiredValidatorSegment,
  formGridSegment
} from 'lib@/components/render-engine/schema-segments'

export const materialTrial = {
  materialTrial: {
    type: 'void',
    'x-component': 'CollapseItem',
    'x-component-props': {
      title: i18nExpression('cusEntry.vendorMod.materialTrial')
    },
    'x-visible': generateCharExpressionByFunction(({ $form }) => {
      return $form.query('effectForm').get('data').ifMaterial === 'Y'
    }),
    properties: {
      layout: {
        type: 'void',
        'x-decorator': 'FormLayout',
        'x-decorator-props': {
          layout: 'vertical'
        },
        ...formGridSegment,
        properties: {
          extTrialFileId: {
            type: 'string',
            'x-decorator': 'FormItem',
            title: i18nExpression('cusEntry.vendorMod.materialTrialHand'),
            'x-component': 'SrmCommonFile',
            'x-component-props': {
              'extra-data': {
                uploadType: 'DEF',
                sourceType: 'WEB_APP',
                fileModular: 'sup',
                fileFunction: 'companyInfoMaintain',
                fileType: 'images'
              },
              'default-file': expression(`{
                fileId: $form.values.extTrialFileId,
                fileName: $form.values.extTrialFileName
              }`),
              '@on-change': expression(`({file}) => {
                const { fileId, fileName } = file || {}
                $form.values.extTrialFileId = fileId
                $form.values.extTrialFileName = fileName
              }`)
            },
            ...requiredValidatorSegment
          },
          extTrialFileName: {
            type: 'string',
            'x-hidden': true
          },
          extTrialFileRemarks: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.extTrialFileRemarks'),
            'x-decorator': 'FormItem',
            'x-decorator-props': {
              gridSpan: 4
            },
            'x-component-props': {
              type: 'textarea'
            }
          }
        }
      }
    }
  }
}
  
  