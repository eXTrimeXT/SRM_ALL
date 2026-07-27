<template>
  <el-container
    class="flex-container the_vendorPurchaseOrderList_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="preArr"
        form-label-width="120px"
        @getFormData="getQuerydata"
      />
      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <el-button
            type="primary"
            @click="addDelivery"
          >
            {{
              $t('orderMod.addDelivery')
            }}
          </el-button>
          <el-button
            :disabled="!currentRows.length"
            @click="handleSubmit('mutil')"
          >
            {{ $t('common.submit') }}
          </el-button>
          <el-button
            :disabled="!currentRows.length"
            @click="handleDelete('mutil')"
          >
            {{ $t('common.delete') }}
          </el-button>
          <!-- <el-button @click="exportOne">Excel导出</el-button> -->
          <!-- <el-button @click="printOne">打印</el-button> -->
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-header="tableHeader"
        :check-change="handleCurrentChange"
        :page-size="pageSize"
        :checkbox="true"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        url="/api-sup-ce/order/deliveryAppoint/listPage"
        :reserve-selection="true"
        row-key="deliveryAppointId"
      />
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import deliveryAppointmentDetail from './deliveryAppointmentDetail'
import { parseTime, adaptDictData } from '@/utils'
import { getDictItemList } from '@/api/common'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import { deliveryAppointmentsApi } from 'mods@/orderManagementSupplier/api'

export default {
  name: 'DeliveryAppointmentList',
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
          label: () => this.$t('quota.org'), // 业务实体
          type: 'OUorganizationSelector'
        },
        {
          prop: 'organizationId',
          parentId: 'orgId',
          label: () => this.$t('purchaseDemand.invOrg'), // 库存组织
          type: 'INVorganizationSelector'
        },
        {
          prop: 'deliveryAppointNumber',
          label: () => this.$t('orderMod.buyerOrderSynergy.deliveryAppointNumber')
        },
        {
          prop: 'deliveryNumber',
          label: () => this.$t('orderMod.buyerOrderSynergy.deliveryNumber')
        },
        {
          prop: 'deliveryAppointStatus',
          label: () => this.$t('common.status'),
          type: 'dict',
          code: 'DELIVERY_APPOINT_STATUS'
        }
      ],
      tableHeader: [
        {
          prop: 'deliveryAppointNumber',
          label: () => this.$t('orderMod.buyerOrderSynergy.deliveryAppointNumber'),
          minWidth: 150,
          showType: 'button',
          btnStyle: 'text',
          callback: (row) => this.viewDelivery(row)
        },
        {
          prop: 'orgName',
          label: () => this.$t('oneStopShopping.businessEntity'),
          minWidth: 150
        },
        {
          prop: 'organizationName',
          label: this.$t('bid_mod.inv'), // 库存组织
          minWidth: 150
        },
        {
          prop: 'receiveAddress',
          label: () => this.$t('oneStopShopping.receiveAddress'),
          width: 150
        },
        {
          prop: 'vendorName',
          label: this.$t('common.vendorName'), // 供应商
          minWidth: 150
        },
        {
          prop: 'entryTime',
          width: 100,
          label: () => this.$t('orderMod.buyerOrderSynergy.entryTime'),
          formattor (val) {
            return val ? parseTime(val, '{y}-{m}-{d}') : ''
          }
        },
        {
          prop: 'respondents',
          width: 100,
          label: () => this.$t('orderMod.buyerOrderSynergy.respondents')
        },
        {
          prop: 'deliveryLocation',
          width: 150,
          label: () => this.$t('orderMod.deliveryLocation')
        },
        {
          prop: 'licensePlate',
          width: 100,
          label: () => this.$t('orderMod.buyerOrderSynergy.licensePlate')
        },
        {
          prop: 'carType',
          width: 100,
          label: () => this.$t('orderMod.buyerOrderSynergy.carType'),
          dataType: 'dict',
          code: 'CAR_TYPE'
        },
        {
          prop: 'comments',
          minWidth: 120,
          label: () => this.$t('orderMod.buyerOrderSynergy.comments')
        },
        {
          prop: 'deliveryAppointStatus',
          width: 100,
          label: () => this.$t('orderMod.buyerOrderSynergy.status'),
          dataType: 'dict',
          code: 'DELIVERY_APPOINT_STATUS'
        },
        {
          label: () => this.$t('oneStopShopping.refusedReason'),
          prop: 'refusedReason',
          width: 100
        },
        {
          label: () => this.$t('orderMod.buyerOrderSynergy.createdBy'),
          prop: 'createdUserName', // createdBy
          width: 100
        },
        {
          label: () => this.$t('orderMod.buyerOrderSynergy.creationDate'),
          prop: 'creationDate',
          width: 100,
          formattor: (val) => (val ? parseTime(val, '{y}-{m}-{d}') : '')
        },
        {
          label: () => this.$t('orderMod.buyerOrderSynergy.lastUpdateBy'),
          prop: 'lastUpdatedUserName', // lastUpdatedBy
          width: 120
        },
        {
          label: () => this.$t('orderMod.buyerOrderSynergy.lastUpdateDate'),
          prop: 'lastUpdateDate',
          width: 120,
          formattor: (val) => (val ? parseTime(val, '{y}-{m}-{d}') : '')
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
              callback: (row) => this.editDelivery(row),
              formattor: () => this.$t('common.edit'),
              show: (row) => ['DRAFT', 'REJECT'].includes(row.deliveryAppointStatus)
            },
            {
              callback: (row) => this.handleSubmit('one', [row]),
              formattor: () => this.$t('common.submit'),
              show: (row) => ['DRAFT', 'REJECT'].includes(row.deliveryAppointStatus)
            },
            {
              callback: (row) => this.handleDelete('one', [row]),
              formattor: () => this.$t('common.delete'),
              show: (row) => row.deliveryAppointStatus === 'DRAFT'
            }
          ]
        }
      ],
      queryParam: {}
    }
  },
  created () {},
  mounted () {
    this.getQuerydata()
  },
  methods: {
    viewDelivery (row) {
      // 编辑tab
      let tab = {
        component: deliveryAppointmentDetail,
        params: { flag: 'view', row },
        title: this.$t('orderMod.deliveryAppointmentReceipt') + row.deliveryAppointNumber,
        name: 'deliveryAppointmentDetail' + row.deliveryAppointId
      }
      this.$emit('tab-add', tab)
    },
    getQuerydata (v) {
      this.queryParam = v
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    handleCurrentChange (val) {
      this.currentRows = val
    },
    printOne () {},
    exportOne () {},
    addDelivery () {
      let tab = {
        component: deliveryAppointmentDetail,
        params: { flag: 'add' },
        title: this.$t('orderMod.addAppointmentDeliveryNote2'),
        name: 'deliveryAppointmentDetail'
      }
      this.$emit('tab-add', tab)
    },
    editDelivery (row) {
      // 编辑tab
      let tab = {
        component: deliveryAppointmentDetail,
        params: { flag: 'edit', row },
        title: this.$t('orderMod.deliveryAppointmentReceipt') + row.deliveryAppointNumber,
        name: 'deliveryAppointmentDetail' + row.deliveryAppointId
      }
      this.$emit('tab-add', tab)
    },
    // 提交
    handleSubmit (type, rows) {
      let list = type === 'mutil' ? this.currentRows : rows
      let unDel = 0
      list.forEach((item) => {
        if (item.deliveryAppointStatus != 'DRAFT') {
          unDel++
        }
      })
      this.$nextTick(() => {
        if (unDel <= 0) {
          const params = list.map((i) => i.deliveryAppointId)
          deliveryAppointmentsApi.deliveryAppointSubmit(params).then((res) => {
            this.$message({
              message: this.$t('common.successSubmit'),
              type: 'success'
            })
            this.getQuerydata()
          })
        } else {
          this.$message({
            type: 'warning',
            message: this.$t('purchaseDemand.have') + unDel + this.$t('orderMod.msgVendorOrder[12]')
          })
        }
      })
    },
    // 删除
    async handleDelete (type, rows) {
      const sign = await this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
      if (sign !== 'confirm') return

      let list = type === 'mutil' ? this.currentRows : rows
      let unDel = 0
      list.forEach((item) => {
        if (item.deliveryAppointStatus != 'DRAFT') {
          unDel++
        }
      })
      this.$nextTick(() => {
        if (unDel <= 0) {
          const params = list.map((i) => i.deliveryAppointId)
          this.$http({
            url: '/api-sup-ce/order/deliveryAppoint/batchDelete',
            method: 'POST',
            data: params
          }).then((res) => {
            this.$message.success(this.$t('common.successDelete'))
            this.getQuerydata()
          })
        } else {
          this.$message({
            type: 'warning',
            message: this.$t('purchaseDemand.have') + unDel + this.$t('orderMod.msgVendorOrder[13]')
          })
        }
      })
    }
  }
}
</script>
<style scoped lang="scss"></style>
