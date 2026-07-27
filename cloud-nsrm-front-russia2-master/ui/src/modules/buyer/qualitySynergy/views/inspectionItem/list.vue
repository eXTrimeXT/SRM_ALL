<template>
  <el-container class="flex-container" direction="vertical">
    <el-main>
      <FormWrapper
        ref="formRef"
        :form-array="preArr"
        :init-active="true"
        @getFormData="getQuerydata"
        @synchronous-value="syncFilterParams"
      />

      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <AuthorityButton type="primary" @click="addOne">
            {{ $t("common.add") }}
          </AuthorityButton>
          <!-- 导入 -->
          <MImport
            ref="import"
            :title="$t('common.import')"
            :extraData="extraData"
            upLoadUrl="/api-pef/perf/inspectionproject/importExcel"
            type="default"
            @downloadTemplate="downloadTemplate"
            @handleSuccess="handleSuccess"
          />

          <ExportExcel
            v-loading
            page-url="/api-pef/perf/inspectionproject/listPage"
            :filter-params="queryParam"
            :table-header="tableHeader"
            :dict-codes="dictCodes"
            :title="$t('common.export')"
            timeout="1000000"
            export-mode="front"
            type="default"
          />
        </template>
      </MainHeader>

      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        url="/api-pef/perf/inspectionproject/listPage"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MainHeader from 'lib@/components/Table/MainHeader'
import TableView from 'lib@/components/Table/TableView'
import ExportExcel from 'lib@/components/export-excel'
import OrganizationSelector from 'lib@/components/organization-selector'
import inspectionItemDetail from './edit'
import MImport from 'lib@/components/import'
import { downloadFileLink } from 'lib@/utils/file'
import { inspectionStandard } from 'modb@/qualitySynergy/api'

export default {
  name: 'InspectionItemList',
  components: {
    FormWrapper,
    MainHeader,
    TableView,
    OrganizationSelector,
    ExportExcel,
    MImport,
    downloadFileLink
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      dictCodes: {
        inspectionProjectAttribute: 'INS_PRO_ATTR',
        inspectionProjectPerf: 'INS_PRO_PERF',
        perfGrade: 'INS_PERF_GRADE',
        sampleStandard: 'INS_SAMPLE_STANDARD',
        inspectionLevel: 'INS_INSPECTION_LEVEL',
        strictLevel: 'INS_SEVERITY',
        problemExchangeRiskLevel: 'INS_INSPECTION_LEVEL',
        problemExchangeStage: 'INS_SEVERITY',
        sampleMode: 'INS_SAMPLING_METHOD',
        inspectionType: 'INS_TYPE'
      },
      pageSize: 15,
      gridId: 'list',
      tableHeader: [],
      tableData: [],
      preArr: [
        // 项目特性
        {
          prop: 'inspectionProjectPerf',
          label: this.$t('qualitySynergy.inspectionProjectPerf'),
          type: 'dict',
          code: 'INS_PRO_PERF'
        },
        // 特性分级
        {
          prop: 'perfGrade',
          label: this.$t('qualitySynergy.perfGrade'),
          type: 'dict',
          code: 'INS_PERF_GRADE'
        },
        // 检验类型
        {
          prop: 'inspectionType',
          label: this.$t('qualitySynergy.inspectionType'),
          type: 'dict',
          code: 'INS_TYPE'
        },
        // 检验项目属性
        {
          prop: 'inspectionProjectAttribute',
          label: this.$t('qualitySynergy.inspectionProjectAttribute'),
          type: 'dict',
          code: 'INS_PRO_ATTR'
        }
      ],
      queryParam: {},
      extraData: {
        fileType: 'excel',
        fileModular: 'qualitySynergy',
        fileFunction: 'inspectionItem'
      }
    }
  },
  created () {
    this.tableHeader = [
      // 检验项目
      {
        prop: 'inspectionProjectName',
        label: this.$t('qualitySynergy.inspectionProjectName')
      },
      // 检验项目属性
      {
        prop: 'inspectionProjectAttribute',
        label: this.$t('qualitySynergy.inspectionProjectAttribute'),
        width: 120,
        dataType: 'dict',
        code: 'INS_PRO_ATTR'
      },
      // 检验工具
      {
        prop: 'inspectionTool',
        label: this.$t('qualitySynergy.inspectionTool'),
        width: 120
      },
      // 检验项目类别
      {
        prop: 'inspectionCategory',
        label: this.$t('qualitySynergy.inspectionCategory'),
        width: 120
      },
      // 项目特性
      {
        prop: 'inspectionProjectPerf',
        label: this.$t('qualitySynergy.inspectionProjectPerf'),
        width: 120,
        dataType: 'dict',
        code: 'INS_PRO_PERF'
      },
      // 特性分级
      {
        prop: 'perfGrade',
        label: this.$t('qualitySynergy.perfGrade'),
        width: 120,
        dataType: 'dict',
        code: 'INS_PERF_GRADE'
      },
      // 抽样标准
      {
        prop: 'sampleStandard',
        label: this.$t('qualitySynergy.sampleStandard'),
        width: 120,
        dataType: 'dict',
        code: 'INS_SAMPLE_STANDARD'
      },
      // 检验水平
      {
        prop: 'inspectionLevel',
        label: this.$t('qualitySynergy.inspectionLevel'),
        width: 120,
        dataType: 'dict',
        code: 'INS_INSPECTION_LEVEL'
      },
      // 严格度
      {
        prop: 'strictLevel',
        label: this.$t('qualitySynergy.strictLevel'),
        width: 120,
        dataType: 'dict',
        code: 'INS_SEVERITY'
      },
      // 抽样方式
      {
        prop: 'sampleMode',
        label: this.$t('qualitySynergy.sampleMode'),
        width: 100,
        dataType: 'dict',
        code: 'INS_SAMPLING_METHOD'
      },
      // 检验类型
      {
        prop: 'inspectionType',
        label: this.$t('qualitySynergy.inspectionType'),
        width: 120,
        dataType: 'dict',
        code: 'INS_TYPE'
      },
      // 检验依据
      {
        prop: 'inspectionBasis',
        label: this.$t('qualitySynergy.inspectionBasis'),
        width: 120
      },
      {
        prop: 'operation',
        label: this.$t('common.operation'),
        width: 120,
        showType: 'buttons',
        fixed: 'right',
        btnStyle: 'text',
        buttons: [
          // 编辑
          {
            callback: row => this.editOne(row),
            formattor: () => this.$t('common.edit')
          },
          // 删除
          {
            callback: row => this.deleteOne(row),
            formattor: () => this.$t('common.delete')
          }
        ]
      }
    ]
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    getQuerydata (obj) {
      let objs = obj || this.queryParam
      this.queryParam = { ...objs }
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    syncFilterParams (values) {
      this.queryParam = values
    },
    addOne () {
      this.$emit('tab-add', {
        component: inspectionItemDetail,
        params: {
          flag: 'add',
          tabName: 'inspectionItemDetail'
        },
        // 检验项目维护
        title: this.$t('qualitySynergy.inspectionItemDetail'),
        name: 'inspectionItemDetail'
      })
    },
    downloadTemplate () {
      downloadFileLink(
        '/api-pef/perf/inspectionproject/exportExcelTemplate',
        this.$t('qualitySynergy.inspectionprojectImportTemp'),
      ).catch(err => {
        this.$message.error(err.message)
      })
    },
    handleSuccess () {
      this.$refs[this.gridId].query()
    },

    editOne (row) {
      this.$emit('tab-add', {
        component: inspectionItemDetail,
        params: {
          flag: 'edit',
          row: row,
          tabName: 'inspectionItemDetail' + row.inspectionProjectId
        },
        title: row.inspectionProjectName,
        name: 'inspectionItemDetail' + row.inspectionProjectId
      })
    },
    deleteOne ({ inspectionProjectId }) {
      // 当前操将永久删除此数据，确认删除此数据
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(async () => {
          await inspectionStandard.inspectionItemDelete({ id: inspectionProjectId })
          // 删除成功
          this.$message.success(this.$t('common.successDelete'))
          this.$refs[this.gridId].query()
        })
        .catch(() => {})
    }
  }
}
</script>

<style lang="scss" scoped></style>
