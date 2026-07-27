<template>
  <el-container
    class="the_onlyofficeContractModeManagerEdit_wrapper"
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
              <dict-select
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
              <dict-select
                v-model="form.modelType"
                code="ELEM_CONTRACT_TYPE"
                :disabled="editable"
                @change="fetchData"
              />
            </el-form-item>
          </srm-col>
          <srm-col>
            <el-form-item
              :label="$t('contractMod.controlMethod')"
              prop="ceeaControlMethod"
              :label-width="formLabelWidth"
            >
              <dict-select
                v-model="form.ceeaControlMethod"
                code="MANAGEMENT_CONTROL_MODEL"
              />
            </el-form-item>
          </srm-col>
          <srm-col>
            <el-form-item
              :label="$t('contractMod.startDate')"
              prop="startDate"
              :label-width="formLabelWidth"
            >
              <el-date-picker
                v-model="form.startDate"
                :disabled="editable"
              />
            </el-form-item>
          </srm-col>
          <srm-col>
            <el-form-item
              :label="$t('contractMod.endDate')"
              prop="endDate"
              :label-width="formLabelWidth"
            >
              <el-date-picker
                v-model="form.endDate"
                :disabled="editable"
              />
            </el-form-item>
          </srm-col>
        </srm-row>
        <srm-row>
          <el-form-item
            :label="$t('contractMod.tempContent')"
            prop="content"
            :label-width="formLabelWidth"
          >
            <p
              v-if="iframeSrc"
              style="color: red"
            >
              * 编辑后自动保存
            </p>
            <iframe
              v-if="iframeSrc"
              width="100%"
              height="800px"
              align="top"
              frameborder="0"
              allowfullscreen=""
              onmousewheel=""
              allow="autoplay; camera; microphone; display-capture"
              :src="iframeSrc"
            />
            <p
              v-else
              style="color: red"
            >
              * 暂存之后方可开始编辑模板
            </p>
          </el-form-item>
        </srm-row>
      </el-form>
      <c-toolbar>
        <template slot="right">
          <el-button
            @click="cancel"
          >
            {{
              !editable ? $t('common.cancel') : $t('common.close')
            }}
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
      </c-toolbar>
    </el-main>
  </el-container>
</template>

<script>
import { tabTodoMixin } from '@/utils/mixins'
import CToolbar from 'lib@/components/c-toolbar'
import { getToken } from '@/utils/auth'
import cloneDeep from 'lodash/cloneDeep'
import DictSelect from '@/library/components/c-select/dict-select'
import { sysPrefix } from '@/config/ipConfig'

export default {
  name: 'Edit',
  components: { CToolbar, DictSelect },
  mixins: [tabTodoMixin],
  data () {
    return {
      formLabelWidth: '100px',
      showInvalid: false,
      showFreeze: false,
      showValid: false,
      iframeSrc: null,
      form: {
        status: '',
        endDate: '',
        startDate: '',
        ceeaControlMethod: '',
        modelType: '',
        modelName: ''
      },
      rules: {
        modelType: [{ required: true, message: this.$t('contractMod.pleaseFillIn') }],
        startDate: [{ required: true, message: this.$t('contractMod.pleaseFillIn') }],
        modelName: [{ required: true, message: this.$t('contractMod.pleaseFillIn') }]
      }
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
    }
  },
  created () {
    if (this.$attrs.params.flag !== 'add') {
      this.getById(this.$attrs.params.row.modelHeadId)
    }
  },
  methods: {
    failure () {
      this.$api.cm.buyer.main.failure(this.form.modelHeadId).then((res) => {
        this.$message.success(res.message)
        this.getById(res.data)
      })
    },
    takeEffect () {
      this.$api.cm.buyer.main.takeEffect(this.form.modelHeadId).then((res) => {
        // this.$message.success(res.message);
        this.$confirm('必须关闭当前页面之后1分钟才会生效，是否关闭页面', '提示', {
          confirmButtonText: this.$t('common.confirm'),
          cancelButtonText: this.$t('common.cancel'),
          type: 'warning'
        })
          .then(() => {
            this.cancel()
          })
          .catch(() => {})
        // this.getById(res.data);
      })
    },
    freeze () {
      this.$api.cm.buyer.main.freeze(this.form.modelHeadId).then((res) => {
        this.$message.success(res.message)
        this.getById(res.data)
      })
    },
    fetchData (value) {
      if (!value) {
        this.typeElemTableData = []
        return
      }
      this.$api.cm.buyer.main.typeElement.queryByValid(value).then((res) => {
        this.typeElemTableData = res.data
      })
    },
    getById (modelHeadId) {
      this.$api.cm.buyer.main.getById(modelHeadId).then((res) => {
        this.form = Object.assign(this.form, res.data)
        const { status, fileuploadId } = res.data
        this.$set(this.form, 'status', status)
        if (fileuploadId) {
          const accessToken = getToken()
          if (['DRAFT', 'FREEZE'].includes(status)) {
            // 冻结&拟定模板之后可以编辑文档
            this.iframeSrc = `${sysPrefix()}/api-onlineview/EditorServlet?uploadId=${fileuploadId}&type=desktop&mode=edit&access_token=${accessToken}&model_head_id=${modelHeadId}&shouldReplace=false`
          } else {
            this.iframeSrc = `${sysPrefix()}/api-onlineview/EditorServlet?uploadId=${fileuploadId}&mode=embedded&access_token=${accessToken}&shouldReplace=false`
          }
        }
      })
    },
    submit () {
      this.$refs.mode.validate((valid) => {
        if (valid) {
          const { flag } = this.$attrs.params
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
            this.$api.cm.buyer.main.onlyofficeAdd(formData).then((res) => {
              this.$message({
                type: 'success',
                message: res.message
              })
              this.getById(res.data.id)
            })
          } else {
            this.$api.cm.buyer.main.modifyAll(formData).then((res) => {
              this.$message({
                type: 'success',
                message: res.message
              })
              this.getById(res.data.id)
            })
          }
        } else {
          this.$message({
            message: this.$t('vendorMod.pleasefinishRequired'), // '请输入单据必填信息'
            type: 'error'
          })
        }
      })
    },
    cancel () {
      const { row, flag } = this.$attrs.params
      if (flag == 'add') {
        this.$emit('tab-remove', 'add')
      } else {
        this.$emit('tab-remove', `${flag}_${row.modelHeadId ? row.modelHeadId : ''}`)
      }
      this.__setTabTodo('contractTemplateList.getQuerydata')
    }
  }
}
</script>
<style scoped lang="scss">
.the_onlyofficeContractModeManagerEdit_wrapper {
  .form-incontainer {
    padding-bottom: 45px;
  }
  .button_group {
    padding: 10px 25px;
  }
}
</style>
