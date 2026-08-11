<template>
  <el-container>
    <el-main>
      <AppHeader pageType="gateway" />
      <img src="../login/img/bulletinBoard.jpg" style="width:100%" alt="">
      <SourcingApplicationDetailNew class="main" />
    </el-main>
  </el-container>
</template>

<script>
import Header from '@/layout/components/Header/index.vue'
import SourcingApplicationDetailNew from './sourcingApplicationDetailNew'
export default {
  name: 'Portal',
  components: {
    SourcingApplicationDetailNew,
    AppHeader: Header
  },
  data () {
    return {

    }
  },
  computed: {
  },
  created () {

  },
  methods: {

  }
}
</script>

<style lang="scss" scoped>
.main{
  width: 70%;
  max-width: 1200px;
  margin: 15px auto 0;
}
</style>
