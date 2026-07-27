<template>
  <el-container
    class="flex-container"
    direction="vertical"
  >
    <el-main>
      <FormWrapper :form-array="preArr" @getFormData="getQueryData" />
      <TableView
        :ref="gridList"
        :table-header="tableHeader"
        :page-size="pageSize"
        :pre-query-data="queryParams"
        open-custom-table
        :auto-query="true"
        :com-active="$attrs['changeTab']"
        :url="pageUrl"
      />
    </el-main>
  </el-container>
</template>

<script>
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
export default {
  name: 'MaterialAreaDetailList',
  components: {
    TableView,
    FormWrapper
  },
  data () {
    return {
      gridList: 'MaterialAreaDetailList',
      preArr: [],
      tableHeader: [],
      pageSize: 15,
      queryParams: {},
      pageUrl: '/api-sup-ce/sc/price/trends/analysis/getOrderPriceTrendsLineList'
    }
  },
  created () {
    this.preArr = [
      {
        prop: 'materialCode',
        label: () => this.$t('cusEntry.reportManagement.materialCode')
      },
      {
        prop: 'materialName',
        label: () => this.$t('cusEntry.reportManagement.materialName')
      },
      {
        prop: 'brand',
        label: () => this.$t('cusEntry.reportManagement.brand')
      },
      {
        prop: 'ym',
        label: () => this.$t('cusEntry.reportManagement.orderDate'),
        type: 'month'
      },
      {
        prop: 'areaCode',
        label: () => this.$t('cusEntry.reportManagement.area')
      },
      {
        prop: 'orgName',
        label: () => this.$t('cusEntry.reportManagement.businessEntity')
      }
    ]
    this.tableHeader = [
      {
        prop: 'materialCode',
        label: () => this.$t('cusEntry.reportManagement.materialCode'),
        minWidth: 120
      },
      {
        prop: 'materialName',
        label: () => this.$t('cusEntry.reportManagement.materialName'),
        minWidth: 120
      },
      {
        prop: 'materialDescribe',
        label: () => this.$t('cusEntry.reportManagement.materialDesc'),
        minWidth: 120
      },
      {
        prop: 'brand',
        label: () => this.$t('cusEntry.reportManagement.brand'),
        minWidth: 120
      },
      {
        prop: 'areaCode',
        label: () => this.$t('cusEntry.reportManagement.area'),
        minWidth: 120
      },
      {
        prop: 'orgName',
        label: () => this.$t('cusEntry.reportManagement.businessEntity'),
        minWidth: 120
      },
      {
        prop: 'noTaxPrice',
        label: () => this.$t('cusEntry.reportManagement.noTaxPrice'),
        minWidth: 120
      },
      {
        prop: 'leadTime',
        label: () => this.$t('cusEntry.reportManagement.deliveryCycle'),
        minWidth: 120
      },
      {
        prop: 'supCode',
        label: () => this.$t('cusEntry.reportManagement.vendorCode'),
        minWidth: 120
      },
      {
        prop: 'supName',
        label: () => this.$t('cusEntry.reportManagement.vendorName'),
        minWidth: 120
      },
      {
        prop: 'supTel',
        label: () => this.$t('cusEntry.reportManagement.vendorContactMethod'),
        minWidth: 140
      },
      {
        prop: 'orderDate',
        label: () => this.$t('cusEntry.reportManagement.orderDate'),
        minWidth: 120
      }
    ]
  },
  methods: {
    // 查询
    getQueryData (params) {
      this.queryParams = params
      this.$nextTick(() => {
        this.$refs[this.gridList].query()
      })
    }
  }
}
</script>
