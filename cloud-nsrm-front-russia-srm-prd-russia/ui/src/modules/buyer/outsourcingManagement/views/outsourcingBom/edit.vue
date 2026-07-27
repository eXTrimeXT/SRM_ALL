<template>
  <el-container
    class="bomheadEdit"
    direction="vertical"
  >
    <el-main>
      <div class="form-container">
        <el-form
          ref="form"
          :model="form"
          :rules="rules"
        >
          <el-row :gutter="32">
            <el-col :span="6">
              <el-form-item
                prop="materialCode"
                label="总成料号"
              >
                <QuickSearch
                  :show-input="form.materialCode"
                  show-key="companyName"
                  :scope-data="form"
                  :pre-query-data="{ 't.MATERIAL_ATTR': 'OUTSOURCING' }"
                  :map-value="[
                    'materialId,materialId',
                    'materialCode,materialCode',
                    'materialName,materialName',
                  ]"
                  name="scc_base_material_item"
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                prop="materialName"
                label="物料名称"
              >
                <el-input
                  v-model="form.materialName"
                  disabled
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                prop="orgId"
                label="业务实体"
              >
                <OrganizationSelector
                  ref="organizationSelector1"
                  v-model="form.orgId"
                  :parent-id="-1"
                  node-type="OU"
                  :placeholder="$t('common.pleaseSelect')"
                  @select="selectHandler1"
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                prop="organizationId"
                label="库存组织"
              >
                <OrganizationSelector
                  ref="organizationSelector2"
                  v-model="form.organizationId"
                  :parent-id="form.orgId"
                  node-type="INV"
                  :placeholder="$t('common.pleaseSelect')"
                  auto-select-when-one-item
                  @select="selectHandler2"
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item
                prop="status"
                label="是否生效"
              >
                <!-- <el-input v-model="form.status"/> -->
                <el-select
                  v-model="form.status"
                  :placeholder="$t('common.pleaseSelect')"
                >
                  <el-option
                    v-for="item in statuss"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>
      <el-collapse v-model="activeLine">
        <el-collapse-item
          title="明细"
          name="0"
        >
          <el-container
            class="flex-container"
            style="height: 300px;"
          >
            <el-main>
              <div style="padding: 12px 0;">
                <el-button
                  class="detail-pbtn"
                  type="primary"
                  @click="BomLineAddLine"
                >
                  新增
                </el-button>
              </div>
              <BaseTable
                ref="BomLineTable"
                :columns="BomLineColumns"
                columns-name="BomLineColumns"
                :data-source="BomLineDataSource"
                :initialize="false"
                row-key="bomLineId"
                border
                @asyncGetRealDataSource="BomLineAsyncGetRealDataSource"
              >
                <template #materialCode="{ scope }">
                  <QuickSearch
                    :show-input="scope.row.materialCode"
                    show-key="companyName"
                    :scope-data="scope.row"
                    :map-value="[
                      'materialId,materialId',
                      'materialCode,materialCode',
                      'materialName,materialName',
                    ]"
                    name="scc_base_material_item"
                  />
                </template>
                <template #materialName="{ scope }">
                  <el-input v-model="scope.row.materialName" />
                </template>
                <template #baseMaterialNum="{ scope }">
                  <el-input v-model="scope.row.baseMaterialNum" />
                </template>
              </BaseTable>
            </el-main>
          </el-container>
        </el-collapse-item>
      </el-collapse>
      <CToolbar>
        <template #right>
          <el-button
            @click="cancelBill"
          >
            取消
          </el-button>
          <el-button
            type="primary"
            :disabled="readOnly"
            @click="save"
          >
            确认
          </el-button>
        </template>
      </CToolbar>
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
  import axios from 'axios'
  import { sysPrefix } from '@/config/ipConfig'
  import { getToken } from '@/utils/auth'
  import QuickSearch from 'lib@/components/QuickSearch'
  import OrganizationSelector from 'lib@/components/organization-selector'
  import { bomApi } from 'modb@/outsourcingManagement/api'
  import { getMenuInfo } from '@/utils/menu-auth'

  export default {
    name: 'BomheadEdit',
    components: {
      MainHeader,
      CToolbar,
      BaseTable,
      MImport,
      QuickSearch,
      OrganizationSelector
    },
    mixins: [tabTodoMixin],
    data () {
      return {
        BomLineRealDataSource: [],
        BomLineDataSource: [],
        activeLine: [
          '0'
        ],
        statuss: [
          {
            value: 'Y',
            label: '是'
          },
          {
            value: 'N',
            label: '否'
          }
        ],
        BomLineColumns: [
          {
            attrs: {
              prop: 'materialCode',
              label: '委外组件编码'
            },
            slot: 'materialCode',
            rules: { required: true, message: '必填' }
          },
          {
            attrs: {
              prop: 'materialName',
              label: '委外组件名称'
            },
            slot: 'materialName',
            rules: { required: true, message: '必填' }
          },
          {
            attrs: {
              prop: 'baseMaterialNum',
              label: '数量'
            },
            slot: 'baseMaterialNum',
            rules: { required: true, message: '必填' }
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
          materialCode: null,
          materialName: null,
          orgId: null,
          orgName: null,
          orgCode: null,
          organizationId: null,
          organizationName: null,
          organizationCode: null,
          status: null
        },
        rules: {
          materialCode: [{ required: true, message: '必填' }],
          materialName: [{ required: true, message: '必填' }],
          orgName: [{ required: true, message: '必填' }],
          orgCode: [{ required: true, message: '必填' }],
          orgId: [{ required: true, message: '必填' }],
          organizationName: [{ required: true, message: '必填' }],
          organizationCode: [{ required: true, message: '必填' }],
          organizationId: [{ required: true, message: '必填' }],
          status: [{ required: true, message: '必填' }]
        },
        readOnly: false
      }
    },
    computed: {},
    watch: {},
    created () {
    },
    mounted () {
      const { flag, row, readOnly = false } = this.$attrs.params
      this.readOnly = readOnly
      if (flag === 'edit') {
        this.getDetail()
      }
    },
    methods: {
      selectHandler1 (node, value, scope) {
        this.form.orgId = node ? node.organizationId : null
        this.form.orgCode = node ? node.organizationCode : null
        this.form.orgName = node ? node.organizationName : null
        // 清空库存组织
        this.form.organizationId = null
        this.form.organizationCode = null
        this.form.organizationName = null
        this.$refs.organizationSelector2.clearOptions()
      },
      selectHandler2 (node, value, scope) {
        this.form.organizationId = node ? node.organizationId : null
        this.form.organizationCode = node ? node.organizationCode : null
        this.form.organizationName = node ? node.organizationName : null
      },
      getDetail () {
        bomApi.getById(this.$attrs.params.row.bomHeadId).then(res => {
          const {
            bomLineList, ...rest
          } = res.data
          this.form = rest
          this.BomLineDataSource = bomLineList
        })
      },
      BomLineDownloadTemplate () {
        downloadFileLink(
          '/api-sup/sup/bom/exportBomLineExcelTemplate',
          '导入模板.xlsx'
        ).catch(() => {
          this.$message.error('下载失败')
        })
      },
      BomLineExportExcel () {
        let menuInfo = getMenuInfo()
        axios({
          method: 'POST',
          url: `${sysPrefix()}/api-sup/sup/bom/exportBomLineExcel`,
          timeout: this.timeout,
          headers: {
            Authorization: 'Bearer ' + getToken(),
            'X-Fun-Info': menuInfo.secretKey
          },
          data: { id: this.$attrs.params.row.quotaHeadId },
          responseType: 'arraybuffer'
        })
          .then(response => {
            console.log(response)
            const { data } = response
            if (response.headers['content-type'].startsWith('application/json')) {
              let enc = new TextDecoder('utf-8')
              let res = JSON.parse(enc.decode(new Uint8Array(data))) // 转化成json对象
              throw new Error(res.message)
            }
            const blob = new Blob([data])
            const disposition = response.headers['content-disposition'] || ''
            const filename = decodeURIComponent(disposition.split('=')[1])
            const url = window.URL.createObjectURL(blob) // URL.createObjectURL(object)表示生成一个File对象或Blob对象
            let dom = document.createElement('a') // 设置一个隐藏的a标签，href为输出流，设置download
            dom.style.display = 'none'
            dom.href = url
            dom.rel = 'noopener'
            dom.setAttribute('download', filename || `${this.fileName}.xlsx`) // 指示浏览器下载url,而不是导航到它；因此将提示用户将其保存为本地文件
            document.body.appendChild(dom)
            dom.click()
          })
          .catch(error => {
            console.log(error)
            this.$message({ type: 'error', message: error.message })
          })
      },
      BomLineAsyncGetRealDataSource (data) {
        this.BomLineRealDataSource = data
      },
      BomLineAddLine () {
        this.$refs.BomLineTable.add({})
      },
      deleteItem (scope, data) {
        data.splice(scope.$index, 1)
      },

      handleSuccess () {
        this.getDetail()
      },
      save () {
        this.$refs.form.validate(result => {
          this.$refs.BomLineTable.validate(res => {
            if (result && res) {
              /* const {flag} = this.$attrs.params;
              const data = {
                ...this.form,
                bomLineList: this.BomLineRealDataSource,
              }
              bomApi.addOrUpdate(data).then(res => {
                this.$message({
                  type: "success",
                  message: res.message
                });
                this.cancelBill();
              }); */
              this.$http({
                method: 'POST',
                url:
                  '/api-sup-ce/sup/bom/addOrUpdate',
                timeout: this.timeout,
                headers: {
                  Authorization: 'Bearer ' + getToken()
                },
                data: {
                  ...this.form,
                  bomLineList: this.BomLineRealDataSource
                }
              })
                .then(response => {
                  const datas = response
                  this.$message({ type: 'success', message: datas.message })
                  this.cancelBill()
                })
                .catch(error => {
                  console.log(error)
                  this.$message({ type: 'error', message: error.message })
                })
            } else {
              this.__focus_error__()
            }
          })
        })
      },
      cancelBill () {
        const { flag, row } = this.$attrs.params
        if (flag === 'add') {
          this.$emit('tab-remove', 'bomheadEdit')
        } else {
          this.$emit('tab-remove', 'bomheadEdit' + row.bomHeadId)
        }
        this.__setTabTodo('bomheadList.getQuerydata')
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
  .bomheadEdit {
    height: 100%;
    padding-bottom: 50px;

    :deep(.table-wrapper) {
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
