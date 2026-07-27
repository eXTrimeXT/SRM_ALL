<template>
  <el-container
    class="flex-container demoorder_list_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="filterConfig"
        @getFormData="getQuerydata"
        @synchronous-value="syncFilterParams"
      >
        <template #orderStatus="{ scope }">
          <DictSelect
            v-model="scope.orderStatus"
            code="ORDER_STATUS"
          />
        </template>
      </FormWrapper>
      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <AuthorityButton
            type="primary"
            @click="addHandle"
          >
            {{
              $t("common.add")
            }}
          </AuthorityButton>
          <MImport
            :title="$t('common.import')"
            up-load-url="/api-base/base/demoorder/importExcel"
            :extra-data="extraData"
            @downloadTemplate="downloadTemplate"
            @handleSuccess="handleSuccess"
          />
          <ExportExcel
            page-url="/api-base/base/demoorder/listPage"
            :filter-params="filterParams"
            :table-header="tableHeader"
            :dict-codes="dictCodes"
            timeout="1000000"
            export-mode="front"
          />
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-header="tableHeader"
        :check-change="handleCurrentChange"
        :page-size="pageSize"
        :checkbox="true"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :source="demoOrderApi.list"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import demoorderEdit from './edit.vue'
import MImport from 'lib@/components/import'
import { downloadFileLink } from 'lib@/utils/file'
import ExportExcel from 'lib@/components/export-excel'
import { demoOrderApi } from 'modb@/demo/api'
export default {
  name: 'DemoorderList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    MImport,
    ExportExcel
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      demoOrderApi:demoOrderApi,
      name: 'demoorderList',
      tableName: 'demoorderTable',
      pageSize: 15,
      gridId: 'list',
      currentRows: [],
      extraData: {
        fileModular: 'base',
        fileFunction: 'demoorder',
        fileType: 'excel'
      },
      dictCodes: {
        orderStatus: 'ORDER_STATUS'
      },
      filterParams: {},
      tableHeader: [
        {
          prop: 'orderStatus',
          label: '订单状态',
          width: 100,
          formattor: (val) => this.$getDictLabel('ORDER_STATUS', val)
        },
        {
          prop: 'orderNumber',
          label: '订单号',
          showType: 'button',
          btnStyle: 'text',
          minWidth: 150,
          callback: row => this.viewHandle(row)
        },
        {
          prop: 'orderAmount',
          label: '订单金额',
          width: 100
        },
        {
          prop: 'rfqSettlementCurrency',
          label: '币种',
          width: 100
        },
        {
          prop: 'termOfPayment',
          label: '付款条件',
          width: 100
        },
        {
          prop: 'paymentMethod',
          label: '付款方式',
          width: 100
        },
        {
          prop: 'deliveryLevel',
          label: '交期等级',
          width: 100
        },
        {
          prop: 'buyerName',
          label: '采购员名称',
          width: 100
        },
        {
          prop: 'tel',
          label: 'TEL',
          width: 100
        },
        {
          prop: 'taxRate',
          label: '税率',
          width: 100
        },
        {
          prop: 'taxKey',
          label: '税率编码 ',
          width: 100
        },
        {
          prop: 'comments',
          label: '备注',
          width: 100
        },
        {
          prop: 'submittedBy',
          label: '订单提交人',
          width: 100
        },
        {
          prop: 'submittedTime',
          label: '提交时间',
          width: 100
        },
        {
          prop: 'comfirmBy',
          label: '订单确认人',
          width: 100
        },
        {
          prop: 'comfirmTime',
          label: '确认时间',
          width: 100
        },
        {
          prop: 'refuseBy',
          label: '订单拒绝人',
          width: 100
        },
        {
          prop: 'refuseTime',
          label: '拒绝时间',
          width: 100
        },
        {
          prop: 'refuseReason',
          label: '拒绝原因',
          width: 100
        },
        {
          prop: 'createdId',
          label: '创建人ID',
          width: 100
        },
        {
          prop: 'creationDate',
          label: '创建时间',
          width: 100
        },
        {
          prop: 'lastUpdateDate',
          label: '最后更新时间',
          width: 100
        },
        {
          prop: 'lastUpdatedBy',
          label: '最后更新人',
          width: 100
        },
        {
          prop: 'organizationCode',
          label: '组织编号',
          width: 100
        },
        {
          prop: 'organizationName',
          label: '组织名称',
          width: 100
        },
        {
          prop: 'vendorCode',
          label: '供应商编号',
          width: 100
        },
        {
          prop: 'vendorName',
          label: '供应商名称',
          width: 100
        },
        {
          prop: 'companyCode',
          label: '公司代码',
          width: 100
        },
        {
          prop: 'responseStatus',
          label: '供应商反馈状态',
          width: 100
        },
        {
          prop: 'sourceSystem',
          label: '来源系统',
          width: 100
        },
        {
          prop: 'purchaseResponse',
          label: '采购商回复',
          width: 100
        },
        {
          prop: 'orderType',
          label: '订单类型',
          width: 100
        },
        {
          prop: 'jitOrder',
          label: '是否JIT订单',
          width: 100
        },
        {
          prop: 'approveStatus',
          label: '审批状态',
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
              callback: (row) => this.editHandle(row),
              // code: "pr:requirementApply:edit",
              // show: row => row.status === "DRAFT",
              formattor: () => {
                return this.$t('common.edit')
              }
            },
            {
              callback: (row) => this.deleteHandle(row),
              // code: "pr:requirementApply:edit",
              // show: row => row.status === "DRAFT",
              formattor: () => {
                return this.$t('common.delete')
              }
            }
          ]
        }
      ],

      filterConfig: [
        { prop: 'organizationId', label: '采购组织ID' },
        {
          prop: 'orderStatus',
          label: '订单状态',
          type: 'slot',
          slot: 'orderStatus'
        },
        { prop: 'orderNumber', label: '订单号' }

      ],
      queryParam: {}
    }
  },
  created () {
    this.defaultTableHeader = this.tableHeader
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    // 导入成功回调
    handleSuccess () {
      this.getQuerydata()
    },
    // 模板下载回调
    downloadTemplate () {
      downloadFileLink(
        '/api-base/base/demoorder/exportExcelTemplate',
        '导入模板.xlsx'
      ).catch(() => {
        this.$message.error('下载失败')
      })
    },
    // 当formwrapper组件中的表单数据有变化时，将数据同步给filterParams。
    // values格式：{ orderStatus: "SUBMIT", orderNumber: "1111"}
    syncFilterParams (values) {
      this.filterParams = values
    },
    // 查询按钮点击回调，params为查询头数据，params格式：{ orderStatus: "SUBMIT", orderNumber: "1111"}
    getQuerydata (params) {
      this.queryParam = params
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    // 查看--订单号(点击进去可以查看单据信息)
    viewHandle (row) {
      this.mode = 'view'
      const tab = {
        component: demoorderEdit,
        params: {
          row,
          flag: this.mode,
          readOnly: true,
          tabName: 'demoorderEdit' + row.demoOrderId
        },
        title: '订单头demo-tab订单号查看',
        name: 'demoorderEdit' + row.demoOrderId
      }
      this.$emit('tab-add', tab)
    },
    // 删除
    deleteHandle (row) {
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          demoOrderApi.delete(row.demoOrderId).then((res) => {
            this.$message.success(res.message)
            this.getQuerydata()
          })
        })
        .catch(() => {})
    },
    // 新增
    addHandle (row) {
      this.mode = 'add'
      const tab = {
        component: demoorderEdit,
        params: {
          row,
          flag: this.mode,
          tabName: 'demoorderEdit'
        },
        title: '订单头demo-tab新增',
        name: 'demoorderEdit'
      }
      this.$emit('tab-add', tab)
    },
    // 编辑
    editHandle (row) {
      this.mode = 'edit'
      const tab = {
        component: demoorderEdit,
        params: {
          row,
          flag: this.mode,
          tabName: 'demoorderEdit' + row.demoOrderId
        },
        title: '订单头demo-tab编辑',
        name: 'demoorderEdit' + row.demoOrderId
      }
      this.$emit('tab-add', tab)
    },
    // 多选框操作回调：val为当前选中的所有行数据数组
    handleCurrentChange (val) {
      this.currentRows = val
    },
    // 上传附件成功
    handleUploadSuccess (file, row, key) {
      const { id, name } = file
      row[key] = id.toString()
    },
    // 删除文件
    handleAttachmentRemove (row, key) {
      row[key] = ''
    }
  }
}
</script>
