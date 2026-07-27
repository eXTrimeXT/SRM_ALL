<template>
  <SrmDialog
    size="xLarge"
    :title="$t('bidMod.biddingControl.startNewRound')"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
    append-to-body
  >
    <el-form
      ref="form"
      :model="formData"
      :rules="formRules"
      :disabled="readonly"
      label-position="top"
    >
      <SrmRow>
        <SrmCol :init-col="3">
          <!-- 新一轮竞价开始时间 -->
          <el-form-item :label="$t('bidMod.common.orderStartTime')" prop="orderStartTime">
            <el-date-picker
              v-model="formData.orderStartTime"
              type="datetime"
              format="yyyy-MM-dd HH:mm:ss"
              value-format="yyyy-MM-dd HH:mm:ss"
              :placeholder="$t('bidMod.datePicker')"
            />
          </el-form-item>
        </SrmCol>

        <SrmCol :init-col="3">
          <!-- 新一轮竞价结束时间 -->
          <el-form-item :label="$t('bidMod.common.orderEndTime')" prop="orderEndTime">
            <el-date-picker
              v-model="formData.orderEndTime"
              type="datetime"
              format="yyyy-MM-dd HH:mm:ss"
              value-format="yyyy-MM-dd HH:mm:ss"
              :placeholder="$t('bidMod.datePicker')"
            />
          </el-form-item>
        </SrmCol>

        <SrmCol :init-col="3">
          <!-- 是否设置新一轮起拍价 -->
          <el-form-item :label="$t('bidMod.competitionLts.configStartingPrice')" prop="configStartingPrice">
            <el-switch
              v-model="formData.configStartingPrice"
              active-value="Y"
              inactive-value="N"
            />
          </el-form-item>
        </SrmCol>
      </SrmRow>
    </el-form>

    <h4>{{ $t('bidMod.common.auctVendor') }}</h4>
    <div v-if="!readonly" style="display:flex;margin-bottom: 10px;">
      <!--智能推荐-->
      <el-button
        style="margin-right: 8px;"
        type="primary"
        @click="openRecommendVendorDialog"
      >
        {{ $t("bidMod.smartRecommond") }}
      </el-button>

      <!--新增-->
      <QuickSearch
        show-button
        :btn-title="$t('common.add')"
        show-key="companyCode"
        name="scc_sup_company_info2"
        class="select-company-search"
        @close-quicksearch="addOneSuppliers"
      />
    </div>

    <BaseTable
      ref="vendorTableRef"
      style="height: 140px;"
      border
      row-key="vendorId"
      :data-source="vendorData"
      :columns="vendorColumn"
      columns-name="vendorColumn"
      :initialize="false"
      :editable="false"
      :index="true"
    />

    <!-- <h4>指定物料多轮竞价</h4>
    <QuickSearch
      name="scc_base_material_item"
      :disabled="readonly"
      :btnTitle="$t('common.add')"
      showButton
      multiSelect
      style="margin-bottom:10px"
      @close-quicksearch="getMaterialData"
    />

    <BaseTable
      ref="materialTableRef"
      style="height: 140px;"
      border
      row-key="materialId"
      :data-source="materialSource"
      :columns="materialColumn"
      columns-name="materialColumn"
      :initialize="false"
      :editable="false"
      :index="true"
    /> -->

    <!-- 推荐供应商 -->
    <RecommendVendorDialog
      v-if="recommendVendorDialogVisible"
      :visible.sync="recommendVendorDialogVisible"
      :base-info="baseInfo"
      :vendors-data="vendorData"
      :item-list="requireInfoData"
      @saveRecommendVendor="saveRecommendVendor"
    />

    <slot name="footerContent" />

    <template #footer>
      <el-button @click="dialogVisible = false">
        {{ $t('common.cancel') }}
      </el-button>

      <el-button v-if="formData.configStartingPrice === 'Y'" type="primary" @click="submit('submitNext')">
        {{ $t('common.nextOne') }}
      </el-button>
      <el-button v-else type="primary" @click="submit('submit')">
        {{ $t('common.publish') }}
      </el-button>
    </template>
  </SrmDialog>
</template>

<script>
/**
 * 发起新一轮报价
 */
import { BUSINESS_TYPE_ENUM } from 'lib@/composition/origin/enum'
import { validatorBusinessType } from '@/library/composition/origin/composition'
import BaseTable from 'lib@/components/BaseTable/baseTable'
import RecommendVendorDialog from 'lib@/composition/origin/recommendVendorDialog'
import QuickSearch from 'lib@/components/QuickSearch'

export default {
  name: 'StartNewRound',
  components: {
    BaseTable,
    RecommendVendorDialog,
    QuickSearch
  },

  props: {
    visible: {
      type: Boolean,
      required: true
    },
    // 业务类型
    businessType: {
      type: String,
      required: true,
      validator: value => validatorBusinessType(value)
    },
    projectId: {
      type: [String, Number],
      required: true
    },
    readonly: {
      type: Boolean,
      default: false
    },
    baseInfo: {
      type: Object,
      default: () => null
    },
    vendorList: {
      type: Array,
      default: () => []
    },
    requireInfoData: {
      type: Array,
      default: () => []
    },
    vendorInfoData: {
      type: Array,
      default: () => []
    }
  },

  data () {
    return {
      formData: {
        orderEndTime: '',
        orderStartTime: '',
        configStartingPrice: 'N'
      },
      formRules: {
        orderEndTime: [{ required: true, message: this.$t('common.pleaseSelect') }],
        orderStartTime: [{ required: true, message: this.$t('common.pleaseSelect') }]
      },
      checkAll: false,
      recommendVendorDialogVisible: false,
      materialData: [],
      vendorData: [],
      vendorColumn: [
        {
          attrs: {
            prop: 'vendorCode',
            label: () => this.$t('orderMod.buyerOrderSynergy.vendorCode'),
            align: 'center',
            minWidth: '120',
            showOverflowTooltip: true
          }
        },
        {
          attrs: {
            prop: 'vendorName',
            label: () => this.$t('orderMod.buyerOrderSynergy.vendorName'),
            align: 'center',
            minWidth: '120',
            showOverflowTooltip: true
          }
        },
        {
          attrs: {
            prop: 'isNewVendor',
            label: () => this.$t('bidMod.common.isNewVendor'),
            align: 'center',
            minWidth: '120',
            formatter: value => this.$getDictLabel('YES_OR_NO', value)
          }
        },
        {
          attrs: {
            prop: 'operation',
            label: () => this.$t('common.operation'),
            width: 150,
            fixed: 'right'
          },
          operations: [
            {
              event: 'deleteItemVendor',
              name: this.$t('common.delete'),
              func: this.deleteItemVendor
            }
          ]
        }
      ],
      materialSource: [],
      materialColumn: [
        {
          attrs: {
            prop: 'itemCode',
            label: () => this.$t('common.materialCode'),
            align: 'center',
            minWidth: '120',
            showOverflowTooltip: true
          }
        },
        {
          attrs: {
            prop: 'itemName',
            label: () => this.$t('common.materialName'),
            align: 'center',
            minWidth: '120',
            showOverflowTooltip: true
          }
        },
        {
          attrs: {
            prop: 'categoryName',
            label: () => this.$t('perfMod.categoryId'),
            align: 'center',
            minWidth: '120'
          }
        },
        {
          attrs: {
            prop: 'unit',
            label: () => this.$t('bidMod.appraisUnit'),
            align: 'center',
            minWidth: '120',
            formatter: value => this.$getDictLabel('unit', value)
          }
        },
        {
          attrs: {
            prop: 'operation',
            label: () => this.$t('common.operation'),
            width: 150,
            fixed: 'right'
          },
          operations: [
            {
              event: 'deleteItemVendor',
              name: this.$t('common.delete'),
              func: this.deleteItemMaterial
            }
          ]
        }
      ]
    }
  },

  computed: {
    dialogVisible: {
      get: function () {
        return this.visible
      },
      set: function (val) {
        this.$emit('update:visible', val)
      }
    },
    vendorIdList () {
      return this.vendorInfoData.map(item => item.vendorId)
    }
  },

  mounted () {

  },

  methods: {
    // 指定物料多轮竞价新增
    getMaterialData (val) {
      console.log(val, 'getMaterialData')
      val.forEach(item => {
        const row = {
          itemCode: item.materialCode,
          itemName: item.materialName,
          itemId: item.materialId,
          ...item
        }
        this.materialSource.push(row)
      })
    },
    // 删除物料
    deleteItemMaterial (scope, data) {
      data.splice(scope.$index, 1)
    },
    // 删除供应商
    deleteItemVendor (scope, data) {
      this.vendorData.splice(scope.$index, 1)
    },
    /* 新增一个供应商 */
    addOneSuppliers (val) {
      if (this.vendorData.find(item => val.companyId === item.vendorId)) {
        // 供应商已存在，请勿重复添加
        this.$message.warning(`${val ? val.companyName || '' : ''} ${this.$t('bidMod.common.vendorRepeatMsg')}`)
        return
      }

      this.vendorData.push({
        vendorId: val.companyId,
        vendorCode: val.companyCode,
        vendorName: val.companyName,
        isNewVendor: this.vendorIdList.includes(val.companyId) ? 'N' : 'Y'
      })
    },
    // 保存智能推荐供应商
    saveRecommendVendor (vendorList = []) {
      console.log('vendorList', vendorList)
      if (vendorList.length) {
        for (let item of vendorList) {
          item.isNewVendor = this.vendorIdList.includes(item.vendorId) ? 'N' : 'Y'
          this.vendorData.push(item)
        }
      }
    },
    /* 打开智能推荐供应商弹窗 */
    openRecommendVendorDialog () {
      this.recommendVendorDialogVisible = true
    },

    /* 校验并返回数据 */
    validate () {
      return new Promise(async resolve => {
        const valid = await this.$refs.form.validate().catch(() => this.__focus_error__())

        if (!valid) {
          resolve({ status: false })
          return
        }

        resolve({
          status: true,
          data: {
            formData: this.formData,
            vendorData: this.vendorData
          }
        })
      })
    },

    /* 提交 */
    async submit (type) {
      const validateData = await this.validate()

      if (validateData.status) {
        this.$emit(type, validateData.data)
      }
    }

  }
}
</script>

<style lang="scss" scoped>
.vendor-checkbox-group {
  margin-top: 15px;
  max-height: 180px;
  overflow: hidden;
  overflow-y: auto;
  & > ::v-deep .el-checkbox {
    display: block;
    .el-checkbox__label {
      width: 100%;
      white-space: nowrap;
      text-overflow: ellipsis;
      overflow: hidden;
    }
  }
}
</style>
