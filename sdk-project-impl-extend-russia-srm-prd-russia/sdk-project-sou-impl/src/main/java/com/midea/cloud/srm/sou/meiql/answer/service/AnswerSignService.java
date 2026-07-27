package com.midea.cloud.srm.sou.meiql.answer.service;

import com.midea.cloud.srm.model.sou.answer.dto.ReplayDTO;
import com.midea.cloud.srm.model.sou.answer.dto.SignReplayDTO;

import java.util.List;

/**
 * 备注
 * @author huangbf3
 */
public interface AnswerSignService {
    /**
     * 备注
     * @param replayDTO 备注
     * @return 备注
     */
    public SignReplayDTO sign(ReplayDTO replayDTO);
}
