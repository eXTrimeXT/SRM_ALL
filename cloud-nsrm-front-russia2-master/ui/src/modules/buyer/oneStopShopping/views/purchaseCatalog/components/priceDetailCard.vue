<template>
  <div class="catalog-detail-infor">
    <div class="detail-big-img">
      <img class="img-fit" :width="catalogList.length > 0 ? '100%' : '70%'" :src="bigImgShow" :alt="formDetail.materialName">
    </div>

    <div class="catalog-infor">
      <!-- 物料名称 -->
      <div class="catalog-title">
        {{ formDetail.materialName }}
      </div>
      <div class="catalog-price-infor">
        <div class="price-block">
          <span class="label-price">{{ $t('bidMod.price') }}：</span>
          <div class="item-price">
            <span class="price-symbol">￥</span>
            <!-- 价格 -->
            <span class="price-amount">{{ formDetail.taxPrice }}</span>
            <div class="price-date">
              <div class="date-tip">
                {{ $t('oneStopShopping.distanceShelf') }}
              </div>
              <!-- 距离下架时间 -->
              <div class="date-time">
                {{ formDetail.remainingTime }}
              </div>
            </div>
          </div>
        </div>
        <div class="item-label">
          {{ $t('dataConfMod.orderQuantityMinimum') }}：<span class="item-text">{{ formDetail.orderQuantityMinimum }}</span>
        </div>
      </div>

      <div class="item-detail">
        <p>
          <span class="item-label">{{ $t('common.materialCode') }}：</span>
          <span class="item-text">{{ formDetail.materialCode }}</span>
        </p>
        <p>
          <span class="item-label">{{ $t('oneStopShopping.specification') }}：</span>
          <span class="item-text">{{ formDetail.specification }}</span>
        </p>
        <p>
          <span class="item-label">{{ $t('oneStopShopping.unit') }}：</span>
          <span class="item-text">{{ $getDictLabel('unit', formDetail.unitCode) }}</span>
        </p>
        <p>
          <span class="item-label">{{ $t('oneStopShopping.vendor') }}：</span>
          <span class="item-text">{{ formDetail.vendorName }}</span>
        </p>
      </div>

      <div class="catalog-scroll-img">
        <div class="img-arrow" @click="arrowImg('pre')">
          〈
        </div>
        <ul class="small-img-list">
          <li
            v-for="(img, i) in imgList"
            :ref="`imgRef${i}`"
            :key="i"
            class="item-small-img"
            @click="selectImgInfor(i)"
          >
            <img width="100%" :src="img" :alt="formDetail.materialName">
          </li>
        </ul>
        <div class="img-arrow" @click="arrowImg('next')">
          〉
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getImgSrc } from 'lib@/utils/file'

const defaultImg = '@/assets/images/catalogLogoBig.png'
export default {
  name: 'PriceDetailCard',
  props: {
    formDetail: {
      type: Object,
      default () {
        return {}
      }
    },
    catalogList: {
      type: Array,
      default () {
        return []
      }
    },
    hasImg: {
      type: Boolean,
      default () {
        return true
      }
    }
  },
  data () {
    return {
      imgList: [],
      bigImgShow: '',
      imgIndex: 0,
      defaultImg: defaultImg
    }
  },
  watch: {
    hasImg (sign) {
      if (sign) {
        this.getImgList()
      } else {
        this.imgList[0] = this.defaultImg
        this.bigImgShow = this.defaultImg
      }
    }
  },
  methods: {
    getImgList () {
      if (this.catalogList.length > 0) {
        this.imgList = this.catalogList.map(item => getImgSrc(item.fileuploadId))
        this.$nextTick(() => {
          let imgRef = this.$refs[`imgRef${this.imgIndex}`][0]
          imgRef.style.borderColor = 'rgba(0,119,255,1)'
          this.bigImgShow = this.imgList[0]
        })
      } else {
        setTimeout(() => {
          this.getImgList()
        }, 80)
      }
    },
    selectImgInfor (index) {
      this.setNextImg('click', index)
    },
    setNextImg (flag, index) {
      // 先重置上一次border颜色
      let preImg = this.$refs[`imgRef${this.imgIndex}`][0]
      preImg.style.borderColor = '#B9C0C7'
      // 指针移动，点击移动或者左右箭头移动
      if (index > -1) {
        this.imgIndex = index
      } else {
        this.imgIndex = flag === 'pre' ? (this.imgIndex - 1) : (this.imgIndex + 1)
      }
      let imgRef = this.$refs[`imgRef${this.imgIndex}`][0]
      imgRef.style.borderColor = 'rgba(0,119,255,1)'
      // 设置默认大图
      this.bigImgShow = this.imgList[this.imgIndex]
    },
    arrowImg (flag) {
      if (flag === 'pre' && this.imgIndex < 1) return
      if (flag === 'next' && this.imgIndex === this.imgList.length - 1) return
      this.setNextImg(flag)
    }
  }
}
</script>

<style scoped lang="scss">
.catalog-detail-infor {
  display: flex;
  margin-bottom: 30px;
  ul,li {
    list-style: none;
    outline: none;
    padding: 0px;
    margin: 0px;
  }
  .item-label {
    font-size: 14px;
    color: #73777C;
    font-weight: 400;
    line-height: 14px;
  }
  .item-text {
    font-size: 14px;
    color: #161C24;
    font-weight: 400;
    line-height: 14px;
  }
  .detail-big-img {
    width: 360px;
    height: 360px;
    background: #F6F6F6;
    // border: 1px solid rgba(220,221,222,1);
    display: flex;
    align-items: center;
    justify-content: center;
    .img-fit {
      height: 100%;
      object-fit: contain;
    }
  }
  .catalog-infor {
    width: calc(100% - 360px);
    margin-left: 30px;
    .catalog-title {
      font-size: 18px;
      color: #161C24;
      line-height: 26px;
      font-weight: 500;
      margin-bottom: 16px;
    }
    .catalog-price-infor {
      width: 100%;
      font-size: 14px;
      padding: 24px 16px;
      background: #F6F6F6;
      .price-block {
        display: flex;
        align-items: center;
        margin-bottom: 18px;
        .label-price {
          color: #73777C;
          font-weight: 400;
          margin-top: 5px;
        }
        .item-price {
          color: #FF5D60;
          display: flex;
          .price-symbol {
            font-size: 12px;
            font-weight: 600;
            margin-top: 12px;
          }
          .price-amount {
            font-size: 24px;
            font-weight: 600;
            margin-top: 3px;
          }
          .price-date {
            font-size: 12px;
            margin-left: 12px;
            display: flex;
            align-items: center;
            .date-tip {
              color: #FFFFFF;
              background: #FF4A4D;
              border: 1px solid #FF4A4D;
              border-radius: 4px 0 0 4px;
              padding: 4px;
            }
            .date-time {
              border: 1px solid #FF4A4D;
              border-radius: 0 4px 4px 0;
              padding: 4px;
            }
          }
        }
      }
    }

    .item-detail {
      margin-bottom: 38px;
    }

    .catalog-scroll-img {
      display: flex;
      .img-arrow {
        width: 16px;
        height: 60px;
        color: #96999C;
        background: #F6F6F6;
        display: flex;
        justify-content: center;
        align-items: center;
        cursor: pointer;
      }
      .small-img-list {
        display: flex;
        margin: 0 2px;
        li {
          width: 60px;
          height: 60px;
          margin-right: 6px;
          border-radius: 4px;
          overflow: hidden;
          border: 1px solid #F6F6F6;
          background: #F6F6F6;
          cursor: pointer;
          display: flex;
          justify-content: center;
          align-items: center;
          &:focus {
            border: 1px solid rgba(0,119,255,1)
          }
          &:hover {
            border: 1px solid rgba(0,119,255,1)
          }
          &:last-child {
            margin-right: 0;
          }
        }
      }
    }
  }
}
</style>
