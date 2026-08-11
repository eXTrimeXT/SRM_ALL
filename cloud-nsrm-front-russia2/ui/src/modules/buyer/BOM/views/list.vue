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
            <!-- 日期选择 -->
          <el-date-picker
            v-model="scope.date"
            type="daterange"
            value-format="yyyy-MM-dd"
            :format="$formatDatePicker"
            range-separator="~"
            :start-placeholder="$t('cusEntry.supplement20250211.queryDateFrom')"
            :end-placeholder="$t('cusEntry.supplement20250211.queryDateTo')"
          />
        </template>
        <template #isExpansion="{scope}">
          <el-checkbox v-model="scope.isExpansion" />
        </template>
      </FormWrapper>

      <MainHeader :lSpan="22" :rSpan="2">
        <template slot="left">
          <AuthorityButton type="primary" @click="exportHandle">
            <!-- 导出 -->
            {{ $t("common.export") }}
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
                    label: this.$t('qualitySynergy.factory'), // '工厂'
                    type: 'OUorganizationSelector',
                    rules: { required: true, message: this.$t('cusEntry.supplement20250211.selectFactory') }  // '请选择工厂'
                },
                {
                    prop: 'rootId',
                    label: 'MTOC'
                },
                {
                    prop: 'to',
                    label: this.$t('cusEntry.supplement20250211.queryDate'),  // '查询日期'
                    type: 'date',
                    rules: { required: true, message: this.$t('cusEntry.supplement20250211.inputSearchDate') } // '请输入查询日期'
                },
                {
                    prop: 'partFid',
                    label: this.$t('cusEntry.supplement20250211.parentPartNumber')  // '父件号'

                },
                {
                    prop: 'partId',
                    label: this.$t('cusEntry.supplement20250211.subPartNumber') // '子件号'

                },
                {
                    prop: 'isExpansion',
                    label: this.$t('cusEntry.supplement20250211.isCrossTable'),  // '是否跨表'
                    slot: 'isExpansion',
                    type: 'slot'
                }

            ],
            tableHeader: [
                {
                    prop: 'orgName',
                    label: this.$t('cusEntry.supplement20250211.factoryName'),  // '工厂名称'
                    minWidth: 160
                },
                {
                    prop: 'rootId',
                    label: 'MTOC',
                    minWidth: 160
                },
                {
                    prop: 'partFId',
                    label: this.$t('cusEntry.supplement20250211.parentPartNumber'), // '父件号'
                    minWidth: 160
                },

                {
                    prop: 'partName',
                    label: this.$t('cusEntry.supplement20250211.subItemName'),  // '子件名'
                    minWidth: 160
                },
                {
                    prop: 'partId',
                    label: this.$t('cusEntry.supplement20250211.subPartNumber'),  // '子件号'
                    minWidth: 160
                },
                {
                    prop: 'simpleNo',
                    label: this.$t('cusEntry.supplement20250211.subItemSn'),  // '子件简号'
                    minWidth: 160
                },
                {
                    prop: 'lineNo',
                    label: this.$t('cusEntry.supplement20250211.positionNumber'),  // '位置号'
                    minWidth: 160
                },
                {
                    prop: 'orgCode',
                    label: this.$t('cusEntry.supplement20250211.factoryCode'),  // '工厂编码'
                    minWidth: 160
                },

                {
                    prop: 'quantity',
                    label: this.$t('bid_mod.quantity'),  // '数量'
                    minWidth: 160
                },
                {
                    prop: 'purchaseType',
                    label: this.$t('bid_mod.purchaseType'),  // '采购类型'
                    minWidth: 160
                },
                {
                    prop: 'usageAmountUnit',
                    label: this.$t('dataConfMod.settingGuide.step3.3'),  // '单位'
                    minWidth: 160
                },

                {
                    prop: 'changeEffDate',
                    label: this.$t('common.effectTime'),  // '生效时间'
                    minWidth: 160,
                    formattor: (val) => this.$parseTime(val, '{y}-{m}-{d}')
                },
                {
                    prop: 'changeUneffDate',
                    label: this.$t('vendorMod.expirationTime'),  // '失效时间'
                    minWidth: 160,
                    formattor: (val) => this.$parseTime(val, '{y}-{m}-{d}')
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
                    // '勾选导出只能选择一条数据'
                    this.$message.error(this.$t('cusEntry.supplement20250211.checkExportSingleData'))
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
                downloadFileLinkByPost('/api-sou/bom/exportBom', `BOM-${this.$t('common.export')}${parseTime(new Date())}.xlsx`, params).catch(() => {
                    // '下载失败'
                    this.$message.error(this.$t('components.eio.downloadFail'))
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
