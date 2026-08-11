<!--// OCR 注册信息信息识别-->
<template>
  <div class="OCR-warp">
    <!-- 营业执照自动识别内容 -->
    <srm-dialog
      :title="title"
      size="middle"
      class="ocr-dialog"
      :visible.sync="dialogVisible"
      :close-on-click-modal="false"
      :append-to-body="true"
      @close="dialogClose"
    >
      <div class="OCR-info">
        <el-form
          v-if="type === 'license'"
          ref="licenseModle"
          :model="licenseModle.licenseForm"
          :rules="licenseModle.licenseRules"
          :show-message="false"
          class="form-fill-style"
        >
          <el-row :gutter="50">
            <el-col :span="12">
              <!-- 企业名称 -->
              <el-form-item
                prop="companyName"
                :label="$t('vendorMod.companyName')"
              >
                <el-input v-model="licenseModle.licenseForm.companyName" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <!-- 法人代表 -->
              <el-form-item
                prop="legalPerson"
                :label="$t('vendorMod.legalPerson')"
              >
                <el-input v-model="licenseModle.licenseForm.legalPerson" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <!-- 统一社会信用代码 -->
              <el-form-item
                prop="lcCode"
                :label="$t('vendorMod.lcCode')"
              >
                <el-input
                  v-model="licenseModle.licenseForm.lcCode"
                  @change="setFormatName"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <!-- 注册资本(万元) -->
              <el-form-item
                prop="registeredCapital"
                :label="$t('vendorMod.registeredCapital')"
              >
                <el-input
                  v-model="licenseModle.licenseForm.registeredCapital"
                  :placeholder="$t('common.pleaseInput')"
                  oninput="value=value.replace(/^\.+|[^\d.]/g,'')"
                  class="input-with-select"
                >
                  <dict-select
                    slot="append"
                    v-model="licenseModle.licenseForm.registCurrency"
                    :placeholder="$t('vendorMod.currencyCode')"
                    style="width: 110px;"
                    code="currency"
                  />
                </el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <!-- 详细地址 -->
              <el-form-item
                prop="companyAddress"
                :label="$t('components.address.detailAddress')"
              >
                <el-input v-model="licenseModle.licenseForm.companyAddress" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <!-- 成立日期 -->
              <el-form-item
                prop="companyCreationDate"
                :label="$t('vendorMod.creationDate')"
              >
                <el-date-picker
                  v-model="licenseModle.licenseForm.companyCreationDate"
                  type="date"
                  :placeholder="$t('common.pleaseSelectDate')"
                  :format="$formatDatePicker"
                  value-format="yyyy-MM-dd"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <!-- 营业范围 -->
              <el-form-item
                prop="businessScope"
                :label="$t('vendorMod.businessScope')"
              >
                <el-input v-model="licenseModle.licenseForm.businessScope" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <!-- 营业日期从 -->
              <el-form-item
                prop="businessStartDate"
                :label="$t('vendorMod.businessStartFrom')"
              >
                <el-date-picker
                  v-model="licenseModle.licenseForm.businessStartDate"
                  type="date"
                  :placeholder="$t('common.pleaseSelectDate')"
                  :format="$formatDatePicker"
                  value-format="yyyy-MM-dd"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <!-- 营业日期至 -->
              <el-form-item
                prop="businessEndDate"
                :label="$t('vendorMod.businessEndAt')"
              >
                <el-date-picker
                  v-model="licenseModle.licenseForm.businessEndDate"
                  type="date"
                  :placeholder="$t('common.pleaseSelectDate')"
                  :format="$formatDatePicker"
                  value-format="yyyy-MM-dd"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <!-- 登记机关 -->
              <el-form-item
                prop="registrationAuthority"
                :label="$t('vendorMod.registrationAuthority')"
              >
                <el-input v-model="licenseModle.licenseForm.registrationAuthority" />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
        <el-form
          v-if="type === 'invoice'"
          ref="invoiceModle"
          :model="invoiceModel.invoiceForm"
          :rules="invoiceModel.invoiceRules"
          :show-message="false"
          class="form-fill-style"
        >
          <el-row :gutter="50">
            <el-col :span="12">
              <!-- 发票类型-->
              <el-form-item
                prop="invoiceType"
                :label="$t('components.ocr.invoiceType')"
              >
                <el-input v-model="invoiceModel.invoiceForm.invoiceType" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <!-- 发票名称 -->
              <el-form-item
                prop="invoiceName"
                :label="$t('components.ocr.invoiceName')"
              >
                <el-input v-model="invoiceModel.invoiceForm.invoiceName" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <!-- 发票代码 -->
              <el-form-item
                prop="invoiceCode"
                :label="$t('components.ocr.invoiceCode')"
              >
                <el-input v-model="invoiceModel.invoiceForm.invoiceCode" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <!-- 发票号码 -->
              <el-form-item
                prop="invoiceNum"
                :label="$t('components.ocr.invoiceNum')"
              >
                <el-input v-model="invoiceModel.invoiceForm.invoiceNum" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <!-- 验证码 -->
              <el-form-item
                prop="checkCode"
                :label="$t('components.ocr.checkCode')"
              >
                <el-input v-model="invoiceModel.invoiceForm.checkCode" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <!-- 开票日期 -->
              <el-form-item
                prop="invoiceDate"
                :label="$t('components.ocr.invoiceDate')"
              >
                <el-date-picker
                  v-model="invoiceModel.invoiceForm.invoiceDate"
                  type="date"
                  :placeholder="$t('common.pleaseSelectDate')"
                  :format="$formatDatePicker"
                  value-format="yyyy-MM-dd"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <!-- 税率 -->
              <el-form-item
                prop="commodityTaxRate"
                :label="$t('components.ocr.commodityTaxRate')"
              >
                <el-input v-model="invoiceModel.invoiceForm.commodityTaxRate" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <!-- 含税发票金额 -->
              <el-form-item
                prop="totalAmount"
                :label="$t('components.ocr.totalAmount')"
              >
                <el-input
                  v-model="invoiceModel.invoiceForm.totalAmount"
                  :placeholder="$t('common.pleaseInput')"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <!-- 开票方 -->
              <el-form-item
                prop="sellerName"
                :label="$t('components.ocr.sellerName')"
              >
                <el-input v-model="invoiceModel.invoiceForm.sellerName" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <!-- 购买方 -->
              <el-form-item
                prop="purchaserName"
                :label="$t('components.ocr.purchaserName')"
              >
                <el-input v-model="invoiceModel.invoiceForm.purchaserName" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <!-- 购买方纳税人识别号 -->
              <el-form-item
                prop="purchaserRegisterNum"
                :label="$t('components.ocr.purchaserRegisterNum')"
              >
                <el-input v-model="invoiceModel.invoiceForm.purchaserRegisterNum" />
              </el-form-item>
            </el-col>

            <el-col :span="12">
              <!-- 备注 -->
              <el-form-item
                prop="remarks"
                :label="$t('components.ocr.remarks')"
              >
                <el-input v-model="invoiceModel.invoiceForm.remarks" />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>
      <div
        slot="footer"
        class="dialog-footer"
      >
        <el-button @click="cancle">
          {{ $t('common.cancel') }}
        </el-button>
        <el-button
          type="primary"
          @click="confirm"
        >
          {{ $t('common.confirm') }}
        </el-button>
      </div>
    </srm-dialog>
  </div>
</template>

<script>
import _pick from 'lodash/pick'
import _omit from 'lodash/omit'
export default {
  name: 'COcr',
  components: {},
  props: {
    // 上传的图片Id
    fileUploadId: {
      type: Number,
      default: () => {
        return null
      }
    },
    visible: {
      type: Boolean,
      default: false
    },
    // 默认选中的值
    defaultValue: {
      type: Array,
      default: () => {
        return []
      }
    },
    type: {
      type: String,
      default: 'license' // license 营业执照 | invoice 发票
    }
  },
  data () {
    return {
      title: this.$t('components.ocr.title'),
      dialogVisible: false,
      licenseModle: {
        licenseForm: {
          companyName: '', // 企业名称
          legalPerson: '', // 法人代表
          lcCode: '', // 统一社会信用代码
          registeredCapital: '', // 注册资本
          registCurrency: '', // 币种
          companyAddress: '', // 详细地址
          businessStartDate: '', // 营业日期从
          businessEndDate: '', // 营业日期至
          companyCreationDate: '', // 成立日期
          businessScope: '', // 营业范围
          registrationAuthority: '' // 登记机关
        }, // 图片信息
        licenseRules: {
          companyName: [{ required: true, message: this.$t('common.pleaseInput') }], // 请输入
          legalPerson: [{ required: true, message: this.$t('common.pleaseInput') }], // 请输入
          companyCreationDate: [{ required: true, message: this.$t('common.pleaseInput') }], // 请输入
          lcCode: [{ required: true, message: this.$t('common.pleaseInput') }], // 请输入
          companyAddress: [{ required: true, message: this.$t('common.pleaseInput') }], // 请输入
          registeredCapital: [{ required: true, message: this.$t('common.pleaseInput') }], // 请输入
          registCurrency: [{ required: true, message: this.$t('common.pleaseInput') }], // 请输入
          businessStartDate: [{ required: true, message: this.$t('common.pleaseInput') }], // 请输入
          businessEndDate: [{ required: true, message: this.$t('common.pleaseInput') }], // 请输入
          businessScope: [{ required: true, message: this.$t('common.pleaseInput') }] // 请输入
        }
      },
      invoiceModel: {
        invoiceForm: {
          invoiceType: '', // 发票类型
          invoiceName: '', // 发票名称
          invoiceCode: '', // 发票代码
          invoiceNum: '', // 发票号码
          checkCode: '', // 校验码。增值税专票无此参数
          invoiceDate: '', // 开票日期
          purchaserName: '', // 购买方名称
          purchaserRegisterNum: '', // 购买方纳税人识别号
          commodityTaxRate: '', // 税率
          sellerName: '', // 销售方名称
          sellerRegisterNum: '', // 销售方纳税人识别号
          totalAmount: '', // 合计金额
          totalTax: '', // 合计税额
          amountInWords: '', // 价税合计(大写)。暂未实现
          amountInFigures: '', // 价税合计(小写)
          remarks: '', // 备注。若检测到多个文字块，则用竖线'\|'分开
          remarksArr: ''
        },
        invoiceRules: {
          invoiceType: [{ required: false, message: this.$t('common.pleaseInput') }], // 请输入
          invoiceName: [{ required: true, message: this.$t('common.pleaseInput') }], // 请输入
          invoiceCode: [{ required: true, message: this.$t('common.pleaseInput') }], // 请输入
          invoiceNum: [{ required: true, message: this.$t('common.pleaseInput') }], // 请输入
          checkCode: [{ required: true, message: this.$t('common.pleaseInput') }], // 请输入
          invoiceDate: [{ required: true, message: this.$t('common.pleaseInput') }], // 请输入
          totalAmount: [{ required: true, message: this.$t('common.pleaseInput') }]
        }
      }
    }
  },
  watch: {
    visible: {
      immediate: true,
      handler (visible) {
        this.dialogVisible = visible
        if (visible) {
          if (this.type === 'license') {
            this.fatchLcData() // 营业执照信息
          } else {
            this.fatchInvoiceData() // 发票信息
          }
        } else {
          this.resetData() // 重置数据
        }
      }
    }
  },
  created () {
    this.title = this.type === 'license' ? this.$t('components.businLienAuto') : this.$t('components.ocr.fapiaoshibie')
    this.resetData() // 重置数据
  },
  methods: {
    fatchInvoiceData () {
      this.$http({
        url: '/api-base/ocr/recognizeInvoice',
        method: 'GET',
        params: { fileuploadId: this.fileUploadId },
        loading: true
      }).then(res => {
        let invoiceForm = res.data.invoiceDetail
        this.invoiceModel.invoiceForm = _omit(invoiceForm, ['remarks', 'invoiceDate', 'checkCode'])
        let invoiceDate = invoiceForm.invoiceDate
        let invoiceDateF = invoiceDate
          ? invoiceDate.replace(/[\u4e00-\u9fa5]/g, '-').substr(0, 10)
          : ''
        let checkCode = invoiceForm.checkCode ? invoiceForm.checkCode.slice(-6) : '' // checkCode 取后6位
        this.invoiceModel.invoiceForm.checkCode = checkCode
        this.invoiceModel.invoiceForm.invoiceDate = invoiceDateF
        this.invoiceModel.invoiceForm.remarks = invoiceForm.remarks.toString()
        this.invoiceModel.invoiceForm.remarksArr = invoiceForm.remarks
      })
    },
    fatchLcData () {
      // 读取图片信息
      this.$http({
        url: '/api-base/ocr/recognizeLcImage',
        method: 'GET',
        params: { fileuploadId: this.fileUploadId },
        loading: true
      })
        .then(res => {
          let licenseForm = res.data
          this.licenseModle.licenseForm = licenseForm
        })
        .catch(err => {
          console.log(err)
        })
    },
    // 重置数据
    resetData () {
      let formObj = this.licenseModle.licenseForm
      Object.keys(formObj).forEach(key => (formObj[key] = ''))

      let invoiceObj = this.invoiceModel.invoiceForm
      Object.keys(invoiceObj).forEach(key => (invoiceObj[key] = ''))
    },
    dialogClose () {
      this.$emit('update:visible', false)
      this.$emit('close')
      this.resetData() // 重置数据
      this.dialogVisible = false
    },
    license () {
      let _this = this
      if (this.licenseModle.licenseForm.lcCode.length > 20) {
        this.$message.warning(this.$t('vendorMod.msgLcCodeErr')) // 统一社会信用代码不能超过20位，请重新输入!
        return
      }
      _this.$refs.licenseModle.validate(valid => {
        if (valid) {
          const imgLicenseForm = _this.licenseModle.licenseForm
          _this.$emit('on-confirm', imgLicenseForm) // 回传数据
          this.dialogVisible = false
        } else {
          this.$message({
            message: this.$t('components.msgMaintainIdenInfo'), // 请手动维护为识别信息!
            type: 'error'
          })
          return false
        }
      })
    },
    invoice () {
      let _this = this
      _this.$refs.invoiceModle.validate(valid => {
        if (valid) {
          const invoiceForm = _this.invoiceModle.invoiceForm
          _this.$emit('on-confirm', invoiceForm) // 回传数据
          _this.dialogClose()
        } else {
          this.$message({
            message: this.$t('components.msgMaintainIdenInfo'), // 请手动维护为识别信息!
            type: 'error'
          })
          return false
        }
      })
    },
    confirm () {
      if (this.type === 'license') {
        this.license() // 营业执照
      } else {
        this.invoice() // 发票
      }
    },
    cancle () {
      this.dialogClose()
    },
    // 只允许允许输入数字和字母
    setFormatName () {
      this.licenseModle.licenseForm.lcCode = this.licenseModle.licenseForm.lcCode.replace(
        /[\W]/g,
        ''
      )
      if (this.licenseModle.licenseForm.lcCode.length > 20) {
        // 统一社会信用代码不能超过20位，请重新输入!
        this.$message.warning(this.$t('vendorMod.msgLcCodeErr'))
      }
    }
  }
}
</script>

<style lang="scss" scoped>

</style>
