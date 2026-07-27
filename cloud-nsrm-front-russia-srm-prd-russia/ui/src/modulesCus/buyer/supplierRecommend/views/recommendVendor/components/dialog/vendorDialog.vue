<template>
  <SrmDialog
    title="选择供应商"
    size="large"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
  >
    <FormWrapper :colLength="2" :form-array="searchFormConfig" @getFormData="getQueryData" />
    <TableView
      ref="list"
      :table-data="tableData"
      :table-header="tableHeader"
      :pre-query-data="queryParam"
      :com-active="$attrs['changeTab']"
      :open-custom-tabl="false"
      :pageEnabled="true"
      :checkbox="true"
      :checkChange="handleCurrentChange"
      :url="tableViewUrl"
      :adeptMeiQl="true"
    />
    <div slot="footer" class="dialog-footer">
      <el-button @click="dialogVisible = false">
        {{ $t("common.cancel") }}
      </el-button>
      <el-button type="primary" @click="handleConfirm">
        {{ $t("common.confirm") }}
      </el-button>
    </div>
  </SrmDialog>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { transformMQL } from 'lib@/utils/util'

export default {
  name: 'VendorDialog',
  components: {
    TableView,
    FormWrapper
  },
  props: {
    visible: {
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
      queryParam: {},
      searchFormConfig: [
        {
          prop: 'vendorName',
          label: '供应商',
          type: 'quicksearch',
          showKey: 'companyName',
          name: 'scc_sup_company_info_all'
        }
      ],
      tableData: [],
      tableHeader: [],
      tableViewUrl: '/api-sou/api-ql/RecommvendorProject/recommvendorQuickQuery',
      seletedRows: []
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
          this.getQueryData()
        }
      },
      immediate: true
    }
  },
  created () {
    this.tableHeader = [
      {
        prop: 'vendorCode',
        label: '供应商编码'
      },
      {
        prop: 'vendorName',
        label: '供应商名称'
      },
      {
        prop: 'sourceType',
        label: '来源'
      }
    ]
  },
  methods: {
    handleConfirm () {
      if (!this.seletedRows || !this.seletedRows.length) return this.$message.warning('请勾选列表')
      this.$emit('confirm', this.seletedRows)
    },
    /* 查询列表数据 */
    getQueryData (params = {}) {
      this.queryParam = transformMQL.save('RecommvendorProject', {
        ...params,
        projectId: this.form.projectId || this.form.originalProjectId
      }, 'recommvendorQuickQuery')

      this.$nextTick(() => {
        this.$refs.list.query()
      })
    },
    handleCurrentChange (val) {
      this.seletedRows = val
    }
  }
}
</script>
