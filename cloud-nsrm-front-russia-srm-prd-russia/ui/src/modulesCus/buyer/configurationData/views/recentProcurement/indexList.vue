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
            <el-form-item :label="'单据编号'">
              <el-input v-model="formData.configNum" readonly placeholder="系统自动生成" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="'业务日期'">
              <el-date-picker
                v-model="formData.startTime"
                type="datetime"
                placeholder="请选择"
                value-format="yyyy-MM-dd HH:mm:ss"
                auto-complete="off"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="32">
          <el-col :span="12">
            <el-form-item :label="'采购单位'" prop="orgId">
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
            <el-form-item :label="'配置金额'" prop="amount">
              <el-input-number
                v-model="formData.amount"
                style="width:100%"
                :precision="0"
                placeholder="请填写配置金额"
                controls-position="right"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="32">
          <el-col :span="12">
            <el-form-item :label="'倍数'" prop="multiple">
              <el-input-number
                v-model="formData.multiple"
                style="width:100%"
                :precision="0"
                placeholder="请填写倍数"
                controls-position="right"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="'有效期(天)'" prop="validDays">
              <el-input-number
                v-model="formData.validDays"
                style="width:100%"
                :precision="0"
                placeholder="请填写有效期"
                controls-position="right"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="32">
          <el-col :span="24">
            <el-form-item :label="'参考信息'">
              <el-input
                v-model="formData.comment"
                type="textarea"
                :rows="4"
                placeholder="请填写参考信息"
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
          label: '采购单位'
        },
        {
          prop: 'amount',
          label: '配置金额(元)'
        },
        {
          prop: 'multiple',
          label: '倍数'
        },
        {
          prop: 'validDays',
          label: '有效期(天)'
        }
      ],
      tableHeader: [
        {
          prop: 'configNum',
          label: '单据编号',
          width: 120
        },
        {
          prop: 'startTime',
          label: '业务日期',
          width: 150,
          formattor: val => (val ? parseTime(val, '{y}-{m}-{d} {h}:{i}:{s}') : '')
        },
        {
          prop: 'orgName',
          label: '采购单位',
          width: 150
        },
        {
          prop: 'amount',
          label: '配置金额',
          width: 100
        },
        {
          prop: 'multiple',
          label: '倍数',
          width: 80
        },
        {
          prop: 'validDays',
          label: '有效期',
          width: 80
        },
        {
          prop: 'comment',
          label: '参考信息',
          width: 120
        },
        {
          prop: 'creationDate',
          label: '创建日期',
          minWidth: 150,
          formattor: val => (val ? parseTime(val, '{y}-{m}-{d}') : '')
        },
        {
          prop: 'createdFullName',
          label: '创建人',
          width: 100
        },
        {
          prop: 'createdByDepartment',
          label: '创建单位',
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
          { required: true, message: '请选择日期' }
        ],
        orgId: [
          { required: true, message: '请选择采购单位' }
        ],
        amount: [
          { required: true, message: '请填写配置金额（只能为整数）', trigger: 'blur' }
        ],
        multiple: [
          { required: true, message: '请填写倍数（只能为整数）', trigger: 'blur' }
        ],
        validDays: [
          { required: true, message: '请填写有效期（只能为整数）', trigger: 'blur' }
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
      title: '新增采购类配置参数'
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
        this.title = '编辑采购类配置参数'
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
        this.title = '新增采购类配置参数'
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
      this.$confirm('此操作将永久删除该配置, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
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
