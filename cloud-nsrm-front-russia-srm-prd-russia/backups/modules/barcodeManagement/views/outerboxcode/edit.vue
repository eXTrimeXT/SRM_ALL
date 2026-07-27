<template>
  <el-container
    class="outerboxcodeEdit"
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
              title="外箱条码基本信息"
              name="1"
            >
              <el-row :gutter="27">
                <el-col :span="6">
                  <el-form-item
                    prop="outerBoxCode"
                    label="外箱编码"
                  >
                    <el-input
                      v-model="form.outerBoxCode"
                      disabled="true"
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
                    prop="materialName"
                    label="物料名称"
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
                    prop="materialSign"
                    label="物料标签"
                  >
                    <el-input v-model="form.materialSign" />
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
                    prop="orderNumber"
                    label="采购订单号"
                  >
                    <el-input v-model="form.orderNumber" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    prop="batchNo"
                    label="批次号"
                  >
                    <el-input v-model="form.batchNo" />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item
                    prop="orderLineNum"
                    label="项次（订单行）"
                  >
                    <el-input v-model="form.orderLineNum" />
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

                <el-col :span="6">
                  <el-form-item
                    v-show="!editMode"
                    prop="numberOfGenerations"
                    label="生成条码数量"
                  >
                    <el-input v-model="form.numberOfGenerations" />
                  </el-form-item>
                </el-col>

                <el-col :span="6">
                  <el-form-item
                    prop="currentLotQuantity"
                    label="待装箱总数量"
                  >
                    <el-input
                      v-model="form.currentLotQuantity"
                      :disabled="editMode"
                    />
                  </el-form-item>
                </el-col>
              </el-row>
            </el-collapse-item>
            <!-- 条码扩展信息 -->
            <el-collapse-item
              v-show="expandFields.length > 0"
              title="外箱条码扩展信息"
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
                :data="echoOuterBoxCode"
                style="width: 100%"
                border
              >
                <el-table-column
                  align="center"
                  prop="outerBoxCode"
                  label="外箱条码编号"
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
                      打印标签信息
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
            返回
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
            批量打印
          </el-button>
          <el-button
            v-show="editMode"
            type="primary"
            @click="printBarCodeEdit"
          >
            打印
          </el-button>
        </template>
      </c-toolbar>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoMixin } from '@/utils/mixins'
import MainHeader from 'lib@/components/Table/MainHeader'
import CToolbar from 'lib@/components/c-toolbar'
import BaseTable from 'lib@/components/BaseTable/baseTable'
import MImport from 'lib@/components/import'
import { downloadFileLink } from 'lib@/utils/file'
import QuickSearch from 'lib@/components/QuickSearch' // 快速查询组件
export default {
  name: 'OuterboxcodeEdit',
  components: {
    MainHeader,
    CToolbar,
    BaseTable,
    MImport,
    QuickSearch
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      activeDims: ['1', '2', '3'],
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
        categoryName: null,
        numberOfGenerations: null,
        currentLotQuantity: null
      },
      rules: {
        categoryName: [{ required: true, message: '请选择品类信息' }],
        materialName: [{ required: true, message: '请选择物料信息' }],
        vendorName: [{ required: true, message: '请选择供应商信息' }],
        orderNumber: [{ required: true, message: '请输入采购订单号' }],
        orderLineNum: [{ required: true, message: '请输入采购订单项次' }]
      },
      readOnly: false,
      // 扩展字段(页面上显示用),数据库中标签字段表
      expandFields: [],
      // 外箱条码扩展字段（接口传参用）
      outerBoxExtendList: [],
      formExpand: [],
      // 回显内箱条码
      echoOuterBoxCode: [],
      // 当前批生成流水号
      generateSerial: '',
      // 显示打印标签按钮
      viewMoreCodeDisabled: true,
      // 编辑模式
      editMode: false,
      // 当前打印模板
      currentPrintTemplateName: ''
      // 打印模板列表
    }
  },
  computed: {},
  watch: {},
  mounted () {
    const { flag, row, readOnly = false } = this.$attrs.params
    this.readOnly = readOnly
    if (flag === 'edit') {
      this.editMode = true // 开启编辑模式
      this.getDetail()
    }
    this.currentPrintTemplateName = 'database:' + '外箱条码-五金件标签-zs.ureport.xml' // "database:"+printTemplate.label;
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
        encodeURIComponent(`outerBoxCode=${this.form.outerBoxCode}`)
      )
    },

    /**
     * 打印标签
     */
    printBarCode (index, row) {
      this.countPrint(row.outerBoxId, '')

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
        params: { outerBoxId: outerBoxId, generateSerial: generateSerial },
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
     * 获取详情
     */
    getDetail () {
      this.$api.generate.outerboxcode.getById(this.$attrs.params.row.outerBoxId).then(res => {
        const { outerBoxExtendList, ...rest } = res.data
        this.form = rest
        console.log(outerBoxExtendList)
        // 清空拓展字段
        this.expandFields = []
        // 回显字段
        for (let field of outerBoxExtendList) {
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
    handleSuccess () {
      this.getDetail()
    },
    save () {
      this.$refs.form.validate(result => {
        if (result) {
          const { flag } = this.$attrs.params
          // 新增时不用提交主键值
          const { outerBoxId, ...rest } = this.form

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
          rest.outerBoxExtendList = outerBoxExtendList
          if (flag === 'add') {
            this.$api.generate.outerboxcode.generateOuterBoxCode(rest).then(res => {
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
            // 更新
            rest.outerBoxId = this.form.outerBoxId
            console.log(rest)
            this.$api.generate.outerboxcode.update(rest).then(res => {
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
      const { flag, row } = this.$attrs.params
      if (flag === 'add') {
        this.$emit('tab-remove', 'outerboxcodeEdit')
      } else {
        this.$emit('tab-remove', 'outerboxcodeEdit' + row.outerBoxId)
      }
      this.__setTabTodo('outerboxcodeList.getQuerydata')
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
.outerboxcodeEdit {
  height: 100%;
  padding-bottom: 50px;
  ::v-deep .table-wrapper {
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
</style>
