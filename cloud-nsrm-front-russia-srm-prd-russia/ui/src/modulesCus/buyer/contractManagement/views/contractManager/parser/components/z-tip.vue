<template>
  <span
    :data-key="code"
    data-is-tip="true"
  >{{ componentInfo.initValue }}</span>
</template>
<script>
import minix from './minix'
export default {
  name: 'ZTip',
  mixins: [minix]
}
</script>
