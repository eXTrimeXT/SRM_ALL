<template>
  <el-container class="flex-container toolinginfo_list_wrapper" direction="vertical">
    <el-main>
      <FormWrapper
        ref="formWrapper"
        :formArray="filterConfig"
        @getFormData="getQuerydata"
      />
      <MainHeader :lSpan="22" :rSpan="2">
        <template slot="left">
          <AuthorityButton type="primary" code="sup:outputConfirm:add" @click="addHandle">
            <!-- 新增 -->
            {{ $t("common.add") }}
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
        :comActive="$attrs['changeTab']"
        url="/api-ppap/outputConfirm/getOutputConfirmByParam"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MainHeader from 'lib@/components/Table/MainHeader'
import productionApprovalEdit from './edit'
import { productionApproval } from 'modb@/productionPrepare/api'
export default {
    name: 'ProductionApprovalList',
    components: {
        TableView,
        FormWrapper,
        MainHeader
    },
    mixins: [tabTodoWatch, tabTodoMixin],
    data () {
			return {
				pageSize: 15,
				queryParam: {},
				filterConfig: [
					{
						prop: 'modelCode',
                        // '车型编码'
						label: this.$t('problemManagement.motorcycleTypeCode')
					},
					{
						prop: 'outputConfirmId',
                        // '认可清单编号'
						label: this.$t('productionPrepare.outputConfirmCode')
					},
					{
						prop: 'createdBy',
                        // '创建人'
						label: this.$t('common.creator')
					},
					{
						prop: 'toolingCode',
                        // '零件编号'
						label: this.$t('productionPrepare.toolingCode')
					}
				],
				tableHeader: []	
			}
    },
    created () {
			this.tableHeader = [
				{
					prop: 'modelCode',
                    // '车型编码'
					label: this.$t('problemManagement.motorcycleTypeCode'),
					minWidth: 150
				},
				{
					prop: 'modelName',
                    // '车型名称'
					label: this.$t('problemManagement.motorcycleTypeName'),
					minWidth: 150
				},
				{
					prop: 'outputConfirmId',
                    // '认可清单编号'
					label: this.$t('productionPrepare.outputConfirmCode'),
					minWidth: 150,
					showType: 'button',
					btnStyle: 'text',
					callback: row => this.goDetail(row)
				},
				{
					prop: 'status',
                    // '状态'
					label: this.$t('components.stratProcess.headers.docStatusValue'),
					minWidth: 100,
					dataType: 'dict',
					code: 'OUTPUT_CONFIRM_STATUS'
				},
				{
					prop: 'createdBy',
                    // '创建人'
					label: this.$t('common.creator'),
					minWidth: 100
				},
				{
					prop: 'creationDate',
                    // '创建时间'
					label: this.$t('common.creationTime'),
					minWidth: 150,
                    dataType: 'dateTime'
				},
				{
					prop: 'remarks',
                    // '备注'
					label: this.$t('components.eio.headers.remark'),
					minWidth: 100
				},
				{
                    // '操作'
					label: this.$t('components.headers.operation'),
					minWidth: 100,
					showType: 'buttons',
					btnStyle: 'text',
					fixed: 'right',
					buttons: [
						{
                            // '编辑'
							formattor: () => this.$t('common.edit'),
							show: row => {
								if (row.status === 'DART') return true
								return false
							},
							callback: row => { this.eidtHandle(row) },
							code: 'sup:outputConfirm:edit'
						},
						{
                            // '删除'
							formattor: () => this.$t('components.common.delete'),
							show: row => {
								if (row.status === 'DART') return true
								return false
							},
							callback: row => { this.deleteHandle(row) },
							code: 'sup:outputConfirm:delete'
						}
					]
				}   
			]
			this.$nextTick(()=>{
				this.getQuerydata()
			})
    },
    methods: {
        addHandle () {
            const tab = {
                component: productionApprovalEdit,
                params: {
                    flag: 'add',
                    tabName: 'productionApprovalEdit'
                },
                // '量产认可新增'
                title: this.$t('productionPrepare.outputConfirmAdd'),
                name: 'productionApprovalEdit'
            }
            this.$emit('tab-add', tab)
        },
        deleteHandle (row) {
            let data = {
                outputConfirmId: row.outputConfirmId
            }
           productionApproval.deleteInventory(data).then(res => {
                if (res.code === '0') {
                    // '删除成功'
                    this.$message.success(this.$t('common.successDelete'))
                    this.getQuerydata(this.queryParam)
                }
            })
        },
        getQuerydata (params) {
            this.queryParam = params
            this.$nextTick(() => {
                this.$refs.list.query()
            })
        },
        reloadData () {
            this.getQuerydata(this.queryParam)
        },
        eidtHandle (row) {
            let name = 'productionApprovalEdit' + row.outputConfirmId
            const tab = {
                component: productionApprovalEdit,
                params: {
                    flag: 'edit',
                    form: row,
                    tabName: name
                },
                // '量产认可编辑'
                title: this.$t('cusEntry.supplement20250211.productApprovalEdit') + row.outputConfirmId,
                name
            }
            this.$emit('tab-add', tab)
        },
        goDetail (row) {
            let name = 'productionApprovalRead' + row.outputConfirmId
            const tab = {
                component: productionApprovalEdit,
                params: {
                    flag: 'read',
                    id: row.outputConfirmId,
                    form: row,
                    tabName: name
                },
                // '量产认可查看'
                title: this.$t('cusEntry.supplement20250211.productionApprovalCheck') + row.outputConfirmId,
                name
            }
            this.$emit('tab-add', tab)
        }
    }
}
</script>
