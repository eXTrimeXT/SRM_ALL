<template>
  <el-container
    class="flex-container ontimeRatioArrivalReportDetailList"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="preArr"
        :pre-form-obj="preFormObj"
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
  name: 'OntimeRatioArrivalReportDetailList',
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
        orderDetailStatus: 'OrderDetailStatus',
        unit: 'unit',
        extBuyType: 'PR_BUY_TYPE'
      },
      tableUrl: '/api-sup-ce/pj/order/report/receiveOnTimeRatioDetail',
      gridId: 'list',
      pageSize: 15,
      preFormObj: {},
      queryParam: {},
      filterParams: {},
      tableData: [],
      preArr: [
        {
          prop: 'ceeaPurchaseOrderDate',
          label: this.$t('oneStopShopping.orderDate'),
          type: 'daterange'
        },
        {
          prop: 'orderNumber',
          label: '订单编号'
        },
        {
          prop: 'ceeaEmpNo',
          label: '采购员',
          type: 'quicksearch',
          showKey: 'nickname',
          propKey: 'username',
          name: 'scc_rbac_user_display'
        },
        {
          prop: 'ceeaOrgId',
          label: '申请单位',
          type: 'OUorganizationSelector'
        },
        {
          prop: 'vendorCode',
          label: this.$t('common.vendorName'),
          type: 'quicksearch',
          showKey: 'companyName',
          propKey: 'companyCode',
          name: 'scc_sup_company_info_all'
        },
        {
          prop: 'materialCode',
          label: '物料编码',
          type: 'quicksearch',
          showKey: 'materialCode',
          propKey: 'materialCode',
          name: 'scc_base_material_item_contract'
        },
        {
          prop: 'deliveryDate',
          label: '交货日期',
          type: 'daterange'
        },
        {
          prop: 'categoryName',
          label: '品类',
          type: 'catSelect',
          showKey: 'categoryName'
        }
      ],
      tableHeader: [
        {
          prop: 'vendorCode',
          label: this.$t('common.vendorCode'),
          minWidth: 150
        },
        {
          prop: 'vendorName',
          label: this.$t('common.vendorName'),
          minWidth: 150
        },
        {
          prop: 'extVendorContacts',
          label: '供应商联系人',
          minWidth: 150
        },
        {
          prop: 'extVendorPhone',
          label: '供应商联系人电话',
          minWidth: 150
        },
        {
          prop: 'extBuyType',
          label: '采购类型',
          minWidth: 150,
          dataType: 'dict',
          code: 'PR_BUY_TYPE'
        },
        {
          prop: 'extPurchaserOrgName',
          label: '采购单位',
          minWidth: 160
        },
        {
          prop: 'extUseDepartmentName',
          label: '使用部门',
          minWidth: 160
        },
        {
          prop: 'extUserName',
          label: '使用人',
          minWidth: 150
        },
        {
          prop: 'extUserCode',
          label: '使用人工号',
          minWidth: 150
        },
        {
          prop: 'easReceiveTime',
          label: 'EAS收货时间',
          minWidth: 150
        },
        {
          prop: 'ceeaOrgName',
          label: '申请单位',
          minWidth: 160
        },
        {
          prop: 'extApplyDate',
          label: '申请日期',
          formattor: val => val ? this.$dayjs(val).format('YYYY-MM-DD') : '',
          minWidth: 160
        },
        {
          prop: 'ceeaPurchaseOrderDate',
          label: '订单日期',
          formattor: val => val ? this.$dayjs(val).format('YYYY-MM-DD') : '',
          minWidth: 160
        },
        {
          prop: 'orderNumber',
          label: '订单编号',
          minWidth: 160
        },
        {
          prop: 'deliveryNumber',
          label: '送货单编号',
          minWidth: 160
        },
        {
          prop: 'deliveryDate',
          label: '交货日期',
          formattor: val => val ? this.$dayjs(val).format('YYYY-MM-DD') : '',
          minWidth: 160
        },
        {
          prop: 'extDeliveryCycle',
          label: '到货周期',
          minWidth: 160
        },
        {
          prop: 'materialCode',
          label: '物料编码',
          minWidth: 160
        },
        {
          prop: 'materialName',
          label: '物料名称',
          minWidth: 160
        },
        {
          prop: 'specification',
          label: '规格型号',
          minWidth: 160
        },
        {
          prop: 'categoryName',
          label: '品类',
          minWidth: 160
        },
        {
          prop: 'unit',
          label: '基本计量单位',
          minWidth: 160
        },
        {
          prop: 'extBrand',
          label: '品牌',
          minWidth: 160
        },
        {
          prop: 'ceeaEmpUsername',
          label: '采购员',
          minWidth: 160
        },
        {
          prop: 'requirementQuantity',
          label: '需求数量',
          minWidth: 160
        },
        {
          prop: 'orderNum',
          label: '订单数量',
          minWidth: 160
        },
        {
          prop: 'storageNum',
          label: '入库数量',
          minWidth: 160
        },
        {
          prop: 'receiveNum',
          label: '收货数量',
          minWidth: 160
        },
        {
          prop: 'orderCancelReason',
          label: '采购订单取消原因',
          minWidth: 160
        },
        {
          prop: 'orderDetailStatus',
          label: '订单行状态',
          dataType: 'dict',
          code: 'OrderDetailStatus',
          minWidth: 160
        },
        {
          prop: 'deliveryNoteDetailStatus',
          label: '送货单行状态',
          minWidth: 160
        }
      ]
    }
  },
  created () {
    // this.$nextTick(() => {
    //   this.getQuerydata()
    // })
  },
  methods: {
    syncFilterParams (values) {
      this.filterParams = values
    },
    getQuerydata (v) {
      if (typeof (v) === 'string') {
        const arr = v.split(',')
        this.preFormObj = {
          vendorCode: arr[0],
          ceeaEmpNo: arr[1],
          ceeaOrgId: Number(arr[2]),
          ceeaOrgCode: arr[3],
          ceeaPurchaseOrderDate: [arr[4], arr[5]]
        }
        this.queryParam = this.preFormObj || {}
      } else {
        this.queryParam = v || {}
      }
      this.filterParams = this.queryParam
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    }
  }
}
</script>
<style scoped lang="scss">
</style>
