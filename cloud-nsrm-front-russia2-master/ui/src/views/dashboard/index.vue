<template>
  <div class="dashboard-container">
    <component :is="currentRole" />
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import DashboardBuyer from './buyer'
import DashboardVendor from './vendor'

export default {
  name: 'Dashboard',
  components: { DashboardBuyer, DashboardVendor },
  data () {
    return {
      currentRole: 'DashboardBuyer',
      userType: this.$store.getters.userType // vendor buyer
    }
  },
  computed: {
    ...mapGetters([
      // 'roles'
    ])
  },
  created () {
    if (this.userType === 'VENDOR') {
      this.currentRole = 'DashboardVendor'
      // this.$http({
      //   url: '/api-pef/projectScoreWarning/listPage',
      //   method: 'POST',
      //   data: {
      //     // companyId: this.$store.getters.userInfo?.companyId,
      //     pageNum: 1,
      //     pageSize: 10000
      //   },
      //   loading: true
      // }).then( res  => {
      //   console.log(res, 'res')
      //   const list = res.data?.list
      //   if (list?.length > 0) {
      //     let num = 0
      //     list.forEach(element => {
      //       if(element.readStatus == 'N'){
      //         num = num + 1
      //       }
      //     })
      //     if (num > 0) {
      //        this.$alert(`有${num}条预警，请在'供应商预警协同'中查看！`, '预警提示', {
      //         confirmButtonText: '确定',
      //         callback: action => {
      //           return false
      //         }
      //       })
      //     }
      //   }
      // })
    } else {
      this.currentRole = 'DashboardBuyer'
    }
  }
}
</script>

<style lang="scss" scoped>
  .dashboard-container {
    background: #edeff2 !important;
  }
</style>
