<template>
  <div class="iframe-preivew-box" v-if="iframeUrl.length">
    <iframe
            :name="$t('route.document')"
            style="border:none; width: 100%;height:calc(100% - 3px);margin:0;padding:0;overflow: hidden;"
            :src="`${iframeUrl}`"
            id="iframe-preview-box"
            scrolling="yes"
            ref="fileFrame" />
  </div>
</template>

<script setup>

const props = defineProps({
  clarifyFileId: Number
})

// type="application/pdf"
import { systemUrl } from '@/config/sysConfig'
import { sysPrefix } from '@/config/ipConfig'

let url = `${systemUrl}${sysPrefix()}`
let iframeUrl = ref(``)
console.log(iframeUrl.value)

let fileFrame = ref(null)

watch(
  () => props.clarifyFileId,
  (id) => {
    // console.log('propsPdfInfo', obj)
    // console.log('iframe展示文件id', id)
    if (id) iframeUrl.value = `${url}/api-onlineview/onlinePreview?fileuploadId=${id}`
    // if (id) iframeUrl.value = `${url}/api-onlineview/onlinePreview?fileuploadId=${id}#toolbar=0&view=FitH&menubar=0&navpanes=0&zoom=100`
  },
  {
    immediate: true
  }
)

onMounted(() => {
  console.log(fileFrame.value)
  // getFile()
})
</script>

<style lang="less" scoped>
.iframe-preivew-box {
  height: 100%;
}
</style>