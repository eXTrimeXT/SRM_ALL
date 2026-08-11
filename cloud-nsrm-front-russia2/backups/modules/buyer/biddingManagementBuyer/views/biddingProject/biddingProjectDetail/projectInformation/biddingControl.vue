<template>
  <div class="the_check_groups">
    <!-- 投标控制 -->
    <h3>{{ $t("bidMod.bidingControl") }}</h3>
    <srm-row>
      <!--允许供应商撤回投标-->
      <srm-col :init-col="3">
        <el-form-item prop="withdrawBiding">
          <el-checkbox
            v-model="bidingBaseInfo.withdrawBiding"
            true-label="Y"
            false-label="N"
          >
            {{ $t("bidMod.withdrawBiding") }}
          </el-checkbox>
        </el-form-item>
      </srm-col>

      <!--允许供应商只对部分商品（组合）报价-->
      <srm-col :init-col="3">
        <el-form-item prop="partPrice">
          <el-checkbox
            v-model="bidingBaseInfo.partPrice"
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
            v-model="bidingBaseInfo.publicLowestPrice"
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
            v-model="bidingBaseInfo.publicTotalRank"
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
            v-model="bidingBaseInfo.needEncryptPrice"
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
            v-model="bidingBaseInfo.visibleRankResult"
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
            v-model="bidingBaseInfo.visibleFinalPrice"
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
            v-model="bidingBaseInfo.visibleWinVendor"
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
 * 投标控制
 */
export default {
  name: 'BidingControl',
  props: {
    bidingBase: {
      type: Object
    }
  },
  computed: {
    bidingBaseInfo: {
      get: function () {
        return this.bidingBase
      },
      set: function (val) {
        this.$emit('update:bidingBase', val)
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
