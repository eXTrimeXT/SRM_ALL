<template>
  <!-- 物料明细选择(采购申请)-->
  <srm-dialog
    class="buyerPurchaseOrder-purchaseDialog"
    :title="$t('purchaseDemand.materialDetailSelect1')"
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
      url="/api-sup-ce/pr/requirementLine/listPageForOrder"
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
import { daterangePayloadFormat } from 'lib@/composition/commonComposition'

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
        },
        {
          prop: 'categoryKey',
          label: () => this.$t('bidMod.categoryName')
        },
        {
          prop: 'requirementHeadNum',
          label: () => this.$t('purchaseDemand.purRequisitionNum')
        },
        // 需求日期
        {
          prop: 'dateList',
          label: this.$t('purchaseDemand.requirementDate'),
          type: 'daterange'
        }
        // {
        //   prop: 'startDate',
        //   label: () => this.$t('purchaseDemand.requestDateFrom'),
        //   type: 'date'
        // },
        // {
        //   prop: 'endDate',
        //   label: () => this.$t('purchaseDemand.requestDateTo'),
        //   type: 'date'
        // }
      ],
      dialogFormVisible: false,
      tableHeader: [
        {
          prop: 'orgName',
          label: () => this.$t('purchaseDemand.businessEntity'),
          width: 120
        },
        {
          prop: 'organizationName',
          label: () => this.$t('purchaseDemand.invOrg'),
          width: 120
        },
        {
          prop: 'receiveAddress',
          label: () => this.$t('purchaseDemand.ceeaDeliveryPlace'),
          width: 120
        },
        {
          prop: 'categoryName',
          label: () => this.$t('purchaseDemand.materialCateSub'),
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
          prop: 'unitCode',
          label: () => this.$t('orderMod.buyerOrderSynergy.unit'),
          width: 120
        },
        {
          prop: 'requirementQuantity',
          label: () => this.$t('orderMod.buyerOrderSynergy.applicationQuantity'),
          width: 120
        },
        {
          prop: 'requirementDate',
          label: () => this.$t('purchaseDemand.requirementDate'),
          width: 120
        },
        {
          prop: 'requirementHeadNum',
          label: () => this.$t('purchaseDemand.purRequisitionNum'),
          width: 120
        },
        {
          prop: 'rowNum',
          label: () => this.$t('purchaseDemand.rowNum'),
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
    getQuerydata (payload = {}) {
      if (payload) {
        // 格式化时间范围
        payload = daterangePayloadFormat(payload, [
          // 创建时间
          { prop: 'dateList', fromProp: 'startDate', toProp: 'endDate' }
        ])
      }

      this.queryParam = {
        ...payload,
        ...this.queryData
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

<style lang="scss">
.buyerPurchaseOrder-purchaseDialog .vxe-table--body-wrapper{
  height: 360px !important;
}
</style>
