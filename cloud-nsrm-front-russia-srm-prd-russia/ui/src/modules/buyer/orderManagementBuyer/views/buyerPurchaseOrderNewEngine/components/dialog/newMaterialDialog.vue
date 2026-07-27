<template>
  <!-- 物料新增 - 物料明细选择-->
  <srm-dialog
    class="buyerPurchaseOrder-materialDialog"
    :title="$t('purchaseDemand.materialDetailSelect')"
    :contentMaxHeightLimit="false"
    size="large"
    :visible.sync="dialogFormVisible"
    :close-on-click-modal="false"
    :destroy-on-close="true"
    :before-close="close"
    v-bind="$attrs"
    v-on="$listeners"
  >
    <FormWrapper
      ref="lineFormRef"
      :colLength="2"
      :form-array="queryForm"
      @getFormData="getQuerydata"
    />
    <TableView
      :ref="gridId"
      :table-data="tableData"
      :table-header="tableHeader"
      :page-size="pageSize"
      :pre-query-data="queryParam"
      :row-index="false"
      checkbox
      :check-change="checkChange"
      url="/api-base/material/materialItem/listForPurchase"
    />

    <div slot="footer" class="dialog-footer">
      <el-button @click="close">
        {{ $t("common.cancel") }}
      </el-button>
      <el-button type="primary" @click="confirm">
        {{ $t("common.confirm") }}
      </el-button>
    </div>
  </srm-dialog>
</template>

<script>
import QuickSearch from 'lib@/components/QuickSearch' // 快速查询组件
import FormWrapper from 'lib@/components/Table/FormWrapper'
import TableView from 'lib@/components/Table/TableView'

export default {
  name: 'PurchaseDialog',
  components: {
    QuickSearch,
    FormWrapper,
    TableView
  },
  props: {
    queryData: {
      type: Object,
      default: () => {
        return {}
      }
    },
    visible: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      selections: [],
      pageSize: 15,
      gridId: 'list',
      tableData: [],
      queryParam: {},
      queryForm: [
        {
          prop: 'materialCode',
          label: () => this.$t('orderMod.buyerOrderSynergy.materialName'),
          type: 'quicksearch',
          showKey: 'materialName',
          propKey: 'materialCode',
          name: 'scc_base_material_item'
        }
      ],
      dialogFormVisible: false,
      tableHeader: [
        {
          prop: 'organizationName',
          label: () => this.$t('purchaseDemand.invOrg'),
          width: 120
        },
        {
          prop: 'materialCode',
          label: () => this.$t('purchaseDemand.itemCode'),
          width: 120
        },
        {
          prop: 'materialName',
          label: () => this.$t('purchaseDemand.itemName'),
          width: 120
        },
        {
          prop: 'categoryCode',
          label: () => this.$t('components.category.categoryCode'), // '品类编码'
          width: 120
        },
        {
          prop: 'categoryName',
          label: () => this.$t('components.category.categoryName'), // '品类名称'
          width: 120
        },
        {
          prop: 'categoryFullName',
          label: () => this.$t('vendorMod.categoryFullName'), // '品类全称'
          width: 120
        }
      ]
    }
  },
  watch: {
    visible (newValue) {
      this.dialogFormVisible = newValue
      if (newValue) {
        this.$nextTick(() => {
          this.$refs['lineFormRef'].reset()
          this.getQuerydata()
        })
      }
    }
  },
  methods: {
    getQuerydata (obj = {}) {
      this.queryParam = {
        ...this.queryData,
        enabled: 'Y',
        materialCode: obj.materialCode || ''
      }
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    checkChange (val) {
      this.selections = val
    },
    close () {
      this.$emit('close', this.selections)
    },
    confirm () {
      this.$emit('confirm', this.selections)
    }
  }
}
</script>

<style lang="scss" scoped>
.form-item-line {
  display: flex;
  align-items: center;
  width: 250px;
}

.input-with-select {
  width: 150px !important;
  margin-right: 10px;
}
</style>
<style lang="scss">
.buyerPurchaseOrder-materialDialog .vxe-table--body-wrapper{
  height: 360px !important;
}
</style>
