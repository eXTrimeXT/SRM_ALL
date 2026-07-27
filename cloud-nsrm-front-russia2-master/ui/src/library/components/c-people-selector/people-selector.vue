<template>
  <div class="people-selector-wrap">
    <!-- 人员选择器 -->
    <section
      :key="`people_${domKey}`"
      class="people-selector selector-section"
    >
      <div class="section-content">
        <div class="overflow-box">
          <el-table
            ref="BuyerSelector"
            style="width: 100%"
            height="300px"
            highlight-current-row
            :data="buyerData"
            @select="handleBuyerSelect"
            @select-all="handleBuyerSelectAll"
          >
            <el-table-column
              type="selection"
              align="center"
              width="50"
            />
            <el-table-column align="left">
              <template
                slot="header"
                slot-scope="scope"
              >
                <!-- 输入关键字搜索 -->
                <el-input
                  v-model="searchKey"
                  :placeholder="
                    $t('components.userSelection.inputKeywordSearch')
                  "
                  clearable
                  @keyup.enter.native="searchUser"
                />
              </template>
              <template slot-scope="scope">
                {{
                  normalizer
                    ? normalizer(scope)
                    : `${scope.row.username} / ${scope.row.nickname} / ${
                      scope.row.department
                    }`
                }}
              </template>
            </el-table-column>
          </el-table>
          <c-pagination
            layout="prev, next"
            :total="buyerDataPage.total"
            :page-num="buyerDataPage.pageNum"
            :page-size="buyerDataPage.pageSize"
            @current-change="buyerDataCurrentChange"
            @size-change="buyerDataSizeChange"
          />
        </div>
      </div>
    </section>
    <section
      :key="`people_opt_${domKey}`"
      class="select-opt selector-section"
    >
      <el-button
        :disabled="!multiSelect"
        @click="addAllBuyer"
      >
        <!-- 全部添加 -->
        {{ $t("components.userSelection.allAdd") }}
      </el-button>
      <el-button @click="deleteAllBuyerChosen">
        <!-- 全部删除 -->
        {{ $t("components.userSelection.allDelete") }}
      </el-button>
    </section>
    <section
      :key="`people_selected_${domKey}`"
      class="people-selected selector-section"
    >
      <div class="section-content">
        <el-table
          ref="buyerChosenSelector"
          :data="buyerChosenData"
          height="340px"
          style="width: 100%"
        >
          <!-- 已选择 -->
          <el-table-column
            align="left"
            :label="$t('components.userSelection.selected')"
          >
            <template slot-scope="scope">
              {{ normalizer ? normalizer(scope) : (scope.row.username || scope.row.nickname) }}
            </template>
          </el-table-column>
          <el-table-column
            width="40"
            align="center"
            style="cursor: pointer;"
          >
            <template slot-scope="scope">
              <span
                class="el-icon-close"
                @click="deleteBuyerChosen(scope.$index)"
              />
            </template>
          </el-table-column>
        </el-table>
      </div>
    </section>
    <!-- END -->
  </div>
</template>

<script>
import { generateUid } from 'lib@/utils/generator'
import CPagination from 'lib@/components/c-pagination'
import { listByBuyer } from '@/api/user'

export default {
  name: 'PeopleSelector',
  components: {
    CPagination
  },
  props: {
    normalizer: {
      type: Function,
      default: null
    },
    multiSelect: {
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
    defaultValue: {
      type: Array,
      default: () => {
        return []
      }
    },
    filterInput: {
      // 父页面传值input
      type: String,
      default: ''
    },
    userType: {
      type: String
    }
  },
  data () {
    return {
      firstLoad: true,
      domKey: generateUid(),
      // 搜索数据框
      searchKey: '',
      // 人员数据
      peopleData: [],
      searchSelectedKey: '',
      selectedPeopleData: [],
      selectedPeople: [],
      // 员工数据
      buyerData: [],
      // 已经选择的数据
      buyerChosenData: [],
      buyerDataPage: {
        total: 0,
        pageNum: 1,
        pageSize: 10
      },
      buyerQuery: {
        // nickname: '',
        // username: '',
        queryName: '',
        userType: this.userType,
        pageNum: 1,
        pageSize: 10
      }
    }
  },
  watch: {
    dialogVisible: {
      immediate: true,
      handler (visible) {
        if (visible) {
          this.buyerChosenData = [].concat(this.defaultValue)
          if (this.resetSelect) {
            this.resetData()
            this.domKey = generateUid()
            this.firstLoad = true
          }
          this.fatchBuyerData()
        } else {
          this.resetData()
        }
      }
    }
  },
  created () {
    this.resetData()
  },
  methods: {
    fatchBuyerData () {
      listByBuyer(this.buyerQuery).then(res => {
        if (res) {
          this.buyerData = res.data.list
          this.buyerDataPage.total = res.data.total
          this.buyerDataPage.pageNum = res.data.pageNum
          this.buyerDataPage.pageSize = res.data.pageSize
        }
      })
    },
    buyerDataCurrentChange (num) {
      this.buyerQuery.pageNum = num
      this.fatchBuyerData()
    },
    buyerDataSizeChange (size) {
      this.buyerQuery.pageSize = size
      this.fatchBuyerData()
    },
    // 搜索用户
    searchUser () {
      // this.buyerQuery.nickname = this.searchKey
      // this.buyerQuery.username = this.searchKey
      this.buyerQuery.queryName = this.searchKey
      this.fatchBuyerData()
    },
    // 处理单个员工选择
    handleBuyerSelect (selection, row) {
      let isChoice = false // 该节点是否被选择，如果存在 selection 中，则说明已经被选择
      let isExist = false // 该节点是否存在已经被选择的员工名单中
      selection.forEach(select => {
        if (row.userId === select.userId) {
          isChoice = true
        }
      })
      this.buyerChosenData.forEach((employee, index) => {
        if (row.userId === employee.userId) {
          isExist = index
        }
      })
      // 多选时被选择员工可以有多个，单选时则最多只能有一个
      if (this.multiSelect) {
        // 如果没有被选择且还存在，则移除
        if (isChoice === false && isExist !== false) {
          this.buyerChosenData.splice(isExist, 1)
        }
        // 如果被选择且还还不在，则添加
        if (isChoice === true && isExist === false) {
          this.buyerChosenData.push(row)
        }
      } else {
        // 单选时处理
        if (isChoice === true) {
          let _this = this
          this.$nextTick(() => {
            _this.$refs.BuyerSelector.clearSelection()
            _this.$refs.BuyerSelector.toggleRowSelection(row)
          })
          if (isExist === false) {
            this.buyerChosenData = [row]
          }
        } else {
          this.buyerChosenData = []
        }
      }
    },
    // 处理全部员工选择
    handleBuyerSelectAll (selection) {
      if (this.buyerData.length === 0) {
        return true
      }
      const _this = this
      // 1 表示添加全部，-1 表示删除全部
      let handleType = selection.length === this.buyerData.length ? 1 : -1
      if (handleType === 1) {
        // 添加全部
        _this.buyerData.forEach(employee => {
          let find = false
          for (let i = 0; i < _this.buyerChosenData.length; i++) {
            if (employee.userId === _this.buyerChosenData[i].userId) {
              find = true
              break
            }
          }
          // 没有找到则添加
          if (find === false) _this.buyerChosenData.push(employee)
        })
      } else if (handleType === -1) {
        // 删除全部
        _this.buyerData.forEach(employee => {
          let find = false
          for (let i = 0; i < _this.buyerChosenData.length; i++) {
            if (employee.userId === _this.buyerChosenData[i].userId) {
              find = i
              break
            }
          }
          // 如果找到则删除
          if (find !== false) _this.buyerChosenData.splice(find, 1)
        })
      }
    },
    // 单个删除被选中员工 buyerChosenData
    deleteBuyerChosen (index) {
      let targetUserId = this.buyerChosenData[index].userId
      this.buyerChosenData.splice(index, 1)
      // 如果被删除的存在被勾选，则取消
      for (let i = 0; i < this.buyerData.length; i++) {
        if (this.buyerData[i].userId === targetUserId) {
          this.$refs.BuyerSelector.toggleRowSelection(this.buyerData[i], false)
          break
        }
      }
    },
    // 添加全部人员
    addAllBuyer () {
      // 添加全部
      this.buyerData.forEach(buyer => {
        let find = false
        for (let i = 0; i < this.buyerChosenData.length; i++) {
          if (buyer.userId === this.buyerChosenData[i].userId) {
            find = true
            break
          }
        }
        // 没有找到则添加
        if (find === false) this.buyerChosenData.push(buyer)
      })
    },
    // 删除全部被选中员工
    deleteAllBuyerChosen () {
      this.buyerChosenData = []
      this.$refs.BuyerSelector.clearSelection()
    },
    // 重置数据
    resetData () {
      this.buyerData = []
    }
  }
}
</script>

<style lang="scss" scoped>
.people-selector-wrap {
  height: 340px;
  .selector-section {
    top: 1px;
    float: left;
    position: relative;
    height: 100%;
    box-sizing: border-box;
    &.people-selector {
      padding-left: 15px;
      padding-right: 15px;
      width: 50%;
    }
    &.people-selected {
      width: 35%;
    }
    &.select-opt {
      width: 15%;
      padding-right: 15px;
      .el-button {
        display: inline-block;
        margin: 0;
        width: 100%;
        margin-bottom: 10px;
        &:first-child {
          margin-top: 50px;
        }
      }
    }
    .section-content {
      position: relative;
      height: 100%;
      border: 1px solid #eaeaea;
      box-sizing: border-box;
      overflow: hidden;
      .c-pagination {
        margin: 8px 5px !important;
      }
    }
  }
}
</style>
