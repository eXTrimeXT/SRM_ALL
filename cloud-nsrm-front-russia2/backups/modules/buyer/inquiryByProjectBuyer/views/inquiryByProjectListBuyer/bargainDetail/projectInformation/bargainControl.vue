<template>
  <div class="the_check_groups">
    <!-- 投标控制 -->
    <h3>{{ $t("bidMod.priceControl") }}</h3>
    <srm-row>
      <!--允许供应商撤回投标-->
      <srm-col :init-col="3">
        <el-form-item prop="withdrawBargain">
          <el-checkbox
            v-model="bargainBaseInfo.withdrawBargain"
            true-label="Y"
            false-label="N"
          >
            {{ $t("bidMod.withdrawBiding1") }}
          </el-checkbox>
        </el-form-item>
      </srm-col>

      <!--允许供应商只对部分商品（组合）报价-->
      <srm-col :init-col="3">
        <el-form-item prop="partPrice">
          <el-checkbox
            v-model="bargainBaseInfo.partPrice"
            true-label="Y"
            false-label="N"
          >
            {{ $t("bidMod.partPrice") }}
          </el-checkbox>
        </el-form-item>
      </srm-col>
    </srm-row>

    <srm-row>
      <!--向供应商公开上轮最低价-->
      <srm-col :init-col="3">
        <el-form-item prop="publicLowestPrice">
          <el-checkbox
            v-model="bargainBaseInfo.publicLowestPrice"
            true-label="Y"
            false-label="N"
          >
            {{ $t("bidMod.publicLowestPrice") }}
          </el-checkbox>
        </el-form-item>
      </srm-col>

      <!--向供应商公开上轮排名结果-->
      <srm-col :init-col="3">
        <el-form-item prop="publicTotalRank">
          <el-checkbox
            v-model="bargainBaseInfo.publicTotalRank"
            true-label="Y"
            false-label="N"
          >
            {{ $t("bidMod.publicTotalRank") }}
          </el-checkbox>
        </el-form-item>
      </srm-col>
    </srm-row>

    <srm-row>
      <!--是否密封报价-->
      <srm-col :init-col="3">
        <el-form-item prop="needEncryptPrice">
          <el-checkbox
            v-model="bargainBaseInfo.needEncryptPrice"
            true-label="Y"
            false-label="N"
            @change="needEncryptPriceChange"
          >
            是否密封报价
          </el-checkbox>
        </el-form-item>
      </srm-col>
    </srm-row>

    <!-- 开标控制 -->
    <h3>{{ $t("bidMod.bidOpenControl") }}</h3>
    <srm-row>
      <!--允许供应商查看最终排名结果-->
      <srm-col :init-col="3">
        <el-form-item prop="visibleRankResult">
          <el-checkbox
            v-model="bargainBaseInfo.visibleRankResult"
            true-label="Y"
            false-label="N"
          >
            {{ $t("bidMod.visibleCodeRanking") }}
          </el-checkbox>
        </el-form-item>
      </srm-col>

      <!--允许供应商查看中标价-->
      <srm-col :init-col="3">
        <el-form-item prop="visibleFinalPrice">
          <el-checkbox
            v-model="bargainBaseInfo.visibleFinalPrice"
            true-label="Y"
            false-label="N"
          >
            {{ $t("bidMod.visibleLowestPrice") }}
          </el-checkbox>
        </el-form-item>
      </srm-col>

      <!--允许供应商查看中标供应商-->
      <srm-col :init-col="3">
        <el-form-item prop="visibleWinVendor">
          <el-checkbox
            v-model="bargainBaseInfo.visibleWinVendor"
            true-label="Y"
            false-label="N"
          >
            {{ $t("bidMod.visibleWinVendor") }}
          </el-checkbox>
        </el-form-item>
      </srm-col>
    </srm-row>
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
      type: Object
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
    needEncryptPriceChange (val) {
      if (val === 'Y') {
        this.$emit('needEncryptPriceChange', true)
      }
    }
  }
}
</script>
