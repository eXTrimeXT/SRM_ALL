<template>
  <el-container class="flex-container the-inspectionStandardDetail-detail" direction="vertical">
    <el-main>
      <div class="form-container2">
        <el-form
          ref="form"
          :model="form"
          label-width="80px"
          label-position="top"
          :rules="rules"
        >
          <el-collapse v-model="activeDims" class="tab-form-style">
            <!-- 物料信息 -->
            <el-collapse-item ref="baseInfo" :title="$t('qualitySynergy.materialInfo')" name="1">
              <srm-row>
                <!-- 物料编码 -->
                <srm-col>
                  <el-form-item
                    :label="$t('common.materialCode')"
                    :label-width="formLabelWidth"
                    prop="materialCode"
                  >
                    <QuickSearch
                      :show-input="form.materialCode"
                      show-key="materialCode"
                      :scope-data="form"
                      name="scc_base_material_item"
                      :disabled="isReadOnly"
                      @close-quicksearch="getMaterial"
                    />
                  </el-form-item>
                </srm-col>

                <!-- 物料名称 -->
                <srm-col>
                  <el-form-item
                    :label="$t('qualitySynergy.materialName')"
                    :label-width="formLabelWidth"
                  >
                    <el-input v-model="form.materialName" disabled />
                  </el-form-item>
                </srm-col>

                <!-- 品类名称 -->
                <srm-col>
                  <el-form-item
                    :label="$t('qualitySynergy.categoryName')"
                    :label-width="formLabelWidth"
                  >
                    <el-input v-model="form.categoryName" disabled />
                  </el-form-item>
                </srm-col>

                <!-- 业务实体 -->
                <srm-col>
                  <el-form-item :label="$t('qualitySynergy.orgId')" :label-width="formLabelWidth">
                    <el-input v-if="isReadOnly" v-model="form.orgName" disabled />
                    <template v-else>
                      <OrganizationSelector
                        ref="organizationSelector"
                        v-model="form.orgId"
                        :parent-id="-1"
                        node-type="OU"
                        :placeholder="$t('common.pleaseSelect')"
                        @select="getOrg"
                      />
                    </template>
                  </el-form-item>
                </srm-col>

                <!-- 标准状态 -->
                <srm-col>
                  <el-form-item
                    :label="$t('qualitySynergy.inspectionStandardStatus')"
                    :label-width="formLabelWidth"
                  >
                    <dict-select v-model="form.status" code="INSPECTION_STANDARD_STATUS" disabled />
                  </el-form-item>
                </srm-col>

                <!-- 供应商名称 -->
                <srm-col v-if="userType === 'BUYER'">
                  <el-form-item :label="$t('common.vendorName')" :label-width="formLabelWidth">
                    <el-input v-if="isReadOnly" v-model="form.vendorName" disabled />
                    <QuickSearch
                      v-else
                      ref="vendorQuicksearch"
                      :show-input="form.vendorName"
                      show-key="companyCode"
                      :scope-data="form"
                      name="scc_sup_company_info"
                      @close-quicksearch="getVendor"
                    />
                  </el-form-item>
                </srm-col>

                <!-- 生效日期 -->
                <srm-col>
                  <el-form-item
                    :label="$t('qualitySynergy.startDate1')"
                    :label-width="formLabelWidth"
                    prop="startDate"
                  >
                    <el-date-picker
                      v-model="form.startDate"
                      :disabled="isReadOnly"
                      type="date"
                      value-format="yyyy-MM-dd"
                      :placeholder="$t('bidMod.datePicker')"
                    />
                  </el-form-item>
                </srm-col>

                <!-- 失效日期 -->
                <srm-col>
                  <el-form-item :label="$t('qualitySynergy.endDate')" :label-width="formLabelWidth">
                    <el-date-picker
                      v-model="form.endDate"
                      :disabled="isReadOnly"
                      type="date"
                      value-format="yyyy-MM-dd"
                      :placeholder="$t('bidMod.datePicker')"
                    />
                  </el-form-item>
                </srm-col>
              </srm-row>

              <!-- 标准附件 -->
              <srm-col :initCol="1">
                <el-form-item
                  :label="$t('qualitySynergy.sceneAttachment')"
                  :label-width="formLabelWidth"
                >
                  <FileDynamic
                    ref="sceneAttachment"
                    v-model="form.insStandardFiles"
                    scene-module-code="SCENE_PERFORMANCE_INS_STANDARD_ATTACHMENT"
                    :business-id="businessId"
                    :needInit="false"
                    :editable="!isReadOnly"
                  />
                </el-form-item>
              </srm-col>
            </el-collapse-item>

            <!-- 检验项 -->
            <el-collapse-item
              ref="inspectionInfo"
              :title="$t('qualitySynergy.inspectionInfo')"
              name="1"
            >
              <MainHeader v-if="!isReadOnly" :l-span="22" :r-span="2">
                <template slot="left">
                  <!-- 新增 -->
                  <el-button type="primary" @click="addItem">
                    {{ $t('common.add') }}
                  </el-button>
                  <!-- 删除 -->
                  <el-button type="primary" @click="batchDelete">
                    {{ $t('common.delete') }}
                  </el-button>
                </template>
              </MainHeader>
              <BaseTable
                stripe
                :data="form.inspectionStandardProjectList"
                :columns="columns"
                border
                @selection-change="selectionChangeHandle"
                @deleteItem="deleteItem"
              >
                <!-- 检验项目属性 -->
                <template #inspectionProjectAttribute="scope">
                  {{ $getDictLabel("INS_PRO_ATTR", scope.row.inspectionProjectAttribute) }}
                </template>
                <!-- 技术要求 -->
                <template #technicalRequirement="scope">
                  <el-input
                    v-model="scope.row.technicalRequirement"
                    type="textarea"
                    :disabled="isReadOnly"
                  />
                </template>
                <!-- 项目特性 -->
                <template #inspectionProjectPerf="scope">
                  {{ $getDictLabel("INS_PRO_PERF", scope.row.inspectionProjectPerf) }}
                </template>
                <!-- 规格值 -->
                <template #specValue="scope">
                  <el-input-number
                    v-model="scope.row.specValue"
                    style="width:100%"
                    :controls="false"
                    :min="0"
                    :disabled="isReadOnly"
                  />
                </template>
                <!-- 最大值（≤） -->
                <template #specValueMax="scope">
                  <el-input-number
                    v-model="scope.row.specValueMax"
                    style="width:100%"
                    :controls="false"
                    :min="0"
                    :disabled="isReadOnly"
                  />
                </template>
                <!-- 最小值（≥） -->
                <template #specValueMin="scope">
                  <el-input-number
                    v-model="scope.row.specValueMin"
                    style="width:100%"
                    :controls="false"
                    :min="0"
                    :disabled="isReadOnly"
                  />
                </template>
                <!-- 单位 -->
                <template #unit="scope">
                  <el-input v-model="scope.row.unit" :disabled="isReadOnly" />
                </template>
                <!-- 抽样标准 -->
                <template #sampleStandard="scope">
                  <span>{{ $getDictLabel("INS_SAMPLE_STANDARD", scope.row.sampleStandard) }}</span>
                </template>
                <!-- 抽样标准 -->
                <template #sampleMode="scope">
                  {{ $getDictLabel("INS_SAMPLING_METHOD", scope.row.sampleMode) }}
                </template>
                <!-- 严格度 -->
                <template #strictLevel="scope">
                  {{ $getDictLabel("INS_SEVERITY", scope.row.strictLevel) }}
                </template>
                <!-- AQL -->
                <template #aql="scope">
                  <el-input-number
                    v-model="scope.row.aql"
                    style="width:100%"
                    :controls="false"
                    :min="0"
                    :disabled="isReadOnly"
                  />
                </template>
                <!-- 检验水平 -->
                <template #inspectionLevel="scope">
                  {{ $getDictLabel("INS_INSPECTION_LEVEL", scope.row.inspectionLevel) }}
                </template>
                <!-- 固定样本 -->
                <template #fixedSampleQuantity="scope">
                  <el-input-number
                    v-model="scope.row.fixedSampleQuantity"
                    style="width:100%"
                    :controls="false"
                    :min="0"
                    :disabled="isReadOnly"
                    :precision="0"
                  />
                </template>
                <!-- AC -->
                <template #ac="scope">
                  <el-input-number
                    v-model="scope.row.ac"
                    style="width:100%"
                    :controls="false"
                    :min="0"
                    :disabled="isReadOnly"
                    :precision="0"
                  />
                </template>
                <!-- Re -->
                <template #re="scope">
                  <el-input-number
                    v-model="scope.row.re"
                    style="width:100%"
                    :controls="false"
                    :min="0"
                    :disabled="isReadOnly"
                    :precision="0"
                  />
                </template>
                <!-- 特性分级 -->
                <template #perfGrade="scope">
                  {{ $getDictLabel("INS_PERF_GRADE", scope.row.perfGrade) }}
                </template>
                <!-- 检验类型 -->
                <template #inspectionType="scope">
                  {{ $getDictLabel("INS_TYPE", scope.row.inspectionType) }}
                </template>
              </BaseTable>
            </el-collapse-item>
          </el-collapse>
        </el-form>
      </div>
      <CToolbar>
        <template slot="right">
          <!-- 关闭 -->
          <el-button @click="backOne">
            {{ $t("common.close") }}
          </el-button>
          <!-- 暂存 -->
          <el-button v-if="!isReadOnly" @click="saveOne">
            {{ $t("common.staging") }}
          </el-button>
          <!-- 提交 -->
          <el-button v-if="!isReadOnly" type="primary" @click="submitOne">
            {{ $t('problemManagement.submit') }}
          </el-button>
        </template>
      </CToolbar>
      <InspectionItemDialog ref="dialog" :visible.sync="visible" @getSelections="getSelections" />
    </el-main>
  </el-container>
</template>
<script>
import MainHeader from 'lib@/components/Table/MainHeader'
import CToolbar from 'lib@/components/c-toolbar'
import { tabTodoMixin } from '@/utils/mixins'
import QuickSearch from 'lib@/components/QuickSearch'
import OrganizationSelector from 'lib@/components/organization-selector'
import FileDynamic from '@/library/components/c-file-management/file-dynamic'
import BaseTable from 'lib@/components/BaseTable'
import InspectionItemDialog from './inspectionItemDialog'
import { inspectionStandard } from 'mods@/qualitySynergySupplier/api'

export default {
  name: 'InspectionStandardDetail',
  components: {
    MainHeader,
    CToolbar,
    QuickSearch,
    BaseTable,
    OrganizationSelector,
    FileDynamic,
    InspectionItemDialog
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      activeDims: ['1', '2'],
      businessId: null,
      form: {
        materialCode: '',
        materialName: '',
        categoryName: '',
        orgName: '',
        orgId: '',
        status: 'DRAFT',
        vendorId: '',
        vendorCode: '',
        vendorName: '',
        startDate: '',
        endDate: '',
        insStandardFiles: [],
        inspectionStandardProjectList: []
      },
      rules: {
        startDate: [{ required: true, message: this.$t('dataConfMod.msgStartDate') }],  // 生效日期
        materialCode: [{ required: true, message: this.$t('qualitySynergy.materialCodeRequired') }] // 物料编码
      },
      formLabelWidth: '120px',
      columns: [
        // 检验项目
        {
          attrs: {
            label: () => this.$t('qualitySynergy.inspectionProjectName'),
            minWidth: 100,
            prop: 'inspectionProjectName'
          }
        },
        // 检验项目属性
        {
          attrs: {
            label: () => this.$t('qualitySynergy.inspectionProjectAttribute'),
            prop: 'inspectionProjectAttribute',
            minWidth: 100
          },
          slot: 'inspectionProjectAttribute'
        },
        // 检验工具
        {
          attrs: {
            label: () => this.$t('qualitySynergy.inspectionTool'),
            prop: 'inspectionTool',
            minWidth: 100
          }
        },
        // 技术要求
        {
          attrs: {
            label: () => this.$t('qualitySynergy.technicalRequirement'),
            prop: 'technicalRequirement',
            minWidth: 200,
            renderHeader: this._addStarToColumn
          },
          slot: 'technicalRequirement'
        },
        // 检验项目类别
        {
          attrs: {
            label: () => this.$t('qualitySynergy.inspectionCategory'),
            prop: 'inspectionCategory',
            minWidth: 100
          }
        },
        // 项目特性
        {
          attrs: {
            label: () => this.$t('qualitySynergy.inspectionProjectPerf'),
            prop: 'inspectionProjectPerf',
            minWidth: 100
          },
          slot: 'inspectionProjectPerf'
        },
        // 规格值
        {
          attrs: {
            label: () => this.$t('qualitySynergy.specValue'),
            prop: 'specValue',
            minWidth: 100
          },
          slot: 'specValue'
        },
        // 最大值（≤）
        {
          attrs: {
            label: () => this.$t('qualitySynergy.specValueMax') + '（≤）',
            prop: 'specValueMax',
            minWidth: 100
          },
          slot: 'specValueMax'
        },
        // 最小值（≥）
        {
          attrs: {
            label: () => this.$t('qualitySynergy.specValueMin') + '（≥）',
            prop: 'specValueMin',
            minWidth: 100
          },
          slot: 'specValueMin'
        },
        // 单位
        {
          attrs: {
            label: () => this.$t('qualitySynergy.unit'),
            prop: 'unit',
            minWidth: 100
          },
          slot: 'unit'
        },
        // 抽样标准
        {
          attrs: {
            label: () => this.$t('qualitySynergy.sampleStandard'),
            prop: 'sampleStandard',
            minWidth: 100
          },
          slot: 'sampleStandard'
        },
        // 抽样方式
        {
          attrs: {
            label: () => this.$t('qualitySynergy.sampleMode'),
            prop: 'sampleMode',
            minWidth: 100
          },
          slot: 'sampleMode'
        },
        // 严格度
        {
          attrs: {
            label: () => this.$t('qualitySynergy.strictLevel'),
            prop: 'strictLevel',
            minWidth: 100
          },
          slot: 'strictLevel'
        },
        // AQL
        {
          attrs: {
            label: () => 'AQL',
            prop: 'aql',
            minWidth: 100
          },
          slot: 'aql'
        },
        // 检验水平
        {
          attrs: {
            label: () => this.$t('qualitySynergy.inspectionLevel'),
            prop: 'inspectionLevel',
            minWidth: 100
          },
          slot: 'inspectionLevel'
        },
        // 固定样本
        {
          attrs: {
            label: () => this.$t('qualitySynergy.fixedSampleQuantity'),
            prop: 'fixedSampleQuantity',
            minWidth: 100
          },
          slot: 'fixedSampleQuantity'
        },
        // Ac
        {
          attrs: {
            label: () => 'Ac',
            prop: 'ac',
            minWidth: 100
          },
          slot: 'ac'
        },
        // Re
        {
          attrs: {
            label: () => 'Re',
            prop: 're',
            minWidth: 100
          },
          slot: 're'
        },
        // 特性分级
        {
          attrs: {
            label: () => this.$t('qualitySynergy.perfGrade'),
            prop: 'perfGrade',
            minWidth: 100
          },
          slot: 'perfGrade'
        },
        // 检验类型
        {
          attrs: {
            label: () => this.$t('qualitySynergy.inspectionType'),
            prop: 'inspectionType',
            width: 100
          },
          slot: 'inspectionType'
        },
        // 检验依据
        {
          attrs: {
            label: () => this.$t('qualitySynergy.inspectionBasis'),
            prop: 'inspectionBasis',
            minWidth: 100
          }
        }
      ],
      selections: [],
      visible: false
    }
  },
  computed: {
    isReadOnly () {
      return this.$attrs.params.flag === 'view'
    },
    userType () {
      return this.$store.getters.userType
    }
  },
  created () {
    const { flag, row } = this.$attrs.params
    let selectionAttrs = {
      attrs: {
        align: 'center',
        width: 60,
        type: 'selection'
      }
    }
    let operationsAttrs = {
      attrs: {
        align: 'center',
        label: t => t.$t('common.operation'),
        fixed: 'right',
        width: 80
      },
      operations: [
        {
          show: scope => !this.isReadOnly,
          event: 'deleteItem',
          name: this.$t('common.delete'),
          attrs: { type: 'text' }
        }
      ]
    }
    if (flag === 'add') {
      this.columns.unshift(selectionAttrs)
      this.columns.push(operationsAttrs)
      this.$nextTick(() => {
        this.$refs.sceneAttachment.loadFileInfo()
      })
    } else {
      if (flag === 'edit') {
        this.columns.unshift(selectionAttrs)
        this.columns.push(operationsAttrs)
      }
      this.getFormDetail(row.inspectionStandardId)
    }
  },
  methods: {
    // 物料编码
    getMaterial (val, scope) {
      scope.materialId = val ? val.materialId : ''
      scope.materialCode = val ? val.materialCode : ''
      scope.materialName = val ? val.materialName : ''
      scope.categoryId = val ? val.categoryId : ''
      scope.categoryName = val ? val.categoryName : ''
    },
    // 业务实体
    getOrg (val) {
      this.form.orgId = val ? val.organizationId : null
      this.form.orgCode = val ? val.organizationCode : null
      this.form.orgName = val ? val.organizationName : null
    },
    // 供应商
    getVendor (val) {
      this.form.vendorId = val ? val.companyId : null
      this.form.vendorCode = val ? val.companyCode : null
      this.form.vendorName = val ? val.companyName : null
    },

    addItem () {
      this.visible = true
      this.$refs.dialog.init(this.form.inspectionStandardProjectList)
    },
    getSelections (selections) {
      selections.forEach(item => {
        item.technicalRequirement = ''
        item.specValue = undefined
        item.specValueMax = undefined
        item.specValueMin = undefined
        item.unit = ''
        item.aql = undefined
        item.fixedSampleQuantity = undefined
        item.ac = undefined
        item.re = undefined
        this.form.inspectionStandardProjectList.push({ ...item })
      })
    },
    selectionChangeHandle (selections) {
      this.selections = selections
    },
    deleteItem ({ row, $index }) {
      this.form.inspectionStandardProjectList.splice($index, 1)
    },
    batchDelete () {
      this.selections.map(item => {
        let index = this.form.inspectionStandardProjectList.findIndex(
          el => el.inspectionProjectId === item.inspectionProjectId,
        )
        if (index > -1) {
          this.form.inspectionStandardProjectList.splice(index, 1)
        }
      })
      this.selections = []
    },

    getFormDetail (inspectionStandardId) {
      inspectionStandard.getInfo({ inspectionStandardId }).then(res => {
        this.form = res.data
        this.businessId = res.data.inspectionStandardId
        this.$refs.sceneAttachment.loadFileInfo()
        inspectionStandard.listByIspId({ inspectionStandardId }).then(res => {
          this.$set(this.form, 'inspectionStandardProjectList', res.data)
        })
      })
    },
    backOne () {
      if (this.$attrs.params.flag === 'add') {
        this.$emit('tab-remove', 'inspectionStandardDetail')
      } else {
        this.$emit('tab-remove', this.$attrs.params.tabName)
      }
      this.__setTabTodo('inspectionStandardList.getQuerydata')
    },
    saveOne () {
      this.$refs.form.validate(valid => {
        if (valid) {
          inspectionStandard.tempSave(this.form).then(res => {
            this.form.inspectionStandardId = res.data
            this.$message.success(this.$t('common.success'))
            this.__setTabTodo('inspectionStandardList.getQuerydata')
          })
        }
        else {
          this.__jump_error__('baseInfo', null, this.$t('common.pleasefinishRequired'))
        }
      })
    },
    submitOne () {
      this.$refs.form.validate(valid => {
        if (valid) {
          for (const i of this.form.inspectionStandardProjectList) {
            if (!i.technicalRequirement) {
              // 请输入技术要求
              return this.__jump_error__(
                'inspectionInfo',
                null,
                this.$t('qualitySynergy.technicalRequirementRequired'),
              )
            }
            if (i.inspectionProjectPerf === 'METERING' && !i.specValue) {
              // 请输入规格值
              return this.__jump_error__(
                'inspectionInfo',
                null,
                this.$t('qualitySynergy.specValueRequired'),
              )
            }
            if (i.inspectionProjectPerf === 'METERING' && !i.specValueMax) {
              return this.__jump_error__(
                'inspectionInfo',
                null,
                this.$t('qualitySynergy.specValueMaxRequired'),
              )
            }
            if (i.inspectionProjectPerf === 'METERING' && !i.specValueMin) {
              return this.__jump_error__(
                'inspectionInfo',
                null,
                this.$t('qualitySynergy.specValueMinRequired'),
              )
            }
            if (i.sampleStandard === 'FIXED_SAMPLE' && !i.ac) {
              return this.__jump_error__(
                'inspectionInfo',
                null,
                this.$t('qualitySynergy.acRequired'),
              )
            }
            if (i.sampleStandard === 'FIXED_SAMPLE' && !i.re) {
              return this.__jump_error__(
                'inspectionInfo',
                null,
                this.$t('qualitySynergy.reRequired'),
              )
            }
            if (i.sampleStandard === 'GBT2828' && !i.aql) {
              return this.__jump_error__(
                'inspectionInfo',
                null,
                this.$t('qualitySynergy.aqlRequired'),
              )
            }
          }

          inspectionStandard.submit(this.form).then(res => {
            this.$message.success(this.$t('common.success'))
            this.backOne()
          })
        } else {
          this.__jump_error__('baseInfo', null, null)
        }
      })
    }
  }
}
</script>
<style lang="scss" scoped>
.the-inspectionStandardDetail-detail {
  padding-bottom: 50px;
}
</style>
