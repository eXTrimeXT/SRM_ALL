<template>
  <el-container
    class="flex-container the_vendorPurchaseOrderList_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="preArr"
        form-label-width="100px"
        @getFormData="getQuerydata"
      />

      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <!-- 新增 -->
          <AuthorityButton
            type="primary"
            @click="showAdd(null)"
          >
            {{ $t('orderMod.buyerOrderSynergy.add') }}
          </AuthorityButton>
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :bigData="true"
        :table-header="tableHeader"
        :check-change="handleCurrentChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :auto-query="false"
        :url="tableUrl"
        :open-custom-table="true"
        :reserve-selection="true"
        :adeptMeiQl="true"
        row-key="orderNumber"
        :comActive="$attrs['changeTab']"
      />
    </el-main>
    <srm-dialog
      v-if="addShow"
      :title="title"
      class="org-selector-dialog"
      :visible.sync="addShow"
      :append-to-body="true"
      :close-on-click-modal="false"
      @close="closeAdd"
    >
      <el-form ref="relForm" :rules="formRules" :model="formData">
        <el-row :gutter="32">
          <el-col :span="12">
            <!-- <el-form-item :label="'单据编号'"> -->
            <el-form-item :label="$t('vendorMod.relegation.receiptNum')">
              <!-- <el-input v-model="formData.configNum" readonly placeholder="系统自动生成" /> -->
              <el-input v-model="formData.configNum" readonly :placeholder="$t('cusEntry.supplement20250205.systemAutoGeneration')" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <!-- <el-form-item :label="'业务日期'"> -->
            <el-form-item :label="$t('accountMod.businessDate')">
              <el-date-picker
                v-model="formData.startTime"
                type="datetime"
                :placeholder="$t('components.approvalHead.headers.selectNode')"
                :format="$formatDatePickerTime"
                value-format="yyyy-MM-dd HH:mm:ss"
                auto-complete="off"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="32">
          <el-col :span="12">
            <!-- <el-form-item :label="'采购单位'" prop="orgId"> -->
            <el-form-item :label="$t('cusEntry.reportManagement.createUserOrgOuName')" prop="orgId">
              <OrganizationSelector
                v-model="formData.orgId"
                node-type="OU"
                :parent-id="-1"
                :placeholder="$t('common.pleaseSelect')"
                @select="orgSelect"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <!-- <el-form-item :label="'配置金额'" prop="amount"> -->
            <el-form-item :label="$t('cusEntry.supplement20250205.configAmount')" prop="amount">
              <!-- <el-input-number
                v-model="formData.amount"
                style="width:100%"
                :precision="0"
                placeholder="请填写配置金额"
                controls-position="right"
              /> -->
              <el-input-number
                v-model="formData.amount"
                style="width:100%"
                :precision="0"
                :placeholder="$t('cusEntry.supplement20250205.configAmount2')"
                controls-position="right"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="32">
          <el-col :span="12">
            <!-- <el-form-item :label="'倍数'" prop="multiple"> -->
            <el-form-item :label="$t('cusEntry.supplement20250205.multiplier')" prop="multiple">
              <!-- 请填写倍数 -->
              <el-input-number
                v-model="formData.multiple"
                style="width:100%"
                :precision="0"
                :placeholder="$t('cusEntry.supplement20250205.fillMultiple')"
                controls-position="right"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <!-- '有效期(天)' -->
            <el-form-item :label="$t('cusEntry.supplement20250205.validityDays')" prop="validDays">
              <!-- 请填写有效期 -->
              <el-input-number
                v-model="formData.validDays"
                style="width:100%"
                :precision="0"
                :placeholder="$t('cusEntry.supplement20250205.validityPeriod')"
                controls-position="right"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="32">
          <el-col :span="24">
            <!-- 参考信息 -->
            <el-form-item :label="$t('cusEntry.supplement20250205.referenceInfo')">
              <el-input
                v-model="formData.comment"
                type="textarea"
                :rows="4"
                :placeholder="$t('cusEntry.supplement20250205.referenceInfo2')"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer">
        <el-button @click="closeAdd">
          {{ $t("common.cancel") }}
        </el-button>
        <el-button type="primary" @click="submit">
          {{ $t("common.confirm") }}
        </el-button>
      </div>
    </srm-dialog>
  </el-container>
</template>
<script>
import { parseTime } from '@/utils'
import { tabTodoMixin, tabTodoWatch } from '@/utils/mixins'
import ExportExcel from 'lib@/components/export-excel'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MainHeader from 'lib@/components/Table/MainHeader'
import TableView from 'lib@/components/Table/TableView'
import { transformMQL } from 'lib@/utils/util'
import OrganizationSelector from 'lib@/components/organization-selector'
export default {
  name: 'PurchaseOrderListBuyer',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    ExportExcel,
    OrganizationSelector
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      integrationMode: '',
      gridId: 'list',
      currentRows: [],
      tableUrl: '/api-sup-ce/api-ql/PrRecentPurchaseConfig/query',
      pageSize: 15,
      preArr: [
        {
          prop: 'orgName',
          // 采购单位
          label: this.$t('cusEntry.reportManagement.createUserOrgOuName')
        },
        {
          prop: 'amount',
          // 配置金额(元)
          label: this.$t('cusEntry.supplement20250205.configAmountYuan')
        },
        {
          prop: 'multiple',
          // 倍数
          label: this.$t('cusEntry.supplement20250205.multiplier')
        },
        {
          prop: 'validDays',
          // 有效期(天)
          label: this.$t('cusEntry.supplement20250205.validityDays')
        }
      ],
      tableHeader: [
        {
          prop: 'configNum',
          // 单据编号
          label: this.$t('vendorMod.relegation.receiptNum'),
          width: 120
        },
        {
          prop: 'startTime',
          // 业务日期
          label: this.$t('accountMod.businessDate'),
          width: 150,
          formattor: val => (val ? parseTime(val, '{y}-{m}-{d} {h}:{i}:{s}') : '')
        },
        {
          prop: 'orgName',
          // 采购单位
          label: this.$t('cusEntry.reportManagement.createUserOrgOuName'),
          width: 150
        },
        {
          prop: 'amount',
          // 配置金额
          label: this.$t('cusEntry.supplement20250205.configAmount'),
          width: 100
        },
        {
          prop: 'multiple',
          // 倍数
          label: this.$t('cusEntry.supplement20250205.multiplier'),
          width: 80
        },
        {
          prop: 'validDays',
          // 有效期
          label: this.$t('cusEntry.dataConfMod.extValidityDate'),
          width: 80
        },
        {
          prop: 'comment',
          // 参考信息
          label: this.$t('cusEntry.supplement20250205.referenceInfo'),
          width: 120
        },
        {
          prop: 'creationDate',
          // 创建日期
          label: this.$t('common.creationDate'),
          minWidth: 150,
          dataType: 'dateTime'
        },
        {
          prop: 'createdFullName',
          // 创建人
          label: this.$t('common.creator'),
          width: 100
        },
        {
          prop: 'createdByDepartment',
          // 创建单位
          label: this.$t('cusEntry.centralizedPurchase.createCompany'),
          width: 130
        },
        {
          prop: 'operation',
          label: () => this.$t('common.operation'),
          width: 150,
          btnStyle: 'text',
          fixed: 'right',
          showType: 'buttons',
          buttons: [
            {
              callback: row => this.showAdd(row),
              formattor: () => this.$t('common.edit')
            },
            {
              callback: row => this.handelDel(row),
              formattor: () => this.$t('common.delete')
            }
          ]
        }
      ],
      queryParam: {},
      addShow: false,
      formRules: {
        startTime: [
          // 请选择日期
          { required: true, message: this.$t('qualitySynergy.msgSelDate') }
        ],
        orgId: [
          // 请选择采购单位
          { required: true, message: this.$t('cusEntry.supplement20250205.selectPurchaseUnit') }
        ],
        amount: [
          // 请填写配置金额（只能为整数）
          { required: true, message: this.$t('cusEntry.supplement20250205.configAmount3'), trigger: 'blur' }
        ],
        multiple: [
          // 请填写倍数（只能为整数）
          { required: true, message: this.$t('cusEntry.supplement20250205.multipleInput'), trigger: 'blur' }
        ],
        validDays: [
          // 请填写有效期（只能为整数）
          { required: true, message: this.$t('cusEntry.supplement20250205.validityPeriod2'), trigger: 'blur' }
        ]
      },
      formData: {
        configId: null,
        startTime: null,
        orgId: null,
        orgCode: null,
        orgName: null,
        amount: null,
        multiple: null,
        validDays: null,
        comment: null
      },
      // 新增采购类配置参数
      title: this.$t('cusEntry.supplement20250205.newPurchaseConfigParams')
    }
  },
  created () {
    this.getQuerydata()
  },
  methods: {
    orgSelect (value) {
      if (value) {
        this.formData.orgName = value.organizationName
        this.formData.orgId = value.organizationId
        this.formData.orgCode = value.organizationCode
      } else {
        this.formData.orgName = null
        this.formData.orgId = null
        this.formData.orgCode = null
      }
    },
    getQuerydata (params = {}) {
      this.queryParam = transformMQL.listPageData({
        type: 'PrRecentPurchaseConfig',
        action: 'query',
        params
      })
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    handleCurrentChange (val) {
      this.currentRows = val
    },
    async getFlowIntegrationMode () {
      let res = await this.$api.base.flowAPI.getFlowIntegrationMode({ businessType: 'ORDER' })
      if (res.data) {
        this.integrationMode = res.data
      }
    },
    showAdd (item = null) {
      if (item) {
        this.formData = {
          ...this.formData, ...item
        }
        // 编辑采购类配置参数
        this.title = this.$t('cusEntry.supplement20250205.editPurchaseConfigParams')
      } else {
        this.formData = {
          configId: null,
          orgId: null,
          orgCode: null,
          orgName: null,
          startTime: null,
          amount: null,
          multiple: null,
          validDays: null,
          comment: null
        }
        // 新增采购类配置参数
        this.title = this.$t('cusEntry.supplement20250205.newPurchaseConfigParams')
      }
      this.addShow = true
    },
    closeAdd () {
      this.$refs.relForm.resetFields()
      this.addShow = false
    },
    submit () {
      this.$refs.relForm.validate(status => {
        if (status) {
          let userInfo = this.$store.getters.userInfo
          let param = {
            ...this.formData,
            createdBy: userInfo.userName,
            createdUserName: userInfo.nickname,
            createdByIp: userInfo.userId,
            createdByDepartment: userInfo.department
          }
          if (param.configId) {
            let params = transformMQL.save('PrRecentPurchaseConfig', [param], 'update')
            this.$http({
              url: '/api-sup-ce/api-ql/PrRecentPurchaseConfig/update',
              method: 'POST',
              data: params,
              loading: true
            }).then(res => {
              this.closeAdd()
              this.getQuerydata()
            })
          } else {
            let params = transformMQL.save('PrRecentPurchaseConfig', [param], 'save')
            this.$http({
              url: '/api-sup-ce/api-ql/PrRecentPurchaseConfig/save',
              method: 'POST',
              data: params,
              loading: true
            }).then(res => {
              this.closeAdd()
              this.getQuerydata()
            })
          }
        }
      })
    },
    handelDel (item) {
      // 此操作将永久删除该配置, 是否继续?
      this.$confirm(this.$t('cusEntry.supplement20250205.deleteConfigConfirmation'), this.$t('components.approvalHead.tips.tip'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('components.common.cancel'),
        type: 'warning'
      }).then(() => {
        let params = transformMQL.save('PrRecentPurchaseConfig', [item.configId], 'delete')
        this.$http({
          url: '/api-sup-ce/api-ql/PrRecentPurchaseConfig/delete',
          method: 'POST',
          data: params,
          loading: true
        }).then(res => {
          this.getQuerydata()
        })
      })
    }
  }
}
</script>
<style scoped lang="scss">
</style>
