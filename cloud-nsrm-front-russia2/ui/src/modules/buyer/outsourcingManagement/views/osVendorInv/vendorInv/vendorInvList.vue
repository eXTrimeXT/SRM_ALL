<template>
  <el-container
    class="flex-container drawingshead_list_wrapper"
    direction="vertical"
  >
    <el-main>
      <!-- 查询条件 -->
      <FormWrapper
        :form-array="formArray"
        @getFormData="getQuerydata"
        @synchronous-value="syncFilterParams"
      />

      <!-- 按钮域 -->
      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <AuthorityButton
            type="primary"
            @click="openSyncInvDialog"
          >
            <!-- 创建盘点单 -->
            {{ $t("outsource.inventorySheet") }}
          </AuthorityButton>
          <AuthorityButton
            type="primary"
            @click="showHistory"
          >
            <!-- 查看历史 -->
            {{ $t("outsource.viewHistory") }}
          </AuthorityButton>
        </template>
      </MainHeader>

      <!-- 列表 -->
      <TableView
        :ref="tableInfo.gridId"
        :table-data="tableInfo.tableData"
        :table-header="tableInfo.tableHeader"
        :row-index-fixed="false"
        :page-size="tableInfo.pageSize"
        :checkbox="false"
        :pre-query-data="tableInfo.queryParam"
        :source="vendorInvApi.stockListPage"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
      >
        <template #operationSlot="{ scope }">
          <el-button
            v-if="scope.row.taskStatus === 'EXECUTE_ERROR'"
            type="text"
            @click="retry(scope.row)"
          >
            <!-- 重试 -->
            {{ $t("marketBudget.retry") }}
          </el-button>
          <el-button
            v-if="scope.row.taskStatus === 'INV_ING'"
            type="text"
            @click="manage(scope.row)"
          >
            <!-- 管理 -->
            {{ $t("bidMod.management") }}
          </el-button>
          <el-button
            v-if="scope.row.taskStatus === 'INV_ING'"
            type="text"
            @click="endInv(scope.row)"
          >
            <!-- 结束盘点 -->
            {{ $t("outsource.endInventory") }}
          </el-button>
        </template>
      </TableView>

      <!--创建盘点单 弹窗-->
      <srm-dialog
        v-if="syncInvDialogVisible"
        :visible.sync="syncInvDialogVisible"
        :title="$t('outsource.inventorySheet')"
        size="small"
        append-to-body

        :close-on-click-modal="false"
      >
        <el-row>
          <el-col
            :span="8"
            style="line-height: 32px;"
          >
            <!-- 本次盘点名称 -->
            {{ $t("outsource.inventoryName") }}
          </el-col>
          <el-col :span="16">
            <!-- 请输入本次盘点名称 -->
            <el-input
              v-model="syncInvForm.invTaskTitle"
              :placeholder="$t('cusEntry.supplement20250211.inputInventoryName')"
            />
          </el-col>
        </el-row>

        <el-row style="margin-top: 15px;">
          <!--调整原因-->
          <el-col>
            <el-radio
              v-model="syncInvForm.taskRule"
              label="ALL"
            >
              <!-- 盘点所有 -->
              {{ $t("cusEntry.supplement20250211.inventoryAll") }}
            </el-radio>
            <el-radio
              v-model="syncInvForm.taskRule"
              label="VENDOR_DIVISION"
            >
              <!-- 按供应商分工规则 -->
              {{ $t("outsource.rulesBySupplier") }}
            </el-radio>
          </el-col>
        </el-row>

        <template
          #footer
          class="dialog-footer"
        >
          <el-button @click="syncInvDialogVisible = false">
            {{ $t("common.cancel") }}
          </el-button>
          <el-button
            type="primary"
            @click="saveSyncInv"
          >
            {{ $t("common.submit") }}
          </el-button>
        </template>
      </srm-dialog>
    </el-main>
  </el-container>
</template>

<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import QuickSearch from 'lib@/components/QuickSearch'
import { downloadFileLinkByPost } from 'lib@/utils/file'
import { parseTime } from '@/utils'
import vendorInvHistory from './vendorInvHistory.vue'
import vendorInvManage from './vendorInvManage.vue'
import { vendorInvApi } from 'modb@/outsourcingManagement/api'
export default {
  name: 'OsVendorInvList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    QuickSearch
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      vendorInvApi: vendorInvApi,
      formArray: [ // 列表查询参数定义
        {
          prop: 'invTaskTitle',
          label: this.$t('outsource.inventoryName1')  // '盘点名称'
        }
      ],
      tableInfo: { // 列表信息
        gridId: 'osVendorInvTable',
        tableData: [],
        tableHeader: [],
        pageSize: 15,
        queryParam: {} // 查询参数
      },
      syncInvDialogVisible: false,
      syncInvForm: {
        invTaskTitle: '',
        taskRule: 'VENDOR_DIVISION'
      }
    }
  },
  created () {
      // 列表定义
      this.tableInfo.tableHeader = [
        // 盘点流水号
        {
          label: this.$t('outsource.takingStockSerialNumbers'), // '盘点流水号'
          prop: 'invTaskNo'
        },
        {// 盘点名称
          label: this.$t('outsource.inventoryName1'),  // '盘点名称'
          prop: 'invTaskTitle'
        },
        {// 盘点状态
          label: this.$t('outsource.inventoryStatus'),  // '盘点状态'
          prop: 'taskStatus',
          formattor: val => this.$getDictLabel('SC_OS_VENDOR_INV_TASK_STATUS', val)
        },
        {// 创建人
          label: this.$t('common.creator'),  // '创建人'
          prop: 'createdBy'
        },
        {// 创建时间
          label: this.$t('common.creationTime'),  // '创建时间'
          prop: 'creationDate',
          dataType: 'dateTime'
        },
        {// 操作
          label: this.$t('components.headers.operation'),  //'操作'
          showType: 'slot',
          slot: 'operationSlot',
          width: '130'
        }
      ]
      this.getQuerydata()
  },
  methods: {
    // 列表查询
    getQuerydata (v, _this) {
      if (!_this) {
        _this = this
      }
      _this.tableInfo.queryParam = v
      _this.$nextTick(() => {
        _this.$refs[_this.tableInfo.gridId].query()
      })
    },
    syncFilterParams (values) {
      this.tableInfo.queryParam = values
    },
    // 导出
    exportExcel () {
      let params = this.tableInfo.queryParam

      downloadFileLinkByPost(
        '/api-sup-ce/os/inv/realTime/exportExcel',
        parseTime(new Date()) + this.$t('qualitySynergy.excHandleExp'),
        params
      ).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail')) // 下载失败
      })
    },
    /* 打开创建盘点单弹窗 */
    openSyncInvDialog () {
      this.syncInvDialogVisible = true
    },
    // 提交创建盘点单
    saveSyncInv () {
      if (!this.syncInvForm.invTaskTitle) {
        // '请输入本次盘点名称'
        this.$message.warning(this.$t('cusEntry.supplement20250211.inputInventoryName'))
        return
      }
      vendorInvApi.syncInv(this.syncInvForm).then(() => {
        // '开始同步库存...'
        this.$message.success(this.$t('outsource.syncingInventory'))
        this.getQuerydata()
        this.syncInvDialogVisible = false
      })
    },
    // 查看历史
    showHistory () {
      const tab = {
        component: vendorInvHistory,
        params: {
        },
        // '查看历史'
        title: this.$t('outsource.viewHistory'),
        name: 'osVendorInvHistory'
      }
      this.$emit('tab-add', tab)
    },
    // 管理界面
    manage (row) {
      const tab = {
        component: vendorInvManage,
        params: {
          row: row
        },
        // '盘点管理'
        title: this.$t('outsource.inventoryManagement'),
        name: 'osVendorInvManage'
      }
      this.$emit('tab-add', tab)
    },
    // 重试(盘点失败时)
    retry (row) {
      vendorInvApi.retrySyncInv(row.osVendorInvTaskId).then(res => {
        // '开始同步库存...'
        this.$message.success(this.$t('outsource.syncingInventory'))
        this.getQuerydata()
      })
    },
    // 结束盘点
    endInv (row) {
      // '是否确认结束本次盘点?'
      this.$confirm(this.$t('outsource.sureEndInventory'), this.$t('components.approvalHead.tips.tip'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('components.common.cancel'),
        type: 'warning'
      }).then(() => {
        vendorInvApi.endInv({
          id: row.osVendorInvTaskId
        }).then(res => {
          // '本次盘点已结束'
          this.$message.success(this.$t('outsource.inventoryEnded'))
          this.getQuerydata()
        })
      })
    }
  }
}
</script>
