<template>
  <el-container class="flex-container the_vendorPurchaseOrderList_wrapper" direction="vertical">
    <el-main>
      <FormWrapper
        :form-array="preArr"
        :pre-form-obj="preFormObj"
        form-label-width="120px"
        @getFormData="getQueryData"
      />

      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :pre-query-data="queryParam"
        :com-active="$attrs['changeTab']"
        url="/api-inq/quote/quoteHeader/listInqQuoteInfo"
      />

      <!--查看报价单-->
      <ReadQuoteFormDialog
        v-if="dialogFormVisible"
        :visible.sync="dialogFormVisible"
        :quote-row="quoteRow"
        :display-score-item="displayScoreItem"
      />

      <!--查看中标结果 bidingResult-->
      <BidingResultDialog
        v-if="bidingResultDialogVisible"
        :visible.sync="bidingResultDialogVisible"
        :view-row="quoteRow"
      />
    </el-main>
  </el-container>
</template>

<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import { getDictItem } from '@/api/common'
import { adaptDictData } from '@/utils'
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import inquiryOrdersDetail from './inquiryOrdersDetail'
import ReadQuoteFormDialog from './inquiryOrdersList/readQuoteFormDialog'
import BidingResultDialog from './bidingResultDialog'

export default {
  name: 'InquiryOrdersList',

  components: {
    TableView,
    FormWrapper,
    ReadQuoteFormDialog,
    BidingResultDialog
  },

  mixins: [tabTodoWatch, tabTodoMixin],

  data () {
    return {
      tableHeader: [
        // t 询价单号
        {
          prop: 'inquiryNo',
          label: this.$t('bidMod.inquiryNo'),
          minWidth: 150,
          showType: 'button',
          btnStyle: 'text',
          callback: row => this.openDetailTab(row, 'read')
        },
        // t 询价标题
        {
          prop: 'inquiryTitle',
          minWidth: 150,
          label: this.$t('bidMod.inquiryTitle')
        },
        // t 询价状态
        {
          prop: 'inqStatus',
          label: this.$t('bidMod.inquiryStatus'),
          minWidth: 120,
          formattor: val => this.$getDictLabelByValue(this.statusList, val)
        },
        // t 报价单号
        {
          prop: 'quoteNo',
          label: this.$t('bidMod.quoteNo'),
          minWidth: 150
        },
        // t 轮次
        {
          prop: 'inqRound',
          label: this.$t('bidMod.currentRound'),
          minWidth: 100
        },
        // t 报价状态
        {
          prop: 'quoteStatus',
          label: this.$t('bidMod.quoteStatus'),
          minWidth: 120,
          formattor: val => this.$getDictLabelByValue(this.quotestatusList, val)
        },
        // t 报价截止时间
        {
          prop: 'deadline',
          label: this.$t('bidMod.quotedeadline'),
          minWidth: 130
        },
        // t 创建人
        {
          prop: 'createdBy',
          label: this.$t('bidMod.bidingCreatedBy'),
          minWidth: 120
        },
        // t 发布时间
        {
          prop: 'publishDate',
          label: this.$t('bidMod.releaseDatetime'),
          minWidth: 130
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
              callback: row => this.openDetailTab(row, 'edit'),
              formattor: () => this.$t('bidMod.doQuote'),
              // 待报价/已撤回 && 接受报价中 && 允许报价
              show: row => ['DRAFT', 'ROLLBACK'].includes(row.quoteStatus) && row.inqStatus === 'RECEI_QUOTATION' && row.canQuote === 'Y'
            },
            // t 撤回
            {
              callback: row => this.withDrawOne(row),
              formattor: () => this.$t('bidMod.withdraw'),
              // 已报价 && 接受报价中 && 允许撤回
              show: row => row.quoteStatus === 'SUBMIT' && row.inqStatus === 'RECEI_QUOTATION' && row.allowWithdrawBiding === 'Y'
            },
            // 查看中标结果
            {
              callback: row => this.openBidingResultDialog(row),
              formattor: () => '查看结果'
            }
          ]
        }
      ],
      gridId: 'list',
      tableData: [],
      statusList: [],
      firstLoad: true,
      preFormObj: {},
      preArr: [
        // 询价单号
        { prop: 'inquiryNo', label: this.$t('bidMod.inquiryNo') },
        // 物料名称
        { prop: 'itemDesc', label: this.$t('quota.itemName') },
        // 报价状态
        {
          prop: 'quoteStatus',
          label: this.$t('bidMod.quoteStatus'),
          type: 'select',
          options: []
        },
        // 询价状态
        {
          prop: 'inqStatus',
          label: this.$t('bidMod.inquiryStatus'),
          type: 'select',
          options: []
        }
      ],
      queryParam: {},
      quotestatusList: [],
      dialogFormVisible: false,
      quoteRow: null,
      displayScoreItem: [],
      bidingResultDialogVisible: false
    }
  },

  computed: {
    curRole () {
      return this.$store.getters.userType
    }
  },

  created () {
    // 询价单状态
    getDictItem('RFQ_STATUS').then(res => {
      this.statusList = adaptDictData(res.data, 'dict')
      this.$set(this.preArr, 3, {
        ...this.preArr[3],
        options: this.statusList
      })
    })
    // 报价状态
    getDictItem('QUOTE_STATUS').then(res => {
      this.quotestatusList = adaptDictData(res.data, 'dict').map(item => {
        return {
          ...item,
          [item.value]: item.label
        }
      })
      this.$set(this.preArr, 2, {
        ...this.preArr[2],
        options: this.quotestatusList
      })
    })

    // 供应商第一次从工作台跳转过来的场景
    const routeParam = this.$route.params || {}
    if (routeParam.from === 'workCount' && this.firstLoad && this.curRole === 'VENDOR') {
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
    getQueryData (v = {}) {
      this.queryParam = Object.assign(v, this.preFormObj)
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },

    /* 撤回 */
    withDrawOne (row) {
      this.$prompt('撤回原因', '撤回说明', {
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }).then(({ value }) => {
        if (!value) {
          this.$message.warning('请输入撤回原因！')
          return
        }
        if (value.length > 250) {
          this.$message.warning('撤回原因长度最多250个字符！')
          return
        }
        this.$api.inq.inquiryBySimple.rollback({
          inquiryId: row.inquiryId,
          rollbackReason: value
        }).then(() => {
          this.$message.success('撤回成功！')
          this.getQueryData()
        })
      }).catch(() => {
        this.$message.info('取消撤回！')
      })
    },

    /* 查看报价 */
    readTab (row) {
      this.quoteRow = row
      this.$api.inq.inquiryBySimple.readQuote({ quoteId: this.quoteRow.quoteId }).then(data => {
        if (data && data.data) {
          this.dialogFormVisible = true
          this.displayScoreItem = (data.data.list || []).map(item => {
            return {
              ...item,
              quoteNo: this.quoteRow.quoteNo,
              quoteStatus: this.quoteRow.quoteStatus,
              organizationName: this.quoteRow.organizationName
            }
          })
        }
      })
    },

    openDetailTab (row, type) {
      this.$emit('tab-add', {
        component: inquiryOrdersDetail,
        params: {
          flag: type,
          row: row
        },
        title: row.inquiryNo,
        name: `inquiryOrders${type}${row.inquiryNo}`
      })
    },

    /* 查看中标结果 */
    openBidingResultDialog (row) {
      this.quoteRow = row
      this.bidingResultDialogVisible = true
    }
  }
}
</script>
