import {expression, i18nExpression} from '@meicloud/render-engine'

import {
  formGridSegment
} from 'lib@/components/render-engine/schema-segments'
import {formMain} from "modb@/vendorManagementBuyer/views/vendorInfoChangeEngine/components/form";
import {companyType} from "modb@/vendorManagementBuyer/views/vendorInfoChangeEngine/components/companyType";
import {
  enterpriseThreeCertificates
} from "modb@/vendorManagementBuyer/views/vendorInfoChangeEngine/components/enterpriseThreeCertificates";
import {companyBaseInfo} from "modb@/vendorManagementBuyer/views/vendorInfoChangeEngine/components/companyBaseInfo";
import {contactData} from "modb@/vendorManagementBuyer/views/vendorInfoChangeEngine/components/contactData";
import {bankInfo} from "modb@/vendorManagementBuyer/views/vendorInfoChangeEngine/components/bankInfo";
import {financeInfo} from "mods@/vendorManagementSupplier/views/vendorInfoChangeEngine/components/financeInfo";
import {siteInfos} from "modb@/vendorManagementBuyer/views/vendorInfoChangeEngine/components/siteInfos";
import {
  sceneAttachmentInfo
} from "modb@/vendorManagementBuyer/views/vendorInfoChangeEngine/components/sceneAttachmentInfo";
import {attachFile} from "modb@/vendorManagementBuyer/views/vendorInfoChangeEngine/components/attachFile";

export const tabs = {
  tab1: {
    type: 'void',
    'x-component': 'FormTab.TabPane',
    'x-component-props': {
      ref: 'companyType',
      label: i18nExpression('vendorMod.companyType') // 企业性质
    },
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
  tab2: {
    type: 'void',
    'x-component': 'FormTab.TabPane',
    'x-component-props': {
      ref: 'enterpriseThreeCertificates',
      label: i18nExpression('vendorMod.enterpriseThreeCertificates') // 企业三证
    },
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
      label: i18nExpression('vendorMod.financeInfo'), // 财务信息
      class: ''
    },
    properties: {
      ...financeInfo
    }
  },
  tab7: {
    type: 'void',
    'x-component': 'FormTab.TabPane',
    'x-component-props': {
      ref: 'financeInfo',
      label: i18nExpression('vendorMod.vendorSiteInfos'), // 供应商地点信息
      class: ''
    },
    properties: {
      ...siteInfos
    }
  },
  tab8: {
    type: 'void',
    'x-component': 'FormTab.TabPane',
    'x-component-props': {
      ref: 'financeInfo',
      label: i18nExpression('vendorMod.sceneAttachmentInfo'), // 相关认证信息
      class: ''
    },
    properties: {
      ...sceneAttachmentInfo
    }
  },
  tab9: {
    type: 'void',
    'x-component': 'FormTab.TabPane',
    'x-component-props': {
      ref: 'financeInfo',
      label: i18nExpression('vendorMod.otherAttachInfo'), // 相关附件信息
      class: ''
    },
    properties: {
      ...attachFile
    }
  }
}
