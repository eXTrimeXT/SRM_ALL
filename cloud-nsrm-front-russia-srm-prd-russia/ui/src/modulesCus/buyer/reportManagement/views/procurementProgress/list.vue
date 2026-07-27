<template>
  <el-container
    class="flex-container procurementProgressList"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="preArr"
        @synchronous-value="syncFilterParams"
        @getFormData="getQuerydata"
      />
      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <ExportExcel
            type="default"
            :page-url="tableUrl"
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
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :table-header="tableHeader"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        :url="tableUrl"
      />
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import ExportExcel from 'lib@/components/export-excel'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'

export default {
  name: 'ProcurementProgressList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    ExportExcel
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      dictCodes: {
        extBuyType: 'PR_BUY_TYPE',
        auditStatus: 'APPROVAL_STATUS'
      },
      tableUrl: '/api-sup-ce/purchaseOrder/process/listPage',
      gridId: 'list',
      pageSize: 15,
      queryParam: {},
      filterParams: {},
      tableData: [],
      tableHeader: [],
      preArr: [
        {
          prop: 'applyFullName',
          label: '申请人'
        },
        {
          prop: 'extUserName',
          label: '使用人'
        },
        {
          prop: 'extUseDepartmentName',
          label: '使用部门'
        },
        {
          prop: 'orgName',
          label: '申请单位'
        },
        {
          prop: 'materialId',
          label: '物料编码',
          showKey: 'materialCode',
          propKey: 'materialId',
          type: 'quicksearch',
          name: 'scc_base_material_item_contract'
        },
        {
          prop: 'extBuyType',
          label: '物料购买类型',
          type: 'dict',
          code: 'PR_BUY_TYPE'
        },
        {
          prop: 'ceeaEmpUsername',
          label: '采购员'
        },
        {
          prop: 'orderNumber',
          label: '订单编号'
        },
        {
          prop: 'vendorName',
          label: '供应商名称'
        },
        {
          prop: 'dateList',
          label: '申请日期',
          type: 'daterange'
        },
        {
          prop: 'requirementHeadNum',
          label: '采购申请编号'
        },
        {
          prop: 'materialName',
          label: '物料名称'
        }
      ]
    }
  },
  created () {
    this.tableHeader = [
      {
        prop: 'requirementHeadNum',
        label: '采购申请编号',
        minWidth: 150
      },
      {
        prop: 'applyDate',
        label: '申请日期',
        formattor: val => val ? this.$dayjs(val).format('YYYY-MM-DD') : '',
        minWidth: 150
      },
      {
        prop: 'extApproveTime',
        label: '审批结束日期',
        formattor: val => val ? this.$dayjs(val).format('YYYY-MM-DD') : '',
        minWidth: 150
      },
      {
        prop: 'requirementDate',
        label: '需求日期',
        formattor: val => val ? this.$dayjs(val).format('YYYY-MM-DD') : '',
        minWidth: 150
      },
      {
        prop: 'applyFullName',
        label: '申请人',
        minWidth: 150
      },
      {
        prop: 'extUserName',
        label: '使用人',
        minWidth: 150
      },
      {
        prop: 'extUseDepartmentName',
        label: '使用部门',
        minWidth: 150
      },
      {
        prop: 'auditStatus',
        label: '需求状态',
        minWidth: 150,
        dataType: 'dict',
        code: 'APPROVAL_STATUS'
      },
      {
        prop: 'extClosedCause',
        label: '关闭原因',
        minWidth: 150
      },
      {
        prop: 'receiveAddress',
        label: '收货地址',
        minWidth: 150
      },
      {
        prop: 'extReceiver',
        label: '收货人',
        minWidth: 150
      },
      {
        prop: 'receiveTelephone',
        label: '收货人联系方式',
        minWidth: 150
      },
      {
        prop: 'extAreaName',
        label: '区域',
        minWidth: 150
      },
      {
        prop: 'orgName',
        label: '申请单位',
        minWidth: 150
      },
      {
        prop: 'materialCode',
        label: '物料编码',
        minWidth: 150
      },
      {
        prop: 'materialName',
        label: '物料名称',
        minWidth: 150
      },
      {
        prop: 'extMaterialModel',
        label: '规格型号',
        minWidth: 150
      },
      {
        prop: 'unit',
        label: '计量单位',
        minWidth: 150
      },
      {
        prop: 'requirementQuantity',
        label: '需求数量',
        minWidth: 150
      },
      {
        prop: 'extBuyType',
        label: '物料购买类型',
        dataType: 'dict',
        code: 'PR_BUY_TYPE',
        minWidth: 150
      },
      {
        prop: 'extWarrantyPeriod',
        label: '质保期（自然日）',
        minWidth: 150
      },
      {
        prop: 'deliveryDate',
        label: '交货日期',
        formattor: val => val ? this.$dayjs(val).format('YYYY-MM-DD') : '',
        minWidth: 150
      },

      {
        prop: 'extDeliveryCycle',
        label: '到货周期',
        minWidth: 150
      },
      {
        prop: 'orderNumber',
        label: '订单编号',
        minWidth: 150
      },
      {
        prop: 'vendorName',
        label: this.$t('common.vendorName'),
        minWidth: 150
      },
      {
        prop: 'vendorCode',
        label: this.$t('common.vendorCode'),
        minWidth: 150
      },
      {
        prop: 'ceeaEmpUsername',
        label: '采购员',
        minWidth: 150
      },
      {
        prop: 'extPurchaserOrgName',
        label: '采购单位',
        minWidth: 150
      },
      {
        prop: 'extPurchaserPhone',
        label: '采购员联系方式',
        minWidth: 160
      },
      {
        prop: 'orderNum',
        label: '实际订单数量',
        minWidth: 160
      },
      {
        prop: 'ceeaPurchaseOrderDate',
        label: '订单日期',
        formattor: val => val ? this.$dayjs(val).format('YYYY-MM-DD') : '',
        minWidth: 160
      },
      {
        prop: 'comments',
        label: '备注信息',
        minWidth: 160
      },
      {
        prop: 'closedCause',
        label: '订单取消原因',
        minWidth: 160
      },
      {
        prop: 'deliveryNumber',
        label: '送货单单号',
        minWidth: 160
      },
      {
        prop: 'deliveryQuantity',
        label: '发货数量',
        minWidth: 160
      },
      {
        prop: 'extExpressNo',
        label: '物流单号',
        minWidth: 160
      },
      {
        prop: 'deliveryTime',
        label: '发货日期',
        formattor: val => val ? this.$dayjs(val).format('YYYY-MM-DD') : '',
        minWidth: 160
      },
      {
        prop: 'receivedTime',
        label: '收货日期',
        formattor: val => val ? this.$dayjs(val).format('YYYY-MM-DD') : '',
        minWidth: 160
      },
      {
        prop: 'receivedNum',
        label: '收货数量',
        minWidth: 160
      },
      {
        prop: 'warehouseQuantity',
        label: '入库数量',
        minWidth: 160
      },
      {
        prop: 'extStorageTime',
        label: '入库日期',
        formattor: val => val ? this.$dayjs(val).format('YYYY-MM-DD') : '',
        minWidth: 160
      },
      {
        prop: 'withoutWarehouseQuantity',
        label: '未入库数量',
        minWidth: 160
      }
    ]
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    syncFilterParams (values) {
      const { dateList = [], ...rest } = values || {}
      let params = { ...rest }
      if (dateList.length > 0) {
        params.applyDateFrom = dateList[0]
        params.applyDateTo = dateList[1]
      }
      this.filterParams = params
    },
    getQuerydata (v) {
      const { dateList = [], ...rest } = v || {}
      let params = { ...rest }
      if (dateList.length > 0) {
        params.applyDateFrom = dateList[0]
        params.applyDateTo = dateList[1]
      }
      this.queryParam = params
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    }
  }
}
</script>
<style scoped lang="scss">
</style>
