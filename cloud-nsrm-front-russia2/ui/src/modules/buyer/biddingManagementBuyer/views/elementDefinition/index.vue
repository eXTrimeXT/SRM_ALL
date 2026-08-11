<template>
  <el-container
    class="flex-container-notab elementDefinition_wrapper"
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
            code="bid:elementDefinition:add"
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
        :source="elem.listPage"
        :open-custom-table="true"
      />
    </el-main>

    <!--弹窗-->
    <srm-dialog
      :visible.sync="addDialog"
      :title="title"
      size="middle"
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
        <template #priceType>
          <dict-select
            v-model="mergeForm.priceType"
            code="PriceType"
          />
        </template>

        <template #baseCode>
          <QuickSearch
            ref="quickSearchTool"
            show-key="baseMaterialCode"
            :show-input="mergeForm.baseMaterialCode"
            name="ceea_bid_base_material"
            :disabled="readOnly"
            @close-quicksearch="getValue"
          />
        </template>

        <template #materialMainFeild>
          <QuickSearch
            ref="quickSearchTool"
            show-key="attributeCode"
            :show-input="mergeForm.materialAttributeName"
            name="scc_base_material_item_attribute"
            :disabled="readOnly"
            @close-quicksearch="getAttribute"
          />
        </template>
      </BaseForm>

      <template
        v-if="mode !=='view'"
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
import { elem } from 'modb@/biddingManagementBuyer/api'

export default {
  name: 'ElementDefinition',
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
      elem: elem,
      dictClass: createDictClass({ 'EssentialFactorFromType': [], 'StuffStatus': [] }),
      defaultTableHeader: [],
      name: 'elementDefinition',
      tableName: 'elementDefinition',
      pageSize: 15,
      gridId: 'list',
      tableHeader: [
        {
          label: () => this.$t('elementDefinition.elementName'),
          prop: 'essentialFactorName',
          minWidth: 120
        },
        {
          label: () => this.$t('elementDefinition.elementDesc'),
          prop: 'essentialFactorDesc',
          minWidth: 120
        },
        {
          label: () => this.$t('basicPrice.baseMaterialCode'),
          prop: 'baseMaterialCode',
          minWidth: 120
        },
        {
          label: () => this.$t('elementDefinition.valueSource'),
          prop: 'essentialFactorFrom',
          formattor: val => this.$getDictLabel('EssentialFactorFromType', val),
          minWidth: 120
        },
        {
          label: () => this.$t('elementDefinition.elementStatus'),
          prop: 'essentialFactorStatus',
          formattor: val => this.$getDictLabel('StuffStatus', val),
          minWidth: 120
        },
        {
          prop: 'operation',
          label: () => this.$t('common.operation'), // 操作
          showType: 'buttons',
          btnStyle: 'text',
          fixed: 'right',
          width: 170,
          buttons: [
            {
              callback: (row, socpe) => this.edit(row, socpe),
              show: row => row.essentialFactorStatus === 'DRAFT',
              formattor: () => this.$t('common.edit')
            },
            {
              callback: (row, socpe) => this.effect(row, socpe),
              show: row => ['DRAFT', 'INVAILD'].includes(row.essentialFactorStatus),
              formattor: () => this.$t('priceModel.costElement.effect')
            },
            {
              callback: (row, socpe) => this.failure(row, socpe),
              show: row => row.essentialFactorStatus === 'ACTIVE',
              formattor: () => this.$t('priceModel.costElement.failure')
            },
            {
              callback: row => this.deleteItem(row),
              show: row => row.essentialFactorStatus === 'DRAFT',
              formattor: () => this.$t('common.delete')
            },
            {
              callback: row => this.viewElementDefinition(row),
              formattor: () => this.$t('common.view')
            }
          ]
        }
      ],
      tableData: [],
      queryForm: [
        {
          label: () => this.$t('elementDefinition.elementName'),
          prop: 'essentialFactorName'
        },
        {
          label: () => this.$t('elementDefinition.elementDesc'),
          prop: 'essentialFactorDesc'
        },
        {
          prop: 'essentialFactorFrom',
          label: () => this.$t('elementDefinition.valueSource'),
          type: 'select',
          options: () => this.dictClass.getDict('EssentialFactorFromType')
        },
        {
          prop: 'essentialFactorStatus',
          label: () => this.$t('elementDefinition.elementStatus'),
          type: 'select',
          options: () => this.dictClass.getDict('StuffStatus')
        }
      ],
      mergeForm: {},
      formItems: [
        {
          itemAttrs: {
            label: this.$t('elementDefinition.elementName'),
            rules: [
              {
                required: true,
                message: this.$t('common.pleaseInput'),
                type: 'string'
              }
            ]
          },
          uiAttrs: {
            key: 'essentialFactorName'
          },
          listeners: {
            change: value => {
              if (value && !this.mergeForm.essentialFactorDesc) {
                this.mergeForm.essentialFactorDesc = value
              }
            }
          }
        },
        {
          itemAttrs: {
            label: this.$t('elementDefinition.elementDesc')
          },
          uiAttrs: {
            key: 'essentialFactorDesc'
          }
        },
        {
          tag: 'select',
          itemAttrs: {
            label: this.$t('elementDefinition.valueSource')
          },
          uiAttrs: {
            key: 'essentialFactorFrom',
            options: () => this.dictClass.getDict('EssentialFactorFromType')
          }
        },
        {
          tag: 'select',
          itemAttrs: {
            label: this.$t('elementDefinition.elementStatus')
          },
          uiAttrs: {
            key: 'essentialFactorStatus',
            options: () => this.dictClass.getDict('StuffStatus'),
            disabled: true
          }
        },
        {
          slot: 'materialMainFeild',
          itemAttrs: {
            label: this.$t('elementDefinition.materialMainFeild')
          },
          ifRender: model => model.essentialFactorFrom === 'MATERIAL_MAIN_DATA',
          uiAttrs: {
            key: 'materialAttributeName'
          }
        },
        {
          slot: 'baseCode',
          itemAttrs: {
            label: this.$t('elementDefinition.baseCode')
          },
          ifRender: model =>
            model.essentialFactorFrom === 'BASE_MATERIAL_PRICE',
          uiAttrs: {
            key: 'baseMaterialCode'
          }
        },
        {
          slot: 'priceType',
          itemAttrs: {
            label: this.$t('elementDefinition.priceType')
          },
          ifRender: model =>
            model.essentialFactorFrom === 'BASE_MATERIAL_PRICE',
          uiAttrs: {
            key: 'priceType'
          }
        }
      ],
      mode: 'add',
      addDialog: false,
      viewDialog: false,
      queryParam: {}
    }
  },
  computed: {
    readOnly () {
      return ['readOnly', 'view'].includes(this.mode)
    },
    title () {
      const map = {
        add: this.$t('common.add'),
        edit: this.$t('common.edit'),
        view: this.$t('common.view')
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
    getValue (value, scope) {
      this.mergeForm.baseMaterialId = value.baseMaterialId || ''
      this.mergeForm.baseMaterialCode = value.baseMaterialCode || ''
      this.mergeForm.baseMaterialName = value.baseMaterialName || ''
    },
    getAttribute (value, scope) {
      this.mergeForm.materialAttributeId = value.materialAttributeId || ''
      this.mergeForm.materialAttributeName = value.attributeName || ''
      this.mergeForm.materialAttributeCode = value.attributeCode || ''
    },
    saveRow () {
      if (this.mode === 'add') {
        elem.add(this.mergeForm).then(res => {
          this.addDialog = false
          this.$message.success(res.message)
          this.getQuerydata()
        })
      }
      if (this.mode === 'edit') {
        elem.save(this.mergeForm).then(res => {
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
      this.addDialog = true
    },
    effect (row) {
      elem.active(row).then(res => {
        this.$message.success(res.message)
        this.getQuerydata()
      })
    },
    deleteItem ({ essentialFactorId }) {
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          elem.deleteItem(essentialFactorId).then(res => {
            this.$message.success(res.message)
            this.getQuerydata()
          })
        })
        .catch(() => {})
    },
    viewElementDefinition (row) {
      if (row) {
        elem.queryEssential(row.essentialFactorId).then(res => {
          if (res) {
            this.mode = 'view'
            this.mergeForm = res.data
            // this.$set(this, "mergeForm", row);
            this.addDialog = true
          }
        })
      }
    },
    failure ({ essentialFactorId }) {
      elem.inActive(essentialFactorId).then(res => {
        this.$message.success(res.message)
        this.getQuerydata()
      })
    },
    edit (row, scope) {
      this.mode = 'edit'
      this.mergeForm = row
      // this.$set(this, "mergeForm", row);
      this.addDialog = true
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
.elementDefinition_wrapper {
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
.elementDefinition_wrapper .el-table th > .cell {
  display: flex;
  justify-content: center;
}
</style>
