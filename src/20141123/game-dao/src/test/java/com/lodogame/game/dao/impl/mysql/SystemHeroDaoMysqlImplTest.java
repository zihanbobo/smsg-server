/**
 * This class has been generated by Fast Code Eclipse Plugin 
 * For more information please go to http://fast-code.sourceforge.net/
 * @author : jacky
 * Created : 05/23/2013
 */

package com.lodogame.game.dao.impl.mysql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

@ContextConfiguration(locations = { "classpath:applicationContext-test.xml" })
public class SystemHeroDaoMysqlImplTest extends AbstractTestNGSpringContextTests {

	@Autowired
	private SystemHeroDaoMysqlImpl systemHeroDaoMysqlImpl;

	/**
	 * 
	 * @see com.lodogame.game.dao.impl.mysql.SystemHeroDaoMysqlImpl#get(Integer)
	 */
	@Test
	public void get() {
		systemHeroDaoMysqlImpl.get(1);
	}

}
