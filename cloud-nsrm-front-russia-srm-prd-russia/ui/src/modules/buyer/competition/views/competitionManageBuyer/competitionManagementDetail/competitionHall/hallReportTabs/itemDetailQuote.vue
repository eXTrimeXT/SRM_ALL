<template>
  <div class="item-detail-quote">
    <FormWrapper :form-array="searchFormConfig" @getFormData="getQueryData" />

    <el-table
      :data="itemDetailQuoteData.slice((pageNum-1)*pageSize,pageNum*pageSize)"
      border
      max-height="251px"
      highlight-current-row
    >
      <el-table-column
        type="index"
        :label="$t('common.sort')"
        width="50"
      />

      <!--物料编码-->
      <el-table-column
        prop="itemCode"
        :label="$t('bidMod.itemCode')"
        min-width="150"
        show-overflow-tooltip
      />

      <!--物料名称-->
      <el-table-column
        prop="itemDesc"
        :label="$t('bidMod.itemName')"
        min-width="150"
        show-overflow-tooltip
      />

      <!--报价供应商-->
      <el-table-column
        prop="vendorName"
        :label="$t('bidMod.quotedSupplier')"
        min-width="150"
        show-overflow-tooltip
      />

      <!--未税单价-->
      <el-table-column
        align="right"
        prop="orderNotaxPrice"
        :label="$t('bidMod.quotenotaxPrice2')"
        min-width="150"
        show-overflow-tooltip
      />

      <!--报价时间-->
      <el-table-column
        prop="submitTime"
        :label="$t('bidMod.quotedTime')"
        min-width="150"
        show-overflow-tooltip
      />

      <!--IP地址-->
      <el-table-column
        prop="submitByIp"
        :label="$t('bidMod.ipAddress')"
        min-width="150"
        show-overflow-tooltip
      />

      <!--报价附件-->
      <!-- <SrmCommonFile
        type="table-column"
        :table-column-options="{
          label: $t('bidMod.finalOffer'),
          prop: 'docId',
          nameProp: 'fileName',
          minWidth: '150'
        }"
        readonly
      /> -->
    </el-table>

    <CPagination
      :pageNum="pageNum"
      :pageSize="pageSize"
      :total="itemDetailQuoteData.length"
      @current-change="currentChange"
      @size-change="sizeChange"
    />
  </div>
</template>

<script>
/**
 * 物料明细报价记录
 */
import { compBuyerHttp } from 'modb@/competition/api'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import CPagination from 'lib@/components/c-pagination'

export default {
  name: 'ItemDetailQuote',

  components: { FormWrapper, CPagination },

  props: {
    projectId: {
      type: [Number, String],
      default: ''
    },
    itemOptions: {
      type: Array,
      required: true
    },
    orderItemList: {
      type: Array,
      default: () => [],
      required: true
    },
    // 邀请供应商数据
    vendorInfoData: {
      type: Array,
      default: () => []
    },
    // 是否当前tab页
    isActiveTab: {
      type: Boolean,
      default: false
    }
  },

  data () {
    return {
      itemDetailQuoteData: [],
      searchParams: {},
      pageNum: 1,
      pageSize: 15
    }
  },

  computed: {
    searchFormConfig () {
      return [
        // 物料名称
        {
          prop: 'souItemId',
          label: this.$t('bidMod.targetDesc'),
          type: 'select',
          options: () => this.itemOptions.map(item => {
            return {
              value: item.souItemId,
              label: item.itemDesc
            }
          })
        },
        // 供应商
        {
          prop: 'vendorId',
          label: this.$t('bidMod.provider'),
          type: 'select',
          options: () => this.vendorInfoData.map(item => {
            return {
              value: item.vendorId,
              label: item.vendorName
            }
          })
        }
      ]
    }
  },

  watch: {
    isActiveTab: {
      handler (newValue, oldValue) {
        // 切换到当前标签页
        if (newValue && !oldValue) {
          this.getQueryData()
        }
      },
      immediate: true
    },
    orderItemList: {
      handler (nVal) {
        if (nVal) {
          this.itemDetailQuoteData = JSON.parse(JSON.stringify(nVal))
        }
      },
      immediate: true,
      deep: true
    }
  },

  methods: {
    /* 查询数据 */
    async getQueryData (val = {}) {
      this.searchParams = Object.assign({}, val)
      const { souItemId, vendorId } = this.searchParams
      if (souItemId && vendorId) {
        this.itemDetailQuoteData = this.orderItemList.filter(item => item.souItemId === souItemId && item.vendorId === vendorId)
      } else if (souItemId && !vendorId) {
        this.itemDetailQuoteData = this.orderItemList.filter(item => item.souItemId === souItemId)
      } else if (!souItemId && vendorId) {
        this.itemDetailQuoteData = this.orderItemList.filter(item => item.vendorId === vendorId)
      } else {
        this.itemDetailQuoteData = JSON.parse(JSON.stringify(this.orderItemList))
      }
    },
    currentChange (value) {
      this.pageNum = value
    },
    sizeChange (value) {
      this.pageSize = value
    }
  }
}
</script>
