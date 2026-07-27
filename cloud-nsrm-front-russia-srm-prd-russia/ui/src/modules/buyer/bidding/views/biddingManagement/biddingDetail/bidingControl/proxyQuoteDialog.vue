<template>
  <SrmDialog
    :visible.sync="dialogVisible"
    :title="$t('bid_mod.proxyQuoteHandle')"
    size="xLarge"
    top="5vh"
    :close-on-click-modal="false"
    :content-max-height-limit="false"
  >
    <BiddingOrdersQuote :proxy-quote-params="proxyQuoteParams" @proxy-quote-success="proxyQuoteSuccess" />
  </SrmDialog>
</template>

<script>
/**
 * 代理报价
 */
import BiddingOrdersQuote from 'mods@/biddingSupplier/views/biddingOrders/biddingOrdersQuote.vue'

export default {
  name: 'ProxyQuoteDialog',

  components: { BiddingOrdersQuote },

  props: {
    visible: {
      type: Boolean,
      required: true
    },
    proxyQuoteParams: {
      type: Object,
      required: true
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
    proxyQuoteSuccess () {
      this.dialogVisible = false
      this.$emit('success')
    }
  }
}
</script>
