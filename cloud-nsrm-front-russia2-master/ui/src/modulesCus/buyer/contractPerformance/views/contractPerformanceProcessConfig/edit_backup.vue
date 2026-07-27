<template>
  <el-container
    class="contractPerformanceProcessConfigEdit"
    direction="vertical"
  >
    <el-main>
      <div class="form-container">
        <el-form
          ref="form"
          :model="form"
          :disabled="readOnly"
        >
          <srm-row>
            <srm-col>
              <el-form-item
                :label="$t('contract_mod.contractType')"
                prop="contractType"
                :rules="[{ required: true, message: $t('contract_mod.required') }]"
              >
                <dict-select
                  v-model="form.contractType"
                  code="ELEM_CONTRACT_TYPE"
                />
              </el-form-item>
            </srm-col>
            <srm-col>
              <el-form-item
                :label="$t('contract_mod.processNum')"
                prop="processNum"
              >
                <el-input
                  v-model="form.processNum"
                  disabled
                />
              </el-form-item>
            </srm-col>
            <srm-col>
              <el-form-item
                :label="$t('contract_mod.configStatus')"
                prop="status"
              >
                <dict-select
                  v-model="form.status"
                  code="PERFORMANCE_OF_CONTRACT"
                  disabled
                />
              </el-form-item>
            </srm-col>
            <srm-col>
              <el-form-item
                :label="$t('contract_mod.templateName')"
                prop="templateName"
                :rules="[{ required: true, message: $t('contract_mod.required') }]"
              >
                <el-input
                  v-model="form.templateName"
                  :maxlength="30"
                  show-word-limit
                />
              </el-form-item>
            </srm-col>
            <srm-col>
              <el-form-item
                :label="$t('common.creator')"
                prop="createdBy"
              >
                <el-input
                  v-model="form.createdBy"
                  disabled
                />
              </el-form-item>
            </srm-col>
            <srm-col :init-col="4">
              <el-form-item
                :label="$t('common.creationTime')"
                prop="creationDate"
              >
                <el-date-picker
                  v-model="form.creationDate"
                  :format="$formatDatePicker"
                  disabled
                />
              </el-form-item>
            </srm-col>
          </srm-row>
        </el-form>
      </div>
      <div class="btn-group">
        <el-button
          type="primary"
          class="detail-pbtn"
          :disabled="readOnly"
          @click="addMilestoneLine"
        >
          {{ $t('contract_mod.addProcessNode') }}
        </el-button>
      </div>
      <el-table
        style="width: 100%"
        border
        max-height="250px"
        :data="form.performTemplLineList"
      >
        <el-table-column type="index" />
        <el-table-column
          align="center"
          :render-header="_addStarToColumn"
          :label="$t('contract_mod.processNodeName')"
        >
          <template slot-scope="scope">
            <dict-select
              v-model="scope.row.milestoneType"
              :disabled="readOnly"
              code="MILESTONE_SCHEDULE"
            />
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          :render-header="_addStarToColumn"
          :label="$t('contract_mod.fileTpl')"
        >
          <template slot-scope="scope">
            <SrmCommonFile
              :extra-data="fileInfo"
              :default-file="{
                fileId: scope.row.fileId,
                fileName: scope.row.fileName
              }"
              :readonly="readOnly"
              @on-change="({file}) => uploadSuccess(file,scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          :label="$t('contract_mod.payTpl')"
        >
          <template slot-scope="scope">
            <el-button
              type="text"
              @click="editPayTpl(scope)"
            >
              {{ readOnly ? $t('common.view') : $t('common.edit') }}
            </el-button>
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          :label="$t('components.headers.operation')"
        >
          <template slot-scope="scope">
            <el-button
              type="text"
              :disabled="readOnly"
              @click="deleteItem(scope)"
            >
              {{ $t('common.delete') }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <c-toolbar>
        <template slot="right">
          <el-button
            @click="cancel"
          >
            {{ $t("common.cancel") }}
          </el-button>
          <el-button
            type="primary"
            :disabled="readOnly"
            @click="save('SAVE')"
          >
            {{ $t("common.staging") }}
          </el-button>
          <el-button
            type="primary"
            :disabled="readOnly"
            @click="save('SUBMIT')"
          >
            {{ $t("common.submit") }}
          </el-button>
        </template>
      </c-toolbar>
    </el-main>
    <srm-dialog
      :title="$t('contract_mod.editPayTpl')"
      size="large"
      :visible.sync="showPayTpl"
      :close-on-click-modal="false"
    >
      <div style="margin-bottom: 16px;">
        <el-button
          type="primary"
          class="detail-pbtn"
          :disabled="readOnly"
          @click="addPayTplLine"
        >
          {{ $t('common.add') }}
        </el-button>
      </div>
      <el-table
        style="width: 100%"
        border
        max-height="250px"
        :data="payTpl"
      >
        <el-table-column
          align="center"
          :render-header="_addStarToColumn"
          :label="$t('contract_mod.fieldType')"
        >
          <template slot-scope="scope">
            <dict-select
              v-model="scope.row.fieldType"
              code="FIELD_TYPE"
              :disabled="readOnly"
            />
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          :label="$t('contract_mod.fieldOptions')"
        >
          <template slot-scope="scope">
            <el-button
              type="text"
              @click="editFieldOptions(scope)"
            >
              {{ readOnly ? $t('common.view') : $t('common.edit') }}
            </el-button>
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          :render-header="_addStarToColumn"
          :label="$t('contract_mod.fieldName')"
        >
          <template slot-scope="scope">
            <el-input
              v-model="scope.row.fieldName"
              clearable
              :disabled="readOnly"
            />
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          :render-header="_addStarToColumn"
          :label="$t('contract_mod.fieldCode')"
        >
          <template slot-scope="scope">
            <el-input
              v-model="scope.row.fieldCode"
              clearable
              :disabled="readOnly"
            />
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          :label="$t('contract_mod.required')"
        >
          <template slot-scope="scope">
            <el-checkbox
              v-model="scope.row.required"
              true-label="true"
              false-label="false"
              :disabled="readOnly"
            />
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          :label="$t('components.headers.operation')"
        >
          <template slot-scope="scope">
            <el-button
              type="text"
              :disabled="readOnly"
              @click="deletePayItem(scope)"
            >
              {{ $t('common.delete') }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <el-button @click="showPayTpl = false">
          {{
            $t("common.cancel")
          }}
        </el-button>
        <el-button
          type="primary"
          :disabled="readOnly"
          @click="savePayTpl"
        >
          {{
            $t("common.confirm")
          }}
        </el-button>
      </template>
    </srm-dialog>
    <srm-dialog
      :title="$t('contract_mod.editFieldOptions')"
      size="large"
      :visible.sync="showFieldOptions"
      :close-on-click-modal="false"
    >
      <i-field-options
        v-if="showFieldOptions"
        v-model="fieldOptions"
        :disabled="readOnly"
        :field-type="fieldScopeRow.fieldType"
      />
      <template #footer>
        <el-button @click="showFieldOptions = false">
          {{
            $t("common.cancel")
          }}
        </el-button>
        <el-button
          type="primary"
          :disabled="readOnly"
          @click="saveFieldOptions"
        >
          {{
            $t("common.confirm")
          }}
        </el-button>
      </template>
    </srm-dialog>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import CToolbar from 'lib@/components/c-toolbar'
import IFieldOptions from './i-field-options.vue'

export default {
  name: 'ContractPerformanceProcessConfigEdit',
  components: {
    CToolbar,
    IFieldOptions
  },
  mixins: [tabTodoMixin, tabTodoWatch],
  data () {
    return {
      showFieldOptions: false,
      fieldScopeRow: {},
      currentScopeRow: {},
      fieldOptions: '',
      showPayTpl: false,
      payTpl: [],
      readOnly: false,
      fileInfo: {
        fileModular: 'sup',
        fileFunction: 'contractPerformanceProcessConfigEdit',
        fileType: 'images'
      },
      form: {
        contractType: '',
        status: '',
        fileName: '',
        fileId: '',
        createdBy: '',
        creationDate: '',
        processNum: '',
        performTemplLineList: [],
        templateName: ''
      },
      currentScope: null
    }
  },
  computed: {},
  watch: {},
  created () {
    const { row, flag } = this.$attrs.params
    this.readOnly = flag === 'view'
    if (row.performTemplHeadId) {
      this.initFormData(row.performTemplHeadId)
    }
  },
  methods: {
    async initFormData (id) {
      const res = await this.$api.cmPerform.buyer.main.performanceTpl.getDetailById(id)
      const { contractType, performTemplLineList } = res.data
      if (this.$attrs.params.flag === 'add') {
        Object.assign(this.form, {
          contractType,
          performTemplLineList: performTemplLineList.map(
            ({ milestoneType, fileName, fileId, configList, serialNumber }) => {
              return {
                milestoneType,
                fileName,
                fileId,
                serialNumber, // 里程碑序号
                configList: configList.map(({ performTemplLineConfigId, ...rest }) => ({ ...rest }))
              }
            }
          )
        })
      }
      if (['edit', 'view'].includes(this.$attrs.params.flag)) {
        Object.assign(this.form, res.data)
      }
    },
    addPayTplLine () {
      this.payTpl.push({
        fieldCode: '',
        fieldName: '',
        fieldType: '',
        fieldOptions: '',
        required: 'true'
      })
    },
    addMilestoneLine () {
      this.form.performTemplLineList.push({
        milestoneType: '',
        fileName: '',
        fileId: '',
        configList: []
      })
    },
    editFieldOptions (scope) {
      this.showFieldOptions = true
      this.fieldOptions = scope.row.fieldOptions
      this.fieldScopeRow = scope.row
    },
    saveFieldOptions () {
      this.fieldScopeRow.fieldOptions = this.fieldOptions
      this.showFieldOptions = false
    },
    savePayTpl () {
      if (this.payTpl.some((i) => !i.fieldType || !i.fieldCode || !i.fieldName)) {
        return this.$message.error(
          `【${this.$t('contract_mod.fieldType')}】和【${this.$t(
            'contract_mod.fieldCode'
          )}】和【${this.$t('contract_mod.fieldName')}】${this.$t('contract_mod.required')}`
        )
      }
      this.currentScopeRow.configList = JSON.parse(JSON.stringify(this.payTpl))
      this.showPayTpl = false
    },
    editPayTpl (scope) {
      this.currentScopeRow = scope.row
      this.payTpl = JSON.parse(JSON.stringify(scope.row.configList || []))
      this.showPayTpl = true
    },
    deleteItem (scope) {
      this.form.performTemplLineList.splice(scope.$index, 1)
    },
    deletePayItem (scope) {
      this.payTpl.splice(scope.$index, 1)
    },
    validate () {
      return new Promise((rs) => {
        this.$refs.form.validate((flag) => rs(flag))
      })
    },
    async save (type) {
      let { addOrUpdate, submit } = this.$api.cmPerform.buyer.main.performanceTpl
      let saveMethod = type === 'SAVE' ? addOrUpdate : submit
      if (type === 'SUBMIT') {
        const flag = await this.validate()
        if (!flag) return
        if (this.form.performTemplLineList.some((i) => !i.milestoneType || !i.fileId)) {
          return this.$message.error(
            this.$t('contract_mod.processNodeName') +
              this.$t('vendorMod.and') +
              this.$t('contract_mod.fileTpl') +
              this.$t('contract_mod.required')
          )
        }
      }
      this.form.performTemplLineList.forEach((item, index) => (item.serialNumber = index + 1))
      const res = await saveMethod(this.form)
      this.$message.success(res.message)
      this.cancel()
    },
    cancel () {
      const { tabName } = this.$attrs.params
      this.$emit('tab-remove', tabName)
      this.__setTabTodo('contractPerformanceProcessConfigList.getQuerydata')
    },
    // 上传附件成功
    uploadSuccess (file, row) {
      const { fileId = '', fileName = '' } = file || {}
      row.fileId = fileId.toString()
      row.fileName = fileName
    }
  }
}
</script>
<style scoped lang="scss">
.contractPerformanceProcessConfigEdit {
  .btn-group {
    padding: 16px;
  }
}
</style>
