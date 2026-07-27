<template>
  <div class="quote-wrapper">
    <div class="quote-title">
      <!-- 以下价格均为未税 -->
      {{ $t("bidMod.competitionLts.hallToolbar1") }}
    </div>
    <div class="quote-content">
      <div class="price start-price">
        <p class="num">
          {{ info.orderStartPrice }}
        </p>
        <p class="desc">
          <!-- 起拍价 -->
          {{ $t("bidMod.startingPrice") }}
        </p>
      </div>
      <div class="price new-price">
        <p class="num">
          {{ latestOrderNotaxPrice }}
        </p>
        <p class="desc">
          <!-- 当前最新价 -->
          {{ $t("competition.lastesPrice") }}
        </p>
      </div>
      <div class="price my-price">
        <p class="num">
          {{ orderInfo.orderNotaxPrice }}
        </p>
        <p class="desc">
          <!-- 我的最新报价<span class="bage">排名：{{ orderInfo.auctRanking }}</span> -->
          {{ $t("competition.myLastesPrice") }}<span class="bage">{{ $t("competition.auctRanking") }}{{ orderInfo.auctRanking }}</span>
        </p>
      </div>
      <div class="price-input">
        <div class="price-tips">
          <!-- 请注意竞价剩余时长，防止错过最佳报价时机 -->
          {{ $t("competition.priceTips") }}
        </div>
        <!-- 描述文字 -->
        <el-input-number
          v-model="quotePrice"
          class="input"
          :min="0"
          :max="max"
          :step="step"
          :label="$t('competition.quotePriceDes')"
          @change="handleChange"
        />
        <div class="btns">
          <el-button type="ghost" size="small" @click="$emit('refresh')">
            <!-- 刷新 -->
            {{ $t("base.tagsView.refresh") }}
          </el-button>
          <el-button type="primary" size="small" :disabled="readonly" @click="quotation">
            <!-- 报价 -->
            {{ $t("bidMod.doBiding1") }}
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  name: 'QuotationInfo',
  props: {
    baseInfo: {
      type: Object,
      default: () => {}
    },
    info: {
      type: Object,
      default: () => {}
    },
    orderInfo: {
      type: Object,
      default: () => {}
    },
    latestOrderNotaxPrice: null,
    readonly: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      quotePrice: null,
      max: Infinity
    }
  },
  computed: {
    step () {
      const { minPercent, minAmount } = this.baseInfo
      let step = 1
      if (minPercent) {
        step = Math.round(this.info.orderStartPrice * minPercent / 100)
      }
      if (minAmount) {
        step = minAmount
      }
      return step
    }
  },
  watch: {
    'orderInfo.orderNotaxPrice': {
      handler (nVal) {
        if (nVal) {
          this.quotePrice = nVal
        } else {
          this.quotePrice = this.info.orderStartPrice
        }
      },
      immediate: true
    }
  },
  methods: {
    quotation () {
      this.$emit('quotation', this.quotePrice)
    },
    handleChange () {

    }
  }
}
</script>
<style scoped lang="scss">
.quote-title {
  color:#8484A4;
  height: 36px;
  line-height: 36px;
}
.quote-content {
  display:flex;
  justify-content: flex-start;
  align-items:center;
  .price {
    padding: 0 40px;
    border-right: 1px solid #E4E4E4;
    &:last-child {
      // border-right:none;
    }
    .num {
      font-size:16px;
    }
    .desc {
      color:#8484A4;
      .bage {
        margin-left: 10px;
        color:#f79a63;
      }
    }
  }
  .price-input {
    margin-left:20px;
    width: 300px;
    .price-tips {
      color:red;
    }
    .input {
      display:block;
      width:100%;
      margin: 6px 0;
    }
    .btns {
      text-align:right;
    }
  }
}
</style>
