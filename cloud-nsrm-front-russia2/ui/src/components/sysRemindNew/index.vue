<template>
  <srm-dialog
    v-if="dialogVisible"
    title="管理授权"
    :visible.sync="dialogVisible"
    size="middle"
    :close-on-click-modal="false"
  >
    <div class="licenseContent">
      <div style="margin-bottom:10px;">
        <el-row type="flex" :gutter="20">
          <el-col style="width:140px;text-align: center;padding-top: 10px;">
            <img src="./appReg.png" width="100" />
          </el-col>
          <el-col class="licenseContentTitle">
            <h3>管理您的授权</h3>
            <p>如果您购买了美擎产品，请在下面输入产品授权密钥。如果您不清楚产品密钥，可复制下方注册码后，联系美云商务团队，将注册码通过邮件的方式发送给商务团队，以此获取密钥。</p>
          </el-col>
        </el-row>
      </div>
      <el-form
        ref="licenseForm"
        :model="LicenseForm"
        :rules="rules"
        label-width="78px"
        :status-icon="false"
      >
        <el-form-item label="注册码" style="margin-bottom: 18px;">
          <p>
            <el-input
              v-model="appRegisterCode"
              type="textarea"
              :disabled="true"
              rows="4"
              resize="none"
              :showWordLimit="false"
              :maxlength="null"
            />
          </p>
          <p style="margin-top:8px;">
            <el-button @click="copyCodeHandle">复制注册码</el-button>
          </p>
        </el-form-item>
        <el-form-item prop="licenseA" label="被授权企业" style="margin-bottom: 18px;">
          <el-input
            v-model="LicenseForm.licenseA"
            :showWordLimit="false"
            :maxlength="null"
            type="textarea"
            rows="3"
            resize="none"
          />
        </el-form-item>
        <el-form-item prop="licenseP" label="授权密钥" style="margin-bottom: 18px;">
          <el-input
            v-model="LicenseForm.licenseP"
            :showWordLimit="false"
            :maxlength="null"
            type="textarea"
            rows="3"
            resize="none"
          />
        </el-form-item>
      </el-form>
    </div>
    <div slot="footer" class="dialog-footer">
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" :loading="updateLicenseLoading" @click="updateLicenseHandle">提交</el-button>
    </div>
  </srm-dialog>
</template>
<script>
import qs from 'qs'
import { mapState } from 'vuex'
export default {
  name: 'SysRemindNew',
  data () {
    return {
      dialogVisible: false,
      updateLicenseLoading: false,
      LicenseForm: {
        licenseA: '', // 被授权企业
        licenseP: '' // 授权密钥
      },
      rules: {
        licenseA: [{ required: true, message: '请输入' }],
        licenseP: [{ required: true, message: '请输入' }]
      }
    }
  },
  computed: {
    ...mapState({
      // 注册码
      appRegisterCode: state => {
        return state.app.appRegisterCode
      }
    })
  },
  watch: {
    appRegisterCode: {
      immediate: true,
      deep: true,
      handler (val) {
        if (val) {
          this.dialogVisible = true
        }
      }
    }
  },
  mounted () {
  },
  methods: {
    copyCodeHandle () {
      let tag = document.createElement('input')
      tag.setAttribute('id', 'cp_hgz_input')
      tag.value = this.appRegisterCode // 注册码
      document.getElementsByTagName('body')[0].appendChild(tag)
      document.getElementById('cp_hgz_input').select()
      document.execCommand('copy')
      document.getElementById('cp_hgz_input').remove()
      this.$message.success('复制成功!')
    },
    updateLicenseHandle () {
      this.$refs['licenseForm'].validate(valid => {
        if (valid) {
          this.updateLicenseLoading = true
          this.$http({
            url: '/license/ac/update',
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' }, // 登录安全考虑，数据放到form-data下面
            data: qs.stringify(this.LicenseForm),
            returnDirectly: true
          }).then(res => {
            let resData = res.data
            this.updateLicenseLoading = false
            if (resData.code == '0') {
              this.$confirm('请稍后尝试重新访问系统', '产品授权成功', {
                confirmButtonText: this.$t('common.confirm'),
                cancelButtonText: this.$t('common.cancel'),
                type: 'success',
                showClose: false,
                showCancelButton: false,
                closeOnClickModal: false
              }).then(() => {
                this.dialogVisible = false
                // 激活成功以后把说明置空
                this.$store.dispatch('app/appRegisterFn', '')
              }).catch(() => {
                this.dialogVisible = false
                this.$store.dispatch('app/appRegisterFn', '')
              })
            } else {
              this.$confirm('请检查授权信息是否正确', '产品授权失败', {
                confirmButtonText: this.$t('common.confirm'),
                cancelButtonText: this.$t('common.cancel'),
                type: 'error',
                showClose: false,
                showCancelButton: false,
                closeOnClickModal: false
              }).then(() => {

              }).catch(() => {

              })
            }
          }).catch(() => {
            this.updateLicenseLoading = false
          })
        }
      })
    }
  }
}
</script>
<style lang="scss" scoped>
.licenseContent{
  min-height:80px;
  p {
    margin: 0;
    font-size:12px;
  }
  .licenseContentTitle{
    h3{
      margin: 0;
      line-height: 28px;
      font-size: 18px;
      color: #161C24;
      font-weight: 500;
    }
    p{
      color: #73777C;
      line-height: 20px;
    }
  }

}
</style>
