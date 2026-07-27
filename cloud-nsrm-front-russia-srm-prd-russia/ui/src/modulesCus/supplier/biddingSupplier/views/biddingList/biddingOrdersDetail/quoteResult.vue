<template>
  <el-container class="flex-container" direction="vertical">
    <el-main>
      <FormWrapper :form-array="formWrapperConfig" @getFormData="getQueryData" />

      <TableView
        ref="biddingResultTable"
        table-height="400px"
        :table-data="tableData"
        :table-header="tableHeader"
        :pre-query-data="queryParam"
        :com-active="$attrs['changeTab']"
        :url="tableViewUrl"
      />
    </el-main>
  </el-container>
</template>

<script>
/**
 * 投标结果
 */
import { bidSupplierHttp } from 'modcs@/biddingSupplier/api'
import { targetNumReveal } from 'lib@/composition/origin/composition'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import TableView from 'lib@/components/Table/TableView'

export default {
  name: 'QuoteResult',

  components: { FormWrapper, TableView },

  props: {
    isCurrentTab: {
      type: Boolean,
      required: true
    },
    projectId: {
      type: [Number, String],
      required: true
    }
  },

  data () {
    return {
      tableViewUrl: bidSupplierHttp.order.pageOrderResultUrl,
      tableData: [],
      tableHeader: [
        // 轮次
        {
          prop: 'round',
          label: this.$t('bidMod.bidingRound'),
          minWidth: 70
        },
        // 业务实体
        {
          prop: 'orgOuName',
          label: this.$t('bid_mod.businessEntity'),
          minWidth: 150
        },
        // 库存组织
        {
          prop: 'orgInvName',
          label: this.$t('bid_mod.inv'),
          minWidth: 150
        },
        // 物料编码
        {
          prop: 'itemCode',
          label: this.$t('bidMod.itemCode'),
          minWidth: 120,
          formattor: val => targetNumReveal(val)
        },
        // 物料名称
        {
          prop: 'itemDesc',
          label: this.$t('bidMod.itemDesc'),
          minWidth: 150
        },
        // 采购分类
        {
          prop: 'categoryName',
          label: this.$t('bidMod.purcategoryName'),
          minWidth: 150
        },
        // 组合
        {
          prop: 'itemGroup',
          label: this.$t('bidMod.itemGroup'),
          minWidth: 100
        },
        // 采购数量
        {
          prop: 'requireQuantity',
          label: this.$t('bid_mod.purQuantity'),
          minWidth: 100
        },
        // 单位
        {
          prop: 'unit',
          label: this.$t('bidMod.unit'),
          minWidth: 70,
          formattor: val => this.$getDictLabel('unit', val)
        },
        // 不含税中标价格
        {
          prop: 'standardNotaxPrice',
          label: this.$t('bidMod.bidPriceExcludingTax'),
          minWidth: 130
        },
        // 税率
        {
          prop: 'taxKey',
          label: this.$t('bidMod.taxRate'),
          minWidth: 100,
          formattor: val => this.$getDictLabel('tax', val)
        },
        // 中标供应商
        {
          prop: 'vendorName',
          label: this.$t('bidMod.wonBidVendor'),
          minWidth: 150
        },
        // 定价开始时间
        {
          prop: 'priceStartTime',
          label: this.$t('bidMod.priceStartTime'),
          minWidth: 150
        },
        // 定价结束时间
        {
          prop: 'priceEndTime',
          label: this.$t('bidMod.priceEndTime'),
          minWidth: 150
        },
        // 评选情况
        {
          prop: 'selectStatus',
          label: this.$t('bidMod.selectSituation'),
          minWidth: 100,
          formattor: val => this.$getDictLabel('SOU_SELECT_STATUS', val)
        },
        // 排名
        {
          prop: 'ranking',
          label: this.$t('bidMod.rank'),
          minWidth: 70
        },
        // 价格类型
        {
          prop: 'priceType',
          label: this.$t('bid_mod.priceType'),
          minWidth: 150,
          formattor: val => this.$getDictLabel('PRICE_TYPE', val)

        },
        // 备注
        {
          prop: 'remark',
          label: this.$t('bidMod.remark'),
          minWidth: 100
        }
      ],
      isGetDataStatus: false,
      formWrapperConfig: [
        // 物料名称
        { prop: 'itemDesc', label: () => this.$t('bidMod.itemName') },
        // 中标供应商名称
        { prop: 'vendorName', label: () => this.$t('bidMod.wonBidVendor') }
      ],
      queryParam: {}
    }
  },

  watch: {
    isCurrentTab: {
      handler (newValue, oldValue) {
        if (newValue && !oldValue && !this.isGetDataStatus) {
          this.isGetDataStatus = true
          this.getQueryData()
        }
      },
      immediate: true
    }
  },

  methods: {
    /* 查询列表数据 */
    getQueryData (val = {}) {
      this.queryParam = Object.assign({
        projectId: this.projectId
      }, val)
      this.$nextTick(() => {
        this.$refs.biddingResultTable.query()
      })
    }
  }
}
</script>
