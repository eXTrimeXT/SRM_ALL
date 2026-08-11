<!--// 选择组织架构-->
<template>
  <div class="the_org_tree">
    <el-row class="quick-tree-group">
      <el-col :span="24">
        <el-input
          v-model.trim="showInput"
          :disabled="disabled"
          :title="showInput"
          class="section-search"
          clearable
          @clear="clearOptions"
          @keyup.enter.native="searchFilterEnter"
        >
          <el-button
            slot="append"
            icon="el-icon-arrow-right"
            @click="searchFilterEnter"
          />
        </el-input>
      </el-col>
    </el-row>
    <!-- 选择组织架构 -->
    <el-dialog
      v-el-drag-dialog
      :title="$t('components.selectOrganization')"
      class="organization-selector-dialog"
      :visible.sync="dialogVisible"
      :append-to-body="true"
      @close="dialogClose"
    >
      <!-- 选择人员内容 -->
      <organization-selector
        ref="organizationSelector"
        :reset-select="resetSelect"
        :multi-select="multiSelect"
        :default-value="defaultValue"
        :filter-input="showInput"
        :dialog-visible="true"
      />
      <div
        slot="footer"
        class="dialog-footer"
      >
        <el-button @click="cancleSelector">
          {{ $t("common.cancel") }}
        </el-button>
        <el-button
          type="primary"
          @click="confirmSelector"
        >
          {{ $t("common.confirm") }}
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import OrganizationSelector from './organization-selector'

export default {
  name: 'COrganizationSelector',
  components: {
    OrganizationSelector
  },
  props: {
    // 禁用
    disabled: {
      type: Boolean,
      default: false
    },
    // 是否多选
    multiSelect: {
      type: Boolean,
      default: true
    },
    // 多选限制
    multipleLimit: {
      type: Number,
      default: 0
    },
    // 重置选择器状态
    resetSelect: {
      type: Boolean,
      default: true
    },
    visible: {
      type: Boolean,
      default: false
    },
    // 默认选中的值
    defaultValue: {
      type: Array,
      default: () => {
        return []
      }
    },
    // 当前行上绑定的 data 对象
    scopeData: {
      type: Object,
      default: function () {
        return null
      }
    },
    inputName: {
      // 父页面传值input
      type: String,
      default: function () {
        return ''
      }
    }
  },
  data () {
    return {
      dialogVisible: false,
      showInput: ''
    }
  },
  watch: {
    visible (value) {
      this.dialogVisible = value
    },
    inputName: {
      immediate: true,
      handler: function (val) {
        this.showInput = val
      }
    }
  },
  methods: {
    dialogClose () {
      // this.$emit('update:visible', false)
      // this.$emit('close')
      this.dialogVisible = false
    },
    confirmSelector () {
      const selectedOrg = this.$refs.organizationSelector.selectedOrg
      this.$emit('on-confirm', selectedOrg, this.scopeData)
      this.showInput = selectedOrg.parentOrganizationName
        ? selectedOrg.parentOrganizationName +
          '-' +
          selectedOrg.organizationName
        : selectedOrg.organizationName
      this.dialogClose()
    },
    clearOptions () {
      this.$emit('on-confirm', null, this.scopeData)
      this.showInput = ''
    },
    cancleSelector () {
      this.dialogClose()
    },
    searchFilterEnter () {
      this.dialogVisible = true
    }
  }
}
</script>

<style lang="scss" scoped>
.the_org_tree /deep/ {
  .quick-tree-group {
    .section-search .el-input-group__append {
      padding: 0 12px !important;
    }
  }

  .organization-selector-dialog {
    .el-dialog {
      max-width: 400px;
      min-width: 360px;
    }

    .dialog-footer {
      text-align: center;
    }
  }
}
</style>
