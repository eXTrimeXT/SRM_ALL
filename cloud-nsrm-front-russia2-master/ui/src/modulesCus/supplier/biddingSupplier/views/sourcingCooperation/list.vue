<template>
  <el-container class="flex-container" direction="vertical">
    <el-main>
      <FormWrapper :form-array="searchFormConfig" :preFormObj="preFormObj" @getFormData="getQueryData" />

      <!-- <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <AuthorityButton type="primary" @click="yixiangClick">
            意向金开票/红字发票申请
          </AuthorityButton>
        </template>
      </MainHeader> -->

      <TableView
        ref="list"
        :table-data="tableData"
        :table-header="tableHeader"
        :pre-query-data="queryParam"
        :com-active="$attrs['changeTab']"
        :checkbox="true"
        :checkChange="handleCurrentChange"
        open-custom-table
        :url="tableViewUrl"
        :adeptMeiQl="true"
      />
      <!-- 报名 -->
      <applyDialog ref="applyDialog" :visible.sync="applyVisible" :editRows="currentRow" @fresh="getQueryData" />
      <!-- 意向金开票申请 -->
      <offertoPayDialog
        ref="offertoPayDialog"
        :visible.sync="offertoPayVisible"
        :editRows="currentRow"
        @offertoFun="offertoFun"
      />
      <!-- 意向金退款详情 -->
      <IntentionDialog
        ref="IntentionDialog"
        :visible.sync="IntentionVisible"
        :editRows="currentRow"
      />
    </el-main>
  </el-container>
</template>

<script>
import { tabTodoWatch } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MainHeader from 'lib@/components/Table/MainHeader'
import { transformMQL } from 'lib@/utils/util'
import SourcingRequireDetail from 'modcb@/sourcing/views/sourcingRequireBuyer/edit'
import soucHttp from '../../api/soucHttp.js'
import IntentionDialog from './dialog/IntentionDialog'
import offertoPayDialog from './dialog/offertoPayDialog'
import applyDialog from './dialog/applyDialog'
import edit from './edit'
import sourcingApplicationDetailNew from './sourcingApplicationDetailNew'
export default {
  name: 'SourcingCooperation',

  components: {
    TableView,
    MainHeader,
    IntentionDialog,
    SourcingRequireDetail,
    offertoPayDialog,
    applyDialog,
    FormWrapper
  },

  mixins: [tabTodoWatch],

  data () {
    return {
      tableViewUrl: soucHttp.listPageUrl,
      tableHeader: [],
      currentRow: {},
      tableData: [],
      IntentionVisible: false,
      offertoPayVisible: false,
      applyVisible: false,
      searchFormConfig: [
        {
          prop: 'projectName',
          label: this.$t('sourcingBuyer.souReqTitile')
        },
        {
          prop: 'reqHeadNo',
          label: this.$t('sourcingBuyer.reqHeadNo')
        },
        {
          prop: 'status',
          label: this.$t('sourcingBuyer.sourcingStatus'),
          type: 'dict',
          code: 'SOU_REQ_HEAD_STATUS'
        },
        {
          prop: 'createdFullName',
          label: this.$t('problemManagement.createdBy')
        },
        {
          prop: 'releaseDate',
          label: this.$t('bidMod.publishDate'),
          type: 'daterange'
        }
      ],
      queryParam: {},
      selectedRows: [], // 标记勾选行
      preFormObj: {}
    }
  },
  watch: {
    $route: {
      deep: true,
      immediate: true,
      handler (newValue) {
        let { from, row, funName, listName } = newValue.params
        if (
          from === 'workCount' &&
          funName === 'sourcingCooperation'
        ) {
          if (listName === 'readProject') {
            this.preFormObj.status = 'APPROVED'
            this.getQueryData({ status: 'APPROVED', todo: 'Y' })
          } else {
            this.getQueryData({ applyStatus: 'FAIL_SIGNUP' })
          }
        } else if (from === 'portal') {
          this.preFormObj = {
            reqHeadNo: row?.formNo
          }
          this.getQueryData({ reqHeadNo: row?.formNo })
        } else {
          this.getQueryData()
        }
      }
    }
  },
  mounted () {
    this.tableHeader = [
      // 寻源需求单号
      {
        prop: 'reqHeadNo',
        label: this.$t('cusEntry.supplement20250205.reqHeadNo'),
        minWidth: 150
      },
      // 板块
      {
        prop: 'orgBuName',
        label: this.$t('cusEntry.bidSuperviseReport.extOrgBuName'),
        minWidth: 120
      },
      // 公司
      {
        prop: 'orgName',
        label: this.$t('cusEntry.orderMod.companyName'),
        minWidth: 150
      },
      {
        prop: 'projectName',
        label: this.$t('bidMod.bidingName'),
        minWidth: 150,
        showType: 'button',
        btnStyle: 'text',
        callback: (row) => {
          this.projectFun(row)
        }
      },
      // 公示截止时间
      {
        prop: 'publicEndTime',
        label: this.$t('cusEntry.reportManagement.publicEndTime'),
        minWidth: 150,
        dataType: 'dateTime'
      },
      {
        prop: 'status',
        label: this.$t('sourcingBuyer.sourcingStatus'),
        dataType: 'dict',
        code: 'SOU_REQ_HEAD_STATUS',
        minWidth: 120
      },
      {
        prop: 'applyStatus',
        label: this.$t('bidMod.signUpStatus'),
        formattor: (val, row) => {
          return this.$getDictLabel('SOU_REQ_APPLY_STATUS', row.souReqApplyList[0]?.applyStatus ? row.souReqApplyList[0].applyStatus : 'NO_SIGNUP')
        },
        minWidth: 120
      },
      {
        prop: 'releaseDate',
        label: this.$t('bidMod.publishDate'),
        minWidth: 120,
        dataType: 'dateTime'
      },
      {
        prop: 'operation',
        label: this.$t('bidMod.operation'),
        minWidth: 130,
        showType: 'buttons',
        fixed: 'right',
        btnStyle: 'text',
        buttons: [
          {
            show: row => row.status === 'APPROVED' && (!row.souReqApplyList[0]?.applyStatus || ['NO_SIGNUP', 'FAIL_SIGNUP', 'WITHDRAW'].includes(row.souReqApplyList[0]?.applyStatus)), // 单据状态接受报名中，报名状态未报名
            formattor: () => this.$t('sourcingBuyer.signUp'),
            callback: row => {
              this.openApplyDialog('edit', row, this.$t('sourcingBuyer.signUp'))
            }
          },
          {
            show: row => row.status !== 'SIGNUP_DONE' , // 单据状态【报名截止】不展示
            formattor: () => this.$t('cusEntry.bidMod.techClarification'), // 技术澄清
            callback: row => this.viewTech(row)
          },
          {
            show: row => ['CONFIRMING_SIGNUP', 'SUCCESS_SIGNUP'].includes(row.souReqApplyList[0]?.applyStatus), // 报名成功，报名确认中
            formattor: () => this.$t('cusEntry.supplement20250121.viewRegistrationDetails'), // 查看报名详情
            callback: row => {
              this.openApplyDialog('view', row, this.$t('sourcingBuyer.viewApply'))
            }
          }
        ]
      }
    ]
  },
  methods: {
    viewTech (row) {
      // 跳转到质疑澄清页面
      this.$router.push({
        name: 'biddingQas',
        params: {
          from: 'sourcingCooperation', // 来源路由name
          flag: 'bid',
          row: {
            souName: row.projectName,
            souNo: row.reqHeadNo,
            projectId: row.projectId || row.requirementHeadId,
            extType: 'REQ'
          }
        }
      })
    },
    projectFun (row) {
      console.log(row, 'row')
      this.$emit('tab-add', {
        component: sourcingApplicationDetailNew,
        params: {
          flag: 'view',
          row: row,
          tabName: 'purchaseApplicationDetail' + row.projectName
        },
        title: row.projectName,
        name: 'purchaseApplicationDetail' + row.projectName
      })
    },
    // ip监控
    ipAddress (row, source) {
      const { companyName, companyId, companyCode } = this.$store.getters.userInfo
      return this.$http({
        url: '/api-sou/bids/ip/address/ipAddress/save',
        method: 'POST',
        data: {
          supplierId: companyId,
          supplierCode: companyCode,
          supplierName: companyName,
          source
        },
        loading: true
      })
    },
    offertoFun (row) {
      this.offertoPayVisible = false
      // const row = this.currentRow
      // console.log(row, 'row')
      this.$emit('tab-add', {
        component: edit,
        params: {
          flag: 'view',
          row: [row],
          tabName: 'sourcing' + row.invoiceNo,
          activeWorkflowTab: true
        },
        title: row.invoiceNo,
        name: 'sourcing' + row.invoiceNo
      })
    },
    async yixiangClick () {
      const checkChangeData = this.selectedRows
      if (checkChangeData.length > 1) {
        // 只能选择一条单据开票，不支持多条同时开票
        this.$message.warning(this.$t('cusEntry.supplement20250205.message1'))
        return false
      } else if (checkChangeData.length <= 0) {
        // 请选择一条单据进行开票
        this.$message.warning(this.$t('cusEntry.supplement20250205.message2'))
        return false
      } else if (!checkChangeData[0].souReqApplyList[0]?.applyStatus) {
        // 未报名不能开票
        this.$message.warning(this.$t('cusEntry.supplement20250205.message3'))
        return false
      } else if (checkChangeData[0].isNeedDeposit === 'N') {
        // 非意向金, 不需要开票
        this.$message.warning(this.$t('cusEntry.supplement20250205.message4'))
        return false
      } else if (checkChangeData[0].souReqApplyList[0].depositStatus !== 'PAID') {
        // 缴纳状态为“已缴纳”，才允许开票
        this.$message.warning(this.$t('cusEntry.supplement20250205.message5'))
        return false
      } else if (['REFUNDING', 'REFUNDED'].includes(checkChangeData[0].souReqApplyList[0].depositRefundStatus)) {
        // 退款状态为退款中，退款成功，不可以申请开票
        this.$message.warning(this.$t('cusEntry.supplement20250205.message6'))
        return false
      }
      if (checkChangeData[0].bankAccount.replace(/\s+/g, '') != '8111801012300947716') {
        this.$message.warning(this.$t('cusEntry.tipMessage.validBankAccountMsg'))
        return false
      }
      
      await this.ipAddress({}, this.$t('cusEntry.supplement20250205.intentionInvoice')) // 意向金开票 ip监控
      this.$emit('tab-add', {
        component: edit,
        params: {
          flag: 'add',
          row: this.selectedRows,
          tabName: 'sourcing',
          activeWorkflowTab: true
        },
        title: this.$t('common.add'),
        name: 'sourcing'
      })
    },
    /* 查询列表数据 */
    getQueryData (params = {}) {
      const filter = {
        vendorId: {
          eq: this.$store.getters.companyId
        }
      }
      if (params.applyStatus === 'FAIL_SIGNUP') {
        filter.applyStatus = {
          eq: params.applyStatus
        }
      }
      if (params.todo) {
        filter.todo = {
          contains: params.todo
        }
      }
      this.queryParam = transformMQL.listPageData({
        type: 'SouReqHead',
        action: 'query',
        sort: 'creationDate',
        query: {
          '*': {},
          souReqApplyList: {
            '*': {},
            $condition: {
              $strictQuery: !!params.applyStatus,
              filter
            }
          }
        },
        params
      })
      this.$nextTick(() => {
        this.$refs.list.query()
      })
    },
    async openApplyDialog (type, row, source) {
      await this.ipAddress(row, source) // ip监控
      this.currentRow = row
      this.currentRow.flag = type
      // 是否可撤回状态
      this.currentRow.isWithdraw = ['CONFIRMING_SIGNUP', 'SUCCESS_SIGNUP'].includes(row.souReqApplyList[0]?.applyStatus) && row.status === 'APPROVED'
      this.applyVisible = true
    },
    /* 选中行 */
    handleCurrentChange (val) {
      console.log('val:::', val)
      this.selectedRows = val
    }
  }
}
</script>
