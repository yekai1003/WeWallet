package net.wzero.wewallet.gateway.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.wzero.wewallet.gateway.domain.ApiData;
import net.wzero.wewallet.gateway.repo.ApiDataRepository;

@RestController
@RequestMapping("/api")
public class ApiController extends BaseController  {

	@Autowired
	private ApiDataRepository apiDataRepository;
	
	@RequestMapping(value="/getAll",produces= {MediaType.APPLICATION_JSON_VALUE})
	public List<ApiData> getAll(){
		return this.apiDataRepository.findAll();
	}
	
	@RequestMapping(value="/getList",produces= {MediaType.APPLICATION_JSON_VALUE})//zuul
	public Page<ApiData> getList(
			@RequestParam(name="page", defaultValue="0") Integer page,
			@RequestParam(name="size", defaultValue="10", required=false) Integer size,
			@RequestParam(name="key", required=false) String key){
		Pageable pageable = new PageRequest(page, size, Sort.Direction.ASC, "id");
		if(key != null) return this.apiDataRepository.findByUriLike("%"+key+"%", pageable);
		return this.apiDataRepository.findAll(pageable);
	}
	
	@RequestMapping(value="/getList2")//for test
	public Page<ApiData> getList2(
			@RequestParam(name="page", defaultValue="0") Integer page,
			@RequestParam(name="size", defaultValue="10", required=false) Integer size,
			@RequestParam(name="key", required=false) String key){
		Pageable pageable = new PageRequest(page, size, Sort.Direction.ASC, "id");
		if(key != null) return this.apiDataRepository.findByUriLike("%"+key+"%", pageable);
		return this.apiDataRepository.findAll(pageable);
	}
	@RequestMapping("/getApiByUri")
	public ApiData getApiByUri(@RequestParam(name="api") String apiuri) {
		return this.apiDataRepository.findByUri(apiuri);
	}
	@RequestMapping("/getApi")
	public ApiData getApi(@RequestParam(name="id") int apiId) {
		return this.apiDataRepository.findOne(apiId);
	}
	@RequestMapping("/updateApi")
	public ApiData updateApi(@ModelAttribute ApiData api) {
		ApiData old =this.apiDataRepository.findOne(api.getId());
		if(api.getUri()!=null)
			old.setUri(api.getUri());
		if(api.getIsEnable()!=null)
			old.setIsEnable(api.getIsEnable());
		if(api.getNeedAuthorization()!=null)
			old.setNeedAuthorization(api.getNeedAuthorization());
		if(api.getIsPublic() != null)
			old.setIsPublic(api.getIsPublic());
		if(api.getDescription()!=null)
			old.setDescription(api.getDescription());
		return this.apiDataRepository.save(old);
		
	}
	@RequestMapping("/createApi")
	public ApiData createApi(@ModelAttribute ApiData api) {
		api = this.apiDataRepository.save(api);
		return api;
	}
}
