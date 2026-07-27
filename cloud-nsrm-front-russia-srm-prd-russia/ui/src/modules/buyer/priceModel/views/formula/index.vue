<template>
  <el-container
    class="flex-container-notab formula_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="queryForm"
        form-label-width="120px"
        @getFormData="getQuerydata"
      />

      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <AuthorityButton
            code="bid:formula:add"
            type="primary"
            @click="showAdd"
          >
            {{ $t("common.add") }}
          </AuthorityButton>
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :row-index-fixed="false"
        :page-size="pageSize"
        :checkbox="false"
        :pre-query-data="queryParam"
        :source="formula.listPage"
        :open-custom-table="true"
      />
    </el-main>
    <srm-dialog
      :visible.sync="addDialog"
      :title="title"
      size="large"
    >
      <BaseForm
        ref="form"
        class="base-form"
        :form-items="formItems"
        :merge-form.sync="mergeForm"
        :inline="false"
        :status-icon="false"
        :show-message="true"
        :disabled="readOnly"
      >
        <template #formulaDetail>
          <el-input
            v-model="combineFormula"
            disabled
          />
        </template>
      </BaseForm>
      <el-button
        style="margin-bottom: 10px;"
        type="primary"
        @click="addRow"
      >
        {{
          $t("common.add")
        }}
      </el-button>
      <BaseTable
        stripe
        :data="formulaData"
        :columns="columns"
        :empty-text="$t('components.noData')"
        border
        @deleteRow="deleteRow"
      >
        <template #type="scope">
          <DictSelect
            v-model="scope.row.pricingFormulaLineType"
            code="PricingFormulaLineType"
            :disabled="readOnly"
          />
        </template>
        <template #value="scope">
          <QuickSearch
            v-if="scope.row.pricingFormulaLineType === 'FIELD'"
            ref="quickSearchTool"
            show-key="essentialFactorName"
            :show-input="scope.row.pricingFormulaLineValue"
            :scope-data="scope"
            name="scc_price_essential_factor"
            :disabled="readOnly"
            @close-quicksearch="getValue"
          />
          <DictSelect
            v-else-if="scope.row.pricingFormulaLineType === 'OP'"
            v-model="scope.row.pricingFormulaLineValue"
            code="OPERATOR"
            :disabled="readOnly"
          />
          <el-input
            v-else
            v-model="scope.row.pricingFormulaLineValue"
            :disabled="readOnly"
          />
        </template>
      </BaseTable>
      <template
        #footer
        class="dialog-footer"
      >
        <el-button @click="addDialog = false">
          {{ $t("common.cancel") }}
        </el-button>
        <el-button
          type="primary"
          @click="saveRow"
        >
          {{ $t("common.confirm") }}
        </el-button>
      </template>
    </srm-dialog>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import BaseForm from 'lib@/components/BaseForm'
import QuickSearch from 'lib@/components/QuickSearch'
import BaseTable from 'lib@/components/BaseTable'
import { formula } from 'modb@/priceModel/api'

import { createDictClass } from '@/library/utils/dict/dict-utils'
const dictClass = createDictClass({ 'OPERATOR': [
  { id: 1, label: '+', value: '+' },
  { id: 2, label: '-', value: '-' },
  { id: 3, label: '*', value: '*' },
  { id: 4, label: '/', value: '/' },
  { id: 5, label: '(', value: '(' },
  { id: 6, label: ')', value: ')' }
] })

export default {
  name: 'PriceFormula',
  components: {
    TableView,
    BaseForm,
    QuickSearch,
    BaseTable,
    MainHeader,
    FormWrapper
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      formula: formula,
      dictClass: dictClass,
      defaultTableHeader: [],
      name: 'priceFormula',
      tableName: 'priceFormula',
      pageSize: 15,
      gridId: 'list',
      tableHeader: [
        {
          label: () => this.$t('priceFormula.formulaName'),
          prop: 'pricingFormulaName',
          minWidth: 150
        },
        {
          label: () => this.$t('priceFormula.formulaDesc'),
          prop: 'pricingFormulaDesc',
          minWidth: 150
        },
        {
          label: () => this.$t('priceFormula.formulaDetail'),
          prop: 'pricingFormulaValue',
          minWidth: 150
        },
        {
          label: () => this.$t('priceFormula.formulaStatus'),
          prop: 'pricingFormulaStatus',
          dataType: 'dict',
          code: 'StuffStatus',
          minWidth: 120
        },
        {
          label: () => this.$t('priceFormula.isSeaFoodFormula'), // 是否海鲜价
          prop: 'isSeaFoodFormula',
          formattor: val => {
            return val === 'Y' ? this.$t('common.yes') : this.$t('common.no')
          },
          minWidth: 100
        },
        {
          label: () => this.$t('priceFormula.createdBy'),
          prop: 'createdUserName', // createdBy
          minWidth: 100
        },
        {
          prop: 'creationDate',
          label: () => this.$t('priceFormula.creationDate'),
          minWidth: 150
        },
        {
          prop: 'operation',
          label: () => this.$t('common.operation'), // 操作
          showType: 'buttons',
          btnStyle: 'text',
          fixed: 'right',
          width: 130,
          buttons: [
            {
              callback: (row, socpe) => this.edit(row, socpe),
              // code: "pr:requirementApply:edit",
              show: row => ['DRAFT'].includes(row.pricingFormulaStatus),
              formattor: () => {
                return this.$t('common.edit')
              }
            },
            {
              callback: (row, socpe) => this.effect(row, socpe),
              // code: "pr:requirementApply:edit",
              show: row =>
                ['DRAFT', 'INVAILD'].includes(row.pricingFormulaStatus),
              formattor: () => {
                return this.$t('priceModel.costElement.effect')
              }
            },
            {
              callback: (row, socpe) => this.failure(row, socpe),
              // code: "pr:requirementApply:edit",
              show: row => row.pricingFormulaStatus === 'ACTIVE',
              formattor: () => {
                return this.$t('priceModel.costElement.failure')
              }
            },
            {
              callback: row => this.deleteItem(row),
              // code: "pr:requirementApply:edit",
              show: row => row.pricingFormulaStatus === 'DRAFT',
              formattor: () => {
                return this.$t('common.delete')
              }
            }
          ]
        }
      ],
      formulaData: [],
      tableData: [],
      queryForm: [
        {
          label: () => this.$t('priceFormula.formulaName'),
          prop: 'pricingFormulaName'
        },
        {
          label: () => this.$t('priceFormula.formulaDesc'),
          prop: 'pricingFormulaDesc'
        },
        {
          prop: 'pricingFormulaStatus',
          label: () => this.$t('priceFormula.formulaStatus'),
          type: 'dict',
          code: 'StuffStatus'
        }
      ],
      mergeForm: {},
      columns: [
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: t => t.$t('priceFormula.type'),
            prop: 'pricingFormulaLineType'
          },
          slot: 'type'
        },
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: t => t.$t('priceFormula.value'),
            prop: 'pricingFormulaLineValue'
          },
          slot: 'value'
        },
        // {
        //   attrs: {
        //     align: "center",
        //     minWidth: "100",
        //     label: t => t.$t("priceFormula.creationDate"),
        //     prop: "creationDate"
        //   }
        // },
        {
          attrs: {
            align: 'center',
            label: () => this.$t('common.operation'), // 操作
            fixed: 'right',
            width: 80
          },
          operations: [
            {
              key: 'deleteRow',
              event: 'deleteRow',
              name: this.$t('common.delete'),
              show: () => !this.readOnly,
              attrs: { type: 'text' }
            }
          ]
        }
      ],
      formItems: [
        {
          itemAttrs: {
            label: this.$t('priceFormula.formulaName')
          },
          uiAttrs: {
            key: 'pricingFormulaName',
            rules: [
              {
                required: true,
                message: this.$t('common.pleaseInput'),
                type: 'string'
              }
            ]
          }, // 请输入
          listeners: {
            change: value => {
              if (value && !this.mergeForm.pricingFormulaDesc) {
                this.mergeForm.pricingFormulaDesc = value
              }
            }
          }
        },
        {
          itemAttrs: {
            label: this.$t('priceFormula.formulaDesc')
          },
          uiAttrs: {
            key: 'pricingFormulaDesc'
          }
        },
        {
          itemAttrs: {
            label: this.$t('priceFormula.formulaDetail'),
            span: 24
          },
          uiAttrs: {
            key: 'pricingFormulaValue'
          },
          slot: 'formulaDetail'
        },
        {
          itemAttrs: {
            label: () => this.$t('priceFormula.isSeaFoodFormula') // 是否海鲜价
          },
          tag: 'checkbox',
          uiAttrs: {
            key: 'isSeaFoodFormula'
          }
        }
      ],
      mode: 'add',
      addDialog: false,
      queryParam: {}
    }
  },
  computed: {
    combineFormula () {
      const sort = ['pricingFormulaLineValue']
      const result = this.formulaData.reduce((str, item) => {
        const res = sort.map(key => item[key] || '').filter(i => i)
        return str.concat(res)
      }, [])
      return result.join(' ')
    },
    readOnly () {
      return this.mode === 'readOnly'
    },
    title () {
      const map = {
        add: this.$t('common.add'),
        edit: this.$t('common.edit')
      }
      return map[this.mode]
    }
  },
  updated () {
    this.defaultTableHeader = this.tableHeader
  },
  mounted () {
    this.getQuerydata()
  },
  activated () {
    this.$refs[this.gridId].doLayout()
  },
  methods: {
    getValue (value, scope) {
      console.log('feild: ', value)
      scope.row.pricingFormulaLineValue = value.essentialFactorName || ''
      scope.row.essentialFactorId = value.essentialFactorId || ''
    },
    addRow () {
      this.formulaData.push({
        pricingFormulaLineType: '',
        pricingFormulaLineValue: ''
      })
    },
    deleteRow (scope) {
      this.formulaData.splice(scope.$index, 1)
    },
    saveRow () {
      // console.log(this.mergeForm);
      const data = {
        ...this.mergeForm,
        pricingFormulaValue: this.combineFormula,
        lineDto: this.formulaData
      }
      // debugger
      data.isSeaFoodFormula = data.isSeaFoodFormula === true ? 'Y' : 'N'
      if (this.mode === 'add') {
        formula.add(data).then(res => {
          this.addDialog = false
          this.$message.success(res.message)
          this.getQuerydata()
        })
      }
      if (this.mode === 'edit') {
        formula.save(data).then(res => {
          this.addDialog = false
          this.$message.success(res.message)
          this.getQuerydata()
        })
      }
    },
    showAdd () {
      this.mode = 'add'
      Object.keys(this.mergeForm).forEach(key => {
        this.mergeForm[key] = null
      })
      this.pricingFormulaValue = null
      this.formulaData = []
      this.addDialog = true
    },
    effect ({ pricingFormulaHeaderId }) {
      formula.get(pricingFormulaHeaderId).then(({ data }) => {
        formula.active(data).then(res => {
          this.$message.success(res.message)
          this.getQuerydata()
        })
      })
    },
    deleteItem ({ pricingFormulaHeaderId }) {
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          formula.deleteItem(pricingFormulaHeaderId).then(res => {
            this.$message.success(res.message)
            this.getQuerydata()
          })
        })
        .catch(() => {})
    },
    failure ({ pricingFormulaHeaderId }) {
      formula.inActive(pricingFormulaHeaderId).then(res => {
        this.$message.success(res.message)
        this.getQuerydata()
      })
    },
    edit (row, scope) {
      this.mode = 'edit'
      this.mergeForm = row
      formula.get(row.pricingFormulaHeaderId).then(({ data }) => {
        const { lineDto, ...rest } = data
        this.mergeForm = { ...rest }
        this.mergeForm.isSeaFoodFormula =
          this.mergeForm.isSeaFoodFormula === 'Y'
        this.formulaData = lineDto
        this.addDialog = true
      })
      // this.$set(this, "mergeForm", row);
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
.formula_wrapper {
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
.formula_wrapper .el-table th > .cell {
  display: flex;
  justify-content: center;
}
</style>
