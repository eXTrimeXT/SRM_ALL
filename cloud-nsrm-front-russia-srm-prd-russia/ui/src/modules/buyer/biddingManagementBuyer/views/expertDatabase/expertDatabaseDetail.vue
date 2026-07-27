<template>
  <el-container
    class="flex-container expert-database-detail"
    direction="vertical"
  >
    <el-main>
      <el-form
        ref="detailForm"
        :model="detailFormData"
        :rules="detailFormRules"
        :disabled="isReadonly"
        label-position="top"
        class="detail-form-wrap form-incontainer"
      >
        <srm-row>
          <!--专家姓名-->
          <srm-col :init-col="3">
            <el-form-item
              :label="$t('bidMod.expertName')"
              prop="expertName"
            >
              <el-input v-model="detailFormData.expertName" />
            </el-form-item>
          </srm-col>

          <!--系统账号-->
          <srm-col :init-col="3">
            <el-form-item
              :label="$t('bidMod.expertAccount')"
              prop="expertAccount"
            >
              <QuickSearch
                :show-input="detailFormData.expertAccount"
                show-key="username"
                :scope-data="detailFormData"
                name="scc_rbac_user_display"
                @close-quicksearch="setUserObj"
              />
            </el-form-item>
          </srm-col>

          <!--出生年月-->
          <srm-col :init-col="3">
            <el-form-item
              :label="$t('dataConfMod.birthDate')"
              prop="birthDate"
            >
              <el-date-picker
                v-model="detailFormData.birthDate"
                type="date"
                :placeholder="$t('bidMod.datePicker')"
              />
            </el-form-item>
          </srm-col>

          <!--专家岗位-->
          <srm-col :init-col="3">
            <el-form-item
              :label="$t('bidMod.expertType')"
              prop="expertJobName"
            >
              <el-input
                v-model="detailFormData.expertJobName"
                disabled
              />
            </el-form-item>
          </srm-col>

          <!--最高学历-->
          <srm-col :init-col="3">
            <el-form-item
              :label="$t('dataConfMod.highestEducation')"
              prop="highestEducation"
            >
              <dict-select
                v-model="detailFormData.highestEducation"
                code="EXPERT_EDUCATION"
              />
            </el-form-item>
          </srm-col>

          <!--职称专业-->
          <srm-col :init-col="3">
            <el-form-item
              :label="$t('dataConfMod.profession')"
              prop="profession"
            >
              <el-input v-model="detailFormData.profession" />
            </el-form-item>
          </srm-col>

          <!--专业工作年限-->
          <srm-col :init-col="3">
            <el-form-item
              :label="$t('dataConfMod.workYears')"
              prop="workYears"
            >
              <el-input
                v-model="detailFormData.workYears"
                v-input-format="{ type: 'number' }"
              />
            </el-form-item>
          </srm-col>

          <!--电话-->
          <srm-col :init-col="3">
            <el-form-item
              :label="$t('common.phone')"
              prop="phone"
            >
              <el-input v-model="detailFormData.phone" />
            </el-form-item>
          </srm-col>

          <!--电子邮箱-->
          <srm-col :init-col="3">
            <el-form-item
              :label="$t('components.approvalHead.headers.email')"
              prop="email"
            >
              <el-input v-model="detailFormData.email" />
            </el-form-item>
          </srm-col>

          <!--登记日期-->
          <srm-col :init-col="3">
            <el-form-item
              :label="$t('bidMod.startDate')"
              prop="startDate"
            >
              <el-input
                v-model="detailFormData.startDate"
                disabled
              />
            </el-form-item>
          </srm-col>

          <!--失效日期-->
          <srm-col :init-col="3">
            <el-form-item
              :label="$t('dataConfMod.endDate')"
              prop="endDate"
            >
              <el-date-picker
                v-model="detailFormData.endDate"
                type="date"
                :placeholder="$t('bidMod.datePicker')"
              />
            </el-form-item>
          </srm-col>
        </srm-row>
      </el-form>

      <!--学历信息-->
      <education-info
        :education-list.sync="educationList"
        :is-readonly="isReadonly"
      />

      <!--工作信息-->
      <work-info
        :work-record-list.sync="workRecordList"
        :is-readonly="isReadonly"
      />

      <!--参与评标项目-->
      <partake-project
        :evaluate-project-list.sync="evaluateProjectList"
        :is-readonly="isReadonly"
      />

      <CToolbar>
        <template slot="right">
          <template v-if="!isReadonly">
            <!--保存-->
            <el-button
              type="primary"
              @click="saveOrSubmit('save')"
            >
              {{ $t('common.save') }}
            </el-button>

            <!--提交-->
            <el-button
              type="primary"
              @click="saveOrSubmit('submit')"
            >
              {{ $t('common.submit') }}
            </el-button>
          </template>

          <!--取消-->
          <el-button @click="navTabsBack">
            {{ $t('common.cancel') }}
          </el-button>
        </template>
      </CToolbar>
    </el-main>
  </el-container>
</template>

<script>
/**
 * 专家新增/编辑/查看
 */
import { isEmail, isMobile } from '@/library/utils/validate'
import { tabTodoMixin } from '@/utils/mixins'
import CToolbar from 'lib@/components/c-toolbar'
import educationInfo from './expertDatabaseDetail/educationInfo'
import workInfo from './expertDatabaseDetail/workInfo'
import partakeProject from './expertDatabaseDetail/partakeProject'
import QuickSearch from 'lib@/components/QuickSearch'

export default {
  name: 'ExpertDatabaseDetail',
  components: {
    CToolbar,
    educationInfo,
    workInfo,
    partakeProject,
    QuickSearch
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      detailFormData: {
        expertName: '',
        expertAccount: '',
        birthDate: '',
        expertJobName: '',
        expertJobCode: '',
        highestEducation: '',
        profession: '',
        workYears: '',
        phone: '',
        email: '',
        startDate: '',
        endDate: ''
      },
      educationList: [],
      workRecordList: [],
      evaluateProjectList: [],
      detailFormRules: {
        expertName: [{ required: true, message: this.$t('common.pleasefinishRequired') }],
        expertAccount: [{ required: true, message: this.$t('common.pleasefinishRequired') }],
        birthDate: [{ required: true, message: this.$t('common.pleasefinishRequired') }],
        highestEducation: [{ required: true, message: this.$t('common.pleasefinishRequired') }],
        profession: [{ required: true, message: this.$t('common.pleasefinishRequired') }],
        workYears: [{ required: true, message: this.$t('common.pleasefinishRequired') }],
        phone: [
          { required: true, message: this.$t('common.pleasefinishRequired') },
          {
            validator: (rule, value, callback) => {
              if (value && !isMobile(value)) {
                // 手机格式不合法
                callback(new Error(this.$t('dataConfMod.msgIllegalPhone')))
              } else {
                callback()
              }
            },
            trigger: 'blur'
          }
        ],
        email: [
          { required: true, message: this.$t('common.pleasefinishRequired') },
          {
            validator: (rule, value, callback) => {
              if (value && !isEmail(value)) {
                // 邮箱格式不合法
                callback(new Error(this.$t('dataConfMod.msgIllegalMail')))
              } else {
                callback()
              }
            },
            trigger: 'blur'
          }
        ]
      },
      pageFlag: this.$attrs.params.flag,
      isReadonly: this.$attrs.params.readOnly
    }
  },
  created () {
    if (this.pageFlag !== 'add') {
      this.getDetail()
    }
  },
  methods: {
    /* 查询详情 */
    getDetail (val) {
      const expertId = (this.$attrs.params.row || {}).expertId || val
      if (!expertId) return

      this.$http({
        url: `/api-sou/bidExpert/getDetail/${expertId}`,
        method: 'GET',
        loading: true
      }).then(data => {
        if (data && data.data) {
          this.detailFormData = data.data.expert
          this.educationList = (data.data.educationList || []).map(item => {
            return {
              ...item,
              // 处理就读时间
              studyTime: item.studyTimeFrom && item.studyTimeTo ? [
                item.studyTimeFrom,
                item.studyTimeTo
              ] : []
            }
          })
          this.workRecordList = data.data.workRecordList || []
          this.evaluateProjectList = data.data.evaluateProjectList || []
        }
      })
    },

    /* 选择一个系统账号 */
    setUserObj (val) {
      this.detailFormData = {
        ...this.detailFormData,
        expertAccount: val ? val.username || '' : '',
        expertJobCode: val ? val.ceeaJobcode || '' : '',
        expertJobName: val ? val.ceeaJobcodeDescr || '' : ''
      }

      this.detailFormData.phone = val.phone
      this.detailFormData.email = val.email
    },

    /* 校验 */
    validateForm (type) {
      return new Promise(resolve => {
        this.$refs.detailForm.validate(valid => {
          if (valid) {
            // 删除不必要的提交参数
            const formData = JSON.parse(JSON.stringify(this.detailFormData))
            resolve({
              valid: true,
              data: {
                tempSave: type === 'save',
                expert: {
                  ...formData
                },
                educationList: this.educationList.map(item => {
                  return {
                    ...item,
                    // 处理就读时间
                    studyTimeFrom: item.studyTime[0],
                    studyTimeTo: item.studyTime[1]
                  }
                }),
                workRecordList: this.workRecordList,
                evaluateProjectList: this.evaluateProjectList
              }
            })
          } else {
            this.__focus_error__()
            resolve({ valid: false })
          }
        })
      })
    },

    /* 提交 */
    async saveOrSubmit (type) {
      const validate = await this.validateForm(type)
      if (validate.valid) {
        this.$http({
          url: '/api-sou/bidExpert/tempSaveOrSubmit',
          method: 'POST',
          data: validate.data,
          loading: true
        }).then(data => {
          this.$message.success(this.$t('common.successSave'))
          if (type === 'submit') {
            // 提交。关闭页签
            this.navTabsBack('refresh')
          } else {
            // 重新查询
            this.getDetail(data.data)
          }
        })
      }
    },

    /* 取消，返回 */
    navTabsBack (type) {
      this.$emit('tab-remove', this.$attrs.tabName)
      if (type === 'refresh') {
        // 需要刷新
        this.__setTabTodo('ExpertDatabase.getQueryData')
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.expert-database-detail {
  padding-top: 20px;
  padding-bottom: 65px;
  :deep(.el-range-editor.el-input__inner,.el-date-editor.el-input) {
    max-width: 100%;
  }
}
</style>
