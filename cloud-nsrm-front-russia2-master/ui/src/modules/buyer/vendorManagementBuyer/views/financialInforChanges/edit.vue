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
          <el-collapse
            v-model="activeDims"
            class="tab-form-style"
          >
            <!--供应商标准账期变更信息-->
            <el-collapse-item
              :title="$t('vendorMod.financialInforChangesInfor')"
              name="1"
            >
              <el-form
                ref="form"
                :model="form"
                :rules="rules"
                :disabled="curOpt === 'view' || form.approveStatus === 'SUBMITTED'"
              >
                <srm-row :gutter="32">
                  <srm-col>
                    <!-- 单据名称 -->
                    <el-form-item
                      prop="changeHeaderName"
                      :label="$t('vendorMod.relegation.billName')"
                    >
                      <el-input
                        v-model="form.changeHeaderName"
                        :disabled="curOpt === 'view'"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <!-- 单据编码 -->
                    <el-form-item
                      prop="changeHeaderCode"
                      :label="$t('dataConfMod.sequenceCode')"
                    >
                      <el-input
                        v-model="form.changeHeaderCode"
                        disabled
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <!-- 状态 -->
                    <el-form-item
                      prop="approveStatus"
                      :label="$t('dataConfMod.triggerState')"
                    >
                      <DictSelect
                        v-model="form.approveStatus"
                        code="APPROVE_STATUS_TYPE"
                        disabled
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <!-- 变更组织 -->
                    <el-form-item
                      prop="orgId"
                      :label="$t('vendorMod.changeOrganization')"
                    >
                      <OrganizationSelector
                        ref="organizationSelector"
                        v-model="form.orgId"
                        :disabled="curOpt === 'view'"
                        :parent-id="-1"
                        node-type="OU"
                        :placeholder="$t('common.pleaseSelect')"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <!-- 创建人 -->
                    <el-form-item
                      prop="createdFullName"
                      :label="$t('common.creator')"
                    >
                      <el-input
                        v-model="form.createdFullName"
                        disabled
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <!-- 申请部门 -->
                    <el-form-item
                      prop="department"
                      :label="$t('purchaseDemand.ceeaDepartment')"
                    >
                      <el-input
                        v-model="form.department"
                        :disabled="curOpt === 'view'"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <!-- 创建时间 -->
                    <el-form-item
                      prop="creationDate"
                      :label="$t('purchaseDemand.creationDate')"
                    >
                      <el-date-picker
                        v-model="form.creationDate"
                        :format="$formatDatePickerTime"
                        value-format="yyyy-MM-dd HH:mm:ss"
                        disabled
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col :init-col="1">
                    <!-- 备注 -->
                    <el-form-item
                      prop="remark"
                      :label="$t('components.eio.headers.remark')"
                    >
                      <el-input
                        v-model="form.remark"
                        :autosize="{ minRows: 2, maxRows: 4}"
                        type="textarea"
                        :disabled="curOpt === 'view'"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col :init-col="1">
                    <!-- 起草人节点建议 -->
                    <el-form-item
                      prop="advice"
                      :label="$t('vendorMod.advice')"
                    >
                      <el-input
                        v-model="form.advice"
                        :autosize="{ minRows: 2, maxRows: 4}"
                        type="textarea"
                        :disabled="curOpt === 'view'"
                      />
                    </el-form-item>
                  </srm-col>
                </srm-row>
              </el-form>
            </el-collapse-item>

            <!-- 标准账期变更明细 -->
            <el-collapse-item
              :title="$t('vendorMod.accountingChangeDetails')"
              name="2"
            >
              <QuickSearch
                v-if="!readOnly && curOpt != 'view'"
                :show-input="form.advice"
                show-key="username"
                name="scc_sup_company_info2"
                :multiSelect="true"
                :showButton="true"
                :btnTitle="$t('common.new')"
                @close-quicksearch="getCategoryObj"
              />
              <!--变更前-->
              <div class="changeTitle changeTitleTop">
                <i />{{ $t("supplierChange.beforeChange") }}
              </div>
              <el-table
                :data="form.changeBeforeList"
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
                <!-- 供应商编码 -->
                <el-table-column
                  align="center"
                  prop="companyCode"
                  :label="$t('vendorMod.vendorCode')"
                  min-width="150"
                  :show-overflow-tooltip="true"
                />
                <!-- 供应商名称 -->
                <el-table-column
                  align="center"
                  prop="companyName"
                  :label="$t('vendorMod.vendorName')"
                  min-width="150"
                  :show-overflow-tooltip="true"
                />
                <!-- 工厂代码 -->
                <el-table-column
                  align="center"
                  prop="factoryCode"
                  :label="$t('vendorMod.factoryCode')"
                  min-width="150"
                  :show-overflow-tooltip="true"
                />
                <!-- 结算币种 -->
                <el-table-column
                  align="center"
                  prop="clearCurrency"
                  min-width="150"
                  :show-overflow-tooltip="true"
                >
                  <template slot="header">
                    <i class="toRequired">*</i>{{ $t('vendorMod.clearCurrency') }}
                  </template>
                  <template slot-scope="scope">
                    <DictSelect
                      v-model="scope.row.clearCurrency"
                      code="BID_TENDER_CURRENCY"
                      disabled
                    />
                  </template>
                </el-table-column>
                <!-- 付款方式 -->
                <el-table-column
                  align="center"
                  prop="paymentMethod"
                  width="150"
                >
                  <template slot="header">
                    <i class="toRequired">*</i>{{ $t('vendorMod.paymentMethod') }}
                  </template>
                  <template slot-scope="scope">
                    <DictSelect
                      v-model="scope.row.paymentMethod"
                      code="PAYMENT_METHOD"
                      disabled
                    />
                  </template>
                </el-table-column>
                <!-- 付款账期 -->
                <el-table-column
                  align="center"
                  prop="paymentTerms"
                  width="150"
                >
                  <template slot="header">
                    <i class="toRequired">*</i>{{ $t('vendorMod.paymentTerms') }}
                  </template>
                  <template slot-scope="scope">
                    <DictSelect
                      v-model="scope.row.paymentTerms"
                      code="PAYMENT_TERMS"
                      disabled
                    />
                  </template>
                </el-table-column>
              </el-table>
              <!--变更后-->
              <el-button
                v-if="curOpt !== 'view'"
                type="primary"
                class="detail-pbtn"
                style="margin-top: 15px"
                @click="addChangeAfterList"
              >
                {{ $t('common.new') }}
              </el-button>
              <div class="changeTitle changeTitleTop margin-title-top">
                <i />{{ $t("supplierChange.afterChange") }}
              </div>
              <el-table
                ref="changeAfterList"
                :data="form.changeAfterList"
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
                <!-- 供应商编码 -->
                <el-table-column
                  align="center"
                  prop="companyCode"
                  :label="$t('vendorMod.vendorCode')"
                  min-width="150"
                  :show-overflow-tooltip="true"
                />
                <!-- 供应商名称 -->
                <el-table-column
                  align="center"
                  prop="companyName"
                  :label="$t('vendorMod.vendorName')"
                  min-width="150"
                  :show-overflow-tooltip="true"
                >
                  <template slot-scope="scope">
                    <el-select
                      v-if="scope.row.Etype == '1' && form.changeBeforeList.length>0"
                      v-model="scope.row.companyId"
                      style="width: 100%"
                      :disabled="curOpt === 'view' || form.approveStatus === 'SUBMITTED'"
                      @change="companyChangeAfter(scope.row)"
                    >
                      <el-option
                        v-for="item in showCompany(form.changeBeforeList)"
                        :key="item.companyId"
                        :label="item.companyName"
                        :value="item.companyId"
                      />
                    </el-select>
                    <QuickSearch
                      v-else-if="scope.row.Etype == '1' && form.changeBeforeList.length == 0"
                      :show-input="scope.row.companyName"
                      show-key="companyName"
                      :scope-data="scope.row"
                      name="scc_sup_company_info2"
                      :disabled="curOpt === 'view'"
                      @close-quicksearch="getCategoryObjNew"
                    />
                    <div v-else>
                      {{ scope.row.companyName }}
                    </div>
                  </template>
                </el-table-column>
                <!-- 工厂代码 -->
                <el-table-column
                  align="center"
                  prop="factoryCode"
                  :label="$t('vendorMod.factoryCode')"
                  min-width="150"
                  :show-overflow-tooltip="true"
                >
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.factoryCode" :disabled="curOpt === 'view' || form.approveStatus === 'SUBMITTED'" onKeyUp="value=value.replace(/[^\w\\/]/ig,'')" />
                  </template>
                </el-table-column>
                <!-- 结算币种 -->
                <el-table-column
                  align="center"
                  prop="clearCurrency"
                  min-width="150"
                  :show-overflow-tooltip="true"
                >
                  <template slot="header">
                    <i class="toRequired">*</i>{{ $t('vendorMod.clearCurrency') }}
                  </template>
                  <template slot-scope="scope">
                    <DictSelect
                      v-model="scope.row.clearCurrency"
                      code="BID_TENDER_CURRENCY"
                      :disabled="curOpt === 'view' || form.approveStatus === 'SUBMITTED'"
                    />
                  </template>
                </el-table-column>
                <!-- 付款方式 -->
                <el-table-column
                  align="center"
                  prop="paymentMethod"
                  width="150"
                >
                  <template slot="header">
                    <i class="toRequired">*</i>{{ $t('vendorMod.paymentMethod') }}
                  </template>
                  <template slot-scope="scope">
                    <DictSelect
                      v-model="scope.row.paymentMethod"
                      code="PAYMENT_METHOD"
                      :disabled="curOpt === 'view' || form.approveStatus === 'SUBMITTED'"
                    />
                  </template>
                </el-table-column>
                <!-- 付款账期 -->
                <el-table-column
                  align="center"
                  prop="paymentTerms"
                  width="150"
                >
                  <template slot="header">
                    <i class="toRequired">*</i>{{ $t('vendorMod.paymentTerms') }}
                  </template>
                  <template slot-scope="scope">
                    <DictSelect
                      v-model="scope.row.paymentTerms"
                      code="PAYMENT_TERMS"
                      :disabled="curOpt === 'view' || form.approveStatus === 'SUBMITTED'"
                    />
                  </template>
                </el-table-column>
                <el-table-column
                  v-if="curOpt !== 'view'"
                  align="center"
                  prop="operation"
                  :label="$t('common.operation')"
                  width="100"
                  fixed="right"
                >
                  <template slot-scope="scope">
                    <el-button type="text" :disabled="isReadOnly" @click="financeDel(scope.$index, scope.row)">
                      {{
                        $t('common.delete')
                      }}
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-collapse-item>
            <!-- 附件 -->
            <el-collapse-item
              :title="$t('vendorMod.relegation.accessory')"
              name="3"
            >
              <FileDynamic
                ref="sceneAttachment"
                v-model="form.fileUploads"
                scene-module-code="SCENE_FINANCE_INFO_CHANGE_HEADER"
                :business-id="form.changeHeaderId"
                :editable="curOpt !== 'view' && form.approveStatus !== 'SUBMITTED'"
              />
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
import FileDynamic from '@/library/components/c-file-management/file-dynamic'
import OrganizationSelector from 'lib@/components/organization-selector'
import WorkflowCommon from '@/library/mixins/workflow-common'
import { financeInfoChangeApi } from 'modb@/vendorManagementBuyer/api/vendorManagement'

export default {
  name: 'FinancialInforChangesEdit',
  components: {
    MainHeader,
    CToolbar,
    QuickSearch,
    OrganizationSelector,
    FileDynamic
  },
  mixins: [tabTodoMixin, WorkflowCommon],
  data () {
    return {
      curOpt: '',
      flag: '',
      changeHeaderId: '',
      activeDims: ['1', '2', '3', '4', '5', '6', '7'],
      form: {
        fileUploads: [],
        changeBeforeList: [],
        changeAfterList: [],
        approveStatus: 'DRAFT'
      },
      rules: {
        changeHeaderName: [{ required: true, message: this.$t('dataConfMod.msgSeqName') }], // '请输入单据名称'
        orgId: [{ required: true, message: this.$t('dataConfMod.msgOrgName') }] // '请输入变更组织'
      },
      readOnly: false
    }
  },
  computed: {
    viewUpdateButton () {
      // 用来控制保存、提交按钮是否可见。如果自定义按钮则无需添加
      return this.curOpt != 'view' && (this.form.approveStatus == 'DRAFT' || this.form.approveStatus === 'REJECTED' || this.form.approveStatus === 'WITHDRAW')
    },
    workflowBusinessId () {
      // 用来指定工作流的业务ID
      return this.changeHeaderId ? this.changeHeaderId : null
    },

    // 展示工作流tab页
    workflowTabDisabled () {
      return this.form.approveStatus === 'DRAFT'
    }
  },
  watch: {
    viewUpdateButton () {
      this.buttonConfigInfo.save.view = this.viewUpdateButton
      this.buttonConfigInfo.submit.view = this.viewUpdateButton
    }
  },
  created () {
    this.buttonConfigInfo.save.view = this.viewUpdateButton
    this.buttonConfigInfo.submit.view = this.viewUpdateButton
    this.buttonConfigInfo.cancel.view = false
    this.buttonConfigInfo.close.view = false
  },
  mounted () {
    const { flag, row, readOnly, datas } = this.$attrs.params
    this.curOpt = flag
    this.readOnly = readOnly
    if (flag === 'edit' || flag === 'approved' || flag === 'view') {
      this.changeHeaderId = row.changeHeaderId
      this.initialization(row.changeHeaderId)
    } else {
      if (flag === 'add') {
        this.$nextTick(() => {
          this.$refs.sceneAttachment.loadFileInfo()
        })
      }
    }
  },
  methods: {
    financeDel (index) {
      this.form.changeAfterList.splice(index, 1)
    },
    companyChangeAfter (val) {
      console.log(val)
      this.form.changeBeforeList.forEach(e => {
        if (e.companyId == val.companyId) {
          val.companyCode = e.companyCode
          val.companyName = e.companyName
        }
      })
    },
    // 供应商去重
    showCompany (arr) {
      let newArr = arr
      let bolArr = []
      console.log(arr.length)
      if (arr.length > 0) {
        newArr.forEach(e => {
          let bol = 1
          bolArr.forEach(u => {
            if (e.companyId == u.companyId) {
              bol = 0
            }
          })
          if (bol == 1) {
            bolArr.push(e)
          }
        })
      }
      return bolArr
    },
    addChangeAfterList () {
      this.form.changeAfterList.push({ Etype: 1 })
    },
    // async getWorkflowBusinessVariables () { // 定义流程变量，如果没有可以不添加
    //   return {
    //     formNo: this.changeHeaderId ? this.changeHeaderId : null
    //   }
    // },
    getCountry (row) {
      // 选择国外就清理省市区，并且禁用
      if (row.country !== 'CN') {
        row.province = null
        row.city = null
      }
    },
    back () {
      this.$emit('tab-remove', this.$attrs.tabName)
      this.__setTabTodo('list.getQuerydata')
    },
    // 指定工作流的业务类型，在定义工作流时指定
    async getWorkflowBusinessType () {
      return 'FINANCECHANGE'
    },
    getCWorkflowRefName () {
      return 'workflowMulti' // 对应CWorkflowMulti标签中的ref
    },
    initialization (changeHeaderId) {
      financeInfoChangeApi.getDetail(changeHeaderId).then(res => {
        if (res.code == '0') {
          this.form = res.data
        } else {
          this.$message.error(res.message)
        }
        this.$refs.sceneAttachment.loadFileInfo()
      })
    },
    getCategoryObjNew (val, scope) {
      console.log(val)
      console.log(scope)
      scope.companyName = val ? val.companyName : ''
      this.$set(scope, 'companyCode', val ? val.companyCode : '')
      scope.companyId = val ? val.companyId : ''
    },
    getCategoryObj (val, scope) {
      let companyIdList = []
      try {
        val.forEach(e => {
          companyIdList.push(e.companyId)
        })
      } catch (err) {
        companyIdList.push(val.companyId)
      }
      let obj = {
        companyIdList: companyIdList,
        orgId: this.form.orgId
      }
      financeInfoChangeApi.listByCompanyIdAndOrgId(obj).then(res => {
        console.log(res)
        if (res.code == '0') {
          if (res.data.length > 0) {
            this.$set(this.form, 'changeBeforeList', JSON.parse(JSON.stringify(res.data)))
            this.$set(this.form, 'changeAfterList', JSON.parse(JSON.stringify(res.data)))
          } else {
            this.$message.error(this.$t('vendor.financialInformationIsEmpty'))
          }
        } else {
          this.$message.error(res.message)
        }
      })
    },
    save (type) {
      this.$refs.form.validate(result => {
        if (result) {
          if (this.form.changeAfterList.length == 0) {
            this.__jump_error__(
              'changeAfterList',
              null,
              this.$t('vendorMod.msgFinanceInfoChange')
            )
            return false
          }
          let bolAfter = false
          this.form.changeAfterList.forEach(e => {
            if (!e.clearCurrency || !e.paymentMethod || !e.paymentTerms) {
              bolAfter = true
            }
          })
          if (bolAfter) {
            this.__jump_error__(
              'changeAfterList',
              null,
              this.$t('vendorMod.msgFinanceInfo')
            )
            return false
          }
          financeInfoChangeApi.saveOrUpdateFinanceInfoChangeHeader(this.form).then(async (res) => {
            if (res.code == '0') {
              this.$message.success(res.message)
              if (type == 'SAVE') {
                if (this.$attrs.params.flag == 'edit') {
                  this.$emit('tab-remove', this.$attrs.params.tabName)
                } else {
                  this.$emit('tab-remove', 'financialInforChangesDeatil')
                }
                this.__setTabTodo('financialInforChangesList.getQuerydata')
              } else {
                this.changeHeaderId = res.data.changeHeaderId
                await this.initialization(res.data.changeHeaderId)
                await this.handlerAfter(type)
                // 切换到工作流tab页
                var workflowMode = this.workflowParamsInfo.integrationMode === 'Product' || this.workflowParamsInfo.integrationMode === 'Iframe' || this.workflowParamsInfo.integrationMode === 'Self'
                if (workflowMode) {
                  this.activeTabName = 'workflowTab'
                }
                // if (this.workflowParamsInfo.integrationMode === 'None') {
                //   this.$message.success(this.$t('common.success'))
                //   this.back()
                // }
              }
            }
          })
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
.changeTitle{
  background-color: #F6F6F6;
  font-size: 14px ;
  color: #393E45 ;
  overflow: hidden;
  line-height: 40px;
  margin-bottom:10px;
  font-weight: 400;
}
.changeTitleTop{
  margin-top: 10px;
}
.changeTitle i{
  width: 4px;
  height: 18px;
  background-color: #0077FF;
  margin: 11px 10px 11px 16px;
  display: block;
  float: left;
}
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
