<template>
  <div>
    <el-popover
      v-if="(addMethod === 'IMAGE' && value)"
      placement="top"
      width="300"
      trigger="hover"
    >
      <el-image
        style="max-width: 300px"
        :src="value"
        fit="contain"
      />
      <el-button
        slot="reference"
        icon="el-icon-picture"
        type="text"
      >
        {{ $t('common.preview') }}
      </el-button>
    </el-popover>
    <span v-else-if="!!addMethod && addMethod !== 'IMAGE'">{{ value }}</span>
    <span v-else></span>
  </div>
</template>

<script>
export default {
  name: 'InitReadComponent',
  props: {
    addMethod: {
      type: String,
      default: ''
    },
    value: {
      type: String,
      default: ''
    }
  },
  data () {
    return {
      data: ''
    }
  },
  mounted () {},
  methods: {}
}
</script>
