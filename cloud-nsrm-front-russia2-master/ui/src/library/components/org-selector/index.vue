<template>
  <el-select
    :value="value"
    :multiple="multiple"
    clearable
    filterable
    style="width: 100%;"
    @change="change"
  >
    <el-option
      v-for="node in suggestions"
      :key="node.uid"
      :value="node.value"
      :label="node.text"
    />
  </el-select>
</template>
<script>
import Node from './node'
import { coerceTruthyValueToArray, valueEquals, walk } from './utils'
import { findMenuInfoByPath } from '@/utils'
import { getInfoByParam, newOrganaztionTreehttp } from '@/api/common'

const flatNodes = (data, leafOnly) => {
  return data.reduce((res, node) => {
    if (node.isLeaf) {
      res.push(node)
    } else {
      !leafOnly && res.push(node)
      res = res.concat(flatNodes(node.children, leafOnly))
    }
    return res
  }, [])
}

export default {
  name: 'OrgSelector',
  model: {
    prop: 'value',
    event: 'change'
  },
  props: {
    value: {},
    // { value: '', label: '', children: '', disabled: '', checkStrictly: true }
    config: {
      type: Object,
      default: () => ({
        value: 'fullPathId',
        label: 'organizationName',
        children: 'childOrganRelation',
        disabled: 'disabled',
        checkStrictly: true
      })
    },
    separator: {
      type: String,
      default: '/'
    },
    showAllLevels: {
      type: Boolean,
      default: true
    },
    multiple: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      suggestions: [],
      flattedNodes: [],
      nodes: [],
      options: [],
      selections: null
    }
  },
  watch: {
    options: {
      handler: function () {
        this.initNodes()
        this.initSuggestions()
      },
      immediate: true,
      deep: true
    }
  },
  async mounted () {
    const menus = this.$store.getters.userInfo.menus
    this.menuInfo = findMenuInfoByPath(this.$route.path, menus) || {}
    await this.setAbleSelectTreeNodes()
    this.initOptions()
  },
  methods: {
    async setAbleSelectTreeNodes () {
      const userType = this.$store.getters.userInfo.userType
      // 采购商
      if (userType === 'BUYER') {
        // 如果菜单设置了组织管控维度就查询可选的组织节点
        if (this.menuInfo.orgControlDim) {
          const list = await this.$http({
            url: '/api-base/organization/relation/selectTreeByType',
            method: 'GET',
            params: { organizationTypeCode: this.menuInfo.orgControlDim }
          })
          this.ableSelectTreeNodes = list.data.map(i => i.fullPathId)
        } else {
          // 如果没有设置组织管控维度就直接使用角色的组织权限
          const { organizationUsers } = this.$store.getters.userInfo
          this.ableSelectTreeNodes = organizationUsers.map(i => i.fullPathId)
        }
      } else {
        // 供应商
        const companyId = this.$store.getters.user.companyId
        const res = await getInfoByParam({ companyId })
        this.ableSelectTreeNodes = (res.data.orgInfos || [])
          .filter(i => i.serviceStatus === 'EFFECTIVE') // FIXME: 接口过滤有效的合作组织
          .map(i => i.fullPathId)
      }
      return this.ableSelectTreeNodes
    },
    initOptions () {
      // 组织架构
      newOrganaztionTreehttp({}).then(res => {
        walk(res.data, (data, deep) => {
          return { ...data }
        })
        this.options = walk(res.data, data => {
          const { fullPathId, childOrganRelation } = data
          const disabled = !this.ableSelectTreeNodes.includes(fullPathId)
          return { ...data, disabled }
        })
      })
    },
    change (value) {
      if (this.multiple) {
        this.selections = value.map(i =>
          this.suggestions.find(j => j.value === i)
        )
      } else {
        this.selections = this.suggestions.find(i => i.value === value)
      }
      this.$emit('change', value, this.selections)
    },
    initNodes () {
      const data = coerceTruthyValueToArray(this.options)
      this.nodes = data.map(
        nodeData =>
          new Node(nodeData, { ...this.config, multiple: this.multiple })
      )
      this.flattedNodes = flatNodes(this.nodes, false)
    },
    initSuggestions () {
      const suggestions = this.flattedNodes.filter(node => {
        if (node.isDisabled) return false
        node.text = node.getText(this.showAllLevels, this.separator) || ''
        return true
      })
      this.suggestions = suggestions
    }
  }
}
</script>
