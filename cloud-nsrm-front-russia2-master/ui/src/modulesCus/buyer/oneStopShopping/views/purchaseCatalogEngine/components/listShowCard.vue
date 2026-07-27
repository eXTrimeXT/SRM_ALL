<template>
  <el-main>
    <div class="list-show-card">
      <div v-if="itemList.length > 0" class="method-card">
        <el-row :gutter="16">
          <el-col v-for="(item, index) in itemList" :key="index" :span="6">
            <div class="itemWrpper">
              <div class="img-info" @click="itemDetail(item)">
                <img width="100%" :src="item.imgUrl" :alt="item.materialName">
              </div>
              <div class="item-info">
                <p>
                  <span>{{ $t('cusEntry.sup.taxPrice') }}:</span>
                  <span class="price-amount">{{ item.extReferencePrice }}</span>
                  <span class="price-currency"> {{ $getDictLabel('currency', 'RUB') }}</span>
                </p>
                <p>
                  {{ $t('cusEntry.sup.goodsNameCode') }}
                  <span class="itemLink item-desc-font" :title="item.materialCode" @click="itemDetail(item)">
                    {{ item.materialCode }}
                  </span>
                </p>
                <p :title="item.materialName">
                  {{ $t('cusEntry.sup.goodsName') }}：
                  <span class="item-desc-font">{{ item.materialNameShow }}</span>
                </p>
                <p>
                  {{ $t('cusEntry.sup.category') }}：
                  <span class="item-desc-font">{{ item.categoryName }}</span>
                </p>
                <p>
                  {{ $t('cusEntry.sup.specification') }}：
                  <span class="item-desc-font">{{ item.specificationShow }}</span>
                </p>
                <p>
                  {{ $t('cusEntry.sup.orderQuantityMinimum') }}：
                  <span class="item-desc-font">{{ item.orderQuantityMinimum }}</span>
                </p>
                <div class="requirement-num">
                  {{ $t('cusEntry.sup.requirementNum') }}：
                  <el-input-number
                    v-model="item.requirementNum"
                    class="requirement-num-input"
                    controls-position="right"
                    :min="0"
                    @change="value => requirementNumChange(value, item)"
                  />
                </div>
                <el-button
                  type="primary"
                  icon="iconfont icongouwuche shop-icon"
                  class="shop-btn"
                  @click="addShoppingCart(item)"
                >
                  {{ $t('common.addShoppingCart') }}
                </el-button>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
      <div v-else>
        <div class="noResData">
          <em class="el-icon-warning-outline" />{{ $t('common.noData') }}
        </div>
      </div>
    </div>
  </el-main>
</template>

<script>
export default {
  name: 'ListShowCard',
  props: {
    itemResData: {
      type: Array,
      default () {
        return []
      }
    }
  },
  data () {
    return {
      itemList: []
    }
  },
  watch: {
    itemResData: {
      deep: true,
      immediate: true,
      handler (newValue) {
        this.itemList = newValue
      }
    }
  },
  methods: {
    // 图片、物料编码跳转
    itemDetail (item) {
      this.$emit('itemDetail', item)
    },
    // 加如购物车
    addShoppingCart (item) {
      this.$emit('addShoppingCart', item)
    },
    /* 需求量变更 */
    requirementNumChange (value, item) {
      setTimeout(() => {
        const ifPrecision = value?.toString().includes('.')
        if (ifPrecision) {
          /* 获取小数点 */
          const [integer, precision] = value?.toString().split('.')
          item.requirementNum = Number(`${integer}.${precision.toString().slice(0, 4)}`)
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.list-show-card {
  .method-card {
    width: 100%;
    height: 100%;
    box-sizing: border-box;
    padding: 16px;
    overflow: hidden;
    overflow-y: auto !important;
    .itemWrpper {
      width: 100%;
      margin-bottom: 16px;
      border-radius: 4px;
      overflow: hidden;
      box-shadow: 0 4px 8px 0 rgba(0, 0, 0, 0.1), 0 6px 20px 0 rgba(0, 0, 0, 0.1);
      &:hover {
        box-shadow: 10px 8px 12px 0 rgba(0, 0, 0, 0.01), 10px 12px 30px 0 rgba(0, 0, 0, 0.05);
        transition: all .2s ease-in-out;
      }
      .img-info {
        // border: 1px solid #dfe6ec;
        overflow: hidden;
        height: 160px;
        cursor: pointer;
        // background-color: #75C8FF;
        background-image: linear-gradient(to right, #95A5C9 , #8295BF);
        display: flex;
        // align-items: center;
        justify-content: center;
      }
      .item-info {
        border: 1px solid #dfe6ec;
        padding: 14px;
        .requirement-num {
          white-space: nowrap;
          overflow: hidden;
          .requirement-num-input {
            width: 50%;
            text-overflow: ellipsis;
          }
        }
        p {
          font-size: 14px;
          color: #393E45;;
          margin: 0;
          line-height: 26px;
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
          span {
            color: #51555B;
            &.itemLink {
              cursor: pointer;
            }
          }
        }
        .shop-btn {
          width: 100%;
          margin-top: 18px;
        }
        .shop-icon {
          font-size: 12px;
          margin-right: 4px;
        }
        .price-amount {
          font-size: 18px;
          font-weight: 600;
          color: #FF4A4D;
        }
        .item-price {
          color: #FF4A4D;
          display: flex;
          margin: 6px 0 8px 0;
          .price-symbol {
            font-size: 12px;
            font-weight: 600;
            margin-top: 5px;
          }
          .price-amount {
            font-size: 18px;
            font-weight: 600;
          }
          .price-date {
            margin-left: 8px;
            display: flex;
            align-items: center;
            border: 1px solid #FF4A4D;
            border-radius: 4px;
            .date-tip {
              color: #FFFFFF;
              background: #FF4A4D;
              padding: 4px;
            }
            .date-time {
              padding: 4px;
              transform: scale(0.9);
            }
          }
        }
      }
    }
  }
  .noResData {
    font-size: 20px;
    text-align: center;
    padding: 80px 30px;
    color: #999;
    :deep(.el-icon-warning-outline) {
      margin-right: 5px;
    }
  }
}
</style>
