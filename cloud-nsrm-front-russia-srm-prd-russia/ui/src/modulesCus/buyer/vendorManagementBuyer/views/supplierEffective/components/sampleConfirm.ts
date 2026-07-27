import {
    expression,
    i18nExpression,
    generateCharExpressionByFunction
  } from '@meicloud/render-engine'
  import {
    requiredValidatorSegment,
    formGridSegment
  } from 'lib@/components/render-engine/schema-segments'
  
  export const sampleConfirm = {
    sampleConfirm: {
      type: 'void',
      'x-component': 'CollapseItem',
      'x-component-props': {
        title: i18nExpression('cusEntry.vendorMod.sampleConfirm')
      },
      'x-visible': generateCharExpressionByFunction(({ $form }) => {
        return $form.query('effectForm').get('data').ifSample === 'Y'
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
            extSampleFileId: {
              type: 'string',
              'x-decorator': 'FormItem',
              title: i18nExpression('cusEntry.vendorMod.sampleConfirmhand'),
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
                  fileId: $form.values.extSampleFileId,
                  fileName: $form.values.extSampleFileName
                }`),
                '@on-change': expression(`({file}) => {
                  const { fileId, fileName } = file || {}
                  $form.values.extSampleFileId = fileId
                  $form.values.extSampleFileName = fileName
                }`)
              },
              ...requiredValidatorSegment
            },
            extSampleFileName: {
              type: 'string',
              'x-hidden': true
            },
            extSampleFileRemarks: {
              type: 'string',
              title: i18nExpression('cusEntry.vendorMod.extSampleFileRemarks'),
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
  
  