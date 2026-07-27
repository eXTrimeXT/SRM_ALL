<template>
  <el-container
    class="flex-container the_currency_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="queryForm"
        @getFormData="getQuerydata"
      />
      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <AuthorityButton
            code="base:timedTaskBind:add"
            type="primary"
            @click="addNew"
          >
            <!-- 新增 -->
            {{ $t("common.add") }}
          </AuthorityButton>
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-data="tableList"
        :table-header="tableHeader"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :show-filter-bar="showFilterBar === 1"
        url="/api-base/quartz/scheduleList"
      />
    </el-main>
    <!-- 新增 编辑弹框区域-->
    <srm-dialog
      :title="dialogTitle"
      :visible.sync="dialogFormVisible"
      :close-on-click-modal="false"
      size="middle"
    >
      <el-form
        ref="orgform"
        :model="purchaseModel.purchaseform"
        :rules="purchaseModel.rules"
      >
        <!-- 任务名称 -->
        <el-form-item
          :label="$t('dataConfMod.strategyName')"
          prop="strategyName"
        >
          <el-input v-model="purchaseModel.purchaseform.strategyName" />
        </el-form-item>
        <!-- URL -->
        <el-form-item
          :label="$t('dataConfMod.strategyUrl')"
          prop="strategyUrl"
        >
          <el-input v-model="purchaseModel.purchaseform.strategyUrl" />
        </el-form-item>
      </el-form>
      <div
        slot="footer"
        class="dialog-footer"
      >
        <el-button @click="dialogFormVisible = false">
          <!-- 取 消 -->
          {{ $t("common.cancel") }}
        </el-button>
        <el-button
          type="primary"
          @click="comfirmSave"
        >
          <!-- 确 定 -->
          {{ $t("common.confirm") }}
        </el-button>
      </div>
    </srm-dialog>
    <srm-dialog
      :title="dialogTitle1"
      :visible.sync="historyVisible"
      :close-on-click-modal="false"
      size="large"
    >
      <el-container style="height: 400px;">
        <TableView
          :ref="gridId1"
          :table-data="tableList"
          :table-header="tableHeader1"
          :page-size="pageSize"
          :pre-query-data="queryParam1"
          :show-filter-bar="showFilterBar === 1"
          url="/api-base/quartz/showHistoryPage"
        />
      </el-container>
      <div
        slot="footer"
        class="dialog-footer"
      >
        <el-button
          type="primary"
          @click="historyVisible = false"
        >
          <!-- 确 定 -->
          {{ $t("common.confirm") }}
        </el-button>
      </div>
    </srm-dialog>
    <!-- 查看 -->
    <srm-dialog
      :title="$t('common.view')"
      :visible.sync="viewVisible"
      :close-on-click-modal="false"
      size="large"
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
          {{ $t("common.confirm") }}
        </el-button>
      </div>
    </srm-dialog>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import vueJsonEditor from 'vue-json-editor'

export default {
  name: 'TimedTaskBind',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    vueJsonEditor
  },
  data () {
    return {
      pageSize: 15,
      gridId: 'list',
      gridId1: 'list1',
      showFilterBar: 1,
      queryParam: {},
      queryParam1: {},
      json: '',
      mode: 'view',
      viewVisible: false,
      dialogFormVisible: false,
      historyVisible: false,
      queryForm: [], // 查询条件
      tableHeader: [], // 表格列数据
      tableHeader1: [], // 表格列数据
      tableList: [],
      curOpt: 'add',
      dialogTitle: this.$t('common.add'), // '新增税率'
      dialogTitle1: this.$t('dataConfMod.history'), // '新增税率'
      purchaseModel: {
        purchaseform: {
          strategyName: '',
          strategyUrl: ''
        },
        rules: {
          strategyName: [
            { required: true, message: this.$t('dataConfMod.strategyNameMsg') }
          ],
          strategyUrl: [
            { required: true, message: this.$t('dataConfMod.strategyUrlMsg') }
          ]
        }
      }
    }
  },
  created () {
    let _this = this
    this.queryForm = [
      {
        prop: 'strategyName',
        label: () => this.$t('dataConfMod.strategyName') // '任务名称'
      }
    ]
    this.tableHeader1 = [
      {
        prop: 'triggerName',
        label: () => this.$t('dataConfMod.triggerName') // '任务名称'
      },
      {
        prop: 'url',
        label: () => this.$t('dataConfMod.strategyUrl') // 'URL'
      },
      {
        prop: 'result',
        label: () => this.$t('dataConfMod.strategyResult'), // '调用结果'
        formattor: value => (value ? this.$t('common.view') : null),
        callback: row => this.showResultView(row.result, 'view'),
        showType: 'button',
        width: 95,
        btnStyle: 'text'
      },
      {
        prop: 'errorLog',
        label: () => this.$t('dataConfMod.errorLog'), // '异常日志'
        formattor: value => (value ? this.$t('common.view') : null),
        callback: row => this.showResultView(row.errorLog, 'view'),
        showType: 'button',
        width: 95,
        btnStyle: 'text'
      },
      {
        prop: 'creationDate',
        label: () => this.$t('dataConfMod.creationDate') // '创建时间'
      }
    ]
    this.tableHeader = [
      {
        prop: 'strategyName',
        label: () => this.$t('dataConfMod.strategyName') // '任务名称'
      },
      {
        prop: 'strategyUrl',
        label: () => this.$t('dataConfMod.strategyUrl') // '税率编码'
      },
      {
        prop: 'lastUpdateDate',
        width: 140,
        label: () => this.$t('dataConfMod.lastUpdateDate') // '最后更新时间'
      },
      {
        label: () => this.$t('common.operation'), // '操作'
        width: '220',
        // fixed: "right",
        showType: 'buttons',
        buttons: [
          {
            code: 'timedTaskBind:history',
            callback: row => this.history(row),
            formattor: () => _this.$t('dataConfMod.history') // 历史
          },
          {
            code: 'timedTaskBind:transfer',
            callback: row => this.transfer(row),
            formattor: () => _this.$t('dataConfMod.transfer') // 调用
          },
          {
            code: 'timedTaskBind:edit',
            callback: row => this.editDetail(row),
            formattor: () => _this.$t('common.edit') // 修改
          },
          {
            code: 'timedTaskBind:delete',
            callback: row => this.deleteStrategy(row),
            formattor: () => _this.$t('common.delete') // 删除
          }
        ]
      }
    ]
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
    deleteStrategy (row) {
      // 是否确认删除本条数据
      this.$confirm(this.$t('common.ifDeleteData'), {
        confirmButtonText: this.$t('common.affirm'), // 确认
        cancelButtonText: this.$t('common.cancel'), // 取消
        type: 'warning'
      })
        .then(() => {
          this.$http({
            url: '/api-base/quartz/deleteStrategy',
            method: 'GET',
            params: { strategyId: row.strategyId }
          }).then(res => {
            // 返回数据处理
            this.$message({
              message: res.message,
              type: 'success'
            })
            this.getQuerydata() // 重新查询数据
          })
        })
    },
    transfer (row) {
      this.$http({
        url: '/api-base/quartz/manual',
        method: 'GET',
        params: { strategyId: row.strategyId },
        loading: true
      }).then(res => {
        // 返回数据处理
        this.$message({
          message: res.message,
          type: 'success'
        })
        this.getQuerydata() // 重新查询数据
      })
    },
    history (row) {
      this.historyVisible = true
      this.getQuerydata1({ triggerName: row.strategyName })
    },
    getQuerydata (v) {
      this.queryParam = v
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    getQuerydata1 (v) {
      this.queryParam1 = v
      this.$nextTick(() => {
        this.$refs[this.gridId1].query()
      })
    },
    addNew () {
      this.curOpt = 'add'
      this.controlHandle(this.curOpt)
    },
    // 新增、编辑
    controlHandle (type) {
      if (type === 'add') {
        // 新增
        this.dialogTitle = this.$t('common.add')
        this.purchaseModel.purchaseform = {}
      } else {
        // 修改
        this.dialogTitle = this.$t('common.edit')
      }
      this.dialogFormVisible = true
    },
    saveData () {
      this.saveOrUpdateHandle(this.curOpt)
    },
    // 新增编辑组织数据
    saveOrUpdateHandle () {
      const submitData = this.purchaseModel.purchaseform
      this.$http({
        url:
          this.curOpt === 'add'
            ? '/api-base/quartz/addStrategy'
            : '/api-base/quartz/updateStrategy',
        method: 'POST',
        data: submitData
      }).then(res => {
        if (res) {
          // 返回数据处理
          this.$message({
            message: res.message,
            type: 'success'
          })
          this.getQuerydata() // 重新查询数据
          this.dialogFormVisible = false
        }
      })
    },
    editDetail (row) {
      this.curOpt = 'edit'
      this.$http({
        method: 'GET',
        url: '/api-base/quartz/getStrategy',
        params: { strategyId: row.strategyId }
      }).then(res => {
        this.purchaseModel.purchaseform = res.data
      })
      this.dialogFormVisible = true
      this.controlHandle(this.curOpt)
    },
    comfirmSave () {
      this.saveOrUpdateHandle(this.curOpt)
    }
  }
}
</script>
