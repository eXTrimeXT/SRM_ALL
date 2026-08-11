<template>
  <srm-dialog
    v-if="dialogVisible"
    :visible="dialogVisible"
    :title="$t('bidMod.batchAddCurrency')"
    size="small"
    append-to-body
    :close-on-click-modal="false"
  >
    <dict-select
      v-model="batchCurrencyType"
      code="currency"
      :transform-options="transformCurrencyOptions"
      style="width: 100%"
    />
    <template
      #footer
      class="dialog-footer"
    >
      <el-button @click="dialogVisible = false">
        {{ $t("common.cancel") }}
      </el-button>
      <el-button
        type="primary"
        @click="saveBatchCurrency"
      >
        {{ $t("common.submit") }}
      </el-button>
    </template>
  </srm-dialog>
</template>

<script>
/**
 * 批量维护币种
 */
export default {
  name: 'BatchMaintainCurrencyDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    currencyList: {
      type: Array
    }
  },
  data () {
    return {
      batchCurrencyType: null
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
    }
  },
  methods: {
    /* 编排文件类型 */
    transformCurrencyOptions (options) {
      // 过滤存在币种列表的可用货币
      return options.filter(item => {
        return this.currencyList.find(itemC => itemC.currencyCode === item.value)
      })
    },

    /* 保存 */
    saveBatchCurrency () {
      this.$emit('saveBatchCurrency', this.batchCurrencyType)
      this.batchCurrencyType = null
      this.dialogVisible = false
    }
  }
}
</script>
