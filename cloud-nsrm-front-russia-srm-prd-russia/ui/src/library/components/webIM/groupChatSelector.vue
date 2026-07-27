<!-- 群聊用户选择 -->
<template>
  <el-container class="compernySelector">
    <el-aside
      width="340px"
      style="background: #fff;"
    >
      <el-table
        ref="allUserSelector"
        height="378px"
        style="width: 100%;"
        highlight-current-row
        :data="allData"
        @select="handleSelect"
        @select-all="handleSelectAll"
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
              :placeholder="$t('components.userSelection.inputKeywordSearch')"
              clearable
              @keyup.enter.native="searchUser"
            />
          </template>
          <template slot-scope="scope">
            <div>{{ `${scope.row.nickname}(${scope.row.username})(${scope.row.department})` }}</div>
            <div>{{ scope.row.ceeaCompanyDescr }}({{ scope.row.ceeaCompany }})</div>
          </template>
        </el-table-column>
      </el-table>
      <c-pagination
        style="margin:8px 5px 0px !important;"
        layout="total,prev, next"
        :total="dataPage.total"
        :page-num="dataPage.pageNum"
        :page-size="dataPage.pageSize"
        @current-change="dataCurrentChange"
        @size-change="dataSizeChange"
      />
    </el-aside>
    <el-container>
      <el-main>
        <el-table
          ref="chosenSelector"
          :data="chosenData"
          height="100%"
          style="width: 100%"
        >
          <!-- 已选择 -->
          <el-table-column
            align="left"
            label="已选择的用户"
          >
            <template slot-scope="scope">
              <div>{{ `${scope.row.nickname}(${scope.row.username})(${scope.row.department})` }}</div>
              <div>{{ scope.row.ceeaCompanyDescr }}({{ scope.row.ceeaCompany }})</div>
            </template>
          </el-table-column>
          <el-table-column
            width="40"
            align="center"
            style="cursor: pointer;"
          >
            <template slot-scope="scope">
              <span
                class="el-icon-circle-close closeIcon"
                @click="deleteBuyerChosen(scope.$index)"
              />
            </template>
          </el-table-column>
        </el-table>
      </el-main>
      <el-footer height="40px">
        <div class="comfirmBtn">
          <el-button
            class="detail-pbtn"
            @click="cancelHandel"
          >
            取消
          </el-button>
          <el-button
            class="detail-pbtn"
            type="primary"
            @click="comfirmHandel"
          >
            确定
          </el-button>
        </div>
      </el-footer>
    </el-container>
  </el-container>
</template>
<script>
import { listByBuyer } from '@/api/user'
import { createChatgroup, addChatgroupMember } from '@/api/chat'
import CPagination from 'lib@/components/c-pagination'
export default {
  name: 'GroupChatSelector',
  components: {
    CPagination
  },
  props: {
    isSearch: { // 是否显示搜索框
      type: Boolean,
      default: true
    },
    imModule: {// 当前模式
      type: String,
      default: 'chartModule'
    },
    visible: {
      type: Boolean,
      default: false
    },
    userType: { // 用户类型
      type: String,
      default: 'BUYER'
    }
  },
  data () {
    return {
      searchKey: '',
      queryParame: {
        queryName: '',
        pageNum: 1,
        pageSize: 50
        // userType: this.userType, // 不用限制用户类型
      },
      allData: [], // 全部数据
      chosenData: [], // 已选择数据
      dataPage: {
        total: 0,
        pageNum: 1,
        pageSize: 50
      },
      multiSelect: true, // 群聊的人员选择是多选
      chatgroupDto: {},
      // 新增群newGroup，增加成员addMembers
      openModeSelector: ''
    }
  },
  created () {
    this.searchUser() // 打开查询用户
  },
  methods: {
    // 初始化变量
    initParam (openModeSelectorParam, chatgroupDtoParam) {
      this.openModeSelector = openModeSelectorParam
      this.chatgroupDto = chatgroupDtoParam
    },
    // 搜索用户
    searchUser () {
      this.queryParame.queryName = this.searchKey
      listByBuyer(this.queryParame).then(res => {
        if (res) {
          this.allData = res.data.list
          this.dataPage.total = res.data.total
          this.dataPage.pageNum = res.data.pageNum
          this.dataPage.pageSize = res.data.pageSize
        }
      })
    },
    dataCurrentChange (num) {
      this.queryParame.pageNum = num
      this.searchUser()
    },
    dataSizeChange (size) {
      this.queryParame.pageSize = size
      this.searchUser()
    },
    // 处理单个员工选择
    handleSelect (selection, row) {
      let isChoice = false // 该节点是否被选择，如果存在 selection 中，则说明已经被选择
      let isExist = false // 该节点是否存在已经被选择的员工名单中
      selection.forEach(select => {
        if (row.userId === select.userId) {
          isChoice = true
        }
      })
      this.chosenData.forEach((employee, index) => {
        if (row.userId === employee.userId) {
          isExist = index
        }
      })
      // 多选时被选择员工可以有多个，单选时则最多只能有一个
      if (this.multiSelect) {
        // 如果没有被选择且还存在，则移除
        if (isChoice === false && isExist !== false) {
          this.chosenData.splice(isExist, 1)
        }
        // 如果被选择且还还不在，则添加
        if (isChoice === true && isExist === false) {
          this.chosenData.push(row)
        }
      } else {
        // 单选时处理
        if (isChoice === true) {
          let _this = this
          this.$nextTick(() => {
            _this.$refs.allUserSelector.clearSelection()
            _this.$refs.allUserSelector.toggleRowSelection(row)
          })
          if (isExist === false) {
            this.chosenData = [row]
          }
        } else {
          this.chosenData = []
        }
      }
    },
    // 处理全部员工选择
    handleSelectAll (selection) {
      if (this.allData.length === 0) {
        return true
      }
      const _this = this
      // 1 表示添加全部，-1 表示删除全部
      let handleType = selection.length === this.allData.length ? 1 : -1
      if (handleType === 1) {
        // 添加全部
        _this.allData.forEach(employee => {
          let find = false
          for (let i = 0; i < _this.chosenData.length; i++) {
            if (employee.userId === _this.chosenData[i].userId) {
              find = true
              break
            }
          }
          // 没有找到则添加
          if (find === false) _this.chosenData.push(employee)
        })
      } else if (handleType === -1) {
        // 删除全部
        _this.buyerData.forEach(employee => {
          let find = false
          for (let i = 0; i < _this.chosenData.length; i++) {
            if (employee.userId === _this.chosenData[i].userId) {
              find = i
              break
            }
          }
          // 如果找到则删除
          if (find !== false) _this.chosenData.splice(find, 1)
        })
      }
    },
    // 单个删除被选中员工 chosenData
    deleteBuyerChosen (index) {
      let targetUserId = this.chosenData[index].userId
      this.chosenData.splice(index, 1)
      // 如果被删除的存在被勾选，则取消
      for (let i = 0; i < this.allData.length; i++) {
        if (this.allData[i].userId === targetUserId) {
          this.$refs.allUserSelector.toggleRowSelection(this.allData[i], false)
          break
        }
      }
    },
    cancelHandel () {
      this.$emit('cancel', false)
    },
    comfirmHandel () {
      const _this = this
      const selectedData = this.chosenData // 选择的数据
      if (this.openModeSelector == 'newGroup') {
        // 构造后端ChatgroupDto
        let chatgroupDtoNew = {}
        chatgroupDtoNew.groupType = 'Multi' // 群聊
        chatgroupDtoNew.unreadCount = 0
        chatgroupDtoNew.chatgroupMembers = new Array()
        this.chosenData.forEach((employee, index) => {
          let chatgroupMemberDto = {}
          chatgroupMemberDto.userId = employee.userId
          chatgroupMemberDto.userName = employee.nickname
          chatgroupMemberDto.userType = employee.userType
          chatgroupMemberDto.companyId = employee.companyId
          chatgroupMemberDto.companyName = employee.ceeaCompanyDescr
          chatgroupDtoNew.chatgroupMembers[index] = chatgroupMemberDto
        })
        // 保存群聊天
        createChatgroup(chatgroupDtoNew).then(res => {
          if (res) {
            let resData = res.data
            let groupName = resData.groupName ? resData.groupName.split(',') : []
            let groupNNew = groupName.map(elm => (elm.substr(-2, 2)))
            resData.groupNameArr = groupNNew.slice(0, 4)

            _this.$emit('confirm', resData)
            _this.$emit('cancel', false)
          }
        })
      } else {
        // 添加成员
        let chatgroupDtoNew = {}
        chatgroupDtoNew.chatgroupId = this.chatgroupDto.chatgroupId
        chatgroupDtoNew.chatgroupMembers = []
        this.chosenData.forEach((employee, index) => {
          let chatgroupMemberDto = {}
          chatgroupMemberDto.chatgroupId = this.chatgroupDto.chatgroupId
          chatgroupMemberDto.userId = employee.userId
          chatgroupMemberDto.userName = employee.nickname
          chatgroupMemberDto.userType = employee.userType
          chatgroupMemberDto.companyId = employee.companyId
          chatgroupMemberDto.companyName = employee.ceeaCompanyDescr
          chatgroupDtoNew.chatgroupMembers.push(chatgroupMemberDto)
        })
        // 保存成员
        addChatgroupMember(chatgroupDtoNew).then(res => {
          if (res) {
            _this.$emit('confirm', res.data)
            _this.$emit('cancel', false)
          }
        })
      }
    }
  }
}
</script>
<style lang="scss">
.compernySelector{
  height: 100%;
  background: #fff;
  .el-aside{
    margin: 0;
    padding: 0px;
    border-right: 1px solid #dfe6ec;
  }
  .el-footer{
    height: 40px;
    .comfirmBtn{
      padding-top: 8px;
      text-align: right;
    }
  }
  .closeIcon{
    font-size: 16px;
    vertical-align: middle;
  }
}
</style>
