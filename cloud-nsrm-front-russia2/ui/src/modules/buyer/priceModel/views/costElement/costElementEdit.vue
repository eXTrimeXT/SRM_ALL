<template>
  <el-container
    class="costElementEdit"
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
          <BaseInfo
            ref="baseInfo"
            :base-info="mergeForm"
            :readonly="readOnly"
            @fee-calc-visible="value => showFeeCalc = value"
          />
        </el-collapse-item>

        <!--详细信息-->
        <el-collapse-item
          :title="$t('priceModel.costElement.detailInfo')"
          name="2"
        >
          <!-- 费率公式列表 -->
          <BaseTable
            v-if="showFeeCalc"
            stripe
            :data="feeCalcTableData"
            :columns="feeCalcColumns"
            columns-name="feeCalcColumns"
            :empty-text="$t('priceModel.costElement.noData')"
            border
          >
            <template #calculationBasis="scope">
              <DictSelect
                v-model="feeCalcTableData[scope.$index].calculationBasis"
                code="RATE_CALCULATION_BASIS"
                :disabled="readOnly"
                @change="calcCalculationFormula"
              />
            </template>
            <!-- rate -->
            <template #rate="scope">
              <el-input
                v-model="feeCalcTableData[scope.$index].rate"
                :disabled="readOnly"
                type="number"
                @change="calcCalculationFormula"
              >
                <template slot="suffix">
                  %
                </template>
              </el-input>
            </template>
          </BaseTable>

          <!-- 非费率公式时，展现下面的信息 -->
          <el-tabs
            v-else
            v-model="activeName"
            type="card"
          >
            <!-- 第一个列表: 要素属性列表 -->
            <el-tab-pane
              :label="$t('priceModel.costElement.first')"
              name="first"
            >
              <div
                v-if="!readOnly"
                style="margin: 15px 0;"
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
                :data="firstTableData"
                :columns="firstColumns"
                columns-name="firstColumns"
                :empty-text="$t('priceModel.costElement.noData')"
                border
                @moveUp="moveUp"
                @moveDown="moveDown"
                @deleteAttr="deleteAttr"
              >
                <!-- 属性名称 -->
                <template #attributeNameHeader="scope">
                  <em class="toRequired">*</em>{{ scope.column.label }}
                </template>

                <template #attributeName="scope">
                  <el-input
                    v-model="firstTableData[scope.$index].attributeName"
                    :disabled="readOnly"
                    maxlength="50"
                    show-word-limit
                  />
                </template>

                <!-- 属性单位 -->
                <template #attributeUnit="scope">
                  <DictSelect
                    v-model="firstTableData[scope.$index].attributeUnit"
                    code="COST_ELEMENT_UNIT"
                    filterable
                    :disabled="readOnly"
                    clearable
                  />
                </template>

                <!-- 属性类型 -->
                <template #attributeTypeHeader="scope">
                  <em class="toRequired">*</em>{{ scope.column.label }}
                </template>

                <template #attributeType="scope">
                  <DictSelect
                    v-model="firstTableData[scope.$index].attributeType"
                    code="FEATURE_ATTRIBUTE_TYPE"
                    :disabled="readOnly"
                    @change="value => attributeTypeChange(value, scope)"
                  />
                </template>

                <!-- 属性值 -->
                <template #attributeValueHeader="scope">
                  <em class="toRequired">*</em>{{ scope.column.label }}
                  <!-- 属性类型为枚举时，请使用英文逗号分隔不同值 -->
                  <el-tooltip
                    :content="$t('priceModel.costElement.useMapDataTip')"
                    placement="top"
                  >
                    <em class="el-icon-info" />
                  </el-tooltip>
                </template>

                <template #attributeValue="scope">
                  <el-input
                    v-if="['ENUM', 'FORMULA'].includes(scope.row.attributeType)"
                    v-model="firstTableData[scope.$index].attributeValue"
                    :disabled="readOnly"
                    maxlength="240"
                    show-word-limit
                    @input="formatAttributeVlaue(scope)"
                    @change="value => attributeValueChange(value, scope)"
                  />
                  <span v-else />
                </template>

                <!-- 关键属性 -->
                <template #crucialFlag="scope">
                  <el-checkbox
                    v-if="scope.row.attributeType === 'ENUM'"
                    v-model="firstTableData[scope.$index].crucialFlag"
                    true-label="Y"
                    false-label="N"
                    :disabled="readOnly"
                    @change="value => crucialFlagChange(value, scope)"
                  />
                </template>

                <!-- 是否必填 -->
                <template #requiredFlag="scope">
                  <el-checkbox
                    v-model="firstTableData[scope.$index].requiredFlag"
                    :disabled="
                      (scope.row.attributeType === 'ENUM' &&
                        scope.row.crucialFlag === 'Y') ||
                        readOnly
                    "
                    true-label="Y"
                    false-label="N"
                  />
                </template>

                <!-- 是否可用 -->
                <template #disableFlag="scope">
                  <el-checkbox
                    v-model="firstTableData[scope.$index].disableFlag"
                    true-label="Y"
                    false-label="N"
                    :disabled="readOnly"
                  />
                </template>
              </BaseTable>
            </el-tab-pane>

            <!--用量公式-->
            <el-tab-pane
              :label="$t('priceModel.costElement.second')"
              name="second"
            >
              <div style="margin: 15px 0;">
                <el-button
                  v-if="!readOnly"
                  type="primary"
                  class="detail-pbtn"
                  @click="addFormula"
                >
                  {{ $t("common.add") }}
                </el-button>
              </div>

              <BaseTable
                stripe
                :data="secondTableData"
                :columns="secondColumns"
                columns-name="secondColumns"
                :empty-text="$t('priceModel.costElement.noData')"
                border
                @deleteAttr2="deleteAttr2"
              >
                <!-- appCondName(编辑应用条件) -->
                <template #appCondName="scope">
                  <span
                    v-if="scope.row.appCondName"
                    class="edit_cond"
                    @click="editCond(scope)"
                  >
                    {{ secondTableData[scope.$index].appCondName }}
                  </span>
                  <span
                    v-else
                    class="edit_cond"
                    @click="editCond(scope)"
                  >
                    {{ $t("priceModel.costElement.editCond") }}
                  </span>
                </template>

                <!-- formula -->
                <template #formulaHeader="scope">
                  <em class="toRequired">*</em>{{ scope.column.label }}
                  <el-tooltip
                    :content="$t('priceModel.costElement.formulaTipInfo')"
                    placement="top"
                  >
                    <em class="el-icon-info" />
                  </el-tooltip>
                </template>
                <template #formula="scope">
                  <el-input
                    v-model="secondTableData[scope.$index].formula"
                    :disabled="readOnly"
                    maxlength="250"
                    show-word-limit
                  >
                    <template slot="prepend">
                      {{ $t("priceModel.costElement.formulaText") }}
                    </template>
                  </el-input>
                </template>
              </BaseTable>
            </el-tab-pane>

            <!--价格公式-->
            <el-tab-pane
              :label="$t('priceModel.costElement.third')"
              name="third"
            >
              <div
                v-if="!readOnly"
                style="margin: 15px 0;"
              >
                <el-button
                  type="primary"
                  class="detail-pbtn"
                  @click="addPrice"
                >
                  {{ $t("common.add") }}
                </el-button>
              </div>

              <BaseTable
                stripe
                :data="thirdTableData"
                :columns="thirdColumns"
                columns-name="thirdColumns"
                :empty-text="$t('priceModel.costElement.noData')"
                border
                @deleteAttr3="deleteAttr3"
              >
                <!-- appCondName -->
                <template #appCondName="scope">
                  <span
                    v-if="scope.row.appCondName"
                    class="edit_cond"
                    @click="editCond(scope, 'price')"
                  >
                    {{ thirdTableData[scope.$index].appCondName }}
                  </span>
                  <span
                    v-else
                    class="edit_cond"
                    @click="editCond(scope, 'price')"
                  >{{ $t("priceModel.costElement.editCond") }}</span>
                </template>

                <!-- formula -->
                <template #formulaHeader="scope">
                  <em class="toRequired">*</em>{{ scope.column.label }}
                  <el-tooltip
                    :content="$t('priceModel.costElement.priceTipInfo')"
                    placement="top"
                  >
                    <em class="el-icon-info" />
                  </el-tooltip>
                </template>

                <template #formula="scope">
                  <el-input
                    v-model="thirdTableData[scope.$index].formula"
                    :disabled="readOnly"
                    maxlength="250"
                    show-word-limit
                  >
                    <template slot="prepend">
                      {{ $t("priceModel.costElement.priceText") }}
                    </template>
                  </el-input>
                </template>

                <!-- remark -->
                <template #remark="scope">
                  <el-input
                    v-model="thirdTableData[scope.$index].remark"
                    :disabled="readOnly"
                    maxlength="250"
                    show-word-limit
                  />
                </template>
              </BaseTable>
            </el-tab-pane>

            <!--基价-->
            <el-tab-pane
              :label="$t('priceModel.costElement.fourth')"
              name="fourth"
            >
              <BaseTable
                stripe
                :data="fourthTableData"
                :columns="fourthColumns"
                columns-name="fourthColumns"
                :empty-text="$t('priceModel.costElement.noData')"
                border
                style="margin-top: 10px"
              >
                <!-- basePrice -->
                <template #basePrice="scope">
                  <el-input
                    v-model.trim="fourthTableData[scope.$index].basePrice"
                    :disabled="readOnly"
                    type="number"
                  />
                </template>

                <!-- clearCurrency -->
                <template #clearCurrency="scope">
                  <DictSelect
                    v-model="fourthTableData[scope.$index].clearCurrency"
                    code="currency"
                    :disabled="readOnly"
                  />
                </template>

                <!-- startDate -->
                <template #startDate="scope">
                  <el-date-picker
                    v-model="fourthTableData[scope.$index].startDate"
                    :disabled="readOnly"
                    type="date"
                    :format="$formatDatePicker"
                    value-format="yyyy-MM-dd"
                  />
                </template>

                <!-- endDate -->
                <template #endDate="scope">
                  <el-date-picker
                    v-model="fourthTableData[scope.$index].endDate"
                    :disabled="readOnly"
                    type="date"
                    :format="$formatDatePicker"
                    value-format="yyyy-MM-dd"
                  />
                </template>
              </BaseTable>
            </el-tab-pane>
          </el-tabs>
        </el-collapse-item>
      </el-collapse>

      <!-- 编辑应用条件 -->
      <srm-dialog
        :visible.sync="editCondVisible"
        :title="$t('priceModel.costElement.editCondTitle')"
        size="large"
      >
        <div style="margin: 15px 0;">
          <el-button
            type="primary"
            class="detail-pbtn"
            @click="addCond"
          >
            {{ $t("common.add") }}
          </el-button>
        </div>
        <!-- 编辑应用条件 -->
        <BaseTable
          stripe
          :data="condTableData"
          :columns="condColumns"
          columns-name="condColumns"
          :empty-text="$t('priceModel.costElement.noData')"
          border
          @deleteCond="deleteCond"
        >
          <!-- 左括号 -->
          <template #leftBracket="scope">
            <DictSelect
              v-model="condTableData[scope.$index].leftBracket"
              code="leftBracket"
              clearable
              :dict-class="dictClass"
            />
          </template>
          <!-- 属性名称 -->
          <template #attributeNameHeader="scope">
            <em class="toRequired">*</em>{{ scope.column.label }}
          </template>
          <template #attributeName="scope">
            <el-select
              v-model="condTableData[scope.$index].attributeName"
              clearable
            >
              <el-option
                v-for="item in attributeNameList"
                :key="item.id"
                :value="item.value"
                :label="item.label"
              />
            </el-select>
          </template>
          <!-- 比较符 -->
          <template #comparisonOperatorsHeader="scope">
            <em class="toRequired">*</em>{{ scope.column.label }}
          </template>
          <template #comparisonOperators="scope">
            <el-select
              v-model="condTableData[scope.$index].comparisonOperators"
              clearable
            >
              <el-option
                v-for="item in comparisonOperators[scope.row.attributeName]"
                :key="item.id"
                :value="item.value"
                :label="item.label"
              />
            </el-select>
          </template>
          <!-- 属性值 -->
          <template #attributeValueHeader="scope">
            <em class="toRequired">*</em>{{ scope.column.label }}
          </template>

          <template #attributeValue="scope">
            <el-select
              v-if="!!attributeValueMap[scope.row.attributeName]"
              v-model="condTableData[scope.$index].attributeValue"
              clearable
            >
              <el-option
                v-for="item in attributeValueMap[scope.row.attributeName]"
                :key="item.id"
                :value="item.value"
                :label="item.label"
              />
            </el-select>
            <el-input
              v-else
              v-model="condTableData[scope.$index].attributeValue"
            />
          </template>

          <!-- 右括号 -->
          <template #rightBracket="scope">
            <DictSelect
              v-model="condTableData[scope.$index].rightBracket"
              code="rightBracket"
              clearable
              :dict-class="dictClass"
            />
          </template>

          <!-- 连接符 -->
          <template #connector="scope">
            <DictSelect
              v-model="condTableData[scope.$index].connector"
              code="connector"
              clearable
              :dict-class="dictClass"
            />
          </template>
        </BaseTable>

        <div style="margin: 10px 0;">
          {{ $t("priceModel.costElement.condDesc") }}
        </div>
        <el-input
          v-model="condDesc"
          type="textarea"
          disabled
        />
        <template
          #footer
          class="dialog-footer"
        >
          <el-button @click="editCondVisible = false">
            {{ $t("common.cancel") }}
          </el-button>
          <el-button
            type="primary"
            @click="saveCond"
          >
            {{ $t("common.submit") }}
          </el-button>
        </template>
      </srm-dialog>

      <CToolbar>
        <template #right>
          <el-button
            @click="cancelBill"
          >
            {{ readOnly ? '关闭' : '取消' }}
          </el-button>
          <el-button
            v-if="!!!mergeForm.costElementId || mergeForm.status === 'DRAFT'"
            @click="tempSave"
          >
            {{ $t('common.staging') }}
          </el-button>
          <el-button
            type="primary"
            :disabled="readOnly || mergeForm.status === 'VALID' || mergeForm.status === 'INVALID'"
            @click="submit"
          >
            {{ $t("orderMod.buyerOrderSynergy.confirm") }}
          </el-button>
        </template>
      </CToolbar>
    </el-main>
  </el-container>
</template>

<script>
import { tabTodoMixin } from '@/utils/mixins'
import { createDictClass } from '@/library/utils/dict/dict-utils'
import CToolbar from 'lib@/components/c-toolbar'
import BaseTable from 'lib@/components/BaseTable'
import BaseInfo from './costElementEdit/baseInfo'
import { costElement } from 'modb@/priceModel/api'

export default {
  name: 'CostElementEdit',
  components: {
    CToolbar,
    BaseTable,
    BaseInfo
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      dictClass: createDictClass({
        'leftBracket': [{ id: 1, label: '(', value: '(' }],
        'comparisonOperators': [
          { id: 1, value: this.$t('components.condition.gt'), label: this.$t('components.condition.gt') },
          { id: 2, value: this.$t('components.condition.lt'), label: this.$t('components.condition.lt') },
          { id: 3, value: this.$t('components.condition.eq'), label: this.$t('components.condition.eq') },
          { id: 4, value: this.$t('components.condition.ne'), label: this.$t('components.condition.ne') },
          { id: 5, value: this.$t('components.condition.ge'), label: this.$t('components.condition.ge') },
          { id: 6, value: this.$t('components.condition.le'), label: this.$t('components.condition.le') }
        ],
        'rightBracket': [{ id: 1, label: ')', value: ')' }],
        'connector': [
          { id: 1, label: this.$t('components.condition.or'), value: this.$t('components.condition.or') },
          { id: 2, label: this.$t('components.condition.and'), value: this.$t('components.condition.and') }
        ]
      }, false),
      form: {},
      activeName: 'first',
      // 应用条件列表数据
      condTableData: [],
      // 应用条件列表定义
      condColumns: [
        {// 左括号
          attrs: {
            align: 'center',
            minWidth: '100',
            label: t => t.$t('priceModel.costElement.leftBracket'),
            prop: 'leftBracket'
          },
          slot: 'leftBracket'
        },
        {// 属性名称
          attrs: {
            align: 'center',
            minWidth: '100',
            label: t => t.$t('priceModel.costElement.attributeName'),
            prop: 'attributeName'
          },
          headerSlot: 'attributeNameHeader',
          slot: 'attributeName'
        },
        {// 操作符
          attrs: {
            align: 'center',
            minWidth: '100',
            label: t => t.$t('priceModel.costElement.comparisonOperators'),
            prop: 'comparisonOperators'
          },
          slot: 'comparisonOperators',
          headerSlot: 'comparisonOperatorsHeader'
        },
        {// 属性值
          attrs: {
            align: 'center',
            minWidth: '100',
            label: t => t.$t('priceModel.costElement.attributeValue'),
            prop: 'attributeValue'
          },
          slot: 'attributeValue',
          headerSlot: 'attributeValueHeader'
        },
        {// 右括号
          attrs: {
            align: 'center',
            minWidth: '100',
            label: t => t.$t('priceModel.costElement.rightBracket'),
            prop: 'rightBracket'
          },
          slot: 'rightBracket'
        },
        {// 连接符
          attrs: {
            align: 'center',
            minWidth: '100',
            label: t => t.$t('priceModel.costElement.connector'),
            prop: 'connector'
          },
          slot: 'connector'
        },
        {// 操作
          attrs: {
            align: 'center',
            label: t => t.$t('common.operation'),
            fixed: 'right',
            width: 80
          },
          operations: [
            {// 删除
              key: 'deleteItem',
              event: 'deleteCond',
              name: this.$t('common.delete'),
              show: () => !this.readOnly,
              attrs: { type: 'text' }
            }
          ]
        }
      ],
      // 要素属性列表数据
      firstTableData: [],
      // 要素属性列表定义
      firstColumns: [
        // 序号
        {
          attrs: {
            align: 'center',
            type: 'index',
            width: 70,
            label: t => t.$t('priceModel.costElement.sequenceFlag')
          }
        },
        // 属性名称
        {
          attrs: {
            align: 'center',
            width: 170,
            label: t => t.$t('priceModel.costElement.attributeName'),
            prop: 'attributeName'
          },
          slot: 'attributeName',
          headerSlot: 'attributeNameHeader'
        },
        // 属性单位
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: t => t.$t('priceModel.costElement.attributeUnit'),
            prop: 'attributeUnit'
          },
          slot: 'attributeUnit'
        },
        // 属性类型
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: t => t.$t('priceModel.costElement.attributeType'),
            prop: 'attributeType'
          },
          slot: 'attributeType',
          headerSlot: 'attributeTypeHeader'
        },
        // 属性值
        {
          attrs: {
            align: 'center',
            width: 200,
            label: t => t.$t('priceModel.costElement.attributeValue'),
            prop: 'attributeValue'
          },
          slot: 'attributeValue',
          headerSlot: 'attributeValueHeader'
        },
        // 关键属性
        {
          attrs: {
            align: 'center',
            width: 80,
            label: t => t.$t('priceModel.costElement.crucialFlag'),
            prop: 'crucialFlag'
          },
          slot: 'crucialFlag'
        },
        // 必填
        {
          attrs: {
            align: 'center',
            width: 80,
            label: t => t.$t('priceModel.costElement.requiredFlag'),
            prop: 'requiredFlag'
          },
          slot: 'requiredFlag'
        },
        // 禁用
        {
          attrs: {
            align: 'center',
            width: 80,
            label: t => t.$t('priceModel.costElement.disableFlag'),
            prop: 'disableFlag'
          },
          slot: 'disableFlag'
        },
        // 操作
        {
          attrs: {
            align: 'center',
            label: t => t.$t('common.operation'),
            fixed: 'right',
            minWidth: 150
          },
          operations: [
            // 上移
            {
              key: 'moveUp',
              event: 'moveUp',
              show: scope => scope.$index !== 0 && !this.readOnly,
              name: this.$t('priceModel.costElement.moveUp'),
              attrs: { type: 'text' }
            },
            // 下移
            {
              key: 'moveDown',
              event: 'moveDown',
              show: scope =>
                this.firstTableData.length !== scope.$index + 1 &&
                !this.readOnly,
              name: this.$t('priceModel.costElement.moveDown'),
              attrs: { type: 'text' }
            },
            // 删除
            {
              key: 'deleteAttr',
              event: 'deleteAttr',
              name: this.$t('common.delete'),
              show: () => !this.readOnly,
              attrs: { type: 'text' }
            }
          ]
        }
      ],
      // 应用公式列表数据
      secondTableData: [],
      // 应用公式列表定义
      secondColumns: [
        // 序号
        {
          attrs: {
            align: 'center',
            type: 'index',
            width: 70,
            label: t => t.$t('priceModel.costElement.sequenceFlag')
          }
        },
        // 应用条件
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: t => t.$t('priceModel.costElement.appCondName'),
            prop: 'appCondName'
          },
          slot: 'appCondName'
        },
        // 用量公式
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: t => t.$t('priceModel.costElement.formula'),
            prop: 'formula'
          },
          slot: 'formula',
          headerSlot: 'formulaHeader'
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
              key: 'deleteAttr2',
              event: 'deleteAttr2',
              name: this.$t('common.delete'),
              show: () => !this.readOnly,
              attrs: { type: 'text' }
            }
          ]
        }
      ],
      // 价格公式列表数据
      thirdTableData: [],
      // 价格公式列表定义
      thirdColumns: [
        {// 序号
          attrs: {
            align: 'center',
            type: 'index',
            width: 70,
            label: t => t.$t('priceModel.costElement.sequenceFlag')
          }
        },
        {// 应用条件
          attrs: {
            align: 'center',
            minWidth: '100',
            label: t => t.$t('priceModel.costElement.appCondName'),
            prop: 'appCondName'
          },
          slot: 'appCondName'
        },
        {// 价格公式
          attrs: {
            align: 'center',
            minWidth: '100',
            label: t => t.$t('priceModel.costElement.priceFormula'),
            prop: 'formula'
          },
          slot: 'formula',
          headerSlot: 'formulaHeader'
        },
        {// 备注
          attrs: {
            align: 'center',
            minWidth: '100',
            label: t => t.$t('priceModel.costElement.remark'),
            prop: 'remark'
          },
          slot: 'remark'
        },
        {// 操作
          attrs: {
            align: 'center',
            label: t => t.$t('common.operation'),
            fixed: 'right',
            width: 80
          },
          operations: [
            {
              key: 'deleteAttr3',
              event: 'deleteAttr3',
              name: this.$t('common.delete'),
              show: () => !this.readOnly,
              attrs: { type: 'text' }
            }
          ]
        }
      ],
      // 基价列表数据
      fourthTableData: [],
      // 基价列表定义
      fourthColumns: [
        // 序号
        {
          attrs: {
            align: 'center',
            type: 'index',
            width: 70,
            label: t => t.$t('priceModel.costElement.sequenceFlag')
          }
        },
        // 组合编码
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: t => t.$t('priceModel.costElement.combinationCode'),
            prop: 'combinationCode'
          }
        },
        // 关键属性组合
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: t => t.$t('priceModel.costElement.keyAttributeCombination'),
            prop: 'keyAttributeCombination'
          }
        },
        // 属性组合值
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: t =>
              t.$t('priceModel.costElement.attributeValueCombination'),
            prop: 'attributeValueCombination'
          }
        },
        // 基价
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: t => t.$t('priceModel.costElement.basePrice'),
            prop: 'basePrice'
          },
          slot: 'basePrice'
        },
        // 币种
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: t => t.$t('priceModel.costElement.clearCurrency'),
            prop: 'clearCurrency'
          },
          slot: 'clearCurrency'
        },
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: t => t.$t('priceModel.costElement.startDate'),
            prop: 'startDate'
          },
          slot: 'startDate'
        },
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: t => t.$t('priceModel.costElement.endDate'),
            prop: 'endDate'
          },
          slot: 'endDate'
        }
      ],
      // 费率公式列表数据
      feeCalcTableData: [],
      // 费率公式列表定义
      feeCalcColumns: [
        // 计算基准
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: t => t.$t('priceModel.costElement.calculationBasis'),
            prop: 'calculationBasis'
          },
          slot: 'calculationBasis'
        },
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: t => t.$t('priceModel.costElement.rate'),
            prop: 'rate'
          },
          slot: 'rate'
        },
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: t => t.$t('priceModel.costElement.calculationFormula'),
            prop: 'calculationFormula'
          }
        }
      ],
      editCondVisible: false,
      currentCondType: null,
      currentCondIndex: null,
      // 是否展示费率公式信息
      showFeeCalc: false,
      readOnly: false,
      activeDims: ['1', '2'],
      // 成本要素信息
      mergeForm: {
        elementCode: '',
        elementName: '',
        calculation: '',
        enableCommon: '',
        orgId: '',
        creationDate: '',
        status: '',
        unit: ''
      }
    }
  },
  computed: {
    condDesc () {
      return this.condTableData.reduce((str, item) => {
        let attrValue
        if (item.attributeValue) {
          attrValue = String(item.attributeValue).trim()
        } else {
          attrValue = ''
        }

        if (attrValue.length > 0) {
          const isNumber = !isNaN(+attrValue)
          attrValue = isNumber ? Number(attrValue) : '\'' + attrValue + '\''
        }

        let res = `${item.leftBracket || ' '} ` +
          `${item.attributeName || ' '} ` +
          `${item.comparisonOperators || ' '} ` +
          `${attrValue || ' '} ` +
          `${item.rightBracket || ' '} ` +
          `${item.connector || ' '}`

        str += res + ' '
        return str
      }, '')
    },

    appCondCode () {
      const sort = [
        'leftBracket',
        'attributeName',
        'comparisonOperators',
        'attributeValue',
        'rightBracket',
        'connector'
      ]
      const result = this.condTableData.reduce((str, item) => {
        const res = sort.map(key => {
          const compMap = new Map()
          compMap.set(this.$t('components.condition.eq'), '==')
          compMap.set(this.$t('components.condition.ne'), '!=')
          compMap.set(this.$t('components.condition.gt'), '>')
          compMap.set(this.$t('components.condition.lt'), '<')
          compMap.set(this.$t('components.condition.le'), '<=')
          compMap.set(this.$t('components.condition.ge'), '>=')
          compMap.set(this.$t('components.condition.and'), '&&')
          compMap.set(this.$t('components.condition.or'), '||')
          if (key === 'comparisonOperators') {
            return compMap.get(item[key])
          }
          if (key === 'connector') {
            return compMap.get(item[key])
          }
          if (key === 'attributeValue' && item[key] !== '') {
            if (item[key]) {
              const isNumber = !isNaN(+item[key])
              return isNumber ? +item[key] : '\'' + item[key] + '\''
            } else {
              return ' '
            }
          }
          return item[key] || ' '
        })
        str += res.join(' ')
        return str
      }, '')
      return result
    },

    attributeNameList () {
      const result = []
      this.firstTableData.forEach(({ attributeName, attributeType }) => {
        result.push({
          label: `[${attributeName}]`,
          value: `[${attributeName}]`
        })
      })
      return result
    },

    comparisonOperators () {
      const result = {}
      this.firstTableData.forEach(({ attributeName, attributeType }) => {
        const fields = [
          this.$t('components.condition.eq'),
          this.$t('components.condition.ne')
        ]
        if (['STRING', 'ENUM'].includes(attributeType)) {
          result[`[${attributeName}]`] = this.dictClass.getDict('comparisonOperators')
            .filter(i => fields.includes(i.value))
        } else {
          result[`[${attributeName}]`] = this.dictClass.getDict(
            'comparisonOperators'
          )
        }
      })
      return result
    },

    attributeValueMap () {
      const result = {}
      this.firstTableData.forEach(
        ({ attributeName, attributeValue = '', attributeType }) => {
          if (attributeType === 'ENUM') {
            const list = attributeValue.split(',') || []
            result[`[${attributeName}]`] = list.map(i => ({
              label: i,
              value: i
            }))
          } else {
            result[`[${attributeName}]`] = false
          }
        }
      )
      return result
    }
  },

  watch: {
    firstTableData: {
      handler () {
        if (!this.readOnly && this.firstTableData.length) {
          let keyAttributeCombination = ''
          const list = this.firstTableData
            .filter(i => i.crucialFlag === 'Y')
            .reduce((arr, item) => {
              const arrs = (item.attributeValue || '').split(',')
              arr.push(arrs.map(j => `[${j}]`))
              keyAttributeCombination += `[${item.attributeName}]`
              return arr
            }, [])
          const doExchange = arr => {
            const len = arr.length
            // 当数组大于等于2个的时候
            if (len >= 2) {
              // 第一个数组的长度
              const len1 = arr[0].length
              // 第二个数组的长度
              const len2 = arr[1].length
              // 2个数组产生的组合数
              const lenBoth = len1 * len2
              //  申明一个新数组,做数据暂存
              const items = new Array(lenBoth)
              // 申明新数组的索引
              let index = 0
              // 2层嵌套循环,将组合放到新数组中
              for (let i = 0; i < len1; i++) {
                for (let j = 0; j < len2; j++) {
                  items[index] = arr[0][i] + arr[1][j]
                  index++
                }
              }
              // 将新组合的数组并到原数组中
              const newArr = new Array(len - 1)
              for (let i = 2; i < arr.length; i++) {
                newArr[i - 1] = arr[i]
              }
              newArr[0] = items
              // 执行回调
              return doExchange(newArr)
            } else {
              return arr[0]
            }
          }
          const resultList = doExchange(list)
          console.time('auto generate attribute combine')
          const resultItems = [];
          (resultList || []).forEach(i => {
            const target = this.fourthTableData.find(
              j => j.attributeValueCombination === i
            )
            if (target) {
              target.keyAttributeCombination = keyAttributeCombination
              resultItems.push(target)
            } else {
              resultItems.push({
                keyAttributeCombination: keyAttributeCombination,
                attributeValueCombination: i,
                // 默认人民币
                clearCurrency: 'CNY',
                startDate: this.$dayjs().format('YYYY-MM-DD')
              })
            }
          })
          this.fourthTableData = resultItems
          console.timeEnd('auto generate attribute combine')
        }
      },
      deep: true
    }
  },

  mounted () {
    const { flag, row, readOnly = false } = this.$attrs.params
    this.readOnly = readOnly
    if (flag === 'edit' || flag === 'createNew') {
      // 如果是通过"编辑"、"创建新版本"打开的页面，就根据ID加载数据
      costElement.get(row.costElementId).then(res => {
        const {
          // 基价信息
          basePriceList,
          // 用量公式信息
          useFormulaList,
          // 要素属性信息
          featureAttributeList,
          // 价格公式信息
          priceFormulaList,
          // 费率公式信息
          rateCalculation,
          // 成本要是信息
          costElement = {}
        } = res.data || {}
        // 基价信息
        this.fourthTableData = basePriceList
        // 要素属性列表排序
        this.firstTableData = featureAttributeList.sort((a, b) => a.sequenceFlag - b.sequenceFlag)
        this.secondTableData = useFormulaList // 用量公式信息
        this.thirdTableData = priceFormulaList // 价格公式信息

        this.mergeForm = JSON.parse(JSON.stringify(costElement))

        if (costElement.calculation === 'CALCULATED_BY_RATE' && costElement.elementType === 'FEE') {
          this.feeCalcTableData = [{ ...rateCalculation, rate: rateCalculation.rate * 100 }]
          this.showFeeCalc = true
          this.calcCalculationFormula()
        }
      })
    } else {
      this.feeCalcTableData = [{}]
    }
  },
  methods: {
    deleteCond (scope) {
      this.condTableData.splice(scope.$index, 1)
    },

    formatAttributeVlaue (scope) {
      if (scope.row.attributeType === 'ENUM') {
        const formatValue = scope.row.attributeValue.replace(/，/g, ',')
        this.$set(
          this.firstTableData[scope.$index],
          'attributeValue',
          formatValue
        )
      }
    },

    attributeTypeChange (value, scope) {
      if (value !== 'ENUM') {
        scope.row.crucialFlag = 'N'
        this.$set(this.firstTableData[scope.$index], 'crucialFlag', 'N')
      }
    },

    attributeValueChange (value, scope) {
      if (scope.row.attributeType === 'ENUM') {
        const formatValue = value.replace(/，/g, ',')
        const enumList = formatValue.split(',')
        const flag = enumList.some(i => !isNaN(+i))
        if (flag) {
          this.$message.error(this.$t('priceModel.costElement.enumErrorMsg'))
        }
      }
    },

    calcCalculationFormula () {
      const { rate, calculationBasis } = this.feeCalcTableData[0]
      const result = `${this.$t('priceModel.costElement.feeText')}(${this.$getDictLabel('RATE_CALCULATION_BASIS',
        calculationBasis
      )})*${rate}%`
      this.$set(this.feeCalcTableData[0], 'calculationFormula', result)
    },

    saveCond () {
      // 校验左右括号合法
      let bracketCount = 0
      let e
      for (let i in this.condTableData) {
        e = this.condTableData[i]
        // 确保必填的都填写了
        if (!e.attributeName || String(e.attributeName).trim().length <= 0) {
          this.$message.warning(this.$t('dataConfMod.selectPropertyName'))
          return
        }
        if (!e.comparisonOperators || String(e.comparisonOperators).trim().length <= 0) {
          this.$message.warning(this.$t('dataConfMod.selectComparator'))
          return
        }
        if (!e.attributeValue || (e.attributeValue = (e.attributeValue + '').trim()).length <= 0) {
          this.$message.warning(this.$t('dataConfMod.enterPropertyValue'))
          return
        }

        if (e.leftBracket === '(') {
          bracketCount++
        }
        if (e.rightBracket === ')') {
          if (bracketCount > 0) {
            bracketCount--
          }
        }
      }
      if (bracketCount > 0) {
        this.$message.warning(this.$t('dataConfMod.notCompleteFormula'))
        return
      }
      const table =
        this.currentCondType === 'dosage'
          ? 'secondTableData'
          : 'thirdTableData'
      this.$set(
        this[table][this.currentCondIndex],
        'appCondName',
        this.condDesc
      )
      this.$set(
        this[table][this.currentCondIndex],
        'appCondCode',
        this.appCondCode
      )
      this.editCondVisible = false
    },

    addPrice () {
      if (this.thirdTableData.length === 0) {
        this.thirdTableData.push({
          appCondName: ''
        })
      } else {
        // 替换应用条件中的空格之后再判断
        const appCondName = this.thirdTableData[0].appCondName.replace(
          /\s/g,
          ''
        )
        const isApplyAll = !appCondName
        if (isApplyAll) {
          this.$message.warning(this.$t('priceModel.costElement.applyAllMsg'))
        } else {
          this.thirdTableData.push({})
        }
      }
    },

    addCond () {
      // 添加公式表格数据 一行
      this.condTableData.push({})
    },

    // 编辑应用条件
    editCond (scope, type = 'dosage') {
      if (this.readOnly) return

      this.currentCondIndex = scope.$index
      this.currentCondType = type

      // 解析应用公式为表格数据
      const condName = scope.row.appCondName || ''
      const condArr = condName.split(/(或者|并且)/g)

      if (condArr.length > 1) {
        const list = []
        condArr.forEach((item, index) => {
          if (index % 2 !== 0) return
          if (item.trim().length <= 0) return
          const o = {}
          const l = item.split(/\s/g)
          l.forEach((j, _i) => {
            if (/\[(.*?)\]/g.exec(j)) o.attributeName = j
            const comp = this.dictClass.getDict('comparisonOperators')
              .map(i => i.label)
              .includes(j)
            if (comp) {
              o.attributeValue = l[_i + 1] || null
              if (/'(.*?)'/g.exec(o.attributeValue)) {
                o.attributeValue = o.attributeValue.substring(1, o.attributeValue.length - 1)
              }
              o.comparisonOperators = j
            }
          })
          o.leftBracket = item.indexOf('(') > -1 ? '(' : ' '
          o.rightBracket = item.indexOf(')') > -1 ? ')' : ' '
          o.connector = condArr[index + 1] || ' '
          list.push(o)
        })
        this.condTableData = list
      } else if (condArr.length === 1) {
        const o = {}
        const elementArr = condArr[0].split(/\s/g)
        elementArr.forEach((element, index) => {
          if (/\[(.*?)\]/g.exec(element)) o.attributeName = element
          // 获取比较运算符
          const comp = this.dictClass.getDict('comparisonOperators')
            .map(i => i.label)
            .includes(element)
          if (comp) {
            o.attributeValue = elementArr[index + 1] || null
            if (/'(.*?)'/g.exec(o.attributeValue)) {
              o.attributeValue = o.attributeValue.substring(1, o.attributeValue.length - 1)
            }
            o.comparisonOperators = element
          }
        })
        o.leftBracket = condArr[0].indexOf('(') > -1 ? '(' : ' '
        o.rightBracket = condArr[0].indexOf(')') > -1 ? ')' : ' '
        this.condTableData = [o]
      } else {
        this.condTableData = []
      }
      this.editCondVisible = true
    },

    addFormula () {
      if (this.secondTableData.length === 0) {
        this.secondTableData.push({
          appCondName: ''
        })
      } else {
        // 替换应用条件中的空格之后再判断
        const appCondName = this.secondTableData[0].appCondName.replace(
          /\s/g,
          ''
        )
        const isApplyAll = !appCondName
        if (isApplyAll) {
          this.$message.warning(this.$t('priceModel.costElement.applyAllMsg'))
        } else {
          this.secondTableData.push({})
        }
      }
    },

    addAttr () {
      this.firstTableData.push({})
    },

    deleteAttr (scope) {
      this.firstTableData.splice(scope.$index, 1)
    },

    deleteAttr2 (scope) {
      this.secondTableData.splice(scope.$index, 1)
    },

    deleteAttr3 (scope) {
      this.thirdTableData.splice(scope.$index, 1)
    },

    crucialFlagChange (value, scope) {
      if (value === 'Y') {
        this.$set(this.firstTableData[scope.$index], 'requiredFlag', 'Y')
      }
    },

    moveDown (scope) {
      const { $index } = scope
      if ($index === this.firstTableData.length - 1) return
      const temp1 = this.firstTableData[$index]
      const temp2 = this.firstTableData[$index + 1]
      this.$set(this.firstTableData, $index + 1, temp1)
      this.$set(this.firstTableData, $index, temp2)
    },

    moveUp (scope) {
      const { $index } = scope
      if ($index === 0) return
      const temp1 = this.firstTableData[$index]
      const temp2 = this.firstTableData[$index - 1]
      this.$set(this.firstTableData, $index - 1, temp1)
      this.$set(this.firstTableData, $index, temp2)
    },

    addOrgHandle (e, value, scope) {
      scope.orgId = e ? e.organizationId : ''
      scope.orgCode = e ? e.organizationCode : ''
      scope.orgName = e ? e.organizationName : ''
    },

    checkFormData () {
      return new Promise(resolve => {
        // 校验枚举值不能是数字或者空的字符串
        const enums = this.firstTableData.filter(
          i => i.attributeType === 'ENUM'
        )
        const error = enums.some(i => {
          if (
            i.attributeType === 'ENUM' &&
            i.attributeValue.indexOf(',') > -1
          ) {
            const enumList = i.attributeValue.split(',')
            return enumList.some(i => !isNaN(+i))
          }
          return false
        })
        if (error) {
          this.$message.error(this.$t('priceModel.costElement.enumErrorMsg'))
          return resolve(false)
        }
        // 校验价格公式是否填写
        const priceFormulaNotFill =
          !this.thirdTableData.length ||
          this.thirdTableData.some(i => !i.formula)
        if (priceFormulaNotFill) {
          this.$message.error(
            this.$t('priceModel.costElement.priceFormulaNotFill')
          )
          return resolve(false)
        }
        resolve(true)
      })
    },

    // 暂存
    async tempSave () {
      const validateResult = await this.$refs.baseInfo.validateForm()
      if (!validateResult) {
        return
      }

      const form = this.mergeForm
      const isFee = form.elementType === 'FEE' && form.calculation === 'CALCULATED_BY_RATE'

      let data = { ...form }
      // 如果计算方式是按照费率计算并且要素类型是费用 只需要上传费率计算表格
      if (isFee) {
        const tableData = this.feeCalcTableData.map(i => ({
          ...i,
          rate: i.rate / 100
        }))
        Object.assign(data, {
          rateCalculation: tableData[0]
        })
      } else {
        Object.assign(data, {
          priceFormulasList: this.thirdTableData,
          featureAttributeList: this.firstTableData
            .map((i, index) => ({
              ...i,
              sequenceFlag: index
            }))
            .map(({ requiredFlag, crucialFlag, disableFlag, ...rest }) => ({
              ...rest,
              requiredFlag: requiredFlag === 'Y' ? 'Y' : 'N',
              disableFlag: disableFlag === 'Y' ? 'Y' : 'N',
              crucialFlag: crucialFlag === 'Y' ? 'Y' : 'N'
            })),
          dosageFormulasList: this.secondTableData,
          basePricesList: this.fourthTableData
        })
      }

      let params = {
        costElement: data, // 成本要素信息
        rateCalculation: data.rateCalculation, // 费率信息
        featureAttributeList: data.featureAttributeList, // 要素属性集合
        useFormulaList: data.dosageFormulasList, // 用量公式集合
        priceFormulaList: data.priceFormulasList, // 价格公式集合
        basePriceList: data.basePricesList // 基价集合
      }

      costElement.tempSave(params).then(res => {
        this.$message({
          type: 'success',
          message: res.message
        })
        this.mergeForm = res.data
      })
    },

    // 生效
    async submit () {
      const validateResult = await this.$refs.baseInfo.validateForm()
      if (validateResult) {
        const form = this.mergeForm
        const isFee = form.elementType === 'FEE' && form.calculation === 'CALCULATED_BY_RATE'
        if (!isFee) {
          const checkResolve = await this.checkFormData()
          if (!checkResolve) return
          // 检查价格\用量公式中第一条是否填写应用条件 如果没有 就设置appCondCode为 1 == 1 默认全局使用一个公式
          const priceAppCondName = this.thirdTableData[0].appCondName.replace(
            /\s/g,
            ''
          )
          if (!priceAppCondName) {
            this.$set(this.thirdTableData[0], 'appCondName', '')
            this.$set(this.thirdTableData[0], 'appCondCode', '1 == 1')
          }
          if (this.secondTableData.length) {
            const formulaAppCondName = this.secondTableData[0].appCondName.replace(
              /\s/g,
              ''
            )
            if (!formulaAppCondName) {
              this.$set(this.secondTableData[0], 'appCondName', '')
              this.$set(this.secondTableData[0], 'appCondCode', '1 == 1')
            }
          }
        }

        let data = { ...form } // 搞这么多花里胡哨的写法
        // 如果计算方式是按照费率计算并且要素类型是费用 只需要上传费率计算表格
        if (isFee) {
          const tableData = this.feeCalcTableData.map(i => ({
            ...i,
            rate: i.rate / 100
          }))
          Object.assign(data, { rateCalculation: tableData[0] })
        } else {
          Object.assign(data, {
            priceFormulasList: this.thirdTableData,
            featureAttributeList: this.firstTableData
              .map((i, index) => ({
                ...i,
                sequenceFlag: index
              }))
              .map(({ requiredFlag, crucialFlag, disableFlag, ...rest }) => ({
                ...rest,
                requiredFlag: requiredFlag === 'Y' ? 'Y' : 'N',
                disableFlag: disableFlag === 'Y' ? 'Y' : 'N',
                crucialFlag: crucialFlag === 'Y' ? 'Y' : 'N'
              })),
            dosageFormulasList: this.secondTableData,
            basePricesList: this.fourthTableData
          })
        }

        let params = {
          costElement: data, // 成本要素信息
          rateCalculation: data.rateCalculation, // 费率信息
          featureAttributeList: data.featureAttributeList, // 要素属性集合
          useFormulaList: data.dosageFormulasList, // 用量公式集合
          priceFormulaList: data.priceFormulasList, // 价格公式集合
          basePriceList: data.basePricesList // 基价集合
        }

        const h = this.$createElement
        this.$msgbox({
          title: this.$t('dataConfMod.notice'),
          message: h('span', null, this.$t('dataConfMod.doNotEdit')),
          showCancelButton: true,
          confirmButtonText: this.$t('common.confirm'),
          cancelButtonText: this.$t('common.cancel'),
          beforeClose: (action, instance, done) => {
            if (action === 'confirm') {
              instance.confirmButtonLoading = true
              instance.confirmButtonText = this.$t('dataConfMod.inExecution')
              costElement.submit(params).then(res => {
                instance.confirmButtonLoading = false
                done()
                this.$message.success(this.$t('common.successSubmit'))
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
      }
    },

    cancelBill () {
      const { flag, row } = this.$attrs.params
      if (flag === 'add') {
        this.$emit('tab-remove', 'costElementEdit')
      } else {
        this.$emit('tab-remove', 'costElementEdit' + row.costElementId)
      }
      this.__setTabTodo('costElementList.getQueryData')
    }
  }
}
</script>

<style scoped lang="scss">
.costElementEdit {
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
