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
        :cell-style="cellStyle"
        open-custom-table
        url="/api-sou/ext/vendor/bid/getPage"
      />
      <!--查看招标文件-->
      <BidFileDialog
        v-if="bidFileDialogVisible"
        :business-type="BUSINESS_TYPE_ENUM.BIDDING_LTS"
        :visible.sync="bidFileDialogVisible"
        :base-info="editRow"
      />
      <!--保证金缴纳-->
      <BondPayDialog
        v-if="bondPayDialogVisible"
        :business-type="BUSINESS_TYPE_ENUM.BIDDING_LTS"
        :visible.sync="bondPayDialogVisible"
        :readonly="bondPayReadonly"
        :base-info="editRow"
        @success="getQueryData"
      />
      <!--确认投标-->
      <ConfirmBidDialog
        v-if="confirmBidDialogVisible"
        :business-type="BUSINESS_TYPE_ENUM.BIDDING_LTS"
        :visible.sync="confirmBidDialogVisible"
        :base-info="editRow"
        @success="getQueryData"
      />
      <!-- 查看结果 -->
      <BidResultDialog
        v-if="bidResultDialogVisible"
        :visible.sync="bidResultDialogVisible"
        :base-info="editRow"
      />
      <!-- 查看保证金退款详情 -->
      <BondRefundDialog
        :visible.sync="showReFund"
        :show-data="bondRefundShowData"
      />
    </el-main>
  </el-container>
</template>
<script>
import { bidSupplierHttp } from 'modcs@/biddingSupplier/api'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import { BUSINESS_TYPE_ENUM } from 'lib@/composition/origin/enum'
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import biddingVendorDetail from './biddingOrdersDetail.vue'
import biddingOrdersQuote from './biddingOrdersQuote.vue'
import BidResultDialog from './composition/bidResultDialog.vue'
import BondPayDialog from './composition/bondPayDialog.vue'
import ConfirmBidDialog from './composition/confirmBidDialog.vue'
import BidFileDialog from './composition/bidFileDialog.vue'
import BondRefundDialog from './composition/bondRefundDialog'
import { calcDate } from 'lib@/utils/date-format'

export default {
  name: 'BiddingOrdersList',
  components: {
    TableView,
    FormWrapper,
    BondPayDialog,
    ConfirmBidDialog,
    BidFileDialog,
    BidResultDialog,
    BondRefundDialog
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      isExamFlag: false,
      bondRefundShowData: {},
      preFormObj: {},
      preArr: [
        // { prop: 'souNo', label: this.$t('cusEntry.biddingSettings.bidingNum') },
        // {
        //   prop: 'projectStatus',
        //   label: this.$t('cusEntry.biddingSettings.projectStatus'),
        //   type: 'dict',
        //   code: 'SOU_BIDDING_PRO_STATUS'
        // },
        { prop: 'souName', label: this.$t('bidMod.bidingName') },
        {
          prop: 'orderStatus',
          label: this.$t('bidMod.orderStatus'),
          type: 'dict',
          code: 'SOU_ORDER_STATUS'
        },
        { prop: 'createdFullName', label: '招标负责人' },
        // {
        //   prop: 'creationDateFrom',
        //   label: this.$t('common.creationDate'),
        //   type: 'daterange'
        // },
        {
          prop: 'publishTimeFrom',
          label: this.$t('bidMod.publishDate'),
          type: 'daterange'
        }
      ],
      pageSize: 15,
      queryParam: {},
      tableData: [],
      tableHeader: [
        // 招标单号
        // {
        //   prop: 'souNo',
        //   label: this.$t('cusEntry.biddingSettings.bidingNum'),
        //   minWidth: 150
        // },
        // 招标项目编号
        {
          prop: 'extProjectNo',
          label: this.$t('bidMod.bidingNumCla'),
          minWidth: 150
        },
        // 项目名称
        {
          prop: 'souName',
          label: this.$t('bidMod.bidingName'),
          minWidth: 150
        },
        // 公司
        {
          prop: 'extOrgOuName',
          label: this.$t('cusEntry.common.company'),
          minWidth: 150
        },
        // 项目状态
        // {
        //   prop: 'projectStatus',
        //   label: this.$t('cusEntry.biddingSettings.projectStatus'),
        //   minWidth: 150,
        //   formattor: val => this.$getDictLabel('SOU_BIDDING_PRO_STATUS', val)
        // },
        // 招标流程
        // {
        //   prop: 'extSouProcess',
        //   label: this.$t('cusEntry.biddingSettings.bidProcess'),
        //   minWidth: 150,
        //   formattor: val => this.$getDictLabel('SOU_BID_PROCCESS', val)
        // },
        // 投标状态 作废-CANCEL  未投标-DRAFT 已投标-SUBMISSION
        {
          prop: 'orderStatus',
          label: this.$t('bidMod.orderStatus'),
          minWidth: 150,
          formattor: val => this.$getDictLabel('SOU_ORDER_STATUS', val)
        },
        // 保证金缴纳状态
        {
          prop: 'marginStatus',
          label: this.$t('cusEntry.biddingSettings.payStatus'),
          minWidth: 150,
          formattor: val => this.$getDictLabel('SOU_BID_MARGIN_STATUS', val)
        },
        // 投标截止时间
        {
          prop: 'orderEndTime',
          label: this.$t('bidMod.enrollEndDatetime'),
          minWidth: 150
        },
        // {
        //   prop: 'farFromOrderEndTime',
        //   label: '距离投标截止剩余',
        //   formattor: (val, row) => {
        //     const now = new Date().getTime()
        //     const countDown = new Date(row.orderEndTime).getTime()
        //     if (now === countDown) return 0

        //     const timeObj = calcDate(now, countDown)
        //     const day = timeObj.days > 0 ? timeObj.days : 0
        //     const hours = timeObj.hours > 0 ? timeObj.hours : 0
        //     const minutes = timeObj.minutes > 0 ? timeObj.minutes : 0
        //     const seconds = timeObj.seconds > 0 ? timeObj.seconds : 0

        //     return row.orderEndTime ? day + '天' + hours + '小时' + minutes + '分钟' + seconds + '秒' : null
        //   },
        //   minWidth: 150
        // },
        // 创建人
        {
          prop: 'createdFullName',
          label: '招标负责人',
          minWidth: 150
        },
        {
          prop: 'tel',
          label: '办公电话',
          minWidth: 150
        },
        // 创建日期
        // {
        //   prop: 'projectCreationDate',
        //   label: this.$t('common.creationDate'),
        //   minWidth: 150
        // },
        // 发布日期
        {
          prop: 'publishTime',
          label: this.$t('bidMod.publishDate'),
          minWidth: 150
        },
        // 查看投标历史
        {
          label: this.$t('cusEntry.biddingSettings.viewBidHistory'),
          showType: 'button',
          btnStyle: 'text',
          callback: row => this.openQuoteTab(row, 'view'),
          formattor: () => this.$t('common.view'),
          minWidth: 120
        },
        // 保证金退款详情
        {
          label: this.$t('cusEntry.biddingSettings.bondRefundHistory'),
          showType: 'button',
          btnStyle: 'text',
          callback: row => this.readRefundDetail(row),
          formattor: () => this.$t('common.view'),
          minWidth: 140
        },
        {
          prop: 'operation',
          label: this.$t('bidMod.operation'),
          minWidth: 240,
          showType: 'buttons',
          fixed: 'right',
          btnStyle: 'text',
          buttons: [
            // 查看招标文件
            {
              formattor: () => this.$t('cusEntry.biddingSettings.viewBidFile'),
              callback: row => this.openBidFileDialog(row)
            },
            // 缴纳保证金
            {
              // 是否允许不缴纳保证金!= 'Y' && 项目状态：商务标已截止前的状态都要可见 && 缴纳状态：未缴纳、缴纳失败
              show: row => row.extEarnestFlag !== 'Y' &&
                ['TECH_BID', 'TECH_BID_END', 'TECH_BID_OPEN', 'TECH_BID_EVA', 'BUS_BID'].includes(row.projectStatus) &&
                ['NOT_PAY', 'FAIL_PAY'].includes(row.marginStatus),
              formattor: () => this.$t('cusEntry.biddingSettings.payDeposit'),
              callback: row => this.openBondPayDialog('edit', row)
            },
            // 查看保证金
            {
              // 缴纳状态：已缴纳、待确认
              show: row => row.extEarnestFlag !== 'Y' && ['PAY', 'CONFIRM_TODO'].includes(row.marginStatus),
              formattor: () => this.$t('cusEntry.biddingSettings.viewDeposit'),
              callback: row => this.openBondPayDialog('view', row)
            },
            // 确认投标
            {
              // 项目状态：技术投标中、商务投标中（投标中） && extTenderFlag != 'Y'
              show: row => ['TECH_BID', 'BUS_BID'].includes(row.projectStatus) && row.extTenderFlag !== 'Y',
              formattor: () => this.$t('cusEntry.biddingSettings.confirmBid'),
              callback: row => this.openConfirmBidDialog(row)
            },
            // 投标
            {
              // 项目状态：技术投标中、商务投标中（投标中） && 点击【确认投标】后 extTenderFlag='Y' 出现【投标】按钮
              // && 投标状态：未投标，已撤回 && 当前轮次标识=Y 可根据投标状态控制投标或者撤回投标
              show: row => ['TECH_BID', 'BUS_BID'].includes(row.projectStatus) && row.extTenderFlag === 'Y' &&
                row.currentRoundFlag === 'Y' && ['DRAFT', 'WITHDRAW'].includes(row.orderStatus),
              formattor: () => this.$t('bidMod.doBiding'),
              callback: row => this.openQuoteTab(row, 'edit')
            },
            // 撤回投标
            {
              // 项目状态：技术投标中、商务投标中（投标中） && 投标状态：已投标 && 当前轮次标识=Y
              show: row => ['TECH_BID', 'BUS_BID'].includes(row.projectStatus) && row.orderStatus === 'SUBMISSION' &&
                row.currentRoundFlag === 'Y',
              formattor: () => this.$t('bidMod.rebackBiding'),
              callback: row => this.allowWithdraw(row)
            },
            // 查看结果
            {
              // 项目状态：待归档 已归档
              // 2024.1.31 新加校验 中/标通知书，是否发送=是
              show: row => ['ARCHIVE_TODO', 'ARCHIVE_DONE'].includes(row.projectStatus) && row.isSend === 'Y',
              formattor: () => this.$t('bidMod.viewResults'),
              callback: row => this.openbidResultDialog(row)
            },
            // 质疑/澄清
            // 点击【确认投标】后 extTenderFlag='Y' 出现【发起质疑】按钮
            {
              //  项目状态：商务标已截止前所有状态
              show: row => row.extTenderFlag === 'Y' && row.orderStatus !== 'SUBMISSION' &&
                ((['BUS_BID_END', 'BUS_BID'].includes(row.projectStatus) && row.extSouMode === 'SAME_TIME') ||
                (['TECH_BID', 'TECH_BID_END'].includes(row.projectStatus) && row.extSouMode === 'TECH_THEN_BUS')),
              formattor: () => '发起质疑',
              callback: row => this.toChallengeClarification(row)
            }
          ]
        }
      ],
      editRow: {},
      bondPayReadonly: false,
      bondPayDialogVisible: false,
      BUSINESS_TYPE_ENUM,
      confirmBidDialogVisible: false,
      bidFileDialogVisible: false,
      bidResultDialogVisible: false,
      showReFund: false
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
        if (this.$route.name === 'biddingList') {
          this.isExamCheck()
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
    // 查看保证金退款详情
    async readRefundDetail (row) {
      const res = await bidSupplierHttp.order.getBondRefundDetail(row.projectId)
      if (res.data && res.data.length) {
        this.bondRefundShowData = res.data[0]
      }
      this.showReFund = true
    },
    cellStyle ({ row, column, rowIndex, columnIndex }) {
      if (column.property == 'farFromOrderEndTime') {
        return {
          'color': '#FF4A4D'
        }
      }
    },
    /* 查询列表数据 */
    getQueryData (val = {}) {
      this.queryParam = Object.assign({}, val || this.preFormObj)
      this.$nextTick(() => {
        this.$refs.biddingListTable.query()
      })
    },
    // 查看招标文件
    openBidFileDialog (row) {
      this.editRow = {
        id: row.projectId,
        idKey: 'projectId',
        mergeFlag: row.mergeFlag
      }
      this.bidFileDialogVisible = true
    },
    /* 保证金缴纳 */
    openBondPayDialog (type, row) {
      // 在此需要做廉洁考试校验
      if (this.isExamFlag) {
        this.isExamTipShow()
      } else {
        this.editRow = {
          id: row.projectId,
          idKey: 'projectId',
          vendorId: row.vendorId
        }
        this.bondPayReadonly = type == 'view'
        this.bondPayDialogVisible = true
      }
    },
    // 确认投标
    openConfirmBidDialog (row) {
      this.editRow = {
        id: row.orderId,
        idKey: 'orderId'
      }
      this.confirmBidDialogVisible = true
    },
    // 查看投标结果
    openbidResultDialog (row) {
      // 在此需要做廉洁考试判断
      if (this.isExamFlag) {
        this.isExamTipShow()
      } else {
        this.editRow = {
          id: row.projectId,
          idKey: 'projectId'
        }
        this.bidResultDialogVisible = true
      }
    },
    /* 投标 *//* 查看投标历史 */
    openQuoteTab (row, flag) {
      // 在此需要做廉洁考试判断 flag=edit时
      if (flag === 'edit' && this.isExamFlag) {
        this.isExamTipShow()
      } else {
        if (flag == 'edit') {
          // 是否允许不缴纳保证金!='Y' && 保证金缴纳状态不为已缴纳 &&
          // (收标方式：先收技术后收商务 && 项目状态为：技术投标中) || (同时收标 && 商务投标中)
          let quoteFlag = (row.extSouMode === 'TECH_THEN_BUS' && row.projectStatus == 'TECH_BID') || (row.extSouMode === 'SAME_TIME' && row.projectStatus == 'BUS_BID')
          // if (row.extEarnestFlag != 'Y' && quoteFlag && row.marginStatus != 'PAY') {
          //   this.$message.error('请先缴纳保证金')
          //   return
          // }
        }

        // 投标的类型，t是技术，b是商务, all是所有
        let type = 'all'
        // 询比价招标流程只有商务标部分
        if (row.extSouProcess == 'INQUIRY') {
          type = 'b'
        }
        // 单据状态为【技术投标中】 打开技术标页面 (只有收标方式为先收技术后收商务，才有【技术投标中】单据状态)
        // 单据状态为【商务投标中】 收标方式为【先收技术后收商务】打开b商务标页面 ，否则打开all投标页面
        if (row.round == 1 && row.extSouMode == 'TECH_THEN_BUS') {
          if (row.projectStatus == 'TECH_BID') {
            type = 't'
          } else if (row.projectStatus == 'BUS_BID') {
            type = 'b'
          }
        } else if (row.round != 1) { // 第二轮及以后轮次 投标只投商务标，不展示技术标内容
          type = 'b'
        }

        this.$emit('tab-add', {
          component: biddingOrdersQuote,
          params: {
            flag,
            type: flag == 'edit' ? type : 'all',
            row: row,
            tabName: `biddingOrdersQuote${row.souNo}`
          },
          title: flag == 'edit' ? '供应商投标' : '供应商投标历史',
          name: `biddingOrdersQuote${row.souNo}`
        })
      }
    },
    /* 撤回投标 */
    async allowWithdraw (row) {
      const promptResult = await this.$prompt(
        this.$t('bidMod.withdrawReason'),
        '撤回投标',
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
      this.$http({
        url: '/api-sou/ext/vendor/bid/withdrawTender',
        method: 'POST',
        data: {
          withdrawReason: promptResult.value,
          orderId: row?.orderId
        },
        loading: true
      }).then(res => {
        this.$message.success('撤回成功')
        this.getQueryData()
      })
    },
    /* 质疑澄清 */
    toChallengeClarification (row) {
      // 在此需要做廉洁考试判断
      if (this.isExamFlag) {
        this.isExamTipShow()
      } else {
        this.$router.push({
          name: 'biddingQas',
          params: {
            from: 'portal', // 来源路由name
            flag: 'bid',
            row: {
              extType: 'SOU',
              projectId: row.projectId,
              souName: row.souName,
              souNo: row.souNo,
              row
            }
          }
        })
      }
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
    },
    // 廉洁考试提示框
    isExamTipShow () {
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
    }
  }
}
</script>
