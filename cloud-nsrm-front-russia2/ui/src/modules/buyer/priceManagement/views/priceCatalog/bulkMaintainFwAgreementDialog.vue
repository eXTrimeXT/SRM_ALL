<template>
  <srm-dialog
    :title="$t('bidMod.bulkMaintainFwAgreement')"
    size="middle"
    :visible.sync="dialogVisible"
    append-to-body
  >
    <el-form
      ref="sumForm"
      :model="sumForm"
      label-width="80px"
    >
      <srm-row type="flex">
        <srm-col :init-col="2">
          <el-form-item :label="$t('common.vendor')">
            <el-input
              v-model="sumForm.vendorName"
              disabled
            />
          </el-form-item>
        </srm-col>
        <srm-col
          :init-col="3"
          style="padding-left: 22px"
        >
          <el-form-item :label="$t('bidMod.isFrameworkAgreement')">
            <el-checkbox
              v-model="sumForm.isFrameworkAgreement"
              true-label="Y"
              false-label="N"
              disabled
            />
          </el-form-item>
        </srm-col>
        <srm-col
          :init-col="6"
          style="text-align: right"
        >
          <el-button
            type="primary"
            @click="queryContractData2"
          >
            {{ $t('common.search') }}
          </el-button>
        </srm-col>
      </srm-row>
    </el-form>

    <el-table
      ref="catSelector2"
      style="width: 100%"
      height="311px"
      border
      highlight-current-row
      :data="contractDataList"
    >
      <el-table-column
        align="center"
        type="index"
        width="50"
      />
      <el-table-column
        prop="contractName"
        min-width="200"
        align="center"
        :label="$t('contractMod.contractName')"
        show-overflow-tooltip
      />
      <el-table-column
        :label="$t('common.operation')"
        width="60"
        align="center"
      >
        <template v-slot="scope">
          <el-button
            type="text"
            @click="saveContractCode(scope.$index, scope.row)"
          >
            {{ $t('common.save') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </srm-dialog>
</template>

<script>
/**
 * 批量维护框架协议
 */
export default {
  name: 'BulkMaintainFwAgreementDialog',

  props: {
    visible: {
      type: Boolean,
      required: true
    },
    currentRows: {
      type: Array,
      required: true
    }
  },

  data () {
    return {
      sumForm: {
        vendorId: null,
        vendorCode: null,
        vendorName: null,
        isFrameworkAgreement: 'Y',
        pageSize: 9999,
        pageNum: 1
      },
      contractDataList: [],
      globalPriceLibraryIds: []
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
    this.sumForm.vendorId = this.currentRows[0].vendorId
    this.sumForm.vendorName = this.currentRows[0].vendorName
    this.globalPriceLibraryIds = this.currentRows.map((v) => v.priceLibraryId)
    this.queryContractData2()
  },

  methods: {
    /* 查询数据 */
    queryContractData2 () {
      this.$http({
        url: '/api-cm/contract/contractHead/listContractHeadByIsMainAndVendorId',
        method: 'POST',
        data: this.sumForm,
        loading: true
      }).then((res) => {
        this.contractDataList = res.data.list
      })
    },

    /* 提交保存 */
    saveContractCode (index, row) {
      this.$http({
        url: '/api-inq/price/priceLibrary/batchSetContractCode',
        method: 'POST',
        data: {
          contractCode: row.contractCode,
          contractName: row.contractName,
          priceLibraryIds: this.globalPriceLibraryIds
        },
        loading: true
      }).then(() => {
        this.$message.success(this.$t('common.success'))
        this.dialogVisible = false
        this.$emit('saveSuccess')
      })
    }
  }
}
</script>
