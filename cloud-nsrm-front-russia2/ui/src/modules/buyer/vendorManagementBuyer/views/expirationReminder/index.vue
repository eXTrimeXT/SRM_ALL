<template>
  <el-container
    class="flex-container-notab the_purchaseDirectory_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="queryForm"
        @getFormData="getQuerydata"
      />
      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :current-change="handleCurrentChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        url="/api-sup/info/vendorInformation/listManagementAttachPageByDTO"
      >
        <!--        日期-->
        <template #endDate="{ scope }">
          <div class="block">
            <el-date-picker
              v-model="scope.row.endDate"
              type="date"
              value-format="yyyy-MM-dd"
              :format="$formatDatePicker"
              :disabled="!scope.row.isNeedTotal"
              :placeholder="$t('bidMod.datePicker')"
            />
          </div>
        </template>
        <!--        附件-->
        <template #authType="{ scope }">
          <SrmCommonFile
            :extra-data="fileInfo"
            :default-file="{
              fileId: scope.row.fileId,
              fileName: scope.row.authType
            }"
            :readonly="!scope.row.isNeedTotal"
            @on-change="({file}) => handleUploadSuccess(file,scope.row)"
          />
        </template>
      </TableView>
      <!-- 操作历史 -->
      <srm-dialog
        size="large"
        :visible.sync="loggerVisible"
        :title="$t('vendorMod.actLog')"
      >
        <!--   历史记录     -->
        <Logger
          ref="loggerOperation"
          :expire-reminder="loggerRow"
        />
      </srm-dialog>
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import QuickSearch from 'lib@/components/QuickSearch'
import { adaptDictData, parseTime } from '@/utils'
import Logger from './logger'
import ExportExcel from 'lib@/components/export-excel'
import { expirationReminder } from 'modb@/vendorManagementBuyer/api/supApi'

export default {
  name: 'ExpirationReminder',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    QuickSearch,
    ExportExcel,
    Logger
  },
  provide () {
    return { context: this }
  },
  data () {
    return {
      tableName: 'purchaseDirectoryList',
      defaultTableHeader: [],
      pageSize: 15,
      gridId: 'purchaseDirectoryList',
      curOpt: 'add',
      queryParam: {},
      filterParams: {},
      currentRow: null,
      loggerRow: {},
      formTypeList: [],
      tableHeader: [],
      tableData: [],
      dialogFormVisible: false,
      displayList: [],
      loggerVisible: false,
      isNeedTotal: false,
      tempRowId: '',
      fileInfo: {
        fileModular: 'supplier',
        fileFunction: 'expireReminder',
        fileType: 'images'
      },
      queryForm: [
        {
          prop: 'companyCode',
          label: () => this.$t('common.vendorCode'), // 供应商编码
          type: 'quicksearch',
          showKey: 'companyName',
          propKey: 'companyCode',
          name: 'scc_sup_company_info_display_buyer'
        },
        {
          prop: 'dateList',
          label: () => this.$t('vendorMod.validityOfCert'), // 证件有效期范围
          type: 'daterange'
        },
        {
          prop: 'isUseReminder',
          label: () => this.$t('vendorMod.isUseReminder'), // 是否停用
          type: 'select',
          options: [
            { value: 'Y', label: this.$t('common.yes') },
            { value: 'N', label: this.$t('common.no') }
          ]
        }
      ]
    }
  },
  created () {
    let _this = this
    this.tableHeader = [
      {
        prop: 'companyCode',
        label: () => _this.$t('common.vendorCode'), // 供应商code
        width: 120
      },
      {
        prop: 'companyName',
        label: () => _this.$t('common.vendorName'), // 供应商名称
        minWidth: 150
      },
      {
        prop: 'authType',
        label: () => _this.$t('vendorMod.authFileName'), // 认证文件
        width: 150,
        showType: 'slot',
        slot: 'authType'
      },
      {
        prop: 'authDescription',
        label: () => _this.$t('vendorMod.authDesc'), // 认证描述
        width: 100
      },
      {
        prop: 'authNum',
        label: () => _this.$t('vendorMod.authNum'), // 认证编号
        width: 100
      },
      {
        prop: 'authDate',
        label: () => _this.$t('vendorMod.authDate'), // 认证时间
        width: 100,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'authOrg',
        label: () => _this.$t('vendorMod.authOrg'), // 认证机构
        width: 100
      },
      {
        prop: 'endDate',
        label: () => _this.$t('vendorMod.certUntil'), // 证件有效期至
        width: 150,
        showType: 'slot',
        slot: 'endDate',
        formattor (val) {
          let day30 = new Date(new Date().setDate(new Date().getDate() + 30))
          if (new Date(val) > new Date() && new Date(val) < day30) {
            return (
              '<span class="el-icon-success" style="color:yellow;padding-right: 11px;"></span>' +
              this.$parseTime(val)
            )
          } else if (new Date(val) < new Date()) {
            return (
              '<span class="el-icon-error" style="color:#F56C6C;padding-right: 11px;"></span>' + this.$parseTime(val)
            )
          } else {
            return this.$parseTime(val)
          }
        }
      },
      {
        prop: 'dueDate',
        label: () => _this.$t('vendorMod.dueDate'), // 到期时间(天数)
        width: 150,
        formattor (val, row) {
          if (!row.endDate) return null
          let diff = new Date(row.endDate).getTime() - new Date().getTime()
          return parseInt((diff / (1000 * 60 * 60 * 24)) + 1)
        }
      },
      {
        prop: 'dataSources',
        label: () => _this.$t('vendorMod.sourceBill'), // 来源单据
        btnStyle: 'text',
        showType: 'button',
        width: 135,
        callback: function (row) {
          this.goTo(row)
        }.bind(this)
      },
      {
        prop: 'formType',
        label: () => this.$t('contractMod.sourceType'), //   来源类型
        width: 120,
        dataType: 'dict', // 数据类型为字典
        code: 'ExpiredCertificateType' // 字典code
      },
      {
        prop: 'creationDate',
        label: () => _this.$t('vendorMod.createTime'), // 生成时间
        width: 135,
        dataType: 'dateTime'
      },
      {
        prop: 'historyRecord',
        label: () => _this.$t('vendorMod.historyRecord'), // 历史记录
        width: 100,
        showType: 'button',
        btnStyle: 'text',
        callback: function (row) {
          this.loggerRow = row
          this.openDialog()
        }.bind(this),
        formattor () {
          return _this.$t('common.view') // 查看
        }
      },
      {
        prop: 'isUseReminder',
        label: () => this.$t('vendorMod.monitoringStatus'),
        width: 100,
        formattor: val => {
          return val === 'Y' ? this.$t('vendorMod.monitored') : this.$t('vendorMod.notMonitored')
        }
      },
      {
        prop: 'operation',
        label: _this.$t('common.operation'),
        width: 150,
        showType: 'buttons',
        fixed: 'right',
        btnStyle: 'text',
        buttons: [
          {
            callback: function (row) {
              this.blockUp(row)
            }.bind(this),
            formattor (val) {
              return _this.$t('common.blockUpMonitor') // 停用
            },
            show: (row) => row.isUseReminder === 'Y'
          },
          {
            callback: function (row) {
              this.startUp(row)
            }.bind(this),
            formattor (val) {
              return _this.$t('common.startUpMonitor') // 启用
            },
            show: (row) => row.isUseReminder === 'N'
          },
          {
            callback: function (row) {
              this.modify(row)
            }.bind(this),
            formattor (val) {
              return _this.$t('common.modify') // 变更
            },
            show: (row) => !row.isNeedTotal
          },
          {
            callback: function (row) {
              this.save(row)
            }.bind(this),
            formattor (val) {
              return _this.$t('common.save') // 保存
            },
            show: (row) => row.isNeedTotal
          },
          {
            callback: function (row) {
              this.cancelModify(row)
            }.bind(this),
            formattor (val) {
              return _this.$t('common.cancel') // 取消
            },
            show: (row) => row.isNeedTotal
          }
        ]
      }
    ]
    this.defaultTableHeader = this.tableHeader
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    getQuerydata (v) {
      if (v && v.dateList) {
        v.authDate = v.dateList[0]
        v.endDate = v.dateList[1]
      } else if (v && !v.dateList) {
        delete v.authDate
        delete v.endDate
      }
      this.queryParam = v
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
        setTimeout(() => {
          // this.setColor();
        }, 5000)
      })
    },
    openDialog () {
      this.loggerVisible = true
      setTimeout(() => {
        this.$refs.loggerOperation.getOperationRecord()
      }, 10)
    },
    goTo (row) {
      switch (row.formType) {
        case 'COMPANY_INFO':
          this.$router.push({
            name: 'vendorProfile',
            params: { dataResources: row }
          })
          break
        case 'MANAGEMENT_ATTACH':
          this.$router.push({
            name: 'vendorProfile',
            params: { dataResources: row }
          })
          break
        case 'REVIEW_FORM':
          this.$router.push({
            name: 'quaOfReview',
            params: { dataResources: row }
          })
          break
        case 'SAMPLE_FORM':
          this.$router.push({
            name: 'sampleConfirmed',
            params: { dataResources: row }
          })
          break
        case 'MATERIAL_FORM':
          this.$router.push({
            name: 'materialTrial',
            params: { dataResources: row }
          })
          break
        case 'AUTH_FORM':
          this.$router.push({
            name: 'siteAssessment',
            params: { dataResources: row }
          })
          break
        default:
          return null
      }
    },
    // 删除
    handleAttachmentRemove (scope) {
      // 如果需要删除文件系统里面的附件则可以先调用接口删除后在清除字段显示
      scope.fileId = ''
      scope.authType = ''
    },
    // 上传成功
    handleUploadSuccess (file, scope) {
      const { fileId = '', fileName = '' } = file || {}
      scope.fileId = fileId.toString()
      scope.authType = fileName
    },
    // 按钮变更
    modify (row) {
      console.log(row)
      row.isNeedTotal = true
      this.$refs[this.gridId].tableData.push({})
      this.$refs[this.gridId].tableData.splice(this.$refs[this.gridId].tableData.length - 1, 1)
    },
    cancelModify (row) {
      row.isNeedTotal = false
      this.$refs[this.gridId].tableData.push({})
      this.$refs[this.gridId].tableData.splice(this.$refs[this.gridId].tableData.length - 1, 1)
      this.$refs[this.gridId].query()
    },
    // 更新保存
    save (row) {
      row.isNeedTotal = false
      expirationReminder.modify(row).then((data) => {
          this.$message.success(this.$t('common.successSave')) // 保存成功
          this.$refs[this.gridId].query()
        })
        .catch((err) => {
          console.log(err)
        })
    },
    blockUp (row) {
      // 当前操作将停用监控，请再次确认是否停用？
      this.$confirm(this.$t('vendorMod.msgStopMonitor'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        row.isUseReminder = 'N'
        expirationReminder.blockUpOrStartUpReminder(row).then((data) => {
            this.$message.success(this.$t('vendorMod.stopSuccess')) // 停用成功
            this.$refs[this.gridId].query()
          })
          .catch((err) => {
            console.log(err)
          })
      })
    },
    startUp (row) {
      row.isUseReminder = 'Y'
      expirationReminder.blockUpOrStartUpReminder(row).then((data) => {
          this.$message.success(this.$t('vendorMod.useMonitorSucces')) // 监控启用成功
          this.$refs[this.gridId].query()
        })
        .catch((err) => {
          console.log(err)
        })
    },
    setColor () {
      var x = document.querySelectorAll('.redColor')
      for (let i = 0; i < x.length; i++) {
        x[i].style.color = 'red'
      }
    },
    // 保存
    handleCurrentChange (val) {
      console.log(val)
      this.currentRow = val
    },
    dateDiff (firstDate, secondDate) {
      let diff = new Date(firstDate).getTime() - new Date(secondDate).getTime()
      return parseInt(diff / (1000 * 60 * 60 * 24))
    }
  }
}
</script>
<style scoped lang="scss">
.the_purchaseDirectory_wrapper {
  .redColor {
    color: red !important;
  }
  .yellowColor {
    color: yellow !important;
  }
}
</style>
