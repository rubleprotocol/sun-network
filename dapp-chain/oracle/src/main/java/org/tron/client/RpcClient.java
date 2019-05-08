package org.tron.client;

import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.tron.api.GrpcAPI.AddressPrKeyPairMessage;
import org.tron.api.GrpcAPI.BytesMessage;
import org.tron.api.GrpcAPI.EmptyMessage;
import org.tron.api.GrpcAPI.Return;
import org.tron.api.GrpcAPI.Return.response_code;
import org.tron.api.GrpcAPI.TransactionExtention;
import org.tron.api.WalletGrpc;
import org.tron.common.exception.RpcConnectException;
import org.tron.common.exception.TxValidateException;
import org.tron.common.utils.ByteArray;
import org.tron.protos.Contract;
import org.tron.protos.Contract.AssetIssueContract;
import org.tron.protos.Protocol.Account;
import org.tron.protos.Protocol.Transaction;
import org.tron.protos.Protocol.TransactionInfo;

@Slf4j
class RpcClient {

  private WalletGrpc.WalletBlockingStub blockingStub;

  RpcClient(String target) {
    ManagedChannel channel = ManagedChannelBuilder.forTarget(target).usePlaintext(true).build();
    blockingStub = WalletGrpc.newBlockingStub(channel);
  }

  TransactionExtention triggerContract(Contract.TriggerSmartContract request) {
    return blockingStub.triggerContract(request);
  }

  Optional<TransactionInfo> getTransactionInfoById(String txID) {
    BytesMessage request = BytesMessage.newBuilder()
        .setValue(ByteString.copyFrom(ByteArray.fromHexString(txID))).build();
    TransactionInfo transactionInfo = blockingStub.getTransactionInfoById(request);
    return Optional.ofNullable(transactionInfo);
  }

  boolean broadcastTransaction(Transaction signaturedTransaction)
      throws RpcConnectException, TxValidateException {

    int maxRetry = 5;
    for (int i = 0; i < maxRetry; i++) {

      Return response = blockingStub.broadcastTransaction(signaturedTransaction);
      if (response.getResult()) {
        // true is success
        return true;
      } else {
        // false is fail
        if (response.getCode() == response_code.SERVER_BUSY) {
          // when SERVER_BUSY, retry
          logger.info("will retry {} time(s)", i + 1);
          try {
            Thread.sleep(300);
          } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
          }
        } else {
          logger.info("server error, fail, code: {}, message {}", response.getCode(),
              response.getMessage().toStringUtf8());
          // fail, not retry
          throw new TxValidateException("tx error, fail");
        }
      }
    }
    logger.error("broadcast transaction, exceed max retry, fail");
    throw new RpcConnectException("broadcast transaction, exceed max retry, fail");
  }

  TransactionExtention createTransaction2(Contract.TransferContract contract) {
    return blockingStub.createTransaction2(contract);
  }

  Account queryAccount(byte[] address) {
    ByteString addressBS = ByteString.copyFrom(address);
    Account request = Account.newBuilder().setAddress(addressBS).build();
    return blockingStub.getAccount(request);
  }

  AddressPrKeyPairMessage generateAddress(EmptyMessage emptyMessage) {
    return blockingStub.generateAddress(emptyMessage);
  }

  AssetIssueContract getAssetIssueById(String assetId) {
    ByteString assetIdBs = ByteString.copyFrom(assetId.getBytes());
    BytesMessage request = BytesMessage.newBuilder().setValue(assetIdBs).build();
    return blockingStub.getAssetIssueById(request);
  }
}
