<template>
  <el-container
    class="flex-container flex-container-right"
    direction="vertical"
  >
    <el-main>
      <el-form
        ref="vendorForm"
        class="tableForm"
        :model="tableForm"
        :rules="vendorRules"
        :show-message="false"
      >
        <div style="padding: 10px 0">
          <el-button
            type="primary"
            :disabled="isdisabledTab"
            class="detail-pbtn"
            @click="addOneVendor"
          >
            {{ $t("common.add") }}
          </el-button>
          <el-button
            type="primary"
            :disabled="isdisabledTab"
            class="detail-pbtn"
            @click="smartRecommond"
          >
            <!-- 智能推荐 -->
            {{ $t("bidMod.smartRecommond") }}
          </el-button>
          <el-button
            type="primary"
            :disabled="isdisabledTab"
            class="detail-pbtn"
            @click="delVendors"
          >
            {{ $t("common.delete") }}
          </el-button>
          <!-- <el-button
            type="primary"
            @click="importExpertList"
            >导出</el-button  > -->
        </div>
        <el-table
          :data="tableForm.t13table"
          style="width: 100%"
          border
          max-height="400px"
          @selection-change="checkVendorsList"
        >
          <el-table-column type="selection" />
          <el-table-column
            align="center"
            :label="$t('contractMod.tabindex')"
            type="index"
            width="50"
          />
          <el-table-column
            align="center"
            prop="vendorCode"
            :label="$t('bidMod.vendorCode')"
            width="150"
            :show-overflow-tooltip="true"
          >
            <template
              slot="header"
              slot-scope="scope"
            >
              <i class="toRequired">*</i>{{ $t("bidMod.vendorCode") }}
            </template>
            <template slot-scope="scope">
              <el-form-item
                :prop="'t13table.' + scope.$index + '.vendorCode'"
                :rules="vendorRules.vendorCode"
              >
                <quick-search
                  :show-input="scope.row.vendorCode"
                  show-key="companyCode"
                  :scope-data="scope.row"
                  :disabled="isdisabledTab"
                  name="scc_sup_company_info"
                  @close-quicksearch="getVendorObj"
                />
              </el-form-item>
            </template>
          </el-table-column>
          <el-table-column
            align="center"
            prop="vendorName"
            :label="$t('bidMod.vendorName')"
            min-width="150"
            :show-overflow-tooltip="true"
          />
          <el-table-column
            align="center"
            prop="linkManName"
            :label="$t('bidMod.linkMan')"
            width="150"
            :show-overflow-tooltip="true"
          >
            <template
              slot="header"
              slot-scope="scope"
            >
              <i class="toRequired">*</i>{{ $t("bidMod.linkMan") }}
            </template>
            <template slot-scope="scope">
              <el-form-item
                :prop="'t13table.' + scope.$index + '.linkManName'"
                :rules="vendorRules.linkManName"
              >
                <!-- <el-input :disabled="isdisabledTab" v-model="scope.row.linkManName" /> -->
                <quick-search
                  :pre-query-data="{ 't.COMPANY_ID': scope.row.vendorId }"
                  :show-input="scope.row.linkManName"
                  show-key="contactName"
                  allow-input
                  :scope-data="scope.row"
                  name="scc_sup_contact_info"
                  @close-quicksearch="getContactObj"
                />
              </el-form-item>
            </template>
          </el-table-column>
          <el-table-column
            align="center"
            prop="phone"
            :label="$t('bidMod.phone')"
            width="150"
            :show-overflow-tooltip="true"
          >
            <template
              slot="header"
              slot-scope="scope"
            >
              <i class="toRequired">*</i>{{ $t("bidMod.phone") }}
            </template>
            <template slot-scope="scope">
              <el-form-item
                :prop="'t13table.' + scope.$index + '.phone'"
                :rules="vendorRules.phone"
              >
                <el-input
                  v-model="scope.row.phone"
                  :disabled="isdisabledTab"
                />
              </el-form-item>
            </template>
          </el-table-column>
          <el-table-column
            align="center"
            prop="email"
            :label="$t('bidMod.email')"
            width="180"
            :show-overflow-tooltip="true"
          >
            <template
              slot="header"
              slot-scope="scope"
            >
              <i class="toRequired">*</i>{{ $t("bidMod.email") }}
            </template>
            <template slot-scope="scope">
              <el-form-item
                :prop="'t13table.' + scope.$index + '.email'"
                :rules="vendorRules.email"
              >
                <el-input
                  v-model="scope.row.email"
                  :disabled="isdisabledTab"
                />
              </el-form-item>
            </template>
          </el-table-column>
          <el-table-column
            align="center"
            prop="comments"
            :label="$t('bidMod.remark')"
            width="150"
            :show-overflow-tooltip="true"
          >
            <template slot-scope="scope">
              <el-input
                v-model="scope.row.comments"
                :disabled="isdisabledTab"
              />
            </template>
          </el-table-column>
          <!-- <el-table-column
          align="center"
          prop="lgtPayPlans"
          label="付款信息"
          width="100"
          :show-overflow-tooltip="true"
        >
          <template slot="header" slot-scope="scope">
            <i class="toRequired">*</i>付款信息
          </template>
          <template slot-scope="scope">
            <el-button
              type="text"
              v-if="scope.row.vendorId"
              @click="contractLook(scope.$index, scope.row)"
              >查看</el-button
            >
          </template>
        </el-table-column> -->
          <el-table-column
            fixed="right"
            align="center"
            :label="$t('bidMod.operation')"
            width="100"
          >
            <template slot-scope="scope">
              <!-- <el-button
              type="text"
              :disabled="isdisabledTab"
              @click="setPermission(scope.$index, scope.row)"
              >报价权限</el-button
            > -->
              <el-button
                type="text"
                :disabled="isdisabledTab"
                @click="handleDelClickt13table(scope.$index, scope.row)"
              >
                {{ $t("common.delete") }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-form>
    </el-main>

    <!-- 弹框区域-->
    <!-- 推荐供应商 -->
    <srm-dialog
      :title="$t('bidMod.recommendVendor')"
      size="middle"
      :visible.sync="dialogSmartVisible"
      :close-on-click-modal="false"
    >
      <el-table
        :data="displaySmartVendor"
        style="width: 100%"
        border
        height="333px"
        highlight-current-row
        @selection-change="handleVendoeSelection"
      >
        <el-table-column
          align="center"
          type="index"
          width="40"
        />
        <el-table-column
          type="selection"
          width="55"
        />
        <el-table-column
          align="center"
          prop="companyCode"
          :label="$t('bidMod.vendorCode')"
          width="150"
          :show-overflow-tooltip="true"
        />
        <el-table-column
          align="center"
          prop="companyName"
          :label="$t('bidMod.vendorName')"
          min-width="150"
          :show-overflow-tooltip="true"
        />
      </el-table>
      <div
        slot="footer"
        class="dialog-footer"
      >
        <el-button
          type="primary"
          @click="addNewSmartVendor"
        >
          {{
            $t("common.confirm")
          }}
        </el-button>
        <el-button @click="dialogSmartVisible = false">
          {{
            $t("common.cancel")
          }}
        </el-button>
      </div>
    </srm-dialog>

    <!-- 付款信息 -->
    <srm-dialog
      :title="$t('paymentType.payment')"
      size="large"
      :visible.sync="contractShow"
      :close-on-click-modal="false"
    >
      <div style="padding-bottom: 3px">
        <el-button
          type="primary"
          :disabled="isdisabledTab"
          @click="addList"
        >
          {{ $t("common.add") }}
        </el-button>
        <el-button
          type="primary"
          :disabled="isdisabledTab"
          @click="saveList"
        >
          {{ $t("common.save") }}
        </el-button>
        <el-button
          type="primary"
          :disabled="isdisabledTab"
          @click="delList"
        >
          {{ $t("common.delete") }}
        </el-button>
        <el-button
          @click="contractShow = false"
        >
          {{
            $t("common.backTo")
          }}
        </el-button>
      </div>
      <el-form
        ref="contractForm"
        class="tableForm"
        :model="form"
        :rules="contractRules"
        :disabled="isdisabledTab"
        :show-message="false"
      >
        <el-table
          :data="form.contractList"
          height="300px"
          style="width: 100%"
          border
          @selection-change="checkContractList"
        >
          <el-table-column type="selection" />
          <el-table-column
            :label="$t('common.sort')"
            align="center"
            type="index"
            width="50"
          />
          <el-table-column
            align="center"
            prop="contractCode"
            :label="$t('logisticsMod.contract')"
          >
            <template
              slot="header"
              slot-scope="scope"
            >
              <i class="toRequired">*</i>{{ $t("logisticsMod.contract") }}
            </template>
            <template slot-scope="scope">
              <el-form-item
                :prop="'contractList.' + scope.$index + '.contractCode'"
                :rules="contractRules.contractCode"
              >
                <quick-search
                  :show-input="scope.row.contractCode"
                  show-key="contractCode"
                  :pre-query-data="{
                    't.contract_Type': 'MIAN_CONTRACT_ADD',
                    't.contract_Status': 'ARCHIVED',
                    't.vendor_ID': vendorId
                  }"
                  :scope-data="scope.row"
                  name="scc_contract_head_confirmed"
                  @close-quicksearch="getcontractObj"
                />
              </el-form-item>
            </template>
          </el-table-column>
          <!-- 付款方式 -->
          <el-table-column
            align="center"
            prop="payMethod"
            :label="$t('paymentType.paymentWay')"
            width="150"
          >
            <template
              slot="header"
              slot-scope="scope"
            >
              <i class="toRequired">*</i>{{ $t("paymentType.paymentWay") }}
            </template>
            <template slot-scope="scope">
              <el-form-item
                :prop="'contractList.' + scope.$index + '.payMethod'"
                :rules="contractRules.payMethod"
              >
                <DictSelect
                  v-model="scope.row.payMethod"
                  code="PAYMENT_MODE"
                />
              </el-form-item>
            </template>
          </el-table-column>
          <!-- 付款条件 -->
          <el-table-column
            align="center"
            prop="payExplain"
            :label="$t('paymentType.paymentTerm')"
            width="150"
          >
            <template
              slot="header"
              slot-scope="scope"
            >
              <i class="toRequired">*</i>{{ $t("paymentType.paymentTerm") }}
            </template>
            <template slot-scope="scope">
              <el-form-item
                :prop="'contractList.' + scope.$index + '.payExplain'"
                :rules="contractRules.payExplain"
              >
                <el-select
                  v-model="scope.row.payExplain"
                  code="PAYMENT_STAGE"
                >
                  <el-option
                    v-for="item in payAllList"
                    :key="item.payTypeId"
                    :label="item.label"
                    :value="item.value"
                  />
                </el-select>
              </el-form-item>
            </template>
          </el-table-column>
          <!-- 账期 -->
          <el-table-column
            align="center"
            prop="dateNum"
            :label="$t('paymentType.paymentDay')"
            width="250"
          >
            <template
              slot="header"
              slot-scope="scope"
            >
              <i class="toRequired">*</i>{{ $t("paymentType.paymentDay") }}
            </template>
            <template slot-scope="scope">
              <el-form-item
                :prop="'contractList.' + scope.$index + '.dateNum'"
                :rules="contractRules.dateNum"
              >
                <DictSelect
                  v-model="scope.row.dateNum"
                  code="PAYMENT_PERIOD"
                />
              </el-form-item>
            </template>
          </el-table-column>
          <el-table-column
            :label="$t('common.operation')"
            width="80"
          >
            <template slot-scope="scope">
              <el-button
                type="text"
                @click="deleteClick(scope.$index, scope.row)"
              >
                {{ $t("common.delete") }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-form>
    </srm-dialog>
    <!--      <srm-dialog-->
    <!--        title="报价权限设置"-->
    <!--        size="large"-->
    <!--        :visible.sync="permissionVisible"-->
    <!--        :close-on-click-modal="false"-->
    <!--      >-->
    <!--        <el-table-->
    <!--          :data="permissionListItem"-->
    <!--          style="width: 100%"-->
    <!--          border-->
    <!--          height="333px"-->
    <!--          highlight-current-row-->
    <!--        >-->
    <!--          <el-table-column-->
    <!--            align="center"-->
    <!--            type="index"-->
    <!--            label="序号"-->
    <!--            width="60"-->
    <!--          />-->
    <!--          <el-table-column-->
    <!--            align="center"-->
    <!--            prop="vendorName"-->
    <!--            label="供应商名称"-->
    <!--            :show-overflow-tooltip="true"-->
    <!--          />-->
    <!--          <el-table-column-->
    <!--            align="center"-->
    <!--            prop="vendorCode"-->
    <!--            label="供应商编码"-->
    <!--            width="120"-->
    <!--            :show-overflow-tooltip="true"-->
    <!--          />-->
    <!--          <el-table-column-->
    <!--            align="center"-->
    <!--            prop="bidingName"-->
    <!--            label="项目名称"-->
    <!--            width="120"-->
    <!--            :show-overflow-tooltip="true"-->
    <!--          />-->
    <!--          <el-table-column-->
    <!--            align="center"-->
    <!--            prop="fromPlace"-->
    <!--            label="起运地"-->
    <!--            width="150"-->
    <!--            :show-overflow-tooltip="true"-->
    <!--          />-->

    <!--          <el-table-column-->
    <!--            align="center"-->
    <!--            prop="toPlace"-->
    <!--            label="目的地"-->
    <!--            width="150"-->
    <!--            :show-overflow-tooltip="true"-->
    <!--          />-->
    <!--          <el-table-column-->
    <!--            align="center"-->
    <!--            prop="ifProhibit"-->
    <!--            label="是否禁止报价"-->
    <!--            width="130"-->
    <!--            :show-overflow-tooltip="true"-->
    <!--          >-->
    <!--            <template slot-scope="scope">-->
    <!--              <el-checkbox-->
    <!--                v-model="scope.row.ifProhibit"-->
    <!--                true-label="Y"-->
    <!--                false-label="N"-->
    <!--                >禁止报价</el-checkbox-->
    <!--              >-->
    <!--            </template>-->
    <!--          </el-table-column>-->
    <!--        </el-table>-->

    <!--        <div slot="footer" class="dialog-footer">-->
    <!--          <el-button type="primary" @click="addItemPermission"-->
    <!--            >确定</el-button-->
    <!--          >-->
    <!--          <el-button @click="permissionVisible = false">取消</el-button>-->
    <!--        </div>-->
    <!--      </srm-dialog>-->
  </el-container>
</template>
<script>
import QuickSearch from 'lib@/components/QuickSearch'
import MainHeader from 'lib@/components/Table/MainHeader'

export default {
  name: 'BiddingProjectDetailVendors',
  components: { QuickSearch, MainHeader },
  props: ['tableForm', 'scopeBidingId', 'isdisabledTab'],
  data () {
    return {
      payAllList: [],
      dialogSmartVisible: false,
      displaySmartVendor: [],
      permissionVisible: false,
      contractShow: false,
      multipleVendorSelection: [],
      permissionListItem: [],
      checkVendors: [],
      checkContract: [],
      vendorId: null,
      form: {
        contractList: []
      },
      contractRules: {
        contractCode: [{ required: true }],
        payMethod: [{ required: true }],
        payExplain: [{ required: true }],
        dateNum: [{ required: true }]
      },
      vendorRules: {
        vendorCode: [{ required: true }],
        linkManName: [{ required: true }],
        phone: [{ required: true }],
        email: [{ required: true }]
      }
    }
  },
  watch: {},
  created () {
  },
mounted () {
  this.$http({
        url: '/api-cm/template/payType/paymentTermsPage',
        method: 'POST',
        data: {
          pageNum: 1,
          pageSize: 1000
        },
        loading: true
      })
        .then(res => {
          return payAllList.map(i => ({
            key: i.payTypeId,
            label: i.payExplain,
            value: i.payTypeId
          }))
        })
        .catch(err => {
          console.log(err)
        })
},
  methods: {
    validate (callback) {
      this.$refs.vendorForm.validate(callback)
    },
    handleVendoeSelection (val) {
      this.multipleVendorSelection = val
    },
    addNewSmartVendor () {
      if (this.multipleVendorSelection.length == 0) {
        this.$message.warning(this.$t('bidMod.pleaseSelVendor')) // 请先勾选供应商!
        return
      }
      let vendorIdArr = this.multipleVendorSelection.map(v => v.companyId)
      this.$http({
        url: '/api-pd/inviteVendor/bidVendor/listVendorContactInfo',
        method: 'POST',
        data: vendorIdArr,
        loading: true
      })
        .then(data => {
          if (data.data && data.data.length !== 0) {
            data.data.map(v => {
              this.tableForm.t13table.push({
                bidingId: this.scopeBidingId,
                vendorId: v.vendorId,
                vendorCode: v.vendorCode,
                vendorName: v.vendorName,
                linkManName: v.linkManName,
                phone: v.phone,
                email: v.email,
                comments: '',
                lgtQuoteAuthorizes: []
              })
            })
            this.dialogSmartVisible = false
          }
        })
        .catch(err => {
          console.log(err)
        })
    },
    smartRecommond () {
      this.$http({
        url: '/api-pd/logistics/biding/intelligentFindVendor',
        method: 'get',
        params: { bidingId: this.scopeBidingId },
        loading: true
      })
        .then(data => {
          if (data && data.data) {
            this.displaySmartVendor = data.data
            this.dialogSmartVisible = true
          }
        })
        .catch(err => {
          console.log(err)
        })
    },
    addOneVendor () {
      this.tableForm.t13table.push({
        id: Math.floor(Math.random() * 1000000),
        bidingId: this.scopeBidingId,
        vendorId: '',
        vendorCode: '',
        vendorName: '',
        linkManName: '',
        phone: '',
        email: '',
        comments: '',
        bond: '',
        lgtPayPlans: [],
        lgtQuoteAuthorizes: []
      })
    },
    delVendors () {
      if (!this.checkVendors.length) {
        this.$message({
          message: this.$t('logisticsMod.msgPurchaseApply[10]'), // 请选择要删除的行
          type: 'error'
        })
      }
      let arr = []
      this.tableForm.t13table.map(i => {
        if (
          !this.checkVendors.includes(i.id) &&
          !this.checkVendors.includes(i.bidVendorId)
        ) {
          arr.push(i)
        }
      })
      this.tableForm.t13table = arr
    },
    checkVendorsList (data) {
      this.checkVendors = data.map(i => i.id || i.bidVendorId)
    },
    getVendorObj (val, scope) {
      scope.vendorId = val ? val.companyId : ''
      scope.vendorCode = val ? val.companyCode : ''
      scope.vendorName = val ? val.companyName : ''
      if (!scope.vendorId) {
        scope.linkManName = ''
        scope.phone = ''
        scope.email = ''
        return
      }
      this.$http({
        url: '/api-pd/inviteVendor/bidVendor/listVendorContactInfo',
        method: 'POST',
        data: [scope.vendorId],
        loading: true
      })
        .then(data => {
          if (data.data && data.data.length == 1) {
            scope.linkManName = data.data[0].linkManName
            scope.phone = data.data[0].phone
            scope.email = data.data[0].email
          } else {
            scope.linkManName = ''
            scope.phone = ''
            scope.email = ''
          }
        })
        .catch(err => {
          console.log(err)
        })
    },
    getContactObj (val, scope) {
      console.log('[联系人]', val, scope)
      if (typeof val === 'string') {
        scope.linkManName = val
        scope.phone = ''
        scope.email = ''
        return
      }
      scope.linkManName = val ? val.contactName : ''
      scope.phone = val ? val.ceeaContactMethod : ''
      scope.email = val ? val.email : ''
    },
    setPermission (index, row) {
      if (!row.bidVendorId) {
        return this.$message.error(
          this.$t('logisticsMod.msgPurchaseApply[31]')
        ) // 先保存未保存的行信息！
      }
      this.thisIndex = index
      if (this.tableForm.t13table[index].lgtQuoteAuthorizes.length > 0) {
        const curLgtQuoteAuthorizes = this.tableForm.t13table[index]
          .lgtQuoteAuthorizes
        this.permissionListItem = curLgtQuoteAuthorizes
        this.permissionVisible = true
      } else {
        this.$http({
          url:
            '/api-pd/logistics/biding/getLgtQuoteAuthorizeByBidVendorId',
          method: 'GET',
          params: {
            bidVendorId: row.bidVendorId || '',
            bidingId: this.scopeBidingId
          },
          loading: true
        })
          .then(data => {
            if (data.data && data.data.length !== 0) {
              this.permissionListItem = data.data
            } else {
              this.permissionListItem = this.tableForm.t13table[
                this.thisIndex
              ].lgtQuoteAuthorizes
            }
            this.permissionVisible = true
          })
          .catch(err => {
            console.log(err)
          })
      }
    },
    // 报价关系 end
    handleDelClickt13table (index, row) {
      this.tableForm.t13table.splice(index, 1)
    },
    addItemPermission () {
      this.tableForm.t13table[
        this.thisIndex
      ].lgtQuoteAuthorizes = this.permissionListItem
      console.log(this.tableForm.t13table[this.thisIndex])
      this.permissionVisible = false
    },
    deleteClick (index, row) {
      this.form.contractList.splice(index, 1)
    },
    getcontractObj (val, scope) {
      scope.contractCode = val.contractCode
      scope.contractName = val.contractName
      scope.contractHeadId = val.contractHeadId
      this.$http({
        url:
          '/api-cm/contract/contractHead/queryPayPlanByContractHeadId',
        method: 'get',
        params: { contractHeadId: val.contractHeadId },
        loading: true
      })
        .then(data => {
          if (data.data.length) {
            scope.payMethod = data.data[0].payMethod
            scope.payExplain = data.data[0].payExplain
            scope.dateNum = data.data[0].dateNum
            data.data.forEach((i, indx) => {
              if (indx > 0) {
                this.form.contractList.unshift({
                  contractCode: val.contractCode,
                  contractName: val.contractName,
                  contractHeadId: val.contractHeadId,
                  payMethod: i.payMethod,
                  payExplain: i.payExplain,
                  dateNum: i.dateNum
                })
              }
            })
          }
        })
        .catch(err => {
          console.log(err)
        })
    },
    contractLook (index, row) {
      this.contractShow = true
      this.vendorId = row.vendorId
      this.form.contractList = row.lgtPayPlans
    },
    addList () {
      this.form.contractList.unshift({
        id: Math.floor(Math.random() * 1000000),
        contractCode: null,
        payMethod: null,
        payExplain: null,
        dateNum: null
      })
    },
    delList () {
      if (!this.checkContract.length) {
        this.$message({
          message: this.$t('logisticsMod.msgPurchaseApply[10]'),
          type: 'error'
        })
      }
      let arr = []
      this.form.contractList.map(i => {
        if (
          !this.checkContract.includes(i.id) &&
          !this.checkContract.includes(i.createdId)
        ) {
          arr.push(i)
        }
      })
      this.form.contractList = arr
    },
    checkContractList (data) {
      this.checkContract = data.map(i => i.id || i.createdId)
    },
    saveList () {
      this.$refs.contractForm.validate(valid => {
        if (valid) {
          this.contractShow = false
        } else {
          this.$message({
            message: this.$t('vendorMod.pleasefinishRequired'), // '请输入单据必填信息'
            type: 'error'
          })
        }
      })
    }
  }
}
</script>
<style scoped>
.toRequired {
  color: #ff4949;
  padding-right: 2px;
}
</style>
