<template>
  <div class="srm-approval-record">
    <slot>
      <p>{{ $t('cusEntry.approval.approvalRecord') }}</p>
    </slot>
    <el-table
      border
      :data="approvalRecords"
    >
      <el-table-column
        align="center"
        prop="activityNo"
        :label="$t('cusEntry.approval.flowNodeCode')"
        width="100"
      />
      <el-table-column
        align="center"
        prop="activityName"
        :label="$t('cusEntry.approval.flowNodeName')"
        min-width="120"
      />
      <el-table-column
        align="center"
        prop="parallel"
        :label="$t('cusEntry.approval.parallel')"
        min-width="120"
        :formatter="row => {
          return row.parallel || row.parallel == 0 ? $getDictLabel('BPM_NODE_TYPE', row.parallel.toString()) : ''
        }"
      />
      <el-table-column
        align="center"
        prop="createUser"
        :label="$t('cusEntry.approval.approvalUserNo')"
        min-width="120"
      />
      <el-table-column
        align="center"
        prop="createUserName"
        min-width="120"
      >
        <template #header>
          <span><i class="requiredStar">*</i>{{ $t('cusEntry.approval.approvalUserName') }}</span>
        </template>
        <template slot-scope="scope">
          <QuickSearch
            v-if="editable && scope.row.executor && !scope.row.executor?.find(item => item.isBpmPeople)"
            ref="userNode"
            :show-input="scope.row.createUserName"
            :placeholder="$t('common.pleaseSelect')"
            show-key="nickname"
            :multi-select="scope.row.parallel == -1 ? false : true"
            :scope-data="scope.row"
            name="scc_rbac_user_display"
            @close-quicksearch="getUserNode"
          />
          <span v-else> {{ scope.row.createUserName }} </span>
        </template>
      </el-table-column>
      <el-table-column
        align="center"
        prop="readTime"
        :label="$t('cusEntry.approval.readTime')"
        min-width="120"
        :formatter="(row, column, cellValue) => $parseTime(cellValue)"
      />
      <el-table-column
        align="center"
        prop="createDate"
        :label="$t('cusEntry.approval.approvalTime')"
        min-width="120"
        :formatter="(row, column, cellValue) => $parseTime(cellValue)"
      />
      <el-table-column
        align="center"
        prop="actionName"
        :label="$t('cusEntry.approval.approvalAction')"
        min-width="120"
      />
      <el-table-column
        align="center"
        prop="msg"
        :label="$t('cusEntry.approval.approvalMessage')"
        min-width="120"
      />
    </el-table>
  </div>
</template>

<script>
import QuickSearch from 'lib@/components/QuickSearch'
export default {
  name: 'ApprovalRecordList',
  components: {
    QuickSearch
  },
  props: {
    // 是否可以编辑
    editable: {
      type: Boolean,
      default: false
    },
    // 审批记录数据
    approvalRecords: {
      type: Array,
      default: () => ([])
    }
  },
  data () {
    return {}
  },
  watch: {
    approvalRecords: {
      immediate: true,
      handler (newValue, oldValue) {
        if (JSON.stringify(newValue) != JSON.stringify(oldValue)) {
          this.$emit('update:approvalRecords', newValue)
        }
      }
    }
  },
  methods: {
    // 获取人员节点
    getUserNode (node, row) {
      // 单选
      if (row.parallel == -1) {
        row.createUserName = node ? node.nickname : ''
        row.createUser = node ? node.username : ''
      } else {
        // 多选
        row.createUserName = node ? node.map(item => item.nickname).join() : ''
        row.createUser = node ? node.map(item => item.username).join() : ''
      }
    }
  }
}
</script>

<style scoped lang="scss">
// .srm-approval-record {
//   margin-bottom: 30px;
// }
.requiredStar {
  margin-right: 4px;
  color: #FF4A4D;
}
</style>
