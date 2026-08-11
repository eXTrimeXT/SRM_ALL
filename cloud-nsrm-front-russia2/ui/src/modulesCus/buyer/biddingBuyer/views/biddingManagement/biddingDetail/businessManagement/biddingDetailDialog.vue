<template>
  <SrmDialog
    :title="$t('bidMod.bidDetail1')"
    size="large"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
  >
    <FormWrapper
      :form-array="preArr"
      :pre-form-obj.sync="preFormObj"
      :select-dictionary="selectDictionary"
      @getFormData="getQueryData"
    />
    <el-table
      :data="itemList"
      style="width: 100%"
      border
      max-height="200px"
    >
      <el-table-column
        align="center"
        type="index"
        :label="$t('common.sort')"
        width="50"
      />
      <el-table-column
        align="center"
        prop="round"
        :label="$t('bidMod.bidingRound')"
        width="100"
        show-overflow-tooltip
      />
      <el-table-column
        align="center"
        prop="vendorName"
        :label="$t('common.vendorName')"
        min-width="150"
        show-overflow-tooltip
      />
      <!-- 包名 -->
      <!-- <el-table-column
        v-if="mergeFlag"
        align="center"
        prop="extPackageName"
        :label="$t('cusEntry.biddingSettings.bagName')"
        min-width="150"
        show-overflow-tooltip
      /> -->
      <el-table-column
        v-for="item in templateData"
        :key="item.columnCode"
        :prop="item.columnCode"
        :label="item.columnName"
        align="center"
        min-width="150"
        show-overflow-tooltip
      >
        <template slot-scope="scope">
          <dict-select
            v-if="item.columnType == 'LIST'"
            v-model="scope.row[item.columnCode]"
            :code="item.code"
            disabled
          />
          <span v-else>{{ scope.row[item.columnCode] }}</span>
        </template>
      </el-table-column>
      <!-- 组织报价原因 -->
      <el-table-column
        align="center"
        prop="extOrderReason"
        :label="$t('cusEntry.bidMod.extOrderReason')"
        min-width="150"
        show-overflow-tooltip
      />
      <el-table-column
        align="center"
        prop="extExchangeRate"
        :label="$t('bidMod.appraisRate')"
        min-width="150"
        show-overflow-tooltip
      />
    </el-table>

    <template #footer>
      <el-button @click="dialogVisible = false">
        {{ $t("common.cancel") }}
      </el-button>
    </template>
  </SrmDialog>
</template>

<script>
/**
 * 报价详情
 */
import FormWrapper from 'lib@/components/Table/FormWrapper'
export default {

  name: 'BiddingDetailDialog',

  components: { FormWrapper },

  props: {
    visible: {
      type: Boolean,
      required: true
    },
    editRow: {
      type: Object,
      required: true
    },
    mergeFlag: {
      type: Boolean,
      default: false
    }
  },

  data () {
    return {
      preArr: [
        { prop: 'round', label: this.$t('bidMod.bidingRound'), type: 'select' },
        { prop: 'vendorId', label: this.$t('common.vendorName'), type: 'select' }
      ],
      preFormObj: {},
      selectDictionary: {},
      templateData: [],
      itemList: []
    }
  },

  computed: {
    dialogVisible: {
      get: function () {
        return this.visible
      },
      set: function (val) {
        this.$emit('update:visible', val)
      }
    }
  },

  async created () {
    this.preFormObj = {
      projectId: this.editRow.projectId,
      vendorId: this.editRow.vendorId
    }
    await this.getOrderInfo()
    await this.getQueryData()
  },

  methods: {
    // 查询动态列名和轮次、供应商下拉项
    getOrderInfo () {
      this.$http({
        url: `/api-sou/ext/buyer/bid/init/getBusinessManagementOrderInfo?projectId=${this.editRow.projectId}`,
        method: 'GET',
        loading: true
      }).then(res => {
        if (res && res.data) {
          this.templateData = res.data.selectedList.map(item => {
            if (item.columnCode == 'extCurrency') {
              item.code = 'currency'
            }
            if (item.columnCode == 'extInvoiceType') {
              item.code = 'SOU_BIDPRICE_INVOICE_TYPE'
            }
            return item
          })
          let roundList = res.data.roundList.map(item => {
            return {
              key: item,
              label: item,
              value: item
            }
          })
          let vendorList = res.data.vendorList.map(item => {
            return {
              key: item.vendorId,
              label: item.vendorName,
              value: item.vendorId
            }
          })
          this.selectDictionary = {
            round: roundList,
            vendorId: vendorList
          }
        }
      })
    },
    /* 查询详情数据 */
    getQueryData (v) {
      let params = v ? { projectId: this.editRow.projectId, ...v } : this.preFormObj
      this.$http({
        url: '/api-sou/ext/buyer/bid/init/getOrderItem',
        method: 'POST',
        data: params,
        loading: true
      }).then(res => {
        if (res && res.data) {
          this.itemList = res.data
        }
      })
    }
  }
}
</script>
