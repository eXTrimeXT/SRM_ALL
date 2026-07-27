<template>
  <div class="quote-info">
    <div class="the_display_content">
      <SrmRow style="margin-bottom: 18px;">
        <SrmCol :init-col="3">
          <span>{{ $t('bidMod.itemDesc') }} &nbsp;</span>
          <el-input v-model="itemDesc" style="width: 80%" />
        </SrmCol>
        <SrmCol :init-col="3">
          <el-button
            type="primary"
            @click="getOrderDetails"
          >
            {{ $t('common.search') }}
          </el-button>
        </SrmCol>
      </SrmRow>
    </div>

    <vxe-table
      ref="xTable"
      border
      show-overflow="tooltip"
      keep-source
      align="left"
      max-height="500"
      :data="orderLineList"
    >
      <!--序号-->
      <vxe-column
        type="seq"
        :title="$t('common.sort')"
        width="60"
      />

      <!--业务实体-->
      <vxe-column
        field="orgOuName"
        :title="$t('bid_mod.businessEntity')"
        width="150"
      />

      <!--库存组织-->
      <vxe-column
        field="orgInvName"
        :title="$t('bid_mod.inv')"
        width="150"
      />

      <!--有无料号-->
      <vxe-column
        field="noCodeItem"
        title="有无料号"
        width="100"
      >
        <template #default="{ row }">
          {{ $getDictLabel('YES_OR_NO', row.noCodeItem) }}
        </template>
      </vxe-column>

      <!--物料编码-->
      <vxe-column
        field="itemCode"
        :title="$t('bidMod.targetNum')"
        width="150"
      />

      <!--物料名称-->
      <vxe-column
        field="itemDesc"
        :title="$t('bidMod.targetDesc')"
        min-width="150"
      />

      <!--单位-->
      <vxe-column
        field="unit"
        title="单位"
        width="100"
      >
        <template #default="{ row }">
          {{ $getDictLabel('unit', row.unit) }}
        </template>
      </vxe-column>

      <!--采购品类-->
      <vxe-column
        field="categoryName"
        title="采购品类"
        width="100"
      />

      <!--需求数量-->
      <vxe-column
        field="requireQuantity"
        :title="$t('bidMod.demandQuantity2')"
        width="150"
      />

      <!--币种-->
      <vxe-column
        field="standardCurrency"
        title="币种"
        min-width="140"
      >
        <template #default>
          {{ $getDictLabel('currency', baseInfo.standardCurrency) }}
        </template>
      </vxe-column>

      <!--税率-->
      <vxe-column
        field="taxKey"
        title="税率"
        min-width="120"
      >
        <template #default="{ row }">
          {{ $getDictLabel('tax', row.taxKey) }}
        </template>
      </vxe-column>

      <!--未税单价-->
      <vxe-column
        field="orderNotaxPrice"
        title="未税单价"
        width="100"
      />

      <!--含税单价-->
      <vxe-column
        field="orderTaxPrice"
        title="含税单价"
        width="100"
      />

      <!--未税金额-->
      <vxe-column
        field="orderNotaxTotalPrice"
        title="未税金额"
        min-width="110"
      />

      <!--含税金额-->
      <vxe-column
        field="orderTaxTotalPrice"
        title="含税金额"
        min-width="110"
      />

      <!--行附件-->
      <!-- <vxe-column
        field="docId"
        title="行附件"
        min-width="150"
      >
        <template #default="{ row }">
          <SrmCommonFile
            :default-file="{
              fileId: row.docId,
              fileName: row.fileName
            }"
            readonly
          />
        </template>
      </vxe-column> -->

      <!--报价时间-->
      <vxe-column
        field="submitTime"
        title="报价时间"
        min-width="160"
      />
    </vxe-table>
  </div>
</template>

<script>
/**
 * 报价明细
 */
import { carVendorHttp } from 'mods@/competitionSupplier/api'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { transformMQL } from 'lib@/utils/util'

export default {
  name: 'QuoteInfo',

  inject: ['attrsParamsRow'],

  components: { FormWrapper },

  props: {
    baseInfo: {
      type: Object,
      default: () => { /* noting */ }
    },
    // 是否当前tab页
    isCurrentActiveTab: {
      type: Boolean,
      default: false
    }
  },

  data () {
    return {
      orderLineList: [],
      itemDesc: ''
    }
  },

  watch: {
    isCurrentActiveTab: {
      handler (newValue, oldValue) {
        // 切换到当前标签页
        if (newValue && !oldValue) {
          this.getOrderDetails()
        }
      },
      immediate: true
    }
  },

  methods: {
    async getOrderDetails () {
      if (!this.attrsParamsRow.projectId) {
        return
      }
      let transfromParams = transformMQL.save('AuctSouOrderForVendor', {
        filter: {
          projectId: {
            eq: this.attrsParamsRow.projectId
          },
          itemDesc: {
            contains: this.itemDesc
          }
        },
        page: {
          sort: 'lastUpdateDate desc',
          pageNum: 1,
          pageSize: 1000
        }
      }, 'listVendorOrderItemHis')
      const response = await carVendorHttp.orderQuotation.listVendorOrderItemHis(transfromParams)
      if (response && response.data) {
        this.orderLineList = (response.data.records || []).map(item => ({
          ...item,
          ...item.auctSouOrderItem,
          ...item.souItem
        }))
      }
    }
  }
}
</script>
