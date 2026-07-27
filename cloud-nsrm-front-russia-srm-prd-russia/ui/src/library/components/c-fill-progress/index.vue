<template>
  <!-- 填写进度调区域 -->
  <div :class="['info-fill-progress', { buttomLeave: isBottom }]">

    <div class="progressCont">
      <div
        v-for="(item, index) in data"
        :key="index"
        :class="['progressItem', { current: cur === index }]"
        @click="handleClick(item.code, index)"
      >
        <div class="progress-title">
          {{ item.name }}
        </div>
        <!-- 下面注释掉的是进度条 -->
        <!-- <el-progress
          v-if="item.percentage != 'NAN'"
          class="progress-bar"
          :stroke-width="8"
          :percentage="item.percentage"
        /> -->
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'CFillProgress',
  props: {
    nodeName: {
      type: String,
      required: true
    },
    percentage: {
      type: Boolean,
      default: () => {
        return true
      }
    },
    data: {
      type: Array,
      default: () => {
        return []
      }
    },
    isHeader: {
      type: Boolean,
      default: () => {
        return true
      }
    },
    isBottom: {
      // 是否bottom值为0
      type: Boolean,
      default: () => {
        return true
      }
    },
    /* 激活的菜单下标 */
    activeNavIndex: {
      type: Number,
      default: 0
    }
  },
  data () {
    return {
      cur: 0
    }
  },
  watch: {
    activeNavIndex (newValue, oldValue) {
      if (newValue !== oldValue) {
        this.cur = newValue
      }
    }
  },
  methods: {
    handleClick (code, index) {
      this.cur = index
      this.$emit('index-click', code)
    }
  }
}
</script>

<style lang="scss">
.info-fill-progress {
  position: absolute;
  width: 23%;
  top: 0;
  right: 0;
  bottom: 0px;
  background: #edeff2 !important;
  overflow: hidden;
  overflow-y: auto;
  &.buttomLeave {
    padding-bottom: 40px;
  }
  .progressTitle {
    height: 40px;
    line-height: 40px;
    padding-left: 30px;
    background: #f7f9fa;
    font-size: 14px;
  }
  .progressCont {
    padding: 6px 0 18px 0;
    .progressItem {
      padding: 0px 22px;
      border-radius: 3px;
      cursor: pointer;
      .progress-title {
        font-size: 12px;
        color: #51555B;
        padding: 4px 8px;
        min-height: 20px;
        line-height: 20px;
        border-left: 2px solid #DCDDDE;
      }
      .progress-bar {
        height: 15px;
      }
      &.current {
        .progress-title{
          color: #0077FF;
          border-left: 2px solid #0077FF;
        }
      }
    }
  }
}
</style>
<style>
.info-fill-progress .el-progress-bar {
  padding-right: 70px;
  margin-right: -70px;
}
</style>
