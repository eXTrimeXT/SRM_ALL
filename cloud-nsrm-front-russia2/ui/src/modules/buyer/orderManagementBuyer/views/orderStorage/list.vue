<template>
  <el-container class="flex-container the_orderStorageList_wrapper" direction="vertical">
    <el-main>
      <FormWrapper :form-array="preArr" @getFormData="getQuerydata" />

      <MainHeader>
        <template slot="left">
          <el-button type="primary" @click="handleStorage('add')">
            {{ $t('common.add') }}
          </el-button>
          <el-button :disabled="!currentRows.length" @click="delStorage('mutil')">
            {{
              $t('common.delete')
            }}
          </el-button>
          <el-button :disabled="!currentRows.length" @click="doConfirmStorage('mutil')">
            {{ $t('orderMod.buyerOrderSynergy.confirmStorage') }}
          </el-button>
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        row-key="warehouseReceiptId"
        open-custom-table
        :pre-query-data="queryParam"
        :table-header="tableHeader"
        :page-size="pageSize"
        checkbox
        :check-change="handleCurrentChange"
        :comActive="$attrs['changeTab']"
        url="/api-sup-ce/po/warehouseReceipt/listPage"
        @afterQuery="afterQuery"
      />
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { parseTime, getValidateFailureSequence } from '@/utils'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import orderStorageDetail from './detail'

export default {
  name: 'OrderStorageListBuyer',
  components: {
    TableView,
    MainHeader,
    FormWrapper
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      gridId: 'list',
      pageSize: 15,
      currentRows: [],
      preArr: [
        {
          prop: 'orgId',
          label: () => this.$t('oneStopShopping.businessEntity'), // 业务实体
          type: 'OUorganizationSelector'
        },
        {
          prop: 'vendorName',
          label: () => this.$t('orderMod.buyerOrderSynergy.vendorName'), // 供应商名称
          type: 'quicksearch',
          showKey: 'companyName',
          name: 'scc_sup_company_info_all'
        },
        {
          prop: 'orderNumber',
          label: () => this.$t('orderMod.buyerOrderSynergy.orderNumber') // 采购订单编号
        },
        {
          prop: 'materialCode',
          label: () => this.$t('orderMod.buyerOrderSynergy.materialName'), // 物料名称
          type: 'quicksearch',
          showKey: 'materialName',
          propKey: 'materialCode',
          name: 'scc_base_material_item'
        },
        {
          prop: 'warehouseReceiptNumber',
          label: () => this.$t('orderMod.buyerOrderSynergy.warehouseReceiptNumber') // 入库单号
        },
        {
          prop: 'deliveryNumber',
          label: () => this.$t('orderMod.buyerOrderSynergy.deliveryNumber') // 送货单号
        },
        {
          prop: 'warehouseReceiptStatus',
          label: () => this.$t('orderMod.buyerOrderSynergy.warehouseReceiptStatus'), // 入库状态
          type: 'dict',
          code: 'WAREHOUSE_RECEIPT_STATUS'
        }
      ],
      queryParam: {},
      tableHeader: [
        {
          prop: 'warehouseReceiptNumber',
          label: () => this.$t('orderMod.buyerOrderSynergy.warehouseReceiptNumber'), // 入库单号
          width: 150,
          showType: 'button',
          btnStyle: 'text',
          callback: (row) => this.handleStorage('view', row)
        },
        {
          prop: 'vendorName',
          label: () => this.$t('orderMod.buyerOrderSynergy.vendorName'), // 供应商名称
          width: 120
        },
        {
          prop: 'vendorCode',
          label: () => this.$t('orderMod.buyerOrderSynergy.vendorCode'), // 供应商编码
          width: 120
        },
        {
          prop: 'orgName',
          label: () => this.$t('oneStopShopping.businessEntity'), // 业务实体
          width: 120
        },
        {
          prop: 'receiveAddress',
          label: () => this.$t('oneStopShopping.receiveAddress'), // 收货地址
          width: 120
        },
        {
          prop: 'warehouseReceiptStatus',
          label: () => this.$t('orderMod.buyerOrderSynergy.status'), // 状态
          width: 100,
          dataType: 'dict',
          code: 'WAREHOUSE_RECEIPT_STATUS'
        },
        { prop: 'comments', label: () => this.$t('common.remark'), width: 100 }, // 备注
        {
          label: () => this.$t('orderMod.buyerOrderSynergy.createdBy'), // 创建人
          prop: 'createdUserName',
          width: 100
        },
        {
          label: () => this.$t('orderMod.buyerOrderSynergy.creationDate'), // 创建日期
          prop: 'creationDate',
          width: 100,
          formattor: (val) => (val ? parseTime(val, '{y}-{m}-{d}') : '')
        },
        {
          label: () => this.$t('orderMod.buyerOrderSynergy.lastUpdateBy'), // 最后更新人
          prop: 'lastUpdatedUserName',
          width: 120
        },
        {
          label: () => this.$t('orderMod.buyerOrderSynergy.lastUpdateDate'), // 最后更新日期
          prop: 'lastUpdateDate',
          width: 120,
          formattor: (val) => (val ? parseTime(val, '{y}-{m}-{d}') : '')
        },
        {
          prop: 'operation',
          label: () => this.$t('common.operation'), // 操作
          width: 130,
          btnStyle: 'text',
          fixed: 'right',
          showType: 'buttons',
          buttons: [
            {
              callback: (row) => this.delStorage(row),
              formattor: () => this.$t('common.delete'), // 删除
              show: (row) =>
                row.warehouseReceiptStatus === 'DRAFT' ||
                row.warehouseReceiptStatus === 'WAITING_CONFIRM'
            },
            {
              callback: (row) => this.handleStorage('edit', row),
              formattor: () => this.$t('common.edit'), // 编辑
              show: (row) => row.warehouseReceiptStatus === 'DRAFT'
            },
            {
              callback: (row) => this.doConfirmStorage(row),
              formattor: () => this.$t('common.affirm'), // 确认
              show: (row) => row.warehouseReceiptStatus === 'WAITING_CONFIRM'
            }
          ]
        }
      ]
    }
  },
  mounted () {
    this.getQuerydata()
  },
  methods: {
    handleCurrentChange (val) {
      this.currentRows = val
    },
    getQuerydata (v) {
      this.queryParam = v
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    afterQuery (data) {
      this.$refs[this.gridId].setTableData(async tableData => {
        tableData.forEach((row, index) => {
          row.sequence = index + 1
        })
      })
    },
    // 确认入库
    doConfirmStorage (typeOrRow) {
      let data = []
      if (typeOrRow === 'mutil') {
        let sequences = getValidateFailureSequence(this.currentRows, 'sequence', row => row.warehouseReceiptStatus !== 'WAITING_CONFIRM')
        if (sequences) {
          // 只有待确认状态的单据才可以确认入库，序号x不可以确认入库
          return this.$message.warning(`${this.$t('orderStorage.prompt4')}${sequences}${this.$t('orderStorage.prompt5')}`)
        }
        data = this.currentRows.map((i) => i.warehouseReceiptId)
      } else {
        data = [typeOrRow.warehouseReceiptId]
      }

      this.$http({
        url: '/api-sup-ce/po/warehouseReceipt/batchConfirm',
        method: 'POST',
        data,
        loading: true
      }).then(res => {
        this.$message.success(this.$t('orderMod.confirmSuccess'))
        this.getQuerydata()
        this.currentRows = []
      })
    },
    // 删除单据
    async delStorage (typeOrRow) {
      let data = []
      if (typeOrRow === 'mutil') {
        let sequences = getValidateFailureSequence(this.currentRows, 'sequence', row => row.warehouseReceiptStatus !== 'DRAFT' && row.warehouseReceiptStatus !== 'WAITING_CONFIRM')
        if (sequences) {
          // 只有拟定和待确认状态的单据才可以删除，序号x不可以删除
          return this.$message.warning(`${this.$t('orderStorage.prompt6')}${sequences}${this.$t('orderStorage.prompt7')}`)
        }
        data = this.currentRows.map((i) => i.warehouseReceiptId)
      } else {
        data = [typeOrRow.warehouseReceiptId]
      }

      const sign = await this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
      if (sign !== 'confirm') return

      this.$http({
        url: '/api-sup-ce/po/warehouseReceipt/batchDelete',
        method: 'POST',
        data,
        loading: true
      }).then((res) => {
        this.$message.success(this.$t('common.successDelete'))
        this.getQuerydata()
        this.currentRows = []
      })
    },

    // 新增、编辑、查看单据
    handleStorage (flag, row) {
      let tab = {
        ctrlHeight: true,
        component: orderStorageDetail,
        params: { flag, row },
        title: this.$t('orderMod.editStorageDetail') + (row ? row.warehouseReceiptNumber : ''),
        name: 'orderStorageDetail' + (row ? row.warehouseReceiptId : '')
      }
      this.$emit('tab-add', tab)
    }
  }
}
</script>
