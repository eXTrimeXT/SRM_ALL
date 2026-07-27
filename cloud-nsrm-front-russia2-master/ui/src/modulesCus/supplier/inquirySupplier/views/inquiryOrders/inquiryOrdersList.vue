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
        :auto-query="true"
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
      <!-- 采购需知 -->
      <PurchaseNoticeDialog
        :visible.sync="purchaseNoticeVisible"
        @confirm="openDetailTab(currentRow, 'edit')"
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
import PurchaseNoticeDialog from './inquiryOrdersList/purchase-notice-dialog'
export default {
  name: 'InquiryOrdersList',

  components: {
    TableView,
    FormWrapper,
    ReadQuoteFormDialog,
    ResultDialog,
    PurchaseNoticeDialog
  },

  mixins: [tabTodoWatch],

  data () {
    return {
      purchaseNoticeVisible: false,
      currentRow: {},
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
        {
          prop: 'createUserOrgOuName',
          label: this.$t('cusEntry.bidMod.companyName'),
          minWidth: 150
        },
        // t 询价状态
        {
          prop: 'extProjectStatus',
          label: this.$t('bidMod.inquiryStatus'),
          minWidth: 120,
          dataType: 'dict',
          code: 'EXT_INQ_SOU_PROJECT_STATUS'
        },
        // t 报价单号
        {
          prop: 'orderNo',
          label: this.$t('bidMod.quoteNo'),
          minWidth: 150
        },
        // t 报价状态
        {
          prop: 'orderStatus',
          label: this.$t('bidMod.quoteStatus'),
          minWidth: 120,
          dataType: 'dict',
          code: 'INQ_SOU_ORDER_STATUS'
        },
        // t 报价开始时间
        {
          prop: 'orderStartTime',
          label: this.$t('bidMod.bidingStartDatetime1'),
          minWidth: 150,
          dataType: 'dateTime'
        },
        // t 报价截止时间
        {
          prop: 'orderEndTime',
          label: this.$t('bidMod.quotedeadline'),
          minWidth: 150,
          dataType: 'dateTime'
        },
        // t 轮次
        {
          prop: 'currentRound',
          label: this.$t('bidMod.currentRound'),
          minWidth: 100
        },
        // t 创建人
        {
          prop: 'createdFullName',
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
              callback: row => this.quote(row),
              formattor: () => this.$t('bidMod.doQuote')
            },
            // t 撤回
            {
              // 已报价 && 接受报价中 && 允许撤回
              show: row => judgeRollback(row),
              callback: row => this.rollback(row),
              formattor: () => this.$t('bidMod.withdraw')
            }
            // 查看中标结果
            // {
            //   // 已报价 || 当前轮次大于1
            //   show: row => judgeResult(row),
            //   callback: row => this.openBidingResultDialog(row),
            //   formattor: () => this.$t('bidMod.viewResults')
            // }
          ]
        }
      ],
      tableData: [],
      firstLoad: true,
      preFormObj: {},
      preArr: [
        // 询价单号
        { prop: 'souNo', label: this.$t('bidMod.inquiryNo') },
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
          code: 'EXT_INQ_SOU_PROJECT_STATUS'
        },
        // 报价单号
        {
          prop: 'orderNo',
          label: this.$t('bidMod.quoteNo')
        },
        {
          prop: 'createUserOrgOuName',
          label: this.$t('cusEntry.bidMod.companyName')
        },
        {
          prop: 'buyerNickName',
          label: this.$t('cusEntry.bidMod.buyerNickName')
        },
        {
          prop: 'itemCode',
          label: this.$t('bidMod.itemCode')
        },
        {
          prop: 'itemDesc',
          label: this.$t('bidMod.itemName')
        },
        {
          prop: 'extBrand',
          label: this.$t('cusEntry.inq.brand')
        },
        {
          prop: 'extMaterialModel',
          label: this.$t('cusEntry.bidMod.specification')
        }
      ],
      queryParam: {},
      dialogFormVisible: false,
      quoteRow: null,
      displayScoreItem: [],
      resultDialogVisible: false,
      tableViewUrl: inqSupplierHttp.order.listPageUrl,
      isExamFlag: false
    }
  },
  watch: {
    $route: {
      deep: true,
      immediate: true,
      handler () {
        if (this.$route.params.from === 'workCount' && this.$route.params.funName === 'inquiryOrders') {
          this.preFormObj.extProjectStatus = 'ACCEPT_ORDER'
          this.preFormObj.orderStatus = 'DRAFT'
          this.getQueryData(this.preFormObj)
        }
        if (this.$route.name === 'inquiryOrders') {
          this.isExamCheck()
        }
      }
    }
  },
  methods: {
    /* 报价 */
    quote (row) {
      if (this.isExamFlag) {
        this.$confirm(this.$t('cusEntry.vendorMod.integrityOtherTipText'), this.$t('cusEntry.vendorMod.integrityTitle'), {
          confirmButtonText: this.$t('cusEntry.vendorMod.integrityTitle'),
          cancelButtonText: this.$t('cusEntry.vendorMod.buttonCancel')
        }).then(() => {
          this.$http({
            url: 'api-pj/sun-honesty/externalSso',
            method: 'POST',
            loading: true
          }).then(res => {
            if (res.code + '' === '0') {
              window.open(res.data)
            }
          })
        }).catch(() => {})
      } else {
        this.currentRow = row
        this.purchaseNoticeVisible = true
      }
    },
    /* 查询列表 */
    getQueryData (val = {}) {
      const {
        itemCode,
        extBrand,
        extMaterialModel
      } = val
      if (itemCode || extBrand || extMaterialModel) {
        val.extensions = {}
        const customQuery = { itemCode, extBrand, extMaterialModel }
        Object.keys(customQuery).filter(key => !!customQuery[key]).forEach(item => {
          val.extensions[item] = customQuery[item]
          Reflect.deleteProperty(val, item)
        })
      }
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
    },
    // 校验是否需要廉洁考试
    isExamCheck () {
      this.$http({
        url: 'api-pj/sun-honesty/checkExam',
        method: 'POST'
      }).then(res => {
        if (res.code + '' === '0') {
          this.isExamFlag = res.data?.isExam === 'N'
        }
      })
    }
  }
}
</script>
