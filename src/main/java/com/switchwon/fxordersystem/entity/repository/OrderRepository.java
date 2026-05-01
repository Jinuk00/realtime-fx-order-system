package com.switchwon.fxordersystem.entity.repository;

import com.switchwon.fxordersystem.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Orders, Long> {
    List<Orders> findAllByOrderByIdAsc();
}
