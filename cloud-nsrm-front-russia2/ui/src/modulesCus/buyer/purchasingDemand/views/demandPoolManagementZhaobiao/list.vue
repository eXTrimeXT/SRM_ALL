<template>
  <el-container
    class="flex-container the_demandPoolManagementList_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        ref="formRef"
        :form-array="preArr"
        :pre-form-obj="preFormObj"
        @getFormData="getQuerydata"
      />

      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <AuthorityButton
            code="supCe:demandPoolManagementZhaobiao:assign"
            type="primary"
            @click="openAssignOne"
          >
            {{ $t('purchaseDemand.distributionOrTransfer') }}
          </AuthorityButton>
          <!-- <AuthorityButton
            @click="rejectOne"
          >
            供应商推荐
          </AuthorityButton> -->
          <!-- 招标计划公示 -->
          <AuthorityButton
            code="supCe:demandPoolManagementZhaobiao:announce"
            @click="createInquiry"
          >
            {{ $t('cusEntry.supplement20250121.bidPlan') }}
          </AuthorityButton>
          <!-- 拟定标书 -->
          <!-- <AuthorityButton
            @click="createInquiryBiaoshu"
          >
            拟定标书
          </AuthorityButton> -->
          <!-- 招标计划取消 -->
          <AuthorityButton
            code="supCe:demandPoolManagementZhaobiao:planCancel"
            @click="requirementCancellation"
          >
            {{ $t('cusEntry.supplement20250121.biddingPlanCancel') }}
          </AuthorityButton>

          <!-- <AuthorityButton
            code="poolZhaobiao:biangeng"
            @click="changeInquiry"
          >
            需求变更
          </AuthorityButton> -->

          <!-- 导出 -->
          <ExportExcel
            type="default"
            pageUrl="/api-sup-ce/api-ql/PrSouRequirementPoolForBuyer/querySouPool"
            :tableHeader="tableHeader"
            :dictCodes="dictCodes"
            :generateMeiQLExportRequest="generateMeiQLExportRequest"
            exportMode="front"
            exportType="meiqlApi"
            code="supCe:demandPoolManagementZhaobiao:export"
          />
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        big-data
        :table-data="tableData"
        :table-header="tableHeader"
        :page-size="pageSize"
        :current-change="handleCurrentChange"
        :checkbox="true"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :adept-mei-ql="true"
        url="/api-sup-ce/api-ql/PrSouRequirementPoolForBuyer/querySouPool"
        :checkChange="checkChange"
        @getFooter="getFooter"
        @getFooterSize="getFooterSize"
      >
        <template #techGroupFullName="{ scope }">
          {{ scope.row?.techGroupUsername }} {{ scope.row?.techGroupFullName }}
        </template>
        <template #vendorGroupFullName="{ scope }">
          {{ scope.row?.vendorGroupUsername }} {{ scope.row?.vendorGroupFullName }}
        </template>
        <template #souGroupFullName="{ scope }">
          {{ scope.row?.souGroupUsername }} {{ scope.row?.souGroupFullName }}
        </template>
        <template #fileInfos="{ scope }">
          <template>
            <div class="download-link-wrap">
              <SrmCommonFile
                :default-file="{
                  fileId: scope.row?.requireProductFileId,
                  fileName: scope.row?.requireProductFileName
                }"
                :readonly="true"
              />
            </div>
          </template>
        </template>
        <template #fileInfos2="{ scope }">
          <template>
            <div class="download-link-wrap">
              <SrmCommonFile
                :default-file="{
                  fileId: scope.row?.deliveryDayFileId,
                  fileName: scope.row?.deliveryDayFileName
                }"
                :readonly="true"
              />
            </div>
          </template>
        </template>
        <template #fileInfos3="{ scope }">
          <template>
            <div class="download-link-wrap">
              <SrmCommonFile
                :default-file="{
                  fileId: scope.row?.putIntoUseDateFileId,
                  fileName: scope.row?.putIntoUseDateFileName
                }"
                :readonly="true"
              />
            </div>
          </template>
        </template>
      </TableView>
      <srm-dialog
        :title="$t('purchaseDemand.assignBuyer')"
        size="small"
        :visible.sync="dialogFormVisible1"
        :close-on-click-modal="false"
      >
        <el-form
          ref="form"
          :model="form"
          class="form-incontainer"
          :rules="rules"
          label-width="80px"
          label-position="top"
        >
          <el-row>
            <el-col :span="24">
              <!-- 请选择供应商专家 -->
              <el-form-item
                :label="$t('cusEntry.supplement20250121.vendorExpert')"
                :label-width="formLabelWidth"
              >
                <QuickSearch
                  :show-input="form.nickname"
                  show-key="nickname"
                  :scope-data="form"
                  name="scc_rbac_user_display"
                  @close-quicksearch="getUserObj10"
                />
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <!-- 请选择招标专家 -->
              <el-form-item
                :label="$t('cusEntry.supplement20250121.bidExpert')"
                :label-width="formLabelWidth"
              >
                <QuickSearch
                  :show-input="form.nickname2"
                  show-key="nickname"
                  :scope-data="form"
                  name="scc_rbac_user_display"
                  @close-quicksearch="getUserObj11"
                />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>

        <div
          slot="footer"
          class="dialog-footer"
        >
          <el-button @click="dialogFormVisible1 = false">
            {{ $t('common.cancel') }}
          </el-button>
          <el-button
            type="primary"
            @click="assignOne"
          >
            {{ $t('common.confirm') }}
          </el-button>
        </div>
      </srm-dialog>

      <!--创建寻源单据-->
      <srm-dialog
        :title="$t('purchaseDemand.createInquiry')"
        size="small"
        :visible.sync="dialogFormVisible2"
        :close-on-click-modal="false"
      >
        <div>
          <span style="padding-right:11px">{{ $t('purchaseDemand.globalSourceTypeTips') }}</span>
          <el-select v-model="globalSourceType">
            <el-option
              v-for="item in sourceTypeList"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </div>
        <div
          slot="footer"
          class="dialog-footer"
        >
          <el-button
            type="primary"
            @click="submitOrionOrder"
          >
            {{ $t('common.confirm') }}
          </el-button>
        </div>
      </srm-dialog>

      <!-- 创建采购订单 -->
      <srm-dialog
        :title="$t('purchaseDemand.createOrder')"
        size="xLarge"
        :visible.sync="dialogFormVisible3"
        :close-on-click-modal="false"
      >
        <el-form
          ref="requirementHead"
          :model="requirementHead"
          label-width="80px"
        >
          <el-row type="flex">
            <el-col style="width:19%">
              <!-- 批量创建订单 -->
              <el-button
                type="primary"
                @click="submitOneItem3"
              >
                {{
                  $t('purchaseDemand.createOrdersBulk')
                }}
              </el-button>
              <!-- 返回 -->
              <el-button
                type="primary"
                @click="saveOneItem3"
              >
                {{ $t('common.backTo') }}
              </el-button>
              <!-- <span style="color:red;display:block;padding:8px;"
                >* {{ $t("purchaseDemand.contactNoTips") }}</span
              > -->
            </el-col>

            <!-- 是否供应商确认 -->
            <el-col :span="3">
              <el-form-item
                :label="$t('purchaseDemand.ceeaIfSupplierConfirm')"
                label-width="80"
              >
                <el-checkbox
                  v-model="requirementHead.ceeaIfSupplierConfirm"
                  true-label="Y"
                  false-label="N"
                  @change="changeSupplierConfirm"
                />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>

        <el-table
          ref="createOrderRef"
          :data="purOrderList"
          style="width: 100%"
          border
          height="345px"
          @selection-change="setCurrentRows2"
        >
          <el-table-column
            type="selection"
            width="55"
            fixed="left"
          />
          <!-- 序号 -->
          <el-table-column
            type="index"
            width="45"
            align="center"
            :label="$t('common.sort')"
            fixed="left"
          />
          <!-- 库存组织 -->
          <el-table-column
            align="center"
            width="130"
            prop="organizationName"
            :label="$t('purchaseDemand.invOrg')"
            show-overflow-tooltip
            fixed="left"
          />
          <!-- 物料编码 -->
          <el-table-column
            align="center"
            width="100"
            prop="materialCode"
            :label="$t('purchaseDemand.itemCode')"
            show-overflow-tooltip
            fixed="left"
          />
          <!-- 物料名称 -->
          <el-table-column
            align="center"
            min-width="130"
            prop="materialName"
            :label="$t('purchaseDemand.itemName')"
            show-overflow-tooltip
            fixed="left"
          />
          <!-- 单位 -->
          <el-table-column
            align="center"
            min-width="80"
            prop="unit"
            :label="$t('bid_mod.unit')"
            show-overflow-tooltip
            fixed="left"
          />
          <!-- 需求数量 -->
          <el-table-column
            width="80"
            align="center"
            :label="$t('purchaseDemand.requirementQuantity')"
            prop="requirementQuantity"
          />
          <!-- 需求日期 -->
          <el-table-column
            align="center"
            width="140"
            prop="requirementDateBuff"
            :label="$t('purchaseDemand.requirementDate')"
            show-overflow-tooltip
          >
            <template slot-scope="scope">
              {{$parseTime(scope.row.requirementDateBuff)}}
            </template>
          </el-table-column>
          <!-- 可下单数量 -->
          <el-table-column
            align="center"
            width="100"
            prop="orderQuantity"
            :label="$t('purchaseDemand.orderQuantity')"
            show-overflow-tooltip
          />
          <!-- 配额比例(%) -->
          <el-table-column
            align="center"
            width="80"
            prop="quotaProportion"
            :label="$t('purchaseDemand.quota')"
            show-overflow-tooltip
          >
            <template slot-scope="scope">
              <el-input
                v-model="scope.row.quotaProportion"
                v-input-format="{ type: 'float', digits: 4, negative: false }"
                @change="setQuantity(scope.row, ++scope.$index)"
              />
            </template>
          </el-table-column>
          <!-- 本次下单数量 -->
          <el-table-column
            align="center"
            width="100"
            prop="thisOrderQuantity"
            :label="$t('purchaseDemand.thisOrderQuantity')"
            show-overflow-tooltip
          >
            <template slot-scope="scope">
              <el-input
                v-model="scope.row.thisOrderQuantity"
                v-input-format="{ type: 'float' }"
                @change="setPortation(scope.row, ++scope.$index)"
              />
            </template>
          </el-table-column>
          <!-- 要求到货日期 -->
          <el-table-column
            align="center"
            width="150"
            prop="requirementDate"
            :label="$t('purchaseDemand.requirementDate1')"
            show-overflow-tooltip
          >
            <template slot-scope="scope">
              <el-date-picker
                v-model="scope.row.requirementDate"
                type="date"
                :format="$formatDatePicker"
                value-format="yyyy-MM-dd"
              />
            </template>
          </el-table-column>
          <!-- 是否供应商确认 -->
          <el-table-column
            align="center"
            width="80"
            prop="ceeaIfSupplierConfirm"
            :label="$t('purchaseDemand.ceeaIfSupplierConfirm')"
            show-overflow-tooltip
          >
            <template slot-scope="scope">
              <el-checkbox
                v-model="scope.row.ceeaIfSupplierConfirm"
                true-label="Y"
                false-label="N"
              />
            </template>
          </el-table-column>
          <!-- 供应商名称 -->
          <el-table-column
            align="center"
            min-width="130"
            prop="vendorName"
            :label="$t('purchaseDemand.vendorName')"
            show-overflow-tooltip
          />
          <!-- 订单类型 -->
          <el-table-column
            align="center"
            width="100"
            prop="ceeaPriceSourceType"
            :label="$t('purchaseDemand.purchaseType')"
            show-overflow-tooltip
          >
            <template slot-scope="scope">
              {{ scope.row.ceeaPurchaseTypeName }}
            </template>
          </el-table-column>
          <!-- 已下单数量 -->
          <el-table-column
            align="center"
            width="80"
            prop="ceeaExecutedQuantity"
            :label="$t('purchaseDemand.ceeaExecutedQuantity')"
            show-overflow-tooltip
          />
          <!-- 含税单价 -->
          <el-table-column
            align="center"
            width="80"
            prop="taxPrice"
            :label="$t('purchaseDemand.taxPrice')"
            show-overflow-tooltip
          />
          <!-- 申请编号 -->
          <el-table-column
            align="center"
            width="130"
            prop="requirementHeadNum"
            :label="$t('purchaseDemand.requirementHeadNum')"
            show-overflow-tooltip
          />

          <!-- 业务实体 -->
          <el-table-column
            align="center"
            width="130"
            prop="orgName"
            :label="$t('purchaseDemand.businessEntity')"
          />
          <!-- 收货地址 -->
          <el-table-column
            align="center"
            width="120"
            prop="receiveAddress"
            :label="$t('purchaseDemand.ceeaDeliveryPlaceOut')"
            show-overflow-tooltip
          >
            <template slot-scope="scope">
              <RenderAsyncText :cell-value="scope.row.receiveAddress" />
            </template>
          </el-table-column>
          <!-- 采购项目 -->
          <!-- <el-table-column
            align="center"
            min-width="100"
            prop="ceeaProjectName"
            :label="$t('purchaseDemand.purchaseItem')"
            show-overflow-tooltip
          /> -->
          <!-- 已分配配额比 -->
          <!-- <el-table-column
            width="100"
            :label="$t('purchaseDemand.alreadyQuota')"
            prop="alreadyQuota"
            :formatter="formatterToFixed"
          /> -->
          <!-- 供应商编码 -->
          <el-table-column
            align="center"
            width="100"
            prop="vendorCode"
            :label="$t('purchaseDemand.vendorCode')"
            show-overflow-tooltip
          />
          <!-- 物料小类 -->
          <el-table-column
            align="center"
            width="100"
            prop="categoryName"
            :label="$t('purchaseDemand.materialCateSub')"
            show-overflow-tooltip
          />
          <!-- 申请行号 -->
          <el-table-column
            align="center"
            width="80"
            prop="rowNum"
            :label="$t('purchaseDemand.rowNum')"
            show-overflow-tooltip
          />
          <!-- 税率 -->
          <el-table-column
            align="center"
            width="80"
            prop="taxRate"
            :label="$t('purchaseDemand.taxRate')"
            show-overflow-tooltip
          />
          <!-- 价格来源 -->
          <el-table-column
            align="center"
            width="80"
            prop="ceeaPriceSourceType"
            :label="$t('purchaseDemand.ceeaPriceSourceType')"
            show-overflow-tooltip
          >
            <template slot-scope="scope">
              {{ getPriceSourceList(scope.row.ceeaPriceSourceType) }}
            </template>
          </el-table-column>
          <!-- 合同序号 -->
          <el-table-column
            align="center"
            width="80"
            prop="contractNo"
            :label="$t('contractMod.contractNo_1')"
            show-overflow-tooltip
          />
          <!-- 币种 -->
          <el-table-column
            align="center"
            width="100"
            prop="currencyName"
            :label="$t('purchaseDemand.currency')"
            show-overflow-tooltip
          />
          <!-- 已分配数量 -->
          <!-- <el-table-column
            align="center"
            width="60"
            :label="$t('purchaseDemand.alreadyNum')"
            prop="alreadyNum"
          /> -->
          <!-- 本次分配总量 -->
          <!-- <el-table-column
            width="60"
            align="center"
            :label="$t('purchaseDemand.totalDistribution')"
            prop="totalDistribution"
          /> -->
          <!-- 分配后配额 -->
          <!-- <el-table-column
            width="60"
            align="center"
            :label="$t('purchaseDemand.afterQuota')"
            prop="afterQuota"
            :formatter="formatterToFixed"
          /> -->
          <!-- 采购申请行备注 -->
          <el-table-column
            align="center"
            width="100"
            prop="comments"
            :label="$t('purchaseDemand.purchaseRequisitionLineRemarks')"
          >
            <template slot-scope="scope">
              <el-input v-model="scope.row.comments" />
            </template>
          </el-table-column>
        </el-table>
      </srm-dialog>

      <!-- 后续单据一览 -->
      <srm-dialog
        :title="$t('purchaseDemand.subsequentDocuments')"
        size="middle"
        :visible.sync="dialogFormVisible4"
        :close-on-click-modal="false"
      >
        <el-table
          :data="followOrderList"
          style="width: 100%"
          border
          height="251px"
        >
          <el-table-column
            type="index"
            width="60"
            :label="$t('common.sort')"
          />
          <!-- 后续单据编号 -->
          <el-table-column
            align="center"
            min-width="120"
            prop="subsequentDocumentsNumber"
            :label="$t('purchaseDemand.subsequentDocumentsNumber')"
          />
          <!-- 后续单据类型 -->
          <el-table-column
            align="center"
            width="150"
            prop="isubsequentDocumentssType"
            :label="$t('purchaseDemand.isubsequentDocumentssType')"
            :formatter="(row, column, cellValue) => $getDictLabel('NEW_RELATED_DOCUMENTS', cellValue)"
          />
          <!-- 创建人 -->
          <el-table-column
            align="center"
            width="150"
            prop="createdUserName"
            :label="$t('purchaseDemand.createdBy1')"
          />
          <!-- 创建时间 -->
          <el-table-column
            align="center"
            width="150"
            prop="creationDate"
            :label="$t('purchaseDemand.creationDate')"
          >
            <template slot-scope="scope">
              {{$parseTime(scope.row.creationDate)}}
            </template>
          </el-table-column>
        </el-table>
      </srm-dialog>

      <!-- 请选择价格起止日期 -->
      <srm-dialog
        :title="$t('purchaseDemand.priceFromToTips')"
        :visible.sync="customPricingTime"
        :close-on-click-modal="false"
        size="middle"
      >
        <el-form
          ref="form"
          :model="customPricingTimeData"
          class="form-incontainer"
          :rules="rules"
          label-width="80px"
          label-position="top"
        >
          <el-row type="flex">
            <el-col>
              <!-- 价格生效日期 -->
              <el-form-item
                :label="$t('purchaseDemand.effectiveDate')"
                :label-width="formLabelWidth"
                prop="from"
              >
                <el-date-picker
                  v-model="customPricingTimeData.from"
                  type="date"
                  :format="$formatDatePicker"
                  value-format="yyyy-MM-dd"
                  :placeholder="$t('purchaseDemand.datePicker')"
                />
              </el-form-item>
            </el-col>
            <el-col>
              <!-- 价格失效日期 -->
              <el-form-item
                :label="$t('purchaseDemand.expirationDate')"
                :label-width="formLabelWidth"
                prop="to"
              >
                <el-date-picker
                  v-model="customPricingTimeData.to"
                  type="date"
                  :format="$formatDatePicker"
                  value-format="yyyy-MM-dd"
                  :placeholder="$t('purchaseDemand.datePicker')"
                />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>

        <div
          slot="footer"
          class="dialog-footer"
        >
          <el-button @click="customPricingTime = false">
            {{ $t('common.cancel') }}
          </el-button>
          <el-button
            type="primary"
            @click="addOneItem"
          >
            {{ $t('common.confirm') }}
          </el-button>
        </div>
      </srm-dialog>

      <el-dialog
        :visible.sync="vendorQuotaDialog"
        :title="$t('purchaseDemand.vendorQuotaDetail')"
        width="1000px"
      >
        <el-table
          ref="table"
          :data="vendorQuotaTable"
          stripe
          border
          highlight-current-row
        >
          <el-table-column
            type="index"
            align="center"
            fixed
          />
          <el-table-column
            :label="$t('common.vendorCode')"
            prop="companyCode"
            align="center"
            show-overflow-tooltip
          />
          <el-table-column
            :label="$t('common.vendorName')"
            prop="companyName"
            align="center"
            show-overflow-tooltip
          />
          <el-table-column
            :label="$t('purchaseDemand.quota')"
            prop="quota"
            align="center"
            show-overflow-tooltip
          />
          <el-table-column
            :label="$t('purchaseDemand.planAmount')"
            prop="planAmount"
            align="center"
            show-overflow-tooltip
          />
          <el-table-column
            :label="$t('purchaseDemand.actualAmount')"
            prop="actualAmount"
            align="center"
            show-overflow-tooltip
          />
        </el-table>
        <div
          slot="footer"
          class="dialog-footer"
        >
          <el-button @click="vendorQuotaDialog = false">
            {{ $t('common.backTo') }}
          </el-button>
        </div>
      </el-dialog>

      <!-- 订单列表 -->
      <srm-dialog
        :title="$t('purchaseDemand.orderList')"
        size="large"
        :visible.sync="isCreateOrderVisible"
        :close-on-click-modal="false"
      >
        <div class="is-create-order">
          <el-button
            type="primary"
            @click="submitOrderDetai"
          >
            {{
              $t('common.affirm')
            }}
          </el-button>
          <span style="color: red;">* {{ $t('purchaseDemand.orderListTip') }}</span>
        </div>
        <el-table
          ref="table"
          :data="createOrderList"
          stripe
          border
          highlight-current-row
          height="200"
          @selection-change="selectOrderList"
        >
          <el-table-column
            type="selection"
            align="center"
            fixed
          />
          <!-- 业务实体 -->
          <el-table-column
            :label="$t('purchaseDemand.businessEntity')"
            prop="ceeaOrgName"
            align="center"
          />
          <!-- 库存组织 -->
          <el-table-column
            :label="$t('purchaseDemand.invOrg')"
            prop="ceeaOrganizationName"
            align="center"
          />
          <!-- 采购项目 -->
          <el-table-column
            :label="$t('purchaseDemand.purchaseItem')"
            prop="purchaseProject"
            align="center"
          />
          <!-- 采购订单号 -->
          <el-table-column
            :label="$t('purchaseDemand.orderNumber')"
            prop="orderNumber"
            align="center"
          />
        </el-table>
      </srm-dialog>

      <!-- 退回原因 -->
      <srm-dialog
        :title="$t('purchaseDemand.rejectReason')"
        :visible.sync="rejectReasonDialog"
        :modal-append-to-body="false"
        size="small"
        class="inportAbcd"
      >
        <el-main>
          <el-form
            ref="returnRef"
            :model="rejectReason"
            :rules="rejectResonRules"
          >
            <el-form-item
              :label="$t('purchaseDemand.rejectReason')"
              :label-width="formLabelWidth"
              prop="reasonDesc"
            >
              <template style="height:200px;">
                <el-input
                  v-model="rejectReason.reasonDesc"
                  type="textarea"
                />
              </template>
            </el-form-item>
          </el-form>
        </el-main>
        <div slot="footer">
          <el-button @click="rejectReasonDialog = false">
            {{ $t('common.cancel') }}
          </el-button>
          <el-button
            type="primary"
            @click="rejectReasonConfirm"
          >
            {{ $t('common.confirm') }}
          </el-button>
        </div>
      </srm-dialog>
      <!-- 查看定标 -->
      <ViewCalingDialog
        ref="viewCalingDialog"
        :visible.sync="viewCalingDialogVisible"
        :editRows="editRow"
      />
    </el-main>
  </el-container>
</template>
<script>
import { downloadFileLink, downloadFileLinkByPost } from 'lib@/utils/file'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import QuickSearch from 'lib@/components/QuickSearch'
import { getDictItem } from '@/api/common'
import { adaptDictData, parseTime, getValidateFailureSequence } from '@/utils'
import RenderAsyncText from '@/library/components/provice-city/renderAsyncText'
import ExportExcel from 'lib@/components/export-excel'
import purchaseOrderDetail from 'modb@/orderManagementBuyer/views/buyerPurchaseOrder/purchaseOrderDetail.vue'
import { transformMQL } from '@/library/utils/util'
import purchaseApplicationDetail2
from '@/modulesCus/buyer/purchasingDemand/views/purchaseApplication/purchaseApplicationDetailZhaobiao'
import withdrawZhaobiao
from '@/modulesCus/buyer/purchasingDemand/views/withdrawZhaobiao/edit'
import { planPool, qa } from 'modc@/buyer/purchasingDemand/api'
import ViewCalingDialog from './dialog/viewCalingDialog'

export default {
  name: 'DemandPoolList',
  components: {
    TableView,
    MainHeader,
    ExportExcel,
    FormWrapper,
    RenderAsyncText,
    ViewCalingDialog,
    QuickSearch
  },
  provide () {
    return { context: this }
  },
  data () {
    return {
      checkChangeData: [],
      demandTypeList: [],
      priceSourceList: [],
      rejectReason: {
        reasonDesc: ''
      },
      rejectReasonDialog: false,
      viewCalingDialogVisible: false,
      editRow: {},
      createOrderList: [],
      isCreateOrderVisible: false,
      name: 'materialAssignRuleTable',
      tableName: 'materialAssignRuleTable',
      orderTypeBol: false,
      dictCodes: {
        requireFrom: 'PR_SOU_REQUIREMENT_FROM',
        prefixTechDiscussion: 'YES_OR_NO',
        needPublic: 'YES_OR_NO',
        noPublicReasonChoose: 'PR_SOU_REQUIREMENT_NO_PUBLIC',
        ifAppointBrand: 'YES_OR_NO',
        ifQualifyUnit: 'YES_OR_NO',
        hasAssigned: 'YES_OR_NO',
        souReqStatus: 'PR_SOU_REQUIREMENT_STATUS',
        specialSouType: 'PR_SOU_REQUIREMENT_SPECIAL_TYPE',
        specialReason: 'PR_SOU_REQUIREMENT_SPECIAL_REASON',
        hasCreateVendorRecommend: 'YES_OR_NO',
        hasCreateSouReq: 'YES_OR_NO',
        hasSendSouProfile: 'YES_OR_NO',
        sendSouProfileStatus: 'BID_DATA_SUBMIT_STATUS',
        hasCreateSou: 'YES_OR_NO'
      },
      reviewFormNumber: '',
      gridData: [],
      pageSize: 15,
      pageNum: 1,
      gridId: 'list',
      selectList: [],
      selectedRows: [],
      showFilterBar: 1,
      tableHeader: [],
      tableData: [],
      vendorQuotaDialog: false,
      vendorQuotaTable: [],
      selectDictionary: {},
      initActive: true,
      strategyList: [],
      carryOutList: [],
      globalorgId: null,
      customPricingTimeData: {
        from: '',
        to: ''
      },
      yesNoOptions: [
        { label: () => this.$t('common.yes'), value: 'Y' },
        { label: () => this.$t('common.no'), value: 'N' }
      ],
      globalorganizationId: null,
      globalcategoryId: null,
      dialogFormVisible1: false,
      dialogFormVisible2: false,
      dialogFormVisible3: false,
      dialogFormVisible4: false,
      customPricingTime: false,
      followOrderList: [],
      requirementHead: {
        ceeaIfSupplierConfirm: 'Y'
      },
      formLabelWidth: '100px',
      preArr: [],
      preFormObj: {
        souReqStatus: 'EXECUTING'
      },
      form: {
        ceeaStrategyUserId: '',
        ceeaStrategyUserId2: '',
        ceeaStrategyUserNickname: '',
        ceeaStrategyUserName: '',
        ceeaPerformUserId: '',
        ceeaPerformUserNickname: '',
        ceeaPerformUserName: ''
      },
      globalSourceType: null,
      sourceTypeList: [
        // 简易询价
        {
          label: this.$t('purchaseDemand.simpleInquiry'),
          value: 'INQUIRY',
          componentName: 'inquiryManagement'
        },
        // 项目式询价
        // {
        //   label: this.$t('purchaseDemand.inquiryByProjectListBuyer'),
        //   value: 'BARGAINING',
        //   componentName: 'bargainManagement'
        // },
        // 招标
        {
          label: this.$t('purchaseDemand.bidding'),
          value: 'BIDDING',
          componentName: 'biddingManagementLTS'
        },
        // 竞价
        {
          label: this.$t('purchaseDemand.priceBidding'),
          value: 'COMPETING',
          componentName: 'competitionManagement'
        }
      ],
      purOrderList: [],
      currentBatchReasons: [],
      currentRows: [],
      rejectResonRules: {
        reasonDesc: [{ required: true, message: this.$t('purchaseDemand.fillInReasonForReturn') }] // 请填写退回原因
      },
      rules: {
        orgName: [{ required: true, message: this.$t('purchaseDemand.orgIdTips') }], // 请输入业务实体
        materialCode: [
          {
            required: true,
            message: this.$t('purchaseDemand.materialCodeTips')
          }
        ], // 请输入物料编号
        supUserNickname: [
          {
            required: true,
            message: this.$t('purchaseDemand.supUserNicknameTips')
          }
        ] // 请输入供应商管理
      },
      queryParam: {},
      statusList: [],
      paymentType: [],
      orderTypeList: [],
      purchaseTypeList: [],
      dmandLineRequestOpts: [],
      ifDistributionVendorList: [],
      getFooterNum: 1,
      getFooterSizeNum: 15
    }
  },
  watch: {
    $route: {
      // 当前功能已经打开的时候监听是否是从其他功能跳转过来的
      deep: true,
      immediate: true,
      handler () {
        if (
          this.$route.params.from === 'fromFun' &&
          this.$route.params.funName === 'demandPoolManagement'
        ) {
          this.preFormObj.requirementHeadNum = this.$route.params.formNo.split('-')[0]
          this.$refs.formRef.setValue('requirementHeadNum', this.$route.params.formNo.split('-')[0])
        }
      }
    }
  },
  created () {
    this.tableHeader = [
      {
        prop: 'requirementHeadNum',
        label: this.$t('purchaseDemand.requirementHeadNum'),
        width: 130,
        showType: 'button',
        btnStyle: 'text',
        formattor: val => val || '--',
        callback: row => this.readPurchaseApplication(row)
      },
      {
        prop: 'projectMonth',
        label: this.$t('components.month'),
        width: 130
      },
      // 需求来源
      {
        prop: 'requireFrom',
        label: this.$t('cusEntry.supplement20250121.sourceFrom'),
        width: 130,
        formattor: val => this.$getDictLabel('PR_SOU_REQUIREMENT_FROM', val)
      },
      // 所属品类
      {
        prop: 'categoryName',
        label: this.$t('cusEntry.supplement20250121.category'),
        width: 110
      },
      {
        prop: 'projectName',
        label: this.$t('bidMod.bidingName'),
        width: 100
      },
      // 项目所在地
      {
        prop: 'projectAddress',
        label: this.$t('cusEntry.supplement20250121.projectAddress'),
        minWidth: 150
      },
      // 规模数量
      {
        prop: 'requireQuantity',
        label: this.$t('cusEntry.bidMod.scaleQuantity'),
        width: 100
      },
      // 概算金额（卢布）
      {
        prop: 'totalAmountByTenKilo',
        label: this.$t('cusEntry.supplement20250121.totalAmountByTenKilo'),
        width: 150
      },
      // 所属板块
      {
        prop: 'orgBuName',
        label: this.$t('cusEntry.supplement20250121.orgBuName'),
        width: 110
      },
      // 公司
      {
        prop: 'orgName',
        label: this.$t('cusEntry.orderMod.companyName'),
        width: 100
      },
      {
        prop: 'ceeaDepartmentName',
        label: this.$t('purchaseDemand.ceeaDepartment'),
        width: 100
      },
      // 技术专家
      {
        prop: 'techGroupFullName',
        label: this.$t('cusEntry.bidMod.techResponer'),
        width: 100,
        showType: 'slot',
        slot: 'techGroupFullName'
      },
      // 技术专家联系方式
      {
        prop: 'techGroupPhone',
        label: this.$t('cusEntry.supplement20250121.techGroupPhone'),
        width: 150
      },
      // 技术专家工作年限
      {
        prop: 'techGroupWorkYear',
        label: this.$t('cusEntry.supplement20250121.techGroupWorkYear'),
        width: 150
      },
      // 前置技术交流意向
      {
        prop: 'prefixTechDiscussion',
        label: this.$t('cusEntry.supplement20250121.prefixTechDiscussion'),
        width: 150,
        formattor: val => this.$getDictLabel('YES_OR_NO', val)
      },
      // 是否公示
      {
        prop: 'needPublic',
        label: this.$t('cusEntry.supplement20250121.needPublic'),
        width: 120,
        formattor: val => this.$getDictLabel('YES_OR_NO', val)
      },
      // 不公示原因
      {
        prop: 'noPublicReasonChoose',
        label: this.$t('cusEntry.supplement20250121.noPublicReasonChoose'),
        width: 120,
        formattor: val => this.$getDictLabel('PR_SOU_REQUIREMENT_NO_PUBLIC', val)
      },
      // 具体原因说明
      {
        prop: 'noPublicReason',
        label: this.$t('cusEntry.supplement20250121.noPublicReason'),
        width: 120
      },
      // 是否指定品牌
      {
        prop: 'ifAppointBrand',
        label: this.$t('cusEntry.supplement20250121.ifAppointBrand'),
        width: 120,
        formattor: val => this.$getDictLabel('YES_OR_NO', val)
      },
      // 指定品牌原因
      {
        prop: 'extAppointBandReason',
        label: this.$t('cusEntry.supplement20250121.extAppointBandReason'),
        width: 150
      },
      // 是否限定单位
      {
        prop: 'ifQualifyUnit',
        label: this.$t('cusEntry.supplement20250121.ifQualifyUnit'),
        width: 120,
        formattor: val => this.$getDictLabel('YES_OR_NO', val)
      },
      // 限定单位原因
      {
        prop: 'extAppointUnitReason',
        label: this.$t('cusEntry.supplement20250121.extAppointUnitReason'),
        width: 150
      },
      // 公示截止时间
      {
        prop: 'publicEndTime',
        label: this.$t('cusEntry.reportManagement.publicEndTime'),
        width: 120,
        dataType: 'dateTime'
      },
      // 调整公示截止时间
      {
        prop: 'extPublicEndTime',
        label: this.$t('cusEntry.supplement20250121.extPublicEndTime'),
        width: 140,
        dataType: 'dateTime'
      },
      // 递交招标申请资料时间
      {
        prop: 'sendSouProfileEndDate',
        label: this.$t('cusEntry.supplement20250121.sendSouProfileEndDate'),
        width: 150,
        dataType: 'dateTime'
      },
      {
        prop: 'hasAssigned',
        label: this.$t('logisticsMod.applyAssignStatus'),
        width: 120,
        formattor: val => this.$getDictLabel('YES_OR_NO', val)
      },
      // 供应商专家
      {
        prop: 'vendorGroupFullName',
        label: this.$t('cusEntry.bidSuperviseReport.vendorPrincipal'),
        width: 120,
        showType: 'slot',
        slot: 'vendorGroupFullName'
      },
      // 供应商专家所在科室
      {
        prop: 'vendorGroupDepartmentName',
        label: this.$t('cusEntry.supplement20250121.vendorGroupDepartmentName'),
        width: 120
      },
      // 招标专家
      {
        prop: 'souGroupFullName',
        label: this.$t('cusEntry.bidSuperviseReport.souPrincipal'),
        width: 120,
        showType: 'slot',
        slot: 'souGroupFullName'
      },
      // 招标专家所在科室
      {
        prop: 'souGroupDepartmentName',
        label: this.$t('cusEntry.supplement20250121.souGroupDepartmentName'),
        width: 120
      },
      // 需求审批完成时间
      {
        prop: 'approvalPassTime',
        label: this.$t('cusEntry.supplement20250121.approvalPassTime'),
        width: 120,
        dataType: 'dateTime'
      },
      // 需求状态
      {
        prop: 'souReqStatus',
        label: this.$t('cusEntry.supplement20250121.extPoolStatusValue'),
        width: 120,
        formattor: val => this.$getDictLabel('PR_SOU_REQUIREMENT_STATUS', val)
      },
      // 未提报月度计划原因
      {
        prop: 'noReportMonthPlanReason',
        label: this.$t('cusEntry.supplement20250121.notMonthlyPlanReason'),
        width: 150
      },
      // 特殊招标类型
      {
        prop: 'specialSouType',
        label: this.$t('cusEntry.supplement20250121.specialSouType'),
        width: 120,
        formattor: val => this.$getDictLabel('PR_SOU_REQUIREMENT_SPECIAL_TYPE', val)
      },
      // 特定原因
      {
        prop: 'specialReason',
        label: this.$t('cusEntry.supplement20250121.specialReason'),
        width: 120,
        formattor: val => this.$getDictLabel('PR_SOU_REQUIREMENT_SPECIAL_REASON', val)
      },
      // 需求产生时间
      {
        prop: 'requireProductDate',
        label: this.$t('cusEntry.supplement20250121.requireProductDate'),
        width: 120,
        dataType: 'dateTime'
      },
      // 需求产生时间附件
      {
        prop: 'fileInfos',
        label: this.$t('cusEntry.supplement20250121.fileInfos'),
        width: 150,
        showType: 'slot',
        slot: 'fileInfos'
      },
      // 工期交货期
      {
        prop: 'deliveryDay',
        label: this.$t('cusEntry.supplement20250121.deliveryDay'),
        width: 120
      },
      // 工期交货期附件
      {
        prop: 'fileInfos2',
        label: this.$t('cusEntry.supplement20250121.fileInfos2'),
        width: 150,
        showType: 'slot',
        slot: 'fileInfos2'
      },
      // 投入使用时间
      {
        prop: 'putIntoUseDate',
        label: this.$t('cusEntry.supplement20250121.putIntoUseDate'),
        width: 120,
        dataType: 'dateTime'
      },
      // 投入使用时间附件
      {
        prop: 'fileInfos3',
        label: this.$t('cusEntry.supplement20250121.fileInfos3'),
        width: 150,
        showType: 'slot',
        slot: 'fileInfos3'
      },
      // 剩余时间
      {
        prop: 'remainingDay',
        label: this.$t('cusEntry.supplement20250121.remainingDay'),
        width: 120
      },
      // 其他特殊原因补充
      {
        prop: 'otherSpecialReason',
        label: this.$t('cusEntry.supplement20250121.otherSpecialReason'),
        width: 120
      },
      {
        prop: 'phone',
        label: this.$t('vendorMod.contactMethod'),
        width: 120
      },
      {
        prop: 'email',
        label: this.$t('common.email'),
        width: 120
      },
      // 推荐人
      {
        prop: 'extReferrer',
        label: this.$t('cusEntry.supplement20250121.extReferrer'),
        width: 120
      },
      // 推荐单位名称
      {
        prop: 'vendorName',
        label: this.$t('cusEntry.supplement20250121.vendorName'),
        width: 120
      },
      // 企业纳税人号
      {
        prop: 'extTaxRecognizeMark',
        label: this.$t('cusEntry.supplement20250121.extTaxRecognizeMark'),
        width: 180
      },
      // 推荐原因
      {
        prop: 'recommendFrom',
        label: this.$t('cusEntry.supplement20250121.recommendFrom'),
        width: 120
      },
      {
        prop: 'techRequire',
        label: this.$t('qualitySynergy.technicalRequirement'),
        width: 120,
        formattor: val => val
      },
      // 业绩要求
      {
        prop: 'performanceRequire',
        label: this.$t('cusEntry.supplement20250121.performanceRequire'),
        width: 120,
        formattor: val => val
      },
      // 其他供应商资质说明
      {
        prop: 'vendorQualificationRequire',
        label: this.$t('cusEntry.supplement20250121.vendorQualificationRequire'),
        width: 150
      },
      {
        prop: 'applyByNickname',
        label: this.$t('mould.applyId'),
        width: 120
      },
      // {
      //   prop: 'check',
      //   label: '查看定标信息',
      //   width: 120,
      //   showType: 'button',
      //   btnStyle: 'text',
      //   formattor: val => '查看',
      //   disabled: row => row.hasCreateSouReq !== 'Y',
      //   callback: row => this.openViewCalingDialog(row)
      // },
      // 是否已创供应商推荐
      {
        prop: 'hasCreateVendorRecommend',
        label: this.$t('cusEntry.supplement20250121.hasCreateVendorRecommend'),
        width: 120,
        formattor: val => this.$getDictLabel('YES_OR_NO', val)
      },
      // 是否已公示
      {
        prop: 'hasCreateSouReq',
        label: this.$t('cusEntry.supplement20250121.hasCreateSouReq'),
        width: 120,
        formattor: val => this.$getDictLabel('YES_OR_NO', val)
      },
      // 是否已递交招标资料
      {
        prop: 'hasSendSouProfile',
        label: this.$t('cusEntry.supplement20250121.hasSendSouProfile'),
        width: 120,
        formattor: val => this.$getDictLabel('YES_OR_NO', val)
      },
      // 招标资料状态
      {
        prop: 'sendSouProfileStatus',
        label: this.$t('cusEntry.supplement20250121.sendSouProfileStatus'),
        width: 120,
        formattor: val => this.$getDictLabel('BID_DATA_SUBMIT_STATUS', val)
      },
      // 是否已发布标书
      {
        prop: 'hasCreateSou',
        label: this.$t('cusEntry.supplement20250121.hasCreateSou'),
        width: 120,
        formattor: val => this.$getDictLabel('YES_OR_NO', val)
      },
      // 需求取消原因
      {
        prop: 'reqCancelReason',
        label: this.$t('cusEntry.supplement20250121.reqCancelReason'),
        width: 120
      }
    ]
    this.defaultTableHeader = this.tableHeader

    this.preArr = [
      {
        prop: 'requirementHeadNum',
        label: this.$t('purchaseDemand.requirementHeadNum')
      },
      {
        prop: 'categoryCode',
        label: this.$t('common.category')
      },
      {
        prop: 'projectName',
        label: this.$t('bidMod.bidingName'),
      },
      {
        prop: 'demandType',
        label: () => this.$t('purchaseDemand.demandType'),
        type: 'dict',
        code: 'DEMAND_TYPE'
      },
      // 需求来源
      {
        prop: 'requireFrom',
        label: this.$t('cusEntry.supplement20250121.sourceFrom'),
        type: 'dict',
        code: 'PR_SOU_REQUIREMENT_FROM'
      },
      {
        prop: 'projectMonth',
        label: this.$t('components.month')
      },
      // 板块
      {
        prop: 'orgBuCode',
        label: this.$t('cusEntry.bidSuperviseReport.extOrgBuName'),
      },
      // 公司
      {
        prop: 'orgCode',
        label: this.$t('cusEntry.perfMod.orgName'),
      },
      {
        prop: 'departmentId',
        label: () => this.$t('purchaseDemand.ceeaDepartment')
      },
      {
        prop: 'applyBy',
        label: () => this.$t('purchaseDemand.applicant')
      },
      {
        prop: 'dateList',
        label: () => this.$t('purchaseDemand.applyDate'),
        type: 'daterange'
      },
      // 项目所在地
      {
        prop: 'projectAddress',
        label: this.$t('cusEntry.supplement20250121.projectAddress')
      },
      // 技术专家
      {
        prop: 'techUsername',
        label: this.$t('cusEntry.bidMod.techResponer'),
        type: 'quicksearch',
        showKey: 'username',
        name: 'scc_rbac_user_display'
      },
      // 是否公示
      {
        prop: 'needPublic',
        label: this.$t('cusEntry.supplement20250121.needPublic'),
        type: 'dict',
        code: 'YES_OR_NO'
      },
      // 公示截止时间
      {
        prop: 'publicEndTime',
        label: this.$t('cusEntry.reportManagement.publicEndTime'),
        type: 'dateranges'
      },
      // 前置交流意向
      {
        prop: 'prefixTechDiscussion',
        label: this.$t('cusEntry.supplement20250121.prefixTechDiscuss'),
        type: 'dict',
        code: 'YES_OR_NO'
      },
      // 递交申请资料时间
      {
        prop: 'sendSouProfileEndTimes',
        label: this.$t('cusEntry.supplement20250121.sendSouProfileEndTimes'),
        type: 'daterange'
      },
      // 需求审批完成时间
      {
        prop: 'approvalPassTime',
        label: this.$t('cusEntry.supplement20250121.approvalPassTime'),
        type: 'dateranges'
      },
      // 需求状态
      {
        prop: 'souReqStatus',
        label: this.$t('cusEntry.supplement20250121.extPoolStatusValue'),
        type: 'dict',
        code: 'PR_SOU_REQUIREMENT_STATUS'
      },
      // 是否已公示
      {
        prop: 'hasCreateSouReq',
        label: this.$t('cusEntry.supplement20250121.hasCreateSouReq'),
        type: 'dict',
        code: 'YES_OR_NO'
      },
      // 是否已递交招标资料
      {
        prop: 'hasSendSouProfile',
        label: this.$t('cusEntry.supplement20250121.hasSendSouProfile'),
        type: 'dict',
        code: 'YES_OR_NO'
      },
      // 招标专家
      {
        prop: 'souUsername',
        label: this.$t('cusEntry.bidSuperviseReport.souPrincipal'),
        type: 'quicksearch',
        showKey: 'username',
        name: 'scc_rbac_user_display'
      },
      // 供应商专家
      {
        prop: 'vendorUsername',
        label: this.$t('cusEntry.bidSuperviseReport.vendorPrincipal'),
        type: 'quicksearch',
        showKey: 'username',
        name: 'scc_rbac_user_display'
      },
      // 推荐单位名称
      {
        prop: 'vendorName',
        label: this.$t('cusEntry.supplement20250121.vendorName')
      },
      // 招标专家所在科室
      {
        prop: 'souUserDeptName',
        label: this.$t('cusEntry.supplement20250121.souGroupDepartmentName')
      },
      // 供应商专家所在科室
      {
        prop: 'vendorUserDeptName',
        label: this.$t('cusEntry.supplement20250121.vendorGroupDepartmentName')
      },
      // 不公示原因
      {
        prop: 'noPublicReasonChoose',
        label: this.$t('cusEntry.supplement20250121.noPublicReasonChoose'),
        type: 'dict',
        code: 'PR_SOU_REQUIREMENT_NO_PUBLIC'
      },
      // 是否指定品牌
      {
        prop: 'ifAppointBrand',
        label: this.$t('cusEntry.supplement20250121.ifAppointBrand'),
        type: 'dict',
        code: 'YES_OR_NO'
      },
      // 是否限定单位
      {
        prop: 'ifQualifyUnit',
        label: this.$t('cusEntry.supplement20250121.ifQualifyUnit'),
        type: 'dict',
        code: 'YES_OR_NO'
      },
      // 特殊招标类型
      {
        prop: 'specialSouType',
        label: this.$t('cusEntry.supplement20250121.specialSouType'),
        type: 'dict',
        code: 'PR_SOU_REQUIREMENT_SPECIAL_TYPE'
      },
      // 是否已分配
      {
        prop: 'hasAssigned',
        label: this.$t('cusEntry.supplement20250121.hasAssigned'),
        type: 'dict',
        code: 'YES_OR_NO'
      }
    ]
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  // activated () {
  //   // 从首页跳转过来需求池界面，自动查询对应的单号
  //   if (
  //     this.$route.params.from === 'fromFun' &&
  //     this.$route.params.funName === 'demandPoolManagement'
  //   ) {
  //     this.preFormObj.requirementHeadNum = this.$route.params.formNo.split('-')[0]
  //     this.$refs.formRef.setValue('requirementHeadNum', this.$route.params.formNo.split('-')[0])
  //   }
  //   this.$nextTick(() => {
  //     this.getQuerydata(this.preFormObj)
  //   })
  // },
  methods: {
    openViewCalingDialog (row) {
      this.editRow = row
      this.viewCalingDialogVisible = true
    },
    generateMeiQLExportRequest () {
      let queryParam = JSON.parse(JSON.stringify(this.queryParam))
      queryParam.type = 'PrSouRequirementPoolForBuyer'
      queryParam.action = 'querySouPool'
      const {
        pageNum,
        pageSize,
        ...otherQuery
      } = queryParam.payload
      // const pageNum = queryParam.payload?.pageNum
      // const pageSize = queryParam.payload?.pageSize
      queryParam.payload = {
        ...otherQuery
      }
      const all = {
        body: queryParam
      }
      return all
    },
    getUserObj (val, scope) {

    },
    getUserObj10 (val, scope) {
      console.log(val, 'val')
      this.form.ceeaStrategyUserId = val?.userId
      this.form.nickname = val?.nickname
    },
    getUserObj11 (val, scope) {
      console.log(val, 'val')
      this.form.ceeaStrategyUserId2 = val?.userId
      this.form.nickname2 = val?.nickname
    },
    requirementCancellation () {
      const checkChangeData = this.checkChangeData
      if (checkChangeData.length > 1) {
        // 只能选择一条单据，不支持多条同时操作
        this.$message.warning(this.$t('cusEntry.supplement20250121.promptTips36'))
        return false
      } else if (checkChangeData.length <= 0) {
        // 请选择一条单据进行操作
        this.$message.warning(this.$t('cusEntry.supplement20250121.promptTips37'))
        return false
      }

      const saveData = transformMQL.save('PrSouRequirementPoolForBuyer', [this.checkChangeData[0]], 'checkCancelCondition')
      planPool.checkCancelCondition(saveData).then((datas) => {
        this.$emit('tab-add', {
          component: withdrawZhaobiao,
          params: {
            flag: 'add',
            row: this.checkChangeData[0],
            tabName: 'withdrawZhaobiao',
            extSource: 'PLAN'
          },
          // 新增取消单
          title: this.$t('cusEntry.supplement20250121.addCancel'),
          name: 'withdrawZhaobiao'
        })
      })
    },
    checkChange (records) {
      this.checkChangeData = records
    },
    afterQuery (data) {
      console.log(data, 'data')
      this.$refs[this.gridId].setTableData(async tableData => {
        console.log(tableData)
        tableData.forEach((item, index) => {
          const { souGroupList, souReqHead, souVendorList, ...res } = item
          let departmentName1 = ''
          let username1 = ''
          let userId1 = ''
          let departmentName2 = ''
          let username2 = ''
          let userId2 = ''
          let phone1 = ''
          let fullName1 = ''
          let souVendorListOne = {}

          if (souGroupList) {
            souGroupList.forEach(e => {
              if (e.groupType == 'SOU') {
                departmentName1 = e.departmentName
                username1 = e.username + e.fullName
                userId1 = e.userId
              }
              if (e.groupType == 'VENDOR') {
                departmentName2 = e.departmentName
                username2 = e.username + e.fullName
                userId2 = e.userId
              }
              if (e.groupType == 'TECH') {
                phone1 = e.phone
                fullName1 = e.username + e.fullName
              }
            })
          }

          if (souVendorList) {
            souVendorListOne = { ...souVendorList[0] }
          }
          const obj = {
            ...res,
            ...souReqHead,
            departmentName1,
            username1,
            departmentName2,
            username2,
            phone1,
            fullName1,
            userId1,
            userId2,
            ...souVendorListOne
          }
          this.$set(tableData, index, obj)
        })
      })
    },
    // 获取价格来源
    getPriceSourceList (type) {
      return this.priceSourceList.find(item => item.value === type).label
    },
    // 创建采购订单 - 选是否供方确认
    changeSupplierConfirm (type) {
      if (this.currentRows.length < 1) {
        // 请至少选择一条目标数据
        this.$message.warning(this.$t('purchaseDemand.minLimitMsg'))
      }
      this.currentRows.map(item => (item.ceeaIfSupplierConfirm = type))
      console.log(this.purOrderList, 'this.purOrderList')
    },
    formatterToFixed (row, column, cellValue, index) {
      return cellValue ? Number(cellValue).toFixed(1) : 0
    },
    assignSupplier () {
      const requirementLineIds = []
      if (!this.selectedRows.length) {
        this.$confirm(this.$t('purchaseDemand.assignSupplierConfirm'), {
          confirmButtonText: this.$t('common.confirm'),
          cancelButtonText: this.$t('common.cancel'),
          type: 'warning'
        })
          .then(() => {
            this.getAssignSupplier([])
          })
          .catch(() => {})
      } else {
        this.selectedRows.forEach(i => {
          requirementLineIds.push(i.requirementLineId)
        })
        this.getAssignSupplier(requirementLineIds)
      }
    },
    getAssignSupplier (data) {
      this.$http({
        url: '/api-sup-ce/vendorDistDescController/assignSupplier',
        method: 'POST',
        data: data,
        loading: true
      })
        .then(res => {
          this.$message({ type: 'success', message: res.message })
          this.getQuerydata()
        })
        .catch(err => {
          console.log(err)
        })
    },
    vendorQuotaDetail (requirementLineId) {
      this.$http({
        url: '/api-sup-ce/vendorDistDescController/getVendorDistDescById',
        method: 'get',
        params: {
          requirementLineId: requirementLineId
        },
        loading: true
      })
        .then(res => {
          this.vendorQuotaDialog = true
          this.vendorQuotaTable = res.data
        })
        .catch(err => {
          console.log(err)
        })
    },
    getFooter (data) {
      this.getFooterNum = data.value
      // this.getQuerydata()
    },
    getFooterSize (data) {
      this.getFooterSizeNum = data.value
    },
    doHold () {
      if (this.selectedRows.length === 0) {
        return this.$message.warning(this.$t('purchaseDemand.selectData'))
      }
      this.$http({
        url: '/api-sup-ce/pr/requirementHead/holdRequirementLine',
        method: 'POST',
        data: this.selectedRows.map(v => v.requirementLineId),
        loading: true
      })
        .then(data => {
          this.$message.success(this.$t('common.success'))
          this.getQuerydata()
        })
        .catch(err => {
          console.log(err)
        })
    },
    cancelHold () {
      if (this.selectedRows.length === 0) {
        return this.$message.warning(this.$t('purchaseDemand.selectData'))
      }
      this.$http({
        url: '/api-sup-ce/pr/requirementHead/releaseRequirementLine',
        method: 'POST',
        data: this.selectedRows.map(v => v.requirementLineId),
        loading: true
      })
        .then(data => {
          this.$message.success(this.$t('common.success'))
          this.getQuerydata()
        })
        .catch(err => {
          console.log(err)
        })
    },
    getQuerydata (obj) {
      const { dateList, sendSouProfileEndTimes, publicEndTime, approvalPassTime, ...rest } = obj || this.preFormObj
      const params = {}
      if (dateList) {
        params.applyDateFrom = dateList[0]
        params.applyDateTo = dateList[1]
      }
      if (publicEndTime) {
        params.publicEndTimeFrom = publicEndTime[0]
        params.publicEndTimeTo = publicEndTime[1]
      }
      if (sendSouProfileEndTimes) {
        params.sendSouProfileEndDateFrom = sendSouProfileEndTimes[0]
        params.sendSouProfileEndDateTo = sendSouProfileEndTimes[1]
      }
      if (approvalPassTime) {
        params.approvalPassTimeFrom = approvalPassTime[0]
        params.approvalPassTimeTo = approvalPassTime[1]
      }
      params.extBidFlag = 'Y'
      params.pageSize = this.pageSize
      params.pageNum = this.getFooterNum
      this.queryParam = transformMQL.save('PrSouRequirementPoolForBuyer', { ...rest, ...params }, 'querySouPool')
      console.log(this.queryParam, 'queryParam')
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    handleCurrentChange (val) {
      // console.log(val, 'handleCurrentChange')
      // this.pageNum = val
      this.selectedRows = val
    },
    setCurrencyObj (row) {
      const obj = row.contractVoList.find(v => v.contractCode === row.contractNum) || {}
      row.taxPrice = obj.taxedPrice
      row.notaxPrice = obj.untaxedPrice
      row.taxRate = obj.taxRate
      row.taxKey = obj.taxKey
      row.currencyId = obj.currencyId
      row.currencyCode = obj.currencyCode
      row.currencyName = obj.currencyName
      row.contractNo = obj.contractNo
      // 如果来自价格库，就设置付款条款给合同
      if (row.fromPrice === 'N') {
        row.ceeaPriceSourceId = obj.contractMaterialId
        row.materialName = obj.materialName
        row.orderPaymentProvisionList = []
        for (const item of row.payPlanList) {
          row.orderPaymentProvisionList.push({
            paymentPeriod: item.dateNum,
            paymentWay: item.payMethod,
            paymentTerm: item.payExplain
          })
        }
      }
    },
    editTab (type, row) {
      if (type === 'add') {
        // 新增
        for (const i in this.form) {
          this.form[i] = null
        }
        this.form.startDate = new Date()
      } else {
        // 修改
        for (const i in this.form) {
          this.form[i] = row[i]
        }
      }
      this.dialogFormVisible1 = true
    },
    getUserObj2 (val, scope) {
      scope.ceeaStrategyUserId = val ? val.personInChargeUserId : ''
      scope.ceeaStrategyUserName = val ? val.personInChargeUsername : ''
      scope.ceeaStrategyUserNickname = val ? val.personInChargeNickname : ''
    },
    getUserObj3 (val, scope) {
      scope.ceeaPerformUserId = val ? val.personInChargeUserId : ''
      scope.ceeaPerformUserName = val ? val.personInChargeUsername : ''
      scope.ceeaPerformUserNickname = val ? val.personInChargeNickname : ''
    },
    setUserObj2 (scope) {
      const val =
        this.strategyList.find(v => v.personInChargeUserId === this.form.ceeaStrategyUserId) || {}
      // scope.ceeaStrategyUserId = val ? val.personInChargeUserId : "";
      scope.ceeaStrategyUserName = val.personInChargeUsername
      scope.ceeaStrategyUserNickname = val.personInChargeNickname
    },
    setUserObj3 (scope) {
      const val =
        this.carryOutList.find(v => v.personInChargeUserId === this.form.ceeaPerformUserId) || {}
      // scope.ceeaStrategyUserId = val ? val.personInChargeUserId : "";
      scope.ceeaPerformUserName = val.personInChargeUsername
      scope.ceeaPerformUserNickname = val.personInChargeNickname
    },
    downloadTemplate () {
      // 下载模板
      downloadFileLink(
        '/api-sup-ce/division/divisionMaterial/importModelDownload',
        `${this.$t('dataConfMod.importTemplate')}${new Date().getTime()}.xls`
      )
    },
    uploadSuccess (val) {
      if (val && val.code === '0') this.getQuerydata()
    },
    setQuantity (row, index) {
      if (row.quotaProportion) {
        row.thisOrderQuantity = (row.quotaProportion / 100) * row.orderQuantity
        const sortIndexs = this.$refs.createOrderRef.selection.map(item => item.sortIndex)
        if (!sortIndexs.includes(row.sortIndex)) {
          this.$refs.createOrderRef.selection.push(row)
          this.currentRows = this.$refs.createOrderRef.selection
        }
        console.log(this.currentRows, 'currentRows')
      } else {
        row.thisOrderQuantity = 0
      }
    },
    setPortation (row, index) {
      if (row.thisOrderQuantity > 0) {
        row.quotaProportion = Number((row.thisOrderQuantity / row.orderQuantity) * 100).toFixed(4)
        const sortIndexs = this.$refs.createOrderRef.selection.map(item => item.sortIndex)
        if (!sortIndexs.includes(row.sortIndex)) {
          this.$refs.createOrderRef.selection.push(row)
          this.currentRows = this.$refs.createOrderRef.selection
        }
        console.log(this.currentRows, 'currentRows')
      } else {
        row.quotaProportion = 0
      }
    },
    createInquiryBiaoshu () {
      const checkChangeData = this.checkChangeData
      const hasAssignedValid = checkChangeData.some(item => item.hasAssigned === 'N')
      const hasSendSouProfileValid = checkChangeData.some(item => item.hasSendSouProfile === 'N')
      const sendSouProfileStatusValid = checkChangeData.some(item => item.sendSouProfileStatus !== 'APPROVED')
      const catArr = checkChangeData.map((obj) => obj.categoryId)
      const isSameCat = catArr.every((name, index, arr) => name === arr[0])
      if (this.checkChangeData.length == 0) {
        // 请至少选中一行数据
        this.$message.warning(this.$t('cusEntry.supplement20250121.promptTips38'))
        return
      }
      if (hasAssignedValid) {
        // 所勾选行存在未分配的计划
        return this.$message.error(this.$t('cusEntry.supplement20250121.promptTips39'))
      }
      if (!isSameCat) {
        // 所勾选行的品类不一致，不允许一起创建标书
        return this.$message.error(this.$t('cusEntry.supplement20250121.promptTips40'))
      }
      if (hasSendSouProfileValid) {
        // 所勾选的行存在未提交招标资料
        return this.$message.error(this.$t('cusEntry.supplement20250121.promptTips41'))
      }
      if (sendSouProfileStatusValid) {
        // 所勾选的行存在未审批的招标资料
        return this.$message.error(this.$t('cusEntry.supplement20250121.promptTips42'))
      }
      let obj = []
      checkChangeData.forEach(e => {
        obj.push({ requirementHeadId: e.requirementHeadId })
      })
      const datas = [{
        souType: 'bid',
        reqHeadList: obj
      }]
      const searchData = transformMQL.save('PrSouRequirementPoolForBuyer', datas, 'createSou')
      planPool.createSou(searchData).then((datas) => {
        console.log('datas:', datas)
        let row = {
          projectId: datas.data.records[0].souVO.project.projectId,
          souNo: datas.data.records[0].souVO.project.souNo
        }
        // TODO
        this.$router.push({
          name: 'biddingManagementNew',
          params: {
            from: 'demandPoolManagementZhaobiao', // 来源路由name
            funName: 'bid', // 功能
            row
          }
        })
        this.getQuerydata()
      }).catch(() => {
      })
    },
    createInquiry () {
      const checkChangeData = this.checkChangeData
      if (checkChangeData.length > 1) {
        // 只能选择一条单据，不支持多条同时操作
        this.$message.warning(this.$t('cusEntry.supplement20250121.promptTips36'))
        return false
      } else if (checkChangeData.length <= 0) {
        // 请选择一条单据进行操作
        this.$message.warning(this.$t('cusEntry.supplement20250121.promptTips37'))
        return false
      }
      let bol = checkChangeData.find(e => e.needPublic !== 'Y')
      if (bol) {
        // 所勾选的数据存在是否公示为否，不允许创建寻源需求，请检查
        this.$message.warning(this.$t('cusEntry.supplement20250121.promptTips43'))
        return false
      }

      let obj = checkChangeData.map(e => e.requirementHeadId)
      const searchData = transformMQL.save('PrSouRequirementPoolForBuyer', obj, 'createSouReq')
      planPool.createSouReq(searchData).then((datas) => {
        console.log(datas, 'datas')
        const dataObj = datas.data?.records[0]
        if (dataObj.reqHeadId) {
          // TODO
          this.$router.push({
            name: 'sourcingRequireBuyer',
            params: {
              from: 'demandPoolManagementZhaobiao', // 来源路由name
              row: {
                reqHeadId: dataObj.reqHeadId,
                reqHeadNo: dataObj.reqHeadNo
              }
            }
          })
        }
      })
    },

    addOneItem () {
      const creatTimeData = {
        list: this.selectedRows,
        from: this.customPricingTimeData.from || '',
        to: this.customPricingTimeData.to || ''
      }
      this.$http({
        url: '/api-sup-ce/pr/requirementManage/createPurchaseOrder',
        method: 'POST',
        data: creatTimeData,
        loading: true
      })
        .then(data => {
          this.purOrderList = data.data.map(({ ceeaDeliveryPlace, ...rest }) => {
            let d = null
            try {
              d = JSON.parse(ceeaDeliveryPlace)
            } catch (e) {
              console.log(e)
            }
            return {
              ...rest,
              ceeaDeliveryPlace: d
            }
          })
          this.dialogFormVisible3 = true
        })
        .catch(err => {
          console.log(err)
        })
    },
    createOrder () {
      if (this.selectedRows.length === 0) {
        this.$message.warning(this.$t('purchaseDemand.selectData'))
        return
      }
      for (const i of this.selectedRows) {
        // 是否目录化为【是】就不要校验了
        if (i.ceeaIfDirectory === 'N' && (i.haveSupplier === 'N' || i.haveEffectivePrice === 'N')) {
          this.$message.warning(this.$t('purchaseDemand.selectData1'))
          return
        }
      }
      this.$http({
        url: '/api-sup-ce/pr/requirementManage/customPricingTime',
        method: 'POST',
        data: this.selectedRows,
        loading: true
      }).then(data => {
        if (data) {
          if (data.data) {
            this.customPricingTime = true
          } else if (!data.data) {
            this.$http({
              url: '/api-sup-ce/pr/requirementManage/createPurchaseOrder',
              method: 'POST',
              data: { list: this.selectedRows },
              loading: true
            })
              .then(data => {
                const mapList = new Map()
                this.orderTypeList.forEach(item => {
                  mapList.set(item.dictItemCode, item.dictItemName)
                })

                this.purOrderList = data.data.map((row, i) => {
                  let ceeaPurchaseTypeName = ''
                  if (mapList.has(row.ceeaPurchaseType)) {
                    ceeaPurchaseTypeName = mapList.get(row.ceeaPurchaseType)
                  }
                  return {
                    sortIndex: ++i,
                    ...row,
                    ceeaIfSupplierConfirm: 'Y',
                    ceeaPurchaseTypeName
                  }
                })
                console.log(this.purOrderList, 'this.purOrderList')
                this.dialogFormVisible3 = true
              })
              .catch(err => {
                console.log(err)
              })
          }
        }
      })
    },
    async getOrderIds () {
      const getIds = await this.$http({
        url: '/api-sup-ce/pr/requirementManage/submitPurchaseOrder',
        method: 'POST',
        data: this.currentRows,
        loading: true
      })
      return getIds.data
    },
    async submitOneItem3 () {
      if (this.currentRows.length === 0) {
        this.$message.warning(this.$t('purchaseDemand.selectData'))
        return
      }
      for (let item of this.currentRows) {
        let requirementDateTime = new Date(item.requirementDate).getTime()
        let nowTime = new Date(new Date().toLocaleDateString()).getTime() // 获取当天0点时间戳
        if (requirementDateTime < nowTime) {
          // 要求到货日期,应晚于当前日期！
          return this.$message.warning(this.$t('demandPoolManagement.prompt5'))
        }
        Object.assign(item, this.requirementHead)
      }

      await this.getOrderIds()

      this.dialogFormVisible3 = false
      // this.getQuerydata()
      this.$router.push({
        name: 'buyerPurchaseOrder',
        params: { from: 'demandPoolManagement' }
      })
    },
    // 选择订单
    selectOrderList (selects) {
      console.log(selects, 'selectOrderList')
      this.selectOrderList = selects
    },
    // 确认跳转订单详情页
    async submitOrderDetai () {
      this.isCreateOrderVisible = false
      const tabs = []
      for (const row of this.selectOrderList) {
        if (row.orderId) {
          console.log(row.orderId, 'orderId')
          tabs.push({
            component: purchaseOrderDetail,
            name: `purchaseOrderDetail${row.orderId}`,
            title: this.$t('dataConfMod.order') + row.orderId, // 采购订单单据
            params: {
              flag: 'approvalOnly',
              showType: 'readOnly',
              row: {
                orderId: row.orderId
              }
            }
          })
        }
      }
      this.$emit('tab-add', tabs)
    },

    /* 确定生成寻源单 */
    submitOrionOrder () {
      if (!this.globalSourceType) {
        return this.$message.error(this.$t('purchaseDemand.addOneItem2Tips1'))
      }

      this.$http({
        url: `/api-sup-ce/pr/requirementManage/createSourceForm/${this.globalSourceType}`,
        method: 'POST',
        data: this.selectedRows,
        loading: true
      }).then(data => {
        if (((data || {}).data || {}).businessId && ((data || {}).data || {}).businessNo) {
          this.$confirm(`${this.$t('purchaseDemand.addOneItem2Tips2')}${data.data.businessNo}${this.$t('purchaseDemand.addOneItem2Tips3')}`, {
            confirmButtonText: this.$t('common.confirm'),
            cancelButtonText: this.$t('common.cancel'),
            type: 'warning'
          }).then(() => {
            this.$message.success(this.$t('common.success'))
            this.dialogFormVisible2 = false
            const source = this.sourceTypeList.find(item => item.value === this.globalSourceType)
            this.$router.push({
              name: source.componentName,
              params: {
                from: 'demandPoolManagement',
                funName: source.componentName,
                formId: data.data.businessId,
                formNo: data.data.businessNo,
                type: 'edit'
              }
            })
          }).catch(() => {
            this.dialogFormVisible2 = false
          })
        }
      })
    },
    formatterStatus (row, column, cellValue, index) {
      return this.$getDictLabel('RELATED_DOCUMENTS', cellValue)
    },
    saveOneItem3 () {
      this.dialogFormVisible3 = false
    },
    changeInquiry () {
      if (this.checkChangeData.length === 0) {
        this.$message.warning(this.$t('purchaseDemand.selectData'))
        return
      }
      if (this.checkChangeData.length > 1) {
        // 只能选择一张单据进行变更
        this.$message.warning(this.$t('cusEntry.supplement20250121.promptTips44'))
        return
      }
      this.$emit('tab-add', {
        component: purchaseApplicationDetail2,
        params: {
          flag: 'change',
          row: this.checkChangeData[0],
          showType: 'readOnly',
          tabName: 'purchaseApplicationDetail' + this.checkChangeData[0]?.requirementHeadNum,
          activeWorkflowTab: false
        },
        title: this.checkChangeData[0]?.requirementHeadNum,
        name: 'purchaseApplicationDetail' + this.checkChangeData[0]?.requirementHeadNum
      })
    },
    openAssignOne () {
      if (this.checkChangeData.length === 0) {
        this.$message.warning(this.$t('purchaseDemand.selectData'))
        return
      }
      if (this.checkChangeData.length > 1) {
        for (let i = 1; i < this.checkChangeData.length; i++) {
          if (this.checkChangeData[0].organizationId !== this.checkChangeData[i].organizationId) {
            return this.$message.warning(this.$t('purchaseDemand.openAssignOneTips1'))
          }
        }
      }
      // for (const i in this.form) {
      //   this.form[i] = null
      // }
      console.log(this.checkChangeData[0], 'name2')
      this.$set(this.form, 'nickname', this.checkChangeData[0].username2)
      this.$set(this.form, 'nickname2', this.checkChangeData[0].username1)
      this.$set(this.form, 'ceeaStrategyUserId', this.checkChangeData[0].userId2)
      this.$set(this.form, 'ceeaStrategyUserId2', this.checkChangeData[0].userId1)
      this.dialogFormVisible1 = true
    },
    assignOne () {
      if (this.checkChangeData.length === 0) {
        this.$message.warning(this.$t('purchaseDemand.selectData'))
        return
      }
      let idList = []
      this.checkChangeData.forEach(e => {
        idList.push(e.requirementHeadId)
      })
      let obj = {
        groupUsers: {
          'VENDOR': this.form.ceeaStrategyUserId,
          'SOU': this.form.ceeaStrategyUserId2
        },
        requirementHeadIds: idList
      }
      const saveData = transformMQL.save('PrSouRequirementPoolForBuyer', [obj], 'batchAssign')
      qa.batchAssign(saveData).then((datas) => {
        this.$message.success(this.$t('common.successConfirm'))
        this.dialogFormVisible1 = false
        this.$nextTick(() => {
          this.getQuerydata(this.preFormObj)
        })
      })
    },
    readPurchaseApplication (row) {
      this.$emit('tab-add', {
        component: purchaseApplicationDetail2,
        params: {
          flag: 'approveNumber',
          row: row,
          showType: 'readOnly',
          tabName: 'purchaseApplicationDetail' + row.requirementHeadNum,
          activeWorkflowTab: false
        },
        title: row.requirementHeadNum,
        name: 'purchaseApplicationDetail' + row.requirementHeadNum
      })
    },
    syncFilterParams (values) {
      this.queryParam = values
    },
    rejectReasonConfirm () {
      if (!this.rejectReason.reasonDesc) return

      this.$http({
        url: '/api-sup-ce/pr/requirementManage/batchReturn',
        method: 'POST',
        data: {
          requirementLineIds: this.selectedRows.map(v => v.requirementLineId),
          rejectReason: this.rejectReason.reasonDesc
        },
        loading: true
      })
        .then(data => {
          // 退回查询条件判断，使用初始查询条件或者更改查询后条件
          const queryConditions =
            Object.keys(this.queryParam).length > 0 ? this.queryParam : this.preFormObj
          this.$message.success(this.$t('common.success'))
          this.getQuerydata(queryConditions)
          this.rejectReasonDialog = false
        })
        .catch(err => {
          console.log(err)
        })
    },
    rejectOne () { // 邀请供应商
      const checkChangeData = this.checkChangeData
      if (checkChangeData.length == 0) {
        // 请选择一条单据进行操作
        this.$message.error(this.$t('cusEntry.supplement20250121.promptTips37'))
        return false
      }
      let bol = false
      let bol2 = false
      let obj = []
      checkChangeData.forEach(e => {
        obj.push({ requirementHeadId: e.requirementHeadId })
        if (e.needPublic == 'Y') {
          bol = true
        }
        if (e.hasCreateVendorRecommend == 'Y') {
          bol2 = true
        }
      })
      if (bol) {
        // 所勾选行存在公示的需求，不允许直接创建推荐单
        this.$message.error(this.$t('cusEntry.supplement20250121.promptTips45'))
        return false
      }
      if (bol2) {
        // 所勾选数据存在已创建供应商推荐，请前往推荐供应商菜单查看
        this.$message.error(this.$t('cusEntry.supplement20250121.promptTips46'))
        return false
      }

      const searchData = transformMQL.save('PrSouRequirementPoolForBuyer', obj, 'createVendorRecommend')
      planPool.createVendorRecommend(searchData).then((datas) => {
        console.log(datas, 'datas')
        // TODO
        const dataObj = datas.data?.records[0]
        console.log(dataObj?.projectId, 'projectId')
        if (dataObj?.projectId) {
          this.$router.push({
            name: 'recommendVendor',
            params: {
              from: 'demandPoolManagementZhaobiao', // 来源路由name
              row: {
                projectId: dataObj?.projectId,
                souNo: dataObj?.souNo
              }
            }
          })
        }
      })
    },
    openFollowDialog (row) {
      this.$http({
        url: '/api-sup-ce/documents/subsequentDocuments/subsequentDocumentsList',
        method: 'POST',
        data: { requirementLineId: row.requirementLineId },
        loading: true
      })
        .then(data => {
          this.followOrderList = data.data
          this.dialogFormVisible4 = true
        })
        .catch(err => {
          console.log(err)
        })
    },
    setCurrentRows2 (val) {
      this.currentRows = val
    }
  }
}
</script>

<style lang="scss" scoped>
.is-create-order {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}
.inportAbcd .el-main {
  height: 70px;
}
</style>
