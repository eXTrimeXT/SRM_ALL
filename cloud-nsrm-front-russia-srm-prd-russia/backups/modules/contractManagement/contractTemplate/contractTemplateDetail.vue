<template>
  <el-container
    class="flex-container the-contractTemplateDetail-detail"
    direction="vertical"
  >
    <el-main>
      <div class="form-container2">
        <el-form
          ref="form"
          :model="form"
          label-width="80px"
          label-position="top"
          class="form-incontainer"
          :rules="rules"
        >
          <el-collapse
            v-model="activeDims"
            class="tab-form-style"
          >
            <el-collapse-item
              :title="addEditText"
              name="1"
            >
              <srm-row>
                <srm-col>
                  <el-form-item
                    :label="$t('contractMod.templType')"
                    :label-width="formLabelWidth"
                    prop="templType"
                  >
                    <dict-select
                      v-model="form.templType"
                      code="TEMPLATE_TYPE"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item
                    :label="$t('contractMod.templHeadId')"
                    :label-width="formLabelWidth"
                    prop="templName"
                  >
                    <el-input v-model="form.templName" />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item
                    :label="$t('contractMod.templDescription')"
                    :label-width="formLabelWidth"
                  >
                    <el-input v-model="form.templDescription" />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item
                    :label="$t('contractMod.contractStatus')"
                    :label-width="formLabelWidth"
                    prop="templStatus"
                  >
                    <dict-select
                      v-model="form.templStatus"
                      code="TEMPLATE_STATUS"
                      disabled
                    />
                  </el-form-item>
                </srm-col>
              </srm-row>
            </el-collapse-item>
            <el-collapse-item
              :title="$t('contractMod.contractContent2')"
              name="2"
            >
              <srm-row>
                <srm-col>
                  <el-form-item
                    :label="$t('contractMod.owner')"
                    :label-width="formLabelWidth"
                    prop="owner"
                  >
                    <el-input v-model="form.owner" />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item
                    :label="$t('contractMod.fax')"
                    :label-width="formLabelWidth"
                    prop="fax"
                  >
                    <el-input v-model="form.fax" />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item
                    :label="$t('common.phone')"
                    :label-width="formLabelWidth"
                    prop="phone"
                  >
                    <el-input v-model="form.phone" />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item
                    :label="$t('contractMod.signingSite')"
                    :label-width="formLabelWidth"
                  >
                    <el-input v-model="form.signingSite" />
                  </el-form-item>
                </srm-col>
              </srm-row>
              <srm-row>
                <srm-col>
                  <el-form-item
                    :label="$t('contractMod.postcode')"
                    :label-width="formLabelWidth"
                    prop="postcode"
                  >
                    <el-input v-model="form.postcode" />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item
                    :label="$t('contractMod.openingBank')"
                    :label-width="formLabelWidth"
                    prop="openingBank"
                  >
                    <el-input v-model="form.openingBank" />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item
                    :label="$t('vendorMod.username')"
                    :label-width="formLabelWidth"
                    prop="bankAccount"
                  >
                    <el-input v-model="form.bankAccount" />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item
                    :label="$t('contractMod.legalPerson')"
                    :label-width="formLabelWidth"
                    prop="legalPerson"
                  >
                    <el-input v-model="form.legalPerson" />
                  </el-form-item>
                </srm-col>
              </srm-row>
              <srm-row>
                <srm-col>
                  <el-form-item
                    :label="$t('contractMod.entrustedAgent')"
                    :label-width="formLabelWidth"
                    prop="entrustedAgent"
                  >
                    <el-input v-model="form.entrustedAgent" />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item
                    :label="$t('dataConfMod.createdBy')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="form.createdUserName"
                      disabled
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item
                    :label="$t('common.creationTime')"
                    :label-width="formLabelWidth"
                  >
                    <el-date-picker
                      v-model="form.creationDate"
                      type="datetime"
                      disabled
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item
                    :label="$t('common.lastUpdatePeople')"
                    :label-width="formLabelWidth"
                  >
                    <el-input
                      v-model="form.lastUpdatedBy"
                      disabled
                    />
                  </el-form-item>
                </srm-col>
              </srm-row>
              <srm-row>
                <srm-col>
                  <el-form-item
                    :label="$t('bidMod.updateTime')"
                    :label-width="formLabelWidth"
                  >
                    <el-date-picker
                      v-model="form.lastUpdateDate"
                      type="datetime"
                      disabled
                    />
                  </el-form-item>
                </srm-col>
                <srm-col><p /></srm-col>
                <srm-col><p /></srm-col>
                <srm-col><p /></srm-col>
              </srm-row>
            </el-collapse-item>
            <el-collapse-item
              :title="$t('contractMod.definition')"
              name="3"
            >
              <p style="margin: 2px">
                <el-button
                  type="primary"
                  @click="addOneContent"
                >
                  {{
                    $t('common.add')
                  }}
                </el-button>
              </p>
              <el-table
                :data="contractContentList"
                style="width: 100%"
                border
                max-height="2000px"
              >
                <el-table-column
                  align="center"
                  type="index"
                  :label="$t('contractMod.order')"
                  width="60"
                />
                <el-table-column
                  align="center"
                  prop="contractItem"
                  :label="$t('contractMod.contractItem')"
                  width="150"
                >
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.contractItem" />
                  </template>
                </el-table-column>
                <el-table-column
                  align="center"
                  prop="templContent"
                  :label="$t('contractMod.contractContent2')"
                  min-width="250"
                >
                  <template slot-scope="scope">
                    <el-input
                      v-model="scope.row.templContent"
                      type="textarea"
                      :rows="2"
                    />
                  </template>
                </el-table-column>
                <el-table-column
                  align="center"
                  prop="isModify"
                  :label="$t('contractMod.Modifiable')"
                  width="80"
                >
                  <template slot-scope="scope">
                    <el-checkbox
                      v-model="scope.row.isModify"
                      true-label="Y"
                      false-label="N"
                    />
                  </template>
                </el-table-column>
                <el-table-column
                  align="center"
                  prop="isDisplayed"
                  :label="$t('contractMod.WhetherToDisplay')"
                  width="80"
                >
                  <template slot-scope="scope">
                    <el-checkbox
                      v-model="scope.row.isDisplayed"
                      true-label="Y"
                      false-label="N"
                    />
                  </template>
                </el-table-column>
                <el-table-column
                  :label="$t('formula.handle')"
                  width="60"
                >
                  <template slot-scope="scope">
                    <el-button
                      type="text"
                      @click="deleteOneContent(scope.$index, scope.row)"
                    >
                      {{ $t('components.common.delete') }}
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-collapse-item>
            <el-collapse-item
              :title="$t('contractMod.OtherContractRelatedDocuments')"
              name="4"
            >
              <p style="margin: 2px">
                <el-button
                  type="primary"
                  @click="addUploadOne"
                >
                  {{ $t('bidMod.affairsIncreased') }}
                </el-button>
              </p>
              <el-table
                :data="fileList"
                style="width: 100%"
                border
                max-height="250px"
              >
                <el-table-column
                  align="center"
                  type="index"
                  width="50"
                />
                <el-table-column
                  align="center"
                  prop="fileSourceName"
                  :label="$t('contract_mod.fileName')"
                  width="250"
                >
                  <template slot-scope="scope">
                    <SrmCommonFile
                      :extra-data="fileInfo"
                      :default-file="{
                        fileId: scope.row.fileuploadId,
                        fileName: scope.row.fileSourceName
                      }"
                      :readonly="false"
                      @on-change="({file}) => outerHandleUploadSuccess(file,scope.row)"
                    />
                  </template>
                </el-table-column>
                <el-table-column
                  :label="$t('formula.handle')"
                  width="60"
                >
                  <template slot-scope="scope">
                    <el-button
                      type="text"
                      @click="handleDelClick(scope.$index, scope.row)"
                    >
                      {{ $t('components.common.delete') }}
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-collapse-item>
          </el-collapse>
        </el-form>
      </div>
      <c-toolbar>
        <template slot="right">
          <el-button
            @click="cancelBill"
          >
            {{ $t('common.cancel') }}
          </el-button>
          <el-button
            type="primary"
            @click="saveBill"
          >
            {{
              $t('common.submit')
            }}
          </el-button>
        </template>
      </c-toolbar>
    </el-main>
  </el-container>
</template>
<script>
import CToolbar from 'lib@/components/c-toolbar'
import { tabTodoMixin } from '@/utils/mixins'
import MainHeader from 'lib@/components/Table/MainHeader'
import DictSelect from '@/library/components/c-select/dict-select'

export default {
  name: 'ContractTemplateDetail',
  components: {
    MainHeader,
    CToolbar,
    DictSelect
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      fileInfo: {
        fileModular: 'sup',
        fileFunction: 'vendorBiddingManagement',
        fileType: 'images'
      },
      form: {
        templHeadId: null,
        templType: '',
        templName: '',
        templDescription: '',
        owner: '',
        templStatus: '',
        fax: '',
        phone: '',
        signingSite: '',
        postcode: '',
        openingBank: '',
        bankAccount: '',
        legalPerson: '',
        entrustedAgent: ''
      },
      fileList: [],
      contractContentList: [],
      activeDims: ['1', '2', '3', '4'],
      addEditText: this.$attrs.params.flag == 'add' ? '创建合同模板' : '编辑合同模板',
      rules: {
        templName: [{ required: true, message: '请输入模板名称' }],
        templType: [{ required: true, message: '请选择模板类型' }],
        owner: [{ required: true, message: '请输入甲方' }],
        fax: [{ required: true, message: '请输入传真' }],
        phone: [{ required: true, message: '请输入电话' }],
        postcode: [{ required: true, message: '请输入邮编' }],
        openingBank: [{ required: true, message: '请输入开户行' }],
        bankAccount: [{ required: true, message: '请输入账号' }],
        legalPerson: [{ required: true, message: '请输入法定代理人' }],
        entrustedAgent: [{ required: true, message: '请输入委托代理人' }]
      },
      isDisabled: this.$attrs.params.flag == 'edit',
      formLabelWidth: '120px',
      isModify: false
    }
  },
  created () {
    if (this.$attrs.params.flag == 'edit') {
      this.getFormDetail()
    }
  },
  methods: {
    getFormDetail () {
      this.$http({
        url: '/api-cm/template/templHead/getContractTemplDTO',
        method: 'GET',
        params: { templHeadId: this.$attrs.params.row.templHeadId },
        loading: true
      })
        .then((data) => {
          this.form = data.data.templHead || {}
          this.fileList = data.data.fileuploads
          this.contractContentList = data.data.templLines
        })
        .catch((err) => {
          console.log(err)
        })
    },
    addUploadOne () {
      this.fileList.push({
        fileuploadId: '',
        fileSourceName: ''
      })
    },
    outerHandleUploadSuccess (file, row) {
      const { fileId = '', fileName = '' } = file || {}
      row.fileuploadId = fileId.toString()
      row.fileSourceName = fileName
    },
    handleDelClick (index, row) {
      if (row.fileuploadId) {
        this.$http({
          url: '/api-file/file/fileupload/delete',
          method: 'POST',
          params: { id: row.fileuploadId },
          loading: true
        })
          .then((data) => {
            this.fileList.splice(index, 1)
          })
          .catch((err) => {
            console.log(err)
          })
      } else {
        this.fileList.splice(index, 1)
      }
    },
    deleteOneContent (index, row) {
      if (row.templLineId) {
        this.$http({
          url: '/api-cm/template/templLine/deleteTempline',
          method: 'GET',
          params: { templLineId: row.templLineId },
          loading: true
        })
          .then((data) => {
            this.contractContentList.splice(index, 1)
          })
          .catch((err) => {
            console.log(err)
          })
      } else {
        this.contractContentList.splice(index, 1)
      }
    },
    backBill () {
      if (this.$attrs.params.flag == 'edit') {
        this.$emit('tab-remove', 'contractTemplateDetail' + this.$attrs.params.row.templName)
      } else {
        this.$emit('tab-remove', 'contractTemplateDetail')
      }
      this.__setTabTodo('contractTemplateList.getQuerydata')
    },
    addOneContent () {
      this.contractContentList.push({
        templLineId: null,
        orderNum: this.contractContentList.length + 1,
        contractItem: '',
        templContent: '',
        isModify: 'N',
        isDisplayed: 'N'
      })
    },
    cancelBill () {
      if (this.$attrs.params.flag == 'edit') {
        this.$emit('tab-remove', 'contractTemplateDetail' + this.$attrs.params.row.templName)
      } else {
        this.$emit('tab-remove', 'contractTemplateDetail')
      }
      this.__setTabTodo('contractTemplateList.getQuerydata')
    },
    saveBill () {
      this.$refs.form.validate((valid) => {
        if (valid) {
          this.$http({
            url: this.form.templHeadId
              ? '/api-cm/template/templHead/modify'
              : '/api-cm/template/templHead/add',
            method: 'POST',
            data: {
              templHead: this.form,
              templLines: this.contractContentList,
              fileuploads: this.fileList
            },
            loading: true
          })
            .then((data) => {
              this.$message({
                message: '保存成功',
                type: 'success'
              })
              if (this.$attrs.params.flag == 'edit') {
                this.$emit(
                  'tab-remove',
                  'contractTemplateDetail' + this.$attrs.params.row.templName
                )
              } else {
                this.$emit('tab-remove', 'contractTemplateDetail')
              }
              this.__setTabTodo('contractTemplateList.getQuerydata')
            })
            .catch((err) => {
              console.log(err)
            })
        } else {
          return false
        }
      })
    },
    reset () {
      // 重置所有过滤条件
      for (let i in this.form) {
        this.form[i] = ''
      }
    }
  }
}
</script>
<style scoped lang="scss">
.the-contractTemplateDetail-detail {
  .form-container2 {
    padding: 5px;
  }
  .sub_header {
    padding: 4px 11px;
    background: #eee;
  }
  .el-table .el-date-editor {
    width: 135px;
  }
}
</style>
