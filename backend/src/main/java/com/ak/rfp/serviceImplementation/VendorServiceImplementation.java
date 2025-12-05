package com.ak.rfp.serviceImplementation;

import com.ak.rfp.dto.VendorRequest;
import com.ak.rfp.dto.VendorResponse;
import com.ak.rfp.entity.Vendor;
import com.ak.rfp.mapper.VendorMapper;
import com.ak.rfp.repository.VendorRepository;
import com.ak.rfp.service.VendorService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class VendorServiceImplementation implements VendorService {

    private final VendorRepository vendorRepository;
    private final VendorMapper vendorMapper;
    public VendorServiceImplementation(VendorRepository vendorRepository, VendorMapper vendorMapper){
        this.vendorRepository = vendorRepository;
        this.vendorMapper = vendorMapper;
    }
    @Override
    public VendorResponse createVendor(VendorRequest request) {
        Vendor vendor = new Vendor();
        vendor.setName(request.getName());
        vendor.setEmail(request.getEmail());
        Vendor saved = vendorRepository.save(vendor);
        return vendorMapper.toResponse(saved);
    }

    @Override
    public List<VendorResponse> getAllVendors() {
        List<Vendor> vendors = vendorRepository.findAllByOrderByIdAsc();
        return vendors.stream().map(vendorMapper::toResponse).toList();
    }

    public void deleteVendor(Long id){
        if(!vendorRepository.existsById(id)) throw new RuntimeException("Vendor not found");
        vendorRepository.deleteById(id);
    }

    @Override
    public VendorResponse updateVendor(Long id, Map<String, Object> updates) {
        if(!vendorRepository.existsById(id)) throw new RuntimeException("Vendor not found");
        Vendor vendor = vendorRepository.findById(id).orElseThrow();
        updates.keySet().forEach(key -> {
            if(key.equals("name")) vendor.setName((String)updates.get("name"));
            else if(key.equals("email")) vendor.setEmail((String)updates.get("email"));
        });
        vendorRepository.save(vendor);
        return vendorMapper.toResponse(vendor);
    }
}

