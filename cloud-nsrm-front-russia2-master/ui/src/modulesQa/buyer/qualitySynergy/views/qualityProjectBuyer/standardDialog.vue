<template>
  <srm-dialog
    :close-on-click-modal="false"
    :visible.sync="dialogVisible"
    :destroyOnClose="true"
    class="c-form-dialog"
    :contentMaxHeightLimit="false"
    fullscreen
    size="xLarge"
    :title="dialogTitle"
    append-to-body
    @close="close"
  >
    <FormWrapper :formArray="preArr" @getFormData="queryStandardData" />
    <MainHeader style="padding-top: 0">
      <template slot="left">
        <el-button
          type="primary"
          size="mini"
          @click="addStandard"
        >
          {{ $t('common.add') }}
        </el-button>
        <el-button
          type="primary"
          size="mini"
          :disabled="!standardSelectList.length"
          @click="activeStandard"
        >
          {{ $t('common.active') }}
        </el-button>
        <el-button
          type="primary"
          size="mini"
          :disabled="!standardSelectList.length"
          @click="inactiveStandard"
        >
          {{ $t('common.inactive') }}
        </el-button>
        <ExportExcel
          page-url="/api-qc/api-ql/SpcRuleStandardBuyer/query"
          :filter-params="computedQueryParam"
          :table-header="standardHeader"
          :dict-codes="dictCodes"
          export-mode="front"
          type="default"
        />
      </template>
    </MainHeader>
    <TableView
      :ref="'standardGridId'"
      style="height: 50vh"
      :table-data="standardData"
      :table-header="standardHeader"
      :checkbox="true"
      :preQueryData="standardQueryParam"
      :check-change="standardSelectionChange"
      :adeptMeiQl="true"
      :isTriggerRow="false"
      :open-custom-table="false"
      :comActive="$attrs['changeTab']"
      url="/api-qc/api-ql/SpcRuleStandardBuyer/query"
    />
  </srm-dialog>
</template>

<script>
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import TableView from 'lib@/components/Table/TableView'
import ExportExcel from 'lib@/components/export-excel'
import { transformMQL } from '@/library/utils/util'
import { tabTodoMixin } from '@/utils/mixins'
import { qualityProject, spcStandard } from '@/modulesQa/buyer/qualitySynergy/api'
const { pageCondition } = qualityProject
const { spcStandardAdd, spcStandardValid, spcStandardInvalid, spcStandardModify } = spcStandard
export default {
  name: 'StandardDialog',

  components: {
    FormWrapper, TableView, MainHeader, ExportExcel
  },
  mixins: [tabTodoMixin],
  props: {
    visible: {
      type: Boolean
    }
  },
  data () {
    return {
      dictCodes: {
        state: 'SPC_STANDARD_STATE'
      },
      standardHeader: [],
      standardData: [],
      standardSelectList: [],
      standardSelectDel: [],
      standardQueryParam: {},
      currentStandardRow: {},
      preArr: [
        { prop: 'customerOrgId',
          label: this.$t('quality.customerOrgName'),
          type: 'OUorganizationSelector'
        },
        { prop: 'workCenter',
          label: this.$t('quality.workCenter'),
          type: 'select',
          options: []
        },
        { prop: 'onLineEquipment',
          label: this.$t('quality.onLineEquipment'),
          type: 'select',
          options: []
        },
        { prop: 'monitoringFeature',
          label: this.$t('quality.monitoringFeature'),
          type: 'select',
          options: []
        },
        { prop: 'itemCode',
          label: this.$t('quality.itemCode'),
          type: 'quicksearch',
          showKey: 'materialCode',
          name: 'scc_base_material_item'
        },
        {
          prop: 'drawingsArea',
          label: this.$t('quality.drawingsArea')
        }
      ]
    }
  },
  computed: {
    dialogTitle () {
      return this.$t('quality.project.spcStandardEdit') + `${this.currentStandardRow.monitorListNo}`
    },
    dialogVisible: {
      get: function () {
        return this.visible
      },
      set: function (val) {
        this.$emit('update:visible', val)
      }
    },
    computedQueryParam () {
      let { pageNum, pageSize } = this.standardQueryParam
      return {
        meiqlPayload: {
          ...this.standardQueryParam
        },
        pageNum,
        pageSize
      }
    }
  },
  created () {
    this.getPageCondition()
    this.standardHeader = [
      {
        prop: 'state',
        label: this.$t('qualitySynergy.inspectionStandardStatus'),
        dataType: 'dict',
        code: 'SPC_STANDARD_STATE'
      },
      { prop: 'monitorListNo',
        label: this.$t('quality.monitorListNo'),
        minWidth: 160
      },
      { prop: 'customerOrgName',
        label: this.$t('quality.customerOrgName'),
        minWidth: 120
      },
      {
        prop: 'itemCode',
        label: this.$t('quality.itemCode'),
        minWidth: 150,
        editable: row => row.editable,
        addStarToColumn: true,
        showType: 'quicksearch',
        showKey: 'materialCode',
        name: 'scc_base_material_item_display',
        getObj: (e, row) => {
          row.itemId = e.materialId
          row.itemCode = e.materialCode
          row.itemDesc = e.materialName
        }
      },
      { prop: 'itemDesc',
        label: this.$t('quality.itemDesc'),
        minWidth: 130
      },
      { prop: 'lineCode',
        label: this.$t('quality.lineCode')
      },
      { prop: 'workCenter',
        label: this.$t('quality.workCenter'),
        minWidth: 130
      },
      { prop: 'onLineEquipment',
        label: this.$t('quality.onLineEquipment')
      },
      { prop: 'monitoringFeature',
        label: this.$t('quality.monitoringFeature')
      },
      { prop: 'spUnit',
        label: this.$t('quality.spUnit')
      },
      { prop: 'characterUnit',
        label: this.$t('quality.characterUnit')
      },
      { prop: 'drawingsArea',
        editable: row => row.editable,
        showType: 'input',
        addStarToColumn: true,
        label: this.$t('quality.drawingsArea')
      },
      { prop: 'standardMax',
        label: this.$t('quality.standardMax'),
        editable: row => row.editable,
        showType: 'input',
        addStarToColumn: true,
        align: 'right'
      },
      { prop: 'standardMin',
        label: this.$t('quality.standardMin'),
        editable: row => row.editable,
        showType: 'input',
        addStarToColumn: true,
        align: 'right'
      },
      // {
      //   prop: 'controlDrawings',
      //   label: '控制图',
      //   editable: row => row.editable,
      //   showType: 'dictSelect',
      //   addStarToColumn: true,
      //   code: 'SPC_CONTROL_DRAWINGS_TYPE'
      // },
      { prop: 'targetValue',
        label: this.$t('quality.targetValue'),
        editable: row => row.editable,
        showType: 'input',
        addStarToColumn: true,
        align: 'right'
      },
      { prop: 'avgUcl',
        label: this.$t('quality.avgUcl'),
        editable: row => row.editable,
        showType: 'input',
        addStarToColumn: true,
        minWidth: 130,
        align: 'right'
      },
      { prop: 'avgLcl',
        label: this.$t('quality.avgLcl'),
        editable: row => row.editable,
        showType: 'input',
        addStarToColumn: true,
        minWidth: 120,
        align: 'right'
      },
      { prop: 'avgMcl',
        label: this.$t('quality.avgMcl'),
        editable: row => row.editable,
        showType: 'input',
        addStarToColumn: true,
        minWidth: 110,
        align: 'right'
      },
      { prop: 'rangeUcl',
        label: this.$t('quality.rangeUcl'),
        editable: row => row.editable,
        showType: 'input',
        addStarToColumn: true,
        minWidth: 120,
        align: 'right'
      },
      { prop: 'rangeLcl',
        label: this.$t('quality.rangeLcl'),
        editable: row => row.editable,
        showType: 'input',
        addStarToColumn: true,
        minWidth: 120,
        align: 'right'
      },
      { prop: 'rangeMcl',
        label: this.$t('quality.rangeMcl'),
        editable: row => row.editable,
        showType: 'input',
        addStarToColumn: true,
        minWidth: 110,
        align: 'right'
      },
      // { prop: 'version',
      //   label: '版本号',
      //   editable: row => row.editable,
      //   showType: 'input',
      //   addStarToColumn: true,
      //   align: 'right'
      // },
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
            callback: row => this.$set(row, 'editable', true),
            formattor: () => this.$t('common.edit'),
            // code: 'marketType:material:edit',
            show: row => !row.editable && row.state === 'A'
          },
          // 删除
          {
            callback: row => this.deleteRow(row),
            formattor: () => this.$t('common.delete'),
            show: row => row.editable && !row.id
          },
          // 取消行编辑
          {
            callback: row => this.$set(row, 'editable', ''),
            formattor: () => this.$t('common.cancel'),
            show: row => row.editable && row.id
          },
          // 保存
          {
            callback: row => this.saveStandard(row),
            formattor: () => this.$t('common.save'),
            show: row => row.editable
          }
        ]
      }
    ]
  },
  methods: {
    close () {
      this.__setTabTodo('QualityProjectBuyerList.refresh')
    },
    activeStandard () {
      if (this.standardSelectList.find(item => item.state == 'A')) {
        return this.$message.warning(this.$t('quality.project.selectRule'))
      }
      let formData = transformMQL.save('SpcRuleStandardBuyer', this.standardSelectDel, 'validRuleStandard')
      spcStandardValid(formData).then(response => {
        const { data } = response
        if (response) {
          this.$message.success(this.$t('common.successUpdate'))
          this.$refs['standardGridId'].query()
        }
      })
    },
    inactiveStandard () {
      if (this.standardSelectList.find(item => item.state != 'A')) {
        return this.$message.warning(this.$t('quality.project.selectUseRule'))
      }
      let formData = transformMQL.save('SpcRuleStandardBuyer', this.standardSelectDel, 'inValidRuleStandard')
      spcStandardInvalid(formData).then(response => {
        const { data } = response
        if (response) {
          this.$message.success(this.$t('common.successUpdate'))
          this.$refs['standardGridId'].query()
        }
      })
    },
    standardSelectionChange (val) {
      this.standardSelectList = val
      this.standardSelectDel = []
      this.standardSelectList.map(item => {
        this.standardSelectDel.push(item.id)
      })
    },
    getPageCondition () {
      let transformParams = transformMQL.save('spcMonitorListBuyer', {}, 'pageCondition')
      pageCondition(transformParams).then(response => {
        const data = response.data.records[0]
        let workCenterList = []; let onLineEquipmentList = []; let monitoringFeatureList = []
        // 工作中心下拉
        Object.keys(data.workCenterMap).forEach(item => {
          let newObj = {}
          newObj.label = item
          newObj.value = data.workCenterMap[item]
          workCenterList.push(newObj)
        })
        // 联机设备下拉
        Object.keys(data.onLineEquipmentMap).forEach(item => {
          let newObj = {}
          newObj.label = item
          newObj.value = data.onLineEquipmentMap[item]
          onLineEquipmentList.push(newObj)
        })
        // 监控特性下拉
        Object.keys(data.monitoringFeatureMap).forEach(item => {
          let newObj = {}
          newObj.label = item
          newObj.value = data.monitoringFeatureMap[item]
          monitoringFeatureList.push(newObj)
        })
        for (let item of this.preArr) {
          switch (item.prop) {
          case 'workCenter':
            item.options = workCenterList
            break
          case 'onLineEquipment':
            item.options = onLineEquipmentList
            break
          case 'monitoringFeature':
            item.options = monitoringFeatureList
            break
          }
        }
      })
    },
    addStandard () {
      let data = this.currentStandardRow
      const row = {
        editable: true,
        spcMonitorListId: data.id,
        monitorListNo: data.monitorListNo,
        itemCode: null,
        itemDesc: null,
        lineCode: data.lineCode,
        workCenter: data.workCenter,
        monitoringFeature: data.monitoringFeature,
        characterUnit: data.characterUnit,
        drawingsArea: null,
        standardMax: null,
        targetValue: null,
        standardMin: null,
        state: 'A',
        controlDrawings: null,
        vendorCode: data.vendorCode,
        vendorId: data.vendorId,
        vendorName: data.vendorName,
        // 以下是产品的
        customerOrgId: data.customerOrgId,
        customerOrgName: data.customerOrgName,
        onLineEquipment: data.onLineEquipment,
        spUnit: data.spUnit,
        avgUcl: null,
        avgLcl: null,
        avgMcl: null,
        rangeUcl: null,
        rangeLcl: null,
        rangeMcl: null,
        version: null,
        __i: Date.now() // 用于删除
      }
      this.$refs['standardGridId'].addOneEditableColumn(row)
    },
    deleteRow (row) {
      const ref = this.$refs['standardGridId']
      const i = ref.tableData.findIndex(v => v.__i === row.__i)
      ref.tableData.splice(i, 1)
    },
    saveStandard (row) {
      let requireFlag = this.standardHeader.findIndex(item => {
        return item.addStarToColumn && row[item.prop] === null
      })
      if (requireFlag > -1) return this.$message.warning(this.$t('common.pleasefinishRequired'))

      const pattern = /^\d+(\.\d{1,4})?$/
      const inputs = [ row.standardMax, row.standardMin, row.targetValue ]
      let flag = inputs.findIndex(item => {
        return !pattern.test(item)
      })
      if (flag > -1) return this.$message.warning(this.$t('quality.project.sumitTip'))
      const inputs2 = [ row.avgLcl, row.avgUcl, row.avgMcl, row.rangeLcl, row.rangeUcl, row.rangeMcl ]
      let flag2 = inputs.findIndex(item => {
        return !pattern.test(item)
      })
      if (flag2 > -1) return this.$message.warning(this.$t('quality.project.submit2'))
      if (row.id) {
        let formData = transformMQL.save('SpcRuleStandardBuyer', [row], 'update')
        spcStandardModify(formData).then(response => {
          const { data } = response
          if (response) {
            this.$message.success(this.$t('common.success'))
            this.$refs['standardGridId'].query()
          }
        })
      } else {
        let formData = transformMQL.save('SpcRuleStandardBuyer', [row], 'create')
        spcStandardAdd(formData).then(response => {
          const { data } = response
          if (response) {
            this.$message.success(this.$t('common.success'))
            this.$refs['standardGridId'].query()
          }
        })
      }
    },
    init (row) {
      this.standardQueryParam = {}
      this.standardQueryParam.monitorListNo = row.monitorListNo
      this.currentStandardRow = row
      this.queryStandardData(this.standardQueryParam)
    },
    queryStandardData (params = {}) {
      params.monitorListNo = this.currentStandardRow.monitorListNo
      this.standardQueryParam = transformMQL.listGetData('SpcRuleStandardBuyer', params, 'lastUpdateDate', undefined, 'query')
      this.$nextTick(() => {
        this.$refs['standardGridId'].query()
      })
    }
  }
}
</script>
