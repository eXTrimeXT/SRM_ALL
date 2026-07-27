<template>
  <div class="inquiry-detail-info">
    <el-form
      ref="form"
      :model="headerData"
      label-width="120px"
      label-position="top"
      class="form-incontainer"
      :disabled="readonly"
      :rules="rules"
    >
      <el-collapse v-model="activeDims" class="tab-form-style">
        <!--c 询价信息 1-->
        <el-collapse-item :title="$t('bidMod.inquiryInfo')" name="1">
          <ProjectInfo :header.sync="headerData" />
        </el-collapse-item>

        <!--c 查看附件 2-->
        <el-collapse-item :title="$t('bidMod.fileList')" name="2">
          <Enclosure
            ref="enclosure"
            :outer-files="outerFiles"
            :inner-files="innerFiles"
            :readonly="readonly"
          />
        </el-collapse-item>

        <!--c 商务信息 3-->
        <el-collapse-item :title="$t('bidMod.businessInfo')" name="3">
          <OriginQuoteCurrency
            ref="quoteCurrency"
            :base-info.sync="headerData"
            :detail-data="currencyList"
            :business-type="BUSINESS_TYPE_ENUM.INQUIRY_LTS"
            :readonly="readonly"
          />
        </el-collapse-item>

        <!--c 联系方式 4-->
        <el-collapse-item :title="$t('bidMod.contactInfo')" name="4">
          <OriginContactInfo
            :business-type="BUSINESS_TYPE_ENUM.INQUIRY_LTS"
            :info-data.sync="headerData"
            :set-default="pageFlag === 'add'"
          />
        </el-collapse-item>

        <!--c 投标控制 5-->
        <el-collapse-item :title="$t('bidMod.bidingControl')" name="5">
          <BidingControl :header.sync="headerData" />
        </el-collapse-item>

        <!--c 智能推荐供应商控制 6-->
        <el-collapse-item :title="$t('bidMod.recommendSupControl')" name="6">
          <RecommendedSupplierControl :header.sync="headerData" />
        </el-collapse-item>
      </el-collapse>
    </el-form>
  </div>
</template>

<script>
/**
 * 询价信息
 */
import { isEmail, isMobile } from 'lib@/utils/validate'
import { BUSINESS_TYPE_ENUM } from 'lib@/composition/origin/enum'
import OriginQuoteCurrency from 'lib@/composition/origin/quoteCurrency/index.vue'
import OriginContactInfo from 'lib@/composition/origin/contactInfo/index.vue'
import ProjectInfo from './inquiryDetailInfo/projectInfo.vue'
import Enclosure from './inquiryDetailInfo/enclosure.vue'
import BidingControl from './inquiryDetailInfo/bidingControl.vue'
import RecommendedSupplierControl from './inquiryDetailInfo/recommendedSupplierControl.vue'

export default {
  name: 'InquiryDetailInfo',

  components: {
    OriginQuoteCurrency,
    OriginContactInfo,
    ProjectInfo,
    Enclosure,
    BidingControl,
    RecommendedSupplierControl
  },

  props: {
    header: {
      type: Object,
      required: true
    },
    innerFiles: {
      type: Array,
      required: true
    },
    outerFiles: {
      type: Array,
      required: true
    },
    currencyList: {
      type: Array,
      required: true
    },
    readonly: {
      type: Boolean,
      required: true
    },
    pageFlag: {
      type: [String, Object],
      required: true
    }
  },

  data () {
    return {
      rules: {
        souName: [{ required: true, message: this.$t('bidMod.inpInquiryTitle') }],
        organizationId: [{ required: true, message: this.$t('bidMod.inpOrg') }],
        orderWay: [{ required: true, message: this.$t('bidMod.inpOrderWay') }],
        orderStartTime: [
          { required: true, message: this.$t('bidMod.inpOrderStartTime') },
          {
            validator: (_rule, value, callback) => {
              if (value) {
                const [valueDate, diffDate] = [
                  this.$dayjs(value).unix(),
                  this.$dayjs(this.headerData.orderEndTime).unix()
                ]
                if (valueDate >= diffDate) {
                  callback(new Error(this.$t('bidMod.orderStartTimeTips1')))
                }
              }
              callback()
            },
            trigger: ['change', 'blur']
          }
        ],
        orderEndTime: [
          { required: true, message: this.$t('bidMod.orderEndTimeTips1') },
          {
            validator: (_rule, value, callback) => {
              if (value) {
                const [nowDate, diffDate, valueDate] = [
                  this.$dayjs().unix(),
                  this.$dayjs(this.headerData.orderStartTime).unix(),
                  this.$dayjs(value).unix()
                ]
                if (valueDate < nowDate) {
                  callback(new Error(this.$t('bidMod.orderEndTimeTips2')))
                }
                if (valueDate < diffDate) {
                  callback(new Error(this.$t('bidMod.orderEndTimeTips3')))
                }
              }
              callback()
            },
            trigger: ['change', 'blur']
          }
        ],
        publishScope: [{ required: true, message: this.$t('bidMod.inpPublishScope') }],
        inquiryType: [{ required: true, message: this.$t('bidMod.inpInquiryType') }],
        standardCurrency: [{ required: true, message: this.$t('bidMod.inpStandardCurrency') }],
        exchangeRateType: [{ required: true, message: this.$t('bidMod.inpExchangeRateType') }],
        currencyExchangeDate: [{ required: true, message: this.$t('bidMod.inpCurrencyExchangeDate') }],
        pricePrecision: [{ required: true, message: this.$t('bidMod.inpPricePrecision') }],
        linkman: [{ required: true, message: this.$t('bidMod.inpLinkman') }],
        email: [
          { required: true, message: this.$t('bidMod.inpEmail') },
          {
            validator: (_rule, value, callback) => {
              if (!value) {
                callback(new Error(this.$t('bidMod.bidMsgList[24]')))
              } else if (!isEmail(value)) {
                callback(new Error(this.$t('bidMod.bidMsgList[25]')))
              } else {
                callback()
              }
            },
            trigger: 'blur'
          }
        ],
        tel: [
          {
            validator: (_rule, value, callback) => {
              if (!value) {
                callback()
              } else if (!isMobile(value)) {
                callback(new Error(this.$t('bidMod.phoneMsg')))
              } else {
                callback()
              }
            },
            trigger: 'blur'
          }
        ]
      },
      activeDims: ['1', '2', '3', '4', '5', '6'],
      BUSINESS_TYPE_ENUM
    }
  },

  computed: {
    headerData: {
      get: function () {
        return this.header
      },
      set: function (val) {
        this.$emit('update:header', val)
      }
    }
  },

  methods: {
    /* 清除表单校验信息 父组件调用 */
    clearFormValidate () {
      this.$refs.form.clearValidate()
    },

    /* 返回当前数据 父组件外部调用 */
    getParamsData () {
      const {
        innerFiles = [],
        outerFiles = []
      } = this.$refs.enclosure.getParamsData()
      return {
        currencyList: this.$refs.quoteCurrency.getParamsData(),
        innerFiles,
        outerFiles
      }
    },

    /* 校验 */
    validateForm () {
      return new Promise(resolve => {
        this.$refs.form.validate(async valid => {
          if (valid) {
            if (this.headerData.orderStartTime > this.headerData.orderEndTime) {
              this.$message.warning(this.$t('bidMod.orderStartTimeTips2'))
              resolve(false)
              return
            }
            let resolveStatus = true
            const currencyList = this.$refs.quoteCurrency.getParamsData()
            for (const i of currencyList) {
              if (!i.priceTax) {
                this.$message.warning(this.$t('bidMod.priceTaxMsg'))
                resolveStatus = false
                return
              }
            }
            resolve(resolveStatus)
          } else {
            this.__focus_error__()
            resolve(false)
          }
        })
      })
    }
  }
}
</script>
