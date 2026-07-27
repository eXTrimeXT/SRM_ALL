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
              <el-collapse-item ref="aptInfo" title="项目信息" name="1">
                <srm-row>
                  <srm-col>
                    <el-form-item
                      label="评分项目名称"
                    >
                      <el-input v-model="requirementHead.projectName" :disabled="disabledBol" />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <el-form-item
                      prop="projectStatus"
                      label="评分状态"
                    >
                      <DictSelect
                        v-model="requirementHead.projectStatus"
                        code="PROJECT_SCORE_ITEM_STATUS"
                        :disabled="disabledBol"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <el-form-item
                      label="板块"
                    >
                      <el-input v-model="requirementHead.buOrganizationName" :disabled="disabledBol" />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <el-form-item
                      label="公司"
                    >
                      <el-input v-model="requirementHead.ouOrganizationName" :disabled="disabledBol" />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <el-form-item
                      label="合同编码"
                    >
                      <el-input v-model="requirementHead.contractNo" :disabled="disabledBol" />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <el-form-item
                      label="合同名称"
                    >
                      <el-input v-model="requirementHead.contractName" :disabled="disabledBol" />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <el-form-item
                      label="履约阶段"
                    >
                      <DictSelect
                        v-model="requirementHead.performanceCode"
                        :disabled="disabledBol"
                        code="MILESTONE_SCHEDULE"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <el-form-item
                      label="供应商编码"
                    >
                      <el-input v-model="requirementHead.companyCode" :disabled="disabledBol" />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <el-form-item
                      label="供应商名称"
                    >
                      <el-input v-model="requirementHead.companyName" :disabled="disabledBol" />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <el-form-item
                      label="招标编号"
                    >
                      <el-input v-model="requirementHead.bidCode" :disabled="disabledBol" />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <el-form-item
                      label="投资编号"
                    >
                      <el-input v-model="requirementHead.extInvestNo" :disabled="disabledBol" />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <el-form-item
                      label="招标结束时间"
                    >
                      <el-input v-model="requirementHead.bidEndDate" :disabled="disabledBol" />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <el-form-item
                      label="招标负责人"
                    >
                      <el-input v-model="requirementHead.bidManager" :disabled="disabledBol" />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <el-form-item
                      label="招标负责人部门"
                    >
                      <el-input v-model="requirementHead.bidManagerFullPath" :disabled="disabledBol" />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <el-form-item
                      label="合同经办人"
                    >
                      <el-input v-model="requirementHead.contractManager" :disabled="disabledBol" />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <el-form-item
                      label="合同经办人部门"
                    >
                      <el-input v-model="requirementHead.contractManagerFullPath" :disabled="disabledBol" />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <el-form-item
                      label="评分开始时间"
                    >
                      <el-date-picker
                        v-model="requirementHead.perStartMonth"
                        type="date"
                        value-format="yyyy-MM-dd"
                        :disabled="isReadOnly"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <el-form-item
                      label="评分结束时间"
                    >
                      <el-date-picker
                        v-model="requirementHead.perEndMonth"
                        type="date"
                        value-format="yyyy-MM-dd"
                        :disabled="isReadOnly"
                      />
                    </el-form-item>
                  </srm-col>
                </srm-row>
              </el-collapse-item>
              <el-collapse-item title="选择评分人" name="3">
                <p class="btn_line">
                  <el-button
                    v-if="!isReadOnly"
                    :disabled="isReadOnly"
                    type="primary"
                    class="detail-pbtn"
                    @click="addUploadOne"
                  >
                    添加评分人
                  </el-button>
                </p>
                <el-table :data="personList" style="width: 100%" border max-height="250px">
                  <el-table-column
                    align="center"
                    type="index"
                    :label="$t('purSettlementMod.tabindex')"
                    width="50"
                  />
                  <el-table-column
                    align="center"
                    prop="attachName"
                    label="品类"
                  >
                    <template slot-scope="scope">
                      <!--                      <CategorySelect-->
                      <!--                        v-model="scope.row.categoryName"-->
                      <!--                        :disabled="isReadOnly"-->
                      <!--                        :scope="scope"-->
                      <!--                        show-key="categoryName"-->
                      <!--                        @select="comfirmSelect"-->
                      <!--                      />-->
                      <el-select v-model="scope.row.categoryId" filterable placeholder="请选择" @change="comfirmSelect($event, scope.row)">
                        <el-option
                          v-for="(item, index) in contractMaterials"
                          :key="index"
                          :label="item.categoryName"
                          :value="item.categoryId"
                        />
                      </el-select>
                    </template>
                  </el-table-column>
                  <el-table-column
                    align="center"
                    prop="templateHeadId"
                    label="绩效模型"
                  >
                    <template slot-scope="scope">
                      <el-select
                        v-model="scope.row.templateHeadId"
                        :disabled="isReadOnly"
                        clearable
                        @change="templateHeadChange($event, scope)"
                      >
                        <el-option
                          v-for="(item,index) in templateHeadList"
                          :key="index"
                          :value="item.value"
                          :label="item.label"
                        />
                      </el-select>
                    </template>
                  </el-table-column>
                  <el-table-column
                    align="center"
                    prop="scoreManAccount"
                    label="评分人账号"
                  >
                    <template slot-scope="scope">
                      <QuickSearch
                        :show-input="scope.row.scoreManAccount"
                        :disabled="isReadOnly"
                        :scope-data="scope.row"
                        show-key="nickname"
                        name="scc_rbac_user_display"
                        @close-quicksearch="getCompanyObj"
                      />
                    </template>
                  </el-table-column>
                  <el-table-column
                    align="center"
                    prop="scoreManName"
                    label="评分人姓名"
                  >
                    <template slot-scope="scope">
                      <el-input v-model="scope.row.scoreManName" disabled />
                    </template>
                  </el-table-column>
                  <el-table-column
                    align="center"
                    prop="email"
                    label="电子邮箱"
                  >
                    <template slot-scope="scope">
                      <el-input v-model="scope.row.email" :disabled="isReadOnly" />
                    </template>
                  </el-table-column>
                  <el-table-column
                    align="center"
                    prop="remark"
                    label="备注"
                  >
                    <template slot-scope="scope">
                      <el-input v-model="scope.row.remark" :disabled="isReadOnly" />
                    </template>
                  </el-table-column>
                  <el-table-column :label="$t('common.operation')" width="60">
                    <template slot-scope="scope">
                      <el-button
                        type="text"
                        :disabled="isReadOnly"
                        @click="handleDelClick(scope.$index, scope.row)"
                      >
                        {{ $t('common.delete') }}
                      </el-button>
                    </template>
                  </el-table-column>
                </el-table>
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
import { createDictClass } from '@/library/utils/dict/dict-utils'
import { getToken } from '@/utils/auth'
import axios from 'axios'
import { sysPrefix } from '@/config/ipConfig'
import { purchaseApplicationApi, qa } from 'modc@/buyer/purchasingDemand/api'
import CategorySelect from 'modc@/buyer/vendorManagementBuyer/views/quaOfReviewEngine/components/categorySelect'
import { transformMQL } from '@/library/utils/util'

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
      contractMaterials: [],
      requirementHead: {
        projectName: null,
        projectStatus: null,
        buOrganizationName: null,
        ouOrganizationName: null,
        contractNo: null,
        contractName: null,
        performanceCode: null,
        companyCode: null,
        companyName: null,
        bidCode: null,
        bidEndDate: null,
        bidManager: null,
        bidManagerFullPath: null,
        contractManager: null,
        contractManagerFullPath: null,
        perStartMonth: null,
        perEndMonth: null,
        extInvestNo: null
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
      console.log(this.$attrs.params?.contractId, 'contractId')
      this.addFun(this.$attrs.params?.contractNo, this.$attrs.params?.contractId)
    } else {
      this.getFormDetail(this.$attrs.params.row.projectScoreItemsId)
    }
    this.getButtonConfig()
  },
  methods: {
    addFun (contractNo, contractId) {
      if (contractNo) {
        const dictClass = createDictClass({
          MILESTONE_SCHEDULE: [] // 里程碑名称
        })
        this.$http({
          url: '/api-pef/pj/projectScoreItems/getInfoByContractNo',
          method: 'POST',
          data: {
            contractNo
          },
          loading: true
        }).then(res => {
          this.requirementHead = res.data
          this.requirementHead.projectName = res.data?.contractName + '-' + dictClass.getDictLabel('MILESTONE_SCHEDULE', this.$attrs.params?.milestoneType)
          this.requirementHead.performanceCode = this.$attrs.params?.milestoneType
          this.requirementHead.contractHeadId = contractId
        })
        this.getContractMaterials(contractId)
      }
    },
    getContractMaterials (contractId) {
      if (contractId) {
        const query = {
          '*': {},
          contractMaterials: { '*': {} }
        }
        const saveData = transformMQL.save('ContractHead', [contractId], 'read', query)
        qa.contractDetails(saveData).then((datas) => {
          console.log(datas.data[0]?.contractMaterials, 'contractMaterials')
          const contractMaterialsOld = datas.data[0]?.contractMaterials
          const contractMaterialsNew = contractMaterialsOld.reduce((acc, cur) => {
            const isExist = acc.some(item => item.id === cur.id)
            if (!isExist) {
              acc.push(cur)
            }
            return acc
          }, [])
          this.contractMaterials = contractMaterialsNew
        })
      }
    },
    getCompanyObj (val, row) {
      this.$set(row, 'scoreManAccount', val?.username)
      this.$set(row, 'scoreManId', val?.userId)
      this.$set(row, 'scoreManName', val?.nickname)
      this.$set(row, 'email', val?.email)
    },
    templateHeadChange (val, scope) {

    },
    // 确认选择品类
    comfirmSelect (node, row) {
      console.log(node, 'node')
      const categoryAll = this.contractMaterials.find(val => val.categoryId == node)

      row.categoryId = categoryAll ? categoryAll.categoryId : ''
      row.categoryName = categoryAll ? categoryAll.categoryName : ''
      row.categoryCode = categoryAll ? categoryAll.categoryCode : ''
      this.listPefTemplateHeaderPage(row.categoryId)
    },
    // 请求绩效模型
    listPefTemplateHeaderPage (categoryId) {
      if (!categoryId) {
        return false
      }
      // 请求绩效模型
      this.$http({
        url: '/api-pef/pj/template/listPefTemplateHeaderPage',
        method: 'POST',
        data: {
          'pageNum': 1,
          'pageSize': 10000,
          'categoryId': categoryId,
          'attribute1': 'PROJECT',
          templateStatus: 'VALID'
        },
        loading: true
      }).then(res => {
        let attr = []
        res.data?.list?.forEach(e => {
          const obj = {
            label: e.templateName,
            value: e.templateHeadId
          }
          attr.push(obj)
        })
        this.templateHeadList = attr
      })
    },
    getButtonConfig () {
      this.buttonConfigInfo.save.view = this.viewUpdateButton
      this.buttonConfigInfo.submit.view = this.viewUpdateButton
      this.buttonConfigInfo.save.disabled = this.disabledUpdateButton
      this.buttonConfigInfo.submit.disabled = this.disabledUpdateButton
      this.buttonConfigInfo.cancel.view = !this.isReadOnly
      this.buttonConfigInfo.close.view = this.isReadOnly
    },
    async getWorkflowBusinessType () {
      return 'MQL_PR_SOU_REQ_CANCEL_INIT'
    },
    async getWorkflowBusinessVariables () {
      return {

      }
    },
    getCWorkflowRefName () {
      return 'workflowMulti'
    },
    async getFormDetail (projectScoreItemsId) {
      this.$http({
        url: '/api-pef/pj/projectScoreItems/getDetailById',
        method: 'GET',
        params: {
          projectScoreItemsId
        },
        loading: true
      }).then(res => {
        const data = res.data
        const { personList, ...other } = data
        this.personList = personList
        this.requirementHead = other
        this.getContractMaterials(this.requirementHead?.contractHeadId)
        this.listPefTemplateHeaderPage(personList[0]?.categoryId)
      })
    },
    addUploadOne () {
      this.personList.push({})
    },
    // 行删除
    handleDelClick (index, row) {
      this.personList.splice(index, 1)
    },
    async submitEvent (allparam) {
      this.saveBill(allparam, 'sub')
    },
    saveBill (allparam, type) {
      this.$http({
        url: '/api-pef/pj/projectScoreItems/saveOrUpdate',
        method: 'POST',
        data: allparam,
        loading: true
      }).then(res => {
        if (type == 'sub') {
          if (res.data && res.data !== '') {
            allparam.projectScoreItemsId = res.data
          }
          this.$http({
            url: '/api-pef/pj/projectScoreItems/notifyScorers',
            method: 'POST',
            data: allparam,
            loading: true
          }).then(res2 => {
            this.$message.success('操作成功')
            this.back()
          })
        } else {
          this.$message.success('操作成功')
          this.back()
        }
      })
    },
    async saveOrSubmitBill (type) {
      let allparam = {
        ...this.requirementHead,
        personList: this.personList
      }
      if (type === 'SUBMIT') {
        this.submitEvent(allparam)
      } else {
        this.saveBill(allparam, 'save')
      }
    },
    back () {
      this.$emit('tab-remove', this.$attrs.params.tabName)
      this.__setTabTodo('purchaseApplicationList.getQuerydata')
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
