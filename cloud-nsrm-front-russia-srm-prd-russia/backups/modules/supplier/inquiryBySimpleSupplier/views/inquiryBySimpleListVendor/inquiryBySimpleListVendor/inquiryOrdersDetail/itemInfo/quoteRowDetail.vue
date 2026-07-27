<template>
  <el-form disabled>
    <SrmRow>
      <!--物料编码-->
      <SrmCol :init-col="4">
        <el-form-item :label="$t('bidMod.itemCode')">
          <el-input :value="detailData.itemCode" />
        </el-form-item>
      </SrmCol>

      <!--物料名称-->
      <SrmCol :init-col="4">
        <el-form-item :label="$t('bidMod.itemName')">
          <el-input :value="detailData.itemDesc" />
        </el-form-item>
      </SrmCol>

      <!--币种-->
      <SrmCol :init-col="4">
        <el-form-item :label="$t('bidMod.currency_price')">
          <el-input :value="$getDictLabel('currency', detailData.currency)" />
        </el-form-item>
      </SrmCol>

      <!--税率-->
      <SrmCol :init-col="4">
        <el-form-item :label="$t('bidMod.taxRate2')">
          <el-input :value="$getDictLabel('tax', detailData.taxKey)" />
        </el-form-item>
      </SrmCol>
    </SrmRow>
  </el-form>
</template>

<script>
/**
 * 报价模板的单据详情
 */
export default {
  name: 'QuoteRowDetail',

  props: {
    detailData: {
      type: Object,
      required: true,
      default: () => ({})
    }
  }
}
</script>
