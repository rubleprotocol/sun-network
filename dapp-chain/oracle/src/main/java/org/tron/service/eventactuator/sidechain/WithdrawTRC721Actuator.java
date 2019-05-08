package org.tron.service.eventactuator.sidechain;

import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.tron.client.MainChainGatewayApi;
import org.tron.protos.Protocol.Transaction;
import org.tron.protos.Sidechain.TaskEnum;
import org.tron.service.check.TransactionExtensionCapsule;
import org.tron.service.eventactuator.Actuator;

@Slf4j(topic = "sideChainTask")
public class WithdrawTRC721Actuator extends Actuator {

  // "event WithdrawTRC721(address from, uint256 tokenId, address mainChainAddress, bytes memory txData);"

  private String from;
  private String tokenId;
  private String mainChainAddress;
  private String txData;

  public WithdrawTRC721Actuator(String from, String tokenId, String mainChainAddress,
      String txData) {
    this.from = from;
    this.tokenId = tokenId;
    this.mainChainAddress = mainChainAddress;
    this.txData = txData;
  }

  @Override
  public TransactionExtensionCapsule getTransactionExtensionCapsule() {
    if (Objects.nonNull(transactionExtensionCapsule)) {
      return this.transactionExtensionCapsule;
    }
    try {
      Transaction tx = MainChainGatewayApi
          .withdrawTRC721Transaction(this.from, this.mainChainAddress, this.tokenId, this.txData);
      this.transactionExtensionCapsule = new TransactionExtensionCapsule(TaskEnum.MAIN_CHAIN, tx);
    } catch (Exception e) {
      logger
          .error(
              "WithdrawTRC721Actuator fail, from: {}, tokenId: {}, mainChainAddress: {}, txData: {}",
              this.from, this.tokenId, this.mainChainAddress, this.txData);
    }
    return this.transactionExtensionCapsule;
  }

}
