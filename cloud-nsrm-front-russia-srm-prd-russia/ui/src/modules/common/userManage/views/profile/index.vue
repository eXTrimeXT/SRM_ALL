<template>
  <el-tabs
    v-model="activeName"
    class="porfileWap"
    @tab-click="handleClick"
  >
    <!-- 个人信息 -->
    <el-tab-pane
      :label="$t('vendorMod.userInfo')"
      name="first"
    >
      <el-form
        ref="userInfo"
        :rules="rules"
        :model="userInfo"
      >
        <el-row :gutter="32">
          <el-col :span="8">
            <!-- 账号 -->
            <el-form-item
              :label="$t('vendorMod.username')"
              prop="username"
            >
              <el-input
                v-model="userInfo.username"
                disabled
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <!-- 姓名 -->
            <el-form-item
              :label="$t('vendorMod.nickname')"
              prop="nickname"
            >
              <el-input
                v-model="userInfo.nickname"
                :disabled="userInfo.userType === 'BUYER'"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <!-- 手机 -->
            <el-form-item
              :label="$t('vendorMod.phone')"
              prop="phone"
            >
              <el-input
                v-model="userInfo.phone"
                :disabled="userInfo.userType === 'BUYER'"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <!-- 邮箱 -->
            <el-form-item
              :label="$t('vendorMod.email')"
              prop="email"
            >
              <el-input
                v-model="userInfo.email"
                :disabled="userInfo.userType === 'BUYER'"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <!-- 部门 -->
            <el-form-item
              :label="$t('vendorMod.department')"
              prop="department"
            >
              <el-input
                v-model="userInfo.department"
                :disabled="userInfo.userType === 'BUYER'"
              />
            </el-form-item>
          </el-col>
          <el-col
            v-if="userInfo.userType === 'BUYER'"
            :span="8"
          >
            <!-- 角色权限 -->
            <el-form-item
              :label="$t('vendorMod.rolePermissions')"
              prop="rolePermissions"
            >
              <el-input
                v-model="userInfo.rolePermissions"
                disabled
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row
          v-if="userInfo.userType === 'BUYER'"
          style="padding: 0 24px;margin-bottom: 10px;"
          :gutter="32"
        >
          <el-collapse v-model="collapseActiveName">
            <!-- 组织权限 -->
            <el-collapse-item
              :title="$t('vendorMod.orgPermissions')"
              name="organization"
            >
              <div
                v-for="item in userInfo.organizationUsers"
                :key="item.organizationId"
              >
                {{ item.organizationName }}
              </div>
            </el-collapse-item>
          </el-collapse>
        </el-row>
        <el-row>
          <el-col>
            <el-button
              v-if="!isDemoEnv && userInfo.userType === 'VENDOR'"
              :loading="loading"
              type="primary"
              @click="modifyUserClick"
            >
              {{ $t('common.save') }}
            </el-button>
          </el-col>
        </el-row>
      </el-form>
    </el-tab-pane>
    <!-- 账号安全  v-if="userInfo.userType === 'VENDOR'" FIXME 暂时屏蔽-->
    <el-tab-pane
      v-if="false"
      :label="$t('vendorMod.userSafe')"
      name="second"
    >
      <el-form
        ref="relModel"
        :rules="passwordRules"
        :model="password"
      >
        <el-row :gutter="32">
          <el-col :span="8">
            <!-- 旧密码 -->
            <el-form-item
              :label="$t('vendorMod.oldPass')"
              prop="oldItem"
            >
              <el-input
                v-model="password.oldItem"
                type="password"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <!-- 新密码 -->
            <el-form-item
              :label="$t('vendorMod.newPass')"
              prop="newItem"
            >
              <el-input
                v-model="password.newItem"
                type="password"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <!-- 确认新密码 -->
            <el-form-item
              :label="$t('vendorMod.newPassConfirm')"
              prop="copyNewItem"
            >
              <el-input
                v-model="password.copyNewItem"
                type="password"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col>
            <el-button
              v-if="!isDemoEnv"
              type="primary"
              :loading="loading"
              @click="modifyUserPassword"
            >
              {{ $t('common.save') }}
            </el-button>
          </el-col>
        </el-row>
      </el-form>
      <div style="margin-top:20px;" />
      <el-form :model="faceFile">
        <el-row :gutter="32">
          <el-col :span="8">
            <!-- 人脸正面 -->
            <el-form-item :label="$t('vendorMod.verifyFaceEnable')" />
          </el-col>
        </el-row>
        <el-row>
          <el-col>
            <el-button
              type="primary"
              @click="recordFaceClick"
            >
              {{ $t('vendorMod.recordFace') }}
            </el-button>
          </el-col>
        </el-row>
      </el-form>
    </el-tab-pane>
    <srm-dialog
      :title="$t('vendorMod.recordFace')"
      :visible.sync="showRecordFaceDialog"
    >
      <RecordFaceDialog @visible="recordFaceDialogVisibleChange" />
    </srm-dialog>
  </el-tabs>
</template>
<script>
import sha1 from 'js-sha1'
import { mapState } from 'vuex'
import * as path from '@/utils/path'
import RecordFaceDialog from './components/RecordFaceDialog'
import { userOptApi } from 'mod@/common/userManage/api'
import { isSinglePoint, singlePointLogoutUrl } from '@/config/sysConfig'

export default {
  name: 'Profile',
  components: { RecordFaceDialog },
  data () {
    const validator = (rule, value, callback) => {
      // eslint-disable-next-line no-useless-escape
      const patrn = /[A-Za-z0-9][`~!@#$%^&*()_\-+=<>?:"{}|,.\/;'\\[\]·~！@#￥%……&*（）——\-+={}|《》？：“”【】、；‘'，。、]/im
      if (!value) {
        callback()
      } else if (!patrn.test(value) || value.length < 8 || value.length > 24) {
        callback(
          new Error(this.$t('vendorMod.errorPass'))// "密码至少包含数字、大小写字母、特殊字符，长度为8~24位"
        )
      } else if (value !== this.password.newItem) {
        callback(this.$t('vendorMod.confirmPassError'))// "新密码与确认新密码不一致！"
      } else {
        callback()
      }
    }
    const validator1 = (rule, value, callback) => {
      // eslint-disable-next-line no-useless-escape
      const patrn = /[A-Za-z0-9][`~!@#$%^&*()_\-+=<>?:"{}|,.\/;'\\[\]·~！@#￥%……&*（）——\-+={}|《》？：“”【】、；‘'，。、]/im
      if (!value) {
        callback()
      } else if (!patrn.test(value) || value.length < 8 || value.length > 24) {
        callback(
          new Error(this.$t('vendorMod.errorPass'))// "密码至少包含数字、大小写字母、特殊字符，长度为8~24位"
        )
      } else if (value !== this.password.copyNewItem) {
        callback(this.$t('vendorMod.confirmPassError'))// "新密码与确认新密码不一致！"
      } else {
        callback()
      }
    }
    return {
      isDemoEnv: false, // 是否在demo环境下面
      activeName: 'first',
      collapseActiveName: 'organization',
      loading: false,
      loadingFace: false,
      showRecordFaceDialog: false,
      password: {
        oldItem: null,
        newItem: null,
        copyNewItem: null
      },
      rules: {
        nickname: [{ required: true, message: this.$t('vendorMod.msgNickname') }], // "请填写姓名"
        email: [{ required: true, message: this.$t('vendorMod.msgEmail') }]// "请填写邮箱"
      },
      passwordRules: {
        oldItem: [{ required: true, message: this.$t('vendorMod.msgOldPass') }], // "请填旧密码"
        newItem: [
          { required: true, message: this.$t('vendorMod.msgNewPass') }, // "请填写新密码"
          { validator: validator1, trigger: 'blur' }
        ],
        copyNewItem: [
          { required: true, message: this.$t('vendorMod.msgNewPassConfirm') }, // "请填写确认新密码"
          { validator, trigger: 'blur' }
        ]
      },
      faceFile: {
        faceFileId: '',
        faceFileName: ''
      },
      userInfo: {}
    }
  },
  computed: {
    ...mapState({
      entrance: (state) => state.user.entrance
    })
  },
  created () {
    // 私有云下面可去掉
    // 判断demo环境下面试用用户赋值 [[[
    let hostName = window.location.hostname
    if (hostName === 'demo.srm.meicloud.com') {
      this.isDemoEnv = true
    }
    const userInfo = this.$store.getters.userInfo || {}
    this.userInfo = {
      ...userInfo,
      rolePermissions: (userInfo.rolePermissions || []).map(i => i.roleName).join(',')
    }
    /// 判断demo环境下面试用用户赋值]]]
  },
  methods: {
    modifyUserClick () {
      const { userId, nickname, phone, email, department, username } = this.userInfo
      this.$refs.userInfo.validate(status => {
        if (status) {
          this.loading = true
          userOptApi.modifyUser({ userId, nickname, phone, email, department, username })
            .then(res => {
              this.$message({ type: 'success', message: res.message })
              this.loading = false
              this.$store.dispatch('user/initSystem')
            })
            .catch(() => (this.loading = false))
        }
      })
    },
    modifyUserPassword () {
      const { userId } = this.userInfo
      const { newItem, oldItem, copyNewItem } = this.password
      let sha1OldItem = sha1(oldItem)
      let sha1CopyNewItem = sha1(copyNewItem)
      this.$refs.relModel.validate(status => {
        if (status) {
          this.loading = true
          userOptApi.checkOldPassword({
            password: sha1OldItem, // oldItem
            userId
          })
            .then(res => {
              userOptApi.modifyPassword({
                userId,
                password: sha1CopyNewItem // copyNewItem
              }).then(res => {
                this.loading = false
                this.$message({
                  type: 'success',
                  message: this.$t('vendorMod.successResetPass')// "密码重置成功，请重新登录！"
                })
                this.logout()
              })
            })
            .catch(res => {
              this.loading = false
            })
        }
      })
    },
    // 内部退出
    innerLogout () {
      this.$store.dispatch('user/getLogout').then(res => {
        if (res) {
          this.$router.push({ path: path.resolve('/login') })
        }
      })
    },
    // 单点登录退出
    singlePointLogout () {
      this.$store.dispatch('user/resetToken').then(() => {
        window.location.href = singlePointLogoutUrl() // 调用单点登录的退出接口
      })
    },
    /* 退出登录 */
    logout () {
      if (isSinglePoint === 'N') { // 内部页面登录后退出
        this.innerLogout()
      } else { // 单点登录退出 保留原来登录方式 退出的方式从哪里进来就回到哪里
        if (this.entrance === 'inside') {
          this.innerLogout()
        } else {
          this.singlePointLogout()
        }
      }
    },
    handleClick (tab, event) {
      console.log(tab, event)
    },
    // 上传成功
    handleUploadSuccess (file, scope) {
      const { id, name } = file
      scope.faceFileId = id.toString()
      scope.faceFileName = name
    },
    modifyFaceClick () {
      this.$refs.relModel.validate(status => {
        if (status) {
          this.loadingFace = true
          userOptApi.modifyFace({
            userId: this.userInfo.userId,
            faceFileId: this.faceFile.faceFileId
          })
            .then(res => {
              this.loadingFace = false
              this.$t('vendorMod.successUpload')// "上传成功！"
            })
            .catch(res => {
              this.loadingFace = false
            })
        }
      })
    },
    recordFaceDialogVisibleChange (visible) {
      this.showRecordFaceDialog = visible
    },
    recordFaceClick () {
      this.showRecordFaceDialog = true
    }

  }
}
</script>
<style lang="scss" scoped>
.porfileWap {
  padding: 10px;
  .el-tab-pane {
    padding: 20px 40px;
  }
}
</style>
