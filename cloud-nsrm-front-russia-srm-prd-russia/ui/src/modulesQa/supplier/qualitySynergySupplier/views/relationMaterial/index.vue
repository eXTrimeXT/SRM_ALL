<template>
  <el-container class="flex-container-notab the_customerMaterial_wrapper" direction="vertical">
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
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :current-change="handleCurrentChange"
        :preQueryData="queryParam"
        :adeptMeiQl="true"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        url="/api-qc/api-ql/ItemRelation/queryMaterialNoPage"
      />
      <!--弹框区域-->
      <srm-dialog :close-on-click-modal="false" :title="$t('quality.kehuwuliao')" :visible.sync="dialogFormVisible">
        <el-form
          ref="form"
          :model="form"
          class="form-incontainer form-fill-style"
          :rules="rules"
          :show-message="false"
          label-width="80px"
          label-position="top"
        >
          <srm-row :gutter="24">
            <srm-col :initCol="3">
              <el-form-item :label="$t('quality.project.itemNameCompany')" :label-width="formLabelWidth" prop="itemId">
                <QuickSearch
                  :show-input="form.itemName"
                  show-key="itemName"
                  :scope-data="form"
                  name="scc_base_company_item"
                  @close-quicksearch="getCompanyItemObj"
                />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="3">
              <el-form-item :label="$t('quality.project.cusItemName')" :label-width="formLabelWidth" prop="materialName">
                <QuickSearch
                  :show-input="form.cusItemName"
                  show-key="materialName"
                  :scope-data="form"
                  name="scc_base_material_item_display"
                  @close-quicksearch="getItemObj"
                />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="3">
              <el-form-item :label="$t('quality.cusOrgName')" :label-width="formLabelWidth" prop="cusOrgId">
                <OrganizationSelector
                  ref="organizationSelector"
                  v-model="form.cusOrgId"
                  :parent-id="-1"
                  node-type="OU"
                  :scope="form"
                  :placeholder="$t('common.pleaseSelect')"
                  @select="selectHandler"
                />
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
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import QuickSearch from 'lib@/components/QuickSearch'
import OrganizationSelector from 'lib@/components/organization-selector'
import { transformMQL } from '@/library/utils/util'
import { relationMaterial } from '@/modulesQa/supplier/qualitySynergySupplier/api'
import { mapGetters } from 'vuex'

const { save, deleteById } = relationMaterial

export default {
  name: 'RelationMaterial',
  components: {
    TableView, MainHeader, FormWrapper, QuickSearch, OrganizationSelector
  },
  data () {
    return {
      gridId: 'list',
      currentRow: null,
      tableHeader: [],
      tableData: [],
      form: {
        itemId: '',
        itemCode: '',
        itemName: '',
        cusOrgId: '',
        cusOrgCode: '',
        cusOrgName: '',
        cusItemId: null, // 客户物料Id
        cusItemCode: null,
        cusItemName: null,
        status: 'Y',
        companyId: null,
        customerId: 1 // 默认客户id为1
      },
      rules: {
        itemCode: [{ required: true, message: this.$t('common.pleaseInput') }],
        itemName: [{ required: true, message: this.$t('common.pleaseInput') }],
        cusItemCode: [{ required: true, message: this.$t('common.pleaseInput') }],
        cusItemName: [{ required: true, message: this.$t('common.pleaseInput') }]
      },
      dialogFormVisible: false,
      formLabelWidth: '100px',
      customerName: '',
      cusOrgName: '',
      preArr: [
        { prop: 'itemId',
          label: () => this.$t('quality.project.itemNameCompany'),
          type: 'quicksearch',
          showKey: 'itemName',
          propKey: 'itemId',
          name: 'scc_base_company_item'
        },
        { prop: 'cusItemId',
          label: () => this.$t('quality.project.cusItemName'),
          type: 'quicksearch',
          showKey: 'materialName',
          propKey: 'materialId',
          name: 'scc_base_material_item'
        }
      ],
      queryParam: {}
    }
  },
  computed: {
    ...mapGetters(['userInfo'])
  },
  created () {
    this.tableHeader = [
      { prop: 'itemCode',
        label: () => this.$t('quality.project.itemCode')
      },
      {
        prop: 'itemName',
        label: () => this.$t('quality.project.itemNameCompany')
      },
      {
        prop: 'cusItemCode',
        label: () => this.$t('quality.productCode')
      },
      {
        prop: 'cusItemName',
        label: () => this.$t('quality.project.cusItemName')
      },
      {
        prop: 'cusOrgName',
        label: () => this.$t('quality.cusOrgName')
      },
      {
        prop: 'operation',
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
    selectHandler (node, value) {
      this.form.cusOrgId = node ? node.organizationId : null
      this.form.cusOrgCode = node ? node.organizationCode : null
      this.form.cusOrgName = node ? node.organizationName : null
    },
    getItemObj (val, data) {
      this.form.cusItemId = val ? val.materialId : null
      this.form.cusItemCode = val ? val.materialCode : null
      this.form.cusItemName = val ? val.materialName : null
    },
    getCompanyItemObj (val, data) {
      this.form.itemId = val ? val.itemId : null
      this.form.itemCode = val ? val.itemCode : null
      this.form.itemName = val ? val.itemName : null
    },
    getQuerydata (params = {}) {
      Object.keys(params).forEach(key => {
        if (params[key]) params[key] = params[key].toString()
      })
      this.queryParam = transformMQL.listGetData('ItemRelation', params, 'lastUpdateDate', undefined, 'queryMaterialNoPage')
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    // 编辑tab
    editTab (type, data) {
      this.dialogFormVisible = true
      if (type == 'add') {
        // 新增
        this.relationId = ''
        Object.keys(this.form).forEach(key => {
          this.form[key] = null
        })
        this.form.status = 'Y'
      } else {
        // 修改
        this.relationId = data.relationId
        this.form = JSON.parse(JSON.stringify(data))
      }
      this.form.companyId = this.userInfo.companyId
      this.form.customerId = 1
    },
    addOne () {
      // 验证form表单
      this.$refs.form.validate(valid => {
        if (valid) {
          // this.form.factoryId有值就是修改模式---否则为新增
          let transformParams = transformMQL.save('ItemRelation', [this.form], 'save')
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
    deleteOne (data) {
      let params = transformMQL.save('ItemRelation', [data.relationId], 'delete')
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
    handleCurrentChange (val) {
      this.currentRow = val
    }
  }
}
</script>
<style scoped lang="scss">

</style>
