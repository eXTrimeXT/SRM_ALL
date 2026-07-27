// 只有供应商变更才用这个业务组件,这个是财务信息模块
<template>
  <div class="bankInfo">
    <p v-if="!disabledBol" class="sub_header">
      <el-button type="primary" @click="addFinance">
        {{
          $t('common.new')
        }}
      </el-button>
    </p>
    <el-table
      ref="financeTable"
      :data="financeInfoData"
      style="width: 100%"
      border
      max-height="250px"
    >
      <!-- 引入组织 -->
      <el-table-column
        align="center"
        prop="fullPathId"
        :label="$t('vendorMod.ceeaOrgName2')"
        min-width="200"
      >
        <template slot-scope="scope">
          <OrganizationSelector
            v-model="scope.row.orgId"
            :parentId="-1"
            nodeType="OU"
            :placeholder="$t('common.pleaseSelect')"
            :scope="scope.row"
            :disabled="disabledBol"
            :class="financeInfoDataY[scope.$index]?(financeInfoDataY[scope.$index].orgId!=scope.row.orgId?'redColorFont':null):'redColorFont'"
            @select="addOrgHandleAccounting"
          />
        </template>
      </el-table-column>
      <!-- 工厂代码 -->
      <el-table-column
        align="center"
        prop="factoryCode"
        :label="$t('vendorMod.factoryCode')"
        width="150"
      >
        <template slot-scope="scope">
          <el-input
            v-model="scope.row.factoryCode"
            :disabled="disabledBol"
            :class="financeInfoDataY[scope.$index]?(financeInfoDataY[scope.$index].factoryCode!=scope.row.factoryCode?'redColorFont':null):'redColorFont'"
            onKeyUp="value=value.replace(/[^\w\\/]/ig,'')"
            maxlength="50"
            show-word-limit
          />
        </template>
      </el-table-column>
      <!-- 结算币种 -->
      <el-table-column
        align="center"
        prop="clearCurrency"
        width="150"
      >
        <template slot="header">
          <i class="toRequired">*</i>{{ $t('vendorMod.clearCurrency') }}
        </template>
        <template slot-scope="scope">
          <DictSelect
            v-model="scope.row.clearCurrency"
            code="BID_TENDER_CURRENCY"
            :disabled="disabledBol"
            :class="financeInfoDataY[scope.$index]?(financeInfoDataY[scope.$index].clearCurrency!=scope.row.clearCurrency?'redColorFont':null):'redColorFont'"
          />
        </template>
      </el-table-column>
      <!-- 付款方式 -->
      <el-table-column
        align="center"
        prop="paymentMethod"
        width="150"
      >
        <template slot="header">
          <i class="toRequired">*</i>{{ $t('vendorMod.paymentMethod') }}
        </template>
        <template slot-scope="scope">
          <DictSelect
            v-model="scope.row.paymentMethod"
            code="PAYMENT_METHOD"
            :disabled="disabledBol"
            :class="financeInfoDataY[scope.$index]?(financeInfoDataY[scope.$index].paymentMethod!=scope.row.paymentMethod?'redColorFont':null):'redColorFont'"
          />
        </template>
      </el-table-column>
      <!-- 付款账期 -->
      <el-table-column
        align="center"
        prop="paymentTerms"
        width="150"
      >
        <template slot="header">
          <i class="toRequired">*</i>{{ $t('vendorMod.paymentTerms') }}
        </template>
        <template slot-scope="scope">
          <DictSelect
            v-model="scope.row.paymentTerms"
            code="PAYMENT_TERMS"
            :disabled="disabledBol"
            :class="financeInfoDataY[scope.$index]?(financeInfoDataY[scope.$index].paymentTerms!=scope.row.paymentTerms?'redColorFont':null):'redColorFont'"
          />
        </template>
      </el-table-column>
      <!-- 拓展字段 [[-->
      <template v-if="financeDimFieldContexts.length > 0">
        <el-table-column
          v-for="col in financeDimFieldContexts"
          :key="col.fieldId"
          :prop="col.fieldCode"
          :label="col.languageCode ? $t(col.languageCode) : col.fieldName"
          width="110px"
        >
          <template slot-scope="scope">
            <el-input v-model="scope.row[col.fieldCode]" :disabled="disabledBol" />
          </template>
        </el-table-column>
      </template>

      <!-- 拓展字段 ]]]-->
      <!-- 操作 -->
      <el-table-column
        align="center"
        prop="operation"
        :label="$t('common.operation')"
        width="100"
        fixed="right"
      >
        <template slot-scope="scope">
          <el-button type="text" :disabled="disabledBol" @click="financeDel(scope.$index, scope.row)">
            {{
              $t('common.delete')
            }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>
<script>

import QuickSearch from 'lib@/components/QuickSearch'
import OrganizationSelector from 'lib@/components/organization-selector'

export default {
  name: 'BankInfo',
  components: {
    QuickSearch,
    OrganizationSelector
  },
  model: {
    prop: 'data',
    event: 'change'
  },
  props: {
    disabledBol: {
      type: Boolean
    },
    financeInfoData: {
      type: Array,
      default () {
        return []
      }
    },
    financeInfoDataY: {
      type: Array,
      default () {
        return []
      }
    },
    financeDimFieldContexts: {
      type: Array,
      default () {
        return []
      }
    }
  },
  data () {
    return {

    }
  },
  inject: ['addFinance'],
  computed: {

  },
  watch: {

  },
  mounted () {

  },
  methods: {
    addOrgHandleAccounting (e, dd, scope) {
      scope.orgId = e ? e.organizationId : null
      scope.orgCode = e ? e.organizationCode : ''
      scope.orgName = e ? e.organizationName : ''
      scope.fullPathId = e ? e.fullPathId : ''
    },
    financeDel (index, row) {
      this.financeInfoData.splice(index, 1)
    }
  }
}
</script>

<style scope>

</style>
