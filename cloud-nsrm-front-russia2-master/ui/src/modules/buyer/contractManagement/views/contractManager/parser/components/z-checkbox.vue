<template>
  <span :data-key="code">
    <!--    <el-checkbox-->
    <!--      :disabled="!editable"-->
    <!--      :value="value"-->
    <!--      :checked="value"-->
    <!--      @change="change"-->
    <!--    />-->
    <span v-if="value == true">
      <input
        v-model="value"
        type="checkbox"
        aria-hidden="false"
        :disabled="!editable"
        checked="true"
        @change="change(value)"
      >
    </span>
    <span v-else>
      <input
        v-model="value"
        type="checkbox"
        aria-hidden="false"
        :disabled="!editable"
        @change="change(value)"
      >
    </span>
  </span>
</template>
<script>
import minix from './minix'
export default {
  name: 'ZCheckbox',
  mixins: [minix]
}
</script>
