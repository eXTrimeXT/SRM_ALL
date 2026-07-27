<template>
  <a
    :class="['c-download-link', { ellipsis: ellipsis }]"
    :title="name"
    href="javascript: void(0);"
    rel="noopener"
    @click="handleClick"
  >
    {{ name }}
  </a>
</template>

<script>
import { downloadWithParam } from 'lib@/utils/file'
import { sysPrefix } from '@/config/ipConfig'

export default {
  name: 'CDownloadLink',
  props: {
    id: {
      type: [String, Number],
      required: true
    },
    name: {
      type: String,
      required: true
    },
    ellipsis: {
      type: Boolean,
      default: true
    }
  },
  data () {
    return {
    }
  },
  methods: {
    handleClick () {
      if (this.id) {
        downloadWithParam(
          this.id,
          this.name
        ).catch(() => {
          this.$message.error(this.$t('components.eio.downloadFail')) // 下载失败
        })
      } else {
        throw new Error('AttachId is null.')
      }
    }
  }
}
</script>

<style lang="scss">
.c-download-link {
  &.ellipsis {
    width: auto;
    max-width: 90%;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    word-wrap: normal;
    display: inline-block;
    vertical-align: middle;
    height: 30px;
    line-height: 30px;
    color: #409eff;
  }
}
</style>
