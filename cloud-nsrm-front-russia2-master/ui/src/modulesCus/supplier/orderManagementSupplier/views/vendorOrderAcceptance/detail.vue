<template>
  <el-container class="flex-container the_orderAcceptanceDetail_wrapper" direction="vertical">
    <el-main>
      <el-collapse v-model="activeDims" class="tab-form-style">
        <el-form
          ref="formRef"
          :model="form"
          :disabled="isReadOnly"
        >
          <el-collapse-item :title="$t('vendorMod.receiptInfo')" name="1">
            <srm-row :gutter="32">
              <srm-col>
                <el-form-item
                  prop="orgName"
                  :label="$t('oneStopShopping.businessEntity')"
                  :rules="[{ required: true, message: $t('quota.orgIdTips') }]"
                >
                  <el-input v-model="form.orgName" disabled />
                </el-form-item>
              </srm-col>
              <!-- 验收单据号 -->
              <srm-col>
                <el-form-item :label="$t('cusEntry.orderMod.checkOrderNumber')">
                  <el-input v-model="form.checkOrderNumber" disabled />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item
                  prop="vendorName"
                  :label="$t('orderMod.buyerOrderSynergy.vendorName')"
                  :rules="[{ required: true, message: $t('quota.vendorTips') }]"
                >
                  <el-input v-model="form.vendorName" disabled />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item :label="$t('bidMod.billstatus')">
                  <DictSelect v-model="form.checkOrderStatus" code="CHECK_ORDER_STATUS" disabled />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item :label="$t('common.creator')">
                  <el-input v-model="form.createdFullName" disabled />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item :label="$t('orderMod.buyerOrderSynergy.creationDate')">
                  <el-date-picker
                    v-model="form.creationDate"
                    type="date"
                    :format="$formatDatePicker"
                    value-format="yyyy-MM-dd"
                    disabled
                  />
                </el-form-item>
              </srm-col>
              <!-- <srm-col>
                <el-form-item :label="$t('purchaseDemand.ceeaDepartment')">
                  <el-input v-model="form.departmentName" disabled />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item :label="$t('cusEntry.orderMod.companyName')">
                  <el-input v-model="form.orgName" disabled />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item :label="$t('cusEntry.common.plate')">
                  <el-input v-model="form.orgBuName" disabled />
                </el-form-item>
              </srm-col> -->
              <srm-col>
                <el-form-item :label="$t('bid_mod.currencyName')">
                  <el-input v-model="form.currencyName" disabled />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item :label="$t('contractMod.totalAmountTax')">
                  <el-input-number
                    v-model="form.taxTotalAmount"
                    style="width: 100%;"
                    :controls="false"
                    :precision="4"
                    disabled
                  />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item :label="$t('contractMod.totalAmountNoTax2')">
                  <el-input-number
                    v-model="form.noTaxTotalAmount"
                    style="width: 100%;"
                    :controls="false"
                    :precision="4"
                    disabled
                  />
                </el-form-item>
              </srm-col>
              <!-- <srm-col>
                <el-form-item
                  prop="approveUserName"
                  :label="$t('cusEntry.inq.departmentLeader')"
                  :rules="[{ required: true, message: $t('cusEntry.orderMod.departmentLeaderMsg') }]"
                >
                  <el-input v-model="form.approveUserName" disabled />
                </el-form-item>
              </srm-col> -->
            </srm-row>
          </el-collapse-item>
          <!-- 验收单明细 -->
          <el-collapse-item :title="$t('cusEntry.orderMod.checkOrderDetail')" name="2">
            <el-table
              :data="detailTableData"
              style="width: 100%;"
              border
              max-height="500px"
            >
              <el-table-column
                align="center"
                :label="$t('purSettlementMod.tabindex')"
                type="index"
                fixed="left"
                width="60"
              />
              <el-table-column
                align="center"
                prop="orderNumber"
                :label="$t('orderMod.buyerOrderSynergy.orderNumber')"
                width="150"
                :show-overflow-tooltip="true"
              />
              <el-table-column
                align="center"
                prop="lineNum"
                :label="$t('orderMod.orderLineNum')"
                width="100"
                :show-overflow-tooltip="true"
              />
              <el-table-column
                align="center"
                prop="categoryName"
                :label="$t('sourcingBuyer.categoryType')"
                width="150"
                :show-overflow-tooltip="true"
              />
              <el-table-column
                align="center"
                prop="materialCode"
                :label="$t('common.materialCode')"
                width="150"
                :show-overflow-tooltip="true"
              />
              <el-table-column
                align="center"
                prop="materialName"
                :label="$t('common.materialName')"
                min-width="150"
                :show-overflow-tooltip="true"
              />
              <el-table-column
                align="center"
                prop="unit"
                :label="$t('dataConfMod.unit')"
                width="100"
                :show-overflow-tooltip="true"
              />
              <el-table-column
                align="center"
                prop="orderNum"
                :label="$t('orderMod.buyerOrderSynergy.orderNum')"
                width="100"
                :show-overflow-tooltip="true"
              />
              <!-- 可验收数量 = 订单数量(orderNum) - 验收数量(extCheckQty)-->
              <el-table-column
                align="center"
                prop="extCheckQtyToDo"
                :label="$t('cusEntry.orderMod.extCheckQty')"
                width="100"
                :show-overflow-tooltip="true"
              />
              <!-- 本次验收数量 -->
              <el-table-column
                align="center"
                prop="checkQty"
                :label="$t('cusEntry.orderMod.checkQty')"
                min-width="120"
                :render-header="_addStarToColumn"
                :show-overflow-tooltip="true"
              >
                <template slot-scope="scope">
                  <el-input-number
                    v-model="scope.row.checkQty"
                    style="width: 100%;"
                    :controls="false"
                    :min="0"
                    :max="scope.row.extCheckQtyToDo"
                    disabled
                  />
                </template>
              </el-table-column>
              <!-- 未税单价 -->
              <el-table-column
                prop="ceeaUnitNoTaxPrice"
                :label="$t('purSettlementMod.unitPriceNoTax')"
                minWidth="120"
                align="center"
                :formatter="setNumberPrecision"
                show-overflow-tooltip
              />
              <!-- 税率 -->
              <el-table-column
                prop="ceeaTaxRate"
                :label="$t('bidMod.taxRate2')"
                minWidth="100"
                align="center"
                show-overflow-tooltip
              />
              <!-- 含税单价 -->
              <el-table-column
                prop="ceeaUnitTaxPrice"
                :label="$t('bid_mod.taxUnitPrice')"
                minWidth="120"
                align="center"
                :formatter="setNumberPrecision"
                show-overflow-tooltip
              />
              <!-- 未税总价 -->
              <el-table-column
                prop="noTaxAmount"
                :label="$t('cusEntry.bidMod.orderNotaxAmount')"
                minWidth="120"
                align="center"
                :formatter="setNumberPrecision"
                show-overflow-tooltip
              />
              <!-- 含税总价 -->
              <el-table-column
                prop="taxAmount"
                :label="$t('cusEntry.bidMod.orderTaxAmount')"
                minWidth="120"
                align="center"
                :formatter="setNumberPrecision"
                show-overflow-tooltip
              />
            </el-table>
          </el-collapse-item>
          <!-- 验收意见 -->
          <el-collapse-item :title="$t('cusEntry.orderMod.checkAdvice')" name="3">
            <srm-row>
              <srm-col :initCol="1">
                <el-form-item
                  prop="checkAdvice"
                  :label="$t('cusEntry.orderMod.checkAdvice')"
                  :rules="[{ required: true, message: $t('cusEntry.orderMod.checkAdviceMsg') }]"
                >
                  <el-input
                    v-model="form.checkAdvice"
                    type="textarea"
                    :disabled="isReadOnly"
                  />
                </el-form-item>
              </srm-col>
            </srm-row>
          </el-collapse-item>
          <el-collapse-item :title="$t('quota.fileInfo')" name="4">
            <el-table
              :data="attachList"
              border
              style="width: 100%; margin-bottom: 16px;"
              max-height="250px"
            >
              <el-table-column
                align="center"
                type="index"
                :label="$t('common.sort')"
                width="60"
              />
              <el-table-column
                align="center"
                prop="attachName"
                :label="$t('bidMod.fileName')"
                minWidth="180"
              >
                <template slot-scope="scope">
                  <SrmCommonFile
                    :extra-data="fileInfo"
                    :default-file="{
                      fileId: scope.row.attachId,
                      fileName: scope.row.attachName
                    }"
                    readonly
                  />
                </template>
              </el-table-column>
              <el-table-column
                align="center"
                prop="createdFullName"
                :label="$t('quota.uploadBy')"
                minWidth="120"
              />
              <!-- 上传账号 -->
              <el-table-column
                align="center"
                prop="createdBy"
                :label="$t('cusEntry.orderMod.uploadBy')"
                minWidth="120"
              />
              <el-table-column
                align="center"
                prop="creationDate"
                :label="$t('quota.uploadDate')"
                minWidth="120"
                :formatter="(row, column, cellValue) => $parseTime(cellValue)"
              />
            </el-table>
          </el-collapse-item>
        </el-form>
      </el-collapse>
      <CToolbar>
        <template slot="right">
          <el-button @click="cancelBill">
            {{ isReadOnly ? $t('common.close') : $t('common.cancel') }}
          </el-button>
          <el-button v-if="isManageApprove" type="primary" @click="refuseHandler">
            {{ $t('common.refused') }}
          </el-button>
          <el-button v-if="isManageApprove" type="primary" @click="approveHandler">
            {{ $t('common.toApprove') }}
          </el-button>
        </template>
      </CToolbar>
    </el-main>
  </el-container>
</template>
<script>
import CToolbar from 'lib@/components/c-toolbar'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import TableView from 'lib@/components/Table/TableView'
import QuickSearch from 'lib@/components/QuickSearch'
import CPagination from 'lib@/components/c-pagination'
import OrganizationSelector from 'lib@/components/organization-selector'
import { transformMQL } from 'lib@/utils/util'
import { parseTime } from '@/utils'

export default {
  name: 'OrderAcceptanceDetail',
  components: {
    MainHeader,
    FormWrapper,
    TableView,
    CToolbar,
    QuickSearch,
    CPagination,
    OrganizationSelector
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      activeDims: ['1', '2', '3', '4'],
      fileInfo: {
        uploadType: 'DEF',
        sourceType: 'WEB_APP',
        fileModular: 'order',
        fileFunction: 'order',
        fileType: 'images'
      },
      form: {
        orgId: null,
        orgCode: null,
        orgName: null,
        checkOrderNumber: null,
        vendorId: null,
        vendorCode: null,
        vendorName: null,
        checkOrderStatus: 'DRAFT',
        createdFullName: null,
        creationDate: null,
        taxTotalAmount: null, // 含税总金额
        noTaxTotalAmount: null, // 未税总金额
        // currencyName: '人民币',
        currencyName: () => this.$t('cusEntry.supplement20250121.rouble'),
        currencyCode: 'RMB',
        departmentName: null,
        departmentCode: null,
        departmentId: null,
        orgBuCode: null,
        orgBuId: null,
        orgBuName: null,
        approveUserName: null,
        approveUserId: null,
        approveUserCode: null
      },
      detailTableData: [],
      detailTableDataDelete: [],
      attachList: [],
      attachListDelete: []
    }
  },
  computed: {
    // 管理按钮进来，供应商审批单据
    isManageApprove () {
      return this.$attrs.params.flag === 'manage'
    },
    isReadOnly () {
      return ['view', 'manage'].includes(this.$attrs.params.flag)
    }
  },
  created () {
    const { flag, row } = this.$attrs.params
    if (flag !== 'add') {
      this.queryDetails(row.checkOrderId)
    }
  },
  methods: {
    // 确认验收单
    async approveHandler () {
      const params = { checkOrderId: this.form.checkOrderId }
      const saveData = transformMQL.save('CheckOrderVendor', [params], 'supplierConfirm')
      this.$http({
        url: '/api-sup-ce/api-ql/CheckOrderVendor/supplierConfirm',
        method: 'POST',
        data: saveData,
        loading: true
      }).then(res => {
        this.$message.success(this.$t('common.success'))
        this.cancelBill()
      })
    },
    // 拒绝验收单
    async refuseHandler () {
      const prompt = await this.$prompt(this.$t('orderMod.msgRufuseReason'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        inputPattern: /\S{1,}/,
        inputErrorMessage: this.$t('orderMod.refuseReasonRequire')
      })
      if (!prompt) return
      const params = {
        checkOrderId: this.form.checkOrderId,
        approveComment: prompt.value
      }
      const saveData = transformMQL.save('CheckOrderVendor', [params], 'supplierRefuse')
      this.$http({
        url: '/api-sup-ce/api-ql/CheckOrderVendor/supplierRefuse',
        method: 'POST',
        data: saveData,
        loading: true
      }).then(res => {
        this.$message.success(this.$t('common.success'))
        this.cancelBill()
      })
    },
    cancelBill () {
      if (this.$attrs.params.flag == 'add') {
        this.$emit('tab-remove', 'orderAcceptanceDetail')
      } else {
        this.$emit('tab-remove', 'orderAcceptanceDetail' + this.form.checkOrderNumber)
      }
      this.__setTabTodo('orderAcceptanceList.getQuerydata')
    },
    // 设置小数点位数4位
    setNumberPrecision (row, column, cellValue, index) {
      return cellValue ? cellValue.toFixed(4) : null
    },
    queryDetails (checkOrderId) {
      const searchData = {
        type: 'CheckOrderVendor',
        action: 'read',
        payload: [checkOrderId],
        query: {
          '*': {},
          'detailList': {
            '*': {},
            'orderDetailId': {
              '*': {},
              'orderId': { '*': {} }
            },
            'checkOrderDetailId': { '*': {} }
          },
          attachList: { '*': {} }
        },
        lang: 'zh-cn',
        tree: true
      }
      this.$http({
        url: '/api-sup-ce/api-ql/CheckOrderVendor/read',
        method: 'POST',
        data: searchData,
        loading: true
      }).then(res => {
        if (res && res.data) {
          const { detailList = [], attachList = [], ...rest } = res.data[0]
          this.form = { ...rest }
          this.attachList = attachList
          // 数据组装：明细表格数据有些字段来自订单详情表
          this.detailTableData = detailList.map(item => {
            const { orderDetailId = {}, ...rest } = item
            const { orderId = {} } = orderDetailId
            const row = {
              ...rest,
              ...orderDetailId,
              ...orderId,
              // 单据保存时，验收数量就会回显，所以单据为了正确展示得加上本次验收数量
              extCheckQtyToDo: orderDetailId.orderNum - orderDetailId.extCheckQty + item.checkQty
            }
            return row
          })
        }
      })
    }
  }
}
</script>
<style scoped lang="scss">
</style>
