/* eslint-disable quotes */
import {
  expression,
  i18nExpression
} from '@meicloud/render-engine'

export default {
  type: 'void',
  'x-component': 'CollapseItem',
  'x-component-props': {
    title: i18nExpression('purSettlementMod.addUploadFile')
  },
  properties: {
    fileUploads: {
      type: 'array',
      'x-query-engine-relation': 'fileUploads:*',
      'x-component': 'FileDynamic',
      'x-component-props': {
        'scene-module-code': 'SCENE_INVOICE_NOTICE_ATTACHMENT',
        primaryKey: 'sceneFileId',
        // 启用级联删除的储值行为
        cascadeDeletion: true,
        'business-id': expression('$values.invoiceNoticeId'),
        'editable': expression(`!$form.readPretty && !$form.query('InvoiceNotice').get('data').isFirstApproveShow`),
        'needInit': false
      }
      // 'x-reactions': expression(`field => {
      //   field.componentProps.editable = !$form.readPretty && !$form.query('InvoiceNotice').get('data').isFirstApproveShow
      // }`)
    }
  }
}
