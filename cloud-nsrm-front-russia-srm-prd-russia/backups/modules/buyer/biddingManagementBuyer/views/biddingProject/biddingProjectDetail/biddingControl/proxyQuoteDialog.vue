<template>
  <srm-dialog
    :visible.sync="dialogVisible"
    :title="$t('bid_mod.proxyQuoteHandle')"
    size="xLarge"
    top="2vh"
    :close-on-click-modal="false"
    :content-max-height-limit="false"
  >
    <do-biding-detail
      :proxy-quote-params="proxyQuoteParams"
      @proxyQuoteSuccess="proxyQuoteSuccess"
    />
  </srm-dialog>
</template>

<script>
/**
 * 代理报价
 */
import doBidingDetail from 'mods@/biddingManagementSupplier/views/vendorBiddingList/doBidingDetail'

export default {
  name: 'ProxyQuoteDialog',
  components: {
    doBidingDetail
  },
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    proxyQuoteParams: {
      type: Object,
      required: false
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
      this.$emit('proxyQuoteSuccess')
    }
  }
}
</script>
