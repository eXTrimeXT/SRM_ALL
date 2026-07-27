<template>
  <el-container class="flex-container-notab the_functionMaintenance_wrapper" direction="vertical">
    <el-main>
      <FormWrapper :form-array="preArr" @getFormData="getQuerydata" />
      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <AuthorityButton
            v-if="$store.getters.userInfo.mainType === 'Y'"
            type="primary"
            code="rbac:user:add"
            @click="addOne"
          >
            {{ $t("common.add") }}
          </AuthorityButton>
          <!-- 导入子账号 todo-后端完成以后再修改接口 -->
          <!-- <MImport
            v-if="userType=='VENDOR'"
            ref="import1"
            :title="$t('common.import')"
            up-load-url="/api-rbac/user/import/importSubUserExcel"
            :extra-data="extraData"
            code=""
            type="default"
            @downloadTemplate="downloadTemplate"
            @handleSuccess="handleSuccess"
          /> -->
        </template>
      </MainHeader>

      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :pre-query-data="queryParam"
        :page-size="pageSize"
        :auto-query="false"
        :comActive="$attrs['changeTab']"
        url="/api-rbac/user/listByVendor"
        align="center"
      />
      <!-- 弹框区域-->
      <srm-dialog
        :title="dialogTitle"
        size="large"
        :visible.sync="dialogFormVisible"
        :close-on-click-modal="false"
      >
        <el-form ref="form" :model="form" :rules="rules">
          <el-row :gutter="32">
            <el-col :span="8">
              <!-- 手机 -->
              <el-form-item :label="$t('dataConfMod.phone')" prop="phone">
                <el-input v-model="form.phone" :disabled="curOpt === 'edit' && oldMainType === 'Y'" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <!-- 账号 -->
              <el-form-item :label="$t('dataConfMod.userID')" prop="username">
                <el-input v-model="form.username" disabled />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <!-- 姓名 -->
              <el-form-item :label="$t('dataConfMod.userName')" prop="nickname">
                <el-input v-model="form.nickname" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <!-- 邮箱 -->
              <el-form-item :label="$t('dataConfMod.email')" prop="email">
                <el-input v-model="form.email" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <!-- 部门 -->
              <el-form-item :label="$t('dataConfMod.department')" prop="department">
                <QuickSearch
                  :show-input="form.department"
                  show-key="descr"
                  :scope-data="form"
                  name="ceea_base_dept"
                  @close-quicksearch="getDepObj"
                />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <!-- 账号类型 -->
              <el-form-item :label="$t('dataConfMod.userType')" prop="userType">
                <DictSelect v-model="form.userType" code="USER_TYPE" disabled />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <!-- 公司名称 -->
              <el-form-item :label="$t('dataConfMod.ceeaCompanyName')">
                <el-input v-model.trim="form.ceeaCompany" disabled />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <!-- 生效日期 -->
              <el-form-item :label="$t('vendorMod.startDate')" prop="startDate">
                <el-date-picker
                  v-model="form.startDate"
                  :default-value="new Date().getTime()"
                  type="date"
                  :format="$formatDatePicker"
                  value-format="timestamp"
                  :placeholder="$t('common.pleaseSelectDate')"
                />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <!-- 失效日期 -->
              <el-form-item :label="$t('dataConfMod.endDate')" prop="endDate">
                <el-date-picker
                  v-model="form.endDate"
                  type="date"
                  :format="$formatDatePicker"
                  value-format="timestamp"
                  :placeholder="$t('common.pleaseSelectDate')"
                />
              </el-form-item>
            </el-col>
            <el-col v-if="oldMainType === 'N'" :span="8">
              <!-- 是否主账号 -->
              <el-form-item :label="$t('cusEntry.accountAccessSupplier.isMainAccount')">
                <DictSelect
                  v-model="form.mainType"
                  code="YES_OR_NO"
                />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="dialogFormVisible = false">
            {{ $t("common.cancel") }}
          </el-button>
          <el-button type="primary" @click="saveData">
            {{ $t("common.confirm") }}
          </el-button>
        </div>
      </srm-dialog>
      <!-- 重置密码 -->
      <srm-dialog :visible.sync="dialogFormVisible2" :title="$t('common.resetPass')" size="small">
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
    </el-main>
  </el-container>
</template>
<script>
import QuickSearch from 'lib@/components/QuickSearch' // 快速查询组件
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { parseTime } from '@/utils'
import sha1 from 'js-sha1'
import { isMobile, isEmail } from 'lib@/utils/validate'
import MImport from 'lib@/components/import'
import { downloadFileLink } from 'lib@/utils/file'
import { accountAccessApi, accountApi, accessApi } from 'modcs@/accountAccessSupplier/api'
export default {
  name: 'AccountManagement',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    QuickSearch,
    MImport
  },
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
      userType: this.$store.getters.userType,
      extraData: {
        fileModular: 'base',
        fileFunction: 'accountManagement',
        fileType: 'excel'
      },
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
      gridId: 'account',
      curOpt: 'add',
      pageSize: 15,
      dialogTitle: '',
      tableData: [],
      tableHeader: [],
      queryParam: {},
      roleUsers: [],
      preArr: [
        { prop: 'username', label: () => this.$t('dataConfMod.userID') }, // "账号"
        { prop: 'nickname', label: () => this.$t('dataConfMod.userName') }, // "姓名"
        {
          prop: 'startDate',
          label: () => this.$t('dataConfMod.startDate'),
          type: 'date'
        } // "生效日期"
      ],
      form: {
        username: ''
      },
      rules: {
        phone: [
          { required: true, message: this.$t('cusEntry.tipMessage.phoneMsg') },
          {
            validator: (rule, value, callback) => {
              if (!value) {
                this.form.username = ''
                callback()
              } else if (!isMobile(value)) {
                this.form.username = ''
                callback(new Error(this.$t('dataConfMod.msgIllegalPhone'))) // "手机格式不合法"
              } else {
                this.form.username = value
                callback()
              }
            },
            trigger: 'blur'
          }
        ],
        username: [{ required: true, message: this.$t('dataConfMod.msgUser') }], // "请输入账号"
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
        ]
      },
      dialogFormVisible: false,
      dialogFormVisible2: false,
      globalUserid: null,
      oldMainType: ''
    }
  },
  computed: {
    isEdit () {
      return this.curOpt === 'edit'
    }
  },
  created () {
    console.log(this.$store.getters.userInfo)
    var _this = this
    this.tableHeader = [
      {
        prop: 'username',
        label: () => this.$t('dataConfMod.userID'),
        width: 150,
        align: 'center'
      }, // "账号"
      {
        prop: 'nickname',
        label: () => this.$t('dataConfMod.userName'),
        width: 150,
        align: 'center'
      }, // "姓名"
      {
        prop: 'phone',
        label: () => this.$t('dataConfMod.phone'),
        width: 150,
        align: 'center'
      }, // "手机"
      {
        prop: 'email',
        label: () => this.$t('dataConfMod.email'),
        align: 'center'
      }, // "邮箱"
      {
        prop: 'department',
        label: () => this.$t('dataConfMod.department'),
        width: 150,
        align: 'center'
      }, // "部门"
      {
        prop: 'userType',
        align: 'center',
        width: 130,
        label: () => this.$t('dataConfMod.userType'), // "账号类型"
        dataType: 'dict',
        code: 'USER_TYPE'

      },
      {
        prop: 'startDate',
        label: () => this.$t('vendorMod.startDate'), // "生效日期"
        align: 'center',
        minWidth: 120,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'endDate',
        label: () => this.$t('dataConfMod.endDate'), // "失效日期"
        align: 'center',
        width: 100,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'operation',
        label: () => this.$t('common.operation'), // "操作"
        width: 120,
        fixed: 'right',
        showType: 'buttons',
        btnStyle: 'text',
        align: 'center',
        buttons: [
          {
            callback: function (row) {
              this.currentRow = row
              this.editTab(row)
            }.bind(this),
            code: 'rbac:user:edit',
            formattor (val) {
              return _this.$t('common.edit') // "编辑"
            },
            show: row => _this.$store.getters.userInfo.mainType === 'Y'
          },
          {
            callback: row => {
              this.deleteRow(row)
            },
            code: 'rbac:user:delete',
            formattor: val => this.$t('common.delete'),
            show: row => _this.$store.getters.userInfo.mainType === 'Y' && row.mainType === 'N'
          }
          // { // iam 登录密码重置还没同步，先注释
          //   callback: function (row) {
          //     this.resumbit(row)
          //   }.bind(this),
          //   code: 'rbac:user:resetPassword',
          //   formattor: () => _this.$t('common.resetPass') // 重置密码
          // }
        ]
      }
    ]
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  mounted () {
    // FIXME: 没有全量返回的接口，hardcode pageSize 999
    accessApi.roleListHttp({ pageSize: 999, pageNum: 1 }).then(res => {
      this.roleList = res.data.list.filter(i => {
        const nowIsAfterStartDate = this.$dayjs().isAfter(
          this.$dayjs(i.startDate)
        )
        if (!i.endDate) {
          return nowIsAfterStartDate
        }
        const nowIsBeforeEndDate = this.$dayjs().isBefore(
          this.$dayjs(i.endDate)
        )
        return nowIsAfterStartDate && nowIsBeforeEndDate
      })
    })
  },
  methods: {
    /* 删除子账号 */
    deleteRow (row) {
      accountAccessApi.deleteRow(row.userId).then(res => {
        this.$message.success(this.$t('common.successDelete'))
        this.getQuerydata(this.queryParam)
      })
    },
    /* 手机号变更, 为账号赋值 */
    phoneChange (value) {
      this.form.username = value
    },
    downloadTemplate () {
      // 子账号用户信息导入模板.xlsx
      downloadFileLink(
        '/api-rbac/user/import/importSubUserModelDownload',
        this.$t('dataConfMod.accountManagementImpTemXLSX')
      ).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail')) // 下载失败
      })
    },
    handleSuccess () {
      this.getQuerydata()
    },
    // 重置密码
    resumbit (row) {
      this.globalUserid = row.userId
      this.dialogFormVisible2 = true
    },
    confirmResetPwd () {
      const { newItem, copyNewItem } = this.password
      if (newItem !== copyNewItem) {
        return this.$message.error(this.$t('vendorMod.confirmPassError')) // 两次输入密码不一致!
      }
      const { email, userId } = this.form
      this.$refs.relModel.validate(status => {
        if (status) {
          let passSha1 = sha1(newItem)
          accountApi.resetUserPwByManage({ userId: this.globalUserid, password: passSha1 }).then(res => {
            this.dialogFormVisible2 = false
            this.$message({
              type: 'scuccess',
              message: this.$t('vendorMod.resetSuccess')
            }) // 重置成功
          })
        }
      })
    },
    bankeDel (index) {
      this.roleUsers.splice(index, 1)
    },
    initFormData () {
      const initFrom = {
        startDate: new Date().getTime(),
        userType: 'VENDOR'
      }
      this.form = { ...initFrom, ...this.form }
      this.roleUsers = []
    },
    getDepObj (val, scope) {
      scope.deptId = val ? val.deptId : ''
      scope.department = val ? val.descr : ''
    },
    getPosObj (val, scope) {
      scope.ceeaJobcode = val ? val.positionNbr : ''
      scope.ceeaJobcodeDescr = val ? val.descr : ''
    },
    addOne () {
      /* 获取目前子账号数 */
      const childAccount = this.$refs[this.gridId].tableData
      if (childAccount.length > 2) {
        this.$message.warning(this.$t('cusEntry.tipMessage.overMaxAccount'))
        return false
      }
      this.initFormData()
      this.form.ceeaCompany = this.$store.getters.userInfo.companyName
      this.form.companyId = this.$store.getters.userInfo.companyId
      this.dialogTitle = this.$t('dataConfMod.addUser') // "新增用户"
      this.curOpt = 'add'
      this.dialogFormVisible = true
    },
    editTab (row) {
      this.oldMainType = row.mainType
      this.curOpt = 'edit'
      this.dialogTitle = this.$t('dataConfMod.editUser') // "编辑用户"
      this.dialogFormVisible = true
      accountApi.getByVendor({ id: row.userId }).then(res => {
        const { user, roleUsers } = res.data
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
        this.roleUsers = this.transfromDate(roleUsers)
      })
    },
    getQuerydata (v) {
      this.queryParam = v
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    transfromDate (target) {
      if (Array.isArray(target) && target.length) {
        return target.map(item => ({
          ...item,
          startDate: this.date2timestamp(item.startDate),
          endDate: item.endDate ? this.date2timestamp(item.endDate) : null
        }))
      }
      return target
    },
    date2timestamp (time) {
      return new Date(time).getTime()
    },
    // 保存数据
    saveData () {
      this.$refs.form.validate(valid => {
        if (valid) {
          const { startDate, endDate } = this.form
          console.log(endDate, 'endDate')
          const submitData = {
            roleUsers: this.transfromDate(this.roleUsers),
            user: {
              ...this.form,
              startDate: this.date2timestamp(startDate),
              endDate: endDate ? this.date2timestamp(endDate) : null
            }
          }
          if (!this.isEdit) {
            // 新增
            accountApi.addVendor(submitData).then(async res => {
              this.$message({
                message: this.$t('cusEntry.tipMessage.passwordSendTextMessage'),
                type: 'success'
              })
              if (this.form.mainType === 'Y') {
                await this.$store.dispatch('user/initSystem')
              }
              this.getQuerydata()
              this.dialogFormVisible = false
            })
          } else {
            // 编辑
            accountApi.modifyVendor(submitData).then(async res => {
              this.$message({
                message: res.message,
                type: 'success'
              })
              /* 当前用户账号 */
              const {
                username,
                mainType
              } = this.$store.getters.userInfo
              if (this.form.mainType === 'Y') {
                await this.$store.dispatch('user/initSystem')
              }
              this.getQuerydata()
              this.dialogFormVisible = false
            })
          }
        } else {
          return false
        }
      })
    },
    addOneRole () {
      this.roleUsers.push({ startDate: new Date().getTime() })
    }
  }
}
</script>
<style scoped lang="scss">
.the_functionMaintenance_wrapper {
}
</style>
