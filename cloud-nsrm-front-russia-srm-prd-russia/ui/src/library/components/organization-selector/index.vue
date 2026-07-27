<template>
  <div class="the_organization-wrap">
    <div v-if="readPretty">
      {{ getOrgName }}
    </div>
    <el-row v-else style="padding: 0;">
      <el-col
        :span="24"
        style="position: relative;padding: 0;"
      >
        <!-- 只读模式显示值  -->
        <template v-if="readPretty">
          {{ selectValue }}
        </template>

        <!-- 按钮选择形式 -->
        <template v-else-if="selectType === 'button'">
          <el-button
            :disabled="disabled"
            type="primary"
            @click="openDialog()"
          >
            {{ $t("components.organization.orgSelect") }}
          </el-button>
        </template>
        <template v-else>
          <el-select
            class="the_org_select"
            :disabled="disabled"
            :placeholder="placeholderText"
            :clearable="clearable"
            :multiple="multiple"
            :value="value"
            :collapse-tags="multiple ? collapseTags: true"
            :class="{'collapseTags': collapseTags}"
            :filterable="true"
            @change="changeHandler"
          >
            <el-option
              v-for="(option, i) in selectTreeData"
              :key="i"
              :value="option[customProps.value]"
              :label="option[customProps.label]"
              :disabled="option[customProps.disabled]"
            />
          </el-select>
          <el-button
            :disabled="disabled"
            icon="iconfont iconselect"
            :class="['quick-search-btn',{'quick-edit': !disabled }]"
            @click="openDialog()"
          />
        </template>
      </el-col>
    </el-row>
    <srm-dialog
      v-if="dialogVisible"
      :title="dialogTitle"
      class="org-selector-dialog"
      :size="dialogSize"
      :visible.sync="dialogVisible"
      :append-to-body="true"
      :close-on-click-modal="false"
      @close="dialogVisible = false"
    >
      <!-- 组织选择 -->
      <Organization
        v-if="dialogVisible"
        ref="srmOrgSelector"
        :multiple="multiple"
        :default-value="defaultValueAc"
        :dialog-visible="dialogVisible"
        :node-type="nodeType"
        :node-type-data="nodeTypeData"
        :select-type="selectType"
        :match-field="matchField"
        @dblclick="dblclickHandel"
      />
      <div
        slot="footer"
        class="dialog-footer"
      >
        <el-button @click="cancleSelector">
          <!-- 取 消 -->
          {{ $t("components.common.cancel") }}
        </el-button>
        <el-button
          type="primary"
          @click="confirmSelector"
        >
          <!-- 确 定 -->
          {{ $t("components.common.confirm") }}
        </el-button>
      </div>
    </srm-dialog>
  </div>
</template>
<script>
import { findMenuInfoByPath } from '@/utils'
import cloneDeep from 'lodash/cloneDeep'
import { store, mutations } from '../organization-cascader/store'
import Organization from './Organization'
import { createDictClass } from '@/library/utils/dict/dict-utils'
const orgTypeDictClass = createDictClass().setCustomSelectType('ORG_TYPE_ALL').loadCustomSelectType('ORG_TYPE_ALL')

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
  components: { Organization },
  model: {
    event: 'change',
    value: 'value'
  },
  props: {
    // 只读态的判断值
    readPretty: {
      type: Boolean,
      default: false
    },
    selectType: {
      // input || button
      type: String,
      default: function () {
        return 'input'
      }
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
    defaultValue: {
      type: Array,
      default: () => {
        return []
      }
    },
    matchField: { // 回显匹配字段
      type: String,
      default: () => {
        return 'organizationId'
      }
    },
    disabled: {
      type: Boolean,
      default: false
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
      type: [String, Number, Array],
      default: null
    },
    placeholder: {
      type: String,
      default: ''
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
      type: [String, Number, Array],
      default: -1
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
      orgTypeDictClass: orgTypeDictClass,
      dialogVisible: false,
      dialogTitle: this.$t('components.organization.orgSelect'),
      menuInfo: {},
      defaultValueAc: [],
      ableSelectTreeNodes: [],
      selectTreeOptions: [],
      nodeTypeData: [] // 节点数据
    }
  },
  computed: {
    selectTreeData () {
      return this.transformData
        ? this.transformData(this.selectTreeOptions)
        : this.selectTreeOptions
    },
    // 弹框尺寸 多选的时候弹框大一号
    dialogSize () {
      if (this.multiple) {
        return 'xLarge'
      } else {
        return 'large'
      }
    },
    getOrgName () {
      return this.selectTreeOptions.filter(item => item.organizationId === this.value)[0]?.organizationName
    },
    selectValue () {
      const record = this.selectTreeData.find(item => item[this.customProps.value] === this.value) || ''
      return record ? record[this.customProps.label] : this.value
    },
    placeholderText () {
      return this.placeholder || this.$t('common.pleaseSelect')
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
        } else {
          this.defaultValueAc = []
        }
      }
    },
    selectTreeData: {
      handler (n, o) {
        this.nodeTypeData = this.transformData
          ? this.transformData(this.selectTreeOptions)
          : this.selectTreeOptions
      }
    }
  },
  async created () {
    const menus = this.$store.getters.userInfo.menus
    this.menuInfo = findMenuInfoByPath(this.$route.path, menus) || {}
    this.defaultValueAc = this.defaultValue
    // 供应商 逻辑
    if (this.supplierOrgCategory) {
      const userType = this.$store.getters.userInfo.userType
      if (userType !== 'BUYER') {
        await this.initSupplierOrgTree()
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

    this.dialogTitle = this.orgTypeDictClass.getDictLabel('ORG_TYPE_ALL', this.nodeType) + this.$t('common.select')
  },
  methods: {
    // 供应商组织信息
    async initSupplierOrgTree () {
      let parentIdCurrent = this.parentId
      if (parentIdCurrent) {
        if (typeof parentIdCurrent === 'object') {
          parentIdCurrent = parentIdCurrent.join(',')
        }
      }
      const key = `${this.nodeType}_${parentIdCurrent}`
      await mutations.fetchSupPermission(this.nodeType, parentIdCurrent)
      let vendorPermission = store.vendorPermission[key] || []
      this.selectTreeOptions = vendorPermission
      this.placeDataTip()
    },
    placeDataTip () {
      if (this.value) {
        const orgData = this.selectTreeOptions.find(i => ((i.organizationId).toString() == this.value.toString()))
        if (!orgData) {
          this.selectTreeOptions.push({
            organizationId: this.value,
            organizationCode: this.value.toString(),
            organizationName: this.$t('components.orgSelection.giveAuth')
          })
        }
      }
    },
    createFilter (queryString) {
      return (restaurant) => {
        return (restaurant[this.customProps.label].indexOf(queryString.toLowerCase()) === 0)
      }
    },
    querySearch (queryString, cb) {
      var restaurants = this.selectTreeData
      var results = queryString ? restaurants.filter(this.createFilter(queryString)) : restaurants
      cb(results)
    },
    clearOptions () {
      this.selectTreeOptions = []
    },
    async fetchTreeData () {
      await mutations.fetchTreeWithFullPathId()
      this.originTreeData = store.treeWithFullPathId
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
    // 接口查询的方式不缓存
    async initSelect () {
      if (!this.parentId) {
        return
      }
      // 供应商
      if (this.supplierOrgCategory) {
        const userType = this.$store.getters.userInfo.userType
        if (userType !== 'BUYER') {
          await this.initSupplierOrgTree()
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
    async initSelectTree (isInit = false) {
      // 供应商
      if (this.supplierOrgCategory) {
        const userType = this.$store.getters.userInfo.userType
        if (userType !== 'BUYER') {
          await this.initSupplierOrgTree()
          return
        }
      }

      this.selectTreeOptions = []
      const children = this.findChildren(this.parentId)
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
        if (Array.isArray(value) && value.includes(data[this.customProps.value])) {
          if (!node) {
            node = [data]
          } else {
            node.push(data)
          }
        } else if (data[this.customProps.value] === value) {
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
        let orgControlDim = false // this.menuInfo.orgControlDim // 目前组织管控维度没用
        if (orgControlDim) {
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
    },
    // 打开弹窗
    openDialog () {
      this.dialogTitle = this.orgTypeDictClass.getDictLabel('ORG_TYPE_ALL', this.nodeType) + this.$t('common.select')
      this.dialogVisible = true
    },
    // 取消 关闭
    cancleSelector () {
      this.dialogVisible = false
    },
    // 单选的弹框可以双击选择数据
    dblclickHandel (row) {
      let orgId = row[this.customProps.value]
      this.$emit('select', row, orgId, this.scope, this.tableIndex)
      this.$emit('change', orgId)
      this.dialogVisible = false
    },
    // 确认按钮事件
    confirmSelector () {
      if (this.multiple) {
        const selectedData = this.$refs.srmOrgSelector.selectedData || []
        const selectedDataBak = this.$refs.srmOrgSelector.selectedDataBak || []
        let resultSelected = null
        // 存在右侧搜索情况
        if (selectedDataBak.length > selectedData.length) {
          this.defaultValueAc = selectedDataBak
          resultSelected = selectedDataBak
        } else {
          this.defaultValueAc = selectedData
          resultSelected = selectedData
        }
        if (resultSelected.length == 0) {
          return this.$message({
            type: 'error',
            message: this.$t('components.orgSelection.selectNeedData')
          })
        }
        let orgShowName = []
        let orgShowId = []
        resultSelected.forEach(item => {
          orgShowName.push(item[this.customProps.label])
          orgShowId.push(item[this.customProps.value])
        })
        let orgId = orgShowId // .toString()
        this.value = orgId
        this.$emit('select', resultSelected, orgId, this.scope, this.tableIndex)
        this.$emit('change', orgId)
      } else {
        const selectedData = this.$refs.srmOrgSelector.currentRow || {}
        this.dblclickHandel(selectedData)
      }
      this.dialogVisible = false
    }
  }
}
</script>
<style scoped lang="scss">
.the_organization-wrap {
  .the_org_select {
    display: block;
    padding: 0;
    &.collapseTags{
      :deep(.el-select__tags){
        >span{
          .el-tag{
            max-width: 62%;
          }
        }
      }
    }
  }
  .the_org_select{
    :deep(>.el-input.el-input--suffix) {
      >.el-input__inner{
        padding-right: 26px;
      }
      >.el-input__suffix{
        right: 24px;
        z-index: 10;
        >.el-input__suffix-inner{
          >.el-select__caret{
            &.el-icon-arrow-up{
              &::before{
                content: " ";
              }
            }
            &.el-icon-arrow-down{
              display: none;
            }
            &.el-icon-search{
              display: none;
            }
          }
        }
      }
      .el-input__icon {
        width: 16px;
      }
    }
  }
  .quick-search-btn {
    position: absolute;
    top: 1px;
    bottom: 1px;
    right: 1px;
    border: none;
    min-width: 20px;
    border-radius: 0 4px 4px 0;
    padding: 4px 6px 4px 2px !important;
    color: #96999c;
  }
  .el-select.collapseTags .el-select__tags .el-tag{
    margin: 0px 0 2px 5px;
  }
  .el-select.collapseTags .el-select__tags .el-select__tags-text{
    line-height: 16px;
  }
}

</style>
