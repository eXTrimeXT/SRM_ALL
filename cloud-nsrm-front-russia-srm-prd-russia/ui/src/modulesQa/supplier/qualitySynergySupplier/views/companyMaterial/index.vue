<template>
  <el-container class="flex-container-notab the_inventory_wrapper" direction="vertical">
    <el-main>
      <FormWrapper :formArray="preArr" :isActive="true" @getFormData="getQuerydata" />
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
        url="/api-qc/api-ql/CompanyItem/query"
      />
      <!-- 弹框区域-->
      <srm-dialog :title="$t('quality.companyMaterial.materialMent')" :close-on-click-modal="false" :visible.sync="dialogFormVisible">
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
            <srm-col :initCol="2">
              <el-form-item :label="$t('common.materialCode')" :label-width="formLabelWidth" prop="itemCode">
                <el-input v-model="form.itemCode" :disabled="!!itemId" />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="2">
              <el-form-item :label="$t('common.materialName')" :label-width="formLabelWidth" prop="itemName">
                <el-input v-model="form.itemName" />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="2">
              <el-form-item :label="$t('quality.companyMaterial.itemLongDesc')" :label-width="formLabelWidth" prop="itemLongDesc">
                <el-input v-model="form.itemLongDesc" />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="2">
              <el-form-item :label="$t('quality.companyMaterial.itemUom')" :label-width="formLabelWidth" prop="itemUom">
                <el-input v-model="form.itemUom" />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="1">
              <el-form-item :label="$t('quality.companyMaterial.comments')" :label-width="formLabelWidth" prop="comments">
                <el-input v-model="form.comments" type="textarea" :rows="3" />
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
import { companyMaterial } from '@/modulesQa/supplier/qualitySynergySupplier/api'
import { transformMQL } from '@/library/utils/util'
import { mapGetters } from 'vuex'

const { deleteById, save } = companyMaterial

export default {
  name: 'CompanyMaterial',
  components: {
    TableView, MainHeader, FormWrapper
  },
  data () {
    return {
      gridId: 'list',
      currentRow: null,
      tableHeader: [],
      tableData: [],
      itemId: '',
      form: {
        itemCode: '',
        itemName: '',
        itemLongDesc: '',
        itemUom: '',
        comments: ''
      },
      rules: {
        itemCode: [{ required: true, message: this.$t('common.pleaseInput') }],
        itemName: [{ required: true, message: this.$t('common.pleaseInput') }],
        itemLongDesc: [{ required: true, message: this.$t('common.pleaseInput') }],
        itemUom: [{ required: true, message: this.$t('common.pleaseInput') }]
      },
      queryParam: {},
      dialogFormVisible: false,
      formLabelWidth: '100px',
      isActive: false,
      preArr: [
        { prop: 'itemCode',
          label: () => this.$t('common.materialCode')
        },
        {
          prop: 'itemName',
          label: () => this.$t('common.materialName')
        },
        {
          prop: 'itemLongDesc',
          label: () => this.$t('quality.companyMaterial.itemLongDesc')
        },
        {
          prop: 'itemUom',
          label: () => this.$t('quality.companyMaterial.itemUom')
        },
        {
          prop: 'comments',
          label: () => this.$t('quality.companyMaterial.comments')
        }
      ]
    }
  },
  computed: {
    ...mapGetters(['userInfo'])
  },
  created () {
    let _this = this
    this.tableHeader = [
      { prop: 'itemCode',
        label: () => this.$t('common.materialCode')
      },
      {
        prop: 'itemName',
        label: () => this.$t('common.materialName')
      },
      {
        prop: 'itemLongDesc',
        label: () => this.$t('quality.companyMaterial.itemLongDesc')
      },
      {
        prop: 'itemUom',
        label: () => this.$t('quality.companyMaterial.itemUom')
      },
      {
        prop: 'comments',
        label: () => this.$t('quality.companyMaterial.comments')
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
    getQuerydata (params = {}) {
      this.queryParam = transformMQL.listGetData('CompanyItem', params, 'lastUpdateDate', undefined, 'query')
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    // 编辑tab
    editTab (type, data) {
      this.dialogFormVisible = true
      if (type == 'add') {
        // 新增
        this.itemId = ''
        Object.keys(this.form).forEach(key => {
          this.form[key] = null
        })
      } else {
        // 修改
        this.itemId = data.itemId
        this.form = JSON.parse(JSON.stringify(data))
      }
      this.form.status = 'Y'
    },
    deleteOne (data) {
      let params = transformMQL.save('CompanyItem', [data.itemId], 'delete')
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
      // 验证form表单
      this.$refs.form.validate(valid => {
        if (valid) {
          let params
          if (this.itemId) {
            params = Object.assign({}, this.form, { itemId: this.itemId })
          } else {
            params = this.form
          }
          params.companyId = this.userInfo.companyId
          let transformParams = transformMQL.save('CompanyItem', [params], 'save')
          // this.form.itemId有值就是修改模式---否则为新增
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
