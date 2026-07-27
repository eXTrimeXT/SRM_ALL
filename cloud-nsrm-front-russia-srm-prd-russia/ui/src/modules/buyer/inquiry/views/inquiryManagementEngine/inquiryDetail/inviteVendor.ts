/**
 * @description 邀请供应商
 */
import { generateCharExpressionByFunction } from '@meicloud/render-engine'
import InviteVendor from 'lib@/compositionEngine/sourcing/inviteVendor'

const InviteVendorSegment = (scope: any): Record<any, any> => {
  console.log(scope, 'scope')
  return {
    inviteVendorTips: {
      type: 'void',
      'x-component': 'p',
      // 公开类型： 邀标类型为“公开”，无需邀请供应商，请进行下一步评分设定！
      // 'x-content': i18nExpression('bidMod.inviteVendorMessage')
      'x-content': generateCharExpressionByFunction(({ $form, $enum, $t }) => {
        return $form.values.publishScope === $enum.SOU_PUBLISH_SCOPE_ENUM.OPEN_TENDER
          ? '邀标类型为“公开”，无需邀请供应商，请进行下一步评分设定！'
          : $t('bidMod.inviteVendorMessage')
      })
    },

    inviteVendor: {
      type: 'void',
      'x-visible': generateCharExpressionByFunction(({ $form, $enum, $t }) => {
        return $form.values.publishScope === $enum.SOU_PUBLISH_SCOPE_ENUM.INVITE_TENDER
      }),
      properties: {
        ...InviteVendor({
          scope,
          showRecommendVendor: true,
          showSuppliersPermission: true
        })
      }
    }
  }
}

export default InviteVendorSegment
