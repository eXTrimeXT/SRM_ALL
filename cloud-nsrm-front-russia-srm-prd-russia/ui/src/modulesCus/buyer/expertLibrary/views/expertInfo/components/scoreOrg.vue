<template>
  <div class="wrapper">
    <div class="header">
      <span class="red">*</span>
      适用评分组织
    </div>
    <div class="btns mg-10">
      <el-button v-if="!readonly" type="primary" @click="add">
        新增
      </el-button>
    </div>
    <el-table
      border
      stripe
      :data="tableData"
      :close-on-click-modal="false"
      max-height="250px"
    >
      <el-table-column
        type="index"
        label="序号"
        width="60"
      />

      <!-- fullPathName -->
      <el-table-column
        prop="fullPathName"
        label="组织名称"
      />

      <el-table-column
        v-if="!readonly"
        prop="operation"
        label="操作"
        width="100"
      >
        <template v-slot="scope">
          <el-button type="text" @click="deleteRow(scope)">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加组织权限 -->
    <srm-dialog
      :visible.sync="showOrgDialog"
      :title="$t('dataConfMod.addOrgAccess')"
      size="middle"
      @close="dialogCancleHandle"
    >
      <div style="height: 300px;overflow: auto;">
        <Treeselect
          v-model="currentRows"
          :normalizer="normalizer"
          :no-children-text="$t('dataConfMod.noChildrenText')"
          :no-options-text="$t('dataConfMod.noOptionsText')"
          :no-results-text="$t('dataConfMod.noResultsText')"
          :placeholder="$t('dataConfMod.msgSelectOrgName')"
          :append-to-body="false"
          :searchable="true"
          :options="options"
          multiple
          value-consists-of="ALL_WITH_INDETERMINATE"
          value-format="object"
          :always-open="true"
          auto-select-descendants
          :flatten-search-results="true"
          auto-deselect-descendants
          flat
        />
      </div>
      <div slot="footer">
        <el-button @click="onCancel">
          {{ $t("common.cancel") }}
        </el-button>
        <el-button type="primary" @click="addOneOrg">
          {{ $t("common.confirm") }}
        </el-button>
      </div>
    </srm-dialog>
  </div>
</template>
<script>
import Treeselect from '@riophae/vue-treeselect'
import '@riophae/vue-treeselect/dist/vue-treeselect.css'
import { newOrganaztionTreehttp } from '@/api/common'
import getOrgList from 'modb@/accountAccess/views/usersAccess/calc'
import { store } from 'lib@/components/organization-cascader/store'

const findMenuInfoByPath = (leafId, nodes, resObj = {}) => {
  for (let i = 0; i < nodes.length; i++) {
    const tmpObj = nodes[i]
    if (leafId === nodes[i].fullPathId) {
      return tmpObj
    }
    if (nodes[i].childOrganRelation) {
      const findResult = findMenuInfoByPath(
        leafId,
        nodes[i].childOrganRelation,
        tmpObj
      )
      if (findResult) {
        return findResult
      }
    }
  }
}

export default {
  components: {
    Treeselect
  },
  props: {
    value: {
      type: Array,
      default: () => []
    },
    readonly: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      currentRows: [],
      showOrgDialog: false,
      options: []
    }
  },
  computed: {
    tableData: {
      get () {
        return this.value
      },
      set (val) {
        this.$emit('update:value', val)
      }
    }
  },
  async mounted () {
    const { data } = await this.newOrganaztionTreehttp({})
    this.options = data
    console.log('options', this.options)
  },
  methods: {
    // 组织树结构 新增虚拟ID
    newOrganaztionTreehttp (data) {
      return this.$http({
        url: '/api-pj/organization/relation/treeNewAllGroupBuOu',
        method: 'POST',
        data
      })
    },
    add () {
      this.showOrgDialog = true
      this.dialogCancleHandle()
    },
    deleteRow (scope) {
      this.tableData.splice(scope.$index, 1)
    },
    dialogCancleHandle () {
      this.currentRows = this.tableData
        .map(item => {
          return {
            ...findMenuInfoByPath(item.fullPathId, this.options),
            startDate: item.startDate
          }
        })
        .filter(i => !!i)
    },
    normalizer (node) {
      const NODE = {
        id: node.fullPathId,
        label: node.organizationName
      }
      if (node.childOrganRelation && node.childOrganRelation.length) { NODE.children = node.childOrganRelation }
      return NODE
    },
    /* 添加组织权限 */
    addOneOrg () {
      const newList = this.currentRows.map(item => {
        return {
          ...item,
          isEditing: false,
          startDate: item.startDate ? item.startDate : new Date().getTime()
        }
      })
      const _list = getOrgList(
        newList,
        this.options,
        store.fullPathNameMap || new Map()
      )
      console.log('list', _list)
      this.tableData = _list.orgList.filter(item => item.organizationTypeCode === 'OU').map(item => ({
        ...item,
        orgId: item.organizationId,
        orgCode: item.organizationCode,
        orgName: item.organizationName
      }))
      store.fullPathNameMap = _list.fullPathNameMap
      this.showOrgDialog = false
    },
    onCancel () {
      this.showOrgDialog = false
    }
  }
}
</script>
<style lang="scss" scoped>
.red {
  color: red;
}
.mg-10 {
  margin: 10px 0;
}
</style>
