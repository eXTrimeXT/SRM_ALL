<template>
  <SrmDialog
    title="查看邀请历史"
    size="large"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
  >
    <FormWrapper :colLength="3" :form-array="searchFormConfig" @getFormData="getQueryData" />
    <TableView
      ref="list"
      :table-data="tableData"
      :table-header="tableHeader"
      :pre-query-data="queryParam"
      :com-active="$attrs['changeTab']"
      :open-custom-tabl="false"
      :url="tableViewUrl"
      :adeptMeiQl="true"
    />
    <div slot="footer" class="dialog-footer" />
  </SrmDialog>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { transformMQL } from 'lib@/utils/util'
import inviteHttp from '../../../api'

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
    }
  },
  data () {
    return {
      searchFormConfig: [
        {
          prop: 'projectName',
          label: '项目名称'
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
          prop: 'souNo',
          label: '申请单号'
        }
      ],
      tableData: [],
      tableHeader: [],
      queryParam: {},
      tableViewUrl: inviteHttp.hisListPageUrl
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
      immediate: true,
      deep: true
    }
  },
  created () {
    this.tableHeader = [
      {
        prop: 'projectName',
        label: '项目名称'
      },
      {
        prop: 'souNo',
        label: '申请单号'
      },
      {
        prop: 'creationDate',
        label: '邀请日期',
        formattor: (val) => this.$dayjsParse(val)
      },
      {
        prop: 'vendorName',
        label: '供应商'
      },
      {
        prop: 'createdFullName',
        label: '邀请人'
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
    }
  }
}
</script>
