<template>
  <el-container class="pdf-contain">
    <iframe
      width="100%"
      height="100%"
      :src="documentPath"
      frameborder="0"
    ></iframe>
  </el-container>
</template>
<script>
import { getToken } from '@/utils/auth'
import { sysPrefix } from '@/config/ipConfig'
import Secret from '@/utils/secret'

export default {
  name: 'Document',
  props: ['id'],
  data () {
    return {
      documentPath: ''
    }
  },
  created () {
    if (!getToken()) {
      this.$router.push({
        name: 'login',
        query: { redirect: `/document/${this.id}` }
      })
    }
    const origin = this.$systemUrl
    const prefix = `./pdfjs/web/viewer.html?file=${origin}${sysPrefix()}/api-file/file/fileupload/download`
    if (this.id) {
      const fileKey = Secret.getValue(this.id)
      this.documentPath = `${prefix}${encodeURIComponent(
        `?fileKey=${fileKey}`
      )}`
    }
  }
}
</script>
<style scoped>
.pdf-contain {
  width: 100%;
  padding: 0;
  margin: 0;
  min-width: 1110px;
  height: calc(100% - 5px);
}
</style>
