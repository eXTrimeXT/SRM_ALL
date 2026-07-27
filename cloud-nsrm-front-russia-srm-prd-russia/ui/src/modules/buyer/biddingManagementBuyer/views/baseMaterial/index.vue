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
        :source="bm.queryBaseMaterialByPage"
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
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import BaseForm from 'lib@/components/BaseForm'
import { bm } from 'modb@/biddingManagementBuyer/api'

export default {
  name: 'BaseMaterial',
  components: {
    TableView,
    BaseForm,
    MainHeader,
    FormWrapper
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      bm: bm,
      test: null,
      test1: null,
      defaultTableHeader: [],
      name: 'baseMaterial',
      tableName: 'baseMaterial',
      pageSize: 15,
      gridId: 'list',
      tableHeader: [
        {
          label: () => this.$t('baseMaterial.baseMaterialName'),
          prop: 'baseMaterialName',
          minWidth: 150
        },
        {
          label: () => this.$t('baseMaterial.baseMaterialCode'),
          prop: 'baseMaterialCode',
          minWidth: 150
        },
        {
          label: () => this.$t('baseMaterial.status'),
          prop: 'baseMaterialStatus',
          formattor: val => this.$getDictLabel('StuffStatus', val),
          width: 150
        },
        {
          label: () => this.$t('baseMaterial.createdBy'),
          prop: 'createdUserName', // createdBy
          width: 150
        },
        {
          prop: 'creationDate',
          label: () => this.$t('baseMaterial.creationDate'),
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
          label: () => this.$t('baseMaterial.baseMaterialName'),
          prop: 'baseMaterialName'
        },
        {
          label: () => this.$t('baseMaterial.baseMaterialCode'),
          prop: 'baseMaterialCode'
        },
        {
          prop: 'baseMaterialStatus',
          label: () => this.$t('baseMaterial.status'),
          type: 'dict',
          code: 'StuffStatus'
        }
      ],
      mergeForm: {},
      formItems: [
        {
          itemAttrs: {
            label: this.$t('baseMaterial.baseMaterialName'),
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
            label: this.$t('baseMaterial.baseMaterialCode')
          },
          uiAttrs: {
            key: 'baseMaterialCode',
            disabled: true
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
    testHandle (value) {},
    testHandle1 (value) {},
    async saveRow () {
      const { flag } = await this.$refs.form.validate()
      if(!flag) return
      if (this.mode === 'add') {
        bm.createBaseMaterial(this.mergeForm).then(res => {
          this.addDialog = false
          this.$message.success(res.message)
          this.getQuerydata()
        })
      }
      if (this.mode === 'edit') {
        bm.saveBaseMaterialTemporary(this.mergeForm).then(res => {
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
      // 无逻辑控制 但后端校验必填 这里直接随意赋值
      this.mergeForm.baseMaterialCalculateType = 'default'
      this.mergeForm.baseMaterialUnit = 'GE'
      this.addDialog = true
      this.$nextTick(() => {
        this.$refs.form.clearValidate()
      })
    },
    effect (row) {
      bm.activeBaseMateria(row).then(res => {
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
          bm.deleteBaseMaterialById(baseMaterialId).then(res => {
            this.$message.success(res.message)
            this.getQuerydata()
          })
        })
        .catch(() => {})
    },
    failure ({ baseMaterialId }) {
      bm.inActiveBaseMateria(baseMaterialId).then(res => {
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
