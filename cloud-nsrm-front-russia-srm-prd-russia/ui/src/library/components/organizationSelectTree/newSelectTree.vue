<template>
  <treeselect
    :value="value"
    :normalizer="normalizer"
    :no-children-text="$t('dataConfMod.noChildrenText')"
    :no-options-text="$t('dataConfMod.noOptionsText')"
    :no-results-text="$t('dataConfMod.noResultsText')"
    :placeholder="placeholderText"
    :append-to-body="appendToBody"
    :searchable="true"
    :options="selectTreeOptions"
    :multiple="multiple"
    flatten-search-results
    :flat="flat"
    :disabled="disabled"
    @select="treeselectChange"
    @input="input"
  />
</template>
<script>
import Treeselect from '@riophae/vue-treeselect'
import '@riophae/vue-treeselect/dist/vue-treeselect.css'
import { findMenuInfoByPath } from '@/utils'
import { store, mutations } from '../organization-cascader/store'

export default {
  name: 'OrganizationSelectTree',
  components: { Treeselect },
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
    appendToBody: {
      type: Boolean,
      default: true
    },
    flat: {
      type: Boolean,
      default: true
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
      selectTreeOptions: []
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
      this.selectTreeOptions = store.treeWithFullPathId
    },
    treeselectChange (node, instanceId) {
      this.$emit('select', node, instanceId, this.scope)
    },
    input (value) {
      this.$emit('change', value)
    },
    normalizer (node) {
      const isDisabled =
        this.ableSelectTreeNodes
          .filter(i => !!i)
          .findIndex(j => j === node.fullPathId) === -1
      if (!isDisabled) console.log(node.fullPathId)
      const result = {
        id: node.fullPathId,
        label: node.organizationName,
        isDisabled
      }
      if (node && (node.childOrganRelation || []).length) {
        result.children = node.childOrganRelation
      }
      return result
    },
    async setAbleSelectTreeNodes () {
      const userType = this.$store.getters.userInfo.userType
      const userId = this.$store.getters.userInfo.userId
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
          .map(i => i.orgId)
      }
      return this.ableSelectTreeNodes
    }
  }
}
</script>
