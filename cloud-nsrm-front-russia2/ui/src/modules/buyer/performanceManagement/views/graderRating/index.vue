<template>
  <el-container
    class="flex-container-notab the_material_wrapper the-graderRating-detail"
    direction="vertical"
  >
    <CustomTable
      class="custom-table"
      page-view-config-code="graderRating"
      @updataConfig="updataConfig"
      :needInit="true"
    />
    <el-main>
      <FormWrapper
        :form-array="queryForm"
        :pre-form-obj="preFormObj"
        @synchronous-value="syncFilterParams"
        @getFormData="getQuerydata"
      />
      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <el-button
            v-if="userType !== 'VENDOR'"
            type="primary"
            @click="saveDataHandle(true)"
          >
            {{ $t('common.submit') }}
          </el-button>
          <el-button
            v-if="userType !== 'VENDOR'"
            @click="doAbstention"
          >
            {{
              $t('perfMod.abstention')
            }}
          </el-button>
          <!-- <m-import
            v-if="userType !== 'VENDOR'"
            ref="import"
            :title="iModal1.title"
            :is-down-load-temp="true"
            :up-load-url="iModal1.upLoadUrl"
            :extra-data="extraData"
            type="default"
            @downloadTemplate="downloadTemplate1"
            @handleSuccess="handleSuccess"
          /> -->
        </template>
      </MainHeader>
      <el-container
        direction="vertical"
        class="tablePd"
        style="padding:0;"
      >
        <el-main style="flex-grow: 1; display: flex; flex-direction: column; position: relative">
          <el-form
            ref="graderTableForm"
            class="tableForm"
            :model="graderForm"
            :rules="graderForm.rules"
          >
            <el-table
              ref="graderTable"
              v-loading="loading"
              element-loading-background="rgba(0, 0, 0, 0.4)"
              stripe
              border
              height="100%"
              :data="graderForm.tableData"
              style="height: 100%"
              @selection-change="handleSelectionChange"
              @cell-click="cellClick"
            >
              <el-table-column
                type="selection"
                fixed="left"
                align="center"
              />
              <el-table-column
                prop="projectName"
                :label="$t('perfMod.projectName')"
                min-width="120"
                sortable
              >
                <template slot-scope="scope">
                  <span>{{ scope.row.projectName }}</span>
                </template>
              </el-table-column>
              <el-table-column
                prop="companyName"
                :label="$t('perfMod.vendorName')"
                min-width="150"
                sortable
                show-overflow-tooltip
              >
                <template slot-scope="scope">
                  <span>{{ scope.row.companyName }}</span>
                </template>
              </el-table-column>
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
              <el-table-column
                prop="categoryName"
                :label="$t('perfMod.modelCategory')"
                min-width="100"
                :show-overflow-tooltip="true"
                sortable
              />
              <el-table-column
                v-if="userType !== 'VENDOR'"
                prop="indicatorLogic"
                :label="$t('perfMod.indicatorLogic_1')"
                min-width="300"
                sortable
              />
              <el-table-column
                v-if="userType !== 'VENDOR'"
                prop="indicatorLineDes"
                :label="$t('perfMod.indicatorLineDes')"
                min-width="230"
                sortable
              >
                <template
                  slot="header"
                  slot-scope="scope"
                >
                  <span>
                    <i class="toRequired">*</i>
                    {{ $t('perfMod.indicator') }}
                    {{ $t('perfMod.onePerMark') }}
                  </span>
                  <el-tooltip
                    class="item"
                    effect="dark"
                    :content="$t('perfMod.dropdownTextConversion')"
                    placement="top"
                  >
                    <i
                      class="el-icon-question"
                      style="margin-left: 3px"
                    />
                  </el-tooltip>
                </template>
                <template v-slot="scope">
                  <el-form-item
                    :prop="'tableData.' + scope.$index + '.templateIndsLineId'"
                    :rules="graderForm.rules.templateIndsLineId"
                  >
                    <!-- TEXT_CONVERSION 文本折算  -->
                    <!-- INTERVAL_CONVERSION 区间折算  -->
                    <!-- DIRECT_QUOTE 直接取值 -->
                    <el-select
                      v-if="scope.row.quoteMode === 'TEXT_CONVERSION'"
                      v-model="scope.row.templateIndsLineId"
                      :disabled="scope.row.status !== 'SCORE_DRAFT'"
                      :placeholder="$t('perfMod.indicator')"
                      @focus="selectFocus(scope.$index)"
                      @change="value => indicatorLineChange(value, scope.row)"
                    >
                      <el-option
                        v-for="item in scope.row.indicatorsLines"
                        :key="item.indicatorLineId"
                        :label="item.indicatorLineDes"
                        :value="item.indicatorLineId"
                      />
                    </el-select>
                    <el-input
                      v-if="scope.row.quoteMode === 'INTERVAL_CONVERSION'"
                      v-model="scope.row.pefScore"
                      v-input-format="{ type: 'number' }"
                      :disabled="scope.row.status !== 'SCORE_DRAFT'"
                      @focus="selectFocus(scope.$index)"
                      @change="value => pefScoreChange(value, scope.row)"
                    />
                    <el-input
                      v-if="scope.row.quoteMode === 'DIRECT_QUOTE'"
                      v-model="scope.row.pefScore"
                      v-input-format="{ type: 'number' }"
                      :disabled="scope.row.status !== 'SCORE_DRAFT'"
                      @focus="selectFocusA(scope.$index)"
                      @input="value => pefDirectScoreChange(value, scope.row)"
                    />
                  </el-form-item>
                </template>
              </el-table-column>
              <el-table-column
                prop="score"
                :label="$t('perfMod.perScore')"
                min-width="160"
                sortable
                show-overflow-tooltip
              >
                <template
                  slot="header"
                  slot-scope="scope"
                >
                  <span><i class="toRequired">*</i>{{ $t('perfMod.perScore') }}</span>
                </template>
                <template v-slot="scope">
                  <el-form-item
                    :prop="'tableData.' + scope.$index + '.score'"
                    :rules="graderForm.rules.score"
                  >
                    <el-input
                      v-model="scope.row.score"
                      class="pefScoreInput"
                      :controls="false"
                      :disabled="
                        ((scope.row.quoteMode === 'TEXT_CONVERSION' ||
                          scope.row.quoteMode === 'INTERVAL_CONVERSION' ||
                          scope.row.quoteMode === 'DIRECT_QUOTE') &&
                          scope.row.status === 'SCORE_DRAFT') ||
                          scope.row.status !== 'SCORE_DRAFT'
                      "
                    />
                  </el-form-item>
                </template>
              </el-table-column>
              <el-table-column
                v-if="userType !== 'VENDOR'"
                prop="ifEndScored"
                :label="$t('perfMod.ifEndScored')"
                show-overflow-tooltip
                sortable
                min-width="140"
              >
                <template slot-scope="scope">
                  <span>{{
                    scope.row.ifEndScored === 'Y' ? $t('common.yes') : $t('common.no')
                  }}</span>
                </template>
              </el-table-column>

              <el-table-column
                prop="perStartMonth"
                :label="$t('perfMod.perStartMonth')"
                show-overflow-tooltip
                sortable
                min-width="120"
              >
                <template slot-scope="scope">
                  <span>{{
                    scope.row.perStartMonth
                      ? `${scope.row.perStartMonth.split('-')[0]}-${
                        scope.row.perStartMonth.split('-')[1]
                      }`
                      : ''
                  }}</span>
                </template>
              </el-table-column>
              <el-table-column
                prop="perEndMonth"
                :label="$t('perfMod.perEndMonth')"
                show-overflow-tooltip
                sortable
                min-width="120"
              >
                <template slot-scope="scope">
                  <span>{{
                    scope.row.perEndMonth
                      ? `${scope.row.perEndMonth.split('-')[0]}-${
                        scope.row.perEndMonth.split('-')[1]
                      }`
                      : ''
                  }}</span>
                </template>
              </el-table-column>
              <el-table-column
                v-if="userType !== 'VENDOR'"
                prop="organizationName"
                :label="$t('perfMod.orgName')"
                min-width="120"
                sortable
                show-overflow-tooltip
              >
                <template slot-scope="scope">
                  <span>{{ scope.row.organizationName }}</span>
                </template>
              </el-table-column>
              <el-table-column
                v-if="userType !== 'VENDOR'"
                prop="scoreUserName"
                :label="$t('perfMod.evalutionBy')"
                min-width="180"
                sortable
                show-overflow-tooltip
              >
                <template slot-scope="scope">
                  {{
                    scope.row.scoreUserName
                      ? `${scope.row.scoreNickName}(${scope.row.scoreUserName})`
                      : ''
                  }}
                </template>
              </el-table-column>
              <el-table-column
                prop="comments"
                :label="$t('perfMod.scoreShows')"
                min-width="200"
                sortable
                show-overflow-tooltip
              >
                <template #header>
                  <span>
                    <i class="toRequired">*</i>
                    {{ $t('perfMod.scoreShows') }}
                  </span>
                </template>
                <template slot-scope="scope">
                  <el-input
                    v-model="scope.row.comments"
                    :placeholder="$t('common.pleaseTypeContents')"
                    maxlength="100"
                  />
                </template>
              </el-table-column>

              <el-table-column
                v-if="userType !== 'VENDOR'"
                prop="lastUpdateDate"
                :label="$t('perfMod.evalutionDate')"
                min-width="150px"
                sortable
                show-overflow-tooltip
                :formatter="(row, column, cellValue) => $parseTime(cellValue)"
              />
            </el-table>
          </el-form>
          <el-dialog
            :title="$t('common.tips')"
            :visible.sync="dialogVisible"
          >
            <div v-if="calculateMessage.length > 0">
              <li
                v-for="item in calculateMessage"
                :key="item"
                style="list-style: none"
              >
                {{ item }}
              </li>
              <li>{{ $t('perfMod.confirmSubmission') }}</li>
            </div>
            <div v-else>
              <span>{{ $t('perfMod.selecteDataGrader') }}</span>
            </div>

            <span
              slot="footer"
              class="dialog-footer"
            >
              <el-button @click="dialogVisible = false">{{ $t('common.cancel') }}</el-button>
              <el-button
                type="primary"
                @click="confirmCalculateVisible"
              >{{
                $t('common.confirm')
              }}</el-button>
            </span>
          </el-dialog>
          <el-dialog
            :title="$t('common.tips')"
            :visible.sync="dialogVisible2"
            width="30%"
          >
            <el-table
              :data="getScoreItemsAfterVisible"
              style="width: 100%"
            >
              <el-table-column
                prop="projectName"
                :label="$t('perfMod.projectName')"
                show-overflow-tooltip
              />
              <el-table-column
                align="center"
                prop="attachName"
                :label="$t('perfMod.accessory')"
                width="250"
              >
                <template slot-scope="scope">
                  <SrmCommonFile
                    :extra-data="fileInfo"
                    :default-file="{
                      fileId: scope.row.fileuploadId,
                      fileName: scope.row.attachName
                    }"
                    :readonly="readOnly"
                    @on-change="({file}) => uploadSuccess(file,scope.row)"
                  />
                </template>
              </el-table-column>
            </el-table>
            <span
              slot="footer"
              class="dialog-footer"
            >
              <el-button @click="dialogVisible2 = false">{{ $t('common.cancel') }}</el-button>
              <el-button
                type="primary"
                @click="getScoreItemsAfterVisible"
              >{{
                $t('common.confirm')
              }}</el-button>
            </span>
          </el-dialog>
        </el-main>
        <el-footer class="page-bar">
          <CPagination
            ref="queryPagination"
            class="c-query-table-pagination"
            :total="pageInfo.total"
            :page-num="pageInfo.pageNum"
            :page-size="pageInfo.pageSize"
            @current-change="handleCurrentChange"
            @size-change="handleSizeChange"
          />
        </el-footer>
      </el-container>
    </el-main>
  </el-container>
</template>
<script>
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import CPagination from 'lib@/components/c-pagination'
import ExportExcel from 'lib@/components/export-excel'
import MImport from 'lib@/components/import'
import { downloadFileLink } from 'lib@/utils/file'
import { performanceManagement } from 'modb@/performanceManagement/api/index'
import CustomTable from 'lib@/components/custom-table'
export default {
  name: 'GraderRating',
  components: {
    CPagination,
    MainHeader,
    FormWrapper,
    ExportExcel,
    MImport,
    CustomTable
  },
  data () {
    return {
      userType: '',
      queryParamArray: {
        fileName: this.$t('perfMod.raterPerformScore'),
        queryParam: this.queryParam || {},
        titleList: [
          'projectName',
          'companyName',
          'indicatorDimensionType',
          'indicatorName',
          'categoryName',
          'indicatorLogic',
          'pefScore',
          'ifEndScored',
          'perStartMonth',
          'perEndMonth',
          'organizationName',
          'scoreNickName',
          'comments',
          'scoreManScoringId'
        ]
      },
      expQuery: {
        ...this.queryParam
      },
      setIfScored: {},
      dialogVisible: false,
      dialogVisible2: false,
      gridId: 'list',
      calculateMessage: [],
      loading: false,
      pageSize: 15,
      currentRow: null,
      queryParam: {},
      queryForm: [],
      preFormObj: {},
      iModal1: {
        title: this.$t('components.eio.importTitle'),
        upLoadUrl: '/api-pef/perf/score-man-scoring-v1/importScoreManScoringV1Excel'
      },
      extraData: {
        fileModular: 'pef',
        fileFunction: 'graderRating',
        fileType: 'excel'
      },
      graderForm: {
        tableData: [],
        rules: {
          score: { required: false, trigger: 'change' },
          templateIndsLineId: { required: false, trigger: 'change' }
        }
      },
      pageInfo: {
        total: 0,
        pageNum: 1,
        pageSize: 15
      },
      selections: [],
      fileInfo: {
        fileModular: 'pef',
        fileFunction: 'score-man-scoring-v1',
        fileType: 'file'
      },
      acceptFileType: ['jpg', 'png', 'jpeg']
    }
  },
  created () {
    // VENDOR
    this.userType = this.$store.getters.userInfo.userType
    if (this.userType === 'VENDOR') {
      this.queryParamArray = {
        fileName: this.$t('perfMod.raterPerformScore'),
        queryParam: this.queryParam || {},
        titleList: [
          'projectName',
          'companyName',
          'indicatorDimensionType',
          'indicatorName',
          'categoryName',
          'score',
          'perStartMonth',
          'perEndMonth',
          'comments',
          'scoreManScoringId'
        ]
      }
      this.expQuery = { ...this.queryParam }
    }

    this.queryForm = [
      {
        prop: 'projectName',
        label: () => this.$t('perfMod.projectName')
      },
      {
        prop: 'organizationId',
        label: () => this.$t('perfMod.fullPathId'),
        type: 'OUorganizationSelector',
        placeholder: () => this.$t('perfMod.selectOrganization')
      },
      {
        prop: 'companyName',
        label: () => this.$t('perfMod.vendorName'),
        type: 'quicksearch',
        showKey: 'companyName',
        name: 'scc_sup_company_info_display_buyer'
      },
      // { prop: 'evaluationPeriod',
      //   label: ()=>this.$t('perfMod.evaluationPeriod'),
      //   type: 'select',
      //   options: []
      // },
      {
        prop: 'indicatorDimensionType',
        label: () => this.$t('perfMod.indicatorDimension'),
        type: 'dict', // 字典类型
        code: 'INDICATORS_DIM' // 字典code
      },
      { prop: 'indicatorName', label: () => this.$t('perfMod.indicatorName') },
      {
        prop: 'ifEndScored',
        label: () => this.$t('perfMod.ifScoreCalculated'),
        type: 'dict', // 字典类型
        code: 'YES_OR_NO' // 字典code
      },
      {
        prop: 'ifScored',
        label: () => this.$t('perfMod.ifCalculated'),
        type: 'dict', // 字典类型
        code: 'YES_OR_NO' // 字典code
      },
      {
        prop: 'ifQueryByScoreItemsCreatedBy',
        label: () => this.$t('perfMod.queryInitiator'),
        type: 'dict', // 字典类型
        code: 'YES_OR_NO' // 字典code
      }
      // { prop: 'categoryName',
      //   label: ()=>this.$t('perfMod.categoryName'),
      //   type:'catSelect',
      //   showKey: 'categoryName'
      // }
    ]
    if (this.userType === 'VENDOR') {
      this.queryForm = this.queryForm.filter(
        (v) =>
          ![
            'ifEndScored',
            'ifScored',
            'ifQueryByScoreItemsCreatedBy',
            'companyName',
            'organizationId'
          ].includes(v.prop)
      )
    } else {
      this.preFormObj = {
        ifEndScored: 'N',
        ifScored: 'N',
        ifQueryByScoreItemsCreatedBy: 'N'
      }
    }

    this.getQuerydata(this.preFormObj) //  查询数据
  },
  mounted () {},
  methods: {
    // 配置后更新表格
    async updataConfig (isupdate) {
      if (isupdate) {
        await this.initComp()
        this.doLayout()
        this.$forceUpdate()
      }
    },
    doLayout () {
      const xTable = this.$refs.graderTable
      // if (xTable) {
      //   xTable.refreshColumn().then(() => xTable.refreshScroll())
      //   xTable.updateData()
      //   xTable.recalculate(true)
      //   this.$forceUpdate()
      // }
    },
    // 初始化表格列
    async initComp () {
        let tHeader = await this.queryConfig()
        this.innerHeader = tHeader
    },
    // 查询配置
    async queryConfig () {
      const key = this.getParams()
      const JSON_CONFIG = localStorage.getItem(key) // 本地
      const JSON_CONFIG_SERVICE = await this.fatchConfig() // 获取后台配置信息
      const CONFIG_RES = JSON_CONFIG_SERVICE || JSON_CONFIG
      const config = CONFIG_RES ? JSON.parse(CONFIG_RES) : undefined
      let resConfig = await this.setColumn(config)
      return resConfig
    },
    getParams () {
      const userId = this.$store.getters.user.userId
      const key = `'custom_table_key'_${userId}_${this.pageViewConfigCode}`
      return key
    },
    // 查询接口配置信息
    async fatchConfig () {
      let tableConfig = ''
      if (this.openCustomTable) {
        let query = { pageViewConfigCode: this.pageViewConfigCode }
        const { data = {} } = await this.$api.base.pageConfig.getCurrentConfig(query)
        tableConfig = data.tableConfig || ''
      }
      return tableConfig
    },
    // 设置显示列
    setColumn (configData) {
      const defaultTConfig = this.defaultTableHeaderBak
      let meta = []
      if (configData) {
        let filterShow = configData.filter(i => i.show)
        meta = filterShow.map(({ prop, width, minWidth, fixed, version }) => {
          const target = defaultTConfig.find(i => i.prop === prop)
          let initFixed = fixed || target?.fixed
          if (target?.prop == 'operation') { // 操作列默认固定右侧
            initFixed = 'right'
          }
          return { ...target, width, minWidth, version, fixed: initFixed }
        })
      } else {
        meta = defaultTConfig
      }
      return meta
    },
    d_getLabel (value, scope) {
      return this.$getDictLabelByValue(value, scope)
    },
    // 上传附件成功
    handleUploadSuccess (file, scope) {
      const { id, name } = file
      scope.row.scoreManScoringFileId = id.toString()
      scope.row.scoreManScoringFileName = name

      this.$set(this.graderForm.tableData[this.rowIndex], 'fileTip', true)
      let rowData = {
        scoreManScoringId: scope.row.scoreManScoringId || '',
        scoreManScoringFileId: id,
        scoreManScoringFileName: name
      }
      performanceManagement.ceeaUploadFile(rowData).then((res) => {
        console.log(res)
      })
    },
    handleRemove (fileId) {},
    buttonClick (index) {
      this.rowIndex = index
    },
    handleAttachmentRemove (row) {
      let FileId = {
        scoreManScoringFileId: row.scoreManScoringFileId,
        scoreManScoringId: row.scoreManScoringId
      }
      performanceManagement.ceeaDeleteFile(FileId).then((res) => {
        if (res) {
          row.scoreManScoringFileId = ''
          row.scoreManScoringFileName = ''
        }
      })
    },
    cellClick (row) {
      this.$refs.graderTable.toggleRowSelection(row, true)
    },
    editTab () {},
    syncFilterParams (values) {
      if (values) {
        if (this.queryParam.ifQueryByScoreItemsCreatedBy !== values.ifQueryByScoreItemsCreatedBy) {
          values.ifScored = values.ifQueryByScoreItemsCreatedBy
        }
        this.queryParam = Object.assign({}, values)
        this.queryParamArray.queryParam = Object.assign({}, values)
        this.expQuery = { ...this.queryParam }
      }
    },
    getQuerydata (v) {
      this.queryParam = v || this.queryParam
      this.$nextTick(() => this.fatchListData(this.queryParam, { pageSize: 15 }))
    },
    selectFocus (index) {
      this.currentRow = index
    },
    selectFocusA (index) {
      this.currentRow = index
    },
    downloadTemplate1 () {
      downloadFileLink(
        '/api-pef/perf/score-man-scoring-v1/importScoreManScoringV1Download',
        `${this.$t('perfMod.raterPerformanceScore')}.xlsx`
      ).catch(() => {
        this.$message.error(this.$t('perfMod.downLoadError'))
      })
    },
    handleSuccess ({ data }, file, fileList) {
      // 导入成功就刷新界面
      this.$http({
        url: '/api-pef/perf/score-man-scoring-v1/listExcelImportData',
        method: 'POST',
        data: {}
      }).then((res) => {
        this.fatchListData({
          ifEndScored: 'N',
          ifScored: 'N',
          ifQueryByScoreItemsCreatedBy: 'N',
          pageSize: 15
        })
      })
    },
    // 事件切换选择
    indicatorLineChange (val, row) {
      if (val) {
        let rowData = row.indicatorsLines.find((item) => {
          return item.indicatorLineId === val
        })
        if (rowData) {
          let indicatorType = row.indicatorType
          if (indicatorType === 'PERFORMANCE') {
            // 绩效
            row.pefScore = rowData.pefScore
            row.score = rowData.pefScore
          } else {
            // 考核 ASSESSMENT
            row.score = rowData.assessmentPenalty
            row.pefScore = rowData.assessmentPenalty
          }
          row.templateIndsLineId = rowData.indicatorLineId
          row.indicatorLineDes = rowData.indicatorLineDes
        }
      }
    },
    indicatorLineChangeifExcel (val, data) {
      let rowData = data
      if (val) {
        let row = rowData.indicatorsLines.find((item) => {
          return item.templateIndsLineId === val
        })
        if (row) {
          let indicatorType = rowData.indicatorType
          if (indicatorType === 'PERFORMANCE') {
            // 绩效
            rowData.pefScore = row.pefScore
            rowData.score = row.pefScore
          } else {
            // 考核 ASSESSMENT
            rowData.score = row.assessmentPenalty
            rowData.pefScore = row.assessmentPenalty
          }
          rowData.templateIndsLineId = row.templateIndsLineId
          rowData.indicatorLineDes = row.indicatorLineDes
        }
      }
      return rowData
    },
    pefDirectScoreChange (val, row) {
      if (val) {
        if (this.currentRow !== null && this.currentRow >= 0) {
          row.score = val
        }

        if (Number(val) < 0) {
          row.pefScore = 0
        } else if (Number(val) > 100) {
          row.pefScore = 100
        }
      } else {
        if (this.currentRow !== null && this.currentRow >= 0 && val != 0) {
          row.score = ''
        }
      }
    },
    pefDirectScoreChangeifExcel (val, data) {
      let rowData = data
      if (val) {
        rowData.score = val
        if (Number(val) < 0) {
          rowData.pefScore = 0
        } else if (Number(val) > 100) {
          rowData.pefScore = 100
        }
      } else {
        rowData.score = ''
      }
      return rowData
    },

    pefScoreChange (val, row) {
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
            if (['GREATER', 'GREATER_EQUAL'].includes(row.indicatorsLines[0].startSymbol)){
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
              this.$message.error('输入数值不在区间范围内')
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
    pefScoreChangeifExcel (val, data) {
      let rowData = data
      if (val) {
        let row = rowData.indicatorsLines.find((item) => {
          return val > item.scoreStart - 1 && val < item.scoreEnd + 1
        })
        if (row) {
          rowData.score = row.pefScore
          rowData.templateIndsLineId = row.templateIndsLineId
          rowData.indicatorLineDes = row.indicatorLineDes
        } else {
          // 查询区间最大值 最小值 [[
          let minAndMax = []
          if (rowData.indicatorsLines.length > 0) {
            rowData.indicatorsLines.forEach((elm) => {
              let min = elm.scoreStart
              let max = elm.scoreEnd
              minAndMax.push(min, max)
            })
            let minD = Math.min(...minAndMax)
            let maxD = Math.max(...minAndMax)
          }
          rowData.templateIndsLineId = ''
          rowData.indicatorLineDes = ''
          rowData.pefScore = ''
          rowData.score = ''
        }
        return rowData
      }
    },
    // 查询列表数据
    fatchListData (p1, p2) {
      let queryObj = { ...p1, ...p2 }
      performanceManagement.listScoreManScoringPage(queryObj).then((res) => {
        if (res.data && res.data.list) {
          this.loading = false
          this.pageInfo.total = res.data.total
          this.pageInfo.pageNum = res.data.pageNum
          this.pageInfo.pageSize = res.data.pageSize
          this.graderForm.tableData = res.data.list.map((i) => ({
            ...i,
            edit: false
          }))
          this.graderForm.tableData.forEach((item) => {
            // if(item.ifExcelImport ==='Y' && item.ifFreshAfterImport==='N'){
            if (item.quoteMode === 'TEXT_CONVERSION') {
              item = this.indicatorLineChangeifExcel(item.templateIndsLineId, item)
              //  console.log("getdataA",item)
            } else if (item.quoteMode === 'INTERVAL_CONVERSION') {
              item = this.pefScoreChangeifExcel(item.pefScore, item)
              //  console.log("getdataB",item)
            } else if (item.quoteMode === 'DIRECT_QUOTE') {
              item = this.pefDirectScoreChangeifExcel(item.pefScore, item)
              //  console.log("getdataC",item)
            }
            // }
          })
        }
      })
    },
    handleSelectionChange (val) {
      this.selections = val
    },
    // 行编辑
    handleEditClick (index, row) {
      this.graderForm.tableData[index].edit = true
      // row.edit = true
      this.$nextTick(() => {
        // this.$refs.graderTable.doLayout()
      })
    },
    doAbstention () {
      if (this.selections.length == 0) {
        return this.$message.warning(this.$t('perfMod.selectData'))
      }
      let url = '/api-pef/perf/score-man-scoring-v1/abstention'
      this.$http({
        url: url,
        method: 'POST',
        data: this.selections
      }).then((res) => {
        this.getQuerydata()
      })
    },
    saveDataHandle (isQuery) {
      if (this.selections.length == 0) {
        return this.$message.warning(this.$t('perfMod.selectData'))
      }
      this.$refs['graderTableForm'].validate((valid, graderForm) => {
        if (valid) {
          // let subData = this.graderForm.tableData
          let subData = this.selections
          performanceManagement.confirmBeforeScoreManScoringSubmit(subData).then((res) => {
            if (res) {
              if (res.data.notScoredList.length === 0) {
                this.confirmCalculateVisible()
              } else {
                this.calculateMessage = res.data.notScoredList
                this.dialogVisible = true
              }
            }
          })
        } else {
          this.$message({
            message: this.$t('perfMod.enterRequired'),
            type: 'warning'
          })
          return false
        }
      })
      // if (this.selections.length > 0) {
      // } else {
      //   this.$message({
      //     message: this.$t('perfMod.selectSaveData'),
      //     type: 'warning'
      //   })
      // }
    },
    confirmCalculateVisible () {
      let url = '/api-pef/perf/score-man-scoring-v1/saveScoreManScoring'
      this.$http({
        url: url,
        method: 'POST',
        data: this.selections
      }).then((res) => {
        this.dialogVisible = false
        this.getQuerydata()
      })
    },
    uploadSuccess (file, row) {
      const { fileId = '', fileName = '' } = file || {}
      row.fileuploadId = fileId.toString()
      row.attachName = fileName
    },

    getScoreItemsAfterVisible () {
      let subData = this.selections
      performanceManagement.saveScoreManScoring(subData).then((res) => {
        if (res) {
          this.$message({
            message: res.message,
            type: 'success'
          })
          this.$nextTick(() => this.fatchListData(this.queryParam, { pageSize: 15 }))
        }
      })
    },
    importOne () {},
    exportOne () {},
    handleCurrentChange (num) {
      let page = {
        pageNum: num,
        pageSize: this.pageInfo.pageSize
      }
      this.loading = true
      this.fatchListData(this.queryParam, page)
    },
    handleSizeChange (size) {
      let page = {
        pageSize: size
      }
      this.loading = true
      this.fatchListData(this.queryParam, page)
    }
  }
}
</script>
<style scoped lang="scss">
.tableForm {
  position: absolute;
  top: 0;
  bottom: 0;
  height: 100%;
  width: 100%;
  .el-table {
    height: 100%;
  }
}
.the_material_wrapper {
}
.download-link-wrap {
  .download-link-item {
    color: #1890ff;
  }
  .close-icon {
    font-weight: bold;
    cursor: pointer;
  }
}
.close-icon {
  font-weight: bold;
  cursor: pointer;
}
.toRequired {
  color: #ff4949;
  padding-right: 2px;
}
</style>
<style>
.the-graderRating-detail .pefScoreInput {
  width: 100%;
}
.the-graderRating-detail .pefScoreInput .el-input-number__increase,
.the-graderRating-detail .pefScoreInput .el-input-number__decrease {
  display: none;
}
.the-graderRating-detail .pefScoreInput .el-input .el-input__inner {
  padding: 0 10px;
}
.the-graderRating-detail .custom-table {
  position: absolute;
  right: 20px;
  top: 72px;
  z-index: 999
}
</style>
