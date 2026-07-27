<template>
  <el-container
    class="flex-container the_demandPoolManagementList_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        ref="formRef"
        :form-array="preArr"
        :pre-form-obj="preFormFilter"
        @getFormData="getQuerydata"
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
          <!-- 创建寻源单据 -->
          <AuthorityButton
            code="pr:demandPoolManagement:createInquiry"
            @click="createInquiry"
          >
            {{ $t('purchaseDemand.createInquiry') }}
          </AuthorityButton>
          <AuthorityButton
            @click="lishi"
          >
            引出历史供应商
          </AuthorityButton>
          <AuthorityButton
            @click="rejectOne"
          >
            关闭
          </AuthorityButton>
          <AuthorityButton
            @click="changeBuyType"
          >
            变更购买类型
          </AuthorityButton>
          <!-- 导出 -->
          <ExportExcel
            type="default"
            :filter-params="filterParams"
            :table-header="tableHeader"
            :dict-codes="dictCodes"
            timeout="1000000"
            export-mode="front"
            page-url="/api-sup-ce/api-ql/PurchaseRequirementLine/query"
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
        :open-custom-table="true"
        :adept-mei-ql="true"
        url="/api-sup-ce/api-ql/PurchaseRequirementLine/query"
        @getFooter="getFooter"
        @getFooterSize="getFooterSize"
        @afterQuery="afterQuery"
      >
        <template #extPoolStatus="{ scope }">
          {{ extPoolStatusFun(scope.row) }}
        </template>
        <template #extUserName="{ scope }">
          {{ scope.row?.extUserCode }}-{{ scope.row?.extUserName }}
        </template>
        <template #extPushUserName="{ scope }">
          {{ scope.row?.extPushUserCode }}-{{ scope.row?.extPushUserName }}
        </template>
        <template #ceeaPerformUserNickname="{ scope }">
          {{ scope.row?.ceeaPerformUserName }}-{{ scope.row?.ceeaPerformUserNickname }}
        </template>
        <template #fileInfos="{ scope }">
          <template>
            <div class="download-link-wrap">
              <SrmCommonFile
                :default-file="{
                  fileId: scope.row?.extAttachId,
                  fileName: scope.row?.extAttachName
                }"
                :readonly="true"
              />
            </div>
          </template>
        </template>
        <template #closeFile="{ scope }">
          <template>
            <div class="download-link-wrap">
              <SrmCommonFile
                :default-file="{
                  fileId: scope.row?.extClosedFileId,
                  fileName: scope.row?.extClosedFileName
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
          <el-row type="flex">
            <el-col>
              <el-form-item
                label="请选择采购员"
                :label-width="formLabelWidth"
              >
                <!--                <el-select-->
                <!--                  v-model="form.ceeaPerformUserId"-->
                <!--                  filterable-->
                <!--                  @change="setUserObj3(form)"-->
                <!--                >-->
                <!--                  <el-option-->
                <!--                    v-for="item in strategyList"-->
                <!--                    :key="item.personInChargeUserId"-->
                <!--                    :label="item.personInChargeNickname"-->
                <!--                    :value="item.personInChargeUserId"-->
                <!--                  />-->
                <!--                </el-select>-->
                <QuickSearch
                  :show-input="form.ceeaPerformUserNickname"
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
          <el-table-column
            align="center"
            width="100"
            prop="extReceiver"
            label="收货人"
            show-overflow-tooltip
          />
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
        title="请填写关闭原因"
        :visible.sync="rejectReasonDialog"
        :modal-append-to-body="false"
        size="small"
        class="inportAbcd"
      >
        <el-main class="reject-reason-container">
          <el-form
            ref="returnRef"
            :model="rejectReason"
            :rules="rejectResonRules"
          >
            <el-form-item
              label="关闭原因"
              label-width="70px"
              prop="extClosedCause"
            >
              <template style="height:200px;">
                <el-input
                  v-model="rejectReason.extClosedCause"
                  type="textarea"
                  :rows="3"
                />
              </template>
            </el-form-item>
            <el-form-item
              label="关闭附件"
              prop="extClosedFileId"
            >
              <SrmCommonFile
                :default-file="{
                  fileId: rejectReason.extClosedFileId,
                  fileName: rejectReason.extClosedFileName
                }"
                @on-change="closeFileChange"
              />
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

      <!-- 变更购买类型 -->
      <srm-dialog
        title="变更购买类型"
        :visible.sync="goumaiChangeVisible"
        :modal-append-to-body="false"
        size="small"
        class="inportAbcd"
      >
        <el-main style="height: 100px">
          <el-form
            ref="returnRef"
            :model="requirementHead"
            :rules="rejectResonRules"
          >
            <el-form-item
              label="购买类型"
              label-width="60px"
              prop="extBuyType"
            >
              <template style="height:200px;">
                <DictSelect
                  v-model="requirementHead.extBuyType"
                  code="PR_BUY_TYPE"
                  :filterItem="['RECENT_PURCHASE']"
                />
              </template>
            </el-form-item>
            <el-form-item
              label="变更原因"
              label-width="60px"
              prop="extBuyTypeComment"
            >
              <template style="height:200px;">
                <el-input
                  v-model="requirementHead.extBuyTypeComment"
                  type="textarea"
                />
              </template>
            </el-form-item>
          </el-form>
        </el-main>
        <div slot="footer">
          <el-button @click="goumaiChangeVisible = false">
            {{ $t('common.cancel') }}
          </el-button>
          <el-button
            type="primary"
            @click="buyTypeFun"
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
import purchaseApplicationDetail2 from '@/modulesCus/buyer/purchasingDemand/views/purchaseApplication/purchaseApplicationDetail'

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
      sourceParams: {},
      preFormFilter: {},
      demandTypeList: [],
      priceSourceList: [],
      rejectReason: {
        extClosedCause: '',
        extClosedFileId: null,
        extClosedFileName: ''
      },
      rejectReasonDialog: false,
      createOrderList: [],
      isCreateOrderVisible: false,
      goumaiChangeVisible: false,
      name: 'materialAssignRuleTable',
      tableName: 'materialAssignRuleTable',
      orderTypeBol: false,
      dictCodes: {
        ceeaPrType: 'application_form_type',
        demandType: 'DEMAND_TYPE',
        applyStatus: 'NPM_PR_ASSIGNE_STATUS',
        unitCode: 'unit',
        ifCreateInq: 'YES_OR_NO',
        extBuyType: 'PR_BUY_TYPE',
        extAreaCode: 'REGION',
        extHistoryVendorFlag: 'YES_OR_NO'
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
        ceeaIfSupplierConfirm: 'Y',
        extBuyType: null,
        extBuyTypeComment: null
      },
      formLabelWidth: '100px',
      preArr: [],
      preFormObj: {},
      form: {
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
        extClosedCause: [{ required: true, message: this.$t('purchaseDemand.fillInReasonForReturn') }], // 请填写退回原因
        extBuyType: [{ required: true, message: '请填写' }],
        extBuyTypeComment: [{ required: true, message: '请填写' }],
        extClosedFileId: [{ required: true, message: '请上传' }]
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
      filterParams: {},
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
          this.preFormFilter = {
            extPoolStatus: '2',
            ifCreateInq: 'N',
            extBuyType: this.$route.params.row.extBuyType
          }
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
        prop: 'extOrgBuName',
        label: '板块',
        width: 130
      },
      {
        prop: 'orgName',
        label: '公司',
        width: 130
      },
      {
        prop: 'createdFullName',
        label: '申请人',
        width: 130
      },
      {
        prop: 'ceeaPrType',
        label: '申请类型',
        width: 130,
        formattor: val => this.$getDictLabel('application_form_type', val)
      },
      {
        prop: 'demandType',
        label: '需求类型',
        width: 130,
        formattor: val => this.$getDictLabel('DEMAND_TYPE', val)
      },
      {
        prop: 'extPushUserName',
        label: '分单人',
        width: 110,
        showType: 'slot',
        slot: 'extPushUserName'
      },
      {
        prop: 'extPushTime',
        label: '分单时间',
        width: 100
      },
      {
        prop: 'extPoolStatusValue',
        label: '需求状态',
        minWidth: 150
      },
      {
        prop: 'applyStatus',
        label: '分配状态', // TODO
        width: 100,
        formattor: val => this.$getDictLabel('NPM_PR_ASSIGNE_STATUS', val)
      },
      {
        prop: 'materialCode',
        label: '物资编码',
        width: 110
      },
      {
        prop: 'materialName',
        label: '物资名称',
        width: 110
      },
      {
        prop: 'categoryName',
        label: '采购品类',
        width: 100
      },
      {
        prop: 'extMaterialModel',
        label: '规格型号',
        width: 100
      },
      {
        prop: 'brand',
        label: '品牌',
        width: 100
      },
      {
        prop: 'extUseDepartmentName',
        label: '使用部门',
        width: 120
      },
      {
        prop: 'extUserName',
        label: '使用人信息',
        width: 150,
        showType: 'slot',
        slot: 'extUserName'
      },
      {
        prop: 'extUserPhone',
        label: '使用人联系方式',
        width: 150
      },
      {
        prop: 'unitCode',
        label: '计量单位',
        width: 90,
        formattor: val => this.$getDictLabel('unit', val)
      },
      // {
      //   prop: 'haveEffectivePrice',
      //   label: '是否存在有效价格',
      //   width: 100,
      //   // formattor: val => ['HAS_PRICE', 'RECENT_PURCHASE'].includes(val) ? '是' : '否'
      //   formattor: val => this.$getDictLabel('YES_OR_NO', val)
      // },
      {
        prop: 'ifCreateInq',
        label: '是否已寻源',
        width: 100,
        formattor: val => this.$getDictLabel('YES_OR_NO', val)
      },
      {
        prop: 'extInqSouNo',
        label: '询价单号',
        width: 100
      },
      {
        prop: 'extBuyType',
        label: '购买类型',
        width: 100,
        formattor: val => this.$getDictLabel('PR_BUY_TYPE', val)
      },
      {
        prop: 'requirementQuantity',
        label: '需求数量',
        width: 80
      },
      {
        prop: 'requirementDate',
        label: '本次需求日期',
        width: 110
      },
      {
        prop: 'orderQuantity',
        label: '可下单数',
        width: 90
      },
      {
        prop: 'receiveAddress',
        label: '收货地址',
        width: 100
      },
      {
        prop: 'extReceiver',
        label: '收货人',
        width: 100
      },
      {
        prop: 'extAreaCode',
        label: '区域',
        width: 100,
        formattor: val => this.$getDictLabel('REGION', val)
      },
      {
        prop: 'ceeaPerformUserNickname',
        label: '采购员',
        width: 150,
        showType: 'slot',
        slot: 'ceeaPerformUserNickname'
      },
      {
        prop: 'extUseTo',
        label: '用途',
        width: 100
      },
      {
        prop: 'extFeeSubject',
        label: '费用科目',
        width: 100
      },
      {
        prop: 'fileInfos',
        label: '附件',
        width: 110,
        showType: 'slot',
        slot: 'fileInfos'
      },
      // 备注
      {
        prop: 'comments',
        label: this.$t('purchaseDemand.comments1'),
        width: 100
      },
      {
        prop: 'ceeaAppointReason',
        label: '采购申请说明',
        width: 100
      },
      {
        prop: 'extHistoryVendorFlag',
        label: '是否引出历史供应商',
        width: 100,
        formattor: val => this.$getDictLabel('YES_OR_NO', val)
      },
      {
        prop: 'extHistoryVendorName1',
        label: '供应商一',
        width: 100
      },
      {
        prop: 'extHistoryVendorPrice1',
        label: '价格',
        width: 100
      },
      {
        prop: 'extHistoryVendorName2',
        label: '供应商二',
        width: 100
      },
      {
        prop: 'extHistoryVendorPrice2',
        label: '价格',
        width: 100
      },
      {
        prop: 'extHistoryVendorName3',
        label: '供应商三',
        width: 100
      },
      {
        prop: 'extHistoryVendorPrice3',
        label: '价格',
        width: 100
      },
      {
        prop: 'extBuyTypeComment',
        label: '变更购买类型原因',
        width: 100
      },
      {
        prop: 'extClosedCause',
        label: '关闭原因',
        width: 100
      },
      {
        prop: 'closeFile',
        label: '关闭附件',
        width: 110,
        showType: 'slot',
        slot: 'closeFile'
      }
    ]
    this.defaultTableHeader = this.tableHeader

    this.preArr = [
      {
        prop: 'materialCode',
        label: '物料编码'
      },
      {
        prop: 'materialName',
        label: '物料名称'
      },
      {
        prop: 'extMaterialModel',
        label: '规格型号'
      },
      {
        prop: 'demandType',
        label: '需求类型',
        type: 'dict',
        code: 'DEMAND_TYPE'
      },
      {
        prop: 'extPushTime',
        label: '分单时间',
        type: 'daterange'
      },
      {
        prop: 'createdFullName',
        label: '申请人'
      },
      // {
      //   prop: 'ceeaDepartmentName',
      //   label: '申请部门'
      // },
      {
        prop: 'extUserName',
        label: '使用人'
      },
      {
        prop: 'extAreaCode',
        label: '区域',
        type: 'dict',
        code: 'REGION'
      },
      {
        prop: 'ifCreateInq',
        label: '是否已寻源',
        type: 'dict',
        code: 'YES_OR_NO'
      },
      {
        prop: 'categoryName',
        label: '采购品类'
      },
      {
        prop: 'requirementHeadNum',
        label: '申请编号'
      },
      {
        prop: 'extBuyType',
        label: '购买类型',
        type: 'dict',
        code: 'PR_BUY_TYPE'
      },
      {
        prop: 'receiveAddress',
        label: '收货地址'
      },
      {
        prop: 'ceeaPerformUserNickname',
        label: '采购员'
      },
      {
        prop: 'extInqSouNo',
        label: '询价单号'
      },
      {
        prop: 'extPoolStatus',
        label: '需求状态',
        type: 'dict',
        code: 'EXT_POOL_STATUS'
      },
      {
        prop: 'applyStatus',
        label: '分配状态',
        type: 'dict',
        code: 'NPM_PR_ASSIGNE_STATUS'
      },
      {
        prop: 'brand',
        label: '品牌'
      }
    ]
    // this.preFormObj = { extBidFlag: 'N', extInPool: 'N' }
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
      this.getQuerydata(this.preFormFilter)
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
    // 关闭附件
    closeFileChange ({ file }) {
      const {
        fileId,
        fileName
      } = file || {}
      this.rejectReason.extClosedFileId = fileId
      this.rejectReason.extClosedFileName = fileName
    },
    changeBuyType () {
      const selectedRows = this.selectedRows
      if (selectedRows.length === 0) {
        this.$message.warning(this.$t('purchaseDemand.selectData'))
        return false
      }
      this.goumaiChangeVisible = true
    },
    lishi () {
      if (this.selectedRows.length === 0) {
        this.$message.warning(this.$t('purchaseDemand.selectData'))
        return
      }
      let requirementLineIds = []
      this.selectedRows.forEach(i => {
        requirementLineIds.push(i.requirementLineId)
      })
      const obj = { requirementLineIds: requirementLineIds }
      const saveData = transformMQL.save('PurchaseRequirementLine', [obj], 'searchHistory')
      this.$http({
        url: '/api-sup-ce/api-ql/PurchaseRequirementLine/searchHistory',
        method: 'POST',
        data: saveData,
        loading: true
      })
        .then(res => {
          this.$message({ type: 'success', message: res.message })
          this.getQuerydata(this.sourceParams)
        })
        .catch(err => {
          console.log(err)
        })
    },
    afterQuery (data) {
      this.$refs[this.gridId].setTableData(async tableData => {
        tableData.forEach((item, index) => {
          const { requirementHeadId, ...res } = item
          res.demandType = requirementHeadId.demandType
          res.extOrgBuName = requirementHeadId.extOrgBuName
          res.orgName = requirementHeadId.orgName
          res.orgId = requirementHeadId.orgId
          res.orgCode = requirementHeadId.orgCode
          res.ceeaAppointReason = requirementHeadId.ceeaAppointReason
          res.requirementHeadId = requirementHeadId.requirementHeadId
          res.ceeaPrType = requirementHeadId.ceeaPrType
          this.$set(tableData, index, res)
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
    getUserObj11 (val, scope) {
      console.log(val, 'val')
      this.form.ceeaPerformUserId = val?.userId
      this.form.ceeaPerformUserName = val?.username
      this.form.ceeaPerformUserNickname = val?.nickname
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
      this.sourceParams = obj
      const { dateList, extPoolStatus, ...rest } = obj || this.queryParam
      const params = {}
      if (dateList) {
        params.applyDate = [dateList[0], dateList[1]]
      }
      let filterStr = 'eq'
      if (extPoolStatus) {
        if (extPoolStatus == 1) {
          params.extPoolStatus = 'Y'
          params.orderQuantity = 0
        } else if (extPoolStatus == 2) {
          params.extPoolStatus = 'Y'
          filterStr = 'gt'
          params.orderQuantity = 0
        } else if (extPoolStatus == 3) {
          params.extPoolStatus = 'N'
        }
      }
      console.log('params', params)
      let filter = {
        'extBidFlag': {
          'eq': 'N'
        },
        'extInPool': {
          'eq': 'Y'
        }
      }
      if (rest.demandType) {
        filter.demandType = {
          contains: rest.demandType
        }
      }
      this.queryParam = transformMQL.listPageData({
        type: 'PurchaseRequirementLine',
        action: 'query',
        params: { ...rest, ...params },
        sort: 'extPushTime',
        query: {
          '*': {},
          'requirementHeadId': {
            'extOrgBuName': {},
            'requirementHeadId': {},
            'orgName': {},
            'orgId': {},
            'orgCode': {},
            'demandType': {},
            'ceeaAppointReason': {},
            'ceeaPrType': {},
            '$condition': {
              '$strictQuery': true,
              'filter': filter
            }
          }
        },
        filterOperator: {
          applyDate: 'between',
          orderQuantity: filterStr,
          extPushTime: 'between'
        }
      })
      this.filterParams = { meiqlPayload: this.queryParam }
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
        this.strategyList.find(v => v.personInChargeUserId === this.form.ceeaPerformUserId) || {}
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
      let bol1 = false
      let bol2 = false
      let bol3 = false
      let bol4 = false
      this.selectedRows.forEach(datas => {
        if (datas.applyStatus == 'UNASSIGNED') {
          bol1 = true
        }
        if (datas.extBuyType == 'HAS_PRICE') {
          bol2 = true
        }
        if (datas.extHistoryVendorFlag !== 'Y') {
          bol3 = true
        }
        // 需求状态有效才可以创建寻源
        if (datas.extPoolStatus !== 'Y' || (datas.extPoolStatus === 'Y' && datas.orderQuantity === 0)) {
          bol4 = true
        }
      })
      if (bol4) {
        this.$message.warning('请选择需求状态为有效的单据')
        return false
      }
      if (bol1) {
        this.$message.warning('请选择已分配单据')
        return false
      }
      if (bol2) {
        this.$message.warning('所勾选的数据存在购买类型为协议类的，不允许创建询比价')
        return false
      }
      if (bol3) {
        this.$message.warning('请引出历史供应商')
        return false
      }
      const obj = {
        souType: 'inq',
        requirementLineIds: this.selectedRows.map(v => v.requirementLineId)
      }
      const searchData = transformMQL.save('PurchaseRequirementLine', obj, 'createSou')
      console.log(searchData, 'searchData')
      this.$http({
        url: '/api-sup-ce/api-ql/PurchaseRequirementLine/createSou',
        method: 'POST',
        data: searchData,
        loading: true
      })
        .then(data => {
          console.log(data, 'data')
          const dataObj = data.data?.records[0]
          if (dataObj.projectId) {
            // TODO
            this.$router.push({
              name: 'inquiryManagement',
              params: {
                funName: 'inquiryManagement',
                from: 'demandPoolManagement', // 来源路由name
                formId: dataObj.projectId,
                formNo: dataObj.souNo
              }
            })
          }
        })
        .catch(err => {
          console.log(err)
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
    buyTypeFun () {
      const extBuyType = this.requirementHead.extBuyType
      const extBuyTypeComment = this.requirementHead.extBuyTypeComment
      const selectedRows = this.selectedRows
      if (!extBuyTypeComment) {
        this.$message.error('请填写原因')
        return false
      }
      let attr = []
      selectedRows?.forEach(e => {
        attr.push(e?.requirementLineId)
      })
      const obj = {
        requirementLineIds: attr,
        extBuyType,
        extBuyTypeComment
      }
      const saveData = transformMQL.save('PurchaseRequirementLine', [obj], 'changeBuyType')
      this.$http({
        url: '/api-sup-ce/api-ql/PurchaseRequirementLine/changeBuyType',
        method: 'POST',
        data: saveData,
        loading: true
      })
        .then(data => {
          this.goumaiChangeVisible = false
          this.getQuerydata(this.sourceParams)
        })
        .catch(err => {
          console.log(err)
        })
    },
    extPoolStatusFun (row) {
      let status = null
      const extPoolStatus = row?.extPoolStatus
      const orderQuantity = row?.orderQuantity
      if (extPoolStatus == 'Y') {
        if (orderQuantity == 0) {
          status = '已完成'
        } else {
          status = '有效'
        }
      } else if (extPoolStatus == 'N') {
        status = '已关闭'
      }
      return status
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
          // if (this.selectedRows[0].categoryId !== this.selectedRows[i].categoryId) {
          //   return this.$message.warning(this.$t('勾选单据的品类必须一致'))
          // }
        }
      }
      for (const i in this.form) {
        this.form[i] = null
      }
      // dialogFormVisible1
      this.dialogFormVisible1 = true
      // const saveData = {
      //   categoryId: this.selectedRows[0].categoryId,
      //   duty: "purchaser",
      //   enable: "Y",
      //   orgIds: [this.selectedRows[0].orgId]
      // }
      // this.$http({
      //   url: '/api-sup-ce/division/divisionCategory/listPageByParam',
      //   method: 'POST',
      //   data: saveData,
      //   loading: true
      // }).then(res => {
      //   console.log(res, 'res')
      //   this.strategyList = res.data.list
      //   this.dialogFormVisible1 = true
      // })
    },
    assignOne () {
      // 分配转办
      const params = Object.assign(
        {
          requirementLineIds: this.selectedRows.map(v => v.requirementLineId)
        },
        this.form
      )
      const saveData = transformMQL.save('PurchaseRequirementLine', [params], 'assign')
      this.$http({
        url: '/api-sup-ce/api-ql/PurchaseRequirementLine/assign',
        method: 'POST',
        data: saveData,
        loading: true
      })
        .then(data => {
          this.dialogFormVisible1 = false
          this.$message.success(this.$t('common.success'))
          this.getQuerydata(this.sourceParams)
        })
        .catch(err => {
          console.log(err)
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
      const obj = {
        requirementLineIds: this.selectedRows.map(v => v.requirementLineId),
        ...this.rejectReason
      }
      if (!this.rejectReason.extClosedCause || this.rejectReason.extClosedCause == '') {
        this.$message.error('请填写关闭原因')
        return false
      }
      if (!this.rejectReason.extClosedFileId) {
        this.$message.error('请上传关闭附件')
        return false
      }
      const saveData = transformMQL.save('PurchaseRequirementLine', [obj], 'close')
      this.$http({
        url: '/api-sup-ce/api-ql/PurchaseRequirementLine/close',
        method: 'POST',
        data: saveData,
        loading: true
      })
        .then(data => {
          this.$message.success(this.$t('common.success'))
          this.rejectReasonDialog = false
          this.getQuerydata(this.sourceParams)
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
      this.rejectReason.extClosedCause = null
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
.reject-reason-container {
  height: 150px !important;
}
</style>
