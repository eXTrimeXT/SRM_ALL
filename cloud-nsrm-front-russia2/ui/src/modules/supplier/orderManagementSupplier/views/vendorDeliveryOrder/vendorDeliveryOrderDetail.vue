<template>
  <el-container class="flex-container the-vendorGreenChannelDetail-detail" direction="vertical">
    <el-main>
      <el-collapse v-model="activeDims" class="tab-form-style">
        <el-form
          :model="form"
          label-position="top"
          :disabled="isReadOnly"
          class="form-incontainer"
          :rules="rules"
        >
          <el-collapse-item :title="$t('orderMod.buyerOrderSynergy.appointDeliveryFormList')" name="1">
            <el-form
              ref="formRef"
              :model="form"
              label-position="top"
              :disabled="isReadOnly"
              class="form-incontainer"
              :rules="rules"
            >
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
                      :placeholder="$t('bidMod.datePicker')"
                      :picker-options="pickerOptions"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item :label="$t('orderMod.buyerOrderSynergy.vendorCode')">
                    <el-input v-if="curRole === 'VENDOR'" v-model="form.vendorCode" disabled />
                    <QuickSearch
                      v-else
                      :show-input="form.vendorCode"
                      show-key="companyCode"
                      :scope-data="form"
                      name="scc_sup_company_info"
                      @close-quicksearch="getVendorObj"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item :label="$t('orderMod.buyerOrderSynergy.vendorName')">
                    <el-input v-model="form.vendorName" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item
                    :label="$t('oneStopShopping.businessEntity')"
                    prop="orgId"
                  >
                    <OrganizationSelector
                      ref="organizationSelector"
                      v-model="form.orgId"
                      :parent-id="-1"
                      node-type="OU"
                      :placeholder="$t('common.pleaseSelect')"
                      :disabled="!!tableData.length"
                      @select="selectHandler"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item
                    :label="$t('dataConfMod.organizationId')"
                    prop="organizationId"
                  >
                    <OrganizationSelector
                      ref="organizationSelector2"
                      v-model="form.organizationId"
                      :parent-id="form.orgId"
                      node-type="INV"
                      :placeholder="$t('common.pleaseSelect')"
                      :disabled="!!tableData.length"
                      @select="selectHandler2"
                    />
                  </el-form-item>
                </srm-col>

                <srm-col>
                  <el-form-item
                    :label="$t('oneStopShopping.receiveAddress')"
                    prop="receivedFactory"
                  >
                    <DictSelect
                      v-model="form.receivedFactory"
                      :code="form.organizationId"
                      :custom-select-type="form.organizationId ? 'RECEIVE_ADDRESS' : ''"
                      :disabled="!!tableData.length"
                      @change-value="(val, element) => changeSiteInfo(form, element)"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item :label="$t('bidMod.billstatus')">
                    <DictSelect v-model="form.deliveryNoteStatus" code="DELIVERY_NOTE_STATUS" disabled />
                  </el-form-item>
                </srm-col>
                <!-- 送货单来源 -->
                <srm-col>
                  <el-form-item :label="$t('buyerDeliveryOrder.orderSource')" prop="orderSource">
                    <el-select v-model="form.orderSource" :disabled="!!tableData.length" @change="orderSourceChange">
                      <el-option
                        v-for="item in orderSourceList"
                        :key="item.label"
                        :label="item.label"
                        :value="item.value"
                      />
                    </el-select>
                  </el-form-item>
                </srm-col>
                <srm-col :initCol="1">
                  <el-form-item :label="$t('orderMod.buyerOrderSynergy.comments')">
                    <el-input v-model="form.comments" type="textarea" :rows="2" />
                  </el-form-item>
                </srm-col>
              </srm-row>
            </el-form>
          </el-collapse-item>

          <!-- 添加订单明细 -->
          <el-collapse-item :title="$t('orderMod.buyerOrderSynergy.vendorDeliveryList')" name="2">
            <el-button
              type="primary"
              style="margin-bottom: 10px"
              :disabled="isReadOnly"
              class="detail-pbtn"
              @click="openMaterialList"
            >
              {{ $t('orderMod.addOrderDetail') }}
            </el-button>
            <!-- 弹框区域 - 订单明细选择 -->
            <srm-dialog
              :title="$t('purchaseDemand.orderDetailSelect')"
              size="large"
              :visible.sync="dialogFormVisible"
              :close-on-click-modal="false"
              destroy-on-close
            >
              <div>
                <el-form ref="filterForm" :model="filterForm">
                  <srm-row :gutter="32">
                    <srm-col v-if="form.orderSource === 'DELIVERY_NOTICE'">
                      <el-form-item :label="$t('orderMod.deliveryNoticeNumber')">
                        <el-input v-model="filterForm.deliveryNoticeNumber" />
                      </el-form-item>
                    </srm-col>
                    <srm-col>
                      <el-form-item :label="$t('purSettlementMod.orderNumber')">
                        <el-input v-model="filterForm.orderNumber" />
                      </el-form-item>
                    </srm-col>
                    <srm-col>
                      <el-form-item :label="$t('common.materialCode')">
                        <el-input v-model="filterForm.materialCode" />
                      </el-form-item>
                    </srm-col>
                    <srm-col :lg="form.orderSource === 'DELIVERY_NOTICE' ? 18 : 24" class="col-btn">
                      <div :style="{marginBottom: form.orderSource !== 'DELIVERY_NOTICE' ? '12px' : ''}">
                        <el-button type="primary" @click="queryItemList">
                          {{
                            $t('common.search')
                          }}
                        </el-button>
                        <el-button type="primary" :disabled="!selection.length" @click="addNewOne">
                          {{
                            $t('common.confirm')
                          }}
                        </el-button>
                      </div>
                    </srm-col>
                  </srm-row>
                </el-form>
              </div>
              <el-table
                v-if="dialogFormVisible"
                :data="displayMaterialItem"
                style="width: 100%"
                border
                height="345px"
                highlight-current-row
                :row-key="setRowKey"
                @selection-change="handleSelectionChange"
              >
                <el-table-column type="selection" :reserve-selection="true" width="55" />
                <el-table-column :label="$t('purSettlementMod.tabindex')" align="center" type="index" width="80" />
                <el-table-column
                  v-if="form.orderSource === 'DELIVERY_NOTICE'"
                  align="center"
                  prop="deliveryNoticeNumber"
                  :label="$t('orderMod.deliveryNoticeNumber')"
                  width="150"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  v-if="form.orderSource === 'DELIVERY_NOTICE'"
                  align="center"
                  prop="deliveryNoticeLineNum"
                  :label="$t('orderMod.deliveryLineNum')"
                  width="150"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="orderNumber"
                  :label="$t('purSettlementMod.orderNumber')"
                  width="150"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="lineNum"
                  :label="$t('orderMod.orderLineNum')"
                  width="80"
                  :show-overflow-tooltip="true"
                />
                <!-- <el-table-column
                align="center"
                :label="$t('orderMod.arrivalPlanNo')"
                width="150"
                :show-overflow-tooltip="true"
              >
                <template v-if="orderSourceUrl === 'DELIVERY_NOTICE'" slot-scope="scope">
                  <span>{{ scope.row.deliveryNoticeNumber }}</span>
                </template>
                <template v-else slot-scope="scope">
                  <span>{{ scope.row.deliverPlanNum }}</span>
                </template>
              </el-table-column> -->
                <el-table-column
                  align="center"
                  prop="categoryName"
                  :label="$t('purchaseDemand.materialCateSub')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="materialCode"
                  :label="$t('common.materialCode')"
                  width="100"
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
                  :label="$t('orderMod.buyerOrderSynergy.unit')"
                  width="60"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="orderNum"
                  :label="$t('orderMod.buyerOrderSynergy.orderNum')"
                  width="80"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="numberRemaining"
                  :label="$t('orderMod.remainUndeliveryQuantity')"
                  width="110"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  v-if="form.orderSource === 'DELIVERY_NOTICE'"
                  align="center"
                  prop="noticeSum"
                  :label="$t('orderMod.surplusDeliveryQuantity1')"
                  width="150"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  :formatter="formatDate"
                  prop="ceeaPlanReceiveDate"
                  :label="$t('orderMod.buyerOrderSynergy.requirementDateStr')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  :formatter="formatDate"
                  prop="ceeaPromiseReceiveDate"
                  :label="$t('purchaseDemand.promiseReceiveDate')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="comments"
                  :label="$t('purchaseDemand.comments')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
              </el-table>
              <CPagination
                :total="parentOrgTableDataPage.total"
                :page-num="parentOrgTableDataPage.pageNum"
                :page-size="parentOrgTableDataPage.pageSize"
                @current-change="parentDataCurrentChange"
                @size-change="parentDataSizeChange"
              />
            </srm-dialog>
            <el-table
              ref="tableRef"
              :data="tableData"
              style="width: 100%"
              border
              max-height="251px"
              @selection-change="handleSelectionDeliveryLine"
            >
              <el-table-column :label="$t('purSettlementMod.tabindex')" align="center" type="index" width="80" />
              <el-table-column :disabled="isReadOnly" type="selection" width="55" />
              <el-table-column
                align="center"
                prop="orderNumber"
                :label="$t('purSettlementMod.orderNumber')"
                width="120"
                :show-overflow-tooltip="true"
              />
              <el-table-column
                align="center"
                prop="orderLineNum"
                :label="$t('orderMod.orderLineNum')"
                width="80"
                :show-overflow-tooltip="true"
              />
              <!-- 送货通知单编号 -->
              <el-table-column
                v-show="form.orderSource === 'DELIVERY_NOTICE'"
                align="center"
                prop="deliveryNoticeNumber"
                :label="$t('orderMod.deliveryNoticeNumber')"
                width="150"
                :show-overflow-tooltip="true"
              />
              <!-- 送货通知行号 -->
              <el-table-column
                v-show="form.orderSource === 'DELIVERY_NOTICE'"
                align="center"
                prop="deliveryNoticeLineNum"
                :label="$t('orderMod.deliveryLineNum')"
                width="150"
                :show-overflow-tooltip="true"
              />
              <el-table-column
                align="center"
                prop="materialCode"
                :label="$t('common.materialCode')"
                width="100"
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
                prop="ceeaBatchNum"
                :label="$t('orderMod.batchNum')"
                width="100"
                :show-overflow-tooltip="true"
              >
                <template slot-scope="scope">
                  <el-input
                    v-model="scope.row.ceeaBatchNum"
                    v-input-format="{ type: 'number' }"
                    :disabled="isReadOnly"
                  />
                </template>
              </el-table-column>
              <el-table-column
                align="center"
                prop="unit"
                :label="$t('bid_mod.unit')"
                width="60"
                :show-overflow-tooltip="true"
              />
              <el-table-column
                align="center"
                prop="orderNum"
                :label="$t('orderMod.buyerOrderSynergy.orderNum')"
                width="80"
                :show-overflow-tooltip="true"
              />
              <el-table-column
                align="center"
                prop="numberRemaining"
                :label="$t('orderMod.remainUndeliveryQuantity')"
                width="110"
                :show-overflow-tooltip="true"
              />
              <!-- 本次通知送货数量 -->
              <el-table-column
                v-if="form.orderSource === 'DELIVERY_NOTICE'"
                align="center"
                prop="noticeSum"
                :label="$t('orderMod.surplusDeliveryQuantity1')"
                width="150"
                :show-overflow-tooltip="true"
              />
              <!-- 本次送货数量 -->
              <el-table-column
                align="center"
                prop="deliveryQuantity"
                :label="$t('orderMod.thisDeliveryQuantity')"
                width="100"
                :show-overflow-tooltip="true"
              >
                <template slot="header">
                  <em class="toRequired">*</em>
                  <span>{{ $t('orderMod.thisDeliveryQuantity') }}</span>
                </template>
                <template slot-scope="scope">
                  <el-input-number
                    v-model="scope.row.deliveryQuantity"
                    :disabled="isReadOnly"
                    :controls="false"
                    class="input-number-precision"
                  />
                </template>
              </el-table-column>
              <el-table-column
                align="center"
                :formatter="formatDate"
                prop="ceeaPromiseReceiveDate"
                :label="$t('purchaseDemand.promiseReceiveDate')"
                width="170"
                :show-overflow-tooltip="true"
              />
              <el-table-column
                align="center"
                prop="comments"
                :label="$t('purchaseDemand.comments')"
                width="100"
                :show-overflow-tooltip="true"
              />

              <el-table-column :label="$t('common.operation')" width="60" fixed="right">
                <template slot-scope="scope">
                  <el-button
                    v-if="form.deliveryNoteStatus === 'CREATE'"
                    type="text"
                    :disabled="isReadOnly"
                    @click="deleteDetials(scope.$index, scope.row)"
                  >
                    {{ $t('common.delete') }}
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-collapse-item>

          <el-collapse-item :title="$t('bidMod.fileInfo')" name="4">
            <p class="btn_line">
              <el-button
                v-if="form.deliveryNoteStatus === 'CREATE'"
                type="primary"
                class="detail-pbtn"
                @click="addUploadOneApproval"
              >
                {{ $t('common.add') }}
              </el-button>
            </p>
            <el-table :data="procurementFile" style="width: 100%" border max-height="250px">
              <el-table-column align="center" type="index" :label="$t('purSettlementMod.tabindex')" width="80" />
              <el-table-column align="center" prop="fileSourceName" :label="$t('bidMod.fileName')">
                <template slot-scope="scope">
                  <SrmCommonFile
                    :extra-data="fileInfo"
                    :default-file="{
                      fileId: scope.row.fileuploadId,
                      fileName: scope.row.fileSourceName
                    }"
                    :readonly="false"
                    @on-change="({ file }) => outerHandleUploadSuccess(file, scope.row)"
                  />
                </template>
              </el-table-column>
              <el-table-column align="center" prop="createdBy" :label="$t('purchaseDemand.attachmentCreatedBy')" />
              <el-table-column align="center" prop="creationDate" :formatter="(row, column, cellValue) => $parseTime(cellValue)" :label="$t('purchaseDemand.attachmentCreatedDate')" />
              <el-table-column :label="$t('common.operation')" width="60">
                <template slot-scope="scope">
                  <el-button
                    v-if="form.deliveryNoteStatus === 'CREATE'"
                    type="text"
                    @click="handleDelClick(scope.$index, scope.row)"
                  >
                    {{ $t('common.delete') }}
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-collapse-item>

          <!-- WMS清单 隐藏，有需要就删除v-if -->
          <el-collapse-item v-if="false" :title="$t('orderMod.WMSList')" name="5">
            <div class="btn_line">
              <el-button type="primary" class="detail-pbtn" @click="exportWMStableData">
                {{
                  $t('common.export')
                }}
              </el-button>
              <MImport
                v-if="!isReadOnly"
                ref="import"
                style="display: inline-block; margin-left: 15px"
                class="importbtn"
                :title="$t('common.import')"
                :up-load-url="upLoadUrl"
                :extra-data="extraData"
                @downloadTemplate="downloadTemplate"
                @handleSuccess="handleSuccess"
              />
            </div>
            <el-table :data="WMStableData" style="width: 100%" border max-height="251px">
              <el-table-column :label="$t('purSettlementMod.tabindex')" align="center" type="index" width="80" />
              <el-table-column
                align="center"
                prop="orderNumber"
                :label="$t('purSettlementMod.orderNumber')"
                width="150"
                :show-overflow-tooltip="true"
              />
              <el-table-column
                align="center"
                prop="materialCode"
                :label="$t('common.materialCode')"
                width="100"
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
                prop="batchNum"
                :label="$t('orderMod.batchNum')"
                width="100"
                :show-overflow-tooltip="true"
              />
              <el-table-column
                align="center"
                prop="consignmentNum"
                :label="$t('orderMod.consignmentNum')"
                width="100"
                :show-overflow-tooltip="true"
              />
              <el-table-column
                align="center"
                prop="boxNum"
                :label="$t('orderMod.boxNum')"
                width="100"
                :show-overflow-tooltip="true"
              />
              <el-table-column
                align="center"
                prop="unit"
                :label="$t('orderMod.buyerOrderSynergy.unit')"
                width="60"
                :show-overflow-tooltip="true"
              />
              <el-table-column
                align="center"
                prop="deliveryQuantity"
                :label="$t('orderMod.thisDeliveryQuantity')"
                width="100"
                :show-overflow-tooltip="true"
              />
              <el-table-column
                align="center"
                :formatter="formatDate"
                prop="produceDate"
                :label="$t('orderMod.produceDate')"
                width="100"
                :show-overflow-tooltip="true"
              />
              <el-table-column
                align="center"
                :formatter="formatDate"
                prop="effectiveDate"
                :label="$t('orderMod.effectiveDate')"
                width="100"
                :show-overflow-tooltip="true"
              />
              <el-table-column
                align="center"
                :formatter="formatDate"
                prop="planReceiveDate"
                :label="$t('purchaseDemand.promiseReceiveDate')"
                width="100"
                :show-overflow-tooltip="true"
              />
              <el-table-column
                align="center"
                prop="comments"
                :label="$t('purchaseDemand.comments')"
                width="100"
                :show-overflow-tooltip="true"
              />
              <el-table-column
                align="center"
                prop="judgeStandard"
                :label="$t('orderMod.judgeStandard')"
                width="100"
                :show-overflow-tooltip="true"
              />
              <el-table-column
                align="center"
                prop="usingStatus"
                :label="$t('orderMod.usingStatus')"
                width="100"
                :show-overflow-tooltip="true"
              />
            </el-table>
          </el-collapse-item>
        </el-form>
      </el-collapse>
      <CToolbar>
        <template slot="right">
          <el-button @click="cancelBill">
            {{
              isReadOnly ? $t('common.close') : $t('common.cancel')
            }}
          </el-button>
          <!-- 保存并保存条码 -->
          <el-button v-if="!isReadOnly" type="primary" :disabled="canSubmit" @click="saveAndGoTagConfirm">
            {{ isReadOnly ? $t('orderMod.goTag') : $t('orderMod.buyerOrderSynergy.saveAndGoTag') }}
          </el-button>
          <el-button v-if="!isReadOnly" type="primary" :disabled="canSubmit" @click="saveBill">
            {{ $t('common.save') }}
          </el-button>
          <!-- 暂时搬去列表页 -->
          <!-- <el-button
            v-if="!isReadOnly"
            type="primary"
            :disabled="!createdDelivery"
            @click="confirmDelivery"
          >
            {{ $t('orderMod.confirmDelivery') }}
          </el-button> -->
          <el-button v-if="form.deliveryNoteStatus === 'DELIVERED'" type="primary" @click="printBill">
            {{ $t('route.pdfPrint') }}
          </el-button>
        </template>
      </CToolbar>
    </el-main>
  </el-container>
</template>
<script>
import CToolbar from 'lib@/components/c-toolbar'
import { tabTodoMixin } from '@/utils/mixins'
import MainHeader from 'lib@/components/Table/MainHeader'
import { parseTime, adaptDictData, isNull } from '@/utils'
import QuickSearch from 'lib@/components/QuickSearch' // 快速查询组件
import Treeselect from '@riophae/vue-treeselect'
import '@riophae/vue-treeselect/dist/vue-treeselect.css'
import CPagination from 'lib@/components/c-pagination'
import OrganizationSelector from 'lib@/components/organization-selector'
import MImport from 'lib@/components/import'
import { downloadFileLink } from 'lib@/utils/file'
import TableView from 'lib@/components/Table/TableView'
import { setRepeatData } from 'lib@/utils/util'
import {
  getDictItem
} from '@/api/common'

import { deliveryOrderApi } from 'mods@/orderManagementSupplier/api'

import tagManage from 'mods@/orderManagementSupplier/views/vendorDeliveryOrderEngine/tagManage.vue'

export default {
  name: 'VendorDeliveryOrderDetail',
  components: {
    MainHeader,
    CToolbar,
    Treeselect,
    QuickSearch,
    MImport,
    CPagination,
    OrganizationSelector,
    TableView
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      outShow: false,
      innShow: false,
      fileInfo: {
        fileModular: 'sup',
        fileFunction: 'vendorBiddingManagement',
        fileType: 'images'
      },
      upLoadUrl: '/api-sup-ce/deliverynote/deliveryNoteWms/importExcel',
      extraData: {},
      queryParams: {},
      selectionDetails: [],
      activeDims: ['1', '2', '3', '4', '5'],
      curRole: this.$store.getters.userType,
      form: {
        deliveryNoteId: null,
        deliveryNumber: '',
        vendorId: '',
        vendorCode: '',
        vendorName: '',
        orgId: '',
        orgCode: '',
        orgName: '',
        deliveryDate: '',
        receivedFactory: '',
        organizationId: '',
        organizationCode: '',
        organizationName: '',
        deliveryQuantity: '',
        comments: '',
        deliveryNoteStatus: 'CREATE'
      },
      procurementFile: [],
      dialogFormVisible: false,
      filterForm: {
        deliveryNoticeNumber: null,
        materialCode: null,
        materialName: null,
        orgName: null,
        categoryCode: null,
        orgId: null,
        organizationId: null,
        organizationName: null,
        receivedFactory: null,
        orderNumber: null,
        // startDate: null,
        // endDate: null,
        orderStatus: 'APPROVED'
      },
      pagesizeA: [15, 30, 45, 60],
      parentOrgQueryForm: {
        pageNum: 1,
        pageSize: 10
      },
      parentOrgTableDataPage: {
        total: 0,
        pageNum: 1,
        pageSize: 20
      },
      selection: [],
      displayMaterialItem: [],
      tableData: [],
      WMStableData: [],
      orderSourceUrl: '',
      orderSourceList: [
        {
          value: 'PURCHASE_ORDER',
          label: this.$t('route.buyerPurchaseOrder')
        },
        // {
        //   value: 'ARRIVAL_PLAN',
        //   label: this.$t('orderMod.arrivalPlanOrder')
        // },
        {
          value: 'DELIVERY_NOTICE',
          label: this.$t('orderMod.arrivalNotice')
        }
      ],
      r: [],
      isModify: false,
      modalVisible: false,
      canSubmit: false,
      rules: {
        orgId: [{ required: true, message: this.$t('orderMod.msgVendorOrder[6]') }],
        organizationId: [{ required: true, message: this.$t('dataConfMod.msgPSelectOrgza') }],
        deliveryDate: [{ required: true, message: this.$t('orderMod.msgOrder[30]') }],
        receivedFactory: [{ required: true, message: this.$t('orderMod.msgVendorOrder[7]') }],
        orderSource: [{ required: true, message: this.$t('orderMod.selectSourceData') }]
      },
      statusList: [],
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
      // 2021年8月13日
      outerBoxListCase: [], // 送货单详情，外箱条码信息
      selectOuterBoxListCase: [], // 选中的外箱条码
      activeDimsList: ['1', '2', '3', '4'],
      activeDimsListA: ['1', '2', '3', '4'],
      activeNames: ['1', '2'],
      echoInnerBoxCode: [],
      echoInnerBoxCodeList: [],

      outerExpandFields: [], // 外箱扩展字段数组(页面上显示用),数据库中标签字段表
      outerFormExpand: {}, // 外箱拓展字段实体
      innerExpandFields: [],
      innerFormExpand: {},
      currentPage: 1, // 初始页
      pagesize: 10, // 每页的数据

      selectDeliveryLine: [], // 勾选送货单行
      createdDelivery: false // 已经创建送货单
    }
  },
  computed: {
    orderDetailIds () {
      return this.tableData.map((i) => i.orderDetailId)
    },
    isReadOnly () {
      return this.$attrs.params.flag == 'readOnly'
    }
  },
  created () {
    const { flag, row } = this.$attrs.params
    if (flag == 'add') {
      // 默认加载采购商联系方式，如果没有才需要填写
      if (this.$store.state.user && this.$store.state.user.userInfo) {
        this.form.vendorId = this.$store.state.user.userInfo.companyId
        this.form.vendorCode = this.$store.state.user.userInfo.companyCode
        this.form.vendorName = this.$store.state.user.userInfo.companyName
      }
    } else if (flag == 'edit' || flag == 'readOnly') {
      for (let i in this.form) {
        this.form[i] = row[i]
      }
      this.canSubmit = row.deliveryNoteStatus === 'SUBMIT'
      this.createdDelivery = true
      this.queryList()
    }
  },
  mounted () {
    const { companyId, companyName, companyCode } = this.$store.getters.userInfo
    this.form = {
      ...this.form,
      vendorId: companyId,
      vendorName: companyName,
      vendorCode: companyCode
    }
  },
  methods: {
    // 选择订单来源
    orderSourceChange () {
      this.doLayout()
    },
    // 收货地点选择
    changeSiteInfo (row, { element }) {
      this.$set(row, 'receiveContact', element.receiver)
      this.$set(row, 'receiveTelephone', element.receiverPhone)
      this.$set(row, 'receivedFactory', element.siteName)
    },
    formatDate (row, column, cellValue, index) {
      return cellValue ? parseTime(cellValue, '{y}-{m}-{d}') : ''
    },
    queryList () {
      const { deliveryNoteId } = this.$attrs.params.row
      this.$http({
        url: '/api-sup-ce/order/deliveryNoteDetail/deliveryNoteDetailPage',
        method: 'POST',
        data: { deliveryNoteId, 'pageSize': 1000, 'pageNum': 1 },
        loading: true
      })
        .then((data) => {
          this.tableData = data.data.list
        })
        .catch((err) => {
          console.log(err)
        })
      this.$http({
        url: '/api-sup-ce/order/deliveryNote/getDeliveryDTO',
        method: 'GET',
        params: { orderId: deliveryNoteId },
        loading: true
      })
        .then((data) => {
          this.form = data.data.deliveryNote
          this.procurementFile = data.data.procurementFile
        })
        .catch((err) => {
          console.log(err)
        })
      this.$http({
        url: '/api-sup-ce/deliverynote/deliveryNoteWms/listPage',
        method: 'POST',
        data: { deliveryNoteId: deliveryNoteId },
        loading: true
      })
        .then((data) => {
          this.WMStableData = data.data.list
        })
        .catch((err) => {
          console.log(err)
        })
    },
    deleteDetials (index, row) {
      this.tableData.splice(index, 1)
    },
    onOk (value) {
      value.forEach((i) => {
        const flag =
          (this.tableData || []).findIndex((j) => j.orderDetailId === i.orderDetailId) === -1
        const obj = {
          ...i,
          buyerName: this.$store.getters.user.userInfo.nickname
        }
        if (flag) {
          this.tableData.push(obj)
        }
      })
      this.modalVisible = false
    },
    onCancle () {
      this.modalVisible = false
    },
    parentDataCurrentChange (num) {
      this.parentOrgQueryForm.pageNum = num
      this.queryItemList()
    },
    parentDataSizeChange (size) {
      this.parentOrgQueryForm.pageSize = size
      this.queryItemList()
    },
    resetFilterForm () {
      for (let i in this.filterForm) {
        this.filterForm[i] = ''
      }
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
    selectHandler2 (node, value, scope) {
      this.form.organizationId = node ? node.organizationId : null
      this.form.organizationCode = node ? node.organizationCode : null
      this.form.organizationName = node ? node.organizationName : null
    },
    queryItemList () {
      this.displayMaterialItem = []
      const data = { ...this.parentOrgQueryForm, ...this.filterForm }
      Object.keys(data).forEach((item) => {
        if (!data[item]) {
          delete data[item]
        }
      })
      if (this.form.orderSource === 'PURCHASE_ORDER') {
        this.$http({
          url: '/api-sup-ce/order/orderDetail/listMaterialPage',
          method: 'POST',
          data: {
            ...data,
            receiveAddress: this.form.receivedFactory
          },
          loading: true
        }).then((res) => {
          this.displayMaterialItem = res.data.list
          this.displayMaterialItem.map((item) => {
            item.deliverPlanNum = item.ceeaPlanReceiveNum
          })
          this.parentOrgTableDataPage.total = res.data.total
        })
      } else if (this.form.orderSource === 'ARRIVAL_PLAN') {
        this.$http({
          url: '/api-sup-ce/deliver/orderDeliveryDetail/orderDeliveryDetailListPageCopy',
          method: 'POST',
          data: {
            ...data,
            receivedFactory: this.form.receivedFactory
          },
          loading: true
        }).then((res) => {
          this.displayMaterialItem = res.data.list
          this.displayMaterialItem.map((item) => {
            item.ceeaArrivalDetailId = item.orderDeliveryDetailId
            item.orderNum = item.planReceiveNum
            item.ceeaPlanReceiveDate = item.requirementDate
            item.ceeaPromiseReceiveDate = item.requirementDate
          })
          this.parentOrgTableDataPage.total = res.data.total
        })
      } else {
        this.$http({
          url: '/api-sup-ce/sup/deliveryNotice/listInDeliveryNote',
          method: 'POST',
          data: {
            ...data,
            receiveAddress: this.form.receivedFactory
          },
          loading: true
        }).then((res) => {
          this.displayMaterialItem = res.data.list
          this.displayMaterialItem.map((item) => {
            item.deliverPlanNum = item.ceeaPlanReceiveNum
          })
          this.parentOrgTableDataPage.total = res.data.total
        })
      }
    },
    getVendorObj (val, scope) {
      scope.vendorId = val ? val.companyId : ''
      scope.vendorCode = val ? val.companyCode : ''
      scope.vendorName = val ? val.companyName : ''
    },
    exportWMStableData () {
      downloadFileLink(
        '/api-sup-ce/deliverynote/deliveryNoteWms/getexportExcel?deliveryNoteId=' +
        this.form.deliveryNoteId,
        this.$t('orderMod.WMSListExport') + `${new Date().getTime()}.xls`
      )
    },
    handleSuccess ({ data }, file, fileList) {
      if (data && data.list) {
        const l = this.WMStableData.length
        data.list.forEach((i, lineNum) =>
          this.WMStableData.push({ ...i, lineNum: l + lineNum + 1 })
        )
      }
    },
    downloadTemplate () {
      downloadFileLink(
        '/api-sup-ce/deliverynote/deliveryNoteWms/downloadTemplate',
        this.$t('orderMod.wmsListImport') + `${new Date().getTime()}.xls`
      )
    },
    // 打开添加明细弹窗
    openMaterialList () {
      const sign = [
        !this.form.orgId,
        !this.form.organizationId,
        !this.form.receivedFactory,
        !this.form.orderSource
      ]
      if (sign.some(item => item)) return this.$message.warning(this.$t('orderMod.pleaseFillrequired'))

      this.dialogFormVisible = true
      this.selection = []
      this.filterForm.orgId = this.form.orgId
      this.filterForm.orgCode = this.form.orgCode
      this.filterForm.orgName = this.form.orgName
      this.filterForm.organizationId = this.form.organizationId
      this.filterForm.organizationCode = this.form.organizationCode
      this.filterForm.organizationName = this.form.organizationName
      this.queryItemList()
    },
    handleSelectionChange (selection) {
      this.selection = selection
    },
    addNewOne () {
      if (this.selection.length === 0) {
        this.$message({
          type: 'warning',
          message: this.$t('orderMod.msgOrder[8]')
        })
        return
      }
      // this.selection.forEach(item => {
      //   item.orderLineNum = item.lineNum
      // })
      // // 条件判断回调 送货通知和订单区分开
      // let condition = row => {
      //   if (this.form.orderSource === 'DELIVERY_NOTICE') return `${row.deliveryNoticeNumber}_${row.deliveryNoticeLineNum}`
      //   if (this.form.orderSource === 'PURCHASE_ORDER' && !row.deliveryNoticeNumber) return `${row.orderNumber}_${row.orderLineNum}`
      // }
      // // 行添加补充字段
      // let lineSet = row => {
      //   return {
      //     // orderLineNum: row.lineNum,
      //     deliveryQuantity: row.numberRemaining,
      //     deliverPlanNum: this.form.orderSource === 'DELIVERY_NOTICE' ? row.deliveryNoticeNumber : ''
      //   }
      // }

      // // 明细添加去重
      // setRepeatData(this.tableData, this.selection, condition, lineSet)
      this.selection.forEach(item => {
        this.tableData.push({ ...item, orderLineNum: item.lineNum, deliveryQuantity: item.numberRemaining, deliverPlanNum: this.form.orderSource === 'DELIVERY_NOTICE' ? item.deliveryNoticeNumber : '' })
      })
      this.doLayout()
      this.dialogFormVisible = false
    },
    // 设置明细行row-key
    setRowKey (row) {
      if (this.form.orderSource === 'DELIVERY_NOTICE') return `${row.deliveryNoticeNumber}_${row.deliveryNoticeLineNum}`
      if (this.form.orderSource === 'PURCHASE_ORDER' && !row.deliveryNoticeNumber) return `${row.orderNumber}_${row.orderLineNum}`
    },
    // 表格重置
    doLayout () {
      this.$nextTick(() => {
        console.log(this.$refs.tableRef, 'this.$refs.tableRef')
        this.$refs.tableRef.doLayout()
      })
      this.$forceUpdate()
    },
    addUploadOneApproval () {
      this.procurementFile.push({
        fileuploadId: null,
        fileSourceName: '',
        startDate: '',
        endDate: '',
        fileFunction: 'procurementFile'
      })
    },
    confirmDelivery () {
      this.$confirm(this.$t(this.$t('orderMod.isConfirmDelivery')), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          this.$http({
            url: '/api-sup-ce/order/deliveryNote/getAffirmDelivery',
            method: 'GET',
            params: { deliveryNoteId: this.form.deliveryNoteId },
            loading: true
          }).then((res) => {
            this.$message.success(this.$t('common.success'))
            this.cancelBill()
          })
        })
        .catch((e) => {
          console.log(e, 'err')
        })
    },
    // form验证返回promise校验返回trun or false
    formValidate (formRef) {
      return new Promise((resolve) => {
        this.$refs[formRef].validate((flag, obj) => {
          resolve({ flag, obj })
        })
      })
    },
    // 校验表单
    async getCheckForm () {
      const formFiled = await this.formValidate('formRef')

      if (!formFiled.flag && Object.keys(formFiled.obj).length > 0) {
        const warnObj = Object.keys(formFiled.obj)[0]
        return {
          flag: formFiled.flag,
          message: formFiled.obj[warnObj][0].message
        }
      }

      return { flag: true }
    },
    async saveBill (type) {
      const { flag, message } = await this.getCheckForm()
      console.log(flag, message, 'flag')
      if (!flag) return this.__focus_error__(message)
      if (!this.tableData.length) {
        return this.$message({
          type: 'error',
          message: this.$t('orderMod.msgVendorOrder[11]')
        })
      }
      for (let row of this.tableData) {
        // 送货单明细，保存的时候就扣减剩余未送货数量；（因为提交送货单会触发审批流，这里会需要长时间审批，到时候释放会来不及。所以要先扣减）
        let updateFlag = this.form.deliveryNumber /// 更新模式
        // 保存提示语
        if (!updateFlag && Number(row.deliveryQuantity) > Number(row.numberRemaining)) {
          return this.$message.warning(this.$t('orderMod.msgVendorOrder[10]'))
        }
        // 剩余送货数量大于0，送货数量大于剩余数量
        if (
          updateFlag &&
          Number(row.numberRemaining) > 0 &&
          Number(row.deliveryQuantity) > Number(row.numberRemaining)
        ) {
          return this.$message.warning(this.$t('orderMod.msgVendorOrder[10]'))
        }
        // 剩余送货数量为0，送货数量大于订单数量
        if (
          updateFlag &&
          Number(row.numberRemaining) == 0 &&
          Number(row.deliveryQuantity) > Number(row.orderNum)
        ) {
          return this.$message.warning(this.$t('orderMod.msgVendorOrder[10]'))
        }
      }

      const data = {
        deliveryNote: this.form,
        detailList: this.tableData,
        deliveryNoteWms: this.WMStableData,
        procurementFile: this.procurementFile
      }
      let res = await deliveryOrderApi.deliveryNoteSave(data)
      this.$message({
        type: 'success',
        message: res.message
      })
      this.form = res.data.deliveryNote
      this.tableData = res.data.detailList
      this.createdDelivery = true
      return res.data
    },
    saveAndGoTagConfirm (row) {
      if (this.isReadOnly) {
        this.saveAndGoTagHandel()
      } else {
        this.$confirm(this.$t('buyerDeliveryOrder.prompt1'), {
          confirmButtonText: this.$t('buyerDeliveryOrder.toBind'),
          cancelButtonText: this.$t('common.cancel'),
          type: 'warning'
        }).then(() => {
          this.saveAndGoTagHandel()
        }).catch(() => {})
      }
    },
    // 保存并保存条码
    async saveAndGoTagHandel () {
      let orderData = null
      if (this.form.deliveryNoteStatus == 'CREATE' && !this.isReadOnly) {
        let data = await this.saveBill('saveAndGoTag') // 保存单据
        orderData = data.deliveryNote
      } else {
        orderData = this.form
      }
      if (orderData) {
        this.goTagManage(orderData) // 跳转绑定条码
      }
    },
    goTagManage (row) {
      let params = {
        deliveryNumber: row.deliveryNumber,
        deliveryNoteId: row.deliveryNoteId,
        deliveryNoteStatus: row.deliveryNoteStatus
      }
      let name = params.deliveryNumber ?? ''
      let tab = {
        component: tagManage,
        params: {
          status: row.deliveryNoteStatus || 'CREATE',
          row: row || '',
          tabName: name ? 'tagManage' + name : 'tagManage'
        },
        title: this.$t('orderMod.buyerOrderSynergy.tagManage') + name,
        name: name ? 'tagManage' + name : 'tagManage'
      }
      if (this.form.deliveryNoteStatus == 'CREATE' && !this.isReadOnly) {
        this.cancelBill()
      }
      this.cancelBill()
      this.$emit('tab-add', tab)
    },
    outerHandleUploadSuccess (file, row) {
      const { fileId = '', fileName = '' } = file || {}
      row.fileuploadId = fileId.toString()
      row.fileSourceName = fileName
    },
    handleDelClick (index, row) {
      this.procurementFile.splice(index, 1)
    },
    printBill () {
      const xml = encodeURIComponent('database:database:送货单打印.ureport.xml')
      const params = encodeURIComponent(`param=${this.form.deliveryNumber}`)
      const url = `${this.$systemUrl}/#/pdfPrint?xml=${xml}&params=${params}`
      window.open(url, '_blank', 'noopener,noreferrer')
    },
    cancelBill () {
      if (this.$attrs.params.flag == 'add') {
        this.$emit('tab-remove', 'vendorDeliveryOrderDetail')
      } else {
        this.$emit('tab-remove', 'vendorDeliveryOrderDetail' + this.form.deliveryNumber)
      }
      this.__setTabTodo('vendorDeliveryOrderList.getQuerydata')
    },
    /**
     * 勾选送货单行
     */
    handleSelectionDeliveryLine (selection) {
      this.selectDeliveryLine = selection
    }
  }
}
</script>
<style scoped lang="scss">
.the-vendorGreenChannelDetail-detail {
  .sub_header {
    padding: 4px 11px;
    background: #eee;
  }

  .el-table .el-date-editor {
    width: 135px;
  }

  .btn_line {
    margin: 0 0 10px 0;
  }

  .importbtn :deep(.el-button) {
    min-width: 56px;
    height: 24px;
    line-height: 22px;
    font-size: 14px;
    border-radius: 2px;
    padding: 1px 14px;
  }

  .input-number-precision {
    width: 100%;
    :deep(.el-input__inner) {
      text-align: left;
      padding-left: 8px;
    }
  }

  .col-btn {
    display: flex;
    align-items: center;
    justify-content: flex-end;
  }
}
</style>
