<template>
  <SrmDialog
    :title="$t('templatePrice.label')"
    :visible.sync="dialogVisible"
    append-to-body
    fullscreen
    :close-on-click-modal="false"
  >
    <TemplateCompile
      ref="templateCompile"
      :readonly="readonly"
      :price-data="priceData"
      :detail-data="detailData"
    />

    <div
      slot="footer"
      class="dialog-footer"
    >
      <el-button @click="dialogVisible = false">
        {{ $t('common.cancel') }}
      </el-button>

      <!--价格计算-->
      <el-button type="primary" @click="previewTest">
        {{ $t('templatePrice.priceCalc') }}
      </el-button>

      <!--确定-->
      <el-button type="primary" @click="confirm">
        {{ $t('common.confirm') }}
      </el-button>
    </div>
  </SrmDialog>
</template>

<script>
/**
 * 简易询价-模板报价
 */

import { quoteBuyerHttp } from 'modb@/quoteTemplate/api'
import TemplateCompile from 'lib@/composition/quoteTemplate/templateCompile.vue'

export default {
  name: 'TemplatePreviewDialog',

  components: {
    TemplateCompile
  },

  props: {
    visible: {
      type: Boolean,
      required: true
    },
    editRow: {
      type: Object,
      required: true
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
      if (!this.editRow.tempId) {
        return
      }

      const response = await quoteBuyerHttp.template.getPreviewData(this.editRow.tempId)
      if (response && response.data) {
        const { attrMap = {}, tempVO = {} } = response.data
        this.detailData = {
          attrMap,
          tempVO
        }
      }
    },

    /* 价格计算 */
    async previewTest () {
      // 校验并拿到数据
      const result = await this.$refs.templateCompile.validateAndResolveData()
      if (!result.status) {
        return
      }

      const response = await quoteBuyerHttp.template.previewTest(this.editRow.tempId, result.data)
      if (response && response.data) {
        this.$message.success(this.$t('templatePrice.priceCalcComplete'))
        // 更新价格
        this.priceData = response.data || {}
      }
    },

    /* 确定 */
    confirm () {
      this.$emit('confirm')
      this.dialogVisible = false
    }
  }
}
</script>
