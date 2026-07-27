<template>
  <el-table
    :data="selectTreeData"
    style="width: 100%"
    border
    height="365px"
    highlight-current-row
    @selection-change="handleVendoeSelection"
  >
    <el-table-column
      align="center"
      type="index"
      width="50"
    />
    <el-table-column
      type="selection"
      width="55"
    />
    <el-table-column
      align="center"
      prop="organizationId"
      :label="$t('components.orgSelection.headers.orgCode')"
      width="250"
      :show-overflow-tooltip="true"
    >
      <template
        slot="header"
        slot-scope="scope"
      >
        <div style="display: block">
          {{ $t('components.orgSelection.headers.orgCode') }}
        </div>
        <div style="display: block; padding-bottom: 5px">
          <el-input
            v-model="filterVendorCode"
            :placeholder="$t('perfMod.enterFilter')"
            @keyup.native.enter="queryFilter"
          />
        </div>
      </template>
    </el-table-column>
    <el-table-column
      align="center"
      prop="organizationName"
      :label="$t('components.orgSelection.headers.orgName')"
      min-width="250"
      :show-overflow-tooltip="true"
    >
      <template
        slot="header"
        slot-scope="scope"
      >
        <div style="display: block">
          {{ $t('components.orgSelection.headers.orgName') }}
        </div>
        <div style="display: block; padding-bottom: 5px">
          <el-input
            v-model="filterVendorName"
            :placeholder="$t('perfMod.enterFilter')"
            @keyup.native.enter="queryFilterByName"
          />
        </div>
      </template>
    </el-table-column>
  </el-table>
</template>
<script>
import { findMenuInfoByPath } from '@/utils'
import cloneDeep from 'lodash/cloneDeep'
import { store, mutations } from '@/library/components/organization-cascader/store'

function walk (list = [], callback, deep = 0) {
  return list.map(it => {
    const result = callback({ ...it }, deep)
    if (it && it.childOrganRelation && it.childOrganRelation.length) {
      result.childOrganRelation = walk(
        it.childOrganRelation,
        callback,
        deep + 1
      )
    }
    return result
  })
}

export default {
  name: 'OrganizationSelector',
  components: {},
  model: {
    event: 'change',
    value: 'value'
  },
  props: {
    handleVendoeSelection: {
      type: Function
    },
    queryFilter: {
      type: Function
    },
    queryFilterByName: {
      type: Function
    },
    // BPM在无登录情况下执行
     jumpLogin: {
      type: Boolean,
      default: true
    },
    customProps: {
      type: Object,
      default: () => ({
        id: 'organizationId',
        label: 'organizationName',
        value: 'organizationId',
        disabled: 'disabled'
      })
    },
    collapseTags: {
      type: Boolean,
      default: true
    },
    multiple: {
      type: Boolean,
      default: false
    },
    value: {
      default: null
    },
    placeholder: {
      type: String,
      default: this.$t('components.approvalHead.headers.selectNode')  //'请选择'
    },
    clearable: {
      type: Boolean,
      default: true
    },
    scope: {
      type: Object,
      default: () => {}
    },
    // 是否限制权限
    limit: {
      type: Boolean,
      default: true
    },
    // 是否使用后端接口获取下拉框数据
    remote: {
      type: Boolean,
      default: false
    },
    // 下拉选项只有一个时是否直接选中
    autoSelectWhenOneItem: {
      type: Boolean,
      default: false
    },
    // 是否启用供应商实体：供应商默认全部启用新逻辑
    supplierOrgCategory: {
      type: Boolean,
      default: true
    },
    parentId: {
      // default: -1
    },
    nodeType: {
      type: String,
      default: 'OU' // 默认值OU节点 //GROUP
    },
    transformData: {},
    // 序号，用于表格数组渲染定位
    tableIndex: [Number, String, Object]
  },
  data () {
    return {
      menuInfo: {},
      ableSelectTreeNodes: [],
      selectTreeOptions: [],
      filterVendorCode: null,
      filterVendorName: null
    }
  },
  computed: {
    selectTreeData () {
      return this.transformData
        ? this.transformData(this.selectTreeOptions)
        : this.selectTreeOptions
    }
  },
  watch: {
    parentId: {
      handler (oldValue, newValue) {
        if (
          JSON.stringify(oldValue) !== JSON.stringify(newValue) &&
          this.parentId
        ) {
          if (this.remote) {
            this.initSelect()
          } else {
            this.initSelectTree()
          }
        }
      },
      deep: true
    },
    value: {
      handler (n, o) {
        if (n && n !== o) {
          if (this.remote) {
            this.initSelect()
          } else {
            this.initSelectTree()
          }
        }
      }
    }
  },
  async mounted () {
    // BPM在无登录情况下执行
    // [start] by liwenhong
    if (this.jumpLogin) {
        const menus = this.$store.getters.userInfo.menus
      this.menuInfo = findMenuInfoByPath(this.$route.path, menus) || {}
    }
  // [end] by liwenhong

    if (this.supplierOrgCategory) {
      const userType = this.$store.getters.userInfo.userType
      // 采购商
      if (userType !== 'BUYER') {
        const res = await this.$api.base.getSupplierOrgTree({ organizationTypeCode: this.nodeType, parentOrganizationIds: this.parentId })
        this.selectTreeOptions = res.data
        return
      }
    }

    if (this.remote) {
      this.initSelect()
    } else {
      // 不开启权限控制，默认全部都是可选的
      if (this.limit) {
        // 设置可选的节点
        await this.setAbleSelectTreeNodes()
      }
      // 获取完成树结构信息
      await this.fetchTreeData()
      // 初始化完成组织架构
      this.initSelectTree(true)
    }
  },
  methods: {
    clearOptions () {
      this.selectTreeOptions = []
    },
    async fetchTreeData () {
      await mutations.fetchTreeWithFullPathId()
      this.originTreeData = store.treeWithFullPathId
    },
    async initSelect () {
      if (!this.parentId) {
        return
      }

      if (this.supplierOrgCategory) {
        const userType = this.$store.getters.userInfo.userType
        // 采购商
        if (userType !== 'BUYER') {
          const res = await this.$api.base.getSupplierOrgTree({ organizationTypeCode: this.nodeType, parentOrganizationIds: this.parentId })
          this.selectTreeOptions = res.data
          return
        }
      }
      const data = {
        organizationTypeCode: this.nodeType,
        parentOrganizationId: this.parentId
      }
      if (this.limit) {
        data.userId = this.$store.getters.userInfo.userId
      }
      const res = await this.$api.base.getOrganizationByOrgCode(data)
      this.selectTreeOptions = res.data
    },
    findChildren (parentId) {
      let parentIds = parentId
      if (!Array.isArray(parentId)) {
        parentIds = [parentId]
      }
      let children = []
      const originiTreeData = cloneDeep(this.originTreeData)
      if (parentIds.includes(-1)) {
        return originiTreeData
      }
      walk(originiTreeData, data => {
        const { organizationId, childOrganRelation } = data || {}
        if (parentIds.includes(organizationId)) {
          children = children.concat(childOrganRelation)
        }
        return data
      })
      return children
    },
    async initSelectTree (isInit = false) {
      if (this.supplierOrgCategory) {
        const userType = this.$store.getters.userInfo.userType
        // 采购商
        if (userType !== 'BUYER') {
          const res = await this.$api.base.getSupplierOrgTree({ organizationTypeCode: this.nodeType, parentOrganizationIds: this.parentId })
          this.selectTreeOptions = res.data
          return
        }
      }

      this.selectTreeOptions = []
      const children = this.findChildren(this.parentId)
      // console.warn("children: ", children);
      walk(children, data => {
        const {
          organizationTypeCode,
          organizationId,
          enabled,
          parentOrganizationId
        } = data
        const isEqulType = this.nodeType === organizationTypeCode
        // console.log("[enabled]", enabled);
        const isEffective = enabled === 'Y'
        // const isEffective = true;
        const hadExsit = this.selectTreeOptions.some(
          i => i.organizationId === organizationId
        )
        if (this.limit) {
          const isLimit = this.ableSelectTreeNodes.includes(organizationId)
          if (isEqulType && isLimit && isEffective && !hadExsit) {
            this.selectTreeOptions.push(data)
          }
          if (!isLimit && this.value && this.value === organizationId) {
            this.selectTreeOptions.push({ ...data, disabled: true })
          }
        } else if (isEqulType && isEffective && !hadExsit) {
          this.selectTreeOptions.push(data)
        }
        return data
      })
      if (this.autoSelectWhenOneItem && this.selectTreeOptions.length === 1) {
        const node = this.selectTreeOptions[0]
        if (node.disabled) {
          return
        }
        if (this.multiple) {
          this.$emit('select', [node], [node.organizationId], this.scope, this.tableIndex)
          this.$emit('change', [node.organizationId])
        } else {
          this.$emit('select', node, node.organizationId, this.scope, this.tableIndex)
          this.$emit('change', node.organizationId)
        }
      }
    },
    changeHandler (value) {
      let node
      walk(this.selectTreeOptions, data => {
        if (Array.isArray(value) && value.includes(data.organizationId)) {
          if (!node) {
            node = [data]
          } else {
            node.push(data)
          }
        } else if (data.organizationId === value) {
          node = data
        }
        return { ...data }
      })
      this.$emit('select', node, value, this.scope, this.tableIndex)
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
            i => i.organizationId
          )
        } else {
          // 如果没有设置组织管控维度就直接使用角色的组织权限
          const { organizationUsers } = this.$store.getters.userInfo
          this.ableSelectTreeNodes = organizationUsers.map(
            i => i.organizationId
          )
        }
      } else {
        // 供应商
        const companyId = this.$store.getters.user.companyId
        const key = `${companyId}_${userId}`
        await mutations.fetchVendorPermission(companyId, userId)
        // 解决根据供应商公司ID查询合作组织读取的数据表为空的问题，临时使用。后续开发出新接口需要删除-开始
        const { organizationUsers } = this.$store.getters.userInfo
        this.ableSelectTreeNodes = organizationUsers.map(
          i => i.organizationId
        )
        // 解决根据供应商公司ID查询合作组织读取的数据表为空的问题，临时使用。后续开发出新接口需要删除-结束
        // this.ableSelectTreeNodes = (store.vendorPermission[key].orgInfos || [])
        //   .filter(i => i.serviceStatus === "EFFECTIVE") // FIXME: 接口过滤有效的合作组织
        //   .map(i => i.organizationId);
      }
      return this.ableSelectTreeNodes
    }
  }
}
</script>
<style>
.el-select.collapseTags .el-select__tags .el-tag{
  margin: 0px 0 2px 5px;
}
.el-select.collapseTags .el-select__tags .el-select__tags-text{
  line-height: 16px;
}
</style>
