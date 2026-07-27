<template>
  <el-container class="flex-container the-quick-demo" direction="vertical">
    <el-main>
      <el-form
        ref="form"
        :model="form"
        class="form-incontainer"
        :rules="rules"
      >
        <SrmRow>
          <SrmCol>
            <el-form-item prop="uniqueCode" :label="$t('todolistConfig.uniqueCode')">
              <el-input v-model="form.uniqueCode" disabled />
            </el-form-item>
          </SrmCol>

          <SrmCol>
            <el-form-item prop="configModule" :label="$t('todolistConfig.configModule')">
              <DictSelect v-model="form.configModule" code="MODULE_DIVISION" />
            </el-form-item>
          </SrmCol>

          <SrmCol>
            <el-form-item prop="configCode" :label="$t('todolistConfig.busconfigCode')">
              <el-input v-model="form.configCode" />
            </el-form-item>
          </SrmCol>

          <SrmCol>
            <el-form-item prop="configName" :label="$t('todolistConfig.budconfigName')">
              <el-input v-model="form.configName" />
            </el-form-item>
          </SrmCol>

          <SrmCol>
            <el-form-item prop="configType" :label="$t('todolistConfig.busconfigType')">
              <el-select v-model="form.configType" clearable>
                <el-option :label="$t('todolistConfig.waitOrder')" value="WAIT_FORM" />
                <el-option :label="$t('todolistConfig.orderDone')" value="DONE_FORM" />
                <el-option :label="$t('flowMod.startProcess')" value="MY_START" />
                <el-option :label="$t('todolistConfig.waitFlow')" value="WAIT_FLOW" />
                <el-option :label="$t('todolistConfig.waitRead')" value="WAIT_READ" />
                <el-option :label="$t('todolistConfig.readed')" value="READED" />
              </el-select>
            </el-form-item>
          </SrmCol>

          <SrmCol>
            <el-form-item prop="configVersion" :label="$t('todolistConfig.busconfigVersion')">
              <el-input v-model="form.configVersion" type="number" />
            </el-form-item>
          </SrmCol>

          <SrmCol>
            <el-form-item prop="configStatus" :label="$t('todolistConfig.busConfigStatus')">
              <DictSelect v-model="form.configStatus" code="YES_OR_NO" />
            </el-form-item>
          </SrmCol>

          <SrmCol>
            <el-form-item prop="configFrom" :label="$t('todolistConfig.configFrom')">
              <el-select v-model="form.configFrom" clearable>
                <el-option :label="$t('common.product')" value="PRODUCT" />
                <el-option :label="$t('common.project')" value="PROJECT" />
              </el-select>
            </el-form-item>
          </SrmCol>

          <SrmCol>
            <el-form-item prop="roleCode" :label="$t('common.role')">
              <CRoleSelector
                v-model="form.roleCode"
                collapse-tags
                value-key="roleCode"
                :placeholder="$t('common.pleaseSelect')"
              />
            </el-form-item>
          </SrmCol>

          <SrmCol>
            <el-form-item prop="dimension" :label="$t('todolistConfig.dimension')">
              <el-input v-model="form.dimension" />
            </el-form-item>
          </SrmCol>
          <SrmCol>
            <el-form-item prop="configMode" :label="$t('todolistConfig.configMode')">
              <el-select v-model="form.configMode" clearable>
                <el-option label="SQL" value="SQL" />
                <el-option label="URL" value="URL" />
              </el-select>
            </el-form-item>
          </SrmCol>
        </SrmRow>

        <SrmRow>
          <SrmCol :init-col="1">
            <el-form-item prop="sqlLanguage" :label="$t('todolistConfig.sqlLanguage')">
              <el-input
                v-model="form.sqlLanguage"
                type="textarea"
                :rows="10"
                :showWordLimit="true"
                :maxlength="4000"
              />
            </el-form-item>
          </SrmCol>
        </SrmRow>

        <SrmRow>
          <SrmCol>
            <el-form-item prop="urlAddress" :label="$t('todolistConfig.urlAddress')">
              <el-input v-model="form.urlAddress" />
            </el-form-item>
          </SrmCol>
          <SrmCol>
            <el-form-item prop="urlParams" :label="$t('todolistConfig.urlParams')">
              <el-input v-model="form.urlParams" />
            </el-form-item>
          </SrmCol>
          <SrmCol>
            <el-form-item prop="urlMapping" :label="$t('todolistConfig.urlMapping')">
              <el-input v-model="form.urlMapping" />
            </el-form-item>
          </SrmCol>
          <SrmCol>
            <el-form-item prop="frontRouteName" :label="$t('todolistConfig.frontRouteName')">
              <el-input v-model="form.frontRouteName" />
            </el-form-item>
          </SrmCol>
          <SrmCol>
            <el-form-item prop="frontFormName" :label="$t('todolistConfig.frontFormName')">
              <el-input v-model="form.frontFormName" />
            </el-form-item>
          </SrmCol>
          <SrmCol>
            <el-form-item prop="frontFunctionName" :label="$t('todolistConfig.frontFunctionName')">
              <el-input v-model="form.frontFunctionName" />
            </el-form-item>
          </SrmCol>
        </SrmRow>

        <SrmRow v-if="!form.selectMode || form.selectMode === 'SQL'">
          <SrmCol :init-col="1">
            <!-- SQL查询语句 -->
            <div class="useDes">
              {{ $t('todolistConfig.todoUseDes') }} ：<br>
              <span class="red">businessId:</span>{{ $t('reApproval.receiptId') }}<br>
              <span class="red">businessCode:</span>{{ $t('dataConfMod.sequenceCode') }}<br>
              <span class="red">title:</span>{{ $t('flowMod.title') }}<br>
              <span class="red">createName:</span>{{ $t('common.creator') }}<br>
              <span class="red">creationDate:</span>{{ $t('common.creationTime') }}<br>
              <span class="red">residenceTime:</span>{{ $t('dashboard.residenceTime') }}<br>
            </div>
          </SrmCol>
          <SrmCol :init-col="1" class="placeholderItem">
            <!-- SQL查询语句 -->
            <div class="useDes">
              {{ $t('reportSetting.sqlUseDes') }}：<br>
              <span class="red">{{ $t('reportSetting.sqlUseDesPlaceHolder') }}</span>
              <span class="red">select * from base_dict where vendor_id =${user.userId}
              </span>
            </div>
          </SrmCol>

          <SrmCol :init-col="1" class="placeholderItem">
            <el-tag
              v-for="(placeholderItem, placeholderKey) in placeholderConfigArray"
              :key="placeholderKey"
            >
              {{ placeholderItem.label }}
            </el-tag>
          </SrmCol>
        </SrmRow>
      </el-form>

      <CToolbar>
        <template #right>
          <el-button @click="back">
            {{ $t('common.backTo') }}
          </el-button>
          <el-button type="primary" @click="submitOne">
            {{ $t('common.submit') }}
          </el-button>
        </template>
      </CToolbar>
    </el-main>
  </el-container>
</template>

<script>
/* eslint-disable no-template-curly-in-string */
import { tabTodoMixin } from '@/utils/mixins'
import todolistConfigHttp from 'modb@/basicSetting/api/todolistConfig'
import MainHeader from 'lib@/components/Table/MainHeader'
import CToolbar from 'lib@/components/c-toolbar'
import CRoleSelector from '@/library/components/c-role-selector'

export default {
  name: 'TodolistConfigDetail',

  components: {
    MainHeader,
    CToolbar,
    CRoleSelector
  },

  mixins: [tabTodoMixin],

  data () {
    return {
      placeholderConfigArray: [
        { type: 'success', label: '${user.userId}' },
        { type: 'success', label: '${user.userName}' },
        { type: 'success', label: '${user.enabled}' },
        { type: 'success', label: '${user.nickname}' },
        { type: 'success', label: '${user.companyId}' },
        { type: 'success', label: '${user.companyCode}' },
        { type: 'success', label: '${user.companyName}' },
        { type: 'success', label: '${user.userType}' }
      ],
      form: {
        todolistConfigId: null,
        uniqueCode: null,
        configModule: null,
        configCode: null,
        configName: null,
        configType: null,
        configFrom: null,
        configVersion: null,
        configStatus: null,
        roleCode: null,
        dimension: null,
        configMode: null,
        sqlLanguage: null,
        urlAddress: null,
        urlParams: null,
        urlMapping: null,
        frontRouteName: null,
        frontFormName: null,
        frontFunctionName: null
      },
      rules: {
        configModule: [{ required: true, message: this.$t('common.requiredField') }],
        configCode: [{ required: true, message: this.$t('common.requiredField') }],
        configName: [{ required: true, message: this.$t('common.requiredField') }],
        configType: [{ required: true, message: this.$t('common.requiredField') }],
        configFrom: [{ required: true, message: this.$t('common.requiredField') }],
        configVersion: [{ required: true, message: this.$t('common.requiredField') }],
        configStatus: [{ required: true, message: this.$t('common.requiredField') }],
        roleCode: [{ required: true, message: this.$t('common.requiredField') }],
        dimension: [{ required: true, message: this.$t('common.requiredField') }],
        configMode: [{ required: true, message: this.$t('common.requiredField') }]
      }
    }
  },

  created () {
    if (this.$attrs.params.flag === 'edit') {
      for (let i in this.form) {
        this.form[i] = this.$attrs.params.row[i] || ''
      }
      this.getDetail()
    }
  },

  methods: {
    /* 详情查询 */
    async getDetail () {
      const response = await todolistConfigHttp.get(this.$attrs.params.row.todolistConfigId)
      if (response) {
        this.form = response.data || []
      }
    },

    /* 提交 */
    submitOne () {
      this.$refs.form.validate(async valid => {
        if (valid) {
          const response = await todolistConfigHttp.saveOrUpdate(Object.assign({}, this.form))
          if (response) {
            this.$message.success(this.$t('common.successSave'))
            this.back()
          }
        } else {
          this.__focus_error__()
        }
      })
    },
    back () {
      this.$emit('tab-remove', this.$attrs.tabName)
      this.__setTabTodo('TodolistConfigList.getQueryData')
    }
  }
}
</script>
<style scoped lang="scss">
.placeholderItem {
  padding: 10px 0;
  .el-tag {
    margin-right: 10px;
  }
}
.useDes {
  font-size: 12px;
  line-height: 20px;
  padding: 8px;
  background: #cdd5da;
  border-radius: 4px;
}
.red {
  color: red;
}
</style>
