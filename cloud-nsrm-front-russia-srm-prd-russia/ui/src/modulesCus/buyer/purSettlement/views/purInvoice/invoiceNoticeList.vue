<template>
  <el-container
    class="flex-container the_quotationPrices_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="preArr"
        @getFormData="getQuerydata"
      />
      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <!-- 新增 -->
          <!-- <AuthorityButton type="primary" @click="editTab('add')">
            {{ $t("purSettlementMod.newStatement") }}
          </AuthorityButton> -->
          <!-- 批量提交供应商确认 -->
          <AuthorityButton type="primary" @click="handleBatchSubmit">
            {{ $t("cusEntry.orderMod.batchSubmit") }}
          </AuthorityButton>
          <!-- 导出 -->
          <ExportExcel
            type="default"
            :table-header="tableHeader"
            export-mode="front"
            :dict-codes="dictCodes"
            :page-url="queryUrl"
            :filter-params="filterParams"
            fileName="对账单导出文件"
            timeout="1000000"
          />
          <AuthorityButton
            type="primary"
            code="pur:invoice:recall"
            @click="handleBatchRecall"
          >
            {{ $t("cusEntry.orderMod.recall") }}
          </AuthorityButton>
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :bigData="true"
        :checkbox="true"
        :table-data="tableData"
        :table-header="tableHeader"
        :check-change="handleCheckChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :comActive="$attrs['changeTab']"
        :url="queryUrl"
        :adeptMeiQl="true"
        @afterQuery="afterQuery"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import invoiceNoticeDetail from './invoiceNoticeDetail'
import { parseTime } from '@/utils'
import ExportExcel from 'lib@/components/export-excel'
import { transformMQL } from 'lib@/utils/util'

export default {
  name: 'InvoiceNoticeList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    ExportExcel
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      sourceParams: {},
      dictCodes: {
        extStatus: 'INVOICE_NOTICE_STATUS',
        invoiceNoticeStatus: 'INVOICE_NOTICE_STATUS'
      },
      gridId: 'invoiceNoticeList',
      pageSize: 15,
      queryParam: {},
      filterParams: {},
      selectList: [],
      tableHeader: [],
      tableData: [],
      preArr: [
        {
          prop: 'invoiceNoticeNumber',
          label: () => this.$t('purSettlementMod.statementNumber')
        },
        {
          prop: 'invoiceNoticeStatus',
          label: () => this.$t('purSettlementMod.paymentPlanStatus'),
          type: 'dict',
          code: 'INVOICE_NOTICE_STATUS'
        },
        {
          prop: 'orgId',
          label: () => this.$t('quota.org'),
          type: 'OUorganizationSelector'
        },
        {
          prop: 'createdFullName',
          label: () => this.$t('common.creator')
        },
        {
          prop: 'creationDate',
          label: () => this.$t('bidMod.dateCreated'),
          type: 'daterange'
        },
        {
          prop: 'vendorName',
          label: () => this.$t('bidMod.vendorName')
        }
      ],
      queryUrl: '/api-sup-ce/api-ql/InvoiceNotice/query'
    }
  },
  created () {
    let _this = this
    this.tableHeader = [
      {
        prop: 'orgName',
        label: _this.$t('quota.org'),
        width: 120
      },
      {
        prop: 'invoiceNoticeNumber',
        label: _this.$t('purSettlementMod.statementNumber'),
        width: 120,
        showType: 'button',
        btnStyle: 'text',
        callback: row => this.editTab('view', row),
        formattor (val) {
          return val || '--'
        }
      },
      {
        prop: 'extStatus',
        label: _this.$t('purSettlementMod.paymentPlanStatus'),
        width: 100,
        dataType: 'dict',
        code: 'INVOICE_NOTICE_STATUS'
      },
      {
        prop: 'vendorCode',
        label: _this.$t('common.vendorCode'),
        width: 120
      },
      {
        prop: 'vendorName',
        label: _this.$t('bidMod.vendorName'),
        minWidth: 150
      },
      {
        prop: 'ceeaReceiveStartDate',
        label: () => this.$t('purSettlementMod.statementStartTime'),
        width: 150
      },
      {
        prop: 'ceeaReceiveEndDate',
        label: () => this.$t('purSettlementMod.statementEndTime'),
        width: 150
      },
      {
        prop: 'ceeaNoTaxTotalAmount',
        label: () => this.$t('cusEntry.bidMod.ceeaNoTaxTotalAmount'),
        width: 150
      },
      {
        prop: 'ceeaTotalTax',
        label: () => this.$t('cusEntry.bidMod.ceeaTotalTax'),
        width: 150
      },
      {
        prop: 'ceeaTaxTotalAmount',
        label: _this.$t('contractMod.totalAmountTax'),
        width: 120
      },
      {
        prop: 'currencyName',
        label: _this.$t('vendorMod.currencyCode'),
        width: 120
      },
      {
        prop: 'rejectReason',
        label: _this.$t('orderMod.refuseReason'),
        width: 120
      },
      {
        prop: 'createdFullName',
        label: _this.$t('common.creator'),
        width: 100
      },
      {
        prop: 'creationDate',
        label: _this.$t('purSettlementMod.creationDate'),
        width: 100,
        formattor: val => val ? parseTime(val, '{y}-{m}-{d}') : ''
      },
      {
        prop: 'operation',
        label: _this.$t('common.operation'),
        width: 180,
        showType: 'buttons',
        fixed: 'right',
        btnStyle: 'text',
        buttons: [
          {
            callback: row => this.editTab('edit', row),
            formattor: _ => this.$t('common.edit'),
            // 拟定、供应商已拒绝
            show: row => ['DRAFT', 'REFUSE'].includes(row.invoiceNoticeStatus)
          },
          {
            callback: row => this.delRowData(row),
            formattor: _ => this.$t('common.delete'),
            // 拟定、供应商已拒绝
            show: row => ['DRAFT', 'REFUSE'].includes(row.invoiceNoticeStatus)
          }
        ]
      }
    ]
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    // 撤回
    handleBatchRecall () {
      if (this.selectList.length === 0) {
        this.$message.warning(this.$t('cusEntry.tipMessage.selectRecallList'))
        return false
      }
      // 校验状态
      let validStatus = true
      this.selectList.some(item => {
        if (item.extStatus !== 'WAITING') {
          validStatus = false
          return true
        }
      })
      if (!validStatus) {
        this.$message.warning(this.$t('cusEntry.tipMessage.onlyWaiting'))
        return false
      }
      const saveData = transformMQL.save('InvoiceNotice', this.selectList, 'extWithdraw')
      this.$http({
        url: '/api-sup-ce/api-ql/InvoiceNotice/extWithdraw',
        method: 'POST',
        data: saveData,
        loading: true
      }).then(res => {
        this.$message.success(this.$t('common.success'))
        this.getQuerydata(this.sourceParams)
      })
    },
    afterQuery (data) {
      this.tableData = data.map(item => {
        // 对账单状态优先取【扩展状态：extStatus】
        item.invoiceNoticeStatus = item.extStatus || item.invoiceNoticeStatus
        return item
      })
    },
    getQuerydata (v) {
      this.sourceParams = v
      // 【待供方确认、已确认、已拒绝】扩展状态 使用 extStatus 查询, 其他状态使用 invoiceNoticeStatus 查询(此时 extStatus为空)
      let params = {}
      const { invoiceNoticeNumber, invoiceNoticeStatus, orgId, createdFullName, creationDate, vendorName } = v || {}
      if (invoiceNoticeNumber) {
        params.invoiceNoticeNumber = { contains: invoiceNoticeNumber }
      }
      if (vendorName) {
        params.vendorName = { contains: vendorName }
      }
      if (invoiceNoticeStatus && ['WAITING', 'CONFIRM', 'REFUSE'].includes(invoiceNoticeStatus)) {
        params.extStatus = { eq: invoiceNoticeStatus }
      } else if (invoiceNoticeStatus) {
        params.invoiceNoticeStatus = { eq: invoiceNoticeStatus }
        params.extStatus = { isNull: true }
      }
      if (orgId) {
        params.orgId = { eq: orgId }
      }
      if (createdFullName) {
        params.createdFullName = { contains: createdFullName }
      }
      if (creationDate) {
        params.creationDate = { between: creationDate }
      }

      this.queryParam = {
        type: 'InvoiceNotice',
        action: 'query',
        payload: {
          filter: { ...params },
          page: {
            pageNum: 1,
            pageSize: 15,
            sort: 'lastUpdateDate desc'
          }
        },
        query: { '*': {} },
        lang: 'zh-cn',
        tree: true
      }
      this.filterParams = { meiqlPayload: this.queryParam }
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    handleCheckChange (val) {
      this.selectList = val
    },
    // 批量提交供应商确认
    handleBatchSubmit () {
      if (this.selectList.length == 0) {
        this.$message.warning('请选择数据')
        return
      }
      const params = { invoiceNoticeIds: this.selectList.map(item => item.invoiceNoticeId) }
      const saveData = transformMQL.save('InvoiceNotice', [params], 'extSubmit')
      this.$http({
        url: '/api-sup-ce/api-ql/InvoiceNotice/extSubmit',
        method: 'POST',
        data: saveData,
        loading: true
      }).then(res => {
        this.$message.success(this.$t('common.success'))
        this.getQuerydata()
      })
    },
    // 删除
    async delRowData (row) {
      const sign = await this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
      if (sign !== 'confirm') return
      const params = {
        'invoiceNoticeId': row.invoiceNoticeId,
        'detailList': [{ $delete: '*' }],
        'fileUploads': [{ $delete: '*' }]
      }
      const saveData = transformMQL.save('InvoiceNotice', [params], 'delete')
      this.$http({
        url: '/api-sup-ce/api-ql/InvoiceNotice/delete',
        method: 'POST',
        data: saveData,
        loading: true
      }).then(res => {
        this.$message.success(this.$t('common.success'))
        this.getQuerydata()
      })
    },
    editTab (type, row) {
      let tab = {}
      if (type === 'add') {
        // 新增
        tab = {
          component: invoiceNoticeDetail,
          params: {
            flag: 'add',
            tabName: 'invoiceNoticeDetail'
          },
          title: this.$t('purSettlementMod.newStatement'),
          name: 'invoiceNoticeDetail'
        }
      } else {
        // 修改
        tab = {
          component: invoiceNoticeDetail,
          params: {
            flag: type,
            showType: 'approveNumber',
            invoiceNoticeId: row.invoiceNoticeId,
            tabName: 'invoiceNoticeDetail' + row.invoiceNoticeNumber
          },
          title: row.invoiceNoticeNumber,
          name: 'invoiceNoticeDetail' + row.invoiceNoticeNumber
        }
      }
      this.$emit('tab-add', tab)
    }
  }
}
</script>
<style scoped lang="scss">
</style>
