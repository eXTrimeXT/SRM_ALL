<template>
  <el-container
    class="flex-container the_more_info_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="preArr"
        form-label-width="120px"
        :pre-form-obj.sync="preFormObj"
        @getFormData="getQuerydata"
      >
        <!-- 订单类型 -->
        <template #orderType="{ scope }">
          <dict-select
            v-model="scope.orderType"
            code="ORDER_TYPE"
          />
        </template>
        <!-- 订单状态 -->
        <template #orderStatus="{ scope }">
          <dict-select
            v-model="scope.orderStatus"
            code="PURCHASE_ORDER"
          />
        </template>
        <!-- 入库状态 -->
        <template #warehouseReceiptStatus="{ scope }">
          <dict-select
            v-model="scope.warehouseReceiptStatus"
            code="RECEIVED_STATUS"
          />
        </template>
        <!-- 组织类型 -->
        <template #organizationTypeCode="{ scope }">
          <dict-select
            v-model="scope.organizationTypeCode"
            code="ORG_TYPE"
            custom-select-type="ORG_TYPE"
          />
        </template>
        <!-- 组织 -->
        <template #organizationId="{ scope }">
          <OrganizationSelector
            ref="selector"
            v-model="scope.organizationId"
            :parent-id="-1"
            :node-type="scope.organizationTypeCode"
            :placeholder="$t('common.pleaseSelect')"
            :scope="scope"
            @select="selectHandle"
          />
        </template>
        <!-- 开始时间 -->
        <template #startMonth="{ scope }">
          <el-date-picker
            v-model="scope.startMonth"
            type="month"
            :format="$formatDatePicker"
            value-format="yyyy-MM"
            :placeholder="$t('dataConfMod.selStartTime')"
          />
        </template>
        <!-- 结束时间 -->
        <template #endMonth="{ scope }">
          <el-date-picker
            v-model="scope.endMonth"
            :format="$formatDatePicker"
            value-format="yyyy-MM"
            type="month"
            :placeholder="$t('dataConfMod.selEndTime')"
          />
        </template>
      </FormWrapper>

      <TableView
        :ref="gridId"
        :table-header="tableHeader"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :auto-query="false"
        :source="purchaseAnalysisApi.queryPurchaseDetailList"
        :open-custom-table="true"
      />
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import OrganizationSelector from 'lib@/components/organization-selector'
import { purchaseAnalysisApi } from 'modb@/orderManagementBuyer/api'
export default {
  name: 'MoreInfo',
  components: {
    TableView,
    FormWrapper,
    OrganizationSelector
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    const { type } = this.$attrs.params
    const dateMap = [
      this.$t('dataConfMod.demand'),
      this.$t('dataConfMod.demand'),
      this.$t('dataConfMod.create'),
      this.$t('dataConfMod.demand'),
      this.$t('dataConfMod.warehousing')
    ]
    const name = dateMap[type - 1]
    return {
      // store: createStore({
      //   ORG_TYPE: [],
      //   CATEGORY: [],
      //   // 入库状态
      //   RECEIVED_STATUS: [],
      //   // 订单状态
      //   PURCHASE_ORDER: [],
      //   // 订单类型
      //   ORDER_TYPE: [],
      //   SEASON: [
      //     // { id: 1, value: 1, label: this.$t("time.quarterList[0]") },
      //     // { id: 2, value: 2, label: this.$t("time.quarterList[1]") },
      //     // { id: 3, value: 3, label: this.$t("time.quarterList[2]") },
      //     // { id: 4, value: 4, label: this.$t("time.quarterList[3]") }
      //   ],
      //   YES_NO: [
      //     // { id: 1, value: "Y", label: this.$t("common.yes") },
      //     // { id: 2, value: "N", label: this.$t("common.no") }
      //   ]
      // }),
      tableName: 'moreInfoDetail',
      defaultTableHeader: [],
      filterParams: {},
      pageSize: 15,
      name: 'more-info-detail',
      gridId: 'list',
      formLabelWidth: '100px',
      source: null,
      preArr: [
        {
          prop: 'organizationTypeCode',
          label: this.$t('dataConfMod.orgType'),
          slot: 'organizationTypeCode',
          type: 'slot'
        },
        {
          prop: 'organizationId',
          label: this.$t('common.orgName'),
          slot: 'organizationId',
          type: 'slot'
        },
        {
          prop: 'startMonth',
          label: `${name}` + this.$t('dataConfMod.startDate1'),
          slot: 'startMonth',
          type: 'slot'
        },
        {
          prop: 'endMonth',
          label: `${name}` + this.$t('dataConfMod.endTime'),
          slot: 'endMonth',
          type: 'slot'
        },
        {
          prop: 'categoryId',
          label: this.$t('dataConfMod.category'),
          type: 'catSelect'
        },
        {
          prop: 'warehouseReceiptStatus:',
          label: this.$t('orderMod.buyerOrderSynergy.warehouseReceiptStatus'),
          slot: 'warehouseReceiptStatus',
          type: 'slot'
        },
        {
          prop: 'orderStatus',
          slot: 'orderStatus',
          type: 'slot',
          label: this.$t('orderMod.buyerOrderSynergy.orderStatus')
        },
        {
          prop: 'orderType',
          slot: 'orderType',
          type: 'slot',
          label: this.$t('orderMod.buyerOrderSynergy.orderType')
        }
      ],
      queryParam: {},
      preFormObj: {},
      tableHeader: [
        {
          prop: 'orderStatus',
          label: this.$t('orderMod.buyerOrderSynergy.orderStatus'),
          width: 100,
          formattor: value => this.$getDictLabel('PURCHASE_ORDER', value)
        },
        {
          prop: 'organizationName',
          label: this.$t('dataConfMod.orgName'),
          width: 150
        },
        {
          prop: 'orderNumber',
          label: this.$t('orderMod.buyerOrderSynergy.orderNumber'),
          width: 150
        },
        {
          prop: 'buyerName',
          label: this.$t('orderMod.buyerOrderSynergy.buyerName'),
          width: 130
        },
        {
          prop: 'currency',
          label: this.$t('dataConfMod.settingGuide.step3[2]'),
          formattor: value => this.$getDictLabel('currency', value)
        },
        {
          prop: 'taxRate',
          label: this.$t('dataConfMod.settingGuide.step3[4]'),
          formattor: value => this.$getDictLabel('tax', value)
        },
        {
          prop: 'jitOrder',
          label: this.$t('orderMod.buyerOrderSynergy.jitOrder'),
          width: 100,
          formattor: value => this.$getDictLabel('YES_NO', value)
        },
        {
          prop: 'orderType',
          label: this.$t('orderMod.buyerOrderSynergy.orderType'),
          width: 100,
          formattor: value => this.$getDictLabel('ORDER_TYPE', value)
        },
        {
          prop: 'unitPriceContainingTax',
          label: this.$t('orderMod.orderIncludeTax'),
          width: 100
        },
        {
          prop: 'requirementDate',
          label: this.$t('orderMod.buyerOrderSynergy.requirementDateStr'),
          width: 150,
          dataType: 'dateTime'
        },
        {
          prop: 'creationDate',
          label: this.$t('orderMod.buyerOrderSynergy.creationDate'),
          width: 150,
          dataType: 'dateTime'
        },
        {
          prop: 'categoryName',
          label: this.$t('common.categoryName'),
          width: 150
        },
        {
          prop: 'level',
          label: this.$t('dataConfMod.categoryLevel'),
          width: 150,
          formattor: value => this.$getDictLabel('CATEGORY', value)
        },
        {
          prop: 'materialCode',
          label: this.$t('common.materialCode'),
          width: 150
        },
        {
          prop: 'materialName',
          label: this.$t('common.materialName'),
          width: 150
        },
        {
          prop: 'unit',
          label: this.$t('orderMod.buyerOrderSynergy.unit'),
          width: 150,
          formattor: value => this.$getDictLabel('unit', value)
        },
        {
          prop: 'comfirmTime',
          label: this.$t('orderMod.orderConfirmTime'),
          width: 150,
          dataType: 'dateTime'
        },
        {
          prop: 'warehouseReceiptStatus',
          label: this.$t('orderMod.buyerOrderSynergy.warehouseReceiptStatus'),
          width: 150
        },
        {
          prop: 'warehouseReceiptNumber',
          label: this.$t('orderMod.buyerOrderSynergy.warehouseReceiptNumber'),
          width: 150
        },
        {
          prop: 'warehouseReceiptQuantity',
          label: this.$t('orderMod.warehouseQuantity'),
          width: 150
        },
        {
          prop: 'warehouseReceiptTime',
          label: this.$t('orderMod.warehouseTime'),
          width: 150,
          dataType: 'dateTime'
        },
        {
          prop: 'receivedFactory',
          label: this.$t('orderMod.buyerOrderSynergy.receivedFactory'),
          width: 150
        },
        {
          prop: 'inventoryPlace',
          label: this.$t('orderMod.buyerOrderSynergy.inventoryPlace'),
          width: 150
        },
        {
          prop: 'comments',
          label: this.$t('dataConfMod.remark'),
          width: 150
        }
      ]
    }
  },
  updated () {
    this.defaultTableHeader = this.tableHeader
  },
  created () {
    // this.store.commit('loadDictionary', ['RECEIVED_STATUS', 'PURCHASE_ORDER', 'ORDER_TYPE']);
    ['currency', 'unit', 'tax'].forEach(item => {
      this.$store.commit('loadByBaseCode', item)
    })
    // this.store.commit('remote', 'ORG_TYPE', async () => {
    //   const { data } = await purchaseAnalysisApi.getOrgType()
    //   return data.map((item, index) => {
    //     const { organizationTypeCode, organizationTypeName, isDefault } = item
    //     return {
    //       id: `org_type_${index}`,
    //       label: organizationTypeName,
    //       value: organizationTypeCode
    //     }
    //   })
    // })
    // this.store.commit('remote', 'CATEGORY', async () => {
    //   const { data } = await purchaseAnalysisApi.getCategory()
    //   return data.map((item, index) => {
    //     const { dictItemCode, dictItemName, isDefault } = item
    //     return {
    //       id: `level_${index}`,
    //       label: dictItemName,
    //       value: +dictItemCode
    //     }
    //   })
    // })
    const { type, name, row, ...queryParams } = this.$attrs.params
    const { season, year, ...restParams } = queryParams
    const seasonMap = [
      ['01', '03'],
      ['04', '06'],
      ['07', '09'],
      ['10', '12']
    ]
    // 设置预制查询参数
    const preParams = {
      startMonth: `${year}-${season === 0 ? '01' : seasonMap[season - 1][0]}`,
      endMonth: `${year}-${season === 0 ? '12' : seasonMap[season - 1][1]}`,
      type,
      ...restParams
    }
    if (type === 5) {
      preParams.categoryId = row.categoryId || ''
    }
    if (type === 4) {
      const { month } = row
      const formatMonth = month.replace(/年/g, '-').replace(/月/g, '')
      const [year, m] = formatMonth.split('-')
      preParams.startMonth = `${year}-${m.padStart(2, 0)}`
      preParams.endMonth = `${year}-${m.padStart(2, 0)}`
    }
    this.preFormObj = preParams
    this.queryParam = preParams
  },
  mounted () {
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    doLayout () {
      console.log('[tab component doLayout]')
      this.$refs[this.gridId].doLayout()
    },
    selectHandle (node, value, scope) {
      const { fullPathId } = node
      scope.fullPathId = fullPathId
    },
    getQuerydata (v) {
      this.queryParam = { ...this.queryParam, ...v }
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    }
  }
}
</script>
<style scoped lang="scss">
.the_more_info_wrapper {
  .order-uploader {
    display: inline-block;
    margin: 0 10px;
  }
}
</style>
