<template>
  <el-container class="flex-container the_vendorDeliveryOrderDetail_wrapper" direction="vertical">
    <el-main>
      <el-collapse v-model="activeDims" class="tab-form-style">
        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
        >
          <el-collapse-item :title="$t('orderMod.buyerOrderSynergy.appointDeliveryFormList')" name="1">
            <srm-row :gutter="32">
              <srm-col>
                <el-form-item :label="$t('orderMod.buyerOrderSynergy.deliveryNumber')">
                  <el-input v-model="form.deliveryNumber" disabled />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item
                  :label="$t('orderMod.buyerOrderSynergy.deliveryDate2')"
                  prop="deliveryDate"
                >
                  <el-date-picker
                    v-model="form.deliveryDate"
                    type="date"
                    :format="$formatDatePicker"
                    value-format="yyyy-MM-dd"
                    :placeholder="$t('bidMod.datePicker')"
                    :picker-options="pickerOptions"
                    disabled
                  />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item :label="$t('orderMod.buyerOrderSynergy.vendorCode')">
                  <el-input v-model="form.vendorCode" disabled />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item :label="$t('orderMod.buyerOrderSynergy.vendorName')">
                  <el-input v-model="form.vendorName" disabled />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item :label="$t('contractMod.linkMan')">
                  <el-input v-model="form.extVendorContacts" disabled />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item :label="$t('cusEntry.orderMod.extVendorPhone')">
                  <el-input v-model="form.extVendorPhone" disabled />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item
                  :label="$t('oneStopShopping.businessEntity')"
                  prop="orgName"
                >
                  <el-input v-model="form.orgName" disabled />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item
                  :label="$t('dataConfMod.organizationId')"
                  prop="organizationId"
                >
                  <el-select
                    v-model="form.organizationId"
                    filterable
                    disabled
                    @change="selectHandler2"
                  >
                    <el-option
                      v-for="item in departList"
                      :key="item.organizationId"
                      :label="item.organizationName"
                      :value="item.organizationId"
                    />
                  </el-select>
                </el-form-item>
              </srm-col>
              <!-- 收货人 -->
              <srm-col>
                <el-form-item :label="$t('cusEntry.orderMod.extReceiveContact')">
                  <el-input v-model="form.extReceiveContact" disabled />
                </el-form-item>
              </srm-col>
              <!-- 收货人联系方式 -->
              <srm-col>
                <el-form-item :label="$t('cusEntry.orderMod.extReceiveTelephone')">
                  <el-input v-model="form.extReceiveTelephone" disabled />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item :label="$t('oneStopShopping.receiveAddress')">
                  <el-input v-model="form.ceeaDeliveryPlace" disabled />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item :label="$t('bid_mod.currencyName')">
                  <dict-select
                    v-model="form.extCurrencyCode"
                    code="currency"
                    disabled
                  />
                </el-form-item>
              </srm-col>
              <!-- <srm-col>
                <el-form-item :label="$t('oneStopShopping.totalAmountIncludingTax')">
                  <el-input
                    v-model="form.extInTaxAmount"
                    disabled
                  />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item :label="$t('oneStopShopping.totalAmountExcludingTax')">
                  <el-input
                    v-model="form.extUnTaxAmount"
                    disabled
                  />
                </el-form-item>
              </srm-col> -->
              <srm-col>
                <el-form-item :label="$t('bidMod.billstatus')">
                  <DictSelect v-model="form.deliveryNoteStatus" code="DELIVERY_NOTE_STATUS" disabled />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item :label="$t('orderMod.buyerOrderSynergy.buyerName')">
                  <el-input v-model="form.extPurchaserName" disabled />
                </el-form-item>
              </srm-col>
              <srm-col>
                <!-- <el-form-item label="采购员电话"> -->
                <el-form-item :label="$t('cusEntry.supplement20250121.purchasingOfficersPhoneNumber')">
                  <el-input v-model="form.extPurchaserPhone" disabled />
                </el-form-item>
              </srm-col>
              <srm-col :initCol="1">
                <el-form-item :label="$t('orderMod.buyerOrderSynergy.comments')">
                  <el-input v-model="form.comments" type="textarea" :rows="2" :disabled="isReadOnly" />
                </el-form-item>
              </srm-col>
            </srm-row>
          </el-collapse-item>
          <!-- 物流信息 -->
          <el-collapse-item :title="$t('cusEntry.orderMod.logisticsInfo')" name="2">
            <srm-row>
              <!-- 货运方式 -->
              <srm-col>
                <el-form-item
                  prop="extExpressType"
                  :label="$t('cusEntry.orderMod.extExpressType')"
                >
                  <DictSelect
                    v-model="form.extExpressType"
                    code="DELIVERY_WAY"
                    :disabled="isReadOnly && form.deliveryNoteStatus !== 'DELIVERED'"
                    @change="extExpressTypeChange"
                  />
                </el-form-item>
              </srm-col>
              <!-- 快递单号 -->
              <srm-col v-if="form.extExpressType=='OTHER'">
                <el-form-item
                  :key="form.extExpressType"
                  prop="extExpressNo"
                  :label="$t('cusEntry.orderMod.extExpressNo')"
                >
                  <el-input v-model="form.extExpressNo" :disabled="isReadOnly && form.deliveryNoteStatus !== 'DELIVERED'" />
                </el-form-item>
              </srm-col>
              <!-- 配送人员 -->
              <srm-col v-if="form.extExpressType=='OWN'">
                <el-form-item
                  :key="form.extExpressType"
                  prop="extExpressMan"
                  :label="$t('cusEntry.orderMod.extExpressMan')"
                >
                  <el-input v-model="form.extExpressMan" :disabled="isReadOnly && form.deliveryNoteStatus !== 'DELIVERED'" />
                </el-form-item>
              </srm-col>
              <!-- 联系方式 -->
              <srm-col v-if="form.extExpressType=='OWN'">
                <el-form-item
                  :key="form.extExpressType"
                  prop="extExpressPhone"
                  :label="$t('cusEntry.orderMod.extExpressPhone')"
                >
                  <el-input v-model="form.extExpressPhone" :disabled="isReadOnly && form.deliveryNoteStatus !== 'DELIVERED'" />
                </el-form-item>
              </srm-col>
            </srm-row>
            <div v-if="form.extExpressType=='OTHER'">
              <!-- 查看快递信息 -->
              <el-button type="primary" @click="getExpressInfo">
                {{ $t('cusEntry.orderMod.getExpressInfo') }}
              </el-button>
            </div>
          </el-collapse-item>
          <el-collapse-item :title="$t('orderMod.buyerOrderSynergy.vendorDeliveryList')" name="3">
            <!-- 送货单头状态为已发货 -->
            <el-button
              v-if="isManage && form.deliveryNoteStatus === 'DELIVERED'"
              type="primary"
              style="margin-bottom: 10px;"
              @click="cancelDelivery"
            >
              {{ $t('common.cancel') }}
            </el-button>
            <el-table
              :data="detailTableData.slice((pageInfo.pageNum - 1) * pageInfo.pageSize, pageInfo.pageNum * pageInfo.pageSize)"
              style="width: 100%"
              border
              max-height="400px"
              :disabled="isReadOnly"
              :row-key="row => row.deliveryNoteDetailId"
              @selection-change="handleSelectionChange"
            >
              <el-table-column type="selection" width="55" :reserve-selection="true" />
              <el-table-column
                align="center"
                :label="$t('purSettlementMod.tabindex')"
                type="index"
                fixed="left"
                width="80"
              />
              <!-- 送货单行号 -->
              <el-table-column
                align="center"
                prop="lineNum"
                :label="$t('cusEntry.orderMod.lineNum')"
                width="120"
                :show-overflow-tooltip="true"
              />
              <!-- 送货单行状态 -->
              <el-table-column
                align="center"
                prop="extDetailStatus"
                :label="$t('cusEntry.orderMod.extDetailStatus')"
                width="120"
                :formatter="(row, column, cellValue) => $getDictLabel('DELIVERY_NOTE_DETAIL_STATUS', cellValue)"
                :show-overflow-tooltip="true"
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
                prop="orderLineNum"
                :label="$t('orderMod.orderLineNum')"
                width="100"
                :show-overflow-tooltip="true"
              />
              <!-- 采购品类 -->
              <el-table-column
                align="center"
                prop="categoryName"
                :label="$t('cusEntry.orderMod.ceeaCategoryName')"
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
              <!-- 规格型号 -->
              <el-table-column
                align="center"
                prop="specification"
                :label="$t('cusEntry.orderMod.specification')"
                width="100"
                :show-overflow-tooltip="true"
              />
              <el-table-column
                align="center"
                prop="extBrand"
                :label="$t('dataConfMod.band')"
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
              <el-table-column
                align="center"
                prop="numberRemaining"
                :label="$t('orderMod.remainUndeliveryQuantity')"
                width="150"
                :show-overflow-tooltip="true"
              />
              <el-table-column
                align="center"
                prop="deliveryQuantity"
                :label="$t('orderMod.thisDeliveryQuantity')"
                min-width="150"
                :render-header="_addStarToColumn"
                :show-overflow-tooltip="true"
              >
                <template slot-scope="scope">
                  <el-input-number
                    v-model="scope.row.deliveryQuantity"
                    :controls="false"
                    :min="0"
                    :max="scope.row.numberRemaining"
                    class="input-number-precision"
                    :disabled="isReadOnly"
                  />
                </template>
              </el-table-column>
              <!-- 该送货单累计到货数量 -->
              <el-table-column
                align="center"
                prop="warehouseQuantity"
                :label="$t('cusEntry.orderMod.warehouseQuantity')"
                width="160"
                :show-overflow-tooltip="true"
              />
              <!-- 该送货单累计已取消数量 -->
              <el-table-column
                align="center"
                prop="extCancelQty"
                :label="$t('cusEntry.orderMod.extCancelQty')"
                width="160"
                :show-overflow-tooltip="true"
              />
              <!-- 需求日期 -->
              <el-table-column
                align="center"
                prop="requirementDate"
                :label="$t('cusEntry.orderMod.requirementDate')"
                width="100"
                :show-overflow-tooltip="true"
                :formatter="(row, column, cellValue) => $parseTime(cellValue)"
              />
              <!-- 使用人 -->
              <el-table-column
                align="center"
                prop="extUserName"
                :label="$t('cusEntry.orderMod.extUserName')"
                width="100"
                :show-overflow-tooltip="true"
              />
              <!-- 使用部门 -->
              <el-table-column
                align="center"
                prop="extUseDepartmentName"
                :label="$t('cusEntry.orderMod.extUseDepartmentName')"
                width="100"
                :show-overflow-tooltip="true"
              />
              <el-table-column
                align="center"
                prop="ceeaPromiseReceiveDate"
                :label="$t('purchaseDemand.promiseReceiveDate')"
                :formatter="(row, column, cellValue) => $parseTime(cellValue)"
                :show-overflow-tooltip="true"
                width="160"
              />
              <!-- 质保期（自然日） -->
              <el-table-column
                align="center"
                prop="extWarrantyPeriod"
                :label="$t('cusEntry.orderMod.extWarrantyPeriod')"
                width="150"
                :show-overflow-tooltip="true"
              />
              <el-table-column
                align="center"
                prop="comments"
                :label="$t('purchaseDemand.comments')"
                width="150"
                :show-overflow-tooltip="true"
              />
              <!-- 完成时间 -->
              <el-table-column
                align="center"
                prop="extFinishTime"
                :label="$t('cusEntry.orderMod.extFinishTime')"
                width="150"
                :show-overflow-tooltip="true"
                :formatter="(row, column, cellValue) => $parseTime(cellValue)"
              />
              <!-- <el-table-column
                align="center"
                prop="extCancelReason"
                label="取消原因"
                minWidth="200"
                :formatter="(row, column, cellValue) => $getDictLabel('DELIVERY_NOTE_CANCEL_REASON', cellValue)"
                :show-overflow-tooltip="true"
              /> -->
              <el-table-column
                align="center"
                prop="extCancelReason"
                :label="$t('cusEntry.bidSuperviseReport.cancelReason')"
                minWidth="200"
                :formatter="(row, column, cellValue) => $getDictLabel('DELIVERY_NOTE_CANCEL_REASON', cellValue)"
                :show-overflow-tooltip="true"
              />
              <el-table-column
                v-if="!isReadOnly"
                :label="$t('common.operation')"
                fixed="right"
                width="80"
              >
                <template slot-scope="scope">
                  <el-button type="text" @click="deleteDetails(scope.$index, scope.row)">
                    {{ $t('common.delete') }}
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
            <CPagination
              ref="queryPagination"
              class="c-query-table-pagination"
              :total="detailTableData.length"
              :page-num="pageInfo.pageNum"
              :page-size="pageInfo.pageSize"
              @current-change="handleCurrentChange"
              @size-change="handleSizeChange"
            />
          </el-collapse-item>
        </el-form>
      </el-collapse>
      <CToolbar>
        <template slot="right">
          <el-button @click="cancelBill">
            {{ isReadOnly ? $t('common.close') : $t('common.cancel') }}
          </el-button>
          <!-- 与后端顾问沟通，暂存也校验必填，因为暂存时会占用剩余未送货数量，必须填写本次送货数量 -->
          <el-button v-if="!isReadOnly" type="primary" @click="submitBill('SAVE')">
            {{ $t('common.save') }}
          </el-button>
          <el-button v-if="!isReadOnly" type="primary" @click="submitBill('SUBMIT')">
            {{ $t('common.submit') }}
          </el-button>
          <el-button v-if="form.deliveryNoteStatus === 'DELIVERED'" @click="printBill">
            {{ $t('route.pdfPrint') }}
          </el-button>
          <el-button
            v-if="form.deliveryNoteStatus === 'DELIVERED' && $attrs.params.flag !== 'view'"
            type="primary"
            @click="updateLogisticInfo"
          >
            {{ $t('cusEntry.common.updateLogisticInfo') }}
          </el-button>
        </template>
      </CToolbar>
    </el-main>
    <!-- 查看快递信息 -->
    <SrmDialog
      :title="$t('cusEntry.orderMod.getExpressInfo')"
      size="middle"
      :visible.sync="dialogVisible"
      :close-on-click-modal="false"
      append-to-body
    >
      <div v-if="expressInfoList.length > 0">
        <el-timeline :reverse="reverse" style="padding: 10px 40px">
          <el-timeline-item
            v-for="(item, index) in expressInfoList"
            :key="index"
            :timestamp="item.time"
            icon="el-icon-eleme"
          >
            {{ item.context }}
          </el-timeline-item>
        </el-timeline>
      </div>
      <div v-else style="text-align: center">
        暂无快递信息
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">
          {{ $t('common.close') }}
        </el-button>
      </div>
    </SrmDialog>
    <!-- 取消发货弹框 -->
    <CancleDialog
      v-if="canclDialogVisible"
      :visible.sync="canclDialogVisible"
      :selection-list="selection"
      @after-cancle="afterCancle"
    />
  </el-container>
</template>
<script>
import CToolbar from 'lib@/components/c-toolbar'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import MainHeader from 'lib@/components/Table/MainHeader'
import { parseTime, adaptDictData } from '@/utils'
import QuickSearch from 'lib@/components/QuickSearch' // 快速查询组件
import CPagination from 'lib@/components/c-pagination'
import OrganizationSelector from 'lib@/components/organization-selector'
import { transformMQL } from 'lib@/utils/util'
import CancleDialog from './cancleDialog'
import { getDictItem } from '@/api/common'

export default {
  name: 'VendorDeliveryOrderDetail',
  components: {
    MainHeader,
    CToolbar,
    QuickSearch,
    CPagination,
    OrganizationSelector,
    CancleDialog
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      pageInfo: {
        pageNum: 1,
        pageSize: 15,
        total: 0
      },
      activeDims: ['1', '2', '3'],
      form: {
        deliveryNoteId: null,
        deliveryNumber: null,
        deliveryDate: null,
        vendorId: null,
        vendorCode: null,
        vendorName: null,
        extVendorContacts: null,
        extVendorPhone: null,
        orgId: null,
        orgCode: null,
        orgName: null,
        organizationId: null,
        organizationCode: null,
        organizationName: null,
        extReceiveContact: null,
        extReceiveTelephone: null,
        ceeaDeliveryPlace: null,
        extCurrencycode: null,
        extInTaxAmount: null,
        extUnTaxAmount: null,
        extPurchaserName: null,
        extPurchaserPhone: null,
        comments: null,
        deliveryNoteStatus: 'CREATE',
        extExpressType: null,
        extExpressNo: null,
        extExpressMan: null,
        extExpressPhone: null
      },
      pickerOptions: {
        disabledDate (time) {
          const today = new Date()
          today.setHours(0)
          today.setMinutes(0)
          today.setSeconds(0)
          today.setMilliseconds(0)
          return time.getTime() < today.getTime()
        }
      },
      rules: {
        organizationId: [{ required: true, message: this.$t('purchaseDemand.organizationIdTips') }],
        deliveryDate: [{ required: true, message: this.$t('orderMod.msgOrder[30]') }]
        // extExpressType: [{ required: true, message: this.$t('cusEntry.orderMod.extExpressTypeMsg') }]
      },
      dialogVisible: false,
      reverse: false,
      expressInfoList: [],
      detailTableData: [],
      detailTableDataDelete: [],
      selection: [],
      departList: [],
      canclDialogVisible: false,
      templateList: [] // 打印模板名称
    }
  },
  computed: {
    isReadOnly () {
      // manage：管理按钮进来，无编辑权限，可取消明细行送货
      return ['view', 'manage'].includes(this.$attrs.params.flag)
    },
    isManage () {
      return this.$attrs.params.flag === 'manage'
    }
  },
  created () {
    getDictItem('Delivery_Note_Printing_Template').then(res => {
      this.templateList = adaptDictData(res.data, 'dict')
    })
    const { flag, row } = this.$attrs.params
    if (flag == 'add') {
      // 默认加载采购商联系方式
      if (this.$store.state.user && this.$store.state.user.userInfo) {
        this.form.vendorId = this.$store.state.user.userInfo.companyId
        this.form.vendorCode = this.$store.state.user.userInfo.companyCode
        this.form.vendorName = this.$store.state.user.userInfo.companyName
      }
    } else {
      this.queryDetails(row.deliveryNoteId)
    }
  },
  methods: {
    // 更新物流信息
    updateLogisticInfo () {
      const params = {
        ...this.form,
        detailList: [
          ...this.detailTableData.map(item => {
            return {
              deliveryNoteDetailId: item.deliveryNoteDetailId,
              deliveryQuantity: item.deliveryQuantity,
              orderDetailId: item.orderDetailId
            }
          }),
          ...this.detailTableDataDelete
        ]
      }
      const saveData = transformMQL.save('DeliveryNoteVendor', [params], 'extSaveOrUpdate')
      this.$http({
        url: '/api-sup-ce/api-ql/DeliveryNoteVendor/extSaveOrUpdate',
        method: 'POST',
        data: saveData,
        loading: true
      }).then(res => {
        this.$message.success(this.$t('common.success'))
        this.cancelBill()
      })
    },
    handleCurrentChange (pageNum) {
      this.pageInfo.pageNum = pageNum
    },
    handleSizeChange (pageSize) {
      this.pageInfo.pageSize = pageSize
      /* 判断数组长度是否达到分页要求 */
      const sourceLength = this.detailTableData.length
      const targetLength = (this.pageInfo.pageNum - 1) * pageSize
      if (sourceLength <= targetLength) {
        this.pageInfo.pageNum = Math.ceil(sourceLength / pageSize)
      }
    },
    cancelBill () {
      if (this.$attrs.params.flag == 'add') {
        this.$emit('tab-remove', 'vendorDeliveryOrderDetail')
      } else {
        this.$emit('tab-remove', 'vendorDeliveryOrderDetail' + this.form.deliveryNumber)
      }
      this.__setTabTodo('vendorDeliveryOrderList.getQuerydata')
    },
    extExpressTypeChange (val) {
      this.form.extExpressNo = null
      this.form.extExpressMan = null
      this.form.extExpressPhone = null
    },
    selectHandler (node, value, scope) {
      this.form.orgId = node ? node.organizationId : null
      this.form.orgCode = node ? node.organizationCode : null
      this.form.orgName = node ? node.organizationName : null

      if (!this.form.organizationId) return
      // 清空库存组织
      this.form.organizationId = null
      this.form.organizationCode = null
      this.form.organizationName = null
    },
    selectHandler2 (val) {
      const obj = this.departList.find(v => v.organizationId === val) || {}
      this.form.organizationName = obj ? obj.organizationName : null
      this.form.organizationCode = obj ? obj.organizationCode : null
    },
    // 查询【库存组织】下拉项
    getDepartList () {
      this.$http({
        url: '/api-base/organization/organization/ch/listAllOrganization',
        method: 'POST',
        data: {
          organizationTypeCode: 'INV',
          parentOrganizationIds: this.form.orgId,
          parentOrganizationNames: this.form.orgName
        },
        loading: true
      }).then(res => {
        this.departList = res.data.list
        if (this.departList.length > 0 && !this.form.organizationId) {
          this.form.organizationId = this.departList[0].organizationId
          this.form.organizationName = this.departList[0].organizationName
          this.form.organizationCode = this.departList[0].organizationCode
        }
      })
    },
    queryDetails (deliveryNoteId) {
      const searchData = {
        type: 'DeliveryNoteVendor',
        action: 'getDetail',
        payload: {
          filter: { deliveryNoteId }
        },
        query: {
          '*': {},
          'detailList': {
            '*': {},
            'orderDetailId': {
              '*': {},
              'orderId': { '*': {} }
            },
            'deliveryNoticeDetailId': {
              '*': {},
              'deliveryNoticeId': { '*': {} }
            }
          }
        },
        lang: 'zh-cn',
        tree: true
      }
      this.$http({
        url: '/api-sup-ce/api-ql/DeliveryNoteVendor/getDetail',
        method: 'POST',
        data: searchData,
        loading: true
      }).then(res => {
        if (res && res.data && res.data.records.length > 0) {
          const { detailList = [], deliveryNoteStatus, extStatus, organizationId, ...rest } = res.data.records[0]
          this.form = {
            ...rest,
            // 过滤 库存组织的默认值 -1
            organizationId: organizationId == -1 ? null : organizationId,
            // 送货单状态优先取【扩展状态：extStatus】
            deliveryNoteStatus: extStatus || deliveryNoteStatus
          }
          // 数据组装：送货单明细表格数据有些字段来自订单详情表
          this.detailTableData = detailList.map(item => {
            const { orderDetailId = {}, lineNum, extDetailStatus, ...rest } = item
            const { orderId = {} } = orderDetailId
            return {
              ...rest,
              ...orderDetailId,
              lineNum, // 送货单行号
              extDetailStatus, // 送货单行状态
              // 单据保存时，剩余未送货数量就会被占用，所以单据为了正确展示得加上本次送货数量 - 累计已取消数量
              numberRemaining: item.numberRemaining + item.deliveryQuantity - item.extCancelQty,
              orderNumber: orderId.orderNumber, // 采购订单编号
              orderLineNum: orderDetailId.lineNum, // 订单行号
              comments: orderDetailId.comments // 订单明细备注
            }
          })
          // 查询库存组织
          if (this.form.orgId && this.departList.length == 0) {
            this.getDepartList()
          }
        }
      })
    },
    deleteDetails (index, row) {
      if (row.deliveryNoteDetailId) {
        this.detailTableDataDelete.push({ '$delete': row.deliveryNoteDetailId })
      }
      let num = (this.pageInfo.pageNum - 1) * this.pageInfo.pageSize + index
      this.detailTableData.splice(num, 1)
      /* 判断数组长度是否达到分页要求 */
      const sourceLength = this.detailTableData.length
      const targetLength = (this.pageInfo.pageNum - 1) * this.pageInfo.pageSize
      if (sourceLength <= targetLength) {
        this.pageInfo.pageNum = Math.ceil(sourceLength / this.pageInfo.pageSize)
      }
    },
    handleSelectionChange (selection) {
      this.selection = selection
    },
    cancelDelivery () {
      if (this.selection.length == 0) {
        // this.$message.error('请先选择数据')
        this.$message.error(this.$t("cusEntry.supplement20250121.pleaseSelectDataFirst"))
        return
      }
      this.canclDialogVisible = true
    },
    afterCancle () {
      this.queryDetails(this.form.deliveryNoteId)
      this.__setTabTodo('vendorDeliveryOrderList.getQuerydata')
    },
    saveBill (type) { // 保存：CREATE，提交：DELIVERED
      const params = {
        ...this.form,
        deliveryNoteStatus: type == 'SAVE' ? 'CREATE' : 'DELIVERED',
        detailList: [
          ...this.detailTableData.map(item => {
            return {
              deliveryNoteDetailId: item.deliveryNoteDetailId,
              deliveryQuantity: item.deliveryQuantity,
              orderDetailId: item.orderDetailId
            }
          }),
          ...this.detailTableDataDelete
        ]
      }
      const saveData = transformMQL.save('DeliveryNoteVendor', [params], 'extSaveOrUpdate')
      this.$http({
        url: '/api-sup-ce/api-ql/DeliveryNoteVendor/extSaveOrUpdate',
        method: 'POST',
        data: saveData,
        loading: true
      }).then(res => {
        this.$message.success(this.$t('common.success'))
        this.detailTableDataDelete = []
        if (type == 'SAVE') {
          this.__setTabTodo('vendorDeliveryOrderList.getQuerydata')
          this.queryDetails(this.form.deliveryNoteId)
        } else {
          this.cancelBill()
        }
      })
    },
    submitBill (type) {
      this.$refs.formRef.validate(valid => {
        if (valid) {
          let errFlag = false
          let errMsg = ''
          this.detailTableData.some((item, index) => {
            if (!item.deliveryQuantity) {
              errFlag = true
              // errMsg = `送货单明细第${index + 1}行未填写本次送货数量`
              errMsg = `${this.$t("cusEntry.supplement20250121.deliveryNoteDetailsNotFilled1")}${index + 1}${this.$t("cusEntry.supplement20250121.deliveryNoteDetailsNotFilled2")}`
              return true // 跳出循环
            }
          })
          if (errFlag) {
            this.$message.error(errMsg)
            return
          }
          this.saveBill(type)
        } else return false
      })
    },
    getExpressInfo () {
      this.$http({
        url: `/api-pj/logistics/logistics/info/list?num=${this.form.extExpressNo}`,
        method: 'POST',
        loading: true
      }).then(res => {
        this.expressInfoList = JSON.parse(res.data).message.data
        this.dialogVisible = true
      })
    },
    printBill () {
      let obj = this.templateList.find(item => item.value === this.form.orgCode) || {}
      let template = obj?.label || ''
      if (!template) {
        // this.$message.error('该业务实体未维护打印模版，请联系对应订单的采购员')
        this.$message.error(this.$t("cusEntry.supplement20250121.noPrintTemplateForBusinessEntity"))
        return
      }
      const xml = encodeURIComponent(`database:${template}.ureport.xml`)
      const params = encodeURIComponent(`deliveryNoteId=${this.form.deliveryNoteId}`)
      const url = `${this.$systemUrl}/#/pdfPrint?xml=${xml}&params=${params}`
      window.open(url, '_blank', 'noopener,noreferrer')
    }
  }
}
</script>
<style scoped lang="scss">
</style>
