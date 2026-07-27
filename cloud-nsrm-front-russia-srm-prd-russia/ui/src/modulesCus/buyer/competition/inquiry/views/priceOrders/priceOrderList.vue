<template>
  <el-container class="flex-container" direction="vertical">
    <el-main>
      <FormWrapper :form-array="preArr" @getFormData="getQueryData" />
      <MainHeader>
        <template slot="left">
          <ExportExcel
            :page-url="pageUrl"
            export-mode="front"
            :table-header="tableHeader"
            :dict-codes="dictCodes"
            :filter-params="queryParams"
            :title="$t('components.eio.customExport')"
            type="default"
          />
          <AuthorityButton
            type="primary"
            code="inq:priceOrderList:add"
            @click="edit({}, 'add')"
          >
            {{ $t('cusEntry.common.createPriceOrder') }}
          </AuthorityButton>
        </template>
      </MainHeader>
      <TableView
        :ref="gridList"
        :table-header="tableHeader"
        :page-size="pageSize"
        :pre-query-data="queryParams"
        open-custom-table
        :auto-query="true"
        :com-active="$attrs['changeTab']"
        :url="pageUrl"
      />
    </el-main>
  </el-container>
</template>

<script>
import { inqBuyerHttp } from 'modcb@/inquiry/api'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import priceOrderDetail from './priceOrderDetail'
import { tabTodoWatch } from '@/utils/mixins'
import ExportExcel from 'lib@/components/export-excel'
export default {
  name: 'PriceOrderList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    ExportExcel
  },
  mixins: [tabTodoWatch],
  data () {
    return {
      preArr: [],
      gridList: 'priceOrderList',
      tableHeader: [],
      pageSize: 15,
      queryParams: {},
      pageUrl: inqBuyerHttp.price.listPageUrl,
      dictCodes: {
        fixPriceStatus: 'EXT_FIX_PRICE_STATUS'
      }
    }
  },
  created () {
    this.preArr = [
      {
        prop: 'fixPriceNo',
        label: this.$t('cusEntry.inq.priceOrderNo')
      },
      {
        prop: 'fixPriceStatus',
        label: this.$t('cusEntry.inq.priceOrderState'),
        type: 'dict',
        code: 'EXT_FIX_PRICE_STATUS'
      },
      {
        prop: 'orgDepName',
        label: this.$t('cusEntry.inq.purchaseDepartment')
      },
      {
        prop: 'creationDate',
        label: this.$t('cusEntry.inq.creationDate'),
        type: 'date'
      },
      {
        prop: 'itemCode',
        label: this.$t('bidMod.itemCode')
      },
      {
        prop: 'itemDesc',
        label: this.$t('bidMod.itemDesc')
      },
      // 创建人
      {
        prop: 'createdBy',
        label: this.$t('bidMod.creator'),
        type: 'quicksearch',
        propKey: 'username',
        showKey: 'nickname',
        name: 'scc_rbac_user_display'
      }
    ]
    this.tableHeader = [
      {
        prop: 'fixPriceNo',
        label: this.$t('cusEntry.inq.priceOrderNo'),
        minWidth: 120,
        showType: 'button',
        btnStyle: 'text',
        callback: row => this.edit(row, 'view')
      },
      {
        prop: 'fixPriceStatus',
        label: this.$t('cusEntry.inq.priceOrderState'),
        minWidth: 120,
        dataType: 'dict',
        code: 'EXT_FIX_PRICE_STATUS'
      },
      {
        prop: 'fixPriceDate',
        label: this.$t('cusEntry.inq.applyDate'),
        minWidth: 120
      },
      {
        prop: 'orgDepName',
        label: this.$t('cusEntry.inq.purchaseDepartment'),
        minWidth: 120
      },
      {
        prop: 'totalNotaxPrice',
        label: this.$t('cusEntry.inq.totalAmount'),
        minWidth: 120
      },
      {
        prop: 'totalTaxPrice',
        label: this.$t('cusEntry.inq.totalPriceAndTax'),
        minWidth: 120
      },
      {
        prop: 'highestTaxPrice',
        label: this.$t('cusEntry.inq.topPrice'),
        minWidth: 120
      },
      {
        prop: 'creationDate',
        label: this.$t('cusEntry.inq.creationDate'),
        minWidth: 120
      },
      {
        prop: 'createdFullName',
        label: this.$t('cusEntry.inq.createdBy'),
        minWidth: 120
      },
      {
        prop: 'operation',
        label: this.$t('common.operation'),
        showType: 'buttons',
        btnStyle: 'text',
        width: 150,
        fixed: 'right',
        buttons: [
          {
            show: row => ['DRAFT', 'WITHDRAW', 'REJECTED'].includes(row.fixPriceStatus),
            callback: row => this.edit(row, 'edit'),
            formattor: () => {
              return this.$t('common.edit')
            }
          },
          {
            show: row => ['DRAFT', 'WITHDRAW', 'REJECTED'].includes(row.fixPriceStatus),
            callback: row => this.delete(row),
            formattor: () => {
              return this.$t('common.delete')
            }
          },
          {
            show: row => row.fixPriceStatus === 'SUBMITTED',
            callback: row => this.undo(row),
            formattor: () => {
              return this.$t('common.recall')
            }
          }
        ]
      }
    ]
  },
  methods: {
    /* 查询列表数据 */
    getQueryData (params) {
      this.queryParams = params
      this.$nextTick(() => {
        this.$refs[this.gridList].query()
      })
    },
    /* 编辑 */
    edit (row, type) {
      const tabName = type === 'add' ? 'priceOrderDetail' : `priceOrderDetail${row.fixPriceNo}`
      const title = type === 'add' ? this.$t('cusEntry.inq.priceOrderAdd') : row.fixPriceNo
      this.$emit('tab-add', {
        component: priceOrderDetail,
        name: tabName,
        params: {
          type,
          tabName,
          row
        },
        title
      })
    },
    /* 删除 */
    delete (row) {
      inqBuyerHttp.price.delete(row.fixPriceHeadId).then(res => {
        this.$message.success(this.$t('common.successDelete'))
        this.getQueryData(this.queryParams)
      })
    },
    /* 撤回 */
    undo (row) {
      this.$prompt('', this.$t('cusEntry.competition.undoReason'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        inputType: 'textarea',
        inputValidator: (value) => {
          if (!value) {
            return this.$t('cusEntry.tipMessage.undoReason')
          }
          return true
        }
      }).then(({ value }) => {
        const data = {
          bussinessType: 'EXT_SOU_FIX_PRICE',
          dataId: row.fixPriceHeadId,
          commentmsg: value
        }
        inqBuyerHttp.price.undo(data).then(res => {
          if (res.data) {
            this.$message.success(this.$t('cusEntry.tipMessage.recallSuccess'))
            this.getQueryData(this.queryParams)
          }
        })
      }).catch(() => {})
    }
  }
}
</script>
