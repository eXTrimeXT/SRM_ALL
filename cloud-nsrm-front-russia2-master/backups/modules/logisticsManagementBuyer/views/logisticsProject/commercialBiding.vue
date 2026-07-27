<template>
  <el-collapse
    v-model="activeDims"
    class="tab-form-style"
  >
    <!-- 投标信息 -->
    <el-collapse-item
      :title="$t('logisticsMod.bidInformation')"
      name="1"
    >
      <div style="padding-bottom: 10px">
        <!-- <el-button type="primary" v-if="bidingStatus=='ACCEPT_BID'" @click="agencyQuotation"
          >代理报价</el-button
        > -->
        <el-button
          class="detail-pbtn"
          type="primary"
          @click="openBusinessBidding"
        >
          {{ $t("bidMod.openBusinessBiding") }}
        </el-button>
      </div>
      <el-table
        :data="businessItemList"
        style="width: 100%"
        border
        height="350px"
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
          :label="$t('bidMod.bidingRound')"
          width="150"
        />
        <el-table-column
          align="center"
          prop="vendorCode"
          :label="$t('common.vendorCode')"
          :show-overflow-tooltip="true"
          width="150"
        />
        <el-table-column
          align="center"
          prop="vendorName"
          :show-overflow-tooltip="true"
          :label="$t('common.vendorName')"
          width="150"
        />
        <!-- 投标详情 -->
        <el-table-column
          align="center"
          prop="bidingNum"
          :label="$t('bidMod.bidDetail')"
          :show-overflow-tooltip="true"
          width="150"
        >
          <template slot-scope="scope">
            <el-button
              type="text"
              @click="getListDetail(scope.row)"
            >
              {{ scope.row.bidingNum }}
            </el-button>
          </template>
        </el-table-column>
        <!-- 投标状态 -->
        <el-table-column
          align="center"
          prop="status"
          :label="$t('bidMod.orderStatus')"
          :formatter="statusFormattor"
          :show-overflow-tooltip="true"
          width="150"
        />
        <!-- 提交时间 -->
        <el-table-column
          align="center"
          prop="submitDate"
          :show-overflow-tooltip="true"
          :label="$t('bidMod.lastUpdateDate2')"
          width="180"
        />
        <!-- <el-table-column
          align="center"
          prop="ifProxy"
          :show-overflow-tooltip="true"
          label="是否代理报价"
          :formatter="formattor"
          width="150">
        </el-table-column> -->
        <!-- 作废原因 -->
        <el-table-column
          align="center"
          prop="invalidReason"
          :show-overflow-tooltip="true"
          :label="$t('bidMod.cancelDescription')"
          width="180"
        />
        <!-- 附件 -->
        <el-table-column
          align="center"
          prop="submitDate"
          :show-overflow-tooltip="true"
          :label="$t('bidMod.attachment')"
          width="180"
        >
          <template slot-scope="scope">
            <el-button
              type="text"
              @click="getFileDetail(scope.row)"
            >
              {{ $t("common.view") }}
            </el-button>
          </template>
        </el-table-column>
        <el-table-column
          :label="$t('common.operation')"
          fixed="right"
          width="100"
        >
          <template slot-scope="scope">
            <el-button
              v-if="
                scope.row.status == 'SUBMISSION' &&
                  bidingStatus == 'ACCEPT_BID' &&
                  currentRound == scope.row.round
              "
              type="text"
              @click="invalidBid(scope.row)"
            >
              {{ $t("bidMod.cancelBiding") }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-collapse-item>
    <srm-dialog
      :title="doBidingTitle"
      size="large"
      :visible.sync="doBidingVisible"
      :close-on-click-modal="false"
    >
      <doBidingDetail
        :all-params="allParams"
        :lgt-file-configs="lgtFileConfigs"
        :schedule-form="scheduleForm"
        :lgt-vendor-quoted-head="lgtVendorQuotedHead"
        :bid-requirement-line-lists="bidRequirementLineLists"
        :table-header="tableHeader"
        :is-read-only="true"
      />
      <template
        #footer
        class="dialog-footer"
      >
        <el-button @click="doBidingVisible = false">
          {{ $t("common.backTo") }}
        </el-button>
      </template>
    </srm-dialog>
    <!-- 商务标附件详情 -->
    <srm-dialog
      :title="$t('bidMod.businessAttchDetail')"
      size="middle"
      :visible.sync="fileVisible"
      :close-on-click-modal="false"
    >
      <el-table
        :data="filesList"
        style="width: 100%"
        border
      >
        <el-table-column
          align="center"
          type="index"
          width="50"
        />
        <!-- 附件名称 -->
        <el-table-column
          align="center"
          prop="vendorFileName"
          :label="$t('bidMod.fileName')"
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
      <template
        #footer
        class="dialog-footer"
      >
        <el-button @click="fileVisible = false">
          {{ $t("common.backTo") }}
        </el-button>
      </template>
    </srm-dialog>
    <!-- 请输入作废原因 -->
    <srm-dialog
      :visible.sync="invalidBidVisible"
      :title="$t('logisticsMod.msgInvalidReason')"
      size="small"
    >
      <el-form
        ref="form"
        class="tableForm"
        :model="form"
        :rules="rules"
        :show-message="false"
      >
        <el-form-item prop="invalidReason">
          <el-input
            v-model="form.invalidReason"
            type="textarea"
            :rows="2"
            :placeholder="$t('logisticsMod.msgInvalidReason')"
          />
        </el-form-item>
      </el-form>
      <template
        #footer
        class="dialog-footer"
      >
        <el-button
          type="primary"
          @click="invalidBidVisibleConfirm"
        >
          {{ $t("common.confirm") }}
        </el-button>
        <el-button @click="invalidBidVisible = false">
          {{ $t("common.cancel") }}
        </el-button>
      </template>
    </srm-dialog>
    <!-- 代理报价 -->
    <srm-dialog
      :visible.sync="proxyQuoteVisible"
      :title="$t('bid_mod.proxyQuoteHandle')"
      size="large"
    >
      <el-table
        :data="proxyQuoteList"
        style="width: 100%"
        border
      >
        <el-table-column
          align="center"
          type="index"
          width="50"
        />
        <el-table-column
          align="center"
          prop="vendorName"
          :label="$t('common.vendorName')"
          :show-overflow-tooltip="true"
        />
        <el-table-column
          align="center"
          prop="vendorCode"
          :label="$t('common.vendorCode')"
          :show-overflow-tooltip="true"
        />
        <el-table-column
          align="center"
          prop="linkManName"
          :label="$t('bid_mod.linkManName')"
          :show-overflow-tooltip="true"
        />
        <el-table-column
          align="center"
          prop="phone"
          :label="$t('common.phone')"
          :show-overflow-tooltip="true"
        />
        <el-table-column
          align="center"
          prop="email"
          :label="$t('common.email')"
          :show-overflow-tooltip="true"
        />
        <el-table-column
          :label="$t('common.operation')"
          fixed="right"
          width="100"
        >
          <template slot-scope="scope">
            <el-button
              type="text"
              @click="proxyQuoteConfirm(scope.row)"
            >
              {{
                $t("bid_mod.proxyQuoteHandle")
              }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <template
        #footer
        class="dialog-footer"
      >
        <el-button @click="proxyQuoteVisible = false">
          {{ $t("common.backTo") }}
        </el-button>
      </template>
    </srm-dialog>
    <!-- 代理报价 -->
    <srm-dialog
      :title="$t('bid_mod.proxyQuoteHandle')"
      size="large"
      :visible.sync="proxyDoBidingVisible"
      :close-on-click-modal="false"
    >
      <doBidingDetail
        ref="doBidingDetail"
        :all-params="allParams"
        :lgt-file-configs="lgtFileConfigs"
        :schedule-form="scheduleForm"
        :bid-requirement-line-lists="bidRequirementLineLists"
        :table-header="tableHeader"
      />
      <template
        #footer
        class="dialog-footer"
      >
        <el-button
          type="primary"
          @click="proxyQuote"
        >
          {{ $t("logisticsMod.submitOffer") }}
        </el-button>
        <el-button @click="proxyDoBidingVisible = false">
          {{ $t("common.cancel") }}
        </el-button>
      </template>
    </srm-dialog>
  </el-collapse>
</template>
<script>
import QuickSearch from 'lib@/components/QuickSearch'
import doBidingDetail from './doBidingDetail'

export default {
  name: 'CommercialBiding',
  components: { QuickSearch, doBidingDetail },
  props: ['businessItemList', 'scopeBidingId', 'bidingStatus', 'currentRound'],
  data () {
    return {
      activeDims: ['1', '2', '3', '4', '5', '6', '7', '8'],
      fileVisible: false,
      doBidingVisible: false,
      invalidBidVisible: false,
      proxyQuoteVisible: false,
      proxyDoBidingVisible: false,
      doBidingTitle: this.$t('bidMod.bidDetail'), // 投标详情
      allParams: {
        biding: {
          bidingNum: null,
          bidingName: null,
          businessModeCode: null,
          transportModeCode: null,
          businessType: null,
          serviceProjectName: null,
          unitCode: null,
          projectTotal: null,
          demandDate: null,
          priceTimeStart: null,
          priceTimeEnd: null,
          enrollEndDatetime: null,
          allowedVehicle: null,
          ifVendorSubmitShipDate: null
        }
      },
      lgtFileConfigs: [],
      scheduleForm: {
        scheduleList: []
      },
      bidRequirementLineLists: [],
      lgtVendorQuotedHead: [],
      tableHeader: [],
      proxyQuoteList: [],
      filesList: [],
      quotedHeadId: null,
      form: {
        invalidReason: null
      },
      rules: {
        invalidReason: [
          { required: true, message: this.$t('logisticsMod.msgInvalidReason') }
        ] // 请输入作废原因
      }
    }
  },
  watch: {},
  mounted () {
  },
  methods: {
    formattor (row) {
      return row.ifProxy == 'Y' ? this.$t('common.yes') : this.$t('common.no')
    },
    statusFormattor (row) {
      return this.$getDictLabel('BIDDING_ORDER_STATES', row.status)
    },
    getFileDetail (row) {
      this.fileVisible = true
      this.$http({
        url: '/api-pd/logistics/biding/queryLgtFileConfig',
        method: 'GET',
        params: {
          quotedHeadId: row.quotedHeadId,
          bidingId: this.scopeBidingId
        },
        loading: true
      })
        .then(data => {
          if (data && data.data) {
            this.filesList = data.data
          }
        })
        .catch(err => {
          console.log(err)
        })
    },
    getListDetail (row) {
      this.doBidingVisible = true
      this.$http({
        url: '/api-pd/logistics/biding/getQuotedInfoByQuotedHeadId',
        method: 'GET',
        params: {
          quotedHeadId: row.quotedHeadId
        },
        loading: true
      })
        .then(data => {
          if (data && data.data) {
            this.lgtFileConfigs = data.data.lgtFileConfigs
            this.lgtVendorQuotedHead = data.data.lgtVendorQuotedHead
            this.scheduleForm.scheduleList = data.data.lgtBidShipPeriods.map(
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
            this.bidRequirementLineLists = data.data.lgtVendorQuotedLines.map(
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
            this.allParams.biding = data.data.lgtBiding
            this.allParams.biding.enrollEndDatetime = data.data.lgtBiding
              .enrollEndDatetime
              ? new Date(data.data.lgtBiding.enrollEndDatetime).getTime()
              : null
            this.getTemplateLines(data.data.lgtBiding.templateHeadId)
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
    invalidBid (row) {
      this.invalidBidVisible = true
      this.quotedHeadId = row.quotedHeadId
    },
    invalidBidVisibleConfirm () {
      this.$refs.form.validate(valid => {
        if (valid) {
          this.$http({
            url: '/api-pd/logistics/biding/invalidBid',
            method: 'GET',
            params: {
              quotedHeadId: this.quotedHeadId,
              invalidReason: this.form.invalidReason
            },
            loading: true
          })
            .then(data => {
              this.$message({
                type: 'success',
                message: data.message
              })
              this.invalidBidVisible = false
              this.$emit('getTab63List', this.scopeBidingId)
            })
            .catch(err => {
              console.log(err)
            })
        } else {
          this.$message({
            message: this.$t('vendorMod.pleasefinishRequired'), // '请输入单据必填信息'
            type: 'error'
          })
          return false
        }
      })
    },
    openBusinessBidding () {
      this.$http({
        url: '/api-pd/logistics/biding/techOpenBusiness',
        method: 'GET',
        params: { bidingId: this.scopeBidingId },
        loading: true
      })
        .then(data => {
          return this.$message.success(this.$t('common.success')) // 操作成功!
        })
        .catch(err => {
          console.log(err)
        })
    },
    agencyQuotation () {
      this.proxyQuoteVisible = true
      this.$http({
        url: '/api-pd/logistics/biding/agencyQuotationQueryVendor',
        method: 'GET',
        params: {
          bidingId: this.scopeBidingId
        },
        loading: true
      })
        .then(data => {
          this.proxyQuoteList = data.data
        })
        .catch(err => {
          console.log(err)
        })
    },
    proxyQuoteConfirm (row) {
      this.proxyDoBidingVisible = true
      this.$http({
        url: '/api-pd/logistics/biding/getQuotedInfo',
        method: 'GET',
        params: {
          bidingId: this.scopeBidingId,
          vendorId: row.vendorId
        },
        loading: true
      })
        .then(data => {
          this.lgtFileConfigs = data.data.lgtFileConfigs
          this.scheduleForm.scheduleList = data.data.lgtBidShipPeriods.map(
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
          this.lgtVendorQuotedHead = data.data.lgtVendorQuotedHead
          this.bidRequirementLineLists = data.data.lgtVendorQuotedLines.map(
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
          this.allParams.biding = data.data.lgtBiding
          this.allParams.biding.enrollEndDatetime = data.data.lgtBiding
            .enrollEndDatetime
            ? new Date(data.data.lgtBiding.enrollEndDatetime).getTime()
            : null
          this.getTemplateLines(data.data.lgtBiding.templateHeadId)
        })
        .catch(err => {
          console.log(err)
        })
    },
    proxyQuote () {
      let params = {
        lgtBiding: this.allParams.biding, // 招标头信息
        lgtVendorQuotedHead: this.lgtVendorQuotedHead, // 供应商报价头信息
        lgtVendorQuotedLines: this.bidRequirementLineLists, // 供应商报价行信息
        lgtBidShipPeriods: this.scheduleForm.scheduleList, // 技术标
        lgtFileConfigs: this.lgtFileConfigs // 配置文件
      }
      if (this.allParams.biding.ifVendorSubmitShipDate == 'Y') {
        if (!this.scheduleForm.scheduleList.length) {
          this.$message({
            message: this.$t('logisticsMod.msgPurchaseApply[23]'), // 必须填写技术标信息
            type: 'error'
          })
          return
        }
        this.$refs.doBidingDetail.validate(valid2 => {
          if (valid2) {
            this.proxy(params)
          } else {
            this.$message({
              message: this.$t('vendorMod.pleasefinishRequired'), // '请输入单据必填信息'
              type: 'error'
            })
          }
        })
      } else {
        this.proxy(params)
      }
    },
    proxy (params) {
      this.$http({
        url: '/api-pd/logistics/biding/submitQuotedPrice',
        method: 'post',
        data: params,
        loading: true
      })
        .then(res => {
          this.$message.success(res.message)
          this.proxyDoBidingVisible = false
          this.$emit('getTab63List', this.scopeBidingId)
        })
        .catch(err => {
          console.log(err)
        })
    }
  }
}
</script>
<style scoped lang="scss">
// .tab-form-style /deep/ {
//   .el-collapse-item__wrap {
//     border: none;
//   }
// }
</style>
