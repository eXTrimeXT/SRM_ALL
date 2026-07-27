<template>
  <el-container
    class="flex-container the_currency_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="queryForm"
        @getFormData="getQueryData"
      />

      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <!-- 新增 -->
          <AuthorityButton
            type="primary"
            code="exchangeRateSetting:add"
            @click="addExchangeRateSetting"
          >
            {{ $t("common.add") }}
          </AuthorityButton>
          <!-- 导入 -->
          <MImport
            type="default"
            :title="$t('common.import')"
            up-load-url="/api-base/purchase/purchaseExchangeRate/importExcel"
            :extra-data="extraData"
            code="exchangeRateSetting:import"
            @downloadTemplate="downloadTemplate"
            @handleSuccess="handleSuccess"
          />
        </template>
      </MainHeader>

      <TableView
        ref="exchangeRateSettingTable"
        :table-data="tableList"
        :table-header="tableHeader"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :com-active="$attrs['changeTab']"
        customTableKey="purchaseBaseSettingExchangeRateSetting"
        url="/api-base/purchase/purchaseExchangeRate/listByParams"
      />
    </el-main>

    <!--弹窗区域，新增汇率-->
    <ExchangeRateSettingDetail
      :visible.sync="dialogFormVisible"
      :edit-row="editRow"
      :currency-list="currencyList"
      @submitSuccess="detailSubmitSuccess"
    />
  </el-container>
</template>

<script>
/**
 * 汇率设置
 */
import { getAllPurCurrency } from '@/api/common'
import { adaptDictData } from '@/utils'
import { parseTimeYMD } from 'lib@/composition/origin/composition'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MainHeader from 'lib@/components/Table/MainHeader'
import TableView from 'lib@/components/Table/TableView'
import ExchangeRateSettingDetail from './exchangeRateSetting/ExchangeRateSettingDetail'
import MImport from 'lib@/components/import'
import { downloadFileLink } from 'lib@/utils/file'

export default {
  name: 'ExchangeRateSetting',
  components: {
    FormWrapper,
    MainHeader,
    TableView,
    ExchangeRateSettingDetail,
    MImport
  },
  data () {
    return {
      extraData: {
        fileModular: 'base',
        fileFunction: 'exchangeSetting',
        fileType: 'excel'
      },
      pageSize: 15,
      showFilterBar: 1,
      queryParam: {},
      tableList: [],
      dialogFormVisible: false,
      editRow: null,
      currencyList: [],
      queryForm: [
        // q 汇率类型
        {
          prop: 'rateType',
          label: this.$t('bid_mod.exchangeRateType'),
          type: 'dict',
          code: 'EXCHANGE_RATE_TYPE'
        },
        // q 币种转换日期
        {
          prop: 'exchangeDate',
          label: this.$t('bid_mod.currencyChangeDate'),
          type: 'date'
        },
        // q 来源币种
        {
          prop: 'fromCurrencyCode',
          label: this.$t('bid_mod.fromCurrencyCode'),
          type: 'dict',
          code: 'currency'
        },
        // q 目标币种
        {
          prop: 'toCurrencyCode',
          label: this.$t('dataConfMod.toCurrencyCode'),
          type: 'dict',
          code: 'currency'
        },
        // q 数据来源
        {
          prop: 'sourceType',
          label: this.$t('materialPrice.dataSource'),
          type: 'dict',
          code: 'EXCHANGE_RATE_SOURCE_TYPE'
        }
      ],
      tableHeader: [
        // t 汇率类型
        {
          prop: 'rateType',
          label: this.$t('bid_mod.exchangeRateType'),
          dataType: 'dict',
          code: 'EXCHANGE_RATE_TYPE',
          minWidth: 110
        },
        // t 币种转换日期
        {
          prop: 'exchangeDate',
          label: this.$t('bid_mod.currencyChangeDate'),
          minWidth: 150,
          formattor: val => parseTimeYMD(val)
        },
        // t 来源币种
        {
          prop: 'fromCurrencyCode',
          label: this.$t('bid_mod.fromCurrencyCode'),
          dataType: 'dict',
          code: 'currency',
          minWidth: 110
        },
        // t 目标币种
        {
          prop: 'toCurrencyCode',
          label: this.$t('dataConfMod.toCurrencyCode'),
          dataType: 'dict',
          code: 'currency',
          minWidth: 110
        },
        // t 汇率
        {
          prop: 'priceTax',
          label: this.$t('bid_mod.priceTax'),
          minWidth: 80
        },
        // t 数据来源
        {
          prop: 'sourceType',
          label: this.$t('materialPrice.dataSource'),
          dataType: 'dict',
          code: 'EXCHANGE_RATE_SOURCE_TYPE',
          minWidth: 100
        },
        // t 创建人
        {
          prop: 'createdUserName',
          label: this.$t('dataConfMod.createdBy'),
          minWidth: 100
        },
        // t 创建时间
        {
          prop: 'creationDate',
          label: this.$t('dataConfMod.creationDate'),
          minWidth: 100,
          formattor: val => parseTimeYMD(val)
        },
        // t 操作
        {
          prop: 'operation',
          label: this.$t('common.operation'), // 操作
          width: 90,
          btnStyle: 'text',
          fixed: 'right',
          showType: 'buttons',
          buttons: [
            // t 编辑
            {
              code: 'exchangeRateSetting:edit',
              callback: (row) => this.editExchangeRateSetting(row),
              formattor: () => this.$t('common.edit')
            }
          ]
        }
      ]
    }
  },
  created () {
    // 查询币种
    getAllPurCurrency().then((res) => {
      this.currencyList = adaptDictData(res.data, 'currency')
    })
  },
  mounted () {
    this.$nextTick(() => {
      this.getQueryData()
    })
  },
  methods: {
    downloadTemplate () {
      downloadFileLink(
        '/api-base/purchase/purchaseExchangeRate/importExcelTemplate',
        this.$t('dataConfMod.purchaseExchangeRateTemplate')
      ).catch(() => {
        // 下载失败
        this.$message.error(this.$t('components.eio.downloadFail'))
      })
    },
    handleSuccess () {
      this.getQuerydata()
    },
    /* 查询列表数据 */
    getQueryData (value) {
      this.queryParam = value
      this.$nextTick(() => {
        this.$refs.exchangeRateSettingTable.query()
      })
    },
    /* 新增汇率 */
    addExchangeRateSetting () {
      this.editRow = null
      this.dialogFormVisible = true
    },
    /* 编辑汇率 */
    editExchangeRateSetting (row) {
      this.editRow = row
      this.dialogFormVisible = true
    },
    /* 新增/编辑保存成功 */
    detailSubmitSuccess () {
      this.getQueryData()
    }
  }
}
</script>
