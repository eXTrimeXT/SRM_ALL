<template>
  <div class="upload-invoice-dialog">
    <!-- 新增发票 - 弹窗 -->
    <srm-dialog
      size="large"
      top="8vh"
      :append-to-body="true"
      :title="$t('purSettlementMod.addInvoice')"
      :visible.sync="addInvoiceDialog"
      :close-on-click-modal="false"
      :destroy-on-close="true"
      :before-close="closeDialog"
      v-bind="$attrs"
      v-on="$listeners"
    >
      <template slot="header">
        <span class="header-title">{{ headerText.title }}</span>
        <span class="header-content">{{ headerText.content }}</span>
      </template>
      <div v-if="addInvoiceDialog" class="upload-invoice-dialog-content">
        <!-- 有发票 -->
        <div v-show="hasInvoice" class="container">
          <div v-if="!editModeV" class="invoice-list-img">
            <!-- 全部保存时消息提示 -->
            <div :style="{ 'visibility': tempFileList.length > 1 ? 'visible' : 'hidden' }" class="save-check-msg">
              {{ checkAllMsg.msg }}
            </div>
            <div class="catalog-scroll-img" :style="{ 'justify-content': lengthMoreOne ? 'space-between' : 'left' }">
              <div v-if="lengthMoreOne" class="img-arrow" @click="arrowImg('pre')">
                <svg-icon icon-class="left" class-name="left-icon" />
              </div>
              <ul class="small-img-list" :style="{ 'width': lengthMoreOne ? '90%' : '17%' }">
                <li
                  v-for="(item, i) in tempFileList"
                  :ref="`imgRef${i}`"
                  :key="i"
                  :class="['item-small-img',{'currentImage': imgIndex == i}]"
                  @click="selectImgInfor(i, 'click')"
                >
                  <img width="100%" :src="item.img" :alt="item.fileSourceName">

                  <div class="img-cover" @click="viewBigImage(item)">
                    {{ $t('components.ocr.bigImg') }}
                  </div>

                  <!-- 删除 -->
                  <div class="delete-item">
                    <div class="delete-icon" @click="deleteItem(i)">
                      <svg-icon icon-class="close" class-name="close-icon" />
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
                <svg-icon icon-class="right" class-name="right-icon" />
              </div>

              <!-- 新增发票 -->
              <div class="add-img-btn">
                <svg-icon icon-class="add" class-name="add-icon" />
                <input
                  ref="addInvoiceRef"
                  class="upload-input"
                  type="file"
                  multiple
                  :accept="accepts"
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
            :wrapper-col="{ span: 6, gutter: 20 }"
            validate-on-rule-change
          />
        </div>

        <!-- 无发票 -->
        <div v-if="!hasInvoice" class="no-data">
          <svg-icon icon-class="no-data" class-name="no-data-icon" />
          <p>{{ $t('common.noData') }}</p>
          <el-button type="primary" @click="addInvoice">
            {{ $t('components.ocr.addInvoice') }}
          </el-button>
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="closeDialog">{{ $t('common.cancel') }}</el-button>
        <el-button v-if="editModeV" type="primary" @click="editInvoiceRow">{{ $t('common.save') }}</el-button>
        <el-button
          v-if="hasInvoice && !editModeV"
          class="ml8"
          :type="tempFileList.length < 2 ? 'primary' : 'default'"
          @click="saveCurrent"
        >{{ $t('components.ocr.saveCurPage') }}</el-button>
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
import isEqual from 'lodash/isEqual'
import BaseForm from 'lib@/components/BaseForm'
import FilePreview from 'lib@/components/filePreview'
import { getImgSrc } from 'lib@/utils/file'
import { processNumericValue } from 'lib@/utils/util'
import uploadUtils from './utils'
export default {
  name: 'UploadInvoice',
  components: {
    BaseForm,
    FilePreview
  },
  mixins: [uploadUtils],
  props: {
    visible: {
      type: Boolean,
      default: false
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
    },
    // 附加补充字段
    fieldSupplement: {
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
            label: this.$t('components.ocr.invoiceName'), // 发票名称
            rules: [{ required: true, message: this.$t('components.ocr.enterInvoiceName') }]
          },
          computedUIAttrs: _ => {
            return {
              key: 'invoiceName',
              maxlength: 50,
              showWordLimit: true
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
            rules: [{ required: true, message: this.$t('components.ocr.fillPurchaserRegisterNum') }]
          },
          computedUIAttrs: _ => {
            return {
              key: 'purchaserRegisterNum',
              maxlength: 50,
              showWordLimit: true
            }
          }
        },
        {
          tag: 'input',
          itemAttrs: {
            label: this.$t('purSettlementMod.invoiceCode') // 发票代码
          },
          computedItemAttrs: _ => {
            return {
              rules: [{ required: this.isInvoiceCode, message: this.$t('components.ocr.fillInvoiceCode'), validator: this.checkInvoiceCode }]
            }
          },
          computedUIAttrs: _ => {
            return {
              key: 'invoiceCode',
              maxlength: 50,
              showWordLimit: true
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
              key: 'invoiceNum',
              maxlength: 50,
              showWordLimit: true
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
              key: 'invoiceDate',
              'value-format': 'yyyy-MM-dd'
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
              rules: [{ required: this.isCheckCode, message: this.$t('components.ocr.fillcheckCode'), validator: this.checkCodeValidator }]
            }
          },
          computedUIAttrs: _ => {
            return {
              key: 'checkCode',
              maxlength: 50,
              showWordLimit: true
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
              key: 'purchaserName',
              maxlength: 50,
              showWordLimit: true
            }
          }
        },
        {
          tag: 'input',
          itemAttrs: {
            label: this.$t('purSettlementMod.sellerName2'), // 供方
            rules: [{ required: true, message: this.$t('components.ocr.fillsellerName') }]
          },
          computedUIAttrs: _ => {
            return {
              key: 'sellerName',
              maxlength: 50,
              showWordLimit: true
            }
          }
        },
        {
          tag: 'input',
          itemAttrs: {
            label: this.$t('purSettlementMod.noTaxTotalAmount2'), // 未税金额
            rules: [{ required: true, message: this.$t('components.ocr.fillnoTaxTotalAmount') }]
          },
          listeners: {
            'change': (val) => this.calTaxAmount(val)
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
            label: this.$t('bidMod.taxRate'), // 税率
            rules: [{ required: true, validator: this.checkTaxRate }]
          },
          listeners: {
            'change': (val) => this.calTaxAmount(val)
          },
          computedUIAttrs: _ => {
            return {
              key: 'commodityTaxRate', // commodityTaxRate
              type: 'number'
              // step: '0.01'
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
              type: 'number',
              disabled: true
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
              type: 'number',
              disabled: true
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
              key: 'sellerRegisterNum',
              maxlength: 50,
              showWordLimit: true
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
              key: 'invoiceCourierNo',
              maxlength: 50,
              showWordLimit: true
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
        },
        ...this.fieldSupplement
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
    // 判断是否需要校验码, '电子普通发票'需要校验码
    isCheckCode () {
      return this.form.invoiceType === '003'
    },
    // 001 普通发票 | 002 专用发票 | 003 电子普通发票
    // 004 电子专用发票 | 005 电子发票(专用发票) | 006 电子发票(普通发票)
    // 判断发票代码是否必填
    isInvoiceCode () {
      return !!this.form.invoiceType && !['005', '006'].includes(this.form.invoiceType)
    },
    // 校验所有票据字段是否齐全
    checkAllMsg () {
      let msg = ''
      // invoiceType 电子增值税普通发票 checkCode
      let noSaveIndex = []
      const checkFormField = this.getCheckFields() // 获取每个file表单必填字段
      for (let i = 0; i < this.tempFileList.length; i++) {
        let item = this.tempFileList[i]
        for (let key in item) {
          if (checkFormField[item.fileuploadId].includes(key)) {
            if (!item[key]) {
              noSaveIndex.push(i + 1)
              break
            }
          }
        }
      }

      if (noSaveIndex.length > 0) {
        msg = this.$t('components.ocr.diTip') + `${noSaveIndex.join('、')}` + this.$t('components.ocr.lessInfo')
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
      this.editModeV = sign ? this.editMode : false
      // 税率处理
      this.tempFileList = this.invoiceFiles || []
      this.form = this.tempFileList[0] || {}
      this.fileList = JSON.parse(JSON.stringify(this.invoiceFiles))
      if (this.hasRepeat && sign) {
        setTimeout(() => {
          this.$message.warning(this.$t('components.ocr.deleteRepeat'))
        }, 500)
      }

      if (sign) {
        setTimeout(() => {
          this.setNextImg('add', 0)
          this.$forceUpdate()
        }, 500)
      }
    }
  },
  created () {
    this.accepts = `.${this.accept.join(', .')}`
  },
  methods: {
    // 未税金额税率change计算
    calTaxAmount (val) {
      // 含税金额=未税金额*(1+税率)
      this.form.totalAmount = this.form.noTaxTotalAmount * (1 + this.form.commodityTaxRate / 100) || 0
      // 税额=含税金额-未税金额
      this.form.totalTax = this.form.totalAmount - this.form.noTaxTotalAmount || 0

      // 限制小数位数
      this.form.totalAmount = processNumericValue(this.form.totalAmount)
      this.form.totalTax = processNumericValue(this.form.totalTax)
      this.form.noTaxTotalAmount = processNumericValue(this.form.noTaxTotalAmount)
      this.form.commodityTaxRate = processNumericValue(this.form.commodityTaxRate)
    },
    // 获取校验信息
    getCheckFields () {
      const checkFormField = {}
      for (const item of this.tempFileList) {
        checkFormField[item.fileuploadId] = []
        const isCheckCode = !!item.invoiceType && item.invoiceType === '003'
        const isInvoiceCode = !!item.invoiceType && !['005', '006'].includes(item.invoiceType)
        checkFormField[item.fileuploadId] = this.setItemConfig(isCheckCode, isInvoiceCode)
      }
      return checkFormField
    },
    // 获取formItems字段必填处理赋值数组数组
    setItemConfig (isCheckCode, isInvoiceCode) {
      const itemArr = []
      for (const { itemAttrs, computedUIAttrs } of this.formItems) {
        let ruleAttr = itemAttrs.rules
        if (ruleAttr) {
          if (computedUIAttrs().key === 'checkCode') {
            ruleAttr[0].required = isCheckCode
          }
          if (computedUIAttrs().key === 'invoiceCode') {
            ruleAttr[0].required = isInvoiceCode
          }
          if (ruleAttr[0].required) {
            itemArr.push(computedUIAttrs().key)
          }
        }
      }

      return itemArr
    },
    // 校验发票代码，避免触发过二次不触发
    checkInvoiceCode (rule, value, callback) {
      if (this.isInvoiceCode && !value) {
        callback(new Error(this.$t('components.ocr.fillInvoiceCode')))
      }
      callback()
    },
    // 校验码，避免触发过二次不触发
    checkCodeValidator (rule, value, callback) {
      if (this.isCheckCode && !value) {
        callback(new Error(this.$t('components.ocr.fillcheckCode')))
      }
      callback()
    },
    checkTaxRate (rule, value, callback) {
      if (!value) {
        callback(new Error(this.$t('components.ocr.filltax')))
      }
      if (value < 0 || value > 100) {
        callback(new Error(this.$t('components.ocr.fillRageNum')))
      }
      callback()
    },
    // 空的时候新增发票
    addInvoice () {
      this.$refs.addInvoiceRef.dispatchEvent(new MouseEvent('click'))
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
      // 指针移动，点击移动或者左右箭头移动
      if (index > -1) {
        this.imgIndex = index
      } else {
        this.imgIndex = flag === 'pre' ? (this.imgIndex - 1) : (this.imgIndex + 1)
      }

      // 选中的发票
      this.form = this.tempFileList.length >= 1 ? this.tempFileList[this.imgIndex] : {}

      // 如果是保存全部则不清除校验，避免定位后清除校验
      return new Promise(resolve => {
        setTimeout(() => {
          if (this.$refs.formRef && !['add', 'saveAll'].includes(flag)) {
            this.$refs.formRef.clearValidate()
          }
          resolve('ok')
        })
      })
    },
    // 图片左右箭头
    arrowImg (flag) {
      if (flag === 'pre' && this.imgIndex < 1) return
      if (flag === 'next' && this.imgIndex === this.tempFileList.length - 1) return
      this.setNextImg(flag)
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
          let sign = isEqual(item, map.get(item.fileuploadId))
          if (!sign) return i + 1
        }
      }).filter(i => typeof i !== 'undefined')

      if (noSaveIndex.length > 0 && this.tempFileList.length > 1) {
        const confirmResult = await this.$confirm(
          this.$t('components.ocr.diTip') + `${noSaveIndex}` + this.$t('components.ocr.notsaveleave'),
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

      this.$emit('closeInvoice', this.fileList)
    },
    // 编辑数据
    async editInvoiceRow () {
      const flag = await this.checkCurrentField()
      if (!flag) return
      this.$emit('editInvoiceRow', this.tempFileList[0])
      // this.$emit('saveAllPage', this.tempFileList)
    },
    // 校验本页具体信息
    async checkCurrentField () {
      const { flag, obj } = await this.$refs.formRef.validate()
      if (!flag) {
        this.$message.warning(obj[Object.keys(obj)[0]][0].message)
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
        sign = isEqual(this.fileList[this.imgIndex], this.form)
      }
      if (!sign) {
        this.fileList[this.imgIndex] = this.form
      }

      // 当发票只有一张时只有保存当页按钮，这里要判断一张发票也要触发全部保存并关闭
      if (this.tempFileList.length === 1) {
        this.$emit('saveAllPage', this.tempFileList)
      }
      // 增加延时避免去重时弹窗遮挡
      setTimeout(() => {
        this.$message.success(this.$t('common.successSave'))
      }, 500)
    },
    // 保存全部
    async saveAllPage () {
      if (this.checkAllMsg.msg) {
        await this.setNextImg('saveAll', this.checkAllMsg.ids[0] - 1)
        this.checkCurrentField()
        return
      }
      // 校验去重
      this.checkRepeatMes()
      this.fileList = this.tempFileList
      this.$emit('saveAllPage', this.tempFileList)
      // 增加延时避免去重时弹窗遮挡
      setTimeout(() => {
        this.$message.success(this.$t('common.successSave'))
      }, 500)
    },
    // 增值税发票类型选择
    selectInvoiceType (val, option) {
      this.form.invoiceType = val
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
        text: this.$t('components.ocr.waiting'),
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
      const { enable, fileList, failItems } = await this.handleUploadFile(formData, this.tempFileList, loading, event)
      // 本次的failItems上传失败
      this.singleUpload = failItems
      this.tempFileList = fileList.map(item => {
        item.commodityTaxRate = (item.commodityTaxRateHundred || item.commodityTaxRate)
        return { ...item, img: getImgSrc(item.fileuploadId, item.fileSourceName) }
      })
      this.form = this.tempFileList[this.imgIndex]
      // 校验上传报错, enable为true标识ocr服务打开
      if (enable) this.checkUploadFail()
      // 数据去重
      this.checkRepeatMes()
      this.setNextImg('click', 0)
    },
    // 校验重复
    checkRepeatMes () {
      const { hasRepeat, fileList } = this.checkRepeat(this.invoiceInformation, this.tempFileList, 'invoiceNum')
      this.tempFileList = fileList

      if (hasRepeat) this.$message.warning(this.$t('components.ocr.delectRepeatData'))
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
          this.$t('components.ocr.diTip') + `${deleteIds.join('、')}` + this.$t('components.ocr.ocrError'),
          {
            confirmButtonText: this.$t('components.ocr.enterKey'),
            cancelButtonText: this.$t('components.common.delete'),
            type: 'warning'
          }
        ).catch(() => {
          deleteIds.reverse().map(i => {
            this.deleteItem(i - 1) // 遍历删除
          })
        })
      }
    }
  }
}
</script>

<style lang="scss" scoped>
:deep(.srm-dialog-content) {
  height: 550px !important;
  padding-top: 4px !important;
}

.ml8 {
  margin-left: 8px !important;
}

.header-title {
  font-size: 16px;
  color: #161C24;
}

.header-content {
  font-size: 12px;
  color: #0077FF;
  margin-left: 8px;
}

.upload-invoice-dialog-content {
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
        filter: drop-shadow(rgba(0, 119, 255, 1) 80px 0);
        transform: translateX(-80px);
      }

      &:hover .right-icon {
        filter: drop-shadow(rgba(0, 119, 255, 1) 80px 0);
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
          border: 1px solid rgba(0, 119, 255, 1)
        }

        &:hover {
          border: 1px solid rgba(0, 119, 255, 1)
        }

        &:last-child {
          margin-right: 0;
        }
        &.currentImage{
          border-color: rgba(0,119,255,1);
          .sort-item{
            background: #0077FF;
            color: rgba(255, 255, 255, .8)
          }
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
        border: 1px solid rgba(0, 119, 255, 1)
      }

      &:hover {
        border: 1px solid rgba(0, 119, 255, 1)
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
}
</style>

<style>
.custom-color-loading .el-loading-text {
  color: rgba(255, 255, 255, .6) !important;
}
</style>
