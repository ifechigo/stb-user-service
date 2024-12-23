package com.suntrustbank.user.entrypoints.services;

import com.suntrustbank.user.core.errorhandling.exceptions.GenericErrorCodeException;
import com.suntrustbank.user.entrypoints.repository.models.Business;

public interface CashPointService {
    void createCashPoint(Business business) throws GenericErrorCodeException;
}
