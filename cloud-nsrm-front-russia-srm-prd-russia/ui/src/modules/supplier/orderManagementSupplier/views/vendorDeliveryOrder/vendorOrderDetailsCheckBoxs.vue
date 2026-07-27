<template>
  <!-- 选择父级菜单弹框 -->
  <srm-dialog
    :title="$t('orderMod.selOrderDetail')"
    size="large"
    :show-close="false"
    :destroy-on-close="true"
    :visible.sync="visible"
    :close-on-click-modal="false"
  >
    <div class="search-content">
      <!-- <el-form ref="orgform" :model="parentOrgQueryForm" label-width="100">
        <el-row :gutter="32">
          <el-col :span="10">
          </el-col>
          <el-col :span="4">
            <el-button type="primary" @click="searchParentOrg">查询</el-button>
          </el-col>
        </el-row>
      </el-form> -->
      <!-- 列表 -->
      <div class="porg-table">
        <el-table
          v-if="visible"
          ref="parentOrgTable"
          border
          :data="parentOrgTableData"
          tooltip-effect="dark"
          style="width: 100%"
          max-height="300px"
          @selection-change="handleSelectionChange"
        >
          <el-table-column
            type="selection"
            width="55"
          />
          <el-table-column
            prop="orderNumber"
            :label="$t('orderMod.buyerOrderSynergy.orderNumber')"
            width="150px"
          />
          <el-table-column
            prop="lineNum"
            :label="$t('orderMod.buyerOrderSynergy.orderLineNum')"
            width="150px"
          />
          <el-table-column
            prop="categoryName"
            :label="$t('orderMod.buyerOrderSynergy.categoryName')"
          />
          <el-table-column
            prop="materialCode"
            :label="$t('orderMod.buyerOrderSynergy.materialCode')"
          />
          <el-table-column
            prop="materialName"
            :label="$t('common.materialName')"
            width="150px"
          />
          <el-table-column
            prop="orderNum"
            :label="$t('orderMod.buyerOrderSynergy.orderNum')"
          />
          <el-table-column
            prop="receiveSum"
            :label="$t('orderMod.buyerOrderSynergy.receiveSum')"
            width="140px"
          />
          <el-table-column
            prop="inventoryPlace"
            :label="$t('orderMod.buyerOrderSynergy.inventoryPlace')"
          />
          <el-table-column
            prop="requirementDate"
            :label="$t('orderMod.buyerOrderSynergy.requirementDateStr')"
            :formatter="formatDate"
            min-width="100"
          />
        </el-table>
        <CPagination
          :total="parentOrgTableDataPage.total"
          :page-num="parentOrgTableDataPage.pageNum"
          :page-size="parentOrgTableDataPage.pageSize"
          @current-change="parentDataCurrentChange"
          @size-change="parentDataSizeChange"
        />
      </div>
    </div>
    <div
      slot="footer"
      class="dialog-footer"
    >
      <el-button @click="cancleHandle">
        {{ $t("common.cancel") }}
      </el-button>
      <el-button
        type="primary"
        @click="comfirmSelect"
      >
        {{
          $t("common.confirm")
        }}
      </el-button>
    </div>
  </srm-dialog>
</template>
<script>
import CPagination from 'lib@/components/c-pagination'
import { getDictItemList } from '@/api/common'
import { adaptDictData, parseTime } from '@/utils'
import { deliveryOrderApi } from 'mods@/orderManagementSupplier/api'

export default {
  name: 'VendorOrderDetailsCheckBoxs',
  components: { CPagination },
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    id: {
      type: Array,
      default: () => []
    },
    queryParams: {
      type: Object,
      default: {}
    }
  },
  data () {
    return {
      selection: null,
      parentOrgTableData: [],
      orgTypeList: [],
      parentOrgTableDataPage: {
        total: 0,
        pageNum: 1,
        pageSize: 20
      },
      deliveryLevelOpts: [],
      parentOrgQueryForm: {
        pageNum: 1,
        pageSize: 10
      }
    }
  },
  watch: {
    visible (oldValue, newValue) {
      if (!newValue) {
        this.searchParentOrg(true)
      }
    }
  },
  created () {
    this.$nextTick(() => {
      this.searchParentOrg(true)
    })
  },
  mounted () {
    // 字典信息查询
    const dictionaryCodes = [
      { dictCode: 'DELIVERY_LEVEL' }
      // { dictCode: "ORDER_STATUS" }
    ]
    getDictItemList(dictionaryCodes).then(res => {
      const [DELIVERY_LEVEL] = res.data
      const deliveryLevelOpts = adaptDictData(DELIVERY_LEVEL.DELIVERY_LEVEL)
      this.deliveryLevelOpts = deliveryLevelOpts
    })
  },
  methods: {
    formatDate (row, column, cellValue, index) {
      return cellValue ? parseTime(cellValue, '{y}-{m}-{d}') : ''
    },
    parentDataCurrentChange (num) {
      this.parentOrgQueryForm.pageNum = num
      this.searchParentOrg()
    },
    parentDataSizeChange (size) {
      this.parentOrgQueryForm.pageSize = size
      this.searchParentOrg()
    },
    searchParentOrg (isFirst = false) {
      const data = isFirst
        ? { pageNum: 1, pageSize: 10 }
        : this.parentOrgQueryForm
        deliveryOrderApi.listUnDeliveryPage({
        ...data,
        ...this.queryParams,
        orderStatus: 'ACCEPT',
        jitOrder: 'N'
      }).then(data => {
        const { list, pageNum = 0, pageSize = 0, total } = data.data
        this.parentOrgTableData = list
        this.parentOrgTableDataPage = { pageNum, pageSize, total }
        if (this.id.length) {
          this.id.forEach(item => {
            const selection = list.find(i => i.orderDetailId === item)
            if (selection) {
              setTimeout(() => {
                if (this.$refs.parentOrgTable) {
                  this.$refs.parentOrgTable.toggleRowSelection(selection, true)
                }
              }, 100)
            }
          })
        }
      })
    },
    comfirmSelect () {
      this.$emit('on-ok', this.selection)
    },
    handleSelectionChange (selection) {
      console.log(selection)
      this.selection = selection
    },
    cancleHandle () {
      this.$emit('on-cancle')
    }
  }
}
</script>
