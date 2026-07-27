<template>
  <NavTabs ref="tabs" :tabs-list="tabs" :cur-tab="activeTab" @tab-change="tabChange" />
</template>
<script>
    import NavTabs from 'lib@/components/NavTabs'
    import salePlanList from './list'
    export default {
        name: 'SalePlan',
        components: {
            NavTabs
        },
        data () {
            return {
                activeTab: 'salePlanList',
                tabs: [
                    {
                        title: '产销计划管理', // 页面名称
                        name: 'salePlanList',
                        component: salePlanList,
                        closable: false
                    }
                ]

            }
        },
        mounted () {

        },
        activated () {
            console.log('[activated]')
            if (this.activeTab === 'salePlanItem') {
                this.dolayout()
            }
        },
        methods: {
            dolayout () {
                this.$nextTick(() => {
                    const data = {
                        name: 'salePlanItem', // 组件名
                        methods: 'dolayout', // 方法名
                        params: null, // 参数
                        random: Math.random()
                    }
                    console.log('[SET_NAV_TABS_TODO]', data)
                    this.$store.commit('SET_NAV_TABS_TODO', data)
                })
            },
            tabChange (tab) {
                if (tab === 'salePlanItem') {
                    this.dolayout()
                }
                console.log('[tab]', tab)
                this.activeTab = tab
            }
        }
    }
</script>
