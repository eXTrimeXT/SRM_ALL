<template>
  <el-container
    class="flex-container reviewformstandard_list_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="filterConfig"
        @getFormData="getQuerydata"
      />
      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <AuthorityButton
            type="primary"
            @click="addHandle"
          >
            {{
              $t('common.add')
            }}
          </AuthorityButton>
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-header="tableHeader"
        :check-change="handleCurrentChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        :source="reviewFormStandard.list"
      />
      <srm-dialog
        :title="$t('dataConfMod.accessFlowConfCate')"
        :visible.sync="dialogFormVisible2"
        :close-on-click-modal="false"
        size="middle"
      >
        <div class="mb10">
          <el-button
            type="primary"
            class="detail-pbtn"
            @click="addOneItem"
          >
            {{
              $t('common.add')
            }}
          </el-button>
        </div>
        <el-table
          :data="displayItem"
          style="width: 100%"
          border
          height="250px"
          highlight-current-row
          @selection-change="handleItemSelection"
        >
          <el-table-column
            align="center"
            type="index"
            :label="$t('contractMod.tabindex')"
            width="60"
          />
          <!-- 品类编码 -->
          <el-table-column
            align="center"
            prop="categoryCode"
            :label="$t('common.categoryCode')"
            min-width="150"
            :show-overflow-tooltip="true"
          >
            <template slot-scope="scope">
              <QuickSearch
                :show-input="scope.row.categoryCode"
                show-key="categoryCode"
                :scope-data="scope.row"
                name="scc_base_purchase_category2"
                @close-quicksearch="getCategoryObj"
              />
            </template>
          </el-table-column>
          <!-- 品类名称 -->
          <el-table-column
            align="center"
            prop="categoryName"
            :label="$t('common.categoryName')"
            min-width="150"
            :show-overflow-tooltip="true"
          />
          <!-- 操作 -->
          <el-table-column
            :label="$t('common.operation')"
            width="60"
          >
            <template slot-scope="scope">
              <!-- 删除 -->
              <el-button
                type="text"
                @click="deleteOneContent(scope.$index, scope.row)"
              >
                {{
                  $t('common.delete')
                }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <template #footer>
          <el-button @click="dialogFormVisible2 = false">
            <!-- 取 消 -->
            {{ $t('common.cancel') }}
          </el-button>
          <el-button
            type="primary"
            @click="addCategorys"
          >
            {{ $t('common.confirm') }}
          </el-button>
        </template>
      </srm-dialog>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import QuickSearch from 'lib@/components/QuickSearch'
import reviewformstandardEdit from './edit.vue'
import { reviewFormStandard } from 'modb@/vendorManagementBuyer/api/vendorManagement'

export default {
  name: 'ReviewformstandardList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    QuickSearch
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      reviewFormStandard: reviewFormStandard,
      globalEntryConfigId: null,
      displayItem: [],
      dialogFormVisible2: false,
      name: 'reviewformstandardList',
      tableName: 'reviewformstandardTable',
      pageSize: 15,
      gridId: 'list',
      currentRows: [],
      tableHeader: [],

      filterConfig: [
        { prop: 'standardName', label: this.$t('dataConfMod.qualificationStandardName') },
        {
          prop: 'queryCreationDate',
          width: 180,
          label: this.$t('common.creationTime'),
          type: 'daterange'
        }
      ],
      queryParam: {}
    }
  },
  created () {
    let _this = this
    this.tableHeader = [
      {
        prop: 'standardCode',
        label: _this.$t('dataConfMod.standardCode')
        // width: 120
      },
      {
        prop: 'standardName',
        label: _this.$t('dataConfMod.standardName'),
        showType: 'button',
        btnStyle: 'text',
        callback: row => this.editHandle(row, 0)
        // width: 120
      },
      {
        prop: 'ceeaCategoryName',
        label: () => _this.$t('common.category'), // 品类
        minWidth: 150,
        showType: 'button',
        btnStyle: 'text',
        callback: row => this.readOne(row),
        formattor (val) {
          return _this.$t('dataConfMod.maintainCategory') // 维护品类
        },
        code: 'sup:accessFlowSetting:ceeaCategoryName'
      },
      {
        prop: 'creationDate',
        label: () => _this.$t('dataConfMod.creationDate')
        // width: 120
      },
      {
        prop: 'createdBy',
        label: () => _this.$t('dataConfMod.createdBy')
        // width: 120
      },
      {
        prop: 'standardStatus',
        label: () => _this.$t('dataConfMod.triggerState'),
        formattor: val => {
          switch (val) {
            case 'DRAFT':
              val = _this.$t('perfMod.DRAFT')
              break
            case 'ENABLE':
              val = _this.$t('dataConfMod.valid')
              break
            case 'UNABLE':
              val = _this.$t('common.inactive')
              break
            default:
              val = ''
              break
          }
          return val
        },
        width: 120
      },
      {
        prop: 'operation',
        label: () => _this.$t('common.operation'),
        showType: 'buttons',
        btnStyle: 'text',
        // fixed: "right",
        width: 130,
        buttons: [
          {
            callback: row => _this.editHandle(row, 1),
            // code: "pr:requirementApply:edit",
            show: row => row.standardStatus === 'DRAFT',
            formattor: () => {
              return _this.$t('common.edit')
            }
          },
          {
            callback: row => this.submitHandle(row, 'ENABLE'),
            // code: "pr:requirementApply:edit",
            show: row => row.standardStatus === 'DRAFT',
            formattor: () => {
              return _this.$t('common.submit')
            }
          },
          {
            callback: row => this.deleteHandle(row),
            // code: "pr:requirementApply:edit",
            show: row => row.standardStatus === 'DRAFT',
            formattor: () => {
              return _this.$t('common.delete')
            }
          },
          {
            callback: row => this.submitHandle(row, 'ENABLE'),
            // code: "pr:requirementApply:edit",
            show: row => row.standardStatus === 'UNABLE',
            formattor: () => {
              return _this.$t('common.active')
            }
          },
          {
            callback: row => this.submitHandle(row, 'UNABLE'),
            // code: "pr:requirementApply:edit",
            show: row => row.standardStatus === 'ENABLE',
            formattor: () => {
              return _this.$t('common.inactive')
            }
          }
        ]
      }
    ]
    this.defaultTableHeader = this.tableHeader
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    deleteOneContent (index, row) {
      this.displayItem.splice(index, 1)
    },
    getCategoryObj (val, scope) {
      scope.categoryId = val ? val.categoryId : ''
      scope.categoryCode = val ? val.categoryCode : ''
      scope.categoryName = val ? val.categoryName : ''
    },
    addCategorys () {
      let datas = {
        standardId: this.globalEntryConfigId,
        reviewFormStandardCategoryList: this.displayItem.filter(v => v.categoryId)
      }
      reviewFormStandard.saveCategoryList(datas).then(data => {
          if (data.data.categoryListSaveStatus) {
            this.$message.warning(this.$t('dataConfMod.msgRepeatDel')) // 你选择的重复的品类已被删除!
            this.dialogFormVisible2 = false
            this.getQuerydata()
          } else {
            this.$message.success(this.$t('common.success'))
            this.dialogFormVisible2 = false
            this.getQuerydata()
          }
        })
    },
    handleItemSelection (val) {
      this.multipleSelection = val
    },
    addOneItem () {
      this.displayItem.push({
        categoryId: null,
        categoryCode: null,
        categoryName: null
      })
    },
    readOne (row) {
      reviewFormStandard.getCategoryList(row.standardId).then(data => {
          this.displayItem = data.data.list
          this.dialogFormVisible2 = true
          this.globalEntryConfigId = row.standardId
        })
    },
    getQuerydata (params) {
      this.queryParam = params
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    deleteHandle (row) {
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          reviewFormStandard.delete(row.standardId).then(res => {
            this.$message.success(res.message)
            this.getQuerydata()
          })
        })
        .catch(() => {})
    },
    addHandle (row) {
      this.mode = 'add'
      const tab = {
        component: reviewformstandardEdit,
        ctrlHeight: true,
        params: {
          row,
          flag: this.mode
        },
        title: this.$t('dataConfMod.reviewformstandardAdd'),
        name: 'reviewformstandardEdit'
      }
      this.$emit('tab-add', tab)
    },
    editHandle (row, bol) {
      if (bol === 1) {
        this.mode = 'edit'
      } else {
        this.mode = 'readOnly'
      }

      const tab = {
        component: reviewformstandardEdit,
        ctrlHeight: true,
        params: {
          row,
          flag: this.mode
        },
        title: this.$t('dataConfMod.reviewformstandardEdit'),
        name: 'reviewformstandardEdit' + row.standardId
      }
      this.$emit('tab-add', tab)
    },
    submitHandle (row, name) {
      const obj = {
        standardId: row.standardId,
        standardStatus: name
      }
      reviewFormStandard.modify(obj).then(res => {
        this.$message.success(res.message)
        this.getQuerydata()
      })
    },
    handleCurrentChange (val) {
      this.currentRows = val
    },
    // 上传附件成功
    handleUploadSuccess (file, row, key) {
      const id = file
      row[key] = id.toString()
    },
    // 删除文件
    handleAttachmentRemove (row, key) {
      row[key] = ''
    }
  }
}
</script>
