<template>
  <el-container
    class="flex-container-notab"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="preArr"
        form-label-width="120px"
        @getFormData="getQueryData"
      />

      <MainHeader :l-span="24">
        <template slot="left">
          <!--自定义导出-->
          <ExportExcel
            page-url="/api-inq/price/priceLibrary/getPriceValidity"
            export-mode="front"
            :table-header="tableHeader"
            :dict-codes="dictCodes"
            :filter-params="queryParam"
            :timeout="10000000"
            @before-open="exportExcelBeforeOpen"
          />
        </template>
      </MainHeader>

      <TableView
        ref="priceValidityReportTable"
        :table-data="tableData"
        :table-header="tableHeader"
        :pre-query-data="queryParam"
        row-key="priceLibraryId"
        url="/api-inq/price/priceLibrary/getPriceValidity"
      />
    </el-main>
  </el-container>
</template>

<script>
/**
 * 价格有效期查询报表
 */
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import ExportExcel from 'lib@/components/export-excel'

export default {
  name: 'PriceValidityReport',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    ExportExcel
  },
  data () {
    return {
      queryParam: {},
      tableHeader: [
        // t 业务实体
        { prop: 'ceeaOrgName', label: this.$t('bid_mod.businessEntity'), minWidth: 150 },
        // t 供应商编号
        { prop: 'vendorCode', label: this.$t('bidMod.vendorCode'), minWidth: 120 },
        // t 供应商名称
        { prop: 'vendorName', label: this.$t('bidMod.vendorName'), minWidth: 150 },
        // t 品类
        { prop: 'categoryName', label: this.$t('common.category'), minWidth: 140 },
        // t 物料编码
        { prop: 'itemCode', label: this.$t('bidMod.itemCode'), minWidth: 120 },
        // t 物料名称
        { prop: 'itemDesc', label: this.$t('bidMod.itemName'), minWidth: 150 },
        // t 单位
        {
          prop: 'unit',
          label: this.$t('bidMod.unit'),
          minWidth: 100,
          dataType: 'dict',
          code: 'unit'
        },
        // 含税价
        { prop: 'taxPrice', label: this.$t('contractMod.taxedPrice'), minWidth: 100 },
        // t 税率
        { prop: 'taxKey', label: this.$t('contractMod.taxRate'), minWidth: 140 },
        // t 结算币种
        {
          prop: 'currencyCode',
          label: this.$t('bidMod.currency'),
          minWidth: 100,
          dataType: 'dict',
          code: 'currency'
        },
        // 价格有效期自
        { prop: 'effectiveDate', label: this.$t('quota.priceStartTime'), minWidth: 140, dataType: 'dateTime' },
        // 价格有效期至
        { prop: 'expirationDate', label: this.$t('quota.priceEndTime'), minWidth: 140, dataType: 'dateTime' },
        // 剩余天数
        { prop: 'remainDays', label: this.$t('bidMod.remainingDays'), minWidth: 140 }
      ],
      tableData: [],
      preArr: [
        // f 物料编码
        {
          prop: 'itemCode',
          label: this.$t('bidMod.itemCode'),
          type: 'quicksearch',
          showKey: 'materialCode',
          name: 'scc_base_material_item'
        },
        // f 物料名称
        { prop: 'itemDesc', label: this.$t('bidMod.itemName') },
        {
          prop: 'dateList',
          // '价格有效期'
          label: this.$t('bidMod.common.priceValidity'),
          type: 'daterange'
        },
        // f 供应商名称
        {
          prop: 'vendorId',
          label: this.$t('bidMod.vendorName'),
          type: 'quicksearch',
          showKey: 'companyName',
          propKey: 'companyId',
          name: 'scc_sup_company_info_display_buyer'
        }
      ],
      dictCodes: {
        unit: 'unit',
        currencyCode: 'currency'
      }
    }
  },
  methods: {
    getQueryData (payload) {
      if (!Object.keys(payload).find(key => payload[key])) {
        // '请至少输入一项查询条件！'
        this.$message.warning(this.$t('bidMod.enterOneQueryCondition'))
        return
      }
      if (payload.dateList && Array.isArray(payload.dateList) && payload.dateList.length === 2) {
        payload = {
          ...payload,
          effectiveDate: payload.dateList[0],
          expirationDate: payload.dateList[1]
        }
      }
      this.queryParam = payload
      this.$nextTick(() => {
        this.$refs.priceValidityReportTable.query()
      })
    },

    /* 导出前置判断 */
    exportExcelBeforeOpen (value, callback) {
      if (!Object.keys(this.queryParam).find(key => this.queryParam[key])) {
        // '请至少输入一项查询条件后点击查询，再执行导出！'
        this.$message.warning(this.$t('bidMod.executeExport'))
        callback(null)
      }
    }
  }
}
</script>
