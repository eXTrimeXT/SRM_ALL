<template>
  <SrmDialog
    :title="$t('paymentType.paymentType')"
    :visible.sync="dialogVisible"
    append-to-body
    size="large"
    :close-on-click-modal="false"
    :before-close="beforeClose"
  >
    <div
      v-if="!readonly"
      style="margin-bottom: 15px"
    >
      <!--新增-->
      <el-button
        type="primary"
        @click="addRow"
      >
        {{ $t("common.add") }}
      </el-button>
    </div>

    <el-table
      ref="paymentTypeTable"
      :data="paymentTypeTableData"
      border
      height="251px"
    >
      <el-table-column
        type="index"
        :label="$t('common.sort')"
        width="50"
      />

      <!--t 付款账期-->
      <el-table-column
        :prop="keyMap.paymentPeriod"
        :label="$t('paymentType.paymentDay1')"
        min-width="150"
        :render-header="_addStarToColumn"
      >
        <template v-slot="scope">
          <DictSelect
            v-model="scope.row[keyMap.paymentPeriod]"
            code="PAYMENT_PERIOD"
            :disabled="readonly"
          />
        </template>
      </el-table-column>

      <!--t 付款条件-->
      <el-table-column
        :prop="keyMap.paymentCondition"
        :label="$t('contractMod.payExplain')"
        min-width="150"
        :render-header="_addStarToColumn"
      >
        <template v-slot="scope">
          <DictSelect
            v-model="scope.row[keyMap.paymentCondition]"
            code="payExplain"
            :disabled="readonly"
            custom-select-type="payExplain"
            @change-value="(value, dictItem) => paymentConditionIdChange(dictItem, scope.row)"
          />
        </template>
      </el-table-column>

      <!--t 付款方式-->
      <el-table-column
        :prop="keyMap.paymentMode"
        :label="$t('paymentType.paymentWay')"
        min-width="150"
        :render-header="_addStarToColumn"
      >
        <template v-slot="scope">
          <DictSelect
            v-model="scope.row[keyMap.paymentMode]"
            code="PAYMENT_MODE"
            :disabled="readonly"
          />
        </template>
      </el-table-column>

      <!--t 付款比例-->
      <el-table-column
        :prop="keyMap.paymentProportion"
        :label="$t('bidMod.payRatio')"
        min-width="100"
        :render-header="_addStarToColumn"
      >
        <template v-slot="scope">
          <el-input
            v-model="scope.row[keyMap.paymentProportion]"
            v-input-format="{ type: 'number', negative: false, zero: false }"
            max="100"
            :disabled="readonly"
          />
        </template>
      </el-table-column>

      <!--t 付款阶段-->
      <el-table-column
        :prop="keyMap.paymentPhase"
        :label="$t('bidMod.payStage')"
        min-width="100"
        :render-header="_addStarToColumn"
      >
        <template v-slot="scope">
          <DictSelect
            v-model="scope.row[keyMap.paymentPhase]"
            code="PAYMENT_STAGE"
            :disabled="readonly"
          />
        </template>
      </el-table-column>

      <!--t 操作-->
      <el-table-column
        v-if="!readonly"
        prop="operation"
        :label="$t('bidMod.operation')"
        width="60"
      >
        <template v-slot="scope">
          <el-button
            type="text"
            @click="deleteRow(scope.$index, scope)"
          >
            {{ $t("common.delete") }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <div
      slot="footer"
      class="dialog-footer"
    >
      <el-button @click="cancel">
        {{ $t('common.cancel') }}
      </el-button>
      <!--提交-->
      <el-button
        v-if="!readonly"
        type="primary"
        @click="savePaymentType"
      >
        {{ $t('common.confirm') }}
      </el-button>
    </div>
  </SrmDialog>
</template>

<script>
/**
 * 付款条款弹窗
 */
import { getApiByBusinessType, getQueryParamsByBusinessType, mappingPropByBusinessTypeAndKey } from './utils'
import { validateRequiredColumn } from 'lib@/mixins/addStarToColumn'
import { validatorBusinessType } from 'lib@/composition/origin/composition'

export default {
  name: 'PaymentTypeDialog',

  props: {
    // 业务类型
    businessType: {
      type: String,
      // 允许为空。只是查看
      validator: value => !value || (value && validatorBusinessType(value))
    },
    visible: {
      type: Boolean
    },
    // 先变更editRow再变更visible。除了paymentList都是接口入参
    editRow: {
      type: Object,
      required: false
    },
    readonly: {
      type: Boolean,
      required: false
    },
    // 适应Mql
    adaptMql: {
      type: Boolean,
      required: false
    }
  },

  data () {
    return {
      paymentTypeTableData: []
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
    },
    // key map 计算一次缓存下来
    keyMap () {
      const mappingProp = key => mappingPropByBusinessTypeAndKey(this.businessType, key)
      return {
        paymentPeriod: mappingProp('paymentPeriod'),
        paymentConditionId: mappingProp('paymentConditionId'),
        paymentCondition: mappingProp('paymentCondition'),
        paymentMode: mappingProp('paymentMode'),
        paymentProportion: mappingProp('paymentProportion'),
        paymentPhase: mappingProp('paymentPhase')
      }
    }
  },
  watch: {
    editRow: {
      handler (nVal) {
        if (nVal) {
          this.getQuoteItemPayments()
        }
      },
      immediate: true,
      deep: true
    }
  },

  created () {
    // 传入查询条件 queryParams
    // 传入查询API Name queryApiName 内置寻源所有模块
    // 传入已有的列表 paymentList

    // if (this.editRow) {
    //   this.getQuoteItemPayments()
    // }
  },

  methods: {
    /* 查询付款条款 */
    async getQuoteItemPayments () {
      const { paymentList, ...queryParams } = JSON.parse(JSON.stringify(this.editRow || {}))

      // 存在列表
      if (paymentList && Array.isArray(paymentList)) {
        this.paymentTypeTableData = paymentList.concat()
        return
      }

      // 根据业务类型净化入参
      const params = getQueryParamsByBusinessType(this.businessType, queryParams)
      if (params && Object.values(params).length === 0) {
        return
      }

      const response = await this.$api.utils.common(
        getApiByBusinessType(this.businessType),
        { queryParams: params }
      )

      if (response && response.data && Array.isArray(response.data)) {
        this.paymentTypeTableData = response.data
      }
    },

    /* 付款条件 */
    paymentConditionIdChange (dictItem, row) {
      // 冗余名称
      row[this.keyMap.paymentConditionId] = dictItem.value
      row[this.keyMap.paymentCondition] = dictItem.label
    },

    /* 新增行 */
    addRow () {
      this.paymentTypeTableData.push({})
    },

    /* 删除行 */
    deleteRow (index) {
      this.paymentTypeTableData.splice(index, 1)
    },

    /* 发起保存 */
    savePaymentType () {
      if (!this.readonly) {
        if (!validateRequiredColumn(
          this.$refs.paymentTypeTable,
          this.paymentTypeTableData,
          {
            validateScope: true,
            tableTitle: this.$t('bidMod.affairsPayClause')
          }
        )) {
          return
        }

        try {
          const totalPaymentProportion = this.paymentTypeTableData.reduce((total, item) => {
            return total + Number(item[this.keyMap.paymentProportion])
          }, 0)
          if (totalPaymentProportion !== 100) {
            // 付款比例相加必须等于100
            this.$message.warning(this.$t('bidMod.common.paymentDialogMsg'))
            return
          }
        } catch (e) {
          this.$message.warning(this.$t('bidMod.common.paymentDialogMsg'))
          return
        }
      }

      this.$emit('savePaymentType', this.paymentTypeTableData)
      this.dialogVisible = false
      if (this.adaptMql) {
        this.beforeClose()
      }
    },
    cancel () {
      this.dialogVisible = false
      if (this.adaptMql) {
        this.beforeClose()
      }
    },
    beforeClose () {
      if (this.adaptMql) {
        this.$emit('before-close')
      } else {
        this.dialogVisible = false
      }
    }
  }
}
</script>
