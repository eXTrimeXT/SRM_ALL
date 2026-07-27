<template>
  <div class="biding-price-control">
    <SrmRow>
      <!--允许供应商撤回报价-->
      <SrmCol :init-col="3">
        <el-checkbox
          v-model="headerData.allowWithdraw"
          true-label="Y"
          false-label="N"
          disabled
        >
          {{ $t("bidMod.withdrawBiding1") }}
        </el-checkbox>
      </SrmCol>
      <!--密封报价-->
      <SrmCol :init-col="3">
        <el-checkbox
          v-model="headerData.needEncryptPrice"
          true-label="Y"
          false-label="N"
          disabled
          @change="needEncryptPriceChange"
        >
          {{ $t('bidMod.needEncryptPrice') }}
        </el-checkbox>
      </SrmCol>
    </SrmRow>
  </div>
</template>

<script>
/**
 * 投标控制
 */
export default {
  name: 'BidingControl',

  props: {
    header: {
      type: Object,
      default: () => { /* nothing */ }
    }
  },

  computed: {
    headerData: {
      get: function () {
        return this.header
      },
      set: function (val) {
        this.$emit('update:header', val)
      }
    }
  },
  methods: {
    needEncryptPriceChange (value) {
      if (value === 'Y') {
        this.headerData.allowProxyOrder = 'N'
      }
    },
    allowProxyOrderChange (value) {
      if (value === 'Y') {
        this.headerData.needEncryptPrice = 'N'
      }
    }
  }
}
</script>
