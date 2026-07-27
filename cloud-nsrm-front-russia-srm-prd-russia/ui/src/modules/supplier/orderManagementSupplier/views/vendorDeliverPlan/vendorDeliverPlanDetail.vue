<template>
  <el-container
    class="flex-container the-vendordeliverPlanDetail-detail"
    direction="vertical"
  >
    <el-main>
      <el-collapse
        v-model="activeDims"
        class="tab-form-style"
      >
        <el-collapse-item
          :title="$t('orderMod.deliveryPlanDetail')"
          name="1"
        >
          <el-form
            ref="form"
            :model="form"
            disabled
            label-width="80px"
            label-position="top"
            class="form-incontainer"
          >
            <el-row type="flex">
              <el-col>
                <el-form-item
                  :label="$t('bid_mod.businessEntity')"
                  :label-width="formLabelWidth"
                >
                  <OrganizationSelector
                    ref="organizationSelector"
                    v-model="form.orgId"
                    :parent-id="-1"
                    node-type="OU"
                  />
                </el-form-item>
              </el-col>
              <el-col>
                <el-form-item
                  :label="$t('bid_mod.inv')"
                  :label-width="formLabelWidth"
                >
                  <OrganizationSelector
                    ref="organizationSelector"
                    v-model="form.organizationId"
                    :parent-id="-1"
                    node-type="INV"
                  />
                </el-form-item>
              </el-col>
              <el-col>
                <el-form-item
                  :label="$t('bid_mod.tradingLocations')"
                  :label-width="formLabelWidth"
                >
                  <el-input v-model="form.deliveryAddress" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row type="flex">
              <el-col>
                <el-form-item
                  :label="$t('common.vendor')"
                  :label-width="formLabelWidth"
                >
                  <el-input v-model="form.vendorName" />
                </el-form-item>
              </el-col>
              <el-col>
                <el-form-item
                  :label="$t('supRisk.material')"
                  :label-width="formLabelWidth"
                >
                  <el-input v-model="form.materialName" />
                </el-form-item>
              </el-col>
              <el-col>
                <el-form-item
                  :label="$t('orderMod.planStatus')"
                  :label-width="formLabelWidth"
                >
                  <DictSelect
                    v-model="form.deliverPlanStatus"
                    code="DELIVER_PLAN_STATUS"
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row type="flex">
              <el-col>
                <el-form-item
                  :label="$t('orderMod.planMonth')"
                  :label-width="formLabelWidth"
                >
                  <el-date-picker
                    v-model="form.monthlySchDate"
                    type="month"
                  />
                </el-form-item>
              </el-col>
              <el-col>
                <el-form-item
                  :label="$t('orderMod.arrivalPlanNo')"
                  :label-width="formLabelWidth"
                >
                  <el-input v-model="form.deliverPlanNum" />
                </el-form-item>
              </el-col>
              <el-col>
                <el-form-item
                  :label="$t('dataConfMod.version')"
                  :label-width="formLabelWidth"
                >
                  <el-input v-model="form.version" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="8">
                <el-form-item
                  :label="$t('orderMod.suitability')"
                  :label-width="formLabelWidth"
                >
                  <el-input v-model="form.matchDegree" />
                </el-form-item>
              </el-col>
              <el-col><p /></el-col>
              <el-col><p /></el-col>
            </el-row>
          </el-form>
        </el-collapse-item>

        <el-collapse-item
          :title="$t('orderMod.deliveryPlanRowInfo')"
          name="2"
        >
          <el-form
            ref="form2"
            :model="form2"
            :disabled="isReadOnly"
            label-width="80px"
            label-position="top"
            class="form-incontainer"
          >
            <el-button
              v-if="curRole === 'BUYER'"
              type="primary"
              @click="lockOneItemData"
            >
              {{ $t('orderMod.lock') }}
            </el-button>
            <!-- <el-button type="primary" @click="saveLineData">{{$t('common.save')}}</el-button> -->
            <el-table
              :data="deliverPlanDetailList"
              style="width: 100%"
              border
              max-height="251px"
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
                prop="schMonthlyDate"
                :label="$t('orderMod.planArrivalData')"
                width="100"
                :formatter="formatDate"
                :show-overflow-tooltip="true"
              />
              <el-table-column
                align="center"
                prop="requirementQuantity"
                :label="$t('orderMod.buyerOrderSynergy.requirementQuantity')"
                width="100"
                :show-overflow-tooltip="true"
              >
                <template slot-scope="scope">
                  <el-input
                    v-model="scope.row.requirementQuantity"
                    v-input-format="{ type: 'float' }"
                    :disabled="scope.row.deliverPlanLock == '1' || curRole === 'VENDOR'"
                  />
                </template>
              </el-table-column>
              <el-table-column
                align="center"
                prop="quantityPromised"
                :label="$t('orderMod.quantityPromised')"
                width="120"
                :show-overflow-tooltip="true"
              >
                <template slot-scope="scope">
                  <el-input
                    v-model="scope.row.quantityPromised"
                    v-input-format="{ type: 'number' }"
                    :disabled="
                      scope.row.deliverPlanLock == '1' ||
                        curRole === 'BUYER' ||
                        scope.row.deliverPlanStatus == 'COMFIRM'
                    "
                  />
                </template>
              </el-table-column>
              <el-table-column
                align="center"
                prop="ceeaReceiveAddress"
                :label="$t('orderMod.specifyPurchaseOrder')"
                min-width="100"
                :show-overflow-tooltip="true"
              >
                <template slot-scope="scope">
                  <el-button
                    type="text"
                    @click="openMaterialList(scope.$index, scope.row)"
                  >
                    {{
                      $t('orderMod.specifyOrderDetail')
                    }}
                  </el-button>
                </template>
              </el-table-column>
              <el-table-column
                align="center"
                prop="orderQuantityMatched"
                :label="$t('orderMod.orderQuantityMatched')"
                width="110"
                :show-overflow-tooltip="true"
              />
              <el-table-column
                align="center"
                prop="deliverPlanLock"
                :label="$t('orderMod.rowStatus')"
                width="100"
                :show-overflow-tooltip="true"
              >
                <template slot-scope="scope">
                  <span>{{ deliverPlanLockObj[scope.row.deliverPlanLock || 2] }}</span>
                </template>
              </el-table-column>
              <el-table-column
                align="center"
                prop="deliverPlanStatus"
                :label="$t('orderMod.ifConfirm')"
                width="150"
                :show-overflow-tooltip="true"
              >
                <template slot-scope="scope">
                  <span>{{
                    scope.row.deliverPlanStatus == 'COMFIRM'
                      ? $t('orderMod.confirmed')
                      : $t('orderMod.unconfirmed')
                  }}</span>
                </template>
              </el-table-column>
              <el-table-column
                :label="$t('route.orderDeliveryDetail')"
                width="150"
              >
                <template slot-scope="scope">
                  <el-button
                    v-if="scope.row.deliverPlanLock != '1'"
                    type="text"
                    @click="saveLineData(scope.$index, scope.row)"
                  >
                    {{ $t('common.save') }}
                  </el-button>
                  <el-button
                    v-if="scope.row.deliverPlanStatus == 'UNCOMFIRMED' && curRole === 'BUYER'"
                    type="text"
                    @click="confirmData(scope.$index, scope.row)"
                  >
                    {{ $t('common.affirm') }}
                  </el-button>
                  <el-button
                    v-if="!!scope.row.orderQuantityMatched"
                    type="text"
                    @click="readMaterialList(scope.$index, scope.row)"
                  >
                    {{ $t('common.view') }}
                  </el-button>
                  <!-- <el-button type="text" @click="matchBill(scope.$index, scope.row)" v-if="!scope.row.orderQuantityMatched && (scope.row.deliverPlanLock =='1')">订单匹配</el-button> -->
                </template>
              </el-table-column>
            </el-table>
            <!-- 弹框区域-->
            <srm-dialog
              :title="$t('orderMod.specifyPurchaseOrder')"
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
                        :label="$t('bid_mod.businessEntity')"
                        :label-width="formLabelWidth"
                      >
                        <OrganizationSelector
                          ref="organizationSelector"
                          v-model="filterForm.orgId"
                          :parent-id="-1"
                          node-type="OU"
                          disabled
                        />
                      </el-form-item>
                    </el-col>
                    <el-col>
                      <el-form-item
                        :label="$t('bid_mod.inv')"
                        :label-width="formLabelWidth"
                      >
                        <OrganizationSelector
                          ref="organizationSelector"
                          v-model="filterForm.organizationId"
                          :parent-id="-1"
                          node-type="INV"
                          disabled
                        />
                      </el-form-item>
                    </el-col>
                    <el-col>
                      <el-form-item
                        :label="$t('bid_mod.tradingLocations')"
                        :label-width="formLabelWidth"
                      >
                        <el-input
                          v-model="filterForm.deliveryAddress"
                          disabled
                        />
                      </el-form-item>
                    </el-col>
                    <el-col><p /></el-col>
                  </el-row>
                  <el-row type="flex">
                    <el-col>
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
                    <el-col>
                      <el-form-item
                        :label="$t('supRisk.material')"
                        :label-width="formLabelWidth"
                      >
                        <el-input
                          v-model="filterForm.materialName"
                          disabled
                        />
                      </el-form-item>
                    </el-col>
                    <el-col>
                      <el-form-item
                        :label="$t('orderMod.planStatus')"
                        :label-width="formLabelWidth"
                      >
                        <DictSelect
                          v-model="filterForm.deliverPlanStatus"
                          code="DELIVER_PLAN_STATUS"
                          disabled
                        />
                      </el-form-item>
                    </el-col>
                    <el-col><p /></el-col>
                  </el-row>
                  <el-row type="flex">
                    <el-col>
                      <el-form-item
                        :label="$t('orderMod.planMonth')"
                        :label-width="formLabelWidth"
                      >
                        <el-date-picker
                          v-model="filterForm.monthlySchDate"
                          type="month"
                          disabled
                        />
                      </el-form-item>
                    </el-col>
                    <el-col>
                      <el-form-item
                        :label="$t('orderMod.planArrivalData')"
                        :label-width="formLabelWidth"
                      >
                        <el-date-picker
                          v-model="filterForm.schMonthlyDate"
                          type="date"
                          disabled
                        />
                      </el-form-item>
                    </el-col>
                    <el-col>
                      <el-form-item
                        :label="$t('bidMod.demandQuantity2')"
                        :label-width="formLabelWidth"
                      >
                        <el-input
                          v-model="filterForm.requirementQuantity"
                          disabled
                        />
                      </el-form-item>
                    </el-col>
                    <el-col>
                      <p class="btn_wrapper">
                        <el-button
                          v-if="globalRow.deliverPlanLock !== '1'"
                          type="primary"
                          @click="openMaterialList2"
                        >
                          {{ $t('common.add') }}
                        </el-button>
                        <el-button @click="dialogFormVisible = false">
                          {{
                            $t('common.close')
                          }}
                        </el-button>
                      </p>
                    </el-col>
                  </el-row>
                </el-form>
              </div>
              <el-table
                :data="displayMaterialItem"
                style="width: 100%"
                border
                height="345px"
                highlight-current-row
              >
                <el-table-column
                  align="center"
                  type="index"
                  width="50"
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
                  prop="creationDate"
                  :label="$t('oneStopShopping.orderDate')"
                  :formatter="formatDate"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="orderNum"
                  :label="$t('orderMod.totalOrder')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="receiveSum"
                  :label="$t('orderMod.receiveSum')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="unreceivedSum"
                  :label="$t('orderMod.unreceivedSum')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="orgName"
                  :label="$t('bid_mod.businessEntity')"
                  min-width="120"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="comment"
                  :label="$t('common.remark')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  :label="$t('common.operation')"
                  width="100"
                >
                  <template slot-scope="scope">
                    <el-button
                      type="text"
                      :disabled="globalRow.deliverPlanLock == '1'"
                      @click="deleteOneItem(scope.$index, scope.row)"
                    >
                      {{ $t('common.delete') }}
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </srm-dialog>
            <!-- 弹框区域3-->
            <srm-dialog
              :title="$t('route.orderDeliveryDetail')"
              size="large"
              :visible.sync="dialogFormVisible3"
              :close-on-click-modal="false"
            >
              <div>
                <el-form
                  ref="filterForm3"
                  :model="filterForm3"
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
                        <el-input v-model="filterForm3.orderNumber" />
                      </el-form-item>
                    </el-col>
                    <el-col>
                      <el-form-item
                        :label="$t('bid_mod.businessEntity')"
                        :label-width="formLabelWidth"
                      >
                        <OrganizationSelector
                          ref="organizationSelector"
                          v-model="filterForm3.orgId"
                          :parent-id="-1"
                          node-type="OU"
                          disabled
                        />
                      </el-form-item>
                    </el-col>
                    <el-col>
                      <el-form-item
                        :label="$t('bid_mod.inv')"
                        :label-width="formLabelWidth"
                      >
                        <OrganizationSelector
                          ref="organizationSelector"
                          v-model="filterForm3.organizationId"
                          :parent-id="-1"
                          node-type="INV"
                          disabled
                        />
                      </el-form-item>
                    </el-col>
                    <el-col>
                      <el-form-item
                        :label="$t('common.vendor')"
                        :label-width="formLabelWidth"
                      >
                        <el-input
                          v-model="filterForm3.vendorName"
                          disabled
                        />
                      </el-form-item>
                    </el-col>
                  </el-row>
                  <el-row type="flex">
                    <el-col>
                      <el-form-item
                        :label="$t('orderMod.buyerOrderSynergy.orderDateFrom')"
                        :label-width="formLabelWidth"
                      >
                        <el-date-picker
                          v-model="filterForm3.startDate"
                          type="date"
                          format="yyyy-MM-dd"
                          value-format="yyyy-MM-dd"
                        />
                      </el-form-item>
                    </el-col>
                    <el-col>
                      <el-form-item
                        :label="$t('orderMod.buyerOrderSynergy.orderDateTo')"
                        :label-width="formLabelWidth"
                      >
                        <el-date-picker
                          v-model="filterForm3.endDate"
                          type="date"
                          format="yyyy-MM-dd"
                          value-format="yyyy-MM-dd"
                        />
                      </el-form-item>
                    </el-col>
                    <el-col>
                      <el-form-item
                        :label="$t('supRisk.material')"
                        :label-width="formLabelWidth"
                      >
                        <el-input
                          v-model="filterForm3.materialName"
                          disabled
                        />
                      </el-form-item>
                    </el-col>
                    <el-col>
                      <el-form-item
                        :label="$t('dataConfMod.categoryLittle')"
                        :label-width="formLabelWidth"
                      >
                        <el-input
                          v-model="filterForm3.categoryName"
                          disabled
                        />
                      </el-form-item>
                    </el-col>
                  </el-row>
                  <el-row type="flex">
                    <el-col>
                      <el-form-item
                        :label="$t('orderMod.arrivalPlanNo')"
                        :label-width="formLabelWidth"
                      >
                        <el-input
                          v-model="filterForm3.deliverPlanNum"
                          disabled
                        />
                      </el-form-item>
                    </el-col>
                    <el-col>
                      <el-form-item
                        :label="$t('bidMod.quotePurchasor')"
                        :label-width="formLabelWidth"
                      >
                        <el-input v-model="filterForm3.buyerName" />
                      </el-form-item>
                    </el-col>
                    <el-col><p /></el-col>
                    <el-col>
                      <p class="btn_wrapper">
                        <el-button
                          type="primary"
                          @click="readQueryMaterialList"
                        >
                          {{
                            $t('common.search')
                          }}
                        </el-button>
                      </p>
                    </el-col>
                  </el-row>
                </el-form>
              </div>
              <el-table
                :data="displayMaterialItem3"
                style="width: 100%"
                border
                height="345px"
                highlight-current-row
              >
                <el-table-column
                  align="center"
                  type="index"
                  width="50"
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
                  prop="orderDate"
                  :label="$t('oneStopShopping.orderDate')"
                  :formatter="formatDate"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="orgName"
                  :label="$t('bid_mod.businessEntity')"
                  min-width="120"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="organizationName"
                  :label="$t('bid_mod.inv')"
                  min-width="120"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="vendorName"
                  :label="$t('common.vendor')"
                  min-width="150"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="buyerName"
                  :label="$t('orderMod.buyerOrderSynergy.buyerName')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="categoryName"
                  :label="$t('dataConfMod.categoryLittle')"
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
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="orderQuantity"
                  :label="$t('orderMod.buyerOrderSynergy.orderNum')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="requirementDate"
                  :label="$t('orderMod.buyerOrderSynergy.requirementDateStr')"
                  :formatter="formatDate"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="planReceiveNum"
                  :label="$t('orderMod.planDeliveryQuantity')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="planReceiveDate"
                  :label="$t('orderMod.planDeliveryDate')"
                  :formatter="formatDate"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="deliverPlanNum"
                  :label="$t('orderMod.arrivalPlanNo')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
              </el-table>
            </srm-dialog>
            <!-- 弹框区域2-->
            <srm-dialog
              :title="$t('orderMod.selPurchaseOrder1')"
              size="large"
              :visible.sync="dialogFormVisible2"
              :close-on-click-modal="false"
            >
              <div>
                <el-form
                  ref="filterForm2"
                  :model="filterForm2"
                  label-width="80px"
                  label-position="top"
                  class="form-incontainer"
                >
                  <el-row type="flex">
                    <el-col>
                      <el-form-item
                        :label="$t('bid_mod.businessEntity')"
                        :label-width="formLabelWidth"
                      >
                        <OrganizationSelector
                          ref="organizationSelector"
                          v-model="filterForm2.orgId"
                          :parent-id="-1"
                          node-type="OU"
                          disabled
                        />
                      </el-form-item>
                    </el-col>
                    <el-col>
                      <el-form-item
                        :label="$t('bid_mod.inv')"
                        :label-width="formLabelWidth"
                      >
                        <OrganizationSelector
                          ref="organizationSelector"
                          v-model="filterForm2.organizationId"
                          :parent-id="-1"
                          node-type="INV"
                          disabled
                        />
                      </el-form-item>
                    </el-col>
                    <el-col>
                      <el-form-item
                        :label="$t('bid_mod.tradingLocations')"
                        :label-width="formLabelWidth"
                      >
                        <el-input
                          v-model="filterForm2.deliveryAddress"
                          disabled
                        />
                      </el-form-item>
                    </el-col>
                    <el-col><p /></el-col>
                  </el-row>
                  <el-row type="flex">
                    <el-col>
                      <el-form-item
                        :label="$t('common.vendor')"
                        :label-width="formLabelWidth"
                      >
                        <el-input
                          v-model="filterForm2.vendorName"
                          disabled
                        />
                      </el-form-item>
                    </el-col>
                    <el-col>
                      <el-form-item
                        :label="$t('supRisk.material')"
                        :label-width="formLabelWidth"
                      >
                        <el-input
                          v-model="filterForm2.materialName"
                          disabled
                        />
                      </el-form-item>
                    </el-col>
                    <el-col>
                      <el-form-item
                        :label="$t('purSettlementMod.orderNumber')"
                        :label-width="formLabelWidth"
                      >
                        <el-input v-model="filterForm2.orderNumber" />
                      </el-form-item>
                    </el-col>
                    <el-col>
                      <p class="btn_wrapper">
                        <el-button
                          type="primary"
                          @click="queryOrderItem"
                        >
                          {{
                            $t('common.search')
                          }}
                        </el-button>
                        <el-button
                          type="primary"
                          @click="addNewOne"
                        >
                          {{
                            $t('common.save')
                          }}
                        </el-button>
                      </p>
                    </el-col>
                  </el-row>
                </el-form>
              </div>
              <el-table
                :data="displayMaterialItem2"
                style="width: 100%"
                border
                height="345px"
                highlight-current-row
                @selection-change="handleSelectionChange2"
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
                  prop="orderNumber"
                  :label="$t('purSettlementMod.orderNumber')"
                  width="150"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="creationDate"
                  :label="$t('oneStopShopping.orderDate')"
                  :formatter="formatDate"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="orderNum"
                  :label="$t('orderMod.totalOrder')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="receiveSum"
                  :label="$t('orderMod.receiveSum')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="unreceivedSum"
                  :label="$t('orderMod.unreceivedSum')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="ceeaOrgName"
                  :label="$t('bid_mod.businessEntity')"
                  min-width="120"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  align="center"
                  prop="comment"
                  :label="$t('common.remark')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
              </el-table>
            </srm-dialog>
          </el-form>
        </el-collapse-item>
      </el-collapse>

      <CToolbar>
        <template slot="right">
          <el-button
            @click="cancelBill"
          >
            {{
              isReadOnly ? this.$t('common.close') : this.$t('common.cancel')
            }}
          </el-button>
          <el-button
            v-if="!isReadOnly"
            type="primary"
            @click="exportData"
          >
            {{
              this.$t('common.export')
            }}
          </el-button>
          <MImport
            v-if="!isReadOnly"
            ref="import"
            style="display: inline-block; margin: 0 15px"
            :title="$t('common.import')"
            :up-load-url="iModal.upLoadUrl"
            :extra-data="extraData"
            @downloadTemplate="downloadTemplate"
            @handleSuccess="handleSuccess"
          />
          <!-- <el-button type="primary" v-if="!isReadOnly" @click="matchBill">订单匹配</el-button> -->
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
import OrganizationSelector from 'lib@/components/organization-selector'
import QuickSearch from 'lib@/components/QuickSearch' // 快速查询组件
import CToolbar from 'lib@/components/c-toolbar'
import MImport from 'lib@/components/import'
import { downloadFileLink } from 'lib@/utils/file'
import CustomSelect from 'modb@/purchasingDemand/views/applicationAndAudit/select'
import { date } from 'jszip/lib/defaults'
import { deliverPlanApi } from 'mods@/orderManagementSupplier/api'

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
      iModal: {
        title: this.$t('common.excelImport'),
        upLoadUrl: '/api-sup-ce/deliver/deliverPlan/importLineExcel'
      },
      extraData: {
        fileModular: 'sup-ce',
        fileFunction: 'vendorDeliverPlan',
        fileType: 'excel'
      },
      curRole: this.$store.getters.userType, // VENDOR---BUYER
      upLoadUrl: '/api-sup-ce/po/orderDetail/importExcel',
      yesNoOptions: [
        { value: 'Y', label: this.$t('common.yes') },
        { value: 'N', label: this.$t('common.no') }
      ],
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
      parentOrgTableDataPage: {
        total: 0,
        pageNum: 1,
        pageSize: 20
      },
      parentOrgQueryForm: {
        pageNum: 1,
        pageSize: 10
      },
      deliverPlanLockObj: {
        1: this.$t('orderMod.deliverPlanLock1'),
        2: this.$t('orderMod.deliverPlanLock2'),
        3: this.$t('orderMod.deliverPlanLock3')
      },
      dialogFormVisible: false,
      dialogFormVisible2: false,
      dialogFormVisible3: false,
      globalRow: {},
      selection: [],
      selection2: [],
      orderFileList: [],
      approvalFileList: [],
      displayMaterialItem: [],
      displayMaterialItem2: [],
      displayMaterialItem3: [],
      globalOrderNum: null,
      filterForm: {
        materialCode: null,
        materialName: null,
        orgName: null,
        categoryCode: null,
        organizationId: null,
        requirementHeadNum: null,
        startDate: null,
        endDate: null
      },
      filterForm2: { orderNumber: null },
      filterForm3: {
        orderNumber: null,
        startDate: null,
        endDate: null,
        buyerName: null
      },
      form: {
        categoryId: null,
        categoryCode: null,
        categoryName: null,
        deliverPlanId: null,
        deliverPlanNum: null,
        deliverPlanStatus: null,
        deliveryAddress: null,
        materialId: null,
        materialCode: null,
        materialName: null,
        monthlySchDate: null,
        orgCode: null,
        orgId: null,
        orgName: null,
        organizationCode: null,
        organizationId: null,
        organizationName: null,
        schTotalQuantity: null,
        vendorCode: null,
        vendorId: null,
        vendorName: null,
        version: null,
        matchDegree: null
      },
      form2: {},
      queryParams: {},
      isDisabled: this.$attrs.params.flag == 'edit',
      isReadOnly: this.$attrs.params.flag == 'readOnly',
      formLabelWidth: '120px',
      deliverPlanDetailList: [],
      activeDims: ['1', '2', '3', '4']
    }
  },
  mounted () {
    this.queryDetails()
  },
  created () {
    // this.extraData.deliverPlanId = this.form.deliverPlanId;
  },
  methods: {
    formatDate (row, column, cellValue, index) {
      return cellValue ? parseTime(cellValue, '{y}-{m}-{d}') : ''
    },
    selectHandler2 (node, value, scope) {
      this.filterForm.organizationId = node.organizationId
    },
    handleSelectionChange (selection) {
      this.selection = selection
    },
    handleSelectionChange2 (selection) {
      this.selection2 = selection
    },
    resetFilterForm () {
      for (let i in this.filterForm) {
        this.filterForm[i] = ''
      }
    },
    addNewOne () {
      if (this.selection2.length === 0) {
        this.$message({
          type: 'warning',
          message: this.$t('orderMod.msgOrder[8]')
        })
        return
      }
      this.$http({
        url: '/api-sup-ce/deliver/orderAppoint/addBatch',
        method: 'POST',
        data: {
          deliverPlanDetail: this.globalRow,
          orderDetailDTOList: this.selection2
        },
        loading: true
      }).then((res) => {
        this.dialogFormVisible2 = false
        this.queryMaterialList()
      })
    },
    lockOneItemData () {
      if (this.selection.length === 0) {
        this.$message.error(this.$t('orderMod.msgVendorOrder[27]'))
        return
      }
      for (let i of this.selection) {
        if (i.deliverPlanStatus !== 'COMFIRM') {
          this.$message.error(this.$t('orderMod.msgVendorOrder[28]'))
          return
        }
        if (i.deliverPlanLock == '1') {
          this.$message.error(this.$t('orderMod.msgVendorOrder[29]'))
          return
        }
      }
      let idArr = this.selection.map((v) => v.deliverPlanDetailId)
      this.$http({
        url: '/api-sup-ce/deliver/deliverPlanDetail/getDeliverPlanLock',
        method: 'POST',
        data: idArr,
        loading: true
      }).then((res) => {
        this.queryDetails()
      })
    },
    deleteOneItem (index, row) {
      this.$http({
        url: '/api-sup-ce/deliver/orderAppoint/deleteBatch',
        method: 'POST',
        data: [row.orderAppointId],
        loading: true
      }).then((res) => {
        this.displayMaterialItem.splice(index, 1)
      })
    },
    confirmData (index, row) {
      this.$http({
        url: '/api-sup-ce/deliver/deliverPlanDetail/getDeliverPlanStatus',
        method: 'POST',
        data: [row.deliverPlanDetailId],
        loading: true
      }).then((res) => {
        this.queryDetails()
      })
    },
    saveLineData (index, row) {
      this.$http({
        url: '/api-sup-ce/deliver/deliverPlan/modifyDeliverPlan',
        method: 'POST',
        data: {
          deliverPlan: this.form,
          deliverPlanDetailList: [row]
        },
        loading: true
      }).then((res) => {
        this.queryDetails()
      })
    },
    queryMaterialList () {
      this.$http({
        url: '/api-sup-ce/deliver/orderAppoint/orderAppointListPage',
        method: 'POST',
        data: {
          deliverPlanId: this.form.deliverPlanId,
          deliverPlanDetailId: this.globalRow.deliverPlanDetailId
        },
        loading: true
      }).then((res) => {
        this.displayMaterialItem = res.data.list
        this.dialogFormVisible = true
      })
    },
    readQueryMaterialList () {
      // this.filterForm3 = Object.assign(this.filterForm3, this.form);
      this.$http({
        url: '/api-sup-ce/deliver/orderDeliveryDetail/orderDeliveryDetailListPage',
        method: 'POST',
        data: {
          pageSize: 1000,
          orderNumber: this.filterForm3.orderNumber,
          startDate: this.filterForm3.startDate,
          endDate: this.filterForm3.endDate,
          buyerName: this.filterForm3.buyerName,
          // deliverPlanId: this.form.deliverPlanId,
          deliverPlanDetailId: this.globalRow.deliverPlanDetailId
        },
        loading: true
      }).then((res) => {
        this.displayMaterialItem3 = res.data.list
        this.dialogFormVisible3 = true
      })
    },
    readMaterialList (index, row) {
      this.globalRow = row
      this.filterForm3 = Object.assign(this.filterForm3, this.form)
      this.$http({
        url: '/api-sup-ce/deliver/orderDeliveryDetail/orderDeliveryDetailListPage',
        method: 'POST',
        data: {
          pageSize: 1000,
          // deliverPlanId: this.form.deliverPlanId,
          deliverPlanDetailId: this.globalRow.deliverPlanDetailId
        },
        loading: true
      }).then((res) => {
        this.displayMaterialItem3 = res.data.list
        this.dialogFormVisible3 = true
      })
    },
    openMaterialList (index, row) {
      this.globalRow = row
      this.filterForm = Object.assign({}, this.form)
      this.$http({
        url: '/api-sup-ce/deliver/orderAppoint/orderAppointListPage',
        method: 'POST',
        data: {
          deliverPlanDetailId: this.globalRow.deliverPlanDetailId,
          deliverPlanId: this.form.deliverPlanId,
          orgId: this.form.orgId,
          organizationId: this.form.organizationId,
          vendorId: this.form.vendorId,
          materialId: this.form.materialId,
          deliveryAddress: this.form.deliveryAddress
        },
        loading: true
      }).then((res) => {
        this.displayMaterialItem = res.data.list
        this.dialogFormVisible = true
      })
    },
    queryOrderItem () {
      this.filterForm2 = Object.assign(this.filterForm2, this.form)
      this.$http({
        url: '/api-sup-ce/deliver/orderAppoint/OrderDetailpage',
        method: 'POST',
        data: {
          orgId: this.filterForm2.orgId,
          organizationId: this.filterForm2.organizationId,
          deliveryAddress: this.filterForm2.deliveryAddress,
          vendorId: this.filterForm2.vendorId,
          materialId: this.filterForm2.materialId,
          pageSize: 1000,
          orderNumber: this.filterForm2.orderNumber
        },
        loading: true
      }).then((res) => {
        this.displayMaterialItem2 = res.data.list
        this.dialogFormVisible2 = true
      })
    },
    openMaterialList2 () {
      this.queryOrderItem()
    },
    queryDetails () {
      const { orderId } = this.$attrs.params.row
      this.$http({
        url: '/api-sup-ce/deliver/deliverPlan/getDeliverPlan',
        method: 'GET',
        params: { id: this.$attrs.params.row.deliverPlanId },
        loading: true
      }).then((res) => {
        this.form = res.data.deliverPlan
        this.deliverPlanDetailList = res.data.deliverPlanDetailList
        this.extraData.deliverPlanId = this.form.deliverPlanId
        this.setMatchDegree()
      })
    },
    setMatchDegree () {
      // 承诺供应数量汇总
      let totalquantityPromised = this.deliverPlanDetailList
        .map((v) => v.quantityPromised)
        .reduce((p, c) => (Number(p) || 0) + (Number(c) || 0))
      // 需求数量汇总
      let totalrequirementQuantity = this.deliverPlanDetailList
        .map((v) => v.requirementQuantity)
        .reduce((p, c) => (Number(p) || 0) + (Number(c) || 0))
      this.form.matchDegree = Number(totalquantityPromised / totalrequirementQuantity).toFixed(2)
    },
    formatter1 (row, column, cellValue, index) {
      return cellValue ? cellValue.slice(0, 10) : null
    },
    publish () {
      if (!this.deliverPlan.length) {
        this.$message({
          type: 'error',
          message: this.$t('orderMod.buyerOrderSynergy.orderDetailsMsg')
        })
        return
      }
      if (this.deliverPlan.some((i) => !i.receivedFactory)) {
        this.$message({
          type: 'error',
          message: this.$t('orderMod.buyerOrderSynergy.orderDetailsMsg1')
        })
        return
      }
      const taxKey = (this.taxList.find((i) => i.value === this.form.taxRate) || {}).key || ''
      const params = {
        order: {
          taxKey,
          ...this.form
        },
        paymentProvisionList: this.displayMaterialItem2,
        detailList: this.deliverPlan.map((item) => ({
          ...item,
          deliveryDate: new Date(item.deliveryDate).getTime()
        }))
      }
      deliverPlanApi.publish(params).then((data) => {
        this.$message({
          type: 'success',
          message: data.message
        })
        this.cancelBill()
      })
    },
    handleSuccess (val) {
      this.queryDetails()
    },
    exportData () {
      if (this.form.deliverPlanId) {
        downloadFileLink(
          '/api-sup-ce/deliver/deliverPlan/exportLine?deliverPlanId=' + this.form.deliverPlanId,
          parseTime(new Date()) + this.$t('orderMod.deliveryPlanDetailExp')
        ).catch((err) => {
          this.$message.error(err.message)
        })
      }
    },
    downloadTemplate () {
      /* downloadFileLink("/api-sup-ce/deliver/deliverPlan/importLineModelDownload?deliverPlanId=" + this.form.deliverPlanId, parseTime(new Date()) + "_导入模板.xlsx").catch(() => {
        this.$message.error(err.message)
      }); */
      downloadFileLink(
        '/api-sup-ce/deliver/deliverPlan/exportLineCopy?deliverPlanId=' +
          this.form.deliverPlanId,
        parseTime(new Date()) + this.$t('orderMod.deliveryPlanDetailExp')
      ).catch(() => {
        this.$message.error(err.message)
      })
    },
    // 订单匹配
    matchBill (index, row) {
      this.$http({
        url: '/api-sup-ce/deliver/deliverPlanDetail/MatchingOrder',
        method: 'POST',
        data: {
          deliverPlan: this.form,
          deliverPlanDetailList: [row]
        },
        loading: true
      }).then((res) => {
        this.queryDetails()
      })
    },
    cancelBill () {
      this.$emit('tab-remove', 'vendorDeliverPlanDetail' + this.$attrs.params.row.deliverPlanNum)
      this.__setTabTodo('vendorDeliverPlanList.getQuerydata')
    },
    reset () {
      // 重置所有过滤条件
      for (let i in this.filterForm) {
        this.filterForm[i] = ''
      }
    }
  }
}
</script>
<style scoped lang="scss">
.the-vendordeliverPlanDetail-detail {
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
  .btn_line {
    margin: 0;
  }
  .btn_wrapper {
    padding-top: 10px;
  }
}
</style>
