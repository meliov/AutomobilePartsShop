package com.example.parts_shop_be.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
interface OrderDetailsRepository extends JpaRepository<OrderDetails, Long> {
    @Query("SELECT MAX(o.trackingNumber) FROM OrderDetails o")
    Long findMaxTrackingNumber();
}
