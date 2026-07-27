<template>
  <el-container class="flex-container-notab formula_wrapper" direction="vertical">
    <el-main>
      <FormWrapper :form-array="queryForm" @getFormData="getQueryData" />

      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <AuthorityButton
            code="bid:formula:add"
            type="primary"
            @click="formulaAdd"
          >
            {{ $t('common.add') }}
          </AuthorityButton>
        </template>
      </MainHeader>

      <TableView
        ref="list"
        :table-data="tableData"
        :table-header="tableHeader"
        :row-index-fixed="false"
        :checkbox="false"
        :pre-query-data="queryParam"
        :source="formula.listPage"
        :com-active="$attrs['changeTab']"
        open-custom-table
      />
    </el-main>

    <FormulaDetailDialog
      v-if="formulaDetailDialogVisible"
      :visible.sync="formulaDetailDialogVisible"
      :edit-row="editRow"
      @submit-success="getQueryData"
    />
  </el-container>
</template>

<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import FormulaDetailDialog from './formulaDetailDialog.vue'
import { formula } from 'modb@/biddingManagementBuyer/api'

export default {
  name: 'Formula',

  components: {
    TableView,
    MainHeader,
    FormWrapper,
    FormulaDetailDialog
  },

  mixins: [tabTodoWatch, tabTodoMixin],

  data () {
    return {
      formula: formula,
      tableHeader: [
        // 公式名称
        {
          label: this.$t('formula.formulaName'),
          prop: 'pricingFormulaName',
          minWidth: 150
        },
        // 公式描述
        {
          label: this.$t('formula.formulaDesc'),
          prop: 'pricingFormulaDesc',
          minWidth: 150
        },
        // 公式明细
        {
          label: this.$t('formula.formulaDetail'),
          prop: 'pricingFormulaValue',
          minWidth: 150
        },
        // 状态
        {
          label: this.$t('formula.formulaStatus'),
          prop: 'pricingFormulaStatus',
          formattor: val => this.$getDictLabel('StuffStatus', val),
          minWidth: 150
        },
        // 创建人
        {
          label: this.$t('formula.createdBy'),
          prop: 'createdUserName',
          minWidth: 150
        },
        // 创建时间
        {
          prop: 'creationDate',
          label: this.$t('formula.creationDate'),
          minWidth: 150
        },
        {
          prop: 'operation',
          label: this.$t('common.operation'),
          showType: 'buttons',
          btnStyle: 'text',
          fixed: 'right',
          width: 130,
          buttons: [
            // 编辑
            {
              show: row => ['DRAFT'].includes(row.pricingFormulaStatus),
              formattor: () => this.$t('common.edit'),
              callback: row => this.edit(row)
            },
            // 生效
            {
              show: row => ['DRAFT', 'INVAILD'].includes(row.pricingFormulaStatus),
              formattor: () => this.$t('priceModel.costElement.effect'),
              callback: row => this.effect(row)
            },
            // 失效
            {
              show: row => row.pricingFormulaStatus === 'ACTIVE',
              formattor: () => this.$t('priceModel.costElement.failure'),
              callback: row => this.failure(row)
            },
            // 删除
            {
              show: row => row.pricingFormulaStatus === 'DRAFT',
              formattor: () => this.$t('common.delete'),
              callback: row => this.deleteItem(row)
            }
          ]
        }
      ],
      tableData: [],
      queryForm: [
        {
          label: () => this.$t('formula.formulaName'),
          prop: 'pricingFormulaName'
        },
        {
          label: () => this.$t('formula.formulaDesc'),
          prop: 'pricingFormulaDesc'
        },
        {
          prop: 'pricingFormulaStatus',
          label: () => this.$t('formula.formulaStatus'),
          type: 'dict',
          code: 'StuffStatus'
        }
      ],
      queryParam: {},
      formulaDetailDialogVisible: false,
      editRow: null
    }
  },

  mounted () {
    this.getQueryData()
  },

  methods: {
    /* 新增 */
    formulaAdd () {
      this.editRow = null
      this.formulaDetailDialogVisible = true
    },

    /* 生效 */
    effect ({ pricingFormulaHeaderId }) {
      formula.active(pricingFormulaHeaderId).then(res => {
        this.$message.success(res.message)
        this.getQueryData()
      })
    },

    /* 删除 */
    deleteItem ({ pricingFormulaHeaderId }) {
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        formula.deleteItem(pricingFormulaHeaderId).then(res => {
          this.$message.success(res.message)
          this.getQueryData()
        })
      })
    },

    /* 失效 */
    failure ({ pricingFormulaHeaderId }) {
      formula.invalid(pricingFormulaHeaderId).then(res => {
        this.$message.success(res.message)
        this.getQueryData()
      })
    },

    /* 编辑 */
    edit (row) {
      this.editRow = row
      this.formulaDetailDialogVisible = true
    },

    getQueryData (v) {
      this.queryParam = v
      this.$nextTick(() => {
        this.$refs.list.query()
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
