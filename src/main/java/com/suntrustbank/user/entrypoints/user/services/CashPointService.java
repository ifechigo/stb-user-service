package com.suntrustbank.user.entrypoints.user.services;

import com.suntrustbank.user.core.errorhandling.exceptions.GenericErrorCodeException;
import com.suntrustbank.user.entrypoints.user.repository.models.Business;
import com.suntrustbank.user.entrypoints.user.repository.models.CashPoint;
import com.suntrustbank.user.entrypoints.user.repository.models.Organization;

public interface CashPointService {
    CashPoint createCashPoint(String authorizationHeader, Organization organization, Business business) throws GenericErrorCodeException;
    CashPoint createNGNCashPointWallet(CashPoint cashPoint) throws GenericErrorCodeException;
}
