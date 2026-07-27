<template>
  <el-container
    direction="vertical"
    class="flex-container blacklist_list_wrapper"
  >
    <FormWrapper
      :form-array="filterConfig"
      @getFormData="search"
      @synchronous-value="syncFilterParams"
    >
      <template #vendorLevel="{ scope }">
        <DictSelect
          v-model="scope.vendorLevel"
          code="VENDOR_LEVEL"
        />
      </template>
      <template #status="{ scope }">
        <DictSelect
          v-model="scope.status"
          code="VALID_STATUS"
        />
      </template>
      <template #categoryName="{ scope }">
        <CCategorySelect
          v-model="scope.categoryName"
          :scope="scope"
          :placeholder="$t('dataConfMod.msgCategoryNormalizer')"
          show-key="categoryName"
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
      <template #orgName="{ scope }">
        <OrganizationSelector
          ref="ouSelector"
          v-model="scope.organizationName"
          :parent-id="-1"
          node-type="OU"
          :placeholder="$t('common.pleaseSelect')"
          @select="(val) => selectHandlerOrg(val, scope)"
        />
      </template>
    </FormWrapper>
    <el-main>
      <EasyTable
        ref="table"
        :methods="methods"
        :columns="columns"
        :selection="true"
        row-key="levelLineScoreId"
        :editable="false"
        table-name="hierarchicalRating_table"
        :query-params.sync="queryParams"
        :comActive="$attrs['changeTab']"
        @selection-change="handleSelectionChange"
      >
        <template #btns>
          <AuthorityButton
            type="primary"
            @click="submit"
          >
            {{
              $t('common.submit')
            }}
          </AuthorityButton>
          <AuthorityButton
            @click="bathDeleteList"
          >
            {{
              $t('common.delete')
            }}
          </AuthorityButton>
          <AuthorityButton
            @click="add"
          >
            {{ $t('common.add') }}
          </AuthorityButton>
          <MImport
            :title="$t('common.import')"
            up-load-url="/api-pef/perf/levellinescore/importExcel"
            :extra-data="extraData"
            type="default"
            @downloadTemplate="downloadTemplate"
            @handleSuccess="handleSuccess"
          />
          <ExportExcel
            pageUrl="/api-pef/perf/levellinescore/listPageByParam"
            :filterParams="filterParams"
            :tableHeader="computedColumns"
            :dictCodes="dictCodes"
            export-mode="front"
            type="default"
          />
          <AuthorityButton
            @click="inactive"
          >
            {{
              $t('common.inactive')
            }}
          </AuthorityButton>
        </template>
        <template #orgName="{ scope }">
          <OrganizationSelector
            v-if="scope.row.dataSource !== 'SYSTEM' && !['APPROVAL','HANDING'].includes(scope.row.approvalStatus)"
            ref="organizationSelector"
            v-model="scope.row.orgId"
            :parent-id="-1"
            node-type="OU"
            :limit="false"
            @select="(val) => selectHandler(val, scope)"
          />
          <span v-else class="font-12">{{ scope.row.orgName }}</span>
        </template>
        <template #reviewYear="{ scope }">
          <el-date-picker
            v-if="scope.row.dataSource !== 'SYSTEM' && !['APPROVAL','HANDING'].includes(scope.row.approvalStatus)"
            v-model="scope.row.reviewYear"
            type="year"
            value-format="yyyy"
            :placeholder="$t('vendorMod.datePicker')"
          />
          <span v-else class="font-12">{{ scope.row.reviewYear }}</span>
        </template>
        <!-- 品类 -->
        <template #categoryId="{ scope }">
          <CCategorySelect
            v-if="scope.row.dataSource !== 'SYSTEM' && !['APPROVAL','HANDING'].includes(scope.row.approvalStatus)"
            v-model="scope.row.categoryFullName"
            :scope="scope.row"
            :placeholder="$t('dataConfMod.msgCategoryNormalizer')"
            show-key="categoryName"
            @select="(val) => selectHandlerCategory(val, scope)"
          />
          <span v-else class="font-12">{{ scope.row.categoryFullName }}</span>
        </template>
        <template #perFormance="{ scope }">
          <el-button type="text" @click="perFormanceDeatil(scope)">
            {{ $t('dataConfMod.detail') }}
          </el-button>
        </template>

        <template #companyName="{ scope }">
          <!-- <el-input v-model="scope.row.companyName" /> -->
          <QuickSearch
            v-if="scope.row.dataSource !== 'SYSTEM' && !['APPROVAL','HANDING'].includes(scope.row.approvalStatus)"
            :show-input="scope.row.companyName"
            show-key="companyName"
            :scope-data="scope.row"
            name="scc_sup_company_info_display"
            @close-quicksearch="(val) => getUserObjnotice(val, scope)"
          />
          <span v-else class="font-12">{{ scope.row.companyName }}</span>
        </template>
        <template #vendorLevel="{ scope }">
          <DictSelect
            v-if="!((scope.row.levelUserName !== username && !scope.row.__add_key__) || scope.row.approvalStatus == 'APPROVAL' || scope.row.approvalStatus == 'HANDING')"
            v-model="scope.row.vendorLevel"
            code="VENDOR_LEVEL"
          />
          <span v-else class="font-12">{{ $getDictLabel('VENDOR_LEVEL',scope.row.vendorLevel) }}</span>
        </template>
      </EasyTable>
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
            :label="$t('common.category')"
          />
          <el-table-column
            prop="scoreAttribute1"
            :label="$t('supplierRating.averageScore2')"
            width="140"
          />
          <el-table-column
            prop="scoreAttribute2"
            :label="$t('supplierRating.averageCostScore2')"
            width="140"
          />
          <el-table-column
            prop="scoreAttribute3"
            :label="$t('supplierRating.averageDeliveryScore2')"
            width="140"
          />
          <el-table-column
            prop="scoreAttribute4"
            :label="$t('supplierRating.averageServiceScore2')"
            width="140"
          />
          <el-table-column
            prop="scoreAttribute5"
            :label="$t('supplierRating.averageTechnicalScore2')"
            width="140"
          />
          <el-table-column
            prop="score"
            :label="$t('supplierRating.meanCompositeScore2')"
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
              <!--              {{ $getDictLabelByValue(VENDOR_LEVEL, ) }}-->
            </template>
          </el-table-column>
          <el-table-column
            prop="rank"
            :label="$t('perfMod.rankAll')"
          />
        </el-table>
      </srm-dialog>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import OrganizationSelector from 'lib@/components/organization-selector'
import EasyTable from 'lib@/components/BaseTable/EasyTable'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MImport from 'lib@/components/import'
import QuickSearch from 'lib@/components/QuickSearch'
import { downloadFileLink, downloadFileLinkByPost } from 'lib@/utils/file'
import ExportExcel from 'lib@/components/export-excel'
import { dateFormat } from 'lib@/utils/date-format'
import CCategorySelect from 'lib@/components/c-category-select'
import { hierarchicalRatingApi } from 'modb@/vendorHierarchicalManagement/api'

export default {
  name: 'HierarchicalRatingList',
  components: {
    EasyTable,
    QuickSearch,
    FormWrapper,
    MImport,
    ExportExcel,
    OrganizationSelector,
    CCategorySelect
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      filterConfig: [
        {
          prop: 'orgId',
          label: () => this.$t('supplierRating.entity'),
          type: 'slot',
          slot: 'orgName'
        },
        {
          prop: 'reviewYear',
          label: () => this.$t('supplierRating.assessmentYear'),
          type: 'slot',
          slot: 'reviewYear'
        },
        { prop: 'version', label: () => this.$t('dataConfMod.version') },
        {
          prop: 'categoryName',
          label: () => this.$t('supplierRating.heading'),
          type: 'slot',
          slot: 'categoryName'
        },
        {
          prop: 'companyCode',
          label: () => this.$t('supplierRating.supplierCode'),
          type: 'quicksearch',
          showKey: 'companyCode',
          propKey: 'companyCode',
          name: 'scc_sup_company_info_display_buyer'
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
          prop: 'vendorLevel',
          label: () => this.$t('supplierRating.classificationSuppliers'),
          type: 'slot',
          slot: 'vendorLevel'
        },
        {
          prop: 'status',
          label: () => this.$t('supplierRating.status'),
          type: 'slot',
          slot: 'status'
        },

        { prop: 'auditCode', label: () => this.$t('supplierRating.approvalNumber') },
        { prop: 'createdBy', label: () => this.$t('supplierRating.creator') }
      ],
      extraData: {
        fileModular: 'sup',
        fileFunction: 'companyInfoImportExcel',
        fileType: 'excel'
      },
      detailViewVisible: false,
      multipleSelection: [],
      graderRatingData: [],
      dictCodes: {
        systemSourceResult: 'VENDOR_LEVEL',
        dataSource: 'LEVEL_DATA_SOURCE',
        approvalStatus: 'LEVEL_APPROVAL_STATUS',
        status: 'VALID_STATUS',
        currentStatus: 'IS_VALID'
      },
      filterParams: {},
      tableHeader: [
        {
          prop: 'blackFlat',
          label: () => this.$t('route.blacklist'),
          width: 100,
          formattor: (val) => this.$getDictLabel('BLACK_FLAT', val)
        },
        {
          prop: 'reason',
          label: () => this.$t('supplierRating.blacklistCause'),
          width: 100
        },
        {
          prop: 'description',
          label: () => this.$t('supplierRating.blacklistRemarks'),
          width: 100
        },
        {
          prop: 'companyCode',
          label: () => this.$t('supplierRating.vendorCode'),
          width: 100
        },
        {
          prop: 'companyName',
          label: () => this.$t('supplierRating.supplierName'),
          width: 100
        },
        {
          prop: 'type',
          label: () => this.$t('supplierRating.supplierType'),
          width: 100,
          formattor: (val) => this.$getDictLabel('TYPE', val)
        },
        {
          prop: 'limitStartDate',
          label: () => this.$t('supplierRating.blacklistStartDate'),
          width: 100
        },
        {
          prop: 'limitEndDate',
          label: () => this.$t('supplierRating.blacklistDeadline'),
          width: 100
        },
        {
          prop: 'limitQualification',
          label: () => this.$t('supplierRating.restricted'),
          width: 100
        },
        {
          prop: 'creationDate',
          label: () => this.$t('vendorMod.creationDate1'),
          width: 100
        },
        {
          prop: 'createdBy',
          label: () => this.$t('supplierRating.creator'),
          width: 100
        },
        {
          prop: 'lastUpdatedBy',
          label: () => this.$t('contractMod.lastUpdatedBy'),
          width: 100
        }
      ],
      queryParams: {},
      methods: {
        listPage: async (params) => {
          const res = await hierarchicalRatingApi.addOrUpdateVendorLevel(params)
          return res
        }
      },
      columns: [
        {
          attrs: {
            label: () => this.$t('supplierRating.entity'),
            prop: 'orgName',
            width: '140px'
          },
          slot: 'orgName',
          rules: { required: 1, message: this.$t('vendorMod.required') }
        },
        {
          attrs: {
            label: () => this.$t('supplierRating.assessmentYear'),
            width: '140px',
            prop: 'reviewYear'
          },
          slot: 'reviewYear',
          rules: { required: 1, message: this.$t('vendorMod.required') }
        },
        {
          attrs: {
            label: () => this.$t('dataConfMod.version'),
            width: '100px',
            prop: 'version'
          }
        },
        {
          attrs: {
            label: () => this.$t('supplierRating.heading'),
            width: '240px',
            prop: 'categoryName'
          },
          slot: 'categoryId',
          rules: { required: 1, message: this.$t('vendorMod.required') }
        },
        {
          attrs: {
            label: () => this.$t('supplierRating.vendorCode'),
            prop: 'companyCode',
            width: '90px'
          }
        },
        {
          attrs: {
            label: () => this.$t('supplierRating.supplierName'),
            prop: 'companyName',
            width: '160px'
          },
          slot: 'companyName',
          rules: { required: 1, message: this.$t('vendorMod.required') }
        },
        {
          attrs: {
            label: () => this.$t('supplierRating.classificationSuppliers'),
            width: '100px',
            prop: 'vendorLevel'
          },
          slot: 'vendorLevel',
          rules: { required: 1, message: this.$t('vendorMod.required') }
        },
        {
          attrs: {
            label: () => this.$t('supplierRating.sourceResults'),
            width: '100px',
            prop: 'systemSourceResult',
            formatter: (value) => this.$getDictLabel('VENDOR_LEVEL', value)
          }
        },
        {
          attrs: {
            label: () => this.$t('vendorMod.dataSources'),
            width: '100px',
            prop: 'dataSource',
            formatter: (value) => this.$getDictLabel('LEVEL_DATA_SOURCE', value)
          }
          // slot: 'dataSource',
        },
        {
          attrs: {
            label: () => this.$t('supplierRating.detailsReview'),
            width: '100px',
            prop: 'perFormance'
          },
          slot: 'perFormance'
        },
        {
          attrs: {
            label: () => this.$t('vendorMod.approveStatus'),
            width: '180px',
            prop: 'approvalStatus',
            formatter: (value) => this.$getDictLabel('LEVEL_APPROVAL_STATUS', value)
          }
          // slot: 'approvalStatus',
        },
        {
          attrs: {
            label: () => this.$t('supplierRating.significantCondition'),
            width: '180px',
            prop: 'status',
            formatter: (value) => this.$getDictLabel('VALID_STATUS', value)
          }
          // slot: 'status',
        },
        {
          attrs: {
            label: () => this.$t('supplierRating.currentDemand'),
            width: '100px',
            prop: 'currentStatus',
            formatter: (value) => this.$getDictLabel('IS_VALID', value)
          }
          // slot: 'currentStatus',
        },
        {
          attrs: {
            label: () => this.$t('supplierRating.gradedCoding'),
            width: '180px',
            prop: 'orderNo'
          }
        },
        {
          attrs: {
            label: () => this.$t('supplierRating.gradedName'),
            width: '100px',
            prop: 'projectName'
          }
        },
        {
          attrs: {
            label: () => this.$t('supplierRating.creator'),
            width: '100px',
            prop: 'createdBy'
          }
        },
        {
          attrs: {
            label: () => this.$t('supplierRating.creationDate'),
            width: '160px',
            prop: 'creationDate',
            sortable: true
          }
        },
        {
          attrs: {
            label: () => this.$t('supplierRating.approvalNumber'),
            width: '100px',
            prop: 'auditCode'
          }
        },
        {
          attrs: {
            label: () => this.$t('supplierRating.approvalTime'),
            width: '100px',
            prop: 'auditDate'
          }
        },
        {
          attrs: {
            label: () => this.$t('supplierRating.dismissReason'),
            width: '100px',
            prop: 'rejectReason'
          }
        },
        {
          attrs: {
            prop: 'operation',
            label: () => this.$t('components.headers.operation'),
            width: 140,
            fixed: 'right'
          },
          operations: [
            {
              event: 'deleteItem',
              name: this.$t('common.delete'),
              show: (scope) => {
                return scope.row.approvalStatus != 'APPROVAL' && scope.row.approvalStatus != 'HANDING'
              },
              func: this.deleteItem
            },
            {
              event: 'addItem',
              name: this.$t('common.save'),
              show: (scope) => {
                return scope.row.approvalStatus != 'APPROVAL' && scope.row.approvalStatus != 'HANDING'
              },
              func: this.save
            }
          ]
        }
      ]
    }
  },
  computed: {
    computedColumns () {
      let arr = []
      for (let item of this.columns) {
        let { prop, label } = item.attrs
        if (prop !== 'operation') {
          arr.push({
            prop,
            label
          })
        }
      }
      return arr
    }
  },
  created () {
    this.username = this.$store.getters.userInfo ? this.$store.getters.userInfo.username : ''
  },
  methods: {
    perFormanceDeatil (scope) {
      let data = {
        reviewYear: scope.row.reviewYear || '',
        orgId: scope.row.orgId || '',
        categoryId: scope.row.categoryId || '',
        companyId: scope.row.companyId || '',
        levelHeadId: scope.row.levelHeadId || ''
      }
      hierarchicalRatingApi.listPageOverallScoreByParam(data).then((res) => {
        let allList = res.data?.list || []
        const lengthAll = allList.length
        const levelName = scope.row?.vendorLevel
        let A1 = 0
        let A2 = 0
        let A3 = 0
        let A4 = 0
        let A5 = 0
        let A6 = 0
        allList.forEach(datas => {
          A1 += Number(datas?.scoreAttribute1)
          A2 += Number(datas?.scoreAttribute2)
          A3 += Number(datas?.scoreAttribute3)
          A4 += Number(datas?.scoreAttribute4)
          A5 += Number(datas?.scoreAttribute5)
          A6 += Number(datas?.score)
          delete datas.levelName
        })
        A1 = (A1 / lengthAll).toFixed(2)
        A2 = (A2 / lengthAll).toFixed(2)
        A3 = (A3 / lengthAll).toFixed(2)
        A4 = (A4 / lengthAll).toFixed(2)
        A5 = (A5 / lengthAll).toFixed(2)
        A6 = (A6 / lengthAll).toFixed(2)
        const obj = {
          scoreAttribute1: this.$t('relegationEntity.key24', {A1: A1}),
          scoreAttribute2: this.$t('relegationEntity.key25', {A2: A2}),
          scoreAttribute3: this.$t('relegationEntity.key26', {A3: A3}),
          scoreAttribute4: this.$t('relegationEntity.key27', {A4: A4}),
          scoreAttribute5: this.$t('relegationEntity.key28', {A5: A5}),
          score: this.$t('relegationEntity.key29', {A6: A6}),
          levelName
        }
        if (lengthAll) {
          allList.push(obj)
        }
        this.graderRatingData = allList
      })
      this.detailViewVisible = true
    },
    handleSelectionChange (val) {
      this.multipleSelection = val
    },
    syncFilterParams (values) {
      this.filterParams = values
    },
    handleSuccess () {
      this.search()
    },
    getUserObjnotice (val, scope) {
      this.$set(this.$refs.table.realDataSource[scope.$index], 'companyId', val.companyId)
      this.$set(this.$refs.table.realDataSource[scope.$index], 'companyName', val.companyName)
      this.$set(this.$refs.table.realDataSource[scope.$index], 'companyCode', val.companyCode)
    },
    downloadTemplate () {
      downloadFileLink(
        '/api-pef/perf/levellinescore/exportExcelTemplate',
        this.$t('supplierRating.importTemplate') + '.xlsx'
      ).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail'))
      })
    },
    selectHandler (val, scope) {
      if (val) {
        this.$set(this.$refs.table.realDataSource[scope.$index], 'orgId', val.organizationId)
        this.$set(this.$refs.table.realDataSource[scope.$index], 'orgName', val.organizationName)
        this.$set(this.$refs.table.realDataSource[scope.$index], 'orgCode', val.organizationCode)
      }
    },
    selectHandlerOrg (val, scope) {
      scope.orgName = val.organizationName || ''
    },
    selectHandlerCategory (row, scope) {
      scope.row.categoryId = row.categoryId || ''
      scope.row.categoryName = row.categoryName || ''
      scope.row.categoryCode = row.categoryCode || ''
      scope.row.categoryFullName = row.categoryFullName || ''
    },
    // 上传附件成功
    handleUploadSuccess (file, row, key) {
      const { id, name } = file
      row[key] = id.toString()
    },
    // 删除文件
    handleAttachmentRemove (row, key) {
      row[key] = ''
    },
    deleteItem (scope, data) {
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          if (scope.row.levelLineScoreId) {
            // 有主键ID
            hierarchicalRatingApi.bathDelete([scope.row.levelLineScoreId]).then((res) => {
              this.$message.success(res.message)
              this.$refs.table.search(this.queryParams, true)
            })
          } else {
            // 无主键ID
            data.splice(scope.$index, 1)
          }
        })
        .catch(() => { })
    },
    bathDeleteList () {
      if (this.multipleSelection && this.multipleSelection.length > 0) {
        let levelLineScoreIdList = this.multipleSelection.map((i) => i.levelLineScoreId)
        hierarchicalRatingApi.bathDelete(levelLineScoreIdList).then((res) => {
          this.$message.success(res.message)
          this.$refs.table.search(this.queryParams, true)
        })
      } else {
        this.$message({
          message: this.$t('supplierRating.selectedRowData'),
          type: 'waring'
        })
      }
    },
    submit () {
      if (this.multipleSelection && this.multipleSelection.length > 0) {
        let vendorLevelList = this.multipleSelection.filter((i) => !i.vendorLevel)
        if (vendorLevelList && vendorLevelList.length > 0) {
          this.$message({
            type: 'warning',
            message: this.$t('supplierRating.supplierClassification ')
          })
          return
        }

        hierarchicalRatingApi.submitScoreList(this.multipleSelection).then((res) => {
          this.$message.success(res.message)
          this.$refs.table.$refs.table.$refs.table.clearSelection()
          this.$refs.table.search(this.queryParams, true)
          this.$confirm(this.$t('supplierRating.hierarchicalReview'), this.$t('common.tips'), {
            confirmButtonText: this.$t('common.confirm'),
            cancelButtonText: this.$t('common.cancel'),
            type: 'warning'
          })
            .then(() => {
              this.$router.push({
                name: 'hierarchicalReview',
                params: {
                  form: 'hierarchicalRating',
                  vendorLevelApproveId: res.data
                }
              })
            })
            .catch(() => {
              this.$message({
                type: 'info',
                message: this.$t('supplierRating.canceled')
              })
            })
        })
      } else {
        this.$message({
          message: this.$t('supplierRating.selectedRowData'),
          type: 'warning'
        })
      }
    },
    search (params) {
      const { pageSize, pageNum } = this.queryParams
      this.$refs.table.search({ pageSize, pageNum, ...params }, true)
    },
    add () {
      this.$refs.table.add({
        creationDate: dateFormat(new Date()),
        createdBy: this.$store.getters.userInfo ? this.$store.getters.userInfo.username : '',
        lastUpdatedBy: dateFormat(new Date())
      })
    },
    save (scope, data) {
      hierarchicalRatingApi.saveOrUpdateScoreList(scope.row).then((res) => {
        this.$message.success(res.message)
        this.$refs.table.search(this.queryParams, true)
      })
    },
    inactive () {
      if (this.multipleSelection && this.multipleSelection.length > 0) {
        hierarchicalRatingApi.inValid(this.multipleSelection).then((res) => {
          this.$message.success(res.message)
        })
      } else {
        this.$message({
          type: 'warning',
          message: this.$t('supplierRating.checkRowData')
        })
      }
    },
    saveList () {
      const list = this.$refs.table.getUpdatedRows()
      this.$refs.table.validate((f) => {
        if (f) {
          this.$api.base.blacklist.batchSaveOrUpdate(
              list.map(({ blacklistId, ...rest }) => {
                if (!blacklistId) {
                  return rest
                } else {
                  return { blacklistId, ...rest }
                }
              })
            )
            .then((res) => {
              this.$message.success(res.message)
              this.$refs.table.search(this.queryParams, true)
            })
        } else {
          this.$message({
            message: this.$t('vendorMod.pleasefinishRequired'),
            type: 'error'
          })
        }
      })
    }
  }
}
</script>
<style scoped lang="scss">
  .font-12 {
    font-size: 12px;
  }
</style>
