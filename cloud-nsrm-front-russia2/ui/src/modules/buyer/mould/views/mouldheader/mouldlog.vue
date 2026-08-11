<template>
  <el-container
    class="mouldScrapEdit"
    direction="vertical"
  >
    <el-main>
      <TableView
        :ref="gridId"
        :table-header="tableHeader"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :source="mouldheader.listLogPage"
        :checkbox="true"
      />
    </el-main>
  </el-container>
</template>

<script>
import { tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import mouldheaderEdit from 'modb@/mould/views/mouldheader/edit'
import mouldScrap from 'modb@/mould/views/mouldheader/mouldScrap'
import mouldChange from 'modb@/mould/views/mouldheader/mouldChange'
import { mouldheader } from 'modb@/mould/api'
export default {
  name: 'Mouldlog',
  components: { TableView },
  mixins: [tabTodoMixin],
  data () {
    return {
      mouldheader: mouldheader,
      queryParam: {},
      pageSize: 15,
      gridId: 'list',
      tableHeader: [],
      dataSource: [],
      disabledButton: false,
      rules: {}
    }
  },
  computed: {},
  watch: {},
  created () {
    const _this = this
    this.tableHeader = [
      {
        prop: 'flowBusinessType',
        label: this.$t('bidMod.billType'),
        dataType: 'dict',
        code: 'MOULD_FLOW_TYPE'
      },
      {
        prop: 'mouldFlowCode',
        label: this.$t('vendorMod.reviewFormNumber')
      },
      {
        prop: 'createdBy',
        label: this.$t('common.creator')
      },
      {
        prop: 'creationDate',
        dataType: 'dateTime',
        label: this.$t('elementDefinition.creationDate')
      },
      {
        prop: 'approveStatus',
        label: this.$t('purchaseDemand.auditStatus'),
        dataType: 'dict',
        code: 'MOULD_APPROVE_STATUS'
      },
      {
        prop: 'operation',
        label: this.$t('common.operation'),
        showType: 'buttons',
        btnStyle: 'text',
        fixed: 'right',
        width: 130,
        buttons: [
          {
            callback: row => this.viewHandle(row),
            formattor: (label, row) => {
              let btnText = this.$t('common.view')
              if (['SUBMITTED', 'APPROVAL'].includes(row.approveStatus)) return btnText
              if (row.flowBusinessType === 'CREATE') return this.$t('mould.editMouldLedger')
              if (row.flowBusinessType === 'UPDATE') return this.$t('mould.moldLedgerChange')
              if (row.flowBusinessType === 'SCRAP') return this.$t('mould.moldScrapped')
              if (row.flowBusinessType === 'CHANGE') return this.$t('mould.transferMold')
              return btnText
            }
          }
        ],
        selectList: []
      }
    ]
  },
  mounted () {
    const { mouldCode } = this.$attrs.params
    this.getDetail(mouldCode)
  },
  methods: {
    viewHandle (row) {
      let readOnly = false
      if (row.flowBusinessType === 'CREATE') { // 新建
        if (row.approveStatus === 'SUBMITTED' || row.approveStatus === 'APPROVAL') {
          this.mode = 'view'
          readOnly = true
        } else {
          this.mode = 'edit'
        }
        const tab = {
          component: mouldheaderEdit,
          params: {
            row,
            flag: this.mode,
            readOnly: readOnly
          },
          title: this.mode === 'view' ? this.$t('mould.checkMoldledger') : this.$t('mould.editMouldLedger'),
          name: 'mouldheaderEdit' + row.mouldHeaderId
        }
        this.$emit('tab-add', tab)
      }
      if (row.flowBusinessType === 'UPDATE') { // 变更
        if (row.approveStatus === 'SUBMITTED' || row.approveStatus === 'APPROVAL') {
          this.mode = 'view'
        } else {
          this.mode = 'update'
        }
        const tab = {
          component: mouldheaderEdit,
          params: {
            row,
            flag: this.mode
          },
          title: this.mode === 'view' ? this.$t('mould.checkMoldledger') : this.$t('mould.moldLedgerChange'),
          name: 'mouldheaderEdit' + row.mouldHeaderId
        }
        this.$emit('tab-add', tab)
      }
      if (row.flowBusinessType === 'SCRAP') { // 报废
        if (row.approveStatus === 'SUBMITTED' || row.approveStatus === 'APPROVAL') {
          this.mode = 'view'
        } else {
          this.mode = 'scrap'
        }
        const tab = {
          component: mouldScrap,
          params: {
            flag: this.mode,
            mouldFlowLogId: row.mouldFlowLogId
          },
          title: this.$t('mould.moldScrapped'),
          name: 'mouldScrapEdit'
        }
        this.$emit('tab-add', tab)
      }
      if (row.flowBusinessType === 'CHANGE') { // 模具转移
        if (row.approveStatus === 'SUBMITTED' || row.approveStatus === 'APPROVAL') {
          this.mode = 'view'
        } else {
          this.mode = 'change'
        }
        const tab = {
          component: mouldChange,
          params: {
            flag: this.mode,
            mouldFlowLogId: row.mouldFlowLogId
          },
          title: this.$t('mould.transferMold'),
          name: 'mouldChangeEdit'
        }
        this.$emit('tab-add', tab)
      }
    },
    getDetail (mouldCode) {
      this.queryParam.mouldCode = mouldCode
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    }
  }
}
</script>
<style scoped lang="scss">
.mouldScrapEdit {
  height: 100%;
  padding-bottom: 50px;

  :deep(.table-wrapper) {
    padding-left: 0;
    padding-right: 0;
  }

  .sub_header {
    padding: 4px 11px;
    background: #eee;
  }

  .el-table .el-date-editor {
    width: 135px;
  }

  .base-form {
    padding: 15px 30px 0;
  }

  .toRequired {
    color: #ff4949;
    padding-right: 2px;
  }

  .edit_cond {
    color: #23adf4;
    cursor: pointer;
  }
}
</style>
