<template>
  <!-- 调整投标时间历史 -->
  <SrmDialog
    :title="$t('cusEntry.supplement20250205.adjustTimeHistory')"
    size="large"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
    append-to-body
  >
    <el-table
      :data="deadlineList"
      border
      height="180"
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
        prop="createdFullName"
        :label="$t('closeTask.operationPerson')"
        min-width="100"
      />
      <!-- 当前投标截止时间 -->
      <el-table-column
        align="center"
        prop="currentEndTime"
        :label="$t('cusEntry.bidMod.currentEndTime')"
        min-width="100"
        :formatter="(row, column, cellValue) => $parseTime(cellValue)"
      />
      <!-- 调整截止时间为 -->
      <el-table-column
        align="center"
        prop="adjustEndTime"
        :label="$t('cusEntry.bidMod.adjustEndTime')"
        min-width="100"
        :formatter="(row, column, cellValue) => $parseTime(cellValue)"
      />
      <el-table-column
        align="center"
        prop="creationDate"
        :label="$t('common.operationTime')"
        min-width="100"
      >
        <template slot-scope="scope">
          {{$parseTime(scope.row.creationDate)}}
        </template>
      </el-table-column>
      <el-table-column
        align="center"
        prop="adjustReason"
        :label="$t('bidMod.bidingExtendReason1')"
        min-width="100"
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
 * 查看调整时间历史
 */
export default {
  name: 'AdjustDeadlineHistory',
  props: {
    visible: {
      type: Boolean,
      required: true
    },
    projectId: {
      type: [String, Number],
      required: true
    }
  },
  data () {
    return {
      deadlineList: []
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
    this.getDeadlineList()
  },

  methods: {
    getDeadlineList () {
      this.$http({
        url: `/api-sou/ext/buyer/bid/init/listAjustTime?projectId=${this.projectId}`,
        method: 'GET',
        loading: true
      }).then(res => {
        if (res && res.data) {
          this.deadlineList = res.data
        }
      })
    }
  }
}
</script>
