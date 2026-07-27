<!--
    快查功能存在缺陷，不满足在估价中查询价格模型的要求:
    1. 只能选择生效的价格模型
    2. 选择物料后，只能匹配无采购分类的价格模型，以及包含物料对应的采购分类的价格模型
 -->
<template>
  <div class="the_quick_search">
    <srm-row :gutter="0" style="display:block;">
      <srm-col
        :init-col="1"
        style="position: relative;padding: 0;"
      >
        <el-input
          v-model="inputName"
          :disabled="true"
          class="the_quick_select"
        />
        <el-button
          :disabled="disabled"
          icon="el-icon-search"
          class="quick-search-btn"
          @click="openDialog"
        />
      </srm-col>
    </srm-row>
    <!-- 选择价格模型 -->
    <el-dialog
      :visible.sync="visible"
      :title="$t('priceModel.priceModel.selectPriceModel')"
      width="900px"
      :destroy-on-close="true"
      :close-on-click-modal="false"
      @close="closeDialog"
    >
      <div class="search-content">
        <el-form
          ref="searchPMform"
          :inline="true"
          :model="queryForm"
          label-width="80px"
        >
        <!-- 模型编码 -->
          <el-form-item :label="$t('priceModel.priceModel.priceModelCode')">
            <el-input
              v-model="queryForm.priceModelCode"
              @keyup.native.enter="queryData"
            />
          </el-form-item>
          <!-- 模型名称 -->
          <el-form-item label="$t('perfMod.templateName2')">
            <el-input
              v-model="queryForm.priceModelName"
              @keyup.native.enter="queryData"
            />
          </el-form-item>
          <el-form-item>
            <el-button
              type="primary"
              @click="queryData"
            >
              <!-- 查询 -->
              {{ $t("components.common.search") }}
            </el-button>
            <el-button @click="resetForm">
              <!-- 重置 -->
              {{ $t("components.common.reset") }}
            </el-button>
          </el-form-item>
        </el-form>
        <el-table
          ref="vendorSelector"
          :data="tableData"
          style="width: 100%"
          border
          highlight-current-row
          max-height="250px"
          @current-change="handleSelectionChange"
        >
          <!-- 模型编码 -->
          <el-table-column
            align="center"
            prop="priceModelCode"
            :label="$t('priceModel.priceModel.priceModelCode')"
            min-width="120"
          />
          <!-- 模型名称 -->
          <el-table-column
            align="center"
            prop="priceModelName"
            :label="$t('perfMod.templateName2')"
            min-width="120"
          />
          <!-- 采购分类 -->
          <el-table-column
            align="center"
            prop="categoryName"
            :label="$t('bidMod.purcategoryName')"
            min-width="120"
          />
        </el-table>
      </div>
      <div
        slot="footer"
        class="dialog-footer"
      >
        <el-button @click="cancleHandle">
          {{ $t('common.cancel') }}
        </el-button>

        <el-button
          type="primary"
          @click="comfirmSelect"
        >
          {{ $t('common.confirm') }}
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import { priceModel } from 'modb@/priceModel/api'
export default {
  name: 'OrderListDailog',
  props: {
    // queryParams 可传入的参数
    // poNumber:'', //订单号
    // vendorCode:'',// 供应商编码
    // vendorName:'',// 供应商名称
    // itemCode:'',// 物料编码
    // itemName:'',// 物料名称
    // ouCode:'',// ou编码
    // djNumber:'',// 定价单号
    queryParams: {
      type: Object,
      default: () => {}
    },
    type: {// qua | siteBaseLine | effect
      type: String,
      default: ''
    },
    disabled: {
      type: Boolean,
      default: function () {
        return false
      }
    },
    inputValue: {
      type: String,
      default: ''
    }
  },
  data () {
    return {
      visible: false,
      inputName: '',
      pageInfo: {
        total: 0,
        pageNum: 1,
        pageSize: 1000
      },
      queryForm: {},
      tableData: [],
      currentRow: {}
    }
  },
  watch: {
    inputValue (val) {
      this.inputName = val
    }
  },
  created () {
    this.inputName = this.inputValue
  },
  methods: {
    // 查询弹框数据
    queryData () {
      let params = {
        ...this.queryParams,
        ...this.queryForm,
        ...this.pageInfo
      }
      priceModel.listPageForEstimate(params).then(res => {
        this.tableData = res.data.list
      })
    },
    handleCurrentChange (num) {
      this.queryForm.pageNum = num
      this.queryData()
    },
    handleSizeChange (size) {
      this.queryForm.pageSize = size
      this.queryData()
    },
    handleSelectionChange (val) {
      this.currentRow = val
    },
    comfirmSelect () {
      this.inputName = this.currentRow.priceModelName
      this.$emit('on-ok', this.currentRow)
      this.visible = false
    },
    cancleHandle () {
      this.$emit('on-cancle')
      this.visible = false
    },
    closeDialog () {
      this.$emit('on-cancle')
      this.visible = false
    },
    async openDialog () {
      if (!this.queryParams.orgId) {
        // '请先选择业务实体！'
        this.$message.warning(this.$t('cusEntry.supplement20250211.businessEntitySelection'))
        return
      }

      if (!this.queryParams.materialCode) {
        // '请先选择物料！'
        this.$message.warning(this.$t('priceModel.priceModel.selectMaterial'))
        return
      }

      this.reset()
      this.visible = true

      this.queryData()
    },
    reset () {
      this.queryForm = {
        priceModelCode: '',
        priceModelName: ''
      }
    },
    resetForm () {
      this.queryForm.priceModelCode = ''
      this.queryForm.priceModelName = ''
    }
  }
}
</script>
<style lang="scss" scoped>
  .inlineFormClass{
    margin-top: 20px;
    text-align: right;
  }
  :deep(.the_quick_search) {
    white-space: normal;
    position: relative;
    .el-input-group__append {
        padding-right: 8px;
    }
    .the_quick_select {
        display: block;
        padding: 0;
    }
    .the_quick_select .el-input__suffix {
        right: 30px;
    }
    .the_quick_select .el-input__suffix .el-input__icon {
        width: 16px;
    }
    .el-form-item {
        margin-bottom: 0;
    }
    .quick-search-btn {
        position: absolute;
        top: 2px;
        bottom: 2px;
        right: 2px;
        padding: 4px 6px !important;
        border: none;
        min-width: 20px;
        margin: 0 !important;
    }
  }
</style>
