<template>
  <el-container class="flex-container the-catalog-detail" direction="vertical">
    <el-main>
      <!-- 卡片详情 -->
      <price-detail-card
        :form-detail="catalogOnShelves"
        :hasImg="catalogOnShelvesAttaches.length > 0"
        :catalog-list="catalogOnShelvesAttaches"
      />
      <el-collapse v-model="activeDims" class="tab-form-style">
        <!-- 物料参数 -->
        <el-collapse-item :title="$t('dataConfMod.materialParams')" name="1">
          <srm-row style="display: flex;flex-wrap: wrap;">
            <srm-col v-for="item in materialParams" :key="item.key" class="detail-list-item">
              <span class="item-label">{{ item.label }}：</span><span class="item-text">{{ catalogOnShelves[item.key] }}</span>
            </srm-col>
          </srm-row>
        </el-collapse-item>
      </el-collapse>
    </el-main>

    <CToolbar>
      <template slot="right">
        <el-button @click="cancelBill">
          {{ $t('common.backTo') }}
        </el-button>
        <el-button type="primary" @click="addShoppingCart">
          {{ $t('common.addShoppingCart') }}
        </el-button>
      </template>
    </CToolbar>
  </el-container>
</template>

<script>
import CToolbar from 'lib@/components/c-toolbar'
import priceDetailCard from './components/priceDetailCard'
import { tabTodoMixin } from '@/utils/mixins'
import { purchaseCatalogApi } from 'modb@/oneStopShopping/api'

export default {
  name: 'PurchaseCatalogDetail',
  components: {
    CToolbar,
    priceDetailCard
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      activeDims: ['1'],
      catalogOnShelves: {},
      catalogOnShelvesAttaches: [],
      rowData: {},
      materialParams: [
        {
          label: this.$t('dataConfMod.deliveryCycle'),
          key: 'deliveryCycle'
        },
        {
          label: this.$t('dataConfMod.band'),
          key: 'brand'
        },
        {
          label: this.$t('dataConfMod.orderQuantityMinimum'),
          key: 'orderQuantityMinimum'
        },
        {
          label: this.$t('common.specification'),
          key: 'specification'
        },
        {
          label: this.$t('common.weight'),
          key: 'weight'
        },
        {
          label: this.$t('common.size'),
          key: 'size'
        },
        {
          label: this.$t('dataConfMod.ceeaColor'),
          key: 'color'
        }
      ]
    }
  },
  mounted () {
    this.rowData = this.$attrs.params.row
    this.queryDetails(this.rowData.catalogOnShelvesId)
  },
  methods: {
    async queryDetails (catalogOnShelvesId) {
      const { data } = await this.$http({
        url: '/api-sup-ce/pr/purchaseCatalog/getDetailPageForPurchaseCatalog',
        method: 'GET',
        params: { catalogOnShelvesId },
        loading: true
      })
      this.catalogOnShelves = data.catalogOnShelves
      this.catalogOnShelvesAttaches = data.catalogOnShelvesAttaches
    },
    // 加入购物车
    addShoppingCart () {
      if (!this.rowData.unitCode || !this.rowData.categoryCode) {
        return this.$message.warning(this.$t('oneStopShopping.addShoppingCartMsg1'))
      }
      if (!this.rowData.orgId || !this.rowData.organizationId) {
        return this.$message.warning(this.$t('oneStopShopping.addShoppingCartMsg2'))
      }
      purchaseCatalogApi.addToShoppingCart({ catalogOnShelvesId: this.rowData.catalogOnShelvesId }).then(res => {
        this.$message.success(this.$t('dataConfMod.addShoppingSuccess'))
        this.cancelBill()
      })
    },
    // 返回
    cancelBill () {
      this.$emit('tab-remove', this.$attrs.tabName)
      this.__setTabTodo('purchaseCatalogList.getQuerydata')
    }
  }
}
</script>

<style scoped lang="scss">
.the-catalog-detail {
  padding-bottom: 48px;
  ul,li {
    list-style: none;
    outline: none;
    padding: 0px;
    margin: 0px;
  }
  .item-label {
    font-size: 14px;
    color: #73777C;
    font-weight: 400;
    line-height: 14px;
  }
  .item-text {
    font-size: 14px;
    color: #161C24;
    font-weight: 400;
    line-height: 14px;
  }

  .detail-list-item {
    margin-bottom: 16px;
  }
}
</style>
