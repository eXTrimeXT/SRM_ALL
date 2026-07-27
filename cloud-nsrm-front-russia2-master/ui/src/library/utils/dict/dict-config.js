import http from '@/utils/axios/http'
import { createCaches } from '@/utils/cache'
const getUrl = path => `${path}`

const DICT_CONFIG = {
  SET_DICTIONARY: 'setDictionary',
  SET_DICT_MAP: 'setDictMap',
  LOAD_DICTIONARY: 'loadDictionary'
}

export const SELECT_TYPE = {
  TAX_RATE_MATERIAL: {
    apiFunction: async params =>
      http({
        method: 'GET',
        url: getUrl('/api-base/material/materialItem/queryTaxByItem'),
        params: params
      }),
    transferParams: code => {
      return { materialId: code }
    },
    transformOptions: element => {
      let label = element.taxName
        ? `${element.taxKey} / ${element.taxName}`
        : element.taxKey
      return {
        id: element.taxId.toString(),
        value: element.taxKey, // 值
        label: label,
        key: element.taxCode // 文字
      }
    }
  },
  MODULE_TABLE_NAME: {
    apiFunction: async params =>
      http({
        method: 'GET',
        url: getUrl('/api-base/global/tool/listTablesInfo'),
        params: params
      }),
    transferParams: code => {
      return { module: code }
    },
    transformOptions: element => {
      return {
        id: element.id,
        value: element.tableName, // 值
        label: element.desc,
        key: element.id // 文字
      }
    }
  },
  ORG_TYPE: {
    apiFunction: async () =>
      http({
        url: getUrl('/api-base/organization/organizationsType/getOrgTypeByUser'),
        loading: true,
        method: 'GET'
      }),
    transferParams: code => {
      return { }
    },
    transformOptions: element => {
      const { organizationTypeCode, organizationTypeName, isDefault } = element
      return {
        id: `org_type_${organizationTypeCode}`,
        label: organizationTypeName,
        value: organizationTypeCode,
        key: organizationTypeCode,
        element: element
      }
    }
  },
  ORG_TYPE_ALL: {
    apiFunction: async () =>
      http({
        url: getUrl('/api-base/organization/organizationsType/listAll'),
        loading: true,
        unToken: true,
        method: 'POST'
      }),
    transferParams: code => {
      return { }
    },
    transformOptions: element => {
      const { organizationTypeCode, organizationTypeName } = element
      return {
        id: `org_type_${organizationTypeCode}`,
        label: organizationTypeName,
        value: organizationTypeCode,
        key: organizationTypeCode,
        element: element
      }
    }
  },
  CATEGORY: {
    apiFunction: async () =>
      http({
        url: getUrl('/api-base/dict/base-dict-item/queryProductType'),
        loading: true,
        method: 'GET'
      }),
    transferParams: code => {
      return { }
    },
    transformOptions: element => {
      const { dictItemCode, dictItemName } = element
      return {
        id: `level_${dictItemCode}`,
        label: dictItemName,
        value: dictItemCode,
        key: dictItemCode
      }
    }
  },
  CONDFACTOR: {
    apiFunction: async () =>
      http({
        url: getUrl('/api-cm/cond-factor/listAll'),
        loading: true,
        method: 'GET'
      }),
    transferParams: code => {
      return { }
    },
    transformOptions: element => {
      const { condFactorId, condFactor } = element
      return {
        id: condFactorId,
        label: condFactor,
        value: condFactorId,
        key: condFactorId
      }
    }
  },
  ELEMNAME: {
    apiFunction: async () =>
      http({
        url: getUrl('/api-cm/elem-maintain/listAll'),
        loading: true,
        method: 'GET'
      }),
    transferParams: code => {
      return { }
    },
    transformOptions: element => {
      const { elemMaintainId, elemCode, elemName } = element
      return {
        id: elemMaintainId,
        label: elemName,
        value: elemName,
        key: elemCode
      }
    }
  },
  payExplain: {
    apiFunction: async () =>
      http({
        url: getUrl('/api-cm/template/payType/getActivationPaymentTerms'),
        loading: true,
        method: 'GET'
      }),
    transferParams: code => {
      return { }
    },
    transformOptions: element => {
      const { payTypeId, payExplain } = element
      return {
        id: payTypeId.toString(),
        label: payExplain,
        value: payTypeId.toString(),
        key: payTypeId.toString()
      }
    }
  },
  condFactorList: {
    apiFunction: async () =>
      http({
        url: getUrl('/api-cm/cond-factor/listAll'),
        loading: true,
        method: 'GET'
      }),
    transferParams: code => {
      return { }
    },
    transformOptions: element => {
      const { condFactorId, condFactor } = element
      return {
        id: condFactorId,
        label: condFactor,
        value: condFactorId,
        key: condFactorId
      }
    }
  },
  COMPANY_DEPT: {
    apiFunction: async params =>
      http({
        url: getUrl('/api-base/base/org_company_dept/listAll'),
        method: 'POST',
        data: params,
        loading: true
      }),
    transferParams: code => {
      return { organizationId: code }
    },
    transformOptions: element => {
      return {
        id: element.deptCode,
        label: element.deptName,
        value: element.deptCode,
        element: element
      }
    }
  },
  OU_DEPT: { // 业务实体获取部门
    apiFunction: async params =>
      http({
        url: getUrl('/api-base/base/org_company_dept/listByOu'),
        method: 'POST',
        data: params,
        loading: true
      }),
    transferParams: code => {
      return { ouId: code }
    },
    transformOptions: element => {
      return {
        id: element.deptCode,
        label: element.deptName,
        value: element.deptCode,
        element: element
      }
    }
  },
  // 收货地址维护
  RECEIVE_ADDRESS: {
    apiFunction: createCaches(data => http({
      url: getUrl('/api-base/base/site/listSiteByCondition'),
      method: 'POST',
      data
    }), 'organizationId'),
    transferParams: code => {
      return {
        siteType: 'RECEIVE_ADDRESS',
        organizationId: code
      }
    },
    transformOptions: element => {
      return {
        label: element.siteName,
        value: element.siteName,
        element: element
      }
    }
  },
  LANGUAGE_LIST: {
    apiFunction: async params =>
      http({
        url: getUrl('/api-base/dict/base-dict-language/listAll'),
        method: 'POST',
        data: params,
        loading: true
      }),
    transferParams: code => {
      return { queryDatasource: code }
    },
    transformOptions: element => {
      return {
        label: element.languageName,
        value: element.language
      }
    }
  },
  PROVINCE: {
    apiFunction: async params =>
      http({
        url: getUrl('/api-base/base/region/queryBaseRegion'),
        method: 'POST',
        params: params,
        loading: false
      }),
    transferParams: code => {
      return { queryType: 'province', parentId: code }
    },
    transformOptions: element => {
      return {
        id: element.provinceId,
        value: element.provinceId.toString(),
        label: element.province
      }
    }
  },
  CITY: {
    apiFunction: async params =>
      http({
        url: getUrl('/api-base/base/region/queryBaseRegion'),
        method: 'POST',
        params: params,
        loading: false
      }),
    transferParams: code => {
      return { queryType: 'city', parentId: code }
    },
    transformOptions: element => {
      return {
        id: element.cityId,
        value: element.cityId.toString(),
        label: element.city
      }
    }
  },
  SECOND_DICT: {
    apiFunction: async params =>
      http({
        url: getUrl('/api-base/dict/base-dict-item/listSecondByDictCode'),
        method: 'POST',
        data: params,
        loading: true
      }),
    transferParams: code => {
      return { dictCode: code }
    },
    transformOptions: element => {
      return {
        id: element.dictItemId,
        value: element.dictItemCode,
        label: element.dictItemName
      }
    }
  },
  ORG_INFO: {
    apiFunction: async params =>
      http({
        url: getUrl('/api-pj/organization/organization/listAllOrganization'),
        method: 'POST',
        data: params,
        loading: true
      }),
    transferParams: code => {
      return {
        organizationTypeCode: 'OU',
        pageNum: 1,
        pageSize: 100
      }
    },
    transformOptions: element => {
      return {
        id: element.organizationId,
        value: element.organizationId.toString(),
        label: element.organizationName
      }
    }
  }
}

export default DICT_CONFIG
