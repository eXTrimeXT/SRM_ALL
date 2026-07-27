package com.midea.cloud.srm.model.pj.sou.qa.vo;

import com.midea.cloud.srm.model.base.scene.entity.SceneFile;
import com.midea.cloud.srm.model.pj.sou.qa.dto.SouAnswerEditDTO;
import com.midea.cloud.srm.model.pj.sou.qa.entity.SouAnswer;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

import java.util.List;

/**
 * @author zhangwk12@midea.com
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("招标澄清信息")
public class SouAnswerEditVO extends SouAnswerEditDTO {

    /**
     * 便捷转换
     */
    public static SouAnswerEditVO convert(SouAnswer answer,
                                          List<SceneFile> sceneFiles) {
        SouAnswerEditVO vo = new SouAnswerEditVO();
        BeanUtils.copyProperties(answer, vo);
        vo.setSceneFiles(sceneFiles);

        return vo;
    }

}
