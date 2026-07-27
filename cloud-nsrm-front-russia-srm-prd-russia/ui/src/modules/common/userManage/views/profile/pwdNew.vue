<template>
  <el-tabs
    v-model="activeName"
    class="porfileWap"
  >
    <el-tab-pane
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
              type="primary"
              :loading="loading"
              @click="modifyUserPassword"
            >
              {{ $t('common.save') }}
            </el-button>
          </el-col>
        </el-row>
      </el-form>
    </el-tab-pane>
  </el-tabs>
</template>

<script>
import sha1 from 'js-sha1'
import * as path from '@/utils/path'
import http from '@/utils/axios/http'
import { mapState } from 'vuex'
import { isSinglePoint, singlePointLogoutUrl } from '@/config/sysConfig'

export default {
  name: 'Profile',
  data () {
    const validator = (rule, value, callback) => {
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
      const patrn = /[A-Za-z0-9][`~!@#$%^&*()_\-+=<>?:"{}|,.\/;'\\[\]·~！@#￥%……&*（）——\-+={}|《》？：“”【】、；‘'，。、]/im
      if (!value) {
        callback()
      } else if (!patrn.test(value) || value.length < 8 || value.length > 24) {
        callback(
          new Error(this.$t('vendorMod.errorPass'))// "密码至少包含数字、大小写字母、特殊字符，长度为8~24位"
        )
      } else {
        callback()
      }
      // else if (value !== this.password.copyNewItem) {
      //   callback(this.$t('vendorMod.confirmPassError'));//"新密码与确认新密码不一致！"
      // }
    }
    return {
      activeName: 'second',
      loading: false,
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
      }
    }
  },
  computed: {
    ...mapState({
      entrance: (state) => state.user.entrance
    })
  },
  created () {
  },
  methods: {
    modifyUserPassword () {
      const { newItem, oldItem, copyNewItem } = this.password
      let sha1OldItem = sha1(oldItem)
      let sha1CopyNewItem = sha1(copyNewItem)
      this.$refs.relModel.validate(status => {
        if (status) {
          this.loading = true
          let hash = window.location.hash
          let query = hash.split('?')[1]
          if (query != '') {
            let userName = query.split('=')[1]
            this.$http({
              url: '/sys/checkAndModifyPassword',
              method: 'POST',
              data: {
                'username': userName,
                'oldPwd': sha1OldItem,
                'newPwd': sha1CopyNewItem
              },
              loading: true
            }).then(res => {
              if (res.code == '0') {
                this.$message({
                  message: '修改成功',
                  type: 'success'
                })
                this.loading = false
                return this.logout()
              }
            })
          }
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
