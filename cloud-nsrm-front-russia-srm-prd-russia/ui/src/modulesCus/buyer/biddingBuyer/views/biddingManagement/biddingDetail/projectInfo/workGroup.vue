<template>
  <div class="work-group">
    <el-button
      v-if="biddingBase.mergeFlag"
      style="margin-bottom: 10px;"
      type="primary"
      @click="addRow"
    >
      {{ $t('common.add') }}
    </el-button>

    <el-table
      ref="groupList"
      :data="groupList"
      style="width: 100%"
      max-height="260"
      border
    >
      <el-table-column
        align="center"
        type="index"
        fixed="left"
        :label="$t('common.sort')"
        width="50"
      />
      <el-table-column
        v-show="biddingBase.mergeFlag"
        align="center"
        prop="extPackageName"
        :label="$t('cusEntry.biddingSettings.bagName')"
        min-width="100"
      />
      <!--工号-->
      <!-- 招标负责人不可修改，招标部长可修改, 非合并招标时其他人不允许修改，合并招标时可修改 -->
      <el-table-column
        align="center"
        prop="userName"
        :label="$t('purchaseDemand.erpNum')"
        :render-header="_addStarToColumn"
        min-width="120"
      >
        <template v-slot="scope">
          <QuickSearch
            v-if="scope.row.groupRole=='MINISTER'"
            :show-input="scope.row.userName"
            show-key="username"
            :scope-data="scope"
            :disabled="readonly"
            name="scc_rbac_user_display"
            @close-quicksearch="getUserObj"
          />
          <QuickSearch
            v-else
            :show-input="scope.row.userName"
            show-key="username"
            :scope-data="scope"
            :disabled="readonly || scope.row.groupRole=='PRINCIPAL' || !biddingBase.mergeFlag"
            :pre-query-data="{'t.project_id': biddingBase.projectId}"
            name="ext_sou_project_group"
            @close-quicksearch="getUserObj"
          />
        </template>
      </el-table-column>
      <!--成员姓名-->
      <el-table-column
        align="center"
        prop="fullName"
        :label="$t('bidMod.fullName')"
        min-width="120"
        show-overflow-tooltip
      />
      <!--电话-->
      <el-table-column
        align="center"
        prop="phone"
        :label="$t('bidMod.phone')"
        min-width="120"
        show-overflow-tooltip
      />
      <!--电子邮箱-->
      <el-table-column
        align="center"
        prop="email"
        :label="$t('bidMod.email')"
        min-width="120"
        show-overflow-tooltip
      />
      <!-- 岗位 -->
      <el-table-column
        align="center"
        prop="position"
        :label="$t('bidMod.position')"
        min-width="120"
        show-overflow-tooltip
      />
      <!-- 角色 -->
      <el-table-column
        align="center"
        prop="groupRole"
        :label="$t('meeting.role')"
        min-width="120"
        show-overflow-tooltip
      >
        <template v-slot="scope">
          <DictSelect
            v-model="scope.row.groupRole"
            code="SOU_GROUP_ROLE"
            disabled
          />
        </template>
      </el-table-column>
      <!--操作权限-->
      <!-- 评标组长默认技术开标，评标组员无操作权限 -->
      <el-table-column
        align="center"
        prop="operateAuth"
        :label="$t('cusEntry.bidMod.operateAuth')"
        min-width="120"
      >
        <template v-slot="{ row, column }">
          <DictSelect
            v-model="row[column.property]"
            code="SOU_GROUP_OPERATE_AUTH"
            disabled
          />
        </template>
      </el-table-column>
      <!--专家等级-->
      <el-table-column
        align="center"
        prop="extExpertLevel"
        :label="$t('cusEntry.bidMod.expertLevel')"
        min-width="120"
      >
        <template v-slot="{ row, column }">
          <DictSelect
            v-model="row[column.property]"
            code="SOU_BID_EXPERT_LEVEL"
            disabled
          />
        </template>
      </el-table-column>
      <!--评分权限-->
      <el-table-column
        align="center"
        prop="scoreAuth"
        :label="$t('cusEntry.bidMod.scoreAuth')"
        min-width="120"
      >
        <template v-slot="{ row, column }">
          <DictSelect
            v-model="row[column.property]"
            code="SCC_SOU_SCORE_DIMENSION_CODE"
            disabled
          />
        </template>
      </el-table-column>
      <!-- 操作 -->
      <el-table-column
        v-if="biddingBase.mergeFlag"
        align="center"
        fixed="right"
        :label="$t('common.operation')"
        width="90"
      >
        <template v-slot="scope">
          <!-- 招标负责人、招标部长不展示删除按钮 -->
          <el-button
            v-if="!['PRINCIPAL', 'MINISTER'].includes(scope.row.groupRole)"
            type="text"
            @click="deleteRow(scope.$index)"
          >
            {{ $t('common.delete') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
/**
 * 工作小组
 */
import QuickSearch from 'lib@/components/QuickSearch'

export default {
  name: 'WorkGroup',

  components: {
    QuickSearch
  },

  props: {
    biddingBase: {
      type: Object,
      required: true
    },
    detailData: {
      type: Array,
      default: () => []
    },
    readonly: {
      type: Boolean,
      default: false
    }
  },

  data () {
    return {
      groupList: []
    }
  },

  watch: {
    detailData: {
      handler (val) {
        this.groupList = val || []
        this.$nextTick(() => {
          this.$refs.groupList.doLayout()
        })
      },
      immediate: true,
      deep: true
    }
  },

  methods: {
    /* 手工添加 */
    addRow () {
      this.groupList.push({
        extPackageName: '',
        userId: null,
        userName: '',
        fullName: '',
        phone: '',
        email: '',
        position: '',
        groupRole: '',
        operateAuth: '',
        extExpertLevel: '',
        scoreAuth: 'SOU_TECH'
      })
    },

    /* 选择一个成员账号 */
    getUserObj (val, scope) {
      // 招标部长用公共人员表，取工号 姓名 邮箱 电话 岗位
      if (scope.row.groupRole == 'MINISTER') {
        scope.row.userId = val ? val.userId : ''
      } else {
        // 招标组长和组员用招标资料人员表，取工号 姓名 邮箱 电话 岗位 角色 操作权限 专家等级
        scope.row.userId = val ? val.ceeaEmpNo : ''
        scope.row.groupRole = val ? val.groupRole : ''
        scope.row.operateAuth = val && val.groupRole == 'LEADER' ? 'SOU_TECH_OPEN' : ''
        scope.row.extExpertLevel = val ? val.extExpertLevel : ''
      }
      scope.row.userName = val ? val.username : ''
      scope.row.fullName = val ? val.nickname : ''
      scope.row.phone = val ? val.phone : ''
      scope.row.email = val ? val.email : ''
      scope.row.position = val ? val.ceeaJobcodeDescr : ''
    },

    /* 工作小组从专家库选择 */
    importExpertList () {
      this.expertDatabaseDialogVisible = true
    },

    /* 保存专家库选择的成员 */
    saveExpertToGroupList (list) {
      this.groupList = this.groupList.concat(list)
    },

    /* 删除一个专家 */
    deleteRow (index) {
      this.groupList.splice(index, 1)
    },

    /* 返回当前数据 父组件外部调用 */
    getParamsData () {
      return this.groupList
    },

    /* 清除数据 */
    clearData () {
      this.groupList = []
    }
  }
}
</script>
