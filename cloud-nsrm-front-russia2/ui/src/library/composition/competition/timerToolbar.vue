<template>
  <MainHeader :l-span="12" :r-span="12">
    <template #left>
      <!--刷新-->
      <el-button
        type="primary"
        class="gep-right-15"
        @click="$emit('refresh')"
      >
        {{ $t('common.refresh') }}
      </el-button>

      <span v-if="showTips">{{ $t('bidMod.competitionLts.hallToolbar1') }}</span>
      <span v-if="role === 'BUYER'" style="margin-left:30px;">{{ $t('bidMod.currentRound') }}：</span>
      <el-select v-if="role === 'BUYER'" v-model="currentRound" @change="changeRound">
        <el-option v-for="item in round" :key="item" :value="item" :label="item" />
      </el-select>
    </template>

    <template #right>
      <span class="gep-right-15" style="font-size: 12px">{{ $t('bidMod.competitionLts.hallToolbar2') }}</span>

      <el-switch
        v-model="autoRefresh"
        class="gep-right-15"
        @change="autoRefreshChange"
      />

      <!--关闭自动刷新才能编辑频次-->
      <el-input
        v-model="frequency"
        v-input-format="{ type: 'integer' }"
        :disabled="autoRefresh"
        class="frequency-input"
      >
        <template #prepend>
          <!-- 频次  -->
          {{ $t('bidMod.competitionLts.hallToolbar3') }}
        </template>
        <template #append>
          <!-- 秒/次 -->
          {{ $t('bidMod.competitionLts.hallToolbar4') }}
        </template>
      </el-input>
    </template>
  </MainHeader>
</template>

<script>
/**
 * 操作工具栏
 */
import { isInteger } from 'lodash'
import MainHeader from 'lib@/components/Table/MainHeader.vue'

export default {
  name: 'TimerToolbar',

  components: { MainHeader },

  props: {
    // 初始配置定时频次
    initFrequency: {
      type: Number,
      default: 60
    },
    // 是否显示提示
    showTips: {
      type: Boolean,
      default: true
    },
    // 是否默认自动刷新
    auto: {
      type: Boolean,
      default: false
    },
    round: {
      type: Number,
      default: 1
    },
    role: {
      type: String,
      default: 'BUYER' // 采购商界面
    }
  },

  data () {
    return {
      autoRefresh: false,
      frequency: 60,
      timer: null,
      currentRound: 1
    }
  },

  watch: {
    initFrequency: {
      handler (val) {
        this.frequency = val
      },
      immediate: true
    },
    auto: {
      handler (val) {
        if (val) {
          this.autoRefresh = true
          this.autoRefreshChange(this.autoRefresh)
        }
      },
      immediate: true
    }
  },

  beforeDestroy () {
    this.clearTimer()
  },

  methods: {
    /* 自动刷新开启关闭 */
    autoRefreshChange (val) {
      if (val) {
        // 开启
        // 判断频次是否是数字
        if (!isInteger(this.frequency)) {
          // 频次请输入正整数！
          this.$message.warning(this.$t('bidMod.competitionLts.hallToolbar5'))
          return
        }

        // 先清除
        this.clearTimer()

        this.timer = setInterval(() => {
          this.$emit('refresh')
        }, this.frequency * 1000)
      } else {
        // 关闭
        this.clearTimer()
      }
    },

    changeRound (round) {
      if (round) {
        this.$emit('change-round', round)
      }
    },

    /* 清除定时器 */
    clearTimer () {
      if (this.timer) {
        clearInterval(this.timer)
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.gep-right-15 {
  margin-right: 15px;
}
.frequency-input {
  width: 160px;
  ::v-deep .el-input__inner {
    text-align: center;
  }
}
</style>
