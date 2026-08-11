<template>
  <el-container class="flex-container the_vendorPurchaseOrderList_wrapper" direction="vertical">
    <el-main>
      <FormWrapper :form-array="preArr" form-label-width="120px" @getFormData="getQuerydata" />

      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <!-- 新增 -->
          <AuthorityButton
            code="pur:purchaseCatalogOnOrOff:add"
            type="primary"
            @click="addData"
          >
            {{ $t('orderMod.buyerOrderSynergy.add') }}
          </AuthorityButton>
        </template>
      </MainHeader>
      <TableView
        ref="tableRef"
        row-key="priceLibraryId"
        :bigData="true"
        :checkbox="true"
        :table-header="tableHeader"
        :pre-query-data="queryParam"
        :auto-query="false"
        url="/api-sup-ce/pr/catalogOnShelves/listPage"
        :open-custom-table="true"
        :reserve-selection="true"
        :comActive="$attrs['changeTab']"
      />
    </el-main>
  </el-container>
</template>
<script>
import { parseTime } from '@/utils'
import ExportExcel from 'lib@/components/export-excel'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MainHeader from 'lib@/components/Table/MainHeader'
import TableView from 'lib@/components/Table/TableView'
import purchaseCatalogOnOrOffDetail from './purchaseCatalogOnOrOffDetail'

export default {
  name: 'PurchaseCatalogOnOrOffList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    ExportExcel
  },
  data () {
    return {
      queryParam: {},
      preArr: [
        {
          prop: 'orgId',
          label: () => this.$t('oneStopShopping.businessEntity'),
          type: 'OUorganizationSelector'
        },
        {
          prop: 'organizationId',
          parentId: 'orgId',
          label: () => this.$t('purchaseDemand.invOrg'),
          type: 'INVorganizationSelector' // 库存组织
        },
        {
          prop: 'status',
          label: () => this.$t('common.status'), // 状态
          type: 'dict',
          code: 'CATALOG_ON_SHELVES_STATUS'
        },
        {
          prop: 'categoryName',
          label: () => this.$t('common.categoryName'), // '品类名称'
          type: 'catSelect',
          showKey: 'categoryName'
        },
        {
          prop: 'materialId',
          label: () => this.$t('common.materialName'), // 物料名称
          type: 'quicksearch',
          showKey: 'materialName',
          propKey: 'materialId',
          name: 'scc_base_material_item'
        },
        {
          prop: 'vendorId',
          label: () => this.$t('orderMod.buyerOrderSynergy.vendorName'),
          type: 'quicksearch',
          showKey: 'companyName',
          propKey: 'companyId',
          name: 'scc_sup_company_info_all'
        }
      ],
      tableHeader: [
        {
          prop: 'orgName',
          label: () => this.$t('purchaseDemand.businessEntity'), // 业务实体
          width: 150
        },
        {
          prop: 'organizationName',
          label: () => this.$t('purchaseDemand.invOrg'), // 库存组织
          width: 100
        },
        {
          prop: 'materialCode',
          label: () => this.$t('common.materialCode'), // 物料编码
          width: 100
        },
        {
          prop: 'materialName',
          label: () => this.$t('common.materialName'), // 物料名称
          width: 100
        },
        {
          prop: 'categoryName',
          label: () => this.$t('dataConfMod.category'), // 品类
          width: 100
        },
        {
          prop: 'taxPrice',
          label: () => this.$t('purchaseDemand.taxPrice'), // 含税单价
          width: 100
        },
        {
          prop: 'vendorCode',
          label: () => this.$t('common.vendorCode'), //  供应商编码
          minWidth: 150
        },
        {
          prop: 'vendorName',
          label: () => this.$t('common.vendorName'), // 供应商名称
          minWidth: 150
        },
        {
          prop: 'priceLibraryNo',
          label: this.$t('bidMod.priceLibraryNo'), // 价格库编号
          width: 140
        },
        {
          prop: 'effectiveDate',
          label: this.$t('dataConfMod.priceEffectiveDate'), // 价格有效期起
          width: 150,
          dataType: 'dateTime'
        },
        {
          prop: 'expirationDate',
          label: this.$t('dataConfMod.priceExpirationDate'), // 价格有效期至
          width: 150,
          dataType: 'dateTime'
        },
        {
          prop: 'createdUserName',
          label: () => this.$t('orderMod.buyerOrderSynergy.createdBy'),
          width: 100
        },
        {
          prop: 'creationDate',
          label: () => this.$t('orderMod.buyerOrderSynergy.creationDate'),
          width: 150,
          dataType: 'dateTime'
        },
        {
          prop: 'offShelvesBy',
          label: () => this.$t('dataConfMod.remover'), // 下架人
          width: 100
        },
        {
          prop: 'offShelvesReason',
          label: () => this.$t('dataConfMod.offShelvesReason'), // 下架原因
          width: 100
        },
        {
          prop: 'offShelvesDate',
          label: () => this.$t('dataConfMod.removeDate'), // 下架时间
          width: 150,
          dataType: 'dateTime'
        },
        {
          prop: 'status',
          label: () => this.$t('common.status'), // 状态
          width: 100,
          btnStyle: 'text',
          fixed: 'right',
          dataType: 'dict',
          code: 'CATALOG_ON_SHELVES_STATUS'
        },
        {
          prop: 'operation',
          label: () => this.$t('common.operation'),
          width: 120,
          btnStyle: 'text',
          fixed: 'right',
          showType: 'buttons',
          buttons: [
            {
              callback: row => this.manageOne(row),
              code: 'pur:purchaseCatalogOnOrOff:manage',
              // 待上架、已上架、已下架、待审核
              show: row =>
                ['TO_BE_ON_SHELVES', 'ON_SHELVES', 'OFF_SHELVES', 'TO_BE_APPROVED'].includes(
                  row.status
                ),
              formattor: () => this.$t('contractMod.manage')
            },
            {
              callback: row => this.viewOne(row),
              code: 'pur:purchaseCatalogOnOrOff:view',
              formattor: () => this.$t('common.view')
            }
          ]
        }
      ]
    }
  },
  mounted () {
    this.getQuerydata()
  },
  methods: {
    getQuerydata (obj) {
      this.queryParam = obj || this.queryParam
      this.$nextTick(() => {
        this.$refs.tableRef.query()
      })
    },
    addData () {
      this.$emit('tab-add', {
        component: purchaseCatalogOnOrOffDetail,
        params: {
          flag: 'add',
          tabName: 'purchaseCatalogOnOrOffDetail'
        },
        title: this.$t('route.purchaseCatalogOnOrOff'),
        name: 'purchaseCatalogOnOrOffDetail'
      })
    },
    manageOne (row) {
      this.$emit('tab-add', {
        component: purchaseCatalogOnOrOffDetail,
        params: {
          flag: 'edit',
          row,
          tabName: 'purchaseCatalogOnOrOffDetail' + row.priceLibraryNo
        },
        title: row.priceLibraryNo,
        name: 'purchaseCatalogOnOrOffDetail' + row.priceLibraryNo
      })
    },
    viewOne (row) {
      this.$emit('tab-add', {
        component: purchaseCatalogOnOrOffDetail,
        params: {
          flag: 'view',
          row,
          tabName: 'purchaseCatalogOnOrOffDetail' + row.priceLibraryNo
        },
        title: row.priceLibraryNo,
        name: 'purchaseCatalogOnOrOffDetail' + row.priceLibraryNo
      })
    }
  }
}
</script>

<style scoped lang="scss"></style>
