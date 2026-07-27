<template>
  <!-- 抱歉，您的页面找不到了！ -->
  <c-exception
    type="404"
    title="404"
    :sub-title="$t('errorPage.page404')"
  />
</template>

<script>
import CException from 'lib@/components/c-exception'

export default {
  name: 'Exception404',
  components: {
    CException
  }
}
</script>
