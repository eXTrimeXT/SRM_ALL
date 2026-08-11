<template>
  <el-container
    class="flex-container"
    direction="vertical"
  >
    <el-main>
      <FormWrapper :form-array="searchFormConfig" @getFormData="getQueryData" />

      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <AuthorityButton code="jcAccount:add" type="primary" @click="editTab('add',{})">
            {{ $t('common.add') }}
          </AuthorityButton>
        </template>
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
      />
    </el-main>
  </el-container>
</template>

<script>
import { designPlanHttp } from 'modcb@/jcAgreement/api'
import { tabTodoWatch } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MainHeader from 'lib@/components/Table/MainHeader'
import MImport from 'lib@/components/import'
import { downloadFileLink } from 'lib@/utils/file'
import JcAccountDetail from './edit'

export default {
  name: 'JcAccountList',

  components: {
    TableView,
    MainHeader,
    FormWrapper,
    MImport
  },

  mixins: [tabTodoWatch],

  data () {
    return {
      tableViewUrl: '/api-sou/design/plan/ledger/getChLedgerPageList',
      tableHeader: [],
      tableData: [],
      searchFormConfig: [
        {
          prop: 'expirationMonth',
          label: this.$t('cusEntry.supplement20250121.expirationMonth'), // '到期月份'
          type: 'month'
        },
        {
          prop: 'projectName',
          label: this.$t('bidMod.bidingName'), // '项目名称
        },
        {
          prop: 'num',
          label: this.$t('cusEntry.supplement20250121.runRound'), // '项目轮次
        },
        {
          prop: 'headPerson',
          label: this.$t('dataConfMod.principal'), // '负责人'
          type: 'quicksearch',
          showKey: 'nickname',
          propKey: 'nickname',
          name: 'scc_rbac_user_display'
        },
        {
          prop: 'contractStartDate',
          label: this.$t('cusEntry.supplement20250121.contractStartDate'), // '合同起始日期'
          type: 'date'
        },
        {
          prop: 'contractEndDate',
          label: this.$t('cusEntry.supplement20250121.contractEndDate'), // '合同结束日期'
          type: 'date'
        },
        {
          prop: 'projectTotalMoney',
          label: this.$t('cusEntry.supplement20250121.projectTotalMoneyStart'), // '项目总金额从
        },
        {
          prop: 'projectTotalMoneyEnd',
          label: this.$t('cusEntry.supplement20250121.projectTotalMoneyEnd'), // '项目总金额至
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
        prop: 'expirationMonth',
        label: this.$t('cusEntry.supplement20250121.expirationMonth'), // '到期月份'
        minWidth: 120
      },
      {
        prop: 'projectName',
        label: this.$t('bidMod.bidingName'), // '项目名称'
        minWidth: 150,
        showType: 'button',
        btnStyle: 'text',
        callback: function (row) {
          this.editTab('view', row)
        }.bind(this),
        formattor (val) {
          return val || '--'
        }
      },
      {
        prop: 'num',
        label: this.$t('cusEntry.supplement20250121.runRound'), // '现运行项目轮数'
        minWidth: 130
      },
      {
        prop: 'contractStartDate',
        label: this.$t('cusEntry.supplement20250121.contractStartDate'), // '合同起始日期'
        minWidth: 130,
        dataType: 'dateTime'
      },
      {
        prop: 'contractEndDate',
        label: this.$t('cusEntry.supplement20250121.contractEndDate'), // '合同终止日期'
        minWidth: 130,
        dataType: 'dateTime'
      },
      {
        prop: 'delayReason',
        label: this.$t('cusEntry.supplement20250121.delayReason'), // '延期原因'
        minWidth: 150
      },
      {
        prop: 'nextSuggest',
        label: this.$t('cusEntry.supplement20250121.nextSuggest'), // '下一轮项目建议和注意事项'
        minWidth: 150
      },
      {
        prop: 'headPerson',
        label: this.$t('dataConfMod.principal'), // '负责人'
        minWidth: 100
      },
      {
        label: this.$t('cusEntry.supplement20250121.newDecreasingSituation'), // '新增集采物资成本递减情况/卢布'
        align: 'center',
        width: 150,
        children: [
          {
            prop: 'projectTotalMoney',
            label: this.$t('cusEntry.supplement20250121.projectTotalMoney'), // '项目总金额
          },
          {
            prop: 'addNum',
            label: this.$t('cusEntry.supplement20250121.addNum'), // '本次新增项目数
          },
          {
            prop: 'addBeforeMoney',
            label: this.$t('cusEntry.supplement20250121.addBeforeMoney'), // '原临采年采购额
          },
          {
            prop: 'addAfterMoney',
            label: this.$t('cusEntry.supplement20250121.addAfterMoney'), // '集采后年采购额
          },
          {
            prop: 'addDecrementMoney',
            label: this.$t('cusEntry.supplement20250121.addDecrementMoney'), // '本次递减金额
          },
          {
            prop: 'aboDecrementRatio',
            label: this.$t('cusEntry.supplement20250121.aboDecrementRatio'), // '成本递减比例(%)
          }
        ]
      },
      {
        label: this.$t('cusEntry.supplement20250121.oldDecreasingSituation'), // '原集采物资成本递减情况/卢布'
        width: 170,
        align: 'center',
        children: [
          {
            prop: 'aboBeforeMoney',
            label: this.$t('cusEntry.supplement20250121.aboBeforeMoney'), // '上期/集采前采购额
          },
          {
            prop: 'addBeforeMoney',
            label: this.$t('cusEntry.supplement20250121.addBeforeMoneyCur'), // '本次集采年采购额
          },
          {
            prop: 'addDecrementMoney',
            label: this.$t('cusEntry.supplement20250121.addDecrementMoney'), // '本次递减金额
          },
          {
            prop: 'aboDecrementRatio',
            label: this.$t('cusEntry.supplement20250121.aboDecrementRatio'), // '成本递减比例(%)
          }
        ]
      },
      {
        prop: 'lastUpdateDate',
        label: this.$t('common.lastUpdateDate'), // '更新日期'
        minWidth: 150,
        dataType: 'dateTime'
      },
      {
        prop: 'operation',
        label: this.$t('bidMod.operation'),
        minWidth: 150,
        showType: 'buttons',
        fixed: 'right',
        btnStyle: 'text',
        buttons: [
          // 拟定可以编辑
          {
            show: row => ['DRAFT'].includes(row.status),
            formattor: () => this.$t('common.edit'),
            // code: 'jcAccount:edit',
            callback: row => {
              this.editTab('edit', row)
            }
          },
          // 拟定可以删除
          // {
          //   show: row => ['DRAFT'].includes(row.status),
          //   formattor: () => this.$t('common.delete'),
          //   code: 'jcAccount:delete',
          //   callback: row => {
          //     this.deleteRow(row)
          //   }
          // }
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
      let transformParams = {}
      const { creationDate, ...rest } = params
      if (creationDate && creationDate.length) {
        transformParams.creationStartDate = creationDate[0]
        transformParams.creationEndDate = creationDate[1]
      }
      this.queryParam = {
        ...rest,
        ...transformParams
      }

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
            component: JcAccountDetail,
            params: {
              flag: type,
              row,
              tabName: 'jcAccount'
            },
            title: this.$t('cusEntry.supplement20250121.jcAccount'),
            name: 'jcAccount'
          }
        ],
        // 编辑
        [
          'edit',
          {
            component: JcAccountDetail,
            params: {
              flag: type,
              row,
              tabName: row.ledgerCode
            },
            title: this.$t('cusEntry.supplement20250121.jcAccount') + (row.ledgerCode || ''),
            name: row.ledgerCode
          }
        ],
        // 查看
        [
          'view',
          {
            component: JcAccountDetail,
            params: {
              flag: type,
              row,
              tabName: row.ledgerCode
            },
            title: this.$t('cusEntry.supplement20250121.jcAccount') + (row.ledgerCode || ''),
            name: row.ledgerCode
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

    async deleteRow (row) {
      const confirmResult = await this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).catch(() => {})
      if (confirmResult !== 'confirm') return
      const response = await designPlanHttp.changeStatus({
        agreementId: row.agreementId,
        operationType: status
      })
      if (response) {
        this.$message.success(this.$t('common.success'))
        this.getQueryData()
      }
    }
  }
}
</script>
