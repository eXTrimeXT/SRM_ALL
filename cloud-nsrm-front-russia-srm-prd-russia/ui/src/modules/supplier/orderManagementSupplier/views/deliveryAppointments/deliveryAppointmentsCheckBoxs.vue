<template>
  <!-- 选择父级菜单弹框 -->
  <srm-dialog
    :title="$t('orderMod.selDeliveryNote')"
    size="large"
    :destroy-on-close="true"
    :visible.sync="visible"
    :close-on-click-modal="false"
    v-bind="$attrs"
    v-on="$listeners"
  >
    <div class="search-content">
      <FormWrapper
        :form-array="preArr"
        form-label-width="120px"
        @getFormData="getQuerydata"
      />
      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <el-button
            type="primary"
            :disabled="!selection.length"
            @click="comfirmSelect"
          >
            {{ $t("common.confirm") }}
          </el-button>
          <el-button @click="cancleHandle">
            {{ $t("common.cancel") }}
          </el-button>
        </template>
      </MainHeader>
      <!-- 列表 -->
      <div class="porg-table">
        <el-table
          v-if="visible"
          ref="parentOrgTable"
          border
          :height="260"
          :data="parentOrgTableData"
          tooltip-effect="dark"
          style="width: 100%"
          @selection-change="handleSelectionChange"
        >
          <el-table-column
            type="selection"
            width="55"
          />
          <el-table-column
            type="index"
            :label="$t('common.sort')"
            width="60"
          />
          <el-table-column
            prop="deliveryNumber"
            :label="$t('orderMod.buyerOrderSynergy.deliveryNumber')"
            min-width="150"
          />
          <el-table-column
            prop="deliveryDate"
            :label="$t('orderMod.buyerOrderSynergy.entryTime')"
            min-width="100"
            :formatter="formatDate"
          />
          <el-table-column
            prop="comments"
            show-overflow-tooltip
            :label="$t('common.remark')"
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
  </srm-dialog>
</template>
<script>
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import CPagination from 'lib@/components/c-pagination'
import { parseTime } from '@/utils'
import { deliveryAppointmentsApi } from 'mods@/orderManagementSupplier/api'

export default {
  name: 'DeliveryAppointmentsCheckBoxs',
  components: { MainHeader, FormWrapper, CPagination },
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    id: {
      type: Array,
      default: []
    }
  },
  data () {
    return {
      preArr: [
        {
          prop: 'deliveryNumber',
          label: () => this.$t('orderMod.buyerOrderSynergy.deliveryNumber')
        },
        {
          prop: 'startDeliveryDate',
          label: () => this.$t('orderMod.startDeliveryDate'),
          type: 'date'
        },
        {
          prop: 'endDeliveryDate',
          label: () => this.$t('orderMod.endDeliveryDate'),
          type: 'date'
        }
      ],
      queryParams: {},
      selection: [],
      parentOrgTableData: [],
      orgTypeList: [],
      parentOrgTableDataPage: {
        total: 0,
        pageNum: 1,
        pageSize: 20
      },
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
  mounted () {
    this.searchParentOrg(true)
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
    getQuerydata (v) {
      this.queryParams = v || {}
      this.searchParentOrg(true)
    },
    searchParentOrg (isFirst = false) {
      const data = isFirst
        ? { pageNum: 1, pageSize: 10 }
        : this.parentOrgQueryForm
        deliveryAppointmentsApi.deliveryNoteList({
        ...data,
        ...this.queryParams
      }).then(data => {
        const { list, pageNum = 0, pageSize = 0, total } = data.data
        this.parentOrgTableData = list
        this.parentOrgTableDataPage = { pageNum, pageSize, total }
        if (this.id.length) {
          this.id.forEach(item => {
            const selection = list.find(i => i.deliveryNoteId === item)
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
      this.selection = selection
    },
    cancleHandle () {
      this.visible = false
    }
  }
}
</script>
