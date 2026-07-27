<template>
  <div>
    <input
      ref="addInvoiceRef"
      type="file"
      style="display:none;"
      multiple
      accept=".jpg,.png,.jpeg,.pdf,.ofd"
      @change="handleFileChange"
    >
    <el-button type="primary" class="detail-pbtn" @click="addInvoiceInfo">
      {{ $t('purSettlementMod.addInvoice') }}
    </el-button>

    <uploadInvoice
      :visible.sync="addInvoiceDialog"
      :headerText="$t('purSettlementMod.uploadInvoice')"
      :url="uploadUrl"
      :invoiceFiles="fileList"
      :extraData="extraData"
      :hasRepeat="hasRepeat"
      :editMode="editMode"
      :invoiceInformation="invoiceInformation"
      :accept="['jpg', 'png', 'jpeg', 'pdf','.ofd']"
      @closeInvoice="addInvoiceDialog = false"
      @editInvoiceRow="editInvoiceRow"
      @saveAllPage="saveAllPage"
    />
  </div>
</template>
<script>
import uploadInvoice from '@/library/composition/purSettlement/uploadInvoice'
import { sysPrefix } from '@/config/ipConfig'
import { downloadWithParam, getImgSrc } from 'lib@/utils/file'
import axios from 'axios'
import { getToken } from '@/utils/auth'
import { getMenuInfo } from '@/utils/menu-auth'

export default {
  name: 'AddInvoiceInfo',
  components: {
    uploadInvoice
  },
  props: {
    // 发票明细表格
    invoiceInformation: {
      type: Array,
      default () {
        return []
      }
    }
  },
  data () {
    return {
      addInvoiceDialog: false,
      extraData: {
        sourceType: 'WEB_APP',
        uploadType: 'DEF',
        fileModular: 'sup-ce',
        fileFunction: 'onlineInvoice',
        fileType: 'images'
      },
      editMode: false,
      hasRepeat: false,
      fileList: [],
      uploadUrl: `${sysPrefix()}/api-sup-ce/ps/invoice/onlineInvoice/batchUploadAndVatInvoice`
    }
  },
  methods: {
    // 编辑发票行
    editInvoiceRow (row) {
      this.addInvoiceDialog = false
      this.$emit('editInvoiceRow', row)
    },
    // 新增发票 - 保存
    saveAllPage (fileList) {
      this.addInvoiceDialog = false
      this.$emit('saveInvoice', fileList)
    },
    async handleFileChange (event) {
      if (event.target.files.length > 5) {
        return this.$message.warning(this.$t('agentOnlineInvoice.prompt6')) // 单次上传数量不能超过5张
      }

      let loading = this.$loading({
        lock: true,
        text: this.$t('agentOnlineInvoice.prompt7'), // 上传中，请耐心等待
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)',
        customClass: 'custom-color-loading'
      })

      let formData = await this.getFormData(event, loading)
      if (!formData.get('files')) {
        loading.close()
        loading = null // 重置loading
        event.target.value = '' // 重置上传list
        return
      }
      // 上传接口请求
      let list = await this.uploadFileRequest(formData, loading)
      if (list.length === 0) return this.$message.error(this.$t('agentOnlineInvoice.prompt8')) // 上传失败
      // 校验重复
      this.checkRepeatMes()
      // 校验ocr识别失败
      this.checkUploadFail()
      // 重置上传list
      event.target.value = ''
    },
    // 获取上传文件参数
    getFormData (event, loading) {
      let formData = new FormData()
      Object.keys(this.extraData).forEach(key => {
        formData.append(key, this.extraData[key])
      })

      let files = this.$refs.addInvoiceRef.files

      for (let file of files) {
        // 校验图片格式，限制上传图片类型及大小
        let obj = this.checkFileFormat(file)
        if (obj.status === 'error') {
          // '发票仅支持JPG、PNG、JPEG、PDF、OFD格式，单个文件大小不超过10M'
          this.$message.warning(this.$t('purSettlementMod.voiceTipMessage'))
          break
        } else {
          formData.append('files', obj.data)
        }
      }

      return formData
    },
    // 文件上传接口请求
    async uploadFileRequest (formData, loading) {
      let menuInfo = getMenuInfo()
      await axios({
        method: 'POST',
        url: this.uploadUrl,
        headers: {
          Authorization: `Bearer ${getToken()}`,
          // 获取本机ip
          ProxyClientIP: this.$store.getters.ip,
          'x-forwarded-for': this.$store.getters.ip,
          'X-Fun-Info': menuInfo.secretKey
        },
        data: formData
      }).then(res => {
        const data = res.data.data.data
        if (data) {
          // 获取成功数据
          if (data.successItems.length > 0) this.fileList.push.apply(this.fileList, data.successItems)
          // 获取识别失败数据
          if (data.failItems.length > 0) {
            let failItems = data.failItems.map(item => {
              return {
                ...item.failItem,
                ...item.failMsgs[0]
              }
            })
            this.fileList.push.apply(this.fileList, failItems)
          }

          this.fileList = this.fileList.map(item => {
            return { ...item, commodityTaxRate: item.commodityTaxRate * 100, img: getImgSrc(item.fileuploadId, item.fileSourceName) }
          })
        }
        loading.close()
        loading = null // 重置loading
      })
        .catch(() => {
          loading.close()
          loading = null // 重置loading
        })

      return this.fileList.length > 0 ? this.fileList : []
    },

    // 校验重复
    checkRepeatMes () {
      const ids = this.fileList.map(item => item.purchaserRegisterNum)
      this.fileList = this.fileList.filter((item, index) => {
        if (ids.indexOf(item.purchaserRegisterNum) === index || !item.purchaserRegisterNum) {
          return true
        } else {
          this.hasRepeat = true
        }
      })
    },
    // 检验上传失败, 保留则通过，删除则过滤
    async checkUploadFail () {
      let fails = [] // 获取失败的序列号
      this.fileList.map((item, i) => {
        if (item.message) {
          fails.push(i + 1)
        }
      })
      if (fails.length > 0) {
        await this.$confirm(
          // 第x张识别失败，票据不清晰或为不可识别类型
          `${this.$t('agentOnlineInvoice.prompt9')}${fails.join('、')}${this.$t('agentOnlineInvoice.prompt10')}`,
          {
            confirmButtonText: this.$t('agentOnlineInvoice.prompt11'), // 保留并手动录入
            cancelButtonText: this.$t('common.delete'), // 删除
            type: 'warning'
          }
        ).catch(() => {
          fails.reverse().map(i => {
            this.fileList.splice(i - 1, 1)
          })
        })
      }

      // 打开弹窗
      this.addInvoiceDialog = true
    },
    // 校验文件格式
    checkFileFormat (file) {
      console.log(file, 'checkFile')
      if (!file.type.includes('image') && !file.type.includes('pdf')) {
        return {
          status: 'error',
          msg: `${file.name}: ${this.$t('agentOnlineInvoice.prompt12')}` // 上传失败,仅支持JPG、PNG、JPEG、PDF、OFD格式
        }
      }

      if (file.size / 1024 / 1024 > 10) {
        return {
          status: 'error',
          msg: `${file.name}: ${this.$t('agentOnlineInvoice.prompt13')}, ${this.$t('purSettlementMod.uploadTableMes2')}` // 上传失败,上传文件大小不能超过 10MB!
        }
      }

      return {
        status: 'ok',
        data: file
      }
    },
    addInvoiceInfo () {
      this.fileList = [] // 每次打开清空上次遗留数据
      this.hasRepeat = false // 重置判断重复变量
      this.editMode = false // 重置编辑模式
      this.$refs.addInvoiceRef.dispatchEvent(new MouseEvent('click'))
    }
  }
}
</script>
