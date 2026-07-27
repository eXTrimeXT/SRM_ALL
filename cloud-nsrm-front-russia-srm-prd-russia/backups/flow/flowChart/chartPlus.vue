<template>
  <div
    id="flowChartContainer"
    style="width: 100%; cursor: pointer;"
  />
</template>
<script>
    import { loadJS } from '@/utils'
    import config from '@/config/user.env'
    export default {
        name: 'CchartPlus',
        props: ['fdId', 'appId'],
        data () {
            return {
                env: ''
            }
        },
        watch: {
            fdId: {
                immediate: true,
                handler (fdId) {
                    console.log('fdId', this.fdId)
                    if (fdId) {
                        this.init()
                    }
                }
            }
        },
        mounted () {
            // this.init();
        },
        methods: {
            init () {
                // console.log(this.env )
                // console.log(this.fdId )
                // console.log(this.appId )
                this.env = config.flowEnv
                const idaasVerify = false // window.environment.idaasVerify || false; // 是否需要开启 idaas网关登录
                loadJS('./MFlowChart.js', () => {
                    MFlowChart.getGrpah('flowChartContainer', {
                        idaasVerify,
                        width: 1000,
                        height: 800,
                        env: this.env,
                        isDraft: false,
                        fdId: this.fdId,
                        appId: this.appId
                    })
                })
            }
        }
    }
</script>
<style scoped>

</style>
