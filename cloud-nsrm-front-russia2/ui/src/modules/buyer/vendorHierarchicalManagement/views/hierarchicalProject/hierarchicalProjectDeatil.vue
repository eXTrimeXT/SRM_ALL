<template>
  <el-container
    class="flex-container the-hierarchicalProjectDeatil-detail"
    direction="vertical"
  >
    <el-main>
      <el-collapse
        v-model="activeDims"
        class="tab-form-style"
      >
        <el-form
          ref="ruleForm"
          :model="hierarchicalForm"
          :rules="rules"
          class="form-fill-style"
          :disabled="curOpt === 'view'"
        >
          <el-collapse-item
            :title="$t('vendorMod.itemInformation')"
            name="1"
          >
            <el-row :gutter="32">
              <el-col :span="6">
                <el-form-item
                  prop="orderNo"
                  :label="$t('vendorMod.hierarchicalCode')"
                >
                  <el-input
                    v-model="hierarchicalForm.orderNo"
                    disabled
                  />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item
                  prop="projectName"
                  :label="$t('vendorMod.classifyProjectName')"
                >
                  <el-input v-model="hierarchicalForm.projectName" />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item
                  prop="createdUserName"
                  :label="$t('vendorMod.creator')"
                >
                  <el-input
                    v-model="hierarchicalForm.createdUserName"
                    disabled
                  />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item
                  prop="creationDate"
                  :label="$t('vendorMod.relegation.creationTime')"
                >
                  <el-date-picker
                    v-model="hierarchicalForm.creationDate"
                    disabled
                    type="date"
                    :placeholder="$t('vendorMod.datePicker')"
                    :format="$formatDatePicker"
                    value-format="yyyy-MM-dd"
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="32">
              <el-col :span="6">
                <el-form-item
                  prop="status"
                  :label="$t('vendorMod.projectStatus')"
                >
                  <DictSelect
                    v-model="hierarchicalForm.status"
                    disabled
                    code="VENDOR_LEVEL_STATUS"
                    clearable
                  />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item
                  prop="orgId"
                  :label="$t('vendorMod.ceeaOrgName')"
                >
                  <OrganizationSelector
                    ref="organizationSelector"
                    v-model="hierarchicalForm.orgId"
                    :disabled="curOpt === 'view'"
                    :parent-id="-1"
                    node-type="OU"
                    :limit="false"
                    @select="selectHandler"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item
                  prop="reviewYear"
                  :label="$t('vendorMod.ratingYear')"
                >
                  <el-date-picker
                    v-model="hierarchicalForm.reviewYear"
                    type="year"
                    :placeholder="$t('vendorMod.datePicker')"
                    value-format="yyyy"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item
                  prop="categoryFlag"
                  :label="$t('vendorMod.allCategories')"
                >
                  <el-select
                    v-model="hierarchicalForm.categoryFlag"
                    :placeholder="$t('common.pleaseSelect')"
                    @change="categoryFlagChange"
                  >
                    <el-option
                      v-for="item in categoryFlagOptions"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
          </el-collapse-item>
          <el-collapse-item
            :title="$t('vendorMod.ratingSubsidiary')"
            name="2"
          >
            <div
              v-if="!readOnly"
              class="commonPad"
            >
              <QuickSearch
                name="scc_base_purchase_category_url"
                :btnTitle="$t('common.new')"
                showButton
                multiSelect
                @close-quicksearch="catSelectHandel"
              />
              <el-button
                type="primary"
                class="detail-pbtn"
                style="margin-left: 10px;"
                :disabled="accordDisabled"
                @click="addAccord"
              >
                {{ $t('vendorMod.categoryRules') }}
              </el-button>
              <div class="btnNick">
                <QuickSearch
                  :show-button="false"
                  show-key="nickname"
                  :btn-title="$t('vendorMod.addRaters')"
                  name="scc_rbac_user_display"
                  @close-quicksearch="getUserdemandObjBtn"
                />
              </div>
            </div>
            <el-table
              ref="bankTable"
              :data="
                hierarchicalForm.levelManList.slice((currentPage - 1) * pageSize_approvalBiddingItemLis, currentPage * pageSize_approvalBiddingItemLis)
              "
              row-key="getIndex"
              style="width: 100%"
              max-height="300px"
              border
              @selection-change="handleSelectionChange"
            >
              <el-table-column
                type="selection"
                :reserve-selection="true"
                align="center"
                width="55"
              />
              <el-table-column
                align="center"
                type="index"
                width="50"
              />
              <el-table-column
                align="center"
                prop="categoryCode"
                width="150"
                :label="$t('common.categoryCode')"
                :show-overflow-tooltip="true"
              />
              <el-table-column
                align="center"
                prop="categoryName"
                :label="$t('vendorMod.relegation.categoryName')"
                :show-overflow-tooltip="true"
              >
                <!-- <template slot-scope="scope">
                  <c-category-select
                    v-model="scope.row.categoryFullName"
                    :scope="scope.row"
                    show-key="categoryName"
                    :placeholder="$t('vendorMod.msgCategoryNormalizer')"
                    @select="comfirmSelect"
                  />
                </template> -->
              </el-table-column>
              <el-table-column
                align="center"
                prop="levelUserName"
                :label="$t('vendorMod.raterAccount')"
                :show-overflow-tooltip="true"
              >
                <template slot-scope="scope">
                  <QuickSearch
                    :show-input="scope.row.levelUserName"
                    :disabled="curOpt === 'view'"
                    show-key="nickname"
                    :scope-data="scope.row"
                    name="scc_rbac_user_display"
                    @close-quicksearch="getUserdemandObj"
                  />
                </template>
              </el-table-column>
              <el-table-column
                align="center"
                prop="levelNickName"
                :label="$t('vendorMod.ratingAgency')"
                :show-overflow-tooltip="true"
              />
              <el-table-column
                align="center"
                prop="levelUserEmail"
                :label="$t('vendorMod.emailAddress')"
                :show-overflow-tooltip="true"
              />
              <el-table-column
                :label="$t('common.operation')"
                width="60"
              >
                <template slot-scope="scope">
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
            <div style="width: 100%; margin-bottom: 0px;margin-top:16px;">
              <el-pagination
                align="center"
                :current-page="currentPage"
                :page-sizes="[10, 20, 60, 100]"
                :page-size="pageSize_approvalBiddingItemLis"
                layout="total, sizes, prev, pager, next, jumper"
                :total="hierarchicalForm.levelManList.length"
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
              />
            </div>
          </el-collapse-item>
        </el-form>
      </el-collapse>

      <CToolbar>
        <template slot="right">
          <el-button
            @click="cancelBill"
          >
            {{ $t('common.backTo') }}
          </el-button>
          <el-button
            :disabled="readOnly"
            @click="saveDataHandle('DRAFT')"
          >
            {{
              $t('common.staging')
            }}
          </el-button>
          <el-button
            type="primary"
            :disabled="readOnly"
            @click="saveDataHandle('PUBLISH')"
          >
            {{ $t('common.submit') }}
          </el-button>
        </template>
      </CToolbar>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoMixin, tabTodoWatch } from '@/utils/mixins'
import CToolbar from 'lib@/components/c-toolbar'
import QuickSearch from 'lib@/components/QuickSearch'
import { isMobile, isEmail } from 'lib@/utils/validate'
import CCategorySelect from 'lib@/components/c-category-select'
import OrganizationSelector from 'lib@/components/organization-selector'
import isEqual from 'lodash/isEqual'
import { hierarchicalProject } from 'modb@/vendorHierarchicalManagement/api'
export default {
  name: 'HierarchicalProjectDeatil',
  components: {
    QuickSearch,
    CToolbar,
    CCategorySelect,
    OrganizationSelector
  },
  mixins: [tabTodoMixin, tabTodoWatch],
  data () {
    return {
      activeDims: ['1', '2', '3'],
      categoryFlagOptions: [
        { value: 'Y', label: this.$t('common.yes') },
        { value: 'N', label: this.$t('common.no') }
      ],
      hierarchicalForm: {
        orderNo: null,
        projectName: null,
        createdUserName: null,
        creationDate: null,
        status: null,
        orgId: null,
        reviewYear: null,
        categoryFlag: null,
        levelManList: []
      },
      selectedData: [],
      allParams: {
        siteJournals: []
      },
      categoryName: '',
      currentPage: 1,
      pageSize_approvalBiddingItemLis: 5,
      rules: {
        projectName: [{ required: true, message: this.$t('vendorMod.required') }],
        orgId: [{ required: true, message: this.$t('vendorMod.required') }],
        reviewYear: [{ required: true, message: this.$t('vendorMod.required') }],
        categoryFlag: [{ required: true, message: this.$t('vendorMod.required') }]
      },
      readOnly: false,
      curOpt: 'view'
    }
  },
  computed: {
    accordDisabled () {
      return !this.selectedData && this.selectedData.length > 0 && this.hierarchicalForm.orgId
    }
  },
  // watch:{
  //   //监听数组是否有重复的数据
  //   "hierarchicalForm.levelManList":{
  //     deep: true,
  //     handler(new_data,old_data){
  //       let new_dataRest = new_data.map(({getIndex,...rest})=>({...rest}))
  //       let sameObject = this.findSameObject(new_dataRest)
  //       console.log("new_dataRest",new_dataRest)

  //       console.log("sameObject",sameObject)

  //     }
  //   }
  // },
  created () {
    this.$nextTick(() => {
      this.$refs.bankTable.doLayout() // 解决表格错位的问题
    })
  },
  mounted () {
    const { flag, row } = this.$attrs.params
    this.vendorLevelId = this.$attrs.params.row.levelHeadId || ''
    this.readOnly = flag === 'view'
    this.curOpt = flag
    if (flag === 'edit' || flag === 'view') {
      this.getDetail()
    } else if (flag === 'add') {
      this.hierarchicalForm.creationDate = new Date()
      this.hierarchicalForm.createdUserName = this.$store.getters.userInfo.username
      this.hierarchicalForm.status = 'DRAFT'
    }
  },
  methods: {
    findSameObject (obj) {
      let uniques = []
      let stringify = {}
      for (let i = 0; i < obj.length; i++) {
        let keys = Object.keys(obj[i])
        keys.sort(function (a, b) {
          return Number(a) - Number(b)
        })
        let str = ''
        for (let j = 0; j < keys.length; j++) {
          str += JSON.stringify(keys[j])
          str += JSON.stringify(obj[i][keys[j]])
        }
        if (!stringify.hasOwnProperty(str)) {
          stringify[str] = true
        } else {
          uniques.push(obj[i])
        }
      }
      return uniques
    },
    getDetail () {
      hierarchicalProject.get(this.vendorLevelId).then((res) => {
        if (res) {
          this.hierarchicalForm = res.data || {}
          this.siteJournalsndex()
        }
      })
    },
    // 保存数据操作
    async saveDataHandle (type) {
      let inviteFormData = this.hierarchicalForm
      this.$refs.ruleForm.validate((valid) => {
        if (valid) {
          let me = this
          let url = ''
          // 暂存
          if (type === 'DRAFT') {
            url = '/api-pef/perf/vendorlevelhead/addOrUpdateVendorLevel'
          } else {
            // 发布
            url = '/api-pef/perf/vendorlevelhead/submitted'
          }

          hierarchicalProject.save(url, inviteFormData).then(async (res) => {
              this.vendorLevelId = res.data || ''
              if (me.curOpt === 'edit' || me.curOpt === 'add') {
                if (type === 'DRAFT') {
                  this.$message({
                    type: 'success',
                    message: this.$t('vendorMod.temporarySuccess')
                  })

                  this.getDetail()
                } else if (type === 'PUBLISH') {
                  this.$message({
                    type: 'success',
                    message: this.$t('common.successSubmit')
                  }) // 提交成功
                  this.getDetail()

                  this.cancelBill()
                }
              }
            })
            .catch((err) => {
              console.log(err)
            })
        } else {
          return false
        }
      })
    },
    // 这里校验邮件手机，统一信用代码信息
    async validateForm () {
      return true
    },
    categoryFlagChange (val) {
      if (val === 'Y' && this.hierarchicalForm.orgId) {
        let levelManList = this.hierarchicalForm.levelManList
        let catIdList = levelManList.map(i => (i.categoryId))

        hierarchicalProject.getAllOrgCategoryByOrgId(this.hierarchicalForm.orgId).then((res) => {
          let resData = res.data || []
          resData.forEach((item) => {
            item.levelNickName = item.levelNickName ? item.levelNickName : ''
            item.levelUserEmail = item.levelUserEmail ? item.levelUserEmail : ''
            if (item.categoryId && !catIdList.includes(item.categoryId)) {
              this.hierarchicalForm.levelManList.push(item)
            }
          })
          this.siteJournalsndex()
          this.handleSizeChange(10)
        })
      } else if (val === 'Y' && !this.hierarchicalForm.orgId) {
        this.$message({
          type: 'warning',
          message: this.$t('orderMod.selBusinessEntityFirst')
        })
        this.hierarchicalForm.categoryFlag = ''
      }
    },
    getUserdemandObj (val, scope) {
      scope.levelUserId = val ? val.userId : ''
      scope.levelUserName = val ? val.username : ''
      scope.levelNickName = val ? val.nickname : ''
      scope.levelUserEmail = val ? val.email : ''
      scope.levelUserPhone = val ? val.phone : ''
    },
    getUserdemandObjBtn (val) {
      if (this.selectedData && this.selectedData.length > 0) {
        // 点击评级人时 过滤已选择相同的品类和评级人提示报错
        let selectedNext = false
        let selectedlevelManListArray = this.hierarchicalForm.levelManList.map(
          ({ categoryCode, levelUserId }) => ({
            categoryCode: `${categoryCode}`,
            levelUserId: `${levelUserId}`
          })
        )
        let selectedDataArray = this.selectedData.map(({ categoryCode, levelUserId }) => ({
          categoryCode: `${categoryCode}`,
          levelUserId: `${val.userId}`
        }))

        selectedlevelManListArray.map((i) => {
          selectedDataArray.map((item) => {
            if (isEqual(item, i)) {
              selectedNext = true
            }
          })
        })
        let categoryCode = this.selectedData.map((i) => i.categoryCode)
        categoryCode.map((itm, index) => {
          if (categoryCode.indexOf(itm) != index) {
            selectedNext = true
          }
        })
        if (selectedNext) {
          this.$message({
            type: 'warning',
            message: this.$t('orderMod.theSelectedCategoryAndRaterAlreadyExist')
          })
          return false
        }

        let getIndexList = this.selectedData.map((i) => i.getIndex)
        this.hierarchicalForm.levelManList.forEach((item, index) => {
          if ([...getIndexList].includes(index)) {
            item.levelUserId = val ? val.userId : ''
            item.levelUserName = val ? val.username : ''
            item.levelNickName = val ? val.nickname : ''
            item.levelUserEmail = val ? val.email : ''
            item.levelUserPhone = val ? val.phone : ''
          }
        })
      } else {
        this.$message({
          type: 'warning',
          message: this.$t('vendorMod.checkInformation')
        })
      }
    },
    cancelBill () {
      const { flag, row } = this.$attrs.params
      if (flag === 'add') {
        this.$emit('tab-remove', 'hierarchicalProjectDeatil')
      } else {
        this.$emit('tab-remove', 'hierarchicalProjectDeatil' + row.orderNo)
      }
      this.__setTabTodo('hierarchicalProjectList.getQuerydata')
    },
    selectHandler (node, value, scope) {
      this.hierarchicalForm.orgId = node.organizationId
      this.hierarchicalForm.orgCode = node.organizationCode
      this.hierarchicalForm.orgName = node.organizationName
    },
    // 确认选择品类
    comfirmSelect (node, scope) {
      scope.categoryId = node ? node.categoryId : null
      scope.categoryName = node ? node.categoryName : ''
      scope.categoryFullName = node ? node.categoryFullName : ''
      scope.categoryCode = node ? node.categoryCode : ''
    },
    handleSizeChange (val) {
      this.currentPage = 1
      this.pageSize_approvalBiddingItemLis = val
    },
    handleCurrentChange (val) {
      this.currentPage = val
    },
    handleDelClickSite (index, row) {
      this.hierarchicalForm.levelManList.splice(index, 1)
      // 当删除整一页的内容时触发
      if (this.hierarchicalForm.levelManList.length % this.pageSize_approvalBiddingItemLis === 0) {
        // 判断是最后一页就往前走一页
        if (
          this.currentPage * this.pageSize_approvalBiddingItemLis ==
          this.hierarchicalForm.levelManList.length + this.pageSize_approvalBiddingItemLis
        ) {
          this.currentPage = this.currentPage - 1
          this.handleCurrentChange(this.currentPage)
        }
      }
      this.siteJournalsndex()
    },
    siteJournalsndex () {
      this.hierarchicalForm.levelManList.forEach((item, index) => {
        item['getIndex'] = index
      })
    },
    // 品类选择
    catSelectHandel (data = []) {
      if (data.length > 0) {
        let levelManList = this.hierarchicalForm.levelManList
        let catIdList = levelManList.map(i => (i.categoryId))
        data.forEach(item => {
          if (item.categoryId && !catIdList.includes(item.categoryId)) {
            levelManList.unshift({
              categoryId: item.categoryId,
              categoryCode: item.categoryCode,
              categoryName: item.categoryName,
              categoryFullName: item.categoryFullName,
              levelUserName: null,
              levelNickName: null,
              levelUserId: null,
              levelUserEmail: null,
              levelUserPhone: null
            })
          }
        })
        this.hierarchicalForm.levelManList = levelManList

        this.siteJournalsndex()

        if (this.hierarchicalForm.levelManList.length === 1) {
          this.handleCurrentChange(1)
        } else {
          this.handleSizeChange(10)
        }
      }
    },
    addAccord () {
      if (this.selectedData && this.selectedData.length > 0 && this.hierarchicalForm.orgId) {
        let categoryNames = this.selectedData.map((item) => item.categoryName)
        let getIndexList = this.selectedData.map((item) => item.getIndex)
        let formData = {
          orgId: String(this.hierarchicalForm.orgId),
          categoryNames
        }
        hierarchicalProject.getUserByCategoryDto(formData).then((res) => {
          if (res.data && res.data.length > 0) {
            res.data.map((item) => {
              this.hierarchicalForm.levelManList.forEach((v, index) => {
                if (v.categoryName === item.categoryName && getIndexList.includes(index)) {
                  v.levelNickName = item.levelNickName
                  v.levelUserEmail = item.levelUserEmail
                  v.levelUserId = item.levelUserId
                  v.levelUserName = item.levelUserName
                  v.levelUserPhone = item.levelUserPhone
                }
              })
            })
            this.siteJournalsndex()
          } else {
            this.$message({
              type: 'warning',
              message: this.$t('hierarchical.nodata')  // '没有查到相关数据！'
            })
          }
        })
      } else {
        this.$message({
          type: 'warning',
          message: this.$t('vendorMod.selectRowEntity')
        })
      }
    },
    handleSelectionChange (val) {
      this.selectedData = val
    }
  }
}
</script>

<style scoped lang="scss">
:deep(.el-table) {
  th.gutter, colgroup.gutter {
    width: 0px !important;//此处的宽度值，对应你自定义滚动条的宽度即可
  }
}
:deep(.el-table__body){
  width: 100% !important;
}
// 关键css代码
:deep(.el-table__header colgroup col[name="gutter"]) {
  display: table-cell !important;
}
.inviteVendorEdit {
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
.btnNick {
  width: 94px;
  margin-left: 10px;
  // margin: -37px 0px 10px 203px;
}
.commonPad {
  display: flex;
  margin-bottom: 5px;
}
</style>
