<template>
  <srm-dialog
    v-if="dialogVisible"
    :visible="dialogVisible"
    :title="$t('bidMod.batchAddDeliverDate')"
    size="small"
    append-to-body
    :close-on-click-modal="false"
  >
    <el-date-picker
      v-model="batchDeliverDate"
      type="date"
      style="width: 100%;"
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
        @click="saveBatchDeliverDate"
      >
        {{ $t("common.submit") }}
      </el-button>
    </template>
  </srm-dialog>
</template>

<script>
/**
 * 批量维护承诺交货期
 */
export default {
  name: 'BatchMaintainDeliverDateDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      batchDeliverDate: null
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
    /* 批量维护承诺交货期 */
    saveBatchDeliverDate () {
      this.$emit('saveBatchDeliverDate', this.batchDeliverDate)
      this.batchDeliverDate = null
      this.dialogVisible = false
    }
  }
}
</script>
