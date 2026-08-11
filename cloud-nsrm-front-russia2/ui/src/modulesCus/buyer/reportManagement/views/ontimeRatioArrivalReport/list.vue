<template>
  <el-container
    class="flex-container ontimeRatioArrivalReportList"
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
  name: 'OntimeRatioArrivalReportList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    ExportExcel
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      dictCodes: {},
      tableUrl: '/api-sup-ce/pj/order/report/receiveOnTimeRatio',
      gridId: 'list',
      pageSize: 15,
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
          prop: 'ceeaEmpNo',
          label: this.$t('bidMod.quotePurchasor'),
          type: 'quicksearch',
          showKey: 'nickname',
          propKey: 'username',
          name: 'scc_rbac_user_display'
        },
        // 申请单位
        {
          prop: 'ceeaOrgId',
          label: this.$t('cusEntry.bidSuperviseReport.extOrgOuName'),
          type: 'OUorganizationSelector'
        },
        {
          prop: 'vendorCode',
          label: this.$t('common.vendorName'),
          type: 'quicksearch',
          showKey: 'companyName',
          propKey: 'companyCode',
          name: 'scc_sup_company_info_all'
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
          prop: 'ceeaOrgName',
          label: this.$t('cusEntry.bidSuperviseReport.extOrgOuName'),
          minWidth: 160
        },
        {
          prop: 'ceeaEmpUsername',
          label: this.$t('cusEntry.inq.pruchaser'),
          minWidth: 160
        },
        // 到货及时率（%）
        {
          prop: 'receiveOnTime',
          label: this.$t('cusEntry.supplement20250205.onTimeDeliveryRate'),
          minWidth: 160
        },
        {
          prop: 'operation',
          label: this.$t('common.operation'),
          width: 150,
          showType: 'buttons',
          fixed: 'right',
          btnStyle: 'text',
          buttons: [
            {
              callback: row => this.viewDetail(row),
              formattor: () => this.$t('accountMod.viewDetail')
            }
          ]
        }
      ]
    }
  },
  created () {
    // 进页面不自动查询
    // this.$nextTick(() => {
    //   this.getQuerydata()
    // })
  },
  methods: {
    syncFilterParams (values) {
      this.filterParams = values
    },
    getQuerydata (v) {
      this.queryParam = v || {}
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    viewDetail (row) {
      this.$emit('tab-show', 'OntimeRatioArrivalReportDetailList')
      this.$nextTick(() => {
        this.__setTabTodo(`OntimeRatioArrivalReportDetailList.getQuerydata.${row.vendorCode},${row.ceeaEmpNo},${row.ceeaOrgId},${row.ceeaOrgCode},${this.queryParam.ceeaPurchaseOrderDate}`)
      })
    }
  }
}
</script>
<style scoped lang="scss">
</style>
