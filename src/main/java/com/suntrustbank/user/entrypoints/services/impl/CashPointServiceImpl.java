package com.suntrustbank.user.entrypoints.services.impl;


import com.suntrustbank.user.core.configs.webclient.WebClientService;
import com.suntrustbank.user.core.enums.ErrorCode;
import com.suntrustbank.user.core.errorhandling.exceptions.GenericErrorCodeException;
import com.suntrustbank.user.core.utils.UUIDGenerator;
import com.suntrustbank.user.entrypoints.repository.models.Organization;
import com.suntrustbank.user.services.dtos.*;
import com.suntrustbank.user.entrypoints.repository.CashPointRepository;
import com.suntrustbank.user.entrypoints.repository.enums.Status;
import com.suntrustbank.user.entrypoints.repository.models.Business;
import com.suntrustbank.user.entrypoints.repository.models.CashPoint;
import com.suntrustbank.user.entrypoints.services.CashPointService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class CashPointServiceImpl implements CashPointService {

    private final WebClientService<WalletCreationRequest, WalletCreationResponse> walletWebClientService;
    private final WebClientService<TransactionUserRequestDto, GenericTransactionResponseDto> transactionWebClientService;
    private final WebClientService<CashPointUpdateRequest, GenericTransactionResponseDto> cashPointUpdateWebClientService;

    private final CashPointRepository cashPointRepository;

    @Override
    @Transactional
    public CashPoint createCashPoint(String authorizationHeader, Organization organization, Business business) throws GenericErrorCodeException {
        CashPoint cashPoint = new CashPoint();
        cashPoint.setReference(UUIDGenerator.generate());
        cashPoint.setBusiness(business);
        cashPoint.setMain(true);
        cashPoint.setStatus(Status.INACTIVE);

        GenericTransactionResponseDto genericTransactionResponseDto = transactionWebClientService.request(TransactionUserRequestDto.builder()
                .authorization(authorizationHeader)
                .creatorReference(organization.getCreator().getReference())
                .userReference(organization.getCreator().getReference()).userFullName(organization.getCreator().getLastName() +" "+organization.getCreator().getFirstName())
                .businessReference(business.getReference()).businessName(business.getName())
                .cashPointReference(cashPoint.getReference())
                .build());

        if (!genericTransactionResponseDto.getStatus().equals("SUCCESS")) {
            throw new GenericErrorCodeException(
                    genericTransactionResponseDto.getMessage(),
                    ErrorCode.BAD_REQUEST,
                    HttpStatus.BAD_REQUEST
            );
        }

        return cashPointRepository.save(cashPoint);
    }

    public CashPoint createNGNCashPointWallet(CashPoint cashPoint) throws GenericErrorCodeException {
        WalletCreationResponse walletCreationResponse = walletWebClientService.request(WalletCreationRequest.builder()
                .currency("NGN")
                .build());
        if (!walletCreationResponse.getStatus().equals("SUCCESS")) {
            throw new GenericErrorCodeException(
                    walletCreationResponse.getMessage(),
                    ErrorCode.BAD_REQUEST,
                    HttpStatus.BAD_REQUEST
            );
        }

        GenericTransactionResponseDto genericTransactionResponseDto = cashPointUpdateWebClientService.request(CashPointUpdateRequest.builder()
                .cashPointReference(cashPoint.getReference())
                .walletReference(walletCreationResponse.getData().getWalletReference())
                .build());

        if (!genericTransactionResponseDto.getStatus().equals("SUCCESS")) {
            throw new GenericErrorCodeException(
                    genericTransactionResponseDto.getMessage(),
                    ErrorCode.BAD_REQUEST,
                    HttpStatus.BAD_REQUEST
            );
        }
        cashPoint.setStatus(Status.ACTIVE);
        cashPoint.setWalletReference(walletCreationResponse.getData().getWalletReference());
        cashPoint.setUpdatedAt(new Date());
        return cashPointRepository.save(cashPoint);
    }
}
