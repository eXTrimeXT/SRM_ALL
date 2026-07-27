<template>
  <div class="the_quick_search" :data-quicksearch-name="name">
    <div v-if="readPretty">
      {{ inputModel }}
    </div>
    <el-tooltip v-if="!readPretty" :disabled="!showTip" :content="inputModel" placement="bottom">
      <el-row style="padding: 0;">
        <el-col
          ref="quickSearchCol"
          :span="24"
          style="position: relative;padding: 0;"
        >
          <!-- 按钮选择形式 -->
          <template v-if="showButton">
            <el-button
              :type="btnType"
              :disabled="disabled"
              @click="openDialog('click', $event)"
            >
              {{ btnTitle }}
            </el-button>
          </template>
          <!-- 输入框搜索 + icon 弹框 -->
          <template v-else>
            <!-- 多个下拉选择 -->
            <el-popover
              ref="quickPopover"
              v-model="fuzzyPopVisible"
              placement="bottom"
              trigger="manual"
              :visible-arrow="false"
              popper-class="fuzzy-pop-warp"
            >
              <div class="fuzzy-pop-div" :loading="loading">
                <vxe-table
                  ref="mulSelectXTable"
                  border
                  show-overflow="tooltip"
                  auto-resize
                  sync-resize
                  :max-height="280"
                  :stripe="true"
                  :data="selectGridData"
                  :column-config="{isCurrent: false, isHover: false,resizable: true}"
                  :row-config="{isCurrent: true, isHover: true,useKey:true, keyField:rowKey}"
                  :radio-config="multiSelect ? null : {trigger: 'row'}"
                  :checkbox-config="multiSelect ? {trigger: 'row',reserve: true} : null"
                  @cell-click="({row,column,cell}) => getLineData(row,column,cell,'fuzzy')"
                >
                  <vxe-column
                    v-if="multiSelect"
                    type="checkbox"
                    width="50"
                    align="center"
                  />
                  <vxe-column
                    v-else
                    type="radio"
                    width="50"
                    align="center"
                  />
                  <template v-for="(item, key) in showfields">
                    <vxe-column
                      :key="key"
                      :field="item.componentType === 'DICTIONARY' ? item.name + 'View' : item.name"
                      :title="$t(`${name.replace(/\s+/g, '')}.${item.name}`)"
                      :min-width="(item.width ? item.width : 150) +'px'"
                      align="left"
                      show-overflow="tooltip"
                    />
                  </template>
                  <template #empty>
                    <div style="color: #96999c;">
                      <p>{{ $t('components.common.noMoreData') }}</p>
                    </div>
                  </template>
                </vxe-table>
                <CPagination
                  class="fuzzy-pop-div-page"
                  :total="queryTotal"
                  :page-num="viewIndex"
                  :page-size="viewSize"
                  layout="total, prev, next"
                  :pager-count="5"
                  @current-change="fuzzyChangeCurrentIndex"
                  @size-change="fuzzyChangeCurrentSize"
                />
                <div
                  v-if="multiSelect"
                  class="srm-quicksearch-footer"
                >
                  <el-button
                    type="primary"
                    @click="comfirm"
                  >
                    {{ $t('common.confirm') }}
                  </el-button>
                </div>
              </div>
              <el-input
                ref="quickSelectSelect"
                slot="reference"
                v-model="inputModel"
                class="the_quick_select"
                :clearable="selectClearable"
                :placeholder="placeholder"
                :disabled="disabled || disabledSelect"
                @input="querySearchAsync(inputModel)"
                @focus="querySearchAsync(inputModel)"
                @clear="fuzzyInputClear"
              />
            </el-popover>
            <el-button
              v-if="purchaseDsiable"
              :disabled="disabled"
              icon="iconfont iconselect"
              :class="['quick-search-btn',{'quick-edit': !disabled }]"
              @click="openDialog('click', $event)"
            />
          </template>
        </el-col>
      </el-row>
    </el-tooltip>
    <!-- 弹框 -->
    <srm-dialog
      v-if="dialogTableVisible"
      :size="dialogSize"
      class="the_quick_search_dialog"
      :title="$t(`${name.replace(/\s+/g, '')}.${name}_title`)"
      :visible.sync="dialogTableVisible"
      :close-on-click-modal="false"
      append-to-body
    >
      <div :class="['quick-select-body',{'multiSelect': multiSelect ,'device-xs': device==='device-xs'}]">
        <!-- 左边 -->
        <div class="select-body-left">
          <div class="search-wrap search-wrap-left">
            <el-form :model="form" @submit.native.prevent>
              <srm-row :gutter="16">
                <template v-for="(col, key) in tableHeader">
                  <srm-col
                    v-if="col.queryItemEnabled==='Y'"
                    :key="key"
                    :init-col="multiSelect ? 3 : 4"
                  >
                    <el-form-item :label="multiSelect ? '': $t(`${name.replace(/\s+/g, '')}.${col.attrHump}`)">
                      <DictSelect
                        v-if="col.componentType === 'DICTIONARY'"
                        v-model="form[col.attrHump]"
                        clearable
                        :code="col.componentProperty ? col.componentProperty['code'] : ''"
                        :placeholder="$t(`${name.replace(/\s+/g, '')}.${col.attrHump}`)"
                        :disabled="!!isDisable(col.name)"
                        @keyup.native.enter="query('enter')"
                      />
                      <el-date-picker
                        v-else-if="col.componentType === 'DATE'"
                        v-model="form[col.attrHump]"
                        clearable
                        :type="col.componentProperty ? col.componentProperty['type'] : 'daterange'"
                        :value-format="col.componentProperty ? col.componentProperty['value-format'] : 'yyyy-MM-dd'"
                        :default-time="['00:00:00', '23:59:59']"
                        range-separator="~"
                        :start-placeholder="$t('components.common.startTime')"
                        :end-placeholder="$t('components.common.endTime')"
                        :disabled="!!isDisable(col.name)"
                      />
                      <el-input
                        v-else
                        v-model="form[col.attrHump]"
                        clearable
                        :placeholder="$t(`${name.replace(/\s+/g, '')}.${col.attrHump}`)"
                        :disabled="!!isDisable(col.name)"
                        @keyup.native.enter="query('enter')"
                      />
                    </el-form-item>
                  </srm-col>
                </template>
              </srm-row>
              <div class="search-form-btn">
                <el-button type="primary" @click="query('enter')">
                  {{ $t("components.common.search") }}
                </el-button>
                <el-button @click="resetHandle('form')">
                  {{ $t("components.common.reset") }}
                </el-button>
              </div>
            </el-form>
          </div>
          <vxe-table
            ref="quickSearchTable"
            height="300"
            border
            show-overflow="tooltip"
            auto-resize
            sync-resize
            :stripe="true"
            :data="gridData"
            :column-config="{isCurrent: false, isHover: false,resizable: true}"
            :row-config="{isCurrent: true, isHover: true,useKey:true, keyField:rowKey}"
            :radio-config="multiSelect ? null : {trigger: 'row'}"
            :checkbox-config="multiSelect ? {trigger: 'row',reserve: true} : null"
            @current-change="({newValue,oldValue}) => handleCurrentChange(newValue,oldValue)"
            @radio-change="({newValue,oldValue}) => handleCurrentChange(newValue,oldValue)"
            @checkbox-change="(selected) => checkChange(selected.records,selected.checked, selected.row)"
            @checkbox-all="(selected) => checkChange(selected.records,selected.checked, selected.row)"
            @cell-dblclick="({row,column,cell}) => getLineData(row,column,cell,'dialog')"
          >
            <vxe-column
              v-if="multiSelect"
              type="checkbox"
              width="50"
              align="center"
            />
            <vxe-column
              v-else
              type="radio"
              width="50"
              align="center"
            />
            <vxe-column
              type="seq"
              width="54"
              :title="$t('components.common.sort')"
            />
            <template v-for="(col, key) in tableHeader">
              <vxe-column
                v-if="col.displayItemEnabled==='Y'"
                :key="key"
                :field="col.componentType === 'DICTIONARY' ? col.attrHump + 'View' : col.attrHump"
                :title="$t(`${name.replace(/\s+/g, '')}.${col.attrHump}`)"
                :min-width="col.columnWidth || 100"
                align="center"
                show-overflow="tooltip"
                :formatter="formatterText"
              />
            </template>
            <template #empty>
              <div style="color: #96999c;">
                <p>{{ $t('components.common.noMoreData') }}</p>
              </div>
            </template>
          </vxe-table>
          <CPagination
            ref="pagerLeft"
            style="margin: 0;padding-bottom: 5px;"
            :total="queryTotal"
            :page-num="viewIndex"
            :page-size="viewSize"
            :layout="pageLayout"
            :pager-count="5"
            @current-change="changeCurrentIndex"
            @size-change="changeCurrentSize"
          />
        </div>
        <!-- 中间操作按钮 -->
        <div v-if="multiSelect" class="select-body-center">
          <div>
            <el-button @click="selectedAddAll">
              {{ $t("components.common.addAllPage") }}
            </el-button>
          </div>
          <div>
            <el-button
              icon="el-icon-arrow-right"
              type="primary"
              :disabled="multipleSelection.length==0"
              @click="selectedAdd"
            >
              {{ $t("components.common.new") }}
            </el-button>
          </div>
          <div>
            <el-button
              icon="el-icon-arrow-left"
              :disabled="rightMultipleSelected.length==0"
              @click="selectedDel"
            >
              {{ $t("components.common.delete") }}
            </el-button>
          </div>
          <div>
            <el-button
              :disabled="selectedData.length==0"
              @click="selectedDelAll"
            >
              {{ $t("components.common.clearAll") }}
            </el-button>
          </div>
        </div>
        <!-- 右边选中内容 -->
        <div v-if="multiSelect" class="select-body-right">
          <div class="search-wrap search-wrap-right">
            <el-row :gutter="16" type="flex">
              <el-col>
                <el-input
                  v-model="selectedSearchKey"
                  :disabled="selectedDataBak.length==0"
                  :placeholder="searchKeyPlaceholder"
                  clearable
                  @keyup.enter.native="searchSelected(selectedSearchKey)"
                  @clear="resetSearchSelected"
                >
                  <el-button
                    slot="append"
                    icon="el-icon-search"
                    @click="searchSelected(selectedSearchKey)"
                  />
                </el-input>
              </el-col>
              <el-col style="text-align:right;">
                <el-button
                  v-if="needImport"
                  type="text"
                  icon="el-icon-upload"
                  style="padding-right: 0;"
                >
                  {{ importBtnText || $t('components.common.importData') }}
                </el-button>
              </el-col>
            </el-row>
          </div>
          <vxe-table
            ref="quickSelectedTable"
            height="300"
            border
            show-overflow="tooltip"
            auto-resize
            sync-resize
            :stripe="true"
            :data="selectedData.slice((currentPage - 1) * currentPageSize, currentPage * currentPageSize)"
            :column-config="{isCurrent: false, isHover: false,resizable: true}"
            :row-config="{isCurrent: true, isHover: true,useKey:true, keyField:rowKey}"
            :checkbox-config="{trigger: 'row',reserve: true}"
            @current-change="({newValue,oldValue}) => selectedHandleCurrentChange(newValue,oldValue)"
            @checkbox-change="(selected) => selectedCheckChange(selected.records)"
            @checkbox-all="(selected) => selectedCheckChange(selected.records)"
          >
            <vxe-column
              type="checkbox"
              width="50"
              align="center"
            />
            <vxe-column
              type="seq"
              width="54"
              :title="$t('components.common.sort')"
            />
            <template v-for="(col, key) in tableHeader">
              <vxe-column
                v-if="col.displayItemEnabled==='Y'"
                :key="key"
                :field="col.componentType === 'DICTIONARY' ? col.attrHump + 'View' : col.attrHump"
                :title="$t(`${name.replace(/\s+/g, '')}.${col.attrHump}`)"
                :min-width="col.width || 100"
                align="center"
                show-overflow="tooltip"
              />
            </template>
            <template #empty>
              <div style="color: #96999c;">
                <p>{{ $t('components.common.noMoreData') }}</p>
              </div>
            </template>
          </vxe-table>
          <CPagination
            ref="pagerRight"
            style="margin: 0;padding-bottom: 5px;"
            :total="queryTotalRight"
            :page-num="viewIndexRight"
            :page-size="viewSizeRight"
            :layout="pageLayout"
            :pager-count="5"
            @current-change="changeCurrentIndexRight"
            @size-change="changeCurrentSizeRight"
          />
        </div>
      </div>
      <template #footer>
        <el-button @click="cancelDialog">
          {{ $t("components.common.cancel") }}
        </el-button>
        <el-button type="primary" @click="confirmDialog">
          {{ $t("components.common.confirm") }}
        </el-button>
      </template>
    </srm-dialog>
  </div>
</template>

<script>
import CPagination from 'lib@/components/c-pagination'
import DictSelect from '@/library/components/c-select/dict-select'
import { isNull, isObject, isArray, trim } from '@/utils'
import { STORE_COMMON_CACHE } from '@/config/store-config'

export default {
  name: 'QuickSearch',
  components: { DictSelect, CPagination },
  inject: {
    elForm: {
      default: ''
    },

    elFormItem: {
      default: ''
    }
  },
  props: {
    name: {
      // 父页面传值--->配置项
      type: String,
      default: function () {
        return ''
      }
    },
    companySort: {
      // 父页面传值--->配置项
      type: String,
      default: function () {
        return ''
      }
    },
    // 父页面传值input
    showInput: {
      type: [String, Object, Number],
      default: function () {
        return ''
      }
    },
    // 组件绑定值
    propKey: {
      type: String,
      default: function () {
        return ''
      }
    },
    // 想要展示的字段属性key
    showKey: {
      type: String,
      default: function () {
        return ''
      }
    },
    // 组件禁用
    disabled: {
      type: Boolean,
      default: function () {
        return false
      }
    },
    // 快查取消搜索弹框
    purchaseDsiable: {
      type: Boolean,
      default: function () {
        return true
      }
    },
    // 是否禁用下拉选择，只能弹框的方式
    disabledSelect: {
      type: Boolean,
      default: function () {
        return false
      }
    },
    // 允许用户手动录入数据
    allowInput: {
      type: Boolean,
      default: function () {
        return false
      }
    },
    // 下拉查询是否全匹配-------默认false
    isSetValue: {
      type: Boolean,
      default: function () {
        return false
      }
    },
    //
    // 展示的字段属性key, 如 {name, code} 中的 name 属性
    displayKey: {
      type: [String, Array],
      default: function () {
        return null
      }
    },
    // 存储值的字段属性key, 如 {name, code} 中的 code 属性
    valueKey: {
      type: [String, Array],
      default: function () {
        return null
      }
    },
    // 取值映射
    mapValue: {
      type: Array,
      default: function () {
        return null
      }
    },
    // 当前行上绑定的 data 对象
    scopeData: {
      type: Object,
      default: function () {
        return null
      }
    },
    // 是否支持多选
    multiSelect: {
      type: Boolean,
      default: function () {
        return false
      }
    },
    // 前置查询条件 key格式是快查定义好的变量和对应数据库的字段格式  {'t.FROM_CURRENCY_CODE': form.currencyCode}
    preQueryData: {
      type: Object,
      default: function () {
        return null
      }
    },
    // 扩展查询条件 入参为json格式
    extendQueryData: {
      type: Object,
      default: function () {
        return null
      }
    },
    formData: {
      type: Object,
      default: null
    },
    enableFormItem: {
      type: Array,
      default: function () {
        return []
      }
    },
    // 打开弹层是否自动查询
    autoQuery: {
      type: Boolean,
      default: true
    },
    // 提示
    placeholder: {
      type: String,
      default: ''
    },
    // Select 下拉框的类名
    popperClass: {
      type: String,
      default: ''
    },
    // 指定显示下拉列表显示字段
    selectFields: {
      type: Array,
      default: function () {
        return []
      }
    },
    // 是否为 input 远程搜索
    autocomplete: {
      type: Boolean,
      default: false
    },
    // 只展示按钮
    showButton: {
      type: Boolean,
      default: false
    },
    // 按钮文字
    btnTitle: {
      type: String,
      default: ''
    },
    // 按钮类型
    btnType: {
      type: String,
      default: 'primary'
    },
    // 序号，用于表格数组渲染定位
    tableIndex: [Number, String, Object],
    // 导入相关属性
    // 多选场景下是否需要导入
    needImport: {
      type: Boolean,
      default: false
    },
    // 导入url
    importUrl: {
      type: String,
      default: ''
    },
    // 导入按钮文字
    importBtnText: {
      type: String,
      default: ''
    },
    // 下拉输入框是否可以清除
    selectClearable: {
      type: Boolean,
      default: true
    },
    // 点击确定是否自动关闭弹窗，默认是，设为否需要手动调关闭方法
    confirmAutoClose: {
      type: Boolean,
      default: true
    },
    // 只读态的判断值
    readPretty: {
      type: Boolean,
      default: false
    }
  },

  data () {
    return {
      fuzzyPopVisible: false,
      dialogTableVisible: false,
      showTip: false, // 是否显示tiptool
      timeout: null,
      loading: false,
      title: '', // 弹框表格
      rowKey: null, // 表格用于跨页勾选，必须每一行的唯一标识
      idColumn: '', // 文本框取值字段,来自数据库配置项

      tableHeader: [], // 表头数据
      filterQueryArr: [], // 查询条件过滤
      filterQueryKeys: [], // 查询字段的key值
      form: {}, // 查询条件双向绑定
      queryForm: {}, // 查询条件对象

      // select下拉data区域
      inputModel: '', // 组件显示输入框绑定变量
      showfields: [], // 下拉显示字段 如果么有入参设置则取表头的前两列
      selectClass: '', // 下拉样式
      selectVisible: false,
      showSelect: false,
      selectGridData: [], // 模糊查询结果集(下拉数据)

      // 分页
      queryParam: {
        pageNum: 1,
        pageSize: 30
      },
      // 左侧表格分页
      queryTotal: 0, // 共几条
      viewIndex: 1,
      viewSize: 30,
      // 右侧侧表格分页
      queryTotalRight: 0, // 共几条
      viewIndexRight: 1,
      viewSizeRight: 30,
      currentPage: 1,
      currentPageSize: 30,
      // 左侧选中栏相关变量
      gridData: [],
      multipleSelection: [], // 多选表格的行数据
      currentRow: {},
      // 右侧选中栏相关变量
      filterQueryKeysRight: [], // 查询字段的key值
      selectedSearchKey: '', // 右侧关键字查询
      searchKeyPlaceholder: this.$t('components.common.enterKeyword'), //
      selectedData: [], // 多选选中的条目
      selectedDataBak: [], // 用于右侧表格前端查询
      rightMultipleSelected: [], // 右边表格选中条目
      rightCurrentRow: {},
      valType: '' // 设值方式： 弹框 dialog | 下拉 fuzzy
    }
  },
  computed: {
    // 弹框尺寸 多选的时候弹框大一号
    dialogSize () {
      if (this.multiSelect) {
        return 'xLarge'
      } else {
        return 'large'
      }
    },
    // 分页布局
    pageLayout () {
      if (this.multiSelect) {
        return 'total, prev, pager, next,sizes'
      } else {
        return 'total, prev, pager, next,sizes, jumper'
      }
    },
    device () {
      return this.$store.getters.device
    }
  },
  watch: {
    inputModel: {
      handler: function (val) {
        if (val) {
          let textWidth = this.getTextWidth(val)
          let domWidth = this.$refs.quickSearchCol ? this.$refs.quickSearchCol.$el.offsetWidth - 38 : 100
          if (textWidth > domWidth) {
            if (this.disabled) {
              this.showTip = true
            } else {
              this.showTip = false
            }
          } else {
            this.showTip = false
          }
        }
      }
    },
    name: {
      handler: function (n, o) {
        // 同个组件实例，不同name的情况（页面通过v-if控制 ）
        if (n != o) {
          this.resetData()
        }
      }
    },
    showInput: {
      immediate: true,
      handler: function (val) {
        this.inputModel = isObject(val)
          ? val[this.propKey || this.showKey || this.idColumn]
          : val
      }
    },
    $route () {
      if (this.fuzzyPopVisible) {
        this.fuzzyPopVisible = false
      }
    },
    // 点击pop外面的区域需要关闭pop弹框
    fuzzyPopVisible () {
      if (this.fuzzyPopVisible) {
        document.body.addEventListener('click', this.closePopOutside)
      } else {
        document.body.removeEventListener('click', this.closePopOutside)
      }
    }
  },
  created () {},

  methods: {
    // 搜索多选确认
    comfirm () {
      // 获取选中数据
      const selectData = this.$refs.mulSelectXTable.getCheckboxRecords()
      if (selectData.length === 0) {
        this.$message.warning(this.$t('contractMod.msgSelData'))
        return false
      }
      this.$emit('close-quicksearch', selectData, this.scopeData)
      this.fuzzyPopVisible = false
    },
    // 手动设置显示值
    setInputModel (value) {
      this.inputModel = value
    },
    getTextWidth (text) {
      let canvas = document.createElement('canvas')
      let context = canvas.getContext('2d')
      context.font = '12px Arial'
      let metrics = context.measureText(text)
      return metrics.width
    },
    // 外部调用组件的清除文字方法
    clearInput () {
      this.inputModel = null
    },
    // 勾选做右侧表格数据同步操作 // 全选的row 为null
    modifyDataHandel (checked, row) {
      if (row) {
        let rowId = row[this.rowKey]
        let hasIndex = this.selectedData.findIndex(i => (i[this.rowKey] == rowId))
        if (checked) { // 勾选
          if (hasIndex < 0) {
            this.selectedData.push({ ...row })
            this.selectedDataBak.push({ ...row })
          }
        } else { // 取消勾选
          if (hasIndex > -1) {
            this.selectedData.splice(hasIndex, 1)
            this.selectedDataBak.splice(hasIndex, 1)
          }
        }
        this.selectedDataTotal()
      } else {
        if (checked) { // 添加当前选择页
          this.selectedAdd()
        } else { // 清除当前选择页
          this.selectedDelAll()
        }
      }
    },
    checkChange (selected, checked, row) {
      this.multipleSelection = selected
      this.modifyDataHandel(checked, row)
    },
    selectedCheckChange (selected) {
      this.rightMultipleSelected = selected
    },
    selectedHandleCurrentChange (row) {
      this.rightCurrentRow = row
    },
    dispatchChange () {
      if (this.elFormItem) {
        this.elFormItem.$emit('el.form.change')
      }
    },
    // 下拉列宽度
    getColWidth (width) {
      return width ? { width: width + 'px' } : { width: '150px' }
    },
    // 模糊搜索清除
    fuzzyInputClear () {
      this.fuzzyPopVisible = false
      this.getSelectData(null, 'fuzzy')
    },
    querySearchAsync (queryVal) {
      if (!queryVal) {
        this.setValue({}, 'fuzzy')
        this.$emit('close-quicksearch', null, this.scopeData, this.tableIndex)
        return
      }
      clearTimeout(this.timeout)
      this.timeout = setTimeout(() => {
        this.remoteMethod(queryVal)
      }, 1000 * Math.random())
    },
    // select 下拉搜索
    async remoteMethod (queryStr) {
      if (this.allowInput) {
        // 允许录入的情况下直接赋值-不要再做请求数据
        this.selectClass = 'qs-select-none'
        this.getSelectData(queryStr, 'fuzzy')
        return
      }
      this.loading = true // 加载状态
      this.selectClass = this.popperClass
      if (queryStr) {
        this.showSelect = true
        // this.$emit('update:showInput', query)
        await this.getConfigration()
        await this.query('', queryStr)

        // 自动选择
        if (this.selectGridData.length == 1) {
          this.getSelectData(this.selectGridData[0], 'fuzzy')
          // this.$refs.quickSelectSelect.blur()
          this.fuzzyPopVisible = false
        }
      } else {
        this.selectGridData = []
        this.fuzzyPopVisible = false
      }
      this.loading = false // 加载状态
    },
    getSelectData (row = {}, setValType) {
      // 获取选中的值并且赋值
      this.setValue(row, setValType)
      this.$forceUpdate()
      // 设置暴露给组件外部的数据
      const exportData = (row && this.multiSelect) ? [row] : row
      this.$emit('close-quicksearch', exportData, this.scopeData, this.tableIndex)
    },
    // 下拉选择数字
    getLineData (row = {}, column, cell, type) { // fuzzy
      // 双击选中行数据
      if (!this.multiSelect) {
        this.setValue(row, type)
        // 弹框双击选择
        this.$emit('close-quicksearch', row, this.scopeData, this.tableIndex)
        if (type == 'fuzzy') {
          // 下拉模糊
          this.fuzzyPopVisible = false
        } else {
          // 自动关闭，如果非自动关闭清自行在合适的时机父组件调cancelDialog方法
          if (this.confirmAutoClose) {
            this.dialogTableVisible = false
          }
          this.selectVisible = false
        }
      }
    },
    // 清空下拉选的值--------
    clearOptions () {
      this.selectGridData = []
    },
    // 点开弹窗查询
    async openDialog (type) {
      // 清空上次选择记录
      this.currentRow = {}
      this.fuzzyPopVisible = false // 确保模糊的弹出框隐藏
      // 前置事件 添加回调判断，如果存在回调就直接中断执行方法, 回调方法不能写异步
      let flag = true
      this.$emit('before-open', true, () => {
        flag = false
      })
      if (!flag) {
        return false
      }

      this.resetData()
      this.selectedData = [] // 右边表格已选中数据
      this.showSelect = false
      this.selectVisible = false
      await this.getConfigration() // 查询配置信息

      this.formData && Object.assign(this.form, this.formData) // 设定的form表单信息

      if (this.autoQuery || type == 'autoQuery') {
        // 自动查询
        await this.query()
        // 非点击事件，如果只有一条数据，则自动设值
        if (type == 'autoQuery' && this.gridData.length == 1) {
          this.getLineData(this.gridData[0], 'dialog')
          return
        }
      }
      this.dialogTableVisible = true
      type != 'click' && this.$emit('query-callback', this.gridData) // 下拉查询后触发
    },
    // 确认
    confirmDialog () {
      // 多选
      if (this.multiSelect) {
        const selectedData = this.selectedData || []
        const selectedDataBak = this.selectedDataBak || []
        let resultSelected = null
        // 存在右侧搜索情况
        if (selectedDataBak.length > selectedData.length) {
          resultSelected = selectedDataBak
        } else {
          resultSelected = selectedData
        }
        if (resultSelected.length == 0) {
          this.$message({
            type: 'error',
            message: this.$t('components.common.placeSelectData')
          })
          return
        }
        this.$emit('close-quicksearch', resultSelected, this.scopeData, this.tableIndex)
      } else {
        // 单选
        if (!Object.keys(this.currentRow).length) {
          this.$message({
            type: 'error',
            message: this.$t('components.common.pleaseSelectOne') // 请选择一条数据！
          })
          return
        }
        this.setValue(this.currentRow, 'dialog')
        this.$emit('close-quicksearch', this.currentRow, this.scopeData, this.tableIndex)
      }

      // 自动关闭，如果非自动关闭清自行在合适的时机父组件调cancelDialog方法
      if (this.confirmAutoClose) {
        this.dialogTableVisible = false
      }
    },
    // 取消
    cancelDialog () {
      this.dialogTableVisible = false
    },
    handleCurrentChange (val) {
      this.currentRow = val
    },
    setCache (key, data) {
      sessionStorage.setItem('QS_' + key, JSON.stringify(data))
    },
    getCache (key) {
      var data = sessionStorage.getItem('QS_' + key)
      return data ? JSON.parse(data) : null
    },
    // 接口查询配置信息
    async getConfigRemote () {
      return this.$http({
        url: `/api-base/quicksearch/quicksearchConfig/quicksearch/component/${this.name}`,
        method: 'GET',
        loading: true
      })
    },
    // 获取配置信息
    async getConfigration () {
      // title // 属性名
      // alias // 别名
      // attr // 属性值
      // attrHump 属性值 驼峰形式
      // attrOrder // 排序
      // columnWidth // 列宽
      // componentProperty // 组件属性
      // componentType // 组件类型
      // displayItemEnabled // 显示项
      // queryItemEnabled // 查询项
      // queryMatchOperator // 匹配类型
      // 缓存数据
      if (this.getCache(this.name)) {
        let cacheData = this.getCache(this.name) // 本地缓存配置信息
        let tableConf = cacheData.tableConf
        this.rowKey = cacheData.idColumn // 数据库行id
        this.idColumn = cacheData.idColumn || this.showKey
        this.tableHeader = tableConf// 表格配置
        this.filterQueryKeys = cacheData.filterQueryKeys
        this.filterQueryKeysRight = cacheData.filterQueryKeysRight
        this.filterQueryArr = tableConf.filter(i => (i.queryItemEnabled === 'Y'))
        this.setFields(tableConf) // 设置下拉显示字段
        this.placeholderKey() // 多选右侧输入框动态占位符
        return Promise.resolve()
      } else { // 接口查询配置信息
        const { data } = await this.getConfigRemote()
        let remoteFormated = await this.configConvertHandel(data)
        let tableConf = remoteFormated.tableConf
        this.rowKey = remoteFormated.idColumn // 数据库行id
        this.idColumn = remoteFormated.idColumn || this.showKey
        this.tableHeader = tableConf // 表格配置
        this.filterQueryKeys = remoteFormated.filterQueryKeys
        this.filterQueryKeysRight = remoteFormated.filterQueryKeysRight
        this.filterQueryArr = tableConf.filter(i => (i.queryItemEnabled === 'Y'))
        this.setFields(tableConf) // 设置下拉显示字段
        this.placeholderKey() // 多选右侧输入框动态占位符
        // 保存缓存
        this.setCache(this.name, remoteFormated)
      }
    },
    // 配置数据转化
    configConvertHandel (configData) {
      let idColumn = configData.valueAttrHump
      let attrConfigs = configData.attrConfigs || []
      let tableConf = [] // 表格配置
      let filterQueryKeys = {} // 属性驼峰格式
      let filterQueryKeysRight = {}
      attrConfigs.forEach(i => {
        const { title, alias, attr, attrHump, attrOrder, columnWidth,
          componentProperty, componentType, displayItemEnabled, queryItemEnabled, queryMatchOperator
        } = i
        let componentPropertyVal = componentProperty ? JSON.parse(componentProperty) : '' // 组件属性
        let obj = {
          title,
          alias,
          attr,
          attrHump,
          attrOrder,
          columnWidth,
          componentType,
          displayItemEnabled,
          queryItemEnabled,
          queryMatchOperator,
          componentProperty: componentPropertyVal,
          attrHumpView: [attrHump + 'View']
        }
        tableConf.push(obj) // 表格配置
        let keyObj = { [ attrHump ]: alias + '.' + attr }
        if (displayItemEnabled === 'Y' && componentType !== 'DATE' && componentType !== 'DICTIONARY') {
          Object.assign(filterQueryKeysRight, keyObj) // 右侧搜索匹配
        }
        Object.assign(filterQueryKeys, keyObj) // 左侧搜索匹配
      })
      return {
        idColumn: idColumn, // 行ID
        tableConf: tableConf, // 表格配置信息
        filterQueryKeys: filterQueryKeys, // 属性对应驼峰字段key
        filterQueryKeysRight: filterQueryKeysRight
      }
    },
    // 设置下拉显示字段
    setFields (filedArr = []) {
      let resShowFileds = filedArr.filter(i => (i.displayItemEnabled == 'Y' && i.componentType != 'DATE' && i.componentType != 'DICTIONARY'))
      this.showfields = []
      if (this.selectFields.length > 0) {
        for (let selectItem of this.selectFields) {
          let item = resShowFileds.find(v => v.attrHump == selectItem.name)
          if (item) {
            this.showfields.push({
              name: selectItem.name,
              label: item.title,
              width: item.columnWidth,
              componentType: item.componentType
            })
          }
        }
      } else {
        this.showfields = (resShowFileds.slice(0, 2)).map(i => ({
          name: i.attrHump,
          label: i.title,
          width: i.columnWidth,
          componentType: i.componentType
        }))
      }
      console.log('showfields')
      console.log(this.showfields)
    },
    // 查询字典信息
    async getDictData (tableConfig = []) {
      // 查询字典
      let dictCols = tableConfig.filter(i => (i.componentType == 'DICTIONARY')) // 取字典项
      let dictCodeArray = dictCols.map(item => (item.componentProperty).code)
      let dictColsKeys = {}
      dictCols.forEach(k => {
        let keyObj = { [ k.attrHump ]: (k.componentProperty).code }
        Object.assign(dictColsKeys, keyObj)
      })
      if (dictCodeArray.length > 0) {
        let dictBatchMap = await this.$store.dispatch(
          STORE_COMMON_CACHE.LIST_DICT_BATCH,
          { dictCodeList: dictCodeArray }
        )
        return {
          dictColsKeys: dictColsKeys,
          dictBatchMap: dictBatchMap
        }
      } else {
        return null
      }
    },
    // 搜索表单参数
    setQueryParams (otherParams) {
      for (let i in this.form) {
        if (this.form[i]) {
          this.queryForm[this.filterQueryKeys[i]] = (this.form[i]).toString()
        }
      }
      otherParams && Object.assign(this.queryForm, otherParams)
    },
    query (opr, queryStr) {
      // 设置调整表格高度
      if (this.dialogTableVisible) {
        const xTable = this.$refs.quickSearchTable
        if (xTable) {
          xTable.refreshColumn().then(() => xTable.refreshScroll())
          xTable.updateData()
          xTable.recalculate(true)
        }
      }
      let selectURL = `/api-base/quicksearch/quicksearchConfig/listDialog/${this.name}`
      this.queryForm = {}
      if (opr == 'enter') { // 弹框查询
        // 来自于enter事件，先清空form绑定的值，再重新赋值
        this.setQueryParams()
        // this.queryTotal = -1
        this.queryParam.pageNum = 1 // 重置为1
        this.viewIndex = 1
      } else {
        if (
          this.showSelect &&
          this.filterQueryArr &&
          this.filterQueryArr[0]
        ) {
          // 下拉查询条件过滤条件----//展开下拉选择 quicksearch/quicksearchConfig/listInputName
          selectURL = `/api-base/quicksearch/quicksearchConfig/pageInputName/${this.name}`
          // this.queryForm[this.filterQueryArr[0].alias + '.' + this.filterQueryArr[0].attr] = queryStr
          // this.queryParam.pageNum = 1 // 重置为1
        } else {
          this.setQueryParams()
        }
      }
      var filterData = {}
      if (this.preQueryData) {
        // 添加前置查询条件
        this.queryForm = Object.assign({}, this.preQueryData, this.queryForm)
      }

      let formData = JSON.stringify({
        query: JSON.stringify(Object.assign({}, this.queryForm, filterData)),
        extendQuery: JSON.stringify(this.setExtendQueryData())
      })

      // 是否是下拉查询
      if (this.showSelect) {
        formData = JSON.stringify({
          key: this.name,
          value: queryStr,
          isSetValue: JSON.stringify(this.isSetValue),
          extendQuery: JSON.stringify(this.setExtendQueryData()),
          query: JSON.stringify(Object.assign({}, this.queryForm, filterData))
        })
      }
      let paramData = Object.assign({}, { params: formData }, this.queryParam)

      return this.$http({
        // 获取数据---展示在table页面上的数据
        url: selectURL,
        method: 'POST',
        loading: !this.showSelect,
        data: paramData
      }).then(async res => {
        let resData = []
        if (this.showSelect) {
          resData = res.data.list.slice(0) // 下拉返回
        } else {
          resData = res.data.data.slice(0) // 弹框返回
        }
        // 查询字典信息 [[
        let dictResInfo = await this.getDictData(this.tableHeader) // 字典查询
        let searchResult = resData
        if (dictResInfo) { // 有字典
          let dictColsKeys = dictResInfo.dictColsKeys
          let dictBatchMap = dictResInfo.dictBatchMap
          for (let key in dictColsKeys) {
            for (let j = 0; j < searchResult.length; j++) {
              if (!dictBatchMap[dictColsKeys[key]]) {
                continue
              }
              var label = dictBatchMap[dictColsKeys[key]][searchResult[j][key]]
              this.$set(searchResult[j], key + 'View', label) // 设置字典label
            }
          }
        }
        // 查询字典信息 ]]
        // 模糊下拉查询
        if (this.showSelect) {
          this.selectGridData = searchResult
          this.fuzzyPopVisible = true
          this.queryTotal = res.data.total
          if (searchResult.length === 0 && !this.autocomplete) {
            // this.inputModel = ''
            this.setValue({}, 'fuzzy')
            this.$emit('close-quicksearch', null, this.scopeData, this.tableIndex)
          }
        } else {
          // 弹框查询表格数据
          this.gridData = searchResult
          this.queryTotal = res.data.totalCount // 总条数
          this.title = res.data.title // 弹框title
        }
      }).catch(err => {
        console.log(err)
      })
    },

    // 重置
    resetData (type) {
      this.form = {}
      this.queryParam.pageNum = 1
      if (type != 'form') {
        this.gridData = [] // 弹框表格数据
        this.selectGridData = [] // 模糊下拉数据
        this.multipleSelection = [] // 多选选择
        this.selectedData = [] // 多选选中的条目
        this.selectedDataBak = [] // 用于右侧表格前端查询
        this.selectedSearchKey = ''
      }
      this.resetPage() // 重置分页
    },
    // 重置左侧表格分页信息
    resetPage () {
      this.viewIndex = 1
      this.viewSize = 30
      this.queryParam.pageNum = 1
      this.queryParam.pageSize = 30
    },
    resetHandle () {
      this.resetData() // 重置相关数据
      this.query('enter') // 重置后需要恢复初始化数据
    },
    // 空值默认显示值
    setNullValue (value, defaultValue = '') {
      return (isNull(value) ? defaultValue : value).toString().trim()
    },
    // 获取行上对应属性的值
    getPropValue (row, key) {
      return isObject(row) ? row[key] || null : row
    },
    // 设置属性值
    setValue (row = {}, setValType = '') {
      row = row || {}
      try {
        if (this.scopeData) {
          [this.displayKey, this.valueKey].forEach((item, index) => {
            if (isArray(item)) {
              let [key, mapKey] = item
              const value = this.setNullValue(this.getPropValue(row, key))
              if (key && mapKey) {
                this.$set(this.scopeData, mapKey, value)
              } else if (key) {
                this.$set(this.scopeData, key, value)
              }
            } else if (item) {
              const value = this.setNullValue(this.getPropValue(row, item))
              this.$set(this.scopeData, item, value)
            }
          })

          // map 值
          if (isArray(this.mapValue)) {
            for (let item of this.mapValue) {
              let [key, mapKey] = trim(item).split(',')
              if (key && mapKey) {
                const value = this.setNullValue(this.getPropValue(row, key))
                this.$set(this.scopeData, mapKey, value)
              } else if (key) {
                const value = this.setNullValue(this.getPropValue(row, key))
                this.$set(this.scopeData, key, value)
              }
            }
          }
        }
      } catch (e) {
        console.log(e)
      }
      this.valType = setValType
      let getValueKey = this.showKey || this.idColumn
      // 设置选择后的显示值
      this.inputModel = isObject(row) ? (row[getValueKey] || this.showInput || '').toString().trim() : row
    },
    // 设置 ExtendQuery
    setExtendQueryData () {
      let params = {
        _quickKey: this.name,
        entityId: 10
      }
      // 如果传入companySort--就加上作为查询条件
      if (this.companySort) {
        params.companySort = this.companySort
      }
      let data = {}
      for (let i in this.extendQueryData) {
        data[i] = this.setNullValue(this.extendQueryData[i], '').toString()
      }
      return Object.assign({}, data, params)
    },
    // 禁用判断
    isDisable (colName) {
      return (
        this.formData &&
        this.formData.hasOwnProperty(colName) &&
        !this.enableFormItem.includes(colName)
      )
    },

    // 根据不同配置多选场景下面右侧表格占位符动态改变
    placeholderKey () {
      let holder = ''
      let time = 0
      let filterKeys = Object.keys(this.filterQueryKeys)
      filterKeys.forEach(i => {
        let col = this.tableHeader.find(elm => (elm.attrHump == i))
        if (col) {
          if (col.componentType !== 'DATE' && col.componentType !== 'DICTIONARY' && col.displayItemEnabled === 'Y') {
            if (time > 0) {
              holder += ',' + this.$t(`${this.name.replace(/\s+/g, '')}.${col.attrHump}`)
            } else {
              holder += this.$t(`${this.name.replace(/\s+/g, '')}.${col.attrHump}`)
            }
            time++
          }
        }
      })
      this.searchKeyPlaceholder = this.$t('components.common.enter') + holder + this.$t('components.common.searchWord')
    },
    // 改变 currentNum
    changeCurrentIndex (currentNum) {
      this.queryParam.pageNum = currentNum
      this.query('')
    },
    // 改变 currentSize
    changeCurrentSize (currentSize) {
      this.queryParam.pageSize = currentSize
      this.query('')
    },
    // 右侧分页
    changeCurrentIndexRight (currentNum) {
      this.currentPage = currentNum
    },
    changeCurrentSizeRight (currentSize) {
      this.currentPageSize = currentSize
    },
    // 右侧总数
    selectedDataTotal () {
      this.queryTotalRight = this.selectedData.length
    },
    // 添加按钮事件处理
    addItemHandel () {
      let leftSelected = this.multipleSelection || []
      leftSelected.forEach(item => {
        let rowId = item[this.rowKey]
        let hasIndex = this.selectedData.findIndex(i => (i[this.rowKey] == rowId))
        if (hasIndex < 0) {
          this.selectedData.push({ ...item })
        }
      })
      this.selectedDataBak = [...this.selectedData]
    },
    // 添加
    selectedAdd () {
      this.addItemHandel()
      this.selectedDataTotal()
    },
    // 添加所有
    async selectedAddAll () {
      let pageSizes = this.$refs.pagerLeft.pageSizes
      if (pageSizes.length > 0) {
        let maxTotalIndex = pageSizes.findIndex(i => i > this.queryTotal) // 大于总数的分页
        if (maxTotalIndex > -1) {
          this.queryParam.pageSize = pageSizes[maxTotalIndex]
          this.viewSize = pageSizes[maxTotalIndex]
          this.selectedAddAllHandel() // 查询数据
        } else {
          let maxSizeIndex = pageSizes.length - 1 // 取最大分页
          if (this.queryTotal > 1500) { // 大于 5000条做浏览器崩溃预警
            this.$confirm(this.$t('components.common.searchTip'), this.$t('common.tips'), {
              confirmButtonText: this.$t('common.pageSearch'),
              cancelButtonText: this.$t('common.continueSearch'),
              type: 'warning',
              showClose: false,
              closeOnClickModal: false,
              closeOnPressEscape: false
            }).then(() => {
              this.queryParam.pageSize = pageSizes[maxSizeIndex]
              this.viewSize = pageSizes[maxSizeIndex]
              this.selectedAddAllHandel() // 查询数据
            }).catch(() => {
              this.queryParam.pageSize = this.queryTotal
              this.selectedAddAllHandel() // 查询数据
            })
          } else {
            this.queryParam.pageSize = pageSizes[maxSizeIndex]
            this.viewSize = pageSizes[maxSizeIndex]
            this.selectedAddAllHandel() // 查询数据
          }
        }
      } else {
        this.queryParam.pageSize = this.queryTotal
        let maxSizeIndex = pageSizes.length - 1 // 取最大分页
        this.queryParam.pageSize = pageSizes[maxSizeIndex]
        this.viewSize = pageSizes[maxSizeIndex]
        this.selectedAddAllHandel() // 查询数据
      }
    },
    async selectedAddAllHandel () {
      await this.query('')
      await this.$refs.quickSearchTable.setAllCheckboxRow(true)
      this.multipleSelection = this.$refs.quickSearchTable.getCheckboxRecords()
      this.selectedData = [...this.multipleSelection]
      this.selectedDataBak = [...this.multipleSelection]
      this.selectedDataTotal()
    },
    async selectedDel () {
      // 去掉左边表格的选中效果
      // await this.$refs.quickSearchTable.setCheckboxRow(this.rightMultipleSelected, false)
      this.rightMultipleSelected.forEach(elm => {
        let rowId = elm[this.rowKey]
        const row = this.gridData.find(i => (i[this.rowKey] == rowId))
        if (row) {
          this.$refs.quickSearchTable.setCheckboxRow(row, false)
        }
      })
      // 清除右边表格选中状态
      await this.$refs.quickSelectedTable.clearCheckboxRow()
      // 获取左边选中条目
      this.multipleSelection = this.$refs.quickSearchTable.getCheckboxRecords()
      // 赋值右边表格
      this.selectedData = [...this.multipleSelection]
      this.selectedDataBak = [...this.multipleSelection]
      this.selectedDataTotal()
      this.rightMultipleSelected = []
    },
    // 清除所有
    async selectedDelAll () {
      await this.$refs.quickSearchTable.clearCheckboxRow() // 清除左边所有选中数据
      // 清除右边表格选中状态
      await this.$refs.quickSelectedTable.clearCheckboxRow()
      // 获取左边选中条目
      this.multipleSelection = this.$refs.quickSearchTable.getCheckboxRecords()
      // 赋值右边表格
      this.selectedData = [...this.multipleSelection]
      this.selectedDataBak = [...this.multipleSelection]
      this.selectedDataTotal()
      this.rightMultipleSelected = []
    },
    resetSearchSelected () {
      this.selectedSearchKey = ''
      this.searchSelected('')
    },
    searchSelected (keyword) {
      let myData = []
      if (keyword) {
        myData = this.filterSelected(this.selectedDataBak, keyword)
      } else {
        myData = this.selectedDataBak
      }
      this.selectedData = myData
      this.selectedDataTotal()
    },
    // 动态根据查询条件去查询表格字段
    filterSelected (searchData, keyword) {
      let filterKey = Object.keys(this.filterQueryKeysRight)
      let newarr = []
      searchData.forEach(element => {
        for (let k = 0; k < filterKey.length; k++) {
          if (element[filterKey[k]].indexOf(keyword) > -1) {
            newarr.push({ ...element })
            break
          }
        }
      })
      return newarr
    },
    // 模糊查询分页
    fuzzyChangeCurrentIndex (currentNum) {
      this.queryParam.pageNum = currentNum
      this.query('', this.inputModel)
    },
    // 模糊查询分页
    fuzzyChangeCurrentSize (currentSize) {
      this.queryParam.pageSize = currentSize
      this.query('', this.inputModel)
    },
    // 点击pop外面的区域需要关闭pop弹框
    closePopOutside () {
      this.inputModel = '' // 查询以后没选择就关掉的，把模糊查询的关键字也清掉
      this.fuzzyPopVisible = false
    },
    // 返回数据处理
    formatterText ({ cellValue, row, column }) {
      return cellValue && cellValue.toString().indexOf('row_') > -1 ? '-' : cellValue
    }
  }
}
</script>

<style lang="scss">
.the_quick_search {
  white-space: normal;
  position: relative;
  .el-input-group__append {
    padding-right: 8px;
  }
  .the_quick_select {
    display: block;
    padding: 0;
    .el-input__suffix {
      right: 24px;
      .el-input__icon {
        width: 16px;
      }
      .el-input__suffix-inner{
        .el-select__caret.el-input__icon.el-icon-search{
          display: none;
        }
      }
    }
  }
  .el-form-item {
    margin-bottom: 0;
  }
  .quick-search-btn {
    position: absolute;
    top: 1px;
    bottom: 1px;
    right: 1px;
    border: none;
    min-width: 20px;
    border-radius: 0 4px 4px 0;
    padding: 4px !important;
    color: #96999c;
  }

}
.the_quick_search_dialog {
  .el-table th > .cell {
    padding-left: 4px;
    padding-right: 4px;
  }
  .el-table th div {
    line-height: 16px;
    .el-input--small {
      padding: 0;
    }
    .el-input__inner {
      border-radius: 2px !important;
      height: 26px !important;
      line-height: 26px !important;
      min-height: 26px !important;
    }
  }
  // 隐藏图标，为何会显示该图标？
  .el-input__validateIcon {
    display: none;
  }
  .el-table .el-table__body tr > td {
    // height: 30px;
    // line-height: 30px;
    // padding: 0;
  }
  .el-table__header tr > th {
    // background: #f8f8f8 !important;
    // padding: 0;
    // padding-top: 2px;
  }
}
.el-select-dropdown{
  &.quick-search-select-pop{
    .el-select-dropdown__list{
      padding: 0;
      .el-select-dropdown__item{
        font-size: 12px;
        border-bottom: 1px solid #ddd;
        padding: 0;
        .select-row-title {
          font-weight: bold;
        }
        .border {
          border-right: 1px solid #ddd;
          &:last-child {
            border-right: none;
          }
        }
        .el-col {
          padding: 0 5px;
          white-space: nowrap;
          text-overflow: ellipsis;
          overflow: hidden;
        }
        &.is-disabled {
          /*受portal样式的影响，重新定义 */
          display: inherit;
          visibility: inherit;
          color: #393E45;
        }
      }
    }
  }
}

.el-button.is-disabled {
  background-color: #f5f7fa;
}
.quick-select-body{
  display: flex;
  flex-direction: row;
  flex-wrap: nowrap;
  justify-content: space-between;
  width: 100%;
  .select-body-left{
    width: 100%;
    .search-wrap-left{
      min-height: 43px;
      .el-form{
        padding-right: 150px;
        position: relative;
        min-height: 42px;
        .search-form-btn{
          position: absolute;
          right: 0;
          bottom: 12px;
        }
        .el-row{
          .el-form-item{
            &:first-child {
              margin-bottom: 12px;
              /* 自适应开启 */
              display: flex;
              flex-direction: row;
              flex-wrap: nowrap;
              justify-content: space-between;
              align-items: center;
            }
          }
        }
        .el-form-item__label {
          padding-right: 8px;
          white-space: normal;
          line-height: 15px !important;
          vertical-align: middle;
          max-height: 30px;
          float: none !important;
          display: inline-block !important;
          // width: 35% !important;
          /* 自适应用这两行 */
          max-width: 55%;
          width: auto !important;
          box-sizing: border-box;
          overflow: hidden;
          text-overflow: ellipsis;
          -webkit-box-orient: vertical;
          -webkit-line-clamp: 2;
        }
        /* display: -webkit-box; */
        .el-form-item__content {
          vertical-align: middle;
          display: inline-block;
          margin-left: 0 !important;
          // width: 65% !important;
          box-sizing: border-box;
          /* 自适应开启 */
          flex: 1;
        }
      }
    }
  }
  .select-body-center{
    padding: 100px 20px 50px;
    box-sizing: border-box;
    .el-button{
      padding: 5px 6px;
      width: 82px;
    }
    >div{
      padding: 7px 0;
    }
  }
  .select-body-right{
    width: 45%;
    .search-wrap-right{
      padding-bottom: 12px;
      .el-input-group{
        .el-input__suffix{z-index: 50;}
      }
    }
  }
  // 多选
  &.multiSelect{
    .select-body-left{
      width: 45%;
    }
    // 小屏幕
    &.device-xs{
      display: block;
      .select-body-left{
        width: 100%;
      }
      .select-body-center{
        width: 100%;
        background: #f5f5f5;
        text-align: center;
        margin: 5px 0 15px;
        padding: 5px 0px;
        >div{
          display: inline-block;
          margin: 0 10px 0 0;
        }
      }
      .select-body-right{
        width: 100%;
      }
    }
  }
  // 小屏幕
  &.device-xs{
    .select-body-left{
      .el-form{
        padding-right: 0px;
        .search-form-btn{
          position: static;
          width: 100%;
          text-align: right;
          padding-bottom: 12px;
        }
      }
    }
  }
  .search-wrap{
    padding-top: 2px;
  }
}
.srm-body .fuzzy-pop-warp{
  margin-top: 2px !important;
  padding: 12px;
  .fuzzy-pop-div-page{
    margin: 0;
    padding-bottom: 0px;
    padding-top: 12px;
  }
}
.srm-quicksearch-footer {
  margin-top: 5px;
  float: right;
}
</style>
