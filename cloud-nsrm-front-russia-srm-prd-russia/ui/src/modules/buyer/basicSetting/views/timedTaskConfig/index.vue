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
            code="base:timedTaskConfig:add"
            type="primary"
            @click="addNew"
          >
            <!-- 新增 -->
            {{ $t('common.add') }}
          </AuthorityButton>
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-data="tableList"
        :table-header="tableHeader"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        url="/api-base/quartz/triggerList"
      />
    </el-main>
    <!-- 新增 编辑弹框区域-->
    <srm-dialog
      :title="dialogTitle"
      :visible.sync="dialogFormVisible"
      :close-on-click-modal="false"
      size="middle"
    >
      <el-tabs v-model="activeTab">
        <!-- CRON触发器 -->
        <el-tab-pane
          name="1"
          :label="$t('dataConfMod.CRONTrigger')"
        >
          <el-form
            ref="orgform"
            :model="purchaseModel.purchaseform"
            :rules="purchaseModel.rules"
          >
            <el-form-item
              :label="$t('dataConfMod.triggerName')"
              prop="triggerName"
            >
              <el-input v-model="purchaseModel.purchaseform.triggerName" />
            </el-form-item>
            <el-form-item
              :label="$t('dataConfMod.cronExpression')"
              prop="cronExpression"
            >
              <el-input v-model.trim="purchaseModel.purchaseform.cronExpression" />
            </el-form-item>
          </el-form>
        </el-tab-pane>
        <!-- 简单触发器 -->
        <el-tab-pane
          name="2"
          :label="$t('dataConfMod.simpleTrigger')"
        >
          <el-form
            ref="orgform1"
            :model="purchaseModel.purchaseform1"
            :rules="purchaseModel.rules1"
          >
            <el-form-item
              :label="$t('dataConfMod.triggerName')"
              prop="triggerName"
            >
              <el-input v-model="purchaseModel.purchaseform1.triggerName" />
            </el-form-item>
            <el-form-item
              :label="$t('dataConfMod.startDate1')"
              prop="startDate"
            >
              <el-date-picker
                v-model="purchaseModel.purchaseform1.startDate"
                type="datetime"
                format="yyyy-MM-dd HH:mm:ss"
                value-format="yyyy-MM-dd HH:mm:ss"
              />
            </el-form-item>
            <el-form-item
              :label="$t('dataConfMod.repeatInterval')"
              prop="repeatInterval"
            >
              <el-input-number
                v-model="purchaseModel.purchaseform1.repeatInterval"
                style="width: 100%"
                :min="1"
              />
            </el-form-item>
            <el-form-item
              :label="$t('dataConfMod.repeatCount')"
              prop="repeatCount"
            >
              <el-input-number
                v-model="purchaseModel.purchaseform1.repeatCount"
                style="width: 100%"
                :min="1"
              />
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>

      <div
        slot="footer"
        class="dialog-footer"
      >
        <el-button @click="dialogFormVisible = false">
          <!-- 取 消 -->
          {{ $t('common.cancel') }}
        </el-button>
        <el-button
          type="primary"
          @click="comfirmSave"
        >
          <!-- 确 定 -->
          {{ $t('common.confirm') }}
        </el-button>
      </div>
    </srm-dialog>
    <srm-dialog
      :title="dialogTitle1"
      :visible.sync="historyVisible"
      :close-on-click-modal="false"
      size="large"
    >
      <el-container style="height: 300px">
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
          {{ $t('common.confirm') }}
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
    </srm-dialog>
  </el-container>
</template>

<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import vueJsonEditor from 'vue-json-editor'
import { purchaseBaseSetting } from 'modb@/basicSetting/api/basicSetting'

export default {
  name: 'TimedTaskConfig',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    vueJsonEditor
  },
  data () {
    return {
      json: null,
      pageSize: 15,
      gridId: 'list',
      gridId1: 'list1',
      showFilterBar: 1,
      activeTab: '1',
      queryParam: {},
      queryParam1: {},
      historyVisible: false,
      viewVisible: false,
      dialogFormVisible: false,
      queryForm: [], // 查询条件
      tableHeader: [], // 表格列数据
      tableHeader1: [], // 表格列数据
      tableList: [],
      curOpt: 'add',
      dialogTitle: this.$t('common.add'), // '新增税率'
      dialogTitle1: this.$t('dataConfMod.history'), // '新增税率'
      purchaseModel: {
        purchaseform: {},
        purchaseform1: {},
        rules1: {
          triggerName: [{ required: true, message: this.$t('dataConfMod.triggerName') }],
          repeatInterval: [
            {
              required: true,
              message: this.$t('dataConfMod.repeatIntervalMsg')
            }
          ],
          startDate: [{ required: true, message: this.$t('dataConfMod.startDateMsg') }],
          repeatCount: [{ required: true, message: this.$t('dataConfMod.repeatCountMsg') }]
        },
        rules: {
          triggerName: [{ required: true, message: this.$t('dataConfMod.triggerNameMsg') }],
          cronExpression: [
            {
              required: true,
              message: this.$t('dataConfMod.cronExpressionMsg')
            }
          ]
        }
      }
    }
  },
  created () {
    let _this = this
    this.queryForm = [
      {
        prop: 'triggerName',
        label: () => this.$t('dataConfMod.triggerName') // '策略名称'
      },
      {
        prop: 'triggerType',
        type: 'dict',
        code: 'TRIGGER_TYPE',
        label: () => this.$t('dataConfMod.triggerType') // '配置类型'
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
        showType: 'button',
        btnStyle: 'text',
        width: 95,
        align: 'center',
        formattor: (value) => (value ? _this.$t('common.view') : null),
        callback: (row) => this.showResultView(row.result)
      },
      {
        prop: 'errorLog',
        showType: 'button',
        btnStyle: 'text',
        width: 95,
        align: 'center',
        formattor: (value) => (value ? _this.$t('common.view') : null),
        callback: (row) => this.showResultView(row.errorLog),
        label: () => _this.$t('dataConfMod.errorLog') // '异常日志'
      },
      {
        prop: 'creationDate',
        label: () => _this.$t('dataConfMod.creationDate') // '创建时间'
      }
    ]
    this.tableHeader = [
      {
        prop: 'triggerName',
        width: 140,
        label: () => _this.$t('dataConfMod.triggerName') // '策略名称'
      },
      {
        prop: 'startDate',
        width: 140,
        label: () => this.$t('dataConfMod.startDate1') // '开始日期'
      },
      {
        prop: 'repeatCount',
        width: 95,
        label: () => this.$t('dataConfMod.repeatCount') // '重复次数'
      },
      {
        prop: 'repeatInterval',
        width: 95,
        label: () => this.$t('dataConfMod.repeatInterval') // '间隔(s)'
      },
      {
        prop: 'cronExpression',
        width: 140,
        label: () => this.$t('dataConfMod.cronExpression') // '表达式'
      },
      {
        prop: 'triggerType',
        width: 140,
        label: () => this.$t('dataConfMod.triggerType'), // '配置类型'
        dataType: 'dict',
        code: 'TRIGGER_TYPE'
      },
      {
        prop: 'triggerState',
        width: 80,
        label: () => this.$t('dataConfMod.triggerState'), // '状态'
        dataType: 'dict',
        code: 'TRIGGER_STATE'
      },
      {
        prop: 'prevFireDate',
        width: 140,
        label: () => this.$t('dataConfMod.prevFireDate') // '上次触发日期'
      },
      {
        prop: 'nextFireDate',
        width: 140,
        label: () => _this.$t('dataConfMod.nextFireDate') // '下次触发日期'
      },
      {
        label: () => _this.$t('common.operation'), // '操作'
        width: '180',
        fixed: 'right',
        showType: 'buttons',
        buttons: [
          {
            code: 'timedTaskConfig:history',
            callback: (row) => this.history(row),
            formattor: () => _this.$t('dataConfMod.history') // 历史
          },
          {
            code: 'timedTaskConfig:activate',
            callback: (row) => this.transfer(row),
            formattor: () => _this.$t('dataConfMod.activate') // 激活
          },
          {
            code: 'timedTaskConfig:dormancy',
            callback: (row) => this.unTransfer(row),
            formattor: () => _this.$t('dataConfMod.dormancy') // 休眠
          },
          {
            code: 'timedTaskConfig:delete',
            callback: (row) => this.deleteStrategy(row),
            formattor: () => _this.$t('common.delete') // 移除
          }
        ]
      }
    ]
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    showResultView (json) {
      this.viewVisible = true
      this.json = JSON.parse(json)
    },
    getQuerydata1 (v) {
      this.queryParam1 = v
      this.$nextTick(() => {
        this.$refs[this.gridId1].query()
      })
    },
    deleteStrategy (row) {
      // 是否确认移除本条数据
      this.$confirm(this.$t('dataConfMod.ifConfirmRemoveData'), this.$t('dataConfMod.ifRemove'), {
        confirmButtonText: this.$t('common.affirm'), // 确认
        cancelButtonText: this.$t('common.cancel'), // 取消
        type: 'warning'
      })
        .then(() => {
          this.$http({
            url: '/api-base/quartz/trigger/delete',
            method: 'POST',
            data: { triggerName: row.triggerName },
            loading: true
          }).then((res) => {
            // 返回数据处理
            this.$message({
              message: res.message,
              type: 'success'
            })
            this.getQuerydata() // 重新查询数据
          })
        })
    },
    unTransfer (row) {
      this.$http({
        url: '/api-base/quartz/trigger/pause',
        method: 'POST',
        data: { triggerName: row.triggerName },
        loading: true
      }).then((res) => {
        // 返回数据处理
        this.$message({
          message: res.message,
          type: 'success'
        })
        this.getQuerydata() // 重新查询数据
      })
    },
    transfer (row) {
      this.$http({
        url: '/api-base/quartz/trigger/resume',
        method: 'POST',
        data: { triggerName: row.triggerName },
        loading: true
      }).then((res) => {
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
      this.getQuerydata1({ triggerName: row.triggerName })
    },
    getQuerydata (v) {
      this.queryParam = v
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
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
      this.purchaseModel.purchaseform = {}
      this.purchaseModel.purchaseform1 = {}
    },
    // 新增编辑组织数据
    saveOrUpdateHandle () {
      const submitData = this.purchaseModel.purchaseform
      purchaseBaseSetting.rateSaveOrUpdate(submitData).then((res) => {
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
      this.purchaseModel.purchaseform = row
      this.dialogFormVisible = true
      this.controlHandle(this.curOpt)
    },
    comfirmSave () {
      if (this.activeTab === '1') {
        this.$http({
          url: '/api-base/quartz/trigger/addCron',
          method: 'POST',
          data: this.purchaseModel.purchaseform,
          loading: true
        }).then((res) => {
          // 返回数据处理
          this.$message({
            message: res.message,
            type: 'success'
          })
          this.getQuerydata() // 重新查询数据
          this.dialogFormVisible = false
        })
      } else {
        this.$http({
          url: '/api-base/quartz/trigger/addSimple',
          method: 'POST',
          data: this.purchaseModel.purchaseform1,
          loading: true
        }).then((res) => {
          // 返回数据处理
          this.$message({
            message: res.message,
            type: 'success'
          })
          this.dialogFormVisible = false
          this.getQuerydata() // 重新查询数据
        })
      }
    }
  }
}
</script>
