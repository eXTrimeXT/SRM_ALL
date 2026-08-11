<template>
  <el-container
    class="flex-container quotadetail_list_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="queryForm"
        @getFormData="getQuerydata"
        @synchronous-value="syncFilterParams"
      />
      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <ExportExcel
            page-url="/api-sup/sup/quotaoffset/listDetailPage"
            :filter-params="queryParam"
            :table-header="tableHeader"
            :dict-codes="dictCodes"
            :title="$t('common.export')"
            export-mode="front"
          />
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-header="tableHeader"
        :table-data="tableData"
        :row-index-fixed="false"
        :page-size="pageSize"
        :check-change="handleCurrentChange"
        :checkbox="true"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :transform-data="transformData"
        :source="quotaOffsetApi.detailList"
      />
    </el-main>
    <!-- 新增弹框 -->
    <srm-dialog
      :title="dialogTitle"
      size="large"
      :visible.sync="visible"
    >
      <div class="quotadetailEdit">
        <div style="padding-bottom: 10px">
          <el-button
            class="detail-pbtn"
            type="primary"
            @click="OsMaterialRequisitionAttachAddLine"
          >
            {{ $t('bidMod.affairsIncreased') }}
          </el-button>
        </div>
        <el-table
          :data="requirementAttaches"
          style="width: 100%"
          border
          max-height="250px"
        >
          <el-table-column
            align="center"
            type="index"
            :label="$t('purSettlementMod.tabindex')"
            width="50"
          />
          <!-- 组织 -->
          <el-table-column
            align="center"
            prop="orgId"
            :label="$t('components.orgSelection.organization')"
          >
            <template slot-scope="scope">
              <!-- :showInput="scope.row.organizationName" -->
              <QuickSearch
                show-key="organizationName"
                :scope-data="scope.row"
                name="scc_base_organization"
                @close-quicksearch="getVendorObj"
              />
            </template>
          </el-table-column>
          <!-- 品类 -->
          <el-table-column
            align="center"
            prop="categoryId"
            :label="$t('common.category')"
          >
            <template slot-scope="scope">
              {{ scope.row.categoryName }}
              <!-- <QuickSearch
                :showInput="scope.row.categoryName"
                show-key="categoryName"
                :scope-data="scope.row"
                name="scc_base_purchase_category2"
                @close-quicksearch="getVendorObj"
              /> -->
            </template>
          </el-table-column>
          <!-- 物料编码 -->
          <el-table-column
            align="center"
            prop="itemId"
            :label="$t('common.materialCode')"
          >
            <template slot-scope="scope">
              <QuickSearch
                :show-input="scope.row.itemId"
                show-key="itemId"
                :scope-data="scope.row"
                name="scc_base_material_item"
                @close-quicksearch="getVendorObj"
              />
            </template>
          </el-table-column>
          <!-- 物料名称 -->
          <el-table-column
            align="center"
            prop="itemName"
            :label="$t('quota.itemName')"
            :show-overflow-tooltip="true"
          />
          <!-- 预计月用量 -->
          <el-table-column
            align="center"
            prop="quantityPerMonth"
            :label="$t('common.estimatedMonthlyConsumption')"
          >
            <template slot-scope="scope">
              <el-input v-model="scope.row.quantityPerMonth" />
            </template>
          </el-table-column>
        </el-table>
      </div>
      <template
        #footer
        class="dialog-footer"
      >
        <el-button
          type="primary"
          @click="confirm"
        >
          {{ $t('common.confirm') }}
        </el-button>
        <el-button @click="cancel">
          {{ $t('common.cancel') }}
        </el-button>
      </template>
    </srm-dialog>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import QuickSearch from 'lib@/components/QuickSearch'
import MImport from 'lib@/components/import'
import { downloadFileLink } from 'lib@/utils/file'
import ExportExcel from 'lib@/components/export-excel'
import { quotaDetailApi, quotaOffsetApi } from 'modb@/quotaManagement/api/quotaApi'

export default {
  name: 'QuotadetailList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    QuickSearch,
    MImport,
    ExportExcel
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      quotaOffsetApi: quotaOffsetApi,
      quotaOffsetId: '',
      row: '',
      tableData: [],
      requirementAttaches: [
        {
          orgId: '', // 组织ID
          orgCode: '', // 组织Code
          orgName: '', // 组织名称
          itemId: '', // 物料ID
          itemCode: '', // 物料Code
          itemName: '', // 物料名称
          categoryId: '', // 品类ID
          categoryCode: '', // 品类code
          categoryName: '', // 品类名称
          quantityPerMonth: ''
        }
      ],
      name: 'quotadetailList',
      tableName: 'quotadetailTable',
      pageSize: 15,
      gridId: 'list',
      currentRows: [],
      visible: false,
      mode: 'add',
      dialogTitle: '详情',
      form: {
        quotaDetailId: this.$t('quota.quotaDetailId'),  // '配额ID'
        quotaFlowId: this.$t('cusEntry.supplement20250211.configFlowId'),  // '配置流程ID'
        quotaGroupId: this.$t('cusEntry.supplement20250211.configGroupId'),  // '配置分组ID'
        orgId: this.$t('cusEntry.supplement20250211.orgId2'),  // '组织ID'
        orgCode: this.$t('cusEntry.supplement20250211.organizationCode'),  // '组织Code'
        orgName: this.$t('components.organization.organizationName'),  // '组织名称'
        itemId: this.$t('mould.itemId'),  // '物料ID'
        itemCode: this.$t('cusEntry.supplement20250211.materialCode'),  // '物料Code'
        itemName: this.$t('common.materialName'),  // '物料名称'
        categoryId: this.$t('cusEntry.supplement20250211.categoryId'),  // '品类ID'
        categoryCode: this.$t('cusEntry.supplement20250211.categoryCode'),  // '品类code'
        categoryName: this.$t('components.category.categoryName'),  // '品类名称'
        unit: this.$t('dataConfMod.settingGuide.step3.3'),  // '单位'
        unitName: this.$t('dataConfMod.unitName'),  // '单位名称'
        startDate: this.$t('vendorMod.startDate'),  // '生效日期'
        endDate: this.$t('vendorMod.endDate'),  // '失效日期'
        companyId: this.$t('cusEntry.supplement20250205.supplierId'),  // '供应商ID'
        companyCode: this.$t('common.vendorCode'),  // '供应商编码'
        companyName: this.$t('common.companyName'),  // '供应商名称'
        presetPercent: this.$t('bidMod.presetProportion'),  // '预设比例'
        quotaCeilLimitPercent: this.$t('quota.quotaCeilLimitPercent'),  // '上限比例'
        treatyPercent: this.$t('bidMod.agreementRatio'),  // '协议比例'
        mouldPercent: this.$t('quota.mouldPercent'),  // '模具比例'
        suggestQuotaPercent: this.$t('quota.suggestQuotaPercent'),  // '建议配额'
        quotaPercent: this.$t('quota.quotaPercent'),  // '制定配额'
        actualQuotaPercent: this.$t('quota.actualQuotaPercent'),  // '执行比例'
        quotaOffset: this.$t('cusEntry.supplement20250211.deviationRatio'),  // '偏差比例'
        quantityPerMonth: this.$t('common.estimatedMonthlyConsumption'),  // '预计月用量'
        flowCode: this.$t('vendorMod.inviteVendorNo'),  // '单据编码'
        approveStatus: this.$t('common.approvalStatus'),  // '审批状态'
        remark: this.$t('components.eio.headers.remark'),  // '备注'
        createdId: this.$t('monitorBizConfig.createdId'),  // '创建人ID'
        createdBy: this.$t('common.creator'),  // '创建人'
        creationDate: this.$t('common.creationTime'),  // '创建时间'
        createdByIp: this.$t('monitorBizConfig.createdByIp'),  // '创建人IP'
        lastUpdatedId: this.$t('monitorBizConfig.lastUpdatedId'),  // '最后更新人ID'
        lastUpdatedBy: this.$t('common.lastUpdatePeople'),  // '最后更新人'
        lastUpdateDate: this.$t('dataConfMod.lastUpdateDate'),  // '最后更新时间'
        lastUpdatedByIp: this.$t('monitorBizConfig.lastUpdatedByIp'),  // '最后更新人IP'
        tenantId: this.$t('cusEntry.supplement20250211.tenant'),  // '租户'
        version: this.$t('dataConfMod.version') // '版本号'
      },
      rules: {},
      extraData: {
        fileModular: 'base',
        fileFunction: 'quotadetail',
        fileType: 'excel'
      },
      dictCodes: {},
      filterParams: {},
      tableHeader: [
        {
          prop: 'orgName',
          label: this.$t('components.organization.INV'),  // '库存组织'
          width: 100
        },
        {
          prop: 'itemCode',
          label: this.$t('common.materialCode'),  // '物料编码'
          width: 100
        },
        {
          prop: 'itemName',
          label: this.$t('common.materialName'),  // '物料名称'
          width: 100
        },
        {
          prop: 'categoryName',
          label: this.$t('common.category'),  // '品类'
          width: 100
        },
        {
          prop: 'unitName',
          label: this.$t('dataConfMod.settingGuide.step3.3'),  // '单位'
          width: 100
        },
        {
          prop: 'queryStartDate',
          label: this.$t('quota.queryStartDate'),  // '查询开始时间'
          showType: 'date',
          formatter: (val) => (this.$parseTime(val) || null),
          editable: (row) => row.editable,
          width: 120
        },
        {
          prop: 'queryEndDate',
          label: this.$t('quota.queryEndDate'),  // '查询结束时间'
          showType: 'date',
          formatter: (val) => (this.$parseTime(val) || null),
          editable: (row) => row.editable,
          width: 120
        },
        {
          prop: 'companyName',
          label: this.$t('common.companyName'),  // '供应商名称'
          width: 120
        },
        {
          prop: 'quotaPercent',
          label: this.$t('cusEntry.supplement20250211.quotaPercentage'),  // '制定配额(%)'
          showType: 'input',
          editable: (row) => row.editable,
          width: 120
        },
        {
          prop: 'actualQuotaPercent',
          label: this.$t('cusEntry.supplement20250211.executionRatioPercentage'),  // '执行比例(%)'
          showType: 'input',
          editable: (row) => row.editable,
          width: 120
        },
        {
          prop: 'quotaOffset',
          label: this.$t('cusEntry.supplement20250211.proportionDeviationPercentage'),  // '比例偏差(%)'
          showType: 'input',
          editable: (row) => row.editable,
          width: 120
        },
        {
          prop: 'createdBy',
          label: this.$t('common.creator'),  // '创建人'
          width: 100
        },
        {
          prop: 'creationDate',
          label: this.$t('common.creationTime'),  // '创建时间'
          width: 100,
          dataType: 'dateTime'
        }
      ],
      queryForm: [],
      queryParam: {}
    }
  },
  created () {
    const { flag, row, readOnly } = this.$attrs.params
    this.row = row
    this.queryForm = [
      {
        label: () => this.$t('dataConfMod.organizationId'),
        type: 'INVorganizationSelector',
        prop: 'orgId'
      },
      {
        label: () => this.$t('common.materialCode'),
        type: 'quicksearch',
        showKey: 'materialCode',
        propKey: 'materialCode',
        prop: 'itemCode',
        name: 'scc_base_material_item'
      },
      {
        label: () => this.$t('common.category'),
        type: 'catSelect',
        showKey: 'categoryId',
        prop: 'categoryId'
      },
      {
        label: () => this.$t('common.vendorName'),
        type: 'quicksearch',
        showKey: 'companyName',
        propKey: 'companyId',
        prop: 'companyId',
        name: 'scc_sup_company_info'
      },
      {
        label: () => this.$t('common.creator'),
        prop: 'createdBy'
      },
      {
        label: () => this.$t('common.creationTime'),
        type: 'daterange',
        prop: 'queryCreationDate'
      },
      {
        label: this.$t('quota.queryTime'),  // '查询时间'
        type: 'daterange',
        prop: 'queryDate'
      }
      ]
  },
  mounted () {
    const { flag, row, readOnly } = this.$attrs.params
    this.row = row
    this.queryParam.quotaOffsetId = this.row.quotaOffsetId
    this.$nextTick(() => {
      this.$refs[this.gridId].query()
    })
  },
  methods: {
    // 点击生成配额清单
    quotaList () {
      let attr = []
      const currentRows = this.currentRows
      currentRows.forEach((element) => {
        attr.push(element.quotaGroupId)
      })
      quotaDetailApi.createQuotaFlow(attr).then((res) => {
        if (res.code == '0') {
          this.$message.success(res.message)
        } else {
          this.$message.error(res.message)
        }
      })
    },
    // 点击配额计算
    quotaCalculation () {
      let attr = []
      const currentRows = this.currentRows
      currentRows.forEach((element) => {
        attr.push(element.quotaGroupId)
      })
      quotaDetailApi.calcQuota(attr).then((res) => {
        if (res.code == '0') {
          this.$message.success(res.message)
        } else {
          this.$message.error(res.message)
        }
      })
    },
    // 点击保存
    save (row, scope) {
      quotaDetailApi.update(row).then((res) => {
        if (res.code == '0') {
          this.$message.success(res.message)
          this.getQuerydata()
        } else {
          this.$message.error(res.message)
          this.getQuerydata()
        }
      })
    },
    transformData (data) {
      const list = data.data.list
      data.data.list = list.map((item) => ({ ...item, editable: false }))
      return data
    },
    // 点击编辑
    edit (row, scope) {
      scope.row.editable = true
    },
    cancelEdit (row, scope) {
      scope.row.editable = false
      this.getQuerydata()
    },
    OsMaterialRequisitionAttachAddLine () {
      this.requirementAttaches.push({
        orgId: '',
        categoryId: '',
        itemId: '',
        quantityPerMonth: ''
      })
    },
    getVendorObj (val, scope) {
      console.log(val)
      if (val.organizationId) {
        scope.orgId = val ? val.organizationId : ''
        scope.orgCode = val ? val.organizationCode : ''
        scope.orgName = val ? val.organizationName : ''
      }
      if (val.categoryId) {
        scope.categoryId = val ? val.categoryId : ''
        scope.categoryName = val ? val.categoryName : ''
        scope.itemId = val ? val.materialId : ''
        scope.itemCode = val ? val.materialCode : ''
        scope.itemName = val ? val.materialName : ''
      }

      console.log(this.requirementAttaches)
    },
    handleSuccess () {
      this.getQuerydata()
    },
    downloadTemplate () {
      downloadFileLink(
        '/api-sup/sup/quotadetail/importExcelTemplate',
        '导入模板.xlsx'
      ).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail'))  // '下载失败'
      })
    },
    cancel () {
      this.visible = false
    },
    confirm () {
      this.$api.inq.quotadetail.add(this.requirementAttaches).then((res) => {
        this.$message(this.$t('components.approvalHead.tips.approvalCompletion'))  // '操作成功'
        this.visible = false
      })
    },

    syncFilterParams (values) {
      this.filterParams = values
    },
    getQuerydata (params) {
      this.queryParam = params
      this.queryParam.quotaOffsetId = this.row.quotaOffsetId
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    deleteHandle (row) {
      const _row = row
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          const quotaDetailId = _row.quotaDetailId
          this.$api.inq.quotadetail.delete(quotaDetailId).then((datas) => {
            this.$message({
              message: this.$t('components.approvalHead.tips.approvalCompletion'),  // '操作成功'
              type: 'success'
            })
            this.getQuerydata()
          })
        })
        .catch(() => {})
    },
    addHandle (row) {
      for (let i in this.form) {
        this.form[i] = ''
      }
      this.dialogTitle = this.$t('quota.quotaListAdded')  // '配额清单新增'
      this.visible = true
      this.mode = 'add'
    },
    editHandle (row) {
      this.form = row
      this.dialogTitle = this.$t('quota.quotaListEdit')  // '配额清单编辑'
      this.visible = true
      this.mode = 'edit'
    },
    handleCurrentChange (val) {
      this.currentRows = val
    }
  }
}
</script>
