<template>
  <el-container class="flex-container" direction="vertical">
    <el-main>
      <FormWrapper
        :form-array="preArr"
        :pre-form-obj="preFormObj"
        @getFormData="getQueryData"
      />

      <TableView
        ref="biddingListTable"
        :table-data="tableData"
        :table-header="tableHeader"
        :pre-query-data="queryParam"
        :com-active="$attrs['changeTab']"
        open-custom-table
        :url="tableViewUrl"
      />

      <!--保证金缴纳-->
      <BondPayDialog
        v-if="bondPayDialogVisible"
        :business-type="BUSINESS_TYPE_ENUM.BIDDING_LTS"
        :visible.sync="bondPayDialogVisible"
        :base-info="editRow"
        @success="getQueryData"
      />
    </el-main>
  </el-container>
</template>

<script>
import { bidSupplierHttp } from 'mods@/biddingSupplier/api'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import { BUSINESS_TYPE_ENUM } from 'lib@/composition/origin/enum'
import { judgeAllowWithdraw, judgeQuote, judgeSignUp } from 'lib@/composition/biddingLts/utils'
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import BondPayDialog from 'lib@/composition/origin/bondPay/bondPayDialogNew.vue'
import biddingVendorDetail from './biddingOrdersDetail.vue'
import biddingVendorSignUp from './biddingOrdersSignUp.vue'
import biddingOrdersQuote from './biddingOrdersQuote.vue'

export default {
  name: 'BiddingOrdersList',

  components: {
    TableView,
    FormWrapper,
    BondPayDialog
  },

  mixins: [tabTodoWatch, tabTodoMixin],

  data () {
    return {
      tableViewUrl: bidSupplierHttp.order.listPageUrl,
      tableHeader: [
        // 项目编号
        {
          prop: 'souNo',
          label: this.$t('bidMod.bidingNum'),
          minWidth: 150,
          showType: 'button',
          btnStyle: 'text',
          callback: row => this.openViewTab(row)
        },
        // 项目名称
        {
          prop: 'souName',
          label: this.$t('bidMod.bidingName'),
          minWidth: 150
        },
        // 招标范围
        {
          prop: 'publishScope',
          label: this.$t('bidMod.bidingScope'),
          minWidth: 100,
          formattor: val => this.$getDictLabel('SOU_PUBLISH_SCOPE', val)
        },
        // 项目状态
        {
          prop: 'projectStatus',
          label: this.$t('bidMod.bidingStatus'),
          minWidth: 100,
          formattor: val => this.$getDictLabel('SOU_PROJECT_STATUS', val)
        },
        // 报名状态
        {
          prop: 'signUpStatus',
          label: this.$t('bidMod.signUpStatus'),
          minWidth: 100,
          formattor: val => this.$getDictLabel('SOU_SIGN_UP_STATUS', val)
        },
        // 投标状态
        {
          prop: 'orderStatus',
          label: this.$t('bidMod.orderStatus'),
          minWidth: 100,
          formattor: val => this.$getDictLabel('SOU_ORDER_STATUS', val)
        },
        // 轮次
        {
          prop: 'currentRound',
          label: this.$t('bidMod.bidingRound'),
          minWidth: 80
        },
        // 调整结束原因
        {
          prop: 'adjustBiddingTimeReason',
          label: '调整结束原因',
          minWidth: 120
        },
        // 投标开始时间
        {
          prop: 'orderStartTime',
          label: this.$t('bidMod.bidingStartDatetime'),
          minWidth: 160
        },
        // 投标截止时间
        {
          prop: 'orderEndTime',
          label: this.$t('bidMod.enrollEndDatetime'),
          minWidth: 160
        },
        // 发布时间
        {
          prop: 'publishTime',
          label: this.$t('bidMod.releaseDatetime'),
          minWidth: 160
        },
        {
          prop: 'operation',
          label: this.$t('bidMod.operation'),
          minWidth: 210,
          showType: 'buttons',
          fixed: 'right',
          btnStyle: 'text',
          buttons: [
            // 报名
            {
              // 项目状态：接受报名中 && 报名状态：未报名/已驳回 && 报名截止时间大于当前时间
              show: row => judgeSignUp(row),
              formattor: () => this.$t('bidMod.signUpBiding'),
              callback: row => this.openSignUpTab(row)
            },
            // 投标
            {
              // 项目状态：接受投标中 && 投标状态：未投标/已驳回 && 允许投标
              show: row => judgeQuote(row),
              formattor: () => this.$t('bidMod.doBiding1'),
              callback: row => this.openQuoteTab(row)
            },
            // 撤回投标
            {
              // 项目状态：接受投标中 && 投标状态：已投标 && 允许投标 && 允许撤回
              show: row => judgeAllowWithdraw(row),
              formattor: () => this.$t('bidMod.rebackBiding1'),
              callback: row => this.allowWithdraw(row)
            },
            // 保证金缴纳
            {
              show: row => row.hasBondNode === 'Y',
              formattor: () => '保证金缴纳',
              callback: row => this.openBondPayDialog(row)
            }
            // 质疑/澄清
            // {
            //   formattor: () => this.$t('bidMod.challengeClarification'),
            //   callback: row => this.toChallengeClarification(row)
            // }
          ]
        }
      ],
      tableData: [],
      preFormObj: {},
      preArr: [
        { prop: 'souNo', label: this.$t('bidMod.bidingNum') },
        { prop: 'souName', label: this.$t('bidMod.bidingName') },
        // 项目状态
        {
          prop: 'projectStatus',
          label: this.$t('bidMod.projStatus'),
          type: 'dict',
          code: 'SOU_PROJECT_STATUS'
        },
        // 报名状态
        {
          prop: 'signUpStatus',
          label: this.$t('bidMod.signUpStatus'),
          type: 'dict',
          code: 'SOU_SIGN_UP_STATUS'
        },
        // 投标状态
        {
          prop: 'orderStatus',
          label: this.$t('bidMod.orderStatus1'),
          type: 'dict',
          code: 'SOU_ORDER_STATUS'
        },
        // 招标范围
        {
          prop: 'publishScope',
          label: this.$t('bidMod.bidingScope1'),
          type: 'dict',
          code: 'SOU_PUBLISH_SCOPE'
        }
      ],
      queryParam: {},
      editRow: null,
      bondPayDialogVisible: false,
      BUSINESS_TYPE_ENUM
    }
  },

  watch: {
    $route: {
      // 当前功能已经打开的时候监听是否是从其他功能跳转过来的
      deep: true,
      immediate: true,
      handler () {
        const params = this.$route.params
        if (params.from === 'workCount' && params.funName === 'biddingOrdersList') {
          // 供应商 工作台跳转
          this.queryParam.projectStatus = params.projectStatus
          this.preFormObj = Object.assign({}, { projectStatus: params.projectStatus })
        }

        // 公开招标门户页跳转报名
        if (
          params.from === 'portalBidding' &&
          params.funName === 'signUp' &&
          params.projectInfo
        ) {
          this.openSignUpTab(params.projectInfo)
        }
      }
    }
  },

  mounted () {
    this.$nextTick(() => {
      this.getQueryData()
    })
  },

  methods: {
    /* 查询列表数据 */
    getQueryData (val = {}) {
      this.queryParam = Object.assign({}, val || this.preFormObj)
      this.$nextTick(() => {
        this.$refs.biddingListTable.query()
      })
    },

    /* 质疑澄清 */
    toChallengeClarification (row) {
      this.$router.push({
        name: 'inquiryByProjectChClVendor',
        params: {
          projectId: row.projectId,
          souName: row.souName,
          souNo: row.souNo
        }
      })
    },

    /* 撤回投标 */
    async allowWithdraw (row) {
      const promptResult = await this.$prompt(
        this.$t('bidMod.withdrawReason'),
        this.$t('bidMod.rebackBiding1'),
        {
          confirmButtonText: this.$t('common.confirm'),
          cancelButtonText: this.$t('common.cancel'),
          inputValidator: value => !(!value || value.length > 200),
          inputErrorMessage: this.$t('bidMod.biddingManagementSupplier.withDrawReasonRequired')
        }
      )

      if (!promptResult) {
        return
      }

      const response = await bidSupplierHttp.order.withdraw({
        projectId: row.projectId,
        withdrawReason: promptResult.value
      })
      if (response) {
        this.$message.success(this.$t('bidMod.withdrawQuoteSuc'))
        this.getQueryData()
      }
    },

    /* 投标 */
    openQuoteTab (row) {
      this.$emit('tab-add', {
        component: biddingOrdersQuote,
        params: {
          flag: 'edit',
          row: row,
          tabName: `biddingOrdersQuote${row.souNo}`
        },
        title: row.souNo,
        name: `biddingOrdersQuote${row.souNo}`
      })
    },

    /* 报名 */
    openSignUpTab (row) {
      // 校验能否报名
      if (!judgeSignUp(row)) {
        this.$message.warning('该单据现无法报名！需满足项目状态：接受报名中；报名状态：未报名/已驳回；报名截止时间大于当前时间')
        return
      }

      this.$emit('tab-add', {
        component: biddingVendorSignUp,
        params: {
          flag: 'edit',
          row: row,
          tabName: `biddingVendorSignUp${row.souNo}`
        },
        title: row.souNo,
        name: `biddingVendorSignUp${row.souNo}`
      })
    },

    /* 查看投标 */
    openViewTab (row) {
      this.$emit('tab-add', {
        component: biddingVendorDetail,
        params: {
          flag: 'view',
          row: row,
          tabName: `biddingVendorDetail${row.souNo}`
        },
        title: row.souNo,
        name: `biddingVendorDetail${row.souNo}`
      })
    },

    /* 保证金缴纳 */
    openBondPayDialog (row) {
      this.editRow = {
        id: row.projectId,
        idKey: 'projectId'
      }
      this.bondPayDialogVisible = true
    }
  }
}
</script>
