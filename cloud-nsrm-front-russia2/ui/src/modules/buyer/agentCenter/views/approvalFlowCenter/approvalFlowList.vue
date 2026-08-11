<template>
  <el-container
    class="flex-container the_approvalFlowList_wrapper"
    direction="vertical"
  >
    <el-main>
      <main-header
        :l-span="24"
        :r-span="0"
        style="height:58px"
      >
        <template slot="left">
          <div class="flowType">
            <el-radio-group
              v-model="activeName"
              @change="flowchange"
            >
              <!-- 待处理 -->
              <el-radio-button label="running">
                {{
                  $t("flowMod.running")
                }}
              </el-radio-button>
              <!-- 已处理 -->
              <el-radio-button label="worked">
                {{
                  $t("flowMod.worked")
                }}
              </el-radio-button>
              <!-- 我启动 -->
              <el-radio-button label="startProcess">
                {{
                  $t("flowMod.startProcess")
                }}
              </el-radio-button>
              <!-- 抄送我 -->
              <el-radio-button label="sendNodes">
                {{
                  $t("flowMod.sendNodes")
                }}
              </el-radio-button>
            </el-radio-group>
          </div>
        </template>
      </main-header>
      <el-container direction="vertical">
        <el-main
          style="flex-grow: 1;display: flex;flex-direction: column;padding:0 10px !important;"
        >
          <el-table
            ref="mtTable"
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
              v-if="activeName === 'running'"
              width="100px"
              prop="stayTime"
              :label="$t('flowMod.stayTime')"
            >
              <template slot-scope="scope">
                <span style="color:#ff4949">{{ $parseTime(scope.row.stayTime) }}</span>
              </template>
            </el-table-column>
            <!-- 标题 -->
            <el-table-column
              prop="fdSubject"
              :label="$t('flowMod.title')"
              show-overflow-tooltip
            >
              <template slot-scope="scope">
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
              width="150px"
              prop="docCreatorName"
              :label="$t('common.creator')"
            />
            <!-- 创建时间 -->
            <el-table-column
              width="150px"
              prop="docCreateTime"
              :formatter="(row, column, cellValue) => $parseTime(cellValue)"
              :label="$t('common.creationTime')"
            />
          </el-table>
        </el-main>
        <el-footer
          height="45px"
          class="pageFooter"
        >
          <c-pagination
            ref="queryPagination"
            class="c-query-table-pagination"
            :total="pageInfo.total"
            :page-num="pageInfo.page"
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

import { getMyProcess } from '@/api/workFlow'

export default {
  name: 'MaterialMaintenance',
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
      queryParam: {
        page: 1,
        pageSize: 15
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
    // this.fatchDictData()
    this.getQuerydata() //  查询未处理数据
  },
  methods: {
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
    },
    getQuerydata () {
      let url = this.switchUrl(this.activeName)
      this.fatchListData(url, this.queryParam)
    },
    // 查询列表数据
    fatchListData (url, parame) {
      this.loading = true
      getMyProcess(url, parame).then(res => {
        if (res.data && res.data.list) {
          this.pageInfo.total = res.data.rowCount
          this.pageInfo.page = res.data.page
          this.pageInfo.pageSize = res.data.pageSize
          this.flowData = res.data.list
        }
        this.loading = false
      })
    },
    handleCurrentChange (num) {
      this.queryParam.page = num
      this.getQuerydata()
    },
    handleSizeChange (size) {
      this.queryParam.pageSize = size
      this.getQuerydata()
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
      } else if (fdFormTemplateId === 'quaOfSampleCheck') {
        // 样品确认
        routeName = 'sampleConfirmed'
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
  padding: 10px;
  .c-pagination {
    margin: 0 !important;
  }
}
</style>
<style>
.flowType {
  border-bottom: 1px solid #d9d9d9 !important;
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
</style>
