<template>
  <div class="vendor-wrapper">
    <div v-if="!isMobile" class="btns mt-10">
      <el-button v-if="!readonly" type="primary" @click="addRow">
        <!-- 新增 -->
        {{ $t("common.add") }}
      </el-button>
      <!-- 寻源招标各一个快查 -->
      <!-- <QuickSearch
        v-if="isSou && !readonly"
        showButton
        class="quickBtn"
        btnTitle="新增"
        name="sou_recommvendor_sou"
        multiSelect
        @close-quicksearch="(val) => getVendor('sou',val)"
      />
      <QuickSearch
        v-if="isBid && !readonly"
        showButton
        class="quickBtn"
        btnTitle="新增"
        name="sou_recommvendor_bid"
        multiSelect
        @close-quicksearch="(val) => getVendor('bid',val)"
      /> -->
      <el-button type="ghost" @click="viewRisk">
        <!-- 查看供应商风险 -->
        {{ $t("cusEntry.supplement20250121.viewSupplierRisks") }}
        <!-- <span class="red">(3)</span> -->
      </el-button>
      <el-button v-if="isSou && !['APPROVING', 'APPROVED'].includes(form.projectStatus)" type="ghost" @click="updateList">
        <!-- 更新 -->
        {{ $t("common.modify") }}
      </el-button>
      <el-button type="ghost" @click="upForward">
        <!-- 上移 -->
        {{ $t("priceModel.costElement.moveUp") }}
      </el-button>
      <el-button type="ghost" @click="downForward">
        <!-- 下移 -->
        {{ $t("priceModel.costElement.moveDown") }}
      </el-button>
      <el-button v-if="!readonly" type="primary" @click="fetchFromCommu">
        <!-- 从标前交流获取 -->
        {{ $t("cusEntry.supplement20250121.obtainedFromPreBidCommunication") }}
      </el-button>
      <el-button type="primary" @click="accompanyBidVisible = true">
        <!-- 查看伴随投标 -->
        {{ $t("cusEntry.supplement20250121.viewAccompanyingBids") }}
      </el-button>
      <el-button
        v-if="form.souRequirementNo"
        type="primary"
        @click="getRegistration"
      >
        <!-- 查看报名详情 -->
        {{ $t("cusEntry.supplement20250121.viewRegistrationDetails") }}
      </el-button>
      <!-- <el-button
        @click="copyText"
      >
        复制
      </el-button> -->
      <!-- 推荐供应商列表导出文件.xlsx -->
      <ExportDirect
        :exprotUrl="`/api-sou/ext/recommend/vendor/exportRecommendVendor?projectId=${form.projectId}`"
        :filename="$t('cusEntry.supplement20250121.recommendedSupplierList') + $t('components.eio.exportFile') + '.xlsx'"
      />
    </div>
    <el-table
      border
      :data="vendorData"
      max-height="250px"
      :row-class-name="tableRowClassName"
      @selection-change="handleSelectionChange"
    >
      <el-table-column
        v-if="!isMobile"
        type="selection"
        width="55"
        fixed="left"
      />
      <!-- <el-table-column
        label="序号"
        align="center"
        type="index"
        width="50"
        fixed="left"
      /> -->
      <el-table-column
        :label="$t('components.common.sort')"
        align="center"
        type="index"
        width="50"
        fixed="left"
      />
      <!-- <el-table-column
        v-if="!isMobile"
        prop="vendorName"
        label="供应商名称"
        show-overflow-tooltip
        min-width="150"
      /> -->
      <el-table-column
        v-if="!isMobile"
        prop="vendorName"
        :label="$t('common.companyName')"
        show-overflow-tooltip
        min-width="150"
      />
      <!-- <el-table-column
        v-else
        prop="vendorName"
        label="供应商名称"
        show-overflow-tooltip
        min-width="150"
      /> -->
      <el-table-column
        v-else
        prop="vendorName"
        :label="$t('common.companyName')"
        show-overflow-tooltip
        min-width="150"
      />
      <!-- <el-table-column
        prop="linkmanName"
        label="报名联系人"
        min-width="130"
        :render-header="_addStarToColumn"
      > -->
      <el-table-column
        prop="linkmanName"
        :label="$t('cusEntry.common.signUpPerson')"
        min-width="130"
        :render-header="_addStarToColumn"
      >
        <template slot-scope="scope">
          <el-tooltip class="item" effect="dark" :content="scope.row.linkmanName" placement="top-start">
            <el-input v-model="scope.row.linkmanName" :disabled="readonly || scope.row.isAppend || (form.rcommendType === 'ADD' && scope.row.extIsAddVendor === 'N')" />
          </el-tooltip>
        </template>
      </el-table-column>
      <!-- <el-table-column
        prop="phone"
        label="报名联系电话"
        min-width="150"
        :render-header="_addStarToColumn"
      > -->
      <el-table-column
        prop="phone"
        :label="$t('cusEntry.common.signUpPhone')"
        min-width="150"
        :render-header="_addStarToColumn"
      >
        <template slot-scope="scope">
          <el-tooltip class="item" effect="dark" :content="scope.row.phone" placement="top-start">
            <el-input
              v-model="scope.row.phone"
              :disabled="readonly || scope.row.isAppend || (form.rcommendType === 'ADD' && scope.row.extIsAddVendor === 'N')"
              @blur="handlePhoneBlur(scope.$index,scope.row)"
            />
          </el-tooltip>
        </template>
      </el-table-column>
      <!-- <el-table-column
        v-if="!isMobile"
        prop="email"
        label="邮箱"
        min-width="180"
        :render-header="_addStarToColumn"
      > -->
      <el-table-column
        v-if="!isMobile"
        prop="email"
        :label="$t('common.email')"
        min-width="180"
        :render-header="_addStarToColumn"
      >
        <template slot-scope="scope">
          <el-tooltip class="item" effect="dark" :content="scope.row.email" placement="top-start">
            <el-input v-model="scope.row.email" :disabled="readonly || scope.row.isAppend || (form.rcommendType === 'ADD' && scope.row.extIsAddVendor === 'N')" />
          </el-tooltip>
        </template>
      </el-table-column>
      <!-- <el-table-column
        v-if="!isMobile"
        prop="extIsMainPoint"
        label="是否重点关注"
        show-overflow-tooltip
        min-width="120"
        :formatter="(row,column,cellValue,index) => $getDictLabel('YES_OR_NO',cellValue)"
      /> -->
      <!-- <el-table-column
        prop="extVendorAttr"
        label="供应商属性"
        show-overflow-tooltip
        min-width="350"
        :render-header="_addStarToColumn"
      > -->
      <el-table-column
        prop="extVendorAttr"
        :label="$t('cusEntry.supplement20250121.supplierAttributes')"
        show-overflow-tooltip
        min-width="350"
        :render-header="_addStarToColumn"
      >
        <template slot-scope="scope">
          <DictSelect
            v-model="scope.row.extVendorAttr"
            multiple
            code="SOU_RECOMM_VENDOR_NATRUE"
            :disabled="readonly || scope.row.isAppend || (form.rcommendType === 'ADD' && scope.row.extIsAddVendor === 'N')"
          />
        </template>
      </el-table-column>
      <!-- <el-table-column
        prop="extIsNewVendor"
        label="是否新供应商"
        min-width="150"
        :render-header="_addStarToColumn"
      > -->
      <el-table-column
        prop="extIsNewVendor"
        :label="$t('bidMod.common.isNewVendor')"
        min-width="150"
        :render-header="_addStarToColumn"
      >
        <template slot-scope="scope">
          <DictSelect v-model="scope.row.extIsNewVendor" code="YES_OR_NO" :disabled="readonly || scope.row.isAppend || (form.rcommendType === 'ADD' && scope.row.extIsAddVendor === 'N')" />
        </template>
      </el-table-column>
      <!-- <el-table-column
        prop="extRegisterFund"
        label="注册资金"
        show-overflow-tooltip
        min-width="120"
      />
      <el-table-column
        v-if="!isMobile"
        prop="extFounded"
        label="成立时间"
        show-overflow-tooltip
        min-width="120"
        :formatter="(row, column, cellValue) => $dayjsParse(cellValue)"
      />
      <el-table-column
        v-if="!isMobile"
        prop="extCompanyAddr"
        label="公司地址"
        show-overflow-tooltip
        min-width="120"
      /> -->
      <!-- <el-table-column
        prop="extRemark"
        label="备注"
        min-width="150"
      > -->
      <el-table-column
        prop="extRemark"
        :label="$t('components.eio.headers.remark')"
        min-width="150"
      >
        <template slot-scope="scope">
          <el-tooltip class="item" effect="dark" :content="scope.row.extRemark" placement="top-start">
            <el-input v-model="scope.row.extRemark" :disabled="readonly || scope.row.isAppend || (form.rcommendType === 'ADD' && scope.row.extIsAddVendor === 'N')" />
          </el-tooltip>
        </template>
      </el-table-column>
      <!-- <el-table-column
        v-if="!isMobile"
        prop="extGscp"
        label="GSCP"
        show-overflow-tooltip
        min-width="120"
      />
      <el-table-column
        prop="extIsDishonesty"
        label="是否失信"
        show-overflow-tooltip
        min-width="120"
        :formatter="(row,column,cellValue,index) => $getDictLabel('YES_OR_NO',cellValue)"
      />
      <el-table-column
        prop="extIsBizAnomaly"
        label="是否经营异常"
        show-overflow-tooltip
        min-width="120"
        :formatter="(row,column,cellValue,index) => $getDictLabel('YES_OR_NO',cellValue)"
      />
      <el-table-column
        v-if="!isMobile"
        prop="extLegal"
        label="法人"
        show-overflow-tooltip
        min-width="120"
      />
      <el-table-column
        v-if="!isMobile"
        prop="extMainPeople"
        label="主要人员"
        show-overflow-tooltip
        min-width="120"
      />
      <el-table-column
        v-if="!isMobile"
        prop="extStockholder"
        label="主要股东"
        show-overflow-tooltip
        min-width="120"
      />
      <el-table-column
        v-if="!isMobile"
        prop="extAptitude"
        label="资质"
        min-width="150"
      >
        <template slot-scope="scope">
          <el-tooltip class="item" effect="dark" :content="scope.row.extAptitude" placement="top-start">
            <el-input v-model="scope.row.extAptitude" :disabled="readonly || scope.row.isAppend || (form.rcommendType === 'ADD' && scope.row.extIsAddVendor === 'N')" />
          </el-tooltip>
        </template>
      </el-table-column>
      <el-table-column
        v-if="!isMobile"
        prop="extBrand"
        label="品牌"
        min-width="150"
      >
        <template slot-scope="scope">
          <el-tooltip class="item" effect="dark" :content="scope.row.extBrand" placement="top-start">
            <el-input v-model="scope.row.extBrand" :disabled="readonly || scope.row.isAppend || (form.rcommendType === 'ADD' && scope.row.extIsAddVendor === 'N')" />
          </el-tooltip>
        </template>
      </el-table-column> -->
      <!-- <el-table-column
        v-if="!isMobile"
        prop="extIsAddVendor"
        label="是否追加供应商"
        show-overflow-tooltip
        min-width="120"
        :formatter="(row,column,cellValue,index) => $getDictLabel('YES_OR_NO',cellValue)"
      /> -->
      <el-table-column
        v-if="!isMobile"
        prop="extIsAddVendor"
        :label="$t('cusEntry.supplement20250121.doYouWantToAddSuppliers')"
        show-overflow-tooltip
        min-width="120"
        :formatter="(row,column,cellValue,index) => $getDictLabel('YES_OR_NO',cellValue)"
      />
      <!-- <el-table-column
        v-if="!readonly"
        prop="operation"
        label="操作"
        fixed="right"
        width="100"
      > -->
      <el-table-column
        v-if="!readonly"
        prop="operation"
        :label="$t('components.headers.operation')"
        fixed="right"
        width="100"
      >
        <template slot-scope="scope">
          <el-button :disabled="scope.row.isAppend || (form.rcommendType === 'ADD' && scope.row.extIsAddVendor === 'N')" type="text" @click="deleteRows(scope)">
            <!-- 删除 -->
            {{ $t("components.common.delete") }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 查看供应商风险 -->
    <VendorRisk
      :visible.sync="vendorRiskVisible"
      :idList="vendorIdList"
      :nameList="vendorNameList"
      :applicantNo="form.applicantNo"
      :projectId="form.projectId"
    />

    <!-- 选择供应商 -->
    <VendorDialog
      :visible.sync="VendorDialogVisible"
      :form="form"
      @confirm="vendorDialogConfirm"
    />
    <!-- 伴随投标 -->
    <AccompanyBidDialog
      :visible.sync="accompanyBidVisible"
      :table-data="accompanyBidList"
    />
  </div>
</template>
<script>
import VendorRisk from './dialog/vendorRisk'
import VendorDialog from './dialog/vendorDialog'
import QuickSearch from '@/library/components/QuickSearch'
import recommendHttp from '../../../api'
import { transformMQL } from 'lib@/utils/util'
import AccompanyBidDialog from './dialog/accompany-bid-dialog'
import ExportDirect from 'lib@/components/export-direct'
import { validatePhone } from '@/utils/validate'
export default {
  components: {
    VendorRisk,
    VendorDialog,
    QuickSearch,
    ExportDirect,
    AccompanyBidDialog
  },
  props: {
    value: {
      type: Array,
      default: () => []
    },
    form: {
      type: Object,
      default: () => {}
    },
    readonly: {
      type: Boolean,
      default: false
    },
    isMobile: {
      type: Boolean,
      default: false
    },
    accompanyBidList: {
      type: Array,
      default: () => []
    },
    approvalFlag: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      vendorData: [],
      selectedRows: [],
      vendorRiskVisible: false,
      VendorDialogVisible: false,
      accompanyBidVisible: false,
      // extGscpList: ['Open', 'Changed', '真实命中，不建议合作']
      extGscpList: ['Open', 'Changed', this.$t('cusEntry.supplement20250121.trueHitNotRecommendedForCollaboration2')]
    }
  },
  computed: {
    isSou () {
      return this.form.sourceFrom === 'SOU'
    },
    isBid () {
      return this.form.sourceFrom === 'BID'
    },
    vendorIdList () {
      return this.vendorData.filter(item => item.vendorId).map(item => item.vendorId)
    },
    vendorNameList () {
      return this.vendorData.filter(item => item.vendorName).map(item => item.vendorName)
    }
  },
  watch: {
    value: {
      handler (nVal) {
        if (nVal) {
          this.vendorData = nVal
        }
      },
      immediate: true,
      deep: true
    }
  },
  methods: {
    handlePhoneBlur (index, row) {
      const bool = validatePhone(row.phone)
      if (!bool && row.phone) {
        // this.$message.warning(`推荐供应商列表 - ${item.vendorName}报名联系电话格式不正确`)
        this.$message.warning(`${this.$t("cusEntry.supplement20250121.recommendedSupplierList_")} ${row.vendorName}${this.$t("cusEntry.supplement20250121.theFormatOfTheRegistrationContactPhoneNumberIsIncorrect")}`)
        return false
      } else {
        return true
      }
    },
    async copyText () {
      const keys = ['vendorName', 'linkmanName', 'phone', 'email', 'extIsMainPoint', 'extAptitude', 'extBrand', 'extVendorAttr', 'extIsNewVendor', 'extRegisterFund', 'extFounded', 'extCompanyAddr', 'extRemark', 'extGscp', 'extIsDishonesty', 'extIsBizAnomaly', 'extLegal', 'extMainPeople', 'extStockholder', 'extIsAddVendor']
      if (this.selectedRows.length === 0) {
        this.$message.warning(this.$t('cusEntry.tipMessage.selectCopyRows'))
        return false
      }
      const text = this.selectedRows.map(item => {
        let resultStr = ''
        keys.forEach(key => {
          let value = ''
          switch (key) {
          case 'extIsMainPoint':
            value = this.$getDictLabel('YES_OR_NO', item[key])
            break
          case 'extVendorAttr':
            if (item[key].length) {
              value = item[key].map(itm => this.$getDictLabel('SOU_RECOMM_VENDOR_NATRUE', itm)).join()
            } else {
              value = ''
            }
            break
          case 'extIsNewVendor':
            value = this.$getDictLabel('YES_OR_NO', item[key])
            break
          case 'extIsDishonesty':
            value = this.$getDictLabel('YES_OR_NO', item[key])
            break
          case 'extIsBizAnomaly':
            value = this.$getDictLabel('YES_OR_NO', item[key])
            break
          case 'extIsAddVendor':
            value = this.$getDictLabel('YES_OR_NO', item[key])
            break
          case 'extFounded':
            value = this.$dayjsParse(item[key])
            break
          default:
            value = item[key] ? item[key] : ''
            break
          }
          resultStr += `${value} `
        })
        return resultStr
      }).join('\n')
      await navigator.clipboard.writeText(text)
    },
    getRegistration () {
      // if (this.approvalFlag) {
      //   const str = encodeURI(`from=fromFun&funName=sourcingRequireDetail&formId=${this.form.souRequirementId}`)
      //   const encodeStr = btoa(str)
      //   const pathname = window.location.pathname
      //   const systemUrl = window.location.origin + pathname.substring(0, pathname.length - 1)
      //   window.open(`${systemUrl}/#/flowTaskViewBase/${encodeStr}`, '_blank')
      // }
      this.$router.push({
        name: 'sourcingRequireBuyer',
        params: {
          from: 'recommendVendor',
          row: {
            reqHeadId: this.form.souRequirementId,
            reqHeadNo: this.form.souRequirementNo
          }
        }
      })
    },
    tableRowClassName ({ row, rowIndex }) {
      if (this.extGscpList.includes(row.extGscp)) {
        return 'red-row'
      }
    },
    getVendor (type, val) {
      if (val && val.length) {
        let idList = this.vendorData.filter(item => item.vendorId).map(item => item.vendorId)
        for (let item of val) {
          if (!idList.includes(item.companyId)) {
            this.vendorData.push({
              vendorId: item.companyId,
              vendorCode: item.companyCode,
              vendorName: item.companyName
            })
          }
        }
      }
    },
    vendorDialogConfirm (val) {
      console.log('val', val)
      if (val && val.length) {
        let idList = this.vendorData.filter(item => item.vendorId).map(item => item.vendorId)
        let vendorRepeatList = []
        for (let item of val) {
          if (!idList.includes(item.vendorId)) {
            this.vendorData.push({
              ...item
            })
          } else {
            vendorRepeatList.push(item.vendorName)
          }
        }
        if (vendorRepeatList.length) {
          // this.$message.warning(`已添加供应商${vendorRepeatList.join(';')},无需重复添加`)
          this.$message.warning(`${this.$t('cusEntry.supplement20250121.supplierAdded')}${vendorRepeatList.join(';')},${this.$t('cusEntry.supplement20250121.noNeedToAddAgain')}`)
        }
      }
      this.VendorDialogVisible = false
    },
    addRow () {
      this.VendorDialogVisible = true
    },
    viewRisk () {
      this.vendorRiskVisible = true
    },
    async updateList () {
      let transformParams = transformMQL.save('Recommvendor', {
        projectId: this.form.projectId
      }, 'vendorUpdate')
      const response = await recommendHttp.vendorUpdate(transformParams)
      if (response.data.length) {
        this.vendorData = response.data
        this.tranfromVendorAttr(this.vendorData)
      }
    },
    // 从标前交流获取
    async fetchFromCommu () {
      let transformParams = transformMQL.save('Recommvendor', {
        projectId: this.form.projectId
      }, 'vendorUpdateAsPreBid', {
        '*': {},
        'recommvendorList': {
          '*': {}
        }
      })
      const response = await recommendHttp.vendorUpdateAsPreBid(transformParams)
      if (response.data && response.data.length) {
        this.vendorData = response.data
        this.tranfromVendorAttr(this.vendorData)
      }
    },
    tranfromVendorAttr (data) {
      if (data && data.length) {
        data.forEach(item => {
          if (item.extVendorAttr && !Array.isArray(item.extVendorAttr)) {
            item.extVendorAttr = item.extVendorAttr.split(';')
          } else {
            item.extVendorAttr = []
          }
        })
      }
    },
    upForward () {
      // if (!this.selectedRows.length) return this.$message.warning('请勾选列表')
      if (!this.selectedRows.length) return this.$message.warning(this.$t('outsource.pleaseCheckList'))
      // if (this.selectedRows.length > 1) return this.$message.warning('仅可勾选一条数据')
      if (this.selectedRows.length > 1) return this.$message.warning(this.$t('cusEntry.supplement20250121.onlyOnePieceOfDataCanBeSelected'))
      let row = this.selectedRows[0]
      let index = this.vendorData.indexOf(row)
      // if (index === 0) return this.$message.warning('第一列无法上移')
      if (index === 0) return this.$message.warning(this.$t('cusEntry.supplement20250121.theFirstColumnCannotBeMovedUp'))
      this.vendorData.splice(index, this.selectedRows.length)
      this.vendorData.splice(index - 1, 0, ...this.selectedRows)
    },
    downForward () {
      // if (!this.selectedRows.length) return this.$message.warning('请勾选列表')
      if (!this.selectedRows.length) return this.$message.warning(this.$t('outsource.pleaseCheckList'))
      // if (this.selectedRows.length > 1) return this.$message.warning('仅可勾选一条数据')
      if (this.selectedRows.length > 1) return this.$message.warning(this.$t('cusEntry.supplement20250121.onlyOnePieceOfDataCanBeSelected'))
      let row = this.selectedRows[this.selectedRows.length - 1]
      let index = this.vendorData.indexOf(row)
      // if (index === this.vendorData.length - 1) return this.$message.warning('最后一列无法下移')
      if (index === this.vendorData.length - 1) return this.$message.warning(this.$t('cusEntry.supplement20250121.theLastColumnCannotBeMovedDown'))
      this.vendorData.splice(index, this.selectedRows.length)
      this.vendorData.splice(index + 1, 0, ...this.selectedRows)
    },
    handleSelectionChange (selection) {
      this.selectedRows = selection
    },
    deleteRows (scope) {
      this.vendorData.splice(scope.$index, 1)
    },
    getParams () {
      return this.vendorData
    }
  }
}
</script>
<style lang="scss" scoped>
.mt-10 {
  margin:10px 0;
}
.quickBtn {
  display: inline-block;
  vertical-align: middle;
  margin-right: 10px;
}
</style>
<style>

.vendor-wrapper .red-row {
  background: red !important;
}
</style>
