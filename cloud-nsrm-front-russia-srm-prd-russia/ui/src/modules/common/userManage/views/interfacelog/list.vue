<template>
  <el-container
    class="flex-container-notab the_announcements_wrapper"
    direction="vertical"
  >
    <el-main class="main-the-body">
      <FormWrapper
        :form-array="preArr"
        form-label-width="120px"
        @getFormData="getQuerydata"
      />
      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :comActive="$attrs['changeTab']"
        url="/api-ac/interfacelog/listPage"
      />
    </el-main>
  </el-container>
</template>

<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import interfacelogEdit from './edit'
import { tabTodoMixin, tabTodoWatch } from '@/utils/mixins'
import { interfaceApi } from 'mod@/common/userManage/api'

export default {
  name: 'InterfacelogList',
  components: {
    TableView,
    MainHeader,
    FormWrapper
  },
  mixins: [tabTodoMixin, tabTodoWatch],
  data () {
    return {
      // 表格配置
      tableData: [],
      tableHeader: [],
      queryParam: {},
      pageSize: 15,
      gridId: 'list',
      // 搜索表单配置
      preArr: [],
      // 字典相关
      serviceNameOpts: [],
      typeOpts: [],
      billTypeOpts: [],
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
      }
    }
  },
  created () {
    this.preArr = [
      {
        prop: 'serviceName',
        label: '接口名称'
      },
      {
        prop: 'status',
        label: '状态',
        type: 'dict',
        code: 'INTERFACE_LOG_STATUS'
      },
      {
        prop: 'serviceInfo',
        label: '内容'
      },
      {
        prop: 'serviceType',
        label: '接口类型',
        type: 'dict',
        code: 'INTERFACE_LOG_SERVICE_TYPE'
      },
      {
        prop: 'type',
        label: '传输类型',
        type: 'dict',
        code: 'INTERFACE_LOG_TYPE'
      },
      {
        prop: 'billId',
        label: '业务单据ID'
      },
      {
        prop: 'billType',
        label: '单据类型',
        type: 'dict',
        code: '' // 没找到code
      },
      {
        prop: 'creationDateList',
        label: '创建时间',
        type: 'daterange'
      }
    ]
    this.tableHeader = [
      {
        prop: 'serviceName',
        label: '接口名称',
        width: 100
      },
      {
        prop: 'serviceType',
        label: '接口类型',
        width: 100,
        dataType: 'dict',
        code: 'INTERFACE_LOG_SERVICE_TYPE'
      },
      {
        prop: 'type',
        label: '传输类型',
        width: 100,
        dataType: 'dict',
        code: 'INTERFACE_LOG_TYPE'
      },
      {
        prop: 'status',
        label: '状态',
        dataType: 'dict',
        code: 'INTERFACE_LOG_STATUS'
      },
      {
        prop: 'billId',
        label: '业务单据ID',
        width: 120
      },
      {
        prop: 'billType',
        label: '单据类型',
        width: 100,
        dataType: 'dict',
        code: ''
      },
      {
        prop: 'dealTime',
        label: '推送次数',
        width: 100
      },
      {
        prop: 'finishDate',
        label: '完成时间',
        width: 100
      },
      {
        prop: 'createdUserName', // createdBy
        label: '创建人',
        width: 100
      },
      {
        prop: 'creationDate',
        label: '创建时间',
        width: 100
      },
      {
        prop: 'lastUpdatedUserName', // lastUpdatedBy
        label: '最后更新人',
        width: 120
      },
      {
        prop: 'lastUpdateDate',
        label: '最后更新时间',
        width: 120
      },
      {
        prop: 'attachmentId',
        label: '操作',
        width: 120,
        showType: 'buttons',
        buttons: [
          {
            callback: row => this.eidtItem(row),
            formattor (val) {
              return '查看'
            }
          },
          {
            callback: function (row) {
              this.sendAgain(row)
            }.bind(this),
            formattor (val) {
              return '重发'
            },
            show: function (row) {
              if (row.status == 'FAIL' && row.type == 'SEND') {
                return true
              } else {
                return false
              }
            }
          }
        ]
      }
    ]
  },
  mounted () {
    this.getQuerydata()
  },
  methods: {
    dolayout () {
      this.$refs[this.gridId].doLayout()
    },
    getQuerydata (v) {
      if (v && v.creationDateList) {
        v.creationDateBegin = v.creationDateList[0]
        v.creationDateEnd = v.creationDateList[1]
        delete v.creationDateList
      }
      if (v) this.queryParam = v
      this.$nextTick(() => this.$refs[this.gridId].query())
    },
    eidtItem ({ logId, serviceName }) {
      this.$emit('tab-add', {
        component: interfacelogEdit,
        params: { logId },
        title: serviceName + ' - ' + logId,
        name: 'interfacelogEdit' + logId
      })
    },
    // 对话框
    closeDialog () {
      this.form = {
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
      }
      this.dialogVisible = false
    },
    showDialog () {
      this.dialogVisible = true
    },
    send () {
      interfaceApi.sendInterface().then(res => {
        this.getQuerydata()
      })
    },
    sendAgain ({ logId }) {
      interfaceApi.sendAgain({ logId }).then(res => {
        this.getQuerydata()
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.the_announcements_wrapper {
  .form-incontainer {
    height: 400px;
    overflow: auto;
  }
  .download-link-wrap {
    .download-link-item {
      color: #1890ff;
    }
    .close-icon {
      font-weight: bold;
      cursor: pointer;
    }
  }
  .notice-vendor {
    display: flex;
    flex-wrap: wrap;
    border: 1px solid #ddd;
    min-height: 30px;
    padding: 5px 5px 0 5px;
    align-items: center;
    .tag {
      margin-right: 5px;
      margin-bottom: 5px;
    }
  }
}
</style>
