<template>
  <el-container class="flex-container" style="padding-bottom:40px;" direction="vertical">
    <el-main>
      <el-form ref="form" :model="form" :rules="formRules">
        <el-collapse v-model="colValue">
          <el-collapse-item title="基础信息" name="1">
            <BaseInfo
              ref="baseInfo"
              :form.sync="form"
              :readonly="true"
            />
          </el-collapse-item>
          <el-collapse-item title="问题清单附件" name="2">
            <!-- 仅需求部门可新增 -->
            <FileDynamic
              ref="sceneAttachmentProblem"
              v-model="form.problemListFiles"
              scene-module-code="PRE_BID_PROBLEM_LIST_ATTACHMENT"
              :business-id="bidFeedbackId"
              :editable="false"
              :needInit="false"
            />
          </el-collapse-item>
          <el-collapse-item
            title="问题清单反馈附件"
            name="3"
          >
            <FileInfo
              ref="fileInfo"
              :value="form.problemFeedbackListFiles"
              :readonly="disabledFlag"
            />
          </el-collapse-item>
        </el-collapse>
      </el-form>
    </el-main>
    <CToolbar>
      <template slot="right">
        <el-button type="ghost" @click="back">
          {{ $t('bidMod.cancel') }}
        </el-button>
        <template v-if="!disabledFlag">
          <el-button type="primary" @click="saveBill('SAVE')">
            {{ $t('bidMod.temporaryStorage') }}
          </el-button>
          <el-button type="primary" @click="saveBill('SUBMIT')">
            {{ $t('problemManagement.submit') }}
          </el-button>
        </template>
      </template>
    </CToolbar>
  </el-container>
</template>
<script>
import { tabTodoMixin } from '@/utils/mixins'
import BaseInfo from './components/baseInfo'
import CToolbar from 'lib@/components/c-toolbar'
import { commuFeedbackHttp } from 'modcs@/preBidCommunicateSupplier/api'
import { transformMQL } from 'lib@/utils/util'
import FileDynamic from '@/library/components/c-file-management/file-dynamic'
import FileInfo from './components/fileInfo'

export default {
  name: 'CommunicateFeedbackDetail',
  components: {
    BaseInfo,
    CToolbar,
    FileDynamic,
    FileInfo
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      bidFeedbackId: null,
      colValue: ['1', '2', '3'],
      form: {
        bidFeedbackId: null,
        bidNoticeId: null,
        bidNoticeNo: null,
        bidNoticeTitle: null,
        requirementHeadNo: null,
        requirementHeadId: null,
        projectName: null,
        orgBuName: null,
        orgName: null,
        demandDepartmentName: null,
        status: null,
        feedbackStatus: 'NO_FEEDBACK', // 未反馈
        createdFullName: null,
        demandUserNickname: null,
        vendorUserNickname: null,
        bidUserNickname: null,
        creationDate: null,
        lastUpdateDate: null,
        remark: null,
        vendorId: null,
        feedbackVendorList: [],
        problemListFiles: [],
        reportListFiles: [],
        problemFeedbackListFiles: []
      },
      formRules: {
        bidNoticeTitle: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }],
        requirementHeadNo: [{ required: true, message: this.$t('common.requiredField'), trigger: 'change' }],
        projectName: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }]
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
    statusFlag () {
      return {
        DRAFT: this.form.status === 'DRAFT', // 拟定
        ISSUED: this.form.status === 'ISSUED', // 待提交报告
        FINISHED: this.form.status === 'FINISHED', // 已完成
        ABANDONED: this.form.status === 'ABANDONED' // 已废弃
      }
    }
  },
  mounted () {
    const { bidFeedbackId } = this.urlParams.row
    this.bidFeedbackId = bidFeedbackId
    if (bidFeedbackId) {
      this.getFormDetail()
    }
  },
  methods: {
    async getFormDetail () {
      let transformParams = transformMQL.save('PreBidFeedback', [this.bidFeedbackId], 'read',
        {
          '*': {},
          'problemListFiles': {
            '*': {}
          },
          'problemFeedbackListFiles': {
            '*': {},
            '$condition': {
              '$strictQuery': true,
              'filter': {
                vendorId: this.$store.getters.userInfo.companyId
              }
            }
          },
          'feedbackVendorList': {
            '*': {},
            '$condition': {
              '$strictQuery': true,
              'filter': {
                vendorId: this.$store.getters.userInfo.companyId
              }
            }
          }
        }
      )
      const response = await commuFeedbackHttp.read(transformParams)
      if (response.data.length) {
        this.form = response.data[0]
        const { feedbackVendorList } = this.form
        if (feedbackVendorList && feedbackVendorList.length) {
          this.form.vendorId = feedbackVendorList[0].vendorId
          this.form.vendorCode = feedbackVendorList[0].vendorCode
          this.form.vendorName = feedbackVendorList[0].vendorName
          this.form.feedbackStatus = feedbackVendorList[0].feedbackStatus
        }
        if (!this.form.vendorId) {
          this.form.vendorId = this.$store.getters.userInfo.companyId
        }
        this.loadFileInfo('sceneAttachmentProblem')
      }
    },
    loadFileInfo (fileRef = 'sceneAttachment') {
      this.$nextTick(() => {
        this.$refs[fileRef].loadFileInfo()
      })
    },
    initParams () { // 参数
      let params = JSON.parse(JSON.stringify(this.form))
      params.problemFeedbackListFiles.forEach((item, index) => {
        item.sortIndex = index
      })
      console.log('params', params)
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
      let params = this.initParams()
      let feedbackStatus = 'NO_FEEDBACK'
      if (type === 'SUBMIT') {
        const validForm = await this.validBill()
        if (!validForm) {
          this.__focus_error__()
          return
        }
        // 校验-问题清单反馈附件
        const { problemFeedbackListFiles = [] } = params
        if (!problemFeedbackListFiles.length) {
          this.$message.warning('请新增问题清单反馈附件')
          return
        }
        for (let item of problemFeedbackListFiles) {
          if (!item.fileId) {
            this.$message.warning('问题清单反馈附件-附件不能为空')
            return
          }
        }
        feedbackStatus = 'ALREADY_FEEDBACK'
      }
      const { feedbackVendorList = [], problemFeedbackListFiles = [] } = params
      if (feedbackVendorList.length) {
        params.feedbackVendorList.forEach(item => { item.feedbackStatus = feedbackStatus })
      }
      if (problemFeedbackListFiles.length) {
        params.problemFeedbackListFiles.forEach(item => {
          item.vendorId = params.vendorId
          item.vendorCode = params.vendorCode
          item.vendorName = params.vendorName
        })
      }

      let transformParams = transformMQL.save('PreBidFeedback', [params], 'save')
      const response = await commuFeedbackHttp.save(transformParams)
      if (response.data && response.data.length) {
        let result = response.data[0] || {}
        this.bidFeedbackId = this.form.bidFeedbackId
        this.$message.success(this.$t('common.successSave'))
        if (type === 'SAVE') {
          await this.getFormDetail()
        }
        if (type === 'SUBMIT') {
          this.back()
        }
      }
    },
    back () {
      let { tabName } = this.$attrs.params
      this.$emit('tab-remove', tabName)
      this.__setTabTodo('CommunicateFeedbackList.getQueryData')
    }
  }
}
</script>
