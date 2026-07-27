<template>
  <el-container
    class="flex-container price_model_list_wrapper"
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
          <el-button
            type="primary"
            @click="edit({}, 'add')"
          >
            {{
              $t("common.add")
            }}
          </el-button>
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
        :source="priceModel.listPage"
        :com-active="$attrs['changeTab']"
        :open-custom-table="true"
      />
    </el-main>
  </el-container>
</template>

<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import priceModelEdit from './edit'
import { priceModel } from 'modb@/priceModel/api'

export default {
  name: 'PriceModelList',
  components: {
    TableView,
    MainHeader,
    FormWrapper
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      priceModel: priceModel,
      defaultTableHeader: [],
      name: 'priceModelList',
      tableName: 'priceModelList',
      pageSize: 15,
      gridId: 'list',
      tableHeader: [
        {
          label: () => this.$t('priceModel.priceModel.priceModelCode'),
          prop: 'priceModelCode',
          minWidth: 150
        },
        {
          label: () => this.$t('priceModel.priceModel.priceModelName'),
          prop: 'priceModelName',
          minWidth: 150,
          showType: 'button',
          btnStyle: 'text',
          callback: row => this.readOnly(row)
        },
        {
          label: () => this.$t('priceModel.priceModel.categoryName'),
          prop: 'categoryName',
          minWidth: 120
        },
        {
          label: () => this.$t('priceModel.costElement.status'),
          prop: 'status',
          formattor: val => this.$getDictLabel('PRICE_MODEL_STATUS', val),
          minWidth: 100
        },
        {
          label: () => this.$t('priceModel.costElement.orgName'),
          prop: 'orgName',
          minWidth: 150
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
          minWidth: 150
        },
        {
          label: () => this.$t('priceModel.costElement.creationDate'),
          prop: 'creationDate',
          minWidth: 150
        },
        {
          prop: 'operation',
          label: () => this.$t('common.operation'),
          showType: 'buttons',
          btnStyle: 'text',
          fixed: 'right',
          minWidth: 130,
          buttons: [
            {
              callback: row => this.edit(row),
              // code: "pr:requirementApply:edit",
              show: row => row.status === 'DRAFT',
              formattor: () => {
                return this.$t('common.edit')
              }
            },
            {
              callback: row => this.deleteItem(row),
              // code: "pr:requirementApply:edit",
              show: row => row.status === 'DRAFT',
              formattor: () => {
                return this.$t('common.delete')
              }
            },
            {
              callback: row => this.effect(row),
              // code: "pr:requirementApply:edit",
              show: row => row.status === 'DRAFT' || row.status === 'INVALID',
              formattor: () => {
                return this.$t('priceModel.costElement.effect')
              }
            },
            {
              callback: row => this.failure(row),
              // code: "pr:requirementApply:edit",
              show: row => row.status === 'VALID',
              formattor: () => {
                return this.$t('priceModel.costElement.failure')
              }
            }
          ]
        }
      ],
      tableData: [],
      queryForm: [],
      queryParam: {}
    }
  },
  updated () {
    this.defaultTableHeader = this.tableHeader
  },
  created () {
    this.queryForm = [
      {
        prop: 'orgId',
        label: () => this.$t('priceModel.costElement.orgName'),
        type: 'OUorganizationSelector'
      },
      {
        prop: 'status',
        type: 'dict',
        code: 'PRICE_MODEL_STATUS',
        label: () => this.$t('priceModel.costElement.status')
      },
      {
        prop: 'priceModelCode',
        label: () => this.$t('priceModel.priceModel.priceModelCode')
      },
      {
        prop: 'priceModelName',
        label: () => this.$t('priceModel.priceModel.priceModelName')
      },
      {
        prop: 'creationDate',
        type: 'date',
        label: () => this.$t('priceModel.costElement.creationDate')
      },
      {
        prop: 'categoryId',
        label: () => this.$t('priceModel.priceModel.categoryName'),
        type: 'quicksearch',
        propKey: 'categoryId',
        showKey: 'categoryName',
        name: 'scc_base_purchase_category'
      }
    ]
  },
  mounted () {
    this.getQuerydata()
  },
  methods: {
    readOnly (row) {
      const tab = {
        component: priceModelEdit,
        params: {
          row,
          flag: 'edit',
          readOnly: true
        },
        title: `${this.$t('priceModel.priceModel.readOnly')}-${
          row.priceModelName
        }`,
        name: 'priceModelEdit' + row.priceModelHeadId
      }
      this.$emit('tab-add', tab)
    },
    effect ({ priceModelHeadId }) {
      priceModel.takeEffect(priceModelHeadId).then(res => {
        this.$message.success(res.message)
        this.getQuerydata()
      })
    },
    failure ({ priceModelHeadId }) {
      priceModel.failure(priceModelHeadId).then(res => {
        this.$message.success(res.message)
        this.getQuerydata()
      })
    },
    deleteItem ({ priceModelHeadId }) {
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          priceModel.delete(priceModelHeadId).then(res => {
            this.$message.success(res.message)
            this.getQuerydata()
          })
        })
        .catch(() => {})
    },
    edit (row, flag = 'edit') {
      const tab = {
        component: priceModelEdit,
        params: {
          row,
          flag
        },
        title: `${this.$t(`common.${flag}`)}${
          flag === 'add' ? '' : `-${row.priceModelName}`
        }`,
        name: 'priceModelEdit' + (flag === 'edit' ? row.priceModelHeadId : '')
      }
      this.$emit('tab-add', tab)
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
.price_model_list_wrapper {
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
.price_model_list_wrapper .el-table th > .cell {
  display: flex;
  justify-content: center;
}
</style>
