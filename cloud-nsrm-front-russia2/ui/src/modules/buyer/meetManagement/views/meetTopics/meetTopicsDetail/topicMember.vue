<template>
  <div class="topic-member-wrap">
    <!--新增-->
    <el-button
      type="primary"
      :disabled="readonly"
      @click="addMeetPerson"
    >
      {{ $t('common.add') }}
    </el-button>

    <!--选择常用组-->
    <el-button
      type="primary"
      :disabled="readonly"
      @click="chooseMeetPerson"
    >
      {{ $t('meeting.selectCommonGroup') }}
    </el-button>

    <!--保存常用组-->
    <el-button
      type="primary"
      :disabled="readonly"
      @click="saveMeetPerson"
    >
      {{ $t('meeting.saveCommonGroup') }}
    </el-button>

    <el-table
      :data="memberData"
      border
      stripe
      style="margin-top: 10px"
    >
      <!--姓名-->
      <el-table-column prop="fullName">
        <template slot="header">
          <em class="toRequired">*</em>
          {{ $t('meeting.fullName') }}
        </template>

        <template v-slot="scope">
          <QuickSearch
            :show-input="scope.row.fullName"
            :disabled="readonly || scope.row.fromRfq"
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

      <!--角色-->
      <el-table-column>
        <template slot="header">
          <em class="toRequired">*</em>
          {{ $t('meeting.role') }}
        </template>
        <template v-slot="scope">
          <DictSelect
            v-model="scope.row.topicRole"
            code="MEET_ROLE"
            :disabled="readonly || scope.row.fromRfq"
          />
        </template>
      </el-table-column>

      <!--手机号-->
      <el-table-column prop="mobileNo" :label="$t('meeting.mobileNo')">
        <template v-slot="scope">
          <el-input v-model="scope.row.mobileNo" disabled />
        </template>
      </el-table-column>

      <!--邮箱-->
      <el-table-column prop="email" :label="$t('common.email')">
        <template v-slot="scope">
          <el-input v-model="scope.row.email" disabled />
        </template>
      </el-table-column>

      <!--负责人-->
      <el-table-column prop="inCharge" :label="$t('meeting.inCharge')">
        <template v-slot="scope">
          <el-checkbox
            v-model="scope.row.inCharge"
            :disabled="readonly"
            @change="val => checkChange(val,scope)"
          />
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

      <el-table-column :label="$t('common.operation')">
        <template v-slot="{ $index }">
          <!--删除-->
          <el-button
            type="text"
            :disabled="readonly"
            @click="deleteMember($index)"
          >
            {{ $t('common.delete') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <CommonUser
      :show.sync="commonUserShow"
      group-type="topic"
      @row-click="commonUserRowClick"
    />
  </div>
</template>

<script>
/**
 * 议题成员
 */
import CommonUser from '../../components/commonUser'
import QuickSearch from 'lib@/components/QuickSearch'

export default {
  name: 'MeetTopicsDetailTopicMember',

  components: {
    QuickSearch,
    CommonUser
  },

  props: {
    readonly: {
      type: Boolean,
      default: false
    },
    detailData: {
      type: Array,
      default: () => []
    }
  },

  data () {
    return {
      memberData: [],
      commonUserShow: false
    }
  },

  watch: {
    detailData: {
      handler (val) {
        if (val && Array.isArray(val)) {
          this.memberData = val.concat()
        }
      },
      immediate: true,
      deep: true
    }
  },

  methods: {
    addMeetPerson () {
      this.memberData.push({
        fullName: null,
        deptName: null,
        mobileNo: null,
        email: null,
        inCharge: false,
        topicRole: null,
        enableMinutesSender: true,
        enableMeetingInvitee: true
      })
    },

    chooseMeetPerson () {
      this.commonUserShow = true
    },

    commonUserRowClick (data) {
      let idList = []
      for (let item of this.memberData) {
        item.fullNameId && idList.push(item.fullNameId)
      }
      for (let item of data) {
        if (!idList.includes(item.fullNameId)) {
          this.memberData.push({ ...item, enableMeetingInvitee: true, enableMinutesSender: true, inCharge: null })
        }
      }
      this.commonUserShow = false
    },

    deleteMember ($index) {
      this.memberData.splice($index, 1)
    },

    checkChange (val, scope) {
      if (val) {
        for (let item of this.memberData) {
          this.$set(item, 'inCharge', false)
        }
        this.$set(scope.row, 'inCharge', true)
      }
    },

    saveMeetPerson () {
      let filterMemberList = this.memberData.filter(item => item.fullNameId)
      if (!filterMemberList.length) {
        this.$message.warning(this.$t('meeting.rule.memberDataNotEmpty'))
        return
      }
      this.saveVisible = true
    },

    getUserObj (val, scope) {
      scope.fullNameId = val.userId
      scope.fullName = val.nickname
      scope.deptName = val.department
      scope.mobileNo = val.phone
      scope.email = val.email
    }
  }
}
</script>
