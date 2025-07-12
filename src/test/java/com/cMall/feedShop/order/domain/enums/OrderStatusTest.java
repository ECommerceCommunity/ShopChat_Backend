package com.cMall.feedShop.order.domain.enums;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class OrderStatusTest {

    @Test
    void OrderStatus_values() {
        // when & then
        assertThat(OrderStatus.values()).hasSize(5);
        assertThat(OrderStatus.ORDERED).isNotNull();
        assertThat(OrderStatus.SHIPPED).isNotNull();
        assertThat(OrderStatus.DELIVERED).isNotNull();
        assertThat(OrderStatus.CANCELLED).isNotNull();
        assertThat(OrderStatus.RETURNED).isNotNull();
    }

    @Test
    void OrderStatus_name() {
        // when & then
        assertThat(OrderStatus.ORDERED.name()).isEqualTo("ORDERED");
        assertThat(OrderStatus.CANCELLED.name()).isEqualTo("CANCELLED");
    }
}