<template>
  <el-container
    class="flex-container-notab the_material_wrapper"
    direction="vertical"
  >
    <el-main>
      <form-wrapper
        :select-dictionary="selectDictionary"
        :form-array="queryForm"
        @getFormData="getQuerydata"
      />

      <main-header
        :l-span="22"
        :r-span="2"
        style="padding-left: 10px"
      >
        <template slot="left">
          <AuthorityButton
            type="primary"

            code="sup:fileManagement:add"
            @click="addOne"
          >
            <!-- 新增 -->
            {{ $t('common.add') }}
          </AuthorityButton>
          <AuthorityButton
            code="sup:fileManagement:saveDataHandle"

            :disabled="selections.length == 0"
            @click="saveDataHandle"
          >
            <!-- 提交 -->
            {{ $t('common.submit') }}
          </AuthorityButton>
        </template>
      </main-header>
      <el-container
        direction="vertical"
        class="tablePd"
      >
        <el-main style="flex-grow: 1;display: flex;flex-direction: column;position:relative;">
          <el-form
            ref="materialTable"
            class="tableForm"
            :model="materialModle"
            :rules="materialModle.rules"
          >
            <el-table
              ref="mtTable"
              v-loading="loading"
              stripe
              border
              height="100%"
              :data="materialModle.tableData"
              style="height: 100%;"
              @selection-change="handleSelectionChange"
              @cell-click="cellClick"
            >
              <el-table-column
                type="selection"
                fixed="left"
                width="50"
                align="center"
              />
              <!-- 流程场景 -->
              <el-table-column
                prop="senceCode"
                :label="$t('dataConfMod.processSence')"
                min-width="120"
              >
                <template slot-scope="scope">
                  <template v-if="scope.row.add || scope.row.edit">
                    <el-form-item
                      :prop="'tableData.' + scope.$index + '.senceCode'"
                      :rules="materialModle.rules.senceCode"
                    >
                      <!-- 流程场景 -->
                      <el-select
                        v-model="scope.row.senceCode"
                        filterable
                        :placeholder="$t('dataConfMod.processSence')"
                        @focus="selectFocus(scope.$index)"
                        @change="unitTypeChange"
                      >
                        <el-option
                          v-for="item in purUnit"
                          :key="item.id"
                          :label="item.label"
                          :value="item.value"
                        />
                      </el-select>
                    </el-form-item>
                  </template>
                  <span v-else>{{ scope.row.senceName }}</span>
                </template>
              </el-table-column>
              <!-- 页内功能类型 -->
              <el-table-column
                prop="ceeaSmallModule"
                :label="$t('dataConfMod.ceeaSmallModule')"
                min-width="120"
              >
                <template slot-scope="scope">
                  <template>
                    <el-form-item :prop="'tableData.' + scope.$index + '.ceeaSmallModule'">
                      <DictSelect
                        v-if="scope.row.senceCode === 'dashboard'"
                        v-model="scope.row.ceeaSmallModule"
                        :disabled="!scope.row.add && !scope.row.edit"
                        code="FILE_SMALLl_MODULE"
                      />
                      <el-input
                        v-else
                        v-model="scope.row.ceeaSmallModule"
                        :disabled="!scope.row.add && !scope.row.edit"
                      />
                    </el-form-item>
                  </template>
                  <!-- <span v-else>{{ scope.row.ceeaSmallModule }}</span> -->
                </template>
              </el-table-column>
              <!-- 附件名称 -->
              <el-table-column
                prop="attachmentName"
                :label="$t('dataConfMod.attachmentName')"
                min-width="120"
              >
                <template slot-scope="scope">
                  <template v-if="scope.row.add || scope.row.edit">
                    <el-form-item
                      :prop="'tableData.' + scope.$index + '.attachmentName'"
                      :rules="materialModle.rules.attachmentName"
                    >
                      <el-input
                        v-show="true"
                        v-model="scope.row.attachmentName"
                      />
                    </el-form-item>
                  </template>
                  <span v-else>{{ scope.row.attachmentName }}</span>
                </template>
              </el-table-column>
              <!-- 附件模板 -->
              <el-table-column
                :label="$t('dataConfMod.attachmentTemplate')"
                min-width="150px"
              >
                <template slot-scope="scope">
                  <SrmCommonFile
                    :extra-data="fileInfo"
                    :default-file="{
                      fileId: scope.row.fileuploadId,
                      fileName: scope.row.fileSourceName
                    }"
                    :readonly="false"
                    @on-change="({file}) => handleUploadSuccess(file,scope.row)"
                  />
                </template>
              </el-table-column>
              <!-- 选择组织 -->
              <!--<el-table-column prop="orgIds" :label="$t('dataConfMod.selectOrg')" min-width="150px">
                <template slot-scope="scope">
                  <template>
                    <el-form-item
                      :prop="'tableData.' + scope.$index + '.orgIds'"
                      :rules="materialModle.rules.orgIds"
                    >
                      <el-button
                        @click="showDialog(scope.$index, scope.row)"
                        size="medium"
                        type="primary"
                        icon="el-icon-edit"
                      ></el-button>
                    </el-form-item>
                  </template>
                </template>
              </el-table-column>
              <el-table-column
                prop="categoryIds"
                :label="$t('dataConfMod.selectCategory')"
                min-width="150px"
              >
                <template slot-scope="scope">
                  <template>
                    <el-form-item
                      :prop="'tableData.' + scope.$index + '.categoryIds'"
                      :rules="materialModle.rules.categoryIds"
                    >
                      <el-button
                        @click="showDialog(scope.$index, scope.row, false)"
                        size="medium"
                        type="primary"
                        icon="el-icon-edit"
                      ></el-button>
                    </el-form-item>
                  </template>
                </template>
              </el-table-column>-->
              <!-- 是否必填 -->
              <el-table-column
                prop="required"
                :label="$t('dataConfMod.isRequested')"
                align="center"
              >
                <template slot-scope="{ row }">
                  <el-checkbox
                    v-model="row.required"
                    true-label="Y"
                    false-label="N"
                  />
                </template>
              </el-table-column>
              <!-- 是否启用 -->
              <el-table-column
                prop="enabled"
                :label="$t('dataConfMod.enabledUse')"
                align="center"
              >
                <template slot-scope="{ row }">
                  <el-checkbox
                    v-model="row.enabled"
                    true-label="Y"
                    false-label="N"
                  />
                </template>
              </el-table-column>
              <!-- 更新时间 -->
              <el-table-column
                prop="lastUpdateDate"
                :label="$t('common.updateTime')"
                min-width="150px"
              />
              <!-- 更新人 -->
              <el-table-column
                prop="lastUpdatedUserName"
                :label="$t('common.updatePeople')"
              >
                <template slot-scope="scope">
                  <span>{{
                    scope.row.lastUpdatedUserName
                      ? scope.row.lastUpdatedUserName
                      : scope.row.createdUserName
                  }}</span>
                </template>
              </el-table-column>
              <!-- 操作 -->
              <el-table-column
                fixed="right"
                :label="$t('common.operation')"
                width="130"
              >
                <template slot-scope="scope">
                  <AuthorityButton
                    v-if="!scope.row.materialId"
                    type="text"
                    code="sup:fileManagement:handleDelClick"
                    @click.stop.prevent="handleDelClick(scope.$index, scope.row)"
                  >
                    <!-- 删除 -->
                    {{ $t('common.delete') }}
                  </AuthorityButton>
                  <AuthorityButton
                    type="text"
                    code="sup:fileManagement:handleEditClick"
                    @click.stop.prevent="handleEditClick(scope.$index, scope.row)"
                  >
                    <!-- 编辑 -->
                    {{ $t('common.edit') }}
                  </AuthorityButton>
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
      </el-container>
    </el-main>
    <srm-dialog
      :title="dialogTitle"
      :visible="dialogVisible"
      @close="dialogCancleHandle"
    >
      <div style="height: 330px;overflow: auto;">
        <template v-if="selectOrganation">
          <treeselect
            v-model="currentOrganationIds"
            :normalizer="normalizer"
            :no-children-text="$t('common.noChildrenText')"
            :no-options-text="$t('common.noOptionsText')"
            :no-results-text="$t('common.noResultsText')"
            :placeholder="$t('common.msgSelectOrganation')"
            always-open
            :append-to-body="false"
            :searchable="true"
            :options="selectTreeOptions"
            multiple
            flatten-search-results
            :default-expand-level="Infinity"
            flat
            @select="treeselectChange"
          />
        </template>
        <template v-else>
          <treeselect
            v-model="currentCategoryIds"
            :normalizer="categoryNormalizer"
            always-open
            :no-children-text="$t('common.noChildrenText')"
            :no-options-text="$t('common.noOptionsText')"
            :no-results-text="$t('common.noResultsText')"
            :placeholder="$t('common.msgCategoryNormalizer')"
            :append-to-body="false"
            :searchable="true"
            :options="selectCategoryTreeOptions"
            multiple
            flatten-search-results
            flat
            :load-options="loadOptions"
            :retry-text="$t('common.retryText')"
            :retry-title="$t('common.retryTitle')"
            :loading-text="$t('common.loadingText')"
            @select="treeselectChange"
          />
        </template>
      </div>
      <div slot="footer">
        <el-button

          @click="dialogCancleHandle"
        >
          <!-- 取消 -->
          {{ $t('common.cancel') }}
        </el-button>
        <el-button

          type="primary"
          @click="dialogOkHandle"
        >
          <!-- 确定 -->
          {{ $t('common.confirm') }}
        </el-button>
      </div>
    </srm-dialog>
  </el-container>
</template>
<script>
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import CPagination from 'lib@/components/c-pagination'
import Treeselect, { LOAD_CHILDREN_OPTIONS } from '@riophae/vue-treeselect'
import '@riophae/vue-treeselect/dist/vue-treeselect.css'

export default {
  name: 'FileManagement',
  components: {
    CPagination,
    MainHeader,
    FormWrapper,
    Treeselect
  },
  data () {
    return {
      dialogTitle: this.$t('dataConfMod.selectOrg'), // "选择组织",
      currentRows: {},
      currentIndex: -1,
      dialogVisible: false,
      gridId: 'list',
      loading: false,
      selectDictionary: {},
      pageSize: 15,
      currentRow: null,
      tableHeader: [],
      currentOrganationIds: [],
      currentCategoryIds: [],
      queryParam: {
        pageSize: 15
      },
      queryForm: [],
      purUnit: [],
      catList: [], // 采购分类
      materialModle: {
        tableData: [],
        rules: {
          materialCode: { type: 'string', required: true },
          materialName: { type: 'string', required: true },
          unit: { type: 'string', required: true },
          categoryName: { type: 'string', required: true }
        }
      },
      selectTreeOptions: [],
      selectCategoryTreeOptions: [],
      pageInfo: {
        total: 0,
        pageNum: 1,
        pageSize: 15
      },
      listSceneCodeAndSceneName: [],
      selections: [],
      selectOrganation: true,
      fileInfo: {
        uploadType: 'FASTDFS',
        sourceType: 'WEB_APP',
        fileModular: 'base', // 文件所属模块 -》基础模块
        fileFunction: 'materialMaintenance', // 文件所属功能
        fileType: 'images' // 文件所属类型
      }
    }
  },
  created () {
    this.queryForm = [
      {
        prop: 'senceCode',
        label: () => this.$t('dataConfMod.processSence'), // "流程场景"
        type: 'select'
      },
      // {
      //   prop: "ceeaSmallModule",
      //   label: '页内功能类型' //"页内功能类型"
      // },
      {
        prop: 'attachmentName',
        label: () => this.$t('dataConfMod.attachmentName') // "附件名称"
      },
      {
        prop: 'enabled',
        label: () => this.$t('dataConfMod.enabledUse'), // "是否启用"
        type: 'select',
        options: [
          { label: this.$t('common.yes'), value: 'Y' },
          { label: this.$t('common.no'), value: 'N' }
        ]
      }
    ]
    this.getQuerydata() //  查询数据
  },
  mounted () {
    this.$api.base.basicSetting.listSceneCodeAndSceneName().then(res => {
      const list = (res.data || []).map(i => ({
        id: i.senceCode,
        label: i.senceName,
        value: i.senceCode
      }))
      this.selectDictionary = { ...this.selectDictionary, senceCode: list }
      this.purUnit = list
    })
    // 组织架构
    this.$api.accountAccess.organaztionTreehttp({}).then(res => {
      this.selectTreeOptions = res.data
    })
    this.$api.base.basicSetting.getCatChildrenData({ categoryId: -1 }).then(response => {
      console.log(response.data)
      this.selectCategoryTreeOptions = response.data.map(i => ({
        ...i,
        children: null
      }))
    })
  },
  methods: {
    cellClick (row) {
      this.$refs.mtTable.toggleRowSelection(row, true)
    },
    loadOptions ({ action, parentNode, callback }) {
      if (action === LOAD_CHILDREN_OPTIONS) {
        this.$api.base.basicSetting.getCatChildrenData({ categoryId: parentNode.categoryId })
          .then(res => {
            parentNode.children = res.data.map(i => ({ ...i, children: null }))
            callback()
          })
          .catch(err => {
            parentNode.children = null
            callback(new Error(err.message))
          })
      }
    },
    dialogOkHandle () {
      if (this.selectOrganation) {
        this.currentRows.orgIds = []
        this.currentRows.orgIds = this.currentOrganationIds
      } else {
        this.currentRows.categoryIds = []
        this.currentRows.categoryIds = this.currentCategoryIds
      }
      this.closeDialog()
    },
    dialogCancleHandle () {
      this.currentOrganationIds = []
      this.currentCategoryIds = []
      this.closeDialog()
    },
    closeDialog () {
      this.dialogVisible = false
    },
    showDialog (index, row, selectOrganation = true) {
      console.log(index, row)
      if (index !== -1) this.currentIndex = index
      if (selectOrganation) {
        this.currentOrganationIds = row.orgIds
      } else {
        this.currentCategoryIds = row.categoryIds
      }
      this.currentRows = row
      this.selectOrganation = selectOrganation
      this.dialogTitle = selectOrganation
        ? this.$t('dataConfMod.selectOrg')
        : this.$t('dataConfMod.selectCategory') // "选择组织" : "选择品类";
      this.dialogVisible = true
    },
    treeselectChange (nodes) {
      console.log(this.currentRows)
      console.log(nodes)
      // this.currentRows[this.currentIndex].orgIds;
    },
    editTab () {},
    normalizer (node) {
      const result = {
        id: node.organizationId,
        label: node.organizationName
      }
      if (node && (node.childOrganRelation || []).length) {
        result.children = node.childOrganRelation
      }
      return result
    },
    categoryNormalizer (node) {
      const result = {
        id: node.categoryId,
        label: node.categoryName
      }
      return result
    },
    getQuerydata (v) {
      console.log(v)
      this.queryParam = v || {}
      this.$nextTick(() => this.fatchListData(this.queryParam, {}))
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
    // 事件切换选择
    unitTypeChange (val) {
      let row = this.purUnit.find(item => {
        return item.value === val
      })
      // this.materialModle.tableData[this.currentRow].senceCode = row.value
      this.materialModle.tableData[this.currentRow].senceName = row.label
      if (val !== 'dashboard') {
        this.materialModle.tableData[this.currentRow].ceeaSmallModule = ''
      }
    },
    // 查询列表数据
    fatchListData (p1, p2) {
      let queryObj = { ...p1, ...p2 }
      this.$api.base.basicSetting.listPageByParm(queryObj).then(res => {
        if (res.data && res.data.list) {
          this.loading = false
          this.pageInfo.total = res.data.total
          this.pageInfo.pageNum = res.data.pageNum
          this.pageInfo.pageSize = res.data.pageSize
          this.materialModle.tableData = res.data.list.map(({ categoryIds, orgIds, ...rest }) => ({
            ...rest,
            categoryIds: categoryIds ? categoryIds.split(',') : null,
            orgIds: orgIds ? orgIds.split(',').map(j => Number(j)) : null,
            edit: false
          }))
        }
      })
    },
    handleSelectionChange (val) {
      this.selections = val
    },
    // 上传附件成功
    handleUploadSuccess (file, row) {
      const { fileId = '', fileName = '' } = file || {}
      row.fileuploadId = fileId.toString()
      row.fileSourceName = fileName
      this.$set(row, 'fileTip', true)
    },
    // 附件删除
    handleRemove (fileId) {
      this.materialModle.tableData[this.rowIndex].ctcAttachmentDto = {}
    },
    handleScriptProgress (percent) {},
    // 删除文件
    handleAttachmentRemove (row) {
      this.$api.base.basicSetting.fileuploadDelete({ sceneAttachmentId: row.sceneAttachmentId }).then(res => {
        this.$message({ type: 'success', message: res.message })
      })
      row.fileuploadId = ''
      row.fileSourceName = ''
    },
    // 文件上传========]
    // 选择组织
    comfirmSelect (node, scope) {
      scope.categoryId = node ? node.categoryId : null
      scope.categoryCode = node ? node.categoryCode : ''
      scope.categoryName = node ? node.categoryName : ''
      scope.categoryFullName = node ? node.categoryFullName : ''
    },
    // 选择组织
    addOrgHandle (e, scope) {
      scope.orgId = e ? e.organizationId : ''
      scope.orgCode = e ? e.organizationCode : ''
      scope.orgName = e ? e.organizationName : ''
    },
    // 行删除
    handleDelClick (index, row) {
      const sceneAttachmentId = row.sceneAttachmentId
      if (sceneAttachmentId) {
        // 当前操将永久删除这条数据，确认删除这条数据？
        this.$confirm(this.$t('common.confirmDelete'), {
          confirmButtonText: this.$t('common.confirm'),
          cancelButtonText: this.$t('common.cancel'),
          type: 'warning'
        })
          .then(() => {
            this.$api.base.basicSetting.deleteSceneAttachment({ sceneAttachmentId }).then(res => {
              this.$message({ type: 'success', message: res.message })
              if (res) {
                this.getQuerydata()
              }
            })
          })
          .catch(() => {})
      } else {
        this.materialModle.tableData.splice(index, 1)
      }
    },
    // 行编辑
    handleEditClick (index, row) {
      this.materialModle.tableData[index].edit = true
      // row.edit = true
      this.$nextTick(() => {
        // this.$refs.mtTable.doLayout()
      })
    },
    // 新增
    addOne () {
      this.materialModle.tableData.unshift({
        add: true,
        enabled: 'Y',
        required: 'N',
        fileSourceName: '',
        fileuploadId: '',
        senceCode: '',
        ceeaSmallModule: '',
        attachmentName: ''
      })
    },
    // 保存
    saveDataHandle () {
      if (this.selections.length > 0) {
        this.$refs['materialTable'].validate((valid, materialModle) => {
          if (valid) {
            // let subData = this.materialModle.tableData
            const subData = this.selections.map(({ categoryIds = [], orgIds = [], ...rest }) => ({
              ...rest,
              categoryIds: categoryIds ? categoryIds.join(',') : '',
              orgIds: orgIds ? orgIds.join(',') : ''
            }))
            this.$api.base.basicSetting.batchSaveOrUpdateSceneAttachment(subData).then(res => {
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
              message: this.$t('common.pleasefinishRequired'), // "请输入必填项"
              type: 'warning'
            })
          }
        })
      } else {
        this.$message({
          message: this.$t('common.cannotSave'), // "请选择保存的数据"
          type: 'warning'
        })
      }
    },
    // 点击当前页
    handleCurrentChange (num) {
      let page = {
        pageNum: num
      }
      this.loading = true
      this.fatchListData(this.queryParam, page)
    },
    // 选择分页
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
</style>
<style>
.the_material_wrapper .el-table td .cell button {
  padding: 3px 10px !important;
}
/* .mainHeaderStyle {
  padding-left: 10px!important;
} */
</style>
