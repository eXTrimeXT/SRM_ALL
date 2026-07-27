<script setup lang="ts">
import { Container } from '@meicloud/element-ui'

defineProps({
  ...Container.props,
  direction: {
    type: String,
    default: 'vertical'
  }
})
</script>

<template>
  <Container class="page-layout">
    <slot />
  </Container>
</template>

<style lang="scss" scoped>
.page-layout {
  padding: 16px;
}
</style>
