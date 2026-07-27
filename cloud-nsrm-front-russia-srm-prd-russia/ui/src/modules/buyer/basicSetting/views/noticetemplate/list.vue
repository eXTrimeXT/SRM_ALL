<template>
  <el-container class="flex-container noticetemplate_list_wrapper" direction="vertical">
    <el-main>
      <FormWrapper :form-array="filterConfig" @getFormData="getQuerydata">
        <template #noticeTemplateLan="{ scope }">
          <dict-select v-model="scope.noticeTemplateLan" code="NOTICE_TEMPLATE_LAN" />
        </template>
        <template #noticeTemplateMode="{ scope }">
          <dict-select v-model="scope.noticeTemplateMode" code="NOTICE_TEMPLATE_MODE" />
        </template>
        <template #noticeTemplateValid="{ scope }">
          <dict-select v-model="scope.noticeTemplateValid" code="NOTICE_TEMPLATE_VAILD" />
        </template>
      </FormWrapper>

      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <AuthorityButton
            code="noticetemplate:add"
            type="primary"
            @click="addHandle"
          >
            {{ $t('common.add') }}
          </AuthorityButton>

          <ExportExcel
            page-url="/api-base/base/noticetemplate/listPage"
            :filter-params="queryParam"
            :table-header="tableHeader"
            :dict-codes="dictCodes"
            :timeout="1000000"
            export-mode="front"
            type="default"
            code="noticetemplate:export"
          />
        </template>
      </MainHeader>

      <TableView
        :ref="gridId"
        :table-header="tableHeader"
        :check-change="handleCurrentChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :com-active="$attrs['changeTab']"
        :source="noticetemplate.list"
      />
    </el-main>

    <srm-dialog :title="dialogTitle" size="large" :visible.sync="visible">
      <div class="noticetemplateEdit">
        <el-form ref="form" :model="form" label-position="top" :rules="rules">
          <el-row :gutter="32">
            <el-col :span="8">
              <el-form-item prop="noticeTemplateCode" :label="$t('notice.noticeTemplateCode')">
                <el-input v-model="form.noticeTemplateCode" />
              </el-form-item>
            </el-col>

            <el-col :span="8">
              <el-form-item prop="noticeTemplateLan" :label="$t('notice.noticeTemplateLan')">
                <dict-select v-model="form.noticeTemplateLan" code="NOTICE_TEMPLATE_LAN" />
              </el-form-item>
            </el-col>

            <el-col :span="8">
              <el-form-item prop="noticeTemplateScene" :label="$t('notice.noticeTemplateScene')">
                <el-input v-model="form.noticeTemplateScene" />
              </el-form-item>
            </el-col>

            <el-col :span="8">
              <el-form-item prop="noticeTemplateMode" :label="$t('notice.noticeTemplateMode')">
                <dict-select v-model="form.noticeTemplateMode" code="NOTICE_TEMPLATE_MODE" />
              </el-form-item>
            </el-col>

            <el-col :span="8">
              <el-form-item prop="noticeTemplateTitle" :label="$t('notice.noticeTemplateTitle')">
                <el-input v-model="form.noticeTemplateTitle" />
              </el-form-item>
            </el-col>

            <el-col :span="8">
              <el-form-item prop="noticeTemplateValid" :label="$t('notice.noticeTemplateValid')">
                <dict-select v-model="form.noticeTemplateValid" code="NOTICE_TEMPLATE_VAILD" />
              </el-form-item>
            </el-col>

            <el-col :span="24">
              <el-form-item prop="noticeTemplateContent" :label="$t('notice.noticeTemplateContent')">
                <Tinymce
                  id="tinymceContractMode"
                  v-model="form.noticeTemplateContent"
                  :height="460"
                />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>

      <template #footer class="dialog-footer">
        <el-button @click="cancel">
          {{ $t('common.cancel') }}
        </el-button>
        <el-button type="primary" @click="confirm">
          {{ $t('common.confirm') }}
        </el-button>
      </template>
    </srm-dialog>
  </el-container>
</template>

<script>
import Tinymce from '@/components/Tinymce'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import ExportExcel from 'lib@/components/export-excel'
import { noticetemplate } from 'modb@/basicSetting/api/basicSetting'

export default {
  name: 'NoticetemplateList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    ExportExcel,
    Tinymce
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      noticetemplate: noticetemplate,
      name: 'noticetemplateList',
      tableName: 'noticetemplateTable',
      pageSize: 15,
      gridId: 'list',
      currentRows: [],
      visible: false,
      mode: 'add',
      dialogTitle: this.$t('notice.dialogTitle'),
      form: {
        noticeTemplateId: '',
        noticeTemplateCode: '',
        noticeTemplateLan: '',
        noticeTemplateScene: '',
        noticeTemplateMode: '',
        noticeTemplateTitle: '',
        noticeTemplateValid: '',
        noticeTemplateContent: '',
        creationDate: '',
        createdBy: '',
        createdId: '',
        createdByIp: '',
        lastUpdatedId: '',
        lastUpdatedBy: '',
        lastUpdateDate: '',
        lastUpdatedByIp: '',
        tenantId: '',
        version: '',
        attributeCategory: 'ATTRIBUTE_CATEGORY'
      },
      rules: {},
      dictCodes: {
        noticeTemplateLan: 'NOTICE_TEMPLATE_LAN',
        noticeTemplateMode: 'NOTICE_TEMPLATE_MODE',
        noticeTemplateValid: 'NOTICE_TEMPLATE_VAILD'
      },
      filterParams: {},
      tableHeader: [
        {
          prop: 'noticeTemplateCode',
          label: () => this.$t('notice.noticeTemplateCode'),
          minWidth: 100,
          unsortable: true
        },
        {
          prop: 'noticeTemplateLan',
          label: () => this.$t('notice.noticeTemplateLan'),
          minWidth: 100,
          dataType: 'dict',
          code: 'NOTICE_TEMPLATE_LAN',
          unsortable: true
        },
        {
          prop: 'noticeTemplateScene',
          label: () => this.$t('notice.noticeTemplateScene'),
          minWidth: 100,
          unsortable: true
        },
        {
          prop: 'noticeTemplateMode',
          label: () => this.$t('notice.noticeTemplateMode'),
          minWidth: 100,
          dataType: 'dict',
          code: 'NOTICE_TEMPLATE_MODE',
          unsortable: true
        },
        {
          prop: 'noticeTemplateTitle',
          label: () => this.$t('notice.noticeTemplateTitle'),
          minWidth: 100,
          unsortable: true
        },
        {
          prop: 'noticeTemplateValid',
          label: () => this.$t('notice.noticeTemplateValid'),
          minWidth: 100,
          dataType: 'dict',
          code: 'NOTICE_TEMPLATE_VAILD',
          unsortable: true
        },
        {
          prop: 'noticeTemplateContent',
          label: () => this.$t('notice.noticeTemplateContent'),
          minWidth: 100,
          unsortable: true
        },
        {
          prop: 'creationDate',
          label: () => this.$t('common.creationDate'),
          minWidth: 100,
          unsortable: true
        },
        {
          prop: 'createdBy',
          label: () => this.$t('common.creator'),
          minWidth: 100,
          unsortable: true
        },
        {
          prop: 'lastUpdatedBy',
          label: () => this.$t('common.lastUpdatePeople'),
          minWidth: 100,
          unsortable: true
        },
        {
          prop: 'operation',
          label: () => this.$t('common.operation'),
          showType: 'buttons',
          btnStyle: 'text',
          fixed: 'right',
          width: 130,
          buttons: [
            {
              code: 'noticetemplate:update',
              callback: row => this.editHandle(row),
              formattor: () => this.$t('common.edit')
            },
            {
              code: 'noticetemplate:delete',
              callback: row => this.deleteHandle(row),
              formattor: () => this.$t('common.delete')
            }
          ]
        }
      ],
      filterConfig: [
        { prop: 'noticeTemplateCode', label: () => this.$t('notice.noticeTemplateCode') },
        { prop: 'noticeTemplateLan', label: () => this.$t('notice.noticeTemplateLan'), type: 'slot', slot: 'noticeTemplateLan' },
        { prop: 'noticeTemplateScene', label: () => this.$t('notice.noticeTemplateScene') },
        { prop: 'noticeTemplateMode', label: () => this.$t('notice.noticeTemplateMode'), type: 'slot', slot: 'noticeTemplateMode' },
        { prop: 'noticeTemplateTitle', label: () => this.$t('notice.noticeTemplateTitle') },
        { prop: 'noticeTemplateValid', label: () => this.$t('notice.noticeTemplateValid'), type: 'slot', slot: 'noticeTemplateValid' }
      ],
      queryParam: {}
    }
  },
  created () {
    this.defaultTableHeader = this.tableHeader
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    cancel () {
      this.visible = false
    },
    noticeSaveOrUpdate (name, query) {
      noticetemplate[name](query).then((res) => {
        this.$message({
          type: 'success',
          message: res.message
        })
        this.visible = false
        this.getQuerydata()
      })
    },
    confirm () {
      this.$refs.form.validate((result) => {
        if (result) {
          const flag = this.mode
          // 新增时不用提交主键值
          const { noticeTemplateId, ...rest } = this.form
          this.noticeSaveOrUpdate(flag, flag === 'add' ? rest : this.form)
        }
      })
    },
    getQuerydata (params) {
      this.queryParam = params
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    deleteHandle (row) {
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          noticetemplate.delete(row.noticeTemplateId).then(res => {
            this.$message.success(res.message)
            this.getQuerydata()
          })
        })
    },
    addHandle () {
      for (const i in this.form) {
        this.form[i] = ''
      }
      this.dialogTitle = this.$t('notice.dialogTitleAdd')
      this.visible = true
      this.mode = 'add'
    },
    editHandle (row) {
      this.form = JSON.parse(JSON.stringify(row))
      this.dialogTitle = this.$t('notice.dialogTitleEdit')
      this.visible = true
      this.mode = 'update'
    },
    handleCurrentChange (val) {
      this.currentRows = val
    }
  }
}
</script>
