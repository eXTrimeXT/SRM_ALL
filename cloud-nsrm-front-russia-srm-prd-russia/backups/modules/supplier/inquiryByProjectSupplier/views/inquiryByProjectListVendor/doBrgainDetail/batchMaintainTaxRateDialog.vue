<template>
  <srm-dialog
    v-if="dialogVisible"
    :visible="dialogVisible"
    :title="$t('bidMod.batchAddTaxRate')"
    size="small"
    append-to-body
    :close-on-click-modal="false"
  >
    <dict-select
      v-model="batchRateTax"
      code="tax"
      style="width: 100%;"
      @change-value="(value, dictItem) => taxKeyChange(value, dictItem)"
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
        @click="saveBatchTaxRate"
      >
        {{ $t("common.submit") }}
      </el-button>
    </template>
  </srm-dialog>
</template>

<script>
/**
 * 批量维护税率
 */
export default {
  name: 'BatchMaintainTaxRateDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      batchRateTax: null,
      taxDictItem: null
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
    /* 税率改变 */
    taxKeyChange (val, dictItem) {
      this.taxDictItem = dictItem
    },

    /* 批量维护税率 */
    saveBatchTaxRate () {
      this.$emit('saveBatchTaxRate', this.batchRateTax, this.taxDictItem)
      this.batchRateTax = null
      this.taxDictItem = null
      this.dialogVisible = false
    }
  }
}
</script>
