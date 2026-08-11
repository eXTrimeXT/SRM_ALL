<template>
  <SrmDialog
    :visible.sync="dialogVisible"
    :title="$t('bid_mod.proxyQuoteHandle')"
    size="xLarge"
    top="2vh"
    :close-on-click-modal="false"
    :content-max-height-limit="false"
  >
    <InquiryOrdersDetail
      :proxy-quote-params="proxyQuoteParams"
      @proxy-quote-success="proxyQuoteSuccess"
      @proxy-quote-close="dialogVisible = false"
    />
  </SrmDialog>
</template>

<script>
/**
 * 代理报价
 */
import InquiryOrdersDetail from '@/modules/supplier/inquirySupplier/views/inquiryOrders/inquiryOrdersDetail.vue'

export default {
  name: 'ProxyQuoteDialog',

  components: {
    InquiryOrdersDetail
  },

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
