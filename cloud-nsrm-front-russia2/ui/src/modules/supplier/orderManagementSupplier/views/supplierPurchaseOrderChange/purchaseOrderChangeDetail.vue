<template>
  <el-container
    class="the-vendorGreenChannelDetail-detail"
    direction="vertical"
  >
    <el-main>
      <el-form
        ref="form"
        :model="form"
        label-width="80px"
        label-position="top"
        class="form-incontainer"
        :disabled="true"
      >
        <el-collapse
          v-model="activeDims"
          class="tab-form-style"
        >
          <el-collapse-item
            :title="$t('orderMod.buyerOrderSynergy.orderDetailsForm')"
            name="1"
          >
            <form-detail
              ref="formWapRef"
              :form-array="formHead"
              :formData="form"
            />
          </el-collapse-item>
          <!-- 订单明细 -->
          <el-collapse-item
            :title="$t('orderMod.buyerOrderSynergy.orderDetailsList')"
            name="2"
          >
            <TableView
              ref="orderRef"
              :tableInfor="tableInfor"
              :table-header="orderHeader"
              :page-enabled="false"
              :cell-style="cellStyle"
            >
              <!-- 变更前合同关联数量 -->
              <template #originUsedContractQuantity="{ scope }">
                <el-button class="contract-quantity" type="text" @click="viewPreContract(scope.row)">
                  {{ scope.row.originUsedContractQuantity }}
                </el-button>
              </template>
              <!-- 变更后合同关联数量 -->
              <template #usedContractQuantity="{ scope }">
                <el-button class="contract-quantity" type="text" @click="viewAfterContract(scope.row)">
                  {{ scope.row.usedContractQuantity }}
                </el-button>
              </template>
            </TableView>
          </el-collapse-item>
          <el-collapse-item
            :title="$t('orderMod.orderChangeAttach')"
            name="3"
          >
            <upload-attach
              :isOperation="false"
              :readonly="true"
              :attachData="orderChangeAttaches"
              :fileInfo="fileInfo"
            />
          </el-collapse-item>
        </el-collapse>
      </el-form>
      <CToolbar>
        <template slot="right">
          <el-button
            @click="cancelBill"
          >
            {{ this.$t('common.close') }}
          </el-button>
          <el-button
            v-if="!isReadOnly && form.orderChangeStatus == 'WAITING_VENDOR_CONFIRM'"
            type="primary"
            @click="supplierRefuseOrder"
          >
            {{ $t('common.refused') }}
          </el-button>
          <el-button
            v-if="!isReadOnly && form.orderChangeStatus == 'WAITING_VENDOR_CONFIRM'"
            type="primary"
            @click="supplierConfirmOrder"
          >
            {{ $t('orderMod.accept') }}
          </el-button>
        </template>
      </CToolbar>
    </el-main>

    <!-- 查看合同 -->
    <contract-infor
      ref="viewContractRef"
      :contract-view="contractView"
      :visible.sync="contractViewParams.visible"
      @close="contractViewParams.visible = false"
      @searchData="searchViewContract"
    />
  </el-container>
</template>
<script>
import CToolbar from '@/library/components/c-toolbar'
import OrganizationSelector from '@/library/components/organization-selector'
import QuickSearch from '@/library/components/QuickSearch' // 快速查询组件
import { parseTime } from '@/utils'
import { tabTodoMixin, tabTodoWatch } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import formDetail from '@/library/composition/orderManagementBuyer/form-detail'
import uploadAttach from '@/library/composition/orderManagementBuyer/upload-attach'
import contractInfor from '@/library/composition/orderManagementBuyer/contract-infor'

export default {
  name: 'PurchaseOrderChangeDetail',
  components: {
    QuickSearch,
    CToolbar,
    OrganizationSelector,
    uploadAttach,
    formDetail,
    TableView,
    contractInfor
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      contractViewParams: {
        from: '',
        visible: false,
        row: {}
      },
      contractView: {
        row: {},
        params: {},
        vendor: true,
        title: this.$t('orderMod.viewContract'),
        checkbox: false,
        hiddenOperation: true
      },
      tableInfor: [],
      fileInfo: {
        uploadType: 'DEF',
        sourceType: 'WEB_APP',
        fileModular: 'sup',
        fileFunction: 'vendorBiddingManagement',
        fileType: 'images'
      },
      orderChangeAttaches: [],
      queryParams: {},
      tableData: [],
      activeDims: ['1', '2', '3'],
      form: {
        orgId: null,
        orgCode: null,
        orgName: null,
        organizationId: null,
        organizationCode: null,
        organizationName: null,
        orderChangeId: null,
        orderNumber: null,
        orderChangeStatus: '',
        purchaseOrderDate: new Date(),
        ifSupplierConfirm: 'Y',
        orderType: null,
        empUsername: null,
        orderChangeVersion: 0,
        vendorId: null,
        vendorCode: null,
        vendorName: null,
        departmentName: ''
      },
      formHead: [
        {
          prop: 'orderNumber',
          label: () => this.$t('orderMod.buyerOrderSynergy.orderNumber'), // 采购订单编号
          disabled: true
        },
        {
          prop: 'orderChangeNumber',
          label: () => this.$t('orderMod.orderChangeCode'), // 采购订单变更单编号
          disabled: true
        },
        {
          prop: 'orgName',
          label: () => this.$t('oneStopShopping.businessEntity'), // 业务实体
          type: 'OUorganizationSelector',
          disabled: true
        },
        {
          prop: 'organizationName',
          parentId: 'orgId',
          label: () => this.$t('purchaseDemand.invOrg'), // 库存组织
          type: 'INVorganizationSelector',
          disabled: true
        },
        {
          prop: 'purchaseOrderDate',
          label: () => this.$t('oneStopShopping.orderDate'), // 订单日期
          type: 'date',
          disabled: true
        },
        {
          prop: 'demandType',
          label: this.$t('purchaseDemand.demandType'), // 需求类型
          type: 'dict',
          code: 'DEMAND_TYPE',
          disabled: true
        },
        {
          prop: 'orderType',
          label: this.$t('purchaseDemand.purchaseType'), // 采购类型
          type: 'dict',
          code: 'ORDER_TYPE',
          disabled: true
        },
        {
          prop: 'orderChangeStatus',
          label: this.$t('orderMod.orderChangeStatus'), // 变更单状态
          type: 'dict',
          code: 'ORDER_CHANGE_STATUS',
          disabled: true
        },
        {
          prop: 'empUsername',
          label: () => this.$t('orderMod.buyerOrderSynergy.buyerName'), // 采购员
          disabled: true
        },
        {
          prop: 'departmentName',
          label: () => this.$t('oneStopShopping.department'), // 采购部门
          disabled: true
        },
        {
          prop: 'orderChangeVersion',
          label: () => this.$t('orderMod.orderVersion'), // 订单版本号
          disabled: true
        },
        {
          prop: 'vendorName',
          label: () => this.$t('common.vendor'), // 供应商
          type: 'quicksearch',
          disabled: true,
          showKey: 'companyName',
          name: 'scc_sup_company_info_all'
        },
        {
          prop: 'ifSupplierConfirm',
          label: this.$t('oneStopShopping.ifSupplierConfirm'), // 是否供方确认
          type: 'dict',
          code: 'YES_OR_NO',
          disabled: true
        },
        {
          prop: 'orderChangeComments',
          label: () => this.$t('orderMod.orderChangeDesc'), // 订单变更说明
          colLength: 1,
          attrs: {
            type: 'textarea',
            maxlength: 2000,
            showWordLimit: true
          }
        },
        {
          prop: 'refuseReason',
          label: () => this.$t('orderMod.vendorRejectDesc'), // 供方拒绝说明
          disabled: true,
          colLength: 1,
          attrs: {
            type: 'textarea',
            maxlength: 2000,
            showWordLimit: true
          }
        }
      ],
      orderHeader: [
        {
          prop: 'categoryName',
          label: () => this.$t('purchaseDemand.materialCateSub'), // 物料小类
          width: 100
        },
        {
          prop: 'materialCode',
          label: () => this.$t('purchaseDemand.itemCode'), // 物料编码
          width: 100
        },
        {
          prop: 'materialName',
          label: () => this.$t('purchaseDemand.itemName'), // 物料名称
          width: 150
        },
        {
          prop: 'orderDetailStatus',
          label: () => this.$t('orderMod.buyerOrderSynergy.orderDetailStatus'), // 订单行状态
          width: 120,
          formattor: (val) => this.$getDictLabel('OrderDetailStatus', val)
        },
        {
          prop: 'unit',
          label: () => this.$t('purchaseDemand.unitCode'), // 单位
          width: 80,
          formattor: (val) => this.$getDictLabel('unit', val)
        },
        {
          prop: 'requirementQuantity',
          label: () => this.$t('orderMod.buyerOrderSynergy.requirementQuantity'), // 需求数量
          width: 110
        },
        {
          prop: 'deliveryNoticeQuantity',
          label: () => this.$t('orderMod.buyerOrderSynergy.noticeSum'), // 累计通知数量
          width: 140
        },
        {
          prop: 'maxOrderQuantity',
          label: () => this.$t('orderMod.maxOrderQuantity'), // 采购申请可用数量
          width: 150
        },
        {
          prop: 'originOrderNum',
          label: () => this.$t('orderMod.oldOrderNum'), // 原订单数量
          width: 110
        },
        {
          prop: 'orderNum',
          label: () => this.$t('orderMod.orderChangeAfterNum'), // 变更后数量
          width: 110
        },
        {
          prop: 'requirementDate',
          label: () => this.$t('purchaseDemand.requirementDate'), // 需求日期
          width: 120,
          formattor: cellValue => cellValue ? parseTime(cellValue, '{y}-{m}-{d}') : ''
        },
        {
          prop: 'originPlanReceiveDate',
          label: () => this.$t('orderMod.oldPlanReceiveDate'), // 原要求到货日期
          width: 140,
          formattor: cellValue => cellValue ? parseTime(cellValue, '{y}-{m}-{d}') : ''
        },
        {
          prop: 'planReceiveDate',
          label: () => this.$t('orderMod.changeAfterReceiveDate'), // 变更后要求到货日期
          width: 150,
          formattor: cellValue => cellValue ? parseTime(cellValue, '{y}-{m}-{d}') : ''
        },
        {
          prop: 'promiseReceiveDate',
          label: () => this.$t('purchaseDemand.promiseReceiveDate'), // 供方承诺到货日期
          width: 140,
          formattor: cellValue => cellValue ? parseTime(cellValue, '{y}-{m}-{d}') : ''
        },
        {
          prop: 'originUsedContractQuantity',
          label: this.$t('orderMod.originUsedContractQuantity'), // 变更前合同关联数量
          width: 150,
          showType: 'slot',
          slot: 'originUsedContractQuantity'
        },
        {
          prop: 'usedContractQuantity',
          label: this.$t('orderMod.orderChangeUsedContractQuantity'), // 变更后合同关联数量
          width: 150,
          showType: 'slot',
          slot: 'usedContractQuantity'
        },
        {
          prop: 'comments',
          label: () => this.$t('purchaseDemand.comments'), // 明细备注
          width: 150
        }
      ]
    }
  },
  computed: {
    isReadOnly () {
      return this.$attrs.params.flag === 'readOnly'
    }
  },
  mounted () {
    this.queryDetails()
  },
  methods: {
    // 设置只读状态下也可点击预览
    setPreviewRead () {
      this.$nextTick(() => {
        const doms = document.querySelectorAll('.contract-quantity')
        doms.forEach(item => {
          item.removeAttribute('disabled')
          item.classList.remove('is-disabled')
        })
      })
    },
    // 获取调用查看关联合同弹窗接口及参数
    getQueryObj (obj) {
      let map = new Map([
        ['preOrderChange', { // 变更前
          listName: 'originOrderChangeContractMappingList',
          url: '/api-sup-ce/po/orderchange/queryOriginContractMappingByOrderDetailId',
          params: {
            orderChangeDetailId: this.contractViewParams.row.orderChangeDetailId,
            ...obj
          }
        }
        ],
        ['afterOrderChange', { // 变更后
          listName: 'orderChangeContractMappingList',
          url: '/api-sup-ce/po/orderchange/queryContractMappingByOrderDetailId',
          params: {
            orderChangeDetailId: this.contractViewParams.row.orderChangeDetailId,
            ...obj
          }
        }
        ]
      ])
      return map.get(this.contractViewParams.from) || {}
    },
    // 明细查看合同 - 搜索
    async searchViewContract (obj) {
      const data = this.getQueryObj(obj)
      let list = await this.$refs.viewContractRef.queryViewContract(data.url, data.params)
      this.contractView.params = {
        orderContractMappingList: list[data.listName],
        ...list
      }
    },
    // 变更前合同信息
    async viewPreContract (row) {
      let url = '/api-sup-ce/po/orderchange/queryOriginContractMappingByOrderDetailId'
      console.log(this.$refs.viewContractRef, 'this.$refs.viewContractRef')
      let list = await this.$refs.viewContractRef.queryViewContract(url, { orderChangeDetailId: row.orderChangeDetailId })
      this.contractView.row = row
      this.contractView.params = {
        orderContractMappingList: list.originOrderChangeContractMappingList,
        ...list
      }
      console.log(this.contractView.params, 'viewPreContract')
      this.contractViewParams = { from: 'preOrderChange', visible: true, row }
    },
    // 变更后合同信息
    async viewAfterContract (row) {
      let url = '/api-sup-ce/po/orderchange/queryContractMappingByOrderDetailId'
      let list = await this.$refs.viewContractRef.queryViewContract(url, { orderChangeDetailId: row.orderChangeDetailId })
      this.contractView.row = row
      this.contractView.params = {
        orderContractMappingList: list.orderChangeContractMappingList,
        ...list
      }
      this.contractViewParams = { from: 'afterOrderChange', visible: true, row }
    },
    cellStyle ({ row, column, rowIndex, columnIndex }) {
      let isNumEqual = row.originOrderNum - 0 !== row.orderNum - 0
      if (isNumEqual && column.property === 'orderNum') {
        return 'color: red;font-weight: bold;'
      }

      let originPlanReceiveDate = parseTime(row.originPlanReceiveDate, '{y}-{m}-{d}', true)
      let planReceiveDate = parseTime(row.planReceiveDate, '{y}-{m}-{d}', true)
      let isDateEqual = originPlanReceiveDate !== planReceiveDate
      if (isDateEqual && column.property === 'planReceiveDate') {
        return 'color: red;font-weight: bold;'
      }
    },
    // 单位格式化
    unitFormatter (row, column, cellValue, index) {
      return cellValue ? this.$getDictLabel('unit', cellValue) : cellValue
    },
    // 日期格式化
    dateFormatter (row, column, cellValue, index) {
      return cellValue ? parseTime(cellValue, '{y}-{m}-{d}') : ''
    },
    // 接受
    supplierConfirmOrder () {
      this.$http({
        url: '/api-sup-ce/sup/orderchange/acceptOrderChange',
        method: 'GET',
        params: {
          id: this.form.orderChangeId
        },
        loading: true
      }).then(res => {
        this.$message({
          type: 'success',
          message: res.message
        })
        this.cancelBill()
      })
    },
    // 拒绝
    async supplierRefuseOrder () {
      // 请输入拒绝原因
      let { value } = await this.$prompt(
        '',
        this.$t('oneStopShopping.refusedReason'),
        {
          confirmButtonText: this.$t('common.confirm'),
          cancelButtonText: this.$t('common.cancel')
        }
      )

      this.$http({
        url: '/api-sup-ce/sup/orderchange/refuseOrderChange',
        method: 'POST',
        data: {
          id: this.form.orderChangeId,
          refuseReason: value
        },
        loading: true
      }).then(res => {
        this.cancelBill()
      })
    },
    async queryDetails () {
      const { orderChangeId } = this.$attrs.params.row
      const { code, data } = await this.$http({
        url: '/api-sup-ce/sup/orderchange/get',
        method: 'GET',
        params: { id: orderChangeId },
        loading: true
      })

      if (code === '0') {
        this.form = data.orderChange
        this.orderChangeAttaches = data.orderChangeAttaches
        this.tableInfor = data.orderChangeDetails
        this.setPreviewRead()
      }
    },
    saveOrSubmitBill (type) {
      if (type === 'SAVE') this.saveBill()
      if (type === 'SUBMIT') this.submitBill(type)
    },
    cancelBill () {
      this.$emit('tab-remove', this.$attrs.tabName)
      this.__setTabTodo('purchaseOrderChangeList.getQueryData')
    }
  }
}
</script>
<style scoped lang="scss">
.the-vendorGreenChannelDetail-detail {
  .el-table .el-date-editor {
    width: 135px;
  }
  .btn_line {
    margin: 0 0 10px 0;
  }
}
</style>
