<template>
  <el-container
    direction="vertical"
    class="code-generate-wrapper"
  >
    <el-header height="80">
      <el-steps :active="active">
        <el-step
          title="选择数据源"
          description="指定模块和数据库表"
        />
        <el-step
          title="基本信息"
          description="配置表单和列表页基本信息"
        />
        <el-step
          v-if="!isAPI"
          title="组件选择"
          description="指定是否需要通用组件"
        />
        <el-step
          v-if="!isAPI"
          title="接口配置"
          description="指定增删查改接口和请求方式"
        />
        <el-step
          title="生成代码"
          description="生成结果下载"
        />
      </el-steps>
    </el-header>
    <el-main style="overflow:hidden;padding-bottom: 32px;">
      <div class="page-content">
        <transition-group name="el-fade-in-linear">
          <!-- step1 -->
          <div
            v-show="showPanel(0)"
            key="step1"
            class="content-box"
          >
            <div class="source-wrapper">
              <el-form
                ref="source"
                :model="source"
                :rules="sourceRules"
              >
                <el-form-item
                  prop="module"
                  label="模块"
                >
                  <DictSelect
                    v-model="source.module"
                    code="MODULE_DIVISION"
                  />
                </el-form-item>
                <template v-if="tableCount === '0'">
                  <el-form-item
                    prop="database"
                    label="表名"
                  >
                    <DictSelect
                      v-model="source.database"
                      :code="source.module"
                      custom-select-type="MODULE_TABLE_NAME"
                    />
                  </el-form-item>
                </template>
                <template v-if="tableCount === '1'">
                  <!-- 头表 -->
                  <el-form-item
                    prop="headerDatabase"
                    :label="databaseLabel.header"
                  >
                    <DictSelect
                      v-model="source.headerDatabase"
                      :code="source.module"
                      custom-select-type="MODULE_TABLE_NAME"
                      filterable
                    />
                  </el-form-item>
                  <!-- 行表 -->
                  <el-form-item
                    prop="lineDatabase"
                    :label="databaseLabel.line"
                  >
                    <DictSelect
                      v-model="source.lineDatabase"
                      :code="source.module"
                      custom-select-type="MODULE_TABLE_NAME"
                      filterable
                      multiple
                    />
                  </el-form-item>
                </template>
                <el-form-item
                  v-if="!isAPI"
                  prop="pageName"
                  label="前端页面名称"
                >
                  <el-input v-model.trim="source.pageName" />
                </el-form-item>
              </el-form>
            </div>
          </div>
          <!-- step2 -->
          <div
            v-show="showPanel(1)"
            key="step2"
            class="content-box"
          >
            <div class="fields-wrapper">
              <template v-if="tableCount === '0'">
                <DatabaseTable
                  :database-feilds="databaseFeilds"
                  @rowClick="rowClick"
                />
              </template>
              <template v-if="tableCount === '1'">
                <el-collapse v-model="activeCollapse">
                  <el-collapse-item
                    :title="`头表 ${source.headerDatabase}`"
                    :label="databaseLabel.header"
                    name="1"
                  >
                    <!-- 头表 -->
                    <DatabaseTable
                      :database-feilds="headerDatabaseFeilds"
                      @rowClick="rowClick"
                    />
                  </el-collapse-item>
                  <el-collapse-item
                    v-for="(lineData,key) in lineDatabaseFeilds"
                    :key="key"
                    :title="`行表 ${lineData.table.name}`"
                    :label="databaseLabel.line"
                    :name="`${key+2}`"
                  >
                    <!-- 行表 -->
                    <DatabaseTable
                      :database-feilds="lineData.selectLineFileList"
                      @rowClick="rowClick"
                    />
                  </el-collapse-item>
                </el-collapse>
              </template>
            </div>
          </div>
          <!-- step3 -->
          <div
            v-show="showPanel(2)"
            v-if="!isAPI"
            key="step3"
            class="content-box"
          >
            <div class="component-wrapper">
              <el-form
                ref="comp"
                :model="comp"
              >
                <el-form-item
                  prop="importComp"
                  label="导入组件"
                >
                  <el-switch
                    v-model="comp.importComp"
                    @change="clearUrl('import')"
                  />
                </el-form-item>
                <el-form-item
                  prop="exportComp"
                  label="导出组件"
                >
                  <el-switch
                    v-model="comp.exportComp"
                    @change="clearUrl('export')"
                  />
                </el-form-item>
              </el-form>
            </div>
          </div>
          <!-- step4 -->
          <div
            v-show="showPanel(3)"
            v-if="!isAPI"
            key="step4"
            class="content-box"
          >
            <div class="api-wrapper">
              <el-form
                ref="api"
                :model="api"
              >
                <el-form-item
                  prop="add"
                  label="新增"
                >
                  <el-switch
                    v-model="api.add"
                    @change="clearUrl('add')"
                  />
                </el-form-item>
                <el-form-item
                  prop="delete"
                  label="删除"
                >
                  <el-switch v-model="api.delete" />
                </el-form-item>
                <el-form-item
                  prop="update"
                  label="更新"
                >
                  <el-switch v-model="api.update" />
                </el-form-item>
                <el-form-item
                  prop="query"
                  label="查询"
                >
                  <el-switch v-model="api.query" />
                </el-form-item>
              </el-form>
            </div>
          </div>
          <!-- step5 -->
          <div
            v-show="showPanel(4)"
            key="step5"
            class="content-box"
          >
            <div class="generate-wrapper">
              <el-button
                type="primary"
                @click="download"
              >
                下载代码
              </el-button>
              <!-- <el-button type="primary" @click="generateCode"
                >生成代码</el-button
              >
              <el-button type="primary" v-if="!!fileId" @click="downloadCode"
                >下载代码</el-button
              > -->
            </div>
          </div>
        </transition-group>
      </div>
    </el-main>
    <CToolbar>
      <template #right>
        <el-button
          v-if="showNext"
          type="primary"
          @click="next"
        >
          下一步
        </el-button>
        <el-button
          v-if="showPrev"
          @click="prev"
        >
          上一步
        </el-button>
        <el-button @click="close">
          关闭
        </el-button>
      </template>
    </CToolbar>
  </el-container>
</template>
<script>
import { downloadFileLink, downloadFileLinkByPost } from 'lib@/utils/file'
import camelCase from 'lodash/camelCase'
import CToolbar from 'lib@/components/c-toolbar'
import DatabaseTable from './table'
import { createDictClass } from '@/library/utils/dict/dict-utils'
import { globalToolAPI } from 'modb@/basicSetting/api/basicSetting'
const dictClass = createDictClass()

export default {
  name: 'CodeGeneratePage',
  filters: {
    yes_no (value) {
      return value ? '是' : '否'
    }
  },
  components: { CToolbar, DatabaseTable },
  props: {
    baseData: {
      type: Object,
      required: true
    }
  },
  data () {
    return {
      dictClass: dictClass,
      active: 0,
      currentRowClick: null,
      fileId: null,
      activeCollapse: [],
      handlers: [
        'sourceHandler',
        'basicHandler',
        'compHandler',
        'apiHandler',
        'generateHandler'
      ],
      databaseFeilds: [],
      headerDatabaseFeilds: [],
      lineDatabaseFeilds: [],
      lineTableDTOS: [],
      api: {
        query: true,
        add: true,
        delete: true,
        update: true
      },
      comp: {
        importComp: false,
        exportComp: false
      },
      source: {
        database: null,
        headerDatabase: null,
        lineDatabase: [],
        module: null,
        pageName: null
      },
      sourceRules: {
        module: [{ required: true, message: '模块必选' }],
        pageName: [{ required: true, message: '前端页面名称必填' }],
        database: [{ required: true, message: '表名必填' }],
        headerDatabase: [{ required: true, message: '头表表名必填' }],
        lineDatabase: [{ required: true, message: '行表表名必填' }]
      }
    }
  },
  computed: {
    tableCount () {
      if (this.isAPI) {
        return '1'
      } else {
        return this.baseData.isMultipleTables
      }
    },
    isAPI () {
      return this.baseData.generateType === '1'
    },
    databaseLabel () {
      if (this.baseData.generateType === '0') {
        return {
          header: '头表',
          line: '行表'
        }
      } else {
        return {
          header: '临时表',
          line: '业务表'
        }
      }
    },
    showNext () {
      return this.active !== 4
    },
    showPrev () {
      return this.active !== 0
    }
  },
  created () {
  },
  methods: {
    async download () {
      const { database, lineDatabase, headerDatabase, pageName, packageName, module } = this.source
      let selectLineFileList = []
      let selectHeadFileList = []
      let lineTableDTOS = []
      let headTable = {}
      if (this.tableCount === '0') {
        headTable = { name: database }
        selectHeadFileList = this.databaseFeilds
      } else if (this.tableCount === '1') {
        this.lineDatabaseFeilds.forEach(item => {
          item.selectLineFileList.forEach((row, i) => {
            // 更新参数值
            item.selectLineFileList.splice(i, 1, this.normalize(row))
          })
        })
        headTable = { name: headerDatabase }
        lineTableDTOS = this.lineDatabaseFeilds
        selectHeadFileList = this.headerDatabaseFeilds
      }
      const postParams = {
        ...this.baseData,
        pageName: pageName,
        moduleName: module,
        packageName: packageName,
        isExport: this.comp.exportComp ? 1 : 0,
        isImport: this.comp.importComp ? 1 : 0,
        isAdd: this.api.add ? 1 : 0,
        isEditor: this.api.update ? 1 : 0,
        isDeleted: this.api.delete ? 1 : 0,
        selectHeadFileList: selectHeadFileList.map(i => this.normalize(i)),
        lineTableDTOS: this.lineDatabaseFeilds,
        headTable
      }
      downloadFileLinkByPost('/api-acode/acode/getCodeDowload', Date.now() + '.zip', postParams, () => {
        console.log('[download code success]')
      })
      // const res = await axios({
      //   url: "/api-acode/acode/getCodeDowload",
      //   method: "POST",
      //   loading: true,
      //   data: postParams,
      //   responseType: "arraybuffer"
      // });
      // console.log("[ArrayBuffer]", res.data instanceof ArrayBuffer);
      // const blob = new Blob([res.data], { type: "application/pdf" });
      // const file = new window.File([blob], "myfile.pdf", {
      //   type: "application/pdf"
      // });
    },
    normalize (data) {
      const { attr, title, typeName, componentType, fileUploadMap, isMain, nullAble, realTitle, realAttr, showTable, queryField, filterType, showForm, division, dictCode, required, quickMap, quickSearchCode } = data
      return {
        filedDesc: title,
        code: null,
        javaCode: realAttr,
        componentType,
        fileUploadMap,
        dbCode: attr,
        name: realTitle,
        type: typeName,
        filedType: typeName,
        filedIfnull: nullAble === 'Y' ? 1 : 0,
        isPk: isMain === 'Y' ? 1 : 0,
        isShowOnGrid: showTable ? 1 : 0,
        isQueryCondition: queryField ? 1 : 0,
        matchQueryCondition: filterType,
        partName: division,
        isShowOnForm: showForm ? 1 : 0,
        dictItemCode: dictCode,
        dictItemName: null,
        dictItemCodeCamel: null,
        isRequired: required ? 1 : 0,
        quickMap,
        quickSearchCode
      }
    },
    formatOfYesOrNo (row, column, cellValue) {
      return cellValue === 'N' ? '否' : '是'
    },
    close () {
      this.$emit('close')
    },
    generateCode () {
      const data = {}
      const { pageName, pageCode } = this.source
      const dictCodes = []
      const table = this.databaseFeilds
        .filter(i => i.showTable)
        .map(i => {
          const res = {
            label: i.realTitle,
            prop: i.realAttr
          }
          if (i.dictCode) {
            dictCodes.push(i.dictCode)
            res.dictCode = i.dictCode
          }
          return res
        })
      const filterConfig = this.databaseFeilds
        .filter(i => i.queryField)
        .map(i => {
          const res = {
            label: i.realTitle,
            prop: i.realAttr
          }
          if (i.dictCode) {
            dictCodes.push(i.dictCode)
            res.dictCode = i.dictCode
          }
          return res
        })
      const form = []
      const division = []
      const showForm = this.databaseFeilds.filter(i => i.showForm)
      const hasDivision = showForm.filter(i => i.division)
      if (hasDivision.length) {
        const divs = hasDivision.reduce((last, i) => {
          const obj = {
            label: i.realTitle,
            prop: i.realAttr
          }
          if (i.dictCode) {
            dictCodes.push(i.dictCode)
            obj.dictCode = i.dictCode
          }
          if (last[i.division]) {
            last[i.division].push(obj)
          } else {
            last[i.division] = [obj]
          }
          return last
        }, {})
        for (let [key, value] of Object.entries(divs)) {
          division.push({
            title: key,
            form: value
          })
        }
      } else {
        showForm.forEach(i => {
          const obj = {
            label: i.realTitle,
            prop: i.realAttr
          }
          if (i.dictCode) {
            dictCodes.push(i.dictCode)
            obj.dictCode = i.dictCode
          }
          form.push(obj)
        })
      }
      const major = this.databaseFeilds.find(i => i.major === 'Y').realAttr
      const rules = this.databaseFeilds
        .filter(i => i.required)
        .map(i => i.realAttr)
      this.$http({
        method: 'POST',
        url: '/egg/generateCode',
        loading: true,
        data: {
          api: this.api,
          pageCode,
          pageName,
          dictCodes: [...new Set(dictCodes)],
          rules,
          table,
          form,
          division,
          comp: this.comp,
          filterConfig,
          major
        }
      }).then(res => {
        console.log('[----res-------]', res)
        const { data: fileId } = res
        this.fileId = fileId
        this.$message.success(res.message)
      })
    },
    downloadCode () {
      downloadFileLink(
        `egg/downloadCode?id=${this.fileId}`,
        `${this.fileId}.zip`
      ).catch(() => {
        this.$message.error('下载失败')
      })
    },
    clearUrl (type) {
      switch (type) {
        case 'import':
          this.comp.importUrl = ''
          break
        case 'export':
          this.comp.exportTitleUrl = ''
          this.comp.exportUrl = ''
          break
        case 'add':
          this.api.addUrl = ''
          break
        case 'update':
          this.api.updateUrl = ''
          break
        case 'delete':
          this.api.deleteUrl = ''
          break
        case 'query':
          this.api.queryUrl = ''
          break
      }
    },
    rowClick (row, column, event) {
      if (this.currentRowClick) {
        this.currentRowClick.isEditing = false
      }
      if (!row.isEditing) {
        row.isEditing = true
        this.currentRowClick = row
      }
    },
    generateHandler () {
      return new Promise(rs => {
        rs(true)
      })
    },
    apiHandler () {
      return new Promise(rs => {
        if (this.$refs.api) {
          console.log('[api]', this.api)
          console.log('[comp]', this.comp)
          this.$refs.api.validate(boolean => {
            if (!boolean) {
              this.focusError()
              this.$message.error('请检查必填项')
            }
            rs(boolean)
          })
        }
      })
    },
    compHandler () {
      return new Promise(rs => {
        if (this.$refs.comp) {
          this.$refs.comp.validate(boolean => {
            if (!boolean) {
              this.focusError()
              this.$message.error('请检查必填项')
            }
            rs(boolean)
          })
        }
      })
    },
    basicHandler () {
      return new Promise(rs => {
        rs(true)
      })
    },
    sourceHandler () {
      return new Promise(rs => {
        if (this.$refs.source) {
          const { database, lineDatabase, headerDatabase } = this.source
          const tableList = []
          if (this.tableCount === '0') {
            tableList.push(database)
          } else if (this.tableCount === '1') {
            tableList.push(lineDatabase)
            tableList.push(headerDatabase)
          }
          this.$refs.source.validate(boolean => {
            if (!boolean) {
              this.focusError()
              rs(false)
              this.$message.error('请检查必填项')
            } else if (tableList.some(i => !/\_/g.test(i))) {
              this.$message.error('表名不规范，请检查')
              rs(false)
            } else {
              rs(true)
              this.queryDatabase()
            }
          })
        }
      })
    },
    queryDatabase () {
      if (this.tableCount === '0') {
        globalToolAPI.listColumns({ module: this.source.module, tableName: this.source.database })
          .then(({ data }) => {
            const temp = {}
            this.databaseFeilds = data.reduce((last, item) => {
              item.quickSearchCode = ''
              if (!temp[item.columnName]) {
                temp[item.columnName] = true
                if (this.checkAutoShow(item)) {
                  last.push({
                    ...item,
                    attr: item.columnName,
                    title: item.desc,
                    showTable: false,
                    showForm: false,
                    isEditing: false,
                    realAttr: camelCase(item.columnName),
                    realTitle: item.desc
                  })
                } else {
                  last.push({
                    ...item,
                    attr: item.columnName,
                    title: item.desc,
                    showTable: true,
                    showForm: true,
                    isEditing: false,
                    realAttr: camelCase(item.columnName),
                    realTitle: item.desc
                  })
                }
              }
              return last
            }, [])
          })
      } else if (this.tableCount === '1') {
        // 头表请求
        globalToolAPI.listColumns({ module: this.source.module, tableName: this.source.headerDatabase })
          .then(({ data }) => {
            const temp = {}
            this.headerDatabaseFeilds = data.reduce((last, item) => {
              if (!temp[item.attr]) {
                temp[item.attr] = true
                if (this.checkAutoShow(item)) {
                  last.push({
                    ...item,
                    attr: item.columnName,
                    title: item.desc,
                    showTable: false,
                    showForm: false,
                    isEditing: false,
                    realAttr: camelCase(item.columnName),
                    realTitle: item.desc
                  })
                } else {
                  last.push({
                    ...item,
                    attr: item.columnName,
                    title: item.desc,
                    showTable: true,
                    showForm: true,
                    isEditing: false,
                    realAttr: camelCase(item.columnName),
                    realTitle: item.desc
                  })
                }
              }
              return last
            }, [])
          })
          this.getLineBaseData()
      }
    },
    // 获取行表数据
    async getLineBaseData () {
      this.activeCollapse = ['1']
      let lineDatas = this.source.lineDatabase.map(query => {
        let obj = { name: query }
        return new Promise((res, reject) => {
          globalToolAPI.listColumns({ module: this.source.module, tableName: query }).then(r => {
            obj.selectLineFiles = r.data
            res(obj)
          })
        })
      })
      let fileLineListAndName = await Promise.all(lineDatas)
      this.lineDatabaseFeilds = fileLineListAndName.map((p, i) => {
        this.activeCollapse.push(`${i + 2}`)
        return {
          table: { name: p.name },
          selectLineFileList: this.lineDataBaseDeal(p.selectLineFiles)
        }
      })
    },
    lineDataBaseDeal (data) {
      const temp = {}
      return data.reduce((last, item) => {
        if (!temp[item.attr]) {
          temp[item.attr] = true
          if (this.checkAutoShow(item)) {
            last.push({
              ...item,
              attr: item.columnName,
              title: item.desc,
              showTable: false,
              showForm: false,
              isEditing: false,
              realAttr: camelCase(item.columnName),
              realTitle: item.desc
            })
          } else {
            last.push({
              ...item,
              attr: item.columnName,
              title: item.desc,
              showTable: true,
              showForm: true,
              isEditing: false,
              realAttr: camelCase(item.columnName),
              realTitle: item.desc
            })
          }
        }
        return last
      }, [])
    },
    focusError () {
      this.$nextTick(() => {
        const isError = document.getElementsByClassName('is-error')
        isError[0].querySelector('input').focus()
      })
    },
    showPanel (current) {
      return this.active === current
    },
    async next () {
      const handlerName = this.handlers[this.active]
      const result = await this[handlerName]()
      if (this.isAPI && this.active === 1) {
        this.active = 4
      } else if (result) {
        this.active = this.active + 1
      }
    },
    prev () {
      if (this.isAPI && this.active === 4) {
        this.active = 1
      } else {
        this.active = this.active - 1
      }
    },
    checkAutoShow (item) {
      return item.columnName == 'CREATED_ID' || item.columnName == 'CREATED_BY_IP' || item.columnName == 'LAST_UPDATE_DATE' ||
            item.columnName == 'LAST_UPDATED_ID' || item.columnName == 'LAST_UPDATED_BY_IP' || item.columnName == 'TENANT_ID' ||
         item.columnName == 'VERSION' || item.isMain == 'Y'
    }
  }
}
</script>

<style scoped lang="scss">
.code-generate-wrapper {
  padding: 10px;
}
.page-content {
  width: 100%;
  // height: calc(100vh - 120px);
  overflow: auto;
}

.content-box {
  width: 100%;
}

.source-wrapper {
  margin: 0 auto;
  padding: 30px 100px;
}

.component-wrapper {
  margin: 0 auto;
  padding: 30px 100px;
}

.generate-wrapper {
  margin: 0 auto;
  padding: 30px 100px;
}

.api-wrapper {
  margin: 0 auto;
  padding: 30px 100px;
}

.buttons-group {
  display: flex;
  width: 100%;
  height: 32px;
  background: #fff;
  justify-content: flex-end;
  align-items: center;
  margin-top: 10px;
}

.fields-wrapper {
  // padding-bottom: 50px;
}
</style>
