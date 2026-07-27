<template>
  <!-- 选择父级菜单弹框 -->
  <!-- 选择父菜单 -->
  <srm-dialog
    :title="$t('dataConfMod.selectParentMenu')"
    size="midden"
    :show-close="false"
    :destroy-on-close="true"
    :visible.sync="visible"
    :close-on-click-modal="false"
  >
    <div class="search-content">
      <el-form ref="orgform" :model="parentOrgQueryForm" label-width="100" @submit.native.prevent>
        <el-row :gutter="32">
          <el-col :span="10">
            <!-- 菜单名称 -->
            <el-form-item
              :label="$t('dataConfMod.menuName')"
              prop="permissionName"
              label-width="100px"
            >
              <el-input
                v-model="parentOrgQueryForm.permissionName"
                @keyup.enter.native="searchParentHandle"
              />
            </el-form-item>
          </el-col>
          <el-col :span="4">
            <el-button type="primary" @click="searchParentHandle">
              {{ $t('common.search') }}
            </el-button>
          </el-col>
        </el-row>
      </el-form>
      <!-- 列表 -->
      <div class="porg-table">
        <el-table
          v-if="visible"
          ref="parentOrgTable"
          max-height="300px"
          border
          :data="parentOrgTableData"
          tooltip-effect="dark"
          style="width: 100%"
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55" />
          <!-- 菜单名称 -->
          <el-table-column prop="permissionName" :label="$t('dataConfMod.menuName')" />
          <!-- 菜单编码 -->
          <el-table-column prop="permissionCode" :label="$t('dataConfMod.menuCode')">
            <template slot-scope="scope">
              {{ scope.row.permissionCode }}
            </template>
          </el-table-column>
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
import { menuApi } from 'modb@/accountAccess/api'

export default {
  name: 'ParentMenu',
  components: { CPagination },
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    id: {
      type: Number
    },
    currentPerissionId: {
      type: Number
    }
  },
  data () {
    return {
      selection: null,
      parentOrgTableData: [],
      orgTypeList: [],
      parentOrgTableDataPage: {
        total: 0,
        pageNum: 1,
        pageSize: 20
      },
      parentOrgQueryForm: {
        permissionName: null,
        pageNum: 1,
        pageSize: 10
      }
    }
  },
  watch: {
    visible (oldValue, newValue) {
      if (!newValue) {
        this.searchParentOrg({ pageNum: 1, pageSize: 10 })
      }
    }
  },
  created () {
    this.$nextTick(() => {
      this.searchParentOrg({ pageNum: 1, pageSize: 10 })
    })
  },
  methods: {
    parentDataCurrentChange (num) {
      this.parentOrgQueryForm.pageNum = num
      this.searchParentOrg(this.parentOrgQueryForm)
    },
    parentDataSizeChange (size) {
      this.parentOrgQueryForm.pageSize = size
      this.searchParentOrg(this.parentOrgQueryForm)
    },
    searchParentHandle () {
      this.parentOrgQueryForm.pageNum = 1
      this.parentOrgQueryForm.pageSize = 10
      this.searchParentOrg(this.parentOrgQueryForm)
    },
    searchParentOrg (paramsObj) {
      const params = paramsObj.permissionName ? paramsObj : { ...paramsObj, permissionName: null }
      menuApi.getListChildrenData(params).then(data => {
        const { list, pageNum = 0, pageSize = 0, total } = data.data
        this.parentOrgTableData = list.filter(item => item.permissionId !== this.currentPerissionId)
        this.parentOrgTableDataPage = { pageNum, pageSize, total }
        if (this.id) {
          const selection = list.find(item => item.permissionId === this.id)
          if (selection) {
            setTimeout(() => this.$refs.parentOrgTable.toggleRowSelection(selection, true), 100)
          }
        }
      })
    },
    comfirmSelect () {
      this.$emit('on-ok', this.selection)
    },
    handleSelectionChange (selection) {
      let shouldChecked = selection[0]
      if (selection.length > 1) {
        this.$refs.parentOrgTable.clearSelection()
        shouldChecked = selection[1]
        this.$refs.parentOrgTable.toggleRowSelection(shouldChecked, true)
      }
      this.selection = [shouldChecked]
    },
    cancleHandle () {
      this.$emit('on-cancle')
    }
  }
}
</script>
