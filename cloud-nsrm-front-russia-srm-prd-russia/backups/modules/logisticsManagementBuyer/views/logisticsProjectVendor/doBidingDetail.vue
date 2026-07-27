<template>
  <el-container
    class="flex-container the_doBidingDetail_wrapper"
    direction="vertical"
  >
    <el-main>
      <el-form
        ref="form"
        :model="allParams"
        label-width="80px"
        label-position="top"
        class="form-incontainer"
      >
        <el-collapse
          v-model="activeDims"
          class="tab-form-style"
        >
          <!-- 项目信息 -->
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
                  <el-input
                    v-model="allParams.biding.bidingName"
                    disabled
                  />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <!-- 业务模式 -->
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
                <!-- 运输方式 -->
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
                <!-- 业务类型 -->
                <el-form-item
                  :label="$t('bidMod.businessType')"
                  :label-width="formLabelWidth"
                  prop="businessType"
                >
                  <DictSelect
                    v-model="allParams.biding.businessType"
                    disabled
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
                    disabled
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
                <!-- 项目总量 -->
                <el-form-item
                  :label="$t('logisticsMod.projectTotal')"
                  :label-width="formLabelWidth"
                  prop="projectTotal"
                >
                  <el-input
                    v-model="allParams.biding.projectTotal"
                    disabled
                  />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <!-- 需求日期 -->
                <el-form-item
                  :label="$t('bidMod.ceeaDemandDate')"
                  :label-width="formLabelWidth"
                  prop="demandDate"
                >
                  <el-date-picker
                    v-model="allParams.biding.demandDate"
                    type="date"
                    disabled
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
                    disabled
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
                    disabled
                    :picker-options="endTiumePickerOptions2"
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
                    disabled
                    :picker-options="endTiumePickerOptions"
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
                    disabled
                  />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <!-- 是否含技术标 -->
                <el-form-item
                  :label="$t('logisticsMod.ifTechnicaclBId1')"
                  :label-width="formLabelWidth"
                  prop="ifVendorSubmitShipDate"
                >
                  <DictSelect
                    v-model="allParams.biding.ifVendorSubmitShipDate"
                    code="yesNoOptions"
                    disabled
                  />
                </el-form-item>
              </el-col>
            </el-row>
          </el-collapse-item>
          <!-- 投标信息 -->
          <el-collapse-item
            :title="$t('logisticsMod.bidInformation')"
            name="2"
          >
            <div style="padding: 0 10px 10px 0">
              <import-upload
                ref="import"
                :title="iModal.title"
                :extra-data="iModal.extraData"
                :up-load-url="iModal.upLoadUrl"
                :confirm-load-url="iModal.confirmLoadUrl"
                :show-success-deal="true"
                :disabled="isReadOnly"
                @beforeUpload="beforeUpload"
                @downloadTemplate="downloadTemplate"
                @handleSuccess="uploadSuccess"
              />
              <el-button
                type="primary"
                :disabled="!bidRequirementLineList.length"
                @click="detailsExcelOut"
              >
                {{ $t("orderMod.excelExport") }}
              </el-button>
            </div>
            <el-table
              ref="tableGrid"
              :data="bidRequirementLineList"
              style="width: 100%"
              border
              :row-height="30"
              max-height="390px"
              highlight-current-row
            >
              <!-- <el-table-column fixed="left" align="center" type="index" :label="$t('contractMod.tabindex')" width="60" /> -->
              <el-table-column
                fixed="left"
                align="center"
                prop="rowNum"
                :label="$t('purchaseDemand.lineNum')"
                width="60"
              />
              <templateList
                ref="templateListId"
                :table-header="tableHeader"
                :requirement-head="allParams.biding"
                operate-flag-type="vendorOperateFlag"
                visible-flag-type="vendorVisibleFlag"
                :is-read-only="isReadOnly"
                :requirement-line-list="bidRequirementLineList"
              />
              <!-- <templateList ref="templateListId"
                  :table-header="tableHeader"
                  operateFlagType="purchaseOperateFlag"
                  :isReadOnly="true"
                  :requirementLineList="bidRequirementLineList"
                /> -->
              <el-table-column
                :label="$t('common.operation')"
                fixed="right"
                width="140"
              >
                <template slot-scope="scope">
                  <el-button
                    v-if="allParams.biding.businessModeCode == 'E'"
                    type="text"
                    :disabled="isReadOnly"
                    @click="copy(scope.$index, scope.row)"
                  >
                    {{ $t("common.copy") }}
                  </el-button>
                  <el-button
                    v-if="
                      allParams.biding.bidingAwardWay ==
                        'INDIVIDUAL_DECISION' || scope.row.ifCopy == 'Y'
                    "
                    type="text"
                    :disabled="isReadOnly"
                    @click="delCopy(scope.$index, scope.row)"
                  >
                    {{ $t("common.delete") }}
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
            <div style="width: 100%; margin-bottom: 5px">
              <el-pagination
                align="center"
                :current-page="viewIndex"
                :page-sizes="[10, 15, 20, 30]"
                :page-size="viewSize"
                layout="total, sizes, prev, pager, next, jumper"
                :total="queryTotal"
                @current-change="changeCurrentIndex"
                @size-change="changeCurrentSize"
              />
            </div>
          </el-collapse-item>
          <!-- 技术标信息 -->
          <el-collapse-item
            v-if="allParams.biding.ifVendorSubmitShipDate == 'Y'"
            :title="$t('logisticsMod.techBidInfo')"
            name="3"
          >
            <div style="padding: 0 0 10px 0">
              <!-- <el-button
                type="primary"
                :disabled="isReadOnly"
                @click="addScheduleList"
                >{{ $t("common.add") }}</el-button
              >
              <el-button type="primary" :disabled="isReadOnly || !scheduleForm.scheduleList.length" @click="delMoreItem">{{
                $t("common.delete")
              }}</el-button>
              <m-import
                style="display: inline-block;margin: 0 10px;"
                ref="import"
                :title="iModal1.title"
                @beforeUpload="beforeUpload1"
                @downloadTemplate="downloadTemplate1"
                @handleSuccess="handleSuccess"
                :upLoadUrl="iModal1.upLoadUrl"
                :extraData="extraData"
                :extraPostData="extraPostData"
                :disabled="isReadOnly"
                /> -->
              <el-button
                type="primary"
                :disabled="!scheduleForm.scheduleList.length"
                @click="ExcelOut"
              >
                {{ $t("orderMod.excelExport") }}
              </el-button>
            </div>
            <shipTableClumn
              ref="shipTableClumnId"
              :table-header="tableHeader"
              :transport-flag="transportFlag"
              operate-flag-type="vendorOperateFlag"
              visible-flag-type="vendorVisibleFlag"
              :schedule-form="scheduleForm"
              :is-read-only="isReadOnly"
              checkbox
              :check-change="checkChange"
            >
              <template slot="footer">
                <el-table-column
                  :label="$t('common.operation')"
                  fixed="right"
                  width="140"
                >
                  <template slot-scope="scope">
                    <el-button
                      type="text"
                      :disabled="isReadOnly"
                      @click="copyShip(scope.$index, scope.row)"
                    >
                      {{ $t("common.copy") }}
                    </el-button>
                    <el-button
                      v-if="scope.row.ifCopy == 'Y'"
                      type="text"
                      :disabled="isReadOnly"
                      @click="delCopyShip(scope.$index, scope.row)"
                    >
                      {{ $t("common.delete") }}
                    </el-button>
                  </template>
                </el-table-column>
              </template>
            </shipTableClumn>
            <div>
              <!-- 投标说明 -->
              <span>{{ $t("logisticsMod.bidDesc") }}</span>
              <el-input
                v-model="lgtVendorQuotedHead.submitComment"
                type="textarea"
                :rows="2"
                :placeholder="$t('common.pleaseTypeContents')"
              />
            </div>
          </el-collapse-item>
          <!-- 附件信息 -->
          <el-collapse-item
            :title="$t('bidMod.fileInfo')"
            name="4"
          >
            <el-table
              :data="lgtFileConfigs"
              style="width: 100%"
              border
              highlight-current-row
            >
              <el-table-column
                align="center"
                type="index"
                width="50"
              />
              <el-table-column
                align="center"
                prop="referenceFileName"
                :label="$t('logisticsMod.bidRefTemplate')"
                min-width="250"
                :show-overflow-tooltip="true"
              >
                <template slot-scope="scope">
                  <SrmCommonFile
                    :default-file="{
                      fileId: scope.row.referenceFileId,
                      fileName: scope.row.referenceFileName
                    }"
                    :readonly="true"
                  />
                </template>
              </el-table-column>
              <el-table-column
                align="center"
                prop="comments"
                :label="$t('bidMod.remark')"
                min-width="150"
                :show-overflow-tooltip="true"
              />
              <el-table-column
                align="center"
                prop="comments"
                :label="$t('bidMod.remark')"
                min-width="150"
                :show-overflow-tooltip="true"
              >
                <template slot-scope="scope">
                  <SrmCommonFile
                    :extra-data="fileInfo"
                    :default-file="{
                      fileId: scope.row.vendorDocId,
                      fileName: scope.row.vendorFileName
                    }"
                    :readonly="false"
                    @on-change="({file}) => vendorFileUploadSuccess(file,scope.row)"
                  />
                </template>
              </el-table-column>
            </el-table>
          </el-collapse-item>
        </el-collapse>
      </el-form>
    </el-main>
    <c-toolbar>
      <template
        v-if="!isOnlyRead"
        slot="right"
      >
        <el-button
          @click="cancelBill"
        >
          {{
            this.$t("common.backTo")
          }}
        </el-button>
        <el-button
          v-if="!isReadOnly"
          type="primary"
          @click="saveBill"
        >
          {{ $t("common.save") }}
        </el-button>
        <el-button
          v-if="!isReadOnly"
          type="primary"
          @click="submitBill"
        >
          {{ $t("common.submit") }}
        </el-button>
      </template>
    </c-toolbar>
  </el-container>
</template>
<script>
import { downloadFileLink, downloadWithParam } from 'lib@/utils/file'
import QuickSearch from 'lib@/components/QuickSearch'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import { tabTodoMixin } from '@/utils/mixins'
import templateList from './vendorTemplateList'
import shipTableClumn from '../logisticsPurchaseOrder/shipTableClumn'
import CToolbar from 'lib@/components/c-toolbar'
import importUpload from './importUpload'
import MImport from 'lib@/components/import'
import { geti18n } from '@/main'
const i18n = geti18n()

export default {
  name: 'DoBidingDetail',
  components: {
    TableView,
    shipTableClumn,
    MainHeader,
    templateList,
    CToolbar,
    QuickSearch,
    MImport,
    importUpload
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      allParams: {
        biding: {
          status: 'DRAFT',
          bidingStatus: 'ACCEPT_BID'
        }
      },
      scopeBidingId: null,
      vendorId: null,
      isReadOnly: false,
      activeDims: ['1', '2', '3', '4', '5', '6'],
      bidRequirementLineLists: [],
      lgtFileConfigs: [],
      tableHeader: [],
      scheduleForm: {
        scheduleList: []
      },
      lgtVendorQuotedHead: [],
      checkLineList: [],
      fileInfo: {
        uploadType: 'FASTDFS', // 固定参数
        sourceType: 'WEB_APP', // 固定参数
        fileModular: 'api-pd', // 文件所属模块 -》基础模块
        fileFunction: 'doBidingDetail', // 文件所属功能
        fileType: 'images' // 文件所属类型
      },
      iModal: {
        title: this.$t('common.import'),
        extraData: {
          bidingId: null,
          file: null
        },
        upLoadUrl: '/api-pd/logistics/biding/vendor/getExcelTitle',
        confirmLoadUrl:
          '/api-pd/logistics/biding/vendor/quotedLineImport'
      },
      extraData: {
        // sourceType: "WEB_APP",
        // uploadType: "FASTDFS",
        // fileModular: "pm",
        // fileFunction: "purchaseApplication",
        // fileType: "excel"
      },
      extraPostData: {},
      iModal1: {
        title: this.$t('common.excelImport'),
        upLoadUrl:
          '/api-pd/logistics/biding/vendor/lgtBidShipPeriodImport'
      },
      queryTotal: -1,
      viewSize: 10,
      viewIndex: 1
    }
  },
  computed: {
    bidRequirementLineList () {
      var list = []
      this.queryTotal = this.bidRequirementLineLists.length
      this.bidRequirementLineLists.forEach((item, index) => {
        if (
          (this.viewIndex - 1) * this.viewSize <= index &&
          index < this.viewIndex * this.viewSize
        ) {
          list.push(item)
        }
      })
      return list
    },
    transportFlag () {
      // 陆运、铁运
      if (
        ['LAND_TRANSPORT', 'RAILWAY_TRANSPORT'].includes(
          this.allParams.biding.transportModeCode
        )
      ) {
        return true
      } else {
        return false
      }
    }
  },
  created () {
  },
  mounted () {},
  methods: {
    copy (Iindex, row) {
      this.bidRequirementLineLists.unshift({
        ...row,
        ifCopy: 'Y'
      })
    },
    delCopy (index, row) {
      if (this.bidRequirementLineLists.length == 1) {
        this.$message({
          message: this.$t('logisticsMod.msgPurchaseApply[35]'), // 至少保留一行数据
          type: 'error'
        })
        return
      }
      this.bidRequirementLineLists.splice(index, 1)
    },
    downloadTemplate () {
      // 下载模板
      if (!this.allParams.biding.templateFileId) return
      downloadWithParam(
        this.allParams.biding.templateFileId,
        this.$t('logisticsMod.bidQuotaImportTemp')
      ).catch(err => {
        this.$message.error(err.message)
      })
    },
    // 上传之前 前置参数
    beforeUpload () {
      this.iModal.extraData.bidingId = this.scopeBidingId
    },
    // 物料需求上传成功
    uploadSuccess ({ data }) {
      if (data.length) {
        let list = data.map(i => ({
          ...i,
          expenseItemList: [
            {
              chargeCode: i.expenseItem,
              chargeName: i.expenseItemName
            }
          ],
          unitList: [
            {
              chargeUnit: i.chargeUnit,
              chargeUnitName: i.chargeUnitName
            }
          ],
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
        }))
        this.bidRequirementLineLists = list
      }
    },
    beforeUpload1 () {
      this.extraPostData.bidingId = this.scopeBidingId
      this.extraPostData.vendorId = this.vendorId
      this.extraData.bidingId = this.scopeBidingId
      this.extraData.vendorId = this.vendorId
      console.log(this.extraData)
    },
    handleSuccess ({ data }, file, fileList) {
      // 导入成功就刷新界面
      if (data.length) {
        let list = data.map(i => ({
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
        }))
        this.scheduleForm.scheduleList = list
      }
    },
    downloadTemplate1 () {
      downloadFileLink(
        `/api-pd/logistics/biding/vendor/lgtBidShipPeriodImportModelDownload?bidingId=${
          this.scopeBidingId
        }`,
        this.$t('logisticsMod.techBidDetailImport')
      ).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail'))
      })
    },
    ExcelOut () {
      downloadFileLink(
        `/api-pd/logistics/biding/vendor/lgtBidShipPeriodExport?bidingId=${
          this.scopeBidingId
        }&vendorId=${this.vendorId}`,
        this.$t('logisticsMod.techBidDetailXlSX')
      ).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail'))
      })
    },
    detailsExcelOut () {
      downloadFileLink(
        `/api-pd/logistics/biding/vendor/exportLgtVendorQuotedLine?bidingId=${
          this.scopeBidingId
        }&vendorId=${this.vendorId}`,
        this.$t('logisticsMod.bidInfoExport')
      ).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail'))
      })
    },
    checkChange (val) {
      this.checkLineList = val.map(i => i.id || i.shipPeriodId) || []
    },
    delMoreItem () {
      if (!this.checkLineList.length) {
        this.$message({
          message: this.$t('logisticsMod.msgPurchaseApply[10]'), // 请选择要删除的行
          type: 'error'
        })
        return
      }
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          let arr = []
          this.scheduleForm.scheduleList.map(i => {
            if (
              !this.checkLineList.includes(i.id) &&
              !this.checkLineList.includes(i.shipPeriodId)
            ) {
              arr.push(i)
            }
          })
          this.scheduleForm.scheduleList = arr
        })
        .catch(() => {})
    },
    getListDetail () {
      this.$http({
        url: '/api-pd/logistics/biding/vendor/getLgtVendorQuotedHead',
        method: 'GET',
        params: {
          bidingId: this.scopeBidingId,
          vendorId: this.vendorId
        },
        loading: true
      })
        .then(data => {
          if (data && data.data) {
            this.allParams.biding = data.data.lgtBiding
            let status = this.$attrs.params.row.status
            if (
              ['DRAFT', 'WITHDRAW'].includes(status) &&
              this.allParams.biding.bidingStatus == 'ACCEPT_BID'
            ) {
              this.isReadOnly = false
            } else {
              this.isReadOnly = true
            }
            this.allParams.biding.enrollEndDatetime = data.data.lgtBiding
              .enrollEndDatetime
              ? new Date(data.data.lgtBiding.enrollEndDatetime).getTime()
              : null
            this.getTemplateLines(data.data.lgtBiding.templateHeadId)
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
                expenseItemList: [
                  {
                    chargeCode: i.expenseItem,
                    chargeName: i.expenseItemName
                  }
                ],
                unitList: [
                  {
                    chargeUnit: i.chargeUnit,
                    chargeUnitName: i.chargeUnitName
                  }
                ],
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
    addScheduleList () {
      this.scheduleForm.scheduleList.push({})
    },
    getPortObj (val, scope) {
      scope.fromPortId = val.portId
      scope.fromPortCode = val.portCode
      scope.fromPortName = val.portNameZhs
    },
    getPortObj2 (val, scope) {
      scope.toPortId = val.portId
      scope.toPortCode = val.portCode
      scope.toPortName = val.portNameZhs
    },
    delCopyShip (index, row) {
      if (this.scheduleForm.scheduleList.length == 1) {
        this.$message({
          message: this.$t('logisticsMod.msgPurchaseApply[35]'), // 至少保留一行数据
          type: 'error'
        })
        return
      }
      this.scheduleForm.scheduleList.splice(index, 1)
    },
    copyShip (index, row) {
      this.scheduleForm.scheduleList.unshift({
        ...row,
        ifCopy: 'Y'
      })
    },
    // 附件上传 处理 [start] by chenzp20
    vendorFileUploadSuccess (file, row) {
      const { fileId = '', fileName = '' } = file || {}
      row.vendorDocId = fileId.toString()
      row.vendorFileName = fileName
    },
    saveBill () {
      let params = {
        lgtBiding: this.allParams.biding, // 招标头信息
        lgtVendorQuotedHead: this.lgtVendorQuotedHead, // 供应商报价头信息
        lgtVendorQuotedLines: this.bidRequirementLineLists, // 供应商报价行信息
        lgtBidShipPeriods: this.scheduleForm.scheduleList, // 技术标
        lgtFileConfigs: this.lgtFileConfigs // 配置文件
      }
      this.$http({
        url: '/api-pd/logistics/biding/vendor/quotedPriceSave',
        method: 'post',
        data: params,
        loading: true
      })
        .then(res => {
          this.$message.success(res.message)
          this.getListDetail()
        })
        .catch(err => {
          console.log(err)
        })
    },
    submitBill () {
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
        if (this.$refs.shipTableClumnId) {
          this.$refs.shipTableClumnId.validate(valid2 => {
            if (valid2) {
              this.submit(params)
            } else {
              this.$message({
                message: this.$t('vendorMod.pleasefinishRequired'), // '请输入单据必填信息'
                type: 'error'
              })
            }
          })
        } else {
          this.submit(params)
        }
      } else {
        this.submit(params)
      }
    },
    submit (params) {
      this.$http({
        url: '/api-pd/logistics/biding/vendor/submitQuotedPrice',
        method: 'post',
        data: params,
        loading: true
      })
        .then(res => {
          this.$message.success(res.message)
          this.cancelBill()
        })
        .catch(err => {
          console.log(err)
        })
    },
    cancelBill () {
      this.$emit(
        'tab-remove',
        'doBidingDetail' + this.$attrs.params.row.bidingName
      )
      this.__setTabTodo('vendorBiddingList.getQuerydata')
    },
    // 改变 currentNum
    changeCurrentIndex (currentNum) {
      this.viewIndex = currentNum
    },
    // 改变 currentSize
    changeCurrentSize (currentSize) {
      this.viewSize = currentSize
    }
  }
}
</script>
<style scoped lang="scss">
.the_doBidingDetail_wrapper /deep/ {
}
</style>
