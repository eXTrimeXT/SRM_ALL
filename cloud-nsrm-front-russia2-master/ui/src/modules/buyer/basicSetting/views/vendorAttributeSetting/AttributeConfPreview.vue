<template>
  <el-container class="flex-container the-attr-config" direction="vertical">
    <el-main>
      <div class="attr-config-page">
        <div class="page-body">
          <srm-row>
            <srm-col :init-col="1">
              <div class="page-header">
                <el-form
                  ref="configAttrForm"
                  :model="configModle.configForm"
                  :rules="configModle.rules"
                >
                  <srm-row>
                    <srm-col :init-col="3">
                      <!-- 版本号 -->
                      <el-form-item :label="$t('dataConfMod.version')" prop="templateVersion">
                        <el-input
                          v-model="configModle.configForm.templateVersion"
                          :disabled="true"
                        />
                      </el-form-item>
                    </srm-col>
                    <srm-col :init-col="3">
                      <!-- 境内外关系 -->
                      <el-form-item
                        :label="$t('dataConfMod.overseasRelation')"
                        prop="overseasRelation"
                      >
                        <el-select
                          v-model="configModle.configForm.overseasRelation"
                          :disabled="curOpt === 'edit'"
                          @change="overseasChangeHandle"
                        >
                          <el-option
                            v-for="item in relations"
                            :key="item.id"
                            :label="item.label"
                            :value="item.value"
                          />
                        </el-select>
                      </el-form-item>
                    </srm-col>
                    <srm-col v-if="configModle.configForm.overseasRelation !== 'OUT'" :init-col="3">
                      <!-- 企业性质 -->
                      <el-form-item :label="$t('dataConfMod.companyType')" prop="companyType">
                        <el-select
                          v-model="configModle.configForm.companyType"
                          :disabled="curOpt === 'edit'"
                        >
                          <el-option
                            v-for="item in natureList"
                            :key="item.id"
                            :label="item.label"
                            :value="item.value"
                          />
                        </el-select>
                      </el-form-item>
                    </srm-col>
                  </srm-row>
                </el-form>
              </div>
              <!-- 字段选择配置表格 -->
              <div class="config-table">
                <div class="config-table-query">
                  <el-form ref="configForm" :model="dimModel" label-width="80px" :rules="rules">
                    <srm-row>
                      <srm-col :init-col="2">
                        <!-- 属性维度 -->
                        <el-form-item :label="$t('dataConfMod.attributeDim')" prop="dimCode">
                          <el-select v-model="dimModel.dimCode" @change="changeLevelType">
                            <el-option
                              v-for="item in attributeDim"
                              :key="item.id"
                              :label="item.label"
                              :value="item.value"
                            />
                          </el-select>
                        </el-form-item>
                      </srm-col>
                      <srm-col :init-col="6">
                        <el-button
                          type="primary"
                          style="margin-left: 5px;"
                          @click="searchAttributeList"
                        >
                          <!-- 查询 -->
                          {{ $t('common.search') }}
                        </el-button>
                      </srm-col>
                      <srm-col :init-col="3">
                        <!-- 维度类型 -->
                        <el-form-item :label="$t('perfMod.dimensionType')" prop="dimShowType">
                          <el-input
                            v-model="dimModel.dimShowType"
                            :disabled="true"
                          />
                        </el-form-item>
                      </srm-col>
                      <srm-col
                        v-if="dimModel.dimShowType == 'TABLE'"
                        :init-col="1"
                        style="height:120px;line-height:38px;background-color:#ede9e9;padding-left:10px;margin-bottom:5px"
                      >
                        <div>
                          {{ $t('dataConfMod.dataAttrConfig') }}
                        </div>
                        <div style="display:flex;flex-wrap:wrap;">
                          <div>
                            <div style="display:inline">
                              {{ $t('dataConfMod.fillOneLineFlag') }}
                            </div>
                            <el-switch
                              v-model="dimModel.fillOneLineFlag"
                              active-value="Y"
                              inactive-value="N"
                            />
                          </div>
                          <div style="margin-left: 25px;">
                            <div style="display:inline">
                              {{ $t('dataConfMod.tableShow') }}
                            </div>
                            <el-tooltip
                              class="item"
                              effect="dark"
                              :content="$t('dataConfMod.showDimCondition')"
                              placement="top"
                            >
                              <el-input
                                v-model="dimModel.showDimCondition"
                                class="edit-input"
                                style="width:64%;height:30px;"
                              />
                            </el-tooltip>
                          </div>
                          <div style="">
                            <div style="display:inline">
                              {{ $t('dataConfMod.relateDimCode') }}
                            </div>
                            <el-select v-model="dimModel.relateDimCode" style="width:40%;height:30px;">
                              <el-option
                                v-for="item in attributeDim"
                                :key="item.id"
                                :label="item.label"
                                :value="item.value"
                              />
                            </el-select>
                          </div>
                        </div>
                      </srm-col>
                    </srm-row>
                  </el-form>
                </div>
                <el-table
                  ref="attributeTable"
                  v-loading="listLoading"
                  :data="tableData"
                  border
                  style="width: 100%"
                  max-height="450"
                  @selection-change="selectionChange"
                >
                  <!-- 使用 -->
                  <el-table-column
                    min-width="45px"
                    :label="$t('dataConfMod.isUse')"
                    align="center"
                    prop="isUse"
                  >
                    <template slot-scope="{ row }">
                      <el-checkbox
                        v-model="row.isUse"
                        true-label="Y"
                        false-label="N"
                      />
                    </template>
                  </el-table-column>
                  <!-- 属性名称 -->
                  <el-table-column
                    min-width="100px"
                    :label="$t('dataConfMod.attributeName')"
                    align="center"
                    prop="fieldName"
                  >
                    <template slot-scope="{ row }">
                      <span>{{ row.fieldName }}</span>
                    </template>
                  </el-table-column>

                  <!-- 多语言参数 -->
                  <el-table-column
                    min-width="100px"
                    :label="$t('dataConfMod.languageCode')"
                    align="center"
                    prop="languageCode"
                  >
                    <template slot-scope="{ row }">
                      <el-tooltip
                        class="item"
                        effect="dark"
                        :content="$t('dataConfMod.languageCodeDes')"
                        placement="top"
                      >
                        <el-input v-model="row.languageCode" class="edit-input" />
                      </el-tooltip>
                    </template>
                  </el-table-column>

                  <!-- 排序 -->
                  <el-table-column
                    min-width="50px"
                    :label="$t('dataConfMod.sorting')"
                    align="center"
                    prop="fieldOrderNum"
                  >
                    <template slot-scope="{ row }">
                      <el-input v-model="row.fieldOrderNum" class="edit-input" />
                    </template>
                  </el-table-column>
                  <!-- 是否必填 -->
                  <el-table-column
                    v-if="dimModel.dimShowType == 'FORM'"
                    min-width="70px"
                    :label="$t('dataConfMod.isRequested')"
                    align="center"
                    prop="isCheck"
                  >
                    <template slot-scope="{ row }">
                      <el-checkbox v-model="row.isCheck" true-label="Y" false-label="N" />
                    </template>
                  </el-table-column>

                  <!-- 是否展示条件 -->
                  <el-table-column
                    min-width="100px"
                    label="code"
                    align="center"
                    prop="fieldCode"
                  >
                    <template slot-scope="{ row }">
                      {{ row.fieldCode }}
                    </template>
                  </el-table-column>

                  <!-- 是否展示条件 -->
                  <el-table-column
                    min-width="100px"
                    :label="$t('dataConfMod.showCondition')"
                    align="center"
                    prop="showCondition"
                  >
                    <template slot-scope="{ row }">
                      <el-tooltip
                        class="item"
                        effect="dark"
                        :content="$t('dataConfMod.showConditionDes')"
                        placement="top"
                      >
                        <el-input v-model="row.showCondition" class="edit-input" :disabled="dimModel.dimShowType != 'FORM'" />
                      </el-tooltip>
                    </template>
                  </el-table-column>

                  <!-- 不可编辑条件 -->
                  <el-table-column
                    min-width="100px"
                    :label="$t('dataConfMod.editCondition')"
                    align="center"
                    prop="editCondition"
                  >
                    <template slot-scope="{ row }">
                      <el-tooltip
                        class="item"
                        effect="dark"
                        :content="$t('dataConfMod.editConditionDes')"
                        placement="top"
                      >
                        <el-input v-model="row.editCondition" class="edit-input" />
                      </el-tooltip>
                    </template>
                  </el-table-column>

                  <!-- 更改字体颜色 -->
                  <el-table-column
                    min-width="100px"
                    :label="$t('dataConfMod.changeFontColor')"
                    align="center"
                    prop="fontColor"
                  >
                    <template slot-scope="{ row }">
                      <el-tooltip
                        class="item"
                        effect="dark"
                        :content="$t('dataConfMod.fontColorDes')"
                        placement="top"
                      >
                        <el-color-picker v-model="row.fontColor" :disabled="dimModel.dimShowType != 'FORM'" />
                      </el-tooltip>
                    </template>
                  </el-table-column>
                  <!-- 表格固定数据 -->
                  <el-table-column
                    min-width="100px"
                    :label="$t('dataConfMod.tableFixedData')"
                    align="center"
                    prop="fixedData"
                  >
                    <template slot-scope="{ row }">
                      <el-tooltip
                        class="item"
                        effect="dark"
                        :content="$t('dataConfMod.fixedDataDes')"
                        placement="top"
                      >
                        <el-input v-model="row.fixedData" :disabled="dimModel.dimShowType == 'FORM'" />
                      </el-tooltip>
                    </template>
                  </el-table-column>
                  <!-- 附件模块 -->
                  <el-table-column
                    min-width="100px"
                    :label="$t('dataConfMod.fileuploadList')"
                    align="center"
                    prop="fileuploadList"
                  >
                    <template slot-scope="{ row }">
                      <el-button icon="el-icon-upload2" @click="showFileDialog(row)">
                        {{ $t('orderMod.buyerOrderSynergy.management') }}
                      </el-button>
                    </template>
                  </el-table-column>
                </el-table>
              </div>
            </srm-col>
            <srm-col :init-col="1" style="margin-top:20px">
              <!-- 预览区域 -->
              <div class="preview-sec">
                <div class="preview-title">
                  <!-- 供应商属性预览 -->
                  <span>{{ $t('dataConfMod.attributeAttrPreview') }}</span>
                  <!-- 模板预览 -->
                  <el-button plain @click="toPreview">
                    {{ $t('dataConfMod.templatePreview') }}
                  </el-button>
                </div>
                <div class="preview-body">
                  <el-collapse v-model="activeNames">
                    <!-- 全局预览 -->
                    <div v-if="isPreviewAll">
                      <el-collapse-item
                        v-for="(conf, index) in previewConfigs"
                        v-if="conf.dimFieldConfigS.length > 0"
                        :key="index"
                        :title="conf.dimName"
                        :name="String(index)"
                      >
                        <el-form>
                          <srm-row>
                            <srm-col
                              v-for="(field, index) in conf.dimFieldConfigS"
                              :key="index"
                              :init-col="3"
                            >
                              <el-form-item
                                v-if="field.isUse === 'Y'"
                                :label="field.fieldName"
                                prop="field.fieldCode"
                                :required="field.isCheck === 'Y'"
                              >
                                <el-input v-if="field.type === 'text'" :disabled="true" />
                                <el-input
                                  v-else-if="field.fieldTypeCode === 'number'"
                                  type="number"
                                  :disabled="true"
                                />
                                <el-date-picker
                                  v-else-if="field.fieldTypeCode === 'date'"
                                  type="date"
                                  :format="$formatDatePicker"
                                  style="width: 100%;"
                                  :disabled="true"
                                />
                                <el-date-picker
                                  v-else-if="field.fieldTypeCode === 'dateTime'"
                                  type="datetime"
                                  :format="$formatDatePickerTime"
                                  style="width: 100%;"
                                  :disabled="true"
                                />
                                <el-select
                                  v-else-if="field.fieldTypeCode === 'valueSet'"
                                  :disabled="true"
                                />
                                <el-upload
                                  v-else-if="field.fieldTypeCode === 'attachment'"
                                  :disabled="true"
                                  action=""
                                >
                                  <!-- 上传文件 -->
                                  <el-button style="width:100%" type="primary">
                                    {{
                                      $t('dataConfMod.uploadFile')
                                    }}
                                  </el-button>
                                </el-upload>
                                <el-input v-else />
                              </el-form-item>
                            </srm-col>
                          </srm-row>
                        </el-form>
                      </el-collapse-item>
                    </div>
                    <!-- 局部预览 -->
                    <div v-else>
                      <el-collapse-item :title="dimObj.dimName" name="3">
                        <el-form>
                          <srm-row>
                            <srm-col v-for="(field, index) in comDimConfigs" :key="index" :init-col="3">
                              <el-form-item
                                v-if="field.isUse === 'Y'"
                                :label="field.fieldName"
                                prop="field.fieldCode"
                                :required="field.isCheck === 'Y'"
                              >
                                <el-input v-if="field.type === 'text'" :disabled="true" />
                                <el-input
                                  v-else-if="field.fieldTypeCode === 'number'"
                                  type="number"
                                  :disabled="true"
                                />
                                <el-date-picker
                                  v-else-if="field.fieldTypeCode === 'date'"
                                  type="date"
                                  :format="$formatDatePicker"
                                  style="width: 100%;"
                                  :disabled="true"
                                />
                                <el-date-picker
                                  v-else-if="field.fieldTypeCode === 'dateTime'"
                                  type="datetime"
                                  style="width: 100%;"
                                  :disabled="true"
                                />
                                <el-select
                                  v-else-if="field.fieldTypeCode === 'valueSet'"
                                  :disabled="true"
                                />
                                <el-upload
                                  v-else-if="field.fieldTypeCode === 'attachment'"
                                  :disabled="true"
                                  action=""
                                >
                                  <!-- 上传文件 -->
                                  <el-button style="width:100%" type="primary">
                                    {{
                                      $t('dataConfMod.uploadFile')
                                    }}
                                  </el-button>
                                </el-upload>
                                <el-input v-else />
                              </el-form-item>
                            </srm-col>
                          </srm-row>
                        </el-form>
                      </el-collapse-item>
                    </div>
                  </el-collapse>
                </div>
              </div>
            </srm-col>
          </srm-row>
        </div>
      </div>
      <srm-dialog
        :title="$t('dataConfMod.fileTempUpload')"
        size="middle"
        :visible.sync="dialogFormVisible"
        :close-on-click-modal="true"
      >
        <div class="btn_line">
          <el-button type="primary" class="detail-pbtn" @click="addFile">
            {{ $t("common.add") }}
          </el-button>
        </div>
        <el-table
          :data="fileuploadList"
          style="width: 100%"
          border
          max-height="200"
        >
          <el-table-column
            align="center"
            type="index"
            :label="$t('purSettlementMod.tabindex')"
            width="50"
          />
          <el-table-column
            align="center"
            prop="attachName"
            :label="$t('bidMod.fileName')"
          >
            <template slot-scope="scope">
              <SrmCommonFile
                :extra-data="fileInfo"
                :default-file="{
                  fileId: scope.row.fileuploadId,
                  fileName: scope.row.fileSourceName
                }"
                :readonly="false"
                @on-change="({file}) => HandleUploadSuccess(file,scope)"
              />
            </template>
          </el-table-column>
          <el-table-column :label="$t('components.viewConfig.seq')" width="100">
            <template slot-scope="scope">
              <el-input v-model="scope.row.comment" />
            </template>
          </el-table-column>
          <el-table-column :label="$t('common.operation')" width="100">
            <template slot-scope="scope">
              <el-button
                type="text"
                @click="deleteOneContent3(scope.$index, scope.row)"
              >
                {{ $t("common.delete") }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </srm-dialog>
      <c-toolbar>
        <template slot="right">
          <el-button type="primary" @click="previewCurDim">
            <!-- 保存 -->
            {{ $t('common.submit') }}
          </el-button>
        </template>
      </c-toolbar>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoMixin } from '@/utils/mixins'
import CToolbar from 'lib@/components/c-toolbar'
import { getDictItem } from '@/api/common'
import { adaptDictData } from '@/utils'
import { vendorAttributeSetting, vendorAttributeControl } from 'modb@/basicSetting/api/basicSetting'

export default {
  name: 'AttributeConfPreview',
  components: {
    CToolbar
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      rules: {
        overseasRelation: [
          {
            required: true,
            message: this.$t('dataConfMod.msgOverseasRelation')
          }
        ], // "请选择境内外关系"
        dimCode: [
          {
            required: true,
            message: this.$t('dataConfMod.selectDimCode')
          }
        ]
      },
      fileInfo: {
        fileModular: 'dimConfig', // 文件所属模块 -》属性配置
        fileFunction: this.$t('dataConfMod.vendorAttrSetting'), // 属性配置相关文件
        fileType: 'images' // 文件所属类型
      },
      fileCurrentRow: {},
      fileuploadList: [],
      dialogFormVisible: false,
      curRel: '',
      configModle: {
        configForm: {
          templateVersion: '',
          overseasRelation: '',
          overseasRelationName: '',
          companyType: ''
        },
        rules: {
          overseasRelation: [
            {
              required: true,
              message: this.$t('dataConfMod.msgOverseasRelation')
            }
          ], // "请选择境内外关系"
          dimCode: [
            {
              required: true,
              message: this.$t('dataConfMod.selectDimCode')
            }
          ]
        }
      },
      dimModel: {
        // 属性维度下拉
        dimCode: '',
        dimId: '',
        dimName: '',
        fillOneLineFlag: 'N',
        showDimCondition: '',
        relateDimCode: '',
        dimShowType: ''
      },
      attributeList: [],
      listLoading: true,
      isDisabled: this.$attrs.params.flag === 'edit',
      gridId: 'orgTypeList',
      pageSize: 15,
      dataCount: 0,
      queryTotal: -1,
      all: -1,
      tableData: [],
      showDialog: false,
      tableHeader: [],
      selectList: [], // 选中属性
      currentRow: '',
      activeNames: ['0', '1', '2', '3', '4', '5', '6', '7', '8'],
      previewConfigs: [], // 预览属性
      attributeDim: [], // 属性维度
      attributeDimMap: {}, // 属性维度Map
      fieldTypeList: [], // 属性类型
      relations: [], // 境内外管理
      natureList: [], // 企业性质
      templateId: '',
      firstDimCode: '',
      curOpt: 'add',
      isPreviewAll: true, // 是否全不预览
      dimObj: {
        dimName: '', // 维度名称
        dimConfigs: [] // 维度配置
      }
    }
  },
  computed: {
    comDimConfigs () {
      let arr = []
      if (this.tableData.length > 0) {
        this.tableData.map(item => {
          if (item.isUse === 'Y') {
            arr.push(item)
          }
        })
      }
      return arr
    }
  },
  created () {
    this.$nextTick(() => {
      this.fatchDictData() // 获取数据字典
      this.fatchOrgTypeList() // 获取列表数据
      this.templateId = this.$attrs.params.templateId
      this.curOpt = this.$attrs.params.flag
      if (this.$attrs.params.flag === 'edit') {
        // 编辑
        this.previewTemplate(this.templateId) // 获取模板数据
      }
    })
  },
  methods: {
    showFileDialog (row) {
      this.dialogFormVisible = true
      this.fileCurrentRow = row
      // 如果当前行的附件list有数据,则显示数据
      if (this.fileCurrentRow.fileuploadList !== null && this.fileCurrentRow.fileuploadList.length !== 0) {
        this.fileuploadList = this.fileCurrentRow.fileuploadList
      } else {
        this.fileuploadList = []
      }
    },
    deleteOneContent3 (index, row) {
      if (row.fileuploadId) {
        this.$http({
          url: '/api-file/file/fileupload/delete',
          method: 'POST',
          params: { id: row.fileuploadId },
          loading: true
        }).then(() => {
          this.fileuploadList.splice(index, 1)
          this.fileCurrentRow.fileuploadList = JSON.parse(JSON.stringify(this.fileuploadList))
        })
      }
    },
    // 上传附件成功
    HandleUploadSuccess (file, scope) {
      const { fileId = '', fileName = '' } = file || {}
      scope.row.fileuploadId = fileId.toString()
      scope.row.fileSourceName = fileName
      this.fileCurrentRow.fileuploadList = JSON.parse(JSON.stringify(this.fileuploadList))
    },
    addFile () {
      this.fileuploadList.push({
        fileuploadId: null,
        fileSourceName: '',
        fileFunction: this.$t('dataConfMod.vendorAttrSetting')
      })
    },
    // 获取数据字典
    fatchDictData () {
      // 属性维度
      vendorAttributeControl.getFieldDim({ pageNum: 1, pageSize: 500 }).then(res => {
        this.attributeDim = this.adaptDimData(res.data)
        this.firstDimCode = this.attributeDim[0].value
      })
      // 属性类型
      getDictItem('VENDOR_ATTRIBUTE_TYPE').then(res => {
        this.fieldTypeList = adaptDictData(res.data, 'dict')
      })
      // 境内外关系
      getDictItem('RELATION').then(res => {
        this.relations = adaptDictData(res.data, 'dict')
      })
      // 企业性质
      getDictItem('COMPANY_NATURE').then(res => {
        this.natureList = adaptDictData(res.data, 'dict')
      })
    },
    overseasChangeHandle (val) {
      this.curRel = val // 当前海内外关系
      this.configModle.configForm.companyType = ''
      this.configModle.configForm.companyTypeName = ''
    },
    // 适配属性维度数据
    adaptDimData (data) {
      let arr = []
      if (data && data.length > 0) {
        data.forEach(element => {
          arr.push({
            id: element.dimId,
            value: element.dimCode,
            label: element.dimName
          })
          this.attributeDimMap[element.dimCode] = element
        })
      }
      return arr
    },
    // 通过模板ID和维度获取配置信息
    fatchTemplateByDim (parmes) {
      vendorAttributeSetting.getTemplateByDimId(parmes).then(res => {
        if (res.data && res.data.length > 0) {
          let dimConfData = res.data[0]
          this.dimModel.dimCode = dimConfData.dimCode
          this.dimModel.dimId = dimConfData.dimId
          this.dimModel.dimName = dimConfData.dimName
          this.dimModel.dimConfigId = dimConfData.dimConfigId

          this.dimModel.originalDimFlag = dimConfData.originalDimFlag
          this.dimModel.fillOneLineFlag = dimConfData.fillOneLineFlag
          this.dimModel.showDimCondition = dimConfData.showDimCondition
          this.dimModel.relateDimCode = dimConfData.relateDimCode
          this.dimModel.enableFlag = dimConfData.enableFlag
          this.tableData = []
          if (dimConfData.originalDimFlag == 'Y') {
            if (dimConfData.dimCode == 'companyInfo') {
              this.dimModel.dimShowType = 'FORM'
            } else if (dimConfData.dimCode == 'bankInfo') {
              this.dimModel.dimShowType = 'TABLE'
            } else if (dimConfData.dimCode === 'linkMan') {
              this.dimModel.dimShowType = 'TABLE'
            } else if (dimConfData.dimCode === 'otherInfo') {
              this.dimModel.dimShowType = 'FORM'
            } else if (dimConfData.dimCode === 'orgInfo') {
              this.dimModel.dimShowType = 'TABLE'
            } else if (dimConfData.dimCode === 'financeInfo') {
              this.dimModel.dimShowType = 'TABLE'
            } else if (dimConfData.dimCode === 'orgCategory') {
              this.dimModel.dimShowType = 'TABLE'
            }
          } else {
            this.dimModel.dimShowType = dimConfData.dimShowType
          }
          res.data.map(item => {
            this.tableData = [...this.tableData, ...item.dimFieldConfigS]
          })

          // 如果字体为空显示黑色
          this.tableData.forEach(datas => {
            if (datas.fontColor == '') {
              datas.fontColor = '#000000'
            }
          })
        } else {
          this.$nextTick(() => {
            this.fatchFieldList(this.dimModel)
            delete this.dimModel.dimConfigId
          })
        }
      })
    },
    // 新增的时候查询查询属性列表
    fatchFieldList (parmes) {
      vendorAttributeSetting.fieldGetListPage(parmes).then(res => {
        let dimConfData = res.data.list[0]
        this.dimModel.dimCode = dimConfData.dimCode
        this.dimModel.dimId = dimConfData.dimId
        this.dimModel.dimName = dimConfData.dimName
        this.dimModel.dimConfigId = dimConfData.dimConfigId

        this.dimModel.dimShowType = this.attributeDimMap[dimConfData.dimCode].dimShowType
        this.dimModel.originalDimFlag = this.attributeDimMap[dimConfData.dimCode].originalDimFlag
        this.dimModel.fillOneLineFlag = dimConfData.fillOneLineFlag
        this.dimModel.showDimCondition = dimConfData.showDimCondition
        this.dimModel.relateDimCode = dimConfData.relateDimCode
        this.dimModel.enableFlag = dimConfData.enableFlag
        this.tableData = []
        this.tableData = res.data.list.map(i => ({ ...i, isUse: 'N' }))
      })
    },
    // 预览模板
    previewTemplate (tempId) {
      vendorAttributeSetting.getDimTemplateById({ templateId: tempId }).then(res => {
        if (res.data) {
          this.configModle.configForm.templateId = res.data.templateId
          this.configModle.configForm.templateVersion = res.data.templateVersion
          this.configModle.configForm.overseasRelation = res.data.overseasRelation
          this.configModle.configForm.companyType = res.data.companyType
          this.previewConfigs = res.data.dimConfigS // 配置的维度列表
        }
      })
    },
    // 点击预览
    toPreview () {
      this.isPreviewAll = true
      this.previewTemplate(this.templateId)
    },
    // 下拉修改属性维度
    changeLevelType (value) {
      // 设置选中的name
      this.isPreviewAll = false // 局部预览
      let selectedRow = {}
      selectedRow = this.attributeDim.find(item => {
        return item.value === value
      })
      this.dimModel.dimName = selectedRow.label
      this.dimModel.dimId = selectedRow.id

      this.searchAttributeList()
      this.dimObj.dimName = selectedRow.label // 维度名称
    },
    // 查询对应维度下的属性
    searchAttributeList () {
      this.tableData = []
      if (this.curOpt === 'edit') {
        // 编辑
        this.fatchTemplateByDim({
          templateId: this.templateId,
          dimCode: this.dimModel.dimCode
        })
      } else {
        // 新增
        this.fatchFieldList(this.dimModel)
      }
      this.$nextTick(() => {
        this.$refs.attributeTable.doLayout()
      })
    },
    // 保存当前维度属性
    previewCurDim () {
      this.$refs.configAttrForm.validate(valid => {
        if (!valid || !this.dimModel.dimCode) {
          this.$message({
            message: this.$t('common.pleasefinishRequired'), // 请输入必填项
            type: 'warning'
          })
          return false
        } else {
          this.saveTemplateData()
        }
      })
    },
    // 新增或更新模板数据
    saveTemplateData () {
      let submitData = {}
      let dimConfigS = [] // 维度列表
      let dimFieldConfigS = [] // 维度下面对应属性列表
      submitData.templateId = this.configModle.configForm.templateId
      submitData.tenantId = this.$store.getters.userId // 租户ID
      submitData.overseasRelation = this.configModle.configForm.overseasRelation // 海内外关系
      submitData.overseasRelationName = ''
      submitData.companyType = this.configModle.configForm.companyType // 企业性质
      submitData.templateVersion = this.configModle.configForm.templateVersion // 版本信息

      this.tableData.forEach(elm => {
        if (elm.fontColor == '#000000') {
          elm.fontColor = ''
        }
        dimFieldConfigS.push({
          fieldId: elm.fieldId,
          fieldOrderNum: elm.fieldOrderNum,
          isCheck: elm.isCheck,
          isUse: elm.isUse,
          fieldNickName: elm.fieldNickName,
          twoDimFlag: elm.twoDimFlag,
          dictCode: elm.dictCode,
          quickSearchColumn: elm.quickSearchColumn,
          quickSearchColumnShow: elm.quickSearchColumnShow,
          languageCode: elm.languageCode,
          editCondition: elm.editCondition,
          fontColor: elm.fontColor,
          fixedData: elm.fixedData,
          showCondition: elm.showCondition,
          fileuploadList: elm.fileuploadList
        })
      })
      dimConfigS[0] = {
        dimCode: this.dimModel.dimCode,
        dimId: this.dimModel.dimId,
        dimName: this.dimModel.dimName,
        dimConfigId: this.dimModel.dimConfigId,
        dimShowType: this.dimModel.dimShowType,
        originalDimFlag: this.dimModel.originalDimFlag,
        fillOneLineFlag: this.dimModel.fillOneLineFlag,
        showDimCondition: this.dimModel.showDimCondition,
        relateDimCode: this.dimModel.relateDimCode,
        enableFlag: this.dimModel.enableFlag,
        dimFieldConfigS: dimFieldConfigS
      }
      submitData.dimConfigS = dimConfigS // 版本配置
      vendorAttributeSetting.saveOrUpdateTemplate(submitData).then(res => {
        if (res) {
          // 查询当前维度下的配置
          this.$message({
            message: res.message,
            type: 'success'
          })
          if (this.curOpt === 'add') {
            this.curOpt = 'edit'
          }
          this.templateId = res.data.templateId
          this.previewTemplate(this.templateId) // 获取模板数据
          this.$emit('tab-remove', this.$attrs['tab-name'])
        }
      })
    },
    // 获取数据
    fatchOrgTypeList () {
      this.listLoading = false
    },
    // 选择项变化
    selectionChange (selection) {
      this.selectList = selection
      this.dimObj.dimConfigs = selection // 维度配置
    },
    handleCurrentChange (row) {
      this.currentRow = row
    },
    // 新增
    addOne () {
      this.tableData.unshift({
        add: true,
        organizationTypeName: '',
        organizationTypeCode: ''
      })
    },
    // 保存新增数据
    saveHandle () {
      let addData = this.selectList
      if (addData.length < 1) {
        this.$message({
          message: this.$t('common.cannotSave'), // "请选择需要保存的数据"
          type: 'warning'
        })
        return false
      }
    },
    // 删除
    delOne () {
      this.$refs[this.gridId].deleteFromView()
    },
    cancelEdit (row) {
      row.title = row.originalTitle
      row.edit = false
      this.$message({
        message: this.$t('common.cancelUpdate'), // "取消更新",
        type: 'warning'
      })
    },
    confirmEdit (row) {
      row.edit = false
      row.originalTitle = row.title
      this.$message({
        message: this.$t('common.successUpdate'), // "更新成功",
        type: 'success'
      })
    }
  }
}
</script>
<style scoped lang="scss">
.attr-config-page {
  padding: 10px;
  padding-bottom: 60px;
  .page-header {
  }
  .page-body {
    .preview-sec {
      .preview-title {
        margin-bottom: 20px;
        span {
          font-size: 18px;
          line-height: 30px;
          display: inline-block;
          margin-right: 20px;
        }
      }
      .preview-body {
      }
    }
  }
}
</style>
<style lang="scss">
.page-body .preview-body .el-upload {
  display: block;
}
</style>
