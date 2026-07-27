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
            page-url="/api-pef/scoring/perfOverallScore/listPerfOverallScorePage"
            export-mode="front"
          />
          <el-button
            v-if="curRole === 'VENDOR'"
            @click="submit"
          >
            {{ $t('common.submit') }}
          </el-button>
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
        url="/api-pef/scoring/perfOverallScore/listPerfOverallScorePage"
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
import { perVendorApi } from 'mods@/performanceManagementSupplier/api'
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
      fileInfo: {
        fileModular: 'pef',
        fileFunction: 'score-man-scoring-v1',
        fileType: 'file'
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
      }
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
    this.queryForm = [
      {
        prop: 'organizationId',
        label: () => this.$t('perfMod.fullPathId'),
        type: 'OUorganizationSelector',
        placeholder: this.$t('perfMod.selectOrganization')
      },
      { prop: 'templateName', label: () => this.$t('perfMod.templateName') },
      {
        prop: 'levelName',
        label: () => this.$t('perfMod.levelName'),
        type: 'select',
        options: this.lavelList
      },
      { prop: 'monthList', label: () => this.$t('perfMod.monthList'), type: 'monthrange' }
    ]
    this.tableHeader = [
      {
        prop: 'projectName',
        label: () => this.$t('perfMod.projectName'),
        minWidth: 150
      },
      {
        prop: 'perStartMonth',
        label: () => this.$t('perfMod.perStartMonth'),
        width: '120',
        formattor (val) {
          return val ? val.substr(0, 7) : ''
        }
      },
      {
        prop: 'perEndMonth',
        label: () => this.$t('perfMod.perEndMonth'),
        width: '120',
        formattor (val) {
          return val ? val.substr(0, 7) : ''
        }
      },
      {
        prop: 'templateName',
        label: () => this.$t('perfMod.templateName'),
        width: '100'
      },
      {
        prop: 'categoryFullName',
        label: () => this.$t('perfMod.templateCategory'),
        width: '100'
      },
      {
        prop: 'organizationName',
        label: () => this.$t('perfMod.fullPathId'),
        width: '120'
      },
      {
        prop: 'companyName',
        label: () => this.$t('perfMod.vendorName'),
        minWidth: 150
      },
      {
        prop: 'scoreAttribute1',
        label: () => this.$t('perfMod.scoreAttribute1'),
        width: '100'
      },
      {
        prop: 'scoreAttribute2',
        label: () => this.$t('perfMod.scoreAttribute2'),
        width: '100'
      },
      {
        prop: 'scoreAttribute3',
        label: () => this.$t('perfMod.scoreAttribute3'),
        width: '100'
      },
      {
        prop: 'scoreAttribute4',
        label: () => this.$t('perfMod.scoreAttribute4'),
        width: '100'
      },
      {
        prop: 'scoreAttribute5',
        label: () => this.$t('perfMod.scoreAttribute5'),
        width: '100'
      },
      {
        prop: 'score',
        label: () => this.$t('perfMod.scoreAll'),
        width: '100'
      },
      {
        prop: 'rank',
        label: () => this.$t('perfMod.rankAll'),
        width: '100'
      },
      {
        prop: 'levelName',
        label: () => this.$t('perfMod.levelName'),
        width: '100'
      },
      {
        label: () => this.$t('perfMod.accessory'),
        width: '180',
        slot: 'accessory',
        showType: 'slot'
      },
      {
        prop: 'vendorFeedbackComments',
        label: () => this.$t('perfMod.vendorFeedbackComments'),
        width: '280',
        slot: 'vendorFeedbackComments',
        showType: 'slot'
      },
      {
        prop: 'operation',
        label: () => this.$t('common.operation'),
        width: 120,
        btnStyle: 'text',
        fixed: 'right',
        showType: 'buttons',
        buttons: [
          {
            callback: function (row) {
              this.rowHandel('view', row)
            }.bind(this),
            formattor (val) {
              return _this.$t('perfMod.readPerDetail')
            },
            show: function (row) {
              return true
            }
          }
        ]
      }
    ]
    this.$nextTick(() => {
      this.fatchDictData()
      this.getQuerydata()
    })
  },
  methods: {
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
      this.queryParam = v
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    // 获取数据字典
    async fatchDictData () {
      const { data = [] } = await perVendorApi.findDistinctLevelNameList()
      this.lavelList = data.map((i) => ({ value: i.levelName, label: i.levelName }))
      this.$set(this.queryForm, 2, {
        prop: 'levelName',
        label: () => this.$t('perfMod.levelName'),
        type: 'select',
        options: this.lavelList
      } )
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
      perVendorApi.vendorConfirm(rowData).then((res) => {
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
      perVendorApi.listPerfceeaUploadFile(rowData).then((res) => {})
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
