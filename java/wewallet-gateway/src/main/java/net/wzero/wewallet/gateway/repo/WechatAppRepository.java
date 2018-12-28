package net.wzero.wewallet.gateway.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import net.wzero.wewallet.gateway.domain.WechatApp;

public interface WechatAppRepository extends JpaRepository<WechatApp, Integer> {

}
