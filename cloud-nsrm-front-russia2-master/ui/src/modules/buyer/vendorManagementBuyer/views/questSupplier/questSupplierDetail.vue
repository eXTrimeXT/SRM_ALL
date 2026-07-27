<template>
  <el-container
    class="questSupplierDetail"
    direction="vertical"
  >
    <el-main>
      <div class="form-container">
        <div class="fillInfoSec">
          <el-form
            ref="form"
            :model="form"
            :rules="rules"
          >
            <el-row :gutter="32">
              <!-- 调查模板类型 -->
              <srm-col :span="6">
                <el-form-item
                  prop="questTemplateTypeName"
                  :label="$t('vendorMod.questTemplateType')"
                >
                  <el-input
                    v-model="form.questTemplateTypeName"
                    disabled
                  />
                </el-form-item>
              </srm-col>
              <!-- 调查表编码 -->
              <srm-col :span="6">
                <el-form-item
                  prop="questNo"
                  :label="$t('vendorMod.questNo')"
                >
                  <el-input
                    v-model="form.questNo"
                    disabled
                  />
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
                    disabled
                  />
                </el-form-item>
              </srm-col>
              <!-- 业务组织 -->
              <srm-col :span="6">
                <el-form-item
                  prop="questTemplateOrgName"
                  :label="$t('vendorMod.questTemplateOrgName')"
                >
                  <el-input
                    v-model="form.questTemplateOrgName"
                    disabled
                  />
                </el-form-item>
              </srm-col>
            </el-row>
            <el-row :gutter="32">
              <!-- 反馈备注 -->
              <srm-col :span="12">
                <el-form-item
                  prop="questFeedback"
                  :label="$t('vendorMod.questFeedback')"
                >
                  <el-input
                    v-model="form.questFeedback"
                    :disabled="curOpt === 'view'"
                  />
                </el-form-item>
              </srm-col>
              <!-- 审批状态 -->
              <srm-col :span="12">
                <el-form-item
                  prop="approvalStatus"
                  :label="$t('vendorMod.approvalStatus2')"
                >
                  <DictSelect
                    v-model="form.approvalStatus"
                    code="QUEST_SUPPLIER_APPROVE_STATUS"
                    filterable
                    :disabled="true"
                  />
                </el-form-item>
              </srm-col>
            </el-row>
          </el-form>
          <div class="fillInfo">
            <renderForm
              v-if="form.questTemplateId"
              ref="renderForm"
              :quest-template-id="form.questTemplateId"
              :quest-sup-id="form.questSupId"
              :disabled="curOpt==='view'"
              :com-data="form.groupInfoList"
              :opt-type="curOpt"
            />
          </div>
        </div>
      </div>
      <CToolbar>
        <template #right>
          <el-button
            v-if="curOpt !== 'view'"
            @click="cancelBill"
          >
            {{ $t('common.cancel') }}
          </el-button>
          <el-button
            v-if="curOpt !== 'view'"
            type="primary"
            :disabled="readOnly"
            @click="saveDataHandle('WRITING')"
          >
            {{ $t('common.staging') }}
          </el-button>
          <el-button
            v-if="curOpt !== 'view'"
            type="primary"
            :disabled="readOnly"
            @click="saveDataHandle('WRITED')"
          >
            {{ $t('common.submit') }}
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
import renderForm from 'modb@/vendorManagementBuyer/views/questTemplate/renderForm'
import { adaptDictData, parseTime, findMenuIdByPath } from '@/utils'

export default {
  name: 'QuestSupplierDetail',
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
      activeDims: ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10'],
      approveStatusList: [], // 审批状态
      questTemplateTypeList: [], // 调查模板类型
      orgIdList: [], // 模板所属组织ID
      yesOrNoList: [], // 是否
      form: {
        questSupId: null,
        questNo: null,
        questName: null,
        questTemplateId: null,
        questTemplateType: null,
        questTemplateTypeName: null,
        questTemplateOrgId: null,
        questTemplateOrgCode: null,
        questTemplateOrgName: null,
        questFeedback: '',
        approvalStatus: '',
        groupInfoList: [],
        fieldInfoList: []
      },
      rules: {},
      readOnly: this.$attrs.params.flag == 'view',
      curRole: this.$store.getters.userType,
      curOpt: 'view'
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
      this.getSupplierResultDetail(row.questSupId)
    }
  },
  mounted () {},
  methods: {
    async validateForm () {
      // const flag = await this.validate();
      // if (!flag) {
      //   return false;
      // }
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
    deleteRowField (index, propArr) {
      propArr.splice(index, 1)
    },
    // 保存数据操作
    async saveDataHandle (type) {
      let vendorFillData = this.$refs.renderForm.getData()
      vendorFillData.questSupId = this.form.questSupId
      this.$set(vendorFillData, 'questFeedback', this.form.questFeedback)
      this.$set(vendorFillData, 'approvalStatus', type)
      console.log(vendorFillData)
      // 提交做数据校验，暂存不做校验
      if (type === 'WRITED') {
        const flag = await this.validateForm()
        if (!flag) {
          return
        }
      }
      this.$http({
        url: '/api-sup/quest/questResult/saveOrUpdateQuestResultForm',
        method: 'POST',
        data: vendorFillData,
        loading: true
      })
        .then(async (res) => {
          if (this.$attrs.params.flag === 'edit') {
            this.$message({
              type: 'success',
              message: this.$t('common.successSubmit')
            }) // 提交成功
            this.$emit(
              'tab-remove',
              'questSupplierDetail' + this.$attrs.params.row.questSupId || ''
            )
          } else {
            this.$emit('tab-remove', 'questSupplierDetail')
          }
          this.__setTabTodo('questSupplierList.getQuerydata') // 查询列表数据
        })
        .catch((err) => {
          console.log(err)
        })
    },

    cancelBill () {
      const { flag, row } = this.$attrs.params
      if (flag === 'add') {
        this.$emit('tab-remove', 'questSupplierDetail')
      } else {
        this.$emit('tab-remove', 'questSupplierDetail' + row.questSupId)
      }
      this.__setTabTodo('questsupplierList.getQuerydata')
    },
    // 通过id查询供应商问卷填写内容
    getSupplierResultDetail (questSupId) {
      this.$http({
        url: '/api-sup/quest/questResult/getQuestResultDtoByQuestSupId',
        method: 'GET',
        params: { questSupId: questSupId },
        loading: true
      })
        .then((res) => {
          let result = res.data
          // 获取单据ID复制到工作流参数中
          // this.workflowParamsInfo.businessId = result.questSupId;
          // this.workflowParamsInfo.tabDisabled = false;
          this.form.questTemplateId = result.questTemplateId
          ;(this.form.questSupId = result.questSupId),
            (this.form.questNo = result.questNo),
            (this.form.questName = result.questName),
            (this.form.questTemplateOrgId = result.questTemplateOrgId),
            (this.form.questTemplateOrgCode = result.questTemplateOrgCode),
            (this.form.questTemplateOrgName = result.questTemplateOrgName),
            (this.form.questTemplateType = result.questTemplateType),
            (this.form.questTemplateTypeName = result.questTemplateTypeName),
            (this.form.questFeedback = result.questFeedback)
          this.form.groupInfoList = result.groupInfoList
          this.form.approvalStatus = result.approvalStatus
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
          console.log(this.orgIdList)
        })
        .catch((err) => {
          console.log(err)
        })
    }
  }
}
</script>
<style scoped lang="scss">
.fillInfoSec {
  padding: 15px 15px 50px;
  .fillInfo {
    margin-top: 20px;
  }
}
</style>
