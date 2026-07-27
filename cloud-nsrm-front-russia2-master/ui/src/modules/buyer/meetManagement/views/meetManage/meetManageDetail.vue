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
            <srm-row>
              <!--会议编号-->
              <srm-col :init-col="4">
                <el-form-item prop="meetingCode" :label="$t('meeting.meetingCode')">
                  <el-input v-model="form.meetingCode" disabled />
                </el-form-item>
              </srm-col>

              <!--库存组织-->
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

              <!--会议名称-->
              <srm-col :init-col="4">
                <el-form-item prop="meetingTitle" :label="$t('meeting.meetingTitle')">
                  <el-input v-model="form.meetingTitle" />
                </el-form-item>
              </srm-col>

              <!--会议时间-->
              <srm-col :init-col="4">
                <el-form-item prop="meetingStartTime" :label="$t('meeting.meetingTime')">
                  <el-date-picker
                    v-model="form.meetingStartTime"
                    type="datetime"
                    value-format="yyyy-MM-dd HH:mm"
                    :format="$formatDatePickerTime"
                    :picker-options="startTimeOptions"
                    @change="changeTime"
                  />
                </el-form-item>
              </srm-col>

              <!--会议结束时间-->
              <srm-col :init-col="4">
                <el-form-item prop="meetingEndTime" :label="$t('meeting.meetingEndTime')">
                  <el-date-picker
                    v-model="form.meetingEndTime"
                    type="datetime"
                    value-format="yyyy-MM-dd HH:mm"
                    :format="$formatDatePickerTime"
                    :picker-options="endTimeOptions"
                    @change="changeTime"
                  />
                </el-form-item>
              </srm-col>

              <!--会议时长-->
              <srm-col :init-col="4">
                <el-form-item prop="meetingHour" :label="$t('meeting.meetingHour')">
                  <el-input v-model="form.meetingHour" disabled />
                </el-form-item>
              </srm-col>

              <!--会议状态-->
              <srm-col :init-col="4">
                <el-form-item prop="status" :label="$t('meeting.meetingStatus')">
                  <DictSelect
                    v-model="form.status"
                    code="MEET_MANAGE_STATUS"
                    disabled
                  />
                </el-form-item>
              </srm-col>

              <!--会议地点-->
              <srm-col :init-col="4">
                <el-form-item prop="meetingAddr" :label="$t('meeting.meetingAddr')">
                  <el-input v-model="form.meetingAddr" />
                </el-form-item>
              </srm-col>

              <!--创建人-->
              <srm-col :init-col="4">
                <el-form-item :label="$t('common.creator')">
                  <el-input v-model="form.createdFullName" disabled />
                </el-form-item>
              </srm-col>

              <!--创建时间-->
              <srm-col :init-col="4">
                <el-form-item :label="$t('common.creationTime')">
                  <el-date-picker
                    v-model="form.creationDate"
                    :format="$formatDatePickerTime"
                    value-format="yyyy-MM-dd HH:mm:ss"
                    disabled
                  />
                </el-form-item>
              </srm-col>

              <!--更新人-->
              <srm-col :init-col="4">
                <el-form-item :label="$t('common.lastUpdatedFullName')">
                  <el-input v-model="form.lastUpdatedFullName" disabled />
                </el-form-item>
              </srm-col>

              <!--更新日期-->
              <srm-col :init-col="4">
                <el-form-item :label="$t('common.lastUpdateDate')">
                  <el-date-picker
                    v-model="form.lastUpdateDate"
                    :format="$formatDatePickerTime"
                    value-format="yyyy-MM-dd HH:mm:ss"
                    disabled
                  />
                </el-form-item>
              </srm-col>
            </srm-row>
          </el-form>
        </el-collapse-item>

        <!--会议议题-->
        <el-collapse-item :title="$t('meeting.meetingTopic')" name="2">
          <el-tabs v-model="tabVal">
            <!--会议议题-->
            <el-tab-pane name="tab2" :label="$t('meeting.meetingTopic') + '（'+ subjectTotal + '）'">
              <QuickSearch
                :show-button="false"
                :btn-title="$t('common.add')"
                :multi-select="true"
                :disabled="disabledFlag"
                name="meeting_lts_scc_meet_topic"
                :pre-query-data="{'t.INV_ID':form.invId}"
                @close-quicksearch="getManyTopicObj"
              />
              <el-table
                ref="subjectTable"
                :data="subjectData"
                border
                stripe
                class="mt-10"
                :row-class-name="rowClassName"
                @row-click="rowClick"
              >
                <!--议题编号-->
                <el-table-column prop="topicCode" :label="$t('meeting.topicCode')">
                  <template v-slot="scope">
                    <el-button
                      type="text"
                      @click="goToLink(scope,'meetSubject')"
                    >
                      {{ scope.row.topicCode }}
                    </el-button>
                  </template>
                </el-table-column>

                <!--议题名称-->
                <el-table-column
                  prop="topicName"
                  :label="$t('meeting.topicName')"
                  min-width="180"
                  show-overflow-tooltip
                />

                <!--关联单据号-->
                <el-table-column
                  prop="relationBillCode"
                  :label="$t('meeting.relationBillCode')"
                  show-overflow-tooltip
                >
                  <template v-slot="scope">
                    <el-button
                      type="text"
                      @click="goToRelation(scope)"
                    >
                      {{ scope.row.relationBillCode }}
                    </el-button>
                  </template>
                </el-table-column>

                <!--负责人-->
                <el-table-column
                  prop="inChargeName"
                  :label="$t('meeting.inCharge')"
                  show-overflow-tooltip
                />

                <!--负责部门-->
                <el-table-column
                  prop="inChargeDeptName"
                  :label="$t('meeting.inChargeDeptName')"
                  show-overflow-tooltip
                />

                <el-table-column :label="$t('common.operation')" width="100">
                  <template v-slot="scope">
                    <!--删除-->
                    <el-button
                      type="text"
                      :disabled="disabledFlag"
                      @click.stop="deleteMeetSubject(scope)"
                    >
                      {{ $t('common.delete') }}
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>

              <el-table
                :data="subjectMemberData"
                border
                stripe
                class="mt-10"
              >
                <!--姓名-->
                <el-table-column prop="fullName" :label="$t('meeting.fullName')" />

                <!--部门-->
                <el-table-column prop="deptName" :label="$t('meeting.deptName')" />

                <!--角色-->
                <el-table-column
                  prop="topicRole"
                  :label="$t('meeting.role')"
                  :formatter="(row, column, cellValue) => $getDictLabel('MEET_ROLE', cellValue)"
                />

                <!--手机号-->
                <el-table-column prop="mobileNo" :label="$t('meeting.mobileNo')" />

                <!--邮箱-->
                <el-table-column prop="email" :label="$t('common.email')" />

                <!--负责人-->
                <el-table-column prop="inCharge" :label="$t('meeting.inCharge')">
                  <template v-slot="scope">
                    <el-checkbox v-model="scope.row.inCharge" disabled />
                  </template>
                </el-table-column>

                <!--参会邀请人-->
                <el-table-column prop="enableMeetingInvitee" :label="$t('meeting.meetingInvitee')">
                  <template v-slot="scope">
                    <el-checkbox v-model="scope.row.enableMeetingInvitee" disabled />
                  </template>
                </el-table-column>

                <!--纪要发送人-->
                <el-table-column prop="enableMinutesSender" :label="$t('meeting.minutesSender')">
                  <template v-slot="scope">
                    <el-checkbox v-model="scope.row.enableMinutesSender" disabled />
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>

            <!--代办事项-->
            <el-tab-pane name="tab1" :label="$t('meeting.todo')+'（' + todoTotal+ '）'">
              <QuickSearch
                :show-button="false"
                :btn-title="$t('common.add')"
                :multi-select="true"
                :disabled="disabledFlag"
                name="meeting-lts_scc_meet_todo"
                :pre-query-data="{'t.INV_ID':form.invId}"
                @close-quicksearch="getManyTodoObj"
              />

              <el-table
                :data="todoData"
                border
                stripe
                class="mt-10"
              >
                <!--待办编号-->
                <el-table-column prop="meetTodoCode" :label="$t('meeting.meetTodoCode')">
                  <template v-slot="scope">
                    <el-button
                      type="text"
                      @click="goToLink(scope,'meetTodo')"
                    >
                      {{ scope.row.meetTodoCode }}
                    </el-button>
                  </template>
                </el-table-column>

                <!--待办内容-->
                <el-table-column
                  prop="todoContent"
                  :label="$t('meeting.todoContent')"
                  min-width="180"
                  show-overflow-tooltip
                />

                <!--关联议题-->
                <el-table-column prop="topicName" :label="$t('meeting.relateTopic')" />

                <el-table-column :label="$t('common.operation')" width="100">
                  <template v-slot="scope">
                    <!--删除-->
                    <el-button
                      type="text"
                      :disabled="disabledFlag"
                      @click="deleteMeetTodo(scope)"
                    >
                      {{ $t('common.delete') }}
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>
          </el-tabs>
        </el-collapse-item>

        <!--会议成员-->
        <el-collapse-item
          ref="memberTable"
          :title="$t('meeting.meetingMember')"
          name="3"
        >
          <!--新增-->
          <el-button
            type="primary"
            :disabled="disabledFlag"
            @click="addMeetPerson"
          >
            {{ $t('common.add') }}
          </el-button>
          <!--选择常用组-->
          <el-button
            type="primary"
            :disabled="disabledFlag"
            @click="chooseMeetPerson"
          >
            {{ $t('meeting.selectCommonGroup') }}
          </el-button>
          <!--保存常用组-->
          <el-button
            type="primary"
            :disabled="disabledFlag"
            @click="saveMeetPerson"
          >
            {{ $t('meeting.saveCommonGroup') }}
          </el-button>

          <el-table
            :data="memberData"
            border
            stripe
            class="mt-10"
          >
            <!--姓名-->
            <el-table-column prop="fullName" :label="$t('meeting.fullName')">
              <template v-slot="scope">
                <QuickSearch
                  :show-input="scope.row.fullName"
                  :disabled="disabledFlag || scope.row._disabled"
                  show-key="username"
                  :scope-data="scope.row"
                  auto-query
                  name="scc_rbac_user_display"
                  @close-quicksearch="getUserObj"
                />
              </template>
            </el-table-column>

            <!--部门-->
            <el-table-column prop="deptName" :label="$t('meeting.deptName')">
              <template v-slot="scope">
                <el-input v-model="scope.row.deptName" disabled />
              </template>
            </el-table-column>

            <!--邮箱-->
            <el-table-column prop="email" :label="$t('common.email')">
              <template v-slot="scope">
                <el-input v-model="scope.row.email" disabled />
              </template>
            </el-table-column>

            <!--主持人-->
            <el-table-column prop="chair" :label="$t('meeting.chair')">
              <template v-slot="scope">
                <el-checkbox v-model="scope.row.chair" disabled />
              </template>
            </el-table-column>

            <!--决策人-->
            <el-table-column prop="reviewer" :label="$t('meeting.reviewer')">
              <template v-slot="scope">
                <el-checkbox
                  v-model="scope.row.reviewer"
                  :disabled="disabledFlag || scope.row._disabled || scope.row.inCharge"
                  @change="val => reviewerChange(scope.row,val)"
                />
              </template>
            </el-table-column>

            <!--参会邀请人-->
            <el-table-column prop="enableMeetingInvitee" :label="$t('meeting.meetingInvitee')">
              <template v-slot="scope">
                <el-checkbox
                  v-model="scope.row.enableMeetingInvitee"
                  :disabled="disabledFlag || scope.row.chair || scope.row.reviewer"
                />
              </template>
            </el-table-column>

            <!--纪要发送人-->
            <el-table-column prop="enableMinutesSender" :label="$t('meeting.minutesSender')">
              <template v-slot="scope">
                <el-checkbox
                  v-model="scope.row.enableMinutesSender"
                  :disabled="disabledFlag || scope.row.chair || scope.row.reviewer"
                />
              </template>
            </el-table-column>

            <el-table-column :label="$t('common.operation')">
              <template v-slot="scope">
                <!--删除-->
                <el-button
                  type="text"
                  :disabled="disabledFlag || scope.row._disabled || scope.row.chair"
                  @click="deleteMember(scope)"
                >
                  {{ $t('common.delete') }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-collapse-item>
      </el-collapse>
    </el-main>

    <CToolbar>
      <template slot="right">
        <!--取消-->
        <el-button type="ghost" @click="back">
          {{ $t('common.cancel') }}
        </el-button>

        <!--暂存-->
        <el-button
          v-if="!disabledFlag && !changeFlag"
          type="primary"
          @click="saveBill('SAVE')"
        >
          {{ $t('common.staging') }}
        </el-button>

        <!--提交-->
        <el-button
          v-if="!disabledFlag && !changeFlag"
          type="primary"
          @click="saveBill('SUBMIT')"
        >
          {{ $t('common.submit') }}
        </el-button>

        <!--会议变更-->
        <el-button
          v-if="!disabledFlag && changeFlag"
          type="primary"
          @click="saveBill('CHANGE')"
        >
          {{ $t('meeting.meetingChange') }}
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
        <!--请填写常用组名称-->
        <el-form-item
          prop="groupName"
          :label="$t('meeting.rule.fillGroupName')"
          :rules="[{required:true,message:$t('common.pleaseInput')}]"
        >
          <el-input v-model="commonPersonForm.groupName" />
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="saveVisible = false">{{ $t('common.cancel') }}</el-button>
        <el-button type="primary" @click="saveCommonConfirm">{{ $t('common.confirm') }}</el-button>
      </span>
    </el-dialog>

    <CommonUser
      :show.sync="commonUserShow"
      group-type="meeting"
      @row-click="commonUserRowClick"
    />
  </el-container>
</template>

<script>
import CToolbar from 'lib@/components/c-toolbar'
import { tabTodoMixin } from '@/utils/mixins'
import QuickSearch from 'lib@/components/QuickSearch'
import OrganizationSelector from 'lib@/components/organization-selector'
// import supaccountperiodchangeEdit from 'modb@/vendorManagementBuyer/views/supAccountPeriodChange/edit'
import CommonUser from '../components/commonUser'
import ssMaterials from './ssMaterials'
import sdMaterials from './sdMaterials'

export default {
  name: 'MeetManageDetail',

  components: {
    CToolbar,
    QuickSearch,
    OrganizationSelector,
    CommonUser
  },

  mixins: [tabTodoMixin],

  data () {
    return {
      startTimeOptions: {
        disabledDate (val) {
          let now = new Date(new Date().toLocaleDateString()).getTime()
          let chooseStartTime = new Date(val).getTime()
          return !(chooseStartTime >= now)
        },
        start: '08:30',
        step: '00:30',
        end: '18:30'
      },
      endTimeOptions: {
        disabledDate (val) {
          let now = new Date(new Date().toLocaleDateString()).getTime()
          let chooseEndTime = new Date(val).getTime()
          return !(chooseEndTime >= now)
        }
      },
      activeDims: ['1', '2', '3', '4'],
      userInfo: this.$store.getters.userInfo,
      form: {
        meetingId: null,
        meetingCode: null,
        invId: null,
        invCode: null,
        invName: null,
        meetingTitle: null,
        // meetingTime:[],// meetingStartTime ~ meetingEndTime
        meetingStartTime: null,
        meetingEndTime: null,
        meetingHour: null,
        status: null,
        meetingAddr: null,
        createdFullName: null,
        creationDate: null,
        lastUpdatedFullName: null,
        lastUpdateDate: null
      },
      rules: {
        meetingTitle: [{ required: true, message: this.$t('common.pleaseInput') }],
        meetingHour: [{ required: true, message: this.$t('common.pleaseInput') }],
        meetingAddr: [{ required: true, message: this.$t('common.pleaseInput') }],
        invId: [{ required: true, message: this.$t('common.pleaseSelect') }],
        meetingStartTime: [{ required: true, message: this.$t('common.pleaseSelect') }],
        meetingEndTime: [{ required: true, message: this.$t('common.pleaseSelect') }]
      },
      tabVal: 'tab2',
      todoData: [],
      subjectData: [],
      memberData: [],
      subjectMemberData: [],
      curIndex: 0,
      commonUserShow: false,
      saveVisible: false,
      commonPersonForm: {
        groupName: ''
      }
    }
  },

  computed: {
    urlParams () {
      return this.$attrs.params || {}
    },
    disabledFlag () {
      return this.urlParams.flag === 'view'
    },
    changeFlag () {
      return this.urlParams.flag === 'change'
    },
    subjectTotal () {
      return this.subjectData.length
    },
    todoTotal () {
      return this.todoData.length
    }
  },

  created () {
    let { row, flag } = this.urlParams
    if (row && row.meetingId) {
      this.getFormDetail(row.meetingId)
    }
    if (flag === 'add') {
      let { userId, nickname, department, phone, email } = this.userInfo
      this.memberData = []
      this.memberData.push({
        fullNameId: userId,
        fullName: nickname,
        deptName: department,
        mobileNo: phone,
        email,
        chair: true,
        inCharge: false,
        reviewer: false,
        enableMeetingInvitee: true,
        enableMinutesSender: true,
        _disabled: true
      })
    }
  },

  methods: {
    saveBill (type) {
      let urlList = [
        { type: 'SAVE', url: '/api-inq/inq/meeting/manuscript' },
        { type: 'SUBMIT', url: '/api-inq/inq/meeting/submit' },
        { type: 'CHANGE', url: '/api-inq/inq/meeting/change' }
      ]
      let url = urlList.find(item => item.type === type).url
      if (['SUBMIT', 'CHANGE'].includes(type)) {
        let validFlag
        // eslint-disable-next-line no-return-assign
        this.$refs.form.validate(valid => validFlag = valid)
        if (!validFlag) {
          this.__focus_error__()
          return
        }
        if (!this.subjectData.length) {
          this.__jump_error__('subjectTable', 'component', this.$t('meeting.rule.meetingListNotEmpty'))
          return
        }
        if (!this.memberData.length) {
          this.__jump_error__('memberTable', 'component', this.$t('meeting.rule.meetingMemberNotEmpty'))
          return
        }
        let hasMemberData = this.memberData.filter(item => item.reviewer && item.fullNameId)
        if (!hasMemberData.length) {
          this.__jump_error__('memberTable', 'component', this.$t('meeting.rule.selectOneReviewer'))
          return
        }
      }
      let params = this.initParams()
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

    chooseMeetPerson () {
      this.commonUserShow = true
    },

    saveCommonConfirm () {
      this.$refs.dialogForm.validate(valid => {
        if (valid) {
          let filterMemberList = this.memberData.filter(item => item.fullNameId)
          this.$http({
            url: '/api-inq/inq/meetGroup/submit',
            method: 'POST',
            data: {
              // 议题：topic,议会:meeting
              groupType: 'meeting',
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

    saveMeetPerson () {
      let filterMemberList = this.memberData.filter(item => item.fullNameId)
      if (!filterMemberList.length) {
        this.$message.warning(this.$t('meeting.rule.memberDataNotEmpty'))
        return
      }
      this.saveVisible = true
    },

    commonUserRowClick (data) {
      let idList = []
      for (let item of this.memberData) {
        item.fullNameId && idList.push(item.fullNameId)
      }
      for (let item of data) {
        if (!idList.includes(item.fullNameId)) {
          this.memberData.push({ ...item, chair: false, reviewer: false, enableMeetingInvitee: true, enableMinutesSender: true, inCharge: null })
        }
      }
      this.commonUserShow = false
    },

    changeTime (val) {
      if (val) {
        if (this.form.meetingStartTime && this.form.meetingEndTime) {
          let startTime = new Date(this.form.meetingStartTime).getTime()
          let endTime = new Date(this.form.meetingEndTime).getTime()
          this.form.meetingHour = Math.round((endTime - startTime) / 1000 / 60) + '分钟'
        }
      }
    },

    getManyTodoObj (selections) { // 多选待办
      let rows = selections
      if (!Array.isArray(selections)) {
        rows = [selections]
      }
      let hasSelectIds = []
      for (let item of this.todoData) {
        item.meetTodoId && hasSelectIds.push(item.meetTodoId)
      }
      for (let item of rows) {
        if (!hasSelectIds.includes(item.meetTodoId)) {
          this.todoData.push(item)
        }
      }
    },

    reviewerChange (row, val) {
      if (val) {
        row.enableMeetingInvitee = true
        row.enableMinutesSender = true
      }
    },

    // 待办事项点击当前行
    rowClick (row) {
      this.curIndex = row.index
      if (row && row.topicId) {
        this.getTopicFormDetail(row.topicId).then(result => {
          let { meetTopicMemberList } = result
          this.subjectMemberData = meetTopicMemberList
        })
      }
    },

    rowClassName ({ row, rowIndex }) {
      row.index = rowIndex
      if (rowIndex === this.curIndex) return 'hover-row'
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

    invSelectHandler (node) {
      this.form.invId = node ? node.organizationId : null
      this.form.invCode = node ? node.organizationCode : null
      this.form.invName = node ? node.organizationName : null
      this.todoData = []
      this.subjectData = []
      this.subjectMemberData = []
    },

    getTopicObj (val, scope) { // 议题单选
      let { $index } = scope
      this.subjectData.splice($index, 1, val)
      if (!val.topicId) return
      this.getTopicFormDetail(val.topicId).then(data => {
        let { meetTopicMemberList } = data
        for (let item of meetTopicMemberList) {
          this.memberData.push({
            ...item,
            topicId: val.topicId,
            topicCode: val.topicCode,
            topicName: val.topicName,
            _disabled: true // 议题带过来的人员标识
          })
        }
      })
    },

    getManyTopicObj (selections) {
      let rows = selections
      if (!Array.isArray(selections)) {
        rows = [selections]
      }
      let hasSelectIds = []
      for (let item of this.subjectData) {
        item.topicId && hasSelectIds.push(item.topicId)
      }
      for (let item of rows) {
        if (!hasSelectIds.includes(item.topicId)) {
          this.subjectData.push(item)
        }
      }
      if (this.subjectData.length) {
        let topicId = this.subjectData[0].topicId
        if (topicId) {
          this.getTopicFormDetail(topicId).then(result => {
            let { meetTopicMemberList } = result
            this.subjectMemberData = meetTopicMemberList
          })
        }
      }
    },

    // 跳转关联单号
    async goToRelation (scope) {
      let { row } = scope
      if (!row.relationBillId) return
      let tab
      if (row.topicType === 'SS') {
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

    checkChange (val, scope) {
      if (val) {
        for (let item of this.memberData) {
          this.$set(item, 'inCharge', false)
        }
        this.$set(scope.row, 'inCharge', true)
      }
    },

    deleteMeetTodo (scope) {
      this.todoData.splice(scope.$index, 1)
    },

    deleteMeetSubject (scope) {
      this.subjectData.splice(scope.$index, 1)
      let { topicId } = scope.row
      if (topicId) {
        this.memberData = this.memberData.filter(item => item.topicId !== topicId)
      }
      if (!this.subjectData.length) {
        this.subjectMemberData = []
      } else {
        if (this.curIndex === scope.$index) { // 删除的是当前行
          let curId = this.subjectData[0].topicId
          if (!curId) return
          this.getTopicFormDetail(curId).then(result => {
            let { meetTopicMemberList } = result
            this.subjectMemberData = meetTopicMemberList
          })
        }
      }
    },

    getUserObj (val, scope) {
      scope.fullNameId = val.userId
      scope.fullName = val.nickname
      scope.deptName = val.department
      scope.mobileNo = val.phone
      scope.email = val.email
      scope.inCharge = false
      scope.chair = false
      scope.reviewer = false
    },

    getMeetObj (val, scope) {
      let attrs = ['topicModelId', 'topicModelCode', 'topicModelName', 'topicType']
      for (let key of attrs) {
        scope[key] = val ? val[key] : null
      }
      if (!val || !val.topicModelId) {
        return
      }
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
      }).then(res => {
        let { meetingMemberList = [], meetTopicList = [], meetTodoList = [], ...rest } = res.data || {}
        Object.assign(this.form, rest)
        this.todoData = meetTodoList
        this.subjectData = meetTopicList
        this.memberData = meetingMemberList
        if (this.subjectData.length) {
          let topicId = this.subjectData[0].topicId
          if (topicId) {
            this.getTopicFormDetail(topicId).then(result => {
              let { meetTopicMemberList } = result
              this.subjectMemberData = meetTopicMemberList
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

    addMeetPerson () {
      this.memberData.push({
        fullName: null,
        deptName: null,
        mobileNo: null,
        email: null,
        inCharge: false,
        chair: false,
        reviewer: false,
        enableMeetingInvitee: true,
        enableMinutesSender: true
      })
    },

    initParams () {
      let params = {}
      for (let key in this.form) {
        params[key] = this.form[key]
      }
      params.meetingMemberList = this.memberData // 会议成员
      params.meetTopicList = this.subjectData // 会议议题
      params.meetTodoList = this.todoData // 待办事项
      return params
    },

    deleteMember (scope) {
      this.memberData.splice(scope.$index, 1)
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
</style>
