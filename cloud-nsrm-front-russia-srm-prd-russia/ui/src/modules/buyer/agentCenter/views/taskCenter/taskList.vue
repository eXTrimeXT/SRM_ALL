<template>
  <el-container
    class="flex-container the_approvalFlowList_wrapper"
    direction="vertical"
  >
    <el-main>
      <form-wrapper
        :form-array="queryForm"
        :col-length="1"
        @getFormData="getQuerydata"
      />
      <main-header
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
      </main-header>
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
              <el-table-column
                v-if="tabValue === 0"
                width="122px"
                prop="residenceTime"
                :label="$t('dashboard.residenceTime')"
                show-overflow-tooltip
                sortable
              >
                <template slot-scope="scope">
                  <!-- style="color: #ff4949" -->
                  <span>{{ scope.row.residenceTime }}</span>
                </template>
              </el-table-column>
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
              />
            </el-table>
          </div>
        </el-main>
        <el-footer
          class="pageFooter"
        >
          <c-pagination
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
  name: 'TaskList',
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
      // 注意：下面两个接口是用于采购商的，供应商的接口还没有，需要重新开发
      tabList: [
        {
          text: this.$t('dashboard.todoTask'),
          url: '/api-base/workbench/task/ceeaFindMyRunningProcess'
        },
        {
          text: this.$t('dashboard.hasTodoTasks'),
          url: '/api-base/workbench/task/ceeaFindMyWorkedProcess'
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
      this.taskGoForm(row)
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
