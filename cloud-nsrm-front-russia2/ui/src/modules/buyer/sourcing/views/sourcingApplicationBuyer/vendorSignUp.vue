<template>
  <el-container class="flex-container the_vendorSignUp_wrapper" direction="vertical">
    <el-main>
      <CWorkflowMulti
        ref="workflowMulti"
        v-model="activeTabName"
        :viewType="viewType"
        :fun-params="workflowParamsInfo"
        :button-config-info="buttonConfigInfo"
        :button-custom="buttonCustom"
        @tab-click="workflowView"
        @workflow-handler="workflowHandler"
        @click-handler="type => submit(type)"
        @submit-direct="type => submit(type)"
        @confirm="(type, comment) => submit(type, comment)"
        @close-tab="backOne"
      >
        <div class="the_progress">
          <div class="head-info">
            <div class="head-info-item">
              {{ sourcingInfo.souReqTitile }}
            </div>
            <div class="head-info-item">
              {{ $t('sourcingBuyer.reqHeadNo') }}：<span>{{ sourcingInfo.reqHeadNo }}</span>
            </div>
            <div class="head-info-item flex-block">
              <div>
                <span>{{ $t('sourcingBuyer.status') }}：{{ statusName }}</span>
                <span class="ml20">{{ $t('sourcingBuyer.expirationTime') }}：{{ $parseTime(sourcingInfo.expirationTime) }}</span>
                <span class="ml20">{{ $t('sourcingBuyer.creationDate1') }}：{{ $parseTime(sourcingInfo.creationDate) }}</span>
              </div>
              <div>{{ $t('sourcingBuyer.signUpCount') }}：（{{ signUpCount }}）</div>
            </div>
          </div>
          <div class="stepDiv">
            <el-steps :active="active" :align-center="true" finish-status="success">
              <el-step :title="$t('sourcingBuyer.published')" />
              <el-step :title="$t('sourcingBuyer.selected')" />
              <el-step :title="$t('sourcingBuyer.scoring')" />
              <el-step :title="$t('sourcingBuyer.submitted')" />
              <el-step :title="$t('sourcingBuyer.closed')" />
            </el-steps>
          </div>
        </div>
        <FormWrapper
          ref="formRef"
          :form-array="preArr"
          :init-active="true"
          @getFormData="getQuerydata"
          @synchronous-value="syncFilterParams"
        >
          <template #registCapital="{ scope }">
            <div class="form-item-line">
              <dict-select v-model="scope.registCapitalState" code="condition" :dictClass="dictClass" />
              <el-input v-model="scope.registCapital" type="number" />
            </div>
          </template>
          <template #companyCreationYear="{ scope }">
            <div class="form-item-line">
              <dict-select v-model="scope.companyCreationYearState" code="condition" :dictClass="dictClass" />
              <el-input v-model="scope.companyCreationYear" type="number" />
            </div>
          </template>
        </FormWrapper>
        <MainHeader>
          <template slot="left">
            <!-- 导出名单 -->
            <ExportExcel
              v-if="['PUBLISHED', 'SELECTED', 'SCORING'].includes(status)"
              v-loading
              page-url="/api-inq/inq/reqhead/listAllForPage"
              :filter-params="queryParam"
              :table-header="tableHeader"
              :dict-codes="dictCodes"
              :title="$t('sourcingBuyer.exportList')"
              timeout="1000000"
              export-mode="front"
            />
            <!-- 截止报名 -->
            <el-button v-if="['PUBLISHED'].includes(status)" type="primary" @click="stopOne">
              {{ $t('sourcingBuyer.closeApply') }}
            </el-button>
            <!-- 入围或淘汰 -->
            <el-dropdown v-if="['SELECTED'].includes(status)" style="margin: 0 10px;" @command="handleDropdownCommand">
              <el-button type="primary">
                {{ $t('sourcingBuyer.nextOrEliminate') }}
                <em class="el-icon-arrow-down el-icon--right" />
              </el-button>

              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item command="toNextRoundConfirm" :disabled="selecteds.length === 0">
                  {{ $t('sourcingBuyer.nextRound') }}
                </el-dropdown-item>

                <el-dropdown-item command="toEliminateConfirm" :disabled="selecteds.length === 0">
                  {{ $t('sourcingBuyer.eliminate') }}
                </el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
            <!-- 发布筛选结果 -->
            <el-button v-if="['SELECTED'].includes(status)" type="primary" @click="publishOne">
              {{ $t('sourcingBuyer.publishOne') }}
            </el-button>
            <!-- 发起询价 -->
            <el-button v-if="['SCORING'].includes(status)" type="primary" @click="inquiryOne">
              {{ $t('sourcingBuyer.inquiryOne') }}
            </el-button>
            <!-- 发起资质审查 -->
            <el-button
              v-if="['SCORING'].includes(status)"
              type="primary"
              :disabled="quaOfReviewDisabled"
              @click="quaOfReviewOne"
            >
              {{ $t('sourcingBuyer.quaOfReviewOne') }}
            </el-button>
          </template>
        </MainHeader>
        <el-table
          ref="list"
          key="vendorId"
          :data="tableData"
          style="width: 100%"
          border
          height="300"
          highlight-current-row
          @selection-change="handleSelectionChange"
        >
          <el-table-column
            v-if="['PUBLISHED', 'SELECTED'].includes(status)"
            :key="Math.random()"
            :selectable="checkboxSelect"
            type="selection"
            width="55"
            fixed
          />
          <!-- 报名供应商 -->
          <el-table-column
            :key="Math.random()"
            align="center"
            prop="vendorName"
            :label="$t('sourcingBuyer.vendorName')"
            fixed
          />
          <!-- 经营模式 -->
          <el-table-column :key="Math.random()" width="120">
            <template slot="header">
              <el-tooltip
                class="item"
                effect="dark"
                :content="sourcingInfo.manageModel ? $t('sourcingBuyer.reference') + sourcingInfo.manageModel : $t('sourcingBuyer.noReference')"
                placement="top"
              >
                <span>{{
                  $t('sourcingBuyer.applyManageModel')

                }}<i class="el-icon-info el-icon--right" />
                </span>
              </el-tooltip>
            </template>
            <template slot-scope="{row}">
              {{ row.manageModel }}
            </template>
          </el-table-column>
          <!-- 注册资本（万元） -->
          <el-table-column :key="Math.random()" align="center" width="120">
            <template slot="header">
              <el-tooltip
                class="item"
                effect="dark"
                :content="sourcingInfo.registCapital ? $t('sourcingBuyer.minReference') + sourcingInfo.registCapital + $t('sourcingBuyer.tenThousand') : $t('sourcingBuyer.noReference')"
                placement="top"
              >
                <span>{{ $t('sourcingBuyer.registCapital') }}<i class="el-icon-info el-icon--right" />
                </span>
              </el-tooltip>
            </template>
            <template slot-scope="{row}">
              {{ row.registCapital }}
            </template>
          </el-table-column>
          <!-- 企业所在地 -->
          <el-table-column :key="Math.random()" align="center" width="120">
            <template slot="header">
              <el-tooltip
                class="item"
                effect="dark"
                :content="sourcingInfo.postalAddress ? $t('sourcingBuyer.reference') + sourcingInfo.postalAddress : $t('sourcingBuyer.noReference')"
                placement="top"
              >
                <span>{{ $t('sourcingBuyer.postalAddress') }}<i class="el-icon-info el-icon--right" />
                </span>
              </el-tooltip>
            </template>
            <template slot-scope="{row}">
              {{ row.postalAddress }}
            </template>
          </el-table-column>
          <!-- 具有的品牌代理资质 -->
          <el-table-column :key="Math.random()" align="center" width="120">
            <template slot="header">
              <el-tooltip
                class="item"
                effect="dark"
                :content="sourcingInfo.agentQualifiedBrand ? $t('sourcingBuyer.reference') + sourcingInfo.agentQualifiedBrand : $t('sourcingBuyer.noReference')"
                placement="top"
              >
                <span>{{
                  $t('sourcingBuyer.applyAgentQualifiedBrand')

                }}<i class="el-icon-info el-icon--right" />
                </span>
              </el-tooltip>
            </template>
            <template slot-scope="{row}">
              {{ row.agentQualifiedBrand }}
            </template>
          </el-table-column>
          <!-- 供货区域 -->
          <el-table-column :key="Math.random()" align="center" width="120">
            <template slot="header">
              <el-tooltip
                class="item"
                effect="dark"
                :content="sourcingInfo.supplyArea ? $t('sourcingBuyer.reference') + sourcingInfo.supplyArea : $t('sourcingBuyer.noReference')"
                placement="top"
              >
                <span>{{
                  $t('sourcingBuyer.applySupplyArea')

                }}<i class="el-icon-info el-icon--right" />
                </span>
              </el-tooltip>
            </template>
            <template slot-scope="{row}">
              {{ row.supplyArea }}
            </template>
          </el-table-column>
          <!-- 公司成立年限（年） -->
          <el-table-column :key="Math.random()" align="center" width="120">
            <template slot="header">
              <el-tooltip
                class="item"
                effect="dark"
                :content="sourcingInfo.companyCreationYear ? $t('sourcingBuyer.minReference') + sourcingInfo.companyCreationYear + '年' : $t('sourcingBuyer.noReference')"
                placement="top"
              >
                <span>{{
                  $t('sourcingBuyer.companyCreationYear')

                }}<i class="el-icon-info el-icon--right" />
                </span>
              </el-tooltip>
            </template>
            <template slot-scope="{row}">
              {{ row.companyCreationYear }}
            </template>
          </el-table-column>
          <!-- 联系人 -->
          <el-table-column
            :key="Math.random()"
            align="center"
            prop="contactName"
            :label="$t('sourcingBuyer.contactName')"
            width="120"
          />
          <!-- 电话 -->
          <el-table-column
            :key="Math.random()"
            align="center"
            prop="phone"
            :label="$t('sourcingBuyer.phone1')"
            width="120"
          />
          <!-- 邮箱 -->
          <el-table-column
            :key="Math.random()"
            align="center"
            prop="email"
            :label="$t('sourcingBuyer.email')"
            width="120"
          />
          <!-- 拒绝理由 -->
          <el-table-column
            :key="Math.random()"
            align="center"
            prop="refuseReason"
            :label="$t('sourcingBuyer.refuseReason1')"
            width="120"
          />
          <!-- 报名时间 -->
          <el-table-column
            :key="Math.random()"
            align="center"
            prop="creationDate"
            :label="$t('sourcingBuyer.signUpTime')"
            width="120"
          >
            <template slot-scope="{row}">
              {{ row.applyStatus === 'SIGN' ? $parseTime(row.creationDate) : '' }}
            </template>
          </el-table-column>
          <!-- 报名详情 -->
          <el-table-column
            :key="Math.random()"
            align="center"
            :label="$t('sourcingBuyer.signUpDetail')"
            width="120"
            fixed="right"
          >
            <template v-if="row.applyStatus === 'SIGN'" slot-scope="{row}">
              <el-button type="text" @click="readOne(row)">
                {{ $t('sourcingBuyer.viewDetail') }}
              </el-button>
            </template>
          </el-table-column>
          <!-- 询价单号 -->
          <el-table-column
            v-if="['SCORING', 'SUBMITTED'].includes(status) && hasInquiryNo > -1"
            :key="Math.random()"
            align="center"
            :label="$t('sourcingBuyer.inquiryNo')"
            prop="inquiryNo"
            width="120"
          >
            <template v-if="row.applyStatus === 'SIGN'" slot-scope="{row}">
              <el-button v-if="row.inquiryNo" type="text" @click="readInquiryDetail(row)">
                {{ row.inquiryNo }}
              </el-button>
            </template>
          </el-table-column>
          <!-- 询价状态  -->
          <el-table-column
            v-if="['SCORING', 'SUBMITTED'].includes(status) && hasInquiryNo > -1"
            :key="Math.random()"
            align="center"
            :label="$t('sourcingBuyer.inquiryStatus')"
            prop="inquiryStatus"
            width="120"
          >
            <template v-if="row.applyStatus === 'SIGN'" slot-scope="{row}">
              {{ $getDictLabel('RFQ_STATUS', row.inquiryStatus) }}
            </template>
          </el-table-column>
          <!-- 报名状态 -->
          <el-table-column
            :key="Math.random()"
            align="center"
            prop="applyStatus"
            :label="$t('sourcingBuyer.applyStatus')"
            width="100"
            fixed="right"
          >
            <template slot-scope="{row}">
              {{ $getDictLabel('APPLY_STATUS', row.applyStatus) }}
            </template>
          </el-table-column>
          <!-- 筛选结果 -->
          <el-table-column
            v-if="['SELECTED', 'SCORING', 'SUBMITTED'].includes(status)"
            :key="Math.random()"
            prop="selectStatus"
            :label="$t('sourcingBuyer.filterResults')"
            width="100"
            fixed="right"
          >
            <template v-if="row.applyStatus === 'SIGN'" slot-scope="{row}">
              {{ $getDictLabel('APPLY_HEAD_STATUS', row.selectStatus) }}
            </template>
          </el-table-column>
          <!-- 排名 -->
          <el-table-column
            v-if="['SCORING', 'SUBMITTED'].includes(status) && hasInquiryNo > -1"
            :key="Math.random()"
            align="center"
            prop="ranking"
            :label="$t('sourcingBuyer.ranking')"
            width="120"
          >
            <template v-if="row.applyStatus === 'SIGN'" slot-scope="{row}">
              {{ row.ranking }}
            </template>
          </el-table-column>
          <!-- 评分 -->
          <el-table-column
            v-if="['SCORING', 'SUBMITTED'].includes(status)"
            :key="Math.random()"
            align="center"
            prop="score"
            :label="$t('sourcingBuyer.score')"
            width="80"
            fixed="right"
            :render-header="_addStarToColumn"
          >
            <template v-if="row.applyStatus === 'SIGN' && row.selectStatus === 'WIN'" slot-scope="{row}">
              <template v-if="['SCORING'].includes(status)">
                <el-input-number v-model="row.score" :controls="false" :min="0" class="input-number-precision" />
              </template>
              <template v-if="['SUBMITTED'].includes(status)">
                {{ row.score }}
              </template>
            </template>
          </el-table-column>
          <!-- 评分依据 -->
          <el-table-column
            v-if="['SCORING', 'SUBMITTED'].includes(status)"
            :key="Math.random()"
            align="center"
            prop="scoreReason"
            :label="$t('sourcingBuyer.scoreReason')"
            width="120"
            fixed="right"
            :render-header="_addStarToColumn"
          >
            <template v-if="row.applyStatus === 'SIGN' && row.selectStatus === 'WIN'" slot-scope="{row}">
              <template v-if="['SCORING'].includes(status)">
                <el-input v-model="row.scoreReason" />
              </template>
              <template v-if="['SUBMITTED'].includes(status)">
                {{ row.scoreReason }}
              </template>
            </template>
          </el-table-column>
          <!-- 资质审查单号 -->
          <el-table-column
            v-if="['SCORING', 'SUBMITTED'].includes(status)"
            :key="Math.random()"
            align="center"
            :label="$t('sourcingBuyer.reviewFormNumber')"
            prop="reviewFormNumber"
            width="120"
          >
            <template v-if="row.applyStatus === 'SIGN'" slot-scope="{row}">
              <el-button v-if="row.reviewFormNumber" type="text" @click="readQuaOfReviewDetail(row)">
                {{ row.reviewFormNumber }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <CToolbar v-if="viewType === 'SINGLE'">
          <template slot="right">
            <el-button @click="backOne">
              {{ $t('common.backTo') }}
            </el-button>
          </template>
        </CToolbar>
        <!-- 资质审查 -->
        <srm-dialog
          :title="$t('sourcingBuyer.review')"
          size="large"
          :visible.sync="dialogVisible"
          :close-on-click-modal="false"
        >
          <el-table
            :data="reviewTableData"
            style="width: 100%"
            border
            height="345px"
            highlight-current-row
            @selection-change="handleDialogSelection"
          >
            <el-table-column type="selection" width="55" />
            <el-table-column align="center" type="index" :label="$t('common.sort')" width="60" />
            <!-- 供应商编码 -->
            <el-table-column prop="vendorCode" :label="$t('sourcingBuyer.vendorCode')" min-width="120" />
            <!-- 供应商名称 -->
            <el-table-column prop="vendorName" :label="$t('sourcingBuyer.vendorName1')" min-width="120" />
          </el-table>
          <div slot="footer" class="dialog-footer">
            <el-button @click="handleDialogCancel">
              <!-- 取 消 -->
              {{ $t('common.cancel') }}
            </el-button>
            <el-button type="primary" :loading="submitLoading" @click="handleDialogComfirm">
              <!-- 确 定 -->
              {{ $t('common.confirm') }}
            </el-button>
          </div>
        </srm-dialog>
      </CWorkflowMulti>
    </el-main>
  </el-container>
</template>

<script>
import CToolbar from 'lib@/components/c-toolbar'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { tabTodoMixin } from '@/utils/mixins'
import ExportExcel from 'lib@/components/export-excel'
import WorkflowCommon from '@/library/mixins/workflow-common'
import sourcingApplicationDetail from 'mods@/sourcingSupplier/views/sourcingApplicationSupplier/sourcingApplicationDetail'
import quaOfReviewDetail from 'modb@/vendorManagementBuyer/views/quaOfReview/quaOfReviewDetail'
import inquiryDetail from 'modb@/inquiry/views/inquiryManagement/inquiryDetail.vue'
import { createDictClass } from '@/library/utils/dict/dict-utils'
import { sourcing } from 'modb@/sourcing/api'

const dictClass = createDictClass(
  {
    condition: [
      { id: 1, value: 'gt', label: this.$t('components.condition.gt') },  // '大于'
      { id: 2, value: 'eq', label: this.$t('components.condition.eq') },  // '等于'
      { id: 3, value: 'lt', label: this.$t('components.condition.lt') }  // '小于'
    ]
  },
  false,
)

export default {
  name: 'VendorBiddingSignUp',
  components: {
    MainHeader,
    FormWrapper,
    CToolbar,
    ExportExcel
  },
  mixins: [tabTodoMixin, WorkflowCommon],
  data () {
    return {
      dictClass: dictClass,
      dictCodes: {
        inquiryStatus: 'RFQ_STATUS',
        applyStatus: 'APPLY_STATUS',
        selectStatus: 'APPLY_HEAD_STATUS'
      },
      tableHeader: [],
      reviewTableData: [],
      sourcingForm: {},
      sourcingInfo: {},
      status: '',
      signUpCount: 0,
      tableData: [],
      selecteds: [],
      quaOfReviewSelecteds: [],
      hasInquiryNo: -1,
      preArr: [
        // 企业名称
        {
          prop: 'vendorName',
          label: this.$t('sourcingBuyer.vendorName2')
        },
        // 注册资本（万元）
        {
          prop: 'registCapital',
          label: this.$t('sourcingBuyer.registCapital'),
          type: 'slot',
          slot: 'registCapital'
        },
        // 企业所在地
        {
          prop: 'postalAddress',
          label: this.$t('sourcingBuyer.postalAddress')
        },
        // 报名状态
        {
          prop: 'applyStatus',
          label: this.$t('sourcingBuyer.applyStatus'),
          type: 'dict',
          code: 'APPLY_STATUS'
        },
        // 公司成立年限（年）
        {
          prop: 'companyCreationYear',
          label: this.$t('sourcingBuyer.companyCreationYear'),
          type: 'slot',
          slot: 'companyCreationYear'
        }
      ],
      queryParam: {},
      dialogVisible: false,
      submitLoading: false,
      flowId: null
    }
  },
  computed: {
    active () {
      return ['PUBLISHED', 'SELECTED', 'SCORING', 'SUBMITTED', 'CLOSED'].indexOf(this.status)
    },
    statusName () {
      return [
        this.$t('sourcingBuyer.published'),
        this.$t('sourcingBuyer.selected'),
        this.$t('sourcingBuyer.scoring'),
        this.$t('sourcingBuyer.submitted'),
        this.$t('sourcingBuyer.closed')
      ][this.active]
    },
    quaOfReviewDisabled () {
      return (
        this.tableData.findIndex(item => item.selectStatus === 'WIN' && !item.reviewFormNumber) ===
        -1
      )
    },
    workflowBusinessId () {
      // 用来指定工作流的业务ID
      return this.status === 'SUBMITTED' ? this.sourcingInfo.reqHeadId : this.flowId
    },
    workflowTabDisabled () {
      return !['SCORING', 'SUBMITTED'].includes(this.status)
    },
    viewType () {
      return ['SCORING', 'SUBMITTED'].includes(this.status) ? 'WORKFLOW' : 'SINGLE'
    },
    viewUpdateButton () {
      return this.status === 'SCORING'
    }
  },
  watch: {
    viewUpdateButton () {
      this.buttonConfigInfo.save.view = this.viewUpdateButton
      this.buttonConfigInfo.submit.view = this.viewUpdateButton
    }
  },
  async created () {
    let { reqHeadId } = this.$attrs.params.row
    await this.getFormDetail(reqHeadId)

    this.buttonConfigInfo.cancel.view = false
    this.buttonConfigInfo.save.view = this.viewUpdateButton
    // '暂存'
    this.buttonConfigInfo.save.name = this.$t('common.staging')
    this.buttonConfigInfo.submit.view = this.viewUpdateButton
    this.buttonConfigInfo.submit.name = this.$t('sourcingBuyer.initiateApproval')

    this.tableHeader = [
      {
        // '报名供应商'
        label: this.$t('sourcingBuyer.vendorName'),
        prop: 'vendorName'
      },
      {
        // '经营模式'
        label: this.$t('sourcingBuyer.applyManageModel'),
        prop: 'manageModel'
      },
      {
        // '注册资本（万元'
        label: this.$t('cusEntry.supplement20250211.capitalContribution'),
        prop: 'registCapital'
      },
      {
        // '企业所在地'
        label: this.$t('sourcingBuyer.postalAddress'),
        prop: 'postalAddress'
      },
      {
        // '具有的品牌代理资质'
        label: this.$t('cusEntry.supplement20250211.brandAgencyQualification'),
        prop: 'agentQualifiedBrand'
      },
      {
        // '供货区域 '
        label: this.$t('cusEntry.supplement20250211.supplyArea'),
        prop: 'supplyArea'
      },
      {
        // '公司成立年限（年）'
        label: this.$t('sourcingBuyer.companyCreationYear'),
        prop: 'companyCreationYear'
      },
      {
        // '联系人'
        label: this.$t('vendorMod.contactPerson'),
        prop: 'contactName'
      },
      {
        // '电话'
        label: this.$t('common.phone'),
        prop: 'phone'
      },
      {
        // '邮箱'
        label: this.$t('common.email'),
        prop: 'email'
      },
      {
        // '拒绝理由'
        label: this.$t('sourcingBuyer.refuseReason1'),
        prop: 'refuseReason'
      },
      {
        // '报名时间'
        label: this.$t('sourcingBuyer.signUpTime'),
        prop: 'creationDate',
        dataType: 'dateTime'
      },
      {
        // '询价单号'
        label: this.$t('bidMod.inquiryNo'),
        prop: 'inquiryNo'
      },
      {
        // '询价状态'
        label: this.$t('bidMod.inquiryStatus'),
        prop: 'inquiryStatus'
      },
      {
        // '报名状态'
        label: this.$t('bidMod.signUpStatus'),
        prop: 'applyStatus'
      },
      {
        // '筛选结果'
        label: this.$t('sourcingBuyer.filterResults'),
        prop: 'selectStatus'
      },
      {
        // '排名'
        label: this.$t('bidMod.rank'),
        prop: 'ranking'
      },
      {
        // '评分'
        label: this.$t('bidMod.score1'),
        prop: 'score'
      },
      {
        // '评分依据'
        label: this.$t('sourcingBuyer.scoreReason'),
        prop: 'scoreReason'
      },
      {
        // '资质审查单号'
        label: this.$t('vendorMod.quaNum'),
        prop: 'reviewFormNumber'
      }
    ]

    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    checkboxSelect (row) {
      return row.applyStatus === 'SIGN' && this.status === 'SELECTED'
    },
    // 指定工作流的业务类型，在定义工作流时指定
    async getWorkflowBusinessType () {
      return 'souReqApply'
    },
    getCWorkflowRefName () {
      return 'workflowMulti' // 对应CWorkflowMulti标签中的ref
    },
    async getFormDetail (reqHeadId) {
      let res = await sourcing.getFormDetail({ id: reqHeadId })
      if (res.data) {
        this.sourcingForm = res.data
        this.sourcingInfo = res.data
        this.status = res.data.status
      }
    },
    syncFilterParams (values) {
      this.queryParam = { ...values, reqHeadId: this.sourcingInfo.reqHeadId }
    },
    async getQuerydata (obj) {
      let objs = obj || this.queryParam
      this.queryParam = { ...objs }
      this.queryParam.reqHeadId = this.sourcingInfo.reqHeadId

      let res = await sourcing.reqHeadListAll(this.queryParam)
      if (res.data) {
        this.tableData = res.data.list
        this.signUpCount = this.tableData.filter(item => item.applyStatus === 'SIGN').length || 0 // 报名人数
        // 是否存在询价单号
        this.hasInquiryNo = this.tableData.findIndex(item => {
          return !!item.inquiryNo
        })
      }
    },
    // 截止报名
    stopOne () {
      this.$confirm(this.$t('sourcingBuyer.confirmStop'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(async () => {
        await sourcing.stopSignUp(this.sourcingInfo.reqHeadId)
        this.$message.success(this.$t('sourcingBuyer.successStop'))
        this.status = 'SELECTED' // 入围筛选中
        this.updateExpirationTime() // 更新截止时间
      })
    },
    async updateExpirationTime () {
      let res = await sourcing.reqHeadListPage({
        reqHeadNo: this.sourcingInfo.reqHeadNo
      })
      if (res.data) {
        this.sourcingInfo.expirationTime = res.data.list[0].expirationTime
      }
    },
    readOne (row) {
      this.$emit('tab-add', {
        component: sourcingApplicationDetail,
        params: {
          flag: 'buyerView',
          row: row,
          showType: 'readOnly',
          tabName: 'sourcingApplicationDetail' + row.applyId
        },
        title: row.applyId,
        name: 'sourcingApplicationDetail' + row.applyId
      })
    },
    handleSelectionChange (val) {
      this.selecteds = val
    },
    async handleDropdownCommand (command) {
      if (this.selecteds.length === 0) {
        this.$message.error(this.$t('sourcingBuyer.vendorIsRequired'))
        return
      }
      let data = {
        reqHeadId: this.sourcingInfo.reqHeadId,
        applyIds: this.selecteds.map(item => item.applyId),
        toWin: command === 'toNextRoundConfirm' // true-入围 false-淘汰
      }
      await sourcing.winOrLose(data)
      this.$message.success(this.$t('sourcingBuyer.handleSuccess'))
      this.getQuerydata()
    },
    async publishOne () {
      await sourcing.publish(this.sourcingInfo.reqHeadId)
      this.$message.success(this.$t('sourcingBuyer.publishSuccess'))
      this.status = 'SCORING' // 评分中
    },
    async inquiryOne () {
      let res = await sourcing.createSouInquiry(this.sourcingInfo.reqHeadId)
      if (res.data && res.data.inquiryNo) {
        this.$confirm(this.$t('sourcingBuyer.inquirySuccess'), {
          confirmButtonText: this.$t('common.confirm'),
          cancelButtonText: this.$t('common.cancel'),
          type: 'warning'
        }).then(() => {
          this.readInquiryDetail(res.data)
          this.getQuerydata()
        })
      }
    },
    quaOfReviewOne () {
      this.reviewTableData = this.tableData.filter(
        item => item.selectStatus === 'WIN' && !item.reviewFormNumber,
      )
      this.dialogVisible = true
    },
    handleDialogSelection (val) {
      this.quaOfReviewSelecteds = val
    },
    handleDialogCancel () {
      this.dialogVisible = false
    },
    async handleDialogComfirm () {
      this.submitLoading = true
      let data = this.quaOfReviewSelecteds.map(item => item.vendorId)
      try {
        await sourcing.createReviewForm(data, this.sourcingInfo.reqHeadId)
        this.submitLoading = false
        this.$message.success(this.$t('sourcingBuyer.quaOfReviewSuccess'))
        this.dialogVisible = false
        this.$nextTick(() => {
          this.getQuerydata()
        })
      } catch (error) {
        this.submitLoading = false
      }
    },
    readInquiryDetail (row) {
      row = { ...row, projectId: row.inquiryId, souNo: row.inquiryNo }
      this.$emit('tab-add', {
        component: inquiryDetail,
        params: {
          flag: 'edit',
          readOnly: false,
          row,
          tabName: row.souNo || ''
        },
        title: row.souNo,
        name: row.souNo || ''
      })
    },
    readQuaOfReviewDetail (row) {
      this.$emit('tab-add', {
        component: quaOfReviewDetail,
        params: {
          flag: 'edit',
          row: row,
          tabName: 'quaOfReviewDetail' + row.reviewFormNumber
        },
        title: row.reviewFormNumber,
        name: 'quaOfReviewDetail' + row.reviewFormNumber
      })
    },
    submit (type) {
      if (type === 'SAVE') {
        this.stagingOne()
      } else if (type === 'SUBMIT') {
        this.submitOne(type)
      }
    },
    async submitOne (type) {
      let data = this.tableData
        .filter(item => item.applyStatus === 'SIGN' && item.selectStatus === 'WIN')
        .map(item => {
          return {
            reqHeadId: item.reqHeadId,
            applyId: item.applyId,
            score: item.score,
            scoreReason: item.scoreReason,
            status: item.selectStatus
          }
        })
      if (data.length) {
        await sourcing.score(data, this.sourcingInfo.reqHeadId)
        this.flowId = this.sourcingInfo.reqHeadId
        await this.handlerAfter(type) // 触发流程激活 (流程相关)
      } else {
        // '只能对已报名且入围的供应商进行评分'
        this.$message.error(this.$t('sourcingBuyer.scoreTip'))
      }
    },
    backOne () {
      this.$emit('tab-remove', this.$attrs.params.tabName)
      this.__setTabTodo('sourcingApplicationList.getQuerydata')
    },
    async stagingOne () {
      let data = this.tableData
        .filter(item => item.applyStatus === 'SIGN' && item.selectStatus === 'WIN')
        .map(item => {
          return {
            reqHeadId: item.reqHeadId,
            applyId: item.applyId,
            score: item.score,
            scoreReason: item.scoreReason,
            status: item.status
          }
        })
      if (data.length) {
        await sourcing.score(data, this.sourcingInfo.reqHeadId)
        this.$message.success(this.$t('sourcingBuyer.stagingSuccess'))
      } else {
        // '只能对已报名且入围的供应商进行评分'
        this.$message.error(this.$t('sourcingBuyer.scoreTip'))
      }
    }
  }
}
</script>
<style scoped lang="scss">
.the_vendorSignUp_wrapper {
  padding-bottom: 40px;

  .the_progress {
    width: 100%;
    margin-bottom: 20px;
    padding: 10px 20px 20px;
    background: #eee;

    .head-info {
      font-size: 14px;
      margin-bottom: 30px;
    }

    .head-info-item {
      margin-top: 10px;
    }

    .flex-block {
      display: flex;
      justify-content: space-between;
    }

    .ml20 {
      margin-left: 20px;
    }
  }

  .input-number-precision {
    width: 100%;

    :deep(.el-input__inner) {
      text-align: left;
      padding-left: 8px;
    }
  }

  .form-item-line {
    display: flex;
    align-items: center;
  }
}
</style>
