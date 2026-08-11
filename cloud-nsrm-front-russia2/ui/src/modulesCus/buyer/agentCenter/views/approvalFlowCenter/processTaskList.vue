<template>
  <el-container
    class="flex-container the_approvalFlowList_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="queryForm"
        @getFormData="getQuerydata"
      />
      <el-tabs
        v-model="activeName"
        @tab-click="tabClick"
      >
        <el-tab-pane
          v-for="(item) in tabList"
          :key="item.name"
          :label="item.text"
          :name="item.name"
        />
      </el-tabs>
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
              <!-- 停留时间 -->
              <!-- <el-table-column
                v-if="activeName === 'ceeaFindMyRunningProcess'"
                width="122px"
                prop="residenceTime"
                :label="$t('dashboard.residenceTime')"
                show-overflow-tooltip
              >
                <template slot-scope="scope">
                  <span>{{ scope.row.residenceTime }}</span>
                </template>
              </el-table-column> -->
              <!-- 标题 -->
              <el-table-column
                prop="title"
                :label="$t('dashboard.title')"
                show-overflow-tooltip
              >
                <template slot-scope="scope">
                  <div
                    style="cursor: pointer;color:#0077FF;"
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
                width="150px"
                v-if="activeName === 'ceeaFindMyRunningProcess'"
                prop="creationDate"
                :label="$t('dashboard.creationDate')"
                show-overflow-tooltip
              >
                <template slot-scope="scope">
                  {{$parseTime(scope.row.creationDate)}}
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-main>
        <CPagination
          ref="queryPagination"
          class="c-query-table-pagination"
          style="padding-bottom:4px"
          :total="pageInfo.total"
          :page-num="pageInfo.pageNum"
          :page-size="pageInfo.pageSize"
          @current-change="handleCurrentChange"
          @size-change="handleSizeChange"
        />
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
      activeName: 'ceeaFindMyRunningProcess',
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
        //   text: this.$t('dashboard.peddingApprove'), // 待审批
        //   url: '/api-base/flow/event/queryTodoCurrent',
        //   name: 'queryTodoCurrent',
        //   num: 0
        // },
        // {
        //   text: this.$t('flowMod.queryDone'), // 已审批
        //   url: '/api-base/flow/event/queryDone',
        //   name: 'approved',
        //   num: 0
        // }
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
  computed: {
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
    this.getQuerydata(this.queryParam) //  查询未处理数据
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
    handleCurrentChange (num) {
      this.queryParam.pageNum = num
      this.flowchange(this.currentUrl)
    },
    handleSizeChange (size) {
      this.queryParam.pageSize = size
      this.flowchange(this.currentUrl)
    },
    getQuerydata (v) {
      let query = v || {}
      this.queryParam.pageSize = 15
      this.queryParam.pageNum = 1
      this.fatchListData(this.currentUrl, { ...this.queryParam, ...query })
    },
    goToForm (row) {
      if (this.activeName === 'queryTodoCurrent') {
        this.flowGoForm(row)
      } else {
        this.taskGoForm(row)
      }
    },
    // 代办任务跳转
    taskGoForm (row) {
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
          // 1-待办 2-已办 3-我启动 4-已审批
          taskIndex: ['ceeaFindMyRunningProcess', 'ceeaFindMyWorkedProcess', 'ceeaFindMyStartProcess', 'approved'].indexOf(this.activeName) + 1
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
<style lang="scss">
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
</style>
