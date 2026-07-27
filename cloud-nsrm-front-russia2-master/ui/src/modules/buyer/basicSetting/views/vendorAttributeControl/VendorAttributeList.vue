<template>
  <el-container
    class="flex-container the_currency_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper :form-array="queryForm" @getFormData="getQuerydata" />
      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <AuthorityButton
            type="primary"
            code="sup:vendorAttributeControl:addNew"
            @click="addNew"
          >
            <!-- 新增属性 -->
            {{ $t("common.add") }}
          </AuthorityButton>
          <AuthorityButton
            code="sup:vendorAttributeControl:dimensionCtrl"
            @click="dimensionCtrl"
          >
            <!-- 属性维度管理 -->
            {{ $t("dataConfMod.dimensionCtrl") }}
          </AuthorityButton>
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-data="tableList"
        :table-header="tableHeader"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :com-active="$attrs['changeTab']"
        :show-filter-bar="showFilterBar === 1"
        url="/api-sup/dim/dimField/listPageByParam"
      />
    </el-main>
    <!-- 新增 编辑弹框区域-->
    <!-- 属性管理 -->
    <srm-dialog
      :title="$t('dataConfMod.attributeManage')"
      :visible.sync="dialogFormVisible"
      :close-on-click-modal="false"
      size="middle"
    >
      <el-form
        ref="orgform"
        :model="fieldDataModel.fieldDataform"
        :rules="fieldDataModel.rules"
      >
        <srm-row>
          <srm-col :init-col="2">
            <!-- 维度名称 -->
            <el-form-item :label="$t('perfMod.dimensionName')" prop="dimId">
              <el-select
                v-model="fieldDataModel.fieldDataform.dimId"
                :disabled="isAttrEdit"
                @change="dimTypeChange"
              >
                <el-option
                  v-for="item in attributeDim"
                  :key="item.id"
                  :label="item.label"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </srm-col>
          <srm-col :init-col="2">
            <!-- 属性编码 -->
            <el-form-item
              :label="$t('dataConfMod.attributeCode')"
              prop="fieldCode"
            >
              <el-input
                v-model="fieldDataModel.fieldDataform.fieldCode"
                :disabled="isAttrEdit"
              />
            </el-form-item>
          </srm-col>
          <srm-col :init-col="2">
            <!-- 属性名称 -->
            <el-form-item
              :label="$t('dataConfMod.attributeName')"
              prop="fieldName"
            >
              <el-input v-model="fieldDataModel.fieldDataform.fieldName" />
            </el-form-item>
          </srm-col>
          <srm-col :init-col="2">
            <!-- 属性类型 -->
            <el-form-item
              :label="$t('dataConfMod.attributeType')"
              prop="fieldTypeCode"
            >
              <DictSelect
                v-model="fieldDataModel.fieldDataform.fieldTypeCode"
                code="VENDOR_ATTRIBUTE_TYPE"
                @change="attrTypeChange"
              />
            </el-form-item>
          </srm-col>
          <!-- 选择字典 -->
          <srm-col v-if="isDict" :init-col="2">
            <el-form-item :label="$t('bidMod.dictCode')" prop="dictCode">
              <el-input v-model="fieldDataModel.fieldDataform.dictCode" />
            </el-form-item>
          </srm-col>
          <!-- 选择快查 -->
          <srm-col v-if="isQuickSearch" :init-col="2">
            <el-form-item :label="$t('dataConfMod.quickSearchColumn')" prop="quickSearchColumn">
              <el-input v-model="fieldDataModel.fieldDataform.quickSearchColumn" />
            </el-form-item>
          </srm-col>
          <srm-col v-if="isQuickSearch" :init-col="2">
            <el-form-item :label="$t('dataConfMod.quickSearchColumnShow')" prop="quickSearchColumnShow">
              <el-input v-model="fieldDataModel.fieldDataform.quickSearchColumnShow" />
            </el-form-item>
          </srm-col>
          <srm-col v-show="isTextOrNumber" :init-col="2">
            <!-- 长度限制 -->
            <el-form-item
              :label="$t('dataConfMod.fieldLength')"
              prop="fieldLength"
            >
              <el-input v-model="fieldDataModel.fieldDataform.fieldLength" />
            </el-form-item>
          </srm-col>
          <srm-col v-show="isTwoDimFlag" :init-col="2">
            <!-- 是否二维数组左侧属性 -->
            <el-form-item
              :label="$t('dataConfMod.twoDimFlag')"
              prop="twoDimFlag"
            >
              <div>
                <el-switch
                  v-model="fieldDataModel.fieldDataform.twoDimFlag"
                  active-value="Y"
                  inactive-value="N"
                />
              </div>
            </el-form-item>
          </srm-col>
          <srm-col v-show="isAttachment" :init-col="2">
            <!-- 附件大小限制 -->
            <el-form-item :label="$t('dataConfMod.fileSize')" prop="fileSize">
              <DictSelect
                v-model="fieldDataModel.fieldDataform.fileSize"
                code="FILE_LIMIT"
              />
            </el-form-item>
          </srm-col>
          <srm-col v-show="isAttachment" :init-col="2">
            <!-- 附件格式限制 -->
            <el-form-item :label="$t('dataConfMod.fileType')" prop="fileType">
              <DictSelect
                v-model="fieldDataModel.fieldDataform.fileType"
                code="FILE_TYPE"
              />
            </el-form-item>
          </srm-col>
        </srm-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">
          <!-- 取 消 -->
          {{ $t("common.cancel") }}
        </el-button>
        <el-button type="primary" @click="comfirmSave">
          <!-- 确 定 -->
          {{ $t("common.confirm") }}
        </el-button>
      </div>
    </srm-dialog>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import AttributeDimension from './AttributeDimension'
import { getDictItem } from '@/api/common'
import { adaptDictData } from '@/utils'
import { vendorAttributeControl } from 'modb@/basicSetting/api/basicSetting'

export default {
  name: 'AttributeList',
  components: {
    TableView,
    MainHeader,
    FormWrapper
  },
  data () {
    return {
      pageSize: 15,
      gridId: 'attributeList',
      currentRow: null,
      showFilterBar: 1,
      queryParam: {},
      dialogFormVisible: false,
      queryForm: [], // 查询条件
      tableHeader: [], // 表格列数据
      tableList: [],
      opt: 'add',
      tableLoading: false,
      isTextOrNumber: false, // 是否是文本或数字
      isAttachment: false, // 是否是附件
      isAttrEdit: false, // 是否是编辑
      isDict: false, // 是否有字典编码
      isQuickSearch: false, // 是否快查
      isTwoDimFlag: false,
      attributeDim: [],
      fieldTypeList: [],
      fileType: [],
      fileLimit: [],
      fieldDataModel: {
        fieldDataform: {
          tenantId: this.$store.getters.userId, // 租户ID //目前传用户id
          dimId: '', // 属性维度
          dimName: '',
          fieldName: '', // 属性名称
          fieldCode: '', // 属性编码
          fieldTypeCode: '', // 属性类型
          fieldTypeName: '', // 属性类型名称
          dimShowType: '', // 维度类型
          fieldLength: '', // 长度限制
          fileSize: '', // 附件大小
          fileType: '', // 附件类型
          isField: 'Y', // 是否是拓展字典
          fieldOrderNum: '', // 排序
          dictCode: '', // 字典编码
          quickSearchColumn: '',
          quickSearchColumnShow: ''
        },
        rules: {
          dimId: [{ required: true, message: this.$t('dataConfMod.msgDimId') }], // '请选择属性维度'
          fieldName: [
            { required: true, message: this.$t('dataConfMod.msgFieldName') }
          ], // '请输入属性名称'
          fieldCode: [
            { required: true, message: this.$t('dataConfMod.msgFieldCode') }
          ], // '请输入属性编码'
          fieldTypeCode: [
            { required: true, message: this.$t('dataConfMod.msgFieldTypeCode') }
          ], // '请选择属性类型'
          dictCode: [{ required: true, message: this.$t('bidMod.msgDictCode') }], // '请选择字典编码'
          quickSearchColumn: [{ required: true, message: this.$t('common.pleaseInput') }],
          quickSearchColumnShow: [{ required: true, message: this.$t('common.pleaseInput') }]
        }
      }
    }
  },
  created () {
    let _this = this
    this.queryForm = [
      {
        prop: 'dimCode',
        label: () => this.$t('dataConfMod.attributeDim'), // '属性维度'
        type: 'select',
        options: []
      },
      {
        prop: 'fieldName',
        label: () => this.$t('dataConfMod.attributeName') // '属性名称'
      },
      {
        prop: 'fieldTypeCode',
        label: () => this.$t('dataConfMod.attributeType'), // '属性类型',
        type: 'dict',
        code: 'VENDOR_ATTRIBUTE_TYPE'
      }
    ]
    this.tableHeader = [
      {
        prop: 'dimName',
        label: () => this.$t('dataConfMod.attributeDim') // '属性维度',
        // formattor (val) {
        //   return _this.$getDictLabelByValue(_this.attributeDim, val)
        // }
      },
      {
        prop: 'fieldName',
        label: () => this.$t('dataConfMod.attributeName'), // '属性名称',
        minWidth: '160'
      },
      {
        prop: 'fieldCode',
        label: () => this.$t('dataConfMod.attributeCode'), // '属性编码',
        minWidth: '160'
      },
      {
        prop: 'fieldTypeName',
        label: () => this.$t('dataConfMod.attributeType'), // '属性类型',
        align: 'center',
        minWidth: '80'
      },
      {
        prop: 'lastUpdateDate',
        dataType: 'dateTime',
        label: () => this.$t('common.updateTime') // '更新时间'
      },
      {
        prop: 'lastUpdatedUserName', // lastUpdatedBy
        label: () => this.$t('common.updatePeople') // '更新人'
      },
      {
        label: () => this.$t('common.operation'), // '操作',
        width: '100',
        fixed: 'right',
        editType: 'none',
        showType: 'button',
        btnStyle: 'text',
        code: 'sup:vendorAttributeControl:editDetail',
        callback: function (row) {
          this.editDetail(row)
        }.bind(this),
        formattor () {
          return _this.$t('common.edit') // '编辑'
        }
      }
    ]

    this.fatchDictData() // 加载数据字典
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    dimTypeChange () {
      this.attributeDim.forEach(datas => {
        if (datas.id == this.fieldDataModel.fieldDataform.dimId) {
          this.fieldDataModel.fieldDataform.dimShowType = datas.dimShowType
        }
      })
      if (this.fieldDataModel.fieldDataform.dimShowType == 'TABLE') {
        let fieldTypeList = this.fieldTypeList
        fieldTypeList.forEach((datas, indexs) => {
          if (
            datas.value == 'radio' ||
            datas.value == 'checkbox' ||
            datas.value == 'textarea'
          ) {
            this.$delete(fieldTypeList, indexs)
          }
        })
        this.fieldTypeList = fieldTypeList
        this.isTwoDimFlag = true
        this.$forceUpdate()
      } else {
        getDictItem('VENDOR_ATTRIBUTE_TYPE').then(res => {
          this.fieldTypeList = adaptDictData(res.data, 'dict')
          this.queryForm[2].options = this.fieldTypeList
        })
      }
    },
    getQuerydata (v) {
      this.queryParam = v
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    // 获取数据字典
    fatchDictData () {
      // 属性维度
      vendorAttributeControl.getFieldDim({ pageNum: 1, pageSize: 500 }).then(res => {
        this.attributeDim = this.adaptDimData(res.data)
        this.queryForm[0].options = this.attributeDim
      })
    },
    // 适配属性维度数据
    adaptDimData (data) {
      let arr = []
      if (data && data.length > 0) {
        data.forEach(element => {
          arr.push({
            id: element.dimId,
            value: element.dimCode,
            label: element.dimName,
            dimShowType: element.dimShowType
          })
        })
      }
      return arr
    },
    addNew () {
      this.isAttrEdit = false
      this.dialogFormVisible = true
      this.fieldDataModel.fieldDataform.dimId = ''
      this.fieldDataModel.fieldDataform.dimName = ''
      this.fieldDataModel.fieldDataform.fieldName = ''
      this.fieldDataModel.fieldDataform.fieldCode = ''
      this.fieldDataModel.fieldDataform.fieldTypeName = ''
      this.fieldDataModel.fieldDataform.fieldLength = ''
      this.fieldDataModel.fieldDataform.fileSize = ''
      this.fieldDataModel.fieldDataform.fileType = ''
      this.fieldDataModel.fieldDataform.isField = 'Y'
      this.fieldDataModel.fieldDataform.fieldOrderNum = ''
      this.fieldDataModel.fieldDataform.dictCode = ''
      this.fieldDataModel.fieldDataform.quickSearchColumn = ''
      this.fieldDataModel.fieldDataform.quickSearchColumnShow = ''
      this.isTextOrNumber = true
      this.isDict = false
      this.isQuickSearch = false
      this.isAttachment = false
      this.opt = 'add'
    },
    // 属性类型下拉切换
    attrTypeChange (value) {
      this.isTextOrNumber = false
      this.isDict = false
      this.isQuickSearch = false
      this.isAttachment = false
      if (value === 'text' || value === 'number') {
        // 文本|数字
        this.isTextOrNumber = true
      } else if (value === 'attachment') {
        // 附件
        this.isAttachment = true
      } else if (
        value === 'select' ||
        value === 'checkbox' ||
        value === 'radio' ||
        value === 'quickSearch'
      ) {
        // 下拉框与多选框的时候显示字典
        this.isDict = true
      }
      if (value === 'quickSearch') {
        this.isQuickSearch = true
      }
      // 设置选中的name
      let selectedWorkName = {}
      selectedWorkName = this.fieldTypeList.find(item => {
        return item.value === value
      })
      let _this = this
      try {
        _this.fieldDataModel.fieldDataform.fieldTypeName =
          selectedWorkName.label
      } catch (err) { /* nothing */ }
    },
    // 保存
    comfirmSave () {
      this.saveOrUpdateHandle(this.opt)
    },
    // 新增编辑组织数据
    saveOrUpdateHandle (opt) {
      let submitData = this.fieldDataModel.fieldDataform
      if (opt === 'add') {
        // 新增
        delete submitData.fieldId
      }
      vendorAttributeControl.fieldSaveOrUpdate(submitData).then(res => {
        if (res) {
          // 返回数据处理
          this.$message({
            message: res.message,
            type: 'success'
          })
          this.getQuerydata() // 重新查询数据
          this.dialogFormVisible = false
        }
      })
    },
    // 编辑属性
    editDetail (row) {
      let fieldId = row.fieldId
      let _this = this
      vendorAttributeControl.getFieldInfo({ fieldId }).then(res => {
        if (res.data) {
          _this.fieldDataModel.fieldDataform.tenantId = res.data.tenantId
          _this.fieldDataModel.fieldDataform.fieldId = res.data.fieldId
          _this.fieldDataModel.fieldDataform.fieldName = res.data.fieldName
          _this.fieldDataModel.fieldDataform.dimId = res.data.dimId
          _this.fieldDataModel.fieldDataform.fieldCode = res.data.fieldCode
          _this.fieldDataModel.fieldDataform.fieldTypeCode =
            res.data.fieldTypeCode
          _this.fieldDataModel.fieldDataform.fieldTypeName =
            res.data.fieldTypeName
          _this.fieldDataModel.fieldDataform.fieldLength = res.data.fieldLength
          _this.fieldDataModel.fieldDataform.fileSize = res.data.fileSize
          _this.fieldDataModel.fieldDataform.fileType = res.data.fileType
          _this.fieldDataModel.fieldDataform.isField = res.data.isField
          _this.fieldDataModel.fieldDataform.fieldOrderNum =
            res.data.fieldOrderNum
          _this.fieldDataModel.fieldDataform.dictCode = res.data.dictCode
          _this.fieldDataModel.fieldDataform.quickSearchColumn = res.data.quickSearchColumn
          _this.fieldDataModel.fieldDataform.quickSearchColumnShow = res.data.quickSearchColumnShow
          this.dimTypeChange()
          if (
            _this.fieldDataModel.fieldDataform.fieldTypeCode == 'select' ||
            _this.fieldDataModel.fieldDataform.fieldTypeCode == 'checkbox' ||
            _this.fieldDataModel.fieldDataform.fieldTypeCode == 'quickSearch'
          ) {
            this.isDict = true
          }
          if (_this.fieldDataModel.fieldDataform.fieldTypeCode == 'quickSearch') {
            this.isQuickSearch = true
          }
        }
      })
      this.isAttrEdit = true
      this.dialogFormVisible = true
      this.opt = 'edit'
    },
    // 属性维度管理
    dimensionCtrl () {
      // 打开tab页面--',
      this.$emit('tab-add', {
        component: AttributeDimension,
        params: { flag: 'add' },
        title: () => this.$t('dataConfMod.dimensionCtrl'), // '属性维度管理'
        name: 'AttributeDimension'
      })
    }
  }
}
</script>
<style scoped lang="scss">
.el-form-item__label{
  // float: inherit;
}
</style>
