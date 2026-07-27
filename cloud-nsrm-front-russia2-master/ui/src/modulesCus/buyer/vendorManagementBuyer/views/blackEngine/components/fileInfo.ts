import {
  i18nExpression
} from '@meicloud/render-engine'

export default {
  type: 'void',
  'x-component': 'CollapseItem',
  'x-component-props': {
    title: i18nExpression('bidMod.fileInfo')
  },
  properties: {
    fileUploads: {
      type: 'array',
      'x-query-engine-relation': 'fileUploads:*',
      'x-component': 'FileDynamic',
      'x-component-props': {
        primaryKey: 'blackId',
        // 启用级联删除的储值行为
        cascadeDeletion: true,
        'scene-module-code': 'SCENE_BLACK_ATTACHMENT',
        'business-id': `{{
          $attrs?.params?.row?.blackId?.blackId || $form.values?.blackId || null
        }}`,
        editable: '{{!$form.readPretty}}',
        'need-init': true
      }
    }
  }
}
