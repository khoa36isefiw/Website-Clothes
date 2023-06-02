package com.example.demo3.orderState.Observer;

import com.example.demo3.business.Order;

import java.util.Observable;
import java.util.Observer;

class OrderStatusObserver implements Observer {
    private Order order;

    public OrderStatusObserver(Order order) {
        this.order = order;
        order.addObserver(this);
    }




    @Override
    public void update(Observable observable, Object o) {
        // Kiểm tra xem đối tượng quan sát có phải là đơn hàng không
        if (observable instanceof Order) {
            // Lấy thông tin trạng thái của đơn hàng
            String statusMessage = ((Order) observable).getStatusMessage();
            // Hiển thị thông báo về trạng thái của đơn hàng cho người dùng
            System.out.println("Trạng thái của đơn hàng: " + statusMessage);
        }
    }
}