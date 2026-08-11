<template>
  <el-container class="flex-container toolinginfo_list_wrapper" direction="vertical">
    <el-main>
      <FormWrapper
        ref="formWrapper"
        :form-array="filterConfig"
        :pre-form-obj="formQueryData"
        @getFormData="getQuerydata"
      />
      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <!-- 导入 -->
          <MImport
            :title="$t('common.import')"
            up-load-url="/api-ppap/toolingConfirm/importToolingConfirm"
            :extra-data="extraData"
            code="sup:toolingConfirm:import"
            @downloadTemplate="downloadItemTemplate"
            @handleSuccess="handleSuccess"
          />
          <ExportExcel
            page-url="/api-ppap/toolingConfirm/getToolingConfirmByParam"
            :filter-params="queryParam"
            :table-header="tableHeader"
            :dict-codes="dictCodes"
            export-mode="front"
            type="default"
          />
        </template>
      </MainHeader>
      <TableView
        ref="list"
        :table-header="tableHeader"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :com-active="$attrs['changeTab']"
        url="/api-ppap/toolingConfirm/getToolingConfirmByParam"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MainHeader from 'lib@/components/Table/MainHeader'
import MImport from 'lib@/components/import'
import { parseTime, formatTimeToDate } from '@/utils'
import { downloadFileLink, downloadFileLinkByPost } from 'lib@/utils/file'
import ExportExcel from 'lib@/components/export-excel'
export default {
  name: 'ToolingConfirmQuery',
  components: {
    TableView,
    FormWrapper,
    MainHeader,
    MImport,
    ExportExcel
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      dictCodes: {},
      formQueryData: {},
      pageSize: 15,
      filterConfig: [
        {
          // '零件编号'
          label: this.$t('productionPrepare.toolingCode'),
          prop: 'toolingCode'
        },
        {
          // '零件名称'
          label: this.$t('marketBudget.partName'),
          prop: 'toolingName'
        },
        {
          // '供应商'
          label: this.$t('common.vendor'),
          prop: 'vendorName',
          type: 'quicksearch',
          showKey: 'companyName',
          name: 'scc_sup_company_info'
        },
        {
          // '领域'
          label: this.$t('productionPrepare.area'),
          prop: 'area',
          type: 'dict',
          code: 'TOOLING_AREA_TYPE'
        },
        {
          // '认可结果'
          label: this.$t('productionPrepare.confirmResult'),
          prop: 'confirmResult',
          type: 'dict',
          code: 'TOOLING_CONFIRM_RESULT'
        },
        {
          // '认可人'
          label: this.$t('productionPrepare.confirmFullName'),
          prop: 'confirmFullName'
        }
      ],
      queryParam: {},
      tableHeader: [
        {
          // '零件编号'
          label: this.$t('productionPrepare.toolingCode'),
          prop: 'toolingCode',
          minWidth: 160
        },
        {
          // '零件名称'
          label: this.$t('marketBudget.partName'),
          prop: 'toolingName',
          minWidth: 160
        },
        {
          // '供应商代码'
          label: this.$t('cusEntry.supplement20250211.supplierCode'),
          prop: 'vendorCode',
          minWidth: 120
        },
        {
          // '供应商名称'
          label: this.$t('common.companyName'),
          prop: 'vendorName',
          minWidth: 120
        },
        {
          // '领域'
          label: this.$t('productionPrepare.area'),
          prop: 'area',
          minWidth: 120
        },
        {
          // '认可结果'
          label: this.$t('productionPrepare.confirmResult'),
          prop: 'confirmResult',
          minWidth: 120
        },
        {
          // '备注'
          label: this.$t('components.eio.headers.remark'),
          prop: 'remarks',
          width: 180
        },
        {
          // '认可人'
          label: this.$t('productionPrepare.confirmFullName'),
          prop: 'confirmFullName',
          minWidth: 120
        },
        {
          // '认可时间'
          label: this.$t('productionPrepare.confirmDate'),
          prop: 'confirmDate',
          minWidth: 120,
          formattor: (val) => {
            return this.$parseTime(val, '{y}-{m}-{d}')
          }
        }
      ],
      extraData: {
        fileModular: 'suplier',
        fileFunction: 'accountAccess',
        fileType: 'excel',
        createdName: '',
        createdTime: ''
      }
    }
  },
  created () {
    let date = parseTime(new Date(), '{y}-{m}-{d}', true)
    this.extraData.createdTime = date
    this.extraData.createdName = this.$store.getters.userInfo.username
    console.log('extraData:::', this.extraData)
    this.getQuerydata()
  },
  methods: {
    getQuerydata (params) {
      this.queryParam = params
      this.$nextTick(() => {
        this.$refs.list.query()
      })
    },
    handleSuccess (res) {
      // this.$message.success('导入成功')
      this.$message.success(this.$t('components.eio.importSuccess'))
      this.getQuerydata(this.queryParam)
    },
    downloadItemTemplate () {
      downloadFileLinkByPost(
        '/api-ppap/toolingConfirm/importModelDownload',
        // '零件认可结果导入模板.xlsx'
        this.$t('cusEntry.supplement20250211.partApprovalResultImportTemplate')
      ).catch(() => {
        // this.$message.error('下载失败')
        this.$message.error(this.$t('components.eio.downloadFail'))
      })
    }
  }
}
</script>
