<template>
  <el-container class="flex-container outerboxcode_list_wrapper" direction="vertical">
    <el-main>
      <FormWrapper :formArray="filterConfig" :preFormObj="preFormObj" @getFormData="getQuerydata">
        <template #isFixedBox="{ scope }">
          <el-select v-model="scope.isFixedBox" clearable>
            <el-option v-for="item in yesOrNoOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </template>
      </FormWrapper>
      <MainHeader :lSpan="22" :rSpan="2">
        <template slot="left">
          <AuthorityButton type="primary" @click="addHandle">
            {{ $t('common.add') }}
          </AuthorityButton>
          <!-- 多选删除 -->
          <AuthorityButton type="primary" @click="batchDelete">
            {{ $t("common.delete") }}
          </AuthorityButton>
          <AuthorityButton type="primary" @click="batchPrintOut">
            <!-- 打印外箱条码 -->
            {{ $t('cusEntry.supplement20250211.printOuterBoxBarcode') }}
          </AuthorityButton>
          <AuthorityButton type="primary" @click="batchPrintInn">
            <!-- 打印绑定内箱条码 -->
            {{ $t('cusEntry.supplement20250211.printBindInnerBoxBarcode') }}
          </AuthorityButton>
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-header="tableHeader"
        :checkChange="checkChangeChange"
        :current-change="handleCurrentChange"
        :page-size="pageSize"
        :preQueryData="queryParam"
        :openCustomTable="true"
        :comActive="$attrs['changeTab']"
        url="/api-base/base/outerboxcode/listPage"
        :checkbox="true"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import outerboxcodeEdit from './edit.vue'
import CUploadFile from '@/library/components/c-upload-file'
import CDownloadLink from 'lib@/components/c-download-link'
import { isNull, adaptDictData } from '@/utils'
import { getDictItem } from '@/api/common'
import { outerBoxCodeApi } from 'mods@/barcodeSupplier/api'

export default {
  name: 'OuterboxcodeList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    CUploadFile,
    CDownloadLink
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      preFormObj: {},
      pageSize: 15,
      gridId: 'list',
      // 文件上传配置信息
      fileInfo: {
        fileModular: 'workFlow', // 文件所属模块 -》审批流程
        fileFunction: 'workflowReport', // 审批流相关文件
        fileType: 'images' // 文件所属类型
      },
      currentRows: [],
      tableHeader: [
        {
          prop: 'outerBoxCode',
          label: this.$t('orderMod.outerBoxCode'),
          width: 100
        },
        {
          prop: 'printCount',
          label: this.$t('orderMod.printCount'),
          width: 100
        },
        {
          prop: 'materialCode',
          label: this.$t('common.materialCode'),
          width: 100
        },
        {
          prop: 'materialSign',
          label: this.$t('orderMod.materialSign'),
          width: 100
        },
        {
          prop: 'vendorCode',
          label: this.$t('common.vendorCode'),
          width: 120
        },
        {
          prop: 'vendorName',
          label: this.$t('common.companyName'),
          width: 120
        },
        {
          prop: 'orderNumber',
          label: this.$t('orderMod.orderNumber'),
          width: 180
        },
        {
          prop: 'batchNo',
          label: this.$t('orderMod.batchNo'),
          width: 130
        },
        {
          prop: 'orderLineNum',
          label: this.$t('orderMod.orderLineNum2'),
          width: 130
        },
        {
          prop: 'deliveryNumber',
          label: this.$t('orderMod.deliveryNumber'),
          width: 180
        },
        {
          prop: 'productionDate',
          label: this.$t('orderMod.productionDate'),
          width: 100,
          dataType: 'dateTime'
        },
        {
          prop: 'creationDate',
          label: this.$t('common.creationDate'),
          width: 100,
          dataType: 'dateTime'
        },
        {
          prop: 'createdBy',
          label: this.$t('purchaseDemand.createdFullName'),
          width: 120
        },
        {
          prop: 'lastUpdatedBy',
          label: this.$t('common.updatePeople'),
          width: 100
        },
        {
          prop: 'operation',
          label: this.$t('components.headers.operation'),
          showType: 'buttons',
          btnStyle: 'text',
          fixed: 'right',
          width: 240,
          buttons: [
            {
              callback: row => this.editHandle(row),
              // code: "pr:requirementApply:edit",
              show: row => row.isFixedBox != 'Y',
              formattor: () => {
                return this.$t('common.edit')
              }
            },
            {
              callback: row => this.deleteHandle(row),
              // code: "pr:requirementApply:edit",
              show: row => row.isFixedBox != 'Y',
              formattor: () => {
                return this.$t('common.delete')
              }
            },
            {
              callback: row => this.outPrint(row),
              // code: "pr:requirementApply:edit",
              show: row => row.isFixedBox == 'Y',
              formattor: () => { // 打印外箱固定条码
                return this.$t('cusEntry.supplement20250211.printOuterBoxFixedBarcode')
              }
            },
            {
              callback: row => this.innPrint(row),
              formattor: () => { // 打印绑定内箱条码
                return this.$t('cusEntry.supplement20250211.printBindInnerBoxBarcode')
              }
            }
          ]
        }
      ],

      filterConfig: [
        { prop: 'outerBoxCode', label: this.$t('orderMod.outerBoxCode') },
        { prop: 'deliveryNumber', label: this.$t('orderMod.deliveryNumber') },
        { prop: 'materialCode', label: this.$t('common.materialCode') },
        { prop: 'orderNumber', label: this.$t('orderMod.orderNumber') }
        // { prop: "isFixedBox",
        //   label: this.$t('cusEntry.supplement20250211.isFixedBox'), // 是否固定箱
        //   type: 'slot',
        //   slot: 'isFixedBox'},
      ],
      queryParam: {},
      // 是否属性
      yesOrNoOptions: [
        { value: 'Y', label: this.$t('common.yes') },
        { value: 'N', label: this.$t('common.no') }
      ],
      // 打印内箱模板列表
      innerPrintTemplateList: [],
      // 打印外箱模板列表
      outerPrintTemplateList: [],

      dictHeaderExportParam: []
    }
  },
  watch: {
    $route: {
      // 当前功能已经打开的时候监听是否是从其他功能跳转过来的
      deep: true,
      immediate: true,
      handler () {
        if (
          this.$route.query.from === 'fromFun' &&
          this.$route.query.funName === 'outerBoxCode'
        ) {
          console.log('++++++++')
          this.currentRows = []
          console.log('this.$route.query.formNo', this.$route.query.formNo)
          this.queryParam.deliveryNumber = this.$route.query.formNo
          this.preFormObj = {
            deliveryNumber: this.$route.query.formNo
          }
          this.$nextTick(() => {
            this.$refs[this.gridId].query()
          })
        } else {
          this.$nextTick(() => {
            this.$refs[this.gridId].query()
          })
        }
      }
    }
  },
  created () {
    this.defaultTableHeader = this.tableHeader
    // this.$nextTick(() => {
    //   this.getQuerydata();
    // });
    // 内箱条码打印模板
    getDictItem('INNER_BOX_PRINT_TEMPLATE').then((res) => {
      this.innerPrintTemplateList = adaptDictData(res.data, 'dict')
      console.log(this.innerPrintTemplateList, 'INNER_BOX_PRINT_TEMPLATE')
    })
    // 外箱条码打印模板
    getDictItem('OUTER_BOX_PRINT_TEMPLATE').then((res) => {
      this.outerPrintTemplateList = adaptDictData(res.data, 'dict')
      console.log(this.outerPrintTemplateList, 'OUTER_BOX_PRINT_TEMPLATE')
    })
  },
  methods: {

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
          if (isNull(row.deliveryNumber)) {
            outerBoxCodeApi.delete(row.outerBoxId).then(res => {
              this.$message.success(res.message)
              this.getQuerydata()
            })
          } else {
            this.$message({
              type: 'error',
              // message: row.outerBoxCode + '已绑定送货单-' + row.deliveryNumber + '，不能删除！！！'
              message: row.outerBoxCode + this.$t('cusEntry.supplement20250211.boundDeliveryOrder') + '-' + row.deliveryNumber + ', ' + this.$t('cusEntry.supplement20250211.cannotDelete')
            })
          }
        })
        .catch(() => { })
    },
    addHandle (row) {
      this.mode = 'add'
      const tab = {
        component: outerboxcodeEdit,
        params: {
          row,
          flag: this.mode
        },
        title: this.$t('cusEntry.supplement20250211.outerboxcodeAdd'), // 外箱条码新增
        name: 'outerboxcodeEdit'
      }
      this.$emit('tab-add', tab)
    },
    editHandle (row) {
      this.mode = 'edit'
      const tab = {
        component: outerboxcodeEdit,
        params: {
          row,
          flag: this.mode
        },
        title: this.$t('cusEntry.supplement20250211.outerboxcodeEdit'), // 外箱条码编辑
        name: 'outerboxcodeEdit' + row.outerBoxId
      }
      this.$emit('tab-add', tab)
    },
    handleCurrentChange (val) {
      this.currentRows = val
    },
    // 多选删除
    checkChangeChange (rows) {
      this.currentHeaderRows = rows
      let rowArr = rows
      this.dictHeaderExportParam = rowArr.map(i => (i.outerBoxId))
    },

    outPrint (row) {
      // this.countPrint(row.outerBoxId, '')
      this.openPrint(
        // 'database:' + '外箱固定条码.ureport.xml',
        'database:' + this.$t('cusEntry.supplement20250211.outerBoxFixedBarcode') + '.ureport.xml',
        encodeURIComponent(`outerBoxCode=${row.outerBoxCode}`)
      )
    },
    /**
     * 累计打印次数(外箱)
     */
    countPrint (outerBoxId, generateSerial) {
      this.$http({
        url: '/api-base/base/outerboxcode/countPrint',
        method: 'GET',
        params: { outerBoxId: outerBoxId, generateSerial: generateSerial },
        loading: true
      })
        .then((data) => { })
        .catch((err) => {
          console.log(err)
        })
    },
    /**
    * 打开pdf
    */
    openPrint (pdfName, params) {
      const xml = encodeURIComponent(pdfName)
      const url = `${this.$systemUrl}/#/pdfPrint?isBarcode=Y&xml=${xml}&params=${params}`
      window.open(url, '_blank', 'noopener,noreferrer')
    },
    innPrint (row) {
      // 校验该外箱是否绑定内箱
      this.$http({
        url: '/api-base/base/outerboxcode/checkInnerBox',
        method: 'POST',
        data: row,
        loading: true
      }).then((data) => {
        console.log(data)
        this.getCategoryCodeByMaterialId(data.data, 1)
      })
    },

    // 批量删除
    batchDelete () {
      let idArr = this.dictHeaderExportParam
      if (idArr.length === 0) return this.$message.error(this.$t('hierarchical.checkbarcode'))
      let count = 0
      for (let outerBoxCode of this.currentHeaderRows) {
        console.log('outerBoxCode', outerBoxCode)
        if (!isNull(outerBoxCode.deliveryNumber)) {
          this.$message({
            type: 'error',
            // message: outerBoxCode.outerBoxCode + '已绑定送货单-' + outerBoxCode.deliveryNumber + '，不能删除！！！'
            message: outerBoxCode.outerBoxCode + this.$t('cusEntry.supplement20250211.boundDeliveryOrder') + '-' + outerBoxCode.deliveryNumber + ', ' + this.$t('cusEntry.supplement20250211.cannotDelete')
          })
          count++
          break
        }
      }
      if (count == 0) {
        this.$confirm(this.$t('dataConfMod.sureDelete'), {
          confirmButtonText: this.$t('common.confirm'),
          cancelButtonText: this.$t('common.cancel'),
          type: 'warning'
        })
          .then(() => {
            this.$http({
              url: '/api-base/base/outerboxcode/batchDelete',
              method: 'POST',
              data: idArr,
              loading: true
            })
              .then(data => {
                this.$message.success(this.$t('components.approvalHead.tips.approvalCompletion'))
                this.getQuerydata()
              })
              .catch(err => {
                console.log(err)
              })
          })
          .catch(() => { })
      }
    },

    // 根据物料id得到所属物料品类
    getCategoryCodeByMaterialId (row, type) {
      console.log('row type', row, type)
      if (type == 1) {
        let currentPrintTemplateName = 'database:' + this.innerPrintTemplateList[0].label
        // 这里暂时不增加打印次数
        this.openPrint(
          currentPrintTemplateName,
          encodeURIComponent(`outerBoxId=${row.outerBoxId}`)
        )
      } else if (type == 2) {
        let currentPrintTemplateName = 'database:' + this.outerPrintTemplateList[0].label
        // 这里暂时不增加打印次数
        this.openPrint(
          currentPrintTemplateName,
          encodeURIComponent(`generateSerial=${row.generateSerial}`)
        )
      } else {
        let currentPrintTemplateName = 'database:' + this.innerPrintTemplateList[0].label
        // 这里暂时不增加打印次数
        this.openPrint(
          currentPrintTemplateName,
          encodeURIComponent(`generateSerial=${row.generateSerial}`)
        )
      }
    },
    // 批量打印外箱
    batchPrintOut () {
      let idArr = this.dictHeaderExportParam
      if (idArr.length === 0) return this.$message.error(this.$t('hierarchical.checkbarcode'))

      this.$http({
        url: '/api-base/base/outerboxcode/batchPrintOut',
        method: 'POST',
        data: idArr,
        loading: true
      })
        .then(data => {
          this.getCategoryCodeByMaterialId(data.data, 2)
        })
        .catch(err => {
          console.log(err)
        })
    },
    // 批量打印绑定内箱
    batchPrintInn () {
      let idArr = this.dictHeaderExportParam
      if (idArr.length === 0) return this.$message.error(this.$t('hierarchical.checkbarcode'))

      this.$http({
        url: '/api-base/base/outerboxcode/batchPrintInn',
        method: 'POST',
        data: idArr,
        loading: true
      })
        .then(data => {
          this.getCategoryCodeByMaterialId(data.data, 3)
        })
        .catch(err => {
          console.log(err)
        })
    }
  }
}
</script>
