<template>
  <el-container class="flex-container" style="padding-bottom:40px;" direction="vertical">
    <el-main>
      <el-form ref="form" :model="form" :rules="formRules">
        <el-collapse v-model="colValue">
          <!-- <el-collapse-item title="基础信息" name="1"> -->
          <el-collapse-item :title="$t('common.baseInfo')" name="1">
            <BaseInfo
              ref="baseInfo"
              :form.sync="form"
              :readonly="disabledFlag || !statusFlag.DRAFT"
              @getNotice="getNotice"
            />
          </el-collapse-item>
          <!-- <el-collapse-item title="技术交流供应商" name="2"> -->
          <el-collapse-item :title="$t('cusEntry.supplement20250205.technicalCommunicationSupplier')" name="2">
            <VendorInfo
              ref="vendorInfo"
              :value.sync="form.feedbackVendorList"
              :form="form"
              :readonly="disabledFlag"
            />
          </el-collapse-item>
          <!-- <el-collapse-item title="问题清单附件" name="3"> -->
          <el-collapse-item :title="$t('cusEntry.supplement20250205.issueListAttachment')" name="3">
            <!-- 仅需求部门可新增 -->
            <FileDynamic
              ref="sceneAttachmentProblem"
              v-model="form.problemListFiles"
              scene-module-code="PRE_BID_PROBLEM_LIST_ATTACHMENT"
              :business-id="bidFeedbackId"
              :editable="!disabledFlag && statusFlag.DRAFT"
              :needInit="false"
            />
          </el-collapse-item>
          <!-- <el-collapse-item
            v-if="!statusFlag.DRAFT"
            title="问题清单反馈附件"
            name="4"
          > -->
          <el-collapse-item
            v-if="!statusFlag.DRAFT"
            :title="$t('cusEntry.supplement20250205.issueListFeedbackAttachment')"
            name="4"
          >
            <el-tabs v-if="problemFeedbackArr.length" type="border-card">
              <el-tab-pane v-for="(item,index) in problemFeedbackArr" :key="index" :label="item.vendorName">
                <!-- 已反馈状态 创建人可以驳回 -->
                <FileInfo :value="item.list" :readonly="disabledFlag || !rejectFlag(item) || !isCreator" @reject-vendor="rejectVendor(item)" />
              </el-tab-pane>
            </el-tabs>
          </el-collapse-item>
          <!-- <el-collapse-item v-if="!statusFlag.DRAFT" title="报告单附件" name="5"> -->
          <el-collapse-item v-if="!statusFlag.DRAFT" :title="$t('cusEntry.supplement20250205.reportAttachment')" name="5">
            <FileDynamic
              ref="sceneAttachmentSign"
              v-model="form.reportListFiles"
              scene-module-code="PRE_BID_REPORT_ATTACHMENT"
              :business-id="bidFeedbackId"
              :editable="!disabledFlag && isDemandUser"
              :needInit="false"
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
        <template v-if="!disabledFlag && statusFlag.DRAFT">
          <el-button type="primary" @click="saveBill('SAVE')">
            {{ $t('bidMod.temporaryStorage') }}
          </el-button>
          <el-button type="primary" @click="saveBill('SUBMIT')">
            {{ $t('problemManagement.submit') }}
          </el-button>
        </template>
        <template v-if="!disabledFlag && statusFlag.ISSUED">
          <el-button type="primary" @click="handlePublish('SAVE')">
            {{ $t('bidMod.temporaryStorage') }}
          </el-button>
          <!-- 创建人才可以提交 -->
          <el-button v-if="isCreator" type="primary" @click="handlePublish('SUBMIT')">
            {{ $t('problemManagement.submit') }}
          </el-button>
        </template>
      </template>
    </CToolbar>
    <!-- 驳回供应商弹窗 -->
    <RejectDialog
      ref="rejectDialog"
      :visible.sync="rejectDialogVisible"
      @confirm="rejectDialogConfirm"
    />
  </el-container>
</template>
<script>
import { tabTodoMixin } from '@/utils/mixins'
import BaseInfo from './components/baseInfo'
import VendorInfo from './components/vendorInfo'
import CToolbar from 'lib@/components/c-toolbar'
import { commuNoticeBuyerHttp, commuFeedbackHttp } from 'modcb@/preBidCommunicate/api'
import { transformMQL } from 'lib@/utils/util'
import FileDynamic from '@/library/components/c-file-management/file-dynamic'
import FileInfo from './components/fileInfo'
import RejectDialog from './components/rejectDialog'
import { mapGetters } from 'vuex'

export default {
  name: 'CommunicateNoticeBuyerDetail',
  components: {
    BaseInfo,
    CToolbar,
    FileDynamic,
    VendorInfo,
    FileInfo,
    RejectDialog
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      rejectDialogVisible: false,
      currentRow: null,
      bidFeedbackId: null,
      colValue: ['1', '2', '3', '4', '5'],
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
        status: 'DRAFT',
        createdFullName: null,
        demandUserNickname: null,
        vendorUserNickname: null,
        bidUserNickname: null,
        creationDate: null,
        lastUpdateDate: null,
        remark: null,
        feedbackVendorList: [],
        problemListFiles: [],
        reportListFiles: [],
        problemFeedbackListFiles: []
      },
      formRules: {
        bidNoticeTitle: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }],
        requirementHeadNo: [{ required: true, message: this.$t('common.requiredField'), trigger: 'change' }],
        projectName: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }],
        orgName: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }]
      }
    }
  },
  computed: {
    ...mapGetters(['userInfo']),
    // 是否需求人员
    isDemandUser () {
      return this.form.demandUserNickname === this.userInfo.nickname
    },
    // 是否供应商专家
    isVendorUser () {
      return this.form.vendorUserNickname === this.userInfo.nickname
    },
    // 是否创建人
    isCreator () {
      return this.form.createdFullName === this.userInfo.nickname
    },
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
    },
    problemFeedbackArr () {
      let { problemFeedbackListFiles = [] } = this.form
      let vendorIdArr = problemFeedbackListFiles.filter(item => item.vendorId).map(item => item.vendorId)
      let newArr = []
      if (vendorIdArr.length) {
        vendorIdArr = Array.from(new Set(vendorIdArr))
        for (let key of vendorIdArr) {
          let obj = problemFeedbackListFiles.find(item => item.vendorId === key) || {}
          let list = problemFeedbackListFiles.filter(item => item.vendorId === key)
          let newObj = {
            vendorId: obj.vendorId,
            vendorCode: obj.vendorCode,
            vendorName: obj.vendorName,
            list
          }
          newArr.push(newObj)
        }
      }
      console.log('newArr', newArr)
      return newArr
    },
    // 是否有驳回按钮 - 供应商反馈状态已反馈才会有
    rejectFlag (row) {
      return (row) => {
        let obj = this.form.feedbackVendorList.find(item => item.vendorId === row.vendorId)
        if (obj) {
          return obj.feedbackStatus === 'ALREADY_FEEDBACK'
        }
        return false
      }
    }
  },
  mounted () {
    const { bidFeedbackId } = this.urlParams.row
    this.bidFeedbackId = bidFeedbackId
    if (bidFeedbackId) {
      this.getFormDetail()
    } else {
      this.loadFileInfo('sceneAttachmentProblem')
    }
  },
  methods: {
    async getFormDetail () {
      let transformParams = transformMQL.save('PreBidFeedbackBuyer', [this.bidFeedbackId], 'read',
        {
          '*': {},
          'feedbackVendorList': {
            '*': {}
          },
          'problemListFiles': {
            '*': {}
          },
          'problemFeedbackListFiles': {
            '*': {}
          },
          'reportListFiles': {
            '*': {}
          }
        }
      )
      const response = await commuFeedbackHttp.read(transformParams)
      if (response.data.length) {
        this.form = response.data[0]
        this.loadFileInfo('sceneAttachmentProblem')
        this.loadFileInfo('sceneAttachmentSign')
      }
    },
    // 交流通知单号查询
    async getNotice (bidNoticeId) {
      let transformParams = transformMQL.save('PreBidNoticeBuyer', [bidNoticeId], 'read', {
        '*': {},
        'noticeVendorList': {
          '*': {}
        }
      })
      const response = await commuNoticeBuyerHttp.read(transformParams)
      if (response.data.length) {
        const { noticeVendorList = [] } = response.data[0]
        this.form.feedbackVendorList = noticeVendorList
      }
    },
    loadFileInfo (fileRef = 'sceneAttachment') {
      this.$nextTick(() => {
        this.$refs[fileRef] && this.$refs[fileRef].loadFileInfo()
      })
    },
    rejectVendor (row) {
      console.log('row', row)
      this.currentRow = row // 记录当前驳回行
      this.rejectDialogVisible = true
      this.$nextTick(() => {
        this.$refs.rejectDialog.resetFields()
      })
    },
    async rejectDialogConfirm (form) {
      let params = {
        bidFeedbackId: this.bidFeedbackId,
        vendorId: this.currentRow.vendorId,
        ...form
      }
      let transformParams = transformMQL.save('PreBidFeedbackBuyer', [params], 'reject')
      const response = await commuFeedbackHttp.reject(transformParams)
      if (response) {
        this.rejectDialogVisible = false
        this.$message.success(this.$t('common.success'))
        this.getFormDetail()
      }
    },
    initParams () { // 参数
      let params = JSON.parse(JSON.stringify(this.form))
      const { feedbackVendorList = [] } = params
      feedbackVendorList.forEach(item => {
        delete item.feedbackStatus
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
      if (type === 'SUBMIT') {
        const validForm = await this.validBill()
        if (!validForm) {
          this.__focus_error__()
          return
        }
        const { problemListFiles = [] } = params
        if (!problemListFiles.length) {
          // return this.$message.warning('请新增问题清单附件')
          return this.$message.warning(this.$t('cusEntry.supplement20250205.addIssueListAttachment'))
        }
      }
      let handleType = type.toLowerCase()
      let transformParams = transformMQL.save('PreBidFeedbackBuyer', [params], handleType)
      const response = await commuFeedbackHttp[handleType](transformParams)
      if (response.data && response.data.length) {
        this.form = response.data[0]
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
    async handlePublish (type) {
      let params = this.initParams()
      if (type === 'SUBMIT') {
        const { feedbackVendorList = [] } = this.form
        for (let item of feedbackVendorList) {
          if (item.feedbackStatus !== 'ALREADY_FEEDBACK') {
            // return this.$message.warning(`${item.vendorName}没有反馈，请反馈后再提交`)
            return this.$message.warning(`${item.vendorName}${this.$t('cusEntry.supplement20250205.noFeedbackPleaseProvideFeedbackBeforeSubmitting')}`)
          }
          if (!item.isSelected) {
            // return this.$message.warning('技术交流供应商-是否入围不能为空')
            return this.$message.warning(this.$t('cusEntry.supplement20250205.techCommSupplierSelectionRequired'))
          }
        }
        params.status = 'FINISHED'
      }
      let transformParams = transformMQL.save('PreBidFeedbackBuyer', [params], 'save')
      const response = await commuFeedbackHttp.save(transformParams)
      if (response.data && response.data.length) {
        this.form = response.data[0]
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
      this.__setTabTodo('CommunicateFeedbackBuyerList.getQueryData')
    }
  }
}
</script>
