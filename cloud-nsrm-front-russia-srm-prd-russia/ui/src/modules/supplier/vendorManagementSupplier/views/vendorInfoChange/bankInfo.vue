// 只有供应商变更才用这个业务组件,这个是银行信息模块
<template>
  <div class="bankInfo">
    <p
      v-if="curOpt !== 'view'"
      class="sub_header"
    >
      <el-button
        v-if="!disabledBol"
        type="primary"
        class="detail-pbtn"
        @click="addBank"
      >
        {{ $t('common.new') }}
      </el-button>
    </p>
    <el-table
      ref="bankTable"
      :disabled="disabledBol"
      :data="bankData"
      :row-class-name="rowClassName"
      style="width: 100%"
      border
      max-height="250px"
    >
      <!-- 银行代码 -->
      <el-table-column
        align="center"
        prop="bankCode"
        min-width="150"
        :show-overflow-tooltip="true"
      >
        <template slot="header">
          <i class="toRequired">*</i>{{ $t('components.bank.bankCode') }}
        </template>
        <template slot-scope="scope">
          <QuickSearch
            :disabled="disabledBol"
            :show-input="scope.row.bankCode"
            propKey="bankNum"
            show-key="branchBankNum"
            :scope-data="scope.row"
            :class="bankDataY[scope.$index]?(bankDataY[scope.$index].bankCode!=scope.row.bankCode?'redColorFont':null):'redColorFont'"
            name="ceea_base_erp_branch_bank_info"
            @close-quicksearch="getBankObj"
          />
        </template>
      </el-table-column>
      <!-- 银行名称 -->
      <el-table-column
        align="center"
        prop="bankName"
        min-width="150"
        :show-overflow-tooltip="true"
      >
        <template slot="header">
          <i class="toRequired">*</i>{{ $t('components.bank.bankName') }}
        </template>
        <template slot-scope="scope">
          <div
            :style="bankDataY[scope.$index]?(bankDataY[scope.$index].bankName!=scope.row.bankName?'color:red':null):'color:red'"
          >
            {{ scope.row.bankName }}
          </div>
        </template>
      </el-table-column>
      <!-- 开户行名称 -->
      <el-table-column
        align="center"
        prop="openingBank"
        min-width="160"
        :show-overflow-tooltip="true"
      >
        <template slot="header">
          <i class="toRequired">*</i>{{ $t('components.bank.branchBankName') }}
        </template>
        <template slot-scope="scope">
          <div
            :style="bankDataY[scope.$index]?(bankDataY[scope.$index].openingBank!=scope.row.openingBank?'color:red':null):'color:red'"
          >
            {{ scope.row.openingBank }}
          </div>
        </template>
      </el-table-column>
      <!-- 分行编码 -->
      <el-table-column
        align="center"
        prop="unionCode"
        min-width="150"
        :show-overflow-tooltip="true"
      >
        <template slot="header">
          <i class="toRequired">*</i>{{ $t('components.bank.unionCode') }}
        </template>
        <template slot-scope="scope">
          <div
            :style="bankDataY[scope.$index]?(bankDataY[scope.$index].unionCode!=scope.row.unionCode?'color:red':null):'color:red'"
          >
            {{ scope.row.unionCode }}
          </div>
        </template>
      </el-table-column>
      <!-- 账户名称 -->
      <el-table-column
        align="center"
        prop="bankAccountName"
        show-overflow-tooltip
        :label="$t('vendorMod.bankAccountName')"
        width="200"
      >
        <template slot-scope="scope">
          <el-input
            v-if="scope.row.opType === 'add' || scope.row.opType === 'update'"
            v-model="scope.row.bankAccountName"
            :class="bankDataY[scope.$index]?(bankDataY[scope.$index].bankAccountName!=scope.row.bankAccountName?'redColorFont':null):'redColorFont'"
            :disabled="disabledBol"
          />
          <span v-else><div
            :style="bankDataY[scope.$index]?(bankDataY[scope.$index].bankAccountName!=scope.row.bankAccountName?'color:red':null):'color:red'"
          >{{ scope.row.bankAccountName }}</div></span>
        </template>
      </el-table-column>
      <!-- 银行账号 -->
      <el-table-column
        align="center"
        prop="bankAccount"
        :label="$t('vendorMod.bankAccount')"
        width="200"
      >
        <template slot-scope="scope">
          <el-input
            v-if="scope.row.opType === 'add' || scope.row.opType === 'update'"
            v-model="scope.row.bankAccount"
            :class="bankDataY[scope.$index]?(bankDataY[scope.$index].bankAccount!=scope.row.bankAccount?'redColorFont':null):'redColorFont'"
            :disabled="disabledBol"
          />
          <span v-else><div
            :style="bankDataY[scope.$index]?(bankDataY[scope.$index].bankAccount!=scope.row.bankAccount?'color:red':null):'color:red'"
          >{{ scope.row.bankAccount }}</div></span>
        </template>
      </el-table-column>
      <!-- 币种 -->
      <el-table-column
        align="center"
        prop="currencyCode"
        :label="$t('vendorMod.currencyCode')"
        width="120"
        :show-overflow-tooltip="true"
      >
        <template slot-scope="scope">
          <DictSelect
            v-model="scope.row.currencyCode"
            code="BID_TENDER_CURRENCY"
            :class="bankDataY[scope.$index]?(bankDataY[scope.$index].currencyCode!=scope.row.currencyCode?'redColorFont':null):'redColorFont'"
            :disabled="disabledBol"
          />
        </template>
      </el-table-column>
      <el-table-column
        align="center"
        prop="ceeaMainAccount"
        :label="$t('components.bank.isMain')"
        width="100"
        :show-overflow-tooltip="true"
      >
        <template slot-scope="scope">
          <el-checkbox
            v-model="scope.row.ceeaMainAccount"
            :disabled="disabledBol"
            :class="bankDataY[scope.$index]?(bankDataY[scope.$index].ceeaMainAccount!=scope.row.ceeaMainAccount?'redColorFont':null):'redColorFont'"
            true-label="Y"
            false-label="N"
          />
        </template>
      </el-table-column>
      <!-- 启用 -->
      <el-table-column
        align="center"
        prop="ceeaEnabled"
        :label="$t('components.bank.isActive')"
        width="100"
        :show-overflow-tooltip="true"
      >
        <template slot-scope="scope">
          <el-checkbox
            v-model="scope.row.ceeaEnabled"
            :disabled="disabledBol"
            :class="bankDataY[scope.$index]?(bankDataY[scope.$index].ceeaEnabled!=scope.row.ceeaEnabled?'redColorFont':null):'redColorFont'"
            true-label="Y"
            false-label="N"
          />
        </template>
      </el-table-column>
      <!-- 拓展字段 [[-->
      <template v-if="bankDimFieldContexts.length > 0">
        <el-table-column
          v-for="col in bankDimFieldContexts"
          :key="col.fieldId"
          :prop="col.fieldCode"
          :label="col.languageCode ? $t(col.languageCode) : col.fieldName"
          width="110px"
        >
          <template slot-scope="scope">
            <el-input
              v-if="scope.row.opType === 'add' || scope.row.opType === 'update'"
              v-model="scope.row[col.fieldCode]"
              :class="bankDataY[scope.$index]?(bankDataY[scope.$index][col.fieldCode]!=scope.row[col.fieldCode]?'redColorFont':null):'redColorFont'"
            />
            <span v-else><div
              :style="bankDataY[scope.$index]?(bankDataY[scope.$index][col.fieldCode]!=scope.row[col.fieldCode]?'color:red':null):'color:red'"
            >{{ scope.row[col.fieldCode] }}</div></span>
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
          <el-button
            v-if="scope.row.opType === 'delete' || scope.row.opType === ''"
            type="text"
            :disabled="disabledBol"
            @click="bankRowHandel(scope.$index, scope.row, 'update')"
          >
            {{ $t('common.edit') }}
          </el-button>
          <el-button
            v-if="scope.row.opType === 'delete' || scope.row.opType === 'update'"
            type="text"
            :disabled="disabledBol"
            @click="bankRowHandel(scope.$index, scope.row, 'cancel')"
          >
            {{ $t('common.cancel') }}
          </el-button>
          <el-button
            type="text"
            :disabled="disabledBol"
            @click="bankRowDelHandel(scope.$index, scope.row)"
          >
            {{ $t('common.delete') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>
<script>

import QuickSearch from 'lib@/components/QuickSearch'

export default {
  name: 'BankInfo',
  components: {
    QuickSearch
  },
  model: {
    prop: 'data',
    event: 'change'
  },
  props: {
    disabledBol: {
      type: Boolean,
      default () {
        return []
      }
    },
    bankData: {
      type: Array,
      default () {
        return []
      }
    },
    bankDataY: {
      type: Array,
      default () {
        return []
      }
    },
    rowClassName: {
      type: Function,
      default () {
        return null
      }
    },
    bankDimFieldContexts: {
      type: Array,
      default () {
        return []
      }
    },
    curOpt: {
      type: String,
      default () {
        return 'add'
      }
    }
  },
  data () {
    return {

    }
  },
  inject: ['addBank', 'getBankObj', 'bankRowHandel', 'bankRowDelHandel'],
  computed: {

  },
  watch: {

  },
  mounted () {

  },
  methods: {

  }
}
</script>

<style scope>
.formClassAll form{
  padding-left: 18px
}
.changeTitle{
  background-color: #F6F6F6;
  font-size: 14px ;
  color: #393E45 ;
  overflow: hidden;
  line-height: 40px;
  margin-bottom:20px;
  font-weight: 400;
}
.changeTitle i{
  width: 4px;
  height: 18px;
  background-color: #0077FF;
  margin: 11px 10px 11px 16px;
  display: block;
  float: left;
}
</style>
