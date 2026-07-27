<template>
  <div class="the_check_groups">
    <SrmRow>
      <!--隐藏评标关键信息-->
      <SrmCol :init-col="3">
        <el-form-item prop="extHideKeyInfo">
          <el-checkbox
            v-model="biddingBaseInfo.extHideKeyInfo"
            true-label="Y"
            false-label="N"
          >
            是否盲评
          </el-checkbox>
        </el-form-item>
      </SrmCol>
    </SrmRow>
  </div>
</template>

<script>
/**
 * 报价控制
 */
export default {
  name: 'BiddingControl',

  props: {
    biddingBase: {
      type: Object,
      required: true
    }
  },

  computed: {
    biddingBaseInfo: {
      get: function () {
        return this.biddingBase
      },
      set: function (val) {
        this.$emit('update:biddingBase', val)
      }
    }
  },

  methods: {

  }
}
</script>
