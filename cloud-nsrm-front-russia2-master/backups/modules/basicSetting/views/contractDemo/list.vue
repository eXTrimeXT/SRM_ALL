<template>
  <el-container
    class="flex-container the_contractDemoList_wrapper"
    direction="vertical"
  >
    <el-main>
      <form-wrapper
        :form-array="preArr"
        @getFormData="getQuerydata"
      />

      <main-header
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <el-button
            type="primary"
            @click="addNewMode"
          >
            新增
          </el-button>
          <!-- <el-button type="primary" @click="showPdf">在线预览</el-button> -->
        </template>
      </main-header>
      <table-view
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :check-change="handleCurrentChange"
        :page-size="pageSize"
        :checkbox="false"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        :source="$api.cm.modelLine.queryContract"
      />
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import PdfPreview from 'lib@/components/PdfPreview'
import { parseTime, adaptDictData } from '@/utils'
import { getDictItemList } from '@/api/common'
import Edit from './edit'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'

export default {
  name: 'ContractDemoList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    PdfPreview
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      pdfVisible: false,
      tableName: 'contractDemoList',
      reviewFormNumber: '',
      gridData: [],
      pageSize: 15,
      gridId: 'list',
      selectList: [],
      currentRows: [],
      showFilterBar: 1,
      tableHeader: [],
      tableData: [],
      statusList: [],
      form: {
        id: '',
        vendorCode: '',
        vendorCompanyName: '',
        reviewFormNumber: '',
        enabled: ''
      },
      rules: {
        vendorCode: [{ required: true, message: '请输入字典编码' }],
        vendorCompanyName: [{ required: true, message: '请输入字典名称' }]
      },
      isModify: false,
      dialogFormVisible: false,
      formLabelWidth: '100px',
      preArr: [
        { prop: 'orderCode', label: '合同编码' },
        { prop: 'orderName', label: '合同名称' }
        // {
        //   prop: "materialCode",
        //   label: "物料编码",
        //   type: "quicksearch",
        //   showKey: "materialCode",
        //   name: "scc_base_material_item"
        // },
        // {
        //   prop: "organizationId",
        //   label: "采购组织",
        //   type:'OUorganizationSelector',
        // },
        // { prop: "orderNumber", label: "采购订单编号" },
        // { prop: "deliveryNumber", label: "送货单号" },
        // {
        //   prop: "vendorName",
        //   label: "供应商名称",
        //   type: "quicksearch",
        //   showKey: "companyName",
        //   name: "scc_sup_company_info_display"
        // },
        // { prop: "startReturnDate", label: "起始退货日期", type: "date" },
        // { prop: "endReturnDate", label: "截止退货日期", type: "date" }
      ],
      queryParam: {},
      deliveryTypes: [],
      retrunTypes: []
    }
  },
  created () {
    this.tableHeader = [
      {
        prop: 'orderName',
        label: '合同名称',
        // showType: "button",
        // btnStyle: "text",
        // callback: row => this.suppleDelivery("edit", row),
        width: 130
      },
      {
        prop: 'orderCode',
        label: '合同编码'
      },
      {
        prop: 'modelHeadId',
        label: '模板编码'
      },
      {
        prop: 'operation',
        label: '操作',
        showType: 'buttons',
        btnStyle: 'text',
        // fixed: "right",
        width: 100,
        buttons: [
          // {
          //   callback: row => this.preview(row),
          //   // code: "pr:requirementApply:edit",
          //   // show: row => row.auditStatus === "DRAFT",
          //   formattor: () => {
          //     // return this.$t("common.edit");
          //     return "预览";
          //   }
          // },
          {
            callback: row => this.edit(row, 'edit'),
            // code: "pr:requirementApply:edit",
            // show: row => row.auditStatus === "DRAFT",
            formattor: () => {
              // return this.$t("common.edit");
              return '编辑'
            }
          }
        ]
      }
    ]
    this.defaultTableHeader = this.tableHeader
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    showPdf () {
      this.pdfVisible = true
    },
    transformData (res) {
      const { data } = res
      return { data: { list: data, total: data.length } }
    },
    addNewMode () {
      this.edit({}, 'add')
    },
    handleCurrentChange () {},
    getQuerydata (v) {
      this.queryParam = v
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    edit (row, flag) {
      const tab = {
        component: Edit,
        params: { row, flag },
        title: `编辑${row.modelName ? '-' + row.modelName : ''}`,
        name: `edit_${row.modelHeadId ? row.modelHeadId : ''}`
      }
      if (flag === 'add') {
        tab.title = '新增'
        tab.name = 'add'
      }
      this.$emit('tab-add', tab)
    },
    preview (row) {
      const tab = {
        component: PdfPreview,
        params: { row },
        title: `预览${row.modelName ? '-' + row.modelName : ''}`,
        name: `preview_${row.modelHeadId ? row.modelHeadId : ''}`
      }
      this.$emit('tab-add', tab)
    }
  }
}
</script>
<style scoped lang="scss">
.the_contractDemoList_wrapper {
}
</style>
