<template>
  <div class="wrapper">
    <div class="header">
      <!-- <span class="tips red">注：统一折算成卢布进行价格对比</span> -->
      <span class="tips red">{{ $t("cusEntry.supplement20250205.convertToRublesForPriceComparison") }}</span>
      <!-- 打开标签页查看 -->
      <!-- <el-button type="primary" @click="viewQuote">
        {{ $t("cusEntry.supplement20250205.openTabToView") }}
      </el-button> -->
      <el-button type="primary" @click="downLoadBusFile">
        <!-- 下载报价附件 -->
        {{ $t("cusEntry.supplement20250205.downloadQuotationAttachment") }}
      </el-button>
      <PriceComparison
        class="mt-10"
        :projectId="baseForm.projectId"
        :showTitle="false"
      />
    </div>
  </div>
</template>
<script>
import PriceComparison from './businessInfo/priceComparison'
import { downloadFileLink } from 'lib@/utils/file'

export default {
  components: {
    PriceComparison
  },
  props: {
    form: {
      type: Object,
      default: () => ({})
    },
    readonly: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {

    }
  },
  computed: {
    baseForm: {
      get: function () {
        return this.form
      },
      set: function (val) {
        this.$emit('update:form', val)
      }
    }
  },
  methods: {
    viewQuote () {
      this.$emit('viewQuote')
    },
    // 下载商务附件
    downLoadBusFile () {
      // this.$confirm('文件已开始下载，请耐心等待，或进入浏览器的下载界面查看下载进度。', '提示', {
      this.$confirm(this.$t('cusEntry.supplement20250205.fileDownloadStarted'), this.$t('components.approvalHead.tips.tip'), {
        showCancelButton: false
      }).then(() => {})
      downloadFileLink(
        `/api-file/bid/businessfile/downloadBusinessFile?projectId=${this.baseForm.projectId}`,
        // `招标项目[${this.baseForm.extProjectNo}]商务附件.zip`
        `${this.$t('cusEntry.supplement20250205.tenderProject')}[${this.baseForm.extProjectNo}]${this.$t('bidMod.businessAttch')}.zip`
      ).catch(res => {
        this.$message.error(res.message)
      })
    }
  }
}
</script>
<style lang="scss" scoped>
.red {
  color: red;
}
.tips {
  margin-right: 20px;
}
.mt-10 {
  margin-top: 10px;
}
</style>
