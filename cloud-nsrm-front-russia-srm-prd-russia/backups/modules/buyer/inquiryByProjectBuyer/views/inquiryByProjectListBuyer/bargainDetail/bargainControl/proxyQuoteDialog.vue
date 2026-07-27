<template>
  <srm-dialog
    :visible.sync="dialogVisible"
    :title="$t('bid_mod.proxyQuoteHandle')"
    size="xLarge"
    top="5vh"
    :close-on-click-modal="false"
    :content-max-height-limit="false"
  >
    <do-bargain-detail
      :proxy-quote-params="proxyQuoteParams"
      @proxyQuoteSuccess="proxyQuoteSuccess"
    />
  </srm-dialog>
</template>

<script>
/**
 * 代理报价
 */
import doBargainDetail from 'mods@/inquiryByProjectSupplier/views/inquiryByProjectListVendor/doBargainDetail'

export default {
  name: 'ProxyQuoteDialog',
  components: {
    doBargainDetail
  },
  props: {
    visible: {
      type: Boolean
    },
    proxyQuoteParams: {
      type: Object
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
