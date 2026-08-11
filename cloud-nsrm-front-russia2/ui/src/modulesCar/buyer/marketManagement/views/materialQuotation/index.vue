<template>
  <el-container class="flex-container toolinginfo_list_wrapper" direction="vertical">
    <el-main>
      <FormWrapper ref="formWrapper" :formArray="filterConfig" @getFormData="getQuerydata" />
      <MainHeader :lSpan="22" :rSpan="2">
        <template slot="left">
          <!-- <AuthorityButton type="primary" @click="importHandle">批量导入</AuthorityButton> -->
          <MImport
            ref="import1"
            style="display: inline-block;margin: 0"
            :title="$t('common.import')"
            code="marketLink:materialQuotation:import"
            upLoadUrl="/api-cost/costRawMarketPrice/importExcel"
            :extraData="{}"
            :extraPostData="extraPostData"
            @downloadTemplate="downloadItemTemplate"
            @handleSuccess="handleSuccess"
          />
          <AuthorityButton type="primary" code="marketLink:materialQuotation:add" @click="add">
            {{ $t('common.add') }}
          </AuthorityButton>
          <AuthorityButton type="primary" code="marketLink:materialQuotation:save" @click="save">
            {{ $t('common.save') }}
          </AuthorityButton>
          <AuthorityButton
            type="primary"
            code="marketLink:materialQuotation:valid"
            @click="handlerFn('active')"
          >
            {{ $t('common.active') }}
          </AuthorityButton>
          <AuthorityButton
            type="primary"
            code="marketLink:materialQuotation:invalid"
            @click="handlerFn('inactive')"
          >
            {{ $t('common.inactive') }}
          </AuthorityButton>
          <AuthorityButton
            type="primary"
            code="marketLink:materialQuotation:delete"
            @click="handlerFn('delete')"
          >
            {{ $t('common.delete') }}
          </AuthorityButton>
          <ExportExcel
            code="marketLink:materialQuotation:export"
            page-url="/api-cost/api-ql/CostRawMarketPrice/customQuery"
            :filter-params="computedQueryParam"
            :table-header="tableHeader"
            :dict-codes="dictCodes"
            timeout="1000000"
            export-mode="front"
          />
        </template>
      </MainHeader>

      <TableView
        ref="list"
        :table-header="tableHeader"
        :checkChange="handleCurrentChange"
        :page-size="pageSize"
        :preQueryData="queryParam"
        :adeptMeiQl="true"
        :openCustomTable="true"
        :checkbox="true"
        url="/api-cost/api-ql/CostRawMarketPrice/customQuery"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MainHeader from 'lib@/components/Table/MainHeader'
import { parseTime, formatTimeToDate } from '@/utils'
import { downloadFileLink, downloadFileLinkByPost } from 'lib@/utils/file'
import { sysPrefix } from '@/config/ipConfig'
import MImport from 'lib@/components/import'
import { transformMQL } from '@/library/utils/util'

import ExportExcel from 'lib@/components/export-excel'
export default {
  name: 'MaterialQuotation',
  components: {
    TableView,
    FormWrapper,
    MainHeader,
    MImport,
    ExportExcel
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      dictCodes: {},
      isVENDOR: false,
      pageSize: 15,
      filterConfig: [
        {
          prop: 'rawMaterialCode',
          label: this.$t('marketBudget.materialCode'),
          queryOperator: 'contains'
        },
        {
          prop: 'rawMaterialName',
          label: this.$t('marketBudget.materialName'),
          queryOperator: 'contains'
        },

        {
          label: this.$t('marketBudget.status'),
          prop: 'status',
          type: 'dict',
          code: 'BASE_INFO_STATUS'
        }
      ],
      queryParam: {},
      tableHeader: [
        {
          label: this.$t('marketBudget.marketStartTime'),
          prop: 'effectiveTime',
          minWidth: 160,
          editable: row => row.editable,
          showType: 'date',
          addStarToColumn: true
          // formattor: val => {
          //   return formatTimeToDate(val, 'Y-M-D')
          // },
        },
        {
          label: this.$t('marketBudget.marketEndTime'),
          prop: 'expiredTime',
          minWidth: 160,
          editable: row => row.editable,
          showType: 'date',
          addStarToColumn: true
          // formattor: val => {
          //   return formatTimeToDate(val, 'Y-M-D')
          // },
        },
        {
          label: this.$t('marketBudget.materialCode'),
          prop: 'rawMaterialCode',
          minWidth: 150,
          editable: row => row.editable,
          addStarToColumn: true,
          showType: 'input'
          // showKey: 'materialCode',
          // name: 'scc_base_material_item',
          // getObj: (e, row) => {
          //   row.rawMaterialCode = e.materialCode
          //   row.rawMaterialName = e.materialName
          //   row.rawMaterialId = e.materialId
          //   row.unit = e.weightUnit
          // },
        },
        {
          label: this.$t('marketBudget.materialName'),
          prop: 'rawMaterialName',
          minWidth: 150,
          editable: row => row.editable,
          addStarToColumn: true,
          showType: 'input'
        },
        {
          label: this.$t('marketBudget.priceSource'),
          prop: 'priceSource', // 此字段待讨论
          minWidth: 150,
          editable: row => row.editable,
          addStarToColumn: true,
          showType: 'input'
        },
        {
          label: this.$t('marketBudget.beforeTaxPrice'),
          prop: 'beforeTaxPrice',
          minWidth: 150,
          editable: row => row.editable,
          addStarToColumn: true,
          showType: 'input'
        },
        {
          label: this.$t('marketBudget.currency'),
          prop: 'currencyType',
          minWidth: 150,
          editable: row => row.editable,
          addStarToColumn: true,
          showType: 'dictSelect',
          code: 'currency',
          formattor: val => this.$getDictLabel('currency', val)
        },
        {
          label: this.$t('marketBudget.exchangeRate'),
          prop: 'exchangeRate',
          minWidth: 150,
          editable: row => row.editable,
          addStarToColumn: true,
          showType: 'input'
        },
        {
          prop: 'unit',
          label: this.$t('marketBudget.unit'),
          minWidth: 150,
          addStarToColumn: true,
          editable: row => row.editable,
          showType: 'dictSelect',
          code: 'unit',
          formattor: val => this.$getDictLabel('unit', val)
        },
        {
          label: this.$t('marketBudget.remark'),
          prop: 'remark',
          editable: row => row.editable,
          showType: 'input',
          minWidth: 150
        },
        {
          label: this.$t('marketBudget.quotationStatus'),
          prop: 'status',
          minWidth: 150,
          formattor: val => this.$getDictLabel('BASE_INFO_STATUS', val)
        },
        {
          label: this.$t('common.creator'),
          prop: 'createdBy',
          minWidth: 150
        },
        {
          label: this.$t('common.creationTime'),
          prop: 'creationDate',
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
              code: 'marketLink:materialQuotation:edit',
              show: row => !row.editable && row.status === 'DRAFT'
            },
            // 删除
            {
              callback: row => this.deleteRow(row),
              formattor: () => '删除',
              show: row => row.editable && !row.rawMarketPriceId
            },
            // 取消行编辑
            {
              callback: row => this.$set(row, 'editable', ''),
              formattor: () => this.$t('common.cancel'),
              show: row => row.editable && row.rawMarketPriceId
            },
            // 保存
            {
              callback: row => this.save([row]),
              formattor: () => '保存',
              show: row => row.editable
            }
          ]
        }
      ],
      extraData: {
        sourceType: 'WEB_APP',
        uploadType: 'FASTDFS',
        fileModular: 'suplier',
        fileFunction: 'accountAccess',
        fileType: 'excel'
      },
      extraPostData: {

      },
      selectArr: []
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
  mounted () {},
  created () {
    this.getQuerydata()
  },
  methods: {
    // 保存
    save (arr) {
      const list = (arr || this.$refs[this.gridId].tableData)
        .filter(v => v.editable)

      if (list.length === 0) {
        this.$message.warning('请选择数据')
        return
      }
      if (
        list.some(v => !v.effectiveTime || !v.expiredTime || !v.rawMaterialCode || !v.rawMaterialName || !v.priceSource || !v.beforeTaxPrice || !v.currencyType || !v.exchangeRate || !v.unit)
      ) {
        this.$message.warning('请检查必填项')
        return
      }
      let formData = transformMQL.save('CostRawMarketPrice', list, 'customSave')
      this.$http({
        url: '/api-cost/api-ql/CostRawMarketPrice/customSave',
        method: 'POST',
        data: formData,
        loading: true
      }).then(res => {
        this.$message.success(res.message)
        this.getQuerydata()
      })
    },
    deleteRow (row) {
      const ref = this.$refs['list']
      const i = ref.tableData.findIndex(v => v.__i === row.__i)
      ref.tableData.splice(i, 1)
    },
    // 新增
    add () {
      const row = {
        editable: true,
        effectiveTime: null,
        expiredTime: null,
        rawMaterialCode: null,
        priceSource: null,
        beforeTaxPrice: null,
        currencyType: null,
        exchangeRate: null,
        remark: null,
        unit: null,
        status: 'DRAFT',
        __i: Date.now() // 用于删除
      }
      this.$refs['list'].addOneEditableColumn(row)
    },
    getQuerydata (params = {}) {
      this.queryParam = transformMQL.listGetData('CostRawMarketPrice', params, 'lastUpdateDate', undefined, 'customQuery', undefined, { status: 'eq' })
      this.$nextTick(() => {
        this.$refs.list.query()
      })
    },

    exportHandle () {
      downloadFileLinkByPost(
        '/api-cost/costRawMarketPrice/template',
        `原材料行情维护-导出${parseTime(new Date())}.xlsx`,
        this.queryParam,
      )
    },

    // active 生效  inactive  失效  delete  删除
    handlerFn (type) {
      const obj = {
        active: {
          url: '/api-cost/api-ql/CostRawMarketPrice/updateStatus',
          tips: '请勾选处理状态为“拟定”或者“失效”的数据，进行生效操作',
          status: ['DRAFT', 'INVALID'],
          title: this.$t('marketBudget.enableCurrent'),
          method: 'POST',
          action: 'updateStatus',
          payload: {
            'filter': {
              'rawMarketPriceId': {
                'in': this.selectArr.map(item => item.rawMarketPriceId)
              }
            },
            'data': {
              'status': 'VALID'
            }
          }
        },
        inactive: {
          url: '/api-cost/api-ql/CostRawMarketPrice/updateStatus',
          tips: '请勾选处理状态为“生效”的数据，进行失效操作',
          status: ['VALID'],
          title: this.$t('marketBudget.disableCurrent'),
          method: 'POST',
          action: 'updateStatus',
          payload: {
            'filter': {
              'rawMarketPriceId': {
                'in': this.selectArr.map(item => item.rawMarketPriceId)
              }
            },
            'data': {
              'status': 'INVALID'
            }
          }
        },
        delete: {
          url: '/api-cost/api-ql/CostRawMarketPrice/batchDelete',
          tips: '请勾选处理状态为“拟定”的数据，进行删除操作',
          status: ['DRAFT'],
          title: this.$t('common.confirmDelete'),
          method: 'POST',
          action: 'batchDelete',
          payload: this.selectArr.map(item => { return { 'rawMarketPriceId': item.rawMarketPriceId } })
        }
      }
      if (obj[type]) {
        const { url, tips, status, title, method, payload, action } = obj[type]
        const flag = this.selectArr.some(v => !status.includes(v.status))
        if (flag || this.selectArr.length === 0) {
          this.$message.warning(tips)
          return
        }
        this.$confirm(title, {
          confirmButtonText: this.$t('common.confirm'),
          cancelButtonText: this.$t('common.cancel'),
          type: 'warning'
        }).then(() => {
          let formData = transformMQL.save('CostRawMarketPrice', payload, action)
          this.$http({
            url,
            method,
            data: formData,
            loading: true
          }).then(res => {
            if (res.code === '0') {
              this.$message.success(res.message)
              this.getQuerydata()
            }
          })
        })
      }
    },

    handleCurrentChange (e) {
      this.selectArr = e
    },
    handleSuccess (res) {
      this.$message.success('导入成功')
      this.getQuerydata(this.queryParam)
    },
    downloadItemTemplate () {
      downloadFileLink(
        '/api-cost/costRawMarketPrice/template',
        '原材料行情维护导入模板.xlsx',
      ).catch(() => {
        this.$message.error('下载失败')
      })
    }
  }
}
</script>
