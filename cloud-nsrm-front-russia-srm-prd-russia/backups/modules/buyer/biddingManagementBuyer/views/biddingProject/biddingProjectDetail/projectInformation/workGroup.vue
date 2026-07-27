<template>
  <div class="work-group">
    <p style="margin: 0; padding: 10px 0">
      <!--手工添加-->
      <el-button
        type="primary"
        class="detail-pbtn"
        @click="addNewOne"
      >
        {{ $t('bidMod.addByHand') }}
      </el-button>

      <!-- 从专家库选择 -->
      <el-button
        type="primary"
        class="detail-pbtn"
        @click="importExpertList"
      >
        {{ $t('bid_mod.expertDatabaseTitle') }}
      </el-button>
    </p>

    <el-table
      :data="groupList"
      style="width: 100%"
      border
      max-height="250px"
    >
      <el-table-column
        align="center"
        type="index"
        width="50"
      />

      <!--成员账号-->
      <el-table-column
        align="center"
        prop="userName"
        :label="$t('bidMod.userName')"
        width="150"
      >
        <template slot-scope="scope">
          <quick-search
            :show-input="scope.row.userName"
            show-key="username"
            :scope-data="scope.row"
            name="scc_rbac_user_display"
            @close-quicksearch="getUserObj"
          />
        </template>
      </el-table-column>

      <!--成员姓名-->
      <el-table-column
        align="center"
        prop="fullName"
        :label="$t('bidMod.fullName')"
        width="150"
        show-overflow-tooltip
      />

      <!--电话-->
      <el-table-column
        align="center"
        prop="phone"
        :label="$t('bidMod.phone')"
        width="150"
        show-overflow-tooltip
      />

      <!--电子邮箱-->
      <el-table-column
        align="center"
        prop="email"
        :label="$t('bidMod.email')"
        min-width="200"
        show-overflow-tooltip
      />

      <!-- 岗位 -->
      <el-table-column
        align="center"
        prop="expertJobName"
        :label="$t('bidMod.position')"
        width="100"
        show-overflow-tooltip
      />

      <!--技术评委-->
      <el-table-column
        align="center"
        prop="judgeFlag"
        :label="$t('bidMod.judgeFlag')"
        width="75"
        show-overflow-tooltip
      >
        <template slot-scope="scope">
          <el-checkbox
            v-model="scope.row.judgeFlag"
            true-label="Y"
            false-label="N"
            :disabled="!bidingBase.bidingType || bidingBase.bidingType === 'BUSINESS'"
          />
        </template>
      </el-table-column>

      <!--解密权限-->
      <el-table-column
        align="center"
        prop="canDecrypt"
        :label="$t('bidMod.biddingManagementBuyer.canDecrypt')"
        width="85"
        show-overflow-tooltip
      >
        <template slot-scope="scope">
          <!--只能勾选一个-->
          <el-checkbox
            v-model="scope.row.canDecrypt"
            true-label="Y"
            false-label="N"
            :disabled="scope.row.canDecrypt !== 'Y' && groupLeaderCount === 1"
          />
        </template>
      </el-table-column>

      <!--更新人-->
      <el-table-column
        align="center"
        prop="lastUpdatedUserName"
        :label="$t('bidMod.lastUpdatedBy')"
        width="100"
        show-overflow-tooltip
      />

      <!-- 操作 -->
      <el-table-column
        align="center"
        fixed="right"
        :label="$t('common.operation')"
        width="90"
      >
        <template slot-scope="scope">
          <el-button
            type="primary"
            icon="el-icon-delete"
            class="el-button-icon"
            @click="handleDelClick(scope.$index)"
          />
        </template>
      </el-table-column>
    </el-table>

    <!-- 从专家库选择工作小组成员 -->
    <ExpertDatabaseDialog
      v-if="expertDatabaseDialogVisible"
      :visible.sync="expertDatabaseDialogVisible"
      :group-list="groupList"
      @save="saveExpertToGroupList"
    />
  </div>
</template>

<script>
/**
 * 工作小组
 */
import QuickSearch from 'lib@/components/QuickSearch'
import ExpertDatabaseDialog from 'lib@/composition/origin/expertDatabase/expertDatabaseDialog'

export default {
  name: 'WorkGroup',

  components: {
    QuickSearch,
    ExpertDatabaseDialog
  },

  props: {
    bidingBase: {
      type: Object,
      required: true
    },
    detailData: {
      type: Array,
      default: () => []
    }
  },

  data () {
    return {
      groupList: [],
      expertDatabaseDialogVisible: false
    }
  },

  computed: {
    groupLeaderCount () {
      let count = 0
      this.groupList.forEach(item => {
        if (item.canDecrypt === 'Y') {
          count++
        }
      })
      return count
    }
  },

  watch: {
    detailData: {
      handler (val) {
        this.groupList = val || []
      },
      immediate: true,
      deep: true
    }
  },

  methods: {
    /* 手工添加 */
    addNewOne () {
      this.groupList.push({
        userName: '',
        fullName: '',
        phone: '',
        email: '',
        position: '',
        judgeFlag: 'N',
        maxEvaluateScore: 100,
        confirmedFlag: 'Y',
        confirmeDatetime: new Date(Date.now())
      })
    },

    /* 选择一个成员账号 */
    getUserObj (val, scope) {
      scope.userName = val ? val.username : ''
      scope.fullName = val ? val.nickname : ''
      scope.phone = val ? val.phone : ''
      scope.email = val ? val.email : ''
      scope.position = val ? val.department : ''
    },

    /* 工作小组从专家库选择 */
    importExpertList () {
      this.expertDatabaseDialogVisible = true
    },

    /* 保存专家库选择的成员 */
    saveExpertToGroupList (list) {
      this.groupList = [...this.groupList, ...(list.map(item => {
        return {
          ...item,
          // 技术专家默认不钩上
          judgeFlag: 'N'
        }
      }))]
    },

    /* 删除一个专家 */
    handleDelClick (index) {
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
