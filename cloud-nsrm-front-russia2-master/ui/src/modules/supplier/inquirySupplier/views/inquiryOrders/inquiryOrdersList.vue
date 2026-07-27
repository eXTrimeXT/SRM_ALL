<template>
  <el-container class="flex-container the_vendorPurchaseOrderList_wrapper" direction="vertical">
    <el-main>
      <FormWrapper
        :form-array="preArr"
        :pre-form-obj="preFormObj"
        @getFormData="getQueryData"
      />

      <TableView
        ref="list"
        :table-data="tableData"
        :table-header="tableHeader"
        :pre-query-data="queryParam"
        :com-active="$attrs['changeTab']"
        :url="tableViewUrl"
      />

      <!--查看报价单-->
      <ReadQuoteFormDialog
        v-if="dialogFormVisible"
        :visible.sync="dialogFormVisible"
        :quote-row="quoteRow"
        :display-score-item="displayScoreItem"
      />

      <!--查看中标结果-->
      <ResultDialog
        v-if="resultDialogVisible"
        :visible.sync="resultDialogVisible"
        :view-row="quoteRow"
      />
    </el-main>
  </el-container>
</template>

<script>
import { inqSupplierHttp } from 'mods@/inquirySupplier/api'
import { tabTodoWatch } from '@/utils/mixins'
import { judgeQuote, judgeResult, judgeRollback } from 'lib@/composition/inquiry/utils'
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import inquiryOrdersDetail from './inquiryOrdersDetail.vue'
import ReadQuoteFormDialog from './inquiryOrdersList/readQuoteFormDialog'
import ResultDialog from './inquiryOrdersList/resultDialog'

export default {
  name: 'InquiryOrdersList',

  components: {
    TableView,
    FormWrapper,
    ReadQuoteFormDialog,
    ResultDialog
  },

  mixins: [tabTodoWatch],

  data () {
    return {
      tableHeader: [
        // t 询价单号
        {
          prop: 'souNo',
          label: this.$t('bidMod.inquiryNo'),
          minWidth: 150,
          showType: 'button',
          btnStyle: 'text',
          callback: row => this.openDetailTab(row, 'read')
        },
        // t 询价标题
        {
          prop: 'souName',
          minWidth: 150,
          label: this.$t('bidMod.inquiryTitle')
        },
        // t 询价状态
        {
          prop: 'extProjectStatus',
          label: this.$t('bidMod.inquiryStatus'),
          minWidth: 120,
          dataType: 'dict',
          code: 'SOU_PROJECT_STATUS'
        },
        // t 报价单号
        {
          prop: 'orderNo',
          label: this.$t('bidMod.quoteNo'),
          minWidth: 150
        },
        // t 轮次
        {
          prop: 'currentRound',
          label: this.$t('bidMod.currentRound'),
          minWidth: 100
        },
        // t 报价状态
        {
          prop: 'orderStatus',
          label: this.$t('bidMod.quoteStatus'),
          minWidth: 120,
          dataType: 'dict',
          code: 'INQ_SOU_ORDER_STATUS'
        },
        // t 报价截止时间
        {
          prop: 'orderEndTime',
          label: this.$t('bidMod.quotedeadline'),
          minWidth: 150,
          dataType: 'dateTime'
        },
        // t 创建人
        {
          prop: 'createdBy',
          label: this.$t('bidMod.bidingCreatedBy'),
          minWidth: 120
        },
        // t 发布时间
        {
          prop: 'publishTime',
          label: this.$t('bidMod.releaseDatetime'),
          minWidth: 150,
          dataType: 'dateTime'
        },
        // t 操作
        {
          prop: 'operation',
          label: this.$t('bidMod.operation'),
          editType: 'none',
          minWidth: 160,
          showType: 'buttons',
          fixed: 'right',
          btnStyle: 'text',
          buttons: [
            // t 报价
            {
              // 待报价/已撤回 && 接受报价中 && 允许报价
              show: row => judgeQuote(row),
              callback: row => this.openDetailTab(row, 'edit'),
              formattor: () => this.$t('bidMod.doQuote')
            },
            // t 撤回
            {
              // 已报价 && 接受报价中 && 允许撤回
              show: row => judgeRollback(row),
              callback: row => this.rollback(row),
              formattor: () => this.$t('bidMod.withdraw')
            },
            // 查看中标结果
            {
              // 已报价 || 当前轮次大于1
              show: row => judgeResult(row),
              callback: row => this.openBidingResultDialog(row),
              formattor: () => this.$t('bidMod.viewResults')
            }
          ]
        }
      ],
      tableData: [],
      firstLoad: true,
      preFormObj: {},
      preArr: [
        // 询价单号
        { prop: 'souNo', label: this.$t('bidMod.inquiryNo') },
        // 物料名称
        { prop: 'itemDesc', label: this.$t('quota.itemName') },
        // 报价状态
        {
          prop: 'orderStatus',
          label: this.$t('bidMod.quoteStatus'),
          type: 'dict',
          code: 'INQ_SOU_ORDER_STATUS'
        },
        // 询价状态
        {
          prop: 'extProjectStatus',
          label: this.$t('bidMod.inquiryStatus'),
          type: 'dict',
          code: 'SOU_PROJECT_STATUS'
        }
      ],
      queryParam: {},
      dialogFormVisible: false,
      quoteRow: null,
      displayScoreItem: [],
      resultDialogVisible: false,
      tableViewUrl: inqSupplierHttp.order.listPageUrl
    }
  },

  created () {
    // 供应商第一次从工作台跳转过来的场景
    const routeParam = this.$route.params || {}
    if (routeParam.from === 'workCount' && this.firstLoad) {
      this.queryParam.approveStatus = routeParam.approveStatus
      this.firstLoad = false
      this.preFormObj = Object.assign({}, routeParam)
      delete this.preFormObj.from
    }

    this.$nextTick(() => {
      this.getQueryData()
    })
  },

  methods: {
    /* 查询列表 */
    getQueryData (val = {}) {
      this.queryParam = Object.assign(val, this.preFormObj)
      this.$nextTick(() => {
        this.$refs.list.query()
      })
    },

    /* 撤回 */
    async rollback (row) {
      const promptResult = await this.$prompt(
        this.$t('bidMod.withdrawReason'),
        this.$t('bidMod.withdrawDesc'),
        {
          confirmButtonText: this.$t('common.confirm'),
          cancelButtonText: this.$t('common.cancel')
        }
      )

      if (!promptResult) {
        return
      }

      if (!promptResult.value) {
        this.$message.warning(this.$t('bidMod.withdrawTips1'))
        return
      }
      if (promptResult.value.length > 250) {
        this.$message.warning(this.$t('bidMod.withdrawTips2'))
        return
      }

      const response = await inqSupplierHttp.order.rollback({
        projectId: row.projectId,
        withdrawReason: promptResult.value
      })

      if (response) {
        this.$message.success(this.$t('common.successWithdraw'))
        this.getQueryData()
      }
    },

    /* 查看中标结果 */
    openBidingResultDialog (row) {
      this.quoteRow = row
      this.resultDialogVisible = true
    },

    /* 打开tab页签 */
    openDetailTab (row, type) {
      console.log(row, type)
      this.$emit('tab-add', {
        component: inquiryOrdersDetail,
        params: {
          flag: type,
          row: row,
          tabName: `inquiryOrders${type}${row.souNo}`
        },
        title: row.souNo,
        name: `inquiryOrders${type}${row.souNo}`
      })
    }
  }
}
</script>
