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
              <el-table-column
                v-if="activeName === 'ceeaFindMyRunningProcess'"
                width="122px"
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
                    style="cursor: pointer;color:#0077FF;"
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
              />
              <!-- 创建时间 -->
              <el-table-column
                prop="creationDate"
                :label="$t('dashboard.creationDate')"
                show-overflow-tooltip
              />
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
          url: '/api-base/vendor/workbench/task/findMyRunningProcess',
          name: 'ceeaFindMyRunningProcess',
          num: 0
        },
        {
          text: this.$t('dashboard.hasTodoTasks'), // 已办任务
          url: '/api-base/vendor/workbench/task/findMyWorkedProcess',
          name: 'ceeaFindMyWorkedProcess',
          num: 0
        },
        {
          text: this.$t('dashboard.iStart'), // 我启动的
          url: '/api-base/vendor/workbench/task/findMyStartProcess',
          name: 'ceeaFindMyStartProcess',
          num: 0
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
        'vendorBiddingList_new': 'inquiryBySimpleListVendor', // 询比价
        'supOnlineInvoice': 'purInvoiceSupplier', // 开票协同
        'purInvoice': 'purInvoiceSupplier', // 开票通知
        'deliveryOrder': 'vendorDeliveryOrder', // 送货单
        'vendorBidding': 'vendorBiddingList', // 招标
        'performanceAssessment': 'performanceAssessment' // 绩效考核
      }
      const typeSearch = new Map(Object.entries(types))
      if (typeSearch.get(keyName)) routeName = typeSearch.get(keyName)
      console.log(routeName, 111)
      this.$router.push({
        name: routeName,
        params: {
          from: 'fromFun',
          funName: routeName,
          formId: formId, // 业务单据ID
          formNo: formNo, // 业务单据No
          taskIndex: ['ceeaFindMyRunningProcess', 'ceeaFindMyWorkedProcess', 'ceeaFindMyStartProcess'].indexOf(this.activeName) + 1
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
