<template>
  <div class="wrapper">
    <div class="header">
      <span class="tips red">注：统一折算成人民币进行价格对比</span>
      <el-button type="primary" @click="viewQuote">
        打开标签页查看
      </el-button>
      <el-button type="primary" @click="downLoadBusFile">
        下载报价附件
      </el-button>
      <PriceComparison
        class="mt-10"
        :projectId="baseForm.projectId"
        :showTitle="false"
      />
      <!-- 比价界面 -->
      <VendorResult
        ref="vendorResult"
        class="mt-10"
        :value.sync="baseForm.caSelectionResults"
        :readonly="readonly || !isWrite"
        :is-write="isWrite"
        :flag="flag"
        :input-flag="inputFlag"
        :winOptions="winOptions"
        @viewQuote="viewQuoteBy"
      />
    </div>
  </div>
</template>
<script>
import VendorResult from './businessInfo/vendorResult'
import PriceComparison from 'modcb@/biddingBuyer/views/biddingManagement/biddingDetail/businessManagement/priceComparison'
import { downloadFileLink } from 'lib@/utils/file'

export default {
  components: {
    VendorResult,
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
    },
    inputFlag: {
      type: Boolean,
      default: true
    },
    flag: {
      type: String,
      default: ''
    },
    winOptions: {
      type: Array,
      default: () => []
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
    },
    isWrite () { // BMP回调标志是否填写 Y是可以填写，N不可以填写
      return this.form.ifWrite === 'Y'
    }
  },
  methods: {
    viewQuote () {
      this.$emit('viewQuote')
    },
    viewQuoteBy () {
      this.$emit('viewQuoteBy')
    },
    // 下载商务附件
    downLoadBusFile () {
      this.$confirm('文件已开始下载，请耐心等待，或进入浏览器的下载界面查看下载进度。', '提示', {
        showCancelButton: false
      }).then(() => {})
      downloadFileLink(
        `/api-file/bid/businessfile/downloadBusinessFile?projectId=${this.baseForm.projectId}`,
        `招标项目[${this.baseForm.extProjectNo}]商务附件.zip`
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
