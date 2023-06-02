package com.example.demo3.controller;


import com.example.demo3.Iterator.filter.Filter;
import com.example.demo3.Iterator.productFilterIterator;
import com.example.demo3.business.Order;
import com.example.demo3.business.Product;
import com.example.demo3.business.User;
import com.example.demo3.data.AccountDAO;
import com.example.demo3.data.OrderDAO;
import com.example.demo3.data.ProductDAO;
import com.example.demo3.Iterator.filter.*;
import com.example.demo3.data.UserDAO;


import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@WebServlet(name = "PageServlet",urlPatterns={"/Page/*"})
public class PageServlet extends HttpServlet {
    private static final long serialVersionUID = 102831973239L;
    String defaultURL = "/Shopping.jsp";
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, IOException {
            String requestURI = request.getRequestURI();
            String url = defaultURL;
            if (requestURI.endsWith("/pageIndex")) {
                url = separatePage(request, response);
            }
            else if (requestURI.endsWith("/applyFilter")) {
                url = applyFilter(request, response);
            }
            else if (requestURI.endsWith("/checkOrder")) {
                url = getListOrder(request, response);
            }


            log("RequestURI: " + url);
        /*getServletContext()
                .getRequestDispatcher(url)
                .forward(request,response);*/
            response.sendRedirect(url);

    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        String url = defaultURL;
        if (requestURI.endsWith("/pageIndex")) {
            url = separatePage(request, response);
        }
        else if (requestURI.endsWith("/applyFilter")) {
            url = applyFilter(request, response);
        }
        else if (requestURI.endsWith("/checkOrder")) {
            url = getListOrder(request, response);
        }
        else if (requestURI.endsWith("/updateUser")) {
            url = updateProfile(request, response);
        }


        log("RequestURI: " + url);
        /*getServletContext()
                .getRequestDispatcher(url)
                .forward(request,response);*/
        response.sendRedirect(url);
    }
    private String separatePage(HttpServletRequest request,
                                  HttpServletResponse response) {
        ProductDAO pd = new ProductDAO();
        HttpSession session = request.getSession();
        Iterator<Serializable> productIterator = pd.iterator();
        List<Product> lProPage = new ArrayList<>() ;
        String indexPS = request.getParameter("indexP");
        int startIndex = 0;
        if(indexPS != null) {
            startIndex = (Integer.parseInt(indexPS)-1)*8 ;

        }else{
            startIndex = 0;
        }
        //startIndex = Integer.parseInt(request.getParameter("indexP"));
        int endIndex = startIndex+8;
        int currentIndex = 0;
        while (productIterator.hasNext()) {
            Serializable data = productIterator.next();
            if (currentIndex >= startIndex && currentIndex < endIndex) {
                lProPage.add((Product)data);
            }
            currentIndex++;
        }
        session.setAttribute("indexPage",startIndex);
        session.setAttribute("lPropage",lProPage);

        return  "Shopping.jsp";
    }
    private String getListOrder(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        User user  = (User) session.getAttribute("user");
        List<Order> orderList = OrderDAO.selectOrder();
        System.out.println(orderList.get(0).getStatusMessage());
        List<Order> newOrderList = new ArrayList<>();
        for (Order order:orderList
             ) {
            if(order.getUser().getUserID()==user.getUserID())
            {
               newOrderList.add(order);
               order.getOrderDetail().getTotalCost();
            }
        }


        session.setAttribute("orderlist",newOrderList);
        return "/OrderList.jsp";
    }
    private String updateProfile(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        User user  = (User) session.getAttribute("user");
        String fname = request.getParameter("firstname");
        String lname = request.getParameter("lastname");
        String email = request.getParameter("email");
        int phone = Integer.parseInt(request.getParameter("phone"));
        String address = request.getParameter("address");
        String oldPassword = request.getParameter("oldPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("newPassword2");

        String message = "confirm password doesn't match";

        user.setFirstName(fname);
        user.setLastName(lname);
        user.setUserPhone(phone);
        user.setUserAddress(address);
        System.out.println(newPassword);
        System.out.println(confirmPassword);
        if (newPassword.trim().equals(confirmPassword.trim())){
            user.getAccount().setPassword(newPassword);
        }
        /*else {
            session.setAttribute("message",message);
            return "/Profile.jsp";
        }*/


        UserDAO.update(user);
        session.setAttribute("user",user);
        return "/Profile.jsp";
    }

    private Filter getFilter(int index){
        switch (index){
            case (1):
                return new categoryProductFilter();
            case (2):
                return new brandProductFilter();
            case(3):
                return new Search();
            default:
                return null;
        }
    }
    private String getCategory(int index){
        switch (index){
            case (1):
                return "TOP";
            case (2):
                return "BOTTOM";
            case(3):
                return "BAG";
            case(4):
                return "SNEAKER";
            case (5):
                return "adidas";
            case (6):
                return "nike";
            default:
                return null;
        }
    }
    private String applyFilter(HttpServletRequest request,
                               HttpServletResponse response)
    {
        ProductDAO pd = new ProductDAO();
        String searchKey = null;
        HttpSession session = request.getSession();
        List<Product> lProPage = new ArrayList<>() ;
        //get parameters from session
        System.out.println(request.getParameter("category") +","+request.getParameter("cIndex")+","+request.getParameter("searchKey"));
        int indexCategory = Integer.parseInt(request.getParameter("category"));

        /*int priceFrom = Integer.parseInt(request.getParameter("priceFrom"));
        int priceTo = Integer.parseInt(request.getParameter("priceTo"));*/



        int cindex=0;
        if(request.getParameter("cIndex")!=null)
        {
            cindex = Integer.parseInt(request.getParameter("cIndex"));
        }
        else{
            cindex = 0;
        }
        searchKey = (String)request.getParameter("searchKey");

        String sIndex;
        if(searchKey!=null){
            sIndex = searchKey.trim();

        }else{
            sIndex = getCategory(cindex).trim();
        }
        //apply filter into the lProduct
        Filter filter;
        filter = getFilter(indexCategory);
        // create an iterator with the category filter

        Iterator<Product> iterator = new productFilterIterator(ProductDAO.selectProducts(),filter,sIndex);
        // iterate over the filtered products
        while (iterator.hasNext()) {
            Product product = iterator.next();
            lProPage.add(product);
            System.out.println(product.getProName());
        }
        session.setAttribute("lPropage",lProPage);



        return  "Shopping.jsp";
    }
    /*private String applyFilter(HttpServletRequest request,
                               HttpServletResponse response)
    {
        ProductDAO pd = new ProductDAO();
        String searchKey = null;
        HttpSession session = request.getSession();
        List<Product> lProPage = new ArrayList<>() ;
        //get parameters from session
        System.out.println(request.getParameter("category") +","+request.getParameter("cIndex")+","+request.getParameter("searchKey"));
        int indexCategory = Integer.parseInt(request.getParameter("category"));
        boolean[][] checkArray = (boolean[][]) session.getAttribute("filterIndex");
        *//*int priceFrom = Integer.parseInt(request.getParameter("priceFrom"));
        int priceTo = Integer.parseInt(request.getParameter("priceTo"));*//*
        boolean[][] a = null;
        if (checkArray==null){
            a = new boolean[3][10];
        }
        else {
            a = checkArray;
        }
        List<Product> lProduct = (List<Product>) session.getAttribute("lPropage");

        int cindex=0;

        if(request.getParameter("cIndex")!=null)
        {
            cindex = Integer.parseInt(request.getParameter("cIndex"));
            a[indexCategory][cindex]=true;

        }
        else{
            cindex = 0;
        }

        searchKey = (String)request.getParameter("searchKey");

        String sIndex;
        if(searchKey!=null){
            sIndex = searchKey.trim();
            //apply filter into the lProduct
            Filter filter;
            filter = getFilter(indexCategory);
            // create an iterator with the category filter

            Iterator<Product> iterator = new productFilterIterator(lProduct,filter,sIndex);
            // iterate over the filtered products
            while (iterator.hasNext()) {
                Product product = iterator.next();
                lProPage.add(product);
                System.out.println(product.getProName());
            }

        }else{
            for (int i =1 ;i<3;i++)
            {
                for (int j = 1; j < 6; j++) {
                    if(a[i][j]== true)
                    {
                        //apply filter into the lProduct
                        Filter filter;
                        filter = getFilter(i);
                        sIndex = getCategory(j).trim();
                        // create an iterator with the category filter

                        Iterator<Product> iterator = new productFilterIterator(lProduct,filter,sIndex);
                        // iterate over the filtered products
                        while (iterator.hasNext()) {
                            Product product = iterator.next();
                            lProPage.add(product);
                            System.out.println(product.getProName());
                        }
                    }
                }
            }
        }
        session.setAttribute("lPropage",lProPage);
        session.setAttribute("filterIndex",a);

        return  "Shopping.jsp";
    }*/

}


