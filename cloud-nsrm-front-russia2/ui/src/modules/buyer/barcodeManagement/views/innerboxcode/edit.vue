<template>
  <el-container class="innerboxcodeEdit" direction="vertical">
    <el-main>
      <div class="form-container">
        <el-collapse v-model="activeDims" class="tab-form-style">
          <el-form ref="form" :disabled="isReadOnly" :model="form" :rules="rules">
            <!-- 条码基本信息 -->
            <!-- 内箱条码基本信息 -->
            <el-collapse-item :title="$t('orderMod.innerBoxBarcodeBasicInformation')" name="1">
              <srm-row :gutter="32">
                <srm-col :span="6">
                  <!-- 内箱条码编号 -->
                  <el-form-item prop="innerBoxCode" :label="$t('orderMod.innerBoxCode')">
                    <el-input v-model="form.innerBoxCode" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col :span="6">
                  <!-- 供应商名称 -->
                  <el-form-item prop="vendorName" :label="$t('common.companyName')">
                    <QuickSearch
                      :disabled="isVendorName"
                      :showInput="form.vendorName"
                      show-key="vendorName"
                      :scope-data="form"
                      name="scc_sup_company_info_all"
                      @close-quicksearch="getCompanyByQuick"
                    />
                  </el-form-item>
                </srm-col>

                <srm-col :span="6">
                  <!-- 物料名称 -->
                  <el-form-item prop="materialName" :label="$t('common.materialName')">
                    <QuickSearch
                      :disabled="editMode"
                      :showInput="form.materialName"
                      show-key="materialName"
                      :scope-data="form"
                      name="scc_base_material_item"
                      @close-quicksearch="getMaterialByQuick"
                    />
                  </el-form-item>
                </srm-col>

                <srm-col :span="6">
                  <!-- 品类 -->
                  <el-form-item prop="categoryName" :label="$t('common.category')">
                    <QuickSearch
                      disabled
                      :showInput="form.categoryName"
                      show-key="categoryName"
                      :scope-data="form"
                      name="scc_base_purchase_category"
                      @close-quicksearch="getCategoryByQuick"
                    />
                  </el-form-item>
                </srm-col>

                <!-- <srm-col :span="6">
                  <el-form-item prop="materialNorms" label="规格型号">
                    <el-input v-model="form.materialNorms" disabled />
                  </el-form-item>
                </srm-col> -->

                <srm-col :span="6">
                  <!-- 生产日期 -->
                  <el-form-item prop="productionDate" :label="$t('orderMod.productionDate')">
                    <el-date-picker
                      v-model="form.productionDate"
                      :format="$formatDatePicker"
                      value-format="yyyy-MM-dd"
                      :picker-options="pickerOptions"
                      @change="selectHandler3"
                    />
                  </el-form-item>
                </srm-col>

                <!-- <srm-col :span="6">
                  <el-form-item prop="productionWeek" label="生产周">
                    <el-input v-model="form.productionWeek" disabled />
                  </el-form-item>
                </srm-col>

                <srm-col :span="6">
                  <el-form-item prop="batchNo" label="批号">
                    <el-input v-model="form.batchNo" disabled />
                  </el-form-item>
                </srm-col> -->

                <srm-col v-if="!innShow&&!editMode" :span="6">
                  <!-- 待装箱数量 -->
                  <el-form-item prop="currentLotQuantity" :label="$t('hierarchical.Quantity')">
                    <el-input v-model="form.currentLotQuantity" />
                  </el-form-item>
                </srm-col>
                <srm-col v-if="!innShow&&editMode" :span="6">
                  <!-- 数量（重量/净重） -->
                  <el-form-item prop="currentBoxQuantity" label="$t('hierarchical.Quantity(weight/netweight)')">
                    <el-input v-model="form.currentBoxQuantity" />
                  </el-form-item>
                </srm-col>
                <srm-col v-if="'B03'===form.categoryCode">
                  <el-form-item
                    :label="$t('orderMod.buyerOrderSynergy.comments')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="form.comments"
                      type="textarea"
                      :autosize="{ minRows: 2, maxRows: 4 }"
                      maxlength="60"
                      show-word-limit
                      @input="onInputBlur($event.target)"
                    />
                  </el-form-item>
                </srm-col>
              </srm-row>
            </el-collapse-item>
          </el-form>
          <!-- 条码扩展信息 -->
          <!-- 內箱条码扩展信息 -->
          <el-collapse-item v-show="expandFields.length > 0" :title="$t('orderMod.innerBoxBarcodeExtensionInformation')" name="2">
            <el-form ref="formExpand" :disabled="isReadOnly" :model="formExpand" :rules="formExpandrules">
              <srm-row :gutter="32">
                <!-- 遍历扩展字段 -->
                <srm-col v-for="(expandField, i) in expandFields" :key="i" :span="6">
                  <!-- 设置拓展字段基本信息 -->
                  <el-form-item
                    v-if="
                      expandField.fieldCode !=
                        'RULE_SPECIFICATION_MODEL' &&
                        expandField.fieldCode != 'RULE_QUANTITY'
                    "
                    :prop="expandField.fieldCode"
                    :label="expandField.fieldName"
                  >
                    <!-- 输入框类型修改为 文本-->
                    <el-input
                      v-if="expandField.fieldTypeCode == 'text'"
                      v-model="formExpand[expandField.fieldCode]"
                      clearable
                    />
                    <!-- 输入框搜索类型为 日期 -->
                    <el-date-picker
                      v-if="expandField.fieldTypeCode == 'date'"
                      v-model="formExpand[expandField.fieldCode]"
                      value-format="yyyy-MM-dd"
                      :format="$formatDatePicker"
                      clearable
                    />
                    <!-- 输入框搜索类型为 时间 -->
                    <el-date-picker
                      v-if="expandField.fieldTypeCode == 'dateTime'"
                      v-model="formExpand[expandField.fieldCode]"
                      type="datetime"
                      :format="$formatDatePickerTime"
                      value-format="yyyy-MM-dd HH:mm:ss"
                      clearable
                    />
                    <!-- 输入框搜索类型为 数字 -->
                    <el-input
                      v-if="expandField.fieldTypeCode == 'number'"
                      v-model="formExpand[expandField.fieldCode]"
                      v-input-format="{ type: 'float' }"
                      clearable
                    />
                  </el-form-item>
                </srm-col>
              </srm-row>
            </el-form>
          </el-collapse-item>

          <!-- 生成条码信息 -->
          <el-collapse-item v-show="!editMode" :title="$t('orderMod.generateBarcodeInformation')" name="3">
            <el-table
              ref="form2"
              class="tableBoxCode"
              :data="echoInnerBoxCode"
              style="width: 100%"
              border
              :row-style="{height:58+'px'}"
              :cell-style="{padding:0+'px'}"
            >
            <!-- 内箱条码编号 -->
              <el-table-column
                align="center"
                prop="innerBoxCode"
                :label="$t('orderMod.innerBoxCode')"
                :show-overflow-tooltip="true"
              />
              <!-- 内箱条码ID -->
              <el-table-column
                v-if="innShow"
                align="center"
                prop="innerBoxId"
                :label="$t('hierarchical.Innerbox')"
                :show-overflow-tooltip="true"
              />
              <!-- 数量 -->
              <el-table-column
                v-if="innShow"
                align="center"
                prop="currentBoxQuantity"
                :label="$t('bid_mod.quantity')"
                :show-overflow-tooltip="true"
                min-width="50"
              >
                <template #header>
                  <i class="required">*</i>
                  <span>
                    <!-- 数量 -->
                     {{ $t("bid_mod.quantity") }} 
                  </span>
                </template>
                <template slot-scope="scope">
                  <el-input v-model="scope.row.currentBoxQuantity" v-input-format="{ type: 'float' }" />
                </template>
              </el-table-column>
              <el-table-column :label="$t('components.headers.operation')" width="200" fixed="right">
                <template slot-scope="scope">
                  <el-button type="text" @click="printBarCode(scope.$index, scope.row)">
                    <!-- 打印标签 -->
                    {{ $t("orderMod.printLabels") }}
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
            <el-button
              v-if="innShow && echoInnerBoxCode.length > 0"
              type="primary"
              style="margin-top: 20px; margin-left: 85%"
              @click="updateInnList"
            >
              <!-- 全部条码保存 -->
              {{ $t("cusEntry.supplement20250211.allBarcodesSaved") }}
            </el-button>
          </el-collapse-item>
        </el-collapse>
      </div>
      <CToolbar>
        <template #right>
          <el-button @click="cancelBill">
            <!-- 取消 -->
            {{ $t("components.common.cancel") }}
          </el-button>
          <el-button v-show="$attrs.params.flag === 'add'" type="primary" @click="save">
            <!-- 保存 -->
            {{ $t("common.save") }}
          </el-button>
          <el-button
            v-show="!editMode"
            type="primary"
            :disabled="viewMoreCodeDisabled"
            @click="printBarCodeMore"
          >
            <!-- 批量打印 -->
            {{ $t("orderMod.batchPrinting") }}
          </el-button>
          <el-button v-show="editMode" type="primary" @click="printBarCodeEdit">
            <!-- 打印 -->
            {{ $t("route.pdfPrint") }}
          </el-button>
        </template>
      </CToolbar>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoMixin } from '@/utils/mixins'
import MainHeader from 'lib@/components/Table/MainHeader'
import CToolbar from 'lib@/components/c-toolbar'
import CUploadFile from '@/library/components/c-upload-file'
import CDownloadLink from 'lib@/components/c-download-link'
import QuickSearch from 'lib@/components/QuickSearch' // 快速查询组件
import {
  getDictItem
} from '@/api/common'
import { adaptDictData, isNull, getWeekNum } from '@/utils'
import { boxtagconfigApi, innerBoxCodeApi } from 'modb@/barcodeManagement/api'

export default {
  name: 'InnerboxcodeEdit',
  components: {
    MainHeader,
    CToolbar,
    CDownloadLink,
    CUploadFile,
    QuickSearch
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      pickerOptions: {
        disabledDate (time) {
          return time.getTime() > Date.now()
        }
      },
      activeDims: ['1', '2', '3'],
      // 文件上传配置信息
      fileInfo: {
        fileModular: 'workFlow', // 文件所属模块 -》审批流程
        fileFunction: 'workflowReport', // 审批流相关文件
        fileType: 'images' // 文件所属类型
      },
      form: {
        innerBoxCode: null,
        vendorName: null,
        vendorId: null,
        vendorCode: null,
        materialId: null,
        materialCode: null,
        materialName: null,
        productionDate: null,
        categoryId: null,
        categoryCode: null,
        categoryName: null,
        packingQuantity: null,
        unitCode: null,
        unit: null,
        createdBy: null,
        creationDate: null,
        lastUpdatedBy: null,
        innerBoxExtendList: null,
        currentLotQuantity: null,
        materialNorms: null,
        productionWeek: 0
      },
      // 表单扩展字段
      formExpand: {},
      rules: {
        categoryName: [
          { required: true, message: this.$t('hierarchical.information') }  // '请选择品类信息'
        ],
        materialName: [
          { required: true, message: this.$t('hierarchical.ItemInformation') }  // '请选择物料信息'
        ],
        vendorName: [
          { required: true, message: this.$t('hierarchical.SupplierInformation') }  // '请选择供应商信息'
        ],
        // batchNo: [
        //   { required: true, message: "请填写批号信息" }
        // ],
        productionDate: [
          { required: true, message: this.$t('hierarchical.productiondate') } //  '请选择生产日期' 
        ]
        // currentLotQuantity:[
        //   { required: true, message: "请填写待装箱数量" }
        // ],
      },
      formExpandrules: {},
      // 拓展字段
      expandFields: [],
      // 回显内箱条码
      echoInnerBoxCode: [],
      // 当前批生成流水号
      generateSerial: '',
      viewMoreCodeDisabled: true,
      // 编辑模式
      editMode: false,
      // 当前打印模板
      currentPrintTemplateName: '',
      // 打印模板列表
      printTemplateList: [],

      isVendorName: false,
      innShow: false,
      // 物料没有最小包装量列表(内箱箱)
      minimumList: [],
      // 扩展字段非空提示
      warnShow: { required: true, message: this.$t('common.pleaseInput') },
      innData: {
        innerBoxExtendList: [],
        innerBoxCodeList: []
      }
    }
  },
  computed: {
    isReadOnly () {
      return this.$attrs.params.flag === 'view'
    }
  },
  watch: {
    innShow: {
      handler () {
        if (this.innShow) {
          this.$set(this.rules, 'currentLotQuantity', [])
        } else {
          this.$set(this.rules, 'currentLotQuantity', [{ required: true, message: this.$t('hierarchical.quantitylynumber') }]) // '请填写待装箱数量'
        }
      },
      deep: true,
      immediate: true
    }
  },
  created () {
    // 内箱条码打印模板
    getDictItem('INNER_BOX_PRINT_TEMPLATE').then(res => {
      this.printTemplateList = adaptDictData(res.data, 'dict')
      console.log(this.printTemplateList)
    })
    // 物料没有最小包装量列表（内箱）
    getDictItem('MINIMUM2').then((res) => {
      this.minimumList = adaptDictData(res.data, 'dict')
    })

    // 默认加载采购商联系方式，如果没有才需要填写
    if (this.$store.state.user && this.$store.state.user.userInfo) {
      this.form.vendorId = this.$store.state.user.userInfo.companyId
      this.form.vendorCode = this.$store.state.user.userInfo.companyCode
      this.form.vendorName = this.$store.state.user.userInfo.companyName
    }
    if (!this.form.vendorId) {
      this.isVendorName = false
    } else {
      this.isVendorName = true
    }
  },
  mounted () {
    const { flag, row } = this.$attrs.params
    if (flag !== 'add') {
      this.editMode = true
      this.getDetail()
      this.$refs.form2.doLayout()
      // const printTemplate = this.printTemplateList.find(t=>t.value===this.form.categoryCode);
      // this.currentPrintTemplateName =  "database:"+"内箱条码-五金件标签-zs.ureport.xml";//"database:"+printTemplate.label;
    }
  },
  methods: {
    /**
     * 回显内箱条码行
     */
    echoInnerBoxCodeLine (res) {
      this.echoInnerBoxCode = res.data
      if (!this.editMode) {
        this.$refs.form2.doLayout()
      }
    },
    /**
     * 根據快查获取供应商信息
     */
    getCompanyByQuick (val, scope) {
      scope.vendorId = val ? val.companyId : ''
      scope.vendorName = val ? val.companyName : ''
      scope.vendorCode = val ? val.companyCode : ''
    },
    /**
     * 根据快查获取物料信息
     */
    getMaterialByQuick (val, scope) {
      scope.materialId = val ? val.materialId : ''
      scope.materialCode = val ? val.materialCode : ''
      scope.materialName = val ? val.materialName : ''
      // this.getByMaterialId(scope.materialId)
      this.getCategoryCodeByMaterialId(scope.materialId)
    },
    /**
     * 根据快查获取品类信息
     */
    getCategoryByQuick (val, scope) {
      scope.categoryId = val ? val.categoryId : ''
      scope.categoryCode = val ? val.categoryCode : ''
      scope.categoryName = val ? val.categoryName : ''

      // 根据品类获取扩展标签
      boxtagconfigApi.getByCategoryIdAndBusinessBoxType({ categoryId: val.categoryId, businessBoxType: 'INNER_BOX' })
        .then((res) => {
          this.expandFields = res.data

          // 回显字段
          for (let field of this.expandFields) {
            this.$set(this.formExpand, field.fieldCode, '')
            this.$set(
              this.formExpand,
              field.fieldCode + '_@type',
              field.fieldTypeCode
            )
            this.$set(
              this.formExpand,
              field.fieldCode + '_@name',
              field.fieldName
            )
            if (field.isMust == 'Y') {
              this.$set(this.rules, field.fieldCode, this.warnShow)
            }
          }
        })

      // 根据品类编码匹配对应打印模板
      // const printTemplate = this.printTemplateList.find(t=>t.value===val.categoryCode);
      // this.currentPrintTemplateName =  "database:"+"内箱条码-五金件标签-zs.ureport.xml";//"database:"+printTemplate.label;
    },
    /**
     * 获取表单扩展字段
     */
    getFormExpand (val) {
      if (val in this.formExpand) {
        return val
      }
    },
    /**
     * 获取打印模板
     */
    getCurrentPrintTemplateName () {
      const _this = this
      // const printTemplate = this.printTemplateList.filter(function (x) {
      //   return x.value.indexOf(_this.form.categoryCode) != -1
      // })
      this.currentPrintTemplateName = 'database:' + this.printTemplateList[0].label
      // this.currentPrintTemplateName = 'database:' + printTemplate[0].label
      // this.currentPrintTemplateName =  "database:"+"内箱条码-触点类.ureport.xml";
      // this.currentPrintTemplateName =  "database:"+"内箱条码-磁铁类.ureport.xml";
      // this.currentPrintTemplateName =  "database:"+"内箱条码-铁芯类.ureport.xml";
      // this.currentPrintTemplateName =  "database:"+"内箱条码-胶水类.ureport.xml";
      // this.currentPrintTemplateName =  "database:"+"内箱条码-CP线.ureport.xml";
      // this.currentPrintTemplateName =  "database:"+"内箱条码-五金件类.ureport.xml";
      // this.currentPrintTemplateName =  "database:"+"内箱条码-漆包线类.ureport.xml";
      // this.currentPrintTemplateName =  "database:"+"内箱条码-客供品.ureport.xml";
      // this.currentPrintTemplateName =  "database:"+"内箱条码-其他.ureport.xml";
      // this.currentPrintTemplateName =  "database:"+"内箱条码-卷料.ureport.xml";
      //  this.currentPrintTemplateName = "database:"+"内箱条码-电镀材料.ureport.xml";
      //  this.currentPrintTemplateName = "database:"+"内箱条码-银丝-铜杆.ureport.xml";
      // this.currentPrintTemplateName = "database:"+"内箱条码-触点件委外.ureport.xml";
      console.log(this.printTemplateList, 'this.printTemplateList')
      console.log(this.currentPrintTemplateName, this.currentPrintTemplateName)
    },
    /**
     * 批量打印标签
     */
    printBarCodeMore () {
      // 获取打印模板
      this.getCurrentPrintTemplateName()

      // this.countPrint('', this.generateSerial)
      this.openPrint(
        this.currentPrintTemplateName,
        encodeURIComponent(`generateSerial=${this.generateSerial}`)
      )
    },
    /**
     * 编辑模式下打印
     */
    printBarCodeEdit () {
      // 获取打印模板
      this.getCurrentPrintTemplateName()

      // this.countPrint(this.form.innerBoxId, '')
      this.openPrint(
        this.currentPrintTemplateName,
        encodeURIComponent(`innerBoxCode=${this.form.innerBoxCode}`)
      )
    },

    /**
     * 打印标签
     */
    printBarCode (index, row) {
      // 获取打印模板
      this.getCurrentPrintTemplateName()

      // this.countPrint(row.innerBoxId, '')
      this.openPrint(
        this.currentPrintTemplateName,
        encodeURIComponent(`innerBoxCode=${row.innerBoxCode}`)
      )
    },
    /**
     * 累计打印次数
     */
    countPrint (innerBoxId, generateSerial) {
      this.$http({
        url: '/api-base/base/innerboxcode/countPrint',
        method: 'GET',
        params: { 'innerBoxId': innerBoxId, 'generateSerial': generateSerial },
        loading: true
      }).then(data => {
      }).catch(err => {
        console.log(err)
      })
    },
    /**
     * 打开pdf
     */
    openPrint (pdfName, params) {
      const xml = encodeURIComponent(pdfName)
      const url = `${this.$systemUrl}/#/pdfPrint?isBarcode=Y&xml=${xml}&params=${params}`
      window.open(url, '_blank', 'noopener,noreferrer')
    },
    // form验证返回promise校验返回trun or false
    formValidate (formRef) {
      return new Promise((resolve) => {
        this.$refs[formRef].validate((flag, obj) => {
          resolve({ flag, obj })
        })
      })
    },
    /*
    * @Description: 校验表单表格必填项
    * @return: {
    *   flag: true/false,  校验是否通过
    *   message: 返回填写信息
    * }
    */
    async getCheckForm () {
      const formFiled = await this.formValidate('form')
      const formFiled1 = await this.formValidate('formExpand')

      if (!formFiled.flag && Object.keys(formFiled.obj).length > 0) {
        const warnObj = Object.keys(formFiled.obj)[0]
        return {
          flag: formFiled.flag,
          message: formFiled.obj[warnObj][0].message
        }
      }

      if (!formFiled1.flag && Object.keys(formFiled1.obj).length > 0) {
        const warnObj = Object.keys(formFiled.obj)[0]
        return {
          flag: formFiled1.flag,
          message: formFiled1.obj[warnObj][0].message
        }
      }

      return { flag: true }
    },
    /**
     * 保存、更新
     */
    async save () {
      const obj = await this.getCheckForm()
      if (obj.flag) {
        const { flag } = this.$attrs.params
        // 新增时不用提交主键值
        const { innerBoxId, ...rest } = this.form
        console.log(this.form)
        // 转换字段类型
        let innerBoxExtendList = []
        let extendFieldTemp = {}
        let fieldCount = 0 // 字段计数，三个字段一个对象
        for (let field in this.formExpand) {
          if (field.indexOf('_@type') > 0) {
            extendFieldTemp.fieldType = this.formExpand[field] // 扩展字段类型
            fieldCount++
          } else if (field.indexOf('_@name') > 0) {
            extendFieldTemp.fieldDesc = this.formExpand[field] // 扩展字段描述
            fieldCount++
          } else {
            extendFieldTemp.fieldVal = this.formExpand[field] // 扩展字段编码
            extendFieldTemp.fieldCode = field // 扩展字段内容
            fieldCount++
          }

          // 填充完成，清空临时变量
          if (fieldCount == 3) {
            let extendFieldRecord = extendFieldTemp
            innerBoxExtendList.push(extendFieldRecord)
            extendFieldTemp = {}
            fieldCount = 0
          }
        }
        rest.innerBoxExtendList = innerBoxExtendList
        if (flag === 'add') {
          innerBoxCodeApi.add(rest).then((res) => {
            this.$message({
              type: 'success',
              message: res.message
            })
            console.log(res)
            this.echoInnerBoxCodeLine(res)
            if (res.data && res.data.length > 0) {
              this.generateSerial = res.data[0].generateSerial || ''
            }
            this.viewMoreCodeDisabled = false
          })
        } else {
          rest.innerBoxId = this.form.innerBoxId
          // 更新
          console.log(rest)
          innerBoxCodeApi.update(rest).then((res) => {
            this.$message({
              type: 'success',
              message: res.message
            })
            if (res.data && res.data.length > 0) {
              this.generateSerial = res.data[0].generateSerial || ''
            }
            this.getDetail()
            this.viewMoreCodeDisabled = false
          })
        }
      } else {
        this.__focus_error__(obj.message)
      }
    },
    /**
     * 获取详情（弃用）
     */
    getDetail () {
      innerBoxCodeApi.getById(this.$attrs.params.row.innerBoxId)
        .then((res) => {
          const { innerBoxExtendList, ...rest } = res.data
          this.form = rest
          console.log(innerBoxExtendList)
          // 清空拓展字段
          this.expandFields = []
          // 回显字段
          for (let field of innerBoxExtendList) {
            // 保存用的
            this.$set(this.formExpand, field.fieldCode, field.fieldVal)
            this.$set(
              this.formExpand,
              field.fieldCode + '_@type',
              field.fieldType
            )
            this.$set(
              this.formExpand,
              field.fieldCode + '_@name',
              field.fieldDesc
            )

            // 页面显示用的
            let fieldRecord = {}
            fieldRecord.fieldCode = field.fieldCode
            fieldRecord.fieldName = field.fieldDesc
            fieldRecord.fieldTypeCode = field.fieldType
            fieldRecord.isMust = 'Y'
            this.expandFields.push(fieldRecord)
          }
        })
    },

    cancelBill () {
      this.$emit('tab-remove', this.$attrs.tabName)
      this.__setTabTodo('innerboxcodeList.getQuerydata')
    },
    selectHandler3 (val) {
      this.form.productionWeek = val ? getWeekNum(val) : ''
    },
    // 根据物料id得到物料规格
    getByMaterialId (materialId) {
      this.form.materialNorms = null
      this.$http({
        url: '/api-base/material/materialItem/getByMaterialId',
        method: 'GET',
        params: { materialId: materialId },
        loading: true
      })
        .then((data) => {
          this.form.materialNorms = data.data.specification
        })
        .catch((err) => {
          console.log(err)
        })
    },
    // 根据物料id得到所属物料品类
    getCategoryCodeByMaterialId (materialId) {
      // 清空拓展字段
      this.expandFields = []
      this.form.categoryCode = null
      this.form.categoryName = null
      this.form.categoryId = null
      this.$http({
        url: '/api-base/material/materialItem/get',
        method: 'GET',
        params: { id: materialId },
        loading: true
      })
        .then((data) => {
          console.log(data.data)
          this.form.categoryCode = data.data.categoryCode
          this.form.categoryName = data.data.categoryName
          this.form.categoryId = data.data.categoryId
          if (isNull(data.data.categoryCode)) {
            this.$message({
              type: 'warning',
              message: '该物料没有绑定品类编号！'
            })
          } else {
            let val = this.minimumList.filter(function (x) {
              return x.value === data.data.categoryCode
            })
            console.log(this.minimumList)
            console.log(val)
            if (isNull(val[0])) {
              this.innShow = false
            } else {
              this.innShow = true
            }
            // this.innShow = true;
            // 根据品类获取扩展标签
            boxtagconfigApi.getByCategoryIdAndBusinessBoxType({ categoryId: data.data.categoryId, businessBoxType: 'INNER_BOX' })
              .then((res) => {
                this.expandFields = res.data

                // 回显字段
                console.log(this.expandFields)
                for (let field of this.expandFields) {
                  this.$set(this.formExpand, field.fieldCode, '')
                  this.$set(
                    this.formExpand,
                    field.fieldCode + '_@type',
                    field.fieldTypeCode
                  )
                  this.$set(
                    this.formExpand,
                    field.fieldCode + '_@name',
                    field.fieldName
                  )
                  if (field.isMust == 'Y') {
                    this.$set(this.formExpandrules, field.fieldCode, this.warnShow)
                  }
                }
              })
          }
        })
        .catch(_ => {
          this.$message({
            type: 'warning',
            message: this.$t('cusEntry.supplement20250211.systemNotMaintainMaterialInfo')  // '系统没有维护此物料信息，请管理员查看！'
          })
        })
    },
    updateInnList () {
      // console.log(this.echoInnerBoxCode)
      this.innData.innerBoxExtendList = []
      this.innData.innerBoxCodeList = []
      // 转换字段类型
      let innerBoxExtendList = []
      let extendFieldTemp = {}
      let fieldCount = 0 // 字段计数，三个字段一个对象
      for (let field in this.formExpand) {
        if (field.indexOf('_@type') > 0) {
          extendFieldTemp.fieldType = this.formExpand[field] // 扩展字段类型
          fieldCount++
        } else if (field.indexOf('_@name') > 0) {
          extendFieldTemp.fieldDesc = this.formExpand[field] // 扩展字段描述
          fieldCount++
        } else {
          extendFieldTemp.fieldVal = this.formExpand[field] // 扩展字段编码
          extendFieldTemp.fieldCode = field // 扩展字段内容
          fieldCount++
        }

        // 填充完成，清空临时变量
        if (fieldCount == 3) {
          let extendFieldRecord = extendFieldTemp
          innerBoxExtendList.push(extendFieldRecord)
          extendFieldTemp = {}
          fieldCount = 0
        }
      }
      this.innData.innerBoxExtendList = innerBoxExtendList
      this.innData.innerBoxCodeList = this.echoInnerBoxCode

      innerBoxCodeApi.updateInnerBoxCodeList(this.innData)
        .then((res) => {
          this.$message({
            type: 'success',
            message: res.message
          })
          this.echoInnerBoxCodeLine(res)
          if (res.data && res.data.length > 0) {
            this.generateSerial = res.data[0].generateSerial || ''
          }

          // 写入id
          // this.innerBoxData.forEach((item) => {
          //   item.innerBoxId = res.data.find(
          //     (i) => i.innerboxcode == item.innerboxcode
          //   ).innerBoxId;
          // });
          this.viewMoreCodeDisabled = false
        })
    },
    onInputBlur () {
      if (this.form.comments.length >= 60) {
        this.$message({
          type: 'warning',
          message: this.$t('cusEntry.supplement20250211.currentCharLengthExceedsLimit')
        })
      }
    }
  }
}
</script>
<style scoped lang="scss">
.innerboxcodeEdit {
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

  tableBoxCode th.gutter {
    display: table-cell !important;
  }

  tableBoxCode colgroup.gutter {
    display: table-cell !important;
  }
}

.required {
  color: #ff4949;
  padding-right: 2px;
}
</style>
