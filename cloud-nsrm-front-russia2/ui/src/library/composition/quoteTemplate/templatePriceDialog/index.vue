<template>
  <SrmDialog
    :title="$t('templatePrice.label')"
    :visible.sync="dialogVisible"
    append-to-body
    size="xLarge"
    :close-on-click-modal="false"
  >
    <!--详情插槽-header-->
    <slot name="header" />

    <TemplateCompile
      ref="templateCompile"
      :readonly="readonly"
      :price-data="priceData"
      :detail-data="detailData"
    />

    <!--预留底部插槽-->
    <slot name="footer" />

    <div
      slot="footer"
      class="dialog-footer"
    >
      <el-button @click="dialogVisible = false">
        {{ $t('common.cancel') }}
      </el-button>

      <template v-if="!readonly">
        <!--价格计算-->
        <el-button type="primary" @click="priceCalc">
          {{ $t('templatePrice.priceCalc') }}
        </el-button>

        <!--确定-->
        <el-button type="primary" @click="confirm">
          {{ $t('common.confirm') }}
        </el-button>
      </template>
    </div>
  </SrmDialog>
</template>

<script>
/**
 * 模板报价-报价应用
 */
import { isNumber } from 'lodash'
import { getApiByBusinessType } from './utils'
import { validatorBusinessType } from 'lib@/composition/origin/composition'
import TemplateCompile from 'lib@/composition/quoteTemplate/templateCompile.vue'

export default {
  name: 'TemplatePriceDialog',

  components: { TemplateCompile },

  props: {
    // 业务类型
    businessType: {
      type: String,
      validator: value => validatorBusinessType(value),
      required: true
    },
    visible: {
      type: Boolean,
      required: true
    },
    // 查询参数 调用方决定
    queryParams: {
      type: Object,
      default: () => null
    },
    // 保存参数 调用方决定
    saveParams: {
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
      detailData: {
        attrMap: null,
        tempVO: null
      },
      priceData: null
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

  created () {
    this.getQuoteTemplatePreviewData()
  },

  methods: {
    /* 查询报价模板数据 */
    async getQuoteTemplatePreviewData () {
      if (!this.queryParams) {
        return
      }

      const response = await this.$api.utils.common(
        getApiByBusinessType(this.businessType),
        { queryParams: this.queryParams }
      )
      if (response && response.data) {
        const {
          attrMap = {},
          tempVO = {},
          priceData = {}
        } = response.data
        this.detailData = {
          attrMap,
          tempVO
        }
        this.priceData = priceData
      }
    },

    /* 价格计算 */
    async priceCalc (showMessage = true) {
      return new Promise(async resolve => {
        // 校验并拿到数据
        const result = await this.$refs.templateCompile.validateAndResolveData()
        if (!result.status) {
          resolve(false)
          return
        }

        const response = await this.$api.utils.common(
          getApiByBusinessType(this.businessType),
          {
            queryParams: {
              ...this.saveParams,
              tempData: result.data
            },
            method: 'POST'
          }
        )
        if (response && response.data) {
          if (showMessage) {
            // 价格计算完成
            this.$message.success(this.$t('templatePrice.priceCalcComplete'))
          }
          // 更新价格
          this.priceData = response.data || {}
          resolve(true)
        }
      })
    },

    /* 确定 */
    async confirm () {
      // 1. 调用价格计算，返回计算结果
      const testResult = await this.priceCalc(false)
      if (!testResult) {
        return
      }

      // 2. 检查数据是否合法 允许为0
      const totalPrice = this.priceData.price
      if ((!totalPrice && totalPrice !== 0) || !isNumber(totalPrice)) {
        // 无法获取计算后的总价，请检查模板数据
        this.$message.warning(this.$t('templatePrice.totalPriceError'))
        return
      }

      // 3. 提示并二次确认 => 价格计算完成，当前物料未税单价报价结果为：{0}，是否确定提交该报价？
      const confirmResult = await this.$confirm(
        this.$t('templatePrice.totalPriceConfirm', [totalPrice]),
        {
          confirmButtonText: this.$t('common.submit'),
          cancelButtonText: this.$t('common.cancel'),
          type: 'success'
        }
      ).catch(() => ({}))

      if (confirmResult !== 'confirm') {
        return
      }

      this.$emit('confirm', totalPrice)
      this.dialogVisible = false
    }
  }
}
</script>
