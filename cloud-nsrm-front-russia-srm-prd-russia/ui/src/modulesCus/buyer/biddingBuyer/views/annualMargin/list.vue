<template>
  <el-container
    class="flex-container the_dictionary_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="queryForm"
        :pre-form-obj="preFormObj"
        @getFormData="getQuerydata"
      />
      <MainHeader>
        <template slot="left">
          <AuthorityButton
            code="bid:annualMargin:return"
            type="primary"
            :disabled="selectionList.length==0"
            @click="openRefundDialog"
          >
            {{ $t('cusEntry.bidMod.returnBond') }}
          </AuthorityButton>
          <ExportExcel
            page-url="/api-sou/ext/buyer/bid/init/listYearlyMargin"
            export-mode="front"
            :table-header="tableHeader"
            :dict-codes="dictCodes"
            :filter-params="queryParam"
            :title="$t('components.eio.customExport')"
            type="default"
          />
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :checkbox="true"
        :checkChange="checkChange"
        :comActive="$attrs['changeTab']"
        url="/api-sou/ext/buyer/bid/init/listYearlyMargin"
      />

      <!-- 保证金退回 -->
      <srm-dialog
        v-if="refundVisible"
        size="xLarge"
        :visible.sync="refundVisible"
        :title="$t('cusEntry.bidMod.refund')"
        append-to-body
        :close-on-click-modal="false"
      >
        <el-table
          :data="refundData"
          style="width: 100%"
          height="240"
          border
        >
          <el-table-column
            align="center"
            type="index"
            :label="$t('common.sort')"
            width="50"
          />
          <el-table-column
            align="center"
            prop="refundAccount"
            :label="$t('cusEntry.bidMod.refundAccount')"
            :render-header="_addStarToColumn"
            min-width="100"
          >
            <template slot-scope="scope">
              <el-input v-model="scope.row.refundAccount" />
            </template>
          </el-table-column>
          <el-table-column
            align="center"
            prop="refundAccountName"
            :label="$t('cusEntry.bidMod.refundAccountName')"
            :render-header="_addStarToColumn"
            min-width="100"
          >
            <!-- <template slot-scope="scope">
              <el-input v-model="scope.row.refundAccountName" />
            </template> -->
          </el-table-column>
          <el-table-column
            align="center"
            prop="refundBank"
            :label="$t('cusEntry.bidMod.refundBank')"
            :render-header="_addStarToColumn"
            min-width="100"
          >
            <template slot-scope="scope">
              <el-input v-model="scope.row.refundBank" />
            </template>
          </el-table-column>
          <el-table-column
            align="center"
            prop="refundBankNum"
            :label="$t('cusEntry.bidMod.refundBankNum')"
            :render-header="_addStarToColumn"
            min-width="150"
          >
            <template slot-scope="scope">
              <el-input v-model="scope.row.refundBankNum" />
            </template>
          </el-table-column>
          <el-table-column
            align="center"
            prop="refundAmount"
            :label="$t('cusEntry.bidMod.chargeAmount1')"
            :render-header="_addStarToColumn"
            min-width="150"
          >
            <template slot-scope="scope">
              <el-input
                v-model="scope.row.refundAmount"
              />
            </template>
          </el-table-column>
          <el-table-column
            prop="expectRefundTime"
            :label="$t('cusEntry.bidMod.expectRefundTime')"
            minWidth="150"
            :render-header="_addStarToColumn"
          >
            <template v-slot="scope">
              <el-date-picker
                v-model="scope.row.expectRefundTime"
                type="date"
                value-format="yyyy-MM-dd"
                :picker-options="pickerOptions"
              />
            </template>
          </el-table-column>
          <el-table-column
            align="center"
            prop="description"
            :label="$t('cusEntry.bidMod.description2')"
            :render-header="_addStarToColumn"
            min-width="120"
          >
            <template slot-scope="scope">
              <el-input v-model="scope.row.description" />
            </template>
          </el-table-column>
          <el-table-column
            align="center"
            prop="fileName"
            :label="$t('cusEntry.bidMod.attachment')"
            min-width="120"
          >
            <template slot-scope="scope">
              <SrmCommonFile
                :default-file="{
                  fileId: scope.row.fileId,
                  fileName: scope.row.fileName
                }"
                @on-change="({file}) => handleUploadSuccess(file,scope.row)"
              />
            </template>
          </el-table-column>
          <el-table-column
            align="center"
            :label="$t('common.operation')"
            width="80"
          >
            <template slot-scope="scope">
              <el-button type="text" @click="deleteRefundRow(scope.$index)">
                {{ $t('common.delete') }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <div slot="footer">
          <el-button @click="refundVisible = false">
            {{ $t('common.close') }}
          </el-button>
          <el-button type="primary" @click="confirmRefund">
            {{ $t('common.confirm') }}
          </el-button>
        </div>
      </srm-dialog>
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import ExportExcel from 'lib@/components/export-excel'
export default {
  name: 'AnnualMarginList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    ExportExcel
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      pageSize: 15,
      gridId: 'list',
      preFormObj: {},
      queryForm: [
        {
          prop: 'vendorName',
          label: () => this.$t('common.vendorName')
        },
        {
          prop: 'refundStatus',
          label: () => this.$t('cusEntry.bidMod.refundStatus'), // 退款状态
          type: 'dict',
          code: 'SOU_INT_DEPOSIT_REFUND_STATUS'
        }
      ],
      queryParam: {},
      tableHeader: [],
      tableData: [],
      selectionList: [],
      refundVisible: false,
      pickerOptions: {
        disabledDate: time => {
          const day = time.getDate()
          return !(day === 10 || day === 20 || day > 21)
        }
      },
      dictCodes: {
        refundStatus: 'SOU_INT_DEPOSIT_REFUND_STATUS'
      }
    }
  },
  created () {
    let _this = this
    _this.tableHeader = [
      {
        prop: 'vendorCode',
        label: () => this.$t('common.vendorCode'),
        minWidth: 120
      },
      {
        prop: 'vendorName',
        label: () => this.$t('common.vendorName'),
        minWidth: 120
      },
      {
        prop: 'categoryName',
        label: () => this.$t('common.category'),
        minWidth: 120
      },
      {
        prop: 'extProjectNo',
        label: () => this.$t('contractMod.sourceNumber'),
        minWidth: 150
      },
      {
        prop: 'souName',
        label: () => this.$t('cusEntry.bidMod.souName'),
        minWidth: 120
      },
      {
        prop: 'payAmount',
        label: () => this.$t('cusEntry.bidMod.payAmount'),
        minWidth: 150
      },
      {
        prop: 'chargeAmount',
        label: () => this.$t('cusEntry.bidMod.chargeAmount'),
        minWidth: 150
      },
      // 退款金额（万元）
      {
        prop: 'refundAmount',
        label: () => this.$t('cusEntry.bidMod.refundAmount'),
        minWidth: 150
      },
      // 是否退回
      {
        prop: 'refundStatus',
        label: () => this.$t('cusEntry.bidMod.refundStatus'),
        minWidth: 100,
        dataType: 'dict', // 数据类型为字典
        code: 'SOU_INT_DEPOSIT_REFUND_STATUS' // 字典code
      },
      // 退款失败原因
      {
        prop: 'refundFailCause',
        label: () => this.$t('cusEntry.bidMod.refundFailCause'),
        minWidth: 150
      }
    ]
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    checkChange (selected) {
      this.selectionList = selected
    },
    getQuerydata (v) {
      this.queryParam = v || {}
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    handleUploadSuccess (file, row) {
      const { fileId = '', fileName = '' } = file || {}
      row.fileId = fileId
      row.fileName = fileName
    },
    deleteRefundRow (index) {
      this.refundData.splice(index, 1)
    },
    // 打开保证金退回弹框
    openRefundDialog () {
      // 校验保证金状态
      let validStatus = true
      this.selectionList.some(item => {
        if (!['', 'REFUND_FAILED', 'NOT_REFUNDED'].includes(item.refundStatus)) {
          validStatus = false
          return true
        }
      })
      if (!validStatus) {
        this.$message.warning(this.$t('cusEntry.tipMessage.validRefundStatusMsg'))
        return false
      }
      this.refundData = this.selectionList.map(item => {
        const {
          payAccount,
          payAccountName,
          payBank,
          bankLine,
          ...rest
        } = item
        return {
          ...rest,
          refundAccount: payAccount,
          refundAccountName: payAccountName,
          refundBank: payBank,
          refundBankNum: bankLine,
          sourceRefundAmount: item.refundAmount
        }
      })
      this.refundVisible = true
    },
    // 确认退回
    confirmRefund () {
      let flag = this.refundData.some(item => !item.refundAccount || !item.refundAccountName || !item.refundBank ||
        !item.refundBankNum || !item.refundAmount || !item.description || !item.expectRefundTime)
      if (flag) {
        this.$message.error(this.$t('common.pleasefinishRequired'))
        return
      }
      // 校验退款金额不能大于可退金额
      let validResult = true
      this.refundData.some((item, index) => {
        if (Number(item.sourceRefundAmount) < Number(item.refundAmount)) {
          validResult = false
          return true
        }
      })
      if (!validResult) {
        this.$message.warning(this.$t('cusEntry.tipMessage.returnAmountMsg'))
        return false
      }
      const params = {
        type: 'REFUND',
        projectId: this.refundData[0].projectId,
        marginRecordList: this.refundData
      }
      this.$http({
        url: '/api-sou/ext/buyer/bid/init/editMarginRecord',
        method: 'POST',
        data: params,
        loading: true
      }).then(res => {
        this.$message.success(res.message)
        this.refundVisible = false
        this.selectionList = []
        this.getQuerydata()
      })
    }
  }
}
</script>
<style scoped lang="scss"></style>
