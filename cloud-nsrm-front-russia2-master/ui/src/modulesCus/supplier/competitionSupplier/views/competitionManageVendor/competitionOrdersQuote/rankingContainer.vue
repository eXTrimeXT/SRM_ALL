<template>
  <SrmRow class="ranking-container">
    <!--单价实时排名-->
    <SrmCol :init-col="2">
      <div class="header-toolbar">
        <div class="space-between-left">
          <div class="label">
            <!-- 单价实时排名 -->
            {{ $t("competition.realTimeRankingOfPrice") }}
          </div>
          <div class="search-wrap">
            <!--查询条件-->
            <span class="select-label">
              <!-- 物料名称 -->
              {{ $t("common.materialName") }}
            </span>
            <el-select v-model="priceRankingItemData" @change="priceRankingItemChange">
              <el-option
                v-for="item in itemOptions"
                :key="item.souItemId"
                :label="item.itemDesc"
                :value="item.souItemId"
              />
            </el-select>
          </div>
        </div>
        <div class="space-between-right">
          <el-button
            v-if="priceTableData.length > 5"
            type="text"
            @click="openDialog('PriceRanking', priceTableData)"
          >
            <!-- 查看全部排名 -->
            {{ $t("cusEntry.supplement20250205.viewAllRankings") }}
          </el-button>
        </div>
      </div>

      <PriceRanking :base-info="baseInfo" :table-data="priceTableData.slice(0, 5)" />
    </SrmCol>

    <!--总价实时排名-->
    <SrmCol :init-col="2">
      <div class="header-toolbar">
        <div class="space-between-left">
          <div class="label">
            <!-- 总价实时排名 -->
            {{ $t("cusEntry.supplement20250205.totalPriceRealTimeRanking") }}
          </div>
        </div>
        <div class="space-between-right">
          <el-button
            v-if="totalPriceTableData.length > 5"
            type="text"
            @click="openDialog('TotalPriceRanking', totalPriceTableData)"
          >
            <!-- 查看全部排名 -->
            {{ $t("cusEntry.supplement20250205.viewAllRankings") }}
          </el-button>
        </div>
      </div>

      <TotalPriceRanking :base-info="baseInfo" :table-data="totalPriceTableData.slice(0, 5)" />
    </SrmCol>

    <!--查看全部排名弹窗-->
    <SrmDialog
      size="large"
      :title="$t('cusEntry.supplement20250205.allRankings')"
      :visible.sync="dialogVisible"
      append-to-body
    >
      <component
        :is="dialogComponent"
        :base-info="baseInfo"
        :table-data="dialogTableData"
      />

      <div slot="footer">
        <!--取消-->
        <el-button @click="dialogVisible = false">
          {{ $t('common.cancel') }}
        </el-button>
      </div>
    </SrmDialog>
  </SrmRow>
</template>

<script>
/**
 * 排名
 */
import PriceRanking from './rankingContainer/priceRanking.vue'
import TotalPriceRanking from './rankingContainer/totalPriceRanking.vue'

export default {
  name: 'RankingContainer',

  components: {
    PriceRanking,
    TotalPriceRanking
  },

  props: {
    itemList: {
      type: Array,
      required: true
    },
    rankingData: {
      type: Object,
      required: true,
      default: () => {
        return {
          price: [],
          totalPrice: []
        }
      }
    },
    priceRankingItem: {
      type: [String, Number],
      required: true
    },
    baseInfo: {
      type: Object,
      required: true
    }
  },

  data () {
    return {
      dialogComponent: '',
      dialogVisible: false,
      dialogTableData: [],
      desensitization: 'xxx'
    }
  },

  computed: {
    priceRankingItemData: {
      get: function () {
        return this.priceRankingItem
      },
      set: function (value) {
        this.$emit('update:priceRankingItem', value)
      }
    },

    itemOptions () {
      return this.itemList.map(item => {
        return {
          itemId: item.itemId,
          souItemId: item.souItemId,
          itemCode: item.itemCode,
          itemDesc: item.itemDesc
        }
      })
    },

    // 单价实时排名
    priceTableData () {
      // 把null转化成xxx
      const list = JSON.parse(JSON.stringify(this.rankingData?.price || []))
      return list.map(item => {
        Object.keys(item).forEach(key => {
          item[key] = !item[key] && item[key] !== 0 ? this.desensitization : item[key]
        })
        return item
      })
    },

    // 总价实时排名
    totalPriceTableData () {
      // 把null转化成xxx
      const list = JSON.parse(JSON.stringify(this.rankingData?.totalPrice || []))
      return list.map(item => {
        Object.keys(item).forEach(key => {
          item[key] = !item[key] && item[key] !== 0 ? this.desensitization : item[key]
        })
        return item
      })
    }
  },

  methods: {
    /* 物料改变  */
    priceRankingItemChange () {
      this.$emit('query-ranking')
    },

    /* 打开查看全部排名弹窗 */
    openDialog (name, data) {
      this.dialogComponent = name
      this.dialogTableData = data
      this.dialogVisible = true
    }
  }
}
</script>

<style lang="scss" scoped>
.header-toolbar {
  display: flex;
  justify-content: space-between;
  padding: 15px 0;
  .space-between-left {
    flex: 1;
    display: flex;
    .label {
      font-size: 15px;
      font-weight: 500;
      height: 28px;
      line-height: 28px;
    }
    .search-wrap {
      margin-left: 10px;
      .select-label {
        margin-right: 6px;
        height: 28px;
        line-height: 28px;
      }
    }
  }

  .space-between-right {
    width: 90px;
  }
}
</style>
