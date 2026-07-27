<template>
  <SrmDialog
    :visible.sync="dialogVisible"
    :title="$t('bid_mod.modelQuoteTitle')"
    :close-on-click-modal="false"
    append-to-body
    size="large"
  >
    <el-collapse v-model="modelQuoteCollapse">
      <el-collapse-item
        v-for="(item, index) in modelQuoteLines"
        :key="`collapseKey-${index}`"
        :name="`collapseKey-${index}`"
        :title="item.description"
      >
        <ModelQuote
          :ref="`modelQuote-${index}`"
          :is-buyer="isBuyer"
          is-read-only-by-vendor
          is-read-only-by-buyer
          :lines-data="item.modelPriceLineTemplateList"
        />
      </el-collapse-item>
    </el-collapse>

    <template #footer class="dialog-footer">
      <el-button @click="dialogVisible = false">
        {{ $t("common.cancel") }}
      </el-button>
    </template>
  </SrmDialog>
</template>

<script>
import ModelQuote from './modelQuote'

export default {
  name: 'ModelQuoteReadDialog',

  components: { ModelQuote },

  props: {
    visible: {
      type: Boolean
    },
    isProxyQuote: {
      type: Boolean
    },
    sourceLine: {
      type: Object
    },
    readonly: {
      type: Boolean
    }
  },

  data () {
    return {
      modelQuoteLines: [],
      modelQuoteCollapse: []
    }
  },

  computed: {
    dialogVisible: {
      get: function () {
        return this.visible
      },
      set: function (val) {
        this.$emit('update:visible', val)
      }
    },
    isBuyer () {
      if (this.isProxyQuote) {
        return false
      }
      return this.$store.getters.userInfo.userType === 'BUYER'
    }
  },

  mounted () {
    this.queryModelQuote()
  },

  methods: {
    /* 查询该供应商模型报价 */
    async queryModelQuote () {
      if (!this.sourceLine.requirementLineId) {
        return
      }

      let params = {
        requirementLineId: this.sourceLine.requirementLineId
      }
      if (this.isBuyer || this.readonly) {
        params = {
          ...params,
          orderLineId: this.sourceLine.orderLineId
        }
      }
      const { data: responseData } = await this.$api.brg.modelQuote.getOrderModelPrices(params)
      this.modelQuoteLines = (responseData || []).map(item => {
        return {
          ...item,
          // 重置为统一的字段名
          modelPriceLineTemplateList: item.priceLineList || []
        }
      })
      this.modelQuoteCollapse = this.modelQuoteLines.map((_item, index) => `collapseKey-${index}`)
    }
  }
}
</script>

<style lang="scss" scoped>
.card-tabs ::v-deep .el-tabs__content {
  padding-top: 10px;
}
</style>
