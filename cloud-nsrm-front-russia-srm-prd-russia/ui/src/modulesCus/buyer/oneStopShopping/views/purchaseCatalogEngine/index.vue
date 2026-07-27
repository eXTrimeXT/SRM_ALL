<template>
  <div class="body-bg">
    <NavTabs
      ref="tabs"
      :tabs-list="tabs"
      :cur-tab="activeTab"
    />
  </div>
</template>
<script>
  import NavTabs from 'lib@/components/NavTabs'
  import listEngine from './list-engine'
  export default {
    name: 'PurchaseCatalog',
    components: {
      NavTabs
    },
    data () {
      return {
        activeTab: 'purchaseCatalogList', // 当前激活标签  与name相同
        tabs: [{
          title: this.$t('route.purchaseDirectory'),
          name: 'purchaseCatalogList',
          component: listEngine,
          closable: false
        }]
      }
    },
    beforeCreate () {
        document.querySelector('body').setAttribute('style', 'background-color:#fffcf5;')
    }

  }
</script>
<style scoped lang="scss">

    .body-bg {
        position: absolute;
        width: 100%;
        height: 100%;
        top: 0;
        left: 0;
        overflow-y: auto;
        background-color: #fff;
    }
</style>
