<template>
  <el-container
    class="flex-container-notab the_purchaseDirectory_wrapper"
    direction="vertical"
  >
    <el-main>
      <form-wrapper
        :form-array="queryForm"
        @getFormData="getQuerydata"
      />

      <main-header
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <el-button
            type="primary"

            @click="multiplePush"
          >
            批量重推
          </el-button>
        </template>
      </main-header>

      <table-view
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :current-change="handleCurrentChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        checkbox
        :set-selectable="setSelectable"
        :check-change="handleSelectionChange"
        url="/api-base/repush/listPageByParam"
      />
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'

export default {
  name: 'InterfaceRepushList',
  components: {
    TableView,
    MainHeader,
    FormWrapper
  },
  provide () {
    return { context: this }
  },
  data () {
    return {
      iModal: {
        title: this.$t('common.import'), // 导入
        upLoadUrl: '/api-sup/info/orgCategory/importExcel'
      },
      tableName: 'purchaseDirectoryList',
      defaultTableHeader: [],
      pageSize: 15,
      gridId: 'purchaseDirectoryList',
      curOpt: 'add',
      queryParam: {},
      filterParams: {},
      catStatus: [],
      formTypeList: [],
      currentRow: null,
      tableHeader: [],
      tableData: [],
      dialogFormVisible: false,
      displayList: [],
      queryForm: [
        { prop: 'title', label: () => this.$t('dataConfMod.title') }, // 标题
        {
          prop: 'pushStatus',
          label: () => this.$t('dataConfMod.pushStatus'), // 推送状态
          type: 'select',
          options: [
            { label: this.$t('dataConfMod.success'), value: 'SUCCESS' },
            { label: this.$t('dataConfMod.fail'), value: 'FAIL' }
          ]
        },
        { prop: 'module', label: () => this.$t('dataConfMod.model') } // 模块
      ],
      ableSelectTreeNodes: [],
      selectTreeOptions: [],
      multipleSelection: []
    }
  },
  created () {
    let _this = this
    this.tableHeader = [
      {
        prop: 'title',
        label: () => this.$t('dataConfMod.title'),
        minWidth: 150
      }, // 标题
      {
        prop: 'pushStatus',
        label: () => this.$t('dataConfMod.pushStatus'), // 推送状态
        width: 100,
        formattor (val) {
          return val == 'SUCCESS'
            ? _this.$t('dataConfMod.success')
            : _this.$t('dataConfMod.fail')
        }
      },
      { prop: 'module', label: () => this.$t('dataConfMod.model'), width: 150 }, // 模块
      {
        prop: 'creationDate',
        label: () => this.$t('common.creationTime'), // 创建时间
        width: 100
      },
      {
        prop: 'lastUpdateDate',
        label: () => this.$t('common.updateTime'), // 更新时间
        width: 100
      },
      {
        prop: 'operation',
        label: () => _this.$t('common.operation'), // 操作
        width: 120,
        btnStyle: 'text',
        fixed: 'right',
        showType: 'buttons',
        buttons: [
          {
            callback: function (row) {
              this.currentRow = row
              this.dorepush(row)
            }.bind(this),
            formattor () {
              return _this.$t('route.interfaceRepush') // 接口重推
            },
            show: row => (row.ifRepush === 1 ? 1 : 0)
          }
        ]
      }
    ]
    this.defaultTableHeader = this.tableHeader
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
    dorepush (row) {
      this.$http({
        url: '/api-base/repush/push',
        method: 'POST',
        data: { repushId: row.repushId },
        loading: true
      })
        .then(() => {
          this.$message.success(this.$t('common.success')) // 操作成功
          this.getQuerydata()
        })
    },
    // 保存
    handleCurrentChange (val) {
      this.currentRow = val
    },
    handleSelectionChange (val) {
      this.multipleSelection = val
    },
    multiplePush () {
      let idList = this.multipleSelection.map(v => v.repushId)
      if (idList.length === 0) {
        this.$message.warning(this.$t('bidMod.msgSelData')) // 请先选择数据！
        return
      }

      this.$http({
        url: '/api-base/repush/multiplePush',
        method: 'POST',
        data: idList,
        loading: true
      })
        .then(() => {
          this.$message.success(this.$t('common.success')) // 操作成功
          this.getQuerydata()
        })
    },
    setSelectable (row) {
      return row.ifRepush === 1 ? 1 : 0
    }
  }
}
</script>
