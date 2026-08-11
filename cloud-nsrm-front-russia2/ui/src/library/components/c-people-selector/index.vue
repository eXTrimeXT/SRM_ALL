<!--// 选择组织架构-->
<template>
  <div class="the_people-wrap">
    <el-row
      v-if="selectType === 'input'"
      style="padding: 0;"
    >
      <!-- input输入 -->
      <el-col
        :span="24"
        style="position: relative;padding: 0;"
      >
        <el-select
          class="the_people_select"
          :disabled="disabled"
          :value="value"
          clearable
          filterable
          reserve-keyword
          remote
          popper-class="cat-select"
          :placeholder="placeholderText"
          :remote-method="querySearchAsync"
          :loading="loading"
          :no-data-text="$t('components.noData')"
          :loading-text="$t('components.loading')"
          @change="inputSelect"
          @clear="clearOptions"
          @focus="focus"
        >
          <el-option
            v-for="(item, key) in selectData"
            :key="key"
            class="option-item"
            :value="item.username"
            :label="item.username + '/' + item.nickname + '/' + item.department"
          />
        </el-select>
        <el-button
          :disabled="disabled"
          icon="iconfont iconselect"
          class="quick-search-btn"
          @click="openDialog()"
        />
      </el-col>
    </el-row>
    <!-- 人员选择 -->
    <el-dialog
      :title="$t('componentDoc.personnelSel')"
      class="people-selector-dialog"
      width="800px"
      :visible.sync="dialogVisible"
      :append-to-body="true"
      :destroy-on-close="false"
      @close="dialogClose"
    >
      <!-- 选择人员内容 -->
      <PeopleSelector
        ref="peopleSelector"
        :reset-select="resetSelect"
        :multi-select="multiSelect"
        :default-value="defaultValue"
        :filter-input="showInput"
        :dialog-visible="dialogVisible"
        :user-type="userType"
        :normalizer="normalizer"
      />
      <div
        slot="footer"
        class="dialog-footer"
      >
        <el-button @click="cancleSelector">
          <!-- 取 消 -->
          {{ $t("common.cancel") }}
        </el-button>
        <el-button
          type="primary"
          @click="confirmSelector"
        >
          <!-- 确 定 -->
          {{ $t("common.confirm") }}
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import PeopleSelector from './people-selector'
import { listByBuyer } from '@/api/user'

export default {
  name: 'CPeopleSelector',
  components: {
    PeopleSelector
  },
  model: {
    event: 'change',
    value: 'value'
  },
  props: {
    selectType: {
      // input || dialog
      type: String,
      default: function () {
        return 'dialog'
      }
    },
    placeholder: {
      type: String,
      default: ''
    },
    // 显示隐藏
    disabled: {
      type: Boolean,
      default: function () {
        return false
      }
    },
    // 当前行上绑定的 data 对象
    scope: {
      type: Object,
      default: () => {}
    },
    value: {
      type: [String, Number],
      default: null
    },
    normalizer: {
      type: Function,
      default: null
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
    userType: {
      type: String,
      default: 'BUYER'
    }
  },
  data () {
    return {
      dialogVisible: false,
      showInput: '',
      loading: false,
      selectData: [], // 下拉数据列表
      buyerQuery: {
        queryName: '',
        userType: this.userType,
        pageNum: 1,
        pageSize: 30
      }
    }
  },
  computed: {
    placeholderText () {
      return this.placeholder || this.$t('common.pleaseSelect')
    }
  },
  watch: {
    visible (value) {
      this.dialogVisible = value
    }
  },
  methods: {
    dialogClose () {
      this.$emit('update:visible', false)
      this.dialogVisible = false
    },
    confirmSelector () {
      const selectedData = this.$refs.peopleSelector.buyerChosenData
      this.$emit('change', selectedData[0].username)
      this.$emit('on-confirm', selectedData, this.scope)
      this.dialogClose()
    },
    cancleSelector () {
      this.dialogClose()
    },
    // 异步查询 , cb
    querySearchAsync (queryVal) {
      let query = {}
      let results = []
      query.categoryName = queryVal
      clearTimeout(this.timeout)
      this.timeout = setTimeout(() => {
        this.loading = true
        this.buyerQuery.queryName = queryVal
        listByBuyer(this.buyerQuery).then(res => {
          this.loading = false
          results = res.data.list
          this.selectData = results
        })
      }, 1000 * Math.random())
    },
    // 选择数据
    inputSelect (value) {
      let showKeyVal = value
      if (value) {
        const catRow = this.selectData.find(item => {
          return item.username === value
        })
        let selectRows = [catRow]
        this.$emit('on-confirm', selectRows, this.scope)
      }
      this.$emit('change', showKeyVal)
    },
    // 清空
    clearOptions () {
      this.selectData = []
      this.buyerQuery.queryName = ''
      this.$emit('on-confirm', null, this.scope)
    },
    focus () {
      this.clearOptions()
    },
    // 打开弹窗
    openDialog () {
      this.dialogVisible = true
      this.selectData = []
    }
  }
}
</script>

<style lang="scss" scoped>
.the_people-wrap {
  .people-selector-dialog {
    max-width: 800px;
    min-width: 660px;
    .dialog-footer {
      text-align: center;
    }
  }
  .quick-search-btn {
    position: absolute;
    top: 1px;
    bottom: 1px;
    right: 1px;
    border: none;
    min-width: 20px;
    border-radius: 0 4px 4px 0;
    padding: 4px 6px !important;
    color: #96999c;
  }
}
</style>
<style>
.the_people_select .el-input__suffix {
  right: 24px;
}
</style>
