<template>
  <el-container
    class="flex-container"
    direction="vertical"
    style="padding-bottom: 30px"
  >
    <el-main>
      <TemplateCompile
        ref="templateCompile"
        :readonly="pageFlag.isView"
        :price-data="priceData"
        :detail-data="detailData"
      />

      <CToolbar>
        <template slot="right">
          <!--价格测算-->
          <el-button type="primary" @click="previewTest">
            {{ $t('other.previewTest') }}
          </el-button>

          <!--关闭-->
          <el-button @click="backTab">
            {{ $t('common.close') }}
          </el-button>
        </template>
      </CToolbar>
    </el-main>
  </el-container>
</template>

<script>
/**
 * 模板预览
 */
import { quoteBuyerHttp } from 'modb@/quoteTemplate/api'
import CToolbar from 'lib@/components/c-toolbar'
import TemplateCompile from 'lib@/composition/quoteTemplate/templateCompile.vue'

export default {
  name: 'TemplatePreview',

  components: {
    TemplateCompile,
    CToolbar
  },

  data () {
    return {
      tempId: this.$attrs.params.row.tempId,
      detailData: {
        attrMap: null,
        tempVO: null
      },
      priceData: null
    }
  },

  computed: {
    pageFlag () {
      // 编辑、只读
      // flag: ['edit', 'view']
      const flag = this.$attrs.params.flag
      return {
        isEdit: flag === 'edit',
        isView: flag === 'view'
      }
    }
  },

  created () {
    this.getQuoteTemplatePreviewData()
  },

  methods: {
    /* 查询报价模板数据 */
    async getQuoteTemplatePreviewData () {
      if (!this.tempId) {
        return
      }

      const response = await quoteBuyerHttp.template.getPreviewData(this.tempId)
      if (response && response.data) {
        const { attrMap = {}, tempVO = {} } = response.data
        this.detailData = {
          attrMap,
          tempVO
        }
      }
    },

    /* 价格测算 */
    async previewTest () {
      // 校验并拿到数据
      const result = await this.$refs.templateCompile.validateAndResolveData()
      if (!result.status) {
        return
      }

      const response = await quoteBuyerHttp.template.previewTest(this.tempId, result.data)
      if (response && response.data) {
        this.$message.success('价格计算完成')
        // 更新价格
        this.priceData = response.data || {}
      }
    },

    /* 关闭 */
    backTab () {
      this.$emit('tab-remove', this.$attrs.params.tabName)
    }
  }
}
</script>
