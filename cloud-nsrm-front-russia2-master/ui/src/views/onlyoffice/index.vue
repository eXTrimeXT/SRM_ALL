<template>
  <div>
    <OnlyofficeView v-bind="post" />
  </div>
</template>

<script>
import OnlyofficeView from './components/OnlyofficeView.vue'
import { getToken } from '@/utils/auth'

const token = "Bearer " + getToken()
export default {
  components: {
    OnlyofficeView
  },
  created() {
    const { title, url, fileType, key, storage, callbackUrl, comment, revision, hideRevision, readonly } = this.$route.query
    this.post = {
      title,
      url: decodeURIComponent(atob(url)),
      fileType,
      storage,
      fileKey: key,
      callbackUrl: decodeURIComponent(atob(callbackUrl)) + `&token=${token}`,
      comment,
      revision,
      hideRevision: [true, 'true'].includes(hideRevision),
      edit: ![true, 'true'].includes(readonly)
    }
  },
  data() {
    return {
      post: {
        title: null,
        url: null,
        fileType: null,
        storage: null,
        fileKey: null,
        callbackUrl: null,
        comment: null,
        revision: null
      }
    }
  }
}
</script>
