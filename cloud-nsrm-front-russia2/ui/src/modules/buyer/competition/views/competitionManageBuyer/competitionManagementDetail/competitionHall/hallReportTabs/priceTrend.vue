<template>
  <div class="price-trend-wrap">
    <p>
      <span style="margin-right: 8px">{{ $t('common.materialName') }}</span>

      <el-select v-model="filterItemSelect" @change="getQueryData">
        <el-option
          v-for="item in itemOptions"
          :key="item.souItemId"
          :label="item.itemDesc"
          :value="item.souItemId"
        />
      </el-select>
    </p>

    <VendorQoutePriceLineChart :vendor-price-nodes="currentItemPriceNodes" :item-desc="filterItemSelectName">
      <template #tips>
        <p />
      </template>
    </VendorQoutePriceLineChart>
  </div>
</template>

<script>
/**
 * 价格走势
 */
import { carBuyerHttp } from 'modb@/competition/api'
import VendorQoutePriceLineChart from 'lib@/composition/origin/vendorQoutePriceLineChart'
import { transformMQL } from 'lib@/utils/util'

export default {
  name: 'PriceTrend',

  components: { VendorQoutePriceLineChart },

  props: {
    itemOptions: {
      type: Array,
      required: true
    },
    // 是否当前tab页
    isActiveTab: {
      type: Boolean,
      default: false
    },
    baseInfo: {
      type: Object,
      default: () => {}
    },
    round: {
      type: Number
    }
  },

  data () {
    return {
      filterItemSelect: '',
      trendList: [],
      currentItemPriceNodes: null
    }
  },

  computed: {
    filterItemSelectName () {
      const item = this.itemOptions.find(item => item.souItemId === this.filterItemSelect)
      return item?.itemDesc || ''
    }
  },

  watch: {
    isActiveTab: {
      handler (newValue, oldValue) {
        // 切换到当前标签页
        if (newValue && !oldValue) {
          this.getQueryData()
        }
      },
      immediate: true
    }
  },

  methods: {
    /* 查询数据 */
    async getQueryData () {
      if (!this.itemOptions.length) {
        return
      }

      if (!this.filterItemSelect) {
        this.filterItemSelect = this.itemOptions[0].souItemId
      }

      this.currentItemPriceNodes = null

      let transformParams = transformMQL.save('AuctSouProjectForBuyer', [{
        souItemId: this.filterItemSelect,
        round: this.round || this.baseInfo.currentRound,
        projectId: this.baseInfo.projectId
      }], 'generatePriceReport')
      const response = await carBuyerHttp.order.getPriceCompareInfos(transformParams)

      if (response) {
        const { priceNodes = {} } = response.data.records[0] || {}
        console.log('priceNodes', priceNodes)
        this.currentItemPriceNodes = priceNodes[this.filterItemSelect] || {}
      }
    }
  }
}
</script>
