<template>
  <div>
    <div v-if="!editMode">
      <input
        ref="addInvoiceRef"
        type="file"
        style="display:none;"
        multiple
        accept=".jpg,.png,.jpeg,.pdf,.ofd"
        @change="handleFileChange"
      >
      <el-button type="primary" class="detail-pbtn" :disabled="isReadonly" @click="addInvoiceInfo">
        {{ $t('purSettlementMod.addInvoice') }}
      </el-button>
    </div>

    <div v-if="editMode">
      <el-button type="text" :disabled="isReadonly" @click="editInvoice">
        {{ $t('common.edit') }}
      </el-button>
    </div>

    <uploadInvoice
      :fieldSupplement="fieldSupplement"
      :visible.sync="addInvoiceDialog"
      :headerText="headerText"
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
import uploadInvoice from './uploadInvoice'
import { getImgSrc } from 'lib@/utils/file'
import uploadUtils from './utils'

export default {
  name: 'InvoiceUpload',
  components: {
    uploadInvoice
  },
  mixins: [uploadUtils],
  props: {
    // 判断是新增模式还是编辑模式
    editMode: {
      type: Boolean,
      default () {
        return false
      }
    },
    // 行编辑对象
    editRow: {
      type: Object,
      default () {
        return {}
      }
    },
    // 按钮是否只读
    isReadonly: {
      type: Boolean,
      default () {
        return false
      }
    },
    // 发票头文字
    headerText: {
      type: Object,
      default () {
        return {
          title: this.$t('purSettlementMod.addInvoice'), // 新增发票
          content: this.$t('purSettlementMod.voiceTitle') // (发票仅支持JPG、PNG、JPEG、PDF、OFD格式，单个文件大小不超过10M)
        }
      }
    },
    // 发票明细表格
    invoiceInformation: {
      type: Array,
      default () {
        return []
      }
    },
    /**
     * 判断是什么功能进来的
     * 采购商开票：BUYER_INVOICE_SETTLE
     * 供应商开票：SUPPLIER_INVOICE_SETTLE
     * 采购商合同履约：BUYER_INVOICE_CONTRACT_PERFORMANCE
     * 供应商合同履约：SUPPLIER_INVOICE_CONTRACT_PERFORMANCE
     */
    fileFunction: {
      type: String,
      default: ''
    },
    // 附加补充字段,遵循baseform添加格式
    fieldSupplement: {
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
        fileFunction: this.fileFunction,
        fileType: 'images'
      },
      hasRepeat: false,
      fileList: []
    }
  },
  methods: {
    // 行编辑发票触发
    editInvoice () {
      this.hasRepeat = false // 重置判断重复变量
      this.addInvoiceDialog = true
      this.fileList = [{ ...this.editRow, img: getImgSrc(this.editRow.fileuploadId, this.editRow.fileSourceName) }]
    },
    // 编辑发票行
    editInvoiceRow (row) {
      for (let item of this.invoiceInformation) {
        if (item.fileuploadId === row.fileuploadId) {
          Object.assign(item, row)
          break
        }
      }
      this.addInvoiceDialog = false
      // 渲染引擎引用时候这里可能不触发更新，把这里方法抄过去调用
      this.$emit('editInvoiceRow', row)
    },
    // 新增发票 - 保存
    saveAllPage (fileList) {
      this.addInvoiceDialog = false
      this.$emit('saveInvoice', fileList)
    },
    async handleFileChange (event) {
      if (event.target.files.length > 5) {
        return this.$message.warning(this.$t('components.ocr.uploadLimit'))
      }

      let loading = this.$loading({
        lock: true,
        text: this.$t('components.ocr.waiting'),
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
      const { fileList, enable } = await this.handleUploadFile(formData, this.fileList, loading, event)

      this.fileList = fileList.map(item => {
        item.commodityTaxRate = (item.commodityTaxRateHundred || item.commodityTaxRate)
        return { ...item, img: getImgSrc(item.fileuploadId, item.fileSourceName) }
      })

      // 校验重复
      this.checkRepeatMes()
      // 校验上传报错, enable为true标识ocr服务打开
      if (enable) {
        this.checkUploadFail()
      } else {
        // 打开弹窗
        this.addInvoiceDialog = true
      }
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
    // 校验重复
    checkRepeatMes () {
      const { hasRepeat, fileList } = this.checkRepeat(this.invoiceInformation, this.fileList, 'invoiceNum')
      this.fileList = fileList
      this.hasRepeat = hasRepeat
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
        let indexList = fails.join('、')
        await this.$confirm(
          this.$t('components.ocr.diTip') + `${indexList}` + this.$t('components.ocr.ocrError'),
          {
            confirmButtonText: this.$t('components.ocr.enterKey'),
            cancelButtonText: this.$t('components.common.delete'),
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
    addInvoiceInfo () {
      this.fileList = [] // 每次打开清空上次遗留数据
      this.hasRepeat = false // 重置判断重复变量
      this.$refs.addInvoiceRef.dispatchEvent(new MouseEvent('click'))
    }
  }
}
</script>
