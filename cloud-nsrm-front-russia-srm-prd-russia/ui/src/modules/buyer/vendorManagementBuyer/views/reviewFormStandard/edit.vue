<template>
  <el-container
    class="reviewformstandardEdit"
    direction="vertical"
  >
    <el-main>
      <div class="form-container">
        <el-form
          ref="form"
          :model="form"
          :rules="rules"
          :disabled="readOnly"
        >
          <srm-row>
            <srm-col :initCol="4">
              <el-form-item
                prop="standardCode"
                :label="$t('dataConfMod.standardNumber')"
              >
                <el-input
                  v-model="form.standardCode"
                  :disabled="true"
                />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="4">
              <el-form-item
                prop="standardName"
                :label="$t('dataConfMod.standardName')"
              >
                <el-input
                  v-model="form.standardName"
                  maxlength="100"
                  show-word-limit
                />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="4">
              <el-form-item
                prop="createdBy"
                :label="$t('dataConfMod.creator')"
              >
                <el-input
                  v-model="form.createdBy"
                  :disabled="true"
                />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="4">
              <el-form-item
                prop="creationDate"
                :label="$t('dataConfMod.creationDate')"
              >
                <el-input
                  v-model="form.creationDate"
                  :disabled="true"
                />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="4">
              <el-form-item
                prop="relationId"
                :label="$t('dataConfMod.relationId')"
              >
                <el-select
                  v-model="form.relationId"
                  :placeholder="$t('common.pleaseSelect')"
                  @change="relationChange"
                >
                  <el-option
                    v-for="item in relationOptions"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  />
                </el-select>
              </el-form-item>
            </srm-col>
            <srm-col :initCol="2">
              <el-form-item
                prop="remark"
                :label="$t('dataConfMod.remark')"
              >
                <el-input v-model="form.remark" />
              </el-form-item>
            </srm-col>
          </srm-row>
        </el-form>
      </div>

      <!-- 标签页列表 -->
      <div class="tabE">
        <div class="addButtom">
          <el-button
            v-if="!readOnly"
            type="primary"
            @click="addTabName(editableTabsValue)"
          >
            {{ $t('common.add') }}
          </el-button>
        </div>
        <el-tabs
          v-model="editableTabsValue"
          type="card"
          :closable="readOnly"
          @tab-remove="removeTab"
          @tab-click="changeTab(editableTabsValue)"
        >
          <el-tab-pane
            v-for="item in editableTabs"
            :key="item.name"
            :label="item.title"
            :name="item.name"
          >
            <div class="listAll">
              <div v-if="!readOnly" style="padding-bottom: 10px;">
                <el-button
                  class="detail-pbtn"
                  type="primary"
                  @click="addList"
                >
                  {{ $t('common.add') }}
                </el-button>
              </div>
              <el-table
                :data="content"
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
                <!-- 字段编码 -->
                <el-table-column
                  align="center"
                  prop="fieldCode"
                  :label="$t('contract_mod.fieldCode')"
                >
                  <template slot-scope="scope">
                    <el-input
                      v-model="scope.row.fieldCode"
                      :placeholder="$t('dataConfMod.autoAccordingRules')"
                      disabled
                    />
                  </template>
                </el-table-column>
                <!-- 字段名称 -->
                <el-table-column
                  align="center"
                  prop="fieldName"
                  :label="$t('contract_mod.fieldName')"
                >
                  <template slot-scope="scope">
                    <el-input
                      v-model="scope.row.fieldName"
                      :placeholder="$t('common.pleaseInput')"
                      :disabled="readOnly"
                    />
                  </template>
                </el-table-column>
                <!-- 字段类型 -->
                <el-table-column
                  align="center"
                  prop="fieldType"
                  :label="$t('contract_mod.fieldType')"
                >
                  <template slot-scope="scope">
                    <el-select
                      v-model="scope.row.fieldType"
                      :placeholder="$t('common.pleaseSelect')"
                      :disabled="readOnly"
                    >
                      <el-option
                        v-for="typeItem in fieldTypeOptions"
                        :key="typeItem.value"
                        :label="typeItem.label"
                        :value="typeItem.value"
                      />
                    </el-select>
                  </template>
                </el-table-column>
                <!-- 字段内容 -->
                <el-table-column
                  align="center"
                  prop="fieldContent"
                  :label="$t('dataConfMod.fieldContent')"
                >
                  <template slot-scope="scope">
                    <el-input
                      v-model="scope.row.fieldContent"
                      :placeholder="$t('common.pleaseInput')"
                      maxlength="100"
                      show-word-limit
                      :disabled="readOnly"
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
                      :disabled="readOnly"
                      @click="handleDelClick(scope.$index, scope.row)"
                    >
                      {{ $t("common.delete") }}
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>

      <CToolbar v-if="!readOnly">
        <template #right>
          <el-button
            @click="preview"
          >
            {{ $t('dataConfMod.previewStandard') }}
          </el-button>
          <el-button
            @click="temporaryStorage"
          >
            {{ $t('common.staging') }}
          </el-button>
          <el-button
            type="primary"
            :disabled="readOnly"
            @click="saveSubmit"
          >
            {{ $t('common.affirm') }}
          </el-button>
        </template>
      </CToolbar>

      <srm-dialog
        :visible.sync="previewBol"
        size="middle"
        :title="form.standardName"
        :append-to-body="true"
      >
        <div ref="printer">
          <el-collapse v-model="activeDims">
            <el-collapse-item
              v-for="item in editableTabs"
              :ref="item.name"
              :key="item.name"
              :title="item.title"
              name="1"
            >
              <srm-row>
                <srm-col
                  v-for="(item2, index) in item.content"
                  :key="index"
                  :initCol="item2.fieldType == 'textarea' ? 2 : 3"
                >
                  {{ item2.fieldName }}：
                  <div
                    style="border:1px solid #ccc;border-radius:5px;padding: 5px;"
                  >
                    {{ item2.fieldContent }}
                  </div>
                  <!-- <el-input
                    v-model="item2.fieldContent"
                    :value="item2.fieldContent"
                    readonly
                    :type="item2.fieldType == 'textarea' ? 'textarea' : 'text'"
                    placeholder="请输入名称"
                /> -->
                </srm-col>
              </srm-row>
            </el-collapse-item>
          </el-collapse>
        </div>
        <div slot="footer">
          <!-- <el-button type="primary" @click="printer">打印</el-button> -->
          <el-button @click="previewBol = false">
            {{ $t('common.backTo') }}
          </el-button>
        </div>
      </srm-dialog>

      <srm-dialog
        :visible.sync="addShow"
        size="middle"
        :title="$t('dataConfMod.newModuleName')"
        :append-to-body="true"
      >
        <srm-row>
          <srm-col :initCol="2">
            {{ $t('dataConfMod.newModuleName') }}:
            <el-input
              v-model="addTabNameC"
              :placeholder="$t('dataConfMod.enterName')"
            />
          </srm-col>
        </srm-row>
        <div slot="footer">
          <el-button @click="addShow = false">
            {{ $t('common.cancel') }}
          </el-button>
          <el-button
            type="primary"
            @click="addTab"
          >
            {{ $t('common.affirm') }}
          </el-button>
        </div>
      </srm-dialog>
    </el-main>
    <iframe
      ref="iframe"
      style="display: none"
      :src="pdfUrl"
    />
  </el-container>
</template>
<script>
import { tabTodoMixin } from '@/utils/mixins'
import MainHeader from 'lib@/components/Table/MainHeader'
import CToolbar from 'lib@/components/c-toolbar'
import QuickSearch from 'lib@/components/QuickSearch'
import axios from 'axios'
import { reviewFormStandard } from 'modb@/vendorManagementBuyer/api/vendorManagement'

export default {
  name: 'ReviewformstandardEdit',
  components: {
    MainHeader,
    CToolbar,
    QuickSearch
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      pdfUrl: '',
      readOnly: false,
      saveType: 'SUBMIT',
      activeDims: [
        '1',
        '2',
        '3',
        '4',
        '5',
        '6',
        '7',
        '8',
        '9',
        '10',
        '11',
        '12'
      ],
      previewBol: false,
      relationOptions: [], // 标准引用下拉框
      fieldTypeOptions: [
        {
          value: 'text',
          label: this.$t('perfMod.text')
        },
        {
          value: 'textarea',
          label: this.$t('dataConfMod.textarea')
        }
      ],
      addTabNameC: '', // 新增模块名称名字
      addShow: false, // 是否展示新增模块名字弹窗
      form: {
        standardId: null,
        standardCode: null,
        standardName: null,
        standardStatus: null,
        createdBy: null,
        creationDate: null,
        lastUpdatedBy: null
      },
      rules: {},
      editableTabsValue: '1',
      editableTabs: [
        {
          title: this.$t('dataConfMod.basicStandard'),
          name: '1',
          content: []
        }
      ],
      content: [{}],
      tabIndex: 1
    }
  },
  computed: {},
  watch: {
    content: {
      handler () {
        const content = this.content
        let indexE = this.editableTabsValue
        indexE = indexE - 1
        console.log(this.editableTabs)
        console.log(indexE)
        this.editableTabs[indexE].content = content
      },
      deep: true,
      immediate: true
    }
  },
  created () {
    // const { flag } = this.$attrs.params
    // if (flag == 'readOnly') {
    //   this.readOnly = true
    // } else {
    //   this.readOnly = false
    // }
    reviewFormStandard.listAll().then(res => {
      let relationOptions = []
      console.log(res)
      res.data.forEach(datas => {
        let obj = {
          value: datas.standardId,
          label: datas.standardName
        }
        relationOptions.push(obj)
      })
      this.relationOptions = relationOptions
    })
  },
  mounted () {
    const { flag, row } = this.$attrs.params
    if (flag == 'readOnly') {
        this.readOnly = true
    } else {
        this.readOnly = false
    }
    if (flag === 'edit' || flag === 'readOnly') {
      this.form = row
      this.editDetail()
    }
  },
  methods: {
    // 打印
    async printer () {
      const res = await axios({
        url: '/egg/upload',
        method: 'POST',
        loading: true,
        data: {
          options: {
            format: 'a4',
            margin: {
              left: '1cm',
              top: '1cm',
              right: '1cm',
              bottom: '1cm'
            }
          },
          htmlString:
            this.$refs.printer.innerHTML +
            '<style>.srm-col-8 {width: 33.33333%;}.el-collapse-item__header {height:32px;line-height:32px;margin-top:25px;font-size:25px}</style>'
        },
        responseType: 'arraybuffer'
      })
      console.log('[ArrayBuffer]', res.data instanceof ArrayBuffer)
      const blob = new Blob([res.data], { type: 'application/pdf' })
      this.pdfUrl = URL.createObjectURL(blob)
      setTimeout(() => {
        this.$refs.iframe.contentWindow.print()
      }, 1000)
    },
    // 点击暂存
    temporaryStorage () {
      this.saveType = 'SAVE'
      this.save()
    },
    saveSubmit () {
      this.saveType = 'SUBMIT'
      this.save()
    },
    // 点击预览标准
    preview () {
      this.previewBol = true
    },
    // 选择标准引用触发
    relationChange () {
      const id = this.form.relationId
      reviewFormStandard.getDetail2(id).then(res => {
        this.getList(res)
      })
    },
    editDetail () {
      const id = this.form.standardId
      reviewFormStandard.getDetail2(id).then(res => {
        this.getList(res)
      })
    },
    // 当编辑或者新增选择模板后循环下面的列表
    getList (res) {
      let editableTabs = []
      res.data.reviewFormStandardDimList.forEach((datas, indexs) => {
        const index = String(indexs + 1)
        let d1 = {
          title: datas.dimName,
          name: index,
          content: []
        }
        this.tabIndex = index
        let attr = []
        datas.reviewFormStandardDimFieldList.forEach(datas2 => {
          let d2 = {
            standardId: datas2.standardId,
            standardDimId: datas2.standardDimId,
            fieldCode: datas2.fieldCode,
            fieldName: datas2.fieldName,
            fieldType: datas2.fieldType,
            fieldContent: datas2.fieldContent
          }
          attr.push(d2)
        })
        d1.content = attr
        editableTabs.push(d1)
      })
      this.editableTabs = editableTabs
      this.content = this.editableTabs[0].content
      // this.form.standardId = res.data.standardId
    },
    handleDelClick (indexs, row) {
      this.content.splice(indexs, 1)
    },
    addList () {
      this.content.push({})
    },
    changeTab (changeOne) {
      // console.log(changeOne);
      let indexE = changeOne
      indexE = indexE - 1
      const content = this.editableTabs[indexE].content
      this.content = content
    },
    addTabName (targetName) {
      this.addTabNameC = ''
      this.addShow = true
    },
    // 标签页点击新增
    addTab (targetName) {
      let newTabName = ++this.tabIndex + ''
      this.editableTabs.push({
        title: this.addTabNameC,
        name: newTabName,
        content: []
      })
      this.editableTabsValue = newTabName
      this.addShow = false
      this.content = [{}]
    },
    // 标签页点击删除
    removeTab (targetName) {
      let tabs = this.editableTabs
      let activeName = this.editableTabsValue
      if (activeName === targetName) {
        tabs.forEach((tab, index) => {
          if (tab.name === targetName) {
            let nextTab = tabs[index + 1] || tabs[index - 1]
            if (nextTab) {
              activeName = nextTab.name
            }
          }
        })
      }

      this.editableTabsValue = activeName
      this.editableTabs = tabs.filter(tab => tab.name !== targetName)
    },
    save () {
      this.$refs.form.validate(result => {
        if (result) {
          const { flag } = this.$attrs.params
          // 修改数据格式准备上传
          let obj = {}
          obj.standardName = this.form.standardName
          obj.remark = this.form.remark
          obj.relationId = this.form.relationId
          obj.standardId = this.form.standardId
          obj.submitFlag = this.saveType
          // 循环表格数据
          console.log(this.editableTabs)
          let reviewFormStandardDimList = []
          this.editableTabs.forEach(datas1 => {
            let d1 = {
              dimName: datas1.title
            }
            let attr = []
            datas1.content.forEach(datas2 => {
              let d2 = {
                fieldName: datas2.fieldName,
                fieldContent: datas2.fieldContent,
                fieldType: datas2.fieldType
              }
              attr.push(d2)
            })
            d1.reviewFormStandardDimFieldList = attr
            reviewFormStandardDimList.push(d1)
          })
          obj.reviewFormStandardDimList = reviewFormStandardDimList
          if (flag === 'add') {
            reviewFormStandard.add(obj).then(res => {
              this.$message({
                type: 'success',
                message: res.message
              })
              this.cancelBill()
            })
          } else if (flag === 'edit') {
            reviewFormStandard.update(obj).then(res => {
              this.$message({
                type: 'success',
                message: res.message
              })
              this.cancelBill()
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
        this.$emit('tab-remove', 'reviewformstandardEdit')
      } else {
        this.$emit('tab-remove', 'reviewformstandardEdit' + row.standardId)
      }
      this.__setTabTodo('reviewformstandardList.getQuerydata')
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
.tabE {
  margin-top: 20px;
  position: relative;
  padding: 10px 0;
  .addButtom {
    position: absolute;
    top: 8px;
    right: 15px;
    z-index: 999;
  }
  .listAll {
    margin-top: 15px;
  }
}
.reviewformstandardEdit {
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
