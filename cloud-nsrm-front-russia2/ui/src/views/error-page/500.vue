<template>
  <!-- 抱歉，服务器出错了，请稍后重试或联系管理员。 -->
  <c-exception
    type="500"
    title="500"
    :sub-title="$t('errorPage.page500')"
  >
    <template v-slot:operation>
      <el-button
        type="primary"
        @click="handleRefresh"
      >
        {{ $t("common.refresh") }}
      </el-button>
    </template>
  </c-exception>
</template>

<script>
import CException from 'lib@/components/c-exception'

export default {
  name: 'Exception500',
  components: {
    CException
  },
  methods: {
    handleRefresh () {
      const { redirect } = this.$route
      if (redirect) {
        this.$router.replace({ path: redirect })
      } else {
        this.$router.replace('/')
      }
    }
  }
}
</script>
