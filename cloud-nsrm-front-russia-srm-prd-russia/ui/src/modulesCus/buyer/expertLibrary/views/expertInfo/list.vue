<template>
  <el-container class="flex-container" direction="vertical">
    <el-main>
      <CWorkflowMulti
        ref="workflowMulti"
        v-model="activeTabName"
        :fun-params="workflowParamsInfo"
        :button-config-info="buttonConfigInfo"
        :button-custom="buttonCustom"
        @tab-click="workflowView"
        @workflow-handler="workflowHandler"
        @click-handler="type => saveBill(type)"
        @submit-direct="type => saveBill(type)"
        @confirm="(type, comment) => saveBill(type, comment)"
        @close-tab="back"
      >
        <el-form ref="form" :model="form" :rules="formRules">
          <el-collapse v-model="colValue">
            <el-collapse-item title="专家申请" name="1">
              <BaseInfo
                ref="baseInfo"
                :form.sync="form"
                :readonly="disabledFlag"
              />
              <ScoreOrg
                ref="scoreOrg"
                :value.sync="form.orgList"
                :readonly="disabledFlag"
              />
              <AdaptCat
                ref="adaptCat"
                class="mt-10"
                :value.sync="form.categoryList"
                :readonly="disabledFlag"
              />
              <WorkHistory
                ref="workHistory"
                class="mt-10"
                :value.sync="form.workList"
                :readonly="disabledFlag"
              />
              <RelativeWork
                ref="relativeWork"
                class="mt-10"
                :value.sync="form.workRelationList"
                :readonly="disabledFlag"
              />
            </el-collapse-item>
            <el-collapse-item title="相关附件上传" name="2">
              <FileDynamic
                ref="sceneAttachment"
                v-model="form.attachList"
                scene-module-code="SCENE_EXT_SOU_EXPERT"
                :business-id="expertApplyId"
                :editable="!disabledFlag"
              />
            </el-collapse-item>
          </el-collapse>
        </el-form>
        <!-- 底部 --- 申请按钮 -->
        <template slot="buttonOne" class="buttonOneClass">
          <!-- 提交 - 适用于绿色通道帮其他人申请之后，专家本人补充工作履历、亲属工作单位、相关附件 -->
          <!-- <el-button v-if="isGreenUpdate" type="primary" @click="saveBill('GREEN')">
            提交
          </el-button> -->
          <!-- 用于专家升级时记录来源申请单号 -->
          <!-- 单据状态已审批 && 专家等级普通 && 没有来源fromApplyId -->
          <!-- <el-button v-if="form.applyStatus === 'APPROVED' && form.applyLevel === 'NORMAL' && !form.noApprovalPassExpertApplyId" type="primary" @click="handleUpgrade">
            升级申请
          </el-button> -->
          <!-- 有新的升级申请id:noApprovalPassExpertApplyId 并且专家等级是普通-->
          <!-- <el-button v-if="form.noApprovalPassExpertApplyId && form.applyLevel === 'NORMAL'" type="primary" @click="viewUpgrade">
            查看申请
          </el-button> -->
          <el-button v-if="form.applyStatus === 'APPROVED' && !form.noApprovalPassExpertApplyId" type="primary" @click="handleUpgrade">
            专家信息变更
          </el-button>
          <el-button v-if="form.noApprovalPassExpertApplyId" type="primary" @click="viewUpgrade">
            查看专家信息变更
          </el-button>
        </template>
      </CWorkflowMulti>

      <!-- 注册须知 -->
      <NoticeDialog
        :visible.sync="noticeDialogVisible"
        @confirm="noticeDialogConfirm"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import WorkflowCommon from '@/library/mixins/workflow-common'
import { transformMQL } from 'lib@/utils/util'
import BaseInfo from './components/baseInfo'
import FileDynamic from '@/library/components/c-file-management/file-dynamic'
import ScoreOrg from './components/scoreOrg'
import AdaptCat from './components/adaptCat'
import WorkHistory from './components/workHistory'
import RelativeWork from './components/relativeWork'
import NoticeDialog from './components/dialog/noticeDialog'
import ExpertUpgrade from './upgrade'
import { mapGetters } from 'vuex'
import { expInfoHttp, expApplyHttp, commonType } from 'modcb@/expertLibrary/api'

export default {
  name: 'ExpertInfoList',
  components: {
    BaseInfo,
    FileDynamic,
    ScoreOrg,
    AdaptCat,
    WorkHistory,
    RelativeWork,
    NoticeDialog
  },
  mixins: [tabTodoWatch, tabTodoMixin, WorkflowCommon],
  data () {
    return {
      expertApplyId: null,
      form: {
        applyStatus: 'DRAFT',
        applyFromType: 'INDEPENDENT', // 默认自主申请
        noApprovalPassExpertApplyId: null,
        expertApplyId: null,
        expertApplyNo: null,
        applyBy: null,
        applyById: null,
        applyByNickname: null,
        highestDegree: null,
        studyDateTo: null,
        sex: null,
        buName: null,
        buCode: null,
        buId: null,
        orgOuId: null,
        orgOuCode: null,
        orgOuName: null,
        departmentId: null,
        departmentCode: null,
        departmentName: null,
        job: null,
        jobRank: null,
        expertLevel: null,
        jobStatus: null,
        phone: null,
        hireDate: null,
        studyCollege: null,
        major: null,
        applyLevel: null,
        greenReason: null,
        ifGreenPersonUpdate: null, // Y N
        orgList: [],
        categoryList: [],
        educationList: [],
        workList: [],
        workRelationList: [],
        attachList: []
      },
      formRules: {
        applyFromType: [{ required: true, message: this.$t('common.requiredField'), trigger: 'change' }],
        applyBy: [{ required: true, message: this.$t('common.requiredField'), trigger: 'change' }],
        applyLevel: [{ required: true, message: this.$t('common.requiredField'), trigger: 'change' }],
        studyCollege: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }],
        major: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }],
        phone: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }]
      },
      colValue: ['1', '2'],
      // 判断是否已注册，没注册需打开弹窗
      noticeDialogVisible: false
    }
  },
  computed: {
    ...mapGetters(['userInfo']),
    disabledFlag () {
      return !['DRAFT', 'REJECTED', 'WITHDRAW'].includes(this.form.applyStatus)
    },
    viewUpdateButton () { // 用来控制保存、提交按钮是否可见。如果自定义按钮则无需添加
      return ['DRAFT', 'REJECTED', 'WITHDRAW'].includes(this.form.applyStatus)
    },
    viewWithDrawButton () { // 用于控制撤回按钮是否显示
      return ['APPROVING'].includes(this.form.applyStatus) && this.workflowParamsInfo.integrationMode === 'Push'
    },
    disabledUpdateButton () {
      return ['APPROVING'].includes(this.form.applyStatus)
    },
    workflowBusinessId () { // 用来指定工作流的业务ID 单据ID 注意每一个单的ID不一样
      return this.expertApplyId || null
    },
    workflowTabDisabled () { // 用来控制审批流tab页是否禁用
      // 拟定 驳回 撤回 可编辑 单据
      return !this.expertApplyId || ['DRAFT'].includes(this.form.applyStatus)
    },
    isGreenUpdate () {
      return this.form.applyStatus === 'APPROVED' && this.form.applyFromType === 'GREEN_CHANNEL' && this.form.ifGreenPersonUpdate === 'N'
    }
  },
  watch: {
    viewUpdateButton () {
      this.buttonConfigInfo.save.view = this.viewUpdateButton
      this.buttonConfigInfo.submit.view = this.viewUpdateButton
    },
    viewWithDrawButton () {
      this.buttonConfigInfo.withdraw.view = this.viewWithDrawButton
    },
    disabledUpdateButton () {
      this.buttonConfigInfo.save.disabled = this.disabledUpdateButton
      this.buttonConfigInfo.submit.disabled = this.disabledUpdateButton
    }
  },
  created () {
    this.getButtonConfig()
    this.getApplyInfoById().then(response => {
    // 未注册
      if (!response || !response.data.records.length || response.data.records[0].expertApplyId === -1) {
        this.noticeDialogVisible = true
      } else {
        this.form = response.data.records[0]
        this.expertApplyId = this.form.expertApplyId
        this.transformWorkDay()
        this.loadFileInfo()
        if (this.form.applyBy && ['DRAFT'].includes(this.form.applyStatus)) {
          this.getHrUserInfo(this.form.applyBy, this.form)
        }
      }
    })
  },
  methods: {
    getApplyInfoById () {
      return new Promise(async resolve => {
        let transformParams = transformMQL.save(commonType, [{ userId: this.userInfo.userId, priorityApprovalPass: true }], 'getLatestApplyInfoByUserId')
        const response = await expInfoHttp.getApplyInfoByUserId(transformParams)
        resolve(response)
      })
    },
    getButtonConfig () {
      this.buttonConfigInfo.save.view = this.viewUpdateButton
      this.buttonConfigInfo.submit.view = this.viewUpdateButton
      this.buttonConfigInfo.save.disabled = this.disabledUpdateButton
      this.buttonConfigInfo.submit.disabled = this.disabledUpdateButton
      this.buttonConfigInfo.cancel.view = false
      this.buttonConfigInfo.close.view = false
      this.buttonConfigInfo.withdraw.view = this.viewWithDrawButton
    },
    // 指定工作流的业务类型，在定义工作流时指定
    async getWorkflowBusinessType () {
      return 'MQL_SOU_EXPERT_APPLY_INIT'
    },
    // 获取ref值
    getCWorkflowRefName () {
      return 'workflowMulti' // 对应CWorkflowMulti标签中的ref
    },
    loadFileInfo (fileRef = 'sceneAttachment') {
      this.$nextTick(() => {
        this.$refs[fileRef].loadFileInfo()
      })
    },
    /** 注册须知弹窗关闭 */
    async noticeDialogConfirm () {
      const val = this.userInfo
      this.noticeDialogVisible = false
      this.form.applyById = val ? val.userId : null
      this.form.applyBy = val ? val.username : null
      this.form.applyByNickname = val ? val.nickname : null
      this.form.applyByCode = val ? val.ceeaEmpNo : null // 工号
      this.form.phone = val ? val.phone : null
      this.getHrUserInfo(val.username, this.form)
      this.loadFileInfo()
    },
    async getHrUserInfo (personnelNo, form) {
      if (personnelNo) {
        const response = await expInfoHttp.getHrUserInfo({ personnelNo })
        if (response && response.data) {
          let result = response.data || {}
          form.highestDegree = form.highestDegree || result.diploma // 最高学历
          form.sex = form.sex || result.sex?.toString() // 性别
          form.job = form.job || result.dutyName // 职务
          form.jobRank = form.jobRank || result.rankName // 序列等级
          form.jobStatus = form.jobStatus || result.state?.toString() // 在职状态
          form.hireDate = form.hireDate || result.admissionDate // 入厂时间
          form.studyDateTo = form.studyDateTo || result.graduateTime // 毕业时间
          form.studyCollege = form.studyCollege || result.graduateSchool // 毕业院校
          form.major = form.major || result.professional // 所学专业
        }
        if (!form.orgOuId) {
          const { data } = await expInfoHttp.getHrUserOrgnizationByUsername({ username: personnelNo })
          form.orgOuId = data.ouOrganization?.organizationId // 所属单位
          form.orgOuCode = data.ouOrganization?.organizationCode
          form.orgOuName = data.ouOrganization?.organizationName
          form.departmentId = data.departmentOrganization?.organizationId // 科室/部门
          form.departmentName = data.departmentOrganization?.organizationName
          form.buId = data.buOrganization?.organizationId
          form.buCode = data.buOrganization?.organizationCode
          form.buName = data.buOrganization?.organizationName
        }
        form.orgOuId && this.$refs.baseInfo.bumenListFun()
      }
    },
    // 工作履历-工作时间转换
    transformWorkDay () {
      const { workList } = this.form
      if (workList && workList.length) {
        workList.forEach(item => {
          item.workDay = [item.entryDate, item.quitDate]
        })
      }
    },
    async getFormDetail () {
      let transformParams = transformMQL.save(commonType, [this.expertApplyId], 'getApplyInfoByApplyId')
      const response = await expApplyHttp.read(transformParams)
      if (response.data.records.length) {
        const { studyDateTo, studyCollege, major } = response.data.records[0].educationList[0] || {}
        this.form = Object.assign({}, response.data.records[0], { studyDateTo, studyCollege, major })
        this.transformWorkDay()
        this.loadFileInfo()
      }
    },
    initParams () { // 参数
      let params = JSON.parse(JSON.stringify(this.form))
      params.educationList = [{
        studyDateTo: this.form.studyDateTo,
        studyCollege: this.form.studyCollege,
        major: this.form.major
      }]
      let { hireDate, educationList = [] } = params
      if (hireDate) {
        params.hireDate = this.$dayjsParse(hireDate)
      }
      if (educationList.length) {
        for (let item of educationList) {
          item.studyDateTo = item.studyDateTo ? this.$dayjsParse(item.studyDateTo) : null
        }
      }
      const { workList } = params
      if (workList && workList.length) {
        workList.forEach(item => {
          delete item.workDay
        })
      }
      return params
    },
    async validBill () {
      return new Promise(async (resolve) => {
        let validForm
        await this.$refs.form.validate(valid => { validForm = valid })
        resolve(validForm)
      })
    },
    async saveBill (type) {
      let handleType = type.toLowerCase()
      let params = this.initParams()
      console.log('saveBill', handleType)
      if (handleType === 'save') {
        const { applyLevel } = params
        if (!applyLevel) {
          this.$message.warning('申报等级不能为空')
          return
        }
      }
      if (handleType === 'submit') {
        const { orgList, categoryList, workList, workRelationList } = params
        const validForm = await this.validBill()
        if (!validForm) {
          this.__focus_error__()
          return
        }
        if (!orgList.length) {
          this.$message.warning('请至少新增一条评分组织')
          return
        }
        if (!categoryList.length) {
          this.$message.warning('请至少新增一条品类')
          return
        }
        if (!workList.length) {
          this.$message.warning('请至少新增一条工作履历')
          return
        }
        if (!workRelationList.length) {
          this.$message.warning('请至少新增一条亲属工作单位')
          return
        }
      }
      let action = handleType === 'save' ? 'tempSaveApply' : 'submitApply'
      if (handleType === 'green') {
        params.ifGreenPersonUpdate = 'Y'
      }
      let transformParams = transformMQL.save(commonType, [params], action)
      let operateType = handleType === 'save' ? 'save' : 'submit'
      const response = await expApplyHttp[operateType](transformParams)
      this.$message.success(this.$t('common.successSave'))
      if (response.data.records.length) {
        this.expertApplyId = this.form.expertApplyId = response.data.records[0].expertApplyId
        await this.getFormDetail()
        if (handleType === 'submit') {
          await this.handlerAfter(type)
          await this.getFormDetail()
        }
      }
    },
    back () {

    },
    /** 升级申请 */
    handleUpgrade () {
      const tab = {
        component: ExpertUpgrade,
        params: {
          flag: 'add',
          row: this.form,
          tabName: 'expertUpgrade'
        },
        title: '专家信息变更',
        name: 'expertUpgrade'
      }
      this.$emit('tab-add', tab)
    },

    /** 查看申请 */
    async viewUpgrade () {
      const noApprovalPassExpertApplyId = this.form.noApprovalPassExpertApplyId
      let transformParams = transformMQL.save(commonType, [noApprovalPassExpertApplyId], 'getApplyInfoByApplyId')
      const response = await expApplyHttp.read(transformParams)
      if (response.data.records.length) {
        const {
          applyStatus
        } = response.data.records[0]
        const tab = {
          component: ExpertUpgrade,
          params: {
            flag: ['DRAFT', 'REJECTED', 'WITHDRAW'].includes(applyStatus) ? 'edit' : 'view',
            row: {
              expertApplyId: noApprovalPassExpertApplyId
            },
            tabName: 'expertUpgrade' + noApprovalPassExpertApplyId
          },
          title: '查看专家信息变更',
          name: 'expertUpgrade' + noApprovalPassExpertApplyId
        }
        this.$emit('tab-add', tab)
      }
    }
  }
}
</script>
<style lang="scss" scoped>
.mt-10 {
  margin-top: 10px;
}
</style>
