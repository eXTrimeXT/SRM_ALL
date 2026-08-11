import {expression, i18nExpression, generateCharExpressionByFunction} from '@meicloud/render-engine'

import { companyType } from './companyType'
import {
  enterpriseThreeCertificates
} from './enterpriseThreeCertificates'
import { companyBaseInfo } from './companyBaseInfo'
import { personBaseInfo } from './personBaseInfo'
import { contactData } from './contactData'
import { serviceRange } from './serviceRange'
import { qualificationInformation } from './qualificationInformation'
import { authInfo } from './authInfo'
import { attachFile } from './attachFile'

export const tabs = {
  tab1: {
    type: 'void',
    'x-component': 'FormTab.TabPane',
    'x-component-props': {
      ref: 'companyType',
      label: i18nExpression('vendorMod.companyType') // 企业性质
    },
    'x-visible': generateCharExpressionByFunction(({ $form }) => {
      return $form.query('state').get('data').userType !== 'PERSONAL'
    }),
    properties: {
      div: {
        type: 'void',
        'x-component': 'div',
        'x-component-props': {
          class: 'formClassWrap'
        },
        properties: {
          ...companyType
        }
      }
    }
  },
  tab11: {
    type: 'void',
    'x-component': 'FormTab.TabPane',
    'x-component-props': {
      ref: 'companyType',
      label: i18nExpression('cusEntry.vendorMod.baseInfo') // 基本信息
    },
    'x-visible': generateCharExpressionByFunction(({ $form }) => {
      return $form.query('state').get('data').userType === 'PERSONAL'
    }),
    properties: {
      div: {
        type: 'void',
        'x-component': 'div',
        'x-component-props': {
          class: 'formClassWrap'
        },
        properties: {
          ...personBaseInfo
        }
      }
    }
  },
  tab2: {
    type: 'void',
    'x-component': 'FormTab.TabPane',
    'x-component-props': {
      ref: 'enterpriseThreeCertificates',
      label: i18nExpression('vendorMod.enterpriseThreeCertificates') // 企业三证
    },
    'x-visible': generateCharExpressionByFunction(({ $form }) => {
      return $form.query('state').get('data').userType !== 'PERSONAL'
    }),
    properties: {
      div: {
        type: 'void',
        'x-component': 'div',
        'x-component-props': {
          class: 'formClassWrap'
        },
        properties: {
          ...enterpriseThreeCertificates
        }
      }
    }
  },
  tab3: {
    type: 'void',
    'x-component': 'FormTab.TabPane',
    'x-component-props': {
      ref: 'companyBaseInfo',
      label: i18nExpression('vendorMod.companyBaseInfo') // 企业基本信息
    },
    'x-visible': generateCharExpressionByFunction(({ $form }) => {
      return $form.query('state').get('data').userType !== 'PERSONAL'
    }),
    properties: {
      div: {
        type: 'void',
        'x-component': 'div',
        'x-component-props': {
          class: 'formClassWrap'
        },
        properties: {
          ...companyBaseInfo
        }
      }
    }
  },
  tab4: {
    type: 'void',
    'x-component': 'FormTab.TabPane',
    'x-component-props': {
      ref: 'contactInfo',
      label: i18nExpression('vendorMod.contactInfo'), // 联系人信息
      class: ''
    },
    properties: {
      ...contactData
    }
  },
  tab8: {
    type: 'void',
    'x-component': 'FormTab.TabPane',
    'x-component-props': {
      ref: 'serviceRange',
      label: i18nExpression('cusEntry.vendorMod.serviceRange'), // 服务范围
    },
    properties: {
      ...serviceRange
    }
  },
  tab9: {
    type: 'void',
    'x-component': 'FormTab.TabPane',
    'x-component-props': {
      ref: 'qualificationInformation',
      label: i18nExpression('cusEntry.vendorMod.qualificationInformation'), // 资质信息
      class: ''
    },
    properties: {
      ...qualificationInformation
    }
  },
  // tab12: {
  //   type: 'void',
  //   'x-component': 'FormTab.TabPane',
  //   'x-component-props': {
  //     ref: 'authInfo',
  //     label: i18nExpression('cusEntry.vendorMod.authInfo'), // 验证信息
  //     class: 'authInfo-layout'
  //   },
  //   properties: {
  //     ...authInfo
  //   }
  // },
  tab10: {
    type: 'void',
    'x-component': 'FormTab.TabPane',
    'x-component-props': {
      ref: 'otherAttachInfo',
      label: i18nExpression('vendorMod.otherAttachInfo'), // 其他附件信息
      class: ''
    },
    properties: {
      ...attachFile
    }
  }
}
