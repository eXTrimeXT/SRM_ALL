<template>
  <el-container
    class="flex-container-notab the_material_wrapper"
    direction="vertical"
  >
    <el-main>
      <form-wrapper
        :form-array="queryForm"
        @getFormData="getQuerydata"
        @synchronous-value="syncFilterParams"
      />

      <main-header
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <m-import
            ref="import"
            code="sup:categoryResponsibility:import"
            :title="excel"
            :up-load-url="iModal.upLoadUrl"
            :extra-data="extraData"
            @downloadTemplate="openDialogVisible('template')"
            @handleSuccess="handleSuccess"
          />
        </template>
      </main-header>
      <el-container
        style="padding: 0 !important;"
        direction="vertical"
        class="tablePd"
      >
        <el-main style="flex-grow: 1;display: flex;flex-direction: column;position:relative;">
          <el-form
            ref="categoryDvTable"
            class="tableForm"
            :model="categoryDvModle"
            :rules="categoryDvModle.rules"
          >
            <el-table
              ref="categoryTable"
              v-loading="loading"
              stripe
              border
              height="100%"
              :data="categoryDvModle.tableData"
              style="height: 100%;"
              @selection-change="handleSelectionChange"
            >
              <!-- 供应商编码 -->
              <el-table-column
                prop="companyCode"
                :label="$t('common.vendorCode')"
                min-width="180px"
                show-overflow-tooltip
              />
              <!-- 供应商名称 -->
              <el-table-column
                prop="companyName"
                :label="$t('common.vendorName')"
                min-width="180px"
                show-overflow-tooltip
              />
              <!-- 负责人 -->
              <el-table-column
                prop="fullName"
                :label="$t('dataConfMod.principal')"
                min-width="150"
              >
                <template slot="header">
                  <em class="toRequired">*</em>{{ $t('dataConfMod.principal') }}
                </template>
                <template slot-scope="scope">
                  <template v-if="scope.row.add || scope.row.edit">
                    <el-form-item :prop="'tableData.' + scope.$index + '.responsibilityName'">
                      <quick-search
                        :show-input="scope.row.responsibilityName"
                        show-key="categoryCode"
                        :scope-data="scope.row"
                        name="ceea_rbac_po_agent_info"
                        @close-quicksearch="getAgentObj"
                      />
                    </el-form-item>
                  </template>
                  <span v-else>{{ scope.row.responsibilityName }}</span>
                </template>
              </el-table-column>
            </el-table>
          </el-form>
        </el-main>
        <el-footer class="page-bar" height="">
          <c-pagination
            ref="queryPagination"
            class="c-query-table-pagination"
            :total="pageInfo.total"
            :page-num="pageInfo.pageNum"
            :page-size="pageInfo.pageSize"
            @current-change="handleCurrentChange"
            @size-change="handleSizeChange"
          />
        </el-footer>
        <!-- 人员选择 -->
        <c-people-selector
          ref="peopleSelector"
          :visible.sync="peopleDialog"
          :multi-select="false"
          @on-confirm="getPeople"
        />
      </el-container>
    </el-main>
  </el-container>
</template>
<script>
import QuickSearch from 'lib@/components/QuickSearch' // 快速查询组件
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import CPagination from 'lib@/components/c-pagination'
import CPeopleSelector from '@/library/components/c-people-selector'
import { parseTime } from '@/utils'
import MImport from 'lib@/components/import'
import { downloadFileLink, downloadFileLinkByPost } from 'lib@/utils/file'

export default {
  name: 'CategoryDivision',

  components: {
    CPagination,
    MainHeader,
    FormWrapper,
    MImport,
    CPeopleSelector,
    QuickSearch
  },
  data () {
    return {
      excel: this.$t('components.eio.importTitle'), // Excel导入
      gridId: 'categoryDivision',
      loading: false,
      iModal: {
        title: this.$t('components.eio.importTitle'), // Excel导入
        upLoadUrl: '/api-sup/supplier-leader/importExcel'
      },
      extraData: {
        fileModular: 'base',
        fileFunction: 'categoryDivision',
        fileType: 'excel'
      },
      pageSize: 15,
      currentRow: null,
      tableHeader: [],
      queryParam: {},
      queryForm: [],
      catList: [], // 采购分类
      peopleDialog: false,
      filterParams: {},
      categoryDvModle: {
        tableData: [],
        rules: {
          fullName: { type: 'string', required: true },
          startDate: { type: 'string', required: true },
          categoryName: { type: 'string', required: true }
        }
      },
      pageInfo: {
        total: 0,
        pageNum: 1,
        pageSize: 15
      },
      selections: []
    }
  },
  created () {
    this.queryForm = [
      {
        prop: 'companyCode',
        label: () => this.$t('common.vendorCode') // 供应商编码
      },
      {
        prop: 'companyName',
        label: () => this.$t('common.vendorName') // 供应商名称
      },
      {
        prop: 'responsibilityId',
        label: () => this.$t('dataConfMod.principal'), // 负责人
        type: 'quicksearch',
        showKey: 'nickname',
        propKey: 'userId',
        name: 'scc_rbac_user_display'
      }
    ]
    this.getQuerydata() //  查询数据
  },
  methods: {
    async openDialogVisible () {
      this.multipleSelection = []
      downloadFileLinkByPost(
        '/api-sup/supplier-leader/importSupplierLeaderModelDownload',
        parseTime(new Date()) + this.$t('dataConfMod.expTemplateXLSX')
      ).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail')) // 下载失败
      })
    },
    handleSuccess () {
      this.getQuerydata()
    },
    syncFilterParams (values) {
      this.filterParams = values
    },
    downloadTemplate () {
      downloadFileLink(
        '/api-base/categoryDv/importModelDownload',
        this.$t('dataConfMod.categoryDvTemp') + '.xlsx'
      ).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail')) // 下载失败
      })
    },
    getQuerydata (v) {
      this.queryParam = v || {}
      this.fatchListData(this.queryParam, { pageSize: 15 })
    },
    // 选择组织
    addOrgHandle (e, dd, scope) {
      scope.orgId = e ? e.organizationId : null
      scope.orgCode = e ? e.organizationCode : null
      scope.orgName = e ? e.organizationName : null
    },
    // 人员选择
    selectPeople (index) {
      this.peopleDialog = true
      this.curRow = index
    },
    // 获取选择器
    getPeople (data) {
      let user = data[0]
      this.categoryDvModle.tableData[this.curRow].userId = user.userId
      this.categoryDvModle.tableData[this.curRow].userName = user.username
      this.categoryDvModle.tableData[this.curRow].fullName = user.nickname
    },
    // 适配单位数据
    adaptUnitData (data) {
      let arr = []
      if (data && data.length > 0) {
        data.forEach(element => {
          arr.push({
            id: element.unitId,
            value: element.unitCode,
            label: element.unitName
          })
        })
      }
      return arr
    },
    selectFocus (index) {
      this.currentRow = index
    },
    // 查询列表数据
    fatchListData (p1, p2) {
      let queryObj = { ...p1, ...p2 }
      this.$http({
        url: '/api-sup/supplier-leader/listPageByParam',
        method: 'POST',
        data: queryObj,
        loading: true
      })
        .then(res => {
          if (res.data && res.data.list) {
            this.loading = false
            this.pageInfo.total = res.data.total
            this.pageInfo.pageNum = res.data.pageNum
            this.pageInfo.pageSize = res.data.pageSize
            this.categoryDvModle.tableData = res.data.list.map(i => ({
              ...i,
              edit: false,
              add: false
            }))
          }
        })
    },
    handleSelectionChange (val) {
      this.selections = val
    },
    // 品类确认选择
    comfirmSelect (node, scope) {
      scope.categoryId = node ? node.categoryId : null
      scope.categoryCode = node ? node.categoryCode : ''
      scope.categoryName = node ? node.categoryName : ''
      scope.categoryFullName = node ? node.categoryFullName : ''
    },
    // 行删除
    handleDelClick (index, row) {
      if (row.categoryResponsibilityId) {
        this.$http({
          url: '/api-sup/category-responsibility/delete',
          method: 'GET',
          params: { id: row.categoryResponsibilityId },
          loading: true
        })
          .then(() => {
            this.$message.success(this.$t('common.successDelete')) // 删除成功
            this.getQuerydata()
          })
      } else {
        this.categoryDvModle.tableData.splice(index, 1)
      }
    },
    // 行编辑
    handleEditClick (index) {
      this.categoryDvModle.tableData[index].edit = true
    },
    addOne () {
      this.categoryDvModle.tableData.unshift({
        add: true,
        edit: false,
        orgId: null,
        orgCode: null,
        orgName: null,
        categoryId: null,
        categoryCode: '',
        categoryName: '',
        categoryFullId: '',
        categoryFullName: '',
        responsibilityId: '',
        responsibilityName: ''
      })
      this.$nextTick(() => {
        this.$refs.categoryTable.toggleRowSelection(this.categoryDvModle.tableData[0], true)
      })
    },
    saveDataHandle () {
      if (this.selections.length > 0) {
        this.$refs['categoryDvTable'].validate((valid) => {
          if (valid) {
            this.$http({
              url: '/api-sup/category-responsibility/addCategoryResponsibility',
              method: 'POST',
              data: this.selections,
              loading: true
            })
              .then(() => {
                this.$message.success(this.$t('common.successSave')) // 保存成功
                this.getQuerydata()
              })
          } else {
            this.$message({
              message: this.$t('common.pleasefinishRequired'), // '请输入必填项'
              type: 'warning'
            })
          }
        })
      } else {
        this.$message({
          message: this.$t('common.cannotSave'), // '请选择保存的数据'
          type: 'warning'
        })
      }
    },
    getCategoryObj (val, scope) {
      scope.categoryId = val ? val.categoryId : ''
      scope.categoryCode = val ? val.categoryCode : ''
      scope.categoryName = val ? val.categoryName : ''
    },
    getAgentObj (val, scope) {
      scope.responsibilityId = val ? val.agentNumber : ''
      scope.responsibilityName = val ? val.agentName : ''
    },
    handleCurrentChange (num) {
      let page = {
        pageNum: num
      }
      this.loading = true
      this.fatchListData(this.queryParam, page)
    },
    handleSizeChange (size) {
      let page = {
        pageSize: size
      }
      this.loading = true
      this.fatchListData(this.queryParam, page)
    }
  }
}
</script>
<style scoped lang="scss">
.tableForm {
  position: absolute;
  top: 0;
  bottom: 0;
  height: 100%;
  width: 100%;
  .el-table {
    height: 100%;
  }
}
.download-link-wrap {
  .download-link-item {
    color: #1890ff;
  }
  .close-icon {
    font-weight: bold;
    cursor: pointer;
  }
}
.toRequired {
  color: #ff4949;
  padding-right: 2px;
}
</style>
