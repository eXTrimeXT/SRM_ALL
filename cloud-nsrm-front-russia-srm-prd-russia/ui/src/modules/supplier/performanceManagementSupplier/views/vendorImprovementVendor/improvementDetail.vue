<template>
  <el-container
    class="flex-container the-vendorEffectDetail-detail"
    direction="vertical"
  >
    <el-main>
      <el-collapse
        v-model="activeDims"
        class="tab-form-style"
        style="margin-bottom: 80px"
      >
        <el-collapse-item
          :title="$t('perfMod.vendorImprovementBillInfo')"
          name="1"
        >
          <el-form
            ref="improveForm"
            :model="improveForm"
            :rules="improveFormRules"
            class="form-fill-style"
            :disabled="curOpt === 'view' || improveForm.status !== 'DRAFT'"
          >
            <srm-row>
              <srm-col :initCol="4">
                <el-form-item :label="$t('perfMod.improveNo')">
                  <el-input
                    v-model="improveForm.improveNo"
                    disabled
                  />
                </el-form-item>
              </srm-col>
              <srm-col :initCol="4">
                <el-form-item
                  :label="$t('perfMod.improveDate')"
                  prop="improveDateStart"
                >
                  <el-date-picker
                    v-model="rangeDate"
                    type="daterange"
                    format="yyyy-MM-dd"
                    value-format="yyyy-MM-dd"
                    :range-separator="$t('components.to')"
                    :start-placeholder="$t('components.beginDate')"
                    :end-placeholder="$t('components.dateClosed')"
                    @change="dateChangeHandel"
                  />
                </el-form-item>
              </srm-col>
              <srm-col :initCol="4">
                <el-form-item
                  :label="$t('perfMod.improveTitle')"
                  prop="improveTitle"
                >
                  <el-input v-model="improveForm.improveTitle" />
                </el-form-item>
              </srm-col>
              <srm-col :initCol="4">
                <el-form-item
                  :label="$t('perfMod.improveProject')"
                  prop="improveProject"
                >
                  <el-input v-model="improveForm.improveProject" />
                </el-form-item>
              </srm-col>
              <srm-col :initCol="4">
                <el-form-item
                  :label="$t('perfMod.respFullName2')"
                  prop="respFullName"
                >
                  <el-input v-model="improveForm.respFullName">
                    <el-button
                      slot="append"
                      icon="el-icon-search"
                      @click="selectPeople"
                    />
                  </el-input>
                </el-form-item>
              </srm-col>
              <srm-col :initCol="4">
                <el-form-item
                  :label="$t('perfMod.fullPathId')"
                  prop="organizationId"
                >
                  <OrganizationSelectTree
                    v-model="improveForm.organizationId"
                    :parent-id="-1"
                    node-type="OU"
                    :scope="improveForm"
                    :placeholder="$t('common.pleaseSelect')"
                    :disabled="curOpt === 'view' || improveForm.status !== 'DRAFT'"
                    @select="addOrgHandle"
                    @change="changeOrgHandle"
                  />
                </el-form-item>
              </srm-col>
              <srm-col :initCol="4">
                <el-form-item
                  :label="$t('perfMod.vendorName')"
                  prop="vendorCode"
                >
                  <el-select
                    v-model="improveForm.vendorName"
                    filterable
                    remote
                    :placeholder="$t('perfMod.enterKeywords')"
                    :remote-method="remoteMethod"
                    clearable
                    automatic-dropdown
                    @change="elSelectChange"
                  >
                    <el-option
                      v-for="item in options"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value"
                    />
                  </el-select>
                </el-form-item>
              </srm-col>
              <srm-col :initCol="4">
                <el-form-item
                  :label="$t('perfMod.categoryName')"
                  prop="categoryName"
                >
                  <CCategorySelect
                    v-model="improveForm.categoryName"
                    :placeholder="$t('vendorMod.msgCategoryNormalizer')"
                    :scope="improveForm"
                    :disabled="curOpt === 'view'"
                    show-key="categoryName"
                    @select="comfirmCategory"
                  />
                </el-form-item>
              </srm-col>

              <srm-col :initCol="4">
                <el-form-item :label="$t('perfMod.improveFiles')">
                  <div>
                    <SrmCommonFile
                      :extra-data="fileInfo"
                      :default-file="{
                        fileId: improveForm.fileUploadId,
                        fileName: improveForm.fileSourceName
                      }"
                      :readonly="!(improveForm.status == '' || improveForm.status == 'DRAFT')"
                      @on-change="({file}) => handleUploadSuccess(file)"
                    />
                  </div>
                </el-form-item>
              </srm-col>
              <srm-col :initCol="2">
                <el-form-item
                  :label="$t('perfMod.improveExplanation')"
                  prop="explanation"
                >
                  <el-input
                    v-model="improveForm.explanation"
                    type="textarea"
                    :rows="3"
                  />
                </el-form-item>
              </srm-col>
              <srm-col :initCol="4">
                <el-form-item
                  :label="$t('perfMod.relatedPerformanceSheet')"
                  prop="overallScoreId"
                >
                  <el-select
                    v-model="improveForm.overallScoreId"
                    :disabled="disOverallScoreId"
                    :placeholder="$t('perfMod.enterKeywords')"
                  >
                    <!-- @change="elSelectChange" -->
                    <el-option
                      v-for="item in overallScoreOptions"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value"
                    />
                  </el-select>
                </el-form-item>
              </srm-col>
            </srm-row>
          </el-form>
          <template v-if="modelConfig && modelConfig.dimConfigMap['baseInfo']">
            <ModelConfigForm
              ref="baseInfo"
              :dimConfig="modelConfig.dimConfigMap['baseInfo']"
              :formValue="dimDataValue"
            />
          </template>
          <!-- General attribute configuration 通用属性配置 -->
          <!-- <gac ></gac> -->
        </el-collapse-item>
        <el-collapse-item
          v-if="
            (curRole === 'VENDOR' && improveForm.status === 'IMPROVING') ||
              (curRole === 'BUYER' &&
              improveForm.status === 'IMPROVING' &&
              improveForm.vIsFeedback === 'Y') ||
              improveForm.status === 'UNDER_EVALUATION' ||
              improveForm.status === 'EVALUATED'
          "
          :title="$t('perfMod.vendorFeedbackFormInfo')"
          name="3"
        >
          <el-form
            ref="vendorForm"
            :model="vendorFeedbackForm"
            class="form-fill-style"
            :disabled="curOpt === 'view' || improveForm.status === 'UNDER_EVALUATION'"
          >
            <srm-row>
              <srm-col :initCol="4">
                <el-form-item
                  :label="$t('perfMod.vFeedbackTime')"
                  prop="vFeedbackTime"
                >
                  <el-date-picker
                    v-model="vendorFeedbackForm.vFeedbackTime"
                    :placeholder="$t('perfMod.vFeedbackTime')"
                    format="yyyy-MM-dd"
                    value-format="yyyy-MM-dd HH:mm:ss"
                  />
                </el-form-item>
              </srm-col>
              <srm-col :initCol="4">
                <el-form-item :label="$t('perfMod.vFeedbackFileUploadId')">
                  <div>
                    <SrmCommonFile
                      :extra-data="fileInfo"
                      :default-file="{
                        fileId: vendorFeedbackForm.vFeedbackFileUploadId,
                        fileName: vendorFeedbackForm.vFeedbackFileSourceName
                      }"
                      :readonly="!(curRole === 'VENDOR' && improveForm.status === 'IMPROVING' && improveForm.vIsFeedback === 'N')"
                      @on-change="({file}) => vendorHandleUploadSuccess(file)"
                    />
                  </div>
                </el-form-item>
              </srm-col>
              <srm-col :initCol="2">
                <el-form-item
                  :label="$t('perfMod.vFeedbackExplanation2')"
                  prop="vFeedbackExplanation"
                >
                  <el-input
                    v-model="vendorFeedbackForm.vFeedbackExplanation"
                    type="textarea"
                    :rows="3"
                  />
                </el-form-item>
              </srm-col>
            </srm-row>
          </el-form>
        </el-collapse-item>
        <el-collapse-item
          v-if="
            (curRole === 'BUYER' && improveForm.status === 'UNDER_EVALUATION') ||
              (curRole === 'BUYER' &&
              improveForm.status === 'UNDER_EVALUATION' &&
              improveForm.vIsFeedback === 'Y') ||
              improveForm.status === 'EVALUATED'
          "
          :title="$t('perfMod.buyerImproveForm')"
          name="4"
        >
          <el-form
            ref="buyForm"
            :model="buyersProcessForm"
            class="form-fill-style"
            :disabled="curOpt === 'view'"
          >
            <srm-row>
              <srm-col :initCol="4">
                <el-form-item
                  :label="$t('perfMod.mFeedbackTime2')"
                  prop="mFeedbackTime"
                >
                  <el-date-picker
                    v-model="buyersProcessForm.mFeedbackTime"
                    :placeholder="$t('perfMod.mFeedbackTime2')"
                    format="yyyy-MM-dd"
                    value-format="yyyy-MM-dd HH:mm:ss"
                  />
                </el-form-item>
              </srm-col>
              <srm-col :initCol="2">
                <el-form-item
                  :label="$t('perfMod.mFeedbackExplanation2')"
                  prop="mFeedbackExplanation"
                >
                  <el-input
                    v-model="buyersProcessForm.mFeedbackExplanation"
                    type="textarea"
                    :rows="3"
                  />
                </el-form-item>
              </srm-col>
            </srm-row>
          </el-form>
        </el-collapse-item>
      </el-collapse>
      <CToolbar>
        <template slot="right">
          <el-button @click="toBack()">
            {{ $t('common.backTo') }}
          </el-button>
          <el-button
            v-if="
              curRole === 'BUYER' && (improveForm.status === 'DRAFT' || improveForm.status == '')
            "
            type="primary"
            @click="submitHandle()"
          >
            {{ $t('common.save') }}
          </el-button>
          <el-button
            v-if="
              curRole === 'BUYER' && (improveForm.status === 'DRAFT' || improveForm.status == '') && curOpt !== 'add' && curOpt !== 'adds'
            "
            type="primary"
            @click="notifySupplier()"
          >
            <!--通知供应商-->
            {{ $t('perfMod.notifySupplier') }}
          </el-button>
          <el-button
            v-if="
              curRole === 'BUYER' &&
                improveForm.status === 'UNDER_EVALUATION' &&
                improveForm.mIsFeedback === 'N'
            "
            type="primary"
            @click="submitProcessRes()"
          >
            <!--提交处理结果-->
            {{ $t('perfMod.submitProcessRes') }}
          </el-button>
          <el-button
            v-if="
              curRole === 'VENDOR' &&
                improveForm.status === 'IMPROVING' &&
                improveForm.vIsFeedback === 'N'
            "
            type="primary"
            @click="vendorFeedback()"
          >
            <!--提交反馈-->
            {{ $t('perfMod.vendorFeedback') }}
          </el-button>
        </template>
      </CToolbar>
      <!-- 人员选择 -->
      <CPeopleSelector
        ref="peopleSelector"
        :visible.sync="peopleDialog"
        :multi-select="false"
        @on-confirm="getPeople"
      />
    </el-main>
  </el-container>
</template>
<script>
import ModelConfigForm from 'mod@/common/userManage/views/ModelConfig/ModelConfigForm'
import ModelConfigTable from 'mod@/common/userManage/views/ModelConfig/ModelConfigTable'
import { tabTodoMixin } from '@/utils/mixins'
import CToolbar from 'lib@/components/c-toolbar'
import OrganizationSelectTree from 'lib@/components/organization-selector'
import QuickSearch from 'lib@/components/QuickSearch'
import CCategorySelect from 'lib@/components/c-category-select'
import CPeopleSelector from '@/library/components/c-people-selector'
import { getDictItemList } from '@/api/common'
import { adaptDictData } from '@/utils'
import _omit from 'lodash/omit'
import _pick from 'lodash/pick'
import { perVendorApi } from 'mods@/performanceManagementSupplier/api'
import { modelConfigApi } from '@/api/modelConfig'
export default {
  name: 'ImprovementDetail',
  components: {
    OrganizationSelectTree,
    CCategorySelect,
    QuickSearch,
    CToolbar,
    CPeopleSelector,
    ModelConfigForm,
    ModelConfigTable
    // Gac
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      dimDataValue: [],
      modelConfig: {
        dimConfigMap: {},
        formDimVOList: []
      },
      disOverallScoreId: false, // 判断关联绩效单是否可选择，如果从考核/绩效页面过来不能选择
      overallScoreOptions: [], // 关联绩效单列表
      fileInfo: {
        fileModular: 'perf',
        fileFunction: 'vendorImprovement',
        fileType: 'images'
      },
      acceptFileType: ['jpeg', 'png', 'xls', 'xlsx', 'docx', 'pdf'],
      curRole: this.$store.getters.userType, // VENDOR BUYER curRole==='VENDOR'
      approveStatusList: [], // 审批状态
      rangeDate: [], // 时间区间
      improveForm: {
        improveNo: '', // 改善单号
        improveDateStart: '', // 改善开始日期 improveForm
        improveDateEnd: '', // 改善结束日期
        improveTitle: '', // 改善主题
        improveProject: '', // 改善项目
        respUserName: '', // 责任跟进人账号
        respFullName: '', // 责任跟进人名称
        fullPathId: null, // 组织全路径
        overallScoreId: null,
        organizationId: null,
        organizationCode: '',
        organizationName: '',
        vendorId: null,
        vendorCode: '', // 供应商code
        vendorName: '', // 供应商名称
        categoryId: null,
        categoryCode: '',
        categoryName: '',
        fileUploadId: '', // 改善附件ID
        fileSourceName: '', // 改善附件名称
        explanation: '', // 改善说明
        status: 'DRAFT' // 状态
      },
      improveFormRules: {
        improveDateStart: [{ required: true, message: this.$t('perfMod.selectDateImprovement') }],
        improveTitle: [{ required: true, message: this.$t('perfMod.improvementSubject') }],
        improveProject: [{ required: true, message: this.$t('perfMod.enterImprovementItems') }],
        fullPathId: [{ required: true, message: this.$t('perfMod.selectOrg') }],
        vendorCode: [{ required: true, message: this.$t('perfMod.selectSupplier') }],
        categoryName: [{ required: true, message: this.$t('perfMod.selectItemsPurchase') }],
        organizationId: [{ required: true, message: this.$t('perfMod.selectOrg') }],
        explanation: [
          {
            required: true,
            message: this.$t('perfMod.enterDescriptionImprovement')
          }
        ]
      },
      // 反馈
      vendorFeedbackForm: {
        vendorImproveId: null, // 单ID
        vFeedbackTime: '', // 供应商反馈时间
        vFeedbackFileUploadId: null, // 供应商反馈附件文件id
        vFeedbackFileSourceName: '', // 供应商反馈附件文件名
        vFeedbackExplanation: '' // 供应商反馈说明
      },
      // 评价
      buyersProcessForm: {
        vendorImproveId: null, // 单ID
        mFeedbackTime: '', // 评价时间
        mFeedbackExplanation: '' // 评价供应商反馈说明
      },
      peopleDialog: false,
      curOpt: 'add',
      activeDims: ['1', '2', '3', '4', '5', '6'],
      curOrderId: null, // 单据Id
      options: [] // 公司
    }
  },
  created () {
    this.getDimDataById(this.$attrs.params.orderId)
    // 获取动态配置(复制粘贴即可)
    this.getConfig()
    this.fatchDictData() // 字典
    this.curOpt = this.$attrs.params.flag
    if (this.$attrs.params.flag !== 'add' && this.$attrs.params.flag != 'adds') {
      this.curOrderId = this.$attrs.params.orderId // 单据Id
      this.getOrderFormDetail()
    }
    if (this.$attrs.params.flag == 'adds') {
      console.log(this.$attrs.params)
      this.improveForm.vendorCode = this.$attrs.params.row.companyCode
      this.improveForm.vendorId = this.$attrs.params.row.companyId
      this.improveForm.vendorName = this.$attrs.params.row.companyName
      this.improveForm.organizationId = this.$attrs.params.row.organizationId
      this.improveForm.organizationName = this.$attrs.params.row.organizationName
      this.improveForm.categoryName = this.$attrs.params.row.categoryName
      this.improveForm.overallScoreId = this.$attrs.params.row.overallScoreId
      this.disOverallScoreId = true
      this.performance(this.$attrs.params.row.companyName)
    }
  },
  methods: {
    getConfig () {
      modelConfigApi.getModelConfig('vendorImprovement').then(result => {
        if (result) {
          this.modelConfig = result.data
          if (this.modelConfig.formDimVOList) {
            this.modelConfig.formDimVOList.forEach(item => {
              // 默认展示所有动态维度
              this.activeDims.push(item.dimCode)
            })
          }
        }
      })
    },
    getDimDataById (businessId) {
      if (businessId) {
        modelConfigApi.getDimDataById(businessId).then(result => {
          this.dimDataValue = result.data
        })
      }
    },
    getDimDataFromVue (businessId) {
      let that = this
      let dimDataList = []
      this.modelConfig.formDimVOList.forEach(item => {
        // 非固定的,需要加[0]
        if (that.$refs[item.dimCode] instanceof Array) {
          let formData = that.$refs[item.dimCode][0].getDataValue()
          dimDataList.push(...formData)
        } else {
          let formData = that.$refs[item.dimCode].getDataValue()
          dimDataList.push(...formData)
        }
      })
      if (dimDataList && dimDataList.length > 0) {
        dimDataList.forEach(item => {
          item.businessId = businessId
        })
      }
      return dimDataList
    },
    showTileFlag (dimTitleShowFlag) {
      if (dimTitleShowFlag === 'N') {
        return 'noShow'
      }
      return ''
    },
    // 获取数据字典
    fatchDictData () {
      // 批量查询字典
      let dictParamsArr = [
        { dictCode: 'VENDOR_ASSES_STATUS' } // 审批状态
      ]
      getDictItemList(dictParamsArr).then(res => {
        const [VENDOR_ASSES_STATUS] = res.data
        this.approveStatusList = adaptDictData(VENDOR_ASSES_STATUS.VENDOR_ASSES_STATUS, 'dict')
      })
    },
    // 查询关联绩效单
    performance (companyName) {
      const obj = {
        pageNum: 1,
        pageSize: 100,
        companyName: companyName
      }
      perVendorApi.listPerfOverallScorePage(obj).then(res => {
        if (res.code == '0') {
          const data = res.data.list
          let attr = []
          data.forEach(element => {
            let obj = {
              label: element.projectName,
              value: element.overallScoreId
            }
            attr.push(obj)
          })
          this.overallScoreOptions = attr
        }
      })
    },
    // 查询公司
    queryCompanyList ({ keyWord = '', orgId }) {
      perVendorApi.listPageByOrgIdAndKeyWord({
        keyWord,
        orgId
      }).then(res => {
        this.options = res.data.map(i => ({
          id: i.companyId,
          value: i.companyCode,
          label: i.companyName
        }))
      })
    },
    // 选择公司
    elSelectChange (val) {
      const company = this.options.find(i => i.value === val)
      const { id, label, value } = company || {}
      this.improveForm = {
        ...this.improveForm,
        vendorId: id,
        vendorName: label,
        vendorCode: value
      }
      this.performance(label)
    },
    remoteMethod (keyWord) {
      if (keyWord && !this.improveForm.organizationId) {
        return this.$message({
          type: 'warning',
          message: this.$t('perfMod.selectPurchasingOrganization')
        })
      }
      if (!this.improveForm.organizationId) return
      const params = { orgId: this.improveForm.organizationId }
      if (keyWord) params.keyWord = keyWord
      this.queryCompanyList(params)
    },
    // 日期选择
    dateChangeHandel (val) {
      if (val) {
        this.improveForm.improveDateStart = val[0]
        this.improveForm.improveDateEnd = val[1]
      }
    },
    // 选择组织
    addOrgHandle (node, instanceId) {
      const { organizationCode, organizationName, organizationId } = node
      this.improveForm.organizationCode = organizationCode
      this.improveForm.organizationName = organizationName
      this.improveForm.organizationId = organizationId
    },
    // 值改变
    changeOrgHandle (val) {
      if (val === null) {
        this.improveForm.vendorCode = ''
        this.improveForm.vendorId = null
        this.improveForm.vendorName = ''
        this.options = []
      }
    },
    // 确认选择品类
    comfirmCategory (node, scope) {
      scope.categoryId = node ? node.categoryId : null
      scope.categoryName = node ? node.categoryName : ''
      scope.categoryCode = node ? node.categoryCode : ''
      scope.categoryFullName = node ? node.categoryFullName : ''
    },
    // 选择业务人员
    selectPeople () {
      this.peopleDialog = true
    },
    // 获取选择器
    getPeople (data) {
      let user = data[0]
      this.improveForm.respFullName = user.nickname || user.username
      this.improveForm.respUserName = user.username
    },
    // 上传附件成功
    handleUploadSuccess (file) {
      const { fileId = '', fileName = '' } = file || {}
      this.improveForm.fileUploadId = fileId.toString()
      this.improveForm.fileSourceName = fileName
    },
    // 供应商反馈附件上传成功
    vendorHandleUploadSuccess (file) {
      const { fileId = '', fileName = '' } = file || {}
      this.vendorFeedbackForm.vFeedbackFileUploadId = fileId.toString()
      this.vendorFeedbackForm.vFeedbackFileSourceName = fileName
    },
    // 供应商反馈附删除
    vendorHandleAttachmentRemove () {
      this.vendorFeedbackForm.vFeedbackFileUploadId = ''
      this.vendorFeedbackForm.vFeedbackFileSourceName = ''
    },
    // 选择供应商回调
    getCompanyObj (val, data) {
      this.improveForm.vendorId = val ? val.companyId : ''
      this.improveForm.vendorCode = val ? val.companyCode : ''
      this.improveForm.vendorName = val ? val.companyName : ''
      this.queryForm.companyId = val.companyId
    },
    // 查询单据详情
    getOrderFormDetail () {
      let vendorImproveId = this.curOrderId
      perVendorApi.vendorImproveQueryById({ vendorImproveId }).then(res => {
        this.elSelectChange(res.data.vendorName)
        let formInfo = res.data
        this.improveForm = _omit(formInfo, [
          'creationDate',
          'lastUpdateDate',
          'createdBy',
          'createdByIp',
          'vFeedbackTime',
          'vFeedbackFileUploadId',
          'vFeedbackFileSourceName',
          'vFeedbackExplanation',
          'mFeedbackTime',
          'mFeedbackExplanation'
        ])
        this.vendorFeedbackForm = _pick(formInfo, [
          'vendorImproveId',
          'vFeedbackTime',
          'vFeedbackFileUploadId',
          'vFeedbackFileSourceName',
          'vFeedbackExplanation'
        ])
        this.buyersProcessForm = _pick(formInfo, [
          'vendorImproveId',
          'mFeedbackTime',
          'mFeedbackExplanation'
        ])
        // 改善时间区间显示处理
        if (res.data.improveDateStart) {
          this.$set(this.rangeDate, 0, res.data.improveDateStart)
        }
        if (res.data.improveDateEnd) {
          this.$set(this.rangeDate, 1, res.data.improveDateEnd)
        }
        // 编辑查询公司
        let organizationId = formInfo.organizationId
        this.queryCompanyList({ orgId: organizationId })
      })
    },
    // 选择品类
    comfirmSelect (node, scope) {
      scope.categoryId = node ? node.categoryId : ''
      scope.categoryName = node ? node.categoryName : ''
    },
    reset () {
      // 重置所有过滤条件
      for (let i in this.form) {
        this.form[i] = ''
      }
    },
    // 通知供应商
    notifySupplier () {
      this.submitHandle()
      let submitData = this.improveForm
      perVendorApi.vendorImproveNotifySupplier([submitData]).then(res => {
        this.$message({
          message: res.message,
          type: 'success'
        })
        this.$emit('tab-remove', this.$attrs.params.tabName)
        this.__setTabTodo('improvementList.getQuerydata')
        this.$router.push('/performanceManagement/vendorImprovement')
      })
    },
    // 提交供应商反馈
    vendorFeedback () {
      let submitData = this.vendorFeedbackForm
      perVendorApi.vendorImproveVendorFeedback(submitData).then(res => {
        this.$message({
          message: res.message,
          type: 'success'
        })
        this.$emit('tab-remove', this.$attrs.params.tabName)
        this.__setTabTodo('improvementList.getQuerydata')
      })
    },
    // 采购商提交处理结果
    submitProcessRes () {
      let submitData = this.buyersProcessForm
      perVendorApi.vendorImproveBuyersProcess(submitData).then(res => {
        this.$message({
          message: res.message,
          type: 'success'
        })
        this.$emit('tab-remove', this.$attrs.params.tabName)
        this.__setTabTodo('improvementList.getQuerydata')
      })
    },
    // 保存
    submitHandle () {
      this.$refs.improveForm.validate(valid => {
        if (!valid) {
          this.$message({
            message: this.$t('perfMod.enterRequired'),
            type: 'warning'
          })
          return false
        } else {
          this.dataHandle()
        }
      })
    },
    // 数据保存更新
    dataHandle () {
      let url = ''
      let submitData = this.improveForm
      if (this.curOpt === 'add' || this.curOpt == 'adds') {
        // 新增
        perVendorApi.vendorImproveAdd(submitData).then(res => {
          this.$message({
            message: res.message,
            type: 'success'
          })
          let dimData = this.getDimDataFromVue(res.data.vendorImproveId)
          modelConfigApi.saveFormResutlForBusiness(dimData)
          this.curOrderId = res.data.vendorImproveId
          this.curOpt = 'edit'
          this.getOrderFormDetail() // 查询单据数据
        })
      } else {
        perVendorApi.vendorImproveModify(submitData).then(res => {
          this.$message({
            message: res.message,
            type: 'success'
          })
          let dimData = this.getDimDataFromVue(submitData.vendorImproveId)
          modelConfigApi.saveFormResutlForBusiness(dimData)
          this.curOpt = 'edit'
          this.getOrderFormDetail() // 查询单据数据
        })
      }
    },
    // 返回
    toBack () {
      this.$emit('tab-remove', this.$attrs.params.tabName)
      this.__setTabTodo('improvementList.getQuerydata')
    }
  }
}
</script>
<style scoped lang="scss">
.the-vendorEffectDetail-detail {
  .el-table .el-date-editor {
    width: 135px;
  }
  .el-collapse-item__content > .el-button {
    margin-bottom: 5px;
  }
}
.noShow > :first-child {
  display: none;
}
::v-deep .el-form-item__label{
  float: initial;
}
</style>
