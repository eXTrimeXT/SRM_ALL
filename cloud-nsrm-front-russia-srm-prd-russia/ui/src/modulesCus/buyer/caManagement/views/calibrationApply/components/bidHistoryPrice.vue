<template>
  <div class="wrapper">
    <div style="display: flex;">
      <QuickSearch
        v-if="!readonly"
        ref="addBidPriceQuickSearch"
        showButton
        class="quickBtn"
        style="margin-bottom: 10px;"
        :btnTitle="$t('common.add')"
        name="ca_scc_npm_sou_bid_price"
        multiSelect
        @close-quicksearch="addBidPrice"
      />
      <el-button
        v-if="!readonly"
        type="primary"
        style="margin-left:10px;height: 28px;"
        @click="handleAddHistory()"
      >
        新增历史价格
      </el-button>
    </div>
    <el-table
      ref="bidHistoryPriceTable"
      :data="historyPriceList"
      style="width: 100%"
      border
    >
      <el-table-column
        align="center"
        type="index"
        fixed="left"
        :label="$t('common.sort')"
        width="50"
      />
      <el-table-column
        align="center"
        prop="souName"
        label="项目名称"
        min-width="150"
        show-overflow-tooltip
      >
        <template slot-scope="scope">
          <span v-if="!scope.row.addHistoryFlag">{{ scope.row.souName }}</span>
          <el-form-item v-else :prop="'historyPriceList.' + scope.$index + '.souName'" :rules="{ required: true, message: '必填项', trigger: 'blur' }">
            <div>
              <span style="color:red;margin-right:1px;">*</span>
              <el-input v-model="scope.row.souName" maxlength="100" />
            </div>
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column
        align="center"
        prop="projectNo"
        label="项目编号"
        min-width="150"
        show-overflow-tooltip
      >
        <template slot-scope="scope">
          <span v-if="!scope.row.addHistoryFlag">{{ scope.row.projectNo }}</span>
          <el-form-item v-else :prop="'historyPriceList.' + scope.$index + '.projectNo'" :rules="{ required: true, message: '必填项', trigger: 'blur' }">
            <div>
              <span style="color:red;margin-right:1px;">*</span>
              <el-input v-model="scope.row.projectNo" maxlength="100" />
            </div>
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column
        align="center"
        prop="itemDesc"
        label="名称"
        min-width="150"
        show-overflow-tooltip
      >
        <template slot-scope="scope">
          <span v-if="!scope.row.addHistoryFlag">{{ scope.row.itemDesc }}</span>
          <el-form-item v-else :prop="'historyPriceList.' + scope.$index + '.itemDesc'" :rules="{ required: true, message: '必填项', trigger: 'blur' }">
            <div>
              <span style="color:red;margin-right:1px;">*</span>
              <el-input v-model="scope.row.itemDesc" maxlength="100" />
            </div>
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column
        align="center"
        prop="specification"
        label="规格型号"
        min-width="150"
        show-overflow-tooltip
      >
        <template slot-scope="scope">
          <span v-if="!scope.row.addHistoryFlag">{{ scope.row.specification }}</span>
          <el-form-item v-else :prop="'historyPriceList.' + scope.$index + '.specification'" :rules="{ required: true, message: '必填项', trigger: 'blur' }">
            <div>
              <span style="color:red;margin-right:1px;">*</span>
              <el-input v-model="scope.row.specification" maxlength="100" />
            </div>
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column
        align="center"
        prop="brand"
        label="品牌"
        min-width="150"
        show-overflow-tooltip
      >
        <template slot-scope="scope">
          <span v-if="!scope.row.addHistoryFlag">{{ scope.row.brand }}</span>
          <el-form-item v-else :prop="'historyPriceList.' + scope.$index + '.brand'" :rules="{ required: true, message: '必填项', trigger: 'blur' }">
            <div>
              <span style="color:red;margin-right:1px;">*</span>
              <el-input v-model="scope.row.brand" maxlength="100" />
            </div>
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column
        align="center"
        prop="priceTax"
        label="含税单价（万元）"
        min-width="150"
        show-overflow-tooltip
      >
        <template slot-scope="scope">
          <span v-if="!scope.row.addHistoryFlag">{{ scope.row.priceTax }}</span>
          <el-form-item v-else :prop="'historyPriceList.' + scope.$index + '.priceTax'" :rules="{ required: true, message: '必填项', trigger: 'blur' }">
            <div>
              <span style="color:red;margin-right:1px;">*</span>
              <el-input v-model="scope.row.priceTax" maxlength="100" onkeyup="this.value=this.value.match(/\d+\.?\d{0,6}/);" />
            </div>
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column
        align="center"
        prop="priceSumTax"
        label="含税总价（万元）"
        min-width="150"
        show-overflow-tooltip
      >
        <template slot-scope="scope">
          <span v-if="!scope.row.addHistoryFlag">{{ scope.row.priceSumTax }}</span>
          <el-form-item v-else :prop="'historyPriceList.' + scope.$index + '.priceSumTax'" :rules="{ required: true, message: '必填项', trigger: 'blur' }">
            <div>
              <span style="color:red;margin-right:1px;">*</span>
              <el-input v-model="scope.row.priceSumTax" maxlength="100" onkeyup="this.value=this.value.match(/\d+\.?\d{0,6}/);" />
            </div>
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column
        align="center"
        prop="region"
        label="区域"
        min-width="150"
        show-overflow-tooltip
      >
        <template slot-scope="scope">
          <span v-if="!scope.row.addHistoryFlag">{{ scope.row.region }}</span>
          <el-form-item v-else :prop="'historyPriceList.' + scope.$index + '.region'" :rules="{ required: true, message: '必填项', trigger: 'blur' }">
            <div>
              <span style="color:red;margin-right:1px;">*</span>
              <DictSelect v-model="scope.row.region" code="REGION" />
            </div>
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column
        align="center"
        prop="souPrincipal"
        label="招标负责人"
        min-width="150"
        show-overflow-tooltip
      >
        <template slot-scope="scope">
          <span v-if="!scope.row.addHistoryFlag">{{ scope.row.souPrincipal }}</span>
          <el-form-item v-else :prop="'historyPriceList.' + scope.$index + '.souPrincipal'" :rules="{ required: true, message: '必填项', trigger: 'blur' }">
            <div>
              <span style="color:red;margin-right:1px;">*</span>
              <el-input v-model="scope.row.souPrincipal" maxlength="100" />
            </div>
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column
        align="center"
        prop="creationDate"
        label="定标时间"
        min-width="150"
        show-overflow-tooltip
      >
        <template slot-scope="scope">
          <span v-if="!scope.row.addHistoryFlag">{{ scope.row.creationDate }}</span>
          <el-form-item v-else :prop="'historyPriceList.' + scope.$index + '.creationDate'" :rules="{ required: true, message: '必填项', trigger: 'change' }">
            <div style="display: flex;align-items: center;">
              <span style="color:red;margin-right:1px;">*</span>
              <el-date-picker
                v-model="scope.row.creationDate"
                type="datetime"
                format="yyyy-MM-dd HH:mm:ss"
                value-format="yyyy-MM-dd HH:mm:ss"
                placeholder="选择日期时间"
              />
            </div>
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column
        align="center"
        label="价格详情"
        width="100"
        show-overflow-tooltip
      >
        <template slot-scope="scope">
          <el-button type="text" :disabled="scope.row.addHistoryFlag || scope.row.bidPriceId == 0" @click="getPriceDetail(scope.row)">
            {{ $t('common.view') }}
          </el-button>
        </template>
      </el-table-column>
      <el-table-column
        v-if="!readonly"
        align="center"
        :label="$t('common.operation')"
        width="100"
      >
        <template slot-scope="scope">
          <el-button type="text" @click="deleteRow(scope.$index)">
            {{ $t('common.delete') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>
<script>
import { transformMQL } from 'lib@/utils/util'
import QuickSearch from '@/library/components/QuickSearch'
import BidPriceLibraryDetail from '@/modulesCus/buyer/biddingBuyer/views/bidPriceLibrary/edit'

export default {
  components: {
    QuickSearch,
    BidPriceLibraryDetail
  },
  props: {
    readonly: {
      type: Boolean,
      default: false
    },
    historyPriceList: {
      type: Array,
      default: () => ([])
    },
    approvalFlag: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {}
  },
  // computed: {
  //   tableData: {
  //     get () {
  //       return this.historyPriceList
  //     },
  //     set (val) {
  //       this.$emit('update:value', val)
  //     }
  //   }
  // },
  methods: {
    getPriceDetail (row) {
      if (this.approvalFlag) {
        const str = encodeURI(`from=fromFun&funName=bidPriceLibrary&formId=${row.bidPriceId}`)
        const encodeStr = btoa(str)
        const pathname = window.location.pathname
        const systemUrl = window.location.origin + pathname.substring(0, pathname.length - 1)
        window.open(`${systemUrl}/#/flowTaskViewBase/${encodeStr}`, '_blank')
      } else {
        let tab = {
          component: BidPriceLibraryDetail,
          params: {
            flag: 'view',
            row: row,
            tabName: 'BidPriceLibraryDetail' + row.bidPriceId
          },
          title: row.projectNo + '招标价格库',
          name: 'BidPriceLibraryDetail' + row.bidPriceId
        }
        this.$emit('tab-add', tab)
      }
    },
    addBidPrice (selection) {
      const bidPriceIds = this.historyPriceList.map(item => item.bidPriceId)
      selection.forEach(item => {
        if (!bidPriceIds.includes(item.bidPriceId)) {
          this.historyPriceList.push(item)
        }
      })
    },
    deleteRow (index) {
      this.historyPriceList.splice(index, 1)
    },
    // 新增历史价格
    handleAddHistory () {
      let child = {
        souName: '',
        projectNo: '',
        itemDesc: '',
        specification: '',
        brand: '',
        priceTax: '',
        priceSumTax: '',
        region: '',
        souPrincipal: '',
        creationDate: '',
        bidPriceId: 0,
        addHistoryFlag: true
      }
      this.historyPriceList.push(child)
    }
  }
}
</script>
<style lang="scss" scoped>
</style>
