package com.ak.rfp.service;

import com.ak.rfp.entity.Rfp;
import com.ak.rfp.entity.Vendor;

public interface EmailService {
    void sendRfpToVendor(Rfp rfp, Vendor vendor);

}
