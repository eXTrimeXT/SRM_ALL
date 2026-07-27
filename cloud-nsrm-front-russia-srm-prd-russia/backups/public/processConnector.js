(function () {
  if (!window.listenProcessState) {
    window.addEventListener('message', function (res) {
      if (res.data.channel && res.data.channel === 'processConnect') {
        handleProcessCall[res.data.type] &&
          handleProcessCall[res.data.type](res.data.info)
        console.log(res.data, 'res Type')
      }
    })
    window.listenProcessState = true
  }

  var processPost = function (type, info, iframe) {
    // 处理打开多tab流程按钮不可触发问题
    // for (let i = 0; i < window.frames.length; i++) {
      // let framesWin = document.getElementById(iframe).contentWindow
      try {
        // console.log('发送消息' + i, type, info)
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
    // }
  }

  var getUrlQuery = function (iframeName) {
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

  // 处理流程呼叫
  var handleProcessCall = {
    // 流程ready
    processReady: function (data) {
      window.processReady(data)
    },

    // 按钮点击前确认
    beforeProcessEnterAction: function (data) {
      var callback = function (businessKey, extJsonData, iframe) {
        if (businessKey === false) return
        var bnsQuery = {}
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
      window.beforeProcessEnterAction(callback, data)
    },

    // 结果返回
    afterProcessAction: function (data) {
      window.afterProcessAction(data.result)
    },

    // 设置fixed浮动
    setFrameFixed: function (data) {
      console.log('setFrameFixed')
      console.log(data)
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

    // 设置iframe高度
    setProcessHeight: function (data) {
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
    },

    // 流程中心点击
    centreClick: function (data) {
      window.centreClick(data.data)
    }
  }

  window.PROCESS_CONNECTOR = {
    /**
     * 更新流程
     * @param {*} formData,表单数据
     * @param {*} nodeData,监听字段值，获取动态node节点， emplyeeId为隐性传参，由业务决定
     */
    updateProcess: function (data, iframe) {
      processPost('updateProcess', data, iframe)
    },

    /**
     * Api接口调用
     * @param {*} type,api接口类型
     * @param {*} params,api接口传参
     */
    postActionApi: function (data, iframe) {
      if (!data || !data.type || !data.params) {
        console.error('type,params 为必传参数')
        return
      }
      processPost('postActionApi', data, iframe)
    },

    /**
     * 通过Api方式调用流程界面方法
     * @param {*} type,api接口类型，必填
     * @param {*} businessKey,业务id，非必填
     * @param {*} extJsonData,业务拓展对象，非必填
     * @param {*} formData,表单数据，非必填，Object
     */
    callFrameActionByApi: function (data, iframe) {
      if (!data || !data.type) {
        console.error('type 为必传参数')
        return
      }
      if (data.extJsonData) {
        if (typeof data.extJsonData === 'object') {
          data.extJsonData = JSON.stringify(data.extJsonData)
        }
      }
      processPost('callFrameActionByApi', data, iframe)
    },

    /**
     * 重新加载iframe
     */
    reloadFrame: function (iframe) {
      processPost('reloadFrame', {}, iframe)
    }
  }

  // 样式注入
  var style = document.createElement('style')
  var styleText = document.createTextNode(`
    .process_fixed{
      position:fixed !important;
      width:100% !important;
      height:100% !important;
      left:0 !important;
      top:0 !important;
      z-index:1002 !important;
    }
  `)
  style.appendChild(styleText)
  var head = document.getElementsByTagName('head')[0]
  head.appendChild(style)
})()
