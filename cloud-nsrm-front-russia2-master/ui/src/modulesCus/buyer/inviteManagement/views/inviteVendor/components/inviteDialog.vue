<template>
  <!-- <SrmDialog
    title="选择申请单号"
    size="large"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
  > -->
  <SrmDialog
    :title="$t('cusEntry.supplement20250121.selectApplicationNumber')"
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
      :pageEnabled="false"
      :checkbox="true"
      :checkChange="handleCurrentChange"
      :url="tableViewUrl"
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
import QuickSearch from '@/library/components/QuickSearch'

export default {
  name: 'InviteDialog',
  components: {
    TableView,
    FormWrapper,
    QuickSearch
  },
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    editRows: {
      type: Array,
      default () {
        return []
      }
    }
  },
  data () {
    return {
      queryParam: {},
      searchFormConfig: [
        {
          prop: 'applyNo',
          // label: '申请单号'
          label: () => this.$t("contractMod.applicationOrderNum")
        },
        {
          prop: 'applyPerson',
          // label: '申请人'
          label: () => this.$t("purchaseDemand.applicant")
        }
      ],
      tableData: [],
      tableHeader: [],
      tableViewUrl: '',
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
  created () {
    this.tableHeader = [
      {
        prop: '',
        // label: '项目名称'
        label: () => this.$t("bidMod.bidingName")
      },
      {
        prop: 'applyNo',
        // label: '申请单号'
        label: () => this.$t("contractMod.applicationOrderNum")
      },
      {
        prop: 'applyPerson',
        // label: '申请人'
        label: () => this.$t("components.approvalHead.applicant")
      },
      {
        prop: 'creationDate',
        // label: '创建时间',
        label: () => this.$t("common.creationTime"),
        dataType: 'dateTime'
      }
    ]
  },
  methods: {
    handleConfirm () {
      // if (!this.seletedRows || !this.seletedRows.length) return this.$message.warning('请勾选列表')
      if (!this.seletedRows || !this.seletedRows.length) return this.$message.warning(this.$t("outsource.pleaseCheckList"))
      // if (this.seletedRows.length > 1) return this.$message.warning('仅可选择一条申请单号')
      if (this.seletedRows.length > 1) return this.$message.warning(this.$t("cusEntry.supplement20250121.onlyOneApplicationNumberCanBeSelected"))
      this.$emit('confirm', this.seletedRows)
    },
    /* 查询列表数据 */
    getQueryData (v = {}) {
      this.queryParam = Object.assign({}, v)

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
