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
            <!-- <el-collapse-item title="专家申请" name="1"> -->
            <el-collapse-item :title="$t('cusEntry.supplement20250205.expertApplication')" name="1">
              <BaseInfo
                ref="baseInfo"
                :form.sync="form"
                :readonly="disabledFlag"
                mode="expertUpgrade"
              />
              <!-- <ScoreOrg
                ref="scoreOrg"
                :value.sync="form.orgList"
                :readonly="disabledFlag"
              /> -->
              <AdaptCat
                ref="adaptCat"
                class="mt-10"
                :value.sync="form.categoryList"
                :readonly="disabledFlag"
              />
              <!-- <WorkHistory
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
              /> -->
            </el-collapse-item>
            <!-- <el-collapse-item :title="$t('outsource.materialReqFile')" name="2">
              <FileDynamic
                ref="sceneAttachment"
                v-model="form.attachList"
                scene-module-code="SCENE_EXT_SOU_EXPERT"
                :business-id="expertApplyId"
                :editable="!disabledFlag"
              />
            </el-collapse-item> -->
          </el-collapse>
        </el-form>
      </CWorkflowMulti>
    </el-main>
  </el-container>
</template>
<script>
import WorkflowCommon from '@/library/mixins/workflow-common'
import { tabTodoMixin } from '@/utils/mixins'
import BaseInfo from './components/baseInfo'
import ScoreOrg from './components/scoreOrg'
import AdaptCat from './components/adaptCat'
import WorkHistory from './components/workHistory'
import RelativeWork from './components/relativeWork'
import FileDynamic from '@/library/components/c-file-management/file-dynamic'
import { expInfoHttp, expApplyHttp, commonType } from 'modcb@/expertLibrary/api'
import { transformMQL } from 'lib@/utils/util'

export default {
  name: 'ExpertUpgrade',
  components: {
    BaseInfo,
    FileDynamic,
    ScoreOrg,
    AdaptCat,
    WorkHistory,
    RelativeWork
  },
  mixins: [tabTodoMixin, WorkflowCommon],
  data () {
    return {
      procTitleObj: {}, // 审批流传参
      expertApplyId: null,
      form: {
        applyStatus: 'DRAFT',
        applyFromType: 'CHANGE', // 默认信息变更
        expertApplyId: null,
        expertApplyNo: null,
        applyBy: null,
        applyById: null,
        applyByNickname: null,
        highestDegree: null,
        studyDateTo: null,
        sex: null,
        orgOuName: null,
        departmentName: null,
        job: null,
        expertLevel: null,
        jobStatus: null,
        phone: null,
        hireDate: null,
        studyCollege: null,
        major: null,
        applyLevel: null,
        greenReason: null,
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
        jobRank: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }],
        phone: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }],
        upgradeReason: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }]
      },
      colValue: ['1', '2']
    }
  },
  computed: {
    urlParams () {
      return this.$attrs.params || {}
    },
    disabledFlag () {
      return !['DRAFT', 'REJECTED', 'WITHDRAW'].includes(this.form.applyStatus) || this.urlParams.flag === 'view'
    },
    viewUpdateButton () { // 用来控制保存、提交按钮是否可见。如果自定义按钮则无需添加
      return ['DRAFT', 'REJECTED', 'WITHDRAW'].includes(this.form.applyStatus) && this.urlParams.flag !== 'view'
    },
    viewWithDrawButton () { // 用于控制撤回按钮是否显示
      return ['APPROVING'].includes(this.form.applyStatus) && this.workflowParamsInfo.integrationMode === 'Push' && this.urlParams.flag !== 'view'
    },
    disabledUpdateButton () {
      return ['APPROVING'].includes(this.form.applyStatus) || this.urlParams.flag === 'view'
    },
    workflowBusinessId () { // 用来指定工作流的业务ID 单据ID 注意每一个单的ID不一样
      return this.expertApplyId || null
    },
    workflowTabDisabled () { // 用来控制审批流tab页是否禁用
      // 拟定 驳回 撤回 可编辑 单据
      return !this.expertApplyId || ['DRAFT'].includes(this.form.applyStatus)
    }
  },
  watch: {
    viewUpdateButton () {
      this.buttonConfigInfo.save.view = this.viewUpdateButton
      this.buttonConfigInfo.submit.view = this.viewUpdateButton
    },
    disabledUpdateButton () {
      this.buttonConfigInfo.save.disabled = this.disabledUpdateButton
      this.buttonConfigInfo.submit.disabled = this.disabledUpdateButton
    },
    viewWithDrawButton () {
      this.buttonConfigInfo.withdraw.view = this.viewWithDrawButton
    }
  },
  created () {
    const { row, flag } = this.urlParams
    this.expertApplyId = row.expertApplyId
    if (flag === 'add') {
      this.getFormDetail('init')
    } else {
      this.getFormDetail()
    }
    this.getButtonConfig()
  },
  methods: {
    getButtonConfig () {
      this.buttonConfigInfo.save.view = this.viewUpdateButton
      this.buttonConfigInfo.submit.view = this.viewUpdateButton
      this.buttonConfigInfo.save.disabled = this.disabledUpdateButton
      this.buttonConfigInfo.submit.disabled = this.disabledUpdateButton
      this.buttonConfigInfo.cancel.view = this.viewUpdateButton
      this.buttonConfigInfo.close.view = !this.viewUpdateButton
      this.buttonConfigInfo.withdraw.view = this.viewWithDrawButton
    },
    // 指定工作流的业务类型，在定义工作流时指定
    async getWorkflowBusinessType () {
      return 'MQL_SOU_EXPERT_CHANG'
    },
    async getWorkflowBusinessVariables () {
      return {
        procTitleObj: this.procTitleObj
      }
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
    // 工作履历-工作时间转换
    transformWorkDay () {
      const { workList } = this.form
      if (workList && workList.length) {
        workList.forEach(item => {
          item.workDay = [item.entryDate, item.quitDate || this.$dayjsParse(new Date())]
        })
      }
    },
    async getFormDetail (type) {
      let transformParams = transformMQL.save(commonType, [this.expertApplyId], 'getApplyInfoByApplyId')
      const response = await expApplyHttp.read(transformParams)
      if (response.data.records.length) {
        this.form = response.data.records[0]
        this.transformWorkDay()
        // this.loadFileInfo()
        if (type === 'init') {
          this.form.applyStatus = 'DRAFT'
          this.form.applyFromType = 'CHANGE'
          this.expertApplyId = this.form.expertApplyId = null
          this.form.expertApplyNo = null
        }
        const { attachList = [], categoryList = [], educationList = [], orgList = [], workList = [], workRelationList = [], ...rest } = this.form
        this.procTitleObj = rest
      }
    },
    initParams () { // 参数
      let params = JSON.parse(JSON.stringify(this.form))
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
      console.log('saveBill', handleType)
      if (handleType === 'submit') {
        const { orgList, categoryList } = this.initParams()
        const validForm = await this.validBill()
        if (!validForm) {
          this.__focus_error__()
          return
        }
        // if (!orgList.length) {
        //   // this.$message.warning('请至少新增一条评分组织')
        //   this.$message.warning(this.$t('cusEntry.supplement20250205.pleaseAddAtLeastOneRatingOrganization'))
        //   return
        // }
        if (!categoryList.length) {
          // this.$message.warning('请至少新增一条品类')
          this.$message.warning(this.$t('cusEntry.supplement20250205.pleaseAddAtLeastOneCategory'))
          return
        }
      }
      let action = handleType === 'save' ? 'tempSaveApply' : 'submitApply'
      let transformParams = transformMQL.save(commonType, [this.initParams()], action)
      const response = await expApplyHttp[handleType](transformParams)
      this.$message.success(this.$t('common.successSave'))
      if (response.data.records.length) {
        this.expertApplyId = this.form.expertApplyId = response.data.records[0].expertApplyId
        await this.getFormDetail()
        if (handleType === 'submit') {
          await this.handlerAfter(type)
        }
      }
    },
    back () {
      let { tabName } = this.$attrs.params
      this.$emit('tab-remove', tabName)
      // this.__setTabTodo('ExpertApplyList.getQueryData')
    }
  }
}
</script>
<style lang="scss" scoped>
.mt-10 {
  margin-top: 10px;
}
</style>
