package com.lodogame.game.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;

import flex.messaging.io.ArrayList;

/**
 * 非法字符工具类
 * @author CJ
 *
 */
public class IllegalWordUtills {
	private static Logger LOG = Logger.getLogger(IllegalWordUtills.class);
	private static List<String> illegalWords;
	
	static{
		LOG.info("初始化非法字符");
		illegalWords = new ArrayList();
		StringBuilder sb = new StringBuilder();
		BufferedReader reader = null;
		String line = "";
		try {
			reader = new BufferedReader(new InputStreamReader((new ClassPathResource("illegalWords.cfg")).getInputStream()));
			while((line = reader.readLine()) != null){
				sb.append(line);
			}
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}finally{
			IOUtils.closeQuietly(reader);
		}
		String[] words = sb.toString().split(",");
		for(int i = 0; i < words.length; i++){
			illegalWords.add(words[i]);
		}
	}
	
	/**
	 * 检测非法字符，false为不合法，true为合法
	 * @param str
	 * @return
	 */
	public static boolean checkWords(String str){
		for(int i = 0; i < illegalWords.size(); i++){
			if(str.contains(illegalWords.get(i))){
				return false;
			}
		}
		
		return true;
	}
	
	public static void main(String[] args) {
		System.out.println(IllegalWordUtills.checkWords("刘爷"));
	}
}
