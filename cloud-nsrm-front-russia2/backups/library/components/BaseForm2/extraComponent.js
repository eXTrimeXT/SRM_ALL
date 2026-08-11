import BaseSelect from './BaseSelect'
import OrganizationSelectTree from 'lib@/components/organization-cascader'

export default {
  BaseSelect,
  OrganizationSelectTree
}

// 所以组件继承自BASIC，建立映射关系
export const basic = {
  input: {
    component: 'el-input',
    uiAttrs: {
      placeholder: '请输入',
      clearable: true
    }
  },
  select: {
    component: 'base-select', // 自定义组件,为了在el-select添加options能够生成el-options节点
    uiAttrs: {
      placeholder: '请选择',
      clearable: true
    }
  },
  date: {
    component: 'el-date-picker',
    uiAttrs: {
      placeholder: '选择日期',
      clearable: true,
      type: 'date',
      format: 'yyyy/MM/dd',
      'value-format': 'yyyy/MM/dd'
    }
  },
  checkbox: {
    component: 'el-checkbox'
  },
  cascader: {
    component: 'el-cascader',
    uiAttrs: {
      placeholder: '请选择',
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
  }
}
