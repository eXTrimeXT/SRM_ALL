<template>
  <el-container
    class="innerboxcodeEdit"
    direction="vertical"
  >
    <el-main>
      <div class="form-container">
        <el-form
          ref="form"
          :model="form"
          :rules="rules"
        >
          <el-collapse
            v-model="activeDims"
            class="tab-form-style"
          >
            <!-- 条码基本信息 -->
            <el-collapse-item
              title="内箱条码基本信息"
              name="1"
            >
              <el-row :gutter="27">
                <el-col :span="6">
                  <el-form-item
                    prop="innerBoxCode"
                    label="内箱条码编号"
                  >
                    <el-input
                      v-model="form.innerBoxCode"
                      disabled="true"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    prop="vendorName"
                    label="供应商名称"
                  >
                    <quick-search
                      :disable="isReadOnly"
                      :show-input="form.vendorName"
                      show-key="vendorName"
                      :scope-data="form"
                      name="scc_sup_company_info5"
                      @close-quicksearch="getCompanyByQuick"
                    />
                  </el-form-item>
                </el-col>

                <el-col :span="6">
                  <el-form-item
                    prop="materialName"
                    label="物料"
                  >
                    <quick-search
                      :disable="isReadOnly"
                      :show-input="form.materialName"
                      show-key="materialName"
                      :scope-data="form"
                      name="scc_base_material_item"
                      @close-quicksearch="getMaterialByQuick"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    prop="categoryName"
                    label="品类"
                  >
                    <quick-search
                      :disable="isReadOnly"
                      :show-input="form.categoryName"
                      show-key="categoryName"
                      :scope-data="form"
                      name="scc_base_purchase_category"
                      @close-quicksearch="getCategoryByQuick"
                    />
                  </el-form-item>
                </el-col>

                <el-col :span="6">
                  <el-form-item
                    prop="productionDate"
                    label="生产日期"
                  >
                    <el-date-picker
                      v-model="form.productionDate"
                      value-format="yyyy-MM-dd"
                    />
                  </el-form-item>
                </el-col>

                <el-col
                  v-show="!editMode"
                  :span="6"
                >
                  <el-form-item
                    prop="numberOfGenerations"
                    label="生成条码数量"
                  >
                    <el-input v-model="form.numberOfGenerations" />
                  </el-form-item>
                </el-col>

                <el-col :span="6">
                  <el-form-item
                    prop="currentLotQuantity"
                    label="待装箱数量"
                  >
                    <el-input v-model="form.currentLotQuantity" />
                  </el-form-item>
                </el-col>
              </el-row>
            </el-collapse-item>

            <!-- 条码扩展信息 -->
            <el-collapse-item
              v-show="expandFields.length > 0"
              title="內箱条码扩展信息"
              name="2"
            >
              <el-row :gutter="27">
                <!-- 遍历扩展字段 -->
                <template v-for="expandField in expandFields">
                  <el-col :key="expandField.fieldCode" :span="6">
                    <!-- 设置拓展字段基本信息 -->
                    <el-form-item
                      :prop="expandField.fieldCode"
                      :label="expandField.fieldName"
                    >
                      <!-- 输入框类型修改为 文本-->
                      <el-input
                        v-if="expandField.fieldTypeCode == 'text'"
                        v-model="formExpand[expandField.fieldCode]"
                      />
                      <!-- 输入框搜索类型为 日期 -->
                      <el-date-picker
                        v-if="expandField.fieldTypeCode == 'date'"
                        v-model="formExpand[expandField.fieldCode]"
                        value-format="yyyy-MM-dd"
                      />
                      <!-- 输入框搜索类型为 时间 -->
                      <el-date-picker
                        v-if="expandField.fieldTypeCode == 'dateTime'"
                        v-model="formExpand[expandField.fieldCode]"
                        value-format="yyyy-MM-dd HH:mm:ss"
                      />
                    </el-form-item>
                  </el-col>
                </template>
              </el-row>
            </el-collapse-item>

            <!-- 生成条码信息 -->
            <el-collapse-item
              v-show="!editMode"
              title="生成条码信息"
              name="3"
            >
              <el-table
                :data="echoInnerBoxCode"
                style="width: 100%"
                border
              >
                <el-table-column
                  align="center"
                  prop="innerBoxCode"
                  label="内箱条码编号"
                  :show-overflow-tooltip="true"
                  :disabled="isReadOnly"
                />

                <el-table-column
                  label="操作"
                  width="200"
                  fixed="right"
                >
                  <template slot-scope="scope">
                    <el-button
                      :disabled="isReadOnly"
                      type="text"
                      @click="printBarCode(scope.$index, scope.row)"
                    >
                      打印标签
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-collapse-item>
          </el-collapse>
        </el-form>
      </div>
      <c-toolbar>
        <template #right>
          <el-button
            @click="cancelBill"
          >
            取消
          </el-button>
          <el-button
            type="primary"
            :disabled="readOnly"
            @click="save"
          >
            保存
          </el-button>
          <el-button
            v-show="!editMode"
            type="primary"
            :disabled="viewMoreCodeDisabled"
            @click="printBarCodeMore"
          >
            批量打印标签
          </el-button>
          <el-button
            v-show="editMode"
            type="primary"
            @click="printBarCodeEdit"
          >
            打印标签
          </el-button>
        </template>
      </c-toolbar>
    </el-main>
  </el-container>
</template>
<script>
import { createDictClass } from 'lib@/utils/dict/dict-utils'
import { tabTodoMixin } from '@/utils/mixins'
import MainHeader from 'lib@/components/Table/MainHeader'
import CToolbar from 'lib@/components/c-toolbar'
import QuickSearch from 'lib@/components/QuickSearch' // 快速查询组件
import {
  getDictItem,
  getDictItemList,
  getAllRule
} from '@/api/common'
import { adaptDictData, parseTime } from '@/utils'

export default {
  name: 'InnerboxcodeEdit',
  components: {
    MainHeader,
    CToolbar,
    QuickSearch
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      dictClass: createDictClass({ 'INNER_BOX_PRINT_TEMPLATE': [], 'unit': [] }),
      activeDims: ['1', '2', '3'],
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
        categoryName: null,
        packingQuantity: null,
        unitCode: null,
        unit: null,
        createdBy: null,
        creationDate: null,
        lastUpdatedBy: null,
        numberOfGenerations: null,
        innerBoxExtendList: null,
        currentLotQuantity: null
      },
      // 表单扩展字段
      formExpand: {},
      rules: {},
      readOnly: false,
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
      currentPrintTemplateName: ''
    }
  },
  computed: {
    unitList () {
      return this.dictClass.getDict('unit')
    },
    // 内箱条码打印模板
    printTemplateList () {
      return this.dictClass.getDict('INNER_BOX_PRINT_TEMPLATE')
    }
  },
  mounted () {
    const { flag, row, readOnly = false } = this.$attrs.params
    this.readOnly = readOnly
    if (flag === 'edit') {
      this.editMode = true
      this.getDetail()

      const printTemplate = this.printTemplateList.find(t => t.value === this.form.categoryCode)
      this.currentPrintTemplateName = 'database:' + '内箱条码-五金件标签-zs.ureport.xml' // "database:"+printTemplate.label;
    }
  },
  methods: {
    /**
     * 回显内箱条码行
     */
    echoInnerBoxCodeLine (res) {
      this.echoInnerBoxCode = res.data
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
    },
    /**
     * 根据快查获取品类信息
     */
    getCategoryByQuick (val, scope) {
      scope.categoryId = val ? val.categoryId : ''
      scope.categoryName = val ? val.categoryName : ''

      // 根据品类获取扩展标签
      this.$api.generate.boxtagconfig.getByCategoryId(val.categoryId).then(res => {
        this.expandFields = res.data
        // 回显字段
        for (let field of this.expandFields) {
          this.$set(this.formExpand, field.fieldCode, '')
          this.$set(this.formExpand, field.fieldCode + '_@type', field.fieldTypeCode)
          this.$set(this.formExpand, field.fieldCode + '_@name', field.fieldName)
        }
      })

      // 根据品类编码匹配对应打印模板
      const printTemplate = this.printTemplateList.find(t => t.value === val.categoryCode)
      this.currentPrintTemplateName = 'database:' + '内箱条码-五金件标签-zs.ureport.xml' // "database:"+printTemplate.label;
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
     * 批量打印标签
     */
    printBarCodeMore () {
      this.countPrint('', this.generateSerial)
      this.openPrint(
        this.currentPrintTemplateName,
        encodeURIComponent(`generateSerial=${this.generateSerial}`)
      )
    },
    /**
     * 编辑模式下打印
     */
    printBarCodeEdit () {
      this.countPrint(this.form.innerBoxId, '')
      this.openPrint(
        this.currentPrintTemplateName,
        encodeURIComponent(`innerBoxCode=${this.form.innerBoxCode}`)
      )
    },

    /**
     * 打印标签
     */
    printBarCode (index, row) {
      this.countPrint(row.innerBoxId, '')

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
        params: { innerBoxId: innerBoxId, generateSerial: generateSerial },
        loading: true
      })
        .then(data => {})
        .catch(err => {
          console.log(err)
        })
    },
    /**
     * 打开pdf
     */
    openPrint (pdfName, params) {
      if (!window.location.origin) {
        window.location.origin =
          window.location.protocol +
          '//' +
          window.location.hostname +
          (window.location.port ? ':' + window.location.port : '')
      }
      const xml = encodeURIComponent(pdfName)
      const url = `${window.location.origin}/#/pdfPrint?isBarcode=Y&xml=${xml}&params=${params}`
      window.open(url)
    },
    /**
     * 保存、更新
     */
    save () {
      this.$refs.form.validate(result => {
        if (result) {
          const { flag } = this.$attrs.params
          // 新增时不用提交主键值
          const { innerBoxId, ...rest } = this.form

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
            this.$api.generate.innerboxcode.add(rest).then(res => {
              this.$message({
                type: 'success',
                message: res.message
              })
              console.log(res)
              this.echoInnerBoxCodeLine(res)
              this.generateSerial = res.data[0].generateSerial
              // 屏蔽按钮
              this.readOnly = true
              this.viewMoreCodeDisabled = false
            })
          } else {
            rest.innerBoxId = this.form.innerBoxId
            // 更新
            console.log(rest)
            this.$api.generate.innerboxcode.update(rest).then(res => {
              this.$message({
                type: 'success',
                message: res.message
              })
              this.generateSerial = res.data[0].generateSerial
              // 屏蔽按钮
              this.readOnly = true
              this.viewMoreCodeDisabled = false
            })
          }
        } else {
          this.__focus_error__()
        }
      })
    },
    /**
     * 获取详情
     */
    getDetail () {
      this.$api.generate.innerboxcode.getById(this.$attrs.params.row.innerBoxId).then(res => {
        const { innerBoxExtendList, ...rest } = res.data
        this.form = rest
        console.log(innerBoxExtendList)
        // 清空拓展字段
        this.expandFields = []
        // 回显字段
        for (let field of innerBoxExtendList) {
          // 保存用的
          this.$set(this.formExpand, field.fieldCode, field.fieldVal)
          this.$set(this.formExpand, field.fieldCode + '_@type', field.fieldType)
          this.$set(this.formExpand, field.fieldCode + '_@name', field.fieldDesc)

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
      const { flag, row } = this.$attrs.params
      if (flag === 'add') {
        this.$emit('tab-remove', 'innerboxcodeEdit')
      } else {
        this.$emit('tab-remove', 'innerboxcodeEdit' + row.innerBoxId)
      }
      this.__setTabTodo('innerboxcodeList.getQuerydata')
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
}
</style>
