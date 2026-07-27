<template>
  <el-container class="flex-container the-purchaseApplicationDetail-detail" direction="vertical">
    <el-main>
      <CWorkflowMulti
        ref="workflowMulti"
        v-model="activeTabName"
        :fun-params="workflowParamsInfo"
        :button-config-info="buttonConfigInfo"
        :button-custom="buttonCustom"
        @tab-click="workflowView"
        @workflow-handler="workflowHandler"
        @click-handler="type => saveOrSubmitBill(type)"
        @submit-direct="type => saveOrSubmitBill(type)"
        @confirm="(type, comment) => saveOrSubmitBill(type, comment)"
        @close-tab="back"
      >
        <div class="form-container2">
          <el-form
            ref="requirementHeadRef"
            :model="requirementHead"
            label-width="80px"
            label-position="top"
            :rules="rules"
          >
            <el-collapse v-model="activeDims" class="tab-form-style">
              <el-collapse-item ref="aptInfo" title="预警单据信息" name="1">
                <srm-row>
                  <srm-col>
                    <el-form-item
                      label="预警单号"
                    >
                      <el-input v-model="requirementHead.warningCode" :disabled="disabledBol" />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <el-form-item
                      prop="warningName"
                      label="预警主题"
                    >
                      <el-input v-model="requirementHead.warningName" :disabled="isReadOnly" />
                    </el-form-item>
                  </srm-col>
                  <srm-col v-if="curRole == 'BUYER'">
                    <el-form-item
                      label="评分项目名称"
                    >
                      <el-input v-model="requirementHead.projectName" :disabled="disabledBol" />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <el-form-item
                      prop="companyName"
                      label="供应商名称"
                    >
                      <el-input v-model="requirementHead.companyName" :disabled="disabledBol" />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <el-form-item
                      prop="perfModelType"
                      label="预警类型"
                    >
                      <DictSelect
                        v-model="requirementHead.perfModelType"
                        code="PERF_MODEL_TYPE"
                        :disabled="disabledBol"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <el-form-item
                      label="预警状态"
                    >
                      <DictSelect
                        v-model="requirementHead.warningStatus"
                        code="PERF_WARNING_STATUS"
                        :disabled="disabledBol"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col v-if="curRole == 'BUYER'">
                    <el-form-item
                      label="公司"
                    >
                      <el-input v-model="requirementHead.ouOrganizationName" :disabled="disabledBol" />
                    </el-form-item>
                  </srm-col>
                  <srm-col v-if="curRole == 'BUYER'">
                    <el-form-item
                      label="查看状态"
                    >
                      <DictSelect
                        v-model="requirementHead.readStatus"
                        code="PERF_READ_STATUS"
                        :disabled="disabledBol"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col v-if="curRole == 'BUYER'">
                    <el-form-item
                      label="招标负责人"
                    >
                      <el-input v-model="requirementHead.bidManager" :disabled="disabledBol" />
                    </el-form-item>
                  </srm-col>
                  <srm-col v-if="curRole == 'BUYER'">
                    <el-form-item
                      label="招标负责人公司部门"
                    >
                      <el-input v-model="requirementHead.bidManagerFullPath" :disabled="disabledBol" />
                    </el-form-item>
                  </srm-col>
                  <srm-col v-if="curRole == 'BUYER'">
                    <el-form-item
                      label="合同经办人"
                    >
                      <el-input v-model="requirementHead.contractManager" :disabled="disabledBol" />
                    </el-form-item>
                  </srm-col>
                  <srm-col v-if="curRole == 'BUYER'">
                    <el-form-item
                      label="合同经办人公司部门"
                    >
                      <el-input v-model="requirementHead.contractManagerFullPath" :disabled="disabledBol" />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <el-form-item
                      label="创建人"
                    >
                      <el-input v-model="requirementHead.createdFullName" :disabled="disabledBol" />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="1">
                    <el-form-item
                      prop="warningRemark"
                      label="预警详情"
                    >
                      <el-input type="textarea" v-model="requirementHead.warningRemark" :autosize="{ minRows: 4, maxRows: 6 }" :disabled="isReadOnly" />
                    </el-form-item>
                  </srm-col>
                </srm-row>
              </el-collapse-item>
            </el-collapse>
          </el-form>
        </div>
      </CWorkflowMulti>
    </el-main>

  </el-container>
</template>
<script>
import _pick from 'lodash/pick'
import OrganizationSelector from 'lib@/components/organization-selector'
import { tabTodoMixin } from '@/utils/mixins'
import QuickSearch from 'lib@/components/QuickSearch'
import { parseTime } from '@/utils'
import { downloadFileLink } from 'lib@/utils/file'
import WorkflowCommon from '@/library/mixins/workflow-common'
import '@riophae/vue-treeselect/dist/vue-treeselect.css'
import { getToken } from '@/utils/auth'
import axios from 'axios'
import { sysPrefix } from '@/config/ipConfig'
import { purchaseApplicationApi } from 'modc@/buyer/purchasingDemand/api'
import CategorySelect from 'modc@/buyer/vendorManagementBuyer/views/quaOfReviewEngine/components/categorySelect'

export default {
  name: 'PurchaseApplicationDetail',
  components: {
    QuickSearch,
    OrganizationSelector,
    CategorySelect
  },
  mixins: [tabTodoMixin, WorkflowCommon],
  data () {
    return {
      curRole: this.$store.getters.userType,
      templateHeadList: [],
      disabledBol: true,
      requirementHead: {
        warningCode: null,
        warningName: null,
        projectName: null,
        companyName: null,
        perfModelType: null,
        warningStatus: null,
        ouOrganizationName: null,
        readStatus: 'N',
        bidManager: null,
        bidManagerFullPath: null,
        contractManager: null,
        contractManagerFullPath: null,
        createdBy: null,
        warningRemark: null
      },
      currentPage: 1,
      allcancelLineList: [],
      activeDims: ['1', '2', '3'],
      rules: {

      },
      personList: []
    }
  },
  computed: {
    viewUpdateButton () {
      return (
        this.curRole === 'BUYER' &&
        !this.isReadOnly &&
        this.requirementHead.cancelStatus !== 'APPROVED'
      )
    },
    disabledUpdateButton () {
      return (
        this.requirementHead.cancelStatus === 'SUBMITTED' ||
        this.requirementHead.cancelStatus === 'APPROVING'
      )
    },
    workflowBusinessId () {
      return this.requirementHead?.projectScoreItemsId
    },
    workflowTabDisabled () {
      return !this.isApprovalOnly
    },
    isReadOnly () {
      return !['add', 'edit'].includes(this.$attrs.params.flag)
    }
  },
  watch: {
    viewUpdateButton () {
      this.buttonConfigInfo.save.view = this.viewUpdateButton
      this.buttonConfigInfo.submit.view = this.viewUpdateButton
    },
    disabledUpdateButton () {
      this.buttonConfigInfo.save.disabled = this.disabledUpdateButton
      this.buttonConfigInfo.submit.disabled = this.disabledUpdateButton
    }
  },
  created () {
    this.Viewflag = this.$attrs.params.flag
    if (this.Viewflag === 'approveNumber') this.workflowParamsInfo.tabDisabled = false
    if (this.$attrs.params.flag === 'add') {
      console.log(this.$attrs.params.row, 'row')
      const row = this.$attrs.params.row
      const warningType = this.$attrs.params.warningType
      this.requirementHead = row
      this.$set(this.requirementHead, 'readStatus', 'N')
      this.$set(this.requirementHead, 'perfModelType', warningType)
      this.$set(this.requirementHead, 'ouOrganizationName', row.organizationName)
      this.$set(this.requirementHead, 'ouOrganizationId', row.organizationId)
      this.$set(this.requirementHead, 'ouOrganizationCode', row.organizationCode)
    } else {
      this.getFormDetail(this.$attrs.params.row.warningId)
    }
    this.getButtonConfig()
  },
  methods: {
    getCompanyObj(val, row) {
      this.$set(row, 'scoreManAccount', val?.username)
      this.$set(row, 'scoreManId', val?.userId)
      this.$set(row, 'scoreManName', val?.nickname)
      this.$set(row, 'email', val?.email)
    },
    templateHeadChange(val, scope) {

    },
    getButtonConfig () {
      this.buttonConfigInfo.save.view = this.viewUpdateButton
      this.buttonConfigInfo.submit.view = this.viewUpdateButton
      this.buttonConfigInfo.submit.name = '发布'
      this.buttonConfigInfo.save.disabled = this.disabledUpdateButton
      this.buttonConfigInfo.submit.disabled = this.disabledUpdateButton
      this.buttonConfigInfo.cancel.view = !this.isReadOnly
      this.buttonConfigInfo.close.view = this.isReadOnly
    },
    async getWorkflowBusinessType () {
      return ''
    },
    async getWorkflowBusinessVariables () {
      return {

      }
    },
    getCWorkflowRefName () {
      return 'workflowMulti'
    },
    async getFormDetail (warningId) {
      let url = '/api-pef/projectScoreWarning/getDetailById' // 采购商
      if (this.curRole != 'BUYER') {
        url = '/api-pef/projectScoreWarning/sup/getDetailById' // 采购商
      }
      this.$http({
        url,
        method: 'GET',
        params: {
          warningId
        },
        loading: true
      }).then( res  => {
        const data = res.data
        const {...other} = data
        this.requirementHead = other
      })
    },
    async submitEvent (allparam) {
      this.$http({
        url: '/api-pef/projectScoreWarning/publish',
        method: 'POST',
        data: allparam,
        loading: true
      }).then( res  => {
        this.$message.success('操作成功')
        this.back()
      })
    },
    saveBill (allparam, type) {
      this.$http({
        url: '/api-pef/projectScoreWarning/saveOrUpdateDetail',
        method: 'POST',
        data: allparam,
        loading: true
      }).then( res  => {
        this.getFormDetail(res.data)
        this.$message.success('操作成功')
        this.back()
      })
    },
    async saveOrSubmitBill (type) {

        let allparam = {
          ...this.requirementHead
        }
        if (type === 'SUBMIT') {
          this.submitEvent(allparam)
        } else {
          this.saveBill(allparam)
        }

    },
    back () {
      if (this.$attrs.params.flag === 'add') {
        this.$emit('tab-remove', this.$attrs.params.tabName)
        this.$router.push({
          name: 'yujingdan',
          params: {
            from: 'fromYujing'
          }
        })
      } else {
        this.$emit('tab-remove', this.$attrs.params.tabName)
        this.__setTabTodo('performanceScoreItemsList.getQuerydata')
      }

    }
  }
}
</script>
<style scoped lang="scss">
.list-page-query :deep(.el-form-item__label) {
  text-align: right !important;
}
.the-purchaseApplicationDetail-detail {
  .sub_header {
    padding: 4px 11px;
    background: #eee;
  }
  .input-with-select .el-input-group__prepend {
    background-color: #fff;
  }
  .isDisabledimport {
    pointer-events: none;
    opacity: 0.5;
  }
  .the_btn_wrapper {
    display: inline-block;
    width: 111px;
  }
  .btn_line {
    display: flex;
    margin: 0 0 8px 0;
  }
  .el-tooltip :deep(.el-button) {
    min-width: 56px;
    font-size: 14px;
    border-radius: 2px;
    padding: 8px 16px;
  }
  .topComment {
    margin-top: 15px;
    text-align: right;
  }
  .input-number-precision {
    width: 100%;
    :deep(.el-input__inner) {
      text-align: left;
      padding-left: 8px;
    }
  }
}
:deep(.el-table td.el-table__cell .el-form-item__content) {
  white-space: nowrap;
  text-overflow: ellipsis;
  overflow: hidden;
}
.toRequired {
  color: #ff4949;
  padding-right: 2px;
}
</style>
