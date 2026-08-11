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
        ref="bargainListTable"
        :table-data="tableData"
        :table-header="tableHeader"
        :page-size="15"
        :pre-query-data="queryParam"
        :com-active="$attrs['changeTab']"
        url="/api-brg/supplierCooperate/orderHead/queryBargain"
        style="margin-top: 10px"
      />

      <!--保证金缴纳-->
      <BondPayDialog
        v-if="bondPayDialogVisible"
        business-type="BARGAIN"
        :visible.sync="bondPayDialogVisible"
        :base-info="editRow"
        @submitBondSuccess="getQueryData"
      />
    </el-main>
  </el-container>
</template>

<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import { parseTimeYMD } from 'lib@/composition/origin/composition'
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import bargainVendorDetail from './bargainVendorDetail'
import bargainVendorSignUp from './bargainVendorSignUp'
import doBargainDetail from './doBargainDetail'
import BondPayDialog from 'lib@/composition/origin/bondPay/bondPayDialog'

export default {
  name: 'BargainVendorList',

  components: {
    TableView,
    FormWrapper,
    BondPayDialog
  },

  mixins: [tabTodoWatch, tabTodoMixin],

  data () {
    return {
      tableHeader: [],
      tableData: [],
      preFormObj: {},
      preArr: [
        { prop: 'bargainNum', label: this.$t('bidMod.bidingNum') },
        { prop: 'bargainName', label: this.$t('bidMod.bidingName') },
        // 项目状态
        {
          prop: 'bargainStatus',
          label: this.$t('bidMod.projStatus'),
          type: 'dict',
          code: 'BRG_PROJECT_STATUS'
        },
        // 报名状态
        {
          prop: 'signUpStatus',
          label: this.$t('bidMod.signUpStatus'),
          type: 'dict',
          code: 'BRG_SIGN_UP_STATUS'
        },
        // 投标状态
        {
          prop: 'orderStatus',
          label: this.$t('bidMod.orderStatus1'),
          type: 'dict',
          code: 'BRG_ORDER_STATUS'
        },
        // 招标范围
        {
          prop: 'bargainScope',
          label: this.$t('bidMod.bidingScope1'),
          type: 'dict',
          code: 'BARGAIN_SCOPE'
        }
      ],
      queryParam: {},
      editRow: null,
      bondPayDialogVisible: false
    }
  },

  watch: {
    $route: {
      // 当前功能已经打开的时候监听是否是从其他功能跳转过来的
      deep: true,
      immediate: true,
      handler () {
        const params = this.$route.params
        if (params.from === 'workCount' && params.funName === 'BargainVendorList') {
          // 供应商 工作台跳转
          this.queryParam.bargainStatus = params.bargainStatus
          this.preFormObj = Object.assign({}, { bargainStatus: params.bargainStatus })
        }
      }
    }
  },

  created () {
    this.tableHeader = [
      // 项目编号
      {
        prop: 'bargainNum',
        label: this.$t('bidMod.bidingNum'),
        minWidth: 150,
        showType: 'button',
        btnStyle: 'text',
        callback: row => this.viewBargain(row)
      },
      // 项目名称
      {
        prop: 'bargainName',
        label: this.$t('bidMod.bidingName'),
        minWidth: 150,
        formattor: val => val || '--'
      },
      // 招标范围
      {
        prop: 'bargainScope',
        label: this.$t('bidMod.bidingScope1'),
        minWidth: 100,
        formattor: val => this.$getDictLabel('BARGAIN_SCOPE', val)
      },
      // 项目状态
      {
        prop: 'bargainStatus',
        label: this.$t('bidMod.bidingStatus'),
        minWidth: 100,
        formattor: val => this.$getDictLabel('BRG_PROJECT_STATUS', val)
      },
      // 报名状态
      {
        prop: 'signUpStatus',
        label: this.$t('bidMod.signUpStatus'),
        minWidth: 100,
        formattor: val => this.$getDictLabel('BRG_SIGN_UP_STATUS', val)
      },
      // 投标状态
      {
        prop: 'orderStatus',
        label: this.$t('bidMod.orderStatus1'),
        minWidth: 100,
        formattor: val => this.$getDictLabel('BRG_ORDER_STATUS', val)
      },
      // 轮次
      {
        prop: 'round',
        label: this.$t('bidMod.bidingRound'),
        minWidth: 80
      },
      // 调整结束原因
      {
        prop: 'adjustBargainTimeReason',
        label: '调整结束原因',
        minWidth: 120
      },
      // 报价截止时间
      {
        prop: 'bargainEndDatetime',
        label: this.$t('bidMod.quotedeadline'),
        minWidth: 120,
        formattor: val => parseTimeYMD(val)
      },
      // 开标时间
      {
        prop: 'businessOpenBrgTime',
        label: this.$t('bidMod.techOpenBidTime'),
        minWidth: 120,
        formattor: val => parseTimeYMD(val)
      },
      // 发布时间
      {
        prop: 'releaseDateTime',
        label: this.$t('bidMod.releaseDatetime'),
        minWidth: 120,
        formattor: val => parseTimeYMD(val)
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
            show: row => row.bargainStatus === 'ACCEPT_SIGNUP' &&
              ['NO_SIGNUP', 'REJECTED'].includes(row.signUpStatus) &&
              new Date(row.enrollEndDatetime) > new Date(),
            formattor: () => this.$t('bidMod.signUpBiding'),
            callback: row => this.signUpBargain(row)
          },
          // 投标
          {
            // 项目状态：接受投标中 && 投标状态：未投标/已驳回 && 允许投标
            show: row => row.bargainStatus === 'ACCEPT_BRG' &&
              ['DRAFT', 'WITHDRAW'].includes(row.orderStatus) &&
              row.canOrder === 'Y',
            formattor: () => this.$t('bidMod.doBiding1'),
            callback: row => this.doBargain(row)
          },
          // 撤回投标
          {
            // 项目状态：接受投标中 && 投标状态：已投标 && 允许投标 && 允许撤回
            show: row => row.bargainStatus === 'ACCEPT_BRG' &&
              row.orderStatus === 'SUBMISSION' &&
              row.canOrder === 'Y' &&
              row.withdrawBargain === 'Y',
            formattor: () => this.$t('bidMod.rebackBiding1'),
            callback: row => this.withdrawBargain(row)
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
        this.$refs.bargainListTable.query()
      })
    },

    /* 投标 */
    doBargain (row) {
      this.$emit('tab-add', {
        component: doBargainDetail,
        params: {
          flag: 'edit',
          row: row
        },
        title: row.bargainNum,
        name: `doBargainDetail${row.bargainNum}`
      })
    },

    /* 质疑澄清 */
    toChallengeClarification (row) {
      this.$router.push({
        name: 'inquiryByProjectChClVendor',
        params: {
          bargainId: row.bargainId,
          bargainName: row.bargainName,
          bargainNum: row.bargainNum
        }
      })
    },

    /* 撤回投标 */
    withdrawBargain (row) {
      this.$prompt(this.$t('bidMod.withdrawReason'), this.$t('bidMod.rebackBiding1'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        inputValidator: value => !(!value || value.length > 200),
        inputErrorMessage: this.$t('bidMod.biddingManagementSupplier.withDrawReasonRequired')
      }).then(({ value }) => {
        this.$http({
          url: '/api-brg/supplierCooperate/orderHead/withdrawOrder',
          method: 'POST',
          data: {
            orderHeadId: row.orderHeadId,
            withDrawReason: value
          },
          loading: true
        }).then(() => {
          this.$message.success(this.$t('bidMod.withdrawQuoteSuc'))
          this.getQueryData()
        })
      })
    },

    /* 报名 */
    signUpBargain (row) {
      this.$emit('tab-add', {
        component: bargainVendorSignUp,
        params: {
          flag: 'edit',
          row: row
        },
        title: row.bargainNum,
        name: `bargainVendorSignUp${row.bargainNum}`
      })
    },

    /* 查看投标 */
    viewBargain (row) {
      let tab = {
        component: bargainVendorDetail,
        params: {
          flag: 'view',
          row: row
        },
        title: row.bargainNum,
        name: `bargainVendorDetail${row.bargainNum}`
      }
      this.$emit('tab-add', tab)
    },

    /* 保证金缴纳 */
    openBondPayDialog (row) {
      this.editRow = {
        id: row.bargainId,
        idKey: 'bargainId'
      }
      this.bondPayDialogVisible = true
    }
  }
}
</script>

<style scoped lang="scss">
:deep(.the_vendorBiddingList_wrapper) {
  .el-button-group .el-button {
    margin-left: 5px !important;
  }
}
</style>
