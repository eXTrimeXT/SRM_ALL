<template>
  <el-container>
    <el-main>
      <!-- <CWorkflowMulti
        ref="workflowMulti"
        v-model="activeTabName"
        :fun-params="workflowParamsInfo"
        :button-config-info="buttonConfigInfo"
        :button-custom="buttonCustom"
        @tab-click="workflowView"
        @workflow-handler="workflowHandler"
        @click-handler="type => saveOrSubmitBill(type)"
        @submit-direct="type => saveOrSubmitBill(type)"
        @confirm="(type, comment) => saveOrSubmitBill(type, comment)"
        @close-tab="goBack"
      > -->
      <ApprovalProcess
        :business-id="workflowBusinessId"
        business-type="RECRUIT"
        :approval-status="detailForm.status"
        :status-map="statusMap"
        :readonly="$attrs.params.flag === 'view'"
        :operation-pre-options="operationPreOptions"
        @approval-handler-callback="approvalHandlerCallback"
      >
        <el-collapse v-model="activeNum">
          <el-collapse-item :title="$t('meeting.baseInfo')" name="1">
            <el-form
              ref="detailForm"
              :model="detailForm"
              :rules="rules"
              :disabled="isReadOnly"
            >
              <srm-row>
                <srm-col :init-col="1">
                  <el-form-item
                    :label="$t('cusEntry.recruitment.recruitName')"
                    prop="name"
                  >
                    <el-input v-model="detailForm.name" :placeholder="$t('cusEntry.common.pleaseFill')" />
                  </el-form-item>
                </srm-col>
                <srm-col v-if="!approvalFlag" :init-col="1">
                  <el-form-item
                    :label="$t('cusEntry.recruitment.subtitle')"
                    prop="title"
                  >
                    <el-input v-model="detailForm.title" :placeholder="$t('cusEntry.common.pleaseFill')" />
                  </el-form-item>
                </srm-col>
                <srm-col :init-col="4">
                  <el-form-item
                    :label="$t('cusEntry.recruitment.stopTime')"
                    prop="deadlineTime"
                  >
                    <el-date-picker
                      v-model="detailForm.deadlineTime"
                      type="datetime"
                      value-format="yyyy-MM-dd HH:mm:ss"
                      :placeholder="$t('common.pleaseSelect')"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col :init-col="4">
                  <el-form-item
                    :label="$t('common.category')"
                    prop="categoryId"
                  >
                    <QuickSearch
                      :disable="isReadOnly"
                      :showInput="detailForm.categoryName"
                      show-key="categoryId"
                      :scope-data="detailForm"
                      :placeholder="$t('common.pleaseSelect')"
                      name="scc_base_purchase_category_url"
                      @close-quicksearch="getCategoryObj"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col v-if="!approvalFlag" :init-col="4">
                  <el-form-item
                    :label="$t('common.status')"
                    prop="status"
                  >
                    <dict-select v-model="detailForm.status" code="RECRUIT_STATUS" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col :init-col="4">
                  <el-form-item
                    :label="$t('common.creator')"
                    prop="createdUserName"
                  >
                    <el-input v-model="detailForm.createdUserName" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col v-if="!approvalFlag" :init-col="4">
                  <el-form-item
                    :label="$t('common.creationDate')"
                    prop="creationDate"
                  >
                    <el-date-picker v-model="detailForm.creationDate" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col v-if="!approvalFlag" :init-col="4">
                  <el-form-item
                    :label="$t('bidMod.publishDate')"
                    prop="publishTime"
                  >
                    <el-date-picker v-model="detailForm.publishTime" disabled />
                  </el-form-item>
                </srm-col>
              </srm-row>
            </el-form>
          </el-collapse-item>
          <el-collapse-item :title="$t('cusEntry.recruitment.recruitContent')" name="2">
            <Tinymce
              id="groupTipOffTinymce"
              v-model="content"
              :height="500"
              @setup="ready"
            />
          </el-collapse-item>
          <el-collapse-item :title="$t('contractMod.addUploadFile')" name="3">
            <el-button
              v-if="!isReadOnly"
              type="primary"
              style="margin-bottom: 10px;"
              @click="addUploadOne"
            >
              {{ $t('common.add') }}
            </el-button>
            <el-table
              border
              :data="fileList"
              style="width: 100%"
              max-height="200"
            >
              <el-table-column
                align="center"
                type="index"
                :label="$t('common.sort')"
                width="60"
              />
              <el-table-column
                align="center"
                prop="fileName"
                :label="$t('bidMod.fileName')"
                min-width="150"
              >
                <template slot-scope="scope">
                  <SrmCommonFile
                    :extra-data="fileInfo"
                    :default-file="{
                      fileId: scope.row.fileId,
                      fileName: scope.row.fileName
                    }"
                    :readonly="isReadOnly"
                    @on-change="({file}) => uploadSuccess(file, scope.row)"
                  />
                </template>
              </el-table-column>
              <el-table-column
                align="center"
                prop="createdFullName"
                :label="$t('quota.uploadBy')"
                min-width="150"
              />
              <el-table-column
                align="center"
                prop="creationDate"
                :label="$t('components.fileupload.uploadDate')"
                min-width="150"
              />
              <el-table-column
                v-if="!isReadOnly"
                align="center"
                :label="$t('common.operation')"
                width="120"
              >
                <template slot-scope="scope">
                  <el-button type="text" @click="deleteItem(scope.$index, scope.row)">
                    {{ $t('common.delete') }}
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-collapse-item>
          <el-collapse-item v-if="vendorListShow" :title="$t('cusEntry.common.signUpSupplier')" name="4">
            <el-table
              border
              max-height="300px"
              :data="vendorList"
            >
              <el-table-column
                align="center"
                type="index"
                fixed="left"
                :label="$t('common.sort')"
                width="55"
              />
              <el-table-column
                align="center"
                prop="companyName"
                :label="$t('cusEntry.common.signUpCompany')"
                minWidth="100"
              />
              <el-table-column
                align="center"
                prop="contactName"
                :label="$t('cusEntry.common.signUpPerson')"
                minWidth="100"
              />
              <el-table-column
                align="center"
                prop="contactMobile"
                :label="$t('cusEntry.common.signUpPhone')"
                minWidth="100"
              />
              <el-table-column
                align="center"
                prop="contactEmail"
                :label="$t('cusEntry.common.signUpEmail')"
                minWidth="100"
              />
            </el-table>
          </el-collapse-item>
        </el-collapse>
      </ApprovalProcess>
      <!-- </CWorkflowMulti> -->
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import { transformMQL } from 'lib@/utils/util'
import { parseTime } from '@/utils'
import CToolbar from 'lib@/components/c-toolbar'
import QuickSearch from 'lib@/components/QuickSearch'
import Tinymce from '@/modulesCus/buyer/biddingSettings/components/Tinymce'
import WorkflowCommon from '@/library/mixins/workflow-common'
import ApprovalProcess from 'modc@/components/approval-process'

export default {
  name: 'RecruitmentDetail',
  components: {
    QuickSearch,
    CToolbar,
    Tinymce,
    ApprovalProcess
  },
  mixins: [tabTodoWatch, tabTodoMixin, WorkflowCommon],
  data () {
    return {
      activeNum: ['1', '2', '3', '4'],
      detailForm: {
        recruitId: null,
        name: null,
        title: null,
        deadlineTime: null,
        categoryId: null,
        categoryCode: null,
        categoryName: null,
        status: null,
        createdUserName: null,
        creationDate: null,
        publishTime: null
      },
      rules: {
        name: [{ required: true, message: this.$t('cusEntry.common.pleaseFill'), trigger: ['blur', 'change'] }],
        title: [{ required: true, message: this.$t('cusEntry.common.pleaseFill'), trigger: ['blur', 'change'] }],
        deadlineTime: [{ required: true, message: this.$t('common.pleaseSelect'), trigger: ['blur', 'change'] }],
        categoryId: [{ required: true, message: this.$t('common.pleaseSelect'), trigger: ['blur', 'change'] }]
      },
      content: null,
      recruitContentId: null,
      vendorList: [],
      fileList: [],
      fileListDel: [],
      fileInfo: {
        uploadType: 'DEF', // 固定
        sourceType: 'WEB_APP', // 固定
        fileModular: 'base', // 模块
        fileFunction: 'commonFile', // 功能
        fileType: 'images' // 类型
      },
      isReadOnly: false,
      vendorListShow: false,
      operationPreOptions: {
        nextStep: this.preNextStepHandler
      },
      statusMap: {
        DRAFT: 'DRAFT', // 拟定
        SUBMITTED: 'APPROVING', // 已提交
        APPROVED: 'APPROVED', // 审批通过
        REJECTED: 'REJECTED', // 已驳回
        WITHDRAW: 'WITHDRAW', // 已撤回
        ABANDONED: 'ABANDON' // 已废弃
      }
    }
  },
  computed: {
    approvalFlag () { // 审批流页面字段展示标识
      return this.$attrs.params.approvalFlag || false
    },
    workflowBusinessId () {
      // 用来指定工作流的业务ID
      return this.detailForm ? this.detailForm.recruitId : null
    },
    workflowTabDisabled () {
      // 拟定状态的单据 流程审批按钮失效
      return ['DRAFT'].includes(this.detailForm.status)
    },
    viewUpdateButton () {
      return ['DRAFT', 'REJECTED', 'WITHDRAW'].includes(this.detailForm.status) && !this.isReadOnly
    },
    viewWithDrawButton () { // 用于控制撤回按钮是否显示
      return ['APPROVING'].includes(this.detailForm.status) &&
        this.workflowParamsInfo.integrationMode === 'Push' &&
        this.$attrs.params.flag === 'approval'
    }
  },
  watch: {
    // 监听保存提交 按钮变更状态，如果自定义按钮则无需添加
    viewUpdateButton () {
      this.buttonConfigInfo.save.view = this.viewUpdateButton
      this.buttonConfigInfo.submit.view = this.viewUpdateButton
    },
    viewWithDrawButton () {
      this.buttonConfigInfo.withdraw.view = this.viewWithDrawButton
    }
  },
  created () {
    const { nickname } = this.$store.getters.userInfo
    const { flag, row } = this.$attrs.params
    this.isReadOnly = ['view', 'approval'].includes(flag)
    if (flag == 'add') {
      this.detailForm.status = 'DRAFT'
      this.detailForm.createdUserName = nickname
      this.detailForm.creationDate = parseTime(new Date(), '{y}-{m}-{d}')
    } else {
      this.vendorListShow = row.status === 'APPROVED'
      this.getFormDetail(row.recruitId)
    }

    this.buttonConfigInfo.withdraw.view = this.viewWithDrawButton
    this.buttonConfigInfo.save.view = this.viewUpdateButton
    this.buttonConfigInfo.submit.view = this.viewUpdateButton
    this.buttonConfigInfo.cancel.view = !this.isReadOnly
    this.buttonConfigInfo.close.view = this.isReadOnly
  },
  methods: {
    // 下一步前置处理
    async preNextStepHandler () {
      let validForm = false
      await this.$refs.detailForm.validate(valid => {
        validForm = valid
      })
      if (!validForm) {
        this.$message.warning('请填写完必填项')
        return false
      }
      if (this.fileList.some(item => !item.fileId)) {
        this.$message.warning('请上传附件')
        return false
      }
      // 调用暂存接口
      const params = {
        ...this.detailForm,
        contentList: [{
          recruitContentId: this.recruitContentId,
          content: this.content
        }],
        fileList: [ ...this.fileList, ...this.fileListDel ]
      }
      const saveData = transformMQL.save('Recruit', [params], 'saveOrUpdate')
      const response = await this.$http({
        url: '/api-sou/api-ql/Recruit/saveOrUpdate',
        method: 'POST',
        data: saveData,
        loading: true
      })
      this.detailForm = response.data[0]
      return true
    },
    // 审批流操作回调
    approvalHandlerCallback (type) {
      switch (type) {
      case 'save':
        this.saveRecruit('SAVE')
        break
      case 'submit':
        this.goBack()
        break
      default:
        break
      }
    },
    getCWorkflowRefName () {
      return 'workflowMulti' // 对应CWorkflowMulti标签中的ref
    },
    // 指定工作流的业务类型，在定义工作流时指定
    async getWorkflowBusinessType () {
      return 'RECRUIT' // 这里之后是和后端对接配置好的流程审批模块
    },
    goBack () {
      if (this.$attrs.params.flag !== 'add') {
        this.$emit('tab-remove', 'recruitmentDetail' + this.$attrs.params.row.recruitId)
      } else {
        this.$emit('tab-remove', 'recruitmentDetail')
      }
      this.__setTabTodo('recruitmentList.getQueryData')
    },
    getCategoryObj (val, scope) {
      scope.categoryId = val ? val.categoryId : ''
      scope.categoryCode = val ? val.categoryCode : ''
      scope.categoryName = val ? val.categoryName : ''
    },
    ready (editorInstance) {
      if (this.isReadOnly) {
        editorInstance.setMode('readonly')
      }
    },
    getFormDetail (recruitId) {
      const searchData = transformMQL.save(
        'Recruit',
        [recruitId],
        'read',
        {
          '*': {},
          'contentList': { '*': {} },
          'vendorList': { '*': {} },
          'fileList': { '*': {} }
        }
      )
      this.$http({
        url: '/api-sou/api-ql/Recruit/read',
        method: 'POST',
        data: searchData,
        loading: true
      }).then(res => {
        if (res && res.data && res.data.length) {
          const { contentList = [], vendorList = [], fileList = [], ...rest } = res.data[0]
          this.detailForm = { ...rest }
          this.recruitContentId = contentList[0]?.recruitContentId
          this.content = contentList[0]?.content
          this.vendorList = vendorList
          this.fileList = fileList
        }
      })
    },
    addUploadOne () {
      this.fileList.push({
        recruitId: this.detailForm.recruitId,
        fileId: null,
        fileName: null,
        createdFullName: null,
        creationDate: null
      })
    },
    uploadSuccess (file, row) {
      const { fileId, fileName, createdFullName, creationDate } = file || {}
      row.fileId = fileId
      row.fileName = fileName
      row.createdFullName = createdFullName
      row.creationDate = creationDate
    },
    // 删除
    deleteItem (index, row) {
      if (row.recruitFileId) {
        this.fileListDel.push({ $delete: row.recruitFileId })
      }
      this.fileList.splice(index, 1)
    },
    saveOrSubmitBill (type) { // 点击 提交审批或保存按钮
      if (type === 'SUBMIT') {
        this.publishRecruit(type)
      } else if (type === 'SAVE') {
        this.saveRecruit(type)
      }
    },
    saveRecruit (type) {
      const params = {
        ...this.detailForm,
        contentList: [{
          recruitContentId: this.recruitContentId,
          content: this.content
        }],
        fileList: [ ...this.fileList, ...this.fileListDel ]

      }
      const saveData = transformMQL.save('Recruit', [params], 'saveOrUpdate')
      this.$http({
        url: '/api-sou/api-ql/Recruit/saveOrUpdate',
        method: 'POST',
        data: saveData,
        loading: true
      }).then(async res => {
        this.detailForm = res.data[0]
        this.getFormDetail(this.detailForm.recruitId)
        if (type === 'SAVE') {
          this.$message.success(this.$t('common.success'))
          this.__setTabTodo('recruitmentList.getQueryData')
        } else {
          await this.handlerAfter(type, 'Y', () => {
            this.__setTabTodo('recruitmentList.getQueryData')
          })
        }
      })
    },
    publishRecruit (type) {
      this.$refs.detailForm.validate(valid => {
        if (valid) {
          this.saveRecruit(type)
        } else {
          return this.$message.warning(this.$t('common.pleasefinishRequired'))
        }
      })
    }
  }
}
</script>
<style lang="scss" scoped>
</style>
