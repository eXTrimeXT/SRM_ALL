<template>
  <el-container
    class="flex-container company-page"
    direction="vertical"
  >
    <el-main>
      <div style="padding: 10px 15px">
        <el-form
          ref="companyForm"
          class="base-form-info form-fill-style"
          :model="companyData"
          :rules="comRules"
        >
          <el-row :gutter="50">
            <el-col :span="8">
              <!-- 税号 -->
              <el-form-item
                prop="taxNumber"
                :label="$t('dataConfMod.taxNumber')"
              >
                <el-input v-model="companyData.taxNumber" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <!-- 公司名称 -->
              <el-form-item
                prop="companyName"
                :label="$t('dataConfMod.ceeaCompanyName')"
              >
                <el-input
                  v-model="companyData.companyName"
                  disabled
                />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>
      <el-collapse
        v-model="activeDims"
        class="tab-form-style"
      >
        <!-- 联系人信息 -->
        <el-collapse-item
          :title="$t('dataConfMod.contactInfo')"
          name="1"
        >
          <div style="margin-bottom: 8px">
            <el-button
              type="primary"
              class="detail-pbtn"
              @click="addContactInfo"
            >
              {{ $t('common.new') }}
            </el-button>
          </div>

          <el-table
            ref="bankTable"
            :data="companyData.companyPeoples"
            style="width: 100%"
            max-height="250px"
            border
          >
            <el-table-column
              align="center"
              type="index"
              width="50"
            />
            <!-- 联系人姓名 -->
            <el-table-column
              align="center"
              prop="name"
              :label="$t('dataConfMod.contactName')"
              width="100"
              :show-overflow-tooltip="true"
            >
              <template slot="header">
                <span class="redText">*</span>{{ $t('dataConfMod.contactName') }}
              </template>
              <template slot-scope="scope">
                <el-input
                  v-if="scope.row.edit"
                  v-model="scope.row.name"
                />
                <span v-else>{{ scope.row.name }}</span>
              </template>
            </el-table-column>
            <!-- 性别 -->
            <el-table-column
              align="center"
              prop="sex"
              :label="$t('dataConfMod.sex')"
              width="100"
              :show-overflow-tooltip="true"
            >
              <template slot-scope="scope">
                <DictSelect
                  v-model="scope.row.sex"
                  code="GENDER"
                  :disabled="!scope.row.edit"
                />
              </template>
            </el-table-column>
            <!-- 部门 -->
            <el-table-column
              align="center"
              prop="department"
              :label="$t('dataConfMod.department')"
              min-width="100"
              :show-overflow-tooltip="true"
            >
              <template slot-scope="scope">
                <el-input
                  v-if="scope.row.edit"
                  v-model="scope.row.department"
                />
                <span v-else>{{ scope.row.department }}</span>
              </template>
            </el-table-column>
            <!--职位  -->
            <el-table-column
              align="center"
              prop="position"
              :label="$t('dataConfMod.position')"
              min-width="100"
              :show-overflow-tooltip="true"
            >
              <template slot-scope="scope">
                <el-input
                  v-if="scope.row.edit"
                  v-model="scope.row.position"
                />
                <span v-else>{{ scope.row.position }}</span>
              </template>
            </el-table-column>
            <!-- 手机 -->
            <el-table-column
              align="center"
              prop="phone"
              :label="$t('dataConfMod.phone')"
              min-width="100"
              :show-overflow-tooltip="true"
            >
              <template slot="header">
                <span class="redText">*</span>{{ $t('dataConfMod.phone') }}
              </template>
              <template slot-scope="scope">
                <el-input
                  v-if="scope.row.edit"
                  v-model="scope.row.phone"
                />
                <span v-else>{{ scope.row.phone }}</span>
              </template>
            </el-table-column>
            <!-- 邮箱 -->
            <el-table-column
              align="center"
              prop="email"
              :label="$t('dataConfMod.email')"
              min-width="180"
              :show-overflow-tooltip="true"
            >
              <template slot-scope="scope">
                <el-input
                  v-if="scope.row.edit"
                  v-model="scope.row.email"
                />
                <span v-else>{{ scope.row.email }}</span>
              </template>
            </el-table-column>
            <!-- 默认联系人 -->
            <el-table-column
              align="center"
              prop="isDefault"
              :label="$t('dataConfMod.isDefault')"
              min-width="100"
              :show-overflow-tooltip="true"
            >
              <template slot-scope="scope">
                <el-checkbox
                  v-model="scope.row.isDefault"
                  true-label="Y"
                  false-label="N"
                  :disabled="!scope.row.edit"
                />
              </template>
            </el-table-column>
            <!-- 备注 -->
            <el-table-column
              align="center"
              prop="remark"
              :label="$t('dataConfMod.remark')"
              min-width="150"
              :show-overflow-tooltip="true"
            >
              <template slot-scope="scope">
                <el-input
                  v-if="scope.row.edit"
                  v-model="scope.row.remark"
                />
                <span v-else>{{ scope.row.remark }}</span>
              </template>
            </el-table-column>
            <!-- 启用 -->
            <el-table-column
              align="center"
              prop="isActive"
              :label="$t('dataConfMod.isActive')"
              min-width="100"
              :show-overflow-tooltip="true"
            >
              <template slot-scope="scope">
                <el-checkbox
                  v-model="scope.row.isActive"
                  true-label="Y"
                  false-label="N"
                  :disabled="!scope.row.edit"
                />
              </template>
            </el-table-column>
            <!-- 操作 -->
            <el-table-column
              fixed="right"
              :label="$t('common.operation')"
              width="110"
            >
              <template slot-scope="scope">
                <el-button
                  v-if="scope.row.companyPersonId && !scope.row.edit"
                  type="text"
                  @click="editClickContact(scope.$index, scope.row)"
                >
                  {{ $t('common.edit') }}
                </el-button>
                <el-button
                  v-if="scope.row.companyPersonId && scope.row.edit"
                  type="text"
                  @click="cancelClickContact(scope.$index, scope.row)"
                >
                  {{ $t('common.cancel') }}
                </el-button>
                <el-button
                  type="text"
                  @click="handleDelClickContact(scope.$index, scope.row)"
                >
                  {{
                    $t('common.delete')
                  }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-collapse-item>
        <!-- 地址信息 -->
        <el-collapse-item
          :title="$t('components.address.addressInfo')"
          name="2"
        >
          <div style="margin-bottom: 8px">
            <el-button
              type="primary"
              class="detail-pbtn"
              @click="addSiteInfo"
            >
              {{ $t('common.new') }}
            </el-button>
          </div>

          <el-table
            ref="bankTable"
            :data="companyData.companyAddresses"
            style="width: 100%"
            max-height="250px"
            border
          >
            <el-table-column
              align="center"
              type="index"
              width="50"
            />
            <!-- 国家/地区 -->
            <el-table-column
              align="center"
              prop="country"
              :label="$t('components.address.country')"
              min-width="100"
              :show-overflow-tooltip="true"
            >
              <template slot-scope="scope">
                <el-input
                  v-if="scope.row.edit"
                  v-model="scope.row.country"
                />
                <span v-else>{{ scope.row.country }}</span>
              </template>
            </el-table-column>
            <!-- 地区 -->
            <el-table-column
              align="center"
              prop="area"
              :label="$t('components.address.area')"
              min-width="150"
              :show-overflow-tooltip="true"
            >
              <template slot-scope="scope">
                <el-input
                  v-if="scope.row.edit"
                  v-model="scope.row.area"
                />
                <span v-else>{{ scope.row.area }}</span>
              </template>
            </el-table-column>
            <!-- 城市 -->
            <el-table-column
              align="center"
              prop="city"
              :label="$t('components.address.city')"
              min-width="150"
              :show-overflow-tooltip="true"
            >
              <template slot-scope="scope">
                <el-input
                  v-if="scope.row.edit"
                  v-model="scope.row.city"
                />
                <span v-else>{{ scope.row.city }}</span>
              </template>
            </el-table-column>
            <!-- 详细地址 -->
            <el-table-column
              align="center"
              prop="address"
              :label="$t('components.address.detailAddress')"
              min-width="150"
              :show-overflow-tooltip="true"
            >
              <template slot="header">
                <span class="redText">*</span>{{ $t('components.address.detailAddress') }}
              </template>
              <template slot-scope="scope">
                <el-input
                  v-if="scope.row.edit"
                  v-model="scope.row.address"
                />
                <span v-else>{{ scope.row.address }}</span>
              </template>
            </el-table-column>
            <!-- 邮政编码 -->
            <el-table-column
              align="center"
              prop="postalCode"
              :label="$t('components.address.postalCode')"
              width="120"
              :show-overflow-tooltip="true"
            >
              <template slot="header">
                <span class="redText">*</span>{{ $t('components.address.postalCode') }}
              </template>
              <template slot-scope="scope">
                <el-input
                  v-if="scope.row.edit"
                  v-model="scope.row.postalCode"
                />
                <span v-else>{{ scope.row.postalCode }}</span>
              </template>
            </el-table-column>
            <!-- 地址备注 -->
            <el-table-column
              align="center"
              prop="remark"
              :label="$t('components.address.remark')"
              min-width="150"
              :show-overflow-tooltip="true"
            >
              <template slot-scope="scope">
                <el-input
                  v-if="scope.row.edit"
                  v-model="scope.row.remark"
                />
                <span v-else>{{ scope.row.remark }}</span>
              </template>
            </el-table-column>
            <el-table-column
              align="center"
              prop="isActive"
              :label="$t('dataConfMod.isActive')"
              width="100"
              :show-overflow-tooltip="true"
            >
              <template slot-scope="scope">
                <el-checkbox
                  v-model="scope.row.isActive"
                  true-label="Y"
                  false-label="N"
                  :disabled="!scope.row.edit"
                />
              </template>
            </el-table-column>
            <!-- 操作 -->
            <el-table-column
              fixed="right"
              :label="$t('common.operation')"
              width="110"
            >
              <template slot-scope="scope">
                <el-button
                  v-if="scope.row.companyAddressId && !scope.row.edit"
                  type="text"
                  @click="editClickSite(scope.$index, scope.row)"
                >
                  {{ $t('common.edit') }}
                </el-button>
                <el-button
                  v-if="scope.row.companyAddressId && scope.row.edit"
                  type="text"
                  @click="cancelClickSite(scope.$index, scope.row)"
                >
                  {{ $t('common.cancel') }}
                </el-button>
                <el-button
                  type="text"
                  @click="handleDelClickSite(scope.$index, scope.row)"
                >
                  {{
                    $t('common.delete')
                  }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-collapse-item>
        <!-- 银行信息 -->
        <el-collapse-item
          :title="$t('components.bank.accountInfo')"
          name="3"
        >
          <div style="margin-bottom: 8px">
            <el-button
              type="primary"
              class="detail-pbtn"
              @click="addBankInfo"
            >
              <!-- 添加 -->
              {{ $t('common.new') }}
            </el-button>
          </div>

          <el-table
            ref="bankTable"
            :data="companyData.companyBanks"
            style="width: 100%"
            max-height="250px"
            border
          >
            <el-table-column
              align="center"
              type="index"
              width="50"
            />
            <!-- 银行编号 -->
            <el-table-column
              align="center"
              prop="bankNum"
              :label="$t('components.bank.bankNum')"
              width="150"
              :show-overflow-tooltip="true"
            >
              <template slot="header">
                <span class="redText">*</span>{{ $t('components.bank.bankNum') }}
              </template>
              <template slot-scope="scope">
                <quick-search
                  :show-input="scope.row.bankNum"
                  show-key="bankNum"
                  :scope-data="scope.row"
                  name="ceea_base_erp_branch_bank_info"
                  :disabled="!scope.row.edit"
                  @close-quicksearch="getBankObj"
                />
              </template>
            </el-table-column>
            <!-- 银行名称 -->
            <el-table-column
              align="center"
              prop="bankName"
              :label="$t('components.bank.bankName')"
              min-width="150"
              :show-overflow-tooltip="true"
            />
            <!-- 开户行名称 -->
            <el-table-column
              align="center"
              prop="branchBankName"
              :label="$t('components.bank.branchBankName')"
              min-width="150"
              :show-overflow-tooltip="true"
            />
            <!-- <el-table-column align="center" prop="unionCode" label="分行编码" min-width="150" :show-overflow-tooltip="true"/> -->
            <!-- 账户名称 -->
            <el-table-column
              align="center"
              prop="accountName"
              :label="$t('components.bank.accountName')"
              min-width="120"
              :show-overflow-tooltip="true"
            >
              <template slot="header">
                <span class="redText">*</span>{{ $t('components.bank.accountName') }}
              </template>
              <template slot-scope="scope">
                <el-input
                  v-if="scope.row.edit"
                  v-model="scope.row.accountName"
                />
                <span v-else>{{ scope.row.accountName }}</span>
              </template>
            </el-table-column>
            <!-- 银行账号 -->
            <el-table-column
              align="center"
              prop="bankAccount"
              :label="$t('components.bank.bankAccount')"
              width="150"
              :show-overflow-tooltip="true"
            >
              <template slot="header">
                <span class="redText">*</span>{{ $t('components.bank.bankAccount') }}
              </template>
              <template slot-scope="scope">
                <el-input
                  v-if="scope.row.edit"
                  v-model="scope.row.bankAccount"
                />
                <span v-else>{{ scope.row.bankAccount }}</span>
              </template>
            </el-table-column>
            <!-- 是否主账户 -->
            <el-table-column
              align="center"
              prop="isMain"
              :label="$t('components.bank.isMain')"
              width="100"
              :show-overflow-tooltip="true"
            >
              <template slot-scope="scope">
                <el-checkbox
                  v-model="scope.row.isMain"
                  true-label="Y"
                  false-label="N"
                  :disabled="!scope.row.edit"
                />
              </template>
            </el-table-column>
            <!--启用  -->
            <el-table-column
              align="center"
              prop="isActive"
              :label="$t('components.bank.isActive')"
              width="100"
              :show-overflow-tooltip="true"
            >
              <template slot-scope="scope">
                <el-checkbox
                  v-model="scope.row.isActive"
                  true-label="Y"
                  false-label="N"
                  :disabled="!scope.row.edit"
                />
              </template>
            </el-table-column>
            <!-- 操作 -->
            <el-table-column
              fixed="right"
              :label="$t('common.operation')"
              width="110"
            >
              <template slot-scope="scope">
                <el-button
                  v-if="scope.row.companyBankId && !scope.row.edit"
                  type="text"
                  @click="editClickBank(scope.$index, scope.row)"
                >
                  {{ $t('common.edit') }}
                </el-button>
                <el-button
                  v-if="scope.row.companyBankId && scope.row.edit"
                  type="text"
                  @click="cancelClickBank(scope.$index, scope.row)"
                >
                  {{ $t('common.cancel') }}
                </el-button>
                <el-button
                  type="text"
                  @click="handleDelClickBank(scope.$index, scope.row)"
                >
                  {{
                    $t('common.delete')
                  }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-collapse-item>
      </el-collapse>
      <c-toolbar v-if="curOpt !== 'view'">
        <template slot="right">
          <el-button
            type="primary"
            @click="saveCompanyInfo"
          >
            {{ $t('common.submit') }}
          </el-button>
        </template>
      </c-toolbar>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoMixin } from '@/utils/mixins'
import MainHeader from 'lib@/components/Table/MainHeader'
import QuickSearch from 'lib@/components/QuickSearch'
import CToolbar from 'lib@/components/c-toolbar'
import { adaptDictData } from '@/utils'
import { getDictItem, getRegion } from '@/api/common'
export default {
  name: 'CompanyMaintain',
  components: {
    MainHeader,
    QuickSearch,
    CToolbar
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      activeDims: ['1', '2', '3'],
      curOpt: 'edit',
      companyData: {
        companyId: '', // company
        companyName: '', // ceeaCompanyName
        taxNumber: '',
        companyPeoples: [], // 联系人
        companyBanks: [], // 银行信息
        companyAddresses: [] // 地址信息
      },
      comRules: {
        taxNumber: [{ required: true, message: this.$t('dataConfMod.msgTaxNumber') }] // 请输入公司税号信息
      }
    }
  },
  created () {
    this.companyData.companyId = this.$attrs.params.company
    this.companyData.companyName = this.$attrs.params.ceeaCompanyName

    this.fatchCompany(this.$attrs.params.company) // 查询公司数据
  },
  methods: {
    getCountry (row) {
      // 选择国外就清理省市区，并且禁用
      if (row.country !== 'CN') {
        row.province = null
        row.city = null
      }
    },
    // 银行信息
    getBankObj (val, scope) {
      scope.branchBankId = val ? val.branchBankId : ''
      scope.bankNum = val ? val.bankNum : '' // 银行编号
      scope.bankName = val ? val.bankName : '' // 银行名称
      scope.unionCode = val ? val.branchBankNum : '' // 分行编号
      scope.branchBankName = val ? val.branchBankName : '' // 分行名称[开户行名称]
    },
    // 删除银行信息
    handleDelClickBank (index, row) {
      this.companyData.companyBanks.splice(index, 1)
    },
    // 删除地址
    handleDelClickSite (index, row) {
      this.companyData.companyAddresses.splice(index, 1)
    },
    // 删除联系人
    handleDelClickContact (index, row) {
      this.companyData.companyPeoples.splice(index, 1)
    },
    editClickBank (index, row) {
      row.edit = true
    },
    editClickContact (index, row) {
      row.edit = true
    },
    editClickSite (index, row) {
      row.edit = true
    },
    cancelClickBank (index, row) {
      row.edit = false
    },
    cancelClickSite (index, row) {
      row.edit = false
    },
    cancelClickContact (index, row) {
      row.edit = false
    },
    // 银行
    addBankInfo () {
      this.companyData.companyBanks.push({
        bankNum: '',
        bankName: '',
        branchBankName: '',
        accountName: '',
        bankAccount: '',
        isMain: '',
        isActive: 'Y',
        edit: true
      })
    },
    // 联系人
    addContactInfo () {
      this.companyData.companyPeoples.push({
        name: '',
        sex: '',
        department: '',
        position: '',
        phone: '',
        email: '',
        remark: '',
        isDefault: '',
        isActive: 'Y',
        edit: true
      })
    },
    // 地址
    addSiteInfo () {
      this.companyData.companyAddresses.push({
        country: null,
        area: null,
        city: null,
        address: null,
        isActive: 'Y',
        postalCode: null,
        remark: null,
        edit: true
      })
    },
    // 保存公司信息
    saveCompanyInfo () {
      this.$refs.companyForm.validate((valid) => {
        if (!valid) {
          this.$message({
            message: this.$t('vendorMod.pleasefinishRequired'), // '请输入单据必填信息'
            type: 'error'
          })
          return false
        } else {
          // companyPeoples: [], // 联系人
          // companyBanks: [], // 银行信息
          // companyAddresses: [] // 地址信息
          for (let i of this.companyData.companyPeoples) {
            if (!i.name) {
              this.$message.warning(this.$t('dataConfMod.msgContactName')) // 请输入联系人姓名
              return
            }
            if (!i.phone) {
              this.$message.warning(this.$t('dataConfMod.msgContactPhone')) // 请维护联系人电话!
              return
            }
          }
          for (let i of this.companyData.companyAddresses) {
            if (!i.address) {
              this.$message.warning(this.$t('vendorMod.msgDetailAddr')) // 请输入详细地址
              return
            }
            if (!i.postalCode) {
              this.$message.warning(this.$t('components.address.msgInputPostal')) // 请输入邮政编码!
              return
            }
          }
          for (let i of this.companyData.companyBanks) {
            if (!i.bankNum) {
              this.$message.warning(this.$t('dataConfMod.msgBankNum')) // 请维护银行编号!
              return
            }
            if (!i.accountName) {
              this.$message.warning(this.$t('dataConfMod.msgAccountName')) // 请维护账号名称!
              return
            }
            if (!i.bankAccount) {
              this.$message.warning(this.$t('dataConfMod.msgBankAccount')) // 请维护银行账号!
              return
            }
          }
          // 保存
          if (!this.companyData.orgCompanyHeadId) {
            // 新增
            this.$api.base.companyAdd(this.companyData).then((res) => {
              if (res.data) {
                this.fatchCompany(res.data)
                this.$message.success(res.message)
              }
            })
          } else {
            // 编辑 更新
            this.$api.base.companyModify(this.companyData).then((res) => {
              if (res.data) {
                this.$message.success(res.message)
                this.$emit('tab-remove', this.$attrs.params.tabName)
                // this.__setTabTodo("biddingProjectList.getQuerydata");
              }
            })
          }
        }
      })
    },
    // 获取数据
    fatchCompany (companyId) {
      this.$api.base.companyGetDetail({ companyId }).then((res) => {
        if (res.data) {
          let resData = res.data
          this.companyData.orgCompanyHeadId = resData.orgCompanyHeadId // 头ID
          this.companyData.taxNumber = resData.taxNumber // 税号
          this.companyData.companyAddresses = resData.companyAddresses.map((i) => ({
            ...i,
            edit: false
          }))
          this.companyData.companyBanks = resData.companyBanks.map((i) => ({
            ...i,
            edit: false
          }))
          this.companyData.companyPeoples = resData.companyPeoples.map((i) => ({
            ...i,
            edit: false
          }))
        }
      })
    },
    handleEdit (index, row) {
      this.tableData[index].edit = true
      this.$nextTick(() => {
        this.$refs.orgTypeTable.doLayout()
      })
    },
    cancelEdit (row) {
      row.title = row.originalTitle
      row.edit = false
      this.$message({
        message: this.$t('common.cancelUpdate'), // '取消更新'
        type: 'warning'
      })
    },
    confirmEdit (row) {
      row.edit = false
      row.originalTitle = row.title
      this.$message({
        message: this.$t('common.successUpdate'), // '更新成功'
        type: 'success'
      })
    }
  }
}
</script>
<style scoped lang="scss">
.company-page {
  .redText {
    color: red;
  }
}
</style>
