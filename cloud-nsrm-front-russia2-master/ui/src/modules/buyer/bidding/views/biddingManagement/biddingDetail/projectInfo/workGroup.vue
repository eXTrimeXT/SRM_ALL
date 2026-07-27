<template>
  <div class="work-group">
    <p style="margin: 0; padding: 10px 0">
      <!--手工添加-->
      <el-button type="primary" @click="addRow">
        {{ $t('bidMod.addByHand') }}
      </el-button>

      <!-- 从专家库选择 -->
      <el-button type="primary" @click="importExpertList">
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
        type="index"
        :label="$t('common.sort')"
        width="50"
      />

      <!--成员账号-->
      <el-table-column
        prop="userName"
        :label="$t('bidMod.userName')"
        width="150"
      >
        <template v-slot="scope">
          <QuickSearch
            :show-input="scope.row.userName"
            show-key="username"
            :scope-data="scope.row"
            name="scc_rbac_user_display"
            :disabled="readonly"
            @close-quicksearch="getUserObj"
          />
        </template>
      </el-table-column>

      <!--成员姓名-->
      <el-table-column
        prop="fullName"
        :label="$t('bidMod.fullName')"
        width="150"
        show-overflow-tooltip
      />

      <!--电话-->
      <el-table-column
        prop="phone"
        :label="$t('bidMod.phone')"
        width="150"
        show-overflow-tooltip
      />

      <!--电子邮箱-->
      <el-table-column
        prop="email"
        :label="$t('bidMod.email')"
        min-width="200"
        show-overflow-tooltip
      />

      <!-- 岗位 -->
      <el-table-column
        prop="position"
        :label="$t('bidMod.position')"
        width="100"
        show-overflow-tooltip
      />

      <!--操作权限-->
      <el-table-column
        prop="operateAuth"
        :label="$t('bidMod.operateAuth')"
        width="150"
      >
        <template v-slot="{ row, column }">
          <DictSelect
            v-model="row[column.property]"
            code="SOU_GROUP_OPERATE_AUTH"
            :disabled="readonly"
          />
        </template>
      </el-table-column>

      <!--评分权限-->
      <el-table-column
        prop="scoreAuth"
        :label="$t('bidMod.scoreAuth')"
        width="150"
      >
        <template v-slot="{ row, column }">
          <DictSelect
            v-model="row[column.property]"
            code="SCC_SOU_SCORE_DIMENSION_CODE"
            :disabled="readonly"
          />
        </template>
      </el-table-column>

      <!--更新人-->
      <el-table-column
        prop="lastUpdatedUserName"
        :label="$t('bidMod.lastUpdatedBy')"
        width="100"
        show-overflow-tooltip
      />

      <!-- 操作 -->
      <el-table-column
        fixed="right"
        :label="$t('common.operation')"
        width="90"
      >
        <template v-slot="{ $index }">
          <el-button type="text" @click="deleteRow($index)">
            {{ $t('common.delete') }}
          </el-button>
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
import { SOU_BRG_TYPE_ENUM } from 'lib@/composition/biddingLts/utils'
import QuickSearch from 'lib@/components/QuickSearch'
import ExpertDatabaseDialog from 'lib@/composition/origin/expertDatabase/expertDatabaseDialog'

export default {
  name: 'WorkGroup',

  components: {
    QuickSearch,
    ExpertDatabaseDialog
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
      groupList: [],
      expertDatabaseDialogVisible: false,
      SOU_BRG_TYPE_ENUM
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
    addRow () {
      this.groupList.push({
        userName: '',
        fullName: '',
        phone: '',
        email: '',
        position: '',
        operateAuth: '',
        scoreAuth: ''
      })
    },

    /* 选择一个成员账号 */
    getUserObj (val, scope) {
      const {
        username = '',
        nickname = '',
        phone = '',
        email = '',
        department = ''
      } = val || {}
      scope.userName = username
      scope.fullName = nickname
      scope.phone = phone
      scope.email = email
      scope.position = department
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
