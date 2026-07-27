<template>
  <div>
    <el-button type="primary" :disabled="disabledFlag" @click="addInvoiceInfo">
      {{ $t('bidMod.affairsIncreased') }}
    </el-button>
    <el-table
      class="mt-10"
      :data="value"
      style="width: 100%"
      border
      max-height="251px"
      @cell-mouse-enter="mouseEnterInvoice"
      @cell-mouse-leave="mouseLeaveInvoice"
    >
      <!-- 序号 -->
      <el-table-column
        align="center"
        type="index"
        width="60"
        fixed="left"
        :label="$t('common.sort')"
      />
      <!-- 发票影像 -->
      <el-table-column
        align="center"
        prop="fileSourceName"
        width="80"
        :label="$t('purSettlementMod.fileSourceName2')"
        show-overflow-tooltip
      >
        <template slot-scope="scope">
          <el-button type="text" @click="isFileSource = true">
            {{ scope.row.fileSourceName }}
          </el-button>
          <filePreview
            vWidth="60%"
            vHeight="400"
            :visible="isFileSource"
            :fileupload-id="scope.row.fileuploadId"
            :file-name="scope.row.fileSourceName.split('.')[0]"
            @cancel="isFileSource = false"
          />
        </template>
      </el-table-column>
      <!-- 增值税发票类型 -->
      <el-table-column
        align="center"
        prop="invoiceType"
        width="120"
        :label="$t('purSettlementMod.invoiceType')"
        show-overflow-tooltip
      />
      <!-- 采购方税号 -->
      <el-table-column
        align="center"
        prop="purchaserRegisterNum"
        width="120"
        :label="$t('purSettlementMod.purchaserRegisterNum')"
        show-overflow-tooltip
      >
        <template slot-scope="scope">
          <el-input
            v-if="scope.row.editabled"
            v-model="scope.row.purchaserRegisterNum"
            :disabled="disabledFlag"
            @input="onExchange(scope.$index)"
          />
          <span v-else-if="!scope.row.editabled">{{ scope.row.purchaserRegisterNum }}</span>
        </template>
      </el-table-column>
      <!-- 发票代码 -->
      <el-table-column
        align="center"
        prop="invoiceCode"
        width="120"
        :label="$t('purSettlementMod.invoiceCode')"
        show-overflow-tooltip
      >
        <template slot-scope="scope">
          <el-input
            v-if="scope.row.editabled"
            v-model="scope.row.invoiceCode"
            :disabled="disabledFlag"
            @input="onExchange(scope.$index)"
          />
          <span v-else-if="!scope.row.editabled">{{ scope.row.invoiceCode }}</span>
        </template>
      </el-table-column>
      <!-- 发票号码 -->
      <el-table-column
        align="center"
        prop="invoiceNum"
        width="120"
        :label="$t('purSettlementMod.invoiceNum')"
        show-overflow-tooltip
      >
        <template slot-scope="scope">
          <el-input
            v-if="scope.row.editabled"
            v-model="scope.row.invoiceNum"
            :disabled="disabledFlag"
            @input="onExchange(scope.$index)"
          />
          <span v-else-if="!scope.row.editabled">{{ scope.row.invoiceNum }}</span>
        </template>
      </el-table-column>
      <!-- 开票日期 -->
      <el-table-column
        align="center"
        prop="invoiceDate"
        width="120"
        :label="$t('purSettlementMod.invoiceDate2')"
        show-overflow-tooltip
      >
        <template slot-scope="scope">
          <el-date-picker
            v-if="scope.row.editabled"
            v-model="scope.row.invoiceDate"
            :disabled="disabledFlag"
            type="date"
            :format="$formatDatePicker"
            value-format="yyyy-MM-dd"
            @input="onExchange(scope.$index)"
            @blur="setNotEditabled(scope.row)"
          />
          <span v-else-if="!scope.row.editabled">{{ $parseTime(scope.row.invoiceDate) }}</span>
        </template>
      </el-table-column>
      <!-- 校验码 -->
      <el-table-column
        align="center"
        prop="checkCode"
        width="120"
        :label="$t('purSettlementMod.checkCode')"
        show-overflow-tooltip
      >
        <template slot-scope="scope">
          <el-input
            v-if="scope.row.editabled"
            v-model="scope.row.checkCode"
            :disabled="disabledFlag"
            @input="onExchange(scope.$index)"
          />
          <span v-else-if="!scope.row.editabled">{{ scope.row.checkCode }}</span>
        </template>
      </el-table-column>
      <!-- 采购方 -->
      <el-table-column
        align="center"
        prop="purchaserName"
        width="120"
        :label="$t('purSettlementMod.purchaserName')"
        show-overflow-tooltip
      >
        <template slot-scope="scope">
          <el-input
            v-if="scope.row.editabled"
            v-model="scope.row.purchaserName"
            :disabled="disabledFlag"
            @input="onExchange(scope.$index)"
          />
          <span v-else-if="!scope.row.editabled">{{ scope.row.purchaserName }}</span>
        </template>
      </el-table-column>
      <!-- 供方 -->
      <el-table-column
        align="center"
        prop="sellerName"
        width="120"
        :label="$t('purSettlementMod.sellerName2')"
        show-overflow-tooltip
      >
        <template slot-scope="scope">
          <el-input
            v-if="scope.row.editabled"
            v-model="scope.row.sellerName"
            :disabled="disabledFlag"
            @input="onExchange(scope.$index)"
          />
          <span v-else-if="!scope.row.editabled">{{ scope.row.sellerName }}</span>
        </template>
      </el-table-column>
      <!-- 未税金额 -->
      <el-table-column
        align="center"
        prop="noTaxTotalAmount"
        width="120"
        :label="$t('purSettlementMod.noTaxTotalAmount2')"
        show-overflow-tooltip
      >
        <template slot-scope="scope">
          <el-input-number
            v-if="scope.row.editabled"
            v-model="scope.row.noTaxTotalAmount"
            :disabled="disabledFlag"
            :controls="false"
            class="input-number-precision"
            @input="onExchange(scope.$index, true, scope.row)"
          />
          <span v-else-if="!scope.row.editabled">{{ scope.row.noTaxTotalAmount }}</span>
        </template>
      </el-table-column>
      <!-- 税额 -->
      <el-table-column
        align="center"
        prop="totalTax"
        width="120"
        :label="$t('purSettlementMod.totalTax')"
        show-overflow-tooltip
      >
        <template slot-scope="scope">
          <el-input-number
            v-if="scope.row.editabled"
            v-model="scope.row.totalTax"
            :disabled="disabledFlag"
            :controls="false"
            class="input-number-precision"
            @input="onExchange(scope.$index, true, scope.row)"
          />
          <span v-else-if="!scope.row.editabled">{{ scope.row.totalTax }}</span>
        </template>
      </el-table-column>
      <!-- 含税金额 -->
      <el-table-column
        align="center"
        prop="totalAmount"
        width="120"
        :label="$t('purSettlementMod.totalAmount')"
        show-overflow-tooltip
      />
      <!-- 发票快递单号 -->
      <el-table-column
        align="center"
        prop="invoiceCourierNo"
        width="120"
        :label="$t('purSettlementMod.invoiceCourierNo')"
        show-overflow-tooltip
      >
        <template slot-scope="scope">
          <el-input
            v-if="scope.row.editabled"
            :value="scope.row.invoiceCourierNo"
            :disabled="disabledFlag"
            @input="val => onChangeInvoiceCourierNo(val, scope)"
          />
          <span v-else-if="!scope.row.editabled">{{ scope.row.invoiceCourierNo }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('common.operation')" width="100" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" :disabled="disabledFlag" @click="deleteInvoiceInfo(scope.$index, scope.row)">
            {{ $t('common.delete') }}
          </el-button>
          <el-button type="text" @click="downloadInvoice(scope.row)">
            {{ $t('common.download') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 新增发票 - 弹窗 -->
    <srm-dialog
      :title="$t('purSettlementMod.addInvoice')"
      size="large"
      :visible.sync="addInvoieDialog"
      :close-on-click-modal="false"
      :destroy-on-close="true"
    >
      <template slot="header">
        <div style="font-size: 18px;">
          <span>{{ $t('purSettlementMod.addInvoice') }}</span>
          <span class="voice-title">{{ $t('purSettlementMod.voiceTitle') }}</span>
        </div>
      </template>
      <upload-table
        :headerText="$t('purSettlementMod.uploadInvoice')"
        :url="uploadUrl"
        :extraData="extraData"
        :accept="['jpg', 'png', 'jpeg']"
        :tableData="invoiceInforData"
        @selectChange="handleInvoieSelection"
      />
      <span slot="footer" class="dialog-footer">
        <el-button @click="addInvoieDialog = false">{{
          $t('vendorMod.relegation.abolish')
        }}</el-button>
        <el-button type="primary" @click="invoiceUplInfo">{{ $t('common.save') }}</el-button>
      </span>
    </srm-dialog>
  </div>
</template>

<script>
import { downloadWithParam } from 'lib@/utils/file'
import { sysPrefix } from '@/config/ipConfig'
import uploadTable from '@/library/composition/purSettlement/uploadTableShow.vue'
// import FileDynamic from '@/library/components/c-file-management/file-dynamic'
import FilePreview from '@/library/components/filePreview'

export default {
  name: 'InvoiceInfo',
  components: {
    uploadTable,
    FilePreview
  },
  props: {
    disabledFlag: {
      type: Boolean,
      default: false
    },
    value: {
      type: Array,
      default: () => []
    }
  },
  data () {
    return {
      isFileSource: false,
      addInvoieDialog: false,
      uploadUrl: `${sysPrefix()}/api-sup-ce/ps/invoice/onlineInvoice/uploadAndVatInvoice`,
      extraData: {
        sourceType: 'WEB_APP',
        uploadType: 'DEF',
        fileModular: 'sup-ce',
        fileFunction: 'onlineInvoice',
        fileType: 'images'
      },
      invoiceInforData: []
    }
  },
  methods: {
    // 新增发票 - 保存
    invoiceUplInfo () {
      // debugger
      // console.log('invoiceUplInfo', JSON.stringify(this.invoiceInforData))
      for (let i = 0; i < this.invoiceInforData.length; i++) {
        if (this.invoiceInforData[i].isFinesh === 'no') {
          return this.$message.warning(this.$t('purSettlementMod.waitForUploadToSucceed'))
        }
      }
      let uids = []
      try {
        uids = this.value.map(item => item.uid)
      } catch (e) {

      }
      this.invoiceInforData.forEach(item => {
        if (item.status === 'success' && !uids.includes(item.uid)) {
          this.value.push(JSON.parse(JSON.stringify(item)))
        }
      })
      // 上传完清空表格
      this.invoiceInforData = []
      this.addInvoieDialog = false
      this.$forceUpdate();
    },
    handleInvoieSelection (select) {
      this.invoiceSelects = select
    },
    // 删除开票信息
    deleteInvoiceInfo (index) {
      // this.perInvoiceInformationList.splice(index, 1)
      this.value.splice(index, 1)
    },
    // 发票信息 - 移入行可编辑
    mouseEnterInvoice (row, column) {
      this.$set(row, 'editabled', true)
    },
    mouseLeaveInvoice (row, column) {
      this.$set(row, 'editabled', false)
      // 开票日期需单独处理（日期组件点击触发elementUI组件挂载dom最外层自动认为鼠标移出）
      if (column.property === 'invoiceDate') {
        this.$set(row, 'editabled', true)
      }
    },
    // 新增发票信息
    addInvoiceInfo () {
      this.addInvoieDialog = true
    },
    onExchange (index, sign, row) {
      // console.log('onExchange', index, sign, row)
      // debugger
      // let moment = this.perInvoiceInformationList[index]
      let moment = this.value[index]
      // this.$set(this.perInvoiceInformationList, index, moment)
      this.$set(this.value, index, moment)
      if (sign) {
        // 发票含税金额 = 未税金额 + 税额
        row.totalAmount = row.noTaxTotalAmount + row.totalTax
      }
      this.$forceUpdate()
    },
    onChangeInvoiceCourierNo(val, scope) {
      console.log('onChangeInvoiceCourierNo', val, scope)
      this.$set(this.value[scope.$index], 'invoiceCourierNo', val)
      // scope.row.invoiceCourierNo = val
      this.$forceUpdate()
      this.onExchange(scope.$index)
    },
    // 日期组件失焦事件
    setNotEditabled (row) {
      this.$set(row, 'editabled', false)
    },
    // 发票下载
    downloadInvoice (row) {
      if (row.fileuploadId) {
        downloadWithParam(
          row.fileuploadId,
          row.fileSourceName,
        ).catch(() => {
          this.$message.error(this.$t('components.eio.downloadFail')) // 下载失败
        })
      } else {
        throw new Error('AttachId is null.')
      }
    }
  }
}
</script>

<style scoped>
::v-deep .mt-10 {
  margin-top: 10px;
  margin-bottom: 10px;
}
</style>
