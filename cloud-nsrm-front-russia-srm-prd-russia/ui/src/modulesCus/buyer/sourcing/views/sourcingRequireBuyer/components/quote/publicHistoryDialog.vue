<template>
  <SrmDialog
    title="查看公示修改历史"
    size="middle"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
    append-to-body
  >
    <el-table
      ref="table"
      border
      stripe
      :data="tableData"
    >
      <el-table-column
        type="index"
        label="序号"
        fixed="left"
        width="60"
      />
      <el-table-column
        label="修改详情"
        minWidth="150"
        show-overflow-tooltip
      >
        <template v-slot="scope">
          <el-button type="text" @click="viewDetail(scope.row)">
            查看
          </el-button>
        </template>
      </el-table-column>
      <el-table-column
        prop="creationDate"
        label="修改时间"
        minWidth="150"
        show-overflow-tooltip
      />
      <el-table-column
        prop="createdFullName"
        label="修改人"
        minWidth="150"
        show-overflow-tooltip
      />
    </el-table>

    <div slot="footer" class="dialog-footer">
      <el-button @click="dialogVisible = false">
        {{ $t("common.close") }}
      </el-button>
    </div>

    <!-- 修改公示信息 -->
    <PublicInfoDialog
      ref="publicInfoDialog"
      :visible.sync="publicInfoDialogVisible"
      :readonly="true"
      :infoHistoryId="currentRow.infoHistoryId"
    />
  </SrmDialog>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import PublicInfoDialog from './publicInfoDialog'
import souHttp from '../../../../api'
import { transformMQL } from 'lib@/utils/util'

export default {
  name: 'PublicHistoryDialog',
  components: {
    TableView,
    FormWrapper,
    PublicInfoDialog
  },
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    readonly: {
      type: Boolean,
      default: false
    },
    form: {
      type: Object,
      default: () => {}
    }
  },
  data () {
    return {
      tableData: [],
      currentRow: {},
      publicInfoDialogVisible: false
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
  watch: {
    visible: {
      handler (nVal) {
        if (nVal) {
          if (this.form.reqHeadId) {
            this.getFormDetail()
          }
        }
      },
      immediate: true,
      deep: true
    }
  },
  methods: {
    viewDetail (row) {
      this.currentRow = row
      this.publicInfoDialogVisible = true
    },
    doLayout () {
      this.$nextTick(() => {
        this.$refs.table.doLayout()
      })
    },
    async getFormDetail () {
      let payload = {
        filter: {
          reqHeadId: {
            eq: this.form.reqHeadId
          }
        },
        page: {
          pageNum: 1,
          pageSize: 1000,
          sot: 'lastUpdateDate desc'
        }
      }
      let transformParams = transformMQL.save('SouInfoHistoryBuyer', payload, 'query')
      const response = await souHttp.publicHisInfo(transformParams)
      if (response.data.records) {
        this.tableData = response.data.records
      }
    }
  }
}
</script>
