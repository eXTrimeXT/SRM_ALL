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
    // const iframes = document.getElementById(iframe)// window.frames[iframe]
    // iframes.contentWindow.postMessage(
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
        let action = res.data.action
        if (action === 'button') { // 按钮事件回调
          this.handleProcessTask('afterProcessAction')(res.data)
        } else if (action === 'beforeProcessEnterAction') { // 按钮点击前
          this.handleProcessTask('beforeProcessEnterAction')(res.data)
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
        // 按钮点击前确认
        beforeProcessEnterAction: (data) => {
          let callback = function (businessKey, extJsonData, iframe, procTitle) {
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
              bnsQuery: { ...bnsQuery, procTitle },
              callbackName: data.info.callbackName
            }, iframe)
          }
          this.beforeProcessEnterAction(callback, data)
        },
        // 结果返回
        afterProcessAction: (data) => {
          this.afterProcessAction(data)
        },
        /**
        * 重新加载iframe
        */
        reloadFrame: (iframe) => {
          this.reloadFrame(iframe)
        }
      }
      return postMessageEvents[type]
    }
  }
}
