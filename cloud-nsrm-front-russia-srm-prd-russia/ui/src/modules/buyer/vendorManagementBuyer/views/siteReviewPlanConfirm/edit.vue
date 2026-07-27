<template>
  <el-container
    class="sitereviewplanconfirmEdit"
    direction="vertical"
  >
    <el-main>
      <CWorkflowMulti
        ref="workflowMulti"
        v-model="activeTabName"
        :fun-params="workflowParamsInfo"
        :button-config-info="buttonConfigInfo"
        @tab-click="workflowView"
        @workflow-handler="workflowHandler"
        @click-handler="type => save(type)"
        @submit-direct="type => save(type)"
        @confirm="(type, comment) => save(type, comment)"
        @close-tab="back"
      >
        <div class="form-container">
          <el-form
            ref="form"
            :model="form"
            :rules="rules"
          >
            <srm-row :gutter="32">
              <srm-col :span="6">
                <!-- 关联计划名称 -->
                <el-form-item
                  prop="planName"
                  :label="$t('vendorMod.planName2')"
                >
                  <QuickSearch
                    :show-input="form.planName"
                    show-key="planName"
                    :scope-data="form"
                    name="scc_sup_site_review_plan"
                    :disabled="readOnly"
                    @close-quicksearch="getCategoryObj2"
                  />
                </el-form-item>
              </srm-col>
              <srm-col :span="6">
                <!-- 供应商名称 -->
                <el-form-item
                  prop="vendorName"
                  :label="$t('vendorMod.vendorName')"
                >
                  <el-input
                    v-model="form.vendorName"
                    disabled
                  />
                </el-form-item>
              </srm-col>
              <srm-col :span="6">
                <!-- 采购组织 -->
                <el-form-item
                  prop="orgName"
                  :label="$t('vendorMod.orgName')"
                >
                  <el-input
                    v-model="form.orgName"
                    disabled
                  />
                </el-form-item>
              </srm-col>
              <!-- <srm-col :span="6">
                <el-form-item prop="categoryName" label="品类名称">
                  <el-input v-model="form.categoryName" disabled />
                </el-form-item>
              </srm-col> -->
              <srm-col :span="6">
                <!-- 计划类型 -->
                <el-form-item
                  prop="planType"
                  :label="$t('vendorMod.planType')"
                >
                  <el-input
                    v-model="form.planType"
                    disabled
                  />
                </el-form-item>
              </srm-col>
              <srm-col :span="6">
                <!-- 计划出发时间 -->
                <el-form-item
                  prop="planSetOutTime"
                  :label="$t('vendorMod.planSetOutTime')"
                >
                  <el-date-picker
                    v-model="form.planSetOutTime"
                    format="yyyy-MM-dd"
                    value-format="yyyy-MM-dd"
                    :placeholder="$t('vendorMod.datePicker')"
                    :disabled="readOnly"
                  />
                </el-form-item>
              </srm-col>
              <srm-col :span="6">
                <!-- 计划到访时间 -->
                <el-form-item
                  prop="planVisitTime"
                  :label="$t('vendorMod.planVisitTime')"
                >
                  <el-date-picker
                    v-model="form.planVisitTime"
                    format="yyyy-MM-dd"
                    value-format="yyyy-MM-dd"
                    :disabled="readOnly"
                    :placeholder="$t('vendorMod.datePicker')"
                  />
                </el-form-item>
              </srm-col>
              <srm-col :span="6">
                <!-- 计划到访天数 -->
                <el-form-item
                  prop="visitDays"
                  :label="$t('vendorMod.visitDays')"
                >
                  <el-input
                    v-model="form.visitDays"
                    :disabled="readOnly"
                  />
                </el-form-item>
              </srm-col>
            </srm-row>
          </el-form>
          <el-collapse
            v-model="activeDims"
            class="tab-form-style"
          >
            <!-- 工作小组人员 -->
            <el-collapse-item
              :title="$t('vendorMod.workingGroupStaff')"
              name="1"
            >
              <el-button
                v-if="!readOnly"
                type="primary"
                class="detail-pbtn"
                style="margin:0 0 10px 0"
                @click="addDisplayItem"
              >
                {{ $t("common.new") }}
              </el-button>
              <el-table
                :data="displayItem"
                style="width: 100%"
                border
                height="250px"
                highlight-current-row
              >
                <el-table-column
                  align="center"
                  type="index"
                  :label="$t('contractMod.tabindex')"
                  width="60"
                />
                <!-- 成员账号 -->
                <el-table-column
                  align="center"
                  prop="userAccount"
                  :label="$t('vendorMod.userAccount')"
                  min-width="100"
                  :show-overflow-tooltip="true"
                >
                  <template slot-scope="scope">
                    <QuickSearch
                      :show-input="scope.row.userAccount"
                      show-key="username"
                      :scope-data="scope.row"
                      name="scc_rbac_user_display"
                      :disabled="readOnly"
                      @close-quicksearch="getCategoryObj"
                    />
                  </template>
                </el-table-column>
                <!-- 成员名称 -->
                <el-table-column
                  align="center"
                  prop="userName"
                  :label="$t('vendorMod.userName2')"
                  min-width="100"
                  :show-overflow-tooltip="true"
                />
                <!-- 手机号码 -->
                <el-table-column
                  align="center"
                  prop="userTel"
                  :label="$t('vendorMod.mobilePhone')"
                  min-width="100"
                  :show-overflow-tooltip="true"
                />
                <!-- 电子邮箱 -->
                <el-table-column
                  align="center"
                  prop="userEmail"
                  :label="$t('vendorMod.emailAddress')"
                  min-width="100"
                  :show-overflow-tooltip="true"
                />
                <!-- 岗位 -->
                <el-table-column
                  align="center"
                  prop="userPost"
                  :label="$t('bidMod.position')"
                  min-width="100"
                  :show-overflow-tooltip="true"
                >
                  <template slot-scope="scope">
                    <el-input
                      v-model="scope.row.userPost"
                      :disabled="readOnly"
                    />
                  </template>
                </el-table-column>
                <!-- 是否到现场 -->
                <el-table-column
                  align="center"
                  prop="onSiteFlag"
                  :label="$t('vendorMod.onSiteFlag')"
                  min-width="100"
                  :show-overflow-tooltip="true"
                >
                  <template slot-scope="scope">
                    <el-checkbox
                      v-model="scope.row.onSiteFlag"
                      true-label="true"
                      :disabled="readOnly"
                    />
                  </template>
                </el-table-column>
                <!-- 操作 -->
                <el-table-column
                  :label="$t('common.operation')"
                  width="60"
                >
                  <template slot-scope="scope">
                    <!-- 删除 -->
                    <el-button
                      v-if="!readOnly"
                      type="text"
                      @click="deleteOneContent(scope.$index, scope.row)"
                    >
                      {{ $t("common.delete") }}
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-collapse-item>
            <!-- 到访地址 -->
            <el-collapse-item
              :title="$t('vendorMod.visitingAddress')"
              name="2"
            >
              <el-button
                v-if="!readOnly"
                type="primary"
                class="detail-pbtn"
                style="margin:0 0 10px 0"
                @click="addDisplayItem2"
              >
                {{ $t("common.new") }}
              </el-button>
              <el-table
                :data="displayItem2"
                style="width: 100%"
                border
                height="250px"
                highlight-current-row
                :disabled="readOnly"
              >
                <el-table-column
                  align="center"
                  type="index"
                  :label="$t('contractMod.tabindex')"
                  width="60"
                />
                <!-- 国家 -->
                <el-table-column
                  align="center"
                  prop="country"
                  :label="$t('components.address.country')"
                  min-width="100"
                  :show-overflow-tooltip="true"
                >
                  <template slot-scope="scope">
                    <DictSelect
                      v-model="scope.row.country"
                      code="country"
                      filterable
                      :disabled="readOnly"
                      @change="getCountry(scope.row)"
                    />
                  </template>
                </el-table-column>
                <!-- 省 -->
                <el-table-column
                  align="center"
                  prop="province"
                  :label="$t('components.address.area')"
                  min-width="100"
                  :show-overflow-tooltip="true"
                >
                  <template slot-scope="scope">
                    <DictSelect
                      v-model="scope.row.province"
                      code="PROVINCE"
                      custom-select-type="PROVINCE"
                      :disabled="scope.row.country !== 'CN' || readOnly"
                    />
                  </template>
                </el-table-column>
                <!-- 城市 -->
                <el-table-column
                  align="center"
                  prop="city"
                  :label="$t('components.address.city')"
                  min-width="100"
                  :show-overflow-tooltip="true"
                >
                  <template slot-scope="scope">
                    <DictSelect
                      v-model="scope.row.city"
                      :code="scope.row.province"
                      custom-select-type="CITY"
                      :disabled="scope.row.country !== 'CN' || readOnly"
                    />
                  </template>
                </el-table-column>
                <!-- 详细地址 -->
                <el-table-column
                  align="center"
                  prop="addressDetail"
                  :label="$t('components.address.detailAddress')"
                  min-width="100"
                  :show-overflow-tooltip="true"
                >
                  <template slot-scope="scope">
                    <!-- 请输入内容 -->
                    <el-input
                      v-model="scope.row.addressDetail"
                      :placeholder="$t('common.pleaseTypeContents')"
                      :disabled="readOnly"
                    />
                  </template>
                </el-table-column>
                <!-- 邮政编码 -->
                <el-table-column
                  align="center"
                  prop="postCode"
                  :label="$t('components.address.postalCode')"
                  min-width="100"
                  :show-overflow-tooltip="true"
                >
                  <template slot-scope="scope">
                    <!-- 请输入内容 -->
                    <el-input
                      v-model="scope.row.postCode"
                      :disabled="readOnly"
                    />
                  </template>
                </el-table-column>
                <!-- 地址备注 -->
                <el-table-column
                  align="center"
                  prop="siteComment"
                  :label="$t('components.address.remark')"
                  min-width="100"
                  :show-overflow-tooltip="true"
                >
                  <template slot-scope="scope">
                    <!-- 请输入内容 -->
                    <el-input
                      v-model="scope.row.siteComment"
                      :placeholder="$t('common.pleaseTypeContents')"
                      :disabled="readOnly"
                    />
                  </template>
                </el-table-column>
                <!-- 操作 -->
                <el-table-column
                  :label="$t('common.operation')"
                  width="60"
                >
                  <template slot-scope="scope">
                    <!-- 删除 -->
                    <el-button
                      v-if="!readOnly"
                      type="text"
                      @click="deleteOneContent2(scope.$index, scope.row)"
                    >
                      {{ $t("common.delete") }}
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-collapse-item>
          </el-collapse>
        </div>
      </CWorkflowMulti>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoMixin } from '@/utils/mixins'
import MainHeader from 'lib@/components/Table/MainHeader'
import CToolbar from 'lib@/components/c-toolbar'
import QuickSearch from 'lib@/components/QuickSearch'
import { adaptDictData } from '@/utils'
import { getDictItem, getRegion } from '@/api/common'
import WorkflowCommon from '@/library/mixins/workflow-common'
import { siteReviewPlanConfirm } from 'modb@/vendorManagementBuyer/api/vendorManagement'
import { supCommonApi } from 'modb@/vendorManagementBuyer/api/supApi'

export default {
  name: 'SitereviewplanconfirmEdit',
  components: {
    MainHeader,
    CToolbar,
    QuickSearch
  },
  mixins: [tabTodoMixin, WorkflowCommon],
  data () {
    return {
      flag: '',
      planTypeAll: [
        {
          value: 'YEAR',
          label: this.$t('vendorMod.YEAR') // 年度
        },
        {
          value: 'HALF_YEAR',
          label: this.$t('vendorMod.HALF_YEAR') // 半年度
        },
        {
          value: 'QUARTER',
          label: this.$t('vendorMod.QUARTER') // 季度
        },
        {
          value: 'ALLOW',
          label: this.$t('vendorMod.ALLOW') // 准入
        },
        {
          value: 'MONTH',
          label: this.$t('vendorMod.MONTH') // 月度
        }
      ],
      displayItem: [],
      displayItem2: [],
      activeDims: ['1', '2', '3', '4', '5', '6', '7'],
      form: {},
      rules: {
        planName: [{ required: true, message: this.$t('contractMod.mgsPlanName') }] // '请输入关联计划名称'
      },
      readOnly: false
    }
  },
  computed: {
    viewUpdateButton () {
      // 用来控制保存、提交按钮是否可见。如果自定义按钮则无需添加
      if (this.workflowParamsInfo.integrationMode !== 'None') {
        return !this.readOnly
      } else {
        if (this.flag == 'approve' || !this.readOnly) {
          return true
        } else {
          return false
        }
      }
    },
    workflowBusinessId () {
      // 用来指定工作流的业务ID
      return this.form.planConfirmId ? this.form.planConfirmId : null
    },

    // 展示工作流tab页
    workflowTabDisabled () {
      if (this.flag == 'approve') {
        return false
      } else {
        return true
      }
    }
  },
  watch: {
    viewUpdateButton () {
      this.buttonConfigInfo.save.view = false
      this.buttonConfigInfo.submit.view = this.viewUpdateButton
    }
  },
  created () {
    this.buttonConfigInfo.save.view = false
    this.buttonConfigInfo.submit.view = this.viewUpdateButton
    this.buttonConfigInfo.submit.name = '提交'
    this.buttonConfigInfo.cancel.view = false
    this.buttonConfigInfo.close.view = false
    // console.log(this.buttonConfigInfo)
  },
  mounted () {
    const { flag, row, readOnly, datas } = this.$attrs.params
    this.flag = flag
    this.readOnly = readOnly
    if (flag === 'edit' || flag === 'approve' || flag === 'view') {
      this.form = row
      this.initialization(row)
      this.planTypeAll.forEach(datas => {
        if (datas.value == this.form.planType) {
          this.$set(this.form, 'planType', datas.label)
        }
      })
    }
    if (datas) {
      this.$set(this.form, 'siteReviewPlanId', datas.siteReviewPlanId)
      this.$set(this.form, 'vendorName', datas.vendorName)
      this.$set(this.form, 'orgName', datas.orgName)
      this.$set(this.form, 'categoryName', datas.categoryName)
      this.$set(this.form, 'planName', datas.planName)
      this.$set(this.form, 'planType', datas.planType)
      this.planTypeAll.forEach(data => {
        if (data.value == this.form.planType) {
          this.$set(this.form, 'planType', data.label)
        }
      })
      supCommonApi.findCategory(row.vendorId).then(res => {
        const data = res.data.companyInfo
        const obj = {
          addressDetail: data.companyAddress,
          city: data.companyCity,
          country: data.companyCountry,
          province: data.companyProvince
        }
        this.displayItem2.push(obj)
      })
    }
  },
  methods: {
    async getWorkflowBusinessVariables () { // 定义流程变量，如果没有可以不添加
      return {
        formNo: this.form.planConfirmCode
      }
    },
    // 删除单行
    deleteOneContent (index, row) {
      this.displayItem.splice(index, 1)
    },
    deleteOneContent2 (index, row) {
      this.displayItem2.splice(index, 1)
    },
    getCountry (row) {
      // 选择国外就清理省市区，并且禁用
      if (row.country !== 'CN') {
        row.province = null
        row.city = null
      }
    },
    back () {
      this.$emit('tab-remove', this.$attrs.tabName)
      this.__setTabTodo('sitereviewplanconfirmList.getQuerydata')
    },
    // 指定工作流的业务类型，在定义工作流时指定
    async getWorkflowBusinessType () {
      return 'PLANCONFIRM'
    },
    getCWorkflowRefName () {
      return 'workflowMulti' // 对应CWorkflowMulti标签中的ref
    },
    initialization (row) {
      const planConfirmId = row.planConfirmId
      this.form.planConfirmId = planConfirmId
      let _this = this
      siteReviewPlanConfirm.planGet(planConfirmId).then(res => {
        if ((res.data.approveStatus == 'DRAFT' || res.data.approveStatus == 'VENDOR_REJECT' || res.data.approveStatus == 'REJECT') && this.flag !== 'view') {
          this.readOnly = false
        } else {
          this.readOnly = true
        }
        console.log(res)
        this.form.planSetOutTime = res.data.planSetOutTime
        this.form.planVisitTime = res.data.planVisitTime
        this.form.visitDays = res.data.visitDays
        _this.displayItem = res.data.personList
        _this.displayItem2 = res.data.addressList
        _this.form.planName = res.data.planName
        if (res.data.approveStatus !== 'DRAFT' &&
          res.data.approveStatus !== 'VENDOR_REJECT' &&
          res.data.approveStatus !== 'REJECT') {
          this.readOnly = true
        }
      })
    },
    addDisplayItem2 () {
      this.displayItem2.push({})
    },
    addDisplayItem () {
      this.displayItem.push({})
    },
    getCategoryObj (val, scope) {
      scope.userAccount = val ? val.username : ''
      scope.userName = val ? val.nickname : ''
      scope.userId = val ? val.userId : ''
      scope.userTel = val ? val.phone : ''
      scope.userEmail = val ? val.email : ''
      this.displayItem.push({})
      this.displayItem.pop()
    },
    getCategoryObj2 (val, scope) {
      if (this.readOnly) {
        this.$message.error(this.$t('vendorMod.readOnlyStatusCannotBeEdited')) // 只读状态不能编辑
        return false
      }
      console.log(val)
      this.$set(this.form, 'siteReviewPlanId', val.siteReviewPlanId)
      this.$set(this.form, 'planName', val.planName)
      this.$set(this.form, 'vendorName', val.vendorName)
      this.$set(this.form, 'orgName', val.orgName)
      this.$set(this.form, 'categoryName', val.categoryName)
      this.planTypeAll.forEach(datas => {
        if (datas.value == val.planType) {
          this.$set(this.form, 'planType', datas.label)
        }
      })
      let id = val.siteReviewPlanId
      this.displayItem2 = []
      supCommonApi.findCategory(val.vendorId).then(res => {
        const data = res.data.companyInfo
        const obj = {
          addressDetail: data.companyAddress,
          city: data.companyCity,
          country: data.companyCountry,
          province: data.companyProvince
        }
        this.displayItem2.push(obj)
      })
    },
    save (bol) {
      this.$refs.form.validate(result => {
        if (result) {
          const { flag } = this.$attrs.params
          console.log(flag)
          // 新增时不用提交主键值
          let datas = this.form
          datas.personList = this.displayItem
          datas.addressList = this.displayItem2
          if (bol == 'save') {
            datas.submitFlag = 'SAVE'
          } else {
            datas.submitFlag = 'SUBMIT'
          }
          if (flag === 'add') {
            siteReviewPlanConfirm.planAdd(datas)
              .then(async res => {
                this.$message({
                  type: 'success',
                  message: res.message
                })
                this.cancelBill()
                await this.getFormDetail(res.data)
                await this.handlerAfter(datas.submitFlag)
              })
          } else if (flag === 'edit') {
            siteReviewPlanConfirm.planModify(datas)
              .then(async res => {
                this.$message({
                  type: 'success',
                  message: res.message
                })
                this.cancelBill()
                await this.getFormDetail(res.data)
                await this.handlerAfter(datas.submitFlag)
              })
          } else if (flag === 'approve') {
          this.handlerAfter('SUBMIT')
        }
        } else {
          this.__focus_error__()
        }
      })
    },
    cancelBill () {
      const { flag, row } = this.$attrs.params
      if (flag === 'add') {
        // this.$emit("tab-remove", "sitereviewplanconfirmEdit");
        this.$router.push('/vendorManagement/siteReviewPlanConfirm')
      } else {
        this.$emit(
          'tab-remove',
          'sitereviewplanconfirmEdit' + row.planConfirmId
        )
      }
      this.__setTabTodo('sitereviewplanconfirmList.getQuerydata')
    },
    // 上传附件成功
    handleUploadSuccess (file, row, key) {
      const { id, name } = file
      row[key] = id.toString()
    },
    // 删除文件
    handleAttachmentRemove (row, key) {
      row[key] = ''
    }
  }
}
</script>
<style scoped lang="scss">
.sitereviewplanconfirmEdit {
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
