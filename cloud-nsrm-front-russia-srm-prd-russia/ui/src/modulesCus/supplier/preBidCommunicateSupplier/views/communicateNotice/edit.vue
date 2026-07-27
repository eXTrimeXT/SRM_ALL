<template>
  <el-container class="flex-container" style="padding-bottom:40px;" direction="vertical">
    <el-main>
      <el-form ref="form" :model="form" :rules="formRules">
        <el-collapse v-model="colValue">
          <el-collapse-item title="基础信息" name="1">
            <BaseInfo
              ref="baseInfo"
              :form.sync="form"
              :readonly="disabledFlag"
            />
          </el-collapse-item>
          <el-collapse-item title="交流大纲编辑附件" name="3">
            <!-- 仅需求部门可新增 -->
            <FileDynamic
              ref="sceneAttachmentEdit"
              v-model="form.commEditFiles"
              scene-module-code="PRE_BID_COMM_EDIT_ATTACHMENT"
              :business-id="bidNoticeId"
              :editable="!disabledFlag"
              :needInit="false"
            />
          </el-collapse-item>
        </el-collapse>
      </el-form>
    </el-main>
    <CToolbar>
      <template slot="right">
        <el-button type="ghost" @click="back">
          {{ $t('common.close') }}
        </el-button>
      </template>
    </CToolbar>
  </el-container>
</template>
<script>
import { tabTodoMixin } from '@/utils/mixins'
import BaseInfo from './components/baseInfo'
import CToolbar from 'lib@/components/c-toolbar'
import { commuNoticeHttp } from 'modcs@/preBidCommunicateSupplier/api'
import { transformMQL } from 'lib@/utils/util'
import FileDynamic from '@/library/components/c-file-management/file-dynamic'

export default {
  name: 'CommunicateNoticeDetail',
  components: {
    BaseInfo,
    CToolbar,
    FileDynamic
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      bidNoticeId: null,
      colValue: ['1', '2', '3', '4'],
      form: {
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
        createdFullName: null,
        demandUserNickname: null,
        vendorUserNickname: null,
        bidUserNickname: null,
        creationDate: null,
        lastUpdateDate: null,
        remark: null,
        noticeVendorList: [],
        commSignFiles: [],
        commEditFiles: []
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
    urlParams () {
      return this.$attrs.params || {}
    },
    disabledFlag () {
      return !!['view', 'approval', 'manage'].includes(this.urlParams.flag)
    }
  },
  mounted () {
    const { bidNoticeId } = this.urlParams.row
    this.bidNoticeId = bidNoticeId
    if (bidNoticeId) {
      this.getFormDetail()
    }
  },
  methods: {
    async getFormDetail () {
      let transformParams = transformMQL.save('PreBidNotice', [this.bidNoticeId], 'read',
        {
          '*': {},
          'commEditFiles': {
            '*': {}
          }
        }
      )
      const response = await commuNoticeHttp.read(transformParams)
      if (response.data.length) {
        this.form = response.data[0]
        this.loadFileInfo('sceneAttachmentEdit')
      }
    },
    loadFileInfo (fileRef = 'sceneAttachment') {
      this.$nextTick(() => {
        this.$refs[fileRef].loadFileInfo()
      })
    },
    initParams () { // 参数
      let params = JSON.parse(JSON.stringify(this.form))
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
        }
      }
      const response = await commuNoticeHttp.save(params)
      if (response.data) {
        this.form = response.data
        this.bidNoticeId = this.form.bidNoticeId
        this.$message.success(this.$t('common.successSave'))
        await this.getFormDetail()
        if (type === 'SUBMIT') {
          this.back()
        }
      }
    },
    back () {
      let { tabName } = this.$attrs.params
      this.$emit('tab-remove', tabName)
      this.__setTabTodo('CommunicateNoticeList.getQueryData')
    }
  }
}
</script>
