<template>
  <SrmDialog
    :visible="dialogVisible"
    :title="$t('bidMod.batchAddCurrency')"
    append-to-body
    size="small"
    :close-on-click-modal="false"
  >
    <DictSelect
      v-model="batchCurrencyType"
      code="currency"
      :transform-options="transformOptions"
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
 * batchMaintainCurrencyDialog
 */
export default {
  name: 'BatchMaintainCurrencyDialog',

  props: {
    visible: {
      type: Boolean,
      required: true
    },
    transformOptions: {
      type: Function,
      required: true
    }
  },

  data () {
    return {
      batchCurrencyType: ''
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
    save () {
      if (!this.batchCurrencyType) {
        // '请选择币种'
        this.$message.warning(this.$t('vendorMod.msgCurrencyCode'))
        return
      }

      this.$emit('save', this.batchCurrencyType)
      this.dialogVisible = false
    }
  }
}
</script>
