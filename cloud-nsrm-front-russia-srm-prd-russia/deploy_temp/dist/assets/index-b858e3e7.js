import{N as NavTabs}from"./index-a035e78f.js";import{al as defineComponent,am as usePageHelper,ar as defineSchemas,ae as expression,ai as generateXindexInOrder,af as i18nExpression,bW as dataTimeSelectorSegment,ag as yearMonthDaySelectorSegment,as as RenderEngine,n as normalizeComponent}from"./index-17d0ccd5.js";import{C as CategorySelect}from"./categorySelect-d3400b4c.js";const _sfc_main$1=defineComponent({__name:"list",setup(__props){const{t:$t,app}=usePageHelper(),schema=defineSchemas({OrderConfig:{type:"void","x-decorator":"el-container","x-decorator-props":{class:"flex-container",direction:"vertical"},"x-component":"QueryEngine","x-query-engine":{service:"sup-ce"},properties:{bus:{type:"void","x-component":"BusEvent","x-component-props":{eventName:"refreshList","@listener":expression(`() => {
            $queryEngine.state.paginationManagement.refresh()
          }`)}},query:{type:"object","x-component":"QueryFormByQueryEngine","x-component-props":{immediateQueryForm:!0,action:"list"},"x-query-engine-skip":!0,properties:generateXindexInOrder({creationDate:{title:i18nExpression("cusEntry.orderMod.creationDate"),...dataTimeSelectorSegment,"x-query-engine-query-operator":"between"},orgId:{type:"string",title:i18nExpression("dataConfMod.orgId"),"x-component":"OrganizationSelector","x-component-props":{"node-type":"OU","parent-id":-1},"x-query-engine-relation-strict":!0,"x-query-engine-relation":"detailList"},createdBy:{type:"string",title:i18nExpression("cusEntry.orderMod.applyer"),"x-query-engine-query-operator":"contains"}})},toolbar:{type:"object","x-component":"ButtonList","x-component-props":{class:"list-form__toolbar"},properties:{add:{type:"void",title:i18nExpression("common.add"),"x-component":"Button","x-component-props":{"@click":expression(`() => {
                $form.query('table').take(field => {
                  field.componentProps.componentInstance.addRow('unshift', {
                    editable: true,
                    creatorOrgName: app.$store.getters.userInfo.ceeaCompany
                  })
                })
              }`)}}}},table:{type:"array","x-component":"RenderTable","x-component-props":{class:"table-view-vxe-table",style:"flex: 1",preColumns:"seq",editMode:!0,openCustomTable:!0},properties:generateXindexInOrder({configId:{type:"string","x-hidden":!0},configNum:{type:"string",title:i18nExpression("cusEntry.orderMod.configNum"),"x-render-table-column":{width:120},"x-read-pretty":!0},configName:{type:"string",title:i18nExpression("cusEntry.orderMod.configName"),"x-render-table-column":{width:120},"x-read-pretty":"{{!$table.getRowByIndex($self.index)?.editable}}"},categoryCode:{type:"string","x-hidden":!0,"x-query-engine-relation":"detailList"},categoryId:{type:"string","x-hidden":!0,"x-query-engine-relation":"detailList"},categoryName:{type:"string",title:i18nExpression("cusEntry.orderMod.materialCategory"),"x-component":"{{ $table.getRowByIndex($self.index)?.editable ?  'CCategorySelect' : 'Input'}}","x-reactions":expression(`(field) => {
              let row = $table.getRowByIndex($self.index)
              $self.value = (row?.categoryList?.map(item => item.categoryName) || []).join()
            }`),"x-component-props":{multiple:!0,"@select":expression(`(data) => {
                let row = $table.getRowByIndex($self.index)
                row.categoryList = data?.map(item => {
                  const {
                    categoryJournalId,
                    ...other
                  } = item
                  return other
                })
              }`)},"x-render-table-column":{width:150},"x-query-engine-relation":"detailList","x-read-pretty":"{{!$table.getRowByIndex($self.index)?.editable}}"},orgName:{type:"string","x-hidden":!0,"x-query-engine-relation":"detailList"},orgCode:{type:"string","x-hidden":!0,"x-query-engine-relation":"detailList"},orgId:{type:"string",title:i18nExpression("cusEntry.orderMod.companyName"),"x-component":"{{ $table.getRowByIndex($self.index)?.editable ? 'OrganizationSelector' : 'Input'}}","x-component-props":{"node-type":"OU","parent-id":-1,multiple:!0,"@select":expression(`selects => {
                /* 组件下拉选择存在bug，这里去重下 */
                let set = new Set()
                selects = selects?.filter(item => !set.has(item.organizationId) && set.add(item.organizationId))
                let row = $table.getRowByIndex($self.index)
                row.orgList = selects?.map(item => {
                  const {
                    organizationId:orgId,
                    organizationCode:orgCode,
                    organizationName:orgName
                  } = item
                  return {
                    orgId,
                    orgCode,
                    orgName
                  }
                })
              }`)},"x-reactions":expression(`() => {
              let row = $table.getRowByIndex($self.index)
              $self.value = row?.editable ? (row?.orgList?.map(item => item.orgId) || []) : row?.orgList?.map(item => item.orgName).join()
            }`),"x-render-table-column":{width:200},"x-query-engine-relation":"detailList","x-read-pretty":"{{!$table.getRowByIndex($self.index)?.editable}}"},createdBy:{type:"string",title:i18nExpression("cusEntry.orderMod.createdBy"),"x-render-table-column":{width:120},"x-read-pretty":!0},creationDate:{title:i18nExpression("common.creationTime"),...yearMonthDaySelectorSegment,"x-render-table-column":{minWidth:120},"x-read-pretty":!0},creatorOrgName:{type:"string",title:i18nExpression("cusEntry.orderMod.creatorOrgName"),"x-render-table-column":{minWidth:120},"x-read-pretty":!0},lastUpdateDate:{type:"string","x-hidden":!0,"x-query-engine-sort":"desc"},operation:{type:"void","x-render-table-column":{title:i18nExpression("common.operation"),width:120,fixed:"right"},"x-component":"RenderTableButtonList",properties:{edit:{type:"void",title:i18nExpression("common.edit"),"x-component-props":{"@click":expression(`({rowIndex, row}) => {
                    row.editable = true
                    const path = 'table.' + rowIndex + '.*'
                    const propsList = ['configName', 'categoryName', 'orgName']
                    $form.query(path).forEach(field => {
                      if (propsList.includes(field.props.name)) {
                        field.editable = true
                      }
                    })
                  }`)},"x-visible":"{{!$table.getRowByIndex($self.index).editable}}"},save:{type:"void",title:i18nExpression("common.save"),"x-component-props":{"@click":expression(`({rowIndex, row}) => {
                    $queryEngine.request.save(row, { customizeAction: 'saveOrUpdate'}).then(res => {
                      $message.success($t('common.successSave'))
                      $bus.$emit('refreshList')
                    })
                  }`)},"x-visible":"{{$table.getRowByIndex($self.index).editable ?? false}}"},delete:{type:"void",title:i18nExpression("common.delete"),"x-component-props":{"@click":expression(`({row}) => {
                    const payload = {
                      '$delete': row.configId,
                      'detailList': [{
                        '$delete': '*'
                      }]
                    }
                    $queryEngine.request.delete(payload).then(res => {
                      $message.success($t('common.successDelete'))
                      $bus.$emit('refreshList')
                    })
                  }`)}}}}})}}}});return{__sfc:!0,$t,app,schema,scope:{app,$t},components:{CategorySelect},RenderEngine}}});var _sfc_render$1=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{attrs:{schemaKey:"autoOrderConfig",scope:_setup.scope,schema:_setup.schema,components:_setup.components}})},_sfc_staticRenderFns$1=[],__component__$1=normalizeComponent(_sfc_main$1,_sfc_render$1,_sfc_staticRenderFns$1,!1,null,null,null,null);const autoOrderConfigList=__component__$1.exports,_sfc_main={name:"AutoOrderConfig",components:{NavTabs},data(){return{activeTab:"autoOrderConfigList",tabs:[{title:this.$t("cusEntry.route.autoOrderConfig"),name:"autoOrderConfigList",component:autoOrderConfigList,closable:!1}]}}};var _sfc_render=function(){var _vm=this,_c=_vm._self._c;return _c("NavTabs",{ref:"tabs",attrs:{"tabs-list":_vm.tabs,"cur-tab":_vm.activeTab}})},_sfc_staticRenderFns=[],__component__=normalizeComponent(_sfc_main,_sfc_render,_sfc_staticRenderFns,!1,null,null,null,null);const index=__component__.exports;export{index as default};
