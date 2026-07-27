<template>
  <SrmDialog
    title="联系人查重"
    size="fullscreen"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
    class="riskDialog"
  >
    <BaseTable
      stripe
      index
      :data="riskData"
      :columns="riskColumns"
      :empty-text="$t('components.noData')"
      border
      :cell-class-name="setCellClass"
    />
    <div class="abnormal-info">
      异常详情
    </div>
    <BaseTable
      stripe
      index
      :data="abnormalData"
      :columns="abnormalColumns"
      :empty-text="$t('components.noData')"
      border
    />
    <div slot="footer" class="dialog-footer">
      <!-- <el-button @click="dialogVisible = false">
        {{ $t("common.cancel") }}
      </el-button> -->
    </div>
  </SrmDialog>
</template>
<script>
import BaseTable from 'lib@/components/BaseTable'
import TableView from 'lib@/components/Table/TableView'
import { transformMQL } from 'lib@/utils/util'
import { bidBuyerHttp } from 'modcb@/biddingBuyer/api'

export default {
  name: 'InviteDialog',
  components: {
    TableView,
    BaseTable
  },
  props: {
    applicantNo: {
      type: String,
      default: ''
    },
    projectId: {
      type: [String, Number],
      default: ''
    },
    idList: {
      type: Array,
      default: () => []
    },
    nameList: {
      type: Array,
      default: () => []
    },
    visible: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      riskCount: 0,
      myDate: [],
      riskColumns: [
        {
          attrs: {
            label: '序号',
            type: 'index',
            width: 60,
            fixed: 'left'
          }
        },
        {
          attrs: {
            prop: 'vendorCode',
            label: '供应商编码',
            minWidth: 120,
            fixed: 'left',
            showOverflowTooltip: true
          }
        },
        {
          attrs: {
            prop: 'vendorName',
            label: '推荐供应商',
            minWidth: 150,
            fixed: 'left',
            showOverflowTooltip: true
          }
        },
        {
          attrs: {
            prop: 'contackRepeatFlag',
            label: '联系人是否重复',
            minWidth: 120,
            formatter: (row, column, cellValue) => this.$getDictLabel('YES_OR_NO', cellValue)
          }
        },
        {
          attrs: {
            prop: 'telRepeatFlag',
            label: '联系人电话是否重复',
            minWidth: 100,
            formatter: (row, column, cellValue) => this.$getDictLabel('YES_OR_NO', cellValue)
          }
        },
        {
          attrs: {
            prop: 'emailRepeatFlag',
            label: '联系人邮箱是否重复',
            minWidth: 100,
            formatter: (row, column, cellValue) => this.$getDictLabel('YES_OR_NO', cellValue)
          }
        }
      ],
      riskData: [],
      abnormalColumns: [
        {
          attrs: {
            type: 'index',
            label: '序号',
            width: 60
          }
        },
        {
          attrs: {
            prop: 'type',
            label: '异常类型',
            showOverflowTooltip: true
          }
        },
        {
          attrs: {
            prop: 'description',
            label: '异常说明',
            showOverflowTooltip: true
          }
        }
      ],
      abnormalData: []
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
  watch: {
    visible: {
      handler (nVal) {
        if (nVal) {
          this.getFormDetail()
        }
      },
      immediate: true,
      deep: true
    }
  },
  methods: {
    setCellClass ({ row, column, rowIndex, columnIndex }) {
      if (row[column.property] === 'Y') return 'red'
    },
    async getFormDetail () {
      const response = await bidBuyerHttp.control.findDuplicatePhone(this.projectId)
      const { riskItemList = [], vendorRiskList = [] } = response.data
      this.riskData = vendorRiskList || []
      this.abnormalData = riskItemList || []
      if (this.riskData.length) {
        let count = 0
        for (let item of this.riskData) {
          for (let key in item) {
            if (item[key] === 'Y') {
              count++
            }
          }
        }
        this.riskCount = count
      }
    }
  }
}
</script>
<style style="scss" scoped>
  .my_chart {
    width: 800px;
    position: relative;
    margin-top: 20px;
  }

  .my_chart .my_chart_line {
    width: 100%;
  }

  .my_chart .my_chart_line .chat_line_item {
    height: 60px;
    display: flex;
    align-items: center;
  }

  .my_chart .my_chart_line .chat_line_item .chart_line_name {
    width: 60px;
    text-align: right;
    margin-right: 5px;
  }

  .my_chart .my_chart_line .chat_line_item .chart_line_content {
    width: calc(100% - 60px);
    height: 1px;
    background: #000;
  }

  .my_chart_content {
    position: absolute;
    top: 0;
    width: 100%;
    height: 100%;
    padding-left: 60px;
    overflow-x: auto;
  }

  .my_chart_content .chart_content_inner {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: flex-end;
    justify-content: center;
    gap: 20px;
  }

  .my_chart_content .chart_content_item {

  }

  .my_chart_content .chart_content_item .content_item_child {
    display: flex;
    align-items: flex-end;
    justify-content: space-around;
    gap: 10px;
    padding: 0 30px;
  }

  .my_chart_content .chart_content_item .content_item_child .item_child_inner {
    width: 20px;
  }
  .chart_popover{
    display: flex;
    align-items: center;
    margin-bottom: 10px;
  }
  .chart_popover:last-child{
    margin-bottom: 0;
  }
  .chart_popover .chart_popover_inner {
    width: 12px;
    height: 12px;
    border-radius: 50%;
  }

  .red {
    color: red;
  }

  .risk-info {
    margin-bottom: 10px;
  }

  .abnormal-info {
    font-size: 14px;
    font-weight: bold;
    margin: 10px 0;
  }
</style>
<style style="scss">
  .red .cell {
    color: red;
  }

  .riskDialog .srm-dialog-content {
    max-height: 100% !important;
  }

  .riskDialog .el-dialog__footer {
    height: 0 !important;
  }
</style>
