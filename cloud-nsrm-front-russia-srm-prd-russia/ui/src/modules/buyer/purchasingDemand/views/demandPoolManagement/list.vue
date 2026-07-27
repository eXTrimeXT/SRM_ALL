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
            code="pr:demandPoolManagement:openAssignOne"
            type="primary"
            @click="openAssignOne"
          >
            {{ $t('purchaseDemand.distributionOrTransfer') }}
          </AuthorityButton>
          <AuthorityButton
            code="pr:demandPoolManagement:rejectOne"
            @click="rejectOne"
          >
            {{ $t('purchaseDemand.reject') }}
          </AuthorityButton>
          <!-- 创建寻源单据 -->
          <AuthorityButton
            code="pr:demandPoolManagement:createInquiry"
            @click="createInquiry"
          >
            {{ $t('purchaseDemand.createInquiry') }}
          </AuthorityButton>
          <!-- 创建采购订单 -->
          <AuthorityButton
            code="pr:demandPoolManagement:createOrder"
            @click="createOrder"
          >
            {{ $t('purchaseDemand.createOrder') }}
          </AuthorityButton>

          <el-dropdown style="margin:0 11px">
            <el-button>
              {{ $t('purchaseDemand.ifHold') }}<i class="el-icon-arrow-down el-icon--right" />
            </el-button>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item>
                <div style="padding-bottom: 5px;">
                  <el-button
                    @click="doHold"
                  >
                    {{ $t('purchaseDemand.newHold') }}
                  </el-button>
                </div>
              </el-dropdown-item>
              <el-dropdown-item>
                <el-button
                  @click="cancelHold"
                >
                  {{ $t('purchaseDemand.cancelHold') }}
                </el-button>
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>

          <ExportExcel
            page-url="/api-sup-ce/pr/requirementManage/listPageByParam"
            export-mode="front"
            :table-header="tableHeader"
            :dict-codes="dictCodes"
            :filter-params="queryParam"
            :timeout="10000000"
            type="default"
          />
          <!-- 分配供应商 -->
          <!-- <AuthorityButton
            style="margin:0 11px"
            @click="assignSupplier">
            {{ $t("purchaseDemand.assignSupplier") }}
          </AuthorityButton> -->
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
        :open-custom-table="true"
        url="/api-sup-ce/pr/requirementManage/listPageByParam"
        @getFooter="getFooter"
        @getFooterSize="getFooterSize"
        @afterQuery="afterQuery"
      />
      <srm-dialog
        :title="$t('purchaseDemand.assignBuyer')"
        size="small"
        :visible.sync="dialogFormVisible"
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
          <el-row type="flex">
            <el-col>
              <!-- 寻源策略 -->
              <el-form-item
                :label="$t('purchaseDemand.ceeaStrategyUser')"
                :label-width="formLabelWidth"
              >
                <el-select
                  v-model="form.ceeaStrategyUserId"
                  filterable
                  @change="setUserObj2(form)"
                >
                  <el-option
                    v-for="item in strategyList"
                    :key="item.personInChargeUserId"
                    :label="item.personInChargeNickname"
                    :value="item.personInChargeUserId"
                  />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row type="flex">
            <el-col>
              <!-- 订单履行 -->
              <el-form-item
                :label="$t('purchaseDemand.performUserNickname')"
                :label-width="formLabelWidth"
              >
                <el-select
                  v-model="form.ceeaPerformUserId"
                  filterable
                  @change="setUserObj3(form)"
                >
                  <el-option
                    v-for="item in carryOutList"
                    :key="item.personInChargeUserId"
                    :label="item.personInChargeNickname"
                    :value="item.personInChargeUserId"
                  />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>

        <div
          slot="footer"
          class="dialog-footer"
        >
          <el-button @click="dialogFormVisible = false">
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
import { getDictItem } from '@/api/common'
import { adaptDictData, parseTime, getValidateFailureSequence } from '@/utils'
import RenderAsyncText from '@/library/components/provice-city/renderAsyncText'
import ExportExcel from 'lib@/components/export-excel'
import purchaseOrderDetail from 'modb@/orderManagementBuyer/views/buyerPurchaseOrder/purchaseOrderDetail.vue'

export default {
  name: 'DemandPoolList',
  components: {
    TableView,
    MainHeader,
    ExportExcel,
    FormWrapper,
    RenderAsyncText
  },
  provide () {
    return { context: this }
  },
  data () {
    return {
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
      dialogFormVisible: false,
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
      // 业务实体
      {
        prop: 'orgName',
        label: this.$t('purchaseDemand.businessEntity'),
        width: 130
      },
      // 库存组织
      {
        prop: 'organizationName',
        label: this.$t('purchaseDemand.invOrg'),
        width: 130
      },
      // 采购申请编号
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
        prop: 'applyStatus',
        label: () => this.$t('purchaseDemand.applyStatus'), // 单据状态
        formattor: val => this.$getDictLabel('APPLICATION_STATUS', val),
        width: 110
      },
      // 物料编码
      {
        prop: 'materialCode',
        label: this.$t('purchaseDemand.itemCode'),
        width: 100
      },
      // 物料名称
      {
        prop: 'materialName',
        label: this.$t('purchaseDemand.itemName'),
        minWidth: 150
      },
      // 需求数量
      {
        prop: 'requirementQuantity',
        label: this.$t('purchaseDemand.requirementQuantity'),
        width: 100
      },
      // 剩余可下单数量
      {
        prop: 'orderQuantity',
        label: this.$t('purchaseDemand.orderQuantity'),
        width: 110
      },
      // 需求日期
      {
        prop: 'requirementDate',
        label: this.$t('purchaseDemand.requirementDate'),
        width: 110
      },
      // 是否货源
      {
        prop: 'haveSupplier',
        label: this.$t('purchaseDemand.ifHaveSupplier'),
        width: 100,
        formattor: val => (val === 'Y' ? this.$t('common.yes') : this.$t('common.no'))
      },
      // 有效价格
      {
        prop: 'haveEffectivePrice',
        label: this.$t('purchaseDemand.ifHaveEffectivePrice'),
        width: 100,
        formattor: val => (val === 'Y' ? this.$t('common.yes') : this.$t('common.no'))
      },
      // 物料小类
      {
        prop: 'categoryName',
        label: this.$t('purchaseDemand.materialCateSub'),
        width: 100
      },
      // 需求类型
      {
        prop: 'demandType',
        label: () => this.$t('purchaseDemand.demandType'),
        width: 120,
        formattor: val => this.$getDictLabelByValue(this.demandTypeList, val)
      },
      // 采购类型
      {
        prop: 'ceeaPurchaseType',
        label: this.$t('purchaseDemand.purchaseType'),
        width: 100,
        formattor: val => this.$getDictLabelByValue(this.purchaseTypeList, val)
      },
      // 寻源策略
      {
        prop: 'ceeaStrategyUserNickname',
        label: this.$t('purchaseDemand.ceeaStrategyUser'),
        width: 100
      },
      // 订单履行
      {
        prop: 'ceeaPerformUserNickname',
        label: this.$t('purchaseDemand.performUserNickname'),
        width: 100
      },
      // 申请日期
      {
        prop: 'applyDate',
        label: this.$t('purchaseDemand.applyDate'),
        width: 100
      },
      // 申请行号
      {
        prop: 'rowNum',
        label: this.$t('purchaseDemand.rowNum'),
        width: 100
      },
      // 单位
      {
        prop: 'unit',
        label: this.$t('purchaseDemand.unitCode'),
        width: 80
      },
      // 是否目录化
      {
        prop: 'ceeaIfDirectory',
        label: this.$t('purchaseDemand.ceeaIfCatalogMaterial'),
        width: 110,
        formattor: val => (val === 'Y' ? this.$t('common.yes') : this.$t('common.no'))
      },
      // 是否创建订单
      {
        prop: 'ifCreateOrder',
        label: this.$t('purchaseDemand.ifCreateOrder'),
        width: 120,
        formattor: val => (val === 'Y' ? this.$t('common.yes') : this.$t('common.no'))
      },
      // 是否创建寻源
      {
        prop: 'ifCreateBid',
        label: this.$t('purchaseDemand.ifCreateBid'),
        width: 100,
        formattor: val => (val === 'Y' ? this.$t('common.yes') : this.$t('common.no'))
      },
      // 是否暂挂
      {
        prop: 'ifHold',
        label: this.$t('purchaseDemand.ifHold'),
        width: 100,
        formattor: val => (val === 'Y' ? this.$t('common.yes') : this.$t('common.no'))
      },
      // 申请部门
      {
        prop: 'ceeaDepartmentName',
        label: this.$t('purchaseDemand.ceeaDepartment'),
        width: 100
      },
      // 申请人
      {
        prop: 'createdFullName',
        label: this.$t('purchaseDemand.applicant'),
        width: 100
      },
      // 后续单据
      {
        prop: 'followFormCode',
        label: this.$t('purchaseDemand.followForm'),
        width: 110,
        showType: 'button',
        formattor: () => this.$t('common.view'),
        btnStyle: 'text',
        callback: row => this.openFollowDialog(row)
      },
      // 需求部门
      {
        prop: 'dmandLineRequest',
        label: this.$t('purchaseDemand.dmandLineRequest'),
        width: 100,
        formattor: val => this.$getDictLabelByValue(this.dmandLineRequestOpts, val)
      },
      // 备注
      {
        prop: 'comments',
        label: this.$t('purchaseDemand.comments1'),
        width: 100
      }
    ]
    this.defaultTableHeader = this.tableHeader

    this.preArr = [
      {
        prop: 'orgIds',
        label: () => this.$t('purchaseDemand.businessEntity'),
        type: 'OUorganizationSelector', // 业务实体
        multiple: true,
        collapseTags: true
      },
      {
        prop: 'organizationIds',
        parentId: 'orgIds',
        label: () => this.$t('purchaseDemand.invOrg'),
        type: 'INVorganizationSelector', // 库存组织
        multiple: true,
        collapseTags: true
      },
      {
        prop: 'ceeaPurchaseType',
        label: () => this.$t('purchaseDemand.purchaseType'),
        type: 'dict', // 采购类型
        code: 'PURCHASE_TYPE'
      },
      // 物料编码
      {
        prop: 'materialCode',
        label: () => this.$t('purchaseDemand.itemCode'),
        type: 'quicksearch',
        showKey: 'materialCode',
        name: 'scc_base_material_item'
      },
      {
        prop: 'categoryIds',
        label: () => this.$t('purchaseDemand.materialCateSub'), // 物料小类
        type: 'quicksearch',
        showKey: 'categoryName',
        propKey: 'categoryId',
        name: 'scc_base_purchase_category4'
      },
      {
        prop: 'requirementHeadNum',
        label: () => this.$t('purchaseDemand.requirementHeadNum')
      }, // 申请编号
      {
        prop: 'applyStatus',
        label: () => this.$t('purchaseDemand.applyStatus'),
        type: 'dict',
        code: 'APPLICATION_STATUS'
      }, // 申请状态
      {
        prop: 'dateList',
        label: () => this.$t('purchaseDemand.applyDate'),
        type: 'daterange'
      },
      {
        prop: 'ceeaStrategyUserNickname',
        label: () => this.$t('purchaseDemand.ceeaStrategyUser')
      }, // 寻源策略
      {
        prop: 'ceeaPerformUserNickname',
        label: () => this.$t('purchaseDemand.performUserNickname')
      }, // 订单履行
      {
        prop: 'ifCreateFollowForm',
        label: () => this.$t('purchaseDemand.ifCreateFollowForm'),
        type: 'select', // 是否创建后续单据
        options: [
          { label: this.$t('common.yes'), value: 'Y' },
          { label: this.$t('common.no'), value: 'N' }
        ]
      },
      {
        prop: 'ceeaDepartmentName',
        label: () => this.$t('purchaseDemand.ceeaDepartment')
      }, // 申请部门
      {
        prop: 'createdFullName',
        label: () => this.$t('purchaseDemand.applicant')
      }, // 申请人
      {
        prop: 'ifHaveOrderQuantity',
        label: () => this.$t('purchaseDemand.ifHaveOrderQuantity'),
        type: 'select', // 是否有剩余下单数量
        options: [
          { label: this.$t('common.yes'), value: 'Y' },
          { label: this.$t('common.no'), value: 'N' }
        ]
      },
      // 先隐藏，后端暂时不能实时获取
      // {
      //   prop: 'haveEffectivePrice',
      //   label: () => this.$t('purchaseDemand.ifHaveEffectivePrice'),
      //   type: 'select', // 有效价格
      //   options: [
      //     { label: this.$t('common.yes'), value: 'Y' },
      //     { label: this.$t('common.no'), value: 'N' }
      //   ]
      // },
      // {
      //   prop: 'haveSupplier',
      //   label: () => this.$t('purchaseDemand.ifHaveSupplier'),
      //   type: 'select', // 是否货源
      //   options: [
      //     { label: this.$t('common.yes'), value: 'Y' },
      //     { label: this.$t('common.no'), value: 'N' }
      //   ]
      // },
      {
        prop: 'ceeaIfDirectory',
        label: () => this.$t('purchaseDemand.ceeaIfCatalogMaterial'),
        type: 'select', // 是否目录化
        options: [
          { label: this.$t('common.yes'), value: 'Y' },
          { label: this.$t('common.no'), value: 'N' }
        ]
      },
      {
        prop: 'followFormCode',
        label: () => this.$t('purchaseDemand.followFormCode')
      }, // 后续单据编号
      {
        prop: 'dmandLineRequest',
        label: () => this.$t('purchaseDemand.dmandLineRequest'),
        type: 'dict',
        code: 'DMAND_LINE_REQUEST'
      }, // 需求人
      {
        prop: 'ifCreateOrder',
        label: () => this.$t('purchaseDemand.ifCreateOrder'),
        type: 'select', // 是否已创建订单
        options: [
          { label: this.$t('common.yes'), value: 'Y' },
          { label: this.$t('common.no'), value: 'N' }
        ]
      },

      {
        prop: 'ifCreateBid',
        label: () => this.$t('purchaseDemand.ifCreateBid'),
        type: 'select', // 是否已创建寻源
        options: [
          { label: this.$t('common.yes'), value: 'Y' },
          { label: this.$t('common.no'), value: 'N' }
        ]
      },
      {
        prop: 'ifHold',
        label: () => this.$t('purchaseDemand.ifHold'),
        type: 'select', // 是否暂挂
        options: [
          { label: this.$t('common.yes'), value: 'Y' },
          { label: this.$t('common.no'), value: 'N' }
        ]
      },
      // 需求类型
      {
        prop: 'demandType',
        label: () => this.$t('purchaseDemand.demandType'),
        type: 'dict',
        code: 'DEMAND_TYPE'
      }
    ]
    this.preFormObj = { ifHaveOrderQuantity: 'Y', ifHold: 'N' }
    // 采购类型
    getDictItem('PURCHASE_TYPE').then(res => {
      this.purchaseTypeList = adaptDictData(res.data, 'dict')
    })
    getDictItem('DMAND_LINE_REQUEST').then(res => {
      this.dmandLineRequestOpts = adaptDictData(res.data, 'dict')
    })
    getDictItem('ORDER_TYPE').then(res => {
      this.orderTypeList = res.data
    })
    getDictItem('IF_DISTRIBUTION_VENDOR').then(res => {
      this.ifDistributionVendorList = adaptDictData(res.data, 'dict')
    })
    // 价格来源
    getDictItem('PRICE_SOURCE').then(res => {
      this.priceSourceList = adaptDictData(res.data, 'dict')
    })
    // 获取需求类型
    getDictItem('DEMAND_TYPE').then(res => {
      this.demandTypeList = adaptDictData(res.data, 'dict')
    })
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
    afterQuery (data) {
      this.$refs[this.gridId].setTableData(async tableData => {
        tableData.forEach((item, index) => this.$set(item, 'sequence', index + 1))
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
    exportList () {
      let params = {}
      if (this.getFooterNum && this.getFooterSizeNum) {
        params = Object.assign(
          {},
          this.queryParam,
          { pageNum: this.getFooterNum },
          { pageSize: this.getFooterSizeNum }
        )
      } else if (this.getFooterNum) {
        params = Object.assign(
          {},
          this.queryParam,
          { pageNum: this.getFooterNum },
          { pageSize: 15 }
        )
      } else {
        params = Object.assign({}, this.queryParam, { pageNum: 1 }, { pageSize: 15 })
      }
      downloadFileLinkByPost(
        '/api-sup-ce/pr/requirementManage/export',
        parseTime(new Date()) + this.$t('purchaseDemand.demandPoolExport'),
        params
      ).catch(() => {
        this.$message.error(this.$t('purchaseDemand.downloadFail'))
      })
    },
    getQuerydata (obj) {
      const { dateList, ...rest } = obj || this.queryParam
      const params = {}
      if (dateList) {
        params.startApplyDate = dateList[0]
        params.endApplyDate = dateList[1]
      }
      this.queryParam = { ...rest, ...params }
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
      this.dialogFormVisible = true
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
    createInquiry () {
      if (this.selectedRows.length === 0) {
        this.$message.warning(this.$t('purchaseDemand.selectData'))
        return
      }
      for (const item of this.selectedRows) {
        if (item.applyStatus !== 'ASSIGNED') {
          this.$message.warning(this.$t('purchaseDemand.applyStatusTips'))
          return
        }
        // if (item.ifCreateBid === 'Y') {
        //   this.$message.warning(this.$t('purchaseDemand.canCreateSourcingTips'))
        //   return
        // }
      }
      let sequences = getValidateFailureSequence(this.selectedRows, 'sequence', row => row.ifCreateBid === 'Y')
      if (sequences) {
        this.$message.warning(`${this.$t('demandPoolManagement.prompt1')}【${sequences}】${this.$t('demandPoolManagement.prompt2')}!`)
        return
      }
      this.globalSourceType = null
      this.dialogFormVisible2 = true
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
    openAssignOne () {
      if (this.selectedRows.length === 0) {
        this.$message.warning(this.$t('purchaseDemand.selectData'))
        return
      }
      if (this.selectedRows.length > 1) {
        for (let i = 1; i < this.selectedRows.length; i++) {
          if (this.selectedRows[0].organizationId !== this.selectedRows[i].organizationId) {
            return this.$message.warning(this.$t('purchaseDemand.openAssignOneTips1'))
          }
        }
      }
      for (const i in this.form) {
        this.form[i] = null
      }
      this.$http({
        url: '/api-sup-ce/pr/requirementManage/findCategorys/Purchase_Strategy',
        method: 'POST',
        data: this.selectedRows,
        loading: true
      }).then(res => {
        this.strategyList = res.data.list
        this.dialogFormVisible = true
      })
      this.$http({
        url: '/api-sup-ce/pr/requirementManage/findCategorys/Carry_Out',
        method: 'POST',
        data: this.selectedRows,
        loading: true
      }).then(res => {
        this.carryOutList = res.data.list
      })
    },
    assignOne () {
      // 分配转办
      const params = Object.assign(
        {
          requirementLineIds: this.selectedRows.map(v => v.requirementLineId)
        },
        this.form
      )
      this.$http({
        url: '/api-sup-ce/pr/requirementManage/bachAssigned',
        method: 'POST',
        data: params,
        loading: true
      })
        .then(data => {
          this.dialogFormVisible = false
          this.$message.success(this.$t('common.success'))
          this.getQuerydata()
        })
        .catch(err => {
          console.log(err)
        })
    },
    readPurchaseApplication (row) {
      this.$router.push({
        name: 'purchaseApplication',
        params: {
          from: 'demandPoolManagement',
          funName: 'purchaseApplication', // 功能
          fdSubject: row
        }
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
    rejectOne () {
      if (this.selectedRows.length === 0) {
        this.$message.warning(this.$t('purchaseDemand.selectData'))
        return
      }
      this.rejectReasonDialog = true
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
