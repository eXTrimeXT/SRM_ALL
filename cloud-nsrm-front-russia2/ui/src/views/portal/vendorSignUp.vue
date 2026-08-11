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
        @click-handler="type => submitOne(type)"
        @submit-direct="type => submitOne(type)"
        @confirm="(type, comment) => submitOne(type, comment)"
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
          <el-steps :active="active">
            <el-step :title="$t('sourcingBuyer.published')" />
            <el-step :title="$t('sourcingBuyer.selected')" />
            <el-step :title="$t('sourcingBuyer.scoring')" />
            <el-step :title="$t('sourcingBuyer.submitted')" />
            <el-step :title="$t('sourcingBuyer.closed')" />
          </el-steps>
        </div>
        <FormWrapper
          ref="formRef"
          :form-array="preArr"
          :init-active="true"
          @getFormData="getQuerydata"
          @synchronous-value="syncFilterParams"
        >
          <template #registCapital="{scope}">
            <div class="form-item-line">
              <el-select v-model="scope.registCapitalState" filterable>
                <el-option
                  v-for="item in condition"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
              <el-input v-model="scope.registCapital" type="number" :disabled="isReadOnly" />
            </div>
          </template>
          <template #companyCreationYear="{scope}">
            <div class="form-item-line">
              <el-select v-model="scope.companyCreationYearState" filterable>
                <el-option
                  v-for="item in condition"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
              <el-input v-model="scope.companyCreationYear" type="number" :disabled="isReadOnly" />
            </div>
          </template>
        </FormWrapper>
        <MainHeader>
          <template slot="left">
            <!-- 导出名单 -->
            <ExportExcel
              v-if="['PUBLISHED','SELECTED','SCORING'].includes(status)"
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
            <el-button
              v-if="['PUBLISHED'].includes(status)"
              type="primary"
              @click="stopOne"
            >
              {{ $t('sourcingBuyer.closeApply') }}
            </el-button>
            <!-- 入围或淘汰 -->
            <el-dropdown
              v-if="['SELECTED'].includes(status)"
              style="margin: 0 10px;"
              @command="handleDropdownCommand"
            >
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
            <el-button
              v-if="['SELECTED'].includes(status)"
              type="primary"
              @click="publishOne"
            >
              {{ $t('sourcingBuyer.publishOne') }}
            </el-button>
            <!-- 发起询价 -->
            <el-button
              v-if="['SCORING'].includes(status)"
              type="primary"
              @click="inquiryOne"
            >
              {{ $t('sourcingBuyer.inquiryOne') }}
            </el-button>
            <!-- 发起资质审查 -->
            <el-button
              v-if="['SCORING'].includes(status)"
              type="primary"
              @click="quaOfReviewOne"
            >
              {{ $t('sourcingBuyer.quaOfReviewOne') }}
            </el-button>
          </template>
        </MainHeader>
        <el-table
          :key="reqHeadId"
          :data="tableData"
          style="width: 100%"
          border
          height="300"
          highlight-current-row
          @selection-change="handleSelectionChange"
        >
          <el-table-column v-if="['SELECTED'].includes(status)" type="selection" width="55" />
          <el-table-column align="center" type="index" :label="$t('common.sort')" width="50" />
          <el-table-column align="center" prop="vendorName" :label="$t('sourcingBuyer.vendorName')" fixed />
          <el-table-column width="120">
            <template slot="header">
              <el-tooltip
                class="item"
                effect="dark"
                :content="sourcingInfo.manageModel ? $t('sourcingBuyer.reference') + sourcingInfo.manageModel : $t('sourcingBuyer.noReference')"
                placement="top"
              >
                <span>{{ $t('sourcingBuyer.applyManageModel') }}<i class="el-icon-info el-icon--right" /> </span>
              </el-tooltip>
            </template>
            <template slot-scope="{row}">
              {{ row.manageModel }}
            </template>
          </el-table-column>
          <el-table-column align="center" width="120">
            <template slot="header">
              <el-tooltip
                class="item"
                effect="dark"
                :content="sourcingInfo.registCapital ? $t('sourcingBuyer.minReference')+ sourcingInfo.registCapital + $t('sourcingBuyer.tenThousand') : $t('sourcingBuyer.noReference')"
                placement="top"
              >
                <span>{{ $t('sourcingBuyer.registCapital') }}<i class="el-icon-info el-icon--right" /> </span>
              </el-tooltip>
            </template>
            <template slot-scope="{row}">
              {{ row.registCapital }}
            </template>
          </el-table-column>
          <el-table-column align="center" width="120">
            <template slot="header">
              <el-tooltip
                class="item"
                effect="dark"
                :content="sourcingInfo.postalAddress ? $t('sourcingBuyer.reference')+ sourcingInfo.postalAddress : $t('sourcingBuyer.noReference')"
                placement="top"
              >
                <span>{{ $t('sourcingBuyer.postalAddress') }}<i class="el-icon-info el-icon--right" /> </span>
              </el-tooltip>
            </template>
            <template slot-scope="{row}">
              {{ row.postalAddress }}
            </template>
          </el-table-column>
          <el-table-column align="center" width="120">
            <template slot="header">
              <el-tooltip
                class="item"
                effect="dark"
                :content="sourcingInfo.agentQualifiedBrand ? $t('sourcingBuyer.reference')+ sourcingInfo.agentQualifiedBrand : $t('sourcingBuyer.noReference')"
                placement="top"
              >
                <span>{{ $t('sourcingBuyer.applyAgentQualifiedBrand') }}<i class="el-icon-info el-icon--right" /> </span>
              </el-tooltip>
            </template>
            <template slot-scope="{row}">
              {{ row.agentQualifiedBrand }}
            </template>
          </el-table-column>
          <el-table-column align="center" width="120">
            <template slot="header">
              <el-tooltip
                class="item"
                effect="dark"
                :content="sourcingInfo.supplyArea ? $t('sourcingBuyer.reference')+ sourcingInfo.supplyArea : $t('sourcingBuyer.noReference')"
                placement="top"
              >
                <span>{{ $t('sourcingBuyer.applySupplyArea') }}<i class="el-icon-info el-icon--right" /> </span>
              </el-tooltip>
            </template>
            <template slot-scope="{row}">
              {{ row.supplyArea }}
            </template>
          </el-table-column>
          <el-table-column align="center" width="120">
            <template slot="header">
              <el-tooltip
                class="item"
                effect="dark"
                :content="sourcingInfo.companyCreationYear ? $t('sourcingBuyer.minReference') + sourcingInfo.companyCreationYear + $t('time.years') : $t('sourcingBuyer.noReference')"
                placement="top"
              >
                <span>{{ $t('sourcingBuyer.companyCreationYear') }}<i class="el-icon-info el-icon--right" /> </span>
              </el-tooltip>
            </template>
            <template slot-scope="{row}">
              {{ row.companyCreationYear }}
            </template>
          </el-table-column>
          <el-table-column align="center" prop="contactName" :label="$t('sourcingBuyer.contactName')" width="120" />
          <el-table-column align="center" prop="phone" :label="$t('sourcingBuyer.phone1')" width="120" />
          <el-table-column align="center" prop="email" :label="$t('sourcingBuyer.email')" width="120" />
          <el-table-column align="center" prop="applyStatus" :label="$t('sourcingBuyer.applyStatus')" width="120">
            <template slot-scope="{row}">
              {{ $getDictLabel('APPLY_STATUS', row.applyStatus) }}
            </template>
          </el-table-column>
          <el-table-column align="center" prop="refuseReason" :label="$t('sourcingBuyer.refuseReason1')" width="120" />
          <el-table-column align="center" prop="creationDate" :label="$t('sourcingBuyer.signUpTime')" :formatter="(row, column, cellValue) => $parseTime(cellValue)" width="120" />
          <el-table-column
            align="center"
            :label="$t('sourcingBuyer.signUpDetail')"
            width="120"
            :fixed="['SELECTED'].includes(status) ?'right' : false"
          >
            <template slot-scope="{row}">
              <el-button type="text" @click="readOne(row)">
                {{ $t('sourcingBuyer.viewDetail') }}
              </el-button>
            </template>
          </el-table-column>
          <el-table-column
            v-if="['SELECTED','SCORING','SUBMITTED'].includes(status)"
            prop="status"
            :label="$t('sourcingBuyer.filterResults')"
            width="120"
            :fixed="['SELECTED'].includes(status) ?'right' : false"
          >
            <template slot-scope="{row}">
              {{ $getDictLabel('APPLY_HEAD_STATUS', row.status) }}
            </template>
          </el-table-column>
          <el-table-column
            v-if="['SCORING','SUBMITTED'].includes(status) && hasInquiryNo"
            align="center"
            :label="$t('sourcingBuyer.inquiryNo')"
            prop="inquiryNo"
            width="120"
          >
            <template slot-scope="{row}">
              <el-button v-if="row.inquiryNo" type="text" @click="readInquiryDetail(row)">
                {{ row.inquiryNo }}
              </el-button>
            </template>
          </el-table-column>
          <el-table-column
            v-if="['SCORING','SUBMITTED'].includes(status) && hasInquiryNo"
            align="center"
            :label="$t('sourcingBuyer.inquiryStatus')"
            prop="inquiryStatus"
            width="120"
          >
            <template slot-scope="{row}">
              {{ $getDictLabel('RFQ_STATUS', row.inquiryStatus) }}
            </template>
          </el-table-column>

          <el-table-column
            v-if="['SCORING','SUBMITTED'].includes(status) && hasInquiryNo"
            align="center"
            prop="ranking"
            :label="$t('sourcingBuyer.ranking')"
            width="120"
          />
          <el-table-column
            v-if="['SCORING','SUBMITTED'].includes(status)"
            align="center"
            prop="score"
            :label="$t('sourcingBuyer.score')"
            width="80"
            fixed="right"
            :render-header="_addStarToColumn"
          >
            <template slot-scope="{row}">
              <template v-if="['SCORING'].includes(status)">
                <el-input-number
                  v-model="row.score"
                  :controls="false"
                  :min="0"
                  class="input-number-precision"
                />
              </template>
              <template v-if="['SUBMITTED'].includes(status)">
                {{ row.score }}
              </template>
            </template>
          </el-table-column>
          <el-table-column
            v-if="['SCORING','SUBMITTED'].includes(status)"
            align="center"
            prop="scoreReason"
            :label="$t('sourcingBuyer.scoreReason')"
            width="120"
            fixed="right"
            :render-header="_addStarToColumn"
          >
            <template slot-scope="{row}">
              <template v-if="['SCORING'].includes(status)">
                <el-input v-model="row.scoreReason" />
              </template>
              <template v-if="['SUBMITTED'].includes(status)">
                {{ row.scoreReason }}
              </template>
            </template>
          </el-table-column>

          <el-table-column
            v-if="['SCORING','SUBMITTED'].includes(status)"
            align="center"
            :label="$t('sourcingBuyer.reviewFormNumber')"
            prop="reviewFormNumber"
            width="120"
          >
            <template slot-scope="{row}">
              <el-button
                v-if="row.reviewFormNumber"
                type="text"
                @click="readQuaOfReviewDetail(row)"
              >
                {{ row.reviewFormNumber }}
              </el-button>
            </template>
          </el-table-column>

          <el-table-column
            v-if="['SCORING','SUBMITTED'].includes(status)"
            align="center"
            :label="$t('sourcingBuyer.isIntroduce')"
            width="100"
            prop="isIntroduce"
            fixed="right"
            :render-header="_addStarToColumn"
          >
            <template slot-scope="{row}">
              <template v-if="['SCORING'].includes(status)">
                <el-checkbox v-model="row.isIntroduce" true-label="Y" false-label="N" />
              </template>
              <template v-if="['SUBMITTED'].includes(status)">
                {{ row.isIntroduce === 'Y' ? $t('common.yes'):$t('common.no') }}
              </template>
            </template>
          </el-table-column>
        </el-table>
        <CToolbar v-if="viewType === 'SINGLE'">
          <template slot="right">
            <el-button @click="backOne">
              {{ $t('common.backTo') }}
            </el-button>
            <el-button
              v-if="['SELECTED','SCORING'].includes(status)"
              @click="stagingOne"
            >
              {{ $t('common.staging') }}
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
            :data="tableData"
            style="width: 100%"
            border
            height="345px"
            highlight-current-row
            @selection-change="handleDialogSelection"
          >
            <el-table-column type="selection" width="55" />
            <el-table-column
              align="center"
              type="index"
              :label="$t('common.sort')"
              width="60"
            />
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
import sourcingApplicationDetail from './sourcingApplicationDetail'
import quaOfReviewDetail from 'modb@/vendorManagementBuyer/views/quaOfReview/quaOfReviewDetail'
import inquiryDetail from 'modb@/inquiryBySimpleBuyer/views/inquiryBySimpleListBuyer/inquiryBySimpleListBuyer/inquiryDetail'

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
      dictCodes: {
        orderStatus: 'ORDER_STATUS'
      },
      tableHeader: [],
      sourcingForm: {},
      sourcingInfo: {},
      status: '',
      signUpCount: 0,
      tableData: [],
      selecteds: [],
      quaOfReviewSelecteds: [],
      hasInquiryNo: false,
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
      condition: [{
        value: 'ge',
        label: this.$t('sourcingBuyer.ge')
      }, {
        value: 'eq',
        label: this.$t('sourcingBuyer.eq')
      }, {
        value: 'le',
        label: this.$t('sourcingBuyer.le')
      }],
      flowId: null
    }
  },
  computed: {
    active () {
      return ['PUBLISHED', 'SELECTED', 'SCORING', 'SUBMITTED', 'CLOSED'].indexOf(this.status)
    },
    statusName () {
      return [this.$t('sourcingBuyer.published'), this.$t('sourcingBuyer.selected'), this.$t('sourcingBuyer.scoring'), this.$t('sourcingBuyer.submitted'), this.$t('sourcingBuyer.closed')][this.active]
    },
    workflowBusinessId () {
      // 用来指定工作流的业务ID
      return this.status === 'SUBMITTED' ? this.sourcingInfo.reqHeadId : this.flowId
    },
    workflowTabDisabled () {
      return !['SCORING', 'SUBMITTED'].includes(this.status)
    },
    viewType () {
      return !this.workflowTabDisabled ? 'WORKFLOW' : 'SINGLE'
    },
    viewUpdateButton () {
      return this.status === 'SCORING'
    }
  },
  created () {
    this.sourcingInfo = this.$attrs.params.row
    this.status = this.sourcingInfo.status
    this.getFormDetail(this.sourcingInfo.reqHeadId)

    this.buttonConfigInfo.cancel.view = false
    this.buttonConfigInfo.save.view = false
    this.buttonConfigInfo.submit.view = this.viewUpdateButton
    this.buttonConfigInfo.submit.name = this.$t('sourcingBuyer.initiateApproval')

    this.tableHeader = [{
      label: this.$t('sourcingBuyer.vendorName1'),
      prop: 'vendorName'
    }, {
      label: this.$t('sourcingBuyer.applyManageModel'),
      prop: 'manageModel'
    }, {
      label: this.$t('sourcingBuyer.registCapital'),
      prop: 'registCapital'
    }, {
      label: this.$t('sourcingBuyer.postalAddress'),
      prop: 'postalAddress'
    }, {
      label: this.$t('sourcingBuyer.applyAgentQualifiedBrand'),
      prop: 'agentQualifiedBrand'
    }, {
      label: this.$t('sourcingBuyer.applySupplyArea'),
      prop: 'supplyArea'
    }, {
      label: this.$t('sourcingBuyer.companyCreationYear'),
      prop: 'companyCreationYear'
    }, {
      label: this.$t('sourcingBuyer.contactName'),
      prop: 'contactName'
    }, {
      label: this.$t('sourcingBuyer.phone1'),
      prop: 'phone'
    }, {
      label: this.$t('sourcingBuyer.email'),
      prop: 'email'
    }, {
      label: this.$t('sourcingBuyer.applyStatus'),
      prop: 'applyStatus'
    }, {
      label: this.$t('sourcingBuyer.refuseReason1'),
      prop: 'refuseReason'
    }, {
      label: this.$t('sourcingBuyer.signUpTime'),
      prop: 'creationDate',
      dataType: 'dateTime'
    }]

    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    // 指定工作流的业务类型，在定义工作流时指定
    async getWorkflowBusinessType () {
      return 'souReqApply'
    },
    getCWorkflowRefName () {
      return 'workflowMulti' // 对应CWorkflowMulti标签中的ref
    },
    async getFormDetail (reqHeadId) {
      var res = await this.$http({
        url: '/api-inq/inq/reqhead/get',
        method: 'GET',
        params: { id: reqHeadId },
        loading: true
      })
      if (res.data) {
        this.sourcingForm = res.data
      }
    },
    syncFilterParams (values) {
      this.queryParam = values
    },
    getQuerydata (obj = {}, flag = null) {
      this.queryParam = { ...obj }
      let { reqHeadId, status } = this.sourcingInfo
      this.queryParam.reqHeadId = reqHeadId
      this.queryParam.status = status

      this.$http({
        url: '/api-inq/inq/reqhead/listAllForPage',
        method: 'POST',
        data: this.queryParam
      }).then(res => {
        this.tableData = res.data
        this.signUpCount = this.tableData.filter(item => item.applyStatus === 'SIGN').length || 0
        this.hasInquiryNo = this.tableData.find(item => {
          return !!item.inquiryNo
        })
        if (flag === 'inquiry') {
          this.readInquiryDetail(this.hasInquiryNo)
        }
      })
    },
    stopOne () {
      this.$confirm(this.$t('sourcingBuyer.confirmStop'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        this.$http({
          url: '/api-inq/inq/reqhead/updateStatusById',
          method: 'POST',
          data: {
            reqHeadId: this.sourcingInfo.reqHeadId,
            status: 'SELECTED'
          }
        }).then(res => {
          this.$message.success(this.$t('sourcingBuyer.successStop'))
          this.status = 'SELECTED' // 入围筛选中
        })
      })
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
    handleDropdownCommand (command) {
      if (this.selecteds.length === 0) {
         this.$message.error(this.$t('sourcingBuyer.vendorIsRequired'))
         return
      }
      let data = this.selecteds.map(item => {
        return {
          reqHeadId: item.reqHeadId,
          applyId: item.applyId,
          status: command === 'toNextRoundConfirm' ? 'SELECTED' : 'LOSE'
        }
      })
      this.$http({
        url: '/api-inq/inq/reqapply/selectOrLose',
        method: 'POST',
        data
      }).then(res => {
        this.$message.success(this.$t('sourcingBuyer.handleSuccess'))
        this.getQuerydata()
      })
    },
    publishOne () {
      let data = this.tableData.filter(item => item.applyStatus === 'SIGN' && item.status === 'SELECTED').map(item => {
        return {
          reqHeadId: item.reqHeadId,
          applyId: item.applyId,
          status: item.status
        }
      })
      if (data.length === 0) {
        this.$message.error(this.$t('sourcingBuyer.publishError'))
        return
      }
      this.$http({
        url: '/api-inq/inq/reqapply/finishSelect',
        method: 'POST',
        data
      }).then(res => {
        this.$message.success(this.$t('sourcingBuyer.publishSuccess'))
        this.status = 'SCORING' // 评分中
      })
    },
    inquiryOne () {
      let { reqItemsList, reqHeadId } = this.sourcingForm
      this.$http({
        url: '/api-inq/inq/reqhead/tempSaveInqFromReq',
        method: 'POST',
        data: {
          reqItemsList,
          reqHeadId,
          organizationId: this.sourcingInfo.organizationId
        }
      }).then(res => {
        if (res.code === '0') {
          this.$confirm(this.$t('sourcingBuyer.inquirySuccess'), {
            confirmButtonText: this.$t('common.confirm'),
            cancelButtonText: this.$t('common.cancel'),
            type: 'warning'
          }).then(() => {
            this.getQuerydata({}, 'inquiry')
          })
        }
      })
    },
    quaOfReviewOne () {
      this.dialogVisible = true
    },
    handleDialogSelection (val) {
      this.quaOfReviewSelecteds = val
    },
    handleDialogCancel () {
      this.dialogVisible = false
    },
    handleDialogComfirm () {
      this.submitLoading = true
      let data = this.quaOfReviewSelecteds.map(item => {
        return {
          sourceId: item.applyId,
          opType: 'TEMPORARY_STORAGE',
          reviewForm: {
            quaReviewType: 'NEW_ITEM',
            reviewFormId: item.reviewFormId,
            vendorId: item.vendorId,
            vendorCode: item.vendorCode,
            vendorName: item.vendorName
          },
          cateJournals: []
        }
      })
      this.$http({
        url: '/api-inq/inq/reqhead/saveOrUpdateBatchReviewForm',
        method: 'POST',
        data
      }).then(res => {
        this.submitLoading = false
        this.$message.success(this.$t('sourcingBuyer.quaOfReviewSuccess'))
        this.dialogVisible = false
      }).catch(() => {
        this.submitLoading = false
      })
    },
    readInquiryDetail (row) {
      this.$emit('tab-add', {
        component: inquiryDetail,
        params: {
          flag: 'view',
          readOnly: true,
          row: row,
          tabName: 'inquiryDetail' + row.inquiryNo
        },
        title: row.inquiryNo,
        name: 'inquiryDetail' + row.inquiryNo
      })
    },
    readQuaOfReviewDetail (row) {
      this.$emit('tab-add', {
        component: quaOfReviewDetail,
        params: {
          flag: 'view',
          row: row,
          tabName: 'quaOfReviewDetail' + row.reviewFormNumber
        },
        title: row.reviewFormNumber,
        name: 'quaOfReviewDetail' + row.reviewFormNumber
      })
    },
    async submitOne (type) {
      console.log(type)
      if (this.tableData.filter(item => item.isIntroduce === 'Y').length === 0) {
         this.$message.error(this.$t('sourcingBuyer.vendorIsRequired1'))
         return
      }

      if (this.tableData.filter(item => item.isIntroduce === 'Y' && item.status === 'LOSE').length) {
         this.$message.error(this.$t('sourcingBuyer.addVendorError'))
         return
      }
      let data = this.tableData.filter(item => item.applyStatus === 'SIGN' && item.isIntroduce === 'Y').map(item => {
        return {
          reqHeadId: item.reqHeadId,
          applyId: item.applyId,
          score: item.score,
          scoreReason: item.scoreReason,
          status: item.status,
          isIntroduce: item.isIntroduce
        }
      })
      let res = await this.$http({
        url: '/api-inq/inq/reqapply/score',
        method: 'POST',
        data
      })
      this.flowId = this.sourcingInfo.reqHeadId
      await this.handlerAfter(type) // 触发流程激活 (流程相关)
      this.status = 'SUBMITTED' // 审批中
      this.buttonConfigInfo.submit.view = this.viewUpdateButton
      this.updateStatus()
    },
    updateStatus () {
      this.$http({
          url: '/api-inq/inq/reqhead/updateStatusById',
          method: 'POST',
          data: {
            reqHeadId: this.sourcingInfo.reqHeadId,
            status: 'SUBMITTED'
          }
        }).then(res => {
            console.log(res)
        })
    },
    backOne () {
      this.$emit('tab-remove', this.$attrs.params.tabName)
      if (!this.$attrs.params.flag) {
        // 登录页跳转过来的直接关闭
        this.__setTabTodo('sourcingApplicationList.getQuerydata')
      }
    },
    stagingOne () {
      if (this.status === 'SELECTED') {
        this.stagingSelected()
      } else {
        this.stagingScoring()
      }
    },
    stagingSelected () {
      let data = this.selecteds.map(item => {
        return {
          reqHeadId: item.reqHeadId,
          applyId: item.applyId,
          status: item.status
        }
      })
      this.$http({
        url: '/api-inq/inq/reqapply/selectOrLose',
        method: 'POST',
        data
      }).then(res => {
        this.$message.success(this.$t('sourcingBuyer.stagingSuccess'))
      })
    },
    stagingScoring () {
      let data = this.tableData.filter(item => item.applyStatus === 'SIGN').map(item => {
        return {
          reqHeadId: item.reqHeadId,
          applyId: item.applyId,
          score: item.score,
          scoreReason: item.scoreReason,
          status: item.status,
          isIntroduce: item.isIntroduce
        }
      })
      this.$http({
        url: '/api-inq/inq/reqapply/score',
        method: 'POST',
        data
      }).then(res => {
        this.$message.success(this.$t('sourcingBuyer.stagingSuccess'))
      })
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
    .el-steps {
      padding-bottom: 0;
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
