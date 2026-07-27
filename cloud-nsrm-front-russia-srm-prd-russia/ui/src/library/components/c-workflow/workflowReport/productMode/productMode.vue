<template>
  <div
    :id="iframeName+'_wrap'"
    class="flowIframeStyle"
  />
</template>

<script>
import { beforeProcess } from '@/api/workFlow'
import _ from 'lodash'
import processConnector from '../mixins/productApprove'
import { getEntranceType } from '@/utils/auth'

export default {
  name: 'ProductMode',
  mixins: [processConnector],
  props: {
    funParams: {
      type: Object,
      default: function () {
        return {}
      }
    },
    isNested: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      iframeUrl: '',
      dataBeforeProcess: {},
      iframeName: 'process_flow',
      currentKey: this.$store.getters.currentKey // 附件加密key
    }
  },
  watch: {
    funParams: {
      handler (data, meta) {
        if (data.workflowActive || this.isNested) { // 流程激活时往下执行
          this.freshFunparams(data, meta)
        }
      },
      immediate: true,
      deep: true
    }
  },
  created () {
    this.iframeName = `${this.funParams.businessType}_process_flow_${this.funParams.businessId}` // iframeName
    // let orderInfo = {
    //   businessType: this.funParams.businessType,
    //   businessId: this.funParams.businessId,
    //   formData: this.funParams.businessVariables,
    //   iframeName: this.iframeName
    // }
    // this.initPage(true, orderInfo)
    // 用于全局使用
    // afterProcessAction = this.afterProcessAction // 按钮事件后回调
    // beforeProcessFunction = this.getdataBeforeProcess // 传递到全局函数，用于全局调用
  },
  methods: {
    freshFunparams (data, meta) {
      console.log('freshFunparams-workflowReport', data)
      let iframeName = `${data.businessType}_process_flow_${data.businessId}` // iframeName
      let orderInfo = {
        businessType: data.businessType,
        processType: data.businessType,
        businessId: data.businessId,
        formData: data.businessVariables,
        iframeName: iframeName
      }
      console.log('freshFunparams-orderInfo', orderInfo)
      // if (!_.isEqual(data, meta)) this.initPage(true, orderInfo)
      this.initPage(true, orderInfo)
    },
    // 根据参数处理查询
    initPage (isReload, orderInfo) {
      // 流程预处理，从后台获取iframe，token等等
      this.getdataBeforeProcess(isReload, orderInfo)
    },

    // 流程预处理，从后台获取iframe，token等等
    async getdataBeforeProcess (isReload, orderInfo) {
      let resOrderInfo = orderInfo
      let businessType = resOrderInfo.businessType || resOrderInfo.businessKey
      let businessId = resOrderInfo.businessId
      if ((businessType == null || businessType === '') || !businessId) {
        return
      }
      // 按钮事件后有单据参数回传
      let param = {
        businessType: businessType,
        processType: businessType,
        businessId: businessId
      }
      const res = await beforeProcess(param)
      const { iframeUrl, flowinstanceId, communicateId, taskId, token, localeKey } = res.data
      this.dataBeforeProcess = {
        iframeHost: iframeUrl, // iframeUrl
        flowinstanceId: flowinstanceId, // 流程实例id
        communicateId: communicateId,
        taskId: taskId,
        token: token,
        localeKey: localeKey
      }
      this.loadIframeProduct(isReload, resOrderInfo)
    },
    /* 操作流程后触发组件回调 */
    afterProcessAction (result) {
      if (result == null) {
        // 提示信息
        return
      }

      // 触发父组件调用回调
      this.$emit('afterProcessAction', result)
      // 更新父页面等
      if (result.success) {
        // 提示信息，工作流内部会提示
        // 默认是前端只刷新工作流，不自动刷新单据，后端保存逻辑要判断是否审批才能保存
        // 重新加载
        let orderInfo = result.orderInfo
        this.getdataBeforeProcess(true, orderInfo)
      } else {
        // 提示信息，工作流内部会提示
      }
    },
    // 加载iframe
    loadIframeProduct (isReload, orderInfo) {
      let entranceType = getEntranceType() // 登录方式
      let token = this.dataBeforeProcess.token
      let taskId = this.dataBeforeProcess.taskId
      let processInstanceId = this.dataBeforeProcess.flowinstanceId
      let communicateId = this.dataBeforeProcess.communicateId
      let localeKey = this.dataBeforeProcess.localeKey
      let businessType = orderInfo.businessType
      let businessId = orderInfo.businessId
      let formData = JSON.stringify(orderInfo.formData)
      let iframeName = orderInfo.iframeName

      // 从服务器端获取url等配置信息,例如http://10.17.145.72/oasis
      // let host =this.dataBeforeProcess.iframeUrl;
      // 优先用当前url的地址,window.location.host（域名 + 端口）
      const host = '/oasis'
      // 如果是本地地址，需要配置/oasis转发
      let url = ''
      // 起草
      url = host + '/#/oasis/ihr/flow/approveFrame?autoFixed=true&token=' + token +
              '&processType=' + businessType +
              '&formData=' + formData +
              '&iframeName=' + iframeName +
              '&localeKey=' + localeKey
      if (processInstanceId != null && processInstanceId !== '') {
        url = url + '&processInstanceId=' + processInstanceId
      }
      if (taskId != null && taskId !== '') {
        url = url + '&taskId=' + taskId
      }
      if (communicateId) {
        url = url + '&communicateId=' + communicateId
      }
      if (businessId) {
        url = url + '&businessKey=' + businessId
      }
      if (entranceType) {
        url = url + '&entranceType=' + entranceType
      }

      console.log(url)
      let iframeSrc = ''
      if (window.location.host.indexOf('localhost') > -1) {
        iframeSrc = `/oasis/#${url.split('#')[1]}`
      } else {
        iframeSrc = url
      }
      let flowIframe = document.getElementById(iframeName)
      if (!flowIframe) {
        let iframeNode = document.createElement('iframe')
        iframeNode.setAttribute('id', iframeName)
        iframeNode.setAttribute('name', iframeName)
        iframeNode.setAttribute('class', 'flowIframeStyle')
        iframeNode.setAttribute('src', iframeSrc)
        document.getElementById(iframeName + '_wrap').appendChild(iframeNode)
      } else {
        document.getElementById(iframeName).src = iframeSrc // 设置iframe url
      }
      // if (isReload) {
        // 触发刷新
        this.reloadFrame(iframeName)
      // }
    },
    processReady (param) {
      // 处理完后调用window.PROCESS_CONNECTOR.updateProcess更新流程数据
      // formData，表单数据
      // nodeData，根据processReady中的nodeKeys，从form表单取出的字段值。注意emplyeeId为隐性值，业务决定是否传
      let orderInfo = param.orderInfo // 单据信息
      this.updateProcess({
        formData: orderInfo.formData,
        nodeData: orderInfo.formData
      }, orderInfo.iframeName)
    },
    beforeProcessEnterAction (callback, result) {
      // 处理完后调用callback，callback中支持传对象，对象会在请求中带入
      console.log('流程返回数据')
      console.log(result)
      let btnMsg = result.btnMsg // 按钮信息
      let rebackOrder = result.orderInfo // 点击按钮前把单据信息返回来
      let businessId = null
      let businessObj = {}
      businessId = rebackOrder.businessId // 单据ID
      businessObj = {
        businessId: businessId,
        handleType: btnMsg.key,
        handleLabel: btnMsg.label
      }
      // 必须要调用，不然不会继续走
      callback(businessId, businessObj, rebackOrder.iframeName)
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
.process_fixed{
  position:fixed !important;
  width:100% !important;
  height:100% !important;
  left:0 !important;
  top:0 !important;
  z-index:1002 !important;
}
</style>
