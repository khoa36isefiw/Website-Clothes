package com.example.demo3.controller;

import com.example.demo3.business.*;
import com.example.demo3.data.AccountDAO;
import com.example.demo3.data.OrderDAO;
import com.example.demo3.data.ProductDAO;
import com.example.demo3.data.UserDAO;
import com.example.demo3.orderState.OrderState;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@WebServlet(name = "AdminServlet",urlPatterns={"/Admin/*"})
public class AdminServlet extends HttpServlet {
    private static final long serialVersionUID = 102831973239L;
    String defaultURL = "/adminSection/adminPage.jsp";
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        String url = defaultURL;
        if (requestURI.endsWith("/Default")) {
            url = getTable(request, response);
        }else if (requestURI.endsWith("/loadProduct")) {
            url = loadProduct(request, response);
        } else if (requestURI.endsWith("/loadUser")) {
            url = loadUser(request, response);
        }else if (requestURI.endsWith("/deleteProduct")) {
            url = deleteProduct(request, response);
        } else if (requestURI.endsWith("/deleteUser")) {
            url = deleteUser(request, response);
        } else if (requestURI.endsWith("/changeState")) {
            url = changeState(request, response);
        }

        response.sendRedirect(url);
    }




    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        String url = defaultURL;
        if (requestURI.endsWith("/operateProduct")) {
            url = operateProduct(request, response);
        }else if (requestURI.endsWith("/operateUser")) {
            url = addUser(request, response);
        }
        response.sendRedirect(url);
    }
    private Iterator<Serializable> getIterator(int index)
    {
        ProductDAO pd = new ProductDAO();
        OrderDAO od = new OrderDAO();
        UserDAO ud = new UserDAO();
        switch (index){
            case (1):
                return ud.iterator();
            case (2):
                return od.iterator();
            case(3):
                return pd.iterator();
            default:
                return null;
        }
    }
    private String getTable(HttpServletRequest request,
                                HttpServletResponse response) {
        HttpSession session = request.getSession();

        int index = Integer.parseInt(request.getParameter("iIndex"));
        Iterator<Serializable> Iterator = getIterator(index);
        List<Serializable> lProPage = new ArrayList<>() ;

        int startIndex = 0;
        int endIndex = 20;
        int currentIndex = 0;
        Serializable data;
        while (Iterator.hasNext()) {
            switch (index){
                case (1):
                     data = (User)Iterator.next();
                    break;
                case (2):
                    data = (Order)Iterator.next();

                    break;
                case(3):
                    data = (Product)Iterator.next();
                    break;
                default:
                    return null;
            }
            if (currentIndex >= startIndex && currentIndex < endIndex) {
                lProPage.add(data);

            }
            currentIndex++;

        }
        session.setAttribute("indexSection",index);
        session.setAttribute("lPage",lProPage);
        session.setAttribute("total",OrderDAO.totalIncome());

        return defaultURL;
    }

    private String loadProduct(HttpServletRequest request,
                               HttpServletResponse response)
    {
        int proNo = Integer.parseInt(request.getParameter("proNo"));
        Product product = ProductDAO.selectProduct(proNo);
        /*Size size = product.getProSize().get(sIn);*/

        HttpSession session = request.getSession();
        session.setAttribute("iProduct",product);
        return  "/adminSection/updateProduct.jsp";
    }
    private String loadUser(HttpServletRequest request, HttpServletResponse response) {
        int userNo = Integer.parseInt(request.getParameter("userNo"));
        User user = UserDAO.selectUserByID(userNo);
        HttpSession session = request.getSession();
        session.setAttribute("iUser",user);

        return "/adminSection/updateAndAddUser.jsp";
    }

    private String deleteProduct(HttpServletRequest request, HttpServletResponse response) {
        int proNo = Integer.parseInt(request.getParameter("proID1"));
        Product product = ProductDAO.selectProduct(proNo);
        System.out.println(product.getProName());
        int i=0;
        HttpSession session = request.getSession();
        session.removeAttribute("proID1");
        product.setProStatus(0);
        ProductDAO.update(product);
        return "/Admin/Default?iIndex=3";
    }
    private String deleteUser(HttpServletRequest request, HttpServletResponse response) {
        int userNo = Integer.parseInt(request.getParameter("userNo"));
        User user = UserDAO.selectUserByID(userNo);
        System.out.println(user.getEmail());
        int i=0;
        HttpSession session = request.getSession();
        session.removeAttribute("userID");
        user.setUserStatus(0);
        UserDAO.update(user);
        return "/Admin/Default?iIndex=1";
    }
    private String operateProduct(HttpServletRequest request,
                              HttpServletResponse response)
    {
        String proName = (String)request.getParameter("name");
        String proSize = (String)request.getParameter("size");
        String proDes= (String)request.getParameter("description");
        String proCate = (String)request.getParameter("category");
        String inSec = (String) request.getParameter("inSec");


        double proPrice = Integer.parseInt(request.getParameter("price"));
        int proStock = Integer.parseInt(request.getParameter("stock"));
        System.out.println(proName);
        System.out.println(proDes);
        System.out.println(proSize);
        System.out.println(proCate);
        System.out.println(proPrice);
     /*   Size size = new Size();
        size.setSize(proSize);
        size.setStock(proStock);*/
        /*ProductDAO.insertSize(size);*/

        Product product = new Product();
        product.setProName(proName);
        product.setProDes(proDes);
        product.setProCategory(proCate);
        product.setProPrice(proPrice);


        if(inSec.trim().equals("addPro")){
            ProductDAO.insert(product);
        }else if (inSec.trim().equals("updPro")) {
            ProductDAO.update(product);
        }
        return "/Admin/Default?iIndex=1";
    }
    private String addUser(HttpServletRequest request,
                              HttpServletResponse response)
    {

        HttpSession session = request.getSession();
        String userName = (String)request.getParameter("userName");
        String userPass= (String)request.getParameter("password");
        String userEmail = (String)request.getParameter("email");
        String userRole = (String)request.getParameter("role");
        String userAdd1 = (String)request.getParameter("addStreet");
        String userAdd2 = (String)request.getParameter("City");
        String userAdd3 = (String)request.getParameter("State");


        String address = userAdd1 +" , "+userAdd2 + " , " +userAdd3;
        System.out.println(userName);
        System.out.println(userPass);
        System.out.println(userEmail);
        System.out.println(address);


        User user = UserDAO.findUserByUsername(userName);
        user.setEmail(userEmail);
        user.setUserAddress(address);
        String inSec = (String) request.getParameter("inSec");



        if(inSec.trim().equals("addUser")){
            UserDAO.insert(user);
            System.out.println(inSec);
        }else if (inSec.trim().equals("uptUser")) {
            UserDAO.update(user);
            System.out.println(inSec);
        }else {
            UserDAO.insert(user);
            System.out.println(inSec);
        }
        return "/Admin/Default?iIndex=1";
    }
    private String changeState(HttpServletRequest request, HttpServletResponse response) {
        int orderID = Integer.parseInt(request.getParameter("orID"));
        Order order = OrderDAO.selectOrderByID(orderID);
        System.out.println(order.getStatusMessage());
        order.process();
        OrderDAO.update(order);
        return "/Admin/Default?iIndex=2";
    }

}
