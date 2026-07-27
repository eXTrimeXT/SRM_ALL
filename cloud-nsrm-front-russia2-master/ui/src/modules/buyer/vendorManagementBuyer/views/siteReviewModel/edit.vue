<template>
  <el-container
    class="sitereviewmodelEdit"
    direction="vertical"
  >
    <el-main>
      <div class="form-container">
        <el-form
          ref="form"
          :model="form"
          :rules="rules"
        >
          <srm-row>
            <srm-col :initCol="4">
              <el-form-item
                prop="reviewModelCode"
                :label="$t('dataConfMod.templateCode')"
              >
                <el-input
                  v-model="form.reviewModelCode"
                  :disabled="true"
                />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="4">
              <el-form-item
                prop="reviewModelName"
                :label="$t('priceTemplate.templateName')"
              >
                <el-input
                  v-model="form.reviewModelName"
                  :disabled="readOnly"
                />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="4">
              <el-form-item
                prop="createdBy"
                :label="$t('dataConfMod.createdBy')"
              >
                <el-input
                  v-model="form.createdBy"
                  :disabled="readOnly"
                />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="4">
              <el-form-item
                prop="creationDate"
                :label="$t('common.creationTime')"
              >
                <el-date-picker
                  v-model="form.creationDate"
                  :format="$formatDatePickerTime"
                  value-format="yyyy-MM-dd HH:mm:ss"
                  :disabled="readOnly"
                />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="4">
              <el-form-item
                prop="relationModelId"
                :label="$t('dataConfMod.quoteQuestTemplateName')"
              >
                <el-select
                  v-model="form.relationModelId"
                  :placeholder="$t('common.pleaseSelect')"
                  :disabled="readOnly"
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
                :label="$t('bidMod.appraisRemark')"
              >
                <el-input
                  v-model="form.remark"
                  :disabled="readOnly"
                />
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
            {{ $t('bidMod.affairsIncreased') }}
          </el-button>
        </div>
        <el-tabs
          v-model="editableTabsValue"
          type="card"
          :closable="!readOnly"
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
              <div style="padding-bottom: 10px;">
                <el-button
                  v-if="!readOnly"
                  class="detail-pbtn"
                  type="primary"
                  @click="addList"
                >
                  {{ $t('bidMod.affairsIncreased') }}
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
                  :label="$t('dataConfMod.questTemplatePropFieldDesc')"
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
                <!-- 字典编码 -->
                <el-table-column
                  align="center"
                  prop="dictCode"
                  :label="$t('bidMod.dictCode')"
                >
                  <template slot-scope="scope">
                    <el-input
                      v-model="scope.row.dictCode"
                      :placeholder="$t('common.pleaseInput')"
                      :disabled="readOnly"
                    />
                  </template>
                </el-table-column>
                <!-- 组件属性 -->
                <el-table-column
                  align="center"
                  prop="assemblyType"
                  :label="$t('dataConfMod.componentProperty')"
                >
                  <template slot-scope="scope">
                    <el-input
                      v-model="scope.row.assemblyType"
                      :placeholder="$t('common.pleaseInput')"
                      :disabled="readOnly"
                    />
                  </template>
                </el-table-column>
                <!-- 是否必填 -->
                <el-table-column
                  align="center"
                  prop="necessaryFlag"
                  :label="$t('dataConfMod.isRequested')"
                  min-width="100"
                  :show-overflow-tooltip="true"
                >
                  <template slot-scope="scope">
                    <el-checkbox
                      v-model="scope.row.necessaryFlag"
                      true-label="true"
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

      <CToolbar>
        <template #right>
          <el-button
            @click="preview"
          >
            {{ $t('dataConfMod.previewTemplate') }}
          </el-button>
          <el-button
            :disabled="readOnly"
            @click="save('SAVE')"
          >
            {{ $t('bidMod.temporaryStorage') }}
          </el-button>
          <el-button
            type="primary"
            :disabled="readOnly"
            @click="save('SUBMIT')"
          >
            {{ $t('problemManagement.submit') }}
          </el-button>
        </template>
      </CToolbar>

      <srm-dialog
        :visible.sync="previewBol"
        size="large"
        :title="form.reviewModelName + $t('cusEntry.supplement20250211.previewStatus')"
        :append-to-body="true"
      >
        <Printer
          :editable-tabs="editableTabs"
          :read-only="true"
        />
        <div slot="footer">
          <el-button @click="previewBol = false">
            {{ $t('bidMod.cancel') }}
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
          <srm-col :initCol="4">
            {{ $t('key14') }}<el-input
              v-model="addTabNameC"
              :placeholder="$t('dataConfMod.enterName')"
            />
          </srm-col>
          <srm-col :initCol="4">
            {{ $t('key13') }}
            <el-select
              v-model="modelType"
              :placeholder="$t('common.pleaseSelect')"
            >
              <el-option
                v-for="item in modelTypeOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </srm-col>
        </srm-row>
        <div slot="footer">
          <el-button @click="addShow = false">
            {{ $t('bidMod.cancel') }}
          </el-button>
          <el-button
            type="primary"
            @click="addTab"
          >
            {{ $t('orderMod.buyerOrderSynergy.confirm') }}
          </el-button>
        </div>
      </srm-dialog>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoMixin } from '@/utils/mixins'
import MainHeader from 'lib@/components/Table/MainHeader'
import CToolbar from 'lib@/components/c-toolbar'
import QuickSearch from 'lib@/components/QuickSearch'
import Printer from './printer'
import { siteReviewModel, reviewFormStandard } from 'modb@/vendorManagementBuyer/api/vendorManagement'

export default {
  name: 'SitereviewmodelEdit',
  components: {
    MainHeader,
    CToolbar,
    QuickSearch,
    Printer
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      previewBol: false,
      relationOptions: [], // 标准引用下拉框
      modelType: 'FORM',
      modelTypeOptions: [
        {
          value: 'TABLE',
          // '表格'
          label: this.$t('dataConfMod.table')
        },
        {
          value: 'FORM',
          // '表单'
          label: this.$t('dataConfMod.menu')
        }
      ],

      addTabNameC: '', // 新增模块名称名字
      addShow: false,
      editableTabsValue: '1',
      editableTabs: [
        {
          // '基础标准'
          title: this.$t('dataConfMod.basicStandard'),
          name: '1',
          type: 'FORM',
          content: []
        }
      ],
      content: [],
      tabIndex: 1,
      fieldTypeOptions: [
        {
          value: 'text',
          // '文本'
          label: this.$t('perfMod.text')
        },
        {
          value: 'textarea',
          // '多行文本'
          label: this.$t('dataConfMod.textarea')
        },
        {
          value: 'timer',
          // '时间'
          label: this.$t('components.time')
        },
        {
          value: 'select',
          // '下拉框'
          label: this.$t('bidMod.selectBox')
        }
      ],
      form: {
        reviewModelCode: null,
        reviewModelName: null,
        approveStatus: null,
        relationModelId: null,
        createdBy: null,
        creationDate: null,
        createdFullName: null,
        lastUpdatedBy: null,
        lastUpdatedFullName: null
      },
      rules: {
        reviewModelName: [{ required: true, message: this.$t('common.pleaseInput') }]
      },
      readOnly: false
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
    siteReviewModel.listAll().then(res => {
      let relationOptions = []
      res.data.forEach(datas => {
        let obj = {
          value: datas.reviewModelId,
          label: datas.reviewModelName
        }
        relationOptions.push(obj)
      })
      this.relationOptions = relationOptions
    })
  },
  mounted () {
    const { flag, row, readOnly } = this.$attrs.params
    this.readOnly = readOnly
    if (flag === 'edit') {
      this.form = row
      this.editDetail()
    }
  },
  methods: {
    handleDelClick (indexs, row) {
      this.content.splice(indexs, 1)
    },
    editDetail () {
      const id = this.form.reviewModelId
      siteReviewModel.getDetail(id).then(res => {
        this.getList(res)
      })
    },
    // 当编辑或者新增选择模板后循环下面的列表
    getList (res) {
      let editableTabs = []
      res.data.dimList.forEach((datas, indexs) => {
        console.log(datas)
        const index = String(indexs + 1)
        let d1 = {
          title: datas.dimName,
          name: index,
          type: datas.dimType,
          content: []
        }
        this.tabIndex = index
        let attr = []
        datas.fieldList.forEach(datas2 => {
          let d2 = {
            fieldCode: datas2.fieldCode,
            fieldName: datas2.fieldName,
            dictCode: datas2.dictCode,
            fieldType: datas2.fieldType,
            assemblyType: datas2.assemblyType,
            necessaryFlag: datas2.necessaryFlag
          }
          attr.push(d2)
        })
        d1.content = attr
        editableTabs.push(d1)
      })
      this.editableTabs = editableTabs
      this.content = this.editableTabs[0].content
    },
    relationChange () {
      const id = this.form.relationModelId
      reviewFormStandard.getDetail(id).then(res => {
        this.getList(res)
      })
    },
    preview () {
      this.previewBol = true
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
        type: this.modelType,
        content: []
      })
      console.log(this.editableTabs)
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
    save (types) {
      this.$refs.form.validate(result => {
        console.log(result)
        if (result) {
          const { flag, row } = this.$attrs.params
          // 新增时不用提交主键值
          let datas = {}
          datas.submitFlag = types
          datas.reviewModelName = this.form.reviewModelName
          datas.relationModelId = this.form.relationModelId
          datas.remark = this.form.remark

          // 循环表格数据
          console.log(this.editableTabs)
          let bol = false
          this.editableTabs.forEach(a1 => {
            a1.content.forEach(a2 => {
              if (a2.fieldType == 'select' && a2.dictCode == '') {
                bol = true
              }
            })
          })
          if (bol) {
            // '下拉框需要填写字段'
            this.$message.error(this.$t('vendorMod.requireFill'))
            return false
          }

          let reviewFormStandardDimList = []
          this.editableTabs.forEach(datas1 => {
            let d1 = {
              dimName: datas1.title,
              dimType: datas1.type
            }
            let attr = []
            datas1.content.forEach(datas2 => {
              let d2 = {
                fieldName: datas2.fieldName,
                dictCode: datas2.dictCode,
                fieldType: datas2.fieldType,
                assemblyType: datas2.assemblyType,
                necessaryFlag: datas2.necessaryFlag
              }
              attr.push(d2)
            })
            d1.fieldList = attr
            reviewFormStandardDimList.push(d1)
          })
          datas.dimList = reviewFormStandardDimList
          console.log(datas)

          // return false;
          if (flag === 'add') {
            siteReviewModel.add(datas).then(res => {
              this.$message({
                type: 'success',
                message: res.message
              })
              this.cancelBill()
            })
          } else if (flag === 'edit') {
            datas.reviewModelId = row.reviewModelId
            console.log(datas)
            siteReviewModel.modify(datas).then(res => {
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
        this.$emit('tab-remove', 'sitereviewmodelEdit')
      } else {
        this.$emit('tab-remove', 'sitereviewmodelEdit' + row.reviewModelId)
      }
      this.__setTabTodo('sitereviewmodelList.getQuerydata')
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
    margin-bottom: 35px;
  }
}
.sitereviewmodelEdit {
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
