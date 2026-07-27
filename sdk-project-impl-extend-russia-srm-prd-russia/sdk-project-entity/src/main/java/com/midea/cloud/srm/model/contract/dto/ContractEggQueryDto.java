package com.midea.cloud.srm.model.contract.dto;

import lombok.Data;

/**
 * egg 请求类
 * @author 100014336 ganyh
 */
@Data
public class ContractEggQueryDto {

    private String htmlString;

    private Options options;


    @Data
    static class Options{

        private String format;

        private Margin margin;

        public static Options createWithMargin(String format,String bottom,String left,String right,String top){
            Options options = new Options();
            options.setFormat(format);
            options.setMargin(Margin.create(bottom,left,right,top));
            return options;
        }

        @Data
        static class Margin{
            private String bottom;
            private String left;
            private String right;
            private String top;
            public static Margin create(String bottom,String left,String right,String top){
                Margin margin = new Margin();
                margin.setBottom(bottom);
                margin.setLeft(left);
                margin.setRight(right);
                margin.setTop(top);
                return margin;
            }
        }
    }

    public static ContractEggQueryDto createA4PdfRequest(String htmlString){
        ContractEggQueryDto queryDto = new ContractEggQueryDto();
        queryDto.setHtmlString(htmlString);
        queryDto.setOptions(Options.createWithMargin("A4","1cm","1cm","1cm","1cm"));
        return queryDto;
    }
}
