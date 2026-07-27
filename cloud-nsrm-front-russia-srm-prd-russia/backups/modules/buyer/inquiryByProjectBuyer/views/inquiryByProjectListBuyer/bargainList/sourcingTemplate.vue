<template>
  <srm-dialog
    :visible.sync="dialogVisible"
    :title="$t('bidMod.addByTemplate')"
    size="large"
  >
    <FormWrapper
      :form-array="sourcingQuery"
      @getFormData="querySourcingTemlapte"
    />
    <TableView
      ref="sourcingTable"
      style="min-height: 400px"
      :table-header="sourcingTemplateTableHeader"
      :check-change="selectRows"
      :page-size="pageSize"
      :checkbox="true"
      :pre-query-data="sourcingParams"
      :source="$api.brg.sourcingTemplate.listPage"
    />
    <template
      #footer
      class="dialog-footer"
    >
      <el-button @click="dialogVisible = false">
        {{ $t('common.cancel') }}
      </el-button>
      <el-button
        type="primary"
        @click="generateSourceForm"
      >
        {{ $t('common.confirm') }}
      </el-button>
    </template>
  </srm-dialog>
</template>

<script>
/**
 * 通过模板新增
 */
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'

export default {
  name: 'SourcingTemplate',
  components: {
    TableView,
    FormWrapper
  },
  props: {
    visible: Boolean
  },
  data () {
    return {
      pageSize: 15,
      sourcingQuery: [
        { prop: 'sn', label: this.$t('sourcingTemplate.sn') },
        { prop: 'name', label: this.$t('sourcingTemplate.name') }
      ],
      sourcingTemplateTableHeader: [
        {
          prop: 'sn',
          label: this.$t('sourcingTemplate.sn'),
          minWidth: 140
        },
        {
          prop: 'name',
          label: this.$t('sourcingTemplate.name'),
          minWidth: 140
        }
      ],
      sourcingParams: {},
      selectedRows: []
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
    dialogVisible (newVal, oldVal) {
      if (newVal && newVal !== oldVal) {
        this.querySourcingTemlapte()
      }
    }
  },
  methods: {
    /* 勾选行 */
    selectRows (rows) {
      this.selectedRows = rows
    },
    /* 确定 */
    generateSourceForm () {
      const rows = this.selectedRows
      if (rows && rows.length) {
        if (rows.length > 1) {
          this.$message.warning(this.$t('bidMod.addByTemplateMsg_1'))
          return false
        }
        const { id } = rows[0]
        this.$api.brg.generateSourceForm(id).then(res => {
          this.$message.success(res.message)
          this.$emit('submitSuccess')
        })
      } else {
        this.$message.error(this.$t('bidMod.addByTemplateMsg_2'))
      }
    },
    /* 查询模板 */
    querySourcingTemlapte (params) {
      // 询比价 查询有效的寻源类型为TENDER的模板
      this.sourcingParams = {
        ...params,
        status: 'VALID',
        sourcingType: 'TENDER'
      }
      this.$nextTick(() => this.$refs.sourcingTable.query())
    }
  }
}
</script>
