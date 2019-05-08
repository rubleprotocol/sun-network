package org.tron.service.eventactuator;

import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.tron.protos.Sidechain.TaskEnum;
import org.tron.service.eventactuator.mainchain.DepositTRC10Actuator;
import org.tron.service.eventactuator.mainchain.DepositTRC20Actuator;
import org.tron.service.eventactuator.mainchain.DepositTRC721Actuator;
import org.tron.service.eventactuator.mainchain.DepositTRXActuator;
import org.tron.service.eventactuator.mainchain.Token10WithdrawnActuator;
import org.tron.service.eventactuator.mainchain.TokenWithdrawnActuator;
import org.tron.service.eventactuator.sidechain.DeployDAppTRC20AndMappingActuator;
import org.tron.service.eventactuator.sidechain.DeployDAppTRC721AndMappingActuator;
import org.tron.service.eventactuator.sidechain.WithdrawTRC10Actuator;
import org.tron.service.eventactuator.sidechain.WithdrawTRC20Actuator;
import org.tron.service.eventactuator.sidechain.WithdrawTRC721Actuator;
import org.tron.service.eventactuator.sidechain.WithdrawTRXActuator;
import org.tron.service.eventenum.MainEventType;
import org.tron.service.eventenum.SideEventType;

@Slf4j(topic = "task")
public class EventActuatorFactory {

  public static Actuator CreateActuator(TaskEnum taskType,
      JSONObject obj) {
    try {

      switch (taskType) {
        case MAIN_CHAIN:
          return createMainChainActuator(obj);
        case SIDE_CHAIN:
          return createSideChainActuator(obj);
      }
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
    return null;
  }

  private static Actuator createMainChainActuator(JSONObject obj) {
    Actuator task;
    MainEventType eventSignature = MainEventType
        .fromSignature(obj.get("eventSignature").toString());
    JSONObject dataMap = (JSONObject) obj.get("dataMap");
    JSONObject topicMap = (JSONObject) obj.get("topicMap");

    switch (eventSignature) {
      case TOKEN_WITHDRAWN: {
        task = new TokenWithdrawnActuator(topicMap.get("owner").toString(),
            dataMap.get("kind").toString(), dataMap.get("contractAddress").toString(),
            dataMap.get("value").toString());
        return task;
      }
      case TOKEN10_WITHDRAWN: {
        task = new Token10WithdrawnActuator(topicMap.get("owner").toString(),
            dataMap.get("kind").toString(), dataMap.get("tokenId").toString(),
            dataMap.get("value").toString());
        return task;
      }
      case TRX_RECEIVED: {
        task = new DepositTRXActuator(dataMap.get("from").toString(),
            dataMap.get("amount").toString());
        return task;
      }
      case TRC10_RECEIVED: {
        task = new DepositTRC10Actuator(dataMap.get("from").toString(),
            dataMap.get("amount").toString(), dataMap.get("tokenId").toString());
        return task;
      }
      case TRC20_RECEIVED: {
        task = new DepositTRC20Actuator(dataMap.get("from").toString(),
            dataMap.get("amount").toString(), dataMap.get("contractAddress").toString());
        return task;
      }
      case TRC721_RECEIVED: {
        task = new DepositTRC721Actuator(dataMap.get("from").toString(),
            dataMap.get("uid").toString(), dataMap.get("contractAddress").toString());
        return task;
      }
      default: {
        logger.warn("event:{},signature:{}.", obj.get("eventSignature").toString(),
            eventSignature.getSignature());
      }
    }
    return null;
  }

  private static Actuator createSideChainActuator(JSONObject obj) {
    Actuator task;
    SideEventType eventType = SideEventType
        .fromMethod(obj.get("eventSignature").toString());
    switch (eventType) {
      case DEPLOY_DAPPTRC20_AND_MAPPING: {
        JSONObject dataMap = (JSONObject) obj.get("dataMap");
        task = new DeployDAppTRC20AndMappingActuator(dataMap.get("developer").toString(),
            dataMap.get("mainChainAddress").toString(), dataMap.get("sideChainAddress").toString());
        return task;
      }
      case DEPLOY_DAPPTRC721_AND_MAPPING: {
        JSONObject dataMap = (JSONObject) obj.get("dataMap");
        task = new DeployDAppTRC721AndMappingActuator(dataMap.get("developer").toString(),
            dataMap.get("mainChainAddress").toString(), dataMap.get("sideChainAddress").toString());
        return task;
      }
      case WITHDRAW_TRC10: {
        JSONObject dataMap = (JSONObject) obj.get("dataMap");
        task = new WithdrawTRC10Actuator(dataMap.get("from").toString(),
            dataMap.get("value").toString(), dataMap.get("trc10").toString(),
            dataMap.get("txData").toString());
        return task;
      }
      case WITHDRAW_TRC20: {
        JSONObject dataMap = (JSONObject) obj.get("dataMap");
        task = new WithdrawTRC20Actuator(dataMap.get("from").toString(),
            dataMap.get("value").toString(), dataMap.get("mainChainAddress").toString(),
            dataMap.get("txData").toString());
        return task;
      }
      case WITHDRAW_TRC721: {
        JSONObject dataMap = (JSONObject) obj.get("dataMap");
        task = new WithdrawTRC721Actuator(dataMap.get("from").toString(),
            dataMap.get("tokenId").toString(), dataMap.get("mainChainAddress").toString(),
            dataMap.get("txData").toString());
        return task;
      }
      case WITHDRAW_TRX: {
        JSONObject dataMap = (JSONObject) obj.get("dataMap");
        task = new WithdrawTRXActuator(dataMap.get("from").toString(),
            dataMap.get("value").toString(),
            dataMap.get("txData").toString());
        return task;
      }
      default: {
        logger.info("event:{},signature:{}.",
            obj.get("eventSignature").toString(), eventType.getMethod());
      }
    }
    return null;
  }
}
