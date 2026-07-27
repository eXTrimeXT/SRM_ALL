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
            <!-- <el-form-item :label="'管理单元'" prop="orgId"> -->
            <el-form-item :label="$t('cusEntry.supplement20250205.managementUnit')" prop="orgId">
              <!-- <el-input v-model="formData.orgName" placeholder="请输入内容" readonly class="input-with-select">
                <div slot="append" @click="openShowOrgDialog">
                  <el-button
                    icon="el-icon-search"
                    size="medium"
                  />
                </div>
              </el-input> -->
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
            <!-- <el-form-item :label="'管理单元编码'" prop="orgCode"> -->
            <el-form-item :label="this.$t('cusEntry.supplement20250205.managementUnitCode')" prop="orgCode">
              <!-- <el-input v-model="formData.orgCode" readonly placeholder="选择管理单元自动生成" /> -->
              <el-input v-model="formData.orgCode" readonly :placeholder="$t('cusEntry.supplement20250205.selectManageUnitAutoGeneration')" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="32">
          <el-col :span="4">
            <!-- <el-form-item :label="'是否参照本单位'" prop="rangFlag"> -->
            <el-form-item :label="$t('cusEntry.supplement20250205.isReferenceToThisUnit')" prop="rangFlag">
              <el-radio-group v-model="formData.rangFlag">
                <el-radio label="Y">
                  <!-- 是 -->
                 {{ $t("common.yes") }}
                </el-radio>
                <el-radio label="N">
                  <!-- 否 -->
                  {{ $t("common.no") }}
                </el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-if="formData.rangFlag == 'N'" :gutter="32">
          <el-col :span="12">
            <!-- <el-form-item :label="'区域'" prop="areaCodes"> -->
            <el-form-item :label="$t('vendorMod.area1')" prop="areaCodes">
              <dict-select
                v-model="formData.areaCodes"
                code="REGION"
                :multiple="true"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="32">
          <el-col :span="24">
            <!-- <el-form-item :label="'备注'"> -->
            <el-form-item :label="$t('components.eio.headers.remark')">
              <!-- <el-input
                v-model="formData.comment"
                type="textarea"
                :rows="4"
                placeholder="请填写备注信息"
              /> -->
              <el-input
                v-model="formData.comment"
                type="textarea"
                :rows="4"
                :placeholder="$t('cusEntry.supplement20250205.remarkInfo')"
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
    <!-- <srm-dialog
      :visible.sync="showOrgDialog"
      :title="'请选择管理单元'"
      size="middle"
      @close="onCancel"
    > -->
    <srm-dialog
      :visible.sync="showOrgDialog"
      :title="$t('cusEntry.supplement20250205.selectManagementUnit')"
      size="middle"
      @close="onCancel"
    >
      <div style="height: 300px;overflow: auto;">
        <Treeselect
          v-model="currentRows"
          :normalizer="normalizer"
          :no-children-text="$t('dataConfMod.noChildrenText')"
          :no-options-text="$t('dataConfMod.noOptionsText')"
          :no-results-text="$t('dataConfMod.noResultsText')"
          :placeholder="$t('dataConfMod.msgSelectOrgName')"
          :append-to-body="false"
          :searchable="true"
          :options="options"
          value-consists-of="ALL_WITH_INDETERMINATE"
          value-format="object"
          :always-open="true"
          auto-select-descendants
          :flatten-search-results="true"
          auto-deselect-descendants
          flat
        />
      </div>
      <div slot="footer">
        <el-button @click="onCancel">
          {{ $t("common.cancel") }}
        </el-button>
        <el-button type="primary" @click="addOneOrg">
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
import Treeselect from '@riophae/vue-treeselect'
import '@riophae/vue-treeselect/dist/vue-treeselect.css'
import { newOrganaztionTreehttp } from '@/api/common'
export default {
  name: 'PurchaseOrderListBuyer',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    ExportExcel,
    OrganizationSelector,
    Treeselect
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      integrationMode: '',
      gridId: 'list',
      currentRows: null,
      tableUrl: '/api-sup-ce/api-ql/PrVendorConfig/query',
      pageSize: 15,
      preArr: [
        {
          prop: 'orgName',
          // label: '管理单元名称'
          label: this.$t('cusEntry.supplement20250205.managementUnitName')
        }
      ],
      tableHeader: [
        {
          prop: 'orgCode',
          // label: '管理单元',
          label: this.$t('cusEntry.supplement20250205.managementUnit'),
          width: 120
        },
        {
          prop: 'orgName',
          // label: '管理单元名称',
          label: this.$t('cusEntry.supplement20250205.managementUnitName'),
          width: 150
        },
        {
          prop: 'rangFlag',
          // label: '是否参照本单元',
          label: this.$t('cusEntry.supplement20250205.isReferenceThisUnit'),
          width: 100,
          formattor: val => this.$getDictLabel('YES_OR_NO', val)
        },
        {
          prop: 'areaCodes',
          // label: '区域',
          label: this.$t('vendorMod.area1'),
          showType: 'dict',
          code: 'REGION',
          width: 120,
          formattor: val => this.getLabel(val ? val.split(',') : [])
        },
        {
          prop: 'status',
          // label: '状态',
          label: this.$t('components.stratProcess.headers.docStatusValue'),
          dataType: 'dict',
          code: 'RECENT_STATUS',
          width: 120
        },
        {
          prop: 'createdBy',
          // label: '创建人',
          label: this.$t('common.creator'),
          width: 120
        },
        {
          prop: 'creationDate',
          // label: '创建时间',
          label: this.$t('common.creationTime'),
          width: 150,
          dataType: 'dateTime'
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
              callback: row => this.handelUpDate(row),
              show: row => row.status == 'N',
              formattor: () => this.$t('common.enable')
            },
            {
              callback: row => this.handelUpDate(row),
              show: row => row.status == 'Y',
              formattor: () => this.$t('common.disable')
            }
          ]
        }
      ],
      queryParam: {},
      addShow: false,
      formRules: {
        orgCode: [
          // { required: true, message: '请选择管理单元编码' }
          { required: true, message: this.$t('cusEntry.supplement20250205.selectManagementUnitCode') }
        ],
        orgId: [
          // { required: true, message: '请选择管理单元' }
          { required: true, message: this.$t('cusEntry.supplement20250205.selectManagementUnit') }
        ],
        rangFlag: [
          // { required: true, message: '请选择是否参照本单位' }
          { required: true, message: this.$t('cusEntry.supplement20250205.isReferToThisUnit') }
        ],
        areaCodes: [
          // { required: true, message: '请选择区域' }
          { required: true, message: this.$t('cusEntry.supplement20250121.pleaseSelectRegion') }
        ]
      },
      formData: {
        configId: null,
        orgId: null,
        orgCode: null,
        orgName: null,
        rangFlag: null,
        areaCodes: null,
        comment: null,
        status: 'N'
      },
      options: [],
      showOrgDialog: false,
      multiple: false,
      // title: '新增历史供应商'
      title: this.$t('cusEntry.supplement20250205.newHistorySupplier')
    }
  },
  created () {
    this.getQuerydata()
  },
  async mounted () {
    const { data } = await this.newOrganaztionTreehttp({})
    this.options = data
  },
  methods: {
    getLabel (array) {
      let str = ''
      let index = array.length
      for (let item of array) {
        str += this.$getDictLabel('REGION', item)
        --index
        if (index != 0) {
          str += ','
        }
      }
      return str
    },
    onCancel () {
      this.currentRows = null
      this.showOrgDialog = false
    },
    addOneOrg () {
      let item = this.currentRows
      if (item) {
        this.formData.orgId = item.organizationId
        this.formData.orgName = item.organizationName
        this.formData.orgCode = item.organizationCode
      }
      this.showOrgDialog = false
    },
    openShowOrgDialog () {
      this.showOrgDialog = true
    },
    newOrganaztionTreehttp (data) {
      return this.$http({
        url: '/api-base/pj/organization/relation/treeNew',
        method: 'POST',
        data
      })
    },
    normalizer (node) {
      const NODE = {
        id: node.fullPathId,
        label: node.organizationName
      }
      if (node.childOrganRelation && node.childOrganRelation.length) { NODE.children = node.childOrganRelation }
      return NODE
    },
    orgSelect (value) {
      if (value) {
        this.formData.orgId = value.organizationId
        this.formData.orgCode = value.organizationCode
        this.formData.orgName = value.organizationName
      } else {
        this.formData.orgName = null
        this.formData.orgId = null
        this.formData.orgCode = null
      }
    },
    getQuerydata (params = {}) {
      this.queryParam = transformMQL.listPageData({
        type: 'PrVendorConfig',
        action: 'query',
        params
      })
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
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
        // this.title = '编辑历史供应商'
        this.title = this.$t('cusEntry.supplement20250205.editHistorySupplier')
        if (item.areaCodes) {
          this.formData.areaCodes = item.areaCodes.split(',')
        }
      } else {
        this.formData = {
          configId: null,
          orgId: null,
          orgCode: null,
          orgName: null,
          rangFlag: null,
          areaCodes: null,
          comment: null,
          status: 'N'
        }
        // this.title = '新增历史供应商'
        this.title = this.$t('cusEntry.supplement20250205.newHistorySupplier')
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
            createdByDepartment: userInfo.department,
            areaCodes: this.formData.areaCodes ? this.formData.areaCodes.join(',') : null
          }
          if (param.configId) {
            let params = transformMQL.save('PrVendorConfig', [param], 'update')
            this.$http({
              url: '/api-sup-ce/api-ql/PrVendorConfig/update',
              method: 'POST',
              data: params,
              loading: true
            }).then(res => {
              this.closeAdd()
              this.getQuerydata()
            })
          } else {
            let params = transformMQL.save('PrVendorConfig', [param], 'save')
            this.$http({
              url: '/api-sup-ce/api-ql/PrVendorConfig/save',
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
    handelUpDate (item) {
      let status = item.status == 'Y' ? 'N' : 'Y'
      let params = transformMQL.save('PrVendorConfig', [{ 'configId': item.configId, 'status': status }], 'update')
      this.$http({
        url: '/api-sup-ce/api-ql/PrVendorConfig/update',
        method: 'POST',
        data: params,
        loading: true
      }).then(res => {
        this.getQuerydata()
      })
    }
  }
}
</script>
<style scoped lang="scss">
.el-input-group__append >div{
    display: flex;
    justify-content: center;
}
</style>
