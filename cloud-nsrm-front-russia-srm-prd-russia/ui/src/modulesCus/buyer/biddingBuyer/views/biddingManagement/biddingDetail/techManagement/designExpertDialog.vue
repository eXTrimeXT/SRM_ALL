<template>
  <SrmDialog
    title="指定专家"
    size="large"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
    append-to-body
  >
    <el-button
      type="primary"
      style="margin-bottom: 12px"
      @click="addRow"
    >
      {{ $t('common.add') }}
    </el-button>
    <el-table
      border
      height="180"
      style="width: 100%"
      :data="evaGroupList"
      highlight-current-row
    >
      <el-table-column
        align="center"
        type="index"
        fixed="left"
        :label="$t('common.sort')"
        width="55"
      />
      <el-table-column
        align="center"
        prop="userName"
        :label="$t('dataConfMod.jobNum')"
        min-width="120"
        :render-header="_addStarToColumn"
      >
        <template slot-scope="scope">
          <QuickSearch
            :show-input="scope.row.userName"
            show-key="username"
            :scope-data="scope"
            name="sou_npm_expert_user_display"
            @close-quicksearch="getUserObj"
          />
        </template>
      </el-table-column>
      <el-table-column
        align="center"
        prop="fullName"
        :label="$t('bidMod.fullName')"
        min-width="120"
      />
      <el-table-column
        align="center"
        prop="groupRole"
        :label="$t('meeting.role')"
        min-width="120"
        :formatter="(row, column, cellValue) => $getDictLabel('SOU_GROUP_ROLE', cellValue)"
      />
      <!-- 专家等级 -->
      <el-table-column
        align="center"
        prop="extExpertLevel"
        :label="$t('cusEntry.bidMod.expertLevel')"
        min-width="120"
        :formatter="(row, column, cellValue) => $getDictLabel('SOU_BID_EXPERT_LEVEL', cellValue)"
      />
      <!-- 评分权限 -->
      <el-table-column
        align="center"
        prop="scoreAuth"
        :label="$t('cusEntry.bidMod.scoreAuth')"
        min-width="120"
        :formatter="(row, column, cellValue) => $getDictLabel('SCC_SOU_SCORE_DIMENSION_CODE', cellValue)"
      />
      <el-table-column
        align="center"
        fixed="right"
        :label="$t('common.operation')"
        width="80"
      >
        <template slot-scope="scope">
          <el-button
            type="text"
            @click="deleteRow(scope.$index)"
          >
            {{ $t('common.delete') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <div slot="footer" class="dialog-footer">
      <el-button @click="dialogVisible = false">
        {{ $t('common.close') }}
      </el-button>
      <el-button type="primary" @click="confirm">
        {{ $t('common.confirm') }}
      </el-button>
    </div>
  </SrmDialog>
</template>

<script>
/**
 * 指定专家
 */
import QuickSearch from 'lib@/components/QuickSearch'
export default {
  name: 'DesignExpertDialog',

  components: {
    QuickSearch
  },

  props: {
    visible: {
      type: Boolean,
      required: true
    },
    projectId: {
      type: [Number, String],
      required: true
    }
  },

  data () {
    return {
      evaGroupList: []
    }
  },

  computed: {
    dialogVisible: {
      get: function () {
        return this.visible
      },
      set: function (val) {
        this.$emit('update:visible', val)
      }
    }
  },

  methods: {
    addRow () {
      this.evaGroupList.push({
        userId: null,
        userName: '',
        fullName: '',
        phone: '',
        email: '',
        position: '',
        groupRole: 'MEMBER',
        operateAuth: 'SOU_TECH',
        extExpertLevel: '',
        scoreAuth: 'SOU_TECH'
      })
    },
    /* 选择一个成员账号 */
    getUserObj (val, scope) {
      scope.row.userId = val ? val.userId : ''
      scope.row.userName = val ? val.username : ''
      scope.row.fullName = val ? val.nickname : ''
      scope.row.phone = val ? val.phone : ''
      scope.row.email = val ? val.email : ''
      scope.row.position = val ? val.ceeaJobcodeDescr : ''
      scope.row.extExpertLevel = val ? val.expertLevel : ''
    },
    deleteRow (index) {
      this.evaGroupList.splice(index, 1)
    },
    confirm () {
      let flag = this.evaGroupList.some(item => !item.userName || !item.extExpertLevel)
      if (flag) {
        this.$message.error('请补全必填项')
        return
      }
      const params = {
        projectId: this.projectId,
        groupList: this.evaGroupList
      }
      this.$http({
        url: '/api-sou/ext/buyer/bid/init/addTechManagementGroup',
        method: 'POST',
        data: params,
        loading: true
      }).then(res => {
        this.$message.success(res.message)
        this.dialogVisible = false
        this.$emit('success')
      })
    }
  }
}
</script>
