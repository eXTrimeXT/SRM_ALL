<template>
  <span :data-key="code">
    <img
      :style="{
        minWidth: `${width_height.width}px`,
        maxHeight: `${width_height.height}px`
      }"
      :src="initValue"
    />
  </span>
</template>
<script>
import minix from './minix'
export default {
  name: 'ZInput',
  mixins: [minix],
  computed: {
    width_height () {
      const wh = this.componentInfo.width || ''
      if (wh.indexOf(',') !== -1) {
        const [width, height] = wh.split(',')
        return { width, height }
      }
      return { width: 120, height: 120 }
    },
    initValue () {
      return this.componentInfo.initValue
    }
  }
}
</script>
