<template>
  <div class="common-file-dragger">
    <em
      v-if="draggerOptionsDefault.icon"
      :class="['dragger-icon themeColor', draggerOptionsDefault.icon]"
    />

    <p
      v-if="draggerOptionsDefault.title"
      class="dragger-title"
    >
      {{ draggerOptionsDefault.title }}
    </p>

    <p
      v-if="draggerOptionsDefault.tips"
      class="dragger-tips"
    >
      {{ draggerOptionsDefault.tips }}
    </p>
  </div>
</template>

<script>
/**
 * 拖拽上传
 */
import { uploadPropsMixin } from './util'

export default {
  name: 'CommonFileDragger',

  props: {
    // 拖拽上传触发组件配置，继承于上传组件
    draggerOptionsDefault: uploadPropsMixin.props.draggerOptions
  }
}
</script>

<style lang="scss" scoped>
@import "./common-file.scss";

.common-file-dragger {
  display: flex;
  flex-direction: column;
  justify-content: center;
  width: 100%;
  height: 100%;
  background-color: $file-wrap-background-color;
  .dragger-icon {
    font-size: 32px;
    line-height: 32px;
    // color: $link-color;
    margin: 0;
  }
  .dragger-title {
    font-size: 14px;
    font-weight: 500;
    line-height: 22px;
    margin: 16px 0 0 0;
  }
  .dragger-tips {
    font-size: 12px;
    font-weight: 400;
    color: #51555B;
    line-height: 20px;
  }
}
</style>
