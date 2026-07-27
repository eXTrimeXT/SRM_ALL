import {
  expression,
  i18nExpression,
} from "@meicloud/render-engine"
import {
  requiredValidatorSegment
} from 'lib@/components/render-engine/schema-segments'
export const authInfo = {
  authInfo: {
    type: 'void',
    'x-component': 'CollapseItem',
    'x-component-props': {
      title: i18nExpression('cusEntry.vendorMod.authInfo')
    },
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
                fileName: expression(`$form.query($self.parent.address.concat('protocolTemplateName')).get('value')`)
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
              "target": ".sunshineFileName",
              "effects": ["onFieldValueChange"],
              "fulfill": {
                "run": "$showSunFile($self)"
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
                $form.values.sunshineFileId = fileList?.map(item => item.fileId).join()
                $form.values.sunshineFileName = fileList?.map(item => item.fileName).join()
                $self.setComponentProps({
                  fileList
                })
              }`),
              readonly: expression(`$form.query('state').get('data').$disabled`)
            },
            ...requiredValidatorSegment
          }
        }
      }
    }
  }
}
