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
            新增
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
						label: '车型编码'
					},
					{
						prop: 'outputConfirmId',
						label: '认可清单编号'
					},
					{
						prop: 'createdBy',
						label: '创建人'
					},
					{
						prop: 'toolingCode',
						label: '零件编号'
					}
				],
				tableHeader: []	
			}
    },
    created () {
			this.tableHeader = [
				{
					prop: 'modelCode',
					label: '车型编码',
					minWidth: 150
				},
				{
					prop: 'modelName',
					label: '车型名称',
					minWidth: 150
				},
				{
					prop: 'outputConfirmId',
					label: '认可清单编号',
					minWidth: 150,
					showType: 'button',
					btnStyle: 'text',
					callback: row => this.goDetail(row)
				},
				{
					prop: 'status',
					label: '状态',
					minWidth: 100,
					dataType: 'dict',
					code: 'OUTPUT_CONFIRM_STATUS'
				},
				{
					prop: 'createdBy',
					label: '创建人',
					minWidth: 100
				},
				{
					prop: 'creationDate',
					label: '创建时间',
					minWidth: 150
				},
				{
					prop: 'remarks',
					label: '备注',
					minWidth: 100
				},
				{
					label: '操作',
					minWidth: 100,
					showType: 'buttons',
					btnStyle: 'text',
					fixed: 'right',
					buttons: [
						{
							formattor: () => '编辑',
							show: row => {
								if (row.status === 'DART') return true
								return false
							},
							callback: row => { this.eidtHandle(row) },
							code: 'sup:outputConfirm:edit'
						},
						{
							formattor: () => '删除',
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
                title: '量产认可新增',
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
                    this.$message.success('删除成功')
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
                title: '量产认可编辑' + row.outputConfirmId,
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
                title: '量产认可查看' + row.outputConfirmId,
                name
            }
            this.$emit('tab-add', tab)
        }
    }
}
</script>
