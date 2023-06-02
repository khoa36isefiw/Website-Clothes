package com.example.demo3.controller;

import com.example.demo3.business.Cart;
import com.example.demo3.business.CartItem;
import com.example.demo3.business.Product;
import com.example.demo3.business.User;
import com.example.demo3.data.CartDAO;
import com.example.demo3.data.ProductDAO;
import com.example.demo3.data.UserDAO;
import com.example.demo3.util.CookieUtil;


import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.Timer;


@WebServlet(name = "CartServlet", urlPatterns = {"/Cart/*"})
public class CartServlet extends HttpServlet {
    private static final long serialVersionUID = 102831973239L;
    private static final String defaultURL = "/Cart1.jsp";
    Timer timer;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        String url = defaultURL;
        log("RequestURI: " + requestURI);
        if (requestURI.endsWith("/showCart")) {
            url = showCart(request, response);
        } else if (requestURI.endsWith("/addItem")) {
            url = addItem(request, response);
        } else if (requestURI.endsWith("/updateItem")) {
            url = updateItem(request, response);
        } else if (requestURI.endsWith("/removeItem")) {
            url = removeItem(request, response);
        } else if (requestURI.endsWith("/clearCart")) {
            url = clearCart(request, response);
        }

        response.sendRedirect(url);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        String url = "/";
        if (requestURI.endsWith("/showCart")) {
            url = showCart(request, response);
        } else if (requestURI.endsWith("/addItem")) {
            url = addItem(request, response);
        } else if (requestURI.endsWith("/updateItem")) {
            url = updateItem(request, response);
        } else if (requestURI.endsWith("/removeItem")) {
            url = removeItem(request, response);
        } else if (requestURI.endsWith("/clearCart")) {
            url = clearCart(request, response);
        }


        response.sendRedirect(url);
    }

    private String clearCart(HttpServletRequest request,
                             HttpServletResponse response) {
        HttpSession session = request.getSession();
        final Object lock = request.getSession().getId().intern();
        Cart cart;
        synchronized (lock) {
            cart = (Cart) session.getAttribute("cart");
            cart = CartDAO.clearCart(cart);
            request.getSession().setAttribute("cart", cart);
        }
        session.setMaxInactiveInterval(-1);
        return "/Cart1.jsp";
    }

    private String showCart(HttpServletRequest request,
                            HttpServletResponse response) {

        // lấy session về
        HttpSession session = request.getSession();
        Cart cart;

        // lấyuser từ session
        User user = (User) session.getAttribute("user");
        double totalPrice;
        final Object lock = request.getSession().getId().intern();

        // chỉ 1 có card
        synchronized (lock) {
            //cart = (Cart) session.getAttribute("cart-list");
            /*if (cart == null) {*/


            // check user login vao chua
            if (user != null) {
                User user1 = UserDAO.selectUserByID(user.getUserID());
                System.out.println(user.getUserID());
                // Retrieve the Cart of that id if they already logged in
                if (UserDAO.getCartByUser(user1) != null) {
                    session.removeAttribute("cart-list");
                    //System.out.println(CartDAO.getCartByUser(user1).get(0).getListCart().get(0).getProduct().getProName());
                    cart = UserDAO.getCartByUser(user1);


                    int n = cart.getListCart().size();
                    request.getSession().setAttribute("data", cart.getListCart());
                    request.getSession().setAttribute("cart-list", cart);
                    request.getSession().setAttribute("size", cart.getCount());

                }
                //Create a Cart if they haven't had a cart yet
                else {
                    cart = new Cart();
                    cart.setUser(user);
                    CartDAO.insert(cart);
                    session.removeAttribute("cart-list");
                    int n = cart.getListCart().size();
                    request.getSession().setAttribute("size", n);
                    request.getSession().setAttribute("data", cart.getListCart());
                    request.getSession().setAttribute("cart-list", cart);
                }
            }

            // chua dang nhap vao
            else {
                session.removeAttribute("cart-list");
                // hiển thị giỏ hàng dựa trên cookie
                showCartByCookie(request, response);
            }
            request.setAttribute("cartInfo", "Your cart is empty");
        }
        //session sẽ không bao giờ hết hạn và sẽ được giữ lại cho đến
        // khi người dùng đóng trình duyệt hoặc session được hủy bởi ứng dụng
        session.setMaxInactiveInterval(-1);
        return defaultURL;
    }

    //Danh cho nguoi dung khong dang nhap vao he thong
    private void showCartByCookie(HttpServletRequest request,
                                  HttpServletResponse response) {

        Cookie[] arr = request.getCookies();
        List<Product> allPro = ProductDAO.selectProducts();
        String cartCookie = CookieUtil.getCookieValue(arr, "cart");
        Cart cart;
        String totalPrice;
        cart = new Cart(cartCookie, allPro);
        List<CartItem> itemList = cart.getListCart();
        int n;
        if (itemList != null) {
            n = itemList.size();
            request.getSession().setAttribute("size", n);
            request.getSession().setAttribute("data", itemList);
            request.getSession().setAttribute("cart-list", cart);
        } else {
            n = 0;
        }

    }

    //---------------------------------------- CRUD Cart -----------------------------------------------------------
    private String addItem(HttpServletRequest request,
                           HttpServletResponse response) {
        HttpSession session = request.getSession();
        Cookie[] cookies = request.getCookies();
        String txt = "";
        final Object lock = request.getSession().getId().intern();
        //Cart cart = (Cart) session.getAttribute("cart");
        User user = (User) session.getAttribute("user");
        Cart cart;
        double totalPrice = 0;
        synchronized (lock) {
            cart = (Cart) session.getAttribute("cart-list");
        }
        if (cart == null) {
            if (user != null) {
                // Retrieve the Cart of that id if they already logged in
                if (UserDAO.getCartByUser(user) != null) {
                    cart = UserDAO.getCartByUser(user);
                }
                //Create a Cart if they haven't had a cart yet
                else {
                    cart = new Cart();
                    cart.setUser(user);
                    CartDAO.insert(cart);
                }
            }
        }
        int productCode = Integer.parseInt(request.getParameter("productCode"));
        int quantity = 1;
        if (request.getParameter("quantity") != null) {
            quantity = Integer.parseInt(request.getParameter("quantity"));
        }
        String size = "L";
        if (request.getParameter("size") != null) {
            size = request.getParameter("size");
        }

        Product product = ProductDAO.selectProduct(productCode);
        int check = 0;
        if (product != null) {
            CartItem cartItem = new CartItem();
            for (CartItem in : cart.getListCart()
            ) {
                if (in.getProduct().getId() == product.getId()) {
                    if (quantity != 1) {
                        in.setQuantity(in.getQuantity() + quantity);
                        check = 1;
                    } else {
                        in.setQuantity(in.getQuantity() + 1);
                        check = 1;
                    }
                }
            }
            if (check == 0) {
                cartItem.setProduct(product);
                cartItem.setSize(size);
                cart.setListCart(CartDAO.addItem(cart.getListCart(), cartItem));
            } else {

            }

            totalPrice = CartDAO.getTotalCurrencyFormat(cart.getListCart());
            //Lấy dữ liệu cart cookie hiện tại và Xoá cart hiện tại trong cookie để thay bằng cái mới

        }
        //xử lý đồng bộ hóa (thread-safe)
        synchronized (lock) {
            session.setAttribute("cart-list", cart);
            session.setAttribute("total", totalPrice);
        }
        if (user != null) {
            CartDAO.update(cart);
        } else {
            updateCookie(request, response, cart);
        }

        session.setMaxInactiveInterval(-1);
        return "/Shopping.jsp";
    }

    private String updateItem(HttpServletRequest request,
                              HttpServletResponse response) {
        final Object lock = request.getSession().getId().intern();
        String quantityString = request.getParameter("quantity");
        int productCode = Integer.parseInt(request.getParameter("productCode"));
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        Cart cart;
        double totalPrice;
        synchronized (lock) {
            cart = (Cart) session.getAttribute("cart-list");
            int quantity;
            try {
                quantity = Integer.parseInt(quantityString);
                if (quantity < 0) {
                    quantity = 1;
                }
            } catch (NumberFormatException ex) {
                quantity = 1;
            }
            Product product = ProductDAO.selectProduct(productCode);
            if (product != null && cart != null) {
                CartItem cartItem = new CartItem();
                cartItem.setProduct(product);
                cartItem.setQuantity(quantity);
                if (quantity > 0) {
                    cart.setListCart(CartDAO.addItem(cart.getListCart(), cartItem));
                    totalPrice = CartDAO.getTotalCurrencyFormat(cart.getListCart());
                    session.setAttribute("total", totalPrice);
                    log("total update: " + totalPrice);
                } else {
                    cart.setListCart(CartDAO.removeItem(cart.getListCart(), cartItem));
                }
            }

        }
        if (user != null) {
            CartDAO.update(cart);
        } else {
            updateCookie(request, response, cart);
        }
        session.setAttribute("cart-list", cart);
        session.setAttribute("size", cart.getCount());
        session.setMaxInactiveInterval(-1);
        return defaultURL;
    }

    private String removeItem(HttpServletRequest request,
                              HttpServletResponse response) {
        final Object lock = request.getSession().getId().intern();
        HttpSession session = request.getSession();
        //Cart cart = (Cart) session.getAttribute("cart");
        Cart cart;
        double totalPrice;
        synchronized (lock) {
            cart = (Cart) session.getAttribute("cart-list");
        }
        int productCode = Integer.parseInt(request.getParameter("productCode"));
        Product product = ProductDAO.selectProduct(productCode);
        if (product != null && cart != null) {
            CartItem lineItem = new CartItem();
            lineItem.setProduct(product);
            cart.setListCart(CartDAO.removeItem(cart.getListCart(), lineItem));
            totalPrice = CartDAO.getTotalCurrencyFormat(cart.getListCart());
            request.getSession().setAttribute("total", totalPrice);
            User user = (User) session.getAttribute("user");
            if (user != null) {
                CartDAO.update(cart);
            } else {
                updateCookie(request, response, cart);
            }
            session.setAttribute("cart-list", cart);
            session.setAttribute("total", totalPrice);
            session.setAttribute("size", cart.getCount());
        }
        session.setMaxInactiveInterval(-1);
        return "/Cart1.jsp";
    }

    private void updateCookie(HttpServletRequest request, HttpServletResponse response, Cart cart) {


        Cookie[] cookies = request.getCookies();
        String txt = "";
        if (cookies != null) {
            for (Cookie index : cookies) {
                if (index.getName().equals("cart")) {
                    txt += index.getValue();
                    index.setMaxAge(0);
                    response.addCookie(index);
                }
            }
        }
        List<CartItem> items = cart.getListCart();
        if (items.size() > 0) {
            txt = items.get(0).getProduct().getId().toString() + ":" +
                    items.get(0).getQuantity().toString();
            for (int i = 1; i < items.size(); i++) {
                txt += "," + items.get(i).getProduct().getId().toString() + ":" +
                        items.get(i).getQuantity().toString();
            }
        } else {
            txt = "";
        }
        Cookie cookie = new Cookie("cart", txt);
        cookie.setMaxAge(2 * 24 * 60 * 60);
        response.addCookie(cookie);
    }
}
