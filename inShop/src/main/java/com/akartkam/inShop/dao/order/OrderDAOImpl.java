package com.akartkam.inShop.dao.order;

import org.springframework.stereotype.Repository;

import com.akartkam.inShop.dao.AbstractGenericDAO;
import com.akartkam.inShop.domain.order.Order;

@Repository
public class OrderDAOImpl extends AbstractGenericDAO<Order> implements OrderDAO {

}
