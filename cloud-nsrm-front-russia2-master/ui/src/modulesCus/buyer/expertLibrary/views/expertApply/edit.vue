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
            <el-collapse-item :title="$t('cusEntry.supplement20250205.expertApplication')" name="1">
              <BaseInfo
                ref="baseInfo"
                :form.sync="form"
                :readonly="disabledFlag"
                mode="expertApply"
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
                v-if="!isGreen"
                ref="workHistory"
                class="mt-10"
                :value.sync="form.workList"
                :readonly="true"
              />
              <RelativeWork
                v-if="!isGreen"
                ref="relativeWork"
                class="mt-10"
                :value.sync="form.workRelationList"
                :readonly="true"
              /> -->
              <SrmRow v-if="isGreen" class="mt-10">
                <SrmCol :init-col="1">
                  <!-- <el-form-item label="绿色通道原因" prop="greenReason"> -->
                  <el-form-item :label="$t('cusEntry.supplement20250205.greenChannelReason')" prop="greenReason">
                    <el-input v-model="form.greenReason" type="textarea" :autosize="{minRows:4,maxRows:6}" :disabled="disabledFlag" />
                  </el-form-item>
                </SrmCol>
              </SrmRow>
            </el-collapse-item>
            <!-- <el-collapse-item v-if="!isGreen" title="相关附件上传" name="2">
              <FileDynamic
                ref="sceneAttachment"
                v-model="form.attachList"
                scene-module-code="SCENE_EXT_SOU_EXPERT"
                :business-id="expertApplyId"
                :editable="false"
              />
            </el-collapse-item> -->
          </el-collapse>
        </el-form>
      </CWorkflowMulti>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoMixin } from '@/utils/mixins'
import WorkflowCommon from '@/library/mixins/workflow-common'
import FileDynamic from '@/library/components/c-file-management/file-dynamic'
import BaseInfo from 'modcb@/expertLibrary/views/expertInfo/components/baseInfo'
import ScoreOrg from 'modcb@/expertLibrary/views/expertInfo/components/scoreOrg'
import AdaptCat from 'modcb@/expertLibrary/views/expertInfo/components/adaptCat'
import WorkHistory from 'modcb@/expertLibrary/views/expertInfo/components/workHistory'
import RelativeWork from 'modcb@/expertLibrary/views/expertInfo/components/relativeWork'
import { transformMQL } from 'lib@/utils/util'
import { expApplyHttp, commonType } from 'modcb@/expertLibrary/api'

export default {
  name: 'ExpertApplyDetail',
  components: {
    BaseInfo,
    ScoreOrg,
    AdaptCat,
    WorkHistory,
    RelativeWork,
    FileDynamic
  },
  mixins: [tabTodoMixin, WorkflowCommon],
  data () {
    return {
      expertApplyId: null,
      colValue: ['1', '2'],
      form: {
        applyStatus: 'DRAFT',
        applyFromType: 'GREEN_CHANNEL',
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
        jobRank: null,
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
        greenReason: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }]
      }
    }
  },
  computed: {
    urlParams () {
      return this.$attrs.params || {}
    },
    disabledFlag () {
      return !!['view', 'approval', 'manage'].includes(this.urlParams.flag)
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
      return !this.form.blackId ||
              (['DRAFT'].includes(this.form.applyStatus) && this.urlParams.flag !== 'approve')
    },
    // 是否绿色通道
    isGreen () {
      return this.form.applyFromType === 'GREEN_CHANNEL'
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
    this.getButtonConfig()
    this.expertApplyId = this.urlParams.row.expertApplyId
    if (this.expertApplyId) {
      this.getFormDetail()
    }
  },
  methods: {
    // 指定工作流的业务类型，在定义工作流时指定
    async getWorkflowBusinessType () {
      let res = this.$attrs.params.row.applyFromType || this.form.applyFromType
      return res == 'INDEPENDENT' ? 'MQL_SOU_EXPERT_APPLY' : 'MQL_SOU_EXPERT_CHANG'
    },
    async getWorkflowBusinessVariables () {
      const procTitleObj = { expertApplyNo: this.form.expertApplyNo }
      return {
        procTitleObj
      }
    },
    // 获取ref值
    getCWorkflowRefName () {
      return 'workflowMulti' // 对应CWorkflowMulti标签中的ref
    },
    getButtonConfig () {
      this.buttonConfigInfo.save.view = this.viewUpdateButton
      this.buttonConfigInfo.submit.view = this.viewUpdateButton
      this.buttonConfigInfo.save.disabled = this.disabledUpdateButton
      this.buttonConfigInfo.submit.disabled = this.disabledUpdateButton
      this.buttonConfigInfo.cancel.view = !this.disabledFlag
      this.buttonConfigInfo.close.view = this.disabledFlag
      this.buttonConfigInfo.withdraw.view = this.viewWithDrawButton
    },
    back () {
      let { tabName } = this.$attrs.params
      this.$emit('tab-remove', tabName)
      this.__setTabTodo('ExpertApplyList.getQueryData')
    },
    loadFileInfo (refName = 'sceneAttachment') {
      this.$nextTick(() => {
        this.$refs[refName].loadFileInfo()
      })
    },
    async getFormDetail () {
      let transformParams = transformMQL.save(commonType, [this.expertApplyId], 'getApplyInfoByApplyId')
      const response = await expApplyHttp.read(transformParams)
      if (response.data.records.length) {
        const { studyDateTo, studyCollege, major } = response.data.records[0].educationList[0] || {}
        this.form = Object.assign({}, response.data.records[0], { studyDateTo, studyCollege, major })
        if (!this.isGreen) {
          // this.loadFileInfo()
        }
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
      if (handleType === 'save') {
        const { applyLevel } = this.initParams()
        if (!applyLevel) {
          // this.$message.warning('申报等级不能为空')
          this.$message.warning(this.$t('cusEntry.supplement20250205.applicationLevelCannotBeEmpty'))
          return
        }
      }
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
    }
  }
}
</script>
<style lang="scss" scoped>
.mt-10 {
  margin-top: 10px;
}
</style>
