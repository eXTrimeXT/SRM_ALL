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
        @synchronous-value="syncFilterParams"
      />

      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <AuthorityButton
            type="primary"
            @click="openAssignOne"
          >
            {{ $t('purchaseDemand.distributionOrTransfer') }}
          </AuthorityButton>
          <AuthorityButton
            @click="rejectOne"
          >
            供应商推荐
          </AuthorityButton>
          <!-- 创建寻源单据 -->
          <AuthorityButton
            @click="createInquiry"
          >
            {{ $t('purchaseDemand.createInquiry') }}
          </AuthorityButton>
          <!-- 创建寻源单据 -->
          <AuthorityButton
            @click="createInquiryBiaoshu"
          >
            拟定标书
          </AuthorityButton>

          <AuthorityButton
            @click="requirementCancellation"
          >
            需求取消
          </AuthorityButton>

          <AuthorityButton
            @click="changeInquiry"
          >
            需求变更
          </AuthorityButton>

          <!-- 导出 -->
          <ExportExcel
            type="default"
            pageUrl="/api-sup-ce/api-ql/PrSouRequirementPoolForBuyer/querySouPool"
            :tableHeader="tableHeader"
            :dictCodes="dictCodes"
            :generateMeiQLExportRequest="generateMeiQLExportRequest"
            exportMode='front'
            exportType='meiqlApi'
          />
        </template>
      </MainHeader>

      <TableView
        :ref="gridId"
        big-data
        :table-data="tableData"
        :table-header="tableHeader"
        :check-change="handleCurrentChange"
        :page-size="pageSize"
        :checkbox="true"
        :pre-query-data="queryParam"
        :open-custom-table="false"
        :adept-mei-ql="true"
        url="/api-sup-ce/api-ql/PrSouRequirementPoolForBuyer/querySouPool"
        :checkChange="checkChange"
        @getFooter="getFooter"
        @getFooterSize="getFooterSize"
        @afterQuery="afterQuery"
      >
        <template #fileInfos="{ scope }">
          <template>
            <div class="download-link-wrap">
              <SrmCommonFile
                :default-file="{
                    fileId: scope.row.souReqHead?.requireProductFileId,
                    fileName: scope.row.souReqHead?.requireProductFileName
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
                    fileId: scope.row.souReqHead?.deliveryDayFileId,
                    fileName: scope.row.souReqHead?.deliveryDayFileName
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
                    fileId: scope.row.souReqHead?.putIntoUseDateFileId,
                    fileName: scope.row.souReqHead?.putIntoUseDateFileName
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
              <el-form-item
                label="请选择供应商负责人"
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
              <el-form-item
                label="请选择技术负责人"
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
          />
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
                format="yyyy-MM-dd"
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
          />
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
                  format="yyyy-MM-dd"
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
                  format="yyyy-MM-dd"
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
import { planPool } from 'modc@/buyer/purchasingDemand/api'
import { qa } from 'modc@/buyer/purchasingDemand/api'

export default {
  name: 'DemandPoolList',
  components: {
    TableView,
    MainHeader,
    ExportExcel,
    FormWrapper,
    RenderAsyncText,
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
      createOrderList: [],
      isCreateOrderVisible: false,
      name: 'materialAssignRuleTable',
      tableName: 'materialAssignRuleTable',
      orderTypeBol: false,
      dictCodes: {
        ceeaPurchaseType: 'PURCHASE_TYPE',
        applyStatus: 'APPLICATION_STATUS',
        ceeaIfDirectory: 'YES_OR_NO',
        haveSupplier: 'YES_OR_NO',
        haveEffectivePrice: 'YES_OR_NO',
        ifCreateBid: 'YES_OR_NO',
        ifCreateOrder: 'YES_OR_NO',
        ifHold: 'YES_OR_NO',
        demandType: 'DEMAND_TYPE'
      },
      reviewFormNumber: '',
      gridData: [],
      pageSize: 15,
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
      preFormObj: {},
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
      getFooterNum: null,
      getFooterSizeNum: null
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
        label: '申请编号',
        width: 130,
        showType: 'button',
        btnStyle: 'text',
        formattor: val => val || '--',
        callback: row => this.readPurchaseApplication(row)
      },
      {
        prop: 'souReqHead',
        label: '月份',
        width: 130,
        formattor: val => val?.projectMonth
      },
      {
        prop: 'souReqHead',
        label: '需求来源',
        width: 130,
        formattor: val => this.$getDictLabel('PR_SOU_REQUIREMENT_FROM', val?.requireFrom)
      },
      {
        prop: 'categoryName',
        label: '所属品类',
        width: 110
      },
      {
        prop: 'souReqHead',
        label: '项目名称',
        width: 100,
        formattor: val => val?.projectName
      },
      {
        prop: 'souReqHead',
        label: '项目所在地',
        minWidth: 150,
        formattor: val => val?.projectAddress
      },
      {
        prop: 'souReqHead',
        label: '数量/规模',
        width: 100,
        formattor: val => val.requireQuantity
      },
      {
        prop: 'souReqHead',
        label: '概算金额（万元）',
        width: 150,
        formattor: val => val.totalAmountByTenKilo
      },
      {
        prop: 'souReqHead',
        label: '所属板块',
        width: 110,
        formattor: val => val.orgBuName
      },
      {
        prop: 'orgName',
        label: '公司',
        width: 100
      },
      {
        prop: 'ceeaDepartmentName',
        label: '申请部门',
        width: 100
      },
      {
        prop: 'souGroupList',
        label: '技术负责人',
        width: 100,
        formattor: val => {
          let perPlan = ''
          if (val) {
            val.forEach(e => {
              if (e.groupType == 'TECH') {
                perPlan = e.fullName
              }
            })
          }
          return perPlan
        }
      },
      {
        prop: 'souGroupList',
        label: '技术负责人联系方式',
        width: 150,
        formattor: val => {
          let perPlan = ''
          if (val) {
            val.forEach(e => {
              if (e.groupType == 'TECH') {
                perPlan = e.phone
              }
            })
          }
          return perPlan
        }
      },
      {
        prop: 'souReqHead',
        label: '前置技术交流意向',
        width: 150,
        formattor: val => this.$getDictLabel('YES_OR_NO', val.prefixTechDiscussion)
      },
      {
        prop: 'souReqHead',
        label: '是否公示',
        width: 120,
        formattor: val => this.$getDictLabel('YES_OR_NO', val.needPublic)
      },
      {
        prop: 'souReqHead',
        label: '公示截止时间',
        width: 120,
        formattor: val => val.publicEndTime
      },
      {
        prop: 'sendSouProfileEndTime',
        label: '递交招标申请资料时间',
        width: 150
      },
      {
        prop: 'souReqHead',
        label: '分配状态',
        width: 120,
        formattor: val => val.hasAssigned
      },
      {
        prop: 'souGroupList',
        label: '供应商负责人',
        width: 120,
        formattor: val => {
          let perPlan = ''
          if (val) {
            val.forEach(e => {
              if (e.groupType == 'VENDOR') {
                perPlan = e.username
              }
            })
          }
          return perPlan
        }
      },
      {
        prop: 'souGroupList',
        label: '供应商负责人所在科室',
        width: 120,
        formattor: val => {
          let perPlan = ''
          if (val) {
            val.forEach(e => {
              if (e.groupType == 'VENDOR') {
                perPlan = e.departmentName
              }
            })
          }
          return perPlan
        }
      },
      {
        prop: 'souGroupList',
        label: '招标负责人',
        width: 120,
        formattor: val => {
          let perPlan = ''
          if (val) {
            val.forEach(e => {
              if (e.groupType == 'SOU') {
                perPlan = e.username
              }
            })
          }
          return perPlan
        }
      },
      {
        prop: 'souGroupList',
        label: '招标负责人所在科室',
        width: 120,
        formattor: val => {
          let perPlan = ''
          if (val) {
            val.forEach(e => {
              if (e.groupType == 'SOU') {
                perPlan = e.departmentName
              }
            })
          }
          return perPlan
        }
      },
    {
      prop: 'souReqHead',
      label: '意向金金额（元）',
      width: 120,
      formattor: val => val.earnestMoney
    },
      {
        prop: 'souReqHead',
        label: '需求审批完成时间',
        width: 120,
        formattor: val => val.approvalPassTime
      },
      {
        prop: 'souReqHead',
        label: '需求状态',
        width: 120,
        formattor: val => this.$getDictLabel('PR_SOU_REQUIREMENT_STATUS', val?.souReqStatus)
      },
      {
        prop: 'souReqHead',
        label: '未提报月度计划原因',
        width: 150,
        formattor: val => val.noReportMonthPlanReason
      },
      {
        prop: 'souReqHead',
        label: '计划编号',
        width: 120,
        formattor: val => val.planNo
      },
      {
        prop: 'souReqHead',
        label: '特殊招标类型',
        width: 120,
        formattor: val => this.$getDictLabel('PR_SOU_REQUIREMENT_SPECIAL_TYPE', val?.specialSouType)
      },
      {
        prop: 'souReqHead',
        label: '特定原因',
        width: 120,
        formattor: val => this.$getDictLabel('PR_SOU_REQUIREMENT_SPECIAL_REASON', val?.specialReason)
      },
      {
        prop: 'souReqHead',
        label: '需求产生时间',
        width: 120,
        formattor: val => val.requireProductDate
      },
      {
        prop: 'fileInfos',
        label: '需求产生时间附件',
        width: 150,
        showType: 'slot',
        slot: 'fileInfos'
      },
      {
        prop: 'souReqHead',
        label: '工期交货期',
        width: 120,
        formattor: val => val.deliveryDay
      },
      {
        prop: 'fileInfos2',
        label: '工期交货期附件',
        width: 150,
        showType: 'slot',
        slot: 'fileInfos2'
      },
      {
        prop: 'souReqHead',
        label: '投入使用时间',
        width: 120,
        formattor: val => val.putIntoUseDate
      },
      {
        prop: 'fileInfos3',
        label: '投入使用时间附件',
        width: 150,
        showType: 'slot',
        slot: 'fileInfos3'
      },
      {
        prop: 'souReqHead',
        label: '剩余时间',
        width: 120,
        formattor: val => val.remainingDay
      },
      {
        prop: 'souReqHead',
        label: '其他特殊原因补充',
        width: 120,
        formattor: val => val.otherSpecialReason
      },
      {
        prop: 'souReqHead',
        label: '推荐单位名称',
        width: 120,
        formattor: val => val.vendorName
      },
      {
        prop: 'souReqHead',
        label: '联系方式',
        width: 120,
        formattor: val => val.phone
      },
      {
        prop: 'souReqHead',
        label: '邮箱',
        width: 120,
        formattor: val => val.email
      },
      {
        prop: 'souReqHead',
        label: '推荐单位来源',
        width: 120,
        formattor: val => val.recommendFrom
      },
      {
        prop: 'souReqHead',
        label: '是否变更',
        width: 120,
        formattor: val => val.changeRequirementHeadId ? '是' : '否'
      },
      {
        prop: 'souReqHead',
        label: '关联变更需求单号',
        width: 120,
        formattor: val => val.changeRequirementHeadNum
      },
      {
        prop: 'souReqHead',
        label: '技术要求',
        width: 120,
        formattor: val => val.techRequire
      },
      {
        prop: 'souReqHead',
        label: '业绩要求',
        width: 120,
        formattor: val => val.performanceRequire
      },
      {
        prop: 'vendorQualificationRequire',
        label: '其他供应商资质说明',
        width: 150
      },
      {
        prop: 'applyByNickname',
        label: '申请人',
        width: 120
      },
      {
        prop: 'applyByNickname',
        label: '查看定标信息',
        width: 120,
        showType: 'button',
        btnStyle: 'text',
        formattor: val => '查看',
        callback: row => this.readPurchaseApplication(row)
      },
      {
        prop: 'souReqHead',
        label: '是否已创供应商推荐',
        width: 120,
        formattor: val => this.$getDictLabel('YES_OR_NO', val.hasCreateVendorRecommend)
      },
      {
        prop: 'souReqHead',
        label: '是否已创建寻源需求',
        width: 120,
        formattor: val => this.$getDictLabel('YES_OR_NO', val.hasCreateVendorRecommend)
      },
      {
        prop: 'souReqHead',
        label: '是否已递交招标资料',
        width: 120,
        formattor: val => this.$getDictLabel('YES_OR_NO', val.hasSendSouProfile)
      },
      {
        prop: 'souReqHead ',
        label: '招标资料状态',
        width: 120,
        formattor: val => this.$getDictLabel('SEND_SOU_PROFILE_STATUS', val?.sendSouProfileStatus)
      },
      {
        prop: 'souReqHead',
        label: '是否已发布标书',
        width: 120,
        formattor: val => this.$getDictLabel('YES_OR_NO', val.hasCreateSouReq)
      },
      {
        prop: 'souReqHead',
        label: '需求取消原因',
        width: 120,
        formattor: val => val.reqCancelReason
      },
    ]
    this.defaultTableHeader = this.tableHeader

    this.preArr = [
      // 需求类型
      {
        prop: 'demandType',
        label: () => this.$t('purchaseDemand.demandType'),
        type: 'dict',
        code: 'DEMAND_TYPE'
      },
      {
        prop: 'categoryCode',
        label: '品类'
      },
      {
        prop: 'projectName',
        label: '项目名称'
      },
      {
        prop: 'projectAddress',
        label: '项目所在地'
      },
      {
        prop: 'techUsername',
        label: '技术负责人',
        type: 'quicksearch',
        showKey: 'username',
        name: 'scc_rbac_user_display'
      },
      {
        prop: 'souUsername',
        label: '招标负责人',
        type: 'quicksearch',
        showKey: 'username',
        name: 'scc_rbac_user_display'
      },
      // 申请时间
      {
        prop: 'dateList',
        label: () => this.$t('purchaseDemand.applyDate'),
        type: 'daterange'
      },
      {
        prop: 'applyBy',
        label: () => this.$t('purchaseDemand.applicant')
      }, // 申请人
      {
        prop: 'departmentId',
        label: () => this.$t('purchaseDemand.ceeaDepartment')
      }, // 申请部门
      {
        prop: 'vendorDepartmentId',
        label: '供应商负责人所在科室'
      },
      {
        prop: 'requirementHeadNum',
        label: '需求申请编号'
      },
      {
        prop: 'hasAssigned',
        label: '是否已分配',
        type: 'dict',
        code: 'YES_OR_NO'
      },
      {
        prop: 'requireFrom',
        label: '需求来源',
        type: 'dict',
        code: 'PR_SOU_REQUIREMENT_FROM'
      },
      {
        prop: 'hasSendSouProfile',
        label: '是否已递交招标资料',
        type: 'dict',
        code: 'YES_OR_NO'
      },
      {
        prop: 'sendSouProfileEndTimes',
        label: '递交申请资料时间(不确定)',
        type: 'daterange'
      }
    ]
    // this.preFormObj = { ifHaveOrderQuantity: 'Y', ifHold: 'N' }
    this.$nextTick(() => {
      this.getQuerydata(this.preFormObj)
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
    generateMeiQLExportRequest () {
      let queryParam = JSON.parse(JSON.stringify(this.queryParam))
      console.log(queryParam, 'queryParam')
      queryParam.type = 'PrSouRequirementPoolForBuyer'
      queryParam.action = 'querySouPool'
      const all = {
        body:queryParam
      }
      return all
    },
    getUserObj (val, scope) {

    },
    getUserObj10 (val, scope) {
      console.log(val, 'val')
      this.form.ceeaStrategyUserId = val?.userId
    },
    getUserObj11 (val, scope) {
      console.log(val, 'val')
      this.form.ceeaStrategyUserId2 = val?.userId
    },
    requirementCancellation () {
      const checkChangeData = this.checkChangeData
      if (checkChangeData.length > 1) {
        this.$message.error('需求取消只能选择一条单据取消，不支持多条同时取消')
        return false
      } else if (checkChangeData.length <= 0) {
        this.$message.error('请选择一条单据进行取消')
        return false
      }
      this.$emit('tab-add', {
        component: withdrawZhaobiao,
        params: {
          flag: 'add',
          row: this.checkChangeData[0],
          tabName: 'withdrawZhaobiao'
        },
        title: '新增取消单',
        name: 'withdrawZhaobiao'
      })
      // const saveData = transformMQL.save('PrSouRequirementCancelForBuyer', [this.checkChangeData[0]], 'tempSaveReqCancel')
      // planPool.tempSaveReqCancel(saveData).then((datas) => {
      //   this.$message({
      //     message: '已生成取消单',
      //     type: 'success'
      //   })
      // })
    },
    checkChange (records) {
      this.checkChangeData = records
    },
    afterQuery (data) {
      console.log(data, 'data')
      this.$refs[this.gridId].setTableData(async tableData => {
        tableData.forEach((item, index) => {
          const { souGroupList, souReqHead, ...res } = item
          const obj = {
            ...res,
            ...souReqHead,
            souGroupList: souGroupList
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
        this.$message.warning(this.$t('purchaseDemand.minLimitMsg')) // 请至少选择一条目标数据
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
      const { dateList, sendSouProfileEndTimes, ...rest } = obj || this.queryParam
      const params = {}
      if (dateList) {
        params.applyDateFrom = dateList[0]
        params.applyDateTo = dateList[1]
      }
      if (sendSouProfileEndTimes) {
        params.sendSouProfileEndDateFrom = sendSouProfileEndTimes[0]
        params.sendSouProfileEndDateTo = sendSouProfileEndTimes[1]
      }
      params.extBidFlag = 'Y'
      this.queryParam = transformMQL.save('PrSouRequirementPoolForBuyer',[{ ...rest, ...params }],'querySouPool')
      console.log(this.queryParam, 'queryParam')
      this.getFooterNum = 1
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    handleCurrentChange (val) {
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
        `导入模板_${new Date().getTime()}.xls`
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
      let obj = []
      checkChangeData.forEach(e => {
        obj.push({requirementHeadId: e.requirementHeadId})
      })
      const datas = [{
        souType: 'bid',
        reqHeadList: obj
      }]

      const searchData = transformMQL.save('PrSouRequirementPoolForBuyer',datas,'createSou')
      planPool.createSou(searchData).then((datas) => {
        // TODO

      })
    },

    createInquiry () {
      const checkChangeData = this.checkChangeData
      if (checkChangeData.length == 0) {
        this.$message.error('请选择一条单据')
        return false
      }
      let obj = []
      checkChangeData.forEach(e => {
        obj.push({requirementHeadId: e.requirementHeadId})
      })

      const searchData = transformMQL.save('PrSouRequirementPoolForBuyer',obj,'createSouReq')
      planPool.createSouReq(searchData).then((datas) => {
        console.log(datas, 'datas')
        const dataObj = datas.data?.records[0]
        if (dataObj.reqHeadId) {
          // TODO
          this.$router.push({
            name: 'sourcingRequireBuyer',
            params: {
              from:'demandPoolManagementZhaobiao', // 来源路由name
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
      this.getQuerydata()
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
            title: `订单${row.orderId}`, // 采购订单单据
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
        this.$message.warning('只能选择一张单据进行变更')
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
      for (const i in this.form) {
        this.form[i] = null
      }
      this.dialogFormVisible1 = true
    },
    assignOne () {
      let idList = []
      this.checkChangeData.forEach(e => {
        idList.push(e.requirementHeadId)
      })
      let obj = {
        groupUsers: {
            "SOU": this.form.ceeaStrategyUserId,
            "VENDOR": this.form.ceeaStrategyUserId2
        },
        requirementHeadIds: idList
      }
      const saveData = transformMQL.save('PrSouRequirementPoolForBuyer', [obj], 'batchAssign')
      qa.batchAssign(saveData).then((datas) => {
        this.$message.success('确认成功')
        this.dialogFormVisible1 = false
      })
    },
    readPurchaseApplication (row) {
      // this.$router.push({
      //   name: 'purchaseApplication',
      //   params: {
      //     from: 'demandPoolManagement',
      //     funName: 'purchaseApplication', // 功能
      //     fdSubject: row
      //   }
      // })
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
        this.$message.error('请选择一条单据')
        return false
      }
      let obj = []
      checkChangeData.forEach(e => {
        obj.push({requirementHeadId: e.requirementHeadId})
      })

      const searchData = transformMQL.save('PrSouRequirementPoolForBuyer',obj,'createVendorRecommend')
      planPool.createVendorRecommend(searchData).then((datas) => {
        console.log(datas,'datas')
        // TODO
        const dataObj = datas.data?.records[0]
        console.log(dataObj?.projectId, 'projectId')
        if (dataObj?.projectId) {
          this.$router.push({
            name: 'recommendVendor',
            params: {
              from:'demandPoolManagementZhaobiao', // 来源路由name
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
