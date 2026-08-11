<template>
  <!-- <SrmDialog
      title="查看邀请历史"
      size="large"
      :visible.sync="dialogVisible"
      :close-on-click-modal="false"
    > -->
  <SrmDialog
    :title="$t('cusEntry.supplement20250121.viewInvitationHistory')"
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
          // label: '项目名称'
          label: () => this.$t("bidMod.bidingName")
        },
        {
          prop: 'vendorName',
          // label: '供应商',
          label: () => this.$t("common.vendor"),
          type: 'quicksearch',
          showKey: 'companyName',
          propKey: 'companyName',
          name: 'scc_sup_company_info_all'
        },
        {
          prop: 'souNo',
          // label: '申请单号'
          label: () => this.$t("contractMod.applicationOrderNum")
        },
        {
          prop: 'categoryName',
          // label: '品类',
          label: () => this.$t("common.category"),
          type: 'catSelect',
          showKey: 'categoryName'
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
        // label: '项目名称'
        label: () => this.$t("bidMod.bidingName")
      },
      {
        prop: 'souNo',
        // label: '申请单号'
        label: () => this.$t("contractMod.applicationOrderNum")
      },
      {
        prop: 'creationDate',
        // label: '邀请日期',
        label: () => this.$t("cusEntry.supplement20250121.invitationDate"),
        dataType: 'dateTime'
      },
      {
        prop: 'vendorName',
        // label: '供应商'
        label: () => this.$t("common.vendor")
      },
      {
        prop: 'categoryName',
        // label: '品类'
        label: () => this.$t("common.category")
      },
      {
        prop: 'createdFullName',
        // label: '邀请人'
        label: () => this.$t("cusEntry.vendorMod.invitor")
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
