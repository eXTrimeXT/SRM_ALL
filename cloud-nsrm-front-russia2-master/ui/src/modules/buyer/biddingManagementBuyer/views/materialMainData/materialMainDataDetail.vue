<template>
  <el-container
    class="flex-container material-main-data-detail"
    direction="vertical"
  >
    <el-main>
      <el-form
        ref="detailForm"
        :model="detailFormData"
        :rules="detailFormRules"
        :disabled="isReadonly"
        label-position="top"
        class="detail-form-wrap"
      >
        <srm-row>
          <!--关联类型-->
          <srm-col :init-col="3">
            <el-form-item :label="$t('dataConfMod.formulaRelateType')" prop="formulaRelateType">
              <dict-select
                v-model="detailFormData.formulaRelateType"
                code="FORMULA_RELATE_TYPE"
              />
            </el-form-item>
          </srm-col>

          <!--公式-业务实体-->
          <template v-if="formulaRelateType.isOrgOu">
            <!--业务实体-->
            <srm-col :init-col="3">
              <el-form-item :label="$t('bidMod.affairsEntity')" prop="orgOuIds">
                <organization-selector
                  v-if="!isReadonly"
                  v-model="detailFormData.orgOuIds"
                  :scope="detailFormData"
                  node-type="OU"
                  multiple
                  collapse-tags
                  :parent-id="-1"
                  :placeholder="$t('common.pleaseSelect')"
                  @select="getOrgOuObj"
                />
                <el-tooltip
                  v-else
                  effect="dark"
                  :content="detailFormData.orgOuNames"
                  placement="top-start"
                >
                  <el-input
                    :value="detailFormData.orgOuNames"
                    disabled
                  />
                </el-tooltip>
              </el-form-item>
            </srm-col>
          </template>

          <!--公式-品类-->
          <template v-if="formulaRelateType.isCategory">
            <!--品类编码-->
            <srm-col :init-col="3">
              <el-form-item :label="$t('common.categoryCode')" prop="categoryCode">
                <c-category-select
                  v-model="detailFormData.categoryCode"
                  :scope="detailFormData"
                  show-key="categoryName"
                  @select="getCategoryObj"
                />
              </el-form-item>
            </srm-col>

            <!--品类名称-->
            <srm-col :init-col="3">
              <el-form-item :label="$t('common.categoryName')" prop="categoryName">
                <el-input
                  :value="detailFormData.categoryName"
                  disabled
                />
              </el-form-item>
            </srm-col>
          </template>

          <!--公式-物料-->
          <template v-if="formulaRelateType.isMaterial">
            <!--物料编码-->
            <srm-col :init-col="3">
              <el-form-item :label="$t('materialMainData.materialCode')" prop="materialCode">
                <quick-search
                  :show-input="detailFormData.materialCode"
                  show-key="itemCode"
                  :scope-data="detailFormData"
                  name="scc_base_material_item_display"
                  @close-quicksearch="getItemObj"
                />
              </el-form-item>
            </srm-col>

            <!--物料名称-->
            <srm-col :init-col="3">
              <el-form-item :label="$t('materialMainData.materialName')" prop="materialName">
                <el-input
                  :value="detailFormData.materialName"
                  disabled
                />
              </el-form-item>
            </srm-col>

            <!--单位-->
            <srm-col :init-col="3">
              <el-form-item :label="$t('materialMainData.unit')" prop="unit">
                <el-input
                  :value="$getDictLabel('unit', detailFormData.unit)"
                  disabled
                />
              </el-form-item>
            </srm-col>
          </template>

          <!--选择公式-->
          <srm-col :init-col="3">
            <el-form-item :label="$t('bidMod.biddingManagementBuyer.formulaId')" prop="formulaName">
              <quick-search
                show-key="pricingFormulaName"
                :show-input="detailFormData.formulaName"
                name="scc_bid_pricing_formula_header"
                :scope-data="detailFormData"
                @close-quicksearch="getFormulaValue"
              />
            </el-form-item>
          </srm-col>

          <!--公式明细-->
          <srm-col :init-col="1">
            <el-form-item :label="$t('formula.formulaDetail')">
              <el-input
                :value="detailFormData.formulaValue"
                disabled
                type="textarea"
                :rows="2"
              />
            </el-form-item>
          </srm-col>
        </srm-row>
      </el-form>

      <el-collapse
        v-model="activeCollapse"
        class="border-card"
      >
        <!-- 基础信息 -->
        <el-collapse-item
          :title="$t('materialMainData.preserveAttributes')"
          name="1"
        >
          <base-table
            stripe
            :data="attributeTableData"
            :columns="attributeTableColumns"
            :empty-text="$t('components.noData')"
            border
          >
            <!--属性编号-->
            <template #materialAttributeId="scope">
              <quick-search
                show-key="attributeCode"
                :show-input="scope.row.attributeCode"
                name="ceea_base_material_item_attribute"
                :scope-data="scope"
                disabled
                @close-quicksearch="getAttr"
              />
            </template>

            <!--属性值-->
            <template #attributeValue="scope">
              <el-input
                v-model.number="scope.row.attributeValue"
                type="number"
                :disabled="isReadonly"
              />
            </template>
          </base-table>
        </el-collapse-item>
      </el-collapse>
    </el-main>

    <c-toolbar>
      <template slot="right">
        <template v-if="!isReadonly">
          <!--保存-->
          <el-button
            type="primary"
            @click="saveOrActiveForm('save')"
          >
            {{ $t('common.save') }}
          </el-button>

          <!--生效-->
          <el-button
            type="primary"
            @click="saveOrActiveForm('active')"
          >
            {{ $t('common.active') }}
          </el-button>
        </template>

        <!--取消-->
        <el-button @click="navTabsBack">
          {{ $t('common.cancel') }}
        </el-button>
      </template>
    </c-toolbar>
  </el-container>
</template>

<script>
/**
 * 物料价格公式新增/编辑/查看
 */
import { tabTodoMixin } from '@/utils/mixins'
import BaseTable from 'lib@/components/BaseTable'
import QuickSearch from 'lib@/components/QuickSearch'
import CToolbar from 'lib@/components/c-toolbar'
import OrganizationSelector from 'lib@/components/organization-selector'
import CCategorySelect from 'lib@/components/c-category-select'

export default {
  name: 'MaterialMainDataDetail',

  components: {
    BaseTable,
    QuickSearch,
    CToolbar,
    OrganizationSelector,
    CCategorySelect
  },

  mixins: [tabTodoMixin],

  data () {
    return {
      activeCollapse: ['1'],
      detailFormData: {
        formulaRelateType: '',
        orgOuIds: [],
        categoryCode: '',
        categoryName: '',
        materialCode: '',
        materialName: '',
        unit: '',
        formulaId: '',
        formulaName: '',
        formulaValue: ''
      },
      detailFormRules: {
        formulaRelateType: [{ required: true, message: this.$t('common.pleasefinishRequired') }],
        categoryCode: [{ required: true, message: this.$t('common.pleasefinishRequired') }],
        materialCode: [{ required: true, message: this.$t('common.pleasefinishRequired') }],
        formulaName: [{ required: true, message: this.$t('common.pleasefinishRequired') }],
        orgOuIds: [{ required: true, message: this.$t('common.pleasefinishRequired') }]
      },
      attributeTableData: [],
      attributeTableColumns: [
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: t => t.$t('materialMainData.attributeCode'),
            prop: 'attributeCode'
          },
          slot: 'materialAttributeId'
        },
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: t => t.$t('materialMainData.attributeName'),
            prop: 'attributeName'
          }
        },
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: t => t.$t('materialMainData.attributeValue'),
            prop: 'attributeValue'
          },
          slot: 'attributeValue'
        }
      ],
      attrsParamsRow: null,
      pageFlag: this.$attrs.params.flag,
      isReadonly: this.$attrs.params.readOnly
    }
  },

  computed: {
    // 关联类型
    formulaRelateType () {
      const formulaRelateType = this.detailFormData.formulaRelateType
      return {
        isOrgOu: formulaRelateType === 'ORG_OU',
        isCategory: formulaRelateType === 'CATEGORY',
        isMaterial: formulaRelateType === 'MATERIAL'
      }
    }
  },

  created () {
    if (this.pageFlag !== 'add') {
      // 非新增，查询数据
      this.attrsParamsRow = this.$attrs.params.row
      this.getMaterialFormulaRelate()
    }
  },

  methods: {
    /* 编辑 / 查看 查询数据 */
    getMaterialFormulaRelate () {
      this.$http({
        url: `/api-base/materialFormulaRelate/getMaterialFormulaRelate/${this.attrsParamsRow.relateId}`,
        method: 'GET',
        loading: true
      }).then(data => {
        if (data && data.data) {
          const formData = JSON.parse(JSON.stringify(data.data))
          delete formData.attributeRelateList
          if (formData.orgOuIds) {
            formData.orgOuIds = formData.orgOuIds.split(',').map(item => Number(item))
          }
          this.detailFormData = formData
          this.attributeTableData = data.data.attributeRelateList || []
        }
      })
    },

    /* 根据公式查询属性列表 */
    getItemAttributes (formulaId) {
      if (!formulaId) {
        this.attributeTableData = []
        return
      }
      this.$http({
        url: `/api-base/materialFormulaRelate/getItemAttributes/${formulaId}`,
        method: 'GET',
        loading: true
      }).then(data => {
        this.attributeTableData = (data || {}).data || []
      })
    },

    /* 选择属性编号 */
    getAttr (value, scope) {
      scope.row.materialAttributeId = value.materialAttributeId || ''
      scope.row.attributeCode = value.attributeCode || ''
      scope.row.attributeName = value.attributeName || ''
    },

    /* 选择物料编码 */
    getItemObj (val) {
      this.detailFormData.materialId = val ? val.materialId : ''
      this.detailFormData.materialCode = val ? val.materialCode : ''
      this.detailFormData.materialName = val ? val.materialName : ''
      this.detailFormData.unit = val ? val.unit : ''
    },

    /* 选择业务实体 */
    getOrgOuObj (val) {
      this.detailFormData.orgOuCodes = val.map(item => item.organizationCode).toString()
    },

    /* 选择公式 */
    getFormulaValue (val) {
      this.detailFormData.formulaId = val.pricingFormulaHeaderId || ''
      this.detailFormData.formulaValue = val.pricingFormulaValue || ''
      this.detailFormData.formulaName = val.pricingFormulaName || ''
      // 查询属性列表
      this.getItemAttributes(this.detailFormData.formulaId)
    },

    /* 选择品类 */
    getCategoryObj (val) {
      this.detailFormData.categoryId = val.categoryId
      this.detailFormData.categoryName = val.categoryName
    },

    /* 表单校验 校验通过返回提交的数据 tempSave: true保存，false生效 */
    validateForm (type) {
      return new Promise(resolve => {
        this.$refs.detailForm.validate(valid => {
          if (valid) {
            // 属性维护输入框必填
            for (const item of this.attributeTableData) {
              if (!item.attributeValue) {
                resolve({ valid: false })
                this.$message.warning(this.$t('dataConfMod.attributeRequired'))
                break
              }
            }
            // 删除不必要的提交参数
            const formData = JSON.parse(JSON.stringify(this.detailFormData))
            delete formData.orgOuIds
            resolve({
              valid: true,
              data: {
                ...formData,
                tempSave: type === 'save',
                attributeRelateList: this.attributeTableData
              }
            })
          } else {
            this.__focus_error__()
            resolve({ valid: false })
          }
        })
      })
    },

    /* 保存 / 生效 */
    async saveOrActiveForm (type) {
      const validate = await this.validateForm(type)
      if (validate.valid) {
        this.$http({
          url: '/api-base/materialFormulaRelate/saveMaterialFormulaRelate',
          method: 'POST',
          data: validate.data,
          loading: true
        }).then(() => {
          this.$message.success(this.$t('common.successSave'))
          this.navTabsBack('refresh')
        })
      }
    },

    /* 取消，返回 */
    navTabsBack (type) {
      this.$emit('tab-remove', this.$attrs.tabName)
      if (type === 'refresh') {
        // 需要刷新
        this.__setTabTodo('MaterialMainDataList.getQueryData')
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.material-main-data-detail {
  padding-bottom: 50px;
}
.detail-form-wrap {
  padding: 15px 0;
}
</style>
