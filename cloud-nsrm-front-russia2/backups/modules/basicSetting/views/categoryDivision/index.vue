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
          <el-button
            type="primary"
            @click="addOne"
          >
            {{ $t('common.add') }}
          </el-button>
          <el-button
            :disabled="selections.length == 0"
            @click="saveDataHandle"
          >
            {{ $t('common.save') }}
          </el-button>
          <m-import
            ref="import"
            style="display: inline-block;margin: 0 10px;"
            :title="iModal.title"
            :up-load-url="iModal.upLoadUrl"
            :extra-data="extraData"
            @downloadTemplate="downloadTemplate"
            @handleSuccess="handleSuccess"
          />
          <export-excel
            prop-url="/api-base/categoryDv/exportExcelTitle"
            exprot-url="/api-base/categoryDv/exportExcel"
            :filter-params="filterParams"
          />
        </template>
      </main-header>
      <el-container
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
              <el-table-column type="selection" />
              <el-table-column
                prop="categoryName"
                min-width="180px"
                show-overflow-tooltip
              >
                <template
                  slot="header"
                  slot-scope="scope"
                >
                  <!-- 品类 -->
                  <i class="toRequired">*</i>{{ $t('dataConfMod.category') }}
                </template>
                <template slot-scope="scope">
                  <template v-if="scope.row.add || scope.row.edit">
                    <el-form-item
                      :prop="'tableData.' + scope.$index + '.categoryName'"
                      :rules="categoryDvModle.rules.categoryName"
                    >
                      <c-category-select
                        v-model="scope.row.categoryName"
                        :scope="scope.row"
                        show-key="categoryName"
                        @select="comfirmSelect"
                      />
                    </el-form-item>
                  </template>
                  <span v-else>{{ scope.row.categoryFullName }}</span>
                </template>
              </el-table-column>
              <!-- 组织 -->
              <el-table-column
                prop="orgName"
                :label="$t('dataConfMod.orgName')"
                min-width="180px"
                show-overflow-tooltip
              >
                <template slot-scope="scope">
                  <template v-if="scope.row.add || scope.row.edit">
                    <el-form-item :prop="'tableData.' + scope.$index + '.orgId'">
                      <organization-select-tree
                        v-model="scope.row.orgId"
                        :scope="scope"
                        @select="addOrgHandle"
                      />
                    </el-form-item>
                  </template>
                  <span v-else>{{ scope.row.orgName }}</span>
                </template>
              </el-table-column>
              <!-- 员工姓名 -->
              <el-table-column
                prop="fullName"
                :label="$t('dataConfMod.staffName')"
                min-width="150"
              >
                <template
                  slot="header"
                  slot-scope="scope"
                >
                  <i class="toRequired">*</i>{{ $t('dataConfMod.staffName') }}
                </template>
                <template slot-scope="scope">
                  <template v-if="scope.row.add || scope.row.edit">
                    <el-form-item
                      :prop="'tableData.' + scope.$index + '.fullName'"
                      :rules="categoryDvModle.rules.fullName"
                    >
                      <el-input
                        v-model="scope.row.fullName"
                        :placeholder="$t('common.pleaseSelect')"
                      >
                        <el-button
                          slot="append"
                          icon="el-icon-search"
                          @click="selectPeople(scope.$index)"
                        />
                      </el-input>
                    </el-form-item>
                  </template>
                  <span v-else>{{ scope.row.fullName }}</span>
                </template>
              </el-table-column>
              <!-- 员工账号 -->
              <el-table-column
                prop="userName"
                :label="$t('dataConfMod.staffUserName')"
                min-width="120"
              >
                <template slot-scope="scope">
                  <span>{{ scope.row.userName }}</span>
                </template>
              </el-table-column>
              <!-- 生效日期 -->
              <el-table-column
                prop="startDate"
                :label="$t('dataConfMod.startDate')"
                min-width="160"
              >
                <template
                  slot="header"
                  slot-scope="scope"
                >
                  <i class="toRequired">*</i>{{ $t('dataConfMod.startDate') }}
                </template>
                <template slot-scope="scope">
                  <template v-if="scope.row.add || scope.row.edit">
                    <el-form-item
                      :prop="'tableData.' + scope.$index + '.startDate'"
                      :rules="categoryDvModle.rules.startDate"
                    >
                      <el-date-picker
                        v-model="scope.row.startDate"
                        type="date"
                        value-format="yyyy-MM-dd"
                        :placeholder="$t('common.pleaseSelectDate')"
                      />
                    </el-form-item>
                  </template>
                  <span v-else>{{ scope.row.startDate }}</span>
                </template>
              </el-table-column>
              <!-- 失效日期 -->
              <el-table-column
                prop="endDate"
                :label="$t('dataConfMod.endDate')"
                min-width="160"
              >
                <template slot-scope="scope">
                  <template v-if="scope.row.add || scope.row.edit">
                    <el-form-item :prop="'tableData.' + scope.$index + '.endDate'">
                      <el-date-picker
                        v-model="scope.row.endDate"
                        type="date"
                        value-format="yyyy-MM-dd"
                        :placeholder="$t('common.pleaseSelectDate')"
                      />
                    </el-form-item>
                  </template>
                  <span v-else>{{ scope.row.endDate }}</span>
                </template>
              </el-table-column>
              <!-- 更新时间 -->
              <el-table-column
                prop="lastUpdateDate"
                :label="$t('common.updateTime')"
                min-width="160px"
              />
              <!-- 更新人 -->
              <el-table-column
                prop="lastUpdatedUserName"
                :label="$t('common.updatePeople')"
                min-width="100"
              />
              <!-- 操作 -->
              <el-table-column
                fixed="right"
                :label="$t('common.operation')"
                width="100"
              >
                <template slot-scope="scope">
                  <el-button
                    v-if="scope.row.categoryDvId"
                    type="text"
                    @click="handleEditClick(scope.$index, scope.row)"
                  >
                    <!-- 编辑 -->
                    {{ $t('common.edit') }}
                  </el-button>
                  <el-button
                    v-if="scope.row.add"
                    type="text"
                    @click="handleDelClick(scope.$index, scope.row)"
                  >
                    <!-- 删除 -->
                    {{ $t('common.delete') }}
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-form>
        </el-main>
        <el-footer class="page-bar">
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
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import CPagination from 'lib@/components/c-pagination'
import CPeopleSelector from '@/library/components/c-people-selector'
import OrganizationSelectTree from 'lib@/components/organization-selector'
import CCategorySelect from 'lib@/components/c-category-select'
import { formatTimeToDate } from '@/utils'
import ExportExcel from 'lib@/components/export-excel'
import MImport from 'lib@/components/import'
import { downloadFileLink } from 'lib@/utils/file'

export default {
  name: 'CategoryDivision',
  components: {
    CPagination,
    MainHeader,
    FormWrapper,
    MImport,
    ExportExcel,
    CPeopleSelector,
    OrganizationSelectTree,
    CCategorySelect
  },
  data () {
    return {
      gridId: 'categoryDivision',
      loading: false,
      iModal: {
        title: this.$t('components.eio.importTitle'), // Excel导入
        upLoadUrl: '/api-base/categoryDv/importExcel'
      },
      extraData: {
        sourceType: 'WEB_APP',
        uploadType: 'FASTDFS',
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
        prop: 'categoryName',
        label: () => this.$t('dataConfMod.category') // '品类'
      },
      {
        prop: 'fullName',
        label: () => this.$t('dataConfMod.staffName') // '员工姓名'
      },
      {
        prop: 'isActive',
        label: () => this.$t('dataConfMod.enabled'), // '是否有效'
        type: 'select',
        options: [
          { label: this.$t('common.yes'), value: 'Y' },
          { label: this.$t('common.no'), value: 'N' }
        ]
      }
    ]
    this.getQuerydata() //  查询数据
  },
  methods: {
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
    editTab () {},
    getQuerydata (v) {
      this.queryParam = v || {}
      this.fatchListData(this.queryParam, { pageSize: 15 })
    },
    // 选择组织
    addOrgHandle (e, dd, scope) {
      scope.row.orgId = e ? e.organizationId : null
      scope.row.orgCode = e ? e.organizationCode : ''
      scope.row.orgName = e ? e.organizationName : ''
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
      this.$api.base.basicSetting.getCategoryDivisionList(queryObj).then(res => {
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
      let categoryDvId = row.categoryDvId
      if (categoryDvId) {
        this.$api.base.basicSetting.categoryDvDel({ categoryDvId }).then(res => {
          if (res) {
            this.getQuerydata()
          }
        })
      } else {
        this.categoryDvModle.tableData.splice(index, 1)
      }
    },
    // 行编辑
    handleEditClick (index, row) {
      this.categoryDvModle.tableData[index].edit = true
    },
    addOne () {
      this.categoryDvModle.tableData.unshift({
        add: true,
        edit: false,
        orgId: null,
        orgCode: '',
        orgName: '',
        categoryId: null,
        categoryCode: '',
        categoryName: '',
        categoryFullName: '',
        userId: null,
        userName: '',
        fullName: '',
        startDate: formatTimeToDate(new Date().getTime(), 'Y-M-D')
      })
      this.$nextTick(() => {
        this.$refs.categoryTable.toggleRowSelection(this.categoryDvModle.tableData[0], true)
      })
    },
    saveDataHandle () {
      if (this.selections.length > 0) {
        this.$refs['categoryDvTable'].validate((valid, categoryDvModle) => {
          if (valid) {
            let subData = this.selections
            this.$api.base.basicSetting.saveOrUpdateDvBatch(subData).then(res => {
              if (res) {
                this.$message({
                  message: res.message,
                  type: 'success'
                })
                this.getQuerydata()
              }
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
    importOne () {},
    exportOne () {},
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
.the_material_wrapper {
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
