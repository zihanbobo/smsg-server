/**
 * This class has been generated by Fast Code Eclipse Plugin 
 * For more information please go to http://fast-code.sourceforge.net/
 * @author : chenjian
 * Created : 10/04/2013
 */

package com.lodogame.game.dao.impl.cache;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.lodogame.game.dao.UserPayRewardDao;
import com.lodogame.model.UserPayReward;
import org.testng.annotations.*;
import static org.testng.Assert.*;


@ContextConfiguration(locations = { "classpath:applicationContext-test.xml" })
public class UserPayRewardDaoCacheImplTest extends AbstractTestNGSpringContextTests {

	@Autowired
	private UserPayRewardDao userPayRewardDaoMysqlImpl;
	
	private String userId;
	private int activityId;
	private int rewardId;
	private String orderId;
	
	@BeforeTest
	public void init(){
		userId = "TESTUSERID";
		orderId = "TESTORDERNO";
		activityId = 11;
		rewardId = 1;
	}
	/**
	 *
	 * @see com.lodogame.game.dao.impl.cache.UserPayRewardDaoCacheImpl#add(UserPayReward)
	 */
	@Test
	public void add() {
		UserPayReward userPayReward = new UserPayReward();
		userPayReward.setActivityId(activityId);
		userPayReward.setRewardId(rewardId);
		userPayReward.setOrderId(orderId);
		userPayReward.setUserId(userId);
		userPayReward.setCreatedTime(new Date());
		userPayReward.setUpdatedTime(new Date());
		boolean result = userPayRewardDaoMysqlImpl.add(userPayReward);
		Assert.assertTrue(result);
	}
	/**
	 *
	 * @see com.lodogame.game.dao.impl.cache.UserPayRewardDaoCacheImpl#getReceivedOrderIdList(String,int,int)
	 */
	@Test
	public void getReceivedOrderIdList() {
		List<UserPayReward> receivedorderidlist = userPayRewardDaoMysqlImpl.getReceivedOrderIdList(userId,activityId,rewardId, new Date(), new Date());
		assertNotNull(receivedorderidlist, "receivedorderidlist cannot be null");
		Assert.assertEquals(receivedorderidlist.size(), 1);
		Assert.assertEquals(receivedorderidlist.get(0).getOrderId(), orderId);
	}
	/**
	 *
	 * @see com.lodogame.game.dao.impl.cache.UserPayRewardDaoCacheImpl#getList(String)
	 */
	@Test
	public void getList() {
		List<UserPayReward> list = userPayRewardDaoMysqlImpl.getList(userId);
		assertNotNull(list, "list cannot be null");
		Assert.assertEquals(list.size(), 1);
		Assert.assertEquals(list.get(0).getOrderId(), orderId);
	}

	
}
