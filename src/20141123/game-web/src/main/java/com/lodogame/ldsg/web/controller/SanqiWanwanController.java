package com.lodogame.ldsg.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.lodogame.game.utils.json.Json;
import com.lodogame.ldsg.partner.model.sanqiwanwan.SanqiWanwanPaymentObj;
import com.lodogame.ldsg.partner.sdk.SanqiWanwanSdk;
import com.lodogame.ldsg.partner.service.PartnerService;
import com.lodogame.ldsg.web.factory.PartnerServiceFactory;

@Controller
public class SanqiWanwanController {
	private static Logger LOG = Logger.getLogger(SanqiWanwanController.class);
	
	@Autowired
	private PartnerServiceFactory serviceFactory;

	@RequestMapping(value = "/webApi/37wanwanPayment.do")
	public ModelAndView payCallback(HttpServletRequest req, HttpServletResponse res) {
		PartnerService ps = serviceFactory.getBean(SanqiWanwanSdk.instance().getPartnerId());
		SanqiWanwanPaymentObj data = new SanqiWanwanPaymentObj();
		data.setOrderid(req.getParameter("order_id"));
		data.setPayTime(req.getParameter("pay_time"));
		data.setPayType(req.getParameter("pay_type"));
		data.setStatus(req.getParameter("status"));
		data.setTotalPrice(req.getParameter("total_price"));
		data.setTxNo(req.getParameter("tx_no"));
		data.setUsergameid(req.getParameter("usergameid"));
		data.setSign(req.getHeader("Authentication"));
		data.setDate(req.getHeader("Date"));
		LOG.info("37wanwan:"+Json.toJson(data));
		try {
			if(ps.doPayment(data)){
				res.getWriter().print("{\"status\":\"success\"}");
			}else{
				res.getWriter().print("{\"status\":\"failed\", \"message\":\"this is error message\"}");
			}
		} catch (Exception e) {
			LOG.error("anzhi payment error!",e);
		}
		return null;
	}
}
