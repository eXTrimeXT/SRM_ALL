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
        'scene-module-code': 'SCENE_DELIVERY_NOTE_ATTACHMENT',
        'business-id': `{{
          $attrs?.params?.row?.deliveryNoticeId || $form.values.deliveryNoticeId || null
        }}`,
        editable: false,
        'need-init': false
      }
    }
  }
}
