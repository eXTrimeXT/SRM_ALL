<template>
  <el-form
    ref="form"
    v-model="loginViolenceCheck"
    label-width="160px"
    class="antiBruteForceCracking"
  >
    <el-collapse
      v-model="activeCollapseForce"
      class="tab-form-style"
    >
      <!-- 认证失败 -->
      <el-collapse-item
        :title="$t('securitySettings.authenticationFailed')"
        name="1"
      >
        <!-- 时间锁定 -->
        <el-row>
          <el-col>
            <el-form-item label-width="0px">
              <div>
                <el-input-number
                  v-model="loginViolenceCheck.TIME.lessTimeSecond"
                  controls-position="right"
                  :min="1"
                  :max="1000000"
                  :precision="0"
                />
                {{ $t('securitySettings.lessTimeSecond') }}
                <!-- 分钟内连续认证失败 -->
                <el-input-number
                  v-model="loginViolenceCheck.TIME.failAmount"
                  controls-position="right"
                  :min="1"
                  :max="1000000"
                  :precision="0"
                />
                {{ $t('securitySettings.failAmount') }}
                <!-- 次，该账号将禁止登录 -->
                <el-input-number
                  v-model="loginViolenceCheck.TIME.lockSecond"
                  controls-position="right"
                  :min="1"
                  :max="1000000"
                  :precision="0"
                />
                {{ $t('securitySettings.lockSecond') }}
                <!-- 分钟 -->
              </div>
            </el-form-item>
          </el-col>
        </el-row>
      </el-collapse-item>
    </el-collapse>
    <CToolbar>
      <template #right>
        <el-button
          type="primary"
          @click="saveConfigFn"
        >
          {{ $t('common.save') }}
        </el-button>
      </template>
    </CToolbar>
  </el-form>
</template>

<script>
import CToolbar from 'lib@/components/c-toolbar'
import { securitySettingsApi } from 'modb@/accountAccess/api'
import cloneDeep from 'lodash/cloneDeep'
export default {
  name: 'AntiBruteForceCracking',
  components: { CToolbar },
  props: {
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
      activeCollapseForce: ['1', '2', '3'],
      loginViolenceCheck: { // 锁定类型：TIME:锁定时间 | ACCOUNT:锁定账号
        TIME: {
          configStatus: 'Y',
          lessTimeSecond: 600, // 600秒以内认证失败，单位秒
          failAmount: 5, // 认证失败次数
          lockSecond: 300, // lockType=TIME时有效 认证失败达到次数后锁定秒数，单位秒
          lockType: 'TIME'
        }
        // ACCOUNT: {
        //   configStatus: 'Y',
        //   lessTimeSecond: 600,
        //   failAmount: 5,
        //   lockSecond: 300,
        //   lockType: 'TIME'
        // }
      }
    }
  },
  watch: {
    configDate: {
      handler (nVal, oVal) {
        let keys = Object.keys(nVal)
        if (keys.length > 0) {
          let timeConfig = cloneDeep(this.configDate.TIME)
          const { lessTimeSecond, lockSecond, ...rest } = timeConfig
          let lessTimeMinutes = lessTimeSecond / 60
          let lockMinutes = lockSecond / 60
          this.loginViolenceCheck.TIME = {
            lessTimeSecond: lessTimeMinutes,
            lockSecond: lockMinutes,
            ...rest
          }
          this.$forceUpdate()
        }
      },
      immediate: true,
      deep: true
    }
  },
  created () {
  },
  methods: {
    // 保存数据
    async saveConfigFn () {
      const { lessTimeSecond, lockSecond, ...rest } = this.loginViolenceCheck.TIME
      let lessTimeSecondT = lessTimeSecond * 60
      let lockSecondT = lockSecond * 60
      let saveData = {
        loginViolenceCheck: {
          TIME: {
            ...rest,
            lessTimeSecond: lessTimeSecondT,
            lockSecond: lockSecondT
          }
        }
      }
      const res = await securitySettingsApi.saveViolence(saveData)
      if (res) {
        this.$message.success(res.message)
        this.$emit('saveSuccess', true, 'violence')
      }
    }
  }
}
</script>

<style lang="scss">
.antiBruteForceCracking{
 padding: 16px;
 color: #161C24;
}

</style>
