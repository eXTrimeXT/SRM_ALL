<template>
  <div class="wrapper">
    <div v-if="designId" class="btns mb-10">
      <ExportExcel
        page-url="/api-sou/design/plan/getPullOrder"
        :filter-params="filterParams"
        :dict-codes="dictCodes"
        :table-header="tableHeader"
        export-mode="front"
        type="primary"
      />
    </div>
    <BaseTable
      stripe
      index
      :data="tableData.slice((pageInfo.pageNum-1)*pageInfo.pageSize,pageInfo.pageNum*pageInfo.pageSize)"
      :columns="tableColumns"
      :empty-text="$t('components.noData')"
      :max-height="250"
      border
    />
    <CPagination
      ref="queryPagination"
      class="c-query-table-pagination"
      style="padding-bottom:4px"
      :total="tableData.length"
      :page-num="pageInfo.pageNum"
      :page-size="pageInfo.pageSize"
      @current-change="handleCurrentChange"
      @size-change="handleSizeChange"
    />
  </div>
</template>
<script>
import BaseTable from 'lib@/components/BaseTable'
import CPagination from 'lib@/components/c-pagination'
import ExportExcel from 'lib@/components/export-excel'

export default {
  name: 'LastYear',
  components: {
    BaseTable,
    CPagination,
    ExportExcel
  },
  props: {
    value: {
      type: Array,
      default: () => []
    },
    designId: null
  },
  data () {
    return {
      exportLoading: false,
      tableData: [],
      tableColumns: [],
      pageInfo: {
        pageNum: 1,
        pageSize: 15,
        total: 0
      },
      dictCodes: {
        buyType: 'PR_BUY_TYPE',
        unitCode: 'unit',
        areaCode: 'REGION'
      }
    }
  },
  computed: {
    filterParams () {
      return {
        type: 1,
        designId: this.designId
      }
    },
    tableHeader () {
      let columns = this.tableColumns.filter(item => item.attrs.type !== 'index').map(item => {
        return {
          prop: item.attrs.prop,
          label: item.attrs.label
        }
      })
      return columns
    }
  },
  watch: {
    value: {
      handler (nVal) {
        if (nVal) {
          this.tableData = nVal
        }
      },
      immediate: true,
      deep: true
    }
  },
  created () {
    this.tableColumns = [
      {
        attrs: {
          label: this.$t('common.sort'), // '序号',
          type: 'index',
          width: 60
        }
      },
      {
        attrs: {
          prop: 'areaCode',
          label: this.$t('cusEntry.centralizedPurchase.supplyScope'), // '供货范围',
          minWidth: 130,
          showOverflowTooltip: true,
          formatter: (row, column, cellValue) => this.$getDictLabel('REGION', cellValue)
        }
      },
      {
        attrs: {
          prop: 'materialCode',
          label: this.$t('common.materialCode'), // '物资编码',
          minWidth: 130,
          showOverflowTooltip: true
        }
      },
      {
        attrs: {
          prop: 'materialName',
          label: this.$t('common.materialName'), // '物资名称',
          minWidth: 130,
          showOverflowTooltip: true
        }
      },
      {
        attrs: {
          prop: 'model',
          label: this.$t('common.specification'), // '规格型号',
          minWidth: 130,
          showOverflowTooltip: true
        }
      },
      {
        attrs: {
          prop: 'unitCode',
          label: this.$t('cusEntry.competition.measurementUnit'), // '计量单位',
          minWidth: 130,
          showOverflowTooltip: true,
          formatter: (row, column, cellValue) => this.$getDictLabel('unit', cellValue)
        }
      },
      {
        attrs: {
          prop: 'orderNum',
          label: this.$t('competition.quantity'), // '数量',
          minWidth: 130,
          showOverflowTooltip: true
        }
      },
      {
        attrs: {
          prop: 'brand',
          label: this.$t('dataConfMod.band'), // '品牌',
          minWidth: 130,
          showOverflowTooltip: true
        }
      },
      {
        attrs: {
          prop: 'priceTax',
          label: this.$t('bidMod.quotenotaxPrice2'), // '未税单价',
          minWidth: 130,
          showOverflowTooltip: true
        }
      },
      {
        attrs: {
          prop: 'taxRate',
          label: this.$t('bidMod.taxRatePer_price'), // '税率%',
          minWidth: 130,
          showOverflowTooltip: true
        }
      },
      {
        attrs: {
          prop: 'ratePrice',
          label: this.$t('bidMod.tech_unitPrice'), // '含税单价',
          minWidth: 130,
          showOverflowTooltip: true
        }
      },
      {
        attrs: {
          prop: 'moneyAmount',
          label: this.$t('orderMod.amount'), // '金额',
          minWidth: 130,
          showOverflowTooltip: true
        }
      },
      {
        attrs: {
          prop: 'priceTotal',
          label: this.$t('accountMod.priceTaxSum'), // '价税合计',
          minWidth: 130,
          showOverflowTooltip: true
        }
      },
      {
        attrs: {
          prop: 'supName',
          label: this.$t('common.companyName'), // '供应商名称',
          minWidth: 130,
          showOverflowTooltip: true
        }
      },
      {
        attrs: {
          prop: 'organizationName',
          label: this.$t('cusEntry.inq.orgDeptName'), // '申请单位',
          minWidth: 130,
          showOverflowTooltip: true
        }
      },
      {
        attrs: {
          prop: 'oneTypeName',
          label: this.$t('cusEntry.supplement20250121.level1'), // '一级分类',
          minWidth: 130,
          showOverflowTooltip: true
        }
      },
      {
        attrs: {
          prop: 'twoTypeName',
          label: this.$t('cusEntry.supplement20250121.level2'), // '二级分类',
          minWidth: 130,
          showOverflowTooltip: true
        }
      },
      {
        attrs: {
          prop: 'orderDate',
          label: this.$t('oneStopShopping.orderDate'), // '订单日期',
          minWidth: 130,
          dataType: 'dateTime',
          showOverflowTooltip: true
        }
      },
      {
        attrs: {
          prop: 'buyUserName',
          label: this.$t('orderMod.buyerName'), // '采购员',
          minWidth: 130,
          showOverflowTooltip: true
        }
      },
      {
        attrs: {
          prop: 'buyType',
          label: this.$t('cusEntry.inq.extBuyType'), // '购买类型',
          minWidth: 130,
          showOverflowTooltip: true,
          formatter: (row, column, cellValue) => this.$getDictLabel('PR_BUY_TYPE', cellValue)
        }
      },
      {
        attrs: {
          prop: 'createUnitName',
          label: this.$t('cusEntry.centralizedPurchase.createCompany'), // '创建单位',
          minWidth: 130,
          showOverflowTooltip: true
        }
      },
      {
        attrs: {
          prop: 'dataSource',
          label: this.$t('vendorMod.dataSource'), // '数据来源',
          minWidth: 130,
          showOverflowTooltip: true
        }
      }
    ]
  },
  methods: {
    handleExport () {

    },
    handleCurrentChange (pageNum) {
      this.pageInfo.pageNum = pageNum
    },
    handleSizeChange (pageSize) {
      this.pageInfo.pageNum = 1
      this.pageInfo.pageSize = pageSize
    }
  }
}
</script>
<style lang="scss" scoped>
.mb-10 {
  margin-bottom: 10px;
}
</style>
