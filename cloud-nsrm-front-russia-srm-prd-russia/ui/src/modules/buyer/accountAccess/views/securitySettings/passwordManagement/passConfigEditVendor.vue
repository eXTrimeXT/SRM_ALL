<template>
  <div class="passConfigEdit">
    <el-form
      v-model="configForm"
    >
      <el-row>
        <el-col>
          <!-- 是否启用默认配置 -->
          <el-form-item prop="useDefault" :label="$t('passwordManagement.useDefault')">
            <el-switch
              v-model="configForm.useDefault"
              active-value="Y"
              inactive-value="N"
              @change="useDefaultChange"
            />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <el-form
      ref="form"
      v-model="configForm"
      :disabled="configForm.useDefault == 'Y'"
      :show-message="false"
    >
      <el-collapse
        v-model="activeCollapsePass"
        class="tab-form-style"
      >
        <!-- 密码生成策略配置 -->
        <el-collapse-item
          :title="$t('securitySettings.passGenerationPolicyConf')"
          name="1"
        >
          <el-row>
            <!-- 用户来源类型 -->
            <!-- <el-col>
              <el-form-item prop="sourceType" label-width="50px">
                <el-radio-group v-model="configForm.pwdCreated.DEFAULT.sourceType">
                  <el-radio
                    v-for="item in sourceTypeList"
                    :key="item.value"
                    :label="item.value"
                  >
                    {{ item.label }}
                  </el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col> -->
            <!-- 策略 -->
            <el-col>
              <el-form-item prop="switcher">
                <el-radio-group
                  v-model="configForm.pwdCreated.DEFAULT.switcher"
                  class="el-radio-group-pass"
                >
                  <el-radio-button label="RANDOM">
                    <!-- 随机字符 -->
                    {{ $t('passwordManagement.switcherRandom') }}
                  </el-radio-button>
                  <el-radio-button label="SOLID">
                    <!-- 固定字符字符 -->
                    {{ $t('passwordManagement.switcherSolid') }}
                  </el-radio-button>
                  <!-- <el-radio-button label="ATTR_VALUE">
                    属性值
                    <el-tooltip
                      class="item"
                      effect="dark"
                      placement="top"
                      content="基于每个账号设置固定密码"
                    >
                      <i class="el-icon-question" />
                    </el-tooltip>
                  </el-radio-button> -->
                </el-radio-group>
              </el-form-item>
            </el-col>
            <!-- 密码长度 -->
            <el-col v-if="configForm.pwdCreated.DEFAULT.switcher=='RANDOM'">
              <el-form-item
                :label="$t('passwordManagement.passLength')"
                :rules="[{required: configForm.pwdCreated.DEFAULT.switcher=='RANDOM'}]"
                label-width="108px"
              >
                <div>
                  <el-input-number
                    v-model="configForm.pwdCreated.DEFAULT.length"
                    controls-position="right"
                    :min="8"
                    :max="64"
                  />
                </div>
              </el-form-item>
              <el-form-item
                label="密码生成字段"
                label-width="108px"
              >
                <div>
                  <el-checkbox
                    v-model="configForm.pwdCreated.DEFAULT.containDigit"
                    true-label="Y"
                    false-label="N"
                  >
                    <!-- 包含数字 -->
                    {{ $t('passwordManagement.containDigit') }}
                  </el-checkbox>
                  <el-checkbox
                    v-model="configForm.pwdCreated.DEFAULT.containLowerLetter"
                    true-label="Y"
                    false-label="N"
                  >
                    <!-- 包含小写字母 -->
                    {{ $t('passwordManagement.containLowerLetter') }}
                  </el-checkbox>
                  <el-checkbox
                    v-model="configForm.pwdCreated.DEFAULT.containUpperLetter"
                    true-label="Y"
                    false-label="N"
                  >
                    <!-- 包含大写字母 -->
                    {{ $t('passwordManagement.containUpperLetter') }}
                  </el-checkbox>
                  <el-checkbox
                    v-model="configForm.pwdCreated.DEFAULT.containSpecialLetter"
                    true-label="Y"
                    false-label="N"
                  >
                    <!-- 包含特殊字符 -->
                    {{ $t('passwordManagement.containSpecialLetter') }}
                  </el-checkbox>
                </div>
              </el-form-item>
            </el-col>
            <!-- 添加固定密码 -->
            <el-col v-if="configForm.pwdCreated.DEFAULT.switcher=='SOLID'">
              <el-form-item
                :label="$t('passwordManagement.solidValue')"
                label-width="108px"
                :rules="[{required: configForm.pwdCreated.DEFAULT.switcher=='SOLID'}]"
              >
                <PasswordInput
                  v-model="configForm.pwdCreated.DEFAULT.value"
                  autocomplete="off"
                  :showWordLimit="false"
                  style="width:300px;"
                />
                <div>
                  允许特殊字符有 `~!@#$%^&*()-_=+[{}];:'",?.
                </div>
              </el-form-item>
            </el-col>
            <!-- 属性值 -->
            <el-col v-if="configForm.pwdCreated.DEFAULT.switcher=='ATTR_VALUE'">
              <div>
                开发中...
              </div>
            </el-col>
          </el-row>
        </el-collapse-item>
        <!-- 密码校验策略配置 -->
        <el-collapse-item
          :title="$t('securitySettings.passverificationPolicyConf')"
          name="2"
        >
          <el-row>
            <el-col>
              <el-form-item label-width="108px" label="密码校验字段">
                <el-checkbox
                  v-model="configForm.pwdCheck.containDigit"
                  true-label="Y"
                  false-label="N"
                >
                  <!-- 包含数字 -->
                  {{ $t('passwordManagement.containDigit') }}
                </el-checkbox>
                <el-checkbox
                  v-model="configForm.pwdCheck.containLowerLetter"
                  true-label="Y"
                  false-label="N"
                >
                  <!-- 包含小写字母 -->
                  {{ $t('passwordManagement.containLowerLetter') }}
                </el-checkbox>
                <el-checkbox
                  v-model="configForm.pwdCheck.containUpperLetter"
                  true-label="Y"
                  false-label="N"
                >
                  <!-- 包含大写字母 -->
                  {{ $t('passwordManagement.containUpperLetter') }}
                </el-checkbox>
                <el-checkbox
                  v-model="configForm.pwdCheck.containSpecialLetter"
                  true-label="Y"
                  false-label="N"
                >
                  <!-- 包含特殊字符 -->
                  {{ $t('passwordManagement.containSpecialLetter') }}
                </el-checkbox>
              </el-form-item>
            </el-col>
            <el-col>
              <el-form-item label-width="108px" :label="$t('passwordManagement.noeUseBeforNo')">
                <div>
                  <!-- 不能使用前 -->
                  <el-input-number
                    v-model="configForm.pwdCheck.cannotUsedAmount"
                    controls-position="right"
                    :min="1"
                    :max="10000"
                    style="margin-right:10px;"
                  />
                  <!-- 次使用过的密码 -->
                  <span>{{ $t('passwordManagement.usedPass') }}</span>
                </div>
              </el-form-item>
            </el-col>
            <el-col>
              <el-form-item label-width="108px" :label="$t('passwordManagement.passLangth')">
                <div>
                  <!-- 密码长度范围是 -->
                  <el-input-number
                    v-model="configForm.pwdCheck.minLength"
                    controls-position="right"
                    :min="8"
                    :max="32"
                  />
                  ~
                  <el-input-number
                    v-model="configForm.pwdCheck.maxLength"
                    controls-position="right"
                    :min="8"
                    :max="32"
                  />
                </div>
              </el-form-item>
            </el-col>
            <el-col>
              <el-form-item label-width="108px" :label="$t('passwordManagement.notContainArr')">
                <div>
                  <!-- 不能包含： -->
                  <el-select
                    v-model="configForm.pwdCheck.notContainArr"
                    style="width:300px;"
                    multiple
                    @change="notContainSelect"
                  >
                    <el-option
                      v-for="item in notContainList"
                      :key="item.value"
                      :value="item.value"
                      :label="item.label"
                    />
                  </el-select>
                </div>
              </el-form-item>
            </el-col>
          </el-row>
        </el-collapse-item>
        <!-- 初始密码强制修改配置 -->
        <el-collapse-item
          :title="$t('securitySettings.initPassForceChangeConf')"
          name="3"
        >
          <el-row>
            <el-col>
              <el-form-item>
                <div>
                  <!-- 初始密码强制修改 -->
                  <span>{{ $t('passwordManagement.forceChangeInitPwd') }}</span>
                  <el-tooltip
                    class="item"
                    effect="dark"
                    placement="top"
                    :content="$t('passwordManagement.forceChangeInitPwdTip')"
                  >
                    <i class="el-icon-question" />
                  </el-tooltip>
                  <el-switch
                    v-model="configForm.loginCheck.forceChangeInitPwd"
                    active-value="Y"
                    inactive-value="N"
                    style="margin-left: 10px;"
                  />
                </div>
              </el-form-item>
            </el-col>
          </el-row>
        </el-collapse-item>
        <!-- 密码自动过期配置 -->
        <el-collapse-item
          :title="$t('securitySettings.autoPassExpirationConf')"
          name="4"
        >
          <el-row>
            <el-col>
              <el-form-item prop="pwdTimeoutDays" label-width="108px" :label="$t('passwordManagement.pwdTimeoutDays')">
                <div>
                  <!-- 默认有效期 -->
                  <el-input-number
                    v-model="configForm.loginCheck.pwdTimeoutDays"
                    controls-position="right"
                    :min="1"
                    :max="10000"
                    :disabled="configForm.loginCheck.pwdNeverTimeout=='Y'"
                    style="margin-right:8px;"
                  />
                  <span>{{ $t('passwordManagement.pwdTimeoutDaysTip') }}</span>
                  <el-checkbox
                    v-model="configForm.loginCheck.pwdNeverTimeout"
                    true-label="Y"
                    false-label="N"
                    style="margin:0 8px 0 24px;"
                  >
                    <!-- 永久 -->
                    {{ $t('passwordManagement.pwdNeverTimeout') }}
                  </el-checkbox>
                </div>
              </el-form-item>
            </el-col>
            <el-col>
              <el-form-item label-width="108px" :label="$t('passwordManagement.pwdExpireRemind')">
                <div>
                  <!-- 过期提醒 -->
                  <el-checkbox
                    v-model="configForm.pwdExpireRemind.email"
                    true-label="Y"
                    false-label="N"
                    :disabled="configForm.loginCheck.pwdNeverTimeout=='Y'"
                  >
                    <!-- 邮箱 -->
                    {{ $t('passwordManagement.emial') }}
                  </el-checkbox>
                  <span style="display:inline-block;margin-left: 24px;">
                    {{ $t('passwordManagement.pwdExpireRemindBeford') }}
                  </span>
                  <el-input-number
                    v-model="configForm.pwdExpireRemind.advanceDays"
                    controls-position="right"
                    :min="1"
                    :max="10000"
                    :disabled="configForm.loginCheck.pwdNeverTimeout=='Y'"
                    style="margin:0 10px;"
                  />
                  <span>{{ $t('passwordManagement.pwdTimeoutDaysTip') }}</span>
                </div>
              </el-form-item>
            </el-col>
          </el-row>
        </el-collapse-item>
      </el-collapse>
    </el-form>
    <div class="passConfigEdit-footer">
      <el-button
        type="primary"
        :loading="saveLoading"
        @click="saveConfigFn"
      >
        {{ $t('common.save') }}
      </el-button>
    </div>
  </div>
</template>

<script>
import PasswordInput from 'lib@/components/passwordInput'
import CToolbar from 'lib@/components/c-toolbar'
import { securitySettingsApi } from 'modb@/accountAccess/api'
import cloneDeep from 'lodash/cloneDeep'
import { validPatrn } from '@/utils/passValid'

export default {
  name: 'PassConfigEditVendor',
  components: {
    PasswordInput,
    CToolbar
  },
  props: {
    // 用户类型
    userType: {
      type: String,
      default: () => { // BUYER | VENDOR
        return 'BUYER'
      }
    },
    // 配置数据
    configDate: {
      type: Object,
      default: () => {
        return {}
      }
    }
  },
  data () {
    return {
      saveLoading: false,
      activeCollapsePass: ['1', '2', '3', '4', '5', '6', '7', '8'],
      configForm: {
        useDefault: 'Y', // 启用默认配置
        userType: 'BUYER', // BUYER | VENDOR
        pwdCreated: { // 创建密码
          // sourceType: 'DEFAULT', // 用户来源类型: DEFAULT | REGISTER | VENDOR_GREEN | VENDOR_OPENAPI
          DEFAULT: { // 对应用户来源类型的设置值
            switcher: 'RANDOM', // 策略: RANDOM 随机 | SOLID 固定
            value: '', // 固定密码的值
            length: 8, // 随机密码时指定的密码长度
            containDigit: 'Y', // 随机密码包含数字: Y | N
            containLowerLetter: 'Y', // 随机密码包含小写字母: Y | N
            containUpperLetter: 'Y', // 随机密码包含大写字母: Y | N
            containSpecialLetter: 'Y' // 随机密码包含特殊字符:Y | N
          }
        },
        pwdCheck: { // 密码校验策略
          cannotUsedAmount: 1, // 不能使用最近的几个密码
          minLength: 8, // 最小长度，包含：>=minLength
          maxLength: 16, // 最大长度，包含：<=maxLength
          containDigit: 'Y', // 随机密码包含数字:Y/N
          containLowerLetter: 'Y', // 随机密码包含小写字母:Y/N
          containUpperLetter: 'Y', // 随机密码包含大写字母:Y/N
          containSpecialLetter: 'Y', // 随机密码包含特殊字符:Y/N
          notContainColumn: {}, // 不能包含的字段
          notContainArr: [] // 不能包含的字段
        },
        loginCheck: { // 密码校验
          forceChangeInitPwd: 'N', // 是否强制修改初始密码
          pwdTimeoutDays: 30, // 密码过期天数
          pwdNeverTimeout: 'N'
        },
        pwdExpireRemind: { // 自动过期设置
          email: 'Y', // 邮件提醒
          advanceDays: 5// 短信提醒
        }
      },
      notContainList: [
        {
          label: '账号',
          value: 'username-total',
          objValue: { username: { total: 'Y' } }
        },
        {
          label: '账号倒序',
          value: 'username-desc',
          objValue: { username: { desc: 'Y' } }
        }
        // {
        //   label: '昵称',
        //   value: 'nickname-total',
        //   objValue: { nickname: { total: 'Y' } }
        // },
        // {
        //   label: '昵称倒序',
        //   value: 'nickname-desc',
        //   objValue: { desc: 'Y' }
        // },
        // {
        //   label: '手机号',
        //   value: 'phone-total',
        //   objValue: { phone: { total: 'Y' } }
        // },
        // {
        //   label: '手机号倒序',
        //   value: 'phone-desc',
        //   objValue: { phone: { desc: 'Y' } }
        // },
        // {
        //   label: '邮箱',
        //   value: 'email-total',
        //   objValue: { email: { total: 'Y' } }
        // },
        // {
        //   label: '邮箱倒序',
        //   value: 'email-desc',
        //   objValue: { email: { desc: 'Y' } }
        // },
        // {
        //   label: '员工号',
        //   value: 'empNo-total',
        //   objValue: { empNo: { total: 'Y' } }
        // },
        // {
        //   label: '员工号倒序',
        //   value: 'empNo-desc',
        //   objValue: { empNo: { desc: 'Y' } }
        // }
      ],
      visEditFlag: false
    }
  },
  computed: {
    // 用户来源类型
    sourceTypeList () {
      if (this.userType == 'BUYER') {
        return [
          { value: 'DEFAULT', label: '采购商初始化' },
          { value: 'REGISTER', label: '新建采购商账号' }
        ]
      } else {
        return [
          { value: 'DEFAULT', label: '供应商初始化' },
          { value: 'REGISTER', label: '供应商注册' },
          { value: 'VENDOR_GREEN', label: '供应商绿色通道' },
          { value: 'VENDOR_GREEN1', label: '子账号来源' },
          { value: 'VENDOR_OPENAPI', label: '供应商开放接口' }
        ]
      }
    }
  },
  watch: {
    configDate: {
      handler (nVal, oVal) {
        let keys = Object.keys(nVal)
        if (keys.length > 0) {
          let config = cloneDeep(this.configDate)
          this.configForm = config
          this.configForm.pwdCheck['notContainArr'] = this.notContainColumnReRender(config.pwdCheck.notContainColumn)
        }
      },
      immediate: true,
      deep: true
    },
    configForm: {
      deep: true,
      handler (nVal, oVal) {
        this.visEditFlag += 1
        this.$emit('isChange', this.visEditFlag)
      }
    }
  },
  async created () {
  },
  methods: {
    // 开启默认配置，查询默认配置
    async useDefaultChange (val) {
      if (val == 'Y') {
        const { data } = await securitySettingsApi.getDefaultUserSecurityConfig({ userType: this.userType })
        this.configForm = data
        this.configForm.pwdCheck['notContainArr'] = this.notContainColumnReRender(data.pwdCheck.notContainColumn)
      }
    },
    notContainSelect (value) {
      let resObj = {}
      if (value) {
        let valArr = value
        resObj = valArr.reduce((acc, cur) => {
          const [key, value] = cur.split('-')
          if (!acc[key]) {
            acc[key] = {}
          }
          acc[key][value] = 'Y'
          return acc
        }, {})
      } else {
        resObj = {}
      }
      this.$forceUpdate()
      this.configForm.pwdCheck.notContainColumn = resObj
    },
    notContainColumnReRender (notContainColumn = {}) {
      let result = []
      for (let key in notContainColumn) {
        let total = notContainColumn[key]['total']
        let desc = notContainColumn[key]['desc']

        if (total === 'Y') {
          result.push(`${key}-total`)
        }

        if (desc === 'Y') {
          result.push(`${key}-desc`)
        }
      }
      console.log(result)
      return result
    },
    // 保存数据
    async saveConfigFn () {
      // 密码生成校验
      if (this.configForm.pwdCreated.DEFAULT.switcher == 'SOLID') {
        if (!this.configForm.pwdCreated.DEFAULT.value) {
          return this.$message.error('请添加添加固定密码')
        } else {
          let patrn = validPatrn
          if (!patrn.test(this.configForm.pwdCreated.DEFAULT.value)) {
            return this.$message.error('固定密码只能是数字、小写字母、大写字母、特殊字符 `~!@#$%^&*()-_=+[{}];:\'",?.')
          }
        }
      } else {
        if (!this.configForm.pwdCreated.DEFAULT.length) {
          return this.$message.error('请输入密码长度')
        }
        let ruleCount = 0
        let keys = ['containDigit', 'containLowerLetter', 'containUpperLetter', 'containSpecialLetter']
        keys.forEach(key => {
          if (this.configForm.pwdCreated.DEFAULT[key] == 'Y') {
            ruleCount += 1
          }
        })
        if (ruleCount == 0) {
          return this.$message.error('密码生成策略的随机字符至少包含数字、小写字母、大写字母、特殊字符其中一种')
        }
      }
      // 密码校验
      // 密码校验长度范围
      if (this.configForm.pwdCheck.minLength > this.configForm.pwdCheck.maxLength) {
        return this.$message.error('密码长度范围左边数值不能大于右边数值')
      }

      let saveData = {
        ...this.configForm,
        userType: this.userType
      }
      const res = await securitySettingsApi.saveUserSecurityConfig(saveData)
      if (res) {
        this.$message.success(res.message)
        this.visEditFlag = 0
        this.$emit('saveSuccess', true, this.userType)
      }
    }
  }
}
</script>

<style lang="scss">
.el-radio-group-pass{
  .el-radio-button{
    &.is-active{
      .el-radio-button__inner{
        background: #E7F2FF;
        border-color: #0077FF;
        color: #0077FF;
        &:hover {
          background-color: #E7F2FF;
        }
      }
    }
  }
}
</style>
