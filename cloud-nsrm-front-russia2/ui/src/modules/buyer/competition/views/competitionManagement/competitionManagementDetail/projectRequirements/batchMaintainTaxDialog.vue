<template>
  <SrmDialog
    :visible="dialogVisible"
    :title="$t('bidMod.batchAddTaxRate')"
    append-to-body
    size="small"
    :close-on-click-modal="false"
  >
    <DictSelect
      v-model="batchRateTax"
      code="tax"
      @change-value="taxChange"
    />

    <template #footer class="dialog-footer">
      <el-button @click="dialogVisible = false">
        {{ $t('common.cancel') }}
      </el-button>

      <el-button type="primary" @click="save">
        {{ $t('common.save') }}
      </el-button>
    </template>
  </SrmDialog>
</template>

<script>
/**
 * 批量维护税率
 */

export default {
  name: 'BatchMaintainTaxDialog',

  props: {
    visible: {
      type: Boolean,
      required: true
    }
  },

  data () {
    return {
      batchRateTax: '',
      batchRateTaxValue: ''
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
    /* 选择币种 */
    taxChange (value, dictItem) {
      this.batchRateTaxValue = value ? dictItem.key : ''
    },

    /* 确定 */
    save () {
      if (!this.batchRateTax) {
        // '请选择税率'
        this.$message.warning(this.$t('bidMod.selectTaxRate'))
        return
      }
      this.dialogVisible = false

      this.$emit('save', {
        taxKey: this.batchRateTax,
        taxRate: this.batchRateTaxValue
      })
    }
  }
}
</script>
