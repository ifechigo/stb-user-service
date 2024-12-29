package com.suntrustbank.user.entrypoints.services.impl;


import com.suntrustbank.user.core.errorhandling.exceptions.GenericErrorCodeException;
import com.suntrustbank.user.core.utils.RandomNumberGenerator;
import com.suntrustbank.user.core.utils.UUIDGenerator;
import com.suntrustbank.user.entrypoints.repository.CashPointRepository;
import com.suntrustbank.user.entrypoints.repository.enums.Status;
import com.suntrustbank.user.entrypoints.repository.models.Business;
import com.suntrustbank.user.entrypoints.repository.models.CashPoint;
import com.suntrustbank.user.entrypoints.services.CashPointService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CashPointServiceImpl implements CashPointService {

    private final CashPointRepository cashPointRepository;

    @Override
    @Transactional
    public CashPoint createCashPoint(Business business) throws GenericErrorCodeException {
        CashPoint cashPoint = new CashPoint();
        cashPoint.setReference(UUIDGenerator.generate());
        cashPoint.setBusiness(business);
        cashPoint.setMain(true);
        cashPoint.setStatus(Status.ACTIVE);
        //Todo place holder
        cashPoint.setWalletId(RandomNumberGenerator.generate(10));
        cashPointRepository.save(cashPoint);
        return cashPoint;
    }
}
