<!--// 选择组织架构-->
<template>
  <div class="the_people-wrap">
    <div class="people-input-wrap">
      <el-select
        v-model="changeValueDataNew"
        class="the_org_select collapseTags"
        :disabled="disabled"
        :placeholder="placeholderText"
        :multiple="true"
        :multiple-limit="multipleLimit"
        :collapse-tags="false"
        :filterable="true"
        remote
        :remote-method="remoteMethod"
        @change="changeHandler"
        @remove-tag="removeTag"
      >
        <el-option
          v-for="(option, index) in options"
          :key="index"
          :value="option.assigneeId"
          :label="option.assigneeName"
        />
      </el-select>

      <el-button
        v-if="!disabled"
        type="text"
        :disabled="disabled"
        icon="iconfont iconselect"
        class="quick-search-btn"
        @click="openDialog()"
      />
    </div>
    <!-- 人员选择 -->
    <srm-dialog
      :title="$t('componentDoc.personnelSel')"
      class="people-selector-dialog"
      size="large"
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
        :default-value="value"
        :user-selected="userSelected"
        :dialog-visible="dialogVisible"
        :normalizer="normalizer"
        :searchData="searchData"
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
    </srm-dialog>
  </div>
</template>

<script>
import PeopleSelector from './people-selector'
import { getUserList } from '@/api/user'

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
    placeholder: {
      type: String,
      default: ''
    },
    disabled: {
      type: Boolean,
      default: function () {
        return false
      }
    },
    value: {
      type: Array,
      default: function () {
        return []
      }
    },
    userSelected: {
      type: Boolean,
      default: function () {
        return false
      }
    },
    // 是否手动选择审批人 
    flag: {
      type: Boolean,
      default: function () {
        return false
      }
    },
    isShowButton: {
      type: Boolean,
      default: true
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
    searchData: {
      type: Object,
      default: () => {}
    }
  },
  data () {
    return {
      dialogVisible: false,
      changeValueDataNew: [],
      options: []
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
    },
    value: {
      handler (data) {
      if (this.userSelected || this.flag || !this.isShowButton) {
          this.changeValueDataNew = this.changeValueData(data)
        }
      },
      immediate: true,
      deep: true
    }
  },
  mounted () {
    this.remoteMethod(' ')
  },
  methods: {
    changeHandler (value) {
      console.log(value, 'value')
      let attr = []
      if (value.length > 0) {
        value.forEach(items => {
          attr.push(this.options.filter(item => item.assigneeId === items)?.[0])
        })
      }
      console.log(attr, 'attr')
      this.$emit('on-confirm', attr)
    },
    remoteMethod (searchKey) {
      if (searchKey) {
        getUserList({ searchKey, pageNum: 1, pageSize: 10, ...this.searchData }).then(res => {
          if (res) {
            let listNew = []
            const list = res.data.list
            if (list.length > 0) {
              list.forEach(item => {
                listNew.push({
                  assigneeId: item.assigneeId,
                  assigneeName: item.assigneeName
                })
              })
            }
            // 合并去重
            const uniqueMap = new Map()
            listNew.concat(this.value).forEach(item => {
              const key = item.assigneeId
              if (!uniqueMap.has(key)) {
                uniqueMap.set(key, item)
              }
            })
            this.options = Array.from(uniqueMap.values())
          }
        })
      }
    },
    changeValueData (value) {
      let attr = []
      if (value.length > 0) {
        value.forEach(data => {
          attr.push(data.assigneeId)
        })
      }
      return attr
    },
    removeTag (value) {
      const data = this.value
      this.value = data.filter(item => item.assigneeId !== value)
      this.$emit('on-confirm', this.value)
    },
    dialogClose () {
      this.$emit('update:visible', false)
      this.dialogVisible = false
    },
    confirmSelector () {
      const selectedData = this.$refs.peopleSelector.buyerChosenData
      console.log(selectedData, 'selectedData')
      this.$emit('on-confirm', selectedData)
      this.dialogClose()
    },
    cancleSelector () {
      this.dialogClose()
    },
    // 打开弹窗
    openDialog () {
      // console.log(this.value, 'openDialog')
      this.dialogVisible = true
      if (this.$refs.peopleSelector) {
        this.$refs.peopleSelector.buyerQuery.pageNum = 1
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.the_people-wrap {
  .the_org_select {
    display: block;
    padding: 0;
    &.collapseTags{
      :deep(.el-select__tags){
        >span{
          .el-tag{
            max-width: 62%;
            background: #E7F2FF;
            border: 1px solid #A6D0FF;
            color: #0077FF;
          }
        }
      }
      :deep(.el-tag__close){
        color: #0077FF;
        margin-left: 3px;
      }
    }
  }
  .the_org_select{
    :deep(>.el-input.el-input--suffix) {
      >.el-input__inner{
        padding-right: 30px;
      }
      >.el-input__suffix{
        right: 26px;
        z-index: 10;
        >.el-input__suffix-inner{
          >.el-select__caret{
            &.el-icon-arrow-up{
              &::before{
                content: " ";
              }
            }
            &.el-icon-arrow-down{
              display: none;
            }
            &.el-icon-search{
              display: none;
            }
          }
        }
      }
      .el-input__icon {
        width: 16px;
      }
    }
  }
  .people-input-wrap {
    position: relative;
  }
  .people-input {
    height: 40px;
    overflow-y: auto;
    margin: 0;
    flex-grow: 2;
    padding: 4px 26px 4px 4px;
    // border: 1px solid rgb(185, 186, 189);
    display: flex;
    flex-wrap: wrap;
    span {
      display: block;
    }
}

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
    border-radius: 0 4px 4px 0;
    padding: 4px 6px !important;
    color: #96999c;
  }
}
</style>
<style>
.srm-body .vxe-table .vxe-body--column .vxe-cell .people-input-wrap .el-button.quick-search-btn.el-button--text{
  width: 30px !important;
  min-width: 30px !important;
}
</style>
