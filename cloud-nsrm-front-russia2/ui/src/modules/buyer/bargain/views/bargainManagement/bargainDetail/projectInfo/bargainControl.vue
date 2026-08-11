<template>
  <div class="the_check_groups">
    <!-- 投标控制 -->
    <h3>{{ $t("bidMod.priceControl") }}</h3>
    <SrmRow>
      <!--允许供应商撤回投标-->
      <SrmCol :init-col="3">
        <el-form-item prop="allowWithdraw">
          <el-checkbox
            v-model="bargainBaseInfo.allowWithdraw"
            true-label="Y"
            false-label="N"
          >
            {{ $t("bidMod.withdrawBiding1") }}
          </el-checkbox>
        </el-form-item>
      </SrmCol>

      <!--允许供应商只对部分商品（组合）报价-->
      <SrmCol :init-col="3">
        <el-form-item prop="allowPartPrice">
          <el-checkbox
            v-model="bargainBaseInfo.allowPartPrice"
            true-label="Y"
            false-label="N"
          >
            {{ $t("bidMod.partPrice") }}
          </el-checkbox>
        </el-form-item>
      </SrmCol>

      <!--是否密封报价-->
      <SrmCol :init-col="3">
        <el-form-item prop="needEncryptPrice">
          <el-checkbox
            v-model="bargainBaseInfo.needEncryptPrice"
            true-label="Y"
            false-label="N"
            @change="needEncryptPriceChange"
          >
            <!-- 是否密封报价 -->
            {{ $t("other.whetherToSealTheQuotation") }}
          </el-checkbox>
        </el-form-item>
      </SrmCol>
    </SrmRow>

    <SrmRow>
      <!--向供应商公开上轮最低价-->
      <SrmCol :init-col="3">
        <el-form-item prop="publicLowestPrice">
          <el-checkbox
            v-model="bargainBaseInfo.publicLowestPrice"
            true-label="Y"
            false-label="N"
          >
            {{ $t("bidMod.publicLowestPrice") }}
          </el-checkbox>
        </el-form-item>
      </SrmCol>

      <!--向供应商公开上轮排名结果-->
      <SrmCol :init-col="3">
        <el-form-item prop="publicTotalRank">
          <el-checkbox
            v-model="bargainBaseInfo.publicTotalRank"
            true-label="Y"
            false-label="N"
          >
            {{ $t("bidMod.publicTotalRank") }}
          </el-checkbox>
        </el-form-item>
      </SrmCol>
    </SrmRow>

    <!-- 开标控制 -->
    <h3>{{ $t("bidMod.bidOpenControl") }}</h3>
    <SrmRow>
      <!--允许供应商查看最终排名结果-->
      <SrmCol :init-col="3">
        <el-form-item prop="visibleRankResult">
          <el-checkbox
            v-model="bargainBaseInfo.visibleRankResult"
            true-label="Y"
            false-label="N"
          >
            {{ $t("bidMod.visibleCodeRanking") }}
          </el-checkbox>
        </el-form-item>
      </SrmCol>

      <!--允许供应商查看中标价-->
      <SrmCol :init-col="3">
        <el-form-item prop="visibleFinalPrice">
          <el-checkbox
            v-model="bargainBaseInfo.visibleFinalPrice"
            true-label="Y"
            false-label="N"
          >
            {{ $t("bidMod.visibleLowestPrice") }}
          </el-checkbox>
        </el-form-item>
      </SrmCol>

      <!--允许供应商查看中标供应商-->
      <SrmCol :init-col="3">
        <el-form-item prop="visibleWinVendor">
          <el-checkbox
            v-model="bargainBaseInfo.visibleWinVendor"
            true-label="Y"
            false-label="N"
          >
            {{ $t("bidMod.visibleWinVendor") }}
          </el-checkbox>
        </el-form-item>
      </SrmCol>
    </SrmRow>
  </div>
</template>

<script>
/**
 * 报价控制
 */
export default {
  name: 'BargainControl',

  props: {
    bargainBase: {
      type: Object,
      required: true
    }
  },

  computed: {
    bargainBaseInfo: {
      get: function () {
        return this.bargainBase
      },
      set: function (val) {
        this.$emit('update:bargainBase', val)
      }
    }
  },

  methods: {
    /* 是否密封报价 */
    needEncryptPriceChange () {
      this.$emit('need-encrypt-price-change', true)
    }
  }
}
</script>
