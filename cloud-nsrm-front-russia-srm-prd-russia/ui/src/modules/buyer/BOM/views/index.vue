<template>
  <NavTabs ref="tabs" :tabs-list="tabs" :cur-tab="activeTab" @tab-change="tabChange" />
</template>
<script>
    import NavTabs from 'lib@/components/NavTabs'
    import BOMList from './list'
    export default {
        name: 'BOM',
        components: {
            NavTabs
        },
        data () {
            return {
                activeTab: 'BOMList',
                tabs: [
                    {
                        title: 'BOM', // 页面名称
                        name: 'BOMList',
                        component: BOMList,
                        closable: false
                    }
                ]

            }
        },
        mounted () {

        },
        activated () {
            console.log('[activated]')
            if (this.activeTab === 'BOMList') {
                this.dolayout()
            }
        },
        methods: {
            dolayout () {
                this.$nextTick(() => {
                    const data = {
                        name: 'BOMList', // 组件名
                        methods: 'dolayout', // 方法名
                        params: null, // 参数
                        random: Math.random()
                    }
                    console.log('[SET_NAV_TABS_TODO]', data)
                    this.$store.commit('SET_NAV_TABS_TODO', data)
                })
            },
            tabChange (tab) {
                if (tab === 'BOMList') {
                    this.dolayout()
                }
                console.log('[tab]', tab)
                this.activeTab = tab
            }
        }
    }
</script>
