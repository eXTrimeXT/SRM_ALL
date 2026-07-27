<template>
  <el-container
    class="flex-container-aside the_dict_wrapper"
    style="min-width: 900px"
  >
    <el-aside
      width="50%"
      style="padding: 0px; padding-right: 11px"
    >
      <el-container
        class="flex-container-notab"
        direction="vertical"
      >
        <el-main>
          <form-wrapper
            :form-array="preArr"
            :col-length="colSpan"
            form-label-width="70px"
            @getFormData="getQuerydata"
          />

          <main-header
            :l-span="22"
            :r-span="2"
          />
          <table-view
            :ref="gridId"
            :table-data="tableData"
            :table-header="tableHeader"
            :current-change="handleCurrentChange"
            :page-size="pageSize"
            :pre-query-data="queryParam"
            :show-filter-bar="showFilterBar == 1"
            url="/api-rbac/userTraces/listPage"
          />
        </el-main>
      </el-container>
    </el-aside>
    <!-- 右边-条目区域 -->
    <el-container
      class="flex-container-notab"
      direction="vertical"
    >
      <el-main>
        <div style="height: 53px" />

        <main-header
          :l-span="22"
          :r-span="2"
        />
        <table-view
          :ref="gridRightId"
          :table-header="tableHeaderRight"
          :page-size="pageSize"
          :pre-query-data="queryParamRight"
          :show-filter-bar="showFilterBar == 1"
          url="/api-rbac/traceinfo/listPage"
        />
      </el-main>
    </el-container>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'

export default {
  name: 'DictionaryMaintenance',
  components: {
    TableView,
    MainHeader,
    FormWrapper
  },
  provide () {
    return { tableName: this.name, defaultTableHeader: this.tableHeader }
  },
  data () {
    return {
      name: 'dictionaryTableList',
      description: '',
      pageSize: 15,
      gridId: 'list',
      gridRightId: 'listRight',
      currentRow: null,
      showFilterBar: 1,
      tableHeader: [],
      tableData: [],
      queryParamRight: {},
      // 右边的字段
      tableData2: [],
      colSpan: 2,
      dialogFormVisible2: false,
      isActive: false,
      preArr: [
        {
          prop: 'nowDate',
          type: 'date',
          label: () => this.$t('dataConfMod.nowDate') // '日期'
        },
        {
          prop: 'username',
          label: () => this.$t('dataConfMod.username') // '用户名'
        },
        {
          prop: 'userType',
          type: 'dict',
          code: 'USER_TYPE',
          label: () => this.$t('dataConfMod.userType') // '账号类型'
        }
      ],
      isedited: false,
      queryParam: {},
      queryItemParam: {},
      querydictId: '',
      tableHeaderRight: []
    }
  },
  created () {
    this.tableHeader = [
      {
        prop: 'nowDate',
        label: () => this.$t('dataConfMod.nowDate'),
        width: 100
      },
      {
        prop: 'username',
        label: () => this.$t('dataConfMod.username'),
        width: 100
      },

      {
        prop: 'userType',
        label: () => this.$t('dataConfMod.userType'),
        dataType: 'dict',
        code: 'USER_TYPE',
        width: 100
      },
      {
        prop: 'loginNum',
        label: () => this.$t('dataConfMod.loginNum'),
        width: 100
      },
      {
        prop: 'cumulativeOnlineTime',
        label: () => this.$t('dataConfMod.cumulativeOnlineTime')
      }
    ]
    this.tableHeaderRight = [
      {
        prop: 'loginDate',
        label: () => this.$t('dataConfMod.loginDate'),
        width: 150
      },
      {
        prop: 'logoutDate',
        label: () => this.$t('dataConfMod.logoutDate'),
        width: 150
      },
      {
        prop: 'singleOnlineTime',
        label: () => this.$t('dataConfMod.singleOnlineTime'),
        width: 100
      },
      {
        prop: 'logIp',
        label: () => this.$t('dataConfMod.logIp')
      }
    ]
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    getQuerydata (v) {
      this.queryParam = v
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    getQuerydataRight (v) {
      if (!(this.currentRow || {}).traceId) {
        return
      }
      this.queryParamRight = {
        traceId: this.currentRow.traceId,
        ...v
      }
      this.$nextTick(() => {
        this.$refs[this.gridRightId].query()
      })
    },
    handleCurrentChange (val) {
      this.currentRow = val
      this.getQuerydataRight()
    }
  }
}
</script>
<style scoped lang="scss">
.the_dict_wrapper {
  border-top: 1px solid #eee;
  :deep(aside) {
    line-height: 16px !important;
  }
  :deep(.el-table th > .cell) {
    display: inline-block;
    white-space: nowrap;
    word-break: keep-all;
    text-overflow: unset;
  }
}
</style>
