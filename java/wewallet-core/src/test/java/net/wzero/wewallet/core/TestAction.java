package net.wzero.wewallet.core;

import org.junit.Test;

import net.wzero.wewallet.core.domain.CardType;

public class TestAction {

	@Test
	public void doAction1() {
		CardType ct = CardType.builder().name("一个名字").rootPath("m/44'/60'/0'/0/0").build();
		//Card c = Card
		CardType ct2 = CardType.builder().name("一个名字").rootPath("m/44'/60'/0'/0/0").build();
		System.out.println(ct2.equals(ct));
		System.out.println("id->"+ct.getId()+"\t"+ct.getName()+"\t"+ct.getRootPath());
	}
}
