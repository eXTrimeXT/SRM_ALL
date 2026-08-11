<template>
  <!-- 选择节点 -->
  <srm-dialog
    :title="$t('dataConfMod.selectLastOrg')"
    size="midden"
    :show-close="true"
    :destroy-on-close="true"
    :visible.sync="visible"
    :close-on-click-modal="false"
  >
    <div class="search-content">
      <el-form
        ref="orgform"
        :model="parentOrgQueryForm"
      >
        <el-row :gutter="32">
          <el-col :span="7">
            <!-- 组织类型 -->
            <el-form-item
              :label="$t('dataConfMod.orgType')"
              prop="organizationTypeCode"
              label-width="60px"
            >
              <DictSelect
                v-model="parentOrgQueryForm.organizationTypeCode"
                code="ORG_TYPE_ALL"
                custom-select-type="ORG_TYPE_ALL"
                @keyup.enter.native="searchParentOrgData"
              />
            </el-form-item>
          </el-col>
          <el-col :span="7">
            <!-- 组织名称 -->
            <el-form-item
              :label="$t('dataConfMod.orgName')"
              prop="organizationName"
              label-width="60px"
            >
              <el-input v-model="parentOrgQueryForm.organizationName" clearable @keyup.enter.native="searchParentOrgData" />
            </el-form-item>
          </el-col>
          <el-col :span="7">
            <!-- 组织编码 -->
            <el-form-item
              :label="$t('common.orgCode')"
              prop="organizationCode"
              label-width="60px"
            >
              <el-input v-model="parentOrgQueryForm.organizationCode" clearable @keyup.enter.native="searchParentOrgData" />
            </el-form-item>
          </el-col>
          <el-col :span="3">
            <!-- 查询 -->
            <el-button
              type="primary"
              @click="searchParentOrgData"
            >
              {{ $t('common.search') }}
            </el-button>
          </el-col>
        </el-row>
      </el-form>
      <!-- 列表 -->
      <div class="porg-table">
        <el-table
          ref="parentOrgTable"
          style="width: 100%;"
          max-height="270"
          border
          :data="parentOrgTableData"
          tooltip-effect="dark"
          highlight-current-row
          @selection-change="handleSelectionChange"
          @current-change="handleCurrentChange"
        >
          <el-table-column
            v-if="multiple"
            type="selection"
            width="55"
          />
          <!-- 组织名称" -->
          <el-table-column
            prop="organizationName"
            :label="$t('dataConfMod.orgName')"
          />
          <!-- 组织编码 -->
          <el-table-column
            prop="organizationCode"
            :label="$t('组织编码')"
          />
          <!-- 组织类型 -->
          <el-table-column
            prop="organizationTypeName"
            :label="$t('dataConfMod.orgType')"
          />
        </el-table>

        <CPagination
          :total="parentOrgTableDataPage.total"
          :page-num="parentOrgTableDataPage.pageNum"
          :page-size="parentOrgTableDataPage.pageSize"
          @current-change="parentDataCurrentChange"
          @size-change="parentDataSizeChange"
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
  name: 'ParentOrg',
  components: { CPagination },
  props: {
    visible: {
      type: Boolean,
      default: () => false
    },
    selectionId: { // 已选择
      type: String,
      default: () => ''
    },
    organizationId: { // 组织ID
      type: Number
    },
    multiple: {
      type: Boolean,
      default: () => false
    },
    curOpt: {
      type: String,
      default: ''
    }
  },
  data () {
    return {
      orgRelDataSelection: [],
      orgRelDataCurrentRow: null,
      parentOrgQueryForm: {
        // 父级组织查询条件
        organizationName: '',
        organizationTypeCode: '',
        organizationCode: '',
        pageNum: 1,
        pageSize: 10
      },
      parentOrgTableData: [],
      parentOrgTableDataPage: {
        total: 0,
        pageNum: 1,
        pageSize: 20
      }
    }
  },
  watch: {
    visible (newValue) {
      if (newValue) {
        this.searchParentOrg(true)
      }
    }
  },
  methods: {
    // 查询按钮事件
    searchParentOrgData () {
      this.parentOrgQueryForm.pageNum = 1
      this.searchParentOrg()
    },
    // 查询上层组织
    async searchParentOrg () {
      const self = this
      const params = self.parentOrgQueryForm
      if (this.curOpt === 'add') {
        delete params.organizationId
      } else {
        // 编辑传id
        params.organizationId = this.organizationId
      }
      const { data } = await organizationSetting.getParentOrgData(params)
      this.parentOrgTableData = data.list || []
      this.parentOrgTableDataPage.total = data.total
      this.parentOrgTableDataPage.pageNum = data.pageNum
      this.parentOrgTableDataPage.pageSize = data.pageSize
      this.toogleSelection()
    },
    toogleSelection () {
      // 旧数据的选择
      let self = this
      const oldSelection = self.selectionId // 原来已经选择的数据
      if (oldSelection) {
        const selectedArr = oldSelection.indexOf(',') > -1 ? oldSelection.split(',') : [oldSelection]
        if (selectedArr.length > 0) {
          self.$nextTick(() => {
            selectedArr.forEach(selected => {
              // 反选table操作
              let rowData = self.parentOrgTableData.find(i => (i.organizationId == selected))
              if (rowData) {
                self.$refs.parentOrgTable.toggleRowSelection(rowData, true)
              }
            })
          })
        }
      }
    },
    parentDataCurrentChange (num) {
      this.parentOrgQueryForm.pageNum = num
      this.searchParentOrg()
    },
    parentDataSizeChange (size) {
      this.parentOrgQueryForm.pageSize = size
      this.searchParentOrg()
    },
    // 上层组织change
    handleSelectionChange (selection) {
      this.orgRelDataSelection = selection
    },
    // 上层组织当前行change
    handleCurrentChange (currentRow) {
      this.orgRelDataCurrentRow = currentRow
    },
    comfirmSelect () {
      let selected = []
      if (this.multiple) {
        selected = this.orgRelDataSelection
      } else {
        selected = [this.orgRelDataCurrentRow]
      }
      this.$emit('on-ok', selected)
    },
    cancleHandle () {
      this.$emit('on-cancle')
    }
  }
}
</script>
