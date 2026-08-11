<template>
  <div
    :id="iframeName+'_wrap'"
    class="flowIframeStyle"
  />
</template>
<script>
import { getToken } from '@/utils/auth'
import { beforeProcess } from '@/api/workFlow'
import _ from 'lodash'
import ideProcessConnector from '../mixins/ideApprove'

export default {
  name: 'IdeMode',
  mixins: [ideProcessConnector],
  props: {
    needInit: {
      type: Boolean,
      default: false
    },
    isNested: {
      type: Boolean,
      default: false
    },
    funParams: {
      type: Object,
      default: function () {
        return {}
      }
    }
  },
  data () {
    return {
      iframeUrl: '',
      iframeHeight: 400,
      dataBeforeProcess: {},
      iframeName: 'process_ideFlow',
      userInfo: this.$store.getters.userInfo,
      refreshTime: 0
    }
  },
  computed: {
    device () {
      return this.$store.getters.device
    },
    authorization () {
      return `Bearer ${getToken()}`
    }
  },
  watch: {
    funParams: {
      handler (data) {
        if (data.workflowActive || this.isNested) {
          this.freshFunparams(data)
        }
      },
      immediate: true,
      deep: true
    }
  },
  created () {
    this.iframeName = `${this.funParams.businessType}_process_ideFlow_${this.funParams.businessId}` // iframeName
  },
  mounted () {
    window.mflow_getToken = () => {
      // 根据实际改为对应前端存放token的地址
      return this.authorization
    }
  },
  // type 按钮类型
  // flowOverrule 驳回
  // flowDiscard 废弃
  // flowCirculate 传阅
  // flowTransfer 转办
  // flowApprove 审批
  // flowHold 暂存
  // flowStart 提交
  // flowCommunicate 沟通
  // reply 回复
  // reback  撤回
  // urge  催办
  // cancelCommunicate 取消沟通
  destroy () {
    // const iFrameWin = this.$refs[this.iframeName].contentWindow
    window.removeEventListener('message', null)
  },
  methods: {
    // 参数刷新
    freshFunparams (data, meta) {
      let iframeName = `${data.businessType}_process_ideFlow_${data.businessId}`
      let orderInfo = {
        businessType: data.businessType,
        processType: data.businessType,
        businessId: data.businessId,
        formData: data.businessVariables.formData,
        procTitleObj: data.businessVariables.procTitleObj,
        iframeName: iframeName
      }
      this.initPage(true, orderInfo, iframeName)
    },
    // 根据参数处理查询
    initPage (isReload, orderInfo, iframeName) {
      // 流程预处理，从后台获取iframe，token等等
      this.getdataBeforeProcess(isReload, orderInfo, iframeName)
    },
    // 流程预处理，从后台获取iframe，token等等
    async getdataBeforeProcess (isReload, orderInfo, iframeName) {
      let resOrderInfo = orderInfo
      let businessType = resOrderInfo.businessType
      let businessId = resOrderInfo.businessId
      if ((businessType == null || businessType === '') || !businessId) {
        return
      }
      // IDE 工作流模式
      // 查询流程参数
      const param = {
        businessType: businessType,
        processType: businessType,
        businessId: businessId
      }
      // 查询模版数据
      const res = await beforeProcess(param)
      let signature = res.data.signature || {}
      this.dataBeforeProcess = {
        appId: signature.appId, // 应用id
        appKey: signature.appKey, // 应用key
        authMode: signature.mode,
        signature: signature.signature,
        timestamp: signature.timestamp,
        localeKey: res.data.localeKey, // 国际化
        iframeHost: res.data.iframeUrl, // iframeUrl
        templateCode: res.data.templateCode,
        flowinstanceId: res.data.flowinstanceId, // 流程实例id
        taskId: res.data.taskId, // 任务ID
        todoId: res.data.todoId,
        titleTemplate: res.data.titleTemplate // 流程标题模版
      }
      let setCacheData = {
        ...orderInfo,
        templateCode: res.data.templateCode,
        titleTemplate: res.data.titleTemplate // 流程标题模版
      }
      // 由于el-tabs 来回切换导致的this 取值问题，把数据存起来
      let strOrder = JSON.stringify(setCacheData)
      sessionStorage.setItem('currentActiveFlow', iframeName)
      sessionStorage.setItem(`${iframeName}`, strOrder)
      // 加载iframe
      let self = this
      this.$nextTick(() => {
        self.ideLoadIframe(isReload, resOrderInfo)
      })
    },
    // 加载iframe
    ideLoadIframe (isReload, orderInfo) {
      let host = this.dataBeforeProcess.iframeHost // iframe host
      let appId = this.dataBeforeProcess.appId // 应用id 固定值
      let appKey = this.dataBeforeProcess.appKey
      let authMode = this.dataBeforeProcess.authMode // 'Signature'
      let signature = this.dataBeforeProcess.signature
      let timestamp = this.dataBeforeProcess.timestamp
      let templateCode = this.dataBeforeProcess.templateCode
      let procInstId = this.dataBeforeProcess.flowinstanceId // 流程ID 选填（生成实例后必传）
      let taskId = this.dataBeforeProcess.taskId // 从代办、已办列表跳转到详情页面时候有的话需要带过来
      let todoId = this.dataBeforeProcess.todoId
      let formData = JSON.stringify(orderInfo.formData) || '' // 单据字段信息
      let formDataId = orderInfo.businessId // 单据id

      let userCode = this.userInfo.username // 用户账号
      let userName = this.userInfo.nickname // 用户名
      // let topBranch = this.userInfo.department // v2部门名称
      // let topUserId = this.userInfo.username // v2工号id
      let iframeName = orderInfo.iframeName
      let linkType = null // 1我的流程 2代办 3已办 4抄送 5草稿
      if (procInstId) { // 有流程ID
        if (taskId) {
          linkType = 2
        } else {
          linkType = 5
        }
      } else {
        linkType = 1
      }

      // let taskKey = '' // 选填 从代办、已办列表跳转到详情页面时候有的话需要带过来
      // let todoId = '' // 待办id  待办列表跳转进来必填参数

      let iframeType = 'v1' // iframeType版本 PC 版本
      if (this.device === 'device-xs') { // 当前设备是移动版本
        iframeType = 'app' // 移动端版本
      }
      // let url = `/ide-flow/setupProcess.html?AppId=${appId}&AuthMode=${authMode}&AppKey=${appKey}&userCode=${userCode}&userName=${userName}&Signature=${signature}&Timestamp=${timestamp}&modelCode=${templateCode}&formData=${formData}&iframeType=${iframeType}&top_title=${topTitle}`
      let url = `/mflow/processPage.html#/?templateCode=${templateCode}&formData=${formData}&iframeType=${iframeType}&beforeActionList=start,hold&iframeName=${iframeName}`
      // &top_branch=${topBranch}&top_userId=${topUserId}&beforeActionList=start,hold
      if (formDataId) {
        url = url + '&formDataId=' + formDataId
      }
      if (procInstId) {
        url = url + '&procInstId=' + procInstId
      }
      if (taskId) {
        url = url + '&taskId=' + taskId
      }
      if (linkType) {
        url = url + '&linkType=' + linkType
      }
      if (todoId) {
        url = url + '&todoId=' + todoId
      }
      // if (taskKey) {
      //   url = url + '&taskKey=' + taskKey
      // }

      let iframeSrc = encodeURI(url)
      console.log('url', url)
      let flowIframeWrap = document.getElementById(iframeName + '_wrap')
      let flowIframe = document.getElementById(iframeName)
      let iframeNode = document.createElement('iframe')
      iframeNode.setAttribute('id', iframeName)
      iframeNode.setAttribute('name', iframeName)
      iframeNode.setAttribute('class', 'flowIframeStyle')
      iframeNode.setAttribute('src', iframeSrc)
      if (!flowIframe) {
        if (flowIframeWrap) {
          document.getElementById(iframeName + '_wrap').appendChild(iframeNode)
        }
      } else {
        if (flowIframeWrap) {
          document.getElementById(iframeName + '_wrap').removeChild(flowIframe)
          document.getElementById(iframeName + '_wrap').appendChild(iframeNode)
          document.getElementById(iframeName).src = iframeSrc // 设置iframe url
        }
      }
      this.iframeName = iframeName
      console.log('iframeName', iframeName)
      let titleTemplate = this.dataBeforeProcess.titleTemplate // 流程标题模版
      let procTitleObj = this.funParams.businessVariables.procTitleObj || {} // 单据传入的表单信息
      let procTitleStr = titleTemplate ? titleTemplate.replace(/{(.*?)}/g, (match, p1) => (procTitleObj[p1]) || '') : ''
      this.reloadFrame(iframeName)
    },
    // 按钮点击前
    beforeProcessEnterAction (callback, data) {
      let currentActiveFlow = data?.queryParams?.iframeName || sessionStorage.getItem('currentActiveFlow')
      sessionStorage.setItem('currentActiveFlow', currentActiveFlow)
      let curOrderInfo = JSON.parse(sessionStorage.getItem(currentActiveFlow)) // 当前操作单据相关缓存数据
      let bussinessKey = curOrderInfo.templateCode // 流程模版code
      let titleTemplate = curOrderInfo.titleTemplate // 流程标题模版
      let procTitleObjLocal = curOrderInfo.procTitleObj // 单据信息
      let procTitleObj = procTitleObjLocal || {} // 单据传入的表单信息
      let procTitleStr = titleTemplate ? titleTemplate.replace(/{(.*?)}/g, (match, p1) => (procTitleObj[p1]) || '') : ''
      // console.log('流程标题', procTitleStr)
      callback(bussinessKey, null, currentActiveFlow, procTitleStr)
    },
    // 流程内部事件执行完成之后回调
    afterProcessAction (data) {
      let info = data.info
      this.eventBus(info.callback, data)
    },
    // flowOverrule 驳回
    // flowDiscard 废弃
    // flowCirculate 传阅
    // flowTransfer 转办
    // flowApprove 审批
    // flowHold 暂存
    // flowStart 提交
    // flowCommunicate 沟通
    // reply 回复
    // reback  撤回
    // 事件处理
    eventBus (eveName, result) {
      let self = this
      let curIframeName = result.queryParams.iframeName
      const { res } = result.info
      if (res == null) {
        // 提示信息
        return
      }
      // 触发父组件调用回调
      this.$emit('flowAfterProcessAction', res, eveName)
      if (eveName) {
        if (['flowHold', 'flowStart', 'flowApprove', 'reback', 'flowOverrule', 'flowDiscard', 'prev'].includes(eveName)) {
          if (res.code == 0) {
            // self.$message.success(res.message) // 消息提示
            // 重新加载frame
            let currentFrameName = curIframeName
            const localOrderConf = JSON.parse(sessionStorage.getItem(currentFrameName)) || {} // 本地
            self.getdataBeforeProcess(true, localOrderConf)
          } else {
            self.$message.error(res.message || this.$t('common.sysError')) // 消息提示
          }
        }
      }
    },
    // 暂存
    flowHold (res) {
      // console.log('暂存 1111', res)
    },
    // 提交
    flowStart (res) {
      // console.log('提交 1111', res)
    },
    // 撤回
    reback (res) {
      // console.log('撤回 1111', res)
    },
    // 审批
    flowApprove (res) {
      // console.log('审批 1111', res)
    },
    // 驳回
    flowOverrule (res) {
      // console.log('驳回 1111', res)
    },
    // 废弃
    flowDiscard (res) {
      // console.log('废弃 1111', res)
    },
    // 传阅
    flowCirculate (res) {
      // console.log('传阅 1111', res)
    },
    // 转办
    flowTransfer (res) {
      // console.log('转办 1111', res)
    },
    // 沟通
    flowCommunicate (res) {
      // console.log('沟通 1111', res)
    },
    // 回复
    reply (res) {
      // console.log('回复 1111', res)
    },
    // 催办
    urge (res) {
      // console.log('催办 1111', res)
    },
    // 取消沟通
    cancelCommunicate (res) {
      // console.log('取消沟通 1111', res)
    },
    // 上一步
    prev (res) {
      // console.log('上一步 1111', res)
    }

  }
}
</script>
<style>
.flowIframeStyle{
  border:none;
  height: 100%;
  position:absolute;
  width:100%;
}
</style>
