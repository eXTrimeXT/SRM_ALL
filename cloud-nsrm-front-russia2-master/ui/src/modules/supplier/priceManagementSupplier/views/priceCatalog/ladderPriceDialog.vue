<template>
  <srm-dialog
    :title="$t('bidMod.ladderPrice')"
    size="middle"
    :visible.sync="dialogVisible"
    append-to-body
    :close-on-click-modal="false"
  >
    <p class="the_title1">
      <span style="margin-right: 10px">{{ $t('bidMod.targetNum') + '：' + itemCode }}</span>
      <span>{{ $t('bidMod.targetDesc') + '：' + itemName }}</span>
    </p>

    <!-- 阶梯价类型：标准阶梯价 -->
    <p>{{ $t('bidMod.ladderType') + '：' + $t('bidMod.standardladderPrice') }}</p>

    <el-table
      :data="tableData"
      style="width: 100%"
      border
      max-height="333px"
    >
      <el-table-column
        align="center"
        type="index"
        width="50"
      />
      <el-table-column
        align="center"
        prop="beginQuantity"
        :label="$t('bidMod.beginQuantity')"
        width="150"
        show-overflow-tooltip
      />
      <el-table-column
        align="center"
        prop="endQuantity"
        :label="$t('bidMod.endQuantity')"
        width="150"
        show-overflow-tooltip
      />
      <el-table-column
        align="center"
        prop="unit"
        :label="$t('bidMod.unit')"
        width="100"
        :formatter="(row, column, cellValue) => $getDictLabel('unit', cellValue)"
        show-overflow-tooltip
      />
      <el-table-column
        align="center"
        prop="price"
        :label="$t('bidMod.notaxSelectedPrice')"
        show-overflow-tooltip
      />
    </el-table>

    <div
      slot="footer"
      class="dialog-footer"
    >
      <el-button @click="dialogVisible = false">
        {{ $t('common.backTo') }}
      </el-button>
    </div>
  </srm-dialog>
</template>

<script>
/**
 * 阶梯价
 */
export default {
  name: 'LadderPriceDialog',

  props: {
    visible: {
      type: Boolean,
      required: true
    },
    viewRow: {
      type: Object,
      required: true
    }
  },

  data () {
    return {
      tableData: [],
      itemName: '',
      itemCode: ''
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
    }
  },

  created () {
    this.getTableData()
  },

  methods: {
    getTableData () {
      this.$http({
        url: '/api-inq/price/priceLadderPrice/vendor/listPage',
        method: 'POST',
        data: {
          priceLibraryId: this.viewRow.priceLibraryId
        },
        loading: true
      }).then((data) => {
        if (data && data.data) {
          this.tableData = (data.data.list || []).map(item => {
            return {
              ...item,
              unit: this.viewRow.unit
            }
          })
          this.itemCode = this.viewRow.itemCode
          this.itemName = this.viewRow.itemDesc
        }
      })
    }
  }
}
</script>
