<template>
  <el-container class="flex-container mouldheader_list_wrapper" direction="vertical">
    <el-main>
      <FormWrapper :form-array="filterConfig" @getFormData="getQueryData" />

      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <AuthorityButton type="primary" code="marketType:material:new" @click="add">
            {{ $t('common.add') }}
          </AuthorityButton>
          <MImport
            code="marketType:material:import"
            :title="$t('common.import')"
            up-load-url="/api-cost/costMarketRawMaterial/importExcel"
            :extra-data="{}"
            type="ghost"
            @downloadTemplate="downloadTemplate"
            @handleSuccess="handleSuccess"
          />
          <AuthorityButton code="marketType:material:save" @click="save()">
            {{ $t('common.save') }}
          </AuthorityButton>
          <AuthorityButton code="marketType:material:valid" @click="handlerFn('active')">
            {{ $t('common.active') }}
          </AuthorityButton>
          <AuthorityButton code="marketType:material:invalid" @click="handlerFn('inactive')">
            {{ $t('common.inactive') }}
          </AuthorityButton>
          <AuthorityButton code="marketType:material:delete" @click="handlerFn('delete')">
            {{ $t('common.delete') }}
          </AuthorityButton>
          <ExportExcel
            page-url="/api-cost/api-ql/CostMarketRawMaterial/customQuery"
            :filter-params="computedQueryParam"
            :table-header="tableHeader"
            :dict-codes="dictCodes"
            timeout="1000000"
            type="ghost"
            export-mode="front"
          />
        </template>
      </MainHeader>

      <TableView
        :ref="gridId"
        :table-header="tableHeader"
        :pre-query-data="queryParam"
        :adeptMeiQl="true"
        url="/api-cost/api-ql/CostMarketRawMaterial/customQuery"
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
        frequency: 'COST_LINK_FREQUENCY',
        status: 'COST_MARKET_ROW_MATERIAL_STATUS'
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
        // 市况类型
        {
          prop: 'marketType',
          label: this.$t('marketBudget.marketType'),
          type: 'dict',
          code: 'COST_LINK_MARKET_TYPE'
        },
        // 原材料名称
        { prop: 'rawMaterialName', label: this.$t('marketBudget.materialName')
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
      // 市况类型
      {
        label: () => this.$t('marketBudget.marketType'),
        prop: 'marketType',
        minWidth: 150,
        editable: row => row.editable,
        addStarToColumn: true,
        showType: 'dictSelect',
        code: 'COST_LINK_MARKET_TYPE',
        formattor: val => this.$getDictLabel('COST_LINK_MARKET_TYPE', val)
      },
      // 原材料编码
      {
        prop: 'rawMaterialCode',
        label: this.$t('marketBudget.materialCode'),
        minWidth: 150,
        editable: row => row.editable,
        showType: 'input',
        addStarToColumn: true
        // showType: 'quicksearch',
        // showKey: 'materialCode',
        // name: 'scc_base_material_item',
        // getObj: (e, row) => {
        //   row.rawMaterialCode = e.materialCode
        //   row.rawMaterialName = e.materialName
        //   row.rawMaterialId = e.materialId
        // }
      },
      // 原材料名称
      {
        prop: 'rawMaterialName',
        label: () => this.$t('marketBudget.materialName'),
        editable: row => row.editable,
        addStarToColumn: true,
        showType: 'input',
        minWidth: 150
      },

      // 联动频次
      {
        label: () => this.$t('marketBudget.linkFrequency'),
        prop: 'frequency',
        minWidth: 150,
        editable: row => row.editable,
        addStarToColumn: true,
        showType: 'dictSelect',
        code: 'COST_LINK_FREQUENCY',
        formattor: val => this.$getDictLabel('COST_LINK_FREQUENCY', val)
      },

      // 状态
      {
        prop: 'status',
        label: this.$t('marketBudget.status'),
        minWidth: 150,
        formattor: val => this.$getDictLabel('COST_MARKET_ROW_MATERIAL_STATUS', val)
      },
      // 生效时间
      {
        prop: 'effectiveTime',
        label: this.$t('marketBudget.enableDate'),
        minWidth: 150
      },
      // 失效时间
      {
        prop: 'expiredTime',
        label: this.$t('marketBudget.disableDate'),
        minWidth: 150
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
            code: 'marketType:material:edit',
            show: row => !row.editable && row.status === 'DRAFT'
          },
          // 删除
          {
            callback: row => this.deleteRow(row),
            formattor: () => '删除',
            show: row => row.editable && !row.marketRawMaterialId
          },
          // 取消行编辑
          {
            callback: row => this.$set(row, 'editable', ''),
            formattor: () => this.$t('common.cancel'),
            show: row => row.editable && row.marketRawMaterialId
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
      downloadFileLink('/api-cost/costMarketRawMaterial/template', '导入模板.xlsx').catch(() => {
        this.$message.error('下载失败')
      })
    },
    handleCurrentChange (val) {
      this.currentRow = val
    },
    getQueryData (params = {}) {
      this.queryParam = transformMQL.listGetData('CostMarketRawMaterial', params, 'lastUpdateDate', undefined, 'customQuery', undefined, { status: 'eq' })
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },

    // active 生效  inactive  失效  delete  删除
    handlerFn (type) {
      const obj = {
        active: {
          url: '/api-cost/api-ql/CostMarketRawMaterial/updateStatus',
          tips: '请勾选处理状态为“拟定”或者“失效”的数据，进行生效操作',
          status: ['DRAFT', 'INVALID'],
          title: this.$t('marketBudget.enableCurrent'),
          method: 'POST',
          action: 'updateStatus',
          payload: {
            'filter': {
              'marketRawMaterialId': {
                'in': this.currentRow.map(item => item.marketRawMaterialId)
              }
            },
            'data': {
              'status': 'VALID'
            }
          }
        },
        inactive: {
          url: '/api-cost/api-ql/CostMarketRawMaterial/updateStatus',
          tips: '请勾选处理状态为“生效”的数据，进行失效操作',
          status: ['VALID'],
          title: this.$t('marketBudget.disableCurrent'),
          method: 'POST',
          action: 'updateStatus',
          payload: {
            'filter': {
              'marketRawMaterialId': {
                'in': this.currentRow.map(item => item.marketRawMaterialId)
              }
            },
            'data': {
              'status': 'INVALID'
            }
          }
        },
        delete: {
          url: '/api-cost/api-ql/CostMarketRawMaterial/batchDelete',
          tips: '请勾选处理状态为“拟定”的数据，进行删除操作',
          status: ['DRAFT'],
          title: this.$t('common.confirmDelete'),
          method: 'POST',
          action: 'batchDelete',
          payload: this.currentRow.map(item => { return { 'marketRawMaterialId': item.marketRawMaterialId } })
        }
      }
      if (obj[type]) {
        const { url, tips, status, title, method, payload, action } = obj[type]
        const flag = this.currentRow.some(v => !status.includes(v.status))
        if (flag || this.currentRow.length === 0) {
          this.$message.warning(tips)
          return
        }
        let formData = transformMQL.save('CostMarketRawMaterial', payload, action)
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
        marketType: null,
        rawMaterialCode: null,
        rawMaterialName: null,
        frequency: null,
        status: 'DRAFT',
        __i: Date.now() // 用于删除
      }
      this.$refs[this.gridId].addOneEditableColumn(row)
    },
    // 保存
    save (arr) {
      const list = (arr || this.$refs[this.gridId].tableData)
        .filter(v => v.editable)
        .map(({ marketType, rawMaterialCode, frequency, rawMaterialName, marketRawMaterialId, rawMaterialId, status }) => {
          return {
            marketType,
            rawMaterialCode,
            frequency,
            rawMaterialName,
            rawMaterialId,
            status,
            marketRawMaterialId
          }
        })
      if (list.length === 0) {
        this.$message.warning('请选择数据')
        return
      }
      if (
        list.some(v => !v.marketType || !v.rawMaterialCode || !v.frequency || !v.rawMaterialName)
      ) {
        this.$message.warning('请检查必填项')
        return
      }
      let formData = transformMQL.save('CostMarketRawMaterial', list, 'customSave')
      this.$http({
        url: '/api-cost/api-ql/CostMarketRawMaterial/customSave',
        method: 'POST',
        data: formData,
        loading: true
      }).then(res => {
        this.$message.success(res.message)
        this.getQueryData()
      })
    },
    // 获取联动频次
    // marketTypeCb(row) {
    //   this.$http({
    //     url: '/api-cost/api-ql/CostMarketRawMaterial/getLinkFrequency',
    //     method: 'GET',
    //     params: { marketType: row.marketType },
    //     loading: true,
    //   }).then(res => {
    //     let result = res.data || null
    //     // this.$set(this.$refs.table.realDataSource[scope.$index], 'frequency', result || '')
    //     row.frequency = result
    //   })
    // },
    // 行删除
    deleteRow (row) {
      const ref = this.$refs[this.gridId]
      const i = ref.tableData.findIndex(v => v.__i === row.__i)
      ref.tableData.splice(i, 1)
    }
  }
}
</script>
