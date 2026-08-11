<template>
  <el-cascader
    style="width: 100%;"
    :options="selectTreeOptions"
    :filterable="filterable"
    :disabled="disabled"
    :placeholder="placeholderText"
    ref="organizationComps"
    :clearable="clearable"
    :separator="separator"
    :collapse-tags="collapseTags"
    :props="{
      checkStrictly: true,
      value: 'fullPathId',
      label: 'organizationName',
      children: 'childOrganRelation',
      emitPath: false,
      expandTrigger: expandTrigger,
      multiple: multiple
    }"
    :value="value"
    @change="treeselectChange"
  />
</template>
<script>
import { findMenuInfoByPath } from '@/utils'
import { store, mutations } from './store'

function walk (list, callback, deep = 0) {
  return list.map(it => {
    const result = callback({ ...it }, deep)
    if (it.childOrganRelation && it.childOrganRelation.length) {
      result.childOrganRelation = walk(
        it.childOrganRelation,
        callback,
        deep + 1
      )
    } else {
      delete it.childOrganRelation
    }
    return result
  })
}

export default {
  name: 'OrganizationSelectTree',
  components: {},
  model: {
    event: 'change',
    value: 'value'
  },
  props: {
    disabled: {
      type: Boolean,
      default: false
    },
    value: {
      type: [String, Number],
      default: null
    },
    placeholder: {
      type: String,
      default: ''
    },
    separator: {
      type: String,
      default: '/'
    },
    expandTrigger: {
      type: String,
      default: 'click'
    },
    clearable: {
      type: Boolean,
      default: true
    },
    filterable: {
      type: Boolean,
      default: true
    },
    collapseTags: {
      type: Boolean,
      default: false
    },
    multiple: {
      type: Boolean,
      default: false
    },
    scope: {
      type: Object,
      default: () => {}
    }
  },
  data () {
    return {
      menuInfo: {},
      ableSelectTreeNodes: [],
      selectTreeOptions: [],
      time: 0
    }
  },
  computed: {
    placeholderText () {
      return this.placeholder || this.$t('dataConfMod.msgSelectOrganation')
    }
  },
  async mounted () {
    const menus = this.$store.getters.userInfo.menus
    this.menuInfo = findMenuInfoByPath(this.$route.path, menus) || {}
    // 设置可选的节点
    await this.setAbleSelectTreeNodes()
    // 初始化完成组织架构
    this.initSelectTree()
    // 通过当前路由path查询当前菜单配置
  },
  methods: {
    async initSelectTree () {
      // 组织架构
      await mutations.fetchTreeWithFullPathId()
      walk(store.treeWithFullPathId, (data, deep) => {
        return { ...data }
      })
      this.selectTreeOptions = walk(store.treeWithFullPathId, data => {
        const { fullPathId, childOrganRelation } = data
        const disabled = !this.ableSelectTreeNodes.includes(fullPathId)
        return { ...data, disabled }
      })
    },
    treeselectChange (value) {
      if (this.multiple) {
        const nodes = []
        walk(this.selectTreeOptions, data => {
          if (value.includes(data.fullPathId)) {
            nodes.push({ ...data })
          }
          return { ...data }
        })
        console.log('organization-cascader multiple: ', nodes)
        this.$emit('select', nodes, value, this.scope)
      } else {
        let node = {}
        walk(this.selectTreeOptions, data => {
          if (data.fullPathId === value) {
            node = data
          }
          return { ...data }
        })
        console.log('organization-cascader radio: ', node)
        this.$emit('select', node, value, this.scope)
      }
      this.$emit('change', value)
    },
    async setAbleSelectTreeNodes () {
      const userId = this.$store.getters.userInfo.userId
      const userType = this.$store.getters.userInfo.userType
      // 采购商
      if (userType === 'BUYER') {
        // 如果菜单设置了组织管控维度就查询可选的组织节点
        if (this.menuInfo.orgControlDim) {
          const organizationTypeCode = this.menuInfo.orgControlDim
          const key = `${organizationTypeCode}_${userId}`
          await mutations.fetchBuyerPermission(organizationTypeCode, userId)
          this.ableSelectTreeNodes = store.buyerPermission[key].map(
            i => i.fullPathId
          )
        } else {
          // 如果没有设置组织管控维度就直接使用角色的组织权限
          const { organizationUsers } = this.$store.getters.userInfo
          this.ableSelectTreeNodes = organizationUsers.map(i => i.fullPathId)
        }
      } else {
        // 供应商
        const companyId = this.$store.getters.user.companyId
        const key = `${companyId}_${userId}`
        await mutations.fetchVendorPermission(companyId, userId)
        this.ableSelectTreeNodes = (store.vendorPermission[key].orgInfos || [])
          .filter(i => i.serviceStatus === 'EFFECTIVE') // FIXME: 接口过滤有效的合作组织
          .map(i => i.fullPathId)
      }
      return this.ableSelectTreeNodes
    }
  }
}
</script>
