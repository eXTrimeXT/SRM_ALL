<template>
  <el-container
    class="sitereviewplanconfirmEdit"
    direction="vertical"
  >
    <el-main>
      <div class="form-container">
        <el-form
          ref="form"
          :model="form"
          :rules="rules"
          :disabled="flag=='view'"
        >
          <srm-row :gutter="32">
            <srm-col :span="6">
              <el-form-item
                prop="planName"
                :label="$t('vendorMod.planName2')"
              >
                <QuickSearch
                  :show-input="form.planName"
                  show-key="planName"
                  :scope-data="form"
                  name="scc_sup_site_review_plan"
                  :disabled="readOnly"
                  @close-quicksearch="getCategoryObj2"
                />
              </el-form-item>
            </srm-col>
            <srm-col :span="6">
              <el-form-item
                prop="vendorName"
                :label="$t('orderMod.buyerOrderSynergy.vendorName')"
              >
                <el-input
                  v-model="form.vendorName"
                  disabled
                />
              </el-form-item>
            </srm-col>
            <srm-col :span="6">
              <el-form-item
                prop="orgName"
                :label="$t('purSettlementMod.fullPathId')"
              >
                <el-input
                  v-model="form.orgName"
                  disabled
                />
              </el-form-item>
            </srm-col>
            <srm-col :span="6">
              <el-form-item
                prop="planType"
                :label="$t('perfMod.planType')"
              >
                <el-input
                  v-model="form.planType"
                  disabled
                />
              </el-form-item>
            </srm-col>
            <srm-col :span="6">
              <el-form-item
                prop="planSetOutTime"
                :label="$t('vendorMod.planSetOutTime')"
              >
                <el-date-picker
                  v-model="form.planSetOutTime"
                  format="yyyy-MM-dd"
                  value-format="yyyy-MM-dd"
                  :placeholder="$t('vendorMod.datePicker')"
                  :disabled="readOnly"
                />
              </el-form-item>
            </srm-col>
            <srm-col :span="6">
              <el-form-item
                prop="planVisitTime"
                :label="$t('vendorMod.planVisitTime')"
              >
                <el-date-picker
                  v-model="form.planVisitTime"
                  format="yyyy-MM-dd"
                  value-format="yyyy-MM-dd"
                  :disabled="readOnly"
                  :placeholder="$t('vendorMod.datePicker')"
                />
              </el-form-item>
            </srm-col>
            <srm-col :span="6">
              <el-form-item
                prop="visitDays"
                :label="$t('vendorMod.visitDays')"
              >
                <el-input
                  v-model="form.visitDays"
                  :disabled="readOnly"
                />
              </el-form-item>
            </srm-col>
          </srm-row>
        </el-form>
        <el-collapse
          v-model="activeDims"
          class="tab-form-style"
        >
          <el-collapse-item
            :title="$t('vendorMod.workingGroupStaff')"
            name="1"
          >
            <el-button
              v-if="!readOnly"
              type="primary"
              class="detail-pbtn"
              style="margin:0 0 10px 0"
              @click="addDisplayItem"
            >
              {{ $t("common.new") }}
            </el-button>
            <el-table
              :data="displayItem"
              style="width: 100%"
              border
              height="250px"
              highlight-current-row
            >
              <el-table-column
                align="center"
                type="index"
                :label="$t('contractMod.tabindex')"
                width="60"
              />
              <el-table-column
                align="center"
                prop="userAccount"
                :label="$t('vendorMod.userAccount')"
                min-width="100"
                :show-overflow-tooltip="true"
              >
                <template slot-scope="scope">
                  <QuickSearch
                    :show-input="scope.row.userAccount"
                    show-key="username"
                    :scope-data="scope.row"
                    name="scc_rbac_user_display"
                    :disabled="readOnly"
                    @close-quicksearch="getCategoryObj"
                  />
                </template>
              </el-table-column>
              <el-table-column
                align="center"
                prop="userName"
                :label="$t('vendorMod.memberName')"
                min-width="100"
                :show-overflow-tooltip="true"
              />
              <!-- 手机号码 -->
              <el-table-column
                align="center"
                prop="userTel"
                :label="$t('vendorMod.mobilePhone')"
                min-width="100"
                :show-overflow-tooltip="true"
              />
              <!-- 电子邮箱 -->
              <el-table-column
                align="center"
                prop="userEmail"
                :label="$t('vendorMod.emailAddress')"
                min-width="100"
                :show-overflow-tooltip="true"
              />
              <!-- 岗位 -->
              <el-table-column
                align="center"
                prop="userPost"
                :label="$t('bidMod.position')"
                min-width="100"
                :show-overflow-tooltip="true"
              >
                <template slot-scope="scope">
                  <el-input
                    v-model="scope.row.userPost"
                    :disabled="readOnly"
                  />
                </template>
              </el-table-column>
              <el-table-column
                align="center"
                prop="onSiteFlag"
                :label="$t('vendorMod.onSiteFlag')"
                min-width="100"
                :show-overflow-tooltip="true"
              >
                <template slot-scope="scope">
                  <el-checkbox
                    v-model="scope.row.onSiteFlag"
                    true-label="true"
                    :disabled="readOnly"
                  />
                </template>
              </el-table-column>
              <!-- 操作 -->
              <el-table-column
                v-if="!readOnly"
                :label="$t('common.operation')"
                width="60"
              >
                <template slot-scope="scope">
                  <!-- 删除 -->
                  <el-button
                    type="text"
                    @click="deleteOneContent(scope.$index, scope.row)"
                  >
                    {{ $t("common.delete") }}
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-collapse-item>

          <el-collapse-item
            :title="$t('vendorMod.visitingAddress')"
            name="2"
          >
            <el-button
              v-if="!readOnly"
              type="primary"
              class="detail-pbtn"
              style="margin:0 0 10px 0"
              @click="addDisplayItem2"
            >
              {{ $t("common.new") }}
            </el-button>
            <el-table
              :data="displayItem2"
              style="width: 100%"
              border
              height="250px"
              highlight-current-row
              :disabled="readOnly"
            >
              <el-table-column
                align="center"
                type="index"
                :label="$t('contractMod.tabindex')"
                width="60"
              />
              <!-- 国家 -->
              <el-table-column
                align="center"
                prop="country"
                :label="$t('components.address.country')"
                min-width="100"
                :show-overflow-tooltip="true"
              >
                <template slot-scope="scope">
                  <el-input
                    v-model="scope.row.country"
                    :placeholder="$t('common.pleaseTypeContents')"
                    :disabled="readOnly"
                  />
                </template>
              </el-table-column>
              <!-- 地区 -->
              <el-table-column
                align="center"
                prop="province"
                :label="$t('components.address.area')"
                min-width="100"
                :show-overflow-tooltip="true"
              >
                <template slot-scope="scope">
                  <el-input
                    v-model="scope.row.province"
                    :placeholder="$t('common.pleaseTypeContents')"
                    :disabled="readOnly"
                  />
                </template>
              </el-table-column>
              <!-- 城市 -->
              <el-table-column
                align="center"
                prop="city"
                :label="$t('components.address.city')"
                min-width="100"
                :show-overflow-tooltip="true"
              >
                <template slot-scope="scope">
                  <el-input
                    v-model="scope.row.city"
                    :placeholder="$t('common.pleaseTypeContents')"
                    :disabled="readOnly"
                  />
                </template>
              </el-table-column>
              <!-- 详细地址 -->
              <el-table-column
                align="center"
                prop="addressDetail"
                :label="$t('components.address.detailAddress')"
                min-width="100"
                :show-overflow-tooltip="true"
              >
                <template slot-scope="scope">
                  <el-input
                    v-model="scope.row.addressDetail"
                    :placeholder="$t('common.pleaseTypeContents')"
                    :disabled="readOnly"
                  />
                </template>
              </el-table-column>
              <!-- 邮政编码 -->
              <el-table-column
                align="center"
                prop="postCode"
                :label="$t('components.address.postalCode')"
                min-width="100"
                :show-overflow-tooltip="true"
              >
                <template slot-scope="scope">
                  <el-input
                    v-model="scope.row.postCode"
                    :placeholder="$t('common.pleaseTypeContents')"
                    :disabled="readOnly"
                  />
                </template>
              </el-table-column>
              <!-- 地址备注 -->
              <el-table-column
                align="center"
                prop="siteComment"
                :label="$t('components.address.remark')"
                min-width="100"
                :show-overflow-tooltip="true"
              >
                <template slot-scope="scope">
                  <el-input
                    v-model="scope.row.siteComment"
                    :placeholder="$t('common.pleaseTypeContents')"
                    :disabled="readOnly"
                  />
                </template>
              </el-table-column>
              <!-- 操作 -->
              <el-table-column
                v-if="!readOnly"
                :label="$t('common.operation')"
                width="60"
              >
                <template slot-scope="scope">
                  <!-- 删除 -->
                  <el-button
                    type="text"
                    @click="deleteOneContent(scope.$index, scope.row)"
                  >
                    {{ $t("common.delete") }}
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-collapse-item>
        </el-collapse>
      </div>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoMixin } from '@/utils/mixins'
import MainHeader from 'lib@/components/Table/MainHeader'
import CToolbar from 'lib@/components/c-toolbar'
import QuickSearch from 'lib@/components/QuickSearch'
import { siteReviewPlanConfirm } from 'mods@/vendorManagementSupplier/api/index'

export default {
  name: 'SitereviewplanconfirmEdit',
  components: {
    MainHeader,
    CToolbar,
    QuickSearch
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      flag: '',
      planTypeAll: [
        {
          value: 'YEAR',
          label: '年度'
        },
        {
          value: 'HALF_YEAR',
          label: '半年度'
        },
        {
          value: 'QUARTER',
          label: '季度'
        },
        {
          value: 'ALLOW',
          label: '准入'
        },
        {
          value: 'MONTH',
          label: '月度'
        }
      ],
      displayItem: [],
      displayItem2: [],
      userType: '',
      activeDims: ['1', '2', '3', '4', '5', '6', '7'],
      form: {},
      rules: {},
      readOnly: false
    }
  },
  watch: {
    viewUpdateButton () {
      this.buttonConfigInfo.save.view = this.viewUpdateButton
      this.buttonConfigInfo.submit.view = this.viewUpdateButton
    }
  },
  created () {
    this.userType = this.$store.getters.userInfo.userType
  },
  mounted () {
    const { flag, row, readOnly } = this.$attrs.params
    this.flag = flag
    this.readOnly = readOnly
    if (flag === 'edit' || flag === 'approve' || flag === 'view') {
      this.form = row
      this.initialization(row)
      this.planTypeAll.forEach(datas => {
        if (datas.value == this.form.planType) {
          this.$set(this.form, 'planType', datas.label)
        }
      })
    }
  },
  methods: {
    back () {
      if (this.$attrs.params.flag === 'add') {
        this.$emit('tab-remove', 'sitereviewplanconfirmEdit')
      } else {
        this.$emit('tab-remove', this.$attrs.params.tabName)
      }
      this.__setTabTodo('list.getQuerydata')
    },
    initialization (row) {
      const planConfirmId = row.planConfirmId
      this.form.planConfirmId = planConfirmId
      let _this = this
      siteReviewPlanConfirm.planGet(planConfirmId).then(res => {
        console.log(res)
        _this.displayItem = res.data.personList
        _this.displayItem2 = res.data.addressList
        _this.form.planName = res.data.planName
      })
    },
    addDisplayItem2 () {
      this.displayItem2.push({})
    },
    addDisplayItem () {
      this.displayItem.push({})
    },
    getCategoryObj (val, scope) {
      scope.userAccount = val ? val.username : ''
      scope.userName = val ? val.nickname : ''
      scope.userId = val ? val.userId : ''
      scope.userTel = val ? val.phone : ''
      scope.userEmail = val ? val.email : ''
      this.displayItem.push({})
      this.displayItem.pop()
    },
    getCategoryObj2 (val, scope) {
      if (this.readOnly) {
        this.$message.error('只读状态不能编辑')
        return false
      }
      this.$set(this.form, 'siteReviewPlanId', val.siteReviewPlanId)
      this.$set(this.form, 'vendorName', val.vendorName)
      this.$set(this.form, 'orgName', val.orgName)
      this.$set(this.form, 'categoryName', val.categoryName)
      this.planTypeAll.forEach(datas => {
        if (datas.value == val.planType) {
          this.$set(this.form, 'planType', datas.label)
        }
      })
      console.log(this.form)
    },
    save (bol) {
      this.$refs.form.validate(result => {
        if (result) {
          const { flag } = this.$attrs.params
          // 新增时不用提交主键值
          let datas = this.form
          datas.personList = this.displayItem
          datas.addressList = this.displayItem2
          if (bol == 'save') {
            datas.submitFlag = 'SAVE'
          } else {
            datas.submitFlag = 'SUBMIT'
          }
          if (flag === 'add') {
            siteReviewPlanConfirm
              .planAdd(datas)
              .then(async res => {
                this.$message({
                  type: 'success',
                  message: res.message
                })
                this.cancelBill()
                await this.getFormDetail(res.data)
                await this.handlerAfter(bol)
              })
          } else if (flag === 'edit') {
            siteReviewPlanConfirm
              .planModify(datas)
              .then(async res => {
                this.$message({
                  type: 'success',
                  message: res.message
                })
                this.cancelBill()
                await this.getFormDetail(res.data)
                await this.handlerAfter(bol)
              })
          }
        } else {
          this.__focus_error__()
        }
      })
    },
    cancelBill () {
      const { flag, row } = this.$attrs.params
      if (flag === 'add') {
        this.$emit('tab-remove', 'sitereviewplanconfirmEdit')
      } else {
        this.$emit(
          'tab-remove',
          'sitereviewplanconfirmEdit' + row.planConfirmId
        )
      }
      this.__setTabTodo('sitereviewplanconfirmList.getQuerydata')
    },
    // 上传附件成功
    handleUploadSuccess (file, row, key) {
      const { id, name } = file
      row[key] = id.toString()
    },
    // 删除文件
    handleAttachmentRemove (row, key) {
      row[key] = ''
    }
  }
}
</script>
<style scoped lang="scss">
.sitereviewplanconfirmEdit {
  height: 100%;
  .sub_header {
    padding: 4px 11px;
    background: #eee;
  }
  .el-table .el-date-editor {
    width: 135px;
  }
  .base-form {
    padding: 15px 30px 0;
  }
  .toRequired {
    color: #ff4949;
    padding-right: 2px;
  }
  .edit_cond {
    color: #23adf4;
    cursor: pointer;
  }
}
</style>
