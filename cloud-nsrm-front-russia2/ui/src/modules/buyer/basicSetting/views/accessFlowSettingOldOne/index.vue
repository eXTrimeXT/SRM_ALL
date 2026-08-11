<template>
  <el-container
    class="flex-container-notab the_inventory_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="queryForm"
        @getFormData="getQuerydata"
      />

      <MainHeader>
        <template slot="left">
          <AuthorityButton
            type="primary"
            code="sup:accessFlowSetting:addNew"
            @click="addNew"
          >
            {{ $t('common.add') }}
          </AuthorityButton>
          <el-button
            :disabled="tableSelection.length==0"
            @click="save"
          >
            {{ $t('common.save') }}
          </el-button>
        </template>
      </MainHeader>

      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :check-change="handleSelectionChange"
        :set-selectable="setSelectable"
        :checkbox="true"
        :is-trigger-row="false"
        :pre-query-data="queryParam"
        url="/api-sup/entry/entryConfig/listPageByParam"
      >
        <template #quaReviewType="{ scope }">
          <DictSelect
            v-model="scope.row.quaReviewType"
            code="QUA_REVIEW_TYPE"
            @change="val => listChange(scope.row)"
          />
        </template>
        <!--资质审查-->
        <template #ifQuaReview="{ scope }">
          <div class="checkboxCon">
            <el-checkbox
              v-model="scope.row.ifQuaReview"
              :checked="true"
              true-label="Y"
              false-label="N"
              :disabled="true"
              @change="val => listChange(scope.row)"
            />
          </div>
        </template>
        <template #ifAuth="{ scope }">
          <div class="checkboxCon">
            <el-checkbox
              v-model="scope.row.ifAuth"
              true-label="Y"
              false-label="N"
              @change="val => listChange(scope.row)"
            />
          </div>
        </template>
        <template #ifAuthSample="{ scope }">
          <div class="checkboxCon">
            <el-checkbox
              v-model="scope.row.ifAuthSample"
              true-label="Y"
              false-label="N"
              @change="val => listChange(scope.row)"
            />
          </div>
        </template>
        <template #ifMaterial="{ scope }">
          <div class="checkboxCon">
            <el-checkbox
              v-model="scope.row.ifMaterial"
              true-label="Y"
              false-label="N"
              @change="val => listChange(scope.row)"
            />
          </div>
        </template>
        <!--是否自动生效-->
        <template #ifAuthEffective="{ scope }">
          <div class="checkboxCon">
            <el-checkbox
              v-model="scope.row.ifAuthEffective"
              true-label="Y"
              false-label="N"
              @change="val => listChange(scope.row)"
            />
          </div>
        </template>
      </TableView>
      <!-- 弹框区域-->
      <!-- 供方准入流程配置 -->
      <srm-dialog
        :title="$t('dataConfMod.accessFlowConfCate')"
        :visible.sync="dialogFormVisible2"
        :close-on-click-modal="false"
        size="middle"
      >
        <div class="mb10" style="display: flex">
          <CCategorySelect
            select-type="button"
            :multiple="true"
            :selected-lines="displayItem"
            @select="addCategorysList"
          />
          <MImport
            :title="$t('common.import')"
            :up-load-url="importsPL"
            :extra-data="extraData"
            btn-class-name="detail-pbtn"
            @downloadTemplate="downloadTemplate"
            @handleSuccess="handleSuccess"
          />
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
          />
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
      <!-- 评审附件配置 -->
      <srm-dialog
        :title="$t('dataConfMod.accessFlowSettingAttch')"
        :visible.sync="fileConfigVisible"
        :close-on-click-modal="false"
        size="large"
      >
        <div class="mb10">
          <el-radio-group
            v-model="configFunType"
            fill="#D3D3D3"
            text-color="#000000"
          >
            <el-radio-button
              v-for="item in entryFileType"
              :key="item.value"
              :label="item.value"
            >
              {{ item.label }}
            </el-radio-button>
          </el-radio-group>
        </div>
        <div class="mb10">
          <el-button
            type="primary"
            class="detail-pbtn"
            style="margin-top:5px;margin-bottom:5px;"
            @click="addFileConfig"
          >
            {{ $t('common.add') }}
          </el-button>
        </div>
        <!-- fileConfigList  -->
        <el-table
          :data="displayData"
          style="width: 100%"
          border
          height="250px"
          highlight-current-row
          @selection-change="handleItemSelection"
        >
          <!-- 附件描述 -->
          <el-table-column
            align="center"
            prop="templateDesc"
            :label="$t('dataConfMod.templateDesc')"
            min-width="150"
            :show-overflow-tooltip="true"
          >
            <template slot-scope="scope">
              <el-input v-model="scope.row.templateDesc" />
            </template>
          </el-table-column>
          <!-- 附件模板 -->
          <el-table-column
            align="center"
            prop="templateFileId"
            :label="$t('dataConfMod.attachTemplate')"
            min-width="150"
            :show-overflow-tooltip="true"
          >
            <template slot-scope="scope">
              <SrmCommonFile
                :extra-data="fileInfo"
                :default-file="{
                  fileId: scope.row.templateFileId,
                  fileName: scope.row.templateFileName
                }"
                :readonly="false"
                @on-change="({file}) => handleUploadSuccess(file,scope.row)"
              />
              <!--              @on-change="({file}) => handleUploadSuccess(file,scope.row)"-->
            </template>
          </el-table-column>
          <el-table-column
            prop="ifRequired"
            :label="$t('dataConfMod.isRequested')"
            align="center"
          >
            <template slot-scope="scope">
              <el-checkbox
                v-model="scope.row.ifRequired"
                true-label="Y"
                false-label="N"
              />
            </template>
          </el-table-column>
          <!-- 有效期必填 -->
          <el-table-column
            prop="ifValidDate"
            :label="$t('dataConfMod.ifValidDate')"
            align="center"
          >
            <template slot-scope="scope">
              <el-checkbox
                v-model="scope.row.ifValidDate"
                true-label="Y"
                false-label="N"
              />
            </template>
          </el-table-column>
          <!-- 更新时间 -->
          <el-table-column
            align="center"
            prop="lastUpdateDate"
            :label="$t('common.updateTime')"
            min-width="150px"
            :formatter="(row, column, cellValue) => $parseTime(cellValue)"
          />
          <!-- 更新人 -->
          <el-table-column
            prop="lastUpdatedUserName"
            :label="$t('common.updatePeople')"
            align="center"
          />
          <!-- 操作 -->
          <el-table-column
            :label="$t('common.operation')"
            width="60"
            align="center"
          >
            <template slot-scope="scope">
              <el-button
                type="text"
                @click="delFileConfig(scope.$index, scope.row)"
              >
                {{ $t('common.delete') }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <template #footer>
          <el-button @click="fileConfigVisible = false">
            {{ $t('common.close') }}
          </el-button>
          <el-button
            type="primary"
            @click="addFiles"
          >
            {{ $t('common.confirm') }}
          </el-button>
        </template>
      </srm-dialog>

      <srm-dialog
        :title="$t('dataConfMod.contractualObligations')"
        :visible.sync="dialogFormVisible3"
        :close-on-click-modal="false"
        size="middle"
      >
        <el-button
          type="primary"
          style="margin-bottom:15px"
          @click="comfirmSaveList"
        >
          <!-- 新增 -->
          {{ $t('common.add') }}
        </el-button>
        <el-table
          v-loading="loadingC"
          :data="contractData"
          style="width: 100%"
          border
          max-height="250px"
        >
          <el-table-column
            align="center"
            type="index"
            :label="$t('purSettlementMod.tabindex')"
            width="50"
          />
          <!-- 合同模板 -->
          <el-table-column
            align="center"
            prop="modelHeadId"
            :label="$t('vendorMod.modelName')"
          >
            <template slot-scope="scope">
              <el-select
                v-model="scope.row.modelHeadId"
                :placeholder="$t('common.pleaseSelect')"
              >
                <el-option
                  v-for="item in modelSelect"
                  :key="item.value"
                  :label="item.name"
                  :value="item.value"
                />
              </el-select>
            </template>
          </el-table-column>
          <!-- 是否必须签订才能供方生效 -->
          <el-table-column
            align="center"
            prop="ifContractNecessary"
            :label="$t('dataConfMod.orderSupplierTakeEffect')"
          >
            <template slot-scope="scope">
              <el-switch
                v-model="scope.row.ifContractNecessary"
                active-value="Y"
                inactive-value="N"
              />
            </template>
          </el-table-column>
          <!-- 删除 -->
          <el-table-column
            :label="$t('common.operation')"
            width="60"
          >
            <template slot-scope="scope">
              <el-button
                type="text"
                @click="handleDelClick(scope.$index, scope.row)"
              >
                {{ $t('common.delete') }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <template #footer>
          <el-button @click="dialogFormVisible3 = false">
            <!-- 取 消 -->
            {{ $t('common.cancel') }}
          </el-button>
          <el-button
            type="primary"
            @click="comfirmSaveModel"
          >
            <!-- 确 定 -->
            {{ $t('common.confirm') }}
          </el-button>
        </template>
      </srm-dialog>
    </el-main>
  </el-container>
</template>

<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import CCategorySelect from 'lib@/components/c-category-select'
import MImport from 'lib@/components/import'
import { downloadFileLink } from 'lib@/utils/file'
import { getDictItemList } from '@/api/common'
import { adaptDictData } from '@/utils'
import { sysPrefix } from '@/config/ipConfig'
import { quaApi } from 'modb@/vendorManagementBuyer/api/vendorManagement'
import { accessFlowSetting } from 'modb@/basicSetting/api/basicSetting'

export default {
  name: 'AccessFlowSetting',

  components: {
    TableView,
    MainHeader,
    FormWrapper,
    MImport,
    CCategorySelect
  },

  data () {
    return {
      tableSelection: [],
      formPL: {
        maxCategoryName: '',
        categoryName: ''
      },
      pageNum: 1,
      totalPages: 0,
      importsPL: `${sysPrefix()}/api-sup/entry/entry-category-config/importExcel/`,
      extraData: {
        fileModular: 'base',
        fileFunction: 'quotadetail',
        fileType: 'excel'
      },
      loadingC: false,
      contractOne: '',
      modelSelect: [],
      contractData: [],
      gridId: 'accessTableList',
      curOpt: 'add',
      selectList: [],
      displayItem: [],
      displayItemList: [],
      fileConfigList: [],
      multipleSelection: [],
      currentRow: [],
      tableHeader: [],
      tableData: [],
      quaReviewType: [],
      accessProcess: [],
      trialProcess: [],
      ifMaterialList: [],
      entryFileType: [],
      queryForm: [],
      preForm: {
        quaReviewType: null,
        categoryId: null,
        categoryCode: null,
        categoryName: null
      },
      submitModel: {
        entryConfigId: null,
        quaReviewType: null,
        ifAuth: 'N',
        ifQpaQsa: 'N',
        ifAuthOnSite: 'N',
        ifAuthSample: 'N',
        ifQuaReview: 'Y', // 是否资质审查
        ifMaterial: '', // 是否物料试用
        ifAuthEffective: 'N' // 是否自动生效
      },
      rules: {
        quaReviewType: [{ required: true, message: this.$t('dataConfMod.msgQuaReviewType') }], // '请输入供方准入类型'
        accessProcess: [{ required: true, message: this.$t('dataConfMod.msgAccessProcess') }], // '请输入准入流程'
        trialProcess: [{ required: true, message: this.$t('dataConfMod.msgTrialProcess') }] // '请输入样品试用流程'
      },
      dialogFormVisible: false,
      dialogFormVisible2: false,
      dialogFormVisible3: false,
      globalEntryConfigId: null,
      fileConfigVisible: false,
      formLabelWidth: '100px',
      isActive: false,
      queryParam: {},
      configFunType: '', // 附件配置功能--数据过滤
      // 文件上传配置信息
      fileInfo: {
        fileModular: 'sup', // 文件所属模块 -》基础模块
        fileFunction: 'accessFlowSetting_backups', // 文件所属功能
        fileType: 'images' // 文件所属类型
      }
    }
  },

  computed: {
    displayData () {
      if (this.configFunType) {
        return this.fileConfigList.filter(item => item.type == this.configFunType)
      } else {
        return this.fileConfigList
      }
    }
  },

  created () {
    const objs = { pageNum: 1, pageSize: 1500, status: 'VALID' }
    this.queryForm = [
      {
        prop: 'quaReviewType',
        label: () => this.$t('dataConfMod.quaReviewType'), // 供方准入类型
        type: 'dict',
        code: 'QUA_REVIEW_TYPE'
      },
      {
        prop: 'categoryId',
        label: () => this.$t('common.category'), // 品类
        type: 'quicksearch',
        showKey: 'categoryName',
        propKey: 'categoryId',
        name: 'scc_base_purchase_category2'
      }
    ]
    this.tableHeader = [
      {
        prop: 'entryConfigNum',
        label: () => this.$t('dataConfMod.entryConfigNum'), // 供方准入配置单号
        width: '150'
      },
      {
        prop: 'quaReviewType',
        label: () => this.$t('dataConfMod.quaReviewType'), // '供方准入类型'
        minWidth: '180',
        showType: 'slot',
        slot: 'quaReviewType'
      },
      // {
      //   prop: 'ifQuaReview',
      //   label: () => this.$t('dataConfMod.ifQuaReview'), // 是否需要资质审查
      //   minWidth: 150,
      //   showType: 'slot',
      //   slot: 'ifQuaReview'
      // },
      {
        prop: 'ifAuth',
        label: () => this.$t('dataConfMod.ifSupplierAuth'), // 是否需要供方评审
        minWidth: 150,
        showType: 'slot',
        slot: 'ifAuth'
      },
      {
        prop: 'ifAuthSample',
        label: () => this.$t('dataConfMod.ifAuthSample'), // 是否样品确认
        minWidth: 150,
        showType: 'slot',
        slot: 'ifAuthSample'
      },
      {
        prop: 'ifMaterial',
        label: () => this.$t('dataConfMod.ifMaterial'), // 是否物料试用
        minWidth: 150,
        showType: 'slot',
        slot: 'ifMaterial'
      },
      // {
      //   prop: 'ifAuthEffective',
      //   label: () => this.$t('dataConfMod.ifEffect'), // 是否自动生效
      //   minWidth: 150,
      //   showType: 'slot',
      //   slot: 'ifAuthEffective'
      // },
      {
        prop: 'ceeaCategoryName',
        label: () => this.$t('common.category'), // 品类
        minWidth: 110,
        showType: 'button',
        btnStyle: 'text',
        callback: row => this.readOne(row),
        show: row => row.isNoShow !== 'Y',
        // 维护品类
        formattor: () => this.$t('dataConfMod.maintainCategory'),
        code: 'sup:accessFlowSetting:ceeaCategoryName'
      },
      {
        prop: 'ceeaCategoryName',
        label: () => this.$t('dataConfMod.reviewAttachDef'), // 评审附件定义
        minWidth: 110,
        showType: 'button',
        btnStyle: 'text',
        show: row => row.isNoShow !== 'Y',
        callback: row => this.reviewAttachment(row),
        // 附件定义
        formattor: () => this.$t('dataConfMod.attachDef'),
        code: 'sup:accessFlowSetting:ceeaCategoryName'
      },
      // {
      //   prop: 'ceeaCategoryName',
      //   label: this.$t('dataConfMod.ceeaCategoryName'), // 合同校验定义
      //   minWidth: 110,
      //   showType: 'button',
      //   btnStyle: 'text',
      //   show: row => row.isNoShow !== 'Y',
      //   callback: row => this.contractFuction(row),
      //   // 合同定义
      //   formattor: () => this.$t('dataConfMod.contractualObligations'),
      //   code: 'sup:accessFlowSetting:ceeaCategoryName'
      // },
      {
        prop: 'createdUserName', // createdBy
        label: () => this.$t('common.creator'), // 创建人
        width: 100
      },
      {
        prop: 'creationDate',
        label: () => this.$t('common.creationTime'), // 创建时间
        width: 150,
        dataType: 'dateTime'
      },
      {
        prop: 'operation',
        label: () => this.$t('common.operation'), // '操作'
        width: 100,
        fixed: 'right',
        showType: 'buttons',
        buttons: [
          // 删除
          {
            callback: (row, scope) => this.delRowData(row, scope),
            formattor: () => this.$t('common.delete'),
            code: 'sup:accessFlowSetting:delete'
          }
        ]
      }
    ]
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },

  mounted () {
    // 即将进行【准入流程配置】，您需要完成：1、设置不同准入类型的准入流程选择；2、设置不同准入类型的样品试用流程选择；
    // '提示'
    // '开始'
    let accessFlowTip = localStorage.getItem('accessFlowTip') || 'Y'
    if (accessFlowTip === 'Y') {
      this.$confirm(this.$t('dataConfMod.accessFlowSettingAlert'), this.$t('common.tips'), {
        distinguishCancelAndClose: true,
        confirmButtonText: this.$t('common.start'),
        cancelButtonText: this.$t('common.toNotshowTip')
      })
        .then(() => { /* 点击开始 */ })
        .catch(() => {
          // 不再提示
          localStorage.setItem('accessFlowTip', 'N')
        })
    }
    this.fatchDictData()
  },

  methods: {
    setSelectable (row) {
      return row.ifRepush === 1 ? 1 : 0
    },
    listChange (row) {
      row.ifRepush = 1
      this.$refs.accessTableList.setCheckboxRow(row)
      this.addTableSelection(row)// 勾选后保留选中字段问题
    },
    addTableSelection (row) {
      // 勾选存储选中
      if (this.isSampleTableSelection(row)) {
        const index = this.tableSelection.findIndex(item => item.entryConfigId === row.entryConfigId)
        this.tableSelection[index] = row
      } else {
        this.tableSelection.push(row)
      }
    },
    isSampleTableSelection (row) {
      // 判断选中行是否存在
      return this.tableSelection.some(item => item.entryConfigId === row.entryConfigId)
    },
    save () {
      let bol = false
      this.tableSelection.forEach(e => {
        if (e.quaReviewType == '' || !e.quaReviewType) {
          bol = true
        }
      })
      if (bol) {
        this.$message.error(this.$t('vendorMod.msgQuaReviewType'))
        return false
      }
      quaApi.batchSaveOrUpdate(this.tableSelection).then(datas => {
        this.$message.success(datas.message)
        this.tableSelection = []
        this.getQuerydata()
      })
    },
    handleCurrentChange (val) {
      this.pageNum = val
      this.displayItem = [...this.displayItem, ...this.multipleSelection]
      let listC = new Set(this.displayItem)
      this.displayItem = Array.from(listC)
      this.categoryList('change')// 如果是分页变更的时候
    },
    handleSuccess (res) {
      let datas = res.data
      this.displayItem = [...this.displayItem, ...datas]
    },
    downloadTemplate () { // 品类导入模板.xlsx
      downloadFileLink(
        '/api-sup/entry/entry-category-config/importExcelTemplate',
        this.$t('vendorMod.entryCategoryConfigImportTemp')
      ).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail'))
      })
    },
    // 获取数据字典
    fatchDictData () {
      let dictParamsArr = [
        { dictCode: 'QUA_REVIEW_TYPE' }, // 供方准入类型
        { dictCode: 'ACCESS_PROCESS_TYPE' }, // 准入流程
        { dictCode: 'MATERIAL_TRIAL' }, //
        { dictCode: 'ENTRY_FILE_TYPE_OLD' } // 准入场景类型
      ]
      getDictItemList(dictParamsArr).then(res => {
        const [QUA_REVIEW_TYPE, ACCESS_PROCESS_TYPE, MATERIAL_TRIAL, ENTRY_FILE_TYPE_OLD] = res.data
        this.quaReviewType = adaptDictData(QUA_REVIEW_TYPE.QUA_REVIEW_TYPE)
        this.queryForm[0].options = this.quaReviewType
        this.accessProcess = adaptDictData(ACCESS_PROCESS_TYPE.ACCESS_PROCESS_TYPE)
        this.ifMaterialList = adaptDictData(MATERIAL_TRIAL.MATERIAL_TRIAL)
        this.entryFileType = adaptDictData(ENTRY_FILE_TYPE_OLD.ENTRY_FILE_TYPE_OLD)
      })
    },
    // 删除合同定义其中一条数据
    handleDelClick (index) {
      this.contractData.splice(index, 1)
    },
    // 点击合同定义
    contractFuction (row) {
      this.contractData = []
      this.dialogFormVisible3 = true
      this.contractOne = row.entryConfigId
      const contractOne = { entryConfigId: row.entryConfigId }

      this.loadingC = true
      accessFlowSetting.contractListPage(contractOne).then(res => {
        this.contractData = res.data.list
        this.loadingC = false
      })
    },
    // 上传附件成功
    handleUploadSuccess (file, row) {
      const { fileId = '', fileName = '' } = file || {}
      row.templateFileId = fileId.toString()
      row.templateFileName = fileName
    },
    getQuerydata (v) {
      this.queryParam = Object.assign({}, v)
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    addNew () {
      this.$refs.accessTableList.addOneEditableColumn({ isNoShow: 'Y' })
    },
    // 选中
    handleSelectionChange (value) {
      this.tableSelection = value
    },
    saveData () {
      this.saveOrUpdateHandle(this.curOpt)
    },
    // 新增编辑组织数据
    saveOrUpdateHandle () {
      let submitData = this.submitModel
      this.$refs.accessform.validate(valid => {
        if (valid) {
          this.$http({
            url: submitData.entryConfigId
              ? '/api-sup/entry/entryConfig/modify'
              : '/api-sup/entry/entryConfig/add',
            method: 'POST',
            data: submitData,
            loading: true
          })
            .then(() => {
              this.$message.success(this.$t('common.success')) // 操作成功
              this.dialogFormVisible = false
              this.getQuerydata()
            })
        }
      })
    },
    resetForm () {
      for (let i in this.preForm) {
        this.preForm[i] = null
      }
    },
    editDetail (row) {
      this.curOpt = 'edit'
      for (let i in this.submitModel) {
        this.submitModel[i] = row[i]
      }
      this.dialogFormVisible = true
      this.controlHandle(this.curOpt)
    },
    delRowData (row, scope) {
      let index = scope.$index
      if (!row.createdId) {
        this.$refs.accessTableList.deleteRow(index)
        return false
      }
      // 当前操将永久删除这条数据，确认删除这条数据？
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          this.$http({
            url: '/api-sup/entry/entryConfig/delete',
            method: 'GET',
            params: { entryConfigId: row.entryConfigId },
            loading: true
          })
            .then(() => {
              this.$message.success(this.$t('common.success')) // 操作成功
              this.getQuerydata()
            })
        })
    },
    comfirmSave () {
      this.saveOrUpdateHandle(this.curOpt)
    },
    comfirmSaveModel () {
      this.contractData.forEach((element, index) => {
        this.contractData[index].entryConfigId = this.contractOne
      })
      const contractData = this.contractData
      accessFlowSetting.batchSave(contractData).then(res => {
        this.$message.success(res.message)
        this.dialogFormVisible3 = false
        this.getQuerydata()
      })
    },
    comfirmSaveList () {
      this.contractData.push({})
    },
    // 维护品类选择查询
    submitForm () {
      this.categoryList()
    },
    readOne (row) {
      this.importsPL = '/api-sup/entry/entry-category-config/importExcel/' + row.entryConfigId
      this.$http({
        url: '/api-sup/entry/entry-category-config/getCategoryListByEntryId',
        method: 'GET',
        params: { entryConfigId: row.entryConfigId },
        loading: true
      })
        .then(data => {
          this.displayItem = data.data.list
          this.dialogFormVisible2 = true
          this.globalEntryConfigId = row.entryConfigId
        })
    },
    categoryList (ifchange) {
      let data = {
        maxCategoryName: this.formPL.maxCategoryName,
        categoryName: this.formPL.categoryName,
        pageSize: 10,
        pageNum: this.pageNum
      }
      accessFlowSetting.listPageForEntryConfig(data).then(res => {
        this.totalPages = res.data.total
        let list = []
        if (this.displayItem.length > 0) {
          res.data.list.forEach(datas => {
            this.displayItem.forEach(datas2 => {
              if (datas.categoryCode != datas2.categoryCode) {
                list.push(datas)
              }
            })
          })
          let listC = new Set(list)
          list = Array.from(listC)
        } else {
          list = res.data.list
        }

        if (ifchange === 'change' && this.pageNum != 1) {
          this.displayItemList = list
        } else {
          this.displayItemList = [...this.displayItem, ...list]
        }

        this.$nextTick(() => {
          this.displayItem.forEach(datas => {
            this.$refs.multipleTable.toggleRowSelection(datas)
          })
        })
      })
    },
    // 附件定义
    reviewAttachment (row) {
      this.$http({
        url: '/api-sup/entry/fileconfig/getFileListByEntryId',
        method: 'GET',
        params: { entryConfigId: row.entryConfigId },
        loading: true
      })
        .then(data => {
          this.fileConfigList = data.data.list
          this.configFunType = this.entryFileType[0].value
          this.fileConfigVisible = true
          this.globalEntryConfigId = row.entryConfigId
        })
    },
    addFileConfig () {
      this.fileConfigList.push({
        templateDesc: '',
        templateFileName: '',
        templateFileId: '',
        ifRequired: 'Y',
        type: this.configFunType // 当前选择的功能
      })
    },
    addCategorysList (data) {
      if (data.length > 0) {
        this.displayItem = data
        let listC = new Set(this.displayItem)
        this.displayItem = Array.from(listC)
      }
    },
    addCategorys () {
      if (this.displayItem.length === 0) {
        this.$message({
          type: 'warning',
          message: this.$t('dataConfMod.msgKeepCateDetail')
        })
        return
      }
      if (this.displayItem.filter(v => v.categoryId).length === 0) {
        this.$message({
          type: 'warning',
          message: this.$t('dataConfMod.msgKeepCateDetail')
        })
        return
      }
      this.$http({
        url: '/api-sup/entry/entry-category-config/saveEntryCategoryList',
        method: 'POST',
        data: {
          entryConfigId: this.globalEntryConfigId,
          entryCategoryConfigList: this.displayItem.filter(v => v.categoryId)
        },
        loading: true
      })
        .then(data => {
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
    // 保存附件
    addFiles () {
      this.$http({
        url: '/api-sup/entry/fileconfig/batchSave',
        method: 'POST',
        data: {
          entryConfigId: this.globalEntryConfigId,
          list: this.fileConfigList
        },
        loading: true
      })
        .then(() => {
          this.$message.success(this.$t('common.successSave'))
          this.fileConfigVisible = false
          this.getQuerydata()
        })
    },
    handleItemSelection (val) {
      this.multipleSelection = val
    },
    deleteOneContent (index) {
      this.displayItem.splice(index, 1)
    },
    delFileConfig (index, row) {
      if (row.createdBy) {
        this.fileConfigList.forEach((datas, index) => {
          if (datas.lastUpdateDate == row.lastUpdateDate && datas.fileConfigId == row.fileConfigId && datas.templateDesc == row.templateDesc) {
            this.fileConfigList.splice(index, 1)
          }
        })
      } else {
        this.fileConfigList.pop()
      }
    },
    getCategoryObj (val, scope) {
      scope.categoryId = val ? val.categoryId : ''
      scope.categoryCode = val ? val.categoryCode : ''
      scope.categoryName = val ? val.categoryName : ''
    }
  }
}
</script>

<style scoped lang="scss">
.the_catSelect-wrap {
  margin-right: 5px;
}
.mb10 {
  margin-bottom: 5px;
}
.el-divider {
  margin: 0;
}
.paginationWrap{
  margin-top:10px;
  display: flex;
  justify-content:flex-end
}
:deep(.smallInput>input){
  height: 24px;
  line-height: 24px;
  min-height: 24px;
}
.checkboxCon {
  width: 100%;
  text-align: center;
}
</style>
