<template>
  <div
    class="outter-form-container"
    :class="{ formExpand: isActive }"
  >
    <div class="sub_header">
      <span>{{ showTitle }}</span>
      <el-button
        type="text"
        :class="['toggleBtn',{btnExpand: isActive}]"
        @click="isActive=!isActive"
      >
        <i class="el-icon-arrow-up" />
      </el-button>
    </div>
    <div class="the_inner_div">
      <slot name="outterWrapper" />
    </div>
  </div>
</template>
<script>
export default {
  name: 'OutterFormWrapper',
  props: {
    showTitle: {
      // 想要展示的title
      type: String,
      default: function () {
        return ''
      }
    }
  },
  data () {
    return {
      isActive: true
    }
  },
  methods: {
    // 切换开合
    /* handleClick () {
      this.isActive = !this.isActive
    } */
  }
}
</script>
<style scoped lang="scss">

.outter-form-container{
  height: 30px; position: relative;
  border: 1px solid #efefef;
  overflow: hidden; margin: 3px;
  .sub_header {
    padding:8px; background: #eee; height: 30px;
    .el-button {
      float: right;
      margin: -8px;
    }
  }
  .btnExpand i{
    transform: rotate(180deg);
  }
  .the_inner_div>.el-form>.el-row:first-child {
    padding-top: 11px;
  }
}
.formExpand{
  height: auto;
}

</style>
