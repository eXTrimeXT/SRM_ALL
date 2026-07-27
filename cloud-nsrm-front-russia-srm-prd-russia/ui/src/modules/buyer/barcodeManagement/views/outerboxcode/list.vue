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
import { outerBoxCodeApi } from 'modb@/barcodeManagement/api'

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
          label: '外箱编码',
          width: 100
        },
        {
          prop: 'printCount',
          label: '打印次数',
          width: 100
        },
        {
          prop: 'materialCode',
          label: '物料编码',
          width: 100
        },
        {
          prop: 'materialSign',
          label: '物料标签',
          width: 100
        },
        {
          prop: 'vendorCode',
          label: '供应商编码',
          width: 120
        },
        {
          prop: 'vendorName',
          label: '供应商名称',
          width: 120
        },
        {
          prop: 'orderNumber',
          label: '采购订单号',
          width: 180
        },
        {
          prop: 'batchNo',
          label: '批次号',
          width: 130
        },
        {
          prop: 'orderLineNum',
          label: '项次（订单行）',
          width: 130
        },
        {
          prop: 'deliveryNumber',
          label: '送货单号',
          width: 180
        },
        {
          prop: 'productionDate',
          label: '生产日期',
          width: 100
        },
        {
          prop: 'creationDate',
          label: '创建日期',
          width: 100
        },
        {
          prop: 'createdBy',
          label: '创建人名称',
          width: 120
        },
        {
          prop: 'lastUpdatedBy',
          label: '更新人',
          width: 100
        }
      ],

      filterConfig: [
        { prop: 'outerBoxCode', label: '外箱编码' },
        { prop: 'deliveryNumber', label: '送货单号' },
        { prop: 'materialCode', label: '物料编码' },
        { prop: 'orderNumber', label: '采购订单号' }
        // { prop: "isFixedBox",
        //   label: "是否固定箱",
        //   type: 'slot',
        //   slot: 'isFixedBox'},
      ],
      queryParam: {},
      // 是否属性
      yesOrNoOptions: [
        { value: 'Y', label: '是' },
        { value: 'N', label: '否' }
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
    handleCurrentChange (val) {
      this.currentRows = val
    },
    // 多选删除
    checkChangeChange (rows) {
      this.currentHeaderRows = rows
      let rowArr = rows
      this.dictHeaderExportParam = rowArr.map(i => (i.outerBoxId))
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
    }
  }
}
</script>
