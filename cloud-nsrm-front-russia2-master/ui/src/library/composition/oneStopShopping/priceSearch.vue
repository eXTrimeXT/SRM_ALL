<template>
  <!-- 价格库编号 -->
  <srm-dialog
    :title="$t('bidMod.priceLibraryNo')"
    size="large"
    :destroy-on-close="true"
    :visible.sync="priceVisible"
    :close-on-click-modal="false"
    v-bind="$attrs"
    v-on="$listeners"
  >
    <FormWrapper
      :form-array="preArr"
      @getFormData="getQuerydata"
    />
    <TableView
      v-if="priceVisible"
      ref="tableRef"
      style="height: 250px;"
      row-key="priceLibraryId"
      :rowIndex="false"
      :radioOptions="{ enabled: true, labelKey: 'priceLibraryId'}"
      :table-header="tableHeader"
      :pre-query-data="queryParam"
      :auto-query="false"
      :url="url"
      :reserve-selection="true"
      :comActive="$attrs['changeTab']"
      @radio-change="radioChange"
    />
    <div slot="footer" class="dialog-footer">
      <el-button @click="priceVisible = false">
        {{ $t('common.cancel') }}
      </el-button>
      <el-button type="primary" @click="priceComfirm">
        {{ $t('common.confirm') }}
      </el-button>
    </div>
  </srm-dialog>
</template>

<script>
import FormWrapper from 'lib@/components/Table/FormWrapper'
import TableView from 'lib@/components/Table/TableView'
import { parseTime } from '@/utils'
export default {
  name: 'PriceSearch',
  components: {
    TableView,
    FormWrapper
  },
  props: {
    url: {
      type: String,
      default: '/api-sup-ce/pr/catalogOnShelves/getPriceLibraryForCatalogOnShelves'
    },
    priceVisible: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      userType: this.$store.getters.user.userType, // VENDOR | BUYER
      currentRow: {},
      queryParam: {},
      preArr: [
        {
          prop: 'vendorId',
          label: () => this.$t('orderMod.buyerOrderSynergy.vendorName'),
          type: 'quicksearch',
          showKey: 'companyName',
          propKey: 'companyId',
          name: 'scc_sup_company_info_all'
        },
        {
          prop: 'materialId',
          label: () => this.$t('orderMod.buyerOrderSynergy.materialName'),
          type: 'quicksearch',
          showKey: 'materialName',
          propKey: 'materialId',
          name: 'scc_base_material_item'
        },
        {
          prop: 'orgId',
          label: () => this.$t('oneStopShopping.businessEntity'),
          type: 'OUorganizationSelector'
        }
      ],
      tableHeader: [
        {
          prop: 'priceLibraryNo',
          label: this.$t('bidMod.priceLibraryNo'), // 价格库编号
          minWidth: 140
        },
        {
          prop: 'vendorName',
          label: () => this.$t('common.vendorName'), // 供应商名称
          minWidth: 150
        },
        {
          prop: 'vendorCode',
          label: () => this.$t('common.vendorCode'), //  供应商编码
          minWidth: 150
        },
        {
          prop: 'materialName',
          label: () => this.$t('common.materialName'), // 物料名称
          minWidth: 100
        },
        {
          prop: 'materialCode',
          label: () => this.$t('common.materialCode'), // 物料编码
          minWidth: 100
        },
        {
          prop: 'orgName',
          label: () => this.$t('purchaseDemand.businessEntity'), // 业务实体
          minWidth: 150
        },
        {
          prop: 'organizationName',
          label: () => this.$t('purchaseDemand.invOrg'), // 库存组织
          minWidth: 100
        },
        {
          prop: 'taxPrice',
          label: () => this.$t('materialPrice.price'), // 价格
          minWidth: 80
        },
        {
          prop: 'currencyCode',
          label: () => this.$t('purchaseDemand.currency'), // 币种
          minWidth: 120,
          formattor: (value) => value ? this.$getDictLabel('currency', value) : ''
        },
        {
          prop: 'priceValidity',
          label: () => this.$t('bidMod.common.priceValidity'), // 价格有效期
          minWidth: 180,
          formattor: (scope, row) => {
            let effectiveDate = row.effectiveDate ? parseTime(row.effectiveDate, '{y}-{m}-{d}') : ''
            let expirationDate = row.expirationDate ? parseTime(row.expirationDate, '{y}-{m}-{d}') : ''
            return `${effectiveDate} / ${expirationDate}`
          }
        }
      ]
    }
  },
  watch: {
    priceVisible (oldValue, newValue) {
      if (!newValue) {
        this.getQuerydata()
      }
    }
  },
  created () {
    if (this.userType === 'VENDOR') {
      // 供方去除供应商查询
      this.preArr.splice(0, 1)
    }
  },
  methods: {
    radioChange (select) {
      this.currentRow = select
    },
    priceComfirm () {
      this.$emit('priceComfirm', this.currentRow)
    },
    getQuerydata (obj) {
      this.queryParam = obj || this.queryParam
      this.$nextTick(() => {
        this.$refs.tableRef.query()
      })
    }
  }
}
</script>

<style></style>
