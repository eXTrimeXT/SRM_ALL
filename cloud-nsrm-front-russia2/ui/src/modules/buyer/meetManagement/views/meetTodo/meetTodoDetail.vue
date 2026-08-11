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
              <!--待办内容-->
              <srm-col :init-col="1">
                <el-form-item prop="todoContent" :label="$t('meeting.todoContent')">
                  <el-input
                    v-model="form.todoContent"
                    type="textarea"
                    :rows="3"
                    :disabled="followFlag"
                  />
                </el-form-item>
              </srm-col>

              <!--待办编号-->
              <srm-col :init-col="4">
                <el-form-item prop="meetTodoCode" :label="$t('meeting.meetTodoCode')">
                  <el-input v-model="form.meetTodoCode" disabled />
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
                    :disabled="followFlag"
                    @select="invSelectHandler"
                  />
                </el-form-item>
              </srm-col>

              <!--议题类型-->
              <srm-col :init-col="4">
                <el-form-item prop="topicType" :label="$t('meeting.todoType')">
                  <DictSelect
                    v-model="form.topicType"
                    clearable
                    code="MEET_TYPE"
                    :disabled="followFlag"
                    @change="topicTypeChange"
                  />
                </el-form-item>
              </srm-col>

              <!--议题编号-->
              <srm-col :init-col="4">
                <el-form-item
                  prop="topicCode"
                  :label="$t('meeting.topicCode')"
                  :rules="[{required:form.topicType !== 'OTHER',message:$t('common.pleaseSelect')}]"
                >
                  <QuickSearch
                    :show-input="form.topicCode"
                    :disabled="followFlag"
                    show-key="topicCode"
                    :scope-data="form"
                    auto-query
                    name="lts_scc_meet_topic"
                    :pre-query-data="{'t.TOPIC_TYPE':form.topicType}"
                    @close-quicksearch="getTopicObj"
                  />
                </el-form-item>
              </srm-col>

              <!--议题名称-->
              <srm-col :init-col="4">
                <el-form-item prop="topicName" :label="$t('meeting.topicName')">
                  <el-input v-model="form.topicName" disabled />
                </el-form-item>
              </srm-col>

              <!--状态-->
              <srm-col :init-col="4">
                <el-form-item prop="todoStatus" :label="$t('common.status')">
                  <DictSelect
                    v-model="form.todoStatus"
                    code="MEET_TODO_STATUS"
                    disabled
                  />
                </el-form-item>
              </srm-col>

              <!--跟踪人-->
              <srm-col :init-col="4">
                <el-form-item prop="todoStalkerName" :label="$t('meeting.todoStalkerName')">
                  <QuickSearch
                    :show-input="form.todoStalkerName"
                    :disabled="followFlag"
                    show-key="username"
                    :scope-data="form"
                    auto-query
                    name="scc_rbac_user_display"
                    @close-quicksearch="getFollowUserObj"
                  />
                </el-form-item>
              </srm-col>

              <!--负责人-->
              <srm-col :init-col="4">
                <el-form-item prop="todoDirectorName" :label="$t('meeting.todoDirectorName')">
                  <QuickSearch
                    :show-input="form.todoDirectorName"
                    :disabled="followFlag"
                    show-key="username"
                    :scope-data="form"
                    auto-query
                    name="scc_rbac_user_display"
                    @close-quicksearch="getDirectorUserObj"
                  />
                </el-form-item>
              </srm-col>

              <!--预计完成时间-->
              <srm-col :init-col="4">
                <el-form-item :label="$t('meeting.todoExpectFinishTime')" prop="todoExpectFinishTime">
                  <el-date-picker
                    v-model="form.todoExpectFinishTime"
                    type="datetime"
                    :format="$formatDatePickerTime"
                    value-format="yyyy-MM-dd HH:mm:ss"
                    :disabled="followFlag"
                  />
                </el-form-item>
              </srm-col>

              <!--提醒时间-->
              <srm-col :init-col="4">
                <el-form-item :label="$t('meeting.todoReminderTime')" prop="todoReminderTime">
                  <el-date-picker
                    v-model="form.todoReminderTime"
                    type="datetime"
                    :format="$formatDatePickerTime"
                    value-format="yyyy-MM-dd HH:mm:ss"
                    :disabled="followFlag"
                  />
                </el-form-item>
              </srm-col>

              <!--预警状态-->
              <srm-col :init-col="4">
                <el-form-item prop="earlyWarningStatus" :label="$t('meeting.warningStatus')">
                  <DictSelect
                    v-model="form.earlyWarningStatus"
                    :store="store"
                    code="MEET_WARNING_STATUS"
                    disabled
                  />
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

              <!--跟踪说明-->
              <srm-col v-if="followFlag || form.todoStatus === 'COMPLETED'" :init-col="1">
                <el-form-item prop="todoStalkerRemark" :label="$t('meeting.todoStalkerRemark')">
                  <el-input v-model="form.todoStalkerRemark" type="textarea" :rows="3" />
                </el-form-item>
              </srm-col>
            </srm-row>
          </el-form>
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
        <el-button v-if="editFlag" type="primary" @click="saveBill('STAGING')">
          {{ $t('common.staging') }}
        </el-button>

        <!--提交-->
        <el-button v-if="editFlag" type="primary" @click="saveBill('SAVE')">
          {{ $t('common.submit') }}
        </el-button>

        <!--提交-->
        <el-button v-if="followFlag" type="primary" @click="saveBill('CLOSE')">
          {{ $t('common.submit') }}
        </el-button>
      </template>
    </CToolbar>
  </el-container>
</template>

<script>
import { tabTodoMixin } from '@/utils/mixins'
import CToolbar from 'lib@/components/c-toolbar'
import QuickSearch from 'lib@/components/QuickSearch'
import OrganizationSelector from 'lib@/components/organization-selector'

export default {
  name: 'MeetModelDetail',

  components: {
    CToolbar,
    QuickSearch,
    OrganizationSelector
  },

  mixins: [tabTodoMixin],

  data () {
    return {
      activeDims: ['1', '2', '3'],
      form: {
        todoContent: '',
        meetTodoCode: '',
        invId: '',
        invCode: '',
        invName: '',
        topicType: '',
        topicId: '',
        topicCode: '',
        topicName: '',
        todoStatus: '',
        todoDirectorName: '',
        todoDirectorId: '',
        department: '',
        todoStalkerName: '',
        todoStalkerId: '',
        todoExpectFinishTime: '',
        todoReminderTime: '',
        earlyWarningStatus: '',
        createdFullName: '',
        creationDate: '',
        lastUpdatedFullName: '',
        lastUpdateDate: '',
        todoStalkerRemark: ''
      },
      rules: {
        todoContent: [{ required: true, message: this.$t('common.pleaseInput') }],
        invId: [{ required: true, message: this.$t('common.pleaseSelect') }],
        topicType: [{ required: true, message: this.$t('common.pleaseSelect') }],
        todoDirectorName: [{ required: true, message: this.$t('common.pleaseSelect') }],
        todoStalkerName: [{ required: true, message: this.$t('common.pleaseSelect') }],
        todoExpectFinishTime: [{ required: true, message: this.$t('common.pleaseSelect') }],
        todoReminderTime: [{ required: true, message: this.$t('common.pleaseSelect') }],
        todoStalkerRemark: [{ required: true, message: this.$t('common.pleaseInput') }]
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
    editFlag () {
      return ['add', 'edit'].includes(this.urlParams.flag)
    },
    followFlag () {
      return this.urlParams.flag === 'follow'
    }
  },

  created () {
    let { row, flag } = this.urlParams
    if (row && row.meetTodoId) {
      this.getFormDetail(row.meetTodoId)
    }
    if (flag === 'add') {
      this.form.topicType = 'OTHER'
    }
  },

  methods: {
    saveBill (type) {
      let params = this.initParams()
      let url = type === 'SAVE' ? '/api-inq/inq/meetTodo/submit' : (type === 'CLOSE' ? '/api-inq/inq/meetTodo/track/submit' : '/api-inq/inq/meetTodo/manuscript')

      let validFlag
      this.$refs.form.validate(valid => (validFlag = valid))

      if (type !== 'STAGING' && !validFlag) {
        this.__focus_error__()
      }
      this.$http({
        url,
        method: 'POST',
        data: params,
        loading: true
      }).then(res => {
        this.$message.success(res.message)
        if (type === 'STAGING') { // 暂存
          if (!res.data) this.back()
          this.getFormDetail(res.data)
        } else {
          this.back()
        }
      })
    },

    topicTypeChange () {
      this.form.topicCode = ''
      this.form.topicName = ''
    },

    invSelectHandler (node) {
      this.form.invId = node ? node.organizationId : null
      this.form.invCode = node ? node.organizationCode : null
      this.form.invName = node ? node.organizationName : null
    },

    getDirectorUserObj (val, scope) {
      scope.todoDirectorId = val.userId
      scope.todoDirectorName = val.nickname
      scope.department = val.department
    },

    getFollowUserObj (val, scope) {
      scope.todoStalkerId = val.userId
      scope.todoStalkerName = val.nickname
    },

    getTopicObj (val, form) {
      let attrs = ['topicCode', 'topicName', 'topicType', 'topicId']
      for (let key of attrs) {
        form[key] = val ? val[key] : null
      }
    },

    getFormDetail (meetTodoId) {
      this.$http({
        url: '/api-inq/inq/meetTodo/get',
        method: 'GET',
        params: { meetTodoId },
        loading: true
      }).then(res => {
        this.form = res.data || {}
      })
    },

    back () {
      let { tabName } = this.$attrs.params
      this.$emit('tab-remove', tabName)
      this.__setTabTodo('MeetTodoList.getQueryData')
    },

    initParams () {
      let params = {}
      for (let key in this.form) {
        params[key] = this.form[key]
      }
      return params
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
