<template>
  <el-container
    class="flex-container projectManagementReportList"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="preArr"
        @synchronous-value="syncFilterParams"
        @getFormData="getQuerydata"
      />
      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <ExportExcel
            type="default"
            :page-url="tableUrl"
            :filter-params="filterParams"
            :table-header="tableHeader"
            :dict-codes="dictCodes"
            timeout="1000000"
            export-mode="front"
          />
        </template>
      </MainHeader>
      <vxe-table
        ref="tableGrid"
        class="report-vxe-table"
        border
        align="center"
        show-overflow
        auto-resize
        sync-resize
        :height="dynamicHeight"
        :stripe="true"
        :data="tableData"
        :column-config="{isCurrent: false, isHover: false,resizable: true}"
      >
        <vxe-column
          type="seq"
          :title="$t('common.sort')"
          fixed="left"
          width="60"
        />
        <vxe-colgroup title="项目信息">
          <vxe-column
            field="requirementPlanType"
            :title="$t('cusEntry.bidSuperviseReport.requirementPlanType')"
            width="150"
          >
            <template #default="{ row }">
              {{ $getDictLabel('PR_SOU_REQUIREMENT_FROM', row.requirementPlanType) }}
            </template>
          </vxe-column>
          <vxe-column
            field="extOrgBuName"
            :title="$t('cusEntry.bidSuperviseReport.extOrgBuName')"
            width="200"
          />
          <vxe-column
            field="extOrgOuName"
            :title="$t('cusEntry.bidSuperviseReport.extOrgOuName')"
            width="200"
          />
          <vxe-column
            field="companyShortCode"
            title="公司代码"
            width="150"
          />
          <vxe-column
            field="year"
            title="年"
            width="150"
          />
          <vxe-column
            field="month"
            title="月"
            width="150"
          />
          <vxe-column
            field="extOrgBuCode"
            title="板块代码"
            width="150"
          />
          <vxe-column
            field="extProjectNo"
            :title="$t('cusEntry.bidSuperviseReport.extProjectNo')"
            width="200"
          />
          <vxe-column
            field="souName"
            :title="$t('cusEntry.bidSuperviseReport.souName')"
            width="200"
          />
          <vxe-column
            field="extSouProcess"
            :title="$t('cusEntry.bidSuperviseReport.extSouProcess')"
            width="150"
          >
            <template #default="{ row }">
              {{ $getDictLabel('SOU_BID_PROCCESS', row.extSouProcess) }}
            </template>
          </vxe-column>
          <vxe-column
            field="classification"
            :title="$t('cusEntry.bidSuperviseReport.classification')"
            width="200"
          />
          <vxe-column
            field="extCategoryName"
            :title="$t('cusEntry.bidSuperviseReport.extCategoryName')"
            width="200"
          />
          <vxe-column
            field="souPrincipal"
            :title="$t('cusEntry.bidSuperviseReport.souPrincipal')"
            width="150"
          />
          <vxe-column
            field="vendorPrincipal"
            :title="$t('cusEntry.bidSuperviseReport.vendorPrincipal')"
            width="150"
          />
          <vxe-column
            field="leaderPrincipal"
            :title="$t('cusEntry.bidSuperviseReport.leaderPrincipal')"
            width="150"
          />
          <vxe-column
            field="extTechPrincipal"
            :title="$t('cusEntry.bidSuperviseReport.extTechPrincipal')"
            width="150"
          />
          <vxe-column
            field="extScaleQuantity"
            :title="$t('cusEntry.bidSuperviseReport.extScaleQuantity')"
            width="150"
          />
          <vxe-column
            field="totalBudget"
            :title="$t('cusEntry.bidSuperviseReport.totalBudget')"
            width="130"
          />
        </vxe-colgroup>
        <vxe-colgroup title="项目进度">
          <vxe-column
            field="sendSouProfileEndDate"
            :title="$t('cusEntry.bidSuperviseReport.sendSouProfileEndDate')"
            width="150"
          />
          <vxe-column
            field="approvalPassTime"
            :title="$t('cusEntry.bidSuperviseReport.approvalPassTime')"
            width="150"
          />
          <vxe-column
            field="publicEndTime"
            :title="$t('cusEntry.bidSuperviseReport.publicEndTime')"
            width="150"
          />
          <vxe-column
            field="planRequirementTime"
            :title="$t('cusEntry.bidSuperviseReport.planRequirementTime')"
            width="150"
          />
          <vxe-column
            field="actualRequirementTime"
            :title="$t('cusEntry.bidSuperviseReport.actualRequirementTime')"
            width="150"
          />
          <vxe-colgroup title="发标环节">
            <vxe-column
              field="planPublishTime"
              :title="$t('cusEntry.bidSuperviseReport.planAcceptanceBidTime')"
              width="150"
            />
            <vxe-column
              field="actualPublishTime"
              :title="$t('cusEntry.bidSuperviseReport.actualAcceptanceBidTime')"
              width="150"
            />
          </vxe-colgroup>
          <vxe-colgroup title="收标环节">
            <vxe-column
              field="planAcceptanceBidTime"
              :title="$t('cusEntry.bidSuperviseReport.planAcceptanceBidTime')"
              width="150"
            />
            <vxe-column
              field="actualAcceptanceBidTime"
              :title="$t('cusEntry.bidSuperviseReport.actualAcceptanceBidTime')"
              width="150"
            />
          </vxe-colgroup>
          <vxe-colgroup title="评标环节">
            <vxe-column
              field="planTechEvaluationTime"
              :title="$t('cusEntry.bidSuperviseReport.planAcceptanceBidTime')"
              width="150"
            />
            <vxe-column
              field="actualTechEvaluationTime"
              :title="$t('cusEntry.bidSuperviseReport.actualAcceptanceBidTime')"
              width="150"
            />
          </vxe-colgroup>
          <vxe-colgroup title="汇总上报环节">
            <vxe-column
              field="planSumReportTime"
              :title="$t('cusEntry.bidSuperviseReport.planSumReportTime')"
              width="150"
            />
            <vxe-column
              field="actualSumReportTime"
              :title="$t('cusEntry.bidSuperviseReport.actualSumReportTime')"
              width="150"
            />
          </vxe-colgroup>
          <vxe-colgroup title="定标环节">
            <vxe-column
              field="planPicketageTime"
              :title="$t('cusEntry.bidSuperviseReport.planAcceptanceBidTime')"
              width="150"
            />
            <vxe-column
              field="actualPicketageTime"
              :title="$t('cusEntry.bidSuperviseReport.actualPicketageTime')"
              width="150"
            />
          </vxe-colgroup>
          <vxe-colgroup title="中落标通知环节">
            <vxe-column
              field="planPublishWinLossTime"
              :title="$t('cusEntry.bidSuperviseReport.planAcceptanceBidTime')"
              width="150"
            />
            <vxe-column
              field="actualPublishWinLossTime"
              :title="$t('cusEntry.bidSuperviseReport.actualAcceptanceBidTime')"
              width="150"
            />
          </vxe-colgroup>
          <vxe-colgroup title="履约">
            <vxe-column
              field="honourScore"
              :title="$t('cusEntry.bidSuperviseReport.honourScore')"
              width="150"
            />
            <vxe-column
              field="honourResult"
              :title="$t('cusEntry.bidSuperviseReport.honourResult')"
              width="150"
            />
          </vxe-colgroup>
        </vxe-colgroup>
        <vxe-column
          field="projectStatus"
          :title="$t('cusEntry.bidSuperviseReport.projectStatus')"
          width="150"
        >
          <template #default="{ row }">
            {{ $getDictLabel('SOU_BIDDING_PRO_STATUS', row.projectStatus) }}
          </template>
        </vxe-column>
        <vxe-colgroup title="供应商部分">
          <vxe-column
            field="sendBidNumber"
            :title="$t('cusEntry.bidSuperviseReport.sendBidNumber')"
            width="150"
          />
          <vxe-column
            field="sendBidAsSubmitNumber"
            :title="$t('cusEntry.bidSuperviseReport.sendBidAsSubmitNumber')"
            width="150"
          />
          <vxe-column
            field="addBidNumber"
            :title="$t('cusEntry.bidSuperviseReport.addBidNumber')"
            width="150"
          />
          <vxe-column
            field="addBidAsSubmitNumber"
            :title="$t('cusEntry.bidSuperviseReport.addBidAsSubmitNumber')"
            width="150"
          />
          <vxe-column
            field="newVendorBidNumber"
            :title="$t('cusEntry.bidSuperviseReport.newVendorBidNumber')"
            width="150"
          />
          <vxe-column
            field="newUniteVendorBidNumber"
            :title="$t('cusEntry.bidSuperviseReport.newUniteVendorBidNumber')"
            width="150"
          />
          <vxe-column
            field="totalBidNumber"
            :title="$t('cusEntry.bidSuperviseReport.totalBidNumber')"
            width="150"
          />
          <vxe-column
            field="totalBidAsSubmitNumber"
            :title="$t('cusEntry.bidSuperviseReport.totalBidAsSubmitNumber')"
            width="150"
          />
        </vxe-colgroup>
        <vxe-column
          title="中标信息"
          width="150"
        >
          <template #default="{ row }">
            <el-button type="text" @click="getProjectInfo(row)">
              {{ $t('common.view') }}
            </el-button>
          </template>
        </vxe-column>
        <vxe-column
          field="planTotalCycle"
          :title="$t('cusEntry.bidSuperviseReport.planTotalCycle')"
          width="150"
        />
        <vxe-column
          field="actualTotalCycle"
          :title="$t('cusEntry.bidSuperviseReport.actualTotalCycle')"
          width="150"
        />
        <vxe-column
          field="vendorPostponeCycle"
          :title="$t('cusEntry.bidSuperviseReport.vendorPostponeCycle')"
          width="150"
        />
        <vxe-colgroup title="招标负责人">
          <vxe-column
            field="publishPostponeCycle"
            :title="$t('cusEntry.bidSuperviseReport.publishPostponeCycle')"
            width="150"
          />
          <vxe-column
            field="publishPostponeProportion"
            :title="$t('cusEntry.bidSuperviseReport.publishPostponeProportion')"
            width="150"
          />
          <vxe-column
            field="acceptancePostponeCycle"
            :title="$t('cusEntry.bidSuperviseReport.acceptancePostponeCycle')"
            width="150"
          />
          <vxe-column
            field="acceptancePostponeProportion"
            :title="$t('cusEntry.bidSuperviseReport.acceptancePostponeProportion')"
            width="150"
          />
          <vxe-column
            field="sumReportPostponeCycle"
            :title="$t('cusEntry.bidSuperviseReport.sumReportPostponeCycle')"
            width="150"
          />
          <vxe-column
            field="sumReportPostponeProportion"
            :title="$t('cusEntry.bidSuperviseReport.sumReportPostponeProportion')"
            width="150"
          />
          <vxe-column
            field="winPostponeCycle"
            :title="$t('cusEntry.bidSuperviseReport.winPostponeCycle')"
            width="150"
          />
          <vxe-column
            field="winPostponeProportion"
            :title="$t('cusEntry.bidSuperviseReport.winPostponeProportion')"
            width="150"
          />
        </vxe-colgroup>
        <vxe-colgroup title="申请人">
          <vxe-column
            field="dataSubmitPostponeCycle"
            :title="$t('cusEntry.bidSuperviseReport.dataSubmitPostponeCycle')"
            width="150"
          />
          <vxe-column
            field="evaluationPostponeCycle"
            :title="$t('cusEntry.bidSuperviseReport.evaluationPostponeCycle')"
            width="150"
          />
          <vxe-column
            field="evaluationPostponeProportion"
            :title="$t('cusEntry.bidSuperviseReport.evaluationPostponeProportion')"
            width="150"
          />
          <vxe-column
            field="picketagePostponeCycle"
            :title="$t('cusEntry.bidSuperviseReport.picketagePostponeCycle')"
            width="150"
          />
          <vxe-column
            field="picketagePostponeProportion"
            :title="$t('cusEntry.bidSuperviseReport.picketagePostponeProportion')"
            width="150"
          />
        </vxe-colgroup>
        <vxe-column
          field="answerIssuedCount"
          :title="$t('cusEntry.bidSuperviseReport.answerIssuedCount')"
          width="150"
        />
        <vxe-column
          field="cancelReason"
          :title="$t('cusEntry.bidSuperviseReport.cancelReason')"
          width="150"
        />
      </vxe-table>

      <div class="report-pagination">
        <CPagination
          ref="queryPagination"
          style="margin: 0; padding-bottom: 4px;"
          class="c-query-table-pagination"
          :total="pageInfo.total"
          :page-num="pageInfo.currentPage"
          :page-size="pageInfo.pageSize"
          @current-change="changeCurrentIndex"
          @size-change="changeCurrentSize"
        />
      </div>

      <!-- 查看项目统计 -->
      <srm-dialog
        title="项目统计"
        :visible.sync="projectInfoVisible"
        size="large"
        append-to-body
        :close-on-click-modal="false"
      >
        <el-table
          border
          max-height="200"
          :data="projectInfoData"
        >
          <el-table-column
            align="center"
            type="index"
            :label="$t('common.sort')"
            width="50"
          />
          <el-table-column
            align="center"
            prop="bidNoticeWinVendor"
            :label="$t('cusEntry.bidSuperviseReport.bidNoticeWinVendor')"
            minWidth="100"
          />
          <el-table-column
            align="center"
            prop="bidNoticeWinVendorLinkMan"
            :label="$t('cusEntry.bidSuperviseReport.bidNoticeWinVendorLinkMan')"
            minWidth="100"
          />
          <el-table-column
            align="center"
            prop="bidNoticeWinVendorPhone"
            :label="$t('cusEntry.bidSuperviseReport.bidNoticeWinVendorPhone')"
            minWidth="100"
          />
          <el-table-column
            align="center"
            prop="bidNoticeWinVendorAmount"
            :label="$t('cusEntry.bidSuperviseReport.bidNoticeWinVendorAmount')"
            minWidth="100"
          />
        </el-table>
        <div slot="footer" class="dialog-footer">
          <el-button @click="projectInfoVisible = false">
            {{ $t('common.close') }}
          </el-button>
        </div>
      </srm-dialog>
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import ExportExcel from 'lib@/components/export-excel'
import CPagination from 'lib@/components/c-pagination'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'

export default {
  name: 'ProjectManagementReportList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    CPagination,
    ExportExcel
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      dictCodes: {
        requirementPlanType: 'PR_SOU_REQUIREMENT_FROM',
        extSouProcess: 'SOU_BID_PROCCESS',
        projectStatus: 'SOU_BIDDING_PRO_STATUS'
      },
      tableUrl: '/api-sou/extReportBid/bidSchedule/listPage',
      dynamicHeight: '400px',
      pageInfo: {
        currentPage: 1,
        pageSize: 15,
        total: 0
      },
      filterParams: {},
      tableData: [],
      queryParam: {},
      tableHeader: [
        {
          prop: 'requirementPlanType',
          label: this.$t('cusEntry.bidSuperviseReport.requirementPlanType'),
          dataType: 'dict',
          code: 'PR_SOU_REQUIREMENT_FROM',
          width: 150
        },
        {
          prop: 'extOrgBuName',
          label: this.$t('cusEntry.bidSuperviseReport.extOrgBuName'),
          width: 150
        },
        {
          prop: 'extOrgOuName',
          label: this.$t('cusEntry.bidSuperviseReport.extOrgOuName'),
          width: 150
        },
        {
          prop: 'companyShortCode',
          label: '公司代码',
          width: 150
        },
        {
          prop: 'year',
          label: '年',
          width: 150
        },
        {
          prop: 'month',
          label: '月',
          width: 150
        },
        {
          prop: 'extOrgBuCode',
          label: '板块代码',
          width: 150
        },
        {
          prop: 'extProjectNo',
          label: this.$t('cusEntry.bidSuperviseReport.extProjectNo'),
          width: 150
        },
        {
          prop: 'souName',
          label: this.$t('cusEntry.bidSuperviseReport.souName'),
          width: 150
        },
        {
          prop: 'extSouProcess',
          label: this.$t('cusEntry.bidSuperviseReport.extSouProcess'),
          width: 150
        },
        {
          prop: 'classification',
          label: this.$t('cusEntry.bidSuperviseReport.classification'),
          width: 150
        },
        {
          prop: 'extCategoryName',
          label: this.$t('cusEntry.bidSuperviseReport.extCategoryName'),
          width: 150
        },
        {
          prop: 'souPrincipal',
          label: this.$t('cusEntry.bidSuperviseReport.souPrincipal'),
          width: 150
        },
        {
          prop: 'vendorPrincipal',
          label: this.$t('cusEntry.bidSuperviseReport.vendorPrincipal'),
          width: 150
        },
        {
          prop: 'leaderPrincipal',
          label: this.$t('cusEntry.bidSuperviseReport.leaderPrincipal'),
          width: 150
        },
        {
          prop: 'extTechPrincipal',
          label: this.$t('cusEntry.bidSuperviseReport.extTechPrincipal'),
          width: 150
        },
        {
          prop: 'extScaleQuantity',
          label: this.$t('cusEntry.bidSuperviseReport.extScaleQuantity'),
          width: 150
        },
        {
          prop: 'totalBudget',
          label: this.$t('cusEntry.bidSuperviseReport.totalBudget'),
          width: 150
        },
        {
          prop: 'sendSouProfileEndDate',
          label: this.$t('cusEntry.bidSuperviseReport.sendSouProfileEndDate'),
          width: 150
        },
        {
          prop: 'approvalPassTime',
          label: this.$t('cusEntry.bidSuperviseReport.approvalPassTime'),
          width: 150
        },
        {
          prop: 'publicEndTime',
          label: this.$t('cusEntry.bidSuperviseReport.publicEndTime'),
          width: 150
        },
        {
          prop: 'planRequirementTime',
          label: this.$t('cusEntry.bidSuperviseReport.planRequirementTime'),
          width: 150
        },
        {
          prop: 'actualRequirementTime',
          label: this.$t('cusEntry.bidSuperviseReport.actualRequirementTime'),
          width: 150
        },
        {
          prop: 'planPublishTime',
          label: '计划发标时间',
          width: 150
        },
        {
          prop: 'actualPublishTime',
          label: '实际发标时间',
          width: 150
        },
        {
          prop: 'planAcceptanceBidTime',
          label: '计划收标时间',
          width: 150
        },
        {
          prop: 'actualAcceptanceBidTime',
          label: '实际收标时间',
          width: 150
        },
        {
          prop: 'planTechEvaluationTime',
          label: '计划技术标评完时间',
          width: 150
        },
        {
          prop: 'actualTechEvaluationTime',
          label: '实际技术标评完时间',
          width: 150
        },
        {
          prop: 'planSumReportTime',
          label: '计划上报时间',
          width: 150
        },
        {
          prop: 'actualSumReportTime',
          label: '实际上报时间',
          width: 150
        },
        {
          prop: 'planPicketageTime',
          label: '计划定标时间',
          width: 150
        },
        {
          prop: 'actualPicketageTime',
          label: '实际定标时间',
          width: 150
        },
        {
          prop: 'planPublishWinLossTime',
          label: '计划中标通知时间',
          width: 150
        },
        {
          prop: 'actualPublishWinLossTime',
          label: '实际中标通知时间',
          width: 150
        },
        {
          prop: 'honourScore',
          label: this.$t('cusEntry.bidSuperviseReport.honourScore'),
          width: 150
        },
        {
          prop: 'honourResult',
          label: this.$t('cusEntry.bidSuperviseReport.honourResult'),
          width: 150
        },
        {
          prop: 'projectStatus',
          label: this.$t('cusEntry.bidSuperviseReport.projectStatus'),
          width: 150
        },
        {
          prop: 'sendBidNumber',
          label: this.$t('cusEntry.bidSuperviseReport.sendBidNumber'),
          width: 150
        },
        {
          prop: 'sendBidAsSubmitNumber',
          label: this.$t('cusEntry.bidSuperviseReport.sendBidAsSubmitNumber'),
          width: 150
        },
        {
          prop: 'addBidNumber',
          label: this.$t('cusEntry.bidSuperviseReport.addBidNumber'),
          width: 150
        },
        {
          prop: 'addBidAsSubmitNumber',
          label: this.$t('cusEntry.bidSuperviseReport.addBidAsSubmitNumber'),
          width: 150
        },
        {
          prop: 'newVendorBidNumber',
          label: this.$t('cusEntry.bidSuperviseReport.newVendorBidNumber'),
          width: 150
        },
        {
          prop: 'newUniteVendorBidNumber',
          label: this.$t('cusEntry.bidSuperviseReport.newUniteVendorBidNumber'),
          width: 150
        },
        {
          prop: 'totalBidNumber',
          label: this.$t('cusEntry.bidSuperviseReport.totalBidNumber'),
          width: 150
        },
        {
          prop: 'totalBidAsSubmitNumber',
          label: this.$t('cusEntry.bidSuperviseReport.totalBidAsSubmitNumber'),
          width: 150
        },
        {
          prop: 'planTotalCycle',
          label: this.$t('cusEntry.bidSuperviseReport.planTotalCycle'),
          width: 150
        },
        {
          prop: 'actualTotalCycle',
          label: this.$t('cusEntry.bidSuperviseReport.actualTotalCycle'),
          width: 150
        },
        {
          prop: 'vendorPostponeCycle',
          label: this.$t('cusEntry.bidSuperviseReport.vendorPostponeCycle'),
          width: 150
        },
        {
          prop: 'publishPostponeCycle',
          label: this.$t('cusEntry.bidSuperviseReport.publishPostponeCycle'),
          width: 150
        },
        {
          prop: 'publishPostponeProportion',
          label: this.$t('cusEntry.bidSuperviseReport.publishPostponeProportion'),
          width: 150
        },
        {
          prop: 'acceptancePostponeCycle',
          label: this.$t('cusEntry.bidSuperviseReport.acceptancePostponeCycle'),
          width: 150
        },
        {
          prop: 'acceptancePostponeProportion',
          label: this.$t('cusEntry.bidSuperviseReport.acceptancePostponeProportion'),
          width: 150
        },
        {
          prop: 'sumReportPostponeCycle',
          label: this.$t('cusEntry.bidSuperviseReport.sumReportPostponeCycle'),
          width: 150
        },
        {
          prop: 'sumReportPostponeProportion',
          label: this.$t('cusEntry.bidSuperviseReport.sumReportPostponeProportion'),
          width: 150
        },
        {
          prop: 'winPostponeCycle',
          label: this.$t('cusEntry.bidSuperviseReport.winPostponeCycle'),
          width: 150
        },
        {
          prop: 'winPostponeProportion',
          label: this.$t('cusEntry.bidSuperviseReport.winPostponeProportion'),
          width: 150
        },
        {
          prop: 'dataSubmitPostponeCycle',
          label: this.$t('cusEntry.bidSuperviseReport.dataSubmitPostponeCycle'),
          width: 150
        },
        {
          prop: 'evaluationPostponeCycle',
          label: this.$t('cusEntry.bidSuperviseReport.evaluationPostponeCycle'),
          width: 150
        },
        {
          prop: 'evaluationPostponeProportion',
          label: this.$t('cusEntry.bidSuperviseReport.evaluationPostponeProportion'),
          width: 150
        },
        {
          prop: 'picketagePostponeCycle',
          label: this.$t('cusEntry.bidSuperviseReport.picketagePostponeCycle'),
          width: 150
        },
        {
          prop: 'picketagePostponeProportion',
          label: this.$t('cusEntry.bidSuperviseReport.picketagePostponeProportion'),
          width: 150
        },
        {
          prop: 'answerIssuedCount',
          label: this.$t('cusEntry.bidSuperviseReport.answerIssuedCount'),
          width: 150
        },
        {
          prop: 'cancelReason',
          label: this.$t('cusEntry.bidSuperviseReport.cancelReason'),
          width: 150
        }
      ],
      preArr: [
        {
          prop: 'requirementPlanType',
          label: '计划类型',
          type: 'dict',
          code: 'PR_SOU_REQUIREMENT_FROM'
        },
        {
          prop: 'extOrgBuName',
          label: '板块'
        },
        {
          prop: 'extOrgOuName',
          label: '公司'
        },
        {
          prop: 'companyShortCode',
          label: '公司代码'
        },
        {
          prop: 'year',
          label: '年'
        },
        {
          prop: 'month',
          label: '月'
        },
        {
          prop: 'extOrgBuCode',
          label: '板块代码'
        },
        {
          prop: 'extProjectNo',
          label: this.$t('cusEntry.bidSuperviseReport.extProjectNo')
        },
        {
          prop: 'souName',
          label: this.$t('cusEntry.bidSuperviseReport.souName')
        },
        {
          prop: 'extSouProcess',
          label: this.$t('cusEntry.bidSuperviseReport.extSouProcess'),
          type: 'dict',
          code: 'SOU_BID_PROCCESS'
        },
        {
          prop: 'extCategoryName',
          label: this.$t('cusEntry.bidSuperviseReport.extCategoryName')
        },
        {
          prop: 'souPrincipal',
          label: this.$t('cusEntry.bidSuperviseReport.souPrincipal')
        },
        {
          prop: 'vendorPrincipal',
          label: this.$t('cusEntry.bidSuperviseReport.vendorPrincipal')
        },
        {
          prop: 'totalBudgetFrom',
          label: '预算金额（≥N万元）'
        },
        {
          prop: 'totalBudgetTo',
          label: '预算金额（≤N万元）'
        },
        {
          prop: 'honourScoreFrom',
          label: '履约分数（≥N）'
        },
        {
          prop: 'honourScoreTo',
          label: '履约分数（≤N）'
        },
        {
          prop: 'honourResult',
          label: this.$t('cusEntry.bidSuperviseReport.honourResult')
        },
        {
          prop: 'projectStatus',
          label: this.$t('cusEntry.bidSuperviseReport.projectStatus'),
          type: 'dict',
          code: 'SOU_BIDDING_PRO_STATUS'
        },
        {
          prop: 'addBidNumber',
          label: '追加单位数量（≥N）'
        },
        {
          prop: 'addBidAsSubmitNumber',
          label: '追加单位投标数量（≥N）'
        },
        {
          prop: 'newVendorBidNumber',
          label: '新供应商数量（≥N）'
        },
        {
          prop: 'newUniteVendorBidNumber',
          label: '开发新单位数量（≥N）'
        },
        {
          prop: 'totalBidNumber',
          label: '总发标单位数量（≤N）'
        },
        {
          prop: 'totalBidAsSubmitNumber',
          label: '总投标单位数量（≤N）'
        },
        {
          prop: 'vendorPostponeCycle',
          label: '供应商推荐延期天数（≥N）'
        },
        {
          prop: 'acceptancePostponeCycle',
          label: '收标延期天数（≥N）'
        },
        {
          prop: 'sumReportPostponeCycle',
          label: '汇总上报延期天数（≥N）'
        },
        {
          prop: 'dataSubmitPostponeCycle',
          label: '资料递交延期天数（≥N）'
        },
        {
          prop: 'evaluationPostponeCycleFrom',
          label: '评标延期天数（≥N）'
        },
        {
          prop: 'evaluationPostponeCycleTo',
          label: '评标延期天数（≤N）'
        },
        {
          prop: 'picketagePostponeCycleFrom',
          label: '定标延期天数（≥N）'
        },
        {
          prop: 'picketagePostponeCycleTo',
          label: '定标延期天数（≤N）'
        },
        {
          prop: 'answerIssuedCount',
          label: '澄清次数（≥N）'
        }
      ],
      projectInfoVisible: false,
      projectInfoData: []

    }
  },
  updated () {
    // 判断是否全屏，切换高度
    window.addEventListener('fullscreenchange', (val) => {
      if (!this.checkFull()) {
        this.dynamicHeight = '400px'
      } else {
        this.dynamicHeight = '600px'
      }
    })
  },
  created () {
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    // 判断浏览器是否处于全屏状态
    checkFull () {
      // 火狐浏览器、谷歌浏览器及Webkit内核浏览器
      let isFull = document.mozFullScreen ||
                  document.fullScreen ||
                  document.webkitIsFullScreen ||
                  document.webkitRequestFullScreen ||
                  document.mozRequestFullScreen ||
                  document.msFullscreenEnabled
      if (isFull === undefined) {
        isFull = false
      }
      return isFull
    },
    syncFilterParams (values) {
      this.filterParams = values
    },
    changeCurrentIndex (currentNum) {
      this.pageInfo.currentPage = currentNum
      this.getQuerydata({ ...this.queryParam, isPage: true })
    },
    changeCurrentSize (currentSize) {
      this.pageInfo.pageSize = currentSize
      this.pageInfo.currentPage = 1
      this.getQuerydata({ ...this.queryParam, isPage: true })
    },
    getQuerydata (v = { isPage: true }) {
      const params = {}
      // 判断是点查询、重置还是页码、也条数变化
      if (!v.isPage) {
        this.pageInfo.currentPage = 1
      }
      Reflect.deleteProperty(v, 'isPage')
      const keys = Object.keys(v)
      if (keys.length) {
        keys.forEach(key => {
          if (v[key]) {
            params[key] = v[key]
          }
        })
      }
      this.queryParam = params || {}
      this.$http({
        url: '/api-sou/extReportBid/bidSchedule/listPage',
        method: 'POST',
        data: {
          pageNum: this.pageInfo.currentPage,
          pageSize: this.pageInfo.pageSize,
          __page: this.pageInfo.currentPage,
          __pagesize: this.pageInfo.pageSize,
          ...this.queryParam
        },
        loading: true
      }).then(res => {
        if (res && res.data) {
          this.tableData = res.data.list
          this.pageInfo.total = res.data.total
        }
      })
    },
    // 查看项目统计
    getProjectInfo (row) {
      this.projectInfoVisible = true
      this.projectInfoData = row.winVendorInfoList || []
    }
  }
}
</script>
<style lang="scss" scoped>
.report-pagination {
  position: fixed;
  bottom: 15px;
  right: 35px;
}
::v-deep .vxe-table--main-wrapper {
  padding-bottom: 17px !important;
}
::v-deep .el-main{
  margin-bottom: 20px;
}
</style>
