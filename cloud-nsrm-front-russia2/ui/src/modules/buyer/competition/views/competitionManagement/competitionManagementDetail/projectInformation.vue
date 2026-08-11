<template>
  <el-form
    ref="form"
    :model="baseInfoData"
    label-position="top"
    class="form-incontainer"
    :rules="rules"
    :disabled="readonly"
  >
    <el-collapse v-model="activeDims" class="tab-form-style">
      <!--项目信息-->
      <el-collapse-item :title="$t('bidMod.projectInformation')" name="1">
        <ProjectInfo :base-info.sync="baseInfoData" @amount-input="amountInput" />
      </el-collapse-item>

      <!--查看附件-->
      <el-collapse-item :title="$t('bidMod.checkAttachment')" name="3">
        <Enclosure
          ref="enclosure"
          :readonly="readonly"
          :detail-data="projectInformationData.fileList"
        />
      </el-collapse-item>

      <!--模板参考-->
      <el-collapse-item :title="$t('bidMod.templateReference')" name="4">
        <TemplateReference
          ref="templateReference"
          :readonly="readonly"
          :detail-data="projectInformationData.fileConfigList"
        />
      </el-collapse-item>

      <!--商务要求-->
      <el-collapse-item :title="$t('bidMod.businessDemand')" name="5">
        <OriginBondConfig
          :business-type="BUSINESS_TYPE_ENUM.COMPETITION"
          :base-data.sync="baseInfoData"
          :form-item-required="false"
        />
      </el-collapse-item>

      <!--报价币种设置-->
      <el-collapse-item :title="$t('bid_mod.quoteCurrency')" name="6">
        <OriginQuoteCurrency
          ref="quoteCurrency"
          :business-type="BUSINESS_TYPE_ENUM.COMPETITION"
          :base-info.sync="baseInfoData"
          :detail-data="projectInformationData.currencyList"
          :readonly="readonly"
        />
      </el-collapse-item>

      <!--向供应商展示的联系方式-->
      <el-collapse-item :title="$t('bidMod.showVendorContactInfo')" name="7">
        <OriginContactInfo
          :business-type="BUSINESS_TYPE_ENUM.COMPETITION"
          :info-data.sync="baseInfoData"
          set-default
        />
      </el-collapse-item>
    </el-collapse>
  </el-form>
</template>

<script>
/**
 * 项目信息
 */
import { BUSINESS_TYPE_ENUM } from 'lib@/composition/origin/enum'
import { compBuyerHttp } from 'modb@/competition/api'
import { isEmail, isMobile } from '@/library/utils/validate'
import ProjectInfo from './projectInformation/projectInfo'
import Enclosure from './projectInformation/enclosure'
import TemplateReference from './projectInformation/templateReference'
import OriginBondConfig from 'lib@/composition/origin/bondPay/bondConfig'
import OriginContactInfo from 'lib@/composition/origin/contactInfo'
import OriginQuoteCurrency from 'lib@/composition/origin/quoteCurrency'

export default {
  name: 'ProjectInformation',

  components: {
    ProjectInfo,
    Enclosure,
    TemplateReference,
    OriginBondConfig,
    OriginContactInfo,
    OriginQuoteCurrency
  },

  props: {
    baseInfo: {
      type: Object,
      default: () => { /* nothing */ }
    },
    projectInformationData: {
      type: Object,
      required: true
    },
    readonly: {
      type: Boolean,
      default: false
    }
  },

  data () {
    return {
      activeDims: ['1', '2', '3', '4', '5', '6', '7'],
      rules: {
        // 请输入延长分钟数
        extendMinute: [{ required: true, message: this.$t('bidMod.enterExtendTrigger') }],
        // 请输入延长竞价触发点
        extendTrigger: [{ required: true, message: this.$t('bidMod.entermInAmount') }],
        // 请填写中标供应商数量
        maxWinVendorCount: [{ required: true, message: this.$t('bidMod.smartBiddingNumber') }],
        // 请输入项目名称
        souName: [{ required: true, message: this.$t('bidMod.enterProjectName') }],
        // 请选择是否进价格库
        isSyncToPriceLibrary: [{ required: true, message: this.$t('bidMod.enterPriceWarehouse') }],
        // 请选择竞价开始时间
        orderStartTime: [{ required: true, message: this.$t('bidMod.selectTimeStartBidding') }],
        // 请输入必填项
        signUpEndTime: [{ required: true, message: this.$t('common.pleasefinishRequired') }],
        // 请选择竞价截止时间
        orderEndTime: [{ required: true, message: this.$t('bidMod.DeadlineBidding') }],
        // 请输入预算金额
        budgetAmount: [{ required: false, message: this.$t('bidMod.enterBudgetedAmount') }],
        // 请选择评分规则
        scoreRuleType: [{ required: true, message: this.$t('bidMod.selectGradingRules') }],
        // 请填写价格有效期
        priceStartTime: [{ required: true, message: this.$t('bidMod.fillPricePeriod') }],
        // 请填写价格有效期
        priceEndTime: [{ required: true, message: this.$t('bidMod.fillPricePeriod') }],
        // 请填写本位币
        standardCurrency: [{ required: true, message: this.$t('bidMod.fillStandardCurrency') }],
        // 请填写价格精度
        pricePrecision: [{ required: true, message: this.$t('bidMod.fillPriceAccuracy') }],
        // 请输入保证金金额
        bondAmount: [{ required: true, message: this.$t('bidMod.enterAmountDeposit') }],
        // 请选择保证金提交截止时间
        bondEndTime: [{ required: false, message: this.$t('bidMod.selectDeadlineSubmission') }],
        // 请选择保证金提交方式
        bondMethod: [{ required: false, message: this.$t('bidMod.selectSubmissionMethod') }],
        // 请输入保证金缴纳账号
        bankAccountNum: [{ required: false, message: this.$t('bidMod.accountNumberPayment') }],
        // 请输入账号名称
        bankAccountName: [{ required: false, message: this.$t('bidMod.enterAccountName') }],
        // 请输入开户支行
        bankBranchName: [{ required: false, message: this.$t('bidMod.enterOpeningBranch') }],
        // 请选择汇率类型
        exchangeRateType: [{ required: true, message: this.$t('dataConfMod.rateTypeRequired') }],
        // 币种转换日期
        currencyExchangeDate: [{ required: true, message: '请输入币种转换日期' }],
        // 请输入姓名
        linkman: [{ required: true, message: this.$t('bidMod.enterName') }],
        email: [
          { required: true, message: this.$t('bidMod.enterEmailAddress') },
          {
            validator: (rule, value, callback) => {
              if (!value) {
                callback(new Error(this.$t('bidMod.enterEmailAddress')))
              } else if (!isEmail(value)) {
                callback(new Error(this.$t('bidMod.mailboxFormatValid')))
              } else {
                callback()
              }
            },
            trigger: 'blur'
          }
        ],
        tel: [
          {
            validator: (rule, value, callback) => {
              if (!value) {
                callback()
              } else if (!isMobile(value)) {
                callback(new Error(this.$t('bidMod.phoneFormatIllegal')))
              } else {
                callback()
              }
            },
            trigger: 'blur'
          }
        ]
      },
      BUSINESS_TYPE_ENUM
    }
  },

  computed: {
    baseInfoData: {
      get: function () {
        return this.baseInfo
      },
      set: function (val) {
        this.$emit('update:baseInfo', val)
      }
    }
  },

  methods: {
    /* 涨降数值改变 */
    amountInput () {
      if (this.$refs.form) {
        this.$refs.form.clearValidate(['minPercent', 'minAmount'])
      }
    },

    /* 清除表单校验信息 父组件调用 */
    clearFormValidate () {
      this.$refs.form.clearValidate()
    },

    /* 暂存 / 下一步 */
    async tempSaveProjectInfo (type) {
      const valid = await this.$refs.form.validate().catch(() => this.__focus_error__())

      if (!valid) {
        return { status: false }
      }

      // 校验供方报名附件
      if (!this.$refs.enclosure.validateOuterFilesTable()) {
        return { status: false }
      }

      // 获取内外部附件
      const { innerFileList, outerFileList } = this.$refs.enclosure.getParamsData()

      let submitData = {
        // 项目基础信息
        project: this.baseInfoData,
        // 内部附件信息
        innerFileList,
        // 外部附件信息
        outerFileList,
        // 模板参考
        fileConfigList: this.$refs.templateReference.getParamsData(),
        // 币种
        currencyList: this.$refs.quoteCurrency.getParamsData(),
        // 是否暂存
        isTempSave: type !== 'nextOne'
      }

      try {
        const response = await compBuyerHttp.init.editProjectInfo(submitData)

        if (response) {
          this.$message.success(this.$t('common.successSave'))
          return { status: true, data: response.data }
        } else {
          return { status: false }
        }
      } catch (e) {
        return { status: false }
      }
    }
  }
}
</script>
