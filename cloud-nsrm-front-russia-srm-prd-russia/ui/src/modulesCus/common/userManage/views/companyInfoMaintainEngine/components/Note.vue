<template>
  <div class="collapse-item-note">
    <span>{{ title }}</span>
    <el-popover
      v-model="visible"
      placement="top"
      width="400"
      trigger="manual"
    >
      <el-input
        v-model="newValue"
        type="textarea"
        maxlength="100"
        :disabled="readonly"
        @change="valueChange"
      />
      <el-button
        slot="reference"
        type="text"
        @click.stop="visible = !visible"
      >
        {{ $t('cusEntry.common.note') }}
      </el-button>
    </el-popover>
  </div>
</template>

<script>
export default {
  name: 'Note',
  props: {
    /* 标题 */
    title: {
      type: String,
      default: ''
    },
    value: {
      type: String,
      default: ''
    },
    /* 是否只读 */
    readonly: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      visible: false,
      newValue: this.value
    }
  },
  watch: {
    value (newValue, oldValue) {
      if (newValue !== oldValue) {
        this.newValue = newValue
      }
    }
  },
  methods: {
    valueChange (value) {
      this.$emit('change', value)
    }
  }
}
</script>

<style>
.collapse-item-note {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
