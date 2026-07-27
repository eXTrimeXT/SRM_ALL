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
            {{ $t('common.submit') }}
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
      :title="$t('outsource.stockDetails')"
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
          <!-- <template v-if="scope.row.detailType === '委外领料' || scope.row.detailType === '退货'"> -->
          <template v-if="scope.row.detailType === $t('cusEntry.supplement20250211.outsourcedMaterialPickup') || scope.row.detailType === $t('cusEntry.supplement20250211.returnToVendor')">
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
            label: this.$t('dataConfMod.businessType'),
            prop: 'detailType',
            width: 110
          },
          {
            label: this.$t('orderMod.orderNumber'),
            prop: 'orderNumber',
            width: 150
          },
          {
            label: this.$t('cusEntry.supplement20250211.materialReqNumber'), // 委外单号
            prop: 'materialReqNumber',
            width: 130
          },
          {
            label: this.$t('outsource.changeQuantity'),
            prop: 'amount',
            showType: 'slot',
            slot: 'amountSlot',
            width: 100
          },
          {
            label: this.$t('outsource.supplierInventory'),
            prop: 'vendorInvAmount',
            width: 100
          },
          {
            label: this.$t('outsource.executionTime'),
            prop: 'executeTime',
            width: 150,
            dataType: 'dateTime'
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
        label: this.$t('components.organization.ORG'),
        prop: 'orgName',
        width: 160
      },
      {// 库存组织
        label: this.$t('components.organization.INV'),
        prop: 'organizationName',
        width: 160
      },
      {// 委外组件编码
        label: this.$t('outsourcingBomNew.materialCode'),
        prop: 'baseMaterialCode',
        width: 160
      },
      {// 委外组件名称
        label: this.$t('outsourcingBomNew.materialName'),
        prop: 'baseMaterialName',
        width: 160
      },
      {// 供应商编码
        label: this.$t('common.vendorCode'),
        prop: 'vendorCode',
        width: 160
      },
      {// 供应商名称
        label: this.$t('common.companyName'),
        prop: 'vendorName',
        width: 160
      },
      {// 供方库存
        label: this.$t('outsource.supplierInventory'),
        prop: 'vendorInvAmount',
        width: 160
      },
      // 状态
      {
        label: this.$t('components.stratProcess.headers.docStatusValue'),
        prop: 'vendorInvStatus',
        formattor: val => this.$getDictLabel('SC_OS_VENDOR_INV_STATUS', val)
      },
      {// 供方确认库存
        label: this.$t('outsource.supplierConfirmsInventory'),
        prop: 'vendorConfirmInvAmount',
        width: 140,
        showType: 'slot',
        slot: 'vendorConfirmSlot'
      },
      {
        label: this.$t('outsource.difference'),
        showType: 'slot',
        slot: 'differenceSlot'
      },
      {// 盘点结果
        label: this.$t('outsource.inventoryResults'),
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
              return this.$t('outsource.stockDetails')
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
        label: this.$t('components.organization.ORG'),
        type: 'OUorganizationSelector'
      },
      {
        prop: 'invResult',
        label: this.$t('outsource.inventoryResults'),
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
        // 请勾选需要提交的数据
        this.$message.warning(this.$t('cusEntry.supplement20250211.checkDataToSubmit'))
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
        this.$message.success(this.$t('common.successSubmit'))
        this.getQuerydata()
      }).catch(err => {
        this.submitInvLoading = false
      })
    }
  }
}
</script>
