<template>
  <el-container class="flex-container" direction="vertical">
    <el-main>
      <CWorkflowMulti
        ref="workflowMulti"
        v-model="activeTabName"
        :funParams="workflowParamsInfo"
        :buttonConfigInfo="buttonConfigInfo"
        :beforeApprove="beforeApprove"
        :showTopBtn="curOpt != 'view'"
        @tab-click="workflowView"
        @workflow-handler="workflowHandler"
        @click-handler="(type) => saveBill(type)"
        @submit-direct="(type) => saveBill(type)"
        @confirm="(type, comment) => saveBill(type, comment)"
        @close-tab="back"
        @updateFlowData="data => flowData = data || {}"
      >
        <el-collapse v-model="activeDims" class="tab-form-style">
          <!-- 物料参数 -->
          <el-collapse-item :title="$t('dataConfMod.materialParams')" name="1">
            <el-form ref="detailForm" :model="form" :rules="rules">
              <srm-row>
                <srm-col>
                  <!-- 物料名称 -->
                  <el-form-item :label="$t('common.materialName')" prop="materialName">
                    <el-input
                      v-model="form.materialName"
                      type="text"
                      maxlength="40"
                      show-word-limit
                      :disabled="!editAble"
                    />
                  </el-form-item>
                </srm-col>

                <srm-col>
                  <!-- 物料名称（中文） -->
                  <el-form-item :label="$t('common.materialName') + $t('cusEntry.dataConfMod.langSignZh')" prop="extMaterialNameChn">
                    <el-input v-model="form.extMaterialNameChn" :disabled="!(editAble || approveEditable)" />
                  </el-form-item>
                </srm-col>


                <srm-col>
                  <!-- 品类 -->
                  <el-form-item :label="$t('dataConfMod.category')" prop="categoryId">
                    <CCategorySelect
                      v-model="form.categoryName"
                      :scope="form"
                      :placeholder="$t('common.pleaseSelect')"
                      show-key="categoryName"
                      @select="getCateObj"
                      :disabled="!editAble"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 单位 -->
                  <el-form-item :label="$t('dataConfMod.unit')" prop="unit" >
                    <dict-select
                      v-model="form.unit"
                      code="unit"
                      :disabled="!editAble"
                      @change-value="(value, dictItem, scope) => {
                        form.unitName = dictItem.label
                      }"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 规格型号 -->
                  <el-form-item :label="$t('vendorMod.specification')" prop="extMaterialModel">
                    <el-input v-model="form.extMaterialModel" disabled/>
                  </el-form-item>
                </srm-col>

                <srm-col>
                  <!-- 规格型号（中文） -->
                  <el-form-item :label="$t('vendorMod.specification') + $t('cusEntry.dataConfMod.langSignZh')" prop="extMaterialModelChn">
                    <el-input v-model="form.extMaterialModelChn" :disabled="!(editAble || approveEditable)" />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 创建人 -->
                  <el-form-item :label="$t('common.creator')">
                    <el-input v-model="form.createdBy" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 创建时间 -->
                  <el-form-item :label="$t('common.creationTime')">
                    <el-date-picker
                      v-model="form.creationDate"
                      :format="$formatDatePicker"
                      value-format="yyyy-MM-dd"
                      disabled
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <!-- 物料编码 -->
                  <el-form-item :label="$t('common.materialCode')" prop="materialCode">
                    <el-input v-model="form.materialCode" disabled />
                  </el-form-item>
                </srm-col>
                <!-- 物料类型 -->
                <srm-col>
                  <el-form-item :label="$t('dataConfMod.materialType')" prop="extSapMaterialType">
                    <dict-select
                      v-model="form.extSapMaterialType"
                      code="SAP_MATERIAL_TYPE"
                      :disabled="!approveEditableFlag"
                      @change="extSapMaterialTypeChange"
                    />
                  </el-form-item>
                </srm-col>
                <!-- 物料组 -->
                <srm-col>
                  <el-form-item :label="$t('bom.materialGroup')" prop="extSapMaterialGroup">
                    <dict-select
                      v-model="form.extSapMaterialGroup"
                      code="SAP_MATERIAL_GROUP"
                      :disabled="!approveEditableFlag"
                    />
                  </el-form-item>
                </srm-col>
                <!-- 评估类 -->
                <srm-col>
                  <el-form-item :label="$t('bom.valuationClass')" prop="extSapValuationClass">
                    <el-select
                      v-model="form.extSapValuationClass"
                      :disabled="!approveEditableFlag || !form.extSapMaterialType"
                    >
                      <el-option
                        v-for="item in extSapValuationClassList"
                        :key="item.dictItemCode"
                        :label="item.dictItemName"
                        :value="item.dictItemCode"
                      />
                    </el-select>
                  </el-form-item>
                </srm-col>
              </srm-row>
            </el-form>
          </el-collapse-item>
          <!-- 物料属性 -->
          <el-collapse-item :title="$t('bidMod.biddingManagementSupplier.preserve')" name="2">
            <el-form ref="detailForm2" :model="form">
              <!-- '缺少该品类与物料属性对应的规则，暂时不能维护该品类的物料属性。需要申请补充品类与物料属性对应的规则。' -->
              <el-table :data="form.materialAttribute" 
              border stripe
              style="width: 100%;"
              :empty-text="form.categoryId===null?
              $t('common.noData'):$t('cusEntry.supplement20250218.applyCategoryMaterialRule')">

                <!-- 序号 -->
                <el-table-column :label="$t('components.common.sort')" width="80" align="center">
                  <template slot-scope="scope">
                    {{ scope.$index + 1 }}
                  </template>
                </el-table-column>
              <!-- 属性名称（俄文） -->
                <el-table-column prop="attributeNameRu" 
                  :label="$t('cusEntry.supplement20250218.attributeNameRu')" 
                  width="180" 
                  align="center">
                </el-table-column>
              <!-- 属性名称（中文） -->
                <el-table-column prop="attributeName" 
                  :label="$t('cusEntry.supplement20250218.attributeNameZH')" 
                  width="180" 
                  align="center">
                </el-table-column>
                <!-- 属性值 -->
                <el-table-column 
                  prop="attributeValue" 
                  :label="$t('materialMainData.attributeValue')" 
                  :render-header="_addStarToColumn"
                  align="center">

                  <template slot-scope="scope">
                    <!-- '请输入' -->
                    <el-form-item 
                    :prop="'materialAttribute.' + scope.$index + '.attributeValue'" 
                    :rules="[{ required: true, message: $t('common.pleaseInput') }]">
                      <el-input v-model="scope.row.attributeValue" :disabled="!editAble"></el-input>
                    </el-form-item>
                  </template>

                </el-table-column>
                
              </el-table>
            </el-form>
          </el-collapse-item>

          <!-- 物料图片 -->
          <el-collapse-item ref="imgRef" :title="$t('dataConfMod.materialPicture')" name="3">
            <picture-card
              :fileList="materialItem"
              :isReadOnly="!editAble&&!maintenanceImgAble"
              @handleSuccess="handleSuccess"
              @handleRemove="handleRemove"
              :profileUpdateDate="form.profileUpdateDate"
              :profileUpdateBy="form.profileUpdateBy"
            />
          </el-collapse-item>
        </el-collapse>
      </CWorkflowMulti>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoMixin } from '@/utils/mixins'
import CCategorySelect from 'modc@/common/baseSettingCommon/views/materialMaintenance/components/c-category-select'
import WorkflowCommon from '@/library/mixins/workflow-common'
import { commonApi } from 'mod@/common/baseSettingCommon/api'
import pictureCard from './components/pictureCard'
import { getDictItem } from '@/api/common'

export default {
  name: 'MaterialMaintenanceDetail',
  components: {
    CCategorySelect,
    pictureCard
  },
  mixins: [tabTodoMixin, WorkflowCommon],
  data () {
    return {
      activeDims: ['1','2','3'],
      curOpt: null,
      valuationClassList: [],
      extSapValuationClassList: [],
      form: {
        extSapMaterialType: null,
        extSapMaterialGroup: null,
        extSapValuationClass: null,
        materialName: '',
        categoryId: null,
        categoryCode: '',
        categoryName: '',
        struct: '',
        categoryFullName: '',
        unit: '',
        unitName: '',
        extMaterialModel: '',
        extMaterialNameChn: '',
        extMaterialModelChn: '',
        materialProfile: [],
        materialAttribute: [],
        profileUpdateDate: '',  // 图片最后更新时间
        profileUpdateBy: '' // 图片最后更新人
      },
      flowData: {}, // 当前审批信息
      materialItem:[]  // 图片列表
    }
  },
  computed: {
    maintenanceImgAble () {
      return ['maintenanceImg'].includes(this.curOpt)
    },
    editAble () {
      return ['add', 'edit'].includes(this.curOpt)
    },
    approveEditable () {
      // 审批环节-采购节点必填，必须补全物料的中文信息
      return ['approval'].includes(this.curOpt) && this.flowData.taskName === '需求部门负责人（中籍）/ Руководитель Заявляющего подразделения (Китайский)'
    },
    approveEditableFlag () {
      // 审批环节sap字段必填标识
      return ['approval'].includes(this.curOpt) && this.flowData.taskName === 'SAP仓库部门SAP组 / Складской отдел. Группа SAP'
    },
    viewUpdateButton () { // 用来控制保存、提交按钮是否可见。如果自定义按钮则无需添加
      return this.editAble || this.maintenanceImgAble
    },
    workflowBusinessId () { // 用来指定工作流的业务ID
      return this.$attrs.params.materialId || this.form.materialId
    },
    rules () {
      const rulesObj = {
        materialName: [{ required: true, message: this.$t('common.pleaseInput') }],
        categoryId: [{ required: true, message: this.$t('common.pleaseSelect') }],
        unit: [{ required: true, message: this.$t('common.pleaseSelect') }],
        extMaterialModel: [{ required: true, message: this.$t('common.pleaseInput') }],
        extMaterialNameChn: [{ required: this.approveEditable, message: this.$t('common.pleaseInput') }],
        extMaterialModelChn: [{ required: this.approveEditable, message: this.$t('common.pleaseInput') }]
      }
      if (this.approveEditableFlag) {
        return {
          ...rulesObj,
          extSapMaterialType: [{ required: this.approveEditableFlag, message: this.$t('common.pleaseInput') }],
          extSapMaterialGroup: [{ required: this.approveEditableFlag, message: this.$t('common.pleaseInput') }],
          extSapValuationClass: [{ required: this.approveEditableFlag, message: this.$t('common.pleaseInput') }]
        }
      }
      return rulesObj
    },
    specificationModel() {
      let specificationModelValue = [];
      if (this.form.materialAttribute) {
        specificationModelValue = this.form.materialAttribute.reduce((acc, cur) => {
          if (cur.attributeValue) {
            acc.push(cur.attributeValue);
          }
          return acc;
        }, []);
      }
      return specificationModelValue.join(', ');
    }
  },
  watch: {
    // 监听保存提交 按钮变更状态，如果自定义按钮则无需添加
    viewUpdateButton () {
      this.buttonConfigInfo.save.view = this.viewUpdateButton
      this.buttonConfigInfo.submit.view = this.viewUpdateButton
      if(this.$attrs.params.flag === 'maintenanceImg'){
        // 如果是图片维护，就不显示下一步按钮
        this.buttonConfigInfo.submit.view = false
        // 保存按钮 展示保存
        this.buttonConfigInfo.save.name = this.$t('common.save')
      }
    },
    'form.materialAttribute': {
      handler(newVal) {
        this.form.extMaterialModel = this.editAble ? this.specificationModel : this.form.extMaterialModel
      },
      deep: true // 深度监听
    }
  },
  async created () {
    const { data } = await getDictItem('SAP_VALUATION_CLASS')
    this.valuationClassList = data
    this.extSapValuationClassList = data
    const { flag, row } = this.$attrs.params
    this.curOpt = flag
    if (flag != 'add') {
      this.getFormDetail(row.materialId) // 查询单据数据
    }
    this.buttonConfigInfo.save.view = this.viewUpdateButton
    this.buttonConfigInfo.submit.view = this.viewUpdateButton
    if(flag === 'maintenanceImg'){
      // 如果是图片维护，就不显示下一步按钮
      this.buttonConfigInfo.submit.view = false
      // 保存按钮 展示保存
      this.buttonConfigInfo.save.name = this.$t('common.save')
    }
    this.buttonConfigInfo.submit.name = this.$t('common.nextOne')
    this.buttonConfigInfo.cancel.view = flag != 'view'
    this.buttonConfigInfo.close.view = flag == 'view'
  },
  methods: {
    // 指定工作流的业务类型，在定义工作流时指定
    async getWorkflowBusinessType () {
      return 'MATERIAL_ADD'
    },
    getCWorkflowRefName () {
      return 'workflowMulti' // 对应CWorkflowMulti标签中的ref
    },
    // 定义流程额外变量，如果没有就不用添加这个函数
    async getWorkflowBusinessVariables () {
      const procTitleObj = { extApplyNumber: this.form.extApplyNumber }
      return {
        procTitleObj
      }
    },
    extSapMaterialTypeChange (val) {
      this.form.extSapValuationClass = null
      this.extSapValuationClassList = this.valuationClassList.filter(item => item.dictItemMark === val)
    },
    async getFormDetail (id) {
      return new Promise((rs, rj) => {
        commonApi.materialItemGet({ id }).then(res => {
          if (res.data) {
            this.form = Object.assign({}, this.form, res.data.materialItem || {})
            this.materialItem = res.data.imageFileList
            this.form.materialProfile =  res.data.materialItem.materialProfile.split(',')
            this.getAttributesByCategoryandMaterial(this.form.categoryCode,this.form.materialId)
            this.getAttributesByCategoryandMaterial(this.form.categoryCode,this.form.materialId)
            rs(true)
          }
        })
      })
    },
    // 确认选中的品类
    getCateObj (node, scope) {
      this.form.categoryId = node ? node.categoryId : null
      this.form.categoryCode = node ? node.categoryCode : ''
      this.form.categoryName = node ? node.categoryName : ''
      this.form.struct = node ? node.struct : ''
      this.form.categoryFullName = node ? node.categoryFullName : ''
      if(this.form.categoryCode){
        this.getAttributesByCategory(this.form.categoryCode)
      }
    },
    test(){
      console.log(this.form)
    },
    // 根据选中的品类code 来查询其属性
    async getAttributesByCategory(categoryCode){
      return new Promise((rs, rj) => {
        commonApi.getAttributesByCategory({ categoryCode }).then(res => {
          if (res.data) {
            this.$set(this.form, 'materialAttribute', []);
            this.$set(this.form, 'materialAttribute', res.data || []);
            rs(true);
          } else {
            rj(new Error('No data received'));
          }
        })
      })
    },
    async getAttributesByCategoryandMaterial(categoryCode,materialItemId){
      return new Promise((rs, rj) => {
        commonApi.getAttributesByCategoryandMaterial({ categoryCode,materialItemId }).then(res => {
          if (res.data) {
            this.$set(this.form, 'materialAttribute', []);
            this.$set(this.form, 'materialAttribute', res.data || []);
            rs(true);
          } else {
            rj(new Error('No data received'));
          }
        })
      })
    },
    /* 校验表单 */
    validateForm (formName, isFocus = true) {
      return new Promise(resolve => {
        this.$refs[formName].validate(async valid => {
          resolve(!!valid)
          if (!valid) {
            if (isFocus) {
              this.__focus_error__(this.$t('common.pleasefinishRequired'))
            } else {
              // __focus_error__滚动上去会被审批流遮住，此处不自动滚动
              this.$message.warning(this.$t('common.pleasefinishRequired'))
            }
          }
        })
      })
    },
    async saveBill (type) {
      if (type == 'SUBMIT') {
        let valid = await this.validateForm('detailForm')
        let valid2 =await this.validateForm('detailForm2')
        if (!valid||!valid2) return
      }
      // 至少上传一张图片
      if(this.materialItem.length<1){
        this.$message.warning(this.$t('cusEntry.supplement20250314.atLeastOneMaterialImage'))
        return 
      }
      const materialProfile = Array.isArray(this.form.materialProfile) ? this.form.materialProfile.join() : this.form.materialProfile
      this.$http({
        url: '/api-base/material/materialItem/ext/ext/sccSaveOrUpdate',
        method: 'POST',
        data: {
          materialItem: Object.assign(this.form, { materialProfile }),
          materialAttribute:this.form.materialAttribute,
        }
      }).then(async res => {
        if (type == 'SAVE') this.$message.success(this.$t('common.successSave'))
        await this.getFormDetail(res.data)
        if (type == 'SUBMIT') this.handlerAfter('SUBMIT')
      }).catch(err => {
        console.log(err)
      })
    },
    beforeApprove (data) {
      return new Promise(async resolve => {
        if (this.approveEditable || this.approveEditableFlag) {
          let valid = await this.validateForm('detailForm', false)
          let valid2 = await this.validateForm('detailForm2')
          if (!valid||!valid2) {
            resolve(false)
            return
          }
          // 至少上传一张图片
          if(this.materialItem.length<1){
            this.$message.warning(this.$t('cusEntry.supplement20250314.atLeastOneMaterialImage'))
            return 
          }
          const materialProfile = Array.isArray(this.form.materialProfile) ? this.form.materialProfile.join() : this.form.materialProfile
          this.$http({
            url: '/api-base/material/materialItem/ext/sccSaveOrUpdate',
            method: 'POST',
            data: {
              materialItem: Object.assign(this.form, { materialProfile }),
              materialAttribute:this.form.materialAttribute,
            }
          }).then(res => {
            resolve(true)
          }).catch(err => {
            resolve(false)
          })
        } else {
          resolve(true)
        }
      })
    },
    back () {
      this.$emit('tab-remove', this.$attrs.params.tabName)
      this.__setTabTodo('MaterialMaintenanceList.getQuerydata')
    },
    // 图片上传成功赋值
    handleSuccess (res, file, fileList) {
      let newFileIdList = []
      let newMaterialItem = []
      fileList.forEach(item => {
        // 更新图片列表数据
        if(item.hasOwnProperty('response')){
          newFileIdList.push(item.response.data.fileuploadId)
          newMaterialItem.push(item.response.data)
        }else{
          newFileIdList.push(item.fileuploadId)
          newMaterialItem.push(item)
        }
      });
      this.form.materialProfile = newFileIdList
      this.materialItem = newMaterialItem
    },
    handleRemove(file){
      // 移除文件
      this.form.materialProfile.forEach((item,index)=>{
        if(file.fileuploadId == item){
          this.form.materialProfile.splice(index,1)
        }
      })
    }
  }
}
</script>
<style scoped lang="scss">
</style>
