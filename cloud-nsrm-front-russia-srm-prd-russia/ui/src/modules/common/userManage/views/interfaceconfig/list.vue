<template>
  <el-container
    class="flex-container-notab the_announcements_wrapper11"
    direction="vertical"
  >
    <el-main class="main-the-body">
      <FormWrapper
        :form-array="preArr"
        form-label-width="120px"
        @getFormData="getQuerydata"
      />
      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <el-button
            type="primary"
            @click="add"
          >
            {{ $t('common.add') }}
          </el-button>
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :comActive="$attrs['changeTab']"
        url="/api-ac/interfaceconfig/listPage"
      />
    </el-main>
  </el-container>
</template>

<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import QuickSearch from 'lib@/components/QuickSearch'
import interfaceconfigEdit from './edit'
import { tabTodoMixin, tabTodoWatch } from '@/utils/mixins'

export default {
  name: 'InterfaceconfigList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    QuickSearch
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
      selectDictionary: {},
      preArr: [],
      // 字典相关
      dataFormatOpts: [],
      ifChangeOpts: [],
      dataTypeOpts: [],
      sourceOpts: [],
      paramTypeOpts: [],
      columnTypeOpts: [],
      allColumns: [],
      childColumns: [],
      sourceColumnOpts: [],
      sourceColumnTypeOpts: [],
      paramStructOpt: []
    }
  },
  created () {
    this.preArr = [
      {
        prop: 'interfaceName',
        label: '接口名称'
      },
      {
        prop: 'interfaceCode',
        label: '接口编码'
      },
      {
        prop: 'systemName',
        label: '系统'
      },
      {
        prop: 'protocol',
        label: '协议类型',
        type: 'dict',
        code: 'INTERFACE_LOG_SERVICE_TYPE'
      },
      {
        prop: 'type',
        label: '传输类型',
        type: 'dict',
        code: 'INTERFACE_LOG_TYPE'
      }
    ]
    this.tableHeader = [
      {
        prop: 'interfaceName',
        label: '接口名称',
        width: 200
      },
      {
        prop: 'interfaceCode',
        label: '接口编码',
        width: 100
      },
      {
        prop: 'interfaceUrl',
        label: '接口地址'
      },
      {
        prop: 'systemName',
        label: '系统',
        width: 100
      },
      {
        prop: 'type',
        label: '传输类型',
        width: 120,
        dataType: 'dict',
        code: 'INTERFACE_LOG_TYPE'
      },
      {
        prop: 'method',
        label: '调用方式',
        width: 120,
        dataType: 'dict',
        code: 'API_METHOD'
      },
      {
        prop: 'protocol',
        label: '协议类型',
        width: 100,
        dataType: 'dict',
        code: 'INTERFACE_LOG_TYPE'
      },
      {
        prop: 'createdUserName', // createdBy
        label: '创建人',
        width: 90
      },
      {
        prop: 'creationDate',
        label: '创建时间',
        width: 100
      },
      {
        prop: 'lastUpdatedUserName', // lastUpdatedBy
        label: '更新人',
        width: 120
      },
      {
        prop: 'lastUpdateDate',
        label: '最后更新时间',
        width: 130
      },
      {
        prop: 'attachmentId',
        label: '操作',
        showType: 'buttons',
        fixed: 'right',
        buttons: [
          {
            callback: row => this.eidtItem(row),
            formattor (val) {
              return '编辑'
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
    add () {
      this.$emit('tab-add', {
        component: interfaceconfigEdit,
        params: { flag: 'add' },
        title: this.$t('common.add'),
        name: 'interfaceconfigEdit'
      })
    },
    eidtItem ({ interfaceId, interfaceName }) {
      this.$emit('tab-add', {
        component: interfaceconfigEdit,
        params: { flag: 'edit', interfaceId },
        title: interfaceName + '-' + interfaceId,
        name: 'interfaceconfigEdit' + interfaceId
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.the_announcements_wrapper11 {
  .form-incontainer {
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
  .tab-form-style {
    // padding-bottom: 0 !important;
  }
}
</style>
