<template>
  <el-container
    class="priceModelEdit"
    direction="vertical"
  >
    <el-main>
      <el-collapse
        v-model="activeDims"
        class="tab-form-style"
      >
        <!--基础信息-->
        <el-collapse-item
          :title="$t('priceModel.costElement.baseInfo')"
          name="1"
        >
          <BaseForm
            ref="form"
            class="base-form"
            :form-items="formItems"
            :merge-form.sync="mergeForm"
            :inline="false"
            :status-icon="false"
            :show-message="true"
            :disabled="readOnly"
          >
            <template #orgId>
              <OrganizationSelector
                ref="organizationSelector"
                v-model="mergeForm.orgId"
                :parent-id="-1"
                node-type="OU"
                :placeholder="$t('common.pleaseSelect')"
                :scope="mergeForm"
                @select="addOrgHandle"
              />
            </template>
          </BaseForm>
        </el-collapse-item>

        <!--采购分类-->
        <el-collapse-item
          :title="$t('priceModel.priceModel.categoryName')"
          name="2"
        >
          <div
            v-if="!readOnly"
            style="margin: 8px 0;"
          >
            <el-button
              type="primary"
              class="detail-pbtn"
              @click="addFormula"
            >
              {{ $t("common.add") }}
            </el-button>
          </div>
          <BaseTable
            stripe
            :data="categoryTableData"
            :columns="categoryColumns"
            columns-name="categoryColumns"
            :empty-text="$t('priceModel.costElement.noData')"
            border
            @deleteAttr2="deleteAttr2"
          >
            <!-- categoryCode -->
            <template #categoryCode="scope">
              <QuickSearch
                :disabled="readOnly"
                :show-input="categoryTableData[scope.$index].categoryCode"
                show-key="categoryCode"
                :scope-data="scope"
                name="scc_base_purchase_category"
                @close-quicksearch="getQuickSearch"
              />
            </template>
          </BaseTable>
        </el-collapse-item>

        <!--详细信息-->
        <el-collapse-item
          :title="$t('priceModel.costElement.detailInfo')"
          name="3"
        >
          <div
            v-if="!readOnly"
            style="margin: 8px 0;"
          >
            <el-button
              type="primary"
              class="detail-pbtn"
              @click="addAttr"
            >
              {{ $t("common.add") }}
            </el-button>
          </div>
          <BaseTable
            stripe
            :data="modelElementTableData"
            :columns="modelElementColumns"
            columns-name="modelElementColumns"
            :empty-text="$t('priceModel.costElement.noData')"
            border
            @deleteAttr1="deleteAttr1"
          >
            <!-- requiredFlag -->
            <template #requiredFlag="scope">
              <el-checkbox
                v-model="modelElementTableData[scope.$index].requiredFlag"
                :disabled="readOnly"
                true-label="Y"
                false-label="N"
              />
            </template>
          </BaseTable>
        </el-collapse-item>
      </el-collapse>

      <!--选择成本要素-->
      <SrmDialog
        v-if="editCondVisible"
        :visible.sync="editCondVisible"
        :title="$t('priceModel.priceModel.checkCostElemntTitle')"
        size="large"
      >
        <CostElementList ref="costElementList" :org-id="mergeForm.orgId" />

        <template
          #footer
          class="dialog-footer"
        >
          <el-button @click="editCondVisible = false">
            {{ $t("common.cancel") }}
          </el-button>
          <el-button
            type="primary"
            @click="saveCostElement"
          >
            {{ $t("common.submit") }}
          </el-button>
        </template>
      </SrmDialog>

      <CToolbar>
        <template #right>
          <el-button
            @click="cancelBill"
          >
            {{ readOnly ? $t("common.close") : $t("common.cancel") }}
          </el-button>
          <el-button
            v-if="!readOnly"
            @click="tempSave"
          >
            {{ $t('bidMod.temporaryStorage') }}
          </el-button>
          <el-button
            v-if="!readOnly"
            type="primary"
            @click="submit"
          >
            {{ $t('problemManagement.submit') }}
          </el-button>
        </template>
      </CToolbar>
    </el-main>
  </el-container>
</template>

<script>
import { tabTodoMixin } from '@/utils/mixins'
import CToolbar from 'lib@/components/c-toolbar'
import BaseForm from 'lib@/components/BaseForm'
import BaseTable from 'lib@/components/BaseTable'
import QuickSearch from 'lib@/components/QuickSearch'
import OrganizationSelector from 'lib@/components/organization-selector'
import CostElementList from './costElementList'
import { priceModel } from 'modb@/priceModel/api'

export default {
  name: 'PriceModelEdit',

  components: {
    CToolbar,
    OrganizationSelector,
    BaseForm,
    BaseTable,
    CostElementList,
    QuickSearch
  },

  mixins: [tabTodoMixin],

  data () {
    return {
      modelElementTableData: [],
      modelElementColumns: [
        {
          attrs: {
            align: 'center',
            type: 'index',
            width: '65',
            label: t => t.$t('priceModel.costElement.sequenceFlag')
          }
        },
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: t => t.$t('priceModel.costElement.elementType'),
            formatter: (row, column, cellValue) => this.$getDictLabel('COST_ELEMENT_TYPE', cellValue),
            prop: 'elementType'
          }
        },
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: t => t.$t('priceModel.costElement.elementCode'),
            prop: 'elementCode'
          }
        },
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: t => t.$t('priceModel.costElement.elementName'),
            prop: 'elementName'
          }
        },
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: t => t.$t('priceModel.costElement.elementVersion'),
            prop: 'elementVersion'
          }
        },
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: t => t.$t('priceModel.costElement.unit'),
            prop: 'unit',
            formatter: (row, column, cellValue) => this.$getDictLabel('COST_ELEMENT_UNIT', cellValue)
          }
        },
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: t => t.$t('priceModel.priceModel.requiredFlag'),
            prop: 'requiredFlag'
          },
          slot: 'requiredFlag'
        },
        {
          attrs: {
            align: 'center',
            label: t => t.$t('common.operation'),
            fixed: 'right',
            width: 80
          },
          operations: [
            {
              key: 'deleteAttr',
              show: () => !this.readOnly,
              event: 'deleteAttr1',
              name: this.$t('common.delete'),
              attrs: { type: 'text' }
            }
          ]
        }
      ],
      categoryTableData: [],
      categoryColumns: [
        {
          attrs: {
            align: 'center',
            type: 'index',
            width: '65',
            label: t => t.$t('priceModel.costElement.sequenceFlag')
          }
        },
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: t => t.$t('priceModel.priceModel.categoryCode'),
            prop: 'categoryCode'
          },
          slot: 'categoryCode'
        },
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: t => t.$t('priceModel.priceModel.categoryName'),
            prop: 'categoryName'
          }
        },
        {
          attrs: {
            align: 'center',
            label: t => t.$t('common.operation'),
            fixed: 'right',
            width: 80
          },
          operations: [
            {
              key: 'deleteAttr',
              show: () => !this.readOnly,
              event: 'deleteAttr2',
              name: this.$t('common.delete'),
              attrs: { type: 'text' }
            }
          ]
        }
      ],
      formItems: [],
      editCondVisible: false,
      readOnly: false,
      activeDims: ['1', '2', '3'],
      mergeForm: {
        orgId: null
      }
    }
  },

  created () {
    this.formItems = [
      {
        itemAttrs: {
          label: this.$t('priceModel.priceModel.priceModelCode')
        },
        uiAttrs: {
          key: 'priceModelCode',
          disabled: true
        }
      },
      {
        itemAttrs: {
          label: this.$t('priceModel.priceModel.priceModelName'),
          rules: [
            {
              required: true,
              message: this.$t('common.pleaseInput'),
              type: 'string'
            }
          ]
        },
        uiAttrs: {
          key: 'priceModelName'
        }
      },
      {
        itemAttrs: {
          label: this.$t('priceModel.costElement.orgName'),
          rules: [
            {
              required: true,
              message: this.$t('common.pleaseInput'),
              type: 'string',
              trigger: 'blur'
            }
          ]
        },
        uiAttrs: {
          key: 'orgId'
        },
        slot: 'orgId'
      },
      {
        tag: 'date',
        itemAttrs: {
          label: this.$t('priceModel.costElement.creationDate')
        },
        uiAttrs: {
          key: 'creationDate',
          disabled: true
        }
      },
      {
        itemAttrs: {
          label: this.$t('priceModel.costElement.createdBy')
        },
        uiAttrs: {
          key: 'createdUserName',
          disabled: true
        }
      },
      {
        tag: 'dictSelect',
        itemAttrs: {
          label: this.$t('priceModel.costElement.status')
        },
        uiAttrs: {
          key: 'status',
          disabled: true,
          code: 'PRICE_MODEL_STATUS'
        }
      }
    ]
  },

  mounted () {
    const { flag, row, readOnly = false } = this.$attrs.params
    this.readOnly = readOnly
    if (flag === 'edit') {
      this.loadDataForEditAndView(row.priceModelHeadId)
    }
  },

  methods: {
    loadDataForEditAndView (priceModelHeadId) {
      priceModel.get(priceModelHeadId).then(res => {
        const { modelCategoryList, modelElementList, modelHead } = res.data
        this.categoryTableData = modelCategoryList
        this.modelElementTableData = modelElementList
        this.mergeForm = modelHead
      })
    },

    deleteAttr1 (scope) {
      this.modelElementTableData.splice(scope.$index, 1)
    },

    deleteAttr2 (scope) {
      this.categoryTableData.splice(scope.$index, 1)
    },

    getQuickSearch (value, scope) {
      scope.row.categoryName = value.categoryName || ''
      scope.row.categoryId = value.categoryId || ''
      scope.row.categoryCode = value.categoryCode || ''
      this.$nextTick(() => {
        this.$set(
          this.categoryTableData,
          scope.$index,
          this.categoryTableData[scope.$index]
        )
      })
    },

    /* 保存成本要素 */
    saveCostElement () {
      const currentRows = this.$refs.costElementList.currentRows
      const exist = this.modelElementTableData.map(i => i.costElementId)
      this.modelElementTableData = this.modelElementTableData.concat(currentRows.filter(i => !exist.includes(i.costElementId)))
      this.editCondVisible = false
    },

    addFormula () {
      this.categoryTableData.push({})
    },

    /* 新增详细信息 */
    addAttr () {
      // 校验必须选择采购组织
      if (!this.mergeForm.orgId) {
        // '请先选择业务实体'
        this.$message.warning(this.$t('orderMod.selBusinessEntityFirst'))
        return
      }
      this.editCondVisible = true
    },

    /* 变更采购组织 */
    addOrgHandle (e, value, scope) {
      scope.orgId = e ? e.organizationId : ''
      scope.orgCode = e ? e.organizationCode : ''
      scope.orgName = e ? e.organizationName : ''

      // 清空详细信息
      this.modelElementTableData = []
    },

    // 暂存
    tempSave () {
      const data = {
        modelHead: this.mergeForm,
        modelCategoryList: this.categoryTableData,
        modelElementList: this.modelElementTableData.map(
          ({ requiredFlag, ...rest }) => ({
            ...rest,
            requiredFlag: requiredFlag === 'Y' ? 'Y' : 'N'
          })
        )
      }
      priceModel.tempSave(data).then(res => {
        this.$message({
          type: 'success',
          message: this.$t('vendorMod.temporarySuccess')  // '暂存成功'
        })
        this.loadDataForEditAndView(res.data.priceModelHeadId)
      })
    },

    // 提交
    submit () {
      const data = {
        modelHead: this.mergeForm,
        modelCategoryList: this.categoryTableData,
        modelElementList: this.modelElementTableData.map(
          ({ requiredFlag, ...rest }) => ({
            ...rest,
            requiredFlag: requiredFlag === 'Y' ? 'Y' : 'N'
          })
        )
      }
      const h = this.$createElement
      this.$msgbox({
        title: this.$t('dataConfMod.notice'),  // '注意'
        message: h('span', null, this.$t('dataConfMod.doNotEdit')),  //'提交成功后单据生效，不能再次编辑'
        showCancelButton: true,
        confirmButtonText: this.$t('common.confirm'),  // '确定'
        cancelButtonText: this.$t('components.common.cancel'),  // '取消'
        beforeClose: (action, instance, done) => {
          if (action === 'confirm') {
            instance.confirmButtonLoading = true
            instance.confirmButtonText = this.$t('dataConfMod.inExecution')  // '执行中...'
            priceModel.submit(data).then(res => {
              instance.confirmButtonLoading = false
              done()
              this.$message({
                type: 'success',
                message: this.$t('common.successSubmit')  // '提交成功'
              })
              this.cancelBill()
            }).catch(res => {
              instance.confirmButtonLoading = false
              done()
            })
          } else {
            instance.confirmButtonLoading = false
            done()
          }
        }
      })
    },

    cancelBill () {
      const { flag, row } = this.$attrs.params
      if (flag === 'add') {
        this.$emit('tab-remove', 'priceModelEdit')
      } else {
        this.$emit('tab-remove', 'priceModelEdit' + row.priceModelHeadId)
      }
      this.__setTabTodo('priceModelList.getQuerydata')
    }
  }
}
</script>
<style scoped lang="scss">
.priceModelEdit {
  height: 100%;
  .sub_header {
    padding: 4px 11px;
    background: #eee;
  }
  .el-table .el-date-editor {
    width: 135px;
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
