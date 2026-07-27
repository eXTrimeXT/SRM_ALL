<template>
  <!-- 项目需求 -->
  <el-form
    ref="projectInformationForm"
    :model="biddingBaseInfo"
    label-width="80px"
    label-position="top"
    :rules="rules"
    :disabled="readonly"
  >
    <el-collapse v-model="activeDims" class="tab-form-style">
      <!--项目信息-->
      <el-collapse-item :title="$t('bidMod.projectInformation')" name="1">
        <ProjectInfoForm
          ref="projectInfoForm"
          :bidding-base.sync="biddingBaseInfo"
          :process-list="processList"
          :show-enroll-end-datetime="!!showEnrollEndDatetime"
          @set-menu-config="(val, type) => $emit('set-menu-config', val, type)"
          @clear-data="clearComponentsData"
        />
      </el-collapse-item>

      <!--工作小组-->
      <el-collapse-item :title="$t('bidMod.workGroupList')" name="2">
        <WorkGroup
          ref="workGroup"
          :readonly="readonly"
          :bidding-base="biddingBaseInfo"
          :detail-data="projectInfoData.groupList"
        />
      </el-collapse-item>

      <!-- 查看附件 -->
      <el-collapse-item :title="$t('bidMod.fileList')" name="3">
        <Enclosure
          ref="enclosure"
          :readonly="readonly"
          :detail-data="projectInfoData.fileList"
        />
      </el-collapse-item>

      <!-- 模板参考 -->
      <el-collapse-item :title="$t('bidMod.templateRef')" name="4">
        <TemplateReference
          ref="templateReference"
          :bidding-base-info.sync="biddingBaseInfo"
          :readonly="readonly"
          :detail-data="projectInfoData.fileConfigList"
        />
      </el-collapse-item>

      <!--商务要求-->
      <el-collapse-item
        v-if="bondConfigVisible"
        :title="$t('bidMod.businessDemand')"
        name="5"
      >
        <OriginBondConfig :business-type="BUSINESS_TYPE_ENUM.BIDDING_LTS" :base-data.sync="biddingBaseInfo" />
      </el-collapse-item>

      <!-- 投标币种设置 -->
      <el-collapse-item :title="$t('bidMod.bidCurrencySetting')" name="6">
        <OriginQuoteCurrency
          ref="quoteCurrency"
          :base-info.sync="biddingBaseInfo"
          :detail-data="projectInfoData.currencyList"
          :business-type="BUSINESS_TYPE_ENUM.BIDDING_LTS"
          :readonly="readonly"
        />
      </el-collapse-item>

      <!-- 向供应商展示的联系方式 -->
      <el-collapse-item :title="$t('bidMod.showVendorContactInfo')" name="7">
        <OriginContactInfo
          :business-type="BUSINESS_TYPE_ENUM.BIDDING_LTS"
          :info-data.sync="biddingBaseInfo"
        />
      </el-collapse-item>

      <!-- 投标控制 -->
      <el-collapse-item :title="$t('bidMod.bidingControl')" name="8">
        <BiddingControl
          :bidding-base.sync="biddingBaseInfo"
          @need-encrypt-price-change="validateNeedEncryptPrice"
        />
      </el-collapse-item>

      <!-- 智能推荐供应商控制 -->
      <el-collapse-item title="智能推荐供应商控制" name="9">
        <VendorsControl :bidding-base.sync="biddingBaseInfo" />
      </el-collapse-item>
    </el-collapse>
  </el-form>
</template>

<script>
import { bidBuyerHttp } from 'modb@/bidding/api'
import { isMobile, isEmail } from 'lib@/utils/validate'
import { BUSINESS_TYPE_ENUM } from 'lib@/composition/origin/enum'
import { SOU_BRG_TYPE_ENUM } from 'lib@/composition/biddingLts/utils'
import OriginQuoteCurrency from 'lib@/composition/origin/quoteCurrency'
import OriginBondConfig from 'lib@/composition/origin/bondPay/bondConfig'
import OriginContactInfo from 'lib@/composition/origin/contactInfo'
import WorkGroup from './projectInfo/workGroup'
import Enclosure from './projectInfo/enclosure'
import TemplateReference from './projectInfo/templateReference'
import BiddingControl from './projectInfo/biddingControl'
import ProjectInfoForm from './projectInfo/projectInfoForm'
import VendorsControl from './projectInfo/vendorsControl'

export default {
  name: 'ProjectInfo',

  components: {
    WorkGroup,
    Enclosure,
    TemplateReference,
    BiddingControl,
    ProjectInfoForm,
    VendorsControl,
    OriginQuoteCurrency,
    OriginBondConfig,
    OriginContactInfo
  },

  props: {
    biddingBase: {
      type: Object,
      default: () => ({})
    },
    processList: {
      type: Array,
      default: () => []
    },
    projectInfoData: {
      type: Object,
      required: true
    },
    // 当前启用的节点
    enabledNodeMenu: {
      type: Array,
      default: () => []
    },
    readonly: {
      type: Boolean,
      default: false
    },
    // 是否显示保证金配置信息
    bondConfigVisible: {
      type: Boolean,
      default: false
    },
    pageFlag: {
      type: Object,
      required: true
    }
  },

  data () {
    return {
      activeDims: ['1', '2', '3', '4', '5', '6', '7', '8', '9'],
      rules: {
        // 请选择模板
        processConfigId: [{ required: true, message: this.$t('bidMod.bidMsgList[0]') }],
        // 请输入项目名称
        souName: [{ required: true, message: this.$t('bidMod.bidMsgList[1]') }],
        // 请选择是否进价格库
        isSyncToPriceLibrary: [{ required: true, message: this.$t('bidMod.bidMsgList[33]') }],
        // 请选择价格有效期自
        priceStartTime: [{ required: true, message: this.$t('bidMod.bidMsgList[34]') }],
        // 请选择价格有效期至
        priceEndTime: [
          { required: true, message: this.$t('bidMod.bidMsgList[35]') },
          {
            validator: (_rule, value, callback) => {
              if (value) {
                const startDate = new Date(this.biddingBaseInfo.priceStartTime)
                const endDate = new Date(value)
                if (startDate.getTime() > endDate.getTime()) {
                  callback(new Error('价格有效期至需要小于价格有效期自'))
                }
              }
              callback()
            },
            trigger: 'blur'
          }
        ],
        // 请选择投标截止时间
        orderEndTime: [
          { required: true, message: this.$t('bidMod.bidMsgList[36]') },
          {
            validator: (_rule, value, callback) => {
              if (value) {
                const startDate = new Date(this.biddingBaseInfo.orderStartTime)
                const endDate = new Date(value)
                if (startDate.getTime() > endDate.getTime()) {
                  callback(new Error('截止时间需要小于开始时间'))
                }
              }
              callback()
            },
            trigger: 'blur'
          }
        ],
        // 请输入预计投标地点
        orderSite: [{ required: true, message: this.$t('bidMod.bidMsgList[2]') }],
        // 请选择招标范围
        publishScope: [{ required: true, message: this.$t('bidMod.bidMsgList[3]') }],
        // 请输入预算金额
        budgetAmount: [{ required: false, message: this.$t('bidMod.bidMsgList[4]') }],
        // 请选择招标类型
        bargainType: [{ required: true, message: this.$t('bidMod.bidMsgList[6]') }],
        // 请选择评分规则
        scoreRuleType: [{ required: true, message: this.$t('bidMod.bidMsgList[7]') }],
        // 请选择决标方式
        orderWay: [{ required: true, message: this.$t('bidMod.bidMsgList[8]') }],
        // 请填写本位币
        standardCurrency: [{ required: true, message: this.$t('bidMod.bidMsgList[10]') }],
        // 请填写价格精度
        pricePrecision: [{ required: true, message: this.$t('bidMod.bidMsgList[11]') }],
        // 请选择投标开始时间
        orderStartTime: [{ required: true, message: this.$t('bidMod.bidMsgList[22]') }],
        // 请选择采购组织
        orgName: [{ required: true, message: this.$t('bidMod.bidMsgList[23]') }],
        // 请选择汇率类型
        exchangeRateType: [{ required: true, message: this.$t('dataConfMod.rateTypeRequired') }],
        currencyExchangeDate: [{ required: true, message: '请选择币种转换日期' }],
        // 联系人
        linkman: [{ required: true, message: this.$t('common.pleaseInput') }],
        // 邮箱
        email: [
          // 请输入邮箱
          { required: true, message: this.$t('bidMod.bidMsgList[24]') },
          {
            validator: (_rule, value, callback) => {
              if (!value) {
                // 请输入邮箱
                callback(new Error(this.$t('bidMod.bidMsgList[24]')))
              } else if (!isEmail(value)) {
                // 邮箱格式不合法
                callback(new Error(this.$t('bidMod.bidMsgList[25]')))
              }
              callback()
            },
            trigger: 'blur'
          }
        ],
        // 电话
        tel: [
          {
            validator: (_rule, value, callback) => {
              if (!value) {
                callback()
              } else if (!isMobile(value)) {
                // 手机格式不合法
                callback(new Error(this.$t('bidMod.bidMsgList[26]')))
              }
              callback()
            },
            trigger: 'blur'
          }
        ],
        // 报名截止时间
        signUpEndTime: [{ required: true, message: this.$t('bidMod.bidMsgList[42]') }],
        bondAmount: [
          {
            validator: (_rule, value, callback) => {
              if (this.bondConfigVisible && !value) {
                callback(new Error(this.$t('bidMod.bidMsgList[12]')))
              }
              callback()
            },
            trigger: ['change', 'blur']
          }
        ],
        // 保证金提交截止时间
        bondEndTime: [
          {
            validator: (_rule, value, callback) => {
              if (this.bondConfigVisible && !value) {
                callback(new Error(this.$t('bidMod.bidMsgList[13]')))
              }
              callback()
            },
            trigger: ['change', 'blur']
          }
        ],
        // 保证金提交方式
        bondMethod: [
          {
            validator: (_rule, value, callback) => {
              if (this.bondConfigVisible && !value) {
                callback(new Error(this.$t('bidMod.bidMsgList[14]')))
              }
              callback()
            },
            trigger: ['change', 'blur']
          }
        ],
        // 保证金缴纳账号
        bankAccountNum: [
          {
            validator: (_rule, value, callback) => {
              if (this.bondConfigVisible && !value) {
                callback(new Error(this.$t('bidMod.bidMsgList[15]')))
              }
              callback()
            },
            trigger: ['change', 'blur']
          }
        ],
        // 账户名称
        bankAccountName: [
          {
            validator: (_rule, value, callback) => {
              if (this.bondConfigVisible && !value) {
                callback(new Error('请输入账户名称'))
              }
              callback()
            },
            trigger: ['change', 'blur']
          }
        ],
        // 开户支行
        bankBranchName: [
          {
            validator: (_rule, value, callback) => {
              if (this.bondConfigVisible && !value) {
                callback(new Error('请输入开户支行'))
              }
              callback()
            },
            trigger: ['change', 'blur']
          }
        ]
      },
      BUSINESS_TYPE_ENUM
    }
  },

  computed: {
    biddingBaseInfo: {
      get: function () {
        return this.biddingBase
      },
      set: function (val) {
        this.$emit('update:biddingBase', val)
      }
    },
    // 是否显示报名截止时间
    showEnrollEndDatetime () {
      // (存在流程模板 && 启用了报名管理) || 不存在流程模板
      return (this.biddingBaseInfo.processConfigId && (this.enabledNodeMenu || []).includes('signUpManagement')) ||
        !this.biddingBaseInfo.processConfigId ||
        // 只读，但是有值
        (this.readonly && this.biddingBaseInfo.signUpEndTime)
    }
  },

  watch: {
    showEnrollEndDatetime: {
      handler (val) {
        // 必填校验
        this.rules.signUpEndTime[0].required = val
      },
      immediate: true
    }
  },

  methods: {
    /* 切换流程模板，清空数据 */
    clearComponentsData () {
      this.biddingBaseInfo.excludeOrgCategoryStatus = ''

      this.$refs.workGroup.clearData()
      this.$refs.enclosure.clearData()
      this.$refs.templateReference.clearData()
      this.$refs.quoteCurrency.clearData()
    },

    /* 是否密封报价改变 */
    validateNeedEncryptPrice () {
      // 密封报价时工作小组需分配解密权限
      if (this.biddingBaseInfo.needEncryptPrice === 'Y') {
        const groupList = this.$refs.workGroup.getParamsData()
        if (!groupList.find(item => item.operateAuth === 'SOU_DECRYPT_PRICE')) {
          this.$message.warning(this.$t('bidMod.biddingManagementBuyer.warningNeedEncryptPrice'))
          return false
        }
      }
      return true
    },

    /* 清除表单校验信息 父组件调用 */
    clearFormValidate () {
      this.$refs.projectInformationForm.clearValidate()
    },

    /* 暂存项目信息 */
    async tempSaveProjectInfo (type) {
      // 校验
      const valid = await this.$refs.projectInformationForm.validate().catch(() => { /* noting */ })
      if (!valid) {
        this.__focus_error__()
        return
      }

      // 获取内外部附件
      const { innerFileList, outerFileList } = this.$refs.enclosure.getParamsData()

      let submitData = {
        // 项目基础信息
        project: this.biddingBaseInfo,
        // 工作小组
        groupList: this.$refs.workGroup.getParamsData(),
        // 内部附件信息
        innerFileList,
        // 外部附件信息
        outerFileList,
        // 模板参考
        fileConfigList: this.$refs.templateReference.getParamsData(),
        // 可用外币列表
        currencyList: this.$refs.quoteCurrency.getParamsData(),
        // 是否是暂存
        isTempSave: type !== 'nextOne'
      }

      if (this.biddingBaseInfo.bargainType === SOU_BRG_TYPE_ENUM.TECHNOLOGY_BUSINESS) {
        // 招标类型为技术+商务 工作小组必须要有一个技术评委
        const count = submitData.groupList.reduce((prev, curr) => {
          if (curr.scoreAuth === 'SOU_TECH') {
            return prev + 1
          }
          return prev
        }, 0)
        if (count === 0) {
          this.$message.warning('工作小组中必须至少有一个技术评委！')
          return
        }
      }

      // 校验报价解密权限
      if (!this.validateNeedEncryptPrice()) {
        return
      }

      const data = await bidBuyerHttp.init.editProjectInfo(submitData)
      if (!data) {
        return
      }

      this.$message.success(this.$t('common.success'))
      // 更新id
      if (!this.biddingBaseInfo.projectId) {
        this.biddingBaseInfo.projectId = data.data
      }
      // 等待ID更新
      await this.$nextTick()
      // 发起保存成功回调
      this.$emit('temp-save-success', type)
    }
  }
}
</script>
