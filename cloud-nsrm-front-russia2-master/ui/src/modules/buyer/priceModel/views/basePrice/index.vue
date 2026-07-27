<template>
  <el-container
    class="flex-container base_price_list_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="queryForm"
        form-label-width="120px"
        @getFormData="getQuerydata"
      />
      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :row-index-fixed="false"
        :page-size="pageSize"
        :checkbox="false"
        :pre-query-data="queryParam"
        :source="basePrice.listPage"
        :open-custom-table="true"
        :transform-data="transformData"
      />
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import dayjs from 'dayjs'
import { basePrice } from 'modb@/priceModel/api'

export default {
  name: 'BasePriceList',
  components: {
    TableView,
    FormWrapper
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    // getLabel(name, value)
    // getSource(name)
    return {
      basePrice: basePrice,
      defaultTableHeader: [],
      name: 'basePriceList',
      tableName: 'basePriceList',
      pageSize: 15,
      gridId: 'list',
      tableHeader: [],
      tableData: [],
      queryForm: [],
      queryParam: {}
    }
  },
  updated () {
    this.defaultTableHeader = this.tableHeader
  },
  created () {
    this.tableHeader = [
      {
        label: () => this.$t('priceModel.costElement.combinationCode'),
        prop: 'combinationCode',
        minWidth: 150
      },
      {
        label: () =>
          this.$t('priceModel.costElement.keyAttributeCombination'),
        prop: 'keyAttributeCombination',
        minWidth: 150
      },
      {
        label: () => this.$t('priceModel.costElement.priceVersion'),
        prop: 'priceVersion',
        minWidth: 100
      },
      {
        label: () =>
          this.$t('priceModel.costElement.attributeValueCombination'),
        prop: 'attributeValueCombination',
        minWidth: 150
      },
      {
        label: () => this.$t('priceModel.costElement.elementName'),
        prop: 'elementName',
        minWidth: 150
      },
      {
        prop: 'elementCode',
        label: () => this.$t('priceModel.costElement.elementCode'),
        minWidth: 150
      },
      {
        label: () => this.$t('priceModel.costElement.elementType'),
        formattor: val => this.$getDictLabel('COST_ELEMENT_TYPE', val),
        prop: 'elementType',
        minWidth: 130
      },
      {
        label: () => this.$t('priceModel.costElement.basePrice'),
        prop: 'basePrice',
        showType: 'input',
        editable: row => row.editable,
        minWidth: 150
      },
      {
        label: () => this.$t('priceModel.costElement.clearCurrency'),
        prop: 'clearCurrency',
        showType: 'dictSelect',
        code: 'currency',
        formattor: val => this.$getDictLabel('currency', val),
        editable: row => row.editable,
        minWidth: 150
      },
      {
        label: () => this.$t('priceModel.costElement.startDate'),
        prop: 'startDate',
        showType: 'date',
        formatter: val => (this.$parseTime(val) || null),
        editable: row => row.editable,
        minWidth: 150
      },
      {
        label: () => this.$t('priceModel.costElement.endDate'),
        prop: 'endDate',
        showType: 'date',
        formatter: val => (this.$parseTime(val) || null),
        editable: row => row.editable,
        minWidth: 150,
        pickerOptions: ({ row }) => {
          return {
            disabledDate: time => {
              return row.startDate ? time < new Date(row.startDate) : time < new Date()
            }
          }
        }
      },
      {
        label: () => this.$t('priceModel.costElement.status'),
        prop: 'status',
        formattor: val => this.$getDictLabel('COST_ELEMENT_STATUS', val),
        minWidth: 100
      },
      {
        label: () => this.$t('priceModel.costElement.createdBy'),
        prop: 'createdUserName', // createdBy
        minWidth: 130
      },
      {
        label: () => this.$t('priceModel.costElement.lastUpdatedBy'),
        prop: 'lastUpdatedUserName', // lastUpdatedBy
        minWidth: 130
      },
      {
        label: () => this.$t('priceModel.costElement.lastUpdateDate'),
        prop: 'lastUpdateDate',
        minWidth: 150,
        dataType: 'dateTime'
      },
      {
        label: () => this.$t('priceModel.costElement.creationDate'),
        prop: 'creationDate',
        minWidth: 150,
        dataType: 'dateTime'
      },
      {
        prop: 'operation',
        label: () => this.$t('common.operation'), // 操作
        showType: 'buttons',
        btnStyle: 'text',
        fixed: 'right',
        minWidth: 130,
        buttons: [
          {
            callback: (row, socpe) => this.edit(row, socpe),
            // code: "pr:requirementApply:edit",
            show: row => !row.editable,
            formattor: () => {
              return this.$t('common.edit')
            }
          },
          {
            callback: (row, socpe) => this.save(row, socpe),
            // code: "pr:requirementApply:edit",
            show: row => row.editable,
            formattor: () => {
              return this.$t('common.save')
            }
          },
          {
            callback: (row, socpe) => this.cancelEdit(row, socpe),
            // code: "pr:requirementApply:edit",
            show: row => row.editable,
            formattor: () => {
              return this.$t('common.cancel')
            }
          },
          {
            callback: (row, socpe) => this.effect(row, socpe),
            // code: "pr:requirementApply:edit",
            show: row => row.status === 'INVALID',
            formattor: () => {
              return this.$t('priceModel.costElement.effect')
            }
          },
          {
            callback: (row, socpe) => this.failure(row, socpe),
            // code: "pr:requirementApply:edit",
            show: row => row.status === 'VALID',
            formattor: () => {
              return this.$t('priceModel.costElement.failure')
            }
          }
        ]
      }
    ]
    this.queryForm = [
      {
        prop: 'combinationCode',
        label: () => this.$t('priceModel.costElement.combinationCode')
      },
      {
        prop: 'elementCode',
        label: () => this.$t('priceModel.costElement.elementCode')
      },
      {
        prop: 'elementName',
        label: () => this.$t('priceModel.costElement.elementName')
      },
      {
        prop: 'elementType',
        label: () => this.$t('priceModel.costElement.elementType'),
        type: 'dict',
        code: 'COST_ELEMENT_TYPE'
      },
      {
        prop: 'status',
        label: () => this.$t('priceModel.costElement.status'),
        type: 'dict',
        code: 'BASE_PRICE_STATUS'
      },
      {// 创建人
        label: this.$t('dataConfMod.createdBy'),
        prop: 'createdId',
        // slot: "createdId",
        type: 'quicksearch',
        propKey: 'userId',
        showKey: 'nickname',
        name: 'scc_rbac_user_display'
      },
      {
        prop: 'startDate',
        type: 'date',
        label: () => this.$t('priceModel.costElement.startDate')
      },
      {
        prop: 'endDate',
        type: 'date',
        label: () => this.$t('priceModel.costElement.endDate')
      }
    ]
  },
  mounted () {
    this.getQuerydata()
    this.$nextTick(() => {
      this.$refs[this.gridId].doLayout()
    })
  },
  methods: {
    transformData (data) {
      const list = data.data.list
      data.data.list = list.map(item => ({ ...item, editable: false }))
      return data
    },
    effect ({ basePriceId }) {
      basePrice.takeEffect(basePriceId).then(res => {
        this.$message.success(res.message)
        this.getQuerydata()
      })
    },
    failure ({ basePriceId }) {
      basePrice.failure(basePriceId).then(res => {
        this.$message.success(res.message)
        this.getQuerydata()
      })
    },
    edit (row, scope) {
      scope.row.editable = true
    },
    save (row) {
      // 校验失效日期需要大于生效日期
      if (row.startDate && row.endDate) {
        if (dayjs(row.startDate).valueOf() > dayjs(row.endDate).valueOf()) {
          this.$message.warning(this.$t('dataConfMod.expirationMustGreater'))
          return
        }
      }
      basePrice.modify(row).then(res => {
        this.$message.success(res.message)
        this.getQuerydata()
      })
    },
    cancelEdit (row, scope) {
      scope.row.editable = false
      this.getQuerydata()
    },
    getQuerydata (v) {
      this.queryParam = v
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    }
  }
}
</script>
<style scoped lang="scss">
.base_price_list_wrapper {
  .order-uploader {
    display: inline-block;
    margin: 0 10px;
  }
  .block {
    display: flex;
    justify-content: center;
  }
}
</style>
<style>
.base_price_list_wrapper .el-table th > .cell {
  display: flex;
  justify-content: center;
}
</style>
