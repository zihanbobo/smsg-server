package com.lodogame.ldsg.partner.service.impl;

import java.math.BigDecimal;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.easou.sso.sdk.AuthAPI;
import com.easou.sso.sdk.service.AuthBean;
import com.easou.sso.sdk.service.EucAPIException;
import com.easou.sso.sdk.service.EucApiResult;
import com.easou.sso.sdk.service.JUser;
import com.easou.sso.sdk.util.TradeInfo;
import com.lodogame.game.utils.json.Json;
import com.lodogame.ldsg.bo.UserToken;
import com.lodogame.ldsg.constants.OrderStatus;
import com.lodogame.ldsg.exception.ServiceException;
import com.lodogame.ldsg.partner.model.PaymentObj;
import com.lodogame.ldsg.partner.model.apple.ApplePaymentObj;
import com.lodogame.ldsg.partner.sdk.AppleSdk;
import com.lodogame.ldsg.partner.sdk.GooGlePlaySdk;
import com.lodogame.ldsg.partner.service.BasePartnerService;
import com.lodogame.ldsg.partner.service.PartnerService;
import com.lodogame.ldsg.sdk.GameApiSdk;
import com.lodogame.ldsg.service.WebApiService;
import com.lodogame.model.PaymentOrder;

public class AppleServiceImpl extends BasePartnerService {
	private static final Logger logger = Logger.getLogger(AppleServiceImpl.class);

	@Override
	public UserToken login(String token, String partnerId, String serverId, long timestamp, String sign, Map<String, String> params) {
		if (StringUtils.isBlank(token) || StringUtils.isBlank(partnerId) || StringUtils.isBlank(serverId) || timestamp == 0 || StringUtils.isBlank(sign)) {
			throw new ServiceException(PartnerService.PARAM_ERROR, "参数不正确");
		}

		checkSign(token, partnerId, serverId, timestamp, sign);

		try {
			if (GooGlePlaySdk.instance().verifySession(token)) {
				UserToken userToken = GameApiSdk.getInstance().loadUserToken(token, partnerId, serverId, "0", params);
				return userToken;
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(PartnerService.LOGIN_VALID_FAILD, "登录验证失败");
		}

		throw new ServiceException(PartnerService.LOGIN_VALID_FAILD, "登录验证失败");
	}

	@Override
	public boolean doPayment(String orderId, String partnerUserId, boolean success, String partnerOrderId, BigDecimal finishAmount, Map<String, String> reqInfo) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean doPayment(PaymentObj paymentObj) {
		if (paymentObj == null) {
			logger.error("paymentObj为空");
			return false;
		}
		ApplePaymentObj cb = (ApplePaymentObj) paymentObj;
		logger.info("game id:" + cb.getGameOrderId());
		PaymentOrder order = paymentOrderDao.get(cb.getGameOrderId());
		if (order == null) {
			logger.error("订单为空：" + Json.toJson(cb));
			return false;
		}
		BigDecimal finishAmount = order.getAmount();
		int n = 0;
		while (!AppleSdk.instance().checkPayCallbackSign(cb)) {
			if (n >= 5) {
				logger.error("apple验证失败" + Json.toJson(paymentObj));
				this.paymentOrderDao.updateStatus(cb.getGameOrderId(), OrderStatus.STATUS_ERROR, cb.getAppleOrderId(), finishAmount, cb.getReceipt());
				return false;
			}
			n++;
		}
		if (!this.paymentOrderDao.countOrderByExtInfo(cb.getAppleOrderId())) {
			logger.error("订单已经完成" + Json.toJson(cb));
			return true;
		}
		logger.info("应用订单：" + Json.toJson(order));
		if (order.getStatus() == OrderStatus.STATUS_FINISH) {
			logger.error("订单已经完成" + Json.toJson(cb));
			return true;
		}
		int gold = GameApiSdk.getInstance().getSystemGold(order.getPartnerId(),order.getServerId(),finishAmount.toString());
		// 更新订单状态
		if (this.paymentOrderDao.updateStatus(order.getOrderId(), OrderStatus.STATUS_FINISH, cb.getAppleOrderId(), finishAmount, cb.getReceipt())) {

			// 请求游戏服，发放游戏货币
			if (!GameApiSdk.getInstance().doPayment(order.getPartnerId(), order.getServerId(), order.getPartnerUserId(), "", order.getOrderId(), finishAmount, gold, "", "")) {
				// 如果失败，要把订单置为未支付
				this.paymentOrderDao.updateStatus(order.getOrderId(), OrderStatus.STATUS_NOT_PAY, cb.getAppleOrderId(), finishAmount, cb.getReceipt());
				logger.error("发货失败：" + Json.toJson(cb));
				return false;
			} else {
				logger.info("支付成功：" + Json.toJson(cb));
				return true;
			}
		}
		this.paymentOrderDao.updateStatus(order.getOrderId(), OrderStatus.STATUS_ERROR, cb.getAppleOrderId(), finishAmount, cb.getReceipt());
		logger.error("充值失败：" + Json.toJson(cb));
		return false;
	}

	@Override
	public String createOrder(String partnerId, String serverId, String partnerUserId, BigDecimal amount, String tradeName, String pkgId, String qn) {
		pkgId = "sgsg_"+pkgId;
		TradeInfo info = createOrderInfo(partnerId, serverId, partnerUserId, amount, tradeName,pkgId,qn);
		// 以分为单位
		// info.setReqFee(Integer.toString(new BigDecimal(100).multiply(new
		// BigDecimal(info.getReqFee())).intValue()));
		return Json.toJson(info);
	}

}