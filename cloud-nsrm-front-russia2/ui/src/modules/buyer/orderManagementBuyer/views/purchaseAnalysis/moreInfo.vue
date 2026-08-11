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
        @synchronous-value="syncFilterParams"
      >
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
            :key="scope.organizationTypeCode"
            ref="selector"
            v-model="scope.organizationId"
            :parent-id="-1"
            :placeholder="$t('common.pleaseSelect')"
            :node-type="scope.organizationTypeCode"
            :scope="scope"
            @select="selectHandle"
          />
        </template>
        <!-- 年份 -->
        <template #year="{ scope }">
          <el-date-picker
            v-model="scope.year"
            class="year"
            type="year"
            value-format="yyyy"
          />
        </template>
        <!-- 季度 -->
        <template #season="{ scope }">
          <dict-select
            v-model="scope.season"
            code="SEASON"
          />
        </template>
      </FormWrapper>

      <TableView
        :ref="gridId"
        :row-index="false"
        :table-header="tableHeader"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :auto-query="false"
        :source="source"
        :open-custom-table="true"
      />
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import OrganizationSelector from 'lib@/components/organization-selector'
import moreInfoDetail from './moreInfoDetail.vue'
import { purchaseAnalysisApi } from 'modb@/orderManagementBuyer/api'
export default {
  name: 'MoreInfo',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    OrganizationSelector
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      tableName: 'moreInfo',
      defaultTableHeader: [],
      filterParams: {},
      pageSize: 15,
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
          prop: 'season',
          label: this.$t('time.quarter'),
          slot: 'season',
          type: 'slot'
        },
        {
          prop: 'year',
          label: this.$t('dataConfMod.year'),
          slot: 'year',
          type: 'slot'
        }
      ],
      queryParam: {},
      preFormObj: {},
      tableHeader: []
    }
  },
  updated () {
    this.defaultTableHeader = this.tableHeader
  },
  created () {
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
    //       value: dictItemCode
    //     }
    //   })
    // })
    const { type, name, ...queryParams } = this.$attrs.params
    // 获取列表接口
    const sourceArray = [
      'queryOrderWarehousing',
      'queryOrderPunctuality',
      'queryOrderConfirm',
      'queryOrderPunctualityYear',
      'getPurchaseCategoryDetail'
    ]
    const sourceName = sourceArray[type - 1]
    this.source = purchaseAnalysisApi[sourceName]
    // 设置列表表头
    const tableHeaderArray = [
      [
        {
          prop: 'num',
          label: this.$t('bidMod.rank'),
          width: 80
        },
        {
          prop: 'organizationName',
          label: this.$t('dataConfMod.orgName')
        },
        {
          prop: 'purchaseAmount',
          label: this.$t('dataConfMod.purchaseAmount'),
          width: 150
        },
        {
          prop: 'warehousingAmount',
          label: this.$t('dataConfMod.warehousingAmount'),
          width: 150
        },
        {
          prop: 'rate',
          label: this.$t('dataConfMod.exacutiveRate'),
          width: 150
        },
        {
          prop: 'operation',
          label: this.$t('common.operation'),
          width: 150,
          btnStyle: 'text',
          fixed: 'right',
          showType: 'buttons',
          buttons: [
            {
              callback: row => this.detailView(row),
              formattor: () =>
                this.$t('common.view') +
                `${name}` +
                this.$t('dataConfMod.detail')
            }
          ]
        }
      ],
      [
        {
          prop: 'num',
          label: this.$t('bidMod.rank'),
          width: 80
        },
        {
          prop: 'organizationName',
          label: this.$t('dataConfMod.orgName')
        },
        {
          prop: 'purchaseAmount',
          label: this.$t('dataConfMod.purchaseAmount'),
          width: 150
        },
        {
          prop: 'warehousingAmount',
          label: this.$t('dataConfMod.onTimeStorageAmount'),
          width: 150
        },
        {
          prop: 'rate',
          label: this.$t('dataConfMod.onTimeRate'),
          width: 150
        },
        {
          prop: 'operation',
          label: this.$t('common.operation'),
          width: 150,
          btnStyle: 'text',
          fixed: 'right',
          showType: 'buttons',
          buttons: [
            {
              callback: row => this.detailView(row),
              formattor: () =>
                this.$t('common.view') +
                `${name}` +
                this.$t('dataConfMod.detail')
            }
          ]
        }
      ],
      [
        {
          prop: 'num',
          label: this.$t('bidMod.rank'),
          width: 80
        },
        {
          prop: 'organizationName',
          label: this.$t('dataConfMod.orgName')
        },
        {
          prop: 'orderNumber',
          label: this.$t('orderMod.purOrderVolume'),
          width: 150
        },
        {
          prop: 'confirmNumber',
          label: this.$t('orderMod.confirmOrderVolume'),
          width: 150
        },
        {
          prop: 'rate',
          label: this.$t('dataConfMod.onTimeRate'),
          width: 150
        },
        {
          prop: 'operation',
          label: this.$t('common.operation'),
          width: 150,
          btnStyle: 'text',
          fixed: 'right',
          showType: 'buttons',
          buttons: [
            {
              callback: row => this.detailView(row),
              formattor: () =>
                this.$t('common.view') +
                `${name}` +
                this.$t('dataConfMod.detail')
            }
          ]
        }
      ],
      [
        {
          prop: 'month',
          label: this.$t('components.month')
        },
        {
          prop: 'orderAmount',
          label: this.$t('dataConfMod.purchaseAmount')
        },
        {
          prop: 'deliveryAmount',
          label: this.$t('dataConfMod.onTimeStorageAmount')
        },
        {
          prop: 'rate',
          label: this.$t('dataConfMod.onTimeRate')
        },
        {
          prop: 'operation',
          label: this.$t('common.operation'),
          width: 150,
          btnStyle: 'text',
          fixed: 'right',
          showType: 'buttons',
          buttons: [
            {
              callback: row => this.detailView(row),
              formattor: () =>
                this.$t('common.view') +
                `${name}` +
                this.$t('dataConfMod.detail')
            }
          ]
        }
      ],
      [
        {
          prop: 'num',
          label: this.$t('bidMod.rank'),
          width: 80
        },
        {
          prop: 'categoryName',
          label: this.$t('common.categoryName')
        },
        {
          prop: 'purchaseAmount',
          label: this.$t('dataConfMod.purchaseAmount')
        },
        {
          prop: 'warehousingAmount',
          label: this.$t('dataConfMod.warehousingAmount')
        },
        {
          prop: 'rate',
          label: this.$t('dataConfMod.catePurchaseProportion')
        },
        {
          prop: 'operation',
          label: this.$t('common.operation'),
          width: 180,
          btnStyle: 'text',
          fixed: 'right',
          showType: 'buttons',
          buttons: [
            {
              callback: row => this.detailView(row),
              formattor: () =>
                this.$t('common.view') +
                `${name}` +
                this.$t('dataConfMod.detail')
            }
          ]
        }
      ]
    ]
    this.tableHeader = tableHeaderArray[type - 1]
    // 设置预制查询参数
    this.preFormObj = queryParams
    this.queryParam = queryParams
    // 设置表格名称
    this.tableName = `moreInfo_${type}`
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
    syncFilterParams (values) {
      this.filterParams = values
    },
    detailView (row) {
      const { type, name, ...queryParams } = this.$attrs.params
      this.$emit('tab-add', {
        title: `${name}` + this.$t('dataConfMod.detail'),
        component: moreInfoDetail,
        name: `moreInfoDetail_${type}`,
        params: { ...queryParams, ...this.filterParams, type, row }
      })
    },
    selectHandle (node, value, scope) {
      const { fullPathId } = node
      scope.fullPathId = fullPathId
    },
    getQuerydata (v) {
      this.queryParam = { ...this.queryParam, ...v }
      if (!this.queryParam.organizationTypeCode) {
        this.$message({
          message: this.$t('dataConfMod.msgOrgTypeName'),
          type: 'error'
        })
        return
      }
      if (!this.queryParam.season && this.queryParam.season !== 0) {
        this.$message({
          message: this.$t('time.msgSelQuarter'),
          type: 'error'
        })
        return
      }
      if (!this.queryParam.year) {
        this.$message({
          message: this.$t('time.msgSelYear'),
          type: 'error'
        })
        return
      }
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
