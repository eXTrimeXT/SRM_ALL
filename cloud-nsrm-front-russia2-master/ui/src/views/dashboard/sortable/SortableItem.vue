<template>
  <li class="list-item">
    <div class="icon-wrap">
      <img
        v-if="item.icon"
        :src="item.icon"
        width="40px"
        height="40px"
      >
      <div
        v-else
        style="width:40px;height:40px;background:#ddd;border-radius:10px;"
      />
      <div
        class="hot-area"
        @click="remove"
      >
        <i class="el-icon-remove-outline remove" />
      </div>
    </div>
    <span
      :title="item.title"
      class="title"
    >{{ item.title }}</span>
  </li>
</template>
<script>
import { ElementMixin } from 'vue-slicksort'

export default {
  name: 'SortableList',
  mixins: [ElementMixin],
  props: {
    item: {
      type: Object,
      default: null
    }
  },
  methods: {
    remove () {
      this.$emit('on-remove', this.index, this.item.id, this.item.parentId)
    }
  }
}
</script>
<style lang="scss" scoped>
.list-item {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  text-align: center;
  list-style: none;
  padding: 10px 0;
  width: 100px;
  z-index: 9999;
  position: relative;
  cursor: move;
  .hot-area {
    display: none;
    justify-content: center;
    align-items: center;
    width: 20px;
    height: 20px;
    position: absolute;
    cursor: pointer;
    right: -12px;
    top: -10px;
  }
  &:hover{
    .hot-area{
      display: flex;
    }
  }
}
.title {
  margin-top: 10px;
  max-width: 90px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.icon-wrap {
  position: relative;
}

</style>
