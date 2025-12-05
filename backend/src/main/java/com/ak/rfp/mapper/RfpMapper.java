package com.ak.rfp.mapper;

import com.ak.rfp.dto.RfpResponse;
import com.ak.rfp.entity.Rfp;
import com.ak.rfp.entity.RfpItem;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RfpMapper {
    public RfpResponse toResponse(Rfp rfp) {
        RfpResponse resp = new RfpResponse();
        resp.setId(rfp.getId());
        resp.setTitle(rfp.getTitle());
        resp.setBudget(rfp.getBudget());
        resp.setDeliveryTimelineDays(rfp.getDeliveryTimelineDays());
        resp.setPaymentTerms(rfp.getPaymentTerms());
        resp.setWarrantyTerms(rfp.getWarrantyTerms());

        if (rfp.getItems() != null) {
            List<RfpResponse.RfpItemResponse> itemResponses = new ArrayList<>();
            for (RfpItem item : rfp.getItems()) {
                RfpResponse.RfpItemResponse ir = new RfpResponse.RfpItemResponse();
                ir.setId(item.getId());
                ir.setItemType(item.getItemType());
                ir.setQuantity(item.getQuantity());
                ir.setRequiredSpecs(item.getRequiredSpecs());
                itemResponses.add(ir);
            }
            resp.setItems(itemResponses);
        }

        return resp;
    }
}
