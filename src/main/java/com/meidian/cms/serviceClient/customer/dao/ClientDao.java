package com.meidian.cms.serviceClient.customer.dao;

import com.meidian.cms.serviceClient.customer.Client;
import com.meidian.cms.serviceClient.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

/**
 * Title: com.meidian.cms.serviceClient.customer.dao<br>
 * Description: <br>
 * Copyright: Copyright (c) 2015<br>
 * Company: 北京云杉世界信息技术有限公司<br>
 *
 * @author 张中凯
 *         2017/11/19
 */
public interface ClientDao extends JpaRepository<Client,Long> , JpaSpecificationExecutor<Client>{

}
