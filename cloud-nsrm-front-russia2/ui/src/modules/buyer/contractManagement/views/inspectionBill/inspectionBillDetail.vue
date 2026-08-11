<template>
  <el-container
    class="flex-container the-inspectionBillDetail-detail"
    direction="vertical"
  >
    <el-main>
      <div class="form-container2">
        <el-form
          ref="acceptOrder"
          :model="acceptOrder"
          label-width="80px"
          label-position="top"
          class="form-fill-style"
          :rules="rules"
          :disabled="isReadOnly || isApprovalOnly"
        >
          <el-collapse
            v-model="activeDims"
            class="tab-form-style"
          >
            <el-collapse-item
              :title="$t('contractMod.acceptOrderInfo')"
              name="1"
            >
              <!-- 业务实体 -->
              <el-row :gutter="32">
                <el-col :span="6">
                  <el-form-item
                    :label="$t('contractMod.buId')"
                    :label-width="formLabelWidth"
                    prop="ceeaOrgId"
                  >
                    <!-- <el-select v-model="acceptOrder.ceeaOrgId"  filterable clearable @change="selectHandler">
                      <el-option
                        v-for="item in organizationList"
                        :key="item.organizationId"
                        :label="item.organizationName"
                        :value="item.organizationId"
                      />
                    </el-select> -->

                    <organization-selector
                      ref="organizationSelector"
                      v-model="acceptOrder.ceeaOrgId"
                      :parent-id="-1"
                      :placeholder="$t('common.pleaseSelect')"
                      node-type="OU"
                      @select="selectHandler"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    :label="$t('contractMod.vendorName')"
                    :label-width="formLabelWidth"
                    prop="vendorId"
                  >
                    <el-select
                      v-model="acceptOrder.vendorId"
                      filterable
                      clearable
                      @change="setVendorObj"
                    >
                      <el-option
                        v-for="item in vendorOptions"
                        :key="item.companyId"
                        :label="item.companyName"
                        :value="item.companyId"
                      />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 验收日期 -->
                  <el-form-item
                    :label="$t('contractMod.acceptDate1')"
                    :label-width="formLabelWidth"
                    prop="acceptDate"
                  >
                    <el-date-picker
                      v-model="acceptOrder.acceptDate"
                      type="date"
                      :format="$formatDatePicker"
                      value-format="yyyy-MM-dd"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    :label="$t('contractMod.acceptNumber')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="acceptOrder.acceptNumber"
                      disabled
                    />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row :gutter="32">
                <el-col :span="6">
                  <!-- 资产类别 -->
                  <el-form-item
                    :label="$t('oneStopShopping.assetClass')"
                    :label-width="formLabelWidth"
                    prop="ceeaAssetType"
                  >
                    <dict-select
                      v-model="acceptOrder.ceeaAssetType"
                      code="AQL_ASSET_TYPE"
                      clearable
                      @change="setAssertType"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 验收申请单号 -->
                  <el-form-item
                    :label="$t('contractMod.acceptApplicationNum')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="acceptOrder.ceeaApplictionCode"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 采购订单 -->
                  <el-form-item
                    :label="$t('route.buyerPurchaseOrder')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="acceptOrder.orderNum"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    :label="$t('contractMod.acceptStatus')"
                    :label-width="formLabelWidth"
                  >
                    <dict-select
                      v-model="acceptOrder.acceptStatus"
                      code="ACCEPTANCE_STATUS"
                      disabled
                    />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row :gutter="32">
                <el-col
                  v-if="acceptOrder.ceeaAssetType == 'SERVICE'"
                  :span="6"
                >
                  <!-- 服务区域 -->
                  <el-form-item
                    :label="$t('contractMod.serviceZone')"
                    :label-width="formLabelWidth"
                  >
                    <el-input v-model="acceptOrder.ceeaServiceZone" />
                  </el-form-item>
                </el-col>
                <el-col
                  v-if="acceptOrder.ceeaAssetType == 'SERVICE'"
                  :span="6"
                >
                  <!-- 服务日期从 -->
                  <el-form-item
                    :label="$t('contractMod.serviceDateStart')"
                    :label-width="formLabelWidth"
                  >
                    <el-date-picker
                      v-model="acceptOrder.ceeaServiceDateStart"
                      type="date"
                      :format="$formatDatePicker"
                      value-format="yyyy-MM-dd"
                    />
                  </el-form-item>
                </el-col>
                <el-col
                  v-if="acceptOrder.ceeaAssetType == 'SERVICE'"
                  :span="6"
                >
                  <!-- 服务日期至 -->
                  <el-form-item
                    :label="$t('contractMod.serviceDataEnd')"
                    :label-width="formLabelWidth"
                  >
                    <el-date-picker
                      v-model="acceptOrder.ceeaServiceDateEnd"
                      type="date"
                      :format="$formatDatePicker"
                      value-format="yyyy-MM-dd"
                    />
                  </el-form-item>
                </el-col>
                <el-col><p /></el-col>
              </el-row>
              <el-row :gutter="32">
                <el-col
                  v-if="acceptOrder.ceeaAssetType !== 'SERVICE'"
                  :span="6"
                >
                  <!-- 资产名称 -->
                  <el-form-item
                    :label="$t('contractMod.assetName')"
                    :label-width="formLabelWidth"
                    prop="ceeaAssetName"
                  >
                    <el-input v-model="acceptOrder.ceeaAssetName" />
                  </el-form-item>
                </el-col>
                <el-col
                  v-if="acceptOrder.ceeaAssetType !== 'SERVICE'"
                  :span="6"
                >
                  <!-- 到货日期 -->
                  <el-form-item
                    :label="$t('contractMod.deliveryDate1')"
                    :label-width="formLabelWidth"
                    prop="ceeaAcceptDate"
                  >
                    <el-date-picker
                      v-model="acceptOrder.ceeaAcceptDate"
                      type="date"
                      :format="$formatDatePicker"
                      value-format="yyyy-MM-dd"
                    />
                  </el-form-item>
                </el-col>
                <el-col
                  v-if="acceptOrder.ceeaAssetType !== 'SERVICE'"
                  :span="6"
                >
                  <!-- 规格型号 -->
                  <el-form-item
                    :label="$t('contractMod.specification')"
                    :label-width="formLabelWidth"
                    prop="ceeaSpecificationsModels"
                  >
                    <el-input v-model="acceptOrder.ceeaSpecificationsModels" />
                  </el-form-item>
                </el-col>
                <el-col
                  v-if="acceptOrder.ceeaAssetType !== 'SERVICE'"
                  :span="6"
                >
                  <!-- 存放地点 -->
                  <el-form-item
                    :label="$t('contractMod.storeSite')"
                    :label-width="formLabelWidth"
                    prop="ceeaStoreCode"
                  >
                    <el-input v-model="acceptOrder.ceeaStoreCode" />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row :gutter="32">
                <el-col
                  v-if="acceptOrder.ceeaAssetType !== 'SERVICE'"
                  :span="6"
                >
                  <!-- 资产数量 -->
                  <el-form-item
                    :label="$t('contractMod.assetQuantity')"
                    :label-width="formLabelWidth"
                    prop="ceeaAssetNumber"
                  >
                    <el-input
                      v-model="acceptOrder.ceeaAssetNumber"
                      v-input-format="{ type: 'number' }"
                    />
                  </el-form-item>
                </el-col>
                <el-col
                  v-if="acceptOrder.ceeaAssetType !== 'SERVICE'"
                  :span="6"
                >
                  <!-- 立项编号 -->
                  <el-form-item
                    :label="$t('contractMod.projectNum')"
                    :label-width="formLabelWidth"
                    prop="ceeaProjectNumber"
                  >
                    <el-input
                      v-model="acceptOrder.ceeaProjectNumber"
                      :placeholder="$t('contractMod.msgContractManage[18]')"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 使用部门ID -->
                  <el-form-item
                    :label="$t('contractMod.userDepartment')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="acceptOrder.ceeaUserDepartment"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 使用人账号 -->
                  <el-form-item
                    :label="$t('contractMod.userName')"
                    :label-width="formLabelWidth"
                    prop="ceeaUserName"
                  >
                    <quick-search
                      :show-input="acceptOrder.ceeaUserName"
                      show-key="username"
                      :scope-data="acceptOrder"
                      name="scc_rbac_user_display"
                      @close-quicksearch="getUserObj"
                    />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row :gutter="32">
                <el-col :span="6">
                  <!-- 随附技术文件 -->
                  <el-form-item
                    :label="$t('contractMod.followTechnicalDoc')"
                    :label-width="formLabelWidth"
                  >
                    <el-checkbox
                      v-model="acceptOrder.ceeaTechnicalDocuments"
                      true-label="1"
                      false-label="0"
                    />
                  </el-form-item>
                </el-col>
                <el-col
                  v-if="acceptOrder.ceeaAssetType !== 'SERVICE'"
                  :span="6"
                >
                  <!-- 附带工具设备 -->
                  <el-form-item
                    :label="$t('contractMod.withTools')"
                    :label-width="formLabelWidth"
                  >
                    <el-checkbox
                      v-model="acceptOrder.ceeaToolEquipment"
                      true-label="1"
                      false-label="0"
                    />
                  </el-form-item>
                </el-col>
                <el-col
                  v-if="acceptOrder.ceeaAssetType !== 'SERVICE'"
                  :span="6"
                >
                  <!-- 不动产大于500万 -->
                  <el-form-item
                    :label="$t('contractMod.realEstate')"
                    :label-width="formLabelWidth"
                  >
                    <el-checkbox
                      v-model="acceptOrder.ceeaAssetQualification"
                      true-label="1"
                      false-label="0"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 总数量 -->
                  <el-form-item
                    :label="$t('contractMod.totalAmount')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="acceptOrder.totalNum"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 总金额 -->
                  <el-form-item
                    :label="$t('purchaseDemand.totalAmount')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="acceptOrder.totalAmount"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 其他杂费 -->
                  <el-form-item
                    :label="$t('contractMod.otherExpense')"
                    :label-width="formLabelWidth"
                    prop="ceeaTotalQuantity"
                    :rules="{
                      required: itemisRequired ? true : false,
                      message: $t('common.pleaseInput')
                    }"
                  >
                    <el-input
                      v-model="acceptOrder.ceeaTotalQuantity"
                      v-input-format="{ type: 'float' }"
                      @change="setTotalValue"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <!-- 总价值 -->
                  <el-form-item
                    :label="$t('contractMod.totalValue')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="acceptOrder.totalValue"
                      disabled
                    />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row type="flex">
                <el-col :span="24">
                  <el-form-item
                    :label="$t('contractMod.remark')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="acceptOrder.remark"
                      type="textarea"
                      :autosize="{ minRows: 2, maxRows: 5 }"
                    />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row type="flex">
                <el-col :span="24">
                  <!-- 起草人意见 -->
                  <el-form-item
                    :label="$t('vendorMod.loggerComment')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="acceptOrder.ceeaDraftsmanOpinion"
                      type="textarea"
                      :autosize="{ minRows: 2, maxRows: 5 }"
                    />
                  </el-form-item>
                </el-col>
              </el-row>
            </el-collapse-item>
            <el-collapse-item
              :title="$t('contractMod.acceptDetail')"
              name="2"
            >
              <p class="btn_line">
                <el-button
                  type="primary"
                  class="detail-pbtn"
                  @click="openDialog"
                >
                  {{
                    $t('common.add')
                  }}
                </el-button>
              </p>
              <el-table
                :data="acceptDetails"
                style="width: 100%"
                border
                max-height="250px"
              >
                <el-table-column
                  align="center"
                  type="index"
                  :label="$t('contractMod.tabindex')"
                  width="60"
                />
                <!-- 采购订单号 -->
                <el-table-column
                  align="center"
                  prop="orderNumber"
                  :label="$t('purSettlementMod.orderNumber')"
                  width="150"
                  :show-overflow-tooltip="true"
                />
                <!-- 订单行号 -->
                <el-table-column
                  align="center"
                  prop="orderLineNumber"
                  :label="$t('orderMod.orderLineNum')"
                  width="80"
                  :show-overflow-tooltip="true"
                />
                <!-- 库存组织 -->
                <el-table-column
                  align="center"
                  prop="ceeaOrganizationName"
                  :label="$t('contractMod.invId')"
                  width="150"
                  :show-overflow-tooltip="true"
                />
                <!-- 物料小类 -->
                <el-table-column
                  align="center"
                  prop="categoryName"
                  :label="$t('contractMod.categoryName')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="materialCode"
                  :label="$t('contractMod.materialCode')"
                  width="150"
                  :show-overflow-tooltip="true"
                />
                <!-- 物料名称 -->
                <el-table-column
                  align="center"
                  prop="materialName"
                  :label="$t('bidMod.itemDesc')"
                  min-width="150"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="ceeaUnit"
                  :label="$t('contractMod.unit')"
                  width="60"
                  :show-overflow-tooltip="true"
                />
                <!-- 可验收数量 -->
                <el-table-column
                  align="center"
                  prop="waitAcceptQuantity"
                  :label="$t('contractMod.acceptableQuantity')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <!-- 本次验收数量 -->
                <el-table-column
                  align="center"
                  prop="acceptQuantity"
                  :label="$t('contractMod.acceptQuantity')"
                  width="100"
                  :show-overflow-tooltip="true"
                >
                  <template slot-scope="scope">
                    <el-input
                      v-model="scope.row.acceptQuantity"
                      v-input-format="{ type: 'number' }"
                      @change="setWaitQuality(scope.row)"
                    />
                  </template>
                </el-table-column>
                <!-- 缺陷数量 -->
                <el-table-column
                  align="center"
                  prop="ceeaDamageQuantity"
                  :label="$t('contractMod.damageQuantity')"
                  width="100"
                  :show-overflow-tooltip="true"
                >
                  <template slot-scope="scope">
                    <el-input
                      v-model="scope.row.ceeaDamageQuantity"
                      v-input-format="{ type: 'number' }"
                      @change="setWaitQuality(scope.row)"
                    />
                  </template>
                </el-table-column>
                <!-- 重量 -->
                <el-table-column
                  align="center"
                  prop="ceeaWeight"
                  :label="$t('common.weight')"
                  width="100"
                  :show-overflow-tooltip="true"
                >
                  <template slot-scope="scope">
                    <el-input
                      v-model="scope.row.ceeaWeight"
                      v-input-format="{ type: 'float' }"
                    />
                  </template>
                </el-table-column>
                <!-- 体积 -->
                <el-table-column
                  align="center"
                  prop="ceeaBulk"
                  :label="$t('contractMod.bulk')"
                  width="100"
                  :show-overflow-tooltip="true"
                >
                  <template slot-scope="scope">
                    <el-input
                      v-model="scope.row.ceeaBulk"
                      v-input-format="{ type: 'float' }"
                    />
                  </template>
                </el-table-column>
                <!-- 出厂日期 -->
                <el-table-column
                  align="center"
                  prop="ceeaProductionDate"
                  :label="$t('contractMod.productionDate')"
                  width="170"
                  :show-overflow-tooltip="true"
                >
                  <template slot-scope="scope">
                    <el-date-picker
                      v-model="scope.row.ceeaProductionDate"
                      type="date"
                      :format="$formatDatePicker"
                      value-format="yyyy-MM-dd"
                    />
                  </template>
                </el-table-column>
                <!-- 出厂编号 -->
                <el-table-column
                  align="center"
                  prop="ceeaProductionNum"
                  :label="$t('contractMod.productionNum')"
                  width="100"
                  :show-overflow-tooltip="true"
                >
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.ceeaProductionNum" />
                  </template>
                </el-table-column>
                <!-- 立项流水号 -->
                <el-table-column
                  align="center"
                  prop="ceeaProjectApprovalNum"
                  :label="$t('purchaseDemand.ceeaProjectApprovalNum')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <!-- 未税单价 -->
                <el-table-column
                  align="center"
                  prop="ceeaUntaxedPrice"
                  :label="$t('contractMod.untaxedPrice')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <!-- 订单数量 -->
                <el-table-column
                  align="center"
                  prop="orderNum"
                  :label="$t('orderMod.buyerOrderSynergy.orderNum')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <!-- 税率 -->
                <el-table-column
                  align="center"
                  prop="ceeaTaxRate"
                  :label="$t('contractMod.taxRate')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <!-- 含税单价 -->
                <el-table-column
                  align="center"
                  prop="ceeaUnitTaxPrice"
                  :label="$t('contractMod.taxedPrice')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <!-- 采购申请单号 -->
                <el-table-column
                  align="center"
                  prop="ceeaRequirementHeadNum"
                  :label="$t('purchaseDemand.purRequisitionNum')"
                  width="150"
                  :show-overflow-tooltip="true"
                />
                <!-- 申请行号 -->
                <el-table-column
                  align="center"
                  prop="ceeaRowNum"
                  :label="$t('purchaseDemand.rowNum')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <!-- 合同编号 -->
                <el-table-column
                  align="center"
                  prop="ceeaContractNo"
                  :label="$t('contractMod.contractNo')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <!-- 验收申请单号 -->
                <el-table-column
                  align="center"
                  prop="ceeaAcceptApplicationNum"
                  :label="$t('contractMod.acceptApplicationNum')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <!-- 操作 -->
                <el-table-column
                  :label="$t('common.operation')"
                  width="60"
                  fixed="right"
                >
                  <template slot-scope="scope">
                    <el-button
                      type="text"
                      @click="deleteOneContent(scope.$index, scope.row)"
                    >
                      {{
                        $t('common.delete')
                      }}
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
              <srm-dialog
                :title="$t('contractMod.addacceptDetail')"
                size="large"
                :visible.sync="dialogVisible"
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
                          :label="$t('purSettlementMod.orderNumber')"
                          :label-width="formLabelWidth"
                        >
                          <el-input v-model="filterForm.orderNumber" />
                        </el-form-item>
                      </el-col>
                      <el-col>
                        <!-- 业务实体 -->
                        <el-form-item
                          :label="$t('contractMod.buId')"
                          :label-width="formLabelWidth"
                        >
                          <organization-selector
                            ref="organizationSelectorFilter1"
                            v-model="filterForm.orgId"
                            :parent-id="-1"
                            node-type="OU"
                            :placeholder="$t('common.pleaseSelect')"
                            disabled
                            @select="selectHandler2"
                          />
                        </el-form-item>
                      </el-col>
                      <el-col>
                        <!-- 库存组织 -->
                        <el-form-item
                          :label="$t('contractMod.invId')"
                          :label-width="formLabelWidth"
                        >
                          <organization-selector
                            ref="organizationSelectorFilter2"
                            v-model="filterForm.organizationId"
                            :parent-id="filterForm.orgId"
                            node-type="INV"
                            :placeholder="$t('common.pleaseSelect')"
                            @select="selectHandler3"
                          />
                        </el-form-item>
                      </el-col>
                      <el-col>
                        <!-- 供应商 -->
                        <el-form-item
                          :label="$t('common.vendor')"
                          :label-width="formLabelWidth"
                        >
                          <el-input
                            v-model="filterForm.vendorName"
                            disabled
                          />
                        </el-form-item>
                      </el-col>
                    </el-row>
                    <el-row type="flex">
                      <el-col>
                        <!-- 物料 -->
                        <el-form-item
                          :label="$t('supRisk.material')"
                          :label-width="formLabelWidth"
                        >
                          <quick-search
                            :show-input="filterForm.materialName"
                            show-key="materialName"
                            :scope-data="filterForm"
                            name="scc_base_material_item_display"
                            @close-quicksearch="getItemObj"
                          />
                        </el-form-item>
                      </el-col>
                      <el-col>
                        <!-- 物料小类 -->
                        <el-form-item
                          :label="$t('contractMod.categoryName')"
                          :label-width="formLabelWidth"
                        >
                          <quick-search
                            :show-input="filterForm.categoryName"
                            show-key="categoryName"
                            :scope-data="filterForm"
                            name="scc_base_purchase_category2"
                            @close-quicksearch="getCategoryObj"
                          />
                        </el-form-item>
                      </el-col>
                      <el-col>
                        <!-- 订单日期从 -->
                        <el-form-item
                          :label="$t('orderMod.buyerOrderSynergy.orderDateFrom')"
                          :label-width="formLabelWidth"
                        >
                          <el-date-picker
                            v-model="filterForm.startTime"
                            type="date"
                            :format="$formatDatePicker"
                            value-format="yyyy-MM-dd"
                          />
                        </el-form-item>
                      </el-col>
                      <el-col>
                        <!-- 订单日期至 -->
                        <el-form-item
                          :label="$t('orderMod.buyerOrderSynergy.orderDateTo')"
                          :label-width="formLabelWidth"
                        >
                          <el-date-picker
                            v-model="filterForm.endTime"
                            type="date"
                            :format="$formatDatePicker"
                            value-format="yyyy-MM-dd"
                          />
                        </el-form-item>
                      </el-col>
                    </el-row>
                    <el-row type="flex">
                      <el-col>
                        <!-- 采购申请单号 -->
                        <el-form-item
                          :label="$t('purchaseDemand.purRequisitionNum')"
                          :label-width="formLabelWidth"
                        >
                          <el-input v-model="filterForm.ceeaRequirementHeadNum" />
                        </el-form-item>
                      </el-col>
                      <el-col>
                        <!-- 合同编号 -->
                        <el-form-item
                          :label="$t('contractMod.contractNo')"
                          :label-width="formLabelWidth"
                        >
                          <el-input v-model="filterForm.contractNo" />
                        </el-form-item>
                      </el-col>
                      <el-col>
                        <!-- 采购员 -->
                        <el-form-item
                          :label="$t('orderMod.buyerOrderSynergy.buyerName')"
                          :label-width="formLabelWidth"
                        >
                          <el-input v-model="filterForm.ceeaEmpUsername" />
                        </el-form-item>
                      </el-col>
                      <el-col>
                        <!-- 验收申请号 -->
                        <el-form-item
                          :label="$t('contractMod.acceptApplicationNum')"
                          :label-width="formLabelWidth"
                        >
                          <el-input v-model="filterForm.acceptApplicationNum" />
                        </el-form-item>
                      </el-col>
                    </el-row>
                  </el-form>
                  <p>
                    <el-button
                      type="primary"
                      @click="queryItemList"
                    >
                      {{
                        $t('common.search')
                      }}
                    </el-button>
                    <!-- <el-button @click="resetFilterForm">{{$t("common.reset") }}</el-button> -->
                    <el-button
                      type="primary"
                      @click="addOneContent"
                    >
                      {{
                        $t('common.confirm')
                      }}
                    </el-button>
                  </p>
                </div>
                <el-table
                  :data="displayItemTable"
                  style="width: 100%"
                  border
                  height="345px"
                  highlight-current-row
                  @selection-change="handleItemSelection"
                  @row-dblclick="handleItemDBClick"
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
                    fixed="left"
                  />
                  <!-- 采购订单号 -->
                  <el-table-column
                    align="center"
                    prop="orderNumber"
                    :label="$t('purSettlementMod.orderNumber')"
                    width="150"
                    :show-overflow-tooltip="true"
                  />
                  <!-- 订单行号 -->
                  <el-table-column
                    align="center"
                    prop="lineNum"
                    :label="$t('orderMod.orderLineNum')"
                    width="80"
                    :show-overflow-tooltip="true"
                  />
                  <!-- 业务实体 -->
                  <el-table-column
                    align="center"
                    prop="ceeaOrgName"
                    :label="$t('contractMod.buId')"
                    min-width="120"
                    :show-overflow-tooltip="true"
                  />
                  <!-- 库存组织 -->
                  <el-table-column
                    align="center"
                    prop="ceeaOrganizationName"
                    :label="$t('contractMod.invId')"
                    width="120"
                    :show-overflow-tooltip="true"
                  />
                  <!-- 采购员 -->
                  <el-table-column
                    align="center"
                    prop="ceeaEmpUsername"
                    :label="$t('orderMod.buyerOrderSynergy.buyerName')"
                    width="100"
                    :show-overflow-tooltip="true"
                  />
                  <!-- 物料小类 -->
                  <el-table-column
                    align="center"
                    prop="categoryName"
                    :label="$t('contractMod.categoryName')"
                    min-width="120"
                    :show-overflow-tooltip="true"
                  />
                  <el-table-column
                    align="center"
                    prop="materialCode"
                    :label="$t('contractMod.materialCode')"
                    width="150"
                    :show-overflow-tooltip="true"
                  />
                  <!-- 物料名称 -->
                  <el-table-column
                    align="center"
                    prop="materialName"
                    :label="$t('bidMod.itemDesc')"
                    min-width="150"
                    :show-overflow-tooltip="true"
                  />
                  <!-- 订单数量 -->
                  <el-table-column
                    align="center"
                    prop="orderNum"
                    :label="$t('orderMod.buyerOrderSynergy.orderNum')"
                    width="80"
                    :show-overflow-tooltip="true"
                  />
                  <!-- 接收数量 -->
                  <el-table-column
                    align="center"
                    prop="receivedQuantity"
                    :label="$t('orderMod.receiveNum')"
                    width="80"
                    :show-overflow-tooltip="true"
                  />
                  <!-- 已验收数量 -->
                  <el-table-column
                    align="center"
                    prop="receiveNum"
                    :label="$t('contractMod.receiveNum')"
                    width="100"
                    :show-overflow-tooltip="true"
                  />
                  <!-- 剩余可验收数量 -->
                  <el-table-column
                    align="center"
                    prop="waitAcceptQuantity"
                    :label="$t('contractMod.remainAcceptQuantity')"
                    width="120"
                    :show-overflow-tooltip="true"
                  />
                  <!-- 未税单价 -->
                  <el-table-column
                    align="center"
                    prop="ceeaUnitNoTaxPrice"
                    :label="$t('contractMod.untaxedPrice')"
                    width="80"
                    :show-overflow-tooltip="true"
                  />
                  <!-- 含税单价 -->
                  <el-table-column
                    align="center"
                    prop="ceeaUnitTaxPrice"
                    :label="$t('contractMod.taxedPrice')"
                    width="80"
                    :show-overflow-tooltip="true"
                  />
                  <!-- 税率 -->
                  <el-table-column
                    align="center"
                    prop="ceeaTaxRate"
                    :label="$t('contractMod.taxRate')"
                    width="80"
                    :show-overflow-tooltip="true"
                  />
                  <!-- 币种 -->
                  <el-table-column
                    align="center"
                    prop="currencyName"
                    :label="$t('contractMod.currencyCode')"
                    width="80"
                    :show-overflow-tooltip="true"
                  />
                  <!-- 采购申请单号 -->
                  <el-table-column
                    align="center"
                    prop="ceeaRequirementHeadNum"
                    :label="$t('purchaseDemand.purRequisitionNum')"
                    width="150"
                    :show-overflow-tooltip="true"
                  />
                  <!-- 申请行号 -->
                  <el-table-column
                    align="center"
                    prop="ceeaRowNum"
                    :label="$t('purchaseDemand.rowNum')"
                    width="80"
                    :show-overflow-tooltip="true"
                  />
                  <!-- 合同编号 -->
                  <el-table-column
                    align="center"
                    prop="ceeaContractNo"
                    :label="$t('contractMod.contractNo')"
                    min-width="150"
                    :show-overflow-tooltip="true"
                  />
                </el-table>
                <c-pagination
                  :total="parentOrgTableDataPage.total"
                  :page-num="parentOrgTableDataPage.pageNum"
                  :page-size="parentOrgTableDataPage.pageSize"
                  @current-change="parentDataCurrentChange"
                  @size-change="parentDataSizeChange"
                />
              </srm-dialog>
            </el-collapse-item>
            <!-- 随附技术文件  -->
            <el-collapse-item
              :title="$t('contractMod.followTechnicalDoc')"
              name="3"
            >
              <p class="btn_line">
                <el-button
                  type="primary"
                  :disabled="acceptOrder.ceeaTechnicalDocuments !== '1'"
                  class="detail-pbtn"
                  @click="addUploadOneFile"
                >
                  {{ $t('common.add') }}
                </el-button>
              </p>
              <el-table
                :data="techFile"
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
                  prop="fileSourceName"
                  :label="$t('contractMod.fileSourceName')"
                  width="250"
                >
                  <template slot-scope="scope">
                    <SrmCommonFile
                      :extra-data="fileInfo"
                      :default-file="{
                        fileId: scope.row.fileuploadId,
                        fileName: scope.row.fileSourceName
                      }"
                      :readonly="false"
                      @on-change="({file}) => outerHandleUploadSuccess(file,scope.row)"
                    />
                  </template>
                </el-table-column>
                <el-table-column
                  align="center"
                  prop="createdUserName"
                  :label="$t('components.fileupload.uploadUserName')"
                  width="150"
                />
                <el-table-column
                  align="center"
                  prop="creationDate"
                  :label="$t('components.fileupload.uploadDate')"
                  width="150"
                  :formatter="(row, column, cellValue) => $parseTime(cellValue)"
                />
                <el-table-column
                  align="center"
                  prop="comment"
                  :label="$t('contractMod.contractComments')"
                  min-width="150"
                >
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.comment" />
                  </template>
                </el-table-column>
                <el-table-column
                  :label="$t('common.operation')"
                  width="60"
                >
                  <template slot-scope="scope">
                    <el-button
                      type="text"
                      @click="handleDelFileClick(scope.$index, scope.row)"
                    >
                      {{
                        $t('common.delete')
                      }}
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-collapse-item>
            <!-- 附带工具设备 -->
            <el-collapse-item
              v-if="acceptOrder.ceeaAssetType !== 'SERVICE'"
              :title="$t('contractMod.withTools')"
              name="4"
            >
              <p class="btn_line">
                <el-button
                  type="primary"
                  :disabled="acceptOrder.ceeaToolEquipment !== '1'"
                  class="detail-pbtn"
                  @click="addOneEquipment"
                >
                  {{ $t('common.add') }}
                </el-button>
              </p>
              <el-table
                :data="toolEqp"
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
                  prop="toolEqpName"
                  :label="$t('contractMod.toolEqpName')"
                  min-width="150"
                >
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.toolEqpName" />
                  </template>
                </el-table-column>
                <el-table-column
                  align="center"
                  prop="eqpSpecification"
                  :label="$t('contractMod.eqpSpecification')"
                  min-width="150"
                >
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.eqpSpecification" />
                  </template>
                </el-table-column>
                <el-table-column
                  align="center"
                  prop="quantity"
                  :label="$t('contractMod.contractQuantity')"
                  min-width="150"
                >
                  <template slot-scope="scope">
                    <el-input
                      v-model="scope.row.quantity"
                      v-input-format="{ type: 'number' }"
                    />
                  </template>
                </el-table-column>
                <el-table-column
                  align="center"
                  prop="unit"
                  :label="$t('contractMod.unit')"
                  min-width="150"
                >
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.unit" />
                  </template>
                </el-table-column>
                <el-table-column
                  align="center"
                  prop="remark"
                  :label="$t('contractMod.remark')"
                  min-width="150"
                >
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.remark" />
                  </template>
                </el-table-column>
                <el-table-column
                  :label="$t('common.operation')"
                  width="60"
                >
                  <template slot-scope="scope">
                    <el-button
                      type="text"
                      @click="handleDelEqipmentClick(scope.$index, scope.row)"
                    >
                      {{ $t('common.delete') }}
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-collapse-item>
            <!-- 附件信息 -->
            <el-collapse-item
              :title="$t('bidMod.fileInfo')"
              name="5"
            >
              <p class="btn_line">
                <el-button
                  type="primary"
                  class="detail-pbtn"
                  @click="addUploadOneApproval"
                >
                  {{ $t('common.add') }}
                </el-button>
              </p>
              <el-table
                :data="assetFile"
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
                  prop="fileSourceName"
                  :label="$t('contractMod.fileSourceName')"
                  width="250"
                >
                  <template slot-scope="scope">
                    <SrmCommonFile
                      :extra-data="fileInfo"
                      :default-file="{
                        fileId: scope.row.fileuploadId,
                        fileName: scope.row.fileSourceName
                      }"
                      :readonly="false"
                      @on-change="({file}) => outerHandleUploadSuccess(file,scope.row)"
                    />
                  </template>
                </el-table-column>
                <el-table-column
                  align="center"
                  prop="createdUserName"
                  :label="$t('components.fileupload.uploadUserName')"
                  width="150"
                />
                <el-table-column
                  align="center"
                  prop="creationDate"
                  :label="$t('components.fileupload.uploadDate')"
                  width="150"
                  :formatter="(row, column, cellValue) => $parseTime(cellValue)"
                />
                <el-table-column
                  align="center"
                  prop="comment"
                  :label="$t('contractMod.remark')"
                  min-width="150"
                >
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.comment" />
                  </template>
                </el-table-column>
                <el-table-column
                  :label="$t('common.operation')"
                  width="60"
                >
                  <template slot-scope="scope">
                    <el-button
                      type="text"
                      @click="handleDelClick(scope.$index, scope.row)"
                    >
                      {{
                        $t('common.delete')
                      }}
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-collapse-item>
          </el-collapse>
        </el-form>
      </div>
      <!-- <c-toolbar v-if="curRole === 'BUYER'"> -->
      <c-toolbar>
        <template slot="right">
          <el-button
            @click="cancelBill"
          >
            {{ $t('common.cancel') }}
          </el-button>
          <el-button
            v-if="isReadOnly && acceptOrder.acceptStatus === 'APPROVED'"
            type="primary"
            @click="printBill"
          >
            {{ $t('route.pdfPrint') }}
          </el-button>
          <el-button
            v-if="curRole === 'BUYER' && !isReadOnly"
            type="primary"
            @click="saveBill('SAVE')"
          >
            {{ $t('common.staging') }}
          </el-button>
          <el-button
            v-if="curRole === 'BUYER' && !isReadOnly"
            type="primary"
            @click="saveBill('SUBMIT')"
          >
            {{ $t('common.submit') }}
          </el-button>
          <el-button
            v-if="curRole === 'BUYER' && isApprovalOnly"
            type="primary"
            @click="approvalBill"
          >
            {{ $t('common.toApprove') }}
          </el-button>
          <el-button
            v-if="curRole === 'BUYER' && isApprovalOnly"
            type="primary"
            @click="rejectBill"
          >
            {{ $t('common.toRefuse') }}
          </el-button>
        </template>
      </c-toolbar>
    </el-main>
  </el-container>
</template>
<script>
import CToolbar from 'lib@/components/c-toolbar'
import { tabTodoMixin } from '@/utils/mixins'
import QuickSearch from 'lib@/components/QuickSearch'
import MainHeader from 'lib@/components/Table/MainHeader'
import { parseTime } from '@/utils'
import OrganizationSelector from 'lib@/components/organization-selector'
import CPagination from 'lib@/components/c-pagination'
import DictSelect from '@/library/components/c-select/dict-select'

export default {
  name: 'InspectionBillDetail',
  components: {
    MainHeader,
    CToolbar,
    OrganizationSelector,
    CPagination,
    QuickSearch,
    DictSelect
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      curRole: this.$store.getters.userType,
      fileInfo: {
        fileModular: 'sup',
        fileFunction: 'vendorBiddingManagement',
        fileType: 'images'
      },
      filterForm: {
        orgId: null,
        orgCode: null,
        orgName: null,
        organizationId: null,
        organizationCode: null,
        organizationName: null,
        materialCode: null,
        materialName: null,
        ceeaEmpUsername: null,
        categoryCode: null,
        requirementHeadNum: null,
        startTime: null,
        endTime: null
      },
      parentOrgTableDataPage: {
        total: 0,
        pageNum: 1,
        pageSize: 20
      },
      parentOrgQueryForm: {
        pageNum: 1,
        pageSize: 10
      },
      vendorOptions: [],
      itemisRequired: false,
      acceptOrder: {
        ceeaUserDepartmentName: '',
        ceeaOrgId: null,
        ceeaOrgCode: null,
        ceeaOrgName: null,
        acceptOrderId: null,
        acceptNumber: '',
        acceptDate: parseTime(new Date(), '{y}-{m}-{d}', true),
        vendorName: '',
        vendorCode: '',
        vendorId: null,
        ceeaApplictionCode: '',
        acceptUserName: '',
        acceptStatus: 'DRAFT',
        remark: '',
        ceeaAssetName: '',
        ceeaAcceptDate: '',
        ceeaSpecificationsModels: '',
        ceeaStoreCode: '',
        ceeaAssetNumber: '',
        ceeaProjectNumber: '',
        ceeaUserDepartment: '',
        ceeaUserName: '',
        ceeaToolEquipment: '0',
        ceeaTechnicalDocuments: '0',
        ceeaAssetQualification: '0',
        totalNum: 0,
        totalAmount: 0,
        ceeaTotalQuantity: '',
        totalValue: '',
        ceeaDraftsmanOpinion: '',
        ceeaAssetType: '',
        orderNum: ''
      },
      globalCeeaAssetType: null,
      acceptDetails: [],
      toolEqp: [],
      techFile: [],
      assetFile: [],
      activeDims: ['1', '2', '3', '4', '5', '6'],
      rules: {
        ceeaOrgId: [{ required: true, message: this.$t('purchaseDemand.orgIdTips') }], // 请选择业务实体
        vendorId: [{ required: true, message: this.$t('vendorMod.msgVendor') }], // 请选择供应商
        ceeaAssetType: [
          {
            required: true,
            message: this.$t('contractMod.msgContractManage[19]')
          }
        ], // 请选择资产类别
        contractNo: [
          {
            required: true,
            message: this.$t('contractMod.msgContractManage[20]')
          }
        ], // 请输入合同编号
        acceptDate: [
          {
            required: true,
            message: this.$t('contractMod.msgContractManage[21]')
          }
        ], // 请输入验收日期
        acceptUserName: [
          {
            required: true,
            message: this.$t('purchaseDemand.acceptUserNameTips')
          }
        ], // 请输入验收人
        ceeaUserName: [
          {
            required: true,
            message: this.$t('contractMod.msgContractManage[22]')
          }
        ], // 请选择使用人账号
        ceeaAssetName: [{ required: true, message: this.$t('common.pleaseInput') }], // 请输入
        ceeaAcceptDate: [{ required: true, message: this.$t('common.pleaseInput') }],
        ceeaSpecificationsModels: [{ required: true, message: this.$t('common.pleaseInput') }],
        ceeaStoreCode: [{ required: true, message: this.$t('common.pleaseInput') }],
        ceeaAssetNumber: [{ required: true, message: this.$t('common.pleaseInput') }],
        ceeaProjectNumber: [{ required: true, message: this.$t('common.pleaseInput') }]
      },
      isDisabled: this.$attrs.params.flag == 'edit',
      isReadOnly: this.$attrs.params.flag == 'readOnly',
      isApprovalOnly: this.$attrs.params.flag == 'approvalOnly',
      formLabelWidth: '120px',
      isModify: false,
      dialogVisible: false,
      departmentList: [],
      displayItemTable: [],
      multipleSelection: [],
      organizationList: [],
      IFcompanyId: false
    }
  },
  created () {
    if (this.$attrs.params.flag == 'add') {
      // this.getorg();
    } else if (this.$attrs.params.flag == 'edit') {
      this.getFormDetail(this.$attrs.params.row.acceptOrderId)
    } else if (this.$attrs.params.flag == 'approvalOnly' || this.$attrs.params.flag == 'readOnly') {
      this.getFormDetail(this.$attrs.params.row.acceptOrderId)
    }
  },
  methods: {
    cancelBill () {
      const { flag, row } = this.$attrs.params
      if (flag === 'add') {
        this.$emit('tab-remove', 'inspectionBillDetail')
      } else {
        this.$emit('tab-remove', 'inspectionBillDetail' + row.acceptNumber)
      }
      this.__setTabTodo('inspectionBillList.getQuerydata')
    },
    getDept (ceeaUserDepartment) {
      this.$http({
        url: '/api-base/quicksearch/quicksearchConfig/listByFormCondition',
        method: 'POST',
        data: {
          pageNum: 1,
          pageSize: 10,
          params:
            '{"query":"{\\"t.DEPTID\\":\\"' +
            ceeaUserDepartment +
            '\\"}","extendQuery":"{\\"_quickKey\\":\\"ceea_base_dept\\",\\"entityId\\":10}"}'
        },
        loading: true
      }).then((res) => {
        this.acceptOrder.ceeaUserDepartmentName = res.data.data[0].descr
        console.log('res', res)
      })
    },

    getFormDetail (acceptOrderId) {
      this.$http({
        url: '/api-cm/accept/acceptOrder/getAcceptDTO',
        method: 'GET',
        params: { acceptOrderId: acceptOrderId },
        loading: true
      })
        .then((data) => {
          if (data.data) {
            this.acceptOrder = data.data.acceptOrder
            this.toolEqp = data.data.toolEqp
            this.techFile = data.data.techFile
            this.assetFile = data.data.assetFile
            this.acceptDetails = data.data.acceptDetails
            this.getCompanyList(data.data.acceptOrder.ceeaOrgId)
            // this.getorg(data.data.acceptOrder.ceeaOrgId)
            this.globalCeeaAssetType = this.acceptOrder.ceeaAssetType
            this.acceptOrder.ceeaTechnicalDocuments = String(
              this.acceptOrder.ceeaTechnicalDocuments
            )
            this.acceptOrder.ceeaToolEquipment = String(this.acceptOrder.ceeaToolEquipment)
            this.acceptOrder.ceeaAssetQualification = String(
              this.acceptOrder.ceeaAssetQualification
            )
            this.acceptOrder.totalNum = 0
            this.acceptOrder.totalAmount = 0
            this.acceptDetails.map((v) => {
              this.acceptOrder.totalNum += v.acceptQuantity || 0
              this.acceptOrder.totalAmount += v.acceptQuantity * v.ceeaUnitTaxPrice || 0
            })
            this.setTotalValue() //
          }
        })
        .catch((err) => {
          console.log(err)
        })
    },
    getorg (val) {
      this.$http({
        url: '/api-base/organization/organization/listAllOrganization',
        method: 'POST',
        data: {
          organizationTypeCode: 'OU',
          pageNum: 1,
          pageSize: 9999
        }
      }).then((data) => {
        if (data && data.data) {
          this.organizationList = data.data.list
          if (val) {
            this.selectHandler(val)
          }
        }
      })
    },
    deleteOneContent (index, row) {
      this.acceptDetails.splice(index, 1)
    },
    selectHandler (node, value, scope) {
      this.acceptOrder.ceeaOrgId = node ? node.organizationId : null
      this.acceptOrder.ceeaOrgCode = node ? node.organizationCode : null
      this.acceptOrder.ceeaOrgName = node ? node.organizationName : null
      // debugger
      if (node) {
        this.getCompanyList(node.organizationId)
      }
    },
    /* selectHandler(val) {
        this.organizationList.forEach(element => {
        if(element.organizationId===val){
              this.acceptOrder.ceeaOrgId = element.organizationId;
              this.acceptOrder.ceeaOrgCode = element.organizationCode;
              this.acceptOrder.ceeaOrgName =element.organizationName;
              this.getCompanyList(element.organizationId);
          return;
        }
        });
    }, */
    getCompanyList (organizationId) {
      this.$http({
        url: '/api-sup/info/companyInfo/listPageByOrgId',
        method: 'POST',
        data: {
          orgId: organizationId,
          pageNum: 1,
          pageSize: 9999
        },
        loading: true
      })
        .then((data) => {
          if (data && data.data) {
            this.vendorOptions = data.data.list
          }
        })
        .catch((err) => {
          console.log(err)
        })
    },
    selectHandler2 (node, value, scope) {
      this.filterForm.orgId = node ? node.organizationId : null
      this.filterForm.orgCode = node ? node.organizationCode : null
      this.filterForm.orgName = node ? node.organizationName : null
    },
    selectHandler3 (node, value, scope) {
      this.filterForm.organizationId = node.organizationId
      this.filterForm.organizationCode = node.organizationCode
      this.filterForm.organizationName = node.organizationName
    },
    getVendorObj (val, scope) {
      scope.vendorId = val ? val.companyId : ''
      scope.vendorCode = val ? val.companyCode : ''
      scope.vendorName = val ? val.companyName : ''
    },
    getItemObj (val, scope) {
      scope.materialId = val ? val.materialId : ''
      scope.materialCode = val ? val.materialCode : ''
      scope.materialName = val ? val.materialName : ''
    },
    setTotalValue () {
      setTimeout(() => {
        this.acceptOrder.totalValue =
          Number(this.acceptOrder.totalAmount) + Number(this.acceptOrder.ceeaTotalQuantity)
      }, 500)
    },
    setWaitQuality (row) {
      // 本次验收数量---acceptQuantity;【ceeaDamageQuantity】="缺陷数量"
      if (
        Number(row.acceptQuantity) + Number(row.ceeaDamageQuantity) >
        Number(row.waitAcceptQuantity)
      ) {
        // "[本次验收数量]与[缺陷数量]之和不能大于[可验收数量]"
        this.$message.error(this.$t('contractMod.msgContractManage[23]'))
        return
      }
      if (row.acceptQuantity <= 0) {
        // "验收数量必须为正数!"
        return this.$message.error(this.$t('contractMod.msgContractManage[24]'))
      }
      // 总金额=每一行汇总【含税单据*验收数量】
      setTimeout(() => {
        this.acceptOrder.totalNum = null
        this.acceptOrder.totalAmount = null
        for (let item of this.acceptDetails) {
          this.acceptOrder.totalNum += item.acceptQuantity || 0
          this.acceptOrder.totalAmount += item.ceeaUnitTaxPrice * item.acceptQuantity || 0
        }
      }, 100)
    },
    handleItemSelection (val) {
      this.multipleSelection = val
    },
    handleItemDBClick (val) {
      this.multipleSelection = [val]
      this.addOneContent()
    },
    backBill () {
      if (this.$attrs.params.flag == 'edit') {
        this.$emit('tab-remove', 'inspectionBillDetail' + this.$attrs.params.row.acceptNumber)
      } else {
        this.$emit('tab-remove', 'inspectionBillDetail')
      }
      this.__setTabTodo('inspectionBillList.getQuerydata')
    },
    // 打印
    printBill () {
      const xml = encodeURIComponent('database:database:验收单.ureport.xml')
      const params = encodeURIComponent(`orderNumber=${this.$attrs.params.row.acceptOrderId}`)

      const url = `${this.$systemUrl}/#/pdfPrint?xml=${xml}&params=${params}`
      window.open(url, '_blank', 'noopener,noreferrer')
    },
    // http://10.0.10.48/api-base/ureport/preview?_u=database:database:验收单.ureport.xml&orderId=8003984525099008&bgText=LONGI
    outerHandleUploadSuccess (file, row) {
      const { fileId = '', fileName = '' } = file || {}
      row.fileuploadId = fileId.toString()
      row.fileSourceName = fileName
    },
    setAssertType (val) {
      this.itemisRequired = !!['EQ', 'IT', 'LG', 'BG', 'FW', 'DZ'].includes(val)
      const formerAssertType = this.globalCeeaAssetType
      if (val === 'SERVICE') {
        this.$confirm(this.$t('contractMod.msgContractManage[25]'), {
          confirmButtonText: this.$t('common.confirm'),
          cancelButtonText: this.$t('common.cancel'),
          type: 'warning'
        })
          .then(() => {
            this.acceptDetails = []
          })
          .catch(() => {
            this.acceptOrder.ceeaAssetType = formerAssertType
            this.globalCeeaAssetType = formerAssertType
          })
      }
      setTimeout(() => {
        this.globalCeeaAssetType = this.acceptOrder.ceeaAssetType
      }, 100)
    },
    openDialog () {
      this.filterForm.orgId = this.acceptOrder.ceeaOrgId
      this.filterForm.vendorId = this.acceptOrder.vendorId
      this.filterForm.vendorName = this.acceptOrder.vendorName
      this.queryItemList()
    },
    parentDataCurrentChange (num) {
      this.parentOrgQueryForm.pageNum = num
      this.queryItemList()
    },
    parentDataSizeChange (size) {
      this.parentOrgQueryForm.pageSize = size
      this.queryItemList()
    },
    queryItemList () {
      let url = '/api-sup-ce/order/orderDetail/OrderDetailListPage'
      if (this.acceptOrder.ceeaAssetType == 'SERVICE') {
        url = '/api-sup-ce/order/orderDetail/OrderDetailListPageCopy'
      }
      const data = { ...this.parentOrgQueryForm, ...this.filterForm }
      this.$http({
        url: url,
        method: 'POST',
        data: data,
        loading: true
      }).then((res) => {
        this.displayItemTable = res.data.list
        this.parentOrgTableDataPage.total = res.data.total
        this.dialogVisible = true
      })
    },
    getCategoryObj (val, scope) {
      scope.categoryId = val ? val.categoryId : ''
      scope.categoryCode = val ? val.categoryCode : ''
      scope.categoryName = val ? val.categoryName : ''
    },
    resetFilterForm () {
      for (let i in this.filterForm) {
        this.filterForm[i] = ''
      }
    },
    addOneContent () {
      if (this.multipleSelection.length === 0) {
        return
      }
      // 不允许添加重复的【orderDetailId】
      let orderDetailIdArr = this.acceptDetails.map((v) => v.orderDetailId)
      this.multipleSelection.map((v) => {
        if (!orderDetailIdArr.includes(v.orderDetailId)) {
          this.acceptDetails.push({
            orderDetailId: v.orderDetailId,
            orderNumber: v.orderNumber,
            orderLineNumber: v.lineNum,
            ceeaOrgName: v.ceeaOrgName,
            ceeaOrganizationName: v.ceeaOrganizationName,
            categoryId: v.categoryId,
            categoryCode: v.categoryCode,
            categoryName: v.categoryName,
            materialCode: v.materialCode,
            materialName: v.materialName,
            ceeaUnit: v.unit,
            orderNum: v.orderNum,
            ceeaDamageQuantity: v.ceeaDamageQuantity,
            waitAcceptQuantity: v.waitAcceptQuantity,
            ceeaWeight: v.weight,
            ceeaBulk: v.bulk,
            ceeaProductionDate: v.productionDate,
            ceeaProductionNum: v.productionNum,
            ceeaProjectApprovalNum: v.projectApprovalNum,
            ceeaUnitNoTaxPrice: v.ceeaUnitNoTaxPrice,
            ceeaUnitTaxPrice: v.ceeaUnitTaxPrice,
            ceeaTaxRate: v.ceeaTaxRate,
            ceeaRequirementHeadNum: v.requirementHeadNum,
            ceeaRowNum: v.rownum,
            ceeaContractNo: v.contractNo,
            ceeaAcceptApplicationNum: v.acceptApplicationNum,
            warehouseReceiptQuantity: v.acceptQuantity
          })
          // 总金额=每一行汇总【含税单据*验收数量】
          this.acceptOrder.totalNum += v.acceptQuantity || 0
          this.acceptOrder.totalAmount += v.acceptQuantity * v.ceeaUnitTaxPrice || 0
        }
      })
      this.dialogVisible = false
    },

    readOneContent () {},
    copyOneContent () {},
    approvalBill () {
      this.$http({
        url: '/api-cm/accept/acceptOrder/vendorPass',
        method: 'POST',
        data: {
          acceptOrderId: this.acceptOrder.acceptOrderId
        },
        loading: true
      })
        .then((data) => {
          this.$message({
            message: this.$t('common.success'),
            type: 'success'
          })
          this.$emit('tab-remove', 'inspectionBillDetail' + this.$attrs.params.row.acceptNumber)
          this.__setTabTodo('inspectionBillList.getQuerydata')
        })
        .catch((err) => {
          console.log(err)
        })
    },
    addUploadOneApproval () {
      this.assetFile.push({
        fileuploadId: null,
        fileSourceName: '',
        fileFunction: 'acceptASSET' // 附件
      })
    },
    addUploadOneFile () {
      this.techFile.push({
        fileuploadId: null,
        fileSourceName: '',
        fileFunction: 'acceptTECH' // 技术附件
      })
    },
    setVendorObj (val) {
      let obj = this.vendorOptions.filter((v) => v.companyId === val)
      if (obj && obj[0]) {
        this.acceptOrder.vendorCode = obj[0].companyCode
        this.acceptOrder.vendorName = obj[0].companyName
        this.acceptOrder.erpVendorCode = obj[0].erpVendorCode
      }
    },
    addOneEquipment () {
      this.toolEqp.push({
        toolEqpId: null,
        toolEqpName: '',
        eqpSpecification: '',
        quantity: '',
        unit: '',
        remark: ''
      })
    },
    getUserObj (val, scope) {
      scope.ceeaUserName = val ? val.username : ''
      scope.ceeaUserDepartment = val ? val.department : ''
      scope.ceeaUserEmpNo = val ? val.ceeaEmpNo : ''
      scope.ceeaUserId = val ? val.userId : ''
    },
    handleDelEqipmentClick (index, row) {
      this.toolEqp.splice(index, 1)
    },
    handleDelFileClick (index, row) {
      this.assetFile.splice(index, 1)
    },
    handleDelClick (index, row) {
      this.assetFile.splice(index, 1)
    },
    rejectBill () {
      this.$prompt(this.$t('bidMod.msgRejectReason'), this.$t('contractMod.rejectReason'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel')
      })
        .then(({ value }) => {
          this.$http({
            url: '/api-cm/accept/acceptOrder/vendorReject',
            method: 'POST',
            data: {
              acceptOrderId: this.acceptOrder.acceptOrderId,
              rejectReason: value
            },
            loading: true
          })
            .then((data) => {
              this.$message({
                message: this.$t('common.success'),
                type: 'success'
              })
              this.$emit('tab-remove', 'inspectionBillDetail' + this.$attrs.params.row.acceptNumber)
              this.__setTabTodo('inspectionBillList.getQuerydata')
            })
            .catch((err) => {
              console.log(err)
            })
        })
        .catch(() => {})
    },
    saveBill (type) {
      this.$refs.acceptOrder.validate((valid) => {
        if (valid) {
          // -----
          if (this.acceptDetails.length === 0) {
            this.$message.error(this.$t('contractMod.msgContractManage[17]')) // 请输入验收明细!
            return
          }
          for (let row of this.acceptDetails) {
            if (!row.acceptQuantity) {
              this.$message.error(this.$t('contractMod.msgContractManage[26]')) // 请输入本次验收数量!
              return
            }
            if (
              Number(row.acceptQuantity) + Number(row.ceeaDamageQuantity) >
              Number(row.waitAcceptQuantity)
            ) {
              this.$message.error(this.$t('contractMod.msgContractManage[23]'))
              return
            }
          }
          let allparam = {
            acceptOrder: this.acceptOrder,
            acceptDetails: this.acceptDetails,
            techFile: this.techFile,
            assetFile: this.assetFile,
            toolEqp: this.toolEqp
          }
          let saveUrl = '/api-cm/accept/acceptOrder/buyerSaveTemporary'
          if (type === 'SUBMIT') saveUrl = '/api-cm/accept/acceptOrder/buyerSubmit'
          this.$http({
            url: saveUrl,
            method: 'POST',
            data: allparam,
            loading: true
          })
            .then((data) => {
              this.$message({
                message: this.$t('common.successSave'), // 保存成功
                type: 'success'
              })
              if (type === 'SUBMIT') {
                if (this.$attrs.params.flag == 'edit') {
                  this.$emit(
                    'tab-remove',
                    'inspectionBillDetail' + this.$attrs.params.row.acceptNumber
                  )
                } else {
                  this.$emit('tab-remove', 'inspectionBillDetail')
                }
                this.__setTabTodo('inspectionBillList.getQuerydata')
              } else {
                this.getFormDetail(data.data)
              }
            })
            .catch((err) => {
              console.log(err)
            })
        } else {
          return false
        }
      })
    }
  }
}
</script>
<style scoped lang="scss">
.the-inspectionBillDetail-detail {
  .form-container2 {
    padding: 5px;
  }
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
}
</style>
