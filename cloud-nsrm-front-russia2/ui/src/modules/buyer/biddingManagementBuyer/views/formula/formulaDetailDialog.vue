<template>
  <SrmDialog
    :visible.sync="dialogVisible"
    :title="dialogTitle"
    size="large"
    append-to-body
    :close-on-click-modal="false"
  >
    <el-form
      ref="form"
      :model="formulaFormData"
      label-width="120px"
      label-position="top"
      class="form-incontainer"
      :disabled="readonly"
      :rules="fromRules"
    >
      <SrmRow>
        <SrmCol :init-col="3">
          <!--询价单号-->
          <el-form-item :label="$t('formula.formulaName')" prop="pricingFormulaName">
            <el-input v-model="formulaFormData.pricingFormulaName" @change="pricingFormulaNameBlur" />
          </el-form-item>
        </SrmCol>
        <SrmCol :init-col="3">
          <!--询价单号-->
          <el-form-item :label="$t('formula.formulaDesc')">
            <el-input v-model="formulaFormData.pricingFormulaDesc" />
          </el-form-item>
        </SrmCol>

        <SrmCol :init-col="1">
          <!--价格公式-->
          <el-form-item
            :label="$t('purchaseDemand.priceFormula')"
            prop="pricingFormulaValue"
            class="formula-form-item"
          >
            <template #label>
              <div class="formula-form-item-label">
                {{ $t('purchaseDemand.priceFormula') }}
                <!-- 填写价格公式，公式中用到的要素名称用[]表示，比如：( [长]+[宽])*[原材料价格]/10 -->
                <span class="label-tips">{{ $t('cusEntry.supplement20250205.message7') }}</span>
              </div>
            </template>
            <el-input
              ref="elInput"
              v-model="formulaFormData.pricingFormulaValue"
              type="textarea"
              :rows="2"
              :placeholder="$t('common.pleaseInput')"
            />
            <!--<textarea-->
            <!--  ref="textarea"-->
            <!--  v-model="pricingFormulaValue"-->
            <!--  :rows="2"-->
            <!--  :placeholder="$t('common.pleaseInput')"-->
            <!--  @input="handleInput"-->
            <!--/>-->
          </el-form-item>
        </SrmCol>
      </SrmRow>
    </el-form>

    <template #footer class="dialog-footer">
      <el-button @click="dialogVisible = false">
        {{ $t('common.cancel') }}
      </el-button>
      <el-button
        type="primary"
        :loading="submitLoading"
        @click="submit"
      >
        {{ $t('common.confirm') }}
      </el-button>
    </template>
  </SrmDialog>
</template>

<script>
/**
 * 价格公式详情弹窗
 */
import { formula, queryEssentialFactorByPage } from 'modb@/biddingManagementBuyer/api'

export default {
  name: 'FormulaDetailDialog',

  props: {
    visible: {
      type: Boolean,
      default: false
    },
    editRow: {
      type: Object,
      default: () => null
    },
    readonly: {
      type: Boolean,
      default: false
    }
  },

  data () {
    return {
      submitLoading: false,
      formulaFormData: {
        pricingFormulaName: '',
        pricingFormulaDesc: '',
        pricingFormulaValue: ''
      },
      fromRules: {
        pricingFormulaName: [{ required: true, message: this.$t('common.pleaseInput') + this.$t('formula.formulaName') }],
        pricingFormulaValue: [{ required: true, message: this.$t('common.pleaseInput') + this.$t('purchaseDemand.priceFormula') }]
      },
      // searchList: [
      //   { value: '零件净重' },
      //   { value: '废料净重' },
      //   { value: '原材料价格' }
      // ],
      pricingFormulaValue: '',
      oldFormulaValue: ''
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
    dialogTitle () {
      if (this.editRow) {
        return this.$t('common.edit')
      }
      return this.$t('common.add')
    }
  },

  created () {
    if (this.editRow) {
      // 编辑
      formula.get(this.editRow.pricingFormulaHeaderId).then(({ data }) => {
        const {
          pricingFormulaName = '',
          pricingFormulaDesc = '',
          pricingFormulaValue = ''
        } = data

        this.formulaFormData = {
          pricingFormulaName,
          pricingFormulaDesc,
          pricingFormulaValue
        }
      })
    } else {
      this.formulaFormData = {
        pricingFormulaName: '',
        pricingFormulaDesc: '',
        pricingFormulaValue: ''
      }
    }
  },

  methods: {
    pricingFormulaNameBlur (value) {
      if (value && !this.formulaFormData.pricingFormulaDesc) {
        this.formulaFormData.pricingFormulaDesc = value
      }
    },

    handleInput () {
      if (this.oldFormulaValue === this.pricingFormulaValue) {
        return
      }

      const newValue = this.pricingFormulaValue

      this.oldFormulaValue = newValue
      // 当前光标位置
      const position = this.$refs.textarea.selectionStart
      // 往前找 [ 所在位置
      const startPosition = newValue.slice(0, position).lastIndexOf('[')
      // 往后找 ] 所在位置
      const endPosition = newValue.slice(position).indexOf(']') + position
      if (startPosition !== endPosition && position <= endPosition) {
        // 找到光标中括号之间的文本 需要限定条件
        const name = newValue.slice(startPosition + 1, endPosition)
        if (!name) {
          return
        }
        queryEssentialFactorByPage({
          pageNum: 1,
          pageSize: 100,
          essentialFactorName: name,
          essentialFactorStatus: 'ACTIVE'
        }).then(res => {
          console.log(res)
        })
      }
    },

    async submit () {
      const valid = await this.$refs.form.validate().catch(() => this.__focus_error__())

      if (!valid) {
        return
      }

      this.submitLoading = true
      let params = {
        ...this.formulaFormData
      }

      if (this.editRow) {
        params = {
          ...params,
          pricingFormulaHeaderId: this.editRow.pricingFormulaHeaderId
        }
      }

      formula.tempSave(params)
        .then(res => {
          this.submitLoading = false
          this.$message.success(res.message)
          this.dialogVisible = false
          this.$emit('submit-success')
        })
        .catch(() => {
          this.submitLoading = false
        })
    }
  }
}
</script>

<style lang="scss" scoped>
.formula-form-item {
  :deep(.el-form-item__label) {
    display: flex;
    .formula-form-item-label {
      .label-tips {
        color: red;
      }
    }
  }
}
</style>
