import{al as defineComponent,ar as defineSchemas,ai as generateXindexInOrder,af as i18nExpression,ae as expression,bM as exportExcelSegment,bN as queryFieldStatePropertyExpression,as as RenderEngine,bO as $dayjs,n as normalizeComponent}from"./index-17d0ccd5.js";const _sfc_main=defineComponent({__name:"index",setup(__props){const scope={$dayjs},schema=defineSchemas({DrawingsHead:{type:"void","x-query-engine":{service:"base",actions:{paginationQuery:{immediate:!0},save:{loading:!0}}},"x-decorator":"QueryEngine","x-component":"PageContainer",properties:{query:{type:"object","x-query-engine-skip":!0,"x-component":"QueryFormByQueryEngine",properties:generateXindexInOrder({materialCode:{type:"string",title:i18nExpression("common.materialCode"),"x-component":"QuickSearchWrapper","x-component-props":{showKey:"materialCode",propKey:"materialCode",name:"scc_base_material_item"}},drawingsVersion:{type:"number",title:i18nExpression("drawingshead.drawingVersion"),"x-component-props":{type:"number",negative:!1,zero:!1}},drawingsStatus:{type:"string",title:i18nExpression("drawingshead.drawingStatus"),"x-component":"DictSelect","x-component-props":{code:"DRAWINGS_STATUS"}}})},toolbar:{type:"void","x-component":"Space","x-component-props":{style:"margin-bottom: 16px"},properties:{add:{type:"void",title:i18nExpression("common.add"),"x-component":"RButton","x-component-props":{type:"primary","@click":expression('() => $form.query(".table").take().componentProps.componentInstance.addRow("unshift")')}},exportExcel:{type:"void","x-component":"ExportExcel","x-component-props":{...exportExcelSegment,type:"default",pageUrl:"/api-base/api-ql/DrawingsHead/query",tableHeader:queryFieldStatePropertyExpression("DrawingsHead.table","data.columns"),dictCodes:{drawingsType:"DRAWING_TYPE",isLatest:"DRAWING_IS_LATEST",drawingsStatus:"DRAWINGS_STATUS"}}},activeHandel:{type:"void",title:i18nExpression("common.active"),"x-component":"RButton","x-component-props":{type:"normal","@click":expression(`({ rowIndex }) => {
                const rows = $self.query('.table').take()
                  .componentProps
                  .componentInstance
                  .getCheckboxRecords()
                rows.forEach(val => {
                  val.drawingsStatus = 'Y'
                })
                $queryEngine.request.save(rows, { action: 'takeEffect', loading: true }).then(() => {
                    $queryEngine.state.paginationManagement.refresh()
                })
              }`)}},inactiveHandel:{type:"void",title:i18nExpression("common.inactive"),"x-component":"RButton","x-component-props":{type:"normal","@click":expression(`({ rowIndex }) => {
                const rows = $self.query('.table').take()
                  .componentProps
                  .componentInstance
                  .getCheckboxRecords()
                rows.forEach(val => {
                  val.drawingsStatus = 'N'
                })
                $queryEngine.request.save(rows, { action: 'failure', loading: true }).then(() => {
                    $queryEngine.state.paginationManagement.refresh()
                })
              }`)}}}},table:{type:"array","x-component":"RenderTable","x-component-props":{class:"table-view-vxe-table",preColumns:"checkbox,seq",editMode:"multi-row",openCustomTable:!0},properties:generateXindexInOrder({drawingsId:{type:"string","x-hidden":!0},materialCode:{type:"string",title:i18nExpression("common.materialCode"),"x-component":"QuickSearchWrapper","x-component-props":{name:"scc_base_material_item","show-key":"materialCode",readPretty:expression("!$table.getSelfRowEditable($self)"),"@close-quicksearch":expression(`(val) => {
                  const row = $table.getRowByIndex($self.index)
                  row.materialId = val.materialId || null
                  row.materialCode = val.materialCode || null
                  row.materialName = val.materialName || null
              }`)},"x-render-table-column":{minWidth:120}},materialName:{type:"string",title:i18nExpression("common.materialName"),"x-render-table-column":{minWidth:120,skipEditable:!0}},drawingsType:{type:"string",title:i18nExpression("drawingshead.drawingType"),"x-component":"DictSelect","x-component-props":{code:"DRAWING_TYPE"},"x-render-table-column":{minWidth:120}},drawingsVersion:{type:"string",title:i18nExpression("drawingshead.drawingVersion"),"x-render-table-column":{minWidth:120,skipEditable:!0}},isLatest:{type:"string",title:i18nExpression("drawingshead.isItCurrent"),"x-component":"DictSelect","x-component-props":{code:"DRAWING_IS_LATEST"},"x-render-table-column":{minWidth:130,skipEditable:!0}},fileuploadAddress:{type:"string",title:i18nExpression("drawingshead.drawingAddress"),"x-render-table-column":{minWidth:100}},fileuploadId:{type:"number","x-hidden":!0,default:null,"x-render-table-column":{minWidth:100}},attachType:{type:"string","x-hidden":!0,"x-render-table-column":{minWidth:100}},attachName:{type:"string",title:i18nExpression("drawingshead.drawingAttachment"),"x-component":"SrmCommonFile","x-component-props":{extraData:{fileModular:"workFlow",fileFunction:"workflowReport",fileType:"images",fileMaxSize:10*1024*1024},readonly:!1,defaultFile:expression(`{
                fileId: $table.getRowByIndex($self.index)?.fileuploadId,
                fileName: $self.value
              }`),"@on-change":expression(`({ file }) => {
                const { fileId , fileName , fileType } = file
                const row = $table.getRowByIndex($self.index)
                row.fileuploadId = fileId
                row.attachName = fileName
                row.attachType = fileType
              }`)},"x-render-table-column":{minWidth:150}},drawingsStatus:{type:"string",title:i18nExpression("drawingshead.drawingStatus"),"x-component":"DictSelect","x-component-props":{code:"DRAWINGS_STATUS"},"x-render-table-column":{minWidth:130}},creationDate:{type:"string",title:i18nExpression("common.creationTime"),"x-render-table-column":{minWidth:130,skipEditable:!0},"x-query-engine-sort":"desc"},lastUpdateDate:{type:"string",title:i18nExpression("common.updateTime"),"x-render-table-column":{minWidth:140,skipEditable:!0}},operation:{type:"void",title:i18nExpression("common.operation"),"x-render-table-column":{width:150,fixed:"right"},"x-component":"RenderTableButtonList",properties:{editRow:{type:"void",title:i18nExpression("common.save"),"x-visible":expression("$table.getSelfRowEditable($self)"),"x-component-props":{"@click":expression(`({ row }) => $queryEngine.request.save(row).then(() => {
                    $table.cancelEditRow($index)
                    $queryEngine.state.paginationManagement.refresh()
                  })`)}},cancelEditRow:{type:"void",title:i18nExpression("common.cancel"),"x-visible":expression("$table.getSelfRowEditable($self)"),"x-component-props":{"@click":expression("({ rowIndex }) => $table.cancelEditRow(rowIndex)")}}}}})}}}});return{__sfc:!0,scope,schema,components:{},RenderEngine}}});var _sfc_render=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{attrs:{pageAttrs:_vm.$attrs,schema:_setup.schema,components:_setup.components,scope:_setup.scope,schemaKey:"contractElement"}})},_sfc_staticRenderFns=[],__component__=normalizeComponent(_sfc_main,_sfc_render,_sfc_staticRenderFns,!1,null,null,null,null);const index=__component__.exports;export{index as default};
