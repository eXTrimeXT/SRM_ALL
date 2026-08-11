<template>
  <el-container class="flex-container the_main_po_list" direction="vertical">
    <el-main>
      <el-form
        ref="detailRef"
        :rules="rules"
        :model="form"
        label-width="80px"
        label-position="top"
        class="form-container2"
      >
        <el-collapse v-model="activeDims" class="tab-form-style">
          <!-- 采购订单单据 -->
          <el-collapse-item :title="$t('orderMod.buyerOrderSynergy.orderDetailsForm')" name="1">
            <BaseForm
              ref="formRef"
              :form-items="formItems"
              :merge-form.sync="form"
              form-name="form"
              :wrapper-col="{ span: 6, gutter: 27 }"
            />
          </el-collapse-item>

          <!-- 付款信息 -->
          <el-collapse-item :title="$t('paymentType.payment')" name="2">
            <p class="btn_line">
              <el-button
                :disabled="isReadOnly"
                type="primary"
                class="detail-pbtn"
                @click="addOnePayment"
              >
                {{ $t('common.add') }}
              </el-button>
            </p>
            <paymen-infor :tableData="paymentInfor" :isReadOnly="isReadOnly" />
          </el-collapse-item>

          <!-- 订单明细 -->
          <el-collapse-item :title="$t('orderMod.buyerOrderSynergy.orderDetailsList')" name="3">
            <div class="btn_line">
              <!-- 采购申请新增 -->
              <el-button
                type="primary"
                :disabled="isReadOnly || ifsourceSystem === 'DEMAND'"
                class="detail-pbtn"
                @click="openMaterialList"
              >
                {{ $t('purchaseDemand.addPurchaseApp') }}
              </el-button>
              <!-- 物料维护新增 -->
              <el-button
                type="primary"
                :disabled="isReadOnly"
                class="detail-pbtn"
                @click="openDialog"
              >
                {{ $t('purchaseDemand.addMaterial') }}
              </el-button>
            </div>

            <BaseInfor
              ref="detailInforRef"
              style="height: 140px;"
              border
              :data-source="detailModel"
              :columns="detailColumn"
              columns-name="detailColumn"
              :initialize="false"
              :editable="false"
              :index="false"
              :selection="$attrs.params.row.orderStatus === 'APPROVED'"
              @asyncGetRealDataSource="asyncGetRealDataSource"
            >
              <!-- 物料名称 -->
              <template #materialName="{scope}">
                <el-input
                  v-model="scope.row.materialName"
                  :disabled="
                    !(
                      ['20', '40', '60'].includes(scope.row.bigCategoryCode) ||
                      (scope.row.bigCategoryCode === '70' &&
                      (scope.row.materialCode.startsWith(61) ||
                      scope.row.materialCode.startsWith(78)))
                    ) || isReadOnly
                  "
                />
              </template>
              <!-- 订单数量 -->
              <template #orderNum="{scope}">
                <el-input
                  v-model="scope.row.orderNum"
                  v-input-format="{ type: 'float' }"
                  :disabled="isReadOnly && form.orderStatus !== 'REFUSED'"
                  @change="setRowAmount(scope.row)"
                />
              </template>
              <template #ceeaPlanReceiveDate="{scope}">
                <el-date-picker
                  v-model="scope.row.ceeaPlanReceiveDate"
                  :disabled="isReadOnly && form.orderStatus !== 'REFUSED'"
                  :picker-options="endTiumePickerOptions"
                  type="date"
                  :format="$formatDatePicker"
                  value-format="yyyy-MM-dd"
                  @change="datePickerChange(scope.row.ceeaPlanReceiveDate, scope.row)"
                />
              </template>
              <template #comments="{scope}">
                <el-input
                  v-model="scope.row.comments"
                  maxlength="80"
                  :disabled="isReadOnly"
                  @input="onInputBlurVendor(scope.row)"
                />
              </template>
              <template #ceeaUnitTaxPrice="{scope}">
                <el-input
                  v-model="scope.row.ceeaUnitTaxPrice"
                  :disabled="ifsourceSystem === 'DEMAND' || isReadOnly"
                  @change="setRowAmount(scope.row)"
                />
              </template>
              <template #currencyName="{scope}">
                <QuickSearch
                  :disabled="ifsourceSystem === 'DEMAND' || isReadOnly"
                  :show-input="scope.row.currencyName"
                  show-key="currencyName"
                  :scope-data="scope.row"
                  name="scc_base_purchase_currency_info"
                  @close-quicksearch="getCurrencyObj"
                />
              </template>
              <template #ceeaTaxKey="{scope}">
                <DictSelect
                  v-model="scope.row.ceeaTaxKey"
                  :disabled="ifsourceSystem === 'DEMAND' || isReadOnly"
                  code="tax"
                  @change-value="
                    (value, dictItem) => taxRateChangeHandel(value, dictItem, scope.row)
                  "
                />
              </template>
              <!-- 合同已关联数量 -->
              <!-- <template #usedContractQuantity="{scope}">
                <el-button
                  type="text"
                  @click="viewContract(scope.row)"
                >
                  {{ scope.row.usedContractQuantity || 0 }}
                </el-button>
              </template> -->
              <!-- 合同信息 -->
              <template #contractInfor="{scope}">
                <el-button
                  type="text"
                  @click="viewContract(scope.row)"
                >
                  {{ $t('vendorMod.check') }}
                </el-button>
              </template>
              <template #operation="{scope}">
                <el-button
                  type="text"
                  :disabled="isReadOnly"
                  @click="concatContract(scope.row)"
                >
                  {{ $t('orderMod.relationshipAgreement') }}
                </el-button>
                <el-button
                  type="text"
                  :disabled="isReadOnly"
                  @click="deleteDetials(scope.$index)"
                >
                  {{ $t('common.delete') }}
                </el-button>
              </template>
            </BaseInfor>

            <!-- 物料明细选择（采购申请）-->
            <purchase-detail
              :dialogFormVisible.sync="dialogFormVisible"
              :form="form"
              :filterForm="filterForm"
              @addNewOne="addNewOne"
              @close="dialogFormVisible = false"
            />
            <!-- 物料新增 - 物料明细选择-->
            <material-detail
              :dialogVisible.sync="dialogVisible"
              :form="form"
              :queryForm="queryForm"
              @addOneContent="addOneContent"
              @close="dialogVisible = false"
            />
          </el-collapse-item>

          <!-- 订单附件信息 -->
          <el-collapse-item :title="$t('orderMod.orderAttachInfo')" name="4">
            <p class="btn_line">
              <el-button
                type="primary"
                :disabled="isReadOnly"
                class="detail-pbtn"
                @click="addUploadOneOrder"
              >
                {{ $t('common.add') }}
              </el-button>
            </p>
            <upload-attach :isOperation="false" :readonly="true" :attachData="orderFileList" :fileInfo="fileInfo" />
          </el-collapse-item>

          <!-- 采购订单变更记录 -->
          <el-collapse-item :title="$t('orderMod.orderChangeReport')" name="5">
            <TableView
              ref="reoprtRef"
              style="height: 200px;"
              :table-header="reportHeader"
              :pre-query-data="{ orderId: $attrs.params.row.orderId }"
              url="/api-sup-ce/po/orderchange/getOrderChangeHistoryList"
            >
              <template #preChangeInfor="{ scope }">
                <el-button
                  type="text"
                  :class="`report-view_${scope.$index}`"
                  @click="viewPreContract(scope.row)"
                >
                  {{ $t('common.view') }}
                </el-button>
              </template>
              <template #afterChangeInfor="{ scope }">
                <el-button
                  :class="`report-view_${scope.$index}`"
                  type="text"
                  @click="viewAfterContract(scope.row)"
                >
                  {{ $t('common.view') }}
                </el-button>
              </template>
            </TableView>
          </el-collapse-item>
        </el-collapse>
      </el-form>

      <CToolbar>
        <template slot="right">
          <el-button
            @click="cancelBill"
          >
            {{ $t('common.close') }}
          </el-button>
          <!-- <el-button
            v-if="['APPROVED', 'ACCEPT'].includes(form.orderStatus)"
            type="primary"
            @click="printBill"
          >
            {{ $t('route.pdfPrint') }}
          </el-button> -->
        </template>
      </CToolbar>
      <!-- 查看合同 -->
      <contract-infor
        ref="viewContractRef"
        :contract-view="contractView"
        :visible.sync="contractViewParams.visible"
        @close="contractViewParams.visible = false"
        @searchData="searchViewContract"
      />

      <!-- 关联合同 -->
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
    </el-main>
  </el-container>
</template>
<script>
import { parseTime } from '@/utils'
import { tabTodoMixin } from '@/utils/mixins'
import BaseInfor from 'lib@/components/BaseTable/baseTable'
import OrganizationSelector from 'lib@/components/organization-selector'
import QuickSearch from 'lib@/components/QuickSearch' // 快速查询组件
import TableView from 'lib@/components/Table/TableView'
import { downloadFileLink } from 'lib@/utils/file'
import formDetail from '@/library/composition/orderManagementBuyer/form-detail'
import paymenInfor from '@/library/composition/orderManagementBuyer/paymen-infor'
import uploadAttach from '@/library/composition/orderManagementBuyer/upload-attach'
import BaseForm from 'lib@/components/BaseForm'
import { mapGetters } from 'vuex'
import CToolbar from 'lib@/components/c-toolbar'
import { formItems, detailColumn, reportHeader } from 'modb@/orderManagementBuyer/views/buyerPurchaseOrder/data/detail'
import purchaseDetail from 'modb@/orderManagementBuyer/views/buyerPurchaseOrder/components/purchaseDetailSelect'
import materialDetail from 'modb@/orderManagementBuyer/views/buyerPurchaseOrder/components/materialDetailSelect'
import contractInfor from '@/library/composition/orderManagementBuyer/contract-infor'
import { purchaseOrderApi } from 'mods@/orderManagementSupplier/api'

export default {
  name: 'PurchaseOrderDetail',
  components: {
    CToolbar,
    QuickSearch,
    OrganizationSelector,
    uploadAttach,
    formDetail,
    paymenInfor,
    TableView,
    BaseForm,
    BaseInfor,
    purchaseDetail,
    materialDetail,
    contractInfor
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      concatSelectRow: {}, // 明细行点击关联合同选择数据
      selectContractConcat: [],
      contractViewParams: {
        from: '',
        visible: false,
        row: {}
      },
      contractView: { // 查看合同
        row: {},
        params: {},
        title: this.$t('orderMod.viewContract'),
        checkbox: false,
        hiddenOperation: true,
        vendor: true
      },
      contractVisible: false,
      contractSelectVisible: false,
      contractSelectView: {
        row: {},
        params: {},
        vendor: false,
        title: this.$t('orderMod.selectContract'),
        checkbox: true,
        hiddenOperation: true,
        selectContract: true
      },
      contractConcat: { // 关联合同
        row: {},
        params: {},
        title: this.$t('orderMod.relationshipAgreement'),
        vendor: false,
        checkbox: false,
        hiddenOperation: false
      },
      closeMethods: 'line',
      closeVisibleDialog: false,
      closedCause: '',
      selectOrderDetail: [],
      dialogVisible: false,
      dialogFormVisible: false,
      queryForm: {
        materialCode: '',
        materialName: '',
        organizationId: '',
        orgId: '',
        bigCategoryName: '',
        bigCategoryId: null,
        middleCategoryName: '',
        middleCategoryId: null,
        categoryId: null,
        categoryName: '',
        inputLevel: '',
        selectLevel: '3',
        isManual: 'N',
        ifSample: ''
      },
      filterForm: {
        materialCode: null,
        materialName: null,
        orgName: null,
        categoryCode: null,
        organizationId: '',
        requirementHeadNum: null,
        startDate: null,
        endDate: null,
        vendorId: null,
        orgId: '', // 业务实体id  必传
        purchaseType: null, // 采购类型 必传
        isManual: 'N'
      },
      detailColumn: detailColumn(this),
      formItems: formItems(this),
      reportHeader: reportHeader(this),
      orderFileList: [],
      paymentInfor: [],
      maxConversionRate: '',
      minConversionRate: '',
      queryParams: {},
      activeDims: ['1', '2', '3', '4', '5'],
      ifsourceSystem: '',
      rateName: '',
      conversionRate: '',
      fileInfo: {
        uploadType: 'DEF',
        sourceType: 'WEB_APP',
        fileModular: 'sup',
        fileFunction: 'vendorBiddingManagement',
        fileType: 'images'
      },
      endTiumePickerOptions: {
        disabledDate: time => {
          const start = new Date()
          return time.getTime() < start.getTime() - 24 * 60 * 60 * 1000
        }
      },
      detailModelReal: [],
      detailModel: [],
      form: {
        budgetManagementId: '',
        budgetManagementNum: '',
        // purchaseProject: '',
        ceeaOrgId: '',
        ceeaOrgCode: '',
        ceeaOrgName: '',
        orderId: '',
        organizationId: '',
        organizationCode: '',
        organizationName: '',
        orderNumber: null,
        orderStatus: 'DRAFT',
        ceeaPurchaseOrderDate: new Date(),
        ceeaIfSupplierConfirm: '',
        orderType: '',
        // ceeaIfPowerStationBusiness: 'N',
        ifSample: 'N',
        buyerName: null,
        orderChangeVersion: 0,
        comments: null,
        ceeaOpinion: null,
        receiveAddress: '',
        receiveContact: null,
        receiveTelephone: null,
        ceeaReceiveOrderAddress: null,
        ceeaReceiveOrderContact: null,
        ceeaReceiveOrderTelephone: null,
        vendorId: null,
        vendorCode: '',
        vendorName: '',
        contactName: null,
        ceeaCostType: '',
        ceeaTotalNum: 0, // 合计数量
        ceeaTaxAmount: 0, // 合计金额含税
        ceeaNoTaxAmount: 0, // 合计金额不含税
        isManual: 'Y' // 是否手动创建
      },
      rules: {
        ceeaUnitTaxPrice: [{ required: true }],
        ceeaPromiseReceiveDate: [{ required: true }],
        currencyName: [{ required: true }],
        ceeaTaxKey: [{ required: true }],
        ceeaOrgId: [{ required: true, message: this.$t('purchaseDemand.orgIdTips') }],
        organizationId: [
          {
            required: true,
            message: this.$t('purchaseDemand.organizationIdTips')
          }
        ],
        orderType: [
          {
            required: true,
            message: this.$t('logisticsMod.msgPurchaseApply[2]')
          }
        ],
        vendorName: [{ required: true, message: this.$t('vendorMod.msgVendor') }],
        receiveAddress: [
          {
            required: true,
            message: this.$t('orderMod.buyerOrderSynergy.msgReceiveAddress')
          }
        ],
        ceeaPlanReceiveDate: [{ required: true, message: this.$t('orderMod.msgOrder[0]') }],
        ceeaReceiveOrderAddress: [{ required: true, message: this.$t('orderMod.msgOrder[1]') }],
        ceeaIfSupplierConfirm: [{ required: true, message: this.$t('orderMod.msgOrder[4]') }]
      }
    }
  },
  computed: {
    ...mapGetters(['userInfo']),
    // 只读判断
    isOnlyRead () {
      // 兼容页面禁用按钮 by Easion
      return this.$attrs.params.isOnlyRead
    },
    isReadOnly () {
      return this.$attrs.params.showType === 'readOnly'
    }
  },
  created () {
    this.initDetail()
  },
  updated () {
    if (this.$refs.the_read_price) {
      this.$refs.the_read_price.$el.removeAttribute('disabled')
      this.$refs.the_read_price.$el.classList.remove('is-disabled')
    }
  },
  methods: {
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
    // 查询关联合同
    async queryContractList (row, params) {
      const { data } = await this.$http({
        url: '/api-sup-ce/po/order/queryContractMappingByOrderDetailId',
        method: 'POST',
        data: {
          'orderDetailId': row.orderDetailId,
          ...params
        },
        loading: true
      })
      return data
    },
    // 获取调用查看关联合同弹窗接口及参数
    getQueryObj (obj) {
      let map = new Map([
        ['concatContract', { // 合同已关联数量
            listName: 'orderContractMappingList',
            url: '/api-sup-ce/po/order/queryContractMappingByOrderDetailId',
            params: {
              orderDetailId: this.contractViewParams.row.orderDetailId,
              ...obj
             }
          }
        ],
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
    // 合同已关联数量
    async viewContract (row) {
      let url = '/api-sup-ce/po/order/queryContractMappingByOrderDetailId'
      this.concatSelectRow = row
      this.contractView.row = row
      this.contractView.params = await this.$refs.viewContractRef.queryViewContract(url, { orderDetailId: row.orderDetailId })
      this.contractViewParams = { from: 'concatContract', visible: true, row }
    },
    // 关联合同
    async concatContract (row) {
      if (!row.orderDetailId) return this.$message.warning(this.$t('orderMod.savedConcatContract'))
      this.contractConcat.row = row
      this.contractConcat.params = await this.queryContractList(row)
      this.concatSelectRow = row
      this.contractVisible = true
    },
    // 关联合同 - 选择事件
    handleContractChange (selection) {
      this.selectContractConcat = selection
    },
    // 获取新增合同列表
    async getContractList (params) {
      const { data } = await this.$http({
        url: '/api-sup-ce/po/order/listContractMaterialByOrderDetail',
        method: 'POST',
        data: {
          'materialId': this.concatSelectRow.materialId,
          'orderDetailId': this.concatSelectRow.orderDetailId,
          'orgId': this.form.ceeaOrgId,
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
      this.contractSelectView.params = list
    },
    async searchAddData (obj) {
      const list = await this.getContractList(obj)
      this.contractSelectView.params = list
    },
    // 获取行关联校验条件
    getCheckConcatObj () {
      return this.contractConcat.params.orderContractMappingList.map((row, i) => {
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

      const contractNumbers = this.contractConcat.params.orderContractMappingList.reduce((r, c) => {
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
        url: '/api-sup-ce/po/order/saveContractMapping',
        method: 'POST',
        data: {
          orderNum: this.concatSelectRow.orderNum,
          orderId: this.form.orderId,
          orderDetailId: this.concatSelectRow.orderDetailId,
          orderContractMappingList: this.contractConcat.params.orderContractMappingList
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
      this.$set(this.concatSelectRow, 'usedContractQuantity', num)
      this.$message.success(this.$t('common.success'))
      this.contractVisible = false
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
      const coverContract = this.contractConcat.params.orderContractMappingList.map(item => item.contractMaterialId)
      this.selectContract.forEach(row => {
        if (!coverContract.includes(row.contractMaterialId)) {
          this.contractConcat.params.orderContractMappingList.unshift(row)
        }
      })
      this.contractSelectVisible = false
    },
    printBill () {
      const xml = encodeURIComponent('database:database:采购订单打印.ureport.xml')
      const params = encodeURIComponent(`param=${this.form.orderNumber}`)
      const url = `${this.$systemUrl}/#/pdfPrint?xml=${xml}&params=${params}`
      window.open(url, '_blank', 'noopener,noreferrer')
    },
    // 选择需求类型为生产性需求则隐藏并清空预算编号及id
    setBugetInfor (val) {
      if (val !== 'NONPRODUCTIVE_DEMAND') {
        this.form.budgetManagementNum = ''
        this.form.budgetManagementId = ''
      }
    },
    addOneContent (multipleSelection) {
      if (multipleSelection.length === 0) {
        this.$message.error(this.$t('orderMod.msgOrder[9]'))
        return
      }
      multipleSelection.map(v => {
        const item = {
          ceeaPriceSourceType: v.ceeaPriceSourceType, // change by liwenhong
          bigCategoryCode: v.bigCategoryCode,
          lineNum: this.detailModelReal.length + 1,
          ceeaIfRequirement: 'N',
          ceeaRequirementLineId: v.requirementLineId,
          ceeaRequirementHeadNum: v.requirementHeadNum,
          ceeaRowNum: v.rowNum,
          ceeaOrganizationId: v.organizationId,
          ceeaOrganizationCode: v.organizationCode,
          ceeaOrganizationName: v.organizationName,
          receiptPlace: v.receivedFactory,
          categoryId: v.categoryId,
          categoryCode: v.categoryCode,
          categoryName: v.categoryName,
          materialId: v.materialId,
          materialCode: v.materialCode,
          materialName: v.materialName,
          unit: v.unitName,
          unitCode: v.unit,
          requirementQuantity: v.requirementQuantity,
          ceeaPlanReceiveDate: v.ceeaPlanReceiveDate,
          ceeaPromiseReceiveDate: v.ceeaPromiseReceiveDate,
          ceeaUnitTaxPrice: v.taxPrice,
          ceeaUnitNoTaxPrice: v.noTaxPrice,
          currencyId: v.currencyId,
          currencyCode: v.currencyCode,
          currencyName: v.currencyName,
          ceeaTaxRate: v.taxRate,
          ceeaTaxKey: v.taxCode,
          ceeaAmountIncludingTax: null,
          ceeaAmountExcludingTax: null,
          ceeaTaxAmount: v.ceeaTaxAmount,
          ceeaContractNo: v.contractCode,
          contractVoList: v.contractVoList,
          comments: v.comments,
          purchaseTaxList: v.purchaseTaxList,
          orderDetailStatus: ''
        }
        this.detailModel.push(item)
      })
      /* 获取查询供应商地点参数 */
      const materialIds = []
      this.detailModel.forEach(item => {
        materialIds.push(item.materialId)
      })
      this.materialIds = materialIds
      this.dialogVisible = false
    },
    addNewOne (selection) {
      if (selection.length === 0) {
        this.$message({
          type: 'warning',
          message: this.$t('orderMod.msgOrder[8]')
        })
        return
      }
      selection.forEach(v => {
        const item = {
          ceeaPriceSourceType: v.ceeaPriceSourceType, // change by liwenhong
          bigCategoryCode: v.bigCategoryCode, // change by liwenhong
          lineNum: this.detailModelReal.length + 1,
          ceeaIfRequirement: 'Y',
          ceeaRequirementLineId: v.requirementLineId,
          ceeaRequirementHeadNum: v.requirementHeadNum,
          ceeaRowNum: v.rowNum,
          ceeaOrganizationId: v.organizationId,
          ceeaOrganizationCode: v.organizationCode,
          ceeaOrganizationName: v.organizationName,
          receiptPlace: v.receivedFactory,
          categoryId: v.categoryId,
          categoryCode: v.categoryCode,
          categoryName: v.categoryName,
          materialId: v.materialId,
          materialCode: v.materialCode,
          materialName: v.materialName,
          unit: v.unit,
          unitCode: v.unitCode,
          requirementQuantity: v.requirementQuantity,
          orderQuantity: v.orderQuantity,
          ceeaPlanReceiveDate: v.ceeaPlanReceiveDate,
          ceeaPromiseReceiveDate: v.requirementDate, // 订单创建时的承诺到货时间默认为需求日期 change byliwenhong
          ceeaUnitTaxPrice: v.taxPrice,
          ceeaUnitNoTaxPrice: v.noTaxPrice,
          currencyId: v.currencyId,
          currencyCode: v.currencyCode,
          currencyName: v.currencyName,
          ceeaTaxRate: v.taxRate,
          ceeaTaxKey: v.taxKey,
          ceeaAmountIncludingTax: null,
          ceeaAmountExcludingTax: null,
          ceeaTaxAmount: v.ceeaTaxAmount,
          ceeaContractNo: v.contractCode,
          comments: v.comments,
          ceeaBusinessSmall: v.ceeaBusinessSmall,
          ceeaBusinessSmallCode: v.ceeaBusinessSmallCode,
          contractVoList: v.contractVoList,
          purchaseTaxList: v.purchaseTaxList
        }
        this.detailModel.push(item)
      })

      /* 获取查询供应商地点参数 */
      const materialIds = []
      this.detailModel.forEach(item => {
        materialIds.push(item.materialId)
      })
      this.materialIds = materialIds
      this.dialogFormVisible = false
    },
    asyncGetRealDataSource (data) {
      this.detailModelReal = data
    },
    initDetail () {
      const { row, flag } = this.$attrs.params
      this.ifsourceSystem = row.sourceSystem || ''
      // 审批单从单据号进来不直接跳审批流oa
      if (flag === 'add') {
        const { phone, nickname, department } = this.userInfo
        this.form.tel = phone
        this.form.buyerName = nickname
        this.form.ceeaDepartmentName = department
        this.$set(this.form, 'isManual', 'Y')
      } else {
        const { orderId } = this.$attrs.params.row
        this.queryDetails(orderId)
      }
    },
    taxRateChangeHandel (val, dictItem, row) {
      row.ceeaTaxRate = dictItem ? dictItem.key : '' // 税率值
      return val
    },
    // 收货地点选择
    changeSiteInfo (row, { element }) {
      this.$set(row, 'receiveContact', element.receiver)
      this.$set(row, 'receiveTelephone', element.receiverPhone)
      this.$set(row, 'receiveAddress', element.siteName)
    },
    // 预算编号快查关闭回写值
    getBudgetNumObj (val) {
      this.form.budgetManagementNum = val ? val.budgetManagementNumber : ''
      this.form.budgetManagementId = val ? val.budgetManagementId : ''
    },
    datePickerChange (val, row) {
      row.ceeaPromiseReceiveDate = row.ceeaPlanReceiveDate
    },
    getOrganizationByOrgCode () {
      const parentOrganizationId = this.form.ceeaOrgId || ''
      const organizationTypeCode = 'INV'
      const { userId } = this.userInfo

      this.$http({
        url: '/api-base/organization/organization/getOrganizationByOrgCode',
        method: 'POST',
        data: { parentOrganizationId, organizationTypeCode, userId },
        loading: true
      }).then(res => {
        this.organizatTaxList = res.data || []
      })
    },
    selectHandler (node, value, scope) {
      this.form.ceeaOrgId = node ? node.organizationId : null
      this.form.ceeaOrgCode = node ? node.organizationCode : null
      this.form.ceeaOrgName = node ? node.organizationName : null

      if (!this.form.organizationId) return
      // 清空库存组织
      this.form.organizationId = null
      this.form.organizationCode = null
      this.form.organizationName = null
    },
    selectHandler2 (node, value, scope) {
      this.filterForm.organizationId = node ? node.organizationId : null

      this.form.organizationId = node ? node.organizationId : null
      this.form.organizationCode = node ? node.organizationCode : null
      this.form.organizationName = node ? node.organizationName : null
      this.getOrganizationByOrgCode()
    },
    downloadTemplate () {
      downloadFileLink(
        '/api-sup-ce/po/orderDetail/downloadTemplate',
        this.$t('orderMod.purchaseOrderImp') + `${new Date().getTime()}` + '.xls',
      )
    },
    getVendorObj (val, scope) {
      console.log(val, scope, 'dddd')
      scope.vendorId = val ? val.companyId : ''
      scope.vendorCode = val ? val.companyCode : ''
      scope.vendorName = val ? val.companyName : ''
      if (val) {
        this.getConcatInfo(scope, val.companyId)
      } else {
        this.form.ceeaCostType = null
      }
    },
    // 查联系人
    async getConcatInfo (scope, companyId) {
      const { data } = await this.$http({
        url: '/api-sup/info/contactInfo/getContactInfoByCompanyId',
        method: 'POST',
        params: { companyId: companyId || '' },
        loading: true
      })
      scope.contactName = data.contactName
      scope.ceeaContactMethod = data.ceeaContactMethod
    },
    getCurrencyObj (val, scope) {
      scope.currencyId = val ? val.currencyId : ''
      scope.currencyCode = val ? val.currencyCode : ''
      scope.currencyName = val ? val.currencyName : ''
    },
    openMaterialList () {
      const sign = [
        !this.form.ceeaOrgId,
        !this.form.organizationId,
        !this.form.orderType,
        !this.form.vendorId,
        !this.form.receiveAddress
      ]
      if (this.form.demandType === 'NONPRODUCTIVE_DEMAND') {
        sign.push(!this.form.budgetManagementId)
      }
      if (sign.some(item => item)) {
        if (this.form.demandType === 'NONPRODUCTIVE_DEMAND') {
          // 请先选择业务实体，订单类型，收货地址、供应商和预算编号
          this.$message.warning(this.$t('orderMod.msgOrder[10]'))
        } else {
          this.$message.warning(this.$t('orderMod.pleaseFillrequired')) // 请先填写必填项
        }
        return
      }
      Object.assign(this.filterForm, {
        orgId: this.form.ceeaOrgId,
        orgName: this.form.ceeaOrgName,
        orgCode: this.form.ceeaOrgCode,
        organizationId: this.form.organizationId,
        organizationName: this.form.organizationName,
        organizationCode: this.form.organizationCode,
        purchaseType: this.form.orderType,
        vendorId: this.form.vendorId
      })

      this.$set(this.filterForm, 'isManual', this.form.isManual)
      this.$set(this.filterForm, 'ifSample', this.form.ifSample)
      this.$set(this.filterForm, 'budgetManagementId', this.form.budgetManagementId)
      console.log(this.filterForm, 'this.filterForm')
      this.dialogFormVisible = true
    },
    // 去除响应对象返回空值问题
    getResponseRemoveEmpty (target, obj) {
      const fillInfo = {}
      Object.keys(obj).forEach(item => {
        if (obj[item]) {
          fillInfo[item] = obj[item]
        }
      })
      Object.assign(target, fillInfo)
    },
    async queryDetails (orderId) {
      await purchaseOrderApi.queryOrderById(orderId).then(res => {
        // 去除响应对象返回空值问题
        this.getResponseRemoveEmpty(this.form, res.data.order)
        this.getOrganizationByOrgCode()
        const detailList = res.data.detailList || []
        const materialIds = []
        detailList.forEach(item => {
          materialIds.push(item.materialId)
        })
        this.materialIds = materialIds
        // 更改税率的类型 byliwenhong
        res.data.detailList = detailList.map(({ ceeaTaxRate, ...rest }) => ({
          ceeaTaxRate: `${ceeaTaxRate}`,
          ...rest
        }))
        console.log(res.data, 'res.data')
        this.detailModel = res.data.detailList.map(({ receiptPlace, ...rest }) => {
          let d = null
          try {
            if (receiptPlace) {
              d = JSON.parse(receiptPlace)
            }
          } catch (e) {
            console.log(e)
          }
          return {
            ...rest,
            receiptPlace: d
          }
        })

        const paymentInfor = res.data.paymentProvisionList
        paymentInfor.forEach(item => {
          let index = 1
          if (!item.paymentPeriodsNumber) {
            item.paymentPeriodsNumber = index++
          }
        })
        this.paymentInfor = paymentInfor
        this.orderFileList = res.data.attachList
      })

      this.$nextTick(() => {
        this.$refs.reoprtRef.query()
      })
    },
    deleteDetials (index) {
      this.detailModel.splice(index, 1)
    },
    setRowAmount (row) {
      row.ceeaAmountIncludingTax = Number(row.orderNum * row.ceeaUnitTaxPrice || 0).toFixed(2)
      row.ceeaAmountExcludingTax = Number(row.orderNum * row.ceeaUnitNoTaxPrice || 0).toFixed(2)
      // 合计num
      this.form.ceeaTotalNum = this.detailModelReal
        .map(v => v.orderNum)
        .reduce((p, c) => (Number(p) || 0) + (Number(c) || 0))
      // 合计金额含税---
      this.form.ceeaTaxAmount = Number(
        this.detailModelReal
          .map(v => v.ceeaAmountIncludingTax)
          .reduce((p, c) => Number(p) + Number(c)),
      ).toFixed(2)
      // 合计金额不含税---
      this.form.ceeaNoTaxAmount = Number(
        this.detailModelReal
          .map(v => v.ceeaAmountExcludingTax)
          .reduce((p, c) => Number(p) + Number(c)) || 0,
      ).toFixed(2)
    },
    addOnePayment () {
      this.paymentInfor.push({
        paymentPeriodsNumber: Number(this.paymentInfor.length) + 1,
        paymentTerm: null,
        paymentPeriod: null,
        paymentRadio: null,
        paymentStage: null,
        paymentWay: null
      })
    },
    setOrderTypeObj (val) {
      // console.log("val",val)
      // 选择零价格采购的时候，不校验必填付款条款 byliwenhong
      // [start] by liwenhong
      if (val === 'ZERO_PRICE') {
        this.zeroPriceCheck = true
      } else {
        this.zeroPriceCheck = false
      }
    },
    addUploadOneOrder () {
      this.orderFileList.push({
        attachId: '',
        fileuploadId: '',
        attachName: ''
      })
    },
    onInputBlur () {
      if (this.form.comments.length >= 80) {
        this.$message({
          type: 'warning',
          message: this.$t('orderMod.msgOrder[12]')
        })
      }
    },
    onInputBlurVendor (scope) {
      if (scope.comments.length >= 80) {
        this.$message({
          type: 'warning',
          message: this.$t('orderMod.msgOrder[12]')
        })
      }
    },
    cancelBill () {
      this.$emit('tab-remove', this.$attrs.tabName)
      this.__setTabTodo('vendorPurchaseOrderDetail.getQuerydata')
    },
    reset () {
      // 重置所有过滤条件
      for (const i in this.form) {
        this.form[i] = ''
      }
    },
    openDialog (row) {
      const sign = [
        this.form.ceeaOrgId,
        this.form.organizationId,
        this.form.orderType,
        this.form.vendorId,
        this.form.receiveAddress
      ]
      if (sign.some(item => !item)) {
        this.$message.warning(this.$t('orderMod.pleaseFillrequired')) // 请先填写必填项
        return
      }
      this.$set(this.queryForm, 'isManual', this.form.isManual)
      this.$set(this.queryForm, 'orgId', this.form.orgId)
      this.$set(this.queryForm, 'organizationId', this.form.organizationId)
      this.filterForm.orgId = this.form.ceeaOrgId
      this.dialogVisible = true
    }
  }
}
</script>
<style scoped lang="scss">
.the_main_po_list {
  .the_render_list {
    display: flex;
    list-style: none;
    li {
      width: 150px;
      border-left: 1px solid #ccc;
      span {
        display: block;
      }
    }
  }
  .sub_header {
    padding: 4px 11px;
    background: #eee;
  }
  .el-table .el-date-editor {
    width: 135px;
  }
  .toRequired {
    color: #ff4949;
    padding-right: 2px;
  }
  .the_header {
    > span {
      padding-right: 11px;
    }
    .el-button {
      float: right;
      margin-right: 11px;
    }
  }

  .btn_line {
    margin: 0 0 10px 0;
  }

  .topComment {
    margin-top: 15px;
    float: right;
  }
}
.form-container2 {
  padding: 16px 16px 0 16px;
}
.detail-add .el-form-item {
  padding-left: 0;
  padding-right: 0;
}
</style>
