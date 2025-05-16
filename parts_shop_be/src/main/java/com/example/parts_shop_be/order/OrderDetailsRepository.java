package com.example.parts_shop_be.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface OrderDetailsRepository extends JpaRepository<OrderDetails, Long> {

}
