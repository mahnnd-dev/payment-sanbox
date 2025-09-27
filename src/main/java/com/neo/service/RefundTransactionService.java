package com.neo.service;

import com.neo.dto.RefundRequest;
import com.neo.dto.RefundResponse;
import com.neo.modal.RefundTransaction;
import com.neo.modal.TransactionLog;
import com.neo.repository.RefundTransactionRepository;
import com.neo.repository.TransactionLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefundTransactionService {
    private final TransactionLogRepository logRepository;
    private final RefundTransactionRepository refundTransactionRepository;
    private final ValidateService validateService;

    public RefundResponse refundTransaction(RefundRequest request) {
        if (!validateService.validateRequestRefundSecureHash(request)) {
            return null;
        }
        RefundResponse refundResponse = new RefundResponse();
        TransactionLog transactionLog = logRepository.findAllByTxnRefAndTransactionStatus(request.getNeoTxnRef(), "00");
        transactionLog.setCommand(request.getNeoCommand());
        transactionLog.setTransactionStatus("01");
        transactionLog.setResponseMessage("Hoàn giao dịch thành công");
        logRepository.save(transactionLog);
        RefundTransaction refundTransaction = new RefundTransaction();
        refundTransaction.setRequestId(request.getNeoRequestId());
        refundTransaction.setVersion(request.getNeoVersion());
        refundTransaction.setCommand(request.getNeoCommand());
        refundTransaction.setTmnCode(request.getNeoTmnCode());
        refundTransaction.setTxnRef(request.getNeoTxnRef());
        refundTransaction.setOrderInfo(request.getNeoOrderInfo());
        refundTransaction.setTransactionNo(String.valueOf(request.getNeoTransactionNo()));
        refundTransaction.setTransactionDate(request.getNeoTransactionDate());
        refundTransaction.setCreateDate(request.getNeoCreateDate());
        refundTransaction.setIpAddr(request.getNeoIpAddr());
        refundTransaction.setAmount(request.getNeoAmount());
        refundTransaction.setRefundAmount(request.getNeoAmount());
        refundTransaction.setRefundReason(null);
        refundTransaction.setResponseCode("01");
        refundTransaction.setResponseMessage("Hoàn giao dịch thành công");
        refundTransaction.setStatus("01");
        refundTransactionRepository.save(refundTransaction);
        refundResponse.setNeo_ResponseId(request.getNeoRequestId());
        refundResponse.setNeo_Command(request.getNeoCommand());
        refundResponse.setNeo_TmnCode(request.getNeoTmnCode());
        refundResponse.setNeo_TxnRef(request.getNeoTxnRef());
        refundResponse.setNeo_Amount(request.getNeoAmount());
        refundResponse.setNeo_OrderInfo(request.getNeoOrderInfo());
        refundResponse.setNeo_ResponseCode("01");
        refundResponse.setNeo_Message("Hoàn giao dịch thành công");
        refundResponse.setNeo_BankCode(transactionLog.getBankCode());
        refundResponse.setNeo_PayDate(transactionLog.getPayDate());
        refundResponse.setNeo_TransactionNo(request.getNeoTransactionNo());
        refundResponse.setNeo_TransactionType(request.getNeoTransactionType());
        refundResponse.setNeo_TransactionStatus(request.getNeoRequestId());
        return refundResponse;
    }
}
