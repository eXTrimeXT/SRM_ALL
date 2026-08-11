<template>
  <el-container
    class="the_contractTemplateEdit_wrapper"
    direction="vertical"
  >
    <el-main>
      <el-form
        ref="mode"
        :rules="rules"
        :model="form"
        label-width="80px"
        label-position="top"
        class="form-incontainer"
      >
        <srm-row>
          <srm-col>
            <el-form-item
              :label="$t('contractMod.templHeadId')"
              prop="modelName"
              :label-width="formLabelWidth"
            >
              <el-input
                v-model="form.modelName"
                :disabled="editable"
              />
            </el-form-item>
          </srm-col>
          <srm-col>
            <el-form-item
              :label="$t('contractMod.contractStatus')"
              prop="status"
              :label-width="formLabelWidth"
            >
              <DictSelect
                v-model="form.status"
                code="CONTRACT_MODEL_STATUS"
                disabled
              />
            </el-form-item>
          </srm-col>
          <srm-col>
            <el-form-item
              :label="$t('contractMod.contractType')"
              prop="modelType"
              :label-width="formLabelWidth"
            >
              <DictSelect
                v-model="form.modelType"
                code="ELEM_CONTRACT_TYPE"
                :disabled="editable"
                @change="initData"
              />
            </el-form-item>
          </srm-col>
          <srm-col>
            <el-form-item
              :label="$t('contractMod.controlMethod')"
              prop="ceeaControlMethod"
              :label-width="formLabelWidth"
            >
              <DictSelect
                v-model="form.ceeaControlMethod"
                code="MANAGEMENT_CONTROL_MODEL"
              />
            </el-form-item>
          </srm-col>
          <srm-col class="sRight">
            <el-form-item
              :label="$t('dataConfMod.expiryDate')"
              prop="allDate"
              :label-width="formLabelWidth"
            >
            <!-- 生效日期 - 失效日期 -->
              <el-date-picker
                v-model="form.allDate"
                type="daterange"
                :format="$formatDatePicker"
                :range-separator="$t('components.to')"
                :start-placeholder="$t('vendorMod.startDate')"
                :end-placeholder="$t('vendorMod.endDate')"
              />
            </el-form-item>
          </srm-col>
        </srm-row>
        <div class="button_group">
          <el-button
            v-if="!editable"
            type="primary"
            @click="showFixedElem"
          >
            {{
              $t('contractMod.fixedElement')
            }}
          </el-button>
          <el-button
            v-if="!editable"
            type="primary"
            @click="showTypeElem"
          >
            {{
              $t('contractMod.contractTypeElement')
            }}
          </el-button>
        </div>
        <srm-row style="margin: 0 !important;">
          <el-form-item
            :label="$t('contractMod.tempContent')"
            prop="content"
            :label-width="formLabelWidth"
          >
            <div style="width: 215mm; position: relative">
              <Tinymce
                id="tinymceContractMode"
                v-model="form.content"
                :height="460"
                @setup="ready"
              />
            </div>
          </el-form-item>
        </srm-row>
      </el-form>
      <CToolbar>
        <template slot="right">
          <el-button
            @click="cancel"
          >
            {{
              !editable ? $t('common.cancel') : $t('common.close')
            }}
          </el-button>
          <el-button
            type="primary"
            @click="preview"
          >
            {{ $t('common.preview') }}
          </el-button>
          <el-button
            v-if="showInvalid"
            type="primary"
            @click="failure"
          >
            {{
              $t('common.inactive')
            }}
          </el-button>
          <el-button
            v-if="showFreeze"
            type="primary"
            @click="freeze"
          >
            {{
              $t('contractMod.freeze')
            }}
          </el-button>
          <el-button
            v-if="!editable"
            type="primary"
            @click="submit"
          >
            {{
              $t('common.staging')
            }}
          </el-button>
          <el-button
            v-if="showValid"
            type="primary"
            @click="takeEffect"
          >
            {{
              $t('common.active')
            }}
          </el-button>
        </template>
      </CToolbar>
      <srm-dialog
        size="middle"
        :visible.sync="fixedPlaceholderVisible"
        :title="$t('contractMod.fixedElement')"
      >
        <BaseTable
          stripe
          :data="fixedElemTableData"
          :columns="fixedElemColumns"
          columns-name="fixedElemColumns"
          :empty-text="$t('components.noData')"
          border
          height="400"
          @insertFixedElem="insertFixedElem"
        />
      </srm-dialog>
      <srm-dialog
        size="middle"
        :visible.sync="typeElemVisible"
        :title="$t('contractMod.contractTypeElement')"
      >
        <BaseTable
          stripe
          :data="typeElemTableData"
          :columns="typeElemColumns"
          columns-name="typeElemColumns"
          :empty-text="$t('components.noData')"
          border
          height="400"
          @insertTypeElem="insertTypeElem"
        />
      </srm-dialog>
    </el-main>
  </el-container>
</template>

<script>
import Tinymce from '@/components/Tinymce'
import { tabTodoMixin } from '@/utils/mixins'
import CToolbar from 'lib@/components/c-toolbar'
import Preview from './preview'
import cloneDeep from 'lodash/cloneDeep'
import BaseTable from 'lib@/components/BaseTable'
import { componentMap } from 'modb@/contractManagement/views/contractManager/parser/enum'
import FIXED_ELEM from './fixedElem'
import uniqueId from 'lodash/uniqueId'
import DictSelect from '@/library/components/c-select/dict-select'
import { createDictClass } from '@/library/utils/dict/dict-utils'
import { contractManagement } from 'modb@/contractManagement/api/index'
const dictClass = createDictClass({
    COMPONENT_TYPE: Object.keys(componentMap).map((i) => ({
      id: i,
      label: componentMap[i],
      value: i
    }))
  },
  false)

export default {
  name: 'Edit',
  components: { Tinymce, CToolbar, BaseTable, DictSelect },
  mixins: [tabTodoMixin],
  data () {
    return {
      switchClick: 0,
      dictClass: dictClass,
      editorInstance: null,
      formLabelWidth: '100px',
      showInvalid: false,
      showFreeze: false,
      showValid: false,
      form: {
        status: '',
        allDate: []
      },
      rules: {
        modelType: [{ required: true, message: this.$t('contractMod.pleaseFillIn') }],
        modelName: [{ required: true, message: this.$t('contractMod.pleaseFillIn') }]
      },
      fixedElemTableData: FIXED_ELEM,
      fixedElemColumns: [
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: (t) => t.$t('contractMod.order'),
            type: 'index'
          }
        },
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: (t) => t.$t('contractMod.elemName'),
            prop: 'elemName'
          }
        },
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: (t) => t.$t('contractMod.elemCode'),
            prop: 'elemCode'
          }
        },
        {
          attrs: {
            align: 'center',
            label: (t) => t.$t('common.operation'),
            fixed: 'right',
            width: 80
          },
          operations: [
            {
              key: 'insertFixedElem',
              event: 'insertFixedElem',
              name: this.$t('contractMod.insert'),
              attrs: { type: 'text' }
            }
          ]
        }
      ],
      typeElemTableData: [],
      typeElemColumns: [
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: (t) => t.$t('contractMod.order'),
            type: 'index'
          }
        },
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: (t) => t.$t('contractMod.elemName'),
            prop: 'elemName'
          }
        },
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: (t) => t.$t('contractMod.elemCode'),
            prop: 'elemCode'
          }
        },
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: (t) => t.$t('contractMod.addMethod'),
            prop: 'addMethod',
            // formatter: (row, column, cellValue, index) => this.$getDictLabel('COMPONENT_TYPE', cellValue)
            formatter: (val) => {
              return this.componentFunction(val.addMethod)
            }
          }
        },
        {
          attrs: {
            align: 'center',
            label: (t) => t.$t('common.operation'),
            fixed: 'right',
            width: 80
          },
          operations: [
            {
              key: 'insertTypeElem',
              event: 'insertTypeElem',
              name: this.$t('contractMod.insert'),
              attrs: { type: 'text' }
            }
          ]
        }
      ],
      _lastElemContractType: null,
      fixedPlaceholderVisible: false,
      typeElemVisible: false
    }
  },
  computed: {
    editable () {
      console.log('editable', this.form.status)
      if (!this.form.status || ['DRAFT', 'FREEZE'].includes(this.form.status)) {
        return false
      }
      return true
    }
  },
  watch: {
    'form.status': {
      handler (value) {
        console.log('status change:', value)
        this.showValid = ['DRAFT', 'FREEZE'].includes(this.form.status)
        this.showInvalid = value === 'VALID'
        this.showFreeze = value === 'VALID'
      },
      immediate: true,
      deep: true
    },
    'form.allDate': {
      handler (value) {
        console.log('allDate change:', value)
        try {
          this.form.startDate = value[0]
          this.form.endDate = value[1]
        } catch (error) {

        }
      },
      immediate: true,
      deep: true
    }
  },
  mounted () {
    this.$nextTick(() => {
      console.log(this.form.startDate)
      if (this.form.endDate == null) {
        this.$set(this.form, 'allDate', [])
      } else {
        this.$set(this.form, 'allDate', [this.form.startDate, this.form.endDate])
      }
      console.log(this.form.allDate)
    })
  },
  methods: {
    componentFunction (val) {
      let listType = this.dictClass.dictStore.$data.dictStates.COMPONENT_TYPE
      let label = ''
      console.log(listType)
      console.log(val)
      listType.forEach(e => {
        if (e.value == val) {
          label = e.label
        }
      })
      return label
    },
    failure () {
      contractManagement.failure(this.form.modelHeadId).then((res) => {
        this.$message.success(res.message)
        this.getById(res.data)
      })
    },
    takeEffect () {
      if (this.switchClick == 0) {
        this.switchClick = 1
        let _this = this
        setTimeout(function () {
          _this.switchClick = 0
        }, 3000)
        this.$refs.mode.validate((valid) => {
          if (valid) {
            const { row, flag } = this.$attrs.params
            const { startDate, endDate, ...rest } = this.form
            const formData = {
              ...rest,
              startDate: null,
              endDate: null
            }
            if (startDate) {
              formData.startDate = this.$dayjs(startDate).format('YYYY-MM-DD')
            }
            if (endDate) {
              formData.endDate = this.$dayjs(endDate).format('YYYY-MM-DD')
            }
            if (flag === 'add' && !this.form.modelHeadId) {
              contractManagement.add(formData).then((res) => {
                this.$message({
                  type: 'success',
                  message: res.message
                })
                contractManagement.takeEffect(this.form.modelHeadId).then((res) => {
                  this.$message.success(res.message)
                  this.editorInstance.setMode('readonly')
                  this.getById(res.data)
                })
              })
            } else {
              contractManagement.modifyAll(formData).then((res) => {
                this.$message({
                  type: 'success',
                  message: res.message
                })
                contractManagement.takeEffect(this.form.modelHeadId).then((res) => {
                  this.$message.success(res.message)
                  this.editorInstance.setMode('readonly')
                  this.getById(res.data)
                })
              })
            }
          } else {
            this.$message({
              message: this.$t('vendorMod.pleasefinishRequired'), // '请输入单据必填信息'
              type: 'error'
            })
          }
        })
      } else {
        this.$message({
          message: this.$t('dataConfMod.notClickFrequently'),
          type: 'error'
        })
      }
    },
    freeze () {
      contractManagement.freeze(this.form.modelHeadId).then((res) => {
        this.$message.success(res.message)
        this.editorInstance.setMode('design')
        this.getById(res.data)
      })
    },
    insertFixedElem (scope) {
      console.log('fixedElem:', scope)
      this.insert(scope)
      this.fixedPlaceholderVisible = false
    },
    insertTypeElem (scope) {
      this.insert(scope)
      this.typeElemVisible = false
    },
    showTypeElem () {
      if (!this.form.modelType) {
        this.$message.warning(this.$t('contractMod.msgSelContractType'))
        return
      }
      this.typeElemVisible = true
    },
    fetchData (value) {
      if (!value) {
        this.typeElemTableData = []
        return
      }
      contractManagement.typeElement.queryByValid(value).then((res) => {
        this.typeElemTableData = res.data
      })
    },
    initData (value, force = false) {
      if (force) {
        this.fetchData(value)
        this._lastElemContractType = value
      } else if (this._lastElemContractType && this.form.content) {
        this.$confirm(this.$t('contractMod.clearModelMsg'), {
          confirmButtonText: this.$t('common.confirm'),
          cancelButtonText: this.$t('common.cancel'),
          type: 'warning'
        })
          .then(() => {
            this.fetchData(value)
            this.form.content = null
          })
          .catch(() => {
            console.log('点击取消')
            this.form.modelType = this._lastElemContractType
          })
      } else {
        this.fetchData(value)
        this._lastElemContractType = value
      }
    },
    getById (modelHeadId) {
      contractManagement.getById(modelHeadId).then((res) => {
        this.form = Object.assign(this.form, res.data)
        this.$set(this.form, 'status', res.data.status)
      })
    },
    submit () {
      if (this.switchClick == 0) {
        this.switchClick = 1
        let _this = this
        setTimeout(function () {
          _this.switchClick = 0
        }, 3000)
        this.$refs.mode.validate((valid) => {
          if (valid) {
            const { row, flag } = this.$attrs.params
            const { startDate, endDate, ...rest } = this.form
            const formData = {
              ...rest,
              startDate: null,
              endDate: null
            }
            if (startDate) {
              formData.startDate = this.$dayjs(startDate).format('YYYY-MM-DD')
            }
            if (endDate) {
              formData.endDate = this.$dayjs(endDate).format('YYYY-MM-DD')
            }
            if (flag === 'add' && !this.form.modelHeadId) {
              contractManagement.add(formData).then((res) => {
                this.$message({
                  type: 'success',
                  message: res.message
                })
                this.cancel()
                this.getById(res.data.id)
              })
            } else {
              contractManagement.modifyAll(formData).then((res) => {
                this.$message({
                  type: 'success',
                  message: res.message
                })
                this.getById(res.data.id)
                this.cancel()
              })
            }
          } else {
            this.$message({
              message: this.$t('vendorMod.pleasefinishRequired'), // '请输入单据必填信息'
              type: 'error'
            })
          }
        })
      } else {
        this.$message({
          message: this.$t('dataConfMod.notClickFrequently'),
          type: 'error'
        })
      }
    },
    cancel () {
      this.$confirm(this.$t('common.cancelDelete2'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          const { row, flag } = this.$attrs.params
          if (flag == 'add') {
            this.$emit('tab-remove', 'add')
          } else {
            this.$emit('tab-remove', `${flag}_${row.modelHeadId ? row.modelHeadId : ''}`)
          }
          this.__setTabTodo('contractTemplateList.getQuerydata')
        })
    },
    preview () {
      const row = this.form
      const tab = {
        component: Preview,
        params: { row },
        title: this.$t('common.preview') + `${row.modelName ? '-' + row.modelName : ''}`,
        name: `preview_${row.modelHeadId ? row.modelHeadId : ''}`
      }
      this.$emit('tab-add', tab)
    },
    showFixedElem () {
      this.fixedPlaceholderVisible = true
    },
    insert (scope) {
      const { elemName, elemCode, addMethod } = scope.row
      const _uniqueId = uniqueId(`key_${Date.now()}_`)
      const content = `\$\{[${elemName}]${elemCode}:${_uniqueId}\}`
      this.editorInstance.focus()
      this.editorInstance.execCommand('mceInsertRawHTML', false, content)
    },
    ready (editorInstance) {
      console.log(editorInstance)
      const { flag, row } = this.$attrs.params
      if (flag === 'read') {
        editorInstance.setMode('readonly')
      }
      this.editorInstance = editorInstance
      console.log('key: ' + editorInstance.key)

      if (flag !== 'add') {
        this.$nextTick(() => {
          this.form = cloneDeep(row)
          this.initData(row.modelType, true)
        })
      }
    }
  }
}
</script>
<style scoped lang="scss">
.the_contractTemplateEdit_wrapper {
  .form-incontainer {
    padding-top:10px;
    padding-bottom: 45px;
  }
  .button_group {
    padding: 10px 0px;
  }
}
.sRight{
  padding-right:8px !important;
}
:deep(.el-form-item__content>div){
  width: 100% !important;
}
</style>
