<template>
  <SrmDialog
    title="调整投标时间历史"
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
        label="操作人"
        min-width="100"
      />
      <el-table-column
        align="center"
        prop="currentEndTime"
        label="当前投标截止时间"
        min-width="100"
      />
      <el-table-column
        align="center"
        prop="adjustEndTime"
        label="调整截止时间为"
        min-width="100"
      />
      <el-table-column
        align="center"
        prop="creationDate"
        label="操作时间"
        min-width="100"
      />
      <el-table-column
        align="center"
        prop="adjustReason"
        label="调整原因"
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
