<template>
  <el-container
    direction="vertical"
    class="flex-container partmapping_list_wrapper"
  >
    <FormWrapper
      :formArray="filterConfig"
      @getFormData="search"
    >
      <template #status="{ scope }">
        <DictSelect v-model="scope.status" code="PART_MAPPING_STATUS" />
      </template>
      <template #mappingType="{ scope }">
        <DictSelect v-model="scope.mappingType" code="PART_MAPPING_TYPE" />
      </template>
    </FormWrapper>
    <el-main>
      <EasyTable
        ref="table"
        :selection="true"
        :methods="methods"
        :columns="columns"
        rowKey="partMappingId"
        tableName="partmapping_table"
        :queryParams.sync="queryParams"
        @row-dblclick="rowDbclick"
        @selection-change="selectionChangeHandler"
      >
        <template #btns>
          <MImport
            style="display: inline-block;margin: 0 10px;"
            title="导入"
            upLoadUrl="/api-sou/inquiry/partMapping/importExcel"
            :extraData="extraData"
            code="inq:partMapping:import"
            @downloadTemplate="downloadTemplate"
            @handleSuccess="handleSuccess"
          />
          <AuthorityButton type="primary" code="base:partmapping:add" @click="add">
            {{
              $t("common.add")
            }}
          </AuthorityButton>
          <AuthorityButton
            :disabled="!selectList.length"
            type="primary"
            code="base:partmapping:save"
            @click="savePartMapping(selectList)"
          >
            {{
              $t("common.save")
            }}
          </AuthorityButton>
          <AuthorityButton
            :disabled="!selectList.length"
            code="base:partmapping:delete"
            type="primary"
            @click="deleteSelectList(selectList)"
          >
            删除
          </AuthorityButton>
          <AuthorityButton
            :disabled="!selectList.length"
            code="base:partmapping:valid"
            type="primary"
            @click="validSelectList(selectList)"
          >
            生效
          </AuthorityButton>
          <AuthorityButton
            :disabled="!selectList.length"
            code="base:partmapping:inValid"
            type="primary"
            @click="inValidSelectList(selectList)"
          >
            失效
          </AuthorityButton>
          <ExportExcel
            pageUrl="/api-sou/inquiry/partMapping/listPage"
            :filterParams="filterParams"
            :tableHeader="tableHeader"
            :dictCodes="dictCodes"
            exportMode="front"
          />
        </template>
        <!-- 零件编号 -->
        <template #partCode="{ scope }">
          <QuickSearch
            :showInput="scope.row.partCode"
            show-key="materialCode"
            :scope-data="scope.row"
            name="gacm_inquiry_parts_claim1"
            @close-quicksearch="(val) => getPartCodeObj(val, scope)"
          />
        </template>
        <!-- 管理支给件/材料编码 -->
        <template #managePartCode="{ scope }">
          <QuickSearch
            :showInput="scope.row.managePartCode"
            show-key="materialCode"
            :scope-data="scope.row"
            name="gacm_inquiry_parts_claim1"
            @close-quicksearch="(val) => getManagePartCodeObj(val, scope)"
          />
        </template>
        <!-- 映射类型 -->
        <template #mappingType="{ scope }">
          <DictSelect v-model="scope.row.mappingType" code="PART_MAPPING_TYPE" />
        </template>
        <!-- 财务统计分类 -->
        <template #financialStatType="{ scope }">
          <DictSelect v-model="scope.row.financialStatType" code="BASE_MARKET_TYPE" />
        </template>
      </EasyTable>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoMixin, tabTodoWatch } from '@/utils/mixins'
import EasyTable from 'lib@/components/BaseTable/EasyTable'
// import createDictionary from "lib@/utils/ponyStore";
import CUploadFile from '@/library/components/c-upload-file'
import CDownloadLink from 'lib@/components/c-download-link'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MImport from 'lib@/components/import'
import { downloadFileLink } from 'lib@/utils/file'
import QuickSearch from 'lib@/components/QuickSearch' // 快速查询组件
import { EDITABLE_KEY } from 'lib@/components/BaseTable/utils'
import ExportExcel from 'lib@/components/export-excel'

// const { store, mutation, getLabel, renderSelect } = createDictionary({
//   PART_MAPPING_STATUS: [],
//   PART_MAPPING_TYPE: [],
//   FINANCIAL_STAT_CLASSIFICATION: [],
//   BASE_MARKET_TYPE:[]
// });

// const RenderSelect = renderSelect();

export default {
  name: 'PartMapping',
  components: {
    EasyTable,
    // RenderSelect,
    CUploadFile,
    CDownloadLink,
    FormWrapper,
    MImport,
    QuickSearch,
    ExportExcel
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      dictCodes: {
        mappingType: 'PART_MAPPING_TYPE',
        financialStatType: 'BASE_MARKET_TYPE',
        status: 'PART_MAPPING_STATUS'
      },
      // 文件上传配置信息
      fileInfo: {
        uploadType: 'FASTDFS', // 固定参数
        sourceType: 'WEB_APP', // 固定参数
        fileModular: 'workFlow', // 文件所属模块 -》审批流程
        fileFunction: 'workflowReport', // 审批流相关文件
        fileType: 'images' // 文件所属类型
      },
      filterConfig: [
        {
          prop: 'partCode',
          label: '零件编号'
        },
        {
          prop: 'partName',
          label: '零件名称'
        },
        {
          prop: 'managePartCode',
          label: '管理支给件/材料编码',
          type: 'quicksearch',
          showKey: 'materialCode',
          name: 'scc_base_material_item'
        },
        { prop: 'mappingType', label: '映射类型', type: 'slot', slot: 'mappingType' },
        { prop: 'status', label: '状态', type: 'slot', slot: 'status' }
      ],
      extraData: {
        sourceType: 'WEB_APP',
        uploadType: 'FASTDFS',
        fileModular: 'base',
        fileFunction: 'partmapping',
        fileType: 'excel'
      },
      queryParams: {},
      methods: {
        listPage: async params => {
          const res = await this.$http({
            url: '/api-sou/inquiry/partMapping/listPage',
            method: 'POST',
            data: params,
            loading: true
          }).then((rows) => {
            let list = rows.data.list
            for (const row of list) {
              if (row.status === 'DRAFT') {
                row[EDITABLE_KEY] = true
              }
            }
            return rows
          }).catch(err => {
            console.log(err)
          })
          return res
        }
      },
      columns: [
        {
          attrs: {
            label: '零件编号',
            prop: 'partCode',
            width: 150,
            sortable: true
          },
          slot: 'partCode',
          rules: { required: true, message: '必填' }
        },
        {
          attrs: {
            label: '零件名称',
            prop: 'partName',
            width: 150,
            sortable: true
          }
        },
        {
          attrs: {
            label: '零件供应商名称',
            prop: 'partVendorName',
            width: 150,
            sortable: true
          }
        },
        {
          attrs: {
            label: '管理支给件/材料编码',
            prop: 'managePartCode',
            width: 150,
            sortable: true
          },
          slot: 'managePartCode',
          rules: { required: true, message: '必填' }
        },
        {
          attrs: {
            label: '管理支给件/材料描述',
            prop: 'managePartName',
            width: 150,
            sortable: true
          }
        },
        {
          attrs: {
            label: '管理支给件/材料供应商名称',
            prop: 'managePartVendorName',
            width: 150,
            sortable: true
          }
        },
        {
          attrs: {
            label: '映射类型',
            prop: 'mappingType',
            formatter: (value) => this.$getDictLabel('PART_MAPPING_TYPE', value),
            sortable: true
          },
          slot: 'mappingType',
          rules: { required: true, message: '必填' }
        },
        {
          attrs: {
            label: '市况类型',
            prop: 'financialStatType',
            formatter: (value) => this.$getDictLabel('BASE_MARKET_TYPE', value),
            sortable: true
          },
          slot: 'financialStatType',
          rules: { required: true, message: '必填' }
        },
        {
          attrs: {
            label: '状态',
            prop: 'status',
            formatter: (value) => this.$getDictLabel('PART_MAPPING_STATUS', value),
            sortable: true
          }
        },
        {
          attrs: {
            label: '生效时间',
            prop: 'startDate',
            sortable: true
          }
        },
        {
          attrs: {
            label: '失效时间',
            prop: 'endDate',
            sortable: true
          }
        }
        /* {
          attrs: {
            prop: 'operation',
            label: '操作',
            width: 100,
            fixed: 'right',
          },
          operations: [
            {
              event: 'deleteItem',
              name: this.$t("common.delete"),
              func: this.deleteItem,
              show: ({row}) => row.status === "INVALID",
            }
          ]
        } */
      ],
      // 选择的行
      selectList: []
    }
  },
  computed: {
    tableHeader () {
      return this.columns.map(item => {
        let { prop, label } = item.attrs
        return {
          prop,
          label
        }
      })
    },
    filterParams () {
      let params = {}; let queryParams = this.queryParams || {}
      Object.keys(queryParams).forEach(key => {
        if (!['pageNum', 'pageSize'].includes(key)) {
          params[key] = queryParams[key]
        }
      })
      return params
    }
  },
  created () {
    // mutation.loadDictionary([
    //   "PART_MAPPING_STATUS",
    //   "PART_MAPPING_TYPE",
    //   "FINANCIAL_STAT_CLASSIFICATION",
    //   "BASE_MARKET_TYPE"
    // ]);
  },
  methods: {
    syncFilterParams (values) {
      this.filterParams = values
    },
    handleSuccess () {
      this.$refs.table.search(this.queryParams, true)
    },
    downloadTemplate () {
      downloadFileLink(
        '/api-sou/inquiry/partMapping/exportExcelTemplate',
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
    // 行删除
    deleteItem (scope, data) {
      let id = scope.row.partMappingId
      if (id) {
        // 有主键ID
        this.$confirm(this.$t('common.deleteViews'), {
          confirmButtonText: this.$t('common.confirm'),
          cancelButtonText: this.$t('common.cancel'),
          type: 'warning'
        }).then(() => {
          this.$http({
            url: '/api-sou/inquiry/partMapping/delete',
            method: 'GET',
            params: { id },
            loading: true
          }).then(res => {
            this.$message.success(res.message)
            this.$refs.table.search(this.queryParams, true)
          })
        }).catch(() => {

        })
      } else {
        // 无主键ID
        data.splice(scope.$index, 1)
      }
    },
    search (params) {
      let { pageSize, pageNum } = this.queryParams
      this.$refs.table.search({ pageSize, pageNum, ...params }, false)
    },
    rowDbclick (row, column, event) {
      if (row.status !== 'DRAFT') {
        row[EDITABLE_KEY] = false
      }
    },
    /* 添加一行数据 */
    add () {
      this.$refs.table.add({
        status: 'DRAFT'
      })
    },
    /* 保存数据 */
    savePartMapping (list) {
      // const list = this.$refs.table.getUpdatedRows();
      for (const row of list) {
        if (row.status !== 'DRAFT') {
          this.$message.warning('只能编辑拟定单据')
          return false
        }
      }
      this.$refs.table.validate((valid) => {
        if (valid) {
          let data = list.map(({ partMappingId, ...rest }) => {
            if (!partMappingId) {
              return rest
            } else {
              return { partMappingId, ...rest }
            }
          })
          this.$http({
            url: '/api-sou/inquiry/partMapping/batchSaveOrUpdate',
            method: 'POST',
            data: data,
            loading: true
          }).then(res => {
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
    selectionChangeHandler (data) {
      this.selectList = data
      console.log('selectionChange', data)
    },
    /* 删除 */
    deleteSelectList (rows) {
      for (const row of rows) {
        if (row.status !== 'DRAFT') {
          this.$message.warning('只能删除拟定单据')
          return false
        }
      }
      this.$confirm('确认删除吗？', {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          let ids
          if (Array.isArray(rows)) {
            ids = rows.filter(i => i.partMappingId).map(i => i.partMappingId)
            this.$http({
              url: '/api-sou/inquiry/partMapping/bathDelete',
              method: 'POST',
              data: ids,
              loading: true
            }).then(res => {
              this.$message.success(res.message)
              this.$refs.table.search(this.queryParams, true)
            })
          }
        })
        .catch(() => {})
    },
    /* 生效 */
    validSelectList (rows) {
      for (const row of rows) {
        if (row.status !== 'DRAFT') {
          this.$message.warning('只能生效拟定单据')
          return false
        }
      }
      this.$refs.table.validate((valid) => {
        if (valid) {
          let data = rows.map(({ partMappingId, ...rest }) => {
            if (!partMappingId) {
              return rest
            } else {
              return { partMappingId, ...rest }
            }
          })
          this.$confirm('确认生效吗？', {
            confirmButtonText: this.$t('common.confirm'),
            cancelButtonText: this.$t('common.cancel'),
            type: 'warning'
          })
            .then(() => {
              let delParams
              if (Array.isArray(rows)) {
                delParams = rows.filter(i => i.partMappingId)
                this.$http({
                  url: '/api-sou/inquiry/partMapping/batchValid',
                  method: 'POST',
                  data: delParams,
                  loading: true
                }).then(res => {
                  this.$message.success(res.message)
                  this.$refs.table.search(this.queryParams, true)
                })
              }
            })
            .catch(() => {})
        } else {
          this.$message({
            message: '请输入单据必填信息',
            type: 'error'
          })
        }
      })
    },
    inValidSelectList (rows) {
      for (const row of rows) {
        if (row.status !== 'VALID') {
          this.$message.warning('只能失效拟定单据')
          return false
        }
      }
      this.$confirm('确认失效吗？', {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          let delParams
          if (Array.isArray(rows)) {
            delParams = rows.filter(i => i.partMappingId)
            this.$http({
              url: '/api-sou/inquiry/partMapping/batchInValid',
              method: 'POST',
              data: delParams,
              loading: true
            }).then(res => {
              this.$message.success(res.message)
              this.$refs.table.search(this.queryParams, true)
            })
          }
        })
        .catch(() => {})
    },
    // 行编辑--零件快查
    getPartCodeObj (val, scope) {
      this.$set(this.$refs.table.realDataSource[scope.$index], 'partId', val.materialId)
      this.$set(this.$refs.table.realDataSource[scope.$index], 'partName', val.materialName)
      this.$set(this.$refs.table.realDataSource[scope.$index], 'partCode', val.materialCode)
      this.$set(this.$refs.table.realDataSource[scope.$index], 'partVendorId', val.vendorId)
      this.$set(this.$refs.table.realDataSource[scope.$index], 'partVendorName', val.vendorName)
      this.$set(this.$refs.table.realDataSource[scope.$index], 'partVendorCode', val.vendorCode)
      // 清空后也要清空供应商信息
      if (!val) {
        this.$set(this.$refs.table.realDataSource[scope.$index], 'partVendorId', null)
        this.$set(this.$refs.table.realDataSource[scope.$index], 'partVendorName', null)
        this.$set(this.$refs.table.realDataSource[scope.$index], 'partVendorCode', null)
      }
    },
    // 行编辑--管理支给件/材料快查
    getManagePartCodeObj (val, scope) {
      this.$set(this.$refs.table.realDataSource[scope.$index], 'managePartId', val.materialId)
      this.$set(this.$refs.table.realDataSource[scope.$index], 'managePartName', val.materialName)
      this.$set(this.$refs.table.realDataSource[scope.$index], 'managePartCode', val.materialCode)
      this.$set(this.$refs.table.realDataSource[scope.$index], 'managePartVendorId', val.vendorId)
      this.$set(this.$refs.table.realDataSource[scope.$index], 'managePartVendorName', val.vendorName)
      this.$set(this.$refs.table.realDataSource[scope.$index], 'managePartVendorCode', val.vendorCode)
      // 清空后也要清空供应商信息
      if (!val) {
        this.$set(this.$refs.table.realDataSource[scope.$index], 'managePartVendorId', null)
        this.$set(this.$refs.table.realDataSource[scope.$index], 'managePartVendorName', null)
        this.$set(this.$refs.table.realDataSource[scope.$index], 'managePartVendorCode', null)
      }
    }
  }
}
</script>
<style scoped lang="scss">
</style>
