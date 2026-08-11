<template>
  <SrmDialog
    :title="$t('cusEntry.inq.supplierDeleteRecord')"
    :visible.sync="supplierDeleteVisible"
    size="middle"
    :close-on-click-modal="false"
  >
    <!--查询头-->
    <FormWrapper :form-array="preArr" @getFormData="getQueryData" />
    <!--表格-->
    <el-table
      border
      :data="vendorList"
      max-heigth="250"
    >
      <el-table-column
        type="index"
        width="50"
        align="center"
      />
      <el-table-column
        prop="vendorName"
        :label="$t('bidMod.vendorName')"
        min-width="120"
        align="center"
        show-overflow-tooltip
      />
      <el-table-column
        prop="vendorCode"
        :label="$t('bidMod.vendorCode')"
        min-width="120"
        align="center"
        show-overflow-tooltip
      />
      <el-table-column
        prop="delReason"
        :label="$t('cusEntry.inq.supplierDelReason')"
        min-width="120"
        align="center"
        show-overflow-tooltip
      />
    </el-table>
    <!--分页-->
    <CPagination
      :total="pagination.total"
      :page-num="pagination.pageNum"
      :page-size="pagination.pageSize"
      @current-change="paginationCurrentChange"
      @size-change="paginationSizeChange"
    />
  </SrmDialog>
</template>

<script>
import FormWrapper from 'lib@/components/Table/FormWrapper'
import CPagination from 'lib@/components/c-pagination/index.vue'
import { inqBuyerHttp } from 'modcb@/inquiry/api'
export default {
  name: 'SupplierDeleteRecordDialog',
  components: {
    FormWrapper,
    CPagination
  },
  props: {
    /* 弹窗显隐藏 */
    visible: {
      type: Boolean,
      default: false
    },
    /* 项目Id */
    projectId: {
      type: [Number, String],
      default: null
    }
  },
  data () {
    return {
      preArr: [
        {
          prop: 'vendorCode',
          label: this.$t('bidMod.vendorCode')
        },
        {
          prop: 'vendorName',
          label: this.$t('bidMod.vendorName')
        }
      ],
      pagination: {
        pageSize: 15,
        pageNum: 1,
        total: 0
      },
      vendorList: [],
      queryParams: {}
    }
  },
  computed: {
    supplierDeleteVisible: {
      get () {
        return this.visible
      },
      set (value) {
        this.$emit('update:visible', value)
      }
    }
  },
  watch: {
    visible: {
      immediate: true,
      handler (newValue, oldValue) {
        if (newValue) {
          this.getQueryData()
        }
      }
    }
  },
  methods: {
    /* 查询 */
    getQueryData (params) {
      this.queryParams = params
      const {
        pageSize,
        pageNum
      } = this.pagination
      const queryParam = {
        projectId: this.projectId,
        ...params,
        pageSize,
        pageNum
      }
      inqBuyerHttp.init.readDeleteSupplier(queryParam).then(res => {
        if (res.data) {
          this.vendorList = res.data.list || []
          this.pagination.total = parseInt(res.data.total)
        }
      })
    },
    /* 页码改变 */
    paginationCurrentChange (pageNum) {
      this.pagination.pageNum = pageNum
      this.getQueryData(this.queryParams)
    },
    /* 页条数改变 */
    paginationSizeChange (pageSize) {
      this.pagination.pageSize = pageSize
      this.getQueryData(this.queryParams)
    }
  }
}
</script>
