<template>
  <NavTabs ref="tabs" :tabs-list="tabs" :cur-tab="activeTab" />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import productionApprovalList from './list'
export default {
    name: 'ProductionApproval',
    components: {
			NavTabs
    },
    data () {
			return {
				activeTab: 'productionApprovalList',
				tabs: [
					{
						title: this.$t('cusEntry.supplement20250211.productionListApproval'), // 页面名称  '量产清单认可'
						name: 'productionApprovalList',
						component: productionApprovalList,
						closable: false
					}
				]
			}
    }
}
</script>
