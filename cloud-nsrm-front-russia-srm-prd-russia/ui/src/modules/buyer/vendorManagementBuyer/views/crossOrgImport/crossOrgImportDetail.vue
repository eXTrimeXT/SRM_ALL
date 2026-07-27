<template>
  <el-container class="flex-container the-crossOrgImportDetail-detail" direction="vertical">
    <el-main>
      <CWorkflowMulti
        ref="workflowMulti"
        v-model="activeTabName"
        :funParams="workflowParamsInfo"
        :buttonConfigInfo="buttonConfigInfo"
        @tab-click="workflowView"
        @workflow-handler="workflowHandler"
        @click-handler="(type) => saveDataHandle(type)"
        @submit-direct="(type) => saveDataHandle(type)"
        @confirm="(type, comment) => saveDataHandle(type)"
        @close-tab="backTo"
      >
        <el-collapse v-model="activeDims" class="tab-form-style">
          <!-- 供应商扩展 -->
          <el-collapse-item :title="$t('vendorMod.vendorExpansion')" name="1">
            <el-form
              ref="siteForm"
              :model="vendorImport"
              :rules="rules"
              class="form-fill-style"
              :disabled="curOpt === 'view'"
            >
              <srm-row :gutter="32">
                <srm-col :span="8">
                  <!-- 供应商名称 -->
                  <el-form-item ref="vendorName" :label="$t('common.vendorName')" prop="vendorName">
                    <QuickSearch
                      :show-input="vendorImport.vendorName"
                      show-key="companyName"
                      :scope-data="vendorImport"
                      name="scc_sup_company_info2"
                      @close-quicksearch="getVendorObj"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col :span="8">
                  <!-- 供应商引入单号 -->
                  <el-form-item :label="$t('vendorMod.importNum')" prop="siteFormNumber">
                    <el-input v-model="vendorImport.importNum" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col :span="8">
                  <!-- 创建人 -->
                  <el-form-item :label="$t('common.creator')">
                    <el-input v-model="vendorImport.createdUserName" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col :span="8">
                  <!-- 原业务实体 -->
                  <el-form-item :label="$t('vendorMod.oldOrg')" prop="oldOrgId">
                    <!--<organization-selector
                    ref="organizationSelector"
                    :parentId="-1"
                    nodeType="OU"
                    :scope="vendorImport"
                    v-model="vendorImport.oldOrgId"
                    @select="selectHandler"
                    />-->
                    <el-select v-model="vendorImport.oldOrgId" @change="setOrgObj">
                      <el-option
                        v-for="item in orgList"
                        :key="item.orgId"
                        :label="item.orgName"
                        :value="item.orgId"
                      />
                    </el-select>
                  </el-form-item>
                </srm-col>

                <srm-col :span="8">
                  <!-- 审批状态 -->
                  <el-form-item :label="$t('common.status')">
                    <DictSelect
                      v-model="vendorImport.importStatus"
                      code="VENDORIMPORTSTATUS"
                      disabled
                    />
                  </el-form-item>
                </srm-col>
                <srm-col :span="8">
                  <!-- 创建时间 -->
                  <el-form-item :label="$t('common.creationTime')">
                    <el-input v-model="vendorImport.creationDate" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col :span="16">
                  <!-- 供应商引入说明 -->
                  <el-form-item :label="$t('vendorMod.vendorImportExplain')">
                    <el-input v-model="vendorImport.importExplain" type="textarea" :rows="2" />
                  </el-form-item>
                </srm-col>
              </srm-row>
            </el-form>
          </el-collapse-item>
          <!-- 引入至业务实体 -->
          <el-collapse-item :title="$t('vendorMod.importOrg')" name="2">
            <p style="margin: 0 0 8px 0;display:flex">
              <el-button
                type="primary"
                :disabled="curOpt === 'view'"
                style="margin-right: 22px"
                class="detail-pbtn"
                @click="addorgDataTable"
              >
                {{ $t('common.new') }}
              </el-button>
            <!--            <el-form-->
            <!--              ref="siteForm2"-->
            <!--              :model="vendorImport"-->
            <!--              :rules="rules"-->
            <!--              class="form-fill-style"-->
            <!--              :disabled="curOpt === 'view'"-->
            <!--            >-->
            <!--              <el-form-item ref="globalDivisionId" prop="globalDivisionId">-->
            <!--                <DictSelect-->
            <!--                  v-model="vendorImport.globalDivisionId"-->
            <!--                  code="DIVISION"-->
            <!--                  filterable-->
            <!--                  clearable-->
            <!--                  :disabled="curOpt === 'view'"-->
            <!--                  @change="setDivision"-->
            <!--                />-->
            <!--              </el-form-item>-->
            <!--            </el-form>-->
            </p>
            <el-table
              ref="vendorImportDetails"
              :data="vendorImportDetails"
              style="width: 100%"
              border
              max-height="250px"
            >
              <!-- 序号 -->
              <el-table-column align="center" type="index" :label="$t('common.sort')" width="50" />
              <!-- 引入OU -->
              <el-table-column
                align="center"
                prop="orgName"
                :label="$t('vendorMod.importOU')"
                min-width="200"
              >
                <template slot-scope="scope">
                  <OrganizationSelector
                    ref="organizationSelector2"
                    v-model="scope.row.orgId"
                    :disabled="curOpt === 'view'"
                    :parent-id="-1"
                    node-type="OU"
                    :placeholder="$t('common.pleaseSelect')"
                    :scope="scope.row"
                    @select="selectHandler2"
                  />
                </template>
              </el-table-column>
              <!-- 事业部 -->
              <el-table-column
                align="center"
                prop="division"
                :formatter="formatData"
                :label="$t('vendorMod.buName')"
                min-width="200"
              />
              <!-- 操作 -->
              <el-table-column
                align="center"
                prop="operation"
                fixed="right"
                :label="$t('common.operation')"
                width="80"
              >
                <template slot-scope="scope">
                  <el-button
                    :disabled="curOpt === 'view'"
                    type="text"
                    @click="deleteOneList(scope.$index, scope.row)"
                  >
                    {{ $t('common.delete') }}
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-collapse-item>
          <!-- 扩展的品类 -->
          <el-collapse-item :title="$t('vendorMod.expandCate')" name="3">
            <el-table
              ref="catDataTable"
              :data="categoryList"
              style="width: 100%"
              border
              max-height="250px"
            >
              <!-- 序号 -->
              <el-table-column align="center" type="index" :label="$t('common.sort')" width="50" />
              <!-- 品类 -->
              <el-table-column
                align="center"
                prop="categoryName"
                :label="$t('common.category')"
                min-width="200"
              />
            </el-table>
          </el-collapse-item>
          <!-- 附件 -->
          <el-collapse-item :title="$t('vendorMod.relegation.accessory')" name="4">
            <FileDynamic
              ref="sceneAttachment"
              v-model="fileuploads"
              scene-module-code="SCENE_ORG_IMPORT_ATTACHMENT"
              :business-id="importId"
              :editable="curOpt === 'add' || curOpt === 'edit'"
            />
          </el-collapse-item>
        </el-collapse>
        <CToolbar v-if="false">
          <template slot="right">
            <el-button @click="backTo">
              {{ $t('common.cancel') }}
            </el-button>
            <el-button
              v-if="vendorImport.importStatus === 'DRAFT'"
              type="primary"
              @click="saveDataHandle('TEMPORARY_STORAGE')"
            >
              {{ $t('common.staging') }}
            </el-button>
            <el-button
              v-if="['DRAFT', 'REJECTED', 'WITHDRAW'].includes(vendorImport.importStatus)"
              type="primary"
              @click="submitDataHandle('SUBMISSION')"
            >
              {{ $t('common.submit') }}
            </el-button>
          </template>
        </CToolbar>
      </CWorkflowMulti>
    </el-main>
    <!-- 起草人意见 -->
    <srm-dialog
      :title="$t('vendor.loggerComment')"
      :visible.sync="loggerComment"
      size="middle"
      style="text-align: center"
    >
      <el-input
        v-model="inputComment"
        type="textarea"
        :rows="4"
        :placeholder="$t('vendorMod.pleaseApproval')"
      />
      <div class="topComment">
        <el-button @click="loggerComment = false">
          {{ $t('common.cancel') }}
        </el-button>
        <el-button type="primary" @click="submitDataHan">
          {{ $t('common.confirm') }}
        </el-button>
      </div>
    </srm-dialog>
  </el-container>
</template>
<script>
import { tabTodoMixin } from '@/utils/mixins'
import MainHeader from 'lib@/components/Table/MainHeader'
import CToolbar from 'lib@/components/c-toolbar'
import QuickSearch from 'lib@/components/QuickSearch'
import OrganizationSelector from 'lib@/components/organization-selector'
import VendorAccessSteps from 'modb@/vendorManagementBuyer/components/VendorAccessSteps'
import FileDynamic from '@/library/components/c-file-management/file-dynamic'
import { accessCommonApi } from 'modb@/vendorManagementBuyer/api/vendorManagement'
import { crossOrgImport } from 'modb@/vendorManagementBuyer/api/supApi'
import WorkflowCommon from '@/library/mixins/workflow-common'

export default {
  name: 'CrossOrgImportDetail',
  components: {
    MainHeader,
    QuickSearch,
    CToolbar,
    VendorAccessSteps,
    OrganizationSelector,
    FileDynamic
  },
  mixins: [tabTodoMixin, WorkflowCommon],
  data () {
    return {
      inputComment: '',
      loggerComment: false,
      vendorAccessStatus: 'site',
      accessType: 'daySiteAssessment',
      simpleAccessType: 'NON',
      orderStatus: 'DRAFT', // 单据状态
      curOrderId: null, // 单据ID
      isQuaFormIdDisabled: false,
      approveStatusList: [], // 审批状态
      quaReviewTypeList: [], // 资质审查类型
      assessmentTypeList: [], // 现场评审类型
      reviewResultList: [], // 评审结果
      vendorList: [], // 供应商列表
      catStatus: [], // 品类状态
      orgStatus: [], // 组织服务状态
      ratingField: [], // 评分领域
      ceeaReviewLinkList: [], // 评分领域
      globalDivisionId: null,
      BUList: [],
      buObj: {},
      rules: {
        assessmentType: [{ required: true, message: this.$t('vendorMod.msgAssessmentType') }], // "请选择现场评审类型"
        vendorName: [{ required: true, message: this.$t('vendorMod.msgVendorName') }], // "请输入供应商名称"
        reviewResult: [{ required: true, message: this.$t('vendorMod.msgReviewResult') }], // "请选择评审结果"
        siteDate: [{ required: true, message: this.$t('vendorMod.msgSiteDate') }], // "请选择评审日期"
        siteMember: [{ required: true, message: this.$t('vendorMod.msgSiteMember') }], // "请输入评审成员"
        vendorAssessor: [{ required: true, message: this.$t('vendorMod.msgVendorAssessor') }] // "请输入供方评审成员"
      },
      tableData: [],
      reviewFormList: [],
      vendorImport: {
        vendorId: '',
        vendorCode: '', // 供应商code
        vendorName: '', // 供应商名称
        createdUserName: '',
        creationDate: '',
        oldOrgId: null,
        oldOrgCode: null,
        oldOrgName: null,
        importNum: null,
        importStatus: '',
        importExplain: null,
        globalDivisionId: null
      },
      orgList: [],
      fileuploads: [],
      categoryList: [],
      displayCatData: [],
      vendorImportDetails: [],
      orgData: [], // 组织
      catData: [], // 品类
      orgJournals: [], // 组织选择
      cateJournals: [], // 品类选择
      selectedOrg: [], // 选中的组织数据
      selectedCat: [], // 选中的品类数据
      isDisabled: this.$attrs.params.flag === 'edit',
      curOpt: 'add',
      orgDialog: false,
      activeDims: ['1', '2', '3', '4', '5', '6'],
      queryReviewFormParame: {}, // 查询资质审查单据入参
      isOutside: '',
      importId: ''
    }
  },
  computed: {
    viewUpdateButton () { // 用来控制保存、提交按钮是否可见。如果自定义按钮则无需添加
      return ['DRAFT', 'REJECTED', 'WITHDRAW'].includes(this.vendorImport.importStatus)
    },
    workflowBusinessId () { // 用来指定工作流的业务ID
      return this.importId || null
    },
    // 展示工作流tab页
    workflowTabDisabled () {
     return ['DRAFT'].includes(this.vendorImport.importStatus)
    }
  },
  watch: {
    // 监听保存提交 按钮变更状态，如果自定义按钮则无需添加
    viewUpdateButton () {
      this.buttonConfigInfo.save.view = this.viewUpdateButton
      this.buttonConfigInfo.submit.view = this.viewUpdateButton
    }
  },
  created () {
    this.fatchDictData() // 字典
    console.log('this.$attrs.params.flag', this.$attrs)
    this.curOpt = this.$attrs.params.flag
    if (this.$attrs.params.flag === 'edit' || this.$attrs.params.flag === 'view') {
      this.importId = this.$attrs.params.row.importId
      this.getSiteFormDetail(this.$attrs.params.row.importId) // 查询单据数据
    }
    if (this.$attrs.params.flag === 'add') {
      this.vendorImport.importStatus = 'DRAFT'
      this.$nextTick(() => {
        this.$refs.sceneAttachment.loadFileInfo()
      })
    }
    this.buttonConfigInfo.save.view = this.viewUpdateButton
    this.buttonConfigInfo.submit.view = this.viewUpdateButton
    this.buttonConfigInfo.close.view = false
  },
  methods: {
    // 指定工作流的业务类型，在定义工作流时指定
    async getWorkflowBusinessType () {
      return 'vendorImport'
    },
    getCWorkflowRefName () {
      return 'workflowMulti' // 对应CWorkflowMulti标签中的ref
    },
    filterOrgHandler (row, column, cellValue, index) {
      let orgs = this.dictDataFarmat(this.orgStatus, cellValue)
      return orgs
    },
    filterCatHandler (row, column, cellValue, index) {
      let cats = this.dictDataFarmat(this.catStatus, cellValue)
      return cats
    },
    dictDataFarmat (arr, dictVal) {
      let dictArr = arr || []
      let pattern = new RegExp('[\u4E00-\u9FA5]+')
      if (dictArr.length > 0 && dictVal) {
        if (dictVal && pattern.test(dictVal)) {
          // 如果是中文直接返回值
          return dictVal
        } else {
          const dicRow = dictArr.find(item => item.value === dictVal)
          if (dicRow) {
            return dicRow.label
          } else {
            return dictVal
          }
        }
      } else {
        return dictVal
      }
    },
    // 获取数据字典
    fatchDictData () {
      this.getOrgInfoList()
    },
    getOrgInfoList () {
      let datas = {
        pageNum: 1,
        pageSize: 9999,
        organizationTypeCode: 'OU'
      }
      crossOrgImport.listAllOrganization(datas).then(data => {
          this.orgInfoList = data.data.list
        })
        .catch(err => {
          console.log(err)
        })
    },
    // 加载供应商
    getvendorList () {
      accessCommonApi.getVendorDataList({}).then(data => {
          this.vendorList = data.data.list
        })
        .catch(err => {
          console.log(err)
        })
    },
    setOrgObj (val) {
      this.vendorImport.oldOrgId = val
      if (val && this.orgList.filter(v => v.orgId === val)) {
        this.vendorImport.oldOrgCode = this.orgList.filter(v => v.orgId === val)[0].orgCode
        this.vendorImport.oldOrgName = this.orgList.filter(v => v.orgId === val)[0].orgName
        this.getCategoryList()
      }
    },
    setCategoryObj (row) {
      let targetObj = this.categoryList.filter(v => v.categoryId === row.categoryId)
        ? this.categoryList.filter(v => v.categoryId === row.categoryId)[0]
        : ''
      if (targetObj) {
        row.categoryId = targetObj.categoryId
        row.categoryCode = targetObj.categoryCode
        row.categoryName = targetObj.categoryName
        row.categoryFullName = targetObj.categoryFullName
        // this.setCatData();
      }
    },
    setCatData () {
      let displayCatDataArr = this.vendorImportDetails.map(v => v.categoryName)
      displayCatDataArr = Array.from(new Set(displayCatDataArr))
      this.displayCatData = []
      for (let i = 0; i < displayCatDataArr.length; i++) {
        this.displayCatData.push({
          categoryName: displayCatDataArr[i]
        })
      }
    },
    // 选择供应商回调
    getVendorObj (val, scope) {
      scope.vendorId = val ? val.companyId : ''
      scope.vendorCode = val ? val.companyCode : ''
      scope.vendorName = val ? val.companyName : ''
      if (val) {
        this.getOrgList()
      }
    },
    // 查询单据信息
    getSiteFormDetail (importId) {
      crossOrgImport.getVendorImportDetail(importId).then(res => {
          this.vendorImport = res.data.vendorImport
          this.vendorImportDetails = res.data.vendorImportDetails
          this.fileuploads = res.data.fileUploads
          this.getOrgList()
          this.setCatData()
          this.getCategoryList()

          this.$nextTick(() => {
            this.$refs.sceneAttachment.loadFileInfo()
          })
        })
        .catch(err => {
          console.log(err)
        })
    },
    // 删除filelist
    deleteFileList (index, row) {
      this.fileuploads.splice(index, 1)
    },
    deleteOneList (index, row) {
      if (row.importDetailId) {
        crossOrgImport.deleteOneList(row.importDetailId).then(res => {
            this.vendorImportDetails.splice(index, 1)
          })
          .catch(err => {
            console.log(err)
          })
      } else {
        this.vendorImportDetails.splice(index, 1)
      }
    },
    getOrgList () {
      crossOrgImport.getOrgByVendorId(this.vendorImport.vendorId).then(res => {
          this.orgList = res.data
        })
        .catch(err => {
          console.log(err)
        })
    },
    getCategoryList () {
      if (!this.vendorImport.vendorId || !this.vendorImport.oldOrgId) {
        return
      }
      let datas = {
        companyId: this.vendorImport.vendorId,
        orgId: this.vendorImport.oldOrgId
      }
      crossOrgImport.listOrgCategoryByParam(datas).then(res => {
          this.categoryList = res.data
        })
        .catch(err => {
          console.log(err)
        })
    },
    setDivision (val) {
      let obj = this.BUList.find(v => v.value === val) || {}
      let division = obj.label || null
      let filterOrgInfoList = this.orgInfoList.filter(v => v.divisionId === val)
      if (filterOrgInfoList.length > 0) {
        for (let i = 0; i < filterOrgInfoList.length; i++) {
          this.vendorImportDetails.push({
            importId: null,
            importDetailId: null,
            orgId: filterOrgInfoList[i].organizationId,
            orgCode: filterOrgInfoList[i].organizationCode,
            orgName: filterOrgInfoList[i].organizationName,
            // buCode: val,
            division: val
          })
        }
      }
    },
    addorgDataTable () {
      if (!this.vendorImport.vendorId || !this.vendorImport.oldOrgId) {
        // 请选择供应商和原业务实体!
        return this.$message.warning(this.$t('vendorMod.msgVendorAndOldOrg'))
      }
      this.vendorImportDetails.push({
        importId: null,
        importDetailId: null,
        orgId: null,
        orgCode: null,
        orgName: null,
        division: null
        /* categoryId: null,
        categoryName: null,
        categoryFullId: null,
        categoryFullName: null,
        categoryCode: null, */
      })
    },
    selectHandler (node, value, scope) {
      scope.oldOrgId = node ? node.organizationId : ''
      scope.oldOrgCode = node ? node.organizationCode : ''
      scope.oldOrgName = node ? node.organizationName : ''
      if (node) {
        this.getCategoryList()
      }
    },
    selectHandler2 (e, value, scope) {
      scope.orgId = e ? e.organizationId : null
      scope.orgCode = e ? e.organizationCode : ''
      scope.orgName = e ? e.organizationName : null
      if (e && e.organizationId) {
        crossOrgImport.getBuByOrgId(e.organizationId).then(data => {
            scope.division = data.data.organizationCode
          })
          .catch(err => {
            console.log(err)
          })
      } else {
        scope.division = null
      }
    },
    formatData (row, column, cellValue, index) {
      return this.buObj[cellValue]
    },
    // 新增评审明细
    addFormRecords () {
      this.fileuploads.push({
        comment: '',
        fileuploadId: '',
        fileFullname: '',
        businessId: this.vendorImport.importId
      })
    },
    reset () {
      // 重置所有过滤条件
      for (let i in this.form) {
        this.form[i] = ''
      }
    },
    backTo () {
      this.$emit('tab-remove', this.$attrs.params.tabName)
      this.__setTabTodo('crossOrgImportList.getQuerydata')
    },
    saveDataHandle (type) {
      let submitData = {
        vendorImport: this.vendorImport,
        vendorImportDetails: this.vendorImportDetails,
        fileUploads: this.fileuploads
      }
      if (type === 'SAVE') {
         crossOrgImport.saveTemporary(submitData).then(res => {
          this.$message({
            message: res.message,
            type: 'success'
          })
          this.getSiteFormDetail(res.data)
        })
        .catch(err => {
          console.log(err)
        })
      } else {
        this.submitDataHandle()
      }
    },
    submitDataHandle () {
      this.$refs['siteForm'].validate((valid, object) => {
        if (valid) {
          this.submitDataHan()
        } else {
          this.__focus_error__()
        }
      })
    },
    submitDataHan () {
      let submitData = {
        vendorImport: { ...this.vendorImport, importStatus: 'SUBMITTED' },
        vendorImportDetails: this.vendorImportDetails,
        fileuploads: this.fileuploads
      }
      crossOrgImport.saveTemporary(submitData).then(async res => {
          this.$message({
            message: res.message,
            type: 'success'
          })
          this.importId = res.data
          await this.getSiteFormDetail(res.data)
          await this.handlerAfter('SUBMIT')
        })
        .catch(err => {
          console.log(err)
        })
    },
    handleScriptProgress (percent) {},
    outerButtonClick (index) {
      this.bankRowIndex = index
    },
    outerHandleUploadSuccess (file) {
      const { id, name } = file
      this.fileuploads[this.bankRowIndex].fileuploadId = id.toString()
      this.fileuploads[this.bankRowIndex].fileFullname = name
    },
    // 移除
    outerHandleRemove (docId) {},
    // 删除银行证明文件
    outerHandleAttachmentRemove (row) {
      row.fileuploadId = ''
      row.fileFullname = ''
    }
  }
}
</script>
<style scoped lang="scss">
.the-crossOrgImportDetail-detail {
  .sub_header {
    padding: 4px 11px;
    background: #eee;
  }
  .el-table .el-date-editor {
    width: 135px;
  }
  .the_first_col .el-form-item__content > div {
    width: 155px;
    margin-right: 11px;
  }
  .the_first_col .el-form-item__content > div:last-child {
    width: 255px;
  }
  .el-collapse-item__content > .el-button {
    margin-bottom: 5px;
  }
  .vendorAccessSteps {
    padding: 12px 5px;
    // border: 1px solid #efefef;
    // border-top: 0;
    // border-bottom: 1px solid #e6ebf5;
  }
}
</style>
