<template>
  <srm-row class="inquiry-tracking-info">
    <srm-col
      v-for="(item, index) in infoRowList"
      :key="`infoCol-${index}`"
      :initCol="24 / span"
    >
      <p class="info-col-p">
        <span class="label">{{ item.label }}:</span>
        <span
          class="value"
          :title="item.value"
        >{{ item.value }}</span>
      </p>
    </srm-col>
  </srm-row>
</template>

<script>
/**
 * 展示信息
 */
export default {
  name: 'InfoRow',
  props: {
    // {{ label: '', value: '' }}
    infoRowList: {
      type: Array,
      default: () => []
    },
    span: {
      type: Number,
      default: 8
    }
  }
}
</script>

<style lang="scss" scoped>
.inquiry-tracking-info {
  padding: 10px 16px;
  margin-top: 10px;
  .info-col-p {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    .value {
      padding-left: 15px;
    }
  }
}
</style>
