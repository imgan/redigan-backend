package com.ecommerceapi.ecomerceapi.filters;

import com.ecommerceapi.ecomerceapi.model.Admin;
import com.ecommerceapi.ecomerceapi.model.Merchant;
import com.ecommerceapi.ecomerceapi.repositories.AdminRepository;
import com.ecommerceapi.ecomerceapi.repositories.MerchantRepository;
import com.ecommerceapi.ecomerceapi.util.ConstantUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ValidationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class AuthFilter extends GenericFilterBean {

    @Autowired
    MerchantRepository merchantRepository;

    @Autowired
    AdminRepository adminRepository;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
        httpResponse.setHeader("Access-Control-Allow-Origin", "*");
        httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
        httpResponse.setHeader("Access-Control-Allow-Methods",
                "ACL, CANCELUPLOAD, CHECKIN, CHECKOUT, COPY, DELETE, GET, HEAD, LOCK, MKCALENDAR, MKCOL, MOVE, OPTIONS, POST, PROPFIND, PROPPATCH, PUT, REPORT, SEARCH, UNCHECKOUT, UNLOCK, UPDATE, VERSION-CONTROL");
        httpResponse.setHeader("Access-Control-Max-Age", "3600");
        httpResponse.setHeader("Access-Control-Allow-Headers",
                "Origin, X-Requested-With, Content-Type, Accept, Key, Authorization");

        String authHeader = httpRequest.getHeader("Authorization");
        if(authHeader != null){
            String [] authHeaderArr = authHeader.split("Bearer ");
            if(authHeaderArr.length > 1 && authHeaderArr[1] != null){
                String token = authHeaderArr[1];
                try {
                    Claims claims = Jwts.parser().setSigningKey(ConstantUtil.API_SECRET_KEY)
                            .parseClaimsJws(token).getBody();
                    if (claims.get("type").toString().compareTo("merchant") != 0) {
                        httpRequest.setAttribute("officerId", Integer.parseInt(claims.get("officerId").toString()));
                    } else {
                        httpRequest.setAttribute("merchantId", Integer.parseInt(claims.get("merchantId").toString()));
                    }
                } catch (Exception e){
                    httpResponse.sendError(HttpStatus.FORBIDDEN.value(), "invalid / expired token");
                    return;
                }
            } else {
                httpResponse.sendError(HttpStatus.FORBIDDEN.value(), "Authorization token must be Bearer [token]");
                return;
            }
        } else {
            httpResponse.sendError((HttpStatus.FORBIDDEN.value()),"Authorization token must be provided");
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    public Merchant getMerchantFromToken(String token) {
        Map data = new HashMap();
        if (token != null) {
            try {
                Claims claims = Jwts.parser().setSigningKey(ConstantUtil.API_SECRET_KEY)
                        .parseClaimsJws(token).getBody();
                Merchant merchant = merchantRepository.findOneByUsername(claims.get("username").toString());
                return merchant;
            } catch (Exception e) {
                logger.error("Error get merchant from token"  + e);
                return null;
            }
        }
        return null;
    }

    public Admin getAdminFromToken(String token) {
        Map data = new HashMap();
        if (token != null) {
            try {
                Claims claims = Jwts.parser().setSigningKey(ConstantUtil.API_SECRET_KEY)
                        .parseClaimsJws(token).getBody();
                Admin admin = adminRepository.findOneByUsername(claims.get("username").toString());
                return admin;
            } catch (Exception e) {
                logger.error("Error get admin from token"  + e);
                return null;
            }
        }
        return null;
    }

    public String getToken(HttpServletRequest httpRequest) {
        if (httpRequest != null) {
            try {
                String authHeader = httpRequest.getHeader("Authorization");
                String [] authHeaderArr = authHeader.split("Bearer ");
                String token = authHeaderArr[1];
                return token;
            } catch (Exception e) {
                logger.error("Error get from token"  + e);
                throw new ValidationException("Error get from token " + e.getMessage());
            }
        }
        return null;
    }
}
