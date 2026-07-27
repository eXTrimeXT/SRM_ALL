<template>
  <div class="the_catSelect-wrap">
    <el-row style="padding: 0;">
      <el-col
        :span="24"
        style="position: relative;padding: 0;"
        :class="{'multiple-edit-type': !disabled && multiple && selectType=='input'}"
      >
        <!-- 按钮选择形式 -->
        <template v-if="selectType=='button'">
          <el-button
            :disabled="disabled"
            type="primary"
            @click="openDialog()"
          >
            <!-- 品类选择 -->
            {{ $t("cusEntry.vendorMod.allCategorySelect") }}
          </el-button>
        </template>
        <!-- input输入 -->
        <template v-else>
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
                border
                show-overflow="tooltip"
                auto-resize
                sync-resize
                :max-height="280"
                :stripe="true"
                :data="selectData"
                :column-config="{isCurrent: false, isHover: false,resizable: true}"
                :row-config="{isCurrent: true, isHover: true,useKey:true, keyField:rowKey}"
                :radio-config="multiple ? null : {trigger: 'row'}"
                :checkbox-config="multiple ? {trigger: 'row',reserve: true} : null"
                @cell-click="({row,column,cell}) => getLineData(row,column,cell,'fuzzy')"
              >
                <vxe-column
                  field="categoryName"
                  :title="$t('components.category.categoryName')"
                  :min-width="'150px'"
                  align="left"
                  show-overflow="tooltip"
                />
                <vxe-column
                  field="categoryFullName"
                  :title="$t('components.category.categoryFullName')"
                  :min-width="'150px'"
                  align="left"
                  show-overflow="tooltip"
                />
                <template #empty>
                  <div style="color: #96999c;">
                    <p>{{ $t('components.common.noMoreData') }}</p>
                  </div>
                </template>
              </vxe-table>
              <CPagination
                v-if="categoryType !== 'logistics'"
                class="fuzzy-pop-div-page"
                :total="pageInfo.total"
                :page-num="pageInfo.pageNum"
                :page-size="pageInfo.pageSize"
                layout="total, prev, next"
                :pager-count="5"
                @current-change="fuzzyChangeCurrentIndex"
                @size-change="fuzzyChangeCurrentSize"
              />
            </div>
            <el-input
              ref="quickSelectSelect"
              slot="reference"
              v-model="inputValue"
              class="the_cat_select"
              :clearable="true"
              :placeholder="placeholderText"
              :disabled="disabled"
              :readonly="multiple"
              @input="querySearchAsync(inputValue)"
              @focus="focus"
              @clear="clearOptions"
            />
          </el-popover>
          <i
            v-if="inputValue && multiple && !disabled"
            class="el-icon-error multiple-clear-btn"
            @click="multipleClear"
          />
          <el-button
            :disabled="disabled"
            icon="iconfont iconselect"
            :class="['quick-search-btn',{'quick-edit': !disabled }]"
            @click="openDialog()"
          />
        </template>
      </el-col>
    </el-row>
    <!-- 品类选择 -->
    <srm-dialog
      v-if="dialogVisible"
      :title="$t('components.category.categorySelect')"
      class="cat-selector-dialog"
      :size="dialogSize"
      :visible.sync="dialogVisible"
      :close-on-click-modal="false"
      append-to-body
      @close="cancleSelector"
    >
      <div :class="['category-select-body',{'multiSelect': multiple ,'device-xs': device==='device-xs' || device==='device-sm '}]">
        <!-- 左边 -->
        <div class="select-body-left">
          <div class="search-wrap search-wrap-left">
            <el-form
              ref="form"
              :model="queryForm"
              label-width="80px"
            >
              <srm-row :gutter="16">
                <srm-col :initCol="multiple ? 2: 3">
                  <!-- 品类编码 -->
                  <el-form-item :label="multiple ? '' : $t('components.category.categoryCodeAndName')">
                    <el-input
                      v-model="queryForm.categoryCode"
                      :placeholder="$t('components.category.categoryCodeAndName')"
                      @keyup.native.enter="queryHandle"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col :initCol="multiple ? 2: 3">
                  <!-- 品类全称 -->
                  <el-form-item :label="multiple ? '' : $t('components.category.categoryFullName')">
                    <el-input
                      v-model="queryForm.categoryFullName"
                      :placeholder="$t('components.category.categoryFullName')"
                      @keyup.native.enter="queryHandle"
                    />
                  </el-form-item>
                </srm-col>
              </srm-row>
              <div class="search-form-btn">
                <el-button type="primary" @click="queryHandle">
                  <!-- 查询 -->
                  {{ $t("components.common.search") }}
                </el-button>
                <el-button class="resetBtn" @click="reset">
                  {{ $t("components.common.reset") }}
                </el-button>
              </div>
            </el-form>
          </div>
          <vxe-table
            ref="categorySearchTable"
            height="300"
            border
            auto-resize
            sync-resize
            :stripe="true"
            :data="catDataList"
            :column-config="{isCurrent: false, isHover: false,resizable: true}"
            :row-config="{isCurrent: true, isHover: true,useKey:true,keyField:rowKey}"
            :radio-config="multiple ? null : {trigger: 'row'}"
            :checkbox-config="multiple ? {trigger: 'row',reserve: true} : null"
            @current-change="({newValue,oldValue}) => handleCurrentRow(newValue,oldValue)"
            @radio-change="({newValue,oldValue}) => handleCurrentRow(newValue,oldValue)"
            @checkbox-change="(selected) => handleCatSelect(selected.records,selected.checked, selected.row)"
            @checkbox-all="(selected) => handleCatSelect(selected.records,selected.checked, selected.row)"
            @cell-dblclick="({row,column,cell}) => getLineData(row,column,cell)"
          >
            <vxe-column
              v-if="multiple"
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
            <!-- 品类编码 -->
            <vxe-column
              field="categoryCode"
              :title="$t('components.category.categoryCode')"
              width="120"
              :show-overflow="true"
            />
            <!-- 品类名称 -->
            <vxe-column
              field="categoryName"
              :title="$t('components.category.categoryName')"
              :show-overflow="true"
            />
            <!-- 品类名称（中文） -->
            <vxe-column
              field="categoryNameChn"
              :title="$t('components.category.categoryName') + $t('cusEntry.dataConfMod.langSignZh')"
              :show-overflow="true"
            />
            <!-- 品类全称 -->
            <vxe-column
              field="categoryFullName"
              :title="$t('components.category.categoryFullName')"
              :show-overflow="true"
            />
            <template #empty>
              <div style="color: #96999c;">
                <p>{{ $t('components.common.noMoreData') }}</p>
              </div>
            </template>
          </vxe-table>
          <CPagination
            ref="pagerLeft"
            style="margin: 0;padding-bottom: 5px;"
            :total="pageInfo.total"
            :page-num="pageInfo.pageNum"
            :page-size="pageInfo.pageSize"
            :layout="pageLayout"
            :pager-count="5"
            @current-change="catDataCurrentChange"
            @size-change="catDataSizeChange"
          />
        </div>
        <!-- 中间操作按钮 -->
        <div v-if="multiple" class="select-body-center">
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
              class="clearAll"
              :disabled="selectedData.length==0"
              @click="selectedDelAll"
            >
              {{ $t("components.common.clearAll") }}
            </el-button>
          </div>
        </div>
        <!-- 右边选中内容 -->
        <div v-if="multiple" class="select-body-right">
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
            </el-row>
          </div>
          <vxe-table
            ref="categorySelectedTable"
            height="300"
            border
            auto-resize
            sync-resize
            :stripe="true"
            :data="selectedData.slice((currentPage - 1) * currentPageSize, currentPage * currentPageSize)"
            :column-config="{isCurrent: false, isHover: false,resizable: true}"
            :row-config="{isCurrent: true, isHover: true,useKey:true,keyField:rowKey}"
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
            <!-- 品类全称 -->
            <vxe-column
              field="categoryFullName"
              :title="$t('components.category.categoryFullName')"
              show-overflow="tooltip"
            />
            <!-- 品类名称 -->
            <vxe-column
              field="categoryName"
              :title="$t('components.category.categoryName')"
              show-overflow="tooltip"
            />
            <!-- 品类编码 -->
            <vxe-column
              field="categoryCode"
              :title="$t('components.category.categoryCode')"
              width="120"
              show-overflow="tooltip"
            />
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
      <div slot="footer" class="dialog-footer">
        <el-button @click="cancleSelector">
          {{ $t("components.common.cancel") }}
        </el-button>
        <el-button
          type="primary"
          @click="confirmSelector"
        >
          {{ $t("components.common.confirm") }}
        </el-button>
      </div>
    </srm-dialog>
  </div>
</template>

<script>
import CPagination from 'lib@/components/c-pagination'

export default {
  name: 'CCategorySelect',
  components: { CPagination },
  model: {
    event: 'change',
    value: 'value'
  },
  props: {
    // 当前行上绑定的 data 对象
    scope: {
      type: Object,
      default: () => {}
    },
    value: {
      type: [String, Number],
      default: null
    },
    orgId: {
      // 组织ID
      type: [String, Number],
      default: null
    },
    placeholder: {
      type: String,
      default: ''
    },
    showKey: {
      // 取值字段 建议 categoryName categoryCode categoryId
      type: String,
      default: function () {
        return 'categoryName'
      }
    },
    // 显示隐藏
    disabled: {
      type: Boolean,
      default: function () {
        return false
      }
    },
    // 多选
    multiple: {
      type: Boolean,
      default: false
    },
    selectType: {
      // input || button
      type: String,
      default: function () {
        return 'input'
      }
    },
    // category 所有品类 || logistics 所有物流品类 没有分页
    categoryType: {
      type: String,
      default: function () {
        return 'category'
      }
    },
    // 反选显示数据
    selectedLines: {
      type: Array,
      default: () => []
    },
    matchField: { // 回显匹配字段
      type: String,
      default: () => {
        return 'categoryId'
      }
    }
  },
  data () {
    return {
      fuzzyPopVisible: false,
      rowKey: 'categoryId',
      loading: false,
      dialogVisible: false,
      inputValue: null,
      // 查询条件
      queryForm: {
        // categoryName: '',
        categoryCode: '',
        categoryFullName: ''
      },
      // 查询分页变量
      queryPage: {
        pageNum: 1,
        pageSize: 30
      },
      // 分页信息
      pageInfo: {
        total: 0,
        pageNum: 1,
        pageSize: 30
      },
      // 模糊下拉查询
      selectData: [], // 下拉数据列表
      // 左侧表格
      catDataList: [], // 列表数据
      multipleSelection: [],
      currentRow: null,
      queryTotal: 0, // 共几条
      viewIndex: 1,
      viewSize: 30,
      // 右侧侧表格分页
      queryTotalRight: 0, // 共几条
      viewIndexRight: 1,
      viewSizeRight: 30,
      currentPage: 1,
      currentPageSize: 30,
      // 右侧选中栏相关变量
      selectedSearchKey: '', // 右侧关键字查询
      searchKeyPlaceholder: this.$t('components.category.enterKeyword'), //
      selectedData: [], // 多选选中的条目
      selectedDataBak: [], // 用于右侧表格前端查询
      rightMultipleSelected: [], // 右边表格选中条目
      rightCurrentRow: {}
    }
  },
  computed: {
    placeholderText () {
      return this.placeholder || this.$t('dataConfMod.msgCategoryNormalizer')
    },
    userId () {
      return this.$store.getters.userInfo.userId
    },
    // 弹框尺寸 多选的时候弹框大一号
    dialogSize () {
      if (this.multiple) {
        return 'xLarge'
      } else {
        return 'large'
      }
    },
    // 分页布局
    pageLayout () {
      if (this.multiple) {
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
    value: {
      immediate: true,
      deep: true,
      handler: function (val) {
        this.inputValue = val
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
  created () {
    this.inputValue = this.value
  },
  methods: {
    // 打开弹窗
    openDialog () {
      this.dialogVisible = true
      this.multipleSelection = []
      this.selectedData = [] // 多选选中的条目
      this.selectedDataBak = [] // 用于右侧表格前端查询
      this.selectedSearchKey = ''
      this.reset()
      this.queryHandle() // 查询数据
    },
    reset () {
      // this.queryForm.categoryName = ''
      this.queryForm.categoryCode = ''
      this.queryForm.categoryFullName = ''
      // 查询分页变量
      this.queryPage = {
        pageNum: 1,
        pageSize: 30
      }
    },
    // 查询按钮
    queryHandle () {
      this.queryPage.pageNum = 1
      this.fatchAllData()
    },
    // 查询数据
    fatchAllData (searchData, allflag = false) {
      let query = {} // 表单数据
      let queryData = {} // 查询参数
      let resQuery = {} // 最后入参数据
      let fetchUrl = ''
      // 物流寻源 查询的品类
      if (this.categoryType === 'logistics') {
        fetchUrl = '/api-base/purchase/purchaseCategory/listLogisticsCategoryByLevel'
      } else {
        fetchUrl = '/api-base/pj/category/listCatePageByParamForComponent'
        queryData = { ...this.queryPage }
      }
      if (searchData) { // 下拉搜索
        this.loading = true
        resQuery = { ...searchData }
      } else { // 弹框搜索
        query = {
          categoryCode: (this.queryForm.categoryCode).trim(), // 后端通过这个字段去查品类名称和编码
          categoryFullName: (this.queryForm.categoryFullName).trim()
        }
        resQuery = { ...query, ...queryData }
      }
      return this.$http({
        url: fetchUrl,
        method: 'POST',
        loading: !searchData, // 下拉不需要弹框loading
        data: resQuery
      }).then(async res => {
        const { data } = res
        this.catDataList = data.list
        this.selectData = data.list
        // 弹框
        if (!searchData) {
          if (this.categoryType !== 'logistics') {
            this.pageInfo.total = data.total
            this.pageInfo.pageNum = data.pageNum
            this.pageInfo.pageSize = data.pageSize
          }
          if (this.multiple && !searchData && !allflag) {
            this.echoSelectionHandel() // 回显已选择
          }
        } else {
          // 下拉
          this.fuzzyPopVisible = true
          this.loading = false
          if (this.categoryType !== 'logistics') {
            this.pageInfo.total = data.total
            this.pageInfo.pageNum = data.pageNum
            this.pageInfo.pageSize = data.pageSize
          }
        }
      })
    },
    // 查回回显时间
    echoSelectionHandel () {
      let slef = this
      // 有已选择的数据
      // 右边表格赋值
      if (this.selectedLines.length > 0) {
        this.selectedLines.forEach(item => {
          let categoryId = item.categoryId
          let selectedRow = slef.catDataList.find(i => (i.categoryId == categoryId)) // 取数据
          let selectedIndex = slef.selectedData.findIndex(o => (o.categoryId == categoryId)) // 判断是否已经在右边表格

          if (selectedIndex < 0) {
            if (selectedRow) {
              slef.selectedData.push({ ...selectedRow })
              slef.selectedDataBak.push({ ...selectedRow })
            } else {
              let obj = { categoryName: item.categoryName, categoryId: item.categoryId, categoryCode: item.categoryCode, categoryFullName: item.categoryFullName }
              slef.selectedData.push(obj)
              slef.selectedDataBak.push(obj)
            }
          }
        })
      }
      // 左边表格勾选
      if (slef.selectedData.length > 0) {
        for (let i = 0; i < slef.selectedData.length; i++) {
          let categoryId = slef.selectedData[i].categoryId
          let leftRow = this.catDataList.find(i => (i.categoryId == categoryId))
          if (leftRow) {
            if (this.$refs.categorySearchTable) {
              this.$refs.categorySearchTable.setCheckboxRow([leftRow], true)
            }
          }
        }
      }
      this.selectedDataTotal() // 计算右边总数
    },
    catDataCurrentChange (num) {
      this.queryPage.pageNum = num
      this.fatchAllData()
    },
    catDataSizeChange (size) {
      this.queryPage.pageNum = 1
      this.queryPage.pageSize = size
      this.fatchAllData()
    },
    // 异步查询 , cb
    querySearchAsync (queryVal) {
      if (!queryVal) return
      let query = {}
      let results = []
      let queryKey = queryVal.trim()
      query.categoryCode = queryKey
      query.pageNum = 1 // 从第一页查
      query.pageSize = this.queryPage.pageSize
      clearTimeout(this.timeout)
      this.timeout = setTimeout(() => {
        this.fatchAllData(query, false)
      }, 1000 * Math.random())
    },
    selectedCheckChange (selected) {
      this.rightMultipleSelected = selected
    },
    selectedHandleCurrentChange (row) {
      this.rightCurrentRow = row
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
        let categoryId = item.categoryId
        let hasIndex = this.selectedData.findIndex(i => (i.categoryId == categoryId))
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
        let maxTotalIndex = pageSizes.findIndex(i => i > this.pageInfo.total) // 大于总数的分页
        if (maxTotalIndex > -1) {
          this.queryPage.pageSize = pageSizes[maxTotalIndex]
          this.pageInfo.pageSize = pageSizes[maxTotalIndex]
        } else {
          // let maxSizeIndex = pageSizes.length - 1 // 取最大分页
          // this.queryPage.pageSize = pageSizes[maxSizeIndex]
          // this.pageInfo.pageSize = pageSizes[maxSizeIndex]
          this.$refs.pagerLeft.pageSizes.push(this.pageInfo.total)
          this.queryPage.pageSize = this.pageInfo.total
          this.pageInfo.pageSize = this.pageInfo.total
        }
      } else {
        let maxSizeIndex = pageSizes.length - 1 // 取最大分页
        this.queryPage.pageSize = pageSizes[maxSizeIndex]
        this.pageInfo.pageSize = pageSizes[maxSizeIndex]
      }
      await this.fatchAllData(null, true)
      this.$refs.categorySearchTable.setAllCheckboxRow(true)
      this.multipleSelection = this.$refs.categorySearchTable.getCheckboxRecords()
      this.selectedData = [...this.multipleSelection]
      this.selectedDataBak = [...this.multipleSelection]
      this.selectedDataTotal()
    },
    async selectedDel () {
      // 去掉左边表格的选中效果
      // await this.$refs.categorySearchTable.setCheckboxRow(this.rightMultipleSelected, false)
      // 去掉左边表格的选中效果
      this.rightMultipleSelected.forEach(elm => {
        let categoryId = elm.categoryId
        const row = this.catDataList.find(i => (i.categoryId == categoryId))
        if (row) {
          this.$refs.categorySearchTable.setCheckboxRow(row, false)
        }
      })
      // 清除右边表格选中状态
      await this.$refs.categorySelectedTable.clearCheckboxRow()
      // 获取左边选中条目
      this.multipleSelection = this.$refs.categorySearchTable.getCheckboxRecords()
      // 赋值右边表格
      this.selectedData = [...this.multipleSelection]
      this.selectedDataBak = [...this.multipleSelection]
      this.selectedDataTotal()
      this.rightMultipleSelected = []
    },
    // 清除所有
    async selectedDelAll () {
      await this.$refs.categorySearchTable.clearCheckboxRow() // 清除左边所有选中数据
      // 清除右边表格选中状态
      await this.$refs.categorySelectedTable.clearCheckboxRow()
      // 获取左边选中条目
      this.multipleSelection = this.$refs.categorySearchTable.getCheckboxRecords()
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
    // 右侧表格模糊查询
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
      let filterKey = ['categoryName', 'categoryCode'] // 根据品类名称 品类编码查询
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
    // 勾选做右侧表格数据同步操作 // 全选的row 为null
    modifyDataHandel (checked, row) {
      if (row) {
        let categoryId = row.categoryId
        let hasIndex = this.selectedData.findIndex(i => (i.categoryId == categoryId))
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
    // checkbox变更
    handleCatSelect (selected, checked, row) {
      this.multipleSelection = selected
      this.modifyDataHandel(checked, row) // 去掉勾选数据同步 宏旺
    },
    handleCurrentRow (val) {
      this.currentRow = val
    },
    // 单选确认
    confirmSelector () {
      if (this.multiple) { // 多选确认
        const selectedData = this.selectedData || []
        const selectedDataBak = this.selectedDataBak || []
        let resultSelected = null
        // 存在右侧搜索情况
        if (selectedDataBak.length > selectedData.length) {
          resultSelected = selectedDataBak
        } else {
          resultSelected = selectedData
        }
        if (resultSelected == 0) {
          this.$message({
            type: 'error',
            message: this.$t('components.common.placeSelectData')
          })
          return
        }
        // 多选输入框显示的时候显示值
        if (this.selectType == 'input') {
          let inputText = (resultSelected.map(i => (i[this.showKey]))).toString()
          this.inputValue = inputText
          this.$emit('change', inputText)
        }
        this.$emit('select', resultSelected)
      } else { // 单选
        let showKeyVal = this.currentRow[this.showKey]
        this.inputValue = showKeyVal
        this.$emit('change', showKeyVal)
        this.$emit('select', this.currentRow, this.scope)
      }
      this.dialogVisible = false
    },
    // 双击选中行数据
    getLineData (row = {}, column, cell, type) {
      if (this.multiple) return
      let showKeyVal = row[this.showKey]
      this.inputValue = showKeyVal
      this.$emit('change', showKeyVal)
      this.$emit('select', row, this.scope)
      if (type && type == 'fuzzy') {
        // 下拉模糊
        this.fuzzyPopVisible = false
      } else {
        this.dialogVisible = false
      }
    },
    // 取消 关闭
    cancleSelector () {
      this.dialogVisible = false
    },
    // 清空
    clearOptions () {
      this.selectData = []
      // this.queryForm.categoryName = ''
      this.queryForm.categoryCode = ''
      this.queryForm.categoryFullName = ''
      this.clearInput()
    },
    clearInput () {
      this.inputValue = null
      this.$emit('change', null)
      this.$emit('select', null)
    },
    // focus
    focus () {
      this.clearOptions()
    },
    // 模糊查询分页
    fuzzyChangeCurrentIndex (currentNum) {
      this.queryPage.pageNum = currentNum
      this.queryPage['categoryCode'] = this.inputValue
      this.fatchAllData(this.queryPage, false)
    },
    // 模糊查询分页
    fuzzyChangeCurrentSize (currentSize) {
      this.queryPage.pageSize = currentSize
      this.queryPage['categoryCode'] = this.inputValue
      this.fatchAllData(this.queryPage, false)
    },
    // 点击pop外面的区域需要关闭pop弹框
    closePopOutside () {
      this.inputValue = ''
      this.fuzzyPopVisible = false
      this.$emit('select', null)
    },
    multipleClear () {
      this.inputValue = ''
      this.$emit('change', '')
      let selectedData = null
      if (this.multiple) {
        selectedData = []
      }
      this.$emit('select', selectedData)
    }
  }
}
</script>
<style scoped lang="scss">
.the_catSelect-wrap {
  .the_cat_select {
    display: block;
    padding: 0;
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
  .multiple-clear-btn{
    position: absolute;
    top: 1px;
    bottom: 1px;
    right: 32px;
    color: #96999c;
    line-height: 26px;
    cursor: pointer;
  }
  .the_cat_select :deep(.el-input__suffix) {
    right: 25px;
    .el-input__suffix-inner{
      .el-select__caret.el-input__icon.el-icon-search{
        display: none;
      }
    }
  }
}

</style>
<style lang="scss">
  .cat-select{
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
  .category-select-body{
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
          margin: 5px 0;
          padding: 5px 0px 15px;
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
  .multiple-edit-type{
    .multiple-clear-btn{
      display: none;
    }
    &:hover{
      .multiple-clear-btn{
        display: block;
      }
    }
  }
</style>
