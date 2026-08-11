<template>
  <el-container class="flex-container the-purchaseApplicationDetail-detail" direction="vertical">
    <el-main>
      <CWorkflowMulti
        ref="workflowMulti"
        v-model="activeTabName"
        :fun-params="workflowParamsInfo"
        :button-config-info="buttonConfigInfo"
        :button-custom="buttonCustom"
        @tab-click="workflowView"
        @workflow-handler="workflowHandler"
        @click-handler="type => saveOrSubmitBill(type)"
        @submit-direct="type => saveOrSubmitBill(type)"
        @confirm="(type, comment) => saveOrSubmitBill(type, comment)"
        @close-tab="back"
      >
        <div class="form-container2">
          <el-form
            ref="requirementHeadRef"
            :model="requirementHead"
            label-width="80px"
            label-position="top"
            :rules="rules"
          >
            <el-collapse v-model="activeDims" class="tab-form-style">
              <!-- 评分信息 -->
              <el-collapse-item ref="aptInfo" :title="$t('cusEntry.supplement20250205.scoreInfo')" name="1">
                <srm-row>
                  <srm-col>
                    <!-- 评分项目名称 -->
                    <el-form-item
                      :label="$t('perfMod.projectName2')"
                    >
                      <el-input v-model="requirementHead.projectName" :disabled="disabledBol" />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <!-- 合同编码 -->
                    <el-form-item
                      :label="$t('bidMod.category_contractCode')"
                    >
                      <el-input v-model="requirementHead.contractNo" :disabled="disabledBol" />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <!-- 合同名称 -->
                    <el-form-item
                      :label="$t('vendorMod.contractName')"
                    >
                      <el-input v-model="requirementHead.contractName" :disabled="disabledBol" />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <!-- 板块 -->
                    <el-form-item
                      :label="$t('cusEntry.bidSuperviseReport.extOrgBuName')"
                    >
                      <el-input v-model="requirementHead.buOrganizationName" :disabled="disabledBol" />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <!-- 公司 -->
                    <el-form-item
                      :label="$t('components.organization.COMPANY')"
                    >
                      <el-input v-model="requirementHead.ouOrganizationName" :disabled="disabledBol" />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <!-- 供应商编码 -->
                    <el-form-item
                      :label="$t('common.vendorCode')"
                    >
                      <el-input v-model="requirementHead.companyCode" :disabled="disabledBol" />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <!-- 供应商名称 -->
                    <el-form-item
                      :label="$t('common.companyName')"
                    >
                      <el-input v-model="requirementHead.companyName" :disabled="disabledBol" />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <!-- 招标编号 -->
                    <el-form-item
                      :label="$t('cusEntry.bidMod.extProjectNo')"
                    >
                      <el-input v-model="requirementHead.bidCode" :disabled="disabledBol" />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <!-- 投资编号 -->
                    <el-form-item
                      :label="$t('cusEntry.bidMod.investNum')"
                    >
                      <el-input v-model="requirementHead.extInvestNo" :disabled="disabledBol" />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <!-- 招标结束时间 -->
                    <el-form-item
                      :label="$t('cusEntry.supplement20250205.bidEndTime')"
                    >
                      <el-date-picker
                        v-model="requirementHead.bidEndDate"
                        type="datetime"
                        :format="$formatDatePickerTime"
                        value-format="yyyy-MM-dd HH:mm:ss"
                        :disabled="disabledBol"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <!-- 履约阶段 -->
                    <el-form-item
                      :label="$t('cusEntry.supplement20250121.performanceStage')"
                    >
                      <DictSelect
                        v-model="requirementHead.performanceCode"
                        :disabled="disabledBol"
                        code="MILESTONE_SCHEDULE"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <!-- 品类 -->
                    <el-form-item
                      :label="$t('common.category')"
                    >
                      <el-input v-model="requirementHead.categoryName" :disabled="disabledBol" />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <!-- 审核状态 -->
                    <el-form-item
                      prop="approveStatus"
                      :label="$t('vendorMod.approvalStatus')"
                    >
                      <DictSelect
                        v-model="requirementHead.approveStatus"
                        code="PROJECT_SCORE_MAN_STATUS"
                        :disabled="disabledBol"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <!-- 评分开始时间 -->
                    <el-form-item
                      :label="$t('perfMod.scoreStartTime')"
                    >
                      <el-date-picker
                        v-model="requirementHead.perStartMonth"
                        type="date"
                        :format="$formatDatePicker"
                        value-format="yyyy-MM-dd"
                        :disabled="disabledBol"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <!-- 评分结束时间 -->
                    <el-form-item
                      :label="$t('cusEntry.supplement20250205.scoreEndTime')"
                    >
                      <el-date-picker
                        v-model="requirementHead.perEndMonth"
                        type="date"
                        :format="$formatDatePicker"
                        value-format="yyyy-MM-dd"
                        :disabled="disabledBol"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <!-- 评分人 -->
                    <el-form-item
                      :label="$t('vendorMod.reviewPerson')"
                    >
                      <el-input v-model="requirementHead.scoreManName" :disabled="disabledBol" />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <!-- 实际评分时间 -->
                    <el-form-item
                      :label="$t('cusEntry.supplement20250205.actualRatingTime')"
                    >
                      <el-date-picker
                        v-model="requirementHead.scoreDate"
                        type="date"
                        :format="$formatDatePicker"
                        value-format="yyyy-MM-dd"
                        :disabled="disabledBol"
                      />
                    </el-form-item>
                  </srm-col>
                </srm-row>
              </el-collapse-item>
              <!-- 评分详情 -->
              <el-collapse-item :title="$t('cusEntry.bidMod.techScoreDetail')" name="3">
                <!-- <p class="btn_line">
                  <el-button
                    v-if="!isReadOnly"
                    :disabled="isReadOnly"
                    type="primary"
                    class="detail-pbtn"
                    @click="addUploadOne"
                  >
                    添加评分人
                  </el-button>
                </p> -->
                <p v-if="detailList[0]?.quoteMode == 'DIRECT_QUOTE'" style="color: red">
                  <!-- 提示：百分制打分请填写“百分制打分”，直接打分请填写“绩效得分”。 -->
                  {{ $t("cusEntry.supplement20250205.percentScoringperformanceScore") }}
                </p>
                <el-table
                  :data="detailList"
                  style="width: 100%"
                  border
                  max-height="500px"
                  show-summary
                  :summary-method="getSummaries"
                >
                  <el-table-column
                    align="center"
                    type="index"
                    :label="$t('purSettlementMod.tabindex')"
                    width="50"
                  />
                  <!-- 指标维度 -->
                  <el-table-column
                    align="center"
                    prop="indicatorDimensionType"
                    width="100"
                    :label="$t('perfMod.indicatorDimensionType')"
                  >
                    <template slot-scope="scope">
                      <DictSelect
                        v-model="scope.row.indicatorDimensionType"
                        code="INDICATORS_DIM"
                        :disabled="disabledBol"
                      />
                    </template>
                  </el-table-column>
                  <!-- 指标名称 -->
                  <el-table-column
                    align="center"
                    prop="indicatorName"
                    width="150"
                    :label="$t('perfMod.indicatorName')"
                  >
                    <template slot-scope="scope">
                      <span>{{ scope.row.indicatorName }}</span>
                    </template>
                  </el-table-column>
                  <!-- 打分逻辑 -->
                  <el-table-column
                    align="center"
                    prop="indicatorLogic"
                    min-width="200"
                    :label="$t('perfMod.indicatorLogic_1')"
                  >
                    <template slot-scope="scope">
                      <span>{{ scope.row.indicatorLogic }}</span>
                    </template>
                  </el-table-column>
                  <!-- 百分制打分 -->
                  <el-table-column
                    align="center"
                    prop="scoreManName"
                    width="100"
                    :label="$t('cusEntry.supplement20250205.percentScore')"
                  >
                    <template slot-scope="scope">
                      <!-- TEXT_CONVERSION 文本折算  -->
                      <!-- INTERVAL_CONVERSION 区间折算  -->
                      <!-- DIRECT_QUOTE 直接取值 -->
                      <el-select
                        v-if="scope.row.quoteMode === 'TEXT_CONVERSION'"
                        v-model="scope.row.templateIndsLineId"
                        :disabled="isReadOnly"
                        :placeholder="$t('perfMod.indicator')"
                        @focus="selectFocus(scope.$index)"
                        @change="value => indicatorLineChange(value, scope.row)"
                      >
                        <el-option
                          v-for="item in scope.row.indicatorsLines"
                          :key="item.templateIndsLineId"
                          :label="item.indicatorLineDes"
                          :value="item.templateIndsLineId"
                        />
                      </el-select>
                      <el-input
                        v-if="scope.row.quoteMode === 'INTERVAL_CONVERSION'"
                        v-model="scope.row.pefScore"
                        v-input-format="{ type: 'number' }"
                        :disabled="isReadOnly"
                        @focus="selectFocus(scope.$index)"
                        @change="value => pefScoreChange(value, scope.row)"
                      />
                      <el-input
                        v-if="scope.row.quoteMode === 'DIRECT_QUOTE'"
                        v-model="scope.row.pefScore"
                        v-input-format="{ type: 'number' }"
                        :disabled="isReadOnly"
                        @focus="selectFocusA(scope.$index)"
                        @input="value => pefDirectScoreChange(value, scope.row)"
                      />
                    </template>
                  </el-table-column>
                  <!-- 绩效得分 -->
                  <el-table-column
                    align="center"
                    prop="score"
                    width="100"
                    :label="$t('bidMod.performScore')"
                  >
                    <template slot-scope="scope">
                      <el-input v-model="scope.row.score" :disabled="(disabledBol && scope.row.quoteMode !== 'DIRECT_QUOTE') || isReadOnly" @input="value => pefDirectScoreChange2(value, scope.row)" />
                    </template>
                  </el-table-column>
                  <!-- 打分说明 -->
                  <el-table-column
                    align="center"
                    prop="comments"
                    width="100"
                    :label="$t('perfMod.scoreShows')"
                  >
                    <template slot-scope="scope">
                      <el-input v-model="scope.row.comments" :disabled="isReadOnly" />
                    </template>
                  </el-table-column>
                  <!-- 相关附件 -->
                  <el-table-column
                    align="center"
                    prop="comments"
                    width="100"
                    :label="$t('accountMod.relevantAttachment')"
                  >
                    <template slot-scope="scope">
                      <SrmCommonFile
                        :extra-data="fileInfo"
                        :default-file="{
                          fileId: scope.row.fileId,
                          fileName: scope.row.fileName
                        }"
                        :readonly="isReadOnly"
                        @on-change="({file}) => outerHandleUploadSuccess(file,scope.row)"
                      />
                    </template>
                  </el-table-column>
                </el-table>
              </el-collapse-item>
              <!-- 复核驳回信息 -->
              <el-collapse-item :title="$t('cusEntry.supplement20250205.reviewRejectionInfo')" name="3">
                <el-table :data="rejectInfoList" style="width: 100%" border max-height="250px">
                  <el-table-column
                    align="center"
                    type="index"
                    :label="$t('purSettlementMod.tabindex')"
                    width="50"
                  />
                  <!-- 轮次 -->
                  <el-table-column
                    align="center"
                    prop="scoreRound"
                    :label="$t('bidMod.bidingRound')"
                  />
                  <!-- 招标驳回说明 -->
                  <el-table-column
                    align="center"
                    prop="rejectInfo"
                    :label="$t('cusEntry.supplement20250205.bidRejectionExplanation')"
                    width="180"
                  />
                  <!-- 驳回时间 -->
                  <el-table-column
                    align="center"
                    prop="rejectDate"
                    :label="$t('logisticsMod.rejectDate')"
                  >
                    <template slot-scope="scope">
                      {{$parseTime(scope.row.rejectDate)}}
                    </template>
                  </el-table-column>
                </el-table>
              </el-collapse-item>
            </el-collapse>
          </el-form>
        </div>
      </CWorkflowMulti>
    </el-main>
  </el-container>
</template>
<script>
import _pick from 'lodash/pick'
import OrganizationSelector from 'lib@/components/organization-selector'
import { tabTodoMixin } from '@/utils/mixins'
import QuickSearch from 'lib@/components/QuickSearch'
import { parseTime } from '@/utils'
import { downloadFileLink } from 'lib@/utils/file'
import WorkflowCommon from '@/library/mixins/workflow-common'
import '@riophae/vue-treeselect/dist/vue-treeselect.css'
import { getToken } from '@/utils/auth'
import axios from 'axios'
import { sysPrefix } from '@/config/ipConfig'
import { purchaseApplicationApi } from 'modc@/buyer/purchasingDemand/api'
import CategorySelect from 'modc@/buyer/vendorManagementBuyer/views/quaOfReviewEngine/components/categorySelect'

export default {
  name: 'PurchaseApplicationDetail',
  components: {
    QuickSearch,
    OrganizationSelector,
    CategorySelect
  },
  mixins: [tabTodoMixin, WorkflowCommon],
  data () {
    return {
      fileInfo: {
        fileModular: 'sup',
        fileFunction: 'vendorBiddingManagement',
        fileType: 'images'
      },
      curRole: this.$store.getters.userType,
      templateHeadList: [],
      disabledBol: true,
      requirementHead: {
        projectName: null,
        projectStatus: null,
        buOrganizationName: null,
        ouOrganizationName: null,
        contractNo: null,
        contractName: null,
        performanceCode: null,
        companyCode: null,
        companyName: null,
        bidCode: null,
        bidEndDate: null,
        bidManager: null,
        bidManagerFullPath: null,
        contractManager: null,
        contractManagerFullPath: null,
        perStartMonth: null,
        perEndMonth: null
      },
      currentPage: 1,
      allcancelLineList: [],
      activeDims: ['1', '2', '3'],
      rules: {

      },
      detailList: [],
      rejectInfoList: []
    }
  },
  computed: {
    viewUpdateButton () {
      return (
        this.curRole === 'BUYER' &&
        !this.isReadOnly &&
        this.requirementHead.approveStatus !== 'APPROVED'
      )
    },
    disabledUpdateButton () {
      return (
        this.requirementHead.approveStatus === 'SUBMITTED' ||
        this.requirementHead.approveStatus === 'APPROVING'
      )
    },
    workflowBusinessId () {
      return this.requirementHead?.projectScoreManId
    },
    workflowTabDisabled () {
      return !this.isReadOnly
    },
    isReadOnly () {
      return !['add', 'edit'].includes(this.$attrs.params.flag)
    }
  },
  watch: {
    viewUpdateButton () {
      this.buttonConfigInfo.save.view = this.viewUpdateButton
      this.buttonConfigInfo.submit.view = this.viewUpdateButton
    },
    disabledUpdateButton () {
      this.buttonConfigInfo.save.disabled = this.disabledUpdateButton
      this.buttonConfigInfo.submit.disabled = this.disabledUpdateButton
    }
  },
  created () {
    this.Viewflag = this.$attrs.params.flag
    if (this.Viewflag === 'approveNumber') this.workflowParamsInfo.tabDisabled = false
    if (this.$attrs.params.flag === 'add') {

    } else {
      this.getFormDetail(this.$attrs.params.row.projectScoreManId)
    }
    this.getButtonConfig()
  },
  methods: {
    getSummaries (param) {
      const { columns, data } = param
      const sums = []
      columns.forEach((column, index) => {
        if (index === 0) {
          sums[index] = this.$t('other.key13') // 合计
          return
        }
        if (column.property === 'score') {
          const values = data.map(item => Number(item[column.property]))
          if (!values.every(value => isNaN(value))) {
            sums[index] = values.reduce((prev, curr) => {
              const value = Number(curr)
              if (!isNaN(value)) {
                return prev + curr
              } else {
                return prev
              }
            }, 0)
          } else {
            sums[index] = '-'
          }
        }
      })
      return sums
    },
    selectFocus (index) {
      this.currentRow = index
    },
    selectFocusA (index) {
      this.currentRow = index
    },
    // 事件切换选择
    indicatorLineChange (val, row) {
      console.log(val, 'val')
      if (val) {
        let rowData = row.indicatorsLines.find((item) => {
          return item.templateIndsLineId === val
        })
        console.log(rowData, 'rowData')
        if (rowData) {
          row.pefScore = rowData.pefScore
          row.score = rowData.pefScore
          // row.templateIndsLineId = rowData.indicatorLineId
          // row.indicatorLineDes = rowData.indicatorLineDes
        }
      }
    },
    pefScoreChange (val, row) {
      console.log(val, 'val')
      if (val) {
        let rowData = null
        if (row.indicatorsLines[0].startSymbol == 'GREATER') {
          rowData = row.indicatorsLines.find((item) => {
            return val > item.scoreStart - 1 && val < item.scoreEnd + 1
          })
        } else if (row.indicatorsLines[0].startSymbol == 'GREATER_EQUAL') {
          rowData = row.indicatorsLines.find((item) => {
            return val >= item.scoreStart && val < item.scoreEnd
          })
        } else if (row.indicatorsLines[0].startSymbol == 'LESS') {
          rowData = row.indicatorsLines.find((item) => {
            return val < item.scoreStart && val >= item.scoreEnd
          })
        } else if (row.indicatorsLines[0].startSymbol == 'LESS_EQUAL') {
          rowData = row.indicatorsLines.find((item) => {
            return val <= item.scoreStart && val > item.scoreEnd
          })
        }
        console.log(rowData, 'rowData')
        if (rowData) {
          row.score = rowData.pefScore
          row.templateIndsLineId = rowData.templateIndsLineId
          row.indicatorLineDes = rowData.indicatorLineDes
        } else {
          // 查询区间最大值 最小值 [[
          let minAndMax = []
          if (row.indicatorsLines.length > 0) {
            if (['GREATER', 'GREATER_EQUAL'].includes(row.indicatorsLines[0].startSymbol)) {
              row.indicatorsLines.forEach((elm) => {
                let min = elm.scoreStart
                let max = elm.scoreEnd
                minAndMax.push(min, max)
              })
              let minD = Math.min(...minAndMax)
              let maxD = Math.max(...minAndMax)
              // console.log(minAndMax,minD,maxD)
              // ]]
              this.$message({
                message:
                  `${this.$t('perfMod.numbersFilled')}` +
                  minD +
                  '~' +
                  maxD +
                  `,${this.$t('perfMod.maintainRangeValue')}`,
                type: 'error'
              })
            } else {
              this.$message.error(this.$t('perfMod.notWithinRange')) // '输入数值不在区间范围内'
            }
          } else {
            this.$message({
              message: this.$t('perfMod.maintainPerformTemplate'),
              type: 'error'
            })
          }
          row.templateIndsLineId = ''
          row.indicatorLineDes = ''
          row.pefScore = ''
          row.score = ''
        }
      }
    },
    pefDirectScoreChange2 (val, row) {
      if (val) {
        if (val > row.dimensionWeight) {
          // this.$message.error(`该打分不能超过${row.dimensionWeight}分`)
          this.$message.error(`${this.$t('cusEntry.supplement20250205.maxScoreNotExceeding')}${row.dimensionWeight}${this.$t('time.min')}`)
          row.score = row.dimensionWeight
          row.pefScore = 100
          return false
        } else {
          row.score = val
          row.pefScore = ((val / row.dimensionWeight) * 100).toFixed(2)
        }
      }
    },
    pefDirectScoreChange (val, row) {
      if (val) {
        // if(val>row.dimensionWeight){
        //   this.$message.error(`该打分不能超过${row.dimensionWeight}分`)
        //   row.score = row.dimensionWeight
        //   row.pefScore = row.dimensionWeight
        //   return false
        // }
        // if (this.currentRow !== null && this.currentRow >= 0) {
        //   row.score = val
        // }

        if (Number(val) < 0) {
          row.pefScore = 0
        } else if (Number(val) > 100) {
          row.pefScore = 100
        }
        const pefScore = row.pefScore
        const dimensionWeight = row.dimensionWeight
        let score = dimensionWeight * pefScore / 100
        if (this.currentRow !== null && this.currentRow >= 0) {
          row.score = score
        }
      } else {
        if (this.currentRow !== null && this.currentRow >= 0 && val != 0) {
          row.score = ''
        }
      }
    },
    outerHandleUploadSuccess (file, row) {
      const { fileId, fileName } = file
      row.fileId = fileId.toString()
      row.fileName = fileName
    },
    templateHeadChange (val, scope) {

    },
    // 确认选择品类
    comfirmSelect (node, scope) {
      scope.row.categoryId = node ? node.categoryId : ''
      scope.row.categoryName = node ? node.categoryName : ''
      scope.row.categoryCode = node ? node.categoryCode : ''
      // 请求绩效模型
      this.$http({
        url: '/api-pef/pj/template/listPefTemplateHeaderPage',
        method: 'POST',
        data: {
          'pageNum': 1,
          'pageSize': 10000,
          'categoryId': node?.categoryId,
          'attribute1': 'PROJECT'
        },
        loading: true
      }).then(res => {
        let attr = []
        res.data?.list?.forEach(e => {
          const obj = {
            label: e.templateName,
            value: e.templateHeadId
          }
          attr.push(obj)
        })
        this.templateHeadList = attr
      })
    },
    getButtonConfig () {
      this.buttonConfigInfo.save.view = this.viewUpdateButton
      this.buttonConfigInfo.submit.view = this.viewUpdateButton
      this.buttonConfigInfo.save.disabled = this.disabledUpdateButton
      this.buttonConfigInfo.submit.disabled = this.disabledUpdateButton
      this.buttonConfigInfo.cancel.view = !this.isReadOnly
      this.buttonConfigInfo.close.view = this.isReadOnly
    },
    async getWorkflowBusinessType () {
      return 'projectScoreMan'
    },
    async getWorkflowBusinessVariables () {
      return {

      }
    },
    getCWorkflowRefName () {
      return 'workflowMulti'
    },
    async getFormDetail (projectScoreItemsId) {
      this.$http({
        url: '/api-pef/projectScoreMan/getDetailById',
        method: 'GET',
        params: {
          id: projectScoreItemsId
        },
        loading: true
      }).then(res => {
        const data = res.data
        const { detailList, rejectInfoList, ...other } = data
        this.detailList = detailList
        console.log(this.detailList, 'this.detailList')
        this.rejectInfoList = rejectInfoList
        this.requirementHead = other
      })
    },
    addUploadOne () {
      this.detailList.push({})
    },
    // 行删除
    handleDelClick (index, row) {
      this.detailList.splice(index, 1)
    },
    async submitEvent (allparam) {
      this.saveBill(allparam, 'sub')
    },
    saveBill (allparam, type) {
      this.$http({
        url: '/api-pef/projectScoreMan/saveOrUpdateDetail',
        method: 'POST',
        data: allparam,
        loading: true
      }).then(async res => {
        if (type == 'sub') {
          await this.handlerAfter('SUBMIT')
        } else {
          this.$message.success(this.$t('components.approvalHead.tips.approvalCompletion'))  // 操作成功
          this.back()
        }
      })
    },
    async saveOrSubmitBill (type) {
      let allparam = {
        ...this.requirementHead,
        detailList: this.detailList,
        rejectInfoList: this.rejectInfoList
      }
      if (type === 'SUBMIT') {
        const detailList = allparam?.detailList
        if (detailList.length <= 0) {
          this.$message.warning(this.$t('cusEntry.supplement20250205.fillInRatingDetails'))  // 请填写评分详情
          return false
        }
        let bol = false
        detailList.forEach(e => {
          if (!e.pefScore || !e.score || e.pefScore == '' || e.score == '') {
            bol = true
          }
        })
        if (bol) {
          this.$message.warning(this.$t('cusEntry.supplement20250205.scoreDetailNotFilled'))  // '评分详情有分数未填写'
          return false
        }
        this.submitEvent(allparam)
      } else {
        this.saveBill(allparam, 'save')
      }
    },
    back () {
      this.$emit('tab-remove', this.$attrs.params.tabName)
      this.__setTabTodo('PerformanceScoreItemsList.getQuerydata')
    }
  }
}
</script>
<style scoped lang="scss">
.list-page-query :deep(.el-form-item__label) {
  text-align: right !important;
}
.the-purchaseApplicationDetail-detail {
  .sub_header {
    padding: 4px 11px;
    background: #eee;
  }
  .input-with-select .el-input-group__prepend {
    background-color: #fff;
  }
  .isDisabledimport {
    pointer-events: none;
    opacity: 0.5;
  }
  .the_btn_wrapper {
    display: inline-block;
    width: 111px;
  }
  .btn_line {
    display: flex;
    margin: 0 0 8px 0;
  }
  .el-tooltip :deep(.el-button) {
    min-width: 56px;
    font-size: 14px;
    border-radius: 2px;
    padding: 8px 16px;
  }
  .topComment {
    margin-top: 15px;
    text-align: right;
  }
  .input-number-precision {
    width: 100%;
    :deep(.el-input__inner) {
      text-align: left;
      padding-left: 8px;
    }
  }
}
:deep(.el-table td.el-table__cell .el-form-item__content) {
  white-space: nowrap;
  text-overflow: ellipsis;
  overflow: hidden;
}
.toRequired {
  color: #ff4949;
  padding-right: 2px;
}
</style>
