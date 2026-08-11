<template>
  <el-container
    class="flex-container the_vendorBiddingList_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="preArr"
        :pre-form-obj="preFormObj"
        @getFormData="getQueryData"
      />

      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :com-active="$attrs['changeTab']"
        url="/api-bid/supplierCooperate/orderHead/queryBiding"
        style="margin-top: 10px"
      />

      <!--保证金缴纳-->
      <BondPayDialog
        v-if="bondPayDialogVisible"
        :business-type="BUSINESS_TYPE_ENUM.BIDING"
        :visible.sync="bondPayDialogVisible"
        :base-info="editRow"
        @submitBondSuccess="getQueryData"
      />
    </el-main>
  </el-container>
</template>

<script>
import { BUSINESS_TYPE_ENUM } from 'lib@/composition/origin/enum'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import { parseTime } from '@/utils'
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import vendorBiddingDetail from './vendorBiddingDetail'
import vendorBiddingSignUp from './vendorBiddingSignUp'
import doBidingDetail from './doBidingDetail'
import BondPayDialog from 'lib@/composition/origin/bondPay/bondPayDialog'

export default {
  name: 'VendorBiddingList',

  components: {
    TableView,
    FormWrapper,
    BondPayDialog
  },

  mixins: [tabTodoWatch, tabTodoMixin],

  data () {
    return {
      pageSize: 15,
      gridId: 'list',
      tableHeader: [],
      tableData: [],
      tableData2: [],
      preFormObj: {},
      preArr: [
        { prop: 'bidingNum', label: this.$t('bidMod.bidingNum') },
        { prop: 'bidingName', label: this.$t('bidMod.bidingName') },
        // 项目状态
        {
          prop: 'bidingStatus',
          label: this.$t('bidMod.projStatus'),
          type: 'dict',
          code: 'BIDDING_PRO_STATUS'
        },
        // 报名状态
        {
          prop: 'signUpStatus',
          label: this.$t('bidMod.signUpStatus'),
          type: 'dict',
          code: 'BIDDING_SIGNUP_STATES'
        },
        // 投标状态
        {
          prop: 'orderStatus',
          label: this.$t('bidMod.orderStatus'),
          type: 'dict',
          code: 'BIDDING_ORDER_STATES'
        },
        // 招标范围
        {
          prop: 'bidingScope',
          label: this.$t('bidMod.bidingScope'),
          type: 'dict',
          code: 'BID_SCOPE'
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
        if (params.from === 'workCount' && params.funName === 'vendorBiddingList') {
          // 供应商 工作台跳转
          this.queryParam.bidingStatus = params.bidingStatus
          this.preFormObj = Object.assign({}, { bidingStatus: params.bidingStatus })
        }
      }
    }
  },

  created () {
    this.tableHeader = [
      // 项目编号
      {
        prop: 'bidingNum',
        label: this.$t('bidMod.bidingNum'),
        minWidth: 150,
        showType: 'button',
        btnStyle: 'text',
        callback: row => this.viewBiding(row)
      },
      // 项目名称
      {
        prop: 'bidingName',
        label: this.$t('bidMod.bidingName'),
        minWidth: 150,
        formattor: val => val || '--'
      },
      // 招标范围
      {
        prop: 'bidingScope',
        label: this.$t('bidMod.bidingScope'),
        minWidth: 100,
        formattor: val => this.$getDictLabel('BID_SCOPE', val)
      },
      // 项目状态
      {
        prop: 'bidingStatus',
        label: this.$t('bidMod.bidingStatus'),
        minWidth: 100,
        formattor: val => this.$getDictLabel('BIDDING_PRO_STATUS', val)
      },
      // 报名状态
      {
        prop: 'signUpStatus',
        label: this.$t('bidMod.signUpStatus'),
        minWidth: 100,
        formattor: val => this.$getDictLabel('BIDDING_SIGNUP_STATES', val)
      },
      // 投标状态
      {
        prop: 'orderStatus',
        label: this.$t('bidMod.orderStatus'),
        minWidth: 100,
        formattor: val => this.$getDictLabel('BIDDING_ORDER_STATES', val)
      },
      // 轮次
      {
        prop: 'round',
        label: this.$t('bidMod.bidingRound'),
        minWidth: 80
      },
      // 调整结束原因
      {
        prop: 'adjustBidingTimeReason',
        label: '调整结束原因',
        minWidth: 120
      },
      // 投标截止时间
      {
        prop: 'bidingEndDatetime',
        label: this.$t('bidMod.enrollEndDatetime'),
        minWidth: 120,
        formattor: val => val ? parseTime(val, '{y}-{m}-{d}') : ''
      },
      // 开标时间
      {
        prop: 'businessOpenBidTime',
        label: this.$t('bidMod.techOpenBidTime'),
        minWidth: 120,
        formattor: val => val ? parseTime(val, '{y}-{m}-{d}') : ''
      },
      // 发布时间
      {
        prop: 'releaseDateTime',
        label: this.$t('bidMod.releaseDatetime'),
        minWidth: 120,
        formattor: val => val ? parseTime(val, '{y}-{m}-{d}') : ''
      },
      {
        prop: 'operation',
        label: this.$t('bidMod.operation'),
        minWidth: 200,
        showType: 'buttons',
        fixed: 'right',
        btnStyle: 'text',
        buttons: [
          // 报名
          {
            // 项目状态：接受报名中 && 报名状态：未报名/已驳回 && 报名截止时间大于当前时间
            show: row => row.bidingStatus === 'ACCEPT_SIGNUP' &&
              ['NO_SIGNUP', 'REJECTED'].includes(row.signUpStatus) &&
              new Date(row.enrollEndDatetime) > new Date(),
            formattor: () => this.$t('bidMod.signUpBiding'),
            callback: row => this.signUpBiding(row)
          },
          // 投标
          {
            // 项目状态：接受投标中 && 投标状态：未投标/已驳回 && 允许投标
            show: row => row.bidingStatus === 'ACCEPT_BID' &&
              ['DRAFT', 'WITHDRAW'].includes(row.orderStatus) &&
              row.canOrder === 'Y',
            formattor: () => this.$t('bidMod.doBiding'),
            callback: row => this.doBiding(row)
          },
          // 撤回投标
          {
            // 项目状态：接受投标中 && 投标状态：已投标 && 允许投标 && 允许撤回
            show: row => row.bidingStatus === 'ACCEPT_BID' &&
              row.orderStatus === 'SUBMISSION' &&
              row.canOrder === 'Y' &&
              row.withdrawBiding === 'Y',
            formattor: () => this.$t('bidMod.rebackBiding'),
            callback: row => this.withdrawBidding(row)
          },
          // 保证金缴纳
          {
            show: row => row.needBond === 'Y',
            formattor: () => '保证金缴纳',
            callback: row => this.openBondPayDialog(row)
          },
          // 质疑/澄清
          {
            formattor: () => this.$t('bidMod.challengeClarification'),
            callback: row => this.toChallengeClarification(row)
          }
        ]
      }
    ]

    this.$nextTick(() => {
      this.getQueryData()
    })
  },

  methods: {
    /* 查询列表数据 */
    getQueryData (v) {
      let query = v || this.preFormObj
      this.queryParam = Object.assign({}, query)
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },

    /* 投标 */
    doBiding (row) {
      this.$emit('tab-add', {
        component: doBidingDetail,
        params: {
          flag: 'edit',
          row: row
        },
        title: row.bidingNum,
        name: `doBidingDetail${row.bidingNum}`
      })
    },

    /* 质疑澄清 */
    toChallengeClarification (row) {
      this.$router.push({
        name: 'vendorChallengeClarification',
        params: {
          bidingId: row.bidingId,
          bidingName: row.bidingName,
          bidingNum: row.bidingNum,
          // 随机数, 触发watch监听
          random: Math.random()
        }
      })
    },

    /* 撤回投标 */
    withdrawBidding (row) {
      this.$prompt(this.$t('bidMod.withdrawReason'), this.$t('bidMod.rebackBiding'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        inputValidator: value => !(!value || value.length > 200),
        inputErrorMessage: this.$t('bidMod.biddingManagementSupplier.withDrawReasonRequired')
      }).then(({ value }) => {
        this.$http({
          url: '/api-bid/supplierCooperate/orderHead/withdrawOrder',
          method: 'POST',
          data: {
            orderHeadId: row.orderHeadId,
            withDrawReason: value
          },
          loading: true
        }).then(() => {
          this.$message.success(this.$t('bidMod.withdrawBidSuccess'))
          this.getQueryData()
        })
      })
    },

    /* 报名 */
    signUpBiding (row) {
      this.$emit('tab-add', {
        component: vendorBiddingSignUp,
        params: {
          flag: 'edit',
          row: row
        },
        title: row.bidingNum,
        name: `vendorBiddingSignUp${row.bidingNum}`
      })
    },

    /* 查看投标 */
    viewBiding (row) {
      let tab = {
        component: vendorBiddingDetail,
        params: {
          flag: 'view',
          row: row
        },
        title: row.bidingNum,
        name: `vendorBiddingDetail${row.bidingNum}`
      }
      this.$emit('tab-add', tab)
    },

    /* 保证金缴纳 */
    openBondPayDialog (row) {
      this.editRow = {
        id: row.bidingId,
        idKey: 'bidingId'
      }
      this.bondPayDialogVisible = true
    }
  }
}
</script>

<style scoped lang="scss">
.the_vendorBiddingList_wrapper {
  :deep(.el-button-group .el-button ){
    margin-left: 5px !important;
  }
}
</style>
