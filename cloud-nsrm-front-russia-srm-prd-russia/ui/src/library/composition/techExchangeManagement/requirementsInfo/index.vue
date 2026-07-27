<template>
  <div class="requirements-info">
    <p v-if="!readonly">
      <!--新增物料-->
      <el-button
        type="primary"
        @click="addRequirementItem"
      >
        {{ $t('bidMod.addItem') }}
      </el-button>
    </p>

    <!-- 表格 -->
    <vxe-table
      ref="requirementTable"
      border
      show-overflow="tooltip"
      keep-source
      align="center"
      max-height="500"
      :data="requirementData"
      :valid-config="{ showMessage: false }"
      :edit-rules="requirementTableValidRules"
      :edit-config="{
        trigger: 'click',
        mode: 'row',
        autoClear: false,
        enabled: !readonly
      }"
    >
      <!--序号-->
      <vxe-column
        type="seq"
        width="60"
      />

      <!--无料号-->
      <vxe-column
        field="isNoCodeItem"
        title="无料号"
        width="130"
        :edit-render="{}"
      >
        <template #edit="{ row, $rowIndex }">
          <el-checkbox
            v-model="row.isNoCodeItem"
            true-label="Y"
            false-label="N"
            @change="isNoCodeItemChange(row)"
          />
        </template>
        <template #default="{ row }">
          {{ $getDictLabel('YES_OR_NO', row.isNoCodeItem) }}
        </template>
      </vxe-column>

      <!--物料编码-->
      <vxe-column
        field="itemCode"
        :title="$t('bidMod.targetNum')"
        width="150"
        :edit-render="{}"
      >
        <template #edit="{ row }">
          <quick-search
            v-if="row.isNoCodeItem !== 'Y'"
            :show-input="row.itemCode"
            show-key="itemCode"
            :scope-data="row"
            name="scc_base_material_item_display"
            @close-quicksearch="itemCodeChange"
          />
        </template>
        <template #default="{ row }">
          {{ row.isNoCodeItem !== 'Y' ? row.itemCode : '' }}
        </template>
      </vxe-column>

      <!--物料名称-->
      <vxe-column
        field="itemName"
        :title="$t('bidMod.targetDesc')"
        min-width="150"
        show-overflow="tooltip"
        :edit-render="{}"
      >
        <template #edit="{ row }">
          <el-input
            v-if="row.isNoCodeItem === 'Y'"
            v-model="row.itemName"
            maxlength="100"
          />
          <template v-else>
            {{ row.itemName }}
          </template>
        </template>
        <template #default="{ row }">
          {{ row.itemName }}
        </template>
      </vxe-column>

      <!--采购分类-->
      <vxe-column
        field="categoryName"
        :title="$t('bidMod.purcategoryName')"
        width="150"
        :edit-render="{}"
      >
        <template #edit="{ row, $rowIndex }">
          <quick-search
            v-if="row.isNoCodeItem === 'Y'"
            :show-input="row.categoryName"
            show-key="categoryName"
            :scope-data="row"
            :table-index="$rowIndex"
            clearable
            name="scc_base_purchase_category2"
            @close-quicksearch="categoryNameChange"
          />
          <span v-else>{{ row.categoryName }}</span>
        </template>
        <template #default="{ row }">
          {{ row.categoryName }}
        </template>
      </vxe-column>

      <!--预计数量-->
      <vxe-column
        field="demandQuantity"
        :title="$t('bidMod.demandQuantity')"
        width="100"
        :edit-render="{}"
      >
        <template #edit="scope">
          <el-input
            v-model="scope.row.demandQuantity"
            v-input-format="{ type: 'number' }"
          />
        </template>
        <template #default="{ row }">
          {{ row.demandQuantity }}
        </template>
      </vxe-column>

      <!--单位-->
      <vxe-column
        field="unit"
        :title="$t('bidMod.unit')"
        width="100"
        :edit-render="{}"
      >
        <template #edit="{ row }">
          <DictSelect
            v-if="row.isNoCodeItem === 'Y'"
            v-model="row.unit"
            code="unit"
          />
          <span v-else>{{ $getDictLabel('unit', row.unit) }}</span>
        </template>
        <template #default="{ row }">
          <span>{{ $getDictLabel('unit', row.unit) }}</span>
        </template>
      </vxe-column>

      <!--技术文件-->
      <vxe-column
        field="itemFiles"
        title="技术文件"
        width="150"
        :edit-render="{}"
      >
        <template #edit="scope">
          <el-button
            type="text"
            @click="openItemFilesDialog(scope)"
          >
            {{ $t('common.select') }}
          </el-button>
        </template>
        <template #default="scope">
          <el-button
            type="text"
            :disabled="false"
            @click.stop="openItemFilesDialog(scope)"
          >
            {{ readonly ? $t('common.view') : $t('common.select') }}
          </el-button>
        </template>
      </vxe-column>

      <!--备注-->
      <vxe-column
        field="remark"
        :title="$t('common.remark')"
        width="160"
        :edit-render="{}"
      >
        <template #edit="scope">
          <el-input
            v-model="scope.row.remark"
            maxlength="300"
          />
        </template>
        <template #default="{ row }">
          {{ row.remark }}
        </template>
      </vxe-column>

      <!--操作-->
      <vxe-column
        width="60"
        :title="$t('common.operation')"
        fixed="right"
        :visible="!readonly"
      >
        <template #default="{ row, $rowIndex }">
          <!--删除-->
          <el-button
            type="text"
            @click="deleteItem(row)"
          >
            {{ $t('common.delete') }}
          </el-button>
        </template>
      </vxe-column>
    </vxe-table>

    <!--技术文件弹窗-->
    <itemFilesDialog
      v-if="itemFilesDialogVisible"
      :visible.sync="itemFilesDialogVisible"
      :edit-row="editRow"
      :readonly="readonly"
      @save="saveItemFiles"
    />
  </div>
</template>

<script>
/**
 * 需求信息
 */
import QuickSearch from 'lib@/components/QuickSearch'
import itemFilesDialog from './itemFilesDialog'

export default {
  name: 'RequirementsInfo',

  components: {
    QuickSearch,
    itemFilesDialog
  },

  props: {
    infoData: {
      type: [Object, Array],
      required: true
    },
    readonly: {
      type: Boolean,
      default: false
    }
  },

  data () {
    return {
      requirementData: [],
      requirementTableValidRules: {
        itemName: [{ required: true }],
        categoryName: [{ required: true }],
        unit: [{ required: true }]
      },
      bankRowIndex: '',
      itemFilesDialogVisible: false,
      editRow: null,
      editRowIndex: ''
    }
  },

  watch: {
    infoData: {
      handler (val) {
        this.requirementData = val || []
      },
      immediate: true,
      deep: true
    }
  },

  methods: {
    /* 新增行 */
    addRequirementItem () {
      this.$refs.requirementTable.insertAt({
        categoryId: '',
        categoryName: '',
        itemId: '',
        itemCode: '',
        itemName: '',
        demandQuantity: '',
        isNoCodeItem: 'N',
        unit: '',
        remark: ''
      }, -1)
    },

    /* 是否无料号寻源勾选 */
    isNoCodeItemChange (row) {
      if (row.isNoCodeItem === 'Y' && row.itemCode) {
        // 无料号，清空原有选的料号相关数据
        row.itemId = ''
        row.itemCode = ''
        row.itemName = ''
        row.categoryName = ''
        row.materialFormulaRelateId = ''
      }
    },

    /* 选择一个物料 */
    itemCodeChange (val, row) {
      row.itemId = val ? val.materialId : ''
      row.itemCode = val ? val.materialCode : ''
      row.itemName = val ? val.materialName : ''
      row.unit = val ? val.unit : ''
      row.categoryId = val ? val.categoryId : ''
      row.categoryCode = val ? val.categoryCode : ''
      row.categoryName = val ? val.categoryName : ''
    },

    /* 选择分类 */
    categoryNameChange (value, row) {
      row.categoryCode = value.categoryCode || ''
      row.categoryName = value.categoryName || ''
      row.categoryId = value.categoryId || ''
    },

    /* 删除行 */
    deleteItem (row) {
      this.$refs.requirementTable.remove(row)
    },

    /* 技术文件弹窗 */
    // 打开
    openItemFilesDialog ({ row, $rowIndex }) {
      this.editRow = {
        ...row,
        businessId: row.technicalExchangeItemId || ''
      }
      this.editRowIndex = $rowIndex
      this.itemFilesDialogVisible = true
    },
    // 保存
    saveItemFiles (data) {
      const { fullData } = this.$refs.requirementTable.getTableData()
      fullData[this.editRowIndex].itemFiles = data
    },
    /* END */

    /* 返回当前数据 父组件外部调用 */
    getParamsData () {
      const { fullData } = this.$refs.requirementTable.getTableData()
      if (fullData.length === 0) {
        // 请先录入需求明细
        this.$message.warning(this.$t('bidMod.bidMsgList[27]'))
        return {
          status: false
        }
      }

      return {
        status: true,
        data: fullData
      }
    }
  }
}
</script>
