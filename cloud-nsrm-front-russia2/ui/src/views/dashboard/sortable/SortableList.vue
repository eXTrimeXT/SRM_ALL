<template>
  <ul class="list">
    <slot />
  </ul>
</template>
<script>
import { ContainerMixin } from 'vue-slicksort'

export default {
  name: 'SortableList',
  mixins: [ContainerMixin]
}
</script>
<style lang="scss" scoped>
  .list {
    display: flex;
    width: 100%;
    margin: 0;
    padding: 0px 0px 10px;
    list-style: none;
    justify-content: space-evenly;
  }
  .stylizedHelper {
    box-shadow: 0 10px 20px rgba(0, 0, 0, 0.2);
    background-color: rgba(31, 136, 255, 0.8);
    color: #fff;
    cursor: row-resize;
    border: 1px solid #fff;
  }
  .stylizedHelper.horizontalItem {
    cursor: col-resize;
  }
  .stylizedHelper.gridItem {
    background-color: transparent;
    white-space: nowrap;
    box-shadow: none;
  }
  .stylizedHelper.gridItem .wrapper {
    background-color: hsla(0, 0%, 100%, 0.8);
    box-shadow: 0 0 7px rgba(0, 0, 0, 0.15);
  }
</style>
