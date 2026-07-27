<template>
  <el-container
    class="flex-container-notab baseMaterial_wrapper"
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
            code="bid:baseMaterial:add"
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
        :source="material.queryMaterialByPage"
        :open-custom-table="true"
      />
    </el-main>
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
        :wrapper-col="{
          span: 8,
          gutter: 30
        }"
      />
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
import { material } from 'modb@/priceModel/api'

export default {
  name: 'Material',
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
      material: material,
      test: null,
      test1: null,
      defaultTableHeader: [],
      name: 'material',
      tableName: 'material',
      pageSize: 15,
      gridId: 'list',
      tableHeader: [
        {
          label: () => this.$t('material.baseMaterialName'),
          prop: 'baseMaterialName',
          minWidth: 150
        },
        {
          label: () => this.$t('material.baseMaterialCode'),
          prop: 'baseMaterialCode',
          minWidth: 150
        },
        {
          label: () => this.$t('material.status'),
          prop: 'baseMaterialStatus',
          dataType: 'dict',
          code: 'StuffStatus',
          width: 150
        },
        {
          label: () => this.$t('material.createdBy'),
          prop: 'createdUserName', // createdBy
          width: 150
        },
        {
          prop: 'creationDate',
          label: () => this.$t('material.creationDate'),
          width: 150
        },
        {
          prop: 'operation',
          label: () => this.$t('common.operation'), // 操作
          showType: 'buttons',
          btnStyle: 'text',
          // fixed: "right",
          width: 130,
          buttons: [
            {
              callback: (row, socpe) => this.edit(row, socpe),
              // code: "pr:requirementApply:edit",
              show: row => row.baseMaterialStatus === 'DRAFT',
              formattor: () => {
                return this.$t('common.edit')
              }
            },
            {
              callback: (row, socpe) => this.effect(row, socpe),
              // code: "pr:requirementApply:edit",
              show: row =>
                ['DRAFT', 'INVAILD'].includes(row.baseMaterialStatus),
              formattor: () => {
                return this.$t('priceModel.costElement.effect')
              }
            },
            {
              callback: (row, socpe) => this.failure(row, socpe),
              // code: "pr:requirementApply:edit",
              show: row => row.baseMaterialStatus === 'ACTIVE',
              formattor: () => {
                return this.$t('priceModel.costElement.failure')
              }
            },
            {
              callback: row => this.deleteItem(row),
              // code: "pr:requirementApply:edit",
              show: row => row.baseMaterialStatus === 'DRAFT',
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
          label: () => this.$t('material.baseMaterialName'),
          prop: 'baseMaterialName'
        },
        {
          label: () => this.$t('material.baseMaterialCode'),
          prop: 'baseMaterialCode'
        },
        {
          prop: 'baseMaterialStatus',
          label: () => this.$t('material.status'),
          type: 'dict',
          code: 'StuffStatus'
        }
      ],
      mergeForm: {},
      formItems: [
        {
          itemAttrs: {
            label: this.$t('material.baseMaterialName'),
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
          }
        },
        {
          itemAttrs: {
            label: this.$t('material.baseMaterialCode')
          },
          uiAttrs: {
            key: 'baseMaterialCode',
            disabled: true
          }
        },
        {
          tag: 'select',
          itemAttrs: {
            label: this.$t('material.seaFoodPrice')
          },
          uiAttrs: {
            key: 'seaFoodPrice',
            options: () => [
              { id: 1, label: this.$t('common.yes'), value: 'Y' },
              { id: 2, label: this.$t('common.no'), value: 'N' }
            ]
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
    }
  },
  updated () {
    this.defaultTableHeader = this.tableHeader
  },
  mounted () {
    this.getQuerydata()
  },
  methods: {
    testHandle (value) {
      console.log('[test handle]', value)
    },
    testHandle1 (value) {
      console.log('[test handle1]', value)
    },
    saveRow () {
      if (this.mode === 'add') {
        material.createMaterial(this.mergeForm).then(res => {
          this.addDialog = false
          this.$message.success(res.message)
          this.getQuerydata()
        })
      }
      if (this.mode === 'edit') {
        material.saveMaterialTemporary(this.mergeForm).then(res => {
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
      this.mergeForm.seaFoodPrice = 'Y'
      // 无逻辑控制 但后端校验必填 这里直接随意赋值
      this.mergeForm.baseMaterialCalculateType = 'default'
      this.mergeForm.baseMaterialUnit = 'GE'
      this.addDialog = true
    },
    effect (row) {
      material.activeMateria(row).then(res => {
        this.$message.success(res.message)
        this.getQuerydata()
      })
    },
    deleteItem ({ baseMaterialId }) {
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          material.deleteMaterialById(baseMaterialId).then(res => {
            this.$message.success(res.message)
            this.getQuerydata()
          })
        })
        .catch(() => {})
    },
    failure ({ baseMaterialId }) {
      material.inActiveMateria(baseMaterialId).then(res => {
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
.baseMaterial_wrapper {
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
.baseMaterial_wrapper .el-table th > .cell {
  display: flex;
  justify-content: center;
}
</style>
