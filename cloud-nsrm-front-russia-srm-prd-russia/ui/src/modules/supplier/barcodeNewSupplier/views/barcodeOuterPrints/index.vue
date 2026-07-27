<template>
  <NavTabs
    ref="tabs"
    class="the-barcodeOuterPrints-detail"
    :tabs-list="tabs"
    :cur-tab="activeTab"
    @tab-change="tabChange"
    @tab-remove="tabRemove"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import barCodeOuterPrintByDelivery from './barCodeOuterPrintByDelivery'
import barCodeOuterPrintByMaterial from './barCodeOuterPrintByMaterial'
export default {
  name: 'BuyerDeliveryNotice',
  components: {
    NavTabs
  },
  data () {
    return {
      historyTabName: 'barCodeOuterPrintByDelivery',
      activeTab: 'barCodeOuterPrintByDelivery', // 当前激活标签  与name相同
      tabs: [
        {
          title: '按送货单',
          name: 'barCodeOuterPrintByDelivery',
          component: barCodeOuterPrintByDelivery,
          closable: false
        },
        {
          title: '按物料',
          name: 'barCodeOuterPrintByMaterial',
          component: barCodeOuterPrintByMaterial,
          closable: false
        }
      ]
    }
  },
  watch: {
    activeTab (_newVal, oldVal) {
      if (['barCodeOuterPrintByDelivery', 'barCodeOuterPrintByMaterial'].includes(oldVal)) {
        this.historyTabName = oldVal
      }
    }
  },
  methods: {
    tabChange (tab) {
      this.activeTab = tab
      this.tabs = this.$refs.tabs.tabs
    },
    tabRemove ({ activeTab }) {
      if (!['barCodeOuterPrintByDelivery', 'barCodeOuterPrintByMaterial'].includes(activeTab)) return
      this.$refs.tabs.activeTab = this.historyTabName
    }
  }
}
</script>

<style lang="scss">
.the-barcodeOuterPrints-detail {

  .render-page-container{
    padding: 0;
  }
  .render-page-container__body {
    padding: 0;

  }

  .vxe-cell--title {
    order: 1
  }

  .vxe-cell-help-icon {
    order: 2
  }

  .vxe-cell--sort {
    order: 3
  }
}

.the-barcodePrint {
  .el-dialog__body {
    height: calc(80vh - 150px);
    overflow-y: auto;
  }

  .render-pix-form-item-label {
    align-items: center;
  }

  .render-pix-form-item-feedback-layout-loose {
    margin-bottom: 20px !important;
  }
}
</style>
