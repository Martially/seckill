package org.seckill.dao;

import static org.junit.Assert.*;
import javax.annotation.Resource;
import org.apache.ibatis.annotations.Param;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.SuccessKilled;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit spring配置文件
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SuccessKilledDaoTest {

	@Resource
	private SuccessKilledDao successKilledDao;
	
	@Test
	public void testInsertSuccessKilled() {
		/*
		 * 第一次：insert=1
		 * 第二次：insert=0
		 * 因为联合主键和ignore
		 */
		long id =1001L;
		long phone = 1575719131L;
		int insert = successKilledDao.insertSuccessKilled(id, phone);
		System.out.println("insert="+insert);
	}

	@Test
	public void testQueryByIdWithSeckill() {
		long id =1001L;
		long phone = 1575719131L;
		 SuccessKilled successKilled=successKilledDao.queryByIdWithSeckill(id, phone);
		 System.out.println(successKilled);
		 System.out.println(successKilled.getSeckill());
	}

}
