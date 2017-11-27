package com.meidian.cms.controller.customer;

import com.meidian.cms.common.Result;
import com.meidian.cms.common.ServiceResult;
import com.meidian.cms.common.exception.BusinessException;
import com.meidian.cms.common.utils.CollectionUtil;
import com.meidian.cms.common.utils.ResultUtils;
import com.meidian.cms.common.utils.TimeUtil;
import com.meidian.cms.controller.basic.BasicController;
import com.meidian.cms.serviceClient.company.Company;
import com.meidian.cms.serviceClient.customer.Client;
import com.meidian.cms.serviceClient.customer.service.ClientService;
import com.meidian.cms.serviceClient.user.User;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/customer")
public class CustomerController extends BasicController{
    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    private ClientService clientService;


    @RequestMapping("/index")
    public ModelAndView index(HttpServletRequest request) throws BusinessException {
        ModelAndView mv = new ModelAndView();
        mv.addObject("companyList", getCompanyByUser(getUser(request)));
        /*mv.addObject("companys", getCompanyByUser(getUser(request)));*/
        mv.setViewName("customer/index");
        return mv;
    }

    @ResponseBody
    @RequestMapping("/getData")
    public Result<List<Client>> getData(HttpServletRequest request,Integer page,Integer limit,Client client) throws BusinessException {
        User user = getUser(request);
        PageRequest pageRequest = new PageRequest(page-1,limit);
        List<Long> companyIds = new ArrayList<>();
        this.getCompanyIds(user,client,companyIds);
        Page<Client> clientResult = clientService.getPageByClient(pageRequest,client,companyIds);
        return ResultUtils.returnTrue(clientResult);
    }

    /**
     * 构造查询参数，如果没有公司，查询登录人全部公司
     * @param client
     * @param companyIds
     */
    private void getCompanyIds(User user,Client client, List<Long> companyIds) throws BusinessException {
        if (null == client.getCompanyId()){
            List<Company> companyList = getCompanyByUser(user);
            if (!CollectionUtil.isEmpty(companyList)){
                companyIds.addAll(companyList.stream().map(Company::getId).collect(Collectors.toList()));
            }
        }else{
            companyIds.add(client.getCompanyId());
        }
    }

    @ResponseBody
    @RequestMapping("/add")
    public Result<List<Client>> add(HttpServletRequest request,Client client) throws BusinessException {
        User user = getUser(request);
        List<Long> companyIds = new ArrayList<>();
        this.makeData(user,client);
        ServiceResult<Boolean> serviceResult = clientService.addClient(client);
        if (!serviceResult.getSuccess()){
            return ResultUtils.returnFalse(serviceResult.getErrorCode(),serviceResult.getMessage());
        }
        return ResultUtils.returnTrue(serviceResult.getMessage());
    }

    private void makeData(User user, Client client) throws BusinessException {
        List<Company> companyList = getCompanyByUser(user);
        Map<Long,String> companyMap = companyList.stream().collect(Collectors.toMap(Company::getId,Company::getCompanyName));

        client.setcUName(user.getName());
        client.setcU(user.getId());
        client.setuUName(user.getName());
        client.setuU(user.getId());
        client.setuT(TimeUtil.getNowTimeStamp());
        client.setcT(TimeUtil.getNowTimeStamp());
        client.setCompanyName(companyMap.get(client.getCompanyId()));
    }
}
