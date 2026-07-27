<template>
  <el-container
    class="flex-container toolinginfo_list_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        ref="formWrapper"
        :formArray="filterConfig"
        :preFormObj="preFormObj"
        :hasErrorMsgConfirm="true"
        @getFormData="getQuerydata"
      >
        <template #date="{ scope }">
          <el-date-picker
            v-model="scope.date"
            type="daterange"
            value-format="yyyy-MM-dd"
            format="yyyy-MM-dd"
            range-separator="~"
            start-placeholder="查询日期从"
            end-placeholder="查询日期至"
          />
        </template>
        <template #isExpansion="{scope}">
          <el-checkbox v-model="scope.isExpansion" />
        </template>
      </FormWrapper>

      <MainHeader :lSpan="22" :rSpan="2">
        <template slot="left">
          <AuthorityButton type="primary" @click="exportHandle">
            导出
          </AuthorityButton>
        </template>
      </MainHeader>

      <TableView
        ref="list"
        :table-header="tableHeader"
        :page-size="pageSize"
        :preQueryData="queryParam"
        :openCustomTable="true"
        :checkbox="true"
        :checkChange="handleCurrentChange"
        url="/api-sou/bom/query"
      >
        <!-- <template #linkagePriceNo="{ scope }">
                    <span style="color:#1890ff" @click="goEdit(scope.row,'view')">{{scope.row.linkagePriceNo}}</span>
                </template> -->
      </TableView>
    </el-main>
  </el-container>
</template>

<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MainHeader from 'lib@/components/Table/MainHeader'
import { downloadFileLink, downloadFileLinkByPost } from 'lib@/utils/file'
import { adaptDictData, parseTime, formatTimeToDate } from '@/utils'

export default {
    name: 'BOMlist',
    components: {
        TableView,
        FormWrapper,
        MainHeader

    },
    mixins: [
        tabTodoWatch,
        tabTodoMixin
    ],
    data () {
        return {
            fileInfo: {
                uploadType: 'FASTDFS', // 固定参数
                sourceType: 'WEB_APP', // 固定参数
                fileModular: 'sup', // 文件所属模块 -》基础模块
                fileFunction: 'vendorBiddingManagement', // 文件所属功能
                fileType: 'images' // 文件所属类型
            },
            pageSize: 15,
            dictCodes: {},
            filterParams: {},
            queryParam: {},
            filterConfig: [
                {
                    prop: 'orgId',
                    label: '工厂',
                    type: 'OUorganizationSelector',
                    rules: { required: true, message: '请选择工厂' }
                },
                {
                    prop: 'rootId',
                    label: 'MTOC'
                },
                {
                    prop: 'to',
                    label: '查询日期',
                    type: 'date',
                    rules: { required: true, message: '请输入查询日期' }
                },
                {
                    prop: 'partFid',
                    label: '父件号'

                },
                {
                    prop: 'partId',
                    label: '子件号'

                },
                {
                    prop: 'isExpansion',
                    label: '是否跨表',
                    slot: 'isExpansion',
                    type: 'slot'
                }

            ],
            tableHeader: [
                {
                    prop: 'orgName',
                    label: '工厂名称',
                    minWidth: 160
                },
                {
                    prop: 'rootId',
                    label: 'MTOC',
                    minWidth: 160
                },
                {
                    prop: 'partFId',
                    label: '父件号',
                    minWidth: 160
                },

                {
                    prop: 'partName',
                    label: '子件名',
                    minWidth: 160
                },
                {
                    prop: 'partId',
                    label: '子件号',
                    minWidth: 160
                },
                {
                    prop: 'simpleNo',
                    label: '子件简号',
                    minWidth: 160
                },
                {
                    prop: 'lineNo',
                    label: '位置号',
                    minWidth: 160
                },
                {
                    prop: 'orgCode',
                    label: '工厂编码',
                    minWidth: 160
                },

                {
                    prop: 'quantity',
                    label: '数量',
                    minWidth: 160
                },
                {
                    prop: 'purchaseType',
                    label: '采购类型',
                    minWidth: 160
                },
                {
                    prop: 'usageAmountUnit',
                    label: '单位',
                    minWidth: 160
                },

                {
                    prop: 'changeEffDate',
                    label: '生效时间',
                    minWidth: 160,
                    formattor: (val) => formatTimeToDate(val, 'Y-M-D')
                },
                {
                    prop: 'changeUneffDate',
                    label: '失效时间',
                    minWidth: 160,
                    formattor: (val) => formatTimeToDate(val, 'Y-M-D')
                }

            ],
            selectArr: [],
            extraData: {
                sourceType: 'WEB_APP',
                uploadType: 'FASTDFS',
                fileModular: 'base',
                fileFunction: 'accountAccess',
                fileType: 'excel'
            },
            uploadObj: {
                vaveIncomingId: ''
                // vaveProposalAttachmentFileId:'',
                // vaveIncomingFileId:'',
            },
            preFormObj: {}
        }
    },
    created () {
        this.preFormObj.to = formatTimeToDate(new Date(), 'Y-M-D')
        this.preFormObj.isExpansion = true
    },
    methods: {
        getQuerydata (params) {
            this.queryParam = params
            this.$nextTick(() => {
                this.$refs.list.query()
            })
        },
        reloadData () {
            this.getQuerydata(this.queryParam)
        },
        async exportHandle () {
            let params = {}
            if (this.selectArr.length > 0) {
                if (this.selectArr.length !== 1) {
                    this.$message.error('勾选导出只能选择一条数据')
                    return false
                }
                params.id = this.selectArr.map(item => {
                    return item.id
                }).join(',')
            } else {
                params = this.$refs.formWrapper.formData
            }
            let res = await this.$http({
                url: '/api-sou/bom/checkBeforeExport',
                method: 'POST',
                data: params,
                loading: true
            })
            if (res.code === 'R000') {
                downloadFileLinkByPost('/api-sou/bom/exportBom', `BOM-导出${parseTime(new Date())}.xlsx`, params).catch(() => {
                    this.$message.error('下载失败')
                })
            } else {
                this.$message.error(res.message)
            }
            // detailExport(),
        },
        handleCurrentChange (e) {
            this.selectArr = e
        }
    }

}
</script>
