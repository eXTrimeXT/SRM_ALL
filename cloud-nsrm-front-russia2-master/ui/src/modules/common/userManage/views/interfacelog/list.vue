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
        label: this.$t('vendorMod.interfaceName')
      },
      {
        prop: 'status',
        label: this.$t('components.stratProcess.headers.docStatusValue'),
        type: 'dict',
        code: 'INTERFACE_LOG_STATUS'
      },
      {
        prop: 'serviceInfo',
        label: this.$t('vendorMod.content')
      },
      {
        prop: 'serviceType',
        label: this.$t('reportMod.apiType'),
        type: 'dict',
        code: 'INTERFACE_LOG_SERVICE_TYPE'
      },
      {
        prop: 'type',
        label: this.$t('interfacelog.label2'),
        type: 'dict',
        code: 'INTERFACE_LOG_TYPE'
      },
      {
        prop: 'billId',
        label: this.$t('vendor.businessDocument')
      },
      {
        prop: 'billType',
        label: this.$t('bidMod.billType'),
        type: 'dict',
        code: '' // 没找到code
      },
      {
        prop: 'creationDateList',
        label: this.$t('common.creationTime'),
        type: 'daterange'
      }
    ]
    this.tableHeader = [
      {
        prop: 'serviceName',
        label: this.$t('vendorMod.interfaceName'),
        width: 100
      },
      {
        prop: 'serviceType',
        label: this.$t('reportMod.apiType'),
        width: 100,
        dataType: 'dict',
        code: 'INTERFACE_LOG_SERVICE_TYPE'
      },
      {
        prop: 'type',
        label: this.$t('interfacelog.label2'),
        width: 100,
        dataType: 'dict',
        code: 'INTERFACE_LOG_TYPE'
      },
      {
        prop: 'status',
        label: this.$t('components.stratProcess.headers.docStatusValue'),
        dataType: 'dict',
        code: 'INTERFACE_LOG_STATUS'
      },
      {
        prop: 'billId',
        label: this.$t('vendor.businessDocument'),
        width: 120
      },
      {
        prop: 'billType',
        label: this.$t('bidMod.billType'),
        width: 100,
        dataType: 'dict',
        code: ''
      },
      {
        prop: 'dealTime',
        label: this.$t('vendor.numberOfPushes'),
        width: 100
      },
      {
        prop: 'finishDate',
        label: this.$t('meeting.completeDate'),
        width: 100,
        dataType: 'dateTime'
      },
      {
        prop: 'createdUserName', // createdBy
        label: this.$t('common.creator'),
        width: 100
      },
      {
        prop: 'creationDate',
        label: this.$t('common.creationTime'),
        width: 100,
        dataType: 'dateTime'
      },
      {
        prop: 'lastUpdatedUserName', // lastUpdatedBy
        label: this.$t('common.lastUpdatePeople'),
        width: 120
      },
      {
        prop: 'lastUpdateDate',
        label: this.$t('dataConfMod.lastUpdateDate'),
        width: 120,
        dataType: 'dateTime'
      },
      {
        prop: 'attachmentId',
        label: this.$t('components.headers.operation'),
        width: 120,
        showType: 'buttons',
        buttons: [
          {
            callback: row => this.eidtItem(row),
            formattor (val) {
              return this.$t('common.view')
            }
          },
          {
            callback: function (row) {
              this.sendAgain(row)
            }.bind(this),
            formattor (val) {
              return this.$t('supRisk.resend')
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
