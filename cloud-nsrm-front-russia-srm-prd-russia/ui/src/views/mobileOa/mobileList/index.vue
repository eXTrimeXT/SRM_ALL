<template>
  <el-container
    class="flex-container the_approvalFlowList_wrapper test-list"
    direction="vertical"
  >
    <el-main>
      <el-header
        height="109"
        style="padding:0 0 0 0;"
      >
        <div class="test-list-wait">
          任务待办
        </div>
        <FormWrapper
          :form-array="queryForm"
          :col-length="1"
          @getFormData="getQuerydata"
        />
      </el-header>
      <el-main style="flex-grow: 1;display: flex;flex-direction: column;">
        <el-table
          ref="mtTable"
          v-loading="loading"
          stripe
          border
          height="100%"
          :data="flowData"
          style="height: 100%"
        >
          <!-- 标题 -->
          <el-table-column
            prop="title"
            :label="$t('flowMod.title')"
            show-overflow-tooltip
          >
            <template slot-scope="scope">
              <el-button
                type="text"
                @click="goToForm(scope.row)"
              >
                {{ scope.row.title }}
              </el-button>
            </template>
          </el-table-column>
          <!-- 业务类型 -->
          <el-table-column
            prop="businessType"
            label="业务类型"
          />
        </el-table>
      </el-main>
      <el-footer
        height="45px"
        class="pageFooter"
      >
        <CPagination
          ref="queryPagination"
          class="c-query-table-pagination"
          layout="prev, pager, next"
          :total="pageInfo.total"
          :page-num="pageInfo.pageNum"
          :page-size="pageInfo.pageSize"
          @current-change="handleCurrentChange"
          @size-change="handleSizeChange"
        />
      </el-footer>
    </el-main>
  </el-container>
</template>
<script>
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import CPagination from 'lib@/components/c-pagination'
import { getMyTaskProcess } from '@/api/workFlow'

export default {
  name: 'MobileList',
  components: {
    CPagination,
    MainHeader,
    FormWrapper
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
        pageSize: 25
      },
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
    switchUrl (val) {
      if (val === 'running') {
        // 待办
        return '/api-base/flow/event/queryTodoCurrent'
      } else if (val === 'worked') {
        // 已办
        return '/api-base/flow/event/queryDone'
      }
    },
    // 切换流程类型
    flowchange (val) {
      let url = this.switchUrl(val)
      this.fatchListData(url, this.queryParam) // 查询接口
    },
    getQuerydata (v) {
      this.queryParam = v || {}
      if (!this.queryParam.pageSize) {
        this.queryParam.pageSize = 25
      }
      let url = this.switchUrl(this.activeName)
      this.fatchListData(url, this.queryParam)
    },
    // 查询列表数据
    fatchListData (url, parame) {
      this.loading = true
      getMyTaskProcess(url, parame).then(res => {
        if (res.data && res.data.list) {
          this.pageInfo.total = res.data.total
          this.pageInfo.pageNum = res.data.pageNum
          this.pageInfo.pageSize = res.data.pageSize
          this.flowData = res.data.list
        }
        this.loading = false
      })
    },
    handleCurrentChange (num) {
      this.queryParam.pageNum = num
      this.getQuerydata(this.queryParam)
    },
    handleSizeChange (size) {
      this.queryParam.pageSize = size
      this.getQuerydata(this.queryParam)
    },
    goToForm (row) {
      this.params = row
      let bType = row.businessType
      let formId = row.businessId
      let formNo = row.title
      let routeName = ''
      if (!formId) return
      // map 使用说明
      // key 对应的是流程的 业务类型 的值
      // value 对应的是 单据里面用来判断该单据的名称 下面是之前开发设置过的名称 对接工作流的时候要修改对应key值
      let types = {
        'quaBusReview': 'quaOfReview', // 资质审查
        'CONTRACT': 'contractManager', // 合同列表
        'black': 'black', // 黑名单
        'BlackTemporary': 'black_temporary', // 黑名单临时业务
        'ORDER': 'buyerPurchaseOrder', // 采购订单
        'REQUIREMENT': 'purchaseApplication', // 采购申请管理
        'MOULDCREATE': 'mouldCreate', // 模具新建
        'MOULDUPDATE': 'mouldUpdate', // 模具更新
        'endProjectApproval': 'biddingProject', // 立项审批
        'initProjectApproval': 'biddingProject', // 结果审批
        'inquiryFlow': 'inquiry', // 询价审批
        'inquiryApprovalFlow': 'inquiryApprovalFlow', // 价格审批单
        'quaOfSampleFlowCheck': 'sampleConfirmed', // 样品确认
        'quaOfMaterialTrial': 'materialTrial', // 物料试用
        'vendorInfoChange': 'vendorInfoChange', // 供应商变更信息
        'endCooperate': 'cooperationEnded', // 合作终止
        'flowPerfAssessment': 'performanceAssessment', // 供应商绩效考核
        'perfScoreItems': 'performanceScoreItems', // 绩效评分项目
        'requireHeader': 'applicationAndAudit', // -- 未知
        'siteAssessment': 'siteAssessment', // 供应商评审
        'crossOrgImport': 'crossOrgImport', // 跨组织引入
        'supplierGreenChannel': 'vendorGreenChannel', // 绿色通道
        'quotaModulation': 'quotaModulation', // 配额调整
        'demandPoolManagement': 'performanceScoreItems', // 绩效评分项目
        'purchaseOrderChange': 'purchaseOrderChange', // 采购订单变更
        'inspectionBill': 'inspectionBill', // 验收单列表
        'advancePayment': 'advancePayment', // 预付款申请维护
        'purPaymentApply': 'purPaymentApply', // 付款申请
        'purInvoice': 'purInvoice', // 开票通知
        'agentOnlineInvoice': 'agentOnlineInvoice', // 代理网上开票
        'shoppingCart': 'shoppingCart', // 购物车
        'biddingProject': 'biddingProject' // 招投标列表
      }
      const typeSearch = new Map(Object.entries(types))
      if (typeSearch.get(bType)) routeName = typeSearch.get(bType)

      this.$router.push({
        name: 'mflowTaskView',
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

.test-list {
  height: 100%;
  :deep(.el-main){
    overflow-x: hidden;
  }
	:deep(.toggleBtn) {
		display: none;
	}
	:deep(.el-form) {
		width: 24rem;
	}
	:deep(.c-pagination) {
		justify-content: center;
	}
  .test-list-wait {
    font-size: 1rem;
    height: 3.5rem;
    border-bottom: 1px solid rgba(0,0,0,.1);
    display: flex;
    justify-content: center;
    align-items: center;
    background: #e0e6e6;
  }
}
</style>
