<template>
  <el-container class="flex-container the_usersAccessInfo_wrapper" direction="vertical">
    <el-main>
      <div class="common-style">
        <el-form ref="form" :model="form" :rules="rules" label-position="top">
          <el-row :gutter="32">
            <el-col :span="8">
              <!-- 账号类型 -->
              <el-form-item :label="$t('dataConfMod.userType')" prop="userType">
                <DictSelect v-model="form.userType" code="USER_TYPE" :disabled="true" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <!-- 账号 -->
              <el-form-item :label="$t('dataConfMod.userID')" prop="username">
                <el-input v-model.trim="form.username" :disabled="isEdit" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <!-- 姓名 -->
              <el-form-item :label="$t('dataConfMod.userName')" prop="nickname">
                <el-input v-model.trim="form.nickname" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <!-- 手机 -->
              <el-form-item :label="$t('dataConfMod.phone')" prop="phone">
                <el-input v-model.trim="form.phone" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <!-- 邮箱 -->
              <el-form-item :label="$t('dataConfMod.email')" prop="email">
                <el-input v-model.trim="form.email" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <!-- 部门 -->
              <el-form-item :label="$t('dataConfMod.department')" prop="department">
                <QuickSearch
                  :show-input="form.department"
                  show-key="descr"
                  auto-query
                  :scope-data="form"
                  name="ceea_base_dept"
                  @close-quicksearch="getDepObj"
                />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <!-- 员工工号 -->
              <el-form-item :label="$t('dataConfMod.employeeId')">
                <el-input v-model.trim="form.ceeaEmpNo" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <!-- 公司名称 -->
              <el-form-item :label="$t('dataConfMod.ceeaCompanyName')">
                <!--                <el-input v-model.trim="form.ceeaCompany" :disabled="true" />-->
                <QuickSearch
                  :show-input="form.ceeaCompany"
                  show-key="organizationName"
                  auto-query
                  :scope-data="form"
                  name="scc_base_organization_company"
                  :disabled="form.userType != 'BUYER'"
                  @close-quicksearch="getCompany"
                />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <!-- 岗位名称 -->
              <el-form-item :label="$t('dataConfMod.positionName')">
                <QuickSearch
                  :show-input="form.ceeaJobcodeDescr"
                  show-key="descr"
                  auto-query
                  :scope-data="form"
                  name="ceea_base_position"
                  @close-quicksearch="getPosObj"
                />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <!-- 生效日期 -->
              <el-form-item :label="$t('vendorMod.startDate')" prop="startDate">
                <el-date-picker
                  v-model="form.startDate"
                  format="yyyy-MM-dd"
                  value-format="timestamp"
                  type="date"
                  :placeholder="$t('common.pleaseSelectDate')"
                />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <!-- 失效日期 -->
              <el-form-item :label="$t('dataConfMod.endDate')" prop="endDate">
                <el-date-picker
                  v-model="form.endDate"
                  format="yyyy-MM-dd"
                  value-format="timestamp"
                  type="date"
                  :placeholder="$t('common.pleaseSelectDate')"
                />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <!-- erp采购员姓名 -->
              <el-form-item :label="$t('dataConfMod.erpPurchaserName')">
                <QuickSearch
                  :show-input="form.ceeaPoAgentName"
                  show-key="nickname"
                  auto-query
                  :scope-data="form"
                  name="ceea_rbac_po_agent_info"
                  @close-quicksearch="getUserObj"
                />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>

      <el-collapse v-model="activeDims" class="tab-form-style">
        <!-- 组织权限 -->
        <el-collapse-item name="1" :title="$t('dataConfMod.orgAccess')">
          <div class="btn_line">
            <el-button
              type="primary"
              class="detail-pbtn"
              @click="showDialogOfOrg"
            >
              {{ $t("common.add") }}
            </el-button>
          </div>

          <el-table
            :ref="gridId"
            :data="organizationUsers"
            border
            max-height="500px"
            @row-click="rowClick"
          >
            <!-- 组织名称 -->
            <el-table-column
              align="center"
              prop="fullPathName"
              :label="$t('dataConfMod.orgName')"
            />
            <!-- 生效日期 -->
            <el-table-column
              align="center"
              prop="startDate"
              :label="$t('vendorMod.startDate')"
              width="160"
            >
              <template slot-scope="scope">
                <el-date-picker
                  v-if="scope.row.isEditing"
                  v-model="scope.row.startDate"
                  type="date"
                  value-format="timestamp"
                  :placeholder="$t('common.pleaseSelectDate')"
                />
                <span v-else>{{ scope.row.startDate | formatDate }}</span>
              </template>
            </el-table-column>
            <!-- 失效日期 -->
            <el-table-column
              align="center"
              prop="endDate"
              :label="$t('dataConfMod.endDate')"
              width="160"
            >
              <template slot-scope="scope">
                <el-date-picker
                  v-if="scope.row.isEditing"
                  v-model="scope.row.endDate"
                  type="date"
                  value-format="timestamp"
                  :placeholder="$t('common.pleaseSelectDate')"
                />
                <span v-else>{{ scope.row.endDate | formatDate }}</span>
              </template>
            </el-table-column>
            <!-- 操作 -->
            <el-table-column
              align="center"
              prop="operation"
              :label="$t('common.operation')"
              width="100"
              fixed="right"
            >
              <template slot-scope="scope">
                <el-button type="text" class="detail-pbtn" @click="orgDel(scope.$index)">
                  {{ $t("common.delete") }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-collapse-item>

        <!-- 角色权限 -->
        <el-collapse-item name="2" :title="$t('dataConfMod.roleAccess')">
          <div class="btn_line">
            <el-button type="primary" class="detail-pbtn" @click="addOneRole">
              {{ $t("common.add") }}
            </el-button>
          </div>
          <el-table :ref="this.gridId2" :data="roleUsers" border height="150px">
            <!-- 角色名称 -->
            <el-table-column align="center" prop="roleId" :label="$t('dataConfMod.roleName')">
              <template slot-scope="scope">
                <el-select v-model="scope.row.roleId" style="display: block;" filterable>
                  <el-option
                    v-for="item in roleList"
                    :key="item.roleId"
                    :label="item.roleName"
                    :value="item.roleId"
                    :disabled="item.disabled"
                  />
                </el-select>
              </template>
            </el-table-column>
            <!-- 生效日期 -->
            <el-table-column
              align="center"
              prop="startDate"
              :label="$t('vendorMod.startDate')"
              width="160"
            >
              <template slot-scope="scope">
                <el-date-picker
                  v-model="scope.row.startDate"
                  type="date"
                  value-format="timestamp"
                  :placeholder="$t('common.pleaseSelectDate')"
                />
              </template>
            </el-table-column>
            <!-- 失效日期 -->
            <el-table-column
              align="center"
              prop="endDate"
              :label="$t('dataConfMod.endDate')"
              width="160"
            >
              <template slot-scope="scope">
                <el-date-picker
                  v-model="scope.row.endDate"
                  type="date"
                  value-format="timestamp"
                  :placeholder="$t('common.pleaseSelectDate')"
                />
              </template>
            </el-table-column>
            <!-- 操作 -->
            <el-table-column
              align="center"
              prop="operation"
              :label="$t('common.operation')"
              fixed="right"
            >
              <template slot-scope="scope">
                <el-button type="text" @click="roleDel(scope.$index)">
                  {{
                    $t("common.delete")
                  }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-collapse-item>
      </el-collapse>

      <CToolbar>
        <template slot="right">
          <el-button @click="cancelOne">
            {{ $t("common.cancel") }}
          </el-button>
          <!-- 重置密码 -->
          <AuthorityButton
            v-if="isEdit"
            type="primary"
            code="usersAccessRole:resetPsw"
            @click="resetPwd"
          >
            {{ $t("common.resetPass") }}
          </AuthorityButton>
          <!-- 提交 -->
          <AuthorityButton
            type="primary"
            code="rbac:usersAccess:add"
            @click="insertOne"
          >
            {{ $t("common.submit") }}
          </AuthorityButton>
        </template>
      </CToolbar>
    </el-main>

    <!-- 添加组织权限 -->
    <!-- :title="isEdit() ? $t('编辑组织权限') : $t('dataConfMod.addOrgAccess')" -->
    <srm-dialog
      :visible.sync="showOrgDialog"
      :title="$t('dataConfMod.addOrgAccess')"
      size="middle"
      @close="dialogCancleHandle"
    >
      <div style="height: 300px;overflow: auto;">
        <Treeselect
          v-model="currentRows"
          :normalizer="normalizer"
          :no-children-text="$t('dataConfMod.noChildrenText')"
          :no-options-text="$t('dataConfMod.noOptionsText')"
          :no-results-text="$t('dataConfMod.noResultsText')"
          :placeholder="$t('dataConfMod.msgSelectOrgName')"
          :append-to-body="false"
          :searchable="true"
          :options="options"
          multiple
          value-consists-of="ALL_WITH_INDETERMINATE"
          value-format="object"
          :always-open="true"
          :default-expand-level="Infinity"
          auto-select-descendants
          :flatten-search-results="true"
          auto-deselect-descendants
          flat
        />
      </div>
      <div slot="footer">
        <el-button @click="onCancel">
          {{ $t("common.cancel") }}
        </el-button>
        <el-button type="primary" @click="addOneOrg">
          {{ $t("common.confirm") }}
        </el-button>
      </div>
    </srm-dialog>

    <!-- 重置密码 -->
    <srm-dialog :visible.sync="dialogFormVisible" :title="$t('common.resetPass')" size="small">
      <el-form ref="relModel" :rules="passwordRules" :model="password">
        <el-row :gutter="32">
          <el-col :span="24">
            <!-- 新密码 -->
            <el-form-item :label="$t('vendorMod.newPass')" prop="newItem">
              <el-input v-model="password.newItem" type="password" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <!-- 确认新密码 -->
            <el-form-item :label="$t('vendorMod.newPassConfirm')" prop="copyNewItem">
              <el-input v-model="password.copyNewItem" type="password" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col>
            <el-button style="float: right;" type="primary" @click="confirmResetPwd">
              {{ $t("common.submit") }}
            </el-button>
          </el-col>
        </el-row>
      </el-form>
    </srm-dialog>
  </el-container>
</template>

<script>
import QuickSearch from 'lib@/components/QuickSearch'
import CToolbar from 'lib@/components/c-toolbar'
import { tabTodoMixin } from '@/utils/mixins'
import { parseTime } from '@/utils'
import { isMobile, isEmail } from 'lib@/utils/validate'
import Treeselect from '@riophae/vue-treeselect'
import '@riophae/vue-treeselect/dist/vue-treeselect.css'
import { store } from 'lib@/components/organization-cascader/store'
import { newOrganaztionTreehttp } from '@/api/common'
import { accountApi, accessApi } from 'modb@/accountAccess/api'
import getOrgList from './calc.js'
import sha1 from 'js-sha1'

const findMenuInfoByPath = (leafId, nodes, resObj = {}) => {
  for (let i = 0; i < nodes.length; i++) {
    const tmpObj = nodes[i]
    if (leafId === nodes[i].fullPathId) {
      return tmpObj
    }
    if (nodes[i].childOrganRelation) {
      const findResult = findMenuInfoByPath(
        leafId,
        nodes[i].childOrganRelation,
        tmpObj
      )
      if (findResult) {
        return findResult
      }
    }
  }
}
//

export default {
  name: 'UsersAccessInfo',

  components: {
    CToolbar,
    Treeselect,
    QuickSearch
  },

  filters: {
    formatDate (value) {
      if (!value) return ''
      return parseTime(value, '{y}-{m}-{d}')
    }
  },

  mixins: [tabTodoMixin],

  data () {
    const validator2 = (rule, value, callback) => {
      const patrn = /[A-Za-z0-9][`~!@#$%^&*()_\-+=<>?:"{}|,.\\/;'\\[\]·~！@#￥%……&*（）——\-+={}|《》？：“”【】、；‘'，。、]/im
      if (!value) {
        callback()
      } else if (!patrn.test(value) || value.length < 8 || value.length > 24) {
        callback(
          new Error(this.$t('vendorMod.errorPass')) // "密码至少包含数字、大小写字母、特殊字符，长度为8~24位"
        )
      } else if (value !== this.password.newItem) {
        callback(this.$t('vendorMod.confirmPassError')) // "新密码与确认新密码不一致！"
      } else {
        callback()
      }
    }
    const validator1 = (rule, value, callback) => {
      const patrn = /[A-Za-z0-9][`~!@#$%^&*()_\-+=<>?:"{}|,.\\/;'\\[\]·~！@#￥%……&*（）——\-+={}|《》？：“”【】、；‘'，。、]/im
      if (!value) {
        callback()
      } else if (!patrn.test(value) || value.length < 8 || value.length > 24) {
        callback(
          new Error(this.$t('vendorMod.errorPass')) // "密码至少包含数字、大小写字母、特殊字符，长度为8~24位"
        )
      } else if (value !== this.password.copyNewItem) {
        callback(this.$t('vendorMod.confirmPassError')) // "新密码与确认新密码不一致！"
      } else {
        callback()
      }
    }
    return {
      addDisabled: false,
      password: {
        newItem: null,
        copyNewItem: null
      },
      passwordRules: {
        newItem: [
          { required: true, message: this.$t('vendorMod.msgNewPass') }, // "请填写新密码"
          { validator: validator1, trigger: 'blur' }
        ],
        copyNewItem: [
          { required: true, message: this.$t('vendorMod.msgNewPassConfirm') }, // "请填写确认新密码"
          { validator: validator2, trigger: 'blur' }
        ]
      },
      options: [],
      orgList: [],
      worker: null,
      roleList: [],
      pageSize: 15,
      gridId: 'list',
      organizationUsers: [],
      roleUsers: [],
      currentRows: [],
      gridId2: 'list2',
      form: {
        userType: 'BUYER',
        ceeaPoAgentNumber: null,
        ceeaPoAgentName: null,
        ceeaCompany: null,
        ceeaEmpNo: null,
        ceeaJobcodeDescr: null,
        startDate: new Date().getTime(),
        ceeaDeptId: null,
        department: null
      },
      rules: {
        username: [{ required: true, message: this.$t('dataConfMod.msgUser') }], // "请输入账号"
        nickname: [
          { required: true, message: this.$t('dataConfMod.msgUserName') }
        ], // "请输入姓名"
        email: [
          { required: true, message: this.$t('dataConfMod.msgMail') }, // "请输入邮箱"
          {
            validator: (rule, value, callback) => {
              if (!value) {
                callback(new Error(this.$t('dataConfMod.msgMail'))) // "请输入邮箱"
              } else if (!isEmail(value)) {
                callback(new Error(this.$t('dataConfMod.msgIllegalMail'))) // "邮箱格式不合法"
              } else {
                callback()
              }
            },
            trigger: 'blur'
          }
        ],
        phone: [
          {
            validator: (rule, value, callback) => {
              if (!isMobile(value) && value) {
                callback(new Error(this.$t('dataConfMod.msgIllegalPhone'))) // "手机格式不合法"
              } else {
                callback()
              }
            },
            trigger: 'blur'
          }
        ]
      },
      newPassword: null,
      newPassword2: null,
      dialogFormVisible: false,
      showOrgDialog: false,
      formLabelWidth: '100px',
      isActive: false,
      preArr: [
        { prop: 'username', label: () => this.$t('dataConfMod.userID') }, // "账号"
        { prop: 'nickname', label: () => this.$t('dataConfMod.userName') }, // "姓名"
        {
          prop: 'startDate',
          label: () => this.$t('dataConfMod.startDate'),
          type: 'date'
        } // "生效日期"
      ],
      activeDims: ['1', '2']
    }
  },

  computed: {
    isEdit () {
      const { flag } = this.$attrs.params
      return flag === 'edit'
    }
  },

  created () {
    let isAdd = this.$attrs.params.flag
    if (isAdd === 'add') {
      this.addDisabled = true
    }
  },

  async mounted () {
    const { data } = await newOrganaztionTreehttp({})

    this.options = data

    if (this.isEdit) {
      // 获取用户信息
      const { row } = this.$attrs.params
      const { userId } = row
      accountApi.getByBuyer({ id: userId })
        .then(res => {
          const { user, organizationUsers, roleUsers } = res.data
          // 时间都要转换为时间戳
          const { startDate, endDate, ...rest } = user
          const startDateTimestamp = startDate
            ? new Date(startDate).getTime()
            : ''
          const endDateTimestamp = endDate ? new Date(endDate).getTime() : ''
          this.form = {
            ...rest,
            startDate: startDateTimestamp,
            endDate: endDateTimestamp
          }

          const _list = getOrgList(
            this.transfromDate(organizationUsers),
            data,
            store.fullPathNameMap || new Map()
          )
          this.organizationUsers = _list.orgList.map(item => {
            return {
              ...item,
              isEditing: false
            }
          })
          store.fullPathNameMap = _list.fullPathNameMap
          this.roleUsers = this.transfromDate(roleUsers)
        })
        .catch(error => console.log(error))
    } else {
      this.initFormData()
    }
    // FIXME: 没有全量返回的接口，hardcode pageSize 999
    let roleRes = await accessApi.roleListHttp({ pageSize: 999, pageNum: 1 })
    // accessApi.roleListHttp({ pageSize: 999, pageNum: 1 }).then(res => {
    console.log('roleRes')
    console.log(roleRes.data.list)
    let roleData = roleRes.data.list || []
    this.roleList = []
    roleData.forEach(item => {
      let itemDisabled = false
      if (item.endDate) {
        // 当前时间在生效时间之后
        const nowIsAfterStartDate = this.$dayjs().isAfter(
          this.$dayjs(item.startDate)
        )
        // 当前时间在失效时间之前
        const nowIsBeforeEndDate = this.$dayjs().isBefore(
          this.$dayjs(item.endDate)
        )
        if (nowIsAfterStartDate && nowIsBeforeEndDate) {
          itemDisabled = false
        } else {
          itemDisabled = true
        }
      }
      this.roleList.push({
        ...item,
        disabled: itemDisabled
      })
    })

    // .filter(i => {
    //   // 当前时间大于生效时间
    //   const nowIsAfterStartDate = this.$dayjs().isAfter(
    //     this.$dayjs(i.startDate)
    //   )
    //   if (!i.endDate) {
    //     return nowIsAfterStartDate
    //   }
    //   // 当前时间在失效时间之前
    //   const nowIsBeforeEndDate = this.$dayjs().isBefore(
    //     this.$dayjs(i.endDate)
    //   )
    //   // 有失效时间时返回没有失效的角色
    //   return nowIsAfterStartDate && nowIsBeforeEndDate
    // })
    // })
  },

  methods: {
    rowClick (row, column, event) {
      row.isEditing = true
    },

    dialogCancleHandle () {
      this.currentRows = this.organizationUsers
        .map(item => {
          return {
            ...findMenuInfoByPath(item.fullPathId, this.options),
            startDate: item.startDate
          }
        })
        .filter(i => !!i)
    },

    /* 新增组织权限 */
    showDialogOfOrg () {
      this.showOrgDialog = true
      this.dialogCancleHandle()
    },

    onCancel () {
      this.showOrgDialog = false
    },

    resetPwd () {
      // this.dialogFormVisible = true // 产品的重置是弹框
      // iam重置
      this.iamResetPwd()
    },
    iamResetPwd () {
      this.$confirm(this.$t('dataConfMod.iamResetPswTip'), this.$t('common.tips'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning',
        closeOnClickModal: false,
        showClose: false
      }).then(() => {
        accountApi.resetUserPwByManageIam({
          username: this.form.username
        }).then(res => {
          this.$message({
            message: res.message,
            type: 'success'
          })
        })
      }).catch(() => {
        this.$message({
          message: this.$t('dataConfMod.cancelReset'), // 已取消重置密码
          type: 'warn'
        })
      })
    },

    confirmResetPwd () {
      const { newItem, copyNewItem } = this.password
      if (newItem !== copyNewItem) {
        return this.$message.error(this.$t('vendorMod.confirmPassError')) // 两次输入密码不一致
      }
      const { email, userId } = this.form
      this.$refs.relModel.validate(status => {
        if (status) {
          let passSha1 = sha1(newItem)
          accountApi.resetBuyerPw({
            user: { email, userId, password: passSha1 }
          }).then(res => {
            this.$message({
              message: res.message,
              type: 'success'
            })
            this.dialogFormVisible = false
          })
        }
      })
    },

    normalizer (node) {
      const NODE = {
        id: node.fullPathId,
        label: node.organizationName
      }
      if (node.childOrganRelation && node.childOrganRelation.length) { NODE.children = node.childOrganRelation }
      return NODE
    },

    transfromDate (target) {
      if (Array.isArray(target) && target.length) {
        return target.map(item => ({
          ...item,
          startDate: this.date2timestamp(item.startDate),
          endDate: this.date2timestamp(item.endDate)
        }))
      }
      return target
    },

    date2timestamp (time) {
      return time ? new Date(time).getTime() : ''
    },

    initFormData () {
      this.form = {
        userType: 'BUYER',
        startDate: new Date().getTime()
      }
      this.roleUsers = []
      this.organizationUsers = []
    },

    getDepObj (val, scope) {
      scope.ceeaDeptId = val ? val.deptid : ''
      scope.department = val ? val.descr : ''
    },

    getPosObj (val, scope) {
      scope.ceeaJobcode = val ? val.positionNbr : ''
      scope.ceeaJobcodeDescr = val ? val.descr : ''
    },

    getCompany (val, scope) {
      scope.ceeaCompany = val ? val.organizationName : ''
      scope.companyId = val ? val.organizationId : ''
    },

    // 选择组织
    addOrgHandle (e, scope) {
      const { $index } = scope
      this.organizationUsers = this.organizationUsers.map((item, index) => {
        if (index === $index) return { ...item, organizationId: e.relId || '' }
        return item
      })
    },

    /* 添加组织权限 */
    addOneOrg () {
      const newList = this.currentRows.map(item => {
        return {
          ...item,
          isEditing: false,
          startDate: item.startDate ? item.startDate : new Date().getTime()
        }
      })
      const _list = getOrgList(
        newList,
        this.options,
        store.fullPathNameMap || new Map()
      )
      this.organizationUsers = _list.orgList
      store.fullPathNameMap = _list.fullPathNameMap
      this.showOrgDialog = false
    },

    orgDel (index) {
      this.organizationUsers.splice(index, 1)
    },

    addOneRole () {
      this.roleUsers.push({ startDate: new Date().getTime() })
    },

    roleDel (index) {
      this.roleUsers.splice(index, 1)
    },

    insertOne () {
      // 验证form表单
      this.$refs.form.validate(valid => {
        if (valid) {
          // 默认userType为BUYER
          const { startDate, endDate, ...rest } = this.form
          const params = {
            user: {
              ...rest,
              // userType: 'BUYER',
              startDate: this.date2timestamp(startDate),
              endDate: this.date2timestamp(endDate)
            },
            organizationUsers: this.transfromDate(this.organizationUsers).map(
              ({ organizationName, ...rest }) => ({ ...rest })
            ),
            roleUsers: this.transfromDate(this.roleUsers)
          }
          if (this.isEdit) {
            accountApi.modifyBuyer(params)
              .then(res => {
                this.$message({
                  message: res.message,
                  type: 'success'
                })
                this.cancelOne()
              })
              .catch(res => console.log(res))
          } else {
            accountApi.addBuyer(params)
              .then(res => {
                this.$message({
                  message: res.message,
                  type: 'success'
                })
                this.cancelOne()
              })
              .catch(res => console.log(res))
          }
        } else {
          return false
        }
      })
    },

    getUserObj (val, scope) {
      if (val) {
        this.$set(this.form, 'ceeaPoAgentName', val.agentName || '')
        this.$set(this.form, 'ceeaPoAgentNumber', val.agentNumber || '')
      } else {
        this.$set(this.form, 'ceeaPoAgentName', '')
        this.$set(this.form, 'ceeaPoAgentNumber', '')
      }
    },

    cancelOne () {
      if (this.$attrs.params.flag === 'edit') {
        this.$emit('tab-remove', 'usersAccessInfo' + this.form.username)
      } else {
        this.$emit('tab-remove', 'usersAccessInfo')
      }
      this.__setTabTodo('usersAccessList.getQuerydata')
    }
  }
}
</script>

<style>
.vue-treeselect__label {
  font-size: 13px;
  font-weight: 400;
}
</style>
<style scoped lang="scss">
.the_usersAccessInfo_wrapper {
  .common-style {
    // padding: 15px;
    // border: 1px solid #dfe6ec;
    // border-bottom: 0;
    &:first-child {
      // border-top: 0;
    }
    &.last-div {
      padding-bottom: 65px;
    }
  }
}
.btn_line {
  margin-bottom: 10px;
}
</style>
