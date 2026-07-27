<template>
  <el-container
    class="flex-container the_dictionary_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="queryForm"
        :pre-form-obj="preFormObj"
        @getFormData="getQueryData"
      />
      <MainHeader>
        <template slot="left">
          <AuthorityButton code="im:inspectManage:add" type="primary" @click="editTab('add')">
            {{ $t('common.add') }}
          </AuthorityButton>
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :adeptMeiQl="true"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        url="/api-sou/api-ql/Inspect/query"
      />
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import { transformMQL } from 'lib@/utils/util'
import inspectApplyDetail from './applyDetail'
import inspectReportDetail from './reportDetail'

export default {
  name: 'InspectManageList',
  components: {
    TableView,
    MainHeader,
    FormWrapper
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      pageSize: 15,
      gridId: 'list',
      queryParam: {},
      tableHeader: [],
      tableData: [],
      preFormObj: {},
      queryForm: []
    }
  },
  computed: {
    username () {
      return this.$store.getters.userInfo.username || ''
    }
  },
  watch: {
    '$route.params': {
      // 当前功能已经打开的时候监听是否是从其他功能跳转过来的
      deep: true,
      immediate: true,
      handler (nVal) {
        const { from, funName, row, taskIndex } = nVal
        if (from === 'bidNotice') {
          this.editTab('view', row)
        } else if (from == 'fromFun' && funName == 'inspectManage') {
          let rowObj = { inspectId: row.formId, inspectNum: row.formNo, reportNum: row.reportNum }
          if (taskIndex === 1) { // 待办
            this.editTab('submitReport', rowObj)
          } else if (taskIndex === 2) { // 已办
            this.editTab('view', rowObj)
          }
        }
      }
    }
  },
  created () {
    let _this = this
    _this.queryForm = [
      {
        prop: 'inspectNum',
        label: () => this.$t('cusEntry.biddingSettings.inspectBill')
      },
      {
        prop: 'bidingName',
        label: () => this.$t('bidMod.bidingName')
      },
      {
        prop: 'createdUserName',
        label: () => this.$t('common.creator')
      },
      {
        prop: 'bidingNum',
        label: () => this.$t('bidMod.bidingNumCla')
      },
      {
        prop: 'orgName',
        label: () => this.$t('cusEntry.common.company')
      }
    ]
    _this.tableHeader = [
      {
        prop: 'inspectNum',
        label: () => this.$t('cusEntry.biddingSettings.inspectBill'),
        showType: 'button',
        btnStyle: 'text',
        callback: row => this.editTab('view', row),
        minWidth: 150
      },
      {
        prop: 'bidingNum',
        label: () => this.$t('bidMod.bidingNumCla'),
        minWidth: 150
      },
      {
        prop: 'bidingName',
        label: () => this.$t('bidMod.bidingName'),
        minWidth: 150
      },
      {
        prop: 'orgName',
        label: () => this.$t('cusEntry.common.company'),
        minWidth: 150
      },
      {
        prop: 'inspectStatus',
        label: () => this.$t('bidMod.billstatus'),
        minWidth: 120,
        dataType: 'dict',
        code: 'INSPECT_STATUS'
      },
      {
        prop: 'createdUserName',
        label: () => this.$t('common.creator'),
        minWidth: 140
      },
      {
        prop: 'creationDate',
        label: () => this.$t('common.creationTime'),
        dataType: 'dateTime',
        minWidth: 120
      },
      {
        prop: 'operation',
        label: () => this.$t('common.operation'),
        width: 150,
        btnStyle: 'text',
        fixed: 'right',
        showType: 'buttons',
        buttons: [
          {
            callback: row => this.editTab('approval', row),
            formattor (val) {
              return _this.$t('common.approve')
            },
            // 申请审批中、报告审批中
            show: row => ['APPLY_APPROVING', 'REPORT_APPROVING'].includes(row.inspectStatus) && row.startBpmUsername === this.username
          },
          {
            callback: row => this.editTab('edit', row),
            code: 'im:inspectManage:edit',
            formattor (val) {
              return _this.$t('common.edit')
            },
            // 拟定、申请已撤回、申请已驳回
            show: row => ['DRAFT', 'APPLY_WITHDRAW', 'APPLY_REJECTED'].includes(row.inspectStatus)
          },
          {
            callback: row => this.deleteItem(row),
            code: 'im:inspectManage:delete',
            formattor (val) {
              return _this.$t('common.delete')
            },
            show: row => row.inspectStatus === 'DRAFT'
          },
          {
            callback: row => this.editTab('submitReport', row),
            code: 'im:inspectManage:submitReport',
            formattor (val) {
              return _this.$t('cusEntry.biddingSettings.submitReport')
            },
            // 待提交报告、报告已撤回、报告已驳回
            show: row => ['APPLY_APPROVED', 'REPORT_WITHDRAW', 'REPORT_REJECTED'].includes(row.inspectStatus)
          },
          {
            callback: row => this.abandonItem(row),
            code: 'im:inspectManage:abandon',
            formattor (val) {
              return _this.$t('common.abandon')
            },
            // 申请已撤回、申请已驳回、待提交报告、报告已撤回、报告已驳回
            show: row => ['APPLY_WITHDRAW', 'APPLY_REJECTED', 'APPLY_APPROVED', 'REPORT_WITHDRAW', 'REPORT_REJECTED'].includes(row.inspectStatus)
          }
        ]
      }
    ]
    this.$nextTick(() => {
      this.getQueryData()
    })
  },
  methods: {
    getQueryData (v) {
      let params = v || {}
      this.queryParam = transformMQL.listPageData({
        type: 'Inspect',
        action: 'query',
        params,
        sort: 'creationDate',
        filter: {
          inspectNum: {
            contains: params.inspectNum
          },
          bidingName: {
            contains: params.bidingName
          },
          $or: {
            createdBy: {
              contains: params.createdUserName
            },
            createdFullName: {
              contains: params.createdUserName
            }
          },
          bidingNum: {
            contains: params.bidingNum
          },
          orgName: {
            contains: params.orgName
          }
        }
      })

      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    // 编辑
    editTab (type, row) {
      let tab = {}
      if (type === 'add') { // 新增 创建考察申请
        tab = {
          component: inspectApplyDetail,
          params: {
            flag: type,
            row: row,
            tabName: 'inspectApplyDetail'
          },
          title: this.$t('cusEntry.biddingSettings.addInspection'),
          name: 'inspectApplyDetail'
        }
      } else if (type === 'edit' || (['view', 'manage'].includes(type) && !row.reportNum)) { // 申请没有reportNum
        tab = {
          component: inspectApplyDetail,
          params: {
            flag: type,
            row: row,
            tabName: 'inspectApplyDetail' + row.inspectId
          },
          title: row.inspectNum,
          name: 'inspectApplyDetail' + row.inspectId
        }
      } else if (type === 'submitReport' || (['view', 'manage'].includes(type) && row.reportNum)) { // 报告有reportNum
        tab = {
          component: inspectReportDetail,
          params: {
            flag: type,
            row: row,
            tabName: 'inspectReportDetail' + row.inspectId
          },
          title: row.inspectNum,
          name: 'inspectReportDetail' + row.inspectId
        }
      }
      this.$emit('tab-add', tab)
    },
    // 删除数据
    deleteItem (row) {
      this.$confirm(this.$t('cusEntry.common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        const params = transformMQL.save(
          'Inspect',
          [{
            'inspectId': row.inspectId,
            'vendorList': [{ $delete: '*' }],
            'userList': [{ $delete: '*' }],
            'attachList': [{ $delete: '*' }]
          }],
          'delete'
        )
        this.$http({
          url: '/api-sou/api-ql/Inspect/delete',
          method: 'POST',
          data: params,
          loading: true
        }).then(res => {
          this.$message.success(this.$t('common.success'))
          this.getQueryData()
        })
      })
    },
    abandonItem (row) {
      this.$confirm(this.$t('cusEntry.common.confirmAbandon'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        const params = transformMQL.save('Inspect', [{ inspectId: row.inspectId }], 'abandon')
        this.$http({
          url: '/api-sou/api-ql/Inspect/abandon',
          method: 'POST',
          data: params,
          loading: true
        }).then(res => {
          this.$message.success(this.$t('common.success'))
          this.getQueryData()
        })
      })
    }
  }
}
</script>
<style scoped lang="scss"></style>
