<template>
  <el-container class="flex-container the_vendorBiddingList_wrapper" direction="vertical">
    <el-main>
      <FormWrapper
        :form-array="searchFormConfig"
        @getFormData="getQueryData"
      />

      <TableView
        ref="list"
        :table-data="tableData"
        :table-header="tableHeader"
        :pre-query-data="queryParam"
        :com-active="$attrs['changeTab']"
        :adeptMeiQl="true"
        open-custom-table
        :url="tableViewUrl"
      />

      <!--保证金缴纳-->
      <BondPayDialog
        v-if="bondPayDialogVisible"
        :business-type="BUSINESS_TYPE_ENUM.COMPETITION_LTS"
        :visible.sync="bondPayDialogVisible"
        :base-info="editRow"
        :readonly="editRow.readonly"
        @success="getQueryData"
      />

      <!--查看中标结果-->
      <ResultDialog
        v-if="resultDialogVisible"
        :visible.sync="resultDialogVisible"
        :view-row="viewResultRow"
      />
    </el-main>
  </el-container>
</template>

<script>
import { carVendorHttp } from 'mods@/competitionSupplier/api'
import { BUSINESS_TYPE_ENUM, SOU_SIGN_UP_STATUS_ENUM } from 'lib@/composition/origin/enum'
import { SOU_AUCT_PROJECT_STATUS_ENUM } from 'lib@/composition/competition/utils'
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import competitionProjectQuote from './competitionOrdersQuote.vue'
import competitionProjectSignUp from './competitionProjectSignUp.vue'
import competitionProjectDetail from './competitionProjectDetail.vue'
import BondPayDialog from 'lib@/composition/origin/bondPayMQL/bondPayDialogNew.vue'
import ResultDialog from './competitionList/resultDialog'
import { mapState } from 'vuex'
import { transformMQL } from 'lib@/utils/util'

export default {
  name: 'CompetitionOrdersList',

  components: {
    TableView,
    FormWrapper,
    BondPayDialog,
    ResultDialog
  },

  data () {
    return {
      viewResultRow: {},
      resultDialogVisible: false,
      tableViewUrl: carVendorHttp.order.listPageUrl,
      gridData: [],
      tableHeader: [
        // 竞价单号
        {
          prop: 'souNo',
          label: this.$t('bidMod.competitionLts.souNo'),
          minWidth: 150,
          showType: 'button',
          btnStyle: 'text',
          callback: row => this.openDetailTab(row, 'view')
        },
        // 项目名称
        {
          prop: 'souName',
          label: this.$t('bidMod.competitionLts.souName'),
          minWidth: 150
        },
        // 竞价状态
        {
          prop: 'projectStatus',
          label: this.$t('bidMod.competitionLts.souStatus'),
          minWidth: 100,
          dataType: 'dict',
          code: 'SOU_AUCT_PROJECT_STATUS'
        },
        // 报名状态
        {
          prop: 'signUpStatus',
          label: this.$t('bidMod.signUpStatus'),
          minWidth: 100,
          dataType: 'dict',
          code: 'SOU_SIGN_UP_STATUS'
        },
        // 报价状态
        {
          prop: 'orderStatus',
          label: this.$t('bidMod.orderStatus1'),
          minWidth: 100,
          dataType: 'dict',
          code: 'SOU_ORDER_STATUS'
        },
        // 轮次
        {
          prop: 'currentRound',
          label: this.$t('bidMod.bidingRound'),
          minWidth: 100
        },
        // 发布人
        {
          prop: 'createdUserName',
          label: this.$t('bidMod.publishBy'),
          minWidth: 100
        },
        // 创建日期
        {
          prop: 'creationDate',
          label: this.$t('bidMod.dateCreated'),
          minWidth: 100,
          formattor: val => this.$parseTime(val)
        },
        // 发布日期
        {
          prop: 'publishTime',
          label: this.$t('bidMod.publishDate'),
          minWidth: 100,
          formattor: val => this.$parseTime(val)
        },
        // 报名截止时间
        {
          prop: 'signUpEndTime',
          label: this.$t('bidMod.registrationDeadline'),
          minWidth: 120,
          dataType: 'dateTime'
        },
        // 竞价开始时间
        {
          prop: 'orderStartTime',
          label: this.$t('bidMod.bidStartTime'),
          minWidth: 150,
          dataType: 'dateTime'
        },
        // 竞价结束时间
        {
          prop: 'orderEndTime',
          label: this.$t('competition.orderEndTimeCom'),
          minWidth: 150,
          dataType: 'dateTime'
        },
        {
          prop: 'operation',
          label: this.$t('bidMod.operation'),
          minWidth: 140,
          showType: 'buttons',
          fixed: 'right',
          btnStyle: 'text',
          buttons: [
            // 报名
            {
              // 未报名 && 接受报名中 && 存在报名节点 && 单据状态要是接受报名中
              show: row => [SOU_SIGN_UP_STATUS_ENUM.NO_SIGN_UP, SOU_SIGN_UP_STATUS_ENUM.REJECTED].includes(row.signUpStatus) &&
                row.hasSignUpNode === 'Y' && row.projectStatus === SOU_AUCT_PROJECT_STATUS_ENUM.ACCEPT_SIGN_UP,
              callback: row => this.signUp(row),
              formattor: () => this.$t('bidMod.signUpBiding')
            },
            // 报价
            {
              // 接受投标中 如存在保证金节点需要完成保证金
              show: row => row.projectStatus === SOU_AUCT_PROJECT_STATUS_ENUM.ACCEPT_ORDER && row.canOrder === 'Y',
              callback: row => this.quote(row),
              formattor: () => this.$t('bidMod.doQuote')
            },
            // 提交保证金
            {
              // 存在保证金节点 && 未缴纳保证金 && 如存在报名节点，需要报名才能提交
              show: row => row.hasBondNode === 'Y' &&
                (
                  row.hasSignUpNode === 'N' ||
                  (
                    row.hasSignUpNode === 'Y' &&
                    row.signUpStatus === SOU_SIGN_UP_STATUS_ENUM.SIGN_UP_DONE
                  )
                ),
              callback: row => this.openBondPayDialog(row),
              formattor: (val, row) => row.bondStatus !== 'PASS' ? this.$t('competition.submitBondPay') : this.$t('competition.viewBondPay')
            },
            // 查看结果
            {
              // 已定价
              show: row => row.projectStatus === SOU_AUCT_PROJECT_STATUS_ENUM.PRICE_END,
              callback: row => this.openBidingResultDialog(row),
              formattor: () => this.$t('bidMod.viewWinResults')
            }
          ]
        }
      ],
      tableData: [],
      searchFormConfig: [
        // 项目编号
        { prop: 'souName', label: this.$t('bidMod.competitionLts.souName') },
        // 项目名称
        { prop: 'souNo', label: this.$t('bidMod.competitionLts.souNo') },
        // 竞价状态
        {
          prop: 'projectStatus',
          label: this.$t('bidMod.competitionLts.souStatus'),
          type: 'dict',
          code: 'SOU_AUCT_PROJECT_STATUS'
        },
        // 报名状态
        { prop: 'signUpStatus',
          label: () => this.$t('bidMod.signUpStatus'),
          type: 'dict',
          code: 'SOU_SIGN_UP_STATUS'
        },
        // 报价状态
        {
          prop: 'orderStatus',
          label: () => this.$t('bidMod.quoteStatus'),
          type: 'dict',
          code: 'SOU_ORDER_STATUS'
        }
      ],
      queryParam: {},
      bondPayDialogVisible: false,
      editRow: {},
      BUSINESS_TYPE_ENUM
    }
  },

  computed: {
    ...mapState({
      userInfo: state => state.user.userInfo
    })
  },

  mounted () {
    this.$nextTick(() => {
      this.getQueryData()
    })
  },

  methods: {
    /* 查询 */
    getQueryData (params = {}) {
      this.queryParam = transformMQL.listPageData({
        type: 'AuctSouProjectForVendor',
        action: 'listOrders',
        params: {
          ...params,
          vendorId: this.userInfo.companyId || 1
        }
      })
      this.$nextTick(() => {
        this.$refs.list.query()
      })
    },

    /* 查看中标结果 */
    openBidingResultDialog (row) {
      this.viewResultRow = row
      this.resultDialogVisible = true
    },

    /* 保证金缴纳 */
    openBondPayDialog (row) {
      this.editRow = {
        id: row.projectId,
        idKey: 'projectId',
        readonly: row.bondStatus === 'PASS'
      }
      this.bondPayDialogVisible = true
    },

    /* 报名 */
    signUp (row) {
      this.$emit('tab-add', {
        component: competitionProjectSignUp,
        params: {
          flag: 'edit',
          row: row,
          tabName: `competitionProjectSignUp${row.souNo}`
        },
        title: row.souNo,
        name: `competitionProjectSignUp${row.souNo}`
      })
    },

    /* 报价 */
    quote (row) {
      this.$emit('tab-add', {
        component: competitionProjectQuote,
        params: {
          flag: 'edit',
          row: row,
          tabName: `competitionProjectQuote${row.souNo}`
        },
        title: row.souNo,
        name: `competitionProjectQuote${row.souNo}`
      })
    },

    /* 打开详情 */
    openDetailTab (row, type) {
      this.$emit('tab-add', {
        component: competitionProjectDetail,
        params: {
          flag: 'view',
          row: row,
          type,
          tabName: `competitionProjectDetail${row.souNo}`
        },
        title: row.souNo,
        name: `competitionProjectDetail${row.souNo}`
      })
    }
  }
}
</script>

<style scoped lang="scss">
.the_vendorBiddingList_wrapper ::v-deep {
  .el-button-group .el-button {
    margin-left: 5px !important;
  }
}
</style>
