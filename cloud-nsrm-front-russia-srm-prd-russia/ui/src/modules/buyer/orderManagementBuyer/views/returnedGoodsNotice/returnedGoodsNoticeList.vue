<template>
  <el-container class="flex-container the_returnedGoodsNoticeList_wrapper" direction="vertical">
    <el-main>
      <FormWrapper :form-array="preArr" form-label-width="120px" @getFormData="getQuerydata" />

      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <el-button type="primary" @click="suppleDelivery('add')">
            {{
              $t('orderMod.buyerOrderSynergy.suppleDelivery')
            }}
          </el-button>
          <el-button :disabled="!currentRows.length" @click="delReturnGoods('mutil')">
            {{ $t('common.delete') }}
          </el-button>
        </template>
      </MainHeader>
      <TableView :ref="gridId" :table-header="tableHeader" :check-change="handleCurrentChange" :page-size="pageSize"
        :checkbox="true" :pre-query-data="queryParam" :open-custom-table="true" :comActive="$attrs['changeTab']"
        url="/api-sup-ce/po/returnOrder/listPage" :reserve-selection="true" row-key="returnOrderId" />
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { tabTodoMixin, tabTodoWatch } from '@/utils/mixins'
import returnedGoodsNoticeDetail from './returnedGoodsNoticeDetail'
import { parseTime, adaptDictData } from '@/utils'
import { getDictItemList } from '@/api/common'

export default {
  name: 'ReturnedGoodsNoticeList',
  components: {
    TableView,
    MainHeader,
    FormWrapper
  },
  mixins: [tabTodoMixin, tabTodoWatch],
  provide() {
    return { context: this }
  },
  data() {
    return {
      gridId: 'list',
      pageSize: 15,
      currentRows: [],
      tableHeader: [],
      defaultTableHeader: [],
      preArr: [
        {
          prop: 'organizationId',
          label: () => this.$t('oneStopShopping.businessEntity'),
          type: 'OUorganizationSelector'
        },
        {
          prop: 'vendorName',
          label: () => this.$t('orderMod.buyerOrderSynergy.vendorName'),
          type: 'quicksearch',
          showKey: 'companyName',
          name: 'scc_sup_company_info_all'
        },
        {
          prop: 'returnOrderNumber',
          label: () => this.$t('orderMod.buyerOrderSynergy.returnOrderNumber')
        },
        {
          prop: 'materialCode',
          label: () => this.$t('orderMod.buyerOrderSynergy.materialName'),
          type: 'quicksearch',
          showKey: 'materialName',
          propKey: 'materialCode',
          name: 'scc_base_material_item'
        },
        {
          prop: 'orderNumber',
          label: () => this.$t('orderMod.buyerOrderSynergy.orderNumber')
        },
        {
          prop: 'deliveryNumber',
          label: () => this.$t('orderMod.buyerOrderSynergy.deliveryNumber')
        },
        {
          prop: 'returnStatus',
          label: () => this.$t('orderMod.buyerOrderSynergy.returnStatus'),
          type: 'dict',
          code: 'RETURN_ORDER_STATUS'
        }
      ],
      queryParam: {}
    }
  },
  created() {
    this.tableHeader = [
      {
        // 退货单号
        prop: 'returnOrderNumber',
        label: () => this.$t('orderMod.buyerOrderSynergy.returnOrderNumber'),
        minWidth: 130,
        showType: 'buttons',
        btnStyle: 'text',
        buttons: [
          {
            callback: (row) => this.suppleDelivery('view', row),
            show: (row) => true,
            formattor: (val, row) => row.returnOrderNumber
          }
        ]
      },
      {
        // 供应商名称
        prop: 'vendorName',
        label: () => this.$t('orderMod.buyerOrderSynergy.vendorName'),
        width: 150
      },
      {
        // 供应商编码
        prop: 'vendorCode',
        label: () => this.$t('orderMod.buyerOrderSynergy.vendorCode'),
        minWidth: 150
      },
      {
        // 业务实体
        prop: 'organizationName',
        label: () => this.$t('purchaseDemand.businessEntity'),
        minWidth: 150
      },
      {
        // 退货单状态
        prop: 'returnStatus',
        label: () => this.$t('orderMod.buyerOrderSynergy.returnStatus'),
        dataType: 'dict',
        code: 'RETURN_ORDER_STATUS',
        minWidth: 120
      },
      {
        // 备注
        label: () => this.$t('purchaseDemand.comments1'),
        prop: 'comments',
        minWidth: 100
      },
      {
        label: () => this.$t('oneStopShopping.refusedReason'),
        prop: 'rejectReason',
        width: 100
      },
      {
        // 创建人
        label: () => this.$t('orderMod.buyerOrderSynergy.createdBy'),
        prop: 'createdUserName',
        minWidth: 100
      },
      {
        // 创建时间
        label: () => this.$t('orderMod.buyerOrderSynergy.creationDate'),
        prop: 'creationDate',
        minWidth: 100,
        formattor: (val) => (val ? parseTime(val, '{y}-{m}-{d}') : '')
      },
      {
        // 最后更新人
        label: () => this.$t('orderMod.buyerOrderSynergy.lastUpdateBy'),
        prop: 'createdUserName', // createdBy
        minWidth: 120
      },
      {
        // 最后更新时间
        label: () => this.$t('orderMod.buyerOrderSynergy.lastUpdateDate'),
        prop: 'lastUpdateDate',
        minWidth: 120,
        formattor: (val) => (val ? parseTime(val, '{y}-{m}-{d}') : '')
      },
      {
        // 操作
        prop: 'operation',
        label: () => this.$t('common.operation'),
        minWidth: 100,
        btnStyle: 'text',
        fixed: 'right',
        showType: 'buttons',
        buttons: [
          {
            // 编辑
            callback: (row) => this.suppleDelivery('edit', row),
            formattor: () => this.$t('common.edit'),
            show: (row) => row.returnStatus === 'DRAFT'
          },
          {
            // 删除
            callback: (row) => this.delReturnGoods('one', [row]),
            formattor: () => this.$t('common.delete'),
            show: (row) => row.returnStatus === 'DRAFT'
          }
        ]
      }
    ]
    this.defaultTableHeader = this.tableHeader
  },
  mounted() {
    this.getQuerydata()
  },
  methods: {
    getQuerydata(v) {
      this.queryParam = v
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    handleCurrentChange(val) {
      this.currentRows = val
    },
    suppleDelivery(type, row) {
      let title =
        type === 'add'
          ? this.$t('orderMod.buyerOrderSynergy.addGoodsReturned')
          : type === 'edit'
            ? this.$t('common.edit') + ' - ' + row.returnOrderNumber
            : this.$t('common.view') + ' - ' + row.returnOrderNumber
      // 编辑tab
      const tab = {
        component: returnedGoodsNoticeDetail,
        params: { flag: type },
        title: title,
        name: 'returnedGoodsNoticeDetail'
      }
      if (type === 'edit' || type === 'view') {
        tab.params.row = row
        tab.name = tab.name + type + row.returnOrderId
      }
      this.$emit('tab-add', tab)
    },
    async delReturnGoods(type, rows) {
      const sign = await this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
      if (sign !== 'confirm') return

      let list = type === 'mutil' ? this.currentRows : rows
      let count = 0
      list.forEach((item) => {
        if (item.returnStatus != 'DRAFT') {
          count++
        }
      })
      this.$nextTick(() => {
        if (count > 0) {
          this.$message({
            type: 'warning',
            message: this.$t('purchaseDemand.have') + count + this.$t('orderMod.msgOrder[33]')
          })
        } else {
          let params = list.map((i) => i.returnOrderId)
          this.$http({
            url: '/api-sup-ce/po/returnOrder/batchDelete',
            method: 'POST',
            data: params,
            loading: true
          }).then((res) => {
            this.$message({
              type: 'success',
              message: this.$t('common.successDelete')
            })
            this.getQuerydata()
          })
        }
      })
    }
  }
}
</script>
<style scoped lang="scss"></style>
