<template>
  <!-- 抱歉，您没有权限访问该页面 -->
  <c-exception
    type="401"
    title="401"
    :sub-title="$t('errorPage.page401')"
  />
</template>

<script>
import CException from 'lib@/components/c-exception'

export default {
  name: 'Exception401',
  components: {
    CException
  }
}
</script>
