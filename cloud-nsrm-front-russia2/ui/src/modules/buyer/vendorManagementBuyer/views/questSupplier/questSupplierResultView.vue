<template>
  <el-container
    class="questSupplierResultView"
    direction="vertical"
  >
    <el-main>
      <CWorkflowMulti
        ref="workflowMulti"
        v-model="activeTabName"
        :fun-params="workflowParamsInfo"
        :button-config-info="buttonConfigInfo"
        :button-custom="buttonCustom"
        @tab-click="workflowView"
        @workflow-handler="workflowHandler"
        @click-handler="type => saveDataHandle(type)"
        @submit-direct="type => saveDataHandle(type)"
        @confirm="(type, comment) => confirmDataHandle(type, comment)"
        @close-tab="back"
      >
        <div class="form-container">
          <div class="fillInfoSec">
            <el-form
              ref="form"
              :model="form"
              :rules="rules"
            >
              <srm-row :gutter="32">
                <srm-col :span="6">
                  <el-form-item
                    prop="questTemplateTypeName"
                    :label="$t('vendorMod.questTemplateType')"
                  >
                    <el-input
                      v-model="form.questTemplateTypeName"
                      disabled
                    />
                  </el-form-item>
                </srm-col>
                <srm-col :span="6">
                  <el-form-item
                    prop="questNo"
                    :label="$t('vendorMod.questNo')"
                  >
                    <el-input
                      v-model="form.questNo"
                      disabled
                    />
                  </el-form-item>
                </srm-col>
                <srm-col :span="6">
                  <el-form-item
                    prop="questName"
                    :label="$t('vendorMod.questName')"
                  >
                    <el-input
                      v-model="form.questName"
                      disabled
                    />
                  </el-form-item>
                </srm-col>
                <srm-col :span="6">
                  <el-form-item
                    prop="questTemplateOrgName"
                    :label="$t('vendorMod.questTemplateOrgName')"
                  >
                    <el-input
                      v-model="form.questTemplateOrgName"
                      disabled
                    />
                  </el-form-item>
                </srm-col>
              </srm-row>
              <srm-row :gutter="32">
                <srm-col :initCol="2">
                  <el-form-item
                    prop="companyName"
                    :label="$t('common.vendor')"
                  >
                    <el-input
                      v-model="form.companyName"
                      disabled
                    />
                  </el-form-item>
                </srm-col>
                <srm-col :initCol="2">
                  <el-form-item
                    prop="questFeedback"
                    :label="$t('vendorMod.questFeedback')"
                  >
                    <el-input
                      v-model="form.questFeedback"
                      disabled
                    />
                  </el-form-item>
                </srm-col>
              </srm-row>
            </el-form>
            <div class="fillInfo">
              <renderForm
                v-if="form.questTemplateId"
                ref="renderForm"
                :quest-template-id="form.questTemplateId"
                :quest-sup-id="form.questSupId"
                :disabled="true"
                :com-data="form.groupInfoList"
                opt-type="view"
              />
            </div>
          </div>
        </div>
      </CWorkflowMulti>
    </el-main>
  </el-container>
</template>
<script>
  import { tabTodoMixin } from '@/utils/mixins'
  import TableView from 'lib@/components/Table/TableView'
  import MainHeader from 'lib@/components/Table/MainHeader'
  import CToolbar from 'lib@/components/c-toolbar'
  import renderForm from 'modb@/vendorManagementBuyer/views/questTemplate/renderForm'
  import WorkflowCommon from '@/library/mixins/workflow-common'
  export default {
    name: 'QuestSupplierResultView',
    components: {
      MainHeader,
      CToolbar,
      TableView,
      renderForm
    },
    mixins: [tabTodoMixin, WorkflowCommon],
    data () {
      return {
        form: {
          questSupId: null,
          questNo: null,
          questName: null,
          questTemplateId: null,
          questTemplateType: null,
          questTemplateTypeName: null,
          questTemplateOrgId: null,
          questTemplateOrgCode: null,
          questTemplateOrgName: null,
          companyName: '',
          questFeedback: '',
          approvalStatus: '',
          groupInfoList: [],
          fieldInfoList: []
        },
        rules: {},
        readOnly: false,
        curRole: this.$store.getters.userType,
        curOpt: 'add'
      }
    },
    computed: {
      workflowBusinessId () {
        return this.form.questSupId
          ? this.form.questSupId
          : null
      },
      workflowTabDisabled () {
        // DRAFT:拟定;PUBLISH:已发布;WRITING:填写中;WRITED:已填写;SUBMITTED:已提交;REJECTED:已驳回;APPROVED:已审批;ABANDONED:已废弃;WITHDRAW:已撤回;PRE_PASS:预审通过;PRE_REJECTED:预审驳回
        return this.form.approvalStatus === 'DRAFT' || this.form.approvalStatus === 'PUBLISH' || this.form.approvalStatus === 'WRITING' || this.form.approvalStatus === 'WRITED'
      }
    },

    watch: {},
    created () {
      this.curOpt = this.$attrs.params.flag
      const { flag, row, readOnly = false } = this.$attrs.params
      this.getSupplierResultDetail(row.questSupId)
      // 屏蔽暂存按钮
      this.buttonConfigInfo.save.view = false
      this.buttonConfigInfo.submit.view = false
      this.buttonConfigInfo.cancel.view = false
    },
    methods: {
      async getWorkflowBusinessVariables () { // 定义流程变量，如果没有可以不添加
        return {
          formNo: this.form.questNo
        }
      },
      async getWorkflowBusinessType () {
        return 'questResultApprove'
      },
      getCWorkflowRefName () {
        return 'workflowMulti'
      },
      // 通过id查询供应商问卷填写内容
      getSupplierResultDetail (questSupId) {
        this.$http({
          url: '/api-sup/quest/questResult/getQuestResultDtoByQuestSupId',
          method: 'GET',
          params: { questSupId: questSupId },
          loading: true
        }).then(res => {
          let result = res.data
          this.form.questTemplateId = result.questTemplateId
          this.form.questSupId = result.questSupId
          this.form.questNo = result.questNo
          this.form.questName = result.questName
          this.form.questTemplateOrgId = result.questTemplateOrgId
          this.form.questTemplateOrgCode = result.questTemplateOrgCode
          this.form.questTemplateOrgName = result.questTemplateOrgName
          this.form.questTemplateType = result.questTemplateType
          this.form.questTemplateTypeName = result.questTemplateTypeName
          this.form.companyName = result.companyName
          this.form.questFeedback = result.questFeedback
          this.form.groupInfoList = result.groupInfoList
        }).catch(err => {
          console.log(err)
        })
      },
      back () {
        if (this.$attrs.params.flag == 'add') {
          this.$emit('tab-remove', 'questManagementFlow')
        } else {
          this.$emit('tab-remove', this.$attrs.params.tabName)
        }
        this.__setTabTodo('questManagementList.getQuerydata')
      }
    }
  }
</script>
<style scoped lang="scss">
  .fillInfoSec{
    padding: 15px 15px 50px;
    .fillInfo{
      margin-top: 20px;
    }
  }
</style>
