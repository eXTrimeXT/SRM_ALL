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
            创建盘点单
          </AuthorityButton>
          <AuthorityButton
            type="primary"
            @click="showHistory"
          >
            查看历史
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
            重试
          </el-button>
          <el-button
            v-if="scope.row.taskStatus === 'INV_ING'"
            type="text"
            @click="manage(scope.row)"
          >
            管理
          </el-button>
          <el-button
            v-if="scope.row.taskStatus === 'INV_ING'"
            type="text"
            @click="endInv(scope.row)"
          >
            结束盘点
          </el-button>
        </template>
      </TableView>

      <!--创建盘点单 弹窗-->
      <srm-dialog
        v-if="syncInvDialogVisible"
        :visible.sync="syncInvDialogVisible"
        title="创建盘点单"
        size="small"
        append-to-body

        :close-on-click-modal="false"
      >
        <el-row>
          <el-col
            :span="8"
            style="line-height: 32px;"
          >
            本次盘点名称
          </el-col>
          <el-col :span="16">
            <el-input
              v-model="syncInvForm.invTaskTitle"
              placeholder="请输入本次盘点名称"
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
              盘点所有
            </el-radio>
            <el-radio
              v-model="syncInvForm.taskRule"
              label="VENDOR_DIVISION"
            >
              按供应商分工规则
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
          label: '盘点名称'
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
          label: '盘点流水号',
          prop: 'invTaskNo'
        },
        {// 盘点名称
          label: '盘点名称',
          prop: 'invTaskTitle'
        },
        {// 盘点状态
          label: '盘点状态',
          prop: 'taskStatus',
          formattor: val => this.$getDictLabel('SC_OS_VENDOR_INV_TASK_STATUS', val)
        },
        {// 创建人
          label: '创建人',
          prop: 'createdBy'
        },
        {// 创建时间
          label: '创建时间',
          prop: 'creationDate'
        },
        {// 操作
          label: '操作',
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
        this.$message.warning('请输入本次盘点名称')
        return
      }
      vendorInvApi.syncInv(this.syncInvForm).then(() => {
        this.$message.success('开始同步库存...')
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
        title: '查看历史',
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
        title: '盘点管理',
        name: 'osVendorInvManage'
      }
      this.$emit('tab-add', tab)
    },
    // 重试(盘点失败时)
    retry (row) {
      vendorInvApi.retrySyncInv(row.osVendorInvTaskId).then(res => {
        this.$message.success('开始同步库存...')
        this.getQuerydata()
      })
    },
    // 结束盘点
    endInv (row) {
      this.$confirm('是否确认结束本次盘点?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        vendorInvApi.endInv({
          id: row.osVendorInvTaskId
        }).then(res => {
          this.$message.success('本次盘点已结束')
          this.getQuerydata()
        })
      })
    }
  }
}
</script>
