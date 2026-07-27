<template>
  <el-container class="flex-container the-performanceScoreItemsDetail-detail" direction="vertical">
    <el-main>
      <el-tabs v-model="editableTabsValue" type="card" style="padding-bottom: 44px;" @tab-click="tabClickHandel">
        <el-tab-pane :label="$t('perfMod.performanceScoreInfo')" name="tab1">
          <div class="form-container2">
            <el-form
              ref="form"
              :model="form"
              label-width="80px"
              label-position="top"
              class="form-incontainer"
              :rules="rules"
              :disabled="
                form.projectStatus !== 'SCORE_DRAFT' || curOpt === 'view' || curOpt === 'viewResult'
              "
            >
              <el-collapse v-model="activeDims" class="tab-form-style">
                <!-- 项目信息 -->
                <el-collapse-item ref="projectInfo" :title="$t('perfMod.projectInformation')" name="1">
                  <srm-row>
                    <srm-col>
                      <el-form-item label="" prop="projectName">
                        <span slot="label">
                          评分项目名称
                          <!-- <div style="color: red; white-space: pre-line">
                            {{ $t('perfMod.performanceEvaluation') }}
                          </div> -->
                        </span>
                        <el-input v-model="form.projectName" />
                      </el-form-item>
                    </srm-col>
                    <srm-col>
                      <el-form-item :label="$t('perfMod.templateHeadId')" prop="templateHeadId">
                        <el-select v-model="form.templateHeadId" @change="templateNameChange">
                          <el-option
                            v-for="item in templateData"
                            :key="item.templateHeadId"
                            :label="item.templateName"
                            :value="item.templateHeadId"
                          />
                        </el-select>
                      </el-form-item>
                    </srm-col>
                    <srm-col>
                      <el-form-item :label="$t('perfMod.projectStatus')">
                        <DictSelect v-model="form.projectStatus" code="PERF_PROJECT_STATUS" disabled />
                      </el-form-item>
                    </srm-col>
                    <srm-col>
                      <el-form-item label="公司">
                        <OrganizationSelectTree
                          v-model="form.organizationId"
                          :parent-id="-1"
                          node-type="OU"
                          :scope="form"
                          :placeholder="$t('common.pleaseSelect')"
                          disabled
                          @select="addOrgHandle"
                        />
                      </el-form-item>
                    </srm-col>

                    <srm-col>
                      <el-form-item :label="$t('perfMod.perStartMonth')" prop="perStartMonth">
                        <el-date-picker
                          v-model="form.perStartMonth"
                          type="month"
                          :picker-options="pickerOptionsStart"
                          format="yyyy-MM"
                          value-format="yyyy-MM-dd"
                          :placeholder="$t('components.beginMonth')"
                          @change="dateChangeHandelA"
                        />
                      </el-form-item>
                    </srm-col>
                    <srm-col>
                      <el-form-item :label="$t('perfMod.perEndMonth')" prop="perEndMonth">
                        <el-date-picker
                          v-model="form.perEndMonth"
                          type="month"
                          :picker-options="pickerOptionsEnd"
                          format="yyyy-MM"
                          value-format="yyyy-MM-dd"
                          :placeholder="$t('components.endMonth')"
                          @change="dateChangeHandelB"
                        />
                      </el-form-item>
                    </srm-col>
                    <srm-col>
                      <el-form-item :label="$t('perfMod.scoreStartTime')" prop="scoreStartTime">
                        <el-date-picker
                          v-model="form.scoreStartTime"
                          :picker-options="pickerOptionsStart2"
                          format="yyyy-MM-dd"
                          value-format="yyyy-MM-dd"
                          :placeholder="$t('perfMod.scoreStartTime')"
                        />
                      </el-form-item>
                    </srm-col>
                    <srm-col>
                      <el-form-item :label="$t('评分结束时间')" prop="scoreEndTime">
                        <el-date-picker
                          v-model="form.scoreEndTime"
                          :picker-options="pickerOptionsEnd2"
                          format="yyyy-MM-dd"
                          value-format="yyyy-MM-dd"
                          :placeholder="$t('perfMod.scoreEndTime')"
                        />
                      </el-form-item>
                    </srm-col>
                    <srm-col>
                      <el-form-item :label="$t('创建人')" prop="createdFullName">
                        <el-input v-model="form.createdFullName" disabled />
                      </el-form-item>
                    </srm-col>
                    <srm-col>
                      <el-form-item :label="$t('创建人部门')" prop="fullPathId">
                        <el-input v-model="form.fullPathId" disabled />
                      </el-form-item>
                    </srm-col>
                  </srm-row>
                </el-collapse-item>
                <!-- 评分供应商 -->
                <el-collapse-item ref="evaluateVendor" :title="$t('perfMod.evaluateVendor')" name="2">
                  <div class="btn-flex">
                    <QuickSearch
                      name="ceea_storage_return"
                      :disabled="['view', 'approve'].includes(curOpt) || !form.organizationId || !form.perStartMonth || !form.perEndMonth"
                      :preQueryData="preQueryData1"
                      :btnTitle="$t('perfMod.addOneVendor')"
                      showButton
                      multiSelect
                      @close-quicksearch="getCompanyList"
                    />
                    <!-- 产品说先隐藏 -->
                    <!-- <quick-search
                      name="scc_perf_template_company_url"
                      style="margin-left: 10px;"
                      :disabled="curOpt === 'view' || !form.templateHeadId"
                      :preQueryData="preQueryData2"
                      :btnTitle="$t('perfMod.addAllVendor')"
                      showButton
                      multiSelect
                      @close-quicksearch="getCompanyList"
                    /> -->
                  </div>
                  <el-table :data="perfScoreItemsSupList" style="width: 100%" border max-height="250px">
                    <el-table-column align="center" type="index" :label="$t('perfMod.index')" width="60" />
                    <el-table-column
                      align="center"
                      prop="companyName"
                      :label="$t('perfMod.vendorName')"
                      width="400"
                      :show-overflow-tooltip="true"
                    />
                    <el-table-column
                      align="center"
                      prop="companyCode"
                      :label="$t('perfMod.vendorCode')"
                      min-width="200"
                      :show-overflow-tooltip="true"
                    />
                    <el-table-column :label="$t('common.operation')" width="80">
                      <template slot-scope="scope">
                        <el-button type="text" @click="deleteOneContent(scope.$index, scope.row)">
                          {{ $t('common.delete') }}
                        </el-button>
                      </template>
                    </el-table-column>
                  </el-table>
                </el-collapse-item>
                <!-- 选择评分人 -->
                <el-collapse-item ref="chooseEvaluator" :title="$t('perfMod.chooseEvaluator')" name="3">
                  <p style="margin: 0 0 16px">
                    <el-button type="primary" class="detail-pbtn" @click="addScorer">
                      {{
                        $t('perfMod.addEvaluator')
                      }}
                    </el-button>
                  </p>
                  <el-table :data="perfScoreItemsManList" style="width: 100%" border max-height="250px">
                    <el-table-column align="center" type="index" :label="$t('perfMod.index')" width="60" />
                    <el-table-column
                      align="center"
                      prop="scoreUserName"
                      :label="$t('perfMod.scoreUserName')"
                      width="200"
                      :show-overflow-tooltip="true"
                    >
                      <template slot-scope="scope">
                        <QuickSearch
                          :show-input="scope.row.scoreUserName"
                          show-key="nickname"
                          :scope-data="scope.row"
                          name="scc_rbac_user_display"
                          @close-quicksearch="getUserdemandObj"
                        />
                      </template>
                    </el-table-column>
                    <el-table-column
                      align="center"
                      prop="scoreNickName"
                      :label="$t('perfMod.scoreNickName')"
                      width="200"
                      :show-overflow-tooltip="true"
                    />
                    <el-table-column
                      align="center"
                      prop="scoreUserEmail"
                      :label="$t('perfMod.scoreUserEmail')"
                      min-width="150"
                      :show-overflow-tooltip="true"
                    />
                    <el-table-column
                      align="center"
                      prop="comments"
                      :label="$t('perfMod.comments')"
                      min-width="150"
                      :show-overflow-tooltip="true"
                    >
                      <template slot-scope="scope">
                        <el-input
                          v-model="scope.row.comments"
                          style="padding-right: 60px"
                          type="text"
                          :placeholder="$t('common.pleaseTypeContents')"
                          maxlength="100"
                          show-word-limit
                        />
                      </template>
                    </el-table-column>
                    <el-table-column :label="$t('common.operation')" width="150">
                      <template slot-scope="scope">
                        <span
                          v-if="scope.row.scoreUserName"
                          class="el-button el-button--text el-button--mini"
                          @click="scoreTaskSetting(scope.$index, scope.row)"
                        >{{ $t('perfMod.perfManSupIndListDetail')
                        }}</span>
                        <el-button type="text" @click="deleteScoreUser(scope.$index, scope.row)">
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
        </el-tab-pane>
        <!--计算结果-->
        <el-tab-pane
          :label="$t('perfMod.evaluatorResult')"
          name="tab2"
          :disabled="
            form.projectStatus !== 'SCORE_CALCULATED' &&
              form.projectStatus !== 'RESULT_PUBLISHED' &&
              form.projectStatus !== 'RESULT_NO_PUBLISHED'
          "
        >
          <el-container direction="vertical">
            <el-main style="height: 430px">
              <MainHeader :l-span="22" :r-span="2">
                <template slot="left">
                  <div style="display: flex">
                    <h3 style="margin: 5px 15px 0 0; font-size: 16px; font-weight: normal">
                      {{ $t('perfMod.compositeScore') }}
                    </h3>

                    <!--导出-->
                    <ExportExcel
                      type="default"
                      page-url="/api-pef/scoring/perfOverallScore/excelExport"
                      export-mode="front"
                      isCustomUrl="/api-pef/scoring/perfOverallScore/excelExport"
                      :table-header="headerTable"
                      :dict-codes="dictCodes"
                      :filter-params="queryParam"
                    />
                  </div>
                </template>
              </MainHeader>
              <el-table :data="tableData" style="width: 100%" height="500px" border>
                <el-table-column
                  align="center"
                  type="index"
                  width="50"
                />
                <!-- 项目名称 -->
                <el-table-column
                  align="center"
                  prop="projectName"
                  :label="$t('perfMod.projectName')"
                  width="120"
                  :show-overflow-tooltip="true"
                />
                <!-- 绩效开始月份 -->
                <el-table-column
                  align="center"
                  prop="perStartMonth"
                  :label="$t('perfMod.perStartMonth')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <!-- 绩效结束月份 -->
                <el-table-column
                  align="center"
                  prop="perEndMonth"
                  :label="$t('perfMod.perEndMonth')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <!-- 采购组织 -->
                <el-table-column
                  align="center"
                  prop="organizationName"
                  :label="$t('perfMod.fullPathId')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <!-- 采购分类 -->
                <el-table-column
                  align="center"
                  prop="categoryName"
                  :label="$t('perfMod.categoryName')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <!-- 供应商名称 -->
                <el-table-column
                  align="center"
                  prop="companyName"
                  :label="$t('perfMod.vendorName')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <!-- 循环拓展 -->
                <el-table-column
                  v-for="item in tableExpand"
                  align="center"
                  :prop="item.prop"
                  :label="item.label"
                  :key="item.prop"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <!-- 综合得分 -->
                <el-table-column
                  align="center"
                  prop="score"
                  :label="$t('perfMod.scoreAll')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <!-- 排名 -->
                <el-table-column
                  align="center"
                  prop="rank"
                  :label="$t('perfMod.rankAll')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <!-- 等级 -->
                <el-table-column
                  align="center"
                  prop="levelName"
                  :label="$t('perfMod.levelName')"
                  width="100"
                  :show-overflow-tooltip="true"
                />
                <!-- 操作 -->
                <el-table-column
                  :label="$t('common.operation')"
                  width="100"
                >
                  <template slot-scope="scope">
                    <el-button
                      type="text"
                      @click="rowHandel('view', scope.row)"
                    >
                      {{
                        $t('perfMod.readPerDetail')
                      }}
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-main>
          </el-container>
        </el-tab-pane>
      </el-tabs>
      <CToolbar>
        <template slot="right">
          <el-button @click="backBill">
            {{ $t('common.backTo') }}
          </el-button>
          <el-button
            v-if="
              form.projectStatus === 'SCORE_DRAFT' && curOpt !== 'view' && curOpt !== 'viewResult'
            "
            type="primary"
            @click="saveBill('save')"
          >
            {{ $t('common.save') }}
          </el-button>
          <el-button
            v-if="
              form.projectStatus === 'SCORE_DRAFT' &&
                curOpt !== 'view' &&
                curOpt !== 'viewResult' &&
                orderId
            "
            type="primary"
            @click="saveBill('submit')"
          >
            {{ $t('common.submit') }}
          </el-button>
          <!--
          <el-button
            type="primary"
            v-if="
              form.projectStatus === 'SCORE_CALCULATED' &&
                curRole === 'BUYER'
            "
            @click="approval"
          >
            提交审批</el-button
          > -->
          <!-- <el-button type="primary"  @click="approvalBill">审核</el-button> -->
        </template>
      </CToolbar>
      <!-- 人员选择 -->
      <CPeopleSelector
        ref="peopleSelector"
        :visible.sync="peopleDialog"
        :multi-select="false"
        @on-confirm="getPeople"
      />
      <!-- 任务分配 -->
      <el-dialog
        v-el-drag-dialog
        :title="$t('perfMod.perfManSupIndListDetail')"
        :visible.sync="perfManSupIndListVisible"
        :close-on-click-modal="false"
        width="880px"
      >
        <div class="the_item1">
          <el-table
            v-if="perfManSupIndListVisible"
            ref="ManSupIndTable"
            :data="perfScoreItemManSupIndList"
            style="width: 100%"
            border
            max-height="320px"
            @selection-change="handleSelectionChange"
          >
            <el-table-column type="selection" :selectable="selectableHandel" width="45px" align="center" />
            <el-table-column
              align="center"
              prop="indicatorDimension"
              :label="$t('perfMod.indicatorDimension')"
              :show-overflow-tooltip="true"
              width="100px"
              :formatter="formatterDim"
            />
            <el-table-column
              align="center"
              prop="indicatorName"
              :label="$t('perfMod.indicatorName')"
              :show-overflow-tooltip="true"
            />
          </el-table>
        </div>
        <div slot="footer" class="dialog-footer">
          <el-button
            :disabled="
              form.projectStatus !== 'SCORE_DRAFT' || curOpt === 'view' || curOpt === 'viewResult'
            "
            type="primary"
            @click="confirmListTemplate"
          >
            {{ $t('common.confirm') }}
          </el-button>
          <el-button @click="perfManSupIndListVisible = false">
            {{ $t('common.backTo') }}
          </el-button>
        </div>
      </el-dialog>
      <!-- 综合绩效明细 -->
      <el-dialog
        :title="$t('perfMod.comperInfoDetail')"
        width="1000px"
        :visible.sync="dialogSmartVisible"
        :close-on-click-modal="false"
      >
        <div v-if="dialogSmartVisible">
          <div class="the_item1">
            <p class="secSubTitle">
              {{ $t('perfMod.comperInfos') }}
            </p>
            <div class="the_display_content">
              <srm-row>
                <srm-col :initCol="3">
                  <span>{{ $t('perfMod.vendorName') }}：</span>
                  {{ performanceDetailComputed.companyName }}
                </srm-col>
                <srm-col :initCol="3">
                  <span>{{ $t('perfMod.fullPathId') }}：</span>
                  {{ performanceDetailComputed.organizationName }}
                </srm-col>
                <srm-col :initCol="3">
                  <span>{{ $t('perfMod.categoryName') }}：</span>
                  {{ performanceDetailComputed.categoryName }}
                </srm-col>
                <srm-col :initCol="3">
                  <span>{{ $t('perfMod.perStartMonth') }}：</span>
                  {{ performanceDetailComputed.perStartMonth }}
                </srm-col>
                <srm-col :initCol="3">
                  <span>{{ $t('perfMod.perEndMonth') }}：</span>
                  {{ performanceDetailComputed.perEndMonth }}
                </srm-col>
                <srm-col :initCol="3">
                  <span>{{ $t('perfMod.perModel') }}：</span>
                  {{ performanceDetailComputed.templateName }}
                </srm-col>
                <srm-col :initCol="3">
                  <span>{{ $t('perfMod.scoreAll') }}：</span>
                  {{ performanceDetailComputed.score }}
                </srm-col>
                <srm-col :initCol="3">
                  <span>{{ $t('perfMod.rankAll') }}：</span>
                  {{ performanceDetailComputed.rank }}
                </srm-col>
                <srm-col :initCol="3">
                  <span>{{ $t('perfMod.levelName') }}：</span>
                  {{ performanceDetailComputed.levelName }}
                </srm-col>
              </srm-row>
            </div>
          </div>
          <div
            v-for="(item, index) in performanceDetailComputed.perfIndicatorDimScoreList"
            :key="index"
            class="the_item1"
          >
            <p class="secSubTitle">
              {{ item.indicatorDimensionTypeName }}{{ $t('perfMod.perInformation') }}
            </p>
            <div class="the_display_content">
              <srm-row>
                <srm-col :initCol="4">
                  <span>{{ item.indicatorDimensionTypeName

                  }}{{ $t('perfMod.indicatorDimensionWeight') }}：</span>
                  {{ item.indicatorDimensionWeight }}
                </srm-col>
                <srm-col :initCol="4">
                  <span>{{ item.indicatorDimensionTypeName }}{{ $t('perfMod.rank') }}：</span>
                  {{ item.rank }}
                </srm-col>
                <srm-col :initCol="2">
                  <span>{{ item.indicatorDimensionTypeName }}{{ $t('perfMod.score') }}：</span>
                  {{ item.result }}
                </srm-col>
              </srm-row>
            </div>
            <el-table
              :data="item.perfIndDimScoreDetailList"
              style="width: 100%"
              border
              max-height="251px"
              class="mutipTablePage"
            >
              <el-table-column
                align="center"
                prop="indicatorName"
                :label="$t('perfMod.indicatorName')"
                :show-overflow-tooltip="true"
              >
                <template slot-scope="scope">
<!--                  <el-button type="text" @click="indicatorNameClick(scope.row)">-->
                    {{ scope.row.indicatorName }}
<!--                  </el-button>-->
                </template>
              </el-table-column>
              <el-table-column
                align="center"
                prop="dimensionWeight"
                :label="$t('perfMod.dimensionWeight')"
                :show-overflow-tooltip="true"
              />
              <el-table-column
                align="center"
                prop="score"
                :label="$t('perfMod.indicatorScore')"
                :show-overflow-tooltip="true"
              />
            </el-table>
          </div>
        </div>
        <div slot="footer" class="dialog-footer">
          <el-button @click="dialogSmartVisible = false">
            {{ $t('common.backTo') }}
          </el-button>
        </div>
      </el-dialog>
      <el-dialog :title="$t('common.tips')" :visible.sync="dialogVisible">
        <div style="display:flex">
          <i class="el-icon-warning dialog_i"></i>
          <div>
            <div>
              <span>{{ $t('perfMod.graderGrading') }}</span>
            </div>
            <!-- <div v-if="total>20000" style="margin-top: 10px;">
              {{ $t('relegationEntity.key23', {total: total, ceil: Math.ceil(total / 10000)}) }}
            </div> -->
          </div>
        </div>
        <span slot="footer" class="dialog-footer">
          <el-button @click="dialogVisible = false">{{ $t('common.cancel') }}</el-button>
          <el-button type="primary" :loading="confirmLoading" @click="confirmCalculateVisible">{{
              $t('common.confirm')
            }}</el-button>
        </span>
      </el-dialog>
      <el-dialog :title="$t('vendorMod.particulars')" :visible.sync="indicatorVisible">
        <div>
          <el-table
            :data="indicatorDetail"
            style="width: 100%"
            border
            max-height="251px"
            class="mutipTablePage"
          >
            <el-table-column
              align="center"
              prop="scoreNickName"
              :label="$t('vendorMod.relegation.assessor')"
              :show-overflow-tooltip="true"
            />
            <el-table-column
              align="center"
              prop="indicatorLineDes"
              :label="$t('vendorMod.indicatorLineDes')"
              :show-overflow-tooltip="true"
            />
            <el-table-column align="center" prop="score" :label="$t('bidMod.score1')" :show-overflow-tooltip="true" />
          </el-table>
        </div>
        <span slot="footer" class="dialog-footer">
          <el-button @click="indicatorVisible = false">{{ $t('common.cancel') }}</el-button>
        </span>
      </el-dialog>
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import CToolbar from 'lib@/components/c-toolbar'
import OrganizationSelectTree from 'lib@/components/organization-selector'
import QuickSearch from 'lib@/components/QuickSearch'
import { tabTodoMixin } from '@/utils/mixins'
import MainHeader from 'lib@/components/Table/MainHeader'
import CPeopleSelector from '@/library/components/c-people-selector'
import _omit from 'lodash/omit'
import { createDictClass } from '@/library/utils/dict/dict-utils'
import { performanceManagement } from 'modc@/buyer/performanceManagement/api/index'
const dictClass = createDictClass({ 'INDICATORS_DIM': [] })
import ExportExcel from 'lib@/components/export-excel'

export default {
  name: 'PerformanceScoreItemsDetail',
  components: {
    MainHeader,
    CToolbar,
    QuickSearch,
    TableView,
    OrganizationSelectTree,
    CPeopleSelector,
    ExportExcel
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      dictCodes: {
        evaluationPeriod: 'PERF_PERIOD'
      },
      confirmLoading: false,
      headerTable: [],
      activeDims: ['1', '2', '3'],
      categoryNum: 0, // 从绩效模型接口查询的品类有多少个
      total: 0, // 前端获取(绩效模板品类数量),(绩效评分项目的供应商数量),(各自评审人的指标任务总和)
      form: {
        projectName: '', // 项目名称
        templateHeadId: '', // 模板ID
        projectStatus: 'SCORE_DRAFT', // 项目状态
        organizationId: null,
        organizationName: '',
        fullPathId: null, // 组织全路径ID
        perStartMonth: '', // 绩效结束月份
        perEndMonth: '', // 绩效开始月份
        scoreStartTime: '', // 评分开始时间
        scoreEndTime: '', // 评分结束时间
        evaluationPeriod: '',
        templateName: '',
        approveStatus: '' // 审批状态
      },
      tableExpand: [],
      perfScoreItemsSupList: [], // 评分供应商
      indicatorDetail: [],
      indicatorVisible: false,
      dictClass: dictClass,
      dialogVisible: false,
      resultFlow: false,
      resultFlowParams: {}, // 结果审批流程参数
      openWorkFlow: false, // 审批流程相关参数
      curRole: this.$store.getters.userType, // VENDOR BUYER curRole==='VENDOR'
      peopleDialog: false,
      curOpt: 'add',
      orderId: null,
      funParams: {
        flag: 'add'
      },
      performanceDetail: {
        vendorName: '',
        purchseOrg: '',
        purType: '',
        startMonth: '22',
        endMonth: '333',
        companyType: '',
        legalPerson: '',
        businessStartDate: '',
        businessEndDate: '',
        businessScope: ''
      },
      approveStatus: [], // 审批状态
      editableTabsValue: 'tab1',
      perfScoreItemsManList: [], // 技术评分人
      rules: {
        projectName: [{ required: true, message: this.$t('perfMod.enterProjectName') }],
        templateHeadId: [{ required: true, message: '请选择绩效模型' }],
        perStartMonth: [{ required: true, message: '请选择绩效开始月份' }],
        perEndMonth: [{ required: true, message: '请选择绩效结束月份' }],
        scoreStartTime: [{ required: true, message: '请选择评分开始时间' }],
        scoreEndTime: [{ required: true, message: '请选择评分截止时间' }]
      },
      perfScoreItemsManSupList: [], // 供应商弹框数据
      perfManSupIndListVisible: false, // 任务分配弹框
      perfScoreItemManSupIndList: [], // 任务分配弹框数据
      selectionTask: [], // 选中的任务行
      templateData: [], // 绩效模型下拉
      pageSize: 15,
      queryParam: {},
      gridId: 'resultTableList',
      tableHeader: [],
      tableData: [],
      minDate: '', // 最小日期
      dialogSmartVisible: false,
      tempSelectionTask: [], // 临时存放数据
      pickerOptionsStart: {
        disabledDate: time => {
          const endDateVal = new Date(this.form.perEndMonth).getTime()
          if (endDateVal) {
            return time.getTime() > endDateVal - 0
          }
        }
      },
      pickerOptionsStart2: {
        disabledDate: time => {
          const endDateVal = new Date(this.form.scoreEndTime).getTime()
          if (endDateVal) {
            return time.getTime() > endDateVal - 0
          }
        }
      },
      pickerOptionsEnd: {
        disabledDate: time => {
          const beginDateVal = new Date(this.form.perStartMonth).getTime()
          if (beginDateVal) {
            return time.getTime() < beginDateVal - 0
          }
        }
      },
      pickerOptionsEnd2: {
        disabledDate: time => {
          const beginDateVal = new Date(this.form.scoreStartTime).getTime()
          if (beginDateVal) {
            return time.getTime() < beginDateVal - 0
          }
        }
      }
    }
  },
  computed: {
    preQueryData1 () {
      return { 't.ORGANIZATION_ID': this.form?.organizationId, 't.PER_START_MONTH': this.form?.perStartMonth, 't.PER_END_MONTH': this.form?.perEndMonth }
    },
    preQueryData2 () {
      return { 't.TEMPLATE_HEAD_ID': this.form.templateHeadId }
    },
    pickerOptions () {
      let _this = this
      return {
        onPick ({ maxDate, minDate }) {
          if (!maxDate) {
            _this.minDate = minDate
          }
        }
      }
    },
    performanceDetailComputed () {
      let formatDimVal = (val) => {
        if (val) {
          return this.formatterDimVal(val)
        } else {
          return '--'
        }
      }
      let obj = { ...this.performanceDetail }
      if (obj.perfIndicatorDimScoreList.length > 0) {
        obj.perfIndicatorDimScoreList.forEach((item) => {
          item.indicatorDimensionTypeName = formatDimVal(item.indicatorDimensionType)
        })
      }
      return obj
    }
  },
  created () {
    this.getTempateList() // 查询模板下拉
    this.curOpt = this.$attrs.params.flag
    if (this.$attrs.params.flag !== 'add') {
      this.orderId = this.$attrs.params.orderId
      this.getFormDetail()
    } else {
      // this.$http({
      //     url: `/api-pj/pj-anon/user/getHrUserOrgnizationByUsername?username=${this.$store.getters.userInfo.username}`,
      //     method: 'GET',
      //     loading: true
      //   }).then((res) => {
      //     const data = res.data
      //     console.log(data)
      //     // this.$set(this.requirementHead, 'ceeaDepartmentName', data.departmentOrganization?.organizationName)
      // })
    }
    // 查看绩效计算结果
    this.queryParam.scoreItemsId = this.orderId
    this.queryResults()
    if (this.curOpt === 'viewResult') {
      this.editableTabsValue = 'tab2'
    }
  },
  methods: {
    // 查询计算结果
    queryResults () {
      // const quality = this.$getDictLabel('INDICATORS_DIM', 'QUALITY')
      // console.log(quality, 'quality')
      this.$http({
        url: '/api-pef/scoring/perfOverallScore/listPerfOverallScorePage',
        method: 'POST',
        data: this.queryParam,
        loading: true
      }).then(res => {
        console.log(res, 'res')
        // this.tableExpand 表格拓展
        let list = res.data.list
        let tableExpand = [] // 表格拓展
        list?.forEach((data, index) => {
          data?.perfIndicatorDimScoreList.forEach((data2) => {
            // 明细得分
            data2.perfIndDimScoreDetailList.forEach((data3, index3) => {
              const objDetail = {
                label: this.$getDictLabel('INDICATORS_DIM', data2.indicatorDimensionType) + '-' + data3.indicatorName, // 明细得分的头信息
                prop: data2.indicatorDimensionType + index3 // 明细得分的prop
              }
              if (index == 0) {
                tableExpand.push(objDetail)
              }
              data[data2.indicatorDimensionType + index3] = data3.score // 明细得分分赋值
            })
            // 总得分
            const obj = {
              label: this.$getDictLabel('INDICATORS_DIM', data2.indicatorDimensionType) + '总得分', // 总得分显示的头信息
              prop: data2.indicatorDimensionType // 各个维度总得分的prop
            }
            if (index == 0) {
              tableExpand.push(obj)
            }
            data[data2.indicatorDimensionType] = data2.score // 各个维度总得分赋值
          })
        })
        this.tableExpand = tableExpand
        this.tableData = list
        this.headerTable = [
          {
            prop: 'projectName',
            label: () => this.$t('perfMod.projectName') // 项目名称
          },
          {
            prop: 'perStartMonth',
            label: () => this.$t('perfMod.perStartMonth') // 绩效开始月份
          },
          {
            prop: 'perEndMonth',
            label: () => this.$t('perfMod.perEndMonth') // 绩效结束月份
          },
          {
            prop: 'organizationName',
            label: () => this.$t('perfMod.fullPathId') // 采购组织
          },
          {
            prop: 'categoryName',
            label: () => this.$t('perfMod.categoryName') // 采购分类
          },
          {
            prop: 'companyName',
            label: () => this.$t('perfMod.vendorName') // 供应商名称
          },
          ...tableExpand,
          {
            prop: 'score',
            label: () => this.$t('perfMod.scoreAll') // 综合得分
          },
          {
            prop: 'rank',
            label: () => this.$t('perfMod.rankAll') // 绩效排名
          },
          {
            prop: 'levelName',
            label: () => this.$t('perfMod.levelName') // 等级名称
          }]
      })
    },
    formatterDimVal (value) {
      return this.dictClass.getDictDetail('INDICATORS_DIM', value).label
    },
    // 查询有效的绩效模型下拉数据
    getTempateList () {
      const obj = {
        attribute1: 'ORDER'
      }
      performanceManagement.getValidTemplateHeader(obj).then((res) => {
        this.templateData = res.data
      })
    },
    // 查询单据详情
    getFormDetail () {
      let scoreItemsId = this.orderId
      performanceManagement.findPerfScoreItemsById({ scoreItemsId })
        .then((res) => {
          let formInfo = res.data
          this.form = _omit(formInfo, [
            'creationDate',
            'lastUpdateDate',
            'createdBy',
            'createdByIp',
            'perfScoreItemsSupList',
            'perfScoreItemsManList'
          ])
          this.orderId = res.data.scoreItemsId || null
          this.perfScoreItemsSupList = res.data.perfScoreItemsSupList // 评分供应商
          this.perfScoreItemsManList = res.data.perfScoreItemsManList // 技术评分人
          // 将已选的指标供应商临时存起来 [[
          if (res.data.perfScoreItemsManList) {
            let arr = []
            res.data.perfScoreItemsManList.forEach((item) => {
              let perfScoreItemManSupIndList = item.perfScoreItemManSupIndList // 已选的指标供应商
              arr = [...arr, ...perfScoreItemManSupIndList]
            })
            this.tempSelectionTask = arr.map((p) => ({
              ...p,
              hasSelect: 'selected', // 已经被选中标识
              key: p.companyId + '_' + p.templateLineId // 拼接ID 作为唯一值
            }))
          }

          // 查询关联数据
          let orgId = formInfo.organizationId
          const templateHeadId = formInfo.templateHeadId

          this.categoryNumCount(templateHeadId)

          // 流程ID
          let cbpmInstaceId = res.data.cbpmInstanceId
          if (cbpmInstaceId) {
            this.openWorkFlow = true
            this.resultFlow = true
            this.resultFlowParams = {
              fdId: cbpmInstaceId
            }
            this.$nextTick(() => {
              this.editableTabsValue = 'tab3' // 切换到流程卡
            })
          }

          this.$http({
            url: '/api-pef/template/listTemplateLinesByTemplateHeaderId',
            method: 'GET',
            params: { templateHeaderId: this.form?.templateHeadId },
            loading: true
          })
            .then((data) => {
              if (data) {
                this.perfScoreItemManSupIndList = data.data
              }
            })
            .catch((err) => {
              console.log(err)
            })
        })
        .catch((err) => {
          console.log(err)
        })
    },

    // 点击计算结果
    tabClickHandel (tab) {
      if (tab.name === 'tab2') {
        this.queryParam.scoreItemsId = this.orderId
        this.queryResults()
      }
    },

    // 选择模板change 事件
    templateNameChange (val) {
      if (val) {
        const row = this.templateData.find((item) => item.templateHeadId === val)
        if (row) {
          this.form.evaluationPeriod = row.evaluationPeriod
          this.form.fullPathId = row.fullPathId
          this.form.organizationId = row.organizationId
          this.form.organizationCode = row.organizationCode
          this.form.organizationName = row.organizationName
          this.form.templateHeadId = row.templateHeadId
          this.form.templateName = row.templateName
          this.perfScoreItemsSupList = []
        }
        if (val) {
          this.categoryNumCount(val)
        }
      }
    },
    // 计算该模板有多少个
    categoryNumCount (id) {
      performanceManagement.getPefTemplateDetail({ perfTemplateHeadId: id }).then(res => {
        this.categoryNum = res?.data.perfTemplateCategoryList?.length || 0
      })
    },
    // 选择组织
    addOrgHandle (node, instanceId) {
      const { organizationCode, organizationName, organizationId } = node
      this.form.organizationCode = organizationCode
      this.form.organizationName = organizationName
      this.form.organizationId = organizationId
    },
    dateChangeHandelA (val) {
      if (val) {
        this.form.perStartMonth = val
      }
    },
    dateChangeHandelB (val) {
      // 获取月的最后的日期
      let monthEndDate = new Date(val)
      monthEndDate.setMonth(monthEndDate.getMonth() + 1)
      let EndDate = monthEndDate.setDate(0)
      EndDate = new Date(EndDate).getDate().toLocaleString()

      let monthRangeObject = val.split('-')
      if (monthRangeObject[0] && monthRangeObject[1]) {
        monthRangeObject = `${monthRangeObject[0]}-${monthRangeObject[1]}-${EndDate}`
        val = monthRangeObject
      }
      this.form.perEndMonth = val
      console.log('perStartMonth', this.form.perEndMonth)
    },
    getCompanyList (data) {
      console.log(data, 'data')
      if (data.length > 0) {
        let companyIdList = []
        for (let item of this.perfScoreItemsSupList) {
          item.companyId && companyIdList.push(item.companyId)
        }
        data.forEach(item => {
          if (item.vendorId && !companyIdList.includes(item.vendorId)) {
            this.perfScoreItemsSupList.unshift({
              companyId: item.vendorId,
              companyCode: item.vendorCode,
              companyName: item.vendorName,
              enableFlag: 'Y',
              vendorOpt: 'add'
            })
          }
        })
      }
    },
    // 删除供应商
    deleteOneContent (index, row) {
      let scoreItemsSupId = row.scoreItemsSupId
      let oldCompanyId = row.companyId // 公司ID
      if (scoreItemsSupId) {
        this.$confirm(this.$t('perfMod.affectVendorPermissionInform'), {
          confirmButtonText: this.$t('common.affirm'),
          cancelButtonText: this.$t('common.cancel'),
          type: 'warning'
        })
          .then(() => {
            performanceManagement.delPerfScoreItemsSupById({ scoreItemsSupId }).then((res) => {
              this.$message({
                message: res.message,
                type: 'success'
              })
              this.perfScoreItemsSupList.splice(index, 1)

              if (this.perfScoreItemsManList.length > 0) {
                // 有评分人信息
                this.perfScoreItemsManList.forEach((item) => {
                  item.perfScoreItemManSupIndList.forEach((elm, i) => {
                    if (oldCompanyId == elm.companyId) {
                      item.perfScoreItemManSupIndList.splice(i, 1)
                    }
                  })
                })
              }
            })
          })
          .catch(() => { })
      } else {
        this.perfScoreItemsSupList.splice(index, 1)
      }
    },
    // 添加评分人信息
    addScorer () {
      this.perfScoreItemsManList.push({
        scoreUserId: null,
        scoreUserName: '',
        scoreNickName: '',
        scoreUserEmail: '',
        scoreUserPhone: '',
        perfScoreItemManSupIndList: [] // 供应商指标
      })
    },
    getUserdemandObj (val, scope) {
      scope.scoreUserId = val ? val.userId : ''
      scope.scoreUserName = val ? val.username : ''
      scope.scoreNickName = val ? val.nickname : ''
      scope.scoreUserEmail = val ? val.email : ''
      scope.scoreUserPhone = val ? val.phone : ''
    },
    // 点击 任务分配
    scoreTaskSetting (index, row) {
      this.curRowPeople = index
      this.$http({
        url: '/api-pef/template/listTemplateLinesByTemplateHeaderId',
        method: 'GET',
        params: { templateHeaderId: this.form.templateHeadId },
        loading: true
      })
        .then((data) => {
          if (data) {
            let curPerfScoreItemsSupList = row.perfScoreItemManSupIndList
            this.perfScoreItemManSupIndList = data.data
            this.perfManSupIndListVisible = true
            if (curPerfScoreItemsSupList.length > 0) {
              let _this = this
              _this.$nextTick(() => {
                curPerfScoreItemsSupList.forEach((selected) => {
                  // 反选table操作
                  this.perfScoreItemManSupIndList.forEach((item) => {
                    if (selected.templateLineId == item.templateLineId) {
                      _this.$refs.ManSupIndTable.toggleRowSelection(item, true)
                    }
                  })
                })
              })
            }
          }
        })
        .catch((err) => {
          console.log(err)
        })
    },
    // 删除评分人
    deleteScoreUser (index, row) {
      let scoreItemsManId = row.scoreItemsManId
      if (scoreItemsManId) {
        this.$confirm(this.$t('perfMod.sureDeleteData'), {
          confirmButtonText: this.$t('common.affirm'),
          cancelButtonText: this.$t('common.cancel'),
          type: 'warning'
        })
          .then(() => {
            performanceManagement.delPerfScoreItemsManById({ scoreItemsManId }).then((res) => {
              this.$message({
                message: res.message,
                type: 'success'
              })
              this.perfScoreItemsManList.splice(index, 1)
              if (this.perfScoreItemsManList.length === 0) {
                this.tempSelectionTask = []
              }
            })
          })
          .catch(() => { })
      } else {
        this.perfScoreItemsManList.splice(index, 1)
        if (this.perfScoreItemsManList.length === 0) {
          this.tempSelectionTask = []
        }
      }
    },
    // 获取选择器
    getPeople (data) {
      let user = data[0]
      this.perfScoreItemsManList[this.curRowPeople].scoreUserId = user.userId
      this.perfScoreItemsManList[this.curRowPeople].scoreUserName = user.username
      this.perfScoreItemsManList[this.curRowPeople].scoreNickName = user.nickname
      this.perfScoreItemsManList[this.curRowPeople].scoreUserEmail = user.email
      this.perfScoreItemsManList[this.curRowPeople].scoreUserPhone = user.phone
    },
    // 选择配置任务
    handleSelectionChange (val) {
      this.selectionTask = val
    },
    selectableHandel (row, index) {
      if (row.hasSelect !== 'selected') {
        return true
      } else {
        return false
      }
    },
    formatterDim (row, column, cellValue, index) {
      return this.dictClass.getDictDetail('INDICATORS_DIM', cellValue).label
    },
    // 确认选择任务项 1-2
    confirmListTemplate () {
      if (this.perfScoreItemsSupList && this.perfScoreItemsSupList.length > 0) {
        let selections = this.selectionTask
        let perfScoreItemsSupList = this.perfScoreItemsSupList

        if (selections && selections.length > 0) {
          let selectionTaskList = []
          selections.forEach((item) => {
            perfScoreItemsSupList.forEach((v) => {
              let obj = Object.assign({}, v, item)
              selectionTaskList.push(obj)
            })
          })
          this.perfScoreItemsManList[this.curRowPeople].perfScoreItemManSupIndList =
            selectionTaskList

          this.perfManSupIndListVisible = false
        } else {
          this.perfScoreItemsManList[this.curRowPeople].perfScoreItemManSupIndList = []
          this.perfManSupIndListVisible = false
        }
      } else {
        this.$message({
          type: 'warning',
          message: this.$t('perfMod.addVendor')
        })
      }
    },
    indicatorNameClick (row) {
      let obj = {
        scoreItemsId: row.scoreItemsId,
        companyId: row.companyId,
        templateLineId: row.templateLineId
      }
      performanceManagement.scoreManScoringV1(obj).then(res => {
        this.indicatorDetail = res.data
      })
      this.indicatorVisible = true
    },
    confirmCalculateVisible () {
      this.confirmLoading = true
      let submitData = { ...this.form }
      submitData.evaluationPeriod = 'MONTHLY'
      submitData.perfScoreItemsSupList = this.perfScoreItemsSupList
      submitData.perfScoreItemsManList = this.perfScoreItemsManList
      performanceManagement.saveOrUpdatePerfScoreItems(submitData).then((res) => {
        if (res) {
          let projectStatus = 'SCORE_NOTIFIED'
          if (this.orderId) {
            let scoreItemsId = this.orderId
            performanceManagement.notifyScorers({ scoreItemsId, projectStatus }).then((res) => {
              this.$message.success(res.message)
              this.dialogVisible = false
              this.confirmLoading = false
              this.$emit('tab-remove', this.$attrs.params.tabName)
              this.__setTabTodo('performanceScoreItemsList.getQuerydata')
            }).catch((err) => {
              this.confirmLoading = false
            })
          }
          this.curOpt = 'edit'
        } else {
          this.dialogVisible = false
          this.confirmLoading = false
        }
      }).catch((err) => {
        this.confirmLoading = false
      })
    },
    // 查看明细
    rowHandel (type, row) {
      if (type === 'view') {
        let overallScoreId = row.overallScoreId
        performanceManagement.findOverallScorelById({ overallScoreId }).then((res) => {
          if (res.data) {
            this.performanceDetail = res.data
            this.dialogSmartVisible = true
          }
        })
      }
    },

    // 返回
    backBill () {
      this.$emit('tab-remove', this.$attrs.params.tabName)
      this.__setTabTodo('performanceScoreItemsList.getQuerydata')
    },
    // 保存单据
    saveBill (type) {
      this.$refs.form.validate(async valid => {
        if (valid) {
          let submitData = { ...this.form }
          submitData.evaluationPeriod = 'MONTHLY'
          // 校验评分供应商重复
          if (this.perfScoreItemsSupList && this.perfScoreItemsSupList.length > 0) {
            let companyIdArray = this.perfScoreItemsSupList.map((v) => v.companyId)
            let nary = companyIdArray.sort()
            for (let i = 0; i < nary.length - 1; i++) {
              if (nary[i] == nary[i + 1]) {
                let messageCompanyName = this.perfScoreItemsSupList.find((v) => v.companyId == nary[i])
                if (messageCompanyName.hasOwnProperty('companyName')) {
                  return this.__jump_error__(
                    'evaluateVendor',
                    null,
                    `${this.$t('perfMod.scoreSupplier')}:(${messageCompanyName.companyName})`
                  )
                }
              }
            }
          }
          submitData.perfScoreItemsSupList = this.perfScoreItemsSupList

          // 校验评分人重复
          if (this.perfScoreItemsManList && this.perfScoreItemsManList.length > 0) {
            let scoreUserNameArray = this.perfScoreItemsManList.map((v) => v.scoreUserName)
            let scoreUserNameArrayA = []
            let scoreUserNameArrayAmess = []
            for (let i = 0; i < scoreUserNameArray.length; i++) {
              if (scoreUserNameArrayA.includes(scoreUserNameArray[i])) {
                scoreUserNameArrayAmess.push(scoreUserNameArray[i])
              } else {
                scoreUserNameArrayA.push(scoreUserNameArray[i])
              }
            }
            if (scoreUserNameArrayAmess.length > 0) {
              return this.__jump_error__(
                'chooseEvaluator',
                null,
                `${this.$t('perfMod.raterDuplicate')}:(${scoreUserNameArrayAmess[0]})`
              )
            }
          }

          submitData.perfScoreItemsManList = this.perfScoreItemsManList
          if (type === 'save') {
            if (this.curOpt === 'add') {
              performanceManagement.savePerfScoreItems(submitData).then((res) => {
                if (res) {
                  this.$message({
                    message: this.$t('common.successSave'),
                    type: 'success'
                  })
                  this.curOpt = 'edit'
                  this.orderId = res.data // 单据ID
                  this.getFormDetail() // 重新查询数据
                }
              })
            } else {
              performanceManagement.saveOrUpdatePerfScoreItems(submitData).then((res) => {
                if (res) {
                  this.$message({
                    message: this.$t('common.successUpdate'),
                    type: 'success'
                  })
                  this.curOpt = 'edit'
                  this.$emit('tab-remove', this.$attrs.params.tabName)
                  this.__setTabTodo('performanceScoreItemsList.getQuerydata')
                }
              })
            }
          } else if (type === 'submit') {
            let perfScoreItemManSupInList = []
            submitData.perfScoreItemsManList?.forEach(e => {
              let curPerfScoreItemsSupList = e?.perfScoreItemManSupIndList
              if (curPerfScoreItemsSupList?.length > 0) {
                  curPerfScoreItemsSupList?.forEach((selected) => {
                    // 反选table操作
                    this.perfScoreItemManSupIndList?.forEach((item) => {
                      if (selected?.templateLineId == item?.templateLineId) {
                        perfScoreItemManSupInList.push(selected?.templateLineId)
                      }
                    })
                  })
              }
            })
            const perfScoreItemManSupInListNew = [...new Set(perfScoreItemManSupInList)]
            let total = parseInt(this.categoryNum) * parseInt(submitData?.perfScoreItemsSupList?.length) * parseInt(perfScoreItemManSupInListNew?.length)
            this.total = total
            this.dialogVisible = true
          }
        } else {
          return this.__jump_error__(
            'projectInfo',
            null,
            this.$t('common.pleasefinishRequired')
          )
        }
      })
    }
  }
}
</script>
<style scoped lang="scss">
.the-performanceScoreItemsDetail-detail {
  .form-container2 {
    padding: 5px;
  }

  .dialog_i {
    width: 50px;
    font-size: 19px;
    color: #0077ff;
  }

  .sub_header {
    padding: 4px 11px;
    background: #f4f5f7;
  }

  .el-table .el-date-editor {
    width: 135px;
  }

  .the_display_content {
    padding: 0 5px;
    margin-bottom: 10px;

    .srm-row {
      margin-bottom: 11px;

      .srm-col {
        line-height: 28px;
        color: #606266;
      }

      span {
        display: inline-block;
      }
    }
  }

  .secSubTitle {
    padding: 0 5px;
    line-height: 30px;
    background-color: #f4f5f7;
    font-size: 14px;
  }
}

.btn-flex {
  display: flex;
  margin-bottom: 16px;
}
</style>
