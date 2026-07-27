<template>
  <el-container
    class="flex-container"
    direction="vertical"
  >
    <el-main>
      <FormWrapper :form-array="searchFormConfig" @getFormData="getQueryData" />

      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <AuthorityButton code="bidNotice:abandonApply" type="primary" @click="abandonApply">
            中/落标废弃申请
          </AuthorityButton>
        </template>
      </MainHeader>

      <TableView
        ref="list"
        :table-data="tableData"
        :table-header="tableHeader"
        :pre-query-data="queryParam"
        :com-active="$attrs['changeTab']"
        :checkbox="true"
        :checkChange="handleCurrentChange"
        open-custom-table
        :url="tableViewUrl"
        :adeptMeiQl="true"
      />
    </el-main>

    <!-- 废弃弹窗 -->
    <DiscardDialog
      ref="discardDialog"
      :visible.sync="discardDialogVisible"
      @confirm="discardDialogConfirm"
    />
  </el-container>
</template>

<script>
import { tabTodoWatch } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MainHeader from 'lib@/components/Table/MainHeader'
import BidNoticeDetail from './edit'
import bidNoticeHttp from './api'
import { transformMQL, transformTimeQuery } from 'lib@/utils/util'
import DiscardDialog from './components/dialog/discardDialog'

export default {
  name: 'BidNoticeList',

  components: {
    TableView,
    MainHeader,
    FormWrapper,
    DiscardDialog
  },

  mixins: [tabTodoWatch],

  data () {
    return {
      tableViewUrl: bidNoticeHttp.listPageUrl,
      tableHeader: [],
      tableData: [],
      searchFormConfig: [
        {
          prop: 'bidNoticeNo',
          label: '中/落标通知单号'
        },
        {
          prop: 'extProjectNo',
          label: '项目编号'
        },
        {
          prop: 'status',
          label: '单据状态',
          type: 'dict',
          code: 'SOU_TN_STATUS'
        },
        {
          prop: 'createdFullName',
          label: '创建人'
        },
        {
          prop: 'souName',
          label: '项目名称'
        },
        {
          prop: 'creationDate',
          label: '创建日期',
          type: 'daterange'
        }
      ],
      queryParam: {},
      selectedRows: [], // 标记勾选行
      discardDialogVisible: false,
      discardRow: []
    }
  },

  watch: {
    '$route.params': {
      // 寻源需求等其它地方跳转过来
      handler (nVal) {
        const { from, row } = nVal
        if (from === 'bidNoticeAbandon') {
          this.editTab('view', row)
        }
        if (from === 'biddingManagementNew') {
          this.editTab('edit', row)
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
        label: '中/落标通知单号',
        minWidth: 130,
        showType: 'button',
        btnStyle: 'text',
        callback: (row) => {
          this.editTab('view', row)
        }
      },
      {
        prop: 'souNo',
        label: '招标单号',
        minWidth: 130
      },
      {
        prop: 'extProjectNo',
        label: '招标项目编号',
        minWidth: 130
      },
      {
        prop: 'souName',
        label: '项目名称',
        minWidth: 130
      },
      {
        prop: 'extOrgOuName',
        label: '公司',
        minWidth: 150
      },
      {
        prop: 'status',
        label: '单据状态',
        dataType: 'dict',
        code: 'SOU_CA_STATUS',
        minWidth: 120
      },
      {
        prop: 'originalBidNoticeNo',
        label: '中/落标废弃申请单号',
        minWidth: 180,
        showType: 'button',
        btnStyle: 'text',
        callback: (row) => {
          this.$router.push({
            name: 'bidNoticeAbandon',
            params: {
              from: 'bidNotice',
              row,
              type: 'view'
            }
          })
        }
      },
      {
        prop: 'discardType',
        label: '单据类型',
        dataType: 'dict',
        code: 'SOU_ATN_TYPE'
      },
      {
        prop: 'createdFullName',
        label: '创建人',
        minWidth: 120
      },
      {
        prop: 'creationDate',
        label: '创建日期',
        minWidth: 130,
        formattor: (val) => this.$dayjsParse(val)
      },
      {
        prop: 'discardReason',
        label: '废弃说明',
        minWidth: 130
      },
      {
        prop: 'operation',
        label: this.$t('bidMod.operation'),
        minWidth: 130,
        showType: 'buttons',
        fixed: 'right',
        btnStyle: 'text',
        buttons: [
          {
            show: row => ['DRAFT', 'WITHDRAW', 'REJECTED'].includes(row.status),
            formattor: () => this.$t('common.edit'),
            code: 'bidNotice:update',
            callback: row => {
              this.editTab('edit', row)
            }
          },
          {
            show: row => ['DRAFT'].includes(row.status),
            formattor: () => this.$t('common.delete'),
            code: 'bidNotice:delete',
            callback: row => {
              this.deleteRows(row)
            }
          },
          {
            show: row => ['WITHDRAW', 'REJECTED'].includes(row.status),
            formattor: () => this.$t('common.abandon'),
            code: 'bidNotice:abandon',
            callback: row => {
              this.abandonRows(row)
            }
          },
          // 审批中 需要审批操作
          {
            show: row => ['APPROVING'].includes(row.status),
            formattor: () => this.$t('common.approve'),
            code: 'bidNotice:approval',
            callback: row => {
              this.editTab('approval', row)
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
      transformTimeQuery(['creationDate'], params)
      this.queryParam = transformMQL.listPageData({
        type: 'BidNotice',
        action: 'query',
        params,
        filterOperator: {
          creationDate: 'between'
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
            component: BidNoticeDetail,
            params: {
              flag: type,
              row,
              tabName: 'bidNotice'
            },
            title: '中/落标通知',
            name: 'bidNotice'
          }
        ],
        // 编辑
        [
          'edit',
          {
            component: BidNoticeDetail,
            params: {
              flag: type,
              row,
              tabName: 'bidNotice' + row.bidNoticeNo
            },
            title: '中/落标通知' + (row.bidNoticeNo || ''),
            name: 'bidNotice' + row.bidNoticeNo
          }
        ],
        // 查看
        [
          'view',
          {
            component: BidNoticeDetail,
            params: {
              flag: type,
              row,
              tabName: 'bidNotice' + row.bidNoticeNo
            },
            title: '中/落标通知' + (row.bidNoticeNo || ''),
            name: 'bidNotice' + row.bidNoticeNo
          }
        ],
        // 审批
        [
          'approval',
          {
            component: BidNoticeDetail,
            params: {
              flag: type,
              row,
              tabName: 'bidNotice' + row.bidNoticeNo,
              activeWorkflowTab: true // 跳转到审批流
            },
            title: '中/落标通知' + (row.bidNoticeNo || ''),
            name: 'bidNotice' + row.bidNoticeNo
          }
        ]
      ])
      this.$emit('tab-add', map.get(type))
    },

    /** 废弃申请 */
    async abandonApply () {
      if (!this.selectedRows || !this.selectedRows.length) return this.$message.warning('请勾选列表')
      if (this.selectedRows.length > 1) return this.$message.warning('仅可勾选一条')
      if (this.selectedRows[0].status !== 'APPROVED') return this.$message.warning('仅【已审批】状态可发起中/落标废弃申请')
      const confirmResult = await this.$confirm('确定发起中/落标废弃申请？', {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).catch(() => {})
      if (confirmResult !== 'confirm') return
      this.$router.push({
        name: 'bidNoticeAbandon',
        params: {
          from: 'bidNotice',
          row: this.selectedRows[0],
          type: 'add'
        }
      })
    },

    /* 选中行 */
    handleCurrentChange (val) {
      console.log('val:::', val)
      this.selectedRows = val
    },

    /** 删除 */
    async deleteRows (row) {
      const confirmResult = await this.$confirm(this.$t('common.confirmDeleteRow'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).catch(() => {})
      if (confirmResult !== 'confirm') return
      let transformParams = transformMQL.save('BidNotice', [row.bidNoticeId], 'delete')
      await bidNoticeHttp.delete(transformParams)
      this.$message.success(this.$t('common.successDelete'))
      this.getQueryData()
    },

    /** 废弃 */
    async abandonRows (row) {
      this.discardDialogVisible = true
      this.$nextTick(() => {
        this.$refs.discardDialog.resetFields()
      })
      this.discardRow = row
    },
    async discardDialogConfirm (form) {
      let transformParams = transformMQL.save('BidNotice', [{
        ...this.discardRow,
        ...form
      }], 'abandon')
      await bidNoticeHttp.abandon(transformParams)
      this.$message.success(this.$t('common.successAbandon'))
      this.getQueryData()
    }
  }
}
</script>
