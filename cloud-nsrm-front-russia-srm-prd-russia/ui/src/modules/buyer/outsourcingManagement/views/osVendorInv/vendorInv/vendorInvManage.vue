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
      >
        <template #taskTitleSlot>
          <el-input
            v-model.trim="$attrs.params.row.invTaskTitle"
            type="text"
            :disabled="true"
          />
        </template>
      </FormWrapper>

      <!-- 按钮域 -->
      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <AuthorityButton
            type="primary"
            :disabled="!canSendVendor"
            @click="sendVendor"
          >
            发送供应商
          </AuthorityButton>
          <AuthorityButton
            type="primary"
            @click="endInv"
          >
            结束盘点
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
        :checkbox="true"
        :check-change="(x) => tableInfo.selectRows = x"
        :pre-query-data="tableInfo.queryParam"
        :source="vendorInvApi.latestStockDetailListPage"
        :open-custom-table="true"
        style="width: 100%"
      />
    </el-main>

    <!-- 库存详情列表弹出 -->
    <srm-dialog
      title="库存详情"
      size="large"
      :visible.sync="invDetailTableInfo.showDialog"
      append-to-body
      destroy-on-close
    >
      <TableView
        :ref="invDetailTableInfo.gridId"
        :table-data="invDetailTableInfo.tableData"
        :table-header="invDetailTableInfo.tableHeader"
        front-paging
        :page-size="15"
        table-max-height="400px"
        request-method="get"
        :pre-query-data="invDetailTableInfo.queryParams"
        :open-custom-table="false"
        :source="vendorInvApi.stockDetail"
        style="height: 400px"
      >
        <template #amountSlot="{ scope }">
          <template v-if="scope.row.detailType === '委外领料' || scope.row.detailType === '退货'">
            <span style="color: red;"><strong>{{ '+ ' + scope.row.amount }}</strong></span>
          </template>
          <template v-else>
            <span style="color: green;"><strong>{{ '- ' + scope.row.amount }}</strong></span>
          </template>
        </template>
      </TableView>
    </srm-dialog>
  </el-container>
</template>

<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import QuickSearch from 'lib@/components/QuickSearch'
import { vendorInvApi } from 'modb@/outsourcingManagement/api'
export default {
  name: 'OsVendorInvManageList',
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
          label: '盘点名称',
          type: 'slot',
          slot: 'taskTitleSlot'
        },
        {// 物料编码
          prop: 'baseMaterialId',
          label: () => this.$t('common.materialCode'),
          type: 'quicksearch',
          showKey: 'baseMaterialCode',
          propKey: 'baseMaterialId',
          name: 'scc_price_material'
        },
        {
          prop: 'vendorId',
          label: '供应商',
          type: 'quicksearch',
          showKey: 'companyName',
          propKey: 'companyId',
          name: 'scc_sup_company_info'
        },
        {
          prop: 'orgId',
          label: '业务实体',
          type: 'OUorganizationSelector'
        },
        {
          prop: 'invResult',
          label: '盘点结果',
          type: 'dict',
          code: 'SC_OS_VENDOR_INV_RESULT'

        }
      ],
      tableInfo: { // 列表信息
        gridId: 'osVendorInvManageTable',
        tableData: [],
        tableHeader: [],
        pageSize: 15,
        queryParam: {}, // 查询参数
        selectRows: []
      },
      filterParams: {},
      invDetailTableInfo: { // 库存详情列表
        showDialog: false,
        gridId: 'invDetailTableGrid',
        tableData: [],
        tableHeader: [
          {
            label: '业务类型',
            prop: 'detailType',
            width: 110
          },
          {
            label: '采购订单号',
            prop: 'orderNumber',
            minWidth: 150
          },
          {
            label: '委外单号',
            prop: 'materialReqNumber',
            minWidth: 130
          },
          {
            label: '变动数量',
            prop: 'amount',
            showType: 'slot',
            slot: 'amountSlot',
            width: 100
          },
          {
            label: '供方库存',
            prop: 'vendorInvAmount',
            width: 100
          },
          {
            label: '执行时间',
            prop: 'executeTime',
            width: 150
          }
        ],
        queryParams: {
          organizationId: '', // 采购组织ID
          baseMaterialId: '', // 物料散件ID
          vendorId: '' // 供应商ID
        }
      },
      attrsParams: this.$attrs.params
    }
  },
  computed: {
    canSendVendor () {
      if (this.tableInfo.selectRows.length <= 0) {
        return false
      }
      for (let i = 0; i < this.tableInfo.selectRows.length; i++) {
        let row = this.tableInfo.selectRows[i]
        if (row.vendorInvStatus === 'VENDOR_UN_CONFIRM') { // 待供方确认
          return false
        }
      }
      return true
    }
  },
  created () {
    // 列表定义
    this.tableInfo.tableHeader = [
      {// 状态
        label: '状态',
        prop: 'vendorInvStatus',
        formattor: val => this.$getDictLabel('SC_OS_VENDOR_INV_STATUS', val)
      },
      {// 业务实体
        label: '业务实体',
        prop: 'orgName',
        width: 160
      },
      {// 库存组织
        label: '库存组织',
        prop: 'organizationName',
        width: 160
      },
      {// 委外组件编码
        label: '委外组件编码',
        prop: 'baseMaterialCode',
        width: 160
      },
      {// 委外组件名称
        label: '委外组件名称',
        prop: 'baseMaterialName',
        width: 160
      },
      {// 供应商编码
        label: '供应商编码',
        prop: 'vendorCode',
        width: 160
      },
      {// 供应商名称
        label: '供应商名称',
        prop: 'vendorName',
        width: 160
      },
      {// 供方库存
        label: '供方库存',
        prop: 'vendorInvAmount',
        width: 160
      },
      {// 供方确认库存
        label: '供方确认库存',
        prop: 'vendorConfirmInvAmount',
        width: 160
      },
      {
        label: '差异',
        formattor: (val, row) => row.vendorInvAmount - row.vendorConfirmInvAmount
      },
      {// 盘点结果
        label: '盘点结果',
        prop: 'invResult',
        width: 160,
        formattor: val => this.$getDictLabel('SC_OS_VENDOR_INV_RESULT', val)
      },
      // 驳回原因
      {
        label: this.$t('bidMod.rejectReason1'),
        prop: 'rejectReason',
        width: 130
      },
      // 操作
      {
        label: this.$t('common.operation'),
        prop: 'operation',
        showType: 'buttons',
        btnStyle: 'text',
        fixed: 'right',
        width: 130,
        buttons: [
          {// 库存详情
            callback: row => {
              this.invDetailTableInfo.queryParams.organizationId = row.organizationId
              this.invDetailTableInfo.queryParams.baseMaterialId = row.baseMaterialId
              this.invDetailTableInfo.queryParams.vendorId = row.vendorId

              this.invDetailTableInfo.showDialog = true
              this.$nextTick(() => {
                this.$refs[this.invDetailTableInfo.gridId].query()
              })
            },
            formattor: () => '库存详情'
          },
          // 驳回
          {
            // 状态：供方已确认
            show: row => row.vendorInvStatus === 'VENDOR_CONFIRM_ED',
            formattor: () => this.$t('common.toRefuse'),
            callback: row => this.toRefuse(row)
          }
        ]
      }
    ]
    this.getQuerydata()
  },
  methods: {
    // 列表查询
    getQuerydata (v) {
      this.tableInfo.queryParam = {
        ...(v || {}),
        osVendorInvTaskId: this.attrsParams.row.osVendorInvTaskId
      }
      this.$nextTick(() => {
        this.$refs[this.tableInfo.gridId].query()
      })
    },
    syncFilterParams (values) {
      this.tableInfo.queryParam = values

      this.filterParams = { ...values }
    },
    // 发送供应商
    sendVendor () {
      let vendorInvIds = []
      this.tableInfo.selectRows.forEach(row => vendorInvIds.push(row.osVendorInvId))

      vendorInvApi.sendInvToVendor(vendorInvIds).then(res => {
        this.$message.success('发送成功')
        this.getQuerydata()
      })
    },
    // 结束盘点
    endInv () {
      this.$confirm('是否确认结束盘点？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        vendorInvApi.endInv({
          id: this.attrsParams.row.osVendorInvTaskId
        }).then(res => {
          this.$message.success('盘点已结束')
          this.cancelBill()
        })
      })
    },

    /* 驳回 */
    toRefuse (row) {
      this.$prompt(this.$t('bidMod.rejectReason1'), this.$t('bidMod.rejectReason1'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        inputValidator: value => !(!value || value.length === 0 || value.length > 300),
        inputErrorMessage: '驳回原因必填且长度不能超过300字符'
      }).then(({ value }) => {
        vendorInvApi.rejectInv({
          vendorInvId: row.osVendorInvId,
          rejectReason: value
        }).then(() => {
          this.$message.success(this.$t('bidMod.successRefuse'))
          this.getQuerydata()
        })
      })
    },

    // 关闭页签
    cancelBill () {
      const { flag, row } = this.$attrs.params
      this.$emit('tab-remove', 'osVendorInvManage')
      this.__setTabTodo('osVendorInvList.getQuerydata')
    }
  }
}
</script>
