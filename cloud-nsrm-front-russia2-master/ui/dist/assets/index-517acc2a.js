import{N as NavTabs}from"./index-9a7f2446.js";import{ak as defineComponent,aE as DictClass,aq as defineSchemas,ah as generateXindexInOrder,c7 as yearMonthDayStartSelectorSegment,ad as expression,af as yearMonthDaySelectorSegment,ae as i18nExpression,ar as RenderEngine,at as DictSelect,n as normalizeComponent}from"./index-6b6051d8.js";const _sfc_main$1=defineComponent({__name:"list",setup(__props){const $getDictLabel=DictClass.getDictLabel,schema=defineSchemas({PayType:{type:"void","x-query-engine":{service:"cm",actions:{paginationQuery:{immediate:!0,transformResponse:res=>{const data=JSON.parse(res);return data.data?.ref?.PayType&&Object.keys(data.data.ref.PayType??{}).forEach(key=>{const item=data.data.ref.PayType[key];item.condFactor&&(item.condFactor=item.condFactor.split(",").map(v=>parseInt(v)))}),data}}}},"x-decorator":"QueryEngine","x-component":"el-container","x-component-props":{class:"flex-container the_contractTemplateList_wrapper",direction:"vertical"},properties:{query:{type:"object","x-query-engine-skip":!0,"x-component":"QueryFormByQueryEngine",properties:generateXindexInOrder({condFactor:{type:"string",title:"{{$t('contractMod.condFactor')}}","x-query-engine-query-operator":"contains"},payExplain:{type:"string",title:"{{$t('contractMod.payExplain')}}","x-query-engine-query-operator":"contains"},startDate:{title:"{{$t('contractMod.startDate')}}","x-query-engine-query-operator":">=",...yearMonthDayStartSelectorSegment}})},toolbar:{type:"void","x-component":"Space","x-component-props":{style:"margin-bottom: 16px"},properties:{add:{type:"void",title:"{{$t('common.add')}}","x-component":"RButton","x-component-props":{type:"primary","@click":expression(`() => {
                $form.query('*.Dialog').take().setComponentProps({ visible: true })
                setTimeout(() => {
                  $form.query('*.Dialog.form').take((field) => {
                    field.reset()
                  })
                })
              }`)}}}},table:{type:"array","x-component":"RenderTable","x-component-props":{class:"table-view-vxe-table",preColumns:"seq",openCustomTable:!0},properties:generateXindexInOrder({payTypeId:{type:"string","x-hidden":!0},condFactorId:{type:"string","x-hidden":!0},payExplain:{type:"string",title:"{{$t('contractMod.payExplain')}}","x-render-table-column":{minWidth:150}},condFactor:{type:"string",title:"{{$t('contractMod.condFactor')}}","x-render-table-column":{minWidth:150},"x-component":"DictSelect","x-component-props":{multiple:!0,code:"condFactorList","custom-select-type":"condFactorList"}},valueRange:{type:"string",title:"{{$t('contractMod.valueRange')}}","x-component":"DictSelect","x-component-props":{code:"valueRange"},"x-render-table-column":{width:110}},startDate:{...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                parseTime(row.startDate, '{y}-{m}-{d}')
              }`)},title:"{{$t('contractMod.startDate')}}","x-render-table-column":{width:130}},endDate:{...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                parseTime(row.endDate, '{y}-{m}-{d}')
              }`)},title:"{{$t('contractMod.endDate')}}","x-render-table-column":{width:130}},createdBy:{type:"string",title:"{{$t('contractMod.createdBy')}}","x-render-table-column":{width:120}},creationDate:{title:"{{$t('contractMod.creationDate')}}","x-query-engine-sort":"desc",...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                parseTime(row.creationDate, '{y}-{m}-{d}')
              }`)},"x-render-table-column":{width:120}},lastUpdatedBy:{type:"string",title:"{{$t('contractMod.lastUpdatedBy')}}","x-render-table-column":{width:120}},lastUpdateDate:{title:"{{$t('contractMod.lastUpdateDate')}}",...yearMonthDaySelectorSegment,"x-component-props":{...yearMonthDaySelectorSegment["x-component-props"],formatter:expression(`({ cellValue, row, column }) => {
                parseTime(row.lastUpdateDate, '{y}-{m}-{d}')
              }`)},"x-render-table-column":{minWidth:150}},operation:{type:"void",title:"{{$t('common.operation')}}","x-render-table-column":{width:130,fixed:"right"},"x-component":"RenderTableButtonList",properties:{management:{type:"void",title:"{{$t('bidMod.management')}}","x-component-props":{type:"text","@click":expression(`({ row }) => {
                    $form.query('*.Dialog').take().setComponentProps({ visible: true })

                    setTimeout(() => {
                      $form.query('*.Dialog.form').take(field => {
                        field.setValue({
                          ...row,
                          condFactorId: row.condFactorId.split(',').filter(Boolean).map(i => Number(i))
                        })
                      })
                    });
                  }`)}}}}})},Dialog:{type:"void",title:i18nExpression("bidMod.bulkMaintainFwAgreement"),"x-component":"RDialog","x-component-props":{beforeClose:expression(`(done, type, closeLoading) => {
            if ( type === 'ok') {
              $self.query('*.Dialog.form').take().submit(values => {
                $queryEngine.request.save({
                  ...values,
                  condFactorId: values.condFactorId.join(','),
                  condFactor: values.condFactorId.map(i => $getDictLabel('condFactorList', i)).join(',')
                }).then(() => {
                  $queryEngine.state.paginationManagement.refresh()
                  done()
                }).catch(err => {
                  closeLoading()
                })
              })
            } else {
              done()
            }
      }`)},properties:{form:{type:"object","x-component":"FormGrid","x-component-props":{maxColumns:2,columnGap:32,rowGap:0},properties:{payExplain:{type:"string",title:"{{$t('contractMod.payExplain')}}",required:!0,"x-decorator":"FormItem"},condFactorId:{type:"string",title:"{{$t('contractMod.condFactor')}}",required:!0,"x-component":"Cselect","x-component-props":{multiple:!0,code:"condFactorList","custom-select-type":"condFactorList"},"x-decorator":"FormItem"},startDate:{title:"{{$t('contractMod.startDate')}}","x-decorator":"FormItem",...yearMonthDaySelectorSegment},endDate:{title:"{{$t('contractMod.endDate')}}","x-decorator":"FormItem",...yearMonthDaySelectorSegment},valueRange:{type:"string",title:"{{$t('contractMod.valueRange')}}","x-component":"DictSelect","x-component-props":{code:"valueRange"},"x-decorator":"FormItem"}}}}}}}});return{__sfc:!0,$getDictLabel,schema,scope:{$getDictLabel},components:{Cselect:DictSelect},RenderEngine}}});var _sfc_render$1=function(){var _vm=this,_c=_vm._self._c,_setup=_vm._self._setupProxy;return _c(_setup.RenderEngine,{staticClass:"contractPaymentType",attrs:{schemaKey:"contractPaymentType",pageAttrs:_vm.$attrs,schema:_setup.schema,scope:_setup.scope,components:_setup.components}})},_sfc_staticRenderFns$1=[],__component__$1=normalizeComponent(_sfc_main$1,_sfc_render$1,_sfc_staticRenderFns$1,!1,null,null,null,null);const contractPaymentTypeList=__component__$1.exports,_sfc_main={name:"ContractPaymentTypeList",components:{NavTabs},data(){return{activeTab:"contractPaymentTypeList",tabs:[{title:this.$t("route.contractPaymentType"),name:"contractPaymentTypeList",component:contractPaymentTypeList,closable:!1}]}}};var _sfc_render=function(){var _vm=this,_c=_vm._self._c;return _c("NavTabs",{ref:"tabs",attrs:{"tabs-list":_vm.tabs,"cur-tab":_vm.activeTab}})},_sfc_staticRenderFns=[],__component__=normalizeComponent(_sfc_main,_sfc_render,_sfc_staticRenderFns,!1,null,null,null,null);const index=__component__.exports;export{index as default};
