<template>
  <el-container
    class="flex-container the_approvalFlowList_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="queryForm"
        :colLength="1"
        @getFormData="getQuerydata"
      />
      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <div class="header">
            <div class="tabs">
              <div
                v-for="(item,index) in tabList"
                :key="index"
                class="btn"
                :class="{active:tabValue === index }"
                @click="tabClick(item.url,index)"
              >
                {{ item.text }}
              </div>
            </div>
          </div>
        </template>
      </MainHeader>
      <el-container direction="vertical">
        <el-main style="flex-grow: 1;display: flex;flex-direction: column;position:relative;">
          <div class="tableForm">
            <el-table
              ref="flowTable"
              v-loading="loading"
              stripe
              border
              height="100%"
              :data="flowData"
              style="height: 100%;"
            >
              <el-table-column type="index" />
              <!-- 标题 -->
              <el-table-column
                prop="title"
                :label="$t('dashboard.title')"
                show-overflow-tooltip
                sortable
              >
                <template slot-scope="scope">
                  <div
                    style="cursor: pointer"
                    @click="goToForm(scope.row)"
                  >
                    {{ scope.row.title }}
                  </div>
                </template>
              </el-table-column>
              <!-- 创建人 -->
              <el-table-column
                width="100px"
                prop="createName"
                :label="$t('dashboard.creator')"
                show-overflow-tooltip
                sortable
              />
              <!-- 创建时间 -->
              <el-table-column
                prop="creationDate"
                :label="$t('dashboard.creationDate')"
                show-overflow-tooltip
                sortable
              >
                <template slot-scope="scope">
                  {{$parseTime(scope.row.creationDate)}}
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-main>
        <el-footer
          class="pageFooter"
        >
          <CPagination
            ref="queryPagination"
            class="c-query-table-pagination"
            :total="pageInfo.total"
            :page-num="pageInfo.pageNum"
            :page-size="pageInfo.pageSize"
            @current-change="handleCurrentChange"
            @size-change="handleSizeChange"
          />
        </el-footer>
      </el-container>
    </el-main>
  </el-container>
</template>
<script>
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import CPagination from 'lib@/components/c-pagination'
import { getMyTaskProcess } from '@/api/workFlow'

export default {
  name: 'ProcessTaskList',
  components: {
    CPagination,
    MainHeader,
    FormWrapper
  },
  props: {
    params: {
      type: Array,
      default: () => {
        return []
      }
    }
  },
  data () {
    return {
      gridId: 'list',
      activeName: 'running',
      loading: false,
      queryParam: {},
      flowData: [],
      pageInfo: {
        total: 0,
        pageNum: 1,
        pageSize: 15
      },
      tabList: [
        {
          text: this.$t('flowMod.queryTodoCurrent'),
          url: '/api-base/flow/event/queryTodoCurrent'
        }
      ],
      tabValue: 0,
      queryForm: [
        {
          prop: 'title',
          label: this.$t('flowMod.title') // 标题,
        }
      ]
    }
  },
  created () {
    this.getQuerydata(this.queryParam) //  查询未处理数据
  },
  methods: {
    tabClick (url, index) {
      this.tabValue = index
      this.flowchange(url)
    },
    flowchange (url) { // 切换流程类型
      this.fatchListData(url, this.queryParam) // 查询接口
    },
    // 查询列表数据
    fatchListData (url, params) {
      this.loading = true
      getMyTaskProcess(url, params).then(res => {
        if (res.data && res.data.list) {
          this.flowData = res.data.list || []
          this.pageInfo.total = res.data.total
          this.pageInfo.pageNum = res.data.pageNum
          this.pageInfo.pageSize = res.data.pageSize
          this.loading = false
        }
      }).catch(err => {
        this.loading = false
        console.log(err)
      }).finally(() => {
        this.$nextTick(() => this.$refs.flowTable.doLayout())
      })
    },
    handleCurrentChange (num) {
      this.queryParam.pageNum = num
      this.flowchange(this.tabList[this.tabValue].url)
    },
    handleSizeChange (size) {
      this.queryParam.pageSize = size
      this.flowchange(this.tabList[this.tabValue].url)
    },
    getQuerydata (v) {
      this.queryParam = v || {}
      if (!this.queryParam.pageSize) {
        this.queryParam.pageSize = 15
      }
      this.fatchListData(this.tabList[this.tabValue].url, this.queryParam)
    },
    goToForm (row) {
      this.flowGoForm(row)
    },
    // 流程代办跳转
    flowGoForm (row) {
      this.params = row
      // let searchUrl = row.businessType
      let bType = row.businessType
      let formId = row.businessId
      // let formId = '312301112639616'
      let formNo = row.title
      let routeName = ''
      if (!formId) return
      // map 使用说明
      // key 对应的是流程的 业务类型 的值
      // value 对应的是 单据里面用来判断该单据的名称 下面是之前开发设置过的名称 对接工作流的时候要修改对应key值
      let types = {
        'quaBusReview': 'quaOfReview', // 资质审查
        'SERVICEREVIEWFORM': 'nonQuaOfReview', // 非材-资质审查
        'SUPPLIER': 'siteAssessment', // 供应商评审
        'SERVICESUPPLIER': 'nonSiteAssessment', // 非材-供应商评审
        'CONTRACT': 'contractManager', // 合同列表
        'black': 'black', // 黑名单
        'BlackTemporary': 'blacktemporary', // 黑名单临时业务
        'MOULDCREATE': 'mouldCreate', // 模具新建
        'MOULDUPDATE': 'mouldUpdate', // 模具更新
        'MOULDSCRAP': 'mouldScrap', // 模具报废
        'MOULDCHANGE': 'mouldChange', // 模具转移
        'VENDORLEVEL': 'hierarchicalReview', // 供应商等级
        'QUOTAFLOW': 'quotaFlow', // 配额
        'PLANCONFIRM': 'siteReviewPlanConfirm', // 计划落实管理
        'QUASAMPLE': 'sampleConfirmed', // 样品确认
        'MATERIALTRIAL': 'materialTrial', // 物料试用
        'COOPERATIONEND': 'cooperationEnded', // 合作终止
        'ORDER': 'buyerPurchaseOrder', // 采购订单
        'REQUIREMENT': 'purchaseApplication', // 采购申请管理
        'paymentapply': 'purPaymentApply', // 付款申请
        'onlineInvoice': 'agentOnlineInvoice', // 开票管理
        'invoiceNotice': 'purInvoice', // 对账管理
        'ORDERCHANGE': 'purchaseOrderChange', // 采购订单变更
        'questResultApprove': 'questManagement', // 供应商调查表管理
        'supplierGreenChannel': 'vendorGreenChannel', // 绿色通道
        'SOUINQCREATE': 'inquiryCreate', // 简易询价立项审批
        'SOUBRGCREATE': 'bargainCreate', // 寻源项目式询价立项审批
        'SOUBIDCREATE': 'bidingCreate', // 招标管理 - 立项
        'souReqApply': 'sourcingApplicationBuyer', // 寻源需求报名审批
        'SUPPLIERINFOCHANGE': 'vendorInfoChange', // 供应商变更信息
        'FINANCECHANGE': 'financialInforChanges', // 财务信息变更
        'agentOnlineInvoice': 'agentOnlineInvoice', // 代理网上开票-
        'ADVANCEPAYMENT': 'advancePayment', // 预付款申请维护-
        'flowPriceApproval': 'inquiryApprovalFlow', // 价格审批单 - 还没接审批流
        'flowPerfAssessment': 'performanceAssessment', // 供应商绩效考核
        'performAcceptance': 'contractPerformanceCheck', // 合同验收
        'PURLOGCHANGE': 'purchaseDirectoryChange', // 货源变更
        'performPlan': 'contractPerformancePlan' // 履约计划
      }
      const typeSearch = new Map(Object.entries(types))
      if (typeSearch.get(bType)) routeName = typeSearch.get(bType)

      this.$router.push({
        name: 'flowTaskView',
        query: {
          from: 'fromFun',
          funName: routeName, //
          formId: formId, // 业务单据ID
          formNo: formNo // 业务单据No
        }
      })
    }
  }
}
</script>
<style scoped lang="scss">
.tableForm {
  position: absolute;
  top: 0;
  bottom: 0;
  height: 100%;
  width: 100%;
  .el-table {
    height: 100%;
  }
}
.butHeader {
  padding: 10px;
}
.pageFooter {
  .c-pagination {
    // margin: 0 !important;
  }
}
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  .tabs {
    display: flex;
    .btn {
      font-size: 12px;
      padding: 0px 0 4px;
      font-weight: normal;
      height: 24px;
      line-height: 24px;
      cursor: pointer;
      box-sizing: content-box;
      color: #393E45;
      &.active {
        border-bottom: 2px solid  #0077FF;
        color:#0077FF;
        font-weight: bold;
      }
      &+.btn {
        margin-left: 32px;
      }
      &:hover{
        color: #0077FF;
      }
    }
  }
}
</style>
