package com.suntrustbank.user.entrypoints.services;

import com.suntrustbank.user.core.errorhandling.exceptions.GenericErrorCodeException;
import com.suntrustbank.user.entrypoints.repository.models.Business;
import com.suntrustbank.user.entrypoints.repository.models.CashPoint;

public interface CashPointService {
    CashPoint createCashPoint(Business business) throws GenericErrorCodeException;
}
