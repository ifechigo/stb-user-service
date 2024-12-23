package com.suntrustbank.user.entrypoints.services.impl;


import com.suntrustbank.user.core.errorhandling.exceptions.GenericErrorCodeException;
import com.suntrustbank.user.core.utils.UUIDGenerator;
import com.suntrustbank.user.entrypoints.repository.CashPointRepository;
import com.suntrustbank.user.entrypoints.repository.enums.Status;
import com.suntrustbank.user.entrypoints.repository.models.Business;
import com.suntrustbank.user.entrypoints.repository.models.CashPoint;
import com.suntrustbank.user.entrypoints.services.CashPointService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CashPointServiceImpl implements CashPointService {

    private final CashPointRepository cashPointRepository;

    @Override
    public void createCashPoint(Business business) throws GenericErrorCodeException {
        CashPoint cashPoint = new CashPoint();
        cashPoint.setId(UUIDGenerator.generate());
        cashPoint.setBusiness(business);
        cashPoint.setMain(true);
        cashPoint.setStatus(Status.ACTIVE);
        cashPointRepository.save(cashPoint);
    }
}
