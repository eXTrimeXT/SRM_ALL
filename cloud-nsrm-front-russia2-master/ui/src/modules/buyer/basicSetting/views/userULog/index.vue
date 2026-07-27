<template>
  <el-container
    class="flex-container-notab the_userLog_wrapper"
    direction="vertical"
  >
    <el-main>
      <form-wrapper
        :form-array="queryForm"
        :pre-form-obj="preFormObj"
        @synchronous-value="syncFilterParams"
        @getFormData="getQuerydata"
      />
      <main-header
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <!-- 查看聚合信息 -->
          <el-button
            type="primary"

            @click="openLogDialog"
          >
            {{
              $t('dataConfMod.viewAggregateInfo')
            }}
          </el-button>
        </template>
      </main-header>
      <table-view
        :ref="gridId"
        :table-data="tableList"
        :table-header="tableHeader"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :show-filter-bar="showFilterBar === 1"
        :open-custom-table="true"
        :url="tableUrl"
      />
    </el-main>
    <!-- 查看 -->
    <el-dialog
      :title="$t('common.view')"
      :visible.sync="viewVisible"
      :close-on-click-modal="false"
      width="800px"
    >
      <vue-json-editor
        v-if="viewVisible"
        v-model="json"
        :show-btns="false"
        :lang="'zh'"
        :mode="mode"
      />
      <div
        slot="footer"
        class="dialog-footer"
      >
        <el-button
          type="primary"
          @click="viewVisible = false"
        >
          <!-- 确 定 -->
          {{ $t('common.confirm') }}
        </el-button>
      </div>
    </el-dialog>
    <!-- 查看日志记录 -->
    <el-dialog
      :title="$t('dataConfMod.vieLogRecord')"
      :visible.sync="viewlogInfoVisible"
      :close-on-click-modal="false"
      width="800px"
    >
      <div
        class="the_loginfo"
        v-html="globalLogInfo"
      />
    </el-dialog>
    <!-- 查看聚合信息 -->
    <el-dialog
      :title="$t('dataConfMod.viewAggregateInfo')"
      :visible.sync="viewGlobalVisible"
      :close-on-click-modal="false"
      width="1000px"
    >
      <div class="the_global_info">
        <el-row type="flex">
          <!-- 用户响应维度 -->
          <el-col>
            <span>{{ $t('dataConfMod.userResponseDimension') }}</span>
          </el-col>
          <el-col>
            <!-- 指标维度 -->
            <span>{{ $t('perfMod.indicatorDimension') }}</span>
            <el-select v-model="requirementHead.indicate">
              <el-option
                v-for="item in indicateList"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-col>
          <el-col>
            <!-- 时间间隔 -->
            <span>{{ $t('dataConfMod.timeInterval') }}</span>
            <el-select
              v-model="requirementHead.interval"
              @change="reRenderCharts2"
            >
              <el-option
                v-for="item in timeList"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-col>
        </el-row>
        <logLineChart :chart-data="chartData" />
      </div>
    </el-dialog>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import vueJsonEditor from 'vue-json-editor'
import logLineChart from 'modb@/basicSetting/views/userULog/logLineChart'

export default {
  name: 'UserULog',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    vueJsonEditor,
    logLineChart
  },
  data () {
    return {
      pageSize: 15,
      gridId: 'list',
      showFilterBar: 1,
      queryParam: {},
      globalLogInfo: null,
      dialogFormVisible: false,
      viewGlobalVisible: false,
      viewlogInfoVisible: false,
      viewVisible: false,
      mode: 'tree',
      tableName: 'ULog',
      queryForm: [], // 查询条件
      tableHeader: [], // 表格列数据
      tableList: [],
      column1Arr: [],
      globalObj: {},
      tableUrl: '/api-base/logsearch/search',
      defalutTableHeader: [],
      indicateList: [
        { label: this.$t('dataConfMod.indicateList[0]'), value: 'userCount' }, // 操作用户数
        { label: this.$t('dataConfMod.indicateList[1]'), value: 'avg' }, // 平均响应时间(ms)
        { label: this.$t('dataConfMod.indicateList[2]'), value: 'max' }, // 最长响应时间(ms)
        { label: this.$t('dataConfMod.indicateList[3]'), value: 'min' }, // 最短响应时间(ms)
        { label: this.$t('dataConfMod.indicateList[4]'), value: 'error' }, // 失败率(%)
        { label: this.$t('dataConfMod.indicateList[5]'), value: 'success' }, // 成功率(%)
        { label: this.$t('dataConfMod.indicateList[6]'), value: 'total' }, // 请求总数
        { label: 'p99', value: '99.0' },
        { label: 'p95', value: '95.0' },
        { label: 'p75', value: '75.0' },
        { label: 'p50', value: '50.0' },
        { label: 'p25', value: '25.0' },
        { label: 'p5', value: '5.0' },
        { label: 'p1', value: '1.0' }
      ],
      timeList: [
        {
          label: this.$t('dataConfMod.timeIntervalList[0]'),
          value: 'm15',
          miSeconds: 15 * 60000
        }, // 15分钟
        {
          label: this.$t('dataConfMod.timeIntervalList[1]'),
          value: 'm30',
          miSeconds: 30 * 60000
        }, // 30分钟
        {
          label: this.$t('dataConfMod.timeIntervalList[2]'),
          value: 'h1',
          miSeconds: 60 * 60000
        }, // 1小时
        {
          label: this.$t('dataConfMod.timeIntervalList[3]'),
          value: 'h3',
          miSeconds: 3 * 60 * 60000
        }, // 3小时
        {
          label: this.$t('dataConfMod.timeIntervalList[4]'),
          value: 'h12',
          miSeconds: 12 * 60 * 60000
        }, // 12小时
        {
          label: this.$t('dataConfMod.timeIntervalList[5]'),
          value: 'day1',
          miSeconds: 24 * 60 * 60000
        }, // 1天
        {
          label: this.$t('dataConfMod.timeIntervalList[6]'),
          value: 'week1',
          miSeconds: 7 * 24 * 60 * 60000
        }, // 1周
        {
          label: this.$t('dataConfMod.timeIntervalList[7]'),
          value: 'month1',
          miSeconds: 30 * 24 * 60 * 60000
        } // 1月
      ],
      requirementHead: {
        indicate: 'total',
        interval: 'day1'
      },
      gloMiSeconds: 24 * 60 * 60000, // 初始化默认是[一天]
      json: null,
      // chartData:{},
      preFormObj: {}
    }
  },
  provide () {
    return { context: this }
  },
  computed: {
    chartData () {
      const json = {
        xAxisData: [],
        seriesData: [],
        vendorInfos: [],
        legend: (this.indicateList.find((v) => v.value == this.requirementHead.indicate) || {})
          .label
      }
      this.column1Arr.forEach((val) => {
        json.xAxisData.push(val.name)
        switch (this.requirementHead.indicate) {
          case 'total':
            json.seriesData.push(val.total)
            break
          case 'userCount':
            json.seriesData.push(val.userCount)
            break
          case 'avg':
            json.seriesData.push(val.dateStats.avg)
            break
          case 'max':
            json.seriesData.push(val.dateStats.max)
            break
          case 'min':
            json.seriesData.push(val.dateStats.min)
            break
          case 'success':
            json.seriesData.push(val.resultStatus.success)
            break
          case 'error':
            json.seriesData.push(val.resultStatus.error)
            break
          case '99.0':
            json.seriesData.push(val.datePercentiles['99.0'])
            break
          case '95.0':
            json.seriesData.push(val.datePercentiles['95.0'])
            break
          case '75.0':
            json.seriesData.push(val.datePercentiles['75.0'])
            break
          case '50.0':
            json.seriesData.push(val.datePercentiles['50.0'])
            break
          case '25.0':
            json.seriesData.push(val.datePercentiles['25.0'])
            break
          case '5.0':
            json.seriesData.push(val.datePercentiles['5.0'])
            break
          case '1.0':
            json.seriesData.push(val.datePercentiles['1.0'])
            break
          default:
            json.seriesData.push(val.userCount)
        }
      })
      return json
    }
  },
  created () {
    this.preFormObj = { dataSource: 'es' }
    this.queryForm = [
      {
        prop: 'dataSource',
        label: () => this.$t('vendorMod.dataSources'), // 数据来源
        type: 'select',
        options: [
          { value: 'es', label: 'es' }
        ]
      },
      {
        prop: 'username',
        label: () => this.$t('dataConfMod.username1') // '操作人账号'
      },
      {
        prop: 'methodName',
        label: () => this.$t('dataConfMod.methodName') // '操作名称'
      },
      {
        prop: 'requestUrl',
        label: () => this.$t('dataConfMod.requestUrl') // '请求URL'
      },
      {
        prop: 'userType',
        type: 'dict',
        code: 'USER_TYPE',
        label: () => this.$t('dataConfMod.userType') // '账号类型'
      },
      {
        prop: 'requestIp',
        label: () => this.$t('dataConfMod.requestIp') // '请求IP'
      },
      {
        prop: 'resultStatus',
        label: () => this.$t('dataConfMod.resultStatus') // '结果状态'
      },
      {
        prop: 'model',
        label: () => this.$t('dataConfMod.model'), // '模块'
        type: 'dict',
        code: 'REQUEST_MODULE_NAME'
      },
      {
        prop: 'operationTimeStart',
        type: 'datetime',
        label: () => this.$t('dataConfMod.operationTimeStart') // '操作日期开始'
      },
      {
        prop: 'operationTimeEnd',
        type: 'datetime',
        label: () => this.$t('dataConfMod.operationTimeEnd') // '操作日期结束'
      },
      {
        prop: 'requestParam',
        label: () => this.$t('dataConfMod.requestParam')
      }, // 请求参数
      {
        prop: 'responseResult',
        label: () => this.$t('dataConfMod.responseResult1')
      }, // 请求结果
      { prop: 'logInfo', label: () => this.$t('dataConfMod.logInfo') }, // 日志记录
      { prop: 'errorInfo', label: () => this.$t('dataConfMod.errorInfo') }, // 错误信息
      { prop: 'sql', label: () => this.$t('dataConfMod.sqlSearch') } // SQL查询
    ]

    this.tableHeader = [
      {
        prop: 'model',
        width: 140,
        label: () => this.$t('dataConfMod.model'), // '模块'
        dataType: 'dict',
        code: 'REQUEST_MODULE_NAME'
      },
      {
        prop: 'nickname',
        width: 120,
        label: () => this.$t('dataConfMod.nickname1') // '操作人'
      },
      {
        prop: 'username',
        width: 120,
        label: () => this.$t('dataConfMod.username1') // '操作人账号'
      },
      {
        prop: 'userType',
        width: 120,
        label: () => this.$t('dataConfMod.userType'), // '用户类型'
        dataType: 'dict',
        code: 'USER_TYPE'
      },
      {
        prop: 'methodName',
        width: 140,
        label: () => this.$t('dataConfMod.methodName') // '操作名称'
      },
      {
        prop: 'operationTime',
        width: 140,
        dataType: 'dateTime',
        label: () => this.$t('dataConfMod.operationTime') // '操作时间'
      },
      {
        prop: 'requestIp',
        width: 140,
        label: () => this.$t('dataConfMod.requestIp') // '请求IP'
      },
      {
        prop: 'requestUrl',
        minWidth: 200,
        label: () => this.$t('dataConfMod.requestUrl') // '请求URL'
      },
      {
        prop: 'requestParam',
        showType: 'button',
        btnStyle: 'text',
        width: 120,
        align: 'center',
        formattor: (value) => (value ? this.$t('common.view') : null),
        callback: (row) => this.showResultView(row.requestParam),
        label: () => this.$t('dataConfMod.requestParam') // '请求参数'
      },
      {
        prop: 'responseResult',
        showType: 'button',
        btnStyle: 'text',
        width: 120,
        align: 'center',
        formattor: (value) => (value ? this.$t('common.view') : null),
        callback: (row) => this.showResultView(row.responseResult),
        label: () => this.$t('dataConfMod.responseResult') // '返回结果'
      },
      {
        prop: 'logInfo',
        label: () => this.$t('dataConfMod.logInfo'), // 日志记录
        width: 120,
        showType: 'button',
        btnStyle: 'text',
        formattor: () => this.$t('common.view'), //  查看
        callback: (row) => this.showLogInfoView(row.logInfo)
      },
      {
        prop: 'errorInfo',
        width: 120,
        showType: 'button',
        btnStyle: 'text',
        label: () => this.$t('dataConfMod.errorInfo'), // '错误信息'
        formattor: (value) => (value ? this.$t('common.view') : null),
        callback: (row) => this.showResultView(row.errorInfo, 'view')
      },
      {
        prop: 'resultStatus',
        width: 120,
        label: () => this.$t('dataConfMod.resultStatus') // '结果状态'
      },
      {
        prop: 'requestStartTime',
        width: 140,
        dataType: 'dateTime',
        label: () => this.$t('dataConfMod.requestStartTime') // '请求开始时间'
      },
      {
        prop: 'requestEndTime',
        width: 140,
        dataType: 'dateTime',
        label: () => this.$t('dataConfMod.requestEndTime') // '请求结束时间'
      },
      {
        prop: 'responseDate',
        width: 140,
        label: () => this.$t('dataConfMod.responseDate') // '响应时间(ms)'
      }
    ]

    this.defaultTableHeader = this.tableHeader
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    showResultView (json, mode = 'tree') {
      this.viewVisible = true
      try {
        this.json = JSON.parse(json)
      } catch (e) {
        this.json = json
      }
      this.mode = mode
    },
    showLogInfoView (val) {
      this.globalLogInfo = val
      this.viewlogInfoVisible = true
    },
    syncFilterParams (values) {
      this.queryParam = values
    },
    reRenderCharts2 (val) {
      this.gloMiSeconds = (this.timeList.find((v) => v.value == val) || {}).miSeconds
      this.getSearchData()
    },
    openLogDialog () {
      this.getSearchData()
    },
    getSearchData () {
      this.$http({
        url: '/api-base/logsearch/agg',
        method: 'POST',
        data: {
          interval: this.gloMiSeconds,
          ...this.queryParam
        },
        loading: true
      })
        .then((data) => {
          if (data && data.data) {
            this.column1Arr = data.data
            this.viewGlobalVisible = true
          }
        })
    },
    getQuerydata (v) {
      this.queryParam = v || this.queryParam
      if (this.queryParam.dataSource === 'es') {
        this.tableUrl = '/api-base/logsearch/search'
      } else if (this.queryParam.dataSource === 'mysql') {
        this.tableUrl = '/api-log/useroperation/listPage'
      }
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    }
  }
}
</script>
<style scoped lang="scss">
#example-ul {
  li {
    list-style: none;
  }
}
.the_global_info {
  height: 440px;
  overflow: auto;
  line-height: 1.5;
  border: 1px solid #eee;
  border-radius: 5px;
  padding: 11px;
  .el-row {
    padding: 11px;
    .el-col > span {
      padding-right: 11px;
    }
  }
}
.the_loginfo {
  height: 300px;
  overflow: auto;
  line-height: 1.5;
  border: 1px solid #eee;
  border-radius: 5px;
  padding: 11px;
}
</style>
