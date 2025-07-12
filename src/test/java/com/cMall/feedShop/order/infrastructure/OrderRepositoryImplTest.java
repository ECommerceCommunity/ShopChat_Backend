package com.cMall.feedShop.order.infrastructure;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class OrderRepositoryImplTest {

    @InjectMocks
    private OrderRepositoryImpl orderRepositoryImpl;

    @Test
    void OrderRepositoryImpl_obj_create() {
        // when & then
        assertThat(orderRepositoryImpl).isNotNull();
    }
}