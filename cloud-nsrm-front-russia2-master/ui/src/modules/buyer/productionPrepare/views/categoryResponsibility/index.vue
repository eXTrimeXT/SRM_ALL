<template>
  <el-container class="flex-container-notab the_material_wrapper" direction="vertical">
    <el-main>
      <FormWrapper
        :form-array="queryForm"
        @getFormData="getQuerydata"
        @synchronous-value="syncFilterParams"
      />

      <MainHeader :lSpan="22" :rSpan="2">
        <template slot="left">
          <MImport
            ref="import"
            code="sup:supplierCategoryResponsibility:import"
            style="display: inline-block;"
            :title="excel"
            :upLoadUrl="iModal.upLoadUrl"
            :extraData="extraData"
            @downloadTemplate="openDialogVisible('template')"
            @handleSuccess="handleSuccess"
          />
          <ExportExcel
            pageUrl="/api-ppap/productionLeader/listPageByParam"
            :filterParams="queryParam"
            :tableHeader="tableHeader"
            :dictCodes="dictCodes"
            exportMode="front"
            type="default"
          />
          <!-- <el-button type="primary"  @click="addOne">-->
          <!--  {{ $t("common.add") }}-->
          <!-- </el-button>-->
          <!-- <AuthorityButton
            code="sup:supplierCategoryResponsibility:save"
            @click="saveDataHandle"
          >
            {{ $t("common.save") }}
          </AuthorityButton> -->
        </template>
      </MainHeader>
      <el-container direction="vertical" class="tablePd" style="padding:0;">
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
              <!-- <el-table-column type="selection" /> -->
              <!-- 供应商编码 -->
              <el-table-column
                prop="companyCode"
                :label="$t('common.vendorCode')"
                min-width="180"
                align="center"
                show-overflow-tooltip
              />
              <!-- 供应商名称 -->
              <el-table-column
                prop="companyName"
                :label="$t('common.companyName')"
                min-width="180"
                align="center"
                show-overflow-tooltip
              />
              <!-- 生准担当工号-->
              <el-table-column
                prop="responsibilityCode"
                :label="$t('cusEntry.supplement20250211.shengZhunDangRenGongHao')"
                min-width="180"
                align="center"
                show-overflow-tooltip
              />
              <el-table-column prop="fullName" :label="$t('productionPrepare.responsibilityName')" min-width="150" align="center">
                <template slot="header">
                  <i class="toRequired">*</i>
                  <!-- 生准担当名称 -->
                  {{ $t("productionPrepare.responsibilityName") }}
                </template>
                <template slot-scope="scope">
                  <template v-if="scope.row.add || scope.row.edit">
                    <el-form-item
                      :prop="
                        'tableData.' + scope.$index + '.responsibilityName'
                      "
                      :rules="{ required: true }"
                    >
                      <QuickSearch
                        :showInput="scope.row.responsibilityName"
                        show-key="nickName"
                        :scope-data="scope.row"
                        name="scc_rbac_user_display"
                        @close-quicksearch="getAgentObj"
                      />
                    </el-form-item>
                  </template>
                  <span v-else>{{ scope.row.responsibilityName }}</span>
                </template>
              </el-table-column>
              <!-- 操作 -->
              <el-table-column
                fixed="right"
                :label="$t('common.operation')"
                min-width="180"
                align="center"
              >
                <template slot-scope="scope">
                  <AuthorityButton
                    v-if="!scope.row.edit"
                    type="text"
                    code="sup:supplierCategoryResponsibility:edit"
                    @click="handleEditClick(scope.$index, scope.row)"
                  >
                    {{ $t("common.edit") }}
                  </AuthorityButton>
                  <AuthorityButton
                    v-else
                    type="text"
                    code="sup:supplierCategoryResponsibility:save"
                    @click="saveDataHandle(scope.$index, scope.row)"
                  >
                    {{ $t("common.save") }}
                  </AuthorityButton>

                  <AuthorityButton
                    type="text"
                    code="sup:supplierCategoryResponsibility:delete"
                    @click="handleDelClick(scope.$index, scope.row)"
                  >
                    {{ $t("common.delete") }}
                  </AuthorityButton>
                </template>
              </el-table-column>
            </el-table>
          </el-form>
        </el-main>
        <el-footer class="page-bar">
          <CPagination
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
        <CPeopleSelector
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
import OrganizationSelector from 'lib@/components/organization-selector'
import CPeopleSelector from '@/library/components/c-people-selector'
import CCategorySelect from 'lib@/components/c-category-select'
import ExportExcel from 'lib@/components/export-excel'
import MImport from 'lib@/components/import'
import { downloadFileLink, downloadFileLinkByPost } from 'lib@/utils/file'
import { categoryResponsibility } from 'modb@/productionPrepare/api'
export default {
  name: 'SupplierCategoryResponsibility',
  components: {
    CPagination,
    MainHeader,
    FormWrapper,
    MImport,
    ExportExcel,
    CPeopleSelector,
    CCategorySelect,
    OrganizationSelector,
    QuickSearch
  },
  data () {
    return {
      dictCodes: {},
      excel: this.$t('components.eio.importTitle'), // Excel导入
      gridId: 'categoryResponsibility',
      loading: false,
      iModal: {
        title: this.$t('components.eio.importTitle'), // Excel导入
        upLoadUrl: '/api-ppap/productionLeader/importExcel'
      },
      extraData: {
        fileModular: 'base',
        fileFunction: 'categoryResponsibility',
        fileType: 'excel'
      },
      pageSize: 15,
      currentRow: null,
      tableHeader: [
        {
          prop: 'companyCode',
          // '供应商编码'
          label: this.$t('common.vendorCode')  
        },
        {
          prop: 'companyName',
          // '供应商名称'
          label: this.$t('common.companyName')
        },
        {
          prop: 'responsibilityCode',
          // '生准担当工号'
          label: this.$t('cusEntry.supplement20250211.shengZhunDangRenGongHao')
        },
        {
          prop: 'responsibilityName',
          // '生准担当名称'
          label: this.$t('productionPrepare.responsibilityName')
        }
      ],
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
        label: this.$t('productionPrepare.responsibilityName'),  // '生准担当名称'
        type: 'quicksearch',
        showKey: 'nickname',
        propKey: 'userId',
        name: 'scc_rbac_user_display'
      }
    ]
    this.getQuerydata() //  查询数据
  },
  methods: {
    async openDialogVisible (type) {
      this.multipleSelection = []
      downloadFileLinkByPost(
        '/api-ppap/productionLeader/downloadTemplate',
        this.$t('productionPrepare.categoryResponsibilityTip2') + '.xlsx'  // '导入供应商与生准担当模板'
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
        this.$t('productionPrepare.categoryResponsibilityTip2') + '.xlsx'  // '导入供应商与生准担当模板'
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
      categoryResponsibility.listPageByParam(queryObj).then(res => {
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
        .catch(err => {
          console.log(err)
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
      categoryResponsibility.delete({ id: row.productionLeaderId }).then(data => {
          this.$message.success(this.$t('common.successDelete')) // 删除成功
          this.getQuerydata()
        })
        .catch(err => {
          console.log(err)
        })
    },
    // 行编辑
    handleEditClick (index, row) {
      this.categoryDvModle.tableData[index].edit = true
      this.curRow = index
    },
    handleCancelClick (index, row) {
      this.categoryDvModle.tableData[index].edit = false
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
        this.$refs.categoryTable.toggleRowSelection(
          this.categoryDvModle.tableData[0],
          true
        )
      })
    },
    saveDataHandle (index, row) {
      let dataArr = [row]
      console.log(dataArr)
      if (dataArr.length > 0) {
        this.$refs['categoryDvTable'].validate((valid, categoryDvModle) => {
          if (valid) {
            categoryResponsibility.modifyResponsibilityByBatch(dataArr).then(data => {
                this.$message.success(this.$t('common.successSave')) // 保存成功
                this.getQuerydata()
              })
              .catch(err => {
                console.log(err)
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
      let responsibilityId = val ? val.userId : ''
      let responsibilityCode = val ? val.ceeaEmpNo : ''
      let responsibilityUserName = val ? val.nickname : ''
      let responsibilityName = val ? val.nickname : ''
      this.$set(scope, 'responsibilityName', responsibilityName)
      this.$set(scope, 'responsibilityId', responsibilityId)
      this.$set(scope, 'responsibilityCode', responsibilityCode)
      this.$set(scope, 'responsibilityUserName', responsibilityUserName)
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
