<template>
  <el-container class="flex-container wrapper" direction="vertical">
    <el-main>
      <el-collapse v-model="colValue">
        <el-collapse-item :title="$t('supRisk.baseInfo')" name="1">
          <el-form
            ref="form"
            :model="form"
            :rules="rules"
            :disabled="disabledFlag"
          >
            <srm-row>
              <srm-col>
                <el-form-item
                  :label="$t('common.vendorCode')"
                  prop="vendorCode"
                >
                  <el-input
                    v-model="form.vendorCode"
                    disabled
                  />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item
                  :label="$t('common.vendorName')"
                  prop="vendorName"
                >
                  <el-input
                    v-model="form.vendorName"
                    disabled
                  />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item :label="$t('bidMod.businessEntity')" prop="buId">
                  <OrganizationSelector
                    ref="orgSelector"
                    v-model="form.buId"
                    :placeholder="$t('common.pleaseSelect')"
                    :disabled="disabledControl"
                    :parent-id="-1"
                    node-type="OU"
                    @select="ouSelectHandler"
                  />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item :label="$t('dataConfMod.settingGuide.step3.2')" prop="currencyCode">
                  <dict-select v-model="form.currencyCode" :disabled="disabledControl" code="currency" @change-value="changeCurrency" />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item :label="$t('dataConfMod.settingGuide.step3.4')" prop="taxKey">
                  <dict-select v-model="form.taxKey" :disabled="disabledControl" code="tax" @change-value="changeTax" />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item :label="$t('other.key25')" prop="taxAmount">
                  <el-input v-model="form.taxAmount" disabled />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item :label="$t('other.key24')" prop="notTaxAmount">
                  <el-input v-model="form.notTaxAmount" disabled />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item
                  :label="$t('components.stratProcess.headers.docStatusValue')"
                  prop="status"
                >
                  <dict-select
                    v-model="form.status"
                    disabled
                    code="CONTRACT_INVOICE_STATUS"
                  />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item
                  :label="$t('common.creator')"
                  prop="createdFullName"
                >
                  <el-input
                    v-model="form.createdFullName"
                    disabled
                  />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item
                  :label="$t('common.creationTime')"
                  prop="creationDate"
                >
                  <el-date-picker
                    v-model="form.creationDate"
                    :format="$formatDatePickerTime"
                    disabled
                  />
                </el-form-item>
              </srm-col>
              <srm-col v-if="form.status === 'REJECTED'" :init-col="1">
                <el-form-item :label="$t('purSettlementMod.rejectReason')">
                  <el-input
                    v-model="form.approvalOpinion"
                    type="textarea"
                    :rows="3"
                    disabled
                  />
                </el-form-item>
              </srm-col>
            </srm-row>
          </el-form>
        </el-collapse-item>
        <el-collapse-item ref="invocie" :title="$t('purSettlementMod.invoiceDetail')" name="2">
          <el-button type="primary" :disabled="disabledFlag" @click="addInvoice">
            {{ $t('bidMod.affairsIncreased') }}
          </el-button>
          <el-table border stripe class="mt-10" :data="perInvoiceDetailList">
            <el-table-column min-width="180" show-overflow-tooltip>
              <template slot="header">
                <i class="toRequired">*</i>
                {{ $t('contractMod.contractNo_1') }}
              </template>
              <template slot-scope="scope">
                <el-input v-model="scope.row.contractNo" disabled>
                  <el-button slot="append" icon="el-icon-search" :disabled="disabledFlag" @click="contractRowClick(scope)" />
                </el-input>
              </template>
            </el-table-column>
            <el-table-column min-width="180" show-overflow-tooltip>
              <template slot="header">
                <i class="toRequired">*</i>
                {{ $t('other.key23') }}
                <!-- 一个开票单只能选择一种里程碑 -->
                <el-tooltip class="item" effect="dark" :content="$t('cusEntry.supplement20250211.openInvoiceMilestoneChoice')" placement="top">
                  <i class="el-icon-info" />
                </el-tooltip>
              </template>
              <template slot-scope="scope">
                <el-input :value="$getDictLabel('MILESTONE_SCHEDULE', scope.row.milestoneType)" disabled>
                  <el-button slot="append" icon="el-icon-search" :disabled="disabledFlag" @click="contractRowClick2(scope)" />
                </el-input>
              </template>
            </el-table-column>
            <el-table-column prop="materialCode" :label="$t('mould.itemNumber')" min-width="130" show-overflow-tooltip />
            <el-table-column prop="materialName" :label="$t('purSettlementMod.materialId')" min-width="150" show-overflow-tooltip />
            <el-table-column prop="contractQuantity" :label="$t('bid_mod.quantity')" min-width="100" show-overflow-tooltip />
            <el-table-column prop="invName" :label="$t('purchaseDemand.invOrg')" min-width="130" show-overflow-tooltip />
            <el-table-column prop="amount" :label="$t('other.key22')" min-width="130" show-overflow-tooltip />
            <el-table-column prop="unAmount" :label="$t('other.key21')" min-width="130" show-overflow-tooltip />
            <el-table-column prop="invoicedTaxedAmount" :label="$t('other.key20')" min-width="130" show-overflow-tooltip />
            <el-table-column prop="invoicedUntaxedAmount" :label="$t('other.key19')" min-width="130" show-overflow-tooltip />
            <el-table-column prop="occupyInvoicedTaxedAmount" :label="$t('other.key18')" min-width="130" show-overflow-tooltip />
            <el-table-column prop="occupyInvoicedUntaxedAmount" :label="$t('other.key17')" min-width="130" show-overflow-tooltip />
            <el-table-column prop="currentUntaxedAmount" min-width="130" show-overflow-tooltip>
              <template slot="header">
                <i class="toRequired">*</i>
                {{ $t('other.key16') }}
              </template>
              <template slot-scope="scope">
                <el-input v-model="scope.row.currentUntaxedAmount" v-input-format="inputFormat" :disabled="disabledFlag" @change="value => amoutChange(value,scope.row)" />
              </template>
            </el-table-column>
            <el-table-column prop="taxRate" :label="$t('purSettlementMod.taxRate2')" min-width="120" show-overflow-tooltip />
            <el-table-column prop="currentTaxedAmount" :label="$t('other.key15')" min-width="130" show-overflow-tooltip />
            <el-table-column :label="$t('formula.handle')" min-width="100" show-overflow-tooltip fixed="right">
              <template slot-scope="scope">
                <el-button type="text" :disabled="disabledFlag" @click="deleteInvoice(scope)">
                  {{ $t('components.common.delete') }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-collapse-item>
        <el-collapse-item :title="$t('accountMod.invoiceInfo')" name="3">
          <el-button type="primary" :disabled="disabledFlag" @click="addInvoiceInfo">
            {{ $t('bidMod.affairsIncreased') }}
          </el-button>
          <el-table
            class="mt-10"
            :data="perInvoiceInformationList"
            style="width: 100%"
            border
            max-height="251px"
            @cell-mouse-enter="mouseEnterInvoice"
            @cell-mouse-leave="mouseLeaveInvoice"
          >
            <!-- 序号 -->
            <el-table-column
              align="center"
              type="index"
              width="60"
              fixed="left"
              :label="$t('common.sort')"
            />
            <!-- 发票影像 -->
            <el-table-column
              align="center"
              prop="fileSourceName"
              width="80"
              :label="$t('purSettlementMod.fileSourceName2')"
              show-overflow-tooltip
            >
              <template slot-scope="scope">
                <el-button type="text" @click="isFileSource = true">
                  {{ scope.row.fileSourceName }}
                </el-button>
                <filePreview
                  vWidth="60%"
                  vHeight="400"
                  :visible="isFileSource"
                  :fileupload-id="scope.row.fileuploadId"
                  :file-name="scope.row.fileSourceName.split('.')[0]"
                  @cancel="isFileSource = false"
                />
              </template>
            </el-table-column>
            <!-- 增值税发票类型 -->
            <el-table-column
              align="center"
              prop="invoiceType"
              width="120"
              :label="$t('purSettlementMod.invoiceType')"
              show-overflow-tooltip
            />
            <!-- 采购方税号 -->
            <el-table-column
              align="center"
              prop="purchaserRegisterNum"
              width="120"
              :label="$t('purSettlementMod.purchaserRegisterNum')"
              show-overflow-tooltip
            >
              <template slot-scope="scope">
                <el-input
                  v-if="scope.row.editabled"
                  v-model="scope.row.purchaserRegisterNum"
                  :disabled="disabledFlag"
                  @input="onExchange(scope.$index)"
                />
                <span v-show="!scope.row.editabled">{{ scope.row.purchaserRegisterNum }}</span>
              </template>
            </el-table-column>
            <!-- 发票代码 -->
            <el-table-column
              align="center"
              prop="invoiceCode"
              width="120"
              :label="$t('purSettlementMod.invoiceCode')"
              show-overflow-tooltip
            >
              <template slot-scope="scope">
                <el-input
                  v-if="scope.row.editabled"
                  v-model="scope.row.invoiceCode"
                  :disabled="disabledFlag"
                  @input="onExchange(scope.$index)"
                />
                <span v-show="!scope.row.editabled">{{ scope.row.invoiceCode }}</span>
              </template>
            </el-table-column>
            <!-- 发票号码 -->
            <el-table-column
              align="center"
              prop="invoiceNum"
              width="120"
              :label="$t('purSettlementMod.invoiceNum')"
              show-overflow-tooltip
            >
              <template slot-scope="scope">
                <el-input
                  v-if="scope.row.editabled"
                  v-model="scope.row.invoiceNum"
                  :disabled="disabledFlag"
                  @input="onExchange(scope.$index)"
                />
                <span v-show="!scope.row.editabled">{{ scope.row.invoiceNum }}</span>
              </template>
            </el-table-column>
            <!-- 开票日期 -->
            <el-table-column
              align="center"
              prop="invoiceDate"
              width="120"
              :label="$t('purSettlementMod.invoiceDate2')"
              show-overflow-tooltip
            >
              <template slot-scope="scope">
                <el-date-picker
                  v-if="scope.row.editabled"
                  v-model="scope.row.invoiceDate"
                  :disabled="disabledFlag"
                  type="date"
                  :format="$formatDatePicker"
                  value-format="yyyy-MM-dd"
                  @input="onExchange(scope.$index)"
                  @blur="setNotEditabled(scope.row)"
                />
                <span v-show="!scope.row.editabled">{{ $parseTime(scope.row.invoiceDate) }}</span>
              </template>
            </el-table-column>
            <!-- 校验码 -->
            <el-table-column
              align="center"
              prop="checkCode"
              width="120"
              :label="$t('purSettlementMod.checkCode')"
              show-overflow-tooltip
            >
              <template slot-scope="scope">
                <el-input
                  v-if="scope.row.editabled"
                  v-model="scope.row.checkCode"
                  :disabled="disabledFlag"
                  @input="onExchange(scope.$index)"
                />
                <span v-show="!scope.row.editabled">{{ scope.row.checkCode }}</span>
              </template>
            </el-table-column>
            <!-- 采购方 -->
            <el-table-column
              align="center"
              prop="purchaserName"
              width="120"
              :label="$t('purSettlementMod.purchaserName')"
              show-overflow-tooltip
            >
              <template slot-scope="scope">
                <el-input
                  v-if="scope.row.editabled"
                  v-model="scope.row.purchaserName"
                  :disabled="disabledFlag"
                  @input="onExchange(scope.$index)"
                />
                <span v-show="!scope.row.editabled">{{ scope.row.purchaserName }}</span>
              </template>
            </el-table-column>
            <!-- 供方 -->
            <el-table-column
              align="center"
              prop="sellerName"
              width="120"
              :label="$t('purSettlementMod.sellerName2')"
              show-overflow-tooltip
            >
              <template slot-scope="scope">
                <el-input
                  v-if="scope.row.editabled"
                  v-model="scope.row.sellerName"
                  :disabled="disabledFlag"
                  @input="onExchange(scope.$index)"
                />
                <span v-show="!scope.row.editabled">{{ scope.row.sellerName }}</span>
              </template>
            </el-table-column>
            <!-- 未税金额 -->
            <el-table-column
              align="center"
              prop="noTaxTotalAmount"
              width="120"
              :label="$t('purSettlementMod.noTaxTotalAmount2')"
              show-overflow-tooltip
            >
              <template slot-scope="scope">
                <el-input-number
                  v-if="scope.row.editabled"
                  v-model="scope.row.noTaxTotalAmount"
                  :disabled="disabledFlag"
                  :controls="false"
                  class="input-number-precision"
                  @input="onExchange(scope.$index, true, scope.row)"
                />
                <span v-show="!scope.row.editabled">{{ scope.row.noTaxTotalAmount }}</span>
              </template>
            </el-table-column>
            <!-- 税额 -->
            <el-table-column
              align="center"
              prop="totalTax"
              width="120"
              :label="$t('purSettlementMod.totalTax')"
              show-overflow-tooltip
            >
              <template slot-scope="scope">
                <el-input-number
                  v-if="scope.row.editabled"
                  v-model="scope.row.totalTax"
                  :disabled="disabledFlag"
                  :controls="false"
                  class="input-number-precision"
                  @input="onExchange(scope.$index, true, scope.row)"
                />
                <span v-show="!scope.row.editabled">{{ scope.row.totalTax }}</span>
              </template>
            </el-table-column>
            <!-- 含税金额 -->
            <el-table-column
              align="center"
              prop="totalAmount"
              width="120"
              :label="$t('purSettlementMod.totalAmount')"
              show-overflow-tooltip
            />
            <!-- 发票快递单号 -->
            <el-table-column
              align="center"
              prop="invoiceCourierNo"
              width="120"
              :label="$t('purSettlementMod.invoiceCourierNo')"
              show-overflow-tooltip
            >
              <template slot-scope="scope">
                <el-input
                  v-if="scope.row.editabled"
                  v-model="scope.row.invoiceCourierNo"
                  :disabled="disabledFlag"
                  @input="onExchange(scope.$index)"
                />
                <span v-show="!scope.row.editabled">{{ scope.row.invoiceCourierNo }}</span>
              </template>
            </el-table-column>
            <el-table-column :label="$t('common.operation')" width="100" fixed="right">
              <template slot-scope="scope">
                <el-button type="text" :disabled="disabledFlag" @click="deleteInvoiceInfo(scope.$index, scope.row)">
                  {{ $t('common.delete') }}
                </el-button>
                <el-button type="text" @click="downloadInvoice(scope.row)">
                  {{ $t('common.download') }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-collapse-item>
        <el-collapse-item :title="$t('accountMod.relevantAttachment')" name="4">
          <FileDynamic
            ref="sceneAttachment"
            v-model="fileUploads"
            scene-module-code="SCENE_PERFORM_INVOICE_ATTACHMENT"
            :business-id="form.perInvoiceId"
            :editable="!disabledFlag"
            :need-init="false"
          />
        </el-collapse-item>
      </el-collapse>
      <CToolbar>
        <template slot="right">
          <el-button @click="back">
            {{ $t('bidMod.cancel') }}
          </el-button>
          <el-button v-if="!disabledFlag" type="primary" @click="saveBill('SAVE')">
            {{ $t('bidMod.temporaryStorage') }}
          </el-button>
          <el-button v-if="!disabledFlag" type="primary" @click="saveBill('SUBMIT')">
            {{ $t('problemManagement.submit') }}
          </el-button>
        </template>
      </CToolbar>
    </el-main>
    <!-- 新增发票 - 弹窗 -->
    <srm-dialog
      :title="$t('purSettlementMod.addInvoice')"
      size="large"
      :visible.sync="addInvoieDialog"
      :close-on-click-modal="false"
      :destroy-on-close="true"
    >
      <template slot="header">
        <div style="font-size: 18px;">
          <span>{{ $t('purSettlementMod.addInvoice') }}</span>
          <span class="voice-title">{{ $t('purSettlementMod.voiceTitle') }}</span>
        </div>
      </template>
      <upload-table
        :headerText="$t('purSettlementMod.uploadInvoice')"
        :url="uploadUrl"
        :extraData="extraData"
        :accept="['jpg', 'png', 'jpeg']"
        :tableData="invoiceInforData"
        @selectChange="handleInvoieSelection"
      />
      <span slot="footer" class="dialog-footer">
        <el-button @click="addInvoieDialog = false">{{
          $t('vendorMod.relegation.abolish')
        }}</el-button>
        <el-button type="primary" @click="invoiceUplInfo">{{ $t('common.save') }}</el-button>
      </span>
    </srm-dialog>
    <!-- 驳回弹窗 -->
    <srm-dialog
      size="middle"
      :visible.sync="rejectShow"
    >
      <template slot="header">
        <i class="toRequired">*</i>
        {{ $t('purSettlementMod.rejectReason') }}
      </template>
      <el-input
        v-model="rejectReason"
        type="textarea"
        :rows="3"
        show-word-limit
        :maxlength="300"
      />
      <div slot="footer">
        <el-button @click="rejectShow = false">
          {{ $t('bidMod.cancel') }}
        </el-button>
        <el-button type="primary" @click="handleReject">
          {{ $t('bidMod.launchQuotation_sure') }}
        </el-button>
      </div>
    </srm-dialog>
    <!-- 合同选择弹窗 -->
    <srm-dialog
      size="large"
      :title="$t('contractMod.selContract')"
      :visible.sync="contractShow"
      :close-on-click-modal="false"
      :destroy-on-close="true"
    >
      <el-form ref="contractForm" :model="contractForm" :inline="true" class="demo-form-inline">
        <el-form-item :label="$t('contractMod.contractNo_1')" prop="contractNo">
          <el-input v-model="contractForm.contractNo" />
        </el-form-item>
        <el-form-item :label="$t('bidMod.contractName')" prop="contractName">
          <el-input v-model="contractForm.contractName" />
        </el-form-item>
        <el-form-item :label="$t('dataConfMod.createdBy')" prop="createdFullName">
          <el-input v-model="contractForm.createdFullName" />
        </el-form-item>
        <span style="float:right;">
          <el-button type="primary" @click="getContractList">
            {{ $t('bidMod.inquire') }}
          </el-button>
          <el-button @click="resetContract">
            {{ $t('common.reset') }}
          </el-button>
        </span>
        <br>
        <el-form-item :label="$t('purSettlementMod.materialId')" prop="materialName">
          <el-input v-model="contractForm.materialName" />
        </el-form-item>
      </el-form>
      <el-table
        class="mt-10"
        :data="contractData"
        border
        stripe
        max-height="300"
        @row-click="rowClick"
      >
        <el-table-column prop="contractNo" :label="$t('contractMod.contractNo_1')" show-overflow-tooltip />
        <el-table-column prop="contractName" :label="$t('bidMod.contractName')" show-overflow-tooltip />
        <el-table-column prop="materialCode" :label="$t('mould.itemNumber')" show-overflow-tooltip />
        <el-table-column prop="materialName" :label="$t('purSettlementMod.materialId')" show-overflow-tooltip />
        <el-table-column prop="surplusUntaxedAmount" :label="$t('other.key14')" show-overflow-tooltip />
        <el-table-column prop="surplusTaxedAmount" :label="$t('other.key27')" show-overflow-tooltip />
        <el-table-column prop="createdFullName" :label="$t('dataConfMod.createdBy')" show-overflow-tooltip />
        <el-table-column prop="creationDate" :label="$t('common.creationTime')" :formatter="(row, column, cellValue) => $parseTime(cellValue)" show-overflow-tooltip />
      </el-table>
      <CPagination
        :pageNum="contractPageNum"
        :pageSize="contractPageSize"
        :total="contractTotal"
        @current-change="contractPageChange"
        @size-change="contractSizeChange"
      />
    </srm-dialog>
    <!-- 里程碑选择弹窗 -->
    <srm-dialog
      size="large"
      :title="$t('other.key26')"
      :visible.sync="contractShow2"
      :close-on-click-modal="false"
      :destroy-on-close="true"
    >
      <el-table
        class="mt-10"
        :data="contractData2"
        border
        stripe
        max-height="300"
        @row-click="rowClick2"
      >
        <el-table-column prop="milestoneType" :label="$t('contract_mod.processNodeName')" show-overflow-tooltip>
          <template slot-scope="scope">
            {{ $getDictLabel('MILESTONE_SCHEDULE', scope.row.milestoneType) }}
          </template>
        </el-table-column>
        <el-table-column prop="paymentStage" :label="$t('bidMod.category_stage')" show-overflow-tooltip>
          <template slot-scope="scope">
            {{ $getDictLabel('PAYMENT_STAGE', scope.row.paymentStage) }}
          </template>
        </el-table-column>
        <el-table-column prop="payExplain" :label="$t('route.contractPaymentType')" show-overflow-tooltip>
          <template slot-scope="scope">
            <dict-select
              :value="num(scope.row.payExplain)"
              code="payExplain"
              custom-select-type="payExplain"
              :disabled="true"
            />
          </template>
        </el-table-column>
        <el-table-column prop="payMethod" :label="$t('bidMod.category_paymentMethod')" show-overflow-tooltip>
          <template slot-scope="scope">
            {{ $getDictLabel('PAYMENT_MODE', scope.row.payMethod) }}
          </template>
        </el-table-column>
        <el-table-column prop="paymentRatio" :label="$t('bidMod.paymentRatio')" show-overflow-tooltip />
        <el-table-column prop="stagePaymentAmount" :label="$t('contractMod.stagePaymentAmount')" show-overflow-tooltip />
      </el-table>
      <CPagination
        :pageNum="contractPageNum"
        :pageSize="contractPageSize"
        :total="contractTotal"
        @current-change="contractPageChange"
        @size-change="contractSizeChange"
      />
    </srm-dialog>
  </el-container>
</template>
<script>
import { tabTodoMixin } from '@/utils/mixins'
import QuickSearch from 'lib@/components/QuickSearch'
import OrganizationSelector from 'lib@/components/organization-selector'
import validate from 'lib@/mixins/validate'
import { createDictClass } from '@/library/utils/dict/dict-utils'
import filePreview from 'lib@/components/filePreview'
import { downloadWithParam } from 'lib@/utils/file'
import uploadTable from '@/library/composition/purSettlement/uploadTableShow.vue'
import CToolbar from 'lib@/components/c-toolbar'
import FileDynamic from '@/library/components/c-file-management/file-dynamic'
import CPagination from 'lib@/components/c-pagination'
import { sysPrefix } from '@/config/ipConfig'

export default {
  name: 'ContractPerformanceInvoiceDetail',
  components: {
    QuickSearch,
    OrganizationSelector,
    filePreview,
    uploadTable,
    CToolbar,
    FileDynamic,
    CPagination
  },
  mixins: [tabTodoMixin, validate],
  data () {
    return {
      uploadUrl: `${sysPrefix()}/api-sup-ce/ps/invoice/onlineInvoice/uploadAndVatInvoice`,
      contractShow2: false,
      dictClass: createDictClass({
        MILESTONE_SCHEDULE: [], // 里程碑名称
        CONTRACT_INVOICE_STATUS: [] // 履约状态
      }),
      colValue: ['1', '2', '3', '4'],
      curRole: this.$store.getters.userType,
      userInfo: this.$store.getters.user,
      curAction: '', // 判断审批流页签是否可选 approval no-approval
      inputFormat: { type: 'float', digits: 2, negative: false, zero: false },
      extraData: {
        sourceType: 'WEB_APP',
        uploadType: 'DEF',
        fileModular: 'sup-ce',
        fileFunction: 'onlineInvoice',
        fileType: 'images'
      },
      form: {
        vendorCode: null,
        vendorName: null,
        vendorId: null,
        buId: null,
        buCode: null,
        buName: null,
        currencyId: null,
        currencyCode: null,
        currencyName: null,
        taxId: null,
        taxKey: null,
        taxRate: null,
        taxName: null,
        taxAmount: null,
        notTaxAmount: null,
        status: 'DRAFT',
        createdFullName: null,
        creationDate: null,
        approvalOpinion: null,
        perInvoiceId: null
      },
      rules: {
        buId: [{ required: true, message: this.$t('common.BusinessEntity') }],
        currencyCode: [{ required: true, message: this.$t('vendorMod.msgCurrencyCode') }],
        taxKey: [{ required: true, message: this.$t('bidMod.selectTaxRate') }]
      },
      perInvoiceDetailList: [], // 开票明细
      perInvoiceInformationList: [], // 发票信息
      fileUploads: [], // 附件
      mode: '',
      perInvoiceId: null,
      isFileSource: false,
      addInvoieDialog: false,
      invoiceInforData: [],
      rejectShow: false,
      rejectReason: '',
      contractShow: false,
      contractForm: {
        contractNo: '',
        contractName: '',
        createdFullName: '',
        materialName: ''
      },
      contractData: [],
      contractData2: [],
      contractPageNum: 1,
      contractPageSize: 15,
      contractTotal: 0,
      contractScope: null
    }
  },
  computed: {
    urlParams () {
      return this.$attrs.params || {}
    },
    disabledFlag () {
      return !!['view', 'manage'].includes(this.urlParams.flag)
    },
    MILESTONE_SCHEDULE () {
      return this.dictClass.getDict('MILESTONE_SCHEDULE')
    },
    disabledControl () {
      return this.perInvoiceDetailList.filter(item => item.contractNo).length
    }
  },
  watch: {
    perInvoiceDetailList: {
      handler (nVal) {
        let total = 0; let unTotal = 0
        if (nVal) {
          for (let item of nVal) {
            total += (item.currentTaxedAmount || 0)
            unTotal += (item.currentUntaxedAmount || 0)
          }
          this.form.taxAmount = total
          this.form.notTaxAmount = unTotal
        }
      },
      deep: true
    }
  },
  created () {
    console.log('MILESTONE_SCHEDULE', this.dictClass)
    let { companyId, companyCode, companyName } = this.userInfo.userInfo || {}
    let { flag, row } = this.urlParams
    if (row.perInvoiceId) {
      this.perInvoiceId = row.perInvoiceId
      this.getFormDetail(row.perInvoiceId)
    }
    if (flag === 'add') {
      this.form.vendorId = companyId
      this.form.vendorCode = companyCode
      this.form.vendorName = companyName
      this.$nextTick(() => {
        this.$refs.sceneAttachment.loadFileInfo()
      })
    }
  },
  methods: {
    num (nums) {
      return parseInt(nums) ? parseInt(nums) : ''
    },
    ouSelectHandler (node, value, scope) {
      this.form.buId = node ? node.organizationId : null
      this.form.buCode = node ? node.organizationCode : null
      this.form.buName = node ? node.organizationName : null
    },
    changeCurrency (value, dictItem) {
      console.log('dictItem', dictItem)
      this.form.currencyId = dictItem ? dictItem.id : null
      this.form.currencyCode = dictItem ? dictItem.value : null
      this.form.currencyName = dictItem ? dictItem.currencyName : null
    },
    changeTax (value, dictItem) {
      console.log('taxItem', dictItem)
      this.form.taxKey = dictItem ? dictItem.value : null // 税率key
      this.form.taxId = dictItem ? dictItem.id : null
      this.form.taxRate = dictItem ? dictItem.key : null // 税率值
      this.form.taxName = dictItem ? dictItem.label : null
    },
    // 新增发票信息
    addInvoiceInfo () {
      this.addInvoieDialog = true
    },
    // 发票信息 - 移入行可编辑
    mouseEnterInvoice (row, column) {
      this.$set(row, 'editabled', true)
    },
    mouseLeaveInvoice (row, column) {
      this.$set(row, 'editabled', false)
      // 开票日期需单独处理（日期组件点击触发elementUI组件挂载dom最外层自动认为鼠标移出）
      if (column.property === 'invoiceDate') {
        this.$set(row, 'editabled', true)
      }
    },
    // 日期组件失焦事件
    setNotEditabled (row) {
      this.$set(row, 'editabled', false)
    },
    onExchange (index, sign, row) {
      let moment = this.perInvoiceInformationList[index]
      this.$set(this.perInvoiceInformationList, index, moment)
      if (sign) {
        // 发票含税金额 = 未税金额 + 税额
        row.totalAmount = row.noTaxTotalAmount + row.totalTax
      }
    },
    // 删除开票信息
    deleteInvoiceInfo (index) {
      this.perInvoiceInformationList.splice(index, 1)
    },
    // 发票下载
    downloadInvoice (row) {
      if (row.fileuploadId) {
        downloadWithParam(
          row.fileuploadId,
          row.fileSourceName,
        ).catch(() => {
          this.$message.error(this.$t('components.eio.downloadFail')) // 下载失败
        })
      } else {
        throw new Error('AttachId is null.')
      }
    },
    // 新增发票 - 保存
    invoiceUplInfo () {
      for (let i = 0; i < this.invoiceInforData.length; i++) {
        if (this.invoiceInforData[i].isFinesh === 'no') {
          return this.$message.warning(this.$t('purSettlementMod.waitForUploadToSucceed'))
        }
      }
      const uids = this.perInvoiceInformationList.map(item => item.uid)
      this.invoiceInforData.forEach(item => {
        if (item.status === 'success' && !uids.includes(item.uid)) {
          this.perInvoiceInformationList.push(item)
        }
      })
      // 上传完清空表格
      this.invoiceInforData = []
      this.addInvoieDialog = false
    },
    // 新增发票 - 选择
    handleInvoieSelection (select) {
      this.invoiceSelects = select
    },
    addInvoice () {
      this.perInvoiceDetailList.push({
        contractNo: null,
        milestoneType: null,
        performTemplLineId: null,
        perPlanMilestoneId: null,
        milestoneTypeList: [],
        materialCode: null,
        materialName: null,
        contractQuantity: null,
        invName: null,
        taxedPrice: null,
        untaxedPrice: null,
        invoicedTaxedAmount: null,
        invoicedUntaxedAmount: null,
        currentUntaxedAmount: null,
        taxRate: null,
        currentTaxedAmount: null
      })
    },
    deleteInvoice (scope) {
      let perInvoiceDetailId = scope.row.perInvoiceDetailId
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
      .then(() => {
        if (perInvoiceDetailId) {
          let { deletePerInvoiceDetailById } = this.$api.cmPerform.vendor.inv.performInvoice
          deletePerInvoiceDetailById(perInvoiceDetailId).then(res => {
            this.perInvoiceDetailList.splice(scope.$index, 1)
          })
        } else {
          this.perInvoiceDetailList.splice(scope.$index, 1)
        }
      })
      .catch(() => {})
    },
    contractPageChange (value) {
      this.contractPageNum = value
      this.getContractList()
    },
    contractSizeChange (value) {
      this.contractPageNum = 1
      this.contractPageSize = value
      this.getContractList()
    },
    async getContractList () {
      let { queryContractMaterial, getMileByContractNo } = this.$api.cmPerform.vendor.inv.performInvoice
      let { buId, buCode, taxId, taxKey, currencyId, currencyCode } = this.form
      let res = await queryContractMaterial({
        pageNum: this.contractPageNum,
        pageSize: this.contractPageSize,
        vendorId: this.userInfo.userInfo.companyId,
        buId,
        buCode,
        taxId,
        taxKey,
        currencyId,
        currencyCode,
        ...this.contractForm
      })
      let list = res.data.list || []
      this.contractData = list
      this.contractTotal = res.data.total || 0
    },
    async getContractList2 (contractNo) {
      let { getPerPayPlanByContractNo } = this.$api.cmPerform.vendor.inv.performInvoice
      let res = await getPerPayPlanByContractNo(contractNo)
      let list = res.data || []
      this.contractData2 = list
      this.contractTotal = res.data.total || 0
    },
    contractRowClick (scope) {
      let validForm
      this.$refs.form.validate(valid => (validForm = valid))
      if (!validForm) {
        this.__focus_error__()
        return
      }
      this.contractScope = scope
      this.contractShow = true
      this.$nextTick(() => this.resetContract())
      this.getContractList()
    },
    contractRowClick2 (scope) {
      let validForm
      this.$refs.form.validate(valid => (validForm = valid))
      if (!validForm) {
        this.__focus_error__()
        return
      }
      this.contractScope = scope
      this.contractShow2 = true
      this.$nextTick(() => this.resetContract())
      this.getContractList2(scope.row.contractNo)
    },
    async rowClick (row, column, event) {
      let { $index } = this.contractScope
      if ($index || $index === 0) {
        let { getMileByContractNo } = this.$api.cmPerform.vendor.inv.performInvoice
        let contractNo = row.contractNo
        if (contractNo) {
          let res = await getMileByContractNo(contractNo)
          let list = res.data || []
          if (list.length) {
            row.milestoneTypeList = []
            for (let item of list) {
              for (let innerItem of this.MILESTONE_SCHEDULE) {
                if (item.milestoneType === innerItem.value) {
                  row.milestoneTypeList.push({
                    value: innerItem.value,
                    label: innerItem.label,
                    performTemplLineId: item.performTemplLineId,
                    perPlanMilestoneId: item.perPlanMilestoneId
                  })
                }
              }
            }
          }
        }
        this.perInvoiceDetailList.splice($index, 1, row)
        this.contractShow = false
      }
    },
    // 点击里程碑选择确定后回显与关掉弹框
    rowClick2 (row, column, event) {
      this.perInvoiceDetailList[this.contractScope.$index].milestoneType = row.milestoneType
      this.perInvoiceDetailList[this.contractScope.$index].perPayPlanId = row.perPayPlanId
      this.contractShow2 = false
    },
    amoutChange (value, row) {
      if (isNaN(+value)) return
      let currentTaxedAmount = Math.abs(value) * (100 + row.taxRate || 0) / 100
      currentTaxedAmount = Math.round(currentTaxedAmount * 100) / 100
      row.currentTaxedAmount = currentTaxedAmount
    },
    resetContract () {
      this.$refs.contractForm.resetFields()
    },
    back () {
      let { tabName } = this.$attrs.params
      this.$emit('tab-remove', tabName)
      this.__setTabTodo('ContractPerformanceInvoiceList.getQuerydata')
    },
    initParams () { // 参数
      let params = {}
      for (let key in this.form) {
        params[key] = this.form[key]
      }
      params.perInvoiceDetailList = JSON.parse(JSON.stringify(this.perInvoiceDetailList)) // 开票明细
      params.perInvoiceDetailList.forEach(item => {
        item.milestoneTypeList = JSON.stringify(item.milestoneTypeList)
      })
      params.perInvoiceInformationList = this.perInvoiceInformationList// 发票信息
      params.fileUploads = this.fileUploads// 合同履约开票计划
      console.log('params:::', params)
      return params
    },
    invoicingVerification () { // 校验是否一个开票单只能有一种里程碑
      if (this.perInvoiceDetailList.length > 0) {
        const milestoneType = this.perInvoiceDetailList[0].milestoneType
        let bol = true
        let bol2 = true
        this.perInvoiceDetailList.forEach((e) => {
          if (!e.milestoneType) {
            bol2 = false
            return false
          }
          if (e.milestoneType != milestoneType) {
            bol = false
          }
        })
        if (!bol2) {
          this.__jump_error__('invocie', 'component', this.$t('cusEntry.supplement20250211.inputMilestoneNode')) // 请输入里程碑节点
          return bol2
        }
        if (!bol) {
          this.__jump_error__('invocie', 'component', this.$t('cusEntry.supplement20250211.openInvoiceMilestoneChoice')) // 一个开票单只能选择一种里程碑
        }
        return bol
      } else {
        this.__jump_error__('invocie', 'component', this.$t('contractMod.billingDetailsMgs'))
        return false
      }
    },
    async saveBill (type) {
      console.log('type:::', type)
      let params = this.initParams()
      let { addOrUpdate, vendorSubmit, approved } = this.$api.cmPerform.vendor.inv.performInvoice
      if (type === 'SAVE') {
        let bol = this.invoicingVerification()
        console.log(bol)
        if (!bol) {
          return false
        }
        addOrUpdate(params).then((res) => {
          this.$message.success(res.message)
          this.getFormDetail(res.data)
        })
      } else if (type === 'SUBMIT') {
        let validForm
        this.$refs.form.validate(valid => (validForm = valid))
        if (!validForm) {
          this.__focus_error__()
          return
        }
        let bol = this.invoicingVerification()
        if (!bol) {
          return false
        }
        let { perInvoiceDetailList } = params
        if (!perInvoiceDetailList.length) {
          this.jumpErrorInvoice(this.$t('cusEntry.supplement20250211.invoiceDetailInfo')) // 请填写开票明细相关信息
          return
        }
        for (let item of perInvoiceDetailList) {
          if (!item.contractNo) {
            this.jumpErrorInvoice(this.$t('cusEntry.supplement20250211.contractNumberRequired')) // 开票明细---合同序号必填
            return
          }
          if (!item.milestoneType) {
            this.jumpErrorInvoice(this.$t('cusEntry.supplement20250211.milestoneRequired')) // 开票明细---里程碑节点必填
            return
          }
          if (!item.currentUntaxedAmount) {
            this.jumpErrorInvoice(this.$t('cusEntry.supplement20250211.currentUntaxedAmountTip')) // 开票明细---本次开票金额未税必填
            return
          }
        }
        vendorSubmit(params).then((res) => {
          this.$message.success(res.message)
          this.back()
        })
      } else if (type === 'REJECT') { // 驳回
        this.rejectShow = true
      } else if (type === 'PASS') { // 通过
        approved(params).then((res) => {
          this.$message.success(res.message)
          this.back()
        })
      }
    },
    jumpErrorInvoice (message) {
      this.__jump_error__('invocie', 'component', message)
    },
    handleReject () {
      if (!this.rejectReason) {
        this.$message.warning(this.$t('bidMod.msgRejectReason'))
        return
      }
      let params = this.initParams()
      params.approvalOpinion = this.rejectReason
      let { rejected } = this.$api.cmPerform.vendor.inv.performInvoice
      rejected(params).then((res) => {
        this.$message.success(res.message)
        this.back()
      })
    },
    async getFormDetail (id) {
      const res = await this.$api.cmPerform.vendor.inv.performInvoice.get(id)
      const { perInvoiceDetailList, perInvoiceInformationList, fileUploads, ...rest } = res.data
      Object.assign(this.form, rest)
      this.perInvoiceId = this.form.perInvoiceId
      this.perInvoiceDetailList = perInvoiceDetailList.map(item => {
        return {
          ...item,
          milestoneTypeList: JSON.parse(item.milestoneTypeList)
        }
      })
      this.perInvoiceInformationList = perInvoiceInformationList
      this.fileUploads = fileUploads
      this.$nextTick(() => {
        this.$refs.sceneAttachment.loadFileInfo()
      })
    }
  }
}
</script>
<style lang="scss" scoped>
.mt-10 {
  margin-top: 10px;
  margin-bottom: 10px;
}
.wrapper {
  padding-bottom: 50px;
}
.input-number-precision {
  width: 100%;
  :deep(.el-input__inner) {
    text-align: left;
    padding-left: 8px;
  }
}
</style>
