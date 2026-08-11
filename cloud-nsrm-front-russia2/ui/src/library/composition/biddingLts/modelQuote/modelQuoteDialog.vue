<template>
  <srm-dialog
    :visible.sync="dialogVisible"
    :title="$t('bid_mod.modelQuoteTitle')"
    :close-on-click-modal="false"
    append-to-body
    size="large"
  >
    <div
      v-if="isBuyer && !readonly && !readonly"
      style="margin-bottom: 10px;"
    >
      <!--新增费用类-->
      <el-button
        type="primary"
        @click="createCostClass"
      >
        {{ $t("bid_mod.createCostClass") }}
      </el-button>
    </div>

    <el-collapse
      v-model="modelQuoteCollapse"
      class="model-quote-collapse"
      style="min-height: 100px;"
    >
      <el-collapse-item
        v-for="(item, index) in modelQuoteLines"
        :key="`collapseKey-${index}`"
        :name="`collapseKey-${index}`"
        :title="item.description"
      >
        <ModelQuote
          :ref="`modelQuote-${index}`"
          :is-buyer="isBuyer"
          :is-read-only-by-vendor="isReadOnlyByVendor"
          :is-read-only-by-buyer="readonly"
          :lines-data="item.modelPriceLineTemplateList"
          @deleteCostClass="deleteCostClass(index)"
        />
      </el-collapse-item>
    </el-collapse>
    <template
      #footer
      class="dialog-footer"
    >
      <!--取消-->
      <el-button @click="dialogVisible = false">
        {{ $t("common.cancel") }}
      </el-button>
      <!--提交-->
      <el-button
        v-if="!readonly && !readonly"
        type="primary"
        @click="save"
      >
        {{ $t("common.confirm") }}
      </el-button>
    </template>
  </srm-dialog>
</template>

<script>
/**
 * 招标模块模型报价
 */
import ModelQuote from './modelQuote'

export default {
  name: 'ModelQuoteDialog',
  components: { ModelQuote },
  props: {
    visible: {
      type: Boolean
    },
    // 代理报价
    isProxyQuote: {
      type: Boolean
    },
    // 入参可包含modelQuoteLines，requirementLineId，orderLineId
    sourceLine: {
      type: Object
    },
    // 供应商禁用
    isReadOnlyByVendor: {
      type: Boolean,
      default: false
    },
    // 采购商禁用
    readonly: {
      type: Boolean,
      default: false
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
        if (value && this.sourceLine) {
          if (this.sourceLine.modelQuoteLines) {
            // 存在旧的，用旧的，如果要清空就传空数组
            this.modelQuoteLines = this.sourceLine.modelQuoteLines
            this.modelQuoteCollapse = (this.sourceLine.modelQuoteLines).map((i, index) => `collapseKey-${index}`)
          } else if (this.sourceLine.requirementLineId || (this.isBuyer && this.sourceLine.orderLineId)) {
            // 需要查询， 传查询条件ID
            this.queryModelQuote()
          }
        }
      },
      immediate: true
    }
  },
  methods: {
    /* 查询模型报价详情 */
    async queryModelQuote () {
      let params = {
        requirementLineId: this.sourceLine.requirementLineId
      }
      if (this.isBuyer || this.isReadOnlyByVendor) {
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
      this.modelQuoteCollapse = this.modelQuoteLines.map((i, index) => `collapseKey-${index}`)
    },

    /* 删除费用类 */
    deleteCostClass (index) {
      this.modelQuoteLines.splice(index, 1)
    },

    /* 新增费用类 */
    createCostClass () {
      this.$prompt(
        // 请输入费用类名称
        this.$t('bid_mod.createCostClassDesc'),
        // 新增费用类
        this.$t('bid_mod.createCostClass'),
        {
          confirmButtonText: this.$t('common.confirm'),
          cancelButtonText: this.$t('common.cancel')
        }
      ).then(({ value }) => {
        if (this.modelQuoteLines.find(item => item.description === value.trim())) {
          this.$message.warning(this.$t('bidMod.biddingManagementSupplier.descriptionRepeat'))
          return
        }
        this.modelQuoteLines.push({
          description: value.trim(),
          modelPriceLineTemplateList: []
        })
        this.modelQuoteCollapse.push(`collapseKey-${this.modelQuoteLines.length - 1}`)
      })
    },

    /* 提交 */
    save () {
      const emitData = this.modelQuoteLines.map((item, index) => {
        return {
          ...item,
          // 获取各个类的表格数据
          modelPriceLineTemplateList: this.$refs[`modelQuote-${index}`][0].getModelQuoteLinesData()
        }
      })
      if (this.isBuyer) {
        // 采购商
        // 请至少添加一个费用类，费用类中至少需要一个费用项
        if (this.modelQuoteLines.length === 0) {
          this.$message.warning(this.$t('bidMod.biddingManagementSupplier.modelQuoteLinesRequired'))
          return
        }
        for (let item of emitData) {
          if (item.modelPriceLineTemplateList.length === 0) {
            // 维护了业务实体，库存组织必须维护！
            this.$message.warning(`${item.description} ${this.$t('bidMod.biddingManagementSupplier.modelQuoteLinesItemRequired')}`)
            return
          }
        }
      } else {
        // 供应商
        for (let item of emitData) {
          for (const [listIndex, listItem] of new Map(item.modelPriceLineTemplateList.map((listItem, listIndex) => [listIndex, listItem]))) {
            if (!listItem.notaxPrice) {
              // 未税单价
              this.$message.warning(`${item.description} ${this.$t('bidMod.biddingManagementSupplier.modelQuoteLinesItemNotaxPrice', [listIndex + 1])}`)
              return
            }
            if (!listItem.taxKey) {
              // 税率
              this.$message.warning(`${item.description} ${this.$t('bidMod.biddingManagementSupplier.modelQuoteLinesItemTaxKey', [listIndex + 1])}`)
              return
            }
          }
        }
      }
      this.$emit('saveModelQuoteData', emitData)
      this.dialogVisible = false
    }
  }
}
</script>

<style lang="scss" scoped>
.model-quote-collapse {
  min-height: 100px
}
</style>
