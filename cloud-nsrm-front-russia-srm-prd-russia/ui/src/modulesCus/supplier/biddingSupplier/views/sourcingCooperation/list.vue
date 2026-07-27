<template>
  <el-container class="flex-container" direction="vertical">
    <el-main>
      <FormWrapper :form-array="searchFormConfig" :preFormObj="preFormObj" @getFormData="getQueryData" />

      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <AuthorityButton type="primary" @click="yixiangClick">
            意向金开票/红字发票申请
          </AuthorityButton>
        </template>
      </MainHeader>

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
          label: '需求标题'
        },
        {
          prop: 'reqHeadNo',
          label: '寻源单号'
        },
        {
          prop: 'status',
          label: '单据状态',
          type: 'dict',
          code: 'SOU_REQ_HEAD_STATUS'
        },
        {
          prop: 'createdFullName',
          label: '创建人'
        },
        {
          prop: 'releaseDate',
          label: '发布日期',
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
      {
        prop: 'reqHeadNo',
        label: '寻源需求单号',
        minWidth: 150
        // showType: 'button',
        // btnStyle: 'text',
        // callback: row => {
        //   console.log('row:::', row)
        //   this.editTab('view', row)
        // }
      },
      {
        prop: 'orgBuName',
        label: '板块',
        minWidth: 120
      },
      {
        prop: 'orgName',
        label: '公司',
        minWidth: 150
      },
      {
        prop: 'projectName',
        label: '项目名称',
        minWidth: 150,
        showType: 'button',
        btnStyle: 'text',
        callback: (row) => {
          this.projectFun(row)
        }
      },
      {
        prop: 'publicEndTime',
        label: '公示截止时间',
        minWidth: 150
      },
      {
        prop: 'status',
        label: '单据状态',
        dataType: 'dict',
        code: 'SOU_REQ_HEAD_STATUS',
        minWidth: 120
      },
      {
        prop: 'applyStatus',
        label: '报名状态',
        formattor: (val, row) => {
          return this.$getDictLabel('SOU_REQ_APPLY_STATUS', row.souReqApplyList[0]?.applyStatus ? row.souReqApplyList[0].applyStatus : 'NO_SIGNUP')
        },
        minWidth: 120
      },
      {
        prop: 'releaseDate',
        label: '发布日期',
        minWidth: 120
      },
      {
        prop: 'billingDetails',
        label: '意向金开票详情',
        minWidth: 130,
        showType: 'buttons',
        btnStyle: 'text',
        buttons: [
          {
            // show: row => ['DRAFT', 'WITHDRAW', 'REJECTED'].includes(row.status),
            formattor: () => this.$t('common.view'),
            callback: row => {
              this.openOffertoPayDialog(row, '查看意向金开票')
            }
          }
        ]
      },
      {
        prop: 'refundDetails',
        label: '意向金退款详情',
        minWidth: 130,
        showType: 'buttons',
        btnStyle: 'text',
        buttons: [
          {
            // show: row => ['DRAFT', 'WITHDRAW', 'REJECTED'].includes(row.status),
            formattor: () => this.$t('common.view'),
            callback: row => {
              this.openRefundDetailsDialog(row, '查看意向金退款')
            }
          }
        ]
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
            formattor: () => '报名',
            callback: row => {
              this.openApplyDialog('edit', row, '报名')
            }
          },
          {
            show: row => ['CONFIRMING_SIGNUP', 'SUCCESS_SIGNUP'].includes(row.souReqApplyList[0]?.applyStatus), // 报名成功，报名确认中
            formattor: () => '查看报名详情',
            callback: row => {
              this.openApplyDialog('view', row, '查看报名')
            }
          }
        ]
      }
    ]
  },
  methods: {
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
        this.$message.warning('只能选择一条单据开票，不支持多条同时开票')
        return false
      } else if (checkChangeData.length <= 0) {
        this.$message.warning('请选择一条单据进行开票')
        return false
      } else if (!checkChangeData[0].souReqApplyList[0]?.applyStatus) {
        this.$message.warning('未报名不能开票')
        return false
      } else if (checkChangeData[0].isNeedDeposit === 'N') {
        this.$message.warning('非意向金, 不需要开票')
        return false
      } else if (checkChangeData[0].souReqApplyList[0].depositStatus !== 'PAID') {
        this.$message.warning('金缴纳状态为“已缴纳”，才允许开票')
        return false
      } else if (['REFUNDING', 'REFUNDED'].includes(checkChangeData[0].souReqApplyList[0].depositRefundStatus)) {
        this.$message.warning('退款状态为退款中，退款成功，不可以申请开票')
        return false
      }
      if (checkChangeData[0].bankAccount.replace(/\s+/g, '') != '8111801012300947716') {
        this.$message.warning(this.$t('cusEntry.tipMessage.validBankAccountMsg'))
        return false
      }
      await this.ipAddress({}, '意向金开票') // ip监控
      this.$emit('tab-add', {
        component: edit,
        params: {
          flag: 'add',
          row: this.selectedRows,
          tabName: 'sourcing',
          activeWorkflowTab: true
        },
        title: '新增',
        name: 'sourcing'
      })
    },
    async openRefundDetailsDialog (row, source) {
      await this.ipAddress(row, source) // ip监控
      this.currentRow = row
      this.IntentionVisible = true
    },
    async openOffertoPayDialog (row, source) {
      await this.ipAddress(row, source) // ip监控
      this.currentRow = row
      this.offertoPayVisible = true
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
    editTab (type, row) {
      const map = new Map([
        // 新增
        [
          'add',
          {
            component: SourcingRequireDetail,
            params: {
              flag: type,
              row,
              tabName: 'sourcingRequireBuyer'
            },
            title: '寻源需求新增',
            name: 'sourcingRequireBuyer'
          }
        ],
        // 编辑
        [
          'edit',
          {
            component: SourcingRequireDetail,
            params: {
              flag: type,
              row,
              tabName: row.reqHeadNo
            },
            title: '寻源需求' + (row.reqHeadNo || ''),
            name: row.reqHeadNo
          }
        ],
        // 查看
        [
          'view',
          {
            component: SourcingRequireDetail,
            params: {
              flag: type,
              row,
              tabName: row.reqHeadNo
            },
            title: '寻源需求' + (row.reqHeadNo || ''),
            name: row.reqHeadNo
          }
        ],
        // 审批
        [
          'approval',
          {
            component: SourcingRequireDetail,
            params: {
              flag: type,
              row,
              tabName: row.reqHeadNo,
              activeWorkflowTab: true // 跳转到审批流
            },
            title: '寻源需求' + (row.reqHeadNo || ''),
            name: row.reqHeadNo
          }
        ]
      ])
      this.$emit('tab-add', map.get(type))
    },

    /* 选中行 */
    handleCurrentChange (val) {
      console.log('val:::', val)
      this.selectedRows = val
    }
  }
}
</script>
