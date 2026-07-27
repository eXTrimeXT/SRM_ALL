<template>
  <el-container class="flex-container purchaseOrderDetail" direction="vertical">
    <el-main>
      <el-form
        ref="detailRef"
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
          <!-- 订单明细 -->
          <el-collapse-item :title="$t('orderMod.buyerOrderSynergy.orderDetailsList')" name="2">
            <div class="btn_line">
              <el-button
                v-if="isManual && !isReadOnly"
                type="primary"
                class="detail-pbtn"
                @click="addOrderDetail"
              >
                {{ $t('common.add') }}
              </el-button>
            </div>
            <vxe-table
              ref="xTable"
              border
              align="center"
              show-overflow
              max-height="360"
              :data="detailModel.slice((pageInfo.currentPage - 1) * pageInfo.pageSize, pageInfo.currentPage * pageInfo.pageSize)"
              :column-config="{resizable: true}"
              :valid-config="{ showMessage: false }"
              :edit-rules="vxeTableValidRules"
              :edit-config="{
                trigger: 'click',
                mode: 'row',
                enabled: !isReadOnly
              }"
            >
              <vxe-column
                type="seq"
                :title="$t('common.sort')"
                fixed="left"
                width="60"
              />
              <vxe-column
                field="ceeaRequirementHeadNum"
                :title="$t('purchaseDemand.purRequisitionNum')"
                width="150"
              />
              <vxe-column
                field="ceeaRowNum"
                :title="$t('purchaseDemand.rowNum')"
                width="100"
              />
              <vxe-column
                field="orderDetailStatus"
                :title="$t('orderMod.buyerOrderSynergy.orderDetailStatus')"
                width="100"
              >
                <template #default="{ row }">
                  {{ $getDictLabel('OrderDetailStatus', row.orderDetailStatus) }}
                </template>
              </vxe-column>
              <vxe-column
                field="materialCode"
                :title="$t('purchaseDemand.itemCode')"
                width="150"
                :edit-render="{}"
              >
                <template #edit="scope">
                  <QuickSearch
                    :show-input="scope.row.materialCode"
                    show-key="materialCode"
                    :scope-data="scope.row"
                    name="scc_base_material_item_contract"
                    :disabled="isReadOnly || !isManual"
                    @close-quicksearch="getMaterialObj"
                  />
                </template>
                <template #default="scope">
                  <QuickSearch
                    :show-input="scope.row.materialCode"
                    show-key="materialCode"
                    :scope-data="scope.row"
                    name="scc_base_material_item_contract"
                    :disabled="isReadOnly || !isManual"
                    @close-quicksearch="getMaterialObj"
                  />
                </template>
              </vxe-column>
              <vxe-column
                field="materialName"
                :title="$t('purchaseDemand.itemName')"
                width="150"
              />
              <vxe-column
                field="specification"
                :title="$t('cusEntry.bidMod.specification')"
                width="150"
              />
              <vxe-column
                field="extBrand"
                title="品牌"
                width="150"
                :edit-render="{}"
              >
                <template #edit="scope">
                  <el-input
                    v-model="scope.row.extBrand"
                    :disabled="isReadOnly || !isManual"
                  />
                </template>
                <template #default="scope">
                  <el-input
                    v-model="scope.row.extBrand"
                    :disabled="isReadOnly || !isManual"
                  />
                </template>
              </vxe-column>
              <vxe-column
                field="unitCode"
                title="基本计量单位"
                width="150"
              >
                <template #default="{ row }">
                  {{ $getDictLabel('unit', row.unitCode) }}
                </template>
              </vxe-column>
              <vxe-column
                field="requirementQuantity"
                :title="$t('purchaseDemand.requirementQuantity')"
                width="120"
              />
              <vxe-column
                field="orderNum"
                :title="$t('orderMod.buyerOrderSynergy.orderNum')"
                width="120"
                :edit-render="{}"
              >
                <template #edit="scope">
                  <el-input
                    v-model="scope.row.orderNum"
                    v-input-format="{ type: 'float' }"
                    :disabled="isReadOnly || !isManual"
                    @change="setRowAmount(scope.row)"
                  />
                </template>
                <template #default="scope">
                  <el-input
                    v-model="scope.row.orderNum"
                    v-input-format="{ type: 'float' }"
                    :disabled="isReadOnly || !isManual"
                    @change="setRowAmount(scope.row)"
                  />
                </template>
              </vxe-column>
              <vxe-column
                field="requirementDate"
                :title="$t('purchaseDemand.requirementDate')"
                width="120"
                :edit-render="{}"
              >
                <template #edit="scope">
                  <el-date-picker
                    v-model="scope.row.requirementDate"
                    type="date"
                    format="yyyy-MM-dd"
                    value-format="yyyy-MM-dd"
                    :disabled="isReadOnly || !isManual"
                  />
                </template>
                <template #default="scope">
                  <el-date-picker
                    v-model="scope.row.requirementDate"
                    type="date"
                    format="yyyy-MM-dd"
                    value-format="yyyy-MM-dd"
                    :disabled="isReadOnly || !isManual"
                  />
                </template>
              </vxe-column>
              <vxe-column
                field="deliveryDate"
                title="供方承诺到货日期"
                width="120"
                :edit-render="{}"
              >
                <template #edit="scope">
                  <el-date-picker
                    v-model="scope.row.deliveryDate"
                    type="date"
                    format="yyyy-MM-dd"
                    value-format="yyyy-MM-dd"
                    :picker-options="endTiumePickerOptions"
                    :disabled="isReadOnly || !isManual"
                    @change="datePickerChange(scope.row)"
                  />
                </template>
                <template #default="scope">
                  <el-date-picker
                    v-model="scope.row.deliveryDate"
                    type="date"
                    format="yyyy-MM-dd"
                    value-format="yyyy-MM-dd"
                    :picker-options="endTiumePickerOptions"
                    :disabled="isReadOnly || !isManual"
                    @change="datePickerChange(scope.row)"
                  />
                </template>
              </vxe-column>
              <vxe-column
                field="ceeaPromiseReceiveDate"
                :title="$t('实际送货日期')"
                width="120"
              >
                <template #default="{ row }">
                  {{ $dayjsParse(row.ceeaPromiseReceiveDate) }}
                </template>
              </vxe-column>
              <vxe-column
                field="categoryName"
                title="品类"
                width="120"
              />
              <vxe-column
                field="ceeaUnitTaxPrice"
                :title="$t('purchaseDemand.taxPrice')"
                width="120"
              />
              <vxe-column
                field="ceeaUnitNoTaxPrice"
                :title="$t('contractMod.notaxPrice')"
                width="120"
                :edit-render="{}"
              >
                <template #edit="scope">
                  <el-input-number
                    v-model="scope.row.ceeaUnitNoTaxPrice"
                    style="width:100%"
                    :controls="false"
                    :precision="2"
                    :disabled="isReadOnly || !isManual"
                    @change="setRowAmount(scope.row)"
                  />
                </template>
                <template #default="scope">
                  <el-input-number
                    v-model="scope.row.ceeaUnitNoTaxPrice"
                    style="width:100%"
                    :controls="false"
                    :precision="2"
                    :disabled="isReadOnly || !isManual"
                    @change="setRowAmount(scope.row)"
                  />
                </template>
              </vxe-column>
              <vxe-column
                field="ceeaTaxKey"
                :title="$t('purchaseDemand.taxRate')"
                width="120"
                :edit-render="{}"
              >
                <template #edit="scope">
                  <DictSelect
                    v-if="isManual"
                    v-model="scope.row.ceeaTaxKey"
                    code="tax"
                    :disabled="isReadOnly || !isManual"
                    @change-value="(value, dictItem) => taxRateChangeHandel(value, dictItem, scope.row)"
                  />
                  <el-input
                    v-else
                    v-model="scope.row.ceeaTaxRate"
                    disabled
                  />
                </template>
                <template #default="scope">
                  <DictSelect
                    v-if="isManual"
                    v-model="scope.row.ceeaTaxKey"
                    code="tax"
                    :disabled="isReadOnly || !isManual"
                    @change-value="(value, dictItem) => taxRateChangeHandel(value, dictItem, scope.row)"
                  />
                  <el-input
                    v-else
                    v-model="scope.row.ceeaTaxRate"
                    disabled
                  />
                </template>
              </vxe-column>
              <vxe-column
                field="ceeaAmountIncludingTax"
                :title="$t('contractMod.amount2')"
                width="120"
              />
              <vxe-column
                field="ceeaAmountExcludingTax"
                :title="$t('contractMod.excludeTaxPayAmount')"
                width="120"
              />
              <vxe-column
                field="extUserName"
                title="使用人"
                width="120"
                :edit-render="{}"
              >
                <template #edit="scope">
                  <QuickSearch
                    :show-input="scope.row.extUserName"
                    show-key="nickname"
                    :scope-data="scope.row"
                    name="scc_rbac_user_display"
                    :disabled="isReadOnly || !isManual"
                    @close-quicksearch="getExtUserObj"
                  />
                </template>
                <template #default="scope">
                  <QuickSearch
                    :show-input="scope.row.extUserName"
                    show-key="nickname"
                    :scope-data="scope.row"
                    name="scc_rbac_user_display"
                    :disabled="isReadOnly || !isManual"
                    @close-quicksearch="getExtUserObj"
                  />
                </template>
              </vxe-column>
              <vxe-column
                field="extUseDepartmentCode"
                title="使用部门"
                width="120"
                :edit-render="{}"
              >
                <template #edit="scope">
                  <el-select
                    v-model="scope.row.extUseDepartmentCode"
                    filterable
                    :disabled="isReadOnly || !isManual"
                    @change="setUserObj(scope.row)"
                  >
                    <el-option
                      v-for="(item,index) in departList"
                      :key="index"
                      :label="item.organizationName"
                      :value="item.organizationCode"
                    />
                  </el-select>
                </template>
                <template #default="scope">
                  <el-select
                    v-model="scope.row.extUseDepartmentCode"
                    filterable
                    :disabled="isReadOnly || !isManual"
                    @change="setUserObj(scope.row)"
                  >
                    <el-option
                      v-for="(item,index) in departList"
                      :key="index"
                      :label="item.organizationName"
                      :value="item.organizationCode"
                    />
                  </el-select>
                </template>
              </vxe-column>
              <vxe-column
                field="extWarrantyPeriod"
                title="质保期（自然日）"
                width="150"
                :edit-render="{}"
              >
                <template #edit="scope">
                  <el-input-number
                    v-model="scope.row.extWarrantyPeriod"
                    style="width:100%"
                    :controls="false"
                    :disabled="isReadOnly || !isManual"
                  />
                </template>
                <template #default="scope">
                  <el-input-number
                    v-model="scope.row.extWarrantyPeriod"
                    style="width:100%"
                    :controls="false"
                    :disabled="isReadOnly || !isManual"
                  />
                </template>
              </vxe-column>
              <vxe-column
                field="extInvoiceType"
                title="发票类型"
                width="120"
                :edit-render="{}"
              >
                <template #edit="scope">
                  <DictSelect
                    v-model="scope.row.extInvoiceType"
                    code="EXT_SOU_INQ_ORDER_INVOICE_TYPE"
                    :disabled="isReadOnly || !isManual"
                  />
                </template>
                <template #default="scope">
                  <DictSelect
                    v-model="scope.row.extInvoiceType"
                    code="EXT_SOU_INQ_ORDER_INVOICE_TYPE"
                    :disabled="isReadOnly || !isManual"
                  />
                </template>
              </vxe-column>
              <vxe-column
                field="comments"
                :title="$t('purchaseDemand.comments')"
                width="120"
                :edit-render="{}"
              >
                <template #edit="scope">
                  <el-input
                    v-model="scope.row.comments"
                    maxlength="80"
                    :disabled="isReadOnly || !isManual"
                  />
                </template>
                <template #default="scope">
                  <el-input
                    v-model="scope.row.comments"
                    maxlength="80"
                    :disabled="isReadOnly || !isManual"
                  />
                </template>
              </vxe-column>
              <vxe-column
                field="extAttachId"
                title="订单行附件"
                width="150"
                :edit-render="{}"
              >
                <template #edit="{ row }">
                  <SrmCommonFile
                    :default-file="{
                      fileId: row.extAttachId,
                      fileName: row.extAttachName
                    }"
                    :readonly="isReadOnly || !isManual"
                    @on-change="({file}) => uploadSuccess(file,row)"
                  />
                </template>
                <template #default="{ row }">
                  <SrmCommonFile
                    :default-file="{
                      fileId: row.extAttachId,
                      fileName: row.extAttachName
                    }"
                    :readonly="isReadOnly || !isManual"
                    @on-change="({file}) => uploadSuccess(file,row)"
                  />
                </template>
              </vxe-column>
              <vxe-column
                field="extAgreementType"
                title="协议性质"
                width="120"
              />
              <vxe-column
                :title="$t('bidMod.operation')"
                fixed="right"
                width="80"
                :visible="!isReadOnly"
              >
                <template #default="{ row, $rowIndex }">
                  <vxe-button style="color: #0077FF" type="text" @click="deleteOrderDetail($rowIndex,row)">
                    {{ $t('common.delete') }}
                  </vxe-button>
                </template>
              </vxe-column>
            </vxe-table>
            <vxe-pager
              :layouts="['PrevPage', 'Number', 'NextPage', 'Sizes', 'FullJump', 'Total']"
              :current-page.sync="pageInfo.currentPage"
              :page-size.sync="pageInfo.pageSize"
              :total.sync="pageInfo.total"
              :page-sizes="[15, 30, 60, 120, 300, 600]"
              style="margin: 8px 0 16px 0;"
              @page-change="handlePageChange"
            />
          </el-collapse-item>
        </el-collapse>
      </el-form>
    </el-main>
    <CToolbar>
      <template slot="right">
        <el-button
          v-if="form.orderStatus !== 'DRAFT'"
          type="primary"
          @click="printBill"
        >
          {{ $t('common.pdfPrint') }}
        </el-button>
        <el-button @click="cancelBill">
          {{ $t('common.cancel') }}
        </el-button>
        <el-button
          v-if="!isReadOnly"
          type="primary"
          @click="solveHandler('SAVE')"
        >
          {{ $t('flowMod.temporaryView') }}
        </el-button>
        <!-- 手工订单提交审批，采购科长 驳回、通过，创建人可撤回提交 -->
        <el-button
          v-if="!isReadOnly && isManual"
          type="primary"
          @click="solveHandler('SUBMIT')"
        >
          {{ $t('purchaseDemand.submitAudit') }}
        </el-button>
        <el-button
          v-if="isManual && isManageWithdraw"
          type="primary"
          @click="withdrawHandler"
        >
          {{ $t('bidMod.withdraw') }}
        </el-button>
        <el-button
          v-if="isManual && isManageApprove"
          type="primary"
          @click="refuseHandler"
        >
          {{ $t('purchaseDemand.refuse') }}
        </el-button>
        <el-button
          v-if="isManual && isManageApprove"
          type="primary"
          @click="approveHandler"
        >
          {{ $t('common.toApprove') }}
        </el-button>
        <!-- 非手工订单直接提交，状态变为待供应商确认 -->
        <el-button
          v-if="!isReadOnly && !isManual"
          type="primary"
          @click="solveHandler('SUBMIT')"
        >
          {{ $t('common.submit') }}
        </el-button>
      </template>
    </CToolbar>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import BaseInfor from 'lib@/components/BaseTable/baseTable'
import OrganizationSelector from 'lib@/components/organization-selector'
import CCategorySelect from 'lib@/components/c-category-select'
import QuickSearch from 'lib@/components/QuickSearch' // 快速查询组件
import CToolbar from 'lib@/components/c-toolbar'
import TableView from 'lib@/components/Table/TableView'
import BaseForm from 'lib@/components/BaseForm'
import CPagination from 'lib@/components/c-pagination'
import { formItems, detailColumn } from './data/detail'
import { purchaseOrderApi } from 'modcb@/orderManagementBuyer/api'
import { transformMQL } from 'lib@/utils/util'
import { parseTime } from '@/utils'

export default {
  name: 'PurchaseOrderDetail',
  components: {
    QuickSearch,
    OrganizationSelector,
    CCategorySelect,
    CPagination,
    TableView,
    BaseForm,
    BaseInfor,
    CToolbar
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      activeDims: ['1', '2'],
      detailColumn: detailColumn(this),
      formItems: formItems(this),
      endTiumePickerOptions: {
        disabledDate: time => {
          const start = new Date()
          return time.getTime() < start.getTime() - 24 * 60 * 60 * 1000
        }
      },
      detailModelDelete: [],
      detailModel: [],
      form: {
        ceeaOrgId: null,
        ceeaOrgCode: null,
        ceeaOrgName: null,
        orderId: null,
        orderNumber: null,
        orderStatus: 'DRAFT',
        organizationId: null,
        organizationCode: null,
        organizationName: null,
        orderType: null,
        ceeaPurchaseOrderDate: null,
        receiveContact: null,
        receiveTelephone: null,
        receiveAddress: null,
        ceeaEmpUserId: null,
        ceeaEmpUsername: null,
        ceeaEmpNo: null,
        ceeaDepartmentName: null,
        extPurchaserPhone: null,
        extPurchaserEmail: null,
        extPurchaserOrgName: null,
        ceeaIfSupplierConfirm: 'Y',
        vendorId: null,
        vendorCode: null,
        vendorName: null,
        extVendorContacts: null,
        extVendorPhone: null,
        paymentMethod: null,
        termOfPayment: null,
        rfqSettlementCurrency: 'RMB',
        extOrderProperty: null,
        ceeaTotalNum: 0, // 合计数量
        ceeaTaxAmount: 0, // 合计金额含税
        ceeaNoTaxAmount: 0, // 合计金额不含税
        extApproveUserId: null,
        extApproveUserCode: null,
        extApproveUserName: null,
        extAreaCode: null, // 区域
        comments: null
      },
      departList: [],
      // 校验配置
      vxeTableValidRules: {
        materialCode: [{ required: true }],
        orderNum: [{ required: true }],
        requirementDate: [{ required: true }],
        deliveryDate: [{ required: true }],
        ceeaUnitNoTaxPrice: [{ required: true }],
        ceeaTaxKey: [{ required: true }],
        extUserName: [{ required: true }],
        extUseDepartmentCode: [{ required: true }]
      },
      pageInfo: {
        total: 0,
        currentPage: 1,
        pageSize: 15
      }
    }
  },
  computed: {
    // 手工订单
    isManual () {
      return this.form.orderType === 'MANUAL'
    },
    // 管理按钮进来，创建人撤回单据
    isManageWithdraw () {
      return this.$attrs.params.flag === 'manage' && this.$store.getters.userInfo.userId === this.form.createdId
    },
    // 管理按钮进来，采购科长审批单据
    isManageApprove () {
      return this.$attrs.params.flag === 'manage' && this.$store.getters.userInfo.userId === this.form.extApproveUserId
    },
    isReadOnly () {
      return ['view', 'manage'].includes(this.$attrs.params.flag)
    }
  },
  created () {
    this.initDetail()
  },
  methods: {
    // 打印
    printBill () {
      const xml = encodeURIComponent('database:采购订单打印模板.ureport.xml')
      const params = encodeURIComponent(`orderId=${this.form.orderId}`)
      const url = `${this.$systemUrl}/#/pdfPrint?xml=${xml}&params=${params}`
      window.open(url, '_blank', 'noopener,noreferrer')
    },
    handlePageChange ({ currentPage, pageSize }) {
      this.pageInfo.pageSize = pageSize
      this.pageInfo.total = this.detailModel.length
      /* 判断数组长度是否达到分页要求 */
      const sourceLength = this.detailModel.length
      const targetLength = (currentPage - 1) * pageSize
      if (sourceLength <= targetLength && currentPage > 1) {
        this.pageInfo.currentPage = Math.ceil(sourceLength / pageSize)
      }
    },
    // 获取时间戳
    getTime (stringDate) {
      return new Date(stringDate).getTime()
    },
    initDetail () {
      const { row, flag } = this.$attrs.params
      if (flag === 'add') {
        this.form.orderType = 'MANUAL'
        this.form.ceeaPurchaseOrderDate = parseTime(new Date(), '{y}-{m}-{d}')
      } else {
        const { orderId } = this.$attrs.params.row
        this.queryDetails(orderId)
      }
    },
    // 业务实体快查
    selectHandler (node, _value, _scope) {
      this.form.ceeaOrgId = node ? node.organizationId : null
      this.form.ceeaOrgCode = node ? node.organizationCode : null
      this.form.ceeaOrgName = node ? node.organizationName : null

      if (node) {
        this.getDepartList()
      } else {
        this.departList = []
      }

      if (this.form.organizationId) {
        this.form.organizationId = null
        this.form.organizationCode = null
        this.form.organizationName = null
      }
    },
    // 查询【使用部门】下拉项
    getDepartList () {
      this.$http({
        url: '/api-base/orgQuery/getSubOrgs',
        method: 'POST',
        data: { parentId: this.form.ceeaOrgId },
        loading: true
      }).then(res => {
        this.departList = res.data
      })
    },
    // 收货人快查
    getContactObj (val, scope) {
      scope.receiveContact = val ? val.nickname : ''
    },
    // 采购员快查
    getCeeaEmpUserObj (val, scope) {
      scope.ceeaEmpUserId = val ? val.userId : ''
      scope.ceeaEmpUsername = val ? val.nickname : ''
      scope.ceeaEmpNo = val ? val.username : ''

      if (val) {
        this.getCeeaEmpUserInfo(val.userId)
      } else {
        this.form.ceeaDepartmentName = ''
        this.form.extPurchaserPhone = ''
        this.form.extPurchaserEmail = ''
        this.form.extPurchaserOrgName = ''
      }
    },
    // 采购科长快查
    getExtApproveUseObj (val, scope) {
      scope.extApproveUserId = val ? val.userId : ''
      scope.extApproveUserCode = val ? val.username : ''
      scope.extApproveUserName = val ? val.nickname : ''
    },
    // 通过采购员id查询采购员部门、采购员电话、采购员邮箱、采购单位
    getCeeaEmpUserInfo (id) {
      this.$http({
        url: `/api-rbac/extUser/getByBuyer?id=${id}`,
        method: 'GET',
        loading: true
      }).then(res => {
        if (res && res.data) {
          this.form.ceeaDepartmentName = res.data.user.department
          this.form.extPurchaserPhone = res.data.user.extOfficePhone
          this.form.extPurchaserEmail = res.data.user.email
          this.form.extPurchaserOrgName = res.data.user.ceeaCompany
        }
      })
    },
    // 供应商快查
    getVendorObj (val, scope) {
      scope.vendorId = val ? val.companyId : ''
      scope.vendorCode = val ? val.companyCode : ''
      scope.vendorName = val ? val.companyName : ''
      if (val) {
        this.getContactInfo(val.companyId)
      } else {
        this.form.extVendorContacts = ''
        this.form.extVendorPhone = ''
      }
    },
    // 查询供应商联系人
    async getContactInfo (companyId) {
      const saveData = transformMQL.listPageData({
        type: 'ContactInfo',
        action: 'query',
        params: { companyId },
        filterOperator: {
          companyId: 'eq'
        }
      })
      this.$http({
        url: '/api-sup/api-ql/ContactInfo/query',
        method: 'POST',
        data: saveData,
        loading: true
      }).then(res => {
        if (res && res.data && res.data.records) {
          this.form.extVendorContacts = res.data.records[0]?.contactName
          this.form.extVendorPhone = res.data.records[0]?.ceeaContactMethod
        }
      })
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
      const searchData = transformMQL.save(
        'Order',
        [orderId],
        'read',
        {
          '*': {},
          'detailList': { '*': {} }
        }
      )
      this.$http({
        url: '/api-sup-ce/api-ql/Order/read',
        method: 'POST',
        data: searchData,
        loading: true
      }).then(res => {
        if (res && res.data) {
          this.getResponseRemoveEmpty(this.form, res.data[0])
          // 订单行明细状态 优先取扩展字段 extDetailStatus
          let detailModel = res.data[0].detailList?.map(item => {
            item.orderDetailStatus = item.extDetailStatus || item.orderDetailStatus
            return item
          })
          this.detailModel = detailModel
          this.pageInfo.total = this.detailModel.length
          // 通过采购员id查询采购员部门、采购员电话、采购员邮箱、采购单位
          let getInfo = !this.form.ceeaDepartmentName && !this.form.extPurchaserPhone &&
            !this.form.extPurchaserEmail && !this.form.extPurchaserOrgName
          if (!!this.form.ceeaEmpUserId && getInfo) {
            this.getCeeaEmpUserInfo(this.form.ceeaEmpUserId)
          }
          // 查供应商联系人
          if (!!this.form.vendorId && !this.form.extVendorContacts && !this.form.extVendorPhone) {
            this.getContactInfo(this.form.vendorId)
          }
          // 查询部门下拉项
          this.getDepartList()
        }
      })
    },
    // 选择物料
    getMaterialObj (val, scope) {
      scope.materialId = val ? val.materialId : null
      scope.materialCode = val ? val.materialCode : ''
      scope.materialName = val ? val.materialName : ''
      scope.specification = val ? val.materialType : ''
      scope.extBrand = val ? val.brand : ''
      scope.unitCode = val ? val.unit : ''
      scope.unit = val ? val.unitName : ''
      scope.categoryId = val ? val.categoryId : null
      scope.categoryName = val ? val.categoryName : ''
      scope.categoryCode = val ? val.categoryCode : ''
    },
    // 选择品类
    getItemObj (node, scope) {
      scope.categoryId = node ? node.categoryId : null
      scope.categoryName = node ? node.categoryFullName : ''
      scope.categoryCode = node ? node.categoryCode : ''
    },
    // 使用部门
    setUserObj (row) {
      const val = this.departList.find(v => v.organizationCode === row.extUseDepartmentCode) || {}
      row.extUseDepartmentName = val.organizationName
      row.extUseDepartmentCode = val.organizationCode
    },
    // 使用人
    getExtUserObj (val, scope) {
      scope.extUserName = val ? val.nickname : ''
      scope.extUserCode = val ? val.username : ''
    },
    // 交货日期
    datePickerChange (row) {
      row.ceeaPromiseReceiveDate = row.deliveryDate
    },
    // 税率改变
    taxRateChangeHandel (_val, dictItem, row) {
      row.ceeaTaxRate = dictItem ? dictItem.key : '' // 税率值
      this.setRowAmount(row)
      return _val
    },
    // 计算金额
    setRowAmount (row) {
      // 未税单价改变, 计算含税单价
      if (!!row.ceeaUnitNoTaxPrice && !!row.ceeaTaxRate) {
        row.ceeaUnitTaxPrice = Number(row.ceeaUnitNoTaxPrice * (1 + row.ceeaTaxRate / 100)).toFixed(2)
      } else {
        row.ceeaUnitTaxPrice = null
      }
      // 计算含税总价
      if (row.orderNum && row.ceeaUnitTaxPrice) {
        row.ceeaAmountIncludingTax = Number(row.orderNum * row.ceeaUnitTaxPrice || 0).toFixed(2)
      } else {
        row.ceeaAmountIncludingTax = null
      }
      // 计算未税总价
      if (row.orderNum && row.ceeaUnitNoTaxPrice) {
        row.ceeaAmountExcludingTax = Number(row.orderNum * row.ceeaUnitNoTaxPrice || 0).toFixed(2)
      } else {
        row.ceeaAmountExcludingTax = null
      }
      // 合计num
      this.form.ceeaTotalNum = this.detailModel
        .map(v => v.orderNum)
        .reduce((p, c) => (Number(p) || 0) + (Number(c) || 0), 0)
      // 合计金额含税---
      this.form.ceeaTaxAmount = Number(
        this.detailModel
          .map(v => v.ceeaAmountIncludingTax)
          .reduce((p, c) => (Number(p) || 0) + (Number(c) || 0), 0)
      ).toFixed(2)
      // 合计金额不含税---
      this.form.ceeaNoTaxAmount = Number(
        this.detailModel
          .map(v => v.ceeaAmountExcludingTax)
          .reduce((p, c) => (Number(p) || 0) + (Number(c) || 0), 0)
      ).toFixed(2)
    },
    /*
     * @Description: 校验表单表格必填项
     * @return: {
     *   flag: true/false,  校验是否通过
     *   message: 返回填写信息
     * }
     */
    async getCheckForm () {
      const formFiled = await this.$refs.formRef.validate()
      if (!formFiled.flag && Object.keys(formFiled.obj).length > 0) {
        const warnObj = Object.keys(formFiled.obj)[0]
        return {
          flag: formFiled.flag,
          message: formFiled.obj[warnObj][0].message
        }
      }
      return { flag: true }
    },
    async solveHandler (type) {
      // 与后端顾问沟通确定：暂存也校验必填（明细行上订单数量等字段 由于产品接口限制了必填）
      const { flag, message } = await this.getCheckForm()
      // 验证form表单
      if (flag) {
        if (this.detailModel.length == 0) {
          this.$message({
            type: 'error',
            message: this.$t('orderMod.buyerOrderSynergy.orderDetailsMsg')
          })
          return
        }
        const errMap = await this.$refs.xTable.fullValidate(true).catch(errMap => errMap)
        if (errMap) {
          this.$message({
            type: 'error',
            message: this.$t('common.pleasefinishRequired')
          })
          return
        }
        if (type === 'SAVE' || this.isManual) { // 手工订单
          this.saveBill(type)
        } else {
          this.submitBill(type) // 其他订单
        }
      } else {
        this.__focus_error__(message)
      }
    },
    saveBill (type) {
      const params = {
        ...this.form,
        orderStatus: type === 'SAVE' ? 'DRAFT' : 'UNDER_APPROVAL',
        ceeaIfSupplierConfirm: 'Y', // 是否需要供应商确认
        detailList: [ ...this.detailModel, ...this.detailModelDelete ]
      }
      const saveData = transformMQL.save('Order', [params], 'extSaveOrUpdate')
      this.$http({
        url: '/api-sup-ce/api-ql/Order/extSaveOrUpdate',
        method: 'POST',
        data: saveData,
        loading: true
      }).then(res => {
        if (res && res.data) {
          this.$message.success(this.$t('common.success'))
          this.detailModelDelete = []
          if (type === 'SAVE') {
            let orderId = res.data[0].orderId
            this.queryDetails(orderId)
            this.__setTabTodo('PurchaseOrderListBuyer.getQuerydata')
          } else {
            this.cancelBill()
          }
        }
      })
    },
    submitBill (type) {
      const params = {
        ...this.form,
        ceeaIfSupplierConfirm: 'Y', // 是否需要供应商确认
        orderStatus: 'APPROVED_INVALID',
        detailList: [ ...this.detailModel, ...this.detailModelDelete ]
      }
      const saveData = transformMQL.save('Order', [params], 'extSaveOrUpdate')
      this.$http({
        url: '/api-sup-ce/api-ql/Order/extSaveOrUpdate',
        method: 'POST',
        data: saveData,
        loading: true
      }).then(res => {
        this.cancelBill()
      })
    },
    // 创建人撤回
    withdrawHandler () {
      const params = { orderId: this.form.orderId }
      const saveData = transformMQL.save('Order', [params], 'extWithdraw')
      this.$http({
        url: '/api-sup-ce/api-ql/Order/extWithdraw',
        method: 'POST',
        data: saveData,
        loading: true
      }).then(res => {
        this.$message.success(this.$t('common.success'))
        this.cancelBill()
      })
    },
    // 采购科长驳回
    refuseHandler () {
      const params = { orderId: this.form.orderId }
      const saveData = transformMQL.save('Order', [params], 'extReject')
      this.$http({
        url: '/api-sup-ce/api-ql/Order/extReject',
        method: 'POST',
        data: saveData,
        loading: true
      }).then(res => {
        this.$message.success(this.$t('common.success'))
        this.cancelBill()
      })
    },
    // 采购科长通过审批
    approveHandler () {
      const params = { orderId: this.form.orderId }
      const saveData = transformMQL.save('Order', [params], 'extApprove')
      this.$http({
        url: '/api-sup-ce/api-ql/Order/extApprove',
        method: 'POST',
        data: saveData,
        loading: true
      }).then(res => {
        this.$message.success(this.$t('common.success'))
        this.cancelBill()
      })
    },
    cancelBill () {
      this.$emit('tab-remove', this.$attrs.tabName)
      this.__setTabTodo('PurchaseOrderListBuyer.getQuerydata')
    },
    addOrderDetail () {
      this.detailModel.push({
        ceeaIfRequirement: 'N', // 物料来源是否为采购需求
        orderDetailId: null,
        ceeaRequirementHeadNum: null,
        ceeaRowNum: null,
        orderDetailStatus: 'DRAFT',
        materialId: null,
        materialCode: null,
        materialName: null,
        specification: null,
        extBrand: null,
        unitCode: null,
        unit: null,
        requirementQuantity: null,
        orderNum: null,
        requirementDate: null,
        ceeaPlanReceiveDate: null,
        deliveryDate: null,
        ceeaPromiseReceiveDate: null,
        categoryName: null,
        categoryCode: null,
        categoryId: null,
        ceeaUnitTaxPrice: null,
        ceeaUnitNoTaxPrice: null,
        currencyId: null,
        currencyName: '人民币',
        currencyCode: 'RMB',
        ceeaTaxKey: null,
        ceeaAmountIncludingTax: null,
        ceeaAmountExcludingTax: null,
        extUserName: null,
        extUserCode: null,
        extUseDepartmentName: null,
        extUseDepartmentCode: null,
        extWarrantyPeriod: null,
        extInvoiceType: null,
        comments: null,
        extAttachId: null,
        extAttachName: null
      })
      this.handlePageChange(this.pageInfo)
    },
    deleteOrderDetail (index, row) {
      if (row.orderDetailId) {
        this.detailModelDelete.push({ '$delete': row.orderDetailId })
      }
      let num = (this.pageInfo.currentPage - 1) * this.pageInfo.pageSize + index
      this.detailModel.splice(num, 1)
      this.handlePageChange(this.pageInfo)
      this.setRowAmount(row)
    },
    uploadSuccess (file, row) {
      const { fileId = null, fileName = null } = file || {}
      row.extAttachId = fileId
      row.extAttachName = fileName
    }
  }
}
</script>
<style scoped lang="scss">
  .btn_line {
    margin: 0 0 10px 0;
  }
</style>
