<template>
  <el-container
    class="flex-container-notab the_buyerDeliveryNotice_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="queryForm"
        @getFormData="getQuerydata"
      />

      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <AuthorityButton
            code="po:buyerDeliveryNotice:add"
            type="primary"
            @click="addOrder"
          >
            {{ $t('orderMod.createDeliveryBill') }}
          </AuthorityButton>
          <ExportExcel
            type="default"
            :page-url="tableUrl"
            :filter-params="queryParam"
            :table-header="tableHeader"
            :dict-codes="dictCodes"
            timeout="1000000"
            export-mode="front"
          />
          <AuthorityButton
            code="po:buyerDeliveryNotice:delete"
            :disabled="!currentRows.length"
            @click="deleteNotice('mutil', currentRows)"
          >
            {{ $t('orderMod.batchDelete') }}
          </AuthorityButton>
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-header="tableHeader"
        :check-change="handleCurrentChange"
        :page-size="15"
        :checkbox="true"
        :pre-query-data="queryParam"
        :url="tableUrl"
        :open-custom-table="true"
        :reserve-selection="true"
        row-key="deliveryNoticeId"
        customTableKey="deliveryNoticeList"
      />
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import ExportExcel from 'lib@/components/export-excel'
import deliveryNoticeDetail from './deliveryNoticeDetail'
import { parseTime } from '@/utils'
import { tabTodoWatch } from '@/utils/mixins'

export default {
  name: 'DeliveryNoticeList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    ExportExcel,
    deliveryNoticeDetail
  },
  mixins: [tabTodoWatch],
  data () {
    return {
      tableUrl: '/api-sup-ce/po/deliveryNotice/listPageDeliveryNotice',
      dictCodes: {
        status: 'DELIVERY_NOTICE_STATUS_NEW' // 单据状态
      },
      gridId: 'list',
      currentRows: [],
      queryForm: [
        {
          prop: 'orgId',
          label: () => this.$t('dataConfMod.orgId'), // 业务实体
          type: 'OUorganizationSelector'
        },
        {
          prop: 'organizationId',
          label: () => this.$t('dataConfMod.organizationId'), // 库存组织
          type: 'INVorganizationSelector',
          parentId: 'orgId'
        },
        {
          prop: 'deliveryNoticeNumber',
          label: () => this.$t('orderMod.buyerOrderSynergy.deliveryNoticeNum')
        },
        {
          prop: 'vendorName',
          label: () => this.$t('orderMod.buyerOrderSynergy.vendorName'),
          type: 'quicksearch',
          showKey: 'companyName',
          name: 'scc_sup_company_info_all'
        },
        {
          prop: 'orderNumber',
          label: () => this.$t('orderMod.buyerOrderSynergy.orderNumber')
        },
        {
          prop: 'status',
          label: () => this.$t('orderMod.deliveryNoteStatus'),
          type: 'dict',
          code: 'DELIVERY_NOTICE_STATUS_NEW'
        },
        // 创建日期
        {
          prop: 'dateList',
          label: () => this.$t('quota.createdDate'),
          type: 'daterange'
        }
      ],
      tableHeader: [
        {
          prop: 'deliveryNoticeNumber',
          showType: 'button',
          btnStyle: 'text',
          label: () => this.$t('orderMod.buyerOrderSynergy.deliveryNoticeNum'),
          minWidth: 170,
          callback: row => this.readOne(row)
        },
        {
          prop: 'orgName',
          label: () => this.$t('oneStopShopping.businessEntity'),
          minWidth: 150
        },
        {
          prop: 'organizationName',
          label: () => this.$t('purchaseDemand.invOrg'),
          minWidth: 150
        },
        {
          label: () => this.$t('common.vendor'),
          prop: 'vendorName',
          minWidth: 150
        },
        {
          label: () => this.$t('orderMod.deliveryNoteStatus'), // 单据状态
          prop: 'status',
          width: 120,
          dataType: 'dict',
          code: 'DELIVERY_NOTICE_STATUS_NEW'
        },
        {
          label: () => this.$t('orderMod.buyerOrderSynergy.createdBy'), // 创建人
          prop: 'createdUserName',
          width: 100
        },
        {
          label: () => this.$t('orderMod.buyerOrderSynergy.creationDate'),
          prop: 'creationDate',
          width: 100,
          formattor: val => (val ? parseTime(val, '{y}-{m}-{d}') : '')
        },
        {
          label: () => this.$t('common.updatePeople'),
          prop: 'lastUpdatedUserName',
          width: 100
        },
        {
          label: () => this.$t('common.lastUpdateDate'),
          prop: 'lastUpdateDate',
          width: 120,
          formattor: val => (val ? parseTime(val, '{y}-{m}-{d}') : '')
        },
        {
          label: () => this.$t('orderMod.confirmDate'),
          prop: 'confirmDate',
          width: 120,
          formattor: val => (val ? parseTime(val, '{y}-{m}-{d}') : '')
        },
        {
          label: () => this.$t('orderMod.refuseReason'),
          prop: 'refuseReason',
          width: 100
        },
        {
          prop: 'operation',
          label: () => this.$t('common.operation'),
          width: 100,
          btnStyle: 'text',
          fixed: 'right',
          showType: 'buttons',
          buttons: [
            {
              callback: row => this.editNotice(row),
              formattor: () => this.$t('common.edit'),
              code: 'po:buyerDeliveryNotice:edit',
              show: row => ['DRAFT'].includes(row.status)
            },
            {
              callback: row => this.deleteNotice('one', [row]),
              formattor: () => this.$t('common.delete'),
              code: 'po:buyerDeliveryNotice:delete',
              show: row => ['DRAFT'].includes(row.status)
            }
          ]
        }
      ],
      queryParam: {}
    }
  },
  created () {
    this.getQuerydata()
  },
  methods: {
    getQuerydata (obj) {
      const { dateList, ...rest } = obj || this.queryParam
      const params = {}
      if (dateList) {
        params.startCreationDate = dateList[0]
        params.endCreationDate = dateList[1]
      }
      this.queryParam = { ...rest, ...params }
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    // 创建通知单
    addOrder () {
      const tab = {
        component: deliveryNoticeDetail,
        params: {
          flag: 'add',
          row: {}
        },
        ctrlHeight: true,
        title: this.$t('orderMod.createDeliveryBill'),
        name: 'deliveryNoticeDetail'
      }
      this.$emit('tab-add', tab)
    },
    readOne (row) {
      // 查看--只读状态
      const tab = {
        component: deliveryNoticeDetail,
        params: {
          flag: 'readOnly',
          row
        },
        ctrlHeight: true,
        title: row.deliveryNoticeNumber,
        name: 'deliveryNoticeDetail' + row.deliveryNoticeNumber
      }
      this.$emit('tab-add', tab)
    },
    handleCurrentChange (val) {
      this.currentRows = val
    },
    // 编辑
    editNotice (row) {
      const tab = {
        component: deliveryNoticeDetail,
        params: {
          flag: 'edit',
          row
        },
        ctrlHeight: true,
        title: row.deliveryNoticeNumber,
        name: 'deliveryNoticeDetail' + row.deliveryNoticeNumber
      }
      this.$emit('tab-add', tab)
    },
    // 删除
    async deleteNotice (_type, rows) {
      const sign = rows.some(row => row.status !== 'DRAFT')
      if (sign) return this.$message.warning(this.$t('orderMod.selectDraftDataDelete'))
      this.$http({
        url: '/api-sup-ce/po/deliveryNotice/batchDelete',
        method: 'POST',
        data: rows.map(row => row.deliveryNoticeId),
        loading: true
      }).then(_ => {
        this.getQuerydata()
        this.$message.success(this.$t('orderMod.deleteSuccess'))
      })
    }
  }
}
</script>
<style scoped lang="scss">
.the_buyerDeliveryNotice_wrapper {
}
</style>
