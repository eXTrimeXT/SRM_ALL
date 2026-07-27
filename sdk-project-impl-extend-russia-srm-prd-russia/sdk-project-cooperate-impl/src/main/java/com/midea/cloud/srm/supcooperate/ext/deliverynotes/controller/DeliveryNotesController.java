package com.midea.cloud.srm.supcooperate.ext.deliverynotes.controller;

import com.midea.cloud.srm.supcooperate.ext.deliverynotes.dto.QueryDeliveryNote;
import com.midea.cloud.srm.supcooperate.ext.deliverynotes.service.ExtDeliveryNoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;

/**
 * @author srm
 * @Description: for srm
 * @date 2024/6/6
 */

/**
 * 送货单导出
 */
@RequestMapping("/deliveryNotes")
@RestController
public class DeliveryNotesController {
    @Autowired
    ExtDeliveryNoteService extDeliveryNoteService;
    //送货单导出Excel
    @PostMapping("/getDeliveryNotesUpload")
    public void upload(HttpServletResponse response,@RequestBody QueryDeliveryNote queryDeliveryNote) throws IOException, ParseException {
        extDeliveryNoteService.deliveryNotesUpload(response,queryDeliveryNote);
    }
}
