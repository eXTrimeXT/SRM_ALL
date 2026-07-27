<!-- 附件管理（新） -->
<template>
  <el-container
    class="flex-container"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="formArray"
        form-label-width="100px"
        @getFormData="getQuerydata"
      />
      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <!-- 新增 -->
          <AuthorityButton
            type="primary"
            @click="handleAdd"
          >
            新增
          </AuthorityButton>
          <AuthorityButton @click="handleSave">
            暂存
          </AuthorityButton>
          <AuthorityButton @click="handleEdit">
            编辑
          </AuthorityButton>
          <AuthorityButton @click="handleSubmit">
            提交
          </AuthorityButton>
          <!-- <AuthorityButton @click="handleDelete">
            删除
          </AuthorityButton> -->
          <AuthorityButton @click="handleAbandon">
            废弃
          </AuthorityButton>
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :bigData="true"
        :table-header="tableHeader"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :auto-query="false"
        :url="tableUrl"
        :open-custom-table="true"
        :reserve-selection="true"
        :adeptMeiQl="true"
        row-key="orderNumber"
        :comActive="$attrs['changeTab']"
        :checkbox="true"
        :check-change="selectChange"
      >
        <template #extProjectNo="{ scope }">
          <QuickSearch
            :disabled="!editMode && !scope.row.editable"
            :show-input="scope.row.extProjectNo"
            show-key="extProjectNo"
            :scope-data="scope.row"
            name="npmAbnormalRegSouProject"
            @close-quicksearch="getExtProjectNo"
          />
        </template>
        <template #fileName="{ scope }">
          <SrmCommonFile
            :readonly="!editMode && !scope.row.editable"
            :default-file="{
              fileId: scope.row.fileId,
              fileName: scope.row.fileName
            }"
            @on-change="({file}) => handleUploadSuccess(file,scope.row)"
          />
        </template>
        <template #feedbackTime="{ scope }">
          <el-date-picker
            v-model="scope.row.feedbackTime"
            :disabled="!editMode && !scope.row.editable"
            type="datetime"
            format="yyyy-MM-dd HH:mm:ss"
            value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="选择日期时间"
          />
        </template>
        <template #abnormalTime="{ scope }">
          <el-date-picker
            v-model="scope.row.abnormalTime"
            :disabled="!editMode && !scope.row.editable"
            type="datetime"
            format="yyyy-MM-dd HH:mm:ss"
            value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="选择日期时间"
          />
        </template>
        <template #vendorName="{ scope }">
          <QuickSearch
            v-if="scope.row.projectId"
            :disabled="!editMode && !scope.row.editable"
            :show-input="scope.row.vendorName"
            show-key="vendorName"
            :scope-data="scope.row"
            name="npmAbnormalRegSouVendor"
            :pre-query-data="{ 't.project_id': scope.row.projectId }"
            :multiSelect="true"
            @close-quicksearch="getVendorName"
          />
        </template>
        <template #abnormalType="{ scope }">
          <dict-select
            v-model="scope.row.abnormalType"
            :allow-create="true"
            :disabled="!editMode && !scope.row.editable"
            code="NPM_SOU_ABNORMAL_TYPE"
          />
        </template>
        <template #abnormalDesc="{ scope }">
          <el-input
            v-model="scope.row.abnormalDesc"
            :disabled="!editMode && !scope.row.editable"
          />
        </template>
        <template #handlingResult="{ scope }">
          <el-input
            v-model="scope.row.handlingResult"
            :disabled="!editMode && !scope.row.editable"
          />
        </template>
      </TableView>
    </el-main>
  </el-container>
</template>

<script>
import { parseTime } from '@/utils'
import { tabTodoMixin, tabTodoWatch } from '@/utils/mixins'
import ExportExcel from 'lib@/components/export-excel'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MainHeader from 'lib@/components/Table/MainHeader'
import TableView from 'lib@/components/Table/TableView'
import { transformMQL } from 'lib@/utils/util'
import QuickSearch from 'lib@/components/QuickSearch'
import indexListVue from '@/modulesCus/buyer/configurationData/views/recentProcurement/indexList.vue'
export default {
  name: 'AbnormalDatailList',
  components: {
    FormWrapper,
    MainHeader,
    TableView,
    QuickSearch
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      username: '',
      gridId: 'list',
      editMode: false,
      currentRows: null,
      tableUrl: '/api-sou/api-ql/SccNpmSouAbnormalReg/query',
      pageSize: 15,
      queryParam: {},
      selectList: [],
      formData: {},
      formArray: [
        {
          prop: 'extProjectNo',
          label: '招标项目标号'
        },
        {
          prop: 'souName',
          label: '招标项目名称'
        },
        {
          prop: 'createdFullName',
          label: '创建人'
        },
        {
          prop: 'regStatus',
          label: '状态',
          type: 'dict',
          code: 'NPM_ABNORMAL_REG_STATUS'
        }
      ],
      tableHeader: [
        {
          prop: 'extProjectNo',
          label: '招标项目编号',
          minWidth: '180',
          name: 'npmAbnormalRegSouProject',
          showType: 'slot',
          slot: 'extProjectNo'
        },
        {
          prop: 'souName',
          label: '项目名称',
          minWidth: '180'
        },
        {
          prop: 'feedbackTime',
          label: '反馈时间',
          minWidth: '180',
          showType: 'slot',
          slot: 'feedbackTime'
        },
        {
          prop: 'abnormalTime',
          label: '异常发生时间',
          minWidth: '180',
          showType: 'slot',
          slot: 'abnormalTime'
        },
        {
          prop: 'souPrincipalName',
          label: '招标负责人',
          minWidth: '180'
        },
        {
          prop: 'departmentName',
          label: '招标负责人科室',
          minWidth: '180'
        },
        {
          prop: 'vendorPrincipalName',
          label: '供应商负责人',
          minWidth: '180'
        },
        {
          prop: 'vendorName',
          label: '供应商名称',
          minWidth: '240',
          showType: 'slot',
          slot: 'vendorName'
        },
        {
          prop: 'abnormalType',
          label: '异常类型',
          minWidth: '180',
          showType: 'slot',
          slot: 'abnormalType'
        },
        {
          prop: 'abnormalDesc',
          label: '具体情况描述',
          minWidth: '240',
          showType: 'slot',
          slot: 'abnormalDesc'
        },
        {
          prop: 'handlingResult',
          label: '处理结果',
          minWidth: '240',
          showType: 'slot',
          slot: 'handlingResult'
        },
        {
          prop: 'fileName',
          label: '附件',
          minWidth: '240',
          showType: 'slot',
          slot: 'fileName'
        },
        {
          prop: 'createdFullName',
          label: '创建人',
          minWidth: '180'
        },
        {
          prop: 'creationDate',
          label: '创建时间',
          minWidth: '180'
        },
        {
          prop: 'lastUpdateDate',
          label: '最后更新时间',
          minWidth: '180'
        },
        {
          prop: 'abandonInstruction',
          label: this.$t('cusEntry.bidMod.instruction'),
          minWidth: 150
        }
      ]
    }
  },
  created () {
    this.getQuerydata()
    this.username = this.$store.getters.userInfo.username
  },
  methods: {
    getQuerydata (params = {}) {
      this.queryParam = transformMQL.listPageData({
        type: 'SccNpmSouAbnormalReg',
        action: 'query',
        params
      })
      if (!this.queryParam.payload?.filter?.regStatus) {
        this.queryParam.payload.filter.regStatus = {
          ne: 'ABANDONED'
        }
      }
      this.$nextTick(() => {
        this.editMode = false
        this.$refs[this.gridId].query()
      })
    },
    getExtProjectNo (value, scope) {
      scope.projectId = value?.projectId
      scope.extProjectNo = value?.extProjectNo
      scope.souName = value?.souName
      scope.souPrincipalId = value?.souPrincipalId
      scope.vendorPrincipalNo = value?.vendorPrincipalNo
      scope.souPrincipalName = value?.souPrincipalName
      scope.departmentId = value?.departmentId
      scope.departmentName = value?.departmentName
      scope.vendorPrincipalId = value?.vendorPrincipalId
      scope.vendorPrincipalNo = value?.vendorPrincipalNo
      scope.vendorPrincipalName = value?.vendorPrincipalName
    },
    getVendorName (value, scope) {
      const vendorId = value?.map(item => item.vendorId)?.join(',') || null
      const vendorCode = value?.map(item => item.vendorCode)?.join(',') || null
      const vendorName = value?.map(item => item.vendorName)?.join(',') || null
      scope.vendorId = vendorId
      scope.vendorCode = vendorCode
      scope.vendorName = vendorName
    },
    handleAdd () {
      this.$refs.list.addOneEditableColumn({
        extProjectNo: null,
        souName: null,
        feedbackTime: null,
        abnormalTime: null,
        souPrincipalName: null,
        departmentName: null,
        vendorPrincipalName: null,
        vendorName: null,
        abnormalType: null,
        abnormalDesc: null,
        handlingResult: null,
        fileName: null,
        createdFullName: null,
        creationDate: null,
        lastUpdateDate: null,
        projectId: null,
        editable: true
      })
    },
    selectChange (select) {
      this.selectList = select
    },
    handleEdit () {
      if (this.selectList.length) {
        let data = this.$refs.list.getTableData()
        let idsList = this.selectList.filter(select => select.regStatus === 'DRAFT' && select.createdBy === this.username).map(item => {
          return item.regId
        })
        data.map((item, index) => {
          if (idsList.indexOf(item.regId) != -1) {
            data[index].editable = true
          }
        })
        this.$refs.list.doLayout()
      }
    },
    handleSave () {
      if (!this.selectList.length) {
        this.$message.warning('请选择需要暂存的数据')
        return
      }
      let params = transformMQL.save('SccNpmSouAbnormalReg', this.selectList, 'save')
      this.$http({
        url: '/api-sou/api-ql/SccNpmSouAbnormalReg/save',
        method: 'POST',
        data: params,
        loading: true
      }).then(res => {
        this.getQuerydata()
      })
    },
    handleSubmit () {
      if (!this.selectList.length) {
        this.$message.warning('请选择需要提交的数据')
        return
      }
      let params = transformMQL.save('SccNpmSouAbnormalReg', this.selectList, 'submit')
      this.$http({
        url: '/api-sou/api-ql/SccNpmSouAbnormalReg/submit',
        method: 'POST',
        data: params,
        loading: true
      }).then(res => {
        this.getQuerydata()
      })
    },
    // 废弃
    handleAbandon () {
      const selectList = this.selectList
      if (!selectList.length) {
        this.$message.warning('请选择需要废弃的数据')
        return false
      }
      if (selectList.length > 1) {
        this.$message.warning('只能选择一条数据废弃')
        return false
      }
      if (selectList[0].regStatus === 'ABANDONED') {
        this.$message.warning('此单据已废弃，无需再次废弃')
        return false
      }
      this.$prompt('请填写废弃原因', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputErrorMessage: '请填写废弃原因',
        inputValidator: (value) => {
          if (value) {
            return true
          }
          return false
        }
      }).then(async ({ value }) => {
        const res = await this.$http({
          url: '/api-sou/sccNpmSouAbnormalReg/updateAbandon',
          method: 'POST',
          data: { id: selectList[0].regId, instruction: value },
          headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
          loading: true
        })
        this.$message.success(this.$t('cusEntry.tipMessage.abandonSuccess'))
        this.getQuerydata()
      }).catch(() => {})
    },
    handleDelete () {
      if (!this.selectList.length) {
        this.$message.warning('请选择需要删除的数据')
        return
      }
      let idsList = this.selectList.filter(select => select.regStatus === 'DRAFT').map(item => {
        return item.regId
      })
      let data = this.$refs.list.getTableData()
      let orderNumList = this.selectList.map(item => {
        return item.orderNumber
      })
      data.map((item, index) => {
        if (orderNumList.indexOf(item.orderNumber) != -1 && !item.regId) {
          this.$refs.list.deleteRow(index)
        }
      })
      let array = idsList.filter(item => item !== '' && item != null)
      if (!array.length) {
        // this.$refs.list.doLayout()
        return
      }
      this.$confirm('此操作将删除选中的异常数据, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        let params = transformMQL.save('SccNpmSouAbnormalReg', array, 'delete')
        this.$http({
          url: '/api-sou/api-ql/SccNpmSouAbnormalReg/delete',
          method: 'POST',
          data: params,
          loading: true
        }).then(res => {
          this.getQuerydata()
        })
      })
    },
    handleUploadSuccess (file, row) {
      const { fileId = null, fileName = null } = file || {}
      row.fileId = fileId
      row.fileName = fileName
    }
  }
}
</script>
<style scoped lang="scss">
</style>
