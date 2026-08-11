<template>
  <el-container class="flex-container-notab the_log_wrapper" direction="vertical">
    <el-main class="main-the-body">
      <div class="time-select-list">
        <el-radio-group v-model="timeRange" @input="timeRangeChange">
          <el-radio-button label="30min">
            <!-- 近30分钟 -->
            {{ $t("cusEntry.supplement20250211.near30Minutes") }}
          </el-radio-button>
          <el-radio-button label="1hour">
            <!-- 近1小时 -->
            {{ $t("cusEntry.supplement20250211.nearOneHour") }}
          </el-radio-button>
          <el-radio-button label="1day">
            <!-- 近1天 -->
            {{ $t("cusEntry.supplement20250211.oneDayAgo") }}
          </el-radio-button>
          <el-radio-button label="7day">
            <!-- 近7天 -->
            {{ $t("cusEntry.supplement20250211.nearSevenDays") }}
          </el-radio-button>
          <el-radio-button label="custom">
            <!-- 自定义 -->
            {{ $t("cusEntry.supplement20250211.customVariableName") }}
          </el-radio-button>
        </el-radio-group>
        <div v-if="timeRange=='custom'" class="customTime">
          <!-- :picker-options="endTimePickerOptions" -->
          <el-date-picker
            v-model="customValue"
            type="datetimerange"
            :range-separator="$t('components.to')"
            :start-placeholder="$t('components.beginDate')"
            :end-placeholder="$t('components.dateClosed')"
            :format="$formatDatePickerTime"
            value-format="yyyy-MM-dd HH:mm:ss"
            :default-time="['00:00:00', '23:59:59']"
            @change="customTimeChange"
          />
        </div>
      </div>
      <FormWrapper
        :formArray="preArr"
        formLabelWidth="120px"
        @synchronous-value="syncFilterParams"
        @getFormData="getQuerydata"
        @reset="resetFn"
      />
      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <ExportExcel
            page-url="/api-base/api-ql/AuditInfo/query"
            :table-header="tableHeader"
            :dict-codes="dictCodes"
            timeout="1000000"
            export-mode="front"
            export-type="meiqlApi"
            :generateMeiQLExportRequest="generateMeiQLExportRequest"
            type="default"
            :exportSize="1000"
          />
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :page-size="pageSize"
        :preQueryData="queryParam"
        :adeptMeiQl="true"
        url="/api-base/api-ql/AuditInfo/query"
      >
        <template #actionDesc="{scope}">
          <span> {{ scope.row.actionDesc }} </span>
          <!-- 后续根据类型 判断是否显示查看附件按钮 -->
          <el-button
            v-if="scope.row.opType.startsWith('FILE')"
            type="text"
            @click="viewFileInfo(scope.row)"
          >
            <!-- 查看附件 -->
            {{ $t("bidMod.fileList") }}
          </el-button>
        </template>
      </TableView>
    </el-main>
    <!-- 查看附件 -->
    <srm-dialog
      :close-on-click-modal="false"
      :visible.sync="dialogVisible"
      :title="$t('bidMod.fileList')"
      @close="dialogVisible = false"
    >
      <div>
        <FormWrapper
          :formArray="filePreArr"
          formLabelWidth="120px"
          :colLength="1"
          @getFormData="fileGetQuerydata"
        />
        <div style="margin-bottom: 16px;">
          <el-button :disabled="selectionRow==0" type="primary" @click="batchDownLoad">
            <!-- 批量下载 -->
            {{ $t("cusEntry.common.batchDownload") }}
          </el-button>
        </div>
        <el-table
          ref="fileTable"
          height="100%"
          :data="fileData"
          :stripe="true"
          border
          highlight-current-row
          :min-height="100"
          :max-height="400"
          style="min-height: 100px;"
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="index" align="center" fixed="left" />
          <el-table-column type="selection" />
          <!-- 附件名称 -->
          <el-table-column
            min-width="80px"
            :label="$t('common.fileUploadName')"
            prop="fileSourceName"
            show-overflow-tooltip
          >
            <template slot-scope="scope">
              <SrmCommonFile
                :default-file="{
                  fileId: scope.row.fileuploadId,
                  fileName: scope.row.fileSourceName
                }"
                :readonly="true"
              />
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="cancleHandle">
          {{ $t('common.cancel') }}
        </el-button>
      </div>
    </srm-dialog>
  </el-container>
</template>

<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import ExportExcel from 'lib@/components/export-excel'
import { downloadWithParam } from 'lib@/utils/file'
import { transformQuery } from '@/utils'
import { transformMQL } from '@/library/utils/util'
export default {
  name: 'BusinessOperationLogList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    ExportExcel
  },
  data () {
    return {
      timeRange: '1day',
      customValue: [],
      // 表格配置
      tableData: [],
      tableHeader: [],
      queryParam: {},
      filterParams: {},
      pageSize: 15,
      gridId: 'list',
      // 搜索表单配置
      preArr: [],
      filePreArr: [],
      title: '',
      // 对话框开关
      dialogVisible: false,
      form: {
        logId: null,
        serviceName: null,
        serviceType: null,
        type: null,
        status: null,
        billId: null,
        billType: null,
        dealTime: null,
        finishDate: null,
        targetSys: null,
        serviceInfo: null,
        returnInfo: null,
        errorInfo: null
      },
      fileData: [],
      fileDataBak: [],
      selectionRow: [],
      dictCodes: {
        opType: 'AUDIT_OP_TYPE',
        principalType: 'USER_TYPE',
        serviceCode: 'REQUEST_MODULE_NAME'
      },
      endTimePickerOptions: {
        disabledDate: time => {
          const nowDate = new Date()
          nowDate.setHours(0)
          nowDate.setMinutes(0)
          nowDate.setSeconds(0)
          nowDate.setMilliseconds(0)
          return time.getTime() > nowDate.getTime() + 24 * 60 * 60 * 1000
        }
      }
    }
  },
  created () {
    this.preArr = [
      {
        prop: 'permissionName',
        label: this.$t('cusEntry.supplement20250211.operationObject')  // '操作对象'
      },
      {
        prop: 'serviceCode',
        label: this.$t('cusEntry.supplement20250211.serviceModule'),  // '服务模块'
        type: 'dict',
        code: 'REQUEST_MODULE_NAME'
      },
      {
        prop: 'action',
        label: this.$t('dataConfMod.eventName')   // '事件名称'
      },
      {
        prop: 'actionDesc',
        label: this.$t('statusConfig.eventName')  // '事件描述'
      },
      {
        prop: 'opType',
        label: this.$t('dataConfMod.operateType'),  // '操作类型'
        type: 'dict',
        code: 'AUDIT_OP_TYPE'
      },
      {
        prop: 'principal',
        label: this.$t('cusEntry.supplement20250211.operationUser')  // '操作用户'
      },
      {
        prop: 'principalName',
        label: this.$t('cusEntry.supplement20250211.operationUserName')  // '操作用户名称'
      },
      {
        prop: 'principalType',
        label: this.$t('cusEntry.supplement20250211.userType'),  // '用户类型'
        type: 'dict',
        code: 'USER_TYPE'
      }
    ]
    this.tableHeader = [
      {
        prop: 'serviceCode',
        label: this.$t('cusEntry.supplement20250211.serviceModule'),  // '服务模块'
        code: 'REQUEST_MODULE_NAME',
        dataType: 'dict',
        width: 150
      },
      {
        prop: 'permissionName',
        label: this.$t('cusEntry.supplement20250211.operationObject'),  // '操作对象'
        width: 150,
        formattor: (value, row) => {
          return value || row.functionName
        }
      },
      {
        prop: 'action',
        label: this.$t('dataConfMod.eventName')  // '事件名称'
      },
      {
        prop: 'actionDesc',
        label: this.$t('statusConfig.eventName'),  // '事件描述'
        showType: 'slot',
        slot: 'actionDesc',
        minWidth: 200
      },
      {
        prop: 'opType',
        label: this.$t('dataConfMod.operateType'),  // '操作类型'
        minWidth: 120,
        dataType: 'dict',
        code: 'AUDIT_OP_TYPE'
      },
      {
        prop: 'principal',
        label: this.$t('cusEntry.supplement20250211.userAccountOperation'),  // '操作用户账号'
        minWidth: 150
      },
      {
        prop: 'principalName',
        label: this.$t('cusEntry.supplement20250211.operationUsername'),  // '操作用户名'
        minWidth: 150
      },
      {
        prop: 'principalType',
        label: this.$t('cusEntry.supplement20250211.userType'),  // '用户类型'
        dataType: 'dict',
        code: 'USER_TYPE'
      },
      {
        prop: 'fmtStartTime',
        label: this.$t('common.operationTime'),  // '操作时间'
        minWidth: 140
      },
      {
        prop: 'runDuration',
        label: this.$t('cusEntry.supplement20250211.timeConsuming'),  // '耗时'
        formattor: value => {
          return value + 'ms'
        }
      },
      {
        prop: 'clientIp',
        label: this.$t('cusEntry.supplement20250211.clientIp'),  // '客户端IP'
        minWidth: 120
      }
      // {
      //   prop: 'operation',
      //   label: '操作',
      //   showType: 'buttons',
      //   fixed: 'right',
      //   buttons: [
      //     {
      //       callback: row => this.eidtItem(row),
      //       formattor (val) {
      //         return '查看详情'
      //       }
      //     }
      //   ]
      // }
    ]
    this.filePreArr = [
      {
        prop: 'fileName',
        label: this.$t('common.fileUploadName')  // '附件名称'
      }
    ]
  },
  mounted () {
    this.getQuerydata()
  },
  methods: {
    generateMeiQLExportRequest () {
      return { body: this.queryParam }
    },
    syncFilterParams (values) {
      this.filterParams = values
    },
    getStartTime (type = '30min') {
      const now = new Date()
      let startTime = null
      if (type == '30min') {
        startTime = new Date(now.getTime() - 30 * 60 * 1000)
      } else if (type == '1hour') {
        startTime = new Date(now.getTime() - 60 * 60 * 1000)
      } else if (type == '1day') {
        startTime = new Date(now.getTime() - 24 * 60 * 60 * 1000)
      } else if (type == '7day') {
        startTime = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000)
      }
      return this.$dayjsParse(startTime, 'YYYY-MM-DD HH:mm:ss')
    },
    timeRangeChange (val) {
      if (this.timeRange == 'custom') {
        return false
      } else {
        this.getQuerydata(this.filterParams)
      }
    },
    customTimeChange (val) {
      this.getQuerydata(this.filterParams)
    },
    async getQuerydata (v = {}) {
      const now = new Date()
      let timeRange = this.timeRange
      if (timeRange == 'custom') {
        let customVal = this.customValue
        if (customVal && customVal.length > 0) {
          v['startTime'] = customVal[0]
          v['endTime'] = customVal[1]
        }
      } else {
        v['startTime'] = await this.getStartTime(timeRange)
        v['endTime'] = await this.$dayjsParse(now, 'YYYY-MM-DD HH:mm:ss')
      }
      // if (v) this.queryParam = v
      this.queryParam = transformMQL.listGetData(
        'AuditInfo',
        v,
        'startTime',
        undefined,
        'query',
        null,
        { startTime: 'ge', endTime: 'le' },
      )
      this.$nextTick(() => this.$refs[this.gridId].query())
    },
    resetFn () {
      this.getQuerydata(this.filterParams)
    },
    eidtItem ({ logId }) {
      this.$http({
        methods: 'GET',
        url: '/api-pj/pj/interface-log/get',
        params: { id: logId }
      }).then(res => {
        this.form = res.data
        this.showDialog()
        this.isModify = false
        this.title = this.$t('cusEntry.supplement20250211.logView')  // '日志查看'
      })
    },
    // 查看1附件
    viewFileInfo (row) {
      this.fileData = row.fileInfos || []
      this.fileDataBak = row.fileInfos || []
      this.dialogVisible = true
      this.$refs.fileTable?.doLayout()
    },
    fileGetQuerydata (v) {
      if (v && v.fileName) {
        let keyWord = v.fileName
        this.fileData = this.filterSelected(this.fileData, keyWord)
      } else {
        this.fileData = this.fileDataBak
      }
    },
    filterSelected (searchData, keyword) {
      let filterKey = ['fileSourceName']
      let newArr = []
      searchData.forEach(element => {
        for (let k = 0; k < filterKey.length; k++) {
          if (element[filterKey[k]].indexOf(keyword) > -1) {
            newArr.push({ ...element })
            break
          }
        }
      })
      return newArr
    },
    // 批量下载
    batchDownLoad () {
      if (this.selectionRow.length > 0) {
        this.selectionRow.forEach(item => {
          downloadWithParam(item.fileuploadId, item.fileSourceName).catch(() => {
            // 下载失败
            this.$message.error(this.$t('components.eio.downloadFail'))
          })
        })
      }
    },
    handleSelectionChange (selection) {
      this.selectionRow = selection
    },
    cancleHandle () {
      this.dialogVisible = false
      this.selectionRow = []
    }
  }
}
</script>

<style lang="scss" scoped>
.the_log_wrapper {
  .time-select-list {
    margin-bottom: 10px;
    vertical-align: middle;
    height: 30px;
    .customTime {
      display: inline-block;
      vertical-align: middle;
      margin-left: 5px;
      width: 320px;
      .el-range-editor {
        width: 100%;
      }
    }
  }
}
</style>
