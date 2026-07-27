<template>
  <el-container
    class="questManagementDetail"
    direction="vertical"
  >
    <el-main>
      <div class="form-container">
        <el-collapse
          v-model="activeDims"
          class="tab-form-style"
        >
          <!-- 调查表基本信息 -->
          <el-collapse-item
            ref="questInfo"
            :title="$t('vendorMod.questInfo')"
            name="1"
          >
            <el-form
              ref="questInfoForm"
              :model="form"
              :rules="questInfoRules"
            >
              <srm-row :gutter="32">
                <srm-col :span="6">
                  <!-- 调查模板类型 -->
                  <el-form-item
                    :label="$t('vendorMod.questTemplateType')"
                    prop="questTemplateType"
                  >
                    <DictSelect
                      v-model="form.questTemplateType"
                      code="QUEST_TEMPLATE_TYPE"
                      :disabled="curOpt === 'view'"
                      @change-value="getQuestTemplateType"
                    />
                  </el-form-item>
                </srm-col>
                <!-- 调查表模板 -->
                <srm-col :span="6">
                  <el-form-item
                    prop="questTemplateId"
                    :label="$t('vendorMod.questTemplateId')"
                  >
                    <QuickSearch
                      :pre-query-data="{ 't.QUEST_TEMPLATE_TYPE': form.questTemplateType }"
                      :show-input="form.questTemplateName"
                      show-key="questTemplateName"
                      :scope-data="form"
                      name="scc_sup_quest_template"
                      :disabled="curOpt === 'view'"
                      @close-quicksearch="getTemplateObj"
                    />
                  </el-form-item>
                </srm-col>
                <!-- 业务组织 -->
                <srm-col :span="6">
                  <el-form-item
                    :label="$t('vendorMod.questTemplateOrgName')"
                    prop="questTemplateOrgName"
                  >
                    <el-select
                      v-model="form.questTemplateOrgName"
                      :disabled="curOpt === 'view'"
                      @change="selectHandler"
                    >
                      <el-option
                        v-for="item in orgIdList"
                        :key="item.organizationId"
                        :label="item.organizationName"
                        :value="item.organizationId"
                      />
                    </el-select>
                  </el-form-item>
                </srm-col>
                <!-- 调查表名称 -->
                <srm-col :span="6">
                  <el-form-item
                    prop="questName"
                    :label="$t('vendorMod.questName')"
                  >
                    <el-input
                      v-model="form.questName"
                      :disabled="curOpt === 'view'"
                    />
                  </el-form-item>
                </srm-col>
              </srm-row>
            </el-form>
          </el-collapse-item>
          <!-- 调查表基本信息 -->
          <!-- 供应商信息 -->
          <el-collapse-item
            ref="supplierInfo"
            :title="$t('vendorMod.vendorInfo')"
            name="1"
          >
            <el-tabs v-model="activeList">
              <!-- 调查表范围 -->
              <el-tab-pane
                name="1"
                :label="$t('vendorMod.questionnaireScope')"
              >
                <!-- <el-button
                  v-if="curOpt !== 'view'"
                  type="primary"
                  @click="addFiledItem()"
                >
                  {{ $t('common.add') }}
                </el-button> -->
                <QuickSearch
                  name="scc_sup_company_info"
                  :disabled="curOpt === 'view'"
                  :btnTitle="$t('common.add')"
                  showButton
                  multiSelect
                  style="margin-bottom:5px"
                  @close-quicksearch="getCompanyList"
                />
                <el-table
                  :data="form.companyInfoList"
                  style="width: 100%"
                  border
                  max-height="350px"
                >
                  <el-table-column
                    :label="$t('common.vendorName')"
                    prop="companyName"
                    min-width="150"
                    :render-header="_addStarToColumn"
                  >
                    <!-- <template slot-scope="scope">
                      <quick-search
                        v-if="!scope.row.companyId"
                        :show-input="scope.row.companyName"
                        show-key="companyName"
                        :scope-data="scope.row"
                        name="scc_sup_company_info_display"
                        :disabled="curOpt === 'view'"
                        :show-overflow-tooltip="true"
                        @close-quicksearch="getCompanyObj"
                      />
                      <span v-else>{{ scope.row.companyName }}</span>
                    </template> -->
                  </el-table-column>
                  <!-- 供应商编码 -->
                  <el-table-column
                    prop="companyCode"
                    :label="$t('vendorMod.vendorCode')"
                    min-width="120"
                  >
                    <template slot-scope="scope">
                      {{ scope.row.companyCode }}
                    </template>
                  </el-table-column>
                  <!-- 社会信用代码 -->
                  <el-table-column
                    prop="lcCode"
                    :label="$t('vendorMod.socialCreditCode2')"
                    min-width="120"
                  >
                    <template slot-scope="scope">
                      {{ scope.row.lcCode }}
                    </template>
                  </el-table-column>

                  <!-- 联系人 -->
                  <el-table-column
                    align="center"
                    prop="contactName"
                    :label="$t('vendorMod.contactPerson')"
                    width="150"
                    :show-overflow-tooltip="true"
                    :render-header="_addStarToColumn"
                  >
                    <template slot-scope="scope">
                      <QuickSearch
                        :pre-query-data="{ 't.COMPANY_ID': scope.row.companyId }"
                        :show-input="scope.row.contactName"
                        show-key="contactName"
                        allow-input
                        :scope-data="scope.row"
                        name="scc_sup_contact_info"
                        :disabled="curOpt === 'view'"
                        @close-quicksearch="getContactObj"
                      />
                    </template>
                  </el-table-column>
                  <!-- 联系方式 -->
                  <el-table-column
                    prop="ceeaContactMethod"
                    align="center"
                    :label="$t('vendorMod.contactMethod')"
                    min-width="120"
                  >
                    <template slot-scope="scope">
                      {{ scope.row.ceeaContactMethod }}
                    </template>
                  </el-table-column>
                  <!-- 邮箱 -->
                  <el-table-column
                    prop="email"
                    :label="$t('vendorMod.email')"
                    min-width="120"
                  >
                    <template slot-scope="scope">
                      {{ scope.row.email }}
                    </template>
                  </el-table-column>
                  <!-- 操作 -->
                  <el-table-column
                    align="center"
                    prop="operation"
                    :label="$t('vendorMod.relegation.operation')"
                  >
                    <template slot-scope="scope">
                      <el-button
                        v-if="curOpt !== 'view'"
                        type="text"
                        @click="deleteRowField(scope.$index, form.companyInfoList)"
                      >
                        {{ $t('common.delete') }}
                      </el-button>
                    </template>
                  </el-table-column>
                </el-table>
              </el-tab-pane>
              <!-- 调查表预览 -->
              <el-tab-pane
                :label="$t('vendorMod.surveyFormPreview')"
                name="2"
              >
                <!-- 根据模板配置渲染相关组件 -->
                <renderForm
                  :quest-template-id="form.questTemplateId"
                  :disabled="true"
                  :opt-type="curOpt"
                />
              </el-tab-pane>
            </el-tabs>
          </el-collapse-item>
        </el-collapse>
      </div>
      <CToolbar>
        <template #right>
          <el-button
            @click="cancelBill"
          >
            {{ $t('common.cancel') }}
          </el-button>
          <el-button
            v-if="curOpt !== 'view'"
            type="primary"
            :disabled="readOnly"
            :loading="loadingFlag"
            @click="save('DRAFT')"
          >
            {{ $t('common.staging') }}
          </el-button>
          <el-button
            v-if="curOpt !== 'view'"
            type="primary"
            :disabled="readOnly"
            :loading="loadingFlag"
            @click="save('PUBLISH')"
          >
            {{ $t('common.publish') }}
          </el-button>
        </template>
      </CToolbar>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import QuickSearch from 'lib@/components/QuickSearch'
import OrganizationSelector from 'lib@/components/organization-selector'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MainHeader from 'lib@/components/Table/MainHeader'
import CToolbar from 'lib@/components/c-toolbar'
import { adaptDictData, parseTime, findMenuIdByPath } from '@/utils'
import { getRegion } from '@/api/common'
import renderForm from 'modb@/vendorManagementBuyer/views/questTemplate/renderForm.vue'

export default {
  name: 'QuestManagementDetail',
  components: {
    MainHeader,
    CToolbar,
    QuickSearch,
    OrganizationSelector,
    FormWrapper,
    TableView,
    renderForm
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      activeList: '1',
      activeDims: ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10'],
      approveStatusList: [], // 审批状态
      questTemplateTypeList: [], // 调查模板类型
      orgIdList: [], // 模板所属组织ID
      yesOrNoList: [], // 是否
      form: {
        questTemplateId: null,
        questTemplateName: null,
        questNo: null,
        questName: null,
        questTemplateType: null,
        questTemplateTypeName: null,
        questTemplateOrgId: null,
        questTemplateOrgCode: null,
        questTemplateOrgName: null,
        approvalStatus: null,
        questFeedback: null,
        createdBy: null,
        creationDate: null,
        lastUpdatedBy: null,
        lastUpdateDate: null,
        createdFullName: null,
        lastUpdatedFullName: null,
        deleteFlag: null,
        version: null,
        opType: '',
        companyInfoList: []
      },
      questInfoRules: {
        questTemplateType: [{ required: true, message: this.$t('vendorMod.pleaseEnter') }], // '请输入
        questTemplateId: [{ required: true, message: this.$t('vendorMod.pleaseEnter') }], // '请输入
        questTemplateOrgId: [{ required: true, message: this.$t('vendorMod.pleaseEnter') }], // '请输入
        questName: [{ required: true, message: this.$t('vendorMod.pleaseEnter') }] // '请输入
      },
      readOnly: false,
      curRole: this.$store.getters.userType,
      curOpt: 'view',
      loadingFlag: false
    }
  },
  computed: {},
  watch: {},
  created () {
    this.curOpt = this.$attrs.params.flag
    if (this.$attrs.params.flag === 'add') {
      const { phone, nickname, username, ceeaDeptId, department } =
        this.$store.getters.user.userInfo
      this.form.createdFullName = nickname
    }
    const { flag, row, readOnly = false } = this.$attrs.params
    this.readOnly = readOnly
    if (flag === 'edit' || flag === 'view') {
      this.getSupplierDetail(row.questSupId, flag)
    }
    if (flag === 'view') {
      this.readOnly = true
    }
  },
  mounted () {},
  methods: {
    selectHandler (val) {
      let node = this.orgIdList.find((v) => v.organizationId == val) || {}
      if (node) {
        this.form.questTemplateOrgId = node.organizationId
        this.form.questTemplateOrgCode = node.organizationCode
        this.form.questTemplateOrgName = node.organizationName
      }
    },

    // 选择模板回调
    getTemplateObj (val, data) {
      data.questTemplateId = val ? val.questTemplateId : ''
      data.questTemplateName = val ? val.questTemplateName : ''
      this.getTemplateOrg(data.questTemplateId)
    },
    // 选择供应商回调
    // getCompanyObj (val, data) {
    //   data.companyId = val ? val.companyId : ''
    //   data.companyCode = val ? val.companyCode : ''
    //   data.companyName = val ? val.companyName : ''
    //   data.lcCode = val ? val.lcCode : ''
    // },
    getCompanyList (data) {
      if (data.length > 0) {
        let companyIdList = []
        for (let item of this.form.companyInfoList) {
          item.companyId && companyIdList.push(item.companyId)
        }
        data.forEach(item => {
          if (item.companyId && !companyIdList.includes(item.companyId)) {
            this.form.companyInfoList.unshift({
              questSupId: '',
              companyId: item.companyId,
              companyCode: item.companyCode,
              companyName: item.companyName,
              lcCode: '',
              contactName: '',
              ceeaContactMethod: '',
              email: ''
            })
          }
        })
      }
    },
    // 选择供应商回调联系人回调
    getContactObj (val, scope) {
      scope.contactName = val ? val.contactName : ''
      scope.ceeaContactMethod = val ? val.ceeaContactMethod : ''
      scope.email = val ? val.email : ''
    },

    // 调查模板类型切换
    getQuestTemplateType (value, dictItem) {
      this.form.questTemplateTypeName = dictItem.label // 复制类型name
      this.$set(this.form, 'questTemplateName', '')
      this.$set(this.form, 'questTemplateId', '')
      this.getTemplateOrg(this.form.questTemplateId)
    },

    async save (type) {
      console.log(type)
      const flag = await this.validateForm()
      if (flag) {
        this.readOnly = true
        this.loadingFlag = true
        return this.saveDataHandle(type)
      }
      return flag
    },
    async validateForm () {
      const flag = await this.validate()
      if (!flag) return false
      // 调查表范围
      let companyInfoList = this.form.companyInfoList || []
      if (companyInfoList.length > 0) {
        for (let item of companyInfoList) {
          // if (!item.companyCode) {
          //   this.$message.error(this.$t('dashboard.venderNameAndCodeRequired'))
          //   return false
          // }
          if (!item.contactName) {
            this.$message.error(this.$t('dashboard.concatRequired'))
            return false
          }
        }
      } else {
        this.$message.error(this.$t('dashboard.vendorInfoRequired'))
        return false
      }
      return true
    },
    validate () {
      return new Promise((rs) => {
        this.$refs.questInfoForm.validate((valid) => {
          if (!valid) {
            this.$message({
              message: this.$t('vendorMod.pleasefinishRequired'), // '请输入单据必填信息'
              type: 'error'
            })
          }
          rs(valid)
        })
      })
    },
    // addFiledItem () {
    //   this.form.companyInfoList.push({
    //     questSupId: '', //
    //     companyId: '', // 供应商ID
    //     companyCode: '', // 供应商编码
    //     companyName: '', // 供应商名称
    //     lcCode: '', // 社会信用代码
    //     contactName: '', // 联系人
    //     ceeaContactMethod: '', // 联系方式
    //     email: '' // 邮箱
    //   })
    // },
    deleteRowField (index, propArr) {
      propArr.splice(index, 1)
    },
    // 保存数据操作
    saveDataHandle (type) {
      let submitData = this.form
      submitData.opType = type
      this.$http({
        url: '/api-sup/quest/questSupplier/saveOrUpdateQuestSupplierForm',
        method: 'POST',
        data: submitData,
        loading: true
      })
        .then((res) => {
          let resData = res.data
          if (this.$attrs.params.flag === 'edit') {
            this.$message({
              type: 'success',
              message: this.$t('common.successSubmit')
            }) // 提交成功
            this.$emit(
              'tab-remove',
              'questManagementDetail' + this.$attrs.params.row.questSupId || ''
            )
          } else {
            this.$emit('tab-remove', 'questManagementDetail')
          }
          this.__setTabTodo('questManagementList.getQuerydata.queryByParams') // 查询列表数据
        })
        .catch((err) => {
          console.log(err)
        })
    },

    cancelBill () {
      const { flag, row } = this.$attrs.params
      if (flag === 'add') {
        this.$emit('tab-remove', 'questManagementDetail')
      } else {
        this.$emit('tab-remove', 'questManagementDetail' + row.questSupId)
      }
      this.__setTabTodo('questManagementList.getQuerydata.queryByParams')
    },
    // 通过id查询分配供应商问卷
    getSupplierDetail (questSupId, flag) {
      this.$http({
        url: '/api-sup/quest/questSupplier/listPageByParm',
        method: 'POST',
        data: { questSupIdForQuery: questSupId },
        loading: true
      })
        .then((res) => {
          let result = res.data.list[0]
            this.form.questTemplateId = result.questTemplateId
            this.form.questTemplateName = result.questTemplateName
            this.form.questNo = result.questNo
            this.form.questName = result.questName
            this.form.questTemplateOrgId = result.questTemplateOrgId
            this.form.questTemplateOrgCode = result.questTemplateOrgCode
            this.form.questTemplateOrgName = result.questTemplateOrgName
            this.form.approvalStatus = result.approvalStatus
            this.form.questFeedback = result.questFeedback
            this.form.questTemplateType = result.questTemplateType
            this.form.questTemplateTypeName = result.questTemplateTypeName
            this.form.questFeedback = result.questFeedback

            this.form.companyInfoList.push({
              questSupId: result.questSupId, //
              companyId: result.companyId, // 供应商ID
              companyCode: result.companyCode, // 供应商编码
              companyName: result.companyName, // 供应商名称
              lcCode: result.lcCode, // 社会信用代码
              contactName: result.contactName, // 联系人
              ceeaContactMethod: result.ceeaContactMethod, // 联系方式
              email: result.email // 邮箱
            })
          if (flag === 'edit') {
            this.getTemplateOrg(result.questTemplateId)
          }
        })
        .catch((err) => {
          console.log(err)
        })
    },
    // 通过id查询模板分配的组织
    getTemplateOrg (questTemplateId) {
      this.$http({
        url: '/api-sup/quest/questTemplateOrg/listPage',
        method: 'POST',
        data: { questTemplateId: questTemplateId },
        loading: true
      })
        .then((res) => {
          res.data.list.forEach((result) => {
            this.orgIdList.push({
              organizationId: result.orgId,
              organizationCode: result.orgCode,
              organizationName: result.orgName
            })
          })
          console.log('[this.orgIdList]', this.orgIdList)
        })
        .catch((err) => {
          console.log(err)
        })
    }
  }
}
</script>
<style scoped lang="scss">
.questManagementDetail {
  height: 100%;

  .sub_header {
    padding: 4px 11px;
    background: #eee;
  }

  .el-table .el-date-editor {
    width: 135px;
  }

  .base-form {
    padding: 15px 30px 0;
  }

  .toRequired {
    color: #ff4949;
    padding-right: 2px;
  }

  .edit_cond {
    color: #23adf4;
    cursor: pointer;
  }
}
</style>
