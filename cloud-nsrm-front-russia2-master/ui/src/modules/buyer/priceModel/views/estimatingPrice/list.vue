<template>
  <el-container
    class="flex-container estimating_price_list"
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
            {{ $t('common.add') }}
          </el-button>
        </template>
      </MainHeader>

      <TableView
        ref="list"
        :table-data="tableData"
        :table-header="tableHeader"
        :row-index-fixed="false"
        :page-size="pageSize"
        :checkbox="false"
        :pre-query-data="queryParam"
        :com-active="$attrs['changeTab']"
        :source="estimatingPrice.listPage"
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
import estimatingPriceEdit from './edit'
import { estimatingPrice } from 'modb@/priceModel/api'

export default {
  name: 'EstimatingPriceList',

  components: {
    TableView,
    MainHeader,
    FormWrapper
  },

  mixins: [tabTodoWatch, tabTodoMixin],

  data () {
    return {
      pageSize: 15,
      estimatingPrice: estimatingPrice,
      defaultTableHeader: [],
      mergeForm: {},
      tableHeader: [
        {// 单据编码
          label: () => this.$t('priceModel.estimatingPrice.estimateCode'),
          prop: 'estimateCode',
          minWidth: 150
        },
        {// 单据名称
          label: () => this.$t('priceModel.estimatingPrice.estimateName'),
          prop: 'estimateName',
          minWidth: 150,
          showType: 'button',
          btnStyle: 'text',
          callback: row => this.readOnly(row)
        },
        {// 单据状态
          label: '单据状态',
          prop: 'estimateStatus',
          formattor: val => this.$getDictLabel('ESTIMATE_STATUS', val),
          minWidth: 100
        },
        {// 采购分类
          label: () => this.$t('priceModel.priceModel.categoryName'),
          prop: 'categoryName',
          minWidth: 150
        },
        {// 物料编码
          label: () => this.$t('priceModel.estimatingPrice.materialCode'),
          prop: 'materialCode',
          minWidth: 150
        },
        {// 物料名称
          label: () => this.$t('priceModel.estimatingPrice.materialName'),
          prop: 'materialName',
          minWidth: 150
        },
        {// 预估总价
          label: () =>
            this.$t('priceModel.estimatingPrice.estimatedTotalPrice'),
          prop: 'estimatedTotalPrice',
          minWidth: 150
        },
        {// 模型名称
          label: () => this.$t('priceModel.priceModel.priceModelName'),
          prop: 'priceModelName',
          minWidth: 150
        },
        {// 模型编码
          label: () => this.$t('priceModel.priceModel.priceModelCode'),
          prop: 'priceModelCode',
          minWidth: 150
        },
        {// 核价人员
          label: () => this.$t('priceModel.estimatingPrice.nuclearUserName'),
          prop: 'nuclearFullName',
          minWidth: 150
        },
        {// 币种
          label: () => this.$t('priceModel.costElement.clearCurrency'),
          prop: 'clearCurrency',
          formattor: val => this.$getDictLabel('currency', val),
          minWidth: 150
        },
        {// 创建人
          label: () => this.$t('priceModel.costElement.createdBy'),
          prop: 'createdUserName', // createdBy
          minWidth: 130
        },
        {// 最近更新人
          label: () => this.$t('priceModel.costElement.lastUpdatedBy'),
          prop: 'lastUpdatedUserName', // lastUpdatedBy
          minWidth: 130
        },
        {// 更新日期
          label: () => this.$t('priceModel.costElement.lastUpdateDate'),
          prop: 'lastUpdateDate',
          minWidth: 150,
          dataType: 'dateTime'
        },
        {// 创建日期
          label: () => this.$t('priceModel.costElement.creationDate'),
          prop: 'creationDate',
          minWidth: 150,
          dataType: 'dateTime'
        },
        {// 操作
          prop: 'operation',
          label: () => this.$t('common.operation'),
          showType: 'buttons',
          btnStyle: 'text',
          fixed: 'right',
          minWidth: 130,
          buttons: [
            {// 编辑
              callback: row => this.edit(row),
              show: row => row.estimateStatus === 'DRAFT',
              formattor: () => this.$t('common.edit')
            },
            {// 删除
              callback: row => this.deleteItem(row),
              show: row => row.estimateStatus === 'DRAFT',
              formattor: () => this.$t('common.delete')
            },
            {// 生效
              callback: row => this.valid(row),
              show: row => row.estimateStatus === 'DRAFT',
              formattor: () => this.$t('common.active')  // '生效'
            },
            {//
              callback: row => this.invalid(row),
              show: row => row.estimateStatus === 'VALID',
              formattor: () => this.$t('common.inactive')  // '失效'
            }
          ]
        }
      ],
      tableData: [],
      queryForm: [
        {// 单据编码
          prop: 'estimateCode',
          label: () => this.$t('priceModel.estimatingPrice.estimateCode')
        },
        {// 单据名称
          prop: 'estimateName',
          label: () => this.$t('priceModel.estimatingPrice.estimateName')
        },
        {// 单据状态
          prop: 'status',
          type: 'dict',
          code: 'ESTIMATE_STATUS',
          label: this.$t('vendorMod.relegation.documentStatus')  // '单据状态'
        },
        {// 创建人
          label: this.$t('common.creator'),  // '创建人'
          prop: 'createdId',
          // slot: "createdId",
          type: 'quicksearch',
          propKey: 'userId',
          showKey: 'nickname',
          name: 'scc_rbac_user_display'
        },
        {// 核心人员
          prop: 'nuclearFullName',
          label: () => this.$t('priceModel.estimatingPrice.nuclearUserName')
        },
        {// 创建日期
          prop: 'creationDate',
          type: 'date',
          label: () => this.$t('priceModel.costElement.creationDate')
        },
        {// 物料编码
          prop: 'materialId',
          label: () => this.$t('priceModel.estimatingPrice.materialCode'),
          type: 'quicksearch',
          showKey: 'materialName',
          propKey: 'materialId',
          name: 'scc_base_material_item'
        }
      ],
      queryParam: {}
    }
  },

  mounted () {
    this.getQuerydata()
  },

  methods: {
    readOnly (row) {
      const tab = {
        component: estimatingPriceEdit,
        params: {
          row,
          flag: 'edit',
          readOnly: true
        },
        title: `${this.$t('priceModel.priceModel.readOnly')}-${
          row.estimateName
        }`,
        name: 'estimatingPriceEdit' + row.estimateHeadId
      }
      this.$emit('tab-add', tab)
    },

    deleteItem ({ estimateHeadId }) {
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          this.estimatingPrice.delete(estimateHeadId).then(res => {
            this.$message.success(res.message)
            this.getQuerydata()
          })
        })
        .catch(() => {})
    },

    edit (row, flag = 'edit') {
      const tab = {
        component: estimatingPriceEdit,
        params: {
          row,
          flag
        },
        title: `${this.$t(`common.${flag}`)}${
          flag === 'add' ? '' : `-${row.estimateName}`
        }`,
        name:
          'estimatingPriceEdit' + (flag === 'edit' ? row.estimateHeadId : '')
      }
      this.$emit('tab-add', tab)
    },

    // 查询列表数据
    getQuerydata (v) {
      this.queryParam = { ...v }
      this.$nextTick(() => {
        this.$refs.list.query()
      })
    },

    // 生效单据
    valid (row) {
      this.estimatingPrice.valid(row.estimateHeadId).then(res => {
        this.$message.success(this.$t('common.effectiveSuccessfully'))  // '生效成功'
        this.getQuerydata()
      })
    },

    // 失效单据
    invalid (row) {
      this.estimatingPrice.invalid(row.estimateHeadId).then(res => {
        this.$message.success(this.$t('common.failedSuccess'))  // '失效成功'
        this.getQuerydata()
      })
    }
  }
}
</script>

<style scoped lang="scss">
.estimating_price_list {
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
.estimating_price_list .el-table th > .cell {
  display: flex;
  justify-content: center;
}
</style>
