<template>
  <!-- 弹框区域 二次认证配置-->
  <srm-dialog
    :title="$t('secondaryAuthentication.dialogConfigTitle')"
    size="middle"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
    :append-to-body="true"
    @close="cancleHandle"
  >
    <el-form
      v-if="dialogVisible"
      ref="authEditForm"
      :model="configForm"
      :rules="configRules"
      label-position="right"
      label-width="74px"
      :disabled="opration=='view'"
      class="edit-auth-form"
    >
      <el-row :gutter="32" type="flex">
        <el-col :span="12">
          <!-- 业务场景 -->
          <el-form-item
            :label="$t('secondaryAuthentication.permissionCode')"
            prop="permissionCode"
            label-width="80px"
          >
            <el-select
              v-model="configForm.permissionCode"
              filterable
              @change="(val) => permissionChange(val, 'form')"
            >
              <el-option
                v-for="item in permissionList"
                :key="item.value+'permissionCode'"
                :value="item.value"
                :label="item.label"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="32" type="flex">
        <el-col :span="12">
          <el-form-item
            :label="$t('secondaryAuthentication.buttonPermissionCode')"
            prop="buttonPermissionCode"
            label-width="80px"
          >
            <el-select
              v-model="configForm.buttonPermissionCode"
              filterable
              @change="buttonPermissionChange"
            >
              <el-option
                v-for="item in buttonPermissionList"
                :key="item.value+'buttonPermissionCode'"
                :value="item.value"
                :label="item.label"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="32" type="flex">
        <el-col :span="24">
          <!-- 认证接口 -->
          <el-form-item
            :label="$t('secondaryAuthentication.authInterface')"
            label-width="80px"
            :rules="[
              { required: true },
            ]"
          >
            <el-table
              :data="twoAuthInterfaceList"
              border
              max-height="260px"
            >
              <el-table-column
                align="center"
                prop="permissionName"
                :label="$t('secondaryAuthentication.authInterfaceName')"
              />
              <!-- 按钮标识 -->
              <el-table-column
                align="center"
                prop="permission"
                :label="$t('secondaryAuthentication.authInterfaceDetail')"
              />
              <el-table-column
                align="center"
                prop="twoAuthStatus"
                width="120"
                :label="$t('secondaryAuthentication.twoAuthStatus')"
              >
                <template slot-scope="scope">
                  <el-checkbox v-model="scope.row.twoAuthStatus" true-label="Y" false-label="N" />
                </template>
              </el-table-column>
            </el-table>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="32" type="flex">
        <el-col :span="24">
          <!-- 认证方式 -->
          <el-form-item
            :label="$t('secondaryAuthentication.authType')"
            label-width="80px"
            :rules="[
              { required: true },
            ]"
          >
            <el-checkbox-group
              v-model="authTypeSelectList"
              class="authType-checkbox-group"
            >
              <el-checkbox label="accountPwd">
                <!-- 账密认证 -->
                {{ $t('secondaryAuthentication.accountPwd') }}
              </el-checkbox>
              <el-checkbox label="smsVerify">
                <!-- 短信认证 -->
                {{ $t('secondaryAuthentication.smsVerify') }}
              </el-checkbox>
              <el-checkbox label="emailVerify">
                <!-- 邮箱认证 -->
                {{ $t('secondaryAuthentication.emailVerify') }}
              </el-checkbox>
            </el-checkbox-group>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <div
      slot="footer"
      class="dialog-footer"
    >
      <el-button @click="cancleHandle">
        {{ $t("common.cancel") }}
      </el-button>
      <template v-if="['edit', 'add'].includes(opration)">
        <el-button
          type="primary"
          plain
          :loading="btnLoading"
          @click="saveConfigData('save')"
        >
          {{ $t("common.staging") }}
        </el-button>
        <el-button
          type="primary"
          :loading="btnLoading"
          @click="saveConfigData('submit')"
        >
          {{ $t("common.active") }}
        </el-button>
      </template>
    </div>
  </srm-dialog>
</template>

<script>
import { transformMQL } from '@/library/utils/util'
export default {
  name: 'EditDialog',
  props: {
    visible: {
      type: Boolean,
      required: true
    },
    opration: {
      type: String
    },
    twoAuthInfo: {
      type: Object
    }
  },

  data () {
    return {
      dialogVisible: false,
      btnLoading: false,
      configRules: {
        permissionCode: [{ required: true, message: this.$t('common.pleaseSelect') + this.$t('secondaryAuthentication.permissionCode') }],
        buttonPermissionCode: [{ required: true, message: this.$t('common.pleaseSelect') + this.$t('secondaryAuthentication.buttonPermissionCode') }]
      },
      configForm: {
        authStatus: 'DRAFT',
        permissionCode: '',
        permissionName: '',
        buttonPermissionCode: '',
        buttonPermissionName: '',
        twoAuthId: null
      },
      permissionList: [], // 场景下拉
      buttonPermissionList: [], // 操作类型下拉
      twoAuthInterfaceList: [],
      authTypeSelectList: []
    }
  },
  watch: {
    visible (oldValue, newValue) {
      this.dialogVisible = oldValue
      if (oldValue) {
        if (['edit', 'view'].includes(this.opration) && this.twoAuthInfo.twoAuthId) {
          this.authTypeSelectList = []
          if (this.twoAuthInfo.permissionCode) {
            // 查询认证操作
            this.permissionChange(this.twoAuthInfo.permissionCode)
          }
          this.getDetail()
        } else {
          // 新增
          this.configForm = {
            authStatus: 'DRAFT',
            permissionCode: '',
            permissionName: '',
            buttonPermissionCode: '',
            buttonPermissionName: ''
          }
          this.twoAuthInterfaceList = []
          this.authTypeSelectList = []
        }
      }
    }
  },
  created () {
    this.getPermissionList()
  },
  methods: {
    // 获取业务场景下拉
    async getPermissionList () {
      let permissionRes = await this.$http({
        url: '/api-rbac/buyer/permission/listLeafMenu',
        method: 'POST',
        data: {}
      })
      if (permissionRes) {
        this.permissionList = (permissionRes.data || []).map(i => ({
          permissionCode: i.permissionCode,
          value: (i.functionId).toString(),
          label: i.permissionName
        }))
      }
    },
    // 业务场景切换
    async permissionChange (val, type) {
      if (type) {
        this.configForm.buttonPermissionCode = ''
        this.configForm.buttonPermissionName = ''
        this.twoAuthInterfaceList = []
      }
      if (val) {
        let functionId = val
        let buttonRes = await this.$http({
          url: '/api-rbac/buyer/permission/listPermissionByParam',
          method: 'POST',
          data: { functionId, permissionType: 'BUTTON' }
        })
        if (buttonRes) {
          this.buttonPermissionList = (buttonRes.data || []).map(i => ({
            permissionCode: i.permissionCode,
            value: (i.permissionId).toString(),
            label: i.permissionName
          }))
        }
        let rowData = this.permissionList.find(i => (i.value == val))
        this.configForm.permissionName = rowData ? rowData.label : ''
      } else {
        this.buttonPermissionList = []
      }
    },
    // 认证操作切换
    async buttonPermissionChange (val) {
      if (val) {
        let parentPermissionId = val
        let interfaceRes = await this.$http({
          url: '/api-rbac/buyer/permission/listPermissionByParam',
          method: 'POST',
          data: { parentPermissionId }
        })
        this.twoAuthInterfaceList = (interfaceRes.data || []).map(i => ({
          ...i,
          twoAuthStatus: 'N'
        }))
        let rowData = this.buttonPermissionList.find(i => (i.value == val))
        this.configForm.buttonPermissionName = rowData ? rowData.label : ''
      } else {
        this.twoAuthInterfaceList = []
      }
    },
    async saveConfigData (type) {
      let validRes = true
      let validObj = {}
      this.btnLoading = true
      if (type == 'submit') {
        this.$refs.authEditForm.validate((valid, obj) => {
          validRes = valid
          validObj = obj
        })
      }
      if (!validRes) {
        const firstKey = Object.keys(validObj)[0]
        this.btnLoading = false
        return this.$message.error(validObj[firstKey][0].message)
      }
      let ifSelectInterface = this.twoAuthInterfaceList.filter(i => (i.twoAuthStatus == 'Y'))
      if (ifSelectInterface.length == 0) {
        this.btnLoading = false
        return this.$message.error(this.$t('secondaryAuthentication.interfaceSelectTip')) // '请勾选需要二次认证的接口'
      }
      let authTypeObj = {
        accountPwd: 'N',
        smsVerify: 'N',
        emailVerify: 'N'
      }
      if (this.authTypeSelectList.length > 0) {
        this.authTypeSelectList.forEach(key => {
          authTypeObj[key] = 'Y'
        })
      } else {
        authTypeObj = {
          accountPwd: 'N',
          smsVerify: 'N',
          emailVerify: 'N'
        }
        this.btnLoading = false
        return this.$message.error(this.$t('secondaryAuthentication.authTypeSelectTip')) // '请至少选择一种验证方式'
      }
      let action = type == 'submit' ? 'submit' : 'tempSave'
      this.configForm.authStatus = type == 'submit' ? 'VALID' : 'DRAFT'
      let submitData = {
        ...this.configForm,
        ...authTypeObj,
        twoAuthInterfaceList: this.twoAuthInterfaceList
      }

      let transformParams = transformMQL.save('TwoAuthBuyer', [submitData], action)
      let res = await this.$http({
        url: `/api-rbac/api-ql/TwoAuthBuyer/${action}`,
        method: 'POST',
        data: transformParams
      }).catch(err => {
        this.btnLoading = false
      })
      if (res) {
        this.btnLoading = false
        this.$message.success(this.$t('common.success'))
        // 暂存
        if (type == 'save') {
          let twoAuthId = res.data[0].twoAuthId
          this.configForm['twoAuthId'] = twoAuthId
          this.$emit('on-success', 'save', false)
        } else {
          this.$emit('on-success', 'submit', true)
        }
      }
    },
    async getDetail () {
      let id = this.twoAuthInfo.twoAuthId
      let query = {
        // '*': {}
        twoAuthId: {},
        authStatus: {},
        permissionCode: {},
        // permissionName: {},
        buttonPermissionCode: {},
        // buttonPermissionName: {},
        accountPwd: {},
        smsVerify: {},
        emailVerify: {},
        twoAuthInterfaceList: { '*': {} }
      }
      let transformParams = transformMQL.save('TwoAuthBuyer', [ id ], 'readDetail', query)
      const response = await this.$http({
        url: '/api-rbac/api-ql/TwoAuthBuyer/readDetail',
        method: 'POST',
        data: transformParams
      })
      if (response && response.data && response.data.length) {
        const { twoAuthInterfaceList, accountPwd, smsVerify, emailVerify, permissionCode, ...rest } = response.data[0]
        this.configForm = {
          ...rest,
          permissionCode
        }
        this.twoAuthInterfaceList = twoAuthInterfaceList
        if (accountPwd == 'Y') {
          this.authTypeSelectList.push('accountPwd')
        }
        if (emailVerify == 'Y') {
          this.authTypeSelectList.push('emailVerify')
        }
        if (smsVerify == 'Y') {
          this.authTypeSelectList.push('smsVerify')
        }
      }
    },
    cancleHandle () {
      this.$emit('on-cancle', 'cancle', true)
    }
  }
}
</script>

<style lang="scss" scoped>
.edit-auth-form{
  .el-form-item__label{
    font-weight: bold;
  }
}
</style>
