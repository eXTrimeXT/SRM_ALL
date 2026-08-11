import i18n from '@/lang'
import OrganizationSelectTree from 'lib@/components/organization-cascader'
import OrganizationSelector from 'lib@/components/organization-selector'
import QuickSearch from 'lib@/components/QuickSearch'; // 快速查询组件
import BaseSelect from './BaseSelect'

export default {
  BaseSelect,
  OrganizationSelectTree,
  OrganizationSelector,
  QuickSearch
}

// 所以组件继承自BASIC，建立映射关系
export const basic = {
  input: {
    component: 'el-input',
    uiAttrs: {
      placeholder: i18n.t('common.pleaseInput'), // 请输入
      clearable: true
    }
  },
  select: {
    component: 'base-select', // 自定义组件,为了在el-select添加options能够生成el-options节点
    uiAttrs: {
      placeholder: i18n.t('common.pleaseSelect'), // 请选择
      clearable: true
    }
  },
  dictSelect: {
    component: 'dict-select', // 自定义组件,为了在el-select添加options能够生成el-options节点
    uiAttrs: {
      placeholder: i18n.t('common.pleaseSelect'), // 请选择
      clearable: true
    }
  },
  date: {
    component: 'el-date-picker',
    uiAttrs: {
      placeholder: i18n.t('purchaseDemand.datePicker'), // 选择日期
      clearable: true,
      type: 'date',
      'value-format': 'yyyy-MM-dd'
    }
  },
  checkbox: {
    component: 'el-checkbox'
  },
  cascader: {
    component: 'el-cascader',
    uiAttrs: {
      placeholder: i18n.t('common.pleaseSelect'), // 请选择
      clearable: true
    }
  },
  textarea: {
    component: 'el-input',
    uiAttrs: {
      type: 'textarea'
    }
  },
  orgSelect: {
    component: 'OrganizationSelectTree',
    uiAttrs: {}
  },
  organizationSelector: {
    component: 'OrganizationSelector',
    uiAttrs: {
      placeholder: i18n.t('common.pleaseSelect')
    }
  },
  quickSearch: {
    component: 'QuickSearch',
    uiAttrs: {}
  }
}
