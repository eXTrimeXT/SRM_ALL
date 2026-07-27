<template>
  <!-- 采购商流程审批 -->
  <div class="grid-content">
    <h3 class="grid-title">
      <!-- 我的任务  -->
      {{ $t("dashboard.myTask") }}
      <span
        class="info-more"
        @click="moreProcess"
      >
        <!-- 更多> -->
        {{ $t("common.more") + ">" }}
      </span>
    </h3>
    <div class="the_flowList">
      <div class="flowType">
        <el-radio-group
          v-model="activeName"
          @change="flowchange"
        >
          <el-radio-button label="running">
            <!-- 待办 -->
            {{ $t("dashboard.upComing") }}
          </el-radio-button>
          <el-radio-button label="worked">
            <!-- 已办 -->
            {{ $t("dashboard.done") }}
          </el-radio-button>
          <el-radio-button label="startProcess">
            <!-- 我启动 -->
            {{ $t("dashboard.iStart") }}
          </el-radio-button>
          <el-radio-button label="sendNodes">
            <!-- 抄送我 -->
            {{ $t("dashboard.sendMe") }}
          </el-radio-button>
        </el-radio-group>
      </div>
      <el-table
        ref="flowTable"
        :data="flowData"
        border
        stripe
        tooltip-effect="dark"
        :highlight-current-row="true"
        style="width: 100%"
        class="processTable"
        height="290"
      >
        <!-- 停留时间 -->
        <el-table-column
          v-if="activeName === 'running'"
          width="100px"
          prop="stayTime"
          :label="$t('dashboard.residenceTime')"
          show-overflow-tooltip
        >
          <template slot-scope="scope">
            <span style="color:#ff4949">{{ $parseTime(scope.row.stayTime) }}</span>
          </template>
        </el-table-column>
        <!-- 标题 -->
        <el-table-column
          prop="fdSubject"
          :label="$t('dashboard.title')"
          show-overflow-tooltip
        >
          <template slot-scope="scope">
            <!-- 'approval' -->
            <!-- <router-link
              :to="{
                name: scope.row.fdFormTemplateId,
                params: {
                  from:'fromFun',
                  fdId: scope.row.fdId,
                  fdFormInstanceId: scope.row.fdFormInstanceId // 业务单据ID
                }
              }"
            >
              <span style="cursor: pointer;">{{scope.row.fdSubject}}</span>
            </router-link> -->
            <div
              style="cursor: pointer;"
              @click="goToForm(scope.row)"
            >
              {{ scope.row.fdSubject }}
            </div>
          </template>
        </el-table-column>
        <!-- 创建人 -->
        <el-table-column
          width="120px"
          prop="docCreatorName"
          :label="$t('dashboard.creator')"
          show-overflow-tooltip
        />
        <!-- 创建时间 -->
        <el-table-column
          width="100px"
          prop="docCreateTime"
          :label="$t('dashboard.creationDate')"
          show-overflow-tooltip
          :formatter="(row, column, cellValue) => $parseTime(cellValue)"
        />
      </el-table>
      <div class="flowPage">
        <c-pagination
          ref="queryPagination"
          class="c-query-table-pagination"
          layout="prev,pager,next"
          :total="pageInfo.total"
          :page-num="pageInfo.page"
          :page-size="pageInfo.pageSize"
          @current-change="handleCurrentChange"
          @size-change="handleSizeChange"
        />
      </div>
    </div>
  </div>
</template>
<script>
import CPagination from 'lib@/components/c-pagination'
import { getMyProcess } from '@/api/workFlow'
export default {
  name: 'BuyerFlowApprove',
  components: { CPagination },
  data () {
    return {
      myProcess: [], // 代办事项
      activeName: 'running',
      queryParam: {
        page: 1,
        pageSize: 10
      },
      flowData: [],
      pageInfo: {
        total: 0,
        page: 1,
        pageSize: 10
      }
    }
  },
  created () {
    this.getflowData() // 查询流程代办
  },
  methods: {
    // 查询代办事项
    fatchMyProcess () {
      let url = '/api-base/workbench/process/findMyRunningProcess'
      let parames = {
        page: 1,
        pageSize: 10
      }
      getMyProcess(url, parames).then(res => {
        if (res) {
          this.myProcess = res.data.list
        }
      })
    },
    // processRowClick 行点击
    processRowClick (row, column, event) {
      let fdId = row.fdId //  流程ID
      let fdFormTemplateId = row.fdFormTemplateId // 流程类型
      this.$router.push({
        name: 'approval',
        params: { fdId: fdId, fdFormTemplateId: fdFormTemplateId }
      })
    },
    goToForm (row) {
      let fdFormTemplateId = row.fdFormTemplateId
      let fdFormInstanceId = row.fdFormInstanceId || row.fdFromInstanceId
      let fdId = row.fdId
      let fdSubject = row.fdSubject
      let routeName = ''
      if (!fdFormTemplateId) return
      if (fdFormTemplateId === 'quaOfReview') {
        // 资质审查
        routeName = 'quaOfReview'
      } else if (
        fdFormTemplateId === 'endProjectApproval' ||
        fdFormTemplateId === 'initProjectApproval'
      ) {
        // 立项审批 |结果审批
        routeName = 'biddingProject'
      } else if (fdFormTemplateId === 'inquiryFlow') {
        // 询价审批
        routeName = 'inquiry'
      } else if (fdFormTemplateId === 'inquiryApprovalFlow') {
        // 价格管理
        routeName = 'priceApproval'
      } else if (fdFormTemplateId === 'quaOfSampleFlowCheck') {
        // 样品确认
        routeName = 'sampleConfirmed'
      } else if (fdFormTemplateId === 'quaOfMaterialTrial') {
        routeName = 'materialTrial'
      } else if (fdFormTemplateId === 'changeSupInfo') {
        routeName = 'vendorInfoChange'
      } else if (fdFormTemplateId === 'endCooperate') {
        routeName = 'cooperationEnded'
      } else if (fdFormTemplateId === 'requireOrder') {
        routeName = 'buyerPurchaseOrder'
      } else if (fdFormTemplateId === 'flowPerfAssessment') {
        routeName = 'performanceAssessment'
      } else if (fdFormTemplateId === 'perfScoreItems') {
        routeName = 'performanceScoreItems'
      } else if (fdFormTemplateId === 'requireHeader') {
        routeName = 'applicationAndAudit'
      }
      this.$router.push({
        name: routeName,
        params: {
          from: 'fromFun',
          fdId: fdId,
          fdFormInstanceId: fdFormInstanceId, // 业务单据ID
          funName: routeName, // 功能
          fdSubject: fdSubject
        }
      })
    },
    moreProcess () {
      this.$router.push({ name: 'approvalFlowCenter' })
    },
    // 查询代办
    getflowData () {
      let url = this.switchUrl(this.activeName)
      this.fatchListData(url, this.queryParam)
    },
    // 切换流程类型
    switchUrl (val) {
      if (val === 'running') {
        return '/api-base/workbench/process/findMyRunningProcess'
      } else if (val === 'worked') {
        return '/api-base/workbench/process/findMyWorkedProcess'
      } else if (val === 'startProcess') {
        return '/api-base/workbench/process/findMyStartProcess'
      } else if (val === 'sendNodes') {
        return '/api-base/workbench/process/findSendNodesToMe'
      }
    },
    // 切换流程类型
    flowchange (val) {
      let url = this.switchUrl(val)
      this.fatchListData(url, this.queryParam) // 查询接口
      this.$nextTick(() => {
        this.$refs.flowTable.doLayout()
      })
    },
    // 查询列表数据
    fatchListData (url, parame) {
      this.loading = true
      getMyProcess(url, parame).then(res => {
        if (res.data && res.data.list) {
          this.loading = false
          this.pageInfo.total = res.data.rowCount
          this.pageInfo.page = res.data.page
          this.pageInfo.pageSize = res.data.pageSize
          this.flowData = res.data.list
        }
      })
    },
    handleCurrentChange (num) {
      this.queryParam.page = num
      this.getflowData()
    },
    handleSizeChange (size) {
      this.queryParam.pageSize = size
      this.getflowData()
    }
  }
}
</script>
<style lang="scss" scoped>
.grid-content {
  // height: 512px;
  .the_flowList {
    height: 100%;
    overflow: hidden;
    position: relative;
    padding: 45px 0 38px;
  }
  .el-tabs--border-card {
    height: 100%;
    background: #fff;
    border: 1px solid #e8e8e8;
    box-shadow: 0 1px 1px 0 rgba(222, 222, 222, 0.3),
      0 0 2px 0 rgba(236, 237, 238, 0.5);
  }
}
</style>
<style>
.flowType {
  border-bottom: 1px solid #d9d9d9 !important;
  position: absolute;
  width: 100%;
  height: 37px;
  top: 0;
}
.flowType .el-radio-group {
  position: relative;
  bottom: -1px;
}
.flowType .el-radio-group .el-radio-button {
  margin-right: 5px;
}
.flowType .el-radio-button__inner {
  border-radius: 4px 4px 0 0px !important;
  background: #fafafa;
  height: 36px;
  line-height: 34px;
  padding: 0 15px;
  border: 1px solid #d9d9d9 !important;
  border-left: 1px solid #d9d9d9 !important;
  box-shadow: none;
}
.flowType .el-radio-button__orig-radio:checked + .el-radio-button__inner {
  box-shadow: none !important;
}
.flowType .el-radio-button__orig-radio:checked + .el-radio-button__inner {
  color: #409eff;
  background-color: #fff;
  border-bottom-color: #fff !important;
}
.flowType .el-radio-button:first-child .el-radio-button__inner {
  border-left: 1px solid #d9d9d9;
  border-radius: 4px 4px 0 0px;
  box-shadow: none !important;
}
.flowType .el-radio-button:last-child .el-radio-button__inner {
  border-left: 1px solid #d9d9d9;
  border-radius: 4px 4px 0 0px;
  box-shadow: none !important;
}
.flowPage {
  padding: 5px;
  position: absolute;
  width: 100%;
  left: 0;
  bottom: 0;
}
.flowPage .c-pagination {
  margin: 0 !important;
}
.processTable.el-table--border {
  border: 1px solid #ededed;
  border-bottom: 0;
  border-right: 0;
}
.processTable.el-table th {
  background-color: #fafafa !important;
  border-right: 0 !important;
}
.processTable.el-table--border td {
  border-right: 0 !important;
  border-bottom: 0 !important;
}
</style>
