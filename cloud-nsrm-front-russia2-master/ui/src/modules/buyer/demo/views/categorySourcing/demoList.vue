<template>
  <el-container class="flex-container-notab studyDemoPage" direction="vertical">
    <el-main>
      <div style="padding:20px;">
        <el-button type="primary" @click="editTab('cateReport')">
          <!-- 采购管理者视图 -->
          {{ $t("cusEntry.supplement20250211.purchaseManagerView") }}
        </el-button>
      </div>
    </el-main>
  </el-container>
</template>
<script>
export default {
  name: 'DemoList',
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
