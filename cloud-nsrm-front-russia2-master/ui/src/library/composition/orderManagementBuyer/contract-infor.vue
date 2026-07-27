<!--
 * @Author: linyk7 && linyk7@meicloud.com
 * @Date: 2022-08-15 17:01:17
 * @LastEditors: linyk7 && linyk7@meicloud.com
 * @LastEditTime: 2022-08-18 17:50:59
 * @FilePath: src\library\composition\orderManagementBuyer\contract-infor.vue
 * @Description: 订单关联合同弹窗
-->
<template>
  <div class="contract-dialog">
    <srm-dialog
      v-el-drag-dialog
      :title="contractView.title"
      :size="contractView.vendor ? 'middle' : 'large'"
      :destroy-on-close="true"
      :visible.sync="dialogVisible"
      :close-on-click-modal="false"
      v-bind="$attrs"
      v-on="$listeners"
    >
      <template v-if="dialogVisible">
        <div v-if="!contractView.vendor" class="addition-header">
          <span class="padding-16">{{ $t('vendorMod.materialCode') }}: {{ contractView.params?.materialCode }}</span>
          <span class="padding-16">{{ $t('vendorMod.materialName') }}: {{ contractView.params?.materialName }}</span>
          <span class="padding-16">{{ $t('orderMod.orderRowNum') }}: {{ contractView.row?.orderNum || 0 }}</span>
          <span class="padding-16">{{ $t('orderMod.orderRowUsedNum') }}: {{ usedContractQuantity || 0 }}</span>
          <span>{{ $t('orderMod.orderRowUnusedNum') }}: {{ unusedContractQuantity || 0 }}</span>
        </div>
        <div v-if="!contractView.vendor" class="header-btn">
          <slot />
          <srm-row v-if="contractView.hiddenOperation" class="header-search">
            <srm-col :initCol="3" style="display: flex;padding: 0 10px;">
              <span :title="$t('orderMod.buyerOrderSynergy.contractNo')" class="search-label">{{ $t('orderMod.buyerOrderSynergy.contractNo') }}：</span>
              <el-input v-model="searchObj.contractNo" :title="searchObj.contractNo" clearable>
                <em slot="suffix" class="iconfont iconselect search-po" @click="openQueryDialog('contractNo')" />
              </el-input>
            </srm-col>
            <srm-col :initCol="3" style="display: flex;padding: 0 10px;">
              <span :title="$t('vendorMod.contractName')" class="search-label">{{ $t('vendorMod.contractName') }}：</span>
              <el-input v-model="searchObj.contractName" :title="searchObj.contractName" clearable>
                <em slot="suffix" class="iconfont iconselect search-po" @click="openQueryDialog('contractName')" />
              </el-input>
            </srm-col>
            <el-button type="primary" size="mini" @click="resetSearch">
              {{ $t('common.reset') }}
            </el-button>
            <el-button type="primary" size="mini" @click="searchData">
              {{ $t('common.search') }}
            </el-button>
          </srm-row>
        </div>
        <!-- 列表 -->
        <TableView
          ref="tableRef"
          style="height: 300px;"
          :table-header="contractHeader"
          :pre-query-data="{}"
          :checkbox="contractView.checkbox"
          :pageEnabled="false"
          :tableInfor="contractView.params?.orderContractMappingList"
          :checkChange="handleCurrentChange"
          :rowDblclick="rowDblclick"
        />
      </template>
    </srm-dialog>
    <!-- 合同编号名称弹窗 -->
    <srm-dialog size="small" :visible.sync="writeDialog">
      <template slot="header">
        {{ writeTitle === 'contractNo' ? $t('contractMod.contractNo') : $t('bidMod.contractName') }} <span class="write-tip">{{ $t('orderMod.multipleValueNewline') }}</span>
      </template>
      <el-input
        v-if="writeTitle === 'contractNo'"
        v-model="writeContract.num"
        type="textarea"
        :rows="10"
      />
      <el-input
        v-else
        v-model="writeContract.name"
        type="textarea"
        :rows="10"
      />
      <div slot="footer">
        <el-button @click="writeDialog = false">
          {{ $t('common.cancel') }}
        </el-button>
        <el-button type="primary" @click="handleContractConfirm">
          {{ $t('common.confirm') }}
        </el-button>
      </div>
    </srm-dialog>
  </div>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'

export default {
  name: 'ContractInfor',
  components: { TableView },
  props: {
    // form表单
    form: {
      type: Object,
      default: () => {
        return {}
      }
    },
    // 选中的物料明细行
    concatSelectRow: {
      type: Object,
      default: () => {
        return {}
      }
    },
    contractView: {
      type: Object,
      default: () => {
        return {
          vendor: false,
          title: this.$t('orderMod.viewContract'),
          checkbox: false,
          hiddenOperation: false,
          row: {},
          params: {
            unusedContractQuantity: 0,
            usedContractQuantity: 0,
            orderContractMappingList: []
          }
        }
      }
    },
    visible: {
      type: Boolean,
      default: () => {
        return false
      }
    }
  },
  data () {
    return {
      dialogVisible: false,
      searchObj: {
        contractNo: '',
        contractName: ''
      },
      writeContract: {
        num: '',
        name: ''
      },
      writeTitle: 'contractNo',
      writeDialog: false,
      contractHeader: [
        {
          label: () => this.$t('orderMod.buyerOrderSynergy.contractNo'),
          prop: 'contractNo',
          align: 'center',
          headerAlign: 'center',
          width: 120
        },
        {
          label: () => this.$t('contractMod.contractName'),
          prop: 'contractName',
          align: 'center',
          headerAlign: 'center',
          width: 120
        },
        {
          label: () => this.$t('contractMod.contractQuantity'),
          prop: 'contractQuantity',
          align: 'center',
          headerAlign: 'center',
          width: 120,
          hidden: this.contractView.vendor
        },
        {
          label: () => this.$t('orderMod.usedContractQuantity'),
          prop: 'usedContractQuantity',
          align: 'center',
          headerAlign: 'center',
          width: 120,
          formattor: val => val || '--',
          hidden: this.contractView.vendor
        },
        {
          label: () => this.$t('orderMod.unusedContractQuantity'),
          prop: 'unusedContractQuantity',
          align: 'center',
          headerAlign: 'center',
          width: 120,
          formattor: val => val || '--',
          hidden: this.contractView.vendor
        },
        {
          label: () => this.$t('orderMod.correlatedQuantity'),
          prop: 'correlatedQuantity',
          align: 'center',
          headerAlign: 'center',
          width: 120,
          controls: false,
          attrs: { precision: 0 },
          showType: !this.contractView.hiddenOperation ? 'inputNumber' : '',
          editable: row => row.isFrameworkAgreement === 'N',
          formattor: val => val || '--',
          hidden: this.contractView.vendor || this.contractView.selectContract,
          callback: row => this.correlatedQuantityChange(row)
        },
        {
          label: () => this.$t('orderMod.buyerOrderSynergy.contractlineNumber'),
          prop: 'lineNumber',
          align: 'center',
          headerAlign: 'center',
          width: 120,
          hidden: this.contractView.vendor
        },
        {
          label: () => this.$t('contractMod.isFrameworkAgreement'),
          prop: 'isFrameworkAgreement',
          align: 'center',
          headerAlign: 'center',
          width: 120,
          formattor: val => this.$getDictLabel('YES_OR_NO', val),
          hidden: this.contractView.vendor
        },
        {
          label: () => this.$t('contractMod.taxedPrice'),
          prop: 'taxedPrice',
          align: 'center',
          headerAlign: 'center',
          width: 120,
          hidden: this.contractView.vendor
        },
        {
          label: () => this.$t('contractMod.constartDate'),
          prop: 'effectiveDateFrom',
          align: 'center',
          headerAlign: 'center',
          width: 120,
          dataType: 'dateTime',
          hidden: this.contractView.vendor
        },
        {
          hidden: this.contractView.hiddenOperation || this.contractView.vendor,
          prop: 'operation',
          label: () => this.$t('common.operation'),
          align: 'center',
          headerAlign: 'center',
          width: 80,
          btnStyle: 'text',
          fixed: 'right',
          showType: 'buttons',
          buttons: [
            {
              callback: (row, scope) => this.deleteRow(row, scope),
              formattor: () => this.$t('common.delete')
            }
          ]
        }
      ]
    }
  },
  computed: {
    attrs () {
      return this.$attrs
    },
    // 订单行已关联数量 求和
    usedContractQuantity () {
      console.log('usedContractQuantity')
      // 查看状态取后端值
      if (this.contractView.hiddenOperation) return this.contractView.params?.usedContractQuantity
      if (this.contractView.params?.orderContractMappingList) {
        return this.contractView.params?.orderContractMappingList.reduce((r, c) => {
          if (c.isFrameworkAgreement === 'N') {
            return Number(r) + Number(c.correlatedQuantity)
          } else {
            return Number(r)
          }
        }, 0)
      } else {
        return 0
      }
    },
    // 订单行未关联数量：【订单数量（前端获取的订单数量）-关联数量之和】
    unusedContractQuantity () {
      return this.contractView.row?.orderNum - this.usedContractQuantity
    }
  },
  watch: {
    visible (sign) {
      this.dialogVisible = sign
      // 每次打开重置搜索条件
      if (sign) {
        this.resetSearch()
      }
    }
  },
  methods: {
    correlatedQuantityChange (row) {
      this.$emit('correlatedQuantityChange', this.contractView.params.orderContractMappingList)
    },
    // 查看合同
    async queryViewContract (url, params) {
      const { data } = await this.$http({
        url: url,
        method: 'POST',
        data: params,
        loading: true
      })
      return data
    },
    // 处理回车换行数据
    setLineString (str) {
      // 根据换行符转化成数组
      let arr = str.split(/[\r\n]+/)
      // 去除划分后的空白符
      let newArr = arr.map((r) => r.replace(/\s+/g, ''))
      return newArr.join(';')
    },
    // 手动输入编号后确认
    handleContractConfirm () {
      console.log(this.writeContract, 'handleContractConfirm')
      if (this.writeTitle === 'contractNo') {
        this.searchObj.contractNo = this.setLineString(this.writeContract.num)
      } else {
        this.searchObj.contractName = this.setLineString(this.writeContract.name)
      }
      this.writeDialog = false
    },
    // 重置
    resetSearch () {
      this.searchObj.contractNo = ''
      this.searchObj.contractName = ''
    },
    // 去除前后空格
    deleteFrontAndEndSpace (str) {
      return str.replace(/(^\s*)|(\s*$)/g, '')
    },
    // 处理数组中每个字符串的前后空格
    setSearchString (arr) {
      if (arr.length < 1) return
      return arr.map(str => {
        return this.deleteFrontAndEndSpace(str)
      })
    },
    // 查询
    searchData () {
      console.log('searchData')
      let contractNameList = this.searchObj.contractName ? this.searchObj.contractName.split(';') : []
      let contractNoList = this.searchObj.contractNo ? this.searchObj.contractNo.split(';') : []

      this.$emit('searchData', {
        contractNameList: this.setSearchString(contractNameList),
        contractNoList: this.setSearchString(contractNoList)
      })
    },
    // 打开多行输入选框回写入值
    inputLineSetString (str) {
      return str.split(';').join('\n')
    },
    // 打开合同弹窗
    openQueryDialog (name) {
      this.writeDialog = true
      this.writeTitle = name
      if (name === 'contractNo') {
        this.writeContract.num = this.inputLineSetString(this.searchObj.contractNo)
      } else {
        this.writeContract.name = this.inputLineSetString(this.searchObj.contractName)
      }
    },
    handleCurrentChange (selection) {
      this.$emit('handleChange', selection)
    },
    deleteRow (row, scope) {
      this.$refs.tableRef.deleteRow(scope.$index)
      this.$emit('deleteRow', row, scope.$index)
    },
    rowDblclick (row, event, column) {
      this.$emit('rowDblclick', row, event, column)
    }
  }
}
</script>

<style scoped lang="scss">
.write-tip {
  color: #AAAAAA;
}
.search-po {
  float: right;
  cursor: pointer;
  line-height: 28px;
}

:deep(.el-dialog__header) {
  display: block;
}

.addition-header {
  padding: 8px 0 8px 8px;
  margin-bottom: 8px;
  background-color: rgba(247, 247, 247, 1);
}

.header-btn {
  margin-bottom: 8px;
  display: flex;

  .header-search {
    width: 100%;
    margin: 0 !important;
    display: flex;
    justify-content: flex-end;
    .search-label {
      width: 98px;
      line-height: 28px;
      text-align: right;
      margin-right: 6px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }
}

.marginL-16 {
  margin-left: 16px;
}

.padding-16 {
  padding-right: 16px;
}
</style>
