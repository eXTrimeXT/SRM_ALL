<template>
  <el-container
    class="flex-container-notab studyDemoPage"
    direction="vertical"
  >
    <el-main>
      <h3>
        <!-- 流程demo、表头配置 -->
        {{ $t("cusEntry.supplement20250205.flowDemoHeaderConfig") }}
      </h3>
      <div>
        <el-button
          type="primary"
          @click="editTab('flowDemo')"
        >
          <!-- 流程demo、表头配置demo -->
          {{ $t("cusEntry.supplement20250211.flowDemoHeaderConfigDemo") }}
        </el-button>
      </div>
      <h3 style="margin:20px 0;">
        <!-- 页面常见单结构--新同事学习用 -->
       {{ $t("cusEntry.supplement20250211.pageCommonSingleStructureForNewColleagueLearning") }}
      </h3>
      <div>
        <el-button
          type="primary"
          @click="editTab('demoOrderMultiple')"
        >
          <!-- 头行结构单据 -->
          {{ $t("cusEntry.supplement20250211.headRowStructureInvoice") }}
        </el-button>
        <el-button
          type="primary"
          @click="editTab('demoOrderTab')"
        >
          <!-- 订单数据tab方式新增编辑查看 -->
          {{ $t("cusEntry.supplement20250211.orderDataTabAddEditView") }}
        </el-button>
        <el-button
          type="primary"
          @click="editTab('demoOrderPop')"
        >
          <!-- 订单数据弹框方式新增编辑查看 -->
          {{ $t("cusEntry.supplement20250211.orderDataDialogMethodAddEditView") }}
        </el-button>
        <el-button
          type="primary"
          @click="editTab('demoOrderAppend')"
        >
          <!-- 订单数据表格追加的方式 -->
          {{ $t("cusEntry.supplement20250211.orderDataTableAppendWay") }}
        </el-button>
      </div>
      <h3 style="margin:20px 0;">
        <!-- 开发同事参考 -->
        {{ $t("cusEntry.supplement20250211.devColleagueRef") }}
      </h3>
      <div>
        <el-button
          type="primary"
          @click="editTab('vxeTableDemo')"
        >
          <!-- 倒计时、大数据vxe-table、input-format指令 -->
          {{ $t("cusEntry.supplement20250211.countdownTimerBigDataVxeTableInputFormatDirective") }}
        </el-button>
        <el-button
          type="primary"
          @click="editTab('quickSearchDemo')"
        >
          <!-- 部分组件示例 -->
          {{ $t("cusEntry.supplement20250211.componentExample") }}
        </el-button>
      </div>
    </el-main>
  </el-container>
</template>
<script>
export default {
  name: 'CodeStudy',
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
