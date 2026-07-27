<template>
  <el-container class="flex-container the_onlineInvoice_wrapper" direction="vertical">
    <el-main>
      <FormWrapper :form-array="preArr" @getFormData="getQuerydata" />
      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <!-- 创建开票单 -->
          <AuthorityButton type="primary" @click="editTab('add')">
            <!-- 开票申请 -->
            {{ $t('cusEntry.supplement20250205.invoiceApplication') }}
          </AuthorityButton>
          <AuthorityButton type="primary" @click="closeBill">
            {{ $t('common.close') }}
          </AuthorityButton>
          <!-- 开票单导出文件 -->
          <ExportExcel
            type="default"
            export-mode="front"
            :table-header="tableHeader"
            :dict-codes="dictCodes"
            :page-url="tableUrl"
            :filter-params="filterParams"
            :fileName="$t('cusEntry.supplement20250205.invoiceExportFile')"
          />
          <AuthorityButton type="primary" @click="pushEAS">
            <!-- 重推EAS -->
            {{ $t('cusEntry.supplement20250205.repushEas') }}
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
        :checkbox="true"
        :checkChange="checkChange"
        :comActive="$attrs['changeTab']"
        :url="tableUrl"
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
import agentOnlineInvoiceDetail from './agentOnlineInvoiceDetail'
import ExportExcel from 'lib@/components/export-excel'
import { parseTime } from '@/utils'
import { transformMQL } from 'lib@/utils/util'

export default {
  name: 'AgentOnlineInvoiceList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    ExportExcel
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      tableUrl: '/api-sup-ce/api-ql/OnlineInvoice/query',
      dictCodes: {
        invoiceStatus: 'INVOICE_STATUS',
        extStatus: 'INVOICE_STATUS'
      },
      gridId: 'agentOnlineInvoiceList',
      pageSize: 15,
      filterParams: {},
      queryParam: {},
      tableHeader: [],
      tableData: [],
      preArr: [
        {
          prop: 'creationDate',
          label: () => this.$t('bidMod.dateCreated'), // 创建日期
          type: 'daterange'
        },
        {
          prop: 'onlineInvoiceNum',
          label: () => this.$t('purSettlementMod.billingNumber') // 开票单号
        },
        {
          prop: 'createdFullName',
          label: () => this.$t('bidMod.creator') // 创建人
        },
        {
          prop: 'extPrincipalName',
          label: () => this.$t('cusEntry.orderMod.invoiceBody') // 开票主体
        },
        {
          prop: 'invoiceStatus',
          label: () => this.$t('purSettlementMod.paymentPlanStatus'), // 单据状态
          type: 'dict',
          code: 'INVOICE_STATUS'
        },
        {
          prop: 'orgId',
          label: () => this.$t('quota.org'), // 业务实体
          type: 'OUorganizationSelector'
        },
        {
          prop: 'vendorName',
          label: () => this.$t('bidMod.vendorName')
        }
      ],
      selectList: [],
      userId: null
    }
  },
  created () {
    this.userId = this.$store.getters.userInfo?.userId || null
    this.tableHeader = [
      // 业务实体
      {
        prop: 'orgName',
        label: this.$t('quota.org'),
        minWidth: 120
      },
      // 开票主体
      {
        prop: 'extPrincipalName',
        label: this.$t('cusEntry.orderMod.invoiceBody'),
        minWidth: 120
      },
      // 开票单号
      {
        prop: 'onlineInvoiceNum',
        label: this.$t('purSettlementMod.billingNumber'),
        minWidth: 120,
        showType: 'button',
        btnStyle: 'text',
        callback: row => this.editTab('view', row)
      },
      // 单据状态
      {
        prop: 'extStatus',
        label: this.$t('purSettlementMod.paymentPlanStatus'),
        minWidth: 100,
        dataType: 'dict',
        code: 'INVOICE_STATUS'
      },
      // 供应商编码
      {
        prop: 'vendorCode',
        label: this.$t('common.vendorCode'),
        minWidth: 120
      },
      // 供应商名称
      {
        prop: 'vendorName',
        label: this.$t('common.vendorName'),
        minWidth: 150
      },
      // 开票含税金额
      {
        prop: 'taxTotalAmount',
        label: this.$t('purSettlementMod.taxTotalAmount'),
        minWidth: 120
      },
      {
        prop: 'currencyName',
        label: this.$t('vendorMod.currencyCode'),
        minWidth: 120
      },
      // 创建人
      {
        prop: 'createdFullName',
        label: this.$t('common.creator'),
        minWidth: 100
      },
      // 创建时间
      {
        prop: 'creationDate',
        label: this.$t('common.creationTime'),
        minWidth: 100,
        formattor: val => val ? parseTime(val, '{y}-{m}-{d}') : ''
      },
      // 关闭原因
      {
        prop: 'comment',
        label: this.$t('cusEntry.orderMod.closeReason'),
        minWidth: 150
      },
      // 是否已成功推送EAS
      {
        prop: 'extSyncEas',
        label: this.$t('cusEntry.supplement20250205.extSyncEas'),
        dataType: 'dict',
        code: 'YES_OR_NO',
        minWidth: 160
      },
      {
        prop: 'operation',
        label: this.$t('common.operation'),
        width: 100,
        showType: 'buttons',
        fixed: 'right',
        btnStyle: 'text',
        buttons: [
          {
            callback: row => this.editTab('edit', row),
            formattor: _ => this.$t('common.edit'),
            show: row => ['DRAFT'].includes(row.invoiceStatus) && row.createdId === this.userId
          },
          {
            callback: row => this.delRowData(row),
            formattor: _ => this.$t('common.delete'),
            show: row => ['DRAFT'].includes(row.invoiceStatus) && row.createdId === this.userId
          }
        ]
      }
    ]

    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    afterQuery (data) {
      this.tableData = data.map(item => {
        // 单据状态优先取【扩展状态：extStatus】
        item.invoiceStatus = item.extStatus || item.invoiceStatus
        return item
      })
    },
    getQuerydata (v) {
      // 【已关闭】扩展状态 使用 extStatus 查询, 其他状态使用 invoiceStatus 查询(此时 extStatus为空)
      let params = {}
      const { onlineInvoiceNum, extPrincipalName, invoiceStatus, orgId, createdFullName, creationDate, vendorName } = v || {}
      if (onlineInvoiceNum) {
        params.onlineInvoiceNum = { contains: onlineInvoiceNum }
      }
      if (vendorName) {
        params.vendorName = { contains: vendorName }
      }
      if (extPrincipalName) {
        params.extPrincipalName = { contains: extPrincipalName }
      }
      if (invoiceStatus && ['CLOSED'].includes(invoiceStatus)) {
        params.extStatus = { eq: invoiceStatus }
      } else if (invoiceStatus) {
        params.invoiceStatus = { eq: invoiceStatus }
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
        type: 'OnlineInvoice',
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
    checkChange (val) {
      this.selectList = val
    },
    closeBill () {
      if (this.selectList.length == 0) {
        return this.$message.warning(this.$t('common.msgSelectData'))
      }
      let sign = this.selectList.some(item => item.invoiceStatus !== 'FINAL_REVIEW_APPROVED')
      if (sign) {
        // 仅可关闭已审批状态的单据
        return this.$message.error(this.$t('cusEntry.supplement20250205.agentOnlineInvoiceTip7'))
      }
      this.$prompt(this.$t('qualitySynergy.closeReason'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        closeOnClickModal: false,
        inputPlaceholder: this.$t('cusEntry.supplement20250205.closeReasonTip'), // '请输入关闭原因'
        inputValidator: value => !!value,
        inputErrorMessage: this.$t('cusEntry.supplement20250205.closeReasonTip') // 请输入关闭原因
      }).then(({ value }) => {
        const params = {
          onlineInvoiceIds: this.selectList.map(item => item.onlineInvoiceId),
          comment: value
        }
        const saveData = transformMQL.save('OnlineInvoice', [params], 'extClose')
        this.$http({
          url: '/api-sup-ce/api-ql/OnlineInvoice/extClose',
          method: 'POST',
          data: saveData,
          loading: true
        }).then(res => {
          this.$message.success(this.$t('common.success'))
          this.getQuerydata()
        })
      })
    },
    pushEAS () {
      if (this.selectList.length == 0) {
        return this.$message.warning(this.$t('common.msgSelectData'))
      }
      let errMsg = ''
      let sign = this.selectList.some(item => {
        if (item.invoiceStatus !== 'FINAL_REVIEW_APPROVED') {
          errMsg = this.$t('cusEntry.supplement20250205.agentOnlineInvoiceTip8') // 仅推送已审批状态的单据
          return true
        }
        if (item.extSyncEas === 'Y') {
          errMsg = this.$t('cusEntry.supplement20250205.agentOnlineInvoiceTip9') // 仅执行未成功推送EAS的数据
          return true
        }
      })
      if (sign) {
        return this.$message.error(errMsg)
      }
      const params = { onlineInvoiceIds: this.selectList.map(item => item.onlineInvoiceId) }
      const saveData = transformMQL.save('OnlineInvoice', [params], 'extPushEas')
      this.$http({
        url: '/api-sup-ce/api-ql/OnlineInvoice/extPushEas',
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
      const saveData = transformMQL.save(
        'OnlineInvoice',
        [{
          'onlineInvoiceId': row.onlineInvoiceId,
          'detailList': [{ $delete: '*' }],
          'advanceApplyList': [{ $delete: '*' }],
          'ocrInvoiceList': [{ $delete: '*' }],
          'punishList': [{ $delete: '*' }],
          'fileUploads': [{ $delete: '*' }]
        }],
        'delete'
      )
      this.$http({
        url: '/api-sup-ce/api-ql/OnlineInvoice/delete',
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
          component: agentOnlineInvoiceDetail,
          params: {
            flag: 'add',
            tabName: 'agentOnlineInvoiceDetail'
          },
          title: this.$t('purSettlementMod.newOnlineInvoice'),
          name: 'agentOnlineInvoiceDetail'
        }
      } else {
        // 修改
        tab = {
          component: agentOnlineInvoiceDetail,
          params: {
            flag: type,
            onlineInvoiceId: row.onlineInvoiceId,
            tabName: 'agentOnlineInvoiceDetail' + row.onlineInvoiceNum
          },
          title: row.onlineInvoiceNum,
          name: 'agentOnlineInvoiceDetail' + row.onlineInvoiceNum
        }
      }
      this.$emit('tab-add', tab)
    }
  }
}
</script>
<style scoped lang="scss">
</style>
