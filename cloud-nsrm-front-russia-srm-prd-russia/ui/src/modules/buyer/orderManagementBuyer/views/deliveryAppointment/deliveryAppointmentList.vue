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
            :disabled="!currentRows.length"
            type="primary"
            @click="handleAccept('mutil')"
          >
            {{ $t('orderMod.accept') }}
          </el-button>
          <el-button
            :disabled="!currentRows.length"
            @click="handleReject('mutil')"
          >
            {{ $t('common.refused') }}
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
        url="/api-sup-ce/po/deliveryAppoint/listPage"
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

export default {
  name: 'DeliveryAppointmentListBuyer',
  components: {
    TableView,
    MainHeader,
    FormWrapper
  },
  provide () {
    return { context: this }
  },
  mixins: [tabTodoWatch, tabTodoMixin],
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
          prop: 'vendorId',
          label: () => this.$t('common.vendorName'), // 供应商名称
          type: 'quicksearch',
          showKey: 'companyName',
          propKey: 'companyId',
          name: 'scc_sup_company_info_all'
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
          prop: 'createdUserName', // createdBy
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
          width: 120,
          btnStyle: 'text',
          fixed: 'right',
          showType: 'buttons',
          buttons: [
            {
              callback: (row) => this.handleAccept('one', [row]),
              formattor: () => this.$t('orderMod.accept'),
              show: (row) => row.deliveryAppointStatus === 'WAITING_CONFIRM'
            },
            {
              callback: (row) => this.handleReject('one', [row]),
              formattor: () => this.$t('common.refused'),
              show: (row) => row.deliveryAppointStatus === 'WAITING_CONFIRM'
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
  activated () {
    this.$refs[this.gridId].doLayout()
  },
  methods: {
    getQuerydata (v) {
      this.queryParam = v
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    handleCurrentChange (val) {
      this.currentRows = val
    },
    handleAccept (type, rows) {
      let list = type === 'mutil' ? this.currentRows : rows
      let unDel = 0
      list.forEach((item) => {
        if (item.deliveryAppointStatus != 'WAITING_CONFIRM') {
          unDel++
        }
      })
      this.$nextTick(() => {
        if (unDel <= 0) {
          const selectData = list.map((i) => i.deliveryAppointId)
          this.$http({
            url: '/api-sup-ce/po/deliveryAppoint/confirmBatch',
            method: 'POST',
            data: selectData,
            loading: true
          })
            .then((data) => {
              this.$message.success(this.$t('common.success'))
              this.getQuerydata()
            })
        } else {
          this.$message({
            type: 'warning',
            message: this.$t('purchaseDemand.have') + unDel + this.$t('orderMod.msgOrder[20]')
          })
        }
      })
    },
    handleReject (type, rows) {
      let list = type === 'mutil' ? this.currentRows : rows
      let unDel = 0
      list.forEach((item) => {
        if (item.deliveryAppointStatus != 'WAITING_CONFIRM') {
          unDel++
        }
      })
      this.$nextTick(() => {
        if (unDel <= 0) {
          this.$prompt(this.$t('orderMod.msgRufuseReason'), this.$t('common.tips'), {
            confirmButtonText: this.$t('common.confirm'),
            cancelButtonText: this.$t('common.cancel'),
            inputPattern: /\S{1,}/,
            inputErrorMessage: this.$t('orderMod.refuseReasonRequire')
          })
            .then(({ value }) => {
              let selectData = list.map((i) => i.deliveryAppointId)
              this.$http({
                url: '/api-sup-ce/po/deliveryAppoint/refuseBatch',
                method: 'POST',
                data: {
                  ids: selectData,
                  refusedReason: value
                }
              }).then((res) => {
                this.$message.success(this.$t('common.success'))
                this.getQuerydata()
              })
            })
            .catch(() => {
              this.$message({
                type: 'info',
                message: this.$t('orderMod.cancelRefuse')
              })
            })
        } else {
          this.$message({
            type: 'warning',
            message: this.$t('purchaseDemand.have') + unDel + this.$t('orderMod.msgOrder[21]')
          })
        }
      })
    },
    viewDelivery (row) {
      // 编辑tab
      let tab = {
        component: deliveryAppointmentDetail,
        params: { flag: 'view', row },
        title: this.$t('orderMod.deliveryAppointmentReceipt') + row.deliveryAppointNumber,
        name: 'deliveryAppointmentDetail_buyer' + row.deliveryAppointId
      }
      this.$emit('tab-add', tab)
    },
    printOne () {},
    exportOne () {}
  }
}
</script>
<style scoped lang="scss"></style>
