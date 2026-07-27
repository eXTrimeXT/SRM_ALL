<template>
  <div :class="['task-wrapper',{'xsTabs': device==='device-xs'}]">
    <div class="task-header">
      <el-tabs
        v-model="activeName"
        class="tabs"
        @tab-click="tabClick"
      >
        <el-tab-pane
          v-for="(item) in tabList"
          :key="item.name"
          :label="item.text"
          :name="item.name"
        >
          <span v-if="item.name==='ceeaFindMyRunningProcess'" slot="label">
            <el-badge :value="item.num" :max="99" size="mini" class="item">
              <div>{{ item.text }}</div>
            </el-badge>
          </span>
        </el-tab-pane>
      </el-tabs>
      <div
        class="more"
        @click="moreProcess"
      >
        <span class="info-more">{{ $t("common.more") }}</span>
        <em class="icon-more el-icon-arrow-right" />
      </div>
    </div>
    <div class="content">
      <el-table
        ref="flowTable"
        v-loading="loading"
        :data="flowData"
        border
        stripe
        tooltip-effect="dark"
        :highlight-current-row="true"
        width="100%"
        style="width: 100%"
        class="processTable"
        height="400px"
      >
        <!-- 停留时间 -->
        <el-table-column
          v-if="activeName === 'ceeaFindMyRunningProcess'"
          width="82px"
          prop="residenceTime"
          :label="$t('dashboard.residenceTime')"
          show-overflow-tooltip
        >
          <template slot-scope="scope">
            <span>{{ scope.row.residenceTime }}</span>
          </template>
        </el-table-column>
        <!-- 标题 -->
        <el-table-column
          prop="title"
          :label="$t('dashboard.title')"
          show-overflow-tooltip
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
          width="150px"
          prop="createName"
          :label="$t('dashboard.creator')"
          show-overflow-tooltip
        />
        <!-- 创建时间 -->
        <el-table-column
          width="148px"
          prop="creationDate"
          :label="$t('dashboard.creationDate')"
          show-overflow-tooltip
        />
      </el-table>
      <div class="flowPage">
        <CPagination
          ref="queryPagination"
          class="c-query-table-pagination"
          :total="pageInfo.total"
          :page-num="pageInfo.pageNum"
          :page-size="pageInfo.pageSize"
          @current-change="handleCurrentChange"
          @size-change="handleSizeChange"
        />
      </div>
    </div>
  </div>
</template>
<script>
import { mapState } from 'vuex'
import { getMyTaskProcess } from '@/api/workFlow'
import CPagination from 'lib@/components/c-pagination'
export default {
  name: 'BuyerProcessTaskNew',
  components: {
    CPagination
  },
  data () {
    return {
      tabList: [
        {
          text: this.$t('dashboard.todoTask'), // 待办任务
          url: '/api-base/workbench/task/ceeaFindMyRunningProcess',
          name: 'ceeaFindMyRunningProcess',
          num: 0
        },
        {
          text: this.$t('dashboard.hasTodoTasks'), // 已办任务
          url: '/api-base/workbench/task/ceeaFindMyWorkedProcess',
          name: 'ceeaFindMyWorkedProcess',
          num: 0
        }
        // {
        //   text: this.$t('dashboard.iStart'), // 已办
        //   url: '/api-base/workbench/task/ceeaFindMyStartProcess',
        //   name: 'ceeaFindMyStartProcess',
        //   num: 0
        // },
        // {
        //   text: this.$t('dashboard.peddingApprove'),
        //   url: '/api-base/flow/event/queryTodoCurrent',
        //   name: 'queryTodoCurrent',
        //   num: 0
        // }
      ],
      activeName: 'ceeaFindMyRunningProcess',
      flowData: [],
      queryParam: {
        pageNum: 1,
        pageSize: 15
      },
      pageInfo: {
        total: 0,
        pageNum: 1,
        pageSize: 15
      },
      loading: false
    }
  },
  computed: {
    ...mapState({
      device: state => state.app.device
    }),
    currentUrl () {
      let curRow = this.tabList.find(i => (i.name === this.activeName))
      if (curRow) {
        return curRow.url
      } else {
        return this.tabList[0].url
      }
    }
  },
  created () {
    this.flowchange(this.currentUrl) // 查询默认列表
    this.tabList.forEach((e, index) => {
      this.fatchListDataBegin(e.url, this.queryParam, index)
    })
  },
  methods: {
    tabClick (tabName) {
      this.queryParam.pageNum = 1
      this.flowchange(this.currentUrl)
    },
    // 切换流程类型
    flowchange (url) { // 测试
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
    // 初始查询列表数据
    fatchListDataBegin (url, params, nums) {
      this.loading = true
      getMyTaskProcess(url, params).then(res => {
        if (res.data && res.data.list) {
          this.tabList[nums].num = res.data.total
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
      this.flowchange(this.currentUrl)
    },
    handleSizeChange (size) {
      this.queryParam.pageSize = size
      this.flowchange(this.currentUrl)
    },
    moreProcess () {
      this.$router.push({ name: 'approvalFlowCenter' })
    },
    goToForm (row) {
      if (this.activeName === 'queryTodoCurrent') {
        this.flowGoForm(row)
      } else {
        this.taskGoForm(row)
      }
    },
    // 代办任务跳转
    async taskGoForm (row) {
      if (row.moduleName === 'lockSeal') {
        const {
          stampContractFileuploadId,
          partnerName,
          extEmployeeNumber
        } = row
        const { data } = await this.$http({
          url: `/api-pj/external/ContractLock/signUrl2?contractId=${stampContractFileuploadId}&tenantName=${partnerName}&tenantType=COMPANY&receiverNumber=${extEmployeeNumber}`,
          method: 'POST'
        })
        window.open(data, '_blank')
        return false
      }
      let searchUrl = row.searchUrl
      let formId = row.formId
      let formNo = row.formNo
      let routeName = ''
      if (!formId) return
      let urlArr = searchUrl.split('/')
      let keyName = urlArr[urlArr.length - 1]
      // key 对应的是url最后一层
      // value 对应的是 单据里面用来判断该单据的名称 下面是之前开发设置过的名称 对接工作流的时候要修改对应key值
      let types = {
        [keyName]: keyName,
        'quaOfReview': 'quaOfReview', // 资质审查
        'endProjectApproval': 'biddingProject', // 立项审批
        'initProjectApproval': 'biddingProject', // 结果审批
        'vendorBiddingList_new': 'inquiryBySimpleListBuyer', // 询价审批
        'inquiryApprovalFlow': 'priceApproval', // 价格审批单
        'quaOfSampleFlowCheck': 'sampleConfirmed', // 样品确认
        'quaOfMaterialTrial': 'materialTrial', // 物料试用
        'vendorInfoChange': 'vendorInfoChange', // 供应商变更信息
        'endCooperate': 'cooperationEnded', // 合作终止
        'vendorAssesForm': 'performanceAssessment', // 供应商绩效考核
        'perfScoreItems': 'performanceScoreItems', // 绩效评分项目
        'requireHeader': 'applicationAndAudit', // -- 未知
        'crossOrgImport': 'crossOrgImport', // 跨组织引入
        'vendorGreenChannel': 'vendorGreenChannel', // 绿色通道
        'quotaModulation': 'quotaModulation', // 配额调整
        'performanceScoreItems': 'performanceScoreItems', // 绩效评分项目
        'purchaseOrderChange': 'purchaseOrderChange', // 采购订单变更
        'inspectionBill': 'inspectionBill', // 验收单列表
        'advancePayment': 'advancePayment', // 预付款申请维护
        'purPaymentApply': 'purPaymentApply', // 付款申请
        'purInvoice': 'purInvoice', // 开票通知
        'agentOnlineInvoice': 'agentOnlineInvoice', // 代理网上开票
        'shoppingCart': 'shoppingCart', // 购物车
        'biddingProject': 'biddingProject', // 招投标列表
        'siteReviewPlanConfirm': 'siteReviewPlanConfirm', // 计划落实
        'buyerPurchaseOrder': 'buyerPurchaseOrder', // 采购订单
        'contractMaintainList': 'contractMaintainList', // 合同列表
        'inquiryBySimpleListBuyer': 'inquiryBySimpleListBuyer', // 询价管理
        'siteAssessment': 'siteAssessment', // 供应商认证
        'nonSiteAssessment': 'nonSiteAssessment',
        'potentialSupplier': 'potentialSupplier'
      }
      const typeSearch = new Map(Object.entries(types))
      if (typeSearch.get(keyName)) routeName = typeSearch.get(keyName)
      this.$router.push({
        name: routeName,
        params: {
          from: 'fromFun',
          funName: routeName,
          formId: formId, // 业务单据ID
          formNo: formNo, // 业务单据No
          row,
          // 1-待办 2-已办 3-我启动
          taskIndex: ['ceeaFindMyRunningProcess', 'ceeaFindMyWorkedProcess', 'ceeaFindMyStartProcess'].indexOf(this.activeName) + 1
        }
      })
    },
    // 流程代办跳转
    flowGoForm (row) {
      // this.params = row
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
        'performPlan': 'contractPerformancePlan', // 履约计划
        'osMaterialChange': 'outsourceMaterialChange' // 委外用料清单变更
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
<style lang="scss" scoped>
.task-wrapper {
  padding:0 16px 16px;
  border-radius: 4px;
  background: #fff;
}
.content {
  margin-top: 16px;
}
.mt-10 {
  margin-top: 10px;
}
.c-query-table-pagination {
  padding-bottom: 0 !important;
}
</style>
<style lang="scss">
.task-wrapper{
  .task-header {
    position: relative;
    padding-right:55px ;
    .tabs {
      &.el-tabs{
        .el-tabs__header{
          margin: 0 !important;
          .el-tabs__nav-wrap{
            &::after{
              display: none !important;
            }
            .el-tabs__nav-next, .el-tabs__nav-prev{
              line-height: 56px;
              font-size: 16px;
            }
          }
          .el-tabs__item{
            height: 46px !important;
            line-height: 25px !important;
            font-size: 16px;
            padding: 16px 32px 0 0 !important;
            &.is-active{
              font-weight: bold;
            }
          }
        }
        .el-tabs__content{
          display: none;
        }
      }
    }
    .more {
      position: absolute;
      right: 0px;
      top: 18px;
      cursor: pointer;
      color: #96999C;
      font-size: 12px;
      line-height: 20px;
      .icon-more {
        margin-left: 2px;
      }
    }
  }
  // 小屏幕
  &.xsTabs {
    .task-header{
      .tabs{
        &.el-tabs{
          .el-tabs__header{
            margin: 0 !important;
            .el-tabs__nav-wrap{
              .el-tabs__nav-next, .el-tabs__nav-prev{
                line-height: 46px;
                font-size: 14px;
              }
            }
            .el-tabs__item{
              height: 46px !important;
              line-height: 16px !important;
              font-size: 14px;
              padding: 0 12px;
            }
          }
        }
      }
      .more{
        top: 12px;
      }
    }
  }
  .el-badge__content{
    line-height: 16px !important;
  }
}

</style>
