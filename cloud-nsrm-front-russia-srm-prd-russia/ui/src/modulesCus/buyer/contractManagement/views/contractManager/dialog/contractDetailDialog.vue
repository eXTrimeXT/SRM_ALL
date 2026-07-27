<template>
  <SrmDialog
    title="查看邀请历史"
    size="large"
    :visible.sync="visible"
    :close-on-click-modal="false"
    :before-close="close"
    v-bind="$attrs"
    v-on="$listeners"
  >
    <FormWrapper :colLength="3" :form-array="searchFormConfig" @getFormData="getQueryData" />
    <TableView
      ref="list"
      :table-data="tableData"
      :table-header="tableHeader"
      :pre-query-data="queryParam"
      :checkbox="true"
      :checkChange="handleCurrentChange"
      :com-active="$attrs['changeTab']"
      :open-custom-tabl="false"
      :url="tableViewUrl"
      :adeptMeiQl="true"
    />
    <div slot="footer" class="dialog-footer">
      <el-button @click="close">
        取消
      </el-button>
      <el-button type="primary" @click="confirm">
        确定
      </el-button>
    </div>
  </SrmDialog>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { transformMQL } from 'lib@/utils/util'
import inviteHttp from 'modcb@/inviteManagement/api'
import { uniqBy } from 'lodash'

export default {
  name: 'InviteHistoryDialog',
  components: {
    FormWrapper,
    TableView
  },
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    mode: { // 用于标识集采合同：collect，临采合同：temp
      type: String,
      default: 'collect'
    }
  },
  data () {
    return {
      searchFormConfig: [
        {
          prop: 'projectName',
          label: '定价单号'
        },
        {
          prop: 'buId',
          label: '公司',
          type: 'OUorganizationSelector'
        },
        {
          prop: 'vendorName',
          label: '供应商',
          type: 'quicksearch',
          showKey: 'companyName',
          propKey: 'companyName',
          name: 'scc_sup_company_info_all'
        },
        {
          prop: 'itemCode',
          label: '物料编码'
        }
      ],
      tableData: [],
      tableHeader: [],
      queryParam: {},
      tableViewUrl: inviteHttp.hisListPageUrl,
      selectedRows: []
    }
  },
  watch: {
    visible: {
      handler (nVal) {
        if (nVal) {
          this.getQueryData()
        }
      },
      immediate: true,
      deep: true
    }
  },
  created () {
    this.tableHeader = [
      {
        prop: 'projectName',
        label: '定价单号'
      },
      {
        prop: 'buName',
        label: '公司'
      },
      {
        prop: 'vendorName',
        label: '供应商'
      },
      {
        prop: 'itemCode',
        label: '物料编码'
      },
      {
        prop: 'itemName',
        label: '物料名称'
      },
      {
        prop: 'noTaxPrice',
        label: '未税单价'
      },
      {
        prop: 'quality',
        label: '数量'
      },
      {
        prop: 'noTaxTotalPrice',
        label: '未税总价'
      }
    ]
  },
  methods: {
    /* 查询列表数据 */
    getQueryData (params = {}) {
      this.queryParam = transformMQL.listPageData({
        type: 'SouInviteHistoryBuyer',
        action: 'query',
        params
      })

      this.$nextTick(() => {
        this.$refs.list.query()
      })
    },
    /* 选中行 */
    handleCurrentChange (val) {
      console.log('val:::', val)
      this.selectedRows = val
    },
    close () {
      this.$emit('close')
    },
    confirm () {
      if (!this.selectedRows || !this.selectedRows.length) return this.$message.warning('请勾选列表')
      if (uniqBy(this.selectedRows, 'buName').length > 1) {
        return this.$message.warning('同一个公司才可以一起创建')
      }
      console.log('####', uniqBy(this.selectedRows, 'vendorName'))
      if (uniqBy(this.selectedRows, 'vendorName').length > 1) {
        return this.$message.warning('同一个供应商才可以一起创建')
      }
      this.$emit('confirm', this.selectedRows)
    }
  }
}
</script>
