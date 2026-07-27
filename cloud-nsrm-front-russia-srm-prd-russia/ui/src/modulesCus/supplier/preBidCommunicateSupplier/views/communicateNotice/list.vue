<template>
  <el-container
    class="flex-container"
    direction="vertical"
  >
    <el-main>
      <FormWrapper :form-array="searchFormConfig" @getFormData="getQueryData" />

      <MainHeader :l-span="22" :r-span="2">
        <template slot="left" />
      </MainHeader>

      <TableView
        ref="list"
        :table-data="tableData"
        :table-header="tableHeader"
        :pre-query-data="queryParam"
        :com-active="$attrs['changeTab']"
        :checkbox="false"
        :checkChange="handleCurrentChange"
        open-custom-table
        :url="tableViewUrl"
        :adeptMeiQl="true"
      />
    </el-main>
  </el-container>
</template>

<script>
import { tabTodoWatch } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MainHeader from 'lib@/components/Table/MainHeader'
import MImport from 'lib@/components/import'
import { downloadFileLink } from 'lib@/utils/file'
import CommunicateNoticeDetail from './edit'
import { commuNoticeHttp } from 'modcs@/preBidCommunicateSupplier/api'
import { transformMQL, transformTimeQuery } from 'lib@/utils/util'

export default {
  name: 'CommunicateNoticeList',

  components: {
    TableView,
    MainHeader,
    FormWrapper,
    MImport
  },

  mixins: [tabTodoWatch],

  data () {
    return {
      tableViewUrl: commuNoticeHttp.listPageUrl,
      tableHeader: [],
      tableData: [],
      searchFormConfig: [
        {
          prop: 'bidNoticeNo',
          label: '交流通知单号'
        },
        {
          prop: 'projectName',
          label: '项目名称'
        }
      ],
      queryParam: {},
      selectedRows: [] // 标记勾选行
    }
  },

  watch: {
    '$route.params': {
      // 寻源需求等其它地方跳转过来
      handler (nVal) {
        const { from, row } = nVal
        if (from) {
          this.editTab('view', row)
        }
      },
      immediate: true,
      deep: true
    }
  },

  mounted () {
    this.tableHeader = [
      {
        prop: 'bidNoticeNo',
        label: '交流通知单号',
        minWidth: 150,
        showType: 'button',
        btnStyle: 'text',
        callback: (row) => {
          console.log('row:::', row)
          this.editTab('view', row)
        }
      },
      {
        prop: 'projectName',
        label: '项目名称',
        minWidth: 120
      },
      {
        prop: 'bidUserNickname',
        label: '招标负责人',
        minWidth: 120
      },
      // {
      //   prop: 'vendorName',
      //   label: '供应商',
      //   minWidth: '150'
      // },
      {
        prop: 'status',
        label: '单据状态',
        dataType: 'dict',
        code: 'PRE_BID_NOTICE_STATUS',
        minWidth: '120'
      },
      {
        prop: 'creationDate',
        label: '发布日期',
        minWidth: 130
      },
      {
        prop: 'operation',
        label: this.$t('bidMod.operation'),
        minWidth: 150,
        showType: 'buttons',
        fixed: 'right',
        btnStyle: 'text',
        buttons: [
          {
            formattor: () => this.$t('common.view'),
            code: 'communicateNotice:view',
            callback: row => {
              this.editTab('view', row)
            }
          }
        ]
      }
    ]
    this.$nextTick(() => {
      this.getQueryData()
    })
  },

  methods: {
    /* 查询列表数据 */
    getQueryData (params = {}) {
      this.queryParam = transformMQL.listPageData({
        type: 'PreBidNotice',
        action: 'query',
        params: {
          ...params,
          status: 'ISSUED'
        }
      })
      this.$nextTick(() => {
        this.$refs.list.query()
      })
    },

    editTab (type, row) {
      const map = new Map([
        // 新增
        [
          'add',
          {
            component: CommunicateNoticeDetail,
            params: {
              flag: type,
              row,
              tabName: 'communicateNoticeBuyer'
            },
            title: '标前交流通知',
            name: 'communicateNoticeBuyer'
          }
        ],
        // 编辑
        [
          'edit',
          {
            component: CommunicateNoticeDetail,
            params: {
              flag: type,
              row,
              tabName: row.bidNoticeNo
            },
            title: '标前交流通知' + (row.bidNoticeNo || ''),
            name: row.bidNoticeNo
          }
        ],
        // 查看
        [
          'view',
          {
            component: CommunicateNoticeDetail,
            params: {
              flag: type,
              row,
              tabName: row.bidNoticeNo
            },
            title: '标前通知查询' + (row.bidNoticeNo || ''),
            name: row.bidNoticeNo
          }
        ]
      ])
      this.$emit('tab-add', map.get(type))
    },

    /* 选中行 */
    handleCurrentChange (val) {
      console.log('val:::', val)
      this.selectedRows = val
    },

    /** 删除 */
    async deleteRows (row) {
      const confirmResult = await this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).catch(() => {})
      if (confirmResult !== 'confirm') return
      let transformParams = transformMQL.save('PreBidNotice', [row.bidNoticeId], 'delete')
      const response = await commuNoticeHttp.delete(transformParams)
      if (response) {
        this.$message.success(this.$t('common.successDelete'))
        this.getQueryData()
      }
    }
  }
}
</script>
