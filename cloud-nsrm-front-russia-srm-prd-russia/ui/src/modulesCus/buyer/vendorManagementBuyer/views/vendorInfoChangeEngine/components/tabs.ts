import {expression, i18nExpression, generateCharExpressionByFunction} from '@meicloud/render-engine'

import {companyType} from 'modcb@/vendorManagementBuyer/views/vendorInfoChangeEngine/components/companyType'
import {
  enterpriseThreeCertificates
} from "modcb@/vendorManagementBuyer/views/vendorInfoChangeEngine/components/enterpriseThreeCertificates";
import {companyBaseInfo} from "modcb@/vendorManagementBuyer/views/vendorInfoChangeEngine/components/companyBaseInfo";
import {personBaseInfo} from "modcb@/vendorManagementBuyer/views/vendorInfoChangeEngine/components/personBaseInfo";
import {contactData} from "modcb@/vendorManagementBuyer/views/vendorInfoChangeEngine/components/contactData";
import {bankInfo} from "modcb@/vendorManagementBuyer/views/vendorInfoChangeEngine/components/bankInfo";
import {financeInfo} from "modcb@/vendorManagementBuyer/views/vendorInfoChangeEngine/components/financeInfo";
import {siteInfos} from "modcb@/vendorManagementBuyer/views/vendorInfoChangeEngine/components/siteInfos";
import {serviceRange} from "modcb@/vendorManagementBuyer/views/vendorInfoChangeEngine/components/serviceRange"
import {qualificationInformation} from "modcb@/vendorManagementBuyer/views/vendorInfoChangeEngine/components/qualificationInformation"
import {attachFile} from "modcb@/vendorManagementBuyer/views/vendorInfoChangeEngine/components/attachFile"
import { authInfo } from "modcb@/vendorManagementBuyer/views/vendorInfoChangeEngine/components/authInfo"
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
  tab5: {
    type: 'void',
    'x-component': 'FormTab.TabPane',
    'x-component-props': {
      ref: 'bankInfo',
      label: i18nExpression('vendorMod.bankInfo'), // 银行信息
      class: ''
    },
    properties: {
      ...bankInfo
    }
  },
  tab6: {
    type: 'void',
    'x-component': 'FormTab.TabPane',
    'x-component-props': {
      ref: 'financeInfo',
      label: i18nExpression('cusEntry.vendorMod.financeReport'), // 财务信息
    },
    'x-visible': generateCharExpressionByFunction(({ $form }) => {
      return $form.query('state').get('data').userType !== 'PERSONAL'
    }),
    properties: {
      ...financeInfo
    }
  },
  tab7: {
    type: 'void',
    'x-component': 'FormTab.TabPane',
    'x-component-props': {
      ref: 'companySize',
      label: i18nExpression('cusEntry.vendorMod.companySize'), // 公司规模
    },
    'x-visible': generateCharExpressionByFunction(({ $form }) => {
      return $form.query('state').get('data').userType !== 'PERSONAL'
    }),
    properties: {
      ...siteInfos
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
    'x-visible': generateCharExpressionByFunction(({ $form }) => {
      return $form.query('state').get('data').userType !== 'PERSONAL'
    }),
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
      ref: 'financeInfo',
      label: i18nExpression('vendorMod.otherAttachInfo'), // 其他附件信息
      class: ''
    },
    'x-visible': generateCharExpressionByFunction(({ $form }) => {
      return $form.query('state').get('data').userType !== 'PERSONAL'
    }),
    properties: {
      ...attachFile
    }
  }
}
