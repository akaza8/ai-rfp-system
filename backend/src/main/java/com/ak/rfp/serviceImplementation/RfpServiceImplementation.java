package com.ak.rfp.serviceImplementation;
import com.ak.rfp.dto.RfpCreateRequest;
import com.ak.rfp.dto.RfpResponse;
import com.ak.rfp.entity.Rfp;
import com.ak.rfp.entity.RfpItem;
import com.ak.rfp.mapper.RfpMapper;
import com.ak.rfp.repository.RfpRepository;
import com.ak.rfp.service.RfpService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class RfpServiceImplementation implements RfpService {

    private final RfpRepository rfpRepository;
    private final RfpMapper rfpMapper;

    public RfpServiceImplementation(RfpRepository rfpRepository, RfpMapper rfpMapper) {
        this.rfpRepository = rfpRepository;
        this.rfpMapper = rfpMapper;
    }
    @Transactional
    @Override
    public RfpResponse createRfp(RfpCreateRequest request) {
        Rfp rfp = new Rfp();
        rfp.setTitle(request.getTitle());
        rfp.setBudget(request.getBudget());
        rfp.setDeliveryTimelineDays(request.getDeliveryTimelineDays());
        rfp.setPaymentTerms(request.getPaymentTerms());
        rfp.setWarrantyTerms(request.getWarrantyTerms());
        List<RfpItem> item = new ArrayList<>();
        for (RfpCreateRequest.RfpItemRequest rfpItemRequest : request.getItems()) {
            RfpItem rfpItem = new RfpItem();
            rfpItem.setItemType(rfpItemRequest.getItemType());
            rfpItem.setQuantity(rfpItemRequest.getQuantity());
            rfpItem.setRequiredSpecs(rfpItemRequest.getRequiredSpecs());
            rfpItem.setRfp(rfp);
            item.add(rfpItem);
        }
        rfp.setItems(item);
        Rfp saved = rfpRepository.save(rfp);
        return rfpMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RfpResponse> getAllRfps() {
        List<Rfp> rfps = rfpRepository.findAll();
        return rfps.stream().map(rfpMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public void deleteRfp(Long id) {
        if (!rfpRepository.existsById(id)) {
            throw new RuntimeException("RFP not found");
        }
        rfpRepository.deleteById(id);
    }
}
