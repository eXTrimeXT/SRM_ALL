<template>
  <SrmDialog
    :title="$t('quoteTemplate.fun.tabTitle')"
    size="large"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
    append-to-body
  >
    <el-form
      ref="apiForm"
      :model="funFormData"
      :rules="apiFormRules"
      :disabled="pageFlag.isView"
    >
      <SrmRow>
        <!--函数名称-->
        <SrmCol :init-col="3">
          <el-form-item :label="$t('quoteTemplate.fun.name')" prop="apiName">
            <el-input v-model="funFormData.apiName" />
          </el-form-item>
        </SrmCol>

        <!--函数类型-->
        <SrmCol :init-col="3">
          <el-form-item :label="$t('quoteTemplate.fun.type')" prop="apiType">
            <DictSelect
              v-model="funFormData.apiType"
              code="SOU_QUOTE_TEMP_API_TYPE"
            />
          </el-form-item>
        </SrmCol>

        <!--函数类型 Service-->
        <template v-if="funFormData.apiType === 'SERVICE'">
          <!--分流服务类-->
          <SrmCol :init-col="3">
            <el-form-item :label="$t('quoteTemplate.fun.client')" prop="apiClient">
              <el-input v-model="funFormData.apiClient" />
            </el-form-item>
          </SrmCol>

          <!--业务服务类-->
          <SrmCol :init-col="3">
            <el-form-item :label="$t('quoteTemplate.fun.class')" prop="apiService">
              <el-input v-model="funFormData.apiService" />
            </el-form-item>
          </SrmCol>
        </template>

        <!--函数类型 URL-->
        <SrmCol v-if="funFormData.apiType === 'URL'" :init-col="3">
          <el-form-item :label="$t('quoteTemplate.fun.url')" prop="apiUrl">
            <el-input v-model="funFormData.apiUrl" />
          </el-form-item>
        </SrmCol>
      </SrmRow>
    </el-form>

    <p>
      <span style="margin-right: 10px">
        {{ $t('quoteTemplate.fun.arg') }}
      </span>

      <!--新增-->
      <el-button
        v-if="!pageFlag.isView"
        type="primary"
        size="mini"
        @click="addRow"
      >
        {{ $t('common.add') }}
      </el-button>
    </p>

    <el-table
      ref="apiDetailsTable"
      :data="apiDetails"
      style="width: 100%"
      border
      max-height="250px"
    >
      <el-table-column
        align="center"
        type="index"
        width="50"
      />

      <!--参数名称-->
      <el-table-column
        align="center"
        prop="argName"
        :label="$t('quoteTemplate.fun.argName')"
        min-width="120"
        :render-header="_addStarToColumn"
      >
        <template v-slot="{ row }">
          <el-input v-model="row.argName" :disabled="pageFlag.isView" />
        </template>
      </el-table-column>

      <!--参数说明-->
      <el-table-column
        align="center"
        prop="argDesc"
        :label="$t('quoteTemplate.fun.argDesc')"
        min-width="120"
        :render-header="_addStarToColumn"
      >
        <template v-slot="{ row }">
          <el-input v-model="row.argDesc" :disabled="pageFlag.isView" />
        </template>
      </el-table-column>

      <!--参数类型-->
      <el-table-column
        align="center"
        prop="argType"
        :label="$t('quoteTemplate.fun.argType')"
        min-width="120"
        :render-header="_addStarToColumn"
      >
        <template v-slot="{ row }">
          <DictSelect
            v-model="row.argType"
            code="SOU_QUOTE_TEMP_API_ARG_TYPE"
            :disabled="pageFlag.isView"
          />
        </template>
      </el-table-column>

      <!--必填-->
      <el-table-column
        align="center"
        prop="required"
        :label="$t('quoteTemplate.fun.required')"
        min-width="90"
        :render-header="_addStarToColumn"
      >
        <template v-slot="{ row }">
          <el-checkbox
            v-model="row.required"
            true-label="Y"
            false-label="N"
            :disabled="pageFlag.isView"
          />
        </template>
      </el-table-column>

      <!--测试入参-->
      <el-table-column
        v-if="pageFlag.isTest"
        align="center"
        prop="apiTestArg"
        :label="$t('quoteTemplate.fun.apiTestArg')"
        min-width="120"
        :render-header="_addStarToColumn"
      >
        <template v-slot="{ row }">
          <el-input v-model="row.apiTestArg" />
        </template>
      </el-table-column>

      <!--t 操作-->
      <el-table-column
        v-if="!pageFlag.isView"
        align="center"
        prop="operation"
        :label="$t('bidMod.operation')"
        width="80"
      >
        <template v-slot="{ $index }">
          <!--删除-->
          <el-button type="text" @click="deleteRow($index)">
            {{ $t('common.delete') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <div
      slot="footer"
      class="dialog-footer"
    >
      <!--取消-->
      <el-button @click="dialogVisible = false">
        {{ $t('common.cancel') }}
      </el-button>

      <template v-if="!pageFlag.isView">
        <!--提交-->
        <el-button
          type="primary"
          @click="saveOrSubmit('SUBMIT')"
        >
          {{ $t('common.submit') }}
        </el-button>
        <!--暂存-->
        <el-button
          type="primary"
          @click="saveOrSubmit('SAVE')"
        >
          {{ $t('common.staging') }}
        </el-button>
      </template>

      <!--API测试-->
      <el-button
        v-if="pageFlag.isTest"
        type="primary"
        @click="apiTest"
      >
        {{ $t('quoteTemplate.fun.apiTest') }}
      </el-button>
    </div>
  </SrmDialog>
</template>

<script>
import { quoteBuyerHttp } from 'modb@/quoteTemplate/api'
import { validateRequiredColumn } from '@/library/mixins/addStarToColumn'

export default {
  name: 'DetailDialog',

  props: {
    visible: {
      type: Boolean,
      required: true
    },
    editRow: {
      type: Object,
      required: false
    },
    flag: {
      type: String,
      default: 'add'
    }
  },

  data () {
    const checkApiName = (_rule, value, callback) => {
      if (value && !/^[_0-9a-zA-Z\u4e00-\u9fa5]*$/.test(value)) {
        // '函数名称只能输入英文字母数组下划线以及中文！'
        callback(new Error(this.$t('cusEntry.supplement20250211.functionNameCanOnlyInputEnglishLettersArrayUnderscoreAndChinese')))
      }
      callback()
    }
    return {
      apiDetails: [],
      funFormData: {
        apiId: '',
        apiName: '',
        apiUrl: '',
        apiClient: '',
        apiService: '',
        apiType: 'URL'
      },
      apiFormRules: {
        apiName: [
          { required: true, message: this.$t('common.pleaseInput') },
          { validator: checkApiName, trigger: 'blur' }
        ],
        apiUrl: [{ required: true, message: this.$t('common.pleaseInput') }]
      }
    }
  },

  computed: {
    dialogVisible: {
      get: function () {
        return this.visible
      },
      set: function (val) {
        this.$emit('update:visible', val)
      }
    },
    pageFlag () {
      // 新增、编辑、只读
      // flag: ['add', 'edit', 'view']
      return {
        isAdd: this.flag === 'add',
        isEdit: this.flag === 'edit',
        isView: ['view', 'test'].includes(this.flag),
        isTest: this.flag === 'test'
      }
    }
  },

  mounted () {
    if (!this.pageFlag.isAdd && (this.editRow || {}).apiId) {
      this.getDetailData()
    }
  },

  methods: {
    /* 查询 */
    async getDetailData () {
      const response = await quoteBuyerHttp.fun.get(this.editRow.apiId)
      if (response && response.data) {
        const { apiDetails = [], ...param } = response.data
        this.apiDetails = apiDetails

        const formData = {}
        for (const key in this.funFormData) {
          if (param[key] || param[key] === 0) {
            formData[key] = param[key]
          }
        }
        this.funFormData = {
          ...this.funFormData,
          ...formData
        }
      }
    },

    /* 新增 */
    addRow () {
      this.apiDetails.push({
        argDesc: '',
        argName: '',
        argType: '',
        required: 'Y'
      })
    },

    /* 删除 */
    deleteRow ($index) {
      this.apiDetails.splice($index, 1)
    },

    /* API测试 */
    async apiTest () {
      if (this.apiDetails.length > 0) {
        // 校验参数
        for (const item of this.apiDetails) {
          if (item.required === 'Y' && (!item.apiTestArg && item.apiTestArg !== 0)) {
            // 必填，但为空 请输入所有必填的入参
            this.$message.warning(this.$t('quoteTemplate.fun.apiTestArgRequired'))
            return
          }
        }
      }

      let submitData = this.apiDetails.reduce((obj, cur) => {
        return {
          ...obj,
          [cur.argName]: cur.apiTestArg
        }
      }, {})
      const response = await quoteBuyerHttp.fun.test(this.funFormData.apiId, submitData)
      if (response) {
        this.$alert(
          `${this.$t('quoteTemplate.fun.apiTestArgResultTitle')}：${response.data}`,
          this.$t('quoteTemplate.fun.apiTestArgResultTitle')
        )
      }
    },

    /* 提交 / 保存 */
    async saveOrSubmit (type) {
      const valid = await this.$refs.apiForm.validate().catch(() => this.__focus_error__())
      if (!valid) {
        return
      }

      // 参数非必填。如果存在行就校验必填列
      if (
        this.apiDetails.length > 0 &&
        !validateRequiredColumn(
          this.$refs.apiDetailsTable,
          this.apiDetails,
          {
            validateScope: true,
            tableTitle: this.$t('quoteTemplate.fun.arg')
          }
        )
      ) {
        return
      }

      let submitData = {
        api: {
          ...this.funFormData,
          apiDetails: this.apiDetails
        },
        tempSave: type === 'SAVE'
      }

      const response = await quoteBuyerHttp.fun.edit(submitData)
      if (response) {
        this.$message.success(type === 'SAVE' ? this.$t('common.successSave') : this.$t('common.successSubmit'))
        this.$emit('submit-success')
        this.dialogVisible = false
      }
    }
  }
}
</script>
