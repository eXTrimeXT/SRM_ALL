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
        row-index-fixed
        :page-size="pageSize"
        :checkbox="false"
        :pre-query-data="queryParam"
        :source="bp.listPage"
        open-custom-table
        :transform-data="transformData"
      />
    </el-main>

    <!--新增弹窗-->
    <srm-dialog
      v-if="addDialog"
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
        :wrapper-col="{ span: 8, gutter: 30 }"
      >
        <template #baseMaterialName>
          <QuickSearch
            ref="baseMaterialName"
            show-key="baseMaterialName"
            :show-input="mergeForm.baseMaterialName"
            name="ceea_bid_base_material"
            :disabled="readOnly"
            @close-quicksearch="getValue"
          />
        </template>
        <template #baseMaterialPriceStatus>
          <DictSelect
            v-model="mergeForm.baseMaterialPriceStatus"
            disabled
            code="StuffStatus"
          />
        </template>
        <template #baseMaterialPriceType>
          <DictSelect
            v-model="mergeForm.baseMaterialPriceType"
            code="PriceType"
          />
        </template>
        <template #activeDateTo>
          <el-date-picker
            v-model="mergeForm.activeDateTo"
            type="date"
            :format="$formatDatePicker"
            value-format="yyyy-MM-dd"
            :picker-options="activeDateToPickerOptions"
            :placeholder="$t('bidMod.datePicker')"
          />
        </template>

        <template #baseMaterialUnit>
          <DictSelect
            v-model="mergeForm.baseMaterialUnit"
            code="unit"
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
import { createDictClass } from 'lib@/utils/dict/dict-utils'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import BaseForm from 'lib@/components/BaseForm'
import QuickSearch from 'lib@/components/QuickSearch'
import { bp } from 'modb@/biddingManagementBuyer/api'

export default {
  name: 'BasicPrice',
  components: {
    TableView,
    BaseForm,
    QuickSearch,
    MainHeader,
    FormWrapper
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      bp: bp,
      dictClass: createDictClass({ 'currency': [] }),
      defaultTableHeader: [],
      name: 'basicPrice',
      tableName: 'basicPrice',
      pageSize: 15,
      gridId: 'list',
      tableHeader: [
        {
          label: () => this.$t('basicPrice.baseMaterialName'),
          prop: 'baseMaterialName',
          width: 150,
          fixed: 'left'
        },
        {
          label: () => this.$t('basicPrice.baseMaterialCode'),
          prop: 'baseMaterialCode',
          width: 150,
          fixed: 'left'
        },
        {
          label: () => this.$t('basicPrice.baseMaterialPrice'),
          prop: 'baseMaterialPrice',
          showType: 'input',
          editable: row => row.editable,
          width: 150,
          fixed: 'left'
        },
        {
          label: () => this.$t('basicPrice.priceType'),
          prop: 'baseMaterialPriceType',
          formattor: val => this.$getDictLabel('PriceType', val),
          width: 150
        },
        {
          label: () => this.$t('basicPrice.dataSource'),
          prop: 'priceFrom',
          width: 150
        },
        {
          label: () => this.$t('basicPrice.effectiveDateFrom'),
          prop: 'activeDateFrom',
          width: 150,
          formattor: val => this.$parseTime(val, '{y}-{m}-{d}')
        },
        {
          label: () => this.$t('basicPrice.effectiveDateTo'),
          prop: 'activeDateTo',
          width: 150,
          formattor: val => this.$parseTime(val, '{y}-{m}-{d}')
        },
        {
          label: () => this.$t('basicPrice.status'),
          prop: 'baseMaterialPriceStatus',
          formattor: val => this.$getDictLabel('StuffStatus', val),
          width: 150
        },
        {
          label: () => this.$t('basicPrice.unit'),
          prop: 'baseMaterialUnit',
          formattor: val => this.$getDictLabel('unit', val),
          width: 150
        },
        {
          label: () => this.$t('basicPrice.currency'),
          prop: 'currencyType',
          formattor: val => this.$getDictLabel('currency', val),
          width: 150
        },
        {
          label: () => this.$t('basicPrice.createdBy'),
          prop: 'createdUserName', // createdBy
          width: 150
        },
        {
          prop: 'creationDate',
          label: () => this.$t('basicPrice.creationDate'),
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
              show: row => !row.editable && row.baseMaterialPriceStatus === 'DRAFT',
              formattor: () => this.$t('basicPrice.editPrice')
            },
            {
              callback: (row, socpe) => this.save(row, socpe),
              show: row => row.editable,
              formattor: () => this.$t('common.save')
            },
            {
              callback: (row, socpe) => this.cancelEdit(row, socpe),
              show: row => row.editable,
              formattor: () => this.$t('common.cancel')
            },
            {
              callback: (row, socpe) => this.effect(row, socpe),
              show: row => ['DRAFT', 'INVAILD'].includes(row.baseMaterialPriceStatus) && !row.editable,
              formattor: () => this.$t('priceModel.costElement.effect')
            },
            {
              callback: (row, socpe) => this.failure(row, socpe),
              show: row => row.baseMaterialPriceStatus === 'ACTIVE' && !row.editable,
              formattor: () => this.$t('priceModel.costElement.failure')
            },
            {
              callback: (row, socpe) => this.drop(row, socpe),
              show: row => row.baseMaterialPriceStatus === 'INVAILD' && !row.editable,
              formattor: () => this.$t('common.abandon')
            },
            {
              callback: row => this.deleteItem(row),
              show: row => row.baseMaterialPriceStatus === 'DRAFT' && !row.editable,
              formattor: () => this.$t('common.delete')
            }
          ]
        }
      ],
      tableData: [],
      queryForm: [
        {
          label: () => this.$t('basicPrice.baseMaterialName'),
          prop: 'baseMaterialName'
        },
        {
          label: () => this.$t('basicPrice.baseMaterialCode'),
          prop: 'baseMaterialCode'
        },
        {
          label: () => this.$t('basicPrice.dataSource'),
          prop: 'dataSource'
        },
        {
          label: () => this.$t('basicPrice.priceType'),
          prop: 'baseMaterialPriceType',
          type: 'dict',
          code: 'PriceType'
        },
        {
          prop: 'baseMaterialPriceStatus',
          label: () => this.$t('basicPrice.status'),
          type: 'dict',
          code: 'StuffStatus'
        }
      ],
      mergeForm: {},
      formItems: [
        {
          itemAttrs: {
            label: this.$t('basicPrice.baseMaterialName'),
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
            label: this.$t('basicPrice.baseMaterialCode')
          },
          uiAttrs: {
            key: 'baseMaterialCode',
            disabled: true
          }
        },
        {
          itemAttrs: {
            label: this.$t('basicPrice.priceType'),
            rules: [{ required: true, message: this.$t('common.pleaseInput') }]
          },
          uiAttrs: {
            key: 'baseMaterialPriceType'
          },
          slot: 'baseMaterialPriceType'
        },
        {
          itemAttrs: {
            label: this.$t('basicPrice.baseMaterialPrice'),
            rules: [{ required: true, message: this.$t('common.pleaseInput') }]
          },
          uiAttrs: {
            key: 'baseMaterialPrice'
          }
        },
        {
          itemAttrs: {
            label: this.$t('basicPrice.dataSource'),
            rules: [{ required: true, message: this.$t('common.pleaseInput') }]
          },
          uiAttrs: {
            key: 'priceFrom'
          }
        },
        {
          tag: 'date',
          itemAttrs: {
            label: this.$t('basicPrice.effectiveDateFrom'),
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
            label: this.$t('basicPrice.effectiveDateTo'),
            rules: [
              {
                required: true,
                message: this.$t('common.pleaseInput')
              }
            ]
          },
          uiAttrs: {
            key: 'activeDateTo'
          },
          slot: 'activeDateTo'
        },
        {
          tag: 'select',
          itemAttrs: {
            label: this.$t('basicPrice.currency'),
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
            options: () => this.currency
          }
        },
        {
          itemAttrs: {
            label: this.$t('basicPrice.unit'),
            rules: [
              {
                required: true,
                message: this.$t('common.pleaseSelect'),
                type: 'string'
              }
            ]
          },
          uiAttrs: {
            key: 'baseMaterialUnit'
          },
          slot: 'baseMaterialUnit'
        },
        {
          itemAttrs: {
            label: this.$t('basicPrice.status')
          },
          uiAttrs: {
            key: 'baseMaterialPriceStatus'
          },
          slot: 'baseMaterialPriceStatus'
        }
      ],
      mode: 'add',
      addDialog: false,
      queryParam: {},
      activeDateToPickerOptions: {
        disabledDate: time => {
          const start = new Date(this.mergeForm.activeDateFrom)
          start.setHours(0)
          start.setMinutes(0)
          start.setSeconds(0)
          start.setMilliseconds(0)
          return time.getTime() < start.getTime()
        }
      }
    }
  },
  computed: {
    readOnly () {
      return this.mode === 'readOnly'
    },
    currency () {
      return this.dictClass.getDict('currency')
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
  methods: {
    transformData (data) {
      const list = data.data.list
      data.data.list = list.map(item => ({ ...item, editable: false }))
      return data
    },
    getValue (value) {
      this.mergeForm.baseMaterialName = value.baseMaterialName || ''
      this.mergeForm.baseMaterialCode = value.baseMaterialCode || ''
      // this.mergeForm.baseMaterialUnit = value.baseMaterialUnit || ''
    },
    saveRow () {
      this.$refs.form.validate().then((valid) => {
        if (valid.flag) {
          if (this.mode === 'add') {
            bp.add(this.mergeForm).then(res => {
              this.addDialog = false
              this.$message.success(res.message)
              this.getQuerydata()
            })
          }
          if (this.mode === 'edit') {
            bp.save(this.mergeForm).then(res => {
              this.addDialog = false
              this.$message.success(res.message)
              this.getQuerydata()
            })
          }
        }
      }).catch(() => {
        return false
      })
    },
    showAdd () {
      this.mode = 'add'
      Object.keys(this.mergeForm).forEach(key => {
        this.mergeForm[key] = null
      })
      // this.mergeForm.baseMaterialPriceType = 'DAY_PRICE'
      this.mergeForm.currencyType = 'CNY'
      this.addDialog = true
    },
    effect (row) {
      bp.active(row).then(res => {
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
          bp.deleteItem(baseMaterialPriceId).then(res => {
            this.$message.success(res.message)
            this.getQuerydata()
          })
        })
        .catch(() => {})
    },
    drop ({ baseMaterialPriceId }) {
      bp.drop(baseMaterialPriceId).then(res => {
        this.$message.success(res.message)
        this.getQuerydata()
      })
    },
    failure ({ baseMaterialPriceId }) {
      bp.inActive(baseMaterialPriceId).then(res => {
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
      bp.save(row).then(res => {
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
    taxRateChangeHandel (val, dictItem, row) {
      row.taxRate = dictItem ? dictItem.key : '' // 税率值
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
