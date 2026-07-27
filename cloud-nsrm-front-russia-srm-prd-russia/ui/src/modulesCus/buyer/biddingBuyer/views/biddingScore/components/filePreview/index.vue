<template>
  <div class="file-preview-box" v-loading="previewLoading">
    <ComIframeFile :clarifyFileId="clarifyFileId" v-if="previewType === 'clarify'" />
    <ComPdf :fileData="fileData" v-if="previewType === 'quote'" />
  </div>
</template>

<script setup>
import ComPdf from './pdf'
import ComIframeFile from './iframeFile'

const props = defineProps({
  fileData: Object,
  clarifyFileId: Number,
  previewType: String,
  previewLoading: Boolean
})

let loading = ref(true)
</script>

<style lang="less" scoped>
.file-preview-box {
  height: 100%;
  background: #dcddde;
}
</style>