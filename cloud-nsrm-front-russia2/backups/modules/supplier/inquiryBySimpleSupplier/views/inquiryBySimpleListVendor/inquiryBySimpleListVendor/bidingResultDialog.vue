<template>
  <srm-dialog
    title="查看中标结果"
    size="middle"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
    append-to-body
  >
    <FormWrapper
      :form-array="formWrapperConfig"
      form-label-width="120px"
      :col-length="2"
      @getFormData="getQueryData"
    />

    <el-table
      :data="bidingResultTable"
      style="width: 100%"
      border
      height="260px"
      highlight-current-row
    >
      <el-table-column align="center" type="index" width="50" />
      <!--t 轮次-->
      <el-table-column
        sortable
        align="center"
        prop="round"
        :label="$t('bidMod.bidingRound')"
        width="70"
      />

      <!--t 物料编码-->
      <el-table-column
        align="center"
        prop="itemCode"
        :label="$t('bidMod.itemCode')"
        min-width="120"
        show-overflow-tooltip
        :formatter="(row, column, value) => targetNumRevealFilter(value)"
      />

      <!--t 物料名称-->
      <el-table-column
        align="center"
        prop="itemDesc"
        :label="$t('bidMod.itemName')"
        min-width="150"
        show-overflow-tooltip
      />

      <!--t 本轮入围情况-->
      <el-table-column
        align="center"
        prop="selectStatus"
        label="本轮入围情况"
        width="100"
        :formatter="(row, column, value) => $getDictLabel('INQ_SELECT_STATUS', value)"
        show-overflow-tooltip
      />
      <!--t 评选情况-->
      <el-table-column
        align="center"
        prop="selectResult"
        :label="$t('bidMod.selectSituation')"
        width="100"
        :formatter="(row, column, value) => $getDictLabel('INQ_SELECT_RESULT', value) "
        show-overflow-tooltip
      />
    </el-table>

    <CPagination
      :total="pagination.total"
      :page-num="pagination.pageNum"
      :page-size="pagination.pageSize"
      @current-change="paginationCurrentChange"
      @size-change="paginationSizeChange"
    />

    <div slot="footer" class="dialog-footer">
      <el-button @click="dialogVisible = false">
        {{ $t('common.cancel') }}
      </el-button>
    </div>
  </srm-dialog>
</template>

<script>
/**
 * 供应商查看中标结果
 */
import FormWrapper from '@/library/components/Table/FormWrapper'
import CPagination from '@/library/components/c-pagination'
import { maxNumberOption } from 'lib@/composition/commonComposition'
import { targetNumReveal } from 'lib@/composition/origin/composition'

export default {
  name: 'BidingResultDialog',
  components: {
    FormWrapper,
    CPagination
  },
  props: {
    visible: Boolean,
    viewRow: Object
  },
  data () {
    return {
      pagination: {
        total: 0,
        pageNum: 1,
        pageSize: 15
      },
      queryParam: {},
      bidingResultTable: [],
      formWrapperConfig: [
        // 物料编码
        { prop: 'itemCode', label: () => this.$t('bidMod.itemCode') },
        // 轮次
        {
          prop: 'round',
          label: this.$t('bidMod.bidingRound'),
          type: 'select',
          options: () => this.roundOption
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
    roundOption () {
      return maxNumberOption(this.viewRow.inqRound)
    }
  },
  watch: {
    dialogVisible: {
      handler (newValue, oldValue) {
        if (newValue && !oldValue) {
          this.getBidingResultData()
        }
      },
      immediate: true
    }
  },
  methods: {
    /* 物料编码格式化 */
    targetNumRevealFilter (value) {
      return targetNumReveal(value)
    },
    /* 查询参数 */
    getQueryData (val) {
      this.queryParam = val
      this.$nextTick(() => {
        this.getBidingResultData()
      })
    },
    /* 查询列表详情 */
    getBidingResultData () {
      if (!this.viewRow.inquiryId) return

      const paramsData = {
        ...this.queryParam,
        inquiryId: this.viewRow.inquiryId,
        // 分页
        pageNum: this.pagination.pageNum,
        pageSize: this.pagination.pageSize
      }
      this.$api.inq.inquiryBySimple.getBidingResultData(paramsData).then(data => {
        if (data && data.data) {
          this.bidingResultTable = data.data.list || []
          this.pagination.total = data.data.total
        }
      })
    },
    /* 分页改变 */
    paginationCurrentChange (num) {
      this.pagination.pageNum = num
      this.getBidingResultData()
    },
    /* 分页大小改变 */
    paginationSizeChange (size) {
      this.pagination.pageSize = size
      this.getBidingResultData()
    }
  }
}
</script>
