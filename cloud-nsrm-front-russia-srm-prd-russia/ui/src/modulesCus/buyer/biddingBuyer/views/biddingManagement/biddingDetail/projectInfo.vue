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
      <el-collapse-item :title="$t('meeting.baseInfo')" name="1">
        <ProjectInfoForm
          ref="projectInfoForm"
          :read-only="readonly"
          :bidding-base.sync="biddingBaseInfo"
          :process-list="processList"
          v-on="$listeners"
          @set-menu-config="(val, type) => $emit('set-menu-config', val, type)"
          @clear-data="clearComponentsData"
        />
      </el-collapse-item>

      <!--工作小组-->
      <el-collapse-item :title="$t('cusEntry.bidMod.workGroupList')" name="2">
        <WorkGroup
          ref="workGroup"
          :read-only="readonly"
          :bidding-base="biddingBaseInfo"
          :detail-data="projectInfoData.groupList"
        />
      </el-collapse-item>

      <!-- 上传资料 -->
      <el-collapse-item :title="$t('cusEntry.bidMod.fileList')" name="3">
        <Enclosure
          ref="enclosure"
          :read-only="readonly"
          :bidding-base="biddingBaseInfo"
          :pack-name-list="packNameList"
          :detail-data="projectInfoData.fileList"
        />
      </el-collapse-item>

      <!-- 制定招标计划 -->
      <el-collapse-item :title="$t('cusEntry.bidMod.biddingPlan')" name="4">
        <BiddingPlan
          ref="biddingPlan"
          :read-only="readonly"
          :bidding-base="biddingBaseInfo"
          :detail-data="projectInfoData.planList"
        />
      </el-collapse-item>

      <!-- 招标保证金 -->
      <el-collapse-item :title="$t('cusEntry.bidMod.biddingDeposit')" name="5">
        <BiddingDeposit
          ref="biddingDeposit"
          :read-only="readonly"
          :bidding-base-info.sync="biddingBaseInfo"
        />
      </el-collapse-item>

      <!-- 向供应商展示联系人 -->
      <el-collapse-item :title="$t('cusEntry.bidMod.showContactInfo')" name="6">
        <OriginContactInfo
          :business-type="BUSINESS_TYPE_ENUM.BIDDING_LTS"
          :read-only="readonly"
          :info-data.sync="biddingBaseInfo"
        />
      </el-collapse-item>

      <!-- 标书控制 -->
      <el-collapse-item :title="$t('cusEntry.bidMod.bidingControl')" name="7">
        <BiddingControl
          :read-only="readonly"
          :bidding-base.sync="biddingBaseInfo"
          @need-encrypt-price-change="validateNeedEncryptPrice"
        />
      </el-collapse-item>
    </el-collapse>
  </el-form>
</template>

<script>
import { bidBuyerHttp } from 'modcb@/biddingBuyer/api'
import { isMobile, isEmail } from 'lib@/utils/validate'
import { BUSINESS_TYPE_ENUM } from 'lib@/composition/origin/enum'
import { SOU_BRG_TYPE_ENUM } from 'lib@/composition/biddingLts/utils'
import OriginContactInfo from './projectInfo/contactInfo'
import WorkGroup from './projectInfo/workGroup'
import Enclosure from './projectInfo/enclosure'
import BiddingPlan from './projectInfo/biddingPlan'
import BiddingDeposit from './projectInfo/biddingDeposit'
import BiddingControl from './projectInfo/biddingControl'
import ProjectInfoForm from './projectInfo/projectInfoForm'

export default {
  name: 'ProjectInfo',

  components: {
    WorkGroup,
    Enclosure,
    BiddingPlan,
    BiddingDeposit,
    BiddingControl,
    ProjectInfoForm,
    OriginContactInfo
  },

  props: {
    biddingBase: {
      type: Object,
      default: () => ({})
    },
    packNameList: {
      type: Array,
      default: () => []
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
      activeDims: ['1', '2', '3', '4', '5', '6', '7'],
      rules: {
        // 请选择招标流程
        extSouProcess: [{ required: true, message: this.$t('common.pleaseSelect') }],
        // 项目名称
        souName: [{ required: true, message: this.$t('common.pleaseInput') }],
        // 请选择评分规则
        extScoreRule: [{ required: true, message: this.$t('common.pleaseSelect') }],
        // 请选择招标书审批人
        approveFullName: [{ required: true, message: this.$t('common.pleaseSelect') }],
        // 是否缴纳保证金
        // extEarnestFlag: [{ required: true, message: this.$t('common.pleaseSelect') }],
        // 姓名
        linkman: [{ required: true, message: this.$t('common.pleaseInput') }],
        // 邮箱
        // email: [
        //   { required: true, message: this.$t('bidMod.bidMsgList[24]') },
        //   {
        //     validator: (_rule, value, callback) => {
        //       if (!value) {
        //         // 请输入邮箱
        //         callback(new Error(this.$t('bidMod.bidMsgList[24]')))
        //       } else if (!isEmail(value)) {
        //         // 邮箱格式不合法
        //         callback(new Error(this.$t('bidMod.bidMsgList[25]')))
        //       }
        //       callback()
        //     },
        //     trigger: 'blur'
        //   }
        // ],
        // 电话
        tel: [
          { required: true, message: this.$t('common.pleaseInput') }
          // {
          //   validator: (_rule, value, callback) => {
          //     if (!value) {
          //       callback()
          //     } else if (!isMobile(value)) {
          //       // 手机格式不合法
          //       callback(new Error(this.$t('bidMod.bidMsgList[26]')))
          //     }
          //     callback()
          //   },
          //   trigger: 'blur'
          // }
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
    }
  },

  created () {},

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

      // 校验招标工作小组 工号必填，评标组长只有一个
      let groupList = this.$refs.workGroup.getParamsData()
      let groupFlag = groupList.some(item => !item.userName)
      if (groupFlag) {
        this.$message.error('招标工作小组工号必填')
        return
      }
      let count = 0
      groupList.map(item => {
        if (item.groupRole == 'LEADER') count++
      })
      if (count > 1) {
        this.$message.error('评标组长只能设置一位')
        return
      }

      // 获取内外部附件
      const { applyFileList, bidFileList } = this.$refs.enclosure.getParamsData()
      // 校验附件至少存在一个
      if (applyFileList.length == 0) {
        this.$message.error('至少上传一份申请资料')
        return
      }
      let applyFlag = applyFileList.some(item => !item.souDocId)
      if (applyFlag) {
        this.$message.error('请上传申请资料附件')
        return
      }
      if (bidFileList.length == 0) {
        this.$message.error('至少上传一份招标文件')
        return
      }
      let bidFlag = bidFileList.some(item => !item.souDocId)
      if (bidFlag) {
        this.$message.error('请上传招标文件附件')
        return
      }

      // 校验招标计划时间必填
      const flag = this.$refs.biddingPlan.timeFillIn()
      if (!flag) {
        this.$message.error('请填写招标计划时间')
        return
      }
      const timeValid = this.$refs.biddingPlan.timeValidate()
      if (!timeValid) {
        this.$message.error('请检查招标计划时间是否填写正确')
        return
      }

      let submitData = {
        // 项目基础信息
        project: this.biddingBaseInfo,
        // 工作小组
        groupList,
        // 内部附件信息
        applyFileList,
        // 外部附件信息
        bidFileList,
        // 招标计划
        planList: this.projectInfoData.planList,
        // 是否是暂存
        tempSave: type !== 'nextOne'
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
