<template>
  <el-container
    class="flex-container the_currency_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="queryForm"
        @getFormData="getQueryData"
        @synchronous-value="syncFilterParams"
      />

      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <ExportExcel
            page-url="/api-base/pj/purchase/purchaseExchangeRate/listByParams"
            :filter-params="queryParam"
            :table-header="tableHeader"
            :dict-codes="dictCodes"
            timeout="1000000"
            :export-size="30000"
            export-mode="front"
            type="default"
          />
        </template>
      </MainHeader>

      <TableView
        ref="exchangeRateSettingTable"
        :table-header="tableHeader"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :com-active="$attrs['changeTab']"
        customTableKey="purchaseBaseSettingExchangeRateSetting"
        url="/api-base/pj/purchase/purchaseExchangeRate/listByParams"
      />
    </el-main>
  </el-container>
</template>

<script>
/**
 * 汇率设置
 */
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MainHeader from 'lib@/components/Table/MainHeader'
import TableView from 'lib@/components/Table/TableView'
import ExportExcel from 'lib@/components/export-excel'

export default {
  name: 'ExchangeRateSetting',
  components: {
    FormWrapper,
    MainHeader,
    TableView,
    ExportExcel
  },
  data () {
    return {
      pageSize: 15,
      queryParam: {},
      dictCodes: {},
      queryForm: [
        {
          prop: 'exchangeDateRange',
          label: this.$t('dataConfMod.nowDate'), // 日期
          type: 'daterange'
        },
        {
          prop: 'extFromCurrencyName',
          label: this.$t('cusEntry.dataConfMod.currencyName') // 货币
        },
        {
          prop: 'fromCurrencyCode',
          label: this.$t('cusEntry.dataConfMod.currencyCode'), // 货币编码
          type: 'dict',
          code: 'currency',
          transformOptions: options => this.transformOptions(options)
        }
      ],
      tableHeader: [
        {
          prop: 'exchangeDate',
          label: this.$t('dataConfMod.nowDate'), // 日期
          minWidth: 150,
          formattor: val => this.$parseTime(val, '{y}-{m}-{d}')
        },
        {
          prop: 'extFromCurrencyName',
          label: this.$t('cusEntry.dataConfMod.currencyName'), // 货币
          minWidth: 120,
          formattor: (val, row) => this.$getDictLabel('currency', row.fromCurrencyCode)
        },
        {
          prop: 'fromCurrencyCode',
          label: this.$t('cusEntry.dataConfMod.currencyCode'), // 货币编码
          minWidth: 120
        },
        {
          prop: 'extUnits',
          label: this.$t('dataConfMod.unit'), // 单位
          minWidth: 80
        },
        {
          prop: 'priceTax',
          label: this.$t('cusEntry.dataConfMod.exchangeRateOfRu'), // 兑卢布汇率
          minWidth: 150
        },
        {
          prop: 'creationDate',
          label: this.$t('common.creationTime'), // 创建时间
          minWidth: 150,
          formattor: val => this.$parseTime(val, '{y}-{m}-{d}')
        },
        {
          prop: 'lastUpdateDate',
          label: this.$t('components.workedProcess.headers.fdEndDate'), // 更新时间
          minWidth: 150,
          formattor: val => this.$parseTime(val, '{y}-{m}-{d}')
        }
      ]
    }
  },
  created () {},
  mounted () {
    this.$nextTick(() => {
      this.getQueryData()
    })
  },
  methods: {
    transformOptions (options) {
      let res = options.map(item => {
        return {
          ...item,
          label: item.value
        }
      })
      return res
    },
    syncFilterParams (values) {
      this.getQueryData(values, false)
    },
    /* 查询列表数据 */
    getQueryData (obj, isQuery = true) {
      const { exchangeDateRange, ...rest } = obj || this.queryParam
      const params = { ...rest }
      if (exchangeDateRange) {
        params.exchangeDateStart = exchangeDateRange[0]
        params.exchangeDateEnd = exchangeDateRange[1]
      }
      this.queryParam = { ...params }
      if (!isQuery) return
      this.$nextTick(() => {
        this.$refs.exchangeRateSettingTable.query()
      })
    }
  }
}
</script>
