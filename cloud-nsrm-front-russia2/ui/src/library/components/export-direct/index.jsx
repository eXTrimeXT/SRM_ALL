import http from '@/utils/axios/http'
import { Message } from '@meicloud/element-ui'
import { messageConfig } from '@/utils/message'
export default {
  name: 'ExportDirect',
  props: {
    exprotUrl: { // 导出URL
      type: String,
      default: null
    },
    filterParams: { // 入参
      type: [Object, Array],
      default: () => { }
    },
    timeout: { // 延时
      type: Number,
      default: 350000
    },
    requstType: { // 请求类型
      type: String,
      default: 'GET'
    },
    filename: { // 导出文件名
      type: String,
      default: ''
    },
    btnText: { // 按钮文字
      type: String,
      default: ''
    },
    disabled: {
      type: Boolean,
      default: false
    },
    validateExport: {
      type: Function,
      default: () => {
        return new Promise(resolve => {
          resolve()
        })
      }
    },
    // 按钮类型
    type: {
      type: String,
      default: 'primary'
    }
  },
  data () {
    return {
    }
  },
  computed: {
    filenameText () {
      return this.filename || this.$t('components.eio.exportFile')
    },
    btnTextVal () {
      return this.btnText || this.$t('common.export')
    }
  },
  methods: {
    async exportData () {
      await this.validateExport()
      return new Promise((resolve, reject) => {
        http({
          method: this.requstType,
          url: this.exprotUrl,
          timeout: this.timeout,
          data: this.filterParams,
          responseType: 'blob',
          loading: true,
          returnDirectly: true
        }).then(response => {
          const { data, headers } = response
          if (data.type === 'application/json') {
            // 将blob转为json
            const reader = new FileReader() // 创建一个FileReader实例
            reader.readAsText(data) // 读取文件,结果用字符串形式表示 , 'utf-8'
            reader.onload = function () { // 读取完成后,**获取reader.result**
              const { message = this.$t('components.eio.downloadFail') } = reader.result ? JSON.parse(reader.result) : {}
              Message({
                ...messageConfig,
                message: message,
                type: 'error',
                duration: 5 * 1000
              })
            }
            return
          }
          const blob = new Blob([data]) // 创建一个类文件对象：Blob对象表示一个不可变的、原始数据的类文件对象
          const url = window.URL.createObjectURL(blob) // URL.createObjectURL(object)表示生成一个File对象或Blob对象
          let dom = document.createElement('a') // 设置一个隐藏的a标签，href为输出流，设置download
          dom.style.display = 'none'
          dom.rel = 'noopener'
          dom.href = url
          dom.setAttribute('download', this.filenameText) // 指示浏览器下载url,而不是导航到它；因此将提示用户将其保存为本地文件
          document.body.appendChild(dom)
          dom.click()
          Message({
            ...messageConfig,
            message: this.$t('common.exportSuccess'),
            type: 'success',
            duration: 5 * 1000
          })
        })
          .catch(error => {
            reject(error)
          })
      })
    }
  },
  render (h) {
    return (
      <ElButton onClick={this.exportData} type={this.type} disabled={this.disabled}>
        {this.btnTextVal}
      </ElButton>
    )
  }
}
