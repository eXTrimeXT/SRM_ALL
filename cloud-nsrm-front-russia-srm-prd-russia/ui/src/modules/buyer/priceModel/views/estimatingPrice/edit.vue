<template>
  <el-container
    class="estimating-price-edit"
    direction="vertical"
  >
    <el-main>
      <el-collapse
        v-model="activeDims"
        class="tab-form-style"
      >
        <!-- 基础信息 -->
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
            show-message
            :disabled="readOnly"
          >
            <!--业务实体-->
            <template #orgOuId>
              <OrganizationSelector
                ref="organizationSelector"
                v-model="mergeForm.orgOuId"
                :parent-id="-1"
                node-type="OU"
                :clearable="false"
                :placeholder="$t('common.pleaseSelect')"
                :scope="mergeForm"
                @select="addOrgHandle"
              />
            </template>

            <!-- 模型名称 -->
            <template #priceModelName>
              <PriceModelDialog
                :query-params="{
                  categoryId: mergeForm.categoryId || '',
                  orgId: mergeForm.orgOuId,
                  materialCode: mergeForm.materialCode
                }"
                :input-value="mergeForm.priceModelName"
                @on-ok="getPriceModelInfo"
              />
            </template>

            <!-- 核价人员 -->
            <template #nuclearUser>
              <QuickSearch
                :disabled="readOnly"
                :show-input="mergeForm.nuclearFullName"
                show-key="nickname"
                name="scc_rbac_user_display"
                @close-quicksearch="getNuclearUser"
              />
            </template>

            <!-- 物料编码 -->
            <template #materialCode>
              <QuickSearch
                :disabled="readOnly"
                :disabled-select="!mergeForm.orgOuId"
                :show-input="mergeForm.materialCode"
                show-key="materialCode"
                name="material_item_org_enable"
                :pre-query-data="{ 't.ORG_ID': mergeForm.orgOuId }"
                @close-quicksearch="getMaterialCode"
                @before-open="materialCodeBeforeOpen"
              />
            </template>
          </BaseForm>
        </el-collapse-item>

        <!-- 文件上传 -->
        <el-collapse-item
          :title="$t('priceModel.estimatingPrice.fileUpload')"
          name="2"
        >
          <EstimateFileList
            ref="estimateFileList"
            :detail-data="fileTableDetailData"
            :read-only="readOnly"
          />
        </el-collapse-item>

        <!--属性维护-->
        <el-collapse-item
          v-for="(item, index) in elementTypeCollapseList"
          :key="item.elementType + index"
          :title="`${$getDictLabel('COST_ELEMENT_TYPE', item.elementType)}属性维护`"
          :name="item.elementType"
        >
          <div class="element-type-total">
            <div class="label">
              {{ $t('other.key13') }}
            </div>
            <div class="value">
              <el-input :value="item.estimateTotal" disabled />
            </div>
          </div>

          <!--属性表格-->
          <BaseTable
            stripe
            :data="item.data"
            :columns="modelElementColumns"
            columns-name="modelElementColumns"
            :empty-text="$t('priceModel.costElement.noData')"
            border
          >
            <template #expand="scope">
              <!--展开行内表格-->
              <BaseTable
                stripe
                :data="item.data[scope.$index].estimateAttrLineList"
                :columns="preserveColumns"
                columns-name="preserveColumns"
                :empty-text="$t('priceModel.costElement.noData')"
                border
                style="width: 930px"
              >
                <!-- 属性值 -->
                <template #attributeValue="scopeColumns">
                  <el-select
                    v-if="Array.isArray(scopeColumns.row.enumAttributeMap)"
                    v-model="scopeColumns.row.attributeValue"
                    style="width: 100%"
                    clearable
                    @change="attributeValueChange(scope, index)"
                  >
                    <el-option
                      v-for="itemAttr in scopeColumns.row.enumAttributeMap"
                      :key="itemAttr"
                      :value="itemAttr"
                      :label="itemAttr"
                    />
                  </el-select>
                  <el-input
                    v-else-if="scopeColumns.row.attributeType === 'FORMULA'"
                    v-model.trim="scopeColumns.row.attributeValue"
                    disabled
                  />
                  <el-input
                    v-else
                    v-model.trim="scopeColumns.row.attributeValue"
                  />
                </template>
              </BaseTable>
            </template>

            <!-- 基价 -->
            <template #basePrice="scope">
              <el-input
                v-model="scope.row.basePrice"
                :disabled="readOnly"
              />
            </template>

            <!--基价币种 无法查询基材价格信息且需要为直接计算方式，可手工维护-->
            <template #currencyCode="scope">
              <DictSelect
                v-if="judgeCalculationFlag(scope.row.calculation)"
                v-model="scope.row.currencyCode"
                code="currency"
                :clearable="false"
                :disabled="readOnly"
                @change="value => currencyCodeChange(value, scope)"
              />
              <span v-else>{{ $getDictLabel('currency', scope.row.currencyCode) }}</span>
            </template>

            <!-- 放大系数 -->
            <template #enlargeFactor="scope">
              <el-input
                v-model="scope.row.enlargeFactor"
                v-input-format="{ type: 'float' }"
                :disabled="readOnly"
              />
            </template>
          </BaseTable>
        </el-collapse-item>
      </el-collapse>

      <CToolbar>
        <template slot="right">
          <!--关闭 / 取消-->
          <el-button
            @click="cancelBill"
          >
            {{ readOnly ? $t("common.close") : $t("common.cancel") }}
          </el-button>

          <!--暂存-->
          <el-button
            type="primary"
            :disabled="readOnly || (!(mergeForm.estimateStatus === 'DRAFT' || $attrs.params.flag === 'add'))"
            @click="tempSave"
          >
            {{ $t('bidMod.temporaryStorage') }}
          </el-button>

          <!--估算测试-->
          <el-button
            type="primary"
            :disabled="readOnly || (!(mergeForm.estimateStatus === 'DRAFT' || $attrs.params.flag === 'add'))"
            @click="testEstimate"
          >
            {{ $t('other.key12') }}
          </el-button>

          <!--提交-->
          <el-button
            type="primary"
            :disabled="readOnly"
            @click="submit"
          >
            {{ $t('common.submit') }}
          </el-button>
        </template>
      </CToolbar>
    </el-main>
  </el-container>
</template>

<script>
import { tabTodoMixin } from '@/utils/mixins'
import Big from 'big.js'
import CToolbar from 'lib@/components/c-toolbar'
import BaseForm from 'lib@/components/BaseForm'
import BaseTable from 'lib@/components/BaseTable'
import QuickSearch from 'lib@/components/QuickSearch'
import PriceModelDialog from './edit/priceModelDialog.vue'
import OrganizationSelector from 'lib@/components/organization-selector'
import EstimateFileList from './edit/estimateFileList'
import { basePrice as basePriceApi, estimatingPrice } from 'modb@/priceModel/api'

export default {
  name: 'EstimatingPriceEdit',

  components: {
    CToolbar,
    BaseForm,
    BaseTable,
    QuickSearch,
    PriceModelDialog,
    OrganizationSelector,
    EstimateFileList
  },

  mixins: [tabTodoMixin],

  data () {
    return {
      form: {},
      readOnly: false,
      modelElementColumns: [
        // 行展开
        {
          attrs: {
            type: 'expand',
            fixed: 'left'
          },
          slot: 'expand'
        },
        // 序号
        {
          attrs: {
            align: 'center',
            type: 'index',
            label: t => t.$t('priceModel.costElement.sequenceFlag'),
            width: 60
          }
        },
        // 要素类型
        {
          attrs: {
            prop: 'elementType',
            align: 'center',
            minWidth: '100',
            label: t => t.$t('priceModel.costElement.elementType'),
            formatter: (row, column, cellValue) => this.$getDictLabel('COST_ELEMENT_TYPE', cellValue)
          }
        },
        // 要素编码
        {
          attrs: {
            align: 'center',
            minWidth: '130',
            label: t => t.$t('priceModel.costElement.elementCode'),
            prop: 'elementCode',
            showOverflowTooltip: true
          }
        },
        // 要素名称
        {
          attrs: {
            align: 'center',
            minWidth: '150',
            label: t => t.$t('priceModel.costElement.elementName'),
            prop: 'elementName',
            showOverflowTooltip: true
          }
        },
        // 版本
        {
          attrs: {
            align: 'center',
            minWidth: '80',
            label: t => t.$t('priceModel.costElement.elementVersion'),
            prop: 'elementVersion'
          }
        },
        // 关键属性组合值
        {
          attrs: {
            align: 'center',
            minWidth: '150',
            label: t => t.$t('priceModel.estimatingPrice.crucialAttributesValue'),
            prop: 'crucialAttributesValue',
            showOverflowTooltip: true
          }
        },
        // 关键属性组合
        {
          attrs: {
            align: 'center',
            minWidth: '150',
            label: t => t.$t('priceModel.estimatingPrice.crucialAttributes'),
            prop: 'crucialAttributes',
            showOverflowTooltip: true
          }
        },
        // 单位
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: t => t.$t('priceModel.costElement.unit'),
            prop: 'unit',
            formatter: (row, column, cellValue) => this.$getDictLabel('COST_ELEMENT_UNIT', cellValue)
          }
        },
        // 基价(不含税)
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: t => t.$t('priceModel.costElement.basePrice'),
            prop: 'basePrice'
          },
          slot: 'basePrice'
        },
        // 基价币种
        {
          attrs: {
            align: 'center',
            minWidth: '140',
            label: () => '基价币种',
            prop: 'currencyCode'
          },
          slot: 'currencyCode'
        },
        // 汇率
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: () => this.$t('bid_mod.priceTax'),
            prop: 'priceTax'
          }
        },
        // 本位币基价 = 基价（不含税）* 汇率
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: () => '本位币基价',
            prop: 'currencyBasePrice',
            // 基价（不含税）* 汇率
            formatter: row => this.calculationCurrencyBasePrice(row)
          }
        },
        // 放大系数
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: t => t.$t('priceModel.estimatingPrice.enlargeFactor'),
            prop: 'enlargeFactor'
          },
          slot: 'enlargeFactor'
        },
        // 单项估价
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: t => t.$t('priceModel.estimatingPrice.estimate'),
            prop: 'estimate'
          }
        },
        // 是否必填
        {
          attrs: {
            align: 'center',
            minWidth: '60',
            label: t => t.$t('priceModel.costElement.requiredFlag'),
            prop: 'requiredFlag',
            formatter: (row, column, cellValue) => this.$getDictLabel('YES_OR_NO', cellValue)
          }
        }
      ],
      fileTableDetailData: [],
      preserveColumns: [
        // 序号
        {
          attrs: {
            align: 'center',
            type: 'index',
            label: t => t.$t('priceModel.costElement.sequenceFlag'),
            width: 60
          }
        },
        // 属性名称
        {
          attrs: {
            prop: 'attributeName',
            align: 'center',
            minWidth: '100',
            label: t => t.$t('priceModel.costElement.attributeName')
          }
        },
        // 属性单位
        {
          attrs: {
            prop: 'attributeUnit',
            align: 'center',
            minWidth: '100',
            label: t => t.$t('priceModel.costElement.attributeUnit'),
            formatter: (row, column, cellValue) => this.$getDictLabel('COST_ELEMENT_UNIT', cellValue),
            width: 110
          }
        },
        // 属性类型
        {
          attrs: {
            prop: 'attributeType',
            align: 'center',
            minWidth: '100',
            label: t => t.$t('priceModel.costElement.attributeType'),
            formatter: (row, column, cellValue) => this.$getDictLabel('FEATURE_ATTRIBUTE_TYPE', cellValue),
            width: 110
          }
        },
        // 属性值
        {
          attrs: {
            prop: 'attributeValue',
            align: 'center',
            minWidth: '100',
            label: t => t.$t('priceModel.costElement.attributeValue'),
            width: 150
          },
          slot: 'attributeValue'
        },
        // 是否关键属性
        {
          attrs: {
            prop: 'crucialFlag',
            align: 'center',
            minWidth: '100',
            label: t => t.$t('priceModel.costElement.crucialFlag'),
            formatter: (row, column, cellValue) => this.$getDictLabel('YES_OR_NO', cellValue),
            width: 100
          }
        },
        // 是否必填
        {
          attrs: {
            prop: 'requiredFlag',
            align: 'center',
            minWidth: '100',
            label: t => t.$t('priceModel.costElement.requiredFlag'),
            formatter: (row, column, cellValue,) => this.$getDictLabel('YES_OR_NO', cellValue),
            width: 100
          }
        }
      ],
      formItems: [],
      activeDims: ['1', '2'],
      mergeForm: {
        rateType: 'M'
      },
      elementTypeCollapseList: []
    }
  },

  created () {
    this.formItems = [
      // 单据编码
      {
        itemAttrs: {
          label: this.$t('priceModel.estimatingPrice.estimateCode')
        },
        uiAttrs: {
          key: 'estimateCode',
          disabled: true
        }
      },
      // 单据名称
      {
        itemAttrs: {
          label: this.$t('priceModel.estimatingPrice.estimateName'),
          rules: [
            {
              required: true,
              message: this.$t('common.pleaseInput'),
              type: 'string'
            }
          ]
        },
        uiAttrs: {
          key: 'estimateName'
        }
      },
      // 业务实体
      {
        itemAttrs: {
          label: this.$t('priceModel.costElement.orgName'),
          rules: [
            {
              required: true,
              message: this.$t('common.pleaseSelect'),
              trigger: ['change', 'blur']
            }
          ]
        },
        uiAttrs: {
          key: 'orgOuId'
        },
        slot: 'orgOuId'
      },
      // 物料编码
      {
        itemAttrs: {
          label: this.$t('priceModel.estimatingPrice.materialCode'),
          rules: [
            {
              required: true,
              message: this.$t('common.pleaseSelect'),
              trigger: ['change', 'blur']
            }
          ]
        },
        uiAttrs: {
          key: 'materialCode'
        },
        slot: 'materialCode'
      },
      // 物料名称
      {
        itemAttrs: {
          label: this.$t('priceModel.estimatingPrice.materialName')
        },
        uiAttrs: {
          key: 'materialName',
          disabled: true
        }
      },
      // 采购分类
      {
        itemAttrs: {
          label: this.$t('priceModel.priceModel.categoryName')
        },
        uiAttrs: {
          key: 'categoryName',
          disabled: true
        }
      },
      // 币种
      {
        tag: 'dictSelect',
        itemAttrs: {
          label: this.$t('priceModel.costElement.clearCurrency'),
          rules: [
            {
              required: true,
              message: '请选择币种',
              type: 'string'
            }
          ]
        },
        uiAttrs: {
          key: 'clearCurrency',
          code: 'currency',
          clearable: false
        },
        listeners: {
          change: this.currencyOrRateTypeChange
        }
      },
      // 汇率类型
      {
        tag: 'dictSelect',
        itemAttrs: {
          label: this.$t('bid_mod.exchangeRateType'),
          rules: [
            {
              required: true,
              message: '请选择汇率类型',
              type: 'string'
            }
          ]
        },
        uiAttrs: {
          key: 'rateType',
          code: 'EXCHANGE_RATE_TYPE',
          clearable: false
        },
        listeners: {
          change: this.currencyOrRateTypeChange
        }
      },
      // 模型名称
      {
        itemAttrs: {
          label: this.$t('priceModel.priceModel.priceModelName'),
          rules: [
            {
              required: true,
              message: '请选择模型',
              type: 'string'
            }
          ]
        },
        uiAttrs: {
          key: 'priceModelName'
        },
        slot: 'priceModelName'
      },
      // 模型编码
      {
        itemAttrs: {
          label: this.$t('priceModel.priceModel.priceModelCode')
        },
        uiAttrs: {
          key: 'priceModelCode',
          disabled: true
        }
      },
      // 核价人员
      {
        itemAttrs: {
          label: this.$t('priceModel.estimatingPrice.nuclearUserName')
        },
        uiAttrs: {
          key: 'nuclearFullName'
        },
        slot: 'nuclearUser'
      },
      // 预估总价
      {
        itemAttrs: {
          label: this.$t('priceModel.estimatingPrice.estimatedTotalPrice')
        },
        uiAttrs: {
          key: 'estimatedTotalPrice',
          disabled: true
        }
      },
      // 创建日期
      {
        tag: 'date',
        itemAttrs: {
          label: this.$t('priceModel.costElement.creationDate')
        },
        uiAttrs: {
          key: 'creationDate',
          disabled: true
        }
      }
    ]
  },

  mounted () {
    const { flag, row, readOnly = false } = this.$attrs.params
    this.readOnly = readOnly
    if (flag === 'edit') {
      this.initPageInfo(row.estimateHeadId)
    }
  },

  methods: {
    /* 加载页面信息 */
    initPageInfo (estimateHeadId) {
      estimatingPrice.get(estimateHeadId).then(res => {
        const { estimateHead, estimateFileList, estimateAttrHeadList } = res.data
        this.fileTableDetailData = estimateFileList.concat()
        Object.assign(this.mergeForm, estimateHead)

        this.elementTypeCollapseListArrange(estimateAttrHeadList)
      })
    },

    /* 选择[价格模型]后，触发查询 */
    getPriceModelInfo (value) {
      if (value && value.priceModelHeadId) {
        this.mergeForm.priceModelCode = value.priceModelCode || ''
        this.mergeForm.priceModelHeadId = value.priceModelHeadId || ''
        this.mergeForm.priceModelName = value.priceModelName || ''

        estimatingPrice.attrGet({
          modelHeadId: value.priceModelHeadId,
          categoryCode: value.categoryCode || ''
        }).then(res => {
          this.elementTypeCollapseListArrange(res.data)
        })
      } else {
        // 点击清空按钮时
        this.mergeForm.priceModelCode = ''
        this.mergeForm.priceModelHeadId = ''
        this.mergeForm.priceModelName = ''

        this.elementTypeCollapseList = []
        this.activeDims = ['1', '2']
      }
    },

    /* 选择业务实体 */
    addOrgHandle (node) {
      if (node && this.mergeForm.orgOuId === node.organizationId) {
        return
      }

      this.mergeForm.orgOuId = node ? node.organizationId : ''
      this.mergeForm.orgOuCode = node ? node.organizationCode : ''
      this.mergeForm.orgOuName = node ? node.organizationName : ''

      // 清空模型
      this.getPriceModelInfo(null)

      // 清空物料
      this.getMaterialCode(null, 'org')
    },

    /* 选择一个核价人员 */
    getNuclearUser (value) {
      this.mergeForm.nuclearUserId = value.userId || ''
      this.mergeForm.nuclearUserName = value.username || ''
      this.mergeForm.nuclearFullName = value.nickname || ''
    },

    /* 判断先选业务实体和库存组织才能选物料 */
    materialCodeBeforeOpen (value, callback) {
      if (!this.mergeForm.orgOuId) {
        this.$message.warning('请先选择业务实体！')
        callback(null)
      }
    },

    /* 选择一个物料编码 */
    getMaterialCode (value, type) {
      if (value && this.mergeForm.materialId === value.materialId) {
        return
      }

      this.mergeForm.categoryId = value ? value.categoryId : ''
      this.mergeForm.categoryName = value ? value.categoryName : ''
      this.mergeForm.materialCode = value ? value.materialCode : ''
      this.mergeForm.materialName = value ? value.materialName : ''
      this.mergeForm.materialId = value ? value.materialId : ''

      if (type !== 'org') {
        // 清空模型
        this.getPriceModelInfo(null)
      }
    },

    /* 编排属性维护列表数据 */
    elementTypeCollapseListArrange (val) {
      if (!val || !Array.isArray(val)) {
        return
      }

      // 编排elementTypeCollapseList 根据类型汇总
      let collapseList = []
      val.forEach(item => {
        const typeIndex = collapseList.findIndex(collapse => collapse.elementType === item.elementType)
        if (typeIndex >= 0) {
          collapseList[typeIndex].data.push(item)
        } else {
          collapseList.push({
            elementType: item.elementType,
            data: [item]
          })
        }
      })

      collapseList = collapseList.map(item => {
        return {
          ...item,
          // 合计estimate
          estimateTotal: item.data
            .map(dataItem => dataItem.estimate || 0)
            .reduce((prev, curr) => !isNaN(Number(curr)) ? prev + curr : prev, 0),
          data: item.data.map(dataItem => {
            return {
              ...dataItem,
              // 更新编排estimateAttrLineList
              estimateAttrLineList: dataItem.estimateAttrLineList.map(lineItem => {
                let resultItem = { ...lineItem }
                if (lineItem.enumAttributeMap && lineItem.attributeType === 'ENUM') {
                  resultItem = {
                    ...resultItem,
                    enumAttributeMap: lineItem.enumAttributeMap.split(',')
                  }
                }
                if (lineItem.attributeType === 'FORMULA') {
                  resultItem = {
                    ...resultItem,
                    attributeValue: lineItem.enumAttributeMap
                  }
                }
                return resultItem
              })
            }
          })
        }
      })

      // 材质提到最前
      collapseList.sort(a => a.elementType === 'MATERIAL' ? -1 : 1)

      this.elementTypeCollapseList = collapseList

      // 打开新增的折叠面板
      this.activeDims = this.activeDims.concat(this.elementTypeCollapseList.map(item => item.elementType))
    },

    /* 判断行的成本要素计算方式，是否为直接计算 */
    judgeCalculationFlag (calculation) {
      // 存在 && 直接计算
      return calculation === 'DIRECT_CALCULATION'
    },

    /* 基础信息币种和汇率类型变更 */
    async currencyOrRateTypeChange () {
      // 更新所有表格行的汇率
      if (!this.mergeForm.clearCurrency || !this.mergeForm.rateType || this.elementTypeCollapseList.length === 0) {
        return
      }
      // 1.找出elementTypeCollapseList需要查询更新汇率的行 [存在币种 && 直接计算]
      let filterCurrencyCodeList = []
      this.elementTypeCollapseList.forEach(item => {
        filterCurrencyCodeList = filterCurrencyCodeList.concat(
          item.data
            .filter(dataItem => dataItem.currencyCode && this.judgeCalculationFlag(dataItem.calculation))
            .map(dataItem => dataItem.currencyCode)
        )
      })

      if (filterCurrencyCodeList.length === 0) {
        return
      }

      let rateList = []
      if (filterCurrencyCodeList.length === 1 && filterCurrencyCodeList[0] === this.mergeForm.clearCurrency) {
        // 列表只有本位币 不需要查 直接赋值1
        rateList = [
          {
            fromCurrencyCode: this.mergeForm.clearCurrency,
            toCurrencyCode: this.mergeForm.clearCurrency,
            priceTax: 1
          }
        ]
      } else {
        // 2.批量查询汇率
        const { status, list } = await this.queryPurchaseExchangeRate(filterCurrencyCodeList)
        if (status) {
          rateList = list.concat()
        }
      }
      // 3.批量回显汇率 如果item.currencyCode === this.mergeForm.clearCurrency直接写1
      if (rateList.length) {
        this.elementTypeCollapseList.forEach(item => {
          item.data.forEach(dataItem => {
            if (this.judgeCalculationFlag(dataItem.calculation) && dataItem.currencyCode) {
              if (dataItem.currencyCode === this.mergeForm.clearCurrency) {
                dataItem.priceTax = 1
              } else {
                dataItem.priceTax = this.findRateByList(rateList, dataItem.currencyCode)
              }
            }
          })
        })
        this.$forceUpdate()
      }
    },

    /* 属性维护列表变更基价币种 */
    async currencyCodeChange (value, { row }) {
      if (!this.mergeForm.clearCurrency) {
        return
      }

      // 判断变更的币种跟本位币一致 直接赋值汇率为1
      if (this.mergeForm.clearCurrency === value) {
        row.priceTax = 1
        return
      }

      try {
        const { status, list } = await this.queryPurchaseExchangeRate([value])
        row.priceTax = status && list.length ? this.findRateByList(list, value) : ''
      } catch (e) { /* nothing */ }
    },

    /* 关键属性值改变 跟后端约定了按表格顺序编排 */
    async attributeValueChange ({ row, $index }, collapseIndex) {
      // 更新属性表格中的关键属性组合值
      const attributeValueCombination = row.estimateAttrLineList
        .filter(i => i.crucialFlag === 'Y')
        .reduce((prev, curr) => {
          prev += `[${curr.attributeValue}]`
          return prev
        }, '')

      // 获取属性名称组合
      const keyAttributeCombination = row.estimateAttrLineList
        .filter(i => i.crucialFlag === 'Y')
        .reduce((prev, curr) => {
          prev += `[${curr.attributeName}]`
          return prev
        }, '')

      // 设置属性值
      this.$set(
        this.elementTypeCollapseList[collapseIndex].data[$index],
        'crucialAttributesValue',
        attributeValueCombination
      )

      // 必须都选完才去查
      if (
        attributeValueCombination &&
        attributeValueCombination.indexOf('[]') === -1 &&
        keyAttributeCombination &&
        keyAttributeCombination.indexOf('[]') === -1
      ) {
        // 查询基价
        const { data } = await basePriceApi.queryBy({
          priceVersion: row.elementVersion,
          elementCode: row.elementCode,
          attributeValueCombination,
          keyAttributeCombination
        })

        if (!data) {
          // 清空币种和汇率和本位币基价 只能一个个变更，否则会触发表格整体更新
          this.$set(this.elementTypeCollapseList[collapseIndex].data[$index], 'currencyCode', '')
          this.$set(this.elementTypeCollapseList[collapseIndex].data[$index], 'priceTax', '')
          this.$set(this.elementTypeCollapseList[collapseIndex].data[$index], 'currencyBasePrice', '')
          return
        }

        const { basePrice, clearCurrency } = data
        if (basePrice) {
          // 设置覆盖基价
          this.$set(
            this.elementTypeCollapseList[collapseIndex].data[$index],
            'basePrice',
            basePrice
          )
        }

        // 覆盖币种
        this.$set(
          this.elementTypeCollapseList[collapseIndex].data[$index],
          'currencyCode',
          clearCurrency
        )

        if (clearCurrency) {
          let rate
          if (clearCurrency === this.mergeForm.clearCurrency) {
            rate = 1
          } else {
            // 根据币种查询汇率
            const { status, list } = await this.queryPurchaseExchangeRate([clearCurrency])
            if (status) {
              // 从list里找到所需的汇率
              rate = this.findRateByList(list, clearCurrency)
            }
          }
          // 覆盖汇率
          this.$set(
            this.elementTypeCollapseList[collapseIndex].data[$index],
            'priceTax',
            rate
          )
        }
      }
    },

    /* 查询汇率 支持批量 */
    async queryPurchaseExchangeRate (fromCodes) {
      if (!this.mergeForm.clearCurrency) {
        this.$message.warning('请维护基础信息币种数据')
        return { status: false }
      }
      if (!this.mergeForm.rateType) {
        this.$message.warning('请维护基础信息汇率类型数据')
        return { status: false }
      }

      const paramData = {
        // 目标币种 表单币种
        toCurrencyCode: this.mergeForm.clearCurrency,
        // 汇率类型
        rateType: this.mergeForm.rateType,
        // 来源币种 基价币种
        fromCurrencyCodeList: fromCodes.concat()
      }

      // 去重
      paramData.fromCurrencyCodeList = [...new Set(paramData.fromCurrencyCodeList)]

      const data = await estimatingPrice.getPriceTaxList(paramData)

      if (data && data.data && Array.isArray(data.data)) {
        if (data.data.length === 0 && paramData.fromCurrencyCodeList.length === 1) {
          // 只查一个时候查不到给予提示
          this.$message.warning(`请配置${paramData.fromCurrencyCodeList[0]} TO ${paramData.toCurrencyCode} 汇率类型为${this.$getDictLabel('EXCHANGE_RATE_TYPE', this.mergeForm.rateType)}的汇率!`)
          return { status: false }
        }
        return {
          status: true,
          list: data.data
        }
      }

      return { status: false }
    },

    /* 根据来源币种，从汇率列表中找到需要的汇率 */
    findRateByList (list, code) {
      const rateObj = list.find(item => item.fromCurrencyCode === code)
      return rateObj ? rateObj.priceTax : ''
    },

    /* 计算本位币基价 本位币基价 = 基价（不含税）* 汇率 保留两位小数 */
    calculationCurrencyBasePrice (row) {
      const { basePrice, priceTax } = row
      // 直接计算
      if (!this.judgeCalculationFlag(row.calculation) || !basePrice || !priceTax) {
        return ''
      }
      const bigBasePrice = new Big(basePrice)
      const bigPriceTax = new Big(priceTax)
      return bigBasePrice.times(bigPriceTax).round(2).toString()
    },

    /* 编排提交数据 */
    submitDataArrange () {
      let headList = []
      this.elementTypeCollapseList.forEach(item => {
        headList = headList.concat(item.data || [])
      })
      return {
        estimateHead: this.mergeForm,
        estimateFileList: this.$refs.estimateFileList.getParamsData(),
        estimateAttrHeadList: headList
      }
    },

    /* 暂存 */
    tempSave () {
      estimatingPrice.tempSave(this.submitDataArrange()).then(res => {
        this.$message.success('暂存成功')
        this.mergeForm = res.data
      })
    },

    /* 估价测试 */
    testEstimate () {
      estimatingPrice.testEstimate(this.submitDataArrange()).then(res => {
        this.$message.success(this.$t('common.success'))
        this.initPageInfo(res.data.estimateHeadId)
      })
    },

    /* 提交(生效) */
    submit () {
      this.$refs.form.validate()
        .then(() => {
          const h = this.$createElement
          this.$msgbox({
            title: '注意',
            message: h('span', null, '提交成功后单据生效，不能再次编辑'),
            showCancelButton: true,
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            beforeClose: (action, instance, done) => {
              if (action === 'confirm') {
                instance.confirmButtonLoading = true
                instance.confirmButtonText = '执行中...'
                estimatingPrice.submit(this.submitDataArrange()).then(res => {
                  instance.confirmButtonLoading = false
                  done()
                  this.$message.success(this.$t('common.successSubmit'))
                  this.cancelBill()
                }).catch(() => {
                  instance.confirmButtonLoading = false
                  done()
                })
              } else {
                instance.confirmButtonLoading = false
                done()
              }
            }
          })
        })
        .catch(() => {
          this.__focus_error__()
        })
    },

    /* 返回 */
    cancelBill () {
      const { flag, row } = this.$attrs.params
      if (flag === 'add') {
        this.$emit('tab-remove', 'estimatingPriceEdit')
      } else {
        this.$emit('tab-remove', 'estimatingPriceEdit' + row.estimateHeadId)
      }
      this.__setTabTodo('EstimatingPriceList.getQuerydata')
    }
  }
}
</script>

<style lang="scss" scoped>
.estimating-price-edit {
  padding-bottom: 40px;
}

.element-type-total {
  display: flex;
  margin-bottom: 15px;
  .label {
    text-align: right;
    line-height: 32px;
    padding-right: 15px;
  }
  .value {
    width: 200px;
  }
}
</style>
