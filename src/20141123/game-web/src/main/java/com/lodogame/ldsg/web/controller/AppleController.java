package com.lodogame.ldsg.web.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;

import com.lodogame.ldsg.partner.model.apple.ApplePaymentObj;
import com.lodogame.ldsg.partner.sdk.AppleSdk;
import com.lodogame.ldsg.partner.service.PartnerService;
import com.lodogame.ldsg.web.factory.PartnerServiceFactory;

@Controller
public class AppleController {
	private static Logger LOG = Logger.getLogger(AppleController.class);

	@Autowired
	private PartnerServiceFactory serviceFactory;

	@RequestMapping(value = "/webApi/applePayment.do", method = RequestMethod.POST)
	public ModelAndView payCallback(HttpServletRequest req, HttpServletResponse res) {
		PartnerService ps = serviceFactory.getBean(AppleSdk.instance().getPartnerId());
		Map<String, Object> map = new HashMap<String, Object>();
		String receiptData = req.getParameter("receiptData");
		String gameOrderId = req.getParameter("gameOrderId");
		String appOrderId = req.getParameter("appOrderId");
		String version = req.getParameter("version");

		if (StringUtils.isNotBlank(receiptData) && StringUtils.isNotBlank(gameOrderId) && StringUtils.isNotBlank(appOrderId)) {
			try {
				ApplePaymentObj data = new ApplePaymentObj();
				data.setReceipt(receiptData);
				data.setGameOrderId(gameOrderId);
				data.setAppleOrderId(appOrderId);
				data.setVersion(version);
				if (ps.doPayment(data)) {
					map.put("errcode", "success");
				} else {
					map.put("errcode", "fail");
				}
				// AppleSdk.instance().getExecutorService().execute(new
				// AppleVerfiyRunnable(ps,data));
			} catch (Exception e) {
				map.put("errcode", "fail");
				LOG.error("decode data error!data:" + receiptData, e);
			}
		} else {
			map.put("errcode", "fail");
		}
		MappingJacksonJsonView view = new MappingJacksonJsonView();
		view.setAttributesMap(map);
		ModelAndView modelView = new ModelAndView();
		modelView.setView(view);
		return modelView;
	}
}
