<!-- 中台组织数据选择 -->
<template>
  <srm-dialog
    :title="$t('dataConfMod.selectOrg')"
    size="midden"
    :show-close="false"
    :destroy-on-close="true"
    :visible.sync="visible"
    :close-on-click-modal="false"
  >
    <div class="search-content">
      <!-- <el-form
        ref="orgform"
        :model="orgQueryForm"
        label-width="100"
      >
        <el-row :gutter="32">
          <el-col :span="12">
            <el-form-item
              :label="$t('dataConfMod.orgName')"
              prop="organizationName"
              label-width="100px"
            >
              <el-input v-model="orgQueryForm.organizationName" />
            </el-form-item>
          </el-col>
          <el-col :span="4">
            <el-button
              type="primary"
              @click="searchOrg"
            >
              {{ $t('common.search') }}
            </el-button>
          </el-col>
        </el-row>
      </el-form> -->
      <!-- 列表 -->
      <div class="porg-table" style="max-height: 280px;margin-bottom: 10px;">
        <el-tree
          ref="sharedOrgRree"
          v-loading="orgLoading"
          element-loading-background="rgba(0, 0, 0, 0.4)"
          lazy
          highlight-current
          node-key="organizationCode"
          :data="orgTreeData"
          :props="orgTreeProps"
          :load="loadNode"
          @node-click="nodeClick"
        />
      </div>
    </div>
    <div slot="footer" class="dialog-footer">
      <el-button @click="cancleHandle">
        {{ $t('common.cancel') }}
      </el-button>
      <el-button type="primary" @click="comfirmSelect">
        {{ $t('common.confirm') }}
      </el-button>
    </div>
  </srm-dialog>
</template>
<script>
import CPagination from 'lib@/components/c-pagination'
import { organizationSetting } from 'modb@/basicSetting/api/basicSetting'

export default {
  name: 'SharedOrg',
  components: { CPagination },
  props: {
    visible: {
      type: Boolean,
      default: () => false
    },
    orgCode: { // 已选择
      type: String,
      default: () => ''
    },
    orgTypeCode: { // 组织类型
      type: String,
      default: ''
    },
    parentCode: { // 父组织code
      type: String,
      default: ''
    },
    multiple: { // 是否多选
      type: Boolean,
      default: () => false
    }
  },
  data () {
    return {
      orgSelection: [],
      orgCurrentRow: null,
      orgQueryForm: {
        organizationName: ''
      },
      orgTableData: [],
      orgTableDataPage: {
        total: 0,
        pageNum: 1,
        pageSize: 20
      },
      canselLoad: false,
      firstLoad: true,
      orgLoading: false,
      // 部门树数据
      orgTreeData: [
        {
          childrens: [],
          organizationName: '',
          disabled: true
        }
      ],
      // 部门树配置选项
      orgTreeProps: {
        children: 'childrens',
        label: 'organizationName',
        disabled: data => {
          return data.disabled ? data.disabled : false
        },
        isLeaf: data => {
          return data.isLeaf
        }
      }
    }
  },
  watch: {
    visible (newValue) {
      if (newValue) {
        this.canselLoad = false
        this.orgSelection = []
        this.orgCurrentRow = null
        this.loasFirstNode()
      }
    }
  },
  methods: {
    // 异步树叶子节点懒加载逻辑
    loadNode (node, resolve) {
      // 一级节点处理
      if (this.canselLoad || this.firstLoad) { return }
      if (node.level === 0) {
        const queryParma = { orgTypeCode: this.orgTypeCode }
        // 查询一级节点
        this.getDepartmentTree(queryParma, resolve)
      } else if (node.level >= 1) {
        // 注意！把resolve传到你自己的异步中去
        const nodeParme = {}
        nodeParme.orgTypeCode = this.orgTypeCode
        nodeParme.parentCode = node.data.organizationCode
        this.getDepartmentTree(nodeParme, resolve)
      } else {
        return resolve([]) // 防止该节点没有子节点时一直转圈的问题出现
      }
    },
    // 加载子节点
    getDepartmentTree (parmes, resolve) {
      try {
        organizationSetting.getRpcOrg(parmes)
          .then(response => {
            if (response && response.data) {
              resolve(response.data)
            } else {
              this.$message({
                // '数据获取失败：'
                message: this.$t('dataConfMod.loadDataFail') + response.msg,
                type: 'error'
              })
            }
          })
          .finally(() => {
            this.firstLoad = false
          }).catch(() => {
            this.firstLoad = false
          })
      } catch (e) {
        resolve([])
        this.firstLoad = false
      }
    },
    // 查询组织
    searchOrg () {
      const queryParma = {
        organizationName: this.orgQueryForm.organizationName
      }
      this.loasFirstNode(queryParma)
    },
    // 加载一级节点
    loasFirstNode (otherParma = {}) {
      this.orgTreeData = []
      const queryParma = {
        orgTypeCode: this.orgTypeCode,
        ...otherParma
      }
      try {
        organizationSetting.getRpcOrg(queryParma)
          .then(response => {
            if (response && response.data) {
              let resData = response.data || []
              this.orgTreeData = resData.map((i) => ({
                ...i,
                childrens: [],
                level: 0,
                disabled: true
              }))
            } else {
              this.$message({
                // '数据获取失败：'
                message: this.$t('dataConfMod.loadDataFail') + response.msg,
                type: 'error'
              })
            }
          })
          .finally(() => {
            this.firstLoad = false
          })
      } catch (e) {
        this.firstLoad = false
      }
    },
    nodeClick (node) {
      // 跟节点不给选择
      this.orgCurrentRow = node.level == 0 ? null : node
    },
    comfirmSelect () {
      this.canselLoad = true
      let selected = []
      if (this.multiple) {
        selected = this.orgSelection
      } else {
        if (!this.orgCurrentRow) {
          return this.$message({
            message: this.$t('dataConfMod.rootNodeCannotSelect'),
            type: 'error'
          })
        }
        selected = this.orgCurrentRow ? [this.orgCurrentRow] : []
      }
      console.log(selected)
      this.$emit('on-ok', selected)
    },
    cancleHandle () {
      this.canselLoad = true
      this.$emit('on-cancle')
    }
  }
}
</script>
