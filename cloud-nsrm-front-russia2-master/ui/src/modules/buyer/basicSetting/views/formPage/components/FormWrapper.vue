<template>
  <div
    class="query-form-container-form-page form-info-detail-page"
    :class="{ formExpand: isActive }"
  >
    <div class="btnGroup">
      <el-button
        type="primary"

        class="searchBtn"
        @click="query"
      >
        {{ $t("common.search") }}
      </el-button>
      <el-button

        class="resetBtn"
        @click="reset"
      >
        {{ $t("common.reset") }}
      </el-button>
      <el-button
        type="text"
        :class="['toggleBtn', { btnExpand: isActive }]"

        @click="handleClick"
      >
        {{ toggleTex }}
        <em class="el-icon-arrow-down" />
      </el-button>
    </div>
    <el-form
      ref="formData"
      :model="value"
      v-bind="$attrs"
      v-on="$listeners"
    >
      <el-row :gutter="32">
        <slot :scope="value" />
      </el-row>
    </el-form>
  </div>
</template>
<script>

export default {
  name: 'FormWrapper',
  props: {
    // 初始化展开或者收缩form的状态
    initActive: {
      type: Boolean,
      default: false
    },
    // 当前form绑定的 data 对象
    formArray: {
      type: Array,
      default: null
    },
    value: {
      type: Object,
      default: null
    }
  },
  data () {
    return {
      isActive: false,
      toggleTex: this.$t('common.expandForm')
    }
  },
  async created () {
    this.isActive = this.initActive
  },
  methods: {
    query () {
      this.$refs['formData'].validate(valid => {
        if (valid) {
          this.$emit('query', this.value)
        } else {
          return false
        }
      })
    },
    // 切换开合
    handleClick () {
      this.isActive = !this.isActive
      this.toggleTex = this.isActive
        ? this.$t('common.collapseForm')
        : this.$t('common.expandForm')
    }
  }
}
</script>
<style scoped>
.query-form-container-form-page.form-info-detail-page{
  border: 0;
  padding: 0;
}
.query-form-container-form-page.form-info-detail-page .el-form{
  padding-top: 16px;
  margin: 0;
}
.vue-treeselect__label {
  font-size: 12px;
  font-weight: 400;
}
.form-info-detail-page .el-form-item--small .el-form-item__content {
  height: 28px !important;
  line-height: 28px !important;
}
.form-info-detail-page .el-form-item__label {
  padding-right: 8px;
  white-space: normal;
  line-height: 15px !important;
  vertical-align: middle;
  max-height: 30px;
  float: none !important;
  display: inline-block !important;
  width: 35% !important;
  /* 自适应用这两行 */
  /* max-width: 55%;
  width: auto !important; */
  box-sizing: border-box;
  overflow: hidden;
  /* text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2; */
}
.form-info-detail-page .el-form-item__content {
  vertical-align: middle;
  display: inline-block;
  margin-left: 0 !important;
  width: 65% !important;
  box-sizing: border-box;
  /* 自适应开启 */
  /* flex: 1; */
}
.input_group__append .el-input-group__append{
  padding: 0 12px;
  text-align: center;
}
.the_el_input_group__append {
  padding: 8px 0px;
  min-width: 20px;
  text-align: center;
}
</style>
<style scoped lang="scss">
.query-form-container-form-page {
  height: 62px;
  position: relative;
  overflow: hidden;
  .el-form {
    padding-right: 215px;
    margin-top: 0px;
  }
  .btnGroup {
    position: absolute;
    top: 16px;
    right: 0px;
    z-index: 994;
    .toggleBtn {
      font-size: 12px;
      min-width: 50px;
      padding-left: 0;
      padding-right: 0;
      color: #51555B;
    }
    .searchBtn,
    .resetBtn {
      padding: 8px 6px;
      width: 58px;
    }
  }
  .el-form .el-row .el-form-item:first-child {
    margin-bottom: 16px;
    /* 自适应开启 */
    // display: flex;
    // flex-direction: row;
    // flex-wrap: nowrap;
    // justify-content: space-between;
    // align-items: center;
  }
  &.form-info-detail-page {
    border-right: 0;
    border-left: 0;
    border-top: 0;
    flex: none;
    .hiddenCol{
      display: none;
    }
  }
}
.formExpand {
  height: auto !important;
}
.btnExpand i {
  transform: rotate(180deg);
}

</style>
