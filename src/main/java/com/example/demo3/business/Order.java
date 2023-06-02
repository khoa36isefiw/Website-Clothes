package com.example.demo3.business;

import com.example.demo3.orderState.OrderPlaced;
import com.example.demo3.orderState.OrderState;
import com.example.demo3.orderState.OrderStateConverter;

import javax.persistence.*;

import java.io.Serializable;
import java.util.Date;
import java.util.Observable;



@Entity
@Table(name = "orders")
public class Order extends Observable implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id", nullable = false)
    private Integer id;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL) //đồng bộ dữ liệu
    //@JoinColumn(name = "shipping_id", referencedColumnName = "id")
    private ShippingInfo shipInfo;
    @OneToOne//đồng bộ dữ liệu
    //@JoinColumn(name = "cartid", referencedColumnName = "cartid")
    private Cart orderDetail;
    @ManyToOne
    private User user;
    private Date dateCreated;



    private Date dateShipped;

    @Column(name = "status")
    @Convert(converter = OrderStateConverter.class)
    private OrderState state;

    public Cart getOrderDetail() {
        return orderDetail;
    }

    public void setOrderDetail(Cart orderDetail) {
        this.orderDetail = orderDetail;
    }

    public Order(ShippingInfo shipInfo, Cart orderDetail, User user, Date dateCreated, Date dateShipped) {
        this.shipInfo = shipInfo;
        this.orderDetail = orderDetail;
        this.user = user;
        this.dateCreated = dateCreated;
        this.dateShipped = dateShipped;
        state = new OrderPlaced(this);
    }

    public Order(int id, Date dateCreated, Date dateShipped, User user, ShippingInfo shipInfo) {
        this.id = id;
        this.dateCreated = dateCreated;
        this.dateShipped = dateShipped;
        this.user = user;
        this.shipInfo = shipInfo;
        state = new OrderPlaced(this);
    }

    public Order() {
       this.state = new OrderPlaced(this);
    }

    //state
    public void process() {
        state.processOrder(this);
        setChanged();
        notifyObservers();
    }

    public void setState(OrderState state) {
        this.state = state;
    }

    public String getStatusMessage() {
        return state.getStatusMessage();
    }



    //End state
    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateShipped() {
        return dateShipped;
    }

    public void setDateShipped(Date dateShipped) {
        this.dateShipped = dateShipped;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

   public ShippingInfo getShipInfo() {
        return shipInfo;
    }
    public void setShipInfo(ShippingInfo shipInfo) {
        this.shipInfo = shipInfo;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Order(Integer id, ShippingInfo shipInfo, Cart orderDetail, User user, Date dateCreated, Date dateShipped, String status) {
        this.id = id;
        this.shipInfo = shipInfo;
        this.orderDetail = orderDetail;
        this.user = user;
        this.dateCreated = dateCreated;
        this.dateShipped = dateShipped;
        state = new OrderPlaced(this);
    }

}