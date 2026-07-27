<template>
  <el-container class="flex-container the-performanceModelDetail-detail" direction="vertical">
    <el-main>
      <div class="form-container2">
        <el-collapse v-model="activeDims" class="tab-form-style">
          <!-- 模板头信息 -->
          <!-- 项目信息 -->
          <el-collapse-item
            ref="modeFormHeader"
            :title="$t('vendorMod.itemInformation')"
            name="1"
            style="border-top:1px solid #e6ebf5;"
          >
            <el-form
              ref="modeForm"
              :model="modeForm"
              label-position="top"
              class="form-incontainer"
              :disabled="curOpt === 'view'"
            >
              <srm-row>
                <srm-col>
                  <!-- 项目名称 -->
                  <el-form-item :label="$t('bidMod.bidingName')">
                    <el-input v-model="modeForm.projectName" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 合同编码 -->
                  <el-form-item :label="$t('bidMod.category_contractCode')">
                    <el-input v-model="modeForm.contractNo" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 合同名称 -->
                  <el-form-item :label="$t('vendorMod.contractName')">
                    <el-input v-model="modeForm.contractName" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 供应商编码 -->
                  <el-form-item :label="$t('common.vendorCode')">
                    <el-input v-model="modeForm.companyCode" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 供应商名称 -->
                  <el-form-item :label="$t('common.companyName')">
                    <el-input v-model="modeForm.companyName" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 板块 -->
                  <el-form-item :label="$t('cusEntry.bidSuperviseReport.extOrgBuName')">
                    <el-input v-model="modeForm.buOrganizationName" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 公司 -->
                  <el-form-item :label="$t('components.organization.COMPANY')">
                    <el-input v-model="modeForm.ouOrganizationName" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 履约阶段 -->
                  <el-form-item label="$t('cusEntry.supplement20250121.performanceStage')">
                    <DictSelect
                      v-model="modeForm.performanceCode"
                      disabled
                      code="MILESTONE_SCHEDULE"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 复核状态 -->
                  <el-form-item label="$t('cusEntry.supplement20250205.reviewStatus')">
                    <DictSelect
                      v-model="modeForm.checkStatus"
                      code="PROJECT_SCORE_ITEM_CHECK_STATUS"
                      :disabled="true"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 招标编号 -->
                  <el-form-item :label="$t('cusEntry.bidMod.extProjectNo')">
                    <el-input v-model="modeForm.bidCode" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 投资编号 -->
                  <el-form-item :label="$t('cusEntry.bidMod.investNum')">
                    <el-input v-model="modeForm.extInvestNo" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 招标结束时间 -->
                  <el-form-item :label="$t('cusEntry.supplement20250205.bidEndTime')">
                    <el-date-picker
                      v-model="modeForm.bidEndDate"
                      type="date"
                      :format="$formatDatePicker"
                      disabled
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 招标专家 -->
                  <el-form-item label="$t('cusEntry.bidSuperviseReport.souPrincipal')">
                    <el-input v-model="modeForm.bidManager" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 招标专家公司部门 -->
                  <el-form-item label="$t('cusEntry.supplement20250205.bidExpertCompanyDept')">
                    <el-input v-model="modeForm.bidManagerFullPath" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 合同经办人 -->
                  <el-form-item label="$t('cusEntry.supplement20250205.contractManager')">
                    <el-input v-model="modeForm.contractManager" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 合同经办人公司部门 -->
                  <el-form-item label="$t('cusEntry.supplement20250205.contractHandlerCompanyDepartment')">
                    <el-input v-model="modeForm.contractManagerFullPath" disabled />
                  </el-form-item>
                </srm-col>
              </srm-row>
            </el-form>
          </el-collapse-item>
          <!-- 评分详情 -->
          <el-collapse-item ref="categoryInfo" title="$t('cusEntry.bidMod.techScoreDetail')" name="2">
            <p style="width:100px">
              <el-button v-if="curOpt !== 'view'" type="primary" @click="bohui">
                <!-- 驳回 -->
                {{ $t("components.approvalHead.headers.refuse") }}
              </el-button>
            </p>
            <el-table
              :data="modeForm.personList"
              style="width: 100%; margin-bottom: 10px"
              border
              height="250px"
              @selection-change="handleSelectionChange"
            >
              <el-table-column
                type="selection"
                width="55"
              />
              <!-- 评分人账号 -->
              <el-table-column
                align="center"
                prop="scoreManAccount"
                :label="$t('perfMod.scoreUserName')"
                min-width="200"
                :show-overflow-tooltip="true"
              />
              <!-- 评分人姓名 -->
              <el-table-column
                align="center"
                prop="scoreManName"
                :label="$t('perfMod.scoreNickName')"
                min-width="200"
                :show-overflow-tooltip="true"
              />
              <!-- 品类 -->
              <el-table-column
                align="center"
                prop="categoryName"
                :label="$t('common.category')"
                min-width="200"
                :show-overflow-tooltip="true"
              />
              <!-- 审批状态 -->
              <el-table-column
                align="center"
                prop="approveStatus"
                :label="$t('common.approvalStatus')"
                min-width="200"
                :show-overflow-tooltip="true"
              >
                <template slot-scope="scope">
                  <DictSelect
                    v-model="scope.row.approveStatus"
                    code="PROJECT_SCORE_MAN_STATUS"
                    :disabled="true"
                  />
                </template>
              </el-table-column>
              <el-table-column :label="$t('common.operation')" width="80">
                <template slot-scope="scope">
                  <el-button type="text" @click="detailShow(scope.row)">
                    <!-- 查看 -->
                    {{ $t("common.view") }}
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-collapse-item>
        </el-collapse>
        <!-- 评分详情 -->
        <el-dialog
          :title="$t('cusEntry.bidMod.techScoreDetail')"
          :visible.sync="dialogVisible"
          width="80%"
        >
          <el-table
            :data="detailListAll"
            border
            style="width: 100%"
          >
            <el-table-column
              type="index"
              width="50"
            />
            <!-- 指标维度 -->
            <el-table-column
              prop="indicatorDimensionType"
              :label="$t('perfMod.indicatorDimensionType')"
              show-overflow-tooltip
              sortable
              min-width="100"
            >
              <template slot-scope="scope">
                {{ $getDictLabel('INDICATORS_DIM', scope.row.indicatorDimensionType) }}
              </template>
            </el-table-column>
            <!-- 指标名称 -->
            <el-table-column
              prop="indicatorName"
              :label="$t('perfMod.indicatorName')"
              min-width="210"
              sortable
              :show-overflow-tooltip="true"
            >
              <template slot-scope="scope">
                <span>{{ scope.row.indicatorName }}</span>
              </template>
            </el-table-column>
            <!-- 打分逻辑 -->
            <el-table-column
              prop="indicatorLogic"
              :label="$t('perfMod.indicatorLogic_1')"
              min-width="300"
              sortable
            />
            <!-- 绩效得分 -->
            <el-table-column
              prop="score"
              :label="$t('perfMod.perScore')"
              min-width="160"
              sortable
              show-overflow-tooltip
            />
            <el-table-column
              prop="indicatorName"
              :label="$t('perfMod.indicatorName')"
              min-width="210"
              sortable
              :show-overflow-tooltip="true"
            >
              <template slot-scope="scope">
                <span>{{ scope.row.indicatorName }}</span>
              </template>
            </el-table-column>
            <!-- 打分说明 -->
            <el-table-column
              prop="comments"
              :label="$t('perfMod.scoreShows')"
              min-width="200"
              sortable
              show-overflow-tooltip
            />
            <!-- 评分时间 -->
            <el-table-column
              prop="scoreDate"
              :label="$t('perfMod.evalutionDate')"
              min-width="150px"
              sortable
              show-overflow-tooltip
            >
              <template slot-scope="scope">
                {{$parseTime(scope.row.scoreDate)}}
              </template>
            </el-table-column>
          </el-table>
          <!-- <p>复核驳回信息</p> -->
          <p>{{ $t("cusEntry.supplement20250205.reviewRejectionInfo") }}</p>
          <el-table
            :data="rejectInfoList"
            style="width: 100%; margin-top:10px;"
            border
            max-height="250px"
          >
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
          <span
            slot="footer"
            class="dialog-footer"
          >
            <el-button @click="dialogVisible = false">{{ $t('common.cancel') }}</el-button>
          </span>
        </el-dialog>
      </div>

      <CToolbar>
        <template slot="right">
          <el-button @click="backTo">
            {{ $t('common.backTo') }}
          </el-button>
          <el-button v-if="curOpt !== 'view'" type="primary" @click="saveBill">
            <!-- 通过 -->
            {{ $t("components.approvalHead.headers.pass") }}
          </el-button>
        </template>
      </CToolbar>
    </el-main>
  </el-container>
</template>
<script>
import CToolbar from 'lib@/components/c-toolbar'
import OrganizationSelectTree from 'lib@/components/organization-selector'
import CCategorySelect from 'lib@/components/c-category-select'
import { tabTodoMixin } from '@/utils/mixins'
import _pick from 'lodash/pick'
import { performanceManagement } from 'modc@/buyer/performanceManagement/api/index'
import { includes } from '@meicloud/render-engine'

export default {
  name: 'PerformanceModelDetail',
  components: {
    CToolbar,
    OrganizationSelectTree,
    CCategorySelect
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      activeDims: ['1', '2', '3', '4'],
      detailListAll: [],
      dialogVisible: false,
      modeForm: {
        projectName: '',
        perEndMonth: '',
        perStartMonth: '',
        companyName: '',
        companyId: '',
        companyCode: '',
        organizationName: '',
        organizationId: '',
        rejectRemark: '',
        personList: []
      },
      multipleSelection: [],
      rejectInfoList: []
    }
  },
  created () {
    this.curOpt = this.$attrs.params.flag
    if (this.$attrs.params.flag !== 'add') {
      this.curOrderId = this.$attrs.params.row.projectScoreItemsId
      this.getFormDetail()
    }
  },
  methods: {
    // 选择明细行
    handleSelectionChange (val) {
      this.multipleSelection = val
    },
    // 驳回
    bohui () {
      const multipleSelection = this.multipleSelection
      let bol = false // 判断是否提交，部分提交状态
      multipleSelection.forEach(data => {
        if (!['APPROVED'].includes(data.approveStatus)) {
          bol = true
        }
      })
      if (bol) {
        // this.$message.error('评分状态为审批通过才能驳回')
        this.$message.error(this.$t('cusEntry.supplement20250205.scoreStatusApprovedBeforeRejection'))
        return false
      }
      // 驳回原因
      this.$prompt(this.$t('vendorMod.rejectReason'), this.$t('components.approvalHead.headers.refuse'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('components.common.cancel'),
        inputType: 'textarea'
      }).then(({ value }) => {
        const obj = {
          'rejectRemark': value,
          'personList': multipleSelection
        }
        this.$http({
          url: '/api-pef/pj/projectScoreItems/reject',
          method: 'POST',
          data: obj,
          loading: true
        }).then(data => {
          this.$message({
            message: this.$t('components.approvalHead.tips.approvalCompletion'),
            type: 'success'
          })
          this.getFormDetail()
        })
          .catch(err => {
            console.log(err)
          })
      })
    },
    // 查询单据详情
    getFormDetail () {
      let projectScoreItemsId = this.curOrderId
      performanceManagement.getFuheXMDetail({ projectScoreItemsId }).then(res => {
        this.modeForm = res.data
      })
    },
    detailShow (row) {
      const obj = {
        projectScoreItemsId: this.curOrderId,
        categoryId: row.categoryId,
        scoreManAccount: row.scoreManAccount
      }
      this.$http({
        url: '/api-pef/pj/projectScoreItems/listScoreManDetailList',
        method: 'POST',
        data: obj,
        loading: true
      }).then(res => {
        this.detailListAll = res.data
        this.dialogVisible = true
        if (this.detailListAll.length) {
          const projectScoreManId = this.detailListAll[0].projectScoreManId
          this.$http({
            url: `/api-pef/pj/projectScoreItems/listRejectInfo/${projectScoreManId}`,
            method: 'GET'
          }).then(res => {
            this.rejectInfoList = res.data || []
          })
        }
      }).catch(err => {
        console.log(err)
      })
    },
    // 返回
    backTo () {
      this.$emit('tab-remove', this.$attrs.params.tabName)
      this.__setTabTodo('performanceModelList.getQuerydata')
    },
    // 通过
    saveBill () {
      let bol = false
      this.modeForm.personList.forEach(e => {
        if (e.approveStatus != 'APPROVED') {
          bol = true
        }
      })
      if (bol) {
        this.$message.error(this.$t('cusEntry.supplement20250205.scoreDetailsApprovedOnlyAllowThrough'))  // '评分详情的数据都审批通过，才允许通过'
        return false
      }
      const projectScoreItemsId = this.curOrderId
      this.$http({
        url: '/api-pef/pj/projectScoreItems/calcScore',
        method: 'GET',
        params: { projectScoreItemsId },
        loading: true
      }).then(res => {
        this.$message.success(this.$t('cusEntry.supplement20250205.calculateSuccess'))  // '计算成功'
        this.backTo()
      }).catch(err => {
        console.log(err)
      })
    }
  }
}
</script>
<style scoped lang="scss">
.table-header {
  background: #F1F2F2;
  border: 1px solid #DCDDDE;
  padding: 10px 15px;
}

.table-body {
  border: 1px solid #DCDDDE;
  padding: 10px 15px;
}

.title {
  display: flex;
  padding: 10px 0;

  i {
    display: block;
    width: 4px;
    height: 14px;
    background: #0077FF;
    margin-right: 15px;
    margin-top: 5px;
  }
}

.detail-wd {
  height: 30px;
  border: 1px dashed #96999C;
  border-radius: 4px;
  cursor: pointer;
  text-align: center;
  margin-bottom: 15px;
}

.the-performanceModelDetail-detail {
  .form-container2 {
    padding: 5px;
  }

  .modelDimLineInfo {
    .dimCont {
      margin-bottom: 35px;

      .el-form {
        background-color: #f4f5f7;
        padding-top: 20px;

        .el-form-item__label {
          line-height: 32px !important;
        }
      }
    }
  }

  .dimSelect {
    .optionFirst {
      padding: 0 !important;
      text-align: center;
      background-color: #f5f7fa;
    }
  }

  .sub_header {
    padding: 4px 11px;
    background: #eee;
  }

  .el-table .el-date-editor {
    width: 135px;
  }

  .the_display_content {
    .srm-row {
      .srm-col {
        margin-bottom: 10px;
        line-height: 28px;
        height: 28px;
        overflow: hidden;
        text-overflow: ellipsis;
        word-break: break-all;
        white-space: nowrap;
      }
    }

    .the_display_footer {
      text-align: center !important;
    }
  }
}

.toRequired {
  color: #ff4949;
  padding-right: 2px;
}
</style>
