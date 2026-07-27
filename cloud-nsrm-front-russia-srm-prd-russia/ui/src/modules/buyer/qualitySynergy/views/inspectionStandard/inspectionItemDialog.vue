<template>
  <srm-dialog
    :title="$t('qualitySynergy.inspectionItemDialog')"
    size="large"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
    append-to-body
  >
    <FormWrapper
      ref="formRef"
      :form-array="preArr"
      init-active
      @getFormData="getQuerydata"
      @synchronous-value="syncFilterParams"
    />
    <TableView
      :ref="gridId"
      :table-data="tableData"
      :table-header="tableHeader"
      :page-size="pageSize"
      :pre-query-data="queryParam"
      :row-index="false"
      checkbox
      reserveSelection
      row-key="inspectionProjectId"
      :check-change="checkChange"
      :setSelectable="setSelectable"
      url="/api-pef/perf/inspectionproject/listPage"
    />
    <div slot="footer" class="dialog-footer">
      <el-button @click="dialogVisible = false">
        {{ $t("common.cancel") }}
      </el-button>
      <el-button type="primary" @click="save">
        {{ $t("common.confirm") }}
      </el-button>
    </div>
  </srm-dialog>
</template>
<script>
import FormWrapper from 'lib@/components/Table/FormWrapper'
import TableView from 'lib@/components/Table/TableView'

export default {
  name: 'InspectionItemDialog',
  components: {
    FormWrapper,
    TableView
  },

  props: {
    visible: {
      type: Boolean
    }
  },

  data () {
    return {
      selections: [],
      selected: [],
      pageSize: 15,
      gridId: 'list',
      tableHeader: [],
      tableData: [],
      preArr: [
        // 项目名称
        {
          prop: 'inspectionProjectName',
          label: this.$t('qualitySynergy.inspectionProjectName1')
        },
        // 项目特性
        {
          prop: 'inspectionProjectPerf',
          label: this.$t('qualitySynergy.inspectionProjectPerf'),
          type: 'dict',
          code: 'INS_PRO_PERF'
        },
        // 检验项目属性
        {
          prop: 'inspectionProjectAttribute',
          label: this.$t('qualitySynergy.inspectionProjectAttribute'),
          type: 'dict',
          code: 'INS_PRO_ATTR'
        }
      ],
      queryParam: {}
    }
  },

  computed: {
    dialogVisible: {
      get: function () {
        return this.visible
      },
      set: function (val) {
        this.$emit('update:visible', val)
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
      }
    ]
  },

  methods: {
    init (selected) {
      this.getQuerydata()
      this.selected = selected
    },
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
    checkChange (val) {
      this.selections = val
    },
    setSelectable (row) {
      return this.selected.findIndex(item => item.inspectionProjectId === row.inspectionProjectId) === -1
    },
    save () {
      this.dialogVisible = false
      this.$emit('getSelections', this.selections)
    }
  }
}
</script>
