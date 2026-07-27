<template>
  <el-container class="outerboxcodeEdit" direction="vertical">
    <el-main>
      <div class="form-container">
        <el-collapse v-model="activeDims" class="tab-form-style">
          <!-- 条码基本信息 -->
          <el-form ref="form" :model="form" :rules="rules">
            <el-collapse-item title="外箱条码基本信息" name="1">
              <srm-row :gutter="32">
                <srm-col>
                  <el-form-item prop="outerBoxCode" label="外箱编码">
                    <el-input v-model="form.outerBoxCode" disabled />
                  </el-form-item>
                </srm-col>

                <srm-col>
                  <el-form-item prop="vendorName" label="供应商名称">
                    <QuickSearch
                      :disabled="isVendorName"
                      :showInput="form.vendorName"
                      show-key="vendorName"
                      :scope-data="form"
                      name="scc_sup_company_info5"
                      @close-quicksearch="getCompanyByQuick"
                    />
                  </el-form-item>
                </srm-col>

                <srm-col>
                  <el-form-item prop="materialName" label="物料名称">
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

                <srm-col>
                  <el-form-item prop="categoryName" label="品类">
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

                <srm-col>
                  <el-form-item prop="productionDate" label="生产日期">
                    <el-date-picker
                      v-model="form.productionDate"
                      value-format="yyyy-MM-dd"
                      :picker-options="pickerOptions"
                      @change="selectHandler3"
                    />
                  </el-form-item>
                </srm-col>

                <srm-col v-show="!outShow&&!editMode">
                  <el-form-item prop="currentLotQuantity" label="待装箱总数量">
                    <el-input v-model="form.currentLotQuantity" />
                  </el-form-item>
                </srm-col>
                <srm-col v-show="'B03'===form.categoryCode">
                  <el-form-item
                    :label="$t('orderMod.buyerOrderSynergy.comments')"
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
          <el-collapse-item v-show="expandFields.length > 0" title="外箱条码扩展信息" name="2">
            <el-form ref="formExpand" :model="formExpand" :rules="formExpandrules">
              <srm-row :gutter="32">
                <!-- 遍历扩展字段 -->
                <srm-col v-for="(expandField, i) in expandFields" :key="i">
                  <!-- 设置拓展字段基本信息 -->
                  <el-form-item
                    v-if="
                      expandField.fieldCode != 'RULE_SPECIFICATION_MODEL' &&
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
                      clearable
                    />
                    <!-- 输入框搜索类型为 时间 -->
                    <el-date-picker
                      v-if="expandField.fieldTypeCode == 'dateTime'"
                      v-model="formExpand[expandField.fieldCode]"
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
          <el-collapse-item v-show="!editMode" title="生成条码信息" name="3">
            <el-table
              :data="echoOuterBoxCode"
              style="width: 100%"
              border
              :row-style="{height:58+'px'}"
              :cell-style="{padding:0+'px'}"
            >
              <el-table-column
                align="center"
                prop="outerBoxCode"
                label="外箱条码编号"
                :show-overflow-tooltip="true"
              />
              <el-table-column
                v-if="outShow"
                align="center"
                prop="outerBoxId"
                label="外箱条码ID"
                :show-overflow-tooltip="true"
              />
              <!-- <el-table-column
                  align="center"
                  prop="currentBoxQuantity"
                  label="数量"
                  :show-overflow-tooltip="true"
                  min-width="50"
                  v-if="outShow"
                >
                  <template #header>
                    <i class="required">*</i>
                    <span>{{ "数量" }}</span>
                  </template>
                  <template slot-scope="scope">
                    <el-input
                      v-model="scope.row.currentBoxQuantity"
                      v-input-format="{ type: 'float' }"
                    />
                  </template>
                </el-table-column> -->
              <el-table-column label="操作" width="200" fixed="right">
                <template slot-scope="scope">
                  <el-button type="text" @click="printBarCode(scope.$index, scope.row)">
                    打印标签
                  </el-button>
                </template>
              </el-table-column>
            </el-table>

            <!-- <el-button
                type="primary"
                v-if="outShow && echoOuterBoxCode.length > 0"
                @click="updateOutList"
                style="margin-top: 20px; margin-left: 85%"
                >全部条码保存</el-button
              > -->
          </el-collapse-item>
        </el-collapse>
      </div>

      <CToolbar>
        <template #right>
          <el-button @click="cancelBill">
            取消
          </el-button>
          <el-button type="primary" :disabled="readOnly" @click="save('1')">
            保存
          </el-button>
          <!-- <el-button
            v-show="!outShow&&!editMode"
            type="primary"
            :disabled="readOnly"

            @click="save('2')"
          >
            自动生成内外箱绑定
          </el-button> -->
          <el-button
            v-show="!editMode"
            :disabled="viewMoreCodeDisabled"
            type="primary"
            @click="printBarCodeMore"
          >
            批量打印
          </el-button>
          <el-button v-show="editMode" type="primary" @click="printBarCodeEdit">
            打印
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
import BaseTable from 'lib@/components/BaseTable/baseTable'
import MImport from 'lib@/components/import'
import { downloadFileLink } from 'lib@/utils/file'
import axios from 'axios'
import { getToken } from '@/utils/auth'
import {
  getDictItem
} from '@/api/common'
import { adaptDictData, isNull, getWeekNum } from '@/utils'
import QuickSearch from 'lib@/components/QuickSearch' // 快速查询组件
import { outerBoxCodeApi, boxtagconfigApi } from 'modb@/barcodeManagement/api'

export default {
  name: 'OuterboxcodeEdit',
  components: {
    MainHeader,
    CToolbar,
    BaseTable,
    MImport,
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
      realDataSource: [],
      dataSource: [],
      activeLine: ['1'],
      columns: [
        {
          attrs: {
            prop: 'outerBoxId',
            label: '外箱id'
          },
          slot: 'outerBoxId'
        },
        {
          attrs: {
            prop: 'fieldType',
            label: '扩展字段类型'
          },
          slot: 'fieldType'
        },
        {
          attrs: {
            prop: 'fieldCode',
            label: '扩展字段编码（dto属性字段）'
          },
          slot: 'fieldCode'
        },
        {
          attrs: {
            prop: 'fieldDesc',
            label: '扩展字段描述'
          },
          slot: 'fieldDesc'
        },
        {
          attrs: {
            prop: 'fieldVal',
            label: '扩展字段数值'
          },
          slot: 'fieldVal'
        },
        {
          attrs: {
            prop: 'createdBy',
            label: '创建人名称'
          },
          slot: 'createdBy'
        },
        {
          attrs: {
            prop: 'creationDate',
            label: '创建日期'
          },
          slot: 'creationDate'
        },
        {
          attrs: {
            prop: 'lastUpdatedBy',
            label: '更新人'
          },
          slot: 'lastUpdatedBy'
        },
        {
          attrs: {
            prop: 'operation',
            label: '操作',
            width: 150,
            fixed: 'right'
          },
          operations: [
            {
              event: 'deleteItem',
              name: this.$t('common.delete'),
              func: this.deleteItem
            }
          ]
        }
      ],
      extraData: {
        sourceType: 'WEB_APP',
        uploadType: 'FASTDFS',
        fileModular: 'base',
        fileFunction: 'quotalinetest',
        fileType: 'excel'
      },
      form: {
        outerBoxId: null,
        outerBoxCode: null,
        createdBy: null,
        creationDate: null,
        lastUpdatedBy: null,
        printCount: null,
        materialCode: null,
        materialId: null,
        materialName: null,
        materialSign: null,
        vendorId: null,
        vendorCode: null,
        vendorName: null,
        orderNumber: null,
        batchNo: null,
        orderLineNum: null,
        productionDate: null,
        categoryId: null,
        categoryCode: null,
        categoryName: null,
        currentLotQuantity: null,
        materialNorms: null,
        productionWeek: 0
      },
      rules: {
        categoryName: [
          { required: true, message: '请选择品类信息' }
        ],
        materialName: [
          { required: true, message: '请选择物料信息' }
        ],
        vendorName: [
          { required: true, message: '请选择供应商信息' }
        ],
        productionDate: [
          { required: true, message: '请选择生产日期' }
        ]
        // currentLotQuantity:[
        //   { required: true, message: "请填写待装箱数量" }
        // ],
        //  orderNumber: [
        //   { required: true, message: "请输入采购订单号" }
        // ],
        // orderLineNum: [
        //   { required: true, message: "请输入采购订单项次" }
        // ],
      },
      formExpandrules: {},
      readOnly: false,
      // 扩展字段(页面上显示用),数据库中标签字段表
      expandFields: [],
      // 外箱条码扩展字段（接口传参用）
      outerBoxExtendList: [],
      formExpand: {},
      // 回显内箱条码
      echoOuterBoxCode: [],
      // 当前批生成流水号
      generateSerial: '',
      // 显示打印标签按钮
      viewMoreCodeDisabled: true,
      // 编辑模式
      editMode: false,
      // 当前打印模板
      currentPrintTemplateName: '',
      // 打印模板列表
      printTemplateList: [],

      isVendorName: false,
      outShow: false,
      // 物料没有最小包装量列表(外箱)
      minimumList: [],
      // 扩展字段非空提示
      warnShow: { required: true, message: this.$t('common.pleaseInput') },
      outData: {
        outerBoxExtendList: [],
        outerBoxCodeList: []
      }

    }
  },
  computed: {},
  watch: {
    outShow: {
      handler () {
        if (this.outShow) {
          this.$set(this.rules, 'currentLotQuantity', [])
        } else {
          this.$set(this.rules, 'currentLotQuantity', [{ required: true, message: '请填写待装箱数量' }])
        }
      },
      deep: true,
      immediate: true
    }
  },
  created () {
    // 外箱条码打印模板
    getDictItem('OUTER_BOX_PRINT_TEMPLATE').then(res => {
      this.printTemplateList = adaptDictData(res.data, 'dict')
      console.log(this.printTemplateList, 'printTemplateList')
    })
    // 物料没有最小包装量列表（外箱）
    getDictItem('MINIMUM').then((res) => {
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
    const { flag, row, readOnly = false } = this.$attrs.params
    this.readOnly = readOnly
    if (flag === 'edit') {
      this.editMode = true// 开启编辑模式
      this.getDetail()
    }

    // const printTemplate = this.printTemplateList.find(t=>t.value===this.form.categoryCode);
    // this.currentPrintTemplateName =  "database:"+"外箱条码-五金件标签-zs.ureport.xml";//"database:"+printTemplate.label;
  },
  methods: {
    /**
     * 回显内箱条码行
     */
    showEchoOuterBoxCode (res) {
      this.echoOuterBoxCode = res.data
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
      boxtagconfigApi.getByCategoryIdAndBusinessBoxType({ categoryId: val.categoryId, businessBoxType: 'OUTER_BOX' })
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
          }
        })
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
      // const printTemplate = this.printTemplateList.filter(function (x) {
      //   return x.value.indexOf(_this.form.categoryCode) != -1
      // })
      this.currentPrintTemplateName = 'database:' + this.printTemplateList[0].label
      console.log(this.currentPrintTemplateName)
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

      // this.countPrint(this.form.outerBoxId, '')
      this.openPrint(
        this.currentPrintTemplateName,
        encodeURIComponent(`outerBoxCode=${this.form.outerBoxCode}`)
      )
    },

    /**
     * 打印标签
     */
    printBarCode (index, row) {
      // 获取打印模板
      this.getCurrentPrintTemplateName()

      // this.countPrint(row.outerBoxId, '')

      this.openPrint(
        this.currentPrintTemplateName,
        encodeURIComponent(`outerBoxCode=${row.outerBoxCode}`)
      )
    },
    /**
     * 累计打印次数
     */
    countPrint (outerBoxId, generateSerial) {
      this.$http({
        url: '/api-base/base/outerboxcode/countPrint',
        method: 'GET',
        params: { 'outerBoxId': outerBoxId, 'generateSerial': generateSerial },
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
    /**
     * 获取详情
     */
    getDetail () {
      outerBoxCodeApi.getById(this.$attrs.params.row.outerBoxId)
        .then((res) => {
          const { outerBoxExtendList, ...rest } = res.data
          this.form = rest
          console.log(outerBoxExtendList)
          // 清空拓展字段
          this.expandFields = []
          // 回显字段
          for (let field of outerBoxExtendList) {
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
    async save (type) {
      const obj = await this.getCheckForm()
      if (obj.flag) {
        const { flag } = this.$attrs.params
        // 新增时不用提交主键值
        const { outerBoxId, ...rest } = this.form

        // 转换字段类型
        let outerBoxExtendList = []
        let extendFieldTemp = {}
        let fieldCount = 0// 字段计数，三个字段一个对象
        console.log(this.formExpand)
        console.log(this.expandFields)
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
            outerBoxExtendList.push(extendFieldRecord)
            extendFieldTemp = {}
            fieldCount = 0
          }
        }

        rest.outerBoxExtendList = outerBoxExtendList
        rest.noCode = 'Y'// 逆向绑定
        rest.isFixedBox = 'N'// 非固定箱，绑定搜索条件用到
        if (flag === 'add') {
          console.log('type', type)
          console.log('1', type == '1')
          console.log('2', type == '2')
          if (type == '1') {
            outerBoxCodeApi.generateOuterBoxCode(rest).then((res) => {
              this.$message({
                type: 'success',
                message: res.message
              })
              console.log(res)
              this.showEchoOuterBoxCode(res)
              this.generateSerial = res.data[0].generateSerial
              // 屏蔽按钮
              this.readOnly = true
              this.viewMoreCodeDisabled = false
            })
          } else {
            const loading = this.$loading({
              lock: true,
              text: '自动生成内外箱条码，并且自动绑定，请稍等！！！',
              spinner: 'el-icon-loading',
              background: 'rgba(0, 0, 0, 0.7)'
            })
            outerBoxCodeApi.generateAutoCode(rest).then((res) => {
              this.$message({
                type: 'success',
                message: res.message
              })
              console.log(res)
              this.showEchoOuterBoxCode(res)
              this.generateSerial = res.data[0].generateSerial
              // 屏蔽按钮
              this.readOnly = true
              this.viewMoreCodeDisabled = false
            }).finally(loading.close())
          }
        } else {
          // 更新
          rest.outerBoxId = this.form.outerBoxId
          console.log(rest)
          outerBoxCodeApi.update(rest).then((res) => {
            this.$message({
              type: 'success',
              message: res.message
            })
            this.generateSerial = res.data.generateSerial
            this.getDetail()
            // 屏蔽按钮
            this.readOnly = true
            this.viewMoreCodeDisabled = false
          })
        }
      } else {
        this.__focus_error__(obj.message)
      }
    },
    asyncGetRealDataSource (data) {
      this.realDataSource = data
    },
    addLine () {
      this.$refs.table.add({})
    },
    deleteItem (scope, data) {
      data.splice(scope.$index, 1)
    },
    cancelBill () {
      this.$emit('tab-remove', this.$attrs.tabName)
      this.__setTabTodo('outerboxcodeList.getQuerydata')
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
            if (isNull(val[0])) {
              this.outShow = false
            } else {
              this.outShow = true
            }
            // this.outShow = true;
            // 根据品类获取扩展标签
            boxtagconfigApi.getByCategoryIdAndBusinessBoxType({ categoryId: data.data.categoryId, businessBoxType: 'OUTER_BOX' })
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
            message: '系统没有维护此物料信息，请管理员查看！'
          })
        })
    },
    updateOutList () {
      // console.log(this.echoOuterBoxCode)
      this.outData.outerBoxExtendList = []
      this.outData.outerBoxCodeList = []
      // 转换字段类型
      let outerBoxExtendList = []
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
          outerBoxExtendList.push(extendFieldRecord)
          extendFieldTemp = {}
          fieldCount = 0
        }
      }
      this.outData.outerBoxExtendList = outerBoxExtendList
      this.outData.outerBoxCodeList = this.echoOuterBoxCode
      this.outData.noCode = 'Y'// 逆向绑定
      outerBoxCodeApi.updateOuterBoxCodeList(this.outData)
        .then((res) => {
          this.$message({
            type: 'success',
            message: res.message
          })
          this.showEchoOuterBoxCode(res)
          this.generateSerial = res.data[0].generateSerial
          // 屏蔽按钮
          this.readOnly = true
          this.viewMoreCodeDisabled = false
        })
    },
    onInputBlur () {
      if (this.form.comments.length >= 60) {
        this.$message({
          type: 'warning',
          message: this.$t('当前字符长度超过字数限制，最大值60个字符')
        })
      }
    }
  }

}
</script>
<style scoped lang="scss">
.outerboxcodeEdit {
  height: 100%;
  padding-bottom: 50px;
  :deep(.table-wrapper) {
    padding-left: 0;
    padding-right: 0;
  }

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

.required {
  color: #ff4949;
  padding-right: 2px;
}
</style>
