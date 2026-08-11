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
          <!-- 导入 -->
          <MImport
            ref="import1"
            style="display: inline-block;margin: 0"
            :title="$t('common.import')"
            code="base:materialQuotation:import"
            upLoadUrl="/api-cost/material-quotation/importExcel"
            :extraData="extraData"
            :extraPostData="extraPostData"
            @downloadTemplate="downloadItemTemplate"
            @handleSuccess="handleSuccess"
          />
          <AuthorityButton type="primary" code="base:materialQuotation:valid" @click="validHandle">
            <!-- 生效 -->
            {{ $t("common.active") }}
          </AuthorityButton>
          <AuthorityButton type="primary" code="base:materialQuotation:invalid" @click="invalidHandle">
            <!-- 失效 -->
            {{ $t("common.inactive") }}
          </AuthorityButton>
          <AuthorityButton type="primary" code="base:materialQuotation:delete" @click="deleteHandle">
            <!-- 删除 -->
            {{ $t("components.common.delete") }}
          </AuthorityButton>
          <AuthorityButton type="primary" code="base:materialQuotation:export" @click="exportHandle">
            <!-- 导出 -->
            {{ $t("common.export") }}
          </AuthorityButton>
          <!-- 市况行情导入 -->
          <MImport
            ref="import2"
            style="display: inline-block;margin: 0 0 0 8px;"
            :title="$t('cusEntry.supplement20250211.citySituationImport')"
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
import { parseTime } from '@/utils'
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
                    // '原材料编号'
                    label: this.$t('cusEntry.supplement20250211.rawMaterialId')
                },
                {
                    prop: 'materialName',
                    // '原材料名称'
                    label: this.$t('marketBudget.materialName')
                },

                {
                    // '行情状态'
                    label: this.$t('marketBudget.quotationStatus'),
                    prop: 'quotationStatus',
                    type: 'select',
                    options: () => {
                        return [
                            // '拟定'
                            { value: 'DARFT', label: this.$t('vendorMod.DRAFT') },
                            // '失效'
                            { value: 'INVALID', label: this.$t('common.inactive') },
                            // '生效'
                            { value: 'VALID', label: this.$t('common.active') }

                        ]
                    }
                },
                {
                    // '创建人'
                    label: this.$t('common.creator'),
                    prop: 'createdBy',
                    type: 'quicksearch',
                    showKey: 'nickname',
                    propKey: 'username',
                    name: 'scc_rbac_user_display'
                },
                {
                    // '供应商编码'
                    label: this.$t('common.vendorCode'),
                    prop: 'vendorCode',
                    type: 'quicksearch',
                    showKey: 'companyCode',
                    propKey: 'companyCode',
                    name: 'scc_sup_company_info_display_buyer'

                },
                {
                    // '价格日期'
                    prop: 'date',
                    label: this.$t('cusEntry.supplement20250211.priceDate'),
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
                    // '行情类型'
                    label: this.$t('marketBudget.quotationType'),
                    prop: 'priceType',
                    type: 'select',
                    options: () => {
                        return [
                            { value: 'DAY', label: this.$t('cusEntry.supplement20250211.dayPrice') },  // '日价格'
                            { value: 'MONTH', label: this.$t('cusEntry.supplement20250211.monthPrice') },  // '月价格'
                            { value: 'YEAR', label: this.$t('cusEntry.supplement20250211.annualPrice') },  // '年度价格'
                            { value: 'TRY_CALCULATE', label: this.$t('cusEntry.supplement20250211.financialTrialCalculation') }  // '财务试算'

                        ]
                    }
                }

            ],
            queryParam: {

            },
            tableHeader: [
                {
                    label: this.$t('marketBudget.marketStartTime'),  // '行情开始时间'
                    prop: 'startTime',
                    minWidth: 160,
                    formattor: (val) => {
                        return this.$parseTime(val, '{y}-{m}-{d}')
                    }
                },
                {
                    label: this.$t('marketBudget.marketEndTime'),  // '行情结束时间'
                    prop: 'endTime',
                    minWidth: 160,
                    formattor: (val) => {
                        return this.$parseTime(val, '{y}-{m}-{d}')
                    }
                },
                {
                    label: this.$t('marketBudget.materialCode'),  // '原材料编码'
                    prop: 'materialCode',
                    minWidth: 120
                },
                {
                    label: this.$t('marketBudget.materialName'),  // '原材料名称'
                    prop: 'materialName',
                    minWidth: 120
                },
                {
                    label: this.$t('contractMod.eqpSpecification'),  // '规格'
                    prop: 'specification',
                    minWidth: 120
                },
                {
                    label: this.$t('cusEntry.supplement20250211.pricePurpose'),  // '价格用途'
                    prop: 'priceUsage',
                    minWidth: 120
                },
                {
                    label: this.$t('common.vendorCode'),  // '供应商编码'
                    prop: 'vendorCode',
                    minWidth: 200
                },
                {
                    label: this.$t('common.companyName'),  // '供应商名称'
                    prop: 'vendorName',
                    minWidth: 120
                },
                {
                    label: this.$t('basicPrice.notaxPrice'),  // '不含税价'
                    prop: 'unitPrice',
                    minWidth: 120
                },
                {
                    label: this.$t('cusEntry.supplement20250211.taxRatePercentage'),  // '税率（%)'
                    prop: 'taxRate',
                    minWidth: 120
                },
                {
                    label: this.$t('vendorMod.currencyCode'),  // '币种'
                    prop: 'currencyCode',
                    minWidth: 180
                },

                {
                    label: this.$t('bid_mod.priceTax'),  // '汇率'
                    prop: 'exchangeRate',
                    minWidth: 120
                },
                {
                    label: this.$t('dataConfMod.settingGuide.step3.3'),  // '单位'
                    prop: 'unitName',
                    minWidth: 120
                },
                {
                    label: this.$t('components.eio.headers.remark'),  // '备注'
                    prop: 'remark',
                    minWidth: 120
                },

                {
                    label: this.$t('marketBudget.quotationStatus'),  // '行情状态'
                    prop: 'quotationStatus',
                    minWidth: 160,
                    formattor: (val) => {
                        if (val === 'DARFT') {
                            // '拟定'
                            return this.$t('vendorMod.DRAFT')
                        } else if (val === 'INVALID') {
                            // '失效'
                            return this.$t('common.inactive')
                        } else if (val === 'VALID') {
                            // '生效'
                            return this.$t('common.active')
                        } else {
                            return val
                        }
                    }
                },
                {
                    // '行情类型'
                    label: this.$t('marketBudget.quotationType'),
                    prop: 'priceType',
                    minWidth: 120,
                    formattor: (val) => {
                        if (val === 'DAY') {
                            // '日价格'
                            return this.$t('cusEntry.supplement20250211.dayPrice')
                        } else if (val === 'MONTH') {
                            // '月价格'
                            return this.$t('cusEntry.supplement20250211.monthPrice')
                        } else if (val === 'HALF_YEAR') {
                            // '半年度价格'
                            return this.$t('cusEntry.supplement20250211.halfYearPrice')
                        } else if (val === 'YEAR') {
                            // '年度价格'
                            return this.$t('cusEntry.supplement20250211.annualPrice')
                        } else if (val === 'TRY_CALCULATE') {
                            // '财务试算'
                            return this.$t('cusEntry.supplement20250211.financialTrialCalculation')
                        } else {
                            return val
                        }
                    }
                },
                {
                    // '工厂'
                    label: this.$t('qualitySynergy.factory'),
                    prop: 'orgName',
                    minWidth: 120
                },
                {
                    // '来源类型'
                    label: this.$t('contractMod.sourceType'),
                    prop: 'sourceType',
                    minWidth: 120
                },
                {
                    // '创建人'
                    label: this.$t('common.creator'),
                    prop: 'createdBy',
                    minWidth: 120
                },
                {
                    // '创建时间'
                    label: this.$t('common.creationTime'),
                    prop: 'creationDate',
                    minWidth: 120,
                    dataType: 'dateTime'
                },
                {
                    // '更新人'
                    label: this.$t('common.updatePeople'),
                    prop: 'lastUpdatedBy',
                    minWidth: 120
                },
                {
                    // '更新时间'
                    label: this.$t('components.workedProcess.headers.fdEndDate'),
                    prop: 'lastUpdateDate',
                    minWidth: 120,
                    dataType: 'dateTime'
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
                // '请先勾选需要删除的数据'
                this.$message.error(this.$t('cusEntry.supplement20250211.deleteSelectionCheckbox'))
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
                // '请先勾选需要生效的数据'
                this.$message.error(this.$t('cusEntry.supplement20250211.selectDataToEffective'))
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
                // '请先勾选需要失效的数据'
                this.$message.error(this.$t('cusEntry.supplement20250211.checkDataToInvalidate'))
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
            // '导入成功'
            this.$message.success(this.$t('components.eio.importSuccess'))
            this.getQuerydata(this.queryParam)
        },
        downloadItemTemplate () {
            downloadFileLink(
                '/api-cost/material-quotation/exportHead',
                // '原材料行情维护导入模板.xlsx'
                this.$t('cusEntry.supplement20250211.rawMaterialMarketMaintenanceImportTemplate')
            ).catch(() => {
                // '下载失败'
                this.$message.error(this.$t('components.eio.downloadFail'))
            })
        },
        downloadPurchaseTemplate () {
            downloadFileLink(
                '/api-cost/material-quotation/purchaseExportHead',
                // '原材料行情维护市况行情导入模板.xlsx'
                this.$t('cusEntry.supplement20250211.rawMaterialMarketMaintenanceTemplate')
            ).catch(() => {
                // '下载失败'
                this.$message.error(this.$t('components.eio.downloadFail'))
            })
        }

    }
}
</script>
