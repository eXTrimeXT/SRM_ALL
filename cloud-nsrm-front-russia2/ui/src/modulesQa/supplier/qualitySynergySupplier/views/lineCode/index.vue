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
        url="/api-qc/api-ql/LineCode/query"
      />
      <!--弹框区域-->
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
          <srm-row>
            <srm-col :initCol="2">
              <el-form-item :label="$t('quality.lineCode')" prop="lineCode">
                <!-- <el-select v-model="form.lineCode" @change="changeLineCode">
                  <el-option v-for="(item,index) in lineCodeList" :key="index" :value="item.value" :label="item.label" />
                </el-select> -->
                <el-input v-model="form.lineCode" />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="2">
              <el-form-item :label="$t('quality.lineCodeQa.productionEquipment')" label-width="150px" prop="productionEquipment">
                <el-input v-model="form.productionEquipment" />
              </el-form-item>
            </srm-col>
            <srm-col :initCol="2">
              <el-form-item :label="$t('quality.lineCodeQa.testEquipment')" label-width="150px" prop="testEquipment">
                <el-input v-model="form.testEquipment" />
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
import { transformMQL } from '@/library/utils/util'
import { lineCode, qualityProject } from '@/modulesQa/supplier/qualitySynergySupplier/api'
import { mapGetters } from 'vuex'

const { save } = lineCode
const { pageCondition } = qualityProject

export default {
  name: 'LineCode',
  components: {
    TableView, MainHeader, FormWrapper
  },
  data () {
    return {
      gridId: 'list',
      currentRow: null,
      tableHeader: [],
      tableData: [],
      codeId: '',
      form: {
        lineCode: '',
        lineName: '',
        productionEquipment: '',
        testEquipment: '',
        status: 'Y',
        companyId: null
      },
      rules: {
        lineCode: [{ required: true, message: this.$t('common.pleaseInput') }]
      },
      queryParam: {},
      dialogFormVisible: false,
      lineCodeList: [],
      preArr: [
        { prop: 'lineCode',
          label: () => this.$t('quality.lineCode'),
          type: 'select',
          options: []
        },
        { prop: 'status',
          label: () => this.$t('quality.lineCodeQa.sratus'),
          type: 'select',
          options: [
            {
              label: this.$t('quality.lineCodeQa.effective'),
              value: 'Y'
            },
            {
              label: this.$t('quality.lineCodeQa.invalid'),
              value: 'N'
            }
          ]
        }
      ]
    }
  },
  computed: {
    ...mapGetters([
      'userInfo'
    ])
  },
  created () {
    let _this = this
    this.tableHeader = [
      { prop: 'lineCode',
        label: () => this.$t('quality.lineCode')
      },
      {
        prop: 'productionEquipment',
        label: () => this.$t('quality.lineCodeQa.productionEquipment')
      },
      {
        prop: 'testEquipment',
        label: () => this.$t('quality.lineCodeQa.testEquipment')
      },
      {
        prop: 'status',
        label: () => this.$t('quality.lineCodeQa.sratus'),
        formattor: (val) => {
          return val === 'Y' ? this.$t('quality.lineCodeQa.effective') : this.$t('quality.lineCodeQa.invalid')
        }
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
            disabled: function (row) {
              return row.status === 'N'
            },
            callback: function (row) {
              this.editTab('change', row)
            }.bind(this),
            formattor: (val) => {
              return this.$t('common.edit')
            }
          },
          {
            btnStyle: 'text',
            show: function (row) {
              return row.status === 'Y'
            },
            callback: function (row) {
              this.$confirm(this.$t('quality.lineCodeQa.invalidThisData'), this.$t('common.tips'), { // 提示跳转登录
                type: 'warning'
              }).then(() => {
                this.changeStatus(row)
              })
            }.bind(this),
            formattor: (val) => {
              return this.$t('quality.lineCodeQa.toInvalid')
            }
          },
          {
            btnStyle: 'text',
            show: function (row) {
              return row.status === 'N'
            },
            callback: function (row) {
              this.$confirm(this.$t('quality.lineCodeQa.toEffectComfirm'), this.$t('common.tips'), { // 提示跳转登录
                type: 'warning'
              }).then(() => {
                this.changeStatus(row)
              })
            }.bind(this),
            formattor: (val) => {
              return this.$t('quality.lineCodeQa.toEffect')
            }
          }
        ]
      }
    ]
    this.getQuerydata()
    this.getPageCondition()
  },
  methods: {
    getPageCondition () {
      let transformParams = transformMQL.save('spcMonitorList', {}, 'pageCondition')
      pageCondition(transformParams).then(response => {
        const data = response.data.records[0]
        let lineCodeList = []
        // 产线下拉
        Object.keys(data.lineCodeMap).forEach(item => {
          let newObj = {}
          newObj.label = item
          newObj.value = data.lineCodeMap[item]
          lineCodeList.push(newObj)
        })
        for (let item of this.preArr) {
          if (item.prop === 'lineCode') {
            item.options = lineCodeList
            break
          }
        }
        this.lineCodeList = lineCodeList
      })
    },
    getQuerydata (params = {}) {
      this.queryParam = transformMQL.listGetData('LineCode', params, 'lastUpdateDate', undefined, 'query')
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    // 编辑tab
    editTab (type, data) {
      this.dialogFormVisible = true
      if (type == 'add') {
        // 新增
        this.codeId = ''
        Object.keys(this.form).forEach(key => {
          this.form[key] = null
        })
        this.form.status = 'Y'
      } else {
        // 修改
        this.codeId = data.codeId
        this.form = JSON.parse(JSON.stringify(data))
        console.log('form', this.form)
      }
    },
    changeStatus (data) {
      let status = data.status === 'Y' ? 'N' : 'Y'
      let transformParams = transformMQL.save('LineCode', [{ ...data, status }], 'save')
      save(transformParams).then(data => {
        this.$message({
          message: this.$t('common.success'),
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
          // this.form.codeId有值就是修改模式---否则为新增
          this.form.companyId = this.userInfo.companyId
          let transformParams = transformMQL.save('LineCode', [this.form], 'save')
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
