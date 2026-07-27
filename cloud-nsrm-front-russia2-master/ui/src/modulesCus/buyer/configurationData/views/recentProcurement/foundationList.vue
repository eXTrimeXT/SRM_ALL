<template>
  <el-container
    class="flex-container the_vendorPurchaseOrderList_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="preArr"
        form-label-width="100px"
        @getFormData="getQuerydata"
      />
      <TableView
        :ref="gridId"
        :bigData="true"
        :table-header="tableHeader"
        :check-change="handleCurrentChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :auto-query="false"
        :url="tableUrl"
        :open-custom-table="true"
        :reserve-selection="true"
        :adeptMeiQl="true"
        row-key="orderNumber"
        :comActive="$attrs['changeTab']"
      />
    </el-main>
  </el-container>
</template>
<script>
import { parseTime } from '@/utils'
import { tabTodoMixin, tabTodoWatch } from '@/utils/mixins'
import ExportExcel from 'lib@/components/export-excel'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MainHeader from 'lib@/components/Table/MainHeader'
import TableView from 'lib@/components/Table/TableView'
import { transformMQL } from 'lib@/utils/util'
import OrganizationSelector from 'lib@/components/organization-selector'
export default {
  name: 'PurchaseOrderListBuyer',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    ExportExcel,
    OrganizationSelector
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      integrationMode: '',
      gridId: 'list',
      currentRows: [],
      tableUrl: '/api-sup-ce/api-ql/PrRecentPurchaseData/query',
      pageSize: 15,
      preArr: [
        {
          prop: 'materialCode',
          // label: '物资编码'
          label: this.$t('cusEntry.inq.materialCode')
        },
        {
          prop: 'materialName',
          // label: '物资名称'
          label: this.$t('cusEntry.competition.materialName')
        },
        {
          prop: 'orgName',
          // label: '采购单位名称'
          label: '采购单位名称'
        }
      ],
      tableHeader: [
        {
          prop: 'materialCode',
          // label: '物资编码',
          label: this.$t('cusEntry.inq.materialCode'),
          width: 120
        },
        {
          prop: 'materialName',
          // label: '物资名称',
          label: this.$t('cusEntry.competition.materialName'),
          width: 120
        },
        {
          prop: 'materialModel',
          // label: '规格型号',
          label: this.$t('vendorMod.specification'),
          width: 120
        },
        {
          prop: 'unit',
          // label: '计量单位',
          label: this.$t('cusEntry.competition.measurementUnit'),
          width: 100,
          dataType: 'dict',
          code: 'unit'
        },
        {
          prop: 'orderNum',
          // label: '采购数量',
          label: this.$t('bid_mod.purQuantity'),
          width: 120
        },
        {
          prop: 'noTaxPrice',
          // label: '未税单价',
          label: this.$t('bid_mod.untaxedPrice'),
          width: 120
        },
        {
          prop: 'taxRate',
          // label: '税率(%)',
          label: this.$t('cusEntry.supplement20250205.taxRatePercentage'),
          width: 120
        },
        {
          prop: 'taxPrice',
          // label: '含税单价',
          label: this.$t('bid_mod.taxUnitPrice'),
          minWidth: 120
        },
        {
          prop: 'orderAmount',
          // label: '采购金额',
          label: this.$t('dataConfMod.purchaseAmount'),
          width: 150
        },
        {
          prop: 'deliveryCycle',
          // label: '到货周期',
          label: this.$t('cusEntry.inq.extLeadTime'),
          width: 120
        },
        {
          prop: 'vendorCode',
          // label: '供应商编码',
          label: this.$t('common.vendorCode'),
          width: 130
        },
        {
          prop: 'vendorName',
          // label: '供应商名称',
          label: this.$t('common.companyName'),
          width: 130
        },
        {
          prop: 'orgName',
          // label: '采购单位名称',
          label: this.$t('cusEntry.supplement20250205.purchaseUnitName'),
          width: 130
        },
        {
          prop: 'startTime',
          // label: '开始时间',
          label: this.$t('components.common.startTime'),
          width: 150,
          dataType: 'dateTime'
        },
        {
          prop: 'endTime',
          // label: '结束时间',
          label: this.$t('components.common.endTime'),
          width: 150,
          dataType: 'dateTime'
        },
        {
          prop: 'status',
          // label: '状态',
          label: this.$t('components.stratProcess.headers.docStatusValue'),
          dataType: 'dict',
          code: 'RECENT_STATUS',
          width: 120
        },
        {
          prop: 'createdByDepartment',
          // label: '创建单位',
          label: this.$t('cusEntry.centralizedPurchase.createCompany'),
          width: 130
        },
        {
          prop: 'operation',
          label: () => this.$t('common.operation'),
          width: 80,
          btnStyle: 'text',
          fixed: 'right',
          showType: 'buttons',
          buttons: [
            {
              callback: row => this.handelUpdate(row),
              // formattor: () => '失效',
              formattor: () => this.$t('common.inactive'),
              show: row => row.status == 'Y'
            }
          ]
        }
      ],
      queryParam: {}
    }
  },
  created () {
    this.getQuerydata()
  },
  methods: {

    getQuerydata (params = {}) {
      this.queryParam = transformMQL.listPageData({
        type: 'PrRecentPurchaseData',
        action: 'query',
        params
      })
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    handleCurrentChange (val) {
      this.currentRows = val
    },
    async getFlowIntegrationMode () {
      let res = await this.$api.base.flowAPI.getFlowIntegrationMode({ businessType: 'ORDER' })
      if (res.data) {
        this.integrationMode = res.data
      }
    },

    handelUpdate (item) {
      // this.$confirm('此操作将该数据修改为失效, 是否继续?', '提示', {
      this.$confirm(this.$t('cusEntry.supplement20250205.operationModifyDataToInvalidContinueQuestion'), this.$t('components.approvalHead.tips.tip'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('components.common.cancel'),
        type: 'warning'
      }).then(() => {
        let params = transformMQL.save('PrRecentPurchaseData', [{ 'dataId': item.dataId, 'status': 'N' }], 'update')
        this.$http({
          url: '/api-sup-ce/api-ql/PrRecentPurchaseData/update',
          method: 'POST',
          data: params,
          loading: true
        }).then(res => {
          this.getQuerydata()
        })
      })
    }
  }
}
</script>
<style scoped lang="scss">
</style>
