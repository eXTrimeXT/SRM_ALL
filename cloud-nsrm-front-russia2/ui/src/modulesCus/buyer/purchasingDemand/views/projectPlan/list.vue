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
      />
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
          <MImport
            ref="import"
            :title="iModal.title"
            :up-load-url="iModal.upLoadUrl"
            :extra-data="extraData"
            type="default"
            @downloadTemplate="downloadTemplate"
            @handleSuccess="handleSuccess"
          />
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :row-index-fixed="false"
        :page-size="pageSize"
        :adept-mei-ql="true"
        :check-change="handleCurrentChange"
        :pre-query-data="queryParam"
        :com-active="$attrs['changeTab']"
        :source="purchaseApplicationApi.listProjectPlans"
        :open-custom-table="false"
      >
        <template #departmentName="props">
          <QuickSearch
            :show-input="props.scope.row.departmentName"
            show-key="descr"
            auto-query
            :disabled="!props.scope.row.editable"
            :scope-data="props.scope"
            name="ceea_base_dept"
            @close-quicksearch="getMaterial"
          />
        </template>
        <template #initDate="props">
          <el-date-picker
            v-if="props.scope.row.editable"
            v-model="props.scope.row.initDate"
            :format="$formatDatePicker"
            value-format="yyyy-MM-dd"
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
            v-model="props.scope.row.planStatus"
            code="PR_SOU_PROJECT_PLAN_TYPE"
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
import { purchaseApplicationApi } from 'modc@/buyer/purchasingDemand/api'
import { transformMQL } from '@/library/utils/util'


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
      purchaseApplicationApi: purchaseApplicationApi,
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
        upLoadUrl: '/api-sup-ce/npm/pr/requirement/projectPlan/importExcel'
      },
      currentRows: [],
      tableHeader: [],
      tableData: [],
      filterConfig: [
        {
          prop: 'projectName',
          label: this.$t('bidMod.bidingName')
        },
        { prop: 'createdBy',
          label: this.$t('bidMod.creator'),
          type: 'quicksearch',
          showKey: 'nickname',
          propKey: 'username',
          name: 'scc_rbac_user_display'
        },
        {
          prop: 'planStatus',
          label: this.$t('bidMod.projStatus'),
          type: 'dict',
          code: 'PR_SOU_PROJECT_PLAN_TYPE'
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
        prop: 'projectName',
        label: this.$t('bidMod.bidingName'),
        width: 120,
        showType: 'input',
        show: row => typeof row.editable === 'undefined' ? false : row.editable,
        editable: row => !!row.editable
      },
      {
        prop: 'initDate',
        label: this.$t('bidMod.projectIdentificationTime'),
        width: 180,
        showType: 'slot',
        slot: 'initDate',
        show: row => row.editable,
        editable: row => row.editable
      },
      // 投资部门
      {
        prop: 'departmentName',
        label: this.$t('cusEntry.supplement20250121.departmentName'),
        width: 150,
        showType: 'slot',
        slot: 'departmentName',
        editable: row => row.editable,
        show: row => row.editable
      },
      {
        prop: 'initAmount',
        label: this.$t('orderMod.buyerOrderSynergy.amount'),
        width: 120,
        showType: 'input',
        show: row => row.editable,
        editable: row => !!row.editable
      },
      {
        prop: 'planNo',
        label: this.$t('vendorMod.planCodeNew'),
        width: 120,
        showType: 'input',
        show: row => row.editable,
        editable: row => !!row.editable
      },
      // 投资地点
      {
        prop: 'planAddress',
        label: this.$t('cusEntry.supplement20250121.planAddress'),
        width: 120,
        showType: 'input',
        show: row => row.editable,
        editable: row => !!row.editable
      },
      // 项目级别
      {
        prop: 'planLevel',
        label: this.$t('cusEntry.supplement20250121.planLevel'),
        width: 120,
        showType: 'input',
        show: row => row.editable,
        editable: row => !!row.editable
      },
      {
        prop: 'planStatus',
        label: this.$t('bidMod.projStatus'),
        width: 120,
        showType: 'slot',
        slot: 'drawingsStatus',
        editable: row => row.editable,
        show: row => row.editable,
        dataType: 'dict',
        code: 'PR_SOU_PROJECT_PLAN_TYPE'
      },
      {
        prop: 'createdBy',
        label: this.$t('bidMod.creator'),
        width: 120
      },
      {
        prop: 'creationDate',
        label: this.$t('common.creationTime'),
        width: 140,
        dataType: 'dateTime'
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
            callback: (row, socpe) => this.editHandle(row, socpe),
            show: row => !row.editable && ['NORMAL', 'CANCEL'].includes(row.planStatus),
            formattor: () => {
              return this.$t('common.edit')
            }
          },
          {
            callback: (row, socpe) => this.saveHandle(row, socpe),
            show: row => row.editable,
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
          },
          {
            callback: (row, socpe) => this.deleteHandle(row, socpe),
            show: row => !row.editable && ['NORMAL', 'CANCEL'].includes(row.planStatus),
            formattor: () => {
              return this.$t('common.delete')
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
      this.queryParam = transformMQL.listPageData({
        type:'PrSouProjectPlanForBuyer',
        action: 'listProjectPlans',
        params
      })
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    getMaterial (value, scope) {
      scope.row.departmentName = value.descr
      scope.row.departmentId = value.deptid
    },
    deleteHandle (row) {
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          const deleteData = transformMQL.save('PrSouProjectPlanForBuyer', [{ projectPlanId: row.projectPlanId }], 'removePlan')
          purchaseApplicationApi.projectPlanDelete(deleteData).then(res => {
            this.$message.success(this.$t('common.successDelete'))
            this.getQuerydata()
          })
        })
    },
    addHandle () {
      this.$refs[this.gridId].addOneEditableColumn()
    },
    saveHandle (row) {
      const saveData = transformMQL.save('PrSouProjectPlanForBuyer', [{ ...row }], 'editPlan')
      purchaseApplicationApi.projectPlanSave(saveData).then(res => {
        this.$message.success(this.$t('common.successSave'))
        this.getQuerydata()
      })
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
        '/api-sup-ce/npm/pr/requirement/projectPlan/downloadExcel',
        this.$t('drawingshead.drawingImportTemplate')
      ).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail'))
      })
    }
  }
}
</script>
