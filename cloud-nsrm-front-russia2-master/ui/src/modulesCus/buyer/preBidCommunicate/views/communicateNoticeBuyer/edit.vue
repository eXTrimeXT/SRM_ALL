<template>
  <el-container class="flex-container" direction="vertical">
    <el-main>
      <el-form ref="form" :model="form" :rules="formRules">
        <el-collapse v-model="colValue">
          <!-- <el-collapse-item title="基础信息" name="1"> -->
          <el-collapse-item :title="$t('common.baseInfo')" name="1">
            <BaseInfo
              ref="baseInfo"
              :form.sync="form"
              :readonly="disabledFlag"
              :isCreator="isCreator"
            />
          </el-collapse-item>
          <!-- <el-collapse-item title="交流大纲签字版附件" name="2"> -->
          <el-collapse-item :title="$t('cusEntry.supplement20250205.communicationOutlineSignedAttachment')" name="2">
            <!-- 仅需求部门可新增 -->
            <FileDynamic
              ref="sceneAttachmentSign"
              v-model="form.commSignFiles"
              scene-module-code="PRE_BID_COMM_SIGN_ATTACHMENT"
              :business-id="bidNoticeId"
              :editable="!disabledFlag && isDemandUser"
              :needInit="false"
            />
          </el-collapse-item>
          <!-- <el-collapse-item title="交流大纲编辑附件" name="3"> -->
          <el-collapse-item :title="$t('cusEntry.supplement20250205.communicationOutlineEditingAttachment')" name="3">
            <!-- 仅需求部门可新增 -->
            <FileDynamic
              ref="sceneAttachmentEdit"
              v-model="form.commEditFiles"
              scene-module-code="PRE_BID_COMM_EDIT_ATTACHMENT"
              :business-id="bidNoticeId"
              :editable="!disabledFlag && isDemandUser"
              :needInit="false"
            />
          </el-collapse-item>
          <!-- <el-collapse-item title="技术交流供应商" name="4"> -->
          <el-collapse-item :title="$t('cusEntry.supplement20250205.technicalCommunicationSupplier')" name="4">
            <VendorInfo
              ref="vendorInfo"
              :value.sync="form.noticeVendorList"
              :form="form"
              :readonly="disabledFlag || !isVendorUser"
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
          <el-button v-if="isCreator" type="primary" @click="saveBill('SUBMIT')">
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
import VendorInfo from './components/vendorInfo'
import CToolbar from 'lib@/components/c-toolbar'
import { commuNoticeBuyerHttp } from 'modcb@/preBidCommunicate/api'
import { transformMQL } from 'lib@/utils/util'
import FileDynamic from '@/library/components/c-file-management/file-dynamic'
import { mapGetters } from 'vuex'

export default {
  name: 'CommunicateNoticeBuyerDetail',
  components: {
    BaseInfo,
    CToolbar,
    FileDynamic,
    VendorInfo
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
    ...mapGetters(['userInfo']),
    urlParams () {
      return this.$attrs.params || {}
    },
    disabledFlag () {
      return !!['view', 'approval', 'manage'].includes(this.urlParams.flag)
    },
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
      return this.form.createdBy === this.userInfo.username || !this.form.createdBy
    }
  },
  mounted () {
    const { bidNoticeId } = this.urlParams.row
    this.bidNoticeId = bidNoticeId
    if (bidNoticeId) {
      this.getFormDetail()
    } else {
      this.loadFileInfo('sceneAttachmentSign')
      this.loadFileInfo('sceneAttachmentEdit')
    }
  },
  methods: {
    async getFormDetail () {
      let transformParams = transformMQL.save('PreBidNoticeBuyer', [this.bidNoticeId], 'read',
        {
          '*': {},
          'noticeVendorList': {
            '*': {}
          },
          'commSignFiles': {
            '*': {}
          },
          'commEditFiles': {
            '*': {}
          }
        }
      )
      const response = await commuNoticeBuyerHttp.read(transformParams)
      if (response.data.length) {
        this.form = response.data[0]
        const { noticeVendorList = [] } = this.form
        if (noticeVendorList.length) {
          noticeVendorList.forEach(item => {
            item.vendorAttribute && (item.vendorAttribute = item.vendorAttribute.split(';'))
          })
        }
        this.loadFileInfo('sceneAttachmentSign')
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
      const { noticeVendorList } = params
      noticeVendorList.forEach(item => {
        item.vendorAttribute = item.vendorAttribute.join(';')
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
        // 校验技术交流供应商
        const { noticeVendorList = [] } = params
        if (!noticeVendorList.length) {
          // this.$message.warning('请新增技术交流供应商')
          this.$message.warning(this.$t('cusEntry.supplement20250205.addTechnicalCommunicationSupplier'))
          return
        }
        for (let i = 0; i < noticeVendorList.length; i++) {
          if (!noticeVendorList[i].vendorCode) {
            // this.$message.warning('技术交流供应商-供应商编码不能为空')
            this.$message.warning(this.$t('cusEntry.supplement20250205.technicalCommunicationSupplierCodeRequired'))
            return
          }
          if (!noticeVendorList[i].vendorAttribute) {
            // this.$message.warning('技术交流供应商-供应商属性不能为空')
            this.$message.warning(this.$t('cusEntry.supplement20250205.technicalCommunicationSupplierAttributeRequired'))
            return
          }
          for (let j = i + 1; j < noticeVendorList.length; j++) {
            if (noticeVendorList[i].vendorCode === noticeVendorList[j].vendorCode) {
              // this.$message.warning(`技术交流供应商-供应商编码${noticeVendorList[i].vendorCode}重复`)
              this.$message.warning(`${this.$t('cusEntry.supplement20250205.technicalCommunicationSupplierCode')}${noticeVendorList[i].vendorCode}${this.$t('cusEntry.supplement20250205.duplicate')}`)
              return
            }
          }
        }
      }
      let handleType = type.toLowerCase()
      let transformParams = transformMQL.save('PreBidNoticeBuyer', [params], handleType)
      const response = await commuNoticeBuyerHttp[handleType](transformParams)
      if (response.data && response.data.length) {
        this.form = response.data[0]
        this.bidNoticeId = this.form.bidNoticeId
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
      this.__setTabTodo('CommunicateNoticeBuyerList.getQueryData')
    }
  }
}
</script>
