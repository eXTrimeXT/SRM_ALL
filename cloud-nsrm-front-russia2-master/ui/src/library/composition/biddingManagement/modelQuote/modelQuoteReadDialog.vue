<template>
  <srm-dialog
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
        <model-quote
          :ref="`modelQuote-${index}`"
          :is-buyer="isBuyer"
          is-read-only-by-vendor
          is-read-only-by-buyer
          :lines-data="item.modelPriceLineTemplateList"
        />
      </el-collapse-item>
    </el-collapse>

    <template
      #footer
      class="dialog-footer"
    >
      <el-button @click="dialogVisible = false">
        {{ $t("common.cancel") }}
      </el-button>
    </template>
  </srm-dialog>
</template>

<script>
import { targetNumReveal } from 'lib@/composition/origin/composition'
import ModelQuote from './modelQuote'
import BaseTable from 'lib@/components/BaseTable'

export default {
  name: 'ModelQuoteReadDialog',
  components: { ModelQuote, BaseTable },
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
    isOnlyRead: {
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
  watch: {
    dialogVisible: {
      handler (value) {
        if (value) {
          this.queryModelQuote()
        }
      },
      immediate: true
    }
  },
  methods: {
    /* 查询该供应商模型报价 */
    async queryModelQuote () {
      if (!this.sourceLine.requirementLineId) return

      let params = {
        requirementLineId: this.sourceLine.requirementLineId
      }
      if (this.isBuyer || this.isOnlyRead) {
        params = {
          ...params,
          orderLineId: this.sourceLine.orderLineId
        }
      }
      const { data: responseData } = await this.$api.bid.modelQuote.getOrderModelPrices(params)
      this.modelQuoteLines = (responseData || []).map(item => {
        return {
          ...item,
          // 重置为统一的字段名
          modelPriceLineTemplateList: item.priceLineList || []
        }
      })
      this.modelQuoteCollapse = this.modelQuoteLines.map((i, index) => `collapseKey-${index}`)
    }
  }
}
</script>

<style lang="scss" scoped>
.card-tabs :deep(.el-tabs__content)  {
  padding-top: 10px;
}
</style>
