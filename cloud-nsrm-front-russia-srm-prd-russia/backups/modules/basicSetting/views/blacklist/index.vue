<template>
  <el-container
    direction="vertical"
    class="flex-container blacklist_list_wrapper"
  >
    <form-wrapper
      :form-array="filterConfig"
      @getFormData="search"
      @synchronous-value="syncFilterParams"
    >
      <template #blackFlat="{ scope }">
        <DictSelect
          v-model="scope.blackFlat"
          code="BLACK_FLAT"
        />
      </template>
      <template #type="{ scope }">
        <DictSelect
          v-model="scope.type"
          code="TYPE"
        />
      </template>
    </form-wrapper>
    <el-main>
      <easy-table
        ref="table"
        :methods="methods"
        :columns="columns"
        :selection="true"
        row-key="blacklistId"
        table-name="blacklist_table"
        :query-params.sync="queryParams"
        @selection-change="handleSelectionChange"
      >
        <template #btns>
          <AuthorityButton
            type="primary"
            @click="add"
          >
            {{ $t('common.add') }}
          </AuthorityButton>
          <AuthorityButton
            type="primary"
            @click="saveListData"
          >
            {{
              $t('common.save')
            }}
          </AuthorityButton>
          <m-import
            style="display: inline-block;margin: 0 10px;"
            title="导入"
            up-load-url="/api-base/base/blacklist/importExcel"
            :extra-data="extraData"
            @downloadTemplate="downloadTemplate"
            @handleSuccess="handleSuccess"
          />
          <export-excel
            page-url="/api-base/base/blacklist/listPage"
            :filter-params="queryParams"
            :table-header="tableHeader"
            :dict-codes="dictCodes"
            timeout="1000000"
            export-mode="front"
          />
        </template>
        <template #blackFlat="{ scope }">
          <DictSelect
            v-model="scope.row.blackFlat"
            code="BLACK_FLAT"
          />
        </template>
        <template #reason="{ scope }">
          <el-input v-model="scope.row.reason" />
        </template>
        <template #description="{ scope }">
          <el-input v-model="scope.row.description" />
        </template>
        <template #companyId="{ scope }">
          <el-input v-model="scope.row.companyId" />
        </template>
        <template #companyName="{ scope }">
          <!-- <el-input v-model="scope.row.companyName" /> -->
          <quick-search
            :show-input="scope.row.companyName"
            show-key="companyName"
            :scope-data="scope.row"
            name="scc_sup_company_info_display"
            @close-quicksearch="val => getUserObjnotice(val, scope)"
          />
        </template>
        <template #type="{ scope }">
          <DictSelect
            v-model="scope.row.type"
            code="TYPE"
          />
        </template>
        <template #limitStartDate="{ scope }">
          <!-- <el-input v-model="scope.row.limitStartDate" /> -->
          <el-date-picker
            v-model="scope.row.limitStartDate"
            format="yyyy-MM-dd"
            value-format="yyyy-MM-dd"
            type="date"
            placeholder="选择日期"
          />
        </template>
        <template #limitEndDate="{ scope }">
          <!-- <el-input v-model="scope.row.limitEndDate" /> -->
          <el-date-picker
            v-model="scope.row.limitEndDate"
            format="yyyy-MM-dd"
            value-format="yyyy-MM-dd"
            type="date"
            placeholder="选择日期"
          />
        </template>
        <template #limitQualification="{ scope }">
          <el-input v-model="scope.row.limitQualification" />
        </template>
        <template #creationDate="{ scope }">
          <el-input
            v-model="scope.row.creationDate"
            disabled="disabled"
          />
        </template>
        <template #createdBy="{ scope }">
          <el-input
            v-model="scope.row.createdBy"
            disabled="disabled"
          />
        </template>
        <template #lastUpdatedBy="{ scope }">
          <el-input
            v-model="scope.row.lastUpdatedBy"
            disabled="disabled"
          />
        </template>
      </easy-table>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import EasyTable from 'lib@/components/BaseTable/EasyTable'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MImport from 'lib@/components/import'
import QuickSearch from 'lib@/components/QuickSearch'
import { downloadFileLink } from 'lib@/utils/file'
import ExportExcel from 'lib@/components/export-excel'
import { dateFormat } from 'lib@/utils/date-format'

export default {
  name: 'Blacklist',
  components: {
    EasyTable,
    QuickSearch,
    FormWrapper,
    MImport,
    ExportExcel
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      filterConfig: [
        { prop: 'blackFlat', label: '黑名单', type: 'slot', slot: 'blackFlat' },
        {
          prop: 'companyName',
          label: '供应商名称',
          type: 'quicksearch',
          showKey: 'companyName',
          propKey: 'companyName',
          name: 'scc_sup_company_info_display'
        },
        { prop: 'type', label: '供应商类型', type: 'slot', slot: 'type' },
        // { prop: "creationDate", label: "创建日期" },
        { prop: 'limitStartDate', label: '黑名单开始日期', type: 'date' },
        { prop: 'limitEndDate', label: '黑名单截止日期', type: 'date' },
        { prop: 'createdBy', label: '创建人' }
      ],
      extraData: {
        sourceType: 'WEB_APP',
        uploadType: 'FASTDFS',
        fileModular: 'base',
        fileFunction: 'blacklist',
        fileType: 'excel'
      },
      multipleSelection: [],
      dictCodes: {
        blackFlat: 'BLACK_FLAT',
        type: 'TYPE'
      },
      filterParams: {},
      tableHeader: [
        {
          prop: 'blackFlat',
          label: '黑名单',
          width: 100,
          dataType: 'dict',
          code: 'BLACK_FLAT'
        },
        {
          prop: 'reason',
          label: '黑名单原因',
          width: 100
        },
        {
          prop: 'description',
          label: '黑名单备注',
          width: 100
        },
        {
          prop: 'companyCode',
          label: '供应商编码',
          width: 100
        },
        {
          prop: 'companyName',
          label: '供应商名称',
          width: 100
        },
        {
          prop: 'type',
          label: '供应商类型',
          width: 100,
          dataType: 'dict',
          code: 'TYPE'
        },
        {
          prop: 'limitStartDate',
          label: '黑名单开始日期',
          width: 100
        },
        {
          prop: 'limitEndDate',
          label: '黑名单截止日期',
          width: 100
        },
        {
          prop: 'limitQualification',
          label: '限制资格',
          width: 100
        },
        {
          prop: 'creationDate',
          label: '创建日期',
          width: 100
        },
        {
          prop: 'createdBy',
          label: '创建人',
          width: 100
        },
        {
          prop: 'lastUpdatedBy',
          label: '最后更新人',
          width: 100
        },
        {
          prop: 'operation',
          label: '操作',
          showType: 'buttons',
          btnStyle: 'text',
          fixed: 'right',
          width: 130,
          buttons: [
            {
              callback: row => this.editHandle(row),
              // code: "pr:requirementApply:edit",
              // show: row => row.status === "DRAFT",
              formattor: () => {
                return this.$t('common.edit')
              }
            },
            {
              callback: row => this.deleteHandle(row),
              // code: "pr:requirementApply:edit",
              // show: row => row.status === "DRAFT",
              formattor: () => {
                return this.$t('common.delete')
              }
            }
          ]
        }
      ],
      queryParams: {},
      methods: {
        listPage: async params => {
          const res = await this.$api.base.blacklist.list(params)
          return res
        }
      },
      columns: [
        {
          attrs: {
            label: '黑名单',
            prop: 'blackFlat',
            width: '100px',
            formatter: value => this.$BLACK_FLAT('TYPE', value)
          },
          slot: 'blackFlat',
          rules: { required: 0, message: '必填' }
        },
        {
          attrs: {
            label: '黑名单原因',
            width: '140px',
            prop: 'reason'
          },
          slot: 'reason',
          rules: { required: 0, message: '必填' }
        },
        {
          attrs: {
            label: '黑名单备注',
            width: '100px',
            prop: 'description'
          },
          slot: 'description',
          rules: { required: 0, message: '必填' }
        },
        {
          attrs: {
            label: '供应商编码',
            prop: 'companyCode',
            width: '90px'
          }
        },
        {
          attrs: {
            label: '供应商名称',
            prop: 'companyName',
            width: '160px'
            // type: "quicksearch",
            // showKey: "companyName",
            // name: "scc_sup_company_info_display"
          },
          slot: 'companyName',
          rules: { required: 0, message: '必填' }
        },
        {
          attrs: {
            label: '供应商类型',
            width: '100px',
            prop: 'type',
            formatter: value => this.$getDictLabel('TYPE', value)
          },
          slot: 'type',
          rules: { required: 0, message: '必填' }
        },
        {
          attrs: {
            label: '黑名单开始日期',
            width: '180px',
            prop: 'limitStartDate'
          },
          slot: 'limitStartDate',
          rules: { required: 0, message: '必填' }
        },
        {
          attrs: {
            label: '黑名单截止日期',
            width: '180px',
            prop: 'limitEndDate'
          },
          slot: 'limitEndDate',
          rules: { required: 0, message: '必填' }
        },
        {
          attrs: {
            label: '限制资格',
            width: '100px',
            prop: 'limitQualification'
          },
          slot: 'limitQualification',
          rules: { required: 0, message: '必填' }
        },
        {
          attrs: {
            label: '创建日期',
            width: '180px',
            prop: 'creationDate'
          },
          slot: 'creationDate',
          rules: { required: 0, message: '必填' }
        },
        {
          attrs: {
            label: '创建人',
            width: '100px',
            prop: 'createdBy'
          },
          slot: 'createdBy',
          rules: { required: 0, message: '必填' }
        },
        {
          attrs: {
            label: '最后更新人',
            width: '100px',
            prop: 'lastUpdatedBy'
          },
          slot: 'lastUpdatedBy',
          rules: { required: 0, message: '必填' }
        },
        {
          attrs: {
            prop: 'operation',
            label: '操作',
            width: 140,
            fixed: 'right'
          },
          operations: [
            {
              event: 'deleteItem',
              name: this.$t('common.delete'),
              func: this.deleteItem
            },
            {
              event: 'addItem',
              name: this.$t('common.save'),
              func: this.save
            }
          ]
        }
      ]
    }
  },
  created () {},
  methods: {
    handleSelectionChange (val) {
      this.multipleSelection = val
    },
    syncFilterParams (values) {
      this.filterParams = values
    },
    handleSuccess () {
      this.getQuerydata()
    },
    getUserObjnotice (val, scope) {
      this.$set(this.$refs.table.realDataSource[scope.$index], 'companyId', val.companyId)
      this.$set(this.$refs.table.realDataSource[scope.$index], 'companyName', val.companyName)
      this.$set(this.$refs.table.realDataSource[scope.$index], 'companyCode', val.companyCode)
    },
    downloadTemplate () {
      downloadFileLink(
        '/api-base/base/blacklist/exportExcelTemplate',
        '导入模板.xlsx'
      ).catch(() => {
        this.$message.error('下载失败')
      })
    },
    // 上传附件成功
    handleUploadSuccess (file, row, key) {
      const { id, name } = file
      row[key] = id.toString()
    },
    // 删除文件
    handleAttachmentRemove (row, key) {
      row[key] = ''
    },
    deleteItem (scope, data) {
      if (scope.row.blacklistId) {
        // 有主键ID
        this.$confirm(this.$t('common.deleteViews'), {
          confirmButtonText: this.$t('common.confirm'),
          cancelButtonText: this.$t('common.cancel'),
          type: 'warning'
        })
          .then(() => {
            this.$api.base.blacklist.delete(scope.row.blacklistId).then(res => {
              this.$message.success(res.message)
              this.$refs.table.search(this.queryParams, true)
            })
          })
          .catch(() => {})
      } else {
        // 无主键ID
        data.splice(scope.$index, 1)
      }
    },
    search (params) {
      const { pageSize, pageNum } = this.queryParams
      this.$refs.table.search({ pageSize, pageNum, ...params }, true)
    },
    add () {
      this.$refs.table.add({
        creationDate: dateFormat(new Date()),
        createdBy: this.$store.getters.userInfo ? this.$store.getters.userInfo.username : '',
        lastUpdatedBy: dateFormat(new Date())
      })
    },
    save (scope, data) {
      this.$api.base.blacklist.batchSaveOrUpdate([scope.row]).then(res => {
        this.$message.success(res.message)
        this.$refs.table.search(this.queryParams, true)
      })
    },
    saveListData () {
      this.$refs.table.validate(f => {
        if (f) {
          this.$api.base.blacklist.batchSaveOrUpdate(this.multipleSelection).then(res => {
            this.$message.success(res.message)
            this.$refs.table.search(this.queryParams, true)
          })
        } else {
          this.$message({
            message: '请输入单据必填信息',
            type: 'error'
          })
        }
      })
    },
    saveList () {
      const list = this.$refs.table.getUpdatedRows()
      this.$refs.table.validate(f => {
        if (f) {
          this.$api.base.blacklist
            .batchSaveOrUpdate(
              list.map(({ blacklistId, ...rest }) => {
                if (!blacklistId) {
                  return rest
                } else {
                  return { blacklistId, ...rest }
                }
              })
            )
            .then(res => {
              this.$message.success(res.message)
              this.$refs.table.search(this.queryParams, true)
            })
        } else {
          this.$message({
            message: '请输入单据必填信息',
            type: 'error'
          })
        }
      })
    }
  }
}
</script>
<style scoped lang="scss"></style>
