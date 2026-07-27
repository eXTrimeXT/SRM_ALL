<template>
  <srm-dialog
    :title="$t('dataConfMod.selectIcon')"
    :visible.sync="dialogVisible"
    append-to-body
    size="middle"
    :close-on-click-modal="false"
  >
    <div class="function-icon-list">
      <el-radio-group v-model="iconModel">
        <el-radio
          v-for="(icon, index) in iconDataList"
          :key="index"
          :label="icon.fileName"
          border
        >
          <img :src="icon.icon" width="40px">
        </el-radio>
      </el-radio-group>
    </div>

    <div slot="footer" class="dialog-footer">
      <el-button @click="dialogVisible = false">
        {{ $t('common.cancel') }}
      </el-button>
      <el-button type="primary" @click="selectIcon">
        {{ $t('common.confirm') }}
      </el-button>
    </div>
  </srm-dialog>
</template>

<script>
/**
 * 选择功能图标
 */
export default {
  name: 'FunctionIconDialog',

  props: {
    visible: {
      type: Boolean,
      required: true
    },
    icon: {
      type: [Object, String]
    }
  },

  data () {
    return {
      iconModel: '',
      iconDataList: []
    }
  },

  computed: {
    dialogVisible: {
      get: function () {
        return this.visible
      },
      set: function (val) {
        this.$emit('update:visible', val)
      }
    }
  },

  created () {
    if (this.icon) {
      this.iconModel = this.icon
    }
    // 读取@/src/assets/function-icon/目录
    const icons = import.meta.glob('../../../../../assets/function-icon/*.png', { eager: false })
    this.iconDataList = Object.keys(icons).map(item => {
      // const fileName = item.replace(/\/public\/assets\/function-icon\/(.*)/, '$1')
      const fileName = item.replace(/..\/..\/..\/..\/..\/assets\/function-icon\/(.*)/, '$1')
      return {
        url: item, // '/assets/function-icon/' + fileName,
        icon: item, // '/assets/function-icon/' + fileName,
        fileName
      }
    })
  },

  methods: {
    // 选择图标
    selectIcon () {
      this.$emit('selectIcon', this.iconModel)
      this.dialogVisible = false
    }
  }
}
</script>

<style lang="scss" scoped>
.function-icon-list {
  :deep(.el-radio.is-bordered) {
    padding: 5px;
    height: 50px;
    margin-bottom: 10px;
    margin-left: 0;
    margin-right: 10px !important;
    .el-radio__label img {
      display: inline-block;
      vertical-align: middle;
    }
  }
}
</style>
