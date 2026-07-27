<template>
  <el-container
    class="flex-container toolinginfo_list_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        ref="formWrapper"
        :formArray="filterConfig"
        :preFormObj="formQueryData"
        :cannotClearFormObj="true"
        @getFormData="getQuerydata"
      />
      <MainHeader :lSpan="22" :rSpan="2">
        <template slot="left">
          <!-- <AuthorityButton type="primary" @click="importHandle">批量导入</AuthorityButton> -->
          <MImport
            ref="import1"
            style="display: inline-block;margin: 0"
            title="导入"
            code="base:materialQuotation:import"
            upLoadUrl="/api-cost/material-quotation/importExcel"
            :extraData="extraData"
            :extraPostData="extraPostData"
            @downloadTemplate="downloadItemTemplate"
            @handleSuccess="handleSuccess"
          />
          <AuthorityButton type="primary" code="base:materialQuotation:valid" @click="validHandle">
            生效
          </AuthorityButton>
          <AuthorityButton type="primary" code="base:materialQuotation:invalid" @click="invalidHandle">
            失效
          </AuthorityButton>
          <AuthorityButton type="primary" code="base:materialQuotation:delete" @click="deleteHandle">
            删除
          </AuthorityButton>
          <AuthorityButton type="primary" code="base:materialQuotation:export" @click="exportHandle">
            导出
          </AuthorityButton>
          <MImport
            ref="import2"
            style="display: inline-block;margin: 0 0 0 8px;"
            title="市况行情导入"
            code="base:materialQuotation:purchaseImport"
            upLoadUrl="/api-cost/material-quotation/purchaseImportExcel"
            :extraData="extraData"
            :extraPostData="extraPostData"
            @downloadTemplate="downloadPurchaseTemplate"
            @handleSuccess="handleSuccess"
          />
        </template>
      </MainHeader>

      <TableView
        ref="list"
        :table-header="tableHeader"
        :checkChange="handleCurrentChange"
        :page-size="pageSize"
        :preQueryData="queryParam"
        :openCustomTable="true"
        :checkbox="true"
        :postQueryData="postQueryData"
        url="/api-cost/material-quotation/query"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MainHeader from 'lib@/components/Table/MainHeader'
import { parseTime, formatTimeToDate } from '@/utils'
import { downloadFileLink, downloadFileLinkByPost } from 'lib@/utils/file'
import { sysPrefix } from '@/config/ipConfig'
import MImport from 'lib@/components/import'
export default {
    name: 'MaterialQuotation',
    components: {
        TableView,
        FormWrapper,
        MainHeader,
        MImport
    },
    mixins: [tabTodoWatch, tabTodoMixin],
    data () {
        return {
            isVENDOR: false,
            formQueryData: {},
            pageSize: 15,
            filterConfig: [
                {
                    prop: 'materialCode',
                    label: '原材料编号'
                },
                {
                    prop: 'materialName',
                    label: '原材料名称'
                },

                {
                    label: '行情状态',
                    prop: 'quotationStatus',
                    type: 'select',
                    options: () => {
                        return [
                            { value: 'DARFT', label: '拟定' },
                            { value: 'INVALID', label: '失效' },
                            { value: 'VALID', label: '生效' }

                        ]
                    }
                },
                {
                    label: '创建人',
                    prop: 'createdBy',
                    type: 'quicksearch',
                    showKey: 'nickname',
                    propKey: 'username',
                    name: 'scc_rbac_user_display'
                },
                {
                    label: '供应商编码',
                    prop: 'vendorCode',
                    type: 'quicksearch',
                    showKey: 'companyCode',
                    propKey: 'companyCode',
                    name: 'scc_sup_company_info_display_buyer'

                },
                {
                    prop: 'date',
                    label: '价格日期',
                    type: 'daterange'
                },
                // {
                //     label:'供应商编码',
                //     prop:'vendorCode',
                //     type:'quicksearch',
                //     showKey: "companyCode",
                //     propKey:'companyCode',
                //     name: "scc_sup_company_info_display",

                // },

                {
                    label: '行情类型',
                    prop: 'priceType',
                    type: 'select',
                    options: () => {
                        return [
                            { value: 'DAY', label: '日价格' },
                            { value: 'MONTH', label: '月价格' },
                            { value: 'YEAR', label: '年度价格' },
                            { value: 'TRY_CALCULATE', label: '财务试算' }

                        ]
                    }
                }

            ],
            queryParam: {

            },
            tableHeader: [
                {
                    label: '行情开始时间',
                    prop: 'startTime',
                    minWidth: 160,
                    formattor: (val) => {
                        return formatTimeToDate(val, 'Y-M-D')
                    }
                },
                {
                    label: '行情结束时间',
                    prop: 'endTime',
                    minWidth: 160,
                    formattor: (val) => {
                        return formatTimeToDate(val, 'Y-M-D')
                    }
                },
                {
                    label: '原材料编码',
                    prop: 'materialCode',
                    minWidth: 120
                },
                {
                    label: '原材料名称',
                    prop: 'materialName',
                    minWidth: 120
                },
                {
                    label: '规格',
                    prop: 'specification',
                    minWidth: 120
                },
                {
                    label: '价格用途',
                    prop: 'priceUsage',
                    minWidth: 120
                },
                {
                    label: '供应商编码',
                    prop: 'vendorCode',
                    minWidth: 200
                },
                {
                    label: '供应商名称',
                    prop: 'vendorName',
                    minWidth: 120
                },
                {
                    label: '不含税价',
                    prop: 'unitPrice',
                    minWidth: 120
                },
                {
                    label: '税率（%)',
                    prop: 'taxRate',
                    minWidth: 120
                },
                {
                    label: '币种',
                    prop: 'currencyCode',
                    minWidth: 180
                },

                {
                    label: '汇率',
                    prop: 'exchangeRate',
                    minWidth: 120
                },
                {
                    label: '单位',
                    prop: 'unitName',
                    minWidth: 120
                },
                {
                    label: '备注',
                    prop: 'remark',
                    minWidth: 120
                },

                {
                    label: '行情状态',
                    prop: 'quotationStatus',
                    minWidth: 160,
                    formattor: (val) => {
                        if (val === 'DARFT') {
                            return '拟定'
                        } else if (val === 'INVALID') {
                            return '失效'
                        } else if (val === 'VALID') {
                            return '生效'
                        } else {
                            return val
                        }
                    }
                },
                {
                    label: '行情类型',
                    prop: 'priceType',
                    minWidth: 120,
                    formattor: (val) => {
                        if (val === 'DAY') {
                            return '日价格'
                        } else if (val === 'MONTH') {
                            return '月价格'
                        } else if (val === 'HALF_YEAR') {
                            return '半年度价格'
                        } else if (val === 'YEAR') {
                            return '年度价格'
                        } else if (val === 'TRY_CALCULATE') {
                            return '财务试算'
                        } else {
                            return val
                        }
                    }
                },
                {
                    label: '工厂',
                    prop: 'orgName',
                    minWidth: 120
                },
                {
                    label: '来源类型',
                    prop: 'sourceType',
                    minWidth: 120
                },
                {
                    label: '创建人',
                    prop: 'createdBy',
                    minWidth: 120
                },
                {
                    label: '创建时间',
                    prop: 'creationDate',
                    minWidth: 120
                },
                {
                    label: '更新人',
                    prop: 'lastUpdatedBy',
                    minWidth: 120
                },
                {
                    label: '更新时间',
                    prop: 'lastUpdateDate',
                    minWidth: 120
                }
            ],
            extraData: {
                sourceType: 'WEB_APP',
                uploadType: 'FASTDFS',
                fileModular: 'suplier',
                fileFunction: 'accountAccess',
                fileType: 'excel'
            },
            // extraPostData:{

            // },
            selectArr: []
        }
    },
    mounted () {

    },
    created () {
        if (this.$store.getters.userType === 'VENDOR') {
            this.isVENDOR = true
            this.filterConfig = this.filterConfig.filter(item => {
                return item.prop !== 'createdBy'
            })
            this.formQueryData.vendorCode = this.$store.getters.userInfo.companyCode
            this.filterConfig = this.filterConfig.map(item => {
                if (item.prop === 'vendorCode') {
                    return {
                        ...item,
                        disabled: true
                    }
                } else {
                    return item
                }
            })
        }
    },
    methods: {
        getQuerydata (params) {
            this.queryParam = params
            if (params.date && params.date.length === 2) {
                this.queryParam.quotationDateFrom = params.date[0]
                this.queryParam.quotationDateTo = params.date[1]
            } else {
                this.queryParam.quotationDateFrom = ''
                this.queryParam.quotationDateTo = ''
            }
            this.$nextTick(() => {
                this.$refs.list.query()
            })
        },

        exportHandle () {
            let params = this.$refs.formWrapper.formData
            if (params.date && params.date.length === 2) {
                params.quotationDateFrom = params.date[0]
                params.quotationDateTo = params.date[1]
            }
            downloadFileLinkByPost('/api-cost/material-quotation/exportExcel', `原材料行情维护-导出${parseTime(new Date())}.xlsx`, params)
        },
        deleteHandle () {
            if (this.selectArr.length === 0) {
                this.$message.error('请先勾选需要删除的数据')
                return false
            }
            // let flag = this.selectArr.some(item=>{
            //     return item.quotationStatus !== 'DARFT'
            // })
            // if(flag){
            //     this.$message.error('只能删除状态为拟定的数据')
            //     return false;
            // }
            const params = this.selectArr.map(item => item.quotationId)
            this.$http({
                url: '/api-base/material-quotation/removeByIds',
                method: 'POST',
                loading: true,
                data: params
            }).then(res => {
                if (res.code === 'R000') {
                    this.$message.success(res.message)
                    this.getQuerydata(this.queryParam)
                }
            })
        },

        validHandle () {
            if (this.selectArr.length === 0) {
                this.$message.error('请先勾选需要生效的数据')
                return false
            }
            const params = this.selectArr.map(item => item.quotationId)

            // let flag = this.selectArr.every(item=>item.quotationStatus === 'VALID');
            // if(!flag){
            //     this.$message.error('请选择行情状态为拟定的数据')
            //     return false;
            // }

            this.$http({
                url: '/api-base/material-quotation/valid',
                method: 'POST',
                loading: true,
                data: params
            }).then(res => {
                if (res.code === 'R000') {
                    this.$message.success(res.message)
                    this.getQuerydata(this.queryParam)
                }
            })
        },
        invalidHandle () {
            if (this.selectArr.length === 0) {
                this.$message.error('请先勾选需要失效的数据')
                return false
            }
            // let flag = this.selectArr.every(item=>item.quotationStatus === 'VALID');
            // if(!flag){
            //     this.$message.error('请选择行情状态为生效的数据')
            //     return false;
            // }
            const params = this.selectArr.map(item => item.quotationId)
            this.$http({
                url: '/api-base/material-quotation/invalid',
                method: 'POST',
                loading: true,
                data: params
            }).then(res => {
                if (res.code === 'R000') {
                    this.$message.success(res.message)
                    this.getQuerydata(this.queryParam)
                }
            })
        },
        handleCurrentChange (e) {
            this.selectArr = e
        },
        handleSuccess (res) {
            this.$message.success('导入成功')
            this.getQuerydata(this.queryParam)
        },
        downloadItemTemplate () {
            downloadFileLink(
                '/api-cost/material-quotation/exportHead',
                '原材料行情维护导入模板.xlsx'
            ).catch(() => {
                this.$message.error('下载失败')
            })
        },
        downloadPurchaseTemplate () {
            downloadFileLink(
                '/api-cost/material-quotation/purchaseExportHead',
                '原材料行情维护市况行情导入模板.xlsx'
            ).catch(() => {
                this.$message.error('下载失败')
            })
        }

    }
}
</script>
