<template>
  <srm-dialog
    :title="dialogTitle"
    :visible.sync="dialogFormVisible"
    :close-on-click-modal="false"
    size="middle"
  >
    <el-form
      ref="exchangeRateSettingForm"
      :model="settingForm"
      :rules="settingFormRules"
    >
      <srm-row>
        <srm-col :init-col="2">
          <!--来源币种-->
          <el-form-item
            :label="$t('bid_mod.fromCurrencyCode')"
            prop="fromCurrencyCode"
          >
            <quick-search
              :show-input="settingForm.fromCurrencyName"
              show-key="currencyName"
              auto-query
              is-set-value
              :scope-data="settingForm"
              name="scc_base_purchase_currency_info"
              @close-quicksearch="setFromCurrencyCode"
            />
          </el-form-item>
        </srm-col>

        <srm-col :init-col="2">
          <!--目标币种-->
          <el-form-item
            :label="$t('dataConfMod.toCurrencyCode')"
            prop="toCurrencyCode"
          >
            <quick-search
              :show-input="settingForm.toCurrencyName"
              show-key="currencyName"
              auto-query
              is-set-value
              :scope-data="settingForm"
              name="scc_base_purchase_currency_info"
              @close-quicksearch="setToCurrencyCode"
            />
          </el-form-item>
        </srm-col>

        <srm-col :init-col="2">
          <!--转换日期-->
          <el-form-item
            :label="$t('dataConfMod.exchangeDate')"
            prop="exchangeDate"
          >
            <el-date-picker
              v-model="settingForm.exchangeDate"
              clearable
              type="date"
              format="yyyy-MM-dd"
              value-format="yyyy-MM-dd"
            />
          </el-form-item>
        </srm-col>

        <srm-col :init-col="2">
          <!--汇率-->
          <el-form-item
            :label="$t('bid_mod.priceTax')"
            prop="priceTax"
          >
            <el-input
              v-model="settingForm.priceTax"
              type="number"
            />
          </el-form-item>
        </srm-col>

        <srm-col :init-col="2">
          <!--汇率类型-->
          <el-form-item
            :label="$t('bid_mod.exchangeRateType')"
            prop="rateType"
          >
            <dict-select
              v-model="settingForm.rateType"
              code="EXCHANGE_RATE_TYPE"
            />
          </el-form-item>
        </srm-col>

        <srm-col :init-col="2">
          <!--是否有效-->
          <el-form-item
            :label="$t('contractMod.isValid')"
            prop="enabled"
          >
            <dict-select
              v-model="settingForm.enabled"
              code="YES_OR_NO"
            />
          </el-form-item>
        </srm-col>
      </srm-row>
    </el-form>

    <div
      slot="footer"
      class="dialog-footer"
    >
      <!-- 取 消 -->
      <el-button @click="cancelDialog">
        {{ $t("common.cancel") }}
      </el-button>
      <!-- 确 定 -->
      <el-button
        type="primary"
        @click="submitSave"
      >
        {{ $t("common.confirm") }}
      </el-button>
    </div>
  </srm-dialog>
</template>

<script>
import QuickSearch from 'lib@/components/QuickSearch'

export default {
  name: 'ExchangeRateSettingDetail',
  components: {
    QuickSearch
  },
  props: {
    visible: Boolean,
    editRow: Object,
    currencyList: Array
  },
  data () {
    return {
      settingForm: {
        fromCurrencyCode: '',
        toCurrencyCode: '',
        exchangeDate: '',
        priceTax: '',
        rateType: '',
        enabled: 'Y'
      },
      settingFormRules: {
        fromCurrencyCode: [{ required: true, message: this.$t('dataConfMod.fromCurrencyCodeRequired') }],
        toCurrencyCode: [{ required: true, message: this.$t('dataConfMod.toCurrencyCodeRequired') }],
        exchangeDate: [{ required: true, message: this.$t('dataConfMod.exchangeDateRequired') }],
        priceTax: [{ required: true, message: this.$t('dataConfMod.priceTaxRequired') }],
        rateType: [{ required: true, message: this.$t('dataConfMod.rateTypeRequired') }],
        enabled: [{ required: true, message: this.$t('dataConfMod.enabledRequired') }]
      }
    }
  },
  computed: {
    dialogFormVisible: {
      get: function () {
        return this.visible
      },
      set: function (val) {
        this.$emit('update:visible', val)
      }
    },
    dialogTitle () {
      return this.editRow ? this.$t('dataConfMod.editExchangeRate') : this.$t('dataConfMod.addExchangeRate')
    }
  },
  watch: {
    visible (newVal) {
      if (newVal && this.editRow) {
        this.settingForm = {
          ...this.settingForm,
          ...this.editRow
        }
        this.currencyList.forEach(item => {
          // 必须手动设置fromCurrencyName和toCurrencyName
          if (item.value === this.settingForm.fromCurrencyCode) {
            this.settingForm = {
              ...this.settingForm,
              fromCurrencyId: item.id,
              fromCurrencyName: item.label
            }
          }
          if (item.value === this.settingForm.toCurrencyCode) {
            this.settingForm = {
              ...this.settingForm,
              toCurrencyId: item.id,
              toCurrencyName: item.label
            }
          }
        })
      }
    }
  },
  methods: {
    /* 设置来源币种 */
    setFromCurrencyCode (val, scope) {
      scope.fromCurrencyId = val ? val.currencyId : ''
      scope.fromCurrencyCode = val ? val.currencyCode : ''
      scope.fromCurrencyName = val ? val.currencyName : ''
    },
    /* 设置目标币种 */
    setToCurrencyCode (val, scope) {
      scope.toCurrencyId = val ? val.currencyId : ''
      scope.toCurrencyCode = val ? val.currencyCode : ''
      scope.toCurrencyName = val ? val.currencyName : ''
    },
    /* 取消 */
    cancelDialog () {
      this.settingForm = {
        fromCurrencyCode: '',
        toCurrencyCode: '',
        exchangeDate: '',
        priceTax: '',
        rateType: '',
        enabled: 'Y'
      }
      // 重置并移除校验
      this.$refs.exchangeRateSettingForm.resetFields()
      this.dialogFormVisible = false
    },
    /* 弹窗保存 */
    submitSave () {
      this.$refs.exchangeRateSettingForm.validate(valid => {
        if (valid) {
          // 通过校验
          this.$http({
            url: '/api-base/purchase/purchaseExchangeRate/saveOrUpdate',
            method: 'POST',
            data: { ...this.settingForm },
            loading: true
          }).then(() => {
            // 重置表单
            this.$message.success(this.$t('common.successSave'))
            this.$emit('submitSuccess')
            this.cancelDialog()
          })
        }
      })
    }
  }
}
</script>
