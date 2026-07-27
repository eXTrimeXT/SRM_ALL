<template>
  <el-container
    class="flex-container-notab basicPrice_wrapper"
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
            code="bid:basePrice:add"
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
        :source="materialPrice.listPage"
        :open-custom-table="true"
        :transform-data="transformData"
      >
        <template #taxRate="{scope}">
          <DicctSelect
            v-if="scope.row.editable"
            v-model="scope.row.taxKey"
            code="tax"
            @change="val => taxRateChangeHandel(val, scope.row)"
          />
          <span v-else>{{ $getDictLabel("tax", scope.row.taxKey) }}</span>
        </template>
      </TableView>
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
        :wrapper-col="{
          span: 8,
          gutter: 30
        }"
      >
        <template #baseMaterialName>
          <QuickSearch
            ref="baseMaterialName"
            show-key="baseMaterialName"
            :show-input="mergeForm.baseMaterialName"
            name="scc_price_material"
            :disabled="readOnly"
            @close-quicksearch="getValue"
          />
        </template>
        <template #taxRate>
          <DicctSelect
            v-model="mergeForm.taxKey"
            code="tax"
            @change="val => taxRateChangeHandel(val, mergeForm)"
          />
        </template>
      </BaseForm>
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
import { createDictClass } from '@/library/utils/dict/dict-utils'
import { materialPrice } from 'modb@/priceModel/api'
const dictClass = createDictClass({ 'tax': [] })

export default {
  name: 'MaterialPrice',
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
      materialPrice: materialPrice,
      dictClass: dictClass,
      defaultTableHeader: [],
      name: 'materialPrice',
      tableName: 'materialPrice',
      pageSize: 15,
      gridId: 'list',
      tableHeader: [
        {
          label: () => this.$t('materialPrice.baseMaterialName'),
          prop: 'baseMaterialName',
          width: 150
        },
        {
          label: () => this.$t('materialPrice.baseMaterialCode'),
          prop: 'baseMaterialCode',
          width: 150
        },
        {
          label: () => this.$t('materialPrice.baseMaterialPrice'),
          prop: 'baseMaterialPrice',
          showType: 'input',
          editable: row => row.editable,
          width: 150
        }, {
          slot: 'taxRate',
          label: () => this.$t('materialPrice.taxRate'),
          prop: 'taxRate',
          showType: 'slot',
          width: 150
        }, {
          label: () => this.$t('materialPrice.notaxPrice'),
          width: 150,
          formattor: (val, row) => {
            return row.taxRate ? (row.baseMaterialPrice / (1 + row.taxRate / 100)).toFixed(4) : row.baseMaterialPrice
          }
        },
        {
          label: () => this.$t('materialPrice.priceType'),
          prop: 'baseMaterialPriceType',
          formattor: val => this.$getDictLabel('PriceType', val),
          width: 150
        },
        {
          label: () => this.$t('materialPrice.dataSource'),
          prop: 'priceFrom',
          width: 150
        },
        {
          label: () => this.$t('materialPrice.effectiveDateFrom'),
          prop: 'activeDateFrom',
          width: 150,
          formattor: val => this.$parseTime(val, '{y}-{m}-{d}')
        },
        {
          label: () => this.$t('materialPrice.effectiveDateTo'),
          prop: 'activeDateTo',
          width: 150,
          formattor: val => this.$parseTime(val, '{y}-{m}-{d}')
        },
        {
          label: () => this.$t('materialPrice.dataCollectStartDate'),
          prop: 'collectStartDate',
          width: 150,
          dataType: 'dateTime'
        },
        {
          label: () => this.$t('materialPrice.dataCollectEndDate'),
          prop: 'collectEndDate',
          width: 150,
          dataType: 'dateTime'
        },
        {
          label: () => this.$t('materialPrice.status'),
          prop: 'baseMaterialPriceStatus',
          formattor: val => this.$getDictLabel('StuffStatus', val),
          width: 150
        },
        {
          label: () => this.$t('materialPrice.unit'),
          prop: 'baseMaterialUnit',
          formattor: val => this.$getDictLabel('unit', val),
          width: 150
        },
        {
          label: () => this.$t('materialPrice.currency'),
          prop: 'currencyType',
          formattor: val => this.$getDictLabel('currency', val),
          width: 150
        },
        {
          label: () => this.$t('materialPrice.createdBy'),
          prop: 'createdUserName', // createdBy
          width: 150
        },
        {
          prop: 'creationDate',
          label: () => this.$t('materialPrice.creationDate'),
          width: 150,
          dataType: 'dateTime'
        },
        {
          prop: 'operation',
          label: () => this.$t('common.operation'), // 操作
          showType: 'buttons',
          btnStyle: 'text',
          fixed: 'right',
          width: 160,
          buttons: [
            {
              callback: (row, socpe) => this.editPrice(row, socpe),
              // code: "pr:requirementApply:edit",
              show: row =>
                !row.editable && row.baseMaterialPriceStatus === 'DRAFT',
              formattor: () => {
                return this.$t('materialPrice.editPrice')
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
              show: row =>
                ['DRAFT', 'INVAILD'].includes(row.baseMaterialPriceStatus),
              formattor: () => {
                return this.$t('priceModel.costElement.effect')
              }
            },
            {
              callback: (row, socpe) => this.failure(row, socpe),
              // code: "pr:requirementApply:edit",
              show: row => row.baseMaterialPriceStatus === 'ACTIVE',
              formattor: () => {
                return this.$t('priceModel.costElement.failure')
              }
            },
            {
              callback: (row, socpe) => this.drop(row, socpe),
              // code: "pr:requirementApply:edit",
              show: row => row.baseMaterialPriceStatus === 'INVAILD',
              formattor: () => {
                return this.$t('common.abandon')
              }
            },
            {
              callback: row => this.deleteItem(row),
              // code: "pr:requirementApply:edit",
              show: row => row.baseMaterialPriceStatus === 'DRAFT',
              formattor: () => {
                return this.$t('common.delete')
              }
            }
          ]
        }
      ],
      tableData: [],
      queryForm: [
        {
          label: () => this.$t('materialPrice.baseMaterialName'),
          prop: 'baseMaterialName'
        },
        {
          label: () => this.$t('materialPrice.baseMaterialCode'),
          prop: 'baseMaterialCode'
        },
        {
          label: () => this.$t('materialPrice.dataSource'),
          prop: 'dataSource'
        },
        {
          label: () => this.$t('materialPrice.priceType'),
          prop: 'baseMaterialPriceType',
          type: 'select',
          options: () => this.PriceType
        },
        {
          prop: 'baseMaterialPriceStatus',
          label: () => this.$t('materialPrice.status'),
          type: 'select',
          options: () => this.StuffStatus
        }
      ],
      mergeForm: {},
      formItems: [
        {
          itemAttrs: {
            label: this.$t('materialPrice.baseMaterialName'),
            rules: [
              {
                required: true,
                message: this.$t('common.pleaseInput'),
                type: 'string'
              }
            ] // 请输入
          },
          uiAttrs: {
            key: 'baseMaterialName'
          },
          slot: 'baseMaterialName'
        },
        {
          itemAttrs: {
            label: this.$t('materialPrice.baseMaterialCode')
          },
          uiAttrs: {
            key: 'baseMaterialCode',
            disabled: true
          }
        },
        {
          itemAttrs: {
            label: this.$t('materialPrice.baseMaterialPrice'),
            rules: [{ required: true, message: this.$t('common.pleaseInput') }]
          },
          uiAttrs: {
            key: 'baseMaterialPrice'
          }
        },
        {
          itemAttrs: {
            label: this.$t('materialPrice.taxRate'),
            rules: [
              {
                required: true,
                message: this.$t('common.pleaseInput'),
                type: 'string'
              }
            ] // 请输入
          },
          uiAttrs: {
            key: 'taxRate'
          },
          slot: 'taxRate'
        },
        {
          itemAttrs: {
            label: this.$t('materialPrice.dataSource'),
            rules: [{ required: true, message: this.$t('common.pleaseInput') }]
          },
          uiAttrs: {
            key: 'priceFrom'
          }
        },
        {
          tag: 'select',
          itemAttrs: {
            label: this.$t('materialPrice.status')
          },
          uiAttrs: {
            key: 'baseMaterialPriceStatus',
            options: () => this.StuffStatus,
            disabled: true
          }
        },
        // {
        //   itemAttrs: {
        //     label: this.$t("materialPrice.priceType"),
        //     rules: [{ required: true, message: "请填写", type: "string" }]
        //   },
        //   uiAttrs: {
        //     key: "baseMaterialPriceType",
        //     options: () => this.PriceType
        //   },
        //   tag: "select"
        // },
        {
          tag: 'date',
          itemAttrs: {
            label: this.$t('materialPrice.effectiveDateFrom'),
            rules: [
              {
                required: true,
                message: this.$t('common.pleaseInput')
              }
            ]
          },
          uiAttrs: {
            key: 'activeDateFrom'
          }
        },
        {
          tag: 'date',
          itemAttrs: {
            label: this.$t('materialPrice.effectiveDateTo'),
            rules: [
              {
                required: true,
                message: this.$t('common.pleaseInput')
              }
            ]
          },
          uiAttrs: {
            key: 'activeDateTo'
          }
        },
        {
          tag: 'dictSelect',
          itemAttrs: {
            label: this.$t('materialPrice.currency'),
            rules: [
              {
                required: true,
                message: this.$t('common.pleaseInput'),
                type: 'string'
              }
            ]
          },
          uiAttrs: {
            key: 'currencyType',
            code: 'currency'
          }
        }
      ],
      mode: 'add',
      addDialog: false,
      queryParam: {}
    }
  },
  computed: {
    readOnly () {
      return this.mode === 'readOnly'
    },
    title () {
      const map = {
        add: this.$t('common.add'),
        edit: this.$t('common.edit')
      }
      return map[this.mode]
    },
    EssentialFactorFromType () {
      return store.EssentialFactorFromType
    },
    PriceType () {
      return store.PriceType
    },
    StuffStatus () {
      return store.StuffStatus
    },
    currency () {
      return store.currency
    },
    unit () {
      return store.unit
    }
  },
  updated () {
    this.defaultTableHeader = this.tableHeader
  },
  mounted () {
    this.getQuerydata()
  },
  methods: {
    transformData (data) {
      const list = data.data.list
      data.data.list = list.map(item => ({ ...item, editable: false }))
      return data
    },
    getValue (value) {
      this.mergeForm.baseMaterialName = value.baseMaterialName || ''
      this.mergeForm.baseMaterialCode = value.baseMaterialCode || ''
      this.mergeForm.baseMaterialUnit = value.baseMaterialUnit || ''
    },
    saveRow () {
      this.$refs['form'].validate().then(() => {
        if (this.mode === 'add') {
          materialPrice.add(this.mergeForm).then(res => {
            this.addDialog = false
            this.$message.success(res.message)
            this.getQuerydata()
          })
        }
        if (this.mode === 'edit') {
          materialPrice.save(this.mergeForm).then(res => {
            this.addDialog = false
            this.$message.success(res.message)
            this.getQuerydata()
          })
        }
      }).catch(() => {
        console.log('error submit!!')
        return false
      })
    },
    showAdd () {
      this.mode = 'add'
      Object.keys(this.mergeForm).forEach(key => {
        this.mergeForm[key] = null
      })
      this.mergeForm.baseMaterialPriceType = 'DAY_PRICE'
      this.mergeForm.currencyType = 'CNY'
      this.addDialog = true
    },
    effect (row) {
      materialPrice.active(row).then(res => {
        this.$message.success(res.message)
        this.getQuerydata()
      })
    },
    deleteItem ({ baseMaterialPriceId }) {
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          materialPrice.deleteItem(baseMaterialPriceId).then(res => {
            this.$message.success(res.message)
            this.getQuerydata()
          })
        })
        .catch(() => {})
    },
    drop ({ baseMaterialPriceId }) {
      materialPrice.drop(baseMaterialPriceId).then(res => {
        this.$message.success(res.message)
        this.getQuerydata()
      })
    },
    failure ({ baseMaterialPriceId }) {
      materialPrice.inActive(baseMaterialPriceId).then(res => {
        this.$message.success(res.message)
        this.getQuerydata()
      })
    },
    editPrice (row, scope) {
      scope.row.editable = true
    },
    edit (row, scope) {
      this.mode = 'edit'
      this.mergeForm = row
      // this.$set(this, "mergeForm", row);
      this.addDialog = true
    },
    cancelEdit (row, scope) {
      scope.row.editable = false
      this.getQuerydata()
    },
    save (row, scope) {
      materialPrice.save(row).then(res => {
        this.addDialog = false
        this.$message.success(res.message)
        this.getQuerydata()
      })
    },
    getQuerydata (v) {
      this.queryParam = v
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    taxRateChangeHandel (val, row) {
      const dRowObj = this.dictClass.getDictDetail('tax', val)
      row.taxRate = dRowObj ? dRowObj.key : ''
    }
  }
}
</script>
<style scoped lang="scss">
.basic_price_wrapper {
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
.basic_price_wrapper .el-table th > .cell {
  display: flex;
  justify-content: center;
}
</style>
