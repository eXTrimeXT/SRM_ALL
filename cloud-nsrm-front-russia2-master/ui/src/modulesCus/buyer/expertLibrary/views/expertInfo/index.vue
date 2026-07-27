<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>

<script>
import NavTabs from 'lib@/components/NavTabs'
// 专家申请界面
import ExpertInfoList from './list'
// 专家升级申请界面
import ExpertUpgrade from './upgrade'
import { mapGetters } from 'vuex'

export default {
  name: 'ExpertInfo',

  components: {
    NavTabs
  },

  data () {
    return {
      activeTab: 'expertInfoList',
      tabs: [
        {
          // title: '专家信息',
          title: () => this.$t('cusEntry.supplement20250205.expertInformation'),
          name: 'expertInfoList',
          component: ExpertInfoList,
          closable: false
        }
        // {
        //   title: '专家升级',
        //   name: 'expertUpgrade',
        //   component: ExpertUpgrade,
        //   closable: false
        // }
      ]
    }
  },

  computed: {
    ...mapGetters(['userInfo'])
  },

  async created () {
    // 需判断是否是升级申请
    // let transformParams = transformMQL.save(commonType, [{ userId: this.userInfo.userId }], 'getLatestApplyInfoByUserId')
    // const response = await expInfoHttp.getApplyInfoByUserId(transformParams)
    // console.log('response', response)
  }
}
</script>
