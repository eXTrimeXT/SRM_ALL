<template>
  <SrmDialog
    :title="$t('cusEntry.bidMod.viewExtractHistory')"
    size="large"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
    append-to-body
  >
    <el-table
      border
      :data="historyList"
      max-height="180"
      style="width: 100%"
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
        show-overflow-tooltip
      />
      <el-table-column
        align="center"
        prop="fullName"
        :label="$t('bidMod.fullName')"
        min-width="120"
        show-overflow-tooltip
      />
      <!-- 专家等级 -->
      <el-table-column
        align="center"
        prop="extExpertLevel"
        :label="$t('cusEntry.bidMod.expertLevel')"
        min-width="120"
        :formatter="(row, column, cellValue) => $getDictLabel('SOU_BID_EXPERT_LEVEL', cellValue)"
        show-overflow-tooltip
      />
      <!-- 抽取时间 -->
      <el-table-column
        align="center"
        prop="extractTime"
        :label="$t('cusEntry.bidMod.extractTime')"
        min-width="120"
        show-overflow-tooltip
      />
      <!-- 移除原因 -->
      <el-table-column
        align="center"
        prop="extRemoveReason"
        :label="$t('cusEntry.bidMod.removeReason')"
        min-width="120"
        show-overflow-tooltip
      />
    </el-table>
    <div slot="footer" class="dialog-footer">
      <el-button @click="dialogVisible = false">
        {{ $t('common.close') }}
      </el-button>
    </div>
  </SrmDialog>
</template>

<script>
/**
 * 抽取历史
 */
export default {
  name: 'TechHistoryDialog',

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
      historyList: []
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

  created () {
    this.getExpertRecord()
  },

  methods: {
    /* 查询数据 */
    getExpertRecord () {
      this.$http({
        url: `/api-sou/ext/buyer/bid/init/getExpertRecord?projectId=${this.projectId}`,
        method: 'GET',
        loading: true
      }).then(res => {
        if (res && res.data) {
          this.historyList = res.data
        }
      })
    }
  }
}
</script>
