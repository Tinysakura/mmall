package com.cfh.mmall.test;

import org.junit.Test;

public class TestEnum {
	public enum emoji{
		happy(1,"happy"),
		sad(2,"sad"),
		suprise(3,"suprise");
		
		emoji(int code,String emotion){
			this.code = code;
			this.emotion = emotion;
		}
		
		private int code;
		private String emotion;
		
		public int getCode() {
			return code;
		}
		public void setCode(int code) {
			this.code = code;
		}
		public String getEmotion() {
			return emotion;
		}
		public void setEmotion(String emotion) {
			this.emotion = emotion;
		}
		
		public static emoji codeOf(int code){
			for(emoji emoji:values()){
				if(emoji.getCode() == code){
					return emoji;
				}
			}
			
			throw new RuntimeException("找不到对应的枚举");
		}
	}
	
	@Test
	public void test(){
		//System.out.println(TestEnum.emoji.happy.getCode());
		//System.out.println(TestEnum.emoji.happy.getEmotion());
		
		emoji temp = TestEnum.emoji.codeOf(3);
		
		switch (temp) {
		case sad:
			System.out.println(temp.getEmotion());
			break;
		case happy:
			System.out.println(temp.getEmotion());
			break;
		case suprise:
			System.out.println(temp.getEmotion());
			break;
		}
	}
}
