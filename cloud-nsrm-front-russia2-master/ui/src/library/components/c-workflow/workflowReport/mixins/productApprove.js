const getUrlQuery = (iframeName) => {
  try {
    var queryStr = document
      .querySelector('iframe[name="' + iframeName + '"]')
      .getAttribute('src')
      .split('?')[1]
    if (!queryStr) return {}
    var queryStrList = queryStr.split('&')
    var query = {}
    queryStrList.forEach(function (item, index) {
      query[item.split('=')[0]] = item.split('=')[1]
    })
    return query
  } catch (err) {
    return {}
  }
}
const processPost = (type, info, iframe) => {
  // 处理打开多tab流程按钮不可触发问题
  try {
    const iframes = window.frames[iframe]
    iframes.postMessage(
      {
        channel: 'processConnect',
        type: type,
        info: info
      },
      window.origin
    )
  } catch (err) {
    console.warn(err)
  }
}

import i18n from '@/lang'

export default {
  data () {
    return {
      bnsQuery: {}, // 业务员附带传参，在功能请求时附带
      btnTypeCallbacks: {}, // 用户存储callback回调
      processData: {} // 流程数据
    }
  },
  created () {
    this.listenProcess()
  },
  methods: {
    // 流程postMessage监听器
    listenProcess () {
      if (window.listenProcessState) return
      window.addEventListener('message', (res) => {
        if (res.data.channel && res.data.channel === 'processConnect') {
          console.log(res.data, 'listenProcess')
          this.handleProcessTask(res.data.type) && this.handleProcessTask(res.data.type)(res.data.info)
        }
      })
      window.listenProcessState = true
    },
    // 更新流程页面
    updateProcess (data, iframe) {
      processPost('updateProcess', data, iframe)
    },
    /**
    * 重新加载iframe
    */
    reloadFrame (iframe) {
      processPost('reloadFrame', {}, iframe)
    },
    // 流程message接收任务分配
    handleProcessTask (type) {
      const postMessageEvents = {
        // 流程ready
        processReady: (data) => {
          this.processReady(data)
        },
        // 按钮点击前确认
        beforeProcessEnterAction: (data) => {
          let callback = function (businessKey, extJsonData, iframe) {
            if (businessKey === false) return
            let bnsQuery = {}
            if (businessKey != undefined || businessKey != null) {
              bnsQuery.businessKey = businessKey
            }
            if (extJsonData) {
              if (typeof extJsonData === 'object') {
                bnsQuery.extJsonData = JSON.stringify(extJsonData)
              } else {
                bnsQuery.extJsonData = extJsonData
              }
            }
            processPost('frameAction', {
              bnsQuery: bnsQuery,
              callbackName: data.callbackName
            }, iframe)
          }
          this.beforeProcessEnterAction(callback, data)
        },
        // 结果返回
        afterProcessAction: (data) => {
          this.afterProcessAction(data.result)
        },

        /**
         * Api接口调用
         * @param {*} type,api接口类型
         * @param {*} params,api接口传参
         */
        postActionApi: (data, iframe) => {
          if (!data || !data.type || !data.params) {
            console.error(i18n.t('cusEntry.library.apiErrorTip1')) // type, params 为必传参数
            return
          }
          processPost('postActionApi', data, iframe)
        },

        /**
        * 通过Api方式调用流程界面方法
        * @param {*} type,api接口类型
        * @param {*} businessKey,业务id
        * @param {*} extJsonData,业务拓展对象
        * @param {*} formData,表单数据
        */
        callFrameActionByApi: (data) => {
          if (!this.processInfo || !this.processInfo.buttonTypeMap || !this.processInfo.buttonTypeMap[data.type]) {
            console.error(i18n.t('cusEntry.library.apiErrorTip2')) // 暂无该功能权限
            return
          }
          this.bnsQuery = {}
          if (data.businessKey) { this.bnsQuery.businessKey = data.businessKey }
          if (data.extJsonData) { this.bnsQuery.extJsonData = data.extJsonData }
          if (data.formData) { this.processInfo.formData = data.formData }
          this.btnTypeMap[data.type].value()
        },

        /**
        * 重新加载iframe
        */
        reloadFrame: (iframe) => {
          this.reloadFrame(iframe)
        },
        // 设置fixed浮动
        setFrameFixed: (data) => {
          console.log('setFrameFixed', data)
          let iframeName = data.iframeName
          var query = getUrlQuery(iframeName)
          console.log('query', query)
          if (query.autoFixed && query.autoFixed === 'true') {
            if (data.state) {
              document
                .querySelector('iframe[name="' + iframeName + '"]')
                .classList.add('process_fixed')
            } else {
              document
                .querySelector('iframe[name="' + iframeName + '"]')
                .classList.remove('process_fixed')
            }
          }
          // 窗口事件
          if (data.state) {
            if (window.frameDialogOpen) {
              window.frameDialogOpen()
            }
          } else {
            if (window.frameDialogClose) {
              window.frameDialogClose()
            }
          }
        },
        // 设置iframe 高度
        setProcessHeight: (data) => {
          let iframeName = data.iframeName
          var query = getUrlQuery()
          var height = data.height || 300
          if (query.sbt && query.sbt === 'false') {
            height -= 16
          } else {
            height += 50
          }
          if (!query.autoHeight || query.autoHeight !== 'true') return
          document.querySelector('iframe[name="' + iframeName + '"]').style.height =
            height + 'px'
        }
      }
      return postMessageEvents[type]
    }
  }
}
