<template>
  <el-container
    class="flex-container the-supInspectionBillDetail-detail"
    direction="vertical"
  >
    <el-main>
      <div class="form-container2">
        <el-form
          ref="acceptOrder"
          :model="acceptOrder"
          label-width="80px"
          label-position="top"
          class="form-incontainer"
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
              <el-row type="flex">
                <el-col>
                  <!-- 业务实体 -->
                  <el-form-item
                    :label="$t('contractMod.buId')"
                    :label-width="formLabelWidth"
                    prop="ceeaOrgId"
                  >
                    <organization-selector
                      ref="organizationSelector"
                      v-model="acceptOrder.ceeaOrgId"
                      :parent-id="-1"
                      node-type="OU"
                      :limit="false"
                      @select="selectHandler"
                    />
                  </el-form-item>
                </el-col>
                <el-col>
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
                <el-col>
                  <el-form-item
                    :label="$t('contractMod.vendorName')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="acceptOrder.vendorName"
                      disabled
                    />
                  </el-form-item>
                </el-col>
                <el-col>
                  <el-form-item
                    :label="$t('contractMod.applicationStatus')"
                    :label-width="formLabelWidth"
                  >
                    <dict-select
                      v-model="acceptOrder.acceptStatus"
                      code="YSSQ_STATUS"
                      disabled
                    />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row type="flex">
                <el-col>
                  <el-form-item
                    :label="$t('purchaseDemand.applyDate')"
                    :label-width="formLabelWidth"
                  >
                    <el-date-picker
                      v-model="acceptOrder.ceeaApplicationDate"
                      type="date"
                      format="yyyy-MM-dd"
                      value-format="yyyy-MM-dd"
                    />
                  </el-form-item>
                </el-col>
                <el-col>
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
                <el-col>
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
                <el-col><p /></el-col>
              </el-row>
              <el-row type="flex">
                <el-col>
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
                <el-col>
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
                <el-col>
                  <!-- 其他杂费 -->
                  <el-form-item
                    :label="$t('contractMod.otherExpense')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="acceptOrder.ceeaTotalQuantity"
                      v-input-format="{ type: 'float' }"
                      @change="setTotalValue"
                    />
                  </el-form-item>
                </el-col>
                <el-col>
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
                <el-col>
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
            </el-collapse-item>
            <el-collapse-item
              :title="$t('contractMod.acceptDetail')"
              name="2"
            >
              <p style="margin: 0">
                <el-button
                  type="primary"
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
                <!-- 申请验收数量 -->
                <el-table-column
                  align="center"
                  prop="waitAcceptQuantity"
                  :label="$t('contractMod.appAcceptQuantity')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
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
                      format="yyyy-MM-dd"
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
                  width="80"
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
                <!-- 税率 -->
                <el-table-column
                  align="center"
                  prop="ceeaTaxRate"
                  :label="$t('contractMod.taxRate')"
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
                  width="150"
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
                        <!-- 采购订单号 -->
                        <el-form-item
                          :label="$t('purSettlementMod.orderNumber')"
                          :label-width="formLabelWidth"
                        >
                          <el-input v-model="filterForm.orderNumber" />
                        </el-form-item>
                      </el-col>
                      <el-col>
                        <!-- 库存组织 -->
                        <el-form-item
                          :label="$t('contractMod.invId')"
                          :label-width="formLabelWidth"
                        >
                          <organization-selector
                            ref="organizationSelectorFilter11"
                            v-model="filterForm.organizationId"
                            :parent-id="acceptOrder.ceeaOrgId"
                            node-type="INV"
                            @select="selectHandler3"
                          />
                        </el-form-item>
                      </el-col>
                      <el-col>
                        <!-- 物料编码 -->
                        <el-form-item
                          :label="$t('common.materialCode')"
                          :label-width="formLabelWidth"
                        >
                          <quick-search
                            :show-input="filterForm.materialCode"
                            show-key="materialCode"
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
                    </el-row>
                    <el-row type="flex">
                      <el-col>
                        <!-- 订单日期从 -->
                        <el-form-item
                          :label="$t('orderMod.buyerOrderSynergy.orderDateFrom')"
                          :label-width="formLabelWidth"
                        >
                          <el-date-picker
                            v-model="filterForm.startTime"
                            type="date"
                            format="yyyy-MM-dd"
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
                            format="yyyy-MM-dd"
                            value-format="yyyy-MM-dd"
                          />
                        </el-form-item>
                      </el-col>
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
                          <el-input v-model="filterForm.ceeaContractNo" />
                        </el-form-item>
                      </el-col>
                    </el-row>
                    <el-row type="flex">
                      <el-col>
                        <!-- 采购员 -->
                        <el-form-item
                          :label="$t('orderMod.buyerOrderSynergy.buyerName')"
                          :label-width="formLabelWidth"
                        >
                          <el-input v-model="filterForm.ceeaEmpUsername" />
                        </el-form-item>
                      </el-col>
                      <el-col><p /></el-col>
                      <el-col><p /></el-col>
                      <el-col>
                        <p style="margin-top: 25px">
                          <el-button
                            type="primary"
                            @click="queryItemList"
                          >
                            {{
                              $t('common.search')
                            }}
                          </el-button>
                          <!--<el-button @click="resetFilterForm">{{$t("common.reset") }}</el-button>-->
                          <el-button
                            type="primary"
                            @click="addOneContent"
                          >
                            {{
                              $t('common.confirm')
                            }}
                          </el-button>
                        </p>
                      </el-col>
                    </el-row>
                  </el-form>
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
                    width="120"
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
                    :label="$t('bidMod.quotePurchasor')"
                    width="100"
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
                    width="120"
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
            <!-- 随附技术文件 -->
            <el-collapse-item
              :title="$t('contractMod.followTechnicalDoc')"
              name="3"
            >
              <p class="btn_line">
                <el-button
                  type="primary"
                  :disabled="acceptOrder.ceeaTechnicalDocuments !== '1'"
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
              :title="$t('contractMod.withTools')"
              name="4"
            >
              <p class="btn_line">
                <el-button
                  type="primary"
                  :disabled="acceptOrder.ceeaToolEquipment !== '1'"
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
                  :disabled="acceptOrder.ceeaAssetQualification !== '1'"
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
                />
                <el-table-column
                  align="center"
                  prop="comments"
                  :label="$t('contractMod.remark')"
                  min-width="150"
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
      <c-toolbar>
        <template slot="right">
          <el-button
            v-if="!isReadOnly"
            type="primary"
            @click="saveBill('SAVE')"
          >
            {{
              $t('common.submit')
            }}
          </el-button>
          <el-button
            v-if="isApprovalOnly"
            type="primary"
            @click="approvalBill"
          >
            {{
              $t('common.toApprove')
            }}
          </el-button>
          <el-button
            v-if="isApprovalOnly"
            type="primary"
            @click="rejectBill"
          >
            {{
              $t('common.toRefuse')
            }}
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
  name: 'SupInspectionBillDetail',
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
        categoryCode: null,
        categoryName: null,
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
      acceptOrder: {
        ceeaOrgId: null,
        ceeaOrgCode: null,
        ceeaOrgName: null,
        acceptOrderId: null,
        acceptNumber: '',
        ceeaAcceptApplicationNum: '',
        ceeaApplicationDate: parseTime(new Date(), '{y}-{m}-{d}'),
        vendorName: '',
        vendorCode: '',
        vendorId: null,
        ceeaApplictionCode: '',
        acceptStatus: 'APPLY_DRAFT',
        remark: '',
        ceeaAssetName: '',
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
        totalValue: 0,
        ceeaTotalQuantity: '',
        ceeaDraftsmanOpinion: '',
        ceeaAssetType: '',
        orderNum: ''
      },
      acceptDetails: [],
      toolEqp: [],
      techFile: [],
      assetFile: [],
      activeDims: ['1', '2', '3', '4', '5', '6'],
      rules: {
        ceeaOrgId: [{ required: true, message: this.$t('purchaseDemand.orgIdTips') }], // 请选择业务实体
        ceeaApplicationDate: [
          {
            required: true,
            message: this.$t('contractMod.msgContractManage[16]')
          }
        ] // 请选择申请日期
      },
      isDisabled: this.$attrs.params.flag == 'edit',
      isReadOnly: this.$attrs.params.flag == 'readOnly',
      isApprovalOnly: this.$attrs.params.flag == 'approvalOnly',
      formLabelWidth: '120px',
      isModify: false,
      dialogVisible: false,
      displayItemTable: [],
      multipleSelection: []
    }
  },
  created () {
    if (this.$attrs.params.flag == 'add') {
      // 默认加载采购商联系方式，如果没有才需要填写
      if (this.$store.state.user && this.$store.state.user.userInfo) {
        this.acceptOrder.vendorId = this.$store.state.user.userInfo.companyId
        this.acceptOrder.vendorCode = this.$store.state.user.userInfo.companyCode
        this.acceptOrder.vendorName = this.$store.state.user.userInfo.companyName
      }
    } else if (this.$attrs.params.flag == 'edit') {
      this.getFormDetail(this.$attrs.params.row.acceptOrderId)
    } else if (this.$attrs.params.flag == 'approvalOnly' || this.$attrs.params.flag == 'readOnly') {
      this.getFormDetail(this.$attrs.params.row.acceptOrderId)
    }
  },
  methods: {
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
              this.acceptOrder.totalNum += v.waitAcceptQuantity || 0
              this.acceptOrder.totalAmount += v.waitAcceptQuantity * v.ceeaUnitTaxPrice || 0
            })
            this.setTotalValue()
          }
        })
        .catch((err) => {
          console.log(err)
        })
    },
    deleteOneContent (index, row) {
      this.acceptDetails.splice(index, 1)
    },
    selectHandler (node, value, scope) {
      this.acceptOrder.ceeaOrgId = node ? node.organizationId : null
      this.acceptOrder.ceeaOrgCode = node ? node.organizationCode : null
      this.acceptOrder.ceeaOrgName = node ? node.organizationName : null
    },
    selectHandler3 (node, value, scope) {
      this.filterForm.organizationId = node ? node.organizationId : null
      this.filterForm.organizationCode = node ? node.organizationCode : null
      this.filterForm.organizationName = node ? node.organizationName : null
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
    handleItemSelection (val) {
      this.multipleSelection = val
    },
    handleItemDBClick (val) {
      this.multipleSelection = [val]
      this.addOneContent()
    },
    backBill () {
      if (this.$attrs.params.flag == 'edit') {
        this.$emit(
          'tab-remove',
          'supInspectionBillDetail' + this.$attrs.params.row.ceeaAcceptApplicationNum
        )
      } else {
        this.$emit('tab-remove', 'supInspectionBillDetail')
      }
      this.__setTabTodo('supInspectionBillList.getQuerydata')
    },
    outerHandleUploadSuccess (file, row) {
      const { fileId = '', fileName = '' } = file || {}
      row.fileuploadId = fileId.toString()
      row.fileSourceName = fileName
    },
    openDialog () {
      this.filterForm.orgId = this.acceptOrder.ceeaOrgId
      this.filterForm.vendorId = this.acceptOrder.vendorId
      this.queryItemList()
      // this.dialogVisible = true;
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
      const data = { ...this.parentOrgQueryForm, ...this.filterForm }
      this.$http({
        url: '/api-sup-ce/order/orderDetail/OrderDetailListPage',
        method: 'POST',
        data: data,
        loading: true
      }).then((res) => {
        this.displayItemTable = res.data.list
        this.parentOrgTableDataPage.total = res.data.total
        this.dialogVisible = true
      })
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
            categoryName: v.categoryName,
            materialCode: v.materialCode,
            materialName: v.materialName,
            ceeaUnit: v.unit,
            orderNum: v.orderNum,
            // receivedQuantity: v.remainingQuantity,
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
          this.acceptOrder.totalNum += v.waitAcceptQuantity || 0
          this.acceptOrder.totalAmount += v.waitAcceptQuantity * v.ceeaUnitTaxPrice || 0
        }
      })
      this.dialogVisible = false
    },
    getCategoryObj (val, scope) {
      scope.categoryId = val ? val.categoryId : ''
      scope.categoryCode = val ? val.categoryCode : ''
      scope.categoryName = val ? val.categoryName : ''
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
          this.backBill()
        })
        .catch((err) => {
          console.log(err)
        })
    },
    setTotalValue (val) {
      setTimeout(() => {
        this.acceptOrder.totalValue =
          Number(this.acceptOrder.totalAmount) + Number(this.acceptOrder.ceeaTotalQuantity)
      }, 500)
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
        fileFunction: 'acceptTECH', // 技术附件
        comment: null
      })
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
      this.$prompt(this.$t('bidMod.msgRejectReason'), this.$t('bidMod.rejectReason1'), {
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
              this.backBill()
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
            this.$message.info(this.$t('contractMod.msgContractManage[17]'))
            return
          }
          let allparam = {
            acceptOrder: this.acceptOrder,
            acceptDetails: this.acceptDetails,
            techFile: this.techFile,
            assetFile: this.assetFile,
            toolEqp: this.toolEqp
          }
          this.$http({
            url: '/api-cm/accept/acceptOrder/buyerSaveTemporary',
            method: 'POST',
            data: allparam,
            loading: true
          })
            .then((data) => {
              this.$message({
                message: this.$t('common.successSave'), // 保存成功
                type: 'success'
              })
              this.getFormDetail(data.data)
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
.the-supInspectionBillDetail-detail {
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
    margin: 0;
  }
}
</style>
