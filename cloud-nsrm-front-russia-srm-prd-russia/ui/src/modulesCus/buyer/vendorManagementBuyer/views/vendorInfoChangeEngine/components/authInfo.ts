import {
  expression,
  i18nExpression,
} from "@meicloud/render-engine"
import {
  requiredValidatorSegment
} from 'lib@/components/render-engine/schema-segments'
export const authInfo = {
  beforeChange: {
    type: 'void',
    'x-component': 'div',
    'x-component-props': {
      class: 'formClassAllChange'
    },
    properties: {
      beforeChangeTitle: {
        type: 'void',
        'x-component': 'changeTitle',
        'x-component-props': {
          language: 'supplierChange.beforeChange'
        }
      },
      authInfoBefore: {
        type: 'object',
        'x-decorator': 'FormLayout',
        'x-decorator-props': {
          layout: 'vertical',
          class: 'authInfo-form-layout'
        },
        'x-component': 'FormGrid',
        'x-component-props': {
          maxColumns: 3,
          columnGap: 32,
          rowGap: 0
        },
        properties: {
          protocolTemplateName: {
            type: 'string',
            'x-decorator': 'FormItem',
            'x-hidden': true,
          },
          protocolTemplateId:{
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.sunProtocol'),
            'x-component': "SrmCommonFile",
            'x-decorator': 'FormItem',
            'x-component-props': {
              extraData: {
                fileModular: 'sup',
                fileFunction: 'companyInfoMaintain',
                fileType: 'images'
              },
              defaultFile: {
                fileId: expression('$self.value'),
                fileName: expression(`$self.query('.protocolTemplateName').get('value')`)
              },
              readonly: true
            }
          },
          sunshineFileName: {
            type: 'string',
            'x-hidden': true
          },
          sunshineFileId:{
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.protocolUpload'),
            'x-component': 'SrmCommonFile',
            'x-decorator': 'FormItem',
            'x-reactions': {
              "target": "authInfoBefore.sunshineFileName",
              "effects": ["onFieldValueChange"],
              "fulfill": {
                "run": "$showBeforeSunFile($self)"
              }
            },
            'x-component-props': {
              limit: 10,
              multiple: true,
              extraData: {
                fileModular: 'sup',
                fileFunction: 'companyInfoMaintain',
                fileType: 'images'
              },
              readonly: true
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
      afterChangeTitle: {
        type: 'void',
        'x-component': 'changeTitle',
        'x-component-props': {
          language: 'supplierChange.afterChange'
        }
      },
      authInfoAfter: {
        type: 'object',
        'x-decorator': 'FormLayout',
        'x-decorator-props': {
          layout: 'vertical',
          class: 'authInfo-form-layout'
        },
        'x-component': 'FormGrid',
        'x-component-props': {
          maxColumns: 3,
          columnGap: 32,
          rowGap: 0
        },
        properties: {
          protocolTemplateName: {
            type: 'string',
            'x-decorator': 'FormItem',
            'x-hidden': true,
          },
          protocolTemplateId:{
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.sunProtocol'),
            'x-component': "SrmCommonFile",
            'x-decorator': 'FormItem',
            'x-component-props': {
              extraData: {
                fileModular: 'sup',
                fileFunction: 'companyInfoMaintain',
                fileType: 'images'
              },
              defaultFile: {
                fileId: expression('$self.value'),
                fileName: expression(`$self.query('.protocolTemplateName').get('value')`)
              },
              readonly: true
            }
          },
          sunshineFileName: {
            type: 'string',
            'x-hidden': true
          },
          sunshineFileId:{
            type: 'string',
            title: i18nExpression('cusEntry.vendorMod.protocolUpload'),
            'x-component': 'SrmCommonFile',
            'x-decorator': 'FormItem',
            'x-reactions': {
              "target": "authInfoAfter.sunshineFileName",
              "effects": ["onFieldValueChange"],
              "fulfill": {
                "run": "$showSunFile($self, $form)"
              }
            },
            'x-component-props': {
              limit: 10,
              multiple: true,
              extraData: {
                fileModular: 'sup',
                fileFunction: 'companyInfoMaintain',
                fileType: 'images'
              },
              '@on-change': expression(`({fileList}) => {
                $form.values.authInfoAfter.sunshineFileId = fileList?.map(item => item.fileId).join()
                $form.values.authInfoAfter.sunshineFileName = fileList?.map(item => item.fileName).join()
              }`),
              readonly: expression('$form.readPretty')
            },
            ...requiredValidatorSegment
          }
        }
      }
    }
  }
}
