import {
  expression,
  i18nExpression
} from "@meicloud/render-engine"

export const authInfo = {
  authInfo: {
    type: 'void',
    'x-component': 'CollapseItem',
    'x-component-props': {
      title: `{{observer(
        {
          render(h) {
            console.log(123)
            return h($$components.Note, {
              props: {
                title: t('cusEntry.vendorMod.authInfo'),
                value: $form.values.extRejectAttribute10,
                readonly: !($form.values.status === 'SUBMITTED' && $form.values.dataSources !== 'MANUALLY_CREATE' && $attrs.params.flag === 'edit')
              },
              on: {
                change: value => {
                  $form.values.extRejectAttribute10 = value
                }
              }
            })
          }
        }
      )}}`
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
              extraData: {
                fileModular: 'sup',
                fileFunction: 'companyInfoMaintain',
                fileType: 'images'
              },
              limit: 10,
              multiple: true,
              readonly: true
            }
          }
        }
      }
    }
  }
}
