<template>
  <div class="uploadTableDialog">
    <!-- 新增发票 - 弹窗 -->
    <srm-dialog
      :title="$t('purSettlementMod.addInvoice')"
      size="large"
      :visible.sync="addInvoiceDialog"
      :close-on-click-modal="false"
      :destroy-on-close="true"
      v-bind="$attrs"
      :before-close="closeDialog"
      v-on="$listeners"
    >
      <template slot="header">
        <div style="font-size: 18px;">
          <span>{{ $t('purSettlementMod.addInvoice') }}</span>
          <!-- （发票仅支持JPG、PNG、JPEG、PDF、OFD格式，单个文件大小不超过10M） -->
          <span class="voice-title">{{ $t('purSettlementMod.voiceTitle') }}</span>
        </div>
      </template>
      <!-- 有发票 -->
      <div v-show="hasInvoice" class="container">
        <div v-if="!editModeV" class="invoice-list-img">
          <!-- 全部保存时消息提示 -->
          <div :style="{'visibility': tempFileList.length > 1 ? 'visible' : 'hidden'}" class="save-check-msg">
            {{ checkAllMsg.msg }}
          </div>
          <div class="catalog-scroll-img" :style="{'justify-content': lengthMoreOne ? 'space-between' : 'left' }">
            <div v-if="lengthMoreOne" class="img-arrow" @click="arrowImg('pre')">
              <svg-icon
                icon-class="left"
                class-name="left-icon"
              />
            </div>
            <ul class="small-img-list" :style="{'width': lengthMoreOne ? '90%' : '17%' }">
              <li
                v-for="(item, i) in tempFileList"
                :ref="`imgRef${i}`"
                :key="i"
                class="item-small-img"
                @click="selectImgInfor(i, 'click')"
              >
                <img width="100%" :src="item.img" :alt="item.fileSourceName">

                <div class="img-cover" @click="viewBigImage(item)">
                  {{ $t('components.ocr.bigImg') }}
                </div>

                <!-- 删除 -->
                <div class="delete-item" @click="deleteItem(i)">
                  <!-- <em class="delete-icon">×</em> -->
                  <div v-if="lengthMoreOne" class="delete-icon" @click="arrowImg('pre')">
                    <svg-icon
                      icon-class="close"
                      class-name="close-icon"
                    />
                  </div>
                </div>
                <!-- 序号 -->
                <div :ref="`sortRef${i}`" class="sort-item">
                  <div class="sort-number">
                    {{ i + 1 }}
                  </div>
                </div>
              </li>
            </ul>
            <div v-if="tempFileList.length > 1" class="img-arrow" @click="arrowImg('next')">
              <svg-icon
                icon-class="right"
                class-name="right-icon"
              />
            </div>

            <!-- 新增发票 -->
            <div class="add-img-btn">
              <svg-icon
                icon-class="add"
                class-name="add-icon"
              />
              <input
                ref="addInvoiceRef"
                class="upload-input"
                type="file"
                multiple
                accept=".jpg,.png,.jpeg,.pdf,.ofd"
                @change="uploadFiles"
              >
            </div>
          </div>
        </div>

        <!-- 发票编辑时图片显示 -->
        <div v-else class="edit-invoice">
          <div class="center-element">
            <img width="100%" :src="tempFileList[0].img" :alt="tempFileList[0].fileSourceName">

            <div class="img-cover" @click="viewBigImage(tempFileList[0])">
              {{ $t('components.ocr.bigImg') }}
            </div>
          </div>
          <strong class="img-name">
            {{ tempFileList[0].fileSourceName }}
          </strong>
        </div>

        <!-- 表单 -->
        <BaseForm
          ref="formRef"
          class="invoice-row"
          :form-items="formItems"
          :merge-form.sync="form"
          form-name="form"
          :wrapper-col="{ span: 8, gutter: 27 }"
        />
      </div>

      <!-- 无发票 -->
      <div v-if="!hasInvoice" class="no-data">
        <svg-icon
          icon-class="no-data"
          class-name="no-data-icon"
        />
        <p>{{ $t('common.noData') }}</p>
        <el-button type="primary" @click="addInvoice">
          {{ $t('components.ocr.addInvoice') }}
        </el-button>
      </div>

      <span slot="footer" class="dialog-footer">
        <el-button @click="closeDialog">{{ $t('common.cancel') }}</el-button>
        <el-button v-if="editModeV" type="primary" @click="editInvoiceRow">{{ $t('common.save') }}</el-button>
        <el-button v-if="hasInvoice && !editModeV" class="ml8" :type="tempFileList.length < 2 ? 'primary' : 'default'" @click="saveCurrent">{{ $t('components.ocr.saveCurPage') }}</el-button>
        <el-button v-if="tempFileList.length > 1" class="ml8" type="primary" @click="saveAllPage">{{ $t('components.ocr.saveAll') }}</el-button>
      </span>
    </srm-dialog>

    <!--附件预览-->
    <FilePreview
      v-if="viewBigImageVisible"
      :visible.sync="viewBigImageVisible"
      :fileupload-id="form.fileuploadId"
      :file-name="form.fileSourceName"
      @cancel="viewBigImageVisible = false"
    />
  </div>
</template>

<script>
import BaseForm from 'lib@/components/BaseForm'
import FilePreview from 'lib@/components/filePreview'
import { getToken } from '@/utils/auth'
import axios from 'axios'
import { getImgSrc } from 'lib@/utils/file'
import { getMenuInfo } from '@/utils/menu-auth'
export default {
  name: 'UploadInvoice',
  components: {
    BaseForm,
    FilePreview
  },
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    headerText: {
      type: String,
      default () {
        return this.$t('purSettlementMod.upload') // 上传
      }
    },
    // 有无重复数据
    hasRepeat: {
      type: Boolean,
      default () {
        return false
      }
    },
    // 上传参数
    extraData: {
      type: Object,
      default () {
        return {}
      }
    },
    // 上传链接
    url: {
      type: String,
      default () {
        return ''
      }
    },
    // 格式
    accept: {
      type: Array,
      default () {
        return []
      }
    },
    // 发票上传的列表信息
    invoiceFiles: {
      type: Array,
      default () {
        return []
      }
    },
    // 行编辑模式
    editMode: {
      type: Boolean,
      default () {
        return false
      }
    },
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
      closeVisible: false,
      singleUpload: [], // 单次上传暂存空间
      editModeV: false,
      fileList: [],
      clickEvent: '',
      viewBigImageVisible: false,
      selectItem: {},
      tempFileList: [],
      imgIndex: 0,
      addInvoiceDialog: false,
      isSuccess: false,
      headers: {},
      accepts: '',
      form: {
        invoiceType: null,
        purchaserRegisterNum: null,
        invoiceCode: null,
        invoiceNum: null,
        invoiceDate: null,
        checkCode: null,
        purchaserName: null,
        sellerName: null,
        noTaxTotalAmount: null,
        totalTax: null,
        totalAmount: null,
        invoiceCourierNo: null
      },
      formItems: [
        {
          tag: 'input',
          itemAttrs: {
            label: this.$t('agentOnlineInvoice.invoiceName'), // 发票名称
            rules: [{ required: true, message: this.$t('components.ocr.enterInvoiceName') }]
          },
          computedUIAttrs: _ => {
            return {
              key: 'invoiceName'
            }
          }
        },
        {
          tag: 'dictSelect',
          itemAttrs: {
            label: this.$t('purSettlementMod.invoiceType'), // 增值税发票类型
            rules: [{ required: true, message: this.$t('components.ocr.selectInvoiceType') }]
          },
          listeners: {
            'change-value': (val, element) => this.selectInvoiceType(val, element)
          },
          computedUIAttrs: _ => {
            return {
              key: 'invoiceType',
              code: 'INVOICE_TYPE'
            }
          }
        },
        {
          tag: 'input',
          itemAttrs: {
            label: this.$t('purSettlementMod.purchaserRegisterNum'), // 采购方税号
            rules: [{ required: true, message: this.$t('components.ocr.enterPurchaserRegisterNum') }]
          },
          computedUIAttrs: _ => {
            return {
              key: 'purchaserRegisterNum'
            }
          }
        },
        {
          tag: 'input',
          itemAttrs: {
            label: this.$t('purSettlementMod.invoiceCode'), // 发票代码
            rules: [{ required: true, message: this.$t('components.ocr.fillInvoiceCode') }]
          },
          computedUIAttrs: _ => {
            return {
              key: 'invoiceCode'
            }
          }
        },
        {
          tag: 'input',
          itemAttrs: {
            label: this.$t('purSettlementMod.invoiceNum'), // 发票号码
            rules: [{ required: true, message: this.$t('components.ocr.fillinvoiceNum') }]
          },
          computedUIAttrs: _ => {
            return {
              key: 'invoiceNum'
            }
          }
        },
        {
          tag: 'date',
          itemAttrs: {
            label: this.$t('purSettlementMod.invoiceDate2'), // 开票日期
            rules: [{ required: true, message: this.$t('components.ocr.fillinvoiceDate') }]
          },
          computedUIAttrs: _ => {
            return {
              key: 'invoiceDate'
            }
          }
        },
        {
          tag: 'input',
          itemAttrs: {
            label: this.$t('purSettlementMod.checkCode') // 校验码

          },
          computedItemAttrs: _ => {
            return {
              rules: [{ required: this.isCheckCode, message: this.$t('components.ocr.fillcheckCode') }]
            }
          },
          computedUIAttrs: _ => {
            return {
              key: 'checkCode'
            }
          }
        },
        {
          tag: 'input',
          itemAttrs: {
            label: this.$t('purSettlementMod.purchaserName'), // 采购方
            rules: [{ required: true, message: this.$t('components.ocr.fillpurchaserName') }]
          },
          computedUIAttrs: _ => {
            return {
              key: 'purchaserName'
            }
          }
        },
        {
          tag: 'input',
          itemAttrs: {
            label: this.$t('purSettlementMod.sellerName2'), // 供方
            rules: [{ required: true, message: this.$t('components.ocr.fillSellerName') }]
          },
          computedUIAttrs: _ => {
            return {
              key: 'sellerName'
            }
          }
        },
        {
          tag: 'input',
          itemAttrs: {
            label: this.$t('purSettlementMod.noTaxTotalAmount2'), // 未税金额
            rules: [{ required: true, message: this.$t('components.ocr.fillnoTaxTotalAmount') }]
          },
          computedUIAttrs: _ => {
            return {
              key: 'noTaxTotalAmount',
              type: 'number'
            }
          }
        },
        {
          tag: 'input',
          itemAttrs: {
            label: this.$t('purSettlementMod.totalTax'), // 税额
            rules: [{ required: true, message: this.$t('components.ocr.filltotalTax') }]
          },
          computedUIAttrs: _ => {
            return {
              key: 'totalTax',
              type: 'number'
            }
          }
        },
        {
          tag: 'input',
          itemAttrs: {
            label: this.$t('purSettlementMod.totalAmount'), // 含税金额
            rules: [{ required: true, message: this.$t('components.ocr.filltotalAmount') }]
          },
          computedUIAttrs: _ => {
            return {
              key: 'totalAmount',
              type: 'number'
            }
          }
        },
        {
          tag: 'input',
          itemAttrs: {
            label: this.$t('bidMod.taxRate'), // 税率
            rules: [{ required: true, validator: this.checkTaxRate }]
          },
          computedUIAttrs: _ => {
            return {
              key: 'commodityTaxRate',
              type: 'number'
              // step: '0.01'
            }
          }
        },
        {
          tag: 'input',
          itemAttrs: {
            label: this.$t('components.ocr.sellerRegisterNum'), // 销售方纳税人识别号
            rules: [{ required: true, message: this.$t('components.ocr.fillsellerRegisterNum') }]
          },
          computedUIAttrs: _ => {
            return {
              key: 'sellerRegisterNum'
            }
          }
        },
        {
          tag: 'input',
          itemAttrs: {
            label: this.$t('purSettlementMod.invoiceCourierNo') // 发票快递单号
          },
          computedUIAttrs: _ => {
            return {
              key: 'invoiceCourierNo'
            }
          }
        },
        {
          tag: 'input',
          itemAttrs: {
            label: this.$t('components.ocr.amountInFigures') // 价税合计(小写)
          },
          computedUIAttrs: _ => {
            return {
              key: 'amountInFigures',
              type: 'number'
            }
          }
        },
        {
          tag: 'input',
          itemAttrs: {
            label: this.$t('components.ocr.amountInWords') // 价税合计(大写)
          },
          computedUIAttrs: _ => {
            return {
              key: 'amountInWords'
            }
          }
        }
      ]
    }
  },
  computed: {
    // 判断发票是否被删完
    hasInvoice () {
      return this.tempFileList.length > 0
    },
    lengthMoreOne () {
      return this.tempFileList.length > 1
    },
    // 判断是否需要校验码
    isCheckCode () {
      return this.form.invoiceType === '电子普通发票'
    },
    // 校验所有票据字段是否齐全
    checkAllMsg () {
      let msg = ''
      // checkAllMsg
      let checkForm = [
        'invoiceType',
        'purchaserRegisterNum',
        'invoiceCode',
        'invoiceNum',
        'invoiceDate',
        'purchaserName',
        'sellerName',
        'noTaxTotalAmount',
        'totalTax',
        'totalAmount'
      ]
      // invoiceType 电子增值税普通发票 checkCode
      let noSaveIndex = []
      for (let i = 0; i < this.tempFileList.length; i++) {
        const item = this.tempFileList[i]
        // 判断是不是这个类型，是就加必填，不是并且有这个字段就去掉
        if (item.invoiceType === '电子普通发票') {
          checkForm.push('checkCode')
        } else if (checkForm.includes('checkCode')) {
          checkForm.pop()
        }

        for (let key in item) {
          if (checkForm.includes(key)) {
            if (!item[key]) {
              noSaveIndex.push(i + 1)
              break
            }
          }
        }
      }
      console.log(noSaveIndex, 'noSaveIndex')
      if (noSaveIndex.length > 0) {
        msg = this.$t('components.ocr.diTip') + noSaveIndex.join('、') + this.$t('components.ocr.lessInfo')
      }

      return {
        msg,
        ids: noSaveIndex
      }
    }
  },
  watch: {
    // 监听弹窗打开关闭，初始化打开赋值及关闭时对象清空
    visible (sign) {
      this.addInvoiceDialog = sign
      this.editModeV = this.editMode
      this.tempFileList = this.invoiceFiles || []
      this.form = this.invoiceFiles[0] || {}
      this.fileList = JSON.parse(JSON.stringify(this.invoiceFiles))
      console.log(this.editMode, 'this.editMode')
      if (!sign) {
        this.editModeV = false
      }
      if (this.hasRepeat && sign) {
        setTimeout(() => {
          this.$message.warning(this.$t('components.ocr.deleteRepeat'))
        }, 500)
      }
    }
  },
  created () {
    let menuInfo = getMenuInfo()
    this.headers = {
      Authorization: `Bearer ${getToken()}`,
      // 获取本机ip
      ProxyClientIP: this.$store.getters.ip,
      'x-forwarded-for': this.$store.getters.ip,
      'X-Fun-Info': menuInfo.secretKey
      // 'Content-type': 'form-data'
    }
    this.accepts = `.${this.accept.join(', .')}`

    this.setFileInit()
  },
  methods: {
    checkTaxRate (rule, value, callback) {
      if (!value) {
        return callback(new Error(this.$t('components.ocr.filltax')))
      }
      // if (value < 0 || value > 1) {
      //   return callback(new Error('请输入范围 0-1 之间'))
      // }
      if (value < 0 || value > 100) {
        return callback(new Error(this.$t('components.ocr.fillRageNum')))
      }
      callback()
    },
    // 空的时候新增发票
    addInvoice () {
      this.$refs.addInvoiceRef.dispatchEvent(new MouseEvent('click'))
    },
    // 初始化聚焦第一个
    setFileInit () {
      if (this.$refs[`imgRef${this.imgIndex}`]) {
        this.$nextTick(() => {
          let imgRef = this.$refs[`imgRef${this.imgIndex}`][0]
          let sortRef = this.$refs[`sortRef${this.imgIndex}`][0]
          console.log(sortRef, 'sortRef')
          imgRef.style.borderColor = 'rgba(0,119,255,1)'
          sortRef.style.background = '#0077FF'
          sortRef.style.color = 'rgba(255, 255, 255, .8)'
        })
      } else {
        setTimeout(() => {
          this.setFileInit()
        }, 80)
      }
    },
    viewBigImage () {
      // 稍加延时避免打开时上张图闪现
      setTimeout(() => {
        this.viewBigImageVisible = true
      }, 300)
    },
    // 删除
    deleteItem (i) {
      this.clickEvent = 'delete' // 避免和click方法重复触发对象定位方法
      console.log('delete')
      this.tempFileList.splice(i, 1)
      this.fileList.splice(i, 1)
      // 首先排除队列删除的情况
      if (this.tempFileList.length < 1) {
        this.form = {}
        return
      }
      // 在不是开始和结束的边界情况下，将第 i 个视为聚焦对象
      let imgIndex = i
      // 定义起始边界，如果删除第一个对象，则定位对象为新的0号位
      if (i === 0) imgIndex = 0
      // 定义length仅有一个情况，只能显示序列0
      if (this.tempFileList.length === 1) imgIndex = 0
      // 假设删除最后一个，将对象聚焦在删除后的最后一个对象，即i-1
      if (this.tempFileList.length === i) imgIndex = i - 1
      console.log(imgIndex, ' imgIndex')
      this.setNextImg('click', imgIndex)
    },
    // 点击图片
    selectImgInfor (index, event) {
      // 防止点击删除时会触发
      if (this.clickEvent === 'delete' && !event) return
      this.clickEvent = ''

      this.setNextImg('click', index)
    },
    // 设置border颜色
    setNextImg (flag, index) {
      // 重置上一次border颜色
      let preImg = this.$refs[`imgRef${this.imgIndex}`][0]
      preImg.style.borderColor = '#B9C0C7'
      // 重置上一次序号颜色
      let preSort = this.$refs[`sortRef${this.imgIndex}`][0]
      preSort.style.background = 'rgba(0, 0, 0, 0.2)'
      preSort.style.color = 'rgba(0, 0, 0, 0.8)'
      // 指针移动，点击移动或者左右箭头移动
      if (index > -1) {
        this.imgIndex = index
      } else {
        this.imgIndex = flag === 'pre' ? (this.imgIndex - 1) : (this.imgIndex + 1)
      }

      let imgRef = this.$refs[`imgRef${this.imgIndex}`][0]
      let sortRef = this.$refs[`sortRef${this.imgIndex}`][0]
      imgRef.style.borderColor = 'rgba(0,119,255,1)'
      sortRef.style.background = '#0077FF'
      sortRef.style.color = 'rgba(255, 255, 255, 0.8)'
      console.log(sortRef, 'sortRef')
      // 选中的发票
      this.form = this.tempFileList.length >= 1 ? this.tempFileList[this.imgIndex] : {}
    },
    // 图片左右箭头
    arrowImg (flag) {
      if (flag === 'pre' && this.imgIndex < 1) return
      if (flag === 'next' && this.imgIndex === this.tempFileList.length - 1) return
      this.setNextImg(flag)
    },
    // 校验两个对象是否完全相同
    isObjectValueEqual (a, b) {
      // 判断两个对象是否指向同一内存，指向同一内存返回 true
      if (!!a || !!b) return false
      if (a === b) return true // 获取两个对象键值数组
      let aProps = Object.getOwnPropertyNames(a)
      let bProps = Object.getOwnPropertyNames(b)
      // 判断两个对象键值数组长度是否一致，不一致返回 false
      if (aProps.length !== bProps.length) return false // 遍历对象的键值
      for (let prop in a) {
        // 判断 a 的键值，在 b 中是否存在，不存在，返回 false
        if (b.hasOwnProperty(prop)) {
        // 判断 a 的键值是否为对象，是则递归，不是对象直接判断键值是否相等，不相等返回 false
          if (!!a[prop] && typeof a[prop] === 'object') {
            if (!this.isObjectValueEqual(a[prop], b[prop])) return false
          } else if (a[prop] !== b[prop]) {
            return false
          }
        } else {
          return false
        }
      }
      return true
    },
    // 关闭弹窗
    async closeDialog (done) {
      let map = new Map()

      this.fileList.forEach(item => {
        map.set(item.fileuploadId, item)
      })

      // 获取未保存的发票
      let noSaveIndex = this.tempFileList.map((item, i) => {
        if (map.get(item.fileuploadId)) {
          let sign = this.isObjectValueEqual(item, map.get(item.fileuploadId))
          if (!sign) return i + 1
        }
      }).filter(i => typeof i !== 'undefined')
      console.log(noSaveIndex, 'noSaveIndex')

      if (noSaveIndex.length > 0 && this.tempFileList.length > 1) {
        const confirmResult = await this.$confirm(
          (this.$t('components.ocr.diTip') + noSaveIndex + this.$t('components.ocr.notsaveleave')),
          {
            confirmButtonText: this.$t('common.confirm'),
            cancelButtonText: this.$t('common.cancel'),
            type: 'warning'
          }
        ).catch(() => { /* nothing */ })

        if (confirmResult !== 'confirm') {
          return
        }
      }

      this.$emit('closeInvoice', this.fileList, (this.$t('components.ocr.diTip') + noSaveIndex + this.$t('components.ocr.notsaveleave')))
    },
    // 编辑数据
    async editInvoiceRow () {
      const flag = await this.checkCurrentField()
      if (!flag) return
      this.$emit('editInvoiceRow', this.tempFileList[0])
      this.$emit('saveAllPage', this.tempFileList)
    },
    // 校验本页具体信息
    async checkCurrentField () {
      const { flag, obj } = await this.$refs.formRef.validate()
      if (!flag) {
        this.$message.warning(obj[Object.keys(obj)[0]][0])
        return false
      }
      return true
    },
    // 保存当前页
    async saveCurrent () {
      const flag = await this.checkCurrentField()
      if (!flag) return

      let sign = false
      if (this.fileList[this.imgIndex]) {
        // 保存时校验是否和缓存值相等，给fileList赋值
        sign = this.isObjectValueEqual(this.fileList[this.imgIndex], this.form)
        console.log(sign, 'saveCurrent')
      }
      if (!sign) {
        this.fileList[this.imgIndex] = this.form
      }

      // 当发票只有一张时只有保存当页按钮，这里要判断一张发票也要触发全部保存并关闭
      if (this.tempFileList.length === 1) {
        this.$emit('saveAllPage', this.tempFileList)
      }
      this.$message.success(this.$t('common.successSave'))
    },
    // 保存全部
    saveAllPage () {
      console.log(this.checkAllMsg, 'checkAllMsg')
      if (this.checkAllMsg.msg) {
        this.setNextImg('click', this.checkAllMsg.ids[0] - 1)
        return
      }
      this.fileList = this.tempFileList
      this.$emit('saveAllPage', this.tempFileList)
      this.$message.success(this.$t('common.successSave'))
    },
    // 增值税发票类型选择
    selectInvoiceType (v, e) {
      this.form.invoiceType = e.label
    },
    // 校验文件格式
    checkFileFormat (file) {
      console.log(file, 'checkFile')
      if (!file.type.includes('image') && !file.type.includes('pdf')) {
        return {
          status: 'error',
          msg: file.name + ': ' + this.$t('agentOnlineInvoice.prompt12')
        }
      }

      if (file.size / 1024 / 1024 > 10) {
        return {
          status: 'error',
          // 上传文件大小不能超过 10MB!
          msg: file.name + ': ' + this.$t('agentOnlineInvoice.prompt13') + ', ' + this.$t('purSettlementMod.uploadTableMes2')
        }
      }

      return {
        status: 'ok',
        data: file
      }
    },
    async uploadFiles (event) {
      let formData = new FormData()
      let files = event.target.files
      let listLength = this.tempFileList.length + files.length

      if (listLength > 50) {
        return this.$message.warning(this.$t('components.ocr.uploadLimit1'))
      }

      if (files.length > 5) {
        return this.$message.warning(this.$t('components.ocr.uploadLimit'))
      }

      let loading = this.$loading({
        lock: true,
        text: this.$t('agentOnlineInvoice.prompt18'),
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)',
        customClass: 'custom-color-loading'
      })

      Object.keys(this.extraData).forEach(key => {
        formData.append(key, this.extraData[key])
      })

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

      if (!formData.get('files')) {
        loading.close()
        loading = null // 重置loading
        event.target.value = '' // 重置上传list
        return
      }

      // 文件上传请求
      await this.uploadFileRequest(formData, loading, event)
    },
    // 文件上传请求
    async uploadFileRequest (formData, loading, event) {
      await axios({
        method: 'POST',
        url: this.url,
        headers: this.headers,
        data: formData
      }).then(res => {
        const data = res.data.data.data
        if (data) {
          // 获取成功数据

          if (data.successItems.length > 0) {
            this.tempFileList.push.apply(this.tempFileList, data.successItems)
          }
          // 获取识别失败数据
          if (data.failItems.length > 0) {
            let failItems = data.failItems.map(item => {
              return {
                ...item.failItem,
                ...item.failMsgs[0]
              }
            })
            this.tempFileList.push.apply(this.tempFileList, failItems)
            this.singleUpload = failItems
          }
          this.tempFileList = this.tempFileList.map(item => {
            return { ...item, img: getImgSrc(item.fileuploadId, item.fileSourceName) }
          })

          this.form = this.tempFileList[this.imgIndex]
          // 校验上传报错
          this.checkUploadFail()
        }
        loading.close()
        loading = null // 重置loading
        event.target.value = ''
      })
        .catch(() => {
          loading.close()
          loading = null // 重置loading
          event.target.value = ''
          this.$message.error(this.$t('agentOnlineInvoice.prompt8'))
        })
    },
    // 校验重复
    checkRepeatMes () {
      let oldLength = this.tempFileList.length
      let newCheckList = [...this.invoiceInformation, ...this.tempFileList]
      let ids = newCheckList.map(item => item.invoiceNum)
      let newArr = []
      // 先拿到两个数组合并后去重的数据
      let noRepeatArr = newCheckList.filter((item, index) => {
        return ids.indexOf(item.invoiceNum) === index || !item.invoiceNum
      })
      // 再去除明细变数据就是剩余新添数据
      noRepeatArr.forEach((row, index) => {
        let findArr = this.invoiceInformation.find(item => {
          return item.invoiceNum === row.invoiceNum
        })
        if (!findArr) {
          newArr.push(row)
        }
      })
      this.tempFileList = newArr
      // this.imgIndex = 0 // 删除识别错误和去重后位置会乱，重置为0
      // this.form = this.tempFileList[0]
      this.setNextImg('click', 0)

      if (this.tempFileList.length < oldLength) {
        this.$message.warning(this.$t('components.ocr.delectRepeatData'))
      }
      console.log(newArr, 'newArr')
    },
    // 检验上传失败, 保留则通过，删除则过滤
    async checkUploadFail () {
      let deleteIds = []
      // 获取本次失败的序列号
      const fails = this.singleUpload.map(item => item.fileuploadId)

      this.tempFileList.map((item, i) => {
        if (fails.includes(item.fileuploadId)) {
          deleteIds.push(i + 1)
        }
      })

      if (deleteIds.length > 0) {
        await this.$confirm(
          (this.$t('agentOnlineInvoice.prompt9') + deleteIds.join('、') + this.$t('agentOnlineInvoice.prompt10')),
          {
            confirmButtonText: this.$t('agentOnlineInvoice.prompt11'),
            cancelButtonText: this.$t('common.delete'),
            type: 'warning'
          }
        ).catch(() => {
          console.log(deleteIds, 'deleteIds')
          deleteIds.reverse().map(i => {
            this.deleteItem(i - 1) // 遍历删除
          })
        })
      }
      console.log('deleteDown')
      // 数据去重
      this.checkRepeatMes()
    }
  }
}
</script>

<style lang="scss" scoped>
::v-deep.uploadTableDialog {
  .srm-dialog-content {
    padding-top: 4px !important;
  }
  .save-check-msg {
    font-size: 12px;
    color: #FF4A4D;
    height: 20px;
  }

  .catalog-scroll-img {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 18px;
    .img-arrow {
      width: 24px;
      height: 90px;
      color: #73777C;
      background: #F6F6F6;
      display: flex;
      justify-content: center;
      align-items: center;
      cursor: pointer;
      overflow: hidden;
      &:hover .left-icon {
        filter: drop-shadow(rgba(0,119,255,1) 80px 0);
        transform: translateX(-80px);
      }
      &:hover .right-icon {
        filter: drop-shadow(rgba(0,119,255,1) 80px 0);
        transform: translateX(-80px);
      }
    }

    .small-img-list {
      display: flex;
      flex-wrap: nowrap;
      width: 90%;
      margin: 0 2px;
      padding: 0;
      overflow-x: auto;
      overflow-y: hidden;
      li {
        flex: none;
        width: 160px;
        height: 90px;
        padding: 0 12px;
        margin-right: 6px;
        // border-radius: 4px;
        overflow: hidden;
        border: 1px solid #F6F6F6;
        background: #F6F6F6;
        cursor: pointer;
        display: flex;
        justify-content: center;
        align-items: center;
        position: relative;
        &:focus {
          border: 1px solid rgba(0,119,255,1)
        }
        &:hover {
          border: 1px solid rgba(0,119,255,1)
        }
        &:last-child {
          margin-right: 0;
        }
      }
      .img-cover {
        width: 100%;
        height: 25%;
        font-size: 12px;
        color: #DCDDDE;
        background: rgba(0, 0, 0, .5);
        position: absolute;
        left: 0;
        bottom: 0;
        display: flex;
        justify-content: center;
        align-items: center;
        opacity: 0;
      }
      li:hover .img-cover {
        transition: all 0.5s;
        opacity: 1;
      }

      .delete-item {
        position: absolute;
        right: -20px;
        top: -20px;
        width: 40px;
        height: 40px;
        border-radius: 50%;
        background: rgba(0, 0, 0, 0.4);
        .delete-icon {
          color: rgba(255, 255, 255, 0.8);
          position: absolute;
          bottom: 5px;
          left: 6px;
          font-size: 12px;
        }
      }

      .sort-item {
        position: absolute;
        left: -20px;
        top: -20px;
        width: 40px;
        height: 40px;
        border-radius: 50%;
        background: rgba(0, 0, 0, 0.2);
        .sort-number {
          position: absolute;
          bottom: 5px;
          right: 8px;
          font-size: 12px;
        }
      }
      li:hover .sort-item {
        transition: all 0.2s;
        opacity: 1;
        background: #0077FF !important;
        .sort-number {
          color: rgba(255, 255, 255, 0.8);
        }
      }
    }

    .add-img-btn {
      width: 28px;
      height: 28px;
      overflow: hidden;
      border-radius: 50%;
      text-align: center;
      line-height: 20px;
      position: relative;
      .add-icon {
        font-size: 28px;
      }
      .upload-input {
        display: inline-block;
        width: 28px;
        height: 28px;
        opacity: 0;
        position: absolute;
        top: 0;
        left: 0;
      }
    }
  }

  .no-data {
    width: 100%;
    height: 100%;
    text-align: center;
    padding-top: 100px;
    .no-data-icon {
      font-size: 100px;
    }
    p {
      font-size: 12px;
      color: #96999C;
    }
  }

  .edit-invoice {
    width: 100%;
    display: flex;
    align-items: center;
    flex-direction: column;
    margin-bottom: 12px;
    .center-element {
      width: 160px;
      height: 90px;
      overflow: hidden;
      border: 1px solid #F6F6F6;
      background: #F6F6F6;
      margin-bottom: 4px;
      cursor: pointer;
      position: relative;
      &:focus {
        border: 1px solid rgba(0,119,255,1)
      }
      &:hover {
        border: 1px solid rgba(0,119,255,1)
      }

      .img-cover {
        width: 100%;
        height: 25%;
        font-size: 12px;
        color: #DCDDDE;
        background: rgba(0, 0, 0, .5);
        position: absolute;
        left: 0;
        bottom: 0;
        display: flex;
        justify-content: center;
        align-items: center;
        opacity: 0;
      }
    }
    .center-element:hover .img-cover {
      transition: all 0.5s;
      opacity: 1;
    }

    .img-name {
      color: rgba(0, 0, 0, .7);
      font-size: 12px;
      text-align: center;
      line-height: 20px;
    }
  }

  .srm-dialog-content {
    max-height: 450px !important;
  }

  .ml8{
   margin-left: 8px !important;
  }
}
</style>

<style>
.custom-color-loading .el-loading-text {
  color: rgba(255, 255, 255, .6) !important;
}
</style>
