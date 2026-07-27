<template>
  <el-container
    class="flex-container the-hierarchicalReviewDeatil-detail"
    direction="vertical"
  >
    <el-main>
      <CWorkflowMulti
        ref="workflowMulti"
        v-model="activeTabName"
        :fun-params="workflowParamsInfo"
        :button-config-info="buttonConfigInfo"
        @tab-click="workflowView"
        @workflow-handler="workflowHandler"
        @click-handler="(type) => saveBill(type)"
        @submit-direct="(type) => saveBill(type)"
        @confirm="(type, comment) => saveBill(type, comment)"
        @close-tab="back"
      >
        <div class="form-container2">
          <FormWrapper
            :form-array="preArr"
            @getFormData="getQuerydata"
          >
            <template #orgName="{ scope }">
              <OrganizationSelector
                ref="ouSelector"
                v-model="scope.organizationName"
                :parent-id="-1"
                node-type="OU"
                :placeholder="$t('common.pleaseSelect')"
                @select="(val) => selectHandler(val, scope)"
              />
            </template>
            <template #reviewYear="{ scope }">
              <el-date-picker
                v-model="scope.reviewYear"
                type="year"
                value-format="yyyy"
                :placeholder="$t('supplierRating.selectYear')"
              />
            </template>
          </FormWrapper>
          <MainHeader>
            <template slot="left">
              <AuthorityButton
                type="primary"
                @click="exportList"
              >
                {{
                  $t('common.export')
                }}
              </AuthorityButton>
            </template>
          </MainHeader>
          <TableView
            :ref="gridId"
            table-height="400px"
            :table-data="tableData"
            :table-header="tableHeader"
            :page-size="pageSize"
            :pre-query-data="queryParam"
            :open-custom-table="true"
            :auto-query="false"
            :check-change="handleSelectionChange"
            :checkbox="true"
            row-key="vendorLevelApproveId"
            url="/api-pef/perf/levellinescore/listPageByParam"
            @afterQuery="afterQueryData"
          >
            <template #orgName="{ scope }">
              <OrganizationSelector
                ref="organizationSelector"
                v-model="scope.row.orgId"
                :disabled="scope.row.dataSource === 'SYSTEM'"
                :parent-id="-1"
                node-type="OU"
                :limit="false"
                @select="(val) => selectHandler(val, scope)"
              />
            </template>
            <template #reviewYear="{ scope }">
              <el-date-picker
                v-model="scope.row.reviewYear"
                :disabled="scope.row.dataSource === 'SYSTEM'"
                type="year"
                value-format="yyyy"
                :placeholder="$t('vendorMod.datePicker')"
              />
            </template>
            <template #vendorLevel="{ scope }">
              <DictSelect
                v-model="scope.row.vendorLevel"
                code="VENDOR_LEVEL"
                :disabled="curRoleView !== 'edit'"
              />
            </template>
          </TableView>
          <srm-dialog
            :visible.sync="detailViewVisible"
            :title="$t('route.graderRating')"
            :close-on-click-modal="false"
            size="large"
          >
            <el-table
              :data="graderRatingData"
              style="width: 100%"
            >
              <el-table-column
                type="index"
                width="50"
              />
              <el-table-column
                prop="projectName"
                :label="$t('supplierRating.projectName')"
                width="140"
              />
              <el-table-column
                prop="companyName"
                :label="$t('supplierRating.supplierName')"
                width="140"
              />
              <el-table-column
                prop="organizationName"
                :label="$t('supplierRating.entity')"
              />
              <el-table-column
                prop="perStartMonth"
                :label="$t('supplierRating.perfStartMonth')"
                width="140"
              />
              <el-table-column
                prop="perEndMonth"
                :label="$t('supplierRating.perfEndMonth')"
                width="140"
              />
              <el-table-column
                prop="categoryName"
                :label="$t('supplierRating.category')"
              />
              <el-table-column
                prop="scoreAttribute1"
                :label="$t('supplierRating.averageScore')"
                width="140"
              />
              <el-table-column
                prop="scoreAttribute2"
                :label="$t('supplierRating.averageCostScore')"
                width="140"
              />
              <el-table-column
                prop="scoreAttribute3"
                :label="$t('supplierRating.averageDeliveryScore')"
                width="140"
              />
              <el-table-column
                prop="scoreAttribute4"
                :label="$t('supplierRating.averageServiceScore')"
                width="140"
              />
              <el-table-column
                prop="scoreAttribute5"
                :label="$t('supplierRating.averageTechnicalScore')"
                width="140"
              />
              <el-table-column
                prop="score"
                :label="$t('supplierRating.meanCompositeScore')"
                width="140"
              />
              <el-table-column
                prop="levelName"
                :label="$t('supplierRating.performanceRating')"
                width="140"
              >
                <template slot-scope="scope">
                  <DictSelect
                    v-model="scope.row.levelName"
                    code="VENDOR_LEVEL"
                    :disabled="true"
                  />
                </template>
              </el-table-column>
              <el-table-column
                prop="rank"
                :label="$t('perfMod.rankAll')"
              />
            </el-table>
          </srm-dialog>
        </div>
      </CWorkflowMulti>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MainHeader from 'lib@/components/Table/MainHeader'
import OrganizationSelector from 'lib@/components/organization-selector'
import WorkflowCommon from '@/library/mixins/workflow-common'
import { downloadFileLinkByPost } from 'lib@/utils/file'
import { adaptDictData, parseTime } from '@/utils'
import ExportExcel from 'lib@/components/export-excel'
import { hierarchicalRatingApi } from 'modb@/vendorHierarchicalManagement/api'

export default {
  name: 'HierarchicalReviewDeatil',
  components: {
    FormWrapper,
    TableView,
    MainHeader,
    OrganizationSelector,
    ExportExcel
  },
  mixins: [tabTodoMixin, WorkflowCommon],
  provide () {
    return { context: this }
  },
  data () {
    return {
      multipleSelection: [],
      detailViewVisible: false,
      graderRatingData: [],
      curRoleView: 'view',
      gridId: 'hierarchicalReviewDeatil',
      tableHeader: [],
      tableData: [],
      queryParam: {},
      pageSize: 15,
      username: '',
      preArr: [
        {
          prop: 'orgName',
          label: () => this.$t('supplierRating.entity'),
          type: 'slot',
          slot: 'orgName'
        },
        {
          prop: 'categoryName',
          label: () => this.$t('supplierRating.heading'),
          type: 'catSelect'
        },
        {
          prop: 'reviewYear',
          label: () => this.$t('supplierRating.assessmentYear'),
          type: 'slot',
          slot: 'reviewYear'
        },
        {
          prop: 'companyName',
          label: () => this.$t('supplierRating.supplierName'),
          type: 'quicksearch',
          showKey: 'companyName',
          propKey: 'companyName',
          name: 'scc_sup_company_info_display_buyer'
        },
        {
          prop: 'version',
          label: () => this.$t('perfMod.version')
        },
        {
          prop: 'createdBy',
          label: () => this.$t('supplierRating.creator')
        },
        {
          prop: 'vendorLevel',
          label: () => this.$t('supplierRating.classificationSuppliers'),
          type: 'dict', // 字典类型
          code: 'VENDOR_LEVEL' // 字典code
        },

        {
          prop: 'status',
          label: () => this.$t('supplierRating.status'),
          type: 'dict', // 字典类型
          code: 'LEVEL_APPROVAL_STATUS' // 字典code
        }
      ]
    }
  },
  computed: {
    viewUpdateButton () {
      // 用来控制保存、提交按钮是否可见。如果自定义按钮则无需添加
      return this.curRoleView !== 'view'
    },
    disabledUpdateButton () {
      return this.curRoleView === 'view'
    },
    workflowTabDisabled () {
      return false
    },
    workflowBusinessId () {
      // 用来指定工作流的业务ID
      let workId = null
      if (this.$attrs.params.vendorLevelApproveId) {
        workId = this.$attrs.params.vendorLevelApproveId
      } else if (this.$attrs.params.row.vendorLevelApproveId) {
        workId = this.$attrs.params.row.vendorLevelApproveId
      }
      return workId
    }
  },
  watch: {
    viewUpdateButton () {
      this.buttonConfigInfo.save.view = false
      this.buttonConfigInfo.submit.view = this.viewUpdateButton
    },
    disabledUpdateButton () {
      this.buttonConfigInfo.save.disabled = this.disabledUpdateButton
      this.buttonConfigInfo.submit.disabled = this.disabledUpdateButton
    }
  },
  created () {
    this.curRoleView = this.$attrs.params.flag || ''
    console.log('curRoleView', this.curRoleView)
    this.buttonConfigInfo.save.view = this.viewUpdateButton
    this.buttonConfigInfo.submit.view = this.viewUpdateButton
    this.buttonConfigInfo.cancel.view = false

    this.buttonConfigInfo.save.disabled = this.disabledUpdateButton
    this.buttonConfigInfo.submit.disabled = this.disabledUpdateButton
    this.username = this.$store.getters.username || ''
    let _this = this
    this.tableHeader = [
      {
        prop: 'orgName',
        type: 'OUorganizationSelector',
        label: _this.$t('supplierRating.entity'),
        width: 140,
        showType: 'slot',
        slot: 'orgName'
      },
      {
        prop: 'reviewYear',
        label: _this.$t('supplierRating.assessmentYear'),
        width: 120,
        showType: 'slot',
        slot: 'reviewYear'
      },
      {
        prop: 'version',
        label: _this.$t('perfMod.version'),
        width: 120
      },
      { prop: 'categoryFullName', label: _this.$t('supplierRating.heading'), width: 240 },

      { prop: 'companyCode', label: _this.$t('supplierRating.vendorCode'), width: 140 },
      { prop: 'companyName', label: _this.$t('supplierRating.supplierName'), width: 140 },
      {
        prop: 'vendorLevel',
        label: _this.$t('supplierRating.classificationSuppliers'),
        dataType: 'dict', // 数据类型为字典
        code: 'VENDOR_LEVEL', // 字典code
        width: 120,
        showType: 'slot',
        slot: 'vendorLevel'
      },
      {
        prop: 'dataSource',
        label: _this.$t('vendorMod.dataSources'),
        width: 120,
        dataType: 'dict', // 数据类型为字典
        code: 'LEVEL_DATA_SOURCE' // 字典code
      },
      {
        prop: 'systemSourceResult',
        label: _this.$t('supplierRating.sourceResults'),
        width: 120,
        dataType: 'dict', // 数据类型为字典
        code: 'VENDOR_LEVEL' // 字典code
      },
      {
        prop: 'perFormance',
        label: _this.$t('supplierRating.detailsReview'),
        width: 120,
        showType: 'button',
        btnStyle: 'text',
        callback: (row) => this.perFormanceDeatil(row),
        formattor () {
          return _this.$t('dataConfMod.detail') // '编辑'
        }
      },
      {
        prop: 'status',
        label: _this.$t('supplierRating.significantCondition'),
        width: 120,
        dataType: 'dict', // 数据类型为字典
        code: 'VALID_STATUS' // 字典code
      },
      {
        prop: 'currentStatus',
        label: _this.$t('supplierRating.currentDemand'),
        width: 120,
        dataType: 'dict', // 数据类型为字典
        code: 'IS_VALID' // 字典code
      },
      {
        prop: 'orderNo',
        label: _this.$t('supplierRating.classificationNumber'),
        width: 120
      },
      {
        prop: 'projectName',
        label: _this.$t('supplierRating.gradedName'),
        width: 120
      },

      {
        prop: 'auditCode',
        label: _this.$t('supplierRating.approvalNumber'),
        width: 120
      },
      // {
      //   prop: "auditStatus",
      //   label: "审批状态",
      //   formattor (val) {
      //     return _this.$getDictLabelByValue(_this.LEVEL_APPROVAL_STATUS, val);
      //   }
      // },
      {
        prop: 'createdBy',
        label: _this.$t('supplierRating.creator'),
        width: 120
      },
      {
        prop: 'creationDate',
        label: _this.$t('supplierRating.creationDate'),
        width: 120
      },
      {
        prop: 'auditBy',
        label: _this.$t('supplierRating.approver'),
        width: 120
      },
      {
        prop: 'auditDate',
        label: _this.$t('supplierRating.approvalTime'),
        width: 120
      }
    ]
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  activated () {
    this.dolayout()
  },

  methods: {
    dolayout () {
      this.$refs[this.gridId].doLayout()
    },
    perFormanceDeatil (row) {
      let data = {
        reviewYear: row.reviewYear || '',
        orgId: row.orgId || '',
        categoryId: row.categoryId || '',
        companyId: row.companyId || '',
        levelHeadId: row.levelHeadId || ''
      }
      hierarchicalRatingApi.listPageOverallScoreByParam(data).then((res) => {
        this.graderRatingData = res.data.list || []
      })
      this.detailViewVisible = true
    },
    afterQueryData (data) {},
    back () {
      this.$emit('tab-remove', this.$attrs.params.tabName)
      this.__setTabTodo('HierarchicalReviewList.getQuerydata')
    },
    getQuerydata (v) {
      if (v && v.dateList) {
        v.createStartDate = v.dateList[0]
        v.createEndDate = v.dateList[1]
      } else if (v && !v.dateList) {
        delete v.createStartDate
        delete v.createEndDate
      }
      this.queryParam = v || this.queryParam
      if (this.$attrs.params.flag === 'hierarchicalRating') {
        this.queryParam.vendorLevelApproveId = this.$attrs.params.vendorLevelApproveId
      } else {
        let auditCode = this.$attrs.params.row ? this.$attrs.params.row.auditCode : ''
        let vendorLevelApproveId = this.$attrs.params.row ? this.$attrs.params.row.vendorLevelApproveId : ''
        this.queryParam.auditCode = auditCode
        this.queryParam.vendorLevelApproveId = vendorLevelApproveId
      }
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    addHandle (row) {},
    selectHandler (val, scope) {
      scope.orgName = val.organizationName || ''
    },
    handleSelectionChange (val) {
      this.multipleSelection = val
    },
    exportList () {
      if (this.multipleSelection && this.multipleSelection.length > 0) {
        let levelLineScoreIdList = this.multipleSelection.map((i) => i.levelLineScoreId)
        downloadFileLinkByPost(
          '/api-pef/perf/levellinescore/exportExcel',
          this.$t('supplierRating.ratingDetails') + ' ' + parseTime(new Date()) + '.xlsx',
          levelLineScoreIdList
        ).catch(() => {
          this.$message.error(this.$t('components.eio.downloadFail'))
        })
      } else {
        this.$message({
          message: this.$t('supplierRating.selectedRowData'),
          type: 'warning'
        })
      }
    },
    // 指定工作流的业务类型，在定义工作流时指定
    async getWorkflowBusinessType () {
      return 'VENDORLEVEL'
    },
    getCWorkflowRefName () {
      return 'workflowMulti' // 对应CWorkflowMulti标签中的ref
    },
    // async getWorkflowBusinessVariables () { // 定义流程变量，如果没有可以不添加
    //   return {
    //     Amount: this.requirementHead.ceeaTotalBudget
    //   }
    // },
    async saveBill (type) {
      // 对于CWorkflowMulti中调用的saveBill方法
      await this.$refs[this.gridId].query()

      await this.handlerAfter(type)
      // 以下为原有的数据提交操作，在提交成功后，加入了红色部分的两行。
      // this.$http({
      //   url: saveUrl,
      //   method: "POST",
      //   data: allparam,
      //   loading: true
      // })
      //   .then(async data => {
      //     this.$message({
      //       message: "操作成功!",
      //       type: "success"
      //     });
      //     // 重新加载数据的方法需要使用同步方式，等待处理完成后下一步。加载数据的作用是更新workflowBusinessId的值
      //     await this.getFormDetail(data.data);
      //     await this.handlerAfter(type)
      //   })
      //   .catch(err => {
      //     console.log(err); //预算告警忽略标识---Y
      //     if (type === "SUBMIT") {
      //       this.requirementHead.budgetIgnore = "Y";
      //     }
      //   });
    }
  }
}
</script>
<style lang="scss" scoped>
.the-hierarchicalReviewDeatil-detail {
  .form-container2 {
    padding: 16px 16px 0 16px;
    .the_TableView {
      min-height: 400px !important;
    }
  }
}
</style>
