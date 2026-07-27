<template>
  <el-container class="flex-container wrapper" direction="vertical">
    <el-main>
      <el-collapse v-model="activeDims">
        <!--基础信息-->
        <el-collapse-item :title="$t('meeting.baseInfo')" name="1">
          <el-form
            ref="form"
            :rules="rules"
            :model="form"
            :disabled="disabledFlag"
          >
            <SrmRow>
              <!--议题编号-->
              <SrmCol :init-col="4">
                <el-form-item prop="topicCode" :label="$t('meeting.topicCode')">
                  <el-input v-model="form.topicCode" disabled />
                </el-form-item>
              </SrmCol>

              <!--议题名称-->
              <SrmCol :init-col="4">
                <el-form-item prop="topicName" :label="$t('meeting.topicName')">
                  <el-input v-model="form.topicName" :placeholder="$t('common.pleaseInput')" />
                </el-form-item>
              </SrmCol>

              <!--上会类型-->
              <SrmCol :init-col="4">
                <el-form-item prop="topicType" :label="$t('meeting.topicType')">
                  <DictSelect
                    v-model="form.topicType"
                    code="MEET_TYPE"
                    :disabled="urlParams.flag === 'add' || topicTypeDisabled || fromInquiry || form.relationBillCode"
                    @change="topicTypeChange"
                  />
                </el-form-item>
              </SrmCol>

              <!--议题模板名称-->
              <SrmCol :init-col="4">
                <el-form-item prop="modelName" :label="$t('meeting.topicModelName')">
                  <QuickSearch
                    :show-input="form.modelName"
                    :disabled="disabledFlag"
                    show-key="modelName"
                    :scope-data="form"
                    auto-query
                    name="lts_scc_meet_model"
                    :pre-query-data="{'t.TOPIC_TYPE':form.topicType}"
                    @close-quicksearch="getMeetObj"
                  />
                </el-form-item>
              </SrmCol>

              <!--模块-->
              <SrmCol :init-col="4">
                <el-form-item :label="$t('meeting.categoryId')" prop="categoryId">
                  <el-select
                    v-model="form.categoryId"
                    :disabled="disabledFlag"
                    @change="categorySelect"
                  >
                    <el-option v-for="(item,index) in categoryList" :key="item.value + index" :value="item.value" :label="item.label" />
                  </el-select>
                </el-form-item>
              </SrmCol>

              <!--库存组织-->
              <SrmCol :init-col="4">
                <el-form-item :label="$t('common.invOrg')" prop="invId">
                  <OrganizationSelector
                    ref="orgSelector"
                    v-model="form.invId"
                    :placeholder="$t('common.pleaseSelect')"
                    :parent-id="-1"
                    node-type="INV"
                    @select="invSelectHandler"
                  />
                </el-form-item>
              </SrmCol>

              <!--关联单据号-->
              <SrmCol :init-col="4">
                <el-form-item prop="relationBillCode" :label="$t('meeting.relationBillCode')">
                  <el-button
                    :class="{'btn':true,'gray': form.topicType === 'OTHER' }"
                    @click="goToRelation"
                  >
                    {{ form.relationBillCode }}
                  </el-button>
                </el-form-item>
              </SrmCol>

              <!--议题状态-->
              <SrmCol :init-col="4">
                <el-form-item prop="status" :label="$t('meeting.topicStatus')">
                  <DictSelect
                    v-model="form.status"
                    code="MEET_SUBJECT_STATUS"
                    disabled
                  />
                </el-form-item>
              </SrmCol>

              <!--创建人-->
              <SrmCol :init-col="4">
                <el-form-item :label="$t('common.creator')">
                  <el-input v-model="form.createdFullName" disabled />
                </el-form-item>
              </SrmCol>

              <!--创建时间-->
              <SrmCol :init-col="4">
                <el-form-item :label="$t('common.creationTime')">
                  <el-date-picker
                    v-model="form.creationDate"
                    :format="$formatDatePickerTime"
                    value-format="yyyy-MM-dd HH:mm:ss"
                    disabled
                  />
                </el-form-item>
              </SrmCol>

              <!--更新人-->
              <SrmCol :init-col="4">
                <el-form-item :label="$t('common.lastUpdatedFullName')">
                  <el-input v-model="form.lastUpdatedFullName" disabled />
                </el-form-item>
              </SrmCol>

              <!--更新日期-->
              <SrmCol :init-col="4">
                <el-form-item :label="$t('common.lastUpdateDate')">
                  <el-date-picker
                    v-model="form.lastUpdateDate"
                    :format="$formatDatePickerTime"
                    value-format="yyyy-MM-dd HH:mm:ss"
                    disabled
                  />
                </el-form-item>
              </SrmCol>

              <!--待决议内容-->
              <SrmCol :init-col="1">
                <el-form-item prop="awaitResolution" :label="$t('meeting.awaitResolution')">
                  <el-input v-model="form.awaitResolution" type="textarea" :rows="2" />
                </el-form-item>
              </SrmCol>

              <!--会议纪要-->
              <SrmCol :init-col="1">
                <el-form-item prop="meetingMinutes" :label="$t('meeting.meetingMinutes')">
                  <el-input v-model="form.meetingMinutes" type="textarea" :rows="2" disabled />
                </el-form-item>
              </SrmCol>

              <!--议题结论-->
              <SrmCol :init-col="4">
                <el-form-item prop="topicConclusion" :label="$t('meeting.topicConclusion')">
                  <DictSelect
                    v-model="form.topicConclusion"
                    code="MEET_RESOLUTION"
                    disabled
                  />
                </el-form-item>
              </SrmCol>

              <!--升级等级-->
              <SrmCol v-if="form.topicConclusion === 'UPGRADE'" :init-col="4">
                <el-form-item prop="upgrade" :label="$t('meeting.upgrade')">
                  <DictSelect
                    v-model="form.upgrade"
                    code="FIXED_POINT_MEETING_TYPE"
                    disabled
                  />
                </el-form-item>
              </SrmCol>
            </SrmRow>
          </el-form>
        </el-collapse-item>

        <!--议题成员-->
        <el-collapse-item
          ref="memberTable"
          :title="$t('meeting.topicMember')"
          name="2"
        >
          <TopicMember
            ref="topicMember"
            :readonly="disabledFlag"
            :detail-data="memberData"
          />
        </el-collapse-item>

        <!--上会材料-->
        <el-collapse-item
          ref="materialFile"
          :title="$t('meeting.materialFile')"
          name="3"
        >
          <MaterialFile
            ref="materialFile"
            :readonly="disabledFlag"
            :detail-data="materialFileData"
          />
        </el-collapse-item>

        <!--材料模板-->
        <el-collapse-item :title="$t('meeting.materialTemplateTitle')" name="4">
          <MaterialTemplate :detail-data="fileData" />
        </el-collapse-item>
      </el-collapse>
    </el-main>

    <CToolbar>
      <template slot="right">
        <el-button type="ghost" @click="back">
          {{ $t('common.cancel') }}
        </el-button>
        <!--暂存-->
        <el-button
          v-if="!disabledFlag"
          type="primary"
          @click="saveBill('SAVE')"
        >
          {{ $t('common.staging') }}
        </el-button>

        <!--申请上会-->
        <el-button
          v-if="!disabledFlag"
          type="primary"
          @click="saveBill('SUBMIT')"
        >
          {{ $t('meeting.applyMeeting') }}
        </el-button>
      </template>
    </CToolbar>

    <!--常用组-->
    <el-dialog
      :title="$t('meeting.commonGroup')"
      width="450px"
      :visible.sync="saveVisible"
    >
      <el-form
        ref="dialogForm"
        :model="commonPersonForm"
        label="120"
      >
        <el-form-item
          prop="groupName"
          :label="$t('common.pleaseInput')"
          :rules="[{ required:true, message:$t('common.pleaseInput') }]"
        >
          <el-input v-model="commonPersonForm.groupName" />
        </el-form-item>
      </el-form>

      <span slot="footer" class="dialog-footer">
        <el-button @click="saveVisible = false">{{ $t('common.cancel') }}</el-button>
        <el-button type="primary" @click="saveCommonConfirm">{{ $t('common.confirm') }}</el-button>
      </span>
    </el-dialog>
  </el-container>
</template>

<script>
import { tabTodoMixin } from '@/utils/mixins'
import CToolbar from 'lib@/components/c-toolbar'
import QuickSearch from 'lib@/components/QuickSearch'
import OrganizationSelector from 'lib@/components/organization-selector'
import inquiryDetail from 'modb@/inquiry/views/inquiryManagement/inquiryDetail.vue'
import TopicMember from './meetTopicsDetail/topicMember'
import MaterialFile from './meetTopicsDetail/materialFile'
import MaterialTemplate from './meetTopicsDetail/materialTemplate'
// import quoteSelectionPage from 'modb@/inquiryBySimpleBuyer/views/inquiryBySimpleListBuyer/inquiryBySimpleListBuyer/inquiryTrackingDetail/inquiryEvaluationTab/quoteSelectionPage'
// import supaccountperiodchangeEdit from 'modb@/vendorManagementBuyer/views/supAccountPeriodChange/edit'

export default {
  name: 'MeetTopicsDetail',

  components: {
    CToolbar,
    QuickSearch,
    OrganizationSelector,
    TopicMember,
    MaterialFile,
    MaterialTemplate
  },

  mixins: [tabTodoMixin],

  data () {
    return {
      activeDims: ['1', '2', '3', '4'],
      form: {
        topicId: null,
        topicCode: null,
        topicName: null,
        topicType: null,
        modelCode: null,
        modelId: null,
        modelName: null,
        invId: null,
        invCode: null,
        invName: null,
        relationBillCode: null,
        relationBillId: null,
        status: null,
        createdFullName: null,
        creationDate: null,
        lastUpdatedFullName: null,
        lastUpdateDate: null,
        awaitResolution: null,
        meetingMinutes: null,
        topicConclusion: null,
        upgrade: null,
        categoryId: null,
        categoryFullName: null,
        categoryCode: null
      },
      rules: {
        topicName: [{ required: true, message: this.$t('common.pleaseInput') }],
        awaitResolution: [{ required: true, message: this.$t('common.pleaseInput') }],
        categoryId: [{ required: true, message: this.$t('common.pleaseSelect') }]
      },
      fileData: [], // 材料模板
      memberData: [], // 议题成员
      materialFileData: [], // 上会材料
      saveVisible: false,
      commonPersonForm: {
        groupName: ''
      },
      topicTypeDisabled: false,
      categoryList: [], // 模块（一级品类）
      modelId: null
    }
  },

  computed: {
    urlParams () {
      return this.$attrs.params || {}
    },
    disabledFlag () {
      return this.urlParams.flag === 'view'
    },
    // 从询价或者其它地方跳转过来
    fromInquiry () {
      return this.urlParams.from === 'fromInquiry'
    }
  },

  created () {
    let { row, flag } = this.urlParams
    if (row && row.topicId) {
      this.getFormDetail(row.topicId)
    }
    if (flag === 'add') {
      this.form.topicType = 'OTHER'
    }
    this.getCategoryList()
  },

  methods: {
    saveBill (type) {
      let params = this.initParams()
      let url = type === 'SAVE' ? '/api-inq/inq/meettopic/add' : '/api-inq/inq/meettopic/apply'
      if (type === 'SUBMIT') {
        let flag
        // this.$refs.form.validate(valid => flag = valid)
        if (!flag) {
          this.__focus_error__()
          return
        }
        if (!this.memberData.length) {
          this.jumpError(this.$t('meeting.rule.memberDataNotEmpty'))
          return
        }
        for (let item of this.memberData) {
          if (!item.fullName) {
            this.jumpError(this.$t('meeting.rule.fullNameNotEmpty'))
            return
          }
          if (!item.topicRole) {
            this.jumpError(this.$t('meeting.rule.roleNotEmpty'))
            return
          }
        }
        let hasChooseIncharge = this.memberData.filter(item => item.inCharge)
        if (!hasChooseIncharge.length) {
          this.jumpError(this.$t('meeting.rule.selectIncharge'))
          return
        }
        if (hasChooseIncharge.length > 1) {
          this.jumpError(this.$t('meeting.rule.selectOneIncharge'))
          return
        }
      }
      this.$http({
        url,
        method: 'POST',
        data: params,
        loading: true
      }).then(res => {
        this.$message.success(res.message)
        if (type === 'SAVE') {
          if (!res.data) return
          this.getFormDetail(res.data)
        } else {
          this.back()
        }
      })
    },

    async goToRelation () {
      let { topicType, relationBillCode, relationBillId, status } = this.form
      if (!relationBillId) return
      let tab
      let ismeeting = status === 'MEETING' ? 'Y' : 'N'
      if (topicType === 'SS') {
        tab = {
          component: inquiryDetail,
          params: {
            flag: 'view',
            readOnly: true,
            row: { projectId: relationBillId, ismeeting },
            tabName: `inquiryDetail${relationBillCode}`
          },
          title: relationBillCode,
          name: `inquiryDetail${relationBillCode}`
        }
      } else if (topicType === 'SD') {
        // let result = await this.$http({
        //   url: `/api-inq/quote/selection/management/${relationBillId}`,
        //   method: 'GET'
        // })
        // let { header = {} } = result.data
        // tab = {
        //   component: quoteSelectionPage,
        //   params: {
        //     header,
        //     tabName: `quoteSelectionPage${header.inquiryId}`
        //   },
        //   title: header.inquiryId,
        //   name: `quoteSelectionPage${header.inquiryId}`
        // }
      } else if (topicType === 'PC') {
        // let result = await this.$http({
        //   url: `/api-sup/sup/supaccountperiodchange/get?id=${relationBillId}`,
        //   method: 'GET'
        // })
        // let header = result.data
        // tab = {
        //   component: supaccountperiodchangeEdit,
        //   params: {
        //     row: header,
        //     flag: 'view',
        //     tabName: `supaccountperiodchangeEdit${header.accountPeriodChangeId}`
        //   },
        //   title: '供应商账期变更详情',
        //   name: `supaccountperiodchangeEdit${header.accountPeriodChangeId}`
        // }
      }
      this.$emit('tab-add', tab)
    },

    categorySelect (val) {
      let obj = this.categoryList.find(item => item.value === val)
      if (obj) {
        this.form.categoryFullName = obj.label
        this.form.categoryCode = obj.categoryCode
      }
    },

    getCategoryList () {
      this.$http({
        url: '/api-base/purchase/purchaseCategory/listChildren',
        method: 'POST',
        params: {
          categoryId: -1
        }
      }).then(res => {
        this.categoryList = res.data.map(item => {
          return {
            value: item.categoryId,
            label: item.categoryFullName,
            categoryCode: item.categoryCode
          }
        })
      })
    },

    invSelectHandler (node, value, scope) {
      this.form.invId = node ? node.organizationId : null
      this.form.invCode = node ? node.organizationCode : null
      this.form.invName = node ? node.organizationName : null
    },

    saveCommonConfirm () {
      this.$refs.dialogForm.validate(valid => {
        if (valid) {
          let filterMemberList = this.memberData.filter(item => item.fullNameId)
          this.$http({
            url: '/api-inq/inq/meetGroup/submit',
            method: 'POST',
            data: {
              groupType: 'topic', // 议题：topic,议会:meeting
              groupName: this.commonPersonForm.groupName,
              groupMemberList: filterMemberList
            },
            loading: true
          }).then((res) => {
            this.$message.success(res.message)
            this.saveVisible = false
          })
        }
      })
    },

    topicTypeChange () {
      this.form.modelName = ''
    },

    async getMeetObj (val, scope) {
      this.modelId = this.form.modelId
      let attrs = ['modelId', 'modelCode', 'modelName', 'topicType']
      for (let key of attrs) {
        scope[key] = val ? val[key] : null
      }
      if (!val || !val.modelId) return
      this.getModelFormDetail(val.modelId)
    },

    getModelForm (modelId) {
      return new Promise(resolve => {
        this.$http({
          url: '/api-inq/inq/meetmodel/get',
          method: 'GET',
          params: { modelId },
          loading: true
        }).then(res => {
          resolve(res)
        })
      })
    },

    getModelFormDetail (modelId) {
      this.getModelForm(modelId).then(res => {
        let { meetModelMemberList, meetModelMaterialList } = res.data || {}
        this.memberData = this.memberData.filter(item => item.modelId !== this.modelId)
        let hasIds = []
        for (let item of this.memberData) {
          item.fullNameId && hasIds.push(item.fullNameId)
        }
        for (let item of meetModelMemberList) {
          if (!hasIds.includes(item.fullNameId)) {
            this.memberData.push(item)
          }
        }
        this.memberData.forEach(item => {
          item.enableMeetingInvitee = true
          item.enableMinutesSender = true
        })
        this.fileData = meetModelMaterialList
      })
    },

    getFormDetail (topicId) {
      this.$http({
        url: '/api-inq/inq/meettopic/get',
        method: 'GET',
        params: { topicId },
        loading: true
      }).then(res => {
        let { meetTopicMemberList, meetTopicMaterialList, ...rest } = res.data || {}
        Object.assign(this.form, rest)
        this.memberData = meetTopicMemberList
        this.materialFileData = meetTopicMaterialList
        if (rest.topicType === 'OTHER') this.topicTypeDisabled = true
        if (this.fromInquiry) {
          // this.memberData.forEach(item => item.fromRfq = true)
        }
        if (!rest.modelId) return
        this.getModelForm(rest.modelId).then(res => {
          let { meetModelMaterialList = [] } = res.data || {}
          this.fileData = meetModelMaterialList
        })
      })
    },

    jumpError (message) {
      this.__jump_error__('memberTable', 'component', message)
    },

    back () {
      let { tabName } = this.$attrs.params
      this.$emit('tab-remove', tabName)
      this.__setTabTodo('MeetSubjectList.getQueryData')
    },

    initParams () {
      let params = {}
      for (let key in this.form) {
        params[key] = this.form[key]
      }
      params.meetTopicMemberList = this.memberData
      params.meetTopicMaterialList = this.materialFileData
      return params
    }
  }
}
</script>

<style lang="scss" scoped>
.wrapper {
  padding-bottom: 40px;
}
.toRequired {
  color: red;
}
.btn {
  display: block;
  width: 100%;
  text-align: left;
  padding-left: 8px;
  height: 30px;
  &.gray{
    background-color: #F5F7FA;
    cursor: not-allowed;
  }
}
</style>
