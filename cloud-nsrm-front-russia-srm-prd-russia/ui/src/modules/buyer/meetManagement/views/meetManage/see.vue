<template>
  <el-container class="flex-container wrapper" direction="vertical">
    <el-main>
      <el-collapse v-model="activeDims">
        <el-collapse-item :title="$t('meeting.baseInfo')" name="1">
          <el-form
            ref="form"
            :rules="rules"
            :model="form"
            disabled
          >
            <srm-row>
              <srm-col :init-col="4">
                <el-form-item prop="meetingCode" :label="$t('meeting.meetingCode')">
                  <el-input v-model="form.meetingCode" disabled />
                </el-form-item>
              </srm-col>

              <srm-col :init-col="4">
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
              </srm-col>
              <srm-col :init-col="4">
                <el-form-item prop="meetingTitle" :label="$t('meeting.meetingTitle')">
                  <el-input v-model="form.meetingTitle" />
                </el-form-item>
              </srm-col>
              <srm-col :init-col="4">
                <el-form-item prop="meetingTime" :label="$t('meeting.meetingTime')">
                  <el-date-picker
                    v-model="form.meetingTime"
                    type="datetimerange"
                    range-separator="至"
                    value-format="yyyy-MM-dd HH:mm"
                    format="yyyy-MM-dd HH:mm"
                  />
                </el-form-item>
              </srm-col>
              <srm-col :init-col="4">
                <el-form-item prop="meetingHour" :label="$t('meeting.meetingHour')">
                  <el-input v-model="form.meetingHour" />
                </el-form-item>
              </srm-col>
              <srm-col :init-col="4">
                <el-form-item prop="status" :label="$t('meeting.meetingStatus')">
                  <render-select
                    v-model="form.status"
                    :store="store"
                    code="MEET_MANAGE_STATUS"
                    disabled
                  />
                </el-form-item>
              </srm-col>
              <srm-col :init-col="4">
                <el-form-item prop="meetingAddr" :label="$t('meeting.meetingAddr')">
                  <el-input v-model="form.meetingAddr" />
                </el-form-item>
              </srm-col>
              <srm-col :init-col="4">
                <el-form-item :label="$t('common.creator')">
                  <el-input v-model="form.createdFullName" disabled />
                </el-form-item>
              </srm-col>
              <srm-col :init-col="4">
                <el-form-item :label="$t('common.creationTime')">
                  <el-input v-model="form.creationDate" disabled />
                </el-form-item>
              </srm-col>
              <srm-col :init-col="4">
                <el-form-item :label="$t('common.lastUpdatedFullName')">
                  <el-input v-model="form.lastUpdatedFullName" disabled />
                </el-form-item>
              </srm-col>
              <srm-col :init-col="4">
                <el-form-item :label="$t('common.lastUpdateDate')">
                  <el-input v-model="form.lastUpdateDate" disabled />
                </el-form-item>
              </srm-col>
            </srm-row>
          </el-form>
        </el-collapse-item>
        <el-collapse-item :title="$t('meeting.topicAndTodo')" name="2">
          <el-tabs v-model="tabVal">
            <el-tab-pane name="tab2" :label="$t('meeting.meetingTopic') + '（'+ subjectTotal + '）'">
              <el-table
                ref="subjectTable"
                highlight-current-row
                :row-class-name="subjectTableRowClassName"
                :data="subjectData"
                border
                stripe
                class="mt-10"
                @row-click="subjectRowClick"
              >
                <el-table-column prop="topicCode" :label="$t('meeting.topicCode')">
                  <template slot-scope="scope">
                    <el-button
                      type="text"
                      @click="goToLink(scope,'meetSubject')"
                    >
                      {{ scope.row.topicCode }}
                    </el-button>
                  </template>
                </el-table-column>
                <el-table-column
                  prop="topicName"
                  :label="$t('meeting.topicName')"
                  min-width="180"
                  show-overflow-tooltip
                />
                <el-table-column prop="relationBillCode" :label="$t('meeting.relationBillCode')" show-overflow-tooltip>
                  <template slot-scope="scope">
                    <el-button
                      type="text"
                      @click.stop="goToRelation(scope)"
                    >
                      {{ scope.row.relationBillCode }}
                    </el-button>
                  </template>
                </el-table-column>
                <el-table-column prop="inChargeName" :label="$t('meeting.inCharge')" show-overflow-tooltip />
                <el-table-column prop="inChargeDeptName" :label="$t('meeting.inChargeDeptName')" show-overflow-tooltip />
              </el-table>
            </el-tab-pane>
            <el-tab-pane name="tab1" :label="$t('meeting.todoHistory')+'（' + todoTotal+ '）'">
              <el-table
                ref="todoTable"
                :data="todoData"
                :row-class-name="toDoTableRowClassName"
                border
                stripe
                class="mt-10"
                @row-click="todoRowClick"
              >
                <el-table-column prop="meetTodoCode" :label="$t('meeting.meetTodoCode')">
                  <template slot-scope="scope">
                    <el-button
                      type="text"
                      @click="goToLink(scope,'meetTodo')"
                    >
                      {{ scope.row.meetTodoCode }}
                    </el-button>
                  </template>
                </el-table-column>
                <el-table-column
                  prop="todoContent"
                  :label="$t('meeting.todoContent')"
                  min-width="180"
                  show-overflow-tooltip
                />
                <el-table-column prop="todoDirectorName" :label="$t('meeting.inCharge')" show-overflow-tooltip />
                <el-table-column prop="department" :label="$t('meeting.inChargeDeptName')" show-overflow-tooltip />
                <el-table-column prop="topicName" :label="$t('meeting.relateTopic')" show-overflow-tooltip />
              </el-table>
              <p>{{ $t('meeting.todoStalkerRemark') }}</p>
              <el-input v-model="todoStalkerRemark" type="textarea" :rows="3" :disabled="form.status == 'END'" />
            </el-tab-pane>
          </el-tabs>
        </el-collapse-item>
        <el-collapse-item v-show="tabVal === 'tab2'" :title="$t('meeting.issueManagement')" name="3">
          <p>{{ $t('meeting.materialFile') }}</p>
          <el-table class="mt-10" :data="materialFileData" border stripe>
            <el-table-column type="index" width="60" :label="$t('common.sort')" />
            <el-table-column :label="$t('common.fileUploadName')">
              <template slot-scope="{ row }">
                <srm-common-file
                  :default-file="{
                    fileId: row.fileuploadId,
                    fileName: row.materialMould
                  }"
                  :readonly="true"
                />
              </template>
            </el-table-column>
            <el-table-column prop="createdFullName" :label="$t('common.uploadUserName')" />
            <el-table-column prop="creationDate" :label="$t('common.uploadDate')" />
          </el-table>
          <p ref="waitMake">
            {{ $t('meeting.awaitResolution') }}
          </p>
          <el-input
            v-model="currentSubjectRow.awaitResolution"
            type="textarea"
            :rows="3"
            disabled
          />
          <p><i class="toRequired">*</i>{{ $t('meeting.meetingMinutes') }}</p>
          <el-input
            v-model="currentSubjectRow.meetingMinutes"
            type="textarea"
            :rows="3"
            :disabled="disabledFlag || makeFlag"
          />
          <srm-row>
            <srm-col :init-col="4">
              <p><i class="toRequired">*</i>{{ $t('meeting.topicResolution') }}</p>
              <render-select
                v-model="currentSubjectRow.topicConclusion"
                :store="store"
                code="MEET_RESOLUTION"
                :disabled="disabledFlag || makeFlag || !canMeetConfirm"
              />
            </srm-col>
            <srm-col v-if="currentSubjectRow.topicConclusion === 'UPGRADE'" :init-col="4">
              <p>{{ $t('meeting.upgrade') }}</p>
              <render-select
                v-model="currentSubjectRow.upgrade"
                :store="store"
                code="FIXED_POINT_MEETING_TYPE"
                :disabled="disabledFlag || makeFlag || !canMeetConfirm"
              />
            </srm-col>
          </srm-row>
          <srm-row class="mt-10">
            <srm-col :init-col="1">
              <el-button
                v-if="!disabledFlag && !makeFlag"
                type="primary"
                :disabled="!(currentSubjectRow.topicConclusion === 'APPROVAL' && canMeetConfirm)"
                @click="gotoMeetConfirm"
              >
                {{ canMeetConfirm ? this.$t('meeting.createIssueConfirm') :this.$t('meeting.hadCreateIssueConfirm') }}
              </el-button>
              <el-button
                v-if="!disabledFlag && makeFlag && isReviewer && currentSubjectRow.topicConclusion === 'APPROVAL'"
                type="primary"
                :disabled="hasMaked"
                @click="handleControl('APPROVAL')"
              >
                {{ $t('common.toPass') }}
              </el-button>
              <el-button
                v-if="!disabledFlag && makeFlag && isReviewer && currentSubjectRow.topicConclusion === 'APPROVAL'"
                type="primary"
                :disabled="hasMaked"
                @click="handleControl('REJECT')"
              >
                {{ $t('common.refused') }}
              </el-button>
            </srm-col>
          </srm-row>
          <srm-row class="mt-10">
            <srm-col :init-col="2">
              <el-table :data="makerData" border stripe @selection-change="handleMakerSelect">
                <el-table-column
                  v-if="!disabledFlag && !makeFlag && canMeetConfirm"
                  type="selection"
                  width="60"
                />
                <el-table-column type="index" :label="$t('common.sort')" width="60" />
                <el-table-column prop="reviewerName" :label="$t('meeting.reviewer')" />
                <el-table-column :label="$t('meeting.meetManageResult')">
                  <template slot-scope="scope">
                    <span>{{ store.getLabel('MEET_MANAGE_RESULT',scope.row.reviewerResults) }}</span>
                  </template>
                </el-table-column>
              </el-table>
            </srm-col>
          </srm-row>
          <el-table :data="subjectMemberData" border stripe class="mt-10">
            <el-table-column prop="fullName" :label="$t('meeting.fullName')" />
            <el-table-column prop="deptName" :label="$t('meeting.deptName')" />
            <el-table-column prop="topicRole" :label="$t('meeting.role')">
              <template slot-scope="scope">
                {{ store.getLabel('MEET_ROLE',scope.row.topicRole) }}
              </template>
            </el-table-column>
            <el-table-column prop="mobileNo" :label="$t('meeting.mobileNo')" />
            <el-table-column prop="email" :label="$t('common.email')" />
            <el-table-column prop="inCharge" :label="$t('meeting.inCharge')">
              <template slot-scope="scope">
                <el-checkbox v-model="scope.row.inCharge" disabled />
              </template>
            </el-table-column>
            <el-table-column prop="enableMeetingInvitee" :label="$t('meeting.meetingInvitee')">
              <template slot-scope="scope">
                <el-checkbox v-model="scope.row.enableMeetingInvitee" disabled />
              </template>
            </el-table-column>
            <el-table-column prop="enableMinutesSender" :label="$t('meeting.minutesSender')">
              <template slot-scope="scope">
                <el-checkbox v-model="scope.row.enableMinutesSender" disabled />
              </template>
            </el-table-column>
          </el-table>
        </el-collapse-item>
        <el-collapse-item v-if="tabVal === 'tab2'" :title="$t('meeting.meetingMember')" name="4">
          <el-table :data="memberData" border stripe class="mt-10">
            <el-table-column prop="fullName" :label="$t('meeting.fullName')">
              <template slot-scope="scope">
                <QuickSearch
                  :show-input="scope.row.fullName"
                  disabled
                  show-key="username"
                  :scope-data="scope.row"
                  auto-query
                  name="scc_rbac_user_display"
                  @close-quicksearch="getUserObj"
                />
              </template>
            </el-table-column>
            <el-table-column prop="deptName" :label="$t('meeting.deptName')">
              <template slot-scope="scope">
                <el-input v-model="scope.row.deptName" disabled />
              </template>
            </el-table-column>
            <el-table-column prop="email" :label="$t('common.email')">
              <template slot-scope="scope">
                <el-input v-model="scope.row.email" disabled />
              </template>
            </el-table-column>
            <el-table-column prop="chair" :label="$t('meeting.chair')">
              <template slot-scope="scope">
                <el-checkbox v-model="scope.row.chair" disabled />
              </template>
            </el-table-column>
            <el-table-column prop="reviewer" :label="$t('meeting.reviewer')">
              <template slot-scope="scope">
                <el-checkbox v-model="scope.row.reviewer" disabled />
              </template>
            </el-table-column>
            <el-table-column prop="enableMeetingInvitee" :label="$t('meeting.meetingInvitee')">
              <template slot-scope="scope">
                <el-checkbox v-model="scope.row.enableMeetingInvitee" disabled />
              </template>
            </el-table-column>
            <el-table-column prop="enableMinutesSender" :label="$t('meeting.minutesSender')">
              <template slot-scope="scope">
                <el-checkbox v-model="scope.row.enableMinutesSender" disabled />
              </template>
            </el-table-column>
            <!-- <el-table-column prop="topicCode" label="负责议题" />
            <el-table-column prop="topicName" label="议题名称" />
            <el-table-column prop="inCharge" label="负责人">
              <template slot-scope="scope">
                <el-checkbox v-model="scope.row.inCharge" disabled />
              </template>
            </el-table-column> -->
          </el-table>
        </el-collapse-item>
      </el-collapse>
    </el-main>
    <CToolbar>
      <template slot="right">
        <el-button type="ghost" @click="back">
          {{ $t('common.cancel') }}
        </el-button>
        <el-button
          v-if="!disabledFlag && !makeFlag"
          type="primary"
          @click="saveBill('SAVE')"
        >
          {{ $t('common.staging') }}
        </el-button>
        <el-button
          v-if="!disabledFlag && !makeFlag"
          type="primary"
          @click="endBill"
        >
          {{ $t('meeting.stopMeeting') }}
        </el-button>
      </template>
    </CToolbar>
  </el-container>
</template>

<script>
import CToolbar from 'lib@/components/c-toolbar'
import { tabTodoMixin } from '@/utils/mixins'
import QuickSearch from 'lib@/components/QuickSearch'
import OrganizationSelector from 'lib@/components/organization-selector'
import ssMaterials from './ssMaterials'
import sdMaterials from './sdMaterials'
// import supaccountperiodchangeEdit from 'modb@/vendorManagementBuyer/views/supAccountPeriodChange/edit'
export default {
  name: 'MeetManageSee',

  components: {
    CToolbar,
    QuickSearch,
    OrganizationSelector
  },

  mixins: [tabTodoMixin],

  data () {
    return {
      activeDims: ['1', '2', '3', '4'],
      form: {
        meetingId: null,
        meetingCode: null,
        invId: null,
        invCode: null,
        invName: null,
        meetingTitle: null,
        meetingTime: [], // 会议时间 meetingStartTime ~ meetingEndTime
        meetingHour: null,
        status: null,
        meetingAddr: null,
        createdFullName: null,
        creationDate: null,
        lastUpdatedFullName: null,
        lastUpdateDate: null
      },
      tabVal: 'tab2',
      todoData: [],
      subjectData: [],
      memberData: [],
      todoStalkerRemark: '', // 待办事项的跟踪说明
      materialFileData: [], // 上会材料
      makerData: [], // 决策人列表
      curSubjectIndex: 0,
      curTodoIndex: 0,
      makerSelections: [], // 已勾选的决策人
      canMeetConfirm: false, // 是否可以发起议题确认 标识
      subjectMemberData: []
    }
  },

  computed: {
    urlParams () {
      return this.$attrs.params || {}
    },
    disabledFlag () {
      return this.urlParams.flag === 'view'
    },
    makeFlag () {
      return this.urlParams.flag === 'make'
    },
    subjectTotal () {
      return this.subjectData.length
    },
    todoTotal () {
      return this.todoData.length
    },
    currentSubjectRow () {
      if (!this.curSubjectIndex && this.curSubjectIndex !== 0) return {}
      return this.subjectData[this.curSubjectIndex] || {}
    },
    userInfo () {
      return this.$store.getters.userInfo
    },
    hasMaked () { // 决策人对当前议题是否已经决策过，如果决策过就置灰批准驳回按钮
      let flagIndex = this.makerData.findIndex(item => item.reviewerName === this.userInfo.nickname)
      if (flagIndex > -1) {
        let { reviewerResults } = this.makerData[flagIndex]
        return !(reviewerResults === 'WAIT_APPROVAL' || !reviewerResults)
      }
      return true
    },
    isReviewer () {
      let flagIndex = this.makerData.findIndex(item => item.reviewerName === this.userInfo.nickname)
      return flagIndex > -1
    }
  },

  created () {
    let { row } = this.urlParams
    if (row && row.meetingId) {
      this.getFormDetail(row.meetingId)
    }
  },

  mounted () {
    let { flag } = this.urlParams
    if (flag === 'make') {
      this.tabVal = 'tab2'
      this.$nextTick(() => {
        this.$refs.waitMake.scrollIntoView(true)
      })
    }
  },

  methods: {
    // 默认切换到需要决议的议题
    async getReviewer (meetingId, subjectData) {
      const data = await this.$http({
        url: '/api-inq/inq/meettopic/list/reviewerforuser',
        method: 'GET',
        params: { meetingId },
        loading: true
      })
      if (data) {
        let reviewData = data.data || {}
        let topicId = ''
        for (let key in reviewData) {
          let flag = reviewData[key].findIndex(item => item.reviewerName === this.userInfo.nickname)
          if (flag > -1) topicId = key
        }
        let index = subjectData.findIndex(item => item.topicId == topicId)
        return index > -1 ? index : 0
      } else {
        return 0
      }
    },

    endBill () {
      let flag = this.subjectData.some(item => !item.meetingMinutes)
      if (flag) {
        this.$message.warning(this.$t('meeting.rule.fillAllTopic'))
        return
      }
      this.$confirm(this.$t('meeting.rule.okToStopMeeting'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          this.saveBill('SUBMIT')
        })
    },

    saveBill (type) {
      if (this.todoData.length && this.todoStalkerRemark) {
        let obj = this.todoData[this.curTodoIndex]
        obj.todoStalkerRemark = this.todoStalkerRemark
        this.$http({
          url: '/api-inq/inq/meetTodo/track/submit',
          method: 'POST',
          data: obj,
          loading: true
        })
      }
      let url, params
      if (type === 'SAVE') {
        url = '/api-inq/inq/meeting/pro/manuscript'
        params = this.subjectData
        params.todoStalkerRemark = this.todoStalkerRemark
      } else {
        params = {}
        url = '/api-inq/inq/meeting/pro/submit'
        params.meetingId = this.form.meetingId
        params.meetTopics = this.subjectData
      }
      this.$http({
        url,
        method: 'POST',
        data: params,
        loading: true
      }).then(res => {
        this.$message.success(res.message)
        if (type === 'SUBMIT') {
          this.back()
        }
      })
    },

    invSelectHandler (node) {
      this.form.invId = node ? node.organizationId : null
      this.form.invCode = node ? node.organizationCode : null
      this.form.invName = node ? node.organizationName : null
    },

    getTopicObj (val, row) {
      let attrs = ['topicId', 'topicCode', 'topicName', 'relationBillCode', 'fullName', 'deptName']
      for (let key of attrs) {
        row[key] = val ? val[key] : null
      }
      if (!val.topicId) return
      this.getTopicFormDetail(val.topicId).then(data => {
        let { meetTopicMemberList } = data
        this.memberData.push(...meetTopicMemberList)
      })
    },

    goToLink (scope, name) {
      this.$router.push({
        name,
        params: {
          from: 'fromMeetManage',
          row: scope.row
        }
      })
    },
    async goToRelation (scope) { // 跳转关联单号
      let { row } = scope
      if (!row.relationBillId) return
      let tab
      if (row.topicType === 'SS') {
        // 跳转到ss上会材料
        tab = {
          component: ssMaterials,
          params: {
            row: {
              inquiryId: row.relationBillId,
              topicId: row.topicId

            },
            name: `ssMaterials${row.relationBillCode}`
          },
          title: `ss上会材料${this.form.meetingCode || ''}`,
          name: `ssMaterials${row.relationBillCode}`
        }
      } else if (row.topicType === 'SD') {
        tab = {
          component: sdMaterials,
          params: {
            row: {
              inquiryId: row.relationBillId,
              topicId: row.topicId

            },
            name: `sdMaterials${row.relationBillCode}`
          },
          title: `sd上会材料${this.form.meetingCode || ''}`,
          name: `sdMaterials${row.relationBillCode}`
        }
      } else if (row.topicType === 'PC') {
        // let result = await this.$http({
        //   url: `/api-sup/sup/supaccountperiodchange/get?id=${row.relationBillId}`,
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

    todoRowClick (row, column, event) { // 待办事项点击当前行
      this.curTodoIndex = row.index
      if (row && row.meetTodoId) {
        this.getTodoDetail(row.meetTodoId).then(result => {
          this.todoStalkerRemark = result.todoStalkerRemark
        })
      }
    },

    getTodoDetail (meetTodoId) {
      return new Promise(resolve => {
        this.$http({
          url: '/api-inq/inq/meetTodo/get',
          method: 'GET',
          params: { meetTodoId }
        }).then(res => {
          resolve(res.data)
        })
      })
    },

    subjectRowClick (row) { // 会议主题点击当前行
      this.curSubjectIndex = row.index
      if (row && row.topicId) {
        this.copyTopicFormDetail(row.topicId)
        this.getMakerData(row.topicId)
      }
    },

    copyTopicFormDetail (topicId) {
      this.getTopicFormDetail(topicId).then(result => {
        let { meetTopicMemberList, meetTopicMaterialList } = result || {}
        this.materialFileData = meetTopicMaterialList
        this.subjectMemberData = meetTopicMemberList
      })
    },

    toDoTableRowClassName ({ row, rowIndex }) {
      row.index = rowIndex
      if (rowIndex === this.curTodoIndex) {
        return 'hover-row'
      }
    },

    subjectTableRowClassName ({ row, rowIndex }) {
      row.index = rowIndex
      if (rowIndex === this.curSubjectIndex) {
        return 'hover-row'
      }
    },

    getUserObj (val, scope) {
      scope.fullNameId = val.userId
      scope.fullName = val.nickname
      scope.deptName = val.department
      scope.mobileNo = val.phone
      scope.email = val.email
    },

    getMeetObj (val, scope) {
      let attrs = ['topicModelId', 'topicModelCode', 'topicModelName', 'topicType']
      for (let key of attrs) {
        scope[key] = val ? val[key] : null
      }
      if (!val || !val.topicModelId) return
      this.getModelFormDetail(val.topicModelId)
    },

    getTopicFormDetail (topicId) {
      return new Promise(resolve => {
        this.$http({
          url: '/api-inq/inq/meettopic/get',
          method: 'GET',
          params: { topicId }
        }).then(res => {
          resolve(res.data)
        })
      })
    },

    getFormDetail (meetingId) {
      this.$http({
        url: '/api-inq/inq/meeting/get',
        method: 'GET',
        params: { meetingId },
        loading: true
      }).then(async res => {
        let { meetingMemberList = [], meetTopicList = [], meetTodoList = [], ...rest } = res.data || {}
        Object.assign(this.form, rest)
        this.todoData = meetTodoList
        this.subjectData = meetTopicList
        this.memberData = meetingMemberList
        this.form.meetingTime = [this.form.meetingStartTime, this.form.meetingEndTime]
        if (this.todoData.length) {
          let meetTodoId = this.todoData[0].meetTodoId
          if (meetTodoId) {
            this.getTodoDetail(meetTodoId).then(result => {
              this.todoStalkerRemark = result.todoStalkerRemark
            })
          }
        }
        if (this.subjectData.length) {
          let index = await this.getReviewer(meetingId, this.subjectData)
          let topicId = this.subjectData[index].topicId
          if (topicId) {
            this.$nextTick(() => {
              this.subjectRowClick(this.subjectData[index])
            })
          }
        }
      })
    },
    back () {
      let { tabName } = this.$attrs.params
      this.$emit('tab-remove', tabName)
      this.__setTabTodo('MeetManageList.getQuerydata')
    },
    handleMakerSelect (selections) {
      this.makerSelections = selections
    },

    // 发起议题确认
    gotoMeetConfirm () {
      if (!this.makerSelections.length) {
        this.$message.warning(this.$t('meeting.rule.selectReviewer'))
        return
      }
      if (!this.currentSubjectRow.meetingMinutes) {
        this.$message.warning(this.$t('meeting.rule.complateMeetingMinutes'))
        return
      }
      if (!this.currentSubjectRow.topicConclusion) {
        this.$message.warning(this.$t('meeting.rule.selectTopicConclusion'))
        return
      }
      let { topicId } = this.currentSubjectRow
      if (!topicId) return
      this.$http({
        url: '/api-inq/inq/meeting/pro/issue/confirmation',
        method: 'POST',
        data: {
          topicId,
          meetingMinutes: this.currentSubjectRow.meetingMinutes,
          topicConclusion: this.currentSubjectRow.topicConclusion,
          meetTopicReviewerList: this.makerSelections
        },
        loading: true
      }).then(res => {
        this.$message.success(res.message)
        this.getFormDetail(this.form.meetingId)
      })
    },

    getMakerData (topicId) {
      this.$http({
        url: '/api-inq/inq/meettopic/list/reviewer',
        method: 'GET',
        params: { topicId }
      }).then(res => {
        this.makerData = res.data || []
        if (!this.makerData.length) { // 如果没有值取的是成员列表中的决策人
          this.canMeetConfirm = true // 没有决策人就代表是没有发起议题确认
          this.makerData = JSON.parse(JSON.stringify(this.memberData)).filter(item => item.reviewer)
          this.makerData.forEach(item => {
            item.reviewerId = item.fullNameId
            item.reviewerName = item.fullName
          })
        } else {
          this.canMeetConfirm = false
        }
      })
    },

    // 批准 驳回
    handleControl (type) {
      let topicId = this.currentSubjectRow.topicId
      if (!topicId) {
        this.$message.info(this.$t('meeting.rule.selectTopic'))
        return
      }
      this.$http({
        url: '/api-inq/inq/meeting/pro/reviewer',
        method: 'POST',
        params: {
          topicId,
          reviewerResults: type
        }
      }).then(res => {
        this.$message.success(res.message)
        let { topicId } = this.currentSubjectRow
        if (!topicId) return
        this.getMakerData(topicId)
        let { meetingId } = this.form
        if (meetingId) this.getFormDetail(meetingId)
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.mt-10 {
  margin-top: 10px;
}
.wrapper {
  padding-bottom: 40px;
}
.toRequired {
  color:red;
}
</style>
