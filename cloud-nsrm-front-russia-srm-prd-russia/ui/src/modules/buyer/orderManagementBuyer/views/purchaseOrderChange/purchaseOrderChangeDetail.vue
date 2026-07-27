<template>
  <el-container
    class="the-vendorGreenChannelDetail-detail"
    direction="vertical"
  >
    <el-main>
      <CWorkflowMulti
        ref="workflowMulti"
        v-model="activeTabName"
        :fun-params="workflowParamsInfo"
        :button-config-info="buttonConfigInfo"
        @tab-click="workflowView"
        @workflow-handler="workflowHandler"
        @click-handler="type => saveOrSubmitBill(type)"
        @submit-direct="type => saveOrSubmitBill(type)"
        @confirm="(type, comment) => saveOrSubmitBill(type, comment)"
        @close-tab="cancelBill"
      >
        <el-form
          ref="form"
          :model="form"
          label-width="80px"
          label-position="top"
          class="form-incontainer"
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
              >
                <template #orderNum="{ scope }">
                  <el-input
                    v-model="scope.row.orderNum"
                    :class="`order-num-${scope.$index}`"
                    :disabled="isReadOnly || scope.row.orderDetailStatus === 'CLOSED'"
                    type="number"
                    @blur="setNumColor($event.target, scope.row)"
                  />
                </template>
                <template #planReceiveDate="{ scope }">
                  <el-date-picker
                    v-model="scope.row.planReceiveDate"
                    :class="`plan-date-${scope.$index}`"
                    :disabled="isReadOnly || scope.row.orderDetailStatus === 'CLOSED'"
                    value-format="yyyy-MM-dd"
                    @blur="setDateColor($event.$children[0].$refs.input, scope.row)"
                  />
                </template>
                <!-- 变更前合同关联数量 -->
                <template #originUsedContractQuantity="{ scope }">
                  <el-button type="text" @click="viewPreContract(scope.row)">
                    {{ scope.row.originUsedContractQuantity }}
                  </el-button>
                </template>
                <!-- 变更后合同关联数量 -->
                <template #usedContractQuantity="{ scope }">
                  <el-button type="text" @click="viewAfterContract(scope.row)">
                    {{ scope.row.usedContractQuantity }}
                  </el-button>
                </template>
                <template #comments="{ scope }">
                  <el-input
                    v-model="scope.row.comments"
                    :disabled="isReadOnly || scope.row.orderDetailStatus === 'CLOSED'"
                    maxlength="10"
                  />
                </template>
              </TableView>
            </el-collapse-item>
            <el-collapse-item
              :title="$t('orderMod.orderChangeAttach')"
              name="3"
            >
              <p class="btn_line">
                <el-button
                  :disabled="isReadOnly"
                  type="primary"
                  class="detail-pbtn"
                  @click="addUploadOneOrder"
                >
                  {{ $t('common.add') }}
                </el-button>
              </p>
              <upload-attach
                :readonly="isReadOnly"
                :attachData="orderChangeAttaches"
                :fileInfo="fileInfo"
              />
            </el-collapse-item>
          </el-collapse>
        </el-form>
      </CWorkflowMulti>
    </el-main>

    <!-- 查看合同 -->
    <contract-infor
      ref="viewContractRef"
      :contract-view="contractView"
      :visible.sync="contractViewParams.visible"
      @close="contractViewParams.visible = false"
      @searchData="searchViewContract"
    />

    <!-- 修改关联合同 -->
    <contract-infor
      :contract-view="contractConcat"
      :visible.sync="contractVisible"
      @close="contractVisible = false"
      @handle-change="handleContractChange"
    >
      <template>
        <el-button type="primary" size="mini" @click="addContract">
          {{ $t('common.add') }}
        </el-button>
        <el-button type="primary" size="mini" @click="confirmConcat">
          {{ $t('orderMod.confirmConcat') }}
        </el-button>
      </template>
    </contract-infor>

    <!-- 选择合同 -->
    <contract-infor
      ref="selectContractRef"
      :form="form"
      :concatSelectRow="concatSelectRow"
      :contract-view="contractSelectView"
      :visible.sync="contractSelectVisible"
      @close="contractSelectVisible = false"
      @handleChange="handleSelectChange"
      @rowDblclick="selectContractDbClick"
      @searchData="searchAddData"
    >
      <template>
        <el-button type="primary" size="mini" @click="confirmSelectContract">
          {{ $t('common.confirm') }}
        </el-button>
      </template>
    </contract-infor>
  </el-container>
</template>
<script>
import CToolbar from '@/library/components/c-toolbar'
import OrganizationSelector from '@/library/components/organization-selector'
import QuickSearch from '@/library/components/QuickSearch' // 快速查询组件
import WorkflowCommon from '@/library/mixins/workflow-common'
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
  mixins: [tabTodoWatch, tabTodoMixin, WorkflowCommon],
  data () {
    return {
      concatSelectRow: {},
      contractViewParams: {
        from: '',
        visible: false,
        row: {}
      },
      contractView: {
        row: {},
        params: {},
        title: this.$t('orderMod.viewContract'),
        checkbox: false,
        hiddenOperation: true
      },
      contractSelectView: {
        row: {},
        params: {},
        title: this.$t('orderMod.selectContract'),
        checkbox: true,
        hiddenOperation: true,
        selectContract: true
      },
      contractConcat: {
        row: {},
        params: {},
        title: this.$t('orderMod.relationshipAgreement'),
        checkbox: false,
        hiddenOperation: false
      },
      selectContractConcat: [],
      selectContract: [], // 选择合同
      contractSelectVisible: false,
      contractViewVisible: false,
      contractVisible: false,
      selection: [],
      tableInfor: [],
      attrParams: this.$attrs.params,
      fileInfo: {
        uploadType: 'DEF',
        sourceType: 'WEB_APP',
        fileModular: 'sup',
        fileFunction: 'vendorBiddingManagement',
        fileType: 'images'
      },
      orderChangeAttaches: [],
      queryParams: {},
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
          width: 110,
          showType: 'slot',
          slot: 'orderNum'
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
          showType: 'slot',
          slot: 'planReceiveDate'
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
          width: 150,
          showType: 'slot',
          slot: 'comments'
        },
        {
          prop: 'operation',
          label: () => this.$t('common.operation'),
          width: 120,
          btnStyle: 'text',
          fixed: 'right',
          showType: 'buttons',
          buttons: [
            {
              disabled: (row) => this.isReadOnly || row.orderDetailStatus === 'CLOSED',
              callback: row => this.concatContract(row),
              formattor: () => this.$t('orderMod.editConcatContract')
            }
          ]
        }
      ]
    }
  },
  computed: {
    isOnlyRead () {
      // 兼容页面禁用按钮 by Easion
      return this.attrParams.isOnlyRead
    },
    isReadOnly () {
      return this.attrParams.showType === 'readOnly'
    },
    // 用来指定工作流的业务ID
    workflowBusinessId () {
      return this.form ? this.form.orderChangeId : null
    },
    // 不是审批按钮进来或者是送单号进来则禁用流程tab
    workflowTabDisabled () {
      return this.attrParams.flag !== 'approvalOnly' || this.attrParams.flag === 'approveNumber'
    }
  },
  mounted () {
    this.initPage()
    this.initButtonsInfor()
    this.setFormConfig()
  },
  methods: {
    handleCurrentChange (selection) {
      this.selection = selection
    },
    // 选择合同
    handleSelectChange (selection) {
      this.selectContract = selection
    },
    // 双击选择合同
    selectContractDbClick (row) {
      this.selectContract = [row]
      this.confirmSelectContract()
    },
    // 确认选择合同
    confirmSelectContract () {
      if (this.selectContract.length < 1) return this.$message.warning(this.$t('orderMod.selectNeedConcatRow'))
      const coverContract = this.contractConcat.params.orderChangeContractMappingList.map(item => item.contractMaterialId)
      this.selectContract.forEach(row => {
        if (!coverContract.includes(row.contractMaterialId)) {
          this.contractConcat.params.orderChangeContractMappingList.unshift(row)
        }
      })
      // 新增之后清掉，避免数据污染
      this.selectContract = []
      this.contractSelectVisible = false
    },
    // 获取新增合同列表
    async getContractList (params) {
      const { data } = await this.$http({
        url: '/api-sup-ce/po/orderchange/listContractMaterialByOrderChangeDetail',
        method: 'POST',
        data: {
          'materialId': this.contractConcat.row.materialId,
          'orderChangeDetailId': this.contractConcat.row.orderChangeDetailId,
          'orgId': this.form.orgId,
          'organizationId': this.form.organizationId,
          'receiveAddress': this.form.receiveAddress,
          'vendorId': this.form.vendorId,
          ...params
        },
        loading: true
      })
      return data
    },
    // 新增合同
    async addContract () {
      this.contractSelectVisible = true
      const list = await this.getContractList()
      console.log(list, 'addContract')
      this.contractSelectView.row = this.contractConcat.row
      this.contractSelectView.params = {
        orderContractMappingList: list.orderChangeContractMappingList,
        ...list
      }
    },
    async searchAddData (obj) {
      const list = await this.getContractList(obj)
      this.contractSelectView.params = this.contractSelectView.params = {
        orderContractMappingList: list.orderChangeContractMappingList,
        ...list
      }
    },
    // 获取行关联校验条件
    getCheckConcatObj () {
      return this.contractConcat.params.orderChangeContractMappingList.map((row, i) => {
        let zeros = []
        let checkNumber = []
        let emps = false
        if (row.isFrameworkAgreement === 'N') {
          if (!row.correlatedQuantity) emps = true
          if (row.correlatedQuantity && row.correlatedQuantity < 1) zeros = [true, i + 1]
          if (row.correlatedQuantity >= row.unusedContractQuantity) checkNumber = [true, i + 1]
        }
        return {
          isZero: zeros, // 判断数量0
          isEmpty: emps, // 判空
          isCheckNumber: checkNumber, // 判断关联数量>剩余数量
          correlatedQuantity: row.correlatedQuantity, // 判断关联数量>订单行数量
          row: row
        }
      })
    },
    // 触发确认关联校验：
    // 1.关联数量不能为空
    // 2.第${isZero.join(',')}行：关联数量应当大于0
    // 3.关联数量不能大于剩余数量
    // 4.第${isCheckNumber.join(',')}行：关联数量>剩余数量，请检查！
    // 5.填写关联数量>订单行数量，请检查！
    async isConfirmConcat () {
      const checkArr = this.getCheckConcatObj()

      const contractNumbers = this.contractConcat.params.orderChangeContractMappingList.reduce((r, c) => {
        return c.isFrameworkAgreement === 'N' ? r + c.correlatedQuantity : r
      }, 0)
      console.log(contractNumbers, 'contractNumbers')
      if (contractNumbers > this.concatSelectRow.orderNum) {
        return this.$message.warning(this.$t('orderMod.orderContractNumCheck'))
      }

      const isEmpty = checkArr.some(item => {
        if (item.isFrameworkAgreement === 'N') return item.isEmpty
      })
      if (isEmpty) {
        return this.$message.warning(this.$t('orderMod.contractNumNotEmpty'))
      }

      const isZero = []
      const isCheckNumber = []
      checkArr.map(item => {
        if (item.isCheckNumber.length > 0 && item.isCheckNumber[0]) {
          isCheckNumber.push(item.isCheckNumber[1])
        }
        if (item.isZero.length > 0 && item.isZero[0]) {
          isZero.push(item.isZero[1])
        }
      })
      if (isZero.length > 0) {
        // 第n行关联数量应当大于0
        return this.$message.warning(`${this.$t('orderMod.chapter')}${isZero.join(',')}${this.$t('orderMod.row')}：${this.$t('orderMod.concatNumGreateZero')}`)
      }
      if (isCheckNumber.length > 0) {
        // 第n行关联数量>未关联数量，请检查！
        return this.$message.warning(`${this.$t('orderMod.chapter')}${isCheckNumber.join(',')}${isZero.join(',')}${this.$t('orderMod.row')}：${this.$t('orderMod.checkConcatNum')}`)
      }
    },
    // 确定关联合同
    async confirmConcat () {
      const sign = await this.isConfirmConcat()
      console.log(sign, 'sign')
      if (sign) return false

      await this.$http({
        url: '/api-sup-ce/po/orderchange/saveContractMapping',
        method: 'POST',
        data: {
          orderNum: this.contractConcat.row.orderNum,
          orderChangeId: this.form.orderChangeId,
          orderChangeDetailId: this.contractConcat.row.orderChangeDetailId,
          orderChangeContractMappingList: this.contractConcat.params.orderChangeContractMappingList
        },
        loading: true
      })

      let num = this.contractConcat.params.orderContractMappingList.reduce((r, c) => {
        if (c.isFrameworkAgreement === 'N') {
          return Number(r) + Number(c.correlatedQuantity)
        } else {
          return Number(r)
        }
      }, 0)
      // 设置已关联合同数量
      this.$set(this.contractConcat.row, 'usedContractQuantity', num)
      this.$message.success(this.$t('common.success'))
      this.contractVisible = false
    },
    // 关联合同 - 选择事件
    handleContractChange (selection) {
      this.selectContractConcat = selection
    },
    // 修改关联合同
    async concatContract (row) {
      let url = '/api-sup-ce/po/orderchange/queryContractMappingByOrderDetailId'
      let list = await this.$refs.viewContractRef.queryViewContract(url, { orderChangeDetailId: row.orderChangeDetailId })
      this.contractConcat.row = row
      this.contractConcat.params = {
        orderContractMappingList: list.orderChangeContractMappingList,
        ...list
      }
      this.contractVisible = true
    },
    // 变更前合同信息
    async viewPreContract (row) {
      let url = '/api-sup-ce/po/orderchange/queryOriginContractMappingByOrderDetailId'
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
    // 单位格式化
    unitFormatter (row, column, cellValue, index) {
      return cellValue ? this.$getDictLabel('unit', cellValue) : cellValue
    },
    // 日期格式化
    dateFormatter (row, column, cellValue, index) {
      return cellValue ? parseTime(cellValue, '{y}-{m}-{d}') : ''
    },
    // 表单可编辑字段配置设置
    setFormConfig () {
      let arrs = [
        'orderChangeComments'
      ]
      this.formHead.forEach(item => {
        if (arrs.includes(item.prop)) {
          this.$set(item, 'disabled', this.isReadOnly)
        }
      })
    },
    // 按钮控制
    initButtonsInfor () {
      const isEdit = this.attrParams.flag === 'edit'
      this.buttonConfigInfo.save.view = isEdit
      this.buttonConfigInfo.submit.view = isEdit
      this.buttonConfigInfo.cancel.view = !this.isReadOnly
      this.buttonConfigInfo.close.view = this.isReadOnly
    },
    // 页面加载判断数据来源，判断是否采购点单点击订单变更跳转过来
    initPage () {
      if (this.attrParams.data) {
        this.orderChangeFrom(this.attrParams.data)
      } else {
        const { orderChangeId } = this.attrParams.row
        this.queryDetails(orderChangeId)
      }
    },
    orderChangeFrom (data) {
      const { orderChange, orderChangeDetails, orderChangeAttaches } = data
      this.form = orderChange
      this.orderChangeAttaches = orderChangeAttaches
      this.tableInfor = orderChangeDetails
    },
    async queryDetails (id) {
      const { data } = await this.$http({
        url: '/api-sup-ce/po/orderchange/get',
        method: 'GET',
        params: { id },
        loading: true
      })
      this.form = data.orderChange
      this.orderChangeAttaches = data.orderChangeAttaches
      this.tableInfor = data.orderChangeDetails
      this.setRowDetails(this.tableInfor)
    },
    // 处理订单明细数据加载
    setRowDetails (tableInfor) {
      tableInfor.forEach((row, i) => {
        this.$nextTick(() => {
          // 变更后数量不等于原订单数量标红
          const orderNumInput = document.querySelector(`.order-num-${i} .el-input__inner`)
          this.setNumColor(orderNumInput, row)

          // 变更后要求到货日期不等于原要求到货日期标红
          const planDate = document.querySelector(`.plan-date-${i} .el-input__inner`)
          this.setDateColor(planDate, row)
        })
      })
    },
    // 变更后要求到货日期颜色设置
    setDateColor (target, row) {
      let originPlanReceiveDate = parseTime(row.originPlanReceiveDate, '{y}-{m}-{d}')
      let planReceiveDate = parseTime(row.planReceiveDate, '{y}-{m}-{d}')
      let sign = originPlanReceiveDate !== planReceiveDate
      this.setFontColor(target, sign)
    },
    // 变更后数量颜色设置
    setNumColor (target, row) {
      let sign = row.originOrderNum - 0 !== row.orderNum - 0
      this.setFontColor(target, sign)
    },
    // 根据条件设置字体颜色
    setFontColor (target, sign) {
      if (target) {
        if (sign) {
          target.style.color = '#F25353'
          target.style.fontWeight = 'bold'
        } else {
          target.style.color = ''
          target.style.fontWeight = ''
        }
      }
    },
    addUploadOneOrder () {
      this.orderChangeAttaches.push({
        attachId: '',
        fileuploadId: '',
        attachName: ''
      })
    },
    saveOrSubmitBill (type) {
      if (type === 'SAVE') this.saveBill()
      if (type === 'SUBMIT') this.submitBill(type)
    },
    submitBill (type) {
      let fileArr = this.orderChangeAttaches.filter(item => !!item.fileuploadId)
      this.$http({
        url: '/api-sup-ce/po/orderchange/submitOrderChange',
        method: 'POST',
        data: {
          orderChange: this.form,
          orderChangeAttaches: fileArr,
          orderChangeDetails: this.$refs.orderRef.getTableData()
        }
      }).then(async res => {
        if (!['None', 'Push'].includes(this.workflowParamsInfo.integrationMode)) {
          await this.handlerAfter(type)
        }
        this.cancelBill()
      })
    },
    saveBill () {
      let fileArr = this.orderChangeAttaches.filter(item => !!item.fileuploadId)
      this.$http({
        url: '/api-sup-ce/po/orderchange/addOrUpdateOrderChange',
        method: 'POST',
        data: {
          orderChange: this.form,
          orderChangeAttaches: fileArr,
          orderChangeDetails: this.$refs.orderRef.getTableData()
        }
      }).then(res => {
        this.$message({
          type: 'success',
          message: res.message
        })
        this.queryDetails(res.data)
      })
    },
    cancelBill () {
      this.$emit('tab-remove', this.$attrs.tabName)
      this.__setTabTodo('purchaseOrderChangeList.getQueryData')
    },
    // 指定工作流的业务类型，在定义工作流时指定
    async getWorkflowBusinessType () {
      return 'ORDERCHANGE'
    },
    getCWorkflowRefName () {
      return 'workflowMulti' // 对应CWorkflowMulti标签中的ref
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
