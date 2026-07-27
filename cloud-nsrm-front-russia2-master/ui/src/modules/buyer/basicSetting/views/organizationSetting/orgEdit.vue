<template>
  <el-container
    class="flex-container org-edit"
    direction="vertical"
  >
    <el-main>
      <el-collapse
        v-model="activeDims"
      >
        <el-collapse-item
          :title="$t('dataConfMod.detail')"
          name="1"
        >
          <el-form
            ref="orgform1"
            :model="orgDataModel.orgDataform"
            :rules="orgDataModel.rules"
            label-position="top"
          >
            <srm-row>
              <srm-col>
                <!-- 组织类型 -->
                <el-form-item
                  :label="$t('dataConfMod.orgType')"
                  prop="organizationTypeCode"
                >
                  <DictSelect
                    v-model="orgDataModel.orgDataform.organizationTypeCode"
                    :disabled="curOpt != 'add'"
                    code="ORG_TYPE_ALL"
                    custom-select-type="ORG_TYPE_ALL"
                    @change-value="changeTypeHandle"
                  />
                </el-form-item>
              </srm-col>
              <srm-col>
                <!-- 组织编码 -->
                <el-form-item
                  :label="$t('common.orgCode')"
                  prop="organizationCode"
                >
                  <div class="org-pop-search">
                    <el-input
                      v-model="orgDataModel.orgDataform.organizationCode"
                      :disabled="!isSrmEdit || curOpt != 'add'"
                    />
                    <el-button
                      v-if="!isSrmEdit"
                      :disabled="!orgDataModel.orgDataform.organizationTypeCode"
                      icon="iconfont iconselect"
                      class="selectBtn"
                      @click="orgSearchBtnClick"
                    />
                  </div>
                </el-form-item>
              </srm-col>
              <srm-col>
                <!-- 组织名称 -->
                <el-form-item
                  :label="$t('dataConfMod.orgName')"
                  prop="organizationName"
                >
                  <el-input
                    v-model="orgDataModel.orgDataform.organizationName"
                    :disabled="!isSrmEdit"
                  />
                  <!-- ['COMPANY','BU', 'OU'].includes(orgDataModel.orgDataform.organizationTypeCode) -->
                </el-form-item>
              </srm-col>
              <srm-col>
                <!-- 生效日期 -->
                <el-form-item
                  :label="$t('dataConfMod.startDate')"
                  prop="startDate"
                >
                  <el-date-picker
                    v-model="orgDataModel.orgDataform.startDate"
                    type="date"
                    style="width: 100%"
                    :format="$formatDatePicker"
                    value-format="yyyy-MM-dd"
                    :placeholder="$t('dataConfMod.startDate')"
                    :disabled="!isSrmEdit"
                    @change="startDateChange"
                  />
                </el-form-item>
              </srm-col>

              <srm-col>
                <!-- 失效日期 -->
                <el-form-item
                  :label="$t('dataConfMod.endDate')"
                  prop="endDate"
                >
                  <el-date-picker
                    v-model="orgDataModel.orgDataform.endDate"
                    type="date"
                    style="width: 100%"
                    :format="$formatDatePicker"
                    value-format="yyyy-MM-dd"
                    :placeholder="$t('dataConfMod.endDate')"
                    :disabled="!isSrmEdit"
                    @change="endDateChange"
                  />
                </el-form-item>
              </srm-col>
              <srm-col>
                <!-- 上层组织名称 -->
                <el-form-item
                  :label="$t('dataConfMod.lastOrgName')"
                  prop="parentOrganizationNames"
                >
                  <div class="org-pop-search">
                    <el-input
                      v-model="orgDataModel.orgDataform.parentOrganizationNames"
                      :disabled="true"
                    />
                    <el-button
                      icon="iconfont iconselect"
                      class="selectBtn"
                      @click="searchParentOrgEdit('editSearch')"
                    />
                  </div>
                </el-form-item>
              </srm-col>
              <srm-col>
                <!-- erp库存组织ID -->
                <el-form-item
                  :label="$t('dataConfMod.erpOrgId')"
                  prop="erpOrgId"
                >
                  <el-input v-model="orgDataModel.orgDataform.erpOrgId" />
                </el-form-item>
              </srm-col>
            </srm-row>
          </el-form>
        </el-collapse-item>
        <!-- 收获地址 -->
        <el-collapse-item
          v-if="ouInvShow"
          :title="$t('dataConfMod.organizationSite')"
          name="2"
        >
          <!-- 收货地点 -->
          <div class="site-line">
            <!-- srm创建时可操作 -->
            <p
              v-if="isSrmEdit"
              class="btn_line"
            >
              <el-button
                type="primary"
                @click="addSiteList"
              >
                {{ $t('common.new') }}
              </el-button>
              <el-button
                type="primary"
                @click="delSiteList"
              >
                {{ $t('common.delete') }}
              </el-button>
            </p>
            <TableView
              ref="siteRef"
              :checkbox="true"
              table-height="200px"
              :table-infor="siteTableList"
              :table-header="siteTableHeader"
              :check-change="siteSelectChange"
              :page-enabled="false"
            >
              <!-- 地址类型 -->
              <template #siteType="{ scope }">
                <dict-select
                  v-if="!scope.row.siteId"
                  v-model="scope.row.siteType"
                  code="SITE_TYPE"
                />
                <span v-else>{{ $getDictLabel("SITE_TYPE", scope.row.siteType) }}</span>
              </template>
            </TableView>
          </div>
        </el-collapse-item>
        <!-- 公司基本信息 -->
        <el-collapse-item
          v-if="companyShow"
          :title="$t('dataConfMod.companyBasicInfo')"
          name="3"
        >
          <!-- 公司信息 -->
          <el-form
            ref="companyForm"
            :model="orgCompany"
            :rules="orgCompanyRules"
            label-position="top"
            :disabled="!isSrmEdit"
          >
            <srm-row>
              <srm-col>
                <!-- 税号 -->
                <el-form-item :label="$t('dataConfMod.taxNumber')">
                  <el-input v-model="orgCompany.taxNumber" />
                </el-form-item>
              </srm-col>

              <srm-col>
                <!-- 有效日期从 -->
                <el-form-item :label="$t('dataConfMod.effectiveDateFrom')" prop="startDate">
                  <el-date-picker
                    v-model="orgCompany.startDate"
                    type="date"
                    style="width: 100%"
                    :format="$formatDatePicker"
                    value-format="yyyy-MM-dd"
                    :placeholder="$t('dataConfMod.effectiveDateFrom')"
                  />
                </el-form-item>
              </srm-col>
              <srm-col>
                <!-- 有效日期至 -->
                <el-form-item :label="$t('dataConfMod.withRetrospectiveEffect')">
                  <el-date-picker
                    v-model="orgCompany.endDate"
                    type="date"
                    style="width: 100%"
                    :format="$formatDatePicker"
                    value-format="yyyy-MM-dd"
                    :placeholder="$t('dataConfMod.withRetrospectiveEffect')"
                  />
                </el-form-item>
              </srm-col>
              <srm-col>
                <!-- 是否有效 -->
                <el-form-item :label="$t('contractMod.isValid')">
                  <DictSelect
                    v-model="orgCompany.enabledFlag"
                    code="YES_OR_NO"
                  />
                </el-form-item>
              </srm-col>
              <srm-col>
                <!-- email -->
                <el-form-item :label="$t('common.email')">
                  <el-input v-model="orgCompany.email" />
                </el-form-item>
              </srm-col>
            </srm-row>
          </el-form>
        </el-collapse-item>
        <!-- 公司联系人 -->
        <el-collapse-item
          v-if="companyShow"
          :title="$t('dataConfMod.companyContactPerson')"
          name="4"
        >
          <!-- 收货地点 -->
          <div class="site-line">
            <p v-if="isSrmEdit" class="btn_line">
              <el-button
                type="primary"
                @click="addPersonList"
              >
                {{ $t('common.new') }}
              </el-button>
              <el-button
                type="primary"
                @click="delPersonList"
              >
                {{ $t('common.delete') }}
              </el-button>
            </p>
            <TableView
              ref="personRef"
              :checkbox="true"
              table-height="200px"
              :table-infor="personTableList"
              :table-header="personTableHeader"
              :check-change="personSelectChange"
              :page-enabled="false"
            >
              <template #username="{ scope }">
                <QuickSearch
                  :show-input="scope.row.username"
                  show-key="username"
                  allow-input
                  :scope-data="scope.row"
                  name="scc_rbac_user_display"
                  @close-quicksearch="setUserObj"
                />
              </template>
              <template #phone="{ scope }">
                <el-input v-model="scope.row.phone" @change="(val) => phoneChange(val,scope)" />
              </template>
              <template #email="{ scope }">
                <el-input v-model="scope.row.email" @change="(val) => emailChange(val,scope)" />
              </template>
            </TableView>
          </div>
        </el-collapse-item>
        <!-- 公司账号 -->
        <el-collapse-item
          v-if="companyShow"
          :title="$t('dataConfMod.companyAccount')"
          name="5"
        >
          <!-- 收货地点 -->
          <div class="site-line">
            <p v-if="isSrmEdit" class="btn_line">
              <el-button
                type="primary"
                @click="addBankList"
              >
                {{ $t('common.new') }}
              </el-button>
              <el-button
                type="primary"
                @click="delBankList"
              >
                {{ $t('common.delete') }}
              </el-button>
            </p>
            <TableView
              ref="bankRef"
              :checkbox="true"
              table-height="200px"
              :table-infor="bankTableList"
              :table-header="bankTableHeader"
              :check-change="bankSelectChange"
              :page-enabled="false"
            >
              <template #bankNum="{ scope }">
                <QuickSearch
                  :show-input="scope.row.bankNum"
                  show-key="bankNum"
                  :pre-query-data="{ 't.attr1': 'Y' }"
                  :scope-data="scope.row"
                  name="ceea_base_erp_branch_bank_info"
                  @close-quicksearch="getBankObj"
                />
              </template>
            </TableView>
          </div>
        </el-collapse-item>
        <!-- 公司地址 -->
        <el-collapse-item
          v-if="companyShow"
          :title="$t('dataConfMod.companyAddress')"
          name="6"
        >
          <!-- 收货地点 -->
          <div class="site-line">
            <p v-if="isSrmEdit" class="btn_line">
              <el-button
                type="primary"
                @click="addAddressList"
              >
                {{ $t('common.new') }}
              </el-button>
              <el-button
                type="primary"
                @click="delAddressList"
              >
                {{ $t('common.delete') }}
              </el-button>
            </p>
            <TableView
              ref="addressRef"
              :checkbox="true"
              table-height="200px"
              :table-infor="addressTableList"
              :table-header="addressTableHeader"
              :check-change="addressSelectChange"
              :page-enabled="false"
            >
              <template #country="{ scope }">
                <DictSelect
                  v-model="scope.row.country"
                  code="country"
                  :disabled="!isSrmEdit"
                  @change="countryChange(scope.row)"
                />
              </template>
              <template #area="{ scope }">
                <DictSelect
                  v-model="scope.row.area"
                  code="PROVINCE"
                  custom-select-type="PROVINCE"
                  :disabled="scope.row.country !== 'CN'"
                />
              </template>
              <template #city="{ scope }">
                <DictSelect
                  v-model="scope.row.city"
                  :code="scope.row.area"
                  custom-select-type="CITY"
                  :disabled="scope.row.country !== 'CN'"
                />
              </template>
            </TableView>
          </div>
        </el-collapse-item>
      </el-collapse>
      <!-- 选择上层组织弹框 -->
      <parentOrg
        :visible="parentOrgdialogVisible"
        :multiple="false"
        :organization-id="null"
        :selection-id="orgRelData"
        @on-ok="comfirmSelect"
        @on-cancle="parentOrgdialogVisible = false"
      />
      <sharedOrg
        :visible="sharedOrgVisible"
        :orgTypeCode="orgDataModel.orgDataform.organizationTypeCode"
        :orgCode="orgDataModel.orgDataform.organizationCode"
        :parentCode="orgDataModel.orgDataform.organizationCode"
        @on-ok="sharedOrgcomfirmSelect"
        @on-cancle="sharedOrgVisible = false"
      />
      <CToolbar>
        <template slot="right">
          <el-button @click="closePage">
            {{ $t('bidMod.cancel') }}
          </el-button>
          <el-button type="primary" @click="confirmSave">
            {{ $t('orderMod.buyerOrderSynergy.confirm') }}
          </el-button>
        </template>
      </CToolbar>
    </el-main>
  </el-container>
</template>
<script>
import QuickSearch from 'lib@/components/QuickSearch'
import CToolbar from 'lib@/components/c-toolbar'
import TableView from 'lib@/components/Table/TableView'
import parentOrg from './parentOrg'
import sharedOrg from './sharedOrg'
import { parseTime } from '@/utils'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import { organizationSetting } from 'modb@/basicSetting/api/basicSetting'
import { getOpenConfigAfterLogin } from '@/api/common'
import { validEmail, validatePhone } from '@/utils/validate'

import { createDictClass } from '@/library/utils/dict/dict-utils'
const orgTypeDictClass = createDictClass().setCustomSelectType('ORG_TYPE_ALL').loadCustomSelectType('ORG_TYPE_ALL')

let seed = 0
function generateUniqueId () {
  const index = ++seed
  return `custom_unique_id_${index}`
}
export default {
  name: 'OrgEdit',
  components: {
    parentOrg,
    sharedOrg,
    TableView,
    CToolbar,
    QuickSearch
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      curOpt: '',
      orgTypeDictClass: orgTypeDictClass, // 组织类型字典
      activeDims: ['1', '2', '3', '4', '5', '6'],
      orgRowInfo: {},
      siteSelectList: [], // 选中的收货地点列表
      siteTableList: [], // 收货地点列表
      siteTableHeader: [], // 收货地点表头

      personSelectList: [], // 选中的收货地点列表
      personTableList: [], // 收货地点列表
      personTableHeader: [], // 收货地点表头

      bankSelectList: [], // 选中的收货地点列表
      bankTableList: [], // 收货地点列表
      bankTableHeader: [], // 收货地点表头

      addressSelectList: [], // 选中的收货地点列表
      addressTableList: [], // 收货地点列表
      addressTableHeader: [], // 收货地点表头
      orgCompany: {
        companyName: null,
        taxNumber: null,
        startDate: parseTime(
          new Date(),
          '{y}-{m}-{d}',
          true
        ),
        endDate: null,
        enabledFlag: null,
        email: null
      },
      orgCompanyRules: {
        startDate: [{ required: true, message: this.$t('dataConfMod.msgStartDate') }]
      },
      orgCreationMode: '', // 组织创建方式： srm/iam 接口返回
      orgDataModel: {
        orgDataform: {
          // 组织名称
          organizationName: '',
          // 组织编码
          organizationCode: '',
          // ERPID
          erpOrgId: '',
          // 开始时间
          startDate: '',
          // 失效时间
          endDate: '',
          // 组织类型ID
          organizationTypeId: '',
          // 组织类型code
          organizationTypeCode: '',
          // 组织类型名称
          organizationTypeName: '',
          // 上层组织名称显示
          parentOrganizationNames: '',
          parentOrganizationIds: '',
          ceeaCompanyName: '',
          ceeaCurrencyId: null,
          ceeaCurrencyCode: null,
          ceeaCurrencyName: null
        },
        rules: {
          organizationCode: [{ required: true, message: this.$t('dataConfMod.msgOrgCode') }],
          organizationTypeCode: [
            { required: true, message: this.$t('dataConfMod.msgOrgTypeCode') }
          ],
          organizationTypeName: [
            { required: true, message: this.$t('dataConfMod.msgOrgTypeName') }
          ],
          organizationName: [{ required: true, message: this.$t('dataConfMod.msgOrgName') }],
          startDate: [{ required: true, message: this.$t('dataConfMod.msgStartDate') }]
        }
      },
      orgRelData: '',
      parentOrgdialogVisible: false,
      sharedOrgVisible: false
    }
  },
  computed: {
    companyShow () {
      return ['COMPANY'].includes(this.orgDataModel.orgDataform.organizationTypeCode) // && this.curOpt === 'edit'
    },
    ouInvShow () {
      return ['OU', 'INV'].includes(this.orgDataModel.orgDataform.organizationTypeCode) // && this.curOpt === 'edit'
    },
    // srm方式 可编辑
    isSrmEdit () {
      return this.orgCreationMode === 'srm'
    }
  },
  async created () {
    const configData = await getOpenConfigAfterLogin() // 获取配置
    this.orgCreationMode = configData.data.orgCreationMode
    console.log('orgCreationMode', this.orgCreationMode)
    this.curOpt = this.$attrs.params.flag
    // 业务实体收获地址
    this.siteTableHeader = [
      // '地点名称'
      {
        prop: 'siteName',
        label: () => this.$t('vendorMod.siteName'),
        showType: 'input',
        editable: row => {
          return this.isSrmEdit
        }
      },
      // '详细地址'
      {
        prop: 'siteDesc',
        label: () => this.$t('dataConfMod.siteDesc'),
        showType: 'input',
        editable: row => {
          return this.isSrmEdit
        }
      },
      // '地址类型'
      {
        prop: 'siteType',
        label: () => this.$t('dataConfMod.siteType'),
        showType: 'slot',
        editable: row => {
          return this.isSrmEdit
        },
        slot: 'siteType'
      },
      // '联系人'
      {
        prop: 'receiver',
        label: () => this.$t('bidMod.contactMan'),
        showType: 'input',
        editable: row => {
          return this.isSrmEdit
        }
      },
      // '联系电话'
      {
        prop: 'receiverPhone',
        label: () => this.$t('contractMod.mobileNumber'),
        showType: 'input',
        editable: row => {
          return this.isSrmEdit
        }
      },
      // '状态'
      {
        prop: 'status',
        label: () => this.$t('common.status'),
        showType: 'dictSelect',
        editable: row => {
          return this.isSrmEdit
        },
        code: 'YES_OR_NO'
      }
    ]

    // 公司联系人信息
    this.personTableHeader = [
      {
        prop: 'username',
        label: () => this.$t('dataConfMod.username'),
        showType: 'slot',
        slot: 'username',
        editable: () => {
          return this.isSrmEdit
        }
      },
      // '联系人姓名'
      {
        prop: 'name',
        label: () => this.$t('dataConfMod.contactName')
        // showType: 'input',
        // editable: () => {
        //   return this.isSrmEdit
        // }
      },
      // '部门'
      {
        prop: 'department',
        label: () => this.$t('dataConfMod.department'),
        showType: 'input',
        editable: () => {
          return this.isSrmEdit
        }
      },
      // '职位'
      {
        prop: 'position',
        label: () => this.$t('dataConfMod.position'),
        showType: 'input',
        editable: () => {
          return this.isSrmEdit
        }
      },
      // '联系电话'
      {
        prop: 'phone',
        label: () => this.$t('contractMod.mobileNumber'),
        showType: 'slot',
        editable: () => {
          return this.isSrmEdit
        },
        slot: 'phone'
      },
      // '邮箱'
      {
        prop: 'email',
        label: () => this.$t('dataConfMod.email'),
        showType: 'slot',
        editable: () => {
          return this.isSrmEdit
        },
        slot: 'email'
      },
      // '是否默认联系人'
      {
        prop: 'isDefault',
        label: () => this.$t('dataConfMod.isDefaultConcat'),
        showType: 'dictSelect',
        minWidth: 130,
        editable: () => {
          return this.isSrmEdit
        },
        code: 'YES_OR_NO',
        callback: (row, scope, dictItem) => {
          row['isDefaultView'] = dictItem.label
        }
      },
      // '是否启用'
      {
        prop: 'isActive',
        label: () => this.$t('dataConfMod.enabledUse'),
        showType: 'dictSelect',
        editable: () => {
          return this.isSrmEdit
        },
        code: 'YES_OR_NO',
        callback: (row, scope, dictItem) => {
          row['isActiveView'] = dictItem.label
        }
      }
    ]

    // 银行信息
    this.bankTableHeader = [
      // '银行编号'
      {
        prop: 'bankNum',
        label: () => this.$t('components.bank.bankNum'),
        showType: 'slot',
        slot: 'bankNum',
        editable: () => {
          return this.isSrmEdit
        }
      },
      // '银行名称'
      {
        prop: 'bankName',
        label: () => this.$t('components.bank.bankName'),
        showType: 'input',
        editable: () => {
          return false
        }
      },
      // '分行编码'
      {
        prop: 'branchBankNum',
        label: () => this.$t('components.bank.unionCode'),
        showType: 'input',
        editable: () => {
          return false
        }
      },
      // '开户行名称'
      {
        prop: 'branchBankName',
        label: () => this.$t('components.bank.branchBankName'),
        showType: 'input',
        editable: () => {
          return false
        }
      },
      // '账户名称'
      {
        prop: 'accountName',
        label: () => this.$t('components.bank.accountName'),
        showType: 'input',
        editable: () => {
          return this.isSrmEdit
        }
      },
      // '银行账号'
      {
        prop: 'bankAccount',
        label: () => this.$t('components.bank.bankAccount'),
        showType: 'input',
        editable: () => {
          return this.isSrmEdit
        }
      },
      // '是否主账户'
      {
        prop: 'isMain',
        label: () => this.$t('components.bank.isMain'),
        showType: 'dictSelect',
        editable: () => {
          return this.isSrmEdit
        },
        code: 'YES_OR_NO',
        callback: (row, scope, dictItem) => {
          row['isMainView'] = dictItem.label
        }
      },
      // '是否启用'
      {
        prop: 'isActive',
        label: () => this.$t('dataConfMod.enabledUse'),
        showType: 'dictSelect',
        editable: () => {
          return this.isSrmEdit
        },
        code: 'YES_OR_NO',
        callback: (row, scope, dictItem) => {
          row['isActiveView'] = dictItem.label
        }
      }
    ]

    // 公司地址
    this.addressTableHeader = [
      // '国家'
      {
        prop: 'country',
        label: () => this.$t('components.address.country'),
        showType: 'slot',
        editable: () => {
          return this.isSrmEdit
        },
        slot: 'country'
      },
      // '地区'
      {
        prop: 'area',
        label: () => this.$t('components.address.area'),
        showType: 'slot',
        editable: row => {
          return this.isSrmEdit && row.country === 'CN'
        },
        slot: 'area'
      },
      // '城市'
      {
        prop: 'city',
        label: () => this.$t('components.address.city'),
        showType: 'slot',
        editable: row => {
          return this.isSrmEdit && row.country === 'CN'
        },
        slot: 'city'
      },
      // '详细地址'
      {
        prop: 'address',
        label: () => this.$t('components.address.detailAddress'),
        showType: 'input',
        editable: () => {
          return this.isSrmEdit
        }
      },
      // '联系电话'
      {
        prop: 'phone',
        label: () => this.$t('contractMod.mobileNumber'),
        showType: 'input',
        editable: () => {
          return this.isSrmEdit
        }
      },
      // '邮政编码'
      {
        prop: 'postalCode',
        label: () => this.$t('components.address.postalCode'),
        showType: 'input',
        editable: () => {
          return this.isSrmEdit
        }
      },
      // '是否启用'
      {
        prop: 'isActive',
        label: () => this.$t('dataConfMod.enabledUse'),
        showType: 'dictSelect',
        editable: () => {
          return this.isSrmEdit
        },
        code: 'YES_OR_NO',
        callback: (row, scope, dictItem) => {
          row['isActiveView'] = dictItem.label
        }
      }
    ]

    if (this.curOpt == 'add') {
      const formObj = this.orgDataModel.orgDataform
      Object.keys(formObj).forEach(key => (formObj[key] = ''))
      // 当前日期
      this.orgDataModel.orgDataform.startDate = parseTime(
        new Date(),
        '{y}-{m}-{d}',
        true
      )
      this.resetCompany()
      this.resetOu()
    } else {
      let organizationId = this.$attrs.params.organizationId
      this.getDataForEdit(organizationId)
    }
  },
  mounted () {
    this.tableDoLayout()
  },
  methods: {
    phoneChange (value, scope) {
      if (value && !validatePhone(value)) {
        scope.row.phone = ''
        this.$message({
          message: this.$t('dataConfMod.fillPhone'),
          type: 'warning'
        })
      }
    },
    emailChange (value, scope) {
      if (value && !validEmail(value)) {
        scope.row.email = ''
        this.$message({
          message: this.$t('dataConfMod.fillEmail'),
          type: 'warning'
        })
      }
    },
    getBankObj (value, scope) {
      console.log(value)
      scope.bankNum = value?.bankNum
      scope.bankName = value?.bankName
      scope.branchBankName = value?.branchBankName
      scope.branchBankNum = value?.branchBankNum
    },
    startDateChange (val) {
      if (this.companyShow) {
        this.orgCompany.startDate = val
      }
    },
    endDateChange (val) {
      if (this.companyShow) {
        this.orgCompany.endDate = val
      }
    },
    setUserObj (value, scope) {
      if (value) {
        scope.username = value.username
        scope.name = value.nickname
      } else {
        scope.username = ''
        scope.name = ''
      }
    },
    countryChange (row) {
      if (row.country !== 'CN') {
        row.area = null
        row.city = null
      }
    },
    // 解决表格错位问题
    tableDoLayout () {
      this.$nextTick(() => {
        this.$refs.siteRef && this.$refs.siteRef.doLayout()
        this.$refs.personRef && this.$refs.personRef.doLayout()
        this.$refs.bankRef && this.$refs.bankRef.doLayout()
        this.$refs.addressRef && this.$refs.addressRef.doLayout()
      })
    },

    // 新增收货地址
    addSiteList () {
      this.$refs.siteRef.addOneEditableColumn({
        'tempRowKey': generateUniqueId(),
        siteName: '',
        siteDesc: '',
        siteType: '',
        organizationId: this.orgRowInfo.organizationId,
        organizationCode: this.orgRowInfo.organizationCode,
        organizationName: this.orgRowInfo.organizationName
      })
    },
    addPersonList () {
      this.$refs.personRef.addOneEditableColumn({
        'tempRowKey': generateUniqueId(),
        name: '',
        sex: null,
        department: '',
        position: '',
        phone: '',
        email: '',
        isDefault: 'N',
        isActive: 'N',
        organizationId: this.orgRowInfo.organizationId
      })
    },
    addBankList () {
      this.$refs.bankRef.addOneEditableColumn({
        'tempRowKey': generateUniqueId(),
        bankNum: '',
        bankName: '',
        branchBankName: '',
        accountName: '',
        bankAccount: '',
        isMain: 'N',
        isActive: 'N',
        organizationId: this.orgRowInfo.organizationId
      })
    },
    addAddressList () {
      this.$refs.addressRef.addOneEditableColumn({
        'tempRowKey': generateUniqueId(),
        country: null,
        area: '',
        city: '',
        address: '',
        postalCode: '',
        isActive: 'N',
        organizationId: this.orgRowInfo.organizationId
      })
    },
    // 删除收货地址
    delSiteList () {
      this.deleteBySelect(this.siteSelectList, this.siteTableList)
    },
    delPersonList () {
      this.deleteBySelect(this.personSelectList, this.personTableList)
    },
    delBankList () {
      this.deleteBySelect(this.bankSelectList, this.bankTableList)
    },
    delAddressList () {
      this.deleteBySelect(this.addressSelectList, this.addressTableList)
    },
    // 选中行 所有行
    deleteBySelect (selectList = [], currentList = []) {
      for (let item of selectList) {
        let selectRowKey = item.tempRowKey
        for (let curItem of currentList) {
          let curRowKey = curItem.tempRowKey
          if (selectRowKey === curRowKey) {
            let rowIndex = currentList.findIndex(k => (k.tempRowKey == selectRowKey))
            currentList.splice(rowIndex, 1)
            break
          }
        }
      }
    },
    // 收货地点选中方法
    siteSelectChange (select) {
      this.siteSelectList = select
    },
    personSelectChange (select) {
      this.personSelectList = select
    },
    bankSelectChange (select) {
      this.bankSelectList = select
    },
    addressSelectChange (select) {
      this.addressSelectList = select
    },

    // 切换类型
    changeTypeHandle (value, orgTypeItem) {
      let obj = orgTypeItem ? orgTypeItem.element : {}
      if (!obj) {
        obj = {}
      }
      this.orgDataModel.orgDataform.organizationTypeCode = obj.organizationTypeCode
      this.orgDataModel.orgDataform.organizationTypeId = obj.typeId
      this.orgDataModel.orgDataform.organizationTypeName = obj.organizationTypeName
      // 切换类型清掉以下字段数据
      this.orgDataModel.orgDataform.organizationCode = ''
      this.orgDataModel.orgDataform.organizationName = ''
      this.orgDataModel.orgDataform.startDate = ''
      this.orgDataModel.orgDataform.endDate = ''

      if (value !== 'COMPANY') {
        this.resetCompany()
      }
      if (value === 'COMPANY') {
        this.initOrgCompany()
      }
      if (value !== 'OU') {
        this.resetOu()
      }
    },
    resetOu () {
      this.siteTableList = []
    },
    initOrgCompany () {
      this.orgCompany = {
        companyName: null,
        taxNumber: null,
        startDate: parseTime(
          new Date(),
          '{y}-{m}-{d}',
          true
        ),
        endDate: null,
        enabledFlag: null,
        email: null
      }
    },
    resetCompany () {
      this.orgCompany = this.initOrgCompany()
      this.personTableList = []
      this.bankTableList = []
      this.addressTableList = []
    },

    // 编辑
    searchParentOrgEdit (type) {
      this.parentOrgdialogVisible = true
    },
    // 编辑之前先获取数据
    async getDataForEdit (organizationId) {
      const params = { organizationId }
      const { data } = await organizationSetting.getOrganization(params)
      if (data) {
        const { organization, siteList, orgCompany, orgCompanyPersonList, orgCompanyBankList, orgCompanyAddressList } = data
        // 返回数据处理
        this.orgDataModel.orgDataform = Object.assign(
          this.orgDataModel.orgDataform,
          organization
        )
        this.orgCompany = orgCompany || {}
        // 上一次选择的上层组织ID
        this.orgRelData = organization.parentOrganizationIds || ''
        const siteArray = (siteList || []).map(i => ({
          ...i,
          'tempRowKey': generateUniqueId()
        }))
        const orgCompanyPersonArray = (orgCompanyPersonList || []).map(i => ({
          ...i,
          'tempRowKey': generateUniqueId()
        }))
        const orgCompanyBankArray = (orgCompanyBankList || []).map(i => ({
          ...i,
          'tempRowKey': generateUniqueId()
        }))
        const orgCompanyAddressArray = (orgCompanyAddressList || []).map(i => ({
          ...i,
          'tempRowKey': generateUniqueId()
        }))
        this.siteTableList = siteArray
        this.personTableList = orgCompanyPersonArray
        this.bankTableList = orgCompanyBankArray
        this.addressTableList = orgCompanyAddressArray
      }
    },
    // 新增编辑组织数据
    saveOrUpdateOrgHandle (opt) {
      const submitData = this.orgDataModel.orgDataform
      if (opt === 'add') { // 新增
        submitData.dataResource = 'add' // 后端数据处理标识
      } else {
        submitData.dataResource = 'update'
      }
      this.$refs.orgform1.validate(valid => {
        if (valid) {
          organizationSetting.orgSaveOrUpdate({
            organization: submitData,
            siteList: this.siteTableList,
            orgCompany: this.orgCompany,
            orgCompanyPersonList: this.personTableList,
            orgCompanyBankList: this.bankTableList,
            orgCompanyAddressList: this.addressTableList
          }).then(res => {
            if (res) {
              // 返回数据处理
              this.$message({
                message: res.message,
                type: 'success'
              })
              this.closePage() // 保存成功关闭页面
            }
          })
        } else {
          return false
        }
      })
    },
    // 保存数据
    confirmSave () {
      if (this.curOpt === 'add') {
        this.saveOrUpdateOrgHandle('add')
      } else {
        this.saveOrUpdateOrgHandle('edit')
      }
    },
    closePage () {
      this.$emit('tab-remove', this.$attrs['tabName'])
      this.__setTabTodo('OrgList.updataPageData')
    },
    // 父级组件 确认选择
    comfirmSelect (data = []) {
      this.orgRelData = ''
      if (data.length > 0) {
        let pName = ''
        let pIds = ''
        let pCodes = ''
        data.forEach((item, index) => {
          if (index > 0) {
            pName += ',' + item.organizationName
            pIds += ',' + item.organizationId
            pCodes += ',' + item.organizationCode
            // 关联上层组织的id 集合
            this.orgRelData += ',' + item.organizationId
          } else {
            pName += item.organizationName
            pIds += item.organizationId
            pCodes += item.organizationCode
            // 关联上层组织的id 集合
            this.orgRelData += item.organizationId
          }
        })
        // 显示名称
        this.orgDataModel.orgDataform.parentOrganizationNames = pName
        this.orgDataModel.orgDataform.parentOrganizationIds = pIds
        this.orgDataModel.orgDataform.parentOrganizationCodes = pCodes
      } else {
        this.orgDataModel.orgDataform.parentOrganizationNames = ''
        this.orgDataModel.orgDataform.parentOrganizationIds = ''
      }
      this.parentOrgdialogVisible = false
    },
    // 组织选择
    orgSearchBtnClick () {
      this.sharedOrgVisible = true
    },
    // 中台组织数据选择
    sharedOrgcomfirmSelect (data = []) {
      console.log(data)
      if (data.length > 0) {
        let orgRow = data[0]
        if (this.curOpt == 'add') {
          this.orgDataModel.orgDataform.organizationCode = orgRow.organizationCode
          this.orgDataModel.orgDataform.organizationName = orgRow.organizationName
          this.orgDataModel.orgDataform.organizationId = orgRow.organizationId
          this.orgDataModel.orgDataform.startDate = orgRow.startDate
          this.orgDataModel.orgDataform.endDate = orgRow.endDate
        } else {
          if (this.orgDataModel.orgDataform.organizationCode == orgRow.organizationCode) { // 编辑时只能选择自己
            this.orgDataModel.orgDataform.startDate = orgRow.startDate
            this.orgDataModel.orgDataform.endDate = orgRow.endDate
          } else {
            this.$message.error(this.$t('dataConfMod.sharedOrgcomfirmSelect'))
            return false
          }
        }
      } else {
        this.orgDataModel.orgDataform.organizationCode = ''
        this.orgDataModel.orgDataform.organizationName = ''
        this.orgDataModel.orgDataform.organizationId = ''
        this.orgDataModel.orgDataform.startDate = ''
        this.orgDataModel.orgDataform.endDate = ''
      }
      this.sharedOrgVisible = false
    }
  }
}
</script>

<style lang="scss" scoped>
.btn_line{
  margin: 0 0 8px;
}
.org-pop-search{
  position: relative;
  display:inline-block;
  width:100%;
  .selectBtn{
    position: absolute;
    width: 30px;
    top: 1px;
    bottom: 1px;
    right: 1px;
    z-index: 100;
    line-height: 26px;
    padding: 0;
    cursor: pointer;
    min-width: 30px;
    border: 0;
    border-radius: 0 4px 4px 0;
  }
}
</style>
<style>
.org-pop-search .el-input__inner{
  padding-right: 30px;
}
</style>
