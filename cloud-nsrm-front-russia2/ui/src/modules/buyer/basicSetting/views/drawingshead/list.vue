<template>
  <el-container
    class="flex-container drawingshead_list_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="filterConfig"
        @getFormData="getQuerydata"
        @synchronous-value="syncFilterParams"
      >
        <template #drawingsVersion="{scope}">
          <el-input v-model="scope.drawingsVersion" v-input-format="{type:'number',negative:false,zero:false}" />
        </template>
      </FormWrapper>
      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <AuthorityButton
            code="generate:drawingshead:add"
            type="primary"
            @click="addHandle"
          >
            {{ $t('common.add') }}
          </AuthorityButton>
<!--          <MImport-->
<!--            ref="import"-->
<!--            :title="iModal.title"-->
<!--            :up-load-url="iModal.upLoadUrl"-->
<!--            :extra-data="extraData"-->
<!--            type="default"-->
<!--            code="generate:drawingshead:import"-->
<!--            @downloadTemplate="downloadTemplate"-->
<!--            @handleSuccess="handleSuccess"-->
<!--          />-->
          <ExportExcel
            export-mode="front"
            :table-header="computedTableHeader"
            :filter-params="queryParam"
            :dict-codes="dictCodes"
            type="default"
            page-url="/api-base/base/drawingshead/listPage"
          />
          <AuthorityButton
            code="generate:drawingshead:active"
            @click="activeHandel"
          >
            {{ $t('common.active') }}
          </AuthorityButton>
          <AuthorityButton
            code="generate:drawingshead:inactive"
            @click="inactiveHandel"
          >
            {{ $t('common.inactive') }}
          </AuthorityButton>
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :row-index-fixed="false"
        :page-size="pageSize"
        :checkbox="true"
        :check-change="handleCurrentChange"
        :pre-query-data="queryParam"
        :com-active="$attrs['changeTab']"
        :source="drawingsHeadApi.list"
        :open-custom-table="true"
      >
        <template #materialCode="props">
          <QuickSearch
            v-if="props.scope.row.editable"
            show-key="materialCode"
            :show-input="props.scope.row.materialCode"
            name="scc_base_material_item"
            :scope-data="props.scope"
            @close-quicksearch="getMaterial"
          />
        </template>
        <template #downloadFileName="{ scope }">
          <SrmCommonFile
            :extra-data="fileInfo"
            :default-file="{
              fileId: scope.row.fileuploadId,
              fileName: scope.row.attachName + '.' + scope.row.attachType
            }"
            :readonly="!scope.row.editable"
            @on-change="({file}) => innerHandleUploadSuccess(file,scope.row)"
          />
        </template>
        <template #drawingsType="props">
          <DictSelect
            v-model="props.scope.row.drawingsType"
            code="DRAWING_TYPE"
          />
        </template>
        <template #drawingsStatus="props">
          <DictSelect
            v-model="props.scope.row.drawingsStatus"
            code="DRAWINGS_STATUS"
          />
        </template>
      </TableView>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import QuickSearch from 'lib@/components/QuickSearch'
import MImport from 'lib@/components/import'
import { downloadFileLink } from 'lib@/utils/file'
import ExportExcel from 'lib@/components/export-excel'
import { drawingsHeadApi } from 'modb@/basicSetting/api/basicSetting'

export default {
  name: 'DrawingsheadList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    QuickSearch,
    MImport,
    ExportExcel
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      drawingsHeadApi: drawingsHeadApi,
      dictCodes: {
        drawingsType: 'DRAWING_TYPE',
        isLatest: 'DRAWING_IS_LATEST',
        drawingsStatus: 'DRAWINGS_STATUS'
      },
      extraData: {
        fileModular: 'cm',
        fileFunction: 'gradingRules',
        fileType: 'excel'
      },
      name: 'drawingsheadList',
      tableName: 'drawingsheadTable',
      pageSize: 15,
      gridId: 'list',
      // 文件上传配置信息
      fileInfo: {
        fileModular: 'workFlow', // 文件所属模块 -》审批流程
        fileFunction: 'workflowReport', // 审批流相关文件
        fileType: 'images', // 文件所属类型
        fileMaxSize: 10 * 1024 * 1024
      },
      iModal: {
        title: this.$t('common.excelImport'), // Excel导入
        upLoadUrl: '/api-base/base/drawingshead/leftImportExcel'
      },
      currentRows: [],
      tableHeader: [],
      tableData: [],
      filterConfig: [
        {
          prop: 'materialCode',
          label: () => this.$t('common.materialCode'), // '物料名称'
          type: 'quicksearch',
          showKey: 'materialCode',
          propKey: 'materialCode',
          name: 'scc_base_material_item'
        },
        { prop: 'drawingsVersion', type: 'slot', label: this.$t('drawingshead.drawingVersion'), slot: 'drawingsVersion' },
        {
          prop: 'drawingsStatus',
          label: this.$t('drawingshead.drawingStatus'),
          type: 'dict',
          code: 'DRAWINGS_STATUS'
        }
      ],
      expLoading: false,
      queryParam: {},
      queryObj: {}
    }
  },
  computed: {
    computedTableHeader () {
      return this.tableHeader.filter(item => !['attachName', 'operation'].includes(item.prop))
    }
  },
  updated () {
    this.defaultTableHeader = this.tableHeader
  },
  created () {
    this.tableHeader = [
      {
        prop: 'materialCode',
        label: this.$t('common.materialCode'),
        width: 120,
        showType: 'slot',
        slot: 'materialCode',
        show: row => typeof row.editable === 'undefined' ? false : row.editable,
        editable: row => !!row.editable
      },
      {
        prop: 'materialName',
        label: this.$t('common.materialName'),
        width: 120,
        editable: row => row.editable
      },
      {
        prop: 'drawingsType',
        label: this.$t('drawingshead.drawingType'),
        width: 120,
        showType: 'slot',
        slot: 'drawingsType',
        editable: row => row.editable,
        show: row => row.editable,
        dataType: 'dict',
        code: 'DRAWING_TYPE'
      },
      {
        prop: 'drawingsVersion',
        label: this.$t('drawingshead.drawingVersion'),
        width: 120,
        editable: row => row.editable
      },
      {
        prop: 'isLatest',
        label: this.$t('drawingshead.isItCurrent'),
        width: 130,
        editable: row => row.editable,
        dataType: 'dict',
        code: 'DRAWING_IS_LATEST'
      },
      {
        prop: 'fileuploadAddress',
        label: this.$t('drawingshead.drawingAddress'),
        width: 100,
        showType: 'input',
        editable: row => row.editable
      },
      {
        prop: 'attachName',
        label: this.$t('drawingshead.drawingAttachment'),
        width: 150,
        showType: 'slot',
        slot: 'downloadFileName'
      },
      {
        prop: 'drawingsStatus',
        label: this.$t('drawingshead.drawingStatus'),
        width: 140,
        showType: 'slot',
        slot: 'drawingsStatus',
        editable: row => row.editable,
        show: row => row.editable,
        dataType: 'dict',
        code: 'DRAWINGS_STATUS'
      },
      {
        prop: 'creationDate',
        label: this.$t('common.creationTime'),
        width: 140,
        dataType: 'dateTime',
        editable: row => row.editable
      },
      {
        prop: 'lastUpdateDate',
        label: this.$t('common.updateTime'),
        width: 140,
        dataType: 'dateTime',
        editable: row => row.editable
      },
      {
        prop: 'operation',
        label: this.$t('components.headers.operation'),
        showType: 'buttons',
        btnStyle: 'text',
        fixed: 'right',
        width: 130,
        buttons: [
          {
            callback: (row, socpe) => this.saveHandle(row, socpe),
            show: row => row.editable,
            code: 'generate:drawingshead:add',
            formattor: () => {
              return this.$t('common.save')
            }
          },
          {
            callback: (row, socpe) => this.cancelEdit(row, socpe),
            show: row => row.editable,
            formattor: () => {
              return this.$t('common.cancel')
            }
          }
        ]
      }
    ]
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    syncFilterParams (values) {
      this.queryObj = values
    },
    getQuerydata (params) {
      this.queryParam = params
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    getMaterial (value, scope) {
      scope.row.materialId = value.materialId
      scope.row.materialCode = value.materialCode
      scope.row.materialName = value.materialName
      this.$refs[this.gridId].setTableData(tableData => {
        this.$set(tableData, scope.$index, scope.row)
      })
    },
    deleteHandle (row) {
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          drawingsHeadApi.delete(row.drawingsId).then(res => {
            this.$message.success(res.message)
            this.getQuerydata()
          })
        })
    },
    addHandle () {
      this.$refs[this.gridId].addOneEditableColumn()
    },
    saveHandle (row) {
      if (row.isNew) {
        drawingsHeadApi.add(row).then(res => {
          this.$message.success(res.message)
          this.getQuerydata()
        })
      } else {
        drawingsHeadApi.update(row).then(res => {
          this.$message.success(res.message)
          this.getQuerydata()
        })
      }
    },
    editHandle (row, scope) {
      this.$set(scope.row, 'editable', true)
    },
    cancelEdit (row, scope) {
      scope.row.editable = false
      this.getQuerydata()
    },
    activeHandel () {
      if (this.currentRows.length === 0) {
        this.$message.warning(this.$t('vendorMod.pleaseCheckAtLeastOnePieceOfData'))
        return
      }
      let rows = { drawingsIds: [], status: 'Y' }
      this.currentRows.forEach(item => {
        rows.drawingsIds.push(item.drawingsId)
      })
      drawingsHeadApi.updateStatus(rows).then(res => {
        this.$message.success(res.message)
        this.getQuerydata()
      })
    },
    inactiveHandel () {
      if (this.currentRows.length === 0) {
        this.$message.warning(this.$t('vendorMod.pleaseCheckAtLeastOnePieceOfData'))
        return
      }
      let rows = { drawingsIds: [], status: 'N' }
      this.currentRows.forEach(item => {
        rows.drawingsIds.push(item.drawingsId)
      })
      drawingsHeadApi.updateStatus(rows).then(res => {
        this.$message.success(res.message)
        this.getQuerydata()
      })
    },
    handleCurrentChange (val) {
      this.currentRows = val
    },
    // 上传附件成功
    innerHandleUploadSuccess (file, row) {
      const { fileId = '', fileName = '', fileExtendType = '' } = file || {}
      this.$set(row, 'fileuploadId', fileId)
      this.$set(row, 'attachName', fileName)
      this.$set(row, 'attachType', fileExtendType)
    },
    handleSuccess () {
      this.getQuerydata()
    },
    downloadTemplate () {
      downloadFileLink(
        '/api-base/base/drawingshead/leftImportExcelTemplate',
        this.$t('drawingshead.drawingImportTemplate')
      ).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail'))
      })
    }
  }
}
</script>
