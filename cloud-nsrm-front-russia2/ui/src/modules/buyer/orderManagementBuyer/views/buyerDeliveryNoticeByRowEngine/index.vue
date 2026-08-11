<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
    @tab-change="tabChange"
    @tab-remove="tabRemove"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import deliveryNoticeList from './deliveryNoticeList'
import deliveryNoticeDetailList from './deliveryNoticeDetailList'
export default {
  name: 'BuyerDeliveryNotice',
  components: {
    NavTabs
  },
  data () {
    return {
      historyTabName: 'deliveryNoticeList',
      activeTab: 'deliveryNoticeList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('orderMod.noticeBill'),
          name: 'deliveryNoticeList',
          component: deliveryNoticeList,
          closable: false
        },
        {
          title: this.$t('orderMod.noticeDetail'),
          name: 'deliveryNoticeDetailList',
          component: deliveryNoticeDetailList,
          closable: false
        }
      ]
    }
  },
  watch: {
    activeTab (_newVal, oldVal) {
      if (['deliveryNoticeList', 'deliveryNoticeDetailList'].includes(oldVal)) {
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
      if (!['deliveryNoticeList', 'deliveryNoticeDetailList'].includes(activeTab)) return
      this.$refs.tabs.activeTab = this.historyTabName
    }
  }
}
</script>
