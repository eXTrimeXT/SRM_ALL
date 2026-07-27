<template>
  <MainHeader :l-span="12" :r-span="12">
    <template #left>
      <!--刷新-->
      <el-button type="primary" class="gep-right-15">
        {{ $t('common.refresh') }}
      </el-button>
<!-- 以下价格均为未税 -->
      <span>{{ $t('bidMod.competitionLts.hallToolbar1') }}</span>
    </template>

    <template #right>
      <!-- 启用自动刷新 -->
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
          {{ $t('bidMod.competitionLts.hallToolbar3') }}
        </template>
        <template #append>
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
  name: 'HallToolbar',

  components: { MainHeader },

  data () {
    return {
      autoRefresh: false,
      frequency: 60,
      timer: null
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
          this.$message.warning(this.$t('bidMod.competitionLts.hallToolbar5'))
          return
        }

        this.timer = setInterval(() => {
          this.$emit('refresh')
        }, this.frequency * 1000)
      } else {
        // 关闭
        this.clearTimer()
      }
    },

    /* 清除定时器 */
    clearTimer () {
      if (this.timer) {
        this.timer.clearTimer()
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
