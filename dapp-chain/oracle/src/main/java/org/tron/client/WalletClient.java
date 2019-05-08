package org.tron.client;

import com.google.protobuf.ByteString;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.tron.api.GrpcAPI;
import org.tron.api.GrpcAPI.AddressPrKeyPairMessage;
import org.tron.api.GrpcAPI.EmptyMessage;
import org.tron.api.GrpcAPI.Return;
import org.tron.common.config.SystemSetting;
import org.tron.common.crypto.ECKey;
import org.tron.common.exception.RpcConnectException;
import org.tron.common.exception.TxValidateException;
import org.tron.common.exception.TxRollbackException;
import org.tron.common.exception.TxFailException;
import org.tron.common.utils.AbiUtil;
import org.tron.common.utils.Base58;
import org.tron.common.utils.TransactionUtils;
import org.tron.common.utils.WalletUtil;
import org.tron.protos.Contract;
import org.tron.protos.Contract.AssetIssueContract;
import org.tron.protos.Protocol.Account;
import org.tron.protos.Protocol.Transaction;
import org.tron.protos.Protocol.Transaction.Result;
import org.tron.protos.Protocol.TransactionInfo;
import org.tron.protos.Protocol.TransactionInfo.code;
import org.tron.service.check.TransactionExtensionCapsule;

@Slf4j
public class WalletClient {

  private RpcClient rpcCli;
  private ECKey ecKey;
  private byte[] address;

  public WalletClient(String target, byte[] priateKey) {
    rpcCli = new RpcClient(target);
    ecKey = ECKey.fromPrivate(priateKey);
    address = ecKey.getAddress();
  }

  public Optional<TransactionInfo> getTransactionInfoById(String txID) {
    return rpcCli.getTransactionInfoById(txID);
  }

  public Account queryAccount() {
    return rpcCli.queryAccount(this.address);
  }

  public Account queryAccount(String address) {
    return rpcCli.queryAccount(WalletUtil.decodeFromBase58Check(address));
  }

  public Account queryAccount(byte[] address) {
    return rpcCli.queryAccount(address);
  }

  public ECKey generateAddress() {
    EmptyMessage.Builder builder = EmptyMessage.newBuilder();
    AddressPrKeyPairMessage result = rpcCli.generateAddress(builder.build());

    byte[] priKey = Base58.hexs2Bytes(result.getPrivateKey().getBytes());
    if (!Base58.priKeyValid(priKey)) {
      return null;
    }
    return ECKey.fromPrivate(priKey);
  }

  public byte[] triggerConstantContractAndReturn(byte[] contractAddress, String method,
      List<Object> params, long callValue, long tokenId, long tokenValue)
      throws RpcConnectException {

    logger.info(
        "trigger constant, contract address: {}, method: {}, params: {}, call value: {}, token id: {}, token value: {}",
        WalletUtil.encode58Check(contractAddress), method, params.toString(), callValue, tokenId,
        tokenValue);

    byte[] data = AbiUtil.parseMethod(method, params);

    org.tron.api.GrpcAPI.TransactionExtention transactionExtention = this
        .triggerConstantContract(contractAddress, data, callValue, tokenValue, tokenId);

    Transaction transaction = transactionExtention.getTransaction();
    if (transaction.getRetCount() != 0 && transactionExtention.getConstantResult(0) != null
        && transactionExtention.getResult() != null) {
      return transactionExtention.getConstantResult(0).toByteArray();
    }
    throw new RpcConnectException("no result");
  }

  public TransactionExtensionCapsule triggerContract(byte[] contractAddress, String method,
      List<Object> params,
      long callValue, long tokenId, long tokenValue) throws RpcConnectException, TxValidateException {

    logger.info(
        "trigger not constant, contract address: {}, method: {}, params: {}, call value: {}, token id: {}, token value: {}",
        WalletUtil.encode58Check(contractAddress), method, params.toString(), callValue, tokenId,
        tokenValue);

    byte[] data = AbiUtil.parseMethod(method, params);
    TransactionExtensionCapsule txId = triggerContract(contractAddress, data,
        SystemSetting.FEE_LIMIT,
        callValue, tokenValue, tokenId);
    logger.info("txId: {}", txId);
    return txId;
  }

  public Transaction triggerContractTransaction(byte[] contractAddress, String method,
      List<Object> params,
      long callValue, long tokenId, long tokenValue) throws RpcConnectException {

    logger.info(
        "trigger not constant, contract address: {}, method: {}, params: {}, call value: {}, token id: {}, token value: {}",
        WalletUtil.encode58Check(contractAddress), method, params.toString(), callValue, tokenId,
        tokenValue);

    byte[] data = AbiUtil.parseMethod(method, params);
    return triggerContractTransaction(contractAddress, data, SystemSetting.FEE_LIMIT,
        callValue, tokenValue, tokenId);

  }

  private org.tron.api.GrpcAPI.TransactionExtention triggerConstantContract(byte[] contractAddress,
      byte[] data,
      long callValue, long tokenValue, Long tokenId) throws RpcConnectException {
    byte[] owner = address;
    Contract.TriggerSmartContract triggerContract = buildTriggerContract(owner, contractAddress,
        callValue, data, tokenValue, tokenId);
    org.tron.api.GrpcAPI.TransactionExtention transactionExtention = rpcCli
        .triggerContract(triggerContract);
    if (!transactionExtention.getResult().getResult()) {
      logger.error("rpc fail, code: {}, message: {}", transactionExtention.getResult().getCode(),
          transactionExtention.getResult().getMessage().toStringUtf8());
      throw new RpcConnectException(
          "rpc fail, code: " + transactionExtention.getResult().getCode());
    }
    return transactionExtention;
  }

  private TransactionExtensionCapsule triggerContract(byte[] contractAddress, byte[] data,
      long feeLimit,
      long callValue,
      long tokenValue, Long tokenId) throws RpcConnectException, TxValidateException {
    GrpcAPI.TransactionExtention transactionExtention = getTransactionExtention(contractAddress,
        data, feeLimit,
        callValue, tokenValue, tokenId);

    return processTransactionExtention(transactionExtention);
  }

  private Transaction triggerContractTransaction(byte[] contractAddress, byte[] data, long feeLimit,
      long callValue,
      long tokenValue, Long tokenId) throws RpcConnectException {
    GrpcAPI.TransactionExtention transactionExtention = getTransactionExtention(contractAddress,
        data, feeLimit, callValue, tokenValue, tokenId);

    return getTransaction(transactionExtention);
  }

  private GrpcAPI.TransactionExtention getTransactionExtention(byte[] contractAddress, byte[] data,
      long feeLimit, long callValue, long tokenValue, Long tokenId) throws RpcConnectException {
    byte[] owner = address;
    Contract.TriggerSmartContract triggerContract = buildTriggerContract(owner, contractAddress,
        callValue, data, tokenValue, tokenId);
    GrpcAPI.TransactionExtention transactionExtention = rpcCli
        .triggerContract(triggerContract);
    if (transactionExtention == null || !transactionExtention.getResult().getResult()) {
      logger.error("rpc fail, code: {}, message: {}", transactionExtention.getResult().getCode(),
          transactionExtention.getResult().getMessage().toStringUtf8());
      throw new RpcConnectException(
          "rpc fail, code: " + transactionExtention.getResult().getCode());
    }

    GrpcAPI.TransactionExtention.Builder txBuilder = GrpcAPI.TransactionExtention
        .newBuilder();
    Transaction.Builder transBuilder = Transaction.newBuilder();
    Transaction.raw.Builder rawBuilder = transactionExtention.getTransaction().getRawData()
        .toBuilder();
    rawBuilder.setFeeLimit(feeLimit);
    transBuilder.setRawData(rawBuilder);
    for (int i = 0; i < transactionExtention.getTransaction().getSignatureCount(); i++) {
      ByteString s = transactionExtention.getTransaction().getSignature(i);
      transBuilder.setSignature(i, s);
    }
    for (int i = 0; i < transactionExtention.getTransaction().getRetCount(); i++) {
      Result r = transactionExtention.getTransaction().getRet(i);
      transBuilder.setRet(i, r);
    }
    txBuilder.setTransaction(transBuilder);
    txBuilder.setResult(transactionExtention.getResult());
    txBuilder.setTxid(transactionExtention.getTxid());
    transactionExtention = txBuilder.build();
    return transactionExtention;
  }

  public static Contract.TriggerSmartContract buildTriggerContract(byte[] address,
      byte[] contractAddress,
      long callValue, byte[] data, long tokenValue, Long tokenId) {
    Contract.TriggerSmartContract.Builder builder = Contract.TriggerSmartContract.newBuilder();
    builder.setOwnerAddress(ByteString.copyFrom(address));
    builder.setContractAddress(ByteString.copyFrom(contractAddress));
    builder.setData(ByteString.copyFrom(data));
    builder.setCallValue(callValue);
    builder.setTokenId(tokenId);
    builder.setCallTokenValue(tokenValue);
    return builder.build();
  }

  public AssetIssueContract getAssetIssueById(String assetId) {
    return rpcCli.getAssetIssueById(assetId);
  }

  private TransactionExtensionCapsule processTransactionExtention(
      org.tron.api.GrpcAPI.TransactionExtention transactionExtention)
    throws RpcConnectException, TxValidateException {
    Transaction transaction = getTransaction(transactionExtention);
    rpcCli.broadcastTransaction(transaction);
    return new TransactionExtensionCapsule(transaction);
  }

  private Transaction getTransaction(
      org.tron.api.GrpcAPI.TransactionExtention transactionExtention)
      throws RpcConnectException {
    if (transactionExtention == null) {
      throw new RpcConnectException("transactionExtensionCapsule is null");
    }
    Return ret = transactionExtention.getResult();
    if (!ret.getResult()) {
      logger
          .error("rpc fail, code: {}, message: {}", ret.getCode(), ret.getMessage().toStringUtf8());
      throw new RpcConnectException("rpc fail, code: " + ret.getCode());
    }
    Transaction transaction = transactionExtention.getTransaction();

    if (transaction.getRawData().getTimestamp() == 0) {
      transaction = TransactionUtils.setTimestamp(transaction);
    }
    return TransactionUtils.sign(transaction, this.ecKey);
  }

  public boolean broadcast(Transaction transaction) throws RpcConnectException, TxValidateException {
      return rpcCli.broadcastTransaction(transaction);
  }

  public byte[] checkTxInfo(String txId) throws TxRollbackException, TxFailException {
    int maxRetry = 3;
    for (int i = 0; i < maxRetry; i++) {
      Optional<TransactionInfo> transactionInfo = rpcCli.getTransactionInfoById(txId);
      TransactionInfo info = transactionInfo.get();
      if (info.getBlockTimeStamp() == 0L) {
        logger.info("will retry {} time(s)", i + 1);
        try {
          Thread.sleep(3_000);
        } catch (InterruptedException e) {
          logger.error(e.getMessage(), e);
        }
      } else {
        if (info.getResult().equals(code.SUCESS)) {
          return info.getContractResult(0).toByteArray();
        } else {
          throw new TxFailException(info.getResMessage().toStringUtf8());
        }
      }
    }
    throw new TxRollbackException(txId);
  }
}