<template>
  <el-container
    class="the-vendorGreenChannelDetail-detail"
    direction="vertical"
  >
    <el-main>
      <el-form
        ref="form"
        :rules="rules"
        :model="form"
        :disabled="isReadOnly"
        label-width="80px"
        label-position="top"
        class="form-incontainer"
      >
        <el-collapse
          v-model="activeDims"
          class="tab-form-style"
        >
          <el-collapse-item
            :title="$t('orderMod.buyerOrderSynergy.orderDetailsForm')"
            name="1"
          >
            <el-row type="flex">
              <el-col>
                <el-form-item
                  :label="$t('orderMod.buyerOrderSynergy.orderNumber')"
                  :label-width="formLabelWidth"
                >
                  <el-input
                    v-model="form.orderNumber"
                    disabled
                  />
                </el-form-item>
              </el-col>
              <el-col>
                <el-form-item
                  :label="$t('oneStopShopping.businessEntity')"
                  :label-width="formLabelWidth"
                  prop="ceeaOrgId"
                >
                  <OrganizationSelector
                    ref="organizationSelector"
                    v-model="form.ceeaOrgId"
                    :parent-id="-1"
                    node-type="OU"
                    :scope="form"
                    @select="selectHandler"
                  />
                </el-form-item>
              </el-col>
              <el-col>
                <el-form-item
                  :label="$t('oneStopShopping.orderDate')"
                  :label-width="formLabelWidth"
                >
                  <el-date-picker v-model="form.ceeaPurchaseOrderDate" />
                </el-form-item>
              </el-col>
              <el-col>
                <el-form-item
                  :label="$t('oneStopShopping.ifSupplierConfirm')"
                  :label-width="formLabelWidth"
                  prop="ceeaIfSupplierConfirm"
                >
                  <el-select
                    v-model="form.ceeaIfSupplierConfirm"
                    :disabled="!canEdit"
                  >
                    <el-option
                      v-for="item in yesNoOptions"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row type="flex">
              <el-col>
                <el-form-item
                  :label="$t('orderMod.buyerOrderSynergy.orderType')"
                  :label-width="formLabelWidth"
                  prop="orderType"
                >
                  <el-select
                    v-model="form.orderType"
                    :disabled="!canEdit"
                    @change="setOrderTypeObj"
                  >
                    <el-option
                      v-for="item in orderTypeList"
                      :key="item.id"
                      :label="item.label"
                      :value="item.value"
                      :disabled="(item.value!=='URGENT')&&(item.value!=='CONVENIENT')&&(item.value!=='ZERO_PRICE')&&(item.value!=='CONSIGNMENT')"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col>
                <el-form-item
                  :label="$t('purchaseDemand.ceeaIfConSignment')"
                  :label-width="formLabelWidth"
                  prop="ceeaIfConSignment"
                >
                  <el-select
                    v-model="form.ceeaIfConSignment"
                    :disabled="!canEdit"
                  >
                    <el-option
                      v-for="item in yesNoOptions"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col>
                <el-form-item
                  :label="$t('purchaseDemand.ceeaIfPowerStationBusiness')"
                  :label-width="formLabelWidth"
                  prop="ceeaIfPowerStationBusiness"
                >
                  <el-select
                    v-model="form.ceeaIfPowerStationBusiness"
                    :disabled="!canEdit"
                  >
                    <el-option
                      v-for="item in yesNoOptions"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col>
                <el-form-item
                  :label="$t('orderMod.buyerOrderSynergy.orderStatus')"
                  :label-width="formLabelWidth"
                >
                  <el-select
                    v-model="form.orderStatus"
                    disabled
                  >
                    <el-option
                      v-for="item in orderStatusOpts"
                      :key="item.id"
                      :label="item.label"
                      :value="item.value"
                    />
                  </el-select>
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
                    v-model="form.buyerName"
                    disabled
                  />
                </el-form-item>
              </el-col>
              <el-col>
                <el-form-item
                  :label="$t('oneStopShopping.department')"
                  :label-width="formLabelWidth"
                >
                  <el-input
                    v-model="form.ceeaDepartmentName"
                    disabled
                  />
                </el-form-item>
              </el-col>
              <el-col>
                <el-form-item
                  :label="$t('dataConfMod.version')"
                  :label-width="formLabelWidth"
                >
                  <el-input
                    v-model="form.version"
                    disabled
                  />
                </el-form-item>
              </el-col>
              <el-col>
                <p>
                  <el-button
                    type="text"
                    @click="openWinDialog"
                  >
                    {{ $t("oneStopShopping.viewPaymentTerms") }}
                  </el-button>
                </p>
              </el-col>
            </el-row>
            <el-row>
              <el-col>
                <el-form-item
                  :label="$t('orderMod.buyerOrderSynergy.comments')"
                  :label-width="formLabelWidth"
                >
                  <el-input
                    v-model="form.comments"
                    :disabled="!canEdit"
                    type="textarea"
                    :rows="2"
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col>
                <el-form-item
                  :label="$t('oneStopShopping.creatorOpinion')"
                  :label-width="formLabelWidth"
                  prop="ceeaOpinion"
                >
                  <el-input
                    v-model="form.ceeaOpinion"
                    :disabled="!canEdit"
                    type="textarea"
                    :rows="2"
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <!--sencc-->
            <el-row type="flex">
              <el-col>
                <el-form-item
                  :label="$t('oneStopShopping.receiveAddress')"
                  :label-width="formLabelWidth"
                  prop="receiveAddress"
                >
                  <el-input v-model="form.receiveAddress" />
                </el-form-item>
              </el-col>
              <el-col>
                <el-form-item
                  :label="$t('oneStopShopping.receiveContacts')"
                  :label-width="formLabelWidth"
                  prop="receiveContact"
                >
                  <el-input v-model="form.receiveContact" />
                </el-form-item>
              </el-col>
              <el-col>
                <el-form-item
                  :label="$t('oneStopShopping.receiveTelephone')"
                  :label-width="formLabelWidth"
                  prop="receiveTelephone"
                >
                  <el-input v-model="form.receiveTelephone" />
                </el-form-item>
              </el-col>
              <el-col><p /></el-col>
            </el-row>
            <el-row type="flex">
              <el-col>
                <el-form-item
                  :label="$t('oneStopShopping.receiveOrderAddress')"
                  :label-width="formLabelWidth"
                  prop="ceeaReceiveOrderAddress"
                >
                  <el-input v-model="form.ceeaReceiveOrderAddress" />
                </el-form-item>
              </el-col>
              <el-col>
                <el-form-item
                  :label="$t('oneStopShopping.receiveOrderContact')"
                  :label-width="formLabelWidth"
                  prop="ceeaReceiveOrderContact"
                >
                  <el-input v-model="form.ceeaReceiveOrderContact" />
                </el-form-item>
              </el-col>
              <el-col>
                <el-form-item
                  :label="$t('oneStopShopping.receiveOrderTelephone')"
                  :label-width="formLabelWidth"
                  prop="ceeaReceiveOrderTelephone"
                >
                  <el-input v-model="form.ceeaReceiveOrderTelephone" />
                </el-form-item>
              </el-col>
              <el-col><p /></el-col>
            </el-row>
            <el-row type="flex">
              <el-col>
                <el-form-item
                  :label="$t('common.vendor')"
                  :label-width="formLabelWidth"
                  prop="vendorName"
                >
                  <QuickSearch
                    :show-input="form.vendorName"
                    show-key="companyCode"
                    :scope-data="form"
                    name="scc_sup_company_info"
                    @close-quicksearch="getVendorObj"
                  />
                </el-form-item>
              </el-col>
              <el-col>
                <el-form-item
                  :label="$t('contractMod.linkMan')"
                  :label-width="formLabelWidth"
                >
                  <el-input v-model="form.ceeaSupplierContacts" />
                </el-form-item>
              </el-col>
              <el-col>
                <el-form-item
                  :label="$t('purchaseDemand.costType')"
                  :label-width="formLabelWidth"
                  prop="ceeaCostType"
                >
                  <el-select v-model="form.ceeaCostType">
                    <el-option
                      v-for="item in ceeaCostTypeList"
                      :key="item.siteInfoId"
                      :label="item.vendorSiteCode"
                      :value="item.vendorSiteCode"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col><p /></el-col>
            </el-row>
            <el-row type="flex">
              <el-col>
                <el-form-item
                  :label="$t('oneStopShopping.totalNum')"
                  :label-width="formLabelWidth"
                >
                  <el-input
                    v-model="form.ceeaTotalNum"
                    disabled
                  />
                </el-form-item>
              </el-col>
              <el-col>
                <el-form-item
                  :label="$t('oneStopShopping.totalAmountIncludingTax')"
                  :label-width="formLabelWidth"
                >
                  <el-input
                    v-model="form.ceeaTotalAmountIncludingTax"
                    disabled
                  />
                </el-form-item>
              </el-col>
              <el-col>
                <el-form-item
                  :label="$t('oneStopShopping.totalAmountExcludingTax')"
                  :label-width="formLabelWidth"
                >
                  <el-input
                    v-model="form.ceeaTotalAmountExcludingTax"
                    disabled
                  />
                </el-form-item>
              </el-col>
              <el-col><p /></el-col>
            </el-row>
            <!-- 弹框---付款条款查看--->
            <srm-dialog
              :title="$t('oneStopShopping.viewPaymentTerms')"
              size="large"
              :visible.sync="windialogVisible"
              :close-on-click-modal="false"
            >
              <p class="the_header">
                <span>{{ $t("oneStopShopping.orderNumber") }}</span>{{ form.orderNumber }}
                <el-button @click="windialogVisible = false">
                  {{ $t("common.close") }}
                </el-button>
                <el-button
                  type="primary"
                  @click="addOnePayment"
                >
                  {{ $t("common.add") }}
                </el-button>
              </p>
              <el-table
                :data="displayMaterialItem2"
                style="width: 100%"
                border
                height="251px"
              >
                <el-table-column
                  align="center"
                  prop="paymentPeriodsNumber"
                  :label="$('contractMod.paymentPeriod')"
                  width="80"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="paymentStage"
                  :label="$('contractMod.paymentStage')"
                  width="120"
                  :show-overflow-tooltip="true"
                >
                  <template slot-scope="scope">
                    <el-select v-model="scope.row.paymentStage">
                      <el-option
                        v-for="item in paymentStageOpts"
                        :key="item.id"
                        :label="item.label"
                        :value="item.value"
                      />
                    </el-select>
                  </template>
                </el-table-column>
                <el-table-column
                  align="center"
                  prop="paymentTerm"
                  :label="$('contractMod.payExplain')"
                  min-width="120"
                  :show-overflow-tooltip="true"
                >
                  <template slot-scope="scope">
                    <el-select v-model="scope.row.paymentTerm">
                      <el-option
                        v-for="item in paymentTermOpts"
                        :key="item.id"
                        :label="item.label"
                        :value="item.value"
                      />
                    </el-select>
                  </template>
                </el-table-column>
                <el-table-column
                  align="center"
                  prop="paymentPeriod"
                  :label="$('paymentType.paymentDay1')"
                  width="100"
                  :show-overflow-tooltip="true"
                >
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.paymentPeriod" />
                  </template>
                </el-table-column>
                <el-table-column
                  align="center"
                  prop="paymentRadio"
                  :label="$('contractMod.payRatio')"
                  width="100"
                  :show-overflow-tooltip="true"
                >
                  <template slot-scope="scope">
                    <el-input
                      v-model="scope.row.paymentRadio"
                      type="number"
                    />
                  </template>
                </el-table-column>
                <el-table-column
                  align="center"
                  prop="paymentWay"
                  :label="$('contractMod.paymentMethod')"
                  min-width="120"
                  :show-overflow-tooltip="true"
                >
                  <template slot-scope="scope">
                    <el-select v-model="scope.row.paymentWay">
                      <el-option
                        v-for="item in paymentWayOpts"
                        :key="item.id"
                        :label="item.label"
                        :value="item.value"
                      />
                    </el-select>
                  </template>
                </el-table-column>
                <el-table-column
                  :label="$t('common.operation')"
                  width="60"
                  fixed="right"
                >
                  <template slot-scope="scope">
                    <el-button
                      type="text"
                      @click="deletePaymentItem(scope.$index, scope.row)"
                    >
                      {{ $t("common.delete") }}
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </srm-dialog>
          </el-collapse-item>

          <el-collapse-item
            :title="$t('orderMod.buyerOrderSynergy.orderDetailsList')"
            name="2"
          >
            <el-button
              type="primary"
              @click="openMaterialList"
            >
              {{ $t("oneStopShopping.newPurchaseApply") }}
            </el-button>
            <el-button
              type="primary"
              @click="openDialog"
            >
              {{ $t("oneStopShopping.newMaterial") }}
            </el-button>
            <MImport
              v-if="canEdit"
              ref="import"
              style="display: inline-block;margin-left: 15px;"
              :title="$t('common.import')"
              :up-load-url="upLoadUrl"
              :extra-data="extraData"
              @downloadTemplate="downloadTemplate"
              @handleSuccess="handleSuccess"
            />

            <!-- 弹框区域-->
            <srm-dialog
              :title="$t('purchaseDemand.materialDetailSelect')"
              size="large"
              :visible.sync="dialogFormVisible"
              :close-on-click-modal="false"
            >
              <div>
                <el-form
                  ref="filterForm"
                  :model="filterForm"
                  label-width="80px"
                  label-position="top"
                  class="form-incontainer"
                >
                  <el-row type="flex">
                    <el-col>
                      <el-form-item
                        :label="$t('common.materialCode')"
                        :label-width="formLabelWidth"
                      >
                        <el-input v-model="filterForm.materialCode" />
                      </el-form-item>
                    </el-col>
                    <el-col>
                      <el-form-item
                        :label="$t('common.materialName')"
                        :label-width="formLabelWidth"
                      >
                        <el-input v-model="filterForm.materialName" />
                      </el-form-item>
                    </el-col>
                    <el-col>
                      <el-form-item
                        :label="$t('quota.org')"
                        :label-width="formLabelWidth"
                      >
                        <el-input
                          v-model="filterForm.orgName"
                          disabled
                        />
                      </el-form-item>
                    </el-col>
                    <el-col>
                      <el-form-item
                        :label="$t('bidMod.categoryName')"
                        :label-width="formLabelWidth"
                      >
                        <el-input v-model="filterForm.categoryCode" />
                      </el-form-item>
                    </el-col>
                  </el-row>
                  <el-row type="flex">
                    <el-col>
                      <el-form-item
                        :label="$t('bid_mod.inv')"
                        :label-width="formLabelWidth"
                      >
                        <OrganizationSelector
                          ref="organizationSelectorFilter"
                          v-model="filterForm.organizationId"
                          :parent-id="filterForm.orgId"
                          node-type="INV"
                        />
                      </el-form-item>
                    </el-col>
                    <el-col>
                      <el-form-item
                        :label="$t('purchaseDemand.purRequisitionNum')"
                        :label-width="formLabelWidth"
                      >
                        <el-input v-model="filterForm.requirementHeadNum" />
                      </el-form-item>
                    </el-col>
                    <el-col>
                      <el-form-item
                        :label="$t('purchaseDemand.requestDateFrom')"
                        :label-width="formLabelWidth"
                      >
                        <el-date-picker v-model="filterForm.startDate" />
                      </el-form-item>
                    </el-col>
                    <el-col>
                      <el-form-item
                        :label="$t('purchaseDemand.requestDateTo')"
                        :label-width="formLabelWidth"
                      >
                        <el-date-picker v-model="filterForm.endDate" />
                      </el-form-item>
                    </el-col>
                  </el-row>
                </el-form>
                <p>
                  <el-button
                    type="primary"
                    @click="queryItemList"
                  >
                    {{ $t("common.search") }}
                  </el-button>
                  <el-button @click="resetFilterForm">
                    {{ $t("common.reset") }}
                  </el-button>
                  <el-button
                    type="primary"
                    @click="addNewOne"
                  >
                    {{ $t("common.confirm") }}
                  </el-button>
                </p>
              </div>
              <el-table
                :data="displayMaterialItem"
                style="width: 100%"
                border
                height="345px"
                highlight-current-row
                @selection-change="handleSelectionChange"
              >
                <el-table-column
                  type="selection"
                  width="55"
                />
                <el-table-column
                  align="center"
                  type="index"
                  width="50"
                />
                <el-table-column
                  align="center"
                  prop="orgName"
                  :label="$t('quota.org')"
                  width="120"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="organizationName"
                  :label="$t('bid_mod.inv')"
                  width="120"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="receiptPlace"
                  :label="$t('bid_mod.tradingLocations')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
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
                  :label="$t('bid_mod.unit')"
                  width="60"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="requirementQuantity"
                  :label="$t('orderMod.buyerOrderSynergy.applicationQuantity')"
                  width="80"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="ceeaPlanReceiveDate"
                  :label="$t('purchaseDemand.requirementDate')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="requirementHeadNum"
                  :label="$t('purchaseDemand.purRequisitionNum')"
                  width="120"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="rowNum"
                  :label="$t('purchaseDemand.rowNum')"
                  width="80"
                  :show-overflow-tooltip="true"
                />
              </el-table>
            </srm-dialog>
            <el-table
              :data="tableData"
              style="width: 100%"
              border
              max-height="251px"
            >
              <el-table-column
                align="center"
                prop="ceeaRequirementHeadNum"
                :label="$t('purchaseDemand.purRequisitionNum')"
                width="120"
                :show-overflow-tooltip="true"
              />
              <el-table-column
                align="center"
                prop="ceeaRowNum"
                :label="$t('purchaseDemand.rowNum')"
                width="80"
                :show-overflow-tooltip="true"
              />
              <el-table-column
                align="center"
                prop="ceeaOrganizationName"
                :label="$t('bid_mod.inv')"
                width="150"
                :show-overflow-tooltip="true"
              />
              <el-table-column
                align="center"
                prop="receiptPlace"
                :label="$t('bid_mod.tradingLocations')"
                width="100"
                :show-overflow-tooltip="true"
              >
                <template slot-scope="scope">
                  <el-input v-model="scope.row.receiptPlace" />
                </template>
              </el-table-column>
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
                width="120"
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
                :label="$t('bid_mod.unit')"
                width="60"
                :show-overflow-tooltip="true"
              />
              <el-table-column
                align="center"
                prop="orderNum"
                :label="$t('orderMod.buyerOrderSynergy.orderNum')"
                width="100"
                :show-overflow-tooltip="true"
              >
                <template slot-scope="scope">
                  <el-input
                    v-model="scope.row.orderNum"
                    v-input-format="{ type: 'number' }"
                    @input="setRowAmount(scope.row)"
                  />
                </template>
              </el-table-column>
              <el-table-column
                align="center"
                prop="ceeaPlanReceiveDate"
                :label="$t('purchaseDemand.requirementDate1')"
                width="160"
                :formatter="formatter1"
                :show-overflow-tooltip="true"
              >
                <template slot-scope="scope">
                  <el-date-picker
                    v-model="scope.row.ceeaPlanReceiveDate"
                    type="date"
                    format="yyyy-MM-dd"
                    value-format="yyyy-MM-dd"
                  />
                </template>
              </el-table-column>
              <el-table-column
                align="center"
                prop="ceeaPromiseReceiveDate"
                :label="$t('purchaseDemand.promiseReceiveDate')"
                width="160"
                :show-overflow-tooltip="true"
              >
                <template slot-scope="scope">
                  <el-date-picker
                    v-model="scope.row.ceeaPromiseReceiveDate"
                    type="date"
                    format="yyyy-MM-dd"
                    value-format="yyyy-MM-dd"
                  />
                </template>
              </el-table-column>
              <el-table-column
                align="center"
                prop="ceeaSinglePriceIncludingTax"
                :label="$t('contractMod.taxedPrice')"
                width="100"
                :show-overflow-tooltip="true"
              />
              <el-table-column
                align="center"
                prop="ceeaSinglePriceExcludingTax"
                :label="$t('contractMod.notaxPrice')"
                width="100"
                :show-overflow-tooltip="true"
              />
              <el-table-column
                align="center"
                prop="currencyName"
                :label="$t('quota.currency')"
                width="100"
                :show-overflow-tooltip="true"
              />
              <el-table-column
                align="center"
                prop="ceeaTaxRate"
                :label="$t('bid_mod.taxRate')"
                width="100"
                :show-overflow-tooltip="true"
              >
                <template slot-scope="scope">
                  <el-select v-model="scope.row.ceeaTaxRate">
                    <el-option
                      v-for="item in taxList"
                      :key="item.id"
                      :label="item.label"
                      :value="item.value"
                    />
                  </el-select>
                </template>
              </el-table-column>
              <el-table-column
                align="center"
                prop="ceeaAmountIncludingTax"
                :label="$t('contractMod.amount2')"
                width="100"
                :show-overflow-tooltip="true"
              />
              <el-table-column
                align="center"
                prop="ceeaAmountExcludingTax"
                :label="$t('contractMod.excludeTaxPayAmount')"
                width="100"
                :show-overflow-tooltip="true"
              />
              <el-table-column
                align="center"
                prop="ceeaTaxAmount"
                :label="$t('contractMod.taxQuota')"
                width="100"
                :show-overflow-tooltip="true"
              />
              <el-table-column
                align="center"
                prop="ceeaContractNo"
                :label="$t('purchaseDemand.contractNum')"
                width="100"
                :show-overflow-tooltip="true"
              />
              <el-table-column
                align="center"
                prop="comments"
                :label="$t('purchaseDemand.comments')"
                width="100"
                :show-overflow-tooltip="true"
              >
                <template slot-scope="scope">
                  <el-input v-model="scope.row.comments" />
                </template>
              </el-table-column>
              <el-table-column
                :label="$t('common.operation')"
                width="60"
                fixed="right"
              >
                <template slot-scope="scope">
                  <el-button
                    type="text"
                    :disabled="!canEdit"
                    @click="deleteDetials(scope.$index, scope.row)"
                  >
                    {{ $t("common.delete") }}
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
            <srm-dialog
              :title="$t('purchaseDemand.materialDetailSelect')"
              size="large"
              :visible.sync="dialogVisible"
              :close-on-click-modal="false"
            >
              <div>
                <el-form
                  ref="queryForm"
                  :model="queryForm"
                  label-width="80px"
                  label-position="top"
                  class="form-incontainer"
                >
                  <el-row type="flex">
                    <el-col>
                      <el-form-item
                        :label="$t('common.materialCode')"
                        :label-width="formLabelWidth2"
                      >
                        <QuickSearch
                          :show-input="queryForm.materialCode"
                          show-key="materialCode"
                          :scope-data="queryForm"
                          name="scc_base_material_item_display"
                          @close-quicksearch="getItemObj"
                        />
                      </el-form-item>
                    </el-col>
                    <el-col>
                      <el-form-item
                        :label="$t('common.materialName')"
                        :label-width="formLabelWidth2"
                      >
                        <el-input
                          v-model="queryForm.materialName"
                          disabled
                        />
                      </el-form-item>
                    </el-col>
                    <el-col>
                      <el-form-item
                        :label="$t('bid_mod.inv')"
                        :label-width="formLabelWidth2"
                      >
                        <OrganizationSelector
                          ref="organizationSelector3"
                          v-model="queryForm.organizationId"
                          :parent-id="form.ceeaOrgId"
                          node-type="INV"
                          :scope="queryForm"
                          @select="selectHandler3"
                        />
                      </el-form-item>
                    </el-col>
                  </el-row>
                  <el-row type="flex">
                    <el-col :span="8">
                      <el-form-item
                        :label="$t('purchaseDemand.materialCate')"
                        :label-width="formLabelWidth2"
                      >
                        <el-input
                          v-model="queryForm.inputLevel"
                          :placeholder="$t('common.pleaseTypeContents')"
                          class="input-with-select"
                          clearable
                        >
                          <el-select
                            slot="prepend"
                            v-model="queryForm.selectLevel"
                            style="width:75px"
                            :placeholder="$t('common.pleaseSelect')"
                            clearable
                          >
                            <el-option
                              :id="1"
                              :label="$t('purchaseDemand.bigCategory')"
                              value="1"
                            />
                            <el-option
                              :id="2"
                              :label="$t('purchaseDemand.midCategory')"
                              value="2"
                            />
                            <el-option
                              :id="3"
                              :label="$t('purchaseDemand.smallCategory')"
                              value="3"
                            />
                          </el-select>
                        </el-input>
                      </el-form-item>
                    </el-col>
                    <el-col :span="8">
                      <p />
                    </el-col>
                    <el-col :span="8">
                      <p style="padding-top: 10px;">
                        <el-button
                          type="primary"
                          @click="queryContent"
                        >
                          {{ $t('common.search') }}
                        </el-button>
                        <el-button
                          type="primary"
                          @click="addOneContent"
                        >
                          {{ $t('common.confirm') }}
                        </el-button>
                      </p>
                    </el-col>
                  </el-row>
                </el-form>
                <el-table
                  :data="displayItemTable"
                  style="width: 100%"
                  border
                  height="345px"
                  highlight-current-row
                  @selection-change="handleItemSelection"
                >
                  <el-table-column
                    align="center"
                    type="index"
                    :label="$t('contractMod.tabindex')"
                    width="60"
                  />
                  <el-table-column
                    type="selection"
                    width="55"
                  />
                  <el-table-column
                    align="center"
                    prop="organizationName"
                    :label="$t('bid_mod.inv')"
                    width="150"
                    :show-overflow-tooltip="true"
                  />
                  <el-table-column
                    align="center"
                    prop="bigCategoryName"
                    :label="$t('purchaseDemand.materialCate')"
                    width="120"
                    :show-overflow-tooltip="true"
                  />
                  <el-table-column
                    align="center"
                    prop="middleCategoryName"
                    :label="$t('purchaseDemand.midCategoryName')"
                    width="120"
                    :show-overflow-tooltip="true"
                  />
                  <el-table-column
                    align="center"
                    prop="categoryName"
                    :label="$t('purchaseDemand.materialCateSub')"
                    width="120"
                    :show-overflow-tooltip="true"
                  />
                  <el-table-column
                    align="center"
                    prop="materialCode"
                    :label="$t('common.materialCode')"
                    width="120"
                    :show-overflow-tooltip="true"
                  />
                  <el-table-column
                    align="center"
                    prop="materialName"
                    :label="$t('common.materialName')"
                    min-width="150"
                    :show-overflow-tooltip="true"
                  />
                </el-table>
                <el-row type="flex">
                  <el-col>
                    <CPagination
                      ref="queryPagination"
                      style="margin:5px"
                      class="c-query-table-pagination"
                      :total="queryTotal"
                      :page-num="viewIndex"
                      :page-size="viewSize"
                      @current-change="changeCurrentIndex"
                      @size-change="changeCurrentSize"
                    />
                  </el-col>
                </el-row>
              </div>
            </srm-dialog>
          </el-collapse-item>

          <el-collapse-item
            :title="$t('orderMod.approveAttachInfo')"
            name="3"
          >
            <p class="btn_line">
              <el-button
                type="primary"
                @click="addUploadOneApproval"
              >
                {{ $t('common.add') }}
              </el-button>
            </p>
            <el-table
              :data="approvalFileList"
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
                :label="$t('quota.fileupload')"
                width="250"
              >
                <template slot-scope="scope">
                  <SrmCommonFile
                    :extra-data="fileInfo"
                    :default-file="{
                      fileId: scope.row.fileuploadId,
                      fileName: scope.row.attachName
                    }"
                    :readonly="false"
                    @on-change="({file}) => outerHandleUploadSuccess(file,scope.row)"
                  />
                </template>
              </el-table-column>
              <el-table-column
                align="center"
                prop="createdUserName"
                :label="$t('quota.uploadBy')"
                width="150"
              />
              <el-table-column
                align="center"
                prop="creationDate"
                :label="$t('quota.uploadDate')"
                width="150"
              />
              <el-table-column
                :label="$t('common.operation')"
                width="60"
              >
                <template slot-scope="scope">
                  <el-button
                    type="text"
                    @click="handleDelClick(scope.$index, scope.row)"
                  >
                    {{ $t('common.delete') }}
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-collapse-item>
          <el-collapse-item
            :title="$t('orderMod.orderAttachInfo')"
            name="4"
          >
            <p class="btn_line">
              <el-button
                type="primary"
                @click="addUploadOneOrder"
              >
                {{ $t('common.add') }}
              </el-button>
            </p>
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
              <el-table-column
                align="center"
                prop="attachName"
                :label="$t('bidMod.fileName')"
                width="250"
              >
                <template slot-scope="scope">
                  <SrmCommonFile
                    :extra-data="fileInfo"
                    :default-file="{
                      fileId: scope.row.fileuploadId,
                      fileName: scope.row.attachName
                    }"
                    :readonly="false"
                    @on-change="({file}) => outerHandleUploadSuccess(file,scope.row)"
                  />
                </template>
              </el-table-column>
              <el-table-column
                align="center"
                prop="createdUserName"
                :label="$t('quota.uploadBy')"
                width="150"
              />
              <el-table-column
                align="center"
                prop="creationDate"
                :label="$t('quota.uploadDate')"
                width="150"
              />
              <el-table-column
                :label="$t('common.operation')"
                width="60"
              >
                <template slot-scope="scope">
                  <el-button
                    type="text"
                    @click="handleDelClick2(scope.$index, scope.row)"
                  >
                    {{ $t('common.delete') }}
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-collapse-item>
        </el-collapse>
      </el-form>

      <CToolbar>
        <template slot="right">
          <el-button
            v-if="form.orderStatus ==='APPROVED'"
            type="primary"
            @click="printBill"
          >
            {{ $t("common.pdfPrint") }}
          </el-button>
          <el-button
            v-if="!isReadOnly"
            type="primary"
            @click="saveBill"
          >
            {{
              this.$t("common.save")
            }}
          </el-button>
          <el-button
            v-if="!isReadOnly"
            type="primary"
            @click="submitBill"
          >
            {{
              this.$t("common.submit")
            }}
          </el-button>
          <el-button
            v-if="!isReadOnly"
            @click="cancelBill"
          >
            {{
              this.$t("common.backTo")
            }}
          </el-button>
          <!--
          <el-button
            v-if="isReadOnly"
            type="primary"
            @click="refuse"
            >{{ $t("orderMod.buyerOrderSynergy.sureRefuse") }}</el-button>-->
        </template>
      </CToolbar>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoMixin } from '@/utils/mixins'
import MainHeader from 'lib@/components/Table/MainHeader'
import CPagination from 'lib@/components/c-pagination'
import { parseTime, adaptDictData } from '@/utils'
import {
  getDictItemList,
  getAllPurTax
} from '@/api/common'
import OrganizationSelector from 'lib@/components/organization-selector'
import QuickSearch from 'lib@/components/QuickSearch' // 快速查询组件
import CToolbar from 'lib@/components/c-toolbar'
import MImport from 'lib@/components/import'
import { downloadFileLink } from 'lib@/utils/file'
import CustomSelect from 'modb@/purchasingDemand/views/applicationAndAudit/select'
import { date } from 'jszip/lib/defaults'

export default {
  name: 'VendorInfoChangeDetail',
  components: {
    MainHeader,
    QuickSearch,
    CToolbar,
    CustomSelect,
    MImport,
    OrganizationSelector,
    CPagination
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      fileInfo: {
        fileModular: 'sup',
        fileFunction: 'vendorBiddingManagement',
        fileType: 'images'
      },
      upLoadUrl: '/api-sup-ce/po/orderDetail/importExcel',
      extraData: {},
      yesNoOptions: [{ value: 'Y', label: this.$t('common.yes') }, { value: 'N', label: this.$t('common.no') }],
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
      queryTotal: -1,
      viewSize: 10,
      viewIndex: 1,
      queryForm: {
        'materialCode': '',
        'materialName': '',
        'organizationId': null,
        'orgId': '',
        'bigCategoryName': '',
        'bigCategoryId': null,
        'middleCategoryName': '',
        'middleCategoryId': null,
        'categoryId': null,
        'categoryName': '',
        'inputLevel': '',
        'selectLevel': ''
      },
      dialogVisible: false,
      displayItemTable: [],
      parentOrgQueryForm: {
        pageNum: 1,
        pageSize: 10
      },
      windialogVisible: false,
      dialogFormVisible: false,
      selection: [],
      orderFileList: [],
      approvalFileList: [],
      displayMaterialItem: [],
      displayMaterialItem2: [],
      globalOrderNum: null,
      canEdit: false,
      filterForm: {
        materialCode: null,
        materialName: null,
        orgName: null,
        categoryCode: null,
        organizationId: null,
        requirementHeadNum: null,
        startDate: null,
        endDate: null,
        vendorId: null,
        'orgId': null, // 业务实体id  必传
        'purchaseType': null // 采购类型 必传
      },
      form: {
        ceeaOrgId: null,
        ceeaOrgCode: null,
        ceeaOrgName: null,
        orderId: null,
        orderNumber: null,
        orderStatus: 'DRAFT',
        ceeaPurchaseOrderDate: new Date(),
        ceeaIfSupplierConfirm: 'Y',
        orderType: null,
        ceeaIfConSignment: 'Y',
        ceeaIfPowerStationBusiness: 'Y',
        buyerName: null,
        version: 0,
        comments: null,
        ceeaOpinion: null,
        receiveAddress: null,
        receiveContact: null,
        receiveTelephone: null,
        ceeaReceiveOrderAddress: null,
        ceeaReceiveOrderContact: null,
        ceeaReceiveOrderTelephone: null,
        ceeaOrderTypeIdentification: null, // 订单类型条目标识
        vendorId: null,
        vendorCode: null,
        vendorName: null,
        ceeaSupplierContacts: null,
        ceeaCostType: null,
        ceeaTotalNum: null, // 合计数量
        ceeaTotalAmountIncludingTax: null, // 合计金额含税
        ceeaTotalAmountExcludingTax: null // 合计金额不含税
      },
      ceeaCostTypeList: [],
      queryParams: {},
      isDisabled: this.$attrs.params.flag == 'edit',
      formLabelWidth: '120px',
      formLabelWidth2: '120px',
      tableData: [],
      isModify: false,
      activeDims: ['1', '2', '3', '4'],
      taxList: [],
      unitList: [],
      globalOrderTypeList: [],
      orderTypeList: [],
      orderStatusOpts: [],
      canOperate: false,
      currentRows: [],
      paymentMethodOpts: [],
      paymentTermsOpts: [],
      responseStatus: [],
      sourceSystem: [],
      purchaseorderRowStatus: [],
      paymentStageOpts: [],
      paymentTermOpts: [],
      paymentWayOpts: [],
      jitOrder: [],
      saleLabel: [],
      visible: false,
      costType: [],
      loading: false,
      modalVisible: false,
      addItemData: {},
      rules: {
        ceeaOrgId: [{ required: true, message: this.$t('quota.orgIdTips') }], // 请选择业务实体
        orderType: [{ required: true, message: this.$t('purchaseDemand.orderTypeTips') }], // 请选择订单类型
        vendorName: [{ required: true, message: this.$t('quota.vendorTips') }], // 请选择供应商
        ceeaCostType: [{ required: true, message: this.$t('quota.costTypeTips') }], // 请选择成本类型
        ceeaOpinion: [{ required: true, message: this.$t('oneStopShopping.creatorOpinionNotNull') }], // 请输入起草节点人意见
        receiveAddress: [{ required: true, message: this.$t('orderMod.buyerOrderSynergy.msgReceiveAddress') }], // 请输入收货地址
        receiveContact: [{ required: true, message: this.$t('oneStopShopping.receiveContactsMsg') }], // 请输入收货联系人
        receiveTelephone: [{ required: true, message: this.$t('oneStopShopping.receiveTelephoneMsg') }], // 请输入收货联系电话
        ceeaReceiveOrderAddress: [{ required: true, message: this.$t('oneStopShopping.receiveOrderAddressMsg') }], // 请输入收单地址
        ceeaReceiveOrderContact: [{ required: true, message: this.$t('oneStopShopping.receiveOrderContactMsg') }], // 请输入收单联系人
        ceeaReceiveOrderTelephone: [{ required: true, message: this.$t('oneStopShopping.receiveOrderTelephoneMsg') }], // 请输入收单联系电话
        ceeaIfConSignment: [{ required: true, message: this.$t('orderMod.msgOrder[2]') }], // 请选择是否寄售
        ceeaIfPowerStationBusiness: [{ required: true, message: this.$t('orderMod.msgOrder[3]') }], // 请选择否电站业务
        ceeaIfSupplierConfirm: [{ required: true, message: this.$t('orderMod.msgOrder[4]') }]// 请选择是否供方确认
      }
    }
  },
  computed: {
    isEmpty () {
      return this.tableData.length === 0
    },
    isReadOnly () {
      return this.$attrs.params.showType === 'readOnly'
    },
    contractIds () {
      return this.tableData.map(i => i.contractMaterialId).filter(i => !!i)
    }
  },
  watch: {
    form (n, o) {
      const { vendorId, organizationId } = n
      const { vendorIdO, organizationIdO } = o
      if (vendorId !== vendorIdO || organizationId !== organizationIdO) {
        this.extraData = { vendorId, organizationId }
        console.log(this.extraData)
      }
    }
  },
  created () {},
  mounted () {
    const { row, flag, showType } = this.$attrs.params
    if (flag === 'add') {
      this.canEdit = true
      const { companyId, phone, nickname, department } = this.$store.getters.user.userInfo
      this.form.tel = phone
      this.form.buyerName = nickname
      this.form.ceeaDepartmentName = department
    }
    if (flag == 'edit') {
      const { orderStatus } = row
      // if (orderStatus === "UNISSUED") {
        if (showType !== 'readOnly') {
          this.canEdit = true
        }
      // }
      this.queryDetails()
    }
    // 字典信息查询
    const dictionaryCodes = [
      { dictCode: 'PAYMENT_MODE' },
      { dictCode: 'PAYMENT_TERM' },
      { dictCode: 'PAYMENT_STAGE' },
      { dictCode: 'PURCHASE_ORDER' },
      { dictCode: 'PAYMENT_METHOD' },
      { dictCode: 'PAYMENT_TERMS' },
      { dictCode: 'RESPONSE_STATUS' },
      { dictCode: 'SOURCE_SYSTERM' },
      { dictCode: 'PURCHASEORDER_ROWSTATUS' },
      { dictCode: 'SALE_LABEL' },
      { dictCode: 'JIT_ORDER' },
      { dictCode: 'COST_TYPE' },
      { dictCode: 'ORDER_TYPE' }
    ]
    getDictItemList(dictionaryCodes).then(res => {
      const [
        PAYMENT_MODE,
        PAYMENT_TERM,
        PAYMENT_STAGE,
        PURCHASE_ORDER,
        PAYMENT_METHOD,
        PAYMENT_TERMS,
        RESPONSE_STATUS,
        SOURCE_SYSTERM,
        PURCHASEORDER_ROWSTATUS,
        SALE_LABEL,
        JIT_ORDER,
        COST_TYPE,
        ORDER_TYPE
      ] = res.data

      this.paymentWayOpts = adaptDictData(PAYMENT_MODE.PAYMENT_MODE)
      this.paymentTermOpts = adaptDictData(PAYMENT_TERM.PAYMENT_TERM)
      this.paymentStageOpts = adaptDictData(PAYMENT_STAGE.PAYMENT_STAGE)
      this.orderStatusOpts = adaptDictData(PURCHASE_ORDER.PURCHASE_ORDER)
      this.paymentMethodOpts = adaptDictData(PAYMENT_METHOD.PAYMENT_METHOD)
      this.paymentTermsOpts = adaptDictData(PAYMENT_TERMS.PAYMENT_TERMS)
      this.responseStatus = adaptDictData(RESPONSE_STATUS.RESPONSE_STATUS)
      this.sourceSystem = adaptDictData(SOURCE_SYSTERM.SOURCE_SYSTERM)
      this.purchaseorderRowStatus = adaptDictData(
        PURCHASEORDER_ROWSTATUS.PURCHASEORDER_ROWSTATUS
      )
      this.saleLabel = adaptDictData(SALE_LABEL.SALE_LABEL)
      this.jitOrder = adaptDictData(JIT_ORDER.JIT_ORDER)
      this.costType = adaptDictData(COST_TYPE.COST_TYPE)
      this.orderTypeList = adaptDictData(ORDER_TYPE.ORDER_TYPE)
      this.globalOrderTypeList = ORDER_TYPE.ORDER_TYPE
    })
    // 获取所有税率（%）
    getAllPurTax().then(res => {
      this.taxList = adaptDictData(res.data, 'tax')
    })
  },
  methods: {
    onOk (value = []) {
      if (!value || value.length === 0) {
        return
      }
      // const ids = this.tableData.map(i => i.materialId);
      // if (value.some(i => ids.includes(i.materialId))) {
      //   this.$message({
      //     type: "error",
      //     message: this.$t("oneStopShopping.cannotAddSameMaterial")
      //   });
      //   return;
      // }
      value.forEach(i => {
        const flag =
          (this.tableData || [])
            .filter(val => val.contractMaterialId)
            .findIndex(j => j.contractMaterialId === i.contractMaterialId) ===
          -1
        if (flag) {
          const {
            contractHeadId,
            contractMaterialId,
            contractNo,
            lineNumber,
            categoryCode,
            categoryName,
            categoryId,
            materialId,
            materialCode,
            materialName,
            unitCode,
            currencyCode,
            untaxedPrice,
            taxedPrice
          } = i
          const item = {
            lineNum: this.tableData.length + 1,
            externalId: contractHeadId,
            externalNum: contractNo,
            externalRowNum: lineNumber,
            externalRowId: contractMaterialId,
            categoryCode,
            categoryName,
            categoryId,
            materialId,
            materialCode,
            materialName,
            unit: unitCode,
            currency: currencyCode,
            // 未税单价
            unitPriceExcludingTax: untaxedPrice,
            // 含税单价
            unitPriceContainingTax: taxedPrice,
            // 默认选中标准
            saleLabel: 'STANDARD',
            // 来源类型
            externalType: 'CONTRACT'
          }
          this.tableData.push(item)
        }
      })
      this.modalVisible = false
    },
    onCancle () {
      this.modalVisible = false
    },
    onCancle1 () {
      this.visible = false
    },
    formatDate (val) {
      return val ? this.$dayjs(val).format('YYYY-MM-DD') : val
    },
    selectHandler (node, value, scope) {
      this.form.ceeaOrgId = node.organizationId
      this.form.ceeaOrgCode = node.organizationCode
      this.form.ceeaOrgName = node.organizationName
    },
    selectHandler2 (node, value, scope) {
      this.filterForm.organizationId = node.organizationId
    },
    selectHandler3 (node, value, scope) {
      scope.organizationId = node.organizationId
      scope.organizationCode = node.organizationCode
      scope.organizationName = node.organizationName
    },
    refuse () {
      this.$prompt(
        this.$t('orderMod.buyerOrderSynergy.rejectPrompt'),
        this.$t('orderMod.buyerOrderSynergy.rejectPromptTitle'),
        {
          confirmButtonText: this.$t('common.confirm'),
          cancelButtonText: this.$t('common.cancel')
        }
      )
        .then(({ value }) => {
          this.$api.po
            .returnOrder({
              orderId: this.form.orderId,
              purchaseResponse: value
            })
            .then(res => {
              this.$message({
                type: 'success',
                message: res.message
              })
              this.cancelBill()
            })
        })
        .catch(() => {})
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
        `采购订单导入模板_${new Date().getTime()}.xls`
      )
    },
    getVendorObj (val, scope) {
      scope.vendorId = val ? val.companyId : ''
      scope.vendorCode = val ? val.companyCode : ''
      scope.vendorName = val ? val.companyName : ''
      scope.ceeaSupplierContacts = val ? val.legalPerson : ''
      // scope.ceeaCostType = val ? val.companyAddress : "";
      if (val && val.companyId) {
        this.$http({
        url: '/api-sup/info/siteInfo/listSiteInfoByParam',
        method: 'POST',
        data: { companyId: val.companyId },
        loading: true
      }).then(res => {
          this.ceeaCostTypeList = res.data
      })
      }
    },
    handleSelectionChange (selection) {
      this.selection = selection
    },
    resetFilterForm () {
      for (let i in this.filterForm) {
        this.filterForm[i] = ''
      }
    },
    openWinDialog () {
      this.windialogVisible = true
    },
    addNewOne () {
      if (this.selection.length === 0) {
        this.$message({ type: 'warning', message: this.$t('orderMod.msgOrder[8]') })
        return
      }
      this.selection.forEach(v => {
        const item = {
            // lineNum: this.tableData.length + 1,
            ceeaIfRequirement: 'Y',
            ceeaRequirementLineId: v.requirementLineId,
            ceeaRequirementHeadNum: v.requirementHeadNum,
            ceeaRowNum: this.tableData.length + 1,
            ceeaOrganizationId: v.organizationId,
            ceeaOrganizationCode: v.organizationCode,
            ceeaOrganizationName: v.organizationName,
            receiptPlace: v.receivedFactory,
            categoryId: v.categoryId,
            categoryCode: v.categoryCode,
            categoryName: v.categoryName,
            materialId: v.materialId,
            materialCode: v.materialCode,
            materialName: v.materialName,
            unit: v.unit,
            unitCode: v.unitCode,
            requirementQuantity: v.requirementQuantity,
            ceeaPlanReceiveDate: v.ceeaPlanReceiveDate,
            ceeaPromiseReceiveDate: v.ceeaPromiseReceiveDate,
            ceeaSinglePriceIncludingTax: v.taxPrice,
            ceeaSinglePriceExcludingTax: v.noTaxPrice,
            currencyId: v.currencyId,
            currencyCode: v.currencyCode,
            currencyName: v.currencyName,
            ceeaTaxRate: v.taxRate,
            ceeaAmountIncludingTax: null,
            ceeaAmountExcludingTax: null,
            ceeaTaxAmount: v.ceeaTaxAmount,
            ceeaContractNo: v.ceeaContractNo,
            comments: v.comments
          }
          this.tableData.push(item)
      })
      this.dialogFormVisible = false
    },
    queryItemList () {
      const data = { ...this.parentOrgQueryForm, ...this.filterForm }
      this.$http({
        url: '/api-sup-ce/pr/requirementLine/listPageForOrder',
        method: 'POST',
        data: data,
        loading: true
      }).then(res => {
          this.displayMaterialItem = res.data
          this.dialogFormVisible = true
      })
    },
    openMaterialList () {
      if (!this.form.ceeaOrgId || !this.form.orderType) {
        this.$message.warning(this.$t('orderMod.selOrgAndOrdertypeFirst'))
        return
      }
      if (!this.form.vendorId) {
        this.$message.warning(this.$t('bid_mod.setPermissionError'))
        return
      }
      //
      this.filterForm.orgId = this.form.ceeaOrgId
      this.filterForm.orgName = this.form.ceeaOrgName
      this.filterForm.purchaseType = this.form.orderType
      this.filterForm.vendorId = this.form.vendorId
      this.queryItemList()
    },
    checkFields () {
      const checkFeilds = [
        'receivedFactory',
        'saleLabel',
        'costType',
        'costNum',
        'inventoryPlace',
        'orderNum',
        'ceeaPlanReceiveDate'
      ]
      if (this.tableData.some(i => checkFeilds.some(j => !i[j] && i[j] !== 0))) {
        this.$message({
          type: 'error',
          message: this.$t('orderMod.buyerOrderSynergy.checkMsg')
        })
        return false
      }
      if (this.tableData.some(i => i.orderNum <= 0)) {
        this.$message({
          type: 'error',
          message: this.$t('orderMod.msgOrder[11]')
        })
        return false
      }
      return true
    },
    queryDetails () {
      const { orderId } = this.$attrs.params.row
      this.$api.po.queryOrderById(orderId).then(res => {
        this.form = res.data.order
        this.tableData = res.data.detailList
        this.displayMaterialItem2 = res.data.paymentProvisionList
        if (res.data.attachList) {
              this.approvalFileList = res.data.attachList.filter(v => v.orderAttachType == 'APPROVAL')
              this.orderFileList = res.data.attachList.filter(v => v.orderAttachType == 'ORDER')
            }
      })
    },
    selectionChange (selection) {
      this.canOperate = selection && selection.length
      this.currentRows = selection
    },
    deleteDetials (index, row) {
      this.tableData.splice(index, 1)
      this.tableData.forEach((r, i) => {
        this.$set(this.tableData[i], 'lineNum', i + 1)
      })
    },
    setRowAmount (row) {
      row.ceeaAmountIncludingTax = Number(row.orderNum * row.ceeaSinglePriceIncludingTax || 0).toFixed(2)
      row.ceeaAmountExcludingTax = Number(row.orderNum * row.ceeaSinglePriceExcludingTax || 0).toFixed(2)
      // 合计num
      this.form.ceeaTotalNum = this.tableData.map(v => v.orderNum).reduce((p, c) => ((Number(p) || 0) + (Number(c) || 0)))
      // 合计金额含税
      this.form.ceeaTotalAmountIncludingTax = Number(this.tableData.map(v => v.ceeaAmountIncludingTax).reduce((p, c) => (Number(p) + Number(c)))).toFixed(2)
      // 合计金额不含税
      this.form.ceeaTotalAmountExcludingTax = Number((this.tableData.map(v => v.ceeaAmountExcludingTax).reduce((p, c) => (Number(p) + Number(c))) || 0)).toFixed(2)
    },
    getCategoryObj (val, scope) {
      scope.categoryId = val ? val.categoryId : ''
      scope.categoryCode = val ? val.categoryCode : ''
      scope.categoryName = val ? val.categoryName : ''
    },
    getItemObj (val, scope) {
      scope.materialId = val ? val.materialId : ''
      scope.materialCode = val ? val.materialCode : ''
      scope.materialName = val ? val.materialName : ''
    },
    addOnePayment () {
      this.displayMaterialItem2.push({
        paymentPeriodsNumber: Number(this.displayMaterialItem2.length) + 1,
        paymentTerm: null,
        paymentPeriod: null,
        paymentRadio: null,
        paymentStage: null,
        paymentWay: null
      })
    },
    setOrderTypeObj (val) {
      let obj = this.globalOrderTypeList.filter(v => v.dictItemCode === val)
      if (obj) {
        this.form.ceeaOrderTypeIdentification = obj[0].dictItemMark
      } else {
        this.form.ceeaOrderTypeIdentification = null
      }
    },
    saveOnePayment () {
      this.windialogVisible = false
      /* if(!this.form.orderId) return;
      this.$http({
        url: "/api-sup-ce/po/order/saveOrderPaymentProvision",
        method: "POST",
        data: {
          "order":{
            "orderId": this.form.orderId
          },
          "paymentProvisionList": this.displayMaterialItem2
        },
        loading: true
      }).then(res => {
         console.log(res)
      }); */
    },
    deletePaymentItem (index, row) {
      this.displayMaterialItem2.splice(index, 1)
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
      row.fileuploadId = fileId.toString()
      row.attachName = fileName
      row.createdBy = createdBy
      row.creationDate = creationDate
    },
    handleDelClick (index, row) {
      this.approvalFileList.splice(index, 1)
    },
    handleDelClick2 (index, row) {
      this.orderFileList.splice(index, 1)
    },
    formatter1 (row, column, cellValue, index) {
      return cellValue ? cellValue.slice(0, 10) : null
    },
    addOneItem (val) {
      if (val) {
        const data = {
          ...val,
          lineNum: this.tableData.length + 1
        }
        console.log(data)
        this.tableData.push(data)
      }
    },
    deleteOne (row) {
      this.tableData.splice(row.$index, 1)
    },
    editOne () {},
    submitBill () {
      // 验证form表单
      this.$refs.form.validate(valid => {
        if (valid) {
          if (!this.tableData.length) {
            this.$message({
              type: 'error',
              message: this.$t('orderMod.buyerOrderSynergy.orderDetailsMsg')
            })
            return
          }
          const params = {
            order: this.form,
            paymentProvisionList: this.displayMaterialItem2,
            attachList: this.approvalFileList.concat(this.orderFileList),
            detailList: this.tableData.map(item => ({
              ...item,
              deliveryDate: new Date(item.deliveryDate).getTime()
            }))
          }
          if (!this.form.orderId) return
          this.$http({
            url: '/api-sup-ce/po/order/submit',
            method: 'POST',
            data: params
          }).then(res => {
            this.$message({
              type: 'success',
              message: res.message
            })
            this.cancelBill()
          })
        } else {
          return false
        }
      })
    },
    saveBill () {
      // 验证form表单
      this.$refs.form.validate(valid => {
        if (valid) {
          if (!this.tableData.length) {
            this.$message({
              type: 'error',
              message: this.$t('orderMod.buyerOrderSynergy.orderDetailsMsg')
            })
            return
          }
          const params = {
            order: this.form,
            paymentProvisionList: this.displayMaterialItem2,
            attachList: this.approvalFileList.concat(this.orderFileList),
            detailList: this.tableData.map(item => ({
              ...item,
              deliveryDate: new Date(item.deliveryDate).getTime()
            }))
          }
          if (this.$attrs.params.flag === 'edit') {
            if (!this.canEdit) {
              return this.cancelBill()
            }
            this.$api.po.save(params).then(res => {
              this.$message({
                type: 'success',
                message: res.message
              })
              if (this.form.orderStatus === 'RETURNED') {
                this.$api.po.publish(params).then(data => {
                  this.$message({
                    type: 'success',
                    message: data.message
                  })
                  this.cancelBill()
                })
              } else {
                this.cancelBill()
              }
            })
          } else {
            this.$api.po.save(params).then(res => {
              this.$message({
                type: 'success',
                message: res.message
              })
              this.cancelBill()
            })
          }
      } else {
          return false
        }
      })
    },
    cancelBill () {
      if (this.$attrs.params.flag == 'edit') {
        this.$emit(
          'tab-remove',
          'vendorPurchaseOrderDetail' + this.$attrs.params.row.orderNumber
        )
      } else {
        this.$emit('tab-remove', 'vendorPurchaseOrderDetail')
      }
      this.__setTabTodo('vendorPurchaseOrderList_buyer.getQuerydata')
    },
    reset () {
      // 重置所有过滤条件
      for (let i in this.form) {
        this.form[i] = ''
      }
    },
    // 改变 currentNum
    changeCurrentIndex (currentNum) {
      this.viewIndex = currentNum
      this.queryContent()
    },
    // 改变 currentSize
    changeCurrentSize (currentSize) {
      this.viewSize = currentSize
      this.queryContent()
    },
    queryContent () {
      if (!this.queryForm.organizationId) {
        this.$message.warning(this.$t('orderMod.selOrganizationFirst'))
        return
      }
      const params = {
        level: this.queryForm.selectLevel,
        param: this.queryForm.inputLevel
      }
      this.$http({
        url: '/api-base/purchase/purchaseCategory/queryCategoryByType',
        method: 'GET',
        params: { ...params, enabled: 'Y' },
        loading: true
      }).then(res => {
        this.$http({
          url: '/api-base/material/materialItem/listMaterialByPurchaseCategory',
          method: 'POST',
          data: {
            'purchaseCategories': res.data,
            'materialCode': this.queryForm.materialCode,
            'materialName': this.queryForm.materialName,
            'organizationName': this.queryForm.organizationName,
            'pageSize': this.viewSize,
            'pageNum': this.viewIndex
          },
          laoding: true
        }).then(data => {
          if (data && data.data) {
            this.displayItemTable = data.data.list
            this.queryTotal = data.data.total
            this.dialogVisible = true
          }
        })
      })
    },
    openDialog (row) {
      if (!this.form.ceeaOrgName) {
        this.$message.warning(this.$t('orderMod.selBusinessEntityFirst'))
        return
      }
      this.filterForm.orgId = this.form.ceeaOrgId
      this.dialogVisible = true
    },
    addOneContent () {
      if (this.multipleSelection.length === 0) {
        return
      }
      this.multipleSelection.map(v => {
        this.tableData.push({
          ceeaOrganizationId: v.organizationId,
          ceeaOrganizationCode: v.organizationCode,
          ceeaOrganizationName: v.organizationName,
          categoryName: v.categoryName,
          categoryId: v.categoryId,
          currencyId: v.currencyId,
          currencyCode: v.currencyCode,
          currencyName: v.currencyName,
          materialId: v.materialId,
          materialCode: v.materialCode,
          materialName: v.materialName,
          unit: v.unit
        })
      })
      this.dialogVisible = false
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
      const xml = encodeURIComponent('database:database:采购订单打印.ureport.xml')
      const params = encodeURIComponent(`param=${this.form.orderNumber}`)
      const url = `${
        window.location.origin
      }/#/pdfPrint?xml=${xml}&params=${params}`
      window.open(url)
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
  .toRequired {
    color: #ff4949;
    padding-right: 2px;
  }
  .the_header {
    >span{padding-right:11px;}
    .el-button{float: right; margin-right: 11px;}
  }
  .btn_line{margin:0}
}
</style>
