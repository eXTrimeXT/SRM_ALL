<template>
  <el-container
    class="flex-container"
    direction="vertical"
  >
    <el-main>
      <form-wrapper
        :form-array="filterConfig"
        @getFormData="getQuerydata"
        @synchronous-value="syncFilterParams"
      >
        <template #status="{ scope }">
          <dict-select
            v-model="scope.status"
            code="CONTRACT_STATE"
          />
        </template>
      </form-wrapper>
      <main-header
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <AuthorityButton
            type="primary"
            @click="addHandle"
          >
            {{ $t("common.add") }}
          </AuthorityButton>
          <export-excel
            style="margin-left: 10px"
            :filter-params="queryParam"
            export-mode="front"
            :table-header="tableHeader"
          />
        </template>
      </main-header>
      <table-view
        :ref="gridId"
        :table-header="tableHeader"
        :check-change="handleCurrentChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        open-custom-table
        checkbox
        :comActive="$attrs['changeTab']"
        :source="$api.cmPerform.buyer.main.performOrder.listPage"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import contractPerformanceBillEdit from './edit.vue'
import ExportExcel from 'lib@/components/export-excel'

export default {
  name: 'ContractPerformanceBillList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    ExportExcel
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      name: 'contractPerformanceBillList',
      tableName: 'contractPerformanceBillList',
      pageSize: 15,
      gridId: 'list',
      filterParams: {},
      currentRows: [],
      tableHeader: [],
      defaultTableHeader: [],
      filterConfig: [
        {
          prop: 'perOrderNo',
          label: this.$t('contract_mod.perOrderNo')
        },
        {
          prop: 'buId',
          label: this.$t('bid_mod.businessEntity'),
          type: 'OUorganizationSelector'
        },
        // {
        //   prop: 'invId',
        //   label: this.$t('bid_mod.inv'),
        //   type: 'INVorganizationSelector',
        //   parentId: 'buId',
        //   multiple: false
        // },
        {
          prop: 'vendorName',
          label: () => this.$t('common.vendorName'), // '供应商名称'
          type: 'quicksearch',
          showKey: 'companyName',
          name: 'scc_sup_company_info_display_buyer'
        },
        {
          prop: 'contractCreatedFullName',
          label: this.$t('common.creator')
        },
        {
          prop: 'contractNo',
          label: this.$t('contractMod.contractNo_1')
        },
        {
          prop: 'contractCreationDateStart',
          label: this.$t('supplierRating.creationStartTime'),
          type: 'date'
        },
        {
          prop: 'contractCreationDateEnd',
          label: this.$t('supplierRating.creationEndTime'),
          type: 'date'
        },
        {
          prop: 'status',
          label: this.$t('orderMod.deliveryNoteStatus'),
          type: 'slot',
          slot: 'status'
        }
      ],
      queryParam: {}
    }
  },
  created () {
    this.tableHeader = [
      {
        prop: 'perOrderNo',
        label: this.$t('contract_mod.perOrderNo'),
        width: 150,
        callback: row => this.viewHandle(row),
        showType: 'button',
        btnStyle: 'text'
      },
      {
        prop: 'status',
        label: this.$t('purchaseDemand.applyStatus'),
        width: 150,
        dataType: 'dict',
        code: 'CONTRACT_STATE'
      },
      {
        prop: 'buName',
        label: this.$t('bid_mod.businessEntity'),
        width: 150
      },
      // {
      //   prop: 'invName',
      //   label: this.$t('bid_mod.inv'),
      //   width: 150
      // },
      {
        prop: 'vendorName',
        label: () => this.$t('common.vendorName'),
        width: 150
      },
      {
        prop: 'vendorCode',
        label: () => this.$t('common.vendorCode'),
        width: 150
      },
      {
        prop: 'contractClass',
        label: this.$t('contract_mod.contractType'),
        width: 150,
        dataType: 'dict',
        code: 'ELEM_CONTRACT_TYPE'
      },
      {
        prop: 'contractNo',
        label: this.$t('contractMod.contractNo_1'),
        width: 150
      },
      {
        prop: 'createdBy',
        label: this.$t('common.creator'),
        width: 150
      },
      {
        prop: 'creationDate',
        label: this.$t('common.creationTime'),
        width: 100,
        formattor: (val) => this.$parseTime(val, '{y}-{m}-{d}')
      },
      {
        prop: 'operation',
        label: this.$t('components.headers.operation'),
        showType: 'buttons',
        btnStyle: 'text',
        fixed: 'right',
        buttons: [
          {
            callback: (row) => this.editHandle(row),
            show: (row) => ['DRAFT', 'REJECTED'].includes(row.status),
            formattor: () => {
              return this.$t('common.edit')
            }
          },
          {
            callback: (row) => this.deleteHandle(row),
            show: (row) => ['DRAFT'].includes(row.status),
            formattor: () => {
              return this.$t('common.delete')
            }
          },
          {
            callback: (row) => this.abandonHandle(row),
            show: (row) => ['REJECTED'].includes(row.status),
            formattor: () => {
              return this.$t('common.abandon')
            }
          },
          {
            callback: (row) => this.approved(row),
            show: (row) => ['TOBEAPPROVED'].includes(row.status),
            formattor: () => {
              return this.$t('common.approve')
            }
          },
          {
            callback: (row) => this.handOver(row),
            show: (row) => ['APPROVAL'].includes(row.status),
            formattor: () => {
              return this.$t('supRisk.deliver')
            }
          }
        ]
      }
    ]
    this.defaultTableHeader = this.tableHeader
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    syncFilterParams (values) {
      this.filterParams = values
    },
    dolayout () {
      // this.$refs[this.gridId].query()
      this.$refs[this.gridId] && this.$refs[this.gridId].doLayout()
    },
    getQuerydata (obj) {
      let objs = obj || this.queryParam
      this.queryParam = { ...objs }
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    viewHandle (row) {
      this.mode = 'view'
      const tab = {
        component: contractPerformanceBillEdit,
        params: {
          row,
          flag: this.mode,
          tabName: 'contractPerformanceBillEditView' + row.perOrderId
        },
        title: `${this.$t('common.view')} - ${row.perOrderNo}`,
        name: 'contractPerformanceBillEditView' + row.perOrderId
      }
      this.$emit('tab-add', tab)
    },
    addHandle (row) {
      this.mode = 'add'
      const tab = {
        component: contractPerformanceBillEdit,
        params: {
          row,
          flag: this.mode,
          tabName: 'contractPerformanceBillEdit'
        },
        title: this.$t('common.add'),
        name: 'contractPerformanceBillEdit'
      }
      this.$emit('tab-add', tab)
    },
    editHandle (row) {
      this.mode = 'edit'
      const tab = {
        component: contractPerformanceBillEdit,
        params: {
          row,
          flag: this.mode,
          tabName: 'contractPerformanceBillEdit' + row.perOrderId
        },
        title: `${this.$t('common.edit')} - ${row.perOrderNo}`,
        name: 'contractPerformanceBillEdit' + row.perOrderId
      }
      this.$emit('tab-add', tab)
    },
    handOver (row) {
      this.mode = 'handOver'
      const tab = {
        component: contractPerformanceBillEdit,
        params: {
          row,
          flag: this.mode,
          tabName: 'contractPerformanceBillHandOver' + row.perOrderId
        },
        title: `${this.$t('supRisk.deliver')} - ${row.perOrderNo}`,
        name: 'contractPerformanceBillHandOver' + row.perOrderId
      }
      this.$emit('tab-add', tab)
    },
    approved (row) {
      this.mode = 'approved'
      const tab = {
        component: contractPerformanceBillEdit,
        params: {
          row,
          flag: this.mode,
          tabName: 'contractPerformanceBillApproved' + row.perOrderId
        },
        title: `${this.$t('common.approve')} - ${row.perOrderNo}`,
        name: 'contractPerformanceBillApproved' + row.perOrderId
      }
      this.$emit('tab-add', tab)
    },
    abandonHandle (row) {
      this.$confirm(this.$t('contract_mod.sureChangeCareers'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          this.$api.cmPerform.buyer.main.performOrder.abandonPerOrder(row.perOrderId).then((res) => {
            this.$message.success(res.message)
            this.getQuerydata()
          })
        })
        .catch(() => {})
    },
    deleteHandle (row) {
      this.$confirm(this.$t('common.delRow'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          this.$api.cmPerform.buyer.main.performOrder.deletePerOrderById(row.perOrderId).then((res) => {
            this.$message.success(res.message)
            this.getQuerydata()
          })
        })
        .catch(() => {})
    }
  }
}
</script>
