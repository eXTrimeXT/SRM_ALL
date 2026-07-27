<template>
  <el-container
    class="flex-container-notab studyDemoPage"
    direction="vertical"
  >
    <el-main>
      <h3>demosup</h3>
    </el-main>
  </el-container>
</template>
<script>
export default {
  name: 'DemoSetNewSup1',
  components: {},
  data () {
    return {

    }
  },
  computed: {

  },
  watch: {

  },
  // 页面创建
  created () {

  },
  // 页面挂载完成
  mounted () {

  },
  methods: {
    editTab (routeName) {
      this.$router.push({
        name: routeName
      })
    }
  }
}
</script>
<style>

</style>
