import{ae as expression,aC as requiredValidatorSegment,ai as generateXindexInOrder}from"./index-17d0ccd5.js";const orderForm={catalogId:{type:"string","x-hidden":!0},vendorId:{type:"string","x-hidden":!0},vendorName:{type:"string",title:"{{$t('orderMod.buyerOrderSynergy.vendorName')}}","x-decorator":"FormItem","x-component":"QuickSearchWrapper","x-component-props":{readPretty:expression("$readOnly"),showKey:"companyName",propKey:"companyName",name:"scc_sup_company_info2","@close-quicksearch":expression(`(val) => {
        $values.vendorId = val ? val.companyId : null
        $values.vendorCode = val ? val.companyCode : null
        $values.vendorName = val ? val.companyName : null
      }`)},...requiredValidatorSegment},vendorCode:{type:"string",title:"{{$t('common.vendorCode')}}","x-decorator":"FormItem","x-component-props":{disabled:!0}},purchaseOrgId:{type:"string",title:"{{$t('bidMod.businessEntity')}}","x-decorator":"FormItem","x-component":"OrganizationSelector","x-component-props":{"read-pretty":expression("$readOnly"),"parent-id":-1,"node-type":"OU","@select":expression(`(val) => {
        $values.purchaseOrgId = val ? val.organizationId : null
        $values.purchaseOrgCode = val ? val.organizationCode : null
        $values.purchaseOrgName = val ? val.organizationName : null
        $values.invId = null
        $values.invCode = null
        $values.invName = null
      }`)},...requiredValidatorSegment},purchaseOrgCode:{type:"string","x-hidden":!0},purchaseOrgName:{type:"string","x-hidden":!0},invId:{type:"string",title:"{{$t('purchaseDemand.invOrg')}}","x-decorator":"FormItem","x-component":"OrganizationSelector","x-component-props":{"read-pretty":expression("$readOnly"),"node-type":"INV","parent-id":expression("$form.query('purchaseOrgId').get('value')"),"@select":expression(`(val) => {
        $values.invId = val ? val.organizationId : null
        $values.invCode = val ? val.organizationCode : null
        $values.invName = val ? val.organizationName : null
      }`)},...requiredValidatorSegment},invCode:{type:"string","x-hidden":!0},invName:{type:"string","x-hidden":!0},materialName:{type:"string",title:"{{$t('purSettlementMod.materialId')}}","x-decorator":"FormItem","x-component":"QuickSearchWrapper","x-component-props":{readPretty:expression("$readOnly"),"show-key":"materialName",name:"scc_base_material_item","@close-quicksearch":expression(`(val) => {
        $values.materialCode = val ? val.materialCode : null
        $values.materialName = val ? val.materialName : null
        $values.materialId = val ? val.materialId : null
        $values.categoryId = val ? val.categoryId : null
        $values.categoryName = val ? val.categoryName : null
        $values.categoryCode = val ? val.categoryCode : null
        $values.categoryFullName = val ? val.categoryFullName : null
      }`)},...requiredValidatorSegment},materialCode:{type:"string",title:"{{$t('mould.itemNumber')}}","x-decorator":"FormItem","x-component-props":{disabled:!0}},materialId:{type:"string","x-hidden":!0},categoryName:{type:"string",title:"{{$t('orderMod.categoryName')}}","x-decorator":"FormItem","x-component-props":{disabled:!0}},categoryId:{type:"string","x-hidden":!0},categoryCode:{type:"string","x-hidden":!0},categoryFullName:{type:"string","x-hidden":!0},catalogStatus:{type:"string",title:"{{$t('priceFormula.formulaStatus')}}","x-decorator":"FormItem",default:"DRAFT","x-component":"DictSelect","x-component-props":{code:"CATALOG_STATUS",disabled:!0}},createdFullName:{type:"string",title:"{{$t('dataConfMod.createdBy')}}","x-decorator":"FormItem","x-component-props":{disabled:!0}},creationDate:{type:"string",title:"{{$t('common.creationTime')}}","x-decorator":"FormItem","x-component-props":{disabled:!0}},dataSource:{type:"string",title:"{{$t('basicPrice.dataSource')}}","x-decorator":"FormItem",default:"MANUAL_CREATE","x-component":"DictSelect","x-component-props":{code:"PURCHASE_DATA_SOURCE",disabled:!0}},startDate:{type:"date",title:"{{$t('common.effectTime')}}","x-decorator":"FormItem","x-component-props":{disabled:!0,format:"yyyy-MM-dd","value-format":"yyyy-MM-dd",placeholder:"{{$t('common.selectDate')}}","picker-options":expression("cannotLessCurrentTimeOptions")}},endDate:{type:"date",title:"{{$t('dataConfMod.endDateTime')}}","x-decorator":"FormItem","x-component-props":{disabled:!0,format:"yyyy-MM-dd","value-format":"yyyy-MM-dd",placeholder:"{{$t('common.selectDate')}}","picker-options":expression("cannotLessCurrentTimeOptions")}}},attrForm={minOrderNum:{type:"string",title:"{{$t('dataConfMod.orderQuantityMinimum')}}","x-decorator":"FormItem"},minInventory:{type:"string",title:"{{$t('dataConfMod.minimumSafetyInventory')}}","x-decorator":"FormItem"},brand:{type:"string",title:"{{$t('dataConfMod.band')}}","x-decorator":"FormItem"},innerBoxMinPackNum:{type:"string",title:"{{$t('dataConfMod.minimumPackingQuantity')}}","x-decorator":"FormItem"},outerBoxPageNum:{type:"string",title:"{{$t('dataConfMod.maxPackingCarton')}}","x-decorator":"FormItem"},placeOrigin:{type:"string",title:"{{$t('purchase.PlaceOfOrigin')}}","x-decorator":"FormItem"},deliveryTime:{type:"string",title:"{{$t('purchase.DeliveryTime')}}","x-decorator":"FormItem"},packNum:{type:"string",title:"{{$t('purchase.NumberOfPackages')}}","x-decorator":"FormItem"},grossWeight:{type:"string",title:"{{$t('purchase.fullContainer')}}","x-decorator":"FormItem"},outerBoxLong:{type:"string",title:"{{$t('purchase.OuterBoxLength')}}","x-decorator":"FormItem"},outerBoxWide:{type:"string",title:"{{$t('purchase.OuterBoxWidth')}}","x-decorator":"FormItem"},outerBoxHide:{type:"string",title:"{{$t('purchase.OuterBoxHeight')}}","x-decorator":"FormItem"},innerBoxLong:{type:"string",title:"{{$t('purchase.InnerBoxLength')}}","x-decorator":"FormItem"},innerBoxWide:{type:"string",title:"{{$t('purchase.InnerBoxWidth')}}","x-decorator":"FormItem"},innerBoxHide:{type:"string",title:"{{$t('purchase.InnerBoxHeight')}}","x-decorator":"FormItem"},innerBoxWeight:{type:"string",title:"{{$t('purchase.InnerBoxWeight')}}","x-decorator":"FormItem"},innerBoxPackNum:{type:"string",title:"{{$t('purchase.NumberOfInnerCases')}}","x-decorator":"FormItem"}},file={toolbar:{type:"void","x-component":"Space","x-component-props":{style:"margin-bottom:16px;display:block;"},"x-reactions":expression(`field => {
      field.visible = !$readOnly
    }`),properties:{add:{type:"void",title:"{{$t('common.add')}}","x-component":"RButton","x-component-props":{"@click":expression(`() => {
            $form.query('purCatalogAttList').take(field => {
              field.value.push({
                fileId:null,
                fileName:null
              })
            })
          }`)}}}},purCatalogAttList:{type:"array","x-query-engine-skip":!0,"x-query-engine-relation":"purCatalogAttList:*","x-component":"RenderTable","x-component-props":{preColumns:"seq",editMode:!0,pagination:!1,maxHeight:"58vh",sortable:!1,primaryKey:"attachId",cascadeDeletion:!0},properties:generateXindexInOrder({attachId:{type:"string","x-hidden":!0},fileId:{type:"string",title:"{{$t('vendorMod.attachmentUpload')}}","x-render-table-column":{minWidth:"150px"},"x-component":"SrmCommonFile","x-component-props":{"extra-data":expression(`{
            fileModular: 'sup',
            fileFunction: 'purchaseDirectory',
            fileType: 'images'
          }`),"default-file":{fileId:expression("$self.value"),fileName:expression("$table.getRowByIndex($self.index).fileName || ''")},readonly:expression("$readOnly"),"@on-change":expression(`({file,$index}) => {
            const { fileId = '', fileName = '' } = file || {}
            const row = $table.getRowByIndex($self.index)
            row.fileId = fileId
            row.fileName = fileName
          }`)}},createdFullName:{type:"string",title:"{{$t('quota.uploadBy')}}","x-render-table-column":{minWidth:100},"x-read-pretty":!0},creationDate:{type:"string",title:"{{$t('components.fileupload.uploadDate')}}","x-render-table-column":{minWidth:100},"x-read-pretty":!0},operation:{type:"void",title:"{{$t('common.operation')}}","x-component":"RenderTableButtonList","x-render-table-column":{width:120},"x-reactions":expression(`field => {
          field.visible = !$readOnly
        }`),properties:{delete:{type:"void",title:"{{$t('common.delete')}}","x-component-props":{"@click":expression(`({rowIndex}) => {
                $table.remove(rowIndex)
              }`)}}}}})}};export{attrForm as a,file as f,orderForm as o};
