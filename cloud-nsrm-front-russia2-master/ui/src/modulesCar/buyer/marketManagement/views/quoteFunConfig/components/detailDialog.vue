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
          <el-form-item :label="$t('quoteTemplate.fun.name')" prop="functionName">
            <el-input v-model="funFormData.functionName" />
          </el-form-item>
        </SrmCol>

        <!--函数类型-->
        <SrmCol :init-col="3">
          <el-form-item :label="$t('quoteTemplate.fun.type')" prop="functionType">
            <DictSelect
              v-model="funFormData.functionType"
              code="SOU_QUOTE_TEMP_API_TYPE"
            />
          </el-form-item>
        </SrmCol>

        <!--函数类型 Service-->
        <template v-if="funFormData.functionType === 'SERVICE'">
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
        <SrmCol v-if="funFormData.functionType === 'URL'" :init-col="3">
          <el-form-item :label="$t('quoteTemplate.fun.url')" prop="urlAddress">
            <el-input v-model="funFormData.urlAddress" />
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
        prop="paramName"
        :label="$t('quoteTemplate.fun.argName')"
        min-width="120"
        :render-header="_addStarToColumn"
      >
        <template v-slot="{ row }">
          <el-input v-model="row.paramName" :disabled="pageFlag.isView" />
        </template>
      </el-table-column>

      <!--参数说明-->
      <el-table-column
        align="center"
        prop="paramDesc"
        :label="$t('quoteTemplate.fun.argDesc')"
        min-width="120"
        :render-header="_addStarToColumn"
      >
        <template v-slot="{ row }">
          <el-input v-model="row.paramDesc" :disabled="pageFlag.isView" />
        </template>
      </el-table-column>

      <!--参数类型-->
      <el-table-column
        align="center"
        prop="paramType"
        :label="$t('quoteTemplate.fun.argType')"
        min-width="120"
        :render-header="_addStarToColumn"
      >
        <template v-slot="{ row }">
          <DictSelect
            v-model="row.paramType"
            code="SOU_QUOTE_TEMP_API_ARG_TYPE"
            :disabled="pageFlag.isView"
          />
        </template>
      </el-table-column>

      <!--必填-->
      <el-table-column
        align="center"
        prop="notNull"
        :label="$t('quoteTemplate.fun.required')"
        min-width="90"
        :render-header="_addStarToColumn"
      >
        <template v-slot="{ row }">
          <el-checkbox
            v-model="row.notNull"
            :true-label="1"
            :false-label="0"
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
        <template v-slot="{ $index,row }">
          <!--删除-->
          <el-button type="text" @click="deleteRow(row,$index)">
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
        <!-- <el-button
          type="primary"
          @click="saveOrSubmit('SUBMIT')"
        >
          {{ $t('common.submit') }}
        </el-button> -->
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
import { validateRequiredColumn } from '@/library/mixins/addStarToColumn'
import { deepClone } from '@/utils'
import { transformMQL } from '@/library/utils/util'

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
        callback(new Error('函数名称只能输入英文字母数组下划线以及中文！'))
      }
      callback()
    }
    return {
      apiDetails: [],
      funFormData: {
        apiFunctionId: '',
        functionName: '',
        urlAddress: '',
        apiClient: '',
        apiService: '',
        functionType: 'URL',
        status: 'DRAFT'
      },
      apiFormRules: {
        functionName: [
          { required: true, message: this.$t('common.pleaseInput') },
          { validator: checkApiName, trigger: 'blur' }
        ],
        urlAddress: [{ required: true, message: this.$t('common.pleaseInput') }]
      },
      deleteLineCache: []
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
    if (!this.pageFlag.isAdd && (this.editRow || {}).apiFunctionId) {
      this.getDetailData()
    }
  },

  methods: {
    /* 查询 */
    getDetailData () {
        const { costApiFunctionLines = [], ...param } = deepClone(this.editRow)
        this.apiDetails = costApiFunctionLines

        this.funFormData = {
          ...param
        }
      
    },

    /* 新增 */
    addRow () {
      this.apiDetails.push({
        paramDesc: '',
        paramName: '',
        paramType: '',
        notNull: 0
      })
    },

    /* 删除 */
    deleteRow (row,$index) {
      this.deleteLineCache.push(row)
      this.apiDetails.splice($index, 1)
    },

    /* API测试 */
    async apiTest () {
      if (this.apiDetails.length > 0) {
        // 校验参数
        for (const item of this.apiDetails) {
          if (item.notNull === 'Y' && (!item.apiTestArg && item.apiTestArg !== 0)) {
            // 必填，但为空 请输入所有必填的入参
            this.$message.warning(this.$t('quoteTemplate.fun.apiTestArgRequired'))
            return
          }
        }
      }

      let submitData = this.apiDetails.reduce((obj, cur) => {
        return {
          ...obj,
          [cur.paramName]: cur.apiTestArg
        }
      }, {})
      const response = await this.$api.cost.buyer.quoteTemp.api.test(this.funFormData.apiFunctionId, submitData)
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

      let costApiFunctionLines = deepClone(this.apiDetails)
      if(this.deleteLineCache.length){
        this.deleteLineCache.forEach(item=>{
          costApiFunctionLines.push({'$delete': item.apiFunctionLineId})
        })
      }

      let submitData = {
        ...this.funFormData,
        costApiFunctionLines: costApiFunctionLines
      }
      let formData = transformMQL.save('CostApiFunction',[submitData],'customSave')
      this.$http({
        url: '/api-cost/api-ql/CostApiFunction/customSave',
        method: 'POST',
        data: formData,
        loading: true,
      }).then(res => {
        this.$message.success(this.$t('common.successSave'))
        this.$emit('submit-success')
        this.deleteLineCache = []
        this.dialogVisible = false
      })
    }
  }
}
</script>
