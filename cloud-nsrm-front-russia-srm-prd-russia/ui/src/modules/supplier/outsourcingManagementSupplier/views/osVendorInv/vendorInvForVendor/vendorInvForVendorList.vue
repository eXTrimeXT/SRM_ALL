<!-- 库存盘点:供方协同 -->
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
            :loading="submitInvLoading"
            :disabled="!canSubmit"
            @click="submitInv"
          >
            提交
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
        :source="vendorInvSupApi.latestStockDetailListPage"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
      >
        <!-- 供方确认库存 -->
        <template #vendorConfirmSlot="{ scope }">
          <!--待供方确认、已驳回-->
          <template v-if="['BUYER_REJECT', 'VENDOR_UN_CONFIRM'].includes(scope.row.vendorInvStatus)">
            <el-input
              v-model="scope.row.vendorConfirmInvAmount"
              type="number"
            />
          </template>
          <template v-else>
            {{ scope.row.vendorConfirmInvAmount }}
          </template>
        </template>
        <!-- 差异 -->
        <template #differenceSlot="{ scope }">
          {{
            scope.row.vendorConfirmInvAmount || scope.row.vendorConfirmInvAmount === 0 ?
              (!isNaN(scope.row.vendorConfirmInvAmount) ? Number(scope.row.vendorInvAmount) - Number(scope.row.vendorConfirmInvAmount) : '')
              : ''
          }}
        </template>
      </TableView>
    </el-main>
    <!-- 库存详情列表弹出 -->
    <el-dialog
      title="库存详情"
      :visible.sync="invDetailTableInfo.showDialog"
    >
      <TableView
        :ref="invDetailTableInfo.gridId"
        :table-data="invDetailTableInfo.tableData"
        :table-header="invDetailTableInfo.tableHeader"
        front-paging
        :page-size="15"
        request-method="get"
        :pre-query-data="invDetailTableInfo.queryParams"
        :open-custom-table="true"
        :source="vendorInvSupApi.stockDetail"
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
    </el-dialog>
  </el-container>
</template>

<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import QuickSearch from 'lib@/components/QuickSearch'
import { vendorInvSupApi } from 'mods@/outsourcingManagementSupplier/api'

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
      vendorInvSupApi: vendorInvSupApi,
      formArray: [], // 列表查询参数定义
      tableInfo: { // 列表信息
        gridId: 'osVendorInvForVendorTable',
        tableData: [],
        tableHeader: [],
        pageSize: 15,
        queryParam: {}, // 查询参数
        selectRows: []
      },
      filterParams: {},
      submitInvLoading: false,
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
            width: 150
          },
          {
            label: '委外单号',
            prop: 'materialReqNumber',
            width: 130
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
      }
    }
  },
  computed: {
    canSubmit () {
      if (this.tableInfo.selectRows.length <= 0) {
        return false
      }
      for (let i = 0; i < this.tableInfo.selectRows.length; i++) {
        let row = this.tableInfo.selectRows[i]
        if (row.vendorInvStatus === 'VENDOR_CONFIRM_ED') { // 供方已确认
          return false
        }
      }
      return true
    }
  },
  created () {
    // 列表定义
    this.tableInfo.tableHeader = [
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
      // 状态
      {
        label: '状态',
        prop: 'vendorInvStatus',
        formattor: val => this.$getDictLabel('SC_OS_VENDOR_INV_STATUS', val)
      },
      {// 供方确认库存
        label: '供方确认库存',
        prop: 'vendorConfirmInvAmount',
        width: 140,
        showType: 'slot',
        slot: 'vendorConfirmSlot'
      },
      {
        label: '差异',
        showType: 'slot',
        slot: 'differenceSlot'
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
      {// 操作
        label: () => this.$t('common.operation'),
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
            show: row => row.taskStatus !== 'INV_FINISHED',
            formattor: () => {
              return '库存详情'
            }
          }
        ]
      }
    ]
    this.formArray = [ // 列表查询参数定义
      {// 物料编码
        prop: 'baseMaterialId',
        label: () => this.$t('common.materialCode'),
        type: 'quicksearch',
        showKey: 'materialCode',
        propKey: 'materialId',
        name: 'scc_base_material_item'
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
    ]
    this.getQuerydata()
  },
  methods: {
    // 列表查询
    getQuerydata (v) {
      this.tableInfo.queryParam = v
      this.$nextTick(() => {
        this.$refs[this.tableInfo.gridId].query()
      })
    },
    syncFilterParams (values) {
      this.tableInfo.queryParam = values

      this.filterParams = { ...values }
    },
    // 提交
    submitInv () {
      if (this.tableInfo.selectRows.length <= 0) {
        this.$message.warning('请勾选需要提交的数据')
        return
      }

      let confrimData = []
      this.tableInfo.selectRows.forEach(row => {
        confrimData.push({
          osVendorInvId: row.osVendorInvId,
          vendorConfirmInvAmount: row.vendorConfirmInvAmount
        })
      })

      this.submitInvLoading = true
      vendorInvSupApi.vendorConfirm(confrimData).then(res => {
        this.submitInvLoading = false
        this.$message.success('提交成功')
        this.getQuerydata()
      }).catch(err => {
        this.submitInvLoading = false
      })
    }
  }
}
</script>
