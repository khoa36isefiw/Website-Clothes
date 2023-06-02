package com.example.demo3.controller;

import com.example.demo3.business.*;
import com.example.demo3.data.CartDAO;
import com.example.demo3.data.OrderDAO;
import com.example.demo3.data.ProductDAO;
import com.example.demo3.data.UserDAO;
import com.example.demo3.util.CookieUtil;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import javax.servlet.http.HttpSession;

@WebServlet(name = "OrderServlet", urlPatterns = {"/Order/*"})
public class OrderServlet extends HttpServlet {
    private final String defaultURL = "/Checkout.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        String url = defaultURL;
        if (requestURI.endsWith("/inCheck")) {
            url = getShippingInfo(request, response);

        }
        else if (requestURI.endsWith("/sendMail")) {
            url = sendMail(request, response);
            System.out.println("vo r nha má");
        }
        response.sendRedirect(url);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        String url = defaultURL;
        if (requestURI.endsWith("/getOrder")) {
            url = order(request, response);
            System.out.println("vo r nha má");
        }
        response.sendRedirect(url);
    }

    private void addCookie(HttpServletRequest request, HttpServletResponse response) {
        Cookie firstnameCookie = new Cookie("firstname", request.getParameter("firstName"));
        Cookie lastnameCookie = new Cookie("lastname", request.getParameter("lastName"));
        Cookie addressCookie = new Cookie("address", request.getParameter("address"));
        Cookie phonenumCookie = new Cookie("phonenum", request.getParameter("PhoneNumber"));
        firstnameCookie.setMaxAge(60 * 24 * 365 * 2 * 60);
        firstnameCookie.setPath("/");
        lastnameCookie.setMaxAge(60 * 24 * 365 * 2 * 60);
        lastnameCookie.setPath("/");
        addressCookie.setMaxAge(60 * 24 * 365 * 2 * 60);
        addressCookie.setPath("/");
        phonenumCookie.setMaxAge(60 * 24 * 365 * 2 * 60);
        phonenumCookie.setPath("/");
        response.addCookie(firstnameCookie);
        response.addCookie(lastnameCookie);
        response.addCookie(addressCookie);
        response.addCookie(phonenumCookie);
    }

    private String getShippingInfo(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String url = "/Checkout.jsp";
        String txt = "";
        List<Product> allPro = ProductDAO.selectProducts();
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user != null) { //nếu user đã sign in
            Cookie[] cookies = request.getCookies();
            txt = CookieUtil.getCookieValue(cookies, "cart");
            Cart cart = new Cart(txt, allPro);
            session.setAttribute("cart", cart);

        } else {  // chưa sign in thì kiểm cookie
            url = "Login1.jsp";
        }
        return url;
    }

    private String order(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String url = "/";
        String txt = "";
        LocalDate curDate = LocalDate.now();
        Date date = java.sql.Date.valueOf(curDate);
        List<Product> allPro = ProductDAO.selectProducts();
        HttpSession session = request.getSession();

        //Get shipping Info
        String firstName = (String) request.getAttribute("checkout-firstname");
        String lastName = (String) request.getAttribute("checkout-lastname");
        String email = (String) request.getAttribute("checkout-email");
        String address = (String) request.getAttribute("checkout-address");
        String phone = (String) request.getAttribute("checkout-phone");

        User user = (User) session.getAttribute("user");
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);


        ShippingInfo userShipIn = new ShippingInfo();
        userShipIn.setAddress(address);
        userShipIn.setShippingCost(0.0);
        userShipIn.setShippingType("fast");
        userShipIn.setShippingRegionId(2);


        if (user != null) { //nếu user đã sign in
            Cart cart = (Cart) session.getAttribute("cart-list");
            log("cart id : " + cart.getListCart().get(0).getProduct().getProName());
            log("đưuọc r nha má 1");
            Order order = new Order();

            order.setOrderDetail(cart);
            order.setShipInfo(userShipIn);
            order.setDateCreated(date);
            order.setUser(user);

            OrderDAO.insert(order);


            log("đưuọc r nha má 1");
            url = sendMail(request,response);
            session.removeAttribute("cart-list");
            url ="/Thanks.html";
        } else {  // chưa sign in thì kiểm cookie
            url = "Login1.jsp";
        }
        return url;
    }
    private String sendMail(HttpServletRequest request,
                                  HttpServletResponse response) throws ServletException, IOException {
        String url = null;
        HttpSession mySession = request.getSession();
        User user = (User)mySession.getAttribute("user");
        Cart cart = (Cart)mySession.getAttribute("cart-list");
        String email = request.getParameter("checkout-email");

        System.out.println(email + user.getEmail());
        RequestDispatcher dispatcher = null;
        int otpvalue = 0;

        //String link = "<html><body><p>Click the button below to visit our website:</p><p><a href=\"http://localhost:8080/newPassword.jsp?email="+email+"\"><button style=\"background-color:#008CBA; border-radius:4px; color:#ffffff; padding:12px 18px; text-align:center; text-decoration:none; display:inline-block;\">Visit website</button></a></p></body></html>";


        if(email!=null || !email.equals("")) {
            // sending otp
            Random rand = new Random();
            //otpvalue = rand.nextInt(1255650);
            // Assuming you are sending email from through gmails smtp
            String host = "smtp.gmail.com";

            String to = email;// change accordingly
            // Get the session object
            Properties properties = System.getProperties();
            properties.put("mail.smtp.host", host);
            properties.put("mail.smtp.port", "465");
            properties.put("mail.smtp.ssl.enable", "true");
            properties.put("mail.smtp.auth", "true");
            Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("phanthanhluan553@gmail.com", "djcmbbcsyrarewwo");
                }
            });
            // compose message

            try {
                MimeMessage message = new MimeMessage(session);
                message.setFrom(new InternetAddress("phanthanhluan553@gmail.com"));// change accordingly
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
                /*message.setContent(link,"text/html");*/
                message.setSubject("CONFIRM YOUR ORDER.");

                // Now set the actual message
                String text = "Shipping Information:\n"
                        + "Thanks " + user.getFirstName() + " " + user.getLastName() + " for visiting and purchase our products.\n"
                        + "We would love to confirm your Email: " + user.getEmail() + "\n"
                        + "Your Address is: " + user.getUserAddress() + "\n"
                        + "Your Phone Number: " + user.getUserPhone() + "\n"
                        + "----------------------------------------------\n"
                        + "Your order: \n" + "Cart: " + cart.getCount() + "\n";
                String finaltext = text;
                List<CartItem> items = cart.getListCart();
                for (int i = 0; i < cart.getCount(); i++) {
                    finaltext += items.get(i).getProduct().getProName().toString() + " * " + items.get(i).getQuantity() + "                " + items.get(i).getSubtotal() + "\n";
                }
                finaltext += "==========================================\n"
                        + "Total:                                " + cart.getTotalCost() + "\n";

                message.setText(text);

                // send message
                Transport.send(message);
                System.out.println("message sent successfully"+email);
            }

            catch (MessagingException e) {
                e.printStackTrace();
            }
            url ="Thanks.html";


            //request.setAttribute("status", "success");
        }

        return "Thanks.html";
    }
}
