<template>
  <!-- 抱歉，系统未找到您的组织单位，请联系管理员。 -->
  <c-exception
    type="401"
    title="402"
    :sub-title="$t('errorPage.page402')"
  />
</template>

<script>
import CException from 'lib@/components/c-exception'

export default {
  name: 'Exception402',
  components: {
    CException
  }
}
</script>
