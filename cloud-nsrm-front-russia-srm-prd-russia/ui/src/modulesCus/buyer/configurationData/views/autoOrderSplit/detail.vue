<template>
  <el-container class="flex-container the-purInvoice-detail" direction="vertical">
    <el-main>
      <el-form ref="relForm" :rules="formRules" :model="formData">
        <el-row :gutter="32">
          <el-col :span="8">
            <el-form-item :label="'管理单元'" prop="orgId">
              <OrganizationSelector
                v-model="formData.orgId"
                node-type="OU"
                :parent-id="-1"
                :placeholder="$t('common.pleaseSelect')"
                @select="orgSelect"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="'管理单元编码'" prop="orgCode">
              <el-input v-model="formData.orgCode" readonly placeholder="选择管理单元自动生成" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="'定时任务'" prop="pushTime">
              <!-- <el-input v-model="formData.pushTime" /> -->
              <el-time-picker
                v-model="formData.pushTime"
                arrow-control
                :format="'HH:mm'"
                :value-format="'HH:mm'"
                placeholder="任意时间点"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="'周期'" prop="pushDate">
              <dict-select
                v-model="formData.pushDate"
                code="WEEK"
                :multiple="true"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="'是否短信通知'" prop="notifyFlag">
              <el-select v-model="formData.notifyFlag" placeholder="请选择是否短信通知" @change="initUser">
                <el-option label="是" value="Y" />
                <el-option label="否" value="N" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col v-if="formData.notifyFlag == 'Y'" :span="8">
            <el-form-item :label="'触发短信人员'" prop="notifyType">
              <el-select v-model="formData.notifyType" placeholder="请选择触发短信人员" @change="changNotifyType">
                <el-option label="短信触发人员" value="短信触发人员" />
                <el-option label="本单位采购员" value="本单位采购员" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col v-if="formData.notifyType == '本单位采购员' " :span="8">
            <el-form-item :label="'分单人员'">
              <el-select v-model="formData.pushUserCode" placeholder="请选择分单人员">
                <el-option v-for="item in userList" :key="item.personInChargeUserId" :label="item.personInChargeNickname" :value="item.personInChargeUsername" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col v-if="formData.notifyType != '本单位采购员' " :span="8">
            <el-form-item :label="'分单人员'">
              <QuickSearch
                :show-input="formData.pushUserName"
                show-key="nickname"
                auto-query
                :scope-data="formData"
                name="scc_rbac_user_display"
                @close-quicksearch="setUserObj"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <el-collapse v-if="formData.notifyFlag == 'Y' && formData.notifyType == '短信触发人员'" v-model="activeName" class="tab-form-style">
        <el-collapse-item :title="'短信触发人员'" name="1">
          <el-button
            type="primary"
            style="margin-bottom:10px"
            @click="handleAddUser"
          >
            新增人员
          </el-button>
          <el-table
            ref="selectUserList"
            :data="selectUserList"
            border
            max-height="500px"
          >
            <el-table-column
              align="center"
              prop="userCode"
              :label="'员工工号'"
            >
              <template slot-scope="scope">
                <QuickSearch
                  :show-input="scope.row.userCode"
                  show-key="ceeaEmpNo"
                  auto-query
                  :scope-data="scope.row"
                  name="scc_rbac_user_display"
                  @close-quicksearch="setUserRow"
                />
              </template>
            </el-table-column>
            <el-table-column
              align="center"
              prop="userName"
              :label="'员工名称'"
            >
              <template slot-scope="scope">
                <el-input v-model="scope.row.userName" placeholder="请输入员工名称" />
              </template>
            </el-table-column>
            <el-table-column
              align="center"
              prop="userMobile"
              :label="'员工电话'"
            >
              <template slot-scope="scope">
                <el-input v-model="scope.row.userMobile" placeholder="请输入员工电话" @blur="handelByMobile(scope.row.userMobile,scope.$index)" />
              </template>
            </el-table-column>
            <el-table-column
              align="center"
              prop="userCode"
              :label="'操作'"
            >
              <template slot-scope="scope">
                <el-button @click="handleDeleteUser(scope.$index)">
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-collapse-item>
      </el-collapse>
      <CToolbar>
        <template slot="right">
          <el-button @click="handleCancel">
            关闭
          </el-button>
          <el-button
            type="primary"
            @click="handleSubmit"
          >
            提交
          </el-button>
        </template>
      </CToolbar>
    </el-main>
  </el-container>
</template>

<script>
import BaseForm from 'lib@/components/BaseForm'
import CToolbar from 'lib@/components/c-toolbar'
import pictureCard from 'lib@/composition/oneStopShopping/pictureCard'
import { tabTodoMixin } from '@/utils/mixins'
import { parseTime } from '@/utils'
import { transformMQL } from 'lib@/utils/util'
import OrganizationSelector from 'lib@/components/organization-selector'
import { validEmail, validatePhone } from '@/utils/validate'
import QuickSearch from 'lib@/components/QuickSearch'
export default {
  name: 'AutoOrderSplitDetail',
  components: {
    BaseForm,
    pictureCard,
    CToolbar,
    OrganizationSelector,
    QuickSearch
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      formRules: {
        orgCode: [
          { required: true, message: '请选择管理单元编码' }
        ],
        orgId: [
          { required: true, message: '请选择管理单元' }
        ],
        pushTime: [
          { required: true, message: '请选择定时任务' }
        ],
        pushDate: [
          { required: true, message: '请选择周期' }
        ],
        notifyFlag: [
          { required: true, message: '请选择是否短信通知' }
        ],
        notifyType: [
          { required: true, message: '请选择触发短信人员' }
        ]
      },
      formData: {
        configId: null,
        orgId: null,
        orgCode: null,
        orgName: null,
        pushDate: [],
        pushTime: null,
        notifyFlag: 'N',
        notifyType: null,
        pushUserId: null,
        pushUserName: null,
        pushUserCode: null,
        notifyList: [],
        status: 'Y'
      },
      userList: [],
      selectUserList: [],
      notifyUser: null,
      activeName: '1'
    }
  },
  created () {
    this.initDetail()
  },
  methods: {
    setUserRow (value, scope) {
      if (value) {
        scope.userCode = value.ceeaEmpNo
        scope.userName = value.nickname
        scope.userMobile = value.phone
      } else {
        scope.userCode = null
        scope.userName = null
        scope.userMobile = null
      }
    },
    setUserObj (value) {
      if (value) {
        this.formData.pushUserId = value.userId
        this.formData.pushUserName = value.nickname
        this.formData.pushUserCode = value.username
      } else {
        this.formData.pushUserId = null
        this.formData.pushUserName = null
        this.formData.pushUserCode = null
      }
    },
    changNotifyType () {
      this.formData.pushUserId = null
      this.formData.pushUserName = null
      this.formData.pushUserCode = null
      this.initUser()
    },
    initUser () {
      this.selectUserList = []
    },
    orgSelect (value) {
      this.initUser()
      this.userList = []
      if (value) {
        this.formData.orgId = value.organizationId
        this.formData.orgCode = value.organizationCode
        this.formData.orgName = value.organizationName
        this.getUserList()
      } else {
        this.formData.orgName = null
        this.formData.orgId = null
        this.formData.orgCode = null
      }
    },
    getUserList () {
      const saveData = {
        categoryId: null,
        duty: 'purchaser',
        enable: 'Y',
        orgIds: this.formData.orgId
      }
      this.$http({
        url: '/api-sup-ce/division/divisionCategory/listPageByParam',
        method: 'POST',
        data: saveData,
        loading: true
      }).then(res => {
        let map = new Map()
        this.userList = res.data.list.filter((item) => !map.has(item.personInChargeUserId.toString()) && map.set(item.personInChargeUserId.toString()))
      })
    },
    initDetail () {
      const { flag, row } = this.$attrs.params
      if (row) {
        this.getDetail(row.configId)
      }
    },
    getDetail (id) {
      let params = transformMQL.save('PrPushConfig', [id], 'read', {
        '*': {},
        'notifyList': { '*': { } }
      })
      this.$http({
        url: '/api-sup-ce/api-ql/PrPushConfig/read',
        method: 'POST',
        data: params,
        loading: true
      }).then(res => {
        this.formData = { ...res.data[0] }
        this.selectUserList = this.formData.notifyList
        this.formData.pushDate = this.formData.pushDate.split(',')
        this.getUserList(this.formData.orgId)
      })
    },
    handleCancel () {
      this.$emit('tab-remove', this.$attrs.tabName)
      this.__setTabTodo('AutoOrderSplitList.getQuerydata')
    },
    handleAddUser () {
      this.selectUserList.push({ userCode: null, userName: null, userMobile: null })
    },
    handleDeleteUser (i) {
      this.selectUserList.splice(i, 1)
    },
    handelByMobile (phone, i) {
      if (!validatePhone(phone)) {
        this.$message.error(`第${i + 1}行员工电话格式错误`)
      }
    },
    handleSubmit () {
      let index = 1
      let flag = true
      for (let item of this.selectUserList) {
        if (!item.userName) {
          this.$message.error(`第${index}行人员名称请填写`)
          flag = false
          return
        }
        if (!item.userCode) {
          this.$message.error(`第${index}行人员工号请填写`)
          flag = false
          return
        }
        if (!item.userMobile) {
          this.$message.error(`第${index}行人员电话请填写`)
          flag = false
          return
        }
        if (item.userMobile && !validatePhone(item.userMobile)) {
          this.$message.error(`第${index}行人员电话格式错误`)
          flag = false
          return
        }
        index++
      }
      if (!flag) {
        return
      }
      this.$refs.relForm.validate(status => {
        if (status) {
          let userInfo = this.$store.getters.userInfo
          this.formData.pushDate.sort((a, b) => {
            return a - b
          })
          if (this.formData.notifyType == '本单位采购员') {
            let newArr = this.userList.filter((item) => {
              return item.personInChargeUsername == this.formData.pushUserCode
            })
            this.formData.pushUserName = newArr.length ? newArr[0].personInChargeNickname : ''
            this.formData.pushUserId = newArr.length ? newArr[0].personInChargeUserId : ''
          }
          let param = {
            ...this.formData,
            createdBy: userInfo.userId,
            createdUserName: userInfo.nickname,
            createdByIp: userInfo.userId,
            creatorOrgName: userInfo.department,
            notifyList: [],
            pushDate: this.formData.pushDate ? this.formData.pushDate.join(',') : null
          }
          for (let item of this.selectUserList) {
            item = {
              ...item,
              configId: this.formData.configId
            }
            param.notifyList.push(item)
          }
          let params = transformMQL.save('PrPushConfig', [param], 'save')
          this.$http({
            url: '/api-sup-ce/api-ql/PrPushConfig/save',
            method: 'POST',
            data: params,
            loading: true
          }).then(res => {
            console.log(res)
            this.$emit('tab-remove', this.$attrs.tabName)
            this.__setTabTodo('AutoOrderSplitList.getQuerydata')
          })
        }
      })
    }
  }
}
</script>

<style scoped lang="scss">
.off-cursor {
  cursor: pointer;
}
.search-po {
  float: right;
}
:deep(.el-input__clear) {
  font-size: 12px;
  width: 12px;
}
:deep(.el-input__suffix) {
  height: 28px;
  padding: 0 4px;
  color: #96999c;
  line-height: 28px;
  margin: 1px 0;
  &:hover {
    color: #0077ff;
    border-color: #96999c;
    background-color: #f6f6f6;
  }
}
</style>
