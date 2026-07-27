<template>
  <!-- 选择父级菜单弹框 -->
  <!-- 选择功能 -->
  <srm-dialog
    :title="$t('dataConfMod.selectFunc')"
    size="midden"
    :show-close="false"
    :destroy-on-close="true"
    :visible.sync="visible"
    :close-on-click-modal="false"
  >
    <div class="search-content">
      <el-form
        ref="orgform"
        :model="parentOrgQueryForm"
        label-width="100"
        @submit.native.prevent
      >
        <el-row :gutter="32">
          <el-col :span="10">
            <!-- 功能名称 -->
            <el-form-item
              :label="$t('dataConfMod.functionName')"
              prop="functionName"
              label-width="100px"
            >
              <el-input
                v-model="parentOrgQueryForm.functionName"
                @keyup.enter.native="searchParentOrg(false)"
              />
            </el-form-item>
          </el-col>
          <el-col :span="4">
            <el-button
              type="primary"
              @click="searchParentOrg(false)"
            >
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
          border
          :data="parentOrgTableData"
          tooltip-effect="dark"
          style="width: 100%"
          max-height="300px"
          @select="handleSelectionChange"
        >
          <el-table-column
            type="selection"
            width="55"
          />
          <!-- 功能名称 -->
          <el-table-column
            prop="functionName"
            :label="$t('dataConfMod.functionName')"
          />
          <!-- 功能描述 -->
          <el-table-column
            prop="functionDesc"
            :label="$t('dataConfMod.functionDesc')"
          />
          <!-- 功能地址 -->
          <el-table-column
            prop="functionAddress"
            :label="$t('dataConfMod.functionAddress')"
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
    <div
      slot="footer"
      class="dialog-footer"
    >
      <el-button @click="cancleHandle">
        {{ $t('common.cancel') }}
      </el-button>
      <el-button
        type="primary"
        @click="comfirmSelect"
      >
        {{ $t('common.confirm') }}
      </el-button>
    </div>
  </srm-dialog>
</template>
<script>
import CPagination from 'lib@/components/c-pagination'
import { menuApi } from 'modb@/accountAccess/api'
export default {
  name: 'FuctionList',
  components: { CPagination },
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    id: {
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
        functionName: null,
        // functionCode: "",
        pageNum: 1,
        pageSize: 10
      }
    }
  },
  watch: {
    visible (oldValue, newValue) {
      if (!newValue) {
        this.searchParentOrg(true)
      }
    }
  },
  created () {
    this.$nextTick(() => {
        this.searchParentOrg(true)
    })
  },
  methods: {
    parentDataCurrentChange (num) {
      this.parentOrgQueryForm.pageNum = num
      this.searchParentOrg()
    },
    parentDataSizeChange (size) {
      this.parentOrgQueryForm.pageSize = size
      this.searchParentOrg()
    },
    searchParentOrg (isFirst = false) {
      const params = this.parentOrgQueryForm.functionName ? this.parentOrgQueryForm : { ...this.parentOrgQueryForm, functionName: null }
      menuApi.getParentOrg(isFirst ? { pageNum: 1, pageSize: 10 } : params,).then(data => {
          const { list, pageNum = 0, pageSize = 0, total } = data.data
          this.parentOrgTableData = list
          this.parentOrgTableDataPage = { pageNum, pageSize, total }
          if (this.id) {
            const selection = list.find(item => item.functionId === this.id)
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
