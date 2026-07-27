<template>
  <el-container
    class="riskEdit"
    direction="vertical"
  >
    <el-main>
      <BaseForm
        ref="form"
        class="base-form"
        form-name="risk-form"
        :form-items="formItems1"
        :merge-form.sync="mergeForm1"
        :inline="false"
        :status-icon="false"
        :show-message="true"
      />

      <el-collapse
        v-model="activeDims"
        class="tab-form-style"
      >
        <el-collapse-item
          :title="$t('supRisk.title1')"
          name="1"
        >
          <el-form
            ref="form2"
            :model="mergeForm2"
            class="base-form"
            :disabled="readOnly || ['update', 'close'].includes(editType)"
            :rules="rules"
          >
            <srm-row :gutter="32">
              <srm-col :init-col="4">
                <el-form-item :label="$t('supRisk.categoryId')">
                  <QuickSearch
                    :disabled="readOnly"
                    :show-input="mergeForm2.categoryName"
                    show-key="categoryName"
                    name="scc_base_purchase_category2"
                    @close-quicksearch="getCategory"
                  />
                </el-form-item>
              </srm-col>
              <srm-col :init-col="4">
                <el-form-item :label="$t('supRisk.vendorId')" prop="vendorName">
                  <QuickSearch
                    :disabled="readOnly"
                    :show-input="mergeForm2.vendorName"
                    show-key="companyName"
                    name="scc_sup_company_info"
                    @close-quicksearch="getVendor"
                  />
                </el-form-item>
              </srm-col>
              <srm-col :init-col="4">
                <el-form-item :label="$t('supRisk.riskDescription')">
                  <el-input v-model="mergeForm2.riskDescription" />
                </el-form-item>
              </srm-col>
            </srm-row>
          </el-form>
        </el-collapse-item>

        <el-collapse-item
          :title="$t('supRisk.title2')"
          name="2"
        >
          <el-alert
            title=""
            type="info"
            show-icon
            :closable="false"
            @click="showRule"
          >
            <div
              class="rule"
              @click="showRule"
            >
              {{ $t("supRisk.alertTitle") }}
            </div>
          </el-alert>
          <BaseForm
            ref="form"
            class="base-form"
            :form-items="formItems3"
            :merge-form.sync="mergeForm3"
            :inline="false"
            :status-icon="false"
            :show-message="true"
            :disabled="readOnly"
          />
        </el-collapse-item>

        <el-collapse-item
          :title="$t('supRisk.title3')"
          name="3"
        >
          <div
            v-if="!readOnly"
            style="margin-bottom: 10px;"
          >
            <el-button
              type="primary"
              class="detail-pbtn"
              @click="addFormula"
            >
              {{
                $t("common.add")
              }}
            </el-button>
            <el-button
              v-if="mergeForm1.status !== 'ADD'"
              type="primary"
              class="detail-pbtn"
              @click="showLastHandle"
            >
              {{ $t("supRisk.showLastHandle") }}
            </el-button>
          </div>
          <BaseTable
            stripe
            :data="tableData"
            :columns="columns"
            :empty-text="$t('components.noData')"
            border
            @deleteAttr2="deleteAttr2"
          >
            <!-- responsesType -->
            <template #responsesType="scope">
              <DictSelect
                v-model="tableData[scope.$index].responsesType"
                code="RESPONSES_TYPE"
                :disabled="readOnly"
              />
            </template>
            <!-- completeDate -->
            <template #completeDate="scope">
              <el-date-picker
                v-model="tableData[scope.$index].completeDate"
                :format="$formatDatePicker"
                value-format="yyyy-MM-dd"
                :disabled="readOnly"
              />
            </template>
            <!-- responseMeasures -->
            <template #responseMeasures="scope">
              <el-input
                v-model="tableData[scope.$index].responseMeasures"
                :disabled="readOnly"
              />
            </template>
            <!-- fileUploadId -->
            <template #fileuploadId="scope">
              <SrmCommonFile
                :extra-data="fileInfo"
                :default-file="{
                  fileId: scope.row.fileuploadId,
                  fileName: scope.row.fileName
                }"
                :readonly="readOnly"
                @on-change="({file}) => outerHandleUploadSuccess(file,scope)"
              />
            </template>
          </BaseTable>
        </el-collapse-item>

        <el-collapse-item
          :title="$t('supRisk.title4')"
          name="4"
        >
          <el-checkbox-group v-model="checkList">
            <el-checkbox
              v-for="item in flowList"
              :key="item.value"
              :label="item.value"
              :value="item.value"
              :disabled="readOnly"
            >
              {{ item.label }}
            </el-checkbox>
            <!--            &lt;!&ndash; 寻源阶段不能投标 &ndash;&gt;-->
            <!--            <el-checkbox :label="$t('supRisk.checkbox1')" />-->
            <!--            &lt;!&ndash; 寻源阶段不能中标 &ndash;&gt;-->
            <!--            <el-checkbox :label="$t('supRisk.checkbox2')" />-->
            <!--            &lt;!&ndash; 停止新订单 &ndash;&gt;-->
            <!--            <el-checkbox :label="$t('supRisk.checkbox3')" />-->
            <!--            &lt;!&ndash; 停止付款（含预付） &ndash;&gt;-->
            <!--            <el-checkbox :label="$t('supRisk.checkbox4')" />-->
          </el-checkbox-group>
        </el-collapse-item>
        <el-collapse-item
          :title="$t('supRisk.title5')"
          name="4"
        >
          <BaseForm
            ref="form"
            class="base-form"
            :form-items="formItems4"
            :merge-form.sync="mergeForm4"
            :disabled="readOnly"
            :inline="false"
            :status-icon="false"
            :show-message="true"
          />
        </el-collapse-item>
      </el-collapse>

      <el-dialog
        :visible.sync="ruleVisible"
        :title="$t('supRisk.ruleTitle')"
        width="1000px"
      >
        <el-collapse
          v-model="ruleActiveDims"
          class="tab-form-style"
        >
          <el-collapse-item
            :title="$t('supRisk.rule1')"
            name="1"
          >
            <BaseTable
              class="sup-risk-rule"
              stripe
              :data="rule1"
              :columns="columns1"
              columns-name="columns1"
              :empty-text="$t('components.noData')"
              border
            />
          </el-collapse-item>
          <el-collapse-item
            :title="$t('supRisk.rule2')"
            name="2"
          >
            <BaseTable
              class="sup-risk-rule"
              stripe
              :data="rule2"
              :columns="columns2"
              :span-method="objectSpanMethod"
              columns-name="columns2"
              :empty-text="$t('components.noData')"
              border
            />
          </el-collapse-item>
          <el-collapse-item
            :title="$t('supRisk.rule3')"
            name="3"
          >
            <BaseTable
              stripe
              class="sup-risk-rule"
              :data="rule3"
              :columns="columns3"
              columns-name="columns3"
              :empty-text="$t('components.noData')"
              border
            />
          </el-collapse-item>
        </el-collapse>
      </el-dialog>

      <el-dialog
        :visible.sync="lastHandleVisible"
        :title="$t('supRisk.showLastHandle')"
        width="1000px"
      >
        <BaseTable
          stripe
          :data="lastHandle"
          :columns="lastHandleColumns"
          columns-name="lastHandleColumns"
          :empty-text="$t('components.noData')"
          border
        >
          <!-- fileUploadId -->
          <template #fileuploadId="scope">
            <div
              v-if="scope.row.fileuploadId"
              class="download-link-wrap"
            >
              <SrmCommonFile
                :default-file="{
                  fileId: scope.row.fileuploadId,
                  fileName: scope.row.fileName
                }"
                :readonly="true"
              />
            </div>
            <span v-else>
              <!-- 无附件 -->
              {{ $t("supRisk.noAttachment") }}
            </span>
          </template>
        </BaseTable>
      </el-dialog>

      <CToolbar>
        <template #right>
          <el-button
            @click="cancelBill"
          >
            {{
              $t("common.cancel")
            }}
          </el-button>
          <template v-if="['add', 'edit'].includes(editType)">
            <el-button
              type="primary"
              :disabled="readOnly"
              @click="save"
            >
              <!-- 暂存 -->
              {{ $t("common.staging") }}
            </el-button>
            <el-button
              type="primary"
              :disabled="readOnly"
              @click="submit"
            >
              {{ $t("common.submit") }}
            </el-button>
          </template>
          <el-button
            v-if="editType === 'close'"
            type="primary"
            :disabled="readOnly"
            @click="close"
          >
            {{ $t("supRisk.close") }}
          </el-button>
          <el-button
            v-if="editType === 'update'"
            type="primary"
            :disabled="readOnly"
            @click="update"
          >
            {{ $t("supRisk.update") }}
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
import _pick from 'lodash/pick'
import { getDictItem } from '@/api/common'
import { adaptDictData } from '@/utils'
import { riskApi } from 'modb@/supplierPortraitAndRisk/api'

export default {
  name: 'RiskEdit',
  components: {
    CToolbar,
    BaseForm,
    BaseTable,
    QuickSearch
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      flowList: [],
      rules: {
        vendorName: [
          {
            required: true,
            message: this.$t('quota.vendorTips')
          }
        ]
      },
      rule1: [
        {
          ruleColumnA_1: '5',
          ruleColumnA_2: this.$t('supRisk.veryHigh'), // 很高
          ruleColumnA_3: this.$t('supRisk.ruleColumnA_3_veryHigh') // 出现的频率很高(或≥ 1 次/半年)；或在大多数情况下会发生；或可以证实多次发生
        },
        {
          ruleColumnA_1: '4',
          ruleColumnA_2: this.$t('supRisk.high'), // 高
          ruleColumnA_3: this.$t('supRisk.ruleColumnA_3_high') // 出现的频率较高(或≥ 1 次/半年)；或在大多数情况下很有可能会发生；或可以证实多次发生。
        },
        {
          ruleColumnA_1: '3',
          ruleColumnA_2: this.$t('supRisk.middle'), // 中
          ruleColumnA_3: this.$t('supRisk.ruleColumnA_3_middle') //  出现的频率中等(或≥ 1 次/年)；或在某种情况下可能会发生；或被证实曾经发生。
        },
        {
          ruleColumnA_1: '2',
          ruleColumnA_2: this.$t('supRisk.low'), // 低
          ruleColumnA_3: this.$t('supRisk.ruleColumnA_3_low') // 出现的频率较小；或一般不太可能发生；或没有被证实发生。
        },
        {
          ruleColumnA_1: '1',
          ruleColumnA_2: this.$t('supRisk.veryLow'), // 很低
          ruleColumnA_3: this.$t('supRisk.ruleColumnA_3_veryLow') // 几乎不可能发生，仅可能在非常罕见和例外的情况下发生。
        }
      ],
      columns1: [
        {
          attrs: {
            align: 'center',
            prop: 'ruleColumnA_1',
            minWidth: 150,
            label: t => t.$t('supRisk.ruleColumnA_1')
          }
        },
        {
          attrs: {
            align: 'center',
            prop: 'ruleColumnA_2',
            minWidth: 150,
            label: t => t.$t('supRisk.ruleColumnA_2')
          }
        },
        {
          attrs: {
            align: 'center',
            prop: 'ruleColumnA_3',
            minWidth: 150,
            label: t => t.$t('supRisk.ruleColumnA_3')
          }
        }
      ],
      rule2: [
        {
          ruleColumnB_1: this.$t('supRisk.quantitativeEvaluation'), // 定量评价
          ruleColumnB_2: this.$t('supRisk.quantitativeEvaluation_1'), // 财务损失/净利润(财报/内核)
          ruleColumnB_3: '0.3%' + this.$t('supRisk.under'), // 0.3%以下
          ruleColumnB_4: '0.4%-0.6%',
          ruleColumnB_5: '0.7%-0.9%',
          ruleColumnB_6: '1%-1.5%',
          ruleColumnB_7: '1.6%' + this.$t('supRisk.over') // 1.6%以上
        },
        {
          ruleColumnB_1: this.$t('supRisk.quantitativeEvaluation'), // 定量评价
          ruleColumnB_2: this.$t('supRisk.quantitativeEvaluation_2'), // 财务损失/总资产(财报/内核)
          ruleColumnB_3: '0.03%' + this.$t('supRisk.under'), // 0.03%以下
          ruleColumnB_4: '0.04%-0.06%',
          ruleColumnB_5: '0.07%-0.09%',
          ruleColumnB_6: '0.1%-0.15%',
          ruleColumnB_7: '0.16%' + this.$t('supRisk.over') // 0.16%以上
        },
        {
          ruleColumnB_1: this.$t('supRisk.qualitativeEvaluation'),
          ruleColumnB_2: this.$t('supRisk.qualitativeEvaluation_1'),
          ruleColumnB_3: this.$t('supRisk.qualitativeEvaluation_1_veryLow'),
          ruleColumnB_4: this.$t('supRisk.qualitativeEvaluation_1_low'),
          ruleColumnB_5: this.$t('supRisk.qualitativeEvaluation_1_middle'),
          ruleColumnB_6: this.$t('supRisk.qualitativeEvaluation_1_high'),
          ruleColumnB_7: this.$t('supRisk.qualitativeEvaluation_1_veryHigh')
        },
        {
          ruleColumnB_1: this.$t('supRisk.qualitativeEvaluation'),
          ruleColumnB_2: this.$t('supRisk.qualitativeEvaluation_2'),
          ruleColumnB_3: this.$t('supRisk.qualitativeEvaluation_2_veryLow'),
          ruleColumnB_4: this.$t('supRisk.qualitativeEvaluation_2_low'),
          ruleColumnB_5: this.$t('supRisk.qualitativeEvaluation_2_middle'),
          ruleColumnB_6: this.$t('supRisk.qualitativeEvaluation_2_high'),
          ruleColumnB_7: this.$t('supRisk.qualitativeEvaluation_2_veryHigh')
        },
        {
          ruleColumnB_1: this.$t('supRisk.qualitativeEvaluation'),
          ruleColumnB_2: this.$t('supRisk.qualitativeEvaluation_3'),
          ruleColumnB_3: this.$t('supRisk.qualitativeEvaluation_3_veryLow'),
          ruleColumnB_4: this.$t('supRisk.qualitativeEvaluation_3_low'),
          ruleColumnB_5: this.$t('supRisk.qualitativeEvaluation_3_middle'),
          ruleColumnB_6: this.$t('supRisk.qualitativeEvaluation_3_high'),
          ruleColumnB_7: this.$t('supRisk.qualitativeEvaluation_3_veryHigh')
        }
      ],
      columns2: [
        {
          attrs: {
            align: 'center',
            prop: 'ruleColumnB_1',
            minWidth: 150,
            label: t => t.$t('supRisk.ruleColumnB_1')
          }
        },
        {
          attrs: {
            align: 'center',
            prop: 'ruleColumnB_2',
            minWidth: 150,
            label: t => t.$t('supRisk.ruleColumnB_2')
          }
        },
        {
          attrs: {
            align: 'center',
            prop: 'ruleColumnB_3',
            minWidth: 150,
            label: t => t.$t('supRisk.ruleColumnB_3')
          }
        },
        {
          attrs: {
            align: 'center',
            prop: 'ruleColumnB_4',
            minWidth: 150,
            label: t => t.$t('supRisk.ruleColumnB_4')
          }
        },
        {
          attrs: {
            align: 'center',
            prop: 'ruleColumnB_5',
            minWidth: 150,
            label: t => t.$t('supRisk.ruleColumnB_5')
          }
        },
        {
          attrs: {
            align: 'center',
            prop: 'ruleColumnB_6',
            minWidth: 150,
            label: t => t.$t('supRisk.ruleColumnB_6')
          }
        },
        {
          attrs: {
            align: 'center',
            prop: 'ruleColumnB_7',
            minWidth: 150,
            label: t => t.$t('supRisk.ruleColumnB_7')
          }
        }
      ],
      rule3: [
        {
          ruleColumnC_1: this.$t('supRisk.level1'),
          ruleColumnC_2: '39-50',
          ruleColumnC_3: this.$t('supRisk.level1_desc')
        },
        {
          ruleColumnC_1: this.$t('supRisk.level2'),
          ruleColumnC_2: '26-38',
          ruleColumnC_3: this.$t('supRisk.level2_desc')
        },
        {
          ruleColumnC_1: this.$t('supRisk.level3'),
          ruleColumnC_2: '13-25',
          ruleColumnC_3: this.$t('supRisk.level3_desc')
        },
        {
          ruleColumnC_1: this.$t('supRisk.level4'),
          ruleColumnC_2: '1-12',
          ruleColumnC_3: this.$t('supRisk.level4_desc')
        }
      ],
      columns3: [
        {
          attrs: {
            align: 'center',
            prop: 'ruleColumnC_1',
            minWidth: 150,
            label: t => t.$t('supRisk.ruleColumnC_1')
          }
        },
        {
          attrs: {
            align: 'center',
            prop: 'ruleColumnC_2',
            minWidth: 150,
            label: t => t.$t('supRisk.ruleColumnC_2')
          }
        },
        {
          attrs: {
            align: 'center',
            prop: 'ruleColumnC_3',
            minWidth: 150,
            label: t => t.$t('supRisk.ruleColumnC_3')
          }
        }
      ],
      checkList: [],
      lastHandle: [],
      lastHandleColumns: [
        {
          attrs: {
            align: 'center',
            prop: 'responsesType',
            minWidth: 150,
            label: t => t.$t('supRisk.responsesType'),
            formatter: (row, column, cellValue, index) =>
              this.$getDictLabel('RESPONSES_TYPE', cellValue)
          }
        },
        {
          attrs: {
            align: 'center',
            prop: 'fileuploadId',
            minWidth: 200,
            label: t => t.$t('supRisk.fileuploadId')
          },
          slot: 'fileuploadId'
        },
        {
          attrs: {
            align: 'center',
            minWidth: 160,
            prop: 'completeDate',
            formatter: val => this.$parseTime(val),
            label: t => t.$t('supRisk.completeDate')
          }
        },
        {
          attrs: {
            align: 'center',
            minWidth: 250,
            prop: 'responseMeasures',
            label: t => t.$t('supRisk.responseMeasures')
          }
        }
      ],
      tableData: [],
      columns: [
        {
          attrs: {
            align: 'center',
            prop: 'responsesType',
            minWidth: 150,
            label: t => t.$t('supRisk.responsesType')
          },
          slot: 'responsesType'
        },
        {
          attrs: {
            align: 'center',
            prop: 'fileuploadId',
            minWidth: 200,
            label: t => t.$t('supRisk.fileuploadId')
          },
          slot: 'fileuploadId'
        },
        {
          attrs: {
            align: 'center',
            minWidth: 160,
            prop: 'completeDate',
            label: t => t.$t('supRisk.completeDate')
          },
          slot: 'completeDate'
        },
        {
          attrs: {
            align: 'center',
            minWidth: 250,
            prop: 'responseMeasures',
            label: t => t.$t('supRisk.responseMeasures')
          },
          slot: 'responseMeasures'
        },
        // {
        //   attrs: {
        //     align: "center",
        //     minWidth: 200,
        //     prop: "firstResponseMeasures",
        //     label: t => t.$t("supRisk.firstResponseMeasures")
        //   }
        // },
        {
          attrs: {
            align: 'center',
            label: t => t.$t('common.operation'),
            fixed: 'right',
            width: 80
          },
          operations: [
            {
              key: 'delete',
              event: 'deleteAttr2',
              name: this.$t('common.delete'),
              show: () => !this.readOnly,
              attrs: { type: 'text' }
            }
          ]
        }
      ],
      mergeForm1: {},
      mergeForm2: {},
      mergeForm3: {},
      mergeForm4: {},
      formItems1: [
        {
          itemAttrs: {
            label: this.$t('supRisk.riskCode')
          },
          uiAttrs: {
            disabled: true,
            key: 'riskCode'
          }
        },
        {
          itemAttrs: {
            label: this.$t('supRisk.status')
          },
          uiAttrs: {
            disabled: true,
            key: 'status',
            code: 'RISK_MONITORING_STATUS'
          },
          tag: 'dictSelect'
        },
        {
          itemAttrs: {
            label: this.$t('supRisk.createdName')
          },
          uiAttrs: {
            disabled: true,
            key: 'createdName'
          }
        },
        {
          itemAttrs: {
            label: this.$t('supRisk.department')
          },
          uiAttrs: {
            disabled: true,
            key: 'department'
          }
        },
        {
          itemAttrs: {
            label: this.$t('supRisk.creationDate')
          },
          uiAttrs: {
            disabled: true,
            key: 'creationDate'
          }
        }
      ],
      formItems2: [
        {
          itemAttrs: {
            label: this.$t('supRisk.categoryId')
          },
          uiAttrs: {
            key: 'categoryId'
          },
          slot: 'categoryId'
        },
        {
          itemAttrs: {
            label: this.$t('supRisk.vendorId')
          },
          uiAttrs: {
            key: 'vendorId'
          },
          slot: 'vendorId'
        },
        {
          itemAttrs: {
            label: this.$t('supRisk.riskDescription'),
            span: 24
          },
          uiAttrs: {
            key: 'riskDescription'
          },
          tag: 'textarea'
        }
      ],
      formItems3: [
        {
          itemAttrs: {
            label: this.$t('supRisk.riskType')
          },
          uiAttrs: {
            key: 'riskType',
            code: 'RISK_TYPE'
          },
          tag: 'dictSelect'
        },
        {
          itemAttrs: {
            label: this.$t('supRisk.riskPossibility')
          },
          uiAttrs: {
            key: 'riskPossibility',
            options: [
              { id: 1, label: '1-' + this.$t('supRisk.veryLow'), value: '1' },
              { id: 2, label: '2-' + this.$t('supRisk.low'), value: '2' },
              { id: 3, label: '3-' + this.$t('supRisk.middle'), value: '3' },
              { id: 4, label: '4-' + this.$t('supRisk.high'), value: '4' },
              { id: 5, label: '5-' + this.$t('supRisk.veryHigh'), value: '5' }
            ]
          },
          listeners: {
            change: this.calcRiskCoefficient
          },
          tag: 'select'
        },
        {
          itemAttrs: {
            label: this.$t('supRisk.riskScore')
          },
          uiAttrs: {
            key: 'riskScore',
            options: [
              { id: 1, label: '1', value: '1' },
              { id: 2, label: '2', value: '2' },
              { id: 3, label: '3', value: '3' },
              { id: 4, label: '4', value: '4' },
              { id: 5, label: '5', value: '5' },
              { id: 6, label: '6', value: '6' },
              { id: 7, label: '7', value: '7' },
              { id: 8, label: '8', value: '8' },
              { id: 9, label: '9', value: '9' },
              { id: 10, label: '10', value: '10' }
            ]
          },
          listeners: {
            change: this.calcRiskCoefficient
          },
          tag: 'select'
        },
        {
          itemAttrs: {
            label: this.$t('supRisk.riskCoefficient')
          },
          uiAttrs: {
            key: 'riskCoefficient',
            disabled: true
          }
        },
        {
          itemAttrs: {
            label: this.$t('supRisk.riskLevel')
          },
          uiAttrs: {
            key: 'riskLevel',
            disabled: true,
            code: 'RISK_LEVEL'
          },
          tag: 'dictSelect'
        },
        {
          itemAttrs: {
            label: this.$t('supRisk.riskInfluencesDescription'),
            span: 24
          },
          uiAttrs: {
            key: 'riskInfluencesDescription'
          },
          tag: 'textarea'
        }
      ],
      formItems4: [
        {
          itemAttrs: {
            label: this.$t('supRisk.closeDate')
          },
          uiAttrs: {
            key: 'closeDate',
            disabled: true
          },
          tag: 'date'
        },
        {
          itemAttrs: {
            label: this.$t('supRisk.riskImplementDesc'),
            span: 24
          },
          uiAttrs: {
            key: 'riskImplementDesc'
          },
          tag: 'textarea'
        }
      ],
      formLabelWidth: '120px',
      peopleDialog: false,
      fileInfo: {
        fileModular: 'sup',
        fileFunction: 'estimatingPrice',
        fileType: 'images'
      },
      readOnly: false,
      ruleVisible: false,
      processControls: [],
      editCondVisible: false,
      lastHandleVisible: false,
      activeDims: ['1', '2', '3', '4', '5'],
      ruleActiveDims: ['1', '2', '3']
    }
  },
  computed: {
    editType () {
      return this.$attrs.params.flag
    }
  },
  created () {
    getDictItem('RISK_PROCESS_CONTROL').then(res => {
      this.flowList = adaptDictData(res.data, 'dict')
    })
  },
  mounted () {
    const { flag, row, readOnly = false } = this.$attrs.params
    this.readOnly = readOnly
    if (flag !== 'add') {
      riskApi.get(row.riskMonitoringId).then(({ data }) => {
        this.processControls = [...data.processControls]
        this.checkList = this.processControls.map(i => i.tag)
        this.mergeForm1 = _pick(data, [
          'riskMonitoringId',
          'riskCode',
          'status',
          'createdName',
          'department',
          'creationDate'
        ])
        this.mergeForm2 = _pick(data, [
          'categoryId',
          'categoryCode',
          'categoryName',
          'vendorId',
          'vendorCode',
          'vendorName',
          'riskDescription'
        ])
        this.mergeForm3 = _pick(data, [
          'riskType',
          'riskPossibility',
          'riskScore',
          'riskCoefficient',
          'riskLevel',
          'riskInfluencesDescription'
        ])
        this.tableData = [...data.responses]
        this.mergeForm4 = _pick(data, ['closeDate', 'riskImplementDesc'])
      })
    }
  },
  methods: {
    close () {
      const data = {
        ...this.mergeForm1,
        ...this.mergeForm2,
        ...this.mergeForm3,
        ...this.mergeForm4,
        responses: this.tableData,
        processControls: this.checkList.map(tag => ({ tag }))
      }
      riskApi.close(data).then(res => {
        this.$message({
          type: 'success',
          message: res.message
        })
        this.cancelBill()
      })
    },
    update () {
      console.log('!!!!!!!!! this.checkList   :',this.checkList);
      const data = {
        ...this.mergeForm1,
        ...this.mergeForm2,
        ...this.mergeForm3,
        ...this.mergeForm4,
        responses: this.tableData,
        processControls: this.checkList.map(tag => ({ tag }))
      }
      riskApi.modify(data).then(res => {
        this.$message({
          type: 'success',
          message: res.message
        })
        this.cancelBill()
      })
    },
    objectSpanMethod ({ row, column, rowIndex, columnIndex }) {
      if (columnIndex === 0) {
        switch (rowIndex) {
          case 0:
            return { rowspan: 2, colspan: 1 }
          case 2:
            return { rowspan: 3, colspan: 1 }
          default:
            return { rowspan: 0, colspan: 0 }
        }
      }
    },
    showRule () {
      console.log('showRule')
      this.ruleVisible = true
    },
    calcRiskCoefficient () {
      const { riskPossibility, riskScore } = this.mergeForm3
      if (riskPossibility && riskScore) {
        const count = (riskPossibility * riskScore).toFixed(2)
        this.mergeForm3.riskCoefficient = count
        const num = [1, 12, 13, 25, 26, 38, 39, 50]
        let max = 0
        if (count == 1) {
          max = 1
        } else {
          max = num.findIndex(i => count <= i)
        }
        const range = `${num[max - 1]}-${num[max]}`
        this.mergeForm3.riskLevel = range
      }
    },
    outerHandleUploadSuccess (file, scope) {
      const { fileId = '', fileName = '' } = file || {}
      scope.row.fileName = fileName
      scope.row.fileuploadId = fileId.toString()
      this.$nextTick(() => {
        this.$set(this.tableData, scope.$index, this.tableData[scope.$index])
      })
    },
    deleteAttr2 (scope) {
      this.tableData.splice(scope.$index, 1)
    },
    getCategory (value, scope) {
      console.log(value, scope)
      this.mergeForm2.categoryId = value.categoryId || ''
      this.mergeForm2.categoryName = value.categoryName || ''
      this.mergeForm2.categoryCode = value.categoryCode || ''
    },
    getVendor (value, scope) {
      console.log(value, scope)
      this.mergeForm2.vendorCode = value.companyCode || ''
      this.$set(this.mergeForm2, 'vendorName', value.companyName)
      // this.mergeForm2.vendorName = value.companyName || ''
      this.mergeForm2.vendorId = value.companyId || ''
    },
    addFormula () {
      this.tableData.push({})
    },
    showLastHandle () {
      riskApi.lastHandle({
          vendorId: this.mergeForm2.vendorId,
          riskType: this.mergeForm3.riskType
        })
        .then(res => {
          this.lastHandle = res.data
          this.lastHandleVisible = true
        })
    },
    submit () {
      this.$refs['form2'].validate((valid) => {
        if (valid) {
          const data = {
            ...this.mergeForm1,
            ...this.mergeForm2,
            ...this.mergeForm3,
            ...this.mergeForm4,
            responses: this.tableData,
            processControls: this.checkList.map(tag => ({ tag }))
          }
          riskApi.submit(data).then(res => {
            this.$message({
              type: 'success',
              message: res.message
            })
            this.cancelBill()
          })
        }
      })
    },
    save () {
      this.$refs['form2'].validate((valid) => {
        if (valid) {
          const data = {
            ...this.mergeForm1,
            ...this.mergeForm2,
            ...this.mergeForm3,
            ...this.mergeForm4,
            responses: this.tableData,
            processControls: this.checkList.map(tag => ({ tag }))
          }
          const { flag } = this.$attrs.params
          if (flag === 'add') {
            riskApi.add(data).then(res => {
              this.$message({
                type: 'success',
                message: res.message
              })
              this.cancelBill()
            })
          } else {
            riskApi.modify(data).then(res => {
              this.$message({
                type: 'success',
                message: res.message
              })
              this.cancelBill()
            })
          }
        }
      })
    },
    cancelBill () {
      const { tabName } = this.$attrs.params
      this.$emit('tab-remove', tabName)
      this.__setTabTodo('RiskList.getQuerydata')
    }
  }
}
</script>
<style scoped lang="scss">
.riskEdit {
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
  .rule {
    cursor: pointer;
  }
}
</style>
