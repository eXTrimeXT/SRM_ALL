<template>
  <!-- 项目需求 -->
  <el-form
    ref="form"
    :model="bargainBaseInfo"
    label-width="80px"
    label-position="top"
    class="form-incontainer"
    :rules="rules"
    :disabled="readOnly"
  >
    <el-collapse
      v-model="activeDims"
      class="tab-form-style"
    >
      <!--项目信息-->
      <el-collapse-item
        :title="$t('bidMod.projectInformation')"
        name="1"
      >
        <ProjectInfo
          ref="projectInfo"
          :bargain-base.sync="bargainBaseInfo"
          :brg-process-config-id-list="brgProcessConfigIdList"
          :show-enroll-end-datetime="showEnrollEndDatetime"
          @setMenuNodeConfig="setMenuNodeConfig"
          @clearData="clearComponentsData"
        />
      </el-collapse-item>

      <!--工作小组-->
      <el-collapse-item
        :title="$t('bidMod.workGroupList')"
        name="2"
      >
        <WorkGroup
          ref="workGroup"
          :bargain-base="bargainBaseInfo"
          :detail-data="projectInformationData.groupList"
        />
      </el-collapse-item>

      <!-- 查看附件 -->
      <el-collapse-item
        :title="$t('bidMod.fileList')"
        name="3"
      >
        <Enclosure
          ref="enclosure"
          :read-only="readOnly"
          :detail-data="projectInformationData.fileList"
        />
      </el-collapse-item>

      <!-- 模板参考 -->
      <el-collapse-item
        :title="$t('bidMod.templateRef')"
        name="4"
      >
        <TemplateReference
          ref="templateReference"
          :bargain-id="scopeBargainId"
          :bargain-base-info.sync="bargainBaseInfo"
          :read-only="readOnly"
          :detail-data="projectInformationData.fileConfigList"
        />
      </el-collapse-item>

      <!--商务要求-->
      <el-collapse-item
        v-if="bondConfigVisible"
        :title="$t('bidMod.businessDemand')"
        name="5"
      >
        <OriginBondConfig :base-data.sync="bargainBaseInfo" />
      </el-collapse-item>

      <!-- 报价币种设置 -->
      <el-collapse-item
        :title="$t('bid_mod.quoteCurrency')"
        name="6"
      >
        <OriginQuoteCurrency
          ref="quoteCurrency"
          :base-info.sync="bargainBaseInfo"
          :detail-data="projectInformationData.currencyList"
          business-type="BARGAIN"
          :readonly="readOnly"
        />
      </el-collapse-item>

      <!-- 向供应商展示的联系方式 -->
      <el-collapse-item
        :title="$t('bidMod.showVendorContactInfo')"
        name="7"
      >
        <OriginContactInfo
          business-type="BARGAIN"
          :info-data.sync="bargainBaseInfo"
          :set-default="pageFlag.isAdd"
        />
      </el-collapse-item>

      <!-- 报价控制 -->
      <el-collapse-item
        :title="$t('bidMod.priceControl')"
        name="8"
      >
        <BargainControl
          :bargain-base.sync="bargainBaseInfo"
          @needEncryptPriceChange="needEncryptPriceChange"
        />
      </el-collapse-item>

      <!-- 推荐供应商控制 -->
      <el-collapse-item
        title="推荐供应商控制"
        name="9"
      >
        <VendorsControl
          ref="vendorsControl"
          :bargain-base.sync="bargainBaseInfo"
        />
      </el-collapse-item>
    </el-collapse>
  </el-form>
</template>

<script>
import { isMobile, isEmail } from 'lib@/utils/validate'
import WorkGroup from './projectInformation/workGroup'
import Enclosure from './projectInformation/enclosure'
import TemplateReference from './projectInformation/templateReference'
import BargainControl from './projectInformation/bargainControl'
import ProjectInfo from './projectInformation/projectInfo'
import VendorsControl from './projectInformation/vendorsControl'
import OriginQuoteCurrency from 'lib@/composition/origin/quoteCurrency'
import OriginBondConfig from 'lib@/composition/origin/bondPay/bondConfig'
import OriginContactInfo from 'lib@/composition/origin/contactInfo'

export default {
  name: 'ProjectInformation',

  components: {
    WorkGroup,
    Enclosure,
    TemplateReference,
    BargainControl,
    ProjectInfo,
    VendorsControl,
    OriginQuoteCurrency,
    OriginBondConfig,
    OriginContactInfo
  },

  props: {
    scopeBargainId: {
      // 招标ID
      type: [Number, String],
      default: ''
    },
    bargainBase: {
      type: Object,
      default: () => {}
    },
    brgProcessConfigIdList: {
      type: Array,
      default: () => []
    },
    projectInformationData: {
      type: Object,
      required: true
    },
    // 当前启用的节点
    enabledNodeMenu: {
      type: Array,
      default: () => []
    },
    readOnly: {
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
        bargainName: [{ required: true, message: this.$t('bidMod.bidMsgList[1]') }],
        // 请选择是否进价格库
        isSyncToPriceLibrary: [{ required: true, message: this.$t('bidMod.bidMsgList[33]') }],
        // 请选择价格有效期自
        priceStartTime: [{ required: true, message: this.$t('bidMod.bidMsgList[34]') }],
        // 请选择价格有效期至
        priceEndTime: [
          { required: true, message: this.$t('bidMod.bidMsgList[35]') },
          {
            validator: (rule, value, callback) => {
              if (value) {
                const startDate = new Date(this.bargainBaseInfo.priceStartTime)
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
        bargainEndDatetime: [
          { required: true, message: this.$t('bidMod.bidMsgList[36]') },
          {
            validator: (rule, value, callback) => {
              if (value) {
                const startDate = new Date(this.bargainBaseInfo.bargainStartDatetime)
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
        bargainSite: [{ required: true, message: this.$t('bidMod.bidMsgList[2]') }],
        // 请选择招标范围
        bargainScope: [{ required: true, message: this.$t('bidMod.bidMsgList[3]') }],
        // 请输入预算金额
        budgetAmount: [{ required: false, message: this.$t('bidMod.bidMsgList[4]') }],
        // 请选择标的类型
        targetType: [{ required: true, message: this.$t('bidMod.bidMsgList[5]') }],
        // 请选择招标类型
        bargainType: [{ required: true, message: this.$t('bidMod.bidMsgList[6]') }],
        // 请选择评分规则
        evaluateMethod: [{ required: true, message: this.$t('bidMod.bidMsgList[7]') }],
        // 请选择决标方式
        bargainAwardWay: [{ required: true, message: this.$t('bidMod.bidMsgList[8]') }],
        // 请填写本位币
        standardCurrency: [{ required: true, message: this.$t('bidMod.bidMsgList[10]') }],
        // 请填写价格精度
        pricePrecision: [{ required: true, message: this.$t('bidMod.bidMsgList[11]') }],
        // 请选择报价是否含税
        taxInclusivePrice: [{ required: true, message: this.$t('bidMod.bidMsgList[18]') }],
        // 请选择招标币种
        bargainCurrency: [{ required: true, message: this.$t('bidMod.bidMsgList[19]') }],
        // 请输入报价最多保留
        decimalAccuracy: [{ required: true, message: this.$t('bidMod.bidMsgList[20]') }],
        // 请输入姓名
        brgContactName: [{ required: true, message: this.$t('bidMod.bidMsgList[21]') }],
        // 请选择投标开始时间
        bargainStartDatetime: [{ required: true, message: this.$t('bidMod.bidMsgList[22]') }],
        // 请选择采购组织
        orgName: [{ required: true, message: this.$t('bidMod.bidMsgList[23]') }],
        // 请选择汇率类型
        exchangeRateType: [{ required: true, message: this.$t('dataConfMod.rateTypeRequired') }],
        brgEmail: [
          // 请输入邮箱
          { required: true, message: this.$t('bidMod.bidMsgList[24]') },
          {
            validator: (rule, value, callback) => {
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
        brgMobilePhone: [
          {
            validator: (rule, value, callback) => {
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
        enrollEndDatetime: [{ required: true, message: this.$t('bidMod.bidMsgList[42]') }],
        bondAmount: [
          {
            validator: (rule, value, callback) => {
              if (this.bondConfigVisible && !value) {
                callback(new Error(this.$t('bidMod.bidMsgList[12]')))
              }
              callback()
            },
            trigger: ['change', 'blur']
          }
        ],
        // 保证金提交截止时间
        bondEndDatetime: [
          {
            validator: (rule, value, callback) => {
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
            validator: (rule, value, callback) => {
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
            validator: (rule, value, callback) => {
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
            validator: (rule, value, callback) => {
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
            validator: (rule, value, callback) => {
              if (this.bondConfigVisible && !value) {
                callback(new Error('请输入开户支行'))
              }
              callback()
            },
            trigger: ['change', 'blur']
          }
        ]
      }
    }
  },

  computed: {
    bargainBaseInfo: {
      get: function () {
        return this.bargainBase
      },
      set: function (val) {
        this.$emit('update:bargainBase', val)
      }
    },
    // 是否显示报名截止时间
    showEnrollEndDatetime () {
      // (存在流程模板 && 启用了报名管理) || 不存在流程模板
      return (this.bargainBaseInfo.processConfigId && (this.enabledNodeMenu || []).includes('t4')) ||
        !this.bargainBaseInfo.processConfigId
    }
  },

  watch: {
    showEnrollEndDatetime: {
      handler (val) {
        // 必填校验
        this.rules.enrollEndDatetime[0].required = val
      },
      immediate: true
    }
  },

  methods: {
    /* 切换流程模板，清空数据 */
    clearComponentsData () {
      this.bargainBaseInfo.excludeOrgCategoryStatus = ''

      this.$refs.workGroup.clearData()
      this.$refs.enclosure.clearData()
      this.$refs.templateReference.clearData()
      this.$refs.quoteCurrency.clearData()
    },

    /* 是否密封报价改变 */
    needEncryptPriceChange (val) {
      // 密封报价时工作小组需分配解密权限
      if (val && this.$refs.workGroup.groupLeaderCount === 0) {
        this.$message.warning(this.$t('bidMod.biddingManagementBuyer.warningNeedEncryptPrice'))
      }
    },

    /* 设置左边菜单节点信息 */
    setMenuNodeConfig (val, type) {
      this.$emit('setMenuNodeConfig', val, type)
    },

    /* 清除表单校验信息 父组件调用 */
    clearFormValidate () {
      this.$refs.form.clearValidate()
    },

    /* 暂存项目信息 */
    tempSaveProjectInfo (type) {
      this.$refs.form.validate(async valid => {
        if (valid) {
          let submitData = {
            // 内外部附件信息
            fileList: this.$refs.enclosure.getParamsData(),
            // 项目基础信息
            bargain: this.bargainBaseInfo,
            // 模板参考
            fileConfigList: this.$refs.templateReference.getParamsData(),
            // 工作小组
            groupList: this.$refs.workGroup.getParamsData(),
            // 可用外币列表
            currencyList: this.$refs.quoteCurrency.getParamsData(),
            // 是否是暂存
            isTempSave: !(type === 'nextOne')
          }

          if (this.bargainBaseInfo.bargainType === 'TECHNOLOGY_BUSINESS') {
            // 招标类型为技术+商务 工作小组必须要有一个技术评委
            if (!submitData.groupList.find(item => item.judgeFlag === 'Y')) {
              this.$message.warning('工作小组中至少需要一个技术评委！')
              return
            }
          }

          const data = await this.$api.brg.inquiryByProject.tempSaveOrSubmitProjectInfo(submitData)
          if (!data) return

          this.$message.success(this.$t('common.success'))
          this.bargainBaseInfo.bargainId = this.scopeBargainId || data.data
          // 查询单据信息
          this.$emit('fetchBaseInfo', this.scopeBargainId || data.data)
          // 下一步保存触发
          if (type === 'nextOne') {
            // 保存后下一步操作
            this.$emit('saveNextTodo', this.scopeBargainId || data.data)
          } else {
            // 暂存触发
            // 更新节点
            this.$emit('updateProcessNode')
          }
        } else {
          this.__focus_error__()
        }
      })
    }
  }
}
</script>
