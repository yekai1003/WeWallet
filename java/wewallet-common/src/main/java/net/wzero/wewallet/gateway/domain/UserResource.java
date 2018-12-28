package net.wzero.wewallet.gateway.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import net.wzero.wewallet.domain.EntityBase;
import net.wzero.wewallet.domain.converter.IntegerListJsonConverter;

@Entity
@Table(name="user_resources")
public class UserResource extends EntityBase implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5411462420111153894L;
//	@Id
//	@GeneratedValue(strategy= GenerationType.AUTO)
	@Id
	@GeneratedValue
	private Integer id;
	@JsonIgnore
	@OneToOne
	@JoinColumn(name="member_id")
	private Member member;
	@Column(columnDefinition="TEXT")
	@Convert(converter = IntegerListJsonConverter.class)
	private List<Integer> apis;
	@Column
	@Convert(converter = IntegerListJsonConverter.class)
	private List<Integer> clients;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public List<Integer> getApis() {
		return apis;
	}
	public void setApis(List<Integer> apis) {
		this.apis = apis;
	}
	public List<Integer> getClients() {
		return clients;
	}
	public void setClients(List<Integer> clients) {
		this.clients = clients;
	}
	public Member getMember() {
		return member;
	}
	public void setMember(Member member) {
		this.member = member;
	}
	
}
