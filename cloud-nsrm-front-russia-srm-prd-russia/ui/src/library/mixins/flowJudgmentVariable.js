/**
 * 流程模式变量描述
 * Product 产品工作流集成模式
 * IdeFlow IDE工作流集成模式
 * IdeSdk IdeSdk推送模式 (/api-base/flow/event/submitEngine 处理后推送到OA)
 * None 无工作流模式 (SRM接口直接提交审批通过 /api-base/flow/event/submitEngine)
 * Iframe iframe嵌入页面模式 (没有拉通)
 * Self 自带页面模式 (没有拉通)
 * Push 无页面推送模式 (没有拉通)
 */
/**
 * 1、流程公共判断变量设置
 * notSearchTodoMode -> 列表页面里面定义哪些模式不需要查询代办数据 /api-base/flow/event/queryTodo
 * flowWithTabMode -> tab形式展示流程以及单据下面显示流程历史信息
 * srmMode 流程关闭模式就是SRM接口审批模式
 * 显示审批流tab和流程历史
 * 审批流关掉以后接口审批的控制
 * tab方式列表页点击审批跳进去流程tab
 * */
export default {
  data () {
    return {
      // 列表页面查询后afterQuery定义哪些模式不需要查询代办数据 /api-base/flow/event/queryTodo
      notSearchTodoMode: ['None', 'IdeSdk', 'Push', 'Iframe', 'Self'],
      // 审批流tab显示和历史信息的模式、列表页操作栏显示审批按钮
      flowWithTabMode: ['Product', 'IdeFlow'],
      // 流程关闭模式就是SRM接口审批模式
      srmFlowMode: ['None']
    }
  }
}
