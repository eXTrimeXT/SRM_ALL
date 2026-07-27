<template>
  <el-container class="flex-container" direction="vertical">
    <el-main>
      <TableView
        ref="list"
        :table-data="tableData"
        :table-header="tableHeader"
        :pre-query-data="queryParam"
        :com-active="$attrs['changeTab']"
        :open-custom-table="false"
        :url="tableViewUrl"
        :adeptMeiQl="true"
      />
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import { tabTodoMixin } from '@/utils/mixins'
import inviteHttp from '../../api'
import { transformMQL } from 'lib@/utils/util'

export default {
  name: 'InviteVendorDetail',
  components: {
    TableView
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      tableViewUrl: inviteHttp.itemListPageUrl,
      queryParam: {},
      tableData: [],
      tableHeader: [],
      inviteHeadId: null
    }
  },
  created () {
    const { inviteHeadId } = this.$attrs.params.row
    this.inviteHeadId = inviteHeadId
    this.tableHeader = [
      {
        prop: 'projectNo',
        label: '招标项目编号',
        minWidth: 150
      },
      {
        prop: 'projectName',
        label: '项目名称'
      },
      {
        prop: 'orgBuName',
        label: '板块'
      },
      {
        prop: 'orgName',
        label: '所属公司'
      },
      {
        prop: 'bidCreationDate',
        label: '招标时间'
      },
      {
        prop: 'categoryName',
        label: '涉及品类'
      },
      {
        prop: 'isBid',
        label: '是否投标',
        dataType: 'dict',
        code: 'YES_OR_NO'
      },
      // {
      //   prop: 'isRefuseBid',
      //   label: '是否拒标',
      //   dataType: 'dict',
      //   code: 'YES_OR_NO'
      // },
      // {
      //   prop: 'refuseBidReason',
      //   label: '拒标原因'
      // },
      {
        prop: 'isInvalidBid',
        label: '是否废标',
        dataType: 'dict',
        code: 'YES_OR_NO'
      },
      {
        prop: 'isSuccBid',
        label: '是否中标',
        dataType: 'dict',
        code: 'YES_OR_NO'
      },
      {
        prop: 'techScore',
        label: '技术得分'
      },
      {
        prop: 'totalScore',
        label: '综合得分'
      },
      {
        prop: 'performanceScore',
        label: '绩效得分'
      },
      {
        prop: 'notParticipatingReason',
        label: '不参与的原因',
        minWidth: 150
      }
    ]
    this.getQueryData()
  },
  methods: {
    getQueryData (params = {}) {
      this.queryParam = transformMQL.listPageData({
        type: 'SouInviteItemBuyer',
        action: 'query',
        params: {
          ...params,
          inviteHeadId: this.inviteHeadId
        },
        filterOperator: {
          inviteHeadId: 'eq'
        }
      })

      this.$nextTick(() => {
        this.$refs.list.query()
      })
    }
  }
}
</script>
