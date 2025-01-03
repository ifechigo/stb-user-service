package com.suntrustbank.user.entrypoints.services;

import com.suntrustbank.user.core.errorhandling.exceptions.GenericErrorCodeException;
import com.suntrustbank.user.entrypoints.repository.models.Business;
import com.suntrustbank.user.entrypoints.repository.models.CashPoint;
import com.suntrustbank.user.entrypoints.repository.models.Organization;
import com.suntrustbank.user.services.dtos.TransactionUserRequestDto;

public interface CashPointService {
    CashPoint createCashPoint(String authorizationHeader, Organization organization, Business business) throws GenericErrorCodeException;
    CashPoint createNGNCashPointWallet(CashPoint cashPoint) throws GenericErrorCodeException;
}
