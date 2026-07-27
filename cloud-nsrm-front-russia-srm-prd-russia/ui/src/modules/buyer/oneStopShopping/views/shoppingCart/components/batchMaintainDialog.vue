<template>
  <!-- 批量维护 -->
  <srm-dialog
    :visible.sync="visibleDialog"
    :title="$t('vendorMod.batchMaintain')"
    size="small"
    destroy-on-close
    :close-on-click-modal="false"
    v-bind="$attrs"
    v-on="$listeners"
  >
    <el-form
      ref="pubForm"
      :model="sumFormBatch"
      :rules="sumRules"
    >
      <el-form-item
        prop="beginQuote"
        :label="$t('purchaseDemand.purchaseType')"
      >
        <DictSelect
          v-model="sumFormBatch.purchaseType"
          code="PURCHASE_TYPE"
        />
      </el-form-item>
      <el-form-item
        prop="deadline"
        :label="$t('purchaseDemand.requirementDate')"
      >
        <el-date-picker
          v-model="sumFormBatch.requirementDate"
          value-format="yyyy-MM-dd"
          type="date"
        />
      </el-form-item>
    </el-form>
    <div
      slot="footer"
      class="dialog-footer"
    >
      <el-button
        @click="visibleDialog = false"
      >
        {{
          $t("common.cancel")
        }}
      </el-button>
      <el-button
        type="primary"
        @click="comfirmBatch"
      >
        {{
          $t("common.confirm")
        }}
      </el-button>
    </div>
  </srm-dialog>
</template>
<script>
export default {
  name: 'NoticeUserDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      visibleDialog: false,
      sumFormBatch: {
        purchaseType: '',
        requirementDate: ''
      },
      sumRules: {}
    }
  },
  watch: {
    visible (sign) {
      this.visibleDialog = sign
      if (sign) {
        this.sumFormBatch = {}
      }
    }
  },
  methods: {
    comfirmBatch () {
      this.$emit('comfirmBatch', this.sumFormBatch)
    }
  }
}
</script>
