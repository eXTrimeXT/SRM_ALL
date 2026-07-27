/**
 * @description 询价信息
 */
import { i18nExpression } from '@meicloud/render-engine'
import ContactInfoSegment from 'lib@/compositionEngine/sourcing/contactInfo'
import QuoteCurrencySegment from 'lib@/compositionEngine/sourcing/quoteCurrency'
import BaseInfoSegment from './projectInfo/baseInfo'
import EnclosuresWrapSegment from './projectInfo/enclosuresWrap'
import RecommendedVendorControlSegment from './projectInfo/recommendedVendorControl'
import BidingControlSegment from './projectInfo/bidingControl'

const ProjectInfoSegment: Record<any, any> = {
  projectInfoCollapse: {
    type: 'void',
    'x-component': 'Collapse',
    'x-component-props': {
      defaultOpenPanelCount: 1
    },
    properties: {
      // 项目信息
      baseInfo: {
        type: 'void',
        'x-component': 'CollapseItem',
        'x-component-props': {
          title: i18nExpression('bidMod.inquiryInfo')
        },
        properties: {
          ...BaseInfoSegment
        }
      },
      // 查看附件
      enclosuresWrap: {
        type: 'void',
        'x-component': 'CollapseItem',
        'x-component-props': {
          title: i18nExpression('bidMod.fileList')
        },
        properties: {
          ...EnclosuresWrapSegment
        }
      },
      // 商务信息
      quoteCurrency: {
        type: 'void',
        'x-component': 'CollapseItem',
        'x-component-props': {
          title: i18nExpression('bidMod.businessInfo')
        },
        properties: {
          ...QuoteCurrencySegment({ isVendorView: false })
        }
      },
      // 联系方式
      contactInfo: {
        type: 'void',
        'x-component': 'CollapseItem',
        'x-component-props': {
          title: i18nExpression('bidMod.contactInfo')
        },
        properties: {
          ...ContactInfoSegment({ setDefault: true })
        }
      },
      // 投标控制
      bidingControl: {
        type: 'void',
        'x-component': 'CollapseItem',
        'x-component-props': {
          title: i18nExpression('bidMod.bidingControl')
        },
        properties: {
          ...BidingControlSegment
        }
      },
      // 智能推荐供应商控制
      recommendedSupplierControl: {
        type: 'void',
        'x-component': 'CollapseItem',
        'x-component-props': {
          title: i18nExpression('bidMod.recommendSupControl')  // '智能推荐供应商控制'
        },
        properties: {
          ...RecommendedVendorControlSegment
        }
      }
    }
  }
}

export default ProjectInfoSegment
