<template>
  <el-container
    class="flex-container-notab the_currency_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="queryForm"
        :select-dictionary="selectDictionary"
        @synchronous-value="syncFilterParams"
        @getFormData="getQuerydata"
      />
      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <ExportExcel
            :filter-params="queryParam"
            :table-header="tableHeader"
            :dict-codes="dictCodes"
            page-url="/api-pef/projectScoreHeader/listPage"
            export-mode="front"
          />
          <!-- <el-button
            v-if="curRole === 'VENDOR'"
            @click="submit"
          >
            {{ $t('common.submit') }}
          </el-button> -->
          <el-button-group style="margin-left: 10px">
            <el-button :type="buttomClick == 1 ? 'primary' : ''" @click="buttomClickFun(1)">
              全部
            </el-button>
            <el-button :type="buttomClick == 2 ? 'primary' : ''" @click="buttomClickFun(2)">
              进行中
            </el-button>
            <el-button :type="buttomClick == 3 ? 'primary' : ''" @click="buttomClickFun(3)">
              已完成
            </el-button>
          </el-button-group>
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-data="tableList"
        :table-header="tableHeader"
        :page-size="pageSize"
        :checkbox="curRole === 'VENDOR'"
        :reserve-selection="true"
        :pre-query-data="queryParam"
        :show-filter-bar="showFilterBar === 1"
        :comActive="$attrs['changeTab']"
        url="/api-pef/projectScoreHeader/listPage"
        row-key="overallScoreId"
        :check-change="handleSelectionChange"
      >
        <template #accessory="{ scope }">
          <SrmCommonFile
            :extra-data="fileInfo"
            :default-file="{
              fileId: scope.row.perfOverallScoreFileId,
              fileName: scope.row.perfOverallScoreFileName
            }"
            :readonly="!(curRole === 'VENDOR' && scope.row.vendorFeedbackStatus === 'NOT_CONFIRMED')"
            @on-change="({file}) => handleUploadSuccess(file,scope.row)"
          />
        </template>
        <template #vendorFeedbackComments="{ scope }">
          <el-input
            v-if="curRole === 'VENDOR'"
            v-model="scope.row.vendorFeedbackComments"
            style="padding-right: 56px"
            type="text"
            :placeholder="$t('common.pleaseTypeContents')"
            maxlength="100"
            show-word-limit
          />
          <span v-else>
            {{ scope.row.vendorFeedbackComments }}
          </span>
        </template>
      </TableView>
    </el-main>
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
                <span />
                <!-- <span>{{$t('perfMod.categoryName')}}：</span>
                {{ performanceDetailComputed.categoryName }} -->
              </srm-col>
              <srm-col :initCol="3">
                <span>{{ $t('perfMod.perStartMonth') }}：</span>
                {{
                  performanceDetailComputed.perStartMonth
                    ? `${performanceDetailComputed.perStartMonth.split('-')[0]}-${
                      performanceDetailComputed.perStartMonth.split('-')[1]
                    }`
                    : ''
                }}
              </srm-col>
              <srm-col :initCol="3">
                <span>{{ $t('perfMod.perEndMonth') }}：</span>
                {{
                  performanceDetailComputed.perEndMonth
                    ? `${performanceDetailComputed.perEndMonth.split('-')[0]}-${
                      performanceDetailComputed.perEndMonth.split('-')[1]
                    }`
                    : ''
                }}
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
                {{
                  performanceDetailComputed.indicatorCount
                    ? `${performanceDetailComputed.rank}/${performanceDetailComputed.indicatorCount}`
                    : performanceDetailComputed.rank
                }}
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
              <srm-col :initCol="3">
                <span>{{ item.indicatorDimensionTypeName
                }}{{ $t('perfMod.indicatorDimensionWeight') }}：</span>
                {{ item.indicatorDimensionWeight }}
              </srm-col>
              <srm-col :initCol="3">
                <span>{{ item.indicatorDimensionTypeName }}{{ $t('perfMod.rank') }}：</span>
                {{ item.rank }}
              </srm-col>
              <srm-col :initCol="3">
                <span>{{ item.indicatorDimensionTypeName }}{{ $t('perfMod.score') }}：</span>
                {{ item.score }}
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
            />
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

      <div
        slot="footer"
        class="dialog-footer"
      >
        <el-button @click="dialogSmartVisible = false">
          {{ $t('common.backTo') }}
        </el-button>
      </div>
    </el-dialog>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import ExportExcel from 'lib@/components/export-excel'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import performanceQueryDetail from './performanceQueryDetail'
import { performanceManagement } from 'modb@/performanceManagement/api/index'
import performanceModelDetail from 'modc@/buyer/performanceManagement/views/XMorderReview/orderReviewDetail'
export default {
  name: 'PerformanceQueryList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    ExportExcel
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      projectStatus: null,
      fileInfo: {
        fileModular: 'pef',
        fileFunction: 'score-man-scoring-v1',
        fileType: 'file'
      },
      dictCodes: {
        projectStatus: 'PROJECT_SCORE_HEADER_STATUS'
      },
      selectDictionary: {},
      filterParams: {},
      curRole: this.$store.getters.userType, // VENDOR BUYER curRole==='VENDOR'
      pageSize: 15,
      gridId: 'performanceQuery',
      currentRow: null,
      showFilterBar: 1,
      queryParam: {},
      dialogSmartVisible: false,
      performanceDetail: {}, // 结果明细
      queryForm: [], // 查询条件
      tableHeader: [], // 表格列数据
      tableList: [],
      tableTotal: 0, // 分页数据
      tableLoading: false,
      curOpt: 'add',
      dialogTitle: this.$t('perfMod.addRate'),
      tableSelection: [],
      langList: [],
      busTypeList: [],
      orgStatusList: [],
      catStatusList: [],
      yesOrNoList: [],
      perfPeriod: [], // 绩效期间
      projectList: [], // 项目
      lavelList: [], // 等级
      submitModel: {
        submitform: {
          businessType: '', // 业务类型
          businessTypeName: '', // 业务类型名称
          categoryStatus: '', // 品类状态
          orgStatus: '', // 组织状态
          isAllow: '' // 是否可创建
        },
        rules: {
          businessType: [{ required: true, message: this.$t('dataConfMod.msgBusinessType') }],
          categoryStatus: [{ required: true, message: this.$t('dataConfMod.msgCategoryStatus') }],
          orgStatus: [{ required: true, message: this.$t('dataConfMod.msgOrgStatus') }]
        }
      },
      buttomClick: 1
    }
  },
  computed: {
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
    let _this = this
    let queryFormCom = [
      { prop: 'projectName', label: '评分项目名称' },
      { prop: 'contractName', label: '合同名称' },
      {
        prop: 'performanceType',
        label: '履约类型',
        type: 'dict', // 字典类型
        code: 'PERF_PERFORMANCE_TYPE' // 字典code
      },
      {
        prop: 'categoryId',
        label: () => this.$t('common.category'), // 品类
        type: 'quicksearch',
        showKey: 'categoryName',
        propKey: 'categoryId',
        name: 'scc_base_purchase_category2'
      },
      { prop: 'bidManagerFullPath', label: '招标负责人部门' },
      { prop: 'contractManagerFullPath', label: '合同负责人部门' },
      {
        prop: 'organizationId',
        label: '公司',
        type: 'OUorganizationSelector',
        placeholder: this.$t('perfMod.selectOrganization')
      },
      { prop: 'buOrganizationName', label: '板块' },
      { prop: 'scoreStart', label: '评分大于' },
      { prop: 'scoreEnd', label: '评分小于' },
      { prop: 'calcDate', label: '评分时间', type: 'daterange' }
    ]
    let queryFormBuyer = [
      {
        prop: 'companyName',
        label: () => this.$t('perfMod.vendorName'),
        type: 'quicksearch',
        showKey: 'companyName',
        name: 'scc_sup_company_info_display_buyer'
      }
    ]
    let queryFormVendor = [

    ]
    if (this.curRole === 'BUYER') {
      // 采购商
      this.queryForm = queryFormCom.concat(queryFormBuyer) // Object.assign(queryFormCom, queryFormBuyer)
    } else {
      // 供应商
      this.queryForm = queryFormCom.concat(queryFormVendor) // Object.assign(queryFormCom, queryFormVendor)
    }
    this.tableHeader = [
      {
        prop: 'companyCode',
        label: '供应商编码',
        minWidth: 150
      },
      {
        prop: 'companyName',
        label: () => this.$t('perfMod.vendorName'),
        minWidth: 150
      },
      {
        prop: 'performanceType',
        label: '履约类型',
        minWidth: 150,
        dataType: 'dict', // 数据类型为字典
        code: 'PERF_PERFORMANCE_TYPE' // 字典code
      },
      {
        prop: 'projectName',
        label: '评分项目名称',
        minWidth: 150,
        showType: 'button',
        btnStyle: 'text',
        callback: function (row) {
          if (row.performanceType === 'PROJECT') {
            this.$message.info('履约类型为履约项目')
            return false
          }
          this.editTab('view', row)
        }.bind(this),
        formattor (val) {
          return val || '--'
        }
      },
      {
        prop: 'buOrganizationName',
        label: '板块',
        minWidth: 150
      },
      {
        prop: 'ouOrganizationName',
        label: '公司',
        width: '120'
      },
      {
        prop: 'contractNo',
        label: '合同编码',
        width: '120'
      },
      {
        prop: 'contractName',
        label: '合同名称',
        width: '120'
      },
      {
        prop: 'performanceCode',
        label: '履约阶段',
        width: '120',
        dataType: 'dict', // 数据类型为字典
        code: 'MILESTONE_SCHEDULE'
      },
      {
        prop: 'categoryName',
        label: '品类',
        width: '120'
      },
      {
        prop: 'projectStatus',
        label: '整体评分状态',
        width: '120',
        dataType: 'dict', // 数据类型为字典
        code: 'PROJECT_SCORE_HEADER_STATUS'
      },
      {
        prop: 'score',
        label: '得分',
        width: '120'
      },
      {
        prop: 'levelName',
        label: '等级',
        width: '120'
      },
      {
        prop: 'lastUpdateDate',
        label: '评分时间',
        width: '120'
      },
      {
        prop: 'bidCode',
        label: '招标编号',
        width: '120'
      },
      {
        prop: 'extInvestNo',
        label: '投资编号',
        width: '120'
      },
      {
        prop: 'bidEndDate',
        label: '招标结束时间',
        width: '120'
      },
      {
        prop: 'bidManager',
        label: '招标负责人',
        width: '120'
      },
      {
        prop: 'bidManagerFullPath',
        label: '招标负责人公司部门',
        width: '120'
      },
      {
        prop: 'contractManager',
        label: '合同经办人',
        width: '120'
      },
      {
        prop: 'contractManagerFullPath',
        label: '合同经办人公司部门',
        width: '120'
      },
      {
        prop: 'extCancelStatus',
        label: () => this.$t('cusEntry.common.extCancelStatus'),
        width: 120,
        formattor: val => {
          return val == '1' ? '是' : '否'
        }
      }
      // {
      //   prop: 'operation',
      //   label: () => this.$t('common.operation'),
      //   width: 120,
      //   btnStyle: 'text',
      //   fixed: 'right',
      //   showType: 'buttons',
      //   buttons: [
      //     {
      //       callback: function (row) {
      //         this.rowHandel('view', row)
      //       }.bind(this),
      //       formattor (val) {
      //         return _this.$t('perfMod.readPerDetail')
      //       },
      //       show: function (row) {
      //         return true
      //       }
      //     }
      //   ]
      // }
    ]
    this.$nextTick(() => {
      this.fatchDictData()
      this.getQuerydata()
    })
  },
  methods: {
    buttomClickFun (num) {
      this.buttomClick = num
      if (num == 1) {
        this.projectStatus = null
      } else if (num == 2) {
        this.projectStatus = 'ON_GOING'
      } else if (num == 3) {
        this.projectStatus = 'FINISHED'
      }
      this.getQuerydata()
    },
    editTab (type, row) {
      this.$emit('tab-add', {
        component: performanceModelDetail,
        params: {
          flag: 'view',
          row,
          tabName: 'performanceScoreItemsDetail' + row.projectScoreItemsId
        },
        title: row.projectName,
        name: 'performanceScoreItemsDetail' + row.projectScoreItemsId
      })
    },
    syncFilterParams (values) {
      this.filterParams = values
    },
    formatterDimVal (value) {
      return this.$getDictLabelByValue(this.indicatorsDim, value)
    },
    getQuerydata (v) {
      if (v && v.monthList) {
        v.perStartMonth = v.monthList[0]
        v.perEndMonth = v.monthList[1]
        delete v.monthList
      }
      if (v && v.calcDate) {
        v.calcDateStart = v.calcDate[0]
        v.calcDateEnd = v.calcDate[1]
        delete v.calcDate
      }
      v = Object.assign({}, { projectStatus: this.projectStatus }, v)
      this.queryParam = v
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    // 获取数据字典
    fatchDictData () {
      performanceManagement.findDistinctLevelNameList().then((res) => {
        if (res.data) {
          this.lavelList = res.data.map((i) => ({ value: i.levelName, label: i.levelName }))

          this.queryForm[2].options = this.lavelList
        }
        // levelName
      })
      if (this.curRole === 'BUYER') {
        // 项目下拉
        performanceManagement.findCalculatedScoreItemsList().then((res) => {
          if (res.data) {
            this.projectList = res.data.map((i) => ({
              value: i.scoreItemsId,
              label: i.projectName
            }))
            this.queryForm[3].options = this.projectList
          }
        })
      }
    },
    // 查看明细
    rowHandel (type, row) {
      if (type === 'view') {
        const tab = {
          component: performanceQueryDetail,
          params: {
            row
          },
          title: row.projectName,
          name: 'performanceQueryDetail' + row.projectName
        }
        this.$emit('tab-add', tab)
      }
    },
    // 选中
    handleSelectionChange (value) {
      this.tableSelection = value
    },
    submit () {
      let rowData = this.tableSelection || []
      performanceManagement.vendorConfirm(rowData).then((res) => {
        if (res) {
          this.$message({ type: 'success', message: this.$t('common.successSubmit') })
          this.$nextTick(() => {
            this.getQuerydata(this.filterParams)
          })
        }
      })
    },
    // 上传附件成功
    handleUploadSuccess (file, row) {
      const { fileId = '', fileName = '' } = file || {}
      row.perfOverallScoreFileId = fileId.toString()
      row.perfOverallScoreFileName = fileName
      let rowData = {
        overallScoreId: row.overallScoreId,
        perfOverallScoreFileId: fileId,
        perfOverallScoreFileName: fileName
      }
      performanceManagement.listPerfceeaUploadFile(rowData).then((res) => {})
    }
  }
}
</script>
<style scoped lang="scss">
.the_display_content {
  padding: 0 5px;
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
</style>
