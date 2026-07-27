<template>
  <el-container
    class="flex-container the_main_po_list"
    direction="vertical"
  >
    <el-main>
      <el-collapse
        v-model="activeDims"
        class="tab-form-style"
      >
        <el-collapse-item
          :title="$t('orderMod.buyerOrderSynergy.orderDetailsForm')"
          name="1"
        >
          <el-form
            ref="form"
            :rules="rules"
            :model="orderHead"
            label-width="80px"
            label-position="top"
            class="form-incontainer"
            :disabled="isReadOnly"
          >
            <el-row type="flex">
              <el-col>
                <!-- 订单号 -->
                <el-form-item
                  :label="$t('orderMod.buyerOrderSynergy.orderNumber2')"
                  :label-width="formLabelWidth"
                >
                  <el-input
                    v-model="orderHead.orderHeadNum"
                    disabled
                  />
                </el-form-item>
              </el-col>
              <el-col>
                <!-- 申请单号 -->
                <el-form-item
                  :label="$t('contractMod.applicationOrderNum')"
                  :label-width="formLabelWidth"
                >
                  <quick-search
                    :show-input="orderHead.requirementHeadNum"
                    show-key="requirementHeadNum"
                    :scope-data="orderHead"
                    name="scc_lgt_requirement_head_for_order"
                    @close-quicksearch="getApplyNumberObj"
                  />
                </el-form-item>
              </el-col>
              <el-col>
                <!-- 供应商名称 -->
                <el-form-item
                  :label="$t('common.vendorName')"
                  :label-width="formLabelWidth"
                  prop="vendorName"
                >
                  <quick-search
                    :show-input="orderHead.vendorName"
                    show-key="companyName"
                    :scope-data="orderHead"
                    name="scc_sup_company_info"
                    @close-quicksearch="getVendorObj"
                  />
                </el-form-item>
              </el-col>
              <el-col>
                <!-- 申请模板 -->
                <el-form-item
                  :label="$t('logisticsMod.applyTemplate')"
                  :label-width="formLabelWidth"
                >
                  <el-select
                    v-model="orderHead.templateCode"
                    disabled
                  >
                    <el-option
                      v-for="item in templateAllList"
                      :key="item.templateCode"
                      :label="item.templateName"
                      :value="item.templateCode"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row type="flex">
              <el-col>
                <!-- 业务模式 -->
                <el-form-item
                  :label="$t('logisticsMod.businessMode')"
                  :label-width="formLabelWidth"
                  prop="businessModeCode"
                >
                  <DictSelect
                    v-model="orderHead.businessModeCode"
                    code="BUSINESS_MODE"
                    disabled
                  />
                </el-form-item>
              </el-col>
              <el-col>
                <!-- 运输方式 -->
                <el-form-item
                  :label="$t('bid_mod.transportType')"
                  :label-width="formLabelWidth"
                  prop="transportModeCode"
                >
                  <DictSelect
                    v-model="orderHead.transportModeCode"
                    code="TRANSPORT_MODE"
                    disabled
                  />
                </el-form-item>
              </el-col>
              <el-col>
                <!-- 业务类型 -->
                <el-form-item
                  :label="$t('dataConfMod.businessType')"
                  :label-width="formLabelWidth"
                  prop="ceeaPurchaseType"
                >
                  <DictSelect
                    v-model="orderHead.businessType"
                    code="LOGISTICS_BUSINESS_TYPE"
                    @change="setBusinessType"
                  />
                </el-form-item>
              </el-col>
              <el-col>
                <!-- 服务项目名称 -->
                <el-form-item
                  :label="$t('logisticsMod.serviceProjectName')"
                  :label-width="formLabelWidth"
                >
                  <quick-search
                    :show-input="orderHead.serviceProjectName"
                    :disabled="orderHead.businessType == 'NOT_PROJECT'"
                    show-key="projectName"
                    :scope-data="orderHead"
                    name="ceea_logistics_project_info"
                    @close-quicksearch="getProjectObj"
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row type="flex">
              <el-col>
                <!-- 是否供应商确认 -->
                <el-form-item
                  :label="$t('purchaseDemand.ceeaIfSupplierConfirm')"
                  :label-width="formLabelWidth"
                >
                  <DictSelect
                    v-model="orderHead.ifNeedVendorComfirm"
                    code="YES_OR_NO"
                    disabled
                  />
                </el-form-item>
              </el-col>
              <el-col>
                <!-- 申请主题 -->
                <el-form-item
                  :label="$t('logisticsMod.requirementTitle')"
                  :label-width="formLabelWidth"
                >
                  <el-input v-model="orderHead.orderTitle" />
                </el-form-item>
              </el-col>
              <el-col>
                <!-- 价格有效开始日期 -->
                <el-form-item
                  :label="$t('logisticsMod.priceStartDate')"
                  :label-width="formLabelWidth"
                  prop="priceStartDate"
                >
                  <el-date-picker
                    v-model="orderHead.priceStartDate"
                    type="date"
                    value-format="yyyy-MM-dd"
                  />
                </el-form-item>
              </el-col>
              <el-col>
                <!-- 价格有效结束日期 -->
                <el-form-item
                  :label="$t('logisticsMod.priceEndDate')"
                  :label-width="formLabelWidth"
                  prop="priceEndDate"
                >
                  <el-date-picker
                    v-model="orderHead.priceEndDate"
                    type="date"
                    value-format="yyyy-MM-dd"
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row type="flex">
              <el-col>
                <el-form-item
                  :label="$t('orderMod.buyerOrderSynergy.buyerName')"
                  :label-width="formLabelWidth"
                >
                  <el-input
                    v-model="orderHead.ceeaApplyUserNickname"
                    disabled
                  />
                </el-form-item>
              </el-col>
              <el-col>
                <!-- 订单日期 -->
                <el-form-item
                  :label="$t('oneStopShopping.orderDate')"
                  :label-width="formLabelWidth"
                  prop="orderDate"
                  disabled
                >
                  <el-date-picker
                    v-model="orderHead.orderDate"
                    disabled
                    type="date"
                    value-format="yyyy-MM-dd"
                  />
                </el-form-item>
              </el-col>
              <el-col>
                <!-- 采购部门 -->
                <el-form-item
                  :label="$t('oneStopShopping.department')"
                  :label-width="formLabelWidth"
                >
                  <el-input
                    v-model="orderHead.purchaseDepartmentName"
                    disabled
                  />
                </el-form-item>
              </el-col>
              <el-col>
                <!-- 单据状态 -->
                <el-form-item
                  :label="$t('bidMod.billstatus')"
                  :label-width="formLabelWidth"
                >
                  <DictSelect
                    v-model="orderHead.orderStatus"
                    code="LOGISTICS_PRICE_STATUS"
                    disabled
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row type="flex">
              <el-col>
                <!-- 供应商编码 -->
                <el-form-item
                  :label="$t('common.vendorCode')"
                  :label-width="formLabelWidth"
                >
                  <el-input
                    v-model="orderHead.vendorCode"
                    disabled
                  />
                </el-form-item>
              </el-col>
              <el-col>
                <!-- 申请人 -->
                <el-form-item
                  :label="$t('purchaseDemand.applicant')"
                  :label-width="formLabelWidth"
                >
                  <el-input
                    v-model="orderHead.applyBy"
                    disabled
                  />
                </el-form-item>
              </el-col>
              <el-col>
                <!-- 申请部门 -->
                <el-form-item
                  :label="$t('purchaseDemand.ceeaDepartment')"
                  :label-width="formLabelWidth"
                >
                  <el-input
                    v-model="orderHead.applyDepartmentName"
                    disabled
                  />
                </el-form-item>
              </el-col>
              <el-col>
                <el-form-item
                  :label="$t('bid_mod.taxRate')"
                  :label-width="formLabelWidth"
                >
                  <DictSelect
                    v-model="orderHead.taxKey"
                    code="tax"
                    @change="setTaxObj"
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row type="flex">
              <el-col :span="6">
                <el-form-item
                  :label="$t('bid_mod.unit')"
                  :label-width="formLabelWidth"
                  prop="unit"
                >
                  <DictSelect
                    v-model="orderHead.unit"
                    code="LOGISTICS_UNIT"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <!-- 项目总量 -->
                <el-form-item
                  :label="$t('logisticsMod.projectTotal')"
                  :label-width="formLabelWidth"
                  prop="projectTotal"
                >
                  <el-input v-model="orderHead.projectTotal" />
                </el-form-item>
              </el-col>
              <!-- <el-col :span="6">
                <el-form-item label="供方是否提交技术标" :label-width="formLabelWidth" prop="ifVendorSubmitShipDate">
                    <el-select disabled v-model="orderHead.ifVendorSubmitShipDate">
                      <el-option
                        v-for="item in yesNoOptions"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value"
                      />
                    </el-select>
                  </el-form-item>
              </el-col> -->
              <el-col :span="12">
                <el-form-item
                  :label="$t('orderMod.buyerOrderSynergy.comments')"
                  :label-width="formLabelWidth"
                >
                  <el-input v-model="orderHead.comments" />
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </el-collapse-item>
        <!-- 合同信息 -->
        <el-collapse-item
          :title="$t('logisticsMod.contractInfo')"
          name="2"
        >
          <el-table
            :data="orderLineContractList"
            style="width: 100%"
            border
            max-height="250px"
          >
            <el-table-column
              align="center"
              type="index"
              :label="$t('purSettlementMod.tabindex')"
              width="50"
            />
            <!-- 合同编号 -->
            <el-table-column
              align="center"
              prop="contractCode"
              :label="$t('contractMod.contractNo')"
              width="200"
            >
              <template slot-scope="scope">
                <quick-search
                  :show-input="scope.row.contractCode"
                  show-key="contractCode"
                  :disabled="isReadOnly"
                  :scope-data="scope.row"
                  name="scc_contract_head_confirmed"
                  @close-quicksearch="getcontractObj"
                />
              </template>
            </el-table-column>
            <!-- 合同名称 -->
            <el-table-column
              align="center"
              prop="contractName"
              :label="$t('contractMod.contractName')"
              min-width="150"
            />
            <!-- 付款方式 -->
            <el-table-column
              align="center"
              prop="paymentMethod"
              :label="$t('paymentType.paymentWay')"
              width="150"
            >
              <template slot-scope="scope">
                <DictSelect
                  v-model="scope.row.paymentMethod"
                  code="PAYMENT_MODE"
                  :disabled="isReadOnly"
                />
              </template>
            </el-table-column>
            <!-- 付款条件 -->
            <el-table-column
              align="center"
              prop="paymentStage"
              :label="$t('paymentType.paymentTerm')"
              width="150"
            >
              <template slot-scope="scope">
                <el-select
                  v-model="scope.row.paymentStage"
                  :disabled="isReadOnly"
                >
                  <el-option
                    v-for="item in payExplainList"
                    :key="item.payTypeId"
                    :label="item.payExplain"
                    :value="item.payTypeId"
                  />
                </el-select>
              </template>
            </el-table-column>
            <!-- 账期 -->
            <el-table-column
              align="center"
              prop="accountDate"
              :label="$t('paymentType.paymentDay')"
              width="250"
            >
              <template slot-scope="scope">
                <DictSelect
                  v-model="scope.row.accountDate"
                  code="PAYMENT_PERIOD"
                  :disabled="isReadOnly"
                />
              </template>
            </el-table-column>
            <el-table-column
              :label="$t('common.operation')"
              width="60"
            >
              <template slot-scope="scope">
                <el-button
                  type="text"
                  :disabled="isReadOnly"
                  @click="handleDelClick(scope.$index, scope.row)"
                >
                  {{ $t("common.delete") }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-collapse-item>
        <!-- 行程明细 -->
        <el-collapse-item
          :title="$t('logisticsMod.itineraryDetail')"
          name="3"
        >
          <el-table
            ref="tableGrid"
            :data="requirementLineList"
            style="width: 100%"
            border
            :row-height="30"
            max-height="390px"
            highlight-current-row
          >
            <el-table-column
              fixed="left"
              align="center"
              type="index"
              :label="$t('contractMod.tabindex')"
              width="60"
            />
            <templateList
              ref="templateListId"
              :table-header="tableHeader"
              operate-flag-type="vendorOperateFlag"
              visible-flag-type="vendorVisibleFlag"
              :requirement-line-list="requirementLineList"
              :is-read-only="true"
            />
            <el-table-column
              :label="$t('common.operation')"
              fixed="right"
              width="60"
            >
              <template slot-scope="scope">
                <el-button
                  type="text"
                  @click="feeDetails(scope.$index, scope.row)"
                >
                  {{ $t("dataConfMod.detail") }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          <!-- 弹框区域-->
          <srm-dialog
            :title="$t('logisticsMod.chargeDetail')"
            size="large"
            :visible.sync="orderLineFeeShow"
            :close-on-click-modal="false"
          >
            <main-header
              :l-span="22"
              :r-span="2"
            >
              <template slot="left">
                <el-button
                  @click="backTo"
                >
                  {{
                    $t("common.backTo")
                  }}
                </el-button>
              </template>
            </main-header>
            <el-table
              :data="orderLineFeeList"
              height="300px"
              style="width: 100%"
              border
            >
              <el-table-column
                :label="$t('common.sort')"
                align="center"
                type="index"
                width="50"
              />
              <el-table-column
                align="center"
                prop="leg"
                label="LEG"
              >
                <template slot-scope="scope">
                  <DictSelect
                    v-model="scope.row.leg"
                    code="LEG"
                    :disabled="isReadOnly"
                  />
                </template>
              </el-table-column>
              <!-- 费项 -->
              <el-table-column
                align="center"
                prop="expenseItem"
                :label="$t('logisticsMod.expenseItem1')"
              >
                <template slot-scope="scope">
                  <el-select
                    v-model="scope.row.expenseItem"
                    :disabled="isReadOnly"
                  >
                    <el-option
                      v-for="item in scope.row.expenseItemList"
                      :key="item.id + 'ee'"
                      :label="item.chargeName"
                      :value="item.chargeCode"
                    />
                  </el-select>
                </template>
              </el-table-column>
              <!-- 计费方式 -->
              <el-table-column
                align="center"
                prop="chargeMethod"
                :label="$t('logisticsMod.chargeMethod')"
              >
                <template slot-scope="scope">
                  <DictSelect
                    v-model="scope.row.chargeMethod"
                    code="CHARGE_LEVEL"
                    :disabled="isReadOnly"
                  />
                </template>
              </el-table-column>
              <!-- 计费单位 -->
              <el-table-column
                align="center"
                prop="chargeUnit"
                :label="$t('logisticsMod.chargeUnit')"
              >
                <template slot-scope="scope">
                  <DictSelect
                    v-model="scope.row.chargeUnit"
                    code="SUB_LEVEL"
                    :disabled="isReadOnly"
                  />
                </template>
              </el-table-column>
              <!-- 最大收费 -->
              <el-table-column
                align="center"
                prop="maxCost"
                :label="$t('logisticsMod.maxCost')"
              >
                <template slot-scope="scope">
                  <el-input
                    v-model="scope.row.maxCost"
                    :disabled="isReadOnly"
                    type="number"
                  />
                </template>
              </el-table-column>
              <!-- 最小收费 -->
              <el-table-column
                align="center"
                prop="minCost"
                :label="$t('logisticsMod.minCost')"
              >
                <template slot-scope="scope">
                  <el-input
                    v-model="scope.row.minCost"
                    :disabled="isReadOnly"
                    type="number"
                  />
                </template>
              </el-table-column>
              <!-- 费用 -->
              <el-table-column
                align="center"
                prop="expense"
                :label="$t('bid_mod.costType')"
              >
                <template slot-scope="scope">
                  <el-input
                    v-model="scope.row.expense"
                    :disabled="isReadOnly"
                    type="number"
                  />
                </template>
              </el-table-column>
              <!-- 币制 -->
              <el-table-column
                align="center"
                prop="currency"
                :label="$t('logisticsMod.currencySystem')"
              >
                <template slot-scope="scope">
                  <DictSelect
                    v-model="scope.row.currency"
                    code="currency"
                    :disabled="isReadOnly"
                  />
                </template>
              </el-table-column>
            </el-table>
          </srm-dialog>
        </el-collapse-item>
        <!-- 技术标明细 -->
        <el-collapse-item
          v-if="orderHead.ifVendorSubmitShipDate == 'Y'"
          :title="$t('logisticsMod.techBidDetail')"
          name="5"
        >
          <p class="btn_line">
            <el-button
              type="primary"
              :disabled="isReadOnly"
              @click="addScheduleList"
            >
              {{ $t("common.add") }}
            </el-button>
          </p>
          <shipTableClumn
            ref="shipTableClumnId"
            :table-header="tableHeader"
            operate-flag-type="vendorOperateFlag"
            visible-flag-type="vendorVisibleFlag"
            :schedule-form="scheduleForm"
            :transport-flag="transportFlag"
            :is-read-only="true"
          />
        </el-collapse-item>
        <!-- 订单附件信息 -->
        <el-collapse-item
          :title="$t('orderMod.orderAttachInfo')"
          name="4"
        >
          <el-table
            :data="orderFileList"
            style="width: 100%"
            border
            max-height="250px"
          >
            <el-table-column
              align="center"
              type="index"
              :label="$t('purSettlementMod.tabindex')"
              width="50"
            />
            <!-- 附件名称 -->
            <el-table-column
              align="center"
              prop="fileName"
              :label="$t('bidMod.fileName')"
            >
              <template slot-scope="scope">
                <SrmCommonFile
                  :extra-data="fileInfo"
                  :default-file="{
                    fileId: scope.row.fileRelationId,
                    fileName: scope.row.fileName
                  }"
                  :readonly="false"
                  @on-change="({file}) => outerHandleUploadSuccess(file,scope.row)"
                />
              </template>
            </el-table-column>
            <!-- 上传人 -->
            <el-table-column
              align="center"
              prop="createdUserName"
              :label="$t('components.fileupload.uploadUserName')"
            />
            <!-- 上传时间 -->
            <el-table-column
              align="center"
              prop="creationDate"
              :label="$t('components.fileupload.uploadDate')"
            />
            <el-table-column
              :label="$t('common.operation')"
              width="80"
            >
              <template slot-scope="scope">
                <el-button
                  type="text"
                  :disabled="isReadOnly"
                  @click="handleDelClick2(scope.$index, scope.row)"
                >
                  {{ $t("common.delete") }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-collapse-item>
        <!-- 驳回记录 -->
        <el-collapse-item
          :title="$t('logisticsMod.rejectRecord')"
          name="6"
        >
          <el-table
            :data="orderRejectRecordList"
            style="width: 100%"
            border
            max-height="250px"
          >
            <el-table-column
              align="center"
              type="index"
              :label="$t('purSettlementMod.tabindex')"
              width="50"
            />
            <!-- 驳回原因 -->
            <el-table-column
              align="center"
              prop="rejectReason"
              min-width="150"
              :label="$t('contractMod.rejectReason')"
            />
            <!-- 驳回人账号 -->
            <el-table-column
              align="center"
              prop="rejectUsername"
              :label="$t('logisticsMod.rejectUsername')"
            />
            <!-- 驳回人 -->
            <el-table-column
              align="center"
              prop="rejectNickname"
              :label="$t('logisticsMod.rejectNickname')"
            />
            <!-- 驳回时间 -->
            <el-table-column
              align="center"
              prop="creationDate"
              :label="$t('logisticsMod.rejectDate')"
            />
          </el-table>
        </el-collapse-item>
        <!-- <el-collapse-item title="操作日志" name="6">
          <el-table
            :data="operationLogList"
            style="width: 100%"
            border
            max-height="250px"
          >
            <el-table-column
              align="center"
              type="index"
              :label="$t('purSettlementMod.tabindex')"
              width="50"
            />
            <el-table-column
              align="center"
              prop="attachName"
              label="操作人"
              width="150"/>
            <el-table-column
              align="center"
              prop="createdUserName"
              label="操作日期"
              width="150"
            />
            <el-table-column
              align="center"
              prop="creationDate"
              label="原因"
              min-width="250"
            />
          </el-table>
        </el-collapse-item> -->
        <!-- 请输入拒绝原因 -->
        <srm-dialog
          :visible.sync="rejectReasonVisible"
          :title="$t('contractMod.msgRefuseReason')"
          size="middle"
        >
          <el-form
            ref="form3"
            class="tableForm"
            :model="form"
            :rules="rules"
            :show-message="false"
          >
            <el-form-item prop="rejectReason">
              <el-input
                v-model="form.rejectReason"
                type="textarea"
                :rows="2"
                :placeholder="$t('common.pleaseTypeContents')"
              />
            </el-form-item>
          </el-form>
          <template
            #footer
            class="dialog-footer"
          >
            <el-button
              type="primary"
              @click="rejectReasonConfirm"
            >
              {{ $t("common.confirm") }}
            </el-button>
            <el-button @click="rejectReasonVisible = false">
              {{ $t("common.cancel") }}
            </el-button>
          </template>
        </srm-dialog>
      </el-collapse>

      <c-toolbar>
        <template
          v-if="!isOnlyRead"
          slot="right"
        >
          <el-button
            v-if="!isReadOnly"
            @click="cancelBill"
          >
            {{
              this.$t("common.cancel")
            }}
          </el-button>
          <el-button
            v-else-if="isReadOnly"
            @click="cancelBill"
          >
            {{
              this.$t("common.close")
            }}
          </el-button>
          <el-button
            type="primary"
            @click="printBill"
          >
            {{
              $t("common.pdfPrint")
            }}
          </el-button>
          <el-button
            v-if="orderHead.orderStatus == 'WAITING_VENDOR_CONFIRM'"
            type="primary"
            @click="submitVendorConfirm"
          >
            {{ $t("orderMod.accept") }}
          </el-button>
          <el-button
            v-if="orderHead.orderStatus == 'WAITING_VENDOR_CONFIRM'"
            type="primary"
            @click="refuse"
          >
            {{ $t("common.refused") }}
          </el-button>
        </template>
      </c-toolbar>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoMixin } from '@/utils/mixins'
import MainHeader from 'lib@/components/Table/MainHeader'
import CPagination from 'lib@/components/c-pagination'
import { parseTime } from '@/utils'
import OrganizationSelector from 'lib@/components/organization-selector'
import QuickSearch from 'lib@/components/QuickSearch' // 快速查询组件
import CToolbar from 'lib@/components/c-toolbar'
import MImport from 'lib@/components/import'
import { downloadFileLink } from 'lib@/utils/file'
import templateList from '../logisticsPurchaseApply/templateList'
import shipTableClumn from '../logisticsPurchaseOrder/shipTableClumn'
import { createDictClass } from '@/library/utils/dict/dict-utils'

const dictClass = createDictClass({ 'tax': [] })

export default {
  name: 'LogisticsPurchaseOrderDetail',
  components: {
    templateList,
    shipTableClumn,
    MainHeader,
    QuickSearch,
    CToolbar,
    MImport,
    OrganizationSelector,
    CPagination
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      dictClass: dictClass,
      exchange: false,
      ceeaOrgAbled: '',
      // 文件上传配置信息
      fileInfo: {
        uploadType: 'FASTDFS', // 固定参数
        sourceType: 'WEB_APP', // 固定参数
        fileModular: 'sup', // 文件所属模块 -》基础模块
        fileFunction: 'vendorBiddingManagement', // 文件所属功能
        fileType: 'images' // 文件所属类型
      },
      upLoadUrl: '/api-sup-ce/po/orderDetail/importExcel',
      extraData: {},
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
      zeroPriceCheck: false,
      priceAdjustmentTable: [],
      tableName: '',
      queryTotal: -1,
      viewSize: 10,
      viewIndex: 1,
      globalUserId: null,
      addressList: [],
      addressListShip: [],
      addressListBill: [],
      selection: [],
      scheduleList: [],
      orderFileList: [],
      orderRejectRecordList: [],
      tableHeader: [],
      requirementLineList: [],
      operationLogList: [],
      orderLineContractList: [],
      approvalFileList: [],
      displayMaterialItem: [],
      displayMaterialItem2: [],
      globalOrderNum: null,
      canEdit: false,
      ceeaFormulaResultData: [],
      ceeaFormulaValueData: '',
      orderHead: {
        ceeaOrgId: null,
        ceeaOrgCode: null,
        ceeaOrgName: null,
        orderId: null,
        orderHeadNum: null,
        ifVendorSubmitShipDate: null,
        orderStatus: 'DRAFT',
        orderDate: parseTime(new Date(), '{y}-{m}-{d}'),
        orderType: null,
        ifNeedVendorComfirm: null,
        ceeaApplyUserNickname: null,
        comments: null,
        vendorId: null,
        vendorCode: null,
        vendorName: null,
        serviceProjectCode: null,
        serviceProjectName: null,
        requirementHeadNum: null,
        applyBy: null,
        applyDepartmentName: null,
        ceeaTaxAmount: null // 合计金额含税
      },
      maxConversionRate: '',
      minConversionRate: '',
      isSeaFoodFormula: '',
      ceeaCostTypeList: [],
      queryParams: {},
      isDisabled: this.$attrs.params.flag == 'edit',
      formLabelWidth: '120px',
      formLabelWidth2: '120px',
      tableData: [],
      orderLineList: [],
      isModify: false,
      activeDims: ['1', '2', '3', '4', '5', '6'],
      canOperate: false,
      currentRows: [],
      rateName: '',
      organizatTaxList: [],
      visible: false,
      conversionRate: '',
      loading: false,
      modalVisible: false,
      rejectReasonVisible: false,
      addItemData: {},
      setBaseMaterialPrice: null,
      payExplainList: [],
      priceAdjustmentData: [],
      priceAdjustmentDataPush: [],
      form: {
        rejectReason: null
      },
      rules: {
        vendorName: [
          { required: true, message: this.$t('vendorMod.msgVendor') }
        ], // 请选择供应商
        priceStartDate: [
          {
            required: true,
            message: this.$t('logisticsMod.msgPurchaseApply[6]')
          }
        ], // 请选择价格有效开始日期
        priceEndDate: [
          {
            required: true,
            message: this.$t('logisticsMod.msgPurchaseApply[7]')
          }
        ], //  请选择价格有效结束日期
        rejectReason: [
          { required: true, message: this.$t('contractMod.msgRefuseReason') }
        ]
      },
      orderLineFeeList: [],
      orderLineFeeShow: false
    }
  },
  computed: {
    isOnlyRead () {
      // 兼容页面禁用按钮 by Easion
      return this.$attrs.params.isOnlyRead
    },
    isReadOnly () {
      return (
        this.orderHead.orderStatus !== 'DRAFT' &&
        this.orderHead.orderStatus !== 'WAITING_CONFIRM'
      )
    },
    transportFlag () {
      // 陆运、铁运
      if (
        ['LAND_TRANSPORT', 'RAILWAY_TRANSPORT'].includes(
          this.orderHead.transportModeCode
        )
      ) {
        return true
      } else {
        return false
      }
    }
  },
  /* watch: {
    form(n, o) {
      const { vendorId, organizationId } = n;
      const { vendorId_o, organizationId_o } = o;
      if (vendorId !== vendorId_o || organizationId !== organizationId_o) {
        this.extraData = { vendorId, organizationId };
        console.log(this.extraData);
      }
    }
  }, */
  created () {
    this.getPaylist()
    this.getTemplateList()
  },
  mounted () {
    const { row, flag, tableName } = this.$attrs.params
    this.tableName = tableName
    // this.isOnlyRead = this.$attrs.params;
    this.globalUserId = this.$store.getters.userInfo.userId
    // console.log("row", row);
    if (flag === 'add') {
      this.canEdit = true
      const {
        companyId,
        phone,
        nickname,
        department
      } = this.$store.getters.user.userInfo
      // console.log("this.$store.getters.user", this.$store.getters.user);
      this.orderHead.tel = phone
      this.orderHead.ceeaApplyUserNickname = nickname
      this.orderHead.purchaseDepartmentName = department
    }
    if (flag == 'edit' || flag == 'readOnly') {
      const { orderHeadId } = this.$attrs.params.row
      this.getDetails(orderHeadId)
    }
  },
  methods: {
    getDetails (id) {
      this.$http({
        url: '/api-pd/po/order-head/getByHeadId',
        method: 'get',
        params: { id: id },
        loading: true
      }).then(res => {
        this.orderHead = res.data.orderHead
        this.getTemplate(this.orderHead.templateHeadId)
        this.orderLineContractList = res.data.orderLineContractList
        this.requirementLineList = res.data.orderLineList.map(i => ({
          ...i,
          provinceList: [
            {
              value: i.fromProvinceCode,
              label: i.fromProvince
            }
          ],
          endProvinceList: [
            {
              value: i.toProvinceCode,
              label: i.toProvince
            }
          ],
          startCityList: [
            {
              value: i.fromCityCode,
              label: i.fromCity
            }
          ],
          startCountyList: [
            {
              value: i.fromCountyCode,
              label: i.fromCounty
            }
          ],
          endCityList: [
            {
              value: i.toCityCode,
              label: i.toCity
            }
          ],
          endCountyList: [
            {
              value: i.toCountyCode,
              label: i.toCounty
            }
          ]
        }))
        this.orderFileList = res.data.orderFileList
        this.orderRejectRecordList = res.data.orderRejectRecordList
        this.scheduleForm.scheduleList = res.data.orderLineShipList.map(i => ({
          ...i,
          provinceList: [
            {
              value: i.fromProvinceCode,
              label: i.fromProvince
            }
          ],
          endProvinceList: [
            {
              value: i.toProvinceCode,
              label: i.toProvince
            }
          ],
          startCityList: [
            {
              value: i.fromCityCode,
              label: i.fromCity
            }
          ],
          startCountyList: [
            {
              value: i.fromCountyCode,
              label: i.fromCounty
            }
          ],
          endCityList: [
            {
              value: i.toCityCode,
              label: i.toCity
            }
          ],
          endCountyList: [
            {
              value: i.toCountyCode,
              label: i.toCounty
            }
          ]
        }))
      })
    },
    getPaylist () {
      let data = { pageNum: 1, pageSize: 1000 }
      this.$http({
        url: '/api-cm/template/payType/paymentTermsPage',
        method: 'POST',
        data: data,
        loading: true
      }).then(res => {
        // console.log("res",res)
        if (res) {
          let payExplainList = res.data.list || []
          payExplainList.forEach(item => {
            item.payTypeId = String(item.payTypeId)
          })
          this.payExplainList = payExplainList
        }
      })
    },
    formatDate (val) {
      return val ? this.$dayjs(val).format('YYYY-MM-DD') : val
    },
    handleSuccess ({ data }, file, fileList) {
      if (data && data.length) {
        const l = this.tableData.length
        data.forEach((i, lineNum) =>
          this.tableData.push({ ...i, lineNum: l + lineNum + 1 })
        )
      }
    },
    downloadTemplate () {
      downloadFileLink(
        '/api-sup-ce/po/orderDetail/downloadTemplate',
        this.$t('orderMod.purchaseOrderImp') + `${new Date().getTime()}.xls`
      )
    },
    getVendorObj (val, scope) {
      scope.vendorId = val ? val.companyId : ''
      scope.vendorCode = val ? val.companyCode : ''
      scope.vendorName = val ? val.companyName : ''
    },
    getApplyNumberObj (val, scope) {
      scope.requirementHeadId = val ? val.requirementHeadId : ''
      scope.requirementHeadNum = val ? val.requirementHeadNum : ''
      scope.templateCode = val ? val.templateCode : ''
      scope.templateHeadId = val ? val.templateHeadId : ''
      scope.templateName = val ? val.templateName : ''
      scope.businessModeCode = val ? val.businessModeCode : ''
      scope.transportModeCode = val ? val.transportModeCode : ''
      scope.ifVendorSubmitShipDate = val ? val.ifVendorSubmitShipDate : ''
      scope.applyBy = val ? val.applyBy : ''
      scope.applyDepartmentName = val ? val.applyDepartmentName : ''
      if (val && val.templateHeadId) {
        this.getFormDetail(val.requirementHeadId)
        this.getTemplate(val.templateHeadId)
      }
    },
    getFormDetail (requirementHeadId) {
      this.$http({
        url: '/api-pd/pr/requirement-head/getByHeadId',
        method: 'GET',
        params: { requirementHeadId: requirementHeadId },
        loading: true
      })
        .then(data => {
          if (data.data) {
            this.orderHead.ifNeedVendorComfirm =
              data.data.logisticsTemplateHead.ifNeedVendorSubmit
            this.requirementLineList = data.data.requirementLineList
            this.requirementLineList.map(i => (i.orderLineFeeList = []))
          }
        })
        .catch(err => {
          console.log(err)
        })
    },
    getTemplate (templateHeadId) {
      this.$http({
        url:
          '/api-pd/logistics/logistics-template-head/listTemplateLinesByHeadId',
        method: 'GET',
        params: { headId: templateHeadId },
        loading: true
      })
        .then(data => {
          if (data.data) {
            this.tableHeader = data.data.templateLines
          }
        })
        .catch(err => {
          console.log(err)
        })
    },
    getCurrencyObj (val, scope) {
      // console.log("val", val, "scope", scope);
      scope.currencyId = val ? val.currencyId : ''
      scope.currencyCode = val ? val.currencyCode : ''
      scope.currencyName = val ? val.currencyName : ''
      // if(val) {
      //   this.fromCurrency = val.currencyCode;
      //   this.getExchangeRate(this.fromCurrency, this.toCurrency);
      // }
    },
    getTemplateList () {
      this.$http({
        url:
          '/api-pd/logistics/logistics-template-head/listPageByParam',
        method: 'POST',
        data: {
          pageNum: 1,
          pageSize: 15,
          status: 'EFFECTIVE'
        },
        loading: true
      })
        .then(data => {
          this.templateAllList = data.data.list
        })
        .catch(err => {
          console.log(err)
        })
    },
    setBusinessType (val) {
      if (val === 'NOT_PROJECT') {
        this.orderHead.serviceProjectCode = null
        this.orderHead.serviceProjectName = null
      }
    },
    getProjectObj (val, scope) {
      scope.serviceProjectCode = val ? val.projectCode : ''
      scope.serviceProjectName = val ? val.projectName : ''
      scope.unit = val ? val.unit : ''
      scope.projectTotal = val ? val.projectTotal : ''
    },
    addUploadOneApproval () {
      this.approvalFileList.push({
        fileuploadId: null,
        attachName: '',
        startDate: '',
        endDate: '',
        orderAttachType: 'APPROVAL'
      })
    },
    addcontractPayList () {
      this.orderLineContractList.push({
        contractId: null,
        contractName: null,
        contractCode: null,
        paymentMethod: null,
        paymentStage: null
      })
    },
    addScheduleList () {
      this.scheduleList.push({})
    },
    addUploadOneOrder () {
      this.orderFileList.push({
        fileuploadId: null,
        attachName: '',
        startDate: '',
        endDate: '',
        orderAttachType: 'ORDER'
      })
    },
    outerHandleUploadSuccess (file, row) {
      const { fileId = '', fileName = '', createdBy = '', creationDate = '' } = file || {}
      row.fileRelationId = fileId.toString()
      row.fileName = fileName
      row.createdBy = createdBy
      row.creationDate = creationDate
      this.orderFileList.push({})
      this.orderFileList.splice(this.orderFileList.length - 1, 1)
    },
    handleDelClick (index, row) {
      this.orderLineContractList.splice(index, 1)
    },
    handleDelClick3 (index, row) {
      this.scheduleList.splice(index, 1)
    },
    handleDelClick2 (index, row) {
      if (row.fileRelationId) {
        this.$http({
          url: '/api-file/file/fileupload/delete',
          method: 'POST',
          data: { id: row.fileRelationId },
          loading: true
        }).then(res => {
          this.orderFileList.splice(index, 1)
        })
      } else {
        this.orderFileList.splice(index, 1)
      }
    },
    deleteOne (row) {
      this.tableData.splice(row.$index, 1)
    },
    setTaxObj (val) {
      // taxKey 税码  taxCode 税率   ceeaTaxKey 税码  ceeaTaxRate 税率
      // const taxKObj = this.taxList.find(i => i.value === val) || {};
      const dRowObj = this.dictClass.getDictDetail('tax', val)
      this.orderHead.taxRate = dRowObj.key
    },
    submitVendorConfirm (row) {
      this.$http({
        url: '/api-pd/po/order-head/submitVendorConfirm',
        method: 'GET',
        params: { orderHeadId: this.orderHead.orderHeadId },
        loading: true
      })
        .then(data => {
          this.$message({
            type: 'success',
            message: data.message
          })
          this.cancelBill()
          this.__setTabTodo('logisticsPurchaseApplyList.getQuerydata')
        })
        .catch(err => {
          console.log(err)
        })
    },
    refuse (row) {
      this.rejectReasonVisible = true
    },
    rejectReasonConfirm () {
      this.$refs.form3.validate(valid => {
        if (valid) {
          this.rejectReasonVisible = false
          this.$http({
            url: '/api-pd/po/order-head/refuse',
            method: 'post',
            data: {
              orderHeadId: this.orderHead.orderHeadId,
              rejectReason: this.form.rejectReason
            },
            loading: true
          })
            .then(data => {
              this.$message({
                type: 'success',
                message: data.message
              })
              this.cancelBill()
              this.__setTabTodo('logisticsPurchaseApplyList.getQuerydata')
            })
            .catch(err => {
              console.log(err)
            })
        } else {
          this.$message({
            message: this.$t('vendorMod.pleasefinishRequired'), // '请输入单据必填信息'
            type: 'error'
          })
          return false
        }
      })
    },
    getcontractObj (val, scope) {
      scope.contractCode = val.contractCode
      scope.contractName = val.contractName
    },
    getPortObj (val, scope) {
      scope.fromPortId = val.portId
      scope.fromPortCode = val.portCode
      scope.fromPortName = val.portNameZhs
    },
    getPortObj2 (val, scope) {
      scope.toPortId = val.portId
      scope.toPortCode = val.portCode
      scope.toPortName = val.portNameZhs
    },
    cancelBill () {
      this.$emit('tab-remove', this.tableName)
    },
    backTo () {
      this.orderLineFeeShow = false
    },
    feeDetails (index, row) {
      this.orderLineFeeShow = true
      this.orderLineFeeList = row.orderLineFeeList
    },
    handleItemSelection (val) {
      this.multipleSelection = val
    },
    save () {},
    printBill () {
      if (!window.location.origin) {
        window.location.origin =
          window.location.protocol +
          '//' +
          window.location.hostname +
          (window.location.port ? ':' + window.location.port : '')
      }
      const xml = encodeURIComponent(
        'database:database:采购订单打印.ureport.xml'
      )
      const params = encodeURIComponent(`param=${this.orderHead.orderNumber}`)
      const url = `${
        window.location.origin
      }/#/pdfPrint?xml=${xml}&params=${params}`
      window.open(url)
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
}
</style>
