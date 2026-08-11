<template>
  <span :data-key="code">{{ componentInfo.initValue }}</span>
</template>
<script>
import minix from './minix'
export default {
  name: 'ZText',
  mixins: [minix]
}
</script>
