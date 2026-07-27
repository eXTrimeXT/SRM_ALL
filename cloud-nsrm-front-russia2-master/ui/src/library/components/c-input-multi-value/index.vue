<template>
  <div class="c-input-multi-value">
    <el-input
      :value="multiValue"
      clearable
      v-bind="$attrs"
      @input="handleChange"
      v-on="$listeners"
    >
      <em slot="suffix" :class="['iconfont', icon, 'search-po']" @click="openQueryDialog" />
    </el-input>
    <!-- 合同编号名称弹窗 -->
    <!-- 格式化本文 -->
    <srm-dialog
      :title="$t('cusEntry.library.textFormat')"
      size="small"
      append-to-body
      :visible.sync="multiVisible"
    >
      <template slot="header">
        <slot v-if="multiHeader" name="dialogHeader" />
        <span v-else class="el-dialog__title">
          {{ multiTitle }} <span class="write-tip">{{ mutilDesc }}</span>
        </span>
      </template>
      <el-input v-model="writeText" type="textarea" :rows="10" />
      <div slot="footer">
        <el-button @click="multiVisible = false">
          {{ $t('common.cancel') }}
        </el-button>
        <el-button type="primary" @click="handleMultiConfirm">
          {{ $t('common.confirm') }}
        </el-button>
      </div>
    </srm-dialog>
  </div>
</template>

<script>
export default {
  name: 'CInputMultiValue',
  model: {
    prop: 'multiValue',
    event: 'change'
  },
  props: {
    // input绑定值
    multiValue: {
      type: Array,
      default: () => {
        return []
      }
    },
    multiHeader: {
      type: Boolean,
      default: () => {
        return false
      }
    },
    // 弹窗标题
    multiTitle: {
      type: String,
      default: () => {
        return ''
      }
    },
    // 弹窗头描述
    mutilDesc: {
      type: String,
      default: () => {
        return ''
      }
    },
    // input点击icon
    icon: {
      type: String,
      default: () => {
        return 'iconselect'
      }
    }
  },
  data () {
    return {
      multiVisible: false,
      writeText: ''
    }
  },
  methods: {
    handleChange (val) {
      this.multiValue = val.split(',')
      this.$emit('change', val)
    },
    // 打开多行输入选框回写入值
    inputLineSetString (str) {
      // 判断是不是数组对数组数据或者字符串数据做处理
      const isArray = Array.isArray(str)
      return isArray ? str.join('\n') : str.split(',').join('\n')
    },
    openQueryDialog () {
      this.multiVisible = true
      this.writeText = this.inputLineSetString(this.multiValue)
    },
    // 处理回车换行数据
    setLineString (str) {
      // 根据换行符转化成数组
      let arr = str.split(/[\r\n]+/)
      // 去除划分后的空白符
      let newArr = arr.map((r) => r.replace(/\s+/g, ''))
      return newArr
    },
    handleMultiConfirm () {
      this.multiValue = this.setLineString(this.writeText).join(',')
      this.$emit('handleMultiConfirm', this.multiValue)
      this.multiVisible = false
    }
  }
}
</script>

<style scoped lang="scss">
.c-input-multi-value {
  .search-po {
    float: right;
    cursor: pointer;
  }
}
</style>
