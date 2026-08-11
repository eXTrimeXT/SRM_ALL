<template>
  <el-container
    class="flex-container-notab studyDemoPage"
    direction="vertical"
  >
    <el-main class="main_body">
      <el-row :gutter="16">
        <!-- 左侧 -->
        <el-col :span="17">
          <el-row>
            <!-- 待办事项 -->
            <img
              src="./img/todo.png"
              alt=""
              width="100%"
            >
          </el-row>
          <el-row>
            <!-- 报表 -->
            <img
              src="./img/report.png"
              alt=""
              width="100%"
            >
          </el-row>
          <el-row>
            <!-- 工作流 -->
            <img
              src="./img/workflow.png"
              alt=""
              width="100%"
            >
          </el-row>
        </el-col>
        <!-- 右侧 -->
        <el-col :span="7">
          <el-row>
            <!-- 欢迎 -->
            <img
              src="./img/welcome.png"
              alt=""
              width="100%"
            >
          </el-row>
          <el-row>
            <!-- 常用功能 -->
            <img
              src="./img/commonFunction.png"
              alt=""
              width="100%"
            >
          </el-row>
          <el-row>
            <!-- 公告 -->
            <img
              src="./img/announcement.png"
              alt=""
              width="100%"
            >
          </el-row>
          <el-row>
            <!-- 寻源云 -->
            <img
              src="./img/sourceCloud.png"
              alt=""
              width="100%"
            >
            <img
              src="./img/tag.png"
              alt=""
              class="free"
            >
          </el-row>
          <el-row>
            <!-- 风控云 -->
            <img
              src="./img/creditCloud.png"
              alt=""
              width="100%"
            >
          </el-row>
          <el-row>
            <!-- 优品供 -->
            <img
              src="./img/optiMailCloud.png"
              alt=""
              width="100%"
            >
          </el-row>
        </el-col>
      </el-row>
    </el-main>
  </el-container>
</template>
<script>
export default {
  name: 'DashboardDemo',
  components: {},
  data () {
    return {}
  },
  // 页面创建
  created () {},
  // 页面挂载完成
  mounted () {},
  methods: {}
}
</script>
<style scoped lang="scss">
.studyDemoPage {
  // min-height: 1030px;
  .section {
    padding: 5px;
  }
  .el-row {
    margin-bottom: 16px;
  }
  .formStyle {
    width: 400px;
  }
  .divRad {
    width: 100px;
    height: 50px;
    background: red;
  }
  .divBlue {
    background: blue;
  }
  .b {
    height: 200px;
    background: blue;
  }
  #a {
    height: 100px;
    background: blue;
  }
  .free {
    position: absolute;
    right: -15px;
    top: -15px;
    width: 71px;
    height: 71px;
  }
  .main_body {
    // overflow-y: hidden;
    margin-top: 6px;
  }
}
.app-main :deep(.app-main-container) {
  background-color: #edf0f2; // #edf0f2
}
</style>
