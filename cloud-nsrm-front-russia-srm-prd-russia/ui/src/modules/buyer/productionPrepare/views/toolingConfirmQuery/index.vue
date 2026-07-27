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
          <MImport
            title="导入"
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
          label: '零件编号',
          prop: 'toolingCode'
        },
        {
          label: '零件名称',
          prop: 'toolingName'
        },
        {
          label: '供应商',
          prop: 'vendorName',
          type: 'quicksearch',
          showKey: 'companyName',
          name: 'scc_sup_company_info'
        },
        {
          label: '领域',
          prop: 'area',
          type: 'dict',
          code: 'TOOLING_AREA_TYPE'
        },
        {
          label: '认可结果',
          prop: 'confirmResult',
          type: 'dict',
          code: 'TOOLING_CONFIRM_RESULT'
        },
        {
          label: '认可人',
          prop: 'confirmFullName'
        }
      ],
      queryParam: {},
      tableHeader: [
        {
          label: '零件编号',
          prop: 'toolingCode',
          minWidth: 160
        },
        {
          label: '零件名称',
          prop: 'toolingName',
          minWidth: 160
        },
        {
          label: '供应商代码',
          prop: 'vendorCode',
          minWidth: 120
        },
        {
          label: '供应商名称',
          prop: 'vendorName',
          minWidth: 120
        },
        {
          label: '领域',
          prop: 'area',
          minWidth: 120
        },
        {
          label: '认可结果',
          prop: 'confirmResult',
          minWidth: 120
        },
        {
          label: '备注',
          prop: 'remarks',
          width: 180
        },
        {
          label: '认可人',
          prop: 'confirmFullName',
          minWidth: 120
        },
        {
          label: '认可时间',
          prop: 'confirmDate',
          minWidth: 120,
          formattor: (val) => {
            return formatTimeToDate(val, 'Y-M-D')
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
    let date = parseTime(new Date())
    this.extraData.createdTime = formatTimeToDate(date, 'Y-M-D')
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
      this.$message.success('导入成功')
      this.getQuerydata(this.queryParam)
    },
    downloadItemTemplate () {
      downloadFileLinkByPost(
        '/api-ppap/toolingConfirm/importModelDownload',
        '零件认可结果导入模板.xlsx'
      ).catch(() => {
        this.$message.error('下载失败')
      })
    }
  }
}
</script>
