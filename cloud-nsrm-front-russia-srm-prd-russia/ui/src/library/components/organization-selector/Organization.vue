<template>
  <div :class="['organization-selector-wrap', {'multiSelect': multiple ,'device-xs': device==='device-xs' || device==='device-sm '}]">
    <!-- 左边 -->
    <div class="select-body-left">
      <div class="search-wrap search-wrap-left">
        <el-form
          ref="form"
          :model="queryForm"
          label-width="80px"
        >
          <srm-row :gutter="12">
            <!-- 组织名称 -->
            <srm-col :initCol="multiple ? 1 : 2">
              <el-form-item>
                <el-input
                  v-model="queryForm.organizationName"
                  :placeholder="$t('components.organization.orgNameCode')"
                  @keyup.native.enter="queryHandle"
                >
                  <!-- <DictSelect
                    slot="prepend"
                    v-model="nodeTypeValue"
                    style="width:90px;"
                    code="ORG_TYPE_ALL"
                    custom-select-type="ORG_TYPE_ALL"
                    :clearable="false"
                    :filterable="false"
                    disabled
                  /> -->
                </el-input>
                <el-input style="display:none;" />
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
        ref="orgSearchTable"
        height="300"
        border
        show-overflow
        auto-resize
        sync-resize
        :stripe="true"
        :data="orgDataList.slice((queryPage.pageNum - 1) * (queryPage.pageSize), queryPage.pageNum * queryPage.pageSize)"
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
        <!-- 组织名称 -->
        <vxe-column
          field="organizationName"
          :title="$t('components.organization.organizationName')"
        />
        <!-- 组织编码 -->
        <vxe-column
          field="organizationCode"
          :title="$t('components.organization.organizationCode')"
        />
        <template #empty>
          <div style="color: #96999c;">
            <p>{{ $t('components.common.noMoreData') }}</p>
          </div>
        </template>
      </vxe-table>
      <CPagination
        ref="pagerLeft"
        class="pagerLeft"
        style="margin: 0;padding-bottom: 5px;"
        :total="pageInfo.total"
        :page-num="pageInfo.pageNum"
        :page-size="pageInfo.pageSize"
        :layout="pageLayout"
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
        <srm-row :gutter="16">
          <srm-col :initCol="device==='device-xs' ? 1: 2">
            <el-input
              v-model="selectedSearchKey"
              :disabled="selectedDataBak.length==0"
              :placeholder="searchKeyPlaceholder"
              clearable
              @submit.native.stop.prevent="searchSelected(selectedSearchKey)"
              @clear="resetSearchSelected"
            >
              <el-button
                slot="append"
                icon="el-icon-search"
                @click="searchSelected(selectedSearchKey)"
              />
            </el-input>
            <el-input style="display:none;" />
          </srm-col>
        </srm-row>
      </div>
      <vxe-table
        ref="orgSelectedTable"
        height="300"
        border
        show-overflow
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
        <!-- 组织名称 -->
        <vxe-column
          field="organizationName"
          :title="$t('components.organization.organizationName')"
        />
        <!-- 组织编码 -->
        <vxe-column
          field="organizationCode"
          :title="$t('components.organization.organizationCode')"
          width="120"
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
        @current-change="changeCurrentIndexRight"
        @size-change="changeCurrentSizeRight"
      />
    </div>
  </div>
</template>

<script>
import { generateUid } from 'lib@/utils/generator'
import CPagination from 'lib@/components/c-pagination'
import { listByBuyer } from '@/api/user'
import { createDictClass } from '@/library/utils/dict/dict-utils'
// const orgTypeDictClass = createDictClass().setCustomSelectType('ORG_TYPE_ALL').loadCustomSelectType('ORG_TYPE_ALL')

export default {
  name: 'Organization',
  components: {
    CPagination
  },
  props: {
    multiple: {
      type: Boolean,
      default: true
    },
    resetSelect: {
      type: Boolean,
      default: true
    },
    dialogVisible: {
      type: Boolean,
      default: false
    },
    // 回显数据
    defaultValue: {
      type: Array,
      default: () => {
        return []
      }
    },
    matchField: { // 回显匹配字段
      type: String,
      default: () => {
        return 'organizationId'
      }
    },
    filterInput: {
      // 父页面传值input
      type: String,
      default: ''
    },
    nodeType: {
      type: String,
      default: 'OU' // 默认值OU节点 //GROUP
    },
    selectType: {
      // input || button
      type: String,
      default: function () {
        return 'input'
      }
    },
    nodeTypeData: {
      type: Array,
      default: () => {
        return []
      }
    }
  },
  data () {
    return {
      rowKey: 'organizationId',
      domKey: generateUid(),
      nodeTypeValue: 'OU',
      // orgTypeDictClass: orgTypeDictClass,
      // 搜索数据框
      // 查询条件
      queryForm: {
        organizationName: '',
        organizationCode: ''
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
      orgDataList: [], // 列表数据
      orgDataListBak: [],
      multipleSelection: [],
      currentRow: null,
      // 右侧侧表格分页
      queryTotalRight: 0, // 共几条
      viewIndexRight: 1,
      viewSizeRight: 30,
      currentPage: 1,
      currentPageSize: 30,
      // 右侧选中栏相关变量
      selectedSearchKey: '', // 右侧关键字查询
      searchKeyPlaceholder: this.$t('components.common.enterKeyword'), //
      selectedData: [], // 多选选中的条目
      selectedDataBak: [], // 用于右侧表格前端查询
      rightMultipleSelected: [], // 右边表格选中条目
      rightCurrentRow: {}
    }
  },
  computed: {
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
    dialogVisible: {
      immediate: true,
      handler (visible) {
        this.nodeTypeValue = this.nodeType
        if (visible) {
          this.reset()
          this.domKey = generateUid()
          // 查询数据
        } else {
          this.reset()
        }
      }
    }
  },
  created () {
    this.nodeTypeValue = this.nodeType
    this.reset()
    this.fatchAllData()
  },
  mounted () {
    if (this.multiple) {
      // 多选下面显示input框回显已勾选数据
      if (this.defaultValue.length > 0) {
        let _self = this
        this.$nextTick(() => {
          _self.defaultValue.forEach(elm => {
            let orgId = elm[this.matchField] // 回显匹配ID
            const row = _self.orgDataList.find(i => (i.organizationId == orgId))
            if (row) {
              _self.$refs.orgSearchTable.setCheckboxRow(row, true)
            }
          })
          // 获取左边选中条目
          this.multipleSelection = this.$refs.orgSearchTable.getCheckboxRecords()
          // 赋值右边表格
          this.selectedData = [...this.multipleSelection]
          this.selectedDataBak = [...this.multipleSelection]
          this.selectedDataTotal()
          this.rightMultipleSelected = []
        })
      }
    }
  },
  methods: {
    reset () {
      this.queryForm.organizationCode = ''
      this.queryForm.organizationName = ''
      // 查询分页变量
      this.queryPage = {
        pageNum: 1,
        pageSize: 30
      }
      this.queryHandle() // 重置后需要恢复初始化数据
    },
    // 左边查询按钮
    queryHandle () {
      let keyword = this.queryForm.organizationName
      let myData = []
      if (keyword) {
        myData = this.filterSelected(this.orgDataListBak, keyword)
        this.queryPage.pageNum = 1 // 搜索以后从第一页查询
      } else {
        myData = this.orgDataListBak
      }
      this.orgDataList = myData
      this.pageInfo.total = myData.length
    },
    // 查询数据
    fatchAllData () {
      this.orgDataList = this.nodeTypeData
      this.orgDataListBak = this.nodeTypeData
      this.pageInfo.total = this.nodeTypeData.length
    },
    catDataCurrentChange (num) {
      this.queryPage.pageNum = num
    },
    catDataSizeChange (size) {
      this.queryPage.pageSize = size
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
        let organizationId = item.organizationId
        let hasIndex = this.selectedData.findIndex(i => (i.organizationId == organizationId))
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
          let maxSizeIndex = pageSizes.length - 1 // 取最大分页
          this.queryPage.pageSize = pageSizes[maxSizeIndex]
          this.pageInfo.pageSize = pageSizes[maxSizeIndex]
        }
      } else {
        let maxSizeIndex = pageSizes.length - 1 // 取最大分页
        this.queryPage.pageSize = pageSizes[maxSizeIndex]
        this.pageInfo.pageSize = pageSizes[maxSizeIndex]
      }
      await this.fatchAllData()
      await this.$refs.orgSearchTable.setAllCheckboxRow(true)
      this.multipleSelection = this.$refs.orgSearchTable.getCheckboxRecords()
      this.selectedData = [...this.multipleSelection]
      this.selectedDataBak = [...this.multipleSelection]
      this.selectedDataTotal()
    },
    async selectedDel () {
      // 去掉左边表格的选中效果
      this.rightMultipleSelected.forEach(elm => {
        let orgId = elm.organizationId
        const row = this.orgDataList.find(i => (i.organizationId == orgId))
        if (row) {
          this.$refs.orgSearchTable.setCheckboxRow(row, false)
        }
      })
      // 清除右边表格选中状态
      await this.$refs.orgSelectedTable.clearCheckboxRow()
      // 获取左边选中条目
      this.multipleSelection = this.$refs.orgSearchTable.getCheckboxRecords()
      // 赋值右边表格
      this.selectedData = [...this.multipleSelection]
      this.selectedDataBak = [...this.multipleSelection]
      this.selectedDataTotal()
      this.rightMultipleSelected = []
    },
    // 清除所有
    async selectedDelAll () {
      await this.$refs.orgSearchTable.clearCheckboxRow() // 清除左边所有选中数据
      // 清除右边表格选中状态
      await this.$refs.orgSelectedTable.clearCheckboxRow()
      // 获取左边选中条目
      this.multipleSelection = this.$refs.orgSearchTable.getCheckboxRecords()
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
      let key = keyword.toString()
      let filterKey = ['organizationName', 'organizationCode'] // 根据组织名称 组织编码查询
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
        let organizationId = row.organizationId
        let hasIndex = this.selectedData.findIndex(i => (i.organizationId == organizationId))
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
      this.modifyDataHandel(checked, row)
    },
    handleCurrentRow (val) {
      this.currentRow = val
    },

    // 双击选中行数据
    getLineData (row = {}) {
      if (this.multiple) return
      this.$emit('dblclick', row)
    }
  }
}
</script>

<style lang="scss">
  .organization-selector-wrap{
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
        .opration-div{
          .el-button{
            padding: 8px 10px;
            &+ .el-button {
                margin-left: 6px;
            }
          }
        }
      }
      .pagerLeft{
        .pagination{
          .el-pagination__sizes{
            .el-select{
              .el-input{
                .el-input__suffix{
                  .el-input__validateIcon{
                    display: none;
                  }
                }
              }
            }
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
      width: 42%;
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
        width: 48%;
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
</style>
