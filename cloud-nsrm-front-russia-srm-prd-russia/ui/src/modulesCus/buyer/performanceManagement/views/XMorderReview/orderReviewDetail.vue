<template>
  <el-container class="flex-container the-performanceModelDetail-detail" direction="vertical">
    <el-main>
      <div class="form-container2">
        <el-collapse v-model="activeDims" class="tab-form-style">
          <!-- 模板头信息 -->
          <el-collapse-item
            ref="modeFormHeader"
            title="项目信息"
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
                  <el-form-item label="项目名称">
                    <el-input v-model="modeForm.projectName" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item label="合同编码">
                    <el-input v-model="modeForm.contractNo" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item label="合同名称">
                    <el-input v-model="modeForm.contractName" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item label="供应商编码">
                    <el-input v-model="modeForm.companyCode" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item label="供应商名称">
                    <el-input v-model="modeForm.companyName" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item label="板块">
                    <el-input v-model="modeForm.buOrganizationName" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item label="公司">
                    <el-input v-model="modeForm.ouOrganizationName" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item label="履约阶段">
                    <DictSelect
                      v-model="modeForm.performanceCode"
                      disabled
                      code="MILESTONE_SCHEDULE"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item label="复核状态">
                    <DictSelect
                      v-model="modeForm.checkStatus"
                      code="PROJECT_SCORE_ITEM_CHECK_STATUS"
                      :disabled="true"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item label="招标编号">
                    <el-input v-model="modeForm.bidCode" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item label="投资编号">
                    <el-input v-model="modeForm.extInvestNo" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item label="招标结束时间">
                    <el-input v-model="modeForm.bidEndDate" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item label="招标负责人">
                    <el-input v-model="modeForm.bidManager" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item label="招标负责人公司部门">
                    <el-input v-model="modeForm.bidManagerFullPath" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item label="合同经办人">
                    <el-input v-model="modeForm.contractManager" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item label="合同经办人公司部门">
                    <el-input v-model="modeForm.contractManagerFullPath" disabled />
                  </el-form-item>
                </srm-col>
              </srm-row>
            </el-form>
          </el-collapse-item>
          <el-collapse-item ref="categoryInfo" title="评分详情" name="2">
            <p style="width:100px">
              <el-button v-if="curOpt !== 'view'" type="primary" @click="bohui">
                驳回
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
              <el-table-column
                align="center"
                prop="scoreManAccount"
                label="评分人账号"
                min-width="200"
                :show-overflow-tooltip="true"
              />
              <el-table-column
                align="center"
                prop="scoreManName"
                label="评分人姓名"
                min-width="200"
                :show-overflow-tooltip="true"
              />
              <el-table-column
                align="center"
                prop="categoryName"
                label="品类"
                min-width="200"
                :show-overflow-tooltip="true"
              />
              <el-table-column
                align="center"
                prop="approveStatus"
                label="审批状态"
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
                    查看
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-collapse-item>
        </el-collapse>
        <el-dialog
          title="评分详情"
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
            />
          </el-table>
          <p>复核驳回信息</p>
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
            <el-table-column
              align="center"
              prop="scoreRound"
              label="轮次"
            />
            <el-table-column
              align="center"
              prop="rejectInfo"
              label="招标驳回说明"
              width="180"
            />
            <el-table-column
              align="center"
              prop="rejectDate"
              label="驳回时间"
            />
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
            通过
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
        this.$message.error('评分状态为审批通过才能驳回')
        return false
      }
      this.$prompt('驳回原因', '驳回', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
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
            message: '操作成功',
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
        this.$message.error('评分详情的数据都审批通过，才允许通过')
        return false
      }
      const projectScoreItemsId = this.curOrderId
      this.$http({
        url: '/api-pef/pj/projectScoreItems/calcScore',
        method: 'GET',
        params: { projectScoreItemsId },
        loading: true
      }).then(res => {
        this.$message.success('计算成功')
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
