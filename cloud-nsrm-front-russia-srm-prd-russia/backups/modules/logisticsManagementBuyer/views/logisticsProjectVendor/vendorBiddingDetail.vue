<template>
  <el-container
    class="flex-container the_vendorBiddingDetail_wrapper"
    direction="vertical"
  >
    <el-main>
      <el-tabs
        v-model="editableTabsValue"
        type="border-card"
      >
        <el-tab-pane
          :label="$t('bidMod.projectInformation')"
          name="t11"
        >
          <el-form
            ref="form"
            :model="allParams"
            disabled
            label-width="80px"
            label-position="top"
            class="form-incontainer"
          >
            <el-collapse
              v-model="activeDims"
              class="tab-form-style"
            >
              <el-collapse-item
                :title="$t('bidMod.projectInformation')"
                name="1"
              >
                <el-row>
                  <el-col :span="8">
                    <el-form-item
                      :label="$t('bidMod.bidingNum')"
                      :label-width="formLabelWidth"
                    >
                      <el-input
                        v-model="allParams.biding.bidingNum"
                        disabled
                      />
                    </el-form-item>
                  </el-col>
                  <el-col :span="8">
                    <el-form-item
                      :label="$t('bidMod.bidingName')"
                      :label-width="formLabelWidth"
                      prop="bidingName"
                    >
                      <el-input v-model="allParams.biding.bidingName" />
                    </el-form-item>
                  </el-col>
                  <el-col :span="8">
                    <el-form-item
                      :label="$t('logisticsMod.businessMode')"
                      :label-width="formLabelWidth"
                      prop="businessModeCode"
                    >
                      <DictSelect
                        v-model="allParams.biding.businessModeCode"
                        code="BUSINESS_MODE"
                        disabled
                      />
                    </el-form-item>
                  </el-col>
                  <el-col :span="8">
                    <el-form-item
                      :label="$t('bid_mod.transportType')"
                      :label-width="formLabelWidth"
                      prop="transportModeCode"
                    >
                      <DictSelect
                        v-model="allParams.biding.transportModeCode"
                        code="TRANSPORT_MODE"
                        disabled
                      />
                    </el-form-item>
                  </el-col>
                  <el-col :span="8">
                    <el-form-item
                      :label="$t('bidMod.businessType')"
                      :label-width="formLabelWidth"
                      prop="businessType"
                    >
                      <DictSelect
                        v-model="allParams.biding.businessType"
                        code="LOGISTICS_BUSINESS_TYPE"
                      />
                    </el-form-item>
                  </el-col>
                  <!-- <el-col :span="8">
                    <el-form-item
                      label="服务项目名称"
                      :label-width="formLabelWidth"
                      prop="serviceProjectName"
                      :rules='businessTypeRules'
                    >
                      <QuickSearch
                        :showInput="allParams.biding.serviceProjectName"
                        show-key="projectName"
                        :scope-data="allParams.biding"
                        name="ceea_logistics_project_info"
                      />
                    </el-form-item>
                  </el-col> -->
                  <el-col :span="8">
                    <el-form-item
                      :label="$t('bid_mod.unit')"
                      :label-width="formLabelWidth"
                      prop="unitCode"
                    >
                      <el-input
                        v-model="allParams.biding.unitCode"
                        disabled
                      />
                    </el-form-item>
                  </el-col>
                  <el-col :span="8">
                    <el-form-item
                      :label="$t('logisticsMod.projectTotal')"
                      :label-width="formLabelWidth"
                      prop="projectTotal"
                    >
                      <el-input v-model="allParams.biding.projectTotal" />
                    </el-form-item>
                  </el-col>
                  <el-col :span="8">
                    <el-form-item
                      :label="$t('bidMod.ceeaDemandDate')"
                      :label-width="formLabelWidth"
                      prop="demandDate"
                    >
                      <el-date-picker
                        v-model="allParams.biding.demandDate"
                        type="date"
                        value-format="yyyy-MM-dd"
                        :placeholder="$t('bidMod.datePicker')"
                      />
                    </el-form-item>
                  </el-col>
                  <el-col :span="8">
                    <el-form-item
                      :label="$t('bid_mod.defaultPriceValidFrom')"
                      :label-width="formLabelWidth"
                      prop="priceTimeStart"
                    >
                      <el-date-picker
                        v-model="allParams.biding.priceTimeStart"
                        type="date"
                        value-format="yyyy-MM-dd"
                        :placeholder="$t('bidMod.datePicker')"
                      />
                    </el-form-item>
                  </el-col>
                  <el-col :span="8">
                    <el-form-item
                      :label="$t('bid_mod.defaultPriceValidTo')"
                      :label-width="formLabelWidth"
                      prop="priceTimeEnd"
                    >
                      <el-date-picker
                        v-model="allParams.biding.priceTimeEnd"
                        type="date"
                        value-format="yyyy-MM-dd"
                        :placeholder="$t('bidMod.datePicker')"
                      />
                    </el-form-item>
                  </el-col>
                  <el-col :span="8">
                    <el-form-item
                      :label="$t('bidMod.enrollEndDatetime')"
                      :label-width="formLabelWidth"
                      prop="enrollEndDatetime"
                    >
                      <el-date-picker
                        v-model="allParams.biding.enrollEndDatetime"
                        type="datetime"
                        value-format="timestamp"
                        :placeholder="$t('bidMod.datePicker')"
                      />
                    </el-form-item>
                  </el-col>
                  <el-col :span="8">
                    <!-- 项目地可进最大车型 -->
                    <el-form-item
                      :label="$t('logisticsMod.allowedVehicleRules')"
                      :label-width="formLabelWidth"
                      prop="allowedVehicle"
                    >
                      <DictSelect
                        v-model="allParams.biding.allowedVehicle"
                        code="ALLOWED_VEHICLE"
                      />
                    </el-form-item>
                  </el-col>
                  <el-col :span="8">
                    <el-form-item
                      :label="$t('logisticsMod.ifTechnicaclBId1')"
                      :label-width="formLabelWidth"
                      prop="ifVendorSubmitShipDate"
                    >
                      <DictSelect
                        v-model="allParams.biding.ifVendorSubmitShipDate"
                        code="YES_OR_NO"
                        disabled
                      />
                    </el-form-item>
                  </el-col>
                </el-row>
              </el-collapse-item>
              <!-- 币种信息 -->
              <el-collapse-item
                :title="$t('logisticsMod.currencyInfo')"
                name="2"
              >
                <el-row type="flex">
                  <el-col :span="8">
                    <el-form-item
                      :label="$t('bid_mod.standardCurrency')"
                      :label-width="formLabelWidth"
                      prop="standardCurrency"
                    >
                      <DictSelect
                        v-model="allParams.biding.standardCurrency"
                        code="currency"
                      />
                    </el-form-item>
                  </el-col>
                  <el-col :span="8">
                    <el-form-item
                      :label="$t('bid_mod.pricePrecision')"
                      :label-width="formLabelWidth"
                      prop="pricePrecision"
                    >
                      <DictSelect
                        v-model="allParams.biding.pricePrecision"
                        code="PRICE_PRECISION"
                      />
                    </el-form-item>
                  </el-col>
                  <el-col :span="8">
                    <el-form-item
                      :label="$t('bid_mod.exchangeRateType')"
                      :label-width="formLabelWidth"
                      prop="exchangeRateType"
                    >
                      <DictSelect
                        v-model="allParams.biding.exchangeRateType"
                        code="EXCHANGE_RATE_TYPE"
                      />
                    </el-form-item>
                  </el-col>
                  <el-col :span="8">
                    <el-form-item
                      :label="$t('bid_mod.currencyChangeDate')"
                      :label-width="formLabelWidth"
                      prop="currencyChangeDate"
                    >
                      <el-date-picker
                        v-model="allParams.biding.currencyChangeDate"
                        type="date"
                        value-format="timestamp"
                        :picker-options="endTiumePickerOptions3"
                      />
                    </el-form-item>
                  </el-col>
                </el-row>
              </el-collapse-item>
              <!-- 特别说明信息 -->
              <el-collapse-item
                :title="$t('logisticsMod.specialInstructionInfo')"
                name="3"
              >
                <el-row>
                  <el-col>
                    <el-form-item
                      :label="$t('logisticsMod.supplierDesc')"
                      :label-width="formLabelWidth"
                      prop="supplierDesc"
                    >
                      <el-input v-model="allParams.biding.supplierDesc" />
                    </el-form-item>
                  </el-col>
                </el-row>
              </el-collapse-item>
              <!-- 向供应商展示的联系方式 -->
              <el-collapse-item
                :title="$t('bidMod.showVendorContactInfo')"
                name="4"
              >
                <el-row>
                  <el-col :span="8">
                    <el-form-item
                      :label="$t('bidMod.bidContactName')"
                      :label-width="formLabelWidth"
                      prop="bidUserName"
                    >
                      <el-input v-model="allParams.biding.bidUserName" />
                    </el-form-item>
                  </el-col>
                  <el-col :span="8">
                    <el-form-item
                      :label="$t('bidMod.bidMobilePhone')"
                      :label-width="formLabelWidth"
                      prop="bidUserPhone"
                    >
                      <el-input v-model="allParams.biding.bidUserPhone" />
                    </el-form-item>
                  </el-col>
                  <el-col :span="8">
                    <el-form-item
                      :label="$t('bidMod.email')"
                      :label-width="formLabelWidth"
                      prop="bidUserEmail"
                    >
                      <el-input v-model="allParams.biding.bidUserEmail" />
                    </el-form-item>
                  </el-col>
                </el-row>
              </el-collapse-item>
              <!-- 附件信息 -->
              <el-collapse-item
                :title="$t('bidMod.fileInfo')"
                name="5"
              >
                <el-table
                  :data="fileList"
                  style="width: 100%"
                  border
                  height="133px"
                >
                  <el-table-column
                    align="center"
                    type="index"
                    width="50"
                  />
                  <el-table-column
                    align="center"
                    prop="fileName"
                    :label="$t('bidMod.fileName')"
                  >
                    <template slot-scope="scope">
                      <SrmCommonFile
                        :default-file="{
                          fileId: scope.row.docId,
                          fileName: scope.row.fileName
                        }"
                        :readonly="true"
                      />
                    </template>
                  </el-table-column>
                  <el-table-column
                    align="center"
                    prop="comments"
                    :label="$t('common.remark')"
                  >
                    <template slot-scope="scope">
                      <el-input
                        v-model="scope.row.comments"
                        disabled
                      />
                    </template>
                  </el-table-column>
                </el-table>
              </el-collapse-item>
            </el-collapse>
          </el-form>
        </el-tab-pane>
        <!-- 需求明细 -->
        <el-tab-pane
          :label="$t('bidMod.demandDetail')"
          name="t12"
        >
          <el-table
            ref="tableGrid"
            :data="bidRequirementLineList"
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
              :requirement-line-list="bidRequirementLineList"
              :is-read-only="true"
            />
          </el-table>
        </el-tab-pane>
        <!-- 投标明细 -->
        <el-tab-pane
          :label="$t('bidMod.bidDetail2')"
          name="t13"
        >
          <el-collapse
            v-model="activeDims1"
            class="tab-form-style"
          >
            <!-- 报价信息 -->
            <el-collapse-item
              :title="$t('bidMod.quoteInfo')"
              name="1"
            >
              <el-table
                ref="tableGrid"
                :data="lgtVendorQuotedLines"
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
                  :requirement-line-list="lgtVendorQuotedLines"
                  :is-read-only="true"
                />
                <!-- <templateList ref="templateListId"
                  :table-header="tableHeader"
                  operateFlagType="purchaseOperateFlag"
                  :requirementLineList="lgtVendorQuotedLines"
                  :isReadOnly="true"
                /> -->
              </el-table>
            </el-collapse-item>
            <!-- 技术标信息 -->
            <el-collapse-item
              :title="$t('logisticsMod.techBidInfo')"
              name="2"
            >
              <shipTableClumn
                ref="shipTableClumnId"
                :table-header="tableHeader"
                operate-flag-type="vendorOperateFlag"
                visible-flag-type="vendorVisibleFlag"
                :schedule-form="scheduleForm"
                :is-read-only="true"
              />
            </el-collapse-item>
            <el-collapse-item
              :title="$t('bidMod.fileInfo')"
              name="3"
            >
              <el-table
                :data="lgtFileConfigs"
                style="width: 100%"
                border
                height="133px"
              >
                <el-table-column
                  align="center"
                  type="index"
                  width="50"
                />
                <!-- 招标附件模板 -->
                <el-table-column
                  align="center"
                  prop="vendorFileName"
                  :label="$t('logisticsMod.bidRefTemplate')"
                >
                  <template slot-scope="scope">
                    <SrmCommonFile
                      :default-file="{
                        fileId: scope.row.vendorDocId,
                        fileName: scope.row.vendorFileName
                      }"
                      :readonly="true"
                    />
                  </template>
                </el-table-column>
                <el-table-column
                  align="center"
                  show-overflow-tooltip
                  prop="comments"
                  :label="$t('common.remark')"
                />
              </el-table>
            </el-collapse-item>
          </el-collapse>
        </el-tab-pane>
        <!-- 投标结果 -->
        <el-tab-pane
          :label="$t('bidMod.bidResult')"
          name="t14"
        >
          <el-table
            :data="lgtVendorQuotedSums"
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
              prop="round"
              show-overflow-tooltip
              :label="$t('bidMod.bidingRound')"
              min-width="150"
            />
            <!-- 始发地 -->
            <el-table-column
              align="center"
              prop="startAddress"
              show-overflow-tooltip
              :label="$t('logisticsMod.startAddress')"
              min-width="150"
            />
            <!-- 目的地 -->
            <el-table-column
              align="center"
              prop="endAddress"
              show-overflow-tooltip
              :label="$t('contractMod.destination')"
              min-width="150"
            />
            <!-- 总价（人民币） -->
            <el-table-column
              align="center"
              prop="sumPrice"
              show-overflow-tooltip
              :label="$t('logisticsMod.totalPriceRMB')"
              min-width="150"
            />
            <!-- 决策结果 -->
            <el-table-column
              align="center"
              prop="bidResult"
              show-overflow-tooltip
              :formatter="bidResultFormatter"
              :label="$t('logisticsMod.decisionResult')"
              min-width="150"
            />
            <!-- 下轮允许投标 -->
            <el-table-column
              align="center"
              prop="shortlisted"
              show-overflow-tooltip
              :formatter="formattor"
              :label="$t('bidMod.nextRoundAllowBid')"
              min-width="150"
            />
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-main>
  </el-container>
</template>
<script>
import FormWrapper from 'lib@/components/Table/FormWrapper'
import QuickSearch from 'lib@/components/QuickSearch'
import OrganizationSelector from 'lib@/components/organization-selector'
import CToolbar from 'lib@/components/c-toolbar'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import { tabTodoMixin } from '@/utils/mixins'
import CPagination from 'lib@/components/c-pagination'
import templateList from '../logisticsPurchaseApply/templateList'
import shipTableClumn from '../logisticsPurchaseOrder/shipTableClumn'
import { geti18n } from '@/main'
const i18n = geti18n()

export default {
  name: 'BiddingProjectDetail',
  components: {
    TableView,
    MainHeader,
    CToolbar,
    QuickSearch,
    FormWrapper,
    CPagination,
    OrganizationSelector,
    templateList,
    shipTableClumn
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      allParams: {
        biding: {}
      },
      editableTabsValue: 't11',
      fileList: [],
      bidRequirementLineList: [],
      lgtVendorQuotedLines: [],
      scheduleForm: {
        lgtBidShipPeriods: []
      },
      lgtFileConfigs: [],
      lgtVendorQuotedSums: [],
      tableHeader: [],
      activeDims: ['1', '2', '3', '4', '5', '6'],
      activeDims1: ['1', '2', '3', '4', '5', '6']
    }
  },
  created () {
  },
  methods: {
    getFormDetail (bidingId) {
      // 项目信息
      this.$http({
        url: '/api-pd/logistics/biding/vendor/getLgtBidVendor',
        method: 'GET',
        params: { bidingId: bidingId },
        loading: true
      })
        .then(data => {
          if (data && data.data) {
            this.allParams.biding = data.data.biding
            this.allParams.biding.enrollEndDatetime = data.data.biding
              .enrollEndDatetime
              ? new Date(data.data.biding.enrollEndDatetime).getTime()
              : null
            this.allParams.biding.currencyChangeDate = data.data.biding
              .currencyChangeDate
              ? new Date(data.data.biding.currencyChangeDate).getTime()
              : null
            this.getTemplateLines(data.data.biding.templateHeadId)
            this.fileList = data.data.fileList
          }
        })
        .catch(err => {
          console.log(err)
        })
      // 需求信息
      this.$http({
        url:
          '/api-pd/logistics/biding/vendor/getLgtBidRequirementLineVendorDto',
        method: 'GET',
        params: { bidingId: bidingId },
        loading: true
      })
        .then(data => {
          if (data && data.data) {
            this.bidRequirementLineList = data.data.lgtBidRequirementLines.map(
              i => ({
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
              })
            )
          }
        })
        .catch(err => {
          console.log(err)
        })
      // 报价信息
      this.$http({
        url:
          '/api-pd/logistics/biding/vendor/getLgtVendorQuotedHeadVendorDto',
        method: 'GET',
        params: { bidingId: bidingId },
        loading: true
      })
        .then(data => {
          if (data && data.data) {
            this.lgtVendorQuotedLines = data.data.lgtVendorQuotedLines.map(
              i => ({
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
              })
            )
            this.scheduleForm.lgtBidShipPeriods = data.data.lgtBidShipPeriods.map(
              i => ({
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
              })
            )
            this.lgtFileConfigs = data.data.lgtFileConfigs
          }
        })
        .catch(err => {
          console.log(err)
        })
      // 投标结果
      this.$http({
        url:
          '/api-pd/logistics/biding/vendor/getLgtVendorQuotedSumVendorDto',
        method: 'GET',
        params: { bidingId: bidingId },
        loading: true
      })
        .then(data => {
          if (data && data.data) {
            this.lgtVendorQuotedSums = data.data.lgtVendorQuotedSums
          }
        })
        .catch(err => {
          console.log(err)
        })
    },
    getTemplateLines (templateHeadId) {
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
    wholeArkFormatter (row) {
      return this.$getDictLabel('PAYMENT_MODE', row.wholeArk)
    },
    formattor (row) {
      return row.shortlisted == 'Y'
        ? this.$t('common.yes')
        : this.$t('common.no')
    },
    bidResultFormatter (row) {
      return this.$getDictLabel('BIDDING_SELECT_STATES', row.bidResult)
    }
  }
}
</script>
<style scoped lang="scss">
.the_vendorBiddingDetail_wrapper /deep/ {
}
</style>
