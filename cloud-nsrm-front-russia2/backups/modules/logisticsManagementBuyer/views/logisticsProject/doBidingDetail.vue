<template>
  <div>
    <el-form
      ref="form"
      :model="allParams"
      label-width="80px"
      label-position="top"
      :disabled="isReadOnly"
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
            <el-col :span="8">
              <!-- 服务项目名称 -->
              <el-form-item
                :label="$t('logisticsMod.serviceProjectName')"
                :label-width="formLabelWidth"
                prop="serviceProjectName"
                :rules="businessTypeRules"
              >
                <quick-search
                  :show-input="allParams.biding.serviceProjectName"
                  show-key="projectName"
                  disabled
                  :scope-data="allParams.biding"
                  name="ceea_logistics_project_info"
                />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <!-- 单位 -->
              <el-form-item
                :label="$t('bidMod.unit')"
                :label-width="formLabelWidth"
                prop="unitCode"
              >
                <DictSelect
                  v-model="allParams.biding.unitCode"
                  code="LOGISTICS_UNIT"
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
                :label="$t('purchaseDemand.requirementDate')"
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
                  code="YES_OR_NO"
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
            />
            <!-- <templateList ref="templateListId"
              :table-header="tableHeader"
              operateFlagType="purchaseOperateFlag"
              :isReadOnly="true"
              :requirementLineList="bidRequirementLineList"
            /> -->
          </el-table>
          <div
            v-if="queryTotal > 10"
            style="width: 100%; margin-bottom: 50px"
          >
            <el-pagination
              align="center"
              :current-page="viewIndex"
              :page-size="viewSize"
              layout="total, sizes, prev, pager, next"
              :total="queryTotal"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
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
            <el-button
              type="primary"
              @click="addScheduleList"
            >
              {{
                $t("common.add")
              }}
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
          >
            <template slot="footer">
              <el-table-column
                :label="$t('common.operation')"
                fixed="right"
                width="60"
              >
                <template slot-scope="scope">
                  <el-button
                    type="text"
                    :disabled="isReadOnly"
                    @click="handleDelClick3(scope.$index, scope.row)"
                  >
                    {{ $t("common.delete") }}
                  </el-button>
                </template>
              </el-table-column>
            </template>
          </shipTableClumn>
          <div>
            <span>{{ $t("logisticsMod.bidDesc") }}</span>
            <el-input
              v-model="(lgtVendorQuotedHead || {}).submitComment"
              type="textarea"
              :rows="2"
              disabled
            />
          </div>
        </el-collapse-item>
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
            <!-- 招标附件模板 -->
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
  </div>
</template>
<script>
import QuickSearch from 'lib@/components/QuickSearch'
import MImport from 'lib@/components/import'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import { tabTodoMixin } from '@/utils/mixins'
import templateList from '../logisticsPurchaseApply/templateList'
import shipTableClumn from '../logisticsPurchaseOrder/shipTableClumn'
import CToolbar from 'lib@/components/c-toolbar'
import { geti18n } from '@/main'
const i18n = geti18n()

export default {
  name: 'DoBidingDetail',
  components: {
    TableView,
    MainHeader,
    templateList,
    CToolbar,
    QuickSearch,
    shipTableClumn,
    MImport
  },
  mixins: [tabTodoMixin],
  props: [
    'allParams',
    'bidRequirementLineLists',
    'lgtFileConfigs',
    'tableHeader',
    'scheduleForm',
    'lgtVendorQuotedHead',
    'isReadOnly'
  ],
  data () {
    return {
      activeDims: ['1', '2', '3', '4', '5', '6'],
      fileInfo: {
        uploadType: 'FASTDFS', // 固定参数
        sourceType: 'WEB_APP', // 固定参数
        fileModular: 'api-pd', // 文件所属模块 -》基础模块
        fileFunction: 'doBidingDetail', // 文件所属功能
        fileType: 'images' // 文件所属类型
      },
      queryTotal: null,
      viewSize: 10,
      viewIndex: 1
    }
  },
  computed: {
    /* bidRequirementLineList () {
      // return this.bidRequirementLineLists.splice(0, 10);
      var list = []
      this.bidRequirementLineLists.forEach((item,index) => {
        if ((this.viewIndex-1)*this.viewSize<=index&&index<this.viewIndex*this.viewSize) {
          list.push(item)
        }
      })
      return list
    }, */
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
  watch: {
    bidRequirementLineLists (val, oldVal) {
      if (val !== oldVal) {
        this.queryTotal = this.bidRequirementLineLists.length
        this.bidRequirementLineList = val.slice(
          (this.viewIndex - 1) * this.viewSize,
          this.viewIndex * this.viewSize
        )
      }
    }
  },
  created () {
  },
  mounted () {},
  methods: {
    handleSizeChange (val) {
      this.viewIndex = 1
      this.viewSize = val
      this.bidRequirementLineList = this.bidRequirementLineLists.slice(
        (this.viewIndex - 1) * this.viewSize,
        this.viewIndex * this.viewSize
      )
    },
    handleCurrentChange (val) {
      this.viewIndex = val
      this.bidRequirementLineList = this.bidRequirementLineLists.slice(
        (this.viewIndex - 1) * this.viewSize,
        this.viewIndex * this.viewSize
      )
    },
    validate (callback) {
      this.$refs.shipTableClumnId.validate(callback)
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
    handleDelClick3 (index, row) {
      this.scheduleForm.scheduleList.splice(index, 1)
    },
    // 附件上传 处理 [start] by chenzp20
    vendorFileUploadSuccess (file, row) {
      const { fileId = '', fileName = '' } = file || {}
      row.vendorDocId = fileId.toString()
      row.vendorFileName = fileName
    }
  }
}
</script>
<style scoped lang="scss">
.the_doBidingDetail_wrapper /deep/ {
}
</style>
