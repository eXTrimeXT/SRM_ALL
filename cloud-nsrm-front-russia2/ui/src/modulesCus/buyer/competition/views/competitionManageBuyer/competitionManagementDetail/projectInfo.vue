<template>
  <el-form
    ref="form"
    :model="baseInfoData"
    label-position="top"
    class="form-incontainer"
    :rules="rules"
    :disabled="readonly"
  >
    <el-collapse
      v-model="activeDims"
      class="tab-form-style"
    >
      <!--基础信息-->
      <el-collapse-item :title="$t('vendorMod.companyBaseInfo2')" name="1">
        <ProjectInfoForm
          :base-info.sync="baseInfoData"
          :process-list="processList"
          :show-sign-up="showSignUp"
          @set-menu-config="(val, type) => $emit('set-menu-config', val, type)"
          @clear-data="clearComponentsData"
        />
      </el-collapse-item>

      <!-- 竞价规则 -->
      <el-collapse-item :title="$t('cusEntry.competition.ruleSetting')" name="2">
        <RulesSetting :base-info.sync="baseInfoData.auctSouProject" @amount-input="amountInput" />
      </el-collapse-item>

      <!--招标资料-->
      <el-collapse-item :title="$t('cusEntry.competition.bidDocuments')" name="3">
        <Enclosure
          ref="enclosure"
          :readonly="readonly"
          :detail-data="projectInfoData.fileList"
        />
      </el-collapse-item>

      <!--保证金信息-->
      <el-collapse-item
        :title="$t('bidMod.competitionLts.bondConfig')"
        name="5"
      >
        <OriginBondConfig
          :base-data.sync="baseInfoData.auctSouProject"
          :business-type="BUSINESS_TYPE_ENUM.COMPETITION"
          :form-item-required="true"
        />
      </el-collapse-item>
    </el-collapse>
  </el-form>
</template>

<script>
/**
 * 项目信息
 */
import { carBuyerHttp } from 'modcb@/competition/api'
import { isEmail } from 'lib@/utils/validate'
import { BUSINESS_TYPE_ENUM } from 'lib@/composition/origin/enum'
import ProjectInfoForm from './projectInfo/projectInfoForm.vue'
import RulesSetting from './projectInfo/rulesSetting'
import TemplateReference from './projectInfo/templateReference'
import ContactInfo from './projectInfo/contactInfo'
import Enclosure from './projectInfo/enclosure'
import OriginBondConfig from './projectInfo/bondConfig'
import RecommendedSupplierControl from 'lib@/composition/competition/inviteVendor/recommendedSupplierControl'
import { transformMQL } from 'lib@/utils/util'

export default {
  name: 'ProjectInfo',

  components: {
    ProjectInfoForm,
    RulesSetting,
    Enclosure,
    TemplateReference,
    OriginBondConfig,
    ContactInfo,
    RecommendedSupplierControl
  },

  props: {
    baseInfo: {
      type: Object,
      default: () => { /* nothing */ }
    },
    processList: {
      type: Array,
      default: () => []
    },
    projectInfoData: {
      type: Object,
      required: true,
      default: () => {}
    },
    readonly: {
      type: Boolean,
      default: false
    },
    pageFlag: {
      type: Object,
      required: true
    },
    // 当前启用的节点
    enabledNodeMenu: {
      type: Array,
      default: () => []
    }
  },

  data () {
    return {
      activeDims: ['1', '2', '3', '4', '5', '6', '7'],
      rules: {
        // 请输入项目名称
        souName: [{ required: true, message: this.$t('bidMod.enterProjectName') }],
        // 报名截止时间
        signUpEndTime: [{ required: true, message: this.$t('common.pleaseSelect') }],
        // 请选择竞价开始时间
        orderStartTime: [{ required: true, message: this.$t('bidMod.selectTimeStartBidding') }],
        // 请选择竞价截止时间
        orderEndTime: [{ required: true, message: this.$t('bidMod.DeadlineBidding') }],
        auctSouProject: {
          // 竞价规则
          souRules: [{ required: true, message: this.$t('cusEntry.tipMessage.auctRule') }],
          // 公开规则
          scopeRule: [{ required: true, message: this.$t('common.pleaseSelect') }],
          // 每项物资报价次数
          orderNum: [{ required: true, message: this.$t('cusEntry.tipMessage.quoteNumOfMaterial') }]
        },
        // 姓名
        linkman: [{ required: true, message: this.$t('bidMod.enterName') }],
        // 邮箱
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
    },

    // 报名
    showSignUp () {
      // (存在流程模板 && 启用了报名管理) || 不存在流程模板
      return !!((this.baseInfoData.processConfigId && (this.enabledNodeMenu || []).includes('signUpManagement')) ||
        !this.baseInfoData.processConfigId ||
        // 只读，但是有值
        (this.pageFlag.isView && this.baseInfoData.signUpEndTime))
    },

    // 保证金
    showBondConfig () {
      return (this.enabledNodeMenu || []).includes('bondManagement')
    }
  },

  // watch 监听，变更校验条件
  watch: {
    showSignUp (val) {
      this.rules.signUpEndTime[0].required = val
    }
  },

  methods: {
    /* 切换流程模板，清空数据 */
    clearComponentsData () {
      this.$refs.enclosure.clearData()
    },

    /* 涨降数值改变 */
    amountInput () {
      if (this.$refs.form) {
        this.$refs.form.clearValidate(['auctSouProject.minPercent', 'auctSouProject.minAmount'])
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

      // 获取内外部附件
      const { innerFiles, outerFiles } = this.$refs.enclosure.getParamsData()
      const { auctSouProject, ...other } = this.baseInfoData
      let submitData = {
        // 项目基础信息
        project: {
          ...other,
          ...auctSouProject
        },
        innerFileList: innerFiles,
        outerFileList: outerFiles,
        createStep: 'projectInfo',
        // 是否暂存
        tempSave: type !== 'nextOne'
      }

      try {
        // let transformParams = transformMQL.save('AuctSouProjectForBuyer', [submitData], 'editProjectInfo')
        const response = await carBuyerHttp.init.editProjectInfo(submitData)

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
