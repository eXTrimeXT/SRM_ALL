<template>
  <div class="date-quarter">
    <mark
      v-show="showSeason"
      style="position:fixed;top:0;bottom:0;left:0;right:0;background:rgba(0,0,0,0);z-index:999;"
      @click.stop="showSeason = false"
    />
    <el-input
      v-model="showValue"
      :placeholder="placeholder"
      @focus="showSeason = true"
    >
      <i
        slot="prefix"
        class="el-input__icon el-icon-date"
      />
    </el-input>
    <el-card
      v-show="showSeason"
      class="box-card"
      style="width:322px;padding: 0 3px 20px;margin-top:10px;position:fixed;z-index:9999"
    >
      <div
        slot="header"
        class="clearfix"
        style="text-align:center;padding:0"
      >
        <!-- 前一年 -->
        <button
          type="button"
          :aria-label="$t('time.lastYear')"
          class="el-picker-panel__icon-btn el-date-picker__prev-btn el-icon-d-arrow-left"
          @click="prev"
        />
        <span
          role="button"
          class="el-date-picker__header-label"
        >{{ year }}年</span>
        <!-- 后一年 -->
        <button
          type="button"
          :aria-label="$t('time.nextYear')"
          class="el-picker-panel__icon-btn el-date-picker__next-btn el-icon-d-arrow-right"
          @click="next"
        />
      </div>
      <div class="select-content">
        <div v-if="type === 'quarter'">
          <div class="text item">
            <el-button
              type="text"
              @click="selectSeason(0)"
            >
              <!-- 第一季度 -->
              {{ $t("time.quarterList")[0] }}
            </el-button>
            <el-button
              type="text"
              @click="selectSeason(1)"
            >
              <!-- 第二季度 -->
              {{ $t("time.quarterList")[1] }}
            </el-button>
          </div>
          <div class="text item">
            <el-button
              type="text"
              @click="selectSeason(2)"
            >
              <!-- 第三季度 -->
              {{ $t("time.quarterList")[2] }}
            </el-button>
            <el-button
              type="text"
              @click="selectSeason(3)"
            >
              <!-- 第四季度 -->
              {{ $t("time.quarterList")[3] }}
            </el-button>
          </div>
        </div>
        <div v-else-if="type === 'halfYear'">
          <div class="text item">
            <el-button
              type="text"
              @click="selectSeason(0)"
            >
              <!-- 上半年 -->
              {{ $t("time.firstHalfY") }}
            </el-button>
            <el-button
              type="text"
              @click="selectSeason(1)"
            >
              <!-- 上半年 -->
              {{ $t("time.firstHalfY") }}
            </el-button>
          </div>
        </div>
        <div v-else>
          <div class="text item">
            <el-button
              type="text"
              @click="selectSeason(0)"
            >
              <!-- 一月 -->
              {{ $t("time.month")[0] }}
            </el-button>
            <el-button
              type="text"
              @click="selectSeason(1)"
            >
              <!-- 二月 -->
              {{ $t("time.month")[1] }}
            </el-button>
            <el-button
              type="text"
              @click="selectSeason(1)"
            >
              <!-- 三月 -->
              {{ $t("time.month")[2] }}
            </el-button>
          </div>
          <div class="text item">
            <el-button
              type="text"
              @click="selectSeason(0)"
            >
              <!-- 四月 -->
              {{ $t("time.month")[3] }}
            </el-button>
            <el-button
              type="text"
              @click="selectSeason(1)"
            >
              <!-- 五月 -->
              {{ $t("time.month")[4] }}
            </el-button>
            <el-button
              type="text"
              @click="selectSeason(1)"
            >
              <!-- 六月 -->
              {{ $t("time.month")[5] }}
            </el-button>
          </div>
          <div class="text item">
            <el-button
              type="text"
              @click="selectSeason(0)"
            >
              <!-- 七月 -->
              {{ $t("time.month")[6] }}
            </el-button>
            <el-button
              type="text"
              @click="selectSeason(1)"
            >
              <!-- 八月 -->
              {{ $t("time.month")[7] }}
            </el-button>
            <el-button
              type="text"
              @click="selectSeason(1)"
            >
              <!-- 九月 -->
              {{ $t("time.month")[8] }}
            </el-button>
          </div>
          <div class="text item">
            <el-button
              type="text"
              @click="selectSeason(0)"
            >
              <!-- 十月 -->
              {{ $t("time.month")[9] }}
            </el-button>
            <el-button
              type="text"
              @click="selectSeason(1)"
            >
              <!-- 十一月 -->
              {{ $t("time.month")[10] }}
            </el-button>
            <el-button
              type="text"
              @click="selectSeason(1)"
            >
              <!-- 十二月 -->
              {{ $t("time.month")[11] }}
            </el-button>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>
<script>
/**
 * @file:  季节 年度 半年度 选择控件
 * @author: zhaomz1
 * @date: 2019-05-23
 * @description: UI组件  可选择季节
 * @api: valueArr : 季度value defalut['01-03', '04-06', '07-09', '10-12'] 默认值待设置
 */
export default {
  name: 'MultiFunDate',
  model: {
    event: 'change',
    value: 'value'
  },
  props: {
    valueArr: {
      default: () => {
        return ['01-03', '04-06', '07-09', '10-12']
      },
      type: Array
    },
    type: {
      // 选择类型 季度quarter | 年度year | 半年度halfYear
      type: String,
      default: 'quarter'
    },
    placeholder: {
      type: String,
      default: ''
    },
    getValue: {
      default: () => {},
      type: Function
    },
    defaultValue: {
      default: '',
      type: String
    }
  },
  data () {
    return {
      showSeason: false,
      season: '',
      curYear: new Date().getFullYear(),
      year: new Date().getFullYear(),
      showValue: ''
    }
  },
  watch: {
    defaultValue: function (value, oldValue) {
      let arr = value.split('-')
      this.year = arr[0].slice(0, 4)
      let str = arr[0].slice(4, 6) + '-' + arr[1].slice(4, 6)
      let arrAll = this.valueArr
      this.showValue =
        `${this.year}` +
        this.$t('time.year') +
        `${arrAll.indexOf(str) + 1}` +
        this.$t('time.quarter')
    }
  },
  created () {
    if (this.defaultValue) {
      let value = this.defaultValue
      let arr = value.split('-')
      this.year = arr[0].slice(0, 4)
      let str = arr[0].slice(4, 6) + '-' + arr[1].slice(4, 6)
      let arrAll = this.valueArr
      this.showValue =
        `${this.year}` +
        this.$t('time.year') +
        `${arrAll.indexOf(str) + 1}` +
        this.$t('time.quarter')
    }
  },
  methods: {
    one () {
      this.showSeason = false
    },
    prev () {
      if (this.year > this.curYear) {
        this.year = this.year * 1 - 1
      }
    },
    next () {
      this.year = this.year * 1 + 1
    },
    selectSeason (i) {
      let that = this
      that.season = i + 1
      let arr = that.valueArr[i].split('-')
      that.getValue(that.year + arr[0] + '-' + that.year + arr[1])
      that.showSeason = false
      this.showValue =
        `${this.year}` +
        this.$t('time.year') +
        `${this.season}` +
        this.$t('time.quarter')
    }
  }
}
</script>
<style lang="scss" scoped>
.select-content {
  clear: both;
  overflow: hidden;
  zoom: 1;
}
</style>
