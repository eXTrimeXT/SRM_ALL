<template>
  <el-container class="flex-container-notab the_inventory_wrapper" direction="vertical">
    <el-main>
      <FormWrapper :formArray="preArr" @getFormData="getQuerydata" />
      <MainHeader>
        <template slot="left">
          <el-button
            type="primary"
            @click="editTab('add')"
          >
            {{ $t('common.add') }}
          </el-button>
          <!-- <MImport
            :title="$t('common.import')"
            up-load-url="/api-qc/baseOrg/importCustomerRelationship"
            :extra-data="extraData"
            type="default"
            @downloadTemplate="downloadTemplate"
            @handleSuccess="handleSuccess"
          /> -->
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-header="tableHeader"
        :preQueryData="queryParam"
        :adeptMeiQl="true"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        url="/api-qc/api-ql/Customer/query"
      />
      <!-- 弹框区域-->
      <srm-dialog :title="$t('route.customRelationQa')" :close-on-click-modal="false" :visible.sync="dialogFormVisible">
        <el-form
          ref="form"
          :model="form"
          class="form-incontainer form-fill-style"
          :rules="rules"
          :show-message="false"
          label-width="80px"
          label-position="top"
        >
          <srm-row>
            <srm-col :initCol="2">
              <el-form-item :label="$t('quality.customerOrgName')" :label-width="formLabelWidth" prop="cusOrgId">
                <OrganizationSelector
                  ref="orgSelector"
                  v-model="form.cusOrgId"
                  :placeholder="$t('common.pleaseSelect')"
                  :disabled="false"
                  :parent-id="-1"
                  node-type="OU"
                  @select="ouSelectHandler"
                />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="2">
              <el-form-item :label="$t('quality.cusCompanyCode')" :label-width="formLabelWidth" prop="cusCompanyCode">
                <el-input v-model="form.cusCompanyCode" />
              </el-form-item>
            </srm-col>
          </srm-row>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="dialogFormVisible = false">
            {{ $t('common.cancel') }}
          </el-button>
          <el-button type="primary" @click="addOne">
            {{ $t('common.confirm') }}
          </el-button>
        </div>
      </srm-dialog>
    </el-main>
  </el-container>
</template>
<script>
import MImport from 'lib@/components/import'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import OrganizationSelector from 'lib@/components/organization-selector'
import { parseTime } from '@/utils'
import { getToken } from '@/utils/auth'
import { customRelation } from '@/modulesQa/supplier/qualitySynergySupplier/api'
import { downloadFileLink } from 'lib@/utils/file'
import { transformMQL } from '@/library/utils/util'
import { mapGetters } from 'vuex'

const { deleteById, save } = customRelation

export default {
  name: 'CustomRelation',
  components: {
    TableView, MainHeader, FormWrapper, MImport, OrganizationSelector
  },
  data () {
    return {
      extraData: {
        fileModular: 'qc',
        fileFunction: 'customRelation',
        fileType: 'excel'
      },
      headers: {
        Authorization: getToken()
      },
      gridId: 'list',
      currentRow: null,
      tableHeader: [],
      tableData: [],
      statusList: [{
        value: 'Y',
        label: this.$t('common.enable')
      }, {
        value: 'N',
        label: this.$t('common.blockUp')
      }],
      customerId: '',
      form: {
        companyId: null,
        cusOrgId: '',
        cusOrgCode: '',
        cusOrgName: '',
        cusCompanyCode: '',
        customerCode: 'huachuang',
        customerName: '华创',
        status: 'Y'
      },
      rules: {
        cusCompanyCode: [{ required: true, message: this.$t('common.pleaseInput') }],
        cusOrgId: [{ required: true, message: this.$t('common.pleaseSelect') }]
      },
      queryParam: {},
      dialogFormVisible: false,
      formLabelWidth: '100px',
      isActive: false,
      preArr: [
        { prop: 'cusOrgId',
          label: () => this.$t('quality.customerOrgName'),
          type: 'OUorganizationSelector'
        },
        {
          prop: 'cusCompanyCode',
          label: () => this.$t('quality.cusCompanyCode')
        }
      ]
    }
  },
  computed: {
    ...mapGetters(['userInfo'])
  },
  created () {
    console.log('uesrInfo', this.$store.getters.userInfo)
    let _this = this
    this.tableHeader = [
      { prop: 'cusOrgCode',
        label: () => this.$t('quality.cusOrgCode')
      },
      {
        prop: 'cusOrgName',
        label: () => this.$t('quality.cusOrgName')
      },
      {
        prop: 'cusCompanyCode',
        label: () => this.$t('quality.cusCompanyCode'),
        width: 130
      },
      {
        prop: 'createdBy',
        label: () => this.$t('common.creator')
      },
      {
        prop: 'creationDate',
        label: () => this.$t('common.creationTime'),
        formattor: (val) => {
          return val ? parseTime(val, '{y}-{m}-{d}') : null
        }
      },
      { prop: 'operation',
        label: () => this.$t('common.operation'),
        width: 105,
        showType: 'buttons',
        fixed: 'right',
        buttons: [
          {
            btnStyle: 'text',
            callback: function (row) {
              this.editTab('change', row)
            }.bind(this),
            formattor: (val) => {
              return this.$t('common.edit')
            }
          },
          {
            btnStyle: 'text',
            callback: function (row) {
              this.$confirm(this.$t('common.confirmDelete'), this.$t('common.tips'), { // 提示跳转登录
                type: 'warning'
              }).then(() => {
                this.deleteOne(row)
              })
            }.bind(this),
            formattor: (val) => {
              return this.$t('common.delete')
            }
          }
        ]
      }
    ]
    this.getQuerydata()
  },
  methods: {
    downloadTemplate () {
      downloadFileLink('/api-qc/base/customer/importExcelTemplate',
        this.$t('quality.customRelationImportTemplate')
      ).catch(
        () => {
          this.$message.error(this.$t('components.eio.downloadFail'))
        }
      )
    },
    ouSelectHandler (node) {
      console.log('node', node)
      this.form.cusOrgId = node ? node.organizationId : null
      this.form.cusOrgCode = node ? node.organizationCode : null
      this.form.cusOrgName = node ? node.organizationName : null
    },
    handleSuccess (response, file, fileList) {
      this.getQuerydata()
    },
    getQuerydata (params = {}) {
      this.queryParam = transformMQL.listGetData('Customer', params, 'lastUpdateDate', undefined, 'query')
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    // 编辑tab
    editTab (type, data) {
      this.dialogFormVisible = true
      if (type == 'add') {
        // 新增
        this.customerId = ''
        Object.keys(this.form).forEach(key => {
          this.form[key] = null
        })
        let { companyCode, companyName, companyId } = this.userInfo
        this.form.cusCompanyCode = companyCode
        this.form.cusCompanyName = companyName
        this.form.companyId = companyId
      } else {
        // 修改
        this.customerId = data.customerId
        this.form = JSON.parse(JSON.stringify(data))
      }
      this.form.status = 'Y'
    },
    deleteOne (data) {
      let params = transformMQL.save('Customer', [data.customerId], 'delete')
      deleteById(params).then(data => {
        this.$message({
          message: this.$t('common.successDelete'),
          type: 'success'
        })
        this.$refs[this.gridId].query()
      }).catch(err => {
        console.log(err)
      })
    },
    addOne () {
      this.form.customerCode = 'huachuang'
      this.form.customerName = '华创'
      // 验证form表单
      this.$refs.form.validate(valid => {
        if (valid) {
          let params
          if (this.customerId) {
            params = Object.assign({}, this.form, { customerId: this.customerId })
          } else {
            params = this.form
          }
          let transformParams = transformMQL.save('Customer', [params], 'save')
          save(transformParams).then(data => {
            this.dialogFormVisible = false
            this.$message({
              message: this.$t('common.successSave'),
              type: 'success'
            })
            this.$refs[this.gridId].query()
          }).catch(err => {
            console.log(err)
          })
        } else {
          return false
        }
      })
    },
    handleCurrentChange (val) {
      this.currentRow = val
    }
  }
}
</script>
<style scoped lang="scss">

</style>
