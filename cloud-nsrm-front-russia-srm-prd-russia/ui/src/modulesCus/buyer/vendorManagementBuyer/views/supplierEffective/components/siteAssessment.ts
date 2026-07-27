import {
  expression,
  i18nExpression,
  generateCharExpressionByFunction
} from '@meicloud/render-engine'
import {
  requiredValidatorSegment,
  formGridSegment
} from 'lib@/components/render-engine/schema-segments'

export const siteAssessment = {
  siteAssessment: {
    type: 'void',
    'x-component': 'CollapseItem',
    'x-component-props': {
      title: i18nExpression('cusEntry.vendorMod.siteAssessment')
    },
    'x-visible': generateCharExpressionByFunction(({ $form }) => {
      return $form.query('effectForm').get('data').ifSite === 'Y'
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
          extSiteFormFileId: {
            type: 'string',
            'x-decorator': 'FormItem',
            title: i18nExpression('cusEntry.vendorMod.siteAssessmentHand'),
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
                fileId: $form.values.extSiteFormFileId,
                fileName: $form.values.extSiteFormFileName
              }`),
              '@on-change': expression(`({file}) => {
                 const { fileId = null, fileName = null } = file || {}
                 $form.values.extSiteFormFileId = fileId
                 $form.values.extSiteFormFileName = fileName
              }`)
            },
            ...requiredValidatorSegment
          },
          extSiteFormFileName: {
            type: 'string',
            'x-hidden': true
          },
          extSiteFormRemarks: {
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.extSiteFormRemarks'),
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

