<template>
  <el-container class="flex-container wrapper" direction="vertical">
    <el-main class="report-main">
      <FormWrapper
            :form-array="filterConfig"
            @getFormData="getQueryData"
          />
          <TableView 
          :ref="gridId" 
          :pre-query-data="queryParam"
          :table-header="tableHeader"  
          :adeptMeiQl="true"
          :pageEnabled="false"
          :open-custom-table="true"
        :checkbox="true"
        :comActive="$attrs['changeTab']"
          url="/api-cost/api-ql/CostLinkRawMaterialPrice/query" />
      <!-- <el-collapse v-model="activeDims">
        <el-collapse-item :title="$t('meeting.baseInfo')" name="1">
          
        </el-collapse-item>
      </el-collapse> -->
    </el-main>

    <!-- <CToolbar>
      <template slot="right">
        <el-button type="ghost" @click="back">
          {{ $t('common.backTo') }}
        </el-button>
      </template>
    </CToolbar> -->
  </el-container>
</template>

<script>
import { tabTodoMixin } from '@/utils/mixins'
import CToolbar from 'lib@/components/c-toolbar'
import TableView from 'lib@/components/Table/TableView'
import { transformMQL } from '@/library/utils/util'
import FormWrapper from 'lib@/components/Table/FormWrapper'

export default {
  name: 'MarketPartsPriceReport',

  components: {
    TableView,
    CToolbar,
    FormWrapper,
  },

  mixins: [tabTodoMixin],

  data() {
    return {
      gridId: 'list',
      activeDims: ['1'],
      queryParam:{},
      filterConfig: [
        // 物料编码
        { prop: 'materialCode', label: '物料编码' },
        { prop: 'materialName', label: '物料名称' },
        { prop: 'rawMaterialCode', label: '原材料编码' },
        { prop: 'rawMaterialName', label: '原材料名称' },
        // 市况类型
        {
          prop: 'marketType',
          label: this.$t('marketBudget.marketType'),
          type: 'dict',
          code: 'COST_LINK_MARKET_TYPE',
        },
      ],
      tableHeader: [
        {
          prop: 'linkPriceNo',
          label: '联动行情单号',
          minWidth: 150,
        },
        // 物料编码
        {
          prop: 'materialCode',
          label: '物料编码',
          minWidth: 150,
        },
        // 物料名称
        {
          prop: 'materialName',
          label: '物料名称',
          minWidth: 150,
        },
        // 原材料编码
        {
          prop: 'rawMaterialCode',
          label: '原材料编码',
          minWidth: 150,
        },
        // 原材料名称
        {
          prop: 'rawMaterialName',
          label: '原材料名称',
          minWidth: 150,
        },
        {
          prop: 'standardPriceBeginTime',
          label: '基准价行情开始时间',
          minWidth: 150,
        },
        {
          prop: 'standardPriceEndTime',
          label: '基准价行情结束时间',
          minWidth: 150,
        },
        // 上期行情未税单价
        {
          prop: 'notaxStandardPrice',
          label: '上期行情未税单价',
          minWidth: 150,
        },

        // 预估价行情开始时间
        {
          prop: 'estimatePriceBeginTime',
          label: '预估价行情开始时间',
          minWidth: 150,
        },
        // 预估价行情结束时间
        {
          prop: 'estimatePriceEndTime',
          label: '预估价行情结束时间',
          minWidth: 150,
        },
        // 本期行情未税单价
        {
          prop: 'notaxEstimatePrice',
          label: '本期行情未税单价',
          minWidth: 150,
        },
        // 币种
        {
          prop: 'currencyCode',
          label: '币种',
          minWidth: 150,
          formattor: val => this.$getDictLabel('currency', val),
        },

        // 投料重量
        {
          prop: 'rawMaterialWeight',
          label: '投料重量',
          minWidth: 150,
        },
        // 单位
        {
          prop: 'unit',
          label: '单位',
          minWidth: 150,
          formattor: val => this.$getDictLabel('unit', val),
        },
        // 市况联动类型
        {
          prop: 'marketType',
          label: '市况联动类型',
          minWidth: 150,
          formattor: val => this.$getDictLabel('COST_LINK_MARKET_TYPE', val),
        },
        // 联动频次
        {
          prop: 'frequency',
          label: '联动频次',
          minWidth: 150,
          formattor: val => this.$getDictLabel('COST_LINK_FREQUENCY', val),
        },
      ],
    }
  },

  computed: {
    urlParams() {
      return this.$attrs.params || {}
    },
    
  },

  created() {
    this.$nextTick(() => {
      this.getQueryData()
    })
  },

  methods: {
    getQueryData(params = {}) {

      let filter = {
        linkPriceId : {'eq': this.urlParams.row.linkPriceId},
        materialCode : {'contains': params.materialCode},
        materialName : {'contains': params.materialName},
        rawMaterialCode : {'contains': params.rawMaterialCode},
        rawMaterialName : {'contains': params.rawMaterialName},
        marketType : {'eq': params.marketType},
      }
      console.log('filter',filter)
      params.linkPriceId = this.urlParams.row.linkPriceId
      this.queryParam = transformMQL.listGetData('CostLinkRawMaterialPrice', params,'lastUpdateDate',undefined,'query',filter)
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    back() {
      this.$emit('tab-remove', this.$attrs['active-tab'])
    },
  },
}
</script>

<style lang="scss" scoped>
// .report-main{
//   height: 100%;
// }
::v-deep .el-collapse {
  height: 100%;
  > div {
    height: calc(100% - 40px);
    .el-collapse-item__wrap {
      height: calc(100% - 16px);
    }
    .el-collapse-item__content {
      height: 100%;
    }
  }
}
</style>
