<template>
  <el-container class="flex-container mouldheader_list_wrapper" direction="vertical">
    <el-main>
      <FormWrapper :form-array="filterConfig" @getFormData="getQueryData" />

      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <MImport
            code="materialWeight:import"
            :title="$t('common.import')"
            up-load-url="/api-cost/costMaterialRawMaterial/importExcel"
            :extra-data="{}"
            @downloadTemplate="downloadTemplate"
            @handleSuccess="handleSuccess"
          />
          <AuthorityButton type="primary" code="materialWeight:new" @click="add">
            {{ $t('common.add') }}
          </AuthorityButton>
          <!--  -->
          <AuthorityButton type="primary" code="materialWeight:save" @click="save">
            {{ $t('common.save') }}
          </AuthorityButton>
          <!--  -->
          <AuthorityButton type="primary" code="materialWeight:valid" @click="handlerFn('active')">
            {{ $t('common.active') }}
          </AuthorityButton>
          <!--  -->
          <AuthorityButton type="primary" code="materialWeight:invalid" @click="handlerFn('inactive')">
            {{ $t('common.inactive') }}
          </AuthorityButton>
          <!--  -->
          <AuthorityButton type="primary" code="materialWeight:del" @click="handlerFn('delete')">
            {{ $t('common.delete') }}
          </AuthorityButton>
          <ExportExcel
            code="materialWeight:export"
            page-url="/api-cost/api-ql/CostMaterialRawMaterial/customQuery"
            :filter-params="computedQueryParam"
            :table-header="tableHeader"
            :dict-codes="dictCodes"
            timeout="1000000"
            export-mode="front"
          />
        </template>
      </MainHeader>

      <TableView
        :ref="gridId"
        :table-header="tableHeader"
        :pre-query-data="queryParam"
        :adeptMeiQl="true"
        url="/api-cost/api-ql/CostMaterialRawMaterial/customQuery"
        :checkChange="handleCurrentChange"
        :open-custom-table="true"
        :checkbox="true"
        :isTriggerRow="false"
        :comActive="$attrs['changeTab']"
      />
    </el-main>
  </el-container>
</template>

<script>
import { tabTodoMixin, tabTodoWatch } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MImport from 'lib@/components/import'
import ExportExcel from 'lib@/components/export-excel'
import { parseTime } from '@/utils'
import { downloadFileLink } from 'lib@/utils/file'
import { transformMQL } from '@/library/utils/util'

export default {
  name: 'MeetTodoList',

  components: {
    TableView,
    MainHeader,
    MImport,
    ExportExcel,
    FormWrapper
  },

  mixins: [tabTodoWatch, tabTodoMixin],

  data () {
    return {
      dictCodes: {
        marketType: 'COST_LINK_MARKET_TYPE',
        status: 'BASE_INFO_STATUS'
      },
      extraData: {
        sourceType: 'WEB_APP',
        uploadType: 'FASTDFS',
        fileModular: 'base',
        fileFunction: 'demoorder',
        fileType: 'excel'
      },
      currentRow: [],
      gridId: 'list',
      tableHeader: [],
      filterConfig: [
        // 物料编码
        { prop: 'materialCode', label: '物料编码', queryOperator: 'contains' },

        // 物料名称
        { prop: 'materialName', label: '物料名称', queryOperator: 'contains' },
        // 原材料编码
        { prop: 'rawMaterialCode', label: this.$t('marketBudget.materialCode'), queryOperator: 'contains' },
        // 状态
        {
          prop: 'status',
          label: this.$t('marketBudget.status'),
          type: 'dict',
          code: 'BASE_INFO_STATUS'
        }
      ],
      queryParam: {}
    }
  },
  computed: {
    computedQueryParam () {
      let { pageNum, pageSize } = this.queryParam
      return {
        meiqlPayload: {
          ...this.queryParam
        },
        pageNum,
        pageSize
      }
    }
  },

  created () {
    this.tableHeader = [
      // 零件编码
      {
        prop: 'materialCode',
        label: '物料编码',
        minWidth: 150,
        editable: row => row.editable,
        addStarToColumn: true,
        showType: 'quicksearch',
        showKey: 'materialCode',
        name: 'scc_base_material_item',
        getObj: (e, row) => {
          row.materialCode = e.materialCode
          row.materialName = e.materialName
          row.materialId = e.materialId
        }
      },
      // 物料名称
      {
        prop: 'materialName',
        label: '物料名称',
        editable: row => row.editable,
        addStarToColumn: true,
        minWidth: 150
      },

      // 原材料编码
      {
        prop: 'rawMaterialCode',
        label: this.$t('marketBudget.materialCode'),
        minWidth: 150,
        editable: row => row.editable,
        addStarToColumn: true,
        // showType: 'input'
        showType: 'quicksearch',
        showKey: 'materialCode',
        preQueryData: {'t.status': 'VALID'},
        name: 'scc_cost_market_raw_material',
        getObj: (e, row) => {
          row.rawMaterialCode = e.rawMaterialCode
          row.rawMaterialName = e.rawMaterialName
          row.rawMaterialId = e.rawMaterialId
          row.marketType = e.marketType
        },
      },
      // 原材料名称
      {
        prop: 'rawMaterialName',
        label: this.$t('marketBudget.materialName'),
        minWidth: 150,
        addStarToColumn: true
      },
      // 重量
      {
        prop: 'rawMaterialWeight',
        label: this.$t('marketBudget.weight'),
        minWidth: 150,
        addStarToColumn: true,
        editable: row => row.editable,
        showType: 'input',
        controls: false
      },
      // 单位
      {
        prop: 'rawMaterialUnit',
        label: this.$t('marketBudget.unit'),
        minWidth: 150,
        addStarToColumn: true,
        editable: row => row.editable,
        showType: 'dictSelect',
        code: 'unit',
        formattor: val => this.$getDictLabel('unit', val)
      },
      // 市况类型
      {
        prop: 'marketType',
        label: this.$t('marketBudget.marketType'),
        minWidth: 150,
        addStarToColumn: true,
        formattor: val => this.$getDictLabel('COST_LINK_MARKET_TYPE', val)
      },

      // 状态
      {
        prop: 'status',
        label: this.$t('marketBudget.status'),
        minWidth: 150,
        formattor: val => this.$getDictLabel('BASE_INFO_STATUS', val)
      },
      // 生效时间
      {
        prop: 'effectiveTime',
        label: this.$t('marketBudget.enableDate'),
        minWidth: 150
        // formattor: cellValue => (cellValue ? parseTime(cellValue, '{y}-{m}-{d}') : ''),
      },
      // 失效时间
      {
        prop: 'expiredTime',
        label: this.$t('marketBudget.disableDate'),
        minWidth: 150
        // formattor: cellValue => (cellValue ? parseTime(cellValue, '{y}-{m}-{d}') : ''),
      },
      {
        prop: 'operation',
        label: this.$t('common.operation'),
        width: 150,
        showType: 'buttons',
        fixed: 'right',
        btnStyle: 'text',
        buttons: [
          // 编辑
          {
            callback: row => this.$set(row, 'editable', true),
            formattor: () => this.$t('common.edit'),
            code: 'materialWeight:edit',
            show: row => !row.editable && row.status === 'DRAFT'
          },
          // 删除
          {
            callback: row => this.deleteRow(row),
            formattor: () => '删除',
            show: row => row.editable && !row.materialRawMaterialId
          },
          // 取消行编辑
          {
            callback: () => this.getQueryData(),
            formattor: () => this.$t('common.cancel'),
            show: row => row.editable && row.materialRawMaterialId
          },
          // 保存
          {
            callback: row => this.save([row]),
            formattor: () => '保存',
            show: row => row.editable
          }
        ]
      }
    ]

    this.$nextTick(() => {
      this.getQueryData()
    })
  },

  methods: {
    // 导入成功回调
    handleSuccess () {
      this.getQueryData()
    },
    downloadTemplate () {
      downloadFileLink('/api-cost/costMaterialRawMaterial/template', '导入模板.xlsx').catch(
        () => {
          this.$message.error('下载失败')
        },
      )
    },
    handleCurrentChange (val) {
      this.currentRow = val
    },

    getQueryData (params = {}) {
      this.queryParam = transformMQL.listGetData('CostMaterialRawMaterial', params, 'lastUpdateDate', undefined, 'customQuery', undefined, { status: 'eq' })
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },

    // active 生效  inactive  失效  delete  删除
    handlerFn (type) {
      const obj = {
        active: {
          url: '/api-cost/api-ql/CostMaterialRawMaterial/updateStatus',
          tips: '请勾选处理状态为“拟定”或者“失效”的数据，进行生效操作',
          status: ['DRAFT', 'INVALID'],
          title: this.$t('marketBudget.enableCurrent'),
          method: 'POST',
          action: 'updateStatus',
          payload: {
            'filter': {
              'materialRawMaterialId': {
                'in': this.currentRow.map(item => item.materialRawMaterialId)
              }
            },
            'data': {
              'status': 'VALID'
            }
          }
        },
        inactive: {
          url: '/api-cost/api-ql/CostMaterialRawMaterial/updateStatus',
          tips: '请勾选处理状态为“生效”的数据，进行失效操作',
          status: ['VALID'],
          title: this.$t('marketBudget.disableCurrent'),
          method: 'POST',
          action: 'updateStatus',
          payload: {
            'filter': {
              'materialRawMaterialId': {
                'in': this.currentRow.map(item => item.materialRawMaterialId)
              }
            },
            'data': {
              'status': 'INVALID'
            }
          }
        },
        delete: {
          url: '/api-cost/api-ql/CostMaterialRawMaterial/batchDelete',
          tips: '请勾选处理状态为“拟定”的数据，进行删除操作',
          status: ['DRAFT'],
          title: this.$t('common.confirmDelete'),
          method: 'POST',
          action: 'batchDelete',
          payload: this.currentRow.map(item => { return { 'materialRawMaterialId': item.materialRawMaterialId } })
        }
      }
      if (obj[type]) {
        const { url, tips, status, title, method, payload, action } = obj[type]
        const flag = this.currentRow.some(v => !status.includes(v.status))
        if (flag || this.currentRow.length === 0) {
          this.$message.warning(tips)
          return
        }
        const data = this.currentRow.map(item => item.materialRawMaterialId)
        let formData = transformMQL.save('CostMaterialRawMaterial', payload, action)

        this.$confirm(title, {
          confirmButtonText: this.$t('common.confirm'),
          cancelButtonText: this.$t('common.cancel'),
          type: 'warning'
        }).then(() => {
          this.$http({
            url,
            method,
            data: formData,
            loading: true
          }).then(res => {
            this.$message.success(res.message)
            this.getQueryData()
          })
        })
      }
    },

    // 新增
    add () {
      const row = {
        editable: true,
        materialCode: null,
        materialName: null,
        rawMaterialCode: null,
        rawMaterialName: null,
        rawMaterialWeight: null,
        marketType: null,
        status: 'DRAFT',
        rawMaterialUnit: null,
        __i: Date.now() // 用于删除
      }
      this.$refs[this.gridId].addOneEditableColumn(row)
    },
    // 保存
    save () {
      const data = this.$refs[this.gridId].tableData.filter(v => v.editable)
      if (data.length === 0) {
        return
      }
      if (data.some(v => !v.materialCode || !v.rawMaterialCode || !v.rawMaterialName || !v.rawMaterialWeight || !v.rawMaterialUnit)) {
        this.$message.warning('请检查必填项')
        return
      }
      let formData = transformMQL.save('CostMaterialRawMaterial', data, 'customSave')
      this.$http({
        url: '/api-cost/api-ql/CostMaterialRawMaterial/customSave',
        method: 'POST',
        data: formData,
        loading: true
      }).then(() => {
        this.$message.success('成功')
        this.getQueryData()
      })
    },
    // 行删除
    deleteRow (row) {
      const ref = this.$refs[this.gridId]
      const i = ref.tableData.findIndex(v => v.__i === row.__i)
      ref.tableData.splice(i, 1)
    }
  }
}
</script>
