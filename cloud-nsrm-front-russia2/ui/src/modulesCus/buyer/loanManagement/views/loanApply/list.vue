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
          <AuthorityButton code="lm:loanApply:add" type="primary" @click="editTab('add')">
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
        url="/api-sou/api-ql/Borrow/query"
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
import loanApplyDetail from './detail'

export default {
  name: 'LoanApplyList',
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
  created () {
    let _this = this
    _this.queryForm = [
      {
        prop: 'borrowNum',
        label: () => this.$t('cusEntry.biddingSettings.loanBill')
      },
      {
        prop: 'bidingNum',
        label: () => this.$t('bidMod.bidingNumCla')
      },
      {
        prop: 'bidingName',
        label: () => this.$t('bidMod.bidingName')
      },
      {
        prop: 'status',
        label: () => this.$t('bidMod.status'),
        type: 'dict',
        code: 'BORROW_STATUS'
      },
      {
        prop: 'applyUserName',
        label: () => this.$t('purchaseDemand.applicant')
      },
      {
        prop: 'departmentName',
        label: () => this.$t('purchaseDemand.ceeaDepartment')
      },
      {
        prop: 'borrowType',
        label: () => this.$t('cusEntry.biddingSettings.borrowDataType')
      },
      {
        prop: 'managerCode',
        label: '被借阅单位总经理',
        type: 'quicksearch',
        showKey: 'nickname',
        propKey: 'username',
        name: 'scc_rbac_user_display'
      }
    ]
    _this.tableHeader = [
      {
        prop: 'borrowNum',
        label: () => this.$t('cusEntry.biddingSettings.loanBill'),
        minWidth: 150,
        showType: 'button',
        btnStyle: 'text',
        callback: row => this.editTab('view', row)
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
        prop: 'borrowType',
        label: () => this.$t('cusEntry.biddingSettings.borrowDataType'),
        minWidth: 120
      },
      {
        prop: 'status',
        label: () => this.$t('common.status'),
        minWidth: 120,
        dataType: 'dict',
        code: 'BORROW_STATUS'
      },
      {
        prop: 'departmentName',
        label: () => this.$t('purchaseDemand.ceeaDepartment'),
        minWidth: 120
      },
      {
        prop: 'applyUserName',
        label: () => this.$t('purchaseDemand.applicant'),
        minWidth: 120
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
        width: 120,
        btnStyle: 'text',
        fixed: 'right',
        showType: 'buttons',
        buttons: [
          {
            callback: row => this.editTab('approval', row),
            formattor (val) {
              return _this.$t('common.approve')
            },
            // 审批中
            show: row => ['APPROVING'].includes(row.status) && row.startBpmUsername === this.username
          },
          {
            callback: row => this.editTab('edit', row),
            code: 'lm:loanApply:edit',
            formattor (val) {
              return _this.$t('common.edit')
            },
            // 拟定、已驳回, 已撤回
            show: row => ['DRAFT', 'REJECTED', 'WITHDRAW'].includes(row.status)
          },
          {
            callback: row => this.deleteItem(row),
            code: 'lm:loanApply:delete',
            formattor (val) {
              return _this.$t('common.delete')
            },
            show: row => row.status === 'DRAFT'
          },
          {
            callback: row => this.abandonItem(row),
            code: 'lm:loanApply:abandon',
            formattor (val) {
              return _this.$t('common.abandon')
            },
            // 已驳回, 已撤回
            show: row => ['REJECTED', 'WITHDRAW'].includes(row.status)
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
        type: 'Borrow',
        action: 'query',
        params
      })
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    editTab (type, row) {
      let tab = {}
      if (type === 'add') {
        tab = {
          component: loanApplyDetail,
          params: {
            flag: type,
            row: row,
            tabName: 'loanApplyDetail'
          },
          title: this.$t('cusEntry.biddingSettings.addLoanApply'),
          name: 'loanApplyDetail'
        }
      } else {
        tab = {
          component: loanApplyDetail,
          params: {
            flag: type,
            row: row,
            tabName: 'loanApplyDetail' + row.borrowId
          },
          title: row.borrowNum,
          name: 'loanApplyDetail' + row.borrowId
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
          'Borrow',
          [{
            'borrowId': row.borrowId,
            'attachList': [{ $delete: '*' }]
          }],
          'delete'
        )
        this.$http({
          url: '/api-sou/api-ql/Borrow/delete',
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
        const params = transformMQL.save('Borrow', [{ borrowId: row.borrowId }], 'abandon')
        this.$http({
          url: '/api-sou/api-ql/Borrow/abandon',
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
